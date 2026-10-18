// File: src/ui/SimulationMenu.java
package ui;

import models.DailyStreakTracker;
import models.User;
import utils.AppContext;
import utils.DateUtils;
import utils.InputValidator;

import java.time.LocalDate;

/**
 * Simulation controls (only available in SIMULATION mode).
 * Lets you advance the app date without waiting in real time, to demonstrate streak logic.
 */
public class SimulationMenu {

    private final User currentUser;

    public SimulationMenu(User currentUser) {
        this.currentUser = currentUser;
    }

    public void show() {
        if (!AppContext.isSimulationMode()) {
            System.out.println("Simulation controls are only available in SIMULATION mode.");
            InputValidator.pause();
            return;
        }

        boolean running = true;
        while (running) {
            displayHeader();
            displayMenu();

            int choice = InputValidator.readIntInRange("Enter choice: ", 1, 5);
            switch (choice) {
                case 1:
                    advanceDays(1);
                    break;
                case 2:
                    advanceDays(7);
                    break;
                case 3:
                    setDate();
                    break;
                case 4:
                    showStreakDiagnostics();
                    break;
                case 5:
                    running = false;
                    break;
            }
        }
    }

    private void displayHeader() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║           SIMULATION CONTROLS          ║");
        System.out.println("╚════════════════════════════════════════╝");
        System.out.println("Simulated Date: " + DateUtils.getCurrentDate());
        System.out.println("Day: " + DateUtils.getCurrentDayOfWeek());
        System.out.println("User: " + currentUser.getUsername());
    }

    private void displayMenu() {
        System.out.println("\n===== SIM MENU =====");
        System.out.println("1. Advance 1 day");
        System.out.println("2. Advance 7 days");
        System.out.println("3. Set date (yyyy-MM-dd)");
        System.out.println("4. Show streak diagnostics");
        System.out.println("5. Back");
        System.out.println("====================");
    }

    private void advanceDays(int days) {
        AppContext.advanceDays(days);

        // Reset habit cycles if needed for the new date
        currentUser.resetDailyCompletions();
            currentUser.getDailyStreakTracker().syncToDate(currentUser, AppContext.today());

        // Apply time-driven streak updates for the new simulated date
        currentUser.getDailyStreakTracker().syncToDate(currentUser, AppContext.today());

        System.out.println("Advanced simulation by " + days + " day(s).");
        System.out.println("Now: " + DateUtils.getCurrentDate() + " (" + DateUtils.getCurrentDayOfWeek() + ")");
        InputValidator.pause();
    }

    private void setDate() {
        String raw = InputValidator.readString("Enter new simulated date (yyyy-MM-dd): ");
        try {
            LocalDate newDate = LocalDate.parse(raw);
            AppContext.setSimulatedDate(newDate);
            currentUser.resetDailyCompletions();

        // Apply time-driven streak updates for the new simulated date
        currentUser.getDailyStreakTracker().syncToDate(currentUser, AppContext.today());
            System.out.println("Simulated date set to: " + DateUtils.getCurrentDate());
        } catch (Exception e) {
            System.out.println("Invalid date format. Example: 2026-01-02");
        }
        InputValidator.pause();
    }

    private void showStreakDiagnostics() {
        DailyStreakTracker tracker = currentUser.getDailyStreakTracker();
        LocalDate today = AppContext.today();

        // Ensure the streak state reflects missed-day resets/saves up to today
        tracker.syncToDate(currentUser, today);

        LocalDate chainDate = tracker.getLastActiveDate();

        System.out.println("\n===== STREAK DIAGNOSTICS =====");
        System.out.println("Today: " + today + " (" + DateUtils.getCurrentDayOfWeek() + ")");
        System.out.println("Chain Date: " + (chainDate == null ? "(none)" : chainDate.toString()));
        System.out.println("Current Streak: " + tracker.getDailyStreak() + " day(s)");
        System.out.println("Streak Saves: " + currentUser.getStreakSaves());

        if (tracker.getDailyStreak() <= 0) {
            System.out.println("Status: No active streak (it may have reset due to missed days). ");
        } else if (chainDate != null && chainDate.equals(today)) {
            System.out.println("Status: You have already been active today (streak won't increase again today). ");
        } else {
            System.out.println("Status: Streak is active. Completing any habit today will count as activity for today.");
        }

        InputValidator.pause();
    }
}
