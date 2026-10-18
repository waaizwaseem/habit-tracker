// File: src/models/SubHabit.java
package models;

import java.io.Serializable;

/**
 * Represents a sub-habit that belongs to a main habit.
 * Demonstrates encapsulation and composition.
 */
public class SubHabit implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Private data members (Encapsulation)
    private String name;
    private boolean completedToday;

    // Constructor
    public SubHabit(String name) {
        this.name = name;
        this.completedToday = false;
    }

    // Getters (Encapsulation)
    public String getName() {
        return name;
    }

    public boolean isCompletedToday() {
        return completedToday;
    }

    // Setters (Encapsulation)
    public void setName(String name) {
        this.name = name;
    }

    public void setCompletedToday(boolean completedToday) {
        this.completedToday = completedToday;
    }

    // Mark as completed
    public void markCompleted() {
        this.completedToday = true;
    }

    // Reset completion status (for new day)
    public void resetCompletion() {
        this.completedToday = false;
    }

    @Override
    public String toString() {
        return name + (completedToday ? " [✓]" : " [ ]");
    }
}