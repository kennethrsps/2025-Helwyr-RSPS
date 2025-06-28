package com.rs.game.npc.combat.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

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
import com.rs.utils.NPCBonuses;
import com.rs.utils.Logger;

/**
 * Enhanced Kree'arra Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Intelligent power-based scaling, armor analysis, HP-aware damage scaling, AoE aerial mechanics
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 4.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System
 */
public class Kreearra extends CombatScript {

    // ===== AERIAL COMBAT PHASES - Enhanced for v5.0 =====
    private static final double FLIGHT_PHASE_2_THRESHOLD = 0.65;
    private static final double FLIGHT_PHASE_3_THRESHOLD = 0.35; 
    private static final double FLIGHT_PHASE_4_THRESHOLD = 0.10;

    // ===== ENHANCED GUIDANCE SYSTEM - Intelligent scaling aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentFlightPhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> aerialManeuvers = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 180000; // 3 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 3000; // 3 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - CRITICAL SAFETY SYSTEM =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.28; // Max 28% of player HP per hit (aerial boss is precise)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.42;  // Max 42% for critical aerial attacks
    private static final double AOE_COMBO_DAMAGE_PERCENT = 0.38; // Max 38% for AoE combos
    private static final double WIND_STORM_DAMAGE_PERCENT = 0.34; // Max 34% for wind storms
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 420;          // Hard cap (28% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 22;               // Minimum damage to prevent 0 hits

    // ===== AERIAL ATTACK PATTERNS with v5.0 intelligence =====
    private static final AerialAttackPattern[] AERIAL_ATTACK_PATTERNS = {
        new AerialAttackPattern(17396, 0, 0, "melee_dive", false, ""),
        new AerialAttackPattern(17397, 1197, 0, "ranged_barrage", true, "RANGED BARRAGE incoming - AoE damage to all nearby!"),
        new AerialAttackPattern(17397, 1198, 1196, "magic_storm", true, "MAGIC STORM incoming - area spell damage!"),
        new AerialAttackPattern(17397, 1197, 1196, "aerial_supremacy", true, "AERIAL SUPREMACY incoming - ultimate AoE attack!")
    };

    // ===== SAFE SPOT PREVENTION - Aerial-themed =====
    private static final Map<Integer, Integer> consecutiveRangedAttacks = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastAttackHit = new ConcurrentHashMap<Integer, Boolean>();

    // ===== AoE TRACKING SYSTEM =====
    private static final Map<Integer, List<Integer>> activeGroupTargets = new ConcurrentHashMap<Integer, List<Integer>>();

    @Override
    public Object[] getKeys() {
        return new Object[] { 6222 }; // Kree'arra NPC ID
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeAerialCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentAerialGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorAerialScalingChanges(player, scaling);
        
        // Update phase tracking with v5.0 scaling
        updateIntelligentFlightPhaseTracking(npc, scaling);
        
        // Check for aerial-themed safe spotting
        checkAerialSafeSpotting(player, npc, scaling);
        
        // Perform aerial maneuvers with enhanced frequency based on scaling
        performEnhancedAerialManeuvers(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentAerialAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize aerial combat session using BossBalancer v5.0
     */
    private void initializeAerialCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            aerialManeuvers.put(sessionKey, Integer.valueOf(0));
            consecutiveRangedAttacks.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastAttackHit.put(sessionKey, Boolean.TRUE);
            
            // Initialize group tracking for AoE mechanics
            Integer npcKey = Integer.valueOf(npc.getIndex());
            activeGroupTargets.put(npcKey, new ArrayList<Integer>());
            
            // Send v5.0 enhanced aerial combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentAerialWelcomeMessage(scaling);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for aerial combat
            performInitialAerialArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial aerial armor analysis
     */
    private void performInitialAerialArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=87CEEB>Aerial Analysis: Exposed areas detected. Wind attacks will find weaknesses!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=4169E1>Aerial Analysis: Full flight protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% wind resistance).</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from aerial attacks
     */
    private int applyHPAwareAerialDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (more dangerous when wounded)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on aerial attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "aerial_supremacy":
                case "ultimate_aerial":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "aoe_combo":
                case "wind_combo":
                    damagePercent = AOE_COMBO_DAMAGE_PERCENT;
                    break;
                case "magic_storm":
                case "wind_storm":
                    damagePercent = WIND_STORM_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 78% of current HP for aerial boss
            if (currentHP > 0) {
                int emergencyCap = (int)(currentHP * 0.78);
                safeDamage = Math.min(safeDamage, emergencyCap);
            }
            
            return safeDamage;
            
        } catch (Exception e) {
            // Fallback to absolute cap
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
    }

    /**
     * NEW v5.0: Send HP warning if player is in danger from aerial attacks
     */
    private void checkAndWarnLowHPForAerial(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming aerial damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.65) {
                    player.sendMessage("<col=ff0000>AERIAL WARNING: This wind attack will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.45) {
                    player.sendMessage("<col=87CEEB>AERIAL WARNING: Heavy wind damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent aerial welcome message based on power analysis
     */
    private String getIntelligentAerialWelcomeMessage(CombatScaling scaling) {
        StringBuilder message = new StringBuilder();
        message.append("<col=4169E1>Kree'arra takes flight, wind currents analyzing your aerial readiness (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=8B0000>[Storm intensifies: +").append(diffIncrease).append("% wind power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Gentle winds: -").append(assistance).append("% aerial damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Wind resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full aerial protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent aerial guidance with power-based scaling awareness
     */
    private void provideIntelligentAerialGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentAerialGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent aerial guidance message based on power analysis
     */
    private String getIntelligentAerialGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentFlightPhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getAerialScalingAnalysisMessage(scaling);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=8B0000>Aerial Analysis: Missing armor exposes you to wind fury! Aerial damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME storms due to scaling!)" : "";
                    return "<col=87CEEB>Aerial Analysis: Hurricane phase reached. Wind power dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=4169E1>Aerial Analysis: Tornado phase! Maximum wind power unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=8B0000>Aerial Analysis: Extreme wind scaling detected! Consider fighting stronger aerial beings!</col>";
                }
                break;
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get aerial scaling analysis message
     */
    private String getAerialScalingAnalysisMessage(CombatScaling scaling) {
        String baseMessage = "<col=DDA0DD>Aerial Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>Wind mercy granted! Aerial damage reduced by " + 
                   assistancePercent + "% due to insufficient flight gear.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=8B0000>Storm escalation active! Wind power increased by " + 
                   difficultyIncrease + "% due to superior gear advantage.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=4169E1>Balanced aerial encounter. Optimal wind resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=87CEEB>Slight gear advantage detected. Wind intensity increased by " + 
                   difficultyIncrease + "% for balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Aerial power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during aerial combat
     */
    private void monitorAerialScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getAerialScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get aerial scaling change message
     */
    private String getAerialScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Aerial Update: Flight readiness improved to balanced! Wind mercy reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=8B0000>Aerial Update: Storm escalation now active due to increased power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Aerial Update: Wind resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=4169E1>Aerial Update: Full flight protection restored! Wind damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent flight phase tracking with BossBalancer integration
     */
    private void updateIntelligentFlightPhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentFlightPhase(npc);
        
        Integer lastPhase = currentFlightPhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentFlightPhase.put(npcKey, newPhase);
            handleIntelligentFlightPhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current flight phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentFlightPhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= FLIGHT_PHASE_4_THRESHOLD) return 4;
            if (hpPercent <= FLIGHT_PHASE_3_THRESHOLD) return 3;
            if (hpPercent <= FLIGHT_PHASE_2_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent flight phase transitions with scaling integration
     */
    private void handleIntelligentFlightPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("The winds grow stronger!"));
            npc.setNextGraphics(new Graphics(1196));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED HURRICANE FORCE!" : "Feel the fury of the storm!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(1198));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE TORNADO - MAXIMUM WIND POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED TORNADO PHASE!" : "TORNADO FORCE!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(1197));
            
            // Enhanced heal calculation with v5.0 scaling (wind regeneration)
            int baseHeal = npc.getMaxHitpoints() / 8; // Aerial boss heals quickly
            int scaledHeal = (int)(baseHeal * scaling.bossHpMultiplier);
            npc.heal(Math.max(scaledHeal, 100));
            break;
        }
    }

    /**
     * NEW v5.0: Check for aerial safe spotting
     */
    private void checkAerialSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveRangedAttacks.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastAttackHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect aerial-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean kreeStuckRanged = consecutiveCount > 7; // Longer for aerial boss
        boolean recentMiss = !lastHit;
        
        boolean aerialSafeSpot = playerDistanced && kreeStuckRanged && recentMiss;
        
        if (aerialSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating aerial-themed responses
            if (warningCount >= 3) {
                performAerialAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=87CEEB>The wind currents calm as you fight with honor...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform aerial anti-safe spot measure
     */
    private void performAerialAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=8B0000>Kree'arra's wind mastery reaches cowards hiding from aerial combat!</col>");
        
        // Wind projectile that pierces obstacles
        npc.setNextAnimation(new Animation(17397));
        World.sendProjectile(npc, player, 1197, 41, 16, 41, 35, 16, 0);
        
        // Enhanced damage based on scaling
        int baseDamage = (int)(npc.getMaxHit() * 1.5);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareAerialDamageScaling(scaledDamage, player, "wind_pierce");
        
        delayHit(npc, 1, player, getRangeHit(npc, safeDamage));
        player.setNextGraphics(new Graphics(1196));
        
        player.sendMessage("<col=87CEEB>AERIAL PENALTY: Safe spotting detected - wind power pierces barriers!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced aerial maneuvers with scaling-based frequency
     */
    private void performEnhancedAerialManeuvers(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        Integer maneuverCount = aerialManeuvers.get(npcKey);
        if (maneuverCount == null) maneuverCount = 0;
        
        // Increase aerial maneuver frequency based on scaling
        int maneuverChance = 20; // Base 20%
        if (scaling.bossDamageMultiplier > 2.0) {
            maneuverChance = 35; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            maneuverChance = 28; // More frequent for high scaling
        }
        
        if (Utils.random(100) < maneuverChance) {
            maneuverCount++;
            aerialManeuvers.put(npcKey, maneuverCount);
            
            // Enhanced aerial maneuvers based on phase and scaling
            int phase = getCurrentFlightPhase(npc);
            performScaledAerialManeuver(npc, phase, scaling, maneuverCount);
        }
    }

    /**
     * NEW v5.0: Perform scaled aerial maneuver based on phase and scaling
     */
    private void performScaledAerialManeuver(NPC npc, int phase, CombatScaling scaling, int maneuverCount) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicManeuvers = {
            "The winds shift direction!",
            "Kree'arra circles overhead!",
            "Wind currents intensify!",
            "Aerial superiority maintained!",
            "The storm builds power!",
            "Flight patterns adjust!",
            "Wind speed increasing!"
        };
        
        String[] enhancedManeuvers = {
            "ENHANCED AERIAL MASTERY!",
            "SUPERIOR WIND CONTROL ACTIVATED!",
            "MAXIMUM FLIGHT POWER ENGAGED!",
            "HURRICANE-FORCE MANEUVERS!",
            "ULTIMATE AERIAL SUPREMACY!"
        };
        
        String selectedManeuver;
        if (isHighScaling && phase >= 3) {
            // Use enhanced maneuvers for high scaling + high phase
            selectedManeuver = enhancedManeuvers[Utils.random(enhancedManeuvers.length)];
        } else {
            // Use basic maneuvers
            selectedManeuver = basicManeuvers[Utils.random(basicManeuvers.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedManeuver));
        npc.setNextGraphics(new Graphics(1196)); // Wind effect
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent aerial warning system
     */
    private int performIntelligentAerialAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Determine if this is AoE or single target based on targets
            List<Entity> possibleTargets = new ArrayList<Entity>(npc.getPossibleTargets());
            boolean isAoEAttack = possibleTargets.size() > 1;
            
            // Select attack pattern with v5.0 intelligence
            int phase = getCurrentFlightPhase(npc);
            AerialAttackPattern pattern = selectIntelligentAerialAttackPattern(phase, scaling, currentCount, isAoEAttack);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentAerialWarning(scaling, currentCount)) {
                sendIntelligentAerialPreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        if (isAoEAttack) {
                            executeIntelligentScaledAoEAttack(npc, possibleTargets, pattern, scaling);
                        } else {
                            executeIntelligentScaledAerialAttack(npc, player, pattern, scaling);
                        }
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentAerialAttackDelay(npc, phase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                if (isAoEAttack) {
                    executeIntelligentScaledAoEAttack(npc, possibleTargets, pattern, scaling);
                } else {
                    executeIntelligentScaledAerialAttack(npc, player, pattern, scaling);
                }
                return getIntelligentAerialAttackDelay(npc, phase, scaling);
            }
            
        } catch (Exception e) {
            return 4;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent aerial warning probability based on scaling
     */
    private boolean shouldGiveIntelligentAerialWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing aerial boss
        boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
        int warningFrequency = isUndergeared ? 5 : 7; // Every 5th vs 7th attack
        
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
     * ENHANCED v5.0: Intelligent aerial pre-attack warning with scaling context
     */
    private void sendIntelligentAerialPreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME wind fury due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense aerial power due to scaling!)";
        }
        
        player.sendMessage("<col=8B0000>AERIAL WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent aerial attack pattern selection with scaling consideration
     */
    private AerialAttackPattern selectIntelligentAerialAttackPattern(int phase, CombatScaling scaling, int attackCount, boolean isAoE) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on phase, scaling, and progression
        int baseSpecialChance = (phase - 1) * 14; // 14% per phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 10 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 8 ? 6 : 0; // More specials as fight progresses
        int aoeBonus = isAoE ? 8 : 0; // More specials for AoE scenarios
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus + aoeBonus;
        
        // v5.0 intelligent pattern selection for aerial attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        if (isOvergeared) {
            // More aggressive aerial patterns for overgeared players
            if (roll < 10 + specialChance) return AERIAL_ATTACK_PATTERNS[3]; // Aerial supremacy
            if (roll < 25 + specialChance) return AERIAL_ATTACK_PATTERNS[2]; // Magic storm  
            if (roll < 40 + specialChance) return AERIAL_ATTACK_PATTERNS[1]; // Ranged barrage
        } else {
            // Standard aerial pattern selection
            if (roll < 6 + specialChance) return AERIAL_ATTACK_PATTERNS[3]; // Aerial supremacy
            if (roll < 18 + specialChance) return AERIAL_ATTACK_PATTERNS[2]; // Magic storm  
            if (roll < 32 + specialChance) return AERIAL_ATTACK_PATTERNS[1]; // Ranged barrage
        }
        
        // Default: melee dive if close, ranged if distant
        return AERIAL_ATTACK_PATTERNS[0]; // Melee dive
    }

    /**
     * ENHANCED v5.0: Execute aerial attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledAerialAttack(NPC npc, Player player, AerialAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced phase damage calculation with v5.0 intelligence
            int phase = getCurrentFlightPhase(npc);
            double phaseModifier = 1.0 + (phase - 1) * 0.12; // 12% per phase for aerial boss
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * phaseModifier);
            
            // Execute different aerial attack types with v5.0 scaling and HP-aware damage
            if ("aerial_supremacy".equals(pattern.name)) {
                executeIntelligentAerialSupremacy(npc, player, baseDamage, scaling);
            } else if ("magic_storm".equals(pattern.name)) {
                executeIntelligentMagicStorm(npc, player, baseDamage, scaling);
            } else if ("ranged_barrage".equals(pattern.name)) {
                executeIntelligentRangedBarrage(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleAerialAttack(npc, player, baseDamage, 0, scaling, "melee_dive");
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
            // Enhanced fallback - execute basic aerial attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleAerialAttack(npc, player, scaledDamage, 0, scaling, "melee_dive");
            }
        }
    }

    /**
     * ENHANCED v5.0: Execute AoE attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledAoEAttack(NPC npc, List<Entity> targets, AerialAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced phase damage calculation with v5.0 intelligence
            int phase = getCurrentFlightPhase(npc);
            double phaseModifier = 1.0 + (phase - 1) * 0.12; // 12% per phase
            
            // Calculate AoE damage reduction based on target count
            int targetCount = targets.size();
            double aoeReduction = Math.max(0.7, 1.0 - (targetCount * 0.08)); // Max 30% reduction
            
            // Process each target
            for (Entity target : targets) {
                if (target instanceof Player) {
                    Player targetPlayer = (Player) target;
                    
                    // Get individual scaling for each player
                    CombatScaling targetScaling = BossBalancer.getIntelligentCombatScaling(targetPlayer, npc);
                    
                    // Enhanced max hit calculation with v5.0 BossBalancer integration
                    int baseMaxHit = defs.getMaxHit();
                    int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, targetPlayer, npc);
                    int baseDamage = (int)(scaledMaxHit * phaseModifier * aoeReduction);
                    
                    // Execute AoE attack based on pattern
                    if ("aerial_supremacy".equals(pattern.name)) {
                        executeAoEAerialSupremacy(npc, targetPlayer, baseDamage, targetScaling);
                    } else if ("magic_storm".equals(pattern.name)) {
                        executeAoEMagicStorm(npc, targetPlayer, baseDamage, targetScaling);
                    } else {
                        executeAoERangedBarrage(npc, targetPlayer, baseDamage, targetScaling);
                    }
                }
            }
            
        } catch (Exception e) {
            // Enhanced fallback - execute basic AoE attack
            for (Entity target : targets) {
                if (target instanceof Player) {
                    Player targetPlayer = (Player) target;
                    NPCCombatDefinitions defs = npc.getCombatDefinitions();
                    if (defs != null) {
                        int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), targetPlayer, npc);
                        executeIntelligentSingleAerialAttack(npc, targetPlayer, scaledDamage, 0, scaling, "ranged_barrage");
                    }
                }
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent aerial supremacy attack with HP-aware scaling
     */
    private void executeIntelligentAerialSupremacy(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ultimate aerial attack - 170% damage with variance
        int damage = (int)(baseDamage * 1.7) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for ultimate attacks
        int safeDamage = applyHPAwareAerialDamageScaling(scaledDamage, player, "aerial_supremacy");
        checkAndWarnLowHPForAerial(player, safeDamage);
        
        // Aerial supremacy with projectile effects
        World.sendProjectile(npc, player, 1197, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 1, player, getRangeHit(npc, safeDamage));
        
        // Add wind graphics
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                player.setNextGraphics(new Graphics(1196));
                this.stop();
            }
        }, 1);
    }

    /**
     * ENHANCED v5.0: Intelligent magic storm attack with HP-aware scaling
     */
    private void executeIntelligentMagicStorm(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Enhanced magic storm damage (140% of base)
        int damage = (int)(baseDamage * 1.4) + Utils.random(baseDamage / 5);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for magic storms
        int safeDamage = applyHPAwareAerialDamageScaling(scaledDamage, player, "magic_storm");
        checkAndWarnLowHPForAerial(player, safeDamage);
        
        World.sendProjectile(npc, player, 1198, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 1, player, getMagicHit(npc, safeDamage));
        
        // Update consecutive ranged counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer consecutiveCount = consecutiveRangedAttacks.get(playerKey);
        if (consecutiveCount == null) consecutiveCount = 0;
        consecutiveRangedAttacks.put(playerKey, consecutiveCount + 1);
    }

    /**
     * NEW v5.0: Intelligent ranged barrage attack with HP-aware scaling
     */
    private void executeIntelligentRangedBarrage(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ranged barrage damage (130% base for aerial mastery)
        int damage = (int)(baseDamage * 1.3) + Utils.random(baseDamage / 6);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for ranged barrages
        int safeDamage = applyHPAwareAerialDamageScaling(scaledDamage, player, "ranged_barrage");
        checkAndWarnLowHPForAerial(player, safeDamage);
        
        World.sendProjectile(npc, player, 1197, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 1, player, getRangeHit(npc, safeDamage));
        
        // Update consecutive ranged counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer consecutiveCount = consecutiveRangedAttacks.get(playerKey);
        if (consecutiveCount == null) consecutiveCount = 0;
        consecutiveRangedAttacks.put(playerKey, consecutiveCount + 1);
    }

    /**
     * NEW v5.0: AoE Aerial Supremacy
     */
    private void executeAoEAerialSupremacy(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // AoE ultimate attack - 150% damage (reduced from single target)
        int damage = (int)(baseDamage * 1.5) + Utils.random(baseDamage / 5);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        int safeDamage = applyHPAwareAerialDamageScaling(scaledDamage, player, "aoe_combo");
        
        World.sendProjectile(npc, player, 1197, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 1, player, getRangeHit(npc, safeDamage));
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                player.setNextGraphics(new Graphics(1196));
                this.stop();
            }
        }, 1);
    }

    /**
     * NEW v5.0: AoE Magic Storm
     */
    private void executeAoEMagicStorm(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // AoE magic storm - 120% damage
        int damage = (int)(baseDamage * 1.2) + Utils.random(baseDamage / 6);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        int safeDamage = applyHPAwareAerialDamageScaling(scaledDamage, player, "wind_storm");
        
        World.sendProjectile(npc, player, 1198, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 1, player, getMagicHit(npc, safeDamage));
    }

    /**
     * NEW v5.0: AoE Ranged Barrage
     */
    private void executeAoERangedBarrage(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // AoE ranged barrage - 110% damage
        int damage = (int)(baseDamage * 1.1) + Utils.random(baseDamage / 7);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        int safeDamage = applyHPAwareAerialDamageScaling(scaledDamage, player, "ranged_barrage");
        
        World.sendProjectile(npc, player, 1197, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 1, player, getRangeHit(npc, safeDamage));
    }

    /**
     * ENHANCED v5.0: Intelligent single aerial attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleAerialAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareAerialDamageScaling(scaledDamage, player, attackType);
        if (!"melee_dive".equals(attackType)) {
            checkAndWarnLowHPForAerial(player, safeDamage);
        }
        
        // Determine attack type based on distance and type
        boolean isInMeleeRange = Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                               player.getX(), player.getY(), player.getSize(), 0);
        
        if (isInMeleeRange && "melee_dive".equals(attackType)) {
            // Melee dive attack
            delayHit(npc, delay, player, getMeleeHit(npc, safeDamage));
            
            // Reset consecutive ranged counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            consecutiveRangedAttacks.put(playerKey, 0);
        } else {
            // Ranged/magic aerial attack
            int projectileId = "magic_storm".equals(attackType) ? 1198 : 1197;
            World.sendProjectile(npc, player, projectileId, 41, 16, 41, 35, 16, 0);
            
            if ("magic_storm".equals(attackType)) {
                delayHit(npc, delay + 1, player, getMagicHit(npc, safeDamage));
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        player.setNextGraphics(new Graphics(1196));
                        this.stop();
                    }
                }, delay + 1);
            } else {
                delayHit(npc, delay + 1, player, getRangeHit(npc, safeDamage));
            }
            
            // Update consecutive ranged counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer consecutiveCount = consecutiveRangedAttacks.get(playerKey);
            if (consecutiveCount == null) consecutiveCount = 0;
            consecutiveRangedAttacks.put(playerKey, consecutiveCount + 1);
        }
        
        // Track if attack hit
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastAttackHit.put(playerKey, safeDamage > 0);
    }

    /**
     * ENHANCED v5.0: Intelligent aerial attack delay with scaling consideration
     */
    private int getIntelligentAerialAttackDelay(NPC npc, int phase, CombatScaling scaling) {
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) return 4;
        
        int baseDelay = defs.getAttackDelay();
        int phaseSpeedBonus = Math.max(0, phase - 1);
        
        // v5.0 intelligent scaling can affect attack speed for aerial boss
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = 1; // Faster for extreme scaling (wind moves faster)
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingSpeedBonus = 1; // Faster for high scaling
        }
        
        return Math.max(3, baseDelay - phaseSpeedBonus - scalingSpeedBonus);
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
     * ENHANCED v5.0: Handle aerial combat end with proper cleanup
     */
    public static void onAerialCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentFlightPhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            aerialManeuvers.remove(npcKey);
            consecutiveRangedAttacks.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastAttackHit.remove(playerKey);
            activeGroupTargets.remove(npcKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=4169E1>Aerial combat session ended. Wind scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("Kreearra: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during aerial combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Aerial scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("Kreearra: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onAerialCombatEnd(player, null);
        }
    }

    /**
     * Enhanced aerial attack pattern data structure
     */
    private static class AerialAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        AerialAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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