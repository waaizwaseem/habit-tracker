// File: src/services/UserService.java
package services;

import models.Category;
import models.Habit;
import models.User;
import utils.AppContext;
import utils.DateUtils;

import java.time.LocalDate;
import java.util.HashSet;

/**
 * Service for managing user-related operations.
 * Demonstrates separation of concerns (business logic layer).
 */
public class UserService {

    public void addCategory(User user, Category category) {
        if (user == null || category == null) return;
        user.addCategory(category);
    }

    public void removeCategory(User user, Category category) {
        if (user == null || category == null) return;
        user.removeCategory(category);
    }

    public void setCategories(User user, HashSet<Category> categories) {
        if (user == null || categories == null) return;
        user.setSelectedCategories(categories);
    }

    public void displayCategories(User user) {
        if (user == null) return;

        if (!user.hasSelectedCategories()) {
            System.out.println("No focus categories selected.");
            return;
        }
        System.out.println("Selected focus categories: " + user.getSelectedCategories());
    }

    public boolean hasCategories(User user) {
        return user != null && user.hasSelectedCategories();
    }

    /**
     * Get user statistics (includes Subsystem 2 progression).
     */
    public String getUserStatistics(User user) {
        if (user == null) return "No user loaded.";

        LocalDate today = AppContext.today();

        int totalHabits = (user.getHabits() == null) ? 0 : user.getHabits().size();
        int completedToday = 0;

        if (user.getHabits() != null) {
            for (Habit h : user.getHabits()) {
                if (h == null) continue;
                h.resetIfNeeded(today);
                if (h.isCompletedToday()) completedToday++;
            }
        }

        StringBuilder stats = new StringBuilder();
        stats.append("Date: ").append(DateUtils.getCurrentDate()).append("\n");
        stats.append("Day: ").append(DateUtils.getCurrentDayOfWeek()).append("\n");
        stats.append("Habits Completed Today: ").append(completedToday).append("/").append(totalHabits).append("\n");

        // Subsystem 2 progression
        user.ensureProgressInitialized();
        stats.append("Level: ").append(user.getExperienceSystem().getLevel()).append("\n");
        stats.append("EXP:   ").append(user.getExperienceSystem().getProgressBar()).append("\n");
        stats.append("Streak: ").append(user.getDailyStreakTracker().getDailyStreak()).append(" day(s)\n");
        stats.append("Streak Saves: ").append(user.getStreakSaves()).append("\n");

        return stats.toString();
    }

    /**
     * Display weekly scoreboard (habit action frequency) for the current ISO week (Mon-Sun).
     * Intended to be accessible on Sundays.
     */
    public void displayWeeklyScoreboard(User user) {
        if (user == null) return;

        // Ensure new progress structures exist (important for older save files)
        user.ensureProgressInitialized();

        LocalDate today = DateUtils.getCurrentLocalDate();
        LocalDate weekStart = today.minusDays(today.getDayOfWeek().getValue() - 1);

        System.out.println("\n===== WEEKLY SCOREBOARD (Mon-Sun) =====");
        System.out.println("Week starting: " + DateUtils.formatDate(weekStart));
        System.out.println("---------------------------------------");

        if (user.getHabits() == null || user.getHabits().isEmpty()) {
            System.out.println("(No habits created)");
            return;
        }

        for (Habit h : user.getHabits()) {
            if (h == null) continue;
            String name = h.getHabitName();
            int count = user.getProgressLog().getActionCountForHabitThisWeek(name, today);
            System.out.printf("%-25s : %d\n", name, count);
        }
    }
}
