// File: src/ui/AuthMenu.java
package ui;

import models.User;
import services.AuthenticationService;
import utils.InputValidator;

/**
 * UI for authentication (login and registration).
 * Demonstrates separation of concerns (UI layer).
 */
public class AuthMenu {
    
    // Private instance variable (Encapsulation)
    private AuthenticationService authService;

    // Constructor
    public AuthMenu() {
        this.authService = new AuthenticationService();
    }

    public User showRegistrationForm() { 
        System.out.println("\n===== USER REGISTRATION =====");
        
        String username = InputValidator.readNonEmptyString("Enter username: ");
        String password = InputValidator.readNonEmptyString("Enter password: ");
        String confirmPassword = InputValidator.readNonEmptyString("Confirm password: ");

        // Check if passwords match
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");
            return null;
        }

        // Attempt registration
        return authService.register(username, password);
    }

    /**
     * Display login form and authenticate user.
     * 
     * @return Authenticated User object, or null if login fails
     */
    public User showLoginForm() {
        System.out.println("\n===== USER LOGIN =====");
        
        String username = InputValidator.readNonEmptyString("Enter username: ");
        String password = InputValidator.readNonEmptyString("Enter password: ");

        // Attempt login
        return authService.login(username, password);
    }

    /**
     * Get the authentication service.
     * 
     * @return AuthenticationService instance
     */
    public AuthenticationService getAuthService() {
        return authService;
    }
}