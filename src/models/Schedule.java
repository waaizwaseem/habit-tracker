// File: src/models/Schedule.java
package models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a user's daily schedule.
 * Demonstrates COMPOSITION (Schedule HAS-A list of TimeSlots).
 */
public class Schedule implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Private data members (Encapsulation)
    private String dayOfWeek;
    private ArrayList<TimeSlot> timeSlots; // Composition: Schedule HAS-A TimeSlot

    // Constructor
    public Schedule(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        this.timeSlots = new ArrayList<>();
    }

    // Getter for dayOfWeek
    public String getDayOfWeek() {
        return dayOfWeek;
    }

    // Setter for dayOfWeek
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    // Add a time slot (Composition management)
    public void addTimeSlot(TimeSlot slot) {
        if (slot != null) {
            timeSlots.add(slot);
        }
    }

    // Remove a time slot by index
    public boolean removeTimeSlot(int index) {
        if (index >= 0 && index < timeSlots.size()) {
            timeSlots.remove(index);
            return true;
        }
        return false;
    }

    // Get all time slots (returns copy to maintain encapsulation)
    public ArrayList<TimeSlot> getTimeSlots() {
        return new ArrayList<>(timeSlots);
    }

    // Get time slot count
    public int getTimeSlotCount() {
        return timeSlots.size();
    }

    // Check if schedule is empty
    public boolean isEmpty() {
        return timeSlots.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Schedule for ").append(dayOfWeek).append(":\n");
        if (timeSlots.isEmpty()) {
            sb.append("  No time slots defined.\n");
        } else {
            for (int i = 0; i < timeSlots.size(); i++) {
                sb.append("  ").append(i + 1).append(". ")
                  .append(timeSlots.get(i).toString()).append("\n");
            }
        }
        return sb.toString();
    }
}