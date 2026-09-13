package mobequipmenttweaker.mixin.vanilla.mobequipment;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import mobequipmenttweaker.config.ConfigHandler;
import mobequipmenttweaker.config.ConfigProvider;
import mobequipmenttweaker.config.data.SetEntry;
import mobequipmenttweaker.util.MobEquipAlgorithm;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityZombie.class)
public abstract class EntityZombieMixin extends EntityLiving {
    public EntityZombieMixin(World worldIn) {
        super(worldIn);
    }

    @Inject(
            method = "setEquipmentBasedOnDifficulty",
            at = @At("HEAD")
    )
    private void mobequipmenttweaker_vanillaEntityZombie_setEquipmentBasedOnDifficulty_head(DifficultyInstance difficulty, CallbackInfo ci, @Share("algo")LocalRef<MobEquipAlgorithm> algo){
        algo.set(new MobEquipAlgorithm.VANILLA_MELEE(this.rand, difficulty.getClampedAdditionalDifficulty(), this.world.getDifficulty()));
    }

    @ModifyArg(
            method = "setEquipmentBasedOnDifficulty",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;<init>(Lnet/minecraft/item/Item;)V")
    )
    private Item mobequipmenttweaker_vanillaEntityZombie_setEquipmentBasedOnDifficulty_changeItem(Item itemIn, @Share("algo")LocalRef<MobEquipAlgorithm> algo){
        //TODO: roll tier
        SetEntry entry = ConfigProvider.getRandomWeapon(this.getRNG(), 0, true);
        this.setDropChance(EntityEquipmentSlot.MAINHAND, entry.dropChance);
        return entry.getItem(EntityEquipmentSlot.MAINHAND);
    }

    @ModifyConstant(
            method = "setEquipmentBasedOnDifficulty",
            constant = {@Constant(floatValue = 0.05F), @Constant(floatValue = 0.01F)}
    )
    private float mobequipmenttweaker_vanillaEntityZombie_setEquipmentBasedOnDifficulty_changeChance(float original, @Share("algo")LocalRef<MobEquipAlgorithm> algo){
        return algo.get().chanceToEquip() * ConfigHandler.melee.baseZombieChanceMulti;
    }
}
