package mobequipmenttweaker.config;

import mobequipmenttweaker.config.data.HandsSetEntry;
import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RangedConfig {
    @Config.Comment("List of mobs getting ranged equipment. Note: Just adding mobs here will not magically give them the ability to use a ranged weapon")
    @Config.Name("Affected Entities")
    public ArrayList<String> entityIds = (ArrayList<String>) Stream.of(
            "minecraft:skeleton",
            "minecraft:stray",
            "minecraft:wither_skeleton",
            "mod_lavacow:forsaken"
    ).collect(Collectors.toList());

    @Config.Comment("Tipped Arrow Options")
    @Config.Name("Tipped Arrow Options")
    public TippedArrowConfig tippedarrows = new TippedArrowConfig();

    @Config.Comment("Pattern: modid:itemid, weight, optional dropChance (default vanilla 0.085)")
    @Config.Name("Ranged Items")
    public ArrayList<HandsSetEntry> weaponSets = (ArrayList<HandsSetEntry>) Stream.of(
            new HandsSetEntry(Arrays.asList("bow", ""), "minecraft", 0, 1).setName("bow")
    ).map(e -> (HandsSetEntry) e).collect(Collectors.toList());

    @Config.Comment("Enchantments for Ranged Weapons")
    @Config.Name("Enchantments")
    public EnchantConfig enchants = new EnchantConfig(5, 22, false, 0.25F);;

    @Config.Comment("Settings for skeletons using Spartan Crossbows and Longbows")
    @Config.Name("Spartan Skeletons")
    public SpartanSkeletonsConfig spartanSkeletons = new SpartanSkeletonsConfig();

}
