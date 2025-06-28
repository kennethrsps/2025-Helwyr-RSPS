package com.rise_of_the_six;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;

public class ShieldSmithing {
    
    // Constants for item IDs
    public static final int MALEVOLENT_ENERGY_ID = 29940;
    public static final int REINFORCING_PLATE_ID = 29942;
    public static final int MALEVOLENT_SHIELD_ID = 29935;
    public static final int VENGEFUL_SHIELD_ID = 29937;
    public static final int MERCILESS_SHIELD_ID = 29939;
    
    // Requirements
    private static final int REQUIRED_SMITHING_LEVEL = 91;
    private static final int REQUIRED_ENERGY_AMOUNT = 86;
    private static final int REQUIRED_PLATE_AMOUNT = 6;
    
    /**
     * Initiates shield making process with proper validation
     */
    public static void shieldMaking(Player player) {
        // Validate player
        if (player == null) {
            System.err.println("ShieldSmithing: Player is null");
            return;
        }
        
        try {
            // Check smithing level
            if (player.getSkills().getLevel(Skills.SMITHING) < REQUIRED_SMITHING_LEVEL) {
                player.sendMessage("You must have at least " + REQUIRED_SMITHING_LEVEL + 
                                 " Smithing before attempting this.");
                return;
            }
            
            // Check required items
            if (!hasRequiredItems(player)) {
                player.sendMessage("You must have " + REQUIRED_ENERGY_AMOUNT + 
                                 " Malevolent energies and " + REQUIRED_PLATE_AMOUNT + 
                                 " reinforcing plates before attempting this.");
                return;
            }
            
            // Start dialogue
            player.getDialogueManager().startDialogue("ShieldSmithingD");
            
        } catch (Exception e) {
            System.err.println("Error in shield making: " + e.getMessage());
            player.sendMessage("An error occurred. Please try again.");
        }
    }
    
    /**
     * Checks if player has required items
     */
    private static boolean hasRequiredItems(Player player) {
        if (player.getInventory() == null) return false;
        
        return player.getInventory().containsItem(MALEVOLENT_ENERGY_ID, REQUIRED_ENERGY_AMOUNT) && 
               player.getInventory().containsItem(REINFORCING_PLATE_ID, REQUIRED_PLATE_AMOUNT);
    }
}