## MobEquipmentTweaker Config Structure

Mobs only get equipment from this mod if they're listed in a **Mob Group**. Everything else is left alone.
Groups pick 
- an **Equipment Algorithm** (how likely things are), 
- **Enchantment Groups** (how things get enchanted) 
- and pools of **Equipment Sets** (what can show up). 

Groups and sets reference each other by ID/name.

### 1. Equipment Algorithms
How likely a mob is to get armor, a weapon and an offhand item, and which tier it gets.
```
EquipmentAlgorithm {
  - ID
  - Armor Rolling:   Base Chance (0.15 vanilla), Additional Piece Chance (per difficulty, 0.75 / 0.9 hard), Difficulty Scaling
  - Weapon Rolling:  Base Chance (per difficulty), Difficulty Scaling
  - Offhand Rolling: Base Chance (per difficulty), Difficulty Scaling
  - Tier Rolling:    Max Base Tier (1 = start at tier 0 or 1), Tier Advancement Rolls (3), Tier Advancement Chance (0.095)
}
```
"Difficulty Scaling" multiplies the chance with the local difficulty, so it only reaches the full value in areas players have been in for a long time.

Defaults: 
- **Vanilla Standard** (zombies) 
- **Vanilla Guaranteed Weapon** (skeletons, pigmen, vindicators, vexes, wither skeletons always get their weapon).

### 2. Enchantment Groups
```
EnchantmentGroup {
  - ID, Enchant Chance, Min/Max Enchantability, Allow Treasure Enchantments, Difficulty Scaling
}
```

### 3. Equipment Sets
Armor sets fill up to four armor slots, mainhand and offhand sets are a single item. Items can be `itemid` (uses the set's Mod ID) or `modid:itemid`.
```
ArmorSet / MainhandSet / OffhandSet {
  - ID, Tier, Weight, Mod ID, Item(s)
  - Drop Chance (0.085 vanilla)
  - Looting Drop Chance: extra drop chance per looting level (0.01 vanilla)
  - Armor and mainhand sets only: Tied Mainhands / Tied Offhands, Tie Chance, Fallback to Pool
}
```
Changes to drop chances also apply to mobs that already spawned. Items a mob picked up will always drop, like in vanilla.

### 4. Mob Groups
```
MobGroup {
  - ID
  - Entities: entity IDs, e.g. minecraft:zombie
  - Allow Original Behavior
  - Equipment Algorithm
  - Enchantments: Armor / Mainhand / Offhand Enchantment Group (empty = never enchanted)
  - Equipment Pools: Armor / Mainhand / Offhand Sets
}
```
**Allow Original Behavior**
- off: the mob's own equipment code is skipped, only the group decides.
- on: the mob equips itself like normal first (including things like armor variants some modded mobs set there), then the group rolls on top. Whatever the group rolls replaces the mob's item in that slot. 

Chances stack, so a vanilla zombie with this on gets vanilla armor chances *and* the group's, so recommended to only use this for modded mobs that have custom equipment logic AND should be equipped by MobEquipmentTweaker.

Equipment a mob sets directly while spawning (outside its equipment code) can't be prevented, the group can only roll over it. 

Mobs spawned outside the normal spawn logic (e.g. `/summon` with NBT) don't get equipment.

---

## How a mob gets equipped

1. **Tier:** one tier is rolled per mob and used for all its equipment. If no set in the pool has that tier, the nearest lower tier is used, otherwise the nearest higher one.
2. **Armor:** roll the armor chance, pick a set from the pool, then fill pieces in random order, rolling "Additional Piece Chance" for each piece after the first.
3. **Mainhand:** roll the weapon chance. If the previously rolled armor set has tied mainhands, "Tie Chance" decides between a tied one and the pool (or nothing, if "Fallback to Pool" is off).
4. **Offhand:** a tied offhand of the mainhand set is used if its tie roll succeeds, even without passing the offhand chance. Otherwise roll the offhand chance and pick from the pool.
5. **Enchanting:** every rolled item gets the group's enchantment group for its slot. Vanilla equipment enchanting logic never touches items rolled from this mod.