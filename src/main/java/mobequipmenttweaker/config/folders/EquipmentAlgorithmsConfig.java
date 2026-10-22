package mobequipmenttweaker.config.folders;

import com.google.common.collect.ImmutableMap;
import mobequipmenttweaker.config.data.EquipmentAlgorithm;
import net.minecraftforge.common.config.Config;
import meldexun.betterconfig.api.Order;
import net.minecraft.world.EnumDifficulty;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configuration for equipment rolling algorithms
 */
public class EquipmentAlgorithmsConfig {

    @Config.Name("Equipment Algorithms")
    @Config.Comment({
        "Defines how equipment is rolled for mobs.",
        "Each algorithm controls spawn chances, tier progression, and difficulty scaling."
    })
    @Order(0)
    public ArrayList<EquipmentAlgorithm> algorithms = Stream.of(
        createVanillaStandard(),
        createVanillaGuaranteedWeapon()
    ).collect(Collectors.toCollection(ArrayList::new));

    /**
     * Creates the default vanilla-like equipment algorithm
     */
    private static EquipmentAlgorithm createVanillaStandard() {
        EquipmentAlgorithm algo = new EquipmentAlgorithm("Vanilla Standard");

        // Vanilla armor rolling - uses default difficulty maps
        algo.armorRolling = new EquipmentAlgorithm.ArmorRolling();

        // Vanilla weapon rolling - uses default difficulty maps, zombie weapon chance is not scaled by local difficulty
        algo.weaponRolling = new EquipmentAlgorithm.WeaponRolling();
        algo.weaponRolling.difficultyScaling = false;

        // Vanilla mobs never get offhand items
        algo.offhandRolling = new EquipmentAlgorithm.OffhandRolling();

        // Vanilla tier rolling
        algo.tierRolling = new EquipmentAlgorithm.TierRolling();

        return algo;
    }

    /**
     * Mobs always get a weapon (skeletons, pigmen, vindicators, vexes, wither skeletons).
     * Armor rolling is vanilla, but of those only skeletons/strays get armor in vanilla, the other groups have empty armor pools
     */
    private static EquipmentAlgorithm createVanillaGuaranteedWeapon() {
        EquipmentAlgorithm algo = createVanillaStandard();
        algo.id = "Vanilla Guaranteed Weapon";
        algo.weaponRolling = new EquipmentAlgorithm.WeaponRolling(new LinkedHashMap<>(ImmutableMap.of(
                EnumDifficulty.PEACEFUL, 1.0F,
                EnumDifficulty.EASY, 1.0F,
                EnumDifficulty.NORMAL, 1.0F,
                EnumDifficulty.HARD, 1.0F
        )), false);
        return algo;
    }
}