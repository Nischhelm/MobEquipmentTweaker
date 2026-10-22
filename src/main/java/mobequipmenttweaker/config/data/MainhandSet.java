package mobequipmenttweaker.config.data;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import meldexun.betterconfig.api.Order;

import java.util.ArrayList;

public class MainhandSet {

    @Config.Name("ID")
    @Config.Comment("Unique identifier for this mainhand")
    @Order(0)
    public String id = "";

    @Config.Name("Tier")
    @Config.Comment("Tier this set belongs to")
    @Config.RangeInt(min = 0, max = 100)
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
    @Config.Comment("Item ID for the mainhand weapon")
    @Order(4)
    public String item = "";

    @Config.Name("Drop Chance")
    @Config.Comment("Drop chance for this weapon")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Config.SlidingOption
    @Order(6)
    public float dropChance = 0.085F;

    @Config.Name("Looting Drop Chance")
    @Config.Comment("Additional drop chance per looting level")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Config.SlidingOption
    @Order(7)
    public float lootingDropChance = 0.01F;

    @Config.Name("Tied Offhands")
    @Config.Comment("Offhand sets this mainhand can tie to with weighted selection")
    @Order(8)
    public ArrayList<TiedOffhand> tiedOffhands = new ArrayList<>();

    @Config.Name("Tie Chance")
    @Config.Comment("Chance to use a tied offhand instead of random selection from mob group pool")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Config.SlidingOption
    @Order(9)
    public float tieChance = 0.0F;

    @Config.Name("Fallback to Pool")
    @Config.Comment("If the tie roll fails, fallback to mob group's offhand pool? If false, the mob gets no offhand. Without any tied offhands the pool is always used.")
    @Order(10)
    public boolean fallbackToPool = true;

    // Cached item instance
    @Config.Ignore
    private Item itemInstance = Items.AIR;

    @Config.Ignore
    private boolean isSetup = false;

    public MainhandSet() {} //needed for betterconfig

    public MainhandSet(String id, int tier, int weight, String modid, String item) {
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

    public MainhandSet addTiedOffhand(String offhandSetId, int weight) {
        this.tiedOffhands.add(new TiedOffhand(offhandSetId, weight));
        return this;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MainhandSet) {
            return ((MainhandSet) obj).id.equals(this.id);
        }
        return false;
    }

    /**
     * Weighted reference to an offhand set for mainhand tie system
     */
    public static class TiedOffhand {

        @Config.Name("Offhand Set ID")
        @Config.Comment("Reference to an OffhandSet by ID")
        @Order(0)
        public String offhandSetId = "";

        @Config.Name("Weight")
        @Config.Comment("Weight for weighted random selection among tied offhands")
        @Config.RangeInt(min = 1)
        @Order(1)
        public int weight = 1;

        public TiedOffhand() {} //needed for betterconfig

        public TiedOffhand(String offhandSetId, int weight) {
            this.offhandSetId = offhandSetId;
            this.weight = weight;
        }

        @Override
        public int hashCode() {
            return offhandSetId.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TiedOffhand) {
                return ((TiedOffhand) obj).offhandSetId.equals(this.offhandSetId);
            }
            return false;
        }
    }
}