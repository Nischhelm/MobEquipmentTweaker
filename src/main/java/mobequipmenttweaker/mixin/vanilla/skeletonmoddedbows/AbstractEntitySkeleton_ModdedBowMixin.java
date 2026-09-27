package mobequipmenttweaker.mixin.vanilla.skeletonmoddedbows;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import mobequipmenttweaker.Tags;
import mobequipmenttweaker.config.ConfigHandler;
import mobequipmenttweaker.mixin.vanilla.arrows.IEntityTippedArrow_Accessor;
import mobequipmenttweaker.util.FishUndeadRisingUtil;
import mobequipmenttweaker.util.ModLoadedUtil;
import mobequipmenttweaker.util.SpartanWeaponryUtil;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractEntitySkeleton_ModdedBowMixin extends EntityMob {
    public AbstractEntitySkeleton_ModdedBowMixin(World world) {
        super(world);
    }

    // TODO Rename and restructure for even more mod compats??? (Ice and Fire Dread Staff, Lycanites Summoning Staff, EBW Staffs, OE Trident, etc)

    @Unique private static final String UUID_WEAPON_RANGE = "e7b2eccc-c495-42d9-81e8-9593f74be7f1";
    @Unique private static final String WEAPON_RANGE_MODIFIER = Tags.MODID + ":moddedWeaponRange";
    @Unique private AttributeModifier eagleMixins$spartanWeaponRange;

    @Shadow @Final private EntityAIAttackRangedBow<AbstractSkeleton> aiArrowAttack;
    @Shadow protected abstract EntityArrow getArrow(float distanceFactor);

    @Inject(
            method = "setCombatTask",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/EntityAITasks;removeTask(Lnet/minecraft/entity/ai/EntityAIBase;)V", ordinal = 0)
    )
    private void eagleMixins_vanillaAbstractSkeleton_setCombatTaskRemoveRangeBonus(CallbackInfo ci) {
        if(this.eagleMixins$spartanWeaponRange != null && ConfigHandler.ranged.spartanSkeletons.enableFollowRangeBonus) {
            IAttributeInstance followRange = this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE);
            if (followRange.hasModifier(eagleMixins$spartanWeaponRange)) followRange.removeModifier(eagleMixins$spartanWeaponRange);
        }
    }

    @ModifyArg(
            method = "setCombatTask",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/EntityAIAttackRangedBow;setAttackCooldown(I)V")
    )
    private int eagleMixins_vanillaAbstractSkeleton_setCombatTaskModifyForLongbow(int cooldown) {
        ItemStack itemStack = this.getHeldItemMainhand();
        if(ModLoadedUtil.spartanweaponry.isLoaded() && SpartanWeaponryUtil.isSpartanLongbow(itemStack.getItem())){
            this.eagleMixins$applyBonusFollowRange(SpartanWeaponryUtil.getMaxVelocity(itemStack));
            return (cooldown / 20) * SpartanWeaponryUtil.getAimAndLoadingTicks(itemStack);
        }
        else if(ModLoadedUtil.fur.isLoaded() && FishUndeadRisingUtil.isFURBow(itemStack.getItem())){
            this.eagleMixins$applyBonusFollowRange(FishUndeadRisingUtil.getMaxVelocity(itemStack));
            return (cooldown / 20) * FishUndeadRisingUtil.getAimAndLoadingTicks(itemStack);
        }
        return cooldown;
    }

    @WrapOperation(
            method = "setCombatTask",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/EntityAITasks;addTask(ILnet/minecraft/entity/ai/EntityAIBase;)V", ordinal = 1)
    )
    private void eagleMixins_vanillaAbstractSkeleton_setCombatTaskCreateForCrossbow(EntityAITasks instance, int priority, EntityAIBase task, Operation<Void> original){
        if(ModLoadedUtil.spartanweaponry.isLoaded() && SpartanWeaponryUtil.isHoldingSpartanRangedWeapon(this) && !SpartanWeaponryUtil.isSpartanLongbow(this.getHeldItemMainhand().getItem())){
            ItemStack itemStack = this.getHeldItemMainhand();
            this.eagleMixins$applyBonusFollowRange(SpartanWeaponryUtil.getMaxVelocity(itemStack));

            int cooldown = SpartanWeaponryUtil.getAimAndLoadingTicks(itemStack);
            if(this.world.getDifficulty() != EnumDifficulty.HARD) cooldown *= 2;
            this.aiArrowAttack.setAttackCooldown(cooldown);

            original.call(instance, priority, this.aiArrowAttack);
        }
        else if(ModLoadedUtil.fur.isLoaded() && FishUndeadRisingUtil.isHoldingFURRangedWeapon(this)){
            ItemStack itemStack = this.getHeldItemMainhand();
            this.eagleMixins$applyBonusFollowRange(FishUndeadRisingUtil.getMaxVelocity(itemStack));

            int cooldown = SpartanWeaponryUtil.getAimAndLoadingTicks(itemStack);
            if(this.world.getDifficulty() != EnumDifficulty.HARD) cooldown *= 2;
            this.aiArrowAttack.setAttackCooldown(cooldown);

            original.call(instance, priority, this.aiArrowAttack);
        }
        else{
            original.call(instance, priority, task);
        }
    }

    @WrapOperation(
            method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/EntityArrow;shoot(DDDFF)V")
    )
    private void eagleMixins_vanillaAbstractSkeleton_attackEntityWithRangedAttackWithSpartan(EntityArrow instance, double x, double y, double z, float velocity, float inaccuracy, Operation<Void> original, @Local(argsOnly = true) float distanceFactor){
        if(ModLoadedUtil.spartanweaponry.isLoaded() && SpartanWeaponryUtil.isHoldingSpartanRangedWeapon(this)){
            SpartanWeaponryUtil.shootCustomBow(instance, x, y, z, velocity, inaccuracy, this, distanceFactor);
        }
        else if(ModLoadedUtil.fur.isLoaded() && FishUndeadRisingUtil.isHoldingFURRangedWeapon(this)){
            FishUndeadRisingUtil.shootCustomBow(instance, x, y, z, velocity, inaccuracy, this, distanceFactor);
        }
        else{
            original.call(instance, x, y, z, velocity, inaccuracy);
        }
    }

    @WrapOperation(
            method = "attackEntityWithRangedAttack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/monster/AbstractSkeleton;playSound(Lnet/minecraft/util/SoundEvent;FF)V")
    )
    private void spartanCombat_vanillaAbstractSkeleton_attackEntityWithRangedAttackCustomSound(AbstractSkeleton instance, SoundEvent soundIn, float volume, float pitch, Operation<Void> original){
        if(ModLoadedUtil.spartanweaponry.isLoaded()) soundIn = SpartanWeaponryUtil.getCustomShootSound(this.getHeldItemMainhand().getItem(), soundIn);
        if(ModLoadedUtil.fur.isLoaded()) soundIn = FishUndeadRisingUtil.getCustomShootSound(this.getHeldItemMainhand().getItem(), soundIn);
        original.call(instance, soundIn, volume, pitch);
    }

    @WrapMethod(method = "getArrow")
    private EntityArrow eagleMixins_vanillaAbstractSkeleton_getBoltForSpartanCrossbow(float distanceFactor, Operation<EntityArrow> original){
        // Call getArrow override to check for (wither shoots flamed, stray shoots slowness tipped, forsaken shoots fur:fragile tipped)
        EntityArrow vanillaArrow = original.call(distanceFactor);
        ItemStack offhandStack = this.getHeldItemOffhand();

        if(offhandStack.getItem() instanceof ItemArrow){
            EntityArrow offHandArrow = ((ItemArrow) offhandStack.getItem()).createArrow(this.world, offhandStack, this);
            offHandArrow.setEnchantmentEffectsFromEntity(this, distanceFactor);

            if(vanillaArrow instanceof IEntityTippedArrow_Accessor){
                if(ModLoadedUtil.spartanweaponry.isLoaded()){
                    SpartanWeaponryUtil.addPotionEffectsToArrow(
                            offHandArrow,
                            ((IEntityTippedArrow_Accessor) vanillaArrow).met$getCustomPotionEffects()
                    );
                }
                else if(offHandArrow instanceof EntityTippedArrow){
                    ((IEntityTippedArrow_Accessor) vanillaArrow).met$getCustomPotionEffects().forEach(potionEffect -> {
                        ((EntityTippedArrow) offHandArrow).addEffect(potionEffect);
                    });
                }
            }

            return offHandArrow;
        }
        else if(ModLoadedUtil.spartanweaponry.isLoaded() && SpartanWeaponryUtil.isHoldingSpartanRangedWeapon(this)){
            EntityArrow spartanArrow = SpartanWeaponryUtil.getCustomArrow(this, distanceFactor);
            if(spartanArrow == null){
                spartanArrow = vanillaArrow;
            }
            else if (vanillaArrow instanceof IEntityTippedArrow_Accessor) {
                SpartanWeaponryUtil.addPotionEffectsToArrow(
                        spartanArrow,
                        ((IEntityTippedArrow_Accessor) vanillaArrow).met$getCustomPotionEffects()
                );
            }
            return spartanArrow;
        }
        else if(ModLoadedUtil.fur.isLoaded() && FishUndeadRisingUtil.isHoldingFURRangedWeapon(this)){
            EntityArrow fishUndeadArrow = FishUndeadRisingUtil.getCustomArrow(this, distanceFactor);
            if(fishUndeadArrow != null) return fishUndeadArrow;
        }

        return original.call(distanceFactor);
    }

    @Unique
    private void eagleMixins$applyBonusFollowRange(double maxVelocityMultiplier){
        if(!ConfigHandler.ranged.spartanSkeletons.enableFollowRangeBonus) return;
        if(maxVelocityMultiplier <= 1) return;

        IAttributeInstance followRange = this.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.FOLLOW_RANGE);
        if(this.eagleMixins$spartanWeaponRange == null){
            this.eagleMixins$spartanWeaponRange = new AttributeModifier(
                    UUID.fromString(UUID_WEAPON_RANGE),
                    WEAPON_RANGE_MODIFIER,
                    maxVelocityMultiplier - 1,
                    1
            );
        }
        if(!followRange.hasModifier(this.eagleMixins$spartanWeaponRange)) followRange.applyModifier(this.eagleMixins$spartanWeaponRange);
    }
}
