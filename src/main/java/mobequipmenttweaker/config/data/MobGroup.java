package mobequipmenttweaker.config.data;

import net.minecraftforge.common.config.Config;
import meldexun.betterconfig.api.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a group of mobs with shared equipment and enchantment settings
 */
public class MobGroup {

    @Config.Name("ID")
    @Config.Comment("Unique identifier for this mob group")
    @Order(0)
    public String id = "";

    @Config.Name("Entities")
    @Config.Comment("List of entity IDs that belong to this group (e.g., minecraft:zombie, minecraft:husk)")
    @Order(1)
    public List<String> entities = new ArrayList<>();

    @Config.Name("Allow Original Behavior")
    @Config.Comment({
        "If true, the mob first equips and enchants itself like it normally would in setEquipmentBasedOnDifficulty and setEnchantmentBasedOnDifficulty.",
        "Afterwards this group rolls on top, and every item it rolls replaces whatever the mob had in that slot. Slots where this group rolls nothing keep the original item.",
        "Items rolled by this group are only enchanted by its enchantment groups, never additionally by the original enchanting.",
        "Note that chances stack this way: a vanilla zombie with this enabled would roll vanilla armor and then this group's armor.",
        "Equipment that a mob sets directly in onInitialSpawn can't be prevented, this group can only roll over it.",
        "Mobs that never call setEquipmentBasedOnDifficulty get this group rolled at the end of onInitialSpawn."
    })
    @Order(2)
    public boolean allowOriginalBehavior = true;

    @Config.Name("Equipment Algorithm")
    @Config.Comment("Reference to an EquipmentAlgorithm ID that determines how equipment is rolled")
    @Order(3)
    public String equipmentAlgorithm = "Vanilla Standard";

    @Config.Name("Enchantments")
    @Order(4)
    public EnchantmentSettings enchantments = new EnchantmentSettings();

    @Config.Name("Equipment Pools")
    @Config.Comment("IDs of equipment sets available to this mob group")
    @Order(5)
    public EquipmentPools equipmentPools = new EquipmentPools();

    public MobGroup() {}

    public MobGroup(String id) {
        this.id = id;
    }

    public static class EnchantmentSettings {
        @Config.Name("Armor Enchantment Group")
        @Config.Comment("Reference to an EnchantmentGroup ID for armor. Leave empty for no enchantments.")
        @Order(0)
        public String armorEnchantGroup = "Standard Armor Enchants";

        @Config.Name("Mainhand Enchantment Group")
        @Config.Comment("Reference to an EnchantmentGroup ID for mainhand weapons. Leave empty for no enchantments.")
        @Order(1)
        public String mainhandEnchantGroup = "Standard Weapon Enchants";

        @Config.Name("Offhand Enchantment Group")
        @Config.Comment("Reference to an EnchantmentGroup ID for offhand items. Leave empty for no enchantments.")
        @Order(2)
        public String offhandEnchantGroup = "Standard Weapon Enchants";

        public EnchantmentSettings() {}

        public EnchantmentSettings(String armorEnchantGroup, String mainhandEnchantGroup, String offhandEnchantGroup) {
            this.armorEnchantGroup = armorEnchantGroup;
            this.mainhandEnchantGroup = mainhandEnchantGroup;
            this.offhandEnchantGroup = offhandEnchantGroup;
        }
    }

    public static class EquipmentPools {
        @Config.Name("Armor Sets")
        @Config.Comment("List of ArmorSet IDs available to this mob group")
        @Order(0)
        public ArrayList<String> armorSets = new ArrayList<>();

        @Config.Name("Mainhand Sets")
        @Config.Comment("List of MainhandSet IDs available to this mob group")
        @Order(1)
        public ArrayList<String> mainhandSets = new ArrayList<>();

        @Config.Name("Offhand Sets")
        @Config.Comment("List of OffhandSet IDs available to this mob group")
        @Order(2)
        public ArrayList<String> offhandSets = new ArrayList<>();

        public EquipmentPools() {}

        public EquipmentPools(ArrayList<String> armorSets, ArrayList<String> mainhandSets, ArrayList<String> offhandSets) {
            this.armorSets = armorSets;
            this.mainhandSets = mainhandSets;
            this.offhandSets = offhandSets;
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MobGroup) {
            return ((MobGroup) obj).id.equals(this.id);
        }
        return false;
    }
}