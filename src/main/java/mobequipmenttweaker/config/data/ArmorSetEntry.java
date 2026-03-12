package mobequipmenttweaker.config.data;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.common.config.Config;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ArmorSetEntry extends SetEntry {
    public ArmorSetEntry(List<String> items, String modid, int tier, int weight, float dropChance) {
        super(items, modid, tier, weight, dropChance);
    }
    public ArmorSetEntry(List<String> items, String modid, int tier, int weight) {
        this(items, modid, tier, weight, 0.085F);
    }
    public ArmorSetEntry(List<String> items, String modid, int tier) {
        this(items, modid, tier, 1);
    }
    public ArmorSetEntry() {} //needed for betterconfig

    @Config.Ignore
    public static final LinkedHashMap<EntityEquipmentSlot, String> defaultMap = new LinkedHashMap<>();
    @Config.Ignore
    public static final List<EntityEquipmentSlot> slotOrder = Arrays.asList(EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET);
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
