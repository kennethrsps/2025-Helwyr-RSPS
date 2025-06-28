package com.rs.game.npc.combat.impl.gwd2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activities.instances.HelwyrInstance;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.gwd2.helwyr.CMHelwyr;
import com.rs.game.npc.gwd2.helwyr.CywirAlpha;
import com.rs.game.npc.gwd2.helwyr.Helwyr;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;
import com.rs.cache.loaders.NPCDefinitions;

/**
 * ULTIMATE Enhanced Helwyr Combat System with FULL BossBalancer v5.0 Integration
 * 
 * NEW v6.0 FEATURES:
 * - COMPLETE Boss Balancer v5.0 intelligent scaling integration
 * - HP-Aware Damage Scaling System (990-1500 HP support)
 * - Enhanced GWD2 nature mechanics with power ratio awareness
 * - Intelligent attack selection based on player power analysis
 * - Advanced mushroom, bleed, and wolf mechanics with scaling
 * - Comprehensive armor analysis and protection warnings
 * - Enhanced educational guidance system with scaling context
 * - Combat session management with power locking
 * - Performance optimized damage calculation with safety caps
 * - Nature-themed scaling messages and warnings
 * 
 * @author Zeus (Enhanced v6.0)
 * @date June 09, 2025
 * @version 6.0 - COMPLETE Boss Balancer v5.0 Integration with HP-Aware Damage Scaling
 * @note Advanced GWD2 boss with nature mechanics, bleed effects, and wolf summons
 * @original_author Tom
 * @original_date April 8, 2017
 */
public final class HelwyrCombat extends CombatScript {

	// ===== BOSS BALANCER v5.0 INTEGRATION CONSTANTS =====
	private static final int HELWYR_BOSS_TYPE = 4; // Hybrid Boss Type (melee + nature magic)
	private static final int HELWYR_DEFAULT_TIER = 7; // Elite tier by default
	
	// ===== HP-AWARE DAMAGE SCALING CONSTANTS =====
	private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.30; // Max 30% of player HP per hit (nature magic)
	private static final double CRITICAL_DAMAGE_PERCENT = 0.42;  // Max 42% for bleed attacks
	private static final double EXPLOSION_DAMAGE_PERCENT = 0.35; // Max 35% for mushroom explosions
	private static final double FRENZY_DAMAGE_PERCENT = 0.38;    // Max 38% for frenzy attacks
	private static final double CHALLENGE_DAMAGE_PERCENT = 0.45; // Max 45% for challenge mode
	private static final int MIN_PLAYER_HP = 990;
	private static final int MAX_PLAYER_HP = 1500;
	private static final int ABSOLUTE_MAX_DAMAGE = 450;          // Hard cap (30% of 1500 HP)
	private static final int MINIMUM_DAMAGE = 20;               // Minimum damage for GWD2 boss

	// ===== ENHANCED GUIDANCE SYSTEM CONSTANTS =====
	private static final long GUIDANCE_INTERVAL = 16000; // 16 seconds for GWD2
	private static final long PHASE_GUIDANCE_INTERVAL = 8000; // 8 seconds for phase changes
	private static final long SAFESPOT_CHECK_INTERVAL = 5000; // 5 seconds
	private static final long WARNING_COOLDOWN = 45000; // 45 seconds between scaling warnings
	private static final int MAX_WARNINGS_PER_FIGHT = 8; // Educational warnings limit
	
	// ===== COMBAT SESSION MANAGEMENT =====
	private static final Map<Integer, HelwyrCombatSession> combatSessions = new ConcurrentHashMap<Integer, HelwyrCombatSession>();
	private static final Map<Integer, Long> lastWarningTimeMap = new ConcurrentHashMap<Integer, Long>();
	private static final Map<Integer, Integer> warningCount = new ConcurrentHashMap<Integer, Integer>();
	private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
	private static final Map<Integer, Integer> educationalTipCount = new ConcurrentHashMap<Integer, Integer>();
	
	// ===== ENHANCED GUIDANCE SYSTEM FOR GWD2 BOSS =====
	private long lastGuidanceMessage = 0;
	private long lastPhaseGuidance = 0;
	private long lastSafespotCheck = 0;
	
	// ===== PHASE-SPECIFIC GUIDANCE TRACKING =====
	private boolean hasGivenWelcome = false;
	private boolean hasWarnedAboutNature = false;
	private boolean hasWarnedAboutBleed = false;
	private boolean hasWarnedAboutFrenzy = false;
	private boolean hasWarnedAboutHowl = false;
	private boolean hasWarnedAboutMushrooms = false;
	
	// ===== COMBAT TRACKING FOR ADVANCED GUIDANCE =====
	private int totalAttackCount = 0;
	private int consecutiveMeleeAttempts = 0;
	private boolean lastAttackConnected = true;
	private int currentPhaseAttacks = 0;
	
	// ===== SCALING-AWARE FORCE TALK MESSAGES =====
	private static final String[] NATURE_AWAKENING_MESSAGES = {
		"Nature, lend me your aid!",
		"The forest speaks through me!",
		"Ancient growth empowers my strikes!",
		"Feel the wrath of the wilderness!"
	};
	
	private static final String[] BLEED_MESSAGES = {
		"YOU. WILL. BLEED!",
		"Your blood feeds the earth!",
		"The hunt ends here!",
		"Nature demands your sacrifice!"
	};
	
	private static final String[] FRENZY_MESSAGES = {
		"You cannot escape me. Aaaargh!",
		"The beast within awakens!",
		"Witness primal fury unleashed!",
		"None escape the wild hunt!"
	};
	
	private static final String[] OVERPOWERED_PLAYER_MESSAGES = {
		"Your power rivals the ancient guardians!",
		"Nature's fury must match your strength!",
		"The forest responds to your might!",
		"Enhanced growth to challenge you!"
	};
	
	private static final String[] UNDERPOWERED_PLAYER_MESSAGES = {
		"Nature shows mercy to the unprepared...",
		"The forest restrains its true power.",
		"Ancient wisdom counsels patience.",
		"Young sapling, grow stronger first."
	};

	/**
	 * Enhanced Helwyr Combat Session for BossBalancer integration
	 */
	private static class HelwyrCombatSession {
		public final int playerId;
		public final int helwyrId;
		public volatile double lockedPlayerPower;
		public volatile double lockedHelwyrPower;
		public volatile double powerRatio;
		public final long sessionStart;
		public volatile boolean powerLocked;
		public volatile String currentScalingType;
		public volatile int currentPhase;
		public volatile boolean hasFullArmor;
		public volatile int aliveWolves;

		public HelwyrCombatSession(int playerId, int helwyrId, double playerPower, double helwyrPower) {
			this.playerId = playerId;
			this.helwyrId = helwyrId;
			this.lockedPlayerPower = playerPower;
			this.lockedHelwyrPower = helwyrPower;
			this.powerRatio = playerPower / helwyrPower;
			this.sessionStart = System.currentTimeMillis();
			this.powerLocked = false;
			this.currentScalingType = "UNKNOWN";
			this.currentPhase = 0;
			this.hasFullArmor = false;
			this.aliveWolves = 0;
		}
	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 22438, 22440 };
	}
	
	@Override
	public int attack(NPC npc, Entity target) {
		Helwyr helwyr = (Helwyr) npc;
		
		// Enhanced safety validation
		if (!isValidHelwyrCombatState(helwyr, target)) {
			return 4;
		}
		
		final Player player = target instanceof Player ? (Player) target : null;
		if (player == null) {
			return executeBasicHelwyrAttack(npc, target);
		}
		
		// ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
		
		// Initialize Helwyr combat session
		initializeHelwyrCombatSession(player, helwyr);
		
		// Get INTELLIGENT combat scaling v5.0
		CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, helwyr);
		
		// Enhanced guidance system with intelligent scaling awareness
		provideIntelligentHelwyrGuidance(helwyr, player, scaling);
		
		// Monitor scaling changes during combat
		monitorHelwyrScalingChanges(player, scaling);
		
		// Check and handle Helwyr safespot with scaling awareness
		checkAndHandleHelwyrSafespot(helwyr, player, scaling);
		
		// Increment attack counters
		totalAttackCount++;
		currentPhaseAttacks++;
		
		// Challenge mode handling with BossBalancer integration
		if (npc.getId() == 22440)
			return enhancedChallengeAttack((CMHelwyr) helwyr, target, scaling);
		
		// Enhanced normal mode with Boss Balancer integration
		final int phase = helwyr.getPhase();
		helwyr.nextPhase();
		
		if (helwyr.getPhase() < 0 || helwyr.getPhase() > 15)
			helwyr.setPhase(0);
			
		// Reset phase attack counter when phase changes
		if (phase != helwyr.getPhase()) {
			currentPhaseAttacks = 0;
		}
		
		switch (phase) {
		case 0:
			return enhancedNature(helwyr, target, scaling);
		case 4:
			return enhancedBleed(helwyr, target, scaling);
		case 8:
			return enhancedFrenzy(helwyr, target, scaling);
		case 12:
			if (helwyr.getInstance().getAliveWolves() >= 4) {
				helwyr.setPhase(1);
				return enhancedNature(helwyr, target, scaling);
			}
			return enhancedHowl(helwyr, target, scaling);
		default:
			return enhancedBite(helwyr, target, scaling);
		}
	}
	
	/**
	 * NEW v6.0: Initialize Helwyr combat session using BossBalancer v5.0
	 */
	private void initializeHelwyrCombatSession(Player player, Helwyr helwyr) {
		Integer sessionKey = Integer.valueOf(player.getIndex());
		
		if (!combatSessions.containsKey(sessionKey)) {
			// Start BossBalancer v5.0 combat session
			BossBalancer.startCombatSession(player, helwyr);
			
			// Calculate powers for session
			double playerPower = BossBalancer.calculateActualPlayerPower(player);
			double helwyrPower = calculateHelwyrPower(helwyr);
			
			// Create session
			HelwyrCombatSession session = new HelwyrCombatSession(
				player.getIndex(), helwyr.getIndex(), playerPower, helwyrPower);
			
			combatSessions.put(sessionKey, session);
			lastScalingType.put(sessionKey, "UNKNOWN");
			educationalTipCount.put(sessionKey, Integer.valueOf(0));
			warningCount.put(sessionKey, Integer.valueOf(0));
			
			// Send v5.0 enhanced Helwyr combat message
			CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, helwyr);
			String welcomeMsg = getIntelligentHelwyrWelcomeMessage(scaling, helwyr);
			player.sendMessage(welcomeMsg);
			
			// Perform initial armor analysis for Helwyr combat
			performInitialHelwyrArmorAnalysis(player);
			
			// Auto-configure boss if not configured
			if (!BossBalancer.isBossConfigured(helwyr.getId())) {
				BossBalancer.autoConfigureBoss(helwyr.getId(), HELWYR_DEFAULT_TIER, 
											  HELWYR_BOSS_TYPE, "HelwyrCombat", false);
			}
		}
	}
	
	/**
	 * NEW v6.0: Calculate Helwyr power based on phase and tier
	 */
	private double calculateHelwyrPower(Helwyr helwyr) {
		if (helwyr == null) {
			return 1.0;
		}

		try {
			int helwyrTier = getHelwyrBossTier(helwyr);
			double basePower = Math.pow(helwyrTier, 1.4); // Same as BossBalancer formula
			
			// Phase modifier (Helwyr phases are different from dragons)
			int phase = helwyr.getPhase();
			double phaseMultiplier = 1.0 + (phase * 0.05); // 5% increase per phase cycle
			
			// GWD2 boss modifier (nature magic specialists)
			double gwd2Modifier = 1.15; // 15% bonus for GWD2 status
			
			// Challenge mode modifier
			double challengeModifier = helwyr.getId() == 22440 ? 1.3 : 1.0; // 30% bonus for challenge mode
			
			return basePower * phaseMultiplier * gwd2Modifier * challengeModifier;

		} catch (Exception e) {
			return Math.pow(HELWYR_DEFAULT_TIER, 1.4);
		}
	}
	
	/**
	 * NEW v6.0: Perform initial Helwyr armor analysis
	 */
	private void performInitialHelwyrArmorAnalysis(Player player) {
		try {
			// Use BossBalancer v5.0 armor analysis
			BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
			
			if (!armorResult.hasFullArmor) {
				player.sendMessage("<col=00ff00>Helwyr Analysis: Exposed flesh detected. Nature's fury seeks unprotected areas!</col>");
				player.sendMessage("<col=FF6600>WARNING: Missing armor will result in significantly increased nature damage!</col>");
			} else {
				double reductionPercent = armorResult.damageReduction * 100;
				player.sendMessage("<col=32CD32>Helwyr Analysis: Full protection detected (" + 
								 String.format("%.1f", reductionPercent) + "% nature resistance). Natural magic still penetrates...</col>");
			}
		} catch (Exception e) {
			// Ignore armor analysis errors
		}
	}
	
	/**
	 * NEW v6.0: Generate intelligent Helwyr welcome message
	 */
	private String getIntelligentHelwyrWelcomeMessage(CombatScaling scaling, Helwyr helwyr) {
		StringBuilder message = new StringBuilder();
		
		message.append("<col=00ff00>Helwyr's eyes glow with nature's power, analyzing your natural defenses (v5.0).</col>");
		
		// Add v5.0 intelligent scaling information for Helwyr
		if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
			int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
			message.append(" <col=32CD32>[Nature's fury enhanced: +").append(diffIncrease).append("% natural intensity]</col>");
		} else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
			int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
			message.append(" <col=90EE90>[Nature's restraint: -").append(assistance).append("% attack damage]</col>");
		} else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
			message.append(" <col=DDA0DD>[Natural resistance scaling active]</col>");
		} else if (scaling.scalingType.contains("FULL_ARMOR")) {
			message.append(" <col=708090>[Full natural protection detected]</col>");
		}
		
		return message.toString();
	}
	
	/**
	 * NEW v6.0: Apply HP-aware Helwyr damage scaling
	 */
	private int applyHPAwareHelwyrDamageScaling(int scaledDamage, Player player, String attackType) {
		if (player == null) {
			return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
		}
		
		try {
			int currentHP = player.getHitpoints();
			int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
			
			// Use current HP for calculation (Helwyr nature attacks are balanced)
			int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
			
			// Determine damage cap based on Helwyr attack type
			double damagePercent;
			switch (attackType.toLowerCase()) {
				case "challenge_mode":
				case "enhanced_explosion":
					damagePercent = CHALLENGE_DAMAGE_PERCENT;
					break;
				case "bleed_attack":
				case "critical_bleed":
					damagePercent = CRITICAL_DAMAGE_PERCENT;
					break;
				case "frenzy_attack":
				case "spinning_frenzy":
					damagePercent = FRENZY_DAMAGE_PERCENT;
					break;
				case "mushroom_explosion":
				case "nature_explosion":
					damagePercent = EXPLOSION_DAMAGE_PERCENT;
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
			
			// Additional safety check - never deal more than 70% of current HP for Helwyr
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
	 * NEW v6.0: Send HP warning if player is in danger from Helwyr attacks
	 */
	private void checkAndWarnLowHPForHelwyr(Player player, int incomingDamage) {
		if (player == null) return;
		
		try {
			int currentHP = player.getHitpoints();
			
			// Warn if incoming nature damage is significant relative to current HP
			if (currentHP > 0) {
				double damagePercent = (double)incomingDamage / currentHP;
				
				if (damagePercent >= 0.60) {
					player.sendMessage("<col=00ff00>NATURE WARNING: This attack will deal " + incomingDamage + 
									 " damage! (" + currentHP + " HP remaining)</col>");
				} else if (damagePercent >= 0.40) {
					player.sendMessage("<col=32CD32>HELWYR WARNING: Heavy nature damage incoming (" + incomingDamage + 
									 ")! Consider healing (" + currentHP + " HP)</col>");
				}
			}
		} catch (Exception e) {
			// Ignore warning errors
		}
	}
	
	/**
	 * ENHANCED v6.0: Intelligent Helwyr guidance with power-based scaling awareness
	 */
	private void provideIntelligentHelwyrGuidance(Helwyr helwyr, Player player, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		long currentTime = System.currentTimeMillis();
		
		// Check if we should provide guidance
		Long lastWarningTimeLong = lastWarningTimeMap.get(playerKey);
		if (lastWarningTimeLong != null && (currentTime - lastWarningTimeLong.longValue()) < WARNING_COOLDOWN) {
			return; // Still in cooldown
		}
		
		Integer currentCount = warningCount.get(playerKey);
		if (currentCount == null) currentCount = Integer.valueOf(0);
		if (currentCount.intValue() >= MAX_WARNINGS_PER_FIGHT) {
			return; // Max warnings reached
		}
		
		// Initial GWD2 encounter guidance
		if (!hasGivenWelcome) {
			hasGivenWelcome = true;
			announceToInstance(helwyr, "<col=00ff00>[HELWYR ENCOUNTER] The Serenic guardian awakens!</col>");
			announceToInstance(helwyr, "<col=ffff00>[GWD2 Guide] NATURE BOSS: Mushrooms, bleeds, frenzy attacks, and wolf summons!</col>");
			announceToInstance(helwyr, "<col=9900ff>[Strategy] Phase-based mechanics, positioning crucial, watch for mushroom spawns!</col>");
			lastGuidanceMessage = currentTime;
			
			// Add scaling-specific opening advice
			if (scaling.scalingType.contains("ANTI_FARMING")) {
				announceToInstance(helwyr, "<col=FF6600>[Power Analysis]: Your equipment is overpowered! Helwyr enhanced by " + 
										 (int)((scaling.bossDamageMultiplier - 1.0) * 100) + "% to maintain challenge!</col>");
			} else if (scaling.scalingType.contains("UNDERPOWERED")) {
				announceToInstance(helwyr, "<col=32CD32>[Power Analysis]: Helwyr showing restraint (" + 
										 (int)((1.0 - scaling.bossDamageMultiplier) * 100) + "% reduced) due to your current preparation!</col>");
			}
			return;
		}
		
		// Phase-specific warnings
		providePhaseSpecificHelwyrGuidance(helwyr, player, scaling);
		
		// Educational guidance (separate from warnings)
		if (Utils.random(4) == 0) {
			sendEducationalHelwyrGuidance(helwyr, player, scaling);
		}
		
		// Advanced periodic guidance
		if (currentTime - lastGuidanceMessage >= GUIDANCE_INTERVAL) {
			lastGuidanceMessage = currentTime;
			provideAdvancedGWD2Guidance(helwyr, player, scaling);
		}
	}
	
	/**
	 * Provide phase-specific Helwyr guidance with scaling awareness
	 */
	private void providePhaseSpecificHelwyrGuidance(Helwyr helwyr, Player player, CombatScaling scaling) {
		int currentPhase = helwyr.getPhase();
		String scalingNote = scaling.bossDamageMultiplier > 1.5 ? " (enhanced by scaling)" : "";
		
		// Nature attack guidance
		if (currentPhase == 0 && !hasWarnedAboutNature) {
			hasWarnedAboutNature = true;
			announceToInstance(helwyr, "<col=00ff00>[Nature Alert] Mushroom spawns incoming" + scalingNote + "! Stay mobile!</col>");
			player.sendMessage("<col=ffff00>Nature Guide: Mushrooms explode after delay - don't stand near them!</col>");
		}
		
		// Bleed attack guidance
		if (currentPhase == 4 && !hasWarnedAboutBleed) {
			hasWarnedAboutBleed = true;
			announceToInstance(helwyr, "<col=cc0000>[Bleed Alert] YOU. WILL. BLEED! AoE attack with DoT effect" + scalingNote + "!</col>");
			player.sendMessage("<col=ff0000>Bleed Guide: Stay away from targeted area, brings ongoing damage!</col>");
		}
		
		// Frenzy attack guidance
		if (currentPhase == 8 && !hasWarnedAboutFrenzy) {
			hasWarnedAboutFrenzy = true;
			announceToInstance(helwyr, "<col=ff6600>[Frenzy Alert] Spinning cleave attack" + scalingNote + "! Multiple hits!</col>");
			player.sendMessage("<col=ffff00>Frenzy Guide: Helwyr spins and attacks in multiple directions!</col>");
		}
		
		// Howl attack guidance
		if (currentPhase == 12 && !hasWarnedAboutHowl) {
			hasWarnedAboutHowl = true;
			announceToInstance(helwyr, "<col=9900ff>[Howl Alert] Wolf summons incoming" + scalingNote + "! Kill wolves quickly!</col>");
			player.sendMessage("<col=ffff00>Howl Guide: Helwyr summons Cywir Alphas - eliminate them fast!</col>");
		}
		
		// Challenge mode mushroom explosion
		if (helwyr instanceof CMHelwyr && currentPhase == 15 && !hasWarnedAboutMushrooms) {
			hasWarnedAboutMushrooms = true;
			announceToInstance(helwyr, "<col=ff0000>[CHALLENGE EXPLOSION] Massive mushroom detonation sequence" + scalingNote + "!</col>");
			player.sendMessage("<col=ff0000>Challenge Guide: All mushrooms explode in sequence - constant movement required!</col>");
		}
	}
	
	/**
	 * Enhanced educational guidance system for Helwyr
	 */
	private void sendEducationalHelwyrGuidance(Helwyr helwyr, Player player, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		Integer tipCount = educationalTipCount.get(playerKey);
		if (tipCount == null) tipCount = Integer.valueOf(0);
		
		if (tipCount.intValue() >= MAX_WARNINGS_PER_FIGHT) {
			return; // Prevent spam
		}
		
		String[] wisdomMessages = { 
			"Learn from nature's lessons, young warrior.", 
			"Each strike teaches forest wisdom.",
			"Observe the patterns in natural magic.", 
			"Armor serves well against nature's wrath.",
			"Antifire protection guards against wolf breath.", 
			"Distance controls the woodland battlefield.",
			"Close combat with beasts requires courage.", 
			"Preparation separates hunters from prey.",
			"Every guardian has natural weaknesses.",
			"The forest itself guides the worthy."
		};

		// Safe array access with bounds checking
		int randomIndex = Math.abs(Utils.random(wisdomMessages.length)) % wisdomMessages.length;
		String wisdom = wisdomMessages[randomIndex];
		
		// Add scaling context to educational messages
		if (scaling != null && scaling.bossDamageMultiplier > 1.5) {
			wisdom += " [This guardian is enhanced due to your natural prowess.]";
		} else if (scaling != null && scaling.bossDamageMultiplier < 0.9) {
			wisdom += " [This guardian shows restraint due to your current natural preparation.]";
		}
		
		player.sendMessage(wisdom, true);

		try {
			helwyr.setNextForceTalk(new ForceTalk(wisdom));
		} catch (Exception e) {
			// ForceTalk failed silently
		}
		
		educationalTipCount.put(playerKey, Integer.valueOf(tipCount.intValue() + 1));
	}
	
	/**
	 * Provide advanced GWD2-level guidance with scaling awareness
	 */
	private void provideAdvancedGWD2Guidance(Helwyr helwyr, Player player, CombatScaling scaling) {
		double hpPercentage = (double) helwyr.getHitpoints() / helwyr.getMaxHitpoints();
		String intensityNote = scaling.bossDamageMultiplier > 1.5 ? " (scaling enhanced)" : "";
		
		if (hpPercentage < 0.25) {
			announceToInstance(helwyr, "<col=ff0000>[Critical Phase] Helwyr below 25% HP - increased aggression" + intensityNote + "!</col>");
		} else if (hpPercentage < 0.5) {
			announceToInstance(helwyr, "<col=ffff00>[High Intensity] Helwyr below 50% HP - more frequent specials" + intensityNote + "!</col>");
		}
		
		// Tier-specific advanced guidance
		int helwyrTier = getHelwyrBossTier(helwyr);
		if (helwyrTier >= 7) {
			String[] advancedTips = {
				"HIGH TIER HELWYR: Enhanced damage and faster attacks!",
				"Use area-of-effect abilities to clear mushrooms quickly!",
				"Anticipate bleed effects - have healing ready!",
				"Position near walls to limit frenzy movement options!",
				"Focus DPS during bite phases for maximum efficiency!"
			};
			String tip = advancedTips[Utils.random(advancedTips.length)];
			if (scaling.bossDamageMultiplier > 1.5) {
				tip += " [Scaling enhanced mechanics active!]";
			}
			announceToInstance(helwyr, "<col=00ff00>[Advanced Strategy] " + tip + "</col>");
		}
	}
	
	/**
	 * NEW v6.0: Monitor Helwyr scaling changes during combat
	 */
	private void monitorHelwyrScalingChanges(Player player, CombatScaling scaling) {
		Integer playerKey = Integer.valueOf(player.getIndex());
		String currentScalingType = scaling.scalingType;
		String lastType = lastScalingType.get(playerKey);
		
		// Check if scaling type changed (prayer activation, gear swap, etc.)
		if (lastType != null && !lastType.equals(currentScalingType)) {
			// Scaling changed - notify player
			String changeMessage = getHelwyrScalingChangeMessage(lastType, currentScalingType, scaling);
			if (changeMessage != null) {
				player.sendMessage(changeMessage);
			}
		}
		
		lastScalingType.put(playerKey, currentScalingType);
	}
	
	/**
	 * NEW v6.0: Get Helwyr scaling change message
	 */
	private String getHelwyrScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
		if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
			return "<col=32CD32>Helwyr Update: Natural balance improved! Guardian restraint reduced.</col>";
		} else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
			return "<col=00ff00>Helwyr Update: Nature's fury now active due to increased natural power!</col>";
		} else if (newType.contains("WITH_ABSORPTION")) {
			return "<col=DDA0DD>Helwyr Update: Natural resistance bonuses detected and factored into scaling!</col>";
		} else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
			return "<col=32CD32>Helwyr Update: Natural protection restored! Guardian attack scaling normalized.</col>";
		}
		
		return null;
	}
	
	/**
	 * Enhanced Challenge Mode with Boss Balancer integration
	 */
	private final int enhancedChallengeAttack(final CMHelwyr helwyr, final Entity target, CombatScaling scaling) {
		// Challenge mode guidance with scaling awareness
		String intensityNote = scaling.bossDamageMultiplier > 2.0 ? " (MAXIMUM INTENSITY)" : " (Enhanced mechanics)";
		announceToInstance(helwyr, "<col=ff0000>[CHALLENGE MODE]" + intensityNote + " and damage!</col>");
		
		final int phase = helwyr.getPhase();
		helwyr.nextPhase();
		
		if (helwyr.getPhase() < 0 || helwyr.getPhase() > 18)
			helwyr.setPhase(0);
			
		switch (phase) {
		case 0:
			return enhancedNature(helwyr, target, scaling);
		case 4:
			return enhancedBleed(helwyr, target, scaling);
		case 8:
			return enhancedFrenzy(helwyr, target, scaling);
		case 12:
			return helwyr.getHowlStage() >= 2 ? enhancedBleed(helwyr, target, scaling) : enhancedHowl(helwyr, target, scaling);
		case 15:
			return enhancedMushroomExplosion(helwyr, target, scaling);
		default:
			return enhancedBite(helwyr, target, scaling);
		}
	}
	
	/**
	 * Enhanced bite attack with Boss Balancer and HP-aware scaling
	 */
	private final int enhancedBite(Helwyr npc, Entity target, CombatScaling scaling) {
		npc.setNextAnimation(new Animation(28205));
		
		// Get Boss Balancer enhanced damage
		int baseMaxHit = getEnhancedHelwyrMaxHit(npc);
		int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, (Player) target, baseMaxHit);
		
		// Apply HP-aware damage scaling if target is player
		if (target instanceof Player) {
			scaledDamage = applyHPAwareHelwyrDamageScaling(scaledDamage, (Player) target, "bite_attack");
		}
		
		// Check if attack connects for safespot detection
		boolean canHit = target.withinDistance(npc, 2);
		if (canHit) {
			delayHit(npc, 0, target, getMeleeHit(npc, scaledDamage));
			lastAttackConnected = true;
			consecutiveMeleeAttempts = 0;
		} else {
			lastAttackConnected = false;
			consecutiveMeleeAttempts++;
		}
		
		// Occasional guidance for basic attack
		if (Utils.random(8) == 0 && target instanceof Player) {
			Player player = (Player) target;
			String scalingNote = scaling.bossDamageMultiplier > 1.3 ? " (enhanced)" : "";
			player.sendMessage("<col=ffff00>Bite Guide: Basic melee attack" + scalingNote + " - stay close for consistent DPS!</col>");
		}
		
		return 4;
	}
	
	/**
	 * Enhanced nature attack with Boss Balancer and HP-aware scaling
	 */
	private final int enhancedNature(Helwyr helwyr, Entity target, CombatScaling scaling) {
		// Select force talk based on scaling
		String[] messages = scaling.scalingType.contains("ANTI_FARMING") ? OVERPOWERED_PLAYER_MESSAGES :
						   scaling.scalingType.contains("UNDERPOWERED") ? UNDERPOWERED_PLAYER_MESSAGES :
						   NATURE_AWAKENING_MESSAGES;
		
		String selectedMessage = messages[Utils.random(messages.length)];
		helwyr.setNextForceTalk(new ForceTalk(selectedMessage));
		helwyr.setNextAnimation(new Animation(28207));
		
		String scalingNote = scaling.bossDamageMultiplier > 1.5 ? " (scaling enhanced)" : "";
		announceToInstance(helwyr, "<col=00ff00>[Nature Alert] Mushroom spawns incoming" + scalingNote + "! Prepare for explosions!</col>");
		
		final int amount = helwyr.getId() == 22440 ? 6 : 3;
		final WorldTile[] tiles = new WorldTile[amount];
		
		for (int i = 0; i < amount; i++) {
			if (helwyr.getInstance().getAvailableTiles().size() == 0)
				break;
			WorldTile tile = helwyr.getInstance().getAvailableTiles().get(Utils.random(helwyr.getInstance().getAvailableTiles().size()));
			helwyr.getInstance().addMushroom(tiles[i] = tile);
			World.sendProjectile(helwyr, tile, 6122, 70, 10, 50, 2, 10, 0);
		}
		
		WorldTasksManager.schedule(new WorldTask() {
			private final WorldObject[] objects = new WorldObject[amount];
			private boolean second;
			
			@Override
			public void run() {
				if (helwyr.isDead() || helwyr.hasFinished()) {
					stop();
					return;
				}
				
				if (!second) {
					for (int i = 0; i < amount; i++) {
						if (tiles[i] == null)
							continue;
						for (Player p : helwyr.getInstance().getPlayers()) {
							p.getPackets().sendGraphics(new Graphics(6124), tiles[i]);
							// Enhanced mushroom guidance
							if (p.getDistance(tiles[i]) < 3) {
								String warningNote = scaling.bossDamageMultiplier > 1.5 ? " (enhanced explosion)" : "";
								p.sendMessage("<col=ffff00>Nature Guide: Mushroom nearby" + warningNote + " - move away before explosion!</col>");
							}
						}
						World.spawnObject(objects[i] = new WorldObject(101900, 10, 3, tiles[i]));
					}
				} else {
					for (int i = 0; i < amount; i++) {
						if (tiles[i] == null || objects[i] == null)
							continue;
						
						// Enhanced explosion damage with Boss Balancer and HP-aware scaling
						for (Player p : helwyr.getInstance().getPlayers()) {
							p.getPackets().sendGraphics(new Graphics(6125), tiles[i]);
							if (p.getDistance(tiles[i]) < 2) {
								int baseExplosionDamage = getEnhancedHelwyrMaxHit(helwyr);
								int scaledExplosionDamage = BossBalancer.calculateNPCDamageToPlayer(helwyr, p, (int)(baseExplosionDamage * 0.8));
								int safeDamage = applyHPAwareHelwyrDamageScaling(scaledExplosionDamage, p, "mushroom_explosion");
								checkAndWarnLowHPForHelwyr(p, safeDamage);
								
								p.applyHit(new Hit(helwyr, safeDamage, HitLook.MAGIC_DAMAGE));
							}
						}
						
						helwyr.getInstance().removeMushroom(tiles[i]);
						World.removeObject(objects[i]);
					}
					stop();
				}
				second = true;
			}
		}, 0, 97);
		
		lastAttackConnected = true; // Nature attack always "connects"
		return 4;
	}
	
	/**
	 * Enhanced bleed attack with Boss Balancer and HP-aware scaling
	 */
	private final int enhancedBleed(Helwyr npc, Entity target, CombatScaling scaling) {
		// Select force talk based on scaling
		String[] messages = scaling.scalingType.contains("ANTI_FARMING") ? OVERPOWERED_PLAYER_MESSAGES : BLEED_MESSAGES;
		String selectedMessage = messages[Utils.random(messages.length)];
		npc.setNextForceTalk(new ForceTalk(selectedMessage));
		
		npc.setNextAnimation(new Animation(28214));
		npc.resetWalkSteps();
		npc.setCannotMove(true);
		npc.setNextGraphics(new Graphics(6126));
		
		String scalingNote = scaling.bossDamageMultiplier > 1.5 ? " (scaling enhanced)" : "";
		announceToInstance(npc, "<col=cc0000>[Bleed Alert] AoE bleed attack" + scalingNote + "! Stay away from target area!</col>");
		
		final WorldTile bleedTile = new WorldTile(target.getX(), target.getY(), 1);
		
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;
			
			@Override
			public void run() {
				if (ticks == 3) {
					npc.getInstance().getPlayers().forEach(p -> {
						if (p.getDistance(bleedTile) < 2) {
							// Enhanced bleed damage with Boss Balancer and HP-aware scaling
							int baseBleedDamage = getEnhancedHelwyrMaxHit((Helwyr) npc);
							double multiplier = npc.getId() == 22440 ? 1.5 : 1.0; // 150% for challenge mode
							int scaledBleedDamage = BossBalancer.calculateNPCDamageToPlayer(npc, p, (int)(baseBleedDamage * multiplier));
							int safeDamage = applyHPAwareHelwyrDamageScaling(scaledBleedDamage, p, "bleed_attack");
							checkAndWarnLowHPForHelwyr(p, safeDamage);
							
							p.applyHit(new Hit(target, safeDamage, HitLook.MELEE_DAMAGE));
							addEnhancedBleedEffect((Helwyr) npc, p, true, scaling);
							
							if (npc.getId() == 22440) {
								p.sendMessage("Helwyr's enhanced claws injure you severely.");
								p.getPrayer().closeAllPrayers();
								p.setPrayerDelay(6000); // Enhanced prayer delay
							}
							
							// Bleed guidance
							String intensityNote = scaling.bossDamageMultiplier > 1.5 ? " (enhanced)" : "";
							p.sendMessage("<col=cc0000>Bleed Guide: You're bleeding" + intensityNote + "! Damage over time effect active!</col>");
						}
						
						if (p.getFamiliar() != null && p.getFamiliar().getDistance(bleedTile) < 2 && p.getFamiliar().getDefinitions().hasAttackOption()) {
							int baseBleedDamage = getEnhancedHelwyrMaxHit((Helwyr) npc);
							double multiplier = npc.getId() == 22440 ? 1.5 : 1.0;
							int scaledBleedDamage = BossBalancer.calculateNPCDamageToPlayer(npc, p, (int)(baseBleedDamage * multiplier));
							int safeDamage = applyHPAwareHelwyrDamageScaling(scaledBleedDamage, p, "bleed_attack");
							p.getFamiliar().applyHit(new Hit(npc, safeDamage, HitLook.MELEE_DAMAGE));
						}
					});
				} else if (ticks == 4) {
					npc.setCannotMove(false);
					npc.setTarget(target);
					stop();
				}
				ticks++;
			}
		}, 0, 0);
		
		lastAttackConnected = true; // Bleed attack always "connects"
		return 7;
	}
	
	/**
	 * Enhanced bleed effect with Boss Balancer and scaling awareness
	 */
	private final void addEnhancedBleedEffect(Helwyr npc, Player p, final boolean bleedAttack, CombatScaling scaling) {
		final int bleed = p.getTemporaryAttributtes().get("bleed") == null ? 0 : (int) p.getTemporaryAttributtes().get("bleed");
		if (!bleedAttack && bleed == 0)
			return;
			
		// Enhanced bleed scaling with Boss Balancer awareness
		int bleedIncrement = npc.getId() == 22440 ? 15 : 8; // Enhanced for challenge mode
		int bleedCap = npc.getId() == 22440 ? 75 : 40; // Enhanced caps
		
		// Apply scaling to bleed effect
		if (scaling.bossDamageMultiplier > 1.5) {
			bleedIncrement = (int)(bleedIncrement * 1.3); // 30% increase for high scaling
			bleedCap = (int)(bleedCap * 1.2); // 20% increase in cap
		}
		
		p.getTemporaryAttributtes().put("bleed", bleed + bleedIncrement > bleedCap ? bleedCap : bleed + bleedIncrement);
		p.getTemporaryAttributtes().put("bleedTime", Utils.currentTimeMillis());
		
		if (bleed != 0) {
			p.getPackets().sendPlayerMessage(1, 15263739, "Helwyr's continued attacks cause you to lose even more blood!", true);
		} else {
			String intensityNote = scaling.bossDamageMultiplier > 1.5 ? " enhanced" : "";
			p.getPackets().sendPlayerMessage(1, 15263739, "Helwyr's" + intensityNote + " attacks cause severe bleeding.", true);
		}
	}
	
	/**
	 * Enhanced frenzy attack with Boss Balancer and HP-aware scaling
	 */
	private final int enhancedFrenzy(Helwyr npc, Entity target, CombatScaling scaling) {
		// Select force talk based on scaling
		String[] messages = scaling.scalingType.contains("ANTI_FARMING") ? OVERPOWERED_PLAYER_MESSAGES : FRENZY_MESSAGES;
		String selectedMessage = messages[Utils.random(messages.length)];
		npc.setNextForceTalk(new ForceTalk(selectedMessage));
		
		npc.setNextAnimation(new Animation(28215));
		npc.resetWalkSteps();
		npc.getCombat().reset();
		npc.setCannotMove(true);
		
		String scalingNote = scaling.bossDamageMultiplier > 1.5 ? " (scaling enhanced)" : "";
		announceToInstance(npc, "<col=ff6600>[Frenzy Alert] Spinning cleave attack" + scalingNote + "! Multiple directional hits!</col>");
		
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;
			private int direction = (int) (Math.round(npc.getDirection() / 45.51) / 45);
			private final List<Player> players = new ArrayList<Player>();
			
			@Override
			public void run() {
				if (ticks > 0)
					direction = (direction - 2 < 0 ? (8 + (direction - 2)) : direction - 2);
					
				final byte[] dirs = Utils.DIRS[direction];
				final WorldTile tile = ticks == 0 ? new WorldTile(target) : 
					new WorldTile(npc.getCoordFaceX(npc.getSize()) + (dirs[0] * 3), 
								 npc.getCoordFaceY(npc.getSize()) + (dirs[1] * 3), npc.getPlane());
				
				npc.getInstance().getPlayers().forEach(p -> {
					if (p.getDistance(tile) < 3) {
						// Enhanced frenzy damage with Boss Balancer and HP-aware scaling
						int baseFrenzyDamage = getEnhancedHelwyrMaxHit((Helwyr) npc);
						double multiplier = npc.getId() == 22440 ? 1.3 : 0.8; // Enhanced for challenge mode
						int scaledFrenzyDamage = BossBalancer.calculateNPCDamageToPlayer(npc, p, (int)(baseFrenzyDamage * multiplier));
						int safeDamage = applyHPAwareHelwyrDamageScaling(scaledFrenzyDamage, p, "frenzy_attack");
						checkAndWarnLowHPForHelwyr(p, safeDamage);
						
						delayHit(npc, 0, target, getMeleeHit(npc, safeDamage));
						
						if (!players.contains(p)) {
							addEnhancedBleedEffect((Helwyr) npc, p, false, scaling);
							players.add(p);
							String intensityNote = scaling.bossDamageMultiplier > 1.5 ? " enhanced" : "";
							p.sendMessage("<col=ff6600>Frenzy Guide: Hit by" + intensityNote + " spinning attack - bleed effect applied!</col>");
						}
					}
					
					if (p.getFamiliar() != null && p.getFamiliar().getDistance(tile) < 3 && 
						p.getFamiliar().getDefinitions().hasAttackOption()) {
						int baseFrenzyDamage = getEnhancedHelwyrMaxHit((Helwyr) npc);
						double multiplier = npc.getId() == 22440 ? 1.3 : 0.8;
						int scaledFrenzyDamage = BossBalancer.calculateNPCDamageToPlayer(npc, p, (int)(baseFrenzyDamage * multiplier));
						int safeDamage = applyHPAwareHelwyrDamageScaling(scaledFrenzyDamage, p, "frenzy_attack");
						p.getFamiliar().applyHit(new Hit(npc, safeDamage, HitLook.MELEE_DAMAGE));
					}
				});
				
				if (ticks++ == 4) {
					npc.setCannotMove(false);
					npc.getCombat().setTarget(target);
					stop();
					return;
				}
			}
		}, 0, 1);
		
		lastAttackConnected = true; // Frenzy attack always "connects"
		return 12;
	}
	
	/**
	 * Enhanced howl attack with Boss Balancer and HP-aware scaling
	 */
	private final int enhancedHowl(Helwyr npc, Entity target, CombatScaling scaling) {
		npc.setNextAnimation(new Animation(28213));
		npc.setNextGraphics(new Graphics(6127));
		
		String scalingNote = scaling.bossDamageMultiplier > 1.5 ? " (scaling enhanced)" : "";
		announceToInstance(npc, "<col=9900ff>[Howl Alert] Wolf summons incoming" + scalingNote + "! Eliminate them quickly!</col>");
		
		if (npc.getId() == 22440)
			((CMHelwyr) npc).incrementHowlStage();
			
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				final int baseAmount = (npc.getInstance().getAliveWolves() == 3 ? 1 : 2);
				// Scale wolf count based on Boss Balancer scaling
				final int amount = scaling.bossDamageMultiplier > 1.8 ? baseAmount + 1 : baseAmount;
				
				for (int i = 0; i < amount; i++) {
					CywirAlpha wolf = new CywirAlpha(npc.getId() == 22440 ? 22441 : 22439, 
						npc.getInstance().getWorldTile(Utils.random(27, 45), Utils.random(27, 45)), 
						-1, true, true);
					npc.getInstance().getWolves().add(wolf);
				}
				
				// Howl guidance for players
				for (Player p : npc.getInstance().getPlayers()) {
					String intensityNote = scaling.bossDamageMultiplier > 1.5 ? " enhanced" : "";
					p.sendMessage("<col=9900ff>Howl Guide: " + amount + intensityNote + " wolves summoned! Focus them down quickly!</col>");
				}
			}
		});
		
		lastAttackConnected = true; // Howl always "connects"
		return 4;
	}
	
	/**
	 * Enhanced mushroom explosion for challenge mode with scaling
	 */
	private final int enhancedMushroomExplosion(final CMHelwyr helwyr, Entity target, CombatScaling scaling) {
		String intensityNote = scaling.bossDamageMultiplier > 2.0 ? " (MAXIMUM INTENSITY)" : " (Enhanced)";
		announceToInstance(helwyr, "<col=ff0000>[CHALLENGE EXPLOSION] Massive mushroom detonation sequence" + intensityNote + "!</col>");
		
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;
			private final List<WorldObject> shrooms = new ArrayList<WorldObject>();
			
			@Override
			public void run() {
				loop : for (WorldObject o : shrooms) {
					if (o != null) {
						if (World.containsObjectWithId(o, o.getId())) {
							for (WorldTile t : helwyr.getInstance().getTiles()) {
								if (t.getTileHash() == o.getTileHash())
									continue loop;
							}
							World.removeObject(o);
						}
						
						helwyr.getInstance().getPlayers().forEach(p -> {
							p.getPackets().sendGraphics(new Graphics(6125), o);
							if (p.getDistance(o) < 2) {
								// Enhanced explosion damage with Boss Balancer and HP-aware scaling
								int baseExplosionDamage = getEnhancedHelwyrMaxHit(helwyr);
								int scaledExplosionDamage = BossBalancer.calculateNPCDamageToPlayer(helwyr, p, (int)(baseExplosionDamage * 1.2));
								int safeDamage = applyHPAwareHelwyrDamageScaling(scaledExplosionDamage, p, "challenge_mode");
								checkAndWarnLowHPForHelwyr(p, safeDamage);
								
								p.applyHit(new Hit(helwyr, safeDamage, HitLook.MAGIC_DAMAGE));
								String intensityNote = scaling.bossDamageMultiplier > 2.0 ? " MAXIMUM" : " enhanced";
								p.sendMessage("<col=ff0000>Challenge Guide:" + intensityNote + " explosion damage! Keep moving!</col>");
							}
							
							if (p.getFamiliar() != null && p.getFamiliar().getDistance(o) < 2 && 
								p.getFamiliar().getDefinitions().hasAttackOption()) {
								int baseExplosionDamage = getEnhancedHelwyrMaxHit(helwyr);
								int scaledExplosionDamage = BossBalancer.calculateNPCDamageToPlayer(helwyr, p, (int)(baseExplosionDamage * 1.2));
								int safeDamage = applyHPAwareHelwyrDamageScaling(scaledExplosionDamage, p, "challenge_mode");
								p.getFamiliar().applyHit(new Hit(helwyr, safeDamage, HitLook.MAGIC_DAMAGE));
							}
						});
					}
				}
				shrooms.clear();
				
				if (ticks != 5) {
					for (int x = 0; x < 5; x++) {
						if (x == ticks)
							continue;
						loop : for (int i = 5 * x; i < (5 * x) + 5; i++) {
							final WorldTile tile = helwyr.getInstance().getWorldTile(HelwyrInstance.MUSHROOM_TILES[i][0], HelwyrInstance.MUSHROOM_TILES[i][1]);
							final WorldObject o = new WorldObject(101900, 10, 3, tile);
							for (WorldTile t : helwyr.getInstance().getTiles()) {
								if (t.getTileHash() == tile.getTileHash())
									continue loop;
							}
							World.spawnObject(o);
							shrooms.add(o);
						}
					}
				} else
					stop();
				ticks++;
			}
		}, 0, 3);
		
		return 5;
	}
	
	/**
	 * ENHANCED ANTI-SAFESPOT SYSTEM: Helwyr-specific countermeasures with scaling
	 */
	private void checkAndHandleHelwyrSafespot(Helwyr helwyr, Player player, CombatScaling scaling) {
		long currentTime = System.currentTimeMillis();
		
		if (currentTime - lastSafespotCheck < SAFESPOT_CHECK_INTERVAL) {
			return;
		}
		lastSafespotCheck = currentTime;
		
		// Detect Helwyr-specific safespotting
		if (isHelwyrSafespotting(helwyr, player)) {
			handleHelwyrSafespotting(helwyr, player, scaling);
		}
	}
	
	/**
	 * Detect Helwyr-specific safespotting
	 */
	private boolean isHelwyrSafespotting(Helwyr helwyr, Player player) {
		// GWD2 instance-based detection
		boolean notInInstance = !helwyr.getInstance().getPlayers().contains(player);
		boolean tooFarFromBoss = !player.withinDistance(helwyr, 12);
		boolean consecutiveMisses = consecutiveMeleeAttempts >= 3;
		boolean cornerHiding = isPlayerInCorner(helwyr, player);
		
		return notInInstance || (tooFarFromBoss && consecutiveMisses) || 
			   (cornerHiding && !lastAttackConnected);
	}
	
	/**
	 * Check if player is hiding in instance corners
	 */
	private boolean isPlayerInCorner(Helwyr helwyr, Player player) {
		// Instance boundary detection (simplified)
		int playerX = player.getX();
		int playerY = player.getY();
		int helwyrX = helwyr.getX();
		int helwyrY = helwyr.getY();
		
		// Check if player is far from center and Helwyr can't reach
		int distanceFromCenter = Math.max(Math.abs(playerX - helwyrX), Math.abs(playerY - helwyrY));
		return distanceFromCenter > 8 && !player.withinDistance(helwyr, 2);
	}
	
	/**
	 * Handle Helwyr safespotting with nature-themed countermeasures and scaling
	 */
	private void handleHelwyrSafespotting(Helwyr helwyr, Player player, CombatScaling scaling) {
		String intensityNote = scaling.bossDamageMultiplier > 1.5 ? " enhanced" : "";
		announceToInstance(helwyr, "<col=ff0000>[Anti-Safespot] Nature's" + intensityNote + " wrath reaches all corners!</col>");
		
		int countermeasure = Utils.random(3);
		
		switch (countermeasure) {
		case 0:
			// Mushroom spawn at safespot
			player.sendMessage("<col=00ff00>Nature's fury spawns mushrooms at your hiding spot!</col>");
			spawnSafespotMushroom(helwyr, player, scaling);
			break;
			
		case 1:
			// Bleed effect reaches safespot
			player.sendMessage("<col=cc0000>Helwyr's claws reach through the undergrowth!</col>");
			applySafespotBleed(helwyr, player, scaling);
			break;
			
		case 2:
			// Wolf pack sent to safespot
			player.sendMessage("<col=9900ff>The pack hunts you down!</col>");
			sendWolfToSafespot(helwyr, player, scaling);
			break;
		}
		
		// Reset consecutive attempts after countermeasure
		consecutiveMeleeAttempts = 0;
	}
	
	// ===== ENHANCED ANTI-SAFESPOT COUNTERMEASURE METHODS =====
	
	private void spawnSafespotMushroom(Helwyr helwyr, Player player, CombatScaling scaling) {
		final WorldTile playerTile = new WorldTile(player);
		final WorldObject mushroom = new WorldObject(101900, 10, 3, playerTile);
		
		World.spawnObject(mushroom);
		player.getPackets().sendGraphics(new Graphics(6124), playerTile);
		
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().sendGraphics(new Graphics(6125), playerTile);
				if (player.getX() == playerTile.getX() && player.getY() == playerTile.getY()) {
					int baseMaxHit = getEnhancedHelwyrMaxHit(helwyr);
					int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(helwyr, player, (int)(baseMaxHit * 0.8));
					int safeDamage = applyHPAwareHelwyrDamageScaling(scaledDamage, player, "mushroom_explosion");
					player.applyHit(new Hit(helwyr, safeDamage, HitLook.MAGIC_DAMAGE));
				}
				World.removeObject(mushroom);
			}
		}, 4);
	}
	
	private void applySafespotBleed(Helwyr helwyr, Player player, CombatScaling scaling) {
		addEnhancedBleedEffect(helwyr, player, true, scaling);
		player.setNextGraphics(new Graphics(6126));
	}
	
	private void sendWolfToSafespot(Helwyr helwyr, Player player, CombatScaling scaling) {
		final WorldTile playerTile = new WorldTile(player);
		int wolfCount = scaling.bossDamageMultiplier > 1.8 ? 2 : 1; // More wolves for high scaling
		
		for (int i = 0; i < wolfCount; i++) {
			CywirAlpha huntingWolf = new CywirAlpha(helwyr.getId() == 22440 ? 22441 : 22439, 
				playerTile, -1, true, true);
			helwyr.getInstance().getWolves().add(huntingWolf);
		}
	}
	
	// ===== ENHANCED BOSS BALANCER INTEGRATION METHODS =====
	
	/**
	 * Get enhanced max hit using Boss Balancer for Helwyr
	 */
	private int getEnhancedHelwyrMaxHit(Helwyr helwyr) {
		try {
			int baseMaxHit = helwyr.getMaxHit();
			
			// Apply Boss Balancer bonuses
			int[] bonuses = NPCBonuses.getBonuses(helwyr.getId());
			if (bonuses != null && bonuses.length >= 5) {
				// Helwyr is primarily melee with nature magic elements
				int meleeBonus = Math.max(Math.max(bonuses[0], bonuses[1]), bonuses[2]);
				int magicBonus = bonuses[3];
				int maxBonus = Math.max(meleeBonus, magicBonus);
				
				if (maxBonus > 0) {
					double bonusMultiplier = 1.0 + (maxBonus * 0.0018); // 0.18% per bonus point
					baseMaxHit = (int) (baseMaxHit * bonusMultiplier);
				}
			}
			
			return Math.max(baseMaxHit, 200); // Minimum for GWD2 boss
		} catch (Exception e) {
			return 450; // Fallback for Helwyr
		}
	}
	
	/**
	 * Determine Helwyr boss tier (mid-high tier GWD2 boss)
	 */
	private int getHelwyrBossTier(Helwyr helwyr) {
		try {
			// Check BossBalancer configuration
			int configuredTier = readBossTierFromFile(helwyr.getId());
			if (configuredTier != -1) {
				return configuredTier;
			}
			
			// Helwyr is typically mid-high tier (6-8)
			int maxHp = helwyr.getMaxHitpoints();
			if (maxHp <= 17000) return 6;      // Master
			else if (maxHp <= 25500) return 7; // Elite
			else return 8;                     // Legendary
			
		} catch (Exception e) {
			return HELWYR_DEFAULT_TIER; // Elite tier default for Helwyr
		}
	}
	
	/**
	 * Execute basic Helwyr attack for non-player targets
	 */
	private int executeBasicHelwyrAttack(NPC npc, Entity target) {
		NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (defs == null) return 4;
		
		int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		npc.setNextAnimation(new Animation(defs.getAttackEmote()));
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		return defs.getAttackDelay();
	}
	
	// ===== UTILITY METHODS =====
	
	private boolean isValidHelwyrCombatState(Helwyr helwyr, Entity target) {
		return helwyr != null && target != null && 
			   !helwyr.isDead() && !helwyr.hasFinished() && 
			   !target.isDead() && !target.hasFinished() &&
			   helwyr.getInstance() != null;
	}
	
	private void announceToInstance(Helwyr helwyr, String message) {
		for (Player player : helwyr.getInstance().getPlayers()) {
			if (player != null) {
				player.sendMessage(message);
			}
		}
	}
	
	private int readBossTierFromFile(int npcId) {
		try {
			java.io.File bossFile = new java.io.File("data/npcs/bosses/" + npcId + ".txt");
			if (!bossFile.exists()) return -1;
			
			java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(bossFile));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("tier=")) {
					reader.close();
					return Integer.parseInt(line.substring(5));
				}
			}
			reader.close();
		} catch (Exception e) {}
		return -1;
	}
	
	// ===== COMBAT SESSION MANAGEMENT METHODS =====
	
	/**
	 * NEW v6.0: Handle Helwyr combat end with proper BossBalancer cleanup
	 */
	public static void onHelwyrCombatEnd(Player player, Helwyr helwyr) {
		if (player == null) return;
		
		try {
			// End BossBalancer v5.0 combat session
			BossBalancer.endCombatSession(player);
			
			// Clear local tracking maps
			Integer playerKey = Integer.valueOf(player.getIndex());
			
			combatSessions.remove(playerKey);
			lastWarningTimeMap.remove(playerKey);
			warningCount.remove(playerKey);
			lastScalingType.remove(playerKey);
			educationalTipCount.remove(playerKey);
			
			// Clear BossBalancer player cache
			BossBalancer.clearPlayerCache(player.getIndex());
			
			// Send completion message with v5.0 info
			player.sendMessage("<col=00ff00>Helwyr combat session ended. Nature scaling data cleared.</col>");
			
		} catch (Exception e) {
			System.err.println("Helwyr: Error ending v5.0 combat session: " + e.getMessage());
		}
	}
	
	/**
	 * NEW v6.0: Handle prayer changes during Helwyr combat
	 */
	public static void onPlayerPrayerChanged(Player player) {
		if (player == null) return;
		
		try {
			Integer playerKey = Integer.valueOf(player.getIndex());
			
			// Only handle if in active combat session
			if (combatSessions.containsKey(playerKey)) {
				// Notify BossBalancer v5.0 of prayer change
				BossBalancer.onPrayerChanged(player);
				
				// Send update message
				player.sendMessage("<col=DDA0DD>Prayer change detected. Helwyr scaling analysis updated.</col>");
			}
		} catch (Exception e) {
			System.err.println("Helwyr: Error handling v5.0 prayer change: " + e.getMessage());
		}
	}
	
	/**
	 * NEW v6.0: Handle equipment changes during Helwyr combat
	 */
	public static void onPlayerEquipmentChanged(Player player) {
		if (player == null) return;
		
		try {
			Integer playerKey = Integer.valueOf(player.getIndex());
			HelwyrCombatSession session = combatSessions.get(playerKey);
			
			if (session != null) {
				// Recalculate power if equipment changed
				double newPlayerPower = BossBalancer.calculateActualPlayerPower(player);
				double oldPowerRatio = session.powerRatio;
				double newPowerRatio = newPlayerPower / session.lockedHelwyrPower;
				
				// Update session data
				session.lockedPlayerPower = newPlayerPower;
				session.powerRatio = newPowerRatio;
				
				// Check armor coverage
				BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
				boolean newArmorStatus = armorResult.hasFullArmor;
				
				if (newArmorStatus != session.hasFullArmor) {
					session.hasFullArmor = newArmorStatus;
					if (newArmorStatus) {
						player.sendMessage("<col=32CD32>Helwyr: Full armor protection restored! Nature damage scaling normalized.</col>");
					} else {
						player.sendMessage("<col=FF6600>Helwyr: Armor protection compromised! Increased nature damage vulnerability!</col>");
					}
				}
				
				// Notify of significant power changes
				if (Math.abs(newPowerRatio - oldPowerRatio) > 0.3) {
					String changeType = newPowerRatio > oldPowerRatio ? "increased" : "decreased";
					player.sendMessage("<col=00ff00>Helwyr: Combat power " + changeType + 
									 "! Scaling analysis updated (Ratio: " + String.format("%.2f", newPowerRatio) + ":1)</col>");
				}
			}
		} catch (Exception e) {
			System.err.println("Helwyr: Error handling equipment change: " + e.getMessage());
		}
	}
	
	/**
	 * NEW v6.0: Get current scaling information for Helwyr
	 */
	public static String getCurrentScalingInfo(Player player) {
		if (player == null) return "No player data";
		
		try {
			Integer playerKey = Integer.valueOf(player.getIndex());
			HelwyrCombatSession session = combatSessions.get(playerKey);
			
			if (session != null) {
				return String.format("Helwyr Scaling - Power Ratio: %.2f:1 | Type: %s | Phase: %d | Full Armor: %s | Wolves: %d",
								   session.powerRatio, session.currentScalingType, session.currentPhase, 
								   session.hasFullArmor ? "YES" : "NO", session.aliveWolves);
			} else {
				return "No active Helwyr combat session";
			}
		} catch (Exception e) {
			return "Error retrieving scaling info: " + e.getMessage();
		}
	}
	
	/**
	 * NEW v6.0: Force cleanup for Helwyr combat (call on logout/death)
	 */
	public static void forceCleanup(Player player) {
		if (player != null) {
			onHelwyrCombatEnd(player, null);
		}
	}
	
	/**
	 * NEW v6.0: Initialize BossBalancer configuration for Helwyr
	 */
	public static void initializeHelwyrBossConfiguration() {
		try {
			// Auto-configure Helwyr if not already configured
			// This should be called during server startup
			
			int[] helwyrIds = {22438, 22440}; // Normal and Challenge mode
			
			for (int helwyrId : helwyrIds) {
				if (!BossBalancer.isBossConfigured(helwyrId)) {
					int tier = helwyrId == 22440 ? 8 : HELWYR_DEFAULT_TIER; // Challenge mode is higher tier
					boolean success = BossBalancer.autoConfigureBoss(
						helwyrId, 
						tier, 
						HELWYR_BOSS_TYPE, 
						"HelwyrCombat_AutoConfig",
						true // Save to file for persistence
					);
					
					if (success) {
						System.out.println("Helwyr: Auto-configured boss " + helwyrId + 
										 " as Tier " + tier + " Hybrid Boss");
					}
				}
			}
			
			System.out.println("Helwyr: BossBalancer v5.0 integration initialization complete");
			
		} catch (Exception e) {
			System.err.println("Helwyr: Error initializing BossBalancer configuration: " + e.getMessage());
		}
	}
	
	/**
	 * NEW v6.0: Get recommended player stats for Helwyr
	 */
	public static String getRecommendedPlayerStats(int helwyrTier) {
		StringBuilder recommendations = new StringBuilder();
		
		recommendations.append("=== HELWYR RECOMMENDATIONS ===\n");
		recommendations.append("Tier ").append(helwyrTier).append(" Helwyr (GWD2 Nature Boss):\n\n");
		
		// Combat level recommendation
		int recommendedCombatLevel = helwyrTier * 14 + 30; // Same formula as BossBalancer
		recommendations.append("Recommended Combat Level: ").append(recommendedCombatLevel).append("+\n");
		
		// Specific stat recommendations
		int baseStatRecommendation = helwyrTier * 10 + 50;
		recommendations.append("Attack/Strength Level: ").append(baseStatRecommendation).append("+ (melee combat)\n");
		recommendations.append("Defence Level: ").append(baseStatRecommendation).append("+ (important for survival)\n");
		recommendations.append("Prayer Level: ").append(baseStatRecommendation - 5).append("+ (for protection prayers)\n");
		recommendations.append("Hitpoints: ").append(Math.max(990, baseStatRecommendation - 15)).append("+ (minimum for survival)\n\n");
		
		// Equipment recommendations
		recommendations.append("Essential Equipment:\n");
		recommendations.append("• Full armor set (Tier ").append(Math.max(1, helwyrTier - 1)).append("+ recommended)\n");
		recommendations.append("• High-tier melee weapon (Tier ").append(helwyrTier).append("+ for optimal DPS)\n");
		recommendations.append("• Antifire protection (for wolf breath attacks)\n");
		recommendations.append("• Melee protection prayers\n");
		recommendations.append("• Food for healing (recommended: ").append(Math.min(20, helwyrTier * 2 + 8)).append("+ healing)\n");
		recommendations.append("• Area-of-effect abilities (for mushroom clearing)\n\n");
		
		// Special notes for Helwyr
		recommendations.append("Helwyr Special Notes:\n");
		recommendations.append("• Nature attack spawns explosive mushrooms\n");
		recommendations.append("• Bleed attack causes area damage and DoT effect\n");
		recommendations.append("• Frenzy attack hits multiple directions with spinning motion\n");
		recommendations.append("• Howl summons Cywir Alpha wolves - prioritize killing them\n");
		recommendations.append("• Challenge mode (22440) has enhanced mechanics and mushroom explosions\n");
		recommendations.append("• BossBalancer v5.0 scales difficulty based on your total power\n");
		recommendations.append("• Stay mobile to avoid mushroom explosions and bleed zones\n");
		
		return recommendations.toString();
	}

	// ===== ORIGINAL METHODS PRESERVED FOR COMPATIBILITY =====
	
	/**
	 * Sends the basic bite attack.
	 */
	private final int bite(Helwyr npc, Entity target) {
		npc.setNextAnimation(new Animation(28205));
		delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, target)));
		return 4;
	}

	/**
	 * Sends three mushrooms to the arena.
	 */
	private final int nature(Helwyr helwyr, Entity target) {
		helwyr.setNextForceTalk(new ForceTalk("Nature, lend me your aid!"));
		helwyr.setNextAnimation(new Animation(28207));
		final int amount = helwyr.getId() == 22440 ? 6 : 3;
		final WorldTile[] tiles = new WorldTile[amount];
		for (int i = 0; i < amount; i++) {
			if (helwyr.getInstance().getAvailableTiles().size() == 0)
				break;
			WorldTile tile = helwyr.getInstance().getAvailableTiles().get(Utils.random(helwyr.getInstance().getAvailableTiles().size()));
			helwyr.getInstance().addMushroom(tiles[i] = tile);
			World.sendProjectile(helwyr, tile, 6122, 70, 10, 50, 2, 10, 0);
		}
		WorldTasksManager.schedule(new WorldTask() {
			private final WorldObject[] objects = new WorldObject[amount];
			private boolean second;
			@Override
			public void run() {
				if (helwyr.isDead() || helwyr.hasFinished()) {
					stop();
					return;
				}
				if (!second) {
					for (int i = 0; i < amount; i++) {
						if (tiles[i] == null)
							continue;
						for (Player p : helwyr.getInstance().getPlayers())
							p.getPackets().sendGraphics(new Graphics(6124), tiles[i]);
						World.spawnObject(objects[i] = new WorldObject(101900, 10, 3, tiles[i]));
					}
				} else {
					for (int i = 0; i < amount; i++) {
						if (tiles[i] == null || objects[i] == null)
							continue;
						for (Player p : helwyr.getInstance().getPlayers())
							p.getPackets().sendGraphics(new Graphics(6125), tiles[i]);
						helwyr.getInstance().removeMushroom(tiles[i]);
						World.removeObject(objects[i]);
					}
					stop();
				}
				second = true;
			}
		}, 0, 97);
		return 4;
	}

	/**
	 * Sends the bleed swipe attack.
	 */
	private final int bleed(Helwyr npc, Entity target) {
		npc.setNextForceTalk(new ForceTalk("YOU. WILL. BLEED!"));
		npc.setNextAnimation(new Animation(28214));
		npc.resetWalkSteps();
		npc.setCannotMove(true);
		npc.setNextGraphics(new Graphics(6126));
		final WorldTile bleedTile = new WorldTile(target.getX(), target.getY(), 1);
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;
			@Override
			public void run() {
				if (ticks == 3) {
					npc.getInstance().getPlayers().forEach(p -> {
						if (p.getDistance(bleedTile) < 2) {
							p.applyHit(new Hit(target, npc.getId() == 22440 ? Utils.random(300, 900) : Utils.random(150, 500), HitLook.MELEE_DAMAGE));
							addBleedEffect(npc, p, true);
							if (npc.getId() == 22440) {
								p.sendMessage("Helwyr's claws injure you.");
								p.getPrayer().closeAllPrayers();
								p.setPrayerDelay(5000);
							}
						}
						if (p.getFamiliar() != null && p.getFamiliar().getDistance(bleedTile) < 2  && p.getFamiliar().getDefinitions().hasAttackOption())
							p.getFamiliar().applyHit(new Hit(npc, npc.getId() == 22440 ? Utils.random(300, 900) : Utils.random(150, 500), HitLook.MELEE_DAMAGE));
					});
				} else if (ticks == 4) {
					npc.setCannotMove(false);
					npc.setTarget(target);
					stop();
				}
				ticks++;
			}
			
		}, 0, 0);
		return 7;
	}
	
	private final void addBleedEffect(Helwyr npc, Player p, final boolean bleedAttack) {
		final int bleed = p.getTemporaryAttributtes().get("bleed") == null ? 0 : (int) p.getTemporaryAttributtes().get("bleed");
		if (!bleedAttack && bleed == 0)
			return;
		p.getTemporaryAttributtes().put("bleed", npc.getId() == 22440 ? (bleed + 10 > 50 ? 50 : bleed + 10) : (bleed + 5 > 25 ? 25 : bleed + 5));
		p.getTemporaryAttributtes().put("bleedTime", Utils.currentTimeMillis());
		if (bleed != 0) {
			p.getPackets().sendPlayerMessage(1, 15263739, "Helwyr's continued attacks cause you to lose even more blood!", true);
		} else {
			p.getPackets().sendPlayerMessage(1, 15263739, "Helwyr's attacks cause you to lose blood.", true);
		}
	}

	private final int frenzy(Helwyr npc, Entity target) {
		npc.setNextForceTalk(new ForceTalk("You cannot escape me. Aaaargh!"));
		npc.setNextAnimation(new Animation(28215));
		npc.resetWalkSteps();
		npc.getCombat().reset();
		npc.setCannotMove(true);
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;
			private int direction = (int) (Math.round(npc.getDirection() / 45.51) / 45);
			private final List<Player> players = new ArrayList<Player>();
			@Override
			public void run() {
				if (ticks > 0)
					direction = (direction - 2 < 0 ? (8 + (direction - 2)) : direction - 2);
				final byte[] dirs = Utils.DIRS[direction];
				final WorldTile tile = ticks == 0 ? new WorldTile(target) : new WorldTile(npc.getCoordFaceX(npc.getSize()) + (dirs[0] * 3), npc.getCoordFaceY(npc.getSize()) + (dirs[1] * 3), npc.getPlane());
				npc.getInstance().getPlayers().forEach(p -> {
					if (p.getDistance(tile) < 3) {
						delayHit(npc, 0, target, getMeleeHit(npc, getRandomMaxHit(npc, npc.getId() == 22440 ? 800 : 400, NPCCombatDefinitions.MELEE, target)));
						if (!players.contains(p)) {
							addBleedEffect(npc, p, false);
							players.add(p);
						}
					}
					if (p.getFamiliar() != null && p.getFamiliar().getDistance(tile) < 3  && p.getFamiliar().getDefinitions().hasAttackOption())
						p.getFamiliar().applyHit(new Hit(npc, getRandomMaxHit(npc, npc.getId() == 22440 ? 800 : 400, NPCCombatDefinitions.MELEE, target), HitLook.MELEE_DAMAGE));
				});
				if (ticks++ == 4) {
					npc.setCannotMove(false);
					npc.getCombat().setTarget(target);
					stop();
					return;
				}
			}
		}, 0, 1);
		return 12;
	}
	
	private final int howl(Helwyr npc, Entity target) {
		npc.setNextAnimation(new Animation(28213));
		npc.setNextGraphics(new Graphics(6127));
		if (npc.getId() == 22440)
			((CMHelwyr) npc).incrementHowlStage();
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				final int amount = (npc.getInstance().getAliveWolves() == 3 ? 1 : 2);
				for (int i = 0; i < amount; i++)
					npc.getInstance().getWolves().add(new CywirAlpha(npc.getId() == 22440 ? 22441 : 22439, npc.getInstance().getWorldTile(Utils.random(27, 45), Utils.random(27, 45)), -1, true, true));
			}
		});
		return 4;
	}
	
	private final int mushroomExplosion(final CMHelwyr helwyr, Entity target) {
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;
			private final List<WorldObject> shrooms = new ArrayList<WorldObject>();
			@Override
			public void run() {
				loop : for (WorldObject o : shrooms) {
					if (o != null) {
						if (World.containsObjectWithId(o, o.getId())) {
							for (WorldTile t : helwyr.getInstance().getTiles()) {
								if (t.getTileHash() == o.getTileHash())
									continue loop;
							}
							World.removeObject(o);
						}
						helwyr.getInstance().getPlayers().forEach(p -> {
							p.getPackets().sendGraphics(new Graphics(6125), o);
							if (p.getDistance(o) < 2) 
								p.applyHit(new Hit(helwyr, Utils.random(300, 600), HitLook.REGULAR_DAMAGE));
							if (p.getFamiliar() != null && p.getFamiliar().getDistance(o) < 2  && p.getFamiliar().getDefinitions().hasAttackOption())
								p.getFamiliar().applyHit(new Hit(helwyr, Utils.random(300, 600), HitLook.REGULAR_DAMAGE));
						});
					}
				}
				shrooms.clear();
				if (ticks != 5) {
					for (int x = 0; x < 5; x++) {
						if (x == ticks)
							continue;
						loop : for (int i = 5 * x; i < (5 * x) + 5; i++) {
							final WorldTile tile = helwyr.getInstance().getWorldTile(HelwyrInstance.MUSHROOM_TILES[i][0], HelwyrInstance.MUSHROOM_TILES[i][1]);
							final WorldObject o = new WorldObject(101900, 10, 3, tile);
							for (WorldTile t : helwyr.getInstance().getTiles()) {
								if (t.getTileHash() == tile.getTileHash())
									continue loop;
							}
							World.spawnObject(o);
							shrooms.add(o);
						}
					}
				} else
					stop();
				ticks++;
			}
		}, 0, 3);
		return 5;
	}
}