package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.others.TormentedDemon;
import com.rs.game.player.Player;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Tormented Demon Combat System with BossBalancer Integration and Guidance System
 * 
 * @author Zeus
 * @date June 02, 2025
 * @version 3.0 - Integrated with BossBalancer v2.2 and Tormented Demon Guidance System
 */
public class TormentedDemonCombat extends CombatScript {

	// Boss guidance system variables
	private static final int GUIDANCE_COOLDOWN = 14000; // 14 seconds between guidance messages
	private long lastGuidanceTime = 0;
	private int guidancePhase = 0;
	
	// Tormented Demon specific tracking
	private boolean hasWarnedAboutStyles = false;
	private boolean hasWarnedAboutPrayers = false;
	private int stylesSwitched = 0;
	
	// Safe spot prevention system
	private static final int DEMON_SAFE_SPOT_CHECK = 3; // Check every 3 attacks
	private int consecutiveRangedAttacks = 0;
	private int safeSpotAttempts = 0;
	private boolean lastAttackBlocked = false;

	@Override
	public int attack(NPC npc, Entity target) {
		TormentedDemon demon = (TormentedDemon) npc;
		
		// BOSS BALANCER INTEGRATION: Get dynamic boss stats
		int dynamicMaxHit = getBossBalancerMaxHit(npc);
		int bossType = getBossType(npc);
		int bossTier = getBossTier(npc);
		
		// Boss Guidance System: Provide contextual advice
		provideTormentedDemonGuidance(npc, target, bossType, bossTier, demon);
		
		// SAFE SPOT PREVENTION: Check for and counter safe spotting
		checkTormentedDemonSafeSpotting(npc, target, bossType, bossTier, demon);
		
		// Enhanced combat style selection with BossBalancer integration
		boolean isDistanced = !Utils.isOnRange(npc, target, 0);
		int style = selectEnhancedCombatStyle(demon, target, isDistanced, bossType, bossTier);
		
		// Perform attack based on selected style
		performTormentedDemonAttack(npc, target, style, dynamicMaxHit, bossTier);
		
		// Update demon's combat tracking
		updateDemonCombatState(demon, style);
		
		return getBalancedAttackDelay(npc, bossType, bossTier);
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Enhanced combat style selection
	 */
	private int selectEnhancedCombatStyle(TormentedDemon demon, Entity target, boolean isDistanced, int bossType, int bossTier) {
		// Reset to random style if fixed amount is depleted
		if (isDistanced && demon.getFixedCombatType() == 0) {
			demon.setFixedAmount(0);
		}
		
		if (demon.getFixedAmount() == 0) {
			int newStyle = chooseAdaptiveStyle(demon, target, isDistanced, bossTier);
			demon.setFixedCombatType(newStyle);
			
			// Higher tier demons stick to styles longer
			int fixedAmount = getStyleDuration(bossTier);
			demon.setFixedAmount(fixedAmount);
			
			stylesSwitched++;
		}
		
		return demon.getFixedCombatType();
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Choose adaptive combat style based on tier
	 */
	private int chooseAdaptiveStyle(TormentedDemon demon, Entity target, boolean isDistanced, int bossTier) {
		// Higher tier demons adapt to player behavior
		if (bossTier >= 6 && target instanceof Player) {
			Player player = (Player) target;
			
			// Advanced demons counter player prayers
			if (isUsingMeleeProtection(player) && !isDistanced) {
				return Utils.random(2) + 1; // Switch to magic or ranged
			} else if (isUsingMagicProtection(player)) {
				return isDistanced ? 2 : Utils.random(2) == 0 ? 0 : 2; // Prefer melee or ranged
			} else if (isUsingRangedProtection(player)) {
				return isDistanced ? 1 : Utils.random(2) == 0 ? 0 : 1; // Prefer melee or magic
			}
		}
		
		// Standard style selection
		if (isDistanced) {
			return Utils.random(1, 3); // Magic or ranged when distanced
		} else {
			return Utils.random(0, 3); // All styles when close
		}
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Get style duration based on tier
	 */
	private int getStyleDuration(int bossTier) {
		switch (bossTier) {
			case 1: case 2: case 3: return Utils.random(2, 4); // Shorter for beginners
			case 4: case 5: return Utils.random(3, 6);         // Standard duration
			case 6: case 7: return Utils.random(4, 8);         // Longer for masters
			case 8: case 9: return Utils.random(5, 10);        // Extended for legends
			case 10: return Utils.random(6, 12);               // Maximum for divine
			default: return Utils.random(3, 7);
		}
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Perform enhanced attack based on style
	 */
	private void performTormentedDemonAttack(NPC npc, Entity target, int style, int dynamicMaxHit, int bossTier) {
		switch (style) {
			case 0: // Melee Attack
				performEnhancedMeleeAttack(npc, target, dynamicMaxHit, bossTier);
				break;
			case 1: // Magic Attack
				performEnhancedMagicAttack(npc, target, dynamicMaxHit, bossTier);
				break;
			case 2: // Ranged Attack
				performEnhancedRangedAttack(npc, target, dynamicMaxHit, bossTier);
				consecutiveRangedAttacks++;
				break;
		}
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Enhanced melee attack
	 */
	private void performEnhancedMeleeAttack(NPC npc, Entity target, int dynamicMaxHit, int bossTier) {
		// Enhanced animations for higher tiers
		npc.setNextAnimation(new Animation(10922));
		npc.setNextGraphics(new Graphics(1886));
		
		// Boss guidance for melee phase
		if (target instanceof Player && shouldShowGuidance()) {
			sendBossMessage((Player) target, "The demon's claws glow with unholy energy! Melee combat engaged!");
			if (bossTier >= 5) {
				sendGuidanceMessage((Player) target, "DEMON TIP: Use Protect from Melee or maintain distance!");
			}
		}
		
		boolean hit = delayEnhancedHit(npc, target, NPCCombatDefinitions.MELEE, 0, dynamicMaxHit, bossTier);
		lastAttackBlocked = !hit;
		consecutiveRangedAttacks = 0; // Reset ranged counter
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Enhanced magic attack
	 */
	private void performEnhancedMagicAttack(NPC npc, Entity target, int dynamicMaxHit, int bossTier) {
		npc.setNextAnimation(new Animation(10918));
		npc.setNextGraphics(new Graphics(1883, 0, 96 << 16));
		World.sendProjectile(npc, target, 1884, 34, 16, 30, 35, 16, 0);
		
		// Boss guidance for magic phase
		if (target instanceof Player && shouldShowGuidance()) {
			sendBossMessage((Player) target, "Dark magic courses through the demon! Arcane assault incoming!");
			if (bossTier >= 7) {
				sendGuidanceMessage((Player) target, "ELITE TIP: Higher tier demons have devastating magic attacks!");
			}
		}
		
		boolean hit = delayEnhancedHit(npc, target, NPCCombatDefinitions.MAGE, 1, dynamicMaxHit, bossTier);
		if (hit) {
			target.setNextGraphics(new Graphics(1883, 1, 100));
		}
		lastAttackBlocked = !hit;
		consecutiveRangedAttacks = 0;
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Enhanced ranged attack
	 */
	private void performEnhancedRangedAttack(NPC npc, Entity target, int dynamicMaxHit, int bossTier) {
		npc.setNextAnimation(new Animation(10919));
		npc.setNextGraphics(new Graphics(1888));
		World.sendProjectile(npc, target, 1887, 34, 16, 30, 35, 16, 0);
		
		// Boss guidance for ranged phase
		if (target instanceof Player && shouldShowGuidance()) {
			sendBossMessage((Player) target, "The demon hurls demonic projectiles! Ranged barrage incoming!");
			if (bossTier >= 6 && consecutiveRangedAttacks > 2) {
				sendGuidanceMessage((Player) target, "PATTERN ALERT: Multiple ranged attacks detected!");
			}
		}
		
		boolean hit = delayEnhancedHit(npc, target, NPCCombatDefinitions.RANGE, 1, dynamicMaxHit, bossTier);
		lastAttackBlocked = !hit;
	}
	
	/**
	 * TORMENTED DEMON SAFE SPOT PREVENTION: Specialized anti-safe spot system
	 */
	private void checkTormentedDemonSafeSpotting(NPC npc, Entity target, int bossType, int bossTier, TormentedDemon demon) {
		if (!(target instanceof Player)) {
			return;
		}
		
		Player player = (Player) target;
		
		// Detect potential safe spotting patterns
		boolean potentialSafeSpot = detectDemonSafeSpotting(npc, player, demon);
		
		if (potentialSafeSpot) {
			safeSpotAttempts++;
			
			// Immediate response for tormented demons
			if (safeSpotAttempts >= 2) {
				performDemonAntiSafeSpotMeasure(npc, player, bossTier, demon);
				safeSpotAttempts = 0;
			}
		} else {
			// Reset when fighting fairly
			if (safeSpotAttempts > 0) {
				safeSpotAttempts = 0;
				sendGuidanceMessage(player, "HONORABLE COMBAT: The demon respects your courage!");
			}
		}
	}
	
	/**
	 * TORMENTED DEMON SAFE SPOT PREVENTION: Detect safe spotting patterns
	 */
	private boolean detectDemonSafeSpotting(NPC npc, Player player, TormentedDemon demon) {
		// Tormented demons are particularly vulnerable to ranged safe spotting
		boolean playerDistanced = !Utils.isOnRange(npc, player, 0);
		boolean demonStuckOnRanged = consecutiveRangedAttacks > 4;
		boolean playerInAttackRange = isPlayerAttackingBoss(player, npc);
		boolean recentlyBlocked = lastAttackBlocked;
		
		// If player is distant, demon keeps using ranged, and attacks are blocked, likely safe spotting
		return playerDistanced && demonStuckOnRanged && playerInAttackRange && recentlyBlocked;
	}
	
	/**
	 * TORMENTED DEMON SAFE SPOT PREVENTION: Apply anti-safe spot measures
	 */
	private void performDemonAntiSafeSpotMeasure(NPC npc, Player player, int bossTier, TormentedDemon demon) {
		sendBossMessage(player, "The demon's fury transcends physical barriers!");
		
		switch (bossTier) {
			case 1: case 2: case 3: // Basic demons
				performBasicDemonCounter(npc, player, demon);
				break;
			case 4: case 5: // Advanced demons
				performAdvancedDemonCounter(npc, player, demon);
				break;
			case 6: case 7: case 8: // Master demons
				performMasterDemonCounter(npc, player, demon);
				break;
			case 9: case 10: // Legendary demons
				performLegendaryDemonCounter(npc, player, demon);
				break;
		}
	}
	
	/**
	 * TORMENTED DEMON SAFE SPOT PREVENTION: Basic demon counter
	 */
	private void performBasicDemonCounter(NPC npc, Player player, TormentedDemon demon) {
		sendGuidanceMessage(player, "BASIC COUNTER: The demon adapts its strategy!");
		
		// Force magic attack that ignores some protection
		npc.setNextAnimation(new Animation(10918));
		npc.setNextGraphics(new Graphics(1883, 0, 96 << 16));
		World.sendProjectile(npc, player, 1884, 34, 16, 30, 35, 16, 0);
		
		// Reduced protection effectiveness
		int damage = Utils.random((int)(npc.getMaxHit() * 1.2));
		if (isUsingMagicProtection(player)) {
			damage = (int)(damage * 0.3); // Partial protection instead of full
		}
		
		player.applyHit(new Hit(npc, damage, HitLook.MAGIC_DAMAGE, 1));
		player.setNextGraphics(new Graphics(1883, 1, 100));
	}
	
	/**
	 * TORMENTED DEMON SAFE SPOT PREVENTION: Advanced demon counter
	 */
	private void performAdvancedDemonCounter(NPC npc, Player player, TormentedDemon demon) {
		sendGuidanceMessage(player, "ADVANCED COUNTER: The demon teleports closer!");
		
		// Demon repositioning
		WorldTile playerTile = new WorldTile(player.getX(), player.getY(), player.getPlane());
		WorldTile newDemonPos = findAdvancedDemonPosition(npc, playerTile);
		
		if (newDemonPos != null) {
			npc.setNextWorldTile(newDemonPos);
			npc.setNextGraphics(new Graphics(342)); // Teleport effect
			
			// Force style reset for immediate adaptation
			demon.setFixedAmount(0);
			demon.setFixedCombatType(0); // Reset to melee for close combat
		}
	}
	
	/**
	 * TORMENTED DEMON SAFE SPOT PREVENTION: Master demon counter
	 */
	private void performMasterDemonCounter(NPC npc, Player player, TormentedDemon demon) {
		sendBossMessage(player, "Master-level demons bend reality to their will!");
		sendGuidanceMessage(player, "MASTER COUNTER: Prayer protection is weakened!");
		
		// Multi-hit attack that partially bypasses protection
		performDemonFuryAttack(npc, player);
		
		// Temporary prayer disruption effect
		applyPrayerDisruption(player);
	}
	
	/**
	 * TORMENTED DEMON SAFE SPOT PREVENTION: Legendary demon counter
	 */
	private void performLegendaryDemonCounter(NPC npc, Player player, TormentedDemon demon) {
		sendBossMessage(player, "Legendary demons command the very fabric of combat!");
		sendGuidanceMessage(player, "LEGENDARY COUNTER: Ultimate demon abilities activated!");
		
		// Teleport player to demon for forced melee combat
		WorldTile demonTile = new WorldTile(npc.getX() + 1, npc.getY() + 1, npc.getPlane());
		player.setNextWorldTile(demonTile);
		player.setNextGraphics(new Graphics(342));
		
		// Devastating combo attack
		performLegendaryComboAttack(npc, player, demon);
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Get dynamic max hit
	 */
	private int getBossBalancerMaxHit(NPC npc) {
		try {
			return npc.getCombatDefinitions().getMaxHit();
		} catch (Exception e) {
			return 210; // Fallback to original value
		}
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Determine boss type (Hybrid for Tormented Demon)
	 */
	private int getBossType(NPC npc) {
		// Tormented Demons are Hybrid bosses (use all three combat styles)
		return 4; // Hybrid Boss
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Estimate boss tier
	 */
	private int getBossTier(NPC npc) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int hp = defs.getHitpoints();
		int maxHit = defs.getMaxHit();
		
		// Tormented Demons are typically mid-to-high tier bosses
		if (hp <= 3200 && maxHit <= 50) return 3;          // Intermediate
		else if (hp <= 6000 && maxHit <= 80) return 4;     // Advanced
		else if (hp <= 10500 && maxHit <= 125) return 5;   // Expert
		else if (hp <= 17000 && maxHit <= 185) return 6;   // Master
		else if (hp <= 25500 && maxHit <= 260) return 7;   // Elite
		else if (hp <= 36000 && maxHit <= 350) return 8;   // Legendary
		else if (hp <= 50000 && maxHit <= 460) return 9;   // Mythical
		else return 10;                                     // Divine
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Get balanced attack delay
	 */
	private int getBalancedAttackDelay(NPC npc, int bossType, int bossTier) {
		int baseDelay = npc.getCombatDefinitions().getAttackDelay();
		
		// Hybrid bosses (type 4) get slight delay variations
		if (bossType == 4) {
			// Higher tier hybrids are more unpredictable
			if (bossTier >= 7) {
				return baseDelay + Utils.random(3) - 1; // -1 to +1 variation
			}
		}
		
		return baseDelay;
	}
	
	/**
	 * TORMENTED DEMON GUIDANCE SYSTEM: Provide contextual advice
	 */
	private void provideTormentedDemonGuidance(NPC npc, Entity target, int bossType, int bossTier, TormentedDemon demon) {
		if (!(target instanceof Player) || !shouldShowGuidance()) {
			return;
		}
		
		Player player = (Player) target;
		long currentTime = System.currentTimeMillis();
		
		// Initial encounter guidance
		if (guidancePhase == 0) {
			sendBossMessage(player, "A " + getBossTierName(bossTier) + " Tormented Demon blocks your path!");
			sendGuidanceMessage(player, "DEMON TIP: This hybrid boss switches between all three combat styles!");
			guidancePhase = 1;
			lastGuidanceTime = currentTime;
			return;
		}
		
		// Style switching guidance
		if (stylesSwitched >= 2 && !hasWarnedAboutStyles) {
			sendBossMessage(player, "The demon adapts its combat approach!");
			sendGuidanceMessage(player, "PATTERN RECOGNITION: It uses each style for several attacks before switching!");
			hasWarnedAboutStyles = true;
			lastGuidanceTime = currentTime;
		}
		
		// Prayer protection guidance
		if (lastAttackBlocked && !hasWarnedAboutPrayers) {
			sendGuidanceMessage(player, "PROTECTION SUCCESS: Your prayers are effective against this demon!");
			if (bossTier >= 6) {
				sendGuidanceMessage(player, "HIGH TIER WARNING: Master+ demons may adapt to your prayers!");
			}
			hasWarnedAboutPrayers = true;
			lastGuidanceTime = currentTime;
		}
		
		// Health-based guidance
		double healthPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
		
		if (healthPercent <= 0.5 && guidancePhase == 1) {
			sendBossMessage(player, "The demon's torment intensifies as its strength fades!");
			sendGuidanceMessage(player, "HALF HEALTH: Wounded demons become more aggressive!");
			guidancePhase = 2;
			lastGuidanceTime = currentTime;
		} else if (healthPercent <= 0.25 && guidancePhase == 2) {
			sendBossMessage(player, "Desperate fury consumes the tormented soul!");
			sendGuidanceMessage(player, "FINAL PHASE: Victory approaches - maintain your strategy!");
			guidancePhase = 3;
			lastGuidanceTime = currentTime;
		}
		
		// Tier-specific advanced guidance
		if (bossTier >= 7 && currentTime - lastGuidanceTime > GUIDANCE_COOLDOWN) {
			provideTierSpecificDemonGuidance(player, bossTier);
			lastGuidanceTime = currentTime;
		}
	}
	
	/**
	 * TORMENTED DEMON GUIDANCE SYSTEM: Tier-specific guidance
	 */
	private void provideTierSpecificDemonGuidance(Player player, int bossTier) {
		switch (bossTier) {
			case 7: // Elite
				sendGuidanceMessage(player, "ELITE DEMON: This demon can counter your protection prayers!");
				break;
			case 8: // Legendary
				sendGuidanceMessage(player, "LEGENDARY DEMON: Expect unpredictable attack patterns!");
				break;
			case 9: // Mythical
				sendGuidanceMessage(player, "MYTHICAL DEMON: Only master combatants can triumph here!");
				break;
			case 10: // Divine
				sendGuidanceMessage(player, "DIVINE DEMON: You face the ultimate tormented soul!");
				break;
		}
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Enhanced hit calculation with tier scaling
	 */
	private boolean delayEnhancedHit(NPC npc, Entity target, int style, int endTime, int maxHit, int bossTier) {
		// Calculate damage based on BossBalancer max hit
		int damage = Utils.random(maxHit + 1);
		
		if (target instanceof Player) {
			Player player = (Player) target;
			boolean negateDamage = false;
			
			// Check prayer protection
			negateDamage |= isUsingMeleeProtection(player) && style == NPCCombatDefinitions.MELEE;
			negateDamage |= isUsingRangedProtection(player) && style == NPCCombatDefinitions.RANGE;
			negateDamage |= isUsingMagicProtection(player) && style == NPCCombatDefinitions.MAGE;
			
			// Higher tier demons have prayer bypass chance
			if (negateDamage && bossTier >= 7) {
				int bypassChance = getBossTierPrayerBypassChance(bossTier);
				if (Utils.random(100) < bypassChance) {
					damage = (int)(damage * 0.3); // Partial damage instead of full negation
					negateDamage = false;
					
					if (shouldShowGuidance()) {
						sendGuidanceMessage(player, "PRAYER BYPASS: Elite+ demons can partially bypass protection!");
					}
				}
			}
			
			if (negateDamage) {
				damage = 0;
			}
		}
		
		// Apply hit with appropriate damage type
		HitLook hitType = style == NPCCombatDefinitions.MELEE ? HitLook.MELEE_DAMAGE : 
				          style == NPCCombatDefinitions.RANGE ? HitLook.RANGE_DAMAGE : HitLook.MAGIC_DAMAGE;
		
		target.applyHit(new Hit(npc, damage, hitType, endTime));
		return damage != 0;
	}
	
	/**
	 * BOSS BALANCER INTEGRATION: Get prayer bypass chance based on tier
	 */
	private int getBossTierPrayerBypassChance(int bossTier) {
		switch (bossTier) {
			case 7: return 10;  // Elite: 10% bypass chance
			case 8: return 15;  // Legendary: 15% bypass chance
			case 9: return 20;  // Mythical: 20% bypass chance
			case 10: return 25; // Divine: 25% bypass chance
			default: return 0;
		}
	}
	
	/**
	 * Update demon combat state tracking
	 */
	private void updateDemonCombatState(TormentedDemon demon, int style) {
		demon.setFixedAmount(demon.getFixedAmount() - 1);
		
		// Reset consecutive ranged counter for other styles
		if (style != 2) {
			consecutiveRangedAttacks = 0;
		}
	}
	
	/**
	 * SAFE SPOT PREVENTION: Helper methods for advanced counters
	 */
	private WorldTile findAdvancedDemonPosition(NPC npc, WorldTile playerTile) {
		// Find position 2-3 tiles away from player for fair combat
		for (int distance = 2; distance <= 3; distance++) {
			for (int x = -distance; x <= distance; x++) {
				for (int y = -distance; y <= distance; y++) {
					WorldTile testTile = new WorldTile(playerTile.getX() + x, playerTile.getY() + y, npc.getPlane());
					if (isValidDemonPosition(testTile, npc)) {
						return testTile;
					}
				}
			}
		}
		return null;
	}
	
	private void performDemonFuryAttack(NPC npc, Player player) {
		// Multi-style fury attack
		npc.setNextAnimation(new Animation(10922));
		npc.setNextGraphics(new Graphics(1886));
		
		// Triple hit combo with reduced prayer effectiveness
		for (int i = 0; i < 3; i++) {
			final int hitDelay = i;
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					int damage = Utils.random((int)(npc.getMaxHit() * 0.8));
					// Reduced prayer protection for fury attack
					if (isUsingMeleeProtection(player)) {
						damage = (int)(damage * 0.4);
					}
					player.applyHit(new Hit(npc, damage, HitLook.MELEE_DAMAGE, 0));
				}
			}, hitDelay);
		}
	}
	
	private void performLegendaryComboAttack(NPC npc, Player player, TormentedDemon demon) {
		// Ultimate combo: Magic -> Ranged -> Melee
		npc.setNextAnimation(new Animation(10918));
		npc.setNextGraphics(new Graphics(1883, 0, 96 << 16));
		
		// Combo sequence
		WorldTasksManager.schedule(new WorldTask() {
			private int phase = 0;
			
			@Override
			public void run() {
				switch (phase) {
					case 0: // Magic
						World.sendProjectile(npc, player, 1884, 34, 16, 30, 35, 16, 0);
						int magicDamage = Utils.random((int)(npc.getMaxHit() * 1.1));
						player.applyHit(new Hit(npc, magicDamage, HitLook.MAGIC_DAMAGE, 0));
						player.setNextGraphics(new Graphics(1883, 1, 100));
						break;
					case 1: // Ranged
						npc.setNextAnimation(new Animation(10919));
						World.sendProjectile(npc, player, 1887, 34, 16, 30, 35, 16, 0);
						int rangedDamage = Utils.random((int)(npc.getMaxHit() * 1.0));
						player.applyHit(new Hit(npc, rangedDamage, HitLook.RANGE_DAMAGE, 0));
						break;
					case 2: // Melee
						npc.setNextAnimation(new Animation(10922));
						int meleeDamage = Utils.random((int)(npc.getMaxHit() * 1.2));
						player.applyHit(new Hit(npc, meleeDamage, HitLook.MELEE_DAMAGE, 0));
						this.stop();
						return;
				}
				phase++;
			}
		}, 0, 2); // Execute every 2 ticks
	}
	
	private void applyPrayerDisruption(Player player) {
		// Placeholder for prayer disruption effect
		// In full implementation, temporarily disable or reduce prayer effectiveness
		player.setNextGraphics(new Graphics(281)); // Disruption effect
	}
	
	private boolean isValidDemonPosition(WorldTile tile, NPC npc) {
		// Basic validation for demon positioning
		return tile != null && Utils.getDistance(tile, npc) >= 1;
	}
	
	/**
	 * Prayer protection checking methods
	 */
	private boolean isUsingMeleeProtection(Player player) {
		try {
			return player.getPrayer().isMeleeProtecting();
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean isUsingRangedProtection(Player player) {
		try {
			return player.getPrayer().isRangeProtecting();
		} catch (Exception e) {
			return false;
		}
	}
	
	private boolean isUsingMagicProtection(Player player) {
		try {
			return player.getPrayer().isMageProtecting();
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Check if player is attacking the boss
	 */
	private boolean isPlayerAttackingBoss(Player player, NPC npc) {
		try {
			int distance = Utils.getDistance(player, npc);
			return distance <= 15 && player.withinDistance(npc);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * BOSS GUIDANCE SYSTEM: Helper methods
	 */
	private boolean shouldShowGuidance() {
		return System.currentTimeMillis() - lastGuidanceTime > GUIDANCE_COOLDOWN;
	}
	
	private void sendBossMessage(Player player, String message) {
		player.sendMessage("<col=cc0000>[Tormented Demon]: " + message);
	}
	
	private void sendGuidanceMessage(Player player, String message) {
		player.sendMessage("<col=ff9900>[Demon Guide]: " + message);
	}
	
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

	@Override
	public Object[] getKeys() {
		return new Object[] { "Tormented demon" };
	}
}