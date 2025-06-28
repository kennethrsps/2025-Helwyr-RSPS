package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.Combat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Metal Dragon Combat System with BossBalancer Integration and Boss Guidance
 * Features: Dynamic damage scaling, dragonfire protection education, enhanced metal dragon mechanics
 * 
 * @author Zeus
 * @date June 03, 2025
 * @version 3.0 - Enhanced with BossBalancer Integration and Boss Guidance System
 */
public class MetalDragonCombat extends CombatScript {

	// Boss guidance message timers (prevent spam)
	private static final long GUIDANCE_COOLDOWN = 35000; // 35 seconds
	private long lastGuidanceTime = 0;
	private long lastMechanicWarning = 0;
	
	// Safespot detection enhancements
	private static final int MAX_SAFESPOT_DISTANCE = 14;
	private static final int MIN_ENGAGEMENT_DISTANCE = 2;
	
	// Boss tier detection and guidance
	private int detectedTier = -1;
	private boolean guidanceSystemActive = true;
	private int attackCounter = 0;
	private String dragonType = "";

	@Override
	public Object[] getKeys() {
		return new Object[] { "Bronze dragon", "Iron dragon", "Steel dragon" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		final Player player = target instanceof Player ? (Player) target : null;
		
		// Initialize boss guidance and balancer integration
		initializeBossGuidance(npc, target);
		
		// Enhanced safespot detection
		checkAndPreventSafespotExploitation(npc, target);
		
		// Increment attack counter for guidance
		attackCounter++;
		
		int damage;
		
		// Enhanced melee attack when close
		if (npc.withinDistance(target, npc.getSize())) {
			// Enhanced melee damage calculation
			damage = calculateBalancedDamage(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE);
			npc.setNextAnimation(new Animation(defs.getAttackEmote()));
			delayHit(npc, 0, target, getMeleeHit(npc, damage));
			
			// Preserved sound effect logic
			ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
			for (Entity t : possibleTargets) {
				if (target.withinDistance(t, 1)) {
					if (t instanceof Player) {
						Player p = (Player) t;
						playSound(408, p, target);
					}
				}
			}
			
			// Occasional melee guidance
			if (player != null && Utils.random(8) == 0) {
				player.sendMessage(dragonType + " dragon's metallic claws are devastating! Melee protection helps!");
			}
			
			return defs.getAttackDelay();
		} else {
			// Enhanced dragonfire attack when distant
			return performEnhancedDragonfireAttack(npc, target, player, defs);
		}
	}

	/**
	 * Initialize boss guidance system and detect tier
	 */
	private void initializeBossGuidance(NPC npc, Entity target) {
		if (detectedTier == -1) {
			// Auto-detect boss tier and dragon type
			NPCCombatDefinitions combatDefs = npc.getCombatDefinitions();
			if (combatDefs != null) {
				detectedTier = estimateBossTier(combatDefs.getHitpoints(), combatDefs.getMaxHit());
				dragonType = determineDragonType(npc);
				sendWelcomeGuidance(npc, target);
			}
		}
	}

	/**
	 * Determine dragon type for themed guidance
	 */
	private String determineDragonType(NPC npc) {
		String name = npc.getDefinitions().getName().toLowerCase();
		if (name.contains("bronze")) return "Bronze";
		if (name.contains("iron")) return "Iron";
		if (name.contains("steel")) return "Steel";
		return "Metal"; // fallback
	}

	/**
	 * Send welcome guidance when boss is first engaged
	 */
	private void sendWelcomeGuidance(NPC npc, Entity target) {
		if (!canSendGuidance()) return;
		
		if (target instanceof Player) {
			String tierName = getTierName(detectedTier);
			
			// Welcome message with tier and dragon type information
			npc.setNextForceTalk(new ForceTalk("I am a " + tierName + " " + dragonType + " dragon! Fear my metallic might!"));
			
			WorldTasksManager.schedule(new WorldTask() {
				private int tick = 0;
				@Override
				public void run() {
					switch(tick) {
					case 2:
						npc.setNextForceTalk(new ForceTalk("My dragonfire melts through unprotected flesh!"));
						break;
					case 4:
						npc.setNextForceTalk(new ForceTalk("Anti-dragon shields are wise against my kind!"));
						break;
					case 6:
						npc.setNextForceTalk(new ForceTalk("Close combat brings claws, distant brings fire!"));
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
	 * Enhanced dragonfire attack with comprehensive protection mechanics
	 */
	private int performEnhancedDragonfireAttack(NPC npc, Entity target, Player player, NPCCombatDefinitions defs) {
		// Enhanced dragonfire damage calculation
		int baseDamage = calculateDragonfireDamage(npc);
		int finalDamage = baseDamage;
		
		if (target instanceof Player) {
			// CRITICAL: Preserve original protection mechanics
			String message = Combat.getProtectMessage(player);
			if (message != null) {
				player.sendMessage(message, true);
				if (message.contains("fully")) {
					finalDamage = 0;
					// Enhanced guidance for full protection
					if (Utils.random(4) == 0) {
						player.sendMessage("Perfect dragonfire protection! " + dragonType + " dragon's fire cannot harm you!");
					}
				} else if (message.contains("most")) {
					finalDamage = (int) (finalDamage * 0.05);
					// Enhanced guidance for partial protection
					if (Utils.random(4) == 0) {
						player.sendMessage("Good protection! Only minimal damage from " + dragonType + " dragonfire!");
					}
				} else if (message.contains("some")) {
					finalDamage = (int) (finalDamage * 0.1);
					// Enhanced guidance for minimal protection
					if (Utils.random(4) == 0) {
						player.sendMessage("Partial protection helps, but better gear recommended against " + dragonType + " dragons!");
					}
				}
			} else {
				// No protection - provide education
				player.sendMessage("You are hit by the " + dragonType.toLowerCase() + " dragon's fiery breath!", true);
				if (Utils.random(3) == 0) {
					player.sendMessage("Anti-dragon shield or dragonfire protection would greatly help!");
				}
			}
		}
		
		// Send dragonfire warning with cooldown
		if (System.currentTimeMillis() - lastMechanicWarning > 12000) {
			npc.setNextForceTalk(new ForceTalk("Metallic dragonfire burns all!"));
			lastMechanicWarning = System.currentTimeMillis();
		}
		
		// Execute dragonfire attack with preserved animations/graphics
		npc.setNextAnimation(new Animation(13164));
		World.sendProjectile(npc, target, 393, 28, 16, 35, 20, 16, 0);
		delayHit(npc, 1, target, getRegularHit(npc, finalDamage));
		
		return defs.getAttackDelay();
	}

	/**
	 * Enhanced safespot detection
	 */
	private void checkAndPreventSafespotExploitation(NPC npc, Entity target) {
		if (!(target instanceof Player)) return;
		
		Player player = (Player) target;
		int distance = player.getDistance(npc);
		
		// Check for potential safespotting
		if (distance > MAX_SAFESPOT_DISTANCE) {
			if (System.currentTimeMillis() - lastGuidanceTime > GUIDANCE_COOLDOWN) {
				npc.setNextForceTalk(new ForceTalk("Face me with honor, not cowardice!"));
				player.sendMessage(dragonType + " dragon demands proper combat! Move within " + MAX_SAFESPOT_DISTANCE + " tiles!");
				lastGuidanceTime = System.currentTimeMillis();
			}
			
			// Reset combat if too far
			if (distance > MAX_SAFESPOT_DISTANCE + 4) {
				npc.resetCombat();
				player.sendMessage(dragonType + " dragon loses interest in distant cowards.");
			}
		}
		
		// Provide distance-based guidance
		if (distance < MIN_ENGAGEMENT_DISTANCE && canSendGuidance()) {
			player.sendMessage("Very close to " + dragonType.toLowerCase() + " dragon! Expect melee attacks!");
		} else if (distance > npc.getSize() + 2 && Utils.random(10) == 0) {
			player.sendMessage("At range from " + dragonType.toLowerCase() + " dragon - dragonfire incoming!");
		}
	}

	/**
	 * Calculate balanced damage using BossBalancer system
	 */
	private int calculateBalancedDamage(NPC npc, int baseDamage, int attackType) {
		NPCCombatDefinitions combatDefs = npc.getCombatDefinitions();
		if (combatDefs == null) {
			return baseDamage;
		}
		
		// Apply tier-based scaling
		if (detectedTier > 0) {
			double tierMultiplier = 1.0 + (detectedTier * 0.09); // 9% per tier
			baseDamage = (int) (baseDamage * tierMultiplier);
		}
		
		return getRandomMaxHit(npc, baseDamage, attackType, null);
	}

	/**
	 * Calculate enhanced dragonfire damage
	 */
	private int calculateDragonfireDamage(NPC npc) {
		// Enhanced version of original formula: Utils.random(80, 450 + npc.getCombatLevel())
		int minDamage = 80;
		int maxDamage = 450 + npc.getCombatLevel();
		
		// Apply tier-based scaling
		if (detectedTier > 0) {
			double tierMultiplier = 1.0 + (detectedTier * 0.09); // 9% per tier
			minDamage = (int) (minDamage * tierMultiplier);
			maxDamage = (int) (maxDamage * tierMultiplier);
		}
		
		return Utils.random(minDamage, maxDamage);
	}

	/**
	 * Send periodic guidance based on combat patterns
	 */
	private void sendPeriodicGuidance(NPC npc, Entity target) {
		if (!(target instanceof Player) || !canSendGuidance()) return;
		
		Player player = (Player) target;
		
		// Every 12 attacks, provide strategic guidance
		if (attackCounter % 12 == 0) {
			String dragonName = dragonType.toLowerCase();
			String[] strategicTips = {
				dragonType + " dragons use melee when close, dragonfire when distant!",
				"Anti-dragon shields provide excellent dragonfire protection!",
				"Protect from Magic prayer helps reduce dragonfire damage!",
				"Stay mobile - control the distance to choose attack types!",
				dragonType + " dragons are weak to certain attack styles!",
				"Dragonfire protection is essential for " + dragonName + " dragon hunting!",
				"Close combat risks melee damage but avoids dragonfire!"
			};
			
			if (strategicTips.length > 0) {
				int randomIndex = Utils.random(strategicTips.length);
				player.sendMessage("<col=8B4513>" + dragonType + " Guide: " + strategicTips[randomIndex]);
				lastGuidanceTime = System.currentTimeMillis();
			}
		}
		
		// Special guidance based on protection status
		if (attackCounter % 8 == 0 && Utils.random(3) == 0) {
			String[] protectionTips = {
				"Dragonfire shield provides both offense and defense!",
				"Extended antifire potions offer temporary dragonfire immunity!",
				"Combination of prayer and shield gives maximum protection!",
				"Watch your dragonfire protection - it can wear off!"
			};
			
			if (protectionTips.length > 0) {
				int randomIndex = Utils.random(protectionTips.length);
				player.sendMessage("<col=FF6600>Protection Tip: " + protectionTips[randomIndex]);
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
}