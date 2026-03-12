package mobequipmenttweaker.config.data;

import meldexun.betterconfig.api.Order;
import meldexun.betterconfig.api.Unmodifiable;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.config.Config;

import java.util.*;

public class SetEntry extends WeightedRandom.Item {
    @Order(0) @Config.Name("Name")
    public String name = "";
    @Order(1) @Config.Name("ModId")
    public String modid = "minecraft";
    @Order(2) @Config.Name("Items") @Unmodifiable
    public LinkedHashMap<EntityEquipmentSlot, String> itemIds = new LinkedHashMap<>(getDefaultMap());
    @Order(3) @Config.Name("Tier")
    public int tier = 0;
//    @Order(4) @Config.Name("Weight")
//    public int weight = 1;
    @Order(5) @Config.Name("Drop Chance")
    public float dropChance = 0.085F;

    @Config.Ignore
    public Map<EntityEquipmentSlot, Item> items = new HashMap<>();
    @Config.Ignore
    public boolean isValid = false;

    public SetEntry(List<String> items, String modid, int tier, int weight, float dropChance) {
        super(weight);
        for(int i = 0; i < items.size(); i++)
            itemIds.put(getSlots().get(i), items.get(i));
        this.modid = modid;
        this.tier = tier;
        this.dropChance = dropChance;
    }
    public SetEntry(List<String> items, String modid, int tier, int weight) {
        this(items, modid, tier, weight, 0.085F);
    }
    public SetEntry(List<String> items, String modid, int tier) {
        this(items, modid, tier, 1);
    }
    public SetEntry() { super(0);} //needed for betterconfig

    public boolean setup(){
        isValid = false;
        for(Map.Entry<EntityEquipmentSlot, String> entry : itemIds.entrySet()) {
            String itemId = entry.getValue();
            if(itemId.isEmpty()) continue;
            Item item = Item.getByNameOrId(this.modid + itemId);
            if(item == null) item = Item.getByNameOrId(itemId);
            if(item == null) return false;
            //TODO: log issues
            items.put(entry.getKey(), item);
        }
        isValid = true;
        return isValid;
    }

    public SetEntry setName(String name){
        this.name = name;
        return this;
    }

    @Config.Ignore
    public static final LinkedHashMap<EntityEquipmentSlot, String> defaultMap = new LinkedHashMap<>();
    @Config.Ignore
    public static final List<EntityEquipmentSlot> slotOrder = Arrays.asList(EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET, EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND);
    static {
        slotOrder.forEach(slot -> defaultMap.put(slot, ""));
    }
    public List<EntityEquipmentSlot> getSlots() {
        return slotOrder;
    }
    public Map<EntityEquipmentSlot, String> getDefaultMap() {
        return defaultMap;
    }
}
