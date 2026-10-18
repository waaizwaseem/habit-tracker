// File: src/models/Frequency.java
package models;

/**
 * Enum representing habit frequency options.
 * Demonstrates use of enums in OOP.
 */
public enum Frequency {
    TIMES_PER_DAY("Times per Day"),
    DAILY("Daily"),
    WEEKLY("Weekly"),
    SPECIFIC_DAYS("Specific Days");

    private final String displayName;

    Frequency(String displayName) {
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