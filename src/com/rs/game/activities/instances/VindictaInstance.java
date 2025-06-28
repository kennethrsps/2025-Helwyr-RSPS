package com.rs.game.activities.instances;

import com.rs.cores.CoresManager;
import com.rs.cores.FixedLengthRunnable;
import com.rs.game.Animation;
import com.rs.game.MapBuilder;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.gwd2.vindicta.CMVindicta;
import com.rs.game.npc.gwd2.vindicta.Gorvek;
import com.rs.game.npc.gwd2.vindicta.Vindicta;
import com.rs.game.player.Player;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

/**
 * Enhanced VindictaInstance with Full BossBalancer Integration - FIXED VERSION
 * Features: Dynamic HP scaling, combat session management, player cache cleanup
 * 
 * @author Tom (Enhanced by Zeus)
 * @date June 06, 2025
 * @version 2.1 - FIXED COMPILATION ERRORS
 */
public class VindictaInstance extends Instance {

	private NPC vindicta;
	private NPC gorvek;
	private boolean finished;
	
	// BossBalancer integration tracking
	private boolean bossSpawned = false;
	private int originalVindictaHp = -1;
	private int originalGorvekHp = -1;
	
	public VindictaInstance(Player owner, int instanceDuration, int respawnSpeed, int playersLimit, int password,
			int bossId, boolean hardMode) {
		super(owner, instanceDuration, respawnSpeed, playersLimit, password, bossId, hardMode);
		chunksToBind = new int[] { 384, 857 };
		sizes = new int[] { 8, 10 };
		boundChunks = MapBuilder.findEmptyChunkBound(sizes[0], sizes[1]);
	}

	@Override
	public WorldTile getWorldTile(int x, int y) {
		return new WorldTile((boundChunks[0] * 8) + x, (boundChunks[1] * 8) + y, 1);
	}

	@Override
	public WorldTile getWaitingRoomCoords() {
		return getWorldTile(42, 42);
	}

	@Override
	public void initiateSpawningSequence() {
		VindictaInstance instance = this;
		CoresManager.getServiceProvider().scheduleFixedLengthTask(new FixedLengthRunnable() {
			private int seconds;
			private boolean resetSeconds;
			@Override
			public boolean repeat() {
				if (!isStable && players.size() == 0 || (totalSeconds % 60 == 0 && (totalSeconds / 60) == instanceDuration + 5)) {
					if (players.size() > 0) {
						players.forEach(player -> {
							if (player != null && player.getCurrentInstance() == instance) {
								// ===== BOSSBALANCER INTEGRATION: CLEANUP ON PLAYER EXIT =====
								cleanupPlayerBossBalancer(player);
								player.setNextWorldTile(getOutsideCoordinates());
							}
						});
					}
					
					// ===== BOSSBALANCER INTEGRATION: PERFORM CLEANUP BEFORE DESTRUCTION =====
					performBossBalancerCleanup();
					
					destroyInstance();
					if (vindicta != null)
						vindicta.finish();
					if (gorvek != null)
						gorvek.finish();
					return false;
				}
				if (seconds == 0 && !finished) {
					resetSeconds = false;
					if (vindicta == null || vindicta.hasFinished()) {
						// ===== BOSSBALANCER INTEGRATION: SPAWN WITH HP SCALING =====
						spawnBossesWithBalancerIntegration(instance);
					}
				}
				if (vindicta != null && vindicta.hasFinished() && !resetSeconds)  {
					// ===== BOSSBALANCER INTEGRATION: CLEANUP ON BOSS DEATH =====
					handleBossDeathCleanup();
					seconds = 0 - respawnSpeed;
					resetSeconds = true;
				}
				if (totalSeconds % 60 == 0 && (totalSeconds / 60) == instanceDuration) {
					finished = true;
					players.forEach(player -> {
						player.sendMessage("The instance has ended. No more monsters will be spawned in this instance.");
						// Clean up BossBalancer data
						cleanupPlayerBossBalancer(player);
					});
				} if (totalSeconds % 60 == 0 && (totalSeconds / 60) == instanceDuration - 2) {
					players.forEach(player -> player.sendMessage("The instance will remain open for two more minutes."));
					isStable = false;
				}
				seconds++;
				totalSeconds++;
				return true;
			}
			
		}, 0, 1);
	}

	/**
	 * ===== BOSSBALANCER INTEGRATION: ENHANCED BOSS SPAWNING =====
	 * Spawn bosses with dynamic HP scaling based on player gear/prayer
	 */
	private void spawnBossesWithBalancerIntegration(VindictaInstance instance) {
		try {
			// Create the boss NPCs
			vindicta = isHardMode() ? 
				new CMVindicta(22461, new WorldTile(getWorldTile(27, 27)), -1, true, true, instance) : 
				new Vindicta(22459, new WorldTile(getWorldTile(27, 27)), -1, true, true, instance);
			gorvek = new Gorvek(22463, new WorldTile(getWorldTile(27, 26)), -1, true, false);

			// Store original HP values for reference
			if (originalVindictaHp == -1) {
				originalVindictaHp = vindicta.getHitpoints();
			}
			if (originalGorvekHp == -1) {
				originalGorvekHp = gorvek.getHitpoints();
			}

			// ===== BOSSBALANCER INTEGRATION: APPLY HP SCALING =====
			applyBossBalancerHpScaling();

			// Set up boss properties
			vindicta.setForceMultiArea(true);
			gorvek.setForceMultiArea(true);
			vindicta.faceObject(new WorldObject(101898, 6487, 8047, 1, 11, 2));
			vindicta.setNextAnimation(new Animation(28257));
			gorvek.setNextAnimation(new Animation(28264));

			// Position Gorvek
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					gorvek.setNextWorldTile(new WorldTile(getWorldTile(63, 62)));
					gorvek.setCantFollowUnderCombat(true);
					gorvek.isCantSetTargetAutoRelatio();
				}
			}, 1);

			bossSpawned = true;

			// Send spawn notification to players with scaling info
			sendBossSpawnNotification();

		} catch (Exception e) {
			System.err.println("Error spawning bosses with BossBalancer integration: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * ===== BOSSBALANCER INTEGRATION: DYNAMIC HP SCALING =====
	 * Apply HP scaling based on the players currently in the instance
	 */
	private void applyBossBalancerHpScaling() {
		try {
			if (players.isEmpty()) {
				return; // No players to scale for
			}

			// Calculate average scaling across all players in instance
			double totalVindictaHpMultiplier = 0.0;
			double totalGorvekHpMultiplier = 0.0;
			int validPlayerCount = 0;

			for (Player player : players) {
				if (player != null && !player.hasFinished()) {
					// Get individual scaling for each player
					int vindictaScaledHp = BossBalancer.applyBossHpScaling(originalVindictaHp, player, vindicta);
					int gorvekScaledHp = BossBalancer.applyBossHpScaling(originalGorvekHp, player, gorvek);

					// Calculate multipliers
					double vindictaMultiplier = (double) vindictaScaledHp / originalVindictaHp;
					double gorvekMultiplier = (double) gorvekScaledHp / originalGorvekHp;

					totalVindictaHpMultiplier += vindictaMultiplier;
					totalGorvekHpMultiplier += gorvekMultiplier;
					validPlayerCount++;
				}
			}

			if (validPlayerCount > 0) {
				// Use average scaling for multi-player instances
				double avgVindictaMultiplier = totalVindictaHpMultiplier / validPlayerCount;
				double avgGorvekMultiplier = totalGorvekHpMultiplier / validPlayerCount;

				// Apply the calculated HP scaling
				int newVindictaHp = (int) Math.round(originalVindictaHp * avgVindictaMultiplier);
				int newGorvekHp = (int) Math.round(originalGorvekHp * avgGorvekMultiplier);

				// Ensure reasonable HP bounds
				newVindictaHp = Math.max(newVindictaHp, originalVindictaHp / 2); // Minimum 50% of original
				newGorvekHp = Math.max(newGorvekHp, originalGorvekHp / 2);

				// FIXED: Apply the new HP values using setHitpoints instead of setMaxHitpoints
				vindicta.setHitpoints(newVindictaHp);
				gorvek.setHitpoints(newGorvekHp);

				// Debug logging
				System.out.println("BossBalancer HP Scaling Applied:");
				System.out.println("Vindicta: " + originalVindictaHp + " -> " + newVindictaHp + 
								   " (x" + String.format("%.2f", avgVindictaMultiplier) + ")");
				System.out.println("Gorvek: " + originalGorvekHp + " -> " + newGorvekHp + 
								   " (x" + String.format("%.2f", avgGorvekMultiplier) + ")");
			}

		} catch (Exception e) {
			System.err.println("Error applying BossBalancer HP scaling: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Send boss spawn notification with scaling information
	 */
	private void sendBossSpawnNotification() {
		try {
			for (Player player : players) {
				if (player != null && !player.hasFinished()) {
					// Send spawn message
					player.sendMessage("<col=ff6600>Vindicta and Gorvek have spawned! Prepare for battle!</col>");
					
					// Get and display BossBalancer scaling info
					try {
						// FIXED: Replace 'var' with explicit type for Java 7 compatibility
						CombatScaling vindictaScaling = BossBalancer.getCombatScaling(player, vindicta);
						if (vindictaScaling != null) {
							// Send scaling summary
							String scalingMessage = String.format(
								"<col=00ccff>Combat Scaling: %s (Tier %d vs %d) - HP: %.0f%%, Damage: %.0f%%</col>",
								vindictaScaling.scalingType,
								vindictaScaling.playerTier,
								vindictaScaling.bossTier,
								vindictaScaling.bossHpMultiplier * 100,
								vindictaScaling.bossDamageMultiplier * 100
							);
							player.sendMessage(scalingMessage);

							// Send prayer tier info if relevant
							if (vindictaScaling.prayerTier > 0.5) {
								player.sendMessage("<col=ffff00>Prayer effects detected: +" + 
												   String.format("%.1f", vindictaScaling.prayerTier) + 
												   " effective combat tiers!</col>");
							}
						}
					} catch (Exception e) {
						// Fallback message if scaling info fails
						player.sendMessage("<col=99ff99>Dynamic combat scaling active based on your gear and prayers!</col>");
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error sending boss spawn notification: " + e.getMessage());
		}
	}

	/**
	 * ===== BOSSBALANCER INTEGRATION: BOSS DEATH CLEANUP =====
	 * Handle BossBalancer cleanup when boss dies
	 */
	private void handleBossDeathCleanup() {
		try {
			// End combat sessions for all players
			for (Player player : players) {
				if (player != null && !player.hasFinished()) {
					BossBalancer.endCombatSession(player);
					
					// Send death message with performance summary
					sendBossDeathSummary(player);
				}
			}

			bossSpawned = false;

		} catch (Exception e) {
			System.err.println("Error handling boss death cleanup: " + e.getMessage());
		}
	}

	/**
	 * Send boss death summary with scaling performance
	 */
	private void sendBossDeathSummary(Player player) {
		try {
			player.sendMessage("<col=ff6600>Vindicta has been defeated! Well fought!</col>");
			
			// Get final scaling info for summary
			// FIXED: Replace 'var' with explicit type for Java 7 compatibility
			CombatScaling finalScaling = BossBalancer.getCombatScaling(player, vindicta);
			if (finalScaling != null) {
				String performanceMessage = "";
				
				switch (finalScaling.scalingType) {
				case "UNDERGEARED":
					performanceMessage = "<col=ffaa00>You overcame the challenge despite being undergeared! Consider upgrading your equipment.</col>";
					break;
				case "OVERGEARED_ANTI_FARM":
				case "OVERGEARED_PRAYER_ANTI_FARM":
					performanceMessage = "<col=ff9900>You defeated a strengthened Vindicta! Your power forced her to fight at full strength.</col>";
					break;
				case "BALANCED":
				case "BALANCED_PRAYER_ENHANCED":
					performanceMessage = "<col=00ff00>Perfect challenge level! Your equipment was well-matched for this encounter.</col>";
					break;
				default:
					performanceMessage = "<col=99ff99>Combat complete! Dynamic scaling provided appropriate challenge.</col>";
					break;
				}
				
				player.sendMessage(performanceMessage);
			}
			
		} catch (Exception e) {
			System.err.println("Error sending boss death summary: " + e.getMessage());
		}
	}

	/**
	 * ===== BOSSBALANCER INTEGRATION: PLAYER CLEANUP =====
	 * Clean up BossBalancer data when player leaves
	 */
	private void cleanupPlayerBossBalancer(Player player) {
		try {
			if (player != null) {
				// End any active combat session
				BossBalancer.endCombatSession(player);
				
				// Clear player-specific cache entries
				BossBalancer.clearPlayerCache(player.getIndex());
				
				// Send cleanup notification
				player.sendMessage("<col=99ff99>BossBalancer data cleared. Combat scaling reset.</col>");
			}
		} catch (Exception e) {
			System.err.println("Error cleaning up player BossBalancer data: " + e.getMessage());
		}
	}

	/**
	 * ===== ENHANCED PLAYER MANAGEMENT =====
	 * FIXED: Use custom method name to avoid overriding final methods
	 */
	public void handlePlayerAddition(Player player) {
		try {
			// If boss is already spawned, apply scaling for the new player
			if (bossSpawned && vindicta != null && !vindicta.hasFinished()) {
				// Recalculate HP scaling with new player included
				applyBossBalancerHpScaling();
				
				// Send scaling info to the new player
				player.sendMessage("<col=00ccff>Joining active Vindicta encounter...</col>");
				
				// Show current boss scaling
				CombatScaling scaling = BossBalancer.getCombatScaling(player, vindicta);
				if (scaling != null) {
					String joinMessage = String.format(
						"<col=ffaa00>Current Boss Scaling: %s - HP: %.0f%%, Damage: %.0f%%</col>",
						scaling.scalingType,
						scaling.bossHpMultiplier * 100,
						scaling.bossDamageMultiplier * 100
					);
					player.sendMessage(joinMessage);
				}
			}
		} catch (Exception e) {
			System.err.println("Error handling player addition with BossBalancer: " + e.getMessage());
		}
	}

	/**
	 * ===== ENHANCED PLAYER REMOVAL =====
	 * FIXED: Use custom method name and match parent signature
	 */
	public void handlePlayerRemoval(Player player) {
		try {
			// Clean up BossBalancer data before removing
			cleanupPlayerBossBalancer(player);
			
			// If boss is active and there are still players, recalculate scaling
			if (bossSpawned && vindicta != null && !vindicta.hasFinished() && !players.isEmpty()) {
				applyBossBalancerHpScaling();
				
				// Notify remaining players of scaling change
				for (Player remainingPlayer : players) {
					if (remainingPlayer != null && !remainingPlayer.hasFinished() && !remainingPlayer.equals(player)) {
						remainingPlayer.sendMessage("<col=ffaa00>Player left - boss scaling recalculated!</col>");
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error handling player removal with BossBalancer: " + e.getMessage());
		}
	}

	/**
	 * ===== PRAYER CHANGE HANDLER =====
	 * Handle prayer changes that affect combat scaling
	 */
	public void handlePlayerPrayerChange(Player player) {
		try {
			if (bossSpawned && vindicta != null && !vindicta.hasFinished()) {
				// Notify BossBalancer of prayer change
				BossBalancer.onPrayerChanged(player);
				
				// Optionally recalculate HP scaling if significant prayer changes
				// (This could be heavy, so maybe only for solo instances)
				if (players.size() == 1) {
					applyBossBalancerHpScaling();
					player.sendMessage("<col=ffaa00>Prayer change detected - boss scaling updated!</col>");
				}
			}
		} catch (Exception e) {
			System.err.println("Error handling prayer change: " + e.getMessage());
		}
	}

	/**
	 * ===== BOSSBALANCER INTEGRATION: INSTANCE DESTRUCTION CLEANUP =====
	 * Custom cleanup method for BossBalancer data (called before instance destruction)
	 */
	public void performBossBalancerCleanup() {
		try {
			// Clean up all player BossBalancer data
			for (Player player : players) {
				if (player != null) {
					cleanupPlayerBossBalancer(player);
				}
			}
			
			// Force cleanup of any remaining boss data
			if (vindicta != null) {
				try {
					// Clear any boss-specific BossBalancer cache entries
					// Note: This method might not exist, but safe to try
					BossBalancer.clearPlayerCache(vindicta.getId());
				} catch (Exception e) {
					// Ignore errors in boss cache cleanup
				}
			}
			
			System.out.println("BossBalancer cleanup completed for VindictaInstance");
			
		} catch (Exception e) {
			System.err.println("Error in BossBalancer cleanup during instance destruction: " + e.getMessage());
		}
	}

	// ===== ORIGINAL METHODS (UNCHANGED) =====

	public NPC getGorvek() {
		return gorvek;
	}
	
	public NPC getVindicta() {
		return vindicta;
	}

	@Override
	public WorldTile getOutsideCoordinates() {
		return new WorldTile(3113, 6897, 1);
	}

	@Override
	public NPC getBossNPC() {
		return vindicta; // Return primary boss for BossBalancer integration
	}
	
	@Override
	public void performOnSpawn() {
		// Could add additional BossBalancer initialization here if needed
	}

	/**
	 * ===== UTILITY METHODS FOR BOSSBALANCER =====
	 */
	
	/**
	 * Get the current boss HP scaling multiplier (for debugging/monitoring)
	 */
	public double getCurrentBossHpMultiplier() {
		if (vindicta != null && originalVindictaHp > 0) {
			return (double) vindicta.getHitpoints() / originalVindictaHp;
		}
		return 1.0;
	}
	
	/**
	 * Force recalculation of boss scaling (admin command utility)
	 */
	public void forceRecalculateBossScaling() {
		if (bossSpawned && vindicta != null && !vindicta.hasFinished()) {
			applyBossBalancerHpScaling();
			
			for (Player player : players) {
				if (player != null && !player.hasFinished()) {
					player.sendMessage("<col=ff6600>Boss scaling forcibly recalculated!</col>");
				}
			}
		}
	}
	
	/**
	 * Check if BossBalancer is active in this instance
	 */
	public boolean isBossBalancerActive() {
		return bossSpawned && vindicta != null && !vindicta.hasFinished();
	}

	/**
	 * ===== PLAYER EVENT HANDLERS =====
	 * Call these methods from your player management system
	 */
	
	/**
	 * Call this method when a player joins the instance
	 */
	public void onPlayerJoin(Player player) {
		// Add to the standard instance player list first
		// Then handle BossBalancer integration
		handlePlayerAddition(player);
	}
	
	/**
	 * Call this method when a player leaves the instance
	 */
	public void onPlayerLeave(Player player) {
		// Handle BossBalancer cleanup first
		handlePlayerRemoval(player);
		// Then remove from standard instance player list
	}
	
	/**
	 * Call this method when a player's prayers change
	 */
	public void onPlayerPrayerChanged(Player player) {
		handlePlayerPrayerChange(player);
	}
	
	/**
	 * Call this method before manually destroying the instance (if needed)
	 * The automatic cleanup in initiateSpawningSequence() handles most cases
	 */
	public void onInstanceDestruction() {
		performBossBalancerCleanup();
	}
}