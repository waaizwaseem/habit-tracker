// File: src/ui/MainMenu.java
package ui;

import models.User;
import services.AuthenticationService;
import utils.InputValidator;

/**
 * Main menu for the application (entry point UI).
 * Demonstrates separation of concerns (UI layer - no business logic).
 */
public class MainMenu {
    
    // Private instance variables (Encapsulation)
    private AuthMenu authMenu;
    private AuthenticationService authService;

    // Constructor
    public MainMenu() {
        this.authMenu = new AuthMenu();
        this.authService = authMenu.getAuthService();
    }

    /**
     * Display and run the main menu.
     */
    public void run() {
        displayWelcome();
        
        boolean running = true;
        
        while (running) {
            displayMenu();
            
            int choice = InputValidator.readIntInRange("Enter choice: ", 1, 3);
            
            switch (choice) {
                case 1:
                    handleRegistration();
                    break;
                case 2:
                    running = handleLogin();
                    break;
                case 3:
                    if (confirmExit()) {
                        running = false;
                    }
                    break;
            }
        }
        
        displayGoodbye();
    }

    private void displayWelcome() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║     WELCOME TO HABIT TRACKER APP       ║");
        System.out.println("║                                        ║");
        System.out.println("║  Build Better Habits, One Day at a Time║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private void displayMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        System.out.println("=====================");
    }

    private void handleRegistration() {
        User newUser = authMenu.showRegistrationForm();
        
        if (newUser != null) {
            System.out.println("\nAccount created successfully!");
            System.out.println("Please login with your credentials.");
            InputValidator.pause();
        }
    }

    /**
     * Handle user login.
     * 
     * @return false if user logs out, true if application should exit
     */
    private boolean handleLogin() {
        User user = authMenu.showLoginForm();
        
        if (user != null) {
            // User logged in successfully, show dashboard
            DashboardMenu dashboard = new DashboardMenu(user, authService);
            return !dashboard.show(); // Returns true if exiting, false if logging out
        }
        
        return true; // Continue main menu loop
    }

    /**
     * Confirm exit from application.
     * 
     * @return true if user confirms, false otherwise
     */
    private boolean confirmExit() {
        return InputValidator.readYesNo("\nAre you sure you want to exit?");
    }

    /**
     * Display goodbye message.
     */
    private void displayGoodbye() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   Thank you for using Habit Tracker!   ║");
        System.out.println("║                                        ║");
        System.out.println("║     Keep building those good habits!   ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
}