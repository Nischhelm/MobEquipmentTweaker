package mobequipmenttweaker.config.data;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import meldexun.betterconfig.api.Order;

public class OffhandSet {

    @Config.Name("ID")
    @Config.Comment("Unique identifier for this offhand")
    @Order(0)
    public String id = "";

    @Config.Name("Tier")
    @Config.Comment("Tier this set belongs to")
    @Config.RangeInt(min = 0)
    @Order(1)
    public int tier = 0;

    @Config.Name("Weight")
    @Config.Comment("Weight for weighted random selection within the tier")
    @Config.RangeInt(min = 1)
    @Order(2)
    public int weight = 1;

    @Config.Name("Mod ID")
    @Config.Comment("Mod namespace for the item (can be overridden per-item with modid:itemid)")
    @Order(3)
    public String modid = "minecraft";

    @Config.Name("Item")
    @Config.Comment("Item ID for the offhand item")
    @Order(4)
    public String item = "";

    @Config.Name("Drop Chance")
    @Config.Comment("Drop chance for this item")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Config.SlidingOption
    @Order(5)
    public float dropChance = 0.085F;

    @Config.Name("Looting Drop Chance")
    @Config.Comment("Additional drop chance per looting level")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Config.SlidingOption
    @Order(6)
    public float lootingDropChance = 0.01F;

    // Cached item instance
    @Config.Ignore
    private Item itemInstance = Items.AIR;

    @Config.Ignore
    private boolean isSetup = false;

    public OffhandSet() {} //needed for betterconfig

    public OffhandSet(String id, int tier, int weight, String modid, String item) {
        this.id = id;
        this.tier = tier;
        this.weight = weight;
        this.modid = modid;
        this.item = item;
    }

    /**
     * Lazy initialization - resolves string ID to actual Item instance
     */
    public void setup() {
        if (isSetup) return;

        if (item.isEmpty()) {
            itemInstance = Items.AIR;
            isSetup = true;
            return;
        }

        String resolvedModId = modid;
        String resolvedItemId = item;

        if (item.contains(":")) {
            String[] parts = item.split(":");
            resolvedModId = parts[0];
            resolvedItemId = parts[1];
        }

        Item foundItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(resolvedModId, resolvedItemId));
        itemInstance = (foundItem != null) ? foundItem : Items.AIR;
        isSetup = true;
    }

    public Item getItem() {
        if (!isSetup) setup();
        return itemInstance;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OffhandSet) {
            return ((OffhandSet) obj).id.equals(this.id);
        }
        return false;
    }
}