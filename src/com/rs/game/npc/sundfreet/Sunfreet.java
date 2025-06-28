package com.rs.game.npc.sundfreet;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.Drop;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.SlayerTask;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.NPCDrops;
import com.rs.utils.Utils;

/**
 * Sunfreet Boss NPC - Custom boss with damage tracking and shared drops
 * 
 * @author ryan (Improved)
 * 
 *         Features: - Individual damage tracking per player - Shared drop
 *         system for all eligible players - Auto-healing when no targets
 *         present - Custom prayer resistance (immune to melee prayer) -
 *         Extended target range (64 tiles) - Damage threshold requirement for
 *         rewards
 */
@SuppressWarnings("serial")
public class Sunfreet extends NPC {
	private static final long serialVersionUID = 6723681547223858148L;
	// Boss configuration constants
	private static final int HEAL_AMOUNT = 100000;
	private static final int HEAL_TICK_AMOUNT = 100;
	private static final int TARGET_DISTANCE = 64;
	private static final int REWARD_DISTANCE = 20;
	private static final int LURE_DELAY = 5000;
	private static final int MINIMUM_DAMAGE_FOR_REWARD = 1;

	// Prayer multipliers
	private static final double MELEE_PRAYER_MULTIPLIER = 0.00; // Immune to melee prayer
	private static final double MAGE_PRAYER_MULTIPLIER = 2.50; // Takes more damage if using mage prayer
	private static final double RANGE_PRAYER_MULTIPLIER = 2.50; // Takes more damage if using range prayer

	// Player reward tracking
	private final List<Player> rewardList = new ArrayList<>();

	public Sunfreet(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		initializeBossSettings();
	}

	/**
	 * Initialize boss-specific settings
	 */
	private void initializeBossSettings() {
		setLureDelay(LURE_DELAY);
		setForceTargetDistance(TARGET_DISTANCE);
		setForceFollowClose(false);
		setNoDistanceCheck(true);
		setIntelligentRouteFinder(false);
		setForceAgressive(false);
		setCantDoDefenceEmote(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();

		if (isDead()) {
			return;
		}

		// Auto-heal when no targets are present
		if (getHitpoints() < getMaxHitpoints() && getPossibleTargets().isEmpty()) {
			heal(HEAL_AMOUNT); // Full heal when no one is fighting
		}
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		super.handleIngoingHit(hit);

		if (!(hit.getSource() instanceof Player)) {
			return;
		}

		Player player = (Player) hit.getSource();
		trackPlayerDamage(player, hit.getDamage());
	}

	/**
	 * Track damage dealt by players for reward eligibility
	 */
	private void trackPlayerDamage(Player player, int damage) {
		// Initialize or reset player's damage tracking for this boss
		if (player.npcSunfreet != this) {
			player.npcSunfreet = this;
			player.npcSunfreetDmg = 0; // Reset damage counter for new boss
		}

		// Add damage to player's total
		player.npcSunfreetDmg += damage;

		// Add to reward list if they meet minimum damage and aren't already listed
		if (!rewardList.contains(player) && player.npcSunfreetDmg >= MINIMUM_DAMAGE_FOR_REWARD) {
			rewardList.add(player);
			// Notify player they're eligible for rewards
			player.sendMessage(Colors.green + "You've dealt enough damage to receive rewards from " + getName() + "!");
		}
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return MELEE_PRAYER_MULTIPLIER; // Boss is immune to melee prayer
	}

	@Override
	public double getMagePrayerMultiplier() {
		return MAGE_PRAYER_MULTIPLIER; // Takes more damage with mage prayer active
	}

	@Override
	public double getRangePrayerMultiplier() {
		return RANGE_PRAYER_MULTIPLIER; // Takes more damage with range prayer active
	}

	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();

		// Reset combat state
		resetWalkSteps();
		getCombat().removeTarget();

		// Reset player combat if applicable
		if (source instanceof Player) {
			Player killer = (Player) source;
			if (shouldResetPlayerCombat()) {
				killer.deathResetCombat();
			}
		}

		// Start death sequence
		setNextAnimation(null);
		scheduleDeathSequence(defs);
	}

	/**
	 * Schedule the death animation and drop processing
	 */
	private void scheduleDeathSequence(NPCCombatDefinitions defs) {
		WorldTasksManager.schedule(new WorldTask() {
			private int loop = 0;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					processCustomDrops();
					reset();
					finish();
					scheduleRespawn();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	/**
	 * Check if player combat should be reset on boss death
	 */
	private boolean shouldResetPlayerCombat() {
		// Check against no-reset combat list if it exists
		if (noResetCombat != null) {
			for (int noResetId : noResetCombat) {
				if (noResetId == getId()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Schedule boss respawn
	 */
	private void scheduleRespawn() {
		int respawnDelay = getCombatDefinitions().getRespawnDelay();

		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				try {
					new Sunfreet(getId(), getRespawnTile(), getMapAreaNameHash(), canBeAttackFromOutOfArea(),
							isSpawned());
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, respawnDelay);
	}

	/**
	 * Process custom drops for all eligible players
	 */
	private void processCustomDrops() {
		if (rewardList.isEmpty()) {
			return;
		}

		List<Player> eligiblePlayers = getEligiblePlayersForRewards();

		for (Player player : eligiblePlayers) {
			try {
				processPlayerRewards(player);
			} catch (Exception e) {
				Logger.handle(e);
				System.err.println("Error processing rewards for player: " + player.getUsername());
			}
		}

		// Clear reward list for next kill
		rewardList.clear();
	}

	/**
	 * Get list of players eligible for rewards (online and within range)
	 */
	private List<Player> getEligiblePlayersForRewards() {
		List<Player> eligible = new ArrayList<>();

		for (Player player : rewardList) {
			if (isPlayerEligibleForReward(player)) {
				eligible.add(player);
			}
		}

		return eligible;
	}

	/**
	 * Check if player is eligible for rewards
	 */
	private boolean isPlayerEligibleForReward(Player player) {
		return player != null && World.isOnline(player.getUsername()) && player.withinDistance(this, REWARD_DISTANCE)
				&& player.npcSunfreetDmg >= MINIMUM_DAMAGE_FOR_REWARD;
	}

	/**
	 * Process rewards for individual player
	 */
	private void processPlayerRewards(Player player) {
		try {
			// Update kill statistics
			increaseKillStatistics(player, getName());

			// Handle pet drops
			handlePetDrop(player, getName());

			// Process regular drops
			processRegularDrops(player);

			// Handle slayer task progress
			SlayerTask.onKill(player, this);

			// Handle contract progress
			ContractHandler.checkContract(player, getId(), this);

			// Notify player of successful kill
			player.sendMessage(Colors.green + "You have successfully defeated " + getName() + "!");

		} catch (Exception e) {
			Logger.handle(e);
			throw e; // Re-throw to be caught by caller
		}
	}

	/**
	 * Process regular drop table for player
	 */
	private void processRegularDrops(Player player) {
		Drop[] drops = NPCDrops.getDrops(getId());
		if (drops == null || drops.length == 0) {
			return;
		}

		List<Drop> possibleDrops = new ArrayList<>();

		for (Drop drop : drops) {
			if (shouldSkipDrop(player, drop)) {
				continue;
			}

			if (drop.getRate() == 100) {
				// Guaranteed drop
				sendDrop(player, drop);
			} else {
				// Chance-based drop
				if (rollForDrop(player, drop)) {
					possibleDrops.add(drop);
				}
			}
		}

		// Send one random drop from possible drops
		if (!possibleDrops.isEmpty()) {
			Drop selectedDrop = possibleDrops.get(Utils.getRandom(possibleDrops.size() - 1));
			sendDrop(player, selectedDrop);
		}
	}

	/**
	 * Check if drop should be skipped
	 */
	private boolean shouldSkipDrop(Player player, Drop drop) {
		// Skip clue scrolls if player already has one
		if (player.getTreasureTrails().isScroll(drop.getItemId())) {
			return player.getTreasureTrails().hasClueScrollItem();
		}
		return false;
	}

	/**
	 * Roll for chance-based drop
	 */
	private boolean rollForDrop(Player player, Drop drop) {
		double dropRate = drop.getRate();
		double playerDropRate = player.getDropRate();
		double finalRate = dropRate + playerDropRate;

		// Cap at 100% to prevent overflow
		if (finalRate > 100) {
			finalRate = 100;
		}

		double random = Utils.getRandomDouble(100);
		return random <= finalRate;
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTargets = new ArrayList<>();

		// Iterate through all map regions this NPC occupies
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();

			if (playerIndexes == null) {
				continue;
			}

			// Check each player in the region
			for (int playerIndex : playerIndexes) {
				Player player = World.getPlayers().get(playerIndex);

				if (isValidTarget(player)) {
					possibleTargets.add(player);
				}
			}
		}

		return possibleTargets;
	}

	/**
	 * Check if player is a valid target
	 */
	private boolean isValidTarget(Player player) {
		if (player == null || player.isDead() || player.hasFinished() || !player.isRunning()) {
			return false;
		}

		// Check distance
		if (!player.withinDistance(this, TARGET_DISTANCE)) {
			return false;
		}

		// Check multi-area combat rules
		if ((!isAtMultiArea() || !player.isAtMultiArea()) && player.getAttackedBy() != this
				&& player.getAttackedByDelay() > System.currentTimeMillis()) {
			return false;
		}

		// Check line of sight
		if (!clipedProjectile(player, false)) {
			return false;
		}

		return true;
	}
}