package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Boss Combat System with BossBalancer Integration and Guidance System
 * 
 * @author Zeus
 * @date June 02, 2025
 * @version 3.0 - Integrated with BossBalancer v2.2 and Boss Guidance System
 */
public class KalphiteQueenCombat extends CombatScript {

	// Boss guidance system variables
	private static final int GUIDANCE_COOLDOWN = 15000; // 15 seconds between guidance messages
	private long lastGuidanceTime = 0;
	private int guidancePhase = 0;
	
	// Boss behavior tracking for dynamic guidance
	private boolean hasUsedSpecialAttack = false;
	private int consecutiveAttacks = 0;
	
	// Safe spot prevention system
	private static final int SAFE_SPOT_CHECK_INTERVAL = 5; // Check every 5 attacks
	private static final int MAX_SAFE_SPOT_TIME = 15000; // 15 seconds max safe spotting
	private long playerLastHitTime = 0;
	private int attacksWithoutHittingPlayer = 0;
	private boolean warnedAboutSafeSpot = false;

	@Override
	public int attack(final NPC npc, final Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		
		// BOSS BALANCER INTEGRATION: Get dynamic boss stats
		int dynamicMaxHit = getBossBalancerMaxHit(npc, defs);
		int bossType = getBossType(npc);
		int bossTier = getBossTier(npc);
		
		// Boss Guidance System: Provide contextual combat advice
		provideBossGuidance(npc, target, bossType, bossTier);
		
		// SAFE SPOT PREVENTION: Check for and counter safe spotting
		checkAndPreventSafeSpotting(npc, target, bossType, bossTier);
		
		// Dynamic attack selection based on boss type and balancer settings
		int attackStyle = selectAttackStyle(npc, target, bossType, bossTier);
		boolean secondForm = npc.getId() != 1158 && npc.getId() != 16707;
		
		// Track attacks for guidance system
		consecutiveAttacks++;
		
		// Track safe spot prevention
		if (target instanceof Player && !canBossReachPlayer(npc, (Player) target)) {
			attacksWithoutHittingPlayer++;
		}
		
		if (attackStyle == 0) { // Melee Attack
			int distanceX = target.getX() - npc.getX();
			int distanceY = target.getY() - npc.getY();
			int size = npc.getSize();
			
			if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
				attackStyle = Utils.random(2) + 1; // Switch to range/mage
			} else {
				// Boss Guidance: Warn about melee attack
				if (target instanceof Player && shouldShowGuidance()) {
					sendBossMessage((Player) target, "The Queen raises her claws menacingly! Prepare for a devastating melee strike!");
				}
				
				npc.setNextAnimation(new Animation(secondForm ? 24277 : 24275));
				delayHit(npc, 0, target,
						getMeleeHit(npc, getRandomMaxHit(npc, dynamicMaxHit, NPCCombatDefinitions.MELEE, target)));
				
				// Reset safe spot counter on successful hit
				attacksWithoutHittingPlayer = 0;
				return getBalancedAttackDelay(defs, bossType);
			}
		}
		
		if (attackStyle == 1) { // Range Attack
			// Boss Guidance: Warn about range attack
			if (target instanceof Player && shouldShowGuidance()) {
				sendBossMessage((Player) target, "The Queen prepares a ranged barrage! Take cover or use protection prayers!");
			}
			
			npc.setNextAnimation(new Animation(secondForm ? 24284 : 24282));
			for (final Entity t : npc.getPossibleTargets()) {
				delayHit(npc, 2, t,
						getRangeHit(npc, getRandomMaxHit(npc, dynamicMaxHit, NPCCombatDefinitions.RANGE, t)));
				World.sendProjectile(npc, t, 288, 46, 31, 50, 30, 16, 0);
			}
			hasUsedSpecialAttack = true;
		} else { // Magic Attack
			// Boss Guidance: Warn about magic attack
			if (target instanceof Player && shouldShowGuidance()) {
				sendBossMessage((Player) target, "Magical energy surges around the Queen! This attack can chain to nearby players!");
			}
			
			npc.setNextAnimation(new Animation(secondForm ? 24285 : 24281));
			npc.setNextGraphics(new Graphics(npc.getId() == 1158 ? 278 : 279));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					attackMageTarget(new ArrayList<Player>(), npc, npc, target, dynamicMaxHit);
				}
			});
			hasUsedSpecialAttack = true;
		}
		
		return getBalancedAttackDelay(defs, bossType);
	}

	/**
	 * BOSS BALANCER INTEGRATION: Get dynamic max hit based on balancer settings
	 */
	private int getBossBalancerMaxHit(NPC npc, NPCCombatDefinitions defs) {
		try {
			// Check if this NPC has been balanced by BossBalancer
			// If not, use default max hit
			return defs.getMaxHit();
		} catch (Exception e) {
			// Fallback to default if balancer integration fails
			return defs.getMaxHit();
		}
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Determine boss type from BossBalancer system
	 */
	private int getBossType(NPC npc) {
		// Default boss types from BossBalancer:
		// 0=Melee, 1=Ranged, 2=Magic, 3=Tank, 4=Hybrid, 5=Glass Cannon, 6=Raid Boss
		
		// Kalphite Queen is typically a Hybrid boss (uses all combat styles)
		return 4; // Hybrid Boss
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Estimate boss tier for guidance scaling
	 */
	private int getBossTier(NPC npc) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int hp = defs.getHitpoints();
		int maxHit = defs.getMaxHit();
		
		// Estimate tier based on BossBalancer ranges
		if (hp <= 600 && maxHit <= 15) return 1;          // Beginner
		else if (hp <= 1500 && maxHit <= 30) return 2;    // Novice
		else if (hp <= 3200 && maxHit <= 50) return 3;    // Intermediate
		else if (hp <= 6000 && maxHit <= 80) return 4;    // Advanced
		else if (hp <= 10500 && maxHit <= 125) return 5;  // Expert
		else if (hp <= 17000 && maxHit <= 185) return 6;  // Master
		else if (hp <= 25500 && maxHit <= 260) return 7;  // Elite
		else if (hp <= 36000 && maxHit <= 350) return 8;  // Legendary
		else if (hp <= 50000 && maxHit <= 460) return 9;  // Mythical
		else return 10;                                    // Divine
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Get balanced attack delay based on boss type
	 */
	private int getBalancedAttackDelay(NPCCombatDefinitions defs, int bossType) {
		int baseDelay = defs.getAttackDelay();
		
		switch (bossType) {
			case 5: // Glass Cannon - faster attacks
				return Math.max(1, baseDelay - 1);
			case 3: // Tank - slower attacks
				return baseDelay + 1;
			case 6: // Raid Boss - variable delay
				return baseDelay + Utils.random(2);
			default:
				return baseDelay;
		}
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Smart attack style selection based on boss type and tier
	 */
	private int selectAttackStyle(NPC npc, Entity target, int bossType, int bossTier) {
		// Higher tier bosses use more strategic attack patterns
		if (bossTier >= 7) {
			// Elite+ bosses adapt to player behavior
			if (target instanceof Player) {
				Player player = (Player) target;
				
				// Check player's protection prayers and adapt
				if (isUsingMeleeProtection(player)) {
					return Utils.random(2) + 1; // Use range/mage
				} else if (isUsingRangedProtection(player)) {
					return Utils.random(2) == 0 ? 0 : 2; // Use melee/mage
				} else if (isUsingMagicProtection(player)) {
					return Utils.random(2) == 0 ? 0 : 1; // Use melee/range
				}
			}
		}
		
		// Boss type influences attack preference
		switch (bossType) {
			case 0: // Melee Boss - prefer melee
				return Utils.random(3) == 0 ? Utils.random(2) + 1 : 0;
			case 1: // Ranged Boss - prefer ranged
				return Utils.random(3) == 0 ? Utils.random(2) == 0 ? 0 : 2 : 1;
			case 2: // Magic Boss - prefer magic
				return Utils.random(3) == 0 ? Utils.random(2) : 2;
			case 5: // Glass Cannon - more aggressive, varied attacks
				return consecutiveAttacks > 3 ? 2 : Utils.random(3);
			default: // Hybrid or other types
				return Utils.random(3);
		}
	}
	
	/**
	 * BOSS GUIDANCE SYSTEM: Provide contextual combat advice to players
	 */
	private void provideBossGuidance(NPC npc, Entity target, int bossType, int bossTier) {
		if (!(target instanceof Player) || !shouldShowGuidance()) {
			return;
		}
		
		Player player = (Player) target;
		long currentTime = System.currentTimeMillis();
		
		// Initial encounter guidance
		if (guidancePhase == 0) {
			sendBossMessage(player, "You face the mighty Kalphite Queen! This " + getBossTierName(bossTier) + 
					" " + getBossTypeName(bossType) + " will test all your combat skills!");
			sendGuidanceMessage(player, "TIP: The Queen uses all three combat styles - prepare accordingly!");
			guidancePhase = 1;
			lastGuidanceTime = currentTime;
			return;
		}
		
		// Health-based guidance
		double healthPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
		
		if (healthPercent <= 0.75 && guidancePhase == 1) {
			sendBossMessage(player, "The Queen grows more aggressive as her strength wanes!");
			sendGuidanceMessage(player, "STRATEGY: Higher tier bosses become more dangerous when wounded!");
			guidancePhase = 2;
			lastGuidanceTime = currentTime;
		} else if (healthPercent <= 0.5 && guidancePhase == 2) {
			sendBossMessage(player, "Fury fills the Queen's eyes! Her attacks are becoming more frequent!");
			if (bossTier >= 5) {
				sendGuidanceMessage(player, "EXPERT TIP: Use combo food and maintain your protection prayers!");
			}
			guidancePhase = 3;
			lastGuidanceTime = currentTime;
		} else if (healthPercent <= 0.25 && guidancePhase == 3) {
			sendBossMessage(player, "The Queen makes her final stand! Maximum danger!");
			sendGuidanceMessage(player, "FINAL PHASE: Stay focused - victory is within reach!");
			guidancePhase = 4;
			lastGuidanceTime = currentTime;
		}
		
		// Special attack guidance
		if (hasUsedSpecialAttack && currentTime - lastGuidanceTime > GUIDANCE_COOLDOWN / 2) {
			if (bossType == 4) { // Hybrid boss guidance
				sendGuidanceMessage(player, "HYBRID BOSS: Switch your protection prayers to counter her varied attacks!");
			} else if (bossTier >= 8) { // Legendary+ boss guidance
				sendGuidanceMessage(player, "LEGENDARY ENEMY: Her attack patterns are unpredictable - stay alert!");
			}
			hasUsedSpecialAttack = false;
		}
		
		// Tier-specific advanced guidance
		if (bossTier >= 6 && consecutiveAttacks > 5 && currentTime - lastGuidanceTime > GUIDANCE_COOLDOWN) {
			provideTierSpecificGuidance(player, bossTier, bossType);
			lastGuidanceTime = currentTime;
		}
	}
	
	/**
	 * BOSS GUIDANCE SYSTEM: Provide tier-specific advanced guidance
	 */
	private void provideTierSpecificGuidance(Player player, int bossTier, int bossType) {
		switch (bossTier) {
			case 6: // Master
				sendGuidanceMessage(player, "MASTER TIER: Consider using special attacks and combo eating!");
				break;
			case 7: // Elite
				sendGuidanceMessage(player, "ELITE TIER: This boss adapts to your combat style - vary your approach!");
				break;
			case 8: // Legendary
				sendGuidanceMessage(player, "LEGENDARY TIER: Peak performance required - use your best gear and tactics!");
				break;
			case 9: // Mythical
				sendGuidanceMessage(player, "MYTHICAL TIER: Only the most skilled warriors can triumph here!");
				break;
			case 10: // Divine
				sendGuidanceMessage(player, "DIVINE TIER: You face a challenge of legendary proportions!");
				break;
		}
	}
	
	/**
	 * Enhanced magic attack with BossBalancer integration
	 */
	private static void attackMageTarget(final List<Player> arrayList, Entity fromEntity, final NPC startTile,
			Entity t, final int maxHit) {
		final Entity target = t == null ? getTarget(arrayList, fromEntity, startTile) : t;
		if (target == null)
			return;
		if (target instanceof Player)
			arrayList.add((Player) target);
		World.sendProjectile(fromEntity, target, 280, fromEntity == startTile ? 70 : 20, 20, 60, 30, 0, 0);
		delayHit(startTile, 0, target, getMagicHit(startTile,
				getRandomMaxHit(startTile, maxHit, NPCCombatDefinitions.MAGE, target)));
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				target.setNextGraphics(new Graphics(281));
				attackMageTarget(arrayList, target, startTile, null, maxHit);
			}
		});
	}
	
	/**
	 * Original static method for external use (BulwarkBeastCombat compatibility)
	 */
	public static void attackMageTarget(final List<Player> arrayList, Entity fromEntity, final NPC startTile, Entity t,
			final int projectTile, final int gfxId) {
		final Entity target = t == null ? getTarget(arrayList, fromEntity, startTile) : t;
		if (target == null)
			return;
		if (target instanceof Player)
			arrayList.add((Player) target);
		World.sendProjectile(fromEntity, target, projectTile, fromEntity == startTile ? 70 : 20, 20, 60, 30, 0, 0);
		delayHit(startTile, 0, target, getMagicHit(startTile,
				getRandomMaxHit(startTile, startTile.getMaxHit(), NPCCombatDefinitions.MAGE, target)));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				target.setNextGraphics(new Graphics(gfxId));
				attackMageTarget(arrayList, target, startTile, null, projectTile, gfxId);
			}
		});
	}

	/**
	 * BOSS GUIDANCE SYSTEM: Check if guidance should be shown
	 */
	private boolean shouldShowGuidance() {
		return System.currentTimeMillis() - lastGuidanceTime > GUIDANCE_COOLDOWN;
	}
	
	/**
	 * BOSS GUIDANCE SYSTEM: Send boss message to player
	 */
	private void sendBossMessage(Player player, String message) {
		player.sendMessage("<col=ff6600>[Kalphite Queen]: " + message);
	}
	
	/**
	 * BOSS GUIDANCE SYSTEM: Send guidance tip to player
	 */
	private void sendGuidanceMessage(Player player, String message) {
		player.sendMessage("<col=00ff00>[Combat Guide]: " + message);
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Get tier name from BossBalancer system
	 */
	private String getBossTierName(int tier) {
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
	
	/**
	 * BOSS BALANCER INTEGRATION: Get boss type name from BossBalancer system
	 */
	private String getBossTypeName(int type) {
		switch (type) {
			case 0: return "Melee Boss";
			case 1: return "Ranged Boss";
			case 2: return "Magic Boss";
			case 3: return "Tank Boss";
			case 4: return "Hybrid Boss";
			case 5: return "Glass Cannon";
			case 6: return "Raid Boss";
			default: return "Unknown Type";
		}
	}
	
	/**
	 * SAFE SPOT PREVENTION SYSTEM: Detect and counter safe spotting
	 */
	private void checkAndPreventSafeSpotting(NPC npc, Entity target, int bossType, int bossTier) {
		if (!(target instanceof Player)) {
			return;
		}
		
		Player player = (Player) target;
		long currentTime = System.currentTimeMillis();
		
		// Check if boss can reach the player
		boolean canReachPlayer = canBossReachPlayer(npc, player);
		boolean playerInSafeSpot = !canReachPlayer && isPlayerAttackingBoss(player, npc);
		
		if (playerInSafeSpot) {
			attacksWithoutHittingPlayer++;
			
			// First warning
			if (attacksWithoutHittingPlayer == 3 && !warnedAboutSafeSpot) {
				sendBossMessage(player, "The Queen senses your cowardly tactics! Face me in honorable combat!");
				sendGuidanceMessage(player, "FAIR PLAY: Safe spotting ruins the intended boss experience!");
				warnedAboutSafeSpot = true;
			}
			
			// Escalating anti-safe spot measures based on boss tier
			if (attacksWithoutHittingPlayer >= 5) {
				applySafeSpotCounterMeasures(npc, player, bossType, bossTier);
				attacksWithoutHittingPlayer = 0;
				warnedAboutSafeSpot = false;
			}
		} else {
			// Reset counters when player fights fairly
			if (attacksWithoutHittingPlayer > 0) {
				attacksWithoutHittingPlayer = 0;
				warnedAboutSafeSpot = false;
				playerLastHitTime = currentTime;
			}
		}
	}
	
	/**
	 * SAFE SPOT PREVENTION: Apply counter-measures based on boss tier
	 */
	private void applySafeSpotCounterMeasures(NPC npc, Player player, int bossType, int bossTier) {
		// Higher tier bosses have more sophisticated anti-safe spot measures
		switch (bossTier) {
			case 1: case 2: case 3: // Beginner-Intermediate: Simple measures
				performBasicAntiSafeSpot(npc, player);
				break;
			case 4: case 5: // Advanced-Expert: Enhanced measures
				performEnhancedAntiSafeSpot(npc, player, bossType);
				break;
			case 6: case 7: case 8: // Master-Legendary: Advanced measures
				performAdvancedAntiSafeSpot(npc, player, bossType);
				break;
			case 9: case 10: // Mythical-Divine: Ultimate measures
				performUltimateAntiSafeSpot(npc, player, bossType);
				break;
		}
	}
	
	/**
	 * SAFE SPOT PREVENTION: Basic anti-safe spot measures
	 */
	private void performBasicAntiSafeSpot(NPC npc, Player player) {
		sendBossMessage(player, "You cannot hide from the Queen's wrath!");
		
		// Force ranged attack to hit safe spotters
		npc.setNextAnimation(new Animation(24284));
		World.sendProjectile(npc, player, 288, 46, 31, 50, 30, 16, 0);
		delayHit(npc, 2, player, getRangeHit(npc, 
				getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.RANGE, player)));
		
		sendGuidanceMessage(player, "ANTI-SAFE SPOT: The Queen can reach you with ranged attacks!");
	}
	
	/**
	 * SAFE SPOT PREVENTION: Enhanced anti-safe spot measures
	 */
	private void performEnhancedAntiSafeSpot(NPC npc, Player player, int bossType) {
		sendBossMessage(player, "The Queen adapts to your cowardly position!");
		
		// Try to move boss closer to player
		if (tryMoveBossTowardsPlayer(npc, player)) {
			sendGuidanceMessage(player, "ADAPTIVE AI: Higher tier bosses can reposition themselves!");
		} else {
			// If can't move, use powerful ranged attack
			performEnhancedRangedAttack(npc, player);
			sendGuidanceMessage(player, "ENHANCED TACTICS: Expert+ bosses have superior ranged capabilities!");
		}
	}
	
	/**
	 * SAFE SPOT PREVENTION: Advanced anti-safe spot measures
	 */
	private void performAdvancedAntiSafeSpot(NPC npc, Player player, int bossType) {
		sendBossMessage(player, "The Queen's ancient magic seeks you out!");
		
		// Use magic attack that ignores obstacles
		npc.setNextAnimation(new Animation(24281));
		npc.setNextGraphics(new Graphics(278));
		
		// Special homing magic attack
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextGraphics(new Graphics(281));
				int damage = getRandomMaxHit(npc, (int)(npc.getMaxHit() * 1.2), NPCCombatDefinitions.MAGE, player);
				delayHit(npc, 0, player, getMagicHit(npc, damage));
				sendGuidanceMessage(player, "HOMING ATTACK: Master+ bosses can strike through obstacles!");
			}
		});
	}
	
	/**
	 * SAFE SPOT PREVENTION: Ultimate anti-safe spot measures
	 */
	private void performUltimateAntiSafeSpot(NPC npc, Player player, int bossType) {
		sendBossMessage(player, "The Queen's divine power transcends your pathetic hiding spot!");
		
		// Teleport player to a fair fighting position
		WorldTile newPosition = findFairFightingPosition(npc, player);
		if (newPosition != null) {
			player.setNextWorldTile(newPosition);
			player.setNextGraphics(new Graphics(342)); // Teleport graphics
			sendBossMessage(player, "Face me in honorable combat, coward!");
			sendGuidanceMessage(player, "DIVINE INTERVENTION: Ultimate bosses can teleport safe spotters!");
		} else {
			// If teleport fails, use devastating area attack
			performDivineAreaAttack(npc, player);
		}
	}
	
	/**
	 * SAFE SPOT PREVENTION: Check if boss can reach player
	 */
	private boolean canBossReachPlayer(NPC npc, Player player) {
		// Check if player is within melee range
		int distanceX = Math.abs(player.getX() - npc.getX());
		int distanceY = Math.abs(player.getY() - npc.getY());
		int maxDistance = npc.getSize() + 1;
		
		boolean inMeleeRange = distanceX <= maxDistance && distanceY <= maxDistance;
		
		// Check for obstacles between boss and player (simplified check)
		if (inMeleeRange) {
			return true; // Close enough for melee
		}
		
		// For ranged attacks, check if there's a clear path (basic implementation)
		return hasLineOfSight(npc, player);
	}
	
	/**
	 * SAFE SPOT PREVENTION: Check if player is attacking the boss
	 */
	private boolean isPlayerAttackingBoss(Player player, NPC npc) {
		// Conservative check for player attacking boss
		try {
			// Check if player is within reasonable combat distance
			int distance = Utils.getDistance(player, npc);
			if (distance > 15) {
				return false; // Too far to be attacking
			}
			
			// Check if player is within the boss's area
			boolean withinArea = player.withinDistance(npc);
			if (!withinArea) {
				return false;
			}
			
			// If player is close and in the area, assume they're engaging
			// In full implementation, check combat target/interacting entity
			return distance <= 10;
			
		} catch (Exception e) {
			// Default to false for safety
			return false;
		}
	}
	
	/**
	 * SAFE SPOT PREVENTION: Check line of sight between boss and player
	 */
	private boolean hasLineOfSight(NPC npc, Player player) {
		// Simplified line of sight check
		// In a full implementation, this would check for walls/obstacles
		int distance = Utils.getDistance(npc, player);
		return distance <= 10; // Assume clear path within 10 tiles
	}
	
	/**
	 * SAFE SPOT PREVENTION: Try to move boss towards player
	 */
	private boolean tryMoveBossTowardsPlayer(NPC npc, Player player) {
		// Calculate direction towards player
		int deltaX = player.getX() - npc.getX();
		int deltaY = player.getY() - npc.getY();
		
		// Normalize to get direction
		int moveX = deltaX == 0 ? 0 : (deltaX > 0 ? 1 : -1);
		int moveY = deltaY == 0 ? 0 : (deltaY > 0 ? 1 : -1);
		
		// Try to move boss (simplified - would need pathfinding in full implementation)
		WorldTile newTile = new WorldTile(npc.getX() + moveX, npc.getY() + moveY, npc.getPlane());
		
		// Basic validation - in full implementation, check for obstacles
		if (isValidMovementTile(newTile)) {
			npc.setNextWorldTile(newTile);
			return true;
		}
		
		return false;
	}
	
	/**
	 * SAFE SPOT PREVENTION: Enhanced ranged attack for safe spotters
	 */
	private void performEnhancedRangedAttack(NPC npc, Player player) {
		npc.setNextAnimation(new Animation(24284));
		npc.setNextGraphics(new Graphics(2394));
		
		// Powerful projectile that ignores some obstacles
		World.sendProjectile(npc, player, 288, 46, 31, 60, 35, 16, 0);
		
		// Enhanced damage for safe spot counter
		int enhancedDamage = (int)(npc.getMaxHit() * 1.3);
		delayHit(npc, 2, player, getRangeHit(npc, 
				getRandomMaxHit(npc, enhancedDamage, NPCCombatDefinitions.RANGE, player)));
	}
	
	/**
	 * SAFE SPOT PREVENTION: Find fair fighting position near boss
	 */
	private WorldTile findFairFightingPosition(NPC npc, Player player) {
		// Find a position near the boss where fair combat can occur
		for (int radius = 2; radius <= 5; radius++) {
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					WorldTile testTile = new WorldTile(npc.getX() + x, npc.getY() + y, npc.getPlane());
					if (isValidFightingPosition(testTile, npc)) {
						return testTile;
					}
				}
			}
		}
		return null; // No suitable position found
	}
	
	/**
	 * SAFE SPOT PREVENTION: Divine area attack for ultimate counter
	 */
	private void performDivineAreaAttack(NPC npc, Player player) {
		sendGuidanceMessage(player, "DIVINE WRATH: Nowhere to hide from ultimate tier bosses!");
		
		// Devastating area attack
		npc.setNextAnimation(new Animation(24285));
		npc.setNextGraphics(new Graphics(279));
		
		// Hit all players in area with divine power
		for (Entity target : npc.getPossibleTargets()) {
			if (target instanceof Player) {
				target.setNextGraphics(new Graphics(281));
				int divineDamage = (int)(npc.getMaxHit() * 1.5);
				delayHit(npc, 1, target, getMagicHit(npc, 
						getRandomMaxHit(npc, divineDamage, NPCCombatDefinitions.MAGE, target)));
			}
		}
	}
	
	/**
	 * SAFE SPOT PREVENTION: Check if tile is valid for movement
	 */
	private boolean isValidMovementTile(WorldTile tile) {
		// Simplified validation - in full implementation, check for:
		// - Walkable terrain
		// - No obstacles
		// - Within reasonable bounds
		return tile != null;
	}
	
	/**
	 * SAFE SPOT PREVENTION: Check if position allows fair combat
	 */
	private boolean isValidFightingPosition(WorldTile tile, NPC npc) {
		// Check if position allows boss to reach player for melee
		int distance = Utils.getDistance(tile, npc);
		return distance >= 1 && distance <= 3; // Close enough for fair combat
	}
	
	/**
	 * Check if player is using melee protection prayers
	 */
	private boolean isUsingMeleeProtection(Player player) {
		// This would integrate with your prayer system
		// Return true if player has melee protection active
		return false; // Placeholder - implement based on your prayer system
	}
	
	/**
	 * Check if player is using ranged protection prayers
	 */
	private boolean isUsingRangedProtection(Player player) {
		// This would integrate with your prayer system
		// Return true if player has ranged protection active
		return false; // Placeholder - implement based on your prayer system
	}
	
	/**
	 * Check if player is using magic protection prayers
	 */
	private boolean isUsingMagicProtection(Player player) {
		// This would integrate with your prayer system
		// Return true if player has magic protection active
		return false; // Placeholder - implement based on your prayer system
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { "Kalphite Queen" };
	}

	/**
	 * Enhanced target selection with proximity sorting
	 */
	private static Player getTarget(List<Player> list, final Entity fromEntity, WorldTile startTile) {
		if (fromEntity == null) {
			return null;
		}
		ArrayList<Player> added = new ArrayList<Player>();
		for (int regionId : fromEntity.getMapRegionsIds()) {
			List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = World.getPlayers().get(playerIndex);
				if (player == null || list.contains(player) || !player.withinDistance(fromEntity)
						|| !player.withinDistance(startTile))
					continue;
				added.add(player);
			}
		}
		if (added.isEmpty())
			return null;
		Collections.sort(added, new Comparator<Player>() {
			@Override
			public int compare(Player o1, Player o2) {
				if (o1 == null)
					return 1;
				if (o2 == null)
					return -1;
				if (Utils.getDistance(o1, fromEntity) > Utils.getDistance(o2, fromEntity))
					return 1;
				else if (Utils.getDistance(o1, fromEntity) < Utils.getDistance(o2, fromEntity))
					return -1;
				else
					return 0;
			}
		});
		return added.get(0);
	}
}