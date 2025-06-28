package com.rs.game.player.content;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.TimeZone;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.rs.game.player.Player;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

/**
 * Manages weekly top rankings for time online, votes, and donations.
 * Thread-safe implementation with proper resource management.
 */
public class WeeklyTopRanking {
    
    private static final Logger LOGGER = Logger.getLogger(WeeklyTopRanking.class.getName());
    private static final int MAX_RANKS = 10;
    private static final TimeZone EST_TIMEZONE = TimeZone.getTimeZone("EST");
    
    // Ranking arrays with thread-safe access
    private static volatile TimeOnlineRank[] timeOnlineRanks;
    private static volatile VoteRank[] voteRanks;
    private static volatile DonationRank[] donationRanks;
    private static volatile int currentDay;
    
    // Read-write locks for thread safety
    private static final ReentrantReadWriteLock timeOnlineLock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock voteLock = new ReentrantReadWriteLock();
    private static final ReentrantReadWriteLock donationLock = new ReentrantReadWriteLock();
    
    // Ranking types enum for better type safety
    public enum RankingType {
        TIME_ONLINE(0, "Time Online"),
        VOTE_COUNT(1, "Vote Count"),
        DONATION_AMOUNT(2, "Donation Amount");
        
        private final int id;
        private final String displayName;
        
        RankingType(int id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }
        
        public int getId() { return id; }
        public String getDisplayName() { return displayName; }
        
        public static RankingType fromId(int id) {
            for (RankingType type : values()) {
                if (type.id == id) return type;
            }
            throw new IllegalArgumentException("Invalid ranking type: " + id);
        }
    }

    /**
     * Initialize the ranking system by loading saved data
     */
    public static void init() {
        try {
            // Load existing data or create new arrays
            timeOnlineRanks = loadOrCreateTimeOnlineRanks();
            voteRanks = loadOrCreateVoteRanks();
            donationRanks = loadOrCreateDonationRanks();
            currentDay = SerializableFilesManager.loadCurrentDay();
            
            LOGGER.info("WeeklyTopRanking initialized successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to initialize WeeklyTopRanking", e);
            // Ensure we have valid arrays even if loading fails
            initializeEmptyArrays();
        }
    }
    
    private static TimeOnlineRank[] loadOrCreateTimeOnlineRanks() {
        TimeOnlineRank[] ranks = SerializableFilesManager.loadTimeOnlineRanks();
        return ranks != null ? ranks : new TimeOnlineRank[MAX_RANKS];
    }
    
    private static VoteRank[] loadOrCreateVoteRanks() {
        VoteRank[] ranks = SerializableFilesManager.loadVoteRanks();
        return ranks != null ? ranks : new VoteRank[MAX_RANKS];
    }
    
    private static DonationRank[] loadOrCreateDonationRanks() {
        DonationRank[] ranks = SerializableFilesManager.loadDonationRanks();
        return ranks != null ? ranks : new DonationRank[MAX_RANKS];
    }
    
    private static void initializeEmptyArrays() {
        timeOnlineRanks = new TimeOnlineRank[MAX_RANKS];
        voteRanks = new VoteRank[MAX_RANKS];
        donationRanks = new DonationRank[MAX_RANKS];
        currentDay = 0;
    }

    /**
     * Process weekly reset logic - clears rankings on Monday
     */
    public static void process() {
        try {
            Calendar calendar = Calendar.getInstance(EST_TIMEZONE);
            int todayDay = calendar.get(Calendar.DAY_OF_WEEK);
            
            if (currentDay != todayDay) {
                if (todayDay == Calendar.MONDAY) {
                    resetWeeklyRankings();
                    LOGGER.info("Weekly rankings reset on Monday");
                }
                currentDay = todayDay;
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error processing weekly reset", e);
        }
    }
    
    private static void resetWeeklyRankings() {
        // Reset all rankings atomically
        timeOnlineLock.writeLock().lock();
        try {
            timeOnlineRanks = new TimeOnlineRank[MAX_RANKS];
        } finally {
            timeOnlineLock.writeLock().unlock();
        }
        
        voteLock.writeLock().lock();
        try {
            voteRanks = new VoteRank[MAX_RANKS];
        } finally {
            voteLock.writeLock().unlock();
        }
        
        donationLock.writeLock().lock();
        try {
            donationRanks = new DonationRank[MAX_RANKS];
        } finally {
            donationLock.writeLock().unlock();
        }
    }

    /**
     * Save all ranking data to persistent storage
     */
    public static void save() {
        try {
            // Ensure arrays are not null before saving
            TimeOnlineRank[] timeRanks = timeOnlineRanks != null ? timeOnlineRanks : new TimeOnlineRank[MAX_RANKS];
            VoteRank[] vRanks = voteRanks != null ? voteRanks : new VoteRank[MAX_RANKS];
            DonationRank[] dRanks = donationRanks != null ? donationRanks : new DonationRank[MAX_RANKS];
            
            SerializableFilesManager.saveTimeOnlineRanks(timeRanks);
            SerializableFilesManager.saveVoteRanks(vRanks);
            SerializableFilesManager.saveDonationRanks(dRanks);
            SerializableFilesManager.saveCurrentDay(currentDay);
            
            LOGGER.fine("Rankings saved successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to save rankings", e);
        }
    }

    /**
     * Display rankings to a player
     * @param player The player to show rankings to
     * @param type The type of ranking (0=time, 1=vote, 2=donation)
     */
    public static void showRanks(Player player, int type) {
        if (player == null) {
            LOGGER.warning("Attempted to show ranks to null player");
            return;
        }
        
        try {
            RankingType rankingType = RankingType.fromId(type);
            
            // Clear previous interface text
            clearInterfaceText(player);
            
            // Display rankings based on type
            switch (rankingType) {
                case TIME_ONLINE:
                    displayTimeOnlineRanks(player);
                    break;
                case VOTE_COUNT:
                    displayVoteRanks(player);
                    break;
                case DONATION_AMOUNT:
                    displayDonationRanks(player);
                    break;
            }
            
            // Set header and show interface
            player.getPackets().sendIComponentText(275, 1, "Top 10 " + rankingType.getDisplayName() + " weekly");
            player.getInterfaceManager().sendInterface(275);
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error showing ranks to player: " + player.getUsername(), e);
        }
    }
    
    private static void clearInterfaceText(Player player) {
        for (int i = 10; i < 310; i++) {
            player.getPackets().sendIComponentText(275, i, "");
        }
    }
    
    private static void displayTimeOnlineRanks(Player player) {
        timeOnlineLock.readLock().lock();
        try {
            for (int i = 0; i < MAX_RANKS && timeOnlineRanks[i] != null; i++) {
                String rankText = formatRankText(i, timeOnlineRanks[i].getUsername(),
                    "Time Online : " + Utils.getTimeToString(timeOnlineRanks[i].getTimeOnline()));
                player.getPackets().sendIComponentText(275, i + 10, rankText);
            }
        } finally {
            timeOnlineLock.readLock().unlock();
        }
    }
    
    private static void displayVoteRanks(Player player) {
        voteLock.readLock().lock();
        try {
            for (int i = 0; i < MAX_RANKS && voteRanks[i] != null; i++) {
                String rankText = formatRankText(i, voteRanks[i].getUsername(),
                    "Vote Count : " + Utils.getFormattedNumber(voteRanks[i].getVoteCount()));
                player.getPackets().sendIComponentText(275, i + 10, rankText);
            }
        } finally {
            voteLock.readLock().unlock();
        }
    }
    
    private static void displayDonationRanks(Player player) {
        donationLock.readLock().lock();
        try {
            for (int i = 0; i < MAX_RANKS && donationRanks[i] != null; i++) {
                String rankText = formatRankText(i, donationRanks[i].getUsername(),
                    "Donation Amount : $" + Utils.getFormattedNumber(donationRanks[i].getDonationAmount()));
                player.getPackets().sendIComponentText(275, i + 10, rankText);
            }
        } finally {
            donationLock.readLock().unlock();
        }
    }
    
    private static String formatRankText(int position, String username, String value) {
        String colorTag = getRankColorTag(position);
        return colorTag + "Top " + (position + 1) + " - " + Utils.formatString(username) + " - " + value;
    }
    
    private static String getRankColorTag(int position) {
        switch (position) {
            case 0: return "<col=ff9900>"; // Gold for 1st
            case 1: return "<col=ff0000>"; // Red for 2nd
            case 2: return "<col=38610B>"; // Green for 3rd
            default: return "<col=000000>"; // Black for others
        }
    }

    // Time Online Ranking Methods
    public static void checkTimeOnlineRank(Player player) {
        if (player == null || !isEligibleForRanking(player)) {
            return;
        }
        
        timeOnlineLock.writeLock().lock();
        try {
            long timeOnline = player.getTimePlayedWeekly();
            updateRanking(timeOnlineRanks, new TimeOnlineRank(player), 
                         rank -> rank.getUsername().equalsIgnoreCase(player.getUsername()),
                         rank -> rank.getTimeOnline() < timeOnline);
            sortTimeOnlineRanks();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error updating time online rank for: " + player.getUsername(), e);
        } finally {
            timeOnlineLock.writeLock().unlock();
        }
    }
    
    private static void sortTimeOnlineRanks() {
        Arrays.sort(timeOnlineRanks, (rank1, rank2) -> {
            if (rank1 == null) return 1;
            if (rank2 == null) return -1;
            return Long.compare(rank2.getTimeOnline(), rank1.getTimeOnline()); // Descending order
        });
    }

    // Vote Ranking Methods
    public static void checkVoteRank(Player player) {
        if (player == null || !isEligibleForRanking(player)) {
            return;
        }
        
        voteLock.writeLock().lock();
        try {
            int voteCount = player.getVoteCountWeekly();
            updateRanking(voteRanks, new VoteRank(player),
                         rank -> rank.getUsername().equalsIgnoreCase(player.getUsername()),
                         rank -> rank.getVoteCount() < voteCount);
            sortVoteRanks();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error updating vote rank for: " + player.getUsername(), e);
        } finally {
            voteLock.writeLock().unlock();
        }
    }
    
    private static void sortVoteRanks() {
        Arrays.sort(voteRanks, (rank1, rank2) -> {
            if (rank1 == null) return 1;
            if (rank2 == null) return -1;
            return Integer.compare(rank2.getVoteCount(), rank1.getVoteCount()); // Descending order
        });
    }

    // Donation Ranking Methods
    public static void checkDonationRank(Player player) {
        if (player == null || !isEligibleForRanking(player)) {
            return;
        }
        
        donationLock.writeLock().lock();
        try {
            int donationAmount = player.getDonationAmountWeekly();
            updateRanking(donationRanks, new DonationRank(player),
                         rank -> rank.getUsername().equalsIgnoreCase(player.getUsername()),
                         rank -> rank.getDonationAmount() < donationAmount);
            sortDonationRanks();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error updating donation rank for: " + player.getUsername(), e);
        } finally {
            donationLock.writeLock().unlock();
        }
    }
    
    private static void sortDonationRanks() {
        Arrays.sort(donationRanks, (rank1, rank2) -> {
            if (rank1 == null) return 1;
            if (rank2 == null) return -1;
            return Integer.compare(rank2.getDonationAmount(), rank1.getDonationAmount()); // Descending order
        });
    }
    
    // Generic ranking update method to reduce code duplication
    private static <T> void updateRanking(T[] rankings, T newRank, 
                                        java.util.function.Predicate<T> isExistingPlayer,
                                        java.util.function.Predicate<T> shouldReplace) {
        
        // First, check if player already exists and update
        for (int i = 0; i < rankings.length; i++) {
            if (rankings[i] != null && isExistingPlayer.test(rankings[i])) {
                rankings[i] = newRank;
                return;
            }
        }
        
        // Second, find empty slot
        for (int i = 0; i < rankings.length; i++) {
            if (rankings[i] == null) {
                rankings[i] = newRank;
                return;
            }
        }
        
        // Third, replace lower ranking player
        for (int i = 0; i < rankings.length; i++) {
            if (shouldReplace.test(rankings[i])) {
                rankings[i] = newRank;
                return;
            }
        }
    }
    
    private static boolean isEligibleForRanking(Player player) {
        return player.getRights() < 2; // Exclude administrators (rights >= 2)
    }

    // =================== GETTER METHODS FOR ANNOUNCEMENTS ===================
    
    /**
     * Get current time online rankings (thread-safe)
     */
    public static TimeOnlineRank[] getTimeOnlineRanks() {
        timeOnlineLock.readLock().lock();
        try {
            return timeOnlineRanks != null ? Arrays.copyOf(timeOnlineRanks, timeOnlineRanks.length) : new TimeOnlineRank[MAX_RANKS];
        } finally {
            timeOnlineLock.readLock().unlock();
        }
    }

    /**
     * Get current vote rankings (thread-safe)
     */
    public static VoteRank[] getVoteRanks() {
        voteLock.readLock().lock();
        try {
            return voteRanks != null ? Arrays.copyOf(voteRanks, voteRanks.length) : new VoteRank[MAX_RANKS];
        } finally {
            voteLock.readLock().unlock();
        }
    }

    /**
     * Get current donation rankings (thread-safe)
     */
    public static DonationRank[] getDonationRanks() {
        donationLock.readLock().lock();
        try {
            return donationRanks != null ? Arrays.copyOf(donationRanks, donationRanks.length) : new DonationRank[MAX_RANKS];
        } finally {
            donationLock.readLock().unlock();
        }
    }

    /**
     * Get the current rank of a specific player in time online
     */
    public static int getPlayerTimeOnlineRank(String username) {
        timeOnlineLock.readLock().lock();
        try {
            for (int i = 0; i < timeOnlineRanks.length && timeOnlineRanks[i] != null; i++) {
                if (timeOnlineRanks[i].getUsername().equalsIgnoreCase(username)) {
                    return i + 1; // Return 1-based rank
                }
            }
            return -1; // Not in rankings
        } finally {
            timeOnlineLock.readLock().unlock();
        }
    }

    /**
     * Get the current rank of a specific player in votes
     */
    public static int getPlayerVoteRank(String username) {
        voteLock.readLock().lock();
        try {
            for (int i = 0; i < voteRanks.length && voteRanks[i] != null; i++) {
                if (voteRanks[i].getUsername().equalsIgnoreCase(username)) {
                    return i + 1; // Return 1-based rank
                }
            }
            return -1; // Not in rankings
        } finally {
            voteLock.readLock().unlock();
        }
    }

    /**
     * Get the current rank of a specific player in donations
     */
    public static int getPlayerDonationRank(String username) {
        donationLock.readLock().lock();
        try {
            for (int i = 0; i < donationRanks.length && donationRanks[i] != null; i++) {
                if (donationRanks[i].getUsername().equalsIgnoreCase(username)) {
                    return i + 1; // Return 1-based rank
                }
            }
            return -1; // Not in rankings
        } finally {
            donationLock.readLock().unlock();
        }
    }

    /**
     * Check if player is in top 3 of any ranking
     */
    public static boolean isInTop3(Player player) {
        if (player == null) return false;
        String username = player.getUsername();
        
        return getPlayerTimeOnlineRank(username) <= 3 && getPlayerTimeOnlineRank(username) > 0 ||
               getPlayerVoteRank(username) <= 3 && getPlayerVoteRank(username) > 0 ||
               getPlayerDonationRank(username) <= 3 && getPlayerDonationRank(username) > 0;
    }

    /**
     * Get top player name for a specific ranking type
     */
    public static String getTopPlayerName(RankingType type) {
        switch (type) {
            case TIME_ONLINE:
                timeOnlineLock.readLock().lock();
                try {
                    return timeOnlineRanks != null && timeOnlineRanks[0] != null ? timeOnlineRanks[0].getUsername() : null;
                } finally {
                    timeOnlineLock.readLock().unlock();
                }
                
            case VOTE_COUNT:
                voteLock.readLock().lock();
                try {
                    return voteRanks != null && voteRanks[0] != null ? voteRanks[0].getUsername() : null;
                } finally {
                    voteLock.readLock().unlock();
                }
                
            case DONATION_AMOUNT:
                donationLock.readLock().lock();
                try {
                    return donationRanks != null && donationRanks[0] != null ? donationRanks[0].getUsername() : null;  
                } finally {
                    donationLock.readLock().unlock();
                }
                
            default:
                return null;
        }
    }

    // Inner classes for ranking data
    public static class TimeOnlineRank implements Serializable {
        private static final long serialVersionUID = 1374198656035761578L;
        
        private final String username;
        private final long timeOnline;

        public TimeOnlineRank(Player player) {
            if (player == null) {
                throw new IllegalArgumentException("Player cannot be null");
            }
            this.username = player.getUsername();
            this.timeOnline = player.getTimePlayedWeekly();
        }

        public String getUsername() { return username; }
        public long getTimeOnline() { return timeOnline; }
        
        @Override
        public String toString() {
            return "TimeOnlineRank{username='" + username + "', timeOnline=" + timeOnline + '}';
        }
    }

    public static class VoteRank implements Serializable {
        private static final long serialVersionUID = 6586073987522525887L;
        
        private final String username;
        private final int voteCount;

        public VoteRank(Player player) {
            if (player == null) {
                throw new IllegalArgumentException("Player cannot be null");
            }
            this.username = player.getUsername();
            this.voteCount = player.getVoteCountWeekly();
        }

        public String getUsername() { return username; }
        public int getVoteCount() { return voteCount; }
        
        @Override
        public String toString() {
            return "VoteRank{username='" + username + "', voteCount=" + voteCount + '}';
        }
    }

    public static class DonationRank implements Serializable {
        private static final long serialVersionUID = -4377661366781875709L;
        
        private final String username;
        private final int donationAmount;

        public DonationRank(Player player) {
            if (player == null) {
                throw new IllegalArgumentException("Player cannot be null");
            }
            this.username = player.getUsername();
            this.donationAmount = player.getDonationAmountWeekly();
        }

        public String getUsername() { return username; }
        public int getDonationAmount() { return donationAmount; }
        
        @Override
        public String toString() {
            return "DonationRank{username='" + username + "', donationAmount=" + donationAmount + '}';
        }
    }
}