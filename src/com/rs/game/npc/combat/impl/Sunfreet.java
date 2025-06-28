package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

import com.rs.cores.CoresManager;
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
 * Enhanced Sunfreet Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Ancient dragon mechanics, fire magic scaling, HP-aware damage limits, intelligent boss guidance
 * Integrated with BossBalancer v5.0 for comprehensive combat scaling with player safety
 * 
 * @author Zeus (Enhanced from Jae's original with v5.0 integration)
 * @date June 10, 2025
 * @version 5.0 - COMPLETE BossBalancer v5.0 Integration with HP-Aware Damage System
 */
public class Sunfreet extends CombatScript {
    
    private static final long serialVersionUID = 6723681547223858148L;
    
    // ===== ANCIENT DRAGON GUIDANCE AND TRACKING SYSTEMS =====
    private static final long GUIDANCE_COOLDOWN = 45000L; // 45 seconds between guidance
    private static final long MECHANIC_WARNING_COOLDOWN = 18000L; // 18 seconds between mechanic warnings
    private static final long SCALING_UPDATE_INTERVAL = 30000L; // 30 seconds for scaling updates
    private static final int MAX_WARNINGS_PER_FIGHT = 4; // Enhanced guidance system
    
    // Combat session tracking for Ancient Dragon
    private static final Map<Integer, Long> lastGuidanceTime = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Long> lastMechanicWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> dragonPhaseTracker = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - ANCIENT DRAGON THEMED =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.38; // Max 38% of player HP per hit (ancient power balanced)
    private static final double POWER_ATTACK_DAMAGE_PERCENT = 0.55; // Max 55% for power attacks (signature attack)
    private static final double MAGIC_ATTACK_DAMAGE_PERCENT = 0.42; // Max 42% for magic attacks
    private static final double ANTI_SAFESPOT_DAMAGE_PERCENT = 0.48; // Max 48% for anti-safespot measures
    private static final double BASIC_ATTACK_DAMAGE_PERCENT = 0.28; // Max 28% for basic attacks
    private static final int MIN_PLAYER_HP = 990; // Minimum expected player HP
    private static final int MAX_PLAYER_HP = 1500; // Maximum expected player HP
    private static final int ABSOLUTE_MAX_DAMAGE = 570; // Hard cap (38% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 30; // Minimum damage to prevent 0 hits
    
    // ===== ANCIENT DRAGON SPECIAL MECHANICS =====
    private static final Map<Integer, Integer> safespotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> consecutiveAvoids = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> hasSeenPowerAttack = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, Boolean> hasSeenMagicAttack = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, Long> lastDamageTaken = new ConcurrentHashMap<Integer, Long>();
    
    // Enhanced mechanic timings for ancient dragon
    private static final long PRE_POWER_ATTACK_WARNING = 3000L; // 3 seconds before power attack
    private static final long PRE_MAGIC_ATTACK_WARNING = 2000L; // 2 seconds before magic attack
    private static final int MAX_SAFESPOT_DISTANCE = 12;
    private static final int FORCE_RESET_DISTANCE = 20;
    private static final long LAST_DAMAGE_TIMEOUT = 15000L; // 15 seconds
    
    // Animation and Graphics Constants
    private static final int POWER_ATTACK_ANIMATION = 16314;
    private static final int MAGIC_ATTACK_ANIMATION = 16318;
    private static final int MAGIC_CAST_ANIMATION = 16317;
    private static final int TELEPORT_ANIMATION = 16315;
    private static final int MAGIC_GRAPHICS_1 = 3002;
    private static final int MAGIC_GRAPHICS_2 = 3003;
    private static final int TELEPORT_GRAPHICS = 3005;
    private static final int PROJECTILE_ID = 3004;
    
    // Combat Phase Thresholds (HP percentages)
    private static final double PHASE_2_THRESHOLD = 0.75; // 75% HP
    private static final double PHASE_3_THRESHOLD = 0.50; // 50% HP
    private static final double PHASE_4_THRESHOLD = 0.25; // 25% HP
    
    // BossBalancer Integration
    private static final int SUNFREET_NPC_ID = 15222;

    @Override
    public Object[] getKeys() {
        return new Object[] { SUNFREET_NPC_ID };
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        // Enhanced null safety checks
        if (!isValidCombatState(npc, target)) {
            return 4;
        }
        
        Player player = (Player) target;
        
        try {
            // ===== FULL BOSSBALANCER v5.0 INTEGRATION =====
            
            // Initialize ancient dragon combat session if needed
            initializeAncientDragonCombatSession(player, npc);
            
            // Get INTELLIGENT combat scaling v5.0
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            
            // Enhanced guidance system with intelligent scaling awareness
            provideIntelligentAncientDragonGuidance(player, npc, scaling);
            
            // Monitor scaling changes during ancient dragon combat
            monitorAncientDragonScalingChanges(player, scaling);
            
            // Enhanced safespot detection with ancient dragon theme
            if (checkAndPreventAncientDragonSafespotExploitation(npc, player, scaling)) {
                return 8; // Longer delay after anti-safespot measure
            }
            
            // Track dragon phases and ancient mechanics
            updateAncientDragonPhaseTracking(npc, scaling, player);
            
            // Execute intelligent ancient dragon attack patterns
            return executeIntelligentAncientDragonAttack(npc, player, scaling);
            
        } catch (Exception e) {
            System.err.println("Error in Sunfreet.attack(): " + e.getMessage());
            e.printStackTrace();
            return executeBasicAncientAttack(npc, target);
        }
    }

    /**
     * Initialize Ancient Dragon combat session using BossBalancer v5.0
     */
    private void initializeAncientDragonCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            safespotWarnings.put(sessionKey, Integer.valueOf(0));
            consecutiveAvoids.put(sessionKey, Integer.valueOf(0));
            warningStage.put(sessionKey, Integer.valueOf(0));
            dragonPhaseTracker.put(sessionKey, Integer.valueOf(1));
            hasSeenPowerAttack.put(sessionKey, Boolean.FALSE);
            hasSeenMagicAttack.put(sessionKey, Boolean.FALSE);
            lastDamageTaken.put(sessionKey, System.currentTimeMillis());
            
            // Send v5.0 enhanced ancient dragon combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentAncientDragonWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for ancient dragon combat
            performInitialAncientDragonArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial ancient dragon armor analysis
     */
    private void performInitialAncientDragonArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=ff6600>Ancient Analysis: Missing armor leaves you vulnerable to ancient fire!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=ff9900>Ancient Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + 
                                 "% damage reduction). Ready for ancient dragon combat!</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from ancient attacks
     */
    private int applyHPAwareAncientDragonDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (ancient power is overwhelming but balanced)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on ancient dragon attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "power_attack":
                case "ancient_power":
                    damagePercent = POWER_ATTACK_DAMAGE_PERCENT;
                    break;
                case "magic_attack":
                case "ancient_magic":
                    damagePercent = MAGIC_ATTACK_DAMAGE_PERCENT;
                    break;
                case "anti_safespot":
                case "teleport_attack":
                    damagePercent = ANTI_SAFESPOT_DAMAGE_PERCENT;
                    break;
                case "basic_attack":
                case "melee":
                default:
                    damagePercent = BASIC_ATTACK_DAMAGE_PERCENT;
                    break;
            }
            
            // Calculate HP-based damage cap
            int hpBasedCap = (int)(effectiveHP * damagePercent);
            
            // Apply multiple safety caps
            int safeDamage = Math.min(scaledDamage, hpBasedCap);
            safeDamage = Math.min(safeDamage, ABSOLUTE_MAX_DAMAGE);
            safeDamage = Math.max(safeDamage, MINIMUM_DAMAGE);
            
            // Additional safety check - never deal more than 80% of current HP for ancient dragon
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
     * NEW v5.0: Send HP warning if player is in danger from ancient attacks
     */
    private void checkAndWarnLowHPForAncientDragon(Player player, int incomingDamage, String attackType) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming ancient damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.65) {
                    player.sendMessage("<col=ff0000>ANCIENT WARNING: " + attackType + " will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.45) {
                    player.sendMessage("<col=ff6600>ANCIENT WARNING: Heavy ancient damage incoming (" + incomingDamage + 
                                     " from " + attackType + ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent Ancient Dragon welcome message based on power analysis
     */
    private String getIntelligentAncientDragonWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Sunfreet";
        
        message.append("<col=ff6600>").append(npcName)
               .append(" awakens, analyzing your ancient worthiness (BossBalancer v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=ff3300>[Ancient fury: +").append(diffIncrease).append("% ancient power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=ff9900>[Ancient mercy: -").append(assistance).append("% damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=ffcc00>[Ancient resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=ffaa00>[Ancient dragon protection acknowledged]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent Ancient Dragon guidance with power-based scaling awareness
     */
    private void provideIntelligentAncientDragonGuidance(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        long currentTime = System.currentTimeMillis();
        
        // Check if we should provide guidance
        Long lastTime = lastGuidanceTime.get(playerKey);
        if (lastTime != null && (currentTime - lastTime) < GUIDANCE_COOLDOWN) {
            return; // Still in cooldown
        }
        
        Integer currentStage = warningStage.get(playerKey);
        if (currentStage == null) currentStage = 0;
        if (currentStage >= MAX_WARNINGS_PER_FIGHT) {
            return; // Max warnings reached
        }
        
        // Get guidance message based on v5.0 intelligent scaling
        String guidanceMessage = getIntelligentAncientDragonGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastGuidanceTime.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent Ancient Dragon guidance message based on power analysis
     */
    private String getIntelligentAncientDragonGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getAncientDragonScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or armor analysis
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=ff6600>Ancient Analysis: Missing armor increases ancient fire damage by 25%! Equip full protection!</col>";
                } else {
                    return "<col=ff9900>Ancient Tactics: Watch for power attacks and magic barrages. Ancient power grows with rage!</col>";
                }
                
            case 2:
                // Third warning: Advanced mechanics
                if (scaling.bossDamageMultiplier > 2.0) {
                    return "<col=ff3300>Ancient Analysis: Extreme ancient scaling active! Consider fighting higher-tier bosses for balanced challenge!</col>";
                } else {
                    return "<col=ffcc00>Ancient Mechanics: Power attacks have long charge time. Magic attacks can hit multiple targets!</col>";
                }
                
            case 3:
                // Final warning: Ultimate tips
                return "<col=ffaa00>Ancient Mastery: Use protection prayers during charging attacks. HP-aware damage limits prevent one-shots from ancient power!</col>";
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get Ancient Dragon scaling analysis message
     */
    private String getAncientDragonScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Sunfreet";
        
        String baseMessage = "<col=ffcc00>Ancient Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=ff9900>" + npcName + "'s ancient power shows restraint! Damage reduced by " + 
                   assistancePercent + "% due to insufficient preparation. Upgrade your gear!</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=ff3300>" + npcName + "'s ancient power unleashes fury! Power increased by " + 
                   difficultyIncrease + "% due to your superior equipment. Seek worthier opponents!</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=ff9900>Balanced ancient encounter. Optimal ancient combat achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=ff6600>Slight advantage detected. " + npcName + "'s ancient intensity increased by " + 
                   difficultyIncrease + "% for balanced ancient combat.</col>";
        }
        
        return baseMessage + "<col=cccccc>Ancient power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during Ancient Dragon combat
     */
    private void monitorAncientDragonScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getAncientDragonScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get Ancient Dragon scaling change message
     */
    private String getAncientDragonScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=ff9900>Ancient Update: Combat balance improved! Ancient mercy reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=ff6600>Ancient Update: Ancient fury now active due to increased power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=ffcc00>Ancient Update: Fire resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=ff9900>Ancient Update: Full protection equipped! Ancient fire damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * Enhanced ancient dragon safe spot detection and prevention
     */
    private boolean checkAndPreventAncientDragonSafespotExploitation(NPC npc, Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        long currentTime = System.currentTimeMillis();
        
        // Update damage tracking
        if (npc.getCombat() != null && npc.getCombat().getTarget() == player) {
            lastDamageTaken.put(playerKey, currentTime);
        }
        
        // Track consecutive avoids
        Integer avoidCount = consecutiveAvoids.get(playerKey);
        Integer warnCount = safespotWarnings.get(playerKey);
        if (avoidCount == null) avoidCount = 0;
        if (warnCount == null) warnCount = 0;
        
        if (isSafespotting(npc, player, currentTime, playerKey)) {
            warnCount++;
            safespotWarnings.put(playerKey, warnCount);
            
            if (warnCount >= 3) {
                // Ancient dragon anti-safespot measure
                performAncientDragonAntiSafeSpotMeasure(npc, player, scaling);
                safespotWarnings.put(playerKey, 0);
                return true;
            } else {
                Long lastWarning = lastMechanicWarning.get(playerKey);
                if (lastWarning == null || (currentTime - lastWarning) > MECHANIC_WARNING_COOLDOWN) {
                    try {
                        npc.setNextForceTalk(new ForceTalk("Ancient power reaches all cowards!"));
                    } catch (Exception e) {
                        player.sendMessage("<col=ff6600>Sunfreet: Ancient power reaches all cowards!</col>");
                    }
                    player.sendMessage("Sunfreet demands honorable combat! Move within " + MAX_SAFESPOT_DISTANCE + " tiles!");
                    lastMechanicWarning.put(playerKey, currentTime);
                }
            }
        } else {
            // Reset warnings when fighting properly
            if (warnCount > 0) {
                safespotWarnings.put(playerKey, 0);
                player.sendMessage("<col=ff9900>Sunfreet acknowledges your honorable combat...</col>");
            }
        }
        
        return false;
    }

    /**
     * Enhanced safespotting detection
     */
    private boolean isSafespotting(NPC npc, Player player, long currentTime, Integer playerKey) {
        try {
            boolean isFarAway = !player.withinDistance(npc, MAX_SAFESPOT_DISTANCE) && 
                               player.getAttackedBy() == npc;
            
            Long lastDamageTime = lastDamageTaken.get(playerKey);
            boolean noDamageReceived = lastDamageTime != null && 
                                      (currentTime - lastDamageTime) > LAST_DAMAGE_TIMEOUT && 
                                      npc.getCombat() != null && npc.getCombat().getTarget() == player;
            
            boolean maxRangeAttack = player.withinDistance(npc, MAX_SAFESPOT_DISTANCE) && 
                                    !player.withinDistance(npc, 5) && 
                                    player.getAttackedBy() == npc;
            
            boolean cantPath = npc.getCombat() != null && npc.getCombat().getTarget() == player && 
                              !npc.withinDistance(player, 3) && !npc.hasWalkSteps();
            
            return isFarAway || noDamageReceived || (maxRangeAttack && cantPath);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * NEW v5.0: Perform ancient dragon anti-safe spot measure
     */
    private void performAncientDragonAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=ff3300>Ancient fire pierces through all hiding places!</col>");
        
        // Ancient fire blast that reaches through all obstacles
        npc.setNextAnimation(new Animation(MAGIC_ATTACK_ANIMATION));
        npc.setNextForceTalk(new ForceTalk("COWARD! Face ancient fire!"));
        
        // Enhanced damage based on scaling with HP-aware limits
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.5) : 200; // Ancient wrath blast
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareAncientDragonDamageScaling(scaledDamage, player, "anti_safespot");
        
        checkAndWarnLowHPForAncientDragon(player, safeDamage, "Ancient Wrath");
        
        // Schedule delayed ancient fire attack
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                try {
                    if (!isValidCombatState(npc, player)) {
                        stop();
                        return;
                    }
                    
                    player.applyHit(new Hit(npc, safeDamage, HitLook.MAGIC_DAMAGE));
                    player.setNextGraphics(new Graphics(MAGIC_GRAPHICS_1));
                    
                    if (Utils.random(2) == 0) {
                        // Teleport player to boss
                        teleportPlayerToAncientDragon(npc, player);
                    }
                } catch (Exception e) {
                    // Silently handle errors
                } finally {
                    stop();
                }
            }
        }, 2);
        
        player.sendMessage("<col=ff6600>ANCIENT PENALTY: Safe spotting detected - ancient fire reaches all!</col>");
    }

    /**
     * NEW v5.0: Teleport player to ancient dragon
     */
    private void teleportPlayerToAncientDragon(NPC npc, Player player) {
        try {
            int randomX = npc.getX() + Utils.random(3) - 1;
            int randomY = npc.getY() + Utils.random(3) - 1;
            
            player.setNextAnimation(new Animation(TELEPORT_ANIMATION));
            player.setNextGraphics(new Graphics(TELEPORT_GRAPHICS));
            
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    try {
                        if (!isValidCombatState(npc, player)) {
                            stop();
                            return;
                        }
                        
                        player.setNextWorldTile(new WorldTile(randomX, randomY, npc.getPlane()));
                        player.setNextGraphics(new Graphics(TELEPORT_GRAPHICS));
                        player.sendMessage("<col=ff9900>Ancient power teleports you into melee range!</col>");
                        
                        // Reset safespot warnings
                        Integer playerKey = Integer.valueOf(player.getIndex());
                        safespotWarnings.put(playerKey, 0);
                        
                    } catch (Exception e) {
                        // Silently handle errors
                    } finally {
                        stop();
                    }
                }
            }, 2);
            
        } catch (Exception e) {
            System.err.println("Error in teleportPlayerToAncientDragon: " + e.getMessage());
        }
    }

    /**
     * NEW v5.0: Update ancient dragon phase tracking with BossBalancer integration
     */
    private void updateAncientDragonPhaseTracking(NPC npc, CombatScaling scaling, Player player) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Calculate current phase based on HP
        double hpPercentage = (double) npc.getHitpoints() / npc.getMaxHitpoints();
        int currentPhase = 1;
        
        if (hpPercentage <= PHASE_4_THRESHOLD) currentPhase = 4;
        else if (hpPercentage <= PHASE_3_THRESHOLD) currentPhase = 3;
        else if (hpPercentage <= PHASE_2_THRESHOLD) currentPhase = 2;
        
        Integer lastPhase = dragonPhaseTracker.get(playerKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (currentPhase > lastPhase) {
            dragonPhaseTracker.put(playerKey, currentPhase);
            handleIntelligentAncientDragonPhaseTransition(npc, currentPhase, scaling, player);
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ancient dragon phase transitions with scaling integration
     */
    private void handleIntelligentAncientDragonPhaseTransition(NPC npc, int newPhase, CombatScaling scaling, Player player) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Sunfreet";
        
        // Enhanced phase messages with scaling context
        String scalingNote = scaling.bossDamageMultiplier > 2.0 ? " (ENHANCED by scaling!)" : "";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("Ancient rage awakens!" + scalingNote));
            npc.setNextAnimation(new Animation(POWER_ATTACK_ANIMATION));
            player.sendMessage("<col=ff6600>" + npcName + " enters rage phase! Power attacks become more frequent!</col>");
            break;
            
        case 3:
            npc.setNextForceTalk(new ForceTalk("Ancient magic flows through me!" + scalingNote));
            npc.setNextGraphics(new Graphics(MAGIC_GRAPHICS_1));
            player.sendMessage("<col=ff9900>" + npcName + " enters magic phase! Beware of ancient spells!</col>");
            break;
            
        case 4:
            String finalMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE ANCIENT POWER UNLEASHED!" : "I will not be defeated!";
            npc.setNextForceTalk(new ForceTalk(finalMessage));
            npc.setNextGraphics(new Graphics(MAGIC_GRAPHICS_2));
            npc.heal(npc.getMaxHitpoints() / 10); // 10% heal
            player.sendMessage("<col=ff3300>" + npcName + " enters final phase! Ancient power at maximum!</col>");
            break;
        }
    }

    /**
     * ENHANCED v5.0: Execute intelligent ancient dragon attack patterns
     */
    private int executeIntelligentAncientDragonAttack(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Get current phase
            Integer currentPhase = dragonPhaseTracker.get(playerKey);
            if (currentPhase == null) currentPhase = 1;
            
            // Select attack based on phase, scaling, and intelligent pattern
            return selectIntelligentAncientDragonAttack(npc, player, scaling, currentPhase, currentCount);
            
        } catch (Exception e) {
            System.err.println("Error in executeIntelligentAncientDragonAttack: " + e.getMessage());
            return executeBasicAncientAttack(npc, player);
        }
    }

    /**
     * NEW v5.0: Select intelligent ancient dragon attack pattern
     */
    private int selectIntelligentAncientDragonAttack(NPC npc, Player player, CombatScaling scaling, int phase, int attackCount) {
        // Calculate attack probabilities based on phase and scaling
        int attackRoll = Utils.random(100);
        int phaseModifier = (phase - 1) * 10;
        int scalingModifier = scaling.bossDamageMultiplier > 1.8 ? 15 : 0;
        
        int meleeChance = Math.max(30, 60 - phaseModifier - scalingModifier);
        int powerChance = 20 + phaseModifier/2 + scalingModifier;
        int magicChance = 20 + phaseModifier/2 + scalingModifier;
        
        // Enhanced attack selection based on v5.0 intelligence
        if (attackRoll < meleeChance) {
            return executeIntelligentMeleeAttack(npc, player, scaling, phase);
        } else if (attackRoll < meleeChance + powerChance) {
            return executeIntelligentPowerAttack(npc, player, scaling, phase);
        } else {
            return executeIntelligentMagicAttack(npc, player, scaling, phase);
        }
    }

    /**
     * ENHANCED v5.0: Execute intelligent melee attack with HP-aware scaling
     */
    private int executeIntelligentMeleeAttack(NPC npc, Player player, CombatScaling scaling, int phase) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                npc.setNextAnimation(new Animation(defs.getAttackEmote()));
            }
            
            // Calculate damage with phase and scaling modifiers
            double multiplier = 1.0 + (phase - 1) * 0.1;
            if (scaling.bossDamageMultiplier > 2.0) multiplier += 0.2;
            
            int baseMaxHit = defs != null ? defs.getMaxHit() : 50;
            int enhancedDamage = (int)(baseMaxHit * multiplier);
            int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, enhancedDamage);
            int safeDamage = applyHPAwareAncientDragonDamageScaling(scaledDamage, player, "basic_attack");
            
            delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
            
            return Math.max(3, 5 - phase);
        } catch (Exception e) {
            System.err.println("Error in executeIntelligentMeleeAttack: " + e.getMessage());
            return 4;
        }
    }

    /**
     * ENHANCED v5.0: Execute intelligent power attack with HP-aware scaling
     */
    private int executeIntelligentPowerAttack(NPC npc, Player player, CombatScaling scaling, int phase) {
        try {
            // Mark that player has seen power attack
            Integer playerKey = Integer.valueOf(player.getIndex());
            hasSeenPowerAttack.put(playerKey, Boolean.TRUE);
            
            // Send pre-attack warning with scaling context
            sendEnhancedMechanicWarning(npc, player, "Ancient power charging! Prepare for massive damage!", scaling);
            
            npc.setNextAnimation(new Animation(POWER_ATTACK_ANIMATION));
            
            // Calculate enhanced power damage
            double powerMultiplier = 1.5 + (phase - 1) * 0.2;
            if (scaling.bossDamageMultiplier > 2.0) powerMultiplier += 0.3;
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            int baseMaxHit = defs != null ? defs.getMaxHit() : 50;
            int powerDamage = (int)(baseMaxHit * powerMultiplier);
            int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, powerDamage);
            int safeDamage = applyHPAwareAncientDragonDamageScaling(scaledDamage, player, "power_attack");
            
            // Schedule delayed power attack
            final long attackStartTime = System.currentTimeMillis();
            final int chargeTime = Math.max(3000, 5000 - (scaling.bossDamageMultiplier > 1.8 ? 1000 : 0));
            
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    try {
                        if (!isValidForDelayedAttack(npc, player, attackStartTime)) {
                            stop();
                            return;
                        }
                        
                        checkAndWarnLowHPForAncientDragon(player, safeDamage, "Ancient Power Attack");
                        
                        int range = 5 + (scaling.bossDamageMultiplier > 2.0 ? 2 : 0);
                        if (player.withinDistance(npc, range)) {
                            player.applyHit(new Hit(npc, safeDamage, HitLook.REGULAR_DAMAGE));
                            player.setNextGraphics(new Graphics(MAGIC_GRAPHICS_1));
                            
                            if (scaling.bossDamageMultiplier > 2.5) {
                                player.sendMessage("<col=ff3300>The enhanced ancient power overwhelms you!</col>");
                            }
                        } else {
                            player.sendMessage("<col=ff9900>You barely escape the ancient power blast!</col>");
                        }
                        
                    } catch (Exception e) {
                        // Silently handle errors
                    } finally {
                        stop();
                    }
                }
            }, chargeTime / 600); // Convert to ticks
            
            return 10 + phase;
        } catch (Exception e) {
            System.err.println("Error in executeIntelligentPowerAttack: " + e.getMessage());
            return 8;
        }
    }

    /**
     * ENHANCED v5.0: Execute intelligent magic attack with HP-aware scaling
     */
    private int executeIntelligentMagicAttack(NPC npc, Player player, CombatScaling scaling, int phase) {
        try {
            // Mark that player has seen magic attack
            Integer playerKey = Integer.valueOf(player.getIndex());
            hasSeenMagicAttack.put(playerKey, Boolean.TRUE);
            
            // Send pre-attack warning with scaling context
            sendEnhancedMechanicWarning(npc, player, "Ancient magic channelling! Multiple targets at risk!", scaling);
            
            npc.setNextAnimation(new Animation(MAGIC_ATTACK_ANIMATION));
            
            // Calculate magic damage with scaling
            double magicMultiplier = 0.8 + (phase - 1) * 0.1;
            if (scaling.bossDamageMultiplier > 1.8) magicMultiplier += 0.3;
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            int baseMaxHit = defs != null ? defs.getMaxHit() : 50;
            int magicDamage = (int)(baseMaxHit * magicMultiplier);
            int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, magicDamage);
            int safeDamage = applyHPAwareAncientDragonDamageScaling(scaledDamage, player, "magic_attack");
            
            // Execute magic attack on multiple targets
            final long attackStartTime = System.currentTimeMillis();
            final int attackRange = 18 + (scaling.bossDamageMultiplier > 2.0 ? 5 : 0);
            
            // Get all valid targets in range
            List<Entity> targets = new ArrayList<Entity>();
            if (npc.getPossibleTargets() != null) {
                int targetsHit = 0;
                int maxTargets = 3 + (scaling.bossDamageMultiplier > 2.0 ? 2 : 0);
                
                for (Entity currentTarget : npc.getPossibleTargets()) {
                    if (currentTarget != null && currentTarget.withinDistance(npc, attackRange)) {
                        targets.add(currentTarget);
                        targetsHit++;
                        if (targetsHit >= maxTargets) break;
                    }
                }
            }
            
            // Schedule magic attacks
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    try {
                        if (!isValidForDelayedAttack(npc, player, attackStartTime)) {
                            stop();
                            return;
                        }
                        
                        // Execute magic attack on all targets
                        for (Entity target : targets) {
                            if (target != null && !target.isDead()) {
                                executeSingleMagicAttack(npc, target, safeDamage, scaling, attackStartTime);
                            }
                        }
                        
                    } catch (Exception e) {
                        // Silently handle errors
                    } finally {
                        stop();
                    }
                }
            }, 2);
            
            return 14 - Math.min(3, phase);
        } catch (Exception e) {
            System.err.println("Error in executeIntelligentMagicAttack: " + e.getMessage());
            return 6;
        }
    }

    /**
     * NEW v5.0: Execute single magic attack with enhanced effects
     */
    private void executeSingleMagicAttack(NPC npc, Entity target, int baseDamage, CombatScaling scaling, long attackStartTime) {
        try {
            target.setNextGraphics(new Graphics(MAGIC_GRAPHICS_1));
            if (scaling.bossDamageMultiplier > 2.0) {
                target.setNextGraphics(new Graphics(MAGIC_GRAPHICS_2));
            }
            
            npc.setNextAnimation(new Animation(MAGIC_CAST_ANIMATION));
            
            // Split damage into multiple hits
            int damage1 = Utils.random(baseDamage / 2 + 1);
            int damage2 = Utils.random(baseDamage / 2 + 1);
            
            target.applyHit(new Hit(npc, damage1, HitLook.MAGIC_DAMAGE));
            target.applyHit(new Hit(npc, damage2, HitLook.MAGIC_DAMAGE));
            
            // Ancient dragon healing effect
            double healMultiplier = 0.4 + (scaling.bossDamageMultiplier > 2.0 ? 0.2 : 0);
            int healAmount = (int)(baseDamage * healMultiplier);
            npc.heal(healAmount);
            
            // Send projectile
            World.sendProjectile(npc, target, PROJECTILE_ID, 60, 32, 50, 50, 0, 0);
            
            // Schedule delayed hit
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    try {
                        if (!isValidForDelayedAttack(npc, target, attackStartTime)) {
                            stop();
                            return;
                        }
                        
                        int delayedDamage = Utils.random(baseDamage / 3 + 1);
                        if (scaling.bossDamageMultiplier > 2.0) {
                            delayedDamage = (int)(delayedDamage * 1.2);
                        }
                        
                        target.applyHit(new Hit(npc, delayedDamage, HitLook.MAGIC_DAMAGE));
                        
                        if (target instanceof Player) {
                            checkAndWarnLowHPForAncientDragon((Player) target, delayedDamage, "Ancient Magic");
                        }
                        
                    } catch (Exception e) {
                        // Silently handle errors
                    } finally {
                        stop();
                    }
                }
            }, 3);
            
        } catch (Exception e) {
            System.err.println("Error in executeSingleMagicAttack: " + e.getMessage());
        }
    }

    /**
     * Enhanced mechanic warning with scaling context
     */
    private void sendEnhancedMechanicWarning(NPC npc, Player player, String message, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        long currentTime = System.currentTimeMillis();
        
        Long lastWarning = lastMechanicWarning.get(playerKey);
        if (lastWarning != null && (currentTime - lastWarning) < MECHANIC_WARNING_COOLDOWN) {
            return;
        }
        
        // Add scaling context to warning
        String enhancedMessage = message;
        if (scaling.bossDamageMultiplier > 2.5) {
            enhancedMessage += " (MAXIMUM ancient power!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            enhancedMessage += " (Enhanced ancient power!)";
        }
        
        try {
            npc.setNextForceTalk(new ForceTalk(enhancedMessage));
        } catch (Exception e) {
            player.sendMessage("<col=ff6600>Sunfreet: " + enhancedMessage + "</col>");
        }
        
        lastMechanicWarning.put(playerKey, currentTime);
    }

    /**
     * Enhanced accuracy check using BossBalancer v5.0
     */
    private boolean checkBossAccuracy(NPC npc, Entity target, int baseAccuracy) {
        try {
            if (!(target instanceof Player)) {
                return Math.random() < 0.85; // Default 85% accuracy for non-players
            }
            
            Player player = (Player) target;
            
            // Apply BossBalancer accuracy scaling
            int scaledAccuracy = BossBalancer.applyBossAccuracyScaling(baseAccuracy, player, npc);
            
            // Convert accuracy to hit chance (simplified calculation)
            double hitChance = Math.min(0.95, scaledAccuracy / 2000.0); // Cap at 95%
            hitChance = Math.max(0.05, hitChance); // Minimum 5% hit chance
            
            return Math.random() < hitChance;
            
        } catch (Exception e) {
            System.err.println("Error checking boss accuracy: " + e.getMessage());
            return Math.random() < 0.85; // Fallback
        }
    }

    /**
     * Fallback basic attack for error conditions with HP-aware scaling
     */
    private int executeBasicAncientAttack(NPC npc, Entity target) {
        if (npc == null || target == null) return 4;
        
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                npc.setNextAnimation(new Animation(defs.getAttackEmote()));
            }
            
            // Even basic attacks use BossBalancer if target is a player
            int damage;
            if (target instanceof Player) {
                Player player = (Player) target;
                int baseMaxHit = defs != null ? defs.getMaxHit() : 50;
                damage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseMaxHit);
                damage = applyHPAwareAncientDragonDamageScaling(damage, player, "basic_attack");
            } else {
                damage = defs != null ? Utils.random(defs.getMaxHit() + 1) : Utils.random(51);
            }
            
            delayHit(npc, 0, target, getMeleeHit(npc, damage));
            return 4;
        } catch (Exception e) {
            System.err.println("Error in executeBasicAncientAttack(): " + e.getMessage());
            return 4;
        }
    }

    /**
     * Enhanced validation with null safety
     */
    private boolean isValidCombatState(NPC npc, Entity target) {
        if (npc == null || target == null) return false;
        if (npc.isDead() || npc.hasFinished()) return false;
        if (target.isDead() || target.hasFinished()) return false;
        
        if (target instanceof Player) {
            Player player = (Player) target;
            
            try {
                if (isPlayerInSafeArea(player)) return false;
                if (!isPlayerInCombatArea(npc, player)) return false;
                if (isPlayerUnavailableForCombat(player)) return false;
            } catch (Exception e) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Enhanced validation for delayed attacks
     */
    private boolean isValidForDelayedAttack(NPC npc, Entity target, long attackStartTime) {
        if (npc == null || target == null) return false;
        if (!isValidCombatState(npc, target)) return false;
        
        if (target instanceof Player) {
            Player player = (Player) target;
            
            try {
                long currentTime = System.currentTimeMillis();
                if (currentTime - attackStartTime > 15000L) { // 15 second timeout
                    return false;
                }
                
                if (player.isDead() || player.hasFinished() || player.isLocked()) {
                    return false;
                }
                
                // Enhanced controller checks
                if (player.getControlerManager() != null) {
                    Object controller = player.getControlerManager().getControler();
                    if (controller != null) {
                        String controllerName = controller.getClass().getSimpleName();
                        if (controllerName.contains("Death") || controllerName.contains("Safe") || 
                            controllerName.contains("Lobby") || controllerName.contains("Portal")) {
                            return false;
                        }
                    }
                }
                
            } catch (Exception e) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Check if player is in a safe area
     */
    private boolean isPlayerInSafeArea(Player player) {
        if (player == null) return true;
        
        try {
            // Enhanced controller checks
            if (player.getControlerManager() != null) {
                Object controller = player.getControlerManager().getControler();
                if (controller != null) {
                    String controllerName = controller.getClass().getSimpleName();
                    if (controllerName.contains("Death") || controllerName.contains("Safe") || 
                        controllerName.contains("Lobby") || controllerName.contains("Portal")) {
                        return true;
                    }
                }
            }
            
            // Coordinate checks for death areas
            int x = player.getX();
            int y = player.getY();
            
            if (isInArea(x, y, 3088, 3472, 3100, 3484)) return true; // Death portal
            if (isInArea(x, y, 3200, 3200, 3300, 3300)) return true; // Safe zone
            if (isInArea(x, y, 3000, 3000, 3100, 3100)) return true; // Respawn area
            
        } catch (Exception e) {
            return true; // Treat errors as safe
        }
        
        return false;
    }

    /**
     * Check if player is in combat area
     */
    private boolean isPlayerInCombatArea(NPC npc, Player player) {
        if (npc == null || player == null) return false;
        
        try {
            if (npc.getPlane() != player.getPlane()) return false;
            if (!player.withinDistance(npc, 50)) return false;
            
            int npcX = npc.getX();
            int npcY = npc.getY();
            int playerX = player.getX();
            int playerY = player.getY();
            
            int minX = npcX - 25;
            int maxX = npcX + 25;
            int minY = npcY - 25;
            int maxY = npcY + 25;
            
            return isInArea(playerX, playerY, minX, minY, maxX, maxY);
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if player is unavailable for combat
     */
    private boolean isPlayerUnavailableForCombat(Player player) {
        if (player == null) return true;
        
        try {
            if (player.isLocked()) return true;
            
            // Interface checks
            if (player.getInterfaceManager() != null) {
                if (player.getInterfaceManager().containsInterface(666) || 
                    player.getInterfaceManager().containsInterface(102) ||
                    player.getInterfaceManager().containsInterface(12)) {
                    return true;
                }
            }
            
            // Cutscene checks
            if (player.getCutscenesManager() != null && player.getCutscenesManager().hasCutscene()) {
                return true;
            }
            
            // Action checks
            if (player.getActionManager() != null && player.getActionManager().getAction() != null) {
                String actionName = player.getActionManager().getAction().getClass().getSimpleName();
                if (actionName.contains("Death") || actionName.contains("Teleport") || 
                    actionName.contains("Banking") || actionName.contains("Trading")) {
                    return true;
                }
            }
            
        } catch (Exception e) {
            return true; // Treat errors as unavailable
        }
        
        return false;
    }

    // ===== BOSSBALANCER v5.0 COMBAT SESSION CLEANUP =====
    
    /**
     * End Ancient Dragon combat session with proper cleanup
     */
    public void endAncientDragonCombatSession(NPC npc, Entity target) {
        try {
            if (target instanceof Player) {
                Player player = (Player) target;
                Integer playerKey = Integer.valueOf(player.getIndex());
                
                // End BossBalancer v5.0 combat session
                BossBalancer.endCombatSession(player);
                
                // Clear local tracking maps
                combatSessionActive.remove(playerKey);
                lastGuidanceTime.remove(playerKey);
                lastMechanicWarning.remove(playerKey);
                lastScalingType.remove(playerKey);
                attackCounter.remove(playerKey);
                warningStage.remove(playerKey);
                dragonPhaseTracker.remove(playerKey);
                safespotWarnings.remove(playerKey);
                consecutiveAvoids.remove(playerKey);
                hasSeenPowerAttack.remove(playerKey);
                hasSeenMagicAttack.remove(playerKey);
                lastDamageTaken.remove(playerKey);
                
                // Clear BossBalancer player cache
                BossBalancer.clearPlayerCache(player.getIndex());
                
                // Send completion message with v5.0 info
                player.sendMessage("<col=ff6600>Ancient dragon combat session ended. Ancient scaling data cleared.</col>");
            }
        } catch (Exception e) {
            System.err.println("Error ending Ancient Dragon combat session: " + e.getMessage());
        }
    }

    /**
     * Handle prayer changes during ancient dragon combat
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
                player.sendMessage("<col=ffcc00>Prayer change detected. Ancient dragon scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("Sunfreet: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            try {
                Sunfreet combat = new Sunfreet();
                combat.endAncientDragonCombatSession(null, player);
            } catch (Exception e) {
                System.err.println("Error in Sunfreet force cleanup: " + e.getMessage());
            }
        }
    }

    // ===== UTILITY METHODS =====
    
    private boolean isInArea(int x, int y, int minX, int minY, int maxX, int maxY) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    /**
     * Enhanced debug method for testing HP-aware scaling
     */
    public static void debugSunfreetScaling(Player player, NPC sunfreet) {
        if (player == null || sunfreet == null) {
            return;
        }

        try {
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, sunfreet);

            System.out.println("=== SUNFREET COMBAT SCALING DEBUG v5.0 ===");
            System.out.println("Player: " + player.getDisplayName());
            System.out.println("Player HP: " + player.getHitpoints() + "/" + player.getSkills().getLevelForXp(Skills.HITPOINTS));
            System.out.println("Player Power: " + String.format("%.2f", scaling.playerPower));
            System.out.println("Boss Power: " + String.format("%.2f", scaling.bossPower));
            System.out.println("Power Ratio: " + String.format("%.2f", scaling.powerRatio));
            System.out.println("HP Multiplier: " + String.format("%.3f", scaling.bossHpMultiplier));
            System.out.println("Damage Multiplier: " + String.format("%.3f", scaling.bossDamageMultiplier));
            System.out.println("Accuracy Multiplier: " + String.format("%.3f", scaling.bossAccuracyMultiplier));
            System.out.println("Scaling Type: " + scaling.scalingType);
            
            // Test HP-aware damage calculations
            Sunfreet combat = new Sunfreet();
            NPCCombatDefinitions defs = sunfreet.getCombatDefinitions();
            if (defs != null) {
                int baseMaxHit = defs.getMaxHit();
                int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(sunfreet, player, baseMaxHit);
                
                System.out.println("=== HP-AWARE DAMAGE TESTING ===");
                System.out.println("Base Max Hit: " + baseMaxHit);
                System.out.println("BossBalancer Scaled: " + scaledDamage);
                
                // Test different attack types
                int basicDamage = combat.applyHPAwareAncientDragonDamageScaling(scaledDamage, player, "basic_attack");
                int powerDamage = combat.applyHPAwareAncientDragonDamageScaling((int)(scaledDamage * 1.5), player, "power_attack");
                int magicDamage = combat.applyHPAwareAncientDragonDamageScaling((int)(scaledDamage * 1.2), player, "magic_attack");
                int antiSafespotDamage = combat.applyHPAwareAncientDragonDamageScaling((int)(scaledDamage * 1.4), player, "anti_safespot");
                
                System.out.println("Basic Attack (HP-aware): " + basicDamage);
                System.out.println("Power Attack (HP-aware): " + powerDamage);
                System.out.println("Magic Attack (HP-aware): " + magicDamage);
                System.out.println("Anti-Safespot (HP-aware): " + antiSafespotDamage);
                
                // Calculate damage percentages
                int currentHP = player.getHitpoints();
                if (currentHP > 0) {
                    System.out.println("=== DAMAGE PERCENTAGES ===");
                    System.out.println("Basic: " + String.format("%.1f", (double)basicDamage / currentHP * 100) + "%");
                    System.out.println("Power: " + String.format("%.1f", (double)powerDamage / currentHP * 100) + "%");
                    System.out.println("Magic: " + String.format("%.1f", (double)magicDamage / currentHP * 100) + "%");
                    System.out.println("Anti-Safespot: " + String.format("%.1f", (double)antiSafespotDamage / currentHP * 100) + "%");
                }
            }

            System.out.println("=====================================");
        } catch (Exception e) {
            System.err.println("Sunfreet: Error in debug scaling: " + e.getMessage());
        }
    }

    /**
     * Get Sunfreet combat statistics
     */
    public static String getSunfreetCombatStats() {
        return "Sunfreet v5.0 - Active Sessions: " + combatSessionActive.size() + 
               ", Guidance Cooldowns: " + lastGuidanceTime.size() + 
               ", Mechanic Warnings: " + lastMechanicWarning.size() + 
               ", Attack Counters: " + attackCounter.size() + 
               ", Dragon Phases: " + dragonPhaseTracker.size();
    }

    /**
     * Enhanced Sunfreet command handler
     */
    public static void handleSunfreetCommand(Player player, String[] cmd) {
        if (player == null) {
            return;
        }

        try {
            if (cmd.length > 1) {
                String subcommand = cmd[1].toLowerCase();

                if ("debug".equals(subcommand)) {
                    if (!player.isAdmin()) {
                        player.sendMessage("You need admin rights for debug commands.");
                        return;
                    }
                    
                    // Find nearby Sunfreet
                    for (NPC npc : World.getNPCs()) {
                        if (npc.getId() == SUNFREET_NPC_ID && npc.getDistance(player) <= 10) {
                            debugSunfreetScaling(player, npc);
                            player.sendMessage("Sunfreet scaling debug output sent to console.");
                            return;
                        }
                    }
                    player.sendMessage("No Sunfreet found nearby for debugging.");
                    
                } else if ("stats".equals(subcommand)) {
                    player.sendMessage(getSunfreetCombatStats());
                    
                } else if ("cleanup".equals(subcommand)) {
                    forceCleanup(player);
                    player.sendMessage("Sunfreet combat session data cleared.");
                    
                } else if ("phase".equals(subcommand)) {
                    // Show current phase info
                    Integer playerKey = Integer.valueOf(player.getIndex());
                    Integer currentPhase = dragonPhaseTracker.get(playerKey);
                    if (currentPhase != null) {
                        player.sendMessage("Current Ancient Dragon Phase: " + currentPhase);
                    } else {
                        player.sendMessage("No active Sunfreet combat session.");
                    }
                    
                } else {
                    player.sendMessage("Usage: ;;sunfreet [debug|stats|cleanup|phase]");
                }
            } else {
                player.sendMessage("Sunfreet Combat v5.0 with BossBalancer integration and HP-aware scaling");
                if (player.isAdmin()) {
                    player.sendMessage("Admin: ;;sunfreet debug - Debug scaling near Sunfreet");
                }
            }

        } catch (Exception e) {
            player.sendMessage("Error in Sunfreet command: " + e.getMessage());
        }
    }

    /**
     * Enhanced onCombatEnd method with v5.0 cleanup
     */
    public void onCombatEnd(NPC npc) {
        try {
            // Find all players who were fighting this NPC and clean up their sessions
            for (Map.Entry<Integer, Boolean> entry : combatSessionActive.entrySet()) {
                Integer playerIndex = entry.getKey();
                try {
                    Player player = World.getPlayers().get(playerIndex);
                    if (player != null) {
                        endAncientDragonCombatSession(npc, player);
                    } else {
                        // Player is null, clean up data manually
                        combatSessionActive.remove(playerIndex);
                        lastGuidanceTime.remove(playerIndex);
                        lastMechanicWarning.remove(playerIndex);
                        lastScalingType.remove(playerIndex);
                        attackCounter.remove(playerIndex);
                        warningStage.remove(playerIndex);
                        dragonPhaseTracker.remove(playerIndex);
                        safespotWarnings.remove(playerIndex);
                        consecutiveAvoids.remove(playerIndex);
                        hasSeenPowerAttack.remove(playerIndex);
                        hasSeenMagicAttack.remove(playerIndex);
                        lastDamageTaken.remove(playerIndex);
                    }
                } catch (Exception e) {
                    // Clean up manually on error
                    combatSessionActive.remove(playerIndex);
                    lastGuidanceTime.remove(playerIndex);
                    lastMechanicWarning.remove(playerIndex);
                    lastScalingType.remove(playerIndex);
                    attackCounter.remove(playerIndex);
                    warningStage.remove(playerIndex);
                    dragonPhaseTracker.remove(playerIndex);
                    safespotWarnings.remove(playerIndex);
                    consecutiveAvoids.remove(playerIndex);
                    hasSeenPowerAttack.remove(playerIndex);
                    hasSeenMagicAttack.remove(playerIndex);
                    lastDamageTaken.remove(playerIndex);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in Sunfreet onCombatEnd: " + e.getMessage());
        }
    }

    /**
     * Legacy compatibility method - cleanup when combat ends
     */
    public static void cleanupCombatSession(Player player) {
        if (player != null) {
            forceCleanup(player);
        }
    }

    /**
     * Get ancient dragon power tier name
     */
    private String getAncientDragonPowerTierName(int tier) {
        switch (tier) {
            case 1: return "Awakening Ancient";
            case 2: return "Stirring Ancient"; 
            case 3: return "Rising Ancient";
            case 4: return "Enraged Ancient";
            case 5: return "Furious Ancient";
            case 6: return "Wrathful Ancient";
            case 7: return "Legendary Ancient";
            case 8: return "Mythical Ancient";
            case 9: return "Primordial Ancient";
            case 10: return "Transcendent Ancient";
            default: return "Unknown Ancient";
        }
    }

    /**
     * Get ancient dragon combat type name
     */
    private String getAncientDragonTypeName(int type) {
        switch (type) {
            case 0: return "Ancient Warrior";
            case 1: return "Ancient Ranger";
            case 2: return "Ancient Mage";
            case 3: return "Ancient Hybrid";
            case 4: return "Ancient Mystic";
            case 5: return "Ancient Guardian";
            case 6: return "Ancient Overlord";
            default: return "Ancient Being";
        }
    }

    /**
     * Enhanced performance monitoring
     */
    public static void performanceReport() {
        System.out.println("=== SUNFREET COMBAT PERFORMANCE REPORT v5.0 ===");
        System.out.println("Active Combat Sessions: " + combatSessionActive.size());
        System.out.println("Guidance Cooldowns: " + lastGuidanceTime.size());
        System.out.println("Mechanic Warnings: " + lastMechanicWarning.size());
        System.out.println("Attack Counters: " + attackCounter.size());
        System.out.println("Dragon Phase Trackers: " + dragonPhaseTracker.size());
        System.out.println("Safespot Warnings: " + safespotWarnings.size());
        System.out.println("Consecutive Avoids: " + consecutiveAvoids.size());
        System.out.println("Power Attack Seen: " + hasSeenPowerAttack.size());
        System.out.println("Magic Attack Seen: " + hasSeenMagicAttack.size());
        System.out.println("Last Damage Taken: " + lastDamageTaken.size());
        
        // Memory usage estimation
        int totalEntries = combatSessionActive.size() + lastGuidanceTime.size() + 
                          lastMechanicWarning.size() + attackCounter.size() + 
                          dragonPhaseTracker.size() + safespotWarnings.size() +
                          consecutiveAvoids.size() + hasSeenPowerAttack.size() +
                          hasSeenMagicAttack.size() + lastDamageTaken.size();
        
        System.out.println("Total Memory Entries: " + totalEntries);
        System.out.println("Estimated Memory Usage: ~" + (totalEntries * 64) + " bytes");
        System.out.println("=================================================");
    }

    /**
     * Cleanup old entries (call periodically)
     */
    public static void cleanupOldEntries() {
        long currentTime = System.currentTimeMillis();
        long cleanupAge = 30 * 60 * 1000L; // 30 minutes
        
        try {
            // Clean up guidance cooldowns older than 30 minutes
            lastGuidanceTime.entrySet().removeIf(entry -> 
                (currentTime - entry.getValue()) > cleanupAge);
            
            // Clean up mechanic warnings older than 30 minutes
            lastMechanicWarning.entrySet().removeIf(entry -> 
                (currentTime - entry.getValue()) > cleanupAge);
            
            // Clean up last damage taken older than 30 minutes
            lastDamageTaken.entrySet().removeIf(entry -> 
                (currentTime - entry.getValue()) > cleanupAge);
            
            System.out.println("Sunfreet: Cleaned up old combat entries.");
        } catch (Exception e) {
            System.err.println("Error in Sunfreet cleanup: " + e.getMessage());
        }
    }
}