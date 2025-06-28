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
 * Enhanced Torag Combat Script with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Progressive defensive phases, ground slam mechanics, damage reflection, shield wall, run energy drain
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 3.0 - FULL BossBalancer v5.0 Integration with Intelligent Defensive Combat Scaling & HP-Aware System for ROTS
 */
public class Torags extends CombatScript {
    
    // ===== DEFENSIVE PHASES - Enhanced for v5.0 =====
    private static final double WARRIOR_THRESHOLD = 0.80;        // 80% HP - normal warrior
    private static final double GUARDIAN_THRESHOLD = 0.55;       // 55% HP - guardian phase  
    private static final double FORTRESS_THRESHOLD = 0.30;       // 30% HP - fortress phase
    private static final double IMMOVABLE_THRESHOLD = 0.12;      // 12% HP - immovable fortress
    
    // ===== ENHANCED GUIDANCE SYSTEM - Minigame-aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentDefensivePhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> defensiveStanceCount = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 120000; // 2 minutes between warnings
    private static final long SCALING_UPDATE_INTERVAL = 20000; // 20 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 2000; // 2 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 4; // More warnings for complex mechanics
    
    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - MINIGAME BALANCED =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.33; // Max 33% of player HP per hit (defensive balanced)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.48;  // Max 48% for ground slam attacks
    private static final double REFLECTION_DAMAGE_PERCENT = 0.25; // Max 25% for reflection damage
    private static final double EXHAUSTION_DAMAGE_PERCENT = 0.40; // Max 40% for exhaustion attacks
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 495;          // Hard cap (33% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 30;               // Minimum damage to prevent 0 hits
    
    // ===== RUN ENERGY DRAIN AND EFFECTS =====
    private static final int RUN_ENERGY_DRAIN_CHANCE = 4; // 1 in 4 chance for energy drain
    private static final int MAX_DEFENSIVE_STANCES_PER_FIGHT = 6; // Prevent spam
    private static final int RUN_ENERGY_DRAIN = 6; // Base energy drain amount
    private static final int REFLECTION_PERCENT = 30; // Damage reflection percentage
    
    // ===== DEFENSIVE ATTACK PATTERNS with v5.0 intelligence =====
    private static final DefensiveAttackPattern[] DEFENSIVE_ATTACK_PATTERNS = {
        new DefensiveAttackPattern(18222, 0, 0, "hammer_strike", false, ""),
        new DefensiveAttackPattern(18222, 399, 0, "energy_drain", true, "ENERGY DRAIN incoming - stamina sapped!"),
        new DefensiveAttackPattern(18236, 157, 0, "ground_slam", true, "GROUND SLAM incoming - earthquake damage!"),
        new DefensiveAttackPattern(18300, 94, 0, "shield_wall", true, "SHIELD WALL incoming - defensive stance!"),
        new DefensiveAttackPattern(18300, 170, 363, "immovable_fortress", true, "IMMOVABLE FORTRESS incoming - ultimate defense!")
    };
    
    // ===== SAFE SPOT PREVENTION - Defensive-themed =====
    private static final Map<Integer, Integer> consecutiveAvoidedSlams = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastSlamHit = new ConcurrentHashMap<Integer, Boolean>();
    
    // ===== ANIMATION AND GRAPHICS CONSTANTS =====
    private static final int HAMMER_ATTACK_ANIM = 18222;
    private static final int GROUND_SLAM_ANIM = 18236;
    private static final int DEFENSIVE_STANCE_ANIM = 18300;
    
    private static final int RUN_ENERGY_DRAIN_GFX = 399;
    private static final int GROUND_CRACK_GFX = 157;
    private static final int SHIELD_REFLECT_GFX = 94;
    private static final int EXHAUSTION_GFX = 170;
    private static final int DEFENSIVE_AURA_GFX = 363;
    
    @Override
    public Object[] getKeys() {
        return new Object[] { 18544 }; // Rise of the Six Torags
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeDefensiveCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0 (special handling for minigames)
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentDefensiveGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorDefensiveScalingChanges(player, scaling);
        
        // Update defensive phase tracking with v5.0 scaling
        updateIntelligentDefensivePhaseTracking(npc, scaling);
        
        // Check for defensive-themed safe spotting
        checkDefensiveSafeSpotting(player, npc, scaling);
        
        // Enhanced defensive taunts with scaling-based frequency
        performEnhancedDefensiveTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentDefensiveAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize defensive combat session using BossBalancer v5.0
     */
    private void initializeDefensiveCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            defensiveStanceCount.put(sessionKey, Integer.valueOf(0));
            consecutiveAvoidedSlams.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastSlamHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced defensive combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentDefensiveWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for defensive combat
            performInitialDefensiveArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial defensive armor analysis
     */
    private void performInitialDefensiveArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=8B4513>Defensive Analysis: Weak points detected. Hammer strikes will exploit gaps!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=654321>Defensive Analysis: Strong armor detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance). Brute force still effective...</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from defensive attacks
     */
    private int applyHPAwareDefensiveDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (defensive attacks are crushing-focused)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on defensive attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "immovable_fortress":
                case "fortress_mode":
                    damagePercent = EXHAUSTION_DAMAGE_PERCENT;
                    break;
                case "ground_slam":
                case "massive_slam":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "reflection_damage":
                case "shield_reflect":
                    damagePercent = REFLECTION_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 85% of current HP for defensive
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
     * NEW v5.0: Send HP warning if player is in danger from defensive attacks
     */
    private void checkAndWarnLowHPForDefensive(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming defensive damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.70) {
                    player.sendMessage("<col=ff0000>DEFENSIVE WARNING: This slam will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.50) {
                    player.sendMessage("<col=8B4513>DEFENSIVE WARNING: Heavy hammer damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent defensive welcome message based on power analysis
     */
    private String getIntelligentDefensiveWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Torags";
        
        message.append("<col=8B4513>").append(npcName).append(" raises his massive hammers, fortifying defenses (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=654321>[Fortress enhanced: +").append(diffIncrease).append("% defensive power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Hammer restraint: -").append(assistance).append("% crushing damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Defensive resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full defensive protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent defensive guidance with power-based scaling awareness
     */
    private void provideIntelligentDefensiveGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentDefensiveGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent defensive guidance message based on power analysis
     */
    private String getIntelligentDefensiveGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentDefensivePhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getDefensiveScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=654321>Defensive Analysis: Missing armor exposes you to crushing blows! Hammer damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME defensive power due to scaling!)" : "";
                    return "<col=8B4513>Defensive Analysis: Fortress phase reached. Defensive capabilities dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=654321>Defensive Analysis: Immovable fortress transformation! Maximum defensive mastery unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=654321>Defensive Analysis: Extreme defensive scaling detected! Consider facing higher-tier warriors!</col>";
                }
                break;
                
            case 3:
                // Fourth warning: Advanced tactics
                return "<col=8B7355>Defensive Tactics: Watch for ground slams and energy drains. Maintain distance from AOE attacks!</col>";
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get defensive scaling analysis message
     */
    private String getDefensiveScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Torags";
        
        String baseMessage = "<col=DDA0DD>Defensive Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>" + npcName + "'s fortress strength restrained! Crushing damage reduced by " + 
                   assistancePercent + "% due to insufficient offensive preparation.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=654321>" + npcName + "'s defensive mastery escalated! Crushing damage increased by " + 
                   difficultyIncrease + "% due to superior offensive capabilities.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=8B4513>Balanced defensive encounter. Optimal offensive positioning achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight offensive advantage detected. " + npcName + "'s defensive intensity increased by " + 
                   difficultyIncrease + "% for tactical balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Defensive power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during defensive combat
     */
    private void monitorDefensiveScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getDefensiveScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get defensive scaling change message
     */
    private String getDefensiveScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Defensive Update: Offensive balance improved! Fortress restraint reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=654321>Defensive Update: Fortress strength now active due to increased offensive power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Defensive Update: Damage resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=8B4513>Defensive Update: Offensive protection restored! Defensive damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent defensive phase tracking with BossBalancer integration
     */
    private void updateIntelligentDefensivePhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentDefensivePhase(npc);
        
        Integer lastPhase = currentDefensivePhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentDefensivePhase.put(npcKey, newPhase);
            handleIntelligentDefensivePhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current defensive phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentDefensivePhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= IMMOVABLE_THRESHOLD) return 4;
            if (hpPercent <= FORTRESS_THRESHOLD) return 3;
            if (hpPercent <= GUARDIAN_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent defensive phase transitions with scaling integration
     */
    private void handleIntelligentDefensivePhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "The fortress guardian";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("Guardian mode activated!"));
            npc.setNextGraphics(new Graphics(DEFENSIVE_AURA_GFX));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED FORTRESS MASTERY UNLEASHED!" : "I become an immovable wall!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(SHIELD_REFLECT_GFX));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE IMMOVABLE FORTRESS TRANSFORMATION - MAXIMUM DEFENSIVE POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED FORTRESS FORM!" : "Nothing shall pass my defense!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(GROUND_CRACK_GFX));
            break;
        }
    }

    /**
     * NEW v5.0: Check for defensive safe spotting
     */
    private void checkDefensiveSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveAvoidedSlams.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastSlamHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect defensive-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean fortressFrustrated = consecutiveCount > 3; // Fortress needs contact
        boolean recentAvoidance = !lastHit;
        
        boolean defensiveSafeSpot = playerDistanced && fortressFrustrated && recentAvoidance;
        
        if (defensiveSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating defensive-themed responses
            if (warningCount >= 3) {
                performDefensiveAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=8B4513>The fortress's anger subsides as you engage in direct combat...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform defensive anti-safe spot measure
     */
    private void performDefensiveAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=654321>Fortress rage seeks those who avoid direct combat!</col>");
        
        // Earthquake that reaches through obstacles
        npc.setNextAnimation(new Animation(GROUND_SLAM_ANIM));
        npc.setNextForceTalk(new ForceTalk("COWARD! Face me in combat!"));
        
        // Enhanced damage based on scaling
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.6) : 420; // Fortress fury
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareDefensiveDamageScaling(scaledDamage, player, "fortress_pursuit");
        
        // Also drain significant run energy
        int currentEnergy = player.getRunEnergy();
        int drainAmount = Math.max(currentEnergy - 10, 0); // Leave 10% energy
        player.setRunEnergy(player.getRunEnergy() - drainAmount);
        
        delayHit(npc, 1, player, getMeleeHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>DEFENSIVE PENALTY: Safe spotting detected - earthquake ignores barriers!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced defensive taunts with scaling-based frequency
     */
    private void performEnhancedDefensiveTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on defensive phase and scaling
        int defensivePhase = getCurrentDefensivePhase(npc);
        int tauntChance = 9 + (defensivePhase * 5); // Base 14% to 29% based on phase
        
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance += 13; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance += 9; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced defensive taunts based on phase and scaling
            performScaledDefensiveTaunt(npc, defensivePhase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled defensive taunt based on phase and scaling
     */
    private void performScaledDefensiveTaunt(NPC npc, int defensivePhase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "My hammers crush all!",
            "Defense is my strength!",
            "You cannot break my guard!",
            "Feel the weight of steel!",
            "My fortress stands eternal!",
            "Endurance conquers all!",
            "I am immovable!"
        };
        
        String[] defensiveTaunts = {
            "FORTRESS DEFENSE ACTIVATED!",
            "GUARDIAN PROTOCOLS ENGAGED!",
            "IMMOVABLE WALL UNLEASHED!",
            "WITNESS TRUE DEFENSIVE MASTERY!",
            "ENDURANCE BEYOND LIMITS!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED FORTRESS MASTERY ACTIVATED!",
            "YOUR SUPERIOR OFFENSE STRENGTHENS MY DEFENSE!",
            "MAXIMUM FORTRESS POWER UNLEASHED!",
            "ULTIMATE GUARDIAN'S DOMINION!",
            "TRANSCENDENT DEFENSIVE MASTERY!"
        };
        
        String selectedTaunt;
        if (isHighScaling && defensivePhase >= 3) {
            // Use enhanced taunts for high scaling + high defensive power
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else if (defensivePhase >= 2) {
            // Use defensive taunts for high defensive phases
            selectedTaunt = defensiveTaunts[Utils.random(defensiveTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent defensive warning system
     */
    private int performIntelligentDefensiveAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int defensivePhase = getCurrentDefensivePhase(npc);
            DefensiveAttackPattern pattern = selectIntelligentDefensiveAttackPattern(defensivePhase, scaling, currentCount);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentDefensiveWarning(scaling, currentCount)) {
                sendIntelligentDefensivePreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledDefensiveAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentDefensiveAttackDelay(npc, defensivePhase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledDefensiveAttack(npc, player, pattern, scaling);
                return getIntelligentDefensiveAttackDelay(npc, defensivePhase, scaling);
            }
            
        } catch (Exception e) {
            return 5;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent defensive warning probability based on scaling
     */
    private boolean shouldGiveIntelligentDefensiveWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing defensive attacks
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
     * ENHANCED v5.0: Intelligent defensive pre-attack warning with scaling context
     */
    private void sendIntelligentDefensivePreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME defensive power due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense fortress damage due to scaling!)";
        }
        
        player.sendMessage("<col=654321>DEFENSIVE WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent defensive attack pattern selection with scaling consideration
     */
    private DefensiveAttackPattern selectIntelligentDefensiveAttackPattern(int defensivePhase, CombatScaling scaling, int attackCount) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on defensive phase, scaling, and progression
        int baseSpecialChance = (defensivePhase - 1) * 15; // 15% per defensive phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 12 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 8 ? 7 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for defensive attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        if (isOvergeared) {
            // More aggressive defensive patterns for overgeared players
            if (roll < 8 + specialChance) return DEFENSIVE_ATTACK_PATTERNS[4]; // Immovable fortress
            if (roll < 22 + specialChance) return DEFENSIVE_ATTACK_PATTERNS[3]; // Shield wall  
            if (roll < 36 + specialChance) return DEFENSIVE_ATTACK_PATTERNS[2]; // Ground slam
            if (roll < 48 + specialChance) return DEFENSIVE_ATTACK_PATTERNS[1]; // Energy drain
        } else {
            // Standard defensive pattern selection
            if (roll < 5 + specialChance) return DEFENSIVE_ATTACK_PATTERNS[4]; // Immovable fortress
            if (roll < 17 + specialChance) return DEFENSIVE_ATTACK_PATTERNS[3]; // Shield wall  
            if (roll < 29 + specialChance) return DEFENSIVE_ATTACK_PATTERNS[2]; // Ground slam
            if (roll < 39 + specialChance) return DEFENSIVE_ATTACK_PATTERNS[1]; // Energy drain
        }
        
        return DEFENSIVE_ATTACK_PATTERNS[0]; // Hammer strike
    }

    /**
     * ENHANCED v5.0: Execute defensive attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledDefensiveAttack(NPC npc, Player player, DefensiveAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced defensive damage calculation with v5.0 intelligence
            int defensivePhase = getCurrentDefensivePhase(npc);
            double defensiveModifier = 1.0 + (defensivePhase - 1) * 0.18; // 18% per defensive phase (fortress mastery)
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * defensiveModifier);
            
            // Execute different defensive attack types with v5.0 scaling and HP-aware damage
            if ("immovable_fortress".equals(pattern.name)) {
                executeIntelligentImmovableFortress(npc, player, baseDamage, scaling);
            } else if ("shield_wall".equals(pattern.name)) {
                executeIntelligentShieldWall(npc, player, baseDamage, scaling);
            } else if ("ground_slam".equals(pattern.name)) {
                executeIntelligentGroundSlam(npc, player, baseDamage, scaling);
            } else if ("energy_drain".equals(pattern.name)) {
                executeIntelligentEnergyDrain(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleDefensiveAttack(npc, player, baseDamage, 0, scaling, "hammer_strike");
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
            // Enhanced fallback - execute basic defensive attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleDefensiveAttack(npc, player, scaledDamage, 0, scaling, "hammer_strike");
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent immovable fortress attack with HP-aware scaling
     */
    private void executeIntelligentImmovableFortress(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Ultimate defensive attack - 250% damage with massive energy drain
        int damage = (int)(baseDamage * 2.5) + Utils.random(baseDamage / 3);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for ultimate attacks
        int safeDamage = applyHPAwareDefensiveDamageScaling(scaledDamage, player, "immovable_fortress");
        checkAndWarnLowHPForDefensive(player, safeDamage);
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        // Massive energy drain
        int currentEnergy = player.getRunEnergy();
        int drainAmount = Math.max(currentEnergy - 2, 0); // Leave 2% energy
        player.setRunEnergy(player.getRunEnergy() - drainAmount);
        
        npc.setNextGraphics(new Graphics(DEFENSIVE_AURA_GFX));
        player.sendMessage("The immovable fortress drains all your stamina!");
    }

    /**
     * ENHANCED v5.0: Intelligent shield wall attack with HP-aware scaling
     */
    private void executeIntelligentShieldWall(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer stanceCount = defensiveStanceCount.get(playerKey);
        if (stanceCount == null) stanceCount = 0;
        
        // Limit defensive stances per fight
        if (stanceCount >= MAX_DEFENSIVE_STANCES_PER_FIGHT) {
            // Fall back to regular energy drain
            executeIntelligentEnergyDrain(npc, player, baseDamage, scaling);
            return;
        }
        
        player.sendMessage("Torags enters shield wall formation!");
        npc.setNextGraphics(new Graphics(SHIELD_REFLECT_GFX));
        
        // Shield wall damage (140% base for formation setup)
        int damage = (int)(baseDamage * 1.4) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for shield wall
        int safeDamage = applyHPAwareDefensiveDamageScaling(scaledDamage, player, "shield_wall");
        checkAndWarnLowHPForDefensive(player, safeDamage);
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        // Start reflection effect
        WorldTasksManager.schedule(new WorldTask() {
            int ticks = 0;
            final int duration = scaling.bossDamageMultiplier > 2.0 ? 10 : 8; // Longer for extreme scaling
            
            @Override
            public void run() {
                if (ticks >= duration || npc.isDead()) {
                    player.sendMessage("The shield wall formation breaks.");
                    stop();
                    return;
                }
                
                // Visual effect every 2 ticks
                if (ticks % 2 == 0) {
                    npc.setNextGraphics(new Graphics(SHIELD_REFLECT_GFX));
                }
                
                // Note: In full implementation, add reflection logic here
                // For now, just visual effect and energy drain
                if (ticks % 3 == 0) {
                    drainRunEnergy(player, RUN_ENERGY_DRAIN / 2);
                }
                
                ticks++;
            }
        }, 1);
        
        // Update stance counter
        defensiveStanceCount.put(playerKey, stanceCount + 1);
    }

    /**
     * ENHANCED v5.0: Intelligent ground slam attack with HP-aware scaling
     */
    private void executeIntelligentGroundSlam(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        player.sendMessage("Torags raises his hammers for a devastating ground slam!");
        
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
                        player.getPackets().sendGraphics(new Graphics(GROUND_CRACK_GFX), effectTile);
                    }
                }
                
                // Deal damage if in range
                if (Utils.getDistance(npc.getWorldTile(), player.getWorldTile()) <= 2) {
                    int damage = (int)(baseDamage * 1.3) + Utils.random(baseDamage / 3);
                    int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
                    
                    // CRITICAL: Apply HP-aware damage scaling for ground slam
                    int safeDamage = applyHPAwareDefensiveDamageScaling(scaledDamage, player, "ground_slam");
                    checkAndWarnLowHPForDefensive(player, safeDamage);
                    
                    player.applyHit(new Hit(npc, safeDamage, Hit.HitLook.MELEE_DAMAGE));
                    drainRunEnergy(player, RUN_ENERGY_DRAIN * 3); // Heavy energy drain
                    player.sendMessage("The ground slam exhausts you completely!");
                } else {
                    player.sendMessage("You avoid the ground slam by keeping your distance!");
                }
                stop();
            }
        }, 2); // 2 tick delay for warning
    }

    /**
     * ENHANCED v5.0: Intelligent energy drain attack with HP-aware scaling
     */
    private void executeIntelligentEnergyDrain(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        int damage = (int)(baseDamage * 1.2);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for energy drain
        int safeDamage = applyHPAwareDefensiveDamageScaling(scaledDamage, player, "energy_drain");
        checkAndWarnLowHPForDefensive(player, safeDamage);
        
        npc.setNextAnimation(new Animation(HAMMER_ATTACK_ANIM));
        player.sendMessage("Torags' hammers drain your stamina!");
        
        // Guaranteed energy drain on this attack
        drainRunEnergy(player, RUN_ENERGY_DRAIN * 2); // Enhanced drain for special
        
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
    }

    /**
     * ENHANCED v5.0: Intelligent single defensive attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleDefensiveAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareDefensiveDamageScaling(scaledDamage, player, attackType);
        if (!"hammer_strike".equals(attackType)) {
            checkAndWarnLowHPForDefensive(player, safeDamage);
        }
        
        npc.setNextAnimation(new Animation(HAMMER_ATTACK_ANIM));
        delayHit(npc, delay, player, getMeleeHit(npc, safeDamage));
        
        // Apply energy drain chance
        if (safeDamage > 0 && Utils.random(RUN_ENERGY_DRAIN_CHANCE) == 0) {
            drainRunEnergy(player, RUN_ENERGY_DRAIN);
            player.sendMessage("Torags' hammers drain your energy!");
        }
        
        // Track if attack hit
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastSlamHit.put(playerKey, safeDamage > 0);
        
        // Update avoided slams counter
        Integer avoidedCount = consecutiveAvoidedSlams.get(playerKey);
        if (avoidedCount == null) avoidedCount = 0;
        if (safeDamage <= 0) {
            consecutiveAvoidedSlams.put(playerKey, avoidedCount + 1);
        } else {
            consecutiveAvoidedSlams.put(playerKey, 0);
        }
    }

    /**
     * ENHANCED v5.0: Intelligent defensive attack delay with scaling consideration
     */
    private int getIntelligentDefensiveAttackDelay(NPC npc, int defensivePhase, CombatScaling scaling) {
        int baseDelay = 5; // Standard defensive delay
        int defensiveSpeedBonus = Math.max(0, defensivePhase - 1); // Defensive mastery makes attacks slower but harder
        
        // v5.0 intelligent scaling can affect attack speed for defensive
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = -1; // Slower but more powerful for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingSpeedBonus = 0; // Same speed for high scaling
        }
        
        return Math.max(4, baseDelay + defensiveSpeedBonus + scalingSpeedBonus);
    }

    /**
     * Helper method to drain player's run energy
     */
    private void drainRunEnergy(Player player, int amount) {
        player.setNextGraphics(new Graphics(RUN_ENERGY_DRAIN_GFX));
        int currentEnergy = player.getRunEnergy();
        int newEnergy = Math.max(0, currentEnergy - amount);
        player.setRunEnergy(newEnergy);
        
        if (amount > RUN_ENERGY_DRAIN) {
            player.sendMessage("Your energy has been severely drained!");
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
     * ENHANCED v5.0: Handle defensive combat end with proper cleanup
     */
    public static void onDefensiveCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentDefensivePhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            defensiveStanceCount.remove(playerKey);
            consecutiveAvoidedSlams.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastSlamHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=8B4513>Defensive combat session ended. Fortress scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("Torags: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during defensive combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Defensive scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("Torags: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onDefensiveCombatEnd(player, null);
        }
    }

    /**
     * Enhanced defensive attack pattern data structure
     */
    private static class DefensiveAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        DefensiveAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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