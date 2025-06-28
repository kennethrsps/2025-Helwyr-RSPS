// ==================== ShieldSmithingD.java ====================
/**
 * Shield Smithing Dialogue
 * Author: Zeus
 * Interactive dialogue system for crafting malevolent shields
 * 
 * FIXES APPLIED:
 * - Eliminated code duplication with helper methods
 * - Added proper error handling
 * - Enhanced user feedback
 * - Used constants from ShieldSmithing class
 * - Added validation for all operations
 */
package com.rise_of_the_six;

import com.rs.game.Animation;
import com.rs.game.player.Skills;
import com.rs.game.player.dialogue.Dialogue;

public class ShieldSmithingD extends Dialogue {
    
    // Animation constants
    private static final int SMITHING_ANIMATION = 898;
    private static final int SMITHING_XP_REWARD = 91;
    
    // Shield types enum for better organization
    private enum ShieldType {
        MALEVOLENT("Malevolent kiteshield", ShieldSmithing.MALEVOLENT_SHIELD_ID),
        VENGEFUL("Vengeful kiteshield", ShieldSmithing.VENGEFUL_SHIELD_ID),
        MERCILESS("Merciless kiteshield", ShieldSmithing.MERCILESS_SHIELD_ID);
        
        private final String name;
        private final int itemId;
        
        ShieldType(String name, int itemId) {
            this.name = name;
            this.itemId = itemId;
        }
        
        public String getName() { return name; }
        public int getItemId() { return itemId; }
    }

    @Override
    public void start() {
        sendOptionsDialogue("Which Shield Do You Wish To Make?", 
                          ShieldType.MALEVOLENT.getName(), 
                          ShieldType.VENGEFUL.getName(), 
                          ShieldType.MERCILESS.getName());
        stage = -1;
    }

    @Override
    public void run(int interfaceId, int componentId) {
        if (stage == -1) {
            try {
                ShieldType selectedShield = null;
                
                switch (componentId) {
                    case OPTION_1:
                        selectedShield = ShieldType.MALEVOLENT;
                        break;
                    case OPTION_2:
                        selectedShield = ShieldType.VENGEFUL;
                        break;
                    case OPTION_3:
                        selectedShield = ShieldType.MERCILESS;
                        break;
                    default:
                        player.sendMessage("Invalid selection.");
                        end();
                        return;
                }
                
                if (selectedShield != null) {
                    craftShield(selectedShield);
                }
                
            } catch (Exception e) {
                System.err.println("Error in shield smithing dialogue: " + e.getMessage());
                player.sendMessage("An error occurred during crafting.");
                end();
            }
        }
    }
    
    /**
     * Crafts the selected shield with proper validation and feedback
     */
    private void craftShield(ShieldType shieldType) {
        if (player == null || player.getInventory() == null) {
            end();
            return;
        }
        
        try {
            // Validate materials again (safety check)
            if (!player.getInventory().containsItem(ShieldSmithing.MALEVOLENT_ENERGY_ID, 86) ||
                !player.getInventory().containsItem(ShieldSmithing.REINFORCING_PLATE_ID, 6)) {
                player.sendMessage("You no longer have the required materials.");
                end();
                return;
            }
            
            // Lock player and start animation
            player.lock(3);
            player.setNextAnimation(new Animation(SMITHING_ANIMATION));
            
            // Remove materials
            player.getInventory().deleteItem(ShieldSmithing.MALEVOLENT_ENERGY_ID, 86);
            player.getInventory().deleteItem(ShieldSmithing.REINFORCING_PLATE_ID, 6);
            
            // Add crafted shield
            if (!player.getInventory().addItem(shieldType.getItemId(), 1)) {
                // If inventory full, try to add to bank
                player.getBank().addItem(shieldType.getItemId(), 1, false);
                player.sendMessage("Your " + shieldType.getName() + " was placed in your bank.");
            }
            
            // Award experience
            player.getSkills().addXp(Skills.SMITHING, SMITHING_XP_REWARD);
            
            // Send success messages
            player.getPackets().sendGameMessage("You carefully combine the energy with the plates...");
            player.getPackets().sendGameMessage("...And you successfully create a " + shieldType.getName() + ".");
            
            end();
            
        } catch (Exception e) {
            System.err.println("Error crafting shield: " + e.getMessage());
            player.sendMessage("An error occurred during crafting.");
            player.unlock();
            end();
        }
    }

    @Override
    public void finish() {
        // Ensure player is unlocked on dialogue end
        if (player != null) {
            player.unlock();
        }
    }
}