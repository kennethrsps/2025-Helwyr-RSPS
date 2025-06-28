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
import com.rs.utils.NPCBonuses;
import com.rs.cache.loaders.NPCDefinitions;

/**
 * Enhanced Verac the Defiled Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Divine warrior mechanics, armor piercing, prayer disruption, HP-aware damage scaling
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 3.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System
 */
public class VeracsCombat extends CombatScript {

    // ===== DIVINE WARRIOR PHASES - Enhanced for v5.0 =====
    private static final double DEVOTED_THRESHOLD = 0.75;   // 75% HP - devoted phase
    private static final double ZEALOT_THRESHOLD = 0.50;    // 50% HP - zealot phase begins
    private static final double DIVINE_THRESHOLD = 0.25;    // 25% HP - divine phase
    private static final double ASCENSION_THRESHOLD = 0.10; // 10% HP - divine ascension

    // ===== ENHANCED GUIDANCE SYSTEM - Intelligent scaling aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentDivinePhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> prayerDisruptCount = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 180000; // 3 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 30000; // 30 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 3000; // 3 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 3; // Increased for v5.0 features

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - CRITICAL SAFETY SYSTEM =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.32; // Max 32% of player HP per hit (divine warrior is balanced)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.45;  // Max 45% for divine strikes
    private static final double ARMOR_PIERCE_DAMAGE_PERCENT = 0.38; // Max 38% for armor piercing
    private static final double ASCENSION_DAMAGE_PERCENT = 0.50; // Max 50% for divine ascension
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 480;          // Hard cap (32% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 30;               // Minimum damage to prevent 0 hits

    // ===== PRAYER DISRUPTION MECHANICS =====
    private static final int PRAYER_DISRUPT_CHANCE = 5; // 1 in 5 chance for prayer effects
    private static final int MAX_PRAYER_DISRUPTS_PER_FIGHT = 6; // Prevent excessive disruption

    // ===== DIVINE ATTACK PATTERNS with v5.0 intelligence =====
    private static final DivineAttackPattern[] DIVINE_ATTACK_PATTERNS = {
        new DivineAttackPattern(18222, 0, 0, "flail_strike", false, ""),
        new DivineAttackPattern(18222, 94, 0, "armor_pierce", true, "ARMOR PIERCE incoming - divine flail ignores all protection!"),
        new DivineAttackPattern(18300, 363, 0, "divine_strike", true, "DIVINE STRIKE incoming - holy power unleashed!"),
        new DivineAttackPattern(18236, 157, 0, "flail_spin", true, "FLAIL SPIN incoming - 360 degree divine assault!"),
        new DivineAttackPattern(18300, 363, 348, "divine_ascension", true, "DIVINE ASCENSION incoming - ultimate holy power!")
    };

    // ===== SAFE SPOT PREVENTION - Divine-themed =====
    private static final Map<Integer, Integer> consecutiveAvoidedStrikes = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastStrikeHit = new ConcurrentHashMap<Integer, Boolean>();

    // ===== ANIMATION AND GRAPHICS CONSTANTS =====
    private static final int FLAIL_ATTACK_ANIM = 18222;  
    private static final int SPIN_ATTACK_ANIM = 18236;   
    private static final int DIVINE_STRIKE_ANIM = 18300; 
    private static final int ARMOR_PIERCE_GFX = 94;      
    private static final int PRAYER_DISABLE_GFX = 348;   
    private static final int DIVINE_POWER_GFX = 363;     
    private static final int FLAIL_SPIN_GFX = 157;       
    private static final int BALANCE_AURA_GFX = 170;     

    @Override
    public Object[] getKeys() {
        return new Object[] { 2025 }; // Verac the Defiled
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeDivineCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentDivineGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorDivineScalingChanges(player, scaling);
        
        // Update divine phase tracking with v5.0 scaling
        updateIntelligentDivinePhaseTracking(npc, scaling);
        
        // Check for divine-themed safe spotting
        checkDivineSafeSpotting(player, npc, scaling);
        
        // Enhanced divine taunts with scaling-based frequency
        performEnhancedDivineTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentDivineAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize divine combat session using BossBalancer v5.0
     */
    private void initializeDivineCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            prayerDisruptCount.put(sessionKey, Integer.valueOf(0));
            consecutiveAvoidedStrikes.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastStrikeHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced divine combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentDivineWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for divine combat
            performInitialDivineArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial divine armor analysis
     */
    private void performInitialDivineArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=FFD700>Divine Analysis: Gaps in your armor offend the divine balance!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=4169E1>Divine Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance). The divine balance acknowledges your preparation.</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from divine attacks
     */
    private int applyHPAwareDivineDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (divine attacks are balanced but powerful)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on divine attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "divine_ascension":
                case "ultimate_divine":
                    damagePercent = ASCENSION_DAMAGE_PERCENT;
                    break;
                case "divine_strike":
                case "holy_power":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "armor_pierce":
                case "defense_ignore":
                    damagePercent = ARMOR_PIERCE_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 80% of current HP for divine
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
     * NEW v5.0: Send HP warning if player is in danger from divine attacks
     */
    private void checkAndWarnLowHPForDivine(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming divine damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.70) {
                    player.sendMessage("<col=ff0000>DIVINE WARNING: This holy strike will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.50) {
                    player.sendMessage("<col=FFD700>DIVINE WARNING: Heavy divine damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent divine welcome message based on power analysis
     */
    private String getIntelligentDivineWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Verac";
        
        message.append("<col=FFD700>").append(npcName).append(" channels divine power, weighing your spiritual balance (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=4169E1>[Divine judgment: +").append(diffIncrease).append("% holy power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Divine mercy: -").append(assistance).append("% divine damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Divine resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Divine protection acknowledged]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent divine guidance with power-based scaling awareness
     */
    private void provideIntelligentDivineGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentDivineGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent divine guidance message based on power analysis
     */
    private String getIntelligentDivineGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentDivinePhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getDivineScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=4169E1>Divine Analysis: Missing armor disrupts the divine balance! Divine damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME divine power due to scaling!)" : "";
                    return "<col=FFD700>Divine Analysis: Divine ascension phase reached. Holy power dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=660000>Divine Analysis: Ultimate ascension! Maximum divine power unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=4169E1>Divine Analysis: Extreme divine scaling detected! Consider facing worthier opponents!</col>";
                }
                break;
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get divine scaling analysis message
     */
    private String getDivineScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Verac";
        
        String baseMessage = "<col=DDA0DD>Divine Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>" + npcName + "'s divine mercy activated! Divine damage reduced by " + 
                   assistancePercent + "% due to insufficient spiritual preparation.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=4169E1>" + npcName + "'s divine judgment escalated! Holy power increased by " + 
                   difficultyIncrease + "% due to superior spiritual defenses.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=FFD700>Balanced divine encounter. Optimal spiritual harmony achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight spiritual advantage detected. " + npcName + "'s divine intensity increased by " + 
                   difficultyIncrease + "% for divine balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Divine power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during divine combat
     */
    private void monitorDivineScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getDivineScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get divine scaling change message
     */
    private String getDivineScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Divine Update: Spiritual balance improved! Divine mercy reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=4169E1>Divine Update: Divine judgment now active due to increased spiritual power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Divine Update: Divine resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=FFD700>Divine Update: Divine protection restored! Divine damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent divine phase tracking with BossBalancer integration
     */
    private void updateIntelligentDivinePhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentDivinePhase(npc);
        
        Integer lastPhase = currentDivinePhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentDivinePhase.put(npcKey, newPhase);
            handleIntelligentDivinePhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current divine phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentDivinePhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= ASCENSION_THRESHOLD) return 4;
            if (hpPercent <= DIVINE_THRESHOLD) return 3;
            if (hpPercent <= ZEALOT_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent divine phase transitions with scaling integration
     */
    private void handleIntelligentDivinePhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "The divine warrior";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("The divine balance tips!"));
            npc.setNextGraphics(new Graphics(BALANCE_AURA_GFX));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED DIVINE POWER UNLEASHED!" : "Feel the weight of divine judgment!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(DIVINE_POWER_GFX));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE DIVINE ASCENSION - MAXIMUM HOLY POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED DIVINE ASCENSION!" : "I ascend to divine glory!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(DIVINE_POWER_GFX));
            break;
        }
    }

    /**
     * NEW v5.0: Check for divine safe spotting
     */
    private void checkDivineSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveAvoidedStrikes.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastStrikeHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect divine-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean divineDispleased = consecutiveCount > 2; // Divine power dislikes avoidance
        boolean recentAvoidance = !lastHit;
        
        boolean divineSafeSpot = playerDistanced && divineDispleased && recentAvoidance;
        
        if (divineSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating divine-themed responses
            if (warningCount >= 3) {
                performDivineAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=FFD700>The divine balance is restored as you engage in honorable combat...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform divine anti-safe spot measure
     */
    private void performDivineAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=4169E1>Divine judgment finds those who avoid righteous combat!</col>");
        
        // Divine strike that reaches through all obstacles
        npc.setNextAnimation(new Animation(DIVINE_STRIKE_ANIM));
        npc.setNextForceTalk(new ForceTalk("COWARD! Face divine judgment!"));
        
        // Enhanced damage based on scaling
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.5) : 200; // Divine judgment strike
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareDivineDamageScaling(scaledDamage, player, "divine_judgment");
        
        delayHit(npc, 1, player, getMeleeHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>DIVINE PENALTY: Safe spotting detected - divine judgment reaches all!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced divine taunts with scaling-based frequency
     */
    private void performEnhancedDivineTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on divine phase and scaling
        int divinePhase = getCurrentDivinePhase(npc);
        int tauntChance = 10 + (divinePhase * 6); // Base 16% to 34% based on phase
        
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance += 15; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance += 10; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced divine taunts based on phase and scaling
            performScaledDivineTaunt(npc, divinePhase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled divine taunt based on phase and scaling
     */
    private void performScaledDivineTaunt(NPC npc, int divinePhase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "Face divine judgment!",
            "The balance must be maintained!",
            "Your armor means nothing!",
            "Divine power flows through me!",
            "Prepare for holy retribution!",
            "The flail of faith strikes true!",
            "Balance through battle!"
        };
        
        String[] divineTaunts = {
            "DIVINE POWER COURSES THROUGH ME!",
            "THE HEAVENS DEMAND JUSTICE!",
            "NO DEFENSE CAN STOP DIVINE WILL!",
            "WITNESS TRUE HOLY FURY!",
            "THE DIVINE BALANCE WILL BE RESTORED!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED DIVINE JUDGMENT ACTIVATED!",
            "YOUR SUPERIOR DEFENSES FUEL MY DIVINE RAGE!",
            "MAXIMUM HOLY POWER UNLEASHED!",
            "ULTIMATE DIVINE WARRIOR'S WRATH!",
            "TRANSCENDENT DIVINE ASCENSION!"
        };
        
        String selectedTaunt;
        if (isHighScaling && divinePhase >= 3) {
            // Use enhanced taunts for high scaling + high divine power
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else if (divinePhase >= 2) {
            // Use divine taunts for high divine phases
            selectedTaunt = divineTaunts[Utils.random(divineTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent divine warning system
     */
    private int performIntelligentDivineAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int divinePhase = getCurrentDivinePhase(npc);
            DivineAttackPattern pattern = selectIntelligentDivineAttackPattern(divinePhase, scaling, currentCount);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentDivineWarning(scaling, currentCount)) {
                sendIntelligentDivinePreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledDivineAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentDivineAttackDelay(npc, divinePhase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledDivineAttack(npc, player, pattern, scaling);
                return getIntelligentDivineAttackDelay(npc, divinePhase, scaling);
            }
            
        } catch (Exception e) {
            return 5;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent divine warning probability based on scaling
     */
    private boolean shouldGiveIntelligentDivineWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing divine attacks
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
     * ENHANCED v5.0: Intelligent divine pre-attack warning with scaling context
     */
    private void sendIntelligentDivinePreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME divine power due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense holy power due to scaling!)";
        }
        
        player.sendMessage("<col=4169E1>DIVINE WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent divine attack pattern selection with scaling consideration
     */
    private DivineAttackPattern selectIntelligentDivineAttackPattern(int divinePhase, CombatScaling scaling, int attackCount) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on divine phase, scaling, and progression
        int baseSpecialChance = (divinePhase - 1) * 16; // 16% per divine phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 14 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 8 ? 7 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for divine attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        if (isOvergeared) {
            // More aggressive divine patterns for overgeared players
            if (roll < 8 + specialChance) return DIVINE_ATTACK_PATTERNS[4]; // Divine ascension
            if (roll < 20 + specialChance) return DIVINE_ATTACK_PATTERNS[2]; // Divine strike  
            if (roll < 35 + specialChance) return DIVINE_ATTACK_PATTERNS[3]; // Flail spin
            if (roll < 50 + specialChance) return DIVINE_ATTACK_PATTERNS[1]; // Armor pierce
        } else {
            // Standard divine pattern selection
            if (roll < 5 + specialChance) return DIVINE_ATTACK_PATTERNS[4]; // Divine ascension
            if (roll < 15 + specialChance) return DIVINE_ATTACK_PATTERNS[2]; // Divine strike  
            if (roll < 28 + specialChance) return DIVINE_ATTACK_PATTERNS[3]; // Flail spin
            if (roll < 40 + specialChance) return DIVINE_ATTACK_PATTERNS[1]; // Armor pierce
        }
        
        return DIVINE_ATTACK_PATTERNS[0]; // Flail strike
    }

    /**
     * ENHANCED v5.0: Execute divine attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledDivineAttack(NPC npc, Player player, DivineAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced divine damage calculation with v5.0 intelligence
            int divinePhase = getCurrentDivinePhase(npc);
            double divineModifier = 1.0 + (divinePhase - 1) * 0.20; // 20% per divine phase (balanced divine power)
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * divineModifier);
            
            // Execute different divine attack types with v5.0 scaling and HP-aware damage
            if ("divine_ascension".equals(pattern.name)) {
                executeIntelligentDivineAscension(npc, player, baseDamage, scaling);
            } else if ("divine_strike".equals(pattern.name)) {
                executeIntelligentDivineStrike(npc, player, baseDamage, scaling);
            } else if ("flail_spin".equals(pattern.name)) {
                executeIntelligentFlailSpin(npc, player, baseDamage, scaling);
            } else if ("armor_pierce".equals(pattern.name)) {
                executeIntelligentArmorPierce(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleDivineAttack(npc, player, baseDamage, 0, scaling, "flail_strike");
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
            // Enhanced fallback - execute basic divine attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleDivineAttack(npc, player, scaledDamage, 0, scaling, "flail_strike");
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent divine ascension attack with HP-aware scaling
     */
    private void executeIntelligentDivineAscension(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ultimate divine attack - 220% damage with divine power
        int damage = (int)(baseDamage * 2.2) + Utils.random(baseDamage / 3);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for ultimate attacks
        int safeDamage = applyHPAwareDivineDamageScaling(scaledDamage, player, "divine_ascension");
        checkAndWarnLowHPForDivine(player, safeDamage);
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        // Apply prayer disruption with ascension
        applyDivinePrayerDisruption(npc, player, true, scaling);
        
        // Update attack counter
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer strikeCount = attackCounter.get(playerKey);
        if (strikeCount == null) strikeCount = 0;
        attackCounter.put(playerKey, strikeCount + 1);
    }

    /**
     * ENHANCED v5.0: Intelligent divine strike attack with HP-aware scaling
     */
    private void executeIntelligentDivineStrike(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Divine strike damage (180% base for enhanced divine power)
        int damage = (int)(baseDamage * 1.8) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for divine strikes
        int safeDamage = applyHPAwareDivineDamageScaling(scaledDamage, player, "divine_strike");
        checkAndWarnLowHPForDivine(player, safeDamage);
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        // Apply prayer disruption
        if (safeDamage > 0) {
            applyDivinePrayerDisruption(npc, player, false, scaling);
        }
    }

    /**
     * ENHANCED v5.0: Intelligent flail spin attack with HP-aware scaling
     */
    private void executeIntelligentFlailSpin(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Flail spin - area attack with armor piercing
        int damage = (int)(baseDamage * 1.5) + Utils.random(baseDamage / 3);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for flail spin
        int safeDamage = applyHPAwareDivineDamageScaling(scaledDamage, player, "flail_spin");
        checkAndWarnLowHPForDivine(player, safeDamage);
        
        // Create spin effect around Verac
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Visual spin effects
                for (int angle = 0; angle < 360; angle += 45) {
                    double radians = Math.toRadians(angle);
                    int offsetX = (int)(Math.cos(radians) * 2);
                    int offsetY = (int)(Math.sin(radians) * 2);
                    
                    WorldTile effectTile = new WorldTile(
                        npc.getX() + offsetX,
                        npc.getY() + offsetY,
                        npc.getPlane()
                    );
                    
                    player.getPackets().sendGraphics(new Graphics(FLAIL_SPIN_GFX), effectTile);
                }
                
                // Deal damage if in range
                if (Utils.getDistance(npc.getWorldTile(), player.getWorldTile()) <= 1) {
                    player.applyHit(new Hit(npc, safeDamage, Hit.HitLook.MELEE_DAMAGE));
                    player.sendMessage("The divine flail spin catches you!");
                } else {
                    player.sendMessage("You avoid the spinning divine flail!");
                }
                stop();
            }
        }, 2);
        
        return;
    }

    /**
     * ENHANCED v5.0: Intelligent armor pierce attack with HP-aware scaling
     */
    private void executeIntelligentArmorPierce(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Armor pierce damage (160% base, ignores defense)
        int damage = (int)(baseDamage * 1.6) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for armor piercing
        int safeDamage = applyHPAwareDivineDamageScaling(scaledDamage, player, "armor_pierce");
        checkAndWarnLowHPForDivine(player, safeDamage);
        
        // Force minimum damage for armor pierce (divine balance ignores defense)
        if (safeDamage < 50) {
            safeDamage = 50 + Utils.random(30); // 50-80 minimum damage
        }
        
        player.setNextGraphics(new Graphics(ARMOR_PIERCE_GFX));
        player.sendMessage("Verac's divine flail pierces through all defenses!");
        
        delayHit(npc, 0, player, new Hit(npc, safeDamage, Hit.HitLook.MELEE_DAMAGE));
    }

    /**
     * ENHANCED v5.0: Intelligent single divine attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleDivineAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareDivineDamageScaling(scaledDamage, player, attackType);
        if (!"flail_strike".equals(attackType)) {
            checkAndWarnLowHPForDivine(player, safeDamage);
        }
        
        delayHit(npc, delay, player, getMeleeHit(npc, safeDamage));
        
        // Apply prayer disruption chance
        if (safeDamage > 0 && Utils.random(PRAYER_DISRUPT_CHANCE) == 0) {
            applyDivinePrayerDisruption(npc, player, false, scaling);
        }
        
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
     * ENHANCED v5.0: Apply divine prayer disruption with HP-aware limits
     */
    private void applyDivinePrayerDisruption(NPC npc, Player player, boolean isAscension, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer disruptCount = prayerDisruptCount.get(playerKey);
        if (disruptCount == null) disruptCount = 0;
        
        // Limit prayer disruptions per fight
        if (disruptCount >= MAX_PRAYER_DISRUPTS_PER_FIGHT) {
            return;
        }
        
        player.setNextGraphics(new Graphics(PRAYER_DISABLE_GFX));
        
        int currentPrayer = player.getSkills().getLevel(Skills.PRAYER);
        
        // Scale disruption based on scaling difficulty and attack type
        double disruptMultiplier = scaling.bossDamageMultiplier > 1.5 ? 1.4 : 1.0;
        int baseDisrupt = isAscension ? 25 : 15;
        int disruptAmount = (int)(baseDisrupt * disruptMultiplier);
        
        // Apply disruption with minimum protection
        int newLevel = Math.max(0, currentPrayer - disruptAmount);
        player.getSkills().set(Skills.PRAYER, newLevel);
        
        // Provide feedback to player
        if (isAscension) {
            player.sendMessage("<col=4169E1>Divine ascension severes your connection to the gods!</col>");
            npc.setNextForceTalk(new ForceTalk("Your prayers are meaningless!"));
        } else {
            player.sendMessage("<col=FFD700>Verac's divine power disrupts your prayers!</col>");
            npc.setNextForceTalk(new ForceTalk("The divine balance shifts!"));
        }
        
        // Update disruption counter
        prayerDisruptCount.put(playerKey, disruptCount + 1);
    }

    /**
     * ENHANCED v5.0: Intelligent divine attack delay with scaling consideration
     */
    private int getIntelligentDivineAttackDelay(NPC npc, int divinePhase, CombatScaling scaling) {
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) return 5;
        
        int baseDelay = defs.getAttackDelay();
        int divineSpeedBonus = Math.max(0, divinePhase - 1); // Divine power makes attacks faster
        
        // v5.0 intelligent scaling can affect attack speed for divine
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = 2; // Much faster for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingSpeedBonus = 1; // Faster for high scaling
        }
        
        return Math.max(4, baseDelay - divineSpeedBonus - scalingSpeedBonus);
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
     * ENHANCED v5.0: Handle divine combat end with proper cleanup
     */
    public static void onDivineCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentDivinePhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            prayerDisruptCount.remove(playerKey);
            consecutiveAvoidedStrikes.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastStrikeHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=FFD700>Divine combat session ended. Holy scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("VeracCombat: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during divine combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Divine scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("VeracCombat: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onDivineCombatEnd(player, null);
        }
    }

    /**
     * Enhanced divine attack pattern data structure
     */
    private static class DivineAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        DivineAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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