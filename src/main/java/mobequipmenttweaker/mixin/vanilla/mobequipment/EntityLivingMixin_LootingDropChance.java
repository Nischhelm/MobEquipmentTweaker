package mobequipmenttweaker.mixin.vanilla.mobequipment;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import mobequipmenttweaker.handlers.MobEquipHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(EntityLiving.class)
public abstract class EntityLivingMixin_LootingDropChance extends EntityLivingBase {
    public EntityLivingMixin_LootingDropChance(World worldIn) {
        super(worldIn);
    }

    @ModifyExpressionValue(
            method = "dropEquipment",
            at = @At(value = "CONSTANT", args = "floatValue=0.01F")
    )
    private float mobequipmenttweaker_vanillaEntityLiving_dropEquipment_lootingDropChance(float original, @Local EntityEquipmentSlot slot){
        return MobEquipHandler.getLootingDropChance((EntityLiving) (Object) this, slot, original);
    }
}
