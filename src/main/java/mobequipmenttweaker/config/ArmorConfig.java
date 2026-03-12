package mobequipmenttweaker.config;

import mobequipmenttweaker.config.data.ArmorSetEntry;
import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArmorConfig {
    @Config.Comment({
            "Applied at least to zombies and skeletons but also to various other mobs extending from them",
            "Pattern: mod_id, helmet_id, chestplate_id, leggings_id, boot_id, tier, optional dropChance (default vanilla 0.085)",
            "Leave slots empty if the armor set doesn't have a piece for that slot. Example: somemod, , onlychest, , , 1, 1",
            "If one of the set pieces are from a different mod, just name the armor piece as modid:itemid",
            "Default: 0: Leather Tier, 1: Gold Tier, 2: Chainmail Tier, 3: Iron Tier, 4: Diamond Tier, >4 custom to be defined if max tier is increased"
    })
    @Config.Name("Armor Sets")
    public ArrayList<ArmorSetEntry> armorSets = (ArrayList<ArmorSetEntry>) Stream.of(
            new ArmorSetEntry(Arrays.asList("leather_helmet", "leather_chestplate", "leather_leggings", "leather_boots"), "minecraft", 0).setName("leather"),
            new ArmorSetEntry(Arrays.asList("golden_helmet", "golden_chestplate", "golden_leggings", "golden_boots"), "minecraft", 1).setName("gold"),
            new ArmorSetEntry(Arrays.asList("chainmail_helmet", "chainmail_chestplate", "chainmail_leggings", "chainmail_boots"), "minecraft", 2).setName("chain"),
            new ArmorSetEntry(Arrays.asList("iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots"), "minecraft", 3).setName("iron"),
            new ArmorSetEntry(Arrays.asList("diamond_helmet", "diamond_chestplate", "diamond_leggings", "diamond_boots"), "minecraft", 4).setName("diamond")
    ).map(e -> (ArmorSetEntry) e).collect(Collectors.toList());

    @Config.Comment("Base chance for mobs getting armor. This is multiplied by the local difficulty ratio (0 to 1), so the given value is only reached once a chunk is inhabited for a long time. Default: 15%")
    @Config.Name("Armor Base Chance")
    @Config.RangeDouble(min = 0)
    public float baseArmorChance = 0.15F;

    @Config.Comment("Starts with a 50/50 chance either at tier 0 or tier 1, then continues to roll with this given chance to increase tier until max tier. Default: roll with 9.5% chance for each additional tier increase")
    @Config.Name("Armor Tier Increase Chance")
    @Config.RangeDouble(min = 0, max = 1)
    public float armorTierIncreaseChance = 0.095F;

    @Config.Comment("Max tier of mob equipment armor. Each tier is exponentially less likely (see \"Armor Tier Increase Chance\"). If you increase this value you want to provide at least one armor set for each additional tier.")
    @Config.Name("Armor Max Tier")
    @Config.RangeInt(min = 1)
    public int armorMaxTier = 4;

    @Config.Comment("This system is a bit hard to comprehend. Once vanilla decided to give a mob at least one armor piece, for each possible additional armor piece on that mob it will roll with a chance of either 90% (hard mode) or 75% (any other difficulty) to add more armor pieces. \n" +
            "The given multiplier here will be multiplied on the 90% or 75%, so a number bigger than 1 will increase the chance for mobs having more than one armor piece, while a number below 1 will reduce it.")
    @Config.Name("Additional Armor Piece Chance Multi")
    @Config.RangeDouble(min = 0, max = 1.34F)
    public float additionalArmorChanceMulti = 1.0F;

    @Config.Comment("Enchantments for Armor")
    @Config.Name("Enchantments")
    public EnchantConfig enchants = new EnchantConfig(5, 22, false, 0.5F);
}
