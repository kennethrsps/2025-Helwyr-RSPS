package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.corp.CorporealBeast;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.game.Hit;
import com.rs.utils.Utils;

/**
 * Enhanced Corporeal Beast Combat System with FULL BossBalancer v5.0 Integration
 * Features: Intelligent power-based scaling, armor analysis, absorption integration
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 6.1 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling
 */
public class CorporealBeastCombat extends CombatScript {
    
    // Combat phase thresholds - adjusted for v5.0 scaling
    private static final double ENRAGED_PHASE_THRESHOLD = 0.75; // 75% health
    private static final double DESPERATE_PHASE_THRESHOLD = 0.50; // 50% health
    private static final double FINAL_FURY_THRESHOLD = 0.25; // 25% health
    
    // Attack probabilities - enhanced for intelligent scaling
    private static final int BASE_CORE_SPAWN_CHANCE = 20;
    private static final int ENRAGED_CORE_SPAWN_CHANCE = 15;
    private static final int DESPERATE_CORE_SPAWN_CHANCE = 12;
    private static final int FINAL_CORE_SPAWN_CHANCE = 8;
    private static final int TARGET_SWITCH_CHANCE = 5;
    
    // Enhanced guidance system - intelligent scaling aware
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    
    // Timing constants - enhanced for v5.0
    private static final long WARNING_COOLDOWN = 240000; // 4 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 4000; // 4 seconds for corp attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features
    
    // HP-aware damage scaling constants - CRITICAL SAFETY SYSTEM
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.35; // Max 35% of player HP per hit
    private static final double CRITICAL_DAMAGE_PERCENT = 0.55;  // Max 55% for critical attacks (AOE/special)
    private static final double DRAIN_DAMAGE_PERCENT = 0.25;     // Max 25% for drain attacks
    private static final double STOMP_DAMAGE_PERCENT = 0.45;     // Max 45% for stomp (close range penalty)
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 525;          // Hard cap (35% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 50;               // Minimum damage to prevent 0 hits
    
    // Animation and graphics constants
    private static final int STOMP_ANIMATION = 10496;
    private static final int STOMP_GRAPHICS = 1834;
    private static final int MAGIC_ANIMATION = 10410;
    private static final int MELEE_ANIMATION_ALT = 10058;
    private static final int CORPOREAL_BEAST_ID = 8133;
    
    // Projectile constants
    private static final int MAGIC_PROJECTILE_POWERFUL = 1825;
    private static final int MAGIC_PROJECTILE_DRAIN = 1823;
    private static final int MAGIC_PROJECTILE_AOE = 1824;
    private static final int AOE_GRAPHICS = 1806;
    
    // Instance tracking
    private int consecutiveMagicAttacks = 0;
    private int coreSpawnCount = 0;

    @Override
    public int attack(final NPC npc, final Entity target) {
        if (npc == null || target == null) {
            return 4;
        }
        
        final CorporealBeast corp;
        try {
            corp = (CorporealBeast) npc;
        } catch (ClassCastException e) {
            return 4;
        }
        
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) {
            return 4;
        }

        if (!(target instanceof Player)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeCombatSession(player, corp);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, corp);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentGuidance(player, corp, scaling);
        
        // Monitor scaling changes during combat
        monitorScalingChanges(player, scaling);
        
        // Enhanced dark energy core spawning with v5.0 scaling
        handleIntelligentCoreSpawning(corp, scaling);
        
        // Intelligent target switching with power awareness
        handleIntelligentTargetSwitching(corp, scaling);
        
        // Phase-appropriate force talk with scaling context
        performIntelligentForceTalk(corp, scaling);
        
        // Get possible targets
        final ArrayList<Entity> possibleTargets = corp.getPossibleTargets();
        if (possibleTargets == null || possibleTargets.isEmpty()) {
            return defs.getAttackDelay();
        }
        
        int size = npc.getSize();
        
        // Enhanced stomp attack with v5.0 scaling
        if (performIntelligentStompAttack(corp, target, scaling, possibleTargets, size)) {
            return defs.getAttackDelay();
        }
        
        // Main attack sequence with v5.0 warnings
        return performIntelligentAttackWithWarning(corp, player, scaling, possibleTargets);
    }

    /**
     * Initialize combat session using BossBalancer v5.0
     */
    private void initializeCombatSession(Player player, CorporealBeast corp) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, corp);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            
            // Send v5.0 enhanced combat message
            String welcomeMsg = "<col=4169E1>Corporeal Beast awakens. Intelligent power analysis active (v5.0).</col>";
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
                player.sendMessage("<col=ff6600>⚠ Armor Analysis: Missing protection detected. Corporeal Beast will deal increased damage!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=00ff00>✓ Armor Analysis: Full protection active (" + 
                                 String.format("%.1f", reductionPercent) + "% damage reduction).</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * ENHANCED v5.0: Intelligent guidance with power-based scaling awareness
     */
    private void provideIntelligentGuidance(Player player, CorporealBeast corp, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentGuidanceMessage(player, corp, scaling, currentStage);
        
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
    private String getIntelligentGuidanceMessage(Player player, CorporealBeast corp, CombatScaling scaling, int stage) {
        double hpPercent = getHealthPercentage(corp);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getScalingAnalysisMessage(scaling);
                
            case 1:
                // Second warning: Equipment effectiveness
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=ff3300>Combat Analysis: Incomplete armor detected! Corporeal Beast damage increased by 25%. Equip missing pieces!</col>";
                } else if (hpPercent <= DESPERATE_PHASE_THRESHOLD) {
                    return "<col=ff6600>Combat Analysis: Desperate phase reached. Core spawn rate increased. Prayer drain attacks more frequent!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or critical issues
                if (hpPercent <= FINAL_FURY_THRESHOLD) {
                    return "<col=ff0000>Combat Analysis: Final fury phase! Corporeal Beast at maximum aggression. All attacks intensified!</col>";
                }
                break;
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
                case "aoe":
                case "critical":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "drain":
                    damagePercent = DRAIN_DAMAGE_PERCENT;
                    break;
                case "stomp":
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
            
            // Additional safety check - never deal more than 90% of current HP
            if (currentHP > 0) {
                int emergencyCap = (int)(currentHP * 0.90);
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
                
                if (damagePercent >= 0.80) {
                    player.sendMessage("<col=ff0000>⚠ CRITICAL: This attack will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.60 && hpStatus.equals("LOW")) {
                    player.sendMessage("<col=ff6600>⚠ WARNING: Heavy damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }
    private String getScalingAnalysisMessage(CombatScaling scaling) {
        String baseMessage = "<col=66ccff>Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=00ff00>Assistance mode active! Boss difficulty reduced by " + 
                   assistancePercent + "% due to gear disadvantage.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=ff6600>Anti-farming scaling active! Boss difficulty increased by " + 
                   difficultyIncrease + "% due to gear advantage.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=ffffff>Balanced encounter detected. Optimal gear-to-boss ratio achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=ffaa00>Slight overgear detected. Boss difficulty increased by " + 
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
            return "<col=00ff00>Power Update: Combat scaling improved to balanced! Assistance reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=ff9900>Power Update: Anti-farming scaling now active due to increased power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=66ccff>Power Update: Absorption bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=00ff00>Power Update: Full armor protection restored! Damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * Get current health percentage
     */
    private double getHealthPercentage(CorporealBeast corp) {
        NPCCombatDefinitions defs = corp.getCombatDefinitions();
        if (defs == null) return 1.0;
        return (double) corp.getHitpoints() / defs.getHitpoints();
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent warning system
     */
    private int performIntelligentAttackWithWarning(CorporealBeast corp, Player player, CombatScaling scaling, ArrayList<Entity> possibleTargets) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Determine attack type with v5.0 intelligence
            int attackStyle = determineIntelligentAttackStyle(corp, player, scaling);
            
            // Check if this attack needs a warning (v5.0 enhanced)
            boolean needsWarning = shouldGiveIntelligentWarning(attackStyle, currentCount, scaling);
            
            if (needsWarning) {
                String warningMessage = getIntelligentAttackWarning(attackStyle, scaling);
                if (warningMessage != null) {
                    sendPreAttackWarning(player, warningMessage);
                    
                    // Delay the actual attack to give player time to react
                    final int finalAttackStyle = attackStyle;
                    WorldTasksManager.schedule(new WorldTask() {
                        @Override
                        public void run() {
                            executeIntelligentAttackSequence(corp, player, scaling, possibleTargets, finalAttackStyle);
                            this.stop();
                        }
                    }, 3); // 1.8 second delay for reaction time
                    
                    return corp.getCombatDefinitions().getAttackDelay() + 3;
                }
            }
            
            // Execute immediately for basic attacks
            executeIntelligentAttackSequence(corp, player, scaling, possibleTargets, attackStyle);
            return corp.getCombatDefinitions().getAttackDelay();
            
        } catch (Exception e) {
            return 4;
        }
    }

    /**
     * ENHANCED v5.0: Determine attack style with intelligent scaling awareness
     */
    private int determineIntelligentAttackStyle(CorporealBeast corp, Player player, CombatScaling scaling) {
        int size = corp.getSize();
        int distanceX = player.getX() - corp.getX();
        int distanceY = player.getY() - corp.getY();
        
        // Check if player is in melee range
        boolean inMeleeRange = (distanceX < size && distanceX > -1 && distanceY < size && distanceY > -1);
        
        double hpPercent = getHealthPercentage(corp);
        
        // v5.0: Adjust attack patterns based on scaling type
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
        
        // Force magic attacks when not in melee range
        if (!inMeleeRange) {
            int magicChance = isOvergeared ? 2 : (isUndergeared ? 4 : 3);
            
            if (hpPercent <= DESPERATE_PHASE_THRESHOLD && Utils.random(magicChance) == 0) {
                return 4; // AOE magic (more likely if overgeared)
            } else if (Utils.random(2) == 0) {
                return 3; // Drain attack
            } else {
                return 2; // Powerful magic
            }
        }
        
        // In melee range - adjust based on scaling
        if (hpPercent <= FINAL_FURY_THRESHOLD) {
            // Final phase - more aggressive for overgeared players
            int attackRange = isOvergeared ? 5 : 4;
            return Utils.random(2) == 0 ? Utils.random(2) : 2 + Utils.random(attackRange - 2);
        } else if (hpPercent <= DESPERATE_PHASE_THRESHOLD) {
            // Desperate phase - balanced attacks with scaling consideration
            int baseRange = isOvergeared ? 6 : (isUndergeared ? 4 : 5);
            return Utils.random(baseRange);
        } else {
            // Early phase - prefer melee but more magic if overgeared
            int magicChance = isOvergeared ? 2 : 3;
            return Utils.random(magicChance) == 0 ? 2 + Utils.random(3) : Utils.random(2);
        }
    }

    /**
     * ENHANCED v5.0: Determine if attack needs warning with scaling awareness
     */
    private boolean shouldGiveIntelligentWarning(int attackStyle, int attackCount, CombatScaling scaling) {
        // More frequent warnings for undergeared players
        boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
        int warningFrequency = isUndergeared ? 6 : 8; // Every 6th vs 8th attack
        
        if (attackCount % warningFrequency != 0) return false;
        
        switch (attackStyle) {
            case 3: return true; // Drain attack
            case 4: return true; // AOE magic
            default: return false;
        }
    }

    /**
     * ENHANCED v5.0: Get intelligent attack warning message
     */
    private String getIntelligentAttackWarning(int attackStyle, CombatScaling scaling) {
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        String intensityPrefix = isOvergeared ? "ENHANCED " : "";
        
        switch (attackStyle) {
            case 3: 
                return intensityPrefix + "SPIRITUAL DRAIN incoming - protect prayer and stats!";
            case 4: 
                return intensityPrefix + "AOE MAGIC BLAST incoming - spread out and use protection prayers!";
            default: 
                return null;
        }
    }

    /**
     * Send pre-attack warning
     */
    private void sendPreAttackWarning(Player player, String warning) {
        player.sendMessage("<col=ff3300>⚠ " + warning + "</col>");
    }

    /**
     * ENHANCED v5.0: Execute intelligent attack sequence with proper scaling
     */
    private void executeIntelligentAttackSequence(CorporealBeast corp, Player player, CombatScaling scaling, 
                                                 ArrayList<Entity> possibleTargets, int attackStyle) {
        
        // Enhanced attack logic with v5.0 scaling
        if (attackStyle == 0 || attackStyle == 1) {
            performIntelligentMeleeAttack(corp, player, scaling, attackStyle);
        } else if (attackStyle == 2) {
            performIntelligentPowerfulMagicAttack(corp, player, scaling);
        } else if (attackStyle == 3) {
            performIntelligentDrainAttack(corp, player, scaling);
        } else {
            performIntelligentAreaMagicAttack(corp, player, scaling, possibleTargets);
        }
    }

    /**
     * ENHANCED v5.0: Handle intelligent core spawning with power-based scaling
     */
    private void handleIntelligentCoreSpawning(CorporealBeast corp, CombatScaling scaling) {
        NPCCombatDefinitions defs = corp.getCombatDefinitions();
        if (defs == null) return;
        
        double healthPercent = getHealthPercentage(corp);
        int spawnChance;
        
        // Phase-based spawning
        if (healthPercent <= FINAL_FURY_THRESHOLD) {
            spawnChance = FINAL_CORE_SPAWN_CHANCE;
        } else if (healthPercent <= DESPERATE_PHASE_THRESHOLD) {
            spawnChance = DESPERATE_CORE_SPAWN_CHANCE;
        } else if (healthPercent <= ENRAGED_PHASE_THRESHOLD) {
            spawnChance = ENRAGED_CORE_SPAWN_CHANCE;
        } else {
            spawnChance = BASE_CORE_SPAWN_CHANCE;
        }
        
        // Apply v5.0 intelligent scaling modifiers
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            // More cores for overgeared players
            spawnChance = (int) (spawnChance / Math.max(1.1, scaling.bossAccuracyMultiplier * 0.8));
            spawnChance = Math.max(4, spawnChance);
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            // Fewer cores for undergeared players
            spawnChance = (int) (spawnChance * 1.3);
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            // Slightly more cores if player has high absorption
            spawnChance = (int) (spawnChance * 0.9);
        }
        
        if (Utils.getRandom(spawnChance) == 0) {
            corp.spawnDarkEnergyCore();
            coreSpawnCount++;
        }
    }

    /**
     * ENHANCED v5.0: Handle intelligent target switching with power awareness
     */
    private void handleIntelligentTargetSwitching(CorporealBeast corp, CombatScaling scaling) {
        final ArrayList<Entity> possibleTargets = corp.getPossibleTargets();
        if (possibleTargets == null || possibleTargets.size() <= 1) {
            return;
        }
        
        double healthPercent = getHealthPercentage(corp);
        int switchChance = healthPercent <= DESPERATE_PHASE_THRESHOLD ? 3 : TARGET_SWITCH_CHANCE;
        
        // Apply v5.0 intelligent scaling modifiers
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            // More aggressive target switching for overgeared players
            switchChance = Math.max(2, (int)(switchChance * 0.7));
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            // Less aggressive for undergeared players
            switchChance = (int)(switchChance * 1.2);
        }
        
        if (Utils.random(switchChance) == 0) {
            Entity newTarget = selectIntelligentTarget(possibleTargets, corp, scaling);
            if (newTarget != null) {
                corp.setTarget(newTarget);
            }
        }
    }

    /**
     * ENHANCED v5.0: Select intelligent target based on power analysis
     */
    private Entity selectIntelligentTarget(ArrayList<Entity> targets, NPC npc, CombatScaling scaling) {
        Entity bestTarget = null;
        double highestThreat = 0;
        
        for (Entity target : targets) {
            if (target instanceof Player) {
                Player player = (Player) target;
                
                // Calculate threat based on v5.0 power analysis
                double playerPower = BossBalancer.calculateActualPlayerPower(player);
                double threatLevel = playerPower;
                
                // Distance factor
                int distance = Utils.getDistance(npc.getX(), npc.getY(), target.getX(), target.getY());
                if (distance <= 3) threatLevel += 2.0;
                
                // Prayer factor
                if (player.getPrayer() != null && player.getPrayer().hasPrayersOn()) {
                    threatLevel += 1.5;
                }
                
                if (threatLevel > highestThreat) {
                    highestThreat = threatLevel;
                    bestTarget = target;
                }
            }
        }
        
        return bestTarget != null ? bestTarget : targets.get(Utils.getRandom(targets.size()));
    }

    /**
     * ENHANCED v5.0: Perform intelligent force talk
     */
    private void performIntelligentForceTalk(CorporealBeast corp, CombatScaling scaling) {
        if (Utils.getRandom(15) != 0) return; // Less frequent force talk
        
        double healthPercent = getHealthPercentage(corp);
        String message;
        
        // Add scaling-aware dialogue
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            message = healthPercent <= FINAL_FURY_THRESHOLD ? 
                "Your power means nothing to me!" :
                "I sense your strength... it will not save you!";
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            message = healthPercent <= DESPERATE_PHASE_THRESHOLD ?
                "Even weakened, I am your superior!" :
                "You dare challenge me with such feeble equipment?";
        } else {
            // Standard dialogue
            if (healthPercent <= FINAL_FURY_THRESHOLD) {
                message = "I am eternal! You are nothing!";
            } else if (healthPercent <= DESPERATE_PHASE_THRESHOLD) {
                message = "I will not be banished again!";
            } else if (healthPercent <= ENRAGED_PHASE_THRESHOLD) {
                message = "My power grows beyond comprehension!";
            } else {
                message = "Who dares disturb my slumber?";
            }
        }
        
        corp.setNextForceTalk(new ForceTalk(message));
    }

    /**
     * ENHANCED v5.0: Perform intelligent stomp attack with HP-aware damage scaling
     */
    private boolean performIntelligentStompAttack(CorporealBeast corp, Entity target, CombatScaling scaling, 
                                                 ArrayList<Entity> possibleTargets, int size) {
        boolean stompPerformed = false;
        int targetsStomped = 0;
        
        for (Entity t : possibleTargets) {
            int distanceX = t.getX() - corp.getX();
            int distanceY = t.getY() - corp.getY();
            if (distanceX < size && distanceX > -1 && distanceY < size && distanceY > -1) {
                targetsStomped++;
                stompPerformed = true;
                
                NPCCombatDefinitions defs = corp.getCombatDefinitions();
                if (defs == null) continue;
                
                if (t instanceof Player) {
                    Player player = (Player) t;
                    
                    // Use v5.0 BossBalancer scaling methods
                    int baseDamage = BossBalancer.applyBossHpScaling(defs.getMaxHit(), player, corp);
                    int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, player, corp);
                    
                    // AOE damage reduction
                    double stompMultiplier = calculateStompMultiplier(targetsStomped);
                    int rawDamage = (int)(scaledDamage * stompMultiplier);
                    
                    // CRITICAL: Apply HP-aware damage scaling to prevent one-shots
                    int finalDamage = applyHPAwareDamageScaling(rawDamage, player, "stomp");
                    
                    // Warn player if damage is significant
                    checkAndWarnLowHP(player, finalDamage);
                    
                    Hit stompHit = getMeleeHit(corp, finalDamage);
                    delayHit(corp, 0, t, stompHit);
                } else {
                    // Non-player targets use standard damage
                    Hit stompHit = getMeleeHit(corp, defs.getMaxHit());
                    delayHit(corp, 0, t, stompHit);
                }
            }
        }
        
        if (stompPerformed) {
            corp.setNextAnimation(new Animation(STOMP_ANIMATION));
            corp.setNextGraphics(new Graphics(STOMP_GRAPHICS));
            return true;
        }
        
        return false;
    }

    /**
     * Calculate stomp damage multiplier
     */
    private double calculateStompMultiplier(int targetsHit) {
        switch (targetsHit) {
            case 1: return 1.0;
            case 2: return 0.85;
            case 3: return 0.75;
            case 4: return 0.65;
            default: return 0.55;
        }
    }

    /**
     * ENHANCED v5.0: Perform intelligent melee attack with HP-aware scaling
     */
    private void performIntelligentMeleeAttack(CorporealBeast corp, Player player, CombatScaling scaling, int attackStyle) {
        NPCCombatDefinitions defs = corp.getCombatDefinitions();
        if (defs == null) return;
        
        corp.setNextAnimation(new Animation(attackStyle == 0 ? defs.getAttackEmote() : MELEE_ANIMATION_ALT));
        
        // Use v5.0 BossBalancer scaling methods
        int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, corp);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareDamageScaling(scaledDamage, player, "melee");
        
        Hit meleeHit = getMeleeHit(corp, safeDamage);
        delayHit(corp, 0, player, meleeHit);
    }

    /**
     * ENHANCED v5.0: Perform intelligent powerful magic attack with HP-aware scaling
     */
    private void performIntelligentPowerfulMagicAttack(CorporealBeast corp, Player player, CombatScaling scaling) {
        corp.setNextAnimation(new Animation(MAGIC_ANIMATION));
        consecutiveMagicAttacks++;
        
        NPCCombatDefinitions defs = corp.getCombatDefinitions();
        if (defs == null) return;
        
        // Use v5.0 BossBalancer scaling methods
        int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, corp);
        
        // 85% damage for powerful magic, adjusted for scaling
        double magicMultiplier = scaling.scalingType.contains("ANTI_FARMING") ? 0.90 : 0.85;
        int rawMagicDamage = (int)(scaledDamage * magicMultiplier);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareDamageScaling(rawMagicDamage, player, "magic");
        
        delayHit(corp, 1, player, getMagicHit(corp, safeDamage));
        World.sendProjectile(corp, player, MAGIC_PROJECTILE_POWERFUL, 41, 16, 41, 0, 16, 0);
    }

    /**
     * ENHANCED v5.0: Perform intelligent drain attack with HP-aware scaling
     */
    private void performIntelligentDrainAttack(CorporealBeast corp, Player player, CombatScaling scaling) {
        corp.setNextAnimation(new Animation(MAGIC_ANIMATION));
        consecutiveMagicAttacks++;
        
        NPCCombatDefinitions defs = corp.getCombatDefinitions();
        if (defs == null) return;
        
        // Use v5.0 BossBalancer scaling methods
        int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, corp);
        
        // 70% damage for drain attack, enhanced for overgeared players
        double drainMultiplier = scaling.scalingType.contains("ANTI_FARMING") ? 0.75 : 0.70;
        int rawDrainDamage = (int)(scaledDamage * drainMultiplier);
        
        // CRITICAL: Apply HP-aware damage scaling (drain attacks less lethal)
        int safeDamage = applyHPAwareDamageScaling(rawDrainDamage, player, "drain");
        
        delayHit(corp, 1, player, getMagicHit(corp, safeDamage));
        
        // Apply intelligent drain effects
        applyIntelligentDrainEffects(player, scaling);
        
        World.sendProjectile(corp, player, MAGIC_PROJECTILE_DRAIN, 41, 16, 41, 0, 16, 0);
    }

    /**
     * ENHANCED v5.0: Apply intelligent drain effects with scaling awareness
     */
    private void applyIntelligentDrainEffects(final Player player, final CombatScaling scaling) {
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                int skill = Utils.getRandom(3);
                skill = skill == 0 ? Skills.MAGIC : (skill == 1 ? Skills.SUMMONING : Skills.PRAYER);
                
                if (skill == Skills.PRAYER) {
                    // Enhanced prayer drain for v5.0
                    int playerTier = BossBalancer.calculatePlayerGearTier(player);
                    int baseDrain = Math.max(8, playerTier * 2);
                    
                    // Scale drain based on scaling type
                    double drainMultiplier = 1.0;
                    if (scaling.scalingType.contains("ANTI_FARMING")) {
                        drainMultiplier = 1.3; // 30% more drain for overgeared
                    } else if (scaling.scalingType.contains("ASSISTANCE")) {
                        drainMultiplier = 0.8; // 20% less drain for undergeared
                    }
                    
                    int totalDrain = (int)(baseDrain * drainMultiplier) + Utils.getRandom(10);
                    
                    player.getPrayer().drainPrayer(totalDrain);
                    player.sendMessage("<col=8B008B>Spiritual drain: Prayer severely reduced!</col>");
                    
                } else {
                    // Enhanced stat drain for v5.0
                    int currentLevel = player.getSkills().getLevel(skill);
                    int playerTier = BossBalancer.calculatePlayerGearTier(player);
                    int baseDrain = Math.max(2, playerTier / 2);
                    
                    // Scale drain based on scaling type
                    double drainMultiplier = 1.0;
                    if (scaling.scalingType.contains("ANTI_FARMING")) {
                        drainMultiplier = 1.2; // 20% more drain for overgeared
                    } else if (scaling.scalingType.contains("ASSISTANCE")) {
                        drainMultiplier = 0.9; // 10% less drain for undergeared
                    }
                    
                    int drainAmount = (int)(baseDrain * drainMultiplier) + Utils.getRandom(4);
                    int newLevel = Math.max(0, currentLevel - drainAmount);
                    
                    player.getSkills().set(skill, newLevel);
                    player.sendMessage("<col=8B008B>Mystical drain: " + Skills.SKILL_NAME[skill] + " reduced!</col>");
                }
                
                this.stop();
            }
        }, 1);
    }

    /**
     * ENHANCED v5.0: Perform intelligent area magic attack
     */
    private void performIntelligentAreaMagicAttack(CorporealBeast corp, Player player, CombatScaling scaling, ArrayList<Entity> possibleTargets) {
        corp.setNextAnimation(new Animation(MAGIC_ANIMATION));
        consecutiveMagicAttacks++;
        
        final WorldTile tile = new WorldTile(player);
        World.sendProjectile(corp, tile, MAGIC_PROJECTILE_AOE, 41, 16, 30, 0, 16, 0);
        
        final NPCCombatDefinitions defs = corp.getCombatDefinitions();
        if (defs == null) return;
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Count targets for damage scaling
                int targetsInRange = 0;
                for (Entity t : possibleTargets) {
                    if (Utils.getDistance(tile.getX(), tile.getY(), t.getX(), t.getY()) <= 3) {
                        targetsInRange++;
                    }
                }
                
                for (int i = 0; i < 6; i++) {
                    final WorldTile newTile = new WorldTile(tile, 3);
                    if (!World.canMoveNPC(newTile.getPlane(), newTile.getX(), newTile.getY(), 1)) {
                        continue;
                    }
                    
                    World.sendProjectile(corp, tile, newTile, MAGIC_PROJECTILE_AOE, 0, 0, 25, 0, 30, 0);
                    
                    for (Entity t : possibleTargets) {
                        if (Utils.getDistance(newTile.getX(), newTile.getY(), t.getX(), t.getY()) > 1) {
                            continue;
                        }
                        
                        // Use v5.0 BossBalancer scaling methods
                        int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), 
                            t instanceof Player ? (Player) t : null, corp);
                        
                        double aoeMultiplier = calculateIntelligentAOEMultiplier(targetsInRange, scaling);
                        int rawAOEDamage = (int)(scaledDamage * aoeMultiplier);
                        
                        // CRITICAL: Apply HP-aware damage scaling for AOE attacks
                        int safeDamage = rawAOEDamage;
                        if (t instanceof Player) {
                            safeDamage = applyHPAwareDamageScaling(rawAOEDamage, (Player) t, "aoe");
                            checkAndWarnLowHP((Player) t, safeDamage);
                        }
                        
                        delayHit(corp, 0, t, getMagicHit(corp, safeDamage));
                    }
                    
                    WorldTasksManager.schedule(new WorldTask() {
                        @Override
                        public void run() {
                            World.sendGraphics(corp, new Graphics(AOE_GRAPHICS), newTile);
                            this.stop();
                        }
                    });
                }
                
                this.stop();
            }
        }, 1);
    }

    /**
     * ENHANCED v5.0: Calculate intelligent AOE damage multiplier
     */
    private double calculateIntelligentAOEMultiplier(int targetsInRange, CombatScaling scaling) {
        double baseMultiplier = 0.60;
        
        // Adjust for scaling type
        if (scaling.scalingType.contains("ANTI_FARMING")) {
            baseMultiplier = 0.65; // Slightly higher for overgeared
        } else if (scaling.scalingType.contains("ASSISTANCE")) {
            baseMultiplier = 0.55; // Slightly lower for undergeared
        }
        
        if (targetsInRange <= 1) return baseMultiplier;
        if (targetsInRange == 2) return baseMultiplier * 0.90;
        if (targetsInRange == 3) return baseMultiplier * 0.80;
        if (targetsInRange == 4) return baseMultiplier * 0.70;
        return baseMultiplier * 0.60;
    }

    /**
     * ENHANCED v5.0: Handle combat end with proper cleanup
     */
    public static void onCombatEnd(Player player, CorporealBeast corp) {
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
            lastScalingType.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=4169E1>Combat session ended. Intelligent scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("CorporealBeastCombat: Error ending v5.0 combat session: " + e.getMessage());
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
                player.sendMessage("<col=66ccff>Prayer change detected. Scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("CorporealBeastCombat: Error handling v5.0 prayer change: " + e.getMessage());
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

    @Override
    public Object[] getKeys() {
        return new Object[] { CORPOREAL_BEAST_ID };
    }
}