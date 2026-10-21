package mobequipmenttweaker.config.folders;

import mobequipmenttweaker.config.data.MobGroup;
import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configuration for mob groups
 */
public class MobGroupsConfig {

    @Config.Name("Mob Groups")
    @Config.Comment({
        "Defines groups of mobs with shared equipment and enchantment settings.",
        "Each group references equipment algorithms, enchantment groups, and equipment pools."
    })
    @Order(0)
    public ArrayList<MobGroup> groups = Stream.of(
        createZombies(),
        createZombiePigmen(),
        createVindicators(),
        createVexes(),
        createSkeletons(),
        createWitherSkeletons()
    ).collect(Collectors.toCollection(ArrayList::new));

    /**
     * zombies, zombie villagers, husks
     */
    private static MobGroup createZombies() {
        MobGroup group = new MobGroup("Zombies");

        // Entities in this group
        group.entities = Stream.of(
            "minecraft:zombie",
            "minecraft:zombie_villager",
            "minecraft:husk"
        ).collect(Collectors.toCollection(ArrayList::new));

        // Use vanilla equipment algorithm
        // Fully reproduces vanilla, so the original code doesn't need to run
        group.allowOriginalBehavior = false;
        group.equipmentAlgorithm = "Vanilla Standard";

        // Vanilla armor: all tiers available
        group.equipmentPools.armorSets = Stream.of(
            "Leather Armor",
            "Gold Armor",
            "Chainmail Armor",
            "Iron Armor",
            "Diamond Armor"
        ).collect(Collectors.toCollection(ArrayList::new));

        // Vanilla weapons: only iron shovel and iron sword (2:1 ratio handled by weights in sets)
        group.equipmentPools.mainhandSets = Stream.of(
            "Iron Shovel",
            "Iron Sword"
        ).collect(Collectors.toCollection(ArrayList::new));

        return group;
    }

    /**
     * Zombie Pigmen - vanilla: golden sword only, NO armor
     */
    private static MobGroup createZombiePigmen() {
        MobGroup group = new MobGroup("Zombie Pigmen");

        group.entities = Stream.of(
            "minecraft:zombie_pigman"
        ).collect(Collectors.toCollection(ArrayList::new));

        // Fully reproduces vanilla, so the original code doesn't need to run
        group.allowOriginalBehavior = false;
        group.equipmentAlgorithm = "Vanilla Guaranteed Weapon";

        // Vanilla: NO armor (overrides parent's armor system)
        group.equipmentPools.armorSets = new ArrayList<>();

        // Vanilla: golden sword only
        group.equipmentPools.mainhandSets = Stream.of(
            "Golden Sword"
        ).collect(Collectors.toCollection(ArrayList::new));

        return group;
    }

    /**
     * Vindicators - vanilla: iron axe only, NO armor
     */
    private static MobGroup createVindicators() {
        MobGroup group = new MobGroup("Vindicators");

        group.entities = Stream.of(
            "minecraft:vindication_illager"
        ).collect(Collectors.toCollection(ArrayList::new));

        // Fully reproduces vanilla, so the original code doesn't need to run
        group.allowOriginalBehavior = false;
        group.equipmentAlgorithm = "Vanilla Guaranteed Weapon";

        // Vanilla: NO armor
        group.equipmentPools.armorSets = new ArrayList<>();

        // Vanilla: iron axe only
        group.equipmentPools.mainhandSets = Stream.of(
            "Iron Axe"
        ).collect(Collectors.toCollection(ArrayList::new));

        return group;
    }

    /**
     * Vexes - vanilla: iron sword only (0% drop chance), NO armor
     */
    private static MobGroup createVexes() {
        MobGroup group = new MobGroup("Vexes");

        group.entities = Stream.of(
            "minecraft:vex"
        ).collect(Collectors.toCollection(ArrayList::new));

        // Fully reproduces vanilla, so the original code doesn't need to run
        group.allowOriginalBehavior = false;
        group.equipmentAlgorithm = "Vanilla Guaranteed Weapon";

        // Vanilla: NO armor
        group.equipmentPools.armorSets = new ArrayList<>();

        // Vanilla: iron sword only (with 0% drop chance)
        group.equipmentPools.mainhandSets = Stream.of(
            "Iron Sword (Vex)"
        ).collect(Collectors.toCollection(ArrayList::new));

        return group;
    }

    /**
     * Skeletons & Strays
     */
    private static MobGroup createSkeletons() {
        MobGroup group = new MobGroup("Skeletons");

        group.entities = Stream.of(
            "minecraft:skeleton",
            "minecraft:stray"
        ).collect(Collectors.toCollection(ArrayList::new));

        // Fully reproduces vanilla, so the original code doesn't need to run
        group.allowOriginalBehavior = false;
        group.equipmentAlgorithm = "Vanilla Guaranteed Weapon";

        // Vanilla: AbstractSkeleton calls the EntityLiving armor rolling
        group.equipmentPools.armorSets = Stream.of(
            "Leather Armor",
            "Gold Armor",
            "Chainmail Armor",
            "Iron Armor",
            "Diamond Armor"
        ).collect(Collectors.toCollection(ArrayList::new));

        // Vanilla: bow only
        group.equipmentPools.mainhandSets = Stream.of(
            "Bow"
        ).collect(Collectors.toCollection(ArrayList::new));

        return group;
    }

    /**
     * Wither Skeletons - vanilla: stone sword only, NO armor
     */
    private static MobGroup createWitherSkeletons() {
        MobGroup group = new MobGroup("Wither Skeletons");

        group.entities = Stream.of(
            "minecraft:wither_skeleton"
        ).collect(Collectors.toCollection(ArrayList::new));

        // Fully reproduces vanilla, so the original code doesn't need to run
        group.allowOriginalBehavior = false;
        group.equipmentAlgorithm = "Vanilla Guaranteed Weapon";

        // Vanilla: wither skeletons override setEnchantmentBasedOnDifficulty to never enchant
        group.enchantments = new MobGroup.EnchantmentSettings("", "", "");

        // Vanilla: NO armor (overrides parent's armor system)
        group.equipmentPools.armorSets = new ArrayList<>();

        // Vanilla: stone sword only
        group.equipmentPools.mainhandSets = Stream.of(
            "Stone Sword"
        ).collect(Collectors.toCollection(ArrayList::new));

        return group;
    }
}