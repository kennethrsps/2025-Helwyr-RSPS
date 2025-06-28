package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.godwars.zaros.Nex.NexPhase;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.cutscenes.NexCutScene;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Nex Angel Combat System with BossBalancer Integration and Boss Guidance
 * Features: Dynamic damage scaling, boss guidance messages, enhanced Zaros angel mechanics
 * 
 * @author Zeus (Enhanced by Zeus from Kingkenobi's original)
 * @date June 03, 2025
 * @version 3.0 - Enhanced with BossBalancer Integration and Boss Guidance System
 */
public class NexAngelCombat extends CombatScript {

	// Boss guidance message timers (prevent spam)
	private static final long GUIDANCE_COOLDOWN = 40000; // 40 seconds
	private long lastGuidanceTime = 0;
	private long lastMechanicWarning = 0;
	
	// Teleportation mechanics make safespot detection different
	private static final int MAX_SAFESPOT_DISTANCE = 20; // Angel has teleport abilities
	private static final int MIN_ENGAGEMENT_DISTANCE = 2;
	
	// Boss tier detection and guidance
	private int detectedTier = -1;
	private boolean guidanceSystemActive = true;
	private int attackCounter = 0;
	private int teleportCounter = 0;

	@Override
	public Object[] getKeys() {
		return new Object[] { 24004 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		
		// Initialize boss guidance and balancer integration
		initializeBossGuidance(npc, target);
		
		// Enhanced safespot detection (lenient due to teleport mechanics)
		checkAndPreventSafespotExploitation(npc, target);
		
		// Increment attack counter for varied guidance
		attackCounter++;
		
		// Attack selection with enhanced mechanics
		if (npc.withinDistance(target, npc.getSize())) {
			// Close range attack selection
			switch (Utils.random(10)) {
			case 1:
				sendMechanicWarning(npc, "Blood sacrifice magic!");
				enhancedMageAttack(npc, target);
				break;
			case 2:
				sendMechanicWarning(npc, "Zaros glory strikes!");
				enhancedRangeAttack(npc, target);
				break;
			case 3:
				sendMechanicWarning(npc, "Raw power unleashed!");
				enhancedMageAttack2(npc, target);
				break;
			case 4:
				sendMechanicWarning(npc, "Shadow devastation!");
				enhancedAoeAttack(npc, target);
				break;
			default:
				enhancedMeleeAttack(npc, target);
				break;
			}
		} else {
			// Long range attack selection
			switch (Utils.random(5)) {
			case 0:
			case 1:
				sendMechanicWarning(npc, "Distant Zaros strike!");
				enhancedRangeAttack(npc, target);
				break;
			case 2:
				sendMechanicWarning(npc, "Magic drain incoming!");
				enhancedMageAttack2(npc, target);
				break;
			case 3:
				sendMechanicWarning(npc, "Area shadow attack!");
				enhancedAoeAttack(npc, target);
				break;
			case 4:
				enhancedMageAttack(npc, target);
				break;
			default:
				enhancedMageAttack(npc, target);
				break;
			}
		}
		
		// Provide periodic guidance
		sendPeriodicGuidance(npc, target);
		
		return defs.getAttackDelay();
	}

	/**
	 * Initialize boss guidance system and detect tier
	 */
	private void initializeBossGuidance(NPC npc, Entity target) {
		if (detectedTier == -1) {
			// Auto-detect boss tier based on combat definitions
			NPCCombatDefinitions combatDefs = npc.getCombatDefinitions();
			if (combatDefs != null) {
				detectedTier = estimateBossTier(combatDefs.getHitpoints(), combatDefs.getMaxHit());
				sendWelcomeGuidance(npc, target);
			}
		}
	}

	/**
	 * Send welcome guidance when boss is first engaged
	 */
	private void sendWelcomeGuidance(NPC npc, Entity target) {
		if (!canSendGuidance()) return;
		
		if (target instanceof Player) {
			String tierName = getTierName(detectedTier);
			
			// Welcome message with tier information and Zaros theme
			npc.setNextForceTalk(new ForceTalk("I am the " + tierName + " Angel of Zaros! Prepare for divine judgment!"));
			
			WorldTasksManager.schedule(new WorldTask() {
				private int tick = 0;
				@Override
				public void run() {
					switch(tick) {
					case 2:
						npc.setNextForceTalk(new ForceTalk("My teleportation knows no bounds!"));
						break;
					case 4:
						npc.setNextForceTalk(new ForceTalk("Blood magic sustains my power!"));
						break;
					case 6:
						npc.setNextForceTalk(new ForceTalk("Your skills shall be drained before me!"));
						stop();
						break;
					}
					tick++;
				}
			}, 3, 2);
			
			lastGuidanceTime = System.currentTimeMillis();
		}
	}

	/**
	 * Enhanced blood sacrifice magic attack with healing
	 */
	public void enhancedMageAttack(NPC npc, Entity target) {
		for (Entity t : npc.getPossibleTargets()) {
			npc.setNextForceTalk(new ForceTalk("Taste the true power of a blood sacrifice!"));
			npc.setNextAnimation(new Animation(17414));
			World.sendGraphics(npc, new Graphics(5028), t);
			
			// Use BossBalancer damage calculation
			int damage = calculateBalancedDamage(npc, NPCCombatDefinitions.MAGE, 1.0, 1.4);
			delayHit(npc, 0, t, getMagicHit(npc, damage));
			target.setNextGraphics(new Graphics(376));
			
			// Enhanced healing based on damage and tier
			int healAmount = (damage / 10) + (detectedTier * 2);
			npc.heal(healAmount);
			
			// Enhanced teleportation chance with guidance
			if (Utils.getRandom(8) == 0) {
				performEnhancedTeleportation(npc, target, "Blood magic teleportation!");
			}
			
			// Provide guidance about healing mechanic
			if (t instanceof Player && Utils.random(4) == 0) {
				((Player) t).sendMessage("Angel heals from blood magic! Reduce damage taken to limit healing!");
			}
		}
	}

	/**
	 * Enhanced raw power magic attack with stat drain and freeze
	 */
	public void enhancedMageAttack2(NPC npc, Entity target) {
		npc.setNextForceTalk(new ForceTalk("WITNESS THE RAW POWER!"));
		npc.setNextAnimation(new Animation(17414));
		
		for (Entity t : npc.getPossibleTargets()) {
			World.sendGraphics(npc, new Graphics(369), t);
			World.sendGraphics(npc, new Graphics(3375), npc);
			
			// Use BossBalancer damage calculation
			int damage = calculateBalancedDamage(npc, NPCCombatDefinitions.MAGE, 1.3, 1.8);
			delayHit(npc, 2, t, getMagicHit(npc, damage));
			
			if (t instanceof Player) {
				Player targetPlayer = (Player) t;
				
				// Enhanced magic level drain based on tier
				int currentLevel = targetPlayer.getSkills().getLevel(Skills.MAGIC);
				int drainAmount = 5 + (detectedTier / 2);
				targetPlayer.getSkills().set(Skills.MAGIC, 
					currentLevel < drainAmount ? 0 : currentLevel - drainAmount);
				
				// Enhanced freeze duration
				int delay = 5 + Utils.random(5) + (detectedTier / 3);
				t.addFreezeDelay(delay * 300, true);
				
				// Guidance about stat drain and freeze
				targetPlayer.sendMessage("Angel drains your magic level and freezes you! Restore potions and freedom abilities help!");
			}
			
			// Enhanced teleportation chance
			if (Utils.getRandom(5) == 0) {
				performEnhancedTeleportation(npc, target, "Raw power teleportation!");
			}
		}
	}

	/**
	 * Enhanced ranged attack with Zaros glory theme
	 */
	public void enhancedRangeAttack(NPC npc, Entity target) {
		npc.setNextForceTalk(new ForceTalk("For the glory of ZAROS!"));
		npc.setNextAnimation(new Animation(17413));
		
		// Use BossBalancer damage calculation
		int damage = calculateBalancedDamage(npc, NPCCombatDefinitions.RANGE, 1.0, 1.5);
		World.sendProjectile(npc, target, 5326, 28, 16, 35, 20, 16, 0);
		delayHit(npc, 1, target, getRangeHit(npc, damage));
		
		// Enhanced teleportation chance with guidance
		if (Utils.getRandom(8) == 0) {
			performEnhancedTeleportation(npc, target, "Zaros glory teleportation!");
		}
		
		// Provide tactical guidance
		if (target instanceof Player && Utils.random(5) == 0) {
			((Player) target).sendMessage("Angel's Zaros projectiles are precise! Ranged defense helps!");
		}
	}

	/**
	 * Enhanced melee attack with attack level drain
	 */
	public void enhancedMeleeAttack(NPC npc, Entity target) {
		World.sendGraphics(npc, new Graphics(5014), target);
		npc.setNextAnimation(new Animation(17453));
		
		// Use BossBalancer damage calculation
		int damage = calculateBalancedDamage(npc, NPCCombatDefinitions.MELEE, 1.1, 1.6);
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		
		if (target instanceof Player) {
			Player targetPlayer = (Player) target;
			
			// Enhanced attack level drain based on tier
			int currentLevel = targetPlayer.getSkills().getLevel(Skills.ATTACK);
			int drainAmount = 7 + (detectedTier / 2);
			targetPlayer.getSkills().set(Skills.ATTACK, 
				currentLevel < drainAmount ? 0 : currentLevel - drainAmount);
			
			// Guidance about stat drain
			targetPlayer.sendMessage("Angel drains your attack level! Restore potions recommended!");
		}
		
		// Occasional melee guidance
		if (target instanceof Player && Utils.random(6) == 0) {
			((Player) target).sendMessage("Angel's divine strength is overwhelming! Melee protection advised!");
		}
	}

	/**
	 * Enhanced AOE shadow attack
	 */
	public void enhancedAoeAttack(NPC npc, Entity target) {
		npc.setNextForceTalk(new ForceTalk("Fear the shadow of Zaros!"));
		
		for (Entity t : npc.getPossibleTargets()) {
			npc.setNextAnimation(new Animation(17407));
			npc.setNextGraphics(new Graphics(3362));
			final WorldTile center = new WorldTile(t);
			World.sendGraphics(npc, new Graphics(383), center);
			
			WorldTasksManager.schedule(new WorldTask() {
				int count = 0;

				@Override
				public void run() {
					for (Player player : World.getPlayers()) {
						if (player == null || player.isDead() || player.hasFinished())
							continue;
						
						if (player.withinDistance(center, 1)) {
							// Use balanced damage for AOE
							int aoeDamage = calculateBalancedDamage(npc, NPCCombatDefinitions.MAGE, 0.7, 1.1);
							delayHit(npc, 0, player, new Hit(npc, aoeDamage, HitLook.REGULAR_DAMAGE));
							player.sendMessage("Zaros shadow engulfs you!");
						}
					}
					
					if (count++ == 10) {
						stop();
						return;
					}
				}
			}, 0, 0);
		}
		
		// AOE guidance
		if (target instanceof Player && Utils.random(3) == 0) {
			((Player) target).sendMessage("Angel's shadow AOE! Move away from targeted areas!");
		}
	}

	/**
	 * Enhanced teleportation mechanic with better guidance
	 */
	private void performEnhancedTeleportation(NPC npc, Entity target, String teleportType) {
		npc.setNextForceTalk(new ForceTalk("There is NO ESCAPE!"));
		teleportCounter++;
		
		WorldTile teleTile = npc;
		for (int trycount = 0; trycount < 3; trycount++) {
			teleTile = new WorldTile(target, 3);
			if (World.canMoveNPC(target.getPlane(), teleTile.getX(), teleTile.getY(), target.getSize()))
				continue;
		}
		
		if (World.canMoveNPC(npc.getPlane(), teleTile.getX(), teleTile.getY(), npc.getSize())) {
			// Different graphics based on teleport type
			if (teleportType.contains("Blood")) {
				npc.setNextGraphics(new Graphics(5019));
			} else if (teleportType.contains("Raw")) {
				npc.setNextGraphics(new Graphics(3607));
			} else {
				npc.setNextGraphics(new Graphics(5019));
			}
			
			npc.setNextWorldTile(teleTile);
			
			// Send guidance about teleportation
			if (target instanceof Player) {
				Player player = (Player) target;
				player.sendMessage("Angel teleports close to you! " + teleportType + " Be ready for follow-up attacks!");
				
				// Every 3rd teleport, provide strategic advice
				if (teleportCounter % 3 == 0) {
					player.sendMessage("Angel teleports frequently! Stay mobile and use area attacks!");
				}
			}
		}
	}

	/**
	 * Enhanced safespot detection (lenient due to teleport mechanics)
	 */
	private void checkAndPreventSafespotExploitation(NPC npc, Entity target) {
		if (!(target instanceof Player)) return;
		
		Player player = (Player) target;
		int distance = player.getDistance(npc);
		
		// More lenient since angel has teleportation abilities
		if (distance > MAX_SAFESPOT_DISTANCE) {
			if (System.currentTimeMillis() - lastGuidanceTime > GUIDANCE_COOLDOWN) {
				npc.setNextForceTalk(new ForceTalk("Divine judgment reaches all!"));
				player.sendMessage("Angel's reach is infinite! Move within " + MAX_SAFESPOT_DISTANCE + " tiles!");
				lastGuidanceTime = System.currentTimeMillis();
			}
			
			// Less aggressive since teleportation handles most safespots
			if (distance > MAX_SAFESPOT_DISTANCE + 10) {
				npc.resetCombat();
				player.sendMessage("Angel loses divine focus on distant targets.");
			}
		}
	}

	/**
	 * Calculate balanced damage using BossBalancer system
	 */
	private int calculateBalancedDamage(NPC npc, int attackType, double minMultiplier, double maxMultiplier) {
		NPCCombatDefinitions combatDefs = npc.getCombatDefinitions();
		if (combatDefs == null) {
			// Fallback to basic calculation
			return Utils.random(100, 300);
		}
		
		int baseMaxHit = combatDefs.getMaxHit();
		
		// Calculate damage range using BossBalancer principles
		int minDamage = (int) (baseMaxHit * minMultiplier);
		int maxDamage = (int) (baseMaxHit * maxMultiplier);
		
		// Add tier-based scaling
		if (detectedTier > 0) {
			double tierMultiplier = 1.0 + (detectedTier * 0.10); // 10% per tier for angel
			minDamage = (int) (minDamage * tierMultiplier);
			maxDamage = (int) (maxDamage * tierMultiplier);
		}
		
		return Utils.random(minDamage, maxDamage);
	}

	/**
	 * Send mechanic warning with cooldown
	 */
	private void sendMechanicWarning(NPC npc, String message) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastMechanicWarning > 8000) { // 8 second cooldown
			npc.setNextForceTalk(new ForceTalk(message));
			lastMechanicWarning = currentTime;
		}
	}

	/**
	 * Send periodic guidance based on combat patterns
	 */
	private void sendPeriodicGuidance(NPC npc, Entity target) {
		if (!(target instanceof Player) || !canSendGuidance()) return;
		
		Player player = (Player) target;
		
		// Every 10 attacks, provide strategic guidance
		if (attackCounter % 10 == 0) {
			String[] strategicTips = {
				"Angel uses diverse Zaros magic - vary your defenses!",
				"Teleportation makes safespotting impossible - stay mobile!",
				"Blood magic heals the Angel - minimize damage taken!",
				"Stat draining is constant - bring restore potions!",
				"Freeze effects are enhanced - use freedom abilities!",
				"AOE shadow attacks require quick positioning!",
				"Divine judgment cannot be escaped - face it directly!"
			};
			
			if (strategicTips.length > 0) {
				int randomIndex = Utils.random(strategicTips.length);
				player.sendMessage("<col=8B4513>Angel Guide: " + strategicTips[randomIndex]);
				lastGuidanceTime = System.currentTimeMillis();
			}
		}
	}

	/**
	 * Check if guidance can be sent (cooldown management)
	 */
	private boolean canSendGuidance() {
		return guidanceSystemActive && (System.currentTimeMillis() - lastGuidanceTime > 18000);
	}

	/**
	 * Estimate boss tier based on combat stats
	 */
	private int estimateBossTier(int hp, int maxHit) {
		int difficulty = (hp / 100) + (maxHit * 8);
		
		if (difficulty <= 60) return 1;
		else if (difficulty <= 150) return 2;
		else if (difficulty <= 280) return 3;
		else if (difficulty <= 480) return 4;
		else if (difficulty <= 740) return 5;
		else if (difficulty <= 1080) return 6;
		else if (difficulty <= 1500) return 7;
		else if (difficulty <= 2100) return 8;
		else if (difficulty <= 2900) return 9;
		else return 10;
	}

	/**
	 * Get tier name for display
	 */
	private String getTierName(int tier) {
		switch (tier) {
		case 1: return "Beginner";
		case 2: return "Novice";
		case 3: return "Intermediate";
		case 4: return "Advanced";
		case 5: return "Expert";
		case 6: return "Master";
		case 7: return "Elite";
		case 8: return "Legendary";
		case 9: return "Mythical";
		case 10: return "Divine";
		default: return "Unknown";
		}
	}

	// ===== LEGACY METHODS FOR BACKWARD COMPATIBILITY =====

	public void mageAttack(NPC npc, Entity target) {
		enhancedMageAttack(npc, target);
	}

	public void mageAttack2(NPC npc, Entity target) {
		enhancedMageAttack2(npc, target);
	}

	public void rangeAttack(NPC npc, Entity target) {
		enhancedRangeAttack(npc, target);
	}

	public void meleeAttack(NPC npc, Entity target) {
		enhancedMeleeAttack(npc, target);
	}

	public void aoeAttack(NPC npc, Entity target) {
		enhancedAoeAttack(npc, target);
	}
}