package mobequipmenttweaker.mixin.vanilla.arrows;

import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.projectile.EntityArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractSkeleton.class)
public interface IAbstractSkeleton_Invoker {

    @Invoker("getArrow")
    EntityArrow met$invokeGetArrow(float distanceFactor);
}
