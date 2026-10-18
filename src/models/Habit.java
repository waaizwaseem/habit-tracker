// File: src/models/Habit.java
package models;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Abstract base class for all habits.
 * Demonstrates: Abstraction, Encapsulation, Composition (has SubHabits).
 */
public abstract class Habit implements Serializable, Rewardable {
    private static final long serialVersionUID = 1L;

    // Static counter for generating unique IDs
    private static int idCounter = 1000;

    // Private data members (Encapsulation)
    private int habitId;
    private String habitName;
    private HashSet<Category> categories;
    private Frequency frequency;
    // For count-based habits (Frequency.TIMES_PER_DAY): required completions per cycle.
    // The cycle (daily/weekly/monthly) is stored in repeatCycle.
    private int timesPerDay;
    private RepeatCycle repeatCycle; // Only used when frequency is TIMES_PER_DAY
    private ArrayList<String> specificDays; // Only used when frequency is SPECIFIC_DAYS
    private ArrayList<SubHabit> subHabits; // Composition: Habit HAS-A SubHabit
    private boolean completedToday;
    private int completionCount; // For TIMES_PER_DAY tracking

    // Tracks when this habit was last reset for its effective cycle.
    private LocalDate lastResetDate;

    // User-defined EXP reward for completing this habit (gamification)
    private int baseExp;

    // Constructor
    public Habit(String habitName, HashSet<Category> categories, Frequency frequency) {
        this.habitId = generateId();
        this.habitName = habitName;
        this.categories = new HashSet<>(categories);
        this.frequency = frequency;
        this.subHabits = new ArrayList<>();
        this.specificDays = new ArrayList<>();
        this.completedToday = false;
        this.completionCount = 0;
        this.timesPerDay = 1;
        this.repeatCycle = RepeatCycle.DAILY;
        this.lastResetDate = utils.AppContext.today();
        this.baseExp = 5; // default for backward compatibility
    }

    // Static method to generate unique IDs
    private static synchronized int generateId() {
        return idCounter++;
    }

    // Getters (Encapsulation)
    public int getHabitId() {
        return habitId;
    }

    public String getHabitName() {
        return habitName;
    }

    public HashSet<Category> getCategories() {
        return new HashSet<>(categories);
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public int getTimesPerDay() {
        return timesPerDay;
    }

    public RepeatCycle getRepeatCycle() {
        return repeatCycle == null ? RepeatCycle.DAILY : repeatCycle;
    }

    public LocalDate getLastResetDate() {
        return lastResetDate;
    }

    public ArrayList<String> getSpecificDays() {
        return new ArrayList<>(specificDays);
    }

    public ArrayList<SubHabit> getSubHabits() {
        return new ArrayList<>(subHabits);
    }

    public boolean isCompletedToday() {
        return completedToday;
    }

    public int getCompletionCount() {
        return completionCount;
    }

    /**
     * User-defined base EXP for this habit.
     * If older saved data has 0, fallback to 5.
     */
    @Override
    public int getBaseExp() {
        return baseExp > 0 ? baseExp : 5;
    }

    // Setters (Encapsulation)
    public void setHabitName(String habitName) {
        this.habitName = habitName;
    }

    public void setTimesPerDay(int timesPerDay) {
        this.timesPerDay = Math.max(timesPerDay, 1);
    }

    public void setRepeatCycle(RepeatCycle repeatCycle) {
        this.repeatCycle = (repeatCycle == null) ? RepeatCycle.DAILY : repeatCycle;
    }

    public void setSpecificDays(ArrayList<String> specificDays) {
        this.specificDays = new ArrayList<>(specificDays);
    }

    public void setCompletedToday(boolean completedToday) {
        this.completedToday = completedToday;
    }

    public void setBaseExp(int baseExp) {
        this.baseExp = Math.max(baseExp, 1);
    }

    // Add a sub-habit
    public void addSubHabit(SubHabit subHabit) {
        if (subHabit != null) {
            subHabits.add(subHabit);
        }
    }

    public boolean hasSubHabits() {
        return subHabits != null && !subHabits.isEmpty();
    }

    public boolean areAllSubHabitsCompleted() {
        if (subHabits == null || subHabits.isEmpty()) return true;
        for (SubHabit sh : subHabits) {
            if (sh != null && !sh.isCompletedToday()) {
                return false;
            }
        }
        return true;
    }

    public void markAllSubHabitsCompleted() {
        if (subHabits == null) return;
        for (SubHabit sh : subHabits) {
            if (sh != null) sh.markCompleted();
        }
    }

    public void resetSubHabitsProgress() {
        if (subHabits == null) return;
        for (SubHabit sh : subHabits) {
            if (sh != null) sh.resetCompletion();
        }
    }


    // Mark habit as completed
    public void markCompleted() {
        resetIfNeeded(utils.AppContext.today());
        if (frequency == Frequency.TIMES_PER_DAY) {
            completionCount++;
            if (completionCount >= timesPerDay) {
                completedToday = true;
            }
        } else {
            completedToday = true;
        }
    }

    /**
     * Reset completion state if the habit's effective cycle has changed.
     * - DAILY resets when the date changes
     * - WEEKLY resets when the ISO week changes
     * - MONTHLY resets when the month changes
     */
    public void resetIfNeeded(LocalDate today) {
        if (today == null) return;

        // Backward compatibility: older serialized objects may have null fields.
        if (repeatCycle == null) repeatCycle = RepeatCycle.DAILY;
        if (lastResetDate == null) {
            lastResetDate = today;
            return;
        }

        RepeatCycle effectiveCycle = getEffectiveCycle();
        boolean shouldReset;

        switch (effectiveCycle) {
            case WEEKLY: {
                WeekFields wf = WeekFields.ISO;
                int w1 = lastResetDate.get(wf.weekOfWeekBasedYear());
                int y1 = lastResetDate.get(wf.weekBasedYear());
                int w2 = today.get(wf.weekOfWeekBasedYear());
                int y2 = today.get(wf.weekBasedYear());
                shouldReset = (w1 != w2) || (y1 != y2);
                break;
            }
            case MONTHLY:
                shouldReset = lastResetDate.getYear() != today.getYear() ||
                              lastResetDate.getMonthValue() != today.getMonthValue();
                break;
            case DAILY:
            default:
                shouldReset = !lastResetDate.equals(today);
                break;
        }

        if (shouldReset) {
            resetCompletion();
            lastResetDate = today;
        }
    }

    private RepeatCycle getEffectiveCycle() {
        if (frequency == Frequency.TIMES_PER_DAY) {
            return getRepeatCycle();
        }
        if (frequency == Frequency.WEEKLY) {
            return RepeatCycle.WEEKLY;
        }
        return RepeatCycle.DAILY;
    }

    // Reset completion status (for new day)
    public void resetCompletion() {
        this.completedToday = false;
        this.completionCount = 0;
        for (SubHabit subHabit : subHabits) {
            subHabit.resetCompletion();
        }
    }

    // Abstract method - must be implemented by subclasses (demonstrates abstraction)
    public abstract String getHabitType();

    // Method to be overridden (demonstrates method overriding)
    public String getDisplayInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(habitName).append(" [").append(getHabitType()).append("]");
        sb.append(" - ").append(frequency.getDisplayName());
        sb.append(" (+").append(getBaseExp()).append(" EXP)");
        if (completedToday) {
            sb.append(" ✓");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return getDisplayInfo();
    }
}
