package com.rs.game.npc.combat.impl.riseofthesix;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
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
import com.rs.cache.loaders.NPCDefinitions;

/**
 * Enhanced Karil Combat Script with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Progressive ranged phases, rapid fire mechanics, explosive attacks, poison rain, enhanced agility draining
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 3.0 - FULL BossBalancer v5.0 Integration with Intelligent Ranged Combat Scaling & HP-Aware System for ROTS
 */
public class Karils extends CombatScript {
    
    // ===== RANGED PHASES - Enhanced for v5.0 =====
    private static final double RANGER_THRESHOLD = 0.80;        // 80% HP - normal ranger
    private static final double MARKSMAN_THRESHOLD = 0.55;      // 55% HP - marksman phase  
    private static final double ASSASSIN_THRESHOLD = 0.30;      // 30% HP - assassin phase
    private static final double SHADOW_ARCHER_THRESHOLD = 0.12; // 12% HP - shadow archer
    
    // ===== ENHANCED GUIDANCE SYSTEM - Minigame-aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentRangedPhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> rapidFireCount = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 120000; // 2 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 20000; // 20 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 2000; // 2 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 4; // More warnings for complex mechanics
    
    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - MINIGAME BALANCED =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.30; // Max 30% of player HP per hit (ranged balanced)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.42;  // Max 42% for explosive attacks
    private static final double RAPID_DAMAGE_PERCENT = 0.25;     // Max 25% per rapid fire shot
    private static final double POISON_DAMAGE_PERCENT = 0.35;    // Max 35% for poison rain
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 450;          // Hard cap (30% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 25;               // Minimum damage to prevent 0 hits
    
    // ===== AGILITY DRAIN AND EFFECTS =====
    private static final int AGILITY_DRAIN_CHANCE = 4; // 1 in 4 chance for agility drain
    private static final int MAX_RAPID_FIRES_PER_FIGHT = 8; // Prevent spam
    private static final double AGILITY_DRAIN_PERCENT = 0.25; // 25% agility drain
    
    // ===== RANGED ATTACK PATTERNS with v5.0 intelligence =====
    private static final RangedAttackPattern[] RANGED_ATTACK_PATTERNS = {
        new RangedAttackPattern(18301, 0, 0, "crossbow_shot", false, ""),
        new RangedAttackPattern(18302, 401, 0, "agility_drain", true, "AGILITY DRAIN incoming - mobility sapped!"),
        new RangedAttackPattern(18302, 26, 0, "rapid_fire", true, "RAPID FIRE incoming - crossbow barrage!"),
        new RangedAttackPattern(18303, 157, 0, "explosive_shot", true, "EXPLOSIVE SHOT incoming - area damage!"),
        new RangedAttackPattern(18304, 170, 348, "poison_rain", true, "POISON RAIN incoming - area denial!")
    };
    
    // ===== SAFE SPOT PREVENTION - Ranged-themed =====
    private static final Map<Integer, Integer> consecutiveAvoidedShots = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastShotHit = new ConcurrentHashMap<Integer, Boolean>();
    
    // ===== ANIMATION AND GRAPHICS CONSTANTS =====
    private static final int NORMAL_ATTACK_ANIM = 18301;
    private static final int RAPID_FIRE_ANIM = 18302;
    private static final int EXPLOSIVE_SHOT_ANIM = 18303;
    private static final int POISON_RAIN_ANIM = 18304;
    
    private static final int AGILITY_DRAIN_GFX = 401;
    private static final int POISON_EFFECT_GFX = 170;
    private static final int EXPLOSIVE_BLAST_GFX = 157;
    private static final int RAPID_FIRE_GFX = 26;
    private static final int BLEEDING_GFX = 377;
    
    // ===== PROJECTILE CONSTANTS =====
    private static final int NORMAL_BOLT = 27;
    private static final int EXPLOSIVE_BOLT = 28;
    private static final int POISON_ARROW = 29;
    private static final int RAPID_BOLT = 30;
    
    @Override
    public Object[] getKeys() {
        return new Object[] { 18543 }; // Rise of the Six Karils
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeRangedCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0 (special handling for minigames)
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentRangedGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorRangedScalingChanges(player, scaling);
        
        // Update ranged phase tracking with v5.0 scaling
        updateIntelligentRangedPhaseTracking(npc, scaling);
        
        // Check for ranged-themed safe spotting
        checkRangedSafeSpotting(player, npc, scaling);
        
        // Enhanced ranged taunts with scaling-based frequency
        performEnhancedRangedTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentRangedAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize ranged combat session using BossBalancer v5.0
     */
    private void initializeRangedCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            rapidFireCount.put(sessionKey, Integer.valueOf(0));
            consecutiveAvoidedShots.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastShotHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced ranged combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentRangedWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for ranged combat
            performInitialRangedArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial ranged armor analysis
     */
    private void performInitialRangedArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=8B4513>Ranged Analysis: Missing armor detected. Crossbow bolts will find their mark!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=2E8B57>Ranged Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance). Precision shots still deadly...</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from ranged attacks
     */
    private int applyHPAwareRangedDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (ranged attacks are precision-focused)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on ranged attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "poison_rain":
                case "shadow_archer":
                    damagePercent = POISON_DAMAGE_PERCENT;
                    break;
                case "explosive_shot":
                case "explosive_blast":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "rapid_fire":
                case "barrage_shot":
                    damagePercent = RAPID_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 80% of current HP for ranged
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
     * NEW v5.0: Send HP warning if player is in danger from ranged attacks
     */
    private void checkAndWarnLowHPForRanged(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming ranged damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.65) {
                    player.sendMessage("<col=ff0000>RANGED WARNING: This shot will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.45) {
                    player.sendMessage("<col=8B4513>RANGED WARNING: Heavy crossbow damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent ranged welcome message based on power analysis
     */
    private String getIntelligentRangedWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Karils";
        
        message.append("<col=8B4513>").append(npcName).append(" readies his crossbow, calculating trajectory (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=2E8B57>[Precision enhanced: +").append(diffIncrease).append("% ranged accuracy]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Crossbow restraint: -").append(assistance).append("% ranged damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Ranged resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full ranged protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent ranged guidance with power-based scaling awareness
     */
    private void provideIntelligentRangedGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentRangedGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent ranged guidance message based on power analysis
     */
    private String getIntelligentRangedGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentRangedPhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getRangedScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=2E8B57>Ranged Analysis: Missing armor exposes you to precise shots! Crossbow damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME ranged power due to scaling!)" : "";
                    return "<col=8B4513>Ranged Analysis: Assassin phase reached. Crossbow precision dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=654321>Ranged Analysis: Shadow archer transformation! Maximum crossbow mastery unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=2E8B57>Ranged Analysis: Extreme ranged scaling detected! Consider facing higher-tier archers!</col>";
                }
                break;
                
            case 3:
                // Fourth warning: Advanced tactics
                return "<col=8B7355>Ranged Tactics: Watch for explosive shots and poison rain. Stay mobile to avoid area attacks!</col>";
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get ranged scaling analysis message
     */
    private String getRangedScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Karils";
        
        String baseMessage = "<col=DDA0DD>Ranged Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>" + npcName + "'s crossbow precision restrained! Ranged damage reduced by " + 
                   assistancePercent + "% due to insufficient defensive preparation.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=2E8B57>" + npcName + "'s crossbow mastery escalated! Ranged damage increased by " + 
                   difficultyIncrease + "% due to superior defensive capabilities.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=8B4513>Balanced ranged encounter. Optimal defensive positioning achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight defensive advantage detected. " + npcName + "'s ranged intensity increased by " + 
                   difficultyIncrease + "% for tactical balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Ranged power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during ranged combat
     */
    private void monitorRangedScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getRangedScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get ranged scaling change message
     */
    private String getRangedScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Ranged Update: Defensive balance improved! Crossbow restraint reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=2E8B57>Ranged Update: Crossbow precision now active due to increased defensive power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Ranged Update: Damage resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=8B4513>Ranged Update: Defensive protection restored! Ranged damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent ranged phase tracking with BossBalancer integration
     */
    private void updateIntelligentRangedPhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentRangedPhase(npc);
        
        Integer lastPhase = currentRangedPhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentRangedPhase.put(npcKey, newPhase);
            handleIntelligentRangedPhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current ranged phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentRangedPhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= SHADOW_ARCHER_THRESHOLD) return 4;
            if (hpPercent <= ASSASSIN_THRESHOLD) return 3;
            if (hpPercent <= MARKSMAN_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ranged phase transitions with scaling integration
     */
    private void handleIntelligentRangedPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "The crossbow master";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("Precision targeting activated!"));
            npc.setNextGraphics(new Graphics(RAPID_FIRE_GFX));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED CROSSBOW MASTERY UNLEASHED!" : "Time for deadly precision!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(EXPLOSIVE_BLAST_GFX));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE SHADOW ARCHER TRANSFORMATION - MAXIMUM RANGED POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED SHADOW FORM!" : "Shadows guide my arrows!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(POISON_EFFECT_GFX));
            break;
        }
    }

    /**
     * NEW v5.0: Check for ranged safe spotting
     */
    private void checkRangedSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveAvoidedShots.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastShotHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect ranged-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean archerFrustrated = consecutiveCount > 3; // Archers need line of sight
        boolean recentAvoidance = !lastHit;
        
        boolean rangedSafeSpot = playerDistanced && archerFrustrated && recentAvoidance;
        
        if (rangedSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating ranged-themed responses
            if (warningCount >= 3) {
                performRangedAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=8B4513>The crossbow's aim steadies as you engage in direct combat...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform ranged anti-safe spot measure
     */
    private void performRangedAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=2E8B57>Crossbow mastery seeks those who hide from ranged combat!</col>");
        
        // Piercing shot that goes through obstacles
        npc.setNextAnimation(new Animation(EXPLOSIVE_SHOT_ANIM));
        npc.setNextForceTalk(new ForceTalk("COWARD! My bolts pierce all defenses!"));
        
        // Enhanced damage based on scaling
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.4) : 280; // Piercing pursuit
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareRangedDamageScaling(scaledDamage, player, "piercing_pursuit");
        
        delayHit(npc, 1, player, getRangeHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>RANGED PENALTY: Safe spotting detected - piercing shot ignores barriers!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced ranged taunts with scaling-based frequency
     */
    private void performEnhancedRangedTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on ranged phase and scaling
        int rangedPhase = getCurrentRangedPhase(npc);
        int tauntChance = 9 + (rangedPhase * 5); // Base 14% to 29% based on phase
        
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance += 13; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance += 9; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced ranged taunts based on phase and scaling
            performScaledRangedTaunt(npc, rangedPhase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled ranged taunt based on phase and scaling
     */
    private void performScaledRangedTaunt(NPC npc, int rangedPhase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "My crossbow never misses!",
            "Precision is my weapon!",
            "Your armor means nothing!",
            "Distance won't save you!",
            "Every bolt finds its mark!",
            "Feel the sting of my bolts!",
            "Ranged combat is superior!"
        };
        
        String[] rangedTaunts = {
            "CROSSBOW MASTERY UNLEASHED!",
            "PRECISION SHOOTING ACTIVATED!",
            "EVERY SHOT IS CALCULATED!",
            "WITNESS TRUE RANGED SUPERIORITY!",
            "MARKSMANSHIP AT ITS FINEST!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED CROSSBOW MASTERY ACTIVATED!",
            "YOUR SUPERIOR DEFENSES SHARPEN MY AIM!",
            "MAXIMUM PRECISION UNLEASHED!",
            "ULTIMATE ARCHER'S DOMINION!",
            "TRANSCENDENT RANGED MASTERY!"
        };
        
        String selectedTaunt;
        if (isHighScaling && rangedPhase >= 3) {
            // Use enhanced taunts for high scaling + high ranged power
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else if (rangedPhase >= 2) {
            // Use ranged taunts for high ranged phases
            selectedTaunt = rangedTaunts[Utils.random(rangedTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent ranged warning system
     */
    private int performIntelligentRangedAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int rangedPhase = getCurrentRangedPhase(npc);
            RangedAttackPattern pattern = selectIntelligentRangedAttackPattern(rangedPhase, scaling, currentCount);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentRangedWarning(scaling, currentCount)) {
                sendIntelligentRangedPreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledRangedAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentRangedAttackDelay(npc, rangedPhase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledRangedAttack(npc, player, pattern, scaling);
                return getIntelligentRangedAttackDelay(npc, rangedPhase, scaling);
            }
            
        } catch (Exception e) {
            return 5;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ranged warning probability based on scaling
     */
    private boolean shouldGiveIntelligentRangedWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing ranged attacks
        boolean isUndergeared = scaling.scalingType.contains("ASSISTANCE");
        int warningFrequency = isUndergeared ? 3 : 4; // Every 3rd vs 4th attack
        
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
     * ENHANCED v5.0: Intelligent ranged pre-attack warning with scaling context
     */
    private void sendIntelligentRangedPreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME ranged power due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense crossbow damage due to scaling!)";
        }
        
        player.sendMessage("<col=2E8B57>RANGED WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent ranged attack pattern selection with scaling consideration
     */
    private RangedAttackPattern selectIntelligentRangedAttackPattern(int rangedPhase, CombatScaling scaling, int attackCount) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on ranged phase, scaling, and progression
        int baseSpecialChance = (rangedPhase - 1) * 15; // 15% per ranged phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 12 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 8 ? 7 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for ranged attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        if (isOvergeared) {
            // More aggressive ranged patterns for overgeared players
            if (roll < 8 + specialChance) return RANGED_ATTACK_PATTERNS[4]; // Poison rain
            if (roll < 22 + specialChance) return RANGED_ATTACK_PATTERNS[3]; // Explosive shot  
            if (roll < 36 + specialChance) return RANGED_ATTACK_PATTERNS[2]; // Rapid fire
            if (roll < 48 + specialChance) return RANGED_ATTACK_PATTERNS[1]; // Agility drain
        } else {
            // Standard ranged pattern selection
            if (roll < 5 + specialChance) return RANGED_ATTACK_PATTERNS[4]; // Poison rain
            if (roll < 17 + specialChance) return RANGED_ATTACK_PATTERNS[3]; // Explosive shot  
            if (roll < 29 + specialChance) return RANGED_ATTACK_PATTERNS[2]; // Rapid fire
            if (roll < 39 + specialChance) return RANGED_ATTACK_PATTERNS[1]; // Agility drain
        }
        
        return RANGED_ATTACK_PATTERNS[0]; // Crossbow shot
    }

    /**
     * ENHANCED v5.0: Execute ranged attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledRangedAttack(NPC npc, Player player, RangedAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced ranged damage calculation with v5.0 intelligence
            int rangedPhase = getCurrentRangedPhase(npc);
            double rangedModifier = 1.0 + (rangedPhase - 1) * 0.18; // 18% per ranged phase (precision mastery)
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * rangedModifier);
            
            // Execute different ranged attack types with v5.0 scaling and HP-aware damage
            if ("poison_rain".equals(pattern.name)) {
                executeIntelligentPoisonRain(npc, player, baseDamage, scaling);
            } else if ("explosive_shot".equals(pattern.name)) {
                executeIntelligentExplosiveShot(npc, player, baseDamage, scaling);
            } else if ("rapid_fire".equals(pattern.name)) {
                executeIntelligentRapidFire(npc, player, baseDamage, scaling);
            } else if ("agility_drain".equals(pattern.name)) {
                executeIntelligentAgilityDrain(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleRangedAttack(npc, player, baseDamage, 0, scaling, "crossbow_shot");
            }
            
            // End graphics with enhanced timing
            if (pattern.endGraphics > 0) {
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        if (!npc.isDead() && !player.isDead()) {
                            player.setNextGraphics(new Graphics(pattern.endGraphics));
                        }
                        this.stop();
                    }
                }, 2);
            }
            
        } catch (Exception e) {
            // Enhanced fallback - execute basic ranged attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleRangedAttack(npc, player, scaledDamage, 0, scaling, "crossbow_shot");
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent poison rain attack with HP-aware scaling
     */
    private void executeIntelligentPoisonRain(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        npc.setNextAnimation(new Animation(POISON_RAIN_ANIM));
        player.sendMessage("Karils fires poison arrows into the sky!");
        
        WorldTasksManager.schedule(new WorldTask() {
            int ticks = 0;
            WorldTile rainCenter = player.getWorldTile();
            final int duration = scaling.bossDamageMultiplier > 2.0 ? 8 : 6; // Longer for extreme scaling
            
            @Override
            public void run() {
                if (ticks >= duration || npc.isDead()) {
                    player.sendMessage("The poison rain subsides.");
                    stop();
                    return;
                }
                
                // Create poison cloud effects in 3x3 area
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        WorldTile poisonTile = new WorldTile(
                            rainCenter.getX() + x,
                            rainCenter.getY() + y,
                            rainCenter.getPlane()
                        );
                        
                        // Add poison graphics
                        player.getPackets().sendGraphics(new Graphics(POISON_EFFECT_GFX), poisonTile);
                    }
                }
                
                // Deal damage if player is in poison area
                if (Utils.getDistance(rainCenter, player.getWorldTile()) <= 1) {
                    int poisonDamage = baseDamage / 3;
                    int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, poisonDamage);
                    
                    // CRITICAL: Apply HP-aware damage scaling for poison rain
                    int safeDamage = applyHPAwareRangedDamageScaling(scaledDamage, player, "poison_rain");
                    
                    if (ticks == 0) {
                        checkAndWarnLowHPForRanged(player, safeDamage);
                        player.sendMessage("You are caught in the poison rain! Move away quickly!");
                    }
                    
                    player.applyHit(new Hit(npc, safeDamage, Hit.HitLook.POISON_DAMAGE));
                    
                    // Apply poison effect
                    player.getPoison().makePoisoned(50); // Light poison
                } else if (ticks == 0) {
                    player.sendMessage("You avoid the poison rain by staying mobile!");
                }
                
                ticks++;
            }
        }, 2); // Start after 2 ticks
    }

    /**
     * ENHANCED v5.0: Intelligent explosive shot attack with HP-aware scaling
     */
    private void executeIntelligentExplosiveShot(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        npc.setNextAnimation(new Animation(EXPLOSIVE_SHOT_ANIM));
        player.sendMessage("Karils loads an explosive bolt!");
        
        // Send explosive projectile
        World.sendProjectile(npc, player, EXPLOSIVE_BOLT, 41, 16, 41, 35, 16, 0);
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Explosion effect at player location
                WorldTile explosionTile = player.getWorldTile();
                player.setNextGraphics(new Graphics(EXPLOSIVE_BLAST_GFX));
                
                // AOE damage around explosion
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        WorldTile checkTile = new WorldTile(
                            explosionTile.getX() + x,
                            explosionTile.getY() + y,
                            explosionTile.getPlane()
                        );
                        
                        // Add explosion graphics to each tile
                        player.getPackets().sendGraphics(new Graphics(EXPLOSIVE_BLAST_GFX), checkTile);
                    }
                }
                
                // Deal damage based on distance from explosion center
                int distance = Utils.getDistance(explosionTile, player.getWorldTile());
                double damageMultiplier = distance == 0 ? 1.5 : (distance == 1 ? 1.0 : 0.5);
                int damage = (int)(baseDamage * damageMultiplier);
                int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
                
                // CRITICAL: Apply HP-aware damage scaling for explosive shot
                int safeDamage = applyHPAwareRangedDamageScaling(scaledDamage, player, "explosive_shot");
                checkAndWarnLowHPForRanged(player, safeDamage);
                
                player.applyHit(new Hit(npc, safeDamage, Hit.HitLook.RANGE_DAMAGE));
                player.sendMessage("The explosive bolt blast catches you!");
                stop();
            }
        }, 3); // 3 tick delay for explosion
    }

    /**
     * ENHANCED v5.0: Intelligent rapid fire attack with HP-aware scaling
     */
    private void executeIntelligentRapidFire(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer rapidCount = rapidFireCount.get(playerKey);
        if (rapidCount == null) rapidCount = 0;
        
        // Limit rapid fires per fight
        if (rapidCount >= MAX_RAPID_FIRES_PER_FIGHT) {
            // Fall back to regular agility drain
            executeIntelligentAgilityDrain(npc, player, baseDamage, scaling);
            return;
        }
        
        npc.setNextAnimation(new Animation(RAPID_FIRE_ANIM));
        player.sendMessage("Karils unleashes a barrage of crossbow bolts!");
        
        // First shot - immediate
        int firstDamage = (int)(baseDamage * 0.6);
        int scaledFirst = BossBalancer.calculateNPCDamageToPlayer(npc, player, firstDamage);
        int safeFirst = applyHPAwareRangedDamageScaling(scaledFirst, player, "rapid_fire");
        
        delayHit(npc, 1, player, getRangeHit(npc, safeFirst));
        World.sendProjectile(npc, player, RAPID_BOLT, 41, 16, 30, 35, 16, 0);
        
        // Second shot - 1 tick delay
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                int secondDamage = (int)(baseDamage * 0.7);
                int scaledSecond = BossBalancer.calculateNPCDamageToPlayer(npc, player, secondDamage);
                int safeSecond = applyHPAwareRangedDamageScaling(scaledSecond, player, "rapid_fire");
                
                player.applyHit(new Hit(npc, safeSecond, Hit.HitLook.RANGE_DAMAGE));
                World.sendProjectile(npc, player, RAPID_BOLT, 41, 16, 30, 35, 16, 0);
                stop();
            }
        }, 1);
        
        // Third shot - 2 tick delay
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                int thirdDamage = (int)(baseDamage * 0.8);
                int scaledThird = BossBalancer.calculateNPCDamageToPlayer(npc, player, thirdDamage);
                int safeThird = applyHPAwareRangedDamageScaling(scaledThird, player, "rapid_fire");
                
                player.applyHit(new Hit(npc, safeThird, Hit.HitLook.RANGE_DAMAGE));
                World.sendProjectile(npc, player, RAPID_BOLT, 41, 16, 30, 35, 16, 0);
                player.sendMessage("The rapid fire barrage devastates you!");
                stop();
            }
        }, 2);
        
        // Update rapid fire counter
        rapidFireCount.put(playerKey, rapidCount + 1);
    }

    /**
     * ENHANCED v5.0: Intelligent agility drain attack with HP-aware scaling
     */
    private void executeIntelligentAgilityDrain(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        int damage = (int)(baseDamage * 1.2);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for agility drain
        int safeDamage = applyHPAwareRangedDamageScaling(scaledDamage, player, "agility_drain");
        checkAndWarnLowHPForRanged(player, safeDamage);
        
        npc.setNextAnimation(new Animation(NORMAL_ATTACK_ANIM));
        player.sendMessage("Karils' bolt saps your energy and mobility!");
        
        // Guaranteed agility drain on this attack
        drainAgility(player, AGILITY_DRAIN_PERCENT * 1.5); // Enhanced drain for special
        
        delayHit(npc, 2, player, getRangeHit(npc, safeDamage));
        World.sendProjectile(npc, player, NORMAL_BOLT, 41, 16, 41, 35, 16, 0);
    }

    /**
     * ENHANCED v5.0: Intelligent single ranged attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleRangedAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareRangedDamageScaling(scaledDamage, player, attackType);
        if (!"crossbow_shot".equals(attackType)) {
            checkAndWarnLowHPForRanged(player, safeDamage);
        }
        
        npc.setNextAnimation(new Animation(NORMAL_ATTACK_ANIM));
        delayHit(npc, delay, player, getRangeHit(npc, safeDamage));
        World.sendProjectile(npc, player, NORMAL_BOLT, 41, 16, 41, 35, 16, 0);
        
        // Apply agility drain chance
        if (safeDamage > 0 && Utils.random(AGILITY_DRAIN_CHANCE) == 0) {
            drainAgility(player, AGILITY_DRAIN_PERCENT);
            player.setNextGraphics(new Graphics(AGILITY_DRAIN_GFX));
            player.sendMessage("Karils' bolt drains your agility!");
        }
        
        // Track if attack hit
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastShotHit.put(playerKey, safeDamage > 0);
        
        // Update avoided shots counter
        Integer avoidedCount = consecutiveAvoidedShots.get(playerKey);
        if (avoidedCount == null) avoidedCount = 0;
        if (safeDamage <= 0) {
            consecutiveAvoidedShots.put(playerKey, avoidedCount + 1);
        } else {
            consecutiveAvoidedShots.put(playerKey, 0);
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ranged attack delay with scaling consideration
     */
    private int getIntelligentRangedAttackDelay(NPC npc, int rangedPhase, CombatScaling scaling) {
        int baseDelay = 5; // Standard ranged delay
        int rangedSpeedBonus = Math.max(0, rangedPhase - 1); // Ranged mastery makes attacks faster
        
        // v5.0 intelligent scaling can affect attack speed for ranged
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = 1; // Faster for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingSpeedBonus = 1; // Slightly faster for high scaling
        }
        
        return Math.max(4, baseDelay - rangedSpeedBonus - scalingSpeedBonus);
    }

    /**
     * Helper method to drain player's agility
     */
    private void drainAgility(Player player, double drainPercent) {
        player.setNextGraphics(new Graphics(AGILITY_DRAIN_GFX, 0, 100));
        
        int maxAgility = player.getSkills().getLevelForXp(Skills.AGILITY);
        int currentAgility = player.getSkills().getLevel(Skills.AGILITY);
        int drainAmount = (int)(maxAgility * drainPercent);
        
        int newLevel = Math.max(0, currentAgility - drainAmount);
        player.getSkills().set(Skills.AGILITY, newLevel);
        
        if (drainPercent > AGILITY_DRAIN_PERCENT) {
            player.sendMessage("Your agility has been severely drained!");
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

    /**
     * ENHANCED v5.0: Handle ranged combat end with proper cleanup
     */
    public static void onRangedCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentRangedPhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            rapidFireCount.remove(playerKey);
            consecutiveAvoidedShots.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastShotHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=8B4513>Ranged combat session ended. Crossbow scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("Karils: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during ranged combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Ranged scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("Karils: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onRangedCombatEnd(player, null);
        }
    }

    /**
     * Enhanced ranged attack pattern data structure
     */
    private static class RangedAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        RangedAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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