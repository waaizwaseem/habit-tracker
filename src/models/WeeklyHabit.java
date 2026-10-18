package models;

import java.util.HashSet;

/**
 * Concrete habit class for weekly habits.
 * Demonstrates INHERITANCE (extends Habit).
 */
public class WeeklyHabit extends Habit {
    private static final long serialVersionUID = 1L;

    public WeeklyHabit(String habitName, HashSet<Category> categories, Frequency frequency) {
        super(habitName, categories, frequency);
    }

    @Override
    public String getHabitType() {
        return "Weekly";
    }

    // Method overriding (demonstrates polymorphism)
    @Override
    public String getDisplayInfo() {
        return super.getDisplayInfo() + " (Once per Week)";
    }
}


