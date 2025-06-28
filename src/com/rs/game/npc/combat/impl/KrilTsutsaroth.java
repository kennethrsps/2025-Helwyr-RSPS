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
import com.rs.utils.Logger;

/**
 * Enhanced K'ril Tsutsaroth Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Intelligent power-based scaling, armor analysis, HP-aware damage scaling, demonic prayer drain mechanics
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 4.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System
 */
public class KrilTsutsaroth extends CombatScript {

    // ===== DEMONIC COMBAT PHASES - Enhanced for v5.0 =====
    private static final double INFERNAL_PHASE_2_THRESHOLD = 0.75;
    private static final double INFERNAL_PHASE_3_THRESHOLD = 0.45; 
    private static final double INFERNAL_PHASE_4_THRESHOLD = 0.20;

    // ===== ENHANCED GUIDANCE SYSTEM - Intelligent scaling aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentInfernalPhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> prayerDrainCount = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> hitcount = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> spec = new ConcurrentHashMap<Integer, Boolean>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 180000; // 3 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 3000; // 3 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - CRITICAL SAFETY SYSTEM =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.34; // Max 34% of player HP per hit (demonic is harsh)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.50;  // Max 50% for critical demonic attacks
    private static final double PRAYER_DRAIN_DAMAGE_PERCENT = 0.46; // Max 46% for prayer drain specials
    private static final double FLAME_DAMAGE_PERCENT = 0.38;     // Max 38% for demonic flames
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 510;          // Hard cap (34% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 30;               // Minimum damage to prevent 0 hits

    // ===== DEMONIC ATTACK PATTERNS with v5.0 intelligence =====
    private static final DemonicAttackPattern[] DEMONIC_ATTACK_PATTERNS = {
        new DemonicAttackPattern(14968, 0, 0, "melee_strike", false, ""),
        new DemonicAttackPattern(14962, 1210, 1211, "flame_barrage", true, "DEMONIC FLAMES incoming - area magic damage and poison!"),
        new DemonicAttackPattern(14963, 0, 0, "prayer_drain_strike", true, "PRAYER DRAIN STRIKE incoming - pierces protection!"),
        new DemonicAttackPattern(14962, 1210, 1211, "infernal_wrath", true, "INFERNAL WRATH incoming - ultimate demonic power!")
    };

    // ===== SAFE SPOT PREVENTION - Demonic-themed =====
    private static final Map<Integer, Integer> consecutiveMagicAttacks = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastAttackHit = new ConcurrentHashMap<Integer, Boolean>();

    @Override
    public Object[] getKeys() {
        return new Object[] { 6203 }; // K'ril Tsutsaroth NPC ID
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeDemonicCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentDemonicGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorDemonicScalingChanges(player, scaling);
        
        // Update phase tracking with v5.0 scaling
        updateIntelligentInfernalPhaseTracking(npc, scaling);
        
        // Check for demonic-themed safe spotting
        checkDemonicSafeSpotting(player, npc, scaling);
        
        // Enhanced demonic taunts with scaling-based frequency
        performEnhancedDemonicTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentDemonicAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize demonic combat session using BossBalancer v5.0
     */
    private void initializeDemonicCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            prayerDrainCount.put(sessionKey, Integer.valueOf(0));
            hitcount.put(sessionKey, Integer.valueOf(0));
            spec.put(sessionKey, Boolean.FALSE);
            consecutiveMagicAttacks.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastAttackHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced demonic combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentDemonicWelcomeMessage(scaling);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for demonic combat
            performInitialDemonicArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial demonic armor analysis
     */
    private void performInitialDemonicArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=8B0000>Infernal Analysis: Exposed soul detected. Demonic forces will exploit weaknesses!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=FF0000>Infernal Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% demonic resistance).</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from demonic attacks
     */
    private int applyHPAwareDemonicDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (more dangerous when wounded)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on demonic attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "infernal_wrath":
                case "ultimate_demonic":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "prayer_drain_strike":
                case "demonic_special":
                    damagePercent = PRAYER_DRAIN_DAMAGE_PERCENT;
                    break;
                case "flame_barrage":
                case "demonic_flame":
                    damagePercent = FLAME_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 84% of current HP for demonic boss
            if (currentHP > 0) {
                int emergencyCap = (int)(currentHP * 0.84);
                safeDamage = Math.min(safeDamage, emergencyCap);
            }
            
            return safeDamage;
            
        } catch (Exception e) {
            // Fallback to absolute cap
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
    }

    /**
     * NEW v5.0: Send HP warning if player is in danger from demonic attacks
     */
    private void checkAndWarnLowHPForDemonic(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming demonic damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.70) {
                    player.sendMessage("<col=ff0000>DEMONIC WARNING: This infernal attack will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.50) {
                    player.sendMessage("<col=8B0000>DEMONIC WARNING: Heavy infernal damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent demonic welcome message based on power analysis
     */
    private String getIntelligentDemonicWelcomeMessage(CombatScaling scaling) {
        StringBuilder message = new StringBuilder();
        message.append("<col=8B0000>K'ril Tsutsaroth emerges from the abyss, infernal powers analyzing your soul (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=FF0000>[Demonic fury: +").append(diffIncrease).append("% infernal power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Demonic mercy: -").append(assistance).append("% infernal damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Infernal resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full demonic protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent demonic guidance with power-based scaling awareness
     */
    private void provideIntelligentDemonicGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentDemonicGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent demonic guidance message based on power analysis
     */
    private String getIntelligentDemonicGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentInfernalPhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getDemonicScalingAnalysisMessage(scaling);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=FF0000>Infernal Analysis: Missing armor exposes your soul! Demonic damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME infernal fury due to scaling!)" : "";
                    return "<col=8B0000>Infernal Analysis: Demonic rage phase reached. Prayer drain intensified" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=660000>Infernal Analysis: Ultimate demonic phase! Maximum prayer drain and infernal power!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=FF0000>Infernal Analysis: Extreme demonic scaling detected! Consider fighting stronger demons!</col>";
                }
                break;
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get demonic scaling analysis message
     */
    private String getDemonicScalingAnalysisMessage(CombatScaling scaling) {
        String baseMessage = "<col=DDA0DD>Infernal Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>Demonic mercy granted! Infernal damage reduced by " + 
                   assistancePercent + "% due to insufficient soul power.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF0000>Demonic fury escalated! Infernal power increased by " + 
                   difficultyIncrease + "% due to superior soul strength.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=8B0000>Balanced demonic encounter. Optimal infernal resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight soul advantage detected. Demonic intensity increased by " + 
                   difficultyIncrease + "% for balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Infernal power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during demonic combat
     */
    private void monitorDemonicScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getDemonicScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get demonic scaling change message
     */
    private String getDemonicScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Infernal Update: Soul strength improved to balanced! Demonic mercy reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=FF0000>Infernal Update: Demonic fury escalation now active due to increased power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Infernal Update: Soul resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=8B0000>Infernal Update: Full demonic protection restored! Infernal damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent infernal phase tracking with BossBalancer integration
     */
    private void updateIntelligentInfernalPhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentInfernalPhase(npc);
        
        Integer lastPhase = currentInfernalPhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentInfernalPhase.put(npcKey, newPhase);
            handleIntelligentInfernalPhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current infernal phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentInfernalPhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= INFERNAL_PHASE_4_THRESHOLD) return 4;
            if (hpPercent <= INFERNAL_PHASE_3_THRESHOLD) return 3;
            if (hpPercent <= INFERNAL_PHASE_2_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent infernal phase transitions with scaling integration
     */
    private void handleIntelligentInfernalPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("The flames of the abyss grow stronger!"));
            npc.setNextGraphics(new Graphics(1210));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED DEMONIC FURY ACTIVATED!" : "Feel the wrath of Zamorak!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(1211));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE INFERNAL WRATH - MAXIMUM DEMONIC POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED DEMONIC SUPREMACY!" : "FEEL THE FULL POWER OF THE ABYSS!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(1210));
            
            // Enhanced heal calculation with v5.0 scaling (demonic regeneration)
            int baseHeal = npc.getMaxHitpoints() / 6; // Significant healing for demonic boss
            int scaledHeal = (int)(baseHeal * scaling.bossHpMultiplier);
            npc.heal(Math.max(scaledHeal, 120));
            break;
        }
    }

    /**
     * NEW v5.0: Check for demonic safe spotting
     */
    private void checkDemonicSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveMagicAttacks.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastAttackHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect demonic-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean krilStuckMagic = consecutiveCount > 6;
        boolean recentMiss = !lastHit;
        
        boolean demonicSafeSpot = playerDistanced && krilStuckMagic && recentMiss;
        
        if (demonicSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating demonic-themed responses
            if (warningCount >= 3) {
                performDemonicAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=8B0000>The demonic flames subside as you fight with honor...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform demonic anti-safe spot measure
     */
    private void performDemonicAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=FF0000>K'ril's infernal power seeks cowards hiding from demonic combat!</col>");
        
        // Demonic flame that pierces obstacles
        npc.setNextAnimation(new Animation(14962));
        npc.setNextGraphics(new Graphics(1210));
        World.sendProjectile(npc, player, 1211, 41, 16, 41, 35, 16, 0);
        
        // Enhanced damage based on scaling
        int baseDamage = (int)(npc.getMaxHit() * 1.6);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareDemonicDamageScaling(scaledDamage, player, "demonic_pierce");
        
        delayHit(npc, 1, player, getMagicHit(npc, safeDamage));
        
        // Apply poison
        player.getPoison().makePoisoned(120);
        
        player.sendMessage("<col=FF6600>DEMONIC PENALTY: Safe spotting detected - infernal flames pierce barriers!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced demonic taunts with scaling-based frequency
     */
    private void performEnhancedDemonicTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on scaling
        int tauntChance = 15; // Base 15%
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance = 25; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance = 20; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced demonic taunts based on phase and scaling
            int phase = getCurrentInfernalPhase(npc);
            performScaledDemonicTaunt(npc, phase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled demonic taunt based on phase and scaling
     */
    private void performScaledDemonicTaunt(NPC npc, int phase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "Attack them, you dogs!",
            "Forward!",
            "Death to Saradomin's dogs!",
            "Kill them, you cowards!",
            "The Dark One will have their souls!",
            "Zamorak, curse them!",
            "Rend them limb from limb!",
            "No retreat!",
            "Flay them all!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED DEMONIC FURY UNLEASHED!",
            "YOUR SUPERIOR GEAR FEEDS MY RAGE!",
            "THE ABYSS ADAPTS TO ALL CHALLENGES!",
            "INFERNAL POWER KNOWS NO BOUNDS!",
            "MAXIMUM DEMONIC SUPREMACY!"
        };
        
        String selectedTaunt;
        if (isHighScaling && phase >= 3) {
            // Use enhanced taunts for high scaling + high phase
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
        
        // Add sound effects based on taunt type
        if (selectedTaunt.contains("ENHANCED") || selectedTaunt.contains("MAXIMUM")) {
            npc.playSound(3229, 3); // Louder for enhanced taunts
        } else if (selectedTaunt.equals("The Dark One will have their souls!")) {
            npc.playSound(3229, 2); // Normal volume
        }
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent demonic warning system
     */
    private int performIntelligentDemonicAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Increment hit count for prayer drain logic
            Integer currentHitCount = hitcount.get(playerKey);
            if (currentHitCount == null) currentHitCount = 0;
            hitcount.put(playerKey, currentHitCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int phase = getCurrentInfernalPhase(npc);
            DemonicAttackPattern pattern = selectIntelligentDemonicAttackPattern(phase, scaling, currentCount, player);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentDemonicWarning(scaling, currentCount)) {
                sendIntelligentDemonicPreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledDemonicAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentDemonicAttackDelay(npc, phase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledDemonicAttack(npc, player, pattern, scaling);
                return getIntelligentDemonicAttackDelay(npc, phase, scaling);
            }
            
        } catch (Exception e) {
            return 4;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent demonic warning probability based on scaling
     */
    private boolean shouldGiveIntelligentDemonicWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing demonic boss
        boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
        int warningFrequency = isUndergeared ? 4 : 6; // Every 4th vs 6th attack
        
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
     * ENHANCED v5.0: Intelligent demonic pre-attack warning with scaling context
     */
    private void sendIntelligentDemonicPreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME demonic fury due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense infernal power due to scaling!)";
        }
        
        player.sendMessage("<col=FF0000>DEMONIC WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent demonic attack pattern selection with scaling consideration
     */
    private DemonicAttackPattern selectIntelligentDemonicAttackPattern(int phase, CombatScaling scaling, int attackCount, Player player) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer currentHitCount = hitcount.get(playerKey);
        if (currentHitCount == null) currentHitCount = 0;
        
        // Check for prayer drain special conditions
        boolean playerUsingProtection = player.getPrayer().usingPrayer(0, 19) || player.getPrayer().usingPrayer(1, 9);
        boolean shouldDoPrayerDrainAttack = currentHitCount >= 2 && playerUsingProtection;
        
        if (shouldDoPrayerDrainAttack) {
            hitcount.put(playerKey, -1); // Reset hit count
            return DEMONIC_ATTACK_PATTERNS[2]; // Prayer drain strike
        }
        
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on phase, scaling, and progression
        int baseSpecialChance = (phase - 1) * 15; // 15% per phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 10 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 12 ? 7 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for demonic attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        // Higher tier bosses prefer more magic attacks
        int magicChance = Math.min(60, 30 + (phase * 8)); // 38% at phase 1, 60% at phase 4
        
        if (isOvergeared) {
            // More aggressive demonic patterns for overgeared players
            if (roll < 8 + specialChance) return DEMONIC_ATTACK_PATTERNS[3]; // Infernal wrath
            if (roll < magicChance + 10) return DEMONIC_ATTACK_PATTERNS[1]; // Flame barrage  
        } else {
            // Standard demonic pattern selection
            if (roll < 5 + specialChance) return DEMONIC_ATTACK_PATTERNS[3]; // Infernal wrath
            if (roll < magicChance) return DEMONIC_ATTACK_PATTERNS[1]; // Flame barrage  
        }
        
        return DEMONIC_ATTACK_PATTERNS[0]; // Melee strike
    }

    /**
     * ENHANCED v5.0: Execute demonic attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledDemonicAttack(NPC npc, Player player, DemonicAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced phase damage calculation with v5.0 intelligence
            int phase = getCurrentInfernalPhase(npc);
            double phaseModifier = 1.0 + (phase - 1) * 0.18; // 18% per phase for demonic boss
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * phaseModifier);
            
            // Execute different demonic attack types with v5.0 scaling and HP-aware damage
            if ("infernal_wrath".equals(pattern.name)) {
                executeIntelligentInfernalWrath(npc, player, baseDamage, scaling);
            } else if ("flame_barrage".equals(pattern.name)) {
                executeIntelligentFlameBarrage(npc, player, baseDamage, scaling);
            } else if ("prayer_drain_strike".equals(pattern.name)) {
                executeIntelligentPrayerDrainStrike(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleDemonicAttack(npc, player, baseDamage, 0, scaling, "melee_strike");
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
            // Enhanced fallback - execute basic demonic attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleDemonicAttack(npc, player, scaledDamage, 0, scaling, "melee_strike");
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent infernal wrath attack with HP-aware scaling
     */
    private void executeIntelligentInfernalWrath(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ultimate demonic attack - 190% damage with variance
        int damage = (int)(baseDamage * 1.9) + Utils.random(baseDamage / 3);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for ultimate attacks
        int safeDamage = applyHPAwareDemonicDamageScaling(scaledDamage, player, "infernal_wrath");
        checkAndWarnLowHPForDemonic(player, safeDamage);
        
        // Special infernal wrath effects
        World.sendProjectile(npc, player, 1211, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 1, player, getMagicHit(npc, safeDamage));
        
        // Enhanced poison based on scaling
        int poisonDamage = 150 + (int)(scaling.bossDamageMultiplier * 30);
        player.getPoison().makePoisoned(Math.min(250, poisonDamage));
        
        // Prayer drain effect
        int prayerDrain = 20 + (int)(scaling.bossDamageMultiplier * 5);
        player.getPrayer().drainPrayer(Math.min(50, prayerDrain));
        
        // Update consecutive magic counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer consecutiveCount = consecutiveMagicAttacks.get(playerKey);
        if (consecutiveCount == null) consecutiveCount = 0;
        consecutiveMagicAttacks.put(playerKey, consecutiveCount + 1);
    }

    /**
     * ENHANCED v5.0: Intelligent flame barrage attack with HP-aware scaling
     */
    private void executeIntelligentFlameBarrage(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Enhanced flame barrage damage (140% of base)
        int damage = (int)(baseDamage * 1.4) + Utils.random(baseDamage / 5);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // Apply protection prayer reduction if applicable
        if (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)) {
            scaledDamage = (int)(scaledDamage * 0.6); // 40% reduction with protect from magic
        }
        
        // CRITICAL: Apply HP-aware damage scaling for flame barrages
        int safeDamage = applyHPAwareDemonicDamageScaling(scaledDamage, player, "flame_barrage");
        checkAndWarnLowHPForDemonic(player, safeDamage);
        
        World.sendProjectile(npc, player, 1211, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 1, player, getMagicHit(npc, safeDamage));
        
        // Poison chance based on scaling
        int poisonChance = Math.min(40, 20 + (int)(scaling.bossDamageMultiplier * 8));
        if (Utils.random(100) < poisonChance) {
            int poisonDamage = 100 + (int)(scaling.bossDamageMultiplier * 20);
            player.getPoison().makePoisoned(Math.min(200, poisonDamage));
        }
        
        // Update consecutive magic counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer consecutiveCount = consecutiveMagicAttacks.get(playerKey);
        if (consecutiveCount == null) consecutiveCount = 0;
        consecutiveMagicAttacks.put(playerKey, consecutiveCount + 1);
    }

    /**
     * NEW v5.0: Intelligent prayer drain strike attack with HP-aware scaling
     */
    private void executeIntelligentPrayerDrainStrike(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Prayer drain special attack - 160% damage (pierces protection)
        int damage = (int)(baseDamage * 1.6) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for prayer drain attacks
        int safeDamage = applyHPAwareDemonicDamageScaling(scaledDamage, player, "prayer_drain_strike");
        checkAndWarnLowHPForDemonic(player, safeDamage);
        
        npc.setNextForceTalk(new ForceTalk("YARRRRRRR!"));
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        // Enhanced prayer drain based on scaling
        int prayerDrainAmount = Math.max(15, 25 + (int)(scaling.bossDamageMultiplier * 8));
        int prayerDelayTime = Math.max(3, 5 + (int)(scaling.bossDamageMultiplier * 2));
        
        player.getPrayer().drainPrayer(Math.min(60, prayerDrainAmount));
        player.setPrayerDelay(Math.min(10, prayerDelayTime));
        
        player.sendMessage("K'ril Tsutsaroth slams through your protection prayer, leaving you feeling drained.");
        
        // Update prayer drain counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer drainCount = prayerDrainCount.get(playerKey);
        if (drainCount == null) drainCount = 0;
        prayerDrainCount.put(playerKey, drainCount + 1);
        
        // Reset consecutive magic counter (melee attack)
        consecutiveMagicAttacks.put(playerKey, 0);
    }

    /**
     * ENHANCED v5.0: Intelligent single demonic attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleDemonicAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // Apply protection prayer reduction if applicable and not special attack
        if ("melee_strike".equals(attackType)) {
            if (player.getPrayer().usingPrayer(0, 18) || player.getPrayer().usingPrayer(1, 8)) {
                scaledDamage = (int)(scaledDamage * 0.6); // 40% reduction with protect from melee
            }
        }
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareDemonicDamageScaling(scaledDamage, player, attackType);
        if (!"melee_strike".equals(attackType)) {
            checkAndWarnLowHPForDemonic(player, safeDamage);
        }
        
        delayHit(npc, delay, player, getMeleeHit(npc, safeDamage));
        
        // Track if attack hit
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastAttackHit.put(playerKey, safeDamage > 0);
        
        // Reset consecutive magic counter for melee attacks
        consecutiveMagicAttacks.put(playerKey, 0);
    }

    /**
     * ENHANCED v5.0: Intelligent demonic attack delay with scaling consideration
     */
    private int getIntelligentDemonicAttackDelay(NPC npc, int phase, CombatScaling scaling) {
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) return 4;
        
        int baseDelay = defs.getAttackDelay();
        int phaseSpeedBonus = Math.max(0, phase - 1);
        
        // v5.0 intelligent scaling can affect attack speed for demonic boss
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = 2; // Much faster for extreme scaling
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
     * ENHANCED v5.0: Handle demonic combat end with proper cleanup
     */
    public static void onDemonicCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentInfernalPhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            prayerDrainCount.remove(playerKey);
            hitcount.remove(playerKey);
            spec.remove(playerKey);
            consecutiveMagicAttacks.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastAttackHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=8B0000>Demonic combat session ended. Infernal scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("KrilTsutsaroth: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during demonic combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Demonic scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("KrilTsutsaroth: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onDemonicCombatEnd(player, null);
        }
    }

    /**
     * Enhanced demonic attack pattern data structure
     */
    private static class DemonicAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        DemonicAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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