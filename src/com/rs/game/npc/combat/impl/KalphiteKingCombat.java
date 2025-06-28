package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.NewForceMovement;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.kalphite.KalphiteKing;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Kalphite King Combat System with FULL BossBalancer v5.0 Integration
 * Features: Intelligent power scaling, HP-aware damage caps, armor analysis
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 8.1 - FULL BossBalancer v5.0 Integration with HP-Aware Damage Scaling
 */
public class KalphiteKingCombat extends CombatScript {

    // Combat tracking
    private long lastSuccessfulHit = System.currentTimeMillis();
    private int safeSpotViolations = 0;
    
    // Enhanced guidance system - intelligent scaling aware
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, String> lastForm = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> formSwitches = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    
    // Timing constants - enhanced for v5.0
    private static final long WARNING_COOLDOWN = 300000; // 5 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 3000; // 3 seconds for KK attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features
    
    // HP-aware damage scaling constants - CRITICAL SAFETY SYSTEM
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.32; // Max 32% of player HP per hit (slightly lower than Corp)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.50;  // Max 50% for critical attacks (green mark, etc.)
    private static final double DRAIN_DAMAGE_PERCENT = 0.22;     // Max 22% for drain/bleed attacks
    private static final double STOMP_DAMAGE_PERCENT = 0.42;     // Max 42% for stomp (AOE penalty)
    private static final double CHARGE_DAMAGE_PERCENT = 0.48;    // Max 48% for charge attacks
    private static final double GREEN_DAMAGE_PERCENT = 0.65;     // Max 65% for green mark (signature attack)
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 480;          // Hard cap (32% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 45;               // Minimum damage to prevent 0 hits

    @Override
    public Object[] getKeys() {
        return new Object[] { 16697, 16698, 16699 };
    }

    @Override
    public int attack(NPC npc, Entity target) {
        // FIXED: Safe casting with type check
        if (!(npc instanceof KalphiteKing)) {
            // Not actually a KalphiteKing instance - use basic attack
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null || !(target instanceof Player)) {
                return 4;
            }
            
            Player player = (Player) target;
            
            // Basic scaled attack for non-KalphiteKing NPCs with these IDs
            npc.setNextAnimation(new Animation(defs.getAttackEmote()));
            int baseDamage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
            
            try {
                // Try to use BossBalancer if available
                CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, player, npc);
                int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "melee");
                delayHit(npc, 0, target, getMeleeHit(npc, safeDamage));
            } catch (Exception e) {
                // Fallback to basic attack if BossBalancer fails
                delayHit(npc, 0, target, getMeleeHit(npc, baseDamage));
            }
            
            return defs.getAttackDelay();
        }
        
        // Safe cast - we've verified it's a KalphiteKing
        KalphiteKing king = (KalphiteKing) npc;
        final NPCCombatDefinitions defs = king.getCombatDefinitions();
        
        if (!(target instanceof Player)) {
            return defs.getAttackDelay();
        }
        
        Player player = (Player) target;

        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeCombatSession(player, king);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, king);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentGuidance(player, king, scaling);
        
        // Monitor scaling changes during combat
        monitorScalingChanges(player, scaling);
        
        // Anti-safe spot system with intelligent scaling
        checkRaidBossSafeSpotting(king, player, scaling);
        
        // Track form switches for guidance
        trackFormSwitches(player, king);

        king.setForceFollowClose(king.getId() == 16697);
        
        // Shield activation with v5.0 scaling
        handleIntelligentShieldActivation(king, player, scaling);
        
        // Enhanced phase transition logic with v5.0 intelligence
        handleIntelligentPhaseTransitions(king, player, scaling);

        if (Utils.random(7) == 0)
            king.switchPhase();

        // Form-specific combat with v5.0 scaling and HP-aware warnings
        int attackDelay = executeIntelligentFormSpecificCombatWithWarnings(npc, player, king, defs, scaling);
        
        return getIntelligentAttackDelay(attackDelay, scaling);
    }
    /**
     * Initialize combat session using BossBalancer v5.0
     */
    private void initializeCombatSession(Player player, KalphiteKing king) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, king);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            formSwitches.put(sessionKey, Integer.valueOf(0));
            lastForm.put(sessionKey, "");
            lastScalingType.put(sessionKey, "UNKNOWN");
            
            // Send v5.0 enhanced combat message
            String welcomeMsg = "<col=4169E1>Kalphite King awakens. Royal power analysis active (v5.0).</col>";
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor check
            performInitialArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial armor analysis
     */
    private void performInitialArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=ff6600>⚠ Royal Analysis: Missing protection detected. Kalphite King will deal increased damage!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=00ff00>✓ Royal Analysis: Full protection active (" + 
                                 String.format("%.1f", reductionPercent) + "% damage reduction).</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * ENHANCED v5.0: Intelligent guidance with power-based scaling awareness
     */
    private void provideIntelligentGuidance(Player player, KalphiteKing king, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentGuidanceMessage(player, king, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent guidance message based on power analysis
     */
    private String getIntelligentGuidanceMessage(Player player, KalphiteKing king, CombatScaling scaling, int stage) {
        double hpPercent = getHealthPercentage(king);
        Integer switches = formSwitches.get(Integer.valueOf(player.getIndex()));
        if (switches == null) switches = 0;
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getScalingAnalysisMessage(scaling);
                
            case 1:
                // Second warning: Equipment effectiveness or form mechanics
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=ff3300>Royal Analysis: Incomplete armor detected! Kalphite King damage increased by 25%. Equip missing pieces!</col>";
                } else if (switches >= 2 || hpPercent <= 0.5) {
                    return "<col=ff6600>Royal Analysis: Form mastery phase. Multi-form adaptation and royal abilities active!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or critical mechanics
                if (hpPercent <= 0.25) {
                    return "<col=ff0000>Royal Analysis: Final royal fury! All forms mastered. Maximum aggression protocols engaged!</col>";
                }
                break;
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get scaling analysis message based on intelligent power calculation
     */
    private String getScalingAnalysisMessage(CombatScaling scaling) {
        String baseMessage = "<col=66ccff>Royal Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=00ff00>Assistance mode active! King difficulty reduced by " + 
                   assistancePercent + "% due to gear disadvantage.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=ff6600>Anti-farming scaling active! King difficulty increased by " + 
                   difficultyIncrease + "% due to gear advantage.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=ffffff>Balanced royal encounter detected. Optimal gear-to-king ratio achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=ffaa00>Slight overgear detected. King difficulty increased by " + 
                   difficultyIncrease + "% for balance.</col>";
        }
        
        return baseMessage + "<col=cccccc>Power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during combat
     */
    private void monitorScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get scaling change message
     */
    private String getScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=00ff00>Royal Update: Combat scaling improved to balanced! Assistance reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=ff9900>Royal Update: Anti-farming scaling now active due to increased power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=66ccff>Royal Update: Absorption bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=00ff00>Royal Update: Full armor protection restored! Damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills
     */
    private int applyHPAwareDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (more dangerous when wounded)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "green":
                case "greenmark":
                    damagePercent = GREEN_DAMAGE_PERCENT;
                    break;
                case "charge":
                case "rush":
                    damagePercent = CHARGE_DAMAGE_PERCENT;
                    break;
                case "critical":
                case "incendiary":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "bleed":
                case "drain":
                    damagePercent = DRAIN_DAMAGE_PERCENT;
                    break;
                case "stomp":
                case "aoe":
                    damagePercent = STOMP_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 85% of current HP
            if (currentHP > 0) {
                int emergencyCap = (int)(currentHP * 0.85);
                safeDamage = Math.min(safeDamage, emergencyCap);
            }
            
            return safeDamage;
            
        } catch (Exception e) {
            // Fallback to absolute cap
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
    }

    /**
     * NEW v5.0: Get player HP status for damage calculations
     */
    private String getPlayerHPStatus(Player player) {
        if (player == null) return "UNKNOWN";
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            double hpPercent = (double)currentHP / maxHP;
            
            if (hpPercent <= 0.25) return "CRITICAL";
            if (hpPercent <= 0.50) return "LOW";
            if (hpPercent <= 0.75) return "MODERATE";
            return "HEALTHY";
            
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * NEW v5.0: Send HP warning if player is in danger
     */
    private void checkAndWarnLowHP(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            String hpStatus = getPlayerHPStatus(player);
            
            // Warn if incoming damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.75) {
                    player.sendMessage("<col=ff0000>⚠ CRITICAL: Royal attack will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.55 && hpStatus.equals("LOW")) {
                    player.sendMessage("<col=ff6600>⚠ WARNING: Heavy royal damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * Get current health percentage
     */
    private double getHealthPercentage(KalphiteKing king) {
        NPCCombatDefinitions defs = king.getCombatDefinitions();
        if (defs == null) return 1.0;
        return (double) king.getHitpoints() / defs.getHitpoints();
    }

    /**
     * ENHANCED v5.0: Handle intelligent shield activation with v5.0 scaling
     */
    private void handleIntelligentShieldActivation(KalphiteKing king, Player player, CombatScaling scaling) {
        if (Utils.random(15) == 1 && !king.isShieldActive && !king.hasActivatedShield && king.getHPPercentage() <= 50) {
            king.activateShield();
            
            String shieldMessage = "ROYAL SHIELD PHASE: The King's defenses increase with desperation!";
            if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
                shieldMessage += " Enhanced against underpowered foes!";
            } else if (scaling.scalingType.contains("ANTI_FARMING")) {
                shieldMessage += " Reinforced against overpowered challengers!";
            }
            sendCombatMessage(player, shieldMessage);
        }
    }

    /**
     * ENHANCED v5.0: Handle intelligent phase transitions with power awareness
     */
    private void handleIntelligentPhaseTransitions(KalphiteKing king, Player player, CombatScaling scaling) {
        boolean shouldTransition = false;
        
        if (king.getHPPercentage() < 75 && ((king.getPhase() != 6 && king.getId() == 16699) 
                || (king.getPhase() != 9 && king.getId() == 16697) || king.getId() == 16698)) {
            shouldTransition = true;
            
            // Enhanced battle cries based on v5.0 power analysis
            double playerPower = BossBalancer.calculateActualPlayerPower(player);
            int cryCount = Math.min(3 + (int)(playerPower / 5), 6);
            
            if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
                cryCount += 2; // More aggressive against underpowered players
            } else if (scaling.scalingType.contains("ANTI_FARMING")) {
                cryCount += 3; // Much more aggressive against overpowered players
            }
            
            for (int i = 0; i < cryCount; i++) {
                king.battleCry();
            }
        }
        else if (king.getHPPercentage() < 25 && ((king.getPhase() != 6 && king.getId() == 16699) 
                || (king.getPhase() != 9 && king.getId() == 16697) || king.getId() == 16698)) {
            shouldTransition = true;
            
            // Enhanced final phase intensity with v5.0 intelligence
            double playerPower = BossBalancer.calculateActualPlayerPower(player);
            int finalCryCount = Math.min(6 + (int)(playerPower / 3), 10);
            
            if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
                finalCryCount += 3; // Much more intense against underpowered players
            } else if (scaling.scalingType.contains("ANTI_FARMING")) {
                finalCryCount += 4; // Extremely intense against overpowered players
            }
            
            for (int i = 0; i < finalCryCount; i++) {
                king.battleCry();
            }
        }
        
        // Execute phase transitions
        if (shouldTransition) {
            if (!(king.getPhase() == 5 && king.getId() == 16697) && !(king.getId() == 16697))
                king.nextPhase();
            else if (king.getPhase() == 5 && king.getId() == 16699)
                king.setPhase(7);
        }
    }

    /**
     * ENHANCED v5.0: Execute intelligent form-specific combat with HP-aware warnings
     */
    private int executeIntelligentFormSpecificCombatWithWarnings(NPC npc, Player player, KalphiteKing king, 
                                                               NPCCombatDefinitions defs, CombatScaling scaling) {
        
        // Increment attack counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer currentCount = attackCounter.get(playerKey);
        if (currentCount == null) currentCount = 0;
        attackCounter.put(playerKey, currentCount + 1);
        
        // Check for dangerous attacks that need warnings (v5.0 enhanced)
        String warningMessage = getIntelligentDangerousAttackWarning(king, currentCount, scaling);
        
        if (warningMessage != null && shouldGiveIntelligentWarning(currentCount, scaling)) {
            sendPreAttackWarning(player, warningMessage);
            
            // Delay the actual attack to give player time to react
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    executeIntelligentFormSpecificCombat(npc, player, king, defs, scaling);
                    this.stop();
                }
            }, 2); // 1.2 second delay for reaction time
            
            return defs.getAttackDelay() + 2;
        }
        
        // Execute immediately for basic attacks
        return executeIntelligentFormSpecificCombat(npc, player, king, defs, scaling);
    }

    /**
     * ENHANCED v5.0: Get intelligent dangerous attack warning
     */
    private String getIntelligentDangerousAttackWarning(KalphiteKing king, int attackCount, CombatScaling scaling) {
        String intensityPrefix = scaling.scalingType.contains("ANTI_FARMING") ? "ENHANCED " : "";
        
        // Form-specific dangerous attack detection with v5.0 intelligence
        if (king.getId() == 16699) { // Ranged form
            if (king.getPhase() == 5) {
                return intensityPrefix + "GREEN MARK incoming - run away from the King immediately!";
            } else if (king.getPhase() == 7) {
                return intensityPrefix + "INCENDIARY BARRAGE incoming - multiple explosive hits!";
            } else if (king.getPhase() == 2) {
                return intensityPrefix + "STUN ATTACK incoming - prepare for long immobilization!";
            }
        } else if (king.getId() == 16697) { // Melee form
            if (king.getPhase() == 1 || king.getPhase() == 2 || king.getPhase() == 6) {
                return intensityPrefix + "STOMP ATTACK incoming - area damage with defense drain!";
            } else if (king.getPhase() == 4) {
                return intensityPrefix + "PUSH ATTACK incoming - prepare for knockback!";
            } else if (king.getPhase() == 5) {
                return intensityPrefix + "RUSH COMBO incoming - stomp followed by charge!";
            }
        } else if (king.getId() == 16698) { // Magic form
            if (king.getPhase() == 1 || king.getPhase() == 8) {
                return intensityPrefix + "FREEZE MAGIC incoming - prepare for immobilization!";
            } else if (king.getPhase() == 5) {
                return intensityPrefix + "DOUBLE MAGIC BALL incoming - two AOE blasts!";
            } else if (king.getPhase() == 9) {
                return intensityPrefix + "MAGIC BLEED incoming - sustained damage over time!";
            }
        }
        return null;
    }

    /**
     * ENHANCED v5.0: Determine if attack needs warning with scaling awareness
     */
    private boolean shouldGiveIntelligentWarning(int attackCount, CombatScaling scaling) {
        // More frequent warnings for underpowered players
        boolean isUnderpowered = scaling.scalingType.contains("ASSISTANCE");
        int warningFrequency = isUnderpowered ? 8 : 10; // Every 8th vs 10th attack
        
        return attackCount % warningFrequency == 0;
    }

    /**
     * Send pre-attack warning
     */
    private void sendPreAttackWarning(Player player, String warning) {
        player.sendMessage("<col=ff3300>⚠ " + warning + "</col>");
    }

    /**
     * ENHANCED v5.0: Execute intelligent form-specific combat with proper v5.0 scaling
     */
    private int executeIntelligentFormSpecificCombat(NPC npc, Player player, KalphiteKing king, 
                                                   NPCCombatDefinitions defs, CombatScaling scaling) {
        
        // Ranged KK (16699)
        if (npc.getId() == 16699) {
            return executeIntelligentRangedFormCombat(npc, player, king, defs, scaling);
        }
        // Melee KK (16697)
        else if (npc.getId() == 16697) {
            return executeIntelligentMeleeFormCombat(npc, player, king, defs, scaling);
        }
        // Magic KK (16698)
        else if (npc.getId() == 16698) {
            return executeIntelligentMagicFormCombat(npc, player, king, defs, scaling);
        }
        return defs.getAttackDelay();
    }

    /**
     * ENHANCED v5.0: Ranged form combat execution with HP-aware scaling
     */
    private int executeIntelligentRangedFormCombat(NPC npc, Player player, KalphiteKing king, 
                                                 NPCCombatDefinitions defs, CombatScaling scaling) {
        switch (king.getPhase()) {
        case 0:
            rangeBasicIntelligent(npc, player, defs, scaling);
            break;
        case 1:
            rangeFragIntelligent(npc, player, defs, scaling);
            break;
        case 2:
            rangeStunIntelligent(npc, player, defs, scaling);
            break;
        case 3:
            rangeBasicIntelligent(npc, player, defs, scaling);
            break;
        case 4:
            rangeBasicIntelligent(npc, player, defs, scaling);
            break;
        case 5:
            greenIntelligent(npc, player, scaling);
            king.nextPhase();
            return 8;
        case 6:
            if (excecuteGreenIntelligent(npc, player, scaling))
                return defs.getAttackDelay();
            else
                return 1;
        case 7:
            king.setForceFollowClose(false);
            rangeIncendiaryShotIntelligent(npc, player, defs, scaling);
            break;
        case 8:
            rangeBasicIntelligent(npc, player, defs, scaling);
            break;
        case 9:
            digIntelligent(npc, player, scaling);
            king.setPhase(0);
            break;
        }
        king.nextPhase();
        if (king.getPhase() < 0 || king.getPhase() > 9)
            king.setPhase(0);
        return defs.getAttackDelay();
    }

    /**
     * ENHANCED v5.0: Melee form combat execution with HP-aware scaling
     */
    private int executeIntelligentMeleeFormCombat(NPC npc, Player player, KalphiteKing king, 
                                                NPCCombatDefinitions defs, CombatScaling scaling) {
        switch (king.getPhase()) {
        case 0:
            meleeBleedIntelligent(npc, player, defs, scaling);
            break;
        case 1:
            meleeStompIntelligent(npc, player, defs, scaling);
            break;
        case 2:
            meleeStompIntelligent(npc, player, defs, scaling);
            break;
        case 3:
            meleeBleedIntelligent(npc, player, defs, scaling);
            break;
        case 4:
            meleePushIntelligent(npc, player, defs, scaling);
            break;
        case 5:
            meleeStompIntelligent(npc, player, defs, scaling);
            rushIntelligent(npc, player, defs, scaling);
            king.nextPhase();
            return 17;
        case 6:
            meleeStompIntelligent(npc, player, defs, scaling);
            break;
        case 7:
            meleeBleedIntelligent(npc, player, defs, scaling);
            break;
        case 8:
            greenIntelligent(npc, player, scaling);
            king.nextPhase();
            return 8;
        case 9:
            if (excecuteGreenIntelligent(npc, player, scaling))
                return defs.getAttackDelay();
            else
                return 1;
        }
        king.nextPhase();
        if (king.getPhase() < 0 || king.getPhase() > 9)
            king.setPhase(0);
        return defs.getAttackDelay();
    }

    /**
     * ENHANCED v5.0: Magic form combat execution with HP-aware scaling
     */
    private int executeIntelligentMagicFormCombat(NPC npc, Player player, KalphiteKing king, 
                                                NPCCombatDefinitions defs, CombatScaling scaling) {
        switch (king.getPhase()) {
        case 0:
            mageBallIntelligent(npc, player, 0, defs, scaling);
            break;
        case 1:
            mageBallBlueIntelligent(npc, player, defs, scaling);
            break;
        case 2:
            mageBallIntelligent(npc, player, 0, defs, scaling);
            break;
        case 3:
            meleePushIntelligent(npc, player, defs, scaling);
            break;
        case 4:
            digIntelligent(npc, player, scaling);
            break;
        case 5:
            mageBallDoubleIntelligent(npc, player, defs, scaling);
            break;
        case 6:
            mageBallIntelligent(npc, player, 0, defs, scaling);
            break;
        case 7:
            rushIntelligent(npc, player, defs, scaling);
            king.nextPhase();
            return 17;
        case 8:
            mageBallBlueIntelligent(npc, player, defs, scaling);
            break;
        case 9:
            mageBallBleedIntelligent(npc, player, defs, scaling);
            break;
        }
        king.nextPhase();
        if (king.getPhase() < 0 || king.getPhase() > 9)
            king.setPhase(0);
        return defs.getAttackDelay() + 3;
    }

    // ===== INTELLIGENT ATTACK METHODS WITH HP-AWARE SCALING =====

    /**
     * ENHANCED v5.0: Range basic attack with HP-aware scaling
     */
    private void rangeBasicIntelligent(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        npc.setNextAnimation(new Animation(19450));
        
        int baseDamage = getRandomMaxHit(npc, 650, NPCCombatDefinitions.RANGE, player);
        int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, player, npc);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "ranged");
        
        player.applyHit(new Hit(npc, safeDamage, HitLook.RANGE_DAMAGE, 10));
        lastSuccessfulHit = System.currentTimeMillis();
    }

    /**
     * ENHANCED v5.0: Range incendiary attack with HP-aware scaling
     */
    private void rangeIncendiaryShotIntelligent(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        npc.setNextAnimation(new Animation(19450));
        List<Entity> list = ((KalphiteKing) npc).getPossibleTargets();
        Collections.shuffle(list);
        
        // Enhanced volley count based on v5.0 power analysis
        double playerPower = BossBalancer.calculateActualPlayerPower(player);
        int volleyCount = Math.min(3 + (int)(playerPower / 8), 5);
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            volleyCount += 1; // Extra volley against underpowered players
        } else if (scaling.scalingType.contains("ANTI_FARMING")) {
            volleyCount += 2; // More volleys against overpowered players
        }
        
        for (int c = 0; c < volleyCount; c++) {
            for (final Entity t : list) {
                if (t instanceof Player) {
                    Player targetPlayer = (Player) t;
                    int baseDamage = getRandomMaxHit(npc, 250, NPCCombatDefinitions.RANGE, t);
                    int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, targetPlayer, npc);
                    
                    // CRITICAL: Apply HP-aware damage scaling for incendiary
                    int safeDamage = applyHPAwareDamageScaling(scaledDamage, targetPlayer, "critical");
                    checkAndWarnLowHP(targetPlayer, safeDamage);
                    
                    t.applyHit(new Hit(npc, safeDamage, HitLook.RANGE_DAMAGE, 10));
                } else {
                    int baseDamage = getRandomMaxHit(npc, 250, NPCCombatDefinitions.RANGE, t);
                    t.applyHit(new Hit(npc, baseDamage, HitLook.RANGE_DAMAGE, 10));
                }

                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        t.setNextGraphics(new Graphics(3522));
                    }
                }, 3);
            }
        }
        lastSuccessfulHit = System.currentTimeMillis();
    }

    /**
     * ENHANCED v5.0: Green mark attack with HP-aware scaling
     */
    private void greenIntelligent(NPC npc, Player player, CombatScaling scaling) {
        final KalphiteKing king = (KalphiteKing) npc;
        
        king.setForceFollowClose(true);
        king.setNextAnimation(new Animation(19464));
        king.setNextGraphics(new Graphics(3738));
        player.setNextGraphics(new Graphics(3740, 1, 0));
        
        // Enhanced lock duration based on v5.0 power analysis
        double playerPower = BossBalancer.calculateActualPlayerPower(player);
        int lockDuration = Math.min(9 + (int)(playerPower / 6), 15);
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            lockDuration += 2; // Longer lock for underpowered players
        } else if (scaling.scalingType.contains("ANTI_FARMING")) {
            lockDuration += 3; // Much longer lock for overpowered players
        }
        
        player.lock(lockDuration);
        player.stopAll();
        player.setNextAnimation(new Animation(-1));
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {    
                king.setForceFollowClose(true);
            }
        }, 8);
    }

    /**
     * ENHANCED v5.0: Execute green attack with HP-aware scaling
     */
    private boolean excecuteGreenIntelligent(NPC npc, Player player, CombatScaling scaling) {
        if (Utils.isOnRange(npc, player, 5)) {
            npc.setNextAnimation(new Animation(19449));
            
            // Enhanced green damage based on v5.0 power analysis
            double playerPower = BossBalancer.calculateActualPlayerPower(player);
            double baseDamagePercent = 0.60 + (playerPower * 0.01); // 60% to 80% based on power
            
            // Apply scaling type adjustments
            if (scaling.scalingType.contains("ANTI_FARMING")) {
                baseDamagePercent += 0.05; // 5% more damage for overpowered
            } else if (scaling.scalingType.contains("ASSISTANCE")) {
                baseDamagePercent -= 0.05; // 5% less damage for underpowered
            }
            
            int baseDamage = player.getHitpoints() > 20 ? 
                (int) (player.getHitpoints() * baseDamagePercent) : 1;
            
            // Apply v5.0 BossBalancer scaling
            int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, player, npc);
            
            // CRITICAL: Apply HP-aware scaling for green mark (signature attack)
            int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "green");
            checkAndWarnLowHP(player, safeDamage);
            
            player.applyHit(new Hit(player, safeDamage, HitLook.REGULAR_DAMAGE));
            npc.setForceFollowClose(false);
            ((KalphiteKing) npc).nextPhase();
            
            return true;
        }
        return false;
    }

    /**
     * ENHANCED v5.0: Enhanced dig attack
     */
    private void digIntelligent(NPC npc, Player player, CombatScaling scaling) {
        ((KalphiteKing) npc).dig(player);
    }

    /**
     * ENHANCED v5.0: Melee stomp attack with HP-aware scaling
     */
    private void meleeStompIntelligent(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        KalphiteKing king = (KalphiteKing) npc;
        king.setNextAnimation(new Animation(19435));
        king.setNextGraphics(new Graphics(3734));
        
        // Enhanced stomp range based on v5.0 power analysis
        double playerPower = BossBalancer.calculateActualPlayerPower(player);
        int stompRange = Math.min(5 + (int)(playerPower / 10), 8);
        
        for (Entity t : npc.getPossibleTargets()) {
            if (Utils.isOnRange(king, t, stompRange)) {
                if (t instanceof Player) {
                    Player targetPlayer = (Player) t;
                    int baseDamage = getRandomMaxHit(npc, 700, NPCCombatDefinitions.MELEE, t);
                    int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, targetPlayer, npc);
                    
                    // CRITICAL: Apply HP-aware damage scaling for stomp
                    int safeDamage = applyHPAwareDamageScaling(scaledDamage, targetPlayer, "stomp");
                    checkAndWarnLowHP(targetPlayer, safeDamage);
                    
                    Hit hit = getMeleeHit(npc, safeDamage);
                    delayHit(npc, 1, t, hit);
                    
                    // Enhanced defense drain based on v5.0 power analysis
                    int drainAmount = Math.max(hit.getDamage() / 200, 1);
                    if (playerPower >= 15) drainAmount *= 2; // Double drain for high power
                    if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
                        drainAmount = (int)(drainAmount * 1.1); // 10% more drain for underpowered
                    }
                    targetPlayer.getSkills().drainLevel(Skills.DEFENCE, drainAmount);
                } else {
                    int baseDamage = getRandomMaxHit(npc, 700, NPCCombatDefinitions.MELEE, t);
                    Hit hit = getMeleeHit(npc, baseDamage);
                    delayHit(npc, 1, t, hit);
                }
            }
        }
        lastSuccessfulHit = System.currentTimeMillis();
    }

    /**
     * ENHANCED v5.0: Enhanced melee push attack with HP-aware scaling
     */
    private void meleePushIntelligent(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        // Enhanced push mechanics with v5.0 power analysis
        final byte[] dirs = Utils.getDirection(npc.getDirection());
        KalphiteKing king = (KalphiteKing) npc;
        WorldTile lastTile = null;
        int distance;
        
        // Enhanced max distance based on v5.0 power analysis
        double playerPower = BossBalancer.calculateActualPlayerPower(player);
        int maxDistance = Math.min(10 + (int)(playerPower / 8), 15);
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            maxDistance += 2; // Charge further against underpowered players
        } else if (scaling.scalingType.contains("ANTI_FARMING")) {
            maxDistance += 3; // Charge much further against overpowered players
        }
        
        for (distance = 1; distance < maxDistance; distance++) {
            WorldTile nextTile = new WorldTile(new WorldTile(king.getX() + (dirs[0] * distance), king.getY() + (dirs[1] * distance), king.getPlane()));
            if (!World.isFloorFree(nextTile.getPlane(), nextTile.getX(), nextTile.getY(), king.getSize())) 
                break;
            lastTile = nextTile;
        }
        
        if(lastTile == null || distance <= 2) {            
            king.setNextAnimation(new Animation(19447));
            king.setNextGraphics(new Graphics(3735));
            for(Entity t : king.getPossibleTargets()) {
                if(!Utils.isOnRange(king, t, 1))
                    continue;
                    
                if (t instanceof Player) {
                    Player targetPlayer = (Player) t;
                    int baseDamage = getRandomMaxHit(npc, 600, NPCCombatDefinitions.MELEE, t);
                    int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, targetPlayer, npc);
                    
                    // CRITICAL: Apply HP-aware damage scaling
                    int safeDamage = applyHPAwareDamageScaling(scaledDamage, targetPlayer, "melee");
                    delayHit(npc, 0, t, getRegularHit(npc, safeDamage));
                } else {
                    int baseDamage = getRandomMaxHit(npc, 600, NPCCombatDefinitions.MELEE, t);
                    delayHit(npc, 0, t, getRegularHit(npc, baseDamage));
                }
            }
        } else {
            // Execute enhanced charge attack
            executeIntelligentChargeAttack(npc, player, king, dirs, lastTile, distance, scaling);
        }
        lastSuccessfulHit = System.currentTimeMillis();
    }

    /**
     * ENHANCED v5.0: Enhanced melee bleed attack with HP-aware scaling
     */
    private void meleeBleedIntelligent(final NPC npc, final Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        Entity t = null;
        try {
            List<Entity> targets = npc.getPossibleTargets();
            Collections.shuffle(targets);
            t = targets.get(0);
        } catch (Exception e) {
            // Silent fallback
        }
        
        if (t != null) {
            npc.setTarget(t);
            npc.setNextFaceEntity(t);
            npc.setNextAnimation(new Animation(19449));
            
            if (t instanceof Player) {
                Player targetPlayer = (Player) t;
                int baseDamage = getRandomMaxHit(npc, 650, NPCCombatDefinitions.MELEE, t);
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, targetPlayer, npc);
                
                // CRITICAL: Apply HP-aware damage scaling for bleed
                int safeDamage = applyHPAwareDamageScaling(scaledDamage, targetPlayer, "bleed");
                
                Hit hit = getMeleeHit(npc, safeDamage);
                t.applyHit(hit);
                
                // Apply enhanced bleed effect based on v5.0 power analysis
                double playerPower = BossBalancer.calculateActualPlayerPower(player);
                if (playerPower >= 12) {
                    applyIntelligentBleedEffect(npc, t, hit.getDamage(), scaling);
                }
            } else {
                int baseDamage = getRandomMaxHit(npc, 650, NPCCombatDefinitions.MELEE, t);
                Hit hit = getMeleeHit(npc, baseDamage);
                t.applyHit(hit);
            }
        }
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run()  {
                npc.setTarget(player);
                stop();
            }
        }, 1);
        lastSuccessfulHit = System.currentTimeMillis();
    }

    /**
     * ENHANCED v5.0: Enhanced rush attack with HP-aware scaling
     */
    private void rushIntelligent(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        int baseDamage = getRandomMaxHit(npc, 700, NPCCombatDefinitions.MELEE, player);
        int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, player, npc);
        
        // CRITICAL: Apply HP-aware damage scaling for rush
        int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "charge");
        checkAndWarnLowHP(player, safeDamage);
        
        delayHit(npc, 0, player, getRegularHit(npc, safeDamage));
        lastSuccessfulHit = System.currentTimeMillis();
    }

    /**
     * ENHANCED v5.0: Enhanced magic ball attack with HP-aware scaling
     */
    private void mageBallIntelligent(final NPC npc, Player player, int n, NPCCombatDefinitions defs, CombatScaling scaling) {
        npc.setNextAnimation(new Animation(19448));
        npc.setNextGraphics(new Graphics(3742));

        for (Entity t : npc.getPossibleTargets()) {
            final WorldTile tile = new WorldTile(t).transform(n, 0, 0);
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    mageDoBallGraphics(npc, tile);
                    WorldTasksManager.schedule(new WorldTask() {
                        @Override
                        public void run() {
                            World.sendGraphics(npc, new Graphics(3752), tile);
                            
                            // Enhanced AOE range based on v5.0 power analysis
                            double playerPower = BossBalancer.calculateActualPlayerPower(player);
                            int aoeRange = Math.min(3 + (int)(playerPower / 12), 5);
                            
                            if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
                                aoeRange += 1; // Larger AOE against underpowered players
                            } else if (scaling.scalingType.contains("ANTI_FARMING")) {
                                aoeRange += 2; // Much larger AOE against overpowered players
                            }
                            
                            for (Entity t : npc.getPossibleTargets()) {
                                if (t.withinDistance(tile, aoeRange)) {
                                    if (t instanceof Player) {
                                        Player targetPlayer = (Player) t;
                                        int baseDamage = getRandomMaxHit(npc, 600, NPCCombatDefinitions.MAGE, t);
                                        int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, targetPlayer, npc);
                                        
                                        // CRITICAL: Apply HP-aware damage scaling for magic ball
                                        int safeDamage = applyHPAwareDamageScaling(scaledDamage, targetPlayer, "aoe");
                                        checkAndWarnLowHP(targetPlayer, safeDamage);
                                        
                                        Hit hit = getMagicHit(npc, safeDamage);
                                        t.applyHit(hit);
                                    } else {
                                        int baseDamage = getRandomMaxHit(npc, 600, NPCCombatDefinitions.MAGE, t);
                                        Hit hit = getMagicHit(npc, baseDamage);
                                        t.applyHit(hit);
                                    }
                                }
                            }
                        }
                    }, 2);
                }
            }, 1);
        }
        lastSuccessfulHit = System.currentTimeMillis();
    }

    /**
     * ENHANCED v5.0: Double magic ball attack
     */
    private void mageBallDoubleIntelligent(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        mageBallIntelligent(npc, player, 1, defs, scaling);
        mageBallIntelligent(npc, player, -1, defs, scaling);
    }

    /**
     * ENHANCED v5.0: Enhanced blue magic ball attack with HP-aware scaling
     */
    private void mageBallBlueIntelligent(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        npc.setNextAnimation(new Animation(19448));
        npc.setNextGraphics(new Graphics(3757));
        
        for (final Entity t : npc.getPossibleTargets()) {
            if (t instanceof Player) {
                Player targetPlayer = (Player) t;
                int baseDamage = getRandomMaxHit(npc, 550, NPCCombatDefinitions.MAGE, t);
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, targetPlayer, npc);
                
                // CRITICAL: Apply HP-aware damage scaling for blue magic
                int safeDamage = applyHPAwareDamageScaling(scaledDamage, targetPlayer, "magic");
                
                Hit hit = getMagicHit(npc, safeDamage);
                
                if (!(Utils.random(100) - hit.getDamage() > 1))
                    delayHit(npc, 1, t, hit);
                    
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        // Enhanced freeze duration based on v5.0 power analysis
                        double playerPower = BossBalancer.calculateActualPlayerPower(player);
                        int freezeDuration = Math.min(8000 + (int)(playerPower * 200), 12000);
                        
                        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
                            freezeDuration = (int)(freezeDuration * 1.1); // 10% longer freeze
                        } else if (scaling.scalingType.contains("ANTI_FARMING")) {
                            freezeDuration = (int)(freezeDuration * 1.2); // 20% longer freeze
                        }
                        t.setFreezeDelay(freezeDuration);
                    }
                }, 1);
            } else {
                int baseDamage = getRandomMaxHit(npc, 550, NPCCombatDefinitions.MAGE, t);
                Hit hit = getMagicHit(npc, baseDamage);
                if (!(Utils.random(100) - hit.getDamage() > 1))
                    delayHit(npc, 1, t, hit);
            }
        }
        lastSuccessfulHit = System.currentTimeMillis();
    }

    /**
     * ENHANCED v5.0: Magic ball with bleed effect
     */
    private void mageBallBleedIntelligent(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        mageBallIntelligent(npc, player, 0, defs, scaling);
        
        // Apply additional bleed effect based on v5.0 power analysis
        double playerPower = BossBalancer.calculateActualPlayerPower(player);
        if (playerPower >= 12) {
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    for (Entity t : npc.getPossibleTargets()) {
                        if (t instanceof Player) {
                            Player targetPlayer = (Player) t;
                            int baseDamage = 150; // Base bleed damage
                            int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, targetPlayer, npc);
                            
                            // CRITICAL: Apply HP-aware damage scaling for bleed
                            int safeDamage = applyHPAwareDamageScaling(scaledDamage, targetPlayer, "bleed");
                            t.applyHit(new Hit(npc, safeDamage, HitLook.POISON_DAMAGE, 0));
                        } else {
                            t.applyHit(new Hit(npc, 150, HitLook.POISON_DAMAGE, 0));
                        }
                    }
                }
            }, 3);
        }
    }

    /**
     * ENHANCED v5.0: Enhanced ranged fragmentation attack
     */
    private void rangeFragIntelligent(final NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        npc.setNextAnimation(new Animation(19450));
        List<Entity> list = npc.getPossibleTargets();
        Collections.shuffle(list);
        
        // Enhanced frag count based on v5.0 power analysis
        double playerPower = BossBalancer.calculateActualPlayerPower(player);
        int fragCount = Math.min(3 + (int)(playerPower / 10), 5);
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            fragCount += 1; // Extra fragment against underpowered players
        } else if (scaling.scalingType.contains("ANTI_FARMING")) {
            fragCount += 2; // More fragments against overpowered players
        }
        
        int c = 0;
        
        for (final Entity e : list) {
            if (c < fragCount) {
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        if (e instanceof Player) {
                            Player targetPlayer = (Player) e;
                            int baseDamage = getRandomMaxHit(npc, 450, NPCCombatDefinitions.RANGE, e);
                            int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, targetPlayer, npc);
                            
                            // CRITICAL: Apply HP-aware damage scaling for frag
                            int safeDamage = applyHPAwareDamageScaling(scaledDamage, targetPlayer, "ranged");
                            delayHit(npc, 1, e, getRegularHit(npc, safeDamage));
                        } else {
                            int baseDamage = getRandomMaxHit(npc, 450, NPCCombatDefinitions.RANGE, e);
                            delayHit(npc, 1, e, getRegularHit(npc, baseDamage));
                        }
                        e.setNextGraphics(new Graphics(3574));
                    }
                }, 1);
                c++;
            }
        }
        lastSuccessfulHit = System.currentTimeMillis();
    }

    /**
     * ENHANCED v5.0: Enhanced ranged stun attack
     */
    private void rangeStunIntelligent(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        for (Entity t : npc.getPossibleTargets()) {
            // Enhanced stun duration based on v5.0 power analysis
            double playerPower = BossBalancer.calculateActualPlayerPower(player);
            int stunDuration = Math.min(16000 + (int)(playerPower * 400), 25000);
            
            if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
                stunDuration = (int)(stunDuration * 1.15); // 15% longer stun for underpowered
            } else if (scaling.scalingType.contains("ANTI_FARMING")) {
                stunDuration = (int)(stunDuration * 1.25); // 25% longer stun for overpowered
            }
            t.setFreezeDelay(stunDuration);
            
            if (t instanceof Player) {
                Player targetPlayer = (Player) t;
                int baseDamage = getRandomMaxHit(npc, 400, NPCCombatDefinitions.RANGE, t);
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, targetPlayer, npc);
                
                // CRITICAL: Apply HP-aware damage scaling for stun
                int safeDamage = applyHPAwareDamageScaling(scaledDamage, targetPlayer, "ranged");
                delayHit(npc, 1, t, getRegularHit(npc, safeDamage));
            } else {
                int baseDamage = getRandomMaxHit(npc, 400, NPCCombatDefinitions.RANGE, t);
                delayHit(npc, 1, t, getRegularHit(npc, baseDamage));
            }
        }
        lastSuccessfulHit = System.currentTimeMillis();
    }

    // ===== ENHANCED HELPER METHODS FOR v5.0 =====

    /**
     * ENHANCED v5.0: Execute intelligent charge attack with HP-aware scaling
     */
    private void executeIntelligentChargeAttack(NPC npc, Player player, KalphiteKing king, byte[] dirs, 
                                              WorldTile lastTile, int distance, CombatScaling scaling) {
        king.setNextAnimation(new Animation(19457));
        final int maxStep = distance / 2;
        king.setCantInteract(true);
        king.setNextAnimation(new Animation(maxStep + 19456));
        int totalTime = distance/2;
        final WorldTile firstTile = new WorldTile(king);
        int dir = king.getDirection();
        king.setNextForceMovement(new NewForceMovement(firstTile, 5, lastTile, totalTime + 5, dir));
        final WorldTile tpTile = lastTile;
        final ArrayList<Entity> targets = king.getPossibleTargets();
        
        WorldTasksManager.schedule(new WorldTask() {
            int step = 0;
            @Override
            public void run() {
                if(step == maxStep-1) {
                    king.setCantInteract(false);
                    king.setTarget(player);
                    stop();
                    return;
                }
                if (step == 1)
                    king.setNextWorldTile(tpTile);
                WorldTile kingTile = new WorldTile(firstTile.getX() + (dirs[0] * step * 2), firstTile.getY() + (dirs[1] * step * 2), king.getPlane());
                int leftSteps = (maxStep - step) + 1;
                for (Entity t : targets) {
                    if (!(t instanceof Player))
                        continue;
                    final Player playerInPath = (Player) t;
                    if (playerInPath.isLocked())
                        continue;
                    if (Utils.colides(kingTile, t, king.getSize(), 1)) {
                        WorldTile lastTileForP = null;
                        int stepCount = 0;
                        for (int thisStep = 1; thisStep <= leftSteps; thisStep++) {
                            WorldTile nextTile = new WorldTile(new WorldTile(playerInPath.getX() + (dirs[0] * thisStep * 2), playerInPath.getY() + (dirs[1] * thisStep * 2), playerInPath.getPlane()));
                            if (!World.isFloorFree(nextTile.getPlane(), nextTile.getX(), nextTile.getY()))
                                break;
                            lastTileForP = nextTile;
                            stepCount = thisStep;
                        }
                        if (lastTileForP == null)
                            continue;
                        playerInPath.setNextForceMovement(new NewForceMovement(playerInPath, 0, lastTileForP, stepCount, Utils.getAngle(firstTile.getX() - playerInPath.getX(), firstTile.getY() - playerInPath.getY())));
                        playerInPath.setNextAnimation(new Animation(10070));
                        playerInPath.lock(stepCount + 1);
                        
                        // ENHANCED v5.0: Enhanced push damage with HP-aware scaling
                        int baseDamage = 500;
                        int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, playerInPath, npc);
                        
                        // CRITICAL: Apply HP-aware damage scaling for charge
                        int safeDamage = applyHPAwareDamageScaling(scaledDamage, playerInPath, "charge");
                        checkAndWarnLowHP(playerInPath, safeDamage);
                        
                        delayHit(npc, 0, t, getRegularHit(npc, safeDamage));
                        final WorldTile lastTileForP_T = lastTileForP;

                        WorldTasksManager.schedule(new WorldTask() {
                            @Override
                            public void run() {
                                playerInPath.setNextWorldTile(lastTileForP_T);
                                playerInPath.faceEntity(king);
                            }
                        }, 0);
                    }
                }
                step++;
            }
        }, 3, 0);
    }

    /**
     * ENHANCED v5.0: Apply intelligent bleed effect with HP-aware scaling
     */
    private void applyIntelligentBleedEffect(NPC npc, Entity target, int initialDamage, CombatScaling scaling) {
        double playerPower = BossBalancer.calculateActualPlayerPower((Player) target);
        int bleedTicks = Math.min(3 + (int)(playerPower / 12), 6);
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            bleedTicks += 1; // Extra bleed tick for underpowered
        } else if (scaling.scalingType.contains("ANTI_FARMING")) {
            bleedTicks += 2; // More bleed ticks for overpowered
        }
        
        for (int i = 1; i <= bleedTicks; i++) {
            final int tick = i;
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    if (target instanceof Player) {
                        Player targetPlayer = (Player) target;
                        int baseDamage = (int)(initialDamage * 0.1); // 10% of initial damage per tick
                        int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, targetPlayer, npc);
                        
                        // CRITICAL: Apply HP-aware damage scaling for bleed
                        int safeDamage = applyHPAwareDamageScaling(scaledDamage, targetPlayer, "bleed");
                        target.applyHit(new Hit(npc, safeDamage, HitLook.POISON_DAMAGE, 0));
                    } else {
                        int baseDamage = (int)(initialDamage * 0.1);
                        target.applyHit(new Hit(npc, baseDamage, HitLook.POISON_DAMAGE, 0));
                    }
                }
            }, tick * 2); // Every 2 ticks
        }
    }

    // ===== ANTI-SAFE SPOT SYSTEM WITH v5.0 INTELLIGENCE =====

    /**
     * ENHANCED v5.0: Comprehensive anti-safe spot system with intelligent scaling
     */
    private void checkRaidBossSafeSpotting(KalphiteKing king, Player player, CombatScaling scaling) {
        // Enhanced safe spot detection with v5.0 power awareness
        boolean potentialSafeSpot = detectIntelligentRaidBossSafeSpotting(king, player, scaling);
        
        if (potentialSafeSpot) {
            safeSpotViolations++;
            
            // Immediate response for raid bosses with v5.0 scaling
            if (safeSpotViolations >= 1) {
                performIntelligentRaidBossAntiSafeSpotMeasure(king, player, scaling);
                safeSpotViolations = 0;
            }
        } else {
            // Reset when fighting fairly
            if (safeSpotViolations > 0) {
                safeSpotViolations = 0;
            }
        }
    }

    /**
     * ENHANCED v5.0: Detect safe spotting patterns with power awareness
     */
    private boolean detectIntelligentRaidBossSafeSpotting(KalphiteKing king, Player player, CombatScaling scaling) {
        long currentTime = System.currentTimeMillis();
        boolean playerAttacking = isPlayerAttackingBoss(player, king);
        boolean canKingReach = canIntelligentRaidBossReachPlayer(king, player, scaling);
        
        // Power-based timeout adjustments
        double playerPower = BossBalancer.calculateActualPlayerPower(player);
        long timeoutDuration = 15000; // Base 15 seconds
        
        if (playerPower >= 20) {
            timeoutDuration = 12000; // High power players get less patience
        } else if (playerPower >= 15) {
            timeoutDuration = 13000; // Medium power
        }
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            timeoutDuration += 3000; // Slight mercy for underpowered players
        } else if (scaling.scalingType.contains("ANTI_FARMING")) {
            timeoutDuration -= 2000; // Less patience for overpowered players
        }
        
        boolean longTimeWithoutHit = (currentTime - lastSuccessfulHit) > timeoutDuration;
        
        // If player is attacking but king can't reach them for extended time, it's safe spotting
        return playerAttacking && !canKingReach && longTimeWithoutHit;
    }

    /**
     * ENHANCED v5.0: Apply intelligent anti-safe spot measures with power scaling
     */
    private void performIntelligentRaidBossAntiSafeSpotMeasure(KalphiteKing king, Player player, CombatScaling scaling) {
        // Power-based counter measures
        double playerPower = BossBalancer.calculateActualPlayerPower(player);
        
        if (playerPower >= 20) { // High power tier
            // Ultimate response - teleport player to king for forced engagement
            WorldTile kingTile = new WorldTile(king.getX() + 2, king.getY() + 2, king.getPlane());
            player.setNextWorldTile(kingTile);
            player.setNextGraphics(new Graphics(342));
            
            // Follow with ultimate combo attack
            performUltimateIntelligentRaidCombo(king, player, scaling);
        } else if (playerPower >= 15) { // Medium power tier
            // King repositioning with form-specific abilities
            WorldTile optimalTile = new WorldTile(player.getX() + 2, player.getY() + 2, king.getPlane());
            king.setNextWorldTile(optimalTile);
            king.setNextGraphics(new Graphics(342)); // Teleport effect
            
            // Follow with devastating attack
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    performDevastatingIntelligentRaidAttack(king, player, scaling);
                }
            }, 2);
        } else { // Basic power tier
            // Force a powerful attack that ignores most protection
            performBasicIntelligentRaidCounter(king, player, scaling);
        }
    }

    /**
     * ENHANCED v5.0: Check if raid boss can reach player with power awareness
     */
    private boolean canIntelligentRaidBossReachPlayer(KalphiteKing king, Player player, CombatScaling scaling) {
        int distance = Utils.getDistance(king, player);
        
        // Different forms have different reach capabilities, enhanced by power
        int baseReach;
        switch (king.getId()) {
            case 16697: // Melee form - limited range
                baseReach = 3;
                break;
            case 16698: // Magic form - good range
                baseReach = 8;
                break;
            case 16699: // Ranged form - excellent range
                baseReach = 12;
                break;
            default:
                baseReach = 5;
        }
        
        // Power-based reach enhancement
        double playerPower = BossBalancer.calculateActualPlayerPower(player);
        int powerBonus = Math.max(0, (int)(playerPower / 8));
        return distance <= (baseReach + powerBonus);
    }

    /**
     * ENHANCED v5.0: Perform basic intelligent raid counter
     */
    private void performBasicIntelligentRaidCounter(KalphiteKing king, Player player, CombatScaling scaling) {
        // Force a powerful attack that ignores most protection
        if (king.getId() == 16699) { // Ranged form - use magic attack
            king.setNextAnimation(new Animation(19448));
            king.setNextGraphics(new Graphics(3742));
            int baseDamage = 800;
            int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, player, king);
            
            // CRITICAL: Apply HP-aware damage scaling
            int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "critical");
            checkAndWarnLowHP(player, safeDamage);
            
            player.applyHit(new Hit(king, safeDamage, HitLook.MAGIC_DAMAGE, 1));
        } else if (king.getId() == 16698) { // Magic form - use ranged attack
            king.setNextAnimation(new Animation(19450));
            int baseDamage = 800;
            int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, player, king);
            
            // CRITICAL: Apply HP-aware damage scaling
            int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "critical");
            checkAndWarnLowHP(player, safeDamage);
            
            player.applyHit(new Hit(king, safeDamage, HitLook.RANGE_DAMAGE, 1));
        } else { // Melee form - use enhanced charge
            king.setNextAnimation(new Animation(19447));
            king.setNextGraphics(new Graphics(3735));
            int baseDamage = 850;
            int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, player, king);
            
            // CRITICAL: Apply HP-aware damage scaling
            int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "charge");
            checkAndWarnLowHP(player, safeDamage);
            
            player.applyHit(new Hit(king, safeDamage, HitLook.MELEE_DAMAGE, 0));
        }
    }

    /**
     * ENHANCED v5.0: Perform devastating intelligent raid attack
     */
    private void performDevastatingIntelligentRaidAttack(KalphiteKing king, Player player, CombatScaling scaling) {
        // Form-specific devastating attack with v5.0 scaling
        switch (king.getId()) {
            case 16697: // Melee
                meleeStompIntelligent(king, player, king.getCombatDefinitions(), scaling);
                break;
            case 16698: // Magic
                mageBallIntelligent(king, player, 0, king.getCombatDefinitions(), scaling);
                break;
            case 16699: // Ranged
                rangeIncendiaryShotIntelligent(king, player, king.getCombatDefinitions(), scaling);
                break;
        }
    }

    /**
     * ENHANCED v5.0: Perform ultimate intelligent raid combo
     */
    private void performUltimateIntelligentRaidCombo(KalphiteKing king, Player player, CombatScaling scaling) {
        // Ultimate 5-phase combo attack with v5.0 scaling
        WorldTasksManager.schedule(new WorldTask() {
            private int phase = 0;
            
            @Override
            public void run() {
                switch (phase) {
                    case 0:
                        greenIntelligent(king, player, scaling); // Enhanced green mark
                        break;
                    case 1:
                        mageBallDoubleIntelligent(king, player, king.getCombatDefinitions(), scaling);
                        break;
                    case 2:
                        rangeIncendiaryShotIntelligent(king, player, king.getCombatDefinitions(), scaling);
                        break;
                    case 3:
                        meleeStompIntelligent(king, player, king.getCombatDefinitions(), scaling);
                        break;
                    case 4:
                        rushIntelligent(king, player, king.getCombatDefinitions(), scaling);
                        this.stop();
                        return;
                }
                phase++;
            }
        }, 0, 3); // Every 3 ticks
    }

    // ===== UTILITY METHODS =====

    /**
     * Track form switches for guidance
     */
    private void trackFormSwitches(Player player, KalphiteKing king) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentForm = getFormName(king.getId());
        String lastFormValue = lastForm.get(playerKey);
        
        if (lastFormValue == null) lastFormValue = "";
        
        if (!currentForm.equals(lastFormValue) && !lastFormValue.isEmpty()) {
            Integer switches = formSwitches.get(playerKey);
            if (switches == null) switches = 0;
            formSwitches.put(playerKey, switches + 1);
        }
        lastForm.put(playerKey, currentForm);
    }

    /**
     * Get form name based on NPC ID
     */
    private String getFormName(int npcId) {
        switch (npcId) {
            case 16697: return "Melee";
            case 16698: return "Magic";
            case 16699: return "Ranged";
            default: return "Unknown";
        }
    }

    /**
     * ENHANCED v5.0: Get intelligent attack delay with power scaling
     */
    private int getIntelligentAttackDelay(int baseDelay, CombatScaling scaling) {
        // Enhanced delays based on v5.0 power analysis
        double playerPower = BossBalancer.calculateActualPlayerPower(null);
        
        if (playerPower >= 20) {
            int variation = Utils.random(4) - 2; // -2 to +1 variation for high power
            if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
                variation -= 1; // Slightly faster against underpowered
            } else if (scaling.scalingType.contains("ANTI_FARMING")) {
                variation -= 2; // Much faster against overpowered
            }
            return Math.max(2, baseDelay + variation);
        } else if (playerPower >= 15) {
            int variation = Utils.random(3) - 1; // -1 to +1 variation for medium power
            if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
                variation -= 1; // Slightly faster against underpowered
            } else if (scaling.scalingType.contains("ANTI_FARMING")) {
                variation -= 1; // Faster against overpowered
            }
            return Math.max(2, baseDelay + variation);
        }
        return baseDelay;
    }

    /**
     * Magic ball graphics
     */
    private void mageDoBallGraphics(final NPC npc, final WorldTile tile) {
        World.sendGraphics(npc, new Graphics(3743), tile);
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                World.sendGraphics(npc, new Graphics(3743), tile);
            }
        }, 1);
    }

    /**
     * Check if player is attacking the boss
     */
    private boolean isPlayerAttackingBoss(Player player, NPC npc) {
        try {
            int distance = Utils.getDistance(player, npc);
            return distance <= 15 && player.withinDistance(npc);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Send combat message
     */
    private void sendCombatMessage(Player player, String message) {
        player.sendMessage("<col=4169E1>Royal Analysis: " + message + "</col>");
    }

    /**
     * ENHANCED v5.0: Handle combat end with proper cleanup
     */
    public static void onCombatEnd(Player player, KalphiteKing king) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking
            Integer playerKey = Integer.valueOf(player.getIndex());
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            combatSessionActive.remove(playerKey);
            attackCounter.remove(playerKey);
            lastForm.remove(playerKey);
            formSwitches.remove(playerKey);
            lastScalingType.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=4169E1>Royal combat session ended. Intelligent scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("KalphiteKingCombat: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during combat
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
                player.sendMessage("<col=66ccff>Prayer change detected. Royal scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("KalphiteKingCombat: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onCombatEnd(player, null);
        }
    }
}