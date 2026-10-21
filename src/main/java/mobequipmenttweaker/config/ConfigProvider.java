package mobequipmenttweaker.config;

import mobequipmenttweaker.Tags;
import mobequipmenttweaker.config.data.*;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import java.util.*;
import java.util.function.Function;

/**
 * Resolves the id references between mob groups, algorithms, enchantment groups and equipment sets.
 * Built lazily on first use and reset on config change.
 */
public class ConfigProvider {
    private static Map<String, ResolvedMobGroup> groupsByEntity = null;
    private static Map<String, MainhandSet> mainhandSetsById = null;
    private static Map<String, OffhandSet> offhandSetsById = null;

    public static ResolvedMobGroup getMobGroup(EntityLiving entity) {
        ResourceLocation entityId = EntityList.getKey(entity);
        if (entityId == null) return null;
        if (groupsByEntity == null) init();
        return groupsByEntity.get(entityId.toString());
    }

    public static MainhandSet getMainhandSet(String id) {
        if (mainhandSetsById == null) init();
        return mainhandSetsById.get(id);
    }

    public static OffhandSet getOffhandSet(String id) {
        if (offhandSetsById == null) init();
        return offhandSetsById.get(id);
    }

    public static void reset() {
        groupsByEntity = null;
        mainhandSetsById = null;
        offhandSetsById = null;
    }

    private static void init() {
        Map<String, EquipmentAlgorithm> algorithms = mapById(ConfigHandler.algorithms.algorithms, algo -> algo.id, "Equipment Algorithm");
        Map<String, EnchantmentGroup> enchantGroups = mapById(ConfigHandler.enchantmentGroups.groups, group -> group.id, "Enchantment Group");
        Map<String, ArmorSet> armorSets = mapById(ConfigHandler.equipmentSets.armorSets, set -> set.id, "Armor Set");
        mainhandSetsById = mapById(ConfigHandler.equipmentSets.mainhandSets, set -> set.id, "Mainhand Set");
        offhandSetsById = mapById(ConfigHandler.equipmentSets.offhandSets, set -> set.id, "Offhand Set");

        groupsByEntity = new HashMap<>();
        for (MobGroup group : ConfigHandler.mobGroups.groups) {
            EquipmentAlgorithm algorithm = algorithms.get(group.equipmentAlgorithm);
            if (algorithm == null) {
                Tags.LOGGER.warn("Mob Group {} references unknown Equipment Algorithm {}, ignoring group.", group.id, group.equipmentAlgorithm);
                continue;
            }

            ResolvedMobGroup resolved = new ResolvedMobGroup(
                    group,
                    algorithm,
                    resolveOptional(enchantGroups, group.enchantments.armorEnchantGroup, group.id, "Enchantment Group"),
                    resolveOptional(enchantGroups, group.enchantments.mainhandEnchantGroup, group.id, "Enchantment Group"),
                    resolveOptional(enchantGroups, group.enchantments.offhandEnchantGroup, group.id, "Enchantment Group"),
                    resolveAll(armorSets, group.equipmentPools.armorSets, group.id, "Armor Set"),
                    resolveAll(mainhandSetsById, group.equipmentPools.mainhandSets, group.id, "Mainhand Set"),
                    resolveAll(offhandSetsById, group.equipmentPools.offhandSets, group.id, "Offhand Set")
            );

            for (String entityId : group.entities) {
                ResolvedMobGroup previous = groupsByEntity.putIfAbsent(entityId, resolved);
                if (previous != null)
                    Tags.LOGGER.warn("Entity {} is in Mob Groups {} and {}, using {}.", entityId, previous.group.id, group.id, previous.group.id);
            }
        }
    }

    private static <T> Map<String, T> mapById(List<T> entries, Function<T, String> idGetter, String type) {
        Map<String, T> map = new HashMap<>();
        for (T entry : entries)
            if (map.putIfAbsent(idGetter.apply(entry), entry) != null)
                Tags.LOGGER.warn("Duplicate {} id {}, using the first one.", type, idGetter.apply(entry));
        return map;
    }

    private static <T> T resolveOptional(Map<String, T> map, String id, String groupId, String type) {
        if (id.isEmpty()) return null;
        T entry = map.get(id);
        if (entry == null) Tags.LOGGER.warn("Mob Group {} references unknown {} {}, ignoring.", groupId, type, id);
        return entry;
    }

    private static <T> List<T> resolveAll(Map<String, T> map, List<String> ids, String groupId, String type) {
        List<T> list = new ArrayList<>();
        for (String id : ids) {
            T entry = resolveOptional(map, id, groupId, type);
            if (entry != null) list.add(entry);
        }
        return list;
    }

    public static class ResolvedMobGroup {
        public final MobGroup group;
        public final EquipmentAlgorithm algorithm;
        public final EnchantmentGroup armorEnchants;
        public final EnchantmentGroup mainhandEnchants;
        public final EnchantmentGroup offhandEnchants;
        public final List<ArmorSet> armorPool;
        public final List<MainhandSet> mainhandPool;
        public final List<OffhandSet> offhandPool;

        private ResolvedMobGroup(MobGroup group, EquipmentAlgorithm algorithm,
                                 EnchantmentGroup armorEnchants, EnchantmentGroup mainhandEnchants, EnchantmentGroup offhandEnchants,
                                 List<ArmorSet> armorPool, List<MainhandSet> mainhandPool, List<OffhandSet> offhandPool) {
            this.group = group;
            this.algorithm = algorithm;
            this.armorEnchants = armorEnchants;
            this.mainhandEnchants = mainhandEnchants;
            this.offhandEnchants = offhandEnchants;
            this.armorPool = armorPool;
            this.mainhandPool = mainhandPool;
            this.offhandPool = offhandPool;
        }
    }
}
