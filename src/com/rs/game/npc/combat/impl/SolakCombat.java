package com.rs.game.npc.combat.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldTile;
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
 * Enhanced Solak Combat System with FULL BossBalancer v5.0 Integration
 * Features: Intelligent power-based scaling, armor analysis, HP-aware damage scaling, nature mechanics
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 4.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System
 */
public class SolakCombat extends CombatScript {

	// Enhanced guidance system - intelligent scaling aware
	private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
	private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
	private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
	private static final Map<Integer, Boolean> hasGivenWelcome = new ConcurrentHashMap<Integer, Boolean>();
	
	// Timing constants - enhanced for v5.0
	private static final long WARNING_COOLDOWN = 240000; // 4 minutes between warnings
	private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
	private static final long PRE_ATTACK_WARNING_TIME = 3000; // 3 seconds for solak attacks
	private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features
	
	// HP-aware damage scaling constants - CRITICAL SAFETY SYSTEM
	private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.38; // Max 38% of player HP per hit
	private static final double CRITICAL_DAMAGE_PERCENT = 0.55;  // Max 55% for critical attacks (fury)
	private static final double POISON_DAMAGE_PERCENT = 0.20;    // Max 20% for poison attacks
	private static final double AOE_DAMAGE_PERCENT = 0.45;       // Max 45% for AOE attacks
	private static final double NATURE_MAGIC_PERCENT = 0.42;     // Max 42% for nature magic
	private static final int MIN_PLAYER_HP = 990;
	private static final int MAX_PLAYER_HP = 1500;
	private static final int ABSOLUTE_MAX_DAMAGE = 570;          // Hard cap (38% of 1500 HP)
	private static final int MINIMUM_DAMAGE = 35;               // Minimum damage to prevent 0 hits
	
	// Safespot detection - enhanced for nature boss
	private static final int MAX_SAFESPOT_DISTANCE = 16; // Nature boss needs larger range
	private static final int MIN_ENGAGEMENT_DISTANCE = 2;
	private static final int SAFESPOT_WARNING_CHANCE = 12; // 1 in 12 chance

	@Override
	public Object[] getKeys() {
		return new Object[] { 25513 };
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
			String welcomeMsg = "<col=4169E1>Solak, Guardian of the Grove awakens. Intelligent nature analysis active (v5.0).</col>";
			player.sendMessage(welcomeMsg);
			
			// Perform initial armor and nature resistance check
			performInitialNatureAnalysis(player);
			
			// Send enhanced welcome guidance
			sendEnhancedWelcomeGuidance(npc, player);
		}
	}

	/**
	 * NEW v5.0: Perform initial nature analysis including armor
	 */
	private void performInitialNatureAnalysis(Player player) {
		try {
			// Use BossBalancer v5.0 armor analysis
			BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
			
			if (!armorResult.hasFullArmor) {
				player.sendMessage("<col=ff6600>Nature Analysis: Missing protection detected. Solak will deal increased damage!</col>");
			} else {
				double reductionPercent = armorResult.damageReduction * 100;
				player.sendMessage("<col=00ff00>Nature Analysis: Full protection active (" + 
								 String.format("%.1f", reductionPercent) + "% damage reduction).</col>");
			}
			
			// Check for nature resistance (magic defense)
			int magicLevel = player.getSkills().getLevel(Skills.MAGIC);
			if (magicLevel >= 80) {
				player.sendMessage("<col=66ccff>Nature Analysis: High magic level detected. Good resistance to grove magic!</col>");
			} else if (magicLevel >= 60) {
				player.sendMessage("<col=ffaa00>Nature Analysis: Moderate magic level. Some grove magic resistance.</col>");
			} else {
				player.sendMessage("<col=ff9900>Nature Analysis: Low magic level. Vulnerable to nature magic!</col>");
			}
			
		} catch (Exception e) {
			// Ignore analysis errors
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
				case "fury":
				case "critical":
					damagePercent = CRITICAL_DAMAGE_PERCENT;
					break;
				case "poison":
				case "spores":
					damagePercent = POISON_DAMAGE_PERCENT;
					break;
				case "aoe":
				case "devastation":
					damagePercent = AOE_DAMAGE_PERCENT;
					break;
				case "nature_magic":
				case "grove_magic":
					damagePercent = NATURE_MAGIC_PERCENT;
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
				// Second warning: Equipment effectiveness or nature mechanics
				if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
					return "<col=ff3300>Nature Analysis: Incomplete armor detected! Solak damage increased by 25%. Equip missing pieces!</col>";
				} else if (currentCount > 10) {
					return "<col=ff6600>Nature Analysis: Solak's grove magic intensifying. Poison resistance and mobility critical!</col>";
				}
				break;
				
			case 2:
				// Third warning: Advanced nature mechanics
				if (currentCount > 20) {
					return "<col=ff0000>Nature Analysis: Solak's ultimate grove power! AOE devastation and fury attacks unlocked!</col>";
				}
				break;
		}
		
		return null;
	}

	/**
	 * NEW v5.0: Get scaling analysis message
	 */
	private String getScalingAnalysisMessage(CombatScaling scaling) {
		String baseMessage = "<col=66ccff>Nature Power Analysis:</col> ";
		
		if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
			return baseMessage + "<col=00ff00>Assistance mode active! Solak difficulty reduced by " + 
				   assistancePercent + "% due to gear disadvantage.</col>";
				   
		} else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=ff6600>Anti-farming scaling active! Solak difficulty increased by " + 
				   difficultyIncrease + "% due to gear advantage.</col>";
				   
		} else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
			return baseMessage + "<col=ffffff>Balanced encounter detected. Optimal gear-to-boss ratio achieved!</col>";
			
		} else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=ffaa00>Slight overgear detected. Solak difficulty increased by " + 
				   difficultyIncrease + "% for balance.</col>";
		}
		
		return baseMessage + "<col=cccccc>Nature power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
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
			return "<col=00ff00>Nature Update: Combat scaling improved to balanced! Assistance reduced.</col>";
		} else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
			return "<col=ff9900>Nature Update: Anti-farming scaling now active due to increased power!</col>";
		} else if (newType.contains("WITH_ABSORPTION")) {
			return "<col=66ccff>Nature Update: Absorption bonuses detected and factored into scaling!</col>";
		} else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
			return "<col=00ff00>Nature Update: Full armor protection restored! Damage scaling normalized.</col>";
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
		
		// Enhanced phase triggers (more aggressive for overgeared players)
		boolean advancedPhase = attackCount > (isOvergeared ? 8 : (isUndergeared ? 15 : 12));
		boolean ultimatePhase = attackCount > (isOvergeared ? 18 : (isUndergeared ? 28 : 22));
		
		if (npc.withinDistance(player, npc.getSize())) {
			// Close range attacks with v5.0 intelligence
			if (ultimatePhase && Utils.random(isOvergeared ? 3 : 5) == 0) {
				return "FURY_MELEE";
			} else if (advancedPhase && Utils.random(4) == 0) {
				return "AOE_DEVASTATION";
			} else {
				int attackChoice = Utils.random(10);
				switch (attackChoice) {
					case 1: return "FURY_MELEE";
					case 2: return "THORN_RANGE";
					case 3: return "POISON_SPORES";
					case 4: return "GROVE_MAGIC";
					case 5: return "AOE_DEVASTATION";
					default: return "MELEE";
				}
			}
		} else {
			// Long range attacks with v5.0 intelligence
			if (ultimatePhase && Utils.random(3) == 0) {
				return "AOE_DEVASTATION";
			} else if (advancedPhase && Utils.random(4) == 0) {
				return "POISON_SPORES";
			} else {
				int attackChoice = Utils.random(5);
				switch (attackChoice) {
					case 0:
					case 1: return "THORN_RANGE";
					case 2: return "AOE_DEVASTATION";
					case 3: return "THORN_RANGE";
					case 4: return "POISON_SPORES";
					default: return "GROVE_MAGIC";
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
		int warningFrequency = isUndergeared ? 8 : 12; // Every 8th vs 12th attack
		
		if (attackCount % warningFrequency != 0) return false;
		
		return "FURY_MELEE".equals(attackType) || "POISON_SPORES".equals(attackType) || 
			   "AOE_DEVASTATION".equals(attackType);
	}

	/**
	 * ENHANCED v5.0: Get intelligent attack warning message
	 */
	private String getIntelligentAttackWarning(String attackType, CombatScaling scaling) {
		boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
		String intensityPrefix = isOvergeared ? "ENHANCED " : "";
		
		switch (attackType) {
			case "FURY_MELEE":
				return intensityPrefix + "NATURE'S FURY incoming - Solak channels devastating grove power!";
			case "POISON_SPORES":
				return intensityPrefix + "TOXIC SPORES incoming - prepare antipoison defenses!";
			case "AOE_DEVASTATION":
				return intensityPrefix + "AREA DEVASTATION incoming - move away from targeted zones!";
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
			case "FURY_MELEE":
				performIntelligentMeleeAttack2(npc, player, scaling);
				break;
			case "THORN_RANGE":
				performIntelligentRangeAttack(npc, player, scaling);
				break;
			case "POISON_SPORES":
				performIntelligentPoisonAttack(npc, player, scaling);
				break;
			case "GROVE_MAGIC":
				performIntelligentMageAttack(npc, player, scaling);
				break;
			case "AOE_DEVASTATION":
				performIntelligentAoeAttack(npc, player, scaling);
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
				npc.setNextForceTalk(new ForceTalk("The grove demands your presence!"));
				sendNatureMessage(player, "Solak calls you closer! Move within " + MAX_SAFESPOT_DISTANCE + " tiles!");
			}
			
			// Reset if too far (more aggressive for overgeared players)
			int resetDistance = isOvergeared ? MAX_SAFESPOT_DISTANCE + 3 : MAX_SAFESPOT_DISTANCE + 5;
			if (distance > resetDistance) {
				npc.resetCombat();
				sendNatureMessage(player, "Solak retreats deeper into the grove.");
			}
		}
		
		// Encourage proper engagement distance
		if (distance < MIN_ENGAGEMENT_DISTANCE) {
			Integer playerKey = Integer.valueOf(player.getIndex());
			Long lastWarningTime = lastWarning.get(playerKey);
			long currentTime = System.currentTimeMillis();
			
			if (lastWarningTime == null || (currentTime - lastWarningTime) > 20000) {
				sendNatureMessage(player, "You're very close to Solak! Be ready for devastating nature attacks!");
				lastWarning.put(playerKey, currentTime);
			}
		}
	}

	/**
	 * NEW v5.0: Send enhanced welcome guidance
	 */
	private void sendEnhancedWelcomeGuidance(NPC npc, Player player) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		Boolean hasWelcome = hasGivenWelcome.get(playerKey);
		
		if (hasWelcome == null || !hasWelcome) {
			// Enhanced welcome sequence with v5.0 intelligence
			npc.setNextForceTalk(new ForceTalk("I am Solak, Guardian of the Grove!"));
			
			WorldTasksManager.schedule(new WorldTask() {
				private int tick = 0;
				@Override
				public void run() {
					switch(tick) {
					case 2:
						npc.setNextForceTalk(new ForceTalk("Nature's power flows through my attacks!"));
						break;
					case 4:
						npc.setNextForceTalk(new ForceTalk("Beware my toxic spores and area devastation!"));
						break;
					case 6:
						npc.setNextForceTalk(new ForceTalk("The grove will protect itself!"));
						stop();
						break;
					}
					tick++;
				}
			}, 3, 2);
			
			hasGivenWelcome.put(playerKey, Boolean.TRUE);
		}
	}

	// ===== INTELLIGENT ATTACK METHODS WITH v5.0 INTEGRATION =====

	/**
	 * ENHANCED v5.0: Perform intelligent magic attack with HP-aware scaling
	 */
	public void performIntelligentMageAttack(NPC npc, Entity target, CombatScaling scaling) {
		if (Utils.random(8) == 0) {
			npc.setNextForceTalk(new ForceTalk("Grove magic awakens!"));
		}
		
		for (Entity t : npc.getPossibleTargets()) {
			npc.setNextAnimation(new Animation(31817));
			World.sendGraphics(npc, new Graphics(6865), t);
			
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) continue;
			
			// Use v5.0 BossBalancer scaling methods
			int baseDamage = (int) (defs.getMaxHit() * Utils.random(1.1, 1.6)); // Grove magic power
			int scaledDamage = BossBalancer.applyBossScaling(baseDamage, 
				t instanceof Player ? (Player) t : null, npc);
			
			// CRITICAL: Apply HP-aware damage scaling
			int safeDamage = scaledDamage;
			if (t instanceof Player) {
				safeDamage = applyHPAwareDamageScaling(scaledDamage, (Player) t, "grove_magic");
				checkAndWarnLowHP((Player) t, safeDamage);
			}
			
			delayHit(npc, 0, t, getMagicHit(npc, safeDamage));
			
			// Provide guidance to player
			if (t instanceof Player && Utils.random(6) == 0) {
				sendNatureMessage((Player) t, "Solak channels nature magic! Magic resistance helps!");
			}
		}
	}

	/**
	 * ENHANCED v5.0: Perform intelligent AOE attack with HP-aware scaling
	 */
	public void performIntelligentAoeAttack(NPC npc, Entity target, CombatScaling scaling) {
		if (Utils.random(6) == 0) {
			npc.setNextForceTalk(new ForceTalk("Seren's power radiates outward!"));
		}
		
		for (Entity t : npc.getPossibleTargets()) {
			npc.setNextAnimation(new Animation(31766));
			final WorldTile center = new WorldTile(t);
			World.sendGraphics(npc, new Graphics(6991), center);
			
			WorldTasksManager.schedule(new WorldTask() {
				int count = 0;

				@Override
				public void run() {
					for (Player player : World.getPlayers()) {
						if (player == null || player.isDead() || player.hasFinished())
							continue;
						
						if (player.withinDistance(center, 1)) {
							NPCCombatDefinitions defs = npc.getCombatDefinitions();
							if (defs == null) continue;
							
							// Use v5.0 BossBalancer scaling methods for AOE
							int baseDamage = (int) (defs.getMaxHit() * Utils.random(0.7, 1.1)); // AOE power
							int scaledDamage = BossBalancer.applyBossScaling(baseDamage, player, npc);
							
							// CRITICAL: Apply HP-aware damage scaling for AOE
							int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "aoe");
							checkAndWarnLowHP(player, safeDamage);
							
							delayHit(npc, 0, player, new Hit(npc, safeDamage, HitLook.REGULAR_DAMAGE));
							sendNatureMessage(player, "You're caught in Solak's area devastation!");
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
			sendNatureMessage((Player) target, "Solak's AOE attack! Move away from the targeted area!");
		}
	}

	/**
	 * ENHANCED v5.0: Perform intelligent ranged attack with HP-aware scaling
	 */
	public void performIntelligentRangeAttack(NPC npc, Entity target, CombatScaling scaling) {
		if (Utils.random(8) == 0) {
			npc.setNextForceTalk(new ForceTalk("Thorns pierce through armor!"));
		}
		
		for (Entity t : npc.getPossibleTargets()) {
			npc.setNextAnimation(new Animation(31764));
			World.sendProjectile(npc, t, 6896, 41, 16, 41, 35, 16, 0);
			
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) continue;
			
			// Use v5.0 BossBalancer scaling methods
			int baseDamage = (int) (defs.getMaxHit() * Utils.random(1.0, 1.5)); // Thorn power
			int scaledDamage = BossBalancer.applyBossScaling(baseDamage, 
				t instanceof Player ? (Player) t : null, npc);
			
			// CRITICAL: Apply HP-aware damage scaling
			int safeDamage = scaledDamage;
			if (t instanceof Player) {
				safeDamage = applyHPAwareDamageScaling(scaledDamage, (Player) t, "ranged");
				checkAndWarnLowHP((Player) t, safeDamage);
			}
			
			delayHit(npc, 0, t, getRangeHit(npc, safeDamage));
			
			// Provide tactical guidance
			if (t instanceof Player && Utils.random(5) == 0) {
				sendNatureMessage((Player) t, "Solak's thorn projectiles are sharp! Ranged defense advised!");
			}
		}
	}

	/**
	 * ENHANCED v5.0: Perform intelligent poison attack with HP-aware scaling
	 */
	public void performIntelligentPoisonAttack(NPC npc, Entity target, CombatScaling scaling) {
		if (Utils.random(6) == 0) {
			npc.setNextForceTalk(new ForceTalk("Toxic spores consume you!"));
		}
		
		for (Entity t : npc.getPossibleTargets()) {
			final Player player = t instanceof Player ? (Player) t : null;
			if (player != null) {
				npc.setNextAnimation(new Animation(31764));
				
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						NPCCombatDefinitions defs = npc.getCombatDefinitions();
						if (defs == null) {
							stop();
							return;
						}
						
						// Use v5.0 BossBalancer scaling methods
						int baseDamage = (int) (defs.getMaxHit() * Utils.random(1.2, 1.7)); // Spore power
						int scaledDamage = BossBalancer.applyBossScaling(baseDamage, player, npc);
						
						// CRITICAL: Apply HP-aware damage scaling for poison
						int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "poison");
						checkAndWarnLowHP(player, safeDamage);
						
						delayHit(npc, 0, t, getMagicHit(npc, safeDamage));
						player.setNextGraphics(new Graphics(6898, 50, 0));
						
						// Apply intelligent poison with v5.0 scaling
						applyIntelligentPoisonEffect(player, scaling);
						
						// Guidance about poison mechanics
						sendNatureMessage(player, "Solak's spores poison you severely! Antipoison recommended!");
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
			int basePoisonStrength = 60 + (BossBalancer.getBossEffectiveTier(null) * 10);
			
			// Apply scaling modifiers
			if (scaling.scalingType.contains("ANTI_FARMING")) {
				basePoisonStrength = (int)(basePoisonStrength * scaling.bossDamageMultiplier);
			} else if (scaling.scalingType.contains("ASSISTANCE")) {
				basePoisonStrength = (int)(basePoisonStrength * 0.8); // 20% less poison for undergeared
			}
			
			// Cap poison strength
			int poisonStrength = Math.min(180, Math.max(40, basePoisonStrength));
			
			player.getPoison().makePoisoned(poisonStrength);
			
		} catch (Exception e) {
			// Fallback poison
			player.getPoison().makePoisoned(80);
		}
	}

	/**
	 * ENHANCED v5.0: Perform intelligent melee attack with HP-aware scaling
	 */
	public void performIntelligentMeleeAttack(NPC npc, Entity target, CombatScaling scaling) {
		for (Entity t : npc.getPossibleTargets()) {
			npc.setNextAnimation(new Animation(31763));
			World.sendGraphics(npc, new Graphics(5516), t);
			
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) continue;
			
			// Use v5.0 BossBalancer scaling methods
			int baseDamage = (int) (defs.getMaxHit() * Utils.random(1.0, 1.5)); // Nature strength
			int scaledDamage = BossBalancer.applyBossScaling(baseDamage, 
				t instanceof Player ? (Player) t : null, npc);
			
			// CRITICAL: Apply HP-aware damage scaling
			int safeDamage = scaledDamage;
			if (t instanceof Player) {
				safeDamage = applyHPAwareDamageScaling(scaledDamage, (Player) t, "melee");
				checkAndWarnLowHP((Player) t, safeDamage);
			}
			
			delayHit(npc, 0, t, getMeleeHit(npc, safeDamage));
			
			// Occasional melee guidance
			if (t instanceof Player && Utils.random(7) == 0) {
				sendNatureMessage((Player) t, "Solak's natural strength is immense! Melee protection helps!");
			}
		}
	}

	/**
	 * ENHANCED v5.0: Perform intelligent fury melee attack with HP-aware scaling
	 */
	public void performIntelligentMeleeAttack2(NPC npc, Entity target, CombatScaling scaling) {
		if (Utils.random(6) == 0) {
			npc.setNextForceTalk(new ForceTalk("Nature's fury unleashed!"));
		}
		npc.setNextAnimation(new Animation(31815));
		
		for (Entity t : npc.getPossibleTargets()) {
			World.sendGraphics(npc, new Graphics(5516), t);
			
			NPCCombatDefinitions defs = npc.getCombatDefinitions();
			if (defs == null) continue;
			
			// Use v5.0 BossBalancer scaling methods for fury attack
			int baseDamage = (int) (defs.getMaxHit() * Utils.random(1.5, 2.0)); // Fury power
			int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, 
				t instanceof Player ? (Player) t : null, npc);
			
			// CRITICAL: Apply HP-aware damage scaling for fury attacks
			int safeDamage = scaledDamage;
			if (t instanceof Player) {
				safeDamage = applyHPAwareDamageScaling(scaledDamage, (Player) t, "fury");
				checkAndWarnLowHP((Player) t, safeDamage);
			}
			
			delayHit(npc, 3, t, getMeleeHit(npc, safeDamage));
			
			// Warning about powerful attack
			if (t instanceof Player) {
				sendNatureMessage((Player) t, "Solak charges a devastating fury attack! High damage incoming!");
			}
		}
	}

	/**
	 * Send nature-themed message
	 */
	private void sendNatureMessage(Player player, String message) {
		player.sendMessage("<col=22aa22>Nature Analysis: " + message + "</col>");
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
			System.err.println("SolakCombat: Error ending v5.0 combat session: " + e.getMessage());
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
			System.err.println("SolakCombat: Error handling v5.0 prayer change: " + e.getMessage());
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
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentMageAttack(npc, target, scaling);
	}

	public void aoeAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentAoeAttack(npc, target, scaling);
	}

	public void rangeAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentRangeAttack(npc, target, scaling);
	}

	public void poisonAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentPoisonAttack(npc, target, scaling);
	}

	public void meleeAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentMeleeAttack(npc, target, scaling);
	}

	public void meleeAttack2(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentMeleeAttack2(npc, target, scaling);
	}

	// Enhanced method aliases
	public void enhancedMageAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentMageAttack(npc, target, scaling);
	}

	public void enhancedAoeAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentAoeAttack(npc, target, scaling);
	}

	public void enhancedRangeAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentRangeAttack(npc, target, scaling);
	}

	public void enhancedPoisonAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentPoisonAttack(npc, target, scaling);
	}

	public void enhancedMeleeAttack(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentMeleeAttack(npc, target, scaling);
	}

	public void enhancedMeleeAttack2(NPC npc, Entity target) {
		CombatScaling scaling = getIntelligentSolakScaling(npc, target);
		performIntelligentMeleeAttack2(npc, target, scaling);
	}

	/**
	 * ENHANCED v5.0: Get combat scaling using BossBalancer v5.0 methods
	 */
	private CombatScaling getIntelligentSolakScaling(NPC npc, Entity target) {
		if (target instanceof Player) {
			Player player = (Player) target;
			
			// Use BossBalancer v5.0 intelligent method
			return BossBalancer.getIntelligentCombatScaling(player, npc);
		}
		
		// Return default scaling for non-players using v5.0 structure
		CombatScaling defaultScaling = new CombatScaling();
		defaultScaling.playerTier = 7;
		defaultScaling.bossTier = 7;
		defaultScaling.bossHpMultiplier = 1.0;
		defaultScaling.bossDamageMultiplier = 1.0;
		defaultScaling.bossAccuracyMultiplier = 1.0;
		defaultScaling.scalingType = "BALANCED_ENCOUNTER";
		defaultScaling.playerPower = 7.0;
		defaultScaling.bossPower = 7.0;
		defaultScaling.powerRatio = 1.0;
		return defaultScaling;
	}
}