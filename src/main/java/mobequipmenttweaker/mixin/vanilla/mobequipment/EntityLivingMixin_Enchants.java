package mobequipmenttweaker.mixin.vanilla.mobequipment;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mobequipmenttweaker.config.ConfigHandler;
import mobequipmenttweaker.util.MobEquipAlgorithm;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin_Enchants extends EntityLivingBase {
    public EntityLivingMixin_Enchants(World worldIn) {
        super(worldIn);
    }

    @ModifyExpressionValue(
            method = "setEnchantmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/DifficultyInstance;getClampedAdditionalDifficulty()F")
    )
    private float mobequipmenttweaker_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_head(float clampedDifficulty, @Share("algoArmor") LocalRef<MobEquipAlgorithm> algoArmor, @Share("algoHand") LocalRef<MobEquipAlgorithm> algoHand){
        algoHand.set(new MobEquipAlgorithm.VANILLA_MELEE(this.rand, clampedDifficulty, this.world.getDifficulty()));
        algoArmor.set(new MobEquipAlgorithm.VANILLA_ARMOR(this.rand, clampedDifficulty, this.world.getDifficulty()));
        return clampedDifficulty;
    }

    // -------- MAINHAND --------

    @Definition(id = "f", local = @Local(type = float.class))
    @Expression("0.25 * f")
    @ModifyExpressionValue(
            method = "setEnchantmentBasedOnDifficulty",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private float mobequipmenttweaker_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_enchChance(float constant, @Share("algoHand") LocalRef<MobEquipAlgorithm> algoHand){
        return algoHand.get().chanceToEnchant();
    }

    @ModifyArg(
            method = "setEnchantmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;addRandomEnchantment(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Lnet/minecraft/item/ItemStack;", ordinal = 0)
    )
    private int mobequipmenttweaker_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_enchLvl(int original, @Share("algoHand") LocalRef<MobEquipAlgorithm> algoHand) {
        return algoHand.get().getEnchantLvl();
    }

    @ModifyArg(
            method = "setEnchantmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;addRandomEnchantment(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Lnet/minecraft/item/ItemStack;", ordinal = 0)
    )
    private boolean mobequipmenttweaker_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_allowTreasure(boolean original) {
        return ConfigHandler.melee.enchants.allowTreasure;
    }

    // -------- ARMOR --------

    @Definition(id = "f", local = @Local(type = float.class))
    @Expression("0.5 * f")
    @ModifyExpressionValue(
            method = "setEnchantmentBasedOnDifficulty",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private float mobequipmenttweaker_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_enchChance_armor(float constant, @Share("algoArmor") LocalRef<MobEquipAlgorithm> algoArmor){
        return algoArmor.get().chanceToEnchant();
    }

    @ModifyArg(
            method = "setEnchantmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;addRandomEnchantment(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Lnet/minecraft/item/ItemStack;", ordinal = 1)
    )
    private int mobequipmenttweaker_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_enchLvl_armor(int original, @Share("algoArmor") LocalRef<MobEquipAlgorithm> algoArmor) {
        return algoArmor.get().getEnchantLvl();
    }

    @ModifyArg(
            method = "setEnchantmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;addRandomEnchantment(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Lnet/minecraft/item/ItemStack;", ordinal = 1)
    )
    private boolean mobequipmenttweaker_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_allowTreasure_armor(boolean original) {
        return ConfigHandler.armor.enchants.allowTreasure;
    }
}
