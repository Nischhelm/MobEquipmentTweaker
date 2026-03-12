package mobequipmenttweaker.mixin.vanilla.mobequipment;

import com.llamalad7.mixinextras.sugar.Local;
import mobequipmenttweaker.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin_Enchants extends EntityLivingBase {
    public EntityLivingMixin_Enchants(World worldIn) {
        super(worldIn);
    }

    // -------- MAINHAND --------

    @ModifyConstant(
            method = "setEnchantmentBasedOnDifficulty",
            constant = @Constant(floatValue = 0.25F)
    )
    private float eaglemixins_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_enchChance(float constant){
        return ConfigHandler.melee.enchants.chanceEnchant;
    }

    @ModifyArg(
            method = "setEnchantmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;addRandomEnchantment(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Lnet/minecraft/item/ItemStack;", ordinal = 0)
    )
    private int eaglemixins_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_enchLvl(int original, @Local(name = "f") float clampedDifficulty) {
        int range = ConfigHandler.melee.enchants.maxEnch - ConfigHandler.melee.enchants.minEnch;
        return ConfigHandler.melee.enchants.minEnch + (int)(clampedDifficulty * (float) this.rand.nextInt(range + 1));
    }

    @ModifyArg(
            method = "setEnchantmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;addRandomEnchantment(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Lnet/minecraft/item/ItemStack;", ordinal = 0)
    )
    private boolean eaglemixins_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_allowTreasure(boolean original) {
        return ConfigHandler.melee.enchants.allowTreasure;
    }

    // -------- ARMOR --------

    @ModifyConstant(
            method = "setEnchantmentBasedOnDifficulty",
            constant = @Constant(floatValue = 0.5F)
    )
    private float eaglemixins_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_enchChance_armor(float constant){
        return ConfigHandler.melee.enchants.chanceEnchant;
    }

    @ModifyArg(
            method = "setEnchantmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;addRandomEnchantment(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Lnet/minecraft/item/ItemStack;", ordinal = 1)
    )
    private int eaglemixins_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_enchLvl_armor(int original, @Local(name = "f") float clampedDifficulty) {
        int range = ConfigHandler.melee.enchants.maxEnch - ConfigHandler.melee.enchants.minEnch;
        return ConfigHandler.melee.enchants.minEnch + (int)(clampedDifficulty * (float) this.rand.nextInt(range + 1));
    }

    @ModifyArg(
            method = "setEnchantmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;addRandomEnchantment(Ljava/util/Random;Lnet/minecraft/item/ItemStack;IZ)Lnet/minecraft/item/ItemStack;", ordinal = 1)
    )
    private boolean eaglemixins_vanillaEntityLiving_setEnchantmentBasedOnDifficulty_allowTreasure_armor(boolean original) {
        return ConfigHandler.melee.enchants.allowTreasure;
    }
}
