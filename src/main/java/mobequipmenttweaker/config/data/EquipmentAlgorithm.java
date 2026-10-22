package mobequipmenttweaker.config.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.common.config.Config;
import meldexun.betterconfig.api.Order;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Defines how equipment is rolled for mobs (equip chances, tier progression, etc.)
 */
public class EquipmentAlgorithm {

    @Config.Name("ID")
    @Config.Comment("Unique identifier for this algorithm")
    @Order(0)
    public String id = "";

    @Config.Name("Armor Rolling")
    @Order(1)
    public ArmorRolling armorRolling = new ArmorRolling();

    @Config.Name("Weapon Rolling")
    @Order(2)
    public WeaponRolling weaponRolling = new WeaponRolling();

    @Config.Name("Offhand Rolling")
    @Order(3)
    public OffhandRolling offhandRolling = new OffhandRolling();

    @Config.Name("Tier Rolling")
    @Order(4)
    public TierRolling tierRolling = new TierRolling();

    public EquipmentAlgorithm() {}

    public EquipmentAlgorithm(String id) {
        this.id = id;
    }

    public EquipmentAlgorithm(String id, ArmorRolling armorRolling, WeaponRolling weaponRolling, TierRolling tierRolling) {
        this.id = id;
        this.armorRolling = armorRolling;
        this.weaponRolling = weaponRolling;
        this.tierRolling = tierRolling;
    }

    public static class ArmorRolling {
        @Config.Name("Base Spawn Chance")
        @Config.Comment("Base chance for mobs getting armor. Multiplied with local difficulty (0-1) if enabled.")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        @Order(0)
        public float baseSpawnChance = 0.15F;

        @Config.Name("Additional Piece Chance")
        @Config.Comment("Chance to add more armor pieces after the first. Applied for each additional piece.")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        @Order(1)
        public Map<EnumDifficulty, Float> additionalPieceChance = new LinkedHashMap<>(ImmutableMap.of(
                EnumDifficulty.PEACEFUL, 0.0F,
                EnumDifficulty.EASY, 0.75F,
                EnumDifficulty.NORMAL, 0.75F,
                EnumDifficulty.HARD, 0.9F
        ));

        @Config.Name("Difficulty Scaling")
        @Config.Comment("Scale chances with local difficulty")
        @Order(2)
        public boolean difficultyScaling = true;

        public ArmorRolling() {}

        public ArmorRolling(float baseSpawnChance, Map<EnumDifficulty, Float> additionalPieceChance, boolean difficultyScaling) {
            this.baseSpawnChance = baseSpawnChance;
            this.additionalPieceChance = additionalPieceChance;
            this.difficultyScaling = difficultyScaling;
        }
    }

    public static class WeaponRolling {
        @Config.Name("Base Spawn Chance")
        @Config.Comment("Base chance for mobs getting weapons. Scaled with local difficulty if enabled.")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        @Order(0)
        public Map<EnumDifficulty, Float> baseSpawnChance = new LinkedHashMap<>(ImmutableMap.of(
            EnumDifficulty.PEACEFUL, 0.0F,
            EnumDifficulty.EASY, 0.01F,
            EnumDifficulty.NORMAL, 0.01F,
            EnumDifficulty.HARD, 0.05F
        ));

        @Config.Name("Difficulty Scaling")
        @Config.Comment("Scale chances with local difficulty")
        @Order(1)
        public boolean difficultyScaling = true;

        public WeaponRolling() {}

        public WeaponRolling(Map<EnumDifficulty, Float> baseSpawnChance, boolean difficultyScaling) {
            this.baseSpawnChance = baseSpawnChance;
            this.difficultyScaling = difficultyScaling;
        }
    }

    public static class OffhandRolling {
        @Config.Name("Base Spawn Chance")
        @Config.Comment("Base chance for mobs getting an offhand item from their pool. Scaled with local difficulty if enabled. Tied offhands of the rolled mainhand ignore this chance.")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        @Order(0)
        public Map<EnumDifficulty, Float> baseSpawnChance = new LinkedHashMap<>(ImmutableMap.of(
            EnumDifficulty.PEACEFUL, 0.0F,
            EnumDifficulty.EASY, 0.01F,
            EnumDifficulty.NORMAL, 0.01F,
            EnumDifficulty.HARD, 0.05F
        ));

        @Config.Name("Difficulty Scaling")
        @Config.Comment("Scale chances with local difficulty")
        @Order(1)
        public boolean difficultyScaling = true;

        public OffhandRolling() {}

        public OffhandRolling(Map<EnumDifficulty, Float> baseSpawnChance, boolean difficultyScaling) {
            this.baseSpawnChance = baseSpawnChance;
            this.difficultyScaling = difficultyScaling;
        }
    }

    public static class TierRolling {
        @Config.Name("Max Base Tier")
        @Config.Comment("Starting tier is rolled uniformly between 0 and this value (inclusive).")
        @Config.RangeInt(min = 0)
        @Order(0)
        public int maxBaseTier = 1;

        @Config.Name("Tier Advancement Rolls")
        @Config.Comment("How often the tier advancement chance is rolled after the starting tier.")
        @Config.RangeInt(min = 0)
        @Order(1)
        public int advancementRolls = 3;

        @Config.Name("Tier Advancement Chance")
        @Config.Comment("Chance per roll to advance to the next tier (binomial progression).")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        @Order(2)
        public float tierAdvancementChance = 0.095F;

        public TierRolling() {}

        public TierRolling(int maxBaseTier, int advancementRolls, float tierAdvancementChance) {
            this.maxBaseTier = maxBaseTier;
            this.advancementRolls = advancementRolls;
            this.tierAdvancementChance = tierAdvancementChance;
        }
    }
}