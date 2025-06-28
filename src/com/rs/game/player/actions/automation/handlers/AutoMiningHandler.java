package com.rs.game.player.actions.automation.handlers;

import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.mining.Mining;
import com.rs.game.player.actions.mining.Mining.RockDefinitions;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.World;
import java.util.List;
import java.util.ArrayList;
// ADDED: Imports required for intelligent pathfinding
import com.rs.game.route.RouteFinder;
import com.rs.game.route.strategy.ObjectStrategy;
import com.rs.game.route.strategy.FixedTileStrategy;

/**
 * Auto-Mining Handler VERSION: Based on the user's "working" file, with only
 * pathfinding upgraded to fix object clipping.
 */
public class AutoMiningHandler implements SkillHandler {

	private static AutoMiningHandler instance;

	// Kept all original coordinates and constants from your file
	private static final int MINING_ZONE_MIN_X = 1330;
	private static final int MINING_ZONE_MAX_X = 1420;
	private static final int MINING_ZONE_MIN_Y = 5624;
	private static final int MINING_ZONE_MAX_Y = 5714;

	private static final RockMapping[] ROCK_MAPPINGS = { new RockMapping(2090, RockDefinitions.Copper_Ore, true),
			new RockMapping(2094, RockDefinitions.Tin_Ore, true), new RockMapping(2092, RockDefinitions.Iron_Ore, true),
			new RockMapping(2093, RockDefinitions.Coal_Ore, true),
			new RockMapping(2094, RockDefinitions.Silver_Ore, true),
			new RockMapping(2098, RockDefinitions.Gold_Ore, true),
			new RockMapping(2102, RockDefinitions.Mithril_Ore, true),
			new RockMapping(2104, RockDefinitions.Adamant_Ore, true),
			new RockMapping(14859, RockDefinitions.Runite_Ore, true),
			new RockMapping(2099, RockDefinitions.GEM_ROCK, true), };

	private long lastRockScan = 0;
	private static final long ROCK_SCAN_INTERVAL = 5000;
	private List<WorldObject> cachedRocks = new ArrayList<>();

	private static final int[] BANK_IDS = { 89398, 2213, 11758, 27663, 28589, 29085 };
	private static final long MESSAGE_THROTTLE_TIME = 5000;
	private static long lastWalkMessage = 0;

	public static AutoMiningHandler getInstance() {
		if (instance == null) {
			instance = new AutoMiningHandler();
		}
		return instance;
	}

	@Override
	public void process(Player player) {
		try {
			if (player.getInventory().getFreeSlots() <= 0 || !isInMiningZone(player)) {
				return;
			}

			if ("AUTO".equals(player.getAutoMiningMode())) {
				checkForRockUpgrade(player);
			}

			if (player.getActionManager().getAction() instanceof Mining || player.hasWalkSteps()) {
				return;
			}

			if (player.getActionManager().getAction() != null) {
				return;
			}

			if (!isNearMiningArea(player)) {
				walkToMiningAreaViaBank(player);
				return;
			}

			startMiningRock(player);

		} catch (Exception e) {
			System.out.println("ERROR in AutoMiningHandler.process: " + e.getMessage());
		}
	}

	/**
	 * FIXED: Upgraded with intelligent pathfinding.
	 */
	private void walkToMiningAreaViaBank(Player player) {
		WorldObject targetBank = (WorldObject) player.getTemporaryAttributtes().get("waypoint_bank_mining");

		if (targetBank == null) {
			targetBank = findClosestBank(player);
			if (targetBank == null) {
				sendThrottledMessage(player, "No bank found for waypoint navigation!");
				return;
			}
			player.getTemporaryAttributtes().put("waypoint_bank_mining", targetBank);
		}

		if (isAtBank(player, targetBank)) {
			player.getTemporaryAttributtes().remove("waypoint_bank_mining");
			sendThrottledMessage(player, "Reached waypoint, continuing to mining area...");
			walkToMiningArea(player);
			return;
		}

		if (!player.hasWalkSteps()) {
			sendThrottledMessage(player, "Using bank as waypoint to reach mining area...");
			// REPLACED: player.addWalkStepsInteract with player.calcFollow
			player.calcFollow(targetBank, true);
		}
	}

	/**
	 * FIXED: Upgraded with intelligent pathfinding.
	 */
	private void walkToMiningArea(Player player) {
		WorldTile miningCenterTile = new WorldTile(1375, 5669, 0);
		if (!player.hasWalkSteps()) {
			// REPLACED: player.addWalkSteps with player.calcFollow
			player.calcFollow(miningCenterTile, true);
		}
	}

	/**
	 * FIXED: Upgraded with intelligent pathfinding.
	 */
	private void moveToRock(Player player, WorldObject rock) {
		// The calcFollow method is smart enough to find an adjacent tile on its own.
		// We no longer need findSafeMiningPosition or isTileSafe.
		sendThrottledMessage(player, "Moving to rock...");
		// REPLACED: player.addWalkSteps with player.calcFollow
		if (!player.calcFollow(rock, true)) {
			sendThrottledMessage(player, "Could not find a path to that rock.");
		}
	}

	/**
	 * FIXED: This now checks for reachability, not just distance, to prevent
	 * getting stuck behind walls.
	 */
	private boolean isPlayerInMiningPosition(Player player, WorldObject rock) {
		return player.withinDistance(rock, 1) && canReachTarget(player, rock);
	}

	/**
	 * NEW: The master pathfinding check from the other working handlers.
	 */
	private boolean canReachTarget(Player player, WorldTile target) {
		int result = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getX(), player.getY(),
				player.getPlane(), player.getSize(),
				target instanceof WorldObject ? new ObjectStrategy((WorldObject) target)
						: new FixedTileStrategy(target.getX(), target.getY()),
				true);
		return result >= 0;
	}

	// --- UNCHANGED METHODS ---
	// All of your "working" logic below this line has been kept exactly as you
	// provided it.

	private WorldObject findClosestBank(Player player) {
		WorldObject closestBank = null;
		int shortestDistance = Integer.MAX_VALUE;
		final int SKILLING_HUB_CENTER_X = 1375;
		final int SKILLING_HUB_CENTER_Y = 5669;
		final int SKILLING_HUB_SIZE = 90;
		int radius = SKILLING_HUB_SIZE / 2;
		int minX = SKILLING_HUB_CENTER_X - radius;
		int maxX = SKILLING_HUB_CENTER_X + radius;
		int minY = SKILLING_HUB_CENTER_Y - radius;
		int maxY = SKILLING_HUB_CENTER_Y + radius;
		for (int x = minX; x <= maxX; x++) {
			for (int y = minY; y <= maxY; y++) {
				WorldTile tile = new WorldTile(x, y, 0);
				for (int bankId : BANK_IDS) {
					WorldObject bank = World.getObjectWithId(tile, bankId);
					if (bank != null) {
						int distance = getDistanceToBank(player, bank);
						if (distance < shortestDistance) {
							closestBank = bank;
							shortestDistance = distance;
						}
					}
				}
			}
		}
		return closestBank;
	}

	private int getDistanceToBank(Player player, WorldObject bank) {
		return Math.abs(player.getX() - bank.getX()) + Math.abs(player.getY() - bank.getY());
	}

	private boolean isAtBank(Player player, WorldObject bank) {
		if (bank == null) {
			return false;
		}
		return player.withinDistance(new WorldTile(bank.getX(), bank.getY(), bank.getPlane()), 3);
	}

	private void sendThrottledMessage(Player player, String message) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastWalkMessage > MESSAGE_THROTTLE_TIME) {
			player.sendMessage(message);
			lastWalkMessage = currentTime;
		}
	}

	private boolean isNearMiningArea(Player player) {
		List<WorldObject> rocks = findRocksInZone();
		for (WorldObject rock : rocks) {
			if (player.withinDistance(new WorldTile(rock.getX(), rock.getY(), rock.getPlane()), 15)) {
				return true;
			}
		}
		return false;
	}

	private List<WorldObject> findRocksInZone() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastRockScan < ROCK_SCAN_INTERVAL && !cachedRocks.isEmpty()) {
			return cachedRocks;
		}
		List<WorldObject> rocksFound = new ArrayList<>();
		try {
			for (int x = MINING_ZONE_MIN_X; x <= MINING_ZONE_MAX_X; x++) {
				for (int y = MINING_ZONE_MIN_Y; y <= MINING_ZONE_MAX_Y; y++) {
					WorldTile tile = new WorldTile(x, y, 0);
					for (RockMapping mapping : ROCK_MAPPINGS) {
						WorldObject rock = World.getObjectWithId(tile, mapping.objectId);
						if (rock != null && isRockAvailable(rock, mapping.rockDefinition)) {
							rocksFound.add(rock);
						}
					}
				}
			}
			cachedRocks = rocksFound;
			lastRockScan = currentTime;
		} catch (Exception e) {
			System.out.println("ERROR scanning rocks in zone: " + e.getMessage());
		}
		return rocksFound;
	}

	private WorldObject findBestRock(Player player) {
		RockDefinitions targetRock = player.getAutoMiningRock();
		if (targetRock == null) {
			return null;
		}
		List<WorldObject> availableRocks = findRocksInZone();
		if (availableRocks.isEmpty()) {
			return null;
		}
		List<WorldObject> targetRocks = new ArrayList<>();
		int targetObjectId = getObjectIdForRock(targetRock);
		for (WorldObject rock : availableRocks) {
			if (rock.getId() == targetObjectId) {
				targetRocks.add(rock);
			}
		}
		if (targetRocks.isEmpty()) {
			return null;
		}
		return getClosestRock(player, targetRocks);
	}

	private WorldObject getClosestRock(Player player, List<WorldObject> rocks) {
		WorldObject closest = null;
		int shortestDistance = Integer.MAX_VALUE;
		for (WorldObject rock : rocks) {
			int distance = getDistance(player, rock);
			if (distance < shortestDistance) {
				closest = rock;
				shortestDistance = distance;
			}
		}
		return closest;
	}

	private void checkForRockUpgrade(Player player) {
		RockDefinitions currentRock = player.getAutoMiningRock();
		if (currentRock == null) {
			RockDefinitions bestAvailable = getBestAvailableRock(player);
			if (bestAvailable != null) {
				player.setAutoMiningRock(bestAvailable);
				player.sendMessage("Auto-mining set to " + bestAvailable.name() + " rocks!");
			}
			return;
		}
		RockDefinitions bestAvailable = getBestAvailableRock(player);
		if (bestAvailable != null && bestAvailable != currentRock
				&& bestAvailable.getLevel() > currentRock.getLevel()) {
			if (hasAvailableRocksOfType(bestAvailable)) {
				player.setAutoMiningRock(bestAvailable);
				player.sendMessage("Level up! Upgraded to " + bestAvailable.name() + " rocks!");
				if (player.getActionManager().getAction() instanceof Mining) {
					player.getActionManager().forceStop();
				}
			}
		}
	}

	private void startMiningRock(Player player) {
		WorldObject targetRock = findBestRock(player);
		if (targetRock == null) {
			handleNoAvailableRocks(player);
			return;
		}
		if (!isPlayerInMiningPosition(player, targetRock)) {
			moveToRock(player, targetRock);
			return;
		}
		if (player.hasWalkSteps()) {
			return;
		}
		player.faceObject(targetRock);
		final WorldObject finalRock = targetRock;
		final RockDefinitions targetType = player.getAutoMiningRock();
		com.rs.game.tasks.WorldTasksManager.schedule(new com.rs.game.tasks.WorldTask() {
			@Override
			public void run() {
				if (player.hasWalkSteps())
					return;
				if (!World.containsObjectWithId(finalRock, finalRock.getId()))
					return;
				if (!isPlayerInMiningPosition(player, finalRock))
					return;
				player.faceObject(finalRock);
				Mining miningAction = new Mining(finalRock, targetType);
				player.getActionManager().setAction(miningAction);
			}
		}, 1);
	}

	private void handleNoAvailableRocks(Player player) {
		RockDefinitions targetRock = player.getAutoMiningRock();
		String mode = player.getAutoMiningMode();
		if (targetRock == null) {
			sendThrottledMessage(player, "No suitable rocks found!");
			return;
		}
		if ("AUTO".equals(mode)) {
			RockDefinitions alternative = findAlternativeRock(player, targetRock);
			if (alternative != null) {
				player.setAutoMiningRock(alternative);
				sendThrottledMessage(player, "Switching to " + alternative.name() + " rocks...");
				return;
			}
		}
		sendThrottledMessage(player, "Waiting for " + targetRock.name() + " rocks to respawn...");
	}

	private RockDefinitions findAlternativeRock(Player player, RockDefinitions currentRock) {
		int playerLevel = player.getSkills().getLevel(Skills.MINING);
		for (int i = ROCK_MAPPINGS.length - 1; i >= 0; i--) {
			RockMapping mapping = ROCK_MAPPINGS[i];
			if (mapping.rockDefinition != currentRock && playerLevel >= mapping.rockDefinition.getLevel()
					&& hasAvailableRocksOfType(mapping.rockDefinition)) {
				return mapping.rockDefinition;
			}
		}
		return null;
	}

	private boolean hasAvailableRocksOfType(RockDefinitions rockType) {
		int objectId = getObjectIdForRock(rockType);
		if (objectId == -1)
			return false;
		List<WorldObject> rocks = findRocksInZone();
		for (WorldObject rock : rocks) {
			if (rock.getId() == objectId && isRockAvailable(rock, rockType)) {
				return true;
			}
		}
		return false;
	}

	private RockDefinitions getBestAvailableRock(Player player) {
		int level = player.getSkills().getLevel(Skills.MINING);
		for (int i = ROCK_MAPPINGS.length - 1; i >= 0; i--) {
			RockMapping mapping = ROCK_MAPPINGS[i];
			if (level >= mapping.rockDefinition.getLevel() && hasAvailableRocksOfType(mapping.rockDefinition)) {
				return mapping.rockDefinition;
			}
		}
		return null;
	}

	private boolean isInMiningZone(Player player) {
		int x = player.getX();
		int y = player.getY();
		return x >= MINING_ZONE_MIN_X && x <= MINING_ZONE_MAX_X && y >= MINING_ZONE_MIN_Y && y <= MINING_ZONE_MAX_Y;
	}

	private boolean isRockAvailable(WorldObject rock, RockDefinitions rockType) {
		if (rockType.getEmptyId() != -1 && rock.getId() == rockType.getEmptyId()) {
			return false;
		}
		return true;
	}

	private int getObjectIdForRock(RockDefinitions rockType) {
		for (RockMapping mapping : ROCK_MAPPINGS) {
			if (mapping.rockDefinition == rockType) {
				return mapping.objectId;
			}
		}
		return -1;
	}

	private int getDistance(Player player, WorldObject rock) {
		return Math.abs(player.getX() - rock.getX()) + Math.abs(player.getY() - rock.getY());
	}

	private boolean hasPickaxe(Player player) {
		if (player.getInventory().containsOneItem(1265, 1267, 1269, 1271, 1273, 1275, 1291, 15259, 32646)) {
			return true;
		}
		int weaponId = player.getEquipment().getWeaponId();
		switch (weaponId) {
		case 1265:
		case 1267:
		case 1269:
		case 1271:
		case 1273:
		case 1275:
		case 1291:
		case 15259:
		case 32646:
			return true;
		default:
			return false;
		}
	}

	@Override
	public String getSkillName() {
		return "Mining";
	}

	@Override
	public boolean canStart(Player player) {
		if (!isInMiningZone(player)) {
			player.sendMessage("You must be in the skilling hub to start auto-mining!");
			return false;
		}
		if (!hasPickaxe(player)) {
			player.sendMessage("You need a pickaxe to start auto-mining!");
			return false;
		}
		if (player.getInventory().getFreeSlots() <= 1) {
			player.sendMessage("You need at least 2 free inventory slots!");
			return false;
		}
		String mode = player.getAutoMiningMode();
		RockDefinitions targetRock = player.getAutoMiningRock();
		if ("AUTO".equals(mode)) {
			if (targetRock == null) {
				RockDefinitions bestRock = getBestAvailableRock(player);
				if (bestRock == null) {
					player.sendMessage("No suitable rocks found in this area!");
					return false;
				}
				player.setAutoMiningRock(bestRock);
				player.sendMessage("Auto-mining will target " + bestRock.name() + " rocks (auto mode).");
			} else {
				player.sendMessage("Auto-mining will target " + targetRock.name() + " rocks (auto mode).");
			}
		} else if ("SPECIFIC".equals(mode)) {
			if (targetRock == null) {
				player.sendMessage("No specific rock selected!");
				return false;
			}
			int playerLevel = player.getSkills().getLevel(Skills.MINING);
			if (playerLevel < targetRock.getLevel()) {
				player.sendMessage("You need level " + targetRock.getLevel() + " Mining!");
				return false;
			}
			if (!hasAvailableRocksOfType(targetRock)) {
				player.sendMessage("No " + targetRock.name() + " rocks found in this area!");
				return false;
			}
			player.sendMessage("Auto-mining will target " + targetRock.name() + " rocks only (specific mode).");
		} else {
			player.sendMessage("Mining mode not set! Please restart from dialogue.");
			return false;
		}
		return true;
	}

	private static class RockMapping {
		final int objectId;
		final RockDefinitions rockDefinition;
		final boolean respawns;

		RockMapping(int objectId, RockDefinitions rockDefinition, boolean respawns) {
			this.objectId = objectId;
			this.rockDefinition = rockDefinition;
			this.respawns = respawns;
		}
	}

	// PASTE THE NEW METHOD RIGHT HERE
	public static boolean isPickaxe(int itemId) {
		switch (itemId) {
		case 1265: // Bronze pickaxe
		case 1267: // Iron pickaxe
		case 1269: // Steel pickaxe
		case 1271: // Mithril pickaxe
		case 1273: // Adamant pickaxe
		case 1275: // Rune pickaxe
		case 1291: // Dragon pickaxe
		case 15259: // Infernal pickaxe
		case 32646: // Crystal pickaxe
			return true;
		default:
			return false;
		}
	}

}