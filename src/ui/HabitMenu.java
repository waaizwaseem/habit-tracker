// File: src/ui/HabitMenu.java
package ui;

import models.*;
import services.HabitService;
import services.HabitRecommendationService;
import utils.InputValidator;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * UI for habit management.
 * Demonstrates separation of concerns (UI layer - no business logic).
 */
public class HabitMenu {
    
    // Private instance variables (Encapsulation)
    private HabitService habitService;
    private HabitRecommendationService recommendationService;
    private User currentUser;

    // Constructor
    public HabitMenu(User user) {
        this.currentUser = user;
        this.habitService = new HabitService();
        this.recommendationService = new HabitRecommendationService();
    }

    /**
     * Display habit management menu.
     */
    public void show() {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== HABIT MANAGEMENT =====");
            System.out.println("1. Create new habit");
            System.out.println("2. View all habits");
            System.out.println("3. Mark habit completed");
            System.out.println("4. View recommendations");
            System.out.println("5. Delete habit");
            System.out.println("6. Back to dashboard");
            
            int choice = InputValidator.readIntInRange("Enter choice: ", 1, 6);
            
            switch (choice) {
                case 1:
                    createHabit();
                    break;
                case 2:
                    viewAllHabits();
                    break;
                case 3:
                    markHabitCompleted();
                    break;
                case 4:
                    viewRecommendations();
                    break;
                case 5:
                    deleteHabit();
                    break;
                case 6:
                    running = false;
                    break;
            }
        }
    }

    /**
     * Create a new habit.
     */
    private void createHabit() {
        System.out.println("\n===== CREATE NEW HABIT =====");
        
        // Get habit name
        String habitName = InputValidator.readNonEmptyString("Enter habit name: ");
        
        // Select categories
        HashSet<Category> selectedCategories = selectCategories();
        if (selectedCategories.isEmpty()) {
            System.out.println("You must select at least one category.");
            return;
        }
        
        // Select frequency
        Frequency frequency = selectFrequency();
        
        // Create habit using service (demonstrates polymorphism)
        Habit newHabit = habitService.createHabit(habitName, selectedCategories, 
                                                  frequency);
        
        // Handle frequency-specific settings
        if (frequency == Frequency.TIMES_PER_DAY) {
            // NEW: Times-per-cycle habits can reset Daily / Weekly / Monthly
            RepeatCycle cycle = selectRepeatCycle();
            newHabit.setRepeatCycle(cycle);

            String unit = cycle.getDisplayName().toLowerCase();
            int times = InputValidator.readIntInRange(
                "How many times per " + unit + "? ", 1, 50);
            newHabit.setTimesPerDay(times);
        } else if (frequency == Frequency.SPECIFIC_DAYS) {
            ArrayList<String> days = selectSpecificDays();
            newHabit.setSpecificDays(days);
        }
        
        // Ask about sub-habits
        if (InputValidator.readYesNo("Add sub-habits?")) {
            addSubHabits(newHabit);
        }

        // Select EXP reward for this habit (user-defined)
        System.out.println("\nSelect EXP reward per completion:");
        System.out.println("1. Easy (5 EXP)");
        System.out.println("2. Medium (10 EXP)");
        System.out.println("3. Hard (30 EXP)");
        int expChoice = InputValidator.readIntInRange("Enter choice: ", 1, 3);
        int baseExp;
        switch (expChoice) {
            case 1: baseExp = 5; break;
            case 2: baseExp = 10; break;
            default: baseExp = 30; break;
        }
        newHabit.setBaseExp(baseExp);

        // Add to user
        habitService.addHabitToUser(currentUser, newHabit);
        System.out.println("Habit created successfully!");
        InputValidator.pause();
    }

    /**
     * View all user habits.
     */
    private void viewAllHabits() {
        ArrayList<Habit> habits = currentUser.getHabits();
        
        if (habits.isEmpty()) {
            System.out.println("\nYou don't have any habits yet.");
        } else {
            System.out.println("\n===== YOUR HABITS =====");
            for (int i = 0; i < habits.size(); i++) {
                Habit habit = habits.get(i);
                System.out.println((i + 1) + ". " + habit.getDisplayInfo());
                
                // Show sub-habits if any
                if (!habit.getSubHabits().isEmpty()) {
                    System.out.println("   Sub-habits:");
                    for (SubHabit subHabit : habit.getSubHabits()) {
                        System.out.println("   - " + subHabit.toString());
                    }
                }
            }
            System.out.println("========================");
            
            // Show completion stats
            double completion = habitService.getCompletionPercentage(currentUser);
            System.out.printf("\nCompletion today: %.1f%%\n", completion);
        }
        
        InputValidator.pause();
    }
    /**
     * Mark a habit as completed.
     */

    private void markHabitCompleted() {
        ArrayList<Habit> pending = habitService.getPendingHabits(currentUser);

        if (pending.isEmpty()) {
            System.out.println("\nNo habits available to mark today.");
            System.out.println("(Either all of today's habits are completed, or some habits are scheduled for different days.)");
            InputValidator.pause();
            return;
        }

        System.out.println("\n===== MARK HABIT COMPLETED =====");
        for (int i = 0; i < pending.size(); i++) {
            System.out.println((i + 1) + ". " + pending.get(i).getDisplayInfo());
        }

        int choice = InputValidator.readIntInRange("Select habit: ", 1, pending.size());
        Habit selectedHabit = pending.get(choice - 1);

        // Sub-habits workflow
        if (!selectedHabit.getSubHabits().isEmpty()) {
            boolean markIndividually = InputValidator.readYesNo(
                    "This habit has sub-habits. Mark them individually?");

            if (markIndividually) {
                while (true) {
                    System.out.println("\nSub-habits:");
                    ArrayList<SubHabit> subHabits = selectedHabit.getSubHabits();
                    for (int i = 0; i < subHabits.size(); i++) {
                        System.out.println((i + 1) + ". " + subHabits.get(i).toString());
                    }
                    System.out.println("0. Done marking sub-habits");

                    int subChoice = InputValidator.readIntInRange(
                            "Select sub-habit to mark as completed: ", 0, subHabits.size());

                    if (subChoice == 0) {
                        if (!selectedHabit.areAllSubHabitsCompleted()) {
                            System.out.println("\nNot all sub-habits are completed yet. The habit will NOT be marked as completed.");
                            if (InputValidator.readYesNo("Continue marking sub-habits?")) {
                                continue;
                            } else {
                                InputValidator.pause();
                                return;
                            }
                        }

                        if (!InputValidator.readYesNo("\nConfirm this habit as completed now?")) {
                            System.out.println("Habit not marked as completed.");
                            InputValidator.pause();
                            return;
                        }

                        break; // proceed to mark the main habit
                    }

                    SubHabit chosen = subHabits.get(subChoice - 1);
                    if (chosen.isCompletedToday()) {
                        System.out.println("Sub-habit already completed.");
                    } else {
                        habitService.markSubHabitCompleted(chosen);
                    }
                }
            } else {
                // Quick path: completing the habit also completes all its sub-habits
                for (SubHabit sh : selectedHabit.getSubHabits()) {
                    if (!sh.isCompletedToday()) {
                        sh.markCompleted();
                    }
                }
            }
        }

        // Mark main habit (service validates that sub-habits are complete if present)
        habitService.markHabitCompleted(currentUser, selectedHabit);
        InputValidator.pause();
    }

    /**
     * View habit recommendations based on user's categories.
     */
    private void viewRecommendations() {
        HashSet<Category> categories = currentUser.getSelectedCategories();
        
        if (categories.isEmpty()) {
            System.out.println("\nPlease select focus categories first.");
            InputValidator.pause();
            return;
        }
        
        System.out.println("\n===== HABIT RECOMMENDATIONS =====");
        
        for (Category category : categories) {
            System.out.println("\n" + category.getDisplayName() + ":");
            ArrayList<String> recommendations = 
                recommendationService.getRecommendationsForCategory(category);
            
            for (String recommendation : recommendations) {
                System.out.println("  - " + recommendation);
            }
        }
        
        System.out.println("\n==================================");
        InputValidator.pause();
    }

    /**
     * Delete a habit.
     */
    private void deleteHabit() {
        ArrayList<Habit> habits = currentUser.getHabits();
        
        if (habits.isEmpty()) {
            System.out.println("\nYou don't have any habits to delete.");
            InputValidator.pause();
            return;
        }
        
        System.out.println("\n===== DELETE HABIT =====");
        for (int i = 0; i < habits.size(); i++) {
            System.out.println((i + 1) + ". " + habits.get(i).getHabitName());
        }
        
        int choice = InputValidator.readIntInRange("Select habit to delete: ", 
                                                   1, habits.size());
        Habit toDelete = habits.get(choice - 1);
        
        if (InputValidator.readYesNo("Are you sure you want to delete '" + 
                                    toDelete.getHabitName() + "'?")) {
            habitService.deleteHabit(currentUser, toDelete.getHabitId());
            System.out.println("Habit deleted successfully!");
        }
        
        InputValidator.pause();
    }

    /**
     * Select categories for a habit.
     * 
     * @return Set of selected categories
     */
    private HashSet<Category> selectCategories() {
        HashSet<Category> selected = new HashSet<>();
        Category[] allCategories = Category.values();
        
        System.out.println("\nSelect categories (enter 0 when done):");
        for (int i = 0; i < allCategories.length; i++) {
            System.out.println((i + 1) + ". " + allCategories[i].getDisplayName());
        }
        
        while (true) {
            int choice = InputValidator.readIntInRange("Add category: ", 0, 
                                                      allCategories.length);
            if (choice == 0) break;
            selected.add(allCategories[choice - 1]);
            System.out.println("Category added!");
        }
        
        return selected;
    }

    /**
     * Select frequency for a habit.
     * 
     * @return Selected frequency
     */
    private Frequency selectFrequency() {
        System.out.println("\nSelect habit type:");
        System.out.println("1. Daily (once per day)");
        System.out.println("2. Times per Day (choose Daily/Weekly/Monthly cycle)");
        System.out.println("3. Specific Days (choose days of the week)");

        int choice = InputValidator.readIntInRange("Enter choice: ", 1, 3);
        switch (choice) {
            case 1:
                return Frequency.DAILY;
            case 2:
                return Frequency.TIMES_PER_DAY;
            default:
                return Frequency.SPECIFIC_DAYS;
        }
    }

    /**
     * Select the reset cycle for a TIMES_PER_DAY habit.
     */
    private RepeatCycle selectRepeatCycle() {
        System.out.println("\nSelect cycle for this habit:");
        System.out.println("1. Daily");
        System.out.println("2. Weekly");
        System.out.println("3. Monthly");

        int choice = InputValidator.readIntInRange("Enter choice: ", 1, 3);
        switch (choice) {
            case 1:
                return RepeatCycle.DAILY;
            case 2:
                return RepeatCycle.WEEKLY;
            default:
                return RepeatCycle.MONTHLY;
        }
    }

    /**
     * Select specific days for a habit.
     * 
     * @return List of selected days
     */
    private ArrayList<String> selectSpecificDays() {
        ArrayList<String> selectedDays = new ArrayList<>();
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday", 
                              "Friday", "Saturday", "Sunday"};
        
        System.out.println("\nSelect days (enter 0 when done):");
        for (int i = 0; i < daysOfWeek.length; i++) {
            System.out.println((i + 1) + ". " + daysOfWeek[i]);
        }
        
        while (true) {
            int choice = InputValidator.readIntInRange("Add day: ", 0, 7);
            if (choice == 0) break;
            selectedDays.add(daysOfWeek[choice - 1]);
            System.out.println("Day added!");
        }
        
        return selectedDays;
    }

    /**
     * Add sub-habits to a habit.
     * 
     * @param habit The parent habit
     */
    private void addSubHabits(Habit habit) {
        System.out.println("\nAdding sub-habits (enter blank to finish):");
        
        while (true) {
            String subHabitName = InputValidator.readString("Sub-habit name: ");
            if (subHabitName.isEmpty()) break;
            
            SubHabit subHabit = new SubHabit(subHabitName);
            habit.addSubHabit(subHabit);
            System.out.println("Sub-habit added!");
        }
    }
}
