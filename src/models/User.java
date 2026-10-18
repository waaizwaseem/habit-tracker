// File: src/models/User.java
package models;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class User implements Serializable { // Represents a user in the system.
    private static final long serialVersionUID = 1L;

    // Private data members (Encapsulation)
    private String username;
    private String hashedPassword;
    private HashSet<Category> selectedCategories;
    private HashMap<String, Schedule> schedules; // Day -> Schedule mapping
    private ArrayList<Habit> habits; // Composition: User HAS-A Habit
    private String lastLoginDate;

    // Gamification / Progress 
    private ExperienceSystem experienceSystem;
    private DailyStreakTracker dailyStreakTracker;
    private ProgressLog progressLog;

    private int streakSaves; // Streak-save currency: earned every 5 levels, can be used to preserve streaks if a day is missed.

    // Constructor
    public User(String username, String hashedPassword) {
        this.username = username;
        this.hashedPassword = hashedPassword;
        this.selectedCategories = new HashSet<>();
        this.schedules = new HashMap<>();
        this.habits = new ArrayList<>();
        this.lastLoginDate = "";
        initializeDefaultSchedules();

        // Initialize progress systems
        this.experienceSystem = new ExperienceSystem();
        this.dailyStreakTracker = new DailyStreakTracker();
        this.progressLog = new ProgressLog();

        this.streakSaves = 0;
    }

    private void initializeDefaultSchedules() {
        String[] daysOfWeek = {"Monday", "Tuesday", "Wednesday", "Thursday",
                               "Friday", "Saturday", "Sunday"};
        for (String day : daysOfWeek) {
            schedules.put(day, new Schedule(day));
        }
    }

    /**
     * Backward-compatibility safety: if older saved data is loaded, these fields
     * may be null. Call this after loading a user.
     */
    public void ensureProgressInitialized() {
        if (experienceSystem == null) experienceSystem = new ExperienceSystem();
        if (dailyStreakTracker == null) dailyStreakTracker = new DailyStreakTracker();
        if (progressLog == null) progressLog = new ProgressLog();
        if (streakSaves < 0) streakSaves = 0;
    }

    public int getStreakSaves() {
        if (streakSaves < 0) streakSaves = 0;
        return streakSaves;
    }

    public void addStreakSaves(int amount) {
        if (amount <= 0) return;
        streakSaves += amount;
    }

    /**
     * Consume N streak saves if available.
     *
     * @return true if consumed, false if not enough balance
     */
    public boolean consumeStreakSaves(int amount) {
        if (amount <= 0) return true;
        if (getStreakSaves() < amount) return false;
        streakSaves -= amount;
        return true;
    }

    /**
     * Award EXP to the user and grant streak saves for milestones.
     * Rule: Each 5 levels reached -> +1 streak save.
     *
     * @return number of streak saves earned by this EXP award
     */
    public int awardExp(int expAmount) {
        if (expAmount <= 0) return 0;

        ExperienceSystem xp = getExperienceSystem();
        int oldLevel = xp.getLevel();
        xp.addExp(expAmount);
        int newLevel = xp.getLevel();

        int earned = 0;
        for (int lvl = oldLevel + 1; lvl <= newLevel; lvl++) {
            if (lvl % 5 == 0) {
                streakSaves++;
                earned++;
            }
        }
        return earned;
    }

    // Getters (Encapsulation)
    public String getUsername() {
        return username;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public HashSet<Category> getSelectedCategories() {
        return new HashSet<>(selectedCategories);
    }

    public Schedule getSchedule(String day) {
        return schedules.get(day);
    }

    public HashMap<String, Schedule> getAllSchedules() {
        return new HashMap<>(schedules);
    }

    public ArrayList<Habit> getHabits() {
        return new ArrayList<>(habits);
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public ExperienceSystem getExperienceSystem() {
        if (experienceSystem == null) experienceSystem = new ExperienceSystem();
        return experienceSystem;
    }

    public DailyStreakTracker getDailyStreakTracker() {
        if (dailyStreakTracker == null) dailyStreakTracker = new DailyStreakTracker();
        return dailyStreakTracker;
    }

    public ProgressLog getProgressLog() {
        if (progressLog == null) progressLog = new ProgressLog();
        return progressLog;
    }

    // Setters (Encapsulation)
    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public void setSelectedCategories(HashSet<Category> selectedCategories) {
        this.selectedCategories = new HashSet<>(selectedCategories);
    }

    // Add a category
    public void addCategory(Category category) {
        selectedCategories.add(category);
    }

    // Remove a category
    public boolean removeCategory(Category category) {
        return selectedCategories.remove(category);
    }

    // Add a habit
    public void addHabit(Habit habit) {
        if (habit != null) {
            habits.add(habit);
        }
    }

    // Remove a habit by ID
    public boolean removeHabit(int habitId) {
        return habits.removeIf(h -> h.getHabitId() == habitId);
    }

    // Get habit by ID
    public Habit getHabitById(int habitId) {
        for (Habit habit : habits) {
            if (habit.getHabitId() == habitId) {
                return habit;
            }
        }
        return null;
    }

    // Check if user has selected any categories
    public boolean hasSelectedCategories() {
        return !selectedCategories.isEmpty();
    }

    // Reset all habit completions (called at start of new day)
    public void resetDailyCompletions() {
        LocalDate today = utils.AppContext.today();
        for (Habit habit : habits) {
            // Each habit decides whether it should reset (daily/weekly/monthly)
            habit.resetIfNeeded(today);
        }
    }

    @Override
    public String toString() {
        return "User: " + username + " (Categories: " + selectedCategories.size() +
               ", Habits: " + habits.size() + ")";
    }
}
