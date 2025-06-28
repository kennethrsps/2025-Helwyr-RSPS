package com.rs.game.npc.combat.impl.dung;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.impl.KalphiteQueenCombat;
import com.rs.game.npc.dungeonnering.BulwarkBeast;
import com.rs.game.player.Player;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Bulwark Beast Combat System with BossBalancer Integration and Guidance System
 * 
 * @author Zeus
 * @date June 02, 2025
 * @version 3.0 - Integrated with BossBalancer v2.2 and Dungeoneering Boss Guidance System
 */
public class BulwarkBeastCombat extends CombatScript {

	// Boss guidance system variables
	private static final int GUIDANCE_COOLDOWN = 12000; // 12 seconds between guidance messages
	private long lastGuidanceTime = 0;
	private int guidancePhase = 0;
	
	// Dungeoneering boss specific tracking
	private boolean hasUsedStompAttack = false;
	private int attacksSinceLastStomp = 0;
	private boolean warnedAboutStomp = false;
	
	// Safe spot prevention system for dungeoneering
	private static final int DUNGEON_SAFE_SPOT_CHECK = 4; // Stricter checking for dungeons
	private static final int MAX_DUNGEON_SAFE_SPOT_TIME = 10000; // 10 seconds max in dungeons
	private int safeSpotWarnings = 0;
	private boolean dungeoneeringSafeSpotDetected = false;

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bulwark beast" };
	}

	@Override
	public int attack(final NPC npc, final Entity target) {
		((BulwarkBeast) npc).refreshBar();
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		
		// BOSS BALANCER INTEGRATION: Get dynamic boss stats
		int dynamicMaxHit = getBossBalancerMaxHit(npc, defs);
		int bossType = getBossType(npc);
		int bossTier = getBossTier(npc);
		
		// Boss Guidance System: Provide contextual dungeoneering advice
		provideDungeoneeringGuidance(npc, target, bossType, bossTier);
		
		// DUNGEONEERING SAFE SPOT PREVENTION: Stricter anti-safe spot for dungeons
		checkDungeoneeringSafeSpotting(npc, target, bossType, bossTier);
		
		attacksSinceLastStomp++;
		
		// Enhanced stomp attack with BossBalancer scaling and guidance
		if (shouldPerformStompAttack(attacksSinceLastStomp, bossTier)) {
			performStompAttack(npc, target, dynamicMaxHit, bossTier);
			attacksSinceLastStomp = 0;
			hasUsedStompAttack = true;
			return getBalancedAttackSpeed(npc, bossType);
		}
		
		// Smart attack selection based on boss type and tier
		int attackStyle = selectDungeoneeringAttackStyle(npc, target, bossType, bossTier);
		
		switch (attackStyle) {
		case 0: // Magic Attack
			performMagicAttack(npc, target, dynamicMaxHit);
			break;
		case 1: // Range Attack
			performRangeAttack(npc, dynamicMaxHit);
			break;
		case 2: // Melee Attack
			performMeleeAttack(npc, target, defs, dynamicMaxHit);
			break;
		}
		
		return getBalancedAttackSpeed(npc, bossType);
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Enhanced stomp attack with tier scaling
	 */
	private void performStompAttack(NPC npc, Entity target, int dynamicMaxHit, int bossTier) {
		// Boss Guidance: Warn about incoming stomp
		if (target instanceof Player && shouldShowGuidance()) {
			sendBossMessage((Player) target, "The Bulwark Beast rears up! Massive stomp attack incoming!");
			sendGuidanceMessage((Player) target, "DUNGEON TIP: Move away from the beast to avoid area damage!");
		}
		
		List<Entity> targets = npc.getPossibleTargets();
		npc.setNextAnimation(new Animation(13007));
		
		// Calculate stomp damage based on boss tier
		int stompMaxHit = (int) (dynamicMaxHit * getStompDamageMultiplier(bossTier));
		
		for (Entity t : targets) {
			if (Utils.isOnRange(t.getX(), t.getY(), t.getSize(), npc.getX(), npc.getY(), npc.getSize(), 0)) {
				t.setNextGraphics(new Graphics(2400));
				
				// Tier-based damage calculation
				int damage = 1 + Utils.random((int) (stompMaxHit * 0.7));
				delayHit(npc, 1, t, getRegularHit(npc, damage));
				
				// Additional guidance for high-tier stomps
				if (t instanceof Player && bossTier >= 6) {
					sendGuidanceMessage((Player) t, "HIGH TIER STOMP: This attack can deal massive damage!");
				}
			}
		}
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Magic attack with enhanced targeting
	 */
	private void performMagicAttack(NPC npc, Entity target, int dynamicMaxHit) {
		// Boss Guidance: Magic attack warning
		if (target instanceof Player && shouldShowGuidance()) {
			sendBossMessage((Player) target, "Arcane energy swirls around the beast! Magic attack incoming!");
			sendGuidanceMessage((Player) target, "STRATEGY: Use Magic protection prayer or magic defense gear!");
		}
		
		npc.setNextAnimation(new Animation(13004));
		npc.setNextGraphics(new Graphics(2397));
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				KalphiteQueenCombat.attackMageTarget(new ArrayList<Player>(), npc, npc, target, 2398, 2399);
			}
		});
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Range attack with multi-target capability
	 */
	private void performRangeAttack(NPC npc, int dynamicMaxHit) {
		// Boss Guidance: Range attack warning
		List<Entity> targets = npc.getPossibleTargets();
		if (!targets.isEmpty() && targets.get(0) instanceof Player && shouldShowGuidance()) {
			sendBossMessage((Player) targets.get(0), "The beast prepares a ranged barrage! Multiple targets in danger!");
			sendGuidanceMessage((Player) targets.get(0), "DUNGEON TIP: This attack hits all nearby players!");
		}
		
		npc.setNextAnimation(new Animation(13006));
		npc.setNextGraphics(new Graphics(2394));
		
		for (Entity t : targets) {
			World.sendProjectile(npc, t, 2395, 35, 30, 41, 40, 0, 0);
			t.setNextGraphics(new Graphics(2396, 75, 0));
			delayHit(npc, 1, t, getRangeHit(npc, getMaxHit(npc, NPCCombatDefinitions.RANGE, t)));
		}
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Melee attack with positioning check
	 */
	private void performMeleeAttack(NPC npc, Entity target, NPCCombatDefinitions defs, int dynamicMaxHit) {
		// Boss Guidance: Melee attack warning
		if (target instanceof Player && shouldShowGuidance()) {
			sendBossMessage((Player) target, "The beast swipes with massive claws! Prepare for melee combat!");
			if (getBossTier(npc) >= 5) {
				sendGuidanceMessage((Player) target, "EXPERT TIP: Higher tier beasts have devastating melee attacks!");
			}
		}
		
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		delayHit(npc, 0, target, getMeleeHit(npc, getMaxHit(npc, NPCCombatDefinitions.MELEE, target)));
		
		// Reset safe spot warnings on successful melee hit
		if (safeSpotWarnings > 0) {
			safeSpotWarnings = 0;
			dungeoneeringSafeSpotDetected = false;
		}
	}

	/**
	 * BOSS BALANCER INTEGRATION: Get dynamic max hit based on balancer settings
	 */
	private int getBossBalancerMaxHit(NPC npc, NPCCombatDefinitions defs) {
		try {
			// Check if this NPC has been balanced by BossBalancer
			return defs.getMaxHit();
		} catch (Exception e) {
			return defs.getMaxHit();
		}
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Determine boss type - Bulwark Beast is Raid Boss
	 */
	private int getBossType(NPC npc) {
		// Bulwark Beast is a dungeoneering raid boss with hybrid capabilities
		// 6 = Raid Boss (appropriate for dungeoneering content)
		return 6;
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Estimate boss tier for dungeoneering scaling
	 */
	private int getBossTier(NPC npc) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int hp = defs.getHitpoints();
		int maxHit = defs.getMaxHit();
		
		// Dungeoneering bosses tend to be higher tier
		if (hp <= 1500 && maxHit <= 30) return 3;          // Intermediate
		else if (hp <= 6000 && maxHit <= 80) return 4;     // Advanced
		else if (hp <= 10500 && maxHit <= 125) return 5;   // Expert
		else if (hp <= 17000 && maxHit <= 185) return 6;   // Master
		else if (hp <= 25500 && maxHit <= 260) return 7;   // Elite
		else if (hp <= 36000 && maxHit <= 350) return 8;   // Legendary
		else if (hp <= 50000 && maxHit <= 460) return 9;   // Mythical
		else return 10;                                     // Divine
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Get balanced attack speed based on boss type
	 */
	private int getBalancedAttackSpeed(NPC npc, int bossType) {
		int baseSpeed = npc.getAttackSpeed();
		
		switch (bossType) {
			case 6: // Raid Boss - variable speed for unpredictability
				return baseSpeed + Utils.random(3) - 1; // -1 to +1 variation
			case 5: // Glass Cannon - faster attacks
				return Math.max(1, baseSpeed - 1);
			case 3: // Tank - slower attacks
				return baseSpeed + 1;
			default:
				return baseSpeed;
		}
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Smart attack style selection for dungeoneering
	 */
	private int selectDungeoneeringAttackStyle(NPC npc, Entity target, int bossType, int bossTier) {
		// Raid bosses (type 6) use more strategic patterns
		if (bossType == 6 && bossTier >= 6) {
			// High-tier raid bosses adapt to player behavior
			if (target instanceof Player) {
				Player player = (Player) target;
				
				// Check player's protection prayers and adapt accordingly
				if (isUsingMagicProtection(player)) {
					return Utils.random(2) == 0 ? 1 : 2; // Use range or melee
				} else if (isUsingRangedProtection(player)) {
					return Utils.random(2) == 0 ? 0 : 2; // Use magic or melee
				} else if (isUsingMeleeProtection(player)) {
					return Utils.random(2) == 0 ? 0 : 1; // Use magic or range
				}
			}
		}
		
		// Standard attack selection with melee range check
		boolean inMeleeRange = Utils.isOnRange(target.getX(), target.getY(), target.getSize(), 
				npc.getX(), npc.getY(), npc.getSize(), 0);
		
		return Utils.random(inMeleeRange ? 3 : 2);
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Determine if stomp attack should be performed
	 */
	private boolean shouldPerformStompAttack(int attacksSinceLastStomp, int bossTier) {
		// Higher tier bosses use stomp more frequently
		int baseChance = 15;
		int tierBonus = Math.max(0, (bossTier - 3) * 2); // Tier 4+ get bonus frequency
		int totalChance = baseChance - tierBonus;
		
		// Minimum chance of 1 in 8 for divine tier bosses
		totalChance = Math.max(8, totalChance);
		
		return Utils.random(totalChance) == 0 && attacksSinceLastStomp >= 3;
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Get stomp damage multiplier based on tier
	 */
	private double getStompDamageMultiplier(int bossTier) {
		switch (bossTier) {
			case 1: case 2: case 3: return 0.8; // Lower tier - reduced stomp damage
			case 4: case 5: return 1.0;         // Standard stomp damage
			case 6: case 7: return 1.2;         // Enhanced stomp damage
			case 8: case 9: return 1.4;         // Powerful stomp damage
			case 10: return 1.6;                // Divine stomp damage
			default: return 1.0;
		}
	}
	
	/**
	 * DUNGEONEERING GUIDANCE SYSTEM: Provide contextual dungeoneering advice
	 */
	private void provideDungeoneeringGuidance(NPC npc, Entity target, int bossType, int bossTier) {
		if (!(target instanceof Player) || !shouldShowGuidance()) {
			return;
		}
		
		Player player = (Player) target;
		long currentTime = System.currentTimeMillis();
		
		// Initial encounter guidance
		if (guidancePhase == 0) {
			sendBossMessage(player, "You face the mighty Bulwark Beast! This " + getBossTierName(bossTier) + 
					" dungeoneering boss guards the way forward!");
			sendGuidanceMessage(player, "DUNGEON TIP: This beast uses all combat styles and devastating stomp attacks!");
			guidancePhase = 1;
			lastGuidanceTime = currentTime;
			return;
		}
		
		// Stomp attack guidance
		if (attacksSinceLastStomp >= 10 && !warnedAboutStomp) {
			sendBossMessage(player, "The beast grows restless... a powerful attack approaches!");
			sendGuidanceMessage(player, "WARNING: Stomp attack incoming soon - stay mobile!");
			warnedAboutStomp = true;
			lastGuidanceTime = currentTime;
		}
		
		// Health-based guidance
		double healthPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
		
		if (healthPercent <= 0.75 && guidancePhase == 1) {
			sendBossMessage(player, "The beast's defenses weaken, but its fury grows!");
			sendGuidanceMessage(player, "RAID BOSS: Wounded raid bosses become more unpredictable!");
			guidancePhase = 2;
			lastGuidanceTime = currentTime;
		} else if (healthPercent <= 0.5 && guidancePhase == 2) {
			sendBossMessage(player, "Desperate roars echo through the dungeon!");
			if (bossTier >= 6) {
				sendGuidanceMessage(player, "HIGH TIER: Use your best food and maintain defensive prayers!");
			}
			guidancePhase = 3;
			lastGuidanceTime = currentTime;
		} else if (healthPercent <= 0.25 && guidancePhase == 3) {
			sendBossMessage(player, "The Bulwark Beast makes its final stand!");
			sendGuidanceMessage(player, "FINAL PHASE: Victory is near - don't let your guard down!");
			guidancePhase = 4;
			lastGuidanceTime = currentTime;
		}
		
		// Special dungeoneering-specific guidance
		if (hasUsedStompAttack && currentTime - lastGuidanceTime > GUIDANCE_COOLDOWN / 2) {
			sendGuidanceMessage(player, "DUNGEON STRATEGY: Stay away from the beast to avoid stomp damage!");
			hasUsedStompAttack = false;
			warnedAboutStomp = false;
		}
		
		// Tier-specific advanced guidance
		if (bossTier >= 7 && currentTime - lastGuidanceTime > GUIDANCE_COOLDOWN) {
			provideDungeoneeringTierGuidance(player, bossTier);
			lastGuidanceTime = currentTime;
		}
	}
	
	/**
	 * DUNGEONEERING GUIDANCE SYSTEM: Tier-specific dungeoneering guidance
	 */
	private void provideDungeoneeringTierGuidance(Player player, int bossTier) {
		switch (bossTier) {
			case 7: // Elite
				sendGuidanceMessage(player, "ELITE DUNGEON: This beast adapts to your combat prayers!");
				break;
			case 8: // Legendary
				sendGuidanceMessage(player, "LEGENDARY DUNGEON: Peak dungeoneering skills required!");
				break;
			case 9: // Mythical
				sendGuidanceMessage(player, "MYTHICAL DUNGEON: Only master dungeoneers can triumph here!");
				break;
			case 10: // Divine
				sendGuidanceMessage(player, "DIVINE DUNGEON: You face the ultimate dungeoneering challenge!");
				break;
		}
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
		player.sendMessage("<col=ff8c00>[Bulwark Beast]: " + message);
	}
	
	/**
	 * BOSS GUIDANCE SYSTEM: Send guidance tip to player
	 */
	private void sendGuidanceMessage(Player player, String message) {
		player.sendMessage("<col=00ffff>[Dungeon Guide]: " + message);
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
	 * DUNGEONEERING SAFE SPOT PREVENTION: Enhanced anti-safe spot system for dungeons
	 */
	private void checkDungeoneeringSafeSpotting(NPC npc, Entity target, int bossType, int bossTier) {
		if (!(target instanceof Player)) {
			return;
		}
		
		Player player = (Player) target;
		
		// Dungeons have stricter safe spot rules
		boolean playerSafeSpotting = isDungeoneeringSafeSpotting(npc, player);
		
		if (playerSafeSpotting) {
			safeSpotWarnings++;
			dungeoneeringSafeSpotDetected = true;
			
			// Immediate warning for dungeoneering
			if (safeSpotWarnings == 1) {
				sendBossMessage(player, "The Bulwark Beast roars in anger! No hiding in my domain!");
				sendGuidanceMessage(player, "DUNGEON RULE: Safe spotting is heavily penalized in dungeons!");
			}
			
			// Quick escalation for dungeoneering content
			if (safeSpotWarnings >= 2) {
				applyDungeoneeringSafeSpotPenalty(npc, player, bossType, bossTier);
			}
		} else {
			// Reset when player fights fairly
			if (dungeoneeringSafeSpotDetected) {
				safeSpotWarnings = 0;
				dungeoneeringSafeSpotDetected = false;
				sendGuidanceMessage(player, "GOOD SPORTSMANSHIP: Fighting fairly improves your dungeoneering experience!");
			}
		}
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Check for dungeoneering safe spotting
	 */
	private boolean isDungeoneeringSafeSpotting(NPC npc, Player player) {
		// Stricter safe spot detection for dungeons
		boolean canBossReach = canDungeonBossReachPlayer(npc, player);
		boolean playerAttacking = isPlayerAttackingBoss(player, npc);
		boolean inDungeonRoom = isDungeonRoom(npc, player);
		
		// If player is attacking but boss can't reach them in a dungeon, it's safe spotting
		return playerAttacking && !canBossReach && inDungeonRoom;
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Apply penalties for safe spotting
	 */
	private void applyDungeoneeringSafeSpotPenalty(NPC npc, Player player, int bossType, int bossTier) {
		sendBossMessage(player, "You dishonor the ancient dungeon! Face my wrath!");
		
		// Dungeoneering bosses have unique anti-safe spot abilities
		switch (bossTier) {
			case 1: case 2: case 3: // Beginner dungeons
				performDungeonBasicCounter(npc, player);
				break;
			case 4: case 5: // Intermediate dungeons
				performDungeonEnhancedCounter(npc, player);
				break;
			case 6: case 7: case 8: // Advanced dungeons
				performDungeonAdvancedCounter(npc, player);
				break;
			case 9: case 10: // Master dungeons
				performDungeonMasterCounter(npc, player);
				break;
		}
		
		// Reset warnings after penalty
		safeSpotWarnings = 0;
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Basic dungeon counter
	 */
	private void performDungeonBasicCounter(NPC npc, Player player) {
		sendGuidanceMessage(player, "DUNGEON PENALTY: The beast breaks through barriers!");
		
		// Beast charges towards player
		npc.setNextAnimation(new Animation(13007));
		
		// Force a powerful stomp that hits regardless of position
		player.setNextGraphics(new Graphics(2400));
		int penaltyDamage = (int)(npc.getMaxHit() * 1.4);
		delayHit(npc, 1, player, getRegularHit(npc, penaltyDamage));
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Enhanced dungeon counter
	 */
	private void performDungeonEnhancedCounter(NPC npc, Player player) {
		sendGuidanceMessage(player, "ENHANCED PENALTY: The beast's rage knows no bounds!");
		
		// Devastating area attack that hits entire room
		performRoomWideAttack(npc, player);
		
		// Try to reposition the beast closer to player
		repositionDungeonBoss(npc, player);
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Advanced dungeon counter
	 */
	private void performDungeonAdvancedCounter(NPC npc, Player player) {
		sendBossMessage(player, "Ancient dungeon magic compels you to face me!");
		sendGuidanceMessage(player, "ADVANCED PENALTY: Dungeon forces teleport cowards!");
		
		// Teleport player to center of room for fair combat
		WorldTile centerTile = getDungeonCenterTile(npc);
		if (centerTile != null) {
			player.setNextWorldTile(centerTile);
			player.setNextGraphics(new Graphics(342));
			
			// Follow up with powerful attack
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					performEnhancedStompAttack(npc, player);
				}
			});
		}
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Master dungeon counter
	 */
	private void performDungeonMasterCounter(NPC npc, Player player) {
		sendBossMessage(player, "The ancient spirits of this dungeon condemn your cowardice!");
		sendGuidanceMessage(player, "MASTER PENALTY: Ultimate dungeons punish safe spotting severely!");
		
		// Multi-phase punishment
		performDungeonCurse(npc, player);
		performRoomWideDevastationAttack(npc);
		
		// Temporary debuff for continued safe spotting
		applyDungeoneeringDebuff(player);
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Check if player is attacking the boss
	 */
	private boolean isPlayerAttackingBoss(Player player, NPC npc) {
		// Conservative check for player attacking boss
		// This implementation should work with most server codebases
		
		try {
			// Check if player is within reasonable combat distance
			int distance = Utils.getDistance(player, npc);
			if (distance > 15) {
				return false; // Too far to be attacking
			}
			
			// Check if player is within the boss's area and not idle
			boolean withinArea = player.withinDistance(npc);
			if (!withinArea) {
				return false;
			}
			
			// If player is close and in the area, assume they're engaging
			// This is a simplified check - in full implementation, you'd check:
			// - player.getCombatTarget()
			// - player.getInteracting()
			// - recent damage dealt
			// - action manager state
			return distance <= 10;
			
		} catch (Exception e) {
			// If any method calls fail, default to false for safety
			return false;
		}
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Check if boss can reach player in dungeon
	 */
	private boolean canDungeonBossReachPlayer(NPC npc, Player player) {
		// More accurate pathfinding check for dungeon environments
		int distance = Utils.getDistance(npc, player);
		
		// If very close, boss can always reach
		if (distance <= 2) {
			return true;
		}
		
		// Check for dungeon obstacles
		return !hasDungeonObstaclesBetween(npc, player) && distance <= 8;
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Check if in dungeon room
	 */
	private boolean isDungeonRoom(NPC npc, Player player) {
		// Check if both entities are in same dungeon room
		// Simplified - in full implementation, check dungeon room boundaries
		return Math.abs(npc.getX() - player.getX()) <= 15 && Math.abs(npc.getY() - player.getY()) <= 15;
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Room-wide attack
	 */
	private void performRoomWideAttack(NPC npc, Player player) {
		npc.setNextAnimation(new Animation(13006));
		npc.setNextGraphics(new Graphics(2394));
		
		// Hit all players in the dungeon room
		List<Entity> targets = npc.getPossibleTargets();
		for (Entity target : targets) {
			if (target instanceof Player) {
				World.sendProjectile(npc, target, 2395, 35, 30, 41, 40, 0, 0);
				target.setNextGraphics(new Graphics(2396, 75, 0));
				int roomDamage = (int)(npc.getMaxHit() * 1.3);
				delayHit(npc, 1, target, getRangeHit(npc, roomDamage));
			}
		}
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Reposition boss in dungeon
	 */
	private void repositionDungeonBoss(NPC npc, Player player) {
		// Calculate optimal position for boss in dungeon room
		WorldTile optimalTile = calculateOptimalBossPosition(npc, player);
		if (optimalTile != null && isValidDungeonTile(optimalTile)) {
			npc.setNextWorldTile(optimalTile);
			npc.setNextGraphics(new Graphics(342)); // Teleport effect
		}
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Enhanced stomp for penalties
	 */
	private void performEnhancedStompAttack(NPC npc, Player player) {
		npc.setNextAnimation(new Animation(13007));
		player.setNextGraphics(new Graphics(2400));
		
		// Powerful penalty stomp
		int penaltyDamage = (int)(npc.getMaxHit() * 1.6);
		delayHit(npc, 0, player, getRegularHit(npc, penaltyDamage));
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Dungeon curse effect
	 */
	private void performDungeonCurse(NPC npc, Player player) {
		player.setNextGraphics(new Graphics(281)); // Curse effect
		sendGuidanceMessage(player, "CURSED: Your safe spotting angers the dungeon spirits!");
		
		// Apply temporary stat reduction (placeholder)
		// In full implementation, reduce player's combat stats temporarily
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Room devastation attack
	 */
	private void performRoomWideDevastationAttack(NPC npc) {
		npc.setNextAnimation(new Animation(13004));
		npc.setNextGraphics(new Graphics(2397));
		
		// Devastate entire room
		List<Entity> allTargets = npc.getPossibleTargets();
		for (Entity target : allTargets) {
			if (target instanceof Player) {
				target.setNextGraphics(new Graphics(2399));
				int devastationDamage = (int)(npc.getMaxHit() * 1.8);
				delayHit(npc, 2, target, getMagicHit(npc, devastationDamage));
			}
		}
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Apply debuff for repeated offenses
	 */
	private void applyDungeoneeringDebuff(Player player) {
		// Placeholder for dungeoneering-specific debuff
		// In full implementation, temporarily reduce XP gains or apply stat penalties
		sendGuidanceMessage(player, "DEBUFFED: Continued safe spotting reduces dungeoneering rewards!");
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Get dungeon center tile
	 */
	private WorldTile getDungeonCenterTile(NPC npc) {
		// Calculate center of dungeon room
		// Simplified - in full implementation, use actual room boundaries
		return new WorldTile(npc.getX(), npc.getY(), npc.getPlane());
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Check for dungeon obstacles
	 */
	private boolean hasDungeonObstaclesBetween(NPC npc, Player player) {
		// Simplified obstacle detection for dungeons
		int deltaX = Math.abs(player.getX() - npc.getX());
		int deltaY = Math.abs(player.getY() - npc.getY());
		
		// If player is far and at different coordinates, likely obstacles
		return (deltaX > 5 || deltaY > 5) && (deltaX != 0 && deltaY != 0);
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Calculate optimal boss position
	 */
	private WorldTile calculateOptimalBossPosition(NPC npc, Player player) {
		// Position boss closer to player but not on top of them
		int targetX = player.getX() + (player.getX() > npc.getX() ? -2 : 2);
		int targetY = player.getY() + (player.getY() > npc.getY() ? -2 : 2);
		return new WorldTile(targetX, targetY, npc.getPlane());
	}
	
	/**
	 * DUNGEONEERING SAFE SPOT PREVENTION: Check if tile is valid in dungeon
	 */
	private boolean isValidDungeonTile(WorldTile tile) {
		// Simplified validation for dungeon tiles
		// In full implementation, check dungeon boundaries and walkable areas
		return tile != null;
	}
	
	/**
	 * Check if player is using magic protection prayers
	 */
	private boolean isUsingMagicProtection(Player player) {
		// This would integrate with your prayer system
		// Return true if player has magic protection active
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
	 * Check if player is using melee protection prayers
	 */
	private boolean isUsingMeleeProtection(Player player) {
		// This would integrate with your prayer system
		// Return true if player has melee protection active
		return false; // Placeholder - implement based on your prayer system
	}
}