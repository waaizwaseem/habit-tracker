package models;

import java.util.HashSet;

public class CustomHabit extends Habit {
    private static final long serialVersionUID = 1L;

    public CustomHabit(String habitName, HashSet<Category> categories, Frequency frequency) {
        super(habitName, categories, frequency);
    }

    @Override
    public String getHabitType() {
        return "Custom";
    }

    // Method overriding 
    @Override
    public String getDisplayInfo() {
        StringBuilder sb = new StringBuilder(super.getDisplayInfo());
        if (getFrequency() == Frequency.TIMES_PER_DAY) {
            String unit;
            switch (getRepeatCycle()) {
                case WEEKLY:
                    unit = "week";
                    break;
                case MONTHLY:
                    unit = "month";
                    break;
                case DAILY:
                default:
                    unit = "day";
                    break;
            }
            sb.append(" (")
              .append(getCompletionCount())
              .append("/")
              .append(getTimesPerDay())
              .append(" per ")
              .append(unit)
              .append(")");
        } else if (getFrequency() == Frequency.SPECIFIC_DAYS) {
            sb.append(" (").append(String.join(", ", getSpecificDays())).append(")");
        }
        return sb.toString();
    }
}