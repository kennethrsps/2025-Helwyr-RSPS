package com.rs.game.player.actions.automation.handlers;

import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.World;
import com.rs.game.Region;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.Animation;
import com.rs.game.item.Item;
import com.rs.utils.Utils;
import com.rs.utils.Colors;

import com.rs.game.route.RouteFinder;
import com.rs.game.route.strategy.FixedTileStrategy;
import com.rs.game.route.strategy.ObjectStrategy;

/**
 * UPDATED Auto-Thieving Handler with Bank Waypoint Navigation
 *
 * Supports two modes:
 * - AUTO: Automatically upgrades to best available stall when leveling up
 * - SPECIFIC: Only thieves from the manually selected stall type (no auto-upgrade)
 *
 * Stall Information:
 * - Crafting stall (4874): Level 1, 25 XP
 * - Food stall (4875): Level 30, 50 XP
 * - General stall (4876): Level 65, 75 XP
 * - Magic stall (4877): Level 85, 100 XP
 * - Scimitar stall (4878): Level 95, 125 XP
 *
 * NEW: Bank Waypoint Navigation for Obstacle Avoidance
 * - Uses bank as reliable waypoint when player can't reach stalls directly
 * - Two-phase movement: Player → Bank → Stalls
 * - Same proven logic as AutoWoodcuttingHandler
 * - FIXED: Now consistently uses player.calcFollow() for intelligent pathfinding
 */
public class AutoThievingHandler implements SkillHandler {

    private static AutoThievingHandler instance;

    // Stall mappings - All stalls available in the skilling area
    private static final StallMapping[] STALL_MAPPINGS = {
            new StallMapping(4874, 1, 25, "Crafting"),     // Crafting stall (level 1)
            new StallMapping(4875, 30, 50, "Food"),        // Food stall (level 30)
            new StallMapping(4876, 65, 75, "General"),     // General stall (level 65)
            new StallMapping(4877, 85, 100, "Magic"),      // Magic stall (level 85)
            new StallMapping(4878, 95, 125, "Scimitar"),   // Scimitar stall (level 95)
    };

    // Stall locations - RELOCATED to skilling hub area (same 90x90 area as other skills)
    // Player positions (where player stands to thieve - NORTH of actual stalls)
    private static final WorldTile[] STALL_PLAYER_LOCATIONS = {
            new WorldTile(1362, 5675, 0), // Player pos for Crafting stall
            new WorldTile(1362, 5674, 0), // Player pos for Food stall
            new WorldTile(1362, 5673, 0), // Player pos for General stall
            new WorldTile(1362, 5672, 0), // Player pos for Magic stall
            new WorldTile(1362, 5671, 0)  // Player pos for Scimitar stall
    };

    // Actual stall coordinates (SOUTH of player positions)
    private static final WorldTile[] ACTUAL_STALL_LOCATIONS = {
            new WorldTile(1361, 5675, 0), // Crafting stall (4874)
            new WorldTile(1361, 5674, 0), // Food stall (4875)
            new WorldTile(1361, 5673, 0), // General stall (4876)
            new WorldTile(1361, 5672, 0), // Magic stall (4877)
            new WorldTile(1361, 5671, 0)  // Scimitar stall (4878)
    };

    // Bank waypoint navigation (consistent with AutoWoodcuttingHandler)
    private static final int[] BANK_OBJECT_IDS = { 89398, 2213, 11758, 27663, 28589, 29085 };
    private static final long MESSAGE_THROTTLE_TIME = 5000; // 5 seconds between messages
    private static long lastWalkMessage = 0;

    public static AutoThievingHandler getInstance() {
        if (instance == null) {
            instance = new AutoThievingHandler();
        }
        return instance;
    }

    /**
     * UPDATED PROCESS - Now supports both AUTO and SPECIFIC modes with bank waypoint navigation
     */
    @Override
    public void process(Player player) {
        // Quick inventory check
        if (player.getInventory().getFreeSlots() <= 0) {
            player.sendMessage("Your inventory is full! Auto-thieving stopped.");
            player.getActionManager().forceStop();
            return;
        }

        // Check if player left the entire skilling hub - if so, stop auto-thieving
        if (!isInSkillingHub(player)) {
            player.sendMessage("You left the skilling hub. Auto-thieving stopped.");
            player.getActionManager().forceStop();
            return;
        }

        // Check for stall upgrades (only in AUTO mode)
        String mode = player.getAutoThievingMode();
        if ("AUTO".equals(mode)) {
            checkForStallUpgrade(player);
        }

        // If player is already busy (doing other actions or actively walking)
        if (player.getActionManager().getAction() != null || player.hasWalkSteps()) {
            return; // Player is doing something else or currently pathfinding
        }

        // If player is in hub but not near stalls, use bank waypoint navigation
        if (!isNearStallArea(player)) {
            walkToStallAreaViaBank(player);
            return;
        }

        // Find and start thieving the appropriate stall
        startThievingStall(player);
    }

    /**
     * Check if player can upgrade to a better stall type (AUTO mode only)
     */
    private void checkForStallUpgrade(Player player) {
        int currentStallIndex = player.getAutoThievingStall();
        if (currentStallIndex == -1) {
            // In AUTO mode, set to best available stall if none set
            int bestAvailable = getBestAvailableStall(player);
            if (bestAvailable != -1) {
                player.setAutoThievingStall(bestAvailable);
                player.sendMessage("Auto-thieving set to " + STALL_MAPPINGS[bestAvailable].name + " stall!");
            }
            return;
        }

        int bestAvailable = getBestAvailableStall(player);
        if (bestAvailable == -1)
            return;

        // If a better stall is available, upgrade automatically (AUTO mode only)
        if (bestAvailable != currentStallIndex && bestAvailable > currentStallIndex) {
            // Check if the new stall type actually has available objects
            int newStallId = STALL_MAPPINGS[bestAvailable].stallId;
            if (hasAvailableStallsOfType(newStallId)) {
                player.setAutoThievingStall(bestAvailable);
                player.sendMessage("Level up detected! Auto-thieving upgraded to " + 
                                 STALL_MAPPINGS[bestAvailable].name + " stall!");

                // Stop current action to force stall switch
                player.getActionManager().forceStop();
            }
        }
    }

    /**
     * Find appropriate stall and start thieving based on mode
     */
    private void startThievingStall(Player player) {
        // Get the stall index we should target
        int targetStallIndex = player.getAutoThievingStall();
        if (targetStallIndex == -1) {
            // Fallback: find best available stall (should not happen after canStart checks)
            targetStallIndex = getBestAvailableStall(player);
            if (targetStallIndex == -1) {
                player.sendMessage("No suitable stalls found! Stopping auto-thieving.");
                player.getActionManager().forceStop();
                return;
            }
            player.setAutoThievingStall(targetStallIndex);
        }

        // In SPECIFIC mode, validate that player can still thieve the selected stall
        String mode = player.getAutoThievingMode();
        if ("SPECIFIC".equals(mode)) {
            int playerLevel = player.getSkills().getLevel(Skills.THIEVING);
            StallMapping targetStall = STALL_MAPPINGS[targetStallIndex];
            if (playerLevel < targetStall.levelRequired) {
                player.sendMessage("You can no longer thieve from " + targetStall.name + " stall! (Level requirement: "
                        + targetStall.levelRequired + ")");
                player.getActionManager().forceStop();
                return;
            }
        }

        // Find the best available stall of that type
        WorldObject stallObject = findBestStall(player, targetStallIndex);
        if (stallObject == null) {
            // Handle case where stall is missing
            handleNoAvailableStalls(player, targetStallIndex, mode);
            return;
        }

        // Position player if needed - now uses intelligent pathfinding
        if (!isPlayerInPosition(player, targetStallIndex)) {
            moveToStall(player, targetStallIndex);
            return;
        }

        // Don't start thieving if player is still walking from a previous pathfinding operation
        if (player.hasWalkSteps()) {
            return; // Wait until player stops walking
        }

        // Face the stall first before starting (realistic behavior)
        player.faceObject(stallObject);

        // Add a realistic delay before starting thieving (like manual gameplay)
        final WorldObject finalStallObject = stallObject;
        final int finalTargetStallIndex = targetStallIndex;

        com.rs.game.tasks.WorldTasksManager.schedule(new com.rs.game.tasks.WorldTask() {
            @Override
            public void run() {
                // Make sure player stopped walking and is ready
                if (player.hasWalkSteps()) {
                    return; // Still walking, try again next cycle
                }

                // Double-check stall is still available after facing delay
                if (!World.containsObjectWithId(finalStallObject, finalStallObject.getId())) {
                    // Stall disappeared while we were facing it
                    sendThrottledMessage(player, "Stall disappeared. Searching for new stall.");
                    player.getActionManager().forceStop();
                    return;
                }

                // Double-check player is still in position
                if (!isPlayerInPosition(player, finalTargetStallIndex)) {
                    // Player moved, try again next cycle
                    sendThrottledMessage(player, "Moved away from stall. Re-positioning.");
                    player.getActionManager().forceStop();
                    return;
                }

                // Face the stall one more time to ensure proper direction
                player.faceObject(finalStallObject);

                // Start thieving action
                performStallThieving(player, finalStallObject.getId());
            }
        }, 1); // 1 tick delay for consistency
    }

    /**
     * Find the best available stall of the target type
     */
    private WorldObject findBestStall(Player player, int stallIndex) {
        StallMapping targetStall = STALL_MAPPINGS[stallIndex];
        WorldTile stallLocation = ACTUAL_STALL_LOCATIONS[stallIndex];

        // Check for the actual stall object
        WorldObject stall = World.getObjectWithId(stallLocation, targetStall.stallId);

        if (stall != null && canReachTarget(player, stall)) {
            return stall;
        }

        // Fallback: search region for the stall
        try {
            for (WorldObject obj : World.getRegion(player.getRegionId()).getAllObjects()) {
                if (obj.getId() == targetStall.stallId && 
                    obj.getX() == stallLocation.getX() && 
                    obj.getY() == stallLocation.getY()) {
                    if (canReachTarget(player, obj)) {
                        return obj;
                    }
                }
            }
        } catch (Exception e) {
            // Ignore region search errors
        }

        return null;
    }

    /**
     * Check if player is in thieving position (exact position for stall)
     */
    private boolean isPlayerInPosition(Player player, int stallIndex) {
        WorldTile playerPosition = STALL_PLAYER_LOCATIONS[stallIndex];
        // Player must be in exact position for thieving
        return player.getX() == playerPosition.getX() && 
               player.getY() == playerPosition.getY() &&
               player.getPlane() == playerPosition.getPlane();
    }

    /**
     * Move player to thieving position using intelligent pathfinding
     */
    private void moveToStall(Player player, int stallIndex) {
        WorldTile playerPosition = STALL_PLAYER_LOCATIONS[stallIndex];
        
        if (isInSkillingHub(playerPosition)) { // Ensure target is in hub
            // Use calcFollow for intelligent pathfinding (exact position required)
            if (!player.calcFollow(playerPosition, true)) {
                sendThrottledMessage(player, "No path found to stall. Re-evaluating.");
                player.getActionManager().forceStop();
            } else {
                sendThrottledMessage(player, "Moving to " + STALL_MAPPINGS[stallIndex].name + " stall...");
            }
        } else {
            sendThrottledMessage(player, "Could not determine a safe position for thieving. Re-evaluating.");
            player.getActionManager().forceStop();
        }
    }

    /**
     * Walk to stall area using bank as waypoint for obstacle avoidance
     */
    private void walkToStallAreaViaBank(Player player) {
        // Check if we already have a target bank stored
        WorldObject targetBank = (WorldObject) player.getTemporaryAttributtes().get("waypoint_bank_thieving");

        // If no target bank is stored or it's no longer valid in the world
        if (targetBank == null || (targetBank.getId() != 0 && !World.containsObjectWithId(targetBank, targetBank.getId()))) {
            targetBank = findClosestBankObject(player);
            if (targetBank == null) {
                sendThrottledMessage(player, "No accessible bank found for waypoint navigation. Seeking stall directly.");
                walkToStallArea(player); // Fallback to direct walk if no bank found
                return;
            }
            player.getTemporaryAttributtes().put("waypoint_bank_thieving", targetBank);
        }

        // Check if we reached the bank waypoint
        if (isAtBankObject(player, targetBank)) {
            // We're at the bank, now continue to stalls
            player.getTemporaryAttributtes().remove("waypoint_bank_thieving");
            sendThrottledMessage(player, "Reached waypoint, continuing to stalls...");
            walkToStallArea(player);
            return;
        }

        // Not at bank yet, keep walking there if not already moving
        if (!player.hasWalkSteps()) {
            // Use calcFollow for intelligent pathfinding to the bank object
            if (!player.calcFollow(targetBank, true)) {
                sendThrottledMessage(player, "Path to bank blocked. Trying direct path to stalls.");
                walkToStallArea(player); // Fallback if path to bank is blocked
            } else {
                sendThrottledMessage(player, "Using bank as waypoint to reach stalls...");
            }
        }
    }

    /**
     * Walk player back to stall area (when they're in hub but away from stalls)
     */
    private void walkToStallArea(Player player) {
        // Find the center of the stall area for walking back
        WorldTile stallCenterTile = new WorldTile(1386, 5675, 0); // Center of stall area

        // Only try to walk if not already walking
        if (!player.hasWalkSteps()) {
            if (!player.calcFollow(stallCenterTile, true)) {
                sendThrottledMessage(player, "No path found to stall area. Waiting.");
                player.getActionManager().forceStop();
            } else {
                sendThrottledMessage(player, "Walking to general stall area...");
            }
        }
    }

    /**
     * Find the closest bank WorldObject (consistent with AutoWoodcuttingHandler logic)
     */
    private WorldObject findClosestBankObject(Player player) {
        WorldObject closestBank = null;
        int shortestDistance = Integer.MAX_VALUE;

        // Calculate skilling hub boundaries
        final int SKILLING_HUB_CENTER_X = 1375;
        final int SKILLING_HUB_CENTER_Y = 5669;
        final int SKILLING_HUB_SIZE = 90;
        int radius = SKILLING_HUB_SIZE / 2;
        int minX = SKILLING_HUB_CENTER_X - radius;
        int maxX = SKILLING_HUB_CENTER_X + radius;
        int minY = SKILLING_HUB_CENTER_Y - radius;
        int maxY = SKILLING_HUB_CENTER_Y + radius;

        // Iterate through regions within the hub bounds
        for (int x = minX / 8; x <= maxX / 8; x++) {
            for (int y = minY / 8; y <= maxY / 8; y++) {
                int regionId = (x << 8) + y;
                Region region = World.getRegion(regionId);

                if (region != null) {
                    for (int bankId : BANK_OBJECT_IDS) {
                        for (WorldObject obj : region.getAllObjects()) {
                            if (obj.getId() == bankId &&
                                obj.getX() >= minX && obj.getX() <= maxX &&
                                obj.getY() >= minY && obj.getY() <= maxY) {
                                int distance = getDistanceToWorldObject(player, obj);
                                if (distance < shortestDistance) {
                                    closestBank = obj;
                                    shortestDistance = distance;
                                }
                            }
                        }
                    }
                }
            }
        }
        return closestBank;
    }

    /**
     * Calculates distance to a WorldObject
     */
    private int getDistanceToWorldObject(Player player, WorldObject obj) {
        return Math.abs(player.getX() - obj.getX()) + Math.abs(player.getY() - obj.getY());
    }

    /**
     * Check if player is at bank WorldObject
     */
    private boolean isAtBankObject(Player player, WorldObject bank) {
        if (bank == null) {
            return false;
        }
        return player.withinDistance(new WorldTile(bank.getX(), bank.getY(), bank.getPlane()), 3);
    }

    /**
     * Send throttled message to prevent spam
     */
    private void sendThrottledMessage(Player player, String message) {
        long currentTime = Utils.currentTimeMillis();
        if (currentTime - lastWalkMessage > MESSAGE_THROTTLE_TIME) {
            player.sendMessage(message);
            lastWalkMessage = currentTime;
        }
    }

    /**
     * Check if player is in the skilling hub
     */
    private boolean isInSkillingHub(Player player) {
        final int SKILLING_HUB_CENTER_X = 1375;
        final int SKILLING_HUB_CENTER_Y = 5669;
        final int SKILLING_HUB_SIZE = 90;

        int playerX = player.getX();
        int playerY = player.getY();

        int radius = SKILLING_HUB_SIZE / 2;
        return playerX >= (SKILLING_HUB_CENTER_X - radius) && playerX <= (SKILLING_HUB_CENTER_X + radius)
                && playerY >= (SKILLING_HUB_CENTER_Y - radius) && playerY <= (SKILLING_HUB_CENTER_Y + radius);
    }

    /**
     * Helper to check if a WorldTile is within the skilling hub boundaries
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
     * Check if player is near the stall area (more lenient range)
     */
    private boolean isNearStallArea(Player player) {
        for (WorldTile location : STALL_PLAYER_LOCATIONS) {
            if (player.withinDistance(location, 15)) { // 15 tiles range
                return true;
            }
        }
        return false;
    }

    /**
     * Handle case where no stalls of target type are available
     */
    private void handleNoAvailableStalls(Player player, int targetStallIndex, String mode) {
        StallMapping targetStall = STALL_MAPPINGS[targetStallIndex];

        if ("AUTO".equals(mode)) {
            // In AUTO mode, try to find alternative available stalls
            int alternativeStall = findAlternativeStall(player, targetStallIndex);
            if (alternativeStall != -1) {
                player.setAutoThievingStall(alternativeStall);
                sendThrottledMessage(player, "All " + targetStall.name + 
                                  " stalls are unavailable. Switching to " + 
                                  STALL_MAPPINGS[alternativeStall].name + " stall...");
                return;
            }
            sendThrottledMessage(player, "All stalls are currently unavailable. Waiting...");
        } else if ("SPECIFIC".equals(mode)) {
            sendThrottledMessage(player, "The " + targetStall.name + " stall is unavailable. Waiting...");
        }
    }

    /**
     * Find an alternative stall type that's available (AUTO mode only)
     */
    private int findAlternativeStall(Player player, int currentStallIndex) {
        int playerLevel = player.getSkills().getLevel(Skills.THIEVING);

        // Check other stall types player can thieve, starting from best available
        for (int i = STALL_MAPPINGS.length - 1; i >= 0; i--) {
            StallMapping mapping = STALL_MAPPINGS[i];

            // Skip the current stall type and stalls above player level
            if (i == currentStallIndex || playerLevel < mapping.levelRequired) {
                continue;
            }

            // Check if this stall type has available objects
            if (hasAvailableStallsOfType(mapping.stallId)) {
                // Ensure the player can actually reach this alternative stall
                if (findBestStall(player, i) != null) {
                    return i;
                }
            }
        }

        return -1; // No alternatives found
    }

    /**
     * Check if there are any available stalls of the specified type
     */
    private boolean hasAvailableStallsOfType(int stallId) {
        for (int i = 0; i < STALL_MAPPINGS.length; i++) {
            if (STALL_MAPPINGS[i].stallId == stallId) {
                WorldTile stallLocation = ACTUAL_STALL_LOCATIONS[i];
                WorldObject stall = World.getObjectWithId(stallLocation, stallId);
                if (stall != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Find best stall type player can thieve
     */
    private int getBestAvailableStall(Player player) {
        int level = player.getSkills().getLevel(Skills.THIEVING);

        // Check from highest to lowest level
        for (int i = STALL_MAPPINGS.length - 1; i >= 0; i--) {
            StallMapping mapping = STALL_MAPPINGS[i];
            if (level >= mapping.levelRequired) {
                // Check if this stall type exists AND has a reachable object in the world
                if (hasAvailableStallsOfType(mapping.stallId)) {
                    if (findBestStall(player, i) != null) {
                        return i;
                    }
                }
            }
        }

        return -1; // No suitable stalls found
    }

    @Override
    public String getSkillName() {
        return "Thieving";
    }

    @Override
    public boolean canStart(Player player) {
        // FIRST CHECK: Must be in skilling hub
        if (!isInSkillingHub(player)) {
            player.sendMessage("Auto-thieving can only be used in the skilling hub!");
            return false;
        }

        if (player.getInventory().getFreeSlots() <= 1) {
            player.sendMessage("You need at least 2 free inventory slots!");
            return false;
        }

        // Mode-specific validation
        String mode = player.getAutoThievingMode();
        int targetStallIndex = player.getAutoThievingStall();

        if ("AUTO".equals(mode)) {
            // For AUTO mode, find the best stall if none set
            if (targetStallIndex == -1) {
                int bestStall = getBestAvailableStall(player);
                if (bestStall == -1) {
                    player.sendMessage("No suitable stalls found for your level!");
                    return false;
                }
                player.setAutoThievingStall(bestStall);
                player.sendMessage("Auto-thieving will target " + STALL_MAPPINGS[bestStall].name + 
                                 " stalls (automatic mode).");
            } else {
                player.sendMessage("Auto-thieving will target " + STALL_MAPPINGS[targetStallIndex].name + 
                                 " stalls (automatic mode).");
            }
        } else if ("SPECIFIC".equals(mode)) {
            // For SPECIFIC mode, validate the selected stall
            if (targetStallIndex == -1 || targetStallIndex >= STALL_MAPPINGS.length) {
                player.sendMessage("No specific stall selected! This should not happen.");
                return false;
            }

            StallMapping targetStall = STALL_MAPPINGS[targetStallIndex];
            int playerLevel = player.getSkills().getLevel(Skills.THIEVING);
            if (playerLevel < targetStall.levelRequired) {
                player.sendMessage("You need level " + targetStall.levelRequired + " Thieving to use "
                        + targetStall.name + " stalls!");
                return false;
            }

            // Check if the specific stall type is available AND reachable
            WorldObject actualStall = findBestStall(player, targetStallIndex);
            if (actualStall == null) {
                player.sendMessage("No " + targetStall.name + " stalls found in the skilling area or reachable!");
                return false;
            }

            player.sendMessage("Auto-thieving will target " + targetStall.name + " stalls only (specific mode).");
        } else {
            // Fallback for missing mode
            player.sendMessage("Thieving mode not set! Please restart from the dialogue.");
            return false;
        }

        // Final check that a reachable stall exists for the chosen type/mode
        if (findBestStall(player, player.getAutoThievingStall()) == null) {
            player.sendMessage("No reachable stalls of the selected type/mode found.");
            return false;
        }

        return true;
    }

    /**
     * Helper method to check if a player can reach a target
     */
    private boolean canReachTarget(Player player, WorldTile target) {
        int result = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER,
                                            player.getX(), player.getY(), player.getPlane(),
                                            player.getSize(),
                                            target instanceof WorldObject ? new ObjectStrategy((WorldObject) target) : new FixedTileStrategy(target.getX(), target.getY()),
                                            true);
        return result >= 0;
    }

    /**
     * Stall mapping helper class
     */
    private static class StallMapping {
        final int stallId;
        final int levelRequired;
        final int xpReward;
        final String name;

        StallMapping(int stallId, int levelRequired, int xpReward, String name) {
            this.stallId = stallId;
            this.levelRequired = levelRequired;
            this.xpReward = xpReward;
            this.name = name;
        }
    }

    /**
     * Perform the actual thieving action (adapted from original code)
     */
    private void performStallThieving(Player player, int stallId) {
        // Check thieving delay
        if (player.getThievingDelay() > Utils.currentTimeMillis() ||
            player.getActionManager().getActionDelay() != 0) {
            return;
        }

        if (stallId == 4874) { // Crafting stall
            performCraftingStallThieving(player);
        } else if (stallId == 4875) { // Food stall
            performFoodStallThieving(player);
        } else if (stallId == 4876) { // General stall
            performGeneralStallThieving(player);
        } else if (stallId == 4877) { // Magic stall
            performMagicStallThieving(player);
        } else if (stallId == 4878) { // Scimitar stall
            performScimitarStallThieving(player);
        }
    }

    private void performCraftingStallThieving(Player player) {
        Item[] items = { new Item(1739), new Item(1737), new Item(1779), new Item(6287), new Item(1635),
                new Item(1734), new Item(1623), new Item(1625), new Item(1627), new Item(1629),
                new Item(1631), new Item(1621), new Item(1619), new Item(1617) };
        
        Item item = items[Utils.random(items.length)];
        if (item.getDefinitions().isStackable())
            item.setAmount(Utils.random(1, 5));
            
        if (player.getInventory().addItem(item)) {
            if (!player.getPerkManager().sleightOfHand)
                player.applyHit(new Hit(player, Utils.random(1, 3), HitLook.REGULAR_DAMAGE, 1));
                
            int amount = player.getSkills().getLevelForXp(Skills.THIEVING) * 52;
            player.addMoney(Utils.random(1, amount));
            player.setNextAnimation(new Animation(881));
            player.setThievingDelay(Utils.currentTimeMillis() + 1900);
            player.getSkills().addXp(Skills.THIEVING, 25);
            player.getResourceGather().submitResourceGatherForStats(item, 25, Skills.THIEVING, false);
            player.addTimesStolen();
            player.getAchManager().addKeyAmount("thieve", 1);
            player.sendMessage("You've successfully stolen from this stall; times thieved: " +
                    Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);
                    
            if (Utils.random(75) == 0) {
                handleRogueEncounter(player);
            }
        } else {
            player.sendMessage("You do not have enough inventory space to do this.", true);
        }
    }

    private void performFoodStallThieving(Player player) {
        if (player.getSkills().getLevel(Skills.THIEVING) < 30) {
            player.sendMessage("You need a Thieving level of 30 to thieve from this stall.");
            return;
        }
        
        Item[] items = { new Item(379), new Item(385), new Item(373), new Item(7946), new Item(391),
                new Item(1963), new Item(315), new Item(319), new Item(347), new Item(325), new Item(361),
                new Item(365), new Item(333), new Item(329), new Item(351), new Item(339) };
        
        Item item = items[Utils.random(items.length)];
        if (item.getDefinitions().isStackable())
            item.setAmount(Utils.random(1, 5));
            
        if (player.getInventory().addItem(item)) {
            if (!player.getPerkManager().sleightOfHand)
                player.applyHit(new Hit(player, Utils.random(1, 4), HitLook.REGULAR_DAMAGE, 1));
                
            int amount = player.getSkills().getLevelForXp(Skills.THIEVING) * 102;
            player.addMoney(Utils.random(1, amount));
            player.setNextAnimation(new Animation(881));
            player.setThievingDelay(Utils.currentTimeMillis() + 1900);
            player.getSkills().addXp(Skills.THIEVING, 50);
            player.getResourceGather().submitResourceGatherForStats(item, 50, Skills.THIEVING, false);
            player.addTimesStolen();
            player.getAchManager().addKeyAmount("thieve", 1);
            player.sendMessage("You've successfully stolen from this stall; times thieved: " +
                    Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);
                    
            if (Utils.random(75) == 0) {
                handleRogueEncounter(player);
            }
        } else {
            player.sendMessage("You do not have enough inventory space to do this.", true);
        }
    }

    private void performGeneralStallThieving(Player player) {
        if (player.getSkills().getLevel(Skills.THIEVING) < 65) {
            player.sendMessage("You need a Thieving level of 65 to thieve from this stall.");
            return;
        }
        
        Item[] items = { new Item(1391), new Item(2357), new Item(1776) };
        
        Item item = items[Utils.random(items.length)];
        if (item.getDefinitions().isStackable())
            item.setAmount(Utils.random(1, 5));
            
        if (player.getInventory().addItem(item)) {
            if (!player.getPerkManager().sleightOfHand)
                player.applyHit(new Hit(player, Utils.random(1, 5), HitLook.REGULAR_DAMAGE, 1));
                
            int amount = player.getSkills().getLevelForXp(Skills.THIEVING) * 152;
            player.addMoney(Utils.random(1, amount));
            player.setNextAnimation(new Animation(881));
            player.setThievingDelay(Utils.currentTimeMillis() + 1900);
            player.getSkills().addXp(Skills.THIEVING, 75);
            player.getResourceGather().submitResourceGatherForStats(item, 75, Skills.THIEVING, false);
            player.addTimesStolen();
            player.getAchManager().addKeyAmount("thieve", 1);
            player.sendMessage("You've successfully stolen from this stall; times thieved: " +
                    Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);
                    
            if (Utils.random(75) == 0) {
                handleRogueEncounter(player);
            }
        } else {
            player.sendMessage("You do not have enough inventory space to do this.", true);
        }
    }

    private void performMagicStallThieving(Player player) {
        if (player.getSkills().getLevel(Skills.THIEVING) < 85) {
            player.sendMessage("You need a Thieving level of 85 to thieve from this stall.");
            return;
        }
        
        Item[] items = { new Item(577), new Item(579), new Item(1011), new Item(1017), new Item(2579),
                new Item(1381), new Item(1383), new Item(1385), new Item(1387), new Item(7398),
                new Item(7399), new Item(7400), new Item(4089), new Item(4099), new Item(4109),
                new Item(4091), new Item(4093), new Item(4101), new Item(4103), new Item(4111),
                new Item(4113), new Item(4095), new Item(4105), new Item(4115), new Item(4097),
                new Item(4107), new Item(4117), new Item(1437), new Item(7937), new Item(554),
                new Item(555), new Item(556), new Item(557), new Item(558), new Item(559), new Item(560),
                new Item(561), new Item(562), new Item(563), new Item(564), new Item(565), new Item(566),
                new Item(9075) };
        
        Item item = items[Utils.random(items.length)];
        if (item.getDefinitions().isStackable())
            item.setAmount(Utils.random(1, 5));
            
        if (player.getInventory().addItem(item)) {
            if (!player.getPerkManager().sleightOfHand)
                player.applyHit(new Hit(player, Utils.random(1, 6), HitLook.REGULAR_DAMAGE, 1));
                
            int amount = player.getSkills().getLevelForXp(Skills.THIEVING) * 202;
            player.addMoney(Utils.random(1, amount));
            player.setNextAnimation(new Animation(881));
            player.setThievingDelay(Utils.currentTimeMillis() + 1900);
            player.getSkills().addXp(Skills.THIEVING, 100);
            player.getResourceGather().submitResourceGatherForStats(item, 100, Skills.THIEVING, false);
            player.addTimesStolen();
            player.getAchManager().addKeyAmount("thieve", 1);
            player.sendMessage("You've successfully stolen from this stall; times thieved: " +
                    Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);
                    
            if (Utils.random(75) == 0) {
                handleRogueEncounter(player);
            }
        } else {
            player.sendMessage("You do not have enough inventory space to do this.", true);
        }
    }

    private void performScimitarStallThieving(Player player) {
        if (player.getSkills().getLevel(Skills.THIEVING) < 95) {
            player.sendMessage("You need a Thieving level of 95 to thieve from this stall.");
            return;
        }
        
        Item[] items = { new Item(1323), new Item(1325), new Item(1327), new Item(1329), new Item(1331),
                new Item(1333), new Item(4587), new Item(6611) };
        
        Item item = items[Utils.random(items.length)];
        if (item.getDefinitions().isStackable())
            item.setAmount(Utils.random(1, 5));
            
        if (player.getInventory().addItem(item)) {
            if (!player.getPerkManager().sleightOfHand)
                player.applyHit(new Hit(player, Utils.random(1, 7), HitLook.REGULAR_DAMAGE, 1));
                
            int amount = player.getSkills().getLevelForXp(Skills.THIEVING) * 252;
            player.addMoney(Utils.random(1, amount));
            player.setNextAnimation(new Animation(881));
            player.setThievingDelay(Utils.currentTimeMillis() + 1900);
            player.getSkills().addXp(Skills.THIEVING, 125);
            player.getResourceGather().submitResourceGatherForStats(item, 125, Skills.THIEVING, false);
            player.addTimesStolen();
            player.getAchManager().addKeyAmount("thieve", 1);
            player.sendMessage("You've successfully stolen from this stall; times thieved: " +
                    Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);
                    
            if (Utils.random(75) == 0) {
                handleRogueEncounter(player);
            }
        } else {
            player.sendMessage("You do not have enough inventory space to do this.", true);
        }
    }

    private void handleRogueEncounter(Player player) {
        player.getActionManager().forceStop();
        player.sendMessage("<col=ff0000>A Rogue appears out of nowhere.");
        
        if (player.isStopOnRogue()) {
            player.getActionManager().forceStop();
            player.sendMessage("Auto-thieving stopped due to rogue encounter!");
        } else {
            player.sendMessage("Continuing auto-thieving after rogue encounter...");
        }
    }
}