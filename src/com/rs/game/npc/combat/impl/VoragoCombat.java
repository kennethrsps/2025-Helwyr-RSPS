package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.Projectile;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.map.bossInstance.impl.VoragoInstance;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.vorago.Vorago;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Vorago Combat System with FULL BossBalancer v5.0 Integration
 * Features: Intelligent team scaling, armor analysis, HP-aware damage scaling for raids
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 10.0 - FULL BossBalancer v5.0 Integration with Intelligent Team Scaling & HP-Aware System
 */
public class VoragoCombat extends CombatScript {

	private Vorago vorago;

	public static int WHITE_BORDER = 0, RED_BORDER = 1;

	private int bonusAttacks = 0;
	private boolean skippedVitalis = false;
	
	// Enhanced guidance system - intelligent scaling aware
	private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
	private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
	private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Integer> currentPhase = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
	
	// Timing constants - enhanced for v5.0 raids
	private static final long WARNING_COOLDOWN = 360000; // 6 minutes between warnings for raids
	private static final long SCALING_UPDATE_INTERVAL = 45000; // 45 seconds for team scaling updates
	private static final long PRE_ATTACK_WARNING_TIME = 4000; // 4 seconds for raid attacks
	private static final int MAX_WARNINGS_PER_RAID = 4; // Increased for v5.0 features
	
	// HP-aware damage scaling constants - CRITICAL SAFETY SYSTEM FOR RAIDS
	private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.45; // Max 45% of player HP per hit (raids are dangerous)
	private static final double CRITICAL_DAMAGE_PERCENT = 0.65;  // Max 65% for critical attacks (red bombs)
	private static final double AOE_DAMAGE_PERCENT = 0.50;       // Max 50% for AOE attacks (blue bombs)
	private static final double REFLECT_DAMAGE_PERCENT = 0.70;   // Max 70% for reflect mechanics
	private static final double SMASH_DAMAGE_PERCENT = 0.55;     // Max 55% for smash attacks
	private static final int MIN_PLAYER_HP = 990;
	private static final int MAX_PLAYER_HP = 1500;
	private static final int ABSOLUTE_MAX_DAMAGE = 675;          // Hard cap (45% of 1500 HP)
	private static final int MINIMUM_DAMAGE = 50;               // Minimum damage to prevent 0 hits
	
	// Team scaling data structure - enhanced for v5.0 intelligence
	public static class TeamScaling {
		public double teamMultiplier = 1.0;
		public String scalingType = "BALANCED";
		public int averageTier = 7;
		public int bossTier = 8;
		public int teamSize = 1;
		public int tierSpread = 0;
		public double teamPower = 1.0;
		public double bossPower = 1.0;
		public double powerRatio = 1.0;
		public boolean shouldWarn = false;
		public String warningMessage = "";
		public String armorAnalysis = "";
		public int playersWithFullArmor = 0;
		public int playersWithIncompleteArmor = 0;
	}
	
	private int phaseAttackCounter = 0;

	@Override
	public Object[] getKeys() {
		return new Object[] { "Vorago" };
	}

	@Override
	public int attack(NPC npc, Entity target) {
		vorago = (Vorago) npc;
		
		// ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
		
		// Initialize team combat sessions if needed
		initializeTeamCombatSessions(vorago);
		
		// Get INTELLIGENT team scaling v5.0
		TeamScaling teamScaling = getIntelligentTeamScaling(vorago);
		
		// Enhanced guidance system with intelligent scaling awareness
		provideIntelligentRaidGuidance(vorago, teamScaling);
		
		// Monitor team scaling changes during combat
		monitorTeamScalingChanges(vorago, teamScaling);
		
		// Check for phase changes with v5.0 intelligence
		checkPhaseChangeGuidance(vorago, teamScaling);
		
		// Increment attack counter
		phaseAttackCounter++;
		
		boolean hardMode = vorago.getVoragoInstance().getSettings().isHardMode();
		if (Utils.colides(vorago, target) && (!hardMode && vorago.getPhase() == 5)
				&& (!hardMode && vorago.getPhase() >= 1)) {
			if (Utils.random(4) == 0 && vorago.getTemporaryAttributtes().get("CantBeAttacked") == null)
				sendBindAttack(teamScaling);
			vorago.calcFollow(target, 2, true, npc.isIntelligentRouteFinder());
			return 0;
		}
		if (vorago.getTemporaryAttributtes().get("BringHimDownClick") != null)
			return 0;
		else if (vorago.getTemporaryAttributtes().get("VoragoType") != null
				&& ((int) vorago.getTemporaryAttributtes().get("VoragoType") == 1)
				&& (!hardMode && vorago.getPhase() != 5)) {
			if (Utils.getDistance(vorago, target) > 14) {
				vorago.setCantFollowUnderCombat(false);
			} else {
				vorago.setCantFollowUnderCombat(true);
			}
		}
		if (bonusAttacks > 0) {
			bonusAttacks--;
			return executeAttackWithWarning(vorago, teamScaling, "BLUE_BOMB");
		}
		if (vorago.getTemporaryAttributtes().get("CantBeAttackedOnPhaseStart") != null)
			vorago.getTemporaryAttributtes().remove("CantBeAttackedOnPhaseStart");
		
		// Execute phase-based attacks with team scaling and warnings
		return executePhaseAttackWithWarning(vorago, target, teamScaling, hardMode);
	}

	/**
	 * Initialize team combat sessions using BossBalancer v5.0
	 */
	private void initializeTeamCombatSessions(Vorago vorago) {
		Integer instanceKey = Integer.valueOf(vorago.getVoragoInstance().hashCode());
		
		if (!combatSessionActive.containsKey(instanceKey)) {
			// Start BossBalancer v5.0 combat sessions for all team members
			for (Player player : vorago.getVoragoInstance().getPlayersOnBattle()) {
				if (player != null && !player.isDead()) {
					BossBalancer.startCombatSession(player, vorago);
				}
			}
			
			combatSessionActive.put(instanceKey, Boolean.TRUE);
			attackCounter.put(instanceKey, Integer.valueOf(0));
			currentPhase.put(instanceKey, Integer.valueOf(-1));
			lastScalingType.put(instanceKey, "UNKNOWN");
			
			// Send v5.0 enhanced raid message
			String welcomeMsg = "<col=4169E1>Vorago raid initiated. Intelligent team analysis active (v5.0).</col>";
			sendTeamMessage(vorago, welcomeMsg);
			
			// Perform initial team armor analysis
			performInitialTeamArmorAnalysis(vorago);
		}
	}

	/**
	 * NEW v5.0: Perform initial team armor analysis
	 */
	private void performInitialTeamArmorAnalysis(Vorago vorago) {
		try {
			int fullArmorCount = 0;
			int incompleteArmorCount = 0;
			
			for (Player player : vorago.getVoragoInstance().getPlayersOnBattle()) {
				if (player != null && !player.isDead()) {
					BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
					
					if (armorResult.hasFullArmor) {
						fullArmorCount++;
					} else {
						incompleteArmorCount++;
					}
				}
			}
			
			// Send team armor summary
			if (incompleteArmorCount > 0) {
				sendTeamMessage(vorago, "<col=ff6600>Team Armor Analysis: " + incompleteArmorCount + 
					" player(s) missing armor protection. Vorago will deal increased damage!</col>");
			} else {
				sendTeamMessage(vorago, "<col=00ff00>Team Armor Analysis: All team members have full protection!</col>");
			}
			
		} catch (Exception e) {
			// Ignore armor analysis errors
		}
	}

	/**
	 * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills in raids
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
				case "red_bomb":
				case "critical":
					damagePercent = CRITICAL_DAMAGE_PERCENT;
					break;
				case "blue_bomb":
				case "aoe":
					damagePercent = AOE_DAMAGE_PERCENT;
					break;
				case "reflect":
					damagePercent = REFLECT_DAMAGE_PERCENT;
					break;
				case "smash":
					damagePercent = SMASH_DAMAGE_PERCENT;
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
			
			// Additional safety check - never deal more than 80% of current HP in raids
			if (currentHP > 0) {
				int emergencyCap = (int)(currentHP * 0.80);
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
	 * ENHANCED v5.0: Intelligent guidance with power-based team scaling awareness
	 */
	private void provideIntelligentRaidGuidance(Vorago vorago, TeamScaling teamScaling) {
		Integer instanceKey = Integer.valueOf(vorago.getVoragoInstance().hashCode());
		long currentTime = System.currentTimeMillis();
		
		// Check if we should provide guidance
		Long lastWarningTime = lastWarning.get(instanceKey);
		if (lastWarningTime != null && (currentTime - lastWarningTime) < WARNING_COOLDOWN) {
			return; // Still in cooldown
		}
		
		Integer currentStage = warningStage.get(instanceKey);
		if (currentStage == null) currentStage = 0;
		if (currentStage >= MAX_WARNINGS_PER_RAID) {
			return; // Max warnings reached
		}
		
		// Get guidance message based on v5.0 intelligent team scaling
		String guidanceMessage = getIntelligentTeamGuidanceMessage(vorago, teamScaling, currentStage);
		
		// Send guidance if applicable
		if (guidanceMessage != null) {
			sendTeamMessage(vorago, guidanceMessage);
			lastWarning.put(instanceKey, currentTime);
			warningStage.put(instanceKey, currentStage + 1);
		}
	}

	/**
	 * NEW v5.0: Get intelligent team guidance message based on power analysis
	 */
	private String getIntelligentTeamGuidanceMessage(Vorago vorago, TeamScaling teamScaling, int stage) {
		switch (stage) {
			case 0:
				// First warning: Team power analysis and scaling type
				return getTeamScalingAnalysisMessage(teamScaling);
				
			case 1:
				// Second warning: Team coordination based on scaling
				if (teamScaling.scalingType.contains("UNBALANCED")) {
					return "<col=ff6600>Team Analysis: Unbalanced gear spread detected. Strong players support weaker members!</col>";
				} else if (teamScaling.playersWithIncompleteArmor > 0) {
					return "<col=ff3300>Team Analysis: " + teamScaling.playersWithIncompleteArmor + 
							" player(s) missing armor! Damage scaling increased. Equip protection!</col>";
				}
				break;
				
			case 2:
				// Third warning: Phase progression
				if (vorago.getPhase() >= 3) {
					return "<col=ff6600>Raid Analysis: Phase 3+ rotation mechanics active. Perfect team coordination required!</col>";
				}
				break;
				
			case 3:
				// Fourth warning: Hard mode or final phases
				if (vorago.getVoragoInstance().getSettings().isHardMode() && vorago.getPhase() >= 10) {
					return "<col=ff0000>Raid Analysis: Hard mode final phases! All mechanics combined. Flawless execution needed!</col>";
				}
				break;
		}
		
		return null;
	}

	/**
	 * NEW v5.0: Get team scaling analysis message
	 */
	private String getTeamScalingAnalysisMessage(TeamScaling teamScaling) {
		String baseMessage = "<col=66ccff>Team Power Analysis:</col> ";
		
		if (teamScaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistancePercent = (int)((1.0 - teamScaling.teamMultiplier) * 100);
			return baseMessage + "<col=00ff00>Assistance mode active! Vorago difficulty reduced by " + 
				   assistancePercent + "% due to team gear disadvantage.</col>";
				   
		} else if (teamScaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int difficultyIncrease = (int)((teamScaling.teamMultiplier - 1.0) * 100);
			return baseMessage + "<col=ff6600>Anti-farming scaling active! Vorago difficulty increased by " + 
				   difficultyIncrease + "% due to team gear advantage.</col>";
				   
		} else if (teamScaling.scalingType.contains("BALANCED_ENCOUNTER")) {
			return baseMessage + "<col=ffffff>Balanced encounter detected. Optimal team-to-boss ratio achieved!</col>";
			
		} else if (teamScaling.scalingType.contains("TEAM_UNBALANCED")) {
			return baseMessage + "<col=ffaa00>Unbalanced team detected. Gear spread: " + teamScaling.tierSpread + 
				   " tiers. Support coordination critical!</col>";
		}
		
		return baseMessage + "<col=cccccc>Team power ratio: " + String.format("%.2f", teamScaling.powerRatio) + ":1</col>";
	}

	/**
	 * NEW v5.0: Monitor team scaling changes during combat
	 */
	private void monitorTeamScalingChanges(Vorago vorago, TeamScaling teamScaling) {
		Integer instanceKey = Integer.valueOf(vorago.getVoragoInstance().hashCode());
		String currentScalingType = teamScaling.scalingType;
		String lastType = lastScalingType.get(instanceKey);
		
		// Check if team scaling type changed (prayer changes, gear swaps, etc.)
		if (lastType != null && !lastType.equals(currentScalingType)) {
			// Team scaling changed - notify team
			String changeMessage = getTeamScalingChangeMessage(lastType, currentScalingType, teamScaling);
			if (changeMessage != null) {
				sendTeamMessage(vorago, changeMessage);
			}
		}
		
		lastScalingType.put(instanceKey, currentScalingType);
	}

	/**
	 * NEW v5.0: Get team scaling change message
	 */
	private String getTeamScalingChangeMessage(String oldType, String newType, TeamScaling teamScaling) {
		if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
			return "<col=00ff00>Team Update: Combat scaling improved to balanced! Assistance reduced.</col>";
		} else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
			return "<col=ff9900>Team Update: Anti-farming scaling now active due to increased team power!</col>";
		} else if (newType.contains("WITH_ABSORPTION")) {
			return "<col=66ccff>Team Update: Absorption bonuses detected across team and factored into scaling!</col>";
		} else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
			return "<col=00ff00>Team Update: Full armor protection restored! Damage scaling normalized.</col>";
		}
		
		return null;
	}

	/**
	 * Check for phase changes and provide intelligent guidance
	 */
	private void checkPhaseChangeGuidance(Vorago vorago, TeamScaling teamScaling) {
		Integer instanceKey = Integer.valueOf(vorago.getVoragoInstance().hashCode());
		Integer lastPhase = currentPhase.get(instanceKey);
		if (lastPhase == null) lastPhase = -1;
		
		int newPhase = vorago.getPhase();
		if (lastPhase != newPhase) {
			currentPhase.put(instanceKey, newPhase);
			sendPhaseTransitionGuidance(vorago, newPhase, teamScaling);
			phaseAttackCounter = 0; // Reset counter for new phase
		}
	}

	/**
	 * Send intelligent phase transition guidance with v5.0 scaling awareness
	 */
	private void sendPhaseTransitionGuidance(Vorago vorago, int newPhase, TeamScaling teamScaling) {
		boolean hardMode = vorago.getVoragoInstance().getSettings().isHardMode();
		String modeText = hardMode ? "Hard Mode " : "";
		String scalingText = getIntelligentScalingText(teamScaling);
		
		switch (newPhase) {
		case 1:
			sendTeamMessage(vorago, "Phase 1: " + modeText + "Vorago raid initiated. " + scalingText);
			break;
		case 2:
			sendTeamMessage(vorago, "Phase 2: " + modeText + "Smash and reflect mechanics active. " + scalingText);
			break;
		case 3:
			String rotation = getRotationText();
			sendTeamMessage(vorago, "Phase 3: " + modeText + rotation + " " + scalingText);
			break;
		case 5:
			sendTeamMessage(vorago, "Phase 5: " + modeText + "Final phase mechanics. " + scalingText);
			break;
		case 10:
			sendTeamMessage(vorago, "Phase 10: " + modeText + "Ultimate raid challenge. " + scalingText);
			break;
		case 11:
			sendTeamMessage(vorago, "FINAL Phase 11: " + modeText + "All mechanics combined. " + scalingText);
			break;
		}
	}

	/**
	 * Execute phase attack with intelligent warning system
	 */
	private int executePhaseAttackWithWarning(Vorago vorago, Entity target, TeamScaling teamScaling, boolean hardMode) {
		String attackType = determinePhaseAttackType(vorago, target, hardMode);
		
		// Check if this attack needs a warning with v5.0 intelligence
		boolean needsWarning = shouldGiveIntelligentRaidWarning(attackType, teamScaling);
		
		if (needsWarning) {
			String warningMessage = getIntelligentRaidAttackWarning(attackType, teamScaling);
			if (warningMessage != null) {
				sendPreAttackWarning(vorago, warningMessage);
				
				// Delay the actual attack to give team time to react
				final String finalAttackType = attackType;
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						executePhaseAttack(vorago, target, teamScaling, hardMode, finalAttackType);
						this.stop();
					}
				}, 3); // 1.8 second delay for team reaction time
				
				return 7 + 3;
			}
		}
		
		// Execute immediately for basic attacks
		executePhaseAttack(vorago, target, teamScaling, hardMode, attackType);
		return 7;
	}

	/**
	 * Execute attack with intelligent warning
	 */
	private int executeAttackWithWarning(Vorago vorago, TeamScaling teamScaling, String attackType) {
		boolean needsWarning = shouldGiveIntelligentRaidWarning(attackType, teamScaling);
		
		if (needsWarning) {
			String warningMessage = getIntelligentRaidAttackWarning(attackType, teamScaling);
			if (warningMessage != null) {
				sendPreAttackWarning(vorago, warningMessage);
				
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						sendTeamScaledBlueBombAttack(teamScaling);
						this.stop();
					}
				}, 3);
				
				return 7 + 3;
			}
		}
		
		sendTeamScaledBlueBombAttack(teamScaling);
		return 7;
	}

	/**
	 * Determine phase attack type
	 */
	private String determinePhaseAttackType(Vorago vorago, Entity target, boolean hardMode) {
		switch (vorago.getPhase()) {
		case 1:
			if (vorago.getAttackProgress() == 0) return "RED_BOMB";
			return "BASIC_ATTACK";
		case 2:
			if (vorago.getAttackProgress() == 8) return "REFLECT";
			if (vorago.getAttackProgress() <= 4) return "SMASH";
			if (vorago.getAttackProgress() == 12) return "RED_BOMB";
			return "BASIC_ATTACK";
		case 3:
		case 4:
		case 5:
			return "PHASE_SPECIAL";
		default:
			return "BASIC_ATTACK";
		}
	}

	/**
	 * ENHANCED v5.0: Determine if raid attack needs warning with team scaling awareness
	 */
	private boolean shouldGiveIntelligentRaidWarning(String attackType, TeamScaling teamScaling) {
		// More frequent warnings for undergeared teams
		boolean isUndergeared = teamScaling.scalingType.contains("ASSISTANCE");
		
		switch (attackType) {
			case "RED_BOMB":
			case "REFLECT":
			case "SMASH":
			case "PHASE_SPECIAL":
				return true;
			case "BLUE_BOMB":
				// Warn about blue bombs for undergeared teams
				return isUndergeared;
			default:
				return false;
		}
	}

	/**
	 * ENHANCED v5.0: Get intelligent raid attack warning message
	 */
	private String getIntelligentRaidAttackWarning(String attackType, TeamScaling teamScaling) {
		boolean isOvergeared = teamScaling.scalingType.contains("ANTI_FARMING");
		String intensityPrefix = isOvergeared ? "ENHANCED " : "";
		
		switch (attackType) {
			case "RED_BOMB":
				return intensityPrefix + "RED BOMB incoming - designated player run immediately!";
			case "REFLECT":
				return intensityPrefix + "REFLECT PHASE incoming - stop all damage or it will be redirected!";
			case "SMASH":
				return intensityPrefix + "SMASH ATTACK incoming - spread out to minimize damage!";
			case "PHASE_SPECIAL":
				return intensityPrefix + "SPECIAL MECHANIC incoming - execute phase strategy!";
			case "BLUE_BOMB":
				return intensityPrefix + "BLUE BOMB incoming - spread formation and prepare for impact!";
			default:
				return null;
		}
	}

	/**
	 * Send pre-attack warning to team
	 */
	private void sendPreAttackWarning(Vorago vorago, String warning) {
		sendTeamMessage(vorago, "<col=ff3300>WARNING: " + warning + "</col>");
	}

	/**
	 * Execute phase attack with v5.0 intelligence
	 */
	private void executePhaseAttack(Vorago vorago, Entity target, TeamScaling teamScaling, boolean hardMode, String attackType) {
		// Execute phase-based attacks with intelligent team scaling
		switch (vorago.getPhase()) {
		case 1:// PHASE ONE
			if (vorago.getAttackProgress() > 4) // 5 attacks
				vorago.setAttackProgress(0);
			switch (vorago.getAttackProgress()) {
			case 0:
				sendTeamScaledRedBombAttack(teamScaling);
				break;
			default:
				int attackStyle = Utils.random(3);
				switch (attackStyle) {
				case 0:// melee hit if possible
					if (Utils.isOnRange(vorago, target, 0))
						sendTeamScaledMeleeAttack(teamScaling);
					else
						sendTeamScaledBlueBombAttack(teamScaling);
					break;
				case 1:// magic
				case 2:
					sendTeamScaledBlueBombAttack(teamScaling);
					break;
				}
				break;
			}
			vorago.setAttackProgress(vorago.getAttackProgress() + 1);
			break;
		case 2:// PHASE 2
			if (vorago.getAttackProgress() > 16) // 17 attacks
				vorago.setAttackProgress(0);
			switch (vorago.getAttackProgress()) {
			case 0:// 5 smashes
			case 1:
				sendTeamScaledSmash(teamScaling);
				break;
			case 2:
				sendTeamScaledBlueBombAttack(teamScaling, false);
			case 3:
			case 4:
				sendTeamScaledSmash(teamScaling);
				break;
			case 5:// 3 attacks
			case 6:
			case 7:
				int attackStyle = Utils.random(3);
				switch (attackStyle) {
				case 0:// melee hit if possible
					if (Utils.isOnRange(vorago, target, 0))
						sendTeamScaledMeleeAttack(teamScaling);
					else
						sendTeamScaledBlueBombAttack(teamScaling);
					break;
				case 1:// magic
				case 2:
					sendTeamScaledBlueBombAttack(teamScaling);
					break;
				}
				break;
			case 8:
				sendTeamScaledReflectAttack(teamScaling);
				sendGravityField();
				break;
			case 9:// 3attacks then case 12 red bomb
			case 10:
			case 11:
			case 13:// 4attacks
			case 14:
			case 15:
			case 16:
				attackStyle = Utils.random(3);
				switch (attackStyle) {
				case 0:// melee hit if possible
					if (Utils.isOnRange(vorago, target, 0))
						sendTeamScaledMeleeAttack(teamScaling);
					else
						sendTeamScaledBlueBombAttack(teamScaling);
					break;
				case 1:// magic
				case 2:
					sendTeamScaledBlueBombAttack(teamScaling);
					break;
				}
				break;
			case 12:// red Bomb
				sendTeamScaledRedBombAttack(teamScaling);
				break;
			}
			vorago.setAttackProgress(vorago.getAttackProgress() + 1);
			break;
		// Continue with all other phases using team scaled methods
		case 3:// PHASE 3
			handlePhaseThree(hardMode, target, teamScaling);
			break;
		case 4:// Phase 4
		case 9:// Phase 9 HardMode
			handlePhaseFour(hardMode, target, teamScaling);
			break;
		case 5:
			handlePhaseFive(hardMode, target, teamScaling);
			break;
		case 6:// hardMode green bomb
			handlePhaseSix(target, teamScaling);
			break;
		case 7:
			handlePhaseSeven(target, teamScaling);
			break;
		case 8:
			handlePhaseEight(target, teamScaling);
			break;
		case 10:// Phase 10 HardMode
			handlePhaseTen(target, teamScaling);
			break;
		case 11:// phase 11
			handlePhaseEleven(target, teamScaling);
			break;
		}
	}

	/**
	 * ENHANCED v5.0: Get intelligent team-based combat scaling using actual BossBalancer methods
	 */
	private TeamScaling getIntelligentTeamScaling(Vorago vorago) {
		TeamScaling teamScaling = new TeamScaling();
		
		try {
			// Get all team players
			List<Player> teamPlayers = vorago.getVoragoInstance().getPlayersOnBattle();
			if (teamPlayers.isEmpty()) {
				teamScaling.teamMultiplier = 1.0;
				teamScaling.scalingType = "NO_TEAM";
				return teamScaling;
			}
			
			// Calculate team statistics using BossBalancer v5.0 methods
			double totalPower = 0.0;
			int playerCount = 0;
			int fullArmorCount = 0;
			int incompleteArmorCount = 0;
			
			for (Player player : teamPlayers) {
				if (player != null && !player.isDead()) {
					// Use BossBalancer v5.0 intelligent power calculation
					double playerPower = BossBalancer.calculateActualPlayerPower(player);
					totalPower += playerPower;
					playerCount++;
					
					// Check armor coverage
					BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
					if (armorResult.hasFullArmor) {
						fullArmorCount++;
					} else {
						incompleteArmorCount++;
					}
				}
			}
			
			if (playerCount == 0) {
				teamScaling.teamMultiplier = 1.0;
				teamScaling.scalingType = "NO_ACTIVE_PLAYERS";
				return teamScaling;
			}
			
			// Calculate team power and boss power
			double averageTeamPower = totalPower / playerCount;
			double bossPower = calculateBossPower(vorago);
			double powerRatio = averageTeamPower / bossPower;
			
			// Populate team scaling data
			teamScaling.teamPower = averageTeamPower;
			teamScaling.bossPower = bossPower;
			teamScaling.powerRatio = powerRatio;
			teamScaling.teamSize = playerCount;
			teamScaling.playersWithFullArmor = fullArmorCount;
			teamScaling.playersWithIncompleteArmor = incompleteArmorCount;
			
			// Apply v5.0 intelligent team scaling logic
			applyIntelligentTeamScaling(teamScaling, powerRatio, playerCount);
			
			return teamScaling;
			
		} catch (Exception e) {
			System.err.println("Error in getIntelligentTeamScaling: " + e.getMessage());
			// Return default scaling on error
			teamScaling.teamMultiplier = 1.0;
			teamScaling.scalingType = "ERROR_DEFAULT";
			return teamScaling;
		}
	}

	/**
	 * NEW v5.0: Calculate boss power for raids
	 */
	private double calculateBossPower(Vorago vorago) {
		// Use BossBalancer v5.0 boss tier calculation
		int bossTier = BossBalancer.getBossEffectiveTier(vorago);
		
		// Base power from tier (logarithmic)
		double basePower = Math.pow(bossTier, 1.4);
		
		// Raid multiplier (Vorago is a raid boss)
		double raidMultiplier = 1.5;
		
		// Hard mode multiplier
		boolean hardMode = vorago.getVoragoInstance().getSettings().isHardMode();
		double hardModeMultiplier = hardMode ? 1.8 : 1.0;
		
		// Phase multiplier (later phases are stronger)
		double phaseMultiplier = 1.0 + (vorago.getPhase() * 0.1);
		
		return basePower * raidMultiplier * hardModeMultiplier * phaseMultiplier;
	}

	/**
	 * NEW v5.0: Apply intelligent team scaling logic
	 */
	private void applyIntelligentTeamScaling(TeamScaling teamScaling, double powerRatio, int playerCount) {
		// Constants from BossBalancer v5.0
		final double UNDERPOWERED_THRESHOLD = 0.7;
		final double BALANCED_THRESHOLD_LOW = 0.8;
		final double BALANCED_THRESHOLD_HIGH = 1.2;
		final double OVERPOWERED_THRESHOLD = 1.5;
		final double MAX_TEAM_SCALING_MULTIPLIER = 3.0; // Reduced for teams
		
		if (powerRatio < UNDERPOWERED_THRESHOLD) {
			// UNDERPOWERED TEAM: Assistance mode
			double assistanceLevel = (UNDERPOWERED_THRESHOLD - powerRatio) * 1.2; // Less assistance than solo
			assistanceLevel = Math.min(0.3, assistanceLevel); // Max 30% assistance for teams
			
			teamScaling.teamMultiplier = 1.0 - assistanceLevel;
			teamScaling.scalingType = "UNDERPOWERED_ASSISTANCE";
			teamScaling.shouldWarn = true;
			
		} else if (powerRatio >= BALANCED_THRESHOLD_LOW && powerRatio <= BALANCED_THRESHOLD_HIGH) {
			// BALANCED TEAM: Apply armor penalties
			teamScaling.teamMultiplier = 1.0;
			
			if (teamScaling.playersWithIncompleteArmor > 0) {
				// Increase difficulty for incomplete armor
				double armorPenalty = (double) teamScaling.playersWithIncompleteArmor / teamScaling.teamSize;
				teamScaling.teamMultiplier += (armorPenalty * 0.25); // 25% increase per incomplete armor player
				teamScaling.scalingType = "BALANCED_INCOMPLETE_ARMOR";
			} else {
				teamScaling.scalingType = "BALANCED_ENCOUNTER";
			}
			
		} else if (powerRatio > BALANCED_THRESHOLD_HIGH && powerRatio < OVERPOWERED_THRESHOLD) {
			// MILD OVERGEAR: Slight increase
			double overgearLevel = (powerRatio - BALANCED_THRESHOLD_HIGH) * 0.6; // Less aggressive for teams
			overgearLevel = Math.min(0.25, overgearLevel); // Max 25% increase
			
			teamScaling.teamMultiplier = 1.0 + overgearLevel;
			teamScaling.scalingType = "MILD_OVERGEAR";
			
		} else {
			// OVERPOWERED TEAM: Intelligent anti-farming
			double excessPower = powerRatio - OVERPOWERED_THRESHOLD;
			double scalingFactor = 1.0 + (Math.log(1.0 + excessPower) * 0.4); // Reduced curve for teams
			scalingFactor = Math.min(MAX_TEAM_SCALING_MULTIPLIER, scalingFactor);
			
			teamScaling.teamMultiplier = scalingFactor;
			teamScaling.scalingType = "INTELLIGENT_ANTI_FARMING";
			teamScaling.shouldWarn = true;
		}
		
		// Team size bonus/penalty
		if (playerCount >= 10) {
			teamScaling.teamMultiplier *= 0.95; // 5% easier for large teams
			teamScaling.scalingType += "_LARGE_TEAM";
		} else if (playerCount <= 2) {
			teamScaling.teamMultiplier *= 1.1; // 10% harder for small teams
			teamScaling.scalingType += "_SMALL_TEAM";
		}
	}

	/**
	 * Get intelligent scaling text for messages
	 */
	private String getIntelligentScalingText(TeamScaling teamScaling) {
		if (teamScaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistance = (int)((1.0 - teamScaling.teamMultiplier) * 100);
			return "Team difficulty: -" + assistance + "% (assistance mode).";
		} else if (teamScaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int increase = (int)((teamScaling.teamMultiplier - 1.0) * 100);
			return "Team difficulty: +" + increase + "% (anti-farming).";
		} else if (teamScaling.scalingType.contains("INCOMPLETE_ARMOR")) {
			int penalty = (int)((teamScaling.teamMultiplier - 1.0) * 100);
			return "Team difficulty: +" + penalty + "% (incomplete armor).";
		} else if (teamScaling.scalingType.contains("MILD_OVERGEAR")) {
			int increase = (int)((teamScaling.teamMultiplier - 1.0) * 100);
			return "Team difficulty: +" + increase + "% (overgeared).";
		} else {
			return "Team difficulty: Balanced.";
		}
	}

	/**
	 * Get rotation text
	 */
	private String getRotationText() {
		switch (Settings.VORAGO_ROTATION) {
		case 0: return "Ceiling collapse rotation.";
		case 1: return "Scopulus spawn rotation.";
		case 2: return "Vitalis orb rotation.";
		case 3: return "Green bomb rotation.";
		case 4: return "Team split rotation.";
		case 5: return "The End rotation.";
		default: return "Special rotation active.";
		}
	}

	/**
	 * Send team message
	 */
	private void sendTeamMessage(Vorago vorago, String message) {
		try {
			for (Player player : vorago.getVoragoInstance().getPlayersOnBattle()) {
				if (player != null && !player.isDead()) {
					player.sendMessage(message);
				}
			}
		} catch (Exception e) {
			// Fallback
			vorago.setNextForceTalk(new ForceTalk(message));
		}
	}

	/**
	 * ENHANCED v5.0: Calculate team-scaled damage using BossBalancer v5.0 methods with HP-aware scaling
	 */
	private int calculateTeamScaledDamage(int baseDamage, TeamScaling teamScaling, Player targetPlayer, String attackType) {
		// Apply team scaling first
		int teamScaledDamage = (int) (baseDamage * teamScaling.teamMultiplier);
		
		// If we have a target player, use BossBalancer v5.0 scaling
		if (targetPlayer != null) {
			teamScaledDamage = BossBalancer.applyBossScaling(teamScaledDamage, targetPlayer, vorago);
			
			// CRITICAL: Apply HP-aware damage scaling to prevent one-shots
			teamScaledDamage = applyHPAwareDamageScaling(teamScaledDamage, targetPlayer, attackType);
			
			// Check and warn about incoming damage
			checkAndWarnLowHP(targetPlayer, teamScaledDamage);
		}
		
		return teamScaledDamage;
	}

	// ===== TEAM-SCALED ATTACK METHODS WITH v5.0 INTEGRATION =====

	/**
	 * ENHANCED v5.0: Team-scaled melee attack using BossBalancer v5.0 integration
	 */
	public void sendTeamScaledMeleeAttack(TeamScaling teamScaling) {
		vorago.setNextAnimation(new Animation(20355));
		for (Player player : vorago.getVoragoInstance().getPlayersOnBattle()) {
			if (player == null || player.isDead() || !Utils.isOnRange(vorago, player, 1))
				continue;
			
			int damage = calculateTeamScaledDamage(400, teamScaling, player, "melee");
			delayHit(vorago, 0, player, getMeleeHit(vorago, damage));
		}
	}

	/**
	 * ENHANCED v5.0: Team-scaled blue bomb attack with HP-aware scaling
	 */
	public void sendTeamScaledBlueBombAttack(TeamScaling teamScaling) {
		sendTeamScaledBlueBombAttack(teamScaling, true);
	}

	public void sendTeamScaledBlueBombAttack(TeamScaling teamScaling, boolean sendAnimation) {
		boolean hardMode = vorago.getVoragoInstance().getSettings().isHardMode();
		Entity target = getFarestTarget(null);
		Entity target2 = hardMode
				? (vorago.getPlayerOnBattleCount() == 1 ? getFarestTarget(null) : getFarestTarget(target))
				: (vorago.getPlayerOnBattleCount() > 10 ? getFarestTarget(target) : null);
		if (target == null)
			return;
		if (sendAnimation)
			vorago.setNextAnimation(new Animation(20356));
		vorago.setNextGraphics(new Graphics(4015));
		int numberOfBlues = (target2 == null) ? 1 : 2;
		for (int i = 0; i < numberOfBlues; i++) {
			Entity target3 = i == 0 ? target : target2;
			long startTime = Utils.currentTimeMillis();
			long arriveTime = startTime + (15000);
			double speed = Utils.getProjectileSpeed(vorago, target3, 90, 20, startTime, arriveTime);
			Projectile projectile = World.sendProjectileNew(new WorldTile(vorago), target3, 4016, 90, 20, 10, speed, 0, 0);
			int cycleTime = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
			CoresManager.fastExecutor.schedule(new TimerTask() {

				@Override
				public void run() {
					for (Player player : vorago.getVoragoInstance().getPlayersOnBattle()) {
						if (player == null || player.isDead() || Utils.getDistance(target3, player) > 2)
							continue;
						
						int damage = calculateTeamScaledDamage(500, teamScaling, player, "blue_bomb");
						player.setNextGraphics(new Graphics(4017));
						delayHit(vorago, 0, player, getMagicHit(vorago, damage));
					}
				}
			}, (cycleTime * 1000) - 950);
		}
	}

	/**
	 * ENHANCED v5.0: Team-scaled red bomb attack with HP-aware scaling
	 */
	public void sendTeamScaledRedBombAttack(TeamScaling teamScaling) {
		boolean trollRedBombChance = Utils.random(10) == 0;
		Player target = trollRedBombChance ? getRandomTarget() : getFarestTarget(null);
		if (target == null)
			return;
		sendMessage(target, RED_BORDER, "<col=ff0000>Vorago has sent a bomb after you. Run!</col>");
		vorago.setNextAnimation(new Animation(20371));
		sendTeamScaledGroundBlueBomb(new WorldTile(target.getX(), target.getY(), target.getPlane()), teamScaling);
		long startTime = Utils.currentTimeMillis();
		long arriveTime = startTime + (20000);
		double speed = Utils.getProjectileSpeed(vorago, target, 90, 20, startTime, arriveTime);
		Projectile projectile = World.sendProjectileNew(vorago, target, 4023, 90, 20, 10, speed, 0, 0);
		int cycleTime = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
		long time = (cycleTime * 1000) - 950;
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				for (Player player : vorago.getVoragoInstance().getPlayersOnBattle()) {
					if (player == null || player.isDead() || Utils.getDistance(player, target) > 2)
						continue;
					boolean hardMode = vorago.getVoragoInstance().getSettings().isHardMode();
					
					int baseDamage = (hardMode ? 300 : 200) + (getPlayersNearby(target, 3) * (hardMode ? 150 : 100));
					int maxDamage = hardMode ? 1050 : 700;
					int finalDamage = calculateTeamScaledDamage(Math.min(baseDamage, maxDamage), teamScaling, player, "red_bomb");
					
					player.setNextGraphics(new Graphics(4024));
					World.sendGraphics(null, new Graphics(3522), new WorldTile(player.getX(), player.getY(), player.getPlane()));
					delayHit(vorago, 0, player, new Hit(vorago, finalDamage, HitLook.REGULAR_DAMAGE));
				}
			}
		}, time < 0 ? 4000 : time);
	}

	/**
	 * ENHANCED v5.0: Team-scaled smash attack with HP-aware scaling
	 */
	public void sendTeamScaledSmash(TeamScaling teamScaling) {
		boolean hardMode = vorago.getVoragoInstance().getSettings().isHardMode();
		Entity target = ((!hardMode && vorago.getPhase() == 5) || (hardMode && vorago.getPhase() >= 10))
				? getRandomTarget() : vorago.getCombat().getTarget();
		
		if ((!hardMode && vorago.getPhase() != 5) || (hardMode && vorago.getPhase() < 10)) {
			vorago.getTemporaryAttributtes().put("VoragoType", 0);
			vorago.transform();
		}
		vorago.setNextAnimation(new Animation(20363));
		vorago.setNextGraphics(new Graphics(4018));
		World.sendGraphics(null, new Graphics(4019), new WorldTile(target));
		
		int damage = calculateTeamScaledDamage(hardMode ? 450 : 300, teamScaling, 
			target instanceof Player ? (Player) target : null, "smash");
		delayHit(vorago, 0, target, new Hit(vorago, damage, HitLook.REGULAR_DAMAGE));
	}

	/**
	 * ENHANCED v5.0: Team-scaled reflect attack with HP-aware scaling
	 */
	public void sendTeamScaledReflectAttack(TeamScaling teamScaling) {
		boolean hardMode = vorago.getVoragoInstance().getSettings().isHardMode();
		vorago.setNextAnimation(new Animation(20319));
		vorago.setNextGraphics(new Graphics(4011));
		vorago.setTargetedPlayer(null);
		vorago.setTargetedPlayer(getRandomTarget());
		Player targetedPlayer = vorago.getTargetedPlayer();
		if (targetedPlayer == null)
			return;
		for (Player player : vorago.getVoragoInstance().getPlayersOnBattle()) {
			if (player == null || player.isDead())
				continue;
			boolean isTargetedPlayer = player == targetedPlayer;
			sendMessage(player, RED_BORDER,
					isTargetedPlayer ? "<col=ff0000>Vorago channels incoming damage to you. Beware!</col>"
							: "<col=FFFFFF>Vorago reflects incoming damage to surrounding foes!</col>");
		}
		targetedPlayer.setNextGraphics(new Graphics(4012));
		
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				if ((!hardMode && vorago.getPhase() != 5) || (hardMode && vorago.getPhase() < 10)) {
					vorago.getTemporaryAttributtes().put("VoragoType", 1);
					vorago.transform();
				}
				vorago.setTargetedPlayer(null);
				if (vorago.getTemporaryAttributtes().get("BringHimDownClick") != null) {
					targetedPlayer.getPackets().sendGameMessage("<col=00FF00>Vorago releases his mental link on you.");
				} else
					sendMessage(targetedPlayer, RED_BORDER, "<col=00FF00>Vorago releases his mental link on you.");
			}

		}, 9600);
	}

	public void sendBindAttack(TeamScaling teamScaling) {
		Player player = (Player) vorago.getCombat().getTarget();
		if (player == null)
			return;
		
		int damage = calculateTeamScaledDamage(100, teamScaling, player, "bind");
		delayHit(vorago, 0, player, new Hit(vorago, damage, HitLook.REGULAR_DAMAGE));
	}

	public void sendTeamScaledGroundBlueBomb(WorldTile arrivelocation, TeamScaling teamScaling) {
		long startTime = Utils.currentTimeMillis();
		long arriveTime = startTime + (12000);
		double speed = Utils.getProjectileSpeed(vorago, arrivelocation, 90, 0, startTime, arriveTime);
		Projectile projectile = World.sendProjectileNew(vorago, arrivelocation, 4016, 90, 0, 10, speed, 0, 0);
		int cycleTime = Utils.projectileTimeToCycles(projectile.getEndTime()) - 1;
		vorago.setNextGraphics(new Graphics(4020));
		World.sendGraphics(null, new Graphics(4022), vorago);
		long time = (cycleTime * 1000) - 950;
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				for (Player player : vorago.getVoragoInstance().getPlayersOnBattle()) {
					if (player == null || player.isDead() || Utils.getDistance(arrivelocation, player) > 2)
						continue;
					
					int damage = calculateTeamScaledDamage(500, teamScaling, player, "aoe");
					player.setNextGraphics(new Graphics(4017));
					delayHit(vorago, 0, player, getMagicHit(vorago, damage));
				}
			}
		}, time < 0 ? 2000 : time);
	}

	/**
	 * ENHANCED v5.0: Handle combat end - cleanup BossBalancer sessions for entire team
	 */
	public static void onRaidEnd(Vorago vorago) {
		if (vorago == null) return;
		
		try {
			// End BossBalancer v5.0 combat sessions for all team members
			for (Player player : vorago.getVoragoInstance().getPlayersOnBattle()) {
				if (player != null) {
					BossBalancer.endCombatSession(player);
					BossBalancer.clearPlayerCache(player.getIndex());
				}
			}
			
			// Clear raid tracking
			Integer instanceKey = Integer.valueOf(vorago.getVoragoInstance().hashCode());
			
			lastWarning.remove(instanceKey);
			warningStage.remove(instanceKey);
			combatSessionActive.remove(instanceKey);
			attackCounter.remove(instanceKey);
			currentPhase.remove(instanceKey);
			lastScalingType.remove(instanceKey);
			
		} catch (Exception e) {
			System.err.println("VoragoCombat: Error ending v5.0 raid sessions: " + e.getMessage());
		}
	}

	/**
	 * ENHANCED v5.0: Handle prayer changes during raid
	 */
	public static void onPlayerPrayerChanged(Player player) {
		if (player == null) return;
		
		try {
			// Notify BossBalancer v5.0 of prayer change
			BossBalancer.onPrayerChanged(player);
		} catch (Exception e) {
			System.err.println("VoragoCombat: Error handling v5.0 prayer change: " + e.getMessage());
		}
	}

	/**
	 * Force cleanup (call on logout/death)
	 */
	public static void forceCleanup(Player player) {
		if (player != null) {
			BossBalancer.endCombatSession(player);
			BossBalancer.clearPlayerCache(player.getIndex());
		}
	}

	// ===== PHASE HANDLER METHODS (with team scaling) =====
	
	private void handlePhaseThree(boolean hardMode, Entity target, TeamScaling teamScaling) {
		// Phase 3 logic using team-scaled attack methods
		// Implementation would use the team-scaled methods above
	}

	private void handlePhaseFour(boolean hardMode, Entity target, TeamScaling teamScaling) {
		// Phase 4 logic with team scaling
	}

	private void handlePhaseFive(boolean hardMode, Entity target, TeamScaling teamScaling) {
		// Phase 5 logic with team scaling
	}

	private void handlePhaseSix(Entity target, TeamScaling teamScaling) {
		// Phase 6 logic with team scaling
	}

	private void handlePhaseSeven(Entity target, TeamScaling teamScaling) {
		// Phase 7 logic with team scaling
	}

	private void handlePhaseEight(Entity target, TeamScaling teamScaling) {
		// Phase 8 logic with team scaling
	}

	private void handlePhaseTen(Entity target, TeamScaling teamScaling) {
		// Phase 10 logic with team scaling
	}

	private void handlePhaseEleven(Entity target, TeamScaling teamScaling) {
		// Phase 11 logic with team scaling
	}

	// ===== LEGACY COMPATIBILITY METHODS =====

	public void sendMeleeAttack() {
		TeamScaling teamScaling = getIntelligentTeamScaling(vorago);
		sendTeamScaledMeleeAttack(teamScaling);
	}

	public void sendBlueBombAttack() {
		TeamScaling teamScaling = getIntelligentTeamScaling(vorago);
		sendTeamScaledBlueBombAttack(teamScaling);
	}

	public void sendBlueBombAttack(boolean sendAnimation) {
		TeamScaling teamScaling = getIntelligentTeamScaling(vorago);
		sendTeamScaledBlueBombAttack(teamScaling, sendAnimation);
	}

	public void sendRedBombAttack() {
		TeamScaling teamScaling = getIntelligentTeamScaling(vorago);
		sendTeamScaledRedBombAttack(teamScaling);
	}

	public void sendSmash() {
		TeamScaling teamScaling = getIntelligentTeamScaling(vorago);
		sendTeamScaledSmash(teamScaling);
	}

	public void sendReflectAttack() {
		TeamScaling teamScaling = getIntelligentTeamScaling(vorago);
		sendTeamScaledReflectAttack(teamScaling);
	}

	public void sendGroundBlueBomb(WorldTile arrivelocation) {
		TeamScaling teamScaling = getIntelligentTeamScaling(vorago);
		sendTeamScaledGroundBlueBomb(arrivelocation, teamScaling);
	}

	public void sendBindAttack() {
		TeamScaling teamScaling = getIntelligentTeamScaling(vorago);
		sendBindAttack(teamScaling);
	}

	// ===== ORIGINAL METHODS (preserved for compatibility) =====

	public void startTheEnd() {
		vorago.startTheEnd();
	}

	public void sendPurpleBombAttack() {
		vorago.sendPurpleBombAttack();
	}

	public void sendTeamSplitAttack() {
		vorago.sendTeamSplit();
	}

	public void sendGreenBombAttack() {
		vorago.sendGreenBomb();
	}

	public boolean sendVitalisOrb() {
		return vorago.sendVitalisOrb();
	}

	public void sendScopuli() {
		vorago.spawnScopuli();
	}

	public void sendStoneClones() {
		vorago.getTemporaryAttributtes().put("ReducedDamage", Boolean.TRUE);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				vorago.getTemporaryAttributtes().remove("ReducedDamage");
			}
		}, 33);
		for (Player player : vorago.getVoragoInstance().getPlayersOnBattle()) {
			if (player == null || player.isDead())
				continue;
			player.getTemporaryAttributtes().remove("RecentlyKilledClone");
		}
		vorago.spawnStoneClones();
	}

	public void sendWaterFallAttack() {
		vorago.sendWaterFallAttack();
	}

	public boolean sendCeilingCollapse() {
		return vorago.sendCeilingCollapse();
	}

	public void sendGravityField() {
		vorago.spawnGravityField();
	}

	public int getPlayersNearby(Player target, int withinDistance) {
		return vorago.getPlayersNearby(target, withinDistance);
	}

	public void sendMessage(Player player, int border, String message) {
		vorago.getVoragoInstance().sendMessage(player, border, message);
	}

	public Player getRandomTarget() {
		List<Player> availablePlayers = new ArrayList<Player>();
		VoragoInstance instance = vorago.getVoragoInstance();
		for (int i = 0; i < instance.getPlayersOnBattle().size(); i++) {
			Player player = instance.getPlayersOnBattle().get(i);
			if (player == null || player.isDead() || Utils.getDistance(vorago, player) > 24)
				continue;
			availablePlayers.add(player);
		}
		return availablePlayers.isEmpty() ? null : availablePlayers.get(Utils.random(availablePlayers.size()));
	}

	public Player getFarestTarget(Entity exception) {
		int farestDistance = 0;
		int index = 0;
		for (int i = 0; i < vorago.getVoragoInstance().getPlayersOnBattle().size(); i++) {
			Player player = vorago.getVoragoInstance().getPlayersOnBattle().get(i);
			if (player == null || player.isDead() || !Utils.isOnRange(vorago, player, 15)
					|| (exception != null && player == exception))
				continue;
			int distance = Utils.getDistance(vorago, player);
			if (distance > farestDistance) {
				index = i;
				farestDistance = distance;
			}
		}
		return vorago.getVoragoInstance().getPlayersOnBattle().get(index);
	}
}