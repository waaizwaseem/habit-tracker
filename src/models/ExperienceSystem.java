// File: src/models/ExperienceSystem.java
package models;

import java.io.Serializable;

/**
 * Tracks user EXP and level.
 * EXP required increases each level.
 */
public class ExperienceSystem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int level;
    private int currentExp;
    private int expToNextLevel;

    // Leveling configuration
    // Requirement rule:
    // Level 1 -> need 100 EXP to reach level 2
    // Level 2 -> need 200 EXP to reach level 3
    // Level N -> need (N * 100) EXP to reach level N+1
    private static final int START_LEVEL = 1;
    private static final int EXP_PER_LEVEL_STEP = 100;

    public ExperienceSystem() {
        this.level = START_LEVEL;
        this.currentExp = 0;
        this.expToNextLevel = level * EXP_PER_LEVEL_STEP;
    }

    public void addExp(int amount) {
        if (amount <= 0) return;
        currentExp += amount;
        handleLevelUps();
    }

    private void handleLevelUps() {
        while (currentExp >= expToNextLevel) {
            currentExp -= expToNextLevel;
            level++;
            expToNextLevel = level * EXP_PER_LEVEL_STEP;
        }
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentExp() {
        return currentExp;
    }

    public int getExpToNextLevel() {
        return expToNextLevel;
    }

    public String getProgressBar() {
        int barLength = 20;
        int filled = (int) ((currentExp / (double) expToNextLevel) * barLength);

        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < filled; i++) bar.append('#');
        for (int i = filled; i < barLength; i++) bar.append('-');
        bar.append("] ");

        bar.append(currentExp).append('/').append(expToNextLevel).append(" EXP");
        return bar.toString();
    }
}
