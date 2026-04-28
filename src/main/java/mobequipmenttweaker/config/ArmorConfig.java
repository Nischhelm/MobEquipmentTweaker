package mobequipmenttweaker.config;

import meldexun.betterconfig.api.Order;
import mobequipmenttweaker.config.data.ArmorSetEntry;
import mobequipmenttweaker.config.data.TierEntry;
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
    @Config.Name("Armor Set Tiers")
    @Order(5)
    public ArrayList<TierEntry> armorSetTiers = (ArrayList<TierEntry>) Stream.of( //Fully correct weights would be 5929741, 7797118, 2063400, 202882, 6859 but these numbers are only less than 2% off of their true weight
            new TierEntry(8645).addSet(new ArmorSetEntry(Arrays.asList("leather_helmet", "leather_chestplate", "leather_leggings", "leather_boots"), "minecraft").setName("leather")),
            new TierEntry(11368).addSet(new ArmorSetEntry(Arrays.asList("golden_helmet", "golden_chestplate", "golden_leggings", "golden_boots"), "minecraft").setName("gold")),
            new TierEntry(3008).addSet(new ArmorSetEntry(Arrays.asList("chainmail_helmet", "chainmail_chestplate", "chainmail_leggings", "chainmail_boots"), "minecraft").setName("chain")),
            new TierEntry(296).addSet(new ArmorSetEntry(Arrays.asList("iron_helmet", "iron_chestplate", "iron_leggings", "iron_boots"), "minecraft").setName("iron")),
            new TierEntry(10).addSet(new ArmorSetEntry(Arrays.asList("diamond_helmet", "diamond_chestplate", "diamond_leggings", "diamond_boots"), "minecraft").setName("diamond"))
    ).collect(Collectors.toList());

    @Config.Comment("Base chance for mobs getting armor. This is multiplied by the local difficulty ratio (0 to 1), so the given value is only reached once a chunk is inhabited for a long time. Default: 15%")
    @Config.Name("Armor Base Chance")
    @Config.RangeDouble(min = 0)
    @Order(0)
    public float baseArmorChance = 0.15F;

    @Config.Comment("This system is a bit hard to comprehend. Once vanilla decided to give a mob at least one armor piece, for each possible additional armor piece on that mob it will roll with a chance of either 90% (hard mode) or 75% (any other difficulty) to add more armor pieces. \n" +
            "The given multiplier here will be multiplied on the 90% or 75%, so a number bigger than 1 will increase the chance for mobs having more than one armor piece, while a number below 1 will reduce it.")
    @Config.Name("Additional Armor Piece Chance Multi")
    @Config.RangeDouble(min = 0, max = 1.34F)
    @Order(3)
    public float additionalArmorChanceMulti = 1.0F;

    @Config.Comment("Enchantments for Armor")
    @Config.Name("Enchantments")
    @Order(4)
    public EnchantConfig enchants = new EnchantConfig(5, 22, false, 0.5F);
}
