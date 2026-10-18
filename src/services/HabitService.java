// File: src/services/HabitService.java
package services;

import models.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import utils.DateUtils;
import utils.AppContext;

/**
 * Service for managing habits.
 * Demonstrates separation of concerns (business logic layer).
 */
public class HabitService {

    // Used to determine which days a habit is scheduled on.
    private final ScheduleService scheduleService = new ScheduleService();

    /**
     * A habit can be restricted to certain days in TWO ways:
     * 1) Frequency.SPECIFIC_DAYS (habit has a specificDays list)
     * 2) The user's Schedule (habit is assigned to one or more day time-slots)
     *
     * Rule: If a habit is scheduled on at least one day, it can ONLY be completed on those days.
     * Unscheduled habits can be completed any day (subject to their reset cycle).
     */
    private boolean isHabitAllowedOnDate(User user, Habit habit, LocalDate date) {
        if (user == null || habit == null || date == null) return false;

        String dayName = DateUtils.getDayName(date);

        // Restriction 1: explicit specific days on the habit itself
        if (habit.getFrequency() == Frequency.SPECIFIC_DAYS) {
            ArrayList<String> allowedDays = habit.getSpecificDays();
            if (allowedDays != null && !allowedDays.isEmpty() && !allowedDays.contains(dayName)) {
                return false;
            }
        }

        // Restriction 2: scheduled days
        HashSet<String> scheduledDays = scheduleService.getScheduledDaysForHabit(user, habit.getHabitId());
        if (!scheduledDays.isEmpty() && !scheduledDays.contains(dayName)) {
            return false;
        }

        return true;
    }

    /**
     * Create a new habit based on frequency type.
     * Demonstrates POLYMORPHISM (returns Habit reference to different child types).
     *
     * @param name Habit name
     * @param categories Categories for this habit
     * @param frequency Frequency type
     * @return Habit object (can be DailyHabit, WeeklyHabit, or CustomHabit)
     */
    public Habit createHabit(String name, HashSet<Category> categories,
                             Frequency frequency) {
        Habit newHabit;

        // Polymorphism: parent reference (Habit) holds child object
        switch (frequency) {
            case DAILY:
                newHabit = new DailyHabit(name, categories, frequency);
                break;
            case WEEKLY:
                newHabit = new WeeklyHabit(name, categories, frequency);
                break;
            default:
                newHabit = new CustomHabit(name, categories, frequency);
                break;
        }

        return newHabit;
    }

    /**
     * Add a habit to a user's habit list.
     *
     * @param user The user
     * @param habit The habit to add
     */
    public void addHabitToUser(User user, Habit habit) {
        user.addHabit(habit);
    }

    /**
     * Mark a habit as completed.
     * Integration with Subsystem 2:
     * - global daily streak (at least one habit completed per day)
     * - user-defined EXP reward per habit
     * - leveling system
     * - progress logging
     *
     * NOTE about TIMES_PER_DAY:
     * This awards EXP only once, when the habit becomes fully completed for the current cycle.
     */
    public void markHabitCompleted(User user, Habit habit) {
        if (user == null || habit == null) return;

        LocalDate today = AppContext.today();

        // Time-driven streak handling: apply missed-day resets/saves as soon as the date changes
        user.getDailyStreakTracker().syncToDate(user, today);

        // Day restriction: if the habit is scheduled for specific days (or uses SPECIFIC_DAYS),
        // block completion on other days.
        if (!isHabitAllowedOnDate(user, habit, today)) {
            HashSet<String> scheduledDays = scheduleService.getScheduledDaysForHabit(user, habit.getHabitId());
            if (!scheduledDays.isEmpty()) {
                System.out.println("This habit is scheduled for: " + scheduledDays + ". You can only complete it on those days.");
            } else if (habit.getFrequency() == Frequency.SPECIFIC_DAYS) {
                System.out.println("This habit can only be completed on: " + habit.getSpecificDays());
            } else {
                System.out.println("This habit cannot be completed today.");
            }
            return;
        }
        // Ensure the habit is in the correct cycle before applying progress
        habit.resetIfNeeded(today);

        // If the habit has sub-habits, require ALL of them to be completed before counting this as a completion.
        // This prevents the main habit from being considered "done" just because one sub-habit was marked.
        if (habit.hasSubHabits() && !habit.areAllSubHabitsCompleted()) {
            System.out.println("This habit has sub-habits. Complete all sub-habits first, then confirm the habit.");
            return;
        }

        boolean wasFullyCompleted = habit.isCompletedToday();
        habit.markCompleted();

        // Log an action for weekly scoreboard (counts each repetition)
        user.getProgressLog().logAction(habit.getHabitName(), today);

        // GLOBAL DAILY STREAK (with streak-save support)
        // Register that the user was active today (counts once per day)
        user.getDailyStreakTracker().registerActivity(today);

        // For count-based habits (TIMES_PER_DAY), reset sub-habits after each confirmed repetition,
        // unless the habit just became fully completed for the current cycle.
        if (habit.getFrequency() == Frequency.TIMES_PER_DAY && habit.hasSubHabits() && !habit.isCompletedToday()) {
            habit.resetSubHabitsProgress();
        }

        // EXP + log only when the habit becomes fully completed for its current cycle
        if (!wasFullyCompleted && habit.isCompletedToday()) {
            int expGained = RewardEngine.calculateExp(habit);
            int earnedSaves = user.awardExp(expGained);
            user.getProgressLog().logCompletion(habit.getHabitName(), expGained, today);
            System.out.println("Habit completed! +" + expGained + " EXP");

            if (earnedSaves > 0) {
                System.out.println("Milestone reward: +" + earnedSaves + " streak save(s)! Total: " + user.getStreakSaves());
            }
        } else {
            System.out.println("Habit progress updated.");
        }
    }

    /**
     * Register a daily activity and handle missed-day gaps using streak saves.
     *
     * Rule:
     * - If the user missed N full days since last active (gap), and the user has >= N streak saves,
     *   the program automatically consumes N saves to preserve the streak and increments it by 1.
     * - If not enough saves are available, the streak resets.
     */
    private void registerDailyActivityConsideringStreakSaves(User user, LocalDate today) {
        if (user == null || today == null) return;

        // Time-driven: apply missed-day resets/saves as soon as date changes
        user.getDailyStreakTracker().syncToDate(user, today);

        // Count the user as active today (increments once per day)
        user.getDailyStreakTracker().registerActivity(today);
    }

/**
     * Mark a sub-habit as completed.
     *
     * @param subHabit The sub-habit to mark
     */
    public void markSubHabitCompleted(SubHabit subHabit) {
        if (subHabit == null) return;
        if (subHabit.isCompletedToday()) {
            System.out.println("Sub-habit already completed.");
            return;
        }
        subHabit.markCompleted();
        System.out.println("Sub-habit marked as completed!");
    }

    /**
     * Get habits by category.
     */
    public ArrayList<Habit> getHabitsByCategory(User user, Category category) {
        ArrayList<Habit> filtered = new ArrayList<>();
        for (Habit habit : user.getHabits()) {
            if (habit.getCategories().contains(category)) {
                filtered.add(habit);
            }
        }
        return filtered;
    }

    /**
     * Get completed habits for today.
     */
    public ArrayList<Habit> getCompletedHabits(User user) {
        ArrayList<Habit> completed = new ArrayList<>();
        LocalDate today = AppContext.today();

        // Time-driven streak handling: apply missed-day resets/saves as soon as the date changes
        user.getDailyStreakTracker().syncToDate(user, today);
        for (Habit habit : user.getHabits()) {
            habit.resetIfNeeded(today);
            if (isHabitAllowedOnDate(user, habit, today) && habit.isCompletedToday()) {
                completed.add(habit);
            }
        }
        return completed;
    }

    /**
     * Get pending (not completed) habits for today.
     */
    public ArrayList<Habit> getPendingHabits(User user) {
        ArrayList<Habit> pending = new ArrayList<>();
        LocalDate today = AppContext.today();

        // Time-driven streak handling: apply missed-day resets/saves as soon as the date changes
        user.getDailyStreakTracker().syncToDate(user, today);
        for (Habit habit : user.getHabits()) {
            habit.resetIfNeeded(today);
            if (isHabitAllowedOnDate(user, habit, today) && !habit.isCompletedToday()) {
                pending.add(habit);
            }
        }
        return pending;
    }

    /**
     * Calculate completion percentage.
     */
    public double getCompletionPercentage(User user) {
        ArrayList<Habit> allHabits = user.getHabits();
        if (allHabits.isEmpty()) {
            return 0.0;
        }

        int completed = 0;
        int totalConsidered = 0;
        LocalDate today = AppContext.today();

        // Time-driven streak handling: apply missed-day resets/saves as soon as the date changes
        user.getDailyStreakTracker().syncToDate(user, today);
        for (Habit habit : allHabits) {
            habit.resetIfNeeded(today);
            if (!isHabitAllowedOnDate(user, habit, today)) continue;
            totalConsidered++;
            if (habit.isCompletedToday()) completed++;
        }

        if (totalConsidered == 0) return 0.0;
        return (completed * 100.0) / totalConsidered;
    }

    /**
     * Delete a habit from user.
     */
    public boolean deleteHabit(User user, int habitId) {
        return user.removeHabit(habitId);
    }
}
