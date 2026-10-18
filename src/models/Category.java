// File: src/models/Category.java
package models;

public enum Category {
    HEALTH("Health"),
    FITNESS("Fitness"),
    PRODUCTIVITY("Productivity"),
    LEARNING("Learning"),
    MENTAL_HEALTH("Mental Health"),
    SPIRITUAL("Spiritual");

    private final String displayName;

    Category(String displayName) {
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