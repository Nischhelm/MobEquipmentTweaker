package mobequipmenttweaker.config.data;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Config;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HandsSetEntry extends SetEntry {
    public HandsSetEntry(List<String> items, String modid, int weight, float dropChance) {
        super(items, modid, weight, dropChance);
    }
    public HandsSetEntry(List<String> items, String modid, int weight) {
        this(items, modid, weight, 0.085F);
    }
    public HandsSetEntry(List<String> items, String modid) {
        this(items, modid, 1);
    }
    public HandsSetEntry() {} //needed for betterconfig

    @Config.Ignore
    public static final LinkedHashMap<EntityEquipmentSlot, String> defaultMap = new LinkedHashMap<>();
    @Config.Ignore
    public static final List<EntityEquipmentSlot> slotOrder = Arrays.asList(EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
    static {
        slotOrder.forEach(slot -> defaultMap.put(slot, ""));
    }
    @Override public List<EntityEquipmentSlot> getSlots() {
        return slotOrder;
    }
    @Override public Map<EntityEquipmentSlot, String> getDefaultMap() {
        return defaultMap;
    }
}
