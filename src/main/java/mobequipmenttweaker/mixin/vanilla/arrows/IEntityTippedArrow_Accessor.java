package mobequipmenttweaker.mixin.vanilla.arrows;

import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.potion.PotionEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(EntityTippedArrow.class)
public interface IEntityTippedArrow_Accessor {

    @Mutable
    @Accessor("customPotionEffects")
    Set<PotionEffect> met$getCustomPotionEffects();
}
