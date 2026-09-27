package mobequipmenttweaker.util;

import com.oblivioussp.spartanweaponry.entity.projectile.EntityArrowBase;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityArrowExplosive;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBolt;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBoltTipped;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBoomerang;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrowingAxe;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrowingKnife;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownJavelin;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownWeapon;
import com.oblivioussp.spartanweaponry.init.EnchantmentRegistrySW;
import com.oblivioussp.spartanweaponry.init.ItemRegistrySW;
import com.oblivioussp.spartanweaponry.init.SoundRegistry;
import com.oblivioussp.spartanweaponry.item.ItemArrowTipped;
import com.oblivioussp.spartanweaponry.item.ItemBolt;
import com.oblivioussp.spartanweaponry.item.ItemBoltTipped;
import com.oblivioussp.spartanweaponry.item.ItemBoomerang;
import com.oblivioussp.spartanweaponry.item.ItemCrossbow;
import com.oblivioussp.spartanweaponry.item.ItemJavelin;
import com.oblivioussp.spartanweaponry.item.ItemLongbow;
import com.oblivioussp.spartanweaponry.item.ItemThrowingAxe;
import com.oblivioussp.spartanweaponry.item.ItemThrowingKnife;
import com.oblivioussp.spartanweaponry.item.ItemThrowingWeapon;
import com.oblivioussp.spartanweaponry.util.NBTHelper;
import com.oblivioussp.spartanweaponry.util.Quaternion;
import mobequipmenttweaker.config.ConfigHandler;
import mobequipmenttweaker.mixin.spartanweaponry.IItemCrossbow_Invoker;
import mobequipmenttweaker.mixin.spartanweaponry.IItemThrowingWeapon_Accessor;
import mobequipmenttweaker.mixin.spartanweaponry.IItemThrowingWeapon_Invoker;
import mobequipmenttweaker.mixin.vanilla.arrows.IAbstractSkeleton_Invoker;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * This is a very reduced copy thanks to not needing Throwing Weapon Handling
 */
public abstract class SpartanWeaponryUtil {
    public static boolean isSpartanTippedArrow(Item item){
        return item instanceof ItemArrowTipped;
    }

    public static boolean isTippedBolt(Item item){
        return item instanceof ItemBoltTipped;
    }

    public static boolean isSpartanCrossbow(Item item){
        return item instanceof ItemCrossbow;
    }

    public static boolean isSpartanLongbow(Item item){
        return item instanceof ItemLongbow;
    }

    public static boolean isSpartanThrowingWeapon(Item item){
        return item instanceof ItemThrowingWeapon;
    }

    public static boolean isHoldingSpartanRangedWeapon(EntityLivingBase shooter){
        if(!shooter.getHeldItemMainhand().isEmpty()){
            Item item = shooter.getHeldItemMainhand().getItem();
            if(isSpartanLongbow(item)) return true;
            else if(isSpartanCrossbow(item)) return true;
            else if(isSpartanThrowingWeapon(item)) return true;
        }
        return false;
    }

    public static void addPotionEffectsToArrow(EntityArrow entityArrow, Set<PotionEffect> vanillaCustomPotionEffects){
        if(entityArrow instanceof EntityBoltTipped){
            vanillaCustomPotionEffects.forEach(potionEffect -> ((EntityBoltTipped) entityArrow).addEffect(potionEffect));
        }
        else if(entityArrow instanceof EntityArrowBase && !(entityArrow instanceof EntityArrowExplosive)){
            vanillaCustomPotionEffects.forEach(potionEffect -> ((EntityArrowBase) entityArrow).addEffect(potionEffect));
        }
        else if(entityArrow instanceof EntityTippedArrow){
            vanillaCustomPotionEffects.forEach(potionEffect -> ((EntityTippedArrow) entityArrow).addEffect(potionEffect));
        }
    }

    public static double getMaxVelocity(ItemStack itemStack){
        if(itemStack.getItem() instanceof ItemLongbow) return ((ItemLongbow) itemStack.getItem()).getMaxArrowSpeed();
        else if(itemStack.getItem() instanceof ItemCrossbow) return ((ItemCrossbow)itemStack.getItem()).getBoltSpeed();
        else if(itemStack.getItem() instanceof IItemThrowingWeapon_Accessor){
            return ((IItemThrowingWeapon_Accessor)itemStack.getItem())
                    .met$accessorThrowVelocity()
                    * ((EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_RANGE, itemStack) * 0.2f)
                    + 1
            );
        }
        return 3.0D; // Vanilla Bow for player
    }

    public static double getDistanceFactorVelocity(ItemStack itemStack){
        if(itemStack.getItem() instanceof ItemThrowingWeapon) return 0;
        return getMaxVelocity(itemStack);
    }

    public static int getAimAndLoadingTicks(ItemStack itemStack){
        if(itemStack.getItem() instanceof ItemCrossbow){
            boolean loadedBefore = NBTHelper.getBoolean(itemStack, ItemCrossbow.NBT_IS_LOADED);
            NBTHelper.setBoolean(itemStack, ItemCrossbow.NBT_IS_LOADED, false);
            int cooldown = itemStack.getItem().getMaxItemUseDuration(itemStack) + ((ItemCrossbow)itemStack.getItem()).getAimTicks(itemStack);
            NBTHelper.setBoolean(itemStack, ItemCrossbow.NBT_IS_LOADED, loadedBefore);

            return cooldown;
        }
        return getDrawSpeedTicks(itemStack); // Bows Load and Draw simultaneously
    }

    public static int getDrawSpeedTicks(ItemStack itemStack){
        if(itemStack.getItem() instanceof ItemLongbow) return ((ItemLongbow) itemStack.getItem()).getDrawTicks();
        else if(itemStack.getItem() instanceof ItemCrossbow){
            return NBTHelper.getBoolean(itemStack, ItemCrossbow.NBT_IS_LOADED) ?
                    ((ItemCrossbow)itemStack.getItem()).getAimTicks(itemStack) :
                    itemStack.getItem().getMaxItemUseDuration(itemStack);
        }
        else if(itemStack.getItem() instanceof ItemThrowingWeapon){
            return Math.round(((ItemThrowingWeapon)itemStack.getItem()).getMaxChargeTicks(itemStack) * ConfigHandler.ranged.spartanSkeletons.throwingCooldown);
        }
        return 20; // Vanilla Bow
    }

    public static int resetCustomBow(ItemStack itemStack, int attackCooldown){
        if(itemStack.getItem() instanceof ItemCrossbow) {
            NBTHelper.setBoolean(itemStack, ItemCrossbow.NBT_IS_LOADED, false);
            NBTHelper.setTagCompound(itemStack, ItemCrossbow.nbtAmmoStack, new NBTTagCompound());
        }
        else if(itemStack.getItem() instanceof ItemThrowingWeapon){
            boolean doFullReload = false;
            if(!itemStack.hasTagCompound()){
                ((IItemThrowingWeapon_Invoker) itemStack.getItem()).met$inokerInitNBT(itemStack, true);
            }
            int maxAmmo = ((ItemThrowingWeapon) itemStack.getItem()).getMaxAmmo(itemStack);
            int ammoCount = maxAmmo - NBTHelper.getInteger(itemStack, ItemThrowingWeapon.NBT_AMMO_USED);
            if(ammoCount > 0){
                ammoCount--;
                itemStack.getTagCompound().setInteger(ItemThrowingWeapon.NBT_AMMO_USED, maxAmmo - ammoCount);
                if(ammoCount == 0){
                    itemStack.getTagCompound().setInteger(ItemThrowingWeapon.NBT_AMMO_USED, maxAmmo);
                    doFullReload = true;
                }
            }
            else{
                itemStack.getTagCompound().setInteger(ItemThrowingWeapon.NBT_AMMO_USED, 1);
            }

            if(doFullReload || maxAmmo <= 1){
                return Math.round(attackCooldown * ((ItemThrowingWeapon) itemStack.getItem()).getMaxAmmoBase() * ConfigHandler.ranged.spartanSkeletons.throwingFullReload);
            }
        }
        return attackCooldown;
    }

    public static void shootCustomBow(EntityArrow entityArrow, double x, double y, double z, float velocity, float inaccuracy, EntityLiving shooter, float distanceFactor){
        ItemStack itemStack = shooter.getHeldItemMainhand();
        if (itemStack.getItem() instanceof ItemLongbow){
            entityArrow.shoot(
                    x,
                    y,
                    z,
                    velocity * ((ItemLongbow)itemStack.getItem()).getMaxArrowSpeed(),
                    inaccuracy * 0.5F
            );
        }
        else if (itemStack.getItem() instanceof IItemCrossbow_Invoker){
            int shots = 1;
            NBTTagCompound nbtLoadedBolt = NBTHelper.getTagCompound(itemStack, ItemCrossbow.nbtAmmoStack);
            if(nbtLoadedBolt.hasKey("Count") && nbtLoadedBolt.getInteger("Count") > 1){
                shots = 3;
            }

            float projectileAngle = 0F;
            Vec3d lookVec = shooter.getLook(1.0F);
            Vec3d vector = new Vec3d(lookVec.x, lookVec.y, lookVec.z);
            for(int i = 0; i < shots; i++){
                if(projectileAngle != 0F){
                    Vec3d shooterUpVec = ((IItemCrossbow_Invoker)itemStack.getItem()).invokeCalculateEntityViewVector(
                            shooter.rotationPitch - 90.0f,
                            shooter.rotationYaw
                    );
                    Quaternion quat = new Quaternion(shooterUpVec, projectileAngle, true);
                    vector = quat.transformVector(lookVec);
                }

                EntityArrow entityBolt = entityArrow;
                if(projectileAngle != 0F && shooter instanceof IAbstractSkeleton_Invoker) {
                    entityBolt = ((IAbstractSkeleton_Invoker)shooter).met$invokeGetArrow(distanceFactor);
                }
                entityBolt.shoot(
                        vector.x,
                        vector.y,
                        vector.z,
                        velocity * ((ItemCrossbow) itemStack.getItem()).getBoltSpeed(),
                        0F
                );

                if(projectileAngle != 0F) shooter.world.spawnEntity(entityBolt);
                projectileAngle = (1 - 2 * i) * 10F;
            }
        }
        else if (itemStack.getItem() instanceof IItemThrowingWeapon_Accessor){
            int velocityBonus = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_RANGE, itemStack);
            entityArrow.shoot(
                    shooter,
                    shooter.rotationPitch,
                    shooter.rotationYaw,
                    0.0F,
                    ((IItemThrowingWeapon_Accessor) itemStack.getItem()).met$accessorThrowVelocity() * ((velocityBonus * 0.2f) + 1),
                    0.5f
            );
        }
    }

    public static Item getTippedBoltItem() {
        return ItemRegistrySW.boltTipped;
    }

    public static void addCrossbowNBT(ItemStack itemStack) {
        if(itemStack.getItem() instanceof ItemCrossbow) {
            NBTHelper.setBoolean(itemStack, ItemCrossbow.NBT_IS_LOADED, false);
            NBTHelper.setTagCompound(itemStack, ItemCrossbow.nbtAmmoStack, new NBTTagCompound());
        }
    }

    public static float getMaxLongbowArrowSpeed(Item item) {
        return ((ItemLongbow)item).getMaxArrowSpeed();
    }

    public static Vec3d getShootingVector(Item item, Vec3d lookVec, float rotationPitch, float rotationYaw, float projectileAngle) {
        Vec3d shooterUpVec = ((IItemCrossbow_Invoker) item).invokeCalculateEntityViewVector(
                rotationPitch - 90.0f,
                rotationYaw
        );
        Quaternion quat = new Quaternion(shooterUpVec, projectileAngle, true);
        return quat.transformVector(lookVec);
    }

    public static float getCrossbowBoltSpeed(Item item) {
        return ((ItemCrossbow) item).getBoltSpeed();
    }

    public static int getCrossbowShotCount(ItemStack stack) {
        int shots = 1;
        NBTTagCompound nbtLoadedBolt = NBTHelper.getTagCompound(stack, ItemCrossbow.nbtAmmoStack);
        if (nbtLoadedBolt.hasKey("Count") && nbtLoadedBolt.getInteger("Count") > 1) {
            shots = 3;
        }
        return shots;
    }

    @Nullable
    public static EntityArrow getCustomArrow(EntityLiving shooter, float distanceFactor){
        ItemStack mainHandStack = shooter.getHeldItemMainhand();

        if(mainHandStack.getItem() instanceof ItemCrossbow){
            ItemStack storedItemStack = shooter.getHeldItemOffhand();

            //No offhand item -> use normal bolts. Same if modded arrows are disallowed
            if(!(storedItemStack.getItem() instanceof ItemBolt) || !ConfigHandler.mixintoggles.enabledModdedArrowsForAll)
                storedItemStack = new ItemStack(ItemRegistrySW.bolt);

            EntityBolt entityBolt = ((ItemBolt) storedItemStack.getItem()).createBolt(shooter.world, mainHandStack, shooter);
            entityBolt.setEnchantmentEffectsFromEntity(shooter, distanceFactor);

            if(entityBolt instanceof EntityBoltTipped) ((EntityBoltTipped) entityBolt).setPotionEffect(storedItemStack);
            return entityBolt;
        }
        else if (mainHandStack.getItem() instanceof ItemThrowingWeapon) {
            EntityThrownWeapon entityArrow = SpartanWeaponryUtil.createThrownWeaponEntity((ItemThrowingWeapon) mainHandStack.getItem(), shooter.world, shooter);
            SpartanWeaponryUtil.setThrownWeaponEnchantmentEffectsFromEntity(entityArrow, shooter, distanceFactor);
            return entityArrow;
        }
        return null; // Flag to use vanilla handling
    }

    public static SoundEvent getCustomShootSound(Item item, SoundEvent soundEvent){
        if (item instanceof ItemThrowingWeapon) return SoundRegistry.THROWING_WEAPON_THROW;
        else if (item instanceof ItemCrossbow) return SoundRegistry.CROSSBOW_FIRE;
        return soundEvent;
    }

    public static EntityThrownWeapon createThrownWeaponEntity(ItemThrowingWeapon throwingWeapon, World world, EntityLivingBase shooter){
        if(throwingWeapon instanceof ItemBoomerang) return new EntityBoomerang(world, shooter);
        else if(throwingWeapon instanceof ItemJavelin) return new EntityThrownJavelin(world, shooter);
        else if(throwingWeapon instanceof ItemThrowingAxe) return new EntityThrowingAxe(world, shooter);
        else if(throwingWeapon instanceof ItemThrowingKnife) return new EntityThrowingKnife(world, shooter);
        else return new EntityThrownWeapon(world, shooter);
    }

    public static void setThrownWeaponEnchantmentEffectsFromEntity(EntityThrownWeapon entityThrownWeapon, EntityLivingBase shooter, float distanceFactor){
        ItemStack itemStack = shooter.getHeldItemMainhand();
        if(!(itemStack.getItem() instanceof ItemThrowingWeapon)) return;
        entityThrownWeapon.setWeapon(itemStack);

        double damageMultiplier = (((ItemThrowingWeapon) itemStack.getItem()).getThrownDamageMultiplier() - 1.0f) + 1.0f;
        entityThrownWeapon.setDamage(
                MathHelper.ceil((
                                (((ItemThrowingWeapon) itemStack.getItem()).getDirectAttackDamage() * (1.6F / 3F))
                                        * damageMultiplier
                                        + 1.0D
                                        + shooter.getRNG().nextGaussian() * 0.25D
                                        + shooter.world.getDifficulty().getId() * 0.11F
                        )
                )
        );

        // Apply enchantments as necessary
        int propulsion = EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_DAMAGE, itemStack);
        if (propulsion > 0) {
            entityThrownWeapon.setDamage(entityThrownWeapon.getDamage() + propulsion * 0.5D + 0.5D);
        }
        int knockback = EnchantmentHelper.getEnchantmentLevel(Enchantments.KNOCKBACK, itemStack);
        if (knockback > 0) {
            entityThrownWeapon.setKnockbackStrength(knockback);
        }
        if (EnchantmentHelper.getEnchantmentLevel(EnchantmentRegistrySW.THROWING_FIRE, itemStack) > 0) {
            entityThrownWeapon.setFire(100);
        }
    }
}
