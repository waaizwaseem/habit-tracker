// File: src/ui/ScheduleMenu.java
package ui;

import models.Schedule;
import models.Habit;
import models.User;
import services.ScheduleService;
import utils.InputValidator;

import java.util.ArrayList;

/**
 * UI for schedule management.
 * Demonstrates separation of concerns (UI layer - no business logic).
 */
public class ScheduleMenu {
    
    // Private instance variables (Encapsulation)
    private ScheduleService scheduleService;
    private User currentUser;

    // Constructor
    public ScheduleMenu(User user) {
        this.currentUser = user;
        this.scheduleService = new ScheduleService();
    }

    /**
     * Display schedule management menu.
     */
    public void show() {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== SCHEDULE MANAGEMENT =====");
            System.out.println("1. View all schedules");
            System.out.println("2. Add time slot");
            System.out.println("3. Remove time slot");
            System.out.println("4. View schedule for specific day");
            System.out.println("5. Copy schedule to another day");
            System.out.println("6. Back to dashboard");
            
            int choice = InputValidator.readIntInRange("Enter choice: ", 1, 6);
            
            switch (choice) {
                case 1:
                    viewAllSchedules();
                    break;
                case 2:
                    addTimeSlot();
                    break;
                case 3:
                    removeTimeSlot();
                    break;
                case 4:
                    viewDaySchedule();
                    break;
                case 5:
                    copySchedule();
                    break;
                case 6:
                    running = false;
                    break;
            }
        }
    }

    /**
     * View all schedules for the week.
     */
    private void viewAllSchedules() {
        scheduleService.displayAllSchedules(currentUser);
        InputValidator.pause();
    }

    /**
     * Add a time slot to a schedule.
     */
    private void addTimeSlot() {
        System.out.println("\n===== ADD TIME SLOT =====");
        
        // Select day
        String day = selectDayOfWeek();
        
        // Get time slot details
        String slotName = InputValidator.readNonEmptyString("Enter slot name: ");
        String startTime = InputValidator.readNonEmptyString(
            "Enter start time (e.g., 08:00): ");
        String endTime = InputValidator.readNonEmptyString(
            "Enter end time (e.g., 09:00): ");
        
        // Optional: assign existing habits to this time slot
        ArrayList<Integer> habitIds = new ArrayList<>();
        if (!currentUser.getHabits().isEmpty() && InputValidator.readYesNo("Add existing habits to this time slot?")) {
            habitIds = selectHabitsForSlot();
        }

        // Add to schedule
        scheduleService.addTimeSlot(currentUser, day, slotName, startTime, endTime, habitIds);
        InputValidator.pause();
    }

    /**
     * Allow the user to select multiple existing habits to assign to a time slot.
     * Returns a list of habit IDs.
     */
    private ArrayList<Integer> selectHabitsForSlot() {
        ArrayList<Integer> selected = new ArrayList<>();
        ArrayList<Habit> habits = currentUser.getHabits();

        while (true) {
            System.out.println("\nSelect habits to add (0 to finish):");
            for (int i = 0; i < habits.size(); i++) {
                Habit h = habits.get(i);
                System.out.println((i + 1) + ". " + h.getHabitName() + " (ID: " + h.getHabitId() + ")");
            }

            int choice = InputValidator.readIntInRange("Choose habit: ", 0, habits.size());
            if (choice == 0) break;

            Habit chosen = habits.get(choice - 1);
            int id = chosen.getHabitId();
            if (selected.contains(id)) {
                System.out.println("Already added.");
            } else {
                selected.add(id);
                System.out.println("Added: " + chosen.getHabitName());
            }
        }

        return selected;
    }

    /**
     * Remove a time slot from a schedule.
     */
    private void removeTimeSlot() {
        System.out.println("\n===== REMOVE TIME SLOT =====");
        
        // Select day
        String day = selectDayOfWeek();
        
        // Display current schedule
        Schedule schedule = scheduleService.getScheduleForDay(currentUser, day);
        if (schedule == null || schedule.isEmpty()) {
            System.out.println("No time slots found for " + day);
            InputValidator.pause();
            return;
        }
        
        System.out.println();
        scheduleService.displayScheduleForDay(currentUser, day);
        
        // Select slot to remove
        int slotIndex = InputValidator.readIntInRange(
            "Enter slot number to remove: ", 1, schedule.getTimeSlotCount()) - 1;
        
        if (scheduleService.removeTimeSlot(currentUser, day, slotIndex)) {
            System.out.println("Time slot removed successfully!");
        } else {
            System.out.println("Failed to remove time slot.");
        }
        
        InputValidator.pause();
    }

    /**
     * View schedule for a specific day.
     */
    private void viewDaySchedule() {
        String day = selectDayOfWeek();
        System.out.println();
        scheduleService.displayScheduleForDay(currentUser, day);
        
        InputValidator.pause();
    }

    /**
     * Copy schedule from one day to another.
     */
    private void copySchedule() {
        System.out.println("\n===== COPY SCHEDULE =====");
        
        System.out.println("From which day?");
        String fromDay = selectDayOfWeek();
        
        System.out.println("To which day?");
        String toDay = selectDayOfWeek();
        
        scheduleService.copySchedule(currentUser, fromDay, toDay);
        InputValidator.pause();
    }

    /**
     * Display day selection menu.
     * 
     * @return Selected day of week
     */
    private String selectDayOfWeek() {
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", 
                        "Friday", "Saturday", "Sunday"};
        
        System.out.println("\nSelect day:");
        for (int i = 0; i < days.length; i++) {
            System.out.println((i + 1) + ". " + days[i]);
        }
        
        int choice = InputValidator.readIntInRange("Enter choice: ", 1, 7);
        return days[choice - 1];
    }
}