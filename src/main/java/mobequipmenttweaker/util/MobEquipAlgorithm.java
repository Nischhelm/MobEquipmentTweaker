package mobequipmenttweaker.util;

import mobequipmenttweaker.config.ConfigHandler;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;

import java.util.Random;
import java.util.stream.Collectors;

public abstract class MobEquipAlgorithm {
    protected float localDifficulty;
    protected EnumDifficulty worldDifficulty;
    protected Random rand;

    public MobEquipAlgorithm(Random rand, float localDifficulty, EnumDifficulty worldDifficulty) {
        this.rand = rand;
        this.localDifficulty = localDifficulty;
        this.worldDifficulty = worldDifficulty;
    }

    public abstract int rollTier();
    public abstract float chanceToEquip();
    public abstract float chanceToAddPieces();
    public abstract float chanceToEnchant();
    public abstract int getEnchantLvl();

    public static class VANILLA_GUARANTEED extends MobEquipAlgorithm { //Skeleton, Zombie Pig, Vex, Vindicator, Wither Skellie
        public VANILLA_GUARANTEED(Random rand, float localDifficulty, EnumDifficulty worldDifficulty) {
            super(rand, localDifficulty, worldDifficulty);
        }

        @Override
        public int rollTier() {
            return 0;
        }

        @Override
        public float chanceToEquip() {
            return 1;
        }

        @Override
        public float chanceToAddPieces() {
            return 0;
        }

        @Override
        public float chanceToEnchant() {
            return ConfigHandler.ranged.enchants.chanceEnchant * localDifficulty;
        }

        @Override
        public int getEnchantLvl() {
            return MathHelper.getInt(rand, ConfigHandler.ranged.enchants.minEnch, (int) (localDifficulty * ConfigHandler.ranged.enchants.maxEnch));
        }
    }

    public static class VANILLA_MELEE extends MobEquipAlgorithm {
        public VANILLA_MELEE(Random rand, float localDifficulty, EnumDifficulty worldDifficulty) {
            super(rand, localDifficulty, worldDifficulty);
        }

        @Override
        public int rollTier() {
            return WeightedRandomIndex.getRandomIndex(rand, ConfigHandler.melee.weaponSetTiers.stream().map(tier -> tier.weight).collect(Collectors.toList()));
        }

        @Override
        public float chanceToEquip() {
            return (worldDifficulty == EnumDifficulty.HARD ? 5 : 1) * 0.01F * ConfigHandler.melee.baseZombieChanceMulti;
        }

        @Override
        public float chanceToAddPieces() {
            return 0F;
        }

        @Override
        public float chanceToEnchant() {
            return ConfigHandler.melee.enchants.chanceEnchant * localDifficulty;
        }

        @Override
        public int getEnchantLvl() {
            return MathHelper.getInt(rand, ConfigHandler.melee.enchants.minEnch, (int) (localDifficulty * ConfigHandler.melee.enchants.maxEnch));
        }
    }

    public static class VANILLA_ARMOR extends MobEquipAlgorithm {
        public VANILLA_ARMOR(Random rand, float localDifficulty, EnumDifficulty worldDifficulty) {
            super(rand, localDifficulty, worldDifficulty);
        }

        @Override
        public int rollTier() {
            return WeightedRandomIndex.getRandomIndex(rand, ConfigHandler.armor.armorSetTiers.stream().map(tier -> tier.weight).collect(Collectors.toList()));
        }

        @Override
        public float chanceToEquip() {
            return ConfigHandler.armor.baseArmorChance * localDifficulty;
        }

        @Override
        public float chanceToAddPieces() {
            return (worldDifficulty == EnumDifficulty.HARD ? 0.9F : 0.75F) * ConfigHandler.armor.additionalArmorChanceMulti;
        }

        @Override
        public float chanceToEnchant() {
            return ConfigHandler.armor.enchants.chanceEnchant * localDifficulty;
        }

        @Override
        public int getEnchantLvl() {
            return MathHelper.getInt(rand, ConfigHandler.armor.enchants.minEnch, (int) (localDifficulty * ConfigHandler.armor.enchants.maxEnch));
        }
    }

}
