package mobequipmenttweaker.config.data;

import net.minecraftforge.common.config.Config;
import meldexun.betterconfig.api.Order;

/**
 * Reusable enchantment settings that can be applied to different equipment types
 */
public class EnchantmentGroup {

    @Config.Name("ID")
    @Config.Comment("Unique identifier for this enchantment group")
    @Order(0)
    public String id = "";

    @Config.Name("Enchant Chance")
    @Config.Comment("Chance for items to be enchanted. Scaled with local difficulty if enabled.")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Order(1)
    public float enchantChance = 0.5F;

    @Config.Name("Min Enchantability")
    @Config.Comment("Minimum enchantability level")
    @Config.RangeInt(min = 1)
    @Order(2)
    public int minEnchantability = 5;

    @Config.Name("Max Enchantability")
    @Config.Comment("Maximum enchantability level. Only reached if local difficulty is 1 (area inhabited for a while).")
    @Config.RangeInt(min = 1)
    @Order(3)
    public int maxEnchantability = 22;

    @Config.Name("Allow Treasure Enchantments")
    @Config.Comment("Allow treasure enchantments (Mending, Frost Walker, etc.)")
    @Order(4)
    public boolean allowTreasure = false;

    @Config.Name("Difficulty Scaling")
    @Config.Comment("Scale enchant chance and enchantability with local difficulty")
    @Order(5)
    public boolean difficultyScaling = true;

    public EnchantmentGroup() {}

    public EnchantmentGroup(String id) {
        this.id = id;
    }

    public EnchantmentGroup(String id, float enchantChance, int minEnchantability, int maxEnchantability, boolean allowTreasure, boolean difficultyScaling) {
        this.id = id;
        this.enchantChance = enchantChance;
        this.minEnchantability = minEnchantability;
        this.maxEnchantability = maxEnchantability;
        this.allowTreasure = allowTreasure;
        this.difficultyScaling = difficultyScaling;
    }
}