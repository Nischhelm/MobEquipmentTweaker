package mobequipmenttweaker.mixin.spartanweaponry;

import com.oblivioussp.spartanweaponry.item.ItemThrowingWeapon;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemThrowingWeapon.class)
public interface IItemThrowingWeapon_Accessor {

    @Accessor(value = "throwVelocity", remap = false)
    float met$accessorThrowVelocity();
}
