package mobequipmenttweaker.config;

import mobequipmenttweaker.config.data.ArmorSetEntry;
import mobequipmenttweaker.config.data.HandsSetEntry;
import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MeleeConfig {
    @Config.Comment("List of mobs getting melee equipment.")
    @Config.Name("Affected Entities")
    public ArrayList<String> entityIds = (ArrayList<String>) Stream.of(
            "minecraft:zombie",
            "minecraft:zombie_villager",
            "minecraft:husk",
            "minecraft:zombie_pigman"
    ).collect(Collectors.toList());

    @Config.Comment("Pattern: modid:itemid, weight, optional dropChance (default vanilla 0.085)")
    @Config.Name("Melee Items")
    public ArrayList<HandsSetEntry> weaponSets = (ArrayList<HandsSetEntry>) Stream.of(
            new HandsSetEntry(Arrays.asList("iron_shovel", ""), "minecraft", 0, 2).setName("shovel"),
            new HandsSetEntry(Arrays.asList("iron_sword", ""), "minecraft", 0, 1).setName("sword")
    ).map(e -> (HandsSetEntry) e).collect(Collectors.toList());

    @Config.Comment("Base chance multiplier for zombie types getting weapons. By default 5% in hard mode, 1% in all other difficulties. The given multiplier here will be multiplied on top of those.")
    @Config.Name("Zombie Weapon Base Chance Multi")
    @Config.RangeDouble(min = Float.MIN_VALUE, max = 100)
    public float baseZombieChanceMulti = 1F;

    @Config.Comment("Enchantments for Melee Weapons")
    @Config.Name("Enchantments")
    public EnchantConfig enchants = new EnchantConfig(5, 22, false, 0.25F);
}
