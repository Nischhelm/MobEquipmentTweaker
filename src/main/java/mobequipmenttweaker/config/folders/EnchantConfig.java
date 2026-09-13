package mobequipmenttweaker.config.folders;

import net.minecraftforge.common.config.Config;

public class EnchantConfig {
    @Config.Comment("Minimum enchantability enchanted items will have")
    @Config.Name("Min Ench")
    @Config.RangeInt(min = 1)
    public int minEnch = 5;

    @Config.Comment("Maximum enchantability enchanted items will have. This is only reached if the local clamped difficulty is 1, so if the area is inhabited for a while.")
    @Config.Name("Max Ench")
    @Config.RangeInt(min = 1)
    public int maxEnch = 22;

    @Config.Comment("Allow treasure enchantments on enchanted items?")
    @Config.Name("Allow Treasure Enchants")
    public boolean allowTreasure = false;

    @Config.Comment("Chance for items to be enchanted. Scaled with local clamped difficulty, so the given value will only be reached if the area is inhabited for a while.")
    @Config.Name("Enchanted Chance")
    public float chanceEnchant = 0.25F;

    public EnchantConfig() {}
    public EnchantConfig(int min, int max, boolean allowTreasure, float chance) {
        this.minEnch = min;
        this.maxEnch = max;
        this.allowTreasure = allowTreasure;
        this.chanceEnchant = chance;
    }
}
