// File: src/utils/PasswordHasher.java
package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class for password hashing using SHA-256.
 * Demonstrates use of static utility methods.
 */
public class PasswordHasher {
    
    // Private constructor to prevent instantiation (utility class)
    private PasswordHasher() {
        throw new AssertionError("Utility class should not be instantiated");
    }

    /**
     * Hash a password using SHA-256 algorithm.
     * Static utility method.
     * 
     * @param password The plain text password
     * @return The hashed password as a hexadecimal string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Verify if a plain text password matches a hashed password.
     * Static utility method.
     * 
     * @param password The plain text password
     * @param hashedPassword The hashed password to compare against
     * @return true if passwords match, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        String hashedInput = hashPassword(password);
        return hashedInput.equals(hashedPassword);
    }

    /**
     * Convert byte array to hexadecimal string.
     * Private helper method.
     * 
     * @param bytes The byte array
     * @return Hexadecimal string representation
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}