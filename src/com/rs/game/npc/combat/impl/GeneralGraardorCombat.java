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
 * Enhanced General Graardor Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Intelligent power-based scaling, armor analysis, HP-aware damage scaling, Bandos war mechanics
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 4.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System
 */
public class GeneralGraardorCombat extends CombatScript {

    // ===== BANDOS WAR PHASES - Enhanced for v5.0 =====
    private static final double WAR_PHASE_2_THRESHOLD = 0.70;
    private static final double WAR_PHASE_3_THRESHOLD = 0.40; 
    private static final double WAR_PHASE_4_THRESHOLD = 0.15;

    // ===== ENHANCED GUIDANCE SYSTEM - Intelligent scaling aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentWarPhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> warCriesCount = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 180000; // 3 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 3000; // 3 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - CRITICAL SAFETY SYSTEM =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.32; // Max 32% of player HP per hit (Bandos is brutal)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.48;  // Max 48% for critical war attacks
    private static final double WAR_COMBO_DAMAGE_PERCENT = 0.44; // Max 44% for war combos
    private static final double AREA_ATTACK_DAMAGE_PERCENT = 0.36; // Max 36% for area ranged attacks
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 480;          // Hard cap (32% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 28;               // Minimum damage to prevent 0 hits

    // ===== BANDOS WAR ATTACK PATTERNS with v5.0 intelligence =====
    private static final BandosAttackPattern[] WAR_ATTACK_PATTERNS = {
        new BandosAttackPattern(17390, 0, 0, "melee_strike", false, ""),
        new BandosAttackPattern(17391, 1200, 0, "ranged_barrage", true, "RANGED BARRAGE incoming - area damage to all nearby!"),
        new BandosAttackPattern(17390, 0, 0, "war_crush", true, "WAR CRUSH incoming - devastating melee blow!"),
        new BandosAttackPattern(17391, 1200, 0, "bandos_wrath", true, "BANDOS WRATH incoming - ultimate area attack!")
    };

    // ===== SAFE SPOT PREVENTION - War-themed =====
    private static final Map<Integer, Integer> consecutiveRangedAttacks = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastAttackHit = new ConcurrentHashMap<Integer, Boolean>();

    @Override
    public Object[] getKeys() {
        return new Object[] { 6260 }; // General Graardor NPC ID
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeBandosCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentWarGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorWarScalingChanges(player, scaling);
        
        // Update phase tracking with v5.0 scaling
        updateIntelligentWarPhaseTracking(npc, scaling);
        
        // Check for war-themed safe spotting
        checkBandosSafeSpotting(player, npc, scaling);
        
        // Epic Bandos war cries with enhanced frequency based on scaling
        performEnhancedWarCries(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentWarAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize Bandos combat session using BossBalancer v5.0
     */
    private void initializeBandosCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            warCriesCount.put(sessionKey, Integer.valueOf(0));
            consecutiveRangedAttacks.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastAttackHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced Bandos combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentWarWelcomeMessage(scaling);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for war combat
            performInitialBandosArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial Bandos armor analysis
     */
    private void performInitialBandosArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=FF6600>War Analysis: Exposed weaknesses detected. Bandos forces will exploit them!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=8B4513>War Analysis: Full battle armor detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance).</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from war attacks
     */
    private int applyHPAwareWarDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (more dangerous when wounded)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on war attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "bandos_wrath":
                case "ultimate_war":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "war_combo":
                    damagePercent = WAR_COMBO_DAMAGE_PERCENT;
                    break;
                case "ranged_barrage":
                case "area_attack":
                    damagePercent = AREA_ATTACK_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 82% of current HP for Bandos
            if (currentHP > 0) {
                int emergencyCap = (int)(currentHP * 0.82);
                safeDamage = Math.min(safeDamage, emergencyCap);
            }
            
            return safeDamage;
            
        } catch (Exception e) {
            // Fallback to absolute cap
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
    }

    /**
     * NEW v5.0: Send HP warning if player is in danger from war attacks
     */
    private void checkAndWarnLowHPForWar(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming war damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.68) {
                    player.sendMessage("<col=ff0000>WAR WARNING: This Bandos attack will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.48) {
                    player.sendMessage("<col=FF6600>WAR WARNING: Heavy war damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent war welcome message based on power analysis
     */
    private String getIntelligentWarWelcomeMessage(CombatScaling scaling) {
        StringBuilder message = new StringBuilder();
        message.append("<col=8B4513>General Graardor rallies for war, analyzing your battle prowess (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=FF0000>[War intensifies: +").append(diffIncrease).append("% combat fury]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Merciful war: -").append(assistance).append("% damage reduction]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Battle resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full war armor protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent war guidance with power-based scaling awareness
     */
    private void provideIntelligentWarGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentWarGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent war guidance message based on power analysis
     */
    private String getIntelligentWarGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentWarPhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getWarScalingAnalysisMessage(scaling);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=FF0000>War Analysis: Missing armor exposes you to Bandos fury! War damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME war fury due to scaling!)" : "";
                    return "<col=FF6600>War Analysis: Enraged war phase reached. Bandos fury dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=8B0000>War Analysis: Final war phase! Bandos unleashes maximum destruction!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=FF0000>War Analysis: Extreme war scaling detected! Consider fighting stronger generals!</col>";
                }
                break;
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get war scaling analysis message
     */
    private String getWarScalingAnalysisMessage(CombatScaling scaling) {
        String baseMessage = "<col=DDA0DD>War Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>Bandos shows mercy! War damage reduced by " + 
                   assistancePercent + "% due to inferior battle gear.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF0000>War fury escalated! Bandos combat increased by " + 
                   difficultyIncrease + "% due to superior gear advantage.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=8B4513>Balanced war engagement. Optimal battle readiness achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight gear advantage detected. War intensity increased by " + 
                   difficultyIncrease + "% for balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>War power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during war combat
     */
    private void monitorWarScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getWarScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get war scaling change message
     */
    private String getWarScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>War Update: Battle readiness improved to balanced! Bandos mercy reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=FF0000>War Update: War fury escalation now active due to increased power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>War Update: Battle resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=8B4513>War Update: Full war armor restored! Combat damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent war phase tracking with BossBalancer integration
     */
    private void updateIntelligentWarPhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentWarPhase(npc);
        
        Integer lastPhase = currentWarPhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentWarPhase.put(npcKey, newPhase);
            handleIntelligentWarPhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current war phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentWarPhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= WAR_PHASE_4_THRESHOLD) return 4;
            if (hpPercent <= WAR_PHASE_3_THRESHOLD) return 3;
            if (hpPercent <= WAR_PHASE_2_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent war phase transitions with scaling integration
     */
    private void handleIntelligentWarPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("Feel the fury of Bandos!"));
            npc.setNextGraphics(new Graphics(0)); // Add appropriate graphics
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED WAR FURY ACTIVATED!" : "FOR THE GLORY OF THE BIG HIGH WAR GOD!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(0)); // Add appropriate graphics
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE BANDOS WRATH - MAXIMUM WAR POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED FINAL WAR PHASE!" : "FINAL STAND FOR BANDOS!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(0)); // Add appropriate graphics
            
            // Enhanced heal calculation with v5.0 scaling (war regeneration)
            int baseHeal = npc.getMaxHitpoints() / 10; // Significant healing for final phase
            int scaledHeal = (int)(baseHeal * scaling.bossHpMultiplier);
            npc.heal(Math.max(scaledHeal, 80));
            break;
        }
    }

    /**
     * NEW v5.0: Check for Bandos safe spotting
     */
    private void checkBandosSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveRangedAttacks.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastAttackHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect war-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean graardorStuckRanged = consecutiveCount > 5;
        boolean recentMiss = !lastHit;
        
        boolean warSafeSpot = playerDistanced && graardorStuckRanged && recentMiss;
        
        if (warSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating war-themed responses
            if (warningCount >= 3) {
                performBandosAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=8B4513>Graardor respects your honorable combat...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform Bandos anti-safe spot measure
     */
    private void performBandosAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=FF0000>Bandos war tactics adapt to find cowards hiding in shadows!</col>");
        
        // War projectile that pierces obstacles
        npc.setNextAnimation(new Animation(17391));
        World.sendProjectile(npc, player, 1200, 41, 16, 41, 35, 16, 0);
        
        // Enhanced damage based on scaling
        int baseDamage = (int)(npc.getMaxHit() * 1.4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareWarDamageScaling(scaledDamage, player, "war_pierce");
        
        delayHit(npc, 1, player, getRangeHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>WAR PENALTY: Safe spotting detected - Bandos fury pierces barriers!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced war cries with scaling-based frequency
     */
    private void performEnhancedWarCries(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        Integer criesCount = warCriesCount.get(npcKey);
        if (criesCount == null) criesCount = 0;
        
        // Increase war cry frequency based on scaling
        int warCryChance = 25; // Base 25%
        if (scaling.bossDamageMultiplier > 2.0) {
            warCryChance = 40; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            warCryChance = 32; // More frequent for high scaling
        }
        
        if (Utils.random(100) < warCryChance) {
            criesCount++;
            warCriesCount.put(npcKey, criesCount);
            
            // Enhanced war cries based on phase and scaling
            int phase = getCurrentWarPhase(npc);
            performScaledWarCry(npc, phase, scaling, criesCount);
        }
    }

    /**
     * NEW v5.0: Perform scaled war cry based on phase and scaling
     */
    private void performScaledWarCry(NPC npc, int phase, CombatScaling scaling, int criesCount) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicCries = {
            "Death to our enemies!",
            "Brargh!",
            "Break their bones!",
            "For the glory of Bandos!",
            "Split their skulls!",
            "CHAAARGE!",
            "Crush them underfoot!",
            "All glory to Bandos!",
            "GRAAAAAAAAAR!"
        };
        
        String[] enhancedCries = {
            "ENHANCED WAR FURY UNLEASHED!",
            "FEEL THE ULTIMATE POWER OF BANDOS!",
            "YOUR SUPERIOR GEAR MEANS NOTHING!",
            "BANDOS ADAPTS TO ALL CHALLENGES!",
            "THE WAR GOD'S FURY KNOWS NO BOUNDS!"
        };
        
        String selectedCry;
        if (isHighScaling && phase >= 3) {
            // Use enhanced cries for high scaling + high phase
            selectedCry = enhancedCries[Utils.random(enhancedCries.length)];
        } else {
            // Use basic cries
            selectedCry = basicCries[Utils.random(basicCries.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedCry));
        
        // Add sound effects based on cry type
        if (selectedCry.contains("ENHANCED") || selectedCry.contains("ULTIMATE")) {
            npc.playSound(3219, 3); // Louder for enhanced cries
        } else {
            npc.playSound(3209, 2); // Normal volume
        }
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent war warning system
     */
    private int performIntelligentWarAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int phase = getCurrentWarPhase(npc);
            BandosAttackPattern pattern = selectIntelligentWarAttackPattern(phase, scaling, currentCount, npc, player);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentWarWarning(scaling, currentCount)) {
                sendIntelligentWarPreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledWarAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentWarAttackDelay(npc, phase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledWarAttack(npc, player, pattern, scaling);
                return getIntelligentWarAttackDelay(npc, phase, scaling);
            }
            
        } catch (Exception e) {
            return 4;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent war warning probability based on scaling
     */
    private boolean shouldGiveIntelligentWarWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing Bandos
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
     * ENHANCED v5.0: Intelligent war pre-attack warning with scaling context
     */
    private void sendIntelligentWarPreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME war fury due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense Bandos fury due to scaling!)";
        }
        
        player.sendMessage("<col=FF0000>WAR WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent war attack pattern selection with scaling consideration
     */
    private BandosAttackPattern selectIntelligentWarAttackPattern(int phase, CombatScaling scaling, int attackCount, NPC npc, Player player) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on phase, scaling, and progression
        int baseSpecialChance = (phase - 1) * 12; // 12% per phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 8 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 10 ? 5 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for war attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        boolean isInMeleeRange = Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                               player.getX(), player.getY(), player.getSize(), 0);
        
        if (isOvergeared) {
            // More aggressive war patterns for overgeared players
            if (roll < 8 + specialChance) return WAR_ATTACK_PATTERNS[3]; // Bandos wrath
            if (roll < 20 + specialChance) return WAR_ATTACK_PATTERNS[1]; // Ranged barrage  
            if (roll < 35 + specialChance && isInMeleeRange) return WAR_ATTACK_PATTERNS[2]; // War crush
        } else {
            // Standard war pattern selection
            if (roll < 5 + specialChance) return WAR_ATTACK_PATTERNS[3]; // Bandos wrath
            if (roll < 15 + specialChance) return WAR_ATTACK_PATTERNS[1]; // Ranged barrage  
            if (roll < 30 + specialChance && isInMeleeRange) return WAR_ATTACK_PATTERNS[2]; // War crush
        }
        
        // Default: melee if in range, ranged if not
        if (isInMeleeRange) {
            return WAR_ATTACK_PATTERNS[0]; // Melee strike
        } else {
            return WAR_ATTACK_PATTERNS[1]; // Ranged barrage
        }
    }

    /**
     * ENHANCED v5.0: Execute war attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledWarAttack(NPC npc, Player player, BandosAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced phase damage calculation with v5.0 intelligence
            int phase = getCurrentWarPhase(npc);
            double phaseModifier = 1.0 + (phase - 1) * 0.15; // 15% per phase for Bandos
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * phaseModifier);
            
            // Execute different war attack types with v5.0 scaling and HP-aware damage
            if ("bandos_wrath".equals(pattern.name)) {
                executeIntelligentBandosWrath(npc, player, baseDamage, scaling);
            } else if ("ranged_barrage".equals(pattern.name)) {
                executeIntelligentRangedBarrage(npc, player, baseDamage, scaling);
            } else if ("war_crush".equals(pattern.name)) {
                executeIntelligentWarCrush(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleWarAttack(npc, player, baseDamage, 0, scaling, "melee_strike");
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
            // Enhanced fallback - execute basic war attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleWarAttack(npc, player, scaledDamage, 0, scaling, "melee_strike");
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent Bandos wrath attack with HP-aware scaling
     */
    private void executeIntelligentBandosWrath(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ultimate Bandos attack - 180% damage with variance
        int damage = (int)(baseDamage * 1.8) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for ultimate attacks
        int safeDamage = applyHPAwareWarDamageScaling(scaledDamage, player, "bandos_wrath");
        checkAndWarnLowHPForWar(player, safeDamage);
        
        // Area effect - hit all nearby players
        for (Entity target : npc.getPossibleTargets()) {
            if (target instanceof Player && target.withinDistance(npc, 8)) {
                int areaDamage = safeDamage;
                if (!target.equals(player)) {
                    areaDamage = (int)(safeDamage * 0.7); // 70% damage to other players
                }
                
                World.sendProjectile(npc, target, 1200, 41, 16, 41, 35, 16, 0);
                delayHit(npc, 1, target, getRangeHit(npc, areaDamage));
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ranged barrage attack with HP-aware scaling
     */
    private void executeIntelligentRangedBarrage(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Enhanced ranged barrage damage (130% of base)
        int damage = (int)(baseDamage * 1.3) + Utils.random(baseDamage / 6);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for ranged barrages
        int safeDamage = applyHPAwareWarDamageScaling(scaledDamage, player, "ranged_barrage");
        checkAndWarnLowHPForWar(player, safeDamage);
        
        // Update consecutive ranged attack counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer consecutiveCount = consecutiveRangedAttacks.get(playerKey);
        if (consecutiveCount == null) consecutiveCount = 0;
        consecutiveRangedAttacks.put(playerKey, consecutiveCount + 1);
        
        // Area effect ranged attack
        for (Entity target : npc.getPossibleTargets()) {
            if (target instanceof Player) {
                int areaDamage = target.equals(player) ? safeDamage : (int)(safeDamage * 0.8);
                World.sendProjectile(npc, target, 1200, 41, 16, 41, 35, 16, 0);
                delayHit(npc, 1, target, getRangeHit(npc, areaDamage));
            }
        }
    }

    /**
     * NEW v5.0: Intelligent war crush attack with HP-aware scaling
     */
    private void executeIntelligentWarCrush(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // War crush damage (140% base for devastating melee)
        int damage = (int)(baseDamage * 1.4) + Utils.random(baseDamage / 5);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for war crush
        int safeDamage = applyHPAwareWarDamageScaling(scaledDamage, player, "war_crush");
        checkAndWarnLowHPForWar(player, safeDamage);
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        // Reset consecutive ranged counter (melee attack)
        Integer playerKey = Integer.valueOf(player.getIndex());
        consecutiveRangedAttacks.put(playerKey, 0);
    }

    /**
     * ENHANCED v5.0: Intelligent single war attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleWarAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareWarDamageScaling(scaledDamage, player, attackType);
        if (!"melee_strike".equals(attackType)) {
            checkAndWarnLowHPForWar(player, safeDamage);
        }
        
        // Determine if this is melee or ranged based on distance
        boolean isInMeleeRange = Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                               player.getX(), player.getY(), player.getSize(), 0);
        
        if (isInMeleeRange && "melee_strike".equals(attackType)) {
            // Melee attack
            delayHit(npc, delay, player, getMeleeHit(npc, safeDamage));
            
            // Reset consecutive ranged counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            consecutiveRangedAttacks.put(playerKey, 0);
        } else {
            // Ranged attack
            World.sendProjectile(npc, player, 1200, 41, 16, 41, 35, 16, 0);
            delayHit(npc, delay + 1, player, getRangeHit(npc, safeDamage));
            
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
     * ENHANCED v5.0: Intelligent war attack delay with scaling consideration
     */
    private int getIntelligentWarAttackDelay(NPC npc, int phase, CombatScaling scaling) {
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) return 4;
        
        int baseDelay = defs.getAttackDelay();
        int phaseSpeedBonus = Math.max(0, phase - 1);
        
        // v5.0 intelligent scaling can affect attack speed for Bandos
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
     * ENHANCED v5.0: Handle war combat end with proper cleanup
     */
    public static void onWarCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentWarPhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            warCriesCount.remove(npcKey);
            consecutiveRangedAttacks.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastAttackHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=8B4513>War combat session ended. Bandos scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("GeneralGraardorCombat: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during war combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. War scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("GeneralGraardorCombat: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onWarCombatEnd(player, null);
        }
    }

    /**
     * Enhanced Bandos attack pattern data structure
     */
    private static class BandosAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        BandosAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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