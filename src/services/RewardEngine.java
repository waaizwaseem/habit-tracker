// File: src/services/RewardEngine.java
package services;

import models.Rewardable;

/**
 * Calculates EXP gained from completing a habit.
 * Design rule: EXP is user-defined per habit (no streak bonuses here).
 */
public final class RewardEngine {

    private RewardEngine() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static int calculateExp(int baseExp) {
        return Math.max(baseExp, 1);
    }

    public static int calculateExp(Rewardable rewardable) {
        if (rewardable == null) return 1;
        return calculateExp(rewardable.getBaseExp());
    }
}
