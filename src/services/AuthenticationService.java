// File: src/services/AuthenticationService.java
package services;

import models.User;
import persistence.UserRepository;
import utils.DateUtils;
import utils.PasswordHasher;

/**
 * Service for handling user authentication.
 * Demonstrates separation of concerns (business logic layer).
 */
public class AuthenticationService {
    
    // Private instance variable (Encapsulation)
    private UserRepository userRepository;

    // Constructor
    public AuthenticationService() {
        this.userRepository = new UserRepository();
    }

    public User register(String username, String password) {
        // Validate username
        if (!isValidUsername(username)) {
            System.out.println("Invalid username. Must be 3-20 characters, " +
                             "alphanumeric only.");
            return null;
        }

        // Validate password
        if (!isValidPassword(password)) {
            System.out.println("Invalid password. Must be at least 6 characters.");
            return null;
        }

        // Check if username already exists
        if (userRepository.userExists(username)) {
            System.out.println("Username already exists. Please choose another.");
            return null;
        }

        // Hash password and create user
        String hashedPassword = PasswordHasher.hashPassword(password);
        User newUser = new User(username, hashedPassword);
        newUser.setLastLoginDate(DateUtils.getCurrentDate());

        // Save user to file
        if (userRepository.saveUser(newUser)) {
            System.out.println("Registration successful!");
            return newUser;
        } else {
            System.out.println("Error saving user data.");
            return null;
        }
    }

    public User login(String username, String password) {
        // Load user from file
        User user = userRepository.loadUser(username);

        if (user != null) {
            user.ensureProgressInitialized();
        }

        if (user == null) {
            System.out.println("User not found.");
            return null;
        }

        // Verify password
        if (!PasswordHasher.verifyPassword(password, user.getHashedPassword())) {
            System.out.println("Incorrect password.");
            return null;
        }

        // Check if it's a new day and reset completions if needed
        String currentDate = DateUtils.getCurrentDate();
        if (!DateUtils.isToday(user.getLastLoginDate())) {
            user.resetDailyCompletions();
        }
        user.setLastLoginDate(currentDate);
        
        // Save updated login date
        userRepository.saveUser(user);

        System.out.println("Login successful! Welcome back, " + username + "!");
        return user;
    }

    private boolean isValidUsername(String username) {
        return username != null && 
               username.length() >= 3 && 
               username.length() <= 20 &&
               username.matches("[a-zA-Z0-9]+");
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public boolean saveUserData(User user) {
        return userRepository.saveUser(user);
    }
}