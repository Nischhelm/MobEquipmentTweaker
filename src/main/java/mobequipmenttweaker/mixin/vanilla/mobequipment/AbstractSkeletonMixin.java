package mobequipmenttweaker.mixin.vanilla.mobequipment;

import mobequipmenttweaker.config.ConfigProvider;
import mobequipmenttweaker.config.data.SetEntry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonMixin extends EntityLiving {
    public AbstractSkeletonMixin(World worldIn) {
        super(worldIn);
    }

    @ModifyArg(
            method = "setEquipmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/Item;)V")
    )
    private Item mobequipmenttweaker_vanillaAbstractSkeleton_setEquipmentBasedOnDifficulty(Item itemIn){
        //TODO: roll tier
        SetEntry entry = ConfigProvider.getRandomWeapon(this.getRNG(), 0, false);
        this.setDropChance(EntityEquipmentSlot.MAINHAND, entry.dropChance);
        return entry.getItem(EntityEquipmentSlot.MAINHAND);
    }
}
