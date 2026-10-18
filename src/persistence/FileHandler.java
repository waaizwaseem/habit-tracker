// File: src/persistence/FileHandler.java
package persistence;

import java.io.*;

/**
 * Central file handling utility for reading and writing objects.
 * Demonstrates use of static utility methods for file I/O.
 */
public class FileHandler {
    
    // Private constructor to prevent instantiation (utility class)
    private FileHandler() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Save an object to a file using serialization.
     * Static utility method.
     * 
     * @param object The object to save
     * @param filePath The file path
     * @return true if successful, false otherwise
     */
    public static boolean saveObject(Object object, String filePath) {
        try {
            // Ensure directory exists
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            // Write object to file
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(file))) {
                oos.writeObject(object);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error saving to file: " + filePath);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load an object from a file using deserialization.
     * Static utility method.
     * 
     * @param filePath The file path
     * @return The loaded object, or null if error occurs
     */
    public static Object loadObject(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading from file: " + filePath);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if a file exists.
     * Static utility method.
     * 
     * @param filePath The file path
     * @return true if file exists, false otherwise
     */
    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

    /**
     * Delete a file.
     * Static utility method.
     * 
     * @param filePath The file path
     * @return true if successful, false otherwise
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    /**
     * Ensure a directory exists, creating it if necessary.
     * Static utility method.
     * 
     * @param directoryPath The directory path
     */
    public static void ensureDirectoryExists(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
}