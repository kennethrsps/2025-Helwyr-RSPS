package com.rs.game.player.actions.automation.handlers;

import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Woodcutting;
import com.rs.game.player.actions.Woodcutting.TreeDefinitions;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.World;
import com.rs.game.Region; // Import Region for findClosestBank

import com.rs.game.route.RouteFinder; // Import RouteFinder
import com.rs.game.route.strategy.EntityStrategy; // Import strategy for Entities
import com.rs.game.route.strategy.FixedTileStrategy; // Import strategy for WorldTiles
import com.rs.utils.Utils; // For Utils.currentTimeMillis

/**
 * UPDATED Auto-Woodcutting Handler with Bank Waypoint Navigation
 *
 * Supports two modes: - AUTO: Automatically upgrades to best available tree
 * when leveling up - SPECIFIC: Only cuts the manually selected tree type (no
 * auto-upgrade)
 *
 * Tree Respawn Behavior in Skilling Area: - Normal trees (1276): Don't respawn
 * when cut (disappear completely) - Oak trees (1281): Don't respawn when cut
 * (confirmed by user) - Maple trees (1307): Respawn normally (stump ID 31057,
 * 43s delay) - Yew trees (1309): Respawn normally (stump ID 1341, 56s delay)
 *
 * Stump ID Conflict Issue: - Normal, Oak, and Yew all use stump ID 1341 - Only
 * Maple uses different stump ID 31057 - This may cause detection issues for
 * some trees
 *
 * Priority System: - AUTO mode: Prioritizes Maple/Yew (respawning) over
 * Normal/Oak (non-respawning) - Fallback: Uses non-respawning trees only when
 * necessary - Smart messaging: Different alerts for respawning vs
 * non-respawning trees
 *
 * NEW: Bank Waypoint Navigation for Obstacle Avoidance - Uses bank as reliable
 * waypoint when player can't reach trees directly - Two-phase movement: Player
 * → Bank → Trees - Same proven logic as banking system
 * - FIXED: Now consistently uses player.calcFollow() for intelligent pathfinding.
 */
public class AutoWoodcuttingHandler implements SkillHandler {

	private static AutoWoodcuttingHandler instance;

	// Tree mappings - Only trees that exist in the skilling area
	private static final TreeMapping[] TREE_MAPPINGS = {
			new TreeMapping(1276, TreeDefinitions.NORMAL), // Normal tree (level 1)
			new TreeMapping(1281, TreeDefinitions.OAK), // Oak tree (level 15) - FIXED ID
			new TreeMapping(1307, TreeDefinitions.MAPLE), // Maple tree (level 45)
			new TreeMapping(1309, TreeDefinitions.YEW), // Yew tree (level 60)
	};

	// Tree locations - same as before
	private static final WorldTile[] TREE_LOCATIONS = {
			// Normal trees (1276) - Column 1
			new WorldTile(1378, 5681, 0), new WorldTile(1372, 5681, 0), new WorldTile(1375, 5681, 0),
			new WorldTile(1388, 5682, 0),

			// Oak trees (1281) - Column 2
			new WorldTile(1388, 5682, 0), new WorldTile(1383, 5685, 0), new WorldTile(1386, 5684, 0),
			new WorldTile(1379, 5686, 0),

			// Maple trees (1307) - Column 3
			new WorldTile(1375, 5686, 0), new WorldTile(1368, 5686, 0), new WorldTile(1371, 5686, 0),
			new WorldTile(1365, 5655, 0),

			// Yew trees (1309) - Column 4
			new WorldTile(1363, 5683, 0)
	};

	// Bank waypoint navigation (consistent with AutoFishingHandler and World.java)
	private static final int[] BANK_OBJECT_IDS = { 89398, 2213, 11758, 27663, 28589, 29085 }; // Same as AutoSkillingManager
	private static final long MESSAGE_THROTTLE_TIME = 5000; // 5 seconds between messages
	private static long lastWalkMessage = 0;

	public static AutoWoodcuttingHandler getInstance() {
		if (instance == null) {
			instance = new AutoWoodcuttingHandler();
		}
		return instance;
	}

	/**
	 * UPDATED PROCESS - Now supports both AUTO and SPECIFIC modes with bank
	 * waypoint navigation
	 */
	@Override
	public void process(Player player) {
		// Quick inventory check
		if (player.getInventory().getFreeSlots() <= 0) {
			player.sendMessage("Your inventory is full! Auto-woodcutting stopped.");
			player.getActionManager().forceStop();
			return;
		}

		// Check if player left the entire skilling hub - if so, stop auto-woodcutting
		if (!isInSkillingHub(player)) {
			player.sendMessage("You left the skilling hub. Auto-woodcutting stopped.");
			player.getActionManager().forceStop();
			return;
		}

		// Check for tree upgrades (only in AUTO mode)
		String mode = player.getAutoWoodcuttingMode();
		if ("AUTO".equals(mode)) {
			checkForTreeUpgrade(player);
		}

		// If player is already doing regular woodcutting, let it continue
		if (player.getActionManager().getAction() instanceof Woodcutting) {
			return; // Let regular woodcutting handle itself
		}

		// Only act if player is not busy (doing other actions or actively walking)
		if (player.getActionManager().getAction() != null || player.hasWalkSteps()) {
			return; // Player is doing something else or currently pathfinding
		}

		// If player is in hub but not near trees, use bank waypoint navigation
		if (!isNearTreeArea(player)) {
			walkToTreeAreaViaBank(player);
			return;
		}

		// Find and start cutting the appropriate tree
		startCuttingTree(player);
	}

	/**
	 * Check if player can upgrade to a better tree type (AUTO mode only)
	 */
	private void checkForTreeUpgrade(Player player) {
		TreeDefinitions currentTree = player.getAutoWoodcuttingTree();
		if (currentTree == null) {
			// In AUTO mode, set to best available tree if none set
			TreeDefinitions bestAvailable = getBestAvailableTree(player);
			if (bestAvailable != null) {
				player.setAutoWoodcuttingTree(bestAvailable);
				player.sendMessage("Auto-woodcutting set to " + bestAvailable.name() + " trees!");
			}
			return;
		}

		TreeDefinitions bestAvailable = getBestAvailableTree(player);
		if (bestAvailable == null)
			return;

		// If a better tree is available, upgrade automatically (AUTO mode only)
		if (bestAvailable != currentTree && bestAvailable.getLevel() > currentTree.getLevel()) {
			// Check if the new tree type actually has available trees
			int newObjectId = getObjectIdForTree(bestAvailable);
			if (hasAvailableTreesOfType(newObjectId, bestAvailable)) {
				player.setAutoWoodcuttingTree(bestAvailable);
				player.sendMessage(
						"Level up detected! Auto-woodcutting upgraded to " + bestAvailable.name() + " trees!");

				// Stop current action to force tree switch
				if (player.getActionManager().getAction() instanceof Woodcutting) {
					player.getActionManager().forceStop();
				}
			}
		}
	}

	/**
	 * Find appropriate tree and start cutting based on mode
	 */
	private void startCuttingTree(Player player) {
		// Get the tree type we should target
		TreeDefinitions targetTree = player.getAutoWoodcuttingTree();
		if (targetTree == null) {
			// Fallback: find best available tree (should not happen after canStart checks)
			targetTree = getBestAvailableTree(player);
			if (targetTree == null) {
				player.sendMessage("No suitable trees found! Stopping auto-woodcutting.");
				player.getActionManager().forceStop(); // Stop if no trees
				return;
			}
			player.setAutoWoodcuttingTree(targetTree);
		}

		// In SPECIFIC mode, validate that player can still cut the selected tree
		String mode = player.getAutoWoodcuttingMode();
		if ("SPECIFIC".equals(mode)) {
			int playerLevel = player.getSkills().getLevel(Skills.WOODCUTTING);
			if (playerLevel < targetTree.getLevel()) {
				player.sendMessage("You can no longer cut " + targetTree.name() + " trees! (Level requirement: "
						+ targetTree.getLevel() + ")");
				player.getActionManager().forceStop();
				return;
			}
		}

		// Find the best available tree of that type
		WorldObject treeObject = findBestTree(player, targetTree);
		if (treeObject == null) {
			// Handle case where all trees are stumps/respawning
			handleNoAvailableTrees(player, targetTree, mode);
			return;
		}

		// Position player if needed - now uses intelligent pathfinding
		if (!isPlayerInPosition(player, treeObject)) {
			moveToTree(player, treeObject);
			return;
		}

		// Don't start cutting if player is still walking from a previous pathfinding operation
		if (player.hasWalkSteps()) {
			return; // Wait until player stops walking
		}

		// Face the tree first before starting (realistic behavior)
		player.faceObject(treeObject);

		// Add a realistic delay before starting woodcutting (like manual gameplay)
		final WorldObject finalTreeObject = treeObject; // Final reference for inner class
		final TreeDefinitions finalTargetTree = targetTree;

		com.rs.game.tasks.WorldTasksManager.schedule(new com.rs.game.tasks.WorldTask() {
			@Override
			public void run() {
				// Make sure player stopped walking and is ready
				if (player.hasWalkSteps()) {
					return; // Still walking, try again next cycle
				}

				// Double-check tree is still available after facing delay
				if (!World.containsObjectWithId(finalTreeObject, finalTreeObject.getId())) {
					// Tree disappeared while we were facing it
					sendThrottledMessage(player, "Tree disappeared. Searching for new tree.");
					player.getActionManager().forceStop(); // Force stop to re-evaluate next tick
					return;
				}

				// Double-check player is still in position
				if (!isPlayerInPosition(player, finalTreeObject)) {
					// Player moved, try again next cycle
					sendThrottledMessage(player, "Moved away from tree. Re-positioning.");
					player.getActionManager().forceStop(); // Force stop to re-evaluate next tick
					return;
				}

				// Face the tree one more time to ensure proper direction
				player.faceObject(finalTreeObject);

				// Start regular woodcutting action
				Woodcutting woodcuttingAction = new Woodcutting(finalTreeObject, finalTargetTree);
				player.getActionManager().setAction(woodcuttingAction);
			}
		}, 1); // Changed delay to 1 tick (600ms) for consistency, pathfinding should handle long walks
	}

	/**
	 * Find the best available tree of the target type (skips stumps and missing
	 * trees)
	 */
	private WorldObject findBestTree(Player player, TreeDefinitions targetType) {
		int targetObjectId = getObjectIdForTree(targetType);
		if (targetObjectId == -1)
			return null;

		WorldObject closest = null;
		int shortestDistance = Integer.MAX_VALUE;

		// Check all tree locations for the target type
		for (WorldTile location : TREE_LOCATIONS) {
			// First check for the actual tree
			WorldObject tree = World.getObjectWithId(location, targetObjectId);

			if (tree != null && isTreeAvailable(tree, targetType)) {
				// Only consider trees that the player can actually reach
				if (canReachTarget(player, tree)) {
					int distance = getDistance(player, tree);
					if (distance < shortestDistance) {
						closest = tree;
						shortestDistance = distance;
					}
				}
			}
			// If no tree found at this location, it might be cut down/respawning
			// We'll just skip this location and try others
		}

		return closest;
	}

	/**
	 * Check if player is in cutting position (adjacent to tree)
	 */
	private boolean isPlayerInPosition(Player player, WorldObject tree) {
		// Player must be adjacent (within 1 tile distance) to the tree object.
		// Also check if player can actually reach the object (pathfinding check).
		return player.withinDistance(new WorldTile(tree.getX(), tree.getY(), tree.getPlane()), 1) &&
				canReachTarget(player, tree);
	}

	/**
	 * Move player to cutting position using intelligent pathfinding.
	 */
	private void moveToTree(Player player, WorldObject tree) {
		WorldTile targetPosition = findAdjacentTile(tree);
		if (targetPosition != null && isInSkillingHub(targetPosition)) { // Ensure target is in hub
			// Use calcFollow for intelligent pathfinding
			if (!player.calcFollow(targetPosition, true)) {
				sendThrottledMessage(player, "No path found to tree. Re-evaluating.");
				player.getActionManager().forceStop(); // Force stop to re-evaluate next tick
			} else {
				sendThrottledMessage(player, "Moving to tree...");
			}
		} else {
			sendThrottledMessage(player, "Could not determine a safe tile to move to for woodcutting. Re-evaluating.");
			player.getActionManager().forceStop();
		}
	}

	/**
	 * Find a safe adjacent tile for cutting.
	 * Now explicitly checks World.isTileFree based on your World.java.
	 */
	private WorldTile findAdjacentTile(WorldObject tree) {
		int[][] offsets = {
				{ -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, // Cardinals first
				{ -1, -1 }, { 1, -1 }, { -1, 1 }, { 1, 1 } // Diagonals
		};

		// Try to find a walkable and safe tile around the object
		for (int[] offset : offsets) {
			int x = tree.getX() + offset[0];
			int y = tree.getY() + offset[1];
			WorldTile tile = new WorldTile(x, y, tree.getPlane());

			// Check if this tile is within the skilling hub boundaries AND is actually walkable
			// Using World.isTileFree(plane, x, y, size) from your World class, assuming player size 1
			boolean isWalkableByClipping = World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), 1);

			if (isInSkillingHub(tile) && isWalkableByClipping) {
				return tile;
			}
		}

		// Fallback: If no ideal adjacent tile is found, try the tile west of the tree.
		// This fallback might still lead to issues if it's blocked.
		// A more robust solution might involve returning null and letting the main loop re-evaluate.
		return new WorldTile(tree.getX() - 1, tree.getY(), tree.getPlane());
	}

	/**
	 * Walk to tree area using bank as waypoint for obstacle avoidance.
	 * This method prioritizes reaching a bank object first if needed, then proceeding to the trees.
	 */
	private void walkToTreeAreaViaBank(Player player) {
		// Check if we already have a target bank stored
		WorldObject targetBank = (WorldObject) player.getTemporaryAttributtes().get("waypoint_bank_woodcutting"); // Renamed for clarity

		// If no target bank is stored or it's no longer valid in the world
		if (targetBank == null || (targetBank.getId() != 0 && !World.containsObjectWithId(targetBank, targetBank.getId()))) {
			targetBank = findClosestBankObject(player);
			if (targetBank == null) {
				sendThrottledMessage(player, "No accessible bank found for waypoint navigation. Seeking tree directly.");
				walkToTreeArea(player); // Fallback to direct walk if no bank found
				return;
			}
			player.getTemporaryAttributtes().put("waypoint_bank_woodcutting", targetBank); // Store new target
		}

		// Check if we reached the bank waypoint
		if (isAtBankObject(player, targetBank)) {
			// We're at the bank, now continue to trees
			player.getTemporaryAttributtes().remove("waypoint_bank_woodcutting"); // Clean up
			sendThrottledMessage(player, "Reached waypoint, continuing to trees...");
			walkToTreeArea(player);
			return;
		}

		// Not at bank yet, keep walking there if not already moving
		if (!player.hasWalkSteps()) {
			// Use calcFollow for intelligent pathfinding to the bank object
			if (!player.calcFollow(targetBank, true)) {
				sendThrottledMessage(player, "Path to bank blocked. Trying direct path to trees.");
				walkToTreeArea(player); // Fallback if path to bank is blocked
			} else {
				sendThrottledMessage(player, "Using bank as waypoint to reach trees...");
			}
		}
	}

	/**
	 * Walk player back to tree area (when they're in hub but away from trees)
	 * Now uses intelligent pathfinding.
	 */
	private void walkToTreeArea(Player player) {
		// Find the center of the tree area for walking back
		WorldTile treeCenterTile = new WorldTile(1375, 5674, 0); // Center of tree area

		// Only try to walk if not already walking
		if (!player.hasWalkSteps()) {
			if (!player.calcFollow(treeCenterTile, true)) {
				sendThrottledMessage(player, "No path found to tree area. Waiting.");
				player.getActionManager().forceStop(); // Stop if no path
			} else {
				sendThrottledMessage(player, "Walking to general tree area...");
			}
		}
	}

	/**
	 * Find the closest bank WorldObject (consistent with AutoFishingHandler logic).
	 * Iterates through regions within the hub bounds to find objects.
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
				Region region = World.getRegion(regionId); // Assuming World.getRegion loads it if needed

				if (region != null) {
					for (int bankId : BANK_OBJECT_IDS) {
						// Check all objects in the region that match a bank ID
						for (WorldObject obj : region.getAllObjects()) { // Assuming getAllObjects() exists
							if (obj.getId() == bankId &&
								obj.getX() >= minX && obj.getX() <= maxX && // Ensure bank is within hub boundaries
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
	 * Calculates distance to a WorldObject.
	 */
	private int getDistanceToWorldObject(Player player, WorldObject obj) {
		return Math.abs(player.getX() - obj.getX()) + Math.abs(player.getY() - obj.getY());
	}

	/**
	 * Check if player is at bank WorldObject (consistent with AutoFishingHandler logic).
	 */
	private boolean isAtBankObject(Player player, WorldObject bank) {
		if (bank == null) {
			return false;
		}
		// Within 3 tiles for interaction range
		return player.withinDistance(new WorldTile(bank.getX(), bank.getY(), bank.getPlane()), 3);
	}

	/**
	 * Send throttled message to prevent spam (consistent with AutoFishingHandler logic).
	 */
	private void sendThrottledMessage(Player player, String message) {
		long currentTime = Utils.currentTimeMillis(); // Use Utils.currentTimeMillis
		if (currentTime - lastWalkMessage > MESSAGE_THROTTLE_TIME) {
			player.sendMessage(message);
			lastWalkMessage = currentTime;
		}
	}

	/**
	 * Check if player is in the skilling hub (consistent logic).
	 */
	private boolean isInSkillingHub(Player player) {
		// Skilling hub coordinates from AutoSkillingManager
		final int SKILLING_HUB_CENTER_X = 1375;
		final int SKILLING_HUB_CENTER_Y = 5669;
		final int SKILLING_HUB_SIZE = 90;

		int playerX = player.getX();
		int playerY = player.getY();

		// Check if within the 90x90 skilling hub area
		int radius = SKILLING_HUB_SIZE / 2;
		return playerX >= (SKILLING_HUB_CENTER_X - radius) && playerX <= (SKILLING_HUB_CENTER_X + radius)
				&& playerY >= (SKILLING_HUB_CENTER_Y - radius) && playerY <= (SKILLING_HUB_CENTER_Y + radius);
	}
	
	/**
	 * Helper to check if a WorldTile is within the skilling hub boundaries.
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
	 * Check if player is near the tree area (more lenient range)
	 */
	private boolean isNearTreeArea(Player player) {
		for (WorldTile location : TREE_LOCATIONS) {
			if (player.withinDistance(location, 15)) { // 15 tiles range
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if a tree is available for cutting (handles non-respawning trees)
	 */
	private boolean isTreeAvailable(WorldObject tree, TreeDefinitions treeType) {
		// Handle trees that don't respawn in the skilling area
		if (treeType == TreeDefinitions.NORMAL || treeType == TreeDefinitions.OAK) {
			// Normal and Oak trees don't respawn in skilling area
			return true; // If object exists, it's available
		}

		// For Maple and Yew trees, check for stumps
		int stumpId = treeType.getStumpId();
		if (stumpId != -1 && tree.getId() == stumpId) {
			return false; // This is a stump, not available
		}

		return true; // This is a real tree, available for cutting
	}

	/**
	 * Handle case where no trees of target type are available
	 */
	private void handleNoAvailableTrees(Player player, TreeDefinitions targetTree, String mode) {
		boolean isRespawningTree = (targetTree == TreeDefinitions.MAPLE || targetTree == TreeDefinitions.YEW);

		if ("AUTO".equals(mode)) {
			// In AUTO mode, try to find alternative available trees
			TreeDefinitions alternativeTree = findAlternativeTree(player, targetTree);
			if (alternativeTree != null) {
				player.setAutoWoodcuttingTree(alternativeTree);
				if (isRespawningTree) {
					sendThrottledMessage(player, "All " + targetTree.name() + " trees are cut. Switching to "
							+ alternativeTree.name() + " trees temporarily...");
				} else {
					sendThrottledMessage(player, "All " + targetTree.name() + " trees are cut. Switching to "
							+ alternativeTree.name() + " trees...");
				}
				return;
			}
			// No alternatives available
			if (isRespawningTree) {
				sendThrottledMessage(player, "All trees are currently cut down. Waiting for respawn...");
			} else {
				sendThrottledMessage(player, "All " + targetTree.name()
						+ " trees are cut. These trees don't respawn - trying other tree types."); // Minor wording change
			}
		} else if ("SPECIFIC".equals(mode)) {
			// In SPECIFIC mode, give appropriate message based on tree type
			if (isRespawningTree) {
				sendThrottledMessage(player, "All " + targetTree.name() + " trees are cut down. Waiting for respawn...");
			} else {
				sendThrottledMessage(player,
						"All " + targetTree.name() + " trees are cut. These trees don't respawn in the skilling area!");
			}
		}
	}

	/**
	 * Find an alternative tree type that's available (AUTO mode only) Prioritizes
	 * respawning trees over non-respawning ones
	 */
	private TreeDefinitions findAlternativeTree(Player player, TreeDefinitions currentTree) {
		int playerLevel = player.getSkills().getLevel(Skills.WOODCUTTING);

		TreeDefinitions bestRespawningAlternative = null;
		TreeDefinitions bestNonRespawningAlternative = null;

		// Check other tree types player can cut, starting from best available
		for (int i = TREE_MAPPINGS.length - 1; i >= 0; i--) {
			TreeMapping mapping = TREE_MAPPINGS[i];
			TreeDefinitions treeType = mapping.treeDefinition;

			// Skip the current tree type and trees above player level
			if (treeType == currentTree || playerLevel < treeType.getLevel()) {
				continue;
			}

			// Check if this tree type has available trees
			if (hasAvailableTreesOfType(mapping.objectId, treeType)) {
				// Ensure the player can actually reach this alternative tree type
				if (findBestTree(player, treeType) != null) { // Check if there's a reachable tree of this type
					if (treeType == TreeDefinitions.MAPLE || treeType == TreeDefinitions.YEW) {
						// Prioritize respawning trees
						if (bestRespawningAlternative == null || bestRespawningAlternative.getLevel() < treeType.getLevel()) {
							bestRespawningAlternative = treeType;
						}
					} else {
						// Store non-respawning as fallback
						if (bestNonRespawningAlternative == null || bestNonRespawningAlternative.getLevel() < treeType.getLevel()) {
							bestNonRespawningAlternative = treeType;
						}
					}
				}
			}
		}

		// Return respawning alternative first, fallback to non-respawning
		return bestRespawningAlternative != null ? bestRespawningAlternative : bestNonRespawningAlternative;
	}

	/**
	 * Check if there are any available trees of the specified type that are reachable
	 */
	private boolean hasAvailableTreesOfType(int objectId, TreeDefinitions treeType) {
		// This method implicitly relies on findBestTree(player, treeType) to confirm reachability.
		// For consistency, this method should probably just check if the object exists in the world
		// at any of its defined locations, and let findBestTree handle player reachability.
		// Re-checking hasAvailableTreesOfType will make this more robust.
		for (WorldTile location : TREE_LOCATIONS) {
			WorldObject tree = World.getObjectWithId(location, objectId);
			if (tree != null && isTreeAvailable(tree, treeType)) {
				// Don't check player reachability here, let findBestTree handle that with calcFollow
				return true;
			}
		}
		return false; // No tree objects found in the world matching this type
	}


	/**
	 * Check if tile is safe (no clipping from objects or trees on it)
	 * Now explicitly uses World.isTileFree.
	 */
	private boolean isTileSafe(WorldTile tile) {
		// Check if the tile is free from general clipping
		// Assuming player size 1 for basic tile walkability check
		boolean isFree = World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), 1);

		// Additionally check if the tile is occupied by an actual tree object.
		// This might be redundant if World.isTileFree accounts for objects,
		// but it's good to be explicit given the tree handling.
		if (isFree) {
			for (TreeMapping mapping : TREE_MAPPINGS) {
				if (World.getObjectWithId(tile, mapping.objectId) != null) {
					return false; // Tile is occupied by a tree object
				}
			}
		}
		return isFree; // Return true only if free from clipping and not a tree object
	}

	/**
	 * Get distance between player and tree
	 */
	private int getDistance(Player player, WorldObject tree) {
		return Math.abs(player.getX() - tree.getX()) + Math.abs(player.getY() - tree.getY());
	}

	/**
	 * Get object ID for tree type
	 */
	private int getObjectIdForTree(TreeDefinitions treeType) {
		for (TreeMapping mapping : TREE_MAPPINGS) {
			if (mapping.treeDefinition == treeType) {
				return mapping.objectId;
			}
		}
		return -1;
	}

	/**
	 * Check if player has hatchet
	 */
	public static boolean hasHatchet(Player player) {
		// Check inventory
		if (player.getInventory().containsOneItem(1351, 1349, 1353, 1355, 1357, 1361, 1359, 6739, 13661, 32645)) {
			return true;
		}

		// Check equipped weapon
		int weaponId = player.getEquipment().getWeaponId();
		switch (weaponId) {
		case 1351:
		case 1349:
		case 1353:
		case 1361:
		case 1355:
		case 1357:
		case 1359:
		case 6739:
		case 13661:
		case 32645:
			return true;
		default:
			return false;
		}
	}
	
	public static boolean isHatchet(int itemId) {
	    // Add all hatchet item IDs here
	    switch(itemId) {
	        case 1351: case 1349: case 1353: case 1355: case 1357: case 1361: case 1359: case 6739: case 13661: case 32645:
	            return true;
	        default:
	            return false;
	    }
	}

	/**
	 * Find best tree type player can cut (prioritizes respawning trees)
	 */
	private TreeDefinitions getBestAvailableTree(Player player) {
		int level = player.getSkills().getLevel(Skills.WOODCUTTING);

		TreeDefinitions bestRespawningTree = null;
		TreeDefinitions bestNonRespawningTree = null;

		// Check from highest to lowest level
		for (int i = TREE_MAPPINGS.length - 1; i >= 0; i--) {
			TreeMapping mapping = TREE_MAPPINGS[i];
			if (level >= mapping.treeDefinition.getLevel()) {
				// Check if this tree type exists AND has a reachable object in the world
				if (hasAvailableTreesOfType(mapping.objectId, mapping.treeDefinition)) { // Re-check this
					if (findBestTree(player, mapping.treeDefinition) != null) { // Only if a reachable one exists
						if (mapping.treeDefinition == TreeDefinitions.MAPLE
								|| mapping.treeDefinition == TreeDefinitions.YEW) {
							// Prioritize respawning trees (Maple/Yew)
							if (bestRespawningTree == null || bestRespawningTree.getLevel() < mapping.treeDefinition.getLevel()) {
								bestRespawningTree = mapping.treeDefinition;
							}
						} else {
							// Store non-respawning trees as fallback (Normal/Oak)
							if (bestNonRespawningTree == null || bestNonRespawningTree.getLevel() < mapping.treeDefinition.getLevel()) {
								bestNonRespawningTree = mapping.treeDefinition;
							}
						}
					}
				}
			}
		}

		// Return respawning trees first, fallback to non-respawning
		return bestRespawningTree != null ? bestRespawningTree : bestNonRespawningTree;
	}

	/**
	 * Check if tree type is available in the area (and not all stumps)
	 * This method now checks for the physical presence of the tree object.
	 * Reachability is handled by findBestTree.
	 */
	private boolean isTreeTypeAvailable(int objectId) {
		// Find the tree type for this object ID
		TreeDefinitions treeType = null;
		for (TreeMapping mapping : TREE_MAPPINGS) {
			if (mapping.objectId == objectId) {
				treeType = mapping.treeDefinition;
				break;
			}
		}

		if (treeType == null)
			return false;

		// Check if there are any available trees (not stumps) by looking for the object in the world
		return hasAvailableTreesOfType(objectId, treeType);
	}

	@Override
	public String getSkillName() {
		return "Woodcutting";
	}

	@Override
	public boolean canStart(Player player) {
		// FIRST CHECK: Must be in skilling hub
		if (!isInSkillingHub(player)) {
			player.sendMessage("Auto-woodcutting can only be used in the skilling hub!");
			return false;
		}

		// Basic checks
		if (!hasHatchet(player)) {
			player.sendMessage("You need a hatchet to start auto-woodcutting!");
			return false;
		}

		if (player.getInventory().getFreeSlots() <= 1) {
			player.sendMessage("You need at least 2 free inventory slots!");
			return false;
		}

		// Mode-specific validation
		String mode = player.getAutoWoodcuttingMode();
		TreeDefinitions targetTree = player.getAutoWoodcuttingTree();

		if ("AUTO".equals(mode)) {
			// For AUTO mode, find the best tree if none set
			if (targetTree == null) {
				TreeDefinitions bestTree = getBestAvailableTree(player);
				if (bestTree == null) {
					player.sendMessage("No suitable trees found for your level!");
					return false;
				}
				player.setAutoWoodcuttingTree(bestTree);
				player.sendMessage("Auto-woodcutting will target " + bestTree.name() + " trees (automatic mode).");
			} else {
				player.sendMessage("Auto-woodcutting will target " + targetTree.name() + " trees (automatic mode).");
			}
		} else if ("SPECIFIC".equals(mode)) {
			// For SPECIFIC mode, validate the selected tree
			if (targetTree == null) {
				player.sendMessage("No specific tree selected! This should not happen.");
				return false;
			}

			int playerLevel = player.getSkills().getLevel(Skills.WOODCUTTING);
			if (playerLevel < targetTree.getLevel()) {
				player.sendMessage("You need level " + targetTree.getLevel() + " Woodcutting to cut "
						+ targetTree.name() + " trees!");
				return false;
			}

			// Check if the specific tree type is available AND reachable
			WorldObject actualTree = findBestTree(player, targetTree); // Use findBestTree for reachability check
			if (actualTree == null) { // If findBestTree returns null, no reachable tree of that type
				player.sendMessage("No " + targetTree.name() + " trees found in the skilling area or reachable!");
				return false;
			}

			player.sendMessage("Auto-woodcutting will target " + targetTree.name() + " trees only (specific mode).");
		} else {
			// Fallback for missing mode (should not happen)
			player.sendMessage("Woodcutting mode not set! Please restart from the dialogue.");
			return false;
		}

		// Final check that a reachable tree exists for the chosen type/mode
		if (findBestTree(player, player.getAutoWoodcuttingTree()) == null) {
		    player.sendMessage("No reachable trees of the selected type/mode found.");
		    return false;
		}

		return true;
	}

	/**
	 * Tree mapping helper class
	 */
	private static class TreeMapping {
		final int objectId;
		final TreeDefinitions treeDefinition;

		TreeMapping(int objectId, TreeDefinitions treeDefinition) {
			this.objectId = objectId;
			this.treeDefinition = treeDefinition;
		}
	}

    /**
     * Helper method to check if a player can reach a target WorldTile (or WorldObject).
     * This utilizes the server's RouteFinder.
     * @param player The player trying to reach the target.
     * @param target The WorldTile (or WorldObject) to reach.
     * @return true if a path exists and the target can be reached, false otherwise.
     */
    private boolean canReachTarget(Player player, WorldTile target) {
        // Use RouteFinder.findRoute to determine if a path exists.
        // RouteFinder.findRoute returns:
        //   > 0: A path was found and steps are needed.
        //   = 0: Target is already reached (no steps needed).
        //   < 0: No path found.
        int result = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER,
                                            player.getX(), player.getY(), player.getPlane(),
                                            player.getSize(),
                                            target instanceof WorldObject ? new com.rs.game.route.strategy.ObjectStrategy((WorldObject) target) : new FixedTileStrategy(target.getX(), target.getY()),
                                            true); // allow finding alternative paths

        return result >= 0; // If result is 0 or positive, a path exists or target is already reached.
    }
}
