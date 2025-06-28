package com.rs.game.npc.combat.impl;

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
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.player.content.Combat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.cache.loaders.NPCDefinitions;

/**
 * Enhanced Verak Lith Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Dynamic damage scaling, draconic phases, stat-draining mechanics, HP-aware damage caps
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 4.0 - FULL BossBalancer v5.0 Integration with Intelligent Draconic Combat Scaling & HP-Aware System
 */
public class VerakLithCombat extends CombatScript {

    // ===== DRACONIC PHASES - Enhanced for v5.0 =====
    private static final double HATCHLING_THRESHOLD = 0.80;      // 80% HP - young wyrm
    private static final double MATURE_THRESHOLD = 0.60;         // 60% HP - mature dragon  
    private static final double ANCIENT_THRESHOLD = 0.35;        // 35% HP - ancient wyrm
    private static final double ELDER_THRESHOLD = 0.15;          // 15% HP - elder destroyer

    // ===== ENHANCED GUIDANCE SYSTEM - Draconic-aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentDraconicPhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> mechanicWarningCount = new ConcurrentHashMap<Integer, Integer>();

    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 100000; // 1.67 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 20000; // 20 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 2000; // 2 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 5; // More warnings for complex mechanics

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - DRACONIC BALANCED =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.32; // Max 32% of player HP per hit (draconic)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.48;  // Max 48% for ancient breath
    private static final double DRAGONFIRE_DAMAGE_PERCENT = 0.40; // Max 40% for dragonfire
    private static final double ELDER_DAMAGE_PERCENT = 0.50;     // Max 50% for elder techniques
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 480;          // Hard cap (32% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 25;               // Minimum damage to prevent 0 hits

    // ===== DRACONIC MECHANICS AND EFFECTS =====
    private static final int GUIDANCE_CHANCE = 10; // 1 in 10 chance for enhanced guidance
    private static final int MAX_MECHANIC_WARNINGS_PER_FIGHT = 12; // Prevent spam
    
    // ===== SAFE SPOT PREVENTION - Draconic-themed =====
    private static final Map<Integer, Integer> consecutiveAvoidedAttacks = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastAttackHit = new ConcurrentHashMap<Integer, Boolean>();

    // Legacy compatibility fields
    private static final long GUIDANCE_COOLDOWN = 30000; // 30 seconds
    private static final int MAX_SAFESPOT_DISTANCE = 14;
    private static final int MIN_ENGAGEMENT_DISTANCE = 2;

    @Override
    public Object[] getKeys() {
        return new Object[] { 25656 };
    }

    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        final Player player = target instanceof Player ? (Player) target : null;

        if (player == null) {
            return executeBasicDraconicAttack(npc, target, defs);
        }

        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeDraconicCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentDraconicGuidance(player, npc, scaling, defs);
        
        // Monitor scaling changes during combat
        monitorDraconicScalingChanges(player, scaling);
        
        // Update draconic phase tracking with v5.0 scaling
        updateIntelligentDraconicPhaseTracking(npc, scaling);
        
        // Check for draconic-themed safe spotting
        checkDraconicSafeSpotting(player, npc, scaling);
        
        // Enhanced draconic taunts with scaling-based frequency
        performEnhancedDraconicTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentDraconicAttackWithWarning(npc, player, defs, scaling);
    }

    /**
     * Initialize draconic combat session using BossBalancer v5.0
     */
    private void initializeDraconicCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            mechanicWarningCount.put(sessionKey, Integer.valueOf(0));
            consecutiveAvoidedAttacks.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastAttackHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced draconic combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentDraconicWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for draconic combat
            performInitialDraconicArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial draconic armor analysis
     */
    private void performInitialDraconicArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=8B0000>Draconic Analysis: Vulnerable points detected. Ancient claws seek exposed flesh!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=660000>Draconic Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance). Ancient power still burns...</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from draconic attacks
     */
    private int applyHPAwareDraconicDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (draconic attacks are essence-focused)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on draconic attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "elder_destroyer":
                case "ancient_devastation":
                    damagePercent = ELDER_DAMAGE_PERCENT;
                    break;
                case "dragonfire":
                case "ancient_breath":
                    damagePercent = DRAGONFIRE_DAMAGE_PERCENT;
                    break;
                case "essence_drain":
                case "toxic_inferno":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 80% of current HP for draconic
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
     * NEW v5.0: Send HP warning if player is in danger from draconic attacks
     */
    private void checkAndWarnLowHPForDraconic(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming draconic damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.68) {
                    player.sendMessage("<col=ff0000>DRACONIC WARNING: This attack will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.48) {
                    player.sendMessage("<col=8B0000>DRACONIC WARNING: Heavy draconic damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent draconic welcome message based on power analysis
     */
    private String getIntelligentDraconicWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Verak Lith";
        
        message.append("<col=8B0000>").append(npcName).append(" spreads ancient wings, analyzing your essence (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=660000>[Ancient fury enhanced: +").append(diffIncrease).append("% draconic devastation]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Wyrm restraint: -").append(assistance).append("% essence drain]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Draconic resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full draconic protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent draconic guidance with power-based scaling awareness
     */
    private void provideIntelligentDraconicGuidance(Player player, NPC npc, CombatScaling scaling, NPCCombatDefinitions defs) {
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
        
        // Enhanced guidance (separate from warnings)
        if (Utils.random(GUIDANCE_CHANCE) == 0) {
            sendEnhancedDraconicGuidance(npc, player, scaling);
        }
        
        // Get guidance message based on v5.0 intelligent scaling
        String guidanceMessage = getIntelligentDraconicGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent draconic guidance message based on power analysis
     */
    private String getIntelligentDraconicGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentDraconicPhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getDraconicScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=660000>Draconic Analysis: Missing armor exposes you to essence drain! Draconic damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME draconic power due to scaling!)" : "";
                    return "<col=8B0000>Draconic Analysis: Ancient phase reached. Essence manipulation dramatically enhanced" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=4B0000>Draconic Analysis: Elder destroyer transformation! Maximum draconic mastery unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=660000>Draconic Analysis: Extreme draconic scaling detected! Consider facing greater wyrms!</col>";
                }
                break;
                
            case 3:
                // Fourth warning: Advanced tactics
                return "<col=8B5A2B>Draconic Tactics: Watch for essence drains and toxic infernos. Maintain stat restoration supplies!</col>";
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get draconic scaling analysis message
     */
    private String getDraconicScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Verak Lith";
        
        String baseMessage = "<col=DDA0DD>Draconic Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>" + npcName + "'s ancient fury restrained! Essence drain reduced by " + 
                   assistancePercent + "% due to insufficient draconic preparation.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=660000>" + npcName + "'s draconic mastery escalated! Essence damage increased by " + 
                   difficultyIncrease + "% due to superior draconic defenses.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=8B0000>Balanced draconic encounter. Optimal essence resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight essence advantage detected. " + npcName + "'s draconic intensity increased by " + 
                   difficultyIncrease + "% for ancient balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Draconic power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * Enhanced draconic guidance system
     */
    private void sendEnhancedDraconicGuidance(NPC npc, Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer warningCount = mechanicWarningCount.get(playerKey);
        if (warningCount == null) warningCount = 0;
        
        if (warningCount >= MAX_MECHANIC_WARNINGS_PER_FIGHT) {
            return; // Prevent spam
        }
        
        String[] strategicTips = {
            "Verak Lith drains your stats - bring restore potions!",
            "His freezing attacks can be countered with freedom abilities!",
            "Prayer points drain quickly - bring prayer restoration!",
            "Dragonfire protection is essential against his breath attacks!",
            "Watch for his multi-target sweeping attacks!",
            "Poison resistance helps against his toxic breath!",
            "His AOE attacks require quick positioning!"
        };

        // Safe array access with bounds checking
        int randomIndex = Math.abs(Utils.random(strategicTips.length)) % strategicTips.length;
        String wisdom = strategicTips[randomIndex];
        
        // Add scaling context to strategic messages
        if (scaling != null && scaling.bossDamageMultiplier > 1.5) {
            wisdom += " [Enhanced due to your combat mastery.]";
        } else if (scaling != null && scaling.bossDamageMultiplier < 0.9) {
            wisdom += " [Restrained due to current preparation.]";
        }
        
        player.sendMessage(wisdom);

        try {
            npc.setNextForceTalk(new ForceTalk(wisdom));
        } catch (Exception e) {
            // ForceTalk failed silently
        }
        
        mechanicWarningCount.put(playerKey, warningCount + 1);
    }

    /**
     * NEW v5.0: Monitor scaling changes during draconic combat
     */
    private void monitorDraconicScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getDraconicScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get draconic scaling change message
     */
    private String getDraconicScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Draconic Update: Essence balance improved! Wyrm restraint reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=660000>Draconic Update: Ancient fury now active due to increased essence power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Draconic Update: Essence resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=8B0000>Draconic Update: Essence protection restored! Draconic damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent draconic phase tracking with BossBalancer integration
     */
    private void updateIntelligentDraconicPhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentDraconicPhase(npc);
        
        Integer lastPhase = currentDraconicPhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentDraconicPhase.put(npcKey, newPhase);
            handleIntelligentDraconicPhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current draconic phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentDraconicPhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= ELDER_THRESHOLD) return 4;
            if (hpPercent <= ANCIENT_THRESHOLD) return 3;
            if (hpPercent <= MATURE_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent draconic phase transitions with scaling integration
     */
    private void handleIntelligentDraconicPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "The ancient destroyer";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("Mature essence manipulation!"));
            npc.setNextGraphics(new Graphics(7015));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED ANCIENT DEVASTATION UNLEASHED!" : "Ancient power courses through my essence!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(7023));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE ELDER DESTROYER TRANSFORMATION - MAXIMUM DRACONIC POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED DESTROYER FORM!" : "Witness the essence of destruction!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(7027));
            break;
        }
    }

    /**
     * NEW v5.0: Check for draconic safe spotting
     */
    private void checkDraconicSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveAvoidedAttacks.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastAttackHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect draconic-themed safe spotting
        int distance = player.getDistance(npc);
        boolean playerDistanced = distance > MAX_SAFESPOT_DISTANCE;
        boolean draconicFrustrated = consecutiveCount > 3; // Draconic essence seeks contact
        boolean recentAvoidance = !lastHit;
        
        boolean draconicSafeSpot = playerDistanced && draconicFrustrated && recentAvoidance;
        
        if (draconicSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating draconic-themed responses
            if (warningCount >= 3) {
                performDraconicAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting honorably
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=8B0000>The draconic essence settles as you engage with honor...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform draconic anti-safe spot measure
     */
    private void performDraconicAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=660000>Draconic essence seeks those who avoid true combat!</col>");
        
        // Essence drain that reaches through obstacles
        npc.setNextAnimation(new Animation(32104));
        npc.setNextForceTalk(new ForceTalk("COWARD! Face the essence of destruction!"));
        
        // Enhanced damage based on scaling
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.4) : 300; // Essence pursuit
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareDraconicDamageScaling(scaledDamage, player, "essence_pursuit");
        
        delayHit(npc, 1, player, getRegularHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>DRACONIC PENALTY: Safe spotting detected - essence drain ignores barriers!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced draconic taunts with scaling-based frequency
     */
    private void performEnhancedDraconicTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on draconic phase and scaling
        int draconicPhase = getCurrentDraconicPhase(npc);
        int tauntChance = 10 + (draconicPhase * 5); // Base 15% to 30% based on phase
        
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance += 12; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance += 8; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced draconic taunts based on phase and scaling
            performScaledDraconicTaunt(npc, draconicPhase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled draconic taunt based on phase and scaling
     */
    private void performScaledDraconicTaunt(NPC npc, int draconicPhase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "Feel the essence of destruction!",
            "Your defenses crumble before me!",
            "Ancient power courses through my veins!",
            "Freeze in terror before my might!",
            "Toxins and flames are my domain!",
            "Your essence feeds my power!",
            "Witness draconic devastation!"
        };
        
        String[] draconicTaunts = {
            "ANCIENT DEVASTATION AWAKENS!",
            "ESSENCE MANIPULATION PERFECTED!",
            "DRACONIC SUPREMACY UNLEASHED!",
            "THE DESTROYER'S FURY BURNS!",
            "TOXIN AND FLAME OBEY MY WILL!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED DRACONIC MASTERY ACTIVATED!",
            "YOUR SUPERIOR DEFENSES FUEL MY RAGE!",
            "MAXIMUM DESTROYER POWER UNLEASHED!",
            "ULTIMATE ELDER WYRM'S DOMINION!",
            "TRANSCENDENT DRACONIC DEVASTATION!"
        };
        
        String selectedTaunt;
        if (isHighScaling && draconicPhase >= 3) {
            // Use enhanced taunts for high scaling + high draconic power
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else if (draconicPhase >= 2) {
            // Use draconic taunts for high draconic phases
            selectedTaunt = draconicTaunts[Utils.random(draconicTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent draconic warning system
     */
    private int performIntelligentDraconicAttackWithWarning(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Enhanced attack selection based on distance and phase
            if (npc.withinDistance(player, npc.getSize())) {
                // Close range attack selection
                int attackChoice = Utils.random(10);
                
                switch (attackChoice) {
                    case 0:
                        sendMechanicWarning(npc, "Arcane energies gather!");
                        return enhancedMageAttack(npc, player, scaling);
                    case 1:
                        sendMechanicWarning(npc, "Ancient dragonfire awakens!");
                        return enhancedDragonFireAttack(npc, player, scaling);
                    case 2:
                        sendMechanicWarning(npc, "Piercing projectiles!");
                        return enhancedRangeAttack(npc, player, scaling);
                    case 3:
                        sendMechanicWarning(npc, "Toxic venom spreads!");
                        return enhancedPoisonAttack(npc, player, scaling);
                    case 4:
                        sendMechanicWarning(npc, "Devastating strike!");
                        return enhancedMeleeAttack2(npc, player, scaling);
                    case 5:
                        sendMechanicWarning(npc, "Prayer-searing flames!");
                        return enhancedMageAttack2(npc, player, scaling);
                    default:
                        return enhancedMeleeAttack(npc, player, scaling);
                }
            } else {
                // Long range attack selection
                int attackChoice = Utils.random(10);
                
                switch (attackChoice) {
                    case 0:
                        sendMechanicWarning(npc, "Freezing barrage!");
                        return enhancedRangeAttack(npc, player, scaling);
                    case 1:
                        sendMechanicWarning(npc, "Distant dragonfire!");
                        return enhancedDragonFireAttack(npc, player, scaling);
                    case 2:
                        sendMechanicWarning(npc, "Area devastation!");
                        return enhancedMageAttack2(npc, player, scaling);
                    case 3:
                        sendMechanicWarning(npc, "Poisonous clouds!");
                        return enhancedPoisonAttack(npc, player, scaling);
                    default:
                        return enhancedMageAttack(npc, player, scaling);
                }
            }
            
        } catch (Exception e) {
            return defs.getAttackDelay();
        }
    }

    /**
     * Enhanced ranged attack with freezing mechanics and HP-aware scaling
     */
    public int enhancedRangeAttack(NPC npc, Player player, CombatScaling scaling) {
        npc.setNextForceTalk(new ForceTalk("Frozen in terror!"));
        
        npc.setNextAnimation(new Animation(32109));
        World.sendProjectile(npc, player, 16, 56, 16, 35, 20, 16, 0);
        
        // Use BossBalancer damage calculation with HP-aware scaling
        int baseDamage = calculateBalancedDamage(npc, NPCCombatDefinitions.RANGE, 1.0, 1.4, scaling);
        int safeDamage = applyHPAwareDraconicDamageScaling(baseDamage, player, "frost_projectile");
        
        checkAndWarnLowHPForDraconic(player, safeDamage);
        delayHit(npc, 1, player, getRangeHit(npc, safeDamage));
        
        // Enhanced freeze mechanics based on scaling
        int freezeDuration = 5 + Utils.random(5) + (getCurrentDraconicPhase(npc) / 2);
        player.addFreezeDelay(freezeDuration * 1000, true);
        
        player.getPackets().sendGameMessage("Verak Lith's icy projectile freezes you in place!");
        player.sendMessage("You're frozen for " + freezeDuration + " seconds! Anti-freeze measures recommended!");
        
        // Track hit success
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastAttackHit.put(playerKey, safeDamage > 0);
        updateAvoidedAttacks(playerKey, safeDamage > 0);
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Enhanced AOE magic attack with prayer drain and HP-aware scaling
     */
    public int enhancedMageAttack2(NPC npc, Player player, CombatScaling scaling) {
        npc.setNextForceTalk(new ForceTalk("Prayer burns before my might!"));
        
        npc.setNextAnimation(new Animation(32104));
        final WorldTile center = new WorldTile(player);
        World.sendGraphics(npc, new Graphics(7027), center);
        World.sendGraphics(npc, new Graphics(7013), player);
        
        // Enhanced prayer drain based on scaling
        int activeLevel = player.getPrayer().getPrayerpoints();
        if (activeLevel > 0) {
            int drainAmount = (player.getSkills().getLevelForXp(Skills.PRAYER) * 200) / (10 - getCurrentDraconicPhase(npc));
            player.getPrayer().drainPrayer(Math.max(drainAmount, activeLevel / 2));
            player.sendMessage("Verak Lith's magic sears your prayer points away!");
        }
        
        WorldTasksManager.schedule(new WorldTask() {
            int count = 0;

            @Override
            public void run() {
                if (player.withinDistance(center, 1)) {
                    // Use balanced damage for AOE with HP-aware scaling
                    int aoeDamage = calculateBalancedDamage(npc, NPCCombatDefinitions.MAGE, 0.8, 1.2, scaling);
                    int safeDamage = applyHPAwareDraconicDamageScaling(aoeDamage, player, "essence_drain");
                    
                    if (count == 0) checkAndWarnLowHPForDraconic(player, safeDamage);
                    delayHit(npc, 0, player, new Hit(npc, safeDamage, HitLook.REGULAR_DAMAGE));
                    player.sendMessage("You're caught in Verak Lith's prayer-draining inferno!");
                }
                
                if (count++ == 8) {
                    stop();
                    return;
                }
            }
        }, 0, 0);
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Enhanced magic attack with defense draining and HP-aware scaling
     */
    public int enhancedMageAttack(NPC npc, Player player, CombatScaling scaling) {
        npc.setNextForceTalk(new ForceTalk("Your defenses crumble!"));
        
        npc.setNextAnimation(new Animation(32112));
        World.sendProjectile(npc, player, 7015, 56, 16, 35, 20, 16, 0);
        World.sendGraphics(npc, new Graphics(7016), player);
        
        // Use BossBalancer damage calculation with HP-aware scaling
        int baseDamage = calculateBalancedDamage(npc, NPCCombatDefinitions.MAGE, 1.1, 1.6, scaling);
        int safeDamage = applyHPAwareDraconicDamageScaling(baseDamage, player, "essence_drain");
        
        checkAndWarnLowHPForDraconic(player, safeDamage);
        delayHit(npc, 0, player, getMagicHit(npc, safeDamage));
        
        player.getPackets().sendGameMessage("Your Defense has been drained by Verak Lith!");
        
        // Enhanced defense drain based on scaling
        int currentLevel = player.getSkills().getLevel(Skills.DEFENCE);
        int drainAmount = 20 + (getCurrentDraconicPhase(npc) * 2); // More drain for higher phases
        player.getSkills().set(Skills.DEFENCE, 
            currentLevel < drainAmount ? 0 : currentLevel - drainAmount);
        
        player.sendMessage("Verak Lith's magic weakens your defenses! Restore potions recommended!");
        
        // Track hit success
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastAttackHit.put(playerKey, safeDamage > 0);
        updateAvoidedAttacks(playerKey, safeDamage > 0);
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Enhanced poison attack with tier-scaled toxicity and HP-aware scaling
     */
    public int enhancedPoisonAttack(NPC npc, Player player, CombatScaling scaling) {
        npc.setNextForceTalk(new ForceTalk("Toxic venom courses through you!"));
        
        npc.setNextAnimation(new Animation(32111));
        World.sendProjectile(npc, player, 3436, 56, 16, 35, 20, 16, 0);
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                player.getPackets().sendGameMessage("You are hit by Verak Lith's poisonous breath!", true);
                
                // Use balanced damage calculation with HP-aware scaling
                int baseDamage = calculateBalancedDamage(npc, NPCCombatDefinitions.MAGE, 1.2, 1.7, scaling);
                int safeDamage = applyHPAwareDraconicDamageScaling(baseDamage, player, "toxic_inferno");
                
                checkAndWarnLowHPForDraconic(player, safeDamage);
                delayHit(npc, 0, player, getMagicHit(npc, safeDamage));
                
                player.setNextGraphics(new Graphics(3437, 50, 0));
                
                // Phase-scaled poison damage
                int poisonDamage = Math.min(200 + (getCurrentDraconicPhase(npc) * 20), 400);
                player.getPoison().makePoisoned(poisonDamage);
                
                player.sendMessage("Verak Lith's toxic breath poisons you severely! Antipoison urgently needed!");
                stop();
            }
        }, 0);
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Enhanced basic melee attack with HP-aware scaling
     */
    public int enhancedMeleeAttack(NPC npc, Player player, CombatScaling scaling) {
        npc.setNextAnimation(new Animation(32106));
        
        // Use BossBalancer damage calculation with HP-aware scaling
        int baseDamage = calculateBalancedDamage(npc, NPCCombatDefinitions.MELEE, 1.0, 1.5, scaling);
        int safeDamage = applyHPAwareDraconicDamageScaling(baseDamage, player, "claw_strike");
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        // Occasional melee guidance
        if (Utils.random(5) == 0) {
            player.sendMessage("Verak Lith's claws strike with draconic fury!");
        }
        
        // Track hit success
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastAttackHit.put(playerKey, safeDamage > 0);
        updateAvoidedAttacks(playerKey, safeDamage > 0);
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Enhanced multi-target melee attack with HP-aware scaling
     */
    public int enhancedMeleeAttack2(NPC npc, Player player, CombatScaling scaling) {
        npc.setNextForceTalk(new ForceTalk("Sweep of destruction!"));
        
        npc.setNextAnimation(new Animation(32107));
        
        // Higher damage for multi-target attack with HP-aware scaling
        int baseDamage = calculateBalancedDamage(npc, NPCCombatDefinitions.MELEE, 1.3, 1.8, scaling);
        int safeDamage = applyHPAwareDraconicDamageScaling(baseDamage, player, "sweep_attack");
        
        checkAndWarnLowHPForDraconic(player, safeDamage);
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        player.sendMessage("Verak Lith's sweeping attack hits with devastating force!");
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Enhanced dragonfire attack with protection mechanics and HP-aware scaling
     */
    public int enhancedDragonFireAttack(NPC npc, Player player, CombatScaling scaling) {
        npc.setNextForceTalk(new ForceTalk("Draconic flames consume all!"));
        
        int baseDamage = calculateBalancedDamage(npc, NPCCombatDefinitions.MAGE, 1.5, 2.5, scaling);
        int finalDamage = baseDamage;

        // Enhanced dragonfire protection system
        String message = Combat.getProtectMessage(player);
        if (message != null) {
            player.sendMessage(message, true);
            if (message.contains("fully")) {
                finalDamage = (int) (baseDamage * 0.1);
                player.sendMessage("Your protection greatly reduces Verak Lith's dragonfire!");
            } else if (message.contains("most")) {
                finalDamage = (int) (baseDamage * 0.2);
                player.sendMessage("Your protection partially blocks the dragonfire!");
            } else if (message.contains("some")) {
                finalDamage = (int) (baseDamage * 0.3);
                player.sendMessage("Your protection provides some relief from the flames!");
            }
        } else {
            // No protection - apply HP-aware scaling
            finalDamage = applyHPAwareDraconicDamageScaling(finalDamage, player, "dragonfire");
            checkAndWarnLowHPForDraconic(player, finalDamage);
            player.sendMessage("Dragonfire protection would greatly help against Verak Lith!");
        }
        
        if (finalDamage > 0) {
            player.sendMessage("You are burned by Verak Lith's fiery breath!", true);
        }
        
        npc.setNextAnimation(new Animation(32110));
        World.sendGraphics(npc, new Graphics(7023), npc);
        World.sendProjectile(npc, player, 438, 56, 16, 35, 20, 16, 0);
        delayHit(npc, 1, player, getRegularHit(npc, finalDamage));
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Calculate balanced damage using BossBalancer system with HP-aware scaling
     */
    private int calculateBalancedDamage(NPC npc, int attackType, double minMultiplier, double maxMultiplier, CombatScaling scaling) {
        NPCCombatDefinitions combatDefs = npc.getCombatDefinitions();
        if (combatDefs == null) {
            return Utils.random(150, 350);
        }
        
        int baseMaxHit = combatDefs.getMaxHit();
        
        // Apply BossBalancer scaling if available
        if (scaling != null) {
            baseMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, null, npc);
        }
        
        // Calculate damage range
        int minDamage = (int) (baseMaxHit * minMultiplier);
        int maxDamage = (int) (baseMaxHit * maxMultiplier);
        
        // Add phase-based scaling
        int draconicPhase = getCurrentDraconicPhase(npc);
        if (draconicPhase > 1) {
            double phaseMultiplier = 1.0 + ((draconicPhase - 1) * 0.15); // 15% per phase
            minDamage = (int) (minDamage * phaseMultiplier);
            maxDamage = (int) (maxDamage * phaseMultiplier);
        }
        
        return Utils.random(minDamage, maxDamage);
    }

    /**
     * Send mechanic warning with cooldown
     */
    private void sendMechanicWarning(NPC npc, String message) {
        long currentTime = System.currentTimeMillis();
        Integer playerKey = Integer.valueOf(npc.getIndex()); // Use NPC index for warning tracking
        Long lastMechanicWarning = lastWarning.get(playerKey);
        
        if (lastMechanicWarning == null || currentTime - lastMechanicWarning > 8000) { // 8 second cooldown
            npc.setNextForceTalk(new ForceTalk(message));
            lastWarning.put(playerKey, currentTime);
        }
    }

    /**
     * Execute basic draconic attack for non-player targets
     */
    private int executeBasicDraconicAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
        npc.setNextAnimation(new Animation(32106));
        int damage = Utils.random(defs.getMaxHit() + 1);
        delayHit(npc, 0, target, getMeleeHit(npc, damage));
        return defs.getAttackDelay();
    }

    /**
     * Update avoided attacks tracking
     */
    private void updateAvoidedAttacks(Integer playerKey, boolean hit) {
        Integer avoidedCount = consecutiveAvoidedAttacks.get(playerKey);
        if (avoidedCount == null) avoidedCount = 0;
        if (!hit) {
            consecutiveAvoidedAttacks.put(playerKey, avoidedCount + 1);
        } else {
            consecutiveAvoidedAttacks.put(playerKey, 0);
        }
    }

    /**
     * Enhanced combat state validation
     */
    private boolean isValidCombatState(NPC npc, Entity target) {
        return npc != null && target != null && 
               !npc.isDead() && !target.isDead() && 
               npc.getCombatDefinitions() != null;
    }

    /**
     * ENHANCED v5.0: Handle draconic combat end with proper cleanup
     */
    public static void onDraconicCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentDraconicPhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            mechanicWarningCount.remove(playerKey);
            consecutiveAvoidedAttacks.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastAttackHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=8B0000>Draconic combat session ended. Essence scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("VerakLith: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during draconic combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Draconic scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("VerakLith: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onDraconicCombatEnd(player, null);
        }
    }

    // ===== LEGACY METHODS FOR BACKWARD COMPATIBILITY =====

    public void rangeAttack(NPC npc, Entity target) {
        if (target instanceof Player) {
            enhancedRangeAttack(npc, (Player) target, BossBalancer.getIntelligentCombatScaling((Player) target, npc));
        }
    }

    public void mageAttack2(NPC npc, Entity target) {
        if (target instanceof Player) {
            enhancedMageAttack2(npc, (Player) target, BossBalancer.getIntelligentCombatScaling((Player) target, npc));
        }
    }

    public void mageAttack(NPC npc, Entity target) {
        if (target instanceof Player) {
            enhancedMageAttack(npc, (Player) target, BossBalancer.getIntelligentCombatScaling((Player) target, npc));
        }
    }

    public void poisonAttack(NPC npc, Entity target) {
        if (target instanceof Player) {
            enhancedPoisonAttack(npc, (Player) target, BossBalancer.getIntelligentCombatScaling((Player) target, npc));
        }
    }

    public void meleeAttack(NPC npc, Entity target) {
        if (target instanceof Player) {
            enhancedMeleeAttack(npc, (Player) target, BossBalancer.getIntelligentCombatScaling((Player) target, npc));
        }
    }

    public void meleeAttack2(NPC npc, Entity target) {
        if (target instanceof Player) {
            enhancedMeleeAttack2(npc, (Player) target, BossBalancer.getIntelligentCombatScaling((Player) target, npc));
        }
    }

    public void dragonFireAttack(NPC npc, Entity target) {
        if (target instanceof Player) {
            enhancedDragonFireAttack(npc, (Player) target, BossBalancer.getIntelligentCombatScaling((Player) target, npc));
        }
    }
}