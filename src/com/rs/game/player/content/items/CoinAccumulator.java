package com.rs.game.player.content.items;

import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

/**
 * Enhanced Coin Accumulator item handler
 * Manages automatic coin collection and distribution from NPC kills
 * 
 * @author Zeus
 * @since June 07, 2025
 */
public class CoinAccumulator {
    
    // Constants
    private static final int COIN_ACCUMULATOR_ITEM_ID = 15585;
    private static final int COINS_ITEM_ID = 995;
    private static final int DROP_TIMEOUT_SECONDS = 60;
    
    // Probability constants
    private static final int BASE_PROC_RATE = 5;
    private static final int PERK_PROC_RATE = 4;
    
    // Multiplier constants
    private static final double COIN_COLLECTOR_MULTIPLIER = 1.25;
    private static final double BASE_MULTIPLIER = 1.0;
    
    // Safety limits
    private static final long MAX_MONEY_POUCH_VALUE = 2000000000L; // Safe limit below Integer.MAX_VALUE
    private static final int MIN_COIN_QUANTITY = 1;
    private static final int MAX_COIN_QUANTITY = 1000000;

    /**
     * Handles coin accumulator processing for NPC kills
     * 
     * @param player The player who killed the NPC
     * @param npc The killed NPC
     * @param baseQuantity Base coin quantity before multipliers
     * @return true if coins were processed, false otherwise
     */
    public static boolean handleCoinAccumulator(Player player, NPC npc, int baseQuantity) {
        // Validate input parameters
        if (!validateInputs(player, npc, baseQuantity)) {
            return false;
        }
        
        // Check if coin accumulator should proc
        if (!shouldProcCoinAccumulator(player)) {
            return false;
        }
        
        // Calculate final coin quantity with multipliers
        int finalQuantity = calculateCoinQuantity(player, baseQuantity);
        
        // Determine distribution method and process coins
        return processCoinDistribution(player, npc, finalQuantity);
    }
    
    /**
     * Validates input parameters for safety
     */
    private static boolean validateInputs(Player player, NPC npc, int baseQuantity) {
        if (player == null) {
            return false;
        }
        
        if (npc == null) {
            return false;
        }
        
        if (baseQuantity < MIN_COIN_QUANTITY || baseQuantity > MAX_COIN_QUANTITY) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Determines if coin accumulator should activate based on probability
     */
    private static boolean shouldProcCoinAccumulator(Player player) {
        boolean hasCoinCollectorPerk = hasCoinCollectorPerk(player);
        int procRate = hasCoinCollectorPerk ? PERK_PROC_RATE : BASE_PROC_RATE;
        
        return Utils.random(procRate) == 0;
    }
    
    /**
     * Checks if player has coin collector perk
     */
    private static boolean hasCoinCollectorPerk(Player player) {
        return player.getPerkManager() != null && player.getPerkManager().coinCollector;
    }
    
    /**
     * Calculates final coin quantity with appropriate multipliers
     */
    private static int calculateCoinQuantity(Player player, int baseQuantity) {
        double multiplier = hasCoinCollectorPerk(player) ? COIN_COLLECTOR_MULTIPLIER : BASE_MULTIPLIER;
        
        // Apply multiplier and ensure result is within safe bounds
        long result = Math.round(baseQuantity * multiplier);
        
        // Clamp to safe integer range
        if (result > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (result < MIN_COIN_QUANTITY) {
            return MIN_COIN_QUANTITY;
        }
        
        return (int) result;
    }
    
    /**
     * Processes coin distribution based on player equipment and status
     */
    private static boolean processCoinDistribution(Player player, NPC npc, int quantity) {
        // Determine if coins should go to inventory or ground
        CoinDistributionMethod method = determineCoinDistributionMethod(player);
        
        switch (method) {
            case INVENTORY_ACCUMULATOR:
                return addCoinsToInventory(player, quantity, "coin accumulator");
                
            case INVENTORY_PET:
                return addCoinsToInventory(player, quantity, "legendary pet");
                
            case GROUND:
                return dropCoinsOnGround(player, npc, quantity);
                
            default:
                return false;
        }
    }
    
    /**
     * Determines the appropriate coin distribution method
     */
    private static CoinDistributionMethod determineCoinDistributionMethod(Player player) {
        // Check for coin accumulator item or perk first
        if (hasCoinAccumulator(player) || hasCoinCollectorPerk(player)) {
            return CoinDistributionMethod.INVENTORY_ACCUMULATOR;
        }
        
        // Check for legendary pet collection
        if (hasLegendaryPet(player)) {
            return CoinDistributionMethod.INVENTORY_PET;
        }
        
        // Default to ground drop
        return CoinDistributionMethod.GROUND;
    }
    
    /**
     * Checks if player has coin accumulator item
     */
    private static boolean hasCoinAccumulator(Player player) {
        return player.getInventory() != null && 
               player.getInventory().containsItem(COIN_ACCUMULATOR_ITEM_ID, 1);
    }
    
    /**
     * Checks if player has legendary pet that can collect items
     */
    private static boolean hasLegendaryPet(Player player) {
        return player.getPetManager() != null && player.getPetManager().isReceivePet();
    }
    
    /**
     * Adds coins directly to player's money pouch with overflow protection
     */
    private static boolean addCoinsToInventory(Player player, int quantity, String source) {
        // Check for money pouch overflow
        if (!canAddToMoneyPouch(player, quantity)) {
            // If can't add to pouch, try inventory instead
            return addCoinsToInventorySlots(player, quantity, source);
        }
        
        // Add to money pouch
        player.addMoney(quantity);
        
        // Send confirmation message
        String message = String.format("Your %s collected %d coins.", source, quantity);
        player.sendMessage(message, true);
        
        return true;
    }
    
    /**
     * Checks if coins can be safely added to money pouch
     */
    private static boolean canAddToMoneyPouch(Player player, int quantity) {
        long currentValue = player.getMoneyPouchValue();
        long newValue = currentValue + quantity;
        
        // Check for overflow and reasonable limits
        return newValue >= currentValue && newValue <= MAX_MONEY_POUCH_VALUE;
    }
    
    /**
     * Attempts to add coins to regular inventory slots
     */
    private static boolean addCoinsToInventorySlots(Player player, int quantity, String source) {
        if (player.getInventory() == null) {
            return false;
        }
        
        Item coinItem = new Item(COINS_ITEM_ID, quantity);
        
        if (player.getInventory().addItem(coinItem)) {
            String message = String.format("Your %s collected %d coins (added to inventory).", source, quantity);
            player.sendMessage(message, true);
            return true;
        }
        
        return false;
    }
    
    /**
     * Drops coins on the ground at NPC location
     */
    private static boolean dropCoinsOnGround(Player player, NPC npc, int quantity) {
        if (npc.getLastWorldTile() == null) {
            return false;
        }
        
        Item coinItem = new Item(COINS_ITEM_ID, quantity);
        WorldTile dropLocation = new WorldTile(npc.getLastWorldTile());
        
        try {
            World.updateGroundItem(coinItem, dropLocation, player, DROP_TIMEOUT_SECONDS, 0);
            
            String message = String.format("Coins (%d) dropped on the ground.", quantity);
            player.sendMessage(message, true);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error dropping coins for player " + player.getDisplayName() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Enumeration for coin distribution methods
     */
    private enum CoinDistributionMethod {
        INVENTORY_ACCUMULATOR,  // Coin accumulator item or perk
        INVENTORY_PET,          // Legendary pet collection
        GROUND                  // Drop on ground
    }
    
    /**
     * Alternative method signature for backward compatibility
     * 
     * @deprecated Use handleCoinAccumulator(Player, NPC, int) instead
     */
    @Deprecated
    public static boolean handleCoinAccumulator(Player player, NPC npc, int quantity, boolean forceInventory) {
        if (forceInventory) {
            return addCoinsToInventory(player, quantity, "coin accumulator");
        } else {
            return handleCoinAccumulator(player, npc, quantity);
        }
    }
    
    /**
     * Utility method to get effective coin multiplier for a player
     */
    public static double getCoinMultiplier(Player player) {
        return hasCoinCollectorPerk(player) ? COIN_COLLECTOR_MULTIPLIER : BASE_MULTIPLIER;
    }
    
    /**
     * Utility method to check if player can benefit from coin accumulator
     */
    public static boolean canUseCoinAccumulator(Player player) {
        return hasCoinAccumulator(player) || hasCoinCollectorPerk(player) || hasLegendaryPet(player);
    }
    
    /**
     * Gets the effective proc rate for a player's coin accumulator
     */
    public static int getProcRate(Player player) {
        return hasCoinCollectorPerk(player) ? PERK_PROC_RATE : BASE_PROC_RATE;
    }
}