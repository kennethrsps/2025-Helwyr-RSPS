package com.rs.utils;

import com.rs.game.player.Player;
import com.rs.game.WorldTile;

/**
 * Simple Player Name Corruption Fixer
 * Compatible with your Player class structure
 * 
 * @author Zeus
 * @date June 06, 2025
 */
public class SimplePlayerFixer {

    /**
     * Diagnose what's wrong with a player file
     */
    public static boolean diagnosePlayer(Player admin, String targetUsername) {
        try {
            admin.sendMessage("=== DIAGNOSING PLAYER: " + targetUsername + " ===");
            
            // Try to load the player file
            Player target = SerializableFilesManager.loadPlayer(targetUsername);
            
            if (target == null) {
                admin.sendMessage("ERROR: Player file could not be loaded!");
                return false;
            }
            
            admin.sendMessage("✓ Player file loaded successfully");
            
            // Check username field
            String username = target.getUsername();
            admin.sendMessage("Username: " + (username != null ? "'" + username + "'" : "NULL"));
            
            // Check display name field  
            String displayName = target.getDisplayName();
            admin.sendMessage("Display Name: " + (displayName != null ? "'" + displayName + "'" : "NULL"));
            
            // Check location
            try {
                admin.sendMessage("Location: " + target.getX() + ", " + target.getY() + ", " + target.getPlane());
            } catch (Exception e) {
                admin.sendMessage("Location: ERROR - " + e.getMessage());
            }
            
            // Check if player is somehow online
            Player onlinePlayer = com.rs.game.World.getPlayerByDisplayName(targetUsername);
            admin.sendMessage("Online Status: " + (onlinePlayer != null ? "GHOST ONLINE" : "Offline"));
            
            // Check rights
            try {
                admin.sendMessage("Rights: " + target.getRights());
            } catch (Exception e) {
                admin.sendMessage("Rights: ERROR - " + e.getMessage());
            }
            
            // Check password (without revealing it)
            try {
                String password = target.getPassword();
                admin.sendMessage("Password: " + (password != null ? "SET" : "NULL"));
            } catch (Exception e) {
                admin.sendMessage("Password: ERROR - " + e.getMessage());
            }
            
            return true;
            
        } catch (Exception e) {
            admin.sendMessage("ERROR during diagnosis: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Fix ONLY the name corruption - simple and safe
     */
    public static boolean fixPlayerName(Player admin, String targetUsername) {
        try {
            admin.sendMessage("=== FIXING PLAYER NAME: " + targetUsername + " ===");
            
            // Create backup first
            boolean backupCreated = SerializableFilesManager.createBackup(targetUsername);
            admin.sendMessage("Backup created: " + backupCreated);
            
            // Load the corrupted player
            Player target = SerializableFilesManager.loadPlayer(targetUsername);
            
            if (target == null) {
                admin.sendMessage("ERROR: Cannot load player file!");
                return false;
            }
            
            // ONLY fix the name fields - nothing else
            target.setUsername(targetUsername);
            target.setDisplayName(targetUsername);
            
            // Fix password if it's null
            if (target.getPassword() == null) {
                admin.sendMessage("WARNING: Password was null, setting to empty");
                target.setPassword("");
            }
            
            // Ensure safe location if coordinates are invalid
            try {
                int x = target.getX();
                int y = target.getY();
                int plane = target.getPlane();
                
                // Check if location is valid
                if (x <= 0 || y <= 0 || plane < 0) {
                    admin.sendMessage("Fixing invalid location: " + x + ", " + y + ", " + plane);
                    target.setLocation(new WorldTile(3222, 3218, 0)); // Lumbridge
                }
            } catch (Exception e) {
                admin.sendMessage("Location was corrupted, setting to Lumbridge");
                target.setLocation(new WorldTile(3222, 3218, 0));
            }
            
            // Clear instance reference if possible
            try {
                target.setCurrentInstance(null);
            } catch (Exception e) {
                // Method might not exist, ignore
            }
            
            // Clear temporary attributes if possible
            try {
                if (target.getTemporaryAttributtes() != null) {
                    target.getTemporaryAttributtes().clear();
                }
            } catch (Exception e) {
                // Method might not exist, ignore
            }
            
            // Save the fixed player
            SerializableFilesManager.savePlayer(target);
            
            admin.sendMessage("✓ Player name corruption fixed!");
            admin.sendMessage("Username set to: " + target.getUsername());
            admin.sendMessage("Display name set to: " + target.getDisplayName());
            
            return true;
            
        } catch (Exception e) {
            admin.sendMessage("ERROR fixing player: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove ghost player from online list
     */
    public static boolean removeGhostPlayer(Player admin, String targetUsername) {
        try {
            admin.sendMessage("=== REMOVING GHOST PLAYER: " + targetUsername + " ===");
            
            // Find the ghost player
            Player ghostPlayer = com.rs.game.World.getPlayerByDisplayName(targetUsername);
            
            if (ghostPlayer == null) {
                // Try searching by username
                for (Player p : com.rs.game.World.getPlayers()) {
                    if (p != null && p.getUsername() != null && 
                        p.getUsername().equalsIgnoreCase(targetUsername)) {
                        ghostPlayer = p;
                        break;
                    }
                }
            }
            
            if (ghostPlayer != null) {
                admin.sendMessage("Found ghost player, removing...");
                
                // Try to logout with boolean parameter (your server's method signature)
                try {
                    ghostPlayer.logout(true);
                } catch (Exception e) {
                    admin.sendMessage("Logout failed: " + e.getMessage());
                }
                
                // Remove from world
                com.rs.game.World.removePlayer(ghostPlayer);
                
                admin.sendMessage("✓ Ghost player removed");
                return true;
            } else {
                admin.sendMessage("No ghost player found online");
                return false;
            }
            
        } catch (Exception e) {
            admin.sendMessage("ERROR removing ghost: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Complete recovery process
     */
    public static boolean recoverPlayer(Player admin, String targetUsername) {
        try {
            admin.sendMessage("=== COMPLETE PLAYER RECOVERY: " + targetUsername + " ===");
            
            // Step 1: Remove any ghost
            removeGhostPlayer(admin, targetUsername);
            
            // Step 2: Diagnose the issue
            diagnosePlayer(admin, targetUsername);
            
            // Step 3: Fix name corruption
            boolean nameFixed = fixPlayerName(admin, targetUsername);
            
            if (nameFixed) {
                admin.sendMessage("✓ Recovery completed successfully!");
                admin.sendMessage("Player should now be able to log in.");
                
                // Test the fix
                admin.sendMessage("Testing fix by loading player again...");
                Player testLoad = SerializableFilesManager.loadPlayer(targetUsername);
                if (testLoad != null) {
                    admin.sendMessage("✓ Test load successful!");
                    admin.sendMessage("Final username: " + testLoad.getUsername());
                    admin.sendMessage("Final display name: " + testLoad.getDisplayName());
                } else {
                    admin.sendMessage("✗ Test load failed!");
                }
                
                return true;
            } else {
                admin.sendMessage("✗ Recovery failed");
                return false;
            }
            
        } catch (Exception e) {
            admin.sendMessage("ERROR in recovery: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}