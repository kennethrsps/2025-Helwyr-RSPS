package com.rs.game.npc.Sotapana;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
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
 * Sotapana NPC Class - Epic Boss with Dynamic Mechanics
 * 
 * This class manages the boss entity itself while the combat script
 * handles attack patterns and damage calculations.
 * 
 * EPIC FEATURES:
 * - 🔥 Health-based phase transitions with announcements
 * - ⚡ Dynamic combat messages and blade-themed taunts
 * - 💎 Enhanced drop mechanics with excitement
 * - 🌟 Spectacular death and spawn sequences
 * - 🛡️ Adaptive prayer resistance based on combat phase
 * 
 * @author ryan
 */
@SuppressWarnings("serial")
public class Sotapana extends NPC {

	private List<Player> rewardList = new ArrayList<Player>();
	private boolean hasEnraged = false; // Track if berserker mode was announced
	private boolean hasDied = false;    // Prevent multiple death sequences

	public Sotapana(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setLureDelay(5000);
		setForceTargetDistance(64);
		setForceFollowClose(false);
		setNoDistanceCheck(true);
		setIntelligentRouteFinder(false);
		this.setForceAgressive(false);
		setCantDoDefenceEmote(true);
		
		// Epic spawn announcement for Sotapana
		announceSpawn();
	}

	/**
	 * Epic spawn announcement to get players excited
	 */
	private void announceSpawn() {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				// Epic spawn message to nearby players
				for (Player player : World.getPlayers()) {
					if (player != null && player.withinDistance(Sotapana.this, 30)) {
						player.sendMessage("⚔️ The deadly blade master Sotapana has appeared!", true);
						player.sendMessage("🌟 Prepare to face a legendary swordsman!", true);
					}
				}
				setNextForceTalk(new ForceTalk("Who dares challenge my blade?"));
				stop();
			}
		}, 2);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		
		// Heal boss when no players are around (prevents camping)
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && getPossibleTargets().isEmpty()) {
			heal(100000); // Full heal when no targets
			hasEnraged = false; // Reset rage state
		}
		
		// Dynamic health-based phase transitions
		checkHealthPhases();
	}
	
	/**
	 * Check health and trigger epic phase transitions
	 */
	private void checkHealthPhases() {
		int healthPercent = (getHitpoints() * 100) / getMaxHitpoints();
		
		// BERSERKER PHASE at 50% HP
		if (healthPercent <= 50 && !hasEnraged && !getPossibleTargets().isEmpty()) {
			hasEnraged = true;
			triggerBerserkerPhase();
		}
		
		// Low health desperate phase at 25%
		if (healthPercent <= 25 && Utils.random(50) == 0) {
			setNextForceTalk(new ForceTalk("My blade shall not be dulled!"));
		}
		
		// Random combat taunts during battle (blade-themed)
		if (!getPossibleTargets().isEmpty() && Utils.random(100) == 0) {
			String[] taunts = {
				"Your skills are lacking!",
				"Face the master's blade!",
				"You cannot match my technique!",
				"Witness true swordsmanship!",
				"My blade thirsts for victory!"
			};
			setNextForceTalk(new ForceTalk(taunts[Utils.random(taunts.length)]));
		}
	}
	
	/**
	 * Epic berserker phase transition
	 */
	private void triggerBerserkerPhase() {
		// Dramatic phase transition
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				// Announce to all nearby players
				for (Player player : World.getPlayers()) {
					if (player != null && player.withinDistance(Sotapana.this, 30)) {
						player.sendMessage("", true);
						player.sendMessage("🔥🔥🔥 SOTAPANA ENTERS BLADE FRENZY! 🔥🔥🔥", true);
						player.sendMessage("⚡ The blade master's attacks become lightning fast!", true);
						player.sendMessage("", true);
					}
				}
				
				// Epic visual effect
				setNextGraphics(new Graphics(1577)); // Berserker graphics
				setNextForceTalk(new ForceTalk("NOW YOU FACE MY TRUE POWER!"));
				stop();
			}
		}, 1);
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		super.handleIngoingHit(hit);
		
		if (hit.getSource() instanceof Player) {
			Player p = (Player) hit.getSource();
			
			// Initialize damage tracking for new attacker
			if (p.npcSotapana == null) {
				p.npcSotapana = this;
				p.npcSotapanaDmg = hit.getDamage();
			}
			// Reset damage if attacking different Sotapana instance
			else if (p.npcSotapana != this) {
				p.npcSotapana = this;
				p.npcSotapanaDmg = hit.getDamage();
			}
			// Add to existing damage total
			else if (p.npcSotapana == this) {
				p.npcSotapanaDmg += hit.getDamage();
			}
			
			// Add to reward list once minimum damage threshold is met
			if (!rewardList.contains(p) && p.npcSotapanaDmg >= 1) {
				rewardList.add(p);
				// Optional: Notify player they qualify for drops
				// p.sm("[" + this.getName() + "] " + Colors.GREEN + "You dealt enough damage to receive rewards!");
			}
		}
	}

	@Override
	public double getMeleePrayerMultiplier() {
		// Dynamic prayer resistance based on health (blade master's focus)
		int healthPercent = (getHitpoints() * 100) / getMaxHitpoints();
		
		if (healthPercent <= 25) {
			return 0.80; // 20% reduction when nearly defeated (desperation)
		} else if (healthPercent <= 50) {
			return 0.70; // 30% reduction in blade frenzy mode
		} else {
			return 0.60; // 40% reduction at full strength
		}
	}

	@Override
	public double getMagePrayerMultiplier() {
		return getMeleePrayerMultiplier(); // Same scaling for all combat types
	}

	@Override
	public double getRangePrayerMultiplier() {
		return getMeleePrayerMultiplier(); // Same scaling for all combat types
	}

	@Override
	public void sendDeath(Entity source) {
		if (hasDied) return; // Prevent multiple death sequences
		hasDied = true;
		
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		
		// Epic death announcement
		epicDeathSequence(source);
		
		// Reset combat for attacking player
		if (source instanceof Player) {
			boolean reset = true;
			for (int i : noResetCombat) {
				if (i == id) {
					reset = false;
					break;
				}
			}
			if (reset)
				((Player) source).deathResetCombat();
		}
		
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					customDrop(); // Handle drop distribution with epic messages
					reset();
					finish();
					startSpawn();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
	
	/**
	 * Epic death sequence with dramatic flair
	 */
	private void epicDeathSequence(Entity source) {
		WorldTasksManager.schedule(new WorldTask() {
			int phase = 0;
			
			@Override
			public void run() {
				switch (phase) {
					case 0:
						// Final blade master's honor
						setNextForceTalk(new ForceTalk("You... have bested my blade..."));
						setNextGraphics(new Graphics(1580)); // Phantom effect
						break;
						
					case 1:
						// Announce defeat to nearby players
						for (Player player : World.getPlayers()) {
							if (player != null && player.withinDistance(Sotapana.this, 30)) {
								player.sendMessage("", true);
								player.sendMessage("🏆 SOTAPANA HAS BEEN DEFEATED! 🏆", true);
								player.sendMessage("⚡ The blade master acknowledges your skill!", true);
								player.sendMessage("", true);
							}
						}
						break;
						
					case 2:
						// Honorable defeat words
						setNextForceTalk(new ForceTalk("Your technique... is worthy of respect..."));
						break;
						
					case 3:
						// Victory announcement for killer
						if (source instanceof Player) {
							Player killer = (Player) source;
							killer.sendMessage("🌟 You have defeated the legendary Sotapana!", true);
							killer.sendMessage("💎 Claim the rewards of your victory!", true);
						}
						stop();
						break;
				}
				phase++;
			}
		}, 1, 2); // 2 tick intervals for dramatic pacing
	}

	protected void startSpawn() {
		int respawnDelay = getCombatDefinitions().getRespawnDelay();
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				try {
					new Sotapana(id, getRespawnTile(), getMapAreaNameHash(), canBeAttackFromOutOfArea(), isSpawned());
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, respawnDelay);
	}

	protected void customDrop() {
		if (!rewardList.isEmpty()) {
			// Epic loot announcement
			for (Player player : World.getPlayers()) {
				if (player != null && player.withinDistance(this, 30)) {
					player.sendMessage("💰 The defeated blade master drops legendary rewards!", true);
				}
			}
			
			for (Player p : rewardList) {
				if (World.isOnline(p.getUsername()) && p.withinDistance(this, 20)) {
					processOriginalDrops(p);
				}
			}
		}
		rewardList.clear();
	}

	private void processOriginalDrops(Player killer) {
		try {
			increaseKillStatistics(killer, getName());
			handlePetDrop(killer, getName());
			
			Drop[] drops = NPCDrops.getDrops(id);
			Drop[] possibleDrops = new Drop[drops.length];
			int possibleDropsCount = 0;
			boolean gotRareDrop = false;
			
			for (Drop drop : drops) {
				// Handle clue scroll special case
				if (killer.getTreasureTrails().isScroll(drop.getItemId())) {
					if (killer.getTreasureTrails().hasClueScrollItem())
						continue;
				}
				
				// Guaranteed drops (100% rate)
				if (drop.getRate() == 100) {
					sendDrop(killer, drop);
				} else {
					// Calculate drop chance with player's drop rate bonus
					double rate = drop.getRate();
					double random = Utils.getRandomDouble(100);
					rate += killer.getDropRate();
					
					if (rate > 100)
						continue;
						
					if (random <= rate) {
						possibleDrops[possibleDropsCount++] = drop;
						// Check if this is a rare drop (< 10% chance)
						if (drop.getRate() < 10) {
							gotRareDrop = true;
						}
					}
				}
			}
			
			// Award one random drop from possible drops
			if (possibleDropsCount > 0) {
				Drop selectedDrop = possibleDrops[Utils.getRandom(possibleDropsCount - 1)];
				sendDrop(killer, selectedDrop);
				
				// Epic rare drop celebration
				if (gotRareDrop) {
					celebrateRareDrop(killer, selectedDrop);
				}
			}
			
			// Epic completion message
			killer.sendMessage("🎉 You have triumphed over the blade master Sotapana!", true);
			killer.sendMessage("⚔️ Your combat mastery has been proven!", true);
			
			// Handle slayer and contract progress
			SlayerTask.onKill(killer, this);
			ContractHandler.checkContract(killer, id, this);
			
		} catch (Exception | Error e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Celebrate rare drops with epic messages
	 */
	private void celebrateRareDrop(Player killer, Drop rareDrop) {
		// Announce rare drop to nearby players
		for (Player player : World.getPlayers()) {
			if (player != null && player.withinDistance(this, 30)) {
				if (player == killer) {
					player.sendMessage("", true);
					player.sendMessage("🌟✨ LEGENDARY DROP ALERT! ✨🌟", true);
					player.sendMessage("🎁 Sotapana has blessed you with a rare treasure!", true);
					player.sendMessage("", true);
				} else {
					player.sendMessage("⭐ " + killer.getDisplayName() + " received a legendary drop from Sotapana!", true);
				}
			}
		}
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playerIndexes != null) {
				for (int npcIndex : playerIndexes) {
					Player player = World.getPlayers().get(npcIndex);
					
					// Validate player and combat conditions
					if (player == null || player.isDead() || player.hasFinished() || !player.isRunning()
							|| !player.withinDistance(this, 64)
							|| ((!isAtMultiArea() || !player.isAtMultiArea()) && player.getAttackedBy() != this
									&& player.getAttackedByDelay() > System.currentTimeMillis())
							|| !clipedProjectile(player, false))
						continue;
						
					possibleTarget.add(player);
				}
			}
		}
		return possibleTarget;
	}
}