package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.CombatScript;
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
 * Enhanced Sotapana Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Epic blade master with dynamic scaling, prayer integration, intelligent guidance, HP-aware damage caps
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 5.0 - FULL BossBalancer v5.0 Integration with Intelligent Blade Combat Scaling & HP-Aware System
 */
public class SotapanaCombat extends CombatScript {

    // ===== BLADE MASTER PHASES - Enhanced for v5.0 =====
    private static final double WARRIOR_THRESHOLD = 0.80;         // 80% HP - normal warrior
    private static final double SWORDSMAN_THRESHOLD = 0.60;       // 60% HP - enhanced swordsman
    private static final double BERSERKER_THRESHOLD = 0.50;       // 50% HP - berserker mode  
    private static final double BLADE_MASTER_THRESHOLD = 0.25;    // 25% HP - ultimate blade master

    // ===== ENHANCED GUIDANCE SYSTEM - Session-aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentBladePhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> specialAttackCount = new ConcurrentHashMap<Integer, Integer>();

    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 120000; // 2 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 20000; // 20 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 2000; // 2 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 4; // More warnings for complex mechanics

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - BLADE MASTER BALANCED =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.35; // Max 35% of player HP per hit (blade master)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.50;  // Max 50% for epic special attacks
    private static final double BERSERKER_DAMAGE_PERCENT = 0.45; // Max 45% for berserker attacks
    private static final double BLADE_MASTER_DAMAGE_PERCENT = 0.55; // Max 55% for ultimate techniques
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 525;          // Hard cap (35% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 35;               // Minimum damage to prevent 0 hits

    // ===== SAFE SPOT PREVENTION - Blade-themed =====
    private static final Map<Integer, Integer> consecutiveAvoidedStrikes = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastStrikeHit = new ConcurrentHashMap<Integer, Boolean>();

    // Boss configuration constants
    private static final int BOSS_NPC_ID = 25577;
    private static final double SINGLE_TARGET_MULTIPLIER = 1.0;
    private static final double AREA_ATTACK_MULTIPLIER = 0.7;
    private static final double BERSERKER_MULTIPLIER = 1.5;
    private static final double SPECIAL_MULTIPLIER = 1.2;
    private static final double EPIC_MULTIPLIER = 1.8;
    
    // Confirmed working animations and graphics
    private static final int MELEE_ANIMATION_1 = 24010;
    private static final int MELEE_ANIMATION_2 = 12031;
    private static final int SPECIAL_ANIMATION = 18374;
    private static final int DASH_ANIMATION = 11989;
    private static final int SPIN_ANIMATION = 24010;
    
    private static final int MELEE_GRAPHICS = 1950;
    private static final int BERSERKER_GRAPHICS = 2113;
    private static final int LIGHTNING_GRAPHICS = 1729;
    private static final int SHATTER_GRAPHICS = 2114;
    private static final int PHANTOM_GRAPHICS = 3586;
    
    // Special attack and mechanics constants
    private static final int PRAYER_DRAIN_CHANCE = 8;
    private static final int PRAYER_DRAIN_MIN = 15;
    private static final int PRAYER_DRAIN_MAX = 25;
    private static final int SPECIAL_ATTACK_CHANCE = 15;
    private static final int MAX_SPECIAL_ATTACKS_PER_FIGHT = 10; // Prevent spam

    @Override
    public Object[] getKeys() {
        return new Object[] { BOSS_NPC_ID };
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        // Enhanced null safety
        if (!isValidCombatState(npc, target)) {
            return 4;
        }
        
        final ArrayList<Entity> targets = npc.getPossibleTargets();
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        
        if (targets == null || targets.isEmpty() || defs == null) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeBladeCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentBladeGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorBladeScalingChanges(player, scaling);
        
        // Update blade phase tracking with v5.0 scaling
        updateIntelligentBladePhaseTracking(npc, scaling);
        
        // Check for blade-themed safe spotting
        checkBladeSafeSpotting(player, npc, scaling);
        
        // Enhanced blade taunts with scaling-based frequency
        performEnhancedBladeTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentBladeAttackWithWarning(npc, player, targets, defs, scaling);
    }

    /**
     * Initialize blade combat session using BossBalancer v5.0
     */
    private void initializeBladeCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            specialAttackCount.put(sessionKey, Integer.valueOf(0));
            consecutiveAvoidedStrikes.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastStrikeHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced blade combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentBladeWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for blade combat
            performInitialBladeArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial blade armor analysis
     */
    private void performInitialBladeArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=9900ff>Blade Analysis: Weak points detected. The blade seeks exposed flesh!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=6600cc>Blade Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance). Skill transcends armor...</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from blade attacks
     */
    private int applyHPAwareBladeDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (blade attacks are precision-focused)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on blade attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "blade_master":
                case "ultimate_technique":
                    damagePercent = BLADE_MASTER_DAMAGE_PERCENT;
                    break;
                case "epic_special":
                case "legendary_strike":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "berserker_strike":
                case "berserker_combo":
                    damagePercent = BERSERKER_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 82% of current HP for blade
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
     * NEW v5.0: Send HP warning if player is in danger from blade attacks
     */
    private void checkAndWarnLowHPForBlade(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming blade damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.68) {
                    player.sendMessage("<col=ff0000>BLADE WARNING: This strike will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.48) {
                    player.sendMessage("<col=9900ff>BLADE WARNING: Heavy blade damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent blade welcome message based on power analysis
     */
    private String getIntelligentBladeWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Sotapana";
        
        message.append("<col=9900ff>").append(npcName).append(" draws his legendary blade, analyzing your combat prowess (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=6600cc>[Blade mastery enhanced: +").append(diffIncrease).append("% technique precision]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Blade restraint: -").append(assistance).append("% strike power]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Blade resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full blade protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent blade guidance with power-based scaling awareness
     */
    private void provideIntelligentBladeGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentBladeGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent blade guidance message based on power analysis
     */
    private String getIntelligentBladeGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentBladePhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getBladeScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=6600cc>Blade Analysis: Missing armor exposes you to precise cuts! Blade damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME blade mastery due to scaling!)" : "";
                    return "<col=9900ff>Blade Analysis: Berserker phase reached. Blade techniques dramatically enhanced" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=660099>Blade Analysis: Ultimate blade master transformation! Maximum technique mastery unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=6600cc>Blade Analysis: Extreme blade scaling detected! Consider facing legendary masters!</col>";
                }
                break;
                
            case 3:
                // Fourth warning: Advanced tactics
                return "<col=9966cc>Blade Tactics: Watch for epic techniques and berserker combos. Maintain distance during special attacks!</col>";
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get blade scaling analysis message
     */
    private String getBladeScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Sotapana";
        
        String baseMessage = "<col=DDA0DD>Blade Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>" + npcName + "'s blade technique restrained! Strike damage reduced by " + 
                   assistancePercent + "% due to insufficient combat preparation.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=6600cc>" + npcName + "'s blade mastery escalated! Strike damage increased by " + 
                   difficultyIncrease + "% due to superior combat capabilities.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=9900ff>Balanced blade encounter. Optimal combat positioning achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight combat advantage detected. " + npcName + "'s blade intensity increased by " + 
                   difficultyIncrease + "% for honorable balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Blade power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during blade combat
     */
    private void monitorBladeScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getBladeScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get blade scaling change message
     */
    private String getBladeScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Blade Update: Combat balance improved! Blade restraint reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=6600cc>Blade Update: Blade mastery now active due to increased combat power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Blade Update: Damage resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=9900ff>Blade Update: Combat protection restored! Blade damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent blade phase tracking with BossBalancer integration
     */
    private void updateIntelligentBladePhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentBladePhase(npc);
        
        Integer lastPhase = currentBladePhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentBladePhase.put(npcKey, newPhase);
            handleIntelligentBladePhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current blade phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentBladePhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= BLADE_MASTER_THRESHOLD) return 4;
            if (hpPercent <= BERSERKER_THRESHOLD) return 3;
            if (hpPercent <= SWORDSMAN_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent blade phase transitions with scaling integration
     */
    private void handleIntelligentBladePhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "The blade master";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("Enhanced swordsmanship!"));
            npc.setNextGraphics(new Graphics(MELEE_GRAPHICS));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED BERSERKER MASTERY UNLEASHED!" : "MY BLADE THIRSTS FOR BLOOD!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(BERSERKER_GRAPHICS));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE BLADE MASTER TRANSFORMATION - MAXIMUM TECHNIQUE POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED MASTER FORM!" : "Witness true blade mastery!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(PHANTOM_GRAPHICS));
            break;
        }
    }

    /**
     * NEW v5.0: Check for blade safe spotting
     */
    private void checkBladeSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveAvoidedStrikes.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastStrikeHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect blade-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean bladeFrustrated = consecutiveCount > 3; // Blade masters need honor
        boolean recentAvoidance = !lastHit;
        
        boolean bladeSafeSpot = playerDistanced && bladeFrustrated && recentAvoidance;
        
        if (bladeSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating blade-themed responses
            if (warningCount >= 3) {
                performBladeAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting honorably
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=9900ff>The blade master nods with respect for your honorable combat...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform blade anti-safe spot measure
     */
    private void performBladeAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=6600cc>Blade mastery seeks those who avoid honorable combat!</col>");
        
        // Dimensional slash that reaches through obstacles
        npc.setNextAnimation(new Animation(SPECIAL_ANIMATION));
        npc.setNextForceTalk(new ForceTalk("COWARD! Face the blade with honor!"));
        
        // Enhanced damage based on scaling
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.5) : 350; // Blade pursuit
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareBladeDamageScaling(scaledDamage, player, "blade_pursuit");
        
        delayHit(npc, 1, player, getMeleeHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>BLADE PENALTY: Safe spotting detected - dimensional slash ignores barriers!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced blade taunts with scaling-based frequency
     */
    private void performEnhancedBladeTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on blade phase and scaling
        int bladePhase = getCurrentBladePhase(npc);
        int tauntChance = 9 + (bladePhase * 5); // Base 14% to 29% based on phase
        
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance += 13; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance += 9; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced blade taunts based on phase and scaling
            performScaledBladeTaunt(npc, bladePhase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled blade taunt based on phase and scaling
     */
    private void performScaledBladeTaunt(NPC npc, int bladePhase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "The blade thirsts!",
            "Witness true swordsmanship!",
            "Your technique is lacking!",
            "Feel the edge of perfection!",
            "Honor the blade!",
            "Skill conquers all!",
            "The way of the sword!"
        };
        
        String[] bladeTaunts = {
            "BLADE MASTERY UNLEASHED!",
            "SWORDSMANSHIP PERFECTION!",
            "LEGENDARY TECHNIQUE ACTIVATED!",
            "WITNESS TRUE BLADE ARTISTRY!",
            "THE BLADE COMMANDS RESPECT!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED BLADE MASTERY ACTIVATED!",
            "YOUR SUPERIOR DEFENSES SHARPEN MY TECHNIQUE!",
            "MAXIMUM BLADE POWER UNLEASHED!",
            "ULTIMATE SWORD MASTER'S DOMINION!",
            "TRANSCENDENT BLADE ARTISTRY!"
        };
        
        String selectedTaunt;
        if (isHighScaling && bladePhase >= 3) {
            // Use enhanced taunts for high scaling + high blade power
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else if (bladePhase >= 2) {
            // Use blade taunts for high blade phases
            selectedTaunt = bladeTaunts[Utils.random(bladeTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent blade warning system
     */
    private int performIntelligentBladeAttackWithWarning(NPC npc, Player player, ArrayList<Entity> targets, NPCCombatDefinitions defs, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Check for special attacks first
            Integer specialCount = specialAttackCount.get(playerKey);
            if (specialCount == null) specialCount = 0;
            
            if (Utils.random(SPECIAL_ATTACK_CHANCE) == 0 && specialCount < MAX_SPECIAL_ATTACKS_PER_FIGHT) {
                specialAttackCount.put(playerKey, specialCount + 1);
                return performEpicSpecialAttackEnhanced(npc, player, targets, getCurrentBladePhase(npc) >= 3, scaling);
            }
            
            // Determine combat mode based on phase
            int bladePhase = getCurrentBladePhase(npc);
            if (bladePhase >= 3) {
                return executeBerserkerCombatEnhanced(npc, player, targets, scaling);
            } else {
                return executeNormalCombatEnhanced(npc, player, targets, defs, scaling);
            }
            
        } catch (Exception e) {
            return 5;
        }
    }

    /**
     * ===== ENHANCED BERSERKER COMBAT: Full BossBalancer integration =====
     */
    private int executeBerserkerCombatEnhanced(NPC npc, Player player, ArrayList<Entity> targets, CombatScaling scaling) {
        int berserkerAttack = Utils.random(3);
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastStrikeHit.put(playerKey, Boolean.TRUE); // Berserker attacks are more accurate
        
        switch (berserkerAttack) {
            case 0:
                return performBerserkerStrikeEnhanced(npc, player, scaling);
            case 1:
                return performBladeDanceEnhanced(npc, targets, true, scaling);
            case 2:
                return performShadowDashEnhanced(npc, player, targets, true, scaling);
            default:
                return performBerserkerStrikeEnhanced(npc, player, scaling);
        }
    }
    
    /**
     * ===== ENHANCED NORMAL COMBAT: Full BossBalancer integration =====
     */
    private int executeNormalCombatEnhanced(NPC npc, Player player, ArrayList<Entity> targets, NPCCombatDefinitions defs, CombatScaling scaling) {
        int attackChoice = Utils.random(10);
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        if (attackChoice <= 2) {
            // 30% chance for area attack
            boolean hit = performSlashAttackEnhanced(npc, player, defs, targets, scaling);
            lastStrikeHit.put(playerKey, hit);
            updateAvoidedStrikes(playerKey, hit);
            return defs.getAttackDelay();
        } else {
            // 70% chance for single target attack
            boolean hit = performPhantomStrikeEnhanced(npc, player, defs, scaling);
            lastStrikeHit.put(playerKey, hit);
            updateAvoidedStrikes(playerKey, hit);
            return defs.getAttackDelay();
        }
    }

    /**
     * Update avoided strikes tracking
     */
    private void updateAvoidedStrikes(Integer playerKey, boolean hit) {
        Integer avoidedCount = consecutiveAvoidedStrikes.get(playerKey);
        if (avoidedCount == null) avoidedCount = 0;
        if (!hit) {
            consecutiveAvoidedStrikes.put(playerKey, avoidedCount + 1);
        } else {
            consecutiveAvoidedStrikes.put(playerKey, 0);
        }
    }

    /**
     * ===== ENHANCED SPECIAL ATTACKS: Full BossBalancer integration =====
     */
    private int performEpicSpecialAttackEnhanced(NPC npc, Player player, ArrayList<Entity> targets, boolean berserkerMode, CombatScaling scaling) {
        int specialType = Utils.random(4);
        
        // Strategic special attack announcement with HP warning
        player.sendMessage("<col=ff6600>[Special Attack] Incoming epic blade technique!</col>");
        
        switch (specialType) {
            case 0:
                return performSpiritLightningEnhanced(npc, targets, berserkerMode, scaling);
            case 1:
                return performEarthShatterEnhanced(npc, targets, berserkerMode, scaling);
            case 2:
                return performShadowDashEnhanced(npc, player, targets, berserkerMode, scaling);
            case 3:
                return performBladeDanceEnhanced(npc, targets, berserkerMode, scaling);
            default:
                return performSpiritLightningEnhanced(npc, targets, berserkerMode, scaling);
        }
    }

    /**
     * ENHANCED SPIRIT LIGHTNING: With BossBalancer scaling and HP-aware damage
     */
    private int performSpiritLightningEnhanced(NPC npc, ArrayList<Entity> targets, boolean berserkerMode, CombatScaling scaling) {
        if (npc == null) return 4;
        
        try {
            npc.setNextAnimation(new Animation(SPECIAL_ANIMATION));
            if (Utils.random(3) == 0) {
                npc.setNextForceTalk(new ForceTalk("Witness spectral fury incarnate!"));
            }
            
            // Strategic guidance with timing
            if (!targets.isEmpty() && targets.get(0) instanceof Player) {
                Player player = (Player) targets.get(0);
                
                // Give players time to spread out
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        player.sendMessage("<col=00ffff>[Lightning Warning] Chain attack incoming - spread out from allies!</col>");
                        stop();
                    }
                }, 1);
            }
            
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    try {
                        int hitCount = 0;
                        for (Entity t : targets) {
                            if (hitCount >= 4) break;
                            if (isValidTarget(t)) {
                                t.setNextGraphics(new Graphics(LIGHTNING_GRAPHICS));
                                
                                // Apply BossBalancer scaling and HP-aware damage
                                int baseDamage = 250;
                                if (t instanceof Player && scaling != null) {
                                    baseDamage = BossBalancer.calculateNPCDamageToPlayer(npc, (Player) t, baseDamage);
                                    
                                    // Apply HP-aware scaling
                                    double multiplier = berserkerMode ? BERSERKER_MULTIPLIER : SPECIAL_MULTIPLIER;
                                    int scaledDamage = (int) (baseDamage * multiplier * 0.8);
                                    int safeDamage = applyHPAwareBladeDamageScaling(scaledDamage, (Player) t, "epic_special");
                                    
                                    checkAndWarnLowHPForBlade((Player) t, safeDamage);
                                    delayHit(npc, 0, t, new Hit(npc, safeDamage, HitLook.MAGIC_DAMAGE));
                                } else {
                                    double multiplier = berserkerMode ? BERSERKER_MULTIPLIER : SPECIAL_MULTIPLIER;
                                    int damage = Utils.random((int)(baseDamage * multiplier * 0.8) + 1);
                                    delayHit(npc, 0, t, new Hit(npc, damage, HitLook.MAGIC_DAMAGE));
                                }
                                hitCount++;
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error in Spirit Lightning Enhanced: " + e.getMessage());
                    } finally {
                        stop();
                    }
                }
            }, 3);
            
            return berserkerMode ? 3 : 5;
            
        } catch (Exception e) {
            System.err.println("Error performing Spirit Lightning Enhanced: " + e.getMessage());
            return 4;
        }
    }

    /**
     * ENHANCED EARTH SHATTER: With full scaling integration and HP-aware damage
     */
    private int performEarthShatterEnhanced(NPC npc, ArrayList<Entity> targets, boolean berserkerMode, CombatScaling scaling) {
        if (npc == null) return 4;
        
        try {
            npc.setNextAnimation(new Animation(SPECIAL_ANIMATION));
            if (Utils.random(3) == 0) {
                npc.setNextForceTalk(new ForceTalk("The earth trembles before my blade!"));
            }
            
            // Strategic warning with reaction time
            if (!targets.isEmpty() && targets.get(0) instanceof Player) {
                Player player = (Player) targets.get(0);
                player.sendMessage("<col=ffff00>[Earth Shatter] Massive area attack - defensive abilities recommended!</col>");
            }
            
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    try {
                        npc.setNextGraphics(new Graphics(SHATTER_GRAPHICS));
                        
                        for (Entity t : targets) {
                            if (isValidTarget(t)) {
                                // Enhanced BossBalancer integration with HP-aware damage
                                int baseDamage = 280;
                                if (t instanceof Player && scaling != null) {
                                    baseDamage = BossBalancer.calculateNPCDamageToPlayer(npc, (Player) t, baseDamage);
                                    
                                    double multiplier = berserkerMode ? BERSERKER_MULTIPLIER : EPIC_MULTIPLIER;
                                    int scaledDamage = (int) (baseDamage * multiplier);
                                    int safeDamage = applyHPAwareBladeDamageScaling(scaledDamage, (Player) t, "epic_special");
                                    
                                    checkAndWarnLowHPForBlade((Player) t, safeDamage);
                                    delayHit(npc, 0, t, new Hit(npc, safeDamage, HitLook.MELEE_DAMAGE));
                                } else {
                                    double multiplier = berserkerMode ? BERSERKER_MULTIPLIER : EPIC_MULTIPLIER;
                                    int damage = Utils.random((int) (baseDamage * multiplier) + 1);
                                    delayHit(npc, 0, t, new Hit(npc, damage, HitLook.MELEE_DAMAGE));
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error in Earth Shatter Enhanced: " + e.getMessage());
                    } finally {
                        stop();
                    }
                }
            }, 3);
            
            return berserkerMode ? 4 : 6;
            
        } catch (Exception e) {
            System.err.println("Error performing Earth Shatter Enhanced: " + e.getMessage());
            return 4;
        }
    }

    /**
     * ENHANCED SHADOW DASH: With scaling and better targeting and HP-aware damage
     */
    private int performShadowDashEnhanced(NPC npc, Entity target, ArrayList<Entity> targets, boolean berserkerMode, CombatScaling scaling) {
        if (npc == null || target == null) return 4;
        
        try {
            Entity furthestTarget = target;
            double maxDistance = 0;
            
            // Find furthest valid target
            for (Entity t : targets) {
                if (t instanceof Player && isValidTarget(t)) {
                    double distance = npc.getDistance(t);
                    if (distance > maxDistance) {
                        maxDistance = distance;
                        furthestTarget = t;
                    }
                }
            }
            
            npc.setNextAnimation(new Animation(DASH_ANIMATION));
            if (Utils.random(4) == 0) {
                npc.setNextForceTalk(new ForceTalk("Shadow strike from the void!"));
            }
            
            // Strategic warning
            if (furthestTarget instanceof Player) {
                Player player = (Player) furthestTarget;
                player.sendMessage("<col=9900ff>[Shadow Dash] Targeting furthest player! Phantom afterimages hit nearby allies!</col>");
            }
            
            final Entity finalTarget = furthestTarget;
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    try {
                        if (isValidTarget(finalTarget)) {
                            // Enhanced damage calculation with BossBalancer and HP-aware scaling
                            int baseDamage = 320;
                            if (finalTarget instanceof Player && scaling != null) {
                                baseDamage = BossBalancer.calculateNPCDamageToPlayer(npc, (Player) finalTarget, baseDamage);
                                
                                double multiplier = berserkerMode ? BERSERKER_MULTIPLIER : EPIC_MULTIPLIER;
                                int scaledDamage = (int) (baseDamage * multiplier * 1.2);
                                int safeDamage = applyHPAwareBladeDamageScaling(scaledDamage, (Player) finalTarget, "epic_special");
                                
                                checkAndWarnLowHPForBlade((Player) finalTarget, safeDamage);
                                delayHit(npc, 0, finalTarget, new Hit(npc, safeDamage, HitLook.MELEE_DAMAGE));
                            } else {
                                double multiplier = berserkerMode ? BERSERKER_MULTIPLIER : EPIC_MULTIPLIER;
                                int damage = Utils.random((int) (baseDamage * multiplier * 1.2) + 1);
                                delayHit(npc, 0, finalTarget, new Hit(npc, damage, HitLook.MELEE_DAMAGE));
                            }
                            
                            // Phantom afterimage hits with scaling
                            for (Entity t : targets) {
                                if (t != finalTarget && isValidTarget(t) && t.getDistance(finalTarget) <= 2) {
                                    if (t instanceof Player && scaling != null) {
                                        int phantomBaseDamage = baseDamage / 3;
                                        phantomBaseDamage = BossBalancer.calculateNPCDamageToPlayer(npc, (Player) t, phantomBaseDamage);
                                        int safeDamage = applyHPAwareBladeDamageScaling(phantomBaseDamage, (Player) t, "phantom_strike");
                                        
                                        t.setNextGraphics(new Graphics(PHANTOM_GRAPHICS));
                                        delayHit(npc, 0, t, new Hit(npc, safeDamage, HitLook.MELEE_DAMAGE));
                                    } else {
                                        t.setNextGraphics(new Graphics(PHANTOM_GRAPHICS));
                                        delayHit(npc, 0, t, new Hit(npc, baseDamage / 3, HitLook.MELEE_DAMAGE));
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error in Shadow Dash Enhanced: " + e.getMessage());
                    } finally {
                        stop();
                    }
                }
            }, 2);
            
            return berserkerMode ? 3 : 5;
            
        } catch (Exception e) {
            System.err.println("Error performing Shadow Dash Enhanced: " + e.getMessage());
            return 4;
        }
    }

    /**
     * ENHANCED BLADE DANCE: Multi-hit with full scaling and HP-aware damage
     */
    private int performBladeDanceEnhanced(NPC npc, ArrayList<Entity> targets, boolean berserkerMode, CombatScaling scaling) {
        if (npc == null) return 4;
        
        try {
            npc.setNextAnimation(new Animation(SPIN_ANIMATION));
            if (Utils.random(4) == 0) {
                npc.setNextForceTalk(new ForceTalk("Dance of a thousand cuts!"));
            }
            
            // Strategic warning with timing
            if (!targets.isEmpty() && targets.get(0) instanceof Player) {
                Player player = (Player) targets.get(0);
                player.sendMessage("<col=ff9900>[Blade Dance] 3-hit spinning combo - prepare defensive measures!</col>");
            }
            
            WorldTasksManager.schedule(new WorldTask() {
                int spins = 0;
                @Override
                public void run() {
                    try {
                        if (spins >= 3) {
                            stop();
                            return;
                        }
                        
                        for (Entity t : targets) {
                            if (isValidTarget(t)) {
                                // BossBalancer integration for each hit with HP-aware damage
                                int baseDamage = 160;
                                if (t instanceof Player && scaling != null) {
                                    baseDamage = BossBalancer.calculateNPCDamageToPlayer(npc, (Player) t, baseDamage);
                                    
                                    double multiplier = berserkerMode ? BERSERKER_MULTIPLIER : AREA_ATTACK_MULTIPLIER;
                                    int scaledDamage = (int) (baseDamage * multiplier * 0.6);
                                    int safeDamage = applyHPAwareBladeDamageScaling(scaledDamage, (Player) t, berserkerMode ? "berserker_combo" : "blade_dance");
                                    
                                    if (spins == 0) checkAndWarnLowHPForBlade((Player) t, safeDamage * 3); // Warn for total combo damage
                                    delayHit(npc, 0, t, new Hit(npc, safeDamage, HitLook.MELEE_DAMAGE));
                                } else {
                                    double multiplier = berserkerMode ? BERSERKER_MULTIPLIER : AREA_ATTACK_MULTIPLIER;
                                    int damage = Utils.random((int) (baseDamage * multiplier * 0.6) + 1);
                                    delayHit(npc, 0, t, new Hit(npc, damage, HitLook.MELEE_DAMAGE));
                                }
                            }
                        }
                        spins++;
                    } catch (Exception e) {
                        System.err.println("Error in Blade Dance Enhanced spin " + spins + ": " + e.getMessage());
                        stop();
                    }
                }
            }, 1, 1);
            
            return berserkerMode ? 4 : 6;
            
        } catch (Exception e) {
            System.err.println("Error performing Blade Dance Enhanced: " + e.getMessage());
            return 4;
        }
    }

    /**
     * ENHANCED BERSERKER STRIKE: Full BossBalancer integration with HP-aware damage
     */
    private int performBerserkerStrikeEnhanced(NPC npc, Player player, CombatScaling scaling) {
        if (npc == null || player == null) return 4;
        
        try {
            npc.setNextAnimation(new Animation(Utils.random(2) == 0 ? MELEE_ANIMATION_1 : MELEE_ANIMATION_2));
            npc.setNextGraphics(new Graphics(BERSERKER_GRAPHICS));
            
            if (Utils.random(8) == 0) {
                npc.setNextForceTalk(new ForceTalk("Blood feeds my cursed blade!"));
            }
            
            // Enhanced damage with full BossBalancer integration and HP-aware scaling
            int baseDamage = 300;
            if (scaling != null) {
                baseDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
            }
            
            int scaledDamage = (int) (baseDamage * BERSERKER_MULTIPLIER);
            int safeDamage = applyHPAwareBladeDamageScaling(scaledDamage, player, "berserker_strike");
            
            checkAndWarnLowHPForBlade(player, safeDamage);
            Hit hit = getMeleeHit(npc, safeDamage);
            delayHit(npc, 1, player, hit);
            
            return 2; // Very fast berserker attacks
            
        } catch (Exception e) {
            System.err.println("Error in berserker strike enhanced: " + e.getMessage());
            return 4;
        }
    }

    /**
     * ENHANCED SLASH ATTACK: With BossBalancer scaling and HP-aware damage
     */
    private boolean performSlashAttackEnhanced(NPC npc, Player player, NPCCombatDefinitions defs, ArrayList<Entity> targets, CombatScaling scaling) {
        if (npc == null || player == null || defs == null) return false;
        
        try {
            npc.setNextAnimation(new Animation(Utils.random(2) == 0 ? MELEE_ANIMATION_1 : MELEE_ANIMATION_2));
            npc.setNextGraphics(new Graphics(MELEE_GRAPHICS));
            
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    try {
                        for (Entity t : targets) {
                            if (isValidTarget(t)) {
                                // BossBalancer scaling for area attacks with HP-aware damage
                                int baseDamage = 200;
                                if (t instanceof Player && scaling != null) {
                                    baseDamage = BossBalancer.calculateNPCDamageToPlayer(npc, (Player) t, baseDamage);
                                    
                                    int scaledDamage = (int) (baseDamage * AREA_ATTACK_MULTIPLIER);
                                    int safeDamage = applyHPAwareBladeDamageScaling(scaledDamage, (Player) t, "slash_attack");
                                    
                                    delayHit(npc, 0, t, new Hit(npc, safeDamage, HitLook.MELEE_DAMAGE));
                                } else {
                                    int damage = Utils.random((int) (baseDamage * AREA_ATTACK_MULTIPLIER) + 1);
                                    delayHit(npc, 0, t, new Hit(npc, damage, HitLook.MELEE_DAMAGE));
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error in slash attack enhanced: " + e.getMessage());
                    } finally {
                        stop();
                    }
                }
            }, 1);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error performing slash attack enhanced: " + e.getMessage());
            return false;
        }
    }

    /**
     * ENHANCED PHANTOM STRIKE: With prayer drain, scaling, and HP-aware damage
     */
    private boolean performPhantomStrikeEnhanced(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        if (npc == null || player == null || defs == null) return false;
        
        try {
            npc.setNextAnimation(new Animation(Utils.random(2) == 0 ? MELEE_ANIMATION_1 : MELEE_ANIMATION_2));
            npc.setNextGraphics(new Graphics(PHANTOM_GRAPHICS));
            
            // Enhanced prayer drain with BossBalancer consideration
            if (Utils.random(PRAYER_DRAIN_CHANCE) == 0) {
                int drainAmount = Utils.random(PRAYER_DRAIN_MIN, PRAYER_DRAIN_MAX + 1);
                
                // Scale drain based on player tier (higher tier = more drain resistance)
                if (scaling != null && scaling.playerTier >= 7) {
                    drainAmount = (int) (drainAmount * 0.8); // 20% reduction for high tier
                }
                
                player.getPrayer().drainPrayer(drainAmount);
                
                if (Utils.random(4) == 0) {
                    player.sendMessage("<col=9900ff>[Phantom Touch] " + drainAmount + " prayer drained by spectral energy!</col>");
                }
                
                if (Utils.random(6) == 0) {
                    npc.setNextForceTalk(new ForceTalk("Your spirit weakens!"));
                }
            }
            
            // Enhanced damage with BossBalancer integration and HP-aware scaling
            int baseDamage = 240;
            if (scaling != null) {
                baseDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
            }
            
            int scaledDamage = (int) (baseDamage * SINGLE_TARGET_MULTIPLIER);
            int safeDamage = applyHPAwareBladeDamageScaling(scaledDamage, player, "phantom_strike");
            
            Hit hit = getMeleeHit(npc, safeDamage);
            delayHit(npc, 1, player, hit);
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error in phantom strike enhanced: " + e.getMessage());
            return false;
        }
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
    
    private boolean isValidTarget(Entity target) {
        if (target == null) return false;
        
        try {
            return !target.isDead() && !target.hasFinished();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ENHANCED v5.0: Handle blade combat end with proper cleanup
     */
    public static void onBladeCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentBladePhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            specialAttackCount.remove(playerKey);
            consecutiveAvoidedStrikes.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastStrikeHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=9900ff>Blade combat session ended. Technique scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("Sotapana: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during blade combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Blade scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("Sotapana: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onBladeCombatEnd(player, null);
        }
    }
}