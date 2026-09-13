package mobequipmenttweaker.config;

import com.google.common.collect.ArrayListMultimap;
import mobequipmenttweaker.config.data.ArmorSetEntry;
import mobequipmenttweaker.config.data.HandsSetEntry;
import mobequipmenttweaker.config.data.SetEntry;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.config.Config;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ConfigProvider {
    @Config.Ignore
    public static List<HandsSetEntry> meleeWeapons = null;
    @Config.Ignore
    public static List<HandsSetEntry> rangedWeapons = null;
    @Config.Ignore
    public static ArrayListMultimap<Integer, ArmorSetEntry> armorByTier = null;

    public static void reset() {
        meleeWeapons = null;
        rangedWeapons = null;
        armorByTier = null;
    }

    public static SetEntry getRandomWeapon(Random rand, int tier, boolean forZombie) {
        if (forZombie) return WeightedRandom.getRandomItem(rand, ConfigHandler.melee.weaponSetTiers.get(tier).sets);
        else return WeightedRandom.getRandomItem(rand, ConfigHandler.ranged.weaponSetTiers.get(tier).sets);
    }

    public static SetEntry getRandomArmor(Random rand, int tier, boolean allowUndroppables) {
        if (allowUndroppables)
            return WeightedRandom.getRandomItem(rand, ConfigHandler.armor.armorSetTiers.get(tier).sets);
        else
            return WeightedRandom.getRandomItem(rand, ConfigHandler.armor.armorSetTiers.get(tier).sets.stream().filter(set -> set.dropChance > 0).collect(Collectors.toList()));
    }
}
