package mobequipmenttweaker.config;

import com.google.common.collect.ArrayListMultimap;
import meldexun.betterconfig.ConfigurationManager;
import meldexun.betterconfig.api.BetterConfig;
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

@Config(modid = MobEquipmentTweaker.MODID)
@BetterConfig(bigCategoryComments = false, lowerCaseCategories = false)
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

	public static HandsSetEntry getRandomWeapon(Random rand, boolean forZombie) {
		if (forZombie && meleeWeapons == null) {
			meleeWeapons = new ArrayList<>();
			meleeWeapons.addAll(melee.weaponSets.stream().filter(SetEntry::setup).collect(Collectors.toList()));
		} else if (!forZombie && rangedWeapons == null) {
			rangedWeapons = new ArrayList<>();
			rangedWeapons.addAll(ranged.weaponSets.stream().filter(SetEntry::setup).collect(Collectors.toList()));
		}
		return WeightedRandom.getRandomItem(rand, forZombie ? meleeWeapons : rangedWeapons);
	}

	public static ArmorSetEntry getRandomArmor(Random rand, int tier, boolean allowUndroppables) {
		if (armorByTier == null) {
			armorByTier = ArrayListMultimap.create();
			armor.armorSets.stream()
					.filter(SetEntry::setup)
					.forEach((set) -> armorByTier.put(set.tier, set));
		}

		return WeightedRandom.getRandomItem(rand, armorByTier.get(tier).stream().filter(set -> allowUndroppables || set.dropChance > 0).collect(Collectors.toList()));
	}
	
	@Mod.EventBusSubscriber(modid = MobEquipmentTweaker.MODID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(MobEquipmentTweaker.MODID)) {
				ConfigurationManager.sync(MobEquipmentTweaker.MODID, Config.Type.INSTANCE);
				ConfigHandler.reset();
			}
		}
	}
}