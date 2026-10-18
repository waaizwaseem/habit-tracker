// File: src/utils/InputValidator.java
package utils;

import java.util.Scanner;

/**
 * Utility class for validating and safely reading user input.
 * Demonstrates use of static utility methods.
 */
public class InputValidator {
    
    // Static Scanner instance for input reading
    private static final Scanner scanner = new Scanner(System.in);

    // Private constructor to prevent instantiation (utility class)
    private InputValidator() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static String readNonEmptyString(String prompt) { //reads a string that can not be empty
        String input;
        do {
            System.out.print(prompt);
            input = scanner.nextLine().trim(); //reads a line and removes irrelevant spaces.
            if (input.isEmpty()) {
                System.out.println("Input cannot be empty. Please try again."); //if statement in case input is empty
            }
        } while (input.isEmpty());
        return input;
    }

    public static int readIntInRange(String prompt, int min, int max) { //static method to input numbers
        int value;
        while (true) { //keeps looping until there isnt a valid input
            System.out.print(prompt);
            try { //the try is used to catch a numberformatexception as shown below
                value = Integer.parseInt(scanner.nextLine().trim()); //.trim removes unnecessary spaces that may result in an exception
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    /**
     * Read a yes/no answer from user.
     * Static utility method.
     * 
     * @param prompt The prompt to display
     * @return true for yes, false for no
     */
    public static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt + " (y/n): ");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                System.out.println("Please enter 'y' or 'n'.");
            }
        }
    }

    public static String readString(String prompt) { //reads strings from other classes
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Pause and wait for user to press Enter.
     * Static utility method.
     */
    public static void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Close the scanner (should be called when application exits).
     * Static utility method.
     */
    public static void closeScanner() {
        scanner.close();
    }
}