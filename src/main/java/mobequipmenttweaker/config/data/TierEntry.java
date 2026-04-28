package mobequipmenttweaker.config.data;

import net.minecraftforge.common.config.Config;

import java.util.ArrayList;

public class TierEntry {
    @Config.Name("Weight")
    public int weight;

    @Config.Name("Sets")
    public ArrayList<SetEntry> sets = new ArrayList<>();

    public TierEntry() {
        this(1);
    }

    public TierEntry(int weight) {
        this.weight = weight;
    }

    public TierEntry addSet(SetEntry sets) {
        this.sets.add(sets);
        return this;
    }
}
