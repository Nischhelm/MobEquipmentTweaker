package mobequipmenttweaker.mixin.vanilla.mobequipment;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mobequipmenttweaker.config.ConfigProvider;
import mobequipmenttweaker.config.data.SetEntry;
import mobequipmenttweaker.util.MobEquipAlgorithm;
import mobequipmenttweaker.util.MobEquipState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin extends EntityLivingBase {

    public EntityLivingMixin(World worldIn) {
        super(worldIn);
    }

    @Unique private static MobEquipState mobequipmenttweaker$state = MobEquipState.OTHER;
    @Unique private static SetEntry mobequipmenttweaker$chosenSet = null;
    @Unique private static EntityLiving mobequipmenttweaker$currentEntity = null;

    @Inject(
            method = "setEquipmentBasedOnDifficulty",
            at = @At("HEAD")
    )
    private void mobequipmenttweaker_vanillaEntityLiving_setEquipmentBasedOnDifficulty_head(DifficultyInstance difficulty, CallbackInfo ci, @Share("algo") LocalRef<MobEquipAlgorithm> algo){
        algo.set(new MobEquipAlgorithm.VANILLA_ARMOR(this.rand, difficulty.getClampedAdditionalDifficulty(), this.world.getDifficulty()));
    }

    @Inject(
            method = "setEquipmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/EntityEquipmentSlot;values()[Lnet/minecraft/inventory/EntityEquipmentSlot;")
    )
    private void mobequipmenttweaker_vanillaEntityLiving_setEquipmentBasedOnDifficulty_beforeLoop(DifficultyInstance p_180481_1_, CallbackInfo ci){
        mobequipmenttweaker$state = MobEquipState.START_EQUIPPING;
        mobequipmenttweaker$currentEntity = (EntityLiving)(Object) this;
    }

    @ModifyExpressionValue(
            method = "setEquipmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/inventory/EntityEquipmentSlot;values()[Lnet/minecraft/inventory/EntityEquipmentSlot;")
    )
    private EntityEquipmentSlot[] mobequipmenttweaker_vanillaEntityLiving_setEquipmentBasedOnDifficulty_randomiseLoop(EntityEquipmentSlot[] original){
        Collections.shuffle(Arrays.asList(original), this.getRNG());
        return original;
    }

    @Inject(
            method = "setEquipmentBasedOnDifficulty",
            at = @At(value = "RETURN")
    )
    private void mobequipmenttweaker_vanillaEntityLiving_setEquipmentBasedOnDifficulty_tail(DifficultyInstance p_180481_1_, CallbackInfo ci){
        mobequipmenttweaker$chosenSet = null;
        mobequipmenttweaker$currentEntity = null;
        mobequipmenttweaker$state = MobEquipState.OTHER;
    }

    @Inject(
            method = "getArmorByChance",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    private static void mobequipmenttweaker_vanillaEntityLiving_getArmorByChance(EntityEquipmentSlot slotIn, int chance, CallbackInfoReturnable<Item> cir){
        switch (mobequipmenttweaker$state){
            case START_EQUIPPING: // called first time for one mob from EntityLiving.setEquipmentBasedOnDifficulty
                mobequipmenttweaker$chosenSet = ConfigProvider.getRandomArmor(mobequipmenttweaker$currentEntity.getRNG(), chance, true);
                cir.setReturnValue(mobequipmenttweaker$chosenSet.getItem(slotIn));
                mobequipmenttweaker$currentEntity.setDropChance(slotIn, mobequipmenttweaker$chosenSet.dropChance);
                mobequipmenttweaker$state = MobEquipState.SET_CHOSEN;
                return;
            case SET_CHOSEN: // called a second/third/fourth time for the same mob from EntityLiving.setEquipmentBasedOnDifficulty -> use same set
                cir.setReturnValue(mobequipmenttweaker$chosenSet.getItem(slotIn));
                mobequipmenttweaker$currentEntity.setDropChance(slotIn, mobequipmenttweaker$chosenSet.dropChance);
                return;
            case OTHER: // called from somewhere else -> random mix of sets that don't have dropchance 0 cause we cant actually apply it here
                cir.setReturnValue(ConfigProvider.getRandomArmor(new Random(), chance, false).getItem(slotIn));
        }
    }

    @Definition(id = "getClampedAdditionalDifficulty", method = "Lnet/minecraft/world/DifficultyInstance;getClampedAdditionalDifficulty()F")
    @Definition(id = "difficulty", local = @Local(argsOnly = true, type = DifficultyInstance.class))
    @Expression("? * difficulty.getClampedAdditionalDifficulty()")
    @ModifyExpressionValue(
            method = "setEquipmentBasedOnDifficulty",
            at = @At(value = "MIXINEXTRAS:EXPRESSION")
    )
    private float mobequipmenttweaker_vanillaEntityLiving_setEquipmentBasedOnDifficulty_base(float originalChance, @Share("algo") LocalRef<MobEquipAlgorithm> algo){
        return algo.get().chanceToEquip();
    }

    @ModifyConstant(
            method = "setEquipmentBasedOnDifficulty",
            constant = @Constant(floatValue = 0.095F)
    )
    private float mobequipmenttweaker_vanillaEntityLiving_setEquipmentBasedOnDifficulty_tierincreasechance(float constant){
        return 0; //fail all of them and instead do our own
    }

    @ModifyVariable(
            method = "setEquipmentBasedOnDifficulty",
            at = @At("STORE"),
            name = "i"
    )
    private int mobequipmenttweaker_vanillaEntityLiving_setEquipmentBasedOnDifficulty_maxtier(int constant, @Share("algo") LocalRef<MobEquipAlgorithm> algo){
        return algo.get().rollTier();
    }

    @ModifyVariable(
            method = "setEquipmentBasedOnDifficulty",
            at = @At("STORE"),
            name = "f"
    )
    private float mobequipmenttweaker_vanillaEntityLiving_setEquipmentBasedOnDifficulty_additionalArmorPieceChance(float constant, @Share("algo") LocalRef<MobEquipAlgorithm> algo) {
        return 1F - algo.get().chanceToAddPieces();
    }
}
