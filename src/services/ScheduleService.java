// File: src/services/ScheduleService.java
package services;

import models.Schedule;
import models.TimeSlot;
import models.User;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Service for managing user schedules.
 * Demonstrates separation of concerns (business logic layer).
 */
public class ScheduleService {

    /**
     * Add a time slot to a user's schedule for a specific day.
     * 
     * @param user The user
     * @param day The day of week
     * @param slotName Name of the time slot
     * @param startTime Start time
     * @param endTime End time
     */
    public void addTimeSlot(User user, String day, String slotName, 
                           String startTime, String endTime) {
        addTimeSlot(user, day, slotName, startTime, endTime, new ArrayList<>());
    }

    /**
     * Add a time slot and optionally assign existing habits to it.
     * Habits are stored by habitId inside the slot.
     */
    public void addTimeSlot(User user, String day, String slotName,
                            String startTime, String endTime,
                            ArrayList<Integer> habitIds) {
        Schedule schedule = user.getSchedule(day);
        if (schedule != null) {
            TimeSlot slot = new TimeSlot(slotName, startTime, endTime);
            if (habitIds != null) {
                for (Integer id : habitIds) {
                    if (id != null) slot.addHabitId(id);
                }
            }
            schedule.addTimeSlot(slot);
            System.out.println("Time slot added successfully!");
        } else {
            System.out.println("Invalid day of week.");
        }
    }

    /**
     * Remove a time slot from a user's schedule.
     * 
     * @param user The user
     * @param day The day of week
     * @param slotIndex Index of the slot to remove
     * @return true if successful, false otherwise
     */
    public boolean removeTimeSlot(User user, String day, int slotIndex) {
        Schedule schedule = user.getSchedule(day);
        if (schedule != null) {
            return schedule.removeTimeSlot(slotIndex);
        }
        return false;
    }

    /**
     * Get a user's schedule for a specific day.
     * 
     * @param user The user
     * @param day The day of week
     * @return Schedule object
     */
    public Schedule getScheduleForDay(User user, String day) {
        return user.getSchedule(day);
    }

    /**
     * Display all schedules for a user.
     * 
     * @param user The user
     */
    public void displayAllSchedules(User user) {
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", 
                              "Friday", "Saturday", "Sunday"};
        
        System.out.println("\n===== YOUR WEEKLY SCHEDULE =====");
        for (String day : daysOfWeek) {
            Schedule schedule = user.getSchedule(day);
            if (schedule != null && !schedule.isEmpty()) {
                System.out.println();
                displayScheduleForDay(user, day);
            }
        }
        System.out.println("================================\n");
    }

    /**
     * Print a day's schedule including assigned habit names.
     */
    public void displayScheduleForDay(User user, String day) {
        Schedule schedule = user.getSchedule(day);
        if (schedule == null) {
            System.out.println("No schedule found for " + day);
            return;
        }

        System.out.println("Schedule for " + day + ":");
        if (schedule.isEmpty()) {
            System.out.println("  No time slots defined.");
            return;
        }

        for (int i = 0; i < schedule.getTimeSlots().size(); i++) {
            TimeSlot slot = schedule.getTimeSlots().get(i);
            System.out.println("  " + (i + 1) + ". " + slot.toString());

            ArrayList<Integer> ids = slot.getHabitIds();
            if (!ids.isEmpty()) {
                System.out.print("     Habits: ");
                boolean first = true;
                for (Integer id : ids) {
                    if (id == null) continue;
                    String name = (user.getHabitById(id) != null)
                            ? user.getHabitById(id).getHabitName()
                            : ("Habit#" + id + " (missing)");
                    if (!first) System.out.print(", ");
                    System.out.print(name);
                    first = false;
                }
                System.out.println();
            }
        }
    }

    /**
     * Returns a set of day names (e.g., "Saturday") on which this habit is scheduled.
     * If empty, the habit is not scheduled anywhere.
     */
    public HashSet<String> getScheduledDaysForHabit(User user, int habitId) {
        HashSet<String> days = new HashSet<>();
        if (user == null || habitId <= 0) return days;

        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday",
                "Friday", "Saturday", "Sunday"};

        for (String day : daysOfWeek) {
            Schedule schedule = user.getSchedule(day);
            if (schedule == null) continue;
            for (TimeSlot slot : schedule.getTimeSlots()) {
                if (slot == null) continue;
                if (slot.getHabitIds().contains(habitId)) {
                    days.add(day);
                    break;
                }
            }
        }
        return days;
    }

    public boolean isHabitScheduledOnDay(User user, int habitId, String day) {
        if (user == null || habitId <= 0 || day == null) return false;
        Schedule schedule = user.getSchedule(day);
        if (schedule == null) return false;
        for (TimeSlot slot : schedule.getTimeSlots()) {
            if (slot == null) continue;
            if (slot.getHabitIds().contains(habitId)) return true;
        }
        return false;
    }

    /**
     * Check if a user has defined any schedules.
     * 
     * @param user The user
     * @return true if user has at least one schedule, false otherwise
     */
    public boolean hasSchedules(User user) {
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", 
                              "Friday", "Saturday", "Sunday"};
        
        for (String day : daysOfWeek) {
            Schedule schedule = user.getSchedule(day);
            if (schedule != null && !schedule.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Copy schedule from one day to another.
     * 
     * @param user The user
     * @param fromDay Source day
     * @param toDay Destination day
     */
    public void copySchedule(User user, String fromDay, String toDay) {
        Schedule sourceSchedule = user.getSchedule(fromDay);
        Schedule targetSchedule = user.getSchedule(toDay);
        
        if (sourceSchedule != null && targetSchedule != null) {
            // Clear target schedule
            while (!targetSchedule.isEmpty()) {
                targetSchedule.removeTimeSlot(0);
            }
            
            // Copy all time slots
            for (TimeSlot slot : sourceSchedule.getTimeSlots()) {
                TimeSlot newSlot = new TimeSlot(slot.getSlotName(),
                        slot.getStartTime(),
                        slot.getEndTime());
                for (Integer id : slot.getHabitIds()) {
                    if (id != null) newSlot.addHabitId(id);
                }
                targetSchedule.addTimeSlot(newSlot);
            }
            
            System.out.println("Schedule copied from " + fromDay + " to " + toDay);
        }
    }
}