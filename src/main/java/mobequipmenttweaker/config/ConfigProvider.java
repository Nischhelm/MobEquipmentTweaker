package mobequipmenttweaker.config;

import mobequipmenttweaker.config.data.SetEntry;
import net.minecraft.util.WeightedRandom;

import java.util.Random;
import java.util.stream.Collectors;

public class ConfigProvider {
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
