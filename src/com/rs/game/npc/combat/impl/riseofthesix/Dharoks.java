package com.rs.game.npc.combat.impl.riseofthesix;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
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
 * Enhanced Dharoks (Rise of the Six) Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Berserker rage scaling, ground slam attacks, multi-hit combos, HP-aware damage scaling for minigame combat
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 3.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System for ROTS
 */
public class Dharoks extends CombatScript {

    // ===== BERSERKER RAGE PHASES - Enhanced for v5.0 =====
    private static final double WOUNDED_THRESHOLD = 0.75;   // 75% HP - wounded berserker
    private static final double RAGE_THRESHOLD = 0.50;      // 50% HP - berserker rage
    private static final double FURY_THRESHOLD = 0.25;      // 25% HP - berserker fury
    private static final double DESPERATE_THRESHOLD = 0.10; // 10% HP - desperate last stand

    // ===== ENHANCED GUIDANCE SYSTEM - Minigame-aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentRagePhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> comboAttemptCount = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 120000; // 2 minutes between warnings (shorter for minigame)
    private static final long SCALING_UPDATE_INTERVAL = 20000; // 20 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 2500; // 2.5 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 4; // More warnings for complex mechanics

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - MINIGAME BALANCED =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.35; // Max 35% of player HP per hit (berserker is brutal)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.50;  // Max 50% for fury attacks
    private static final double COMBO_DAMAGE_PERCENT = 0.42;     // Max 42% for combo hits
    private static final double DESPERATE_DAMAGE_PERCENT = 0.55; // Max 55% for desperate attacks
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 525;          // Hard cap (35% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 32;               // Minimum damage to prevent 0 hits

    // ===== RAGE AND COMBO MECHANICS =====
    private static final int COMBO_CHANCE = 4; // 1 in 4 chance for combo effects
    private static final int MAX_COMBOS_PER_FIGHT = 6; // Prevent excessive comboing
    private static final double RAGE_DAMAGE_MULTIPLIER = 1.5; // Base rage multiplier

    // ===== BERSERKER ATTACK PATTERNS with v5.0 intelligence =====
    private static final BerserkerAttackPattern[] BERSERKER_ATTACK_PATTERNS = {
        new BerserkerAttackPattern(18236, 0, 0, "axe_strike", false, ""),
        new BerserkerAttackPattern(18238, 2517, 0, "ground_slam", true, "GROUND SLAM incoming - berserker earth shatter!"),
        new BerserkerAttackPattern(18237, 2516, 0, "rage_strike", true, "RAGE STRIKE incoming - berserker fury unleashed!"),
        new BerserkerAttackPattern(18237, 2516, 0, "berserker_combo", true, "BERSERKER COMBO incoming - devastating axe flurry!"),
        new BerserkerAttackPattern(18237, 2516, 2516, "desperate_assault", true, "DESPERATE ASSAULT incoming - ultimate berserker power!")
    };

    // ===== SAFE SPOT PREVENTION - Berserker-themed =====
    private static final Map<Integer, Integer> consecutiveAvoidedStrikes = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastStrikeHit = new ConcurrentHashMap<Integer, Boolean>();

    // ===== ANIMATION AND GRAPHICS CONSTANTS =====
    private static final int NORMAL_ATTACK_ANIM = 18236;
    private static final int RAGE_ATTACK_ANIM = 18237;
    private static final int GROUND_SLAM_ANIM = 18238;
    private static final int RAGE_GRAPHICS = 2516;
    private static final int GROUND_SLAM_GRAPHICS = 2517;

    @Override
    public Object[] getKeys() {
        return new Object[] { 18540 }; // Rise of the Six Dharoks
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeBerserkerCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0 (special handling for minigames)
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentBerserkerGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorBerserkerScalingChanges(player, scaling);
        
        // Update berserker rage phase tracking with v5.0 scaling
        updateIntelligentBerserkerPhaseTracking(npc, scaling);
        
        // Check for berserker-themed safe spotting
        checkBerserkerSafeSpotting(player, npc, scaling);
        
        // Enhanced berserker taunts with scaling-based frequency
        performEnhancedBerserkerTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentBerserkerAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize berserker combat session using BossBalancer v5.0
     */
    private void initializeBerserkerCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            comboAttemptCount.put(sessionKey, Integer.valueOf(0));
            consecutiveAvoidedStrikes.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastStrikeHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced berserker combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentBerserkerWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for berserker combat
            performInitialBerserkerArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial berserker armor analysis
     */
    private void performInitialBerserkerArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=8B0000>Berserker Analysis: Exposed flesh detected. My axe thirsts for blood!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=FF0000>Berserker Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance). The rage grows...</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from berserker attacks
     */
    private int applyHPAwareBerserkerDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (berserkers are dangerous when wounded)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on berserker attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "desperate_assault":
                case "last_stand":
                    damagePercent = DESPERATE_DAMAGE_PERCENT;
                    break;
                case "berserker_fury":
                case "rage_strike":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "combo_hit":
                case "berserker_combo":
                    damagePercent = COMBO_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 85% of current HP for berserker
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
     * NEW v5.0: Send HP warning if player is in danger from berserker attacks
     */
    private void checkAndWarnLowHPForBerserker(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming berserker damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.72) {
                    player.sendMessage("<col=ff0000>BERSERKER WARNING: This rage attack will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.52) {
                    player.sendMessage("<col=8B0000>BERSERKER WARNING: Heavy berserker damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent berserker welcome message based on power analysis
     */
    private String getIntelligentBerserkerWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Dharoks";
        
        message.append("<col=8B0000>").append(npcName).append(" awakens with rage, analyzing your combat prowess (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=FF0000>[Berserker fury: +").append(diffIncrease).append("% rage power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Restrained rage: -").append(assistance).append("% berserker damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Rage resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full berserker protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent berserker guidance with power-based scaling awareness
     */
    private void provideIntelligentBerserkerGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentBerserkerGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent berserker guidance message based on power analysis
     */
    private String getIntelligentBerserkerGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentBerserkerPhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getBerserkerScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=FF0000>Berserker Analysis: Missing armor exposes you to axe strikes! Berserker damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME rage due to scaling!)" : "";
                    return "<col=8B0000>Berserker Analysis: Berserker fury phase reached. Damage dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=660000>Berserker Analysis: Desperate last stand! Maximum berserker fury unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=FF0000>Berserker Analysis: Extreme rage scaling detected! Consider fighting stronger warriors!</col>";
                }
                break;
                
            case 3:
                // Fourth warning: Advanced tactics
                return "<col=DC143C>Berserker Tactics: Watch for ground slams and combos. Stay mobile and keep distance!</col>";
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get berserker scaling analysis message
     */
    private String getBerserkerScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Dharoks";
        
        String baseMessage = "<col=DDA0DD>Berserker Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>" + npcName + "'s rage restrained! Berserker damage reduced by " + 
                   assistancePercent + "% due to insufficient combat prowess.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF0000>" + npcName + "'s fury escalated! Berserker power increased by " + 
                   difficultyIncrease + "% due to superior gear advantage.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=8B0000>Balanced berserker encounter. Optimal rage resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight gear advantage detected. " + npcName + "'s rage intensity increased by " + 
                   difficultyIncrease + "% for balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Berserker power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during berserker combat
     */
    private void monitorBerserkerScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getBerserkerScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get berserker scaling change message
     */
    private String getBerserkerScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Berserker Update: Combat prowess improved to balanced! Rage restraint reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=FF0000>Berserker Update: Rage escalation now active due to increased power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Berserker Update: Rage resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=8B0000>Berserker Update: Full protection restored! Berserker damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent berserker phase tracking with BossBalancer integration
     */
    private void updateIntelligentBerserkerPhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentBerserkerPhase(npc);
        
        Integer lastPhase = currentRagePhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentRagePhase.put(npcKey, newPhase);
            handleIntelligentBerserkerPhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current berserker phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentBerserkerPhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= DESPERATE_THRESHOLD) return 4;
            if (hpPercent <= FURY_THRESHOLD) return 3;
            if (hpPercent <= RAGE_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent berserker phase transitions with scaling integration
     */
    private void handleIntelligentBerserkerPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "The berserker";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("My wounds only fuel my rage!"));
            npc.setNextGraphics(new Graphics(RAGE_GRAPHICS));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED BERSERKER FURY UNLEASHED!" : "Feel my growing wrath!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(RAGE_GRAPHICS));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE DESPERATE ASSAULT - MAXIMUM BERSERKER POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED LAST STAND!" : "I will not fall! Last stand!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(RAGE_GRAPHICS));
            break;
        }
    }

    /**
     * NEW v5.0: Check for berserker safe spotting
     */
    private void checkBerserkerSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveAvoidedStrikes.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastStrikeHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect berserker-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean berserkerFrustrated = consecutiveCount > 2; // Berserkers get frustrated quickly
        boolean recentAvoidance = !lastHit;
        
        boolean berserkerSafeSpot = playerDistanced && berserkerFrustrated && recentAvoidance;
        
        if (berserkerSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating berserker-themed responses
            if (warningCount >= 3) {
                performBerserkerAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=8B0000>The berserker's rage calms as you fight with honor...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform berserker anti-safe spot measure
     */
    private void performBerserkerAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=FF0000>The berserker's rage finds cowards hiding from honorable combat!</col>");
        
        // Berserker charge that breaks through obstacles
        npc.setNextAnimation(new Animation(RAGE_ATTACK_ANIM));
        npc.setNextForceTalk(new ForceTalk("COWARD! FACE ME!"));
        
        // Enhanced damage based on scaling
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.8) : 400; // Berserker charge
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareBerserkerDamageScaling(scaledDamage, player, "berserker_charge");
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>BERSERKER PENALTY: Safe spotting detected - rage-fueled charge breaks through!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced berserker taunts with scaling-based frequency
     */
    private void performEnhancedBerserkerTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on rage phase and scaling
        int ragePhase = getCurrentBerserkerPhase(npc);
        int tauntChance = 10 + (ragePhase * 5); // Base 15% to 35% based on rage
        
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance += 15; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance += 10; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced berserker taunts based on phase and scaling
            performScaledBerserkerTaunt(npc, ragePhase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled berserker taunt based on phase and scaling
     */
    private void performScaledBerserkerTaunt(NPC npc, int ragePhase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "Face me in combat!",
            "Your death approaches!",
            "I will crush you!",
            "Feel my wrath!",
            "Blood for blood!",
            "This axe thirsts for battle!",
            "You cannot escape!"
        };
        
        String[] rageTaunts = {
            "MY RAGE KNOWS NO BOUNDS!",
            "DEATH TO ALL WHO OPPOSE ME!",
            "I FIGHT UNTIL MY LAST BREATH!",
            "WITNESS TRUE BERSERKER FURY!",
            "PAIN ONLY MAKES ME STRONGER!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED BERSERKER POWER UNLEASHED!",
            "YOUR SUPERIOR GEAR FEEDS MY RAGE!",
            "MAXIMUM BERSERKER FURY ACTIVATED!",
            "ULTIMATE WARRIOR'S WRATH!",
            "TRANSCENDENT BERSERKER RAGE!"
        };
        
        String selectedTaunt;
        if (isHighScaling && ragePhase >= 3) {
            // Use enhanced taunts for high scaling + high rage
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else if (ragePhase >= 2) {
            // Use rage taunts for high rage phases
            selectedTaunt = rageTaunts[Utils.random(rageTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent berserker warning system
     */
    private int performIntelligentBerserkerAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int ragePhase = getCurrentBerserkerPhase(npc);
            BerserkerAttackPattern pattern = selectIntelligentBerserkerAttackPattern(ragePhase, scaling, currentCount);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentBerserkerWarning(scaling, currentCount)) {
                sendIntelligentBerserkerPreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledBerserkerAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentBerserkerAttackDelay(npc, ragePhase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledBerserkerAttack(npc, player, pattern, scaling);
                return getIntelligentBerserkerAttackDelay(npc, ragePhase, scaling);
            }
            
        } catch (Exception e) {
            return 4;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent berserker warning probability based on scaling
     */
    private boolean shouldGiveIntelligentBerserkerWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing berserker
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
     * ENHANCED v5.0: Intelligent berserker pre-attack warning with scaling context
     */
    private void sendIntelligentBerserkerPreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME berserker fury due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense rage due to scaling!)";
        }
        
        player.sendMessage("<col=FF0000>BERSERKER WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent berserker attack pattern selection with scaling consideration
     */
    private BerserkerAttackPattern selectIntelligentBerserkerAttackPattern(int ragePhase, CombatScaling scaling, int attackCount) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on rage phase, scaling, and progression
        int baseSpecialChance = (ragePhase - 1) * 18; // 18% per rage phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 15 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 6 ? 8 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for berserker attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        if (isOvergeared) {
            // More aggressive berserker patterns for overgeared players
            if (roll < 12 + specialChance) return BERSERKER_ATTACK_PATTERNS[4]; // Desperate assault
            if (roll < 26 + specialChance) return BERSERKER_ATTACK_PATTERNS[3]; // Berserker combo  
            if (roll < 42 + specialChance) return BERSERKER_ATTACK_PATTERNS[2]; // Rage strike
            if (roll < 56 + specialChance) return BERSERKER_ATTACK_PATTERNS[1]; // Ground slam
        } else {
            // Standard berserker pattern selection
            if (roll < 8 + specialChance) return BERSERKER_ATTACK_PATTERNS[4]; // Desperate assault
            if (roll < 20 + specialChance) return BERSERKER_ATTACK_PATTERNS[3]; // Berserker combo  
            if (roll < 34 + specialChance) return BERSERKER_ATTACK_PATTERNS[2]; // Rage strike
            if (roll < 46 + specialChance) return BERSERKER_ATTACK_PATTERNS[1]; // Ground slam
        }
        
        return BERSERKER_ATTACK_PATTERNS[0]; // Axe strike
    }

    /**
     * ENHANCED v5.0: Execute berserker attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledBerserkerAttack(NPC npc, Player player, BerserkerAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced berserker damage calculation with v5.0 intelligence
            int ragePhase = getCurrentBerserkerPhase(npc);
            double berserkerModifier = calculateBerserkerRageModifier(npc, ragePhase);
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * berserkerModifier);
            
            // Execute different berserker attack types with v5.0 scaling and HP-aware damage
            if ("desperate_assault".equals(pattern.name)) {
                executeIntelligentDesperateAssault(npc, player, baseDamage, scaling);
            } else if ("berserker_combo".equals(pattern.name)) {
                executeIntelligentBerserkerCombo(npc, player, baseDamage, scaling);
            } else if ("rage_strike".equals(pattern.name)) {
                executeIntelligentRageStrike(npc, player, baseDamage, scaling);
            } else if ("ground_slam".equals(pattern.name)) {
                executeIntelligentGroundSlam(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleBerserkerAttack(npc, player, baseDamage, 0, scaling, "axe_strike");
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
                }, 1);
            }
            
        } catch (Exception e) {
            // Enhanced fallback - execute basic berserker attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleBerserkerAttack(npc, player, scaledDamage, 0, scaling, "axe_strike");
            }
        }
    }

    /**
     * Calculate berserker rage modifier based on missing HP
     */
    private double calculateBerserkerRageModifier(NPC npc, int ragePhase) {
        try {
            int currentHp = npc.getHitpoints();
            int maxHp = npc.getMaxHitpoints();
            
            // Classic Dharok's formula: more missing HP = more damage
            double hpPercent = (double) currentHp / maxHp;
            double hpLostPercent = 1.0 - hpPercent;
            
            // Base modifier increases with missing HP
            double baseModifier = 1.0 + (hpLostPercent * RAGE_DAMAGE_MULTIPLIER);
            
            // Phase bonus
            double phaseBonus = 1.0 + ((ragePhase - 1) * 0.15); // 15% per rage phase
            
            return baseModifier * phaseBonus;
            
        } catch (Exception e) {
            return 1.0;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent desperate assault attack with HP-aware scaling
     */
    private void executeIntelligentDesperateAssault(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ultimate berserker attack - triple strike (250% total damage)
        for (int i = 0; i < 3; i++) {
            int hitDamage = (int)(baseDamage * (0.75 + (i * 0.1))) + Utils.random(baseDamage / 4);
            int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, hitDamage);
            
            // CRITICAL: Apply HP-aware damage scaling for ultimate attacks
            int safeDamage = applyHPAwareBerserkerDamageScaling(scaledDamage, player, "desperate_assault");
            if (i == 0) checkAndWarnLowHPForBerserker(player, safeDamage * 3); // Warn for total damage
            
            // Delay hits for combo effect
            delayHit(npc, i, player, getMeleeHit(npc, safeDamage));
        }
        
        // Update attack counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer strikeCount = attackCounter.get(playerKey);
        if (strikeCount == null) strikeCount = 0;
        attackCounter.put(playerKey, strikeCount + 3);
    }

    /**
     * ENHANCED v5.0: Intelligent berserker combo attack with HP-aware scaling
     */
    private void executeIntelligentBerserkerCombo(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer comboCount = comboAttemptCount.get(playerKey);
        if (comboCount == null) comboCount = 0;
        
        // Limit combos per fight
        if (comboCount >= MAX_COMBOS_PER_FIGHT) {
            // Fall back to regular rage strike
            executeIntelligentRageStrike(npc, player, baseDamage, scaling);
            return;
        }
        
        // Make variable effectively final for lambda
        final int currentComboCount = comboCount;
        
        player.sendMessage("Dharoks unleashes a berserker combo!");
        
        // First hit - 80% damage
        int damage1 = (int)(baseDamage * 0.80) + Utils.random(baseDamage / 6);
        int scaledDamage1 = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage1);
        
        // CRITICAL: Apply HP-aware damage scaling for combo hits
        int safeDamage1 = applyHPAwareBerserkerDamageScaling(scaledDamage1, player, "combo_hit");
        
        // Second hit - 100% damage with delay
        int damage2 = baseDamage + Utils.random(baseDamage / 5);
        int scaledDamage2 = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage2);
        int safeDamage2 = applyHPAwareBerserkerDamageScaling(scaledDamage2, player, "combo_hit");
        
        checkAndWarnLowHPForBerserker(player, safeDamage1 + safeDamage2);
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage1));
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                player.applyHit(new Hit(npc, safeDamage2, Hit.HitLook.MELEE_DAMAGE));
                
                // Update combo counter
                comboAttemptCount.put(playerKey, currentComboCount + 1);
                
                stop();
            }
        }, 1);
    }

    /**
     * ENHANCED v5.0: Intelligent rage strike attack with HP-aware scaling
     */
    private void executeIntelligentRageStrike(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Rage strike damage (180% base for enhanced berserker fury)
        int damage = (int)(baseDamage * 1.8) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for rage strikes
        int safeDamage = applyHPAwareBerserkerDamageScaling(scaledDamage, player, "rage_strike");
        checkAndWarnLowHPForBerserker(player, safeDamage);
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
    }

    /**
     * ENHANCED v5.0: Intelligent ground slam attack with HP-aware scaling
     */
    private void executeIntelligentGroundSlam(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        player.sendMessage("Dharoks raises his axe high, preparing a devastating ground slam!");
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Create ground slam effect
                WorldTile playerTile = player.getWorldTile();
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        WorldTile effectTile = new WorldTile(
                            playerTile.getX() + x, 
                            playerTile.getY() + y, 
                            playerTile.getPlane()
                        );
                        // Add ground crack graphics
                        player.getPackets().sendGraphics(new Graphics(GROUND_SLAM_GRAPHICS), effectTile);
                    }
                }
                
                // Ground slam damage (160% base for area attack)
                int damage = (int)(baseDamage * 1.6) + Utils.random(baseDamage / 3);
                int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
                
                // CRITICAL: Apply HP-aware damage scaling for ground slam
                int safeDamage = applyHPAwareBerserkerDamageScaling(scaledDamage, player, "ground_slam");
                checkAndWarnLowHPForBerserker(player, safeDamage);
                
                // Deal damage if player is in area
                if (Utils.getDistance(npc.getWorldTile(), player.getWorldTile()) <= 2) {
                    player.applyHit(new Hit(npc, safeDamage, Hit.HitLook.MELEE_DAMAGE));
                    player.sendMessage("The ground slam catches you! Massive damage!");
                } else {
                    player.sendMessage("You avoid the ground slam by staying mobile!");
                }
                stop();
            }
        }, 2);
    }

    /**
     * ENHANCED v5.0: Intelligent single berserker attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleBerserkerAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareBerserkerDamageScaling(scaledDamage, player, attackType);
        if (!"axe_strike".equals(attackType)) {
            checkAndWarnLowHPForBerserker(player, safeDamage);
        }
        
        delayHit(npc, delay, player, getMeleeHit(npc, safeDamage));
        
        // Track if attack hit
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastStrikeHit.put(playerKey, safeDamage > 0);
        
        // Update avoided strikes counter
        Integer avoidedCount = consecutiveAvoidedStrikes.get(playerKey);
        if (avoidedCount == null) avoidedCount = 0;
        if (safeDamage <= 0) {
            consecutiveAvoidedStrikes.put(playerKey, avoidedCount + 1);
        } else {
            consecutiveAvoidedStrikes.put(playerKey, 0);
        }
    }

    /**
     * ENHANCED v5.0: Intelligent berserker attack delay with scaling consideration
     */
    private int getIntelligentBerserkerAttackDelay(NPC npc, int ragePhase, CombatScaling scaling) {
        int baseDelay = 5; // Standard berserker delay
        int rageSpeedBonus = Math.max(0, ragePhase - 1); // Rage makes berserkers faster
        
        // v5.0 intelligent scaling can affect attack speed for berserker
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = 2; // Much faster for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingSpeedBonus = 1; // Faster for high scaling
        }
        
        return Math.max(3, baseDelay - rageSpeedBonus - scalingSpeedBonus);
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
     * ENHANCED v5.0: Handle berserker combat end with proper cleanup
     */
    public static void onBerserkerCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentRagePhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            comboAttemptCount.remove(playerKey);
            consecutiveAvoidedStrikes.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastStrikeHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=8B0000>Berserker combat session ended. Rage scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("Dharoks: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during berserker combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Berserker scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("Dharoks: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onBerserkerCombatEnd(player, null);
        }
    }

    /**
     * Enhanced berserker attack pattern data structure
     */
    private static class BerserkerAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        BerserkerAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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