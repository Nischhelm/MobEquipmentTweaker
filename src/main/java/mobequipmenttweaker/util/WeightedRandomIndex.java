package mobequipmenttweaker.util;

import java.util.List;
import java.util.Random;

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
}
