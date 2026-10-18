// File: src/models/TimeSlot.java
package models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a time slot in a user's daily schedule.
 * Demonstrates encapsulation with private fields and public getters/setters.
 */
public class TimeSlot implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Private data members (Encapsulation)
    private String slotName;
    private String startTime;
    private String endTime;

    // Optional: habits assigned to this time slot (stored by habitId)
    // Using IDs avoids duplicating habit data and keeps schedule lightweight.
    private ArrayList<Integer> habitIds;

    // Constructor
    public TimeSlot(String slotName, String startTime, String endTime) {
        this.slotName = slotName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.habitIds = new ArrayList<>();
    }

    // Getters (Encapsulation)
    public String getSlotName() {
        return slotName;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    /**
     * Returns a copy of assigned habit IDs.
     * Backward compatible: if older serialized data has null, returns empty list.
     */
    public ArrayList<Integer> getHabitIds() {
        if (habitIds == null) habitIds = new ArrayList<>();
        return new ArrayList<>(habitIds);
    }

    // Setters (Encapsulation)
    public void setSlotName(String slotName) {
        this.slotName = slotName;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Assign a habit to this time slot.
     */
    public void addHabitId(int habitId) {
        if (habitIds == null) habitIds = new ArrayList<>();
        if (habitId <= 0) return;
        if (!habitIds.contains(habitId)) {
            habitIds.add(habitId);
        }
    }

    /**
     * Remove a habit assignment from this time slot.
     */
    public boolean removeHabitId(int habitId) {
        if (habitIds == null) habitIds = new ArrayList<>();
        return habitIds.remove(Integer.valueOf(habitId));
    }

    public boolean hasHabits() {
        return habitIds != null && !habitIds.isEmpty();
    }

    @Override
    public String toString() {
        int count = (habitIds == null) ? 0 : habitIds.size();
        if (count > 0) {
            return String.format("%s (%s - %s) [Habits: %d]", slotName, startTime, endTime, count);
        }
        return String.format("%s (%s - %s)", slotName, startTime, endTime);
    }
}