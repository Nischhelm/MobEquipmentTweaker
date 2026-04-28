package mobequipmenttweaker.config;

import com.google.common.collect.ArrayListMultimap;
import meldexun.betterconfig.api.BetterConfig;
import meldexun.betterconfig.api.BetterConfigManager;
import mobequipmenttweaker.config.data.ArmorSetEntry;
import mobequipmenttweaker.config.data.HandsSetEntry;
import mobequipmenttweaker.config.data.SetEntry;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import mobequipmenttweaker.MobEquipmentTweaker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@BetterConfig(modid = MobEquipmentTweaker.MODID, bigCategoryComments = false, lowerCaseCategories = false)
public class ConfigHandler {
	//TODO: CT event support
	//TODO: add mobs to this (zombie pigs, oe drowned, defiled hosts?...)

	@Config.Comment("Mixin Toggles!")
	@Config.Name("Mixin Toggles")
	@SuppressWarnings("unused")
	public static final MixinToggleConfig mixintoggles = new MixinToggleConfig();

	@Config.Comment("TODO")
	@Config.Name("Armor")
	@SuppressWarnings("unused")
	public static final ArmorConfig armor = new ArmorConfig();

	@Config.Comment("TODO")
	@Config.Name("Melee")
	@SuppressWarnings("unused")
	public static final MeleeConfig melee = new MeleeConfig();

	@Config.Comment("TODO")
	@Config.Name("Ranged")
	@SuppressWarnings("unused")
	public static final RangedConfig ranged = new RangedConfig();

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
		if(forZombie) return WeightedRandom.getRandomItem(rand, melee.weaponSetTiers.get(tier).sets);
		else return WeightedRandom.getRandomItem(rand, ranged.weaponSetTiers.get(tier).sets);
	}

	public static SetEntry getRandomArmor(Random rand, int tier, boolean allowUndroppables) {
		if(allowUndroppables)
			return WeightedRandom.getRandomItem(rand, armor.armorSetTiers.get(tier).sets);
		else
			return WeightedRandom.getRandomItem(rand, armor.armorSetTiers.get(tier).sets.stream().filter(set -> set.dropChance > 0).collect(Collectors.toList()));
	}
	
	@Mod.EventBusSubscriber(modid = MobEquipmentTweaker.MODID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(MobEquipmentTweaker.MODID)) {
				BetterConfigManager.sync(MobEquipmentTweaker.MODID);
				ConfigHandler.reset();
			}
		}
	}
}