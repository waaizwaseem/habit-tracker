// File: src/persistence/ScheduleRepository.java
package persistence;

import models.Schedule;
import models.User;
import utils.AppContext;

/**
 * Repository for managing schedule persistence.
 * Demonstrates separation of concerns (data access layer).
 * 
 * Note: In this implementation, schedules are stored as part of the User object,
 * so this class provides helper methods for schedule-specific operations.
 */
public class ScheduleRepository {
    
    private String getScheduleDir() {
        String dir = AppContext.dataDir() + "schedules/";
        FileHandler.ensureDirectoryExists(dir);
        return dir;
    }
    
    // Reference to user repository
    private UserRepository userRepository;

    // Constructor
    public ScheduleRepository() {
        this.userRepository = new UserRepository();
        FileHandler.ensureDirectoryExists(getScheduleDir());
    }

    /**
     * Save a user's schedules (saves the entire user object).
     * 
     * @param user The user whose schedules to save
     * @return true if successful, false otherwise
     */
    public boolean saveUserSchedules(User user) {
        return userRepository.saveUser(user);
    }

    /**
     * Load a user's schedules (loads the entire user object).
     * 
     * @param username The username
     * @return User object with schedules, or null if not found
     */
    public User loadUserWithSchedules(String username) {
        return userRepository.loadUser(username);
    }

    /**
     * Get a schedule for a specific day.
     * 
     * @param user The user
     * @param day The day of week
     * @return Schedule object for that day
     */
    public Schedule getScheduleForDay(User user, String day) {
        return user.getSchedule(day);
    }

    /**
     * Save a specific schedule (saves the entire user).
     * 
     * @param user The user
     * @param day The day of week
     * @param schedule The schedule to save
     * @return true if successful, false otherwise
     */
    public boolean saveSchedule(User user, String day, Schedule schedule) {
        // The schedule is already part of the user object
        // Just save the user
        return userRepository.saveUser(user);
    }
}