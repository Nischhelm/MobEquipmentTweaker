package mobequipmenttweaker.handlers;

import mobequipmenttweaker.config.ConfigHandler;
import mobequipmenttweaker.config.ConfigProvider;
import mobequipmenttweaker.config.ConfigProvider.ResolvedMobGroup;
import mobequipmenttweaker.config.data.*;
import mobequipmenttweaker.util.WeightedRandomIndex;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * Hooks are called at HEAD and before every return (TAIL) of onInitialSpawn / setEquipmentBasedOnDifficulty / setEnchantmentBasedOnDifficulty
 * in EntityLiving and all subclasses (see MobEquipmentClassTransformer). Only mobs that are part of a mob group are affected.
 * Super calls cause nested HEAD/TAIL pairs, so depth counters make sure only the outermost call acts.
 * - setEquipmentBasedOnDifficulty: without original behavior, the outermost HEAD rolls the mob group and cancels. With it, the outermost TAIL rolls the mob group on top
 * - setEnchantmentBasedOnDifficulty: without original behavior, the outermost HEAD cancels. With it, mob group items are hidden from the original enchanting between HEAD and TAIL
 * - onInitialSpawn: never cancelled, the outermost TAIL rolls the mob group if setEquipmentBasedOnDifficulty never got called
 */
public class MobEquipHandler {
    private static final EntityEquipmentSlot[] ARMOR_SLOTS = {EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};

    // Entities currently inside any of the hooked methods
    private static final Map<EntityLiving, EquipContext> CONTEXTS = new IdentityHashMap<>();

    private static class EquipContext {
        int spawnDepth = 0;
        int equipDepth = 0;
        int enchantDepth = 0;
        boolean equipped = false;
        final Set<EntityEquipmentSlot> filledSlots = EnumSet.noneOf(EntityEquipmentSlot.class);
        final Map<EntityEquipmentSlot, ItemStack> hiddenStacks = new EnumMap<>(EntityEquipmentSlot.class);

        boolean isDone() {
            return spawnDepth == 0 && equipDepth == 0 && enchantDepth == 0;
        }
    }

    private static ResolvedMobGroup getGroup(EntityLiving entity) {
        if (!ConfigHandler.mixintoggles.mobEquipmentModify) return null;
        return ConfigProvider.getMobGroup(entity);
    }

    private static void removeIfDone(EntityLiving entity, EquipContext context) {
        if (context.isDone()) CONTEXTS.remove(entity);
    }

    private static void equipAndRemember(EntityLiving entity, DifficultyInstance difficulty, ResolvedMobGroup group, EquipContext context) {
        context.filledSlots.addAll(equip(entity, difficulty, group));
        context.equipped = true;
    }

    // -------- onInitialSpawn --------

    public static void onInitialSpawnHead(EntityLiving entity, DifficultyInstance difficulty, IEntityLivingData livingData, CallbackInfoReturnable<IEntityLivingData> cir) {
        if (getGroup(entity) == null) return;
        CONTEXTS.computeIfAbsent(entity, e -> new EquipContext()).spawnDepth++;
    }

    public static void onInitialSpawnTail(EntityLiving entity, DifficultyInstance difficulty, IEntityLivingData livingData) {
        ResolvedMobGroup group = getGroup(entity);
        if (group == null) return;
        EquipContext context = CONTEXTS.get(entity);
        if (context == null) return;

        // Mob never called setEquipmentBasedOnDifficulty -> equip it now. Whatever it equipped in onInitialSpawn stays unless rolled over
        if (--context.spawnDepth == 0 && !context.equipped) equip(entity, difficulty, group);
        removeIfDone(entity, context);
    }

    // -------- setEquipmentBasedOnDifficulty --------

    public static void setEquipmentBasedOnDifficultyHead(EntityLiving entity, DifficultyInstance difficulty, CallbackInfo ci) {
        ResolvedMobGroup group = getGroup(entity);
        if (group == null) return;
        EquipContext context = CONTEXTS.computeIfAbsent(entity, e -> new EquipContext());

        if (context.equipDepth == 0 && !group.group.allowOriginalBehavior) {
            equipAndRemember(entity, difficulty, group, context);
            ci.cancel(); // no TAIL will follow
            removeIfDone(entity, context);
            return;
        }
        context.equipDepth++;
    }

    public static void setEquipmentBasedOnDifficultyTail(EntityLiving entity, DifficultyInstance difficulty) {
        ResolvedMobGroup group = getGroup(entity);
        if (group == null) return;
        EquipContext context = CONTEXTS.get(entity);
        if (context == null) return;

        if (--context.equipDepth == 0) equipAndRemember(entity, difficulty, group, context);
        removeIfDone(entity, context);
    }

    // -------- setEnchantmentBasedOnDifficulty --------

    public static void setEnchantmentBasedOnDifficultyHead(EntityLiving entity, DifficultyInstance difficulty, CallbackInfo ci) {
        ResolvedMobGroup group = getGroup(entity);
        if (group == null) return;
        EquipContext context = CONTEXTS.computeIfAbsent(entity, e -> new EquipContext());

        if (context.enchantDepth == 0) {
            // Mob groups enchant their equipment while equipping
            if (!group.group.allowOriginalBehavior) {
                ci.cancel(); // no TAIL will follow
                removeIfDone(entity, context);
                return;
            }
            // Mob group items are already enchanted by their enchantment group, so hide them from the original enchanting
            for (EntityEquipmentSlot slot : context.filledSlots) {
                context.hiddenStacks.put(slot, entity.getItemStackFromSlot(slot));
                entity.setItemStackToSlot(slot, ItemStack.EMPTY);
            }
        }
        context.enchantDepth++;
    }

    public static void setEnchantmentBasedOnDifficultyTail(EntityLiving entity, DifficultyInstance difficulty) {
        if (getGroup(entity) == null) return;
        EquipContext context = CONTEXTS.get(entity);
        if (context == null) return;

        if (--context.enchantDepth == 0) {
            context.hiddenStacks.forEach(entity::setItemStackToSlot);
            context.hiddenStacks.clear();
        }
        removeIfDone(entity, context);
    }

    private static Set<EntityEquipmentSlot> equip(EntityLiving entity, DifficultyInstance difficulty, ResolvedMobGroup group) {
        Set<EntityEquipmentSlot> filledSlots = EnumSet.noneOf(EntityEquipmentSlot.class);
        Random rand = entity.getRNG();
        EquipmentAlgorithm algo = group.algorithm;
        EnumDifficulty worldDifficulty = entity.world.getDifficulty();
        float localDifficulty = difficulty.getClampedAdditionalDifficulty();
        int tier = rollTier(rand, algo.tierRolling);

        // -------- ARMOR --------
        ArmorSet armorSet = null;
        float armorChance = algo.armorRolling.baseSpawnChance * (algo.armorRolling.difficultyScaling ? localDifficulty : 1F);
        if (rand.nextFloat() < armorChance) {
            armorSet = pickByTier(rand, group.armorPool, tier, set -> set.tier, set -> set.weight);
            if (armorSet != null) {
                float additionalPieceChance = algo.armorRolling.additionalPieceChance.getOrDefault(worldDifficulty, 0F);
                List<EntityEquipmentSlot> slots = new ArrayList<>(Arrays.asList(ARMOR_SLOTS));
                Collections.shuffle(slots, rand);

                boolean first = true;
                for (EntityEquipmentSlot slot : slots) {
                    if (!first && rand.nextFloat() >= additionalPieceChance) break;
                    first = false;
                    if (setItem(entity, slot, armorSet.getItem(slot), armorSet.dropChance, rand, localDifficulty, group.armorEnchants))
                        filledSlots.add(slot);
                }
            }
        }

        // -------- MAINHAND --------
        MainhandSet mainhandSet = null;
        float mainhandChance = algo.weaponRolling.baseSpawnChance.getOrDefault(worldDifficulty, 0F) * (algo.weaponRolling.difficultyScaling ? localDifficulty : 1F);
        if (rand.nextFloat() < mainhandChance) {
            List<ArmorSet.TiedMainhand> ties = armorSet == null ? Collections.emptyList() : armorSet.tiedMainhands;
            mainhandSet = pickWithTies(rand, ties, armorSet == null ? 0 : armorSet.tieChance, armorSet == null || armorSet.fallbackToPool,
                    tie -> tie.weight, tie -> ConfigProvider.getMainhandSet(tie.mainhandSetId),
                    () -> pickByTier(rand, group.mainhandPool, tier, set -> set.tier, set -> set.weight));
            if (mainhandSet != null && setItem(entity, EntityEquipmentSlot.MAINHAND, mainhandSet.getItem(), mainhandSet.dropChance, rand, localDifficulty, group.mainhandEnchants))
                filledSlots.add(EntityEquipmentSlot.MAINHAND);
        }

        // -------- OFFHAND --------
        // Tied offhands of the mainhand ignore the offhand chance
        OffhandSet offhandSet = null;
        if (mainhandSet != null && !mainhandSet.tiedOffhands.isEmpty() && rand.nextFloat() < mainhandSet.tieChance) {
            MainhandSet.TiedOffhand tie = WeightedRandomIndex.getRandomEntry(rand, mainhandSet.tiedOffhands, t -> t.weight);
            if (tie != null) offhandSet = ConfigProvider.getOffhandSet(tie.offhandSetId);
        } else if (mainhandSet == null || mainhandSet.tiedOffhands.isEmpty() || mainhandSet.fallbackToPool) {
            float offhandChance = algo.offhandRolling.baseSpawnChance.getOrDefault(worldDifficulty, 0F) * (algo.offhandRolling.difficultyScaling ? localDifficulty : 1F);
            if (rand.nextFloat() < offhandChance)
                offhandSet = pickByTier(rand, group.offhandPool, tier, set -> set.tier, set -> set.weight);
        }
        if (offhandSet != null && setItem(entity, EntityEquipmentSlot.OFFHAND, offhandSet.getItem(), offhandSet.dropChance, rand, localDifficulty, group.offhandEnchants))
            filledSlots.add(EntityEquipmentSlot.OFFHAND);

        return filledSlots;
    }

    private static boolean setItem(EntityLiving entity, EntityEquipmentSlot slot, Item item, float dropChance, Random rand, float localDifficulty, EnchantmentGroup enchants) {
        if (item == null || item == Items.AIR) return false;
        ItemStack stack = new ItemStack(item);
        if (enchants != null) stack = enchant(rand, stack, localDifficulty, enchants);
        entity.setItemStackToSlot(slot, stack);
        entity.setDropChance(slot, dropChance);
        return true;
    }

    // Vanilla: chance 0.25 * f (mainhand) or 0.5 * f (armor), enchantability 5 + f * rand.nextInt(18)
    private static ItemStack enchant(Random rand, ItemStack stack, float localDifficulty, EnchantmentGroup enchants) {
        float scale = enchants.difficultyScaling ? localDifficulty : 1F;
        if (rand.nextFloat() >= enchants.enchantChance * scale) return stack;
        int range = Math.max(1, enchants.maxEnchantability - enchants.minEnchantability + 1);
        int level = enchants.minEnchantability + (int) (scale * rand.nextInt(range));
        return EnchantmentHelper.addRandomEnchantment(rand, stack, level, enchants.allowTreasure);
    }

    // Vanilla: rand.nextInt(2) + three 9.5% rolls to go up one tier
    private static int rollTier(Random rand, EquipmentAlgorithm.TierRolling tierRolling) {
        int tier = rand.nextInt(Math.max(0, tierRolling.maxBaseTier) + 1);
        for (int i = 0; i < tierRolling.advancementRolls; i++)
            if (rand.nextFloat() < tierRolling.tierAdvancementChance) tier++;
        return tier;
    }

    /**
     * Weighted pick among the sets of the highest populated tier at or below the rolled tier.
     * If there is no such tier, uses the lowest populated tier above it.
     */
    private static <T> T pickByTier(Random rand, List<T> pool, int rolledTier, ToIntFunction<T> tierGetter, ToIntFunction<T> weightGetter) {
        if (pool.isEmpty()) return null;
        int bestBelow = Integer.MIN_VALUE;
        int bestAbove = Integer.MAX_VALUE;
        for (T entry : pool) {
            int tier = tierGetter.applyAsInt(entry);
            if (tier <= rolledTier) bestBelow = Math.max(bestBelow, tier);
            else bestAbove = Math.min(bestAbove, tier);
        }
        int chosenTier = bestBelow != Integer.MIN_VALUE ? bestBelow : bestAbove;

        List<T> candidates = new ArrayList<>();
        for (T entry : pool)
            if (tierGetter.applyAsInt(entry) == chosenTier) candidates.add(entry);
        return WeightedRandomIndex.getRandomEntry(rand, candidates, weightGetter);
    }

    /**
     * Without ties the pool is used. With ties, a successful tie roll picks a tied entry, otherwise fallbackToPool decides.
     */
    private static <S, T> S pickWithTies(Random rand, List<T> ties, float tieChance, boolean fallbackToPool,
                                         ToIntFunction<T> tieWeight, Function<T, S> tieResolver, Supplier<S> poolPicker) {
        if (ties.isEmpty()) return poolPicker.get();
        if (rand.nextFloat() < tieChance) {
            T tie = WeightedRandomIndex.getRandomEntry(rand, ties, tieWeight);
            return tie == null ? null : tieResolver.apply(tie);
        }
        return fallbackToPool ? poolPicker.get() : null;
    }
}
