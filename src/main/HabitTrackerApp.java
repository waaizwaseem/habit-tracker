package main; //defines this class as main and allows any other class to import it

import java.time.LocalDate; //import for any date related actions, like in sim mode, for streaks etc
import ui.MainMenu; //class imported from ui folder for main menu functions
import utils.AppContext; //tells the code what mode is on and what today is
import utils.AppMode; //defines what mode the program can have
import utils.InputValidator; //one singular class to check and verify input

//classes are imported like this because all the files are in different folders, else we wouldn't need to import them
//enums are special java types that represent a fixed set of name constants. the category and appmode files are enums

public class HabitTrackerApp { 
        private static void selectMode() { //displays the initial option for the user to choose the mode
        System.out.println("\nSelect mode:\n1. Normal Mode \n2. Simulation Mode");
        int choice = InputValidator.readIntInRange("Enter choice: ", 1, 2);

        if (choice == 2) {
            String raw = InputValidator.readString("Enter starting simulated date (yyyy-MM-dd) or press Enter for today: ");
            LocalDate start = null; //localdate is a class that is imported and can have objects and ref variables, just like scanner.
            if (raw != null && !raw.trim().isEmpty()) { /*null = nothing. =2
                ! is the not equal to op. isEmpty() checks if empty, and the 
                ! before raw changes the value.*/
                try {
                    start = LocalDate.parse(raw.trim()); //the parse() function is from the localdate file, reading a date from a string.
                } catch (Exception e) { //exception handling for an invalid date
                    System.out.println("Invalid date; using today's real date instead.");
                }
            }
            if (start == null) start = LocalDate.now(); //uses today's date
            AppContext.enableSimulation(start); //passes the date into the enablesim method
        } else {
            AppContext.setMode(AppMode.NORMAL); //sets the program's mode to normal
        }
    }
    public static void main(String[] args) {  //method to run the program
        try {
            selectMode(); //triggers the method above

            MainMenu mainMenu = new MainMenu();
            mainMenu.run();
            
        } catch (Exception e) {
            System.err.println("\nAn unexpected error occurred:");
            e.printStackTrace();
            System.err.println("\nPlease report this issue.");
            
        } finally {
            InputValidator.closeScanner();
        }
    }
}