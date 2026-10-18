// File: src/ui/DashboardMenu.java
package ui;

import models.Category;
import models.User;
import services.AuthenticationService;
import services.UserService;
import utils.InputValidator;
import utils.DateUtils;
import utils.AppContext;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.util.HashSet;

/**
 * UI for user dashboard (main menu after login).
 * Demonstrates separation of concerns (UI layer - no business logic).
 */
public class DashboardMenu {
    
    // Private instance variables (Encapsulation)
    private User currentUser;
    private UserService userService;
    private AuthenticationService authService;
    private ScheduleMenu scheduleMenu;
    private HabitMenu habitMenu;

    // Constructor
    public DashboardMenu(User user, AuthenticationService authService) {
        this.currentUser = user;
        this.authService = authService;
        this.userService = new UserService();
        this.scheduleMenu = new ScheduleMenu(user);
        this.habitMenu = new HabitMenu(user);
    }

    /**
     * Display and run the dashboard menu.
     * 
     * @return false if user logs out, true to continue
     */
    public boolean show() {
        boolean running = true;
        
        while (running) {
            // Sync streak on each loop so missed-day resets apply even without completing tasks
            currentUser.getDailyStreakTracker().syncToDate(currentUser, AppContext.today());
            displayHeader();
            displayMenu();
            
            boolean isSunday = (DateUtils.getCurrentLocalDate().getDayOfWeek() == DayOfWeek.SUNDAY);
            int maxChoice = 8 + (isSunday ? 1 : 0) + (AppContext.isSimulationMode() ? 1 : 0);
            int choice = InputValidator.readIntInRange("Enter choice: ", 1, maxChoice);
            
            switch (choice) {
                case 1:
                    manageSchedule();
                    break;
                case 2:
                    manageFocusCategories();
                    break;
                case 3:
                    manageHabits();
                    break;
                case 4:
                    viewDashboardSummary();
                    break;
                case 5:
                    viewStatistics();
                    break;
                case 6:
                    saveAndExit();
                    return false; // Logout
                case 7:
                    about();
                    break;
                case 8:
                    if (confirmExit()) {
                        saveAndExit();
                        return true; // Exit application
                    }
                    break;
                default:
                    // Sunday-only weekly scoreboard
                    int scoreboardChoice = isSunday ? 9 : -1;
                    int simulationChoice = AppContext.isSimulationMode() ? (isSunday ? 10 : 9) : -1;

                    if (choice == scoreboardChoice) {
                        userService.displayWeeklyScoreboard(currentUser);
                        InputValidator.pause();
                    } else if (choice == simulationChoice) {
                        openSimulationControls();
                    }
                    break;
            }
        }
        
        return false;
    }

    /**
     * Display dashboard header with user info.
     */
    private void displayHeader() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        HABIT TRACKER DASHBOARD        ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Welcome, " + currentUser.getUsername() + "!");
        System.out.println("Date: " + DateUtils.getCurrentDate());
        System.out.println("Day: " + DateUtils.getCurrentDayOfWeek());
        System.out.println("Mode: " + AppContext.getModeDisplayName());

        // Subsystem 2 integration: show progression
        System.out.println("Level: " + currentUser.getExperienceSystem().getLevel());
        System.out.println("EXP:   " + currentUser.getExperienceSystem().getProgressBar());
        System.out.println("Streak: " + currentUser.getDailyStreakTracker().getDailyStreak() + " day(s)");
        System.out.println("Streak Saves: " + currentUser.getStreakSaves());
    }

    /**
     * Display main menu options.
     */
    private void displayMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. Manage Schedule");
        System.out.println("2. Manage Focus Categories");
        System.out.println("3. Manage Habits");
        System.out.println("4. View Dashboard Summary");
        System.out.println("5. View Statistics");
        System.out.println("6. Save & Logout");
        System.out.println("7. About");
        System.out.println("8. Exit Application");

        boolean isSunday = (DateUtils.getCurrentLocalDate().getDayOfWeek() == DayOfWeek.SUNDAY);
        int nextOption = 9;

        if (isSunday) {
            System.out.println(nextOption + ". Weekly Scoreboard (This Week)");
            nextOption++;
        }
        if (AppContext.isSimulationMode()) {
            System.out.println(nextOption + ". Simulation Controls");
        }

        System.out.println("=====================");
    }

    private void openSimulationControls() {
        if (!AppContext.isSimulationMode()) return;
        SimulationMenu simMenu = new SimulationMenu(currentUser);
        simMenu.show();
    }

    /**
     * Open schedule management submenu.
     */
    private void manageSchedule() {
        scheduleMenu.show();
    }

    /**
     * Manage user's focus categories.
     */
    private void manageFocusCategories() {
        boolean managing = true;
        
        while (managing) {
            System.out.println("\n===== FOCUS CATEGORIES =====");
            System.out.println("1. View current categories");
            System.out.println("2. Add category");
            System.out.println("3. Remove category");
            System.out.println("4. Back to dashboard");
            
            int choice = InputValidator.readIntInRange("Enter choice: ", 1, 4);
            
            switch (choice) {
                case 1:
                    userService.displayCategories(currentUser);
                    InputValidator.pause();
                    break;
                case 2:
                    addCategory();
                    break;
                case 3:
                    removeCategory();
                    break;
                case 4:
                    managing = false;
                    break;
            }
        }
    }

    /**
     * Add a category to user's focus areas.
     */
    private void addCategory() {
        Category[] allCategories = Category.values();
        HashSet<Category> currentCategories = currentUser.getSelectedCategories();
        
        System.out.println("\n===== ADD CATEGORY =====");
        System.out.println("Available categories:");
        
        int index = 1;
        for (Category category : allCategories) {
            String status = currentCategories.contains(category) ? " [SELECTED]" : "";
            System.out.println(index++ + ". " + category.getDisplayName() + status);
        }
        
        int choice = InputValidator.readIntInRange("Select category: ", 1, 
                                                   allCategories.length);
        Category selectedCategory = allCategories[choice - 1];
        
        if (currentCategories.contains(selectedCategory)) {
            System.out.println("Category already selected.");
        } else {
            userService.addCategory(currentUser, selectedCategory);
        }
        
        InputValidator.pause();
    }

    /**
     * Remove a category from user's focus areas.
     */
    private void removeCategory() {
        HashSet<Category> categories = currentUser.getSelectedCategories();
        
        if (categories.isEmpty()) {
            System.out.println("\nNo categories selected.");
            InputValidator.pause();
            return;
        }
        
        System.out.println("\n===== REMOVE CATEGORY =====");
        Category[] categoryArray = categories.toArray(new Category[0]);
        
        for (int i = 0; i < categoryArray.length; i++) {
            System.out.println((i + 1) + ". " + categoryArray[i].getDisplayName());
        }
        
        int choice = InputValidator.readIntInRange("Select category to remove: ", 
                                                   1, categoryArray.length);
        userService.removeCategory(currentUser, categoryArray[choice - 1]);
        InputValidator.pause();
    }

    /**
     * Open habit management submenu.
     */
    private void manageHabits() {
        habitMenu.show();
    }

    /**
     * Display dashboard summary.
     */
    private void viewDashboardSummary() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║         DASHBOARD SUMMARY              ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        // Categories
        System.out.println("\n--- Focus Categories ---");
        if (currentUser.hasSelectedCategories()) {
            for (Category category : currentUser.getSelectedCategories()) {
                System.out.println("  ✓ " + category.getDisplayName());
            }
        } else {
            System.out.println("  No categories selected yet.");
        }
        
        // Habits summary
        System.out.println("\n--- Habits Summary ---");
        int totalHabits = currentUser.getHabits().size();
        int completedCount = 0;
        
        LocalDate today = AppContext.today();
        for (models.Habit habit : currentUser.getHabits()) {
            habit.resetIfNeeded(today);
            if (habit.isCompletedToday()) {
                completedCount++;
            }
        }
        
        System.out.println("  Total Habits: " + totalHabits);
        System.out.println("  Completed Today: " + completedCount);
        System.out.println("  Pending: " + (totalHabits - completedCount));
        
        if (totalHabits > 0) {
            double percentage = (completedCount * 100.0) / totalHabits;
            System.out.printf("  Progress: %.1f%%\n", percentage);
        }
        
        System.out.println("\n========================================");
        InputValidator.pause();
    }

    /**
     * View detailed statistics.
     */
    private void viewStatistics() {
        System.out.println(userService.getUserStatistics(currentUser));
        InputValidator.pause();
    }

    /**
     * Save user data and logout.
     */
    private void saveAndExit() {
        System.out.println("\nSaving your data...");
        if (authService.saveUserData(currentUser)) {
            System.out.println("Data saved successfully!");
        } else {
            System.out.println("Warning: Failed to save some data.");
        }
    }

    /**
     * Display about information.
     */
    private void about() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║          HABIT TRACKER v1.0            ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("\nA Java-based habit tracking application");
        System.out.println("demonstrating Object-Oriented Programming");
        System.out.println("principles including:");
        System.out.println("  • Classes & Objects");
        System.out.println("  • Encapsulation");
        System.out.println("  • Inheritance");
        System.out.println("  • Polymorphism");
        System.out.println("  • Abstraction");
        System.out.println("  • Composition");
        System.out.println("\nDeveloped for OOP final project evaluation.");
        System.out.println("========================================");
        InputValidator.pause();
    }

    /**
     * Confirm exit from application.
     * 
     * @return true if user confirms, false otherwise
     */
    private boolean confirmExit() {
        return InputValidator.readYesNo("\nAre you sure you want to exit?");
    }
}