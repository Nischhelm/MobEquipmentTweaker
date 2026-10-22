package mobequipmenttweaker.config.folders;

import net.minecraftforge.common.config.Config;

public class SpartanSkeletonsConfig {
    @Config.Comment("Skeletons holding spartan bows/crossbows will have a movement speed penalty when strafing depending on the extra projectile speed they get from their spartan ranged weapon.")
    @Config.Name("Enable Move Speed Penalty")
    public boolean enableMoveSpeedPenalty = true;

    @Config.Comment("Skeletons holding spartan bows/crossbows will gain a (multiplicative/op1) follow range modifier depending on the extra shooting range they get due to their increased projectile speed.")
    @Config.Name("Enable Follow Range Bonus")
    public boolean enableFollowRangeBonus = true;

    @Config.Comment("Skeletons holding spartan bows/crossbows will gain a bonus on the distance in which they will strafe around a target entity based on the extra projectile range they get from their spartan ranged weapon.")
    @Config.Name("Enable AI Strafe Distance Bonus")
    public boolean enableStrafeDistanceBonus = true;

    // TODO Set balanced default as throwing knives are annoying
    @Config.Comment({
            "Throwing Weapon Cooldown Multiplier for Skeletons holding spartan throwing weapons.",
            "The cooldown is based on the player's ticks required to throw the maximum distance.",
            "    (this * max_distance_ticks)",
            "The Vanilla Drowned throws a Trident evey 30 ticks compared to a player being able to throw one every 10 ticks."
    })
    @Config.Name("Throwing Weapon Shot Cooldown Multiplier")
    public float throwingCooldown = 1.5F;

    @Config.Comment({
            "Throwing Weapon Reload Multiplier for Skeletons holding spartan throwing weapons.",
            "The reload is based on the item's maximum ammo capacity and the skeleton's difficulty scaled attack cooldown.",
            "    (this * ammo_capacity * scaled_cooldown)",
            "The Vanilla Drowned throws a Trident evey 30 ticks compared to a player being able to throw one every 10 ticks."
    })
    @Config.Name("Throwing Weapon Full Reload Cooldown Multiplier")
    public float throwingFullReload = 1.5F;
}
