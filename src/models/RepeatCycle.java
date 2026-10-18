// File: src/models/RepeatCycle.java
package models;

/**
 * Reset cycle for count-based habits.
 * Used when the user chooses "Times per Day" and then selects Daily/Weekly/Monthly.
 */
public enum RepeatCycle {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    MONTHLY("Monthly");

    private final String displayName;

    RepeatCycle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
