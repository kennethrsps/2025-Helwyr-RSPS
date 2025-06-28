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
 * Enhanced Guthans (Rise of the Six) Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Vampiric healing mechanics, life drain attacks, healing pools, HP-aware damage scaling for minigame combat
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 3.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System for ROTS
 */
public class Guthans extends CombatScript {

    // ===== VAMPIRIC PHASES - Enhanced for v5.0 =====
    private static final double WARRIOR_THRESHOLD = 0.80;   // 80% HP - normal warrior
    private static final double VAMPIRIC_THRESHOLD = 0.55;  // 55% HP - vampiric phase
    private static final double UNDEAD_THRESHOLD = 0.30;    // 30% HP - undead lord
    private static final double LICH_THRESHOLD = 0.12;      // 12% HP - undead lich

    // ===== ENHANCED GUIDANCE SYSTEM - Minigame-aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentVampiricPhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> healingAttemptCount = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 120000; // 2 minutes between warnings (shorter for minigame)
    private static final long SCALING_UPDATE_INTERVAL = 20000; // 20 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 2000; // 2 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 4; // More warnings for complex mechanics

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - MINIGAME BALANCED =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.32; // Max 32% of player HP per hit (vampiric is balanced)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.45;  // Max 45% for life drain attacks
    private static final double DRAIN_DAMAGE_PERCENT = 0.38;     // Max 38% for drain attacks
    private static final double LICH_DAMAGE_PERCENT = 0.50;      // Max 50% for lich transformation
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 480;          // Hard cap (32% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 28;               // Minimum damage to prevent 0 hits

    // ===== HEALING AND DRAIN MECHANICS =====
    private static final int HEALING_CHANCE = 4; // 1 in 4 chance for healing effects
    private static final int MAX_HEALS_PER_FIGHT = 12; // Prevent excessive healing
    private static final double LIFESTEAL_PERCENT = 0.35; // 35% lifesteal base

    // ===== VAMPIRIC ATTACK PATTERNS with v5.0 intelligence =====
    private static final VampiricAttackPattern[] VAMPIRIC_ATTACK_PATTERNS = {
        new VampiricAttackPattern(18222, 0, 0, "spear_thrust", false, ""),
        new VampiricAttackPattern(18236, 94, 0, "lifesteal_strike", true, "LIFESTEAL STRIKE incoming - vampiric spear drain!"),
        new VampiricAttackPattern(18236, 377, 0, "life_drain", true, "LIFE DRAIN incoming - vitality siphon!"),
        new VampiricAttackPattern(18300, 363, 0, "healing_pool", true, "HEALING POOL incoming - vampiric regeneration zone!"),
        new VampiricAttackPattern(18300, 170, 348, "undead_lich", true, "UNDEAD LICH incoming - ultimate vampiric transformation!")
    };

    // ===== SAFE SPOT PREVENTION - Vampiric-themed =====
    private static final Map<Integer, Integer> consecutiveAvoidedDrains = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastDrainHit = new ConcurrentHashMap<Integer, Boolean>();

    // ===== ANIMATION AND GRAPHICS CONSTANTS =====
    private static final int SPEAR_ATTACK_ANIM = 18222;
    private static final int LIFE_DRAIN_ANIM = 18236;
    private static final int HEALING_POOL_ANIM = 18300;
    private static final int LIFESTEAL_GFX = 94;
    private static final int LIFE_DRAIN_GFX = 377;
    private static final int HEALING_POOL_GFX = 363;
    private static final int UNDEAD_RESIST_GFX = 170;
    private static final int REGENERATION_GFX = 348;

    @Override
    public Object[] getKeys() {
        return new Object[] { 18541 }; // Rise of the Six Guthans
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeVampiricCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0 (special handling for minigames)
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentVampiricGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorVampiricScalingChanges(player, scaling);
        
        // Update vampiric phase tracking with v5.0 scaling
        updateIntelligentVampiricPhaseTracking(npc, scaling);
        
        // Check for vampiric-themed safe spotting
        checkVampiricSafeSpotting(player, npc, scaling);
        
        // Enhanced vampiric taunts with scaling-based frequency
        performEnhancedVampiricTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentVampiricAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize vampiric combat session using BossBalancer v5.0
     */
    private void initializeVampiricCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            healingAttemptCount.put(sessionKey, Integer.valueOf(0));
            consecutiveAvoidedDrains.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastDrainHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced vampiric combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentVampiricWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for vampiric combat
            performInitialVampiricArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial vampiric armor analysis
     */
    private void performInitialVampiricArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=8B0000>Vampiric Analysis: Exposed flesh detected. The spear hungers for blood!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=DC143C>Vampiric Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance). Life force still flows...</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from vampiric attacks
     */
    private int applyHPAwareVampiricDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (vampiric attacks are life-focused)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on vampiric attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "undead_lich":
                case "lich_transformation":
                    damagePercent = LICH_DAMAGE_PERCENT;
                    break;
                case "life_drain":
                case "massive_drain":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "vampiric_drain":
                case "healing_drain":
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
            
            // Additional safety check - never deal more than 82% of current HP for vampiric
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
     * NEW v5.0: Send HP warning if player is in danger from vampiric attacks
     */
    private void checkAndWarnLowHPForVampiric(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming vampiric damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.70) {
                    player.sendMessage("<col=ff0000>VAMPIRIC WARNING: This life drain will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.50) {
                    player.sendMessage("<col=8B0000>VAMPIRIC WARNING: Heavy vampiric damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent vampiric welcome message based on power analysis
     */
    private String getIntelligentVampiricWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Guthans";
        
        message.append("<col=8B0000>").append(npcName).append(" raises his vampiric spear, sensing your life force (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=DC143C>[Vampiric hunger: +").append(diffIncrease).append("% life drain power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Vampiric restraint: -").append(assistance).append("% life drain damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Vampiric resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full vampiric protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent vampiric guidance with power-based scaling awareness
     */
    private void provideIntelligentVampiricGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentVampiricGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent vampiric guidance message based on power analysis
     */
    private String getIntelligentVampiricGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentVampiricPhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getVampiricScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=DC143C>Vampiric Analysis: Missing armor exposes you to life drain! Vampiric damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME vampiric power due to scaling!)" : "";
                    return "<col=8B0000>Vampiric Analysis: Undead lord phase reached. Life drain dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=660000>Vampiric Analysis: Undead lich transformation! Maximum vampiric power unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=DC143C>Vampiric Analysis: Extreme vampiric scaling detected! Consider facing greater undead!</col>";
                }
                break;
                
            case 3:
                // Fourth warning: Advanced tactics
                return "<col=B22222>Vampiric Tactics: Watch for healing pools and life drains. Burst damage prevents healing!</col>";
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get vampiric scaling analysis message
     */
    private String getVampiricScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Guthans";
        
        String baseMessage = "<col=DDA0DD>Vampiric Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>" + npcName + "'s vampiric hunger restrained! Life drain reduced by " + 
                   assistancePercent + "% due to insufficient vital preparation.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=DC143C>" + npcName + "'s vampiric thirst escalated! Life drain increased by " + 
                   difficultyIncrease + "% due to superior vital defenses.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=8B0000>Balanced vampiric encounter. Optimal life force resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight vital advantage detected. " + npcName + "'s vampiric intensity increased by " + 
                   difficultyIncrease + "% for vital balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Vampiric power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during vampiric combat
     */
    private void monitorVampiricScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getVampiricScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get vampiric scaling change message
     */
    private String getVampiricScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Vampiric Update: Vital balance improved! Vampiric restraint reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=DC143C>Vampiric Update: Vampiric thirst now active due to increased vital power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Vampiric Update: Life resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=8B0000>Vampiric Update: Vital protection restored! Vampiric damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent vampiric phase tracking with BossBalancer integration
     */
    private void updateIntelligentVampiricPhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentVampiricPhase(npc);
        
        Integer lastPhase = currentVampiricPhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentVampiricPhase.put(npcKey, newPhase);
            handleIntelligentVampiricPhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current vampiric phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentVampiricPhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= LICH_THRESHOLD) return 4;
            if (hpPercent <= UNDEAD_THRESHOLD) return 3;
            if (hpPercent <= VAMPIRIC_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent vampiric phase transitions with scaling integration
     */
    private void handleIntelligentVampiricPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "The vampiric warrior";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("The blood calls to me!"));
            npc.setNextGraphics(new Graphics(LIFESTEAL_GFX));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED VAMPIRIC MASTERY UNLEASHED!" : "I embrace my undead nature!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(UNDEAD_RESIST_GFX));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE UNDEAD LICH TRANSFORMATION - MAXIMUM VAMPIRIC POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED LICH FORM!" : "Death is but a doorway to power!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(REGENERATION_GFX));
            break;
        }
    }

    /**
     * NEW v5.0: Check for vampiric safe spotting
     */
    private void checkVampiricSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveAvoidedDrains.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastDrainHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect vampiric-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean vampireFrustrated = consecutiveCount > 3; // Vampires need life force
        boolean recentAvoidance = !lastHit;
        
        boolean vampiricSafeSpot = playerDistanced && vampireFrustrated && recentAvoidance;
        
        if (vampiricSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating vampiric-themed responses
            if (warningCount >= 3) {
                performVampiricAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=8B0000>The vampiric hunger calms as you engage in direct combat...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform vampiric anti-safe spot measure
     */
    private void performVampiricAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=DC143C>Vampiric hunger seeks those who deny the life force!</col>");
        
        // Life drain that reaches through obstacles
        npc.setNextAnimation(new Animation(LIFE_DRAIN_ANIM));
        npc.setNextForceTalk(new ForceTalk("COWARD! Your life force calls to me!"));
        
        // Enhanced damage based on scaling
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.5) : 300; // Vampiric pursuit
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareVampiricDamageScaling(scaledDamage, player, "vampiric_pursuit");
        
        // Heal Guthans for the damage dealt
        int healAmount = (int)(safeDamage * LIFESTEAL_PERCENT);
        npc.heal(healAmount);
        
        delayHit(npc, 1, player, getMeleeHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>VAMPIRIC PENALTY: Safe spotting detected - life force drained through barriers!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced vampiric taunts with scaling-based frequency
     */
    private void performEnhancedVampiricTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on vampiric phase and scaling
        int vampiricPhase = getCurrentVampiricPhase(npc);
        int tauntChance = 9 + (vampiricPhase * 5); // Base 14% to 29% based on phase
        
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance += 13; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance += 9; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced vampiric taunts based on phase and scaling
            performScaledVampiricTaunt(npc, vampiricPhase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled vampiric taunt based on phase and scaling
     */
    private void performScaledVampiricTaunt(NPC npc, int vampiricPhase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "Your life force strengthens me!",
            "I hunger for vitality!",
            "Blood calls to blood!",
            "The spear thirsts!",
            "Life belongs to the undead!",
            "Your essence feeds my power!",
            "Death and rebirth eternal!"
        };
        
        String[] vampiricTaunts = {
            "VAMPIRIC POWER COURSES THROUGH ME!",
            "YOUR LIFE FORCE IS MINE TO CLAIM!",
            "THE UNDEAD HUNGER NEVER ENDS!",
            "WITNESS TRUE VAMPIRIC MASTERY!",
            "LIFE AND DEATH OBEY MY WILL!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED VAMPIRIC MASTERY ACTIVATED!",
            "YOUR SUPERIOR VITALITY FUELS MY HUNGER!",
            "MAXIMUM LIFE DRAIN UNLEASHED!",
            "ULTIMATE UNDEAD LORD'S DOMINION!",
            "TRANSCENDENT VAMPIRIC LICH POWER!"
        };
        
        String selectedTaunt;
        if (isHighScaling && vampiricPhase >= 3) {
            // Use enhanced taunts for high scaling + high vampiric power
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else if (vampiricPhase >= 2) {
            // Use vampiric taunts for high vampiric phases
            selectedTaunt = vampiricTaunts[Utils.random(vampiricTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent vampiric warning system
     */
    private int performIntelligentVampiricAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int vampiricPhase = getCurrentVampiricPhase(npc);
            VampiricAttackPattern pattern = selectIntelligentVampiricAttackPattern(vampiricPhase, scaling, currentCount);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentVampiricWarning(scaling, currentCount)) {
                sendIntelligentVampiricPreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledVampiricAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentVampiricAttackDelay(npc, vampiricPhase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledVampiricAttack(npc, player, pattern, scaling);
                return getIntelligentVampiricAttackDelay(npc, vampiricPhase, scaling);
            }
            
        } catch (Exception e) {
            return 5;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent vampiric warning probability based on scaling
     */
    private boolean shouldGiveIntelligentVampiricWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing vampiric attacks
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
     * ENHANCED v5.0: Intelligent vampiric pre-attack warning with scaling context
     */
    private void sendIntelligentVampiricPreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME vampiric power due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense life drain due to scaling!)";
        }
        
        player.sendMessage("<col=DC143C>VAMPIRIC WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent vampiric attack pattern selection with scaling consideration
     */
    private VampiricAttackPattern selectIntelligentVampiricAttackPattern(int vampiricPhase, CombatScaling scaling, int attackCount) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on vampiric phase, scaling, and progression
        int baseSpecialChance = (vampiricPhase - 1) * 15; // 15% per vampiric phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 12 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 8 ? 7 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for vampiric attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        if (isOvergeared) {
            // More aggressive vampiric patterns for overgeared players
            if (roll < 8 + specialChance) return VAMPIRIC_ATTACK_PATTERNS[4]; // Undead lich
            if (roll < 22 + specialChance) return VAMPIRIC_ATTACK_PATTERNS[3]; // Healing pool  
            if (roll < 36 + specialChance) return VAMPIRIC_ATTACK_PATTERNS[2]; // Life drain
            if (roll < 48 + specialChance) return VAMPIRIC_ATTACK_PATTERNS[1]; // Lifesteal strike
        } else {
            // Standard vampiric pattern selection
            if (roll < 5 + specialChance) return VAMPIRIC_ATTACK_PATTERNS[4]; // Undead lich
            if (roll < 17 + specialChance) return VAMPIRIC_ATTACK_PATTERNS[3]; // Healing pool  
            if (roll < 29 + specialChance) return VAMPIRIC_ATTACK_PATTERNS[2]; // Life drain
            if (roll < 39 + specialChance) return VAMPIRIC_ATTACK_PATTERNS[1]; // Lifesteal strike
        }
        
        return VAMPIRIC_ATTACK_PATTERNS[0]; // Spear thrust
    }

    /**
     * ENHANCED v5.0: Execute vampiric attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledVampiricAttack(NPC npc, Player player, VampiricAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced vampiric damage calculation with v5.0 intelligence
            int vampiricPhase = getCurrentVampiricPhase(npc);
            double vampiricModifier = 1.0 + (vampiricPhase - 1) * 0.18; // 18% per vampiric phase (life force mastery)
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * vampiricModifier);
            
            // Execute different vampiric attack types with v5.0 scaling and HP-aware damage
            if ("undead_lich".equals(pattern.name)) {
                executeIntelligentUndeadLich(npc, player, baseDamage, scaling);
            } else if ("healing_pool".equals(pattern.name)) {
                executeIntelligentHealingPool(npc, player, baseDamage, scaling);
            } else if ("life_drain".equals(pattern.name)) {
                executeIntelligentLifeDrain(npc, player, baseDamage, scaling);
            } else if ("lifesteal_strike".equals(pattern.name)) {
                executeIntelligentLifestealStrike(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleVampiricAttack(npc, player, baseDamage, 0, scaling, "spear_thrust");
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
            // Enhanced fallback - execute basic vampiric attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleVampiricAttack(npc, player, scaledDamage, 0, scaling, "spear_thrust");
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent undead lich attack with HP-aware scaling
     */
    private void executeIntelligentUndeadLich(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ultimate vampiric attack - 240% damage with massive life drain
        int damage = (int)(baseDamage * 2.4) + Utils.random(baseDamage / 3);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for ultimate attacks
        int safeDamage = applyHPAwareVampiricDamageScaling(scaledDamage, player, "undead_lich");
        checkAndWarnLowHPForVampiric(player, safeDamage);
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        // Massive vampiric healing
        int healAmount = (int)(safeDamage * LIFESTEAL_PERCENT * 2.0); // Double lifesteal
        npc.heal(healAmount);
        npc.setNextGraphics(new Graphics(REGENERATION_GFX));
        
        player.sendMessage("The undead lich drains massive life force!");
    }

    /**
     * ENHANCED v5.0: Intelligent healing pool attack with HP-aware scaling
     */
    private void executeIntelligentHealingPool(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer healCount = healingAttemptCount.get(playerKey);
        if (healCount == null) healCount = 0;
        
        // Limit healing pools per fight
        if (healCount >= MAX_HEALS_PER_FIGHT) {
            // Fall back to regular lifesteal strike
            executeIntelligentLifestealStrike(npc, player, baseDamage, scaling);
            return;
        }
        
        // Make variable effectively final for lambda
        final int currentHealCount = healCount;
        
        player.sendMessage("Guthans creates a vampiric healing pool!");
        npc.setNextGraphics(new Graphics(HEALING_POOL_GFX));
        
        // Healing pool damage (140% base for pool creation)
        int damage = (int)(baseDamage * 1.4) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for healing pool
        int safeDamage = applyHPAwareVampiricDamageScaling(scaledDamage, player, "healing_pool");
        checkAndWarnLowHPForVampiric(player, safeDamage);
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        // Start healing pool effect
        WorldTasksManager.schedule(new WorldTask() {
            int ticks = 0;
            final int duration = scaling.bossDamageMultiplier > 2.0 ? 8 : 6; // Longer for extreme scaling
            
            @Override
            public void run() {
                if (ticks >= duration || npc.isDead()) {
                    player.sendMessage("The vampiric healing pool dissipates.");
                    stop();
                    return;
                }
                
                // Visual effect every 2 ticks
                if (ticks % 2 == 0) {
                    npc.setNextGraphics(new Graphics(HEALING_POOL_GFX));
                }
                
                // Heal based on scaling
                int healAmount = (int)(npc.getMaxHitpoints() * 0.03 * scaling.bossDamageMultiplier); // 3% max HP per tick, scaled
                npc.heal(healAmount);
                
                ticks++;
            }
        }, 1);
        
        // Update healing counter
        healingAttemptCount.put(playerKey, currentHealCount + 1);
    }

    /**
     * ENHANCED v5.0: Intelligent life drain attack with HP-aware scaling
     */
    private void executeIntelligentLifeDrain(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        player.sendMessage("Guthans channels dark magic to drain your life!");
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Drain effect
                player.setNextGraphics(new Graphics(LIFE_DRAIN_GFX));
                
                // Life drain damage (180% base for enhanced drain)
                int damage = (int)(baseDamage * 1.8) + Utils.random(baseDamage / 3);
                int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
                
                // CRITICAL: Apply HP-aware damage scaling for life drain
                int safeDamage = applyHPAwareVampiricDamageScaling(scaledDamage, player, "life_drain");
                checkAndWarnLowHPForVampiric(player, safeDamage);
                
                // Damage player and heal Guthans
                player.applyHit(new Hit(npc, safeDamage, Hit.HitLook.MAGIC_DAMAGE));
                
                // Enhanced lifesteal based on scaling
                double lifestealMultiplier = scaling.bossDamageMultiplier > 1.5 ? 1.4 : 1.0;
                int healAmount = (int)(safeDamage * LIFESTEAL_PERCENT * lifestealMultiplier);
                npc.heal(healAmount);
                
                player.sendMessage("Your life force is drained to empower Guthans!");
                stop();
            }
        }, 2);
    }

    /**
     * ENHANCED v5.0: Intelligent lifesteal strike attack with HP-aware scaling
     */
    private void executeIntelligentLifestealStrike(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Lifesteal strike damage (160% base for vampiric enhancement)
        int damage = (int)(baseDamage * 1.6) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for lifesteal strikes
        int safeDamage = applyHPAwareVampiricDamageScaling(scaledDamage, player, "lifesteal_strike");
        checkAndWarnLowHPForVampiric(player, safeDamage);
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        // Guaranteed enhanced lifesteal
        if (safeDamage > 0) {
            double lifestealMultiplier = scaling.bossDamageMultiplier > 1.5 ? 1.5 : 1.2;
            int healAmount = (int)(safeDamage * LIFESTEAL_PERCENT * lifestealMultiplier);
            npc.heal(healAmount);
            player.setNextGraphics(new Graphics(LIFESTEAL_GFX));
            player.sendMessage("Guthans' enhanced spear drains significant life force!");
        }
    }

    /**
     * ENHANCED v5.0: Intelligent single vampiric attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleVampiricAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareVampiricDamageScaling(scaledDamage, player, attackType);
        if (!"spear_thrust".equals(attackType)) {
            checkAndWarnLowHPForVampiric(player, safeDamage);
        }
        
        delayHit(npc, delay, player, getMeleeHit(npc, safeDamage));
        
        // Apply lifesteal chance
        if (safeDamage > 0 && Utils.random(HEALING_CHANCE) == 0) {
            int healAmount = (int)(safeDamage * LIFESTEAL_PERCENT);
            npc.heal(healAmount);
            player.setNextGraphics(new Graphics(LIFESTEAL_GFX));
            player.sendMessage("Guthans' spear drains your life force!");
        }
        
        // Track if attack hit
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastDrainHit.put(playerKey, safeDamage > 0);
        
        // Update avoided drains counter
        Integer avoidedCount = consecutiveAvoidedDrains.get(playerKey);
        if (avoidedCount == null) avoidedCount = 0;
        if (safeDamage <= 0) {
            consecutiveAvoidedDrains.put(playerKey, avoidedCount + 1);
        } else {
            consecutiveAvoidedDrains.put(playerKey, 0);
        }
    }

    /**
     * ENHANCED v5.0: Intelligent vampiric attack delay with scaling consideration
     */
    private int getIntelligentVampiricAttackDelay(NPC npc, int vampiricPhase, CombatScaling scaling) {
        int baseDelay = 5; // Standard vampiric delay
        int vampiricSpeedBonus = Math.max(0, vampiricPhase - 1); // Vampiric mastery makes attacks faster
        
        // v5.0 intelligent scaling can affect attack speed for vampiric
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = 1; // Faster for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingSpeedBonus = 1; // Slightly faster for high scaling
        }
        
        return Math.max(4, baseDelay - vampiricSpeedBonus - scalingSpeedBonus);
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
     * ENHANCED v5.0: Handle vampiric combat end with proper cleanup
     */
    public static void onVampiricCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentVampiricPhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            healingAttemptCount.remove(playerKey);
            consecutiveAvoidedDrains.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastDrainHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=8B0000>Vampiric combat session ended. Life drain scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("Guthans: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during vampiric combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Vampiric scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("Guthans: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onVampiricCombatEnd(player, null);
        }
    }

    /**
     * Enhanced vampiric attack pattern data structure
     */
    private static class VampiricAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        VampiricAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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