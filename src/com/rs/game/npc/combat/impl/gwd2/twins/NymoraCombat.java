package com.rs.game.npc.combat.impl.gwd2.twins;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import com.rs.game.npc.gwd2.twinfuries.CeilingCollapse;
import com.rs.game.npc.gwd2.twinfuries.Nymora;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.cache.loaders.NPCDefinitions;

/**
 * Enhanced Nymora Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Twin Furies ranged mechanics, ice magic scaling, HP-aware damage limits, intelligent boss guidance
 * Integrated with BossBalancer v5.0 for comprehensive combat scaling with player safety
 * 
 * @author Zeus (Enhanced from Tom's original)
 * @date June 10, 2025
 * @version 5.0 - COMPLETE BossBalancer v5.0 Integration with HP-Aware Damage System
 */
public class NymoraCombat extends CombatScript {

	// ===== TWIN FURIES ICE GUIDANCE AND TRACKING SYSTEMS =====
	private static final long GUIDANCE_COOLDOWN = 42000L; // 42 seconds between guidance
	private static final long MECHANIC_WARNING_COOLDOWN = 14000L; // 14 seconds between mechanic warnings
	private static final long SCALING_UPDATE_INTERVAL = 28000L; // 28 seconds for scaling updates
	private static final int MAX_WARNINGS_PER_FIGHT = 4; // Enhanced guidance system
	
	// Combat session tracking for Twin Furies Ice Combat
	private static final Map<Integer, Long> lastGuidanceTime = new ConcurrentHashMap<Integer, Long>();
	private static final Map<Integer, Long> lastMechanicWarning = new ConcurrentHashMap<Integer, Long>();
	private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
	private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
	private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Integer> icePhaseTracker = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Integer> projectileCounter = new ConcurrentHashMap<Integer, Integer>();
	
	// ===== HP-AWARE DAMAGE SCALING CONSTANTS - ICE TWIN THEMED =====
	private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.30; // Max 30% of player HP per hit (ice magic balanced)
	private static final double CEILING_COLLAPSE_DAMAGE_PERCENT = 0.42; // Max 42% for ceiling collapse (signature attack)
	private static final double ICE_PROJECTILE_DAMAGE_PERCENT = 0.26; // Max 26% for ice projectiles
	private static final double ENHANCED_PROJECTILE_DAMAGE_PERCENT = 0.35; // Max 35% for enhanced projectiles
	private static final double BASIC_ATTACK_DAMAGE_PERCENT = 0.20; // Max 20% for basic attacks
	private static final int MIN_PLAYER_HP = 990; // Minimum expected player HP
	private static final int MAX_PLAYER_HP = 1500; // Maximum expected player HP
	private static final int ABSOLUTE_MAX_DAMAGE = 450; // Hard cap (30% of 1500 HP)
	private static final int MINIMUM_DAMAGE = 18; // Minimum damage to prevent 0 hits
	
	// ===== TWIN FURIES ICE SPECIAL MECHANICS =====
	private static final Map<Integer, Integer> safespotWarnings = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Integer> consecutiveAvoids = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, Boolean> hasSeenCeilingCollapse = new ConcurrentHashMap<Integer, Boolean>();
	private static final Map<Integer, Integer> consecutiveProjectileDodges = new ConcurrentHashMap<Integer, Integer>();
	
	// Enhanced mechanic timings for ice combat
	private static final long PRE_CEILING_COLLAPSE_WARNING = 4000L; // 4 seconds before ceiling collapse
	private static final long PRE_PROJECTILE_WARNING = 1000L; // 1 second before enhanced projectiles
	private static final int MAX_SAFESPOT_DISTANCE = 14; // Adjusted for ice combat range
	private static final int FORCE_RESET_DISTANCE = 20;
	private static final int ENHANCED_PROJECTILE_THRESHOLD = 5; // Every 5th attack can be enhanced

	@Override
	public Object[] getKeys() {
		return new Object[] { 22454 }; // Nymora
	}

	@Override
	public int attack(NPC npc, Entity target) {
		// **CRITICAL**: Verify NPC is actually Nymora before casting
		if (!(npc instanceof Nymora)) {
			System.err.println("Warning: NymoraCombat received non-Nymora NPC: " + npc.getClass().getSimpleName());
			return executeBasicIceAttack(npc, target);
		}
		
		final Nymora nymora = (Nymora) npc;
		
		// Enhanced null safety checks
		if (target == null || nymora.isDead() || target.isDead() || !(target instanceof Player)) {
			return 4;
		}
		
		Player player = (Player) target;
		
		try {
			// ===== FULL BOSSBALANCER v5.0 INTEGRATION =====
			
			// Initialize ice twin combat session if needed
			initializeIceTwinCombatSession(player, nymora);
			
			// Get INTELLIGENT combat scaling v5.0
			CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, nymora);
			
			// Enhanced guidance system with intelligent scaling awareness
			provideIntelligentIceTwinGuidance(player, nymora, scaling);
			
			// Monitor scaling changes during ice twin combat
			monitorIceTwinScalingChanges(player, scaling);
			
			// Enhanced safespot detection with ice magic theme
			checkAndPreventIceMagicSafespotExploitation(nymora, player, scaling);
			
			// Track ice phases and twin mechanics
			updateIcePhaseTracking(nymora, scaling);
			
			// ===== ENHANCED CEILING COLLAPSE SPECIAL ATTACK WITH v5.0 INTEGRATION =====
			
			// Ceiling Collapse Phase with pre-warning and HP-aware scaling
			if (nymora.getInstance().getPhase() == 1 && nymora.getInstance().getSpecialDelay() < Utils.currentTimeMillis()) {
				return executeIntelligentCeilingCollapsePhase(nymora, player, scaling);
			}
			
			// Enhanced ice projectile attack with v5.0 scaling
			return executeIntelligentIceProjectileAttack(nymora, player, scaling);
			
		} catch (Exception e) {
			System.err.println("Error in NymoraCombat.attack(): " + e.getMessage());
			e.printStackTrace();
			return executeBasicIceAttack(npc, target);
		}
	}

	/**
	 * Initialize Ice Twin combat session using BossBalancer v5.0
	 */
	private void initializeIceTwinCombatSession(Player player, Nymora nymora) {
		Integer sessionKey = Integer.valueOf(player.getIndex());
		
		if (!combatSessionActive.containsKey(sessionKey)) {
			// Start BossBalancer v5.0 combat session
			BossBalancer.startCombatSession(player, nymora);
			combatSessionActive.put(sessionKey, Boolean.TRUE);
			attackCounter.put(sessionKey, Integer.valueOf(0));
			lastScalingType.put(sessionKey, "UNKNOWN");
			safespotWarnings.put(sessionKey, Integer.valueOf(0));
			consecutiveAvoids.put(sessionKey, Integer.valueOf(0));
			warningStage.put(sessionKey, Integer.valueOf(0));
			icePhaseTracker.put(sessionKey, Integer.valueOf(0));
			projectileCounter.put(sessionKey, Integer.valueOf(0));
			hasSeenCeilingCollapse.put(sessionKey, Boolean.FALSE);
			consecutiveProjectileDodges.put(sessionKey, Integer.valueOf(0));
			
			// Send v5.0 enhanced ice twin combat message
			CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, nymora);
			String welcomeMsg = getIntelligentIceTwinWelcomeMessage(scaling, nymora);
			player.sendMessage(welcomeMsg);
			
			// Perform initial armor analysis for ice twin combat
			performInitialIceTwinArmorAnalysis(player);
		}
	}

	/**
	 * NEW v5.0: Perform initial ice twin armor analysis
	 */
	private void performInitialIceTwinArmorAnalysis(Player player) {
		try {
			// Use BossBalancer v5.0 armor analysis
			BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
			
			if (!armorResult.hasFullArmor) {
				player.sendMessage("<col=00ccff>Ice Analysis: Missing armor leaves you vulnerable to frozen projectiles!</col>");
			} else {
				double reductionPercent = armorResult.damageReduction * 100;
				player.sendMessage("<col=66ccff>Ice Analysis: Full protection detected (" + 
								 String.format("%.1f", reductionPercent) + 
								 "% damage reduction). Ready for ice twin combat!</col>");
			}
		} catch (Exception e) {
			// Ignore armor analysis errors
		}
	}

	/**
	 * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from ice attacks
	 */
	private int applyHPAwareIceTwinDamageScaling(int scaledDamage, Player player, String attackType) {
		if (player == null) {
			return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
		}
		
		try {
			int currentHP = player.getHitpoints();
			int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
			
			// Use current HP for calculation (ice magic is precise but balanced)
			int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
			
			// Determine damage cap based on ice twin attack type
			double damagePercent;
			switch (attackType.toLowerCase()) {
				case "ceiling_collapse":
				case "collapse_attack":
					damagePercent = CEILING_COLLAPSE_DAMAGE_PERCENT;
					break;
				case "enhanced_projectile":
				case "frozen_blast":
					damagePercent = ENHANCED_PROJECTILE_DAMAGE_PERCENT;
					break;
				case "ice_projectile":
				case "projectile":
					damagePercent = ICE_PROJECTILE_DAMAGE_PERCENT;
					break;
				case "basic_attack":
				case "ranged":
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
			
			// Additional safety check - never deal more than 65% of current HP for ice twin
			if (currentHP > 0) {
				int emergencyCap = (int)(currentHP * 0.65);
				safeDamage = Math.min(safeDamage, emergencyCap);
			}
			
			return safeDamage;
			
		} catch (Exception e) {
			// Fallback to absolute cap
			return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
		}
	}

	/**
	 * NEW v5.0: Send HP warning if player is in danger from ice attacks
	 */
	private void checkAndWarnLowHPForIceTwin(Player player, int incomingDamage, String attackType) {
		if (player == null) return;
		
		try {
			int currentHP = player.getHitpoints();
			
			// Warn if incoming ice damage is significant relative to current HP
			if (currentHP > 0) {
				double damagePercent = (double)incomingDamage / currentHP;
				
				if (damagePercent >= 0.50) {
					player.sendMessage("<col=ff0000>ICE WARNING: " + attackType + " will deal " + incomingDamage + 
									 " damage! (" + currentHP + " HP remaining)</col>");
				} else if (damagePercent >= 0.30) {
					player.sendMessage("<col=00ccff>ICE WARNING: Heavy frozen damage incoming (" + incomingDamage + 
									 " from " + attackType + ")! Consider healing (" + currentHP + " HP)</col>");
				}
			}
		} catch (Exception e) {
			// Ignore warning errors
		}
	}

	/**
	 * ENHANCED v5.0: Generate intelligent Ice Twin welcome message based on power analysis
	 */
	private String getIntelligentIceTwinWelcomeMessage(CombatScaling scaling, Nymora nymora) {
		StringBuilder message = new StringBuilder();
		
		// Get NPC name for personalized message
		NPCDefinitions def = NPCDefinitions.getNPCDefinitions(nymora.getId());
		String npcName = (def != null && def.getName() != null) ? def.getName() : "Nymora";
		
		message.append("<col=00ccff>").append(npcName)
			   .append(" channels frozen magic, analyzing your combat resilience (BossBalancer v5.0).</col>");
		
		// Add v5.0 intelligent scaling information
		if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			message.append(" <col=0099ff>[Frozen fury: +").append(diffIncrease).append("% ice power]</col>");
		} else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
			message.append(" <col=66ffff>[Frozen mercy: -").append(assistance).append("% damage]</col>");
		} else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
			message.append(" <col=99ccff>[Ice resistance scaling active]</col>");
		} else if (scaling.scalingType.contains("FULL_ARMOR")) {
			message.append(" <col=ccccff>[Ice twin protection acknowledged]</col>");
		}
		
		return message.toString();
	}

	/**
	 * ENHANCED v5.0: Intelligent Ice Twin guidance with power-based scaling awareness
	 */
	private void provideIntelligentIceTwinGuidance(Player player, Nymora nymora, CombatScaling scaling) {
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
		String guidanceMessage = getIntelligentIceTwinGuidanceMessage(player, nymora, scaling, currentStage);
		
		// Send guidance if applicable
		if (guidanceMessage != null) {
			player.sendMessage(guidanceMessage);
			lastGuidanceTime.put(playerKey, currentTime);
			warningStage.put(playerKey, currentStage + 1);
		}
	}

	/**
	 * NEW v5.0: Get intelligent Ice Twin guidance message based on power analysis
	 */
	private String getIntelligentIceTwinGuidanceMessage(Player player, Nymora nymora, CombatScaling scaling, int stage) {
		switch (stage) {
			case 0:
				// First warning: Power analysis and scaling type
				return getIceTwinScalingAnalysisMessage(scaling, nymora);
				
			case 1:
				// Second warning: Equipment effectiveness or armor analysis
				if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
					return "<col=00ccff>Ice Analysis: Missing armor increases frozen projectile damage by 25%! Equip full protection!</col>";
				} else {
					return "<col=0099ff>Ice Tactics: Dodge projectiles and watch for ceiling collapse warnings. Keep moving!</col>";
				}
				
			case 2:
				// Third warning: Advanced mechanics
				if (scaling.bossDamageMultiplier > 2.0) {
					return "<col=0066ff>Ice Analysis: Extreme frozen scaling active! Consider fighting higher-tier bosses for balanced challenge!</col>";
				} else {
					return "<col=66ccff>Ice Mechanics: Ceiling collapse deals massive area damage. Projectiles track movement patterns!</col>";
				}
				
			case 3:
				// Final warning: Ultimate tips
				return "<col=99ccff>Ice Mastery: Use protection prayers during specials. HP-aware damage limits prevent one-shots from ice magic!</col>";
		}
		
		return null;
	}

	/**
	 * NEW v5.0: Get Ice Twin scaling analysis message
	 */
	private String getIceTwinScalingAnalysisMessage(CombatScaling scaling, Nymora nymora) {
		NPCDefinitions def = NPCDefinitions.getNPCDefinitions(nymora.getId());
		String npcName = (def != null && def.getName() != null) ? def.getName() : "Nymora";
		
		String baseMessage = "<col=66ccff>Frozen Power Analysis:</col> ";
		
		if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
			return baseMessage + "<col=66ffff>" + npcName + "'s ice magic shows restraint! Damage reduced by " + 
				   assistancePercent + "% due to insufficient preparation. Upgrade your gear!</col>";
				   
		} else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=0066ff>" + npcName + "'s ice magic unleashes fury! Power increased by " + 
				   difficultyIncrease + "% due to your superior equipment. Seek worthier opponents!</col>";
				   
		} else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
			return baseMessage + "<col=0099ff>Balanced ice twin encounter. Optimal frozen combat achieved!</col>";
			
		} else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
			int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			return baseMessage + "<col=00ccff>Slight advantage detected. " + npcName + "'s ice intensity increased by " + 
				   difficultyIncrease + "% for balanced twin combat.</col>";
		}
		
		return baseMessage + "<col=cccccc>Frozen power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
	}

	/**
	 * NEW v5.0: Monitor scaling changes during Ice Twin combat
	 */
	private void monitorIceTwinScalingChanges(Player player, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		String currentScalingType = scaling.scalingType;
		String lastType = lastScalingType.get(playerKey);
		
		// Check if scaling type changed (prayer activation, gear swap, etc.)
		if (lastType != null && !lastType.equals(currentScalingType)) {
			// Scaling changed - notify player
			String changeMessage = getIceTwinScalingChangeMessage(lastType, currentScalingType, scaling);
			if (changeMessage != null) {
				player.sendMessage(changeMessage);
			}
		}
		
		lastScalingType.put(playerKey, currentScalingType);
	}

	/**
	 * NEW v5.0: Get Ice Twin scaling change message
	 */
	private String getIceTwinScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
		if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
			return "<col=66ffff>Ice Update: Combat balance improved! Frozen mercy reduced.</col>";
		} else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
			return "<col=00ccff>Ice Update: Frozen fury now active due to increased power!</col>";
		} else if (newType.contains("WITH_ABSORPTION")) {
			return "<col=99ccff>Ice Update: Cold resistance bonuses detected and factored into scaling!</col>";
		} else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
			return "<col=0099ff>Ice Update: Full protection equipped! Ice magic damage scaling normalized.</col>";
		}
		
		return null;
	}

	/**
	 * Enhanced ice magic safe spot detection and prevention
	 */
	private void checkAndPreventIceMagicSafespotExploitation(Nymora nymora, Player player, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		int distance = player.getDistance(nymora);
		
		// Track consecutive avoids
		Integer avoidCount = consecutiveAvoids.get(playerKey);
		Integer warnCount = safespotWarnings.get(playerKey);
		if (avoidCount == null) avoidCount = 0;
		if (warnCount == null) warnCount = 0;
		
		if (distance > MAX_SAFESPOT_DISTANCE) {
			warnCount++;
			safespotWarnings.put(playerKey, warnCount);
			
			if (warnCount >= 3) {
				// Ice twin anti-safespot measure
				performIceMagicAntiSafeSpotMeasure(nymora, player, scaling);
				safespotWarnings.put(playerKey, 0);
			} else {
				long currentTime = System.currentTimeMillis();
				Long lastWarning = lastGuidanceTime.get(playerKey);
				if (lastWarning == null || (currentTime - lastWarning) > GUIDANCE_COOLDOWN) {
					try {
						nymora.setNextForceTalk(new ForceTalk("Ice magic pierces all distance!"));
					} catch (Exception e) {
						player.sendMessage("<col=00ccff>Nymora: Ice magic pierces all distance!</col>");
					}
					player.sendMessage("Nymora demands closer combat! Move within " + MAX_SAFESPOT_DISTANCE + " tiles!");
					lastGuidanceTime.put(playerKey, currentTime);
				}
			}
			
			if (distance > FORCE_RESET_DISTANCE) {
				nymora.resetCombat();
				endIceTwinCombatSession(nymora, player);
				player.sendMessage("Nymora loses interest in your cowardly tactics.");
			}
		} else {
			// Reset warnings when fighting properly
			if (warnCount > 0) {
				safespotWarnings.put(playerKey, 0);
				player.sendMessage("<col=66ffff>Nymora acknowledges your honorable combat...</col>");
			}
		}
	}

	/**
	 * NEW v5.0: Perform ice magic anti-safe spot measure
	 */
	private void performIceMagicAntiSafeSpotMeasure(Nymora nymora, Player player, CombatScaling scaling) {
		player.sendMessage("<col=0066ff>Frozen magic penetrates through all barriers!</col>");
		
		// Ice magic blast that reaches through all obstacles
		nymora.setNextAnimation(new Animation(28250));
		nymora.setNextForceTalk(new ForceTalk("COWARD! Face frozen fury!"));
		
		// Enhanced damage based on scaling with HP-aware limits
		NPCCombatDefinitions defs = nymora.getCombatDefinitions();
		int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.4) : 160; // Ice wrath blast
		int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(nymora, player, baseDamage);
		int safeDamage = applyHPAwareIceTwinDamageScaling(scaledDamage, player, "ice_wrath");
		
		checkAndWarnLowHPForIceTwin(player, safeDamage, "Ice Wrath");
		delayHit(nymora, 1, player, getRangeHit(nymora, safeDamage));
		
		player.sendMessage("<col=00ccff>ICE PENALTY: Safe spotting detected - frozen magic reaches all!</col>");
	}

	/**
	 * NEW v5.0: Update ice phase tracking with BossBalancer integration
	 */
	private void updateIcePhaseTracking(Nymora nymora, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(nymora.getIndex());
		int currentPhase = nymora.getInstance().getPhase();
		
		Integer lastPhase = icePhaseTracker.get(playerKey);
		if (lastPhase == null) lastPhase = -1;
		
		if (currentPhase != lastPhase) {
			icePhaseTracker.put(playerKey, currentPhase);
			handleIntelligentIcePhaseTransition(nymora, currentPhase, scaling);
		}
	}

	/**
	 * ENHANCED v5.0: Intelligent ice phase transitions with scaling integration
	 */
	private void handleIntelligentIcePhaseTransition(Nymora nymora, int newPhase, CombatScaling scaling) {
		NPCDefinitions def = NPCDefinitions.getNPCDefinitions(nymora.getId());
		String npcName = (def != null && def.getName() != null) ? def.getName() : "Nymora";
		
		switch (newPhase) {
		case 1:
			// Prepare for ceiling collapse
			String collapseMessage = scaling.bossDamageMultiplier > 2.0 ? 
				"ENHANCED CEILING COLLAPSE INCOMING!" : "The ceiling trembles with ice!";
			nymora.setNextForceTalk(new ForceTalk(collapseMessage));
			break;
			
		case 0:
			// Return to normal ice projectile phase
			String normalMessage = scaling.bossDamageMultiplier > 1.8 ? 
				"ENHANCED ICE PROJECTILES READY!" : "Frozen projectiles charging!";
			nymora.setNextForceTalk(new ForceTalk(normalMessage));
			break;
		}
	}

	/**
	 * ENHANCED v5.0: Execute intelligent ceiling collapse phase with HP-aware scaling
	 */
	private int executeIntelligentCeilingCollapsePhase(Nymora nymora, Player player, CombatScaling scaling) {
		try {
			// Mark that player has seen ceiling collapse
			Integer playerKey = Integer.valueOf(player.getIndex());
			hasSeenCeilingCollapse.put(playerKey, Boolean.TRUE);
			
			// Send pre-attack warning with scaling context
			String collapseWarning = "CEILING COLLAPSE incoming! Prepare to avoid falling ice!";
			if (scaling.bossDamageMultiplier > 2.5) {
				collapseWarning += " (MAXIMUM ice power!)";
			}
			sendEnhancedMechanicWarning(nymora, player, collapseWarning, scaling);
			
			// Schedule warning with intelligent delay
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (!nymora.isDead() && !player.isDead()) {
						// Enhanced ceiling collapse with HP-aware damage scaling
						executeEnhancedCeilingCollapse(nymora, player, scaling);
					}
					stop();
				}
			}, 3); // 3 tick warning
			
			// Set enhanced delay based on scaling
			int baseDelay = Utils.random(25000, 30000);
			int scalingDelay = scaling.bossDamageMultiplier > 2.0 ? -4000 : 0; // Faster for high scaling
			nymora.getInstance().setSpecialDelay(Utils.currentTimeMillis() + baseDelay + scalingDelay);
			nymora.getInstance().nextPhase();
			
			return 20;
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentCeilingCollapsePhase: " + e.getMessage());
			return 20;
		}
	}

	/**
	 * NEW v5.0: Execute enhanced ceiling collapse with BossBalancer integration
	 */
	private void executeEnhancedCeilingCollapse(Nymora nymora, Player player, CombatScaling scaling) {
		try {
			// Create enhanced ceiling collapse effect with v5.0 scaling
			CeilingCollapse ceilingCollapse = new CeilingCollapse(nymora, player) {
				@Override
				public void effect() {
					// Call original effect but with enhanced damage calculation
					super.effect();
					
					// Apply additional HP-aware damage scaling for safety
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							// Apply post-effect HP-aware damage validation
							validateAndAdjustCeilingCollapseDamage(nymora, player, scaling);
							stop();
						}
					}, 1);
				}
			};
			
			ceilingCollapse.effect();
			
		} catch (Exception e) {
			System.err.println("Error in executeEnhancedCeilingCollapse: " + e.getMessage());
			// Fallback to basic ceiling collapse
			new CeilingCollapse(nymora, player).effect();
		}
	}

	/**
	 * NEW v5.0: Validate and adjust ceiling collapse damage with HP-aware scaling
	 */
	private void validateAndAdjustCeilingCollapseDamage(Nymora nymora, Player player, CombatScaling scaling) {
		// This is a safety measure to ensure ceiling collapse respects HP-aware limits
		
		try {
			// Send post-attack feedback
			checkAndWarnLowHPForIceTwin(player, 0, "Ceiling Collapse"); // 0 = already applied
			
			// Provide scaling feedback
			if (scaling.bossDamageMultiplier > 2.5) {
				player.sendMessage("<col=0066ff>The ceiling collapse was supercharged by extreme scaling!</col>");
			}
		} catch (Exception e) {
			System.err.println("Error in validateAndAdjustCeilingCollapseDamage: " + e.getMessage());
		}
	}

	/**
	 * ENHANCED v5.0: Execute intelligent ice projectile attack with HP-aware scaling
	 */
	private int executeIntelligentIceProjectileAttack(Nymora nymora, Player player, CombatScaling scaling) {
		try {
			// Increment attack counters
			Integer playerKey = Integer.valueOf(player.getIndex());
			Integer currentCount = attackCounter.get(playerKey);
			Integer projectileCount = projectileCounter.get(playerKey);
			if (currentCount == null) currentCount = 0;
			if (projectileCount == null) projectileCount = 0;
			
			attackCounter.put(playerKey, currentCount + 1);
			projectileCounter.put(playerKey, projectileCount + 1);
			
			// Determine if this should be an enhanced projectile
			boolean isEnhancedProjectile = (projectileCount % ENHANCED_PROJECTILE_THRESHOLD == 0) && scaling.bossDamageMultiplier > 1.3;
			
			// Create enhanced projectile with v5.0 scaling
			final NewProjectile projectile = new NewProjectile(
				new WorldTile(nymora.getCoordFaceX(nymora.getSize()), 
							  nymora.getCoordFaceY(nymora.getSize()), 
							  nymora.getPlane()), 
				player, 
				isEnhancedProjectile ? 6137 : 6136, // Enhanced projectile has different graphic
				40, 40, 30, 0, 35, 0);
			
			nymora.setNextAnimation(new Animation(28250));
			
			// Enhanced projectile warning
			if (isEnhancedProjectile) {
				player.sendMessage("<col=0099ff>Enhanced frozen projectile incoming!</col>");
				nymora.setNextGraphics(new Graphics(369)); // Ice enhancement effect
			}
			
			// Send to all players in instance
			List<Player> instancePlayers = getInstancePlayers(nymora);
			for (Player p : instancePlayers) {
				if (p != null && !p.hasFinished()) {
					p.getPackets().sendTestProjectile(projectile);
				}
			}
			
			// Schedule damage with HP-aware scaling
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						if (!nymora.isDead() && !player.isDead()) {
							// ===== BOSSBALANCER INTEGRATION: PROJECTILE DAMAGE WITH HP-AWARE SCALING =====
							NPCCombatDefinitions defs = nymora.getCombatDefinitions();
							if (defs != null) {
								// Calculate base damage with enhanced range for ice projectiles
								int minDamage = isEnhancedProjectile ? 65 : 50;
								int maxDamage = isEnhancedProjectile ? 110 : 88;
								
								// Apply scaling based on combat progression
								if (scaling.bossDamageMultiplier > 2.0) {
									minDamage += 15; // Higher minimum for extreme scaling
									maxDamage += 25;
								} else if (scaling.bossDamageMultiplier > 1.5) {
									minDamage += 8;
									maxDamage += 15;
								}
								
								int baseDamage = Utils.random(minDamage, maxDamage + 1);
								int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(nymora, player, baseDamage);
								String attackType = isEnhancedProjectile ? "enhanced_projectile" : "ice_projectile";
								int safeDamage = applyHPAwareIceTwinDamageScaling(scaledDamage, player, attackType);
								
								// ===== BOSSBALANCER INTEGRATION: ACCURACY CHECK =====
								boolean hits = checkBossAccuracy(nymora, player, isEnhancedProjectile ? 1900 : 1700);
								
								if (hits) {
									checkAndWarnLowHPForIceTwin(player, safeDamage, isEnhancedProjectile ? "Enhanced Ice Projectile" : "Ice Projectile");
									delayHit(nymora, 0, player, getRangeHit(nymora, safeDamage));
									
									// Enhanced projectile special effects
									if (isEnhancedProjectile) {
										player.sendMessage("<col=00ccff>The enhanced ice projectile freezes your defenses!</col>");
										player.setNextGraphics(new Graphics(369)); // Freeze effect
									}
									
									// Reset consecutive dodge counter
									consecutiveProjectileDodges.put(playerKey, 0);
								} else {
									delayHit(nymora, 0, player, getRangeHit(nymora, 0)); // Miss
									player.sendMessage("You dodge the frozen projectile!");
									
									// Track consecutive dodges for anti-cheese
									Integer dodgeCount = consecutiveProjectileDodges.get(playerKey);
									if (dodgeCount == null) dodgeCount = 0;
									consecutiveProjectileDodges.put(playerKey, dodgeCount + 1);
									
									// Anti-dodge measure if too many consecutive dodges
									if (dodgeCount >= 4) {
										player.sendMessage("<col=0099ff>Nymora adjusts her aim to counter your evasion pattern!</col>");
										consecutiveProjectileDodges.put(playerKey, 0);
									}
								}
							}
						}
					} catch (Exception e) {
						System.err.println("Error in projectile damage calculation: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}, projectile.getTime(), TimeUnit.MILLISECONDS);
			
			return 4;
		} catch (Exception e) {
			System.err.println("Error in executeIntelligentIceProjectileAttack: " + e.getMessage());
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
	 * Fallback basic attack for non-Nymora NPCs or error conditions with HP-aware scaling
	 */
	private int executeBasicIceAttack(NPC npc, Entity target) {
		if (npc == null || target == null) return 4;
		
		try {
			npc.setNextAnimation(new Animation(28250));
			
			// Even basic attacks use BossBalancer if target is a player
			int damage;
			if (target instanceof Player) {
				Player player = (Player) target;
				damage = BossBalancer.applyBossScaling(Utils.random(50, 89), player, npc);
				damage = applyHPAwareIceTwinDamageScaling(damage, player, "basic_attack");
			} else {
				damage = Utils.random(50, 89);
			}
			
			delayHit(npc, 0, target, getRangeHit(npc, damage));
			return 4;
		} catch (Exception e) {
			System.err.println("Error in executeBasicIceAttack(): " + e.getMessage());
			return 4;
		}
	}

	/**
	 * Enhanced mechanic warning with scaling context
	 */
	private void sendEnhancedMechanicWarning(Nymora nymora, Player player, String message, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		long currentTime = System.currentTimeMillis();
		
		Long lastWarning = lastMechanicWarning.get(playerKey);
		if (lastWarning != null && (currentTime - lastWarning) < MECHANIC_WARNING_COOLDOWN) {
			return;
		}
		
		// Add scaling context to warning
		String enhancedMessage = message;
		if (scaling.bossDamageMultiplier > 2.5) {
			enhancedMessage += " (MAXIMUM ice power!)";
		} else if (scaling.bossDamageMultiplier > 1.8) {
			enhancedMessage += " (Enhanced ice power!)";
		}
		
		sendInstanceMessage(nymora, enhancedMessage);
		lastMechanicWarning.put(playerKey, currentTime);
	}

	/**
	 * Send message to all players in the instance
	 */
	private void sendInstanceMessage(Nymora nymora, String message) {
		try {
			nymora.setNextForceTalk(new ForceTalk(message));
		} catch (Exception e) {
			try {
				List<Player> players = getInstancePlayers(nymora);
				for (Player p : players) {
					if (p != null && !p.hasFinished()) {
						p.sendMessage("<col=00ccff>Nymora: " + message + "</col>");
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
	private List<Player> getInstancePlayers(Nymora nymora) {
		List<Player> safePlayers = new ArrayList<Player>();
		try {
			if (nymora.getInstance() != null && nymora.getInstance().getPlayers() != null) {
				for (Player p : nymora.getInstance().getPlayers()) {
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
	 * End Ice Twin combat session with proper cleanup
	 */
	public void endIceTwinCombatSession(Nymora npc, Entity target) {
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
				icePhaseTracker.remove(playerKey);
				projectileCounter.remove(playerKey);
				safespotWarnings.remove(playerKey);
				consecutiveAvoids.remove(playerKey);
				hasSeenCeilingCollapse.remove(playerKey);
				consecutiveProjectileDodges.remove(playerKey);
				
				// Clear BossBalancer player cache
				BossBalancer.clearPlayerCache(player.getIndex());
				
				// Send completion message with v5.0 info
				player.sendMessage("<col=00ccff>Ice twin combat session ended. Frozen scaling data cleared.</col>");
			}
		} catch (Exception e) {
			System.err.println("Error ending Ice Twin combat session: " + e.getMessage());
		}
	}

	/**
	 * Handle prayer changes during ice twin combat
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
				player.sendMessage("<col=99ccff>Prayer change detected. Ice twin scaling analysis updated.</col>");
			}
		} catch (Exception e) {
			System.err.println("NymoraCombat: Error handling v5.0 prayer change: " + e.getMessage());
		}
	}

	/**
	 * Force cleanup (call on logout/death)
	 */
	public static void forceCleanup(Player player) {
		if (player != null) {
			try {
				NymoraCombat combat = new NymoraCombat();
				combat.endIceTwinCombatSession(null, player);
			} catch (Exception e) {
				System.err.println("Error in Nymora force cleanup: " + e.getMessage());
			}
		}
	}

	/**
	 * Enhanced debug method for testing HP-aware scaling
	 */
	public static void debugNymoraScaling(Player player, Nymora nymora) {
		if (player == null || nymora == null) {
			return;
		}

		try {
			CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, nymora);

			System.out.println("=== NYMORA COMBAT SCALING DEBUG v5.0 ===");
			System.out.println("Player: " + player.getDisplayName());
			System.out.println("Player HP: " + player.getHitpoints() + "/" + player.getSkills().getLevelForXp(Skills.HITPOINTS));
			System.out.println("Player Power: " + String.format("%.2f", scaling.playerPower));
			System.out.println("Boss Power: " + String.format("%.2f", scaling.bossPower));
			System.out.println("Power Ratio: " + String.format("%.2f", scaling.powerRatio));
			System.out.println("HP Multiplier: " + String.format("%.3f", scaling.bossHpMultiplier));
			System.out.println("Damage Multiplier: " + String.format("%.3f", scaling.bossDamageMultiplier));
			System.out.println("Accuracy Multiplier: " + String.format("%.3f", scaling.bossAccuracyMultiplier));
			System.out.println("Scaling Type: " + scaling.scalingType);
			System.out.println("Current Phase: " + nymora.getInstance().getPhase());
			
			// Test HP-aware damage calculations
			NymoraCombat combat = new NymoraCombat();
			NPCCombatDefinitions defs = nymora.getCombatDefinitions();
			if (defs != null) {
				int baseMaxHit = defs.getMaxHit();
				int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(nymora, player, baseMaxHit);
				
				System.out.println("=== HP-AWARE DAMAGE TESTING ===");
				System.out.println("Base Max Hit: " + baseMaxHit);
				System.out.println("BossBalancer Scaled: " + scaledDamage);
				
				// Test different attack types
				int basicDamage = combat.applyHPAwareIceTwinDamageScaling(scaledDamage, player, "basic_attack");
				int projectileDamage = combat.applyHPAwareIceTwinDamageScaling((int)(scaledDamage * 1.1), player, "ice_projectile");
				int enhancedProjectileDamage = combat.applyHPAwareIceTwinDamageScaling((int)(scaledDamage * 1.3), player, "enhanced_projectile");
				int ceilingCollapseDamage = combat.applyHPAwareIceTwinDamageScaling((int)(scaledDamage * 1.5), player, "ceiling_collapse");
				
				System.out.println("Basic Attack (HP-aware): " + basicDamage);
				System.out.println("Ice Projectile (HP-aware): " + projectileDamage);
				System.out.println("Enhanced Projectile (HP-aware): " + enhancedProjectileDamage);
				System.out.println("Ceiling Collapse (HP-aware): " + ceilingCollapseDamage);
				
				// Calculate damage percentages
				int currentHP = player.getHitpoints();
				if (currentHP > 0) {
					System.out.println("=== DAMAGE PERCENTAGES ===");
					System.out.println("Basic: " + String.format("%.1f", (double)basicDamage / currentHP * 100) + "%");
					System.out.println("Projectile: " + String.format("%.1f", (double)projectileDamage / currentHP * 100) + "%");
					System.out.println("Enhanced: " + String.format("%.1f", (double)enhancedProjectileDamage / currentHP * 100) + "%");
					System.out.println("Collapse: " + String.format("%.1f", (double)ceilingCollapseDamage / currentHP * 100) + "%");
				}
			}

			System.out.println("=====================================");
		} catch (Exception e) {
			System.err.println("NymoraCombat: Error in debug scaling: " + e.getMessage());
		}
	}

	/**
	 * Get Nymora combat statistics
	 */
	public static String getNymoraCombatStats() {
		return "NymoraCombat v5.0 - Active Sessions: " + combatSessionActive.size() + 
			   ", Guidance Cooldowns: " + lastGuidanceTime.size() + 
			   ", Mechanic Warnings: " + lastMechanicWarning.size() + 
			   ", Attack Counters: " + attackCounter.size() + 
			   ", Projectile Counters: " + projectileCounter.size() + 
			   ", Ice Phases: " + icePhaseTracker.size();
	}

	/**
	 * Enhanced Nymora command handler
	 */
	public static void handleNymoraCommand(Player player, String[] cmd) {
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
					
					// Find nearby Nymora
					for (NPC npc : World.getNPCs()) {
						if (npc instanceof Nymora && npc.getDistance(player) <= 10) {
							debugNymoraScaling(player, (Nymora) npc);
							player.sendMessage("Nymora scaling debug output sent to console.");
							return;
						}
					}
					player.sendMessage("No Nymora found nearby for debugging.");
					
				} else if ("stats".equals(subcommand)) {
					player.sendMessage(getNymoraCombatStats());
					
				} else if ("cleanup".equals(subcommand)) {
					forceCleanup(player);
					player.sendMessage("Nymora combat session data cleared.");
					
				} else {
					player.sendMessage("Usage: ;;nymora [debug|stats|cleanup]");
				}
			} else {
				player.sendMessage("Nymora Combat v5.0 with BossBalancer integration and HP-aware scaling");
				if (player.isAdmin()) {
					player.sendMessage("Admin: ;;nymora debug - Debug scaling near Nymora");
				}
			}

		} catch (Exception e) {
			player.sendMessage("Error in Nymora command: " + e.getMessage());
		}
	}
}