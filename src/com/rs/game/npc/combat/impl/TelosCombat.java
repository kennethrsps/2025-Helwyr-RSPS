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
import com.rs.game.player.content.Combat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Telos Combat System with FULL BossBalancer v5.0 Integration
 * Features: Intelligent power-based scaling, armor analysis, HP-aware damage scaling
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 11.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System
 */
public class TelosCombat extends CombatScript {

	// Enhanced guidance system - intelligent scaling aware
	private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
	private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
	private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
	private static final Map<Integer, Boolean> hasGivenWelcome = new ConcurrentHashMap<Integer, Boolean>();
	
	// Timing constants - enhanced for v5.0
	private static final long WARNING_COOLDOWN = 300000; // 5 minutes between warnings
	private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
	private static final long PRE_ATTACK_WARNING_TIME = 3000; // 3 seconds for telos attacks
	private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features
	
	// HP-aware damage scaling constants - CRITICAL SAFETY SYSTEM
	private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.40; // Max 40% of player HP per hit
	private static final double CRITICAL_DAMAGE_PERCENT = 0.60;  // Max 60% for critical attacks (devastating)
	private static final double POISON_DAMAGE_PERCENT = 0.25;    // Max 25% for poison attacks
	private static final double DRAGONFIRE_DAMAGE_PERCENT = 0.55; // Max 55% for dragonfire
	private static final double DOUBLE_ATTACK_PERCENT = 0.35;    // Max 35% per hit in double attacks
	private static final int MIN_PLAYER_HP = 990;
	private static final int MAX_PLAYER_HP = 1500;
	private static final int ABSOLUTE_MAX_DAMAGE = 600;          // Hard cap (40% of 1500 HP)
	private static final int MINIMUM_DAMAGE = 40;               // Minimum damage to prevent 0 hits
	
	// Safespot detection - enhanced
	private static final int MAX_SAFESPOT_DISTANCE = 12;
	private static final int MIN_ENGAGEMENT_DISTANCE = 2;
	private static final int SAFESPOT_WARNING_CHANCE = 15; // 1 in 15 chance

	@Override
	public Object[] getKeys() {
		return new Object[] { 22891 };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (defs == null) {
			return 4;
		}
		
		if (!(target instanceof Player)) {
			return defs.getAttackDelay();
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
		
		// Enhanced safespot detection with v5.0 intelligence
		checkIntelligentSafespotting(npc, player, scaling);
		
		// Execute attack with v5.0 warnings and HP-aware scaling
		return performIntelligentAttackWithWarning(npc, player, scaling, defs);
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
			hasGivenWelcome.put(sessionKey, Boolean.FALSE);
			
			// Send v5.0 enhanced combat message
			String welcomeMsg = "<col=4169E1>Telos the Warden awakens. Intelligent earth magic analysis active (v5.0).</col>";
			player.sendMessage(welcomeMsg);
			
			// Perform initial armor check
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
				player.sendMessage("<col=ff6600>Armor Analysis: Missing protection detected. Telos will deal increased damage!</col>");
			} else {
				double reductionPercent = armorResult.damageReduction * 100;
				player.sendMessage("<col=00ff00>Armor Analysis: Full protection active (" + 
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
				case "devastating":
				case "critical":
					damagePercent = CRITICAL_DAMAGE_PERCENT;
					break;
				case "poison":
					damagePercent = POISON_DAMAGE_PERCENT;
					break;
				case "dragonfire":
					damagePercent = DRAGONFIRE_DAMAGE_PERCENT;
					break;
				case "double_attack":
					damagePercent = DOUBLE_ATTACK_PERCENT;
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
				
				if (damagePercent >= 0.75) {
					player.sendMessage("<col=ff0000>WARNING: This attack will deal " + incomingDamage + 
									 " damage! (" + currentHP + " HP remaining)</col>");
				} else if (damagePercent >= 0.55) {
					player.sendMessage("<col=ff6600>WARNING: Heavy damage incoming (" + incomingDamage + 
									 ")! Consider healing (" + currentHP + " HP)</col>");
				}
			}
		} catch (Exception e) {
			// Ignore warning errors
		}
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
		Integer currentCount = attackCounter.get(Integer.valueOf(player.getIndex()));
		if (currentCount == null) currentCount = 0;
		
		switch (stage) {
			case 0:
				// First warning: Power analysis and scaling type
				return getScalingAnalysisMessage(scaling);
				
			case 1:
				// Second warning: Equipment effectiveness or combat progression
				if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
					return "<col=ff3300>Combat Analysis: Incomplete armor detected! Telos damage increased by 25%. Equip missing pieces!</col>";
				} else if (currentCount > 15) {
					return "<col=ff6600>Combat Analysis: Telos advanced phase. Multi-style attacks intensified. Protection prayers essential!</col>";
				}
				break;
				
			case 2:
				// Third warning: Advanced combat mechanics
				if (currentCount > 30) {
					return "<col=ff0000>Combat Analysis: Telos ultimate phase! Devastating attacks unlocked. Maximum defensive measures required!</col>";
				}
				break;
		}
		
		return null;
	}

	/**
	 * NEW v5.0: Get scaling analysis message
	 */
	private String getScalingAnalysisMessage(CombatScaling scaling) {
		String baseMessage = "<col=66ccff>Power Analysis:</col> ";
		
		if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
			return baseMessage + "<col=00ff00>Assistance mode active! Telos difficulty reduced by " + 
				   assistancePercent + "% due to gear disadvantage.</col>";
				   
		} else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=ff6600>Anti-farming scaling active! Telos difficulty increased by " + 
				   difficultyIncrease + "% due to gear advantage.</col>";
				   
		} else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
			return baseMessage + "<col=ffffff>Balanced encounter detected. Optimal gear-to-boss ratio achieved!</col>";
			
		} else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=ffaa00>Slight overgear detected. Telos difficulty increased by " + 
				   difficultyIncrease + "% for balance.</col>";
		}
		
		return baseMessage + "<col=cccccc>Power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
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
			return "<col=00ff00>Power Update: Combat scaling improved to balanced! Assistance reduced.</col>";
		} else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
			return "<col=ff9900>Power Update: Anti-farming scaling now active due to increased power!</col>";
		} else if (newType.contains("WITH_ABSORPTION")) {
			return "<col=66ccff>Power Update: Absorption bonuses detected and factored into scaling!</col>";
		} else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
			return "<col=00ff00>Power Update: Full armor protection restored! Damage scaling normalized.</col>";
		}
		
		return null;
	}

	/**
	 * ENHANCED v5.0: Perform attack with intelligent warning system
	 */
	private int performIntelligentAttackWithWarning(NPC npc, Player player, CombatScaling scaling, NPCCombatDefinitions defs) {
		try {
			// Increment attack counter
			Integer playerKey = Integer.valueOf(player.getIndex());
			Integer currentCount = attackCounter.get(playerKey);
			if (currentCount == null) currentCount = 0;
			attackCounter.put(playerKey, currentCount + 1);
			
			// Determine attack type with v5.0 intelligence
			String attackType = determineIntelligentAttackType(npc, player, scaling, currentCount);
			
			// Check if this attack needs a warning (v5.0 enhanced)
			boolean needsWarning = shouldGiveIntelligentWarning(attackType, currentCount, scaling);
			
			if (needsWarning) {
				String warningMessage = getIntelligentAttackWarning(attackType, scaling);
				if (warningMessage != null) {
					sendPreAttackWarning(player, warningMessage);
					
					// Delay the actual attack to give player time to react
					final String finalAttackType = attackType;
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							executeIntelligentAttackSequence(npc, player, scaling, finalAttackType);
							this.stop();
						}
					}, 2); // 1.2 second delay for reaction time
					
					return defs.getAttackDelay() + 2;
				}
			}
			
			// Execute immediately for basic attacks
			executeIntelligentAttackSequence(npc, player, scaling, attackType);
			return defs.getAttackDelay();
			
		} catch (Exception e) {
			return 4;
		}
	}

	/**
	 * ENHANCED v5.0: Determine attack type with intelligent scaling awareness
	 */
	private String determineIntelligentAttackType(NPC npc, Player player, CombatScaling scaling, int attackCount) {
		boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
		boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
		
		// Advanced phase triggers (more frequent for overgeared players)
		boolean advancedPhase = attackCount > (isOvergeared ? 15 : (isUndergeared ? 25 : 20));
		boolean ultimatePhase = attackCount > (isOvergeared ? 30 : (isUndergeared ? 45 : 35));
		
		if (npc.withinDistance(player, npc.getSize())) {
			// Close range attacks with v5.0 intelligence
			if (ultimatePhase && Utils.random(isOvergeared ? 3 : 5) == 0) {
				return "DEVASTATING_MELEE";
			} else if (advancedPhase && Utils.random(4) == 0) {
				return "DOUBLE_ATTACK";
			} else {
				int attackChoice = Utils.random(5);
				switch (attackChoice) {
					case 0: return "DEVASTATING_MELEE";
					case 1: return "RANGE";
					case 2: return "POISON";
					case 3: return "MAGIC";
					default: return "MELEE";
				}
			}
		} else {
			// Long range attacks with v5.0 intelligence
			if (ultimatePhase && Utils.random(4) == 0) {
				return "DRAGONFIRE";
			} else if (advancedPhase && Utils.random(5) == 0) {
				return "POISON";
			} else {
				int attackChoice = Utils.random(4);
				switch (attackChoice) {
					case 0: return "DEVASTATING_MELEE";
					case 1: return "RANGE";
					case 2: return "POISON";
					default: return "MAGIC";
				}
			}
		}
	}

	/**
	 * ENHANCED v5.0: Determine if attack needs warning with scaling awareness
	 */
	private boolean shouldGiveIntelligentWarning(String attackType, int attackCount, CombatScaling scaling) {
		// More frequent warnings for undergeared players
		boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
		int warningFrequency = isUndergeared ? 10 : 15; // Every 10th vs 15th attack
		
		if (attackCount % warningFrequency != 0) return false;
		
		return "DEVASTATING_MELEE".equals(attackType) || "POISON".equals(attackType) || 
			   "DRAGONFIRE".equals(attackType) || "DOUBLE_ATTACK".equals(attackType);
	}

	/**
	 * ENHANCED v5.0: Get intelligent attack warning message
	 */
	private String getIntelligentAttackWarning(String attackType, CombatScaling scaling) {
		boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
		String intensityPrefix = isOvergeared ? "ENHANCED " : "";
		
		switch (attackType) {
			case "DEVASTATING_MELEE":
				return intensityPrefix + "DEVASTATING STRIKE incoming - Telos charges devastating earth power!";
			case "POISON":
				return intensityPrefix + "ANCIENT CURSE incoming - prepare antipoison defenses!";
			case "DRAGONFIRE":
				return intensityPrefix + "ANCIENT FLAMES incoming - use dragonfire protection!";
			case "DOUBLE_ATTACK":
				return intensityPrefix + "DOUBLE STRIKE incoming - dual earth magic assault!";
			default:
				return null;
		}
	}

	/**
	 * Send pre-attack warning
	 */
	private void sendPreAttackWarning(Player player, String warning) {
		player.sendMessage("<col=ff3300>WARNING: " + warning + "</col>");
	}

	/**
	 * ENHANCED v5.0: Execute intelligent attack sequence with proper scaling
	 */
	private void executeIntelligentAttackSequence(NPC npc, Player player, CombatScaling scaling, String attackType) {
		switch (attackType) {
			case "DEVASTATING_MELEE":
				performIntelligentMeleeAttack3(npc, player, scaling);
				break;
			case "RANGE":
				performIntelligentRangeAttack(npc, player, scaling);
				break;
			case "POISON":
				performIntelligentPoisonAttack(npc, player, scaling);
				break;
			case "MAGIC":
				performIntelligentMageAttack(npc, player, scaling);
				break;
			case "DRAGONFIRE":
				performIntelligentDragonFireAttack(npc, player, scaling);
				break;
			case "DOUBLE_ATTACK":
				performIntelligentMeleeAttack2(npc, player, scaling);
				break;
			case "MELEE":
			default:
				performIntelligentMeleeAttack(npc, player, scaling);
				break;
		}
	}

	/**
	 * ENHANCED v5.0: Check safespotting with intelligent warnings
	 */
	private void checkIntelligentSafespotting(NPC npc, Player player, CombatScaling scaling) {
		int distance = player.getDistance(npc);
		
		if (distance > MAX_SAFESPOT_DISTANCE) {
			// Enhanced safespot warning with v5.0 intelligence
			boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
			int warningChance = isOvergeared ? SAFESPOT_WARNING_CHANCE / 2 : SAFESPOT_WARNING_CHANCE;
			
			if (Utils.random(warningChance) == 0) {
				npc.setNextForceTalk(new ForceTalk("Face the Warden with honor!"));
				sendCombatMessage(player, "Telos demands proper engagement within " + MAX_SAFESPOT_DISTANCE + " tiles.");
			}
			
			// Reset if too far (more aggressive for overgeared players)
			int resetDistance = isOvergeared ? MAX_SAFESPOT_DISTANCE + 2 : MAX_SAFESPOT_DISTANCE + 3;
			if (distance > resetDistance) {
				npc.resetCombat();
				sendCombatMessage(player, "Telos loses interest in dishonorable tactics.");
			}
		}
	}

	// ===== INTELLIGENT ATTACK METHODS WITH v5.0 INTEGRATION =====

	/**
	 * ENHANCED v5.0: Perform intelligent magic attack with HP-aware scaling
	 */
	public void performIntelligentMageAttack(NPC npc, Entity target, CombatScaling scaling) {
		if (Utils.random(8) == 0) {
			npc.setNextForceTalk(new ForceTalk("Power from the ground!"));
		}
		
		for (Entity t : npc.getPossibleTargets()) {
			npc.setNextAnimation(new Animation(28959));
			World.sendGraphics(npc, new Graphics(5513), t);
			
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) continue;
			
			// Use v5.0 BossBalancer scaling methods
			int baseDamage = (int) (defs.getMaxHit() * Utils.random(1.0, 1.5)); // Earth magic power
			int scaledDamage = BossBalancer.applyBossScaling(baseDamage, 
				t instanceof Player ? (Player) t : null, npc);
			
			// CRITICAL: Apply HP-aware damage scaling
			int safeDamage = scaledDamage;
			if (t instanceof Player) {
				safeDamage = applyHPAwareDamageScaling(scaledDamage, (Player) t, "magic");
				checkAndWarnLowHP((Player) t, safeDamage);
			}
			
			delayHit(npc, 0, t, getMagicHit(npc, safeDamage));
		}
	}

	/**
	 * ENHANCED v5.0: Perform intelligent ranged attack with HP-aware scaling
	 */
	public void performIntelligentRangeAttack(NPC npc, Entity target, CombatScaling scaling) {
		if (Utils.random(8) == 0) {
			npc.setNextForceTalk(new ForceTalk("I'm Telos, The Warden!"));
		}
		
		for (Entity t : npc.getPossibleTargets()) {
			npc.setNextAnimation(new Animation(29006));
			World.sendProjectile(npc, t, 6000, 41, 16, 41, 35, 16, 0);
			
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) continue;
			
			// Use v5.0 BossBalancer scaling methods
			int baseDamage = (int) (defs.getMaxHit() * Utils.random(0.9, 1.4)); // Ranged precision
			int scaledDamage = BossBalancer.applyBossScaling(baseDamage, 
				t instanceof Player ? (Player) t : null, npc);
			
			// CRITICAL: Apply HP-aware damage scaling
			int safeDamage = scaledDamage;
			if (t instanceof Player) {
				safeDamage = applyHPAwareDamageScaling(scaledDamage, (Player) t, "ranged");
				checkAndWarnLowHP((Player) t, safeDamage);
			}
			
			delayHit(npc, 0, t, getRangeHit(npc, safeDamage));
		}
	}

	/**
	 * ENHANCED v5.0: Perform intelligent poison attack with HP-aware scaling
	 */
	public void performIntelligentPoisonAttack(NPC npc, Entity target, CombatScaling scaling) {
		if (Utils.random(8) == 0) {
			npc.setNextForceTalk(new ForceTalk("Ancient curse consumes you!"));
		}
		
		for (Entity t : npc.getPossibleTargets()) {
			final Player player = t instanceof Player ? (Player) t : null;
			if (player != null) {
				npc.setNextAnimation(new Animation(28959));
				World.sendGraphics(npc, new Graphics(6245), npc);
				
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						NPCCombatDefinitions defs = npc.getCombatDefinitions();
						if (defs == null) {
							stop();
							return;
						}
						
						// Use v5.0 BossBalancer scaling methods
						int baseDamage = (int) (defs.getMaxHit() * Utils.random(1.1, 1.7)); // Curse power
						int scaledDamage = BossBalancer.applyBossScaling(baseDamage, player, npc);
						
						// CRITICAL: Apply HP-aware damage scaling
						int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "poison");
						checkAndWarnLowHP(player, safeDamage);
						
						delayHit(npc, 0, t, getMagicHit(npc, safeDamage));
						player.setNextGraphics(new Graphics(6129, 50, 0));
						
						// Apply intelligent poison with v5.0 scaling
						applyIntelligentPoisonEffect(player, scaling);
						
						if (Utils.random(4) == 0) { // Only occasionally send poison message
							sendCombatMessage(player, "Ancient curse flows through you! Use antipoison.");
						}
						
						stop();
					}
				}, 0);
			}
		}
	}

	/**
	 * NEW v5.0: Apply intelligent poison effect with scaling awareness
	 */
	private void applyIntelligentPoisonEffect(Player player, CombatScaling scaling) {
		try {
			// Calculate base poison strength with v5.0 scaling
			int basePoisonStrength = 45 + (BossBalancer.getBossEffectiveTier(null) * 5);
			
			// Apply scaling modifiers
			if (scaling.scalingType.contains("ANTI_FARMING")) {
				basePoisonStrength = (int)(basePoisonStrength * scaling.bossDamageMultiplier);
			} else if (scaling.scalingType.contains("ASSISTANCE")) {
				basePoisonStrength = (int)(basePoisonStrength * 0.8); // 20% less poison for undergeared
			}
			
			// Cap poison strength
			int poisonStrength = Math.min(120, Math.max(30, basePoisonStrength));
			
			player.getPoison().makePoisoned(poisonStrength);
			
		} catch (Exception e) {
			// Fallback poison
			player.getPoison().makePoisoned(60);
		}
	}

	/**
	 * ENHANCED v5.0: Perform intelligent melee attack with HP-aware scaling
	 */
	public void performIntelligentMeleeAttack(NPC npc, Entity target, CombatScaling scaling) {
		for (Entity t : npc.getPossibleTargets()) {
			npc.setNextAnimation(new Animation(28929));
			World.sendGraphics(npc, new Graphics(5516), t);
			
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) continue;
			
			// Use v5.0 BossBalancer scaling methods
			int baseDamage = (int) (defs.getMaxHit() * Utils.random(0.8, 1.3)); // Standard melee
			int scaledDamage = BossBalancer.applyBossScaling(baseDamage, 
				t instanceof Player ? (Player) t : null, npc);
			
			// CRITICAL: Apply HP-aware damage scaling
			int safeDamage = scaledDamage;
			if (t instanceof Player) {
				safeDamage = applyHPAwareDamageScaling(scaledDamage, (Player) t, "melee");
				checkAndWarnLowHP((Player) t, safeDamage);
			}
			
			delayHit(npc, 0, t, getMeleeHit(npc, safeDamage));
		}
	}

	/**
	 * ENHANCED v5.0: Perform intelligent devastating melee attack with HP-aware scaling
	 */
	public void performIntelligentMeleeAttack3(NPC npc, Entity target, CombatScaling scaling) {
		if (Utils.random(6) == 0) {
			npc.setNextForceTalk(new ForceTalk("Feel my devastating might!"));
		}
		
		for (Entity t : npc.getPossibleTargets()) {
			npc.setNextAnimation(new Animation(28938));
			World.sendGraphics(npc, new Graphics(6246), npc);
			
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) continue;
			
			// Use v5.0 BossBalancer scaling methods for devastating attack
			int baseDamage = (int) (defs.getMaxHit() * Utils.random(1.4, 2.0)); // Devastating power
			int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, 
				t instanceof Player ? (Player) t : null, npc);
			
			// CRITICAL: Apply HP-aware damage scaling for devastating attacks
			int safeDamage = scaledDamage;
			if (t instanceof Player) {
				safeDamage = applyHPAwareDamageScaling(scaledDamage, (Player) t, "devastating");
				checkAndWarnLowHP((Player) t, safeDamage);
			}
			
			delayHit(npc, 5, t, getMeleeHit(npc, safeDamage));
		}
	}

	/**
	 * ENHANCED v5.0: Perform intelligent double melee attack with HP-aware scaling
	 */
	public void performIntelligentMeleeAttack2(NPC npc, Entity target, CombatScaling scaling) {
		if (Utils.random(8) == 0) {
			npc.setNextForceTalk(new ForceTalk("Double strike of the warden!"));
		}
		npc.setNextAnimation(new Animation(28935));
		
		for (Entity t : npc.getPossibleTargets()) {
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) continue;
			
			// First hit (lighter) - Use v5.0 BossBalancer scaling
			int damage1 = (int) (defs.getMaxHit() * Utils.random(0.6, 1.0));
			int scaledDamage1 = BossBalancer.applyBossScaling(damage1, 
				t instanceof Player ? (Player) t : null, npc);
			
			// CRITICAL: Apply HP-aware damage scaling for double attacks
			int safeDamage1 = scaledDamage1;
			if (t instanceof Player) {
				safeDamage1 = applyHPAwareDamageScaling(scaledDamage1, (Player) t, "double_attack");
				checkAndWarnLowHP((Player) t, safeDamage1);
			}
			delayHit(npc, 2, t, getMeleeHit(npc, safeDamage1));
			
			// Second hit (stronger) - Use v5.0 BossBalancer scaling
			int damage2 = (int) (defs.getMaxHit() * Utils.random(1.0, 1.5));
			int scaledDamage2 = BossBalancer.applyBossScaling(damage2, 
				t instanceof Player ? (Player) t : null, npc);
			
			// CRITICAL: Apply HP-aware damage scaling for second hit
			int safeDamage2 = scaledDamage2;
			if (t instanceof Player) {
				safeDamage2 = applyHPAwareDamageScaling(scaledDamage2, (Player) t, "double_attack");
			}
			delayHit(npc, 7, t, getMeleeHit(npc, safeDamage2));
		}
	}

	/**
	 * ENHANCED v5.0: Perform intelligent dragonfire attack with HP-aware scaling
	 */
	public void performIntelligentDragonFireAttack(NPC npc, Entity target, CombatScaling scaling) {
		if (Utils.random(8) == 0) {
			npc.setNextForceTalk(new ForceTalk("Ancient flames consume all!"));
		}
		
		final Player player = target instanceof Player ? (Player) target : null;
		
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (defs == null) return;
		
		// Use v5.0 BossBalancer scaling methods
		int baseDamage = (int) (defs.getMaxHit() * Utils.random(0.8, 1.8)); // Fire power
		int scaledDamage = BossBalancer.applyBossScaling(baseDamage, player, npc);
		
		// Apply dragonfire protection
		if (player != null) {
			String message = Combat.getProtectMessage(player);
			if (message != null) {
				if (message.contains("fully")) {
					scaledDamage = 0;
				} else if (message.contains("most")) {
					scaledDamage = (int) (scaledDamage * 0.05);
				} else if (message.contains("some")) {
					scaledDamage = (int) (scaledDamage * 0.1);
				}
			}
			
			// CRITICAL: Apply HP-aware damage scaling for dragonfire
			if (scaledDamage > 0) {
				scaledDamage = applyHPAwareDamageScaling(scaledDamage, player, "dragonfire");
				checkAndWarnLowHP(player, scaledDamage);
			}
		}
		
		npc.setNextAnimation(new Animation(28959));
		World.sendGraphics(npc, new Graphics(5513), player);
		delayHit(npc, 1, target, getRegularHit(npc, scaledDamage));
	}

	/**
	 * Send combat message
	 */
	private void sendCombatMessage(Player player, String message) {
		player.sendMessage("<col=4169E1>Combat Analysis: " + message + "</col>");
	}

	/**
	 * ENHANCED v5.0: Handle combat end with proper cleanup
	 */
	public static void onCombatEnd(Player player, NPC npc) {
		if (player == null) return;
		
		try {
			// End BossBalancer v5.0 combat session
			BossBalancer.endCombatSession(player);
			
			// Clear local tracking
			Integer playerKey = Integer.valueOf(player.getIndex());
			
			lastWarning.remove(playerKey);
			warningStage.remove(playerKey);
			combatSessionActive.remove(playerKey);
			attackCounter.remove(playerKey);
			lastScalingType.remove(playerKey);
			hasGivenWelcome.remove(playerKey);
			
			// Clear BossBalancer player cache
			BossBalancer.clearPlayerCache(player.getIndex());
			
			// Send completion message with v5.0 info
			player.sendMessage("<col=4169E1>Combat session ended. Intelligent scaling data cleared.</col>");
			
		} catch (Exception e) {
			System.err.println("TelosCombat: Error ending v5.0 combat session: " + e.getMessage());
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
			System.err.println("TelosCombat: Error handling v5.0 prayer change: " + e.getMessage());
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

	// ===== LEGACY COMPATIBILITY METHODS WITH v5.0 ENHANCEMENT =====

	public void mageAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentTelosScaling(npc, target);
		performIntelligentMageAttack(npc, target, scaling);
	}

	public void rangeAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentTelosScaling(npc, target);
		performIntelligentRangeAttack(npc, target, scaling);
	}

	public void poisonAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentTelosScaling(npc, target);
		performIntelligentPoisonAttack(npc, target, scaling);
	}

	public void meleeAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentTelosScaling(npc, target);
		performIntelligentMeleeAttack(npc, target, scaling);
	}

	public void meleeAttack3(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentTelosScaling(npc, target);
		performIntelligentMeleeAttack3(npc, target, scaling);
	}

	public void meleeAttack2(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentTelosScaling(npc, target);
		performIntelligentMeleeAttack2(npc, target, scaling);
	}

	public void dragonFireAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentTelosScaling(npc, target);
		performIntelligentDragonFireAttack(npc, target, scaling);
	}

	/**
	 * ENHANCED v5.0: Get combat scaling using BossBalancer v5.0 methods
	 */
	private CombatScaling getIntelligentTelosScaling(NPC npc, Entity target) {
		if (target instanceof Player) {
			Player player = (Player) target;
			
			// Use BossBalancer v5.0 intelligent method
			return BossBalancer.getIntelligentCombatScaling(player, npc);
		}
		
		// Return default scaling for non-players using v5.0 structure
		CombatScaling defaultScaling = new CombatScaling();
		defaultScaling.playerTier = 6;
		defaultScaling.bossTier = 6;
		defaultScaling.bossHpMultiplier = 1.0;
		defaultScaling.bossDamageMultiplier = 1.0;
		defaultScaling.bossAccuracyMultiplier = 1.0;
		defaultScaling.scalingType = "BALANCED_ENCOUNTER";
		defaultScaling.playerPower = 6.0;
		defaultScaling.bossPower = 6.0;
		defaultScaling.powerRatio = 1.0;
		return defaultScaling;
	}

	// Enhanced method aliases for v5.0
	public void enhancedMageAttack(NPC npc, Entity target, CombatScaling scaling) {
		performIntelligentMageAttack(npc, target, scaling);
	}

	public void enhancedRangeAttack(NPC npc, Entity target, CombatScaling scaling) {
		performIntelligentRangeAttack(npc, target, scaling);
	}

	public void enhancedPoisonAttack(NPC npc, Entity target, CombatScaling scaling) {
		performIntelligentPoisonAttack(npc, target, scaling);
	}

	public void enhancedMeleeAttack(NPC npc, Entity target, CombatScaling scaling) {
		performIntelligentMeleeAttack(npc, target, scaling);
	}

	public void enhancedMeleeAttack3(NPC npc, Entity target, CombatScaling scaling) {
		performIntelligentMeleeAttack3(npc, target, scaling);
	}

	public void enhancedMeleeAttack2(NPC npc, Entity target, CombatScaling scaling) {
		performIntelligentMeleeAttack2(npc, target, scaling);
	}

	public void enhancedDragonFireAttack(NPC npc, Entity target, CombatScaling scaling) {
		performIntelligentDragonFireAttack(npc, target, scaling);
	}
}