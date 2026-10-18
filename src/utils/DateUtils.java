// File: src/utils/DateUtils.java
package utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Utility class for date operations.
 * Demonstrates use of static utility methods.
 */
public class DateUtils {
    
    // Static constant
    private static final DateTimeFormatter DATE_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Private constructor to prevent instantiation (utility class)
    private DateUtils() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Get current date as a formatted string.
     * Static utility method.
     * 
     * @return Current date in yyyy-MM-dd format
     */
    public static String getCurrentDate() {
        return getCurrentLocalDate().format(DATE_FORMATTER);
    }

    /**
     * Get current day of week.
     * Static utility method.
     * 
     * @return Day of week (e.g., "Monday")
     */
    public static String getCurrentDayOfWeek() {
        return getDayName(getCurrentLocalDate());
    }

    /**
     * Get current date as LocalDate.
     * Uses AppContext so simulation mode can control the date.
     */
    public static LocalDate getCurrentLocalDate() {
        return AppContext.today();
    }

    /**
     * Returns a human-readable day name (e.g., "Monday") for a given date.
     */
    public static String getDayName(LocalDate date) {
        if (date == null) return "";
        return date.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    }

    /**
     * Check if two date strings represent the same day.
     * Static utility method.
     * 
     * @param date1 First date string
     * @param date2 Second date string
     * @return true if dates are the same, false otherwise
     */
    public static boolean isSameDay(String date1, String date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.equals(date2);
    }

    /**
     * Check if a date string is today.
     * Static utility method.
     * 
     * @param date Date string to check
     * @return true if date is today, false otherwise
     */
    public static boolean isToday(String date) {
        return isSameDay(date, getCurrentDate());
    }

    /**
     * Format a LocalDate object to string.
     * Static utility method.
     * 
     * @param date LocalDate object
     * @return Formatted date string
     */
    public static String formatDate(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }
}