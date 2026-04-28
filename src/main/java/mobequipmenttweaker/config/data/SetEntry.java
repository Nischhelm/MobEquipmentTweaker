package mobequipmenttweaker.config.data;

import meldexun.betterconfig.api.Order;
import meldexun.betterconfig.api.Unmodifiable;
import net.minecraft.init.Items;
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
    @Order(2) @Unmodifiable @Config.Name("Items")
    public LinkedHashMap<EntityEquipmentSlot, String> itemIds = new LinkedHashMap<>(getDefaultMap());
    @Order(5) @Config.Name("Drop Chance")
    public float dropChance = 0.085F;

    @Config.Ignore
    private final Map<EntityEquipmentSlot, Item> items = new HashMap<>();
    @Config.Ignore
    public boolean isSetup = false;

    public SetEntry(List<String> items, String modid, int weight, float dropChance) {
        super(weight);
        for(int i = 0; i < items.size(); i++)
            itemIds.put(getSlots().get(i), items.get(i));
        this.modid = modid;
        this.dropChance = dropChance;
    }
    public SetEntry() {  //needed for betterconfig
        super(0);
    }

    public void setup(){
        for(Map.Entry<EntityEquipmentSlot, String> entry : itemIds.entrySet()) {
            String itemId = entry.getValue();
            Item item;
            if(itemId.isEmpty()) {
                item = Items.AIR;
            } else {
                item = Item.getByNameOrId(this.modid + itemId);
                if (item == null) item = Item.getByNameOrId(itemId); //fallback use custom modid in itemId
                if (item == null) item = Items.AIR;
            }
            //TODO: log issues
            items.put(entry.getKey(), item);
        }
        isSetup = true;
    }

    public SetEntry setName(String name){
        this.name = name;
        return this;
    }

    public Item getItem(EntityEquipmentSlot slot){
        if(!isSetup) setup();
        return items.get(slot);
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
