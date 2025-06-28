package com.rs.game.player.actions.automation.handlers;

import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Fishing;
import com.rs.game.player.actions.Fishing.FishingSpots;
import com.rs.game.npc.NPC;
import com.rs.game.WorldTile;
import com.rs.game.World;
import java.util.List;
import java.util.ArrayList;

/**
 * Auto-Fishing Handler with Bank Waypoint Navigation
 * 
 * Features:
 * - Uses specific fishing spot locations in skilling hub
 * - Smart fishing spot prioritization based on level and availability
 * - Bank waypoint navigation for obstacle avoidance
 * - Supports both AUTO and SPECIFIC fishing modes
 * - Two-phase movement: Player → Bank → Fishing Area
 */
public class AutoFishingHandler implements SkillHandler {
    
    private static AutoFishingHandler instance;
    
    // Fishing spot mappings with your exact locations
    private static final FishingSpotMapping[] FISHING_SPOT_MAPPINGS = {
        // NET spots (ID 327) - Level 1
        new FishingSpotMapping(327, new WorldTile(1371, 5670, 0), FishingSpots.NET, 1),
        new FishingSpotMapping(327, new WorldTile(1373, 5670, 0), FishingSpots.NET, 1),
        
        // LURE spots (ID 329) - Level 20 for Trout
        new FishingSpotMapping(329, new WorldTile(1375, 5670, 0), FishingSpots.LURE2, 20),
        new FishingSpotMapping(329, new WorldTile(1377, 5670, 0), FishingSpots.LURE2, 20),
        
        // CAGE/HARPOON spots (ID 312) - Level 40 for Lobster, 35 for Tuna
        new FishingSpotMapping(312, new WorldTile(1379, 5670, 0), FishingSpots.CAGE2, 40),
        new FishingSpotMapping(312, new WorldTile(1371, 5672, 0), FishingSpots.HARPOON, 35),
        
        // CAVEFISH spots (ID 8841) - Level 85
        new FishingSpotMapping(8841, new WorldTile(1373, 5672, 0), FishingSpots.CAVEFISH_SHOAL, 85),
        new FishingSpotMapping(8841, new WorldTile(1375, 5672, 0), FishingSpots.CAVEFISH_SHOAL, 85),
        
        // ROCKTAIL spots (ID 8842) - Level 90
        new FishingSpotMapping(8842, new WorldTile(1377, 5672, 0), FishingSpots.ROCKTAIL_SHOAL, 90),
        new FishingSpotMapping(8842, new WorldTile(1379, 5672, 0), FishingSpots.ROCKTAIL_SHOAL, 90),
    };
    
    // Cache for fishing spot scanning
    private long lastSpotScan = 0;
    private static final long SPOT_SCAN_INTERVAL = 5000; // 5 seconds
    private List<FishingSpotInfo> availableSpots = new ArrayList<>();
    
    // Bank waypoint navigation (same as woodcutting/mining)
    private static final int[] BANK_IDS = {89398, 2213, 11758, 27663, 28589, 29085};
    private static final long MESSAGE_THROTTLE_TIME = 5000; // 5 seconds between messages
    private static long lastWalkMessage = 0;
    
    public static AutoFishingHandler getInstance() {
        if (instance == null) {
            instance = new AutoFishingHandler();
        }
        return instance;
    }
    
    @Override
    public void process(Player player) {
        try {
            // Quick inventory check
            if (player.getInventory().getFreeSlots() <= 0) {
                return; // Let auto-skilling manager handle full inventory
            }
            
            // Validate player is in skilling hub
            if (!isInSkillingHub(player)) {
                return; // Player moved outside hub
            }
            
            // Check for fishing spot upgrades (AUTO mode only)
            String mode = player.getAutoFishingMode();
            if ("AUTO".equals(mode)) {
                checkForSpotUpgrade(player);
            }
            
            // If already fishing, let it continue
            if (player.getActionManager().getAction() instanceof Fishing) {
                return;
            }
            
            // If busy with other actions, wait
            if (player.getActionManager().getAction() != null) {
                return;
            }
            
            // If player is in hub but not near fishing spots, use bank waypoint navigation
            if (!isNearFishingArea(player)) {
                walkToFishingAreaViaBank(player);
                return;
            }
            
            // Find and start fishing at appropriate spot
            startFishingAtSpot(player);
            
        } catch (Exception e) {
            System.out.println("ERROR in AutoFishingHandler.process: " + e.getMessage());
        }
    }
    
    /**
     * Walk to fishing area using bank as waypoint (same method as woodcutting/mining)
     */
    private void walkToFishingAreaViaBank(Player player) {
        // Check if we already have a target bank stored
        NPC targetBank = (NPC) player.getTemporaryAttributtes().get("waypoint_bank_fishing");
        
        if (targetBank == null) {
            // No target bank stored, find a new one (same as banking system)
            targetBank = findClosestBank(player);
            if (targetBank == null) {
                sendThrottledMessage(player, "No bank found for waypoint navigation!");
                return;
            }
            player.getTemporaryAttributtes().put("waypoint_bank_fishing", targetBank);
        }
        
        // Check if we reached the bank waypoint
        if (isAtBank(player, targetBank)) {
            // We're at the bank, now continue to fishing area
            player.getTemporaryAttributtes().remove("waypoint_bank_fishing"); // Clean up
            sendThrottledMessage(player, "Reached waypoint, continuing to fishing area...");
            walkToFishingArea(player);
            return;
        }
        
        // Not at bank yet, keep walking there
        if (!player.hasWalkSteps()) {
            player.addWalkStepsInteract(targetBank.getX(), targetBank.getY(), -1, 1, true);
            sendThrottledMessage(player, "Using bank as waypoint to reach fishing area...");
        }
    }
    
    /**
     * Walk player to fishing area (when they're in hub but away from spots)
     */
    private void walkToFishingArea(Player player) {
        // Find the center of the fishing area for walking back
        WorldTile fishingCenterTile = new WorldTile(1375, 5671, 0); // Center of fishing spots
        
        // Only try to walk if not already walking
        if (!player.hasWalkSteps()) {
            player.addWalkSteps(fishingCenterTile.getX(), fishingCenterTile.getY(), -1, false);
        }
    }
    
    /**
     * Find the closest bank (for waypoint navigation)
     */
    private NPC findClosestBank(Player player) {
        // Note: This is simplified - you may need to adapt based on how banks are implemented
        // For now, return null and let the system handle it differently
        return null;
    }
    
    /**
     * Check if player is at bank
     */
    private boolean isAtBank(Player player, NPC bank) {
        if (bank == null) {
            return false;
        }
        return player.withinDistance(new WorldTile(bank.getX(), bank.getY(), bank.getPlane()), 3);
    }
    
    /**
     * Send throttled message to prevent spam
     */
    private void sendThrottledMessage(Player player, String message) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastWalkMessage > MESSAGE_THROTTLE_TIME) {
            player.sendMessage(message);
            lastWalkMessage = currentTime;
        }
    }
    
    /**
     * Check if player is near fishing area
     */
    private boolean isNearFishingArea(Player player) {
        for (FishingSpotMapping mapping : FISHING_SPOT_MAPPINGS) {
            if (player.withinDistance(mapping.location, 15)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if player is in the skilling hub
     */
    private boolean isInSkillingHub(Player player) {
        // Skilling hub coordinates
        final int SKILLING_HUB_CENTER_X = 1375;
        final int SKILLING_HUB_CENTER_Y = 5669;
        final int SKILLING_HUB_SIZE = 90;
        
        int playerX = player.getX();
        int playerY = player.getY();
        
        // Check if within the 90x90 skilling hub area
        int radius = SKILLING_HUB_SIZE / 2;
        return playerX >= (SKILLING_HUB_CENTER_X - radius) && 
               playerX <= (SKILLING_HUB_CENTER_X + radius) &&
               playerY >= (SKILLING_HUB_CENTER_Y - radius) && 
               playerY <= (SKILLING_HUB_CENTER_Y + radius);
    }
    
    /**
     * Scan for available fishing spots
     */
    private List<FishingSpotInfo> findFishingSpotsInArea() {
        long currentTime = System.currentTimeMillis();
        
        // Use cached results if recent scan
        if (currentTime - lastSpotScan < SPOT_SCAN_INTERVAL && !availableSpots.isEmpty()) {
            return availableSpots;
        }
        
        List<FishingSpotInfo> spotsFound = new ArrayList<>();
        
        try {
            // Check each known fishing spot location
            for (FishingSpotMapping mapping : FISHING_SPOT_MAPPINGS) {
                // Look for NPC at this location
                for (NPC npc : World.getNPCs()) {
                    if (npc != null && npc.getId() == mapping.npcId && 
                        npc.withinDistance(mapping.location, 2)) {
                        
                        FishingSpotInfo spotInfo = new FishingSpotInfo(
                            npc, mapping.fishingSpot, mapping.levelRequired
                        );
                        spotsFound.add(spotInfo);
                        break;
                    }
                }
            }
            
            // Update cache
            availableSpots = spotsFound;
            lastSpotScan = currentTime;
            
        } catch (Exception e) {
            System.out.println("ERROR scanning fishing spots: " + e.getMessage());
        }
        
        return spotsFound;
    }
    
    /**
     * Find best fishing spot for player
     */
    private FishingSpotInfo findBestFishingSpot(Player player) {
        FishingSpots targetSpot = player.getAutoFishingSpot();
        if (targetSpot == null) {
            return null;
        }
        
        List<FishingSpotInfo> availableSpots = findFishingSpotsInArea();
        if (availableSpots.isEmpty()) {
            return null;
        }
        
        // Find spots of the target type
        List<FishingSpotInfo> targetSpots = new ArrayList<>();
        for (FishingSpotInfo spotInfo : availableSpots) {
            if (spotInfo.fishingSpot == targetSpot) {
                targetSpots.add(spotInfo);
            }
        }
        
        if (targetSpots.isEmpty()) {
            return null; // No spots of target type available
        }
        
        // Return closest spot of target type
        return getClosestSpot(player, targetSpots);
    }
    
    /**
     * Get closest fishing spot from a list
     */
    private FishingSpotInfo getClosestSpot(Player player, List<FishingSpotInfo> spots) {
        FishingSpotInfo closest = null;
        int shortestDistance = Integer.MAX_VALUE;
        
        for (FishingSpotInfo spot : spots) {
            int distance = getDistance(player, spot.npc);
            if (distance < shortestDistance) {
                closest = spot;
                shortestDistance = distance;
            }
        }
        
        return closest;
    }
    
    /**
     * Check for fishing spot upgrades in AUTO mode
     */
    private void checkForSpotUpgrade(Player player) {
        FishingSpots currentSpot = player.getAutoFishingSpot();
        if (currentSpot == null) {
            FishingSpots bestAvailable = getBestAvailableSpot(player);
            if (bestAvailable != null) {
                player.setAutoFishingSpot(bestAvailable);
                player.sendMessage("Auto-fishing set to " + bestAvailable.name() + " spots!");
            }
            return;
        }
        
        FishingSpots bestAvailable = getBestAvailableSpot(player);
        if (bestAvailable != null && bestAvailable != currentSpot) {
            
            // Get level requirements
            int currentLevel = getCurrentSpotLevel(currentSpot);
            int newLevel = getCurrentSpotLevel(bestAvailable);
            
            if (newLevel > currentLevel) {
                // Check if new spot type is actually available
                if (hasAvailableSpotsOfType(bestAvailable)) {
                    player.setAutoFishingSpot(bestAvailable);
                    player.sendMessage("Level up! Upgraded to " + bestAvailable.name() + " spots!");
                    
                    // Stop current action to force spot switch
                    if (player.getActionManager().getAction() instanceof Fishing) {
                        player.getActionManager().forceStop();
                    }
                }
            }
        }
    }
    
    /**
     * Start fishing at the best available spot
     */
    private void startFishingAtSpot(Player player) {
        FishingSpotInfo targetSpot = findBestFishingSpot(player);
        
        if (targetSpot == null) {
            handleNoAvailableSpots(player);
            return;
        }
        
        // Move to spot if not in position
        if (!isPlayerInFishingPosition(player, targetSpot.npc)) {
            moveToSpot(player, targetSpot.npc);
            return;
        }
        
        // Don't start if still walking
        if (player.hasWalkSteps()) {
            return;
        }
        
        // Start fishing with delay
        final NPC finalSpot = targetSpot.npc;
        final FishingSpots fishingSpotType = targetSpot.fishingSpot;
        
        com.rs.game.tasks.WorldTasksManager.schedule(new com.rs.game.tasks.WorldTask() {
            @Override
            public void run() {
                if (player.hasWalkSteps()) return;
                
                // Verify spot still exists
                if (finalSpot.hasFinished()) {
                    return;
                }
                
                // Verify position
                if (!isPlayerInFishingPosition(player, finalSpot)) {
                    return;
                }
                
                // Start fishing
                player.faceEntity(finalSpot);
                Fishing fishingAction = new Fishing(fishingSpotType, finalSpot);
                player.getActionManager().setAction(fishingAction);
            }
        }, 1);
    }
    
    /**
     * Handle case when no fishing spots are available
     */
    private void handleNoAvailableSpots(Player player) {
        FishingSpots targetSpot = player.getAutoFishingSpot();
        String mode = player.getAutoFishingMode();
        
        if (targetSpot == null) {
            sendThrottledMessage(player, "No suitable fishing spots found!");
            return;
        }
        
        if ("AUTO".equals(mode)) {
            // Try to find alternative spots
            FishingSpots alternative = findAlternativeSpot(player, targetSpot);
            if (alternative != null) {
                player.setAutoFishingSpot(alternative);
                sendThrottledMessage(player, "Switching to " + alternative.name() + " spots...");
                return;
            }
        }
        
        // No alternatives or SPECIFIC mode
        sendThrottledMessage(player, "Waiting for " + targetSpot.name() + " fishing spots...");
    }
    
    /**
     * Find alternative fishing spot when current type unavailable
     */
    private FishingSpots findAlternativeSpot(Player player, FishingSpots currentSpot) {
        int playerLevel = player.getSkills().getLevel(Skills.FISHING);
        
        // Find best alternative spot player can fish
        for (int i = FISHING_SPOT_MAPPINGS.length - 1; i >= 0; i--) {
            FishingSpotMapping mapping = FISHING_SPOT_MAPPINGS[i];
            
            if (mapping.fishingSpot != currentSpot && 
                playerLevel >= mapping.levelRequired &&
                hasAvailableSpotsOfType(mapping.fishingSpot)) {
                return mapping.fishingSpot;
            }
        }
        
        return null;
    }
    
    /**
     * Check if fishing spot type has available spots
     */
    private boolean hasAvailableSpotsOfType(FishingSpots spotType) {
        List<FishingSpotInfo> spots = findFishingSpotsInArea();
        for (FishingSpotInfo spotInfo : spots) {
            if (spotInfo.fishingSpot == spotType) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get best available fishing spot for player's level
     */
    private FishingSpots getBestAvailableSpot(Player player) {
        int level = player.getSkills().getLevel(Skills.FISHING);
        
        // Check from highest to lowest level
        for (int i = FISHING_SPOT_MAPPINGS.length - 1; i >= 0; i--) {
            FishingSpotMapping mapping = FISHING_SPOT_MAPPINGS[i];
            
            if (level >= mapping.levelRequired && 
                hasAvailableSpotsOfType(mapping.fishingSpot)) {
                return mapping.fishingSpot;
            }
        }
        
        return null;
    }
    
    /**
     * Get level requirement for fishing spot
     */
    private int getCurrentSpotLevel(FishingSpots spot) {
        for (FishingSpotMapping mapping : FISHING_SPOT_MAPPINGS) {
            if (mapping.fishingSpot == spot) {
                return mapping.levelRequired;
            }
        }
        return 1;
    }
    
    /**
     * Check if player is in fishing position
     */
    private boolean isPlayerInFishingPosition(Player player, NPC spot) {
        int deltaX = Math.abs(player.getX() - spot.getX());
        int deltaY = Math.abs(player.getY() - spot.getY());
        
        return (deltaX <= 1 && deltaY <= 1);
    }
    
    /**
     * Move player to fishing spot
     */
    private void moveToSpot(Player player, NPC spot) {
        WorldTile targetPosition = findSafeFishingPosition(spot);
        if (targetPosition != null && isInSkillingHub(targetPosition)) {
            player.addWalkSteps(targetPosition.getX(), targetPosition.getY(), -1, false);
        }
    }
    
    /**
     * Check if tile is in skilling hub
     */
    private boolean isInSkillingHub(WorldTile tile) {
        final int SKILLING_HUB_CENTER_X = 1375;
        final int SKILLING_HUB_CENTER_Y = 5669;
        final int SKILLING_HUB_SIZE = 90;
        int radius = SKILLING_HUB_SIZE / 2;
        
        return tile.getX() >= (SKILLING_HUB_CENTER_X - radius) && 
               tile.getX() <= (SKILLING_HUB_CENTER_X + radius) &&
               tile.getY() >= (SKILLING_HUB_CENTER_Y - radius) && 
               tile.getY() <= (SKILLING_HUB_CENTER_Y + radius);
    }
    
    /**
     * Find safe fishing position adjacent to spot
     */
    private WorldTile findSafeFishingPosition(NPC spot) {
        int[][] offsets = {
            {-1, 0}, {1, 0}, {0, -1}, {0, 1},  // Cardinals
            {-1, -1}, {1, -1}, {-1, 1}, {1, 1}  // Diagonals
        };
        
        for (int[] offset : offsets) {
            int x = spot.getX() + offset[0];
            int y = spot.getY() + offset[1];
            WorldTile tile = new WorldTile(x, y, spot.getPlane());
            
            // Must be within hub and safe
            if (isInSkillingHub(tile)) {
                return tile;
            }
        }
        
        return new WorldTile(spot.getX() - 1, spot.getY(), spot.getPlane());
    }
    
    /**
     * Calculate distance between player and spot
     */
    private int getDistance(Player player, NPC spot) {
        return Math.abs(player.getX() - spot.getX()) + Math.abs(player.getY() - spot.getY());
    }
    
    /**
     * Check if player has required fishing tool
     */
    private boolean hasFishingTool(Player player, FishingSpots spot) {
        // Check inventory for required tool
        return player.getInventory().containsOneItem(spot.getTool());
    }
    
    @Override
    public String getSkillName() {
        return "Fishing";
    }
    
    @Override
    public boolean canStart(Player player) {
        // Must be in skilling hub
        if (!isInSkillingHub(player)) {
            player.sendMessage("You must be in the skilling hub to start auto-fishing!");
            return false;
        }
        
        if (player.getInventory().getFreeSlots() <= 1) {
            player.sendMessage("You need at least 2 free inventory slots!");
            return false;
        }
        
        // Check if there are any fishing spots in the area
        List<FishingSpotInfo> spots = findFishingSpotsInArea();
        if (spots.isEmpty()) {
            player.sendMessage("No fishing spots found in this area!");
            return false;
        }
        
        // Mode-specific validation
        String mode = player.getAutoFishingMode();
        FishingSpots targetSpot = player.getAutoFishingSpot();
        
        if ("AUTO".equals(mode)) {
            if (targetSpot == null) {
                FishingSpots bestSpot = getBestAvailableSpot(player);
                if (bestSpot == null) {
                    player.sendMessage("No suitable fishing spots found for your level!");
                    return false;
                }
                player.setAutoFishingSpot(bestSpot);
                player.sendMessage("Auto-fishing will target " + bestSpot.name() + " spots (auto mode).");
            } else {
                player.sendMessage("Auto-fishing will target " + targetSpot.name() + " spots (auto mode).");
            }
        } else if ("SPECIFIC".equals(mode)) {
            if (targetSpot == null) {
                player.sendMessage("No specific fishing spot selected!");
                return false;
            }
            
            int playerLevel = player.getSkills().getLevel(Skills.FISHING);
            int requiredLevel = getCurrentSpotLevel(targetSpot);
            if (playerLevel < requiredLevel) {
                player.sendMessage("You need level " + requiredLevel + " Fishing!");
                return false;
            }
            
            if (!hasAvailableSpotsOfType(targetSpot)) {
                player.sendMessage("No " + targetSpot.name() + " fishing spots found in this area!");
                return false;
            }
            
            player.sendMessage("Auto-fishing will target " + targetSpot.name() + " spots only (specific mode).");
        } else {
            player.sendMessage("Fishing mode not set! Please restart from dialogue.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Fishing spot mapping helper class
     */
    private static class FishingSpotMapping {
        final int npcId;
        final WorldTile location;
        final FishingSpots fishingSpot;
        final int levelRequired;
        
        FishingSpotMapping(int npcId, WorldTile location, FishingSpots fishingSpot, int levelRequired) {
            this.npcId = npcId;
            this.location = location;
            this.fishingSpot = fishingSpot;
            this.levelRequired = levelRequired;
        }
    }
    
    /**
     * Fishing spot info helper class
     */
    private static class FishingSpotInfo {
        final NPC npc;
        final FishingSpots fishingSpot;
        final int levelRequired;
        
        FishingSpotInfo(NPC npc, FishingSpots fishingSpot, int levelRequired) {
            this.npc = npc;
            this.fishingSpot = fishingSpot;
            this.levelRequired = levelRequired;
        }
    }
}