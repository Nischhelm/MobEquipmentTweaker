package mobequipmenttweaker.event;

import mobequipmenttweaker.config.data.SetEntry;
import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

@Event.HasResult
public class MobEquipEvent extends EntityEvent {
    public MobEquipEvent(Entity entity, SetEntry entry) {
        super(entity);
        this.originalSet = this.newSet = entry;
    }

    SetEntry originalSet;
    SetEntry newSet;

    public SetEntry getOriginalSet() {
        return originalSet;
    }
    public SetEntry getSet() {
        return newSet;
    }
    public void setSet(SetEntry newSet) {
        this.newSet = newSet;
    }
}
