package mobequipmenttweaker.config.folders;

import mobequipmenttweaker.config.data.EnchantmentGroup;
import net.minecraftforge.common.config.Config;
import meldexun.betterconfig.api.Order;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configuration for reusable enchantment groups
 */
public class EnchantmentGroupsConfig {

    @Config.Name("Enchantment Groups")
    @Config.Comment({
        "Reusable enchantment settings that can be applied to different equipment types.",
        "Reference these by ID in mob groups."
    })
    @Order(0)
    public ArrayList<EnchantmentGroup> groups = Stream.of(
            new EnchantmentGroup("Standard Armor Enchants", 0.5F,5,22, false, true),
            new EnchantmentGroup("Standard Weapon Enchants", 0.25F, 5, 22, false, true)
    ).collect(Collectors.toCollection(ArrayList::new));
}