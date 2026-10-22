package mobequipmenttweaker.util;

import com.Fishmod.mod_LavaCow.entities.projectiles.EntityCactusThorn;
import com.Fishmod.mod_LavaCow.entities.projectiles.EntityEnchantableFireBall;
import com.Fishmod.mod_LavaCow.entities.projectiles.EntityPiranhaLauncher;
import com.Fishmod.mod_LavaCow.entities.projectiles.EntityWarSmallFireball;
import com.Fishmod.mod_LavaCow.init.FishItems;
import com.Fishmod.mod_LavaCow.item.ItemPiranhaLauncher;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class FishUndeadRisingUtil {

    public static boolean isFURBow(Item item){
        return item instanceof ItemPiranhaLauncher;
    }

    public static boolean isHoldingFURRangedWeapon(EntityLiving shooter){
        return !shooter.getHeldItemMainhand().isEmpty() && shooter.getHeldItemMainhand().getItem() instanceof ItemPiranhaLauncher;
    }

    public static double getMaxVelocity(ItemStack itemStack){
        Item item = itemStack.getItem();
        if(item == FishItems.THORN_SHOOTER) return 1.6D * 2.0D / 3.0D;
        else if(item == FishItems.PIRANHALAUNCHER) return 1.6D * 2.0D / 3.0D;
        else if(item == FishItems.WAR) return 1.6D * 2.5D / 3.0D;
        return 3.0D; // Vanilla Bow for player
    }

    public static double getDistanceFactorVelocity(ItemStack itemStack){
        return getMaxVelocity(itemStack);
    }

    public static int getAimAndLoadingTicks(ItemStack itemStack){
        return getDrawSpeedTicks(itemStack); // Bows Load and Draw simultaneously
    }

    public static int getDrawSpeedTicks(ItemStack itemStack){
        Item item = itemStack.getItem();
        if(item == FishItems.THORN_SHOOTER) return 1;
        else if(item == FishItems.PIRANHALAUNCHER) return 20;
        else if(item == FishItems.WAR) return 20;
        return 20; // Vanilla Bow
    }

    public static int resetCustomBow(ItemStack itemStack, int attackCooldown){
        return attackCooldown;
    }

    public static void shootCustomBow(EntityArrow entityArrow, double x, double y, double z, float velocity, float inaccuracy, EntityLiving shooter, float distanceFactor){
        ItemStack itemStack = shooter.getHeldItemMainhand();
        Vec3d lookVec = shooter.getLookVec();
        if(itemStack.getItem() instanceof ItemPiranhaLauncher && entityArrow instanceof EntityCactusThorn){
            if(itemStack.getItem() == FishItems.THORN_SHOOTER){
                EntityCactusThorn cactusThorn = (EntityCactusThorn) entityArrow;
                cactusThorn.shoot(
                        shooter,
                        shooter.rotationPitch,
                        shooter.rotationYaw,
                        0.0F,
                        (float) getMaxVelocity(itemStack),
                        2.0F
                );
            }
        }
        else if(itemStack.getItem() == FishItems.PIRANHALAUNCHER){
            EntityPiranhaLauncher piranhaAmmo = new EntityPiranhaLauncher(shooter.world);
            piranhaAmmo.shootingEntity = shooter;
            piranhaAmmo.addVelocity(lookVec.x * 2.0D, lookVec.y * 2.0D, lookVec.z * 2.0D);
            piranhaAmmo.setPosition(shooter.posX + lookVec.x, shooter.posY + (double)(shooter.height), shooter.posZ + lookVec.z);

            setFireBallEnchantmentEffectsFromEntity(piranhaAmmo, shooter, distanceFactor);
            entityArrow.setDead();
            shooter.world.spawnEntity(piranhaAmmo);
        }
        else if(itemStack.getItem() instanceof ItemPiranhaLauncher){
            EntityWarSmallFireball entityAmmo = new EntityWarSmallFireball(shooter.world);
            entityAmmo.shootingEntity = shooter;
            entityAmmo.addVelocity(lookVec.x * 2.5D, lookVec.y * 2.5D, lookVec.z * 2.5D);
            entityAmmo.setPosition(shooter.posX + lookVec.x, shooter.posY + (double)(shooter.height), shooter.posZ + lookVec.z);

            setFireBallEnchantmentEffectsFromEntity(entityAmmo, shooter, distanceFactor);
            entityArrow.setDead();
            shooter.world.spawnEntity(entityAmmo);
        }
    }

    @Nullable
    public static EntityArrow getCustomArrow(EntityLiving shooter, float distanceFactor){
        ItemStack mainHandStack = shooter.getHeldItemMainhand();
        if(mainHandStack.getItem() == FishItems.THORN_SHOOTER){
            EntityCactusThorn cactusThorn = new EntityCactusThorn(shooter.world, shooter);

            int powerLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, mainHandStack);
            int punchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, mainHandStack);
            if(powerLvl > 0){
                cactusThorn.setDamage(cactusThorn.getDamage() + powerLvl * 0.1D + 0.1D);
            }
            if(punchLvl > 0){
                cactusThorn.setKnockbackStrength(punchLvl);
            }
            if(EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, mainHandStack) > 0){
                cactusThorn.setFire(100);
            }

            return cactusThorn;
        }
        return null; // Flag to use vanilla handling
    }

    public static SoundEvent getCustomShootSound(Item item, SoundEvent soundEvent){
        if (item == FishItems.THORN_SHOOTER) return FishItems.RANDOM_THORN_SHOOT;
        else if (item == FishItems.PIRANHALAUNCHER) return FishItems.RANDOM_PIRANHA_SHOOT;
        else if(item == FishItems.WAR) return SoundEvents.ENTITY_BLAZE_SHOOT;
        return soundEvent;
    }

    public static void setFireBallEnchantmentEffectsFromEntity(EntityEnchantableFireBall entityEnchantableFireBall, EntityLivingBase shooter, float distanceFactor){
        ItemStack itemStack = shooter.getHeldItemMainhand();
        if(!(itemStack.getItem() instanceof ItemPiranhaLauncher)) return;

        int powerLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, itemStack);
        int punchLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, itemStack);
        int flameLvl = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, itemStack);

        if(powerLvl > 0){
            entityEnchantableFireBall.setDamage(entityEnchantableFireBall.getDamage() * (1F + (powerLvl + 1) * 0.25F));
        }
        if(powerLvl > 0){
            entityEnchantableFireBall.setKnockbackStrength(punchLvl);
        }
        if(flameLvl > 0){
            if(entityEnchantableFireBall instanceof EntityPiranhaLauncher){
                entityEnchantableFireBall.setFire(100);
            }
            else if(entityEnchantableFireBall instanceof EntityWarSmallFireball){
                entityEnchantableFireBall.setFlame(true);
            }
        }
    }
}
