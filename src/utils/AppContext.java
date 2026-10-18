// File: src/utils/AppContext.java
package utils;

import java.time.LocalDate;

/**
 * Central application context.
 *
 * Responsibilities:
 * - Provide a single source of truth for "today" (real date in NORMAL mode, simulated date in SIMULATION mode)
 * - Provide a mode-specific data directory so simulation data never mixes with real data
 */
public final class AppContext {

    private static AppMode mode = AppMode.NORMAL; //default setting of mode to normal
    private static LocalDate simulatedDate = LocalDate.now();

    private AppContext() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    public static AppMode getMode() {
        return mode;
    }

    public static boolean isSimulationMode() {
       return mode == AppMode.SIMULATION;
    }

    public static void setMode(AppMode newMode) { //for setting mode through another class
        if (newMode == null) return;  
        mode = newMode; 
    }

    public static void enableSimulation(LocalDate startDate) {
        mode = AppMode.SIMULATION; //sets mode to simulation
        if (startDate != null) { //if there is no start date it takes today's date
            simulatedDate = startDate;
        }
    }

    /**
     * Return "today" according to the current mode.
     */
    public static LocalDate today() {
        return isSimulationMode() ? simulatedDate : LocalDate.now();
    }

    /**
     * Set simulated date (only works in simulation mode).
     */
    public static void setSimulatedDate(LocalDate date) {
        if (!isSimulationMode() || date == null) return;
        simulatedDate = date;
    }

    /**
     * Advance simulated date by N days (only works in simulation mode).
     */
    public static void advanceDays(int days) {
        if (!isSimulationMode()) return;
        simulatedDate = simulatedDate.plusDays(days);
    }

    /**
     * Data directory base for persistence.
     * NORMAL -> "data/"
     * SIMULATION -> "sim_data/"
     */
    public static String dataDir() {
        return isSimulationMode() ? "sim_data/" : "data/";
    }

    public static String getModeDisplayName() {
        return isSimulationMode() ? "SIMULATION" : "NORMAL";
    }
}
