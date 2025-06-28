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
import com.rs.utils.NPCBonuses;

/**
 * Enhanced Commander Zilyana Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Intelligent power-based scaling, armor analysis, HP-aware damage scaling, divine hybrid mechanics
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 4.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System
 */
public class CommanderZilyanaCombat extends CombatScript {

    // ===== DIVINE COMBAT PHASES - Enhanced for v5.0 =====
    private static final double BLESSING_PHASE_THRESHOLD = 0.75;
    private static final double DIVINE_WRATH_THRESHOLD = 0.50; 
    private static final double FINAL_JUDGMENT_THRESHOLD = 0.25;

    // ===== ENHANCED GUIDANCE SYSTEM - Intelligent scaling aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentDivinePhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> aoeSpellCount = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> consecutiveMisses = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 180000; // 3 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 3000; // 3 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - CRITICAL SAFETY SYSTEM =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.30; // Max 30% of player HP per hit (divine is precise)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.45;  // Max 45% for critical divine attacks
    private static final double AOE_DAMAGE_PERCENT = 0.36;       // Max 36% for divine AoE spells
    private static final double DIVINE_COMBO_DAMAGE_PERCENT = 0.40; // Max 40% for divine combos
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 450;          // Hard cap (30% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 25;               // Minimum damage to prevent 0 hits

    // ===== DIVINE ATTACK PATTERNS with v5.0 intelligence =====
    private static final DivineAttackPattern[] DIVINE_ATTACK_PATTERNS = {
        new DivineAttackPattern(6967, 0, 0, "melee_strike", false, ""),
        new DivineAttackPattern(6967, 1194, 0, "divine_aoe", true, "DIVINE AOE incoming - area magic damage!"),
        new DivineAttackPattern(6967, 1194, 0, "saradomin_blessing", true, "SARADOMIN'S BLESSING incoming - enhanced divine power!"),
        new DivineAttackPattern(6967, 1194, 0, "final_judgment", true, "FINAL JUDGMENT incoming - ultimate divine wrath!")
    };

    // ===== SAFE SPOT PREVENTION - Divine-themed =====
    private static final Map<Integer, Integer> consecutiveAoeAttacks = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastAttackHit = new ConcurrentHashMap<Integer, Boolean>();

    @Override
    public Object[] getKeys() {
        return new Object[] { 6247 }; // Commander Zilyana NPC ID
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeDivineCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentDivineGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorDivineScalingChanges(player, scaling);
        
        // Update phase tracking with v5.0 scaling
        updateIntelligentDivinePhaseTracking(npc, scaling);
        
        // Check for divine-themed safe spotting
        checkDivineSafeSpotting(player, npc, scaling);
        
        // Enhanced divine proclamations with scaling-based frequency
        performEnhancedDivineProclamations(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentDivineAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize divine combat session using BossBalancer v5.0
     */
    private void initializeDivineCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            aoeSpellCount.put(sessionKey, Integer.valueOf(0));
            consecutiveMisses.put(sessionKey, Integer.valueOf(0));
            consecutiveAoeAttacks.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastAttackHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced divine combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentDivineWelcomeMessage(scaling);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for divine combat
            performInitialDivineArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial divine armor analysis
     */
    private void performInitialDivineArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=4169E1>Divine Analysis: Exposed areas detected. Saradomin's light will find weaknesses!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=00FFFF>Divine Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% divine resistance).</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from divine attacks
     */
    private int applyHPAwareDivineDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (more dangerous when wounded)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on divine attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "final_judgment":
                case "ultimate_divine":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "divine_combo":
                case "saradomin_blessing":
                    damagePercent = DIVINE_COMBO_DAMAGE_PERCENT;
                    break;
                case "divine_aoe":
                case "aoe_spell":
                    damagePercent = AOE_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 75% of current HP for divine boss
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
     * NEW v5.0: Send HP warning if player is in danger from divine attacks
     */
    private void checkAndWarnLowHPForDivine(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming divine damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.60) {
                    player.sendMessage("<col=ff0000>DIVINE WARNING: This holy attack will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.40) {
                    player.sendMessage("<col=4169E1>DIVINE WARNING: Heavy divine damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent divine welcome message based on power analysis
     */
    private String getIntelligentDivineWelcomeMessage(CombatScaling scaling) {
        StringBuilder message = new StringBuilder();
        message.append("<col=4169E1>Commander Zilyana raises her blade, divine light analyzing your righteousness (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=FF0000>[Divine wrath: +").append(diffIncrease).append("% holy power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Divine mercy: -").append(assistance).append("% divine damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Divine resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full divine protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent divine guidance with power-based scaling awareness
     */
    private void provideIntelligentDivineGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentDivineGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent divine guidance message based on power analysis
     */
    private String getIntelligentDivineGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentDivinePhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getDivineScalingAnalysisMessage(scaling);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=FF0000>Divine Analysis: Missing armor exposes you to holy light! Divine damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME divine wrath due to scaling!)" : "";
                    return "<col=4169E1>Divine Analysis: Final judgment phase reached. Divine power dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=00FFFF>Divine Analysis: Ultimate divine phase! Maximum holy power unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=FF0000>Divine Analysis: Extreme divine scaling detected! Consider fighting stronger holy beings!</col>";
                }
                break;
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get divine scaling analysis message
     */
    private String getDivineScalingAnalysisMessage(CombatScaling scaling) {
        String baseMessage = "<col=DDA0DD>Divine Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>Divine mercy granted! Holy damage reduced by " + 
                   assistancePercent + "% due to insufficient righteousness.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF0000>Divine wrath escalated! Holy power increased by " + 
                   difficultyIncrease + "% due to superior equipment advantage.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=4169E1>Balanced divine encounter. Optimal holy resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=00FFFF>Slight gear advantage detected. Divine intensity increased by " + 
                   difficultyIncrease + "% for balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Divine power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during divine combat
     */
    private void monitorDivineScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getDivineScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get divine scaling change message
     */
    private String getDivineScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Divine Update: Righteousness improved to balanced! Divine mercy reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=FF0000>Divine Update: Divine wrath escalation now active due to increased power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Divine Update: Holy resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=4169E1>Divine Update: Full divine protection restored! Holy damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent divine phase tracking with BossBalancer integration
     */
    private void updateIntelligentDivinePhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentDivinePhase(npc);
        
        Integer lastPhase = currentDivinePhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentDivinePhase.put(npcKey, newPhase);
            handleIntelligentDivinePhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current divine phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentDivinePhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= FINAL_JUDGMENT_THRESHOLD) return 4;
            if (hpPercent <= DIVINE_WRATH_THRESHOLD) return 3;
            if (hpPercent <= BLESSING_PHASE_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent divine phase transitions with scaling integration
     */
    private void handleIntelligentDivinePhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("Saradomin lend me strength!"));
            npc.setNextGraphics(new Graphics(1194));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED DIVINE WRATH UNLEASHED!" : "Good will always triumph!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(1194));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE DIVINE JUDGMENT - MAXIMUM HOLY POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED FINAL JUDGMENT!" : "Attack! Find the Godsword!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(1194));
            
            // Enhanced heal calculation with v5.0 scaling (divine regeneration)
            int baseHeal = npc.getMaxHitpoints() / 8; // Quick healing for divine boss
            int scaledHeal = (int)(baseHeal * scaling.bossHpMultiplier);
            npc.heal(Math.max(scaledHeal, 90));
            break;
        }
    }

    /**
     * NEW v5.0: Check for divine safe spotting
     */
    private void checkDivineSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveAoeAttacks.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastAttackHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect divine-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean zilvanaStuckAoe = consecutiveCount > 5;
        boolean recentMiss = !lastHit;
        
        boolean divineSafeSpot = playerDistanced && zilvanaStuckAoe && recentMiss;
        
        if (divineSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating divine-themed responses
            if (warningCount >= 3) {
                performDivineAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=4169E1>The divine light calms as you fight with honor...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform divine anti-safe spot measure
     */
    private void performDivineAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=FF0000>Zilyana's divine power seeks those who hide from righteous combat!</col>");
        
        // Divine light that pierces obstacles
        npc.setNextAnimation(new Animation(6967));
        npc.setNextGraphics(new Graphics(1194));
        
        // Enhanced damage based on scaling
        int baseDamage = (int)(npc.getMaxHit() * 1.4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareDivineDamageScaling(scaledDamage, player, "divine_pierce");
        
        delayHit(npc, 1, player, getMagicHit(npc, safeDamage));
        player.setNextGraphics(new Graphics(1194));
        
        player.sendMessage("<col=00FFFF>DIVINE PENALTY: Safe spotting detected - holy light pierces barriers!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced divine proclamations with scaling-based frequency
     */
    private void performEnhancedDivineProclamations(NPC npc, CombatScaling scaling) {
        // Increase proclamation frequency based on scaling
        int proclamationChance = 18; // Base 18%
        if (scaling.bossDamageMultiplier > 2.0) {
            proclamationChance = 30; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            proclamationChance = 24; // More frequent for high scaling
        }
        
        if (Utils.random(100) < proclamationChance) {
            // Enhanced divine proclamations based on phase and scaling
            int phase = getCurrentDivinePhase(npc);
            performScaledDivineProclamation(npc, phase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled divine proclamation based on phase and scaling
     */
    private void performScaledDivineProclamation(NPC npc, int phase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicProclamations = {
            "Death to the enemies of the light!",
            "Slay the evil ones!",
            "In the name of Saradomin!",
            "Saradomin lend me strength!",
            "By the power of Saradomin!",
            "Good will always triumph!",
            "The light shall never fade!",
            "For Saradomin's eternal glory!"
        };
        
        String[] enhancedProclamations = {
            "ENHANCED DIVINE POWER ACTIVATED!",
            "MAXIMUM HOLY RIGHTEOUSNESS!",
            "SARADOMIN'S ULTIMATE BLESSING!",
            "DIVINE SUPREMACY UNLEASHED!",
            "ETERNAL LIGHT CONQUERS ALL!"
        };
        
        String selectedProclamation;
        if (isHighScaling && phase >= 3) {
            // Use enhanced proclamations for high scaling + high phase
            selectedProclamation = enhancedProclamations[Utils.random(enhancedProclamations.length)];
        } else {
            // Use basic proclamations
            selectedProclamation = basicProclamations[Utils.random(basicProclamations.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedProclamation));
        
        // Add sound effects based on proclamation type
        if (selectedProclamation.contains("ENHANCED") || selectedProclamation.contains("MAXIMUM")) {
            npc.playSound(3258, 3); // Louder for enhanced proclamations
        } else {
            npc.playSound(3247 + Utils.random(3), 2); // Normal volume with variety
        }
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent divine warning system
     */
    private int performIntelligentDivineAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int phase = getCurrentDivinePhase(npc);
            DivineAttackPattern pattern = selectIntelligentDivineAttackPattern(phase, scaling, currentCount, npc);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentDivineWarning(scaling, currentCount)) {
                sendIntelligentDivinePreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledDivineAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentDivineAttackDelay(npc, phase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledDivineAttack(npc, player, pattern, scaling);
                return getIntelligentDivineAttackDelay(npc, phase, scaling);
            }
            
        } catch (Exception e) {
            return 4;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent divine warning probability based on scaling
     */
    private boolean shouldGiveIntelligentDivineWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing divine boss
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
     * ENHANCED v5.0: Intelligent divine pre-attack warning with scaling context
     */
    private void sendIntelligentDivinePreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME divine wrath due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense holy power due to scaling!)";
        }
        
        player.sendMessage("<col=FF0000>DIVINE WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent divine attack pattern selection with scaling consideration
     */
    private DivineAttackPattern selectIntelligentDivineAttackPattern(int phase, CombatScaling scaling, int attackCount, NPC npc) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on phase, scaling, and progression
        int baseSpecialChance = (phase - 1) * 16; // 16% per phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 12 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 10 ? 8 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for divine attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        // Higher phase = more AoE attacks
        int aoeChance = Math.min(60, 25 + (phase * 10)); // 35% at phase 1, 60% at phase 4
        
        if (isOvergeared) {
            // More aggressive divine patterns for overgeared players
            if (roll < 10 + specialChance) return DIVINE_ATTACK_PATTERNS[3]; // Final judgment
            if (roll < aoeChance + 15) return DIVINE_ATTACK_PATTERNS[1]; // Divine AoE  
            if (roll < 45 + specialChance) return DIVINE_ATTACK_PATTERNS[2]; // Saradomin blessing
        } else {
            // Standard divine pattern selection
            if (roll < 6 + specialChance) return DIVINE_ATTACK_PATTERNS[3]; // Final judgment
            if (roll < aoeChance) return DIVINE_ATTACK_PATTERNS[1]; // Divine AoE  
            if (roll < 35 + specialChance) return DIVINE_ATTACK_PATTERNS[2]; // Saradomin blessing
        }
        
        return DIVINE_ATTACK_PATTERNS[0]; // Melee strike
    }

    /**
     * ENHANCED v5.0: Execute divine attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledDivineAttack(NPC npc, Player player, DivineAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced phase damage calculation with v5.0 intelligence
            int phase = getCurrentDivinePhase(npc);
            double phaseModifier = 1.0 + (phase - 1) * 0.14; // 14% per phase for divine boss
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * phaseModifier);
            
            // Execute different divine attack types with v5.0 scaling and HP-aware damage
            if ("final_judgment".equals(pattern.name)) {
                executeIntelligentFinalJudgment(npc, player, baseDamage, scaling);
            } else if ("divine_aoe".equals(pattern.name)) {
                executeIntelligentDivineAoe(npc, player, baseDamage, scaling);
            } else if ("saradomin_blessing".equals(pattern.name)) {
                executeIntelligentSaradominBlessing(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleDivineAttack(npc, player, baseDamage, 0, scaling, "melee_strike");
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
            // Enhanced fallback - execute basic divine attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleDivineAttack(npc, player, scaledDamage, 0, scaling, "melee_strike");
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent final judgment attack with HP-aware scaling
     */
    private void executeIntelligentFinalJudgment(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ultimate divine attack - 180% damage with variance
        int damage = (int)(baseDamage * 1.8) + Utils.random(baseDamage / 3);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for ultimate attacks
        int safeDamage = applyHPAwareDivineDamageScaling(scaledDamage, player, "final_judgment");
        checkAndWarnLowHPForDivine(player, safeDamage);
        
        delayHit(npc, 1, player, getMagicHit(npc, safeDamage));
        player.setNextGraphics(new Graphics(1194));
        
        // Update consecutive AoE counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer consecutiveCount = consecutiveAoeAttacks.get(playerKey);
        if (consecutiveCount == null) consecutiveCount = 0;
        consecutiveAoeAttacks.put(playerKey, consecutiveCount + 1);
    }

    /**
     * ENHANCED v5.0: Intelligent divine AoE attack with HP-aware scaling
     */
    private void executeIntelligentDivineAoe(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Get all possible targets for AoE
        int targetsHit = 0;
        for (Entity t : npc.getPossibleTargets()) {
            if (t.withinDistance(npc, 3)) {
                targetsHit++;
            }
        }
        
        // Calculate AoE damage reduction (more targets = less damage each)
        double aoeReduction = Math.max(0.6, 1.0 - (targetsHit * 0.08)); // Max 40% reduction
        
        // Enhanced AoE damage (130% of base with reduction)
        int damage = (int)(baseDamage * 1.3 * aoeReduction) + Utils.random(baseDamage / 5);
        
        // Apply to all targets
        for (Entity t : npc.getPossibleTargets()) {
            if (!t.withinDistance(npc, 3)) continue;
            
            if (t instanceof Player) {
                Player targetPlayer = (Player) t;
                
                // Get individual scaling for each player
                CombatScaling targetScaling = BossBalancer.getIntelligentCombatScaling(targetPlayer, npc);
                int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, targetPlayer, damage);
                
                // CRITICAL: Apply HP-aware damage scaling for AoE attacks
                int safeDamage = applyHPAwareDivineDamageScaling(scaledDamage, targetPlayer, "divine_aoe");
                
                delayHit(npc, 1, t, getMagicHit(npc, safeDamage));
                t.setNextGraphics(new Graphics(1194));
            }
        }
        
        // Update AoE spell counter and consecutive counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer spellCount = aoeSpellCount.get(playerKey);
        if (spellCount == null) spellCount = 0;
        aoeSpellCount.put(playerKey, spellCount + 1);
        
        Integer consecutiveCount = consecutiveAoeAttacks.get(playerKey);
        if (consecutiveCount == null) consecutiveCount = 0;
        consecutiveAoeAttacks.put(playerKey, consecutiveCount + 1);
    }

    /**
     * NEW v5.0: Intelligent Saradomin blessing attack with HP-aware scaling
     */
    private void executeIntelligentSaradominBlessing(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Blessed attack damage (150% base for enhanced divine power)
        int damage = (int)(baseDamage * 1.5) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for blessed attacks
        int safeDamage = applyHPAwareDivineDamageScaling(scaledDamage, player, "saradomin_blessing");
        checkAndWarnLowHPForDivine(player, safeDamage);
        
        delayHit(npc, 1, player, getMagicHit(npc, safeDamage));
        player.setNextGraphics(new Graphics(1194));
        
        // Enhanced healing for Zilyana based on scaling
        int healAmount = (int)(npc.getMaxHitpoints() * 0.03 * scaling.bossHpMultiplier);
        npc.heal(Math.max(50, healAmount));
        
        // Update consecutive AoE counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer consecutiveCount = consecutiveAoeAttacks.get(playerKey);
        if (consecutiveCount == null) consecutiveCount = 0;
        consecutiveAoeAttacks.put(playerKey, consecutiveCount + 1);
    }

    /**
     * ENHANCED v5.0: Intelligent single divine attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleDivineAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareDivineDamageScaling(scaledDamage, player, attackType);
        if (!"melee_strike".equals(attackType)) {
            checkAndWarnLowHPForDivine(player, safeDamage);
        }
        
        delayHit(npc, delay, player, getMeleeHit(npc, safeDamage));
        
        // Track if attack hit and consecutive misses
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastAttackHit.put(playerKey, safeDamage > 0);
        
        if (safeDamage == 0) {
            Integer missCount = consecutiveMisses.get(playerKey);
            if (missCount == null) missCount = 0;
            consecutiveMisses.put(playerKey, missCount + 1);
        } else {
            consecutiveMisses.put(playerKey, 0);
        }
        
        // Reset consecutive AoE counter for melee attacks
        consecutiveAoeAttacks.put(playerKey, 0);
    }

    /**
     * ENHANCED v5.0: Intelligent divine attack delay with scaling consideration
     */
    private int getIntelligentDivineAttackDelay(NPC npc, int phase, CombatScaling scaling) {
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) return 4;
        
        int baseDelay = defs.getAttackDelay();
        int phaseSpeedBonus = Math.max(0, phase - 1);
        
        // v5.0 intelligent scaling can affect attack speed for divine boss
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = 1; // Faster for extreme scaling (divine power accelerates)
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
     * ENHANCED v5.0: Handle divine combat end with proper cleanup
     */
    public static void onDivineCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentDivinePhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            aoeSpellCount.remove(playerKey);
            consecutiveMisses.remove(playerKey);
            consecutiveAoeAttacks.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastAttackHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=4169E1>Divine combat session ended. Holy scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("CommanderZilyanaCombat: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during divine combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Divine scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("CommanderZilyanaCombat: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onDivineCombatEnd(player, null);
        }
    }

    /**
     * Enhanced divine attack pattern data structure
     */
    private static class DivineAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        DivineAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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