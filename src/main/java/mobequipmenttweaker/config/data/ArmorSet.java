package mobequipmenttweaker.config.data;

import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import meldexun.betterconfig.api.Order;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ArmorSet {

    @Config.Name("ID")
    @Config.Comment("Unique identifier for this armor set")
    @Order(0)
    public String id = "";

    @Config.Name("Tier")
    @Config.Comment("Tier this set belongs to (0=leather, 1=gold, 2=chain, 3=iron, 4=diamond, etc.)")
    @Config.RangeInt(min = 0)
    @Order(1)
    public int tier = 0;

    @Config.Name("Weight")
    @Config.Comment("Weight for weighted random selection within the tier")
    @Config.RangeInt(min = 1)
    @Order(2)
    public int weight = 1;

    @Config.Name("Default Mod ID")
    @Config.Comment("Default mod namespace for items (can be overridden per-item with modid:itemid)")
    @Order(4)
    public String modid = "minecraft";

    @Config.Name("Armor Pieces")
    @Config.Comment("Item IDs for each armor slot. Leave empty for slots you don't want filled.")
    @Order(5)
    public LinkedHashMap<ArmorSlot, String> armorPieces = getDefaultArmorMap();

    @Config.Name("Drop Chance")
    @Config.Comment("Drop chance for armor pieces")
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

    @Config.Name("Tied Mainhands")
    @Config.Comment("Mainhand sets this armor set can tie to with weighted selection")
    @Order(8)
    public ArrayList<TiedMainhand> tiedMainhands = new ArrayList<>();

    @Config.Name("Tie Chance")
    @Config.Comment("Chance to use a tied mainhand instead of random selection from mob group pool")
    @Config.RangeDouble(min = 0.0, max = 1.0)
    @Config.SlidingOption
    @Order(9)
    public float tieChance = 0.0F;

    @Config.Name("Fallback to Pool")
    @Config.Comment("If the tie roll fails, fallback to mob group's mainhand pool? If false, the mob gets no mainhand. Without any tied mainhands the pool is always used.")
    @Order(10)
    public boolean fallbackToPool = true;

    // Cached item instances
    @Config.Ignore
    private LinkedHashMap<EntityEquipmentSlot, Item> items = new LinkedHashMap<>();

    @Config.Ignore
    private boolean isSetup = false;

    public enum ArmorSlot {
        HEAD, CHEST, LEGS, FEET
    }

    public ArmorSet() {} //needed for betterconfig

    public ArmorSet(String id, int tier, int weight, String modid) {
        this.id = id;
        this.tier = tier;
        this.weight = weight;
        this.modid = modid;
    }

    public static LinkedHashMap<ArmorSlot, String> getDefaultArmorMap() {
        LinkedHashMap<ArmorSlot, String> map = new LinkedHashMap<>();
        map.put(ArmorSlot.HEAD, "");
        map.put(ArmorSlot.CHEST, "");
        map.put(ArmorSlot.LEGS, "");
        map.put(ArmorSlot.FEET, "");
        return map;
    }

    /**
     * Lazy initialization - resolves string IDs to actual Item instances
     */
    public void setup() {
        if (isSetup) return;

        for (ArmorSlot slot : ArmorSlot.values()) {
            String itemId = armorPieces.getOrDefault(slot, "");
            if (itemId.isEmpty()) {
                items.put(slotToEquipmentSlot(slot), Items.AIR);
                continue;
            }

            String resolvedModId = modid;
            String resolvedItemId = itemId;

            if (itemId.contains(":")) {
                String[] parts = itemId.split(":");
                resolvedModId = parts[0];
                resolvedItemId = parts[1];
            }

            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(resolvedModId, resolvedItemId));
            if (item == null) item = Items.AIR;
            items.put(slotToEquipmentSlot(slot), item);
        }

        isSetup = true;
    }

    public Item getItem(EntityEquipmentSlot slot) {
        if (!isSetup) setup();
        return items.getOrDefault(slot, Items.AIR);
    }

    private EntityEquipmentSlot slotToEquipmentSlot(ArmorSlot slot) {
        switch (slot) {
            case HEAD: return EntityEquipmentSlot.HEAD;
            case CHEST: return EntityEquipmentSlot.CHEST;
            case LEGS: return EntityEquipmentSlot.LEGS;
            case FEET: return EntityEquipmentSlot.FEET;
            default: return EntityEquipmentSlot.HEAD;
        }
    }

    public ArmorSet addTiedMainhand(String mainhandSetId, int weight) {
        this.tiedMainhands.add(new TiedMainhand(mainhandSetId, weight));
        return this;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArmorSet) {
            return ((ArmorSet) obj).id.equals(this.id);
        }
        return false;
    }

    /**
     * Weighted reference to a mainhand set for armor tie system
     */
    public static class TiedMainhand {

        @Config.Name("Mainhand Set ID")
        @Config.Comment("Reference to a MainhandSet by ID")
        @Order(0)
        public String mainhandSetId = "";

        @Config.Name("Weight")
        @Config.Comment("Weight for weighted random selection among tied mainhands")
        @Config.RangeInt(min = 1)
        @Order(1)
        public int weight = 1;

        public TiedMainhand() {} //needed for betterconfig

        public TiedMainhand(String mainhandSetId, int weight) {
            this.mainhandSetId = mainhandSetId;
            this.weight = weight;
        }

        @Override
        public int hashCode() {
            return mainhandSetId.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TiedMainhand) {
                return ((TiedMainhand) obj).mainhandSetId.equals(this.mainhandSetId);
            }
            return false;
        }
    }
}