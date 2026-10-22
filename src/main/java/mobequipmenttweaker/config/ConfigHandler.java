package mobequipmenttweaker.config;

import meldexun.betterconfig.api.BetterConfig;
import meldexun.betterconfig.api.BetterConfigManager;
import mobequipmenttweaker.Tags;
import mobequipmenttweaker.config.folders.*;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@BetterConfig(
		modid = Tags.MODID,
		bigCategoryComments = false,
		lowerCaseCategories = false,
		version = Tags.CFG_VERSION
)
public class ConfigHandler {
	//TODO: CT event support

	@Config.Comment("Mixin Toggles!")
	@Config.Name("Mixin Toggles")
	public static final MixinToggleConfig mixintoggles = new MixinToggleConfig();

	@Config.Comment("Mob Groups decide which mobs get equipment, which algorithm and enchantment groups they use and which equipment sets they can roll from.")
	@Config.Name("Mob Groups")
	public static final MobGroupsConfig mobGroups = new MobGroupsConfig();

	@Config.Comment("Equipment Algorithms define spawn chances and tier rolling.")
	@Config.Name("Equipment Algorithms")
	public static final EquipmentAlgorithmsConfig algorithms = new EquipmentAlgorithmsConfig();

	@Config.Comment("Equipment Sets define the armor, mainhand and offhand items mobs can roll.")
	@Config.Name("Equipment Sets")
	public static final EquipmentSetsConfig equipmentSets = new EquipmentSetsConfig();

	@Config.Comment("Enchantment Groups define how equipment gets enchanted.")
	@Config.Name("Enchantment Groups")
	public static final EnchantmentGroupsConfig enchantmentGroups = new EnchantmentGroupsConfig();

	@Config.Comment("Tipped Arrow Options")
	@Config.Name("Tipped Arrows")
	public static final TippedArrowConfig tippedArrows = new TippedArrowConfig();

	@Config.Comment("Settings for skeletons using Spartan Crossbows and Longbows")
	@Config.Name("Spartan Skeletons")
	public static final SpartanSkeletonsConfig spartanSkeletons = new SpartanSkeletonsConfig();

	@Mod.EventBusSubscriber
	private static class EventHandler {
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(Tags.MODID)) {
				BetterConfigManager.sync(Tags.MODID);
				ConfigProvider.reset();
				tippedArrows.reset();
			}
		}
	}
}
