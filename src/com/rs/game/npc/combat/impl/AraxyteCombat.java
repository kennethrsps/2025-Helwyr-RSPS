package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.araxxor.AraxyteNPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Araxxor Combat System with BossBalancer v5.0 Integration + Pull Spam Prevention
 * Features: Intelligent power-based scaling, armor analysis, HP-aware damage scaling, coordinated cleave attacks
 * 
 * @author Zeus
 * @date June 11, 2025
 * @version 10.0 - Full v5.0 Integration with Pull Spam Prevention & Clean Code
 */
public class AraxyteCombat extends CombatScript {
    
    // Combat phase thresholds
    private static final double ENRAGED_PHASE_THRESHOLD = 0.75;
    private static final double DESPERATE_PHASE_THRESHOLD = 0.50;
    private static final double FINAL_FURY_THRESHOLD = 0.25;
    
    // Enhanced guidance system tracking
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Boolean> phaseWarningGiven = new ConcurrentHashMap<Integer, Boolean>();
    
    // PULL SPAM PREVENTION - New tracking systems
    private static final Map<Integer, Long> lastPullTime = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Boolean> currentlyPulling = new ConcurrentHashMap<Integer, Boolean>();
    private static final long PULL_COOLDOWN = 5000; // 5 seconds between pulls per player
    
    // Timing constants
    private static final long WARNING_COOLDOWN = 240000; // 4 minutes between warnings
    private static final int MAX_WARNINGS_PER_FIGHT = 3;
    
    // HP-aware damage scaling constants
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.35;
    private static final double CRITICAL_DAMAGE_PERCENT = 0.50;
    private static final double POISON_DAMAGE_PERCENT = 0.15;
    private static final double AOE_DAMAGE_PERCENT = 0.40;
    private static final int MIN_PLAYER_HP = 990;
    private static final int ABSOLUTE_MAX_DAMAGE = 525;
    private static final int MINIMUM_DAMAGE = 35;
    
    // Animation and graphics constants
    private static final int CLEAVE_ANIMATION = 24050;
    private static final int ACID_SPIT_ANIMATION = 24047;
    private static final int COCOON_SPIT_ANIMATION = 24047;
    private static final int CLEAVE_GRAPHICS = 4986;
    private static final int ACID_SPIT_GRAPHICS = 4988;
    private static final int ACID_IMPACT_GRAPHICS = 4980;
    private static final int COCOON_IMPACT_GRAPHICS = 4993;
    
    // Projectile constants
    private static final int ACID_PROJECTILE = 4979;
    private static final int COCOON_PROJECTILE = 4997;
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (npc == null || target == null) {
            return 4;
        }
        
        final AraxyteNPC araxxor;
        try {
            araxxor = (AraxyteNPC) npc;
        } catch (ClassCastException e) {
            return 4;
        }
        
        final NPCCombatDefinitions defs = araxxor.getCombatDefinitions();
        if (defs == null) {
            return 4;
        }

        if (!(target instanceof Player)) {
            return defs.getAttackDelay();
        }

        Player player = (Player) target;
        
        // Initialize combat session if needed
        initializeCombatSession(player, araxxor);
        
        // Get intelligent combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, araxxor);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentGuidance(player, araxxor, scaling);
        
        // Monitor scaling changes during combat
        monitorScalingChanges(player, scaling);
        
        // Check if spider can interact
        if (araxxor.isCantInteract()) {
            return 2;
        }
        
        // Main attack sequence with v5.0 warnings and HP-aware scaling
        return performIntelligentAttackWithWarning(araxxor, player, scaling);
    }

    /**
     * Initialize combat session using BossBalancer v5.0
     */
    private void initializeCombatSession(Player player, AraxyteNPC araxxor) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            BossBalancer.startCombatSession(player, araxxor);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            phaseWarningGiven.put(sessionKey, Boolean.FALSE);
            
            player.sendMessage("<col=4169E1>Araxxor awakens. Intelligent arachnid analysis active (v5.0).</col>");
            performInitialArmorAnalysis(player);
        }
    }

    /**
     * Perform initial armor analysis
     */
    private void performInitialArmorAnalysis(Player player) {
        try {
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=ff6600> Armor Analysis: Missing protection detected. Araxxor will deal increased damage!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=00ff00> Armor Analysis: Full protection active (" + 
                                 String.format("%.1f", reductionPercent) + "% damage reduction).</col>");
            }
        } catch (Exception e) {
            // Continue silently
        }
    }

    /**
     * Intelligent guidance with power-based scaling awareness
     */
    private void provideIntelligentGuidance(Player player, AraxyteNPC araxxor, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        long currentTime = System.currentTimeMillis();
        
        Long lastWarningTime = lastWarning.get(playerKey);
        if (lastWarningTime != null && (currentTime - lastWarningTime) < WARNING_COOLDOWN) {
            return;
        }
        
        Integer currentStage = warningStage.get(playerKey);
        if (currentStage == null) currentStage = 0;
        if (currentStage >= MAX_WARNINGS_PER_FIGHT) {
            return;
        }
        
        String guidanceMessage = getIntelligentGuidanceMessage(player, araxxor, scaling, currentStage);
        
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * Get intelligent guidance message based on power analysis
     */
    private String getIntelligentGuidanceMessage(Player player, AraxyteNPC araxxor, CombatScaling scaling, int stage) {
        double hpPercent = getHealthPercentage(araxxor);
        Boolean phaseWarning = phaseWarningGiven.get(Integer.valueOf(player.getIndex()));
        if (phaseWarning == null) phaseWarning = false;
        
        switch (stage) {
            case 0:
                return getScalingAnalysisMessage(scaling);
                
            case 1:
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=ff3300>Combat Analysis: Incomplete armor detected! Araxxor damage increased by 25%. Equip missing pieces!</col>";
                } else if (AraxyteNPC.phase2 && !phaseWarning) {
                    phaseWarningGiven.put(Integer.valueOf(player.getIndex()), Boolean.TRUE);
                    return "<col=ff6600>Combat Analysis: Phase 2 active! Cocoon attacks unlocked. Web mechanics intensified!</col>";
                }
                break;
                
            case 2:
                if (hpPercent <= FINAL_FURY_THRESHOLD) {
                    return "<col=ff0000>Combat Analysis: Final fury phase! Araxxor at maximum aggression. All attacks intensified!</col>";
                } else if (hpPercent <= DESPERATE_PHASE_THRESHOLD) {
                    return "<col=ff6600>Combat Analysis: Desperate phase reached. Cleave frequency increased. Multi-target attacks more likely!</col>";
                }
                break;
        }
        
        return null;
    }

    /**
     * Apply HP-aware damage scaling to prevent instant kills
     */
    private int applyHPAwareDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "cleave":
                case "critical":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "poison":
                    damagePercent = POISON_DAMAGE_PERCENT;
                    break;
                case "aoe":
                case "acid_aoe":
                    damagePercent = AOE_DAMAGE_PERCENT;
                    break;
                default:
                    damagePercent = MAX_DAMAGE_PERCENT_OF_HP;
                    break;
            }
            
            int hpBasedCap = (int)(effectiveHP * damagePercent);
            int safeDamage = Math.min(scaledDamage, hpBasedCap);
            safeDamage = Math.min(safeDamage, ABSOLUTE_MAX_DAMAGE);
            safeDamage = Math.max(safeDamage, MINIMUM_DAMAGE);
            
            if (currentHP > 0) {
                int emergencyCap = (int)(currentHP * 0.85);
                safeDamage = Math.min(safeDamage, emergencyCap);
            }
            
            return safeDamage;
            
        } catch (Exception e) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
    }

    /**
     * Send HP warning if player is in danger
     */
    private void checkAndWarnLowHP(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.75) {
                    player.sendMessage("<col=ff0000> CRITICAL: This attack will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.55) {
                    String hpStatus = getPlayerHPStatus(player);
                    if ("LOW".equals(hpStatus)) {
                        player.sendMessage("<col=ff6600> WARNING: Heavy damage incoming (" + incomingDamage + 
                                         ")! Consider healing (" + currentHP + " HP)</col>");
                    }
                }
            }
        } catch (Exception e) {
            // Continue silently
        }
    }

    /**
     * Get player HP status for damage calculations
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
     * Get scaling analysis message
     */
    private String getScalingAnalysisMessage(CombatScaling scaling) {
        String baseMessage = "<col=66ccff>Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=00ff00>Assistance mode active! Araxxor difficulty reduced by " + 
                   assistancePercent + "% due to gear disadvantage.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=ff6600>Anti-farming scaling active! Araxxor difficulty increased by " + 
                   difficultyIncrease + "% due to gear advantage.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=ffffff>Balanced encounter detected. Optimal gear-to-boss ratio achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=ffaa00>Slight overgear detected. Araxxor difficulty increased by " + 
                   difficultyIncrease + "% for balance.</col>";
        }
        
        return baseMessage + "<col=cccccc>Power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * Monitor scaling changes during combat
     */
    private void monitorScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        if (lastType != null && !lastType.equals(currentScalingType)) {
            String changeMessage = getScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * Get scaling change message
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
    private double getHealthPercentage(AraxyteNPC araxxor) {
        NPCCombatDefinitions defs = araxxor.getCombatDefinitions();
        if (defs == null) return 1.0;
        return (double) araxxor.getHitpoints() / defs.getHitpoints();
    }

    /**
     * Perform attack with intelligent warning system
     */
    private int performIntelligentAttackWithWarning(AraxyteNPC araxxor, Player player, CombatScaling scaling) {
        try {
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            String attackType = determineIntelligentAttackType(araxxor, player, scaling);
            boolean needsWarning = shouldGiveIntelligentWarning(attackType, currentCount, scaling);
            
            if (needsWarning) {
                String warningMessage = getIntelligentAttackWarning(attackType, scaling);
                if (warningMessage != null) {
                    sendPreAttackWarning(player, warningMessage);
                    
                    final String finalAttackType = attackType;
                    WorldTasksManager.schedule(new WorldTask() {
                        @Override
                        public void run() {
                            executeIntelligentAttackSequence(araxxor, player, scaling, finalAttackType);
                            this.stop();
                        }
                    }, 2);
                    
                    return araxxor.getCombatDefinitions().getAttackDelay() + 2;
                }
            }
            
            executeIntelligentAttackSequence(araxxor, player, scaling, attackType);
            return getScaledAttackSpeed(scaling);
            
        } catch (Exception e) {
            return 4;
        }
    }

    /**
     * Determine attack style with intelligent scaling awareness
     */
    private String determineIntelligentAttackType(AraxyteNPC araxxor, Player player, CombatScaling scaling) {
        double hpPercent = getHealthPercentage(araxxor);
        
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
        
        int cleaveChance = getIntelligentCleaveChance(hpPercent, scaling, isOvergeared, isUndergeared);
        
        if (Utils.random(100) < cleaveChance && araxxor.getHitpoints() >= 5000) {
            return "CLEAVE";
        }
        
        boolean inMeleeRange = player.withinDistance(araxxor, 1);
        
        if (!inMeleeRange) {
            if (AraxyteNPC.phase2 && Utils.random(isOvergeared ? 2 : 3) == 0) {
                return "COCOON_SPIT";
            } else {
                return "ACID_SPIT";
            }
        }
        
        if (AraxyteNPC.phase2) {
            int attackRange = isOvergeared ? 4 : 3;
            int attackChoice = Utils.random(attackRange);
            
            switch (attackChoice) {
                case 0: return "MELEE";
                case 1: return "ACID_SPIT";
                case 2: return "COCOON_SPIT";
                case 3: return isOvergeared ? "ACID_SPIT" : "MELEE";
                default: return "MELEE";
            }
        } else {
            int magicChance = isOvergeared ? 2 : 3;
            return Utils.random(magicChance) == 0 ? "ACID_SPIT" : "MELEE";
        }
    }

    /**
     * FIXED: Calculate intelligent cleave chance (reduced frequency to prevent spam)
     */
    private int getIntelligentCleaveChance(double hpPercent, CombatScaling scaling, boolean isOvergeared, boolean isUndergeared) {
        int baseChance;
        
        // Reduced base chances to prevent cleave spam
        if (hpPercent <= FINAL_FURY_THRESHOLD) {
            baseChance = 20; // Reduced from 30
        } else if (hpPercent <= DESPERATE_PHASE_THRESHOLD) {
            baseChance = 15; // Reduced from 25
        } else if (hpPercent <= ENRAGED_PHASE_THRESHOLD) {
            baseChance = 12; // Reduced from 20
        } else {
            baseChance = 8;  // Reduced from 15
        }
        
        // Apply v5.0 intelligent scaling modifiers (reduced impact)
        if (isOvergeared) {
            baseChance = (int)(baseChance * 1.2); // Reduced from 1.3
        } else if (isUndergeared) {
            baseChance = (int)(baseChance * 0.9); // Increased from 0.8
        }
        
        // Phase 2 bonus (reduced)
        if (AraxyteNPC.phase2) {
            baseChance += 3; // Reduced from 5
        }
        
        return Math.min(25, Math.max(5, baseChance)); // Lower max chance
    }

    /**
     * FIXED: Determine if attack needs warning with scaling awareness (less frequent)
     */
    private boolean shouldGiveIntelligentWarning(String attackType, int attackCount, CombatScaling scaling) {
        boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
        
        if ("CLEAVE".equals(attackType)) {
            // Only warn for cleaves every 15th attack (increased from 8/12)
            return attackCount % 15 == 0;
        } else if ("COCOON_SPIT".equals(attackType)) {
            // Warn for cocoon every 12th attack for undergeared, 18th for others
            int warningFrequency = isUndergeared ? 12 : 18;
            return attackCount % warningFrequency == 0;
        }
        
        return false;
    }

    /**
     * Get intelligent attack warning message
     */
    private String getIntelligentAttackWarning(String attackType, CombatScaling scaling) {
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        String intensityPrefix = isOvergeared ? "ENHANCED " : "";
        
        switch (attackType) {
            case "CLEAVE": 
                return intensityPrefix + "CLEAVE ATTACK incoming - Araxxor will pull and strike all nearby targets!";
            case "COCOON_SPIT": 
                return intensityPrefix + "COCOON SPIT incoming - entanglement projectile with binding effects!";
            default: 
                return null;
        }
    }

    /**
     * Send pre-attack warning
     */
    private void sendPreAttackWarning(Player player, String warning) {
        player.sendMessage("<col=ff3300> " + warning + "</col>");
    }

    /**
     * Execute intelligent attack sequence with proper scaling
     */
    private void executeIntelligentAttackSequence(AraxyteNPC araxxor, Player player, CombatScaling scaling, String attackType) {
        switch (attackType) {
            case "CLEAVE":
                executeIntelligentCleaveAttack(araxxor, player, scaling);
                break;
            case "ACID_SPIT":
                executeIntelligentAcidSpitAttack(araxxor, player, scaling);
                break;
            case "COCOON_SPIT":
                executeIntelligentCocoonSpitAttack(araxxor, player, scaling);
                break;
            case "MELEE":
            default:
                executeIntelligentMeleeAttack(araxxor, player, scaling);
                break;
        }
    }

    /**
     * Execute intelligent melee attack with HP-aware scaling
     */
    private void executeIntelligentMeleeAttack(AraxyteNPC araxxor, Player player, CombatScaling scaling) {
        NPCCombatDefinitions defs = araxxor.getCombatDefinitions();
        if (defs == null) return;
        
        araxxor.setNextAnimation(new Animation(defs.getAttackEmote()));
        
        int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, araxxor);
        int prayerReducedDamage = applyPrayerProtection(scaledDamage, player, NPCCombatDefinitions.MELEE);
        int safeDamage = applyHPAwareDamageScaling(prayerReducedDamage, player, "melee");
        
        delayHit(araxxor, 0, player, getMeleeHit(araxxor, safeDamage));
    }

    /**
     * FIXED: Execute intelligent cleave attack with pull spam prevention
     */
    private void executeIntelligentCleaveAttack(AraxyteNPC araxxor, Player player, CombatScaling scaling) {
        final ArrayList<Entity> possibleTargets = araxxor.getPossibleTargets();
        if (possibleTargets == null || possibleTargets.isEmpty()) return;
        
        // Filter valid targets and check pull cooldowns
        final ArrayList<Entity> validTargets = new ArrayList<>();
        final ArrayList<Entity> targetsToPull = new ArrayList<>();
        
        for (Entity t : possibleTargets) {
            if (t == null || t.isDead() || t.hasFinished()) continue;
            
            validTargets.add(t);
            
            // Check if target needs to be pulled and isn't on cooldown
            if (!t.withinDistance(araxxor, 1)) {
                if (t instanceof Player) {
                    Player p = (Player) t;
                    Integer playerKey = Integer.valueOf(p.getIndex());
                    Long lastPull = lastPullTime.get(playerKey);
                    Boolean isPulling = currentlyPulling.get(playerKey);
                    
                    // Only pull if not recently pulled and not currently being pulled
                    if ((lastPull == null || (System.currentTimeMillis() - lastPull) >= PULL_COOLDOWN) &&
                        (isPulling == null || !isPulling)) {
                        targetsToPull.add(t);
                    }
                } else {
                    targetsToPull.add(t); // NPCs can always be pulled
                }
            }
        }
        
        if (validTargets.isEmpty()) return;
        
        // FIXED: Single coordinated attack task instead of multiple individual tasks
        WorldTasksManager.schedule(new WorldTask() {
            int step;
            boolean pullCompleted = false;
            
            @Override
            public void run() {
                if (araxxor == null || araxxor.isCantInteract()) {
                    stop();
                    return;
                }
                
                if (step == 0 && !pullCompleted) {
                    // FIXED: Pull all targets at once, with cooldown protection
                    performCoordinatedPull(araxxor, targetsToPull);
                    pullCompleted = true;
                }
                
                if (step == 2) {
                    // Execute the actual cleave damage
                    performCleaveAttack(araxxor, validTargets, scaling);
                    
                    // Set animations
                    araxxor.setNextAnimation(new Animation(CLEAVE_ANIMATION));
                    araxxor.setNextGraphics(new Graphics(CLEAVE_GRAPHICS));
                    stop();
                }
                step++;
            }
        }, 0, 1);
    }

    /**
     * NEW: Perform coordinated pull for all targets (prevents spam)
     */
    private void performCoordinatedPull(AraxyteNPC araxxor, ArrayList<Entity> targetsToPull) {
        if (targetsToPull.isEmpty()) return;
        
        for (Entity t : targetsToPull) {
            if (t == null || t.isDead() || t.hasFinished()) continue;
            
            // Mark as currently being pulled
            if (t instanceof Player) {
                Player p = (Player) t;
                Integer playerKey = Integer.valueOf(p.getIndex());
                currentlyPulling.put(playerKey, Boolean.TRUE);
                lastPullTime.put(playerKey, System.currentTimeMillis());
                
                // Send pull message once
                p.sendMessage("<col=ff6600>Araxxor's cleave pulls you into melee range!</col>");
            }
            
            // Perform the pull
            t.setNextWorldTile(araxxor);
            t.resetWalkSteps();
            
            if (t instanceof Player) {
                ((Player) t).getActionManager().forceStop();
            }
            
            // Clear pulling flag after a short delay
            if (t instanceof Player) {
                final Player finalPlayer = (Player) t;
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        Integer playerKey = Integer.valueOf(finalPlayer.getIndex());
                        currentlyPulling.remove(playerKey);
                        stop();
                    }
                }, 3); // Clear after 1.8 seconds
            }
        }
    }

    /**
     * NEW: Perform cleave damage calculation (separated from pull logic)
     */
    private void performCleaveAttack(AraxyteNPC araxxor, ArrayList<Entity> validTargets, CombatScaling scaling) {
        final int targetsInRange = validTargets.size();
        
        for (final Entity t : validTargets) {
            if (t == null || t.isDead() || t.hasFinished()) continue;
            
            // Only damage targets that are actually in range (pulled or already close)
            if (t.withinDistance(araxxor, 1)) {
                int baseDamage = (int)(araxxor.getCombatDefinitions().getMaxHit() * 1.25); // 25% enhanced cleave
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(baseDamage, 
                    t instanceof Player ? (Player) t : null, araxxor);
                
                // Apply cleave damage reduction for multiple targets
                double cleaveMultiplier = calculateCleaveMultiplier(targetsInRange);
                int cleaveReducedDamage = (int)(scaledDamage * cleaveMultiplier);
                
                // Apply prayer protection
                int prayerReducedDamage = applyPrayerProtection(cleaveReducedDamage, t, NPCCombatDefinitions.MELEE);
                
                // Apply HP-aware damage scaling
                int safeDamage = prayerReducedDamage;
                if (t instanceof Player) {
                    safeDamage = applyHPAwareDamageScaling(prayerReducedDamage, (Player) t, "cleave");
                    checkAndWarnLowHP((Player) t, safeDamage);
                }
                
                delayHit(araxxor, 1, t, getMeleeHit(araxxor, safeDamage));
                t.faceEntity(araxxor);
            }
        }
    }

    /**
     * Calculate cleave damage multiplier based on targets hit
     */
    private double calculateCleaveMultiplier(int targetsHit) {
        switch (targetsHit) {
            case 1: return 1.0;
            case 2: return 0.90;
            case 3: return 0.80;
            case 4: return 0.70;
            default: return 0.60;
        }
    }

    /**
     * Execute intelligent acid spit attack with HP-aware scaling
     */
    private void executeIntelligentAcidSpitAttack(AraxyteNPC araxxor, Player player, CombatScaling scaling) {
        final ArrayList<Entity> possibleTargets = araxxor.getPossibleTargets();
        if (possibleTargets == null) return;
        
        for (final Entity t : possibleTargets) {
            if (t == null || t.isDead() || t.hasFinished()) continue;
                
            araxxor.setNextAnimation(new Animation(ACID_SPIT_ANIMATION));
            araxxor.setNextGraphics(new Graphics(ACID_SPIT_GRAPHICS, 0, 180));
            
            int baseDamage = (int)(araxxor.getCombatDefinitions().getMaxHit() * 1.1); // 10% enhanced acid
            int scaledDamage = BossBalancer.applyBossScaling(baseDamage, 
                t instanceof Player ? (Player) t : player, araxxor);
            
            int prayerReducedDamage = applyPrayerProtection(scaledDamage, t, NPCCombatDefinitions.MAGE);
            
            int safeDamage = prayerReducedDamage;
            if (t instanceof Player) {
                safeDamage = applyHPAwareDamageScaling(prayerReducedDamage, (Player) t, "magic");
                checkAndWarnLowHP((Player) t, safeDamage);
            }
            
            delayHit(araxxor, 1, t, getMagicHit(araxxor, safeDamage));
            World.sendProjectile(araxxor.getMiddleWorldTile(), t, ACID_PROJECTILE, 60, 30, 45, 0, 5, 200);
            
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    World.sendGraphics(araxxor, new Graphics(ACID_IMPACT_GRAPHICS), t);
                    applyIntelligentPoison(t, scaling, player);
                    stop();
                }
            }, 1);
        }
    }

    /**
     * Apply intelligent poison with HP-aware scaling
     */
    private void applyIntelligentPoison(Entity target, CombatScaling scaling, Player primaryPlayer) {
        if (target == null) return;
        
        try {
            int basePoisonDamage = 60 + (BossBalancer.getBossEffectiveTier(null) * 8);
            
            if (scaling.scalingType.contains("ANTI_FARMING")) {
                basePoisonDamage = (int)(basePoisonDamage * scaling.bossDamageMultiplier);
            } else if (scaling.scalingType.contains("ASSISTANCE")) {
                basePoisonDamage = (int)(basePoisonDamage * 0.9);
            }
            
            int safePoisonDamage = basePoisonDamage;
            if (target instanceof Player) {
                safePoisonDamage = applyHPAwareDamageScaling(basePoisonDamage, (Player) target, "poison");
            }
            
            safePoisonDamage = Math.min(200, safePoisonDamage);
            target.getPoison().makePoisoned(safePoisonDamage);
            
        } catch (Exception e) {
            target.getPoison().makePoisoned(100);
        }
    }

    /**
     * Execute intelligent cocoon spit attack with HP-aware scaling
     */
    private void executeIntelligentCocoonSpitAttack(AraxyteNPC araxxor, Player player, CombatScaling scaling) {
        final ArrayList<Entity> possibleTargets = araxxor.getPossibleTargets();
        if (possibleTargets == null) return;
        
        for (final Entity t : possibleTargets) {
            if (t == null || t.isDead() || t.hasFinished()) continue;
                
            araxxor.setNextAnimation(new Animation(COCOON_SPIT_ANIMATION));
            araxxor.setNextGraphics(new Graphics(ACID_SPIT_GRAPHICS, 0, 180));
            
            int baseDamage = araxxor.getCombatDefinitions().getMaxHit();
            int scaledDamage = BossBalancer.applyBossScaling(baseDamage, 
                t instanceof Player ? (Player) t : player, araxxor);
            
            int prayerReducedDamage = applyPrayerProtection(scaledDamage, t, NPCCombatDefinitions.RANGE);
            
            int safeDamage = prayerReducedDamage;
            if (t instanceof Player) {
                safeDamage = applyHPAwareDamageScaling(prayerReducedDamage, (Player) t, "ranged");
                checkAndWarnLowHP((Player) t, safeDamage);
            }
            
            delayHit(araxxor, 1, t, getRangeHit(araxxor, safeDamage));
            World.sendProjectile(araxxor.getMiddleWorldTile(), t, COCOON_PROJECTILE, 60, 30, 45, 0, 5, 200);
            
            if (t instanceof Player) {
                final Player targetPlayer = (Player) t;
                applyIntelligentEntanglement(targetPlayer, scaling);
            }
            
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    World.sendGraphics(araxxor, new Graphics(COCOON_IMPACT_GRAPHICS), t);
                    stop();
                }
            }, 1);
        }
    }

    /**
     * Apply intelligent entanglement effects
     */
    private void applyIntelligentEntanglement(final Player player, final CombatScaling scaling) {
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                try {
                    int baseDuration = 4000; // 4 seconds base
                    int bossTier = BossBalancer.getBossEffectiveTier(null);
                    int tierBonus = bossTier * 300; // 300ms per tier
                    
                    int entangleDuration = baseDuration + tierBonus;
                    
                    if (scaling.scalingType.contains("ANTI_FARMING")) {
                        entangleDuration = (int)(entangleDuration * 1.2); // 20% longer for overgeared
                    } else if (scaling.scalingType.contains("ASSISTANCE")) {
                        entangleDuration = (int)(entangleDuration * 0.8); // 20% shorter for undergeared
                    }
                    
                    entangleDuration = Math.min(8000, Math.max(2000, entangleDuration));
                    
                    player.setFreezeDelay(entangleDuration);
                    player.sendMessage("<col=8B4513>You are entangled in Araxxor's web!</col>");
                    
                } catch (Exception e) {
                    player.setFreezeDelay(4000);
                }
                stop();
            }
        }, 1);
    }

    /**
     * Apply prayer protection calculations
     */
    private int applyPrayerProtection(int baseDamage, Entity target, int attackStyle) {
        if (target instanceof Player) {
            Player player = (Player) target;
            
            if (attackStyle == NPCCombatDefinitions.MELEE) {
                if (player.getPrayer().usingPrayer(0, 18) || player.getPrayer().usingPrayer(1, 8)) {
                    baseDamage = (int)(baseDamage * 0.6);
                }
            } else if (attackStyle == NPCCombatDefinitions.MAGE) {
                if (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)) {
                    baseDamage = (int)(baseDamage * 0.6);
                }
            } else if (attackStyle == NPCCombatDefinitions.RANGE) {
                if (player.getPrayer().usingPrayer(0, 16) || player.getPrayer().usingPrayer(1, 6)) {
                    baseDamage = (int)(baseDamage * 0.6);
                }
            }
        }
        
        return Math.max(1, baseDamage);
    }

    /**
     * Get scaled attack speed with v5.0 intelligence
     */
    private int getScaledAttackSpeed(CombatScaling scaling) {
        int baseTier = BossBalancer.getBossEffectiveTier(null);
        
        int baseSpeed = AraxyteNPC.phase2 ? 
            Math.max(3, 6 - (baseTier / 3)) :  // Phase 2: faster
            Math.max(4, 7 - (baseTier / 3));   // Phase 1: normal
        
        if (scaling.scalingType.contains("ANTI_FARMING")) {
            baseSpeed = Math.max(2, (int)(baseSpeed * 0.85)); // 15% faster when overgeared
        } else if (scaling.scalingType.contains("ASSISTANCE")) {
            baseSpeed = (int)(baseSpeed * 1.1); // 10% slower for undergeared
        }
        
        return baseSpeed;
    }

    /**
     * ENHANCED: Handle combat end with proper cleanup (includes pull tracking)
     */
    public static void onCombatEnd(Player player, AraxyteNPC araxxor) {
        if (player == null) return;
        
        try {
            BossBalancer.endCombatSession(player);
            
            Integer playerKey = Integer.valueOf(player.getIndex());
            
            // Clear all tracking data
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            combatSessionActive.remove(playerKey);
            attackCounter.remove(playerKey);
            lastScalingType.remove(playerKey);
            phaseWarningGiven.remove(playerKey);
            
            // FIXED: Clear pull tracking
            lastPullTime.remove(playerKey);
            currentlyPulling.remove(playerKey);
            
            BossBalancer.clearPlayerCache(player.getIndex());
            player.sendMessage("<col=4169E1>Combat session ended. Intelligent scaling data cleared.</col>");
            
        } catch (Exception e) {
            // Continue silently
        }
    }

    /**
     * Handle prayer changes during combat
     */
    public static void onPlayerPrayerChanged(Player player) {
        if (player == null) return;
        
        try {
            Integer playerKey = Integer.valueOf(player.getIndex());
            
            if (combatSessionActive.containsKey(playerKey)) {
                BossBalancer.onPrayerChanged(player);
                player.sendMessage("<col=66ccff>Prayer change detected. Scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            // Continue silently
        }
    }

    /**
     * NEW: Clean up pull tracking on player logout/disconnect
     */
    public static void onPlayerLogout(Player player) {
        if (player == null) return;
        
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastPullTime.remove(playerKey);
        currentlyPulling.remove(playerKey);
    }

    /**
     * NEW: Emergency pull cleanup (call if issues persist)
     */
    public static void emergencyPullCleanup() {
        lastPullTime.clear();
        currentlyPulling.clear();
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
        return new Object[] { "Araxxor", "Araxxi" };
    }
}