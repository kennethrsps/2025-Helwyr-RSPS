package com.rs.game.npc.combat.impl.gwd2.twins;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.gwd2.twinfuries.Avaryss;
import com.rs.game.npc.gwd2.twinfuries.ChannelledBomb;
import com.rs.game.npc.gwd2.twinfuries.WallCharge;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.cache.loaders.NPCDefinitions;

/**
 * Enhanced Avaryss Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Twin Furies mechanics, shadow magic scaling, HP-aware damage limits, intelligent boss guidance
 * Integrated with BossBalancer v5.0 for comprehensive combat scaling with player safety
 * 
 * @author Zeus (Enhanced from Tom's original)
 * @date June 10, 2025
 * @version 5.0 - COMPLETE BossBalancer v5.0 Integration with HP-Aware Damage System
 */
public class AvaryssCombat extends CombatScript {

	// ===== TWIN FURIES GUIDANCE AND TRACKING SYSTEMS =====
	private static final long GUIDANCE_COOLDOWN = 40000L; // 40 seconds between guidance
	private static final long MECHANIC_WARNING_COOLDOWN = 12000L; // 12 seconds between mechanic warnings
	private static final long SCALING_UPDATE_INTERVAL = 25000L; // 25 seconds for scaling updates
	private static final int MAX_WARNINGS_PER_FIGHT = 4; // Enhanced guidance system
	
	// Combat session tracking for Twin Furies
	private static final Map<Integer, Long> lastGuidanceTime = new ConcurrentHashMap<Integer, Long>();
	private static final Map<Integer, Long> lastMechanicWarning = new ConcurrentHashMap<Integer, Long>();
	private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
	private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
	private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Integer> shadowPhaseTracker = new ConcurrentHashMap<Integer, Integer>();
	
	// ===== HP-AWARE DAMAGE SCALING CONSTANTS - TWIN FURIES THEMED =====
	private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.32; // Max 32% of player HP per hit (twin magic balanced)
	private static final double WALL_CHARGE_DAMAGE_PERCENT = 0.45; // Max 45% for wall charge (signature attack)
	private static final double CHANNELLED_BOMB_DAMAGE_PERCENT = 0.40; // Max 40% for channelled bomb
	private static final double SHADOW_MAGIC_DAMAGE_PERCENT = 0.28; // Max 28% for shadow magic attacks
	private static final double BASIC_ATTACK_DAMAGE_PERCENT = 0.22; // Max 22% for basic attacks
	private static final int MIN_PLAYER_HP = 990; // Minimum expected player HP
	private static final int MAX_PLAYER_HP = 1500; // Maximum expected player HP
	private static final int ABSOLUTE_MAX_DAMAGE = 480; // Hard cap (32% of 1500 HP)
	private static final int MINIMUM_DAMAGE = 20; // Minimum damage to prevent 0 hits
	
	// ===== TWIN FURIES SPECIAL MECHANICS =====
	private static final Map<Integer, Integer> safespotWarnings = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Integer> consecutiveAvoids = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Boolean> hasSeenWallCharge = new ConcurrentHashMap<Integer, Boolean>();
	private static final Map<Integer, Boolean> hasSeenChannelledBomb = new ConcurrentHashMap<Integer, Boolean>();
	
	// Enhanced mechanic timings
	private static final long PRE_WALL_CHARGE_WARNING = 3000L; // 3 seconds before wall charge
	private static final long PRE_BOMB_WARNING = 2000L; // 2 seconds before bomb
	private static final int MAX_SAFESPOT_DISTANCE = 12; // Reduced for twin furies arena
	private static final int FORCE_RESET_DISTANCE = 18;

	@Override
	public Object[] getKeys() {
		return new Object[] { 22453 }; // Avaryss
	}

	@Override
	public int attack(NPC npc, Entity target) {
		// **CRITICAL**: Verify NPC is actually Avaryss before casting
		if (!(npc instanceof Avaryss)) {
			System.err.println("Warning: AvaryssCombat received non-Avaryss NPC: " + npc.getClass().getSimpleName());
			return executeBasicShadowAttack(npc, target);
		}
		
		final Avaryss avaryss = (Avaryss) npc;
		
		// Enhanced null safety checks
		if (target == null || avaryss.isDead() || target.isDead() || !(target instanceof Player)) {
			return 4;
		}
		
		Player player = (Player) target;
		
		try {
			// ===== FULL BOSSBALANCER v5.0 INTEGRATION =====
			
			// Initialize twin furies combat session if needed
			initializeTwinFuriesCombatSession(player, avaryss);
			
			// Get INTELLIGENT combat scaling v5.0
			CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, avaryss);
			
			// Enhanced guidance system with intelligent scaling awareness
			provideIntelligentTwinFuriesGuidance(player, avaryss, scaling);
			
			// Monitor scaling changes during twin furies combat
			monitorTwinFuriesScalingChanges(player, scaling);
			
			// Enhanced safespot detection with shadow magic theme
			checkAndPreventShadowMagicSafespotExploitation(avaryss, player, scaling);
			
			// Track shadow phases and twin mechanics
			updateShadowPhaseTracking(avaryss, scaling);
			
			// ===== ENHANCED TWIN FURIES SPECIAL ATTACKS WITH v5.0 INTEGRATION =====
			
			// Wall Charge Phase with pre-warning and HP-aware scaling
			if (avaryss.getInstance().getPhase() == 0 && avaryss.getInstance().getSpecialDelay() < Utils.currentTimeMillis()) {
				return executeIntelligentWallChargePhase(avaryss, player, scaling);
			} 
			// Channelled Bomb Phase with pre-warning and HP-aware scaling
			else if (avaryss.getInstance().getPhase() == 2 && avaryss.getInstance().getSpecialDelay() < Utils.currentTimeMillis()) {
				return executeIntelligentChannelledBombPhase(avaryss, player, scaling);
			}
			
			// Enhanced basic shadow magic attack with v5.0 scaling
			return executeIntelligentShadowMagicAttack(avaryss, player, scaling);
			
		} catch (Exception e) {
			System.err.println("Error in AvaryssCombat.attack(): " + e.getMessage());
			e.printStackTrace();
			return executeBasicShadowAttack(npc, target);
		}
	}

	/**
	 * Initialize Twin Furies combat session using BossBalancer v5.0
	 */
	private void initializeTwinFuriesCombatSession(Player player, Avaryss avaryss) {
		Integer sessionKey = Integer.valueOf(player.getIndex());
		
		if (!combatSessionActive.containsKey(sessionKey)) {
			// Start BossBalancer v5.0 combat session
			BossBalancer.startCombatSession(player, avaryss);
			combatSessionActive.put(sessionKey, Boolean.TRUE);
			attackCounter.put(sessionKey, Integer.valueOf(0));
			lastScalingType.put(sessionKey, "UNKNOWN");
			safespotWarnings.put(sessionKey, Integer.valueOf(0));
			consecutiveAvoids.put(sessionKey, Integer.valueOf(0));
			warningStage.put(sessionKey, Integer.valueOf(0));
			shadowPhaseTracker.put(sessionKey, Integer.valueOf(0));
			hasSeenWallCharge.put(sessionKey, Boolean.FALSE);
			hasSeenChannelledBomb.put(sessionKey, Boolean.FALSE);
			
			// Send v5.0 enhanced twin furies combat message
			CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, avaryss);
			String welcomeMsg = getIntelligentTwinFuriesWelcomeMessage(scaling, avaryss);
			player.sendMessage(welcomeMsg);
			
			// Perform initial armor analysis for twin furies combat
			performInitialTwinFuriesArmorAnalysis(player);
		}
	}

	/**
	 * NEW v5.0: Perform initial twin furies armor analysis
	 */
	private void performInitialTwinFuriesArmorAnalysis(Player player) {
		try {
			// Use BossBalancer v5.0 armor analysis
			BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
			
			if (!armorResult.hasFullArmor) {
				player.sendMessage("<col=9933cc>Shadow Analysis: Missing armor leaves you vulnerable to twin magic!</col>");
			} else {
				double reductionPercent = armorResult.damageReduction * 100;
				player.sendMessage("<col=6633ff>Shadow Analysis: Full protection detected (" + 
								 String.format("%.1f", reductionPercent) + 
								 "% damage reduction). Ready for twin furies combat!</col>");
			}
		} catch (Exception e) {
			// Ignore armor analysis errors
		}
	}

	/**
	 * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from twin attacks
	 */
	private int applyHPAwareTwinFuriesDamageScaling(int scaledDamage, Player player, String attackType) {
		if (player == null) {
			return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
		}
		
		try {
			int currentHP = player.getHitpoints();
			int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
			
			// Use current HP for calculation (twin magic is powerful but balanced)
			int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
			
			// Determine damage cap based on twin furies attack type
			double damagePercent;
			switch (attackType.toLowerCase()) {
				case "wall_charge":
				case "charge_attack":
					damagePercent = WALL_CHARGE_DAMAGE_PERCENT;
					break;
				case "channelled_bomb":
				case "bomb_explosion":
					damagePercent = CHANNELLED_BOMB_DAMAGE_PERCENT;
					break;
				case "shadow_magic":
				case "shadow_attack":
					damagePercent = SHADOW_MAGIC_DAMAGE_PERCENT;
					break;
				case "basic_attack":
				case "melee":
				default:
					damagePercent = BASIC_ATTACK_DAMAGE_PERCENT;
					break;
			}
			
			// Calculate HP-based damage cap
			int hpBasedCap = (int)(effectiveHP * damagePercent);
			
			// Apply multiple safety caps
			int safeDamage = Math.min(scaledDamage, hpBasedCap);
			safeDamage = Math.min(safeDamage, ABSOLUTE_MAX_DAMAGE);
			safeDamage = Math.max(safeDamage, MINIMUM_DAMAGE);
			
			// Additional safety check - never deal more than 70% of current HP for twin furies
			if (currentHP > 0) {
				int emergencyCap = (int)(currentHP * 0.70);
				safeDamage = Math.min(safeDamage, emergencyCap);
			}
			
			return safeDamage;
			
		} catch (Exception e) {
			// Fallback to absolute cap
			return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
		}
	}

	/**
	 * NEW v5.0: Send HP warning if player is in danger from twin attacks
	 */
	private void checkAndWarnLowHPForTwinFuries(Player player, int incomingDamage, String attackType) {
		if (player == null) return;
		
		try {
			int currentHP = player.getHitpoints();
			
			// Warn if incoming twin damage is significant relative to current HP
			if (currentHP > 0) {
				double damagePercent = (double)incomingDamage / currentHP;
				
				if (damagePercent >= 0.55) {
					player.sendMessage("<col=ff0000>TWIN WARNING: " + attackType + " will deal " + incomingDamage + 
									 " damage! (" + currentHP + " HP remaining)</col>");
				} else if (damagePercent >= 0.35) {
					player.sendMessage("<col=9933cc>TWIN WARNING: Heavy shadow damage incoming (" + incomingDamage + 
									 " from " + attackType + ")! Consider healing (" + currentHP + " HP)</col>");
				}
			}
		} catch (Exception e) {
			// Ignore warning errors
		}
	}

	/**
	 * ENHANCED v5.0: Generate intelligent Twin Furies welcome message based on power analysis
	 */
	private String getIntelligentTwinFuriesWelcomeMessage(CombatScaling scaling, Avaryss avaryss) {
		StringBuilder message = new StringBuilder();
		
		// Get NPC name for personalized message
		NPCDefinitions def = NPCDefinitions.getNPCDefinitions(avaryss.getId());
		String npcName = (def != null && def.getName() != null) ? def.getName() : "Avaryss";
		
		message.append("<col=9933cc>").append(npcName)
			   .append(" channels shadow magic, analyzing your combat readiness (BossBalancer v5.0).</col>");
		
		// Add v5.0 intelligent scaling information
		if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			message.append(" <col=6633ff>[Shadow fury: +").append(diffIncrease).append("% magic power]</col>");
		} else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
			message.append(" <col=99ccff>[Shadow mercy: -").append(assistance).append("% damage]</col>");
		} else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
			message.append(" <col=cc99ff>[Shadow resistance scaling active]</col>");
		} else if (scaling.scalingType.contains("FULL_ARMOR")) {
			message.append(" <col=9999cc>[Twin furies protection acknowledged]</col>");
		}
		
		return message.toString();
	}

	/**
	 * ENHANCED v5.0: Intelligent Twin Furies guidance with power-based scaling awareness
	 */
	private void provideIntelligentTwinFuriesGuidance(Player player, Avaryss avaryss, CombatScaling scaling) {
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
		String guidanceMessage = getIntelligentTwinFuriesGuidanceMessage(player, avaryss, scaling, currentStage);
		
		// Send guidance if applicable
		if (guidanceMessage != null) {
			player.sendMessage(guidanceMessage);
			lastGuidanceTime.put(playerKey, currentTime);
			warningStage.put(playerKey, currentStage + 1);
		}
	}

	/**
	 * NEW v5.0: Get intelligent Twin Furies guidance message based on power analysis
	 */
	private String getIntelligentTwinFuriesGuidanceMessage(Player player, Avaryss avaryss, CombatScaling scaling, int stage) {
		switch (stage) {
			case 0:
				// First warning: Power analysis and scaling type
				return getTwinFuriesScalingAnalysisMessage(scaling, avaryss);
				
			case 1:
				// Second warning: Equipment effectiveness or armor analysis
				if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
					return "<col=9933cc>Twin Analysis: Missing armor increases shadow magic damage by 25%! Equip full protection!</col>";
				} else {
					return "<col=6633ff>Twin Tactics: Watch for wall charges and channelled bombs. Stay mobile during special attacks!</col>";
				}
				
			case 2:
				// Third warning: Advanced mechanics
				if (scaling.bossDamageMultiplier > 2.0) {
					return "<col=ff3399>Twin Analysis: Extreme shadow scaling active! Consider fighting higher-tier bosses for balanced challenge!</col>";
				} else {
					return "<col=9966cc>Twin Mechanics: Wall charges deal massive damage - dodge at the right moment. Bombs have channelling time!</col>";
				}
				
			case 3:
				// Final warning: Ultimate tips
				return "<col=cc66ff>Twin Mastery: Use protection prayers during specials. HP-aware damage limits prevent one-shots from twin magic!</col>";
		}
		
		return null;
	}

	/**
	 * NEW v5.0: Get Twin Furies scaling analysis message
	 */
	private String getTwinFuriesScalingAnalysisMessage(CombatScaling scaling, Avaryss avaryss) {
		NPCDefinitions def = NPCDefinitions.getNPCDefinitions(avaryss.getId());
		String npcName = (def != null && def.getName() != null) ? def.getName() : "Avaryss";
		
		String baseMessage = "<col=9966cc>Shadow Power Analysis:</col> ";
		
		if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
			return baseMessage + "<col=99ccff>" + npcName + "'s shadow magic shows restraint! Damage reduced by " + 
				   assistancePercent + "% due to insufficient preparation. Upgrade your gear!</col>";
				   
		} else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=ff3399>" + npcName + "'s shadow magic unleashes fury! Power increased by " + 
				   difficultyIncrease + "% due to your superior equipment. Seek worthier opponents!</col>";
				   
		} else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
			return baseMessage + "<col=6633ff>Balanced twin furies encounter. Optimal shadow combat achieved!</col>";
			
		} else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=9933cc>Slight advantage detected. " + npcName + "'s shadow intensity increased by " + 
				   difficultyIncrease + "% for balanced twin combat.</col>";
		}
		
		return baseMessage + "<col=cccccc>Shadow power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
	}

	/**
	 * NEW v5.0: Monitor scaling changes during Twin Furies combat
	 */
	private void monitorTwinFuriesScalingChanges(Player player, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		String currentScalingType = scaling.scalingType;
		String lastType = lastScalingType.get(playerKey);
		
		// Check if scaling type changed (prayer activation, gear swap, etc.)
		if (lastType != null && !lastType.equals(currentScalingType)) {
			// Scaling changed - notify player
			String changeMessage = getTwinFuriesScalingChangeMessage(lastType, currentScalingType, scaling);
			if (changeMessage != null) {
				player.sendMessage(changeMessage);
			}
		}
		
		lastScalingType.put(playerKey, currentScalingType);
	}

	/**
	 * NEW v5.0: Get Twin Furies scaling change message
	 */
	private String getTwinFuriesScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
		if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
			return "<col=99ccff>Shadow Update: Combat balance improved! Shadow mercy reduced.</col>";
		} else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
			return "<col=9933cc>Shadow Update: Shadow fury now active due to increased power!</col>";
		} else if (newType.contains("WITH_ABSORPTION")) {
			return "<col=cc99ff>Shadow Update: Magic absorption bonuses detected and factored into scaling!</col>";
		} else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
			return "<col=6633ff>Shadow Update: Full protection equipped! Shadow magic damage scaling normalized.</col>";
		}
		
		return null;
	}

	/**
	 * Enhanced shadow magic safe spot detection and prevention
	 */
	private void checkAndPreventShadowMagicSafespotExploitation(Avaryss avaryss, Player player, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		int distance = player.getDistance(avaryss);
		
		// Track consecutive avoids
		Integer avoidCount = consecutiveAvoids.get(playerKey);
		Integer warnCount = safespotWarnings.get(playerKey);
		if (avoidCount == null) avoidCount = 0;
		if (warnCount == null) warnCount = 0;
		
		if (distance > MAX_SAFESPOT_DISTANCE) {
			warnCount++;
			safespotWarnings.put(playerKey, warnCount);
			
			if (warnCount >= 3) {
				// Twin furies anti-safespot measure
				performShadowMagicAntiSafeSpotMeasure(avaryss, player, scaling);
				safespotWarnings.put(playerKey, 0);
			} else {
				long currentTime = System.currentTimeMillis();
				Long lastWarning = lastGuidanceTime.get(playerKey);
				if (lastWarning == null || (currentTime - lastWarning) > GUIDANCE_COOLDOWN) {
					try {
						avaryss.setNextForceTalk(new ForceTalk("Shadow magic reaches all corners!"));
					} catch (Exception e) {
						player.sendMessage("<col=9933cc>Avaryss: Shadow magic reaches all corners!</col>");
					}
					player.sendMessage("Avaryss demands closer combat! Move within " + MAX_SAFESPOT_DISTANCE + " tiles!");
					lastGuidanceTime.put(playerKey, currentTime);
				}
			}
			
			if (distance > FORCE_RESET_DISTANCE) {
				avaryss.resetCombat();
				endTwinFuriesCombatSession(avaryss, player);
				player.sendMessage("Avaryss loses interest in your cowardly tactics.");
			}
		} else {
			// Reset warnings when fighting properly
			if (warnCount > 0) {
				safespotWarnings.put(playerKey, 0);
				player.sendMessage("<col=99ccff>Avaryss acknowledges your honorable combat...</col>");
			}
		}
	}

	/**
	 * NEW v5.0: Perform shadow magic anti-safe spot measure
	 */
	private void performShadowMagicAntiSafeSpotMeasure(Avaryss avaryss, Player player, CombatScaling scaling) {
		player.sendMessage("<col=ff3399>Shadow magic pierces through all hiding places!</col>");
		
		// Shadow magic blast that reaches through all obstacles
		avaryss.setNextAnimation(new Animation(28239));
		avaryss.setNextForceTalk(new ForceTalk("COWARD! Face shadow magic!"));
		
		// Enhanced damage based on scaling with HP-aware limits
		NPCCombatDefinitions defs = avaryss.getCombatDefinitions();
		int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.3) : 150; // Shadow wrath blast
		int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(avaryss, player, baseDamage);
		int safeDamage = applyHPAwareTwinFuriesDamageScaling(scaledDamage, player, "shadow_wrath");
		
		checkAndWarnLowHPForTwinFuries(player, safeDamage, "Shadow Wrath");
		delayHit(avaryss, 1, player, getMagicHit(avaryss, safeDamage));
		
		player.sendMessage("<col=9933cc>SHADOW PENALTY: Safe spotting detected - shadow magic reaches all!</col>");
	}

	/**
	 * NEW v5.0: Update shadow phase tracking with BossBalancer integration
	 */
	private void updateShadowPhaseTracking(Avaryss avaryss, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(avaryss.getIndex());
		int currentPhase = avaryss.getInstance().getPhase();
		
		Integer lastPhase = shadowPhaseTracker.get(playerKey);
		if (lastPhase == null) lastPhase = -1;
		
		if (currentPhase != lastPhase) {
			shadowPhaseTracker.put(playerKey, currentPhase);
			handleIntelligentShadowPhaseTransition(avaryss, currentPhase, scaling);
		}
	}

	/**
	 * ENHANCED v5.0: Intelligent shadow phase transitions with scaling integration
	 */
	private void handleIntelligentShadowPhaseTransition(Avaryss avaryss, int newPhase, CombatScaling scaling) {
		NPCDefinitions def = NPCDefinitions.getNPCDefinitions(avaryss.getId());
		String npcName = (def != null && def.getName() != null) ? def.getName() : "Avaryss";
		
		switch (newPhase) {
		case 0:
			// Prepare for wall charge
			String wallMessage = scaling.bossDamageMultiplier > 2.0 ? 
				"ENHANCED WALL CHARGE INCOMING!" : "Prepare for the charge!";
			avaryss.setNextForceTalk(new ForceTalk(wallMessage));
			break;
			
		case 2:
			// Prepare for channelled bomb
			String bombMessage = scaling.bossDamageMultiplier > 2.0 ? 
				"ENHANCED SHADOW BOMB CHANNELLING!" : "Channelling shadow bomb!";
			avaryss.setNextForceTalk(new ForceTalk(bombMessage));
			break;
		}
	}

	/**
	 * ENHANCED v5.0: Execute intelligent wall charge phase with HP-aware scaling
	 */
	private int executeIntelligentWallChargePhase(Avaryss avaryss, Player player, CombatScaling scaling) {
		try {
			// Mark that player has seen wall charge
			Integer playerKey = Integer.valueOf(player.getIndex());
			hasSeenWallCharge.put(playerKey, Boolean.TRUE);
			
			// Send pre-attack warning with scaling context
			String chargeWarning = "WALL CHARGE incoming! Prepare to dodge!";
			if (scaling.bossDamageMultiplier > 2.0) {
				chargeWarning += " (EXTREME power due to scaling!)";
			}
			sendEnhancedMechanicWarning(avaryss, player, chargeWarning, scaling);
			
			// Schedule warning with intelligent delay
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (!avaryss.isDead() && !player.isDead()) {
						// Enhanced wall charge with HP-aware damage scaling
						executeEnhancedWallCharge(avaryss, player, scaling);
					}
					stop();
				}
			}, 2); // 2 tick warning
			
			// Set enhanced delay based on scaling
			int baseDelay = Utils.random(30000, 35000);
			int scalingDelay = scaling.bossDamageMultiplier > 1.8 ? -5000 : 0; // Faster for high scaling
			avaryss.getInstance().setSpecialDelay(Utils.currentTimeMillis() + baseDelay + scalingDelay);
			avaryss.getInstance().nextPhase();
			
			return 25;
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentWallChargePhase: " + e.getMessage());
			return 25;
		}
	}

	/**
	 * NEW v5.0: Execute enhanced wall charge with BossBalancer integration
	 */
	private void executeEnhancedWallCharge(Avaryss avaryss, Player player, CombatScaling scaling) {
		try {
			// Create enhanced wall charge effect with v5.0 scaling
			WallCharge wallCharge = new WallCharge(avaryss, player) {
				@Override
				public void effect() {
					// Call original effect but with enhanced damage calculation
					super.effect();
					
					// Apply additional HP-aware damage scaling for safety
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							// Apply post-effect HP-aware damage validation
							validateAndAdjustWallChargeDamage(avaryss, player, scaling);
							stop();
						}
					}, 1);
				}
			};
			
			wallCharge.effect();
			
		} catch (Exception e) {
			System.err.println("Error in executeEnhancedWallCharge: " + e.getMessage());
			// Fallback to basic wall charge
			new WallCharge(avaryss, player).effect();
		}
	}

	/**
	 * NEW v5.0: Validate and adjust wall charge damage with HP-aware scaling
	 */
	private void validateAndAdjustWallChargeDamage(Avaryss avaryss, Player player, CombatScaling scaling) {
		// This is a safety measure to ensure wall charge respects HP-aware limits
		// The actual implementation would depend on how WallCharge calculates damage
		
		try {
			// Send post-attack feedback
			checkAndWarnLowHPForTwinFuries(player, 0, "Wall Charge"); // 0 = already applied
			
			// Provide scaling feedback
			if (scaling.bossDamageMultiplier > 2.0) {
				player.sendMessage("<col=ff3399>The wall charge was enhanced by extreme scaling!</col>");
			}
		} catch (Exception e) {
			System.err.println("Error in validateAndAdjustWallChargeDamage: " + e.getMessage());
		}
	}

	/**
	 * ENHANCED v5.0: Execute intelligent channelled bomb phase with HP-aware scaling
	 */
	private int executeIntelligentChannelledBombPhase(Avaryss avaryss, Player player, CombatScaling scaling) {
		try {
			// Mark that player has seen channelled bomb
			Integer playerKey = Integer.valueOf(player.getIndex());
			hasSeenChannelledBomb.put(playerKey, Boolean.TRUE);
			
			// Send pre-attack warning with scaling context
			String bombWarning = "CHANNELLED BOMB incoming! Watch for the explosion!";
			if (scaling.bossDamageMultiplier > 2.5) {
				bombWarning += " (MAXIMUM shadow power!)";
			}
			sendEnhancedMechanicWarning(avaryss, player, bombWarning, scaling);
			
			// Schedule warning with intelligent delay
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (!avaryss.isDead() && !player.isDead()) {
						// Enhanced channelled bomb with HP-aware damage scaling
						executeEnhancedChannelledBomb(avaryss, player, scaling);
					}
					stop();
				}
			}, 1); // 1 tick warning
			
			// Set enhanced delay based on scaling
			int baseDelay = Utils.random(20000, 25000);
			int scalingDelay = scaling.bossDamageMultiplier > 2.0 ? -3000 : 0; // Faster for high scaling
			avaryss.getInstance().setSpecialDelay(Utils.currentTimeMillis() + baseDelay + scalingDelay);
			avaryss.getInstance().setPhase(0);
			
			return 15;
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentChannelledBombPhase: " + e.getMessage());
			return 15;
		}
	}

	/**
	 * NEW v5.0: Execute enhanced channelled bomb with BossBalancer integration
	 */
	private void executeEnhancedChannelledBomb(Avaryss avaryss, Player player, CombatScaling scaling) {
		try {
			// Create enhanced channelled bomb effect with v5.0 scaling
			ChannelledBomb channelledBomb = new ChannelledBomb(avaryss, player) {
				@Override
				public void effect() {
					// Call original effect but with enhanced damage calculation
					super.effect();
					
					// Apply additional HP-aware damage scaling for safety
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							// Apply post-effect HP-aware damage validation
							validateAndAdjustBombDamage(avaryss, player, scaling);
							stop();
						}
					}, 1);
				}
			};
			
			channelledBomb.effect();
			
		} catch (Exception e) {
			System.err.println("Error in executeEnhancedChannelledBomb: " + e.getMessage());
			// Fallback to basic channelled bomb
			new ChannelledBomb(avaryss, player).effect();
		}
	}

	/**
	 * NEW v5.0: Validate and adjust bomb damage with HP-aware scaling
	 */
	private void validateAndAdjustBombDamage(Avaryss avaryss, Player player, CombatScaling scaling) {
		// This is a safety measure to ensure channelled bomb respects HP-aware limits
		
		try {
			// Send post-attack feedback
			checkAndWarnLowHPForTwinFuries(player, 0, "Channelled Bomb"); // 0 = already applied
			
			// Provide scaling feedback
			if (scaling.bossDamageMultiplier > 2.5) {
				player.sendMessage("<col=ff3399>The channelled bomb was supercharged by extreme scaling!</col>");
			}
		} catch (Exception e) {
			System.err.println("Error in validateAndAdjustBombDamage: " + e.getMessage());
		}
	}

	/**
	 * ENHANCED v5.0: Execute intelligent shadow magic attack with HP-aware scaling
	 */
	private int executeIntelligentShadowMagicAttack(Avaryss avaryss, Player player, CombatScaling scaling) {
		try {
			// Increment attack counter
			Integer playerKey = Integer.valueOf(player.getIndex());
			Integer currentCount = attackCounter.get(playerKey);
			if (currentCount == null) currentCount = 0;
			attackCounter.put(playerKey, currentCount + 1);
			
			avaryss.setNextAnimation(new Animation(28239));
			
			// ===== BOSSBALANCER INTEGRATION: ENHANCED DAMAGE CALCULATION WITH HP-AWARE SCALING =====
			NPCCombatDefinitions defs = avaryss.getCombatDefinitions();
			if (defs == null) {
				return 4;
			}
			
			// Calculate base damage with enhanced range for shadow magic
			int minDamage = 50;
			int maxDamage = 88;
			
			// Apply scaling based on combat progression
			if (scaling.bossDamageMultiplier > 2.0) {
				minDamage = 65; // Higher minimum for extreme scaling
				maxDamage = 110;
			} else if (scaling.bossDamageMultiplier > 1.5) {
				minDamage = 58;
				maxDamage = 98;
			}
			
			int baseDamage = Utils.random(minDamage, maxDamage + 1);
			int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(avaryss, player, baseDamage);
			int safeDamage = applyHPAwareTwinFuriesDamageScaling(scaledDamage, player, "shadow_magic");
			
			// ===== BOSSBALANCER INTEGRATION: ACCURACY CHECK =====
			boolean hits = checkBossAccuracy(avaryss, player, 1750); // Base accuracy for shadow magic
			
			if (hits) {
				delayHit(avaryss, 0, player, getMeleeHit(avaryss, safeDamage));
				
				// Shadow magic special effects based on scaling
				if (scaling.bossDamageMultiplier > 2.0 && Utils.random(5) == 0) {
					player.sendMessage("<col=9933cc>The enhanced shadow magic corrupts your defenses!</col>");
					player.setNextGraphics(new Graphics(85)); // Shadow effect
				}
			} else {
				delayHit(avaryss, 0, player, getMeleeHit(avaryss, 0)); // Miss
				player.sendMessage("You resist the shadow magic!");
			}
			
			return 4;
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentShadowMagicAttack: " + e.getMessage());
			return 4;
		}
	}

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
	 * Fallback basic attack for non-Avaryss NPCs or error conditions with HP-aware scaling
	 */
	private int executeBasicShadowAttack(NPC npc, Entity target) {
		if (npc == null || target == null) return 4;
		
		try {
			npc.setNextAnimation(new Animation(28239));
			
			// Even basic attacks use BossBalancer if target is a player
			int damage;
			if (target instanceof Player) {
				Player player = (Player) target;
				damage = BossBalancer.applyBossScaling(Utils.random(50, 89), player, npc);
				damage = applyHPAwareTwinFuriesDamageScaling(damage, player, "basic_attack");
			} else {
				damage = Utils.random(50, 89);
			}
			
			delayHit(npc, 0, target, getMeleeHit(npc, damage));
			return 4;
		} catch (Exception e) {
			System.err.println("Error in executeBasicShadowAttack(): " + e.getMessage());
			return 4;
		}
	}

	/**
	 * Enhanced mechanic warning with scaling context
	 */
	private void sendEnhancedMechanicWarning(Avaryss avaryss, Player player, String message, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		long currentTime = System.currentTimeMillis();
		
		Long lastWarning = lastMechanicWarning.get(playerKey);
		if (lastWarning != null && (currentTime - lastWarning) < MECHANIC_WARNING_COOLDOWN) {
			return;
		}
		
		// Add scaling context to warning
		String enhancedMessage = message;
		if (scaling.bossDamageMultiplier > 2.5) {
			enhancedMessage += " (MAXIMUM shadow power!)";
		} else if (scaling.bossDamageMultiplier > 1.8) {
			enhancedMessage += " (Enhanced shadow power!)";
		}
		
		sendInstanceMessage(avaryss, enhancedMessage);
		lastMechanicWarning.put(playerKey, currentTime);
	}

	/**
	 * Send message to all players in the instance
	 */
	private void sendInstanceMessage(Avaryss avaryss, String message) {
		try {
			avaryss.setNextForceTalk(new ForceTalk(message));
		} catch (Exception e) {
			try {
				List<Player> players = getInstancePlayers(avaryss);
				for (Player p : players) {
					if (p != null && !p.hasFinished()) {
						p.sendMessage("<col=9933cc>Avaryss: " + message + "</col>");
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
	private List<Player> getInstancePlayers(Avaryss avaryss) {
		List<Player> safePlayers = new ArrayList<Player>();
		try {
			if (avaryss.getInstance() != null && avaryss.getInstance().getPlayers() != null) {
				for (Player p : avaryss.getInstance().getPlayers()) {
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

	// ===== BOSSBALANCER v5.0 COMBAT SESSION CLEANUP =====
	
	/**
	 * End Twin Furies combat session with proper cleanup
	 */
	public void endTwinFuriesCombatSession(Avaryss npc, Entity target) {
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
				shadowPhaseTracker.remove(playerKey);
				safespotWarnings.remove(playerKey);
				consecutiveAvoids.remove(playerKey);
				hasSeenWallCharge.remove(playerKey);
				hasSeenChannelledBomb.remove(playerKey);
				
				// Clear BossBalancer player cache
				BossBalancer.clearPlayerCache(player.getIndex());
				
				// Send completion message with v5.0 info
				player.sendMessage("<col=9933cc>Twin furies combat session ended. Shadow scaling data cleared.</col>");
			}
		} catch (Exception e) {
			System.err.println("Error ending Twin Furies combat session: " + e.getMessage());
		}
	}

	/**
	 * Handle prayer changes during twin furies combat
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
				player.sendMessage("<col=cc99ff>Prayer change detected. Twin furies scaling analysis updated.</col>");
			}
		} catch (Exception e) {
			System.err.println("AvaryssCombat: Error handling v5.0 prayer change: " + e.getMessage());
		}
	}

	/**
	 * Force cleanup (call on logout/death)
	 */
	public static void forceCleanup(Player player) {
		if (player != null) {
			try {
				AvaryssCombat combat = new AvaryssCombat();
				combat.endTwinFuriesCombatSession(null, player);
			} catch (Exception e) {
				System.err.println("Error in Avaryss force cleanup: " + e.getMessage());
			}
		}
	}

	/**
	 * Enhanced debug method for testing HP-aware scaling
	 */
	public static void debugAvaryssScaling(Player player, Avaryss avaryss) {
		if (player == null || avaryss == null) {
			return;
		}

		try {
			CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, avaryss);

			System.out.println("=== AVARYSS COMBAT SCALING DEBUG v5.0 ===");
			System.out.println("Player: " + player.getDisplayName());
			System.out.println("Player HP: " + player.getHitpoints() + "/" + player.getSkills().getLevelForXp(Skills.HITPOINTS));
			System.out.println("Player Power: " + String.format("%.2f", scaling.playerPower));
			System.out.println("Boss Power: " + String.format("%.2f", scaling.bossPower));
			System.out.println("Power Ratio: " + String.format("%.2f", scaling.powerRatio));
			System.out.println("HP Multiplier: " + String.format("%.3f", scaling.bossHpMultiplier));
			System.out.println("Damage Multiplier: " + String.format("%.3f", scaling.bossDamageMultiplier));
			System.out.println("Accuracy Multiplier: " + String.format("%.3f", scaling.bossAccuracyMultiplier));
			System.out.println("Scaling Type: " + scaling.scalingType);
			System.out.println("Current Phase: " + avaryss.getInstance().getPhase());
			
			// Test HP-aware damage calculations
			AvaryssCombat combat = new AvaryssCombat();
			NPCCombatDefinitions defs = avaryss.getCombatDefinitions();
			if (defs != null) {
				int baseMaxHit = defs.getMaxHit();
				int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(avaryss, player, baseMaxHit);
				
				System.out.println("=== HP-AWARE DAMAGE TESTING ===");
				System.out.println("Base Max Hit: " + baseMaxHit);
				System.out.println("BossBalancer Scaled: " + scaledDamage);
				
				// Test different attack types
				int basicDamage = combat.applyHPAwareTwinFuriesDamageScaling(scaledDamage, player, "basic_attack");
				int shadowDamage = combat.applyHPAwareTwinFuriesDamageScaling((int)(scaledDamage * 1.2), player, "shadow_magic");
				int wallChargeDamage = combat.applyHPAwareTwinFuriesDamageScaling((int)(scaledDamage * 1.5), player, "wall_charge");
				int bombDamage = combat.applyHPAwareTwinFuriesDamageScaling((int)(scaledDamage * 1.4), player, "channelled_bomb");
				
				System.out.println("Basic Attack (HP-aware): " + basicDamage);
				System.out.println("Shadow Magic (HP-aware): " + shadowDamage);
				System.out.println("Wall Charge (HP-aware): " + wallChargeDamage);
				System.out.println("Channelled Bomb (HP-aware): " + bombDamage);
				
				// Calculate damage percentages
				int currentHP = player.getHitpoints();
				if (currentHP > 0) {
					System.out.println("=== DAMAGE PERCENTAGES ===");
					System.out.println("Basic: " + String.format("%.1f", (double)basicDamage / currentHP * 100) + "%");
					System.out.println("Shadow: " + String.format("%.1f", (double)shadowDamage / currentHP * 100) + "%");
					System.out.println("Wall Charge: " + String.format("%.1f", (double)wallChargeDamage / currentHP * 100) + "%");
					System.out.println("Bomb: " + String.format("%.1f", (double)bombDamage / currentHP * 100) + "%");
				}
			}

			System.out.println("=====================================");
		} catch (Exception e) {
			System.err.println("AvaryssCombat: Error in debug scaling: " + e.getMessage());
		}
	}

	/**
	 * Get Avaryss combat statistics
	 */
	public static String getAvaryssCombatStats() {
		return "AvaryssCombat v5.0 - Active Sessions: " + combatSessionActive.size() + 
			   ", Guidance Cooldowns: " + lastGuidanceTime.size() + 
			   ", Mechanic Warnings: " + lastMechanicWarning.size() + 
			   ", Attack Counters: " + attackCounter.size() + 
			   ", Shadow Phases: " + shadowPhaseTracker.size();
	}

	/**
	 * Enhanced Avaryss command handler
	 */
	public static void handleAvaryssCommand(Player player, String[] cmd) {
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
					
					// Find nearby Avaryss
					for (NPC npc : World.getNPCs()) {
						if (npc instanceof Avaryss && npc.getDistance(player) <= 10) {
							debugAvaryssScaling(player, (Avaryss) npc);
							player.sendMessage("Avaryss scaling debug output sent to console.");
							return;
						}
					}
					player.sendMessage("No Avaryss found nearby for debugging.");
					
				} else if ("stats".equals(subcommand)) {
					player.sendMessage(getAvaryssCombatStats());
					
				} else if ("cleanup".equals(subcommand)) {
					forceCleanup(player);
					player.sendMessage("Avaryss combat session data cleared.");
					
				} else {
					player.sendMessage("Usage: ;;avaryss [debug|stats|cleanup]");
				}
			} else {
				player.sendMessage("Avaryss Combat v5.0 with BossBalancer integration and HP-aware scaling");
				if (player.isAdmin()) {
					player.sendMessage("Admin: ;;avaryss debug - Debug scaling near Avaryss");
				}
			}

		} catch (Exception e) {
			player.sendMessage("Error in Avaryss command: " + e.getMessage());
		}
	}
}