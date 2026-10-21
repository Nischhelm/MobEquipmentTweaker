package mobequipmenttweaker.core;

import meldexun.asmutil2.AbstractClassTransformer;
import meldexun.asmutil2.NonLoadingClassWriter;
import meldexun.asmutil2.reader.ClassUtil;
import meldexun.betterconfig.asm.BetterConfigClassTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;

/**
 * Injects into every onInitialSpawn / setEquipmentBasedOnDifficulty / setEnchantmentBasedOnDifficulty in EntityLiving and all its subclasses.
 * At HEAD:
 * CallbackInfo ci = new CallbackInfo("mobequipmenttweaker_asm", true); --- or CallbackInfoReturnable
 * MobEquipHandler.hookMethodHead(this, params..., ci);
 * if(ci.isCancelled()) return; --- or return ci.getReturnValue();
 * Before every return of the original code:
 * MobEquipHandler.hookMethodTail(this, params...);
 */
public class MobEquipmentClassTransformer extends AbstractClassTransformer implements IClassTransformer {

    @SuppressWarnings("deprecation")
    private static final ClassUtil REMAPPING_CLASS_UTIL = ReflectionHelper.getPrivateValue(BetterConfigClassTransformer.class, null, "REMAPPING_CLASS_UTIL");

    private static final String HOOK_CLASS = "mobequipmenttweaker/handlers/MobEquipHandler";
    private static final String CALLBACK_INFO = "org/spongepowered/asm/mixin/injection/callback/CallbackInfo";
    private static final String CALLBACK_INFO_RETURNABLE = "org/spongepowered/asm/mixin/injection/callback/CallbackInfoReturnable";
    private static final String EQUIP_DESC = "(Lnet/minecraft/world/DifficultyInstance;)V";
    private static final String SPAWN_DESC = "(Lnet/minecraft/world/DifficultyInstance;Lnet/minecraft/entity/IEntityLivingData;)Lnet/minecraft/entity/IEntityLivingData;";

    @Override
    protected byte[] transformOrNull(String obfName, String name, byte[] basicClass) {
        if (basicClass == null) return null;

        // Cheap check first: does the class declare one of the target methods at all
        if (!declaresTargetMethod(basicClass)) return null;

        // check if class is or extends EntityLiving
        try {
            if (REMAPPING_CLASS_UTIL.findInClassHierarchy(name.replace('.', '/'), "net/minecraft/entity/EntityLiving"::equals) == null)
                return null;
        } catch (MissingResourceException e) {
            return null; // Exception happens for Optional.Interface for example
        }

        ClassNode classNode = new ClassNode();
        new ClassReader(basicClass).accept(classNode, 0);

        for (MethodNode method : classNode.methods) {
            String hookMethod = getHookMethod(method.name, method.desc, method.access);
            if (hookMethod == null) continue;
            injectAtTail(method, hookMethod + "Tail"); // first, so the returns of the HEAD injection don't get a TAIL call
            injectAtHeadAndMaybeCancel(method, hookMethod + "Head");
        }

        // Write the transformed class
        ClassWriter classWriter = new NonLoadingClassWriter(ClassWriter.COMPUTE_FRAMES, REMAPPING_CLASS_UTIL);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }

    private static String getHookMethod(String name, String desc, int access) {
        if ((access & (Opcodes.ACC_ABSTRACT | Opcodes.ACC_STATIC)) != 0) return null;
        // Bridges (covariant onInitialSpawn overrides) only forward to the real method, hooking them just adds one more depth level
        if ((access & Opcodes.ACC_SYNTHETIC) != 0 && (access & Opcodes.ACC_BRIDGE) == 0) return null;
        switch (name) {
            case "setEquipmentBasedOnDifficulty": case "func_180481_a": if (desc.equals(EQUIP_DESC)) return "setEquipmentBasedOnDifficulty"; break;
            case "setEnchantmentBasedOnDifficulty": case "func_180483_b": if (desc.equals(EQUIP_DESC)) return "setEnchantmentBasedOnDifficulty"; break;
            case "onInitialSpawn": case "func_180482_a": if (desc.equals(SPAWN_DESC)) return "onInitialSpawn"; break;
        }
        return null;
    }

    private static boolean declaresTargetMethod(byte[] basicClass) {
        boolean[] found = {false};
        new ClassReader(basicClass).accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                if (getHookMethod(name, desc, access) != null) found[0] = true;
                return null;
            }
        }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        return found[0];
    }

    /**
     * Inserts MobEquipHandler.hookMethod(this, params...); before every return.
     * The return value (if any) stays on the stack below the hook call.
     */
    private static void injectAtTail(MethodNode method, String hookMethod) {
        Type[] argTypes = Type.getArgumentTypes(method.desc);
        String targetParams = method.desc.substring(1, method.desc.indexOf(')'));
        String hookDesc = "(Lnet/minecraft/entity/EntityLiving;" + targetParams + ")V";

        List<AbstractInsnNode> returnNodes = new ArrayList<>();
        for (AbstractInsnNode insn : method.instructions.toArray())
            if (insn.getOpcode() >= Opcodes.IRETURN && insn.getOpcode() <= Opcodes.RETURN) returnNodes.add(insn);

        for (AbstractInsnNode returnNode : returnNodes) {
            InsnList injectedInstructions = new InsnList();
            for (int slot = 0; slot <= argTypes.length; slot++) // this and params, all of them are objects
                injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, slot));
            injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK_CLASS, hookMethod, hookDesc, false));
            method.instructions.insertBefore(returnNode, injectedInstructions);
        }
    }

    private static void injectAtHeadAndMaybeCancel(MethodNode method, String hookMethod) {
        Type returnType = Type.getReturnType(method.desc);
        boolean isVoid = returnType.equals(Type.VOID_TYPE);
        String callbackInfoClass = isVoid ? CALLBACK_INFO : CALLBACK_INFO_RETURNABLE;
        String targetParams = method.desc.substring(1, method.desc.indexOf(')'));
        String hookDesc = "(Lnet/minecraft/entity/EntityLiving;" + targetParams + "L" + callbackInfoClass + ";)V";

        Type[] argTypes = Type.getArgumentTypes(method.desc);
        int ciLVTSlot = argTypes.length + 1; // after this and params, all of them are objects

        InsnList injectedInstructions = new InsnList();

        // CallbackInfo ci = new CallbackInfo("mobequipmenttweaker_asm", true); --- or CallbackInfoReturnable
        injectedInstructions.add(new TypeInsnNode(Opcodes.NEW, callbackInfoClass));
        injectedInstructions.add(new InsnNode(Opcodes.DUP));
        injectedInstructions.add(new LdcInsnNode("mobequipmenttweaker_asm"));
        injectedInstructions.add(new InsnNode(Opcodes.ICONST_1));
        injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, callbackInfoClass, "<init>", "(Ljava/lang/String;Z)V", false));
        injectedInstructions.add(new VarInsnNode(Opcodes.ASTORE, ciLVTSlot));

        // MobEquipHandler.hookMethod(this, params..., ci);
        for (int slot = 0; slot <= argTypes.length; slot++)
            injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, slot));
        injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, ciLVTSlot));
        injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, HOOK_CLASS, hookMethod, hookDesc, false));

        // if(ci.isCancelled() == 0) [jump to labelToOriginal]
        injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, ciLVTSlot));
        injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, callbackInfoClass, "isCancelled", "()Z", false));
        LabelNode labelToOriginal = new LabelNode();
        injectedInstructions.add(new JumpInsnNode(Opcodes.IFEQ, labelToOriginal));

        if (isVoid) {
            // return;
            injectedInstructions.add(new InsnNode(Opcodes.RETURN));
        } else {
            // return (ReturnType) ci.getReturnValue();
            injectedInstructions.add(new VarInsnNode(Opcodes.ALOAD, ciLVTSlot));
            injectedInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, CALLBACK_INFO_RETURNABLE, "getReturnValue", "()Ljava/lang/Object;", false));
            injectedInstructions.add(new TypeInsnNode(Opcodes.CHECKCAST, returnType.getInternalName()));
            injectedInstructions.add(new InsnNode(Opcodes.ARETURN));
        }

        // original code
        injectedInstructions.add(labelToOriginal);

        method.instructions.insert(injectedInstructions);
        method.maxLocals = Math.max(method.maxLocals, ciLVTSlot + 1);
    }
}
