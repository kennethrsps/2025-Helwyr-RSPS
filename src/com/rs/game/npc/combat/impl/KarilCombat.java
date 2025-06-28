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
import com.rs.utils.NPCBonuses;
import com.rs.cache.loaders.NPCDefinitions;

/**
 * Enhanced Karil the Tainted Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Intelligent crossbow attacks, agility drain mechanics, anti-safespot system, HP-aware damage scaling
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 3.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System
 */
public class KarilCombat extends CombatScript {

    // ===== RANGED COMBAT PHASES - Enhanced for v5.0 =====
    private static final double ACCURACY_THRESHOLD = 0.85;   // 85% accuracy - normal phase
    private static final double RAPID_FIRE_THRESHOLD = 0.65; // 65% HP - rapid fire begins
    private static final double DEADLY_AIM_THRESHOLD = 0.35;  // 35% HP - deadly aim mode
    private static final double DESPERATION_THRESHOLD = 0.15; // 15% HP - desperation mode

    // ===== ENHANCED GUIDANCE SYSTEM - Intelligent scaling aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentRangedPhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> agilityDrainCount = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 180000; // 3 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 2500; // 2.5 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - CRITICAL SAFETY SYSTEM =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.28; // Max 28% of player HP per hit (ranged is precise)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.38;  // Max 38% for critical ranged attacks
    private static final double RAPID_FIRE_DAMAGE_PERCENT = 0.32; // Max 32% for rapid fire
    private static final double DESPERATION_DAMAGE_PERCENT = 0.45; // Max 45% for desperation mode
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 420;          // Hard cap (28% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 28;               // Minimum damage to prevent 0 hits

    // ===== AGILITY DRAIN MECHANICS =====
    private static final int AGILITY_DRAIN_THRESHOLD = 4; // 1 in 4 chance
    private static final double AGILITY_DRAIN_PERCENTAGE = 0.15; // 15% drain per hit
    private static final int MAX_AGILITY_DRAINS_PER_FIGHT = 8; // Prevent excessive draining

    // ===== RANGED ATTACK PATTERNS with v5.0 intelligence =====
    private static final RangedAttackPattern[] RANGED_ATTACK_PATTERNS = {
        new RangedAttackPattern(4199, 0, 0, "crossbow_bolt", false, ""),
        new RangedAttackPattern(4199, 0, 0, "rapid_fire", true, "RAPID FIRE incoming - multiple crossbow bolts!"),
        new RangedAttackPattern(4199, 0, 0, "deadly_aim", true, "DEADLY AIM incoming - precision crossbow strike!"),
        new RangedAttackPattern(4199, 0, 0, "desperation_barrage", true, "DESPERATION BARRAGE incoming - final crossbow assault!")
    };

    // ===== SAFE SPOT PREVENTION - Ranged-themed =====
    private static final Map<Integer, Integer> consecutiveMissedShots = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastShotHit = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, Long> lastSafespotCheck = new ConcurrentHashMap<Integer, Long>();
    private static final long SAFESPOT_CHECK_INTERVAL = 3000; // 3 seconds

    @Override
    public Object[] getKeys() {
        return new Object[] { 2028 }; // Karil the Tainted
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeRangedCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentRangedGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorRangedScalingChanges(player, scaling);
        
        // Update ranged phase tracking with v5.0 scaling
        updateIntelligentRangedPhaseTracking(npc, scaling);
        
        // Check for ranged-themed safe spotting
        checkRangedSafeSpotting(player, npc, scaling);
        
        // Enhanced ranged taunts with scaling-based frequency
        performEnhancedRangedTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentRangedAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize ranged combat session using BossBalancer v5.0
     */
    private void initializeRangedCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            agilityDrainCount.put(sessionKey, Integer.valueOf(0));
            consecutiveMissedShots.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastShotHit.put(sessionKey, Boolean.TRUE);
            lastSafespotCheck.put(sessionKey, Long.valueOf(0L));
            
            // Send v5.0 enhanced ranged combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentRangedWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for ranged combat
            performInitialRangedArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial ranged armor analysis
     */
    private void performInitialRangedArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=8B4513>Crossbow Analysis: Gaps in armor detected. My bolts will find their mark!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=FF4500>Crossbow Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance). I'll adjust my aim...</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from ranged attacks
     */
    private int applyHPAwareRangedDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (ranged attacks are precise)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on ranged attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "desperation_barrage":
                case "final_shot":
                    damagePercent = DESPERATION_DAMAGE_PERCENT;
                    break;
                case "deadly_aim":
                case "precision_shot":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "rapid_fire":
                case "multi_shot":
                    damagePercent = RAPID_FIRE_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 75% of current HP for ranged
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
     * NEW v5.0: Send HP warning if player is in danger from ranged attacks
     */
    private void checkAndWarnLowHPForRanged(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming ranged damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.65) {
                    player.sendMessage("<col=ff0000>RANGED WARNING: This crossbow bolt will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.45) {
                    player.sendMessage("<col=8B4513>RANGED WARNING: Heavy crossbow damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent ranged welcome message based on power analysis
     */
    private String getIntelligentRangedWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Karil";
        
        message.append("<col=8B4513>").append(npcName).append(" readies his crossbow, analyzing your defenses (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=FF4500>[Crossbow precision: +").append(diffIncrease).append("% accuracy power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Restrained aim: -").append(assistance).append("% crossbow damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Ranged resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full ranged protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent ranged guidance with power-based scaling awareness
     */
    private void provideIntelligentRangedGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentRangedGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent ranged guidance message based on power analysis
     */
    private String getIntelligentRangedGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentRangedPhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getRangedScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=FF4500>Ranged Analysis: Missing armor exposes you to crossbow bolts! Ranged damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME precision due to scaling!)" : "";
                    return "<col=8B4513>Ranged Analysis: Deadly aim phase reached. Accuracy dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=660000>Ranged Analysis: Desperation mode! Maximum crossbow precision unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=FF4500>Ranged Analysis: Extreme precision scaling detected! Consider fighting stronger opponents!</col>";
                }
                break;
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get ranged scaling analysis message
     */
    private String getRangedScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Karil";
        
        String baseMessage = "<col=DDA0DD>Crossbow Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>" + npcName + "'s aim restrained! Crossbow damage reduced by " + 
                   assistancePercent + "% due to insufficient combat prowess.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF4500>" + npcName + "'s precision escalated! Crossbow power increased by " + 
                   difficultyIncrease + "% due to superior gear advantage.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=8B4513>Balanced ranged encounter. Optimal crossbow resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight gear advantage detected. " + npcName + "'s aim intensity increased by " + 
                   difficultyIncrease + "% for balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Crossbow power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during ranged combat
     */
    private void monitorRangedScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getRangedScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get ranged scaling change message
     */
    private String getRangedScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Crossbow Update: Combat prowess improved to balanced! Aim restraint reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=FF4500>Crossbow Update: Precision escalation now active due to increased power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Crossbow Update: Ranged resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=8B4513>Crossbow Update: Full protection restored! Ranged damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent ranged phase tracking with BossBalancer integration
     */
    private void updateIntelligentRangedPhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentRangedPhase(npc);
        
        Integer lastPhase = currentRangedPhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentRangedPhase.put(npcKey, newPhase);
            handleIntelligentRangedPhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current ranged phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentRangedPhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= DESPERATION_THRESHOLD) return 4;
            if (hpPercent <= DEADLY_AIM_THRESHOLD) return 3;
            if (hpPercent <= RAPID_FIRE_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ranged phase transitions with scaling integration
     */
    private void handleIntelligentRangedPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "The crossbow master";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("My aim grows more precise!"));
            npc.setNextGraphics(new Graphics(206));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED CROSSBOW PRECISION UNLEASHED!" : "Feel my deadly aim!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(206));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE DESPERATION - MAXIMUM CROSSBOW POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED DESPERATION MODE!" : "Death approaches! I'll take you with me!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(206));
            break;
        }
    }

    /**
     * NEW v5.0: Check for ranged safe spotting
     */
    private void checkRangedSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        long currentTime = System.currentTimeMillis();
        
        // Check safespot detection interval
        Long lastCheck = lastSafespotCheck.get(playerKey);
        if (lastCheck != null && (currentTime - lastCheck) < SAFESPOT_CHECK_INTERVAL) {
            return;
        }
        lastSafespotCheck.put(playerKey, currentTime);
        
        // Get tracking values
        Integer consecutiveCount = consecutiveMissedShots.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastShotHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect ranged-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean crossbowFrustrated = consecutiveCount > 3; // Crossbow users get frustrated with missed shots
        boolean recentMiss = !lastHit;
        boolean cannotReachWithProjectile = !npc.clipedProjectile(player, true);
        
        boolean rangedSafeSpot = playerDistanced && crossbowFrustrated && (recentMiss || cannotReachWithProjectile);
        
        if (rangedSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating ranged-themed responses
            if (warningCount >= 3) {
                performRangedAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=8B4513>Karil's crossbow aim steadies as you engage in fair combat...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform ranged anti-safe spot measure
     */
    private void performRangedAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=FF4500>Karil's crossbow finds those who hide from honorable combat!</col>");
        
        // Crossbow shot that pierces through obstacles
        npc.setNextAnimation(new Animation(4199));
        npc.setNextForceTalk(new ForceTalk("COWARD! My bolts will find you!"));
        
        // Enhanced damage based on scaling
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.6) : 150; // Crossbow precision shot
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareRangedDamageScaling(scaledDamage, player, "piercing_shot");
        
        // Send projectile that ignores obstacles
        World.sendProjectile(npc, player, 27, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 2, player, getRangeHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>CROSSBOW PENALTY: Safe spotting detected - piercing shot breaks through!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced ranged taunts with scaling-based frequency
     */
    private void performEnhancedRangedTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on ranged phase and scaling
        int rangedPhase = getCurrentRangedPhase(npc);
        int tauntChance = 8 + (rangedPhase * 4); // Base 12% to 24% based on phase
        
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance += 12; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance += 8; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced ranged taunts based on phase and scaling
            performScaledRangedTaunt(npc, rangedPhase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled ranged taunt based on phase and scaling
     */
    private void performScaledRangedTaunt(NPC npc, int rangedPhase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "My aim is true!",
            "These bolts never miss!",
            "Face my crossbow!",
            "You cannot dodge forever!",
            "My shots find their mark!",
            "Feel the sting of my bolts!",
            "Distance means nothing!"
        };
        
        String[] precisionTaunts = {
            "MY CROSSBOW NEVER MISSES!",
            "PRECISION BEYOND MORTAL SKILL!",
            "EVERY SHOT FINDS ITS TARGET!",
            "WITNESS TRUE MARKSMANSHIP!",
            "NO ARMOR CAN STOP MY BOLTS!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED CROSSBOW PRECISION ACTIVATED!",
            "YOUR SUPERIOR GEAR SHARPENS MY AIM!",
            "MAXIMUM RANGED ACCURACY UNLEASHED!",
            "ULTIMATE MARKSMAN'S FOCUS!",
            "TRANSCENDENT CROSSBOW MASTERY!"
        };
        
        String selectedTaunt;
        if (isHighScaling && rangedPhase >= 3) {
            // Use enhanced taunts for high scaling + high precision
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else if (rangedPhase >= 2) {
            // Use precision taunts for high precision phases
            selectedTaunt = precisionTaunts[Utils.random(precisionTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent ranged warning system
     */
    private int performIntelligentRangedAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int rangedPhase = getCurrentRangedPhase(npc);
            RangedAttackPattern pattern = selectIntelligentRangedAttackPattern(rangedPhase, scaling, currentCount);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentRangedWarning(scaling, currentCount)) {
                sendIntelligentRangedPreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledRangedAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentRangedAttackDelay(npc, rangedPhase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledRangedAttack(npc, player, pattern, scaling);
                return getIntelligentRangedAttackDelay(npc, rangedPhase, scaling);
            }
            
        } catch (Exception e) {
            return 4;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ranged warning probability based on scaling
     */
    private boolean shouldGiveIntelligentRangedWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing ranged attacks
        boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
        int warningFrequency = isUndergeared ? 4 : 6; // Every 4th vs 6th attack
        
        if (attackCount % warningFrequency != 0) return false;
        
        // Enhanced warning probability based on scaling
        if (scaling.bossDamageMultiplier > 3.0) {
            return Utils.random(2) == 0; // 50% chance for extreme difficulty
        } else if (scaling.bossDamageMultiplier > 1.8) {
            return Utils.random(3) == 0; // 33% chance for high difficulty
        } else {
            return Utils.random(5) == 0; // 20% chance for normal difficulty
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ranged pre-attack warning with scaling context
     */
    private void sendIntelligentRangedPreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME precision due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense accuracy due to scaling!)";
        }
        
        player.sendMessage("<col=FF4500>CROSSBOW WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent ranged attack pattern selection with scaling consideration
     */
    private RangedAttackPattern selectIntelligentRangedAttackPattern(int rangedPhase, CombatScaling scaling, int attackCount) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on ranged phase, scaling, and progression
        int baseSpecialChance = (rangedPhase - 1) * 15; // 15% per ranged phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 12 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 10 ? 6 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for ranged attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        if (isOvergeared) {
            // More aggressive ranged patterns for overgeared players
            if (roll < 10 + specialChance) return RANGED_ATTACK_PATTERNS[3]; // Desperation barrage
            if (roll < 25 + specialChance) return RANGED_ATTACK_PATTERNS[2]; // Deadly aim  
            if (roll < 45 + specialChance) return RANGED_ATTACK_PATTERNS[1]; // Rapid fire
        } else {
            // Standard ranged pattern selection
            if (roll < 6 + specialChance) return RANGED_ATTACK_PATTERNS[3]; // Desperation barrage
            if (roll < 18 + specialChance) return RANGED_ATTACK_PATTERNS[2]; // Deadly aim  
            if (roll < 35 + specialChance) return RANGED_ATTACK_PATTERNS[1]; // Rapid fire
        }
        
        return RANGED_ATTACK_PATTERNS[0]; // Crossbow bolt
    }

    /**
     * ENHANCED v5.0: Execute ranged attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledRangedAttack(NPC npc, Player player, RangedAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced precision damage calculation with v5.0 intelligence
            int rangedPhase = getCurrentRangedPhase(npc);
            double precisionModifier = 1.0 + (rangedPhase - 1) * 0.18; // 18% per ranged phase (crossbow precision)
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * precisionModifier);
            
            // Execute different ranged attack types with v5.0 scaling and HP-aware damage
            if ("desperation_barrage".equals(pattern.name)) {
                executeIntelligentDesperationBarrage(npc, player, baseDamage, scaling);
            } else if ("deadly_aim".equals(pattern.name)) {
                executeIntelligentDeadlyAim(npc, player, baseDamage, scaling);
            } else if ("rapid_fire".equals(pattern.name)) {
                executeIntelligentRapidFire(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleRangedAttack(npc, player, baseDamage, 2, scaling, "crossbow_bolt");
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
                }, 2);
            }
            
        } catch (Exception e) {
            // Enhanced fallback - execute basic ranged attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleRangedAttack(npc, player, scaledDamage, 2, scaling, "crossbow_bolt");
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent desperation barrage attack with HP-aware scaling
     */
    private void executeIntelligentDesperationBarrage(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ultimate ranged attack - multiple shots (3 shots at 60%, 70%, 80% damage)
        for (int i = 0; i < 3; i++) {
            int shotDamage = (int)(baseDamage * (0.6 + (i * 0.1))) + Utils.random(baseDamage / 4);
            int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, shotDamage);
            
            // CRITICAL: Apply HP-aware damage scaling for ultimate attacks
            int safeDamage = applyHPAwareRangedDamageScaling(scaledDamage, player, "desperation_barrage");
            if (i == 0) checkAndWarnLowHPForRanged(player, safeDamage * 3); // Warn for total damage
            
            // Send projectile and delay hit
            World.sendProjectile(npc, player, 27, 41, 16, 41, 35, 16, 0);
            delayHit(npc, 2 + i, player, getRangeHit(npc, safeDamage));
        }
        
        // Update attack counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer shotCount = attackCounter.get(playerKey);
        if (shotCount == null) shotCount = 0;
        attackCounter.put(playerKey, shotCount + 3);
    }

    /**
     * ENHANCED v5.0: Intelligent deadly aim attack with HP-aware scaling
     */
    private void executeIntelligentDeadlyAim(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Deadly aim damage (170% base for enhanced crossbow precision)
        int damage = (int)(baseDamage * 1.7) + Utils.random(baseDamage / 3);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for deadly aim
        int safeDamage = applyHPAwareRangedDamageScaling(scaledDamage, player, "deadly_aim");
        checkAndWarnLowHPForRanged(player, safeDamage);
        
        // Enhanced projectile
        World.sendProjectile(npc, player, 27, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 2, player, getRangeHit(npc, safeDamage));
        
        // Apply agility drain with deadly aim
        if (safeDamage > 0) {
            applyEnhancedAgilityDrain(npc, player, safeDamage, scaling);
        }
    }

    /**
     * ENHANCED v5.0: Intelligent rapid fire attack with HP-aware scaling
     */
    private void executeIntelligentRapidFire(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Rapid fire - 2 shots at 75% and 85% damage
        int damage1 = (int)(baseDamage * 0.75) + Utils.random(baseDamage / 5);
        int scaledDamage1 = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage1);
        
        // CRITICAL: Apply HP-aware damage scaling for first shot
        int safeDamage1 = applyHPAwareRangedDamageScaling(scaledDamage1, player, "rapid_fire");
        
        int damage2 = (int)(baseDamage * 0.85) + Utils.random(baseDamage / 5);
        int scaledDamage2 = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage2);
        
        // CRITICAL: Apply HP-aware damage scaling for second shot
        int safeDamage2 = applyHPAwareRangedDamageScaling(scaledDamage2, player, "rapid_fire");
        
        checkAndWarnLowHPForRanged(player, safeDamage1 + safeDamage2);
        
        // Send projectiles
        World.sendProjectile(npc, player, 27, 41, 16, 41, 35, 16, 0);
        World.sendProjectile(npc, player, 27, 41, 16, 41, 35, 16, 0);
        
        delayHit(npc, 2, player, getRangeHit(npc, safeDamage1));
        delayHit(npc, 3, player, getRangeHit(npc, safeDamage2));
        
        // Update attack counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer shotCount = attackCounter.get(playerKey);
        if (shotCount == null) shotCount = 0;
        attackCounter.put(playerKey, shotCount + 2);
    }

    /**
     * ENHANCED v5.0: Intelligent single ranged attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleRangedAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareRangedDamageScaling(scaledDamage, player, attackType);
        if (!"crossbow_bolt".equals(attackType)) {
            checkAndWarnLowHPForRanged(player, safeDamage);
        }
        
        // Send projectile
        World.sendProjectile(npc, player, 27, 41, 16, 41, 35, 16, 0);
        delayHit(npc, delay, player, getRangeHit(npc, safeDamage));
        
        // Apply agility drain chance
        if (safeDamage > 0 && Utils.random(AGILITY_DRAIN_THRESHOLD) == 0) {
            applyEnhancedAgilityDrain(npc, player, safeDamage, scaling);
        }
        
        // Track if attack hit
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastShotHit.put(playerKey, safeDamage > 0);
        
        // Update missed shots counter
        Integer missedCount = consecutiveMissedShots.get(playerKey);
        if (missedCount == null) missedCount = 0;
        if (safeDamage <= 0) {
            consecutiveMissedShots.put(playerKey, missedCount + 1);
        } else {
            consecutiveMissedShots.put(playerKey, 0);
        }
    }

    /**
     * ENHANCED v5.0: Apply enhanced agility drain with visual effects and HP-aware limits
     */
    private void applyEnhancedAgilityDrain(NPC npc, Player player, int damage, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer drainCount = agilityDrainCount.get(playerKey);
        if (drainCount == null) drainCount = 0;
        
        // Limit agility drains per fight
        if (drainCount >= MAX_AGILITY_DRAINS_PER_FIGHT) {
            return;
        }
        
        player.setNextGraphics(new Graphics(401, 0, 100));
        
        int currentAgility = player.getSkills().getLevel(Skills.AGILITY);
        int maxAgility = player.getSkills().getLevelForXp(Skills.AGILITY);
        
        // Scale drain based on scaling difficulty
        double drainMultiplier = scaling.bossDamageMultiplier > 1.5 ? 1.3 : 1.0;
        int drain = (int)(maxAgility * AGILITY_DRAIN_PERCENTAGE * drainMultiplier);
        
        // Apply drain with minimum protection
        int newLevel = Math.max(0, currentAgility - drain);
        player.getSkills().set(Skills.AGILITY, newLevel);
        
        // Provide feedback to player
        player.sendMessage("<col=ff0000>Karil's crossbow bolts weaken your agility!");
        npc.setNextForceTalk(new ForceTalk("Your movements grow sluggish!"));
        
        // Additional guidance based on drain
        if (newLevel <= maxAgility * 0.3) {
            player.sendMessage("<col=ffff00>Hint: Restore your agility or your movement will be severely impaired!</col>");
        }
        
        // Update drain counter
        agilityDrainCount.put(playerKey, drainCount + 1);
    }

    /**
     * ENHANCED v5.0: Intelligent ranged attack delay with scaling consideration
     */
    private int getIntelligentRangedAttackDelay(NPC npc, int rangedPhase, CombatScaling scaling) {
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) return 4;
        
        int baseDelay = defs.getAttackDelay();
        int precisionSpeedBonus = Math.max(0, rangedPhase - 1); // Precision makes ranged attacks faster
        
        // v5.0 intelligent scaling can affect attack speed for ranged
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = 1; // Faster for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingSpeedBonus = 1; // Slightly faster for high scaling
        }
        
        return Math.max(3, baseDelay - precisionSpeedBonus - scalingSpeedBonus);
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
     * ENHANCED v5.0: Handle ranged combat end with proper cleanup
     */
    public static void onRangedCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentRangedPhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            agilityDrainCount.remove(playerKey);
            consecutiveMissedShots.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastShotHit.remove(playerKey);
            lastSafespotCheck.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=8B4513>Ranged combat session ended. Crossbow scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("KarilCombat: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during ranged combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Crossbow scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("KarilCombat: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onRangedCombatEnd(player, null);
        }
    }

    /**
     * Enhanced ranged attack pattern data structure
     */
    private static class RangedAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        RangedAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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