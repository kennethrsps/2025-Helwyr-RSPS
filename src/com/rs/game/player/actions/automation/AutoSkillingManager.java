package com.rs.game.player.actions.automation;

import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.automation.handlers.AutoCookingHandler;
import com.rs.game.player.actions.automation.handlers.AutoFiremakingHandler;
import com.rs.game.player.actions.automation.handlers.AutoFishingHandler;
import com.rs.game.player.actions.automation.handlers.AutoMiningHandler;
import com.rs.game.player.actions.automation.handlers.AutoThievingHandler;
import com.rs.game.player.actions.automation.handlers.AutoWoodcuttingHandler;
import com.rs.game.player.actions.automation.handlers.SkillHandler;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.utils.Utils;
import com.rs.utils.Colors;
import java.util.List;
import java.util.ArrayList;
// ADDED: Imports required for intelligent pathfinding
import com.rs.game.route.RouteFinder;
import com.rs.game.route.strategy.ObjectStrategy;
import com.rs.game.route.strategy.FixedTileStrategy;


public class AutoSkillingManager {
    
    public enum SkillingType {
        WOODCUTTING, MINING, FISHING, COOKING, FIREMAKING, THIEVING
    }
    
    public enum AutoSkillingState {
        STOPPED, WORKING, WALKING_TO_BANK, BANKING, WALKING_TO_RESOURCE
    }
    
    public enum InventoryAction {
        AUTO_BANK, AUTO_DROP, STOP_WHEN_FULL
    }
    
    // Daily limit constants - 4 hours shared across all unified skills
    private static final long DAILY_TIME_LIMIT_MS = 4 * 60 * 60 * 1000; // 4 hours in milliseconds
    private static final long DAY_IN_MS = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
    
    // Custom Skilling Hub coordinates - Updated with actual location
    private static final WorldTile SKILLING_HUB_CENTER = new WorldTile(1375, 5669, 0);
    private static final int SKILLING_HUB_SIZE = 90;
    
    // Enhanced banking system - multiple bank IDs and dynamic discovery
    private static final int[] BANK_IDS = {89398, 2213, 11758, 27663, 28589, 29085}; // Add your server's bank object IDs
    private static final WorldTile FALLBACK_BANK_TILE = new WorldTile(1365, 5668, 0); // Original bank location
    
    // Bank discovery cache (similar to tree/rock caching)
    private static long lastBankScan = 0;
    private static final long BANK_SCAN_INTERVAL = 10000; // 10 seconds (banks don't move often)
    private static List<WorldObject> cachedBanks = new ArrayList<>();
    
    // ===================== CORE AUTO-SKILLING METHODS =====================
    
    /**
     * Check if player has time remaining today for unified skills
     */
    public static boolean hasTimeRemaining(Player player) {
        long currentTime = Utils.currentTimeMillis();
        long lastResetTime = player.getAutoSkillingLastReset();
        long usedTimeToday = player.getAutoSkillingUsedTime();
        
        // Reset daily timer if it's a new day
        if (currentTime - lastResetTime >= DAY_IN_MS) {
            player.setAutoSkillingUsedTime(0);
            player.setAutoSkillingLastReset(currentTime);
            usedTimeToday = 0;
        }
        
        return usedTimeToday < DAILY_TIME_LIMIT_MS;
    }
    
    /**
     * Get remaining time in minutes for unified skills
     */
    public static int getRemainingTimeMinutes(Player player) {
        long currentTime = Utils.currentTimeMillis();
        long lastResetTime = player.getAutoSkillingLastReset();
        long usedTimeToday = player.getAutoSkillingUsedTime();
        
        // Reset daily timer if it's a new day
        if (currentTime - lastResetTime >= DAY_IN_MS) {
            player.setAutoSkillingUsedTime(0);
            player.setAutoSkillingLastReset(currentTime);
            usedTimeToday = 0;
        }
        
        long remainingMs = DAILY_TIME_LIMIT_MS - usedTimeToday;
        return (int) (remainingMs / (60 * 1000)); // Convert to minutes
    }
    
    /**
     * Check if player is in the skilling hub
     */
    public static boolean isInSkillingHub(Player player) {
        int playerX = player.getX();
        int playerY = player.getY();
        
        // Check if within the 90x90 skilling hub area
        int radius = SKILLING_HUB_SIZE / 2;
        return playerX >= (SKILLING_HUB_CENTER.getX() - radius) && 
               playerX <= (SKILLING_HUB_CENTER.getX() + radius) &&
               playerY >= (SKILLING_HUB_CENTER.getY() - radius) && 
               playerY <= (SKILLING_HUB_CENTER.getY() + radius);
    }
    
    /**
     * Start auto-skilling for specified skill
     */
    public static void startAutoSkilling(Player player, SkillingType skill) {
        // Check daily time limit first
        if (!hasTimeRemaining(player)) {
            player.sendMessage("You have reached your daily 4-hour auto-skilling limit! Try again tomorrow.");
            return;
        }
        
        // Check if in skilling hub
        if (!isInSkillingHub(player)) {
            player.sendMessage("Auto-" + skill.name().toLowerCase() + " can only be used in the skilling hub!");
            return;
        }
        
        // AUTO-FIX: Reset stale state after server restart
        if (player.getAutoSkillingState() != AutoSkillingState.STOPPED) {
            // Check if there's actually a running task by checking action delay
            if (player.getActionManager().getActionDelay() == 0) {
                // No active delay = no running task = stale state from server restart
                player.setAutoSkillingState(AutoSkillingState.STOPPED);
                player.sendMessage("Auto-skilling state reset after server restart.");
            } else {
                // Actually running
                player.sendMessage("Auto-skilling is already running! Stop it first.");
                return;
            }
        }
        
        // Get and validate skill handler
        SkillHandler handler = getHandler(skill);
        if (handler == null) {
            player.sendMessage("This skill is not supported yet!");
            return;
        }
        
        if (!handler.canStart(player)) {
            return; // Error message sent by handler
        }
        
        // Start the automation
        player.setAutoSkillingState(AutoSkillingState.WORKING);
        player.setAutoSkillingType(skill);
        startTimeTracking(player);
        startSessionTracking(player);
        
        int remainingMinutes = getRemainingTimeMinutes(player);
        player.sendMessage("Auto-" + skill.name().toLowerCase() + " started! Time remaining: " + remainingMinutes + " minutes");
        
        // Start processing loop
        startProcessingLoop(player);
    }
    
    /**
     * Stop auto-skilling
     */
    public static void stopAutoSkilling(Player player) {
        try {
            if (player == null || player.hasFinished()) {
                System.out.println("WARNING: Attempted to stop auto-skilling for null/finished player");
                return;
            }
            
            stopTimeTracking(player);
            player.setAutoSkillingState(AutoSkillingState.STOPPED);
            
            if (player.getActionManager() != null) {
                player.getActionManager().forceStop();
            }
            
            // Reset session tracking
            player.setAutoSkillingStartXP(0.0);
            
            // Clean up temporary banking data
            if (player.getTemporaryAttributtes() != null) {
                player.getTemporaryAttributtes().remove("target_bank");
            }
            
            int remainingMinutes = getRemainingTimeMinutes(player);
            if (player.isRunning()) { // Only send message if player is still connected
                player.sendMessage("Auto-skilling stopped! Time remaining today: " + remainingMinutes + " minutes");
            }
            
        } catch (Exception e) {
            System.out.println("ERROR in stopAutoSkilling: " + e.getMessage());
            // Force reset state even if other operations fail
            if (player != null) {
                player.setAutoSkillingState(AutoSkillingState.STOPPED);
            }
        }
    }
    
    /**
     * Enhanced login detection to handle returning players
     */
    public static void handlePlayerLogin(Player player) {
        try {
            if (player == null) return;
            
            // Check for stale auto-skilling state from previous session
            if (player.getAutoSkillingState() != AutoSkillingState.STOPPED) {
                System.out.println("DEBUG: Player " + player.getUsername() + " logged in with active auto-skilling state. Resetting...");
                
                // Reset to stopped state (they need to manually restart)
                player.setAutoSkillingState(AutoSkillingState.STOPPED);
                player.setAutoSkillingStartXP(0.0);
                
                // Clean up temporary banking data
                if (player.getTemporaryAttributtes() != null) {
                    player.getTemporaryAttributtes().remove("target_bank");
                }
                
                // Force stop any stale actions
                if (player.getActionManager() != null) {
                    player.getActionManager().forceStop();
                }
                
                // No message sent here as this is handled by a different fix
            }
            
        } catch (Exception e) {
            System.out.println("ERROR handling player login auto-skilling check: " + e.getMessage());
        }
    }
    
    public static void handlePlayerLogout(Player player) {
        try {
            if (player == null) return;
            
            // Check if player was auto-skilling
            if (player.getAutoSkillingState() != AutoSkillingState.STOPPED) {
                System.out.println("DEBUG: Player " + player.getUsername() + " logged out while auto-skilling. Cleaning up...");
                
                // Stop time tracking and save session data
                stopTimeTracking(player);
                
                // Force stop any running actions
                if (player.getActionManager() != null) {
                    player.getActionManager().forceStop();
                }
                
                // Reset auto-skilling state to stopped
                player.setAutoSkillingState(AutoSkillingState.STOPPED);
                
                // Clean up temporary banking data
                if (player.getTemporaryAttributtes() != null) {
                    player.getTemporaryAttributtes().remove("target_bank");
                }
                
                // Save session XP data before logout (optional but recommended)
                saveSessionDataOnLogout(player);
                
                System.out.println("DEBUG: Auto-skilling cleanup completed for " + player.getUsername());
            }
            
        } catch (Exception e) {
            System.out.println("ERROR: Failed to clean up auto-skilling for player logout: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Enhanced processing loop with better logout detection
     */
    private static void startProcessingLoop(Player player) {
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                try {
                    // Enhanced logout detection
                    if (player == null || player.hasFinished() || !player.isRunning()) {
                        System.out.println("DEBUG: Player logged out during auto-skilling. Stopping task.");
                        stop(); // Stop this WorldTask
                        return;
                    }
                    
                    // Additional safety check
                    if (player.getAutoSkillingState() == AutoSkillingState.STOPPED) {
                        stop(); // Stop this WorldTask
                        return;
                    }
                    
                    // Check if player is still in game world
                    if (!World.containsPlayer(player.getUsername())) {
                        System.out.println("DEBUG: Player no longer in world. Stopping auto-skilling task.");
                        stop(); // Stop this WorldTask
                        return;
                    }
                    
                    process(player);
                    
                } catch (Exception e) {
                    System.out.println("ERROR in auto-skilling processing loop: " + e.getMessage());
                    e.printStackTrace();
                    stop(); // Stop this WorldTask on any error
                }
            }
        }, 0, 2);
    }
    
    /**
     * Save session data when player logs out (optional but recommended)
     */
    private static void saveSessionDataOnLogout(Player player) {
        try {
            double sessionXP = getSessionXPGained(player);
            long sessionTime = Utils.currentTimeMillis() - player.getAutoSkillingStartTime();
            
            if (sessionXP > 0 || sessionTime > 60000) { // Only save if meaningful session
                // You could save this to database or player file for statistics
                System.out.println("Session data for " + player.getUsername() + ": " + 
                                 (int)sessionXP + " XP in " + (sessionTime/60000) + " minutes");
            }
            
            // Reset session tracking
            player.setAutoSkillingStartXP(0.0);
            
        } catch (Exception e) {
            System.out.println("ERROR saving session data: " + e.getMessage());
        }
    }
    
    /**
     * Enhanced process method with additional safety checks
     */
    public static void process(Player player) {
        try {
            // Safety check at start of every process cycle
            if (player == null || player.hasFinished() || !player.isRunning()) {
                System.out.println("DEBUG: Player disconnected during processing. Stopping auto-skilling.");
                return;
            }
            
            // Check area restriction
            if (!isInSkillingHub(player)) {
                stopAutoSkilling(player);
                player.sendMessage("Auto-skilling stopped - you left the skilling hub!");
                return;
            }
            
            // Check daily time limit
            if (!hasTimeRemaining(player)) {
                stopTimeTracking(player);
                player.setAutoSkillingState(AutoSkillingState.STOPPED);
                player.getActionManager().forceStop();
                player.sendMessage("Daily 4-hour auto-skilling limit reached! Auto-skilling stopped. Try again tomorrow.");
                return;
            }
            
            AutoSkillingState state = player.getAutoSkillingState();
            
            switch (state) {
                case WORKING:
                    processWorking(player);
                    break;
                    
                case WALKING_TO_BANK:
                    processWalkingToBank(player);
                    break;
                    
                case BANKING:
                    processBanking(player);
                    break;
                    
                case WALKING_TO_RESOURCE:
                    processWalkingToResource(player);
                    break;
                    
                case STOPPED:
                    break;
            }
            
        } catch (Exception e) {
            System.out.println("ERROR in auto-skilling process for " + 
                              (player != null ? player.getUsername() : "null player") + ": " + e.getMessage());
            e.printStackTrace();
            
            // On any error, safely stop auto-skilling
            if (player != null) {
                try {
                    stopAutoSkilling(player);
                } catch (Exception stopError) {
                    // If even stopping fails, force reset the state
                    player.setAutoSkillingState(AutoSkillingState.STOPPED);
                }
            }
        }
    }
    
    /**
     * Delegate working to appropriate skill handler
     */
    private static void processWorking(Player player) {
        if (player.getInventory().getFreeSlots() <= 1) {
            handleInventoryFull(player);
            return;
        }
        
        SkillingType skill = player.getAutoSkillingType();
        SkillHandler handler = getHandler(skill);
        
        if (handler != null) {
            handler.process(player);
        }
    }
    
    /**
     * Get the appropriate handler for the skill
     */
    private static SkillHandler getHandler(SkillingType skill) {
        switch (skill) {
            case WOODCUTTING:
                return AutoWoodcuttingHandler.getInstance();
            case MINING:
                return AutoMiningHandler.getInstance();
            case FISHING:
                return AutoFishingHandler.getInstance();
            case COOKING:
                return AutoCookingHandler.getInstance();
            case FIREMAKING:
                return AutoFiremakingHandler.getInstance();
            case THIEVING:  // ADD THIS CASE
                return AutoThievingHandler.getInstance();
            default:
                return null;
        }
    }
    
    // ===================== BANKING METHODS =====================
    // THIS SECTION HAS BEEN MODIFIED FOR PATHFINDING
    
    /**
     * NEW: The master pathfinding check from the other working handlers.
     */
    private static boolean canReachTarget(Player player, WorldTile target) {
        int result = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER,
                                            player.getX(), player.getY(), player.getPlane(),
                                            player.getSize(),
                                            target instanceof WorldObject ? new ObjectStrategy((WorldObject) target) : new FixedTileStrategy(target.getX(), target.getY()),
                                            true);
        return result >= 0;
    }

    /**
     * Find all available banks within the skilling hub (Unchanged as requested)
     */
    private static List<WorldObject> findBanksInHub() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBankScan < BANK_SCAN_INTERVAL && !cachedBanks.isEmpty()) {
            return cachedBanks;
        }
        List<WorldObject> banksFound = new ArrayList<>();
        try {
            int radius = SKILLING_HUB_SIZE / 2;
            int minX = SKILLING_HUB_CENTER.getX() - radius;
            int maxX = SKILLING_HUB_CENTER.getX() + radius;
            int minY = SKILLING_HUB_CENTER.getY() - radius;
            int maxY = SKILLING_HUB_CENTER.getY() + radius;
            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    WorldTile tile = new WorldTile(x, y, 0);
                    for (int bankId : BANK_IDS) {
                        WorldObject bank = World.getObjectWithId(tile, bankId);
                        if (bank != null) {
                            banksFound.add(bank);
                        }
                    }
                }
            }
            cachedBanks = banksFound;
            lastBankScan = currentTime;
        } catch (Exception e) {
            System.out.println("ERROR scanning banks in hub: " + e.getMessage());
        }
        return banksFound;
    }
    
    /**
     * Find the closest bank to the player (Unchanged as requested)
     */
    private static WorldObject findClosestBank(Player player) {
        List<WorldObject> availableBanks = findBanksInHub();
        if (availableBanks.isEmpty()) {
            return findBankFallback(player);
        }
        WorldObject closestBank = null;
        int shortestDistance = Integer.MAX_VALUE;
        for (WorldObject bank : availableBanks) {
            int distance = getDistanceToBank(player, bank);
            if (distance < shortestDistance) {
                closestBank = bank;
                shortestDistance = distance;
            }
        }
        return closestBank;
    }
    
    /**
     * Fallback bank search (Unchanged as requested)
     */
    private static WorldObject findBankFallback(Player player) {
        try {
            for (int bankId : BANK_IDS) {
                WorldObject bank = World.getObjectWithId(FALLBACK_BANK_TILE, bankId);
                if (bank != null) {
                    return bank;
                }
            }
            for (WorldObject obj : World.getRegion(player.getRegionId()).getAllObjects()) {
                for (int bankId : BANK_IDS) {
                    if (obj.getId() == bankId && player.withinDistance(obj, 15)) {
                        return obj;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR in bank fallback search: " + e.getMessage());
        }
        return null;
    }
    
    private static int getDistanceToBank(Player player, WorldObject bank) {
        return Math.abs(player.getX() - bank.getX()) + Math.abs(player.getY() - bank.getY());
    }
    
    private static void handleInventoryFull(Player player) {
        InventoryAction action = player.getSkillingInventoryAction();
        player.getActionManager().forceStop(); // Stop current skilling action first
        switch (action) {
            case AUTO_BANK:
                startBankingSequence(player);
                break;
            case AUTO_DROP:
                dropItems(player);
                player.setAutoSkillingState(AutoSkillingState.WORKING); // Go back to work after dropping
                break;
            case STOP_WHEN_FULL:
                stopAutoSkilling(player);
                player.sendMessage("Auto-skilling stopped - inventory full!");
                break;
        }
    }
    
    /**
     * FIXED: This now uses intelligent pathfinding.
     */
    private static void startBankingSequence(Player player) {
        WorldObject targetBank = findClosestBank(player);
        if (targetBank == null) {
            player.sendMessage("No bank found in the area! Stopping auto-skilling.");
            stopAutoSkilling(player);
            return;
        }
        
        player.getTemporaryAttributtes().put("target_bank", targetBank);
        player.setAutoSkillingState(AutoSkillingState.WALKING_TO_BANK);
        player.sendMessage("Inventory full, going to bank...");
        
        // REPLACED: player.addWalkStepsInteract with player.calcFollow
        if (!player.calcFollow(targetBank, true)) {
            player.sendMessage(Colors.red + "Could not find a path to the bank. Stopping.");
            stopAutoSkilling(player);
        }
    }
    
    /**
     * FIXED: This now uses intelligent pathfinding.
     */
    private static void processWalkingToBank(Player player) {
        WorldObject targetBank = (WorldObject) player.getTemporaryAttributtes().get("target_bank");
        if (targetBank == null) {
            stopAutoSkilling(player);
            player.sendMessage(Colors.red + "Target bank lost. Stopping.");
            return;
        }
        
        // Check if we have arrived and can actually interact
        if (player.withinDistance(targetBank, 2) && canReachTarget(player, targetBank)) {
            player.setAutoSkillingState(AutoSkillingState.BANKING);
            player.sendMessage("Arrived at bank, opening bank...");
        } else if (!player.hasWalkSteps()) {
            // Player got stuck or stopped, re-issue pathfinding command
            if (!player.calcFollow(targetBank, true)) {
                player.sendMessage(Colors.red + "Path to bank is blocked. Stopping.");
                stopAutoSkilling(player);
            }
        }
    }
    
    private static void processBanking(Player player) {
        WorldObject targetBank = (WorldObject) player.getTemporaryAttributtes().get("target_bank");
        if (targetBank == null) {
            stopAutoSkilling(player);
            return;
        }
        if (!isAtBank(player, targetBank)) {
            player.setAutoSkillingState(AutoSkillingState.WALKING_TO_BANK);
            return;
        }
        if (!player.getInterfaceManager().containsBankInterface()) {
            player.getBank().openPlayerBank();
        }
        player.getBank().depositAllInventory(true);
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                player.closeInterfaces();
                player.setAutoSkillingState(AutoSkillingState.WALKING_TO_RESOURCE);
                player.sendMessage("Banking complete, returning to resource area...");
            }
        }, 2);
    }
    
    /**
     * FIXED: This now uses intelligent pathfinding.
     */
    private static void processWalkingToResource(Player player) {
        // If we are already close to the center, start working.
        if (player.withinDistance(SKILLING_HUB_CENTER, 10)) {
            player.setAutoSkillingState(AutoSkillingState.WORKING);
            return;
        }

        // If not walking, start pathfinding towards the hub center.
        if (!player.hasWalkSteps()) {
            // REPLACED: player.addWalkStepsInteract with player.calcFollow
            if (!player.calcFollow(SKILLING_HUB_CENTER, true)) {
                player.sendMessage(Colors.red + "Could not find a path back to the skilling area. Stopping.");
                stopAutoSkilling(player);
            }
        }
    }
    
    // --- Unchanged Methods Below ---
    
    private static void dropItems(Player player) {
        player.sendMessage("Dropping all items to continue skilling...");
        java.util.List<Integer> itemsToDropSlots = new java.util.ArrayList<>();
        for (int i = 0; i < 28; i++) {
            Item item = player.getInventory().getItem(i);
            if (item != null) {
                itemsToDropSlots.add(i);
            }
        }
        if (itemsToDropSlots.isEmpty()) {
            player.sendMessage("No items to drop!");
            return;
        }
        dropItemsWithDelay(player, itemsToDropSlots, 0);
    }
    
    private static void dropItemsWithDelay(Player player, java.util.List<Integer> slotsToDelete, int currentIndex) {
        if (currentIndex >= slotsToDelete.size()) {
            player.sendMessage("Finished dropping items!");
            return;
        }
        int slot = slotsToDelete.get(currentIndex);
        Item item = player.getInventory().getItem(slot);
        if (item != null) {
            int offsetX = Utils.random(-1, 2);
            int offsetY = Utils.random(-1, 2);
            WorldTile dropTile = new WorldTile(player.getX() + offsetX, player.getY() + offsetY, player.getPlane());
            player.getInventory().deleteItem(slot, item);
            World.addGroundItem(item, dropTile, player, true, 180, 1);
        }
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                dropItemsWithDelay(player, slotsToDelete, currentIndex + 1);
            }
        }, 2);
    }
    
    private static boolean isAtBank(Player player, WorldObject bank) {
        if (bank == null) {
            return false;
        }
        return player.withinDistance(new WorldTile(bank.getX(), bank.getY(), bank.getPlane()), 3);
    }
    
    private static boolean isAtBank(Player player) {
        WorldObject nearbyBank = findClosestBank(player);
        return nearbyBank != null && isAtBank(player, nearbyBank);
    }
    
    private static void startTimeTracking(Player player) {
        player.setAutoSkillingStartTime(Utils.currentTimeMillis());
    }
    
    private static void stopTimeTracking(Player player) {
        long currentTime = Utils.currentTimeMillis();
        long startTime = player.getAutoSkillingStartTime();
        if (startTime > 0) {
            long sessionTime = currentTime - startTime;
            long totalUsedTime = player.getAutoSkillingUsedTime() + sessionTime;
            player.setAutoSkillingUsedTime(totalUsedTime);
            player.setAutoSkillingStartTime(0);
        }
    }
    
    private static void startSessionTracking(Player player) {
        SkillingType skill = player.getAutoSkillingType();
        if (skill != null) {
            double currentXP = getCurrentSkillXP(player);
            player.setAutoSkillingStartXP(currentXP);
        }
    }
    
    public static int getCurrentSkillLevel(Player player) {
        try {
            SkillingType currentSkill = player.getAutoSkillingType();
            if (currentSkill == null) return -1;
            return player.getSkills().getLevel(getSkillIdFromType(currentSkill));
        } catch (Exception e) { return -1; }
    }

    public static double getCurrentSkillXP(Player player) {
        try {
            SkillingType currentSkill = player.getAutoSkillingType();
            if (currentSkill == null) return -1;
            return player.getSkills().getXp(getSkillIdFromType(currentSkill));
        } catch (Exception e) { return -1; }
    }

    public static String getSkillLevelsWeb(Player player) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("woodcutting:").append(player.getSkills().getLevel(Skills.WOODCUTTING)).append(",");
            sb.append("mining:").append(player.getSkills().getLevel(Skills.MINING)).append(",");
            sb.append("fishing:").append(player.getSkills().getLevel(Skills.FISHING)).append(",");
            sb.append("cooking:").append(player.getSkills().getLevel(Skills.COOKING)).append(",");
            sb.append("firemaking:").append(player.getSkills().getLevel(Skills.FIREMAKING));
            return sb.toString();
        } catch (Exception e) { return "error"; }
    }

    public static int getSkillProgressPercent(Player player) {
        try {
            SkillingType currentSkill = player.getAutoSkillingType();
            if (currentSkill == null) return 0;
            int skillId = getSkillIdFromType(currentSkill);
            if (skillId == -1) return 0;
            int currentLevel = player.getSkills().getLevel(skillId);
            if (currentLevel >= 99) return 100;
            double currentXP = player.getSkills().getXp(skillId);
            double xpForCurrentLevel = Skills.getXPForLevel(currentLevel);
            double xpForNextLevel = Skills.getXPForLevel(currentLevel + 1);
            double progress = (currentXP - xpForCurrentLevel) / (xpForNextLevel - xpForCurrentLevel);
            return Math.max(0, Math.min(100, (int)(progress * 100)));
        } catch (Exception e) { return 0; }
    }

    public static double getSessionXPGained(Player player) {
        try {
            if (player.getAutoSkillingStartXP() <= 0) return 0.0;
            return Math.max(0.0, getCurrentSkillXP(player) - player.getAutoSkillingStartXP());
        } catch (Exception e) { return 0.0; }
    }

    public static double getSessionXPPerHour(Player player) {
        try {
            double xpGained = getSessionXPGained(player);
            long sessionTime = Utils.currentTimeMillis() - player.getAutoSkillingStartTime();
            if (sessionTime <= 0) return 0.0;
            double hoursElapsed = sessionTime / 3600000.0;
            if (hoursElapsed <= 0) return 0.0;
            return xpGained / hoursElapsed;
        } catch (Exception e) { return 0.0; }
    }

    public static String getSessionStatsWeb(Player player) {
        try {
            long sessionTime = Utils.currentTimeMillis() - player.getAutoSkillingStartTime();
            long hours = sessionTime / 3600000;
            long minutes = (sessionTime % 3600000) / 60000;
            return "xpGained:" + (int)getSessionXPGained(player) + ",xpPerHour:" + (int)getSessionXPPerHour(player) + ",sessionTime:" + hours + "h" + minutes + "m";
        } catch (Exception e) { return "error"; }
    }
    
    public static boolean startAutoSkillingWeb(Player player, String skillName) {
        try {
            SkillingType skill = parseSkillingType(skillName);
            if (skill == null) {
                player.sendMessage("Invalid skill: " + skillName);
                return false;
            }
            startAutoSkilling(player, skill);
            return player.getAutoSkillingState() == AutoSkillingState.WORKING;
        } catch (Exception e) {
            player.sendMessage("Error starting auto-skilling: " + e.getMessage());
            return false;
        }
    }
    
    public static boolean stopAutoSkillingWeb(Player player) {
        try {
            if (player.getAutoSkillingState() == AutoSkillingState.STOPPED) {
                player.sendMessage("Auto-skilling is not running.");
                return false;
            }
            stopAutoSkilling(player);
            return player.getAutoSkillingState() == AutoSkillingState.STOPPED;
        } catch (Exception e) {
            player.sendMessage("Error stopping auto-skilling: " + e.getMessage());
            return false;
        }
    }

    public static boolean isAutoSkillingWeb(Player player) {
        try {
            return player.getAutoSkillingState() != AutoSkillingState.STOPPED;
        } catch (Exception e) { return false; }
    }

    public static String getCurrentSkillWeb(Player player) {
        try {
            if (player.getAutoSkillingState() == AutoSkillingState.STOPPED) return "None";
            SkillingType currentSkill = player.getAutoSkillingType();
            return currentSkill == null ? "Unknown" : formatSkillName(currentSkill);
        } catch (Exception e) { return "Error"; }
    }
    
    public static String getRemainingTimeWeb(Player player) {
        try {
            int remainingMinutes = getRemainingTimeMinutes(player);
            if (remainingMinutes <= 0) return "No time remaining";
            int hours = remainingMinutes / 60;
            int minutes = remainingMinutes % 60;
            return hours > 0 ? hours + "h " + minutes + "m" : minutes + "m";
        } catch (Exception e) { return "Unknown"; }
    }
    
    public static String getStatusInfoWeb(Player player) {
        try {
            AutoSkillingState state = player.getAutoSkillingState();
            String timeLeft = getRemainingTimeWeb(player);
            switch (state) {
                case WORKING:
                    SkillingType skill = player.getAutoSkillingType();
                    String skillName = skill != null ? formatSkillName(skill) : "Unknown";
                    int skillLevel = getCurrentSkillLevel(player);
                    int progressPercent = getSkillProgressPercent(player);
                    return "Active: " + skillName + " (Level " + skillLevel + ") - " + progressPercent + "% (" + timeLeft + " left)";
                case WALKING_TO_BANK: return "Banking: Going to bank (" + timeLeft + " left)";
                case BANKING: return "Banking: Depositing items (" + timeLeft + " left)";
                case WALKING_TO_RESOURCE: return "Moving: Returning to skill area (" + timeLeft + " left)";
                case STOPPED: return "Idle (" + timeLeft + " left today)";
                default: return "Unknown status";
            }
        } catch (Exception e) { return "Error retrieving status"; }
    }
    
    public static String getBankingDebugInfo(Player player) {
        StringBuilder info = new StringBuilder("=== Banking Debug Info ===\n");
        List<WorldObject> banks = findBanksInHub();
        info.append("Banks found in hub: ").append(banks.size()).append("\n");
        for (WorldObject bank : banks) {
            info.append("Bank ID ").append(bank.getId()).append(" at (").append(bank.getX()).append(", ").append(bank.getY()).append(") - Distance: ").append(getDistanceToBank(player, bank)).append("\n");
        }
        WorldObject targetBank = (WorldObject) player.getTemporaryAttributtes().get("target_bank");
        if (targetBank != null) {
            info.append("Current target bank: ID ").append(targetBank.getId()).append(" at (").append(targetBank.getX()).append(", ").append(targetBank.getY()).append(")\n");
        } else {
            info.append("No target bank currently set\n");
        }
        info.append("Player location: (").append(player.getX()).append(", ").append(player.getY()).append(")\n");
        info.append("At bank: ").append(isAtBank(player)).append("\n");
        return info.toString();
    }
    
    public static void refreshBankCache() {
        lastBankScan = 0;
        cachedBanks.clear();
        System.out.println("DEBUG: Bank cache manually refreshed");
    }
    
    private static int getSkillIdFromType(SkillingType skillType) {
        switch (skillType) {
            case WOODCUTTING: return Skills.WOODCUTTING;
            case MINING: return Skills.MINING;
            case FISHING: return Skills.FISHING;
            case COOKING: return Skills.COOKING;
            case FIREMAKING: return Skills.FIREMAKING;
            case THIEVING: return Skills.THIEVING;
            default: return -1;
        }
    }
    
    private static SkillingType parseSkillingType(String skillName) {
        if (skillName == null) return null;
        try {
            return SkillingType.valueOf(skillName.toUpperCase().trim());
        } catch (Exception e) { return null; }
    }

    private static String formatSkillName(SkillingType skill) {
        if (skill == null) return "Unknown";
        try {
            String name = skill.name().toLowerCase();
            return Character.toUpperCase(name.charAt(0)) + name.substring(1);
        } catch (Exception e) { return "Unknown"; }
    }
    
    public static String getSkillingHubInfo() {
        return String.format("Skilling Hub: Center(%d, %d, %d) Size: %dx%d Bank: (%d, %d, %d)", 
            SKILLING_HUB_CENTER.getX(), SKILLING_HUB_CENTER.getY(), SKILLING_HUB_CENTER.getPlane(),
            SKILLING_HUB_SIZE, SKILLING_HUB_SIZE,
            FALLBACK_BANK_TILE.getX(), FALLBACK_BANK_TILE.getY(), FALLBACK_BANK_TILE.getPlane());
    }

    public static double getTotalXP(Player player) {
        try {
            double totalXP = 0;
            for (int skillId = 0; skillId < 27; skillId++) {
                try { totalXP += player.getSkills().getXp(skillId); } catch (Exception e) {}
            }
            return totalXP;
        } catch (Exception e) { return 0.0; }
    }

    public static double getTotalXPLoop(Player player) { return getTotalXP(player); }

    public static double getWoodcuttingXP(Player player) {
        try { return player.getSkills().getXp(Skills.WOODCUTTING); } catch (Exception e) { return 0.0; }
    }

    public static String getAllSkillLevelsWeb(Player player) {
        try {
            StringBuilder sb = new StringBuilder();
            String[] skillNames = {"attack", "defence", "strength", "hitpoints", "range", "prayer", "magic", "cooking", "woodcutting", "fletching", "fishing", "firemaking", "crafting", "smithing", "mining", "herblore", "agility", "thieving", "slayer", "farming", "runecrafting", "hunter", "construction", "summoning", "dungeoneering"};
            for (int i = 0; i < skillNames.length; i++) {
                sb.append(skillNames[i]).append(":").append(player.getSkills().getLevel(i)).append(i == skillNames.length - 1 ? "" : ",");
            }
            return sb.toString();
        } catch (Exception e) { return "error"; }
    }
}