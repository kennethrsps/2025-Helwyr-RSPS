package com.rs.game.player.web;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import com.rs.game.World;
import com.rs.game.player.Player;

public class WebAuthManager {
    
    // Store active PINs: PIN -> PlayerData
    private static final Map<String, PlayerPINData> activePINs = new ConcurrentHashMap<>();
    
    // PIN expires after 10 minutes
    private static final long PIN_EXPIRY = 10 * 60 * 1000;
    
    /**
     * Generate 6-digit PIN for player
     */
    public static String generatePIN(Player player) {
        // Remove any existing PIN for this player
        removePlayerPINs(player.getUsername());
        
        // Generate new PIN
        String pin = String.format("%06d", (int)(Math.random() * 1000000));
        
        // Store PIN
        PlayerPINData pinData = new PlayerPINData(player.getUsername(), System.currentTimeMillis());
        activePINs.put(pin, pinData);
        
        // Clean up expired PINs
        cleanupExpiredPINs();
        
        return pin;
    }
    
    public static Player validatePIN(String pin) {
        System.out.println("DEBUG: Validating PIN: " + pin);
        PlayerPINData pinData = activePINs.get(pin);
        
        if (pinData == null) {
            System.out.println("DEBUG: PIN not found in activePINs map!");
            System.out.println("DEBUG: Current activePINs size: " + activePINs.size());
            return null;
        }
        
        System.out.println("DEBUG: Found PIN data for username: " + pinData.getUsername());
        
        // Check if PIN expired
        if (System.currentTimeMillis() - pinData.getCreatedTime() > PIN_EXPIRY) {
            System.out.println("DEBUG: PIN expired!");
            activePINs.remove(pin);
            return null;
        }
        
        // Check if player is still online
        Player player = World.getPlayerByDisplayName(pinData.getUsername());
        if (player == null) {
            System.out.println("DEBUG: World.getPlayerByDisplayName returned null for: " + pinData.getUsername());
            activePINs.remove(pin);
            return null;
        }
        
        System.out.println("DEBUG: Successfully found player: " + player.getUsername());
        return player;
    }
    
    /**
     * Remove all PINs for a player (when they log out)
     */
    public static void removePlayerPINs(String username) {
        activePINs.entrySet().removeIf(entry -> 
            entry.getValue().getUsername().equalsIgnoreCase(username));
    }
    
    private static void cleanupExpiredPINs() {
        long currentTime = System.currentTimeMillis();
        activePINs.entrySet().removeIf(entry -> 
            currentTime - entry.getValue().getCreatedTime() > PIN_EXPIRY);
    }
    
    public static int getActivePINCount() {
        cleanupExpiredPINs();
        return activePINs.size();
    }
    
    // Simple data class for PIN storage
    public static class PlayerPINData {
        private final String username;
        private final long createdTime;
        
        public PlayerPINData(String username, long createdTime) {
            this.username = username;
            this.createdTime = createdTime;
        }
        
        public String getUsername() { return username; }
        public long getCreatedTime() { return createdTime; }
    }
}
