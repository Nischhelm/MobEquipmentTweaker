package mobequipmenttweaker.config.folders;

import fermiumbooter.annotations.MixinConfig;
import mobequipmenttweaker.Tags;
import net.minecraftforge.common.config.Config;

@MixinConfig(name = Tags.MODID)
@SuppressWarnings("unused")
public class MixinToggleConfig {

    @Config.Comment("Allows to modify mob equipment (mainly zombies and skeletons) set in the \"Mob Equipment\" config.")
    @Config.Name("Mob Equipment Modification (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.mobequipmenttweaker.vanilla.mobequipment.json", defaultValue = true)
    public boolean mobEquipmentModify = true;

    @Config.Comment("Allows skeletons to use Spartan Weaponry and benefit from weapon stats. Options in the \"Mob Equipment\" config. If \"Modded Arrow Skeletons\" is disabled, skeletons using crossbows will always use normal bolts, and skeletons using longbows will use the same arrow behaviors like when they have a normal bow.")
    @Config.Name("Spartan Skeletons (Vanilla/SpartanWeaponry)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(
            earlyMixin = "mixins.mobequipmenttweaker.vanilla.rangedaiweapons.json",
            lateMixin = "mixins.mobequipmenttweaker.spartanweaponry.rangedaiweapons.json",
            defaultValue = true
    )
    @MixinConfig.CompatHandling(modid = "spartanweaponry", desired = true, reason = "Requires mod to properly function")
    public boolean enableSpartanRangedSkeletons = true;

    @Config.Comment("Allows all AbstractSkeletons (stray, wither skellie, FUR forsaken etc) to use offhand special arrows like tipped arrows, spectral arrows etc. This is also needed for the \"Tipped Arrows\" section to work.")
    @Config.Name("Modded Arrow Skeletons (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.mobequipmenttweaker.vanilla.skeletonarrows.json", defaultValue = true)
    public boolean enabledModdedArrowsForAll = true;

    @Config.Comment("Mobs holding any bow or crossbow in mainhand will pickup tipped arrows or tipped bolts if they can pickup items at all. Only tipped arrows/bolts with potion types defined in the tipped arrow config are viable to pickup.")
    @Config.Name("Mobs Pickup Tipped Arrows (Vanilla)")
    @Config.RequiresMcRestart
    @MixinConfig.MixinToggle(earlyMixin = "mixins.mobequipmenttweaker.vanilla.pickuptippedarrows.json", defaultValue = true)
    public boolean mobsPickupArrows = true;
}
