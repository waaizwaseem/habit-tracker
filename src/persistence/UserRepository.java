// File: src/persistence/UserRepository.java
package persistence;

import models.User;
import java.io.File;
import java.util.ArrayList;
import utils.AppContext;

/**
 * Repository for managing user persistence.
 * Demonstrates separation of concerns (data access layer).
 */
public class UserRepository {

    /**
     * Users directory is mode-dependent:
     * - NORMAL: data/users/
     * - SIMULATION: sim_data/users/
     */
    private String getUsersDirectory() {
        String dir = AppContext.dataDir() + "users/";
        FileHandler.ensureDirectoryExists(dir);
        return dir;
    }

    /**
     * Save a user to file.
     * 
     * @param user The user to save
     * @return true if successful, false otherwise
     */
    public boolean saveUser(User user) {
        String filePath = getUserFilePath(user.getUsername());
        return FileHandler.saveObject(user, filePath);
    }

    /**
     * Load a user from file by username.
     * 
     * @param username The username
     * @return User object, or null if not found
     */
    public User loadUser(String username) {
        String filePath = getUserFilePath(username);
        Object obj = FileHandler.loadObject(filePath);
        if (obj instanceof User) {
            User u = (User) obj;
            u.ensureProgressInitialized();
            return u;
        }
        return null;
    }

    /**
     * Check if a user exists.
     * 
     * @param username The username to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String username) {
        String filePath = getUserFilePath(username);
        return FileHandler.fileExists(filePath);
    }

    /**
     * Delete a user.
     * 
     * @param username The username
     * @return true if successful, false otherwise
     */
    public boolean deleteUser(String username) {
        String filePath = getUserFilePath(username);
        return FileHandler.deleteFile(filePath);
    }

    /**
     * Get all registered usernames.
     * 
     * @return List of usernames
     */
    public ArrayList<String> getAllUsernames() {
        ArrayList<String> usernames = new ArrayList<>();
        File directory = new File(getUsersDirectory());
        File[] files = directory.listFiles();
        
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".dat")) {
                    String username = file.getName().replace(".dat", "");
                    usernames.add(username);
                }
            }
        }
        
        return usernames;
    }

    /**
     * Get the file path for a user.
     * Private helper method.
     * 
     * @param username The username
     * @return File path
     */
    private String getUserFilePath(String username) {
        return getUsersDirectory() + username + ".dat";
    }
}