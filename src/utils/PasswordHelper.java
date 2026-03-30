package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Password hashing utility using SHA-256
 * One-way hash - cannot be decrypted
 */
public class PasswordHelper {

    /**
     * Hash a password using SHA-256
     * @param password Plain text password
     * @return Hashed password string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }

    /**
     * Verify a password against a hash
     * @param password Plain text password to verify
     * @param hashedPassword Stored hash to compare against
     * @return true if password matches
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        String newHash = hashPassword(password);
        return newHash.equals(hashedPassword);
    }

    /**
     * Check if password is hashed (starts with Base64 charset)
     * @param password Password to check
     * @return true if appears to be hashed
     */
    public static boolean isHashed(String password) {
        if (password == null || password.length() < 20) return false;
        // Base64 encoded SHA-256 hash is 44 characters
        return password.matches("^[A-Za-z0-9+/]+=*$");
    }
}
