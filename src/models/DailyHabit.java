package models;

import java.util.HashSet;



public class DailyHabit extends Habit {
    private static final long serialVersionUID = 1L;

    public DailyHabit(String habitName, HashSet<Category> categories, Frequency frequency) {
        super(habitName, categories, frequency);
    }

    @Override
    public String getHabitType() {
        return "Daily";
    }

    
    @Override // Method overriding (demonstrates polymorphism)
    public String getDisplayInfo() {
        return super.getDisplayInfo() + " (Every Day)";
    }
}

