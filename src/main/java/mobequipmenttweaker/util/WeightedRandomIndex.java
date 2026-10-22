package mobequipmenttweaker.util;

import java.util.List;
import java.util.Random;
import java.util.function.ToIntFunction;

public class WeightedRandomIndex {
    private static int getTotalWeight(List<Integer> weights) {
        int totalWeight = 0;

        for (int weight : weights)
            totalWeight += weight;

        return totalWeight;
    }

    private static int getRandomIndex(Random rand, List<Integer> weights, int totalWeight) {
        if (totalWeight <= 0) {
            throw new IllegalArgumentException();
        } else {
            int rolledWeight = rand.nextInt(totalWeight);

            for (int idx = 0; idx < weights.size(); idx++) {
                rolledWeight -= weights.get(idx);
                if (rolledWeight < 0) return idx;
            }

            return -1;
        }
    }

    public static int getRandomIndex(Random rand, List<Integer> weights) {
        return getRandomIndex(rand, weights, getTotalWeight(weights));
    }

    public static <T> T getRandomEntry(Random rand, List<T> entries, ToIntFunction<T> weightGetter) {
        int totalWeight = 0;
        for (T entry : entries)
            totalWeight += Math.max(0, weightGetter.applyAsInt(entry));
        if (totalWeight <= 0) return null;

        int rolledWeight = rand.nextInt(totalWeight);
        for (T entry : entries) {
            rolledWeight -= Math.max(0, weightGetter.applyAsInt(entry));
            if (rolledWeight < 0) return entry;
        }
        return null;
    }
}
