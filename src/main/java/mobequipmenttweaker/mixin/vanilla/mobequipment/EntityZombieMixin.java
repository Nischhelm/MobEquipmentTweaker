package mobequipmenttweaker.mixin.vanilla.mobequipment;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import mobequipmenttweaker.config.ConfigHandler;
import mobequipmenttweaker.config.data.HandsSetEntry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(EntityZombie.class)
public abstract class EntityZombieMixin extends EntityLiving {
    public EntityZombieMixin(World worldIn) {
        super(worldIn);
    }

    @ModifyArg(
            method = "setEquipmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/Item;)V")
    )
    private Item eaglemixins_vanillaEntityZombie_setEquipmentBasedOnDifficulty_changeItem(Item itemIn){
        HandsSetEntry entry = ConfigHandler.getRandomWeapon(this.getRNG(), true);
        this.setDropChance(EntityEquipmentSlot.MAINHAND, entry.dropChance);
        return entry.items.get(EntityEquipmentSlot.MAINHAND);
    }

    @ModifyExpressionValue(
            method = "setEquipmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Ljava/util/Random;nextFloat()F")
    )
    private float eaglemixins_vanillaEntityZombie_setEquipmentBasedOnDifficulty_changeChance(float original){
        return original / ConfigHandler.melee.baseZombieChanceMulti; //easier to do rand/multi < somevalue than rand < somevalue x multi how its setup in this code
    }
}
