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
import com.rs.cache.loaders.NPCDefinitions;

/**
 * Enhanced Ahrim the Blighted Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Necromantic magic mastery, stat drain mechanics, curse spells, HP-aware damage scaling
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 3.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System
 */
public class AhrimCombat extends CombatScript {

    // ===== NECROMANTIC PHASES - Enhanced for v5.0 =====
    private static final double APPRENTICE_THRESHOLD = 0.75;  // 75% HP - apprentice phase
    private static final double ADEPT_THRESHOLD = 0.50;       // 50% HP - adept necromancer
    private static final double MASTER_THRESHOLD = 0.25;      // 25% HP - master necromancer
    private static final double ARCHLICH_THRESHOLD = 0.10;    // 10% HP - archlich transformation

    // ===== ENHANCED GUIDANCE SYSTEM - Intelligent scaling aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentNecromancyPhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> statDrainCount = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 180000; // 3 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 2500; // 2.5 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - CRITICAL SAFETY SYSTEM =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.30; // Max 30% of player HP per hit (magic is precise)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.42;  // Max 42% for master spells
    private static final double DRAIN_DAMAGE_PERCENT = 0.36;     // Max 36% for drain spells
    private static final double ARCHLICH_DAMAGE_PERCENT = 0.48;  // Max 48% for archlich transformation
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 450;          // Hard cap (30% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 25;               // Minimum damage to prevent 0 hits

    // ===== STAT DRAIN MECHANICS =====
    private static final int STAT_DRAIN_CHANCE = 4; // 1 in 4 chance for stat effects
    private static final int MAX_STAT_DRAINS_PER_FIGHT = 10; // Prevent excessive draining
    private static final int BASE_DRAIN_AMOUNT = 3; // Base stat drain amount

    // ===== NECROMANTIC ATTACK PATTERNS with v5.0 intelligence =====
    private static final NecromancyAttackPattern[] NECROMANCY_ATTACK_PATTERNS = {
        new NecromancyAttackPattern(2078, 157, 0, "shadow_bolt", false, ""),
        new NecromancyAttackPattern(2078, 400, 0, "drain_spell", true, "DRAIN SPELL incoming - necromantic stat siphon!"),
        new NecromancyAttackPattern(2078, 401, 0, "curse_magic", true, "CURSE MAGIC incoming - devastating necromantic curse!"),
        new NecromancyAttackPattern(2078, 157, 400, "shadow_storm", true, "SHADOW STORM incoming - multiple necromantic bolts!"),
        new NecromancyAttackPattern(2078, 401, 401, "archlich_transformation", true, "ARCHLICH TRANSFORMATION incoming - ultimate necromantic power!")
    };

    // ===== SAFE SPOT PREVENTION - Necromantic-themed =====
    private static final Map<Integer, Integer> consecutiveAvoidedCurses = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastCurseHit = new ConcurrentHashMap<Integer, Boolean>();

    @Override
    public Object[] getKeys() {
        return new Object[] { 2027 }; // Ahrim the Blighted
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeNecromancyCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentNecromancyGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorNecromancyScalingChanges(player, scaling);
        
        // Update necromancy phase tracking with v5.0 scaling
        updateIntelligentNecromancyPhaseTracking(npc, scaling);
        
        // Check for necromantic-themed safe spotting
        checkNecromancySafeSpotting(player, npc, scaling);
        
        // Enhanced necromantic taunts with scaling-based frequency
        performEnhancedNecromancyTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentNecromancyAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize necromancy combat session using BossBalancer v5.0
     */
    private void initializeNecromancyCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            statDrainCount.put(sessionKey, Integer.valueOf(0));
            consecutiveAvoidedCurses.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastCurseHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced necromancy combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentNecromancyWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for necromantic combat
            performInitialNecromancyArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial necromancy armor analysis
     */
    private void performInitialNecromancyArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=8A2BE2>Necromantic Analysis: Exposed flesh detected. The shadows hunger for weakness!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=4B0082>Necromantic Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance). Death magic adapts...</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from necromantic attacks
     */
    private int applyHPAwareNecromancyDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (necromancy is precise and calculated)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on necromantic attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "archlich_transformation":
                case "ultimate_necromancy":
                    damagePercent = ARCHLICH_DAMAGE_PERCENT;
                    break;
                case "curse_magic":
                case "master_spell":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "drain_spell":
                case "stat_siphon":
                    damagePercent = DRAIN_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 78% of current HP for necromancy
            if (currentHP > 0) {
                int emergencyCap = (int)(currentHP * 0.78);
                safeDamage = Math.min(safeDamage, emergencyCap);
            }
            
            return safeDamage;
            
        } catch (Exception e) {
            // Fallback to absolute cap
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
    }

    /**
     * NEW v5.0: Send HP warning if player is in danger from necromantic attacks
     */
    private void checkAndWarnLowHPForNecromancy(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming necromantic damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.68) {
                    player.sendMessage("<col=ff0000>NECROMANCY WARNING: This dark spell will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.48) {
                    player.sendMessage("<col=8A2BE2>NECROMANCY WARNING: Heavy necromantic damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent necromancy welcome message based on power analysis
     */
    private String getIntelligentNecromancyWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Ahrim";
        
        message.append("<col=8A2BE2>").append(npcName).append(" weaves dark magic, analyzing your spiritual essence (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=4B0082>[Necromantic mastery: +").append(diffIncrease).append("% dark power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Death's mercy: -").append(assistance).append("% necromantic damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Necromantic resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Death magic acknowledges your protection]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent necromancy guidance with power-based scaling awareness
     */
    private void provideIntelligentNecromancyGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentNecromancyGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent necromancy guidance message based on power analysis
     */
    private String getIntelligentNecromancyGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentNecromancyPhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getNecromancyScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=4B0082>Necromantic Analysis: Missing armor exposes you to stat drain! Necromantic damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME necromantic power due to scaling!)" : "";
                    return "<col=8A2BE2>Necromantic Analysis: Master necromancer phase reached. Death magic dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=660000>Necromantic Analysis: Archlich transformation! Maximum death magic unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=4B0082>Necromantic Analysis: Extreme necromantic scaling detected! Consider facing greater liches!</col>";
                }
                break;
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get necromancy scaling analysis message
     */
    private String getNecromancyScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Ahrim";
        
        String baseMessage = "<col=DDA0DD>Necromantic Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>" + npcName + "'s death magic restrained! Necromantic damage reduced by " + 
                   assistancePercent + "% due to insufficient arcane preparation.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=4B0082>" + npcName + "'s necromantic mastery escalated! Death magic increased by " + 
                   difficultyIncrease + "% due to superior magical defenses.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=8A2BE2>Balanced necromantic encounter. Optimal death magic resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight magical advantage detected. " + npcName + "'s necromantic intensity increased by " + 
                   difficultyIncrease + "% for arcane balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Necromantic power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during necromantic combat
     */
    private void monitorNecromancyScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getNecromancyScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get necromancy scaling change message
     */
    private String getNecromancyScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Necromancy Update: Arcane balance improved! Death magic restraint reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=4B0082>Necromancy Update: Necromantic mastery now active due to increased magical power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Necromancy Update: Magic resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=8A2BE2>Necromancy Update: Magical protection restored! Necromantic damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent necromancy phase tracking with BossBalancer integration
     */
    private void updateIntelligentNecromancyPhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentNecromancyPhase(npc);
        
        Integer lastPhase = currentNecromancyPhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentNecromancyPhase.put(npcKey, newPhase);
            handleIntelligentNecromancyPhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current necromancy phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentNecromancyPhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= ARCHLICH_THRESHOLD) return 4;
            if (hpPercent <= MASTER_THRESHOLD) return 3;
            if (hpPercent <= ADEPT_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent necromancy phase transitions with scaling integration
     */
    private void handleIntelligentNecromancyPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "The necromancer";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("The shadows grow stronger!"));
            npc.setNextGraphics(new Graphics(157));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED NECROMANTIC MASTERY UNLEASHED!" : "Death magic flows through me!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(400));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE ARCHLICH TRANSFORMATION - MAXIMUM DEATH MAGIC!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED ARCHLICH FORM!" : "I transcend mortality itself!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(401));
            break;
        }
    }

    /**
     * NEW v5.0: Check for necromantic safe spotting
     */
    private void checkNecromancySafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveAvoidedCurses.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastCurseHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect necromantic-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean necromancerFrustrated = consecutiveCount > 2; // Death magic doesn't tolerate avoidance
        boolean recentAvoidance = !lastHit;
        boolean cannotReachWithProjectile = !npc.clipedProjectile(player, true);
        
        boolean necromancySafeSpot = playerDistanced && necromancerFrustrated && (recentAvoidance || cannotReachWithProjectile);
        
        if (necromancySafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating necromantic-themed responses
            if (warningCount >= 3) {
                performNecromancyAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=8A2BE2>The necromantic energies stabilize as you engage in direct combat...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform necromancy anti-safe spot measure
     */
    private void performNecromancyAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=4B0082>Death magic seeks those who hide from necromantic justice!</col>");
        
        // Shadow bolt that penetrates through obstacles
        npc.setNextAnimation(new Animation(2078));
        npc.setNextForceTalk(new ForceTalk("COWARD! The shadows will find you!"));
        
        // Enhanced damage based on scaling
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.4) : 180; // Necromantic pursuit
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareNecromancyDamageScaling(scaledDamage, player, "shadow_pursuit");
        
        // Send shadow projectile
        World.sendProjectile(npc, player, 162, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 2, player, getMagicHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>NECROMANCY PENALTY: Safe spotting detected - shadow magic pierces through!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced necromantic taunts with scaling-based frequency
     */
    private void performEnhancedNecromancyTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on necromancy phase and scaling
        int necromancyPhase = getCurrentNecromancyPhase(npc);
        int tauntChance = 9 + (necromancyPhase * 5); // Base 14% to 29% based on phase
        
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance += 13; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance += 9; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced necromantic taunts based on phase and scaling
            performScaledNecromancyTaunt(npc, necromancyPhase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled necromancy taunt based on phase and scaling
     */
    private void performScaledNecromancyTaunt(NPC npc, int necromancyPhase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "Death magic flows through me!",
            "Your stats will drain away!",
            "The shadows hunger for life!",
            "Necromancy is eternal!",
            "Feel your strength ebb!",
            "Dark magic commands you!",
            "Life force is mine to control!"
        };
        
        String[] masterTaunts = {
            "NECROMANTIC MASTERY AWAKENS!",
            "YOUR ESSENCE FEEDS MY POWER!",
            "DEATH MAGIC TRANSCENDS ALL!",
            "WITNESS TRUE NECROMANTIC FURY!",
            "THE SHADOWS OBEY MY WILL!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED NECROMANTIC MASTERY ACTIVATED!",
            "YOUR SUPERIOR DEFENSES FUEL MY DARK MAGIC!",
            "MAXIMUM DEATH MAGIC UNLEASHED!",
            "ULTIMATE NECROMANCER'S DOMINION!",
            "TRANSCENDENT ARCHLICH POWER!"
        };
        
        String selectedTaunt;
        if (isHighScaling && necromancyPhase >= 3) {
            // Use enhanced taunts for high scaling + high necromancy
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else if (necromancyPhase >= 2) {
            // Use master taunts for high necromancy phases
            selectedTaunt = masterTaunts[Utils.random(masterTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent necromancy warning system
     */
    private int performIntelligentNecromancyAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int necromancyPhase = getCurrentNecromancyPhase(npc);
            NecromancyAttackPattern pattern = selectIntelligentNecromancyAttackPattern(necromancyPhase, scaling, currentCount);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentNecromancyWarning(scaling, currentCount)) {
                sendIntelligentNecromancyPreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledNecromancyAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentNecromancyAttackDelay(npc, necromancyPhase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledNecromancyAttack(npc, player, pattern, scaling);
                return getIntelligentNecromancyAttackDelay(npc, necromancyPhase, scaling);
            }
            
        } catch (Exception e) {
            return 4;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent necromancy warning probability based on scaling
     */
    private boolean shouldGiveIntelligentNecromancyWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing necromancy
        boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
        int warningFrequency = isUndergeared ? 3 : 5; // Every 3rd vs 5th attack
        
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
     * ENHANCED v5.0: Intelligent necromancy pre-attack warning with scaling context
     */
    private void sendIntelligentNecromancyPreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME necromantic power due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense death magic due to scaling!)";
        }
        
        player.sendMessage("<col=4B0082>NECROMANCY WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent necromancy attack pattern selection with scaling consideration
     */
    private NecromancyAttackPattern selectIntelligentNecromancyAttackPattern(int necromancyPhase, CombatScaling scaling, int attackCount) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on necromancy phase, scaling, and progression
        int baseSpecialChance = (necromancyPhase - 1) * 14; // 14% per necromancy phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 13 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 9 ? 6 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for necromantic attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        if (isOvergeared) {
            // More aggressive necromantic patterns for overgeared players
            if (roll < 7 + specialChance) return NECROMANCY_ATTACK_PATTERNS[4]; // Archlich transformation
            if (roll < 19 + specialChance) return NECROMANCY_ATTACK_PATTERNS[2]; // Curse magic  
            if (roll < 34 + specialChance) return NECROMANCY_ATTACK_PATTERNS[3]; // Shadow storm
            if (roll < 48 + specialChance) return NECROMANCY_ATTACK_PATTERNS[1]; // Drain spell
        } else {
            // Standard necromantic pattern selection
            if (roll < 4 + specialChance) return NECROMANCY_ATTACK_PATTERNS[4]; // Archlich transformation
            if (roll < 14 + specialChance) return NECROMANCY_ATTACK_PATTERNS[2]; // Curse magic  
            if (roll < 26 + specialChance) return NECROMANCY_ATTACK_PATTERNS[3]; // Shadow storm
            if (roll < 38 + specialChance) return NECROMANCY_ATTACK_PATTERNS[1]; // Drain spell
        }
        
        return NECROMANCY_ATTACK_PATTERNS[0]; // Shadow bolt
    }

    /**
     * ENHANCED v5.0: Execute necromancy attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledNecromancyAttack(NPC npc, Player player, NecromancyAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced necromantic damage calculation with v5.0 intelligence
            int necromancyPhase = getCurrentNecromancyPhase(npc);
            double necromancyModifier = 1.0 + (necromancyPhase - 1) * 0.16; // 16% per necromancy phase (death magic mastery)
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * necromancyModifier);
            
            // Execute different necromantic attack types with v5.0 scaling and HP-aware damage
            if ("archlich_transformation".equals(pattern.name)) {
                executeIntelligentArchlichTransformation(npc, player, baseDamage, scaling);
            } else if ("curse_magic".equals(pattern.name)) {
                executeIntelligentCurseMagic(npc, player, baseDamage, scaling);
            } else if ("shadow_storm".equals(pattern.name)) {
                executeIntelligentShadowStorm(npc, player, baseDamage, scaling);
            } else if ("drain_spell".equals(pattern.name)) {
                executeIntelligentDrainSpell(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleNecromancyAttack(npc, player, baseDamage, 2, scaling, "shadow_bolt");
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
            // Enhanced fallback - execute basic necromancy attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleNecromancyAttack(npc, player, scaledDamage, 2, scaling, "shadow_bolt");
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent archlich transformation attack with HP-aware scaling
     */
    private void executeIntelligentArchlichTransformation(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ultimate necromantic attack - 230% damage with death magic
        int damage = (int)(baseDamage * 2.3) + Utils.random(baseDamage / 3);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for ultimate attacks
        int safeDamage = applyHPAwareNecromancyDamageScaling(scaledDamage, player, "archlich_transformation");
        checkAndWarnLowHPForNecromancy(player, safeDamage);
        
        // Send dark projectile
        World.sendProjectile(npc, player, 162, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 2, player, getMagicHit(npc, safeDamage));
        
        // Apply massive stat drain with transformation
        applyNecromancyStatDrain(npc, player, true, scaling);
        
        // Update attack counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer spellCount = attackCounter.get(playerKey);
        if (spellCount == null) spellCount = 0;
        attackCounter.put(playerKey, spellCount + 1);
    }

    /**
     * ENHANCED v5.0: Intelligent curse magic attack with HP-aware scaling
     */
    private void executeIntelligentCurseMagic(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Curse magic damage (190% base for enhanced necromantic power)
        int damage = (int)(baseDamage * 1.9) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for curse magic
        int safeDamage = applyHPAwareNecromancyDamageScaling(scaledDamage, player, "curse_magic");
        checkAndWarnLowHPForNecromancy(player, safeDamage);
        
        // Send curse projectile
        World.sendProjectile(npc, player, 162, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 2, player, getMagicHit(npc, safeDamage));
        
        // Apply multi-stat drain with curse
        if (safeDamage > 0) {
            applyNecromancyStatDrain(npc, player, false, scaling);
        }
    }

    /**
     * ENHANCED v5.0: Intelligent shadow storm attack with HP-aware scaling
     */
    private void executeIntelligentShadowStorm(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Shadow storm - 3 hits at 65%, 75%, 85% damage
        for (int i = 0; i < 3; i++) {
            int hitDamage = (int)(baseDamage * (0.65 + (i * 0.1))) + Utils.random(baseDamage / 5);
            int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, hitDamage);
            
            // CRITICAL: Apply HP-aware damage scaling for each shadow bolt
            int safeDamage = applyHPAwareNecromancyDamageScaling(scaledDamage, player, "shadow_storm");
            if (i == 0) checkAndWarnLowHPForNecromancy(player, safeDamage * 3); // Warn for total damage
            
            // Send shadow projectiles
            World.sendProjectile(npc, player, 162, 41, 16, 41, 35, 16, 0);
            delayHit(npc, 2 + i, player, getMagicHit(npc, safeDamage));
        }
        
        // Update attack counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer spellCount = attackCounter.get(playerKey);
        if (spellCount == null) spellCount = 0;
        attackCounter.put(playerKey, spellCount + 3);
    }

    /**
     * ENHANCED v5.0: Intelligent drain spell attack with HP-aware scaling
     */
    private void executeIntelligentDrainSpell(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Drain spell damage (170% base for enhanced stat siphon)
        int damage = (int)(baseDamage * 1.7) + Utils.random(baseDamage / 3);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for drain spells
        int safeDamage = applyHPAwareNecromancyDamageScaling(scaledDamage, player, "drain_spell");
        checkAndWarnLowHPForNecromancy(player, safeDamage);
        
        // Send drain projectile
        World.sendProjectile(npc, player, 162, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 2, player, getMagicHit(npc, safeDamage));
        
        // Apply enhanced stat drain
        if (safeDamage > 0) {
            applyNecromancyStatDrain(npc, player, false, scaling);
        }
    }

    /**
     * ENHANCED v5.0: Intelligent single necromancy attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleNecromancyAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareNecromancyDamageScaling(scaledDamage, player, attackType);
        if (!"shadow_bolt".equals(attackType)) {
            checkAndWarnLowHPForNecromancy(player, safeDamage);
        }
        
        // Send shadow projectile
        World.sendProjectile(npc, player, 162, 41, 16, 41, 35, 16, 0);
        delayHit(npc, delay, player, getMagicHit(npc, safeDamage));
        
        // Apply stat drain chance
        if (safeDamage > 0 && Utils.random(STAT_DRAIN_CHANCE) == 0) {
            applyNecromancyStatDrain(npc, player, false, scaling);
        }
        
        // Track if attack hit
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastCurseHit.put(playerKey, safeDamage > 0);
        
        // Update avoided curses counter
        Integer avoidedCount = consecutiveAvoidedCurses.get(playerKey);
        if (avoidedCount == null) avoidedCount = 0;
        if (safeDamage <= 0) {
            consecutiveAvoidedCurses.put(playerKey, avoidedCount + 1);
        } else {
            consecutiveAvoidedCurses.put(playerKey, 0);
        }
    }

    /**
     * ENHANCED v5.0: Apply necromancy stat drain with HP-aware limits
     */
    private void applyNecromancyStatDrain(NPC npc, Player player, boolean isArchlich, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer drainCount = statDrainCount.get(playerKey);
        if (drainCount == null) drainCount = 0;
        
        // Limit stat drains per fight
        if (drainCount >= MAX_STAT_DRAINS_PER_FIGHT) {
            return;
        }
        
        player.setNextGraphics(new Graphics(400, 0, 100));
        
        // Scale drain based on scaling difficulty and attack type
        double drainMultiplier = scaling.bossDamageMultiplier > 1.5 ? 1.3 : 1.0;
        int baseDrain = isArchlich ? (BASE_DRAIN_AMOUNT + 3) : BASE_DRAIN_AMOUNT;
        int drainAmount = (int)(baseDrain * drainMultiplier);
        
        if (isArchlich) {
            // Archlich transformation drains multiple stats heavily
            int[] allCombatStats = {Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGE, Skills.MAGIC, Skills.PRAYER};
            
            for (int i = 0; i < 4; i++) { // Drain 4 different stats
                int statToDrain = allCombatStats[Utils.random(allCombatStats.length)];
                drainSingleStat(player, statToDrain, drainAmount);
            }
            
            player.sendMessage("<col=4B0082>Archlich transformation devastates your essence!</col>");
            npc.setNextForceTalk(new ForceTalk("Your life force feeds my transformation!"));
        } else {
            // Standard necromancy drains 1-2 stats
            int[] primaryStats = {Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE};
            int statToDrain = primaryStats[Utils.random(primaryStats.length)];
            drainSingleStat(player, statToDrain, drainAmount);
            
            // Chance for second stat
            if (Utils.random(3) == 0) {
                int secondStat = primaryStats[Utils.random(primaryStats.length)];
                if (secondStat != statToDrain) {
                    drainSingleStat(player, secondStat, drainAmount - 1);
                }
            }
            
            player.sendMessage("<col=8A2BE2>Necromantic magic saps your strength!</col>");
            npc.setNextForceTalk(new ForceTalk("Your power becomes mine!"));
        }
        
        // Update drain counter
        statDrainCount.put(playerKey, drainCount + 1);
    }

    /**
     * Drain a single stat with feedback
     */
    private void drainSingleStat(Player player, int statId, int drainAmount) {
        int currentLevel = player.getSkills().getLevel(statId);
        int newLevel = Math.max(0, currentLevel - drainAmount);
        player.getSkills().set(statId, newLevel);
        
        String statName = Skills.SKILL_NAME[statId];
        player.sendMessage("Your " + statName + " is drained by " + drainAmount + " levels!");
    }

    /**
     * ENHANCED v5.0: Intelligent necromancy attack delay with scaling consideration
     */
    private int getIntelligentNecromancyAttackDelay(NPC npc, int necromancyPhase, CombatScaling scaling) {
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) return 4;
        
        int baseDelay = defs.getAttackDelay();
        int necromancySpeedBonus = Math.max(0, necromancyPhase - 1); // Necromancy mastery makes casting faster
        
        // v5.0 intelligent scaling can affect attack speed for necromancy
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = 1; // Faster for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingSpeedBonus = 1; // Slightly faster for high scaling
        }
        
        return Math.max(3, baseDelay - necromancySpeedBonus - scalingSpeedBonus);
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
     * ENHANCED v5.0: Handle necromancy combat end with proper cleanup
     */
    public static void onNecromancyCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentNecromancyPhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            statDrainCount.remove(playerKey);
            consecutiveAvoidedCurses.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastCurseHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=8A2BE2>Necromantic combat session ended. Death magic scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("AhrimCombat: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during necromantic combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Necromantic scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("AhrimCombat: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onNecromancyCombatEnd(player, null);
        }
    }

    /**
     * Enhanced necromancy attack pattern data structure
     */
    private static class NecromancyAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        NecromancyAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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