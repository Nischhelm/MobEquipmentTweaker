package mobequipmenttweaker.mixin.vanilla.skeletonmoddedbows;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import mobequipmenttweaker.config.ConfigHandler;
import mobequipmenttweaker.util.FishUndeadRisingUtil;
import mobequipmenttweaker.util.ModLoadedUtil;
import mobequipmenttweaker.util.SpartanWeaponryUtil;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.monster.EntityMob;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EntityAIAttackRangedBow.class)
public abstract class EntityAIAttackRangedBow_ModdedBowMixin<T extends EntityMob & IRangedAttackMob> {

    @Shadow @Final private T entity;

    @ModifyReturnValue(
            method = "isBowInMainhand",
            at = @At("RETURN")
    )
    private boolean spartanCombat_vanillaEntityAIAttackRangedBow_isBowInMainhandSpartan(boolean original){
        return original
                || (ModLoadedUtil.spartanweaponry.isLoaded() && SpartanWeaponryUtil.isHoldingSpartanRangedWeapon(this.entity))
                || (ModLoadedUtil.fur.isLoaded() && FishUndeadRisingUtil.isHoldingFURRangedWeapon(this.entity));
    }

    @ModifyExpressionValue(
            method = "updateTask",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/EntityAIAttackRangedBow;moveSpeedAmp:D")
    )
    private double spartanCombat_vanillaEntityAIAttackRangedBow_updateTaskMoveSpeedPenalty(double original){
        if(ConfigHandler.spartanSkeletons.enableMoveSpeedPenalty){
            if(ModLoadedUtil.spartanweaponry.isLoaded() && SpartanWeaponryUtil.isHoldingSpartanRangedWeapon(this.entity)){
                return original / SpartanWeaponryUtil.getMaxVelocity(this.entity.getHeldItemMainhand());
            }
            else if(ModLoadedUtil.fur.isLoaded() && FishUndeadRisingUtil.isHoldingFURRangedWeapon(this.entity)){
                return original / FishUndeadRisingUtil.getMaxVelocity(this.entity.getHeldItemMainhand());
            }
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "updateTask",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/EntityAIAttackRangedBow;maxAttackDistance:F")
    )
    private float spartanCombat_vanillaEntityAIAttackRangedBow_updateTaskStrafeDistanceBonus(float original){
        if(ConfigHandler.spartanSkeletons.enableStrafeDistanceBonus){
            if (ModLoadedUtil.spartanweaponry.isLoaded() && SpartanWeaponryUtil.isHoldingSpartanRangedWeapon(this.entity)) {
                return (float) (original * SpartanWeaponryUtil.getMaxVelocity(this.entity.getHeldItemMainhand()));
            }
            else if(ModLoadedUtil.fur.isLoaded() && FishUndeadRisingUtil.isHoldingFURRangedWeapon(this.entity)){
                return (float) (original * FishUndeadRisingUtil.getMaxVelocity(this.entity.getHeldItemMainhand()));
            }
        }
        return original;
    }

    @ModifyConstant(
            method = "updateTask",
            constant = @Constant(intValue = 20, ordinal = 2)
    )
    private int spartanCombat_vanillaEntityAIAttackRangedBow_updateTaskAimDelay(int constant){
        if(ModLoadedUtil.spartanweaponry.isLoaded() && SpartanWeaponryUtil.isHoldingSpartanRangedWeapon(this.entity)){
            return SpartanWeaponryUtil.getDrawSpeedTicks(this.entity.getHeldItemMainhand());
        }
        else if(ModLoadedUtil.fur.isLoaded() && FishUndeadRisingUtil.isHoldingFURRangedWeapon(this.entity)){
            return FishUndeadRisingUtil.getDrawSpeedTicks(this.entity.getHeldItemMainhand());
        }
        return constant;
    }

    @ModifyExpressionValue(
            method = "updateTask",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemBow;getArrowVelocity(I)F")
    )
    private float spartanCombat_vanillaEntityAIAttackRangedBow_updateTaskGetMaxVelocity(float original){
        if(ModLoadedUtil.spartanweaponry.isLoaded() && SpartanWeaponryUtil.isHoldingSpartanRangedWeapon(this.entity)) {
            return (float) SpartanWeaponryUtil.getDistanceFactorVelocity(this.entity.getHeldItemMainhand());
        }
        else if(ModLoadedUtil.fur.isLoaded() && FishUndeadRisingUtil.isHoldingFURRangedWeapon(this.entity)){
            return (float) FishUndeadRisingUtil.getDistanceFactorVelocity(this.entity.getHeldItemMainhand());
        }
        return original;
    }

    @ModifyExpressionValue(
            method = "updateTask",
            at = @At(value = "FIELD", target = "Lnet/minecraft/entity/ai/EntityAIAttackRangedBow;attackCooldown:I")
    )
    private int spartanCombat_vanillaEntityAIAttackRangedBow_updateTaskResetWeaponState(int original){
        if(ModLoadedUtil.spartanweaponry.isLoaded() && SpartanWeaponryUtil.isHoldingSpartanRangedWeapon(this.entity)) {
            return SpartanWeaponryUtil.resetCustomBow(this.entity.getHeldItemMainhand(), original);
        }
        else if(ModLoadedUtil.fur.isLoaded() && FishUndeadRisingUtil.isHoldingFURRangedWeapon(this.entity)) {
            return FishUndeadRisingUtil.resetCustomBow(this.entity.getHeldItemMainhand(), original);
        }
        return original;
    }
}
