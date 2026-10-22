package mobequipmenttweaker.config.folders;

import mobequipmenttweaker.config.data.ArmorSet;
import mobequipmenttweaker.config.data.MainhandSet;
import mobequipmenttweaker.config.data.OffhandSet;
import net.minecraftforge.common.config.Config;
import meldexun.betterconfig.api.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configuration for all equipment sets (armor, mainhand, offhand)
 */
public class EquipmentSetsConfig {

    @Config.Name("Armor Sets")
    @Config.Comment({
        "All available armor sets.",
        "Reference these by ID in mob groups."
    })
    @Order(0)
    public ArrayList<ArmorSet> armorSets = Stream.of(
            createDefaultArmorSet("Leather Armor", 0, "leather"),
            createDefaultArmorSet("Gold Armor", 1, "golden"),
            createDefaultArmorSet("Chainmail Armor", 2, "chainmail"),
            createDefaultArmorSet("Iron Armor", 3, "iron"),
            createDefaultArmorSet("Diamond Armor", 4, "diamond")
    ).collect(Collectors.toCollection(ArrayList::new));

    @Config.Name("Mainhand Sets")
    @Config.Comment({
        "All available mainhand weapon sets.",
        "Reference these by ID in mob groups and tie systems."
    })
    @Order(1)
    public ArrayList<MainhandSet> mainhandSets = Stream.of(
            new MainhandSet("Stone Sword", 0, 1, "minecraft", "stone_sword"),
            new MainhandSet("Golden Sword", 0, 1, "minecraft", "golden_sword"),
            new MainhandSet("Iron Shovel", 0, 2, "minecraft", "iron_shovel"),
            new MainhandSet("Iron Sword", 0, 1, "minecraft", "iron_sword"),
            createIronSwordVex(),
            new MainhandSet("Iron Axe", 0, 1, "minecraft", "iron_axe"),
            new MainhandSet("Bow", 0, 1, "minecraft", "bow")
    ).collect(Collectors.toCollection(ArrayList::new));

    @Config.Name("Offhand Sets")
    @Config.Comment({
        "All available offhand item sets.",
        "Reference these by ID in mob groups and tie systems."
    })
    @Order(2)
    public ArrayList<OffhandSet> offhandSets = Stream.of(
            new OffhandSet("Shield", 0, 100, "minecraft", "shield"),
            new OffhandSet("Totem of Undying", 4, 10, "minecraft", "totem_of_undying")
    ).collect(Collectors.toCollection(ArrayList::new));

    private static ArmorSet createDefaultArmorSet(String id, int tier, String material) {
        ArmorSet set = new ArmorSet(id, tier, 1, "minecraft");
        LinkedHashMap<ArmorSet.ArmorSlot, String> pieces = new LinkedHashMap<>();
        pieces.put(ArmorSet.ArmorSlot.HEAD, material+"_helmet");
        pieces.put(ArmorSet.ArmorSlot.CHEST, material+"_chestplate");
        pieces.put(ArmorSet.ArmorSlot.LEGS, material+"_leggings");
        pieces.put(ArmorSet.ArmorSlot.FEET, material+"_boots");
        set.armorPieces = pieces;
        return set;
    }

    private static MainhandSet createIronSwordVex() {
        // Vanilla vexes: iron sword with 0% drop chance (never drops)
        MainhandSet set = new MainhandSet("Iron Sword (Vex)", 0, 1, "minecraft", "iron_sword");
        set.dropChance = 0.0F;
        return set;
    }
}