package mobequipmenttweaker.config;

import meldexun.betterconfig.api.BetterConfig;
import meldexun.betterconfig.api.BetterConfigManager;
import mobequipmenttweaker.Tags;
import mobequipmenttweaker.config.folders.ArmorConfig;
import mobequipmenttweaker.config.folders.MeleeConfig;
import mobequipmenttweaker.config.folders.MixinToggleConfig;
import mobequipmenttweaker.config.folders.RangedConfig;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@BetterConfig(
		modid = Tags.MODID,
		bigCategoryComments = false,
		lowerCaseCategories = false
)
public class ConfigHandler {
	//TODO: CT event support
	//TODO: add mobs to this (zombie pigs, oe drowned, defiled hosts?...)

	@Config.Comment("Mixin Toggles!")
	@Config.Name("Mixin Toggles")
	public static final MixinToggleConfig mixintoggles = new MixinToggleConfig();

	@Config.Comment("TODO")
	@Config.Name("Armor")
	public static final ArmorConfig armor = new ArmorConfig();

	@Config.Comment("TODO")
	@Config.Name("Melee")
	public static final MeleeConfig melee = new MeleeConfig();

	@Config.Comment("TODO")
	@Config.Name("Ranged")
	public static final RangedConfig ranged = new RangedConfig();

	@Mod.EventBusSubscriber(modid = Tags.MODID)
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(Tags.MODID)) {
				BetterConfigManager.sync(Tags.MODID);
				ConfigProvider.reset();
			}
		}
	}
}