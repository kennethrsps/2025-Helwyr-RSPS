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
import com.rs.game.npc.glacor.Glacor;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;

/**
 * Enhanced Glacor Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Intelligent power-based scaling, armor analysis, HP-aware damage scaling, ice-themed mechanics
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 4.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System
 */
public class GlacorCombat extends CombatScript {

    // ===== ICE PHASE SYSTEM - Enhanced for v5.0 =====
    private static final double ICE_PHASE_2_THRESHOLD = 0.75;
    private static final double ICE_PHASE_3_THRESHOLD = 0.50; 
    private static final double ICE_PHASE_4_THRESHOLD = 0.25;

    // ===== ENHANCED GUIDANCE SYSTEM - Intelligent scaling aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentIcePhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> stylesSwitched = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 180000; // 3 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 3000; // 3 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - CRITICAL SAFETY SYSTEM =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.30; // Max 30% of player HP per hit (ice boss is safer)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.45;  // Max 45% for critical ice attacks
    private static final double FREEZE_COMBO_DAMAGE_PERCENT = 0.42; // Max 42% for freeze combos
    private static final double ICE_STORM_DAMAGE_PERCENT = 0.38;   // Max 38% for ice storms
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 450;          // Hard cap (30% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 25;               // Minimum damage to prevent 0 hits

    // ===== ICE-THEMED ATTACK PATTERNS with v5.0 intelligence =====
    private static final IceAttackPattern[] ICE_ATTACK_PATTERNS = {
        new IceAttackPattern(9955, 0, 0, "ice_shard", false, ""),
        new IceAttackPattern(9967, 634, 369, "frost_blast", true, "FROST BLAST incoming - high magic damage!"),
        new IceAttackPattern(9968, 962, 2315, "ice_storm", true, "ICE STORM incoming - move to avoid area damage!"),
        new IceAttackPattern(9955, 0, 369, "freezing_combo", true, "FREEZING COMBO incoming - prepare for freeze and damage!")
    };

    // ===== SAFE SPOT PREVENTION - Ice-themed =====
    private static final Map<Integer, Integer> consecutiveDistancedAttacks = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastAttackHit = new ConcurrentHashMap<Integer, Boolean>();

    @Override
    public Object[] getKeys() {
        return new Object[] { 14301 }; // Glacor NPC ID
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeGlacialCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentIceGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorIceScalingChanges(player, scaling);
        
        // Update phase tracking with v5.0 scaling
        updateIntelligentIcePhaseTracking(npc, scaling);
        
        // Check for ice-themed safe spotting
        checkGlacialSafeSpotting(player, npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentIceAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize glacial combat session using BossBalancer v5.0
     */
    private void initializeGlacialCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            stylesSwitched.put(sessionKey, Integer.valueOf(0));
            consecutiveDistancedAttacks.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastAttackHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced glacial combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentIceWelcomeMessage(scaling);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for ice combat
            performInitialGlacialArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial glacial armor analysis
     */
    private void performInitialGlacialArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=87CEEB>Glacial Analysis: Exposed areas detected. Ice magic will pierce deeper!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=00FFFF>Glacial Analysis: Full protection against frost (" + 
                                 String.format("%.1f", reductionPercent) + "% ice resistance).</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from ice attacks
     */
    private int applyHPAwareIceDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (more dangerous when wounded)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on ice attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "freezing_combo":
                case "ultimate_ice":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "ice_storm":
                    damagePercent = ICE_STORM_DAMAGE_PERCENT;
                    break;
                case "freeze_combo":
                    damagePercent = FREEZE_COMBO_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 80% of current HP for ice boss
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
     * NEW v5.0: Send HP warning if player is in danger from ice attacks
     */
    private void checkAndWarnLowHPForIce(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming ice damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.65) {
                    player.sendMessage("<col=ff0000>ICE WARNING: This frost attack will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.45) {
                    player.sendMessage("<col=87CEEB>ICE WARNING: Heavy frost damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent ice welcome message based on power analysis
     */
    private String getIntelligentIceWelcomeMessage(CombatScaling scaling) {
        StringBuilder message = new StringBuilder();
        message.append("<col=00FFFF>The ancient Glacor stirs, frost magic analyzing your power (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=4169E1>[Ice intensifies: +").append(diffIncrease).append("% frost power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=87CEEB>[Frost mercy: -").append(assistance).append("% ice damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=ADD8E6>[Ice resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=00CED1>[Full frost protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent ice guidance with power-based scaling awareness
     */
    private void provideIntelligentIceGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentIceGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent ice guidance message based on power analysis
     */
    private String getIntelligentIceGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentIcePhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getIceScalingAnalysisMessage(scaling);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=4169E1>Glacial Analysis: Missing armor leaves you vulnerable to ice! Frost damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME frost due to scaling!)" : "";
                    return "<col=87CEEB>Glacial Analysis: Deep freeze phase reached. Ice power dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=ffffff>Glacial Analysis: Absolute zero phase! Maximum ice power unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=4169E1>Glacial Analysis: Extreme ice scaling detected! Consider fighting higher-tier frost beings!</col>";
                }
                break;
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get ice scaling analysis message
     */
    private String getIceScalingAnalysisMessage(CombatScaling scaling) {
        String baseMessage = "<col=ADD8E6>Glacial Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=87CEEB>Frost mercy granted! Ice damage reduced by " + 
                   assistancePercent + "% due to insufficient cold resistance.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=4169E1>Ice intensification active! Frost power increased by " + 
                   difficultyIncrease + "% due to superior gear advantage.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=00FFFF>Balanced frost encounter. Optimal ice resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=00CED1>Slight gear advantage detected. Ice power increased by " + 
                   difficultyIncrease + "% for balance.</col>";
        }
        
        return baseMessage + "<col=E0E0E0>Frost power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during ice combat
     */
    private void monitorIceScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getIceScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get ice scaling change message
     */
    private String getIceScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=87CEEB>Glacial Update: Ice resistance improved to balanced! Frost mercy reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=4169E1>Glacial Update: Ice intensification now active due to increased power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=ADD8E6>Glacial Update: Ice resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=00FFFF>Glacial Update: Full frost protection restored! Ice damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent ice phase tracking with BossBalancer integration
     */
    private void updateIntelligentIcePhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentIcePhase(npc);
        
        Integer lastPhase = currentIcePhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentIcePhase.put(npcKey, newPhase);
            handleIntelligentIcePhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current ice phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentIcePhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= ICE_PHASE_4_THRESHOLD) return 4;
            if (hpPercent <= ICE_PHASE_3_THRESHOLD) return 3;
            if (hpPercent <= ICE_PHASE_2_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ice phase transitions with scaling integration
     */
    private void handleIntelligentIcePhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("The frost deepens..."));
            npc.setNextGraphics(new Graphics(369));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED ICE MAGIC ACTIVATED!" : "Feel the bite of eternal winter!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(634));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ABSOLUTE ZERO - ULTIMATE GLACIAL FORM!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED ABSOLUTE ZERO REACHED!" : "ABSOLUTE ZERO!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(2315));
            
            // Enhanced heal calculation with v5.0 scaling (ice regeneration)
            int baseHeal = npc.getMaxHitpoints() / 12; // More healing for ice boss
            int scaledHeal = (int)(baseHeal * scaling.bossHpMultiplier);
            npc.heal(Math.max(scaledHeal, 60));
            break;
        }
    }

    /**
     * NEW v5.0: Check for glacial safe spotting
     */
    private void checkGlacialSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveDistancedAttacks.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastAttackHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect ice-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean glacorStuckRanged = consecutiveCount > 6;
        boolean recentMiss = !lastHit;
        
        boolean icySafeSpot = playerDistanced && glacorStuckRanged && recentMiss;
        
        if (icySafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating ice-themed responses
            if (warningCount >= 3) {
                performGlacialAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=87CEEB>The ice magic subsides as you fight honorably...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform glacial anti-safe spot measure
     */
    private void performGlacialAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=4169E1>The ancient ice magic finds you no matter where you hide!</col>");
        
        // Ice magic that pierces obstacles
        npc.setNextAnimation(new Animation(9967));
        World.sendProjectile(npc, npc.getMiddleWorldTile(), player, 634, 50, 30, 50, 50, 5, 0);
        
        // Enhanced damage based on scaling
        int baseDamage = (int)(npc.getMaxHit() * 1.3);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareIceDamageScaling(scaledDamage, player, "ice_pierce");
        
        player.applyHit(new Hit(npc, safeDamage, HitLook.MAGIC_DAMAGE, 2));
        player.setFreezeDelay(2400); // 4 seconds freeze
        player.setNextGraphics(new Graphics(369));
        
        player.sendMessage("<col=00FFFF>ICE PENALTY: Safe spotting detected - piercing frost magic applied!</col>");
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent ice warning system
     */
    private int performIntelligentIceAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int phase = getCurrentIcePhase(npc);
            IceAttackPattern pattern = selectIntelligentIceAttackPattern(phase, scaling, currentCount);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentIceWarning(scaling, currentCount)) {
                sendIntelligentIcePreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledIceAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentIceAttackDelay(npc, phase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledIceAttack(npc, player, pattern, scaling);
                return getIntelligentIceAttackDelay(npc, phase, scaling);
            }
            
        } catch (Exception e) {
            return 4;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ice warning probability based on scaling
     */
    private boolean shouldGiveIntelligentIceWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing ice boss
        boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
        int warningFrequency = isUndergeared ? 5 : 7; // Every 5th vs 7th attack
        
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
     * ENHANCED v5.0: Intelligent ice pre-attack warning with scaling context
     */
    private void sendIntelligentIcePreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME frost due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense ice due to scaling!)";
        }
        
        player.sendMessage("<col=4169E1>ICE WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent ice attack pattern selection with scaling consideration
     */
    private IceAttackPattern selectIntelligentIceAttackPattern(int phase, CombatScaling scaling, int attackCount) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on phase, scaling, and progression
        int baseSpecialChance = (phase - 1) * 15; // Increased from 12 for ice boss
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 10 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 12 ? 6 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for ice attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        if (isOvergeared) {
            // More aggressive ice patterns for overgeared players
            if (roll < 10 + specialChance) return ICE_ATTACK_PATTERNS[3]; // Freezing combo
            if (roll < 25 + specialChance) return ICE_ATTACK_PATTERNS[2]; // Ice storm  
            if (roll < 40 + specialChance) return ICE_ATTACK_PATTERNS[1]; // Frost blast
        } else {
            // Standard ice pattern selection
            if (roll < 6 + specialChance) return ICE_ATTACK_PATTERNS[3]; // Freezing combo
            if (roll < 18 + specialChance) return ICE_ATTACK_PATTERNS[2]; // Ice storm  
            if (roll < 35 + specialChance) return ICE_ATTACK_PATTERNS[1]; // Frost blast
        }
        
        return ICE_ATTACK_PATTERNS[0]; // Ice shard (basic)
    }

    /**
     * ENHANCED v5.0: Execute ice attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledIceAttack(NPC npc, Player player, IceAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced phase damage calculation with v5.0 intelligence
            int phase = getCurrentIcePhase(npc);
            double phaseModifier = 1.0 + (phase - 1) * 0.16; // 16% per phase for ice boss
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * phaseModifier);
            
            // Execute different ice attack types with v5.0 scaling and HP-aware damage
            if ("freezing_combo".equals(pattern.name)) {
                executeIntelligentFreezingCombo(npc, player, baseDamage, scaling);
            } else if ("ice_storm".equals(pattern.name)) {
                executeIntelligentIceStorm(npc, player, baseDamage, scaling);
            } else if ("frost_blast".equals(pattern.name)) {
                executeIntelligentFrostBlast(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleIceAttack(npc, player, baseDamage, 0, scaling, "ice_shard");
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
            // Enhanced fallback - execute basic ice attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleIceAttack(npc, player, scaledDamage, 0, scaling, "ice_shard");
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent freezing combo attack with HP-aware scaling
     */
    private void executeIntelligentFreezingCombo(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ice magic hit - 60% damage with variance
        int damage1 = (int)(baseDamage * 0.60) + Utils.random(baseDamage / 6);
        int scaledDamage1 = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage1);
        
        // CRITICAL: Apply HP-aware damage scaling for first hit
        int safeDamage1 = applyHPAwareIceDamageScaling(scaledDamage1, player, "freezing_combo");
        checkAndWarnLowHPForIce(player, safeDamage1);
        
        // Freeze effect hit - 75% damage
        int damage2 = (int)(baseDamage * 0.75) + Utils.random(baseDamage / 6);
        int scaledDamage2 = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage2);
        
        // CRITICAL: Apply HP-aware damage scaling for second hit
        int safeDamage2 = applyHPAwareIceDamageScaling(scaledDamage2, player, "freezing_combo");
        
        delayHit(npc, 0, player, getMagicHit(npc, safeDamage1));
        delayHit(npc, 2, player, getMagicHit(npc, safeDamage2));
        
        // Apply enhanced freeze effect
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                player.setFreezeDelay(3000); // 5 seconds freeze
                player.setNextGraphics(new Graphics(369));
                this.stop();
            }
        }, 2);
    }

    /**
     * ENHANCED v5.0: Intelligent ice storm attack with HP-aware scaling
     */
    private void executeIntelligentIceStorm(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Enhanced ice storm damage (increased from 1.3x to 1.4x)
        int damage = (int)(baseDamage * 1.4) + Utils.random(baseDamage / 5);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for ice storms
        int safeDamage = applyHPAwareIceDamageScaling(scaledDamage, player, "ice_storm");
        checkAndWarnLowHPForIce(player, safeDamage);
        
        // Area effect - check if player moved
        final WorldTile originalTile = new WorldTile(player);
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                if (player.matches(originalTile)) {
                    // Full damage if didn't move
                    delayHit(npc, 0, player, getRangeHit(npc, safeDamage));
                } else {
                    // Reduced damage if moved
                    int reducedDamage = safeDamage / 3;
                    delayHit(npc, 0, player, getRangeHit(npc, reducedDamage));
                    player.sendMessage("<col=87CEEB>You partially avoid the ice storm by moving!</col>");
                }
                this.stop();
            }
        }, 2);
    }

    /**
     * NEW v5.0: Intelligent frost blast attack with HP-aware scaling
     */
    private void executeIntelligentFrostBlast(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Frost blast damage (1.25x base)
        int damage = (int)(baseDamage * 1.25) + Utils.random(baseDamage / 7);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for frost blasts
        int safeDamage = applyHPAwareIceDamageScaling(scaledDamage, player, "frost_blast");
        checkAndWarnLowHPForIce(player, safeDamage);
        
        delayHit(npc, 2, player, getMagicHit(npc, safeDamage));
        
        // Enhanced prayer drain for frost blast
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                int prayerDrain = Math.max(15, safeDamage / 8);
                player.getPrayer().drainPrayer(prayerDrain);
                player.sendMessage("<col=ADD8E6>The frost blast drains your divine protection!</col>");
                this.stop();
            }
        }, 2);
    }

    /**
     * ENHANCED v5.0: Intelligent single ice attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleIceAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        // Determine if this is a ranged or magic attack based on Glacor state
        boolean isRangedAttack = false;
        if (npc instanceof Glacor) {
            Glacor glacor = (Glacor) npc;
            isRangedAttack = glacor.isRangeAttack();
            
            // Update consecutive distanced attacks counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer consecutiveCount = consecutiveDistancedAttacks.get(playerKey);
            if (consecutiveCount == null) consecutiveCount = 0;
            consecutiveDistancedAttacks.put(playerKey, consecutiveCount + 1);
        }
        
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareIceDamageScaling(scaledDamage, player, attackType);
        if (!"ice_shard".equals(attackType)) {
            checkAndWarnLowHPForIce(player, safeDamage);
        }
        
        if (isRangedAttack) {
            // Ice shard ranged attack
            World.sendProjectile(npc, npc.getMiddleWorldTile(), player, 962, 50, 30, 50, 50, 0, 0);
            delayHit(npc, delay + 2, player, getRangeHit(npc, safeDamage));
        } else {
            // Frost magic attack
            World.sendProjectile(npc, npc.getMiddleWorldTile(), player, 634, 50, 30, 50, 50, 5, 0);
            delayHit(npc, delay + 2, player, getMagicHit(npc, safeDamage));
            
            // Chance to freeze with magic attacks
            if (Utils.random(100) < 25) { // 25% freeze chance
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        player.setFreezeDelay(1500); // 2.5 seconds
                        player.setNextGraphics(new Graphics(369));
                        this.stop();
                    }
                }, delay + 2);
            }
        }
        
        // Track if attack hit
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastAttackHit.put(playerKey, safeDamage > 0);
    }

    /**
     * ENHANCED v5.0: Intelligent ice attack delay with scaling consideration
     */
    private int getIntelligentIceAttackDelay(NPC npc, int phase, CombatScaling scaling) {
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) return 4;
        
        int baseDelay = defs.getAttackDelay();
        int phaseSpeedBonus = Math.max(0, phase - 1);
        
        // v5.0 intelligent scaling can affect attack speed for ice boss
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = 1; // Faster for extreme scaling (ice moves faster)
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
               npc.getCombatDefinitions() != null &&
               npc instanceof Glacor;
    }

    /**
     * ENHANCED v5.0: Handle ice combat end with proper cleanup
     */
    public static void onIceCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentIcePhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            stylesSwitched.remove(playerKey);
            consecutiveDistancedAttacks.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastAttackHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=00FFFF>Glacial combat session ended. Ice scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("GlacorCombat: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during ice combat
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
                player.sendMessage("<col=ADD8E6>Prayer change detected. Glacial scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("GlacorCombat: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onIceCombatEnd(player, null);
        }
    }

    /**
     * Enhanced ice attack pattern data structure
     */
    private static class IceAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        IceAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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