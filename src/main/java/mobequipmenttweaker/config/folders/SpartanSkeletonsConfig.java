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
}
