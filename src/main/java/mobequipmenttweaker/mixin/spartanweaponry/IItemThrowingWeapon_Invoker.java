package mobequipmenttweaker.mixin.spartanweaponry;

import com.oblivioussp.spartanweaponry.item.ItemThrowingWeapon;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemThrowingWeapon.class)
public interface IItemThrowingWeapon_Invoker {

    @Invoker(value = "initNBT", remap = false)
    void met$inokerInitNBT(ItemStack stack, boolean initUUID);
}
