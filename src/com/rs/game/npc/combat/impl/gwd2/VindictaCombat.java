package com.rs.game.npc.combat.impl.gwd2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.NewProjectile;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.gwd2.vindicta.Vindicta;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.cache.loaders.NPCDefinitions;

/**
 * Enhanced Vindicta Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Dynamic damage scaling, HP-aware damage limits, intelligent boss guidance, enhanced mechanics
 * Integrated with BossBalancer v5.0 for comprehensive combat scaling with player safety
 * 
 * @author Zeus
 * @date June 10, 2025
 * @version 5.0 - COMPLETE BossBalancer v5.0 Integration with HP-Aware Damage System
 */
public class VindictaCombat extends CombatScript {

	// ===== BOSS GUIDANCE AND TRACKING SYSTEMS =====
	private static final long GUIDANCE_COOLDOWN = 45000L; // 45 seconds between guidance
	private static final long MECHANIC_WARNING_COOLDOWN = 15000L; // 15 seconds between mechanic warnings
	private static final long SCALING_UPDATE_INTERVAL = 30000L; // 30 seconds for scaling updates
	private static final int MAX_WARNINGS_PER_FIGHT = 4; // Increased for v5.0 features
	
	// Combat session tracking
	private static final Map<Integer, Long> lastGuidanceTime = new ConcurrentHashMap<Integer, Long>();
	private static final Map<Integer, Long> lastMechanicWarning = new ConcurrentHashMap<Integer, Long>();
	private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
	private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
	private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
	
	// ===== HP-AWARE DAMAGE SCALING CONSTANTS - CRITICAL SAFETY SYSTEM =====
	private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.35; // Max 35% of player HP per hit (dragon rider balanced)
	private static final double HURRICANE_DAMAGE_PERCENT = 0.50; // Max 50% for hurricane (ultimate attack)
	private static final double RANGED_DAMAGE_PERCENT = 0.30; // Max 30% for ranged attacks
	private static final double DRAGONFIRE_DAMAGE_PERCENT = 0.40; // Max 40% for dragonfire attacks
	private static final double SLICE_DAMAGE_PERCENT = 0.25; // Max 25% for basic slice attacks
	private static final int MIN_PLAYER_HP = 990; // Minimum expected player HP
	private static final int MAX_PLAYER_HP = 1500; // Maximum expected player HP
	private static final int ABSOLUTE_MAX_DAMAGE = 525; // Hard cap (35% of 1500 HP)
	private static final int MINIMUM_DAMAGE = 25; // Minimum damage to prevent 0 hits
	
	// ===== ENHANCED SAFESPOT DETECTION =====
	private static final int MAX_SAFESPOT_DISTANCE = 15;
	private static final int MIN_ENGAGEMENT_DISTANCE = 3;
	private static final int FORCE_RESET_DISTANCE = 20;
	private static final Map<Integer, Integer> safespotWarnings = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Integer> consecutiveAvoids = new ConcurrentHashMap<Integer, Integer>();
	
	// Attack type constants
	private static final int ATTACK_TYPE_MELEE = 0;
	private static final int ATTACK_TYPE_RANGE = 1;
	private static final int ATTACK_TYPE_MAGIC = 2;

	@Override
	public Object[] getKeys() {
		return new Object[] { 22459, 22460, 22461, 22462 }; // All Vindicta forms
	}

	@Override
	public int attack(NPC npc, Entity target) {
		// **CRITICAL FIX**: Verify NPC is actually Vindicta before casting
		if (!(npc instanceof Vindicta)) {
			System.err.println("Warning: VindictaCombat received non-Vindicta NPC: " + npc.getClass().getSimpleName());
			return executeBasicAttack(npc, target);
		}
		
		Vindicta vindicta = (Vindicta) npc;
		
		// Enhanced null safety checks
		if (target == null || vindicta.isDead() || target.isDead() || !(target instanceof Player)) {
			return 4;
		}
		
		Player player = (Player) target;
		
		try {
			// ===== FULL BOSSBALANCER v5.0 INTEGRATION =====
			
			// Initialize combat session if needed
			initializeVindictaCombatSession(player, vindicta);
			
			// Get INTELLIGENT combat scaling v5.0
			CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, vindicta);
			
			// Enhanced guidance system with intelligent scaling awareness
			provideIntelligentVindictaGuidance(player, vindicta, scaling);
			
			// Monitor scaling changes during combat
			monitorVindictaScalingChanges(player, scaling);
			
			// Enhanced phase progression with guidance
			vindicta.nextPhase();
			final int phase = vindicta.getPhase();
			final int players = getPlayerCount(vindicta);
			
			if (vindicta.getPhase() < 0 || vindicta.getPhase() > getMaxPhase(vindicta, players)) {
				vindicta.setPhase(0);
			}
			
			// Provide phase transition guidance with v5.0 scaling context
			sendPhaseGuidance(vindicta, target, phase, scaling);
			
			// Enhanced safespot detection with dragon rider theme
			checkAndPreventDragonRiderSafespotExploitation(vindicta, player, scaling);
			
			// Execute attack with v5.0 scaling and HP-aware damage
			if (isGroundPhase(npc)) {
				return executeIntelligentGroundPhaseAttack(vindicta, player, phase, players, scaling);
			} else {
				return executeIntelligentAirPhaseAttack(vindicta, player, phase, scaling);
			}
			
		} catch (Exception e) {
			System.err.println("Error in VindictaCombat.attack(): " + e.getMessage());
			e.printStackTrace();
			return executeBasicAttack(npc, target);
		}
	}

	/**
	 * Initialize Vindicta combat session using BossBalancer v5.0
	 */
	private void initializeVindictaCombatSession(Player player, Vindicta vindicta) {
		Integer sessionKey = Integer.valueOf(player.getIndex());
		
		if (!combatSessionActive.containsKey(sessionKey)) {
			// Start BossBalancer v5.0 combat session
			BossBalancer.startCombatSession(player, vindicta);
			combatSessionActive.put(sessionKey, Boolean.TRUE);
			attackCounter.put(sessionKey, Integer.valueOf(0));
			lastScalingType.put(sessionKey, "UNKNOWN");
			safespotWarnings.put(sessionKey, Integer.valueOf(0));
			consecutiveAvoids.put(sessionKey, Integer.valueOf(0));
			warningStage.put(sessionKey, Integer.valueOf(0));
			
			// Send v5.0 enhanced dragon rider combat message
			CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, vindicta);
			String welcomeMsg = getIntelligentVindictaWelcomeMessage(scaling, vindicta);
			player.sendMessage(welcomeMsg);
			
			// Perform initial armor analysis for dragon rider combat
			performInitialDragonRiderArmorAnalysis(player);
		}
	}

	/**
	 * NEW v5.0: Perform initial dragon rider armor analysis
	 */
	private void performInitialDragonRiderArmorAnalysis(Player player) {
		try {
			// Use BossBalancer v5.0 armor analysis
			BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
			
			if (!armorResult.hasFullArmor) {
				player.sendMessage("<col=ff6600>Dragon Rider Analysis: Missing armor leaves you vulnerable to dragonfire!</col>");
			} else {
				double reductionPercent = armorResult.damageReduction * 100;
				player.sendMessage("<col=00ff00>Dragon Rider Analysis: Full protection detected (" + 
								 String.format("%.1f", reductionPercent) + 
								 "% damage reduction). Ready for dragon combat!</col>");
			}
		} catch (Exception e) {
			// Ignore armor analysis errors
		}
	}

	/**
	 * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from dragon attacks
	 */
	private int applyHPAwareVindictaDamageScaling(int scaledDamage, Player player, String attackType) {
		if (player == null) {
			return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
		}
		
		try {
			int currentHP = player.getHitpoints();
			int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
			
			// Use current HP for calculation (dragon rider attacks are balanced but powerful)
			int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
			
			// Determine damage cap based on dragon rider attack type
			double damagePercent;
			switch (attackType.toLowerCase()) {
				case "hurricane":
				case "ultimate_hurricane":
					damagePercent = HURRICANE_DAMAGE_PERCENT;
					break;
				case "dragonfire":
				case "dragonfire_wall":
				case "second_dragonfire":
					damagePercent = DRAGONFIRE_DAMAGE_PERCENT;
					break;
				case "ranged":
				case "dragonfire_blast":
					damagePercent = RANGED_DAMAGE_PERCENT;
					break;
				case "slice":
				case "basic_attack":
				default:
					damagePercent = SLICE_DAMAGE_PERCENT;
					break;
			}
			
			// Calculate HP-based damage cap
			int hpBasedCap = (int)(effectiveHP * damagePercent);
			
			// Apply multiple safety caps
			int safeDamage = Math.min(scaledDamage, hpBasedCap);
			safeDamage = Math.min(safeDamage, ABSOLUTE_MAX_DAMAGE);
			safeDamage = Math.max(safeDamage, MINIMUM_DAMAGE);
			
			// Additional safety check - never deal more than 75% of current HP for dragon rider
			if (currentHP > 0) {
				int emergencyCap = (int)(currentHP * 0.75);
				safeDamage = Math.min(safeDamage, emergencyCap);
			}
			
			return safeDamage;
			
		} catch (Exception e) {
			// Fallback to absolute cap
			return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
		}
	}

	/**
	 * NEW v5.0: Send HP warning if player is in danger from dragon attacks
	 */
	private void checkAndWarnLowHPForDragonRider(Player player, int incomingDamage, String attackType) {
		if (player == null) return;
		
		try {
			int currentHP = player.getHitpoints();
			
			// Warn if incoming dragon damage is significant relative to current HP
			if (currentHP > 0) {
				double damagePercent = (double)incomingDamage / currentHP;
				
				if (damagePercent >= 0.60) {
					player.sendMessage("<col=ff0000>DRAGON WARNING: " + attackType + " will deal " + incomingDamage + 
									 " damage! (" + currentHP + " HP remaining)</col>");
				} else if (damagePercent >= 0.40) {
					player.sendMessage("<col=ff6600>DRAGON WARNING: Heavy damage incoming (" + incomingDamage + 
									 " from " + attackType + ")! Consider healing (" + currentHP + " HP)</col>");
				}
			}
		} catch (Exception e) {
			// Ignore warning errors
		}
	}

	/**
	 * ENHANCED v5.0: Generate intelligent Vindicta welcome message based on power analysis
	 */
	private String getIntelligentVindictaWelcomeMessage(CombatScaling scaling, Vindicta vindicta) {
		StringBuilder message = new StringBuilder();
		
		// Get NPC name for personalized message
		NPCDefinitions def = NPCDefinitions.getNPCDefinitions(vindicta.getId());
		String npcName = (def != null && def.getName() != null) ? def.getName() : "Vindicta";
		
		message.append("<col=ff6600>").append(npcName)
			   .append(" mounts her dragon, analyzing your combat prowess (BossBalancer v5.0).</col>");
		
		// Add v5.0 intelligent scaling information
		if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			message.append(" <col=ffaa00>[Dragon's fury: +").append(diffIncrease).append("% dragonfire power]</col>");
		} else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
			message.append(" <col=00ff00>[Dragon's mercy: -").append(assistance).append("% damage]</col>");
		} else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
			message.append(" <col=9966cc>[Dragon resistance scaling active]</col>");
		} else if (scaling.scalingType.contains("FULL_ARMOR")) {
			message.append(" <col=808080>[Dragon rider protection acknowledged]</col>");
		}
		
		return message.toString();
	}

	/**
	 * ENHANCED v5.0: Intelligent Vindicta guidance with power-based scaling awareness
	 */
	private void provideIntelligentVindictaGuidance(Player player, Vindicta vindicta, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		long currentTime = System.currentTimeMillis();
		
		// Check if we should provide guidance
		Long lastTime = lastGuidanceTime.get(playerKey);
		if (lastTime != null && (currentTime - lastTime) < GUIDANCE_COOLDOWN) {
			return; // Still in cooldown
		}
		
		Integer currentStage = warningStage.get(playerKey);
		if (currentStage == null) currentStage = 0;
		if (currentStage >= MAX_WARNINGS_PER_FIGHT) {
			return; // Max warnings reached
		}
		
		// Get guidance message based on v5.0 intelligent scaling
		String guidanceMessage = getIntelligentVindictaGuidanceMessage(player, vindicta, scaling, currentStage);
		
		// Send guidance if applicable
		if (guidanceMessage != null) {
			player.sendMessage(guidanceMessage);
			lastGuidanceTime.put(playerKey, currentTime);
			warningStage.put(playerKey, currentStage + 1);
		}
	}

	/**
	 * NEW v5.0: Get intelligent Vindicta guidance message based on power analysis
	 */
	private String getIntelligentVindictaGuidanceMessage(Player player, Vindicta vindicta, CombatScaling scaling, int stage) {
		switch (stage) {
			case 0:
				// First warning: Power analysis and scaling type
				return getVindictaScalingAnalysisMessage(scaling, vindicta);
				
			case 1:
				// Second warning: Equipment effectiveness or armor analysis
				if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
					return "<col=ff6600>Dragon Analysis: Missing armor increases dragonfire damage by 25%! Equip full protection!</col>";
				} else {
					return "<col=ffaa00>Dragon Tactics: Stay exactly 3 tiles away during hurricanes. Watch for dragonfire wall gaps!</col>";
				}
				
			case 2:
				// Third warning: Advanced mechanics
				if (scaling.bossDamageMultiplier > 2.0) {
					return "<col=ff3300>Dragon Analysis: Extreme scaling active! Consider fighting higher-tier bosses for optimal challenge!</col>";
				} else {
					return "<col=00aaff>Dragon Mechanics: During air phase, expect aerial bombardment and massive dragonfire walls!</col>";
				}
				
			case 3:
				// Final warning: Ultimate tips
				return "<col=9966cc>Dragon Mastery: Use protection prayers during special attacks. HP-aware damage limits prevent one-shots!</col>";
		}
		
		return null;
	}

	/**
	 * NEW v5.0: Get Vindicta scaling analysis message
	 */
	private String getVindictaScalingAnalysisMessage(CombatScaling scaling, Vindicta vindicta) {
		NPCDefinitions def = NPCDefinitions.getNPCDefinitions(vindicta.getId());
		String npcName = (def != null && def.getName() != null) ? def.getName() : "Vindicta";
		
		String baseMessage = "<col=ff9900>Dragon Power Analysis:</col> ";
		
		if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
			return baseMessage + "<col=00ff00>" + npcName + "'s dragon shows mercy! Damage reduced by " + 
				   assistancePercent + "% due to insufficient preparation. Upgrade your gear!</col>";
				   
		} else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=ff3300>" + npcName + "'s dragon unleashes fury! Power increased by " + 
				   difficultyIncrease + "% due to your superior equipment. Fight worthier foes!</col>";
				   
		} else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
			return baseMessage + "<col=ffaa00>Balanced dragon encounter. Optimal challenge achieved!</col>";
			
		} else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=ff6600>Slight advantage detected. " + npcName + "'s intensity increased by " + 
				   difficultyIncrease + "% for balanced combat.</col>";
		}
		
		return baseMessage + "<col=999999>Power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
	}

	/**
	 * NEW v5.0: Monitor scaling changes during Vindicta combat
	 */
	private void monitorVindictaScalingChanges(Player player, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		String currentScalingType = scaling.scalingType;
		String lastType = lastScalingType.get(playerKey);
		
		// Check if scaling type changed (prayer activation, gear swap, etc.)
		if (lastType != null && !lastType.equals(currentScalingType)) {
			// Scaling changed - notify player
			String changeMessage = getVindictaScalingChangeMessage(lastType, currentScalingType, scaling);
			if (changeMessage != null) {
				player.sendMessage(changeMessage);
			}
		}
		
		lastScalingType.put(playerKey, currentScalingType);
	}

	/**
	 * NEW v5.0: Get Vindicta scaling change message
	 */
	private String getVindictaScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
		if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
			return "<col=00ff00>Dragon Update: Combat balance improved! Dragon mercy reduced.</col>";
		} else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
			return "<col=ff6600>Dragon Update: Dragon fury now active due to increased power!</col>";
		} else if (newType.contains("WITH_ABSORPTION")) {
			return "<col=9966cc>Dragon Update: Damage absorption bonuses detected and factored into scaling!</col>";
		} else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
			return "<col=ffaa00>Dragon Update: Full protection equipped! Dragonfire damage scaling normalized.</col>";
		}
		
		return null;
	}

	/**
	 * Enhanced dragon rider safe spot detection and prevention
	 */
	private void checkAndPreventDragonRiderSafespotExploitation(Vindicta vindicta, Player player, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		int distance = player.getDistance(vindicta);
		
		// Track consecutive avoids
		Integer avoidCount = consecutiveAvoids.get(playerKey);
		Integer warnCount = safespotWarnings.get(playerKey);
		if (avoidCount == null) avoidCount = 0;
		if (warnCount == null) warnCount = 0;
		
		if (distance > MAX_SAFESPOT_DISTANCE) {
			warnCount++;
			safespotWarnings.put(playerKey, warnCount);
			
			if (warnCount >= 3) {
				// Dragon rider anti-safespot measure
				performDragonRiderAntiSafeSpotMeasure(vindicta, player, scaling);
				safespotWarnings.put(playerKey, 0);
			} else {
				long currentTime = System.currentTimeMillis();
				Long lastWarning = lastGuidanceTime.get(playerKey);
				if (lastWarning == null || (currentTime - lastWarning) > GUIDANCE_COOLDOWN) {
					try {
						vindicta.setNextForceTalk(new ForceTalk("You cannot escape the dragon's reach!"));
					} catch (Exception e) {
						player.sendMessage("<col=ff6600>Vindicta: You cannot escape the dragon's reach!</col>");
					}
					player.sendMessage("Vindicta demands closer combat! Move within " + MAX_SAFESPOT_DISTANCE + " tiles!");
					lastGuidanceTime.put(playerKey, currentTime);
				}
			}
			
			if (distance > FORCE_RESET_DISTANCE) {
				vindicta.resetCombat();
				endVindictaCombatSession(vindicta, player);
				player.sendMessage("Vindicta loses interest in your cowardly tactics.");
			}
		} else {
			// Reset warnings when fighting properly
			if (warnCount > 0) {
				safespotWarnings.put(playerKey, 0);
				player.sendMessage("<col=00ff00>Vindicta acknowledges your honorable combat...</col>");
			}
		}
	}

	/**
	 * NEW v5.0: Perform dragon rider anti-safe spot measure
	 */
	private void performDragonRiderAntiSafeSpotMeasure(Vindicta vindicta, Player player, CombatScaling scaling) {
		player.sendMessage("<col=ff3300>Dragon's wrath reaches all who flee from battle!</col>");
		
		// Dragon fire blast that reaches through all obstacles
		vindicta.setNextAnimation(new Animation(28277)); // Air phase ranged animation
		vindicta.setNextForceTalk(new ForceTalk("COWARD! Face dragonfire!"));
		
		// Enhanced damage based on scaling with HP-aware limits
		NPCCombatDefinitions defs = vindicta.getCombatDefinitions();
		int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.4) : 180; // Dragon wrath blast
		int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(vindicta, player, baseDamage);
		int safeDamage = applyHPAwareVindictaDamageScaling(scaledDamage, player, "dragon_wrath");
		
		checkAndWarnLowHPForDragonRider(player, safeDamage, "Dragon Wrath");
		delayHit(vindicta, 1, player, getRangeHit(vindicta, safeDamage));
		
		player.sendMessage("<col=ff6600>DRAGON PENALTY: Safe spotting detected - dragonfire reaches all!</col>");
	}

	/**
	 * ENHANCED v5.0: Intelligent ground phase attack execution with HP-aware scaling
	 */
	private int executeIntelligentGroundPhaseAttack(Vindicta vindicta, Player player, int phase, int players, CombatScaling scaling) {
		// Increment attack counter
		Integer playerKey = Integer.valueOf(player.getIndex());
		Integer currentCount = attackCounter.get(playerKey);
		if (currentCount == null) currentCount = 0;
		attackCounter.put(playerKey, currentCount + 1);
		
		try {
			if (!vindicta.performedHurricane()) {
				switch (phase) {
				case 2:
					sendEnhancedMechanicWarning(vindicta, player, "Brace yourselves! Hurricane incoming!", scaling);
					vindicta.setPhase(0);
					vindicta.setHasPerformedHurricane();
					return executeIntelligentHurricane(vindicta, player, scaling);
				default:
					return executeIntelligentSliceAttack(vindicta, player, scaling);
				}
			} else {
				if (players > 1) {
					switch (phase) {
					case 3:
						sendEnhancedMechanicWarning(vindicta, player, "Switching to ranged assault!", scaling);
						return executeIntelligentRangedAttack(vindicta, player, scaling);
					case 5:
						sendEnhancedMechanicWarning(vindicta, player, "Dragonfire wall! Find the safe spots!", scaling);
						return executeIntelligentDragonfireWall(vindicta, player, scaling);
					case 9:
						sendEnhancedMechanicWarning(vindicta, player, "Final hurricane! Take cover!", scaling);
						return executeIntelligentHurricane(vindicta, player, scaling);
					default:
						return executeIntelligentSliceAttack(vindicta, player, scaling);
					}
				} else {
					switch (phase) {
					case 2:
						sendEnhancedMechanicWarning(vindicta, player, "Dragonfire wall approaches!", scaling);
						return executeIntelligentDragonfireWall(vindicta, player, scaling);
					case 6:
						sendEnhancedMechanicWarning(vindicta, player, "Ultimate hurricane attack!", scaling);
						return executeIntelligentHurricane(vindicta, player, scaling);
					default:
						return executeIntelligentSliceAttack(vindicta, player, scaling);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentGroundPhaseAttack: " + e.getMessage());
			return executeIntelligentSliceAttack(vindicta, player, scaling);
		}
	}

	/**
	 * ENHANCED v5.0: Intelligent air phase attack execution with HP-aware scaling
	 */
	private int executeIntelligentAirPhaseAttack(Vindicta vindicta, Player player, int phase, CombatScaling scaling) {
		try {
			switch (phase) {
			case 1:
				sendEnhancedMechanicWarning(vindicta, player, "Aerial bombardment incoming!", scaling);
				return executeIntelligentRangedAttack(vindicta, player, scaling);
			case 3:
				sendEnhancedMechanicWarning(vindicta, player, "Massive dragonfire wall from above!", scaling);
				return executeIntelligentSecondDragonfireWall(vindicta, player, scaling);
			default:
				return executeIntelligentSliceAttack(vindicta, player, scaling);
			}
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentAirPhaseAttack: " + e.getMessage());
			return executeIntelligentSliceAttack(vindicta, player, scaling);
		}
	}

	/**
	 * Enhanced slice attack with full BossBalancer integration and HP-aware scaling
	 */
	private int executeIntelligentSliceAttack(Vindicta vindicta, Player player, CombatScaling scaling) {
		try {
			vindicta.setNextAnimation(new Animation(isGroundPhase(vindicta) ? 28253 : 28273));
			
			// ===== BOSSBALANCER INTEGRATION: DAMAGE CALCULATION WITH HP-AWARE SCALING =====
			NPCCombatDefinitions defs = vindicta.getCombatDefinitions();
			int baseMaxHit = defs != null ? defs.getMaxHit() : 150;
			int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(vindicta, player, baseMaxHit);
			int safeDamage = applyHPAwareVindictaDamageScaling(scaledDamage, player, "slice");
			
			// ===== BOSSBALANCER INTEGRATION: ACCURACY CHECK =====
			boolean hits = checkBossAccuracy(vindicta, player, 1800); // Base accuracy of 1800
			
			if (hits) {
				delayHit(vindicta, 0, player, getMeleeHit(vindicta, safeDamage));
			} else {
				delayHit(vindicta, 0, player, getMeleeHit(vindicta, 0)); // Miss
				player.sendMessage("You dodge Vindicta's slice attack!");
			}
			
			return 4;
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentSliceAttack: " + e.getMessage());
			return 4;
		}
	}

	/**
	 * Enhanced hurricane with BossBalancer damage scaling and HP-aware limits
	 */
	private int executeIntelligentHurricane(Vindicta vindicta, Player player, CombatScaling scaling) {
		try {
			vindicta.setCannotMove(true);
			vindicta.resetWalkSteps();
			
			// Send guidance about hurricane mechanics with scaling context
			String hurricaneWarning = "Hurricane detected! Stay exactly 3 tiles away from Vindicta!";
			if (scaling.bossDamageMultiplier > 2.0) {
				hurricaneWarning += " (EXTREME power due to scaling!)";
			}
			sendPlayerMessage(player, hurricaneWarning);
			
			WorldTasksManager.schedule(new WorldTask() {
				final WorldTile centerTile = new WorldTile(vindicta.getCoordFaceX(vindicta.getSize()), 
														  vindicta.getCoordFaceY(vindicta.getSize()), 
														  vindicta.getPlane());
				private int preparationTicks = 0;

				@Override
				public void run() {
					try {
						if (vindicta.isDead() || vindicta.hasFinished()) {
							stop();
							return;
						}
						
						if (preparationTicks < 2) {
							// Preparation phase guidance
							List<Player> players = getInstancePlayers(vindicta);
							for (Player p : players) {
								if (p != null && !p.hasFinished()) {
									int distance = p.getDistance(centerTile);
									if (distance < 2) {
										p.sendMessage("You're too close! Move back to avoid the hurricane!");
									} else if (distance > 5) {
										p.sendMessage("Position yourself 3 tiles from Vindicta for optimal safety!");
									}
								}
							}
							preparationTicks++;
							return;
						}
						
						// Execute hurricane with BossBalancer damage and HP-aware scaling
						vindicta.setNextAnimation(new Animation(28256));
						vindicta.setNextGraphics(new Graphics(6111));
						
						List<Player> players = getInstancePlayers(vindicta);
						for (Player p : players) {
							if (p != null && !p.hasFinished()) {
								int distance = p.getDistance(centerTile);
								if (distance < 3 && p.getTileHash() != centerTile.getTileHash()) {
									// ===== BOSSBALANCER INTEGRATION: HURRICANE DAMAGE WITH HP-AWARE SCALING =====
									NPCCombatDefinitions defs = vindicta.getCombatDefinitions();
									int baseHurricaneDamage = defs != null ? (int)(defs.getMaxHit() * 1.5) : 200;
									int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(vindicta, p, baseHurricaneDamage);
									int safeDamage = applyHPAwareVindictaDamageScaling(scaledDamage, p, "hurricane");
									
									boolean hits = checkBossAccuracy(vindicta, p, 2000); // High accuracy for hurricane
									
									if (hits) {
										checkAndWarnLowHPForDragonRider(p, safeDamage, "Hurricane");
										delayHit(vindicta, 0, p, getMeleeHit(vindicta, safeDamage));
										p.sendMessage("The hurricane tears through you!");
									} else {
										delayHit(vindicta, 0, p, getMeleeHit(vindicta, 0));
										p.sendMessage("You barely escape the hurricane's fury!");
									}
								}
								
								// Handle familiars with BossBalancer scaling and HP-aware limits
								if (p.getFamiliar() != null && p.getFamiliar().getDistance(centerTile) < 3 
									&& p.getFamiliar().getTileHash() != centerTile.getTileHash() 
									&& p.getFamiliar().getDefinitions() != null
									&& p.getFamiliar().getDefinitions().hasAttackOption()) {
									NPCCombatDefinitions defs = vindicta.getCombatDefinitions();
									int familiarDamage = defs != null ? (int)(defs.getMaxHit() * 1.2) : 150;
									int scaledFamiliarDamage = BossBalancer.calculateNPCDamageToPlayer(vindicta, p, familiarDamage);
									delayHit(vindicta, 0, p.getFamiliar(), getMeleeHit(vindicta, scaledFamiliarDamage));
								}
							}
						}
						
						vindicta.setCannotMove(false);
						stop();
					} catch (Exception e) {
						System.err.println("Error in hurricane task: " + e.getMessage());
						vindicta.setCannotMove(false);
						stop();
					}
				}
			}, 1, 1);
			
			return 6;
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentHurricane: " + e.getMessage());
			vindicta.setCannotMove(false);
			return 6;
		}
	}

	/**
	 * Enhanced ranged attack with BossBalancer integration and HP-aware scaling
	 */
	public int executeIntelligentRangedAttack(Vindicta vindicta, Player player, CombatScaling scaling) {
		try {
			vindicta.setNextAnimation(new Animation(isGroundPhase(vindicta) ? 28260 : 28277));
			
			final List<Entity> targets = getValidTargets(vindicta, player);
			
			// Send guidance about ranged attack with scaling context
			if (targets.size() > 1) {
				String rangedWarning = "Vindicta is targeting multiple enemies! Spread out!";
				if (scaling.bossDamageMultiplier > 1.8) {
					rangedWarning += " (Enhanced power active!)";
				}
				sendPlayerMessage(player, rangedWarning);
			}
			
			// Apply BossBalancer scaling to each target with HP-aware limits
			for (Entity t : targets) {
				if (t != null && !t.isDead() && !t.hasFinished()) {
					final NewProjectile projectile = new NewProjectile(
						new WorldTile(vindicta.getCoordFaceX(vindicta.getSize()), 
									  vindicta.getCoordFaceY(vindicta.getSize()), 
									  vindicta.getPlane()), 
						t, 6116, isGroundPhase(vindicta) ? 50 : 80, 30, 55, 10, 30, 0);
					
					World.sendProjectile(projectile);
					
					final Entity targetEntity = t;
					try {
						CoresManager.slowExecutor.schedule(new Runnable() {
							@Override
							public void run() {
								try {
									if (targetEntity != null && !targetEntity.isDead() && !targetEntity.hasFinished()) {
										// ===== BOSSBALANCER INTEGRATION: RANGED DAMAGE WITH HP-AWARE SCALING =====
										NPCCombatDefinitions defs = vindicta.getCombatDefinitions();
										int baseRangedDamage = defs != null ? (int)(defs.getMaxHit() * 1.2) : 160;
										int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(vindicta, 
																								targetEntity instanceof Player ? (Player)targetEntity : player, 
																								baseRangedDamage);
										
										boolean hits = checkBossAccuracy(vindicta, targetEntity, 1900); // High accuracy for ranged
										
										if (hits && targetEntity instanceof Player) {
											Player targetPlayer = (Player) targetEntity;
											int safeDamage = applyHPAwareVindictaDamageScaling(scaledDamage, targetPlayer, "ranged");
											checkAndWarnLowHPForDragonRider(targetPlayer, safeDamage, "Dragonfire Blast");
											delayHit(vindicta, 0, targetEntity, getRangeHit(vindicta, safeDamage));
										} else if (hits) {
											// For familiars, use scaled damage directly
											delayHit(vindicta, 0, targetEntity, getRangeHit(vindicta, scaledDamage));
										} else {
											delayHit(vindicta, 0, targetEntity, getRangeHit(vindicta, 0));
											if (targetEntity instanceof Player) {
												((Player) targetEntity).sendMessage("You dodge the dragonfire blast!");
											}
										}
									}
								} catch (Exception e) {
									System.err.println("Error in ranged attack damage: " + e.getMessage());
								}
							}
						}, projectile.getTime(), TimeUnit.MILLISECONDS);
					} catch (Exception e) {
						System.err.println("Error scheduling ranged attack: " + e.getMessage());
					}
				}
			}
			
			return 4;
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentRangedAttack: " + e.getMessage());
			return 4;
		}
	}

	/**
	 * Enhanced dragonfire wall with BossBalancer damage integration and HP-aware scaling
	 */
	private int executeIntelligentDragonfireWall(Vindicta vindicta, Player player, CombatScaling scaling) {
		try {
			final int direction = Utils.random(16000);
			final int dir = (int) Math.ceil(direction / 2048);
			
			String dragonfireWarning = "Dragonfire wall incoming! Look for the safe spots!";
			if (scaling.bossDamageMultiplier > 2.0) {
				dragonfireWarning += " (EXTREME dragonfire due to scaling!)";
			}
			sendPlayerMessage(player, dragonfireWarning);
			
			WorldTasksManager.schedule(new WorldTask() {
				private WorldTile dest;
				private int ticks;
				private WorldTile[] fireArray;
				private WorldTile[] safeSpots = new WorldTile[3];
				private NPC decoy = null;
				
				@Override
				public void run() {
					try {
						if (vindicta.isDead() || vindicta.hasFinished()) {
							cleanupTask();
							return;
						}
						
						if (ticks == 0) {
							vindicta.setNextAnimation(new Animation(28259));
							decoy = new NPC(22464, getWorldTile(vindicta, player, direction), -1, true, true);
							if (decoy != null) {
								decoy.setDirection(direction);
								decoy.setNextGraphics(new Graphics(6114));
							}
						} else if (ticks == 1) {
							createEnhancedFireWall(vindicta, player, dir);
						} else if (ticks == 3) {
							// ===== BOSSBALANCER INTEGRATION: DRAGONFIRE DAMAGE WITH HP-AWARE SCALING =====
							applyIntelligentDragonfireDamage(vindicta, scaling);
						} else if (ticks == 5) {
							if (decoy != null && !decoy.hasFinished()) {
								decoy.finish();
							}
						} else if (ticks == 50) {
							cleanupTask();
						}
						ticks++;
					} catch (Exception e) {
						System.err.println("Error in dragonfire wall task: " + e.getMessage());
						cleanupTask();
					}
				}
				
				private void applyIntelligentDragonfireDamage(Vindicta vindicta, CombatScaling scaling) {
					try {
						List<Player> players = getInstancePlayers(vindicta);
						for (Player p : players) {
							if (p != null && !p.hasFinished()) {
								// Check if player is standing on fire tiles
								if (isPlayerOnFire(vindicta, p)) {
									// ===== BOSSBALANCER INTEGRATION: DRAGONFIRE DAMAGE WITH HP-AWARE SCALING =====
									NPCCombatDefinitions defs = vindicta.getCombatDefinitions();
									int baseDragonfireDamage = defs != null ? (int)(defs.getMaxHit() * 1.3) : 180;
									int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(vindicta, p, baseDragonfireDamage);
									int safeDamage = applyHPAwareVindictaDamageScaling(scaledDamage, p, "dragonfire");
									
									boolean hits = checkBossAccuracy(vindicta, p, 1700);
									
									if (hits) {
										checkAndWarnLowHPForDragonRider(p, safeDamage, "Dragonfire");
										delayHit(vindicta, 0, p, getMagicHit(vindicta, safeDamage));
										p.sendMessage("The dragonfire burns you!");
									} else {
										p.sendMessage("You resist the dragonfire!");
									}
								}
							}
						}
					} catch (Exception e) {
						System.err.println("Error applying dragonfire damage: " + e.getMessage());
					}
				}
				
				private void cleanupTask() {
					try {
						if (fireArray != null) {
							cleanupFireWall(vindicta);
						}
						if (decoy != null && !decoy.hasFinished()) {
							decoy.finish();
						}
					} catch (Exception e) {
						System.err.println("Error in dragonfire cleanup: " + e.getMessage());
					}
					stop();
				}
				
				private void createEnhancedFireWall(Vindicta vindicta, Entity target, int dir) {
					final List<WorldTile> fireTiles = new ArrayList<WorldTile>();
					final List<WorldTile> potentialSafeSpots = new ArrayList<WorldTile>();
					
					try {
						for (int i = -30; i < 30; i++) {
							WorldTile tile = new WorldTile(target.getX() + (Utils.DIRS[dir][0] * i), 
														  target.getY() + (Utils.DIRS[dir][1] * i), 
														  target.getPlane());
							if (addFire(vindicta, tile, false)) {
								if (i % 8 == 0 && target.getDistance(tile) >= 3) {
									potentialSafeSpots.add(tile);
								} else {
									fireTiles.add(tile);
								}
							}
						}
						
						int safeSpotCount = Math.min(3, potentialSafeSpots.size());
						for (int i = 0; i < safeSpotCount; i++) {
							if (i < potentialSafeSpots.size()) {
								safeSpots[i] = potentialSafeSpots.get(i);
								vindicta.addSafeTile(safeSpots[i]);
							}
						}
						
						fireArray = fireTiles.toArray(new WorldTile[fireTiles.size()]);
						vindicta.addFires(fireArray);
						
						// Store fire tiles for damage checking
						vindicta.getTemporaryAttributtes().put("fireTiles", fireArray);
						vindicta.getTemporaryAttributtes().put("rangedDelay", Utils.currentTimeMillis() + 5000);
						
						List<Player> players = getInstancePlayers(vindicta);
						for (Player p : players) {
							if (p != null && !p.hasFinished()) {
								p.sendMessage("Safe spots created! Move to the gaps in the fire wall!");
							}
						}
					} catch (Exception e) {
						System.err.println("Error creating fire wall: " + e.getMessage());
					}
				}
				
				private void cleanupFireWall(Vindicta vindicta) {
					try {
						if (fireArray != null) {
							vindicta.removeFires(fireArray);
						}
						for (int i = 0; i < safeSpots.length; i++) {
							if (safeSpots[i] != null) {
								vindicta.removeSafeTile(safeSpots[i]);
								safeSpots[i] = null;
							}
						}
						// Clear fire tile data
						vindicta.getTemporaryAttributtes().remove("fireTiles");
					} catch (Exception e) {
						System.err.println("Error cleaning fire wall: " + e.getMessage());
					}
				}
			}, 0, 0);
			
			return 4;
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentDragonfireWall: " + e.getMessage());
			return 4;
		}
	}

	/**
	 * Enhanced second dragonfire wall (air phase) with HP-aware scaling
	 */
	private int executeIntelligentSecondDragonfireWall(Vindicta vindicta, Player player, CombatScaling scaling) {
		try {
			final WorldTile destination = getCorner(vindicta, player);
			vindicta.resetWalkSteps();
			vindicta.resetCombat();
			vindicta.setNextGraphics(new Graphics(6118));
			vindicta.setCannotMove(true);
			vindicta.setNextAnimation(new Animation(28275));
			vindicta.getTemporaryAttributtes().put("rangedDelay", Utils.currentTimeMillis() + 15000);
			
			String aerialWarning = "Vindicta is positioning for a massive aerial dragonfire! Prepare to dodge!";
			if (scaling.bossDamageMultiplier > 2.5) {
				aerialWarning += " (MAXIMUM power due to extreme scaling!)";
			}
			sendPlayerMessage(player, aerialWarning);
			
			WorldTasksManager.schedule(new WorldTask() {
				private int ticks;
				private WorldTile[] fireArray;

				@Override
				public void run() {
					try {
						if (vindicta.isDead() || vindicta.hasFinished()) {
							cleanupTask();
							return;
						}
						
						if (ticks == 2) {
							vindicta.resetWalkSteps();
							List<Player> players = getInstancePlayers(vindicta);
							for (Player p : players) {
								if (p != null && !p.hasFinished()) {
									p.stopAll();
									p.sendMessage("Vindicta is repositioning! Prepare for incoming dragonfire!");
								}
							}
							vindicta.setNextWorldTile(destination);
							vindicta.setNextFaceWorldTile(new WorldTile(player));
							vindicta.setNextGraphics(new Graphics(6118));
							vindicta.setNextAnimation(new Animation(28276));
						} else if (ticks == 5) {
							vindicta.setNextAnimationForce(new Animation(28274));
							sendPlayerMessage(player, "Massive dragonfire incoming! Move perpendicular to the line!");
						} else if (ticks == 6) {
							fireArray = createAerialFireLine(vindicta, player);
							if (fireArray != null) {
								vindicta.addFires(fireArray);
								// Store fire tiles for damage checking
								vindicta.getTemporaryAttributtes().put("fireTiles", fireArray);
								
								// ===== BOSSBALANCER INTEGRATION: AERIAL FIRE DAMAGE WITH HP-AWARE SCALING =====
								applyIntelligentAerialFireDamage(vindicta, scaling);
							}
							vindicta.setCannotMove(false);
							vindicta.resetWalkSteps();
							vindicta.getCombat().setTarget(player);
						} else if (ticks == 55) {
							cleanupTask();
						}
						ticks++;
					} catch (Exception e) {
						System.err.println("Error in second dragonfire wall task: " + e.getMessage());
						cleanupTask();
					}
				}
				
				private void applyIntelligentAerialFireDamage(Vindicta vindicta, CombatScaling scaling) {
					try {
						List<Player> players = getInstancePlayers(vindicta);
						for (Player p : players) {
							if (p != null && !p.hasFinished() && isPlayerOnFire(vindicta, p)) {
								// Higher damage for aerial attack with HP-aware scaling
								NPCCombatDefinitions defs = vindicta.getCombatDefinitions();
								int baseAerialDamage = defs != null ? (int)(defs.getMaxHit() * 1.6) : 220;
								int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(vindicta, p, baseAerialDamage);
								int safeDamage = applyHPAwareVindictaDamageScaling(scaledDamage, p, "second_dragonfire");
								
								boolean hits = checkBossAccuracy(vindicta, p, 1800);
								
								if (hits) {
									checkAndWarnLowHPForDragonRider(p, safeDamage, "Massive Dragonfire");
									delayHit(vindicta, 0, p, getMagicHit(vindicta, safeDamage));
									p.sendMessage("The aerial dragonfire engulfs you!");
								} else {
									p.sendMessage("You narrowly avoid the massive dragonfire!");
								}
							}
						}
					} catch (Exception e) {
						System.err.println("Error applying aerial fire damage: " + e.getMessage());
					}
				}
				
				private void cleanupTask() {
					try {
						if (fireArray != null) {
							vindicta.removeFires(fireArray);
							fireArray = null;
						}
						vindicta.getTemporaryAttributtes().remove("fireTiles");
						vindicta.setCannotMove(false);
					} catch (Exception e) {
						System.err.println("Error in second dragonfire cleanup: " + e.getMessage());
					}
					stop();
				}
			}, 0, 0);
			
			return 10;
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentSecondDragonfireWall: " + e.getMessage());
			vindicta.setCannotMove(false);
			return 10;
		}
	}

	/**
	 * ===== ENHANCED BOSSBALANCER INTEGRATION METHODS =====
	 */

	/**
	 * Enhanced accuracy check using BossBalancer v5.0
	 */
	private boolean checkBossAccuracy(NPC npc, Entity target, int baseAccuracy) {
		try {
			if (!(target instanceof Player)) {
				return Math.random() < 0.8; // Default 80% accuracy for non-players
			}
			
			Player player = (Player) target;
			
			// Apply BossBalancer accuracy scaling
			int scaledAccuracy = BossBalancer.applyBossAccuracyScaling(baseAccuracy, player, npc);
			
			// Convert accuracy to hit chance (simplified calculation)
			double hitChance = Math.min(0.95, scaledAccuracy / 2000.0); // Cap at 95%
			hitChance = Math.max(0.05, hitChance); // Minimum 5% hit chance
			
			return Math.random() < hitChance;
			
		} catch (Exception e) {
			System.err.println("Error checking boss accuracy: " + e.getMessage());
			return Math.random() < 0.8; // Fallback
		}
	}

	/**
	 * Fallback basic attack for non-Vindicta NPCs or error conditions with HP-aware scaling
	 */
	private int executeBasicAttack(NPC npc, Entity target) {
		if (npc == null || target == null) return 4;
		
		try {
			npc.setNextAnimation(new Animation(isGroundPhase(npc) ? 28253 : 28273));
			
			// Even basic attacks use BossBalancer if target is a player
			int damage;
			if (target instanceof Player) {
				Player player = (Player) target;
				damage = BossBalancer.applyBossScaling(200, player, npc);
				damage = applyHPAwareVindictaDamageScaling(damage, player, "basic_attack");
			} else {
				damage = Utils.random(100, 300);
			}
			
			delayHit(npc, 0, target, getMeleeHit(npc, damage));
			return 4;
		} catch (Exception e) {
			System.err.println("Error in executeBasicAttack(): " + e.getMessage());
			return 4;
		}
	}

	/**
	 * Enhanced mechanic warning with scaling context
	 */
	private void sendEnhancedMechanicWarning(Vindicta vindicta, Player player, String message, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		long currentTime = System.currentTimeMillis();
		
		Long lastWarning = lastMechanicWarning.get(playerKey);
		if (lastWarning != null && (currentTime - lastWarning) < MECHANIC_WARNING_COOLDOWN) {
			return;
		}
		
		// Add scaling context to warning
		String enhancedMessage = message;
		if (scaling.bossDamageMultiplier > 2.5) {
			enhancedMessage += " (EXTREME power active!)";
		} else if (scaling.bossDamageMultiplier > 1.8) {
			enhancedMessage += " (Enhanced power active!)";
		}
		
		sendInstanceMessage(vindicta, enhancedMessage);
		lastMechanicWarning.put(playerKey, currentTime);
	}

	/**
	 * ===== BOSSBALANCER v5.0 COMBAT SESSION CLEANUP =====
	 */
	public void endVindictaCombatSession(Vindicta npc, Entity target) {
		try {
			if (target instanceof Player) {
				Player player = (Player) target;
				Integer playerKey = Integer.valueOf(player.getIndex());
				
				// End BossBalancer v5.0 combat session
				BossBalancer.endCombatSession(player);
				
				// Clear local tracking maps
				combatSessionActive.remove(playerKey);
				lastGuidanceTime.remove(playerKey);
				lastMechanicWarning.remove(playerKey);
				lastScalingType.remove(playerKey);
				attackCounter.remove(playerKey);
				warningStage.remove(playerKey);
				safespotWarnings.remove(playerKey);
				consecutiveAvoids.remove(playerKey);
				
				// Clear BossBalancer player cache
				BossBalancer.clearPlayerCache(player.getIndex());
				
				// Send completion message with v5.0 info
				player.sendMessage("<col=ff6600>Dragon rider combat session ended. Scaling data cleared.</col>");
			}
		} catch (Exception e) {
			System.err.println("Error ending Vindicta combat session: " + e.getMessage());
		}
	}

	/**
	 * Handle prayer changes during dragon rider combat
	 */
	public static void onPlayerPrayerChanged(Player player) {
		if (player == null) return;
		
		try {
			Integer playerKey = Integer.valueOf(player.getIndex());
			
			// Only handle if in active combat session
			if (combatSessionActive.containsKey(playerKey)) {
				// Notify BossBalancer v5.0 of prayer change
				BossBalancer.onPrayerChanged(player);
				
				// Send update message
				player.sendMessage("<col=9966cc>Prayer change detected. Dragon rider scaling analysis updated.</col>");
			}
		} catch (Exception e) {
			System.err.println("VindictaCombat: Error handling v5.0 prayer change: " + e.getMessage());
		}
	}

	// ===== UTILITY METHODS (ENHANCED) =====

	/**
	 * FIXED: Check if player is standing on fire tiles
	 */
	private boolean isPlayerOnFire(Vindicta npc, Player player) {
		try {
			// Check if player is standing on any fire tiles
			Object fireData = npc.getTemporaryAttributtes().get("fireTiles");
			if (fireData instanceof WorldTile[]) {
				WorldTile[] fireTiles = (WorldTile[]) fireData;
				WorldTile playerTile = new WorldTile(player.getX(), player.getY(), player.getPlane());
				
				for (WorldTile fireTile : fireTiles) {
					if (fireTile != null && fireTile.matches(playerTile)) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			System.err.println("Error checking if player is on fire: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Safe method to get player count with null checking
	 */
	private int getPlayerCount(Vindicta vindicta) {
		try {
			if (vindicta.getInstance() != null && vindicta.getInstance().getPlayers() != null) {
				return vindicta.getInstance().getPlayers().size();
			}
		} catch (Exception e) {
			System.err.println("Error getting player count: " + e.getMessage());
		}
		return 1;
	}

	/**
	 * Send message to player with proper checks
	 */
	private void sendPlayerMessage(Entity target, String message) {
		if (target instanceof Player && message != null) {
			try {
				((Player) target).sendMessage(message);
			} catch (Exception e) {
				System.err.println("Error sending player message: " + e.getMessage());
			}
		}
	}

	/**
	 * Send message to all players in the instance
	 */
	private void sendInstanceMessage(Vindicta vindicta, String message) {
		try {
			vindicta.setNextForceTalk(new ForceTalk(message));
		} catch (Exception e) {
			try {
				List<Player> players = getInstancePlayers(vindicta);
				for (Player p : players) {
					if (p != null && !p.hasFinished()) {
						p.sendMessage("<col=ff6600>Vindicta: " + message + "</col>");
					}
				}
			} catch (Exception e2) {
				System.err.println("Error sending instance message: " + e2.getMessage());
			}
		}
	}

	/**
	 * Get instance players with null safety
	 */
	private List<Player> getInstancePlayers(Vindicta vindicta) {
		List<Player> safePlayers = new ArrayList<Player>();
		try {
			if (vindicta.getInstance() != null && vindicta.getInstance().getPlayers() != null) {
				for (Player p : vindicta.getInstance().getPlayers()) {
					if (p != null && !p.hasFinished()) {
						safePlayers.add(p);
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error getting instance players: " + e.getMessage());
		}
		return safePlayers;
	}

	/**
	 * Send phase guidance messages with v5.0 scaling context
	 */
	private void sendPhaseGuidance(Vindicta vindicta, Entity target, int phase, CombatScaling scaling) {
		if (!(target instanceof Player)) return;
		
		try {
			Player player = (Player) target;
			Integer playerKey = Integer.valueOf(player.getIndex());
			
			Long lastTime = lastGuidanceTime.get(playerKey);
			long currentTime = System.currentTimeMillis();
			if (lastTime != null && (currentTime - lastTime) < 15000L) {
				return; // Cooldown active
			}
			
			String scalingNote = "";
			if (scaling.bossDamageMultiplier > 2.0) {
				scalingNote = " (Enhanced by scaling!)";
			}
			
			switch (phase) {
			case 0:
				if (Math.random() < 0.3) {
					player.sendMessage("Vindicta is in basic attack mode - good time to deal damage!" + scalingNote);
				}
				break;
			case 2:
			case 6:
			case 9:
				player.sendMessage("Hurricane phase detected! Prepare to position correctly!" + scalingNote);
				break;
			case 3:
				player.sendMessage("Ranged attack phase! Vindicta will target multiple players!" + scalingNote);
				break;
			case 5:
				player.sendMessage("Dragonfire wall phase! Look for safe spots in the fire!" + scalingNote);
				break;
			}
			
			lastGuidanceTime.put(playerKey, currentTime);
		} catch (Exception e) {
			System.err.println("Error sending phase guidance: " + e.getMessage());
		}
	}

	// ===== UTILITY METHODS (UNCHANGED BUT ENHANCED) =====

	private boolean isGroundPhase(NPC npc) {
		return npc.getId() == 22459 || npc.getId() == 22461;
	}

	private int getMaxPhase(Vindicta vindicta, int players) {
		boolean isAirPhase = vindicta.getId() == 22460 || vindicta.getId() == 22462;
		if (isAirPhase) {
			return 3;
		} else {
			return players > 1 ? 9 : 6;
		}
	}

	private List<Entity> getValidTargets(Vindicta npc, Entity primaryTarget) {
		final List<Entity> targets = new ArrayList<Entity>();
		
		try {
			List<Player> players = getInstancePlayers(npc);
			boolean isSoloMode = players.size() == 1;
			
			for (Player p : players) {
				if (p != null && !p.hasFinished()) {
					if (isSoloMode) {
						targets.add(p);
						if (p.getFamiliar() != null && !p.getFamiliar().hasFinished()) {
							targets.add(p.getFamiliar());
						}
					} else if (!p.equals(primaryTarget)) {
						targets.add(p);
						if (p.getFamiliar() != null && !p.getFamiliar().hasFinished()) {
							targets.add(p.getFamiliar());
						}
					}
				}
			}
		} catch (Exception e) {
			System.err.println("Error getting valid targets: " + e.getMessage());
		}
		
		return targets;
	}

	private WorldTile[] createAerialFireLine(Vindicta npc, Entity target) {
		final List<WorldTile> tiles = new ArrayList<WorldTile>();
		
		try {
			final WorldTile dest = new WorldTile(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane());
			final WorldTile zero = npc.getInstance().getWorldTile(0, 0);
			
			final int minBoundaryX = zero.getX() + 15;
			final int minBoundaryY = zero.getY() + 7;
			final int startX = dest.getX();
			final int startY = dest.getY();
			final int maxBoundaryX = zero.getX() + 40;
			final int maxBoundaryY = zero.getY() + 40;
			
			float m = (float) (target.getY() - startY) / (float) (target.getX() - startX);
			final float intercept = ((float) target.getY() - m * (float) target.getX());
			float y = (float) target.getY();
			float x = (float) target.getX();
			
			if (x - startX == 0) {
				boolean down = y < startY;
				y = down ? minBoundaryY : maxBoundaryY;
			} else {
				boolean left = x < startX;
				for (; left ? x >= minBoundaryX : x <= maxBoundaryX; x = left ? x - 1 : x + 1) {
					if (m * x + intercept < maxBoundaryY) {
						y = (m * x) + intercept;
					} else {
						break;
					}
				}
			}
			
			final List<WorldTile> lineList = Utils.calculateLine(startX, startY, Math.round(x), Math.round(y), npc.getPlane());
			for (WorldTile tile : lineList) {
				if (addFire(npc, tile, true)) {
					tiles.add(tile);
				}
			}
		} catch (Exception e) {
			System.err.println("Error creating aerial fire line: " + e.getMessage());
		}
		
		return tiles.toArray(new WorldTile[tiles.size()]);
	}

	private final WorldTile getWorldTile(Vindicta npc, Entity target, int dir) {
		try {
			final int direction = (int) (Math.round(dir / 45.51) / 45);
			final byte[] offsets = Utils.DIRS[direction];
			WorldTile tile = new WorldTile(target.getX() + offsets[0], target.getY() + offsets[1], target.getPlane());
			int multiplier = 2;
			while (true) {
				tile = new WorldTile(target.getX() + (offsets[0] * multiplier), target.getY() + (offsets[1] * multiplier++), target.getPlane());
				if (tile.getDistance(npc) > npc.getSize()) {
					return tile;
				}
				if (multiplier > 50) {
					break;
				}
			}
			return tile;
		} catch (Exception e) {
			System.err.println("Error in getWorldTile: " + e.getMessage());
			return new WorldTile(target.getX(), target.getY(), target.getPlane());
		}
	}

	final boolean addFire(Vindicta npc, WorldTile target, boolean secondary) {
		try {
			if (!World.canMoveNPC(target.getPlane(), target.getX(), target.getY(), 1)) {
				return false;
			}
			if (secondary && target.getDistance(new WorldTile(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane())) <= 2) {
				return false;
			}
			final WorldTile corner = npc.getInstance().getWorldTile(0, 0);
			return target.getX() >= corner.getX() + 16 && target.getX() <= corner.getX() + 39 
				&& target.getY() >= corner.getY() + 8 && target.getY() <= corner.getY() + 39;
		} catch (Exception e) {
			System.err.println("Error in addFire: " + e.getMessage());
			return false;
		}
	}

	private final WorldTile getCorner(Vindicta npc, Entity target) {
		try {
			final WorldTile zero = npc.getInstance().getWorldTile(0, 0);
			int attempts = 0;
			while (attempts < 10) {
				final WorldTile t = new WorldTile(zero.getX() + Vindicta.CORNERS[Utils.random(4)][0], 
												 zero.getY() + Vindicta.CORNERS[Utils.random(4)][1], 
												 zero.getPlane());
				if (t.getDistance(target) > 8) {
					return t;
				}
				attempts++;
			}
			return new WorldTile(zero.getX() + 20, zero.getY() + 20, zero.getPlane());
		} catch (Exception e) {
			System.err.println("Error in getCorner: " + e.getMessage());
			return new WorldTile(target.getX(), target.getY(), target.getPlane());
		}
	}

	/**
	 * Static method for backward compatibility with HP-aware scaling
	 */
	public static final int rangedAttack(Vindicta npc, Entity target) {
		try {
			VindictaCombat combat = new VindictaCombat();
			
			// Use intelligent ranged attack if target is a player
			if (target instanceof Player) {
				Player player = (Player) target;
				CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
				return combat.executeIntelligentRangedAttack(npc, player, scaling);
			} else {
				// Fallback for non-player targets
				return combat.executeBasicAttack(npc, target);
			}
		} catch (Exception e) {
			System.err.println("Error in static rangedAttack: " + e.getMessage());
			return 4;
		}
	}

	// ===== ENHANCED CLEANUP AND MANAGEMENT METHODS =====

	/**
	 * Force cleanup method (call on logout/death)
	 */
	public static void forceCleanup(Player player) {
		if (player != null) {
			try {
				VindictaCombat combat = new VindictaCombat();
				combat.endVindictaCombatSession(null, player);
			} catch (Exception e) {
				System.err.println("Error in Vindicta force cleanup: " + e.getMessage());
			}
		}
	}

	/**
	 * Enhanced debug method for testing HP-aware scaling
	 */
	public static void debugVindictaScaling(Player player, Vindicta vindicta) {
		if (player == null || vindicta == null) {
			return;
		}

		try {
			CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, vindicta);

			System.out.println("=== VINDICTA COMBAT SCALING DEBUG v5.0 ===");
			System.out.println("Player: " + player.getDisplayName());
			System.out.println("Player HP: " + player.getHitpoints() + "/" + player.getSkills().getLevelForXp(Skills.HITPOINTS));
			System.out.println("Player Power: " + String.format("%.2f", scaling.playerPower));
			System.out.println("Boss Power: " + String.format("%.2f", scaling.bossPower));
			System.out.println("Power Ratio: " + String.format("%.2f", scaling.powerRatio));
			System.out.println("HP Multiplier: " + String.format("%.3f", scaling.bossHpMultiplier));
			System.out.println("Damage Multiplier: " + String.format("%.3f", scaling.bossDamageMultiplier));
			System.out.println("Accuracy Multiplier: " + String.format("%.3f", scaling.bossAccuracyMultiplier));
			System.out.println("Scaling Type: " + scaling.scalingType);
			
			// Test HP-aware damage calculations
			VindictaCombat combat = new VindictaCombat();
			NPCCombatDefinitions defs = vindicta.getCombatDefinitions();
			if (defs != null) {
				int baseMaxHit = defs.getMaxHit();
				int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(vindicta, player, baseMaxHit);
				
				System.out.println("=== HP-AWARE DAMAGE TESTING ===");
				System.out.println("Base Max Hit: " + baseMaxHit);
				System.out.println("BossBalancer Scaled: " + scaledDamage);
				
				// Test different attack types
				int sliceDamage = combat.applyHPAwareVindictaDamageScaling(scaledDamage, player, "slice");
				int hurricaneDamage = combat.applyHPAwareVindictaDamageScaling((int)(scaledDamage * 1.5), player, "hurricane");
				int dragonfireDamage = combat.applyHPAwareVindictaDamageScaling((int)(scaledDamage * 1.3), player, "dragonfire");
				int rangedDamage = combat.applyHPAwareVindictaDamageScaling((int)(scaledDamage * 1.2), player, "ranged");
				
				System.out.println("Slice (HP-aware): " + sliceDamage);
				System.out.println("Hurricane (HP-aware): " + hurricaneDamage);
				System.out.println("Dragonfire (HP-aware): " + dragonfireDamage);
				System.out.println("Ranged (HP-aware): " + rangedDamage);
				
				// Calculate damage percentages
				int currentHP = player.getHitpoints();
				if (currentHP > 0) {
					System.out.println("=== DAMAGE PERCENTAGES ===");
					System.out.println("Slice: " + String.format("%.1f", (double)sliceDamage / currentHP * 100) + "%");
					System.out.println("Hurricane: " + String.format("%.1f", (double)hurricaneDamage / currentHP * 100) + "%");
					System.out.println("Dragonfire: " + String.format("%.1f", (double)dragonfireDamage / currentHP * 100) + "%");
					System.out.println("Ranged: " + String.format("%.1f", (double)rangedDamage / currentHP * 100) + "%");
				}
			}

			System.out.println("=====================================");
		} catch (Exception e) {
			System.err.println("VindictaCombat: Error in debug scaling: " + e.getMessage());
		}
	}

	/**
	 * Get Vindicta combat statistics
	 */
	public static String getVindictaCombatStats() {
		return "VindictaCombat v5.0 - Active Sessions: " + combatSessionActive.size() + 
			   ", Guidance Cooldowns: " + lastGuidanceTime.size() + 
			   ", Mechanic Warnings: " + lastMechanicWarning.size() + 
			   ", Attack Counters: " + attackCounter.size();
	}

	/**
	 * Enhanced Vindicta command handler
	 */
	public static void handleVindictaCommand(Player player, String[] cmd) {
		if (player == null) {
			return;
		}

		try {
			if (cmd.length > 1) {
				String subcommand = cmd[1].toLowerCase();

				if ("debug".equals(subcommand)) {
					if (!player.isAdmin()) {
						player.sendMessage("You need admin rights for debug commands.");
						return;
					}
					
					// Find nearby Vindicta
					for (NPC npc : World.getNPCs()) {
						if (npc instanceof Vindicta && npc.getDistance(player) <= 10) {
							debugVindictaScaling(player, (Vindicta) npc);
							player.sendMessage("Vindicta scaling debug output sent to console.");
							return;
						}
					}
					player.sendMessage("No Vindicta found nearby for debugging.");
					
				} else if ("stats".equals(subcommand)) {
					player.sendMessage(getVindictaCombatStats());
					
				} else if ("cleanup".equals(subcommand)) {
					forceCleanup(player);
					player.sendMessage("Vindicta combat session data cleared.");
					
				} else {
					player.sendMessage("Usage: ;;vindicta [debug|stats|cleanup]");
				}
			} else {
				player.sendMessage("Vindicta Combat v5.0 with BossBalancer integration and HP-aware scaling");
				if (player.isAdmin()) {
					player.sendMessage("Admin: ;;vindicta debug - Debug scaling near Vindicta");
				}
			}

		} catch (Exception e) {
			player.sendMessage("Error in Vindicta command: " + e.getMessage());
		}
	}
}