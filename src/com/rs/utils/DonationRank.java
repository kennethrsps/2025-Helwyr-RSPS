package com.rs.utils;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.player.Player;

/**
 * Manages donation rankings for players, maintaining a top 10 leaderboard
 * of players based on their donation amounts.
 */
public final class DonationRank implements Serializable {

    private static final long serialVersionUID = 5403480618483552509L;
    
    // Constants
    private static final int MAX_RANKS = 10;
    private static final int TOP_DONATORS_COUNT = 3;
    private static final String RANKS_FILE_PATH = "data/hiscores/donation.chry";
    
    // Interface component IDs
    private static final int INTERFACE_ID = 1158;
    private static final int TITLE_COMPONENT = 74;
    private static final int USERNAME_COMPONENT_BASE = 9;
    private static final int DONATION_COMPONENT_BASE = 10;
    private static final int STATUS_COMPONENT_BASE = 11;
    private static final int COMPONENT_SPACING = 5;
    
    // Instance fields
    private final String username;
    private final int amountDonated;
    private final int totalLevel;
    private final String xpMode;
    
    // Static data
    private static DonationRank[] ranks;
    
    /**
     * Creates a new DonationRank from a player's current stats.
     * 
     * @param player The player to create the rank for
     * @throws IllegalArgumentException if player is null or has invalid data
     */
    public DonationRank(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        if (player.getUsername() == null || player.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Player username cannot be null or empty");
        }
        
        this.username = player.getUsername().trim();
        this.amountDonated = Math.max(0, player.getMoneySpent()); // Ensure non-negative
        this.xpMode = player.getXPMode();
        this.totalLevel = player.getSkills().getTotalLevel(player);
    }
    
    /**
     * Initializes the donation ranking system by loading existing data from file.
     */
    public static void init() {
        File file = new File(RANKS_FILE_PATH);
        
        if (file.exists()) {
            try {
                DonationRank[] loadedRanks = (DonationRank[]) SerializableFilesManager.loadSerializedFile(file);
                if (loadedRanks != null && loadedRanks.length == MAX_RANKS) {
                    ranks = loadedRanks;
                    validateAndCleanRanks();
                    return;
                }
            } catch (Exception e) {
                Logger.handle(e);
                Logger.log("DonationRank", "Failed to load donation ranks, creating new array");
            }
        }
        
        ranks = new DonationRank[MAX_RANKS];
    }
    
    /**
     * Saves the current donation rankings to file.
     */
    public static void save() {
        if (ranks == null) {
            Logger.log("DonationRank", "Cannot save null ranks array");
            return;
        }
        
        try {
            SerializableFilesManager.storeSerializableClass(ranks, new File(RANKS_FILE_PATH));
        } catch (Exception e) {
            Logger.handle(e);
            Logger.log("DonationRank", "Failed to save donation ranks");
        }
    }
    
    /**
     * Validates and cleans the ranks array, removing any invalid entries.
     */
    private static void validateAndCleanRanks() {
        if (ranks == null) return;
        
        for (int i = 0; i < ranks.length; i++) {
            DonationRank rank = ranks[i];
            if (rank != null && (rank.username == null || rank.username.trim().isEmpty())) {
                Logger.log("DonationRank", "Removing invalid rank entry at index " + i);
                ranks[i] = null;
            }
        }
        
        sort(); // Re-sort after cleanup
    }
    
    /**
     * Determines if a player is currently online.
     * 
     * @param rank The donation rank to check
     * @return Formatted string indicating online/offline status
     */
    private static String getPlayerOnlineStatus(DonationRank rank) {
        if (rank == null || rank.username == null) {
            return Colors.red + "Unknown";
        }
        
        Player onlinePlayer = World.getPlayer(rank.username);
        return onlinePlayer != null ? Colors.green + "Online" : Colors.red + "Offline";
    }
    
    /**
     * Displays the donation rankings interface to a player.
     * 
     * @param player The player to show the rankings to
     */
    public static void showRanks(Player player) {
        if (player == null) {
            Logger.log("DonationRank", "Cannot show ranks to null player");
            return;
        }
        
        if (ranks == null) {
            init();
        }
        
        player.getInterfaceManager().sendInterface(INTERFACE_ID);
        player.getPackets().sendIComponentText(INTERFACE_ID, TITLE_COMPONENT, 
            Colors.white + "Top " + MAX_RANKS + " Respected Donators</col>");
        
        int displayCount = 0;
        for (int i = 0; i < ranks.length && displayCount < MAX_RANKS; i++) {
            DonationRank rank = ranks[i];
            if (rank == null) {
                break; // Stop at first null entry since array is sorted
            }
            
            int baseComponent = displayCount * COMPONENT_SPACING;
            player.getPackets().sendIComponentText(INTERFACE_ID, 
                USERNAME_COMPONENT_BASE + baseComponent, 
                Utils.formatString(rank.username));
            player.getPackets().sendIComponentText(INTERFACE_ID, 
                DONATION_COMPONENT_BASE + baseComponent,
                "Total Donation: " + Colors.green + "$" + Utils.getFormattedNumber(rank.amountDonated));
            player.getPackets().sendIComponentText(INTERFACE_ID, 
                STATUS_COMPONENT_BASE + baseComponent, 
                getPlayerOnlineStatus(rank));
            
            displayCount++;
        }
    }
    
    /**
     * Sorts the donation ranks in descending order by donation amount.
     * Null entries are moved to the end of the array.
     */
    public static void sort() {
        if (ranks == null) return;
        
        Arrays.sort(ranks, new DonationRankComparator());
    }
    
    /**
     * Comparator for sorting donation ranks by donation amount (descending).
     */
    private static class DonationRankComparator implements Comparator<DonationRank> {
        @Override
        public int compare(DonationRank rank1, DonationRank rank2) {
            // Handle null values - nulls go to end
            if (rank1 == null && rank2 == null) return 0;
            if (rank1 == null) return 1;
            if (rank2 == null) return -1;
            
            // Sort by donation amount (descending)
            return Integer.compare(rank2.amountDonated, rank1.amountDonated);
        }
    }
    
    /**
     * Checks and updates a player's position in the donation rankings.
     * 
     * @param player The player to check and potentially add/update
     */
    public static void checkRank(Player player) {
        if (player == null || player.getUsername() == null || player.getUsername().trim().isEmpty()) {
            return;
        }
        
        // Initialize ranks if null
        if (ranks == null) {
            ranks = new DonationRank[MAX_RANKS];
        }
        
        // Skip administrators (rights == 2)
        if (player.getRights() == 2) {
            return;
        }
        
        int playerDonationAmount = player.getMoneySpent();
        String playerUsername = player.getUsername().trim();
        
        // First, check if player already exists in rankings and update
        if (updateExistingPlayer(player, playerUsername)) {
            return;
        }
        
        // If player doesn't exist, try to add them to an empty slot
        if (addToEmptySlot(player)) {
            return;
        }
        
        // Finally, check if player can replace someone with lower donation
        replaceLowestQualifyingRank(player, playerDonationAmount);
    }
    
    /**
     * Updates an existing player's rank if they're already in the rankings.
     * 
     * @param player The player to update
     * @param playerUsername The player's username
     * @return true if player was found and updated, false otherwise
     */
    private static boolean updateExistingPlayer(Player player, String playerUsername) {
        for (int i = 0; i < ranks.length; i++) {
            DonationRank rank = ranks[i];
            if (rank != null && rank.username != null && 
                rank.username.equalsIgnoreCase(playerUsername)) {
                ranks[i] = new DonationRank(player);
                sort();
                Logger.log("DonationRank", "Updated existing rank for player: " + playerUsername);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Adds a player to the first available empty slot in the rankings.
     * 
     * @param player The player to add
     * @return true if player was added to an empty slot, false otherwise
     */
    private static boolean addToEmptySlot(Player player) {
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i] == null) {
                ranks[i] = new DonationRank(player);
                sort();
                Logger.log("DonationRank", "Added new player to empty slot: " + player.getUsername());
                return true;
            }
        }
        return false;
    }
    
    /**
     * Replaces the player with the lowest donation amount if the new player qualifies.
     * 
     * @param player The player to potentially add
     * @param playerDonationAmount The player's donation amount
     */
    private static void replaceLowestQualifyingRank(Player player, int playerDonationAmount) {
        // Find the rank with the lowest donation amount
        int lowestIndex = -1;
        int lowestAmount = Integer.MAX_VALUE;
        
        for (int i = 0; i < ranks.length; i++) {
            if (ranks[i] != null && ranks[i].amountDonated < lowestAmount) {
                lowestAmount = ranks[i].amountDonated;
                lowestIndex = i;
            }
        }
        
        // Replace if new player has donated more than the lowest
        if (lowestIndex != -1 && playerDonationAmount > lowestAmount) {
            ranks[lowestIndex] = new DonationRank(player);
            sort();
            Logger.log("DonationRank", "Replaced lowest rank with player: " + player.getUsername());
        }
    }
    
    /**
     * Gets the top 3 donator usernames as a formatted string.
     * 
     * @return Formatted string with top 3 donators, or null if no valid data
     */
    public static String getDonatorTop() {
        if (ranks == null || ranks[0] == null) {
            return "No donation data available.";
        }
        
        StringBuilder topDonators = new StringBuilder();
        topDonators.append("Top ").append(TOP_DONATORS_COUNT).append(" ")
                  .append(Settings.SERVER_NAME).append(" contributors");
        
        int count = 0;
        for (DonationRank rank : ranks) {
            if (rank == null || count >= TOP_DONATORS_COUNT) {
                break;
            }
            
            topDonators.append("  -  ").append(Utils.formatString(rank.username));
            count++;
        }
        
        // Handle case where we have fewer than 3 donators
        while (count < TOP_DONATORS_COUNT) {
            topDonators.append("  -  N/A");
            count++;
        }
        
        topDonators.append(".");
        return topDonators.toString();
    }
    
    // Getter methods for accessing private fields
    public String getUsername() {
        return username;
    }
    
    public int getAmountDonated() {
        return amountDonated;
    }
    
    public int getTotalLevel() {
        return totalLevel;
    }
    
    public String getXpMode() {
        return xpMode;
    }
    
    /**
     * Gets a copy of the current rankings array.
     * 
     * @return A copy of the rankings array
     */
    public static DonationRank[] getRankings() {
        if (ranks == null) {
            return new DonationRank[MAX_RANKS];
        }
        return Arrays.copyOf(ranks, ranks.length);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        DonationRank that = (DonationRank) obj;
        return amountDonated == that.amountDonated &&
               totalLevel == that.totalLevel &&
               Objects.equals(username, that.username) &&
               Objects.equals(xpMode, that.xpMode);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(username, amountDonated, totalLevel, xpMode);
    }
    
    @Override
    public String toString() {
        return String.format("DonationRank{username='%s', amountDonated=%d, totalLevel=%d, xpMode='%s'}", 
                           username, amountDonated, totalLevel, xpMode);
    }
}