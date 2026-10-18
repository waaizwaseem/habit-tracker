// File: src/persistence/HabitRepository.java
package persistence;

import models.Habit;
import models.User;
import java.util.ArrayList;
import utils.AppContext;

/**
 * Repository for managing habit persistence.
 * Demonstrates separation of concerns (data access layer).
 * 
 * Note: In this implementation, habits are stored as part of the User object,
 * so this class provides helper methods for habit-specific operations.
 */
public class HabitRepository {
    
    private String getHabitsDirectory() {
        String dir = AppContext.dataDir() + "habits/";
        FileHandler.ensureDirectoryExists(dir);
        return dir;
    }
    
    // Reference to user repository
    private UserRepository userRepository;

    // Constructor
    public HabitRepository() {
        this.userRepository = new UserRepository();
        FileHandler.ensureDirectoryExists(getHabitsDirectory());
    }

    /**
     * Save a user's habits (saves the entire user object).
     * 
     * @param user The user whose habits to save
     * @return true if successful, false otherwise
     */
    public boolean saveUserHabits(User user) {
        return userRepository.saveUser(user);
    }

    /**
     * Load a user's habits (loads the entire user object).
     * 
     * @param username The username
     * @return User object with habits, or null if not found
     */
    public User loadUserWithHabits(String username) {
        return userRepository.loadUser(username);
    }

    /**
     * Get all habits for a user.
     * 
     * @param user The user
     * @return List of habits
     */
    public ArrayList<Habit> getAllHabits(User user) {
        return user.getHabits();
    }

    /**
     * Get a specific habit by ID.
     * 
     * @param user The user
     * @param habitId The habit ID
     * @return Habit object, or null if not found
     */
    public Habit getHabitById(User user, int habitId) {
        return user.getHabitById(habitId);
    }

    /**
     * Delete a habit from a user.
     * 
     * @param user The user
     * @param habitId The habit ID
     * @return true if successful, false otherwise
     */
    public boolean deleteHabit(User user, int habitId) {
        boolean deleted = user.removeHabit(habitId);
        if (deleted) {
            userRepository.saveUser(user);
        }
        return deleted;
    }
}