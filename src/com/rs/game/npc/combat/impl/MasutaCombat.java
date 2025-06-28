package com.rs.game.npc.combat.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Masuta Combat System with FULL BossBalancer v5.0 Integration
 * Features: Intelligent power-based scaling, armor analysis, HP-aware damage scaling, phase progression
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 9.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System
 */
public class MasutaCombat extends CombatScript {

	// Combat phases - enhanced for v5.0
	private static final double PHASE_2_THRESHOLD = 0.70;
	private static final double PHASE_3_THRESHOLD = 0.40; 
	private static final double PHASE_4_THRESHOLD = 0.15;

	// Enhanced guidance system - intelligent scaling aware
	private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
	private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Integer> currentPhase = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
	private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
	private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
	
	// Timing constants - enhanced for v5.0
	private static final long WARNING_COOLDOWN = 180000; // 3 minutes between warnings
	private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
	private static final long PRE_ATTACK_WARNING_TIME = 3000; // 3 seconds before big attacks
	private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features

	// HP-aware damage scaling constants - CRITICAL SAFETY SYSTEM
	private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.35; // Max 35% of player HP per hit
	private static final double CRITICAL_DAMAGE_PERCENT = 0.50;  // Max 50% for critical attacks (combo)
	private static final double SPINNING_DAMAGE_PERCENT = 0.45;  // Max 45% for spinning attacks
	private static final double CRUSHING_DAMAGE_PERCENT = 0.42;  // Max 42% for crushing blows
	private static final int MIN_PLAYER_HP = 990;
	private static final int MAX_PLAYER_HP = 1500;
	private static final int ABSOLUTE_MAX_DAMAGE = 525;          // Hard cap (35% of 1500 HP)
	private static final int MINIMUM_DAMAGE = 30;               // Minimum damage to prevent 0 hits

	// Enhanced attack patterns with v5.0 intelligence
	private static final AttackPattern[] ATTACK_PATTERNS = {
		new AttackPattern(18163, 4585, 0, "basic_strike", false, ""),
		new AttackPattern(18177, 3585, 5100, "crushing_blow", true, "CRUSHING BLOW incoming - activate melee protect!"),
		new AttackPattern(18185, 3582, 6002, "spinning_attack", true, "SPINNING ATTACK incoming - prepare for high damage!"),
		new AttackPattern(18554, 3471, 5012, "devastating_combo", true, "DEVASTATING COMBO incoming - use defensive abilities!")
	};

	@Override
	public Object[] getKeys() {
		return new Object[] { 25589, 25590, 25591 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		if (!isValidCombatState(npc, target)) {
			return 4;
		}

		Player player = (Player) target;
		
		// ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
		
		// Initialize combat session if needed
		initializeCombatSession(player, npc);
		
		// Get INTELLIGENT combat scaling v5.0
		CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
		
		// Enhanced guidance system with intelligent scaling awareness
		provideIntelligentGuidance(player, npc, scaling);
		
		// Monitor scaling changes during combat
		monitorScalingChanges(player, scaling);
		
		// Update phase tracking with v5.0 scaling
		updateIntelligentPhaseTracking(npc, scaling);
		
		// Execute attack with v5.0 warnings and HP-aware scaling
		return performIntelligentAttackWithWarning(npc, player, scaling);
	}

	/**
	 * Initialize combat session using BossBalancer v5.0
	 */
	private void initializeCombatSession(Player player, NPC npc) {
		Integer sessionKey = Integer.valueOf(player.getIndex());
		
		if (!combatSessionActive.containsKey(sessionKey)) {
			// Start BossBalancer v5.0 combat session
			BossBalancer.startCombatSession(player, npc);
			combatSessionActive.put(sessionKey, Boolean.TRUE);
			attackCounter.put(sessionKey, Integer.valueOf(0));
			lastScalingType.put(sessionKey, "UNKNOWN");
			
			// Send v5.0 enhanced combat message
			CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
			String welcomeMsg = getIntelligentWelcomeMessage(scaling);
			player.sendMessage(welcomeMsg);
			
			// Perform initial armor analysis
			performInitialArmorAnalysis(player);
		}
	}

	/**
	 * NEW v5.0: Perform initial armor analysis
	 */
	private void performInitialArmorAnalysis(Player player) {
		try {
			// Use BossBalancer v5.0 armor analysis
			BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
			
			if (!armorResult.hasFullArmor) {
				player.sendMessage("<col=ff6600>Ascension Analysis: Missing protection detected. Masuta will deal increased damage!</col>");
			} else {
				double reductionPercent = armorResult.damageReduction * 100;
				player.sendMessage("<col=00ff00>Ascension Analysis: Full protection active (" + 
								 String.format("%.1f", reductionPercent) + "% damage reduction).</col>");
			}
		} catch (Exception e) {
			// Ignore armor analysis errors
		}
	}

	/**
	 * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills
	 */
	private int applyHPAwareDamageScaling(int scaledDamage, Player player, String attackType) {
		if (player == null) {
			return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
		}
		
		try {
			int currentHP = player.getHitpoints();
			int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
			
			// Use current HP for calculation (more dangerous when wounded)
			int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
			
			// Determine damage cap based on attack type
			double damagePercent;
			switch (attackType.toLowerCase()) {
				case "devastating_combo":
				case "critical":
					damagePercent = CRITICAL_DAMAGE_PERCENT;
					break;
				case "spinning_attack":
					damagePercent = SPINNING_DAMAGE_PERCENT;
					break;
				case "crushing_blow":
					damagePercent = CRUSHING_DAMAGE_PERCENT;
					break;
				default:
					damagePercent = MAX_DAMAGE_PERCENT_OF_HP;
					break;
			}
			
			// Calculate HP-based damage cap
			int hpBasedCap = (int)(effectiveHP * damagePercent);
			
			// Apply multiple safety caps
			int safeDamage = Math.min(scaledDamage, hpBasedCap);
			safeDamage = Math.min(safeDamage, ABSOLUTE_MAX_DAMAGE);
			safeDamage = Math.max(safeDamage, MINIMUM_DAMAGE);
			
			// Additional safety check - never deal more than 85% of current HP
			if (currentHP > 0) {
				int emergencyCap = (int)(currentHP * 0.85);
				safeDamage = Math.min(safeDamage, emergencyCap);
			}
			
			return safeDamage;
			
		} catch (Exception e) {
			// Fallback to absolute cap
			return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
		}
	}

	/**
	 * NEW v5.0: Send HP warning if player is in danger
	 */
	private void checkAndWarnLowHP(Player player, int incomingDamage) {
		if (player == null) return;
		
		try {
			int currentHP = player.getHitpoints();
			
			// Warn if incoming damage is significant relative to current HP
			if (currentHP > 0) {
				double damagePercent = (double)incomingDamage / currentHP;
				
				if (damagePercent >= 0.70) {
					player.sendMessage("<col=ff0000>WARNING: This attack will deal " + incomingDamage + 
									 " damage! (" + currentHP + " HP remaining)</col>");
				} else if (damagePercent >= 0.50) {
					player.sendMessage("<col=ff6600>WARNING: Heavy damage incoming (" + incomingDamage + 
									 ")! Consider healing (" + currentHP + " HP)</col>");
				}
			}
		} catch (Exception e) {
			// Ignore warning errors
		}
	}

	/**
	 * ENHANCED v5.0: Generate intelligent welcome message based on power analysis
	 */
	private String getIntelligentWelcomeMessage(CombatScaling scaling) {
		StringBuilder message = new StringBuilder();
		message.append("<col=4169E1>Masuta the Ascended awakens. Intelligent ascension analysis active (v5.0).</col>");
		
		// Add v5.0 intelligent scaling information
		if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			message.append(" <col=ff3300>[Anti-farming: +").append(diffIncrease).append("% difficulty]</col>");
		} else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
			message.append(" <col=00ff00>[Assistance: -").append(assistance).append("% difficulty]</col>");
		} else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
			message.append(" <col=66ccff>[Absorption scaling active]</col>");
		} else if (scaling.scalingType.contains("FULL_ARMOR")) {
			message.append(" <col=22aa22>[Full armor protection detected]</col>");
		}
		
		return message.toString();
	}

	/**
	 * ENHANCED v5.0: Intelligent guidance with power-based scaling awareness
	 */
	private void provideIntelligentGuidance(Player player, NPC npc, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		long currentTime = System.currentTimeMillis();
		
		// Check if we should provide guidance
		Long lastWarningTime = lastWarning.get(playerKey);
		if (lastWarningTime != null && (currentTime - lastWarningTime) < WARNING_COOLDOWN) {
			return; // Still in cooldown
		}
		
		Integer currentStage = warningStage.get(playerKey);
		if (currentStage == null) currentStage = 0;
		if (currentStage >= MAX_WARNINGS_PER_FIGHT) {
			return; // Max warnings reached
		}
		
		// Get guidance message based on v5.0 intelligent scaling
		String guidanceMessage = getIntelligentGuidanceMessage(player, npc, scaling, currentStage);
		
		// Send guidance if applicable
		if (guidanceMessage != null) {
			player.sendMessage(guidanceMessage);
			lastWarning.put(playerKey, currentTime);
			warningStage.put(playerKey, currentStage + 1);
		}
	}

	/**
	 * NEW v5.0: Get intelligent guidance message based on power analysis
	 */
	private String getIntelligentGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
		int phase = getCurrentPhase(npc);
		
		switch (stage) {
			case 0:
				// First warning: Power analysis and scaling type
				return getScalingAnalysisMessage(scaling);
				
			case 1:
				// Second warning: Equipment effectiveness or phase progression
				if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
					return "<col=ff3300>Ascension Analysis: Incomplete armor detected! Masuta damage increased by 25%. Equip missing pieces!</col>";
				} else if (phase >= 3) {
					String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME difficulty due to scaling!)" : "";
					return "<col=ff6600>Ascension Analysis: Phase 3+ reached. Masuta's power significantly increased" + difficultyNote + " Use protection prayers!</col>";
				}
				break;
				
			case 2:
				// Third warning: Final phase or extreme scaling
				if (phase >= 4) {
					return "<col=ff0000>Ascension Analysis: Final phase! Masuta at maximum power. All attacks intensified!</col>";
				} else if (scaling.bossDamageMultiplier > 2.5) {
					return "<col=ff0000>Ascension Analysis: Extreme anti-farming scaling detected! Consider fighting higher-tier bosses!</col>";
				}
				break;
		}
		
		return null;
	}

	/**
	 * NEW v5.0: Get scaling analysis message
	 */
	private String getScalingAnalysisMessage(CombatScaling scaling) {
		String baseMessage = "<col=66ccff>Ascension Power Analysis:</col> ";
		
		if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
			return baseMessage + "<col=00ff00>Assistance mode active! Masuta difficulty reduced by " + 
				   assistancePercent + "% due to gear disadvantage.</col>";
				   
		} else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=ff6600>Anti-farming scaling active! Masuta difficulty increased by " + 
				   difficultyIncrease + "% due to gear advantage.</col>";
				   
		} else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
			return baseMessage + "<col=ffffff>Balanced encounter detected. Optimal gear-to-boss ratio achieved!</col>";
			
		} else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=ffaa00>Slight overgear detected. Masuta difficulty increased by " + 
				   difficultyIncrease + "% for balance.</col>";
		}
		
		return baseMessage + "<col=cccccc>Ascension power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
	}

	/**
	 * NEW v5.0: Monitor scaling changes during combat
	 */
	private void monitorScalingChanges(Player player, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		String currentScalingType = scaling.scalingType;
		String lastType = lastScalingType.get(playerKey);
		
		// Check if scaling type changed (prayer activation, gear swap, etc.)
		if (lastType != null && !lastType.equals(currentScalingType)) {
			// Scaling changed - notify player
			String changeMessage = getScalingChangeMessage(lastType, currentScalingType, scaling);
			if (changeMessage != null) {
				player.sendMessage(changeMessage);
			}
		}
		
		lastScalingType.put(playerKey, currentScalingType);
	}

	/**
	 * NEW v5.0: Get scaling change message
	 */
	private String getScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
		if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
			return "<col=00ff00>Ascension Update: Combat scaling improved to balanced! Assistance reduced.</col>";
		} else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
			return "<col=ff9900>Ascension Update: Anti-farming scaling now active due to increased power!</col>";
		} else if (newType.contains("WITH_ABSORPTION")) {
			return "<col=66ccff>Ascension Update: Absorption bonuses detected and factored into scaling!</col>";
		} else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
			return "<col=00ff00>Ascension Update: Full armor protection restored! Damage scaling normalized.</col>";
		}
		
		return null;
	}

	/**
	 * ENHANCED v5.0: Intelligent phase tracking with BossBalancer integration
	 */
	private void updateIntelligentPhaseTracking(NPC npc, CombatScaling scaling) {
		Integer npcKey = Integer.valueOf(npc.getIndex());
		int newPhase = getCurrentPhase(npc);
		
		Integer lastPhase = currentPhase.get(npcKey);
		if (lastPhase == null) lastPhase = 1;
		
		if (newPhase > lastPhase) {
			currentPhase.put(npcKey, newPhase);
			handleIntelligentPhaseTransition(npc, newPhase, scaling);
		}
	}

	/**
	 * Get current phase based on HP (accounts for BossBalancer HP scaling)
	 */
	private int getCurrentPhase(NPC npc) {
		try {
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) return 1;
			
			// Calculate phase based on percentage (works regardless of HP scaling)
			double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
			
			if (hpPercent <= PHASE_4_THRESHOLD) return 4;
			if (hpPercent <= PHASE_3_THRESHOLD) return 3;
			if (hpPercent <= PHASE_2_THRESHOLD) return 2;
			return 1;
		} catch (Exception e) {
			return 1;
		}
	}

	/**
	 * ENHANCED v5.0: Intelligent phase transitions with scaling integration
	 */
	private void handleIntelligentPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
		switch (newPhase) {
		case 2:
			npc.setNextForceTalk(new ForceTalk("You think you can defeat me?!"));
			npc.setNextGraphics(new Graphics(3582));
			break;
			
		case 3:
			String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
				"Feel my ENHANCED power!" : "Feel my true power!";
			npc.setNextForceTalk(new ForceTalk(phase3Message));
			npc.setNextGraphics(new Graphics(4585));
			break;
			
		case 4:
			String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
				"THIS IS MY ULTIMATE ASCENDED FORM!" : 
				scaling.bossDamageMultiplier > 1.5 ? 
				"THIS IS MY ENHANCED FINAL FORM!" : "THIS IS MY FINAL FORM!";
			npc.setNextForceTalk(new ForceTalk(finalFormMessage));
			npc.setNextGraphics(new Graphics(5004));
			
			// Enhanced heal calculation with v5.0 scaling
			int baseHeal = npc.getMaxHitpoints() / 15;
			int scaledHeal = (int)(baseHeal * scaling.bossHpMultiplier);
			npc.heal(Math.max(scaledHeal, 50));
			break;
		}
	}

	/**
	 * ENHANCED v5.0: Perform attack with intelligent warning system
	 */
	private int performIntelligentAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
		try {
			// Increment attack counter
			Integer playerKey = Integer.valueOf(player.getIndex());
			Integer currentCount = attackCounter.get(playerKey);
			if (currentCount == null) currentCount = 0;
			attackCounter.put(playerKey, currentCount + 1);
			
			// Select attack pattern with v5.0 intelligence
			int phase = getCurrentPhase(npc);
			AttackPattern pattern = selectIntelligentAttackPattern(phase, scaling, currentCount);
			
			// Enhanced pre-attack warning system
			if (pattern.requiresWarning && shouldGiveIntelligentWarning(scaling, currentCount)) {
				sendIntelligentPreAttackWarning(player, pattern.warningMessage, scaling);
				
				// Adaptive delay based on scaling difficulty
				int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
				
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						executeIntelligentScaledAttack(npc, player, pattern, scaling);
						this.stop();
					}
				}, warningDelay);
				
				return getIntelligentAttackDelay(npc, phase, scaling) + warningDelay;
			} else {
				// Execute immediately for basic attacks
				executeIntelligentScaledAttack(npc, player, pattern, scaling);
				return getIntelligentAttackDelay(npc, phase, scaling);
			}
			
		} catch (Exception e) {
			return 4;
		}
	}

	/**
	 * ENHANCED v5.0: Intelligent warning probability based on scaling
	 */
	private boolean shouldGiveIntelligentWarning(CombatScaling scaling, int attackCount) {
		// More frequent warnings for undergeared players
		boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
		int warningFrequency = isUndergeared ? 6 : 8; // Every 6th vs 8th attack
		
		if (attackCount % warningFrequency != 0) return false;
		
		// Enhanced warning probability based on scaling
		if (scaling.bossDamageMultiplier > 3.0) {
			return Utils.random(2) == 0; // 50% chance for extreme difficulty
		} else if (scaling.bossDamageMultiplier > 1.8) {
			return Utils.random(3) == 0; // 33% chance for high difficulty
		} else {
			return Utils.random(4) == 0; // 25% chance for normal difficulty
		}
	}

	/**
	 * ENHANCED v5.0: Intelligent pre-attack warning with scaling context
	 */
	private void sendIntelligentPreAttackWarning(Player player, String warning, CombatScaling scaling) {
		String scalingNote = "";
		if (scaling.bossDamageMultiplier > 2.5) {
			scalingNote = " (EXTREME damage due to scaling!)";
		} else if (scaling.bossDamageMultiplier > 1.8) {
			scalingNote = " (High damage due to scaling!)";
		}
		
		player.sendMessage("<col=ff3300>WARNING: " + warning + scalingNote + "</col>");
	}

	/**
	 * ENHANCED v5.0: Intelligent attack pattern selection with scaling consideration
	 */
	private AttackPattern selectIntelligentAttackPattern(int phase, CombatScaling scaling, int attackCount) {
		int roll = Utils.random(100);
		
		// Enhanced special attack chances based on phase, scaling, and progression
		int baseSpecialChance = (phase - 1) * 12; // Increased from 10 to 12
		int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 8 : 0; // More specials for scaled fights
		int progressionBonus = attackCount > 15 ? 5 : 0; // More specials as fight progresses
		int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
		
		// v5.0 intelligent pattern selection
		boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
		
		if (isOvergeared) {
			// More aggressive patterns for overgeared players
			if (roll < 8 + specialChance) return ATTACK_PATTERNS[3]; // Devastating combo
			if (roll < 20 + specialChance) return ATTACK_PATTERNS[2]; // Spinning attack  
			if (roll < 35 + specialChance) return ATTACK_PATTERNS[1]; // Crushing blow
		} else {
			// Standard pattern selection
			if (roll < 5 + specialChance) return ATTACK_PATTERNS[3]; // Devastating combo
			if (roll < 15 + specialChance) return ATTACK_PATTERNS[2]; // Spinning attack  
			if (roll < 30 + specialChance) return ATTACK_PATTERNS[1]; // Crushing blow
		}
		
		return ATTACK_PATTERNS[0]; // Basic strike
	}

	/**
	 * ENHANCED v5.0: Execute attack with intelligent BossBalancer integration and HP-aware scaling
	 */
	private void executeIntelligentScaledAttack(NPC npc, Player player, AttackPattern pattern, CombatScaling scaling) {
		try {
			// Set animation and graphics
			npc.setNextAnimation(new Animation(pattern.animation));
			if (pattern.startGraphics > 0) {
				npc.setNextGraphics(new Graphics(pattern.startGraphics));
			}
			
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) return;
			
			// Enhanced phase damage calculation with v5.0 intelligence
			int phase = getCurrentPhase(npc);
			double phaseModifier = 1.0 + (phase - 1) * 0.18; // Increased from 15% to 18% per phase
			
			// Enhanced max hit calculation with v5.0 BossBalancer integration
			int baseMaxHit = defs.getMaxHit();
			int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
			int baseDamage = (int)(scaledMaxHit * phaseModifier);
			
			// Execute different attack types with v5.0 scaling and HP-aware damage
			if ("devastating_combo".equals(pattern.name)) {
				executeIntelligentDevastatingCombo(npc, player, baseDamage, scaling);
			} else if ("spinning_attack".equals(pattern.name)) {
				executeIntelligentSpinningAttack(npc, player, baseDamage, scaling);
			} else if ("crushing_blow".equals(pattern.name)) {
				executeIntelligentCrushingBlow(npc, player, baseDamage, scaling);
			} else {
				executeIntelligentSingleAttack(npc, player, baseDamage, 0, scaling, "basic");
			}
			
			// End graphics with enhanced timing
			if (pattern.endGraphics > 0) {
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						if (!npc.isDead() && !player.isDead()) {
							World.sendGraphics(npc, new Graphics(pattern.endGraphics), player);
						}
						this.stop();
					}
				}, 1);
			}
			
		} catch (Exception e) {
			// Enhanced fallback - execute basic attack with v5.0 scaling
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs != null) {
				int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
				executeIntelligentSingleAttack(npc, player, scaledDamage, 0, scaling, "basic");
			}
		}
	}

	/**
	 * ENHANCED v5.0: Intelligent devastating combo attack with HP-aware scaling
	 */
	private void executeIntelligentDevastatingCombo(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
		// First hit - 65% damage with variance (increased from 60%)
		int damage1 = (int)(baseDamage * 0.65) + Utils.random(baseDamage / 5);
		int scaledDamage1 = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage1);
		
		// CRITICAL: Apply HP-aware damage scaling for first hit
		int safeDamage1 = applyHPAwareDamageScaling(scaledDamage1, player, "devastating_combo");
		checkAndWarnLowHP(player, safeDamage1);
		
		// Second hit - 80% damage (increased from 75%)
		int damage2 = (int)(baseDamage * 0.80) + Utils.random(baseDamage / 5);
		int scaledDamage2 = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage2);
		
		// CRITICAL: Apply HP-aware damage scaling for second hit
		int safeDamage2 = applyHPAwareDamageScaling(scaledDamage2, player, "devastating_combo");
		
		delayHit(npc, 0, player, getMeleeHit(npc, safeDamage1));
		delayHit(npc, 1, player, getMeleeHit(npc, safeDamage2));
	}

	/**
	 * ENHANCED v5.0: Intelligent spinning attack with HP-aware scaling
	 */
	private void executeIntelligentSpinningAttack(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
		// Enhanced spinning attack damage (increased from 1.25x to 1.35x)
		int damage = (int)(baseDamage * 1.35) + Utils.random(baseDamage / 4);
		int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
		
		// CRITICAL: Apply HP-aware damage scaling for spinning attacks
		int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "spinning_attack");
		checkAndWarnLowHP(player, safeDamage);
		
		delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
	}

	/**
	 * NEW v5.0: Intelligent crushing blow attack with HP-aware scaling
	 */
	private void executeIntelligentCrushingBlow(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
		// Crushing blow damage (1.2x base)
		int damage = (int)(baseDamage * 1.2) + Utils.random(baseDamage / 6);
		int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
		
		// CRITICAL: Apply HP-aware damage scaling for crushing blows
		int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "crushing_blow");
		checkAndWarnLowHP(player, safeDamage);
		
		delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
	}

	/**
	 * ENHANCED v5.0: Intelligent single attack with proper scaling and HP-aware damage
	 */
	private void executeIntelligentSingleAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
		int damage = Utils.random(baseDamage + 1);
		int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
		
		// CRITICAL: Apply HP-aware damage scaling
		int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, attackType);
		if (!"basic".equals(attackType)) {
			checkAndWarnLowHP(player, safeDamage);
		}
		
		delayHit(npc, delay, player, getMeleeHit(npc, safeDamage));
	}

	/**
	 * ENHANCED v5.0: Intelligent attack delay with scaling consideration
	 */
	private int getIntelligentAttackDelay(NPC npc, int phase, CombatScaling scaling) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (defs == null) return 4;
		
		int baseDelay = defs.getAttackDelay();
		int phaseSpeedBonus = Math.max(0, phase - 1);
		
		// v5.0 intelligent scaling can affect attack speed
		int scalingSpeedBonus = 0;
		if (scaling.bossDamageMultiplier > 2.5) {
			scalingSpeedBonus = 2; // Much faster for extreme scaling
		} else if (scaling.bossDamageMultiplier > 1.8) {
			scalingSpeedBonus = 1; // Faster for high scaling
		}
		
		return Math.max(2, baseDelay - phaseSpeedBonus - scalingSpeedBonus);
	}

	/**
	 * Enhanced combat state validation
	 */
	private boolean isValidCombatState(NPC npc, Entity target) {
		return npc != null && target != null && 
			   !npc.isDead() && !target.isDead() && 
			   target instanceof Player &&
			   npc.getCombatDefinitions() != null;
	}

	/**
	 * ENHANCED v5.0: Handle combat end with proper cleanup
	 */
	public static void onCombatEnd(Player player, NPC npc) {
		if (player == null) return;
		
		try {
			// End BossBalancer v5.0 combat session
			BossBalancer.endCombatSession(player);
			
			// Clear local tracking maps
			Integer playerKey = Integer.valueOf(player.getIndex());
			Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
			
			lastWarning.remove(playerKey);
			warningStage.remove(playerKey);
			currentPhase.remove(npcKey);
			combatSessionActive.remove(playerKey);
			lastScalingType.remove(playerKey);
			attackCounter.remove(playerKey);
			
			// Clear BossBalancer player cache
			BossBalancer.clearPlayerCache(player.getIndex());
			
			// Send completion message with v5.0 info
			player.sendMessage("<col=4169E1>Combat session ended. Intelligent scaling data cleared.</col>");
			
		} catch (Exception e) {
			System.err.println("MasutaCombat: Error ending v5.0 combat session: " + e.getMessage());
		}
	}

	/**
	 * ENHANCED v5.0: Handle prayer changes during combat
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
				player.sendMessage("<col=66ccff>Prayer change detected. Scaling analysis updated.</col>");
			}
		} catch (Exception e) {
			System.err.println("MasutaCombat: Error handling v5.0 prayer change: " + e.getMessage());
		}
	}

	/**
	 * Force cleanup (call on logout/death)
	 */
	public static void forceCleanup(Player player) {
		if (player != null) {
			onCombatEnd(player, null);
		}
	}

	/**
	 * Enhanced attack pattern data structure
	 */
	private static class AttackPattern {
		final int animation;
		final int startGraphics;
		final int endGraphics;
		final String name;
		final boolean requiresWarning;
		final String warningMessage;

		AttackPattern(int animation, int startGraphics, int endGraphics, String name, 
					  boolean requiresWarning, String warningMessage) {
			this.animation = animation;
			this.startGraphics = startGraphics;
			this.endGraphics = endGraphics;
			this.name = name;
			this.requiresWarning = requiresWarning;
			this.warningMessage = warningMessage;
		}
	}
}