// File: src/models/ProgressLog.java
package models;

import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores a history of daily completions (full-cycle) and EXP gained,
 * plus daily ACTION counts (each time the user performs a habit repetition).
 */
public class ProgressLog implements Serializable {
    private static final long serialVersionUID = 1L;

    // Full-cycle completions (for achievements / EXP reporting)
    private Map<LocalDate, List<String>> dailyCompletions;

    // EXP gained per day
    private Map<LocalDate, Integer> dailyExp;

    // Action counts per day: habitName -> count
    private Map<LocalDate, Map<String, Integer>> dailyActions;

    public ProgressLog() {
        dailyCompletions = new HashMap<>();
        dailyExp = new HashMap<>();
        dailyActions = new HashMap<>();
    }

    private void ensureInitialized() {
        if (dailyCompletions == null) dailyCompletions = new HashMap<>();
        if (dailyExp == null) dailyExp = new HashMap<>();
        if (dailyActions == null) dailyActions = new HashMap<>();
    }

    public void logCompletion(String habitName, int expGained, LocalDate date) {
        if (habitName == null || habitName.isBlank() || date == null) return;
        ensureInitialized();

        dailyCompletions.computeIfAbsent(date, d -> new ArrayList<>()).add(habitName);
        dailyExp.put(date, dailyExp.getOrDefault(date, 0) + Math.max(0, expGained));
    }

    /** Convenience overload using AppContext.today(). */
    public void logCompletion(String habitName, int expGained) {
        logCompletion(habitName, expGained, utils.AppContext.today());
    }

    /**
     * Log an ACTION for a habit (each time the user performs the habit in a day).
     * This is used for the weekly scoreboard.
     */
    public void logAction(String habitName, LocalDate date) {
        if (habitName == null || habitName.isBlank() || date == null) return;
        ensureInitialized();

        Map<String, Integer> map = dailyActions.computeIfAbsent(date, d -> new HashMap<>());
        map.put(habitName, map.getOrDefault(habitName, 0) + 1);
    }

    public int getExpForDate(LocalDate date) {
        if (date == null) return 0;
        ensureInitialized();
        return dailyExp.getOrDefault(date, 0);
    }

    public int getCompletionCount(LocalDate date) {
        if (date == null) return 0;
        ensureInitialized();
        List<String> list = dailyCompletions.get(date);
        return list == null ? 0 : list.size();
    }

    public int getActionCountForHabitOnDate(String habitName, LocalDate date) {
        if (habitName == null || habitName.isBlank() || date == null) return 0;
        ensureInitialized();

        Map<String, Integer> map = dailyActions.get(date);
        if (map == null) return 0;
        return map.getOrDefault(habitName, 0);
    }

    /**
     * Returns the action count for a habit for the ISO week containing referenceDate.
     * Week is Monday..Sunday.
     */
    public int getActionCountForHabitThisWeek(String habitName, LocalDate referenceDate) {
        if (habitName == null || habitName.isBlank() || referenceDate == null) return 0;
        ensureInitialized();

        LocalDate start = referenceDate.minusDays(referenceDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue());
        LocalDate end = start.plusDays(6);

        int total = 0;
        LocalDate d = start;
        while (!d.isAfter(end)) {
            total += getActionCountForHabitOnDate(habitName, d);
            d = d.plusDays(1);
        }
        return total;
    }
}
