package com.rs.game.activities.instances;

import java.util.ArrayList;
import java.util.Iterator;
import com.rs.game.World;
import com.rs.game.player.Player;

/**
 * Emergency Instance Management Tools
 * Fix for stuck players in corrupted instances
 * 
 * @author Zeus
 * @date June 06, 2025
 */
public class InstanceEmergencyManager {

    /**
     * IMMEDIATE FIX: Clear all instances and free stuck players
     * Add this as an admin command: ::clearallinstances
     */
    public static boolean clearAllInstances(Player admin) {
        try {
            int clearedCount = 0;
            int playersFreed = 0;
            
            // Get all instances from World
            ArrayList<Instance> instances = World.getInstances();
            
            admin.sendMessage("Found " + instances.size() + " active instances");
            
            // Create a copy to avoid ConcurrentModificationException
            ArrayList<Instance> instancesCopy = new ArrayList<Instance>(instances);
            
            for (Instance instance : instancesCopy) {
                if (instance != null) {
                    admin.sendMessage("Destroying instance owned by: " + 
                        (instance.getOwner() != null ? instance.getOwner().getDisplayName() : "Unknown"));
                    
                    // Free all players from this instance
                    ArrayList<Player> players = instance.getPlayers();
                    if (players != null) {
                        // Java 1.7 compatible loop (no forEach)
                        for (Player p : players) {
                            if (p != null) {
                                p.setCurrentInstance(null);
                                p.setNextWorldTile(instance.getOutsideCoordinates());
                                playersFreed++;
                                admin.sendMessage("Freed player: " + p.getDisplayName());
                            }
                        }
                    }
                    
                    // Destroy the instance
                    instance.destroyInstance();
                    clearedCount++;
                }
            }
            
            // Clear the instances list completely
            instances.clear();
            
            admin.sendMessage("Emergency cleanup complete!");
            admin.sendMessage("Cleared " + clearedCount + " instances");
            admin.sendMessage("Freed " + playersFreed + " players");
            
            return true;
            
        } catch (Exception e) {
            admin.sendMessage("Error during cleanup: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * DEBUG: Find which instance a specific player is stuck in
     * Add this as admin command: ::findplayerinstance PLAYERNAME
     */
    public static boolean findPlayerInstance(Player admin, String targetUsername) {
        try {
            admin.sendMessage("Searching for player: " + targetUsername);
            
            ArrayList<Instance> instances = World.getInstances();
            boolean found = false;
            
            for (Instance instance : instances) {
                if (instance != null && instance.getPlayers() != null) {
                    for (Player p : instance.getPlayers()) {
                        if (p != null && p.getUsername().equalsIgnoreCase(targetUsername)) {
                            admin.sendMessage("FOUND PLAYER IN INSTANCE!");
                            admin.sendMessage("Instance Owner: " + 
                                (instance.getOwner() != null ? instance.getOwner().getDisplayName() : "Unknown"));
                            admin.sendMessage("Instance Boss ID: " + instance.getBoss());
                            admin.sendMessage("Instance Stable: " + instance.isStable);
                            admin.sendMessage("Instance Finished: " + instance.finished);
                            admin.sendMessage("Player Current Instance: " + 
                                (p.getCurrentInstance() != null ? "SET" : "NULL"));
                            found = true;
                        }
                    }
                }
            }
            
            if (!found) {
                admin.sendMessage("Player not found in any instance");
            }
            
            return true;
            
        } catch (Exception e) {
            admin.sendMessage("Error searching: " + e.getMessage());
            return false;
        }
    }

    /**
     * TARGETED FIX: Free a specific player from all instances
     * Add this as admin command: ::freeplayer PLAYERNAME
     */
    public static boolean freePlayer(Player admin, String targetUsername) {
        try {
            admin.sendMessage("Attempting to free player: " + targetUsername);
            
            boolean freed = false;
            ArrayList<Instance> instances = World.getInstances();
            
            for (Instance instance : instances) {
                if (instance != null && instance.getPlayers() != null) {
                    // Use iterator to safely remove during iteration
                    Iterator<Player> it = instance.getPlayers().iterator();
                    while (it.hasNext()) {
                        Player p = it.next();
                        if (p != null && p.getUsername().equalsIgnoreCase(targetUsername)) {
                            admin.sendMessage("Found player in instance owned by: " + 
                                (instance.getOwner() != null ? instance.getOwner().getDisplayName() : "Unknown"));
                            
                            // Remove from instance
                            it.remove();
                            p.setCurrentInstance(null);
                            
                            // Teleport to safe location
                            p.setNextWorldTile(instance.getOutsideCoordinates());
                            
                            admin.sendMessage("Player freed from instance!");
                            freed = true;
                        }
                    }
                }
            }
            
            if (!freed) {
                admin.sendMessage("Player not found in any instance");
            }
            
            return true;
            
        } catch (Exception e) {
            admin.sendMessage("Error freeing player: " + e.getMessage());
            return false;
        }
    }

    /**
     * DEBUG: List all active instances
     * Add this as admin command: ::listinstances
     */
    public static boolean listInstances(Player admin) {
        try {
            ArrayList<Instance> instances = World.getInstances();
            
            admin.sendMessage("=== ACTIVE INSTANCES ===");
            admin.sendMessage("Total instances: " + instances.size());
            
            int count = 0;
            for (Instance instance : instances) {
                if (instance != null) {
                    count++;
                    admin.sendMessage("Instance #" + count + ":");
                    admin.sendMessage("  Owner: " + 
                        (instance.getOwner() != null ? instance.getOwner().getDisplayName() : "NULL"));
                    admin.sendMessage("  Boss ID: " + instance.getBoss());
                    admin.sendMessage("  Players: " + 
                        (instance.getPlayers() != null ? instance.getPlayers().size() : 0));
                    admin.sendMessage("  Stable: " + instance.isStable);
                    admin.sendMessage("  Finished: " + instance.finished);
                    
                    // List players in this instance
                    if (instance.getPlayers() != null) {
                        for (Player p : instance.getPlayers()) {
                            if (p != null) {
                                admin.sendMessage("    Player: " + p.getDisplayName());
                            }
                        }
                    }
                }
            }
            
            return true;
            
        } catch (Exception e) {
            admin.sendMessage("Error listing instances: " + e.getMessage());
            return false;
        }
    }

    /**
     * NUCLEAR OPTION: Force destroy all instances and reset instance system
     * Add this as admin command: ::nukeinstances
     */
    public static boolean nukeInstanceSystem(Player admin) {
        try {
            admin.sendMessage("=== NUCLEAR INSTANCE RESET ===");
            
            ArrayList<Instance> instances = World.getInstances();
            int count = instances.size();
            
            // Free all players first
            for (Instance instance : instances) {
                if (instance != null && instance.getPlayers() != null) {
                    for (Player p : instance.getPlayers()) {
                        if (p != null) {
                            p.setCurrentInstance(null);
                            // Teleport to Lumbridge
                            p.setNextWorldTile(new com.rs.game.WorldTile(3222, 3218, 0));
                        }
                    }
                }
            }
            
            // Clear all instances
            instances.clear();
            
            admin.sendMessage("NUCLEAR RESET COMPLETE!");
            admin.sendMessage("Destroyed " + count + " instances");
            admin.sendMessage("All players teleported to Lumbridge");
            
            return true;
            
        } catch (Exception e) {
            admin.sendMessage("Nuclear reset failed: " + e.getMessage());
            return false;
        }
    }
}