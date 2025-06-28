package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.dragons.RuneDragon;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.player.content.Combat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.game.Hit;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;
import com.rs.cache.loaders.NPCDefinitions;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * ULTIMATE Elite Rune Dragon Combat System with FULL BossBalancer v5.0 Integration
 * 
 * NEW v6.0 FEATURES:
 * - COMPLETE Boss Balancer v5.0 intelligent scaling integration
 * - HP-Aware Damage Scaling System (990-1500 HP support)
 * - Enhanced multi-phase combat with power ratio awareness
 * - Intelligent attack selection based on player power analysis
 * - Advanced rune magic abilities with scaling-based intensity
 * - Comprehensive armor analysis and protection warnings
 * - Enhanced educational guidance system with scaling context
 * - Combat session management with power locking
 * - Performance optimized damage calculation with safety caps
 * - Elite dragon-themed scaling messages and warnings
 * 
 * @author Zeus  
 * @date June 09, 2025
 * @version 6.0 - COMPLETE Boss Balancer v5.0 Integration with HP-Aware Damage Scaling
 */
public class EliteRuneDragonCombat extends CombatScript {
    
    // ===== BOSS BALANCER v5.0 INTEGRATION CONSTANTS =====
    private static final int ELITE_RUNE_DRAGON_BOSS_TYPE = 4; // Hybrid Boss Type (all combat styles + dragonfire)
    private static final int ELITE_RUNE_DRAGON_DEFAULT_TIER = 6; // Master tier by default
    
    // ===== HP-AWARE DAMAGE SCALING CONSTANTS =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.32; // Max 32% of player HP per hit (rune magic)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.45;  // Max 45% for ancient rune breath  
    private static final double DRAGONFIRE_DAMAGE_PERCENT = 0.40; // Max 40% for enhanced dragonfire
    private static final double DISRUPTION_DAMAGE_PERCENT = 0.28; // Max 28% for rune disruption
    private static final double ELDER_DAMAGE_PERCENT = 0.48;     // Max 48% for elder rune techniques
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 480;          // Hard cap (32% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 25;               // Minimum damage for elite dragon
    
    // ===== ENHANCED PHASE-BASED COMBAT THRESHOLDS =====
    private static final double PHASE_TRANSITION_THRESHOLD = 0.50; // 50% health - enters enhanced phase 2
    private static final double RUNE_MASTERY_THRESHOLD = 0.75; // 75% health - enhanced rune abilities
    private static final double FINAL_DESPERATION_THRESHOLD = 0.25; // 25% health - maximum power
    
    // ===== INTELLIGENT ATTACK PROBABILITIES =====
    private static final int MELEE_RANGE_ATTACKS = 4; // 4 attack types when in melee range
    private static final int DISTANT_RANGE_ATTACKS = 3; // 3 attack types when distant
    
    // ===== SPECIAL ABILITY CHANCES (Scaling-Aware) =====
    private static final int FREEZE_CHANCE = 3; // 1 in 3 chance for freeze on range attack
    private static final int ENHANCED_FREEZE_CHANCE = 2; // 1 in 2 in phase 2
    private static final int RUNE_DISRUPTION_CHANCE = 15; // 1 in 15 for rune disruption
    private static final int ENHANCED_DISRUPTION_CHANCE = 10; // 1 in 10 in phase 2
    
    // ===== ENHANCED GUIDANCE SYSTEM =====
    private static final int GUIDANCE_FREQUENCY = 4; // 1 in 4 chance for strategic hints
    private static final int HINT_COOLDOWN = 15000; // 15 seconds between hints
    private static final int PHASE_WARNING_COOLDOWN = 30000; // 30 seconds between phase warnings
    private static final long WARNING_COOLDOWN = 60000; // 1 minute between scaling warnings
    private static final int MAX_WARNINGS_PER_FIGHT = 6; // Educational warnings limit
    
    // ===== COMBAT SESSION MANAGEMENT =====
    private static final Map<Integer, EliteRuneDragonCombatSession> combatSessions = new ConcurrentHashMap<Integer, EliteRuneDragonCombatSession>();
    private static final Map<Integer, Long> lastWarningTimeMap = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningCount = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> educationalTipCount = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== INSTANCE VARIABLES FOR ENHANCED TRACKING =====
    private long lastHintTime = 0;
    private long lastPhaseWarningTime = 0;
    private boolean hasGivenOpeningAdvice = false;
    private boolean hasGivenMasteryWarning = false;
    private boolean hasGivenPhaseWarning = false;
    private boolean hasGivenDesperationWarning = false;
    private int freezeAttackCount = 0;
    private int dragonFireCount = 0;
    private int runeDisruptionCount = 0;
    private int phaseTransitionCount = 0;
    private int totalDamageDealt = 0;
    private int lastKnownPhase = 1;
    
    // ===== ENHANCED ELITE RUNE DRAGON FORCE TALK MESSAGES =====
    private static final String[] AWAKENING_MESSAGES = {
        "You dare challenge the master of rune magic!",
        "I am forged from the essence of ancient runes!",
        "Your mortal magic is nothing before mine!",
        "Witness the power of concentrated rune energy!"
    };
    
    private static final String[] RUNE_MASTERY_MESSAGES = {
        "Behold the true power of rune magic!",
        "I command the fundamental forces!",
        "Ancient runes flow through my very being!",
        "The runic arts obey my every whim!"
    };
    
    private static final String[] PHASE_TRANSITION_MESSAGES = {
        "You have pushed me to my limits!",
        "Now you face my enhanced form!",
        "The runes themselves empower me!",
        "Enhanced rune mastery activated!"
    };
    
    private static final String[] FINAL_DESPERATION_MESSAGES = {
        "I will not be defeated by mortals!",
        "The ancient powers will not be denied!",
        "My rune magic is eternal!",
        "Ultimate runic transcendence!"
    };
    
    // ===== SCALING-AWARE FORCE TALK MESSAGES =====
    private static final String[] OVERPOWERED_PLAYER_MESSAGES = {
        "Your power rivals the ancient masters!",
        "Enhanced rune magic to match your strength!",
        "The runes respond to your overwhelming might!",
        "Ancient fury awakens to challenge you!"
    };
    
    private static final String[] UNDERPOWERED_PLAYER_MESSAGES = {
        "I shall restrain my true power...",
        "The runes show mercy to the unprepared.",
        "Ancient wisdom counsels restraint.",
        "Your courage is noted, young one."
    };

    /**
     * Enhanced Elite Rune Dragon Combat Session for BossBalancer integration
     */
    private static class EliteRuneDragonCombatSession {
        public final int playerId;
        public final int dragonId;
        public volatile double lockedPlayerPower;
        public volatile double lockedDragonPower;
        public volatile double powerRatio;
        public final long sessionStart;
        public volatile boolean powerLocked;
        public volatile String currentScalingType;
        public volatile int currentPhase;
        public volatile boolean hasFullArmor;

        public EliteRuneDragonCombatSession(int playerId, int dragonId, double playerPower, double dragonPower) {
            this.playerId = playerId;
            this.dragonId = dragonId;
            this.lockedPlayerPower = playerPower;
            this.lockedDragonPower = dragonPower;
            this.powerRatio = playerPower / dragonPower;
            this.sessionStart = System.currentTimeMillis();
            this.powerLocked = false;
            this.currentScalingType = "UNKNOWN";
            this.currentPhase = 1;
            this.hasFullArmor = false;
        }
    }

    @Override
    public int attack(NPC npc, Entity target) {
        // Enhanced null safety checks
        if (npc == null || target == null) {
            return 5; // Default attack delay
        }
        
        final RuneDragon dragon;
        try {
            dragon = (RuneDragon) npc;
        } catch (ClassCastException e) {
            return 5; // Not a proper RuneDragon instance
        }
        
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) {
            return 5; // No combat definitions available
        }
        
        final Player player = target instanceof Player ? (Player) target : null;
        if (player == null) {
            return executeBasicRuneDragonAttack(npc, target, defs);
        }
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize elite rune dragon combat session
        initializeEliteRuneDragonCombatSession(player, dragon);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, dragon);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentEliteRuneDragonGuidance(player, dragon, scaling, defs);
        
        // Monitor scaling changes during combat
        monitorEliteRuneDragonScalingChanges(player, scaling);
        
        // Update dragon phase tracking with v5.0 scaling
        updateIntelligentRuneDragonPhaseTracking(dragon, scaling);
        
        // Provide opening elite dragon strategy advice
        if (!hasGivenOpeningAdvice) {
            provideOpeningEliteRuneDragonStrategy(player, dragon, defs, scaling);
            hasGivenOpeningAdvice = true;
        }
        
        // Check combat phases and provide phase warnings
        checkCombatPhases(dragon, target, defs, scaling);
        
        // Monitor phase transitions
        monitorPhaseTransitions(dragon, scaling);
        
        // Elite dragon-themed force talk with rune lore and scaling awareness
        performRuneDragonForceTalk(dragon, defs, scaling);
        
        // Enhanced attack selection with phase and scaling considerations
        performIntelligentRuneDragonAttackSelection(dragon, target, defs, scaling);
        
        // Provide strategic elite dragon guidance
        provideEliteDragonGuidance(player, dragon, defs, scaling);
        
        return defs.getAttackDelay();
    }
    
    /**
     * NEW v6.0: Initialize elite rune dragon combat session using BossBalancer v5.0
     */
    private void initializeEliteRuneDragonCombatSession(Player player, RuneDragon dragon) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessions.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, dragon);
            
            // Calculate powers for session
            double playerPower = BossBalancer.calculateActualPlayerPower(player);
            double dragonPower = calculateEliteRuneDragonPower(dragon);
            
            // Create session
            EliteRuneDragonCombatSession session = new EliteRuneDragonCombatSession(
                player.getIndex(), dragon.getIndex(), playerPower, dragonPower);
            
            combatSessions.put(sessionKey, session);
            lastScalingType.put(sessionKey, "UNKNOWN");
            educationalTipCount.put(sessionKey, Integer.valueOf(0));
            warningCount.put(sessionKey, Integer.valueOf(0));
            
            // Send v5.0 enhanced elite rune dragon combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, dragon);
            String welcomeMsg = getIntelligentEliteRuneDragonWelcomeMessage(scaling, dragon);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for elite rune dragon combat
            performInitialEliteRuneDragonArmorAnalysis(player);
            
            // Auto-configure boss if not configured
            if (!BossBalancer.isBossConfigured(dragon.getId())) {
                BossBalancer.autoConfigureBoss(dragon.getId(), ELITE_RUNE_DRAGON_DEFAULT_TIER, 
                                              ELITE_RUNE_DRAGON_BOSS_TYPE, "EliteRuneDragonCombat", false);
            }
        }
    }
    
    /**
     * NEW v6.0: Calculate elite rune dragon power based on phase and tier
     */
    private double calculateEliteRuneDragonPower(RuneDragon dragon) {
        if (dragon == null) {
            return 1.0;
        }

        try {
            int dragonTier = determineEliteRuneDragonTier(dragon, dragon.getCombatDefinitions());
            double basePower = Math.pow(dragonTier, 1.4); // Same as BossBalancer formula
            
            // Phase modifier
            int phase = dragon.getPhase();
            double phaseMultiplier = 1.0 + (phase - 1) * 0.3; // 30% increase per phase
            
            // Elite dragon modifier (rune magic specialists)
            double eliteModifier = 1.25; // 25% bonus for elite status
            
            return basePower * phaseMultiplier * eliteModifier;

        } catch (Exception e) {
            return Math.pow(ELITE_RUNE_DRAGON_DEFAULT_TIER, 1.4);
        }
    }
    
    /**
     * NEW v6.0: Perform initial elite rune dragon armor analysis
     */
    private void performInitialEliteRuneDragonArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=8A2BE2>Elite Rune Dragon Analysis: Exposed flesh detected. Ancient rune magic seeks unprotected areas!</col>");
                player.sendMessage("<col=FF6600>WARNING: Missing armor will result in significantly increased rune damage!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=4169E1>Elite Rune Dragon Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% rune resistance). Ancient magic still penetrates...</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }
    
    /**
     * NEW v6.0: Generate intelligent elite rune dragon welcome message
     */
    private String getIntelligentEliteRuneDragonWelcomeMessage(CombatScaling scaling, RuneDragon dragon) {
        StringBuilder message = new StringBuilder();
        
        message.append("<col=8A2BE2>The Elite Rune Dragon's eyes glow with ancient power, analyzing your runic defenses (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information for rune dragons
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=4169E1>[Ancient rune fury enhanced: +").append(diffIncrease).append("% magical intensity]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Rune dragon restraint: -").append(assistance).append("% spell damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Magical resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full runic protection detected]</col>");
        }
        
        return message.toString();
    }
    
    /**
     * NEW v6.0: Apply HP-aware elite rune dragon damage scaling
     */
    private int applyHPAwareEliteRuneDragonDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (elite rune dragons are magical focused)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on elite rune dragon attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "elder_rune":
                case "ultimate_disruption":
                    damagePercent = ELDER_DAMAGE_PERCENT;
                    break;
                case "ancient_rune_breath":
                case "enhanced_dragonfire":
                    damagePercent = DRAGONFIRE_DAMAGE_PERCENT;
                    break;
                case "rune_disruption":
                case "magical_disruption":
                    damagePercent = DISRUPTION_DAMAGE_PERCENT;
                    break;
                case "critical_rune_magic":
                case "rune_storm":
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
            
            // Additional safety check - never deal more than 75% of current HP for elite rune dragons
            if (currentHP > 0) {
                int emergencyCap = (int)(currentHP * 0.75);
                safeDamage = Math.min(safeDamage, emergencyCap);
            }
            
            return safeDamage;
            
        } catch (Exception e) {
            // Fallback to absolute cap
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
    }
    
    /**
     * NEW v6.0: Send HP warning if player is in danger from elite rune dragon attacks
     */
    private void checkAndWarnLowHPForEliteRuneDragon(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming rune damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.65) {
                    player.sendMessage("<col=8A2BE2>ELITE RUNE WARNING: This ancient spell will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.45) {
                    player.sendMessage("<col=4169E1>RUNE WARNING: Powerful magic incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }
    
    /**
     * Enhanced opening elite dragon strategy advice with v5.0 scaling
     */
    private void provideOpeningEliteRuneDragonStrategy(Player player, RuneDragon dragon, NPCCombatDefinitions defs, CombatScaling scaling) {
        int dragonTier = determineEliteRuneDragonTier(dragon, defs);
        
        player.getPackets().sendGameMessage("<col=8A2BE2>[Elite Rune Dragon]: Master of ancient rune magic with devastating multi-phase combat!</col>");
        player.getPackets().sendGameMessage("<col=8A2BE2>[Combat Analysis v5.0]: Tier " + dragonTier + " Hybrid Boss - Uses all combat styles plus enhanced dragonfire!</col>");
        player.getPackets().sendGameMessage("<col=00FFFF>[Critical Strategy]: Prepare for phase transitions at 50% health - dragon becomes significantly more powerful!</col>");
        player.getPackets().sendGameMessage("<col=00FFFF>[Tactical Warning]: Range attacks can freeze you, dragonfire bypasses most protection in phase 2!</col>");
        
        // Add scaling-specific advice
        if (scaling.scalingType.contains("ANTI_FARMING")) {
            player.getPackets().sendGameMessage("<col=FF6600>[Power Analysis]: Your equipment is overpowered! Elite dragon enhanced by " + 
                                               (int)((scaling.bossDamageMultiplier - 1.0) * 100) + "% to maintain challenge!</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED")) {
            player.getPackets().sendGameMessage("<col=32CD32>[Power Analysis]: Elite dragon showing restraint (" + 
                                               (int)((1.0 - scaling.bossDamageMultiplier) * 100) + "% reduced) due to your current preparation!</col>");
        }
    }
    
    /**
     * ENHANCED v6.0: Intelligent elite rune dragon guidance with power-based scaling awareness
     */
    private void provideIntelligentEliteRuneDragonGuidance(Player player, RuneDragon dragon, CombatScaling scaling, NPCCombatDefinitions defs) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        long currentTime = System.currentTimeMillis();
        
        // Check if we should provide guidance
        Long lastWarningTimeLong = lastWarningTimeMap.get(playerKey);
        if (lastWarningTimeLong != null && (currentTime - lastWarningTimeLong.longValue()) < WARNING_COOLDOWN) {
            return; // Still in cooldown
        }
        
        Integer currentCount = warningCount.get(playerKey);
        if (currentCount == null) currentCount = Integer.valueOf(0);
        if (currentCount.intValue() >= MAX_WARNINGS_PER_FIGHT) {
            return; // Max warnings reached
        }
        
        // Educational guidance (separate from warnings)
        if (Utils.random(GUIDANCE_FREQUENCY) == 0) {
            sendEducationalEliteRuneDragonGuidance(dragon, player, scaling);
        }
        
        // Get guidance message based on v5.0 intelligent scaling
        String guidanceMessage = getIntelligentEliteRuneDragonGuidanceMessage(player, dragon, scaling, currentCount.intValue());
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarningTimeMap.put(playerKey, Long.valueOf(currentTime));
            warningCount.put(playerKey, Integer.valueOf(currentCount.intValue() + 1));
        }
    }
    
    /**
     * NEW v6.0: Get intelligent elite rune dragon guidance message based on power analysis
     */
    private String getIntelligentEliteRuneDragonGuidanceMessage(Player player, RuneDragon dragon, CombatScaling scaling, int stage) {
        int phase = dragon.getPhase();
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getEliteRuneDragonScalingAnalysisMessage(scaling, dragon);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=8A2BE2>Elite Rune Analysis: Missing armor exposes you to ancient rune magic! Spell damage increased by 25%!</col>";
                } else if (phase >= 2) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME rune fury due to scaling!)" : "";
                    return "<col=4169E1>Elite Rune Analysis: Enhanced phase reached. Rune magic dramatically intensified" + difficultyNote + " Use magical protection!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 2) {
                    return "<col=663399>Elite Rune Analysis: Maximum runic transformation! Ultimate draconic magic unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=8A2BE2>Elite Rune Analysis: Extreme dragon scaling detected! Consider facing higher-tier rune masters!</col>";
                }
                break;
                
            case 3:
                // Fourth warning: Advanced tactics
                return "<col=9370DB>Elite Rune Tactics: Watch for rune disruption and magical storms. Maintain antifire and magic protection!</col>";
        }
        
        return null;
    }
    
    /**
     * NEW v6.0: Get elite rune dragon scaling analysis message
     */
    private String getEliteRuneDragonScalingAnalysisMessage(CombatScaling scaling, RuneDragon dragon) {
        String baseMessage = "<col=DDA0DD>Elite Rune Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>Elite Rune Dragon's ancient fury restrained! Spell damage reduced by " + 
                   assistancePercent + "% due to insufficient magical preparation.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=8A2BE2>Elite Rune Dragon's magical mastery escalated! Spell damage increased by " + 
                   difficultyIncrease + "% due to superior magical defenses.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=4169E1>Balanced runic encounter. Optimal magical resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight magical advantage detected. Elite Rune Dragon's spell intensity increased by " + 
                   difficultyIncrease + "% for balanced challenge.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Runic power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }
    
    /**
     * Enhanced educational guidance system for elite rune dragons
     */
    private void sendEducationalEliteRuneDragonGuidance(RuneDragon dragon, Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer tipCount = educationalTipCount.get(playerKey);
        if (tipCount == null) tipCount = Integer.valueOf(0);
        
        if (tipCount.intValue() >= MAX_WARNINGS_PER_FIGHT) {
            return; // Prevent spam
        }
        
        String[] wisdomMessages = { 
            "Learn from the ancient runes, young mage.", 
            "Each spell teaches mastery of the arcane.",
            "Observe the patterns in runic magic.", 
            "Magical protection serves well against elite dragons.",
            "Antifire shields are essential against rune fire.", 
            "Distance controls magical battlefield dynamics.",
            "Close combat with mages requires courage.", 
            "Preparation separates novices from masters.",
            "Every elite dragon has runic weaknesses.",
            "The runes themselves guide the worthy."
        };

        // Safe array access with bounds checking
        int randomIndex = Math.abs(Utils.random(wisdomMessages.length)) % wisdomMessages.length;
        String wisdom = wisdomMessages[randomIndex];
        
        // Add scaling context to educational messages
        if (scaling != null && scaling.bossDamageMultiplier > 1.5) {
            wisdom += " [This elite dragon is enhanced due to your magical prowess.]";
        } else if (scaling != null && scaling.bossDamageMultiplier < 0.9) {
            wisdom += " [This elite dragon shows restraint due to your current magical preparation.]";
        }
        
        player.sendMessage(wisdom, true);

        try {
            dragon.setNextForceTalk(new ForceTalk(wisdom));
        } catch (Exception e) {
            // ForceTalk failed silently
        }
        
        educationalTipCount.put(playerKey, Integer.valueOf(tipCount.intValue() + 1));
    }
    
    /**
     * NEW v6.0: Monitor elite rune dragon scaling changes during combat
     */
    private void monitorEliteRuneDragonScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getEliteRuneDragonScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }
    
    /**
     * NEW v6.0: Get elite rune dragon scaling change message
     */
    private String getEliteRuneDragonScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Elite Rune Update: Magical balance improved! Draconic restraint reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=8A2BE2>Elite Rune Update: Ancient fury now active due to increased magical power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Elite Rune Update: Magical resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=4169E1>Elite Rune Update: Magical protection restored! Dragon spell scaling normalized.</col>";
        }
        
        return null;
    }
    
    /**
     * ENHANCED v6.0: Intelligent elite rune dragon phase tracking with BossBalancer integration
     */
    private void updateIntelligentRuneDragonPhaseTracking(RuneDragon dragon, CombatScaling scaling) {
        Integer dragonKey = Integer.valueOf(dragon.getIndex());
        int newPhase = dragon.getPhase();
        
        if (newPhase > lastKnownPhase) {
            lastKnownPhase = newPhase;
            handleIntelligentEliteRuneDragonPhaseTransition(dragon, newPhase, scaling);
        }
    }
    
    /**
     * ENHANCED v6.0: Intelligent elite rune dragon phase transitions with scaling integration
     */
    private void handleIntelligentEliteRuneDragonPhaseTransition(RuneDragon dragon, int newPhase, CombatScaling scaling) {
        switch (newPhase) {
        case 2:
            String phase2Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED RUNIC MASTERY UNLEASHED!" : "Enhanced rune magic flows through me!";
            dragon.setNextForceTalk(new ForceTalk(phase2Message));
            dragon.setNextGraphics(new Graphics(2346)); // Enhanced rune energy
            break;
        }
    }
    
    /**
     * Check combat phases and provide phase warnings with scaling awareness
     */
    private void checkCombatPhases(RuneDragon dragon, Entity target, NPCCombatDefinitions defs, CombatScaling scaling) {
        double healthPercent = (double) dragon.getHitpoints() / defs.getHitpoints();
        
        if (healthPercent <= FINAL_DESPERATION_THRESHOLD && !hasGivenDesperationWarning && target instanceof Player) {
            Player player = (Player) target;
            String intensityNote = scaling.bossDamageMultiplier > 2.0 ? " (MAXIMUM INTENSITY DUE TO SCALING!)" : "";
            player.getPackets().sendGameMessage("<col=FF0000>[FINAL DESPERATION]: The Elite Rune Dragon unleashes maximum power!" + intensityNote + "</col>");
            player.getPackets().sendGameMessage("<col=FF0000>[CRITICAL PHASE]: All runic abilities at peak strength - expect devastating spells!</col>");
            player.getPackets().sendGameMessage("<col=FF0000>[ULTIMATE THREAT]: Ancient rune magic reaches its zenith!</col>");
            hasGivenDesperationWarning = true;
            
        } else if (healthPercent <= PHASE_TRANSITION_THRESHOLD && !hasGivenPhaseWarning && target instanceof Player) {
            Player player = (Player) target;
            String enhancementNote = scaling.bossDamageMultiplier > 1.5 ? " with scaling enhancement" : "";
            player.getPackets().sendGameMessage("<col=FF8000>[PHASE TRANSITION]: Elite Rune Dragon enters enhanced combat mode" + enhancementNote + "!</col>");
            player.getPackets().sendGameMessage("<col=FF8000>[Enhanced Abilities]: Increased dragonfire power, higher freeze chance, stronger spells!</col>");
            player.getPackets().sendGameMessage("<col=FF8000>[Strategic Update]: Dragonfire protection less effective, rune disruption more frequent!</col>");
            hasGivenPhaseWarning = true;
            
        } else if (healthPercent <= RUNE_MASTERY_THRESHOLD && !hasGivenMasteryWarning && target instanceof Player) {
            Player player = (Player) target;
            String scalingNote = scaling.bossDamageMultiplier > 1.3 ? " (enhanced by scaling)" : "";
            player.getPackets().sendGameMessage("<col=FFFF00>[Rune Mastery]: The dragon's magical abilities intensify" + scalingNote + "!</col>");
            player.getPackets().sendGameMessage("<col=FFFF00>[Enhanced Magic]: More frequent rune disruption and stronger magical attacks!</col>");
            player.getPackets().sendGameMessage("<col=FFFF00>[Combat Advisory]: Prepare for enhanced freeze effects and rune manipulation!</col>");
            hasGivenMasteryWarning = true;
        }
    }
    
    /**
     * Monitor phase transitions for enhanced mechanics with scaling awareness
     */
    private void monitorPhaseTransitions(RuneDragon dragon, CombatScaling scaling) {
        int currentPhase = dragon.getPhase();
        if (currentPhase != lastKnownPhase) {
            phaseTransitionCount++;
            lastKnownPhase = currentPhase;
            
            // Enhanced visual effects for phase transition with scaling intensity
            int graphicsId = scaling.bossDamageMultiplier > 2.0 ? 2348 : 2346; // More intense graphics for high scaling
            dragon.setNextGraphics(new Graphics(graphicsId));
            dragon.playSound(1350, 3); // Magical transformation sound
        }
    }
    
    /**
     * Perform rune dragon-themed force talk based on combat phase and scaling
     */
    private void performRuneDragonForceTalk(RuneDragon dragon, NPCCombatDefinitions defs, CombatScaling scaling) {
        if (Utils.getRandom(15) != 0) return; // 1 in 15 chance for force talk
        
        double healthPercent = (double) dragon.getHitpoints() / defs.getHitpoints();
        String[] messageArray;
        int soundBase;
        
        // Select messages and sounds based on current phase, health, and scaling
        if (healthPercent <= FINAL_DESPERATION_THRESHOLD) {
            messageArray = FINAL_DESPERATION_MESSAGES;
            soundBase = 1356; // Desperate, powerful dragon sounds
        } else if (healthPercent <= PHASE_TRANSITION_THRESHOLD) {
            messageArray = PHASE_TRANSITION_MESSAGES;
            soundBase = 1354;
        } else if (healthPercent <= RUNE_MASTERY_THRESHOLD) {
            messageArray = RUNE_MASTERY_MESSAGES;
            soundBase = 1352;
        } else {
            // Choose message array based on scaling
            if (scaling.scalingType.contains("ANTI_FARMING")) {
                messageArray = OVERPOWERED_PLAYER_MESSAGES;
            } else if (scaling.scalingType.contains("UNDERPOWERED")) {
                messageArray = UNDERPOWERED_PLAYER_MESSAGES;
            } else {
                messageArray = AWAKENING_MESSAGES;
            }
            soundBase = 1350;
        }
        
        String message = messageArray[Utils.getRandom(messageArray.length)];
        dragon.setNextForceTalk(new ForceTalk(message));
        dragon.playSound(soundBase + Utils.getRandom(2), 3); // Elite dragon sound effects
    }
    
    /**
     * Perform intelligent rune dragon attack selection with phase, scaling, and HP-aware considerations
     */
    private void performIntelligentRuneDragonAttackSelection(RuneDragon dragon, Entity target, NPCCombatDefinitions defs, CombatScaling scaling) {
        // Determine range and available attacks
        boolean inMeleeRange = Utils.isOnRange(dragon.getX(), dragon.getY(), dragon.getSize(), 
                                             target.getX(), target.getY(), target.getSize(), 0);
        
        int attackOptions = inMeleeRange ? MELEE_RANGE_ATTACKS : DISTANT_RANGE_ATTACKS;
        int attackChoice = Utils.random(attackOptions);
        
        // Enhanced rune disruption chance check with scaling awareness
        double healthPercent = (double) dragon.getHitpoints() / dragon.getMaxHitpoints();
        int disruptionChance = (dragon.getPhase() == 2 || healthPercent <= RUNE_MASTERY_THRESHOLD) ? 
                              ENHANCED_DISRUPTION_CHANCE : RUNE_DISRUPTION_CHANCE;
        
        // Increase disruption chance for high scaling
        if (scaling.bossDamageMultiplier > 1.5) {
            disruptionChance = Math.max(5, disruptionChance - 3); // More frequent disruption
        }
        
        if (Utils.getRandom(disruptionChance) == 0) {
            performRuneDisruptionAttack(dragon, target, defs, scaling);
            return;
        }
        
        // Standard attack selection with phase and scaling enhancements
        switch (attackChoice) {
            case 3: // melee (only available in melee range)
                if (dragon.getPhase() != 2) {
                    meleeAttack(dragon, target, scaling);
                } else {
                    dragonFireAttack(dragon, target, scaling);
                }
                break;
            case 2: // magic
                mageAttack(dragon, target, scaling);
                break;
            case 1: // range
                rangeAttack(dragon, target, scaling);
                break;
            case 0: // dragonfire
            default:
                dragonFireAttack(dragon, target, scaling);
                break;
        }
    }
    
    /**
     * Enhanced melee attack with Boss Balancer and HP-aware scaling
     */
    private void meleeAttack(RuneDragon dragon, Entity target, CombatScaling scaling) {
        NPCCombatDefinitions defs = dragon.getCombatDefinitions();
        dragon.setNextAnimation(new Animation(defs.getAttackEmote()));
        
        // Get Boss Balancer stats
        int dragonTier = determineEliteRuneDragonTier(dragon, defs);
        int baseMaxHit = getBaseMaxHit(dragon, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, dragonTier, false); // Melee attack
        
        // Apply BossBalancer scaling
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(dragon, (Player) target, tierScaledMaxHit);
        
        // Apply HP-aware damage scaling if target is player
        if (target instanceof Player) {
            scaledDamage = applyHPAwareEliteRuneDragonDamageScaling(scaledDamage, (Player) target, "melee_claw");
        }
        
        int damage = getRandomMaxHit(dragon, scaledDamage, NPCCombatDefinitions.MELEE, target);
        totalDamageDealt += damage;
        delayHit(dragon, 0, target, getMeleeHit(dragon, damage));
        
        // Enhanced melee visual effect
        target.setNextGraphics(new Graphics(2347)); // Rune impact effect
    }
    
    /**
     * Enhanced range attack with advanced freeze mechanics and HP-aware scaling
     */
    private void rangeAttack(RuneDragon dragon, Entity target, CombatScaling scaling) {
        NPCCombatDefinitions defs = dragon.getCombatDefinitions();
        dragon.setNextAnimation(new Animation(dragon.getPhase() == 2 ? 26530 : 26524));
        World.sendProjectile(dragon, target, 16, (dragon.getPhase() == 2 ? 56 : 28), 16, 35, 20, 16, 0);
        
        // Get Boss Balancer stats
        int dragonTier = determineEliteRuneDragonTier(dragon, defs);
        int baseMaxHit = getBaseMaxHit(dragon, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, dragonTier, false); // Range attack
        
        // Apply BossBalancer scaling
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(dragon, (Player) target, tierScaledMaxHit);
        
        // Apply HP-aware damage scaling if target is player
        if (target instanceof Player) {
            scaledDamage = applyHPAwareEliteRuneDragonDamageScaling(scaledDamage, (Player) target, "rune_arrow");
            checkAndWarnLowHPForEliteRuneDragon((Player) target, scaledDamage);
        }
        
        int damage = getRandomMaxHit(dragon, scaledDamage, NPCCombatDefinitions.RANGE, target);
        
        // Enhanced freeze mechanics with tier, phase, and scaling
        int freezeChance = (dragon.getPhase() == 2) ? ENHANCED_FREEZE_CHANCE : FREEZE_CHANCE;
        
        // Adjust freeze chance based on scaling
        if (scaling.bossDamageMultiplier > 1.5) {
            freezeChance = Math.max(1, freezeChance - 1); // Higher chance for enhanced dragons
        }
        
        if (Utils.random(freezeChance) == 0) {
            freezeAttackCount++;
            
            // Tier and scaling-based bonus damage for freeze attack
            int bonusDamage = Math.max(30, dragonTier * 18); // Minimum 30, scales with tier
            if (scaling.bossDamageMultiplier > 1.5) {
                bonusDamage = (int)(bonusDamage * 1.3); // 30% bonus for enhanced scaling
            }
            damage += Utils.random(1, bonusDamage);
            
            if (target instanceof Player) {
                Player player = (Player) target;
                player.sendMessage("<col=8A2BE2>[Rune Binding]: You've been frozen by ancient rune magic!", true);
                
                // Tier, phase, and scaling-based freeze duration
                int baseDuration = 3000; // 3 seconds base
                int tierBonus = dragonTier * 200; // 200ms per tier
                int phaseBonus = (dragon.getPhase() == 2) ? 1000 : 0; // 1 second bonus in phase 2
                int scalingBonus = scaling.bossDamageMultiplier > 1.5 ? 500 : 0; // 0.5 second for high scaling
                int freezeDuration = baseDuration + tierBonus + phaseBonus + scalingBonus;
                
                target.setFreezeDelay(freezeDuration);
                target.setNextGraphics(new Graphics(2348)); // Rune freeze effect
                
                // Apply HP-aware damage scaling for freeze bonus
                damage = applyHPAwareEliteRuneDragonDamageScaling(damage, player, "rune_freeze");
            }
        }
        
        totalDamageDealt += damage;
        delayHit(dragon, 1, target, getRangeHit(dragon, damage));
    }
    
    /**
     * Enhanced magic attack with Boss Balancer and HP-aware scaling
     */
    private void mageAttack(RuneDragon dragon, Entity target, CombatScaling scaling) {
        NPCCombatDefinitions defs = dragon.getCombatDefinitions();
        dragon.setNextAnimation(new Animation(dragon.getPhase() == 2 ? 26530 : 26525));
        World.sendProjectile(dragon, target, 2735, (dragon.getPhase() == 2 ? 56 : 28), 16, 35, 20, 16, 0);
        
        // Get Boss Balancer stats
        int dragonTier = determineEliteRuneDragonTier(dragon, defs);
        int baseMaxHit = getBaseMaxHit(dragon, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, dragonTier, true); // Magic attack
        
        // Apply BossBalancer scaling
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(dragon, (Player) target, tierScaledMaxHit);
        
        // Apply HP-aware damage scaling if target is player
        if (target instanceof Player) {
            scaledDamage = applyHPAwareEliteRuneDragonDamageScaling(scaledDamage, (Player) target, "rune_magic");
            checkAndWarnLowHPForEliteRuneDragon((Player) target, scaledDamage);
        }
        
        int damage = getRandomMaxHit(dragon, scaledDamage, NPCCombatDefinitions.MAGE, target);
        totalDamageDealt += damage;
        delayHit(dragon, 1, target, getMagicHit(dragon, damage));
        
        // Enhanced magic visual effect
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                target.setNextGraphics(new Graphics(2349)); // Rune magic impact
                this.stop();
            }
        }, 1);
    }
    
    /**
     * Enhanced dragonfire attack with phase-based protection scaling and HP-aware damage
     */
    private void dragonFireAttack(RuneDragon dragon, Entity target, CombatScaling scaling) {
        final Player player = target instanceof Player ? (Player) target : null;
        dragonFireCount++;
        
        // Get Boss Balancer stats
        int dragonTier = determineEliteRuneDragonTier(dragon, dragon.getCombatDefinitions());
        int baseMaxHit = getBaseMaxHit(dragon, dragon.getCombatDefinitions());
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, dragonTier, true); // Magic-based dragonfire
        
        // Elite dragon fire base damage (135% of tier-scaled max hit)
        int damage = (int)(tierScaledMaxHit * 1.35);
        
        // Apply BossBalancer scaling
        if (player != null) {
            damage = BossBalancer.calculateNPCDamageToPlayer(dragon, player, damage);
            
            String message = Combat.getProtectMessage(player);
            if (message != null) {
                player.sendMessage(message, true);
                
                // Phase and scaling-based protection effectiveness
                double protectionReduction = 1.0;
                if (message.contains("fully")) {
                    protectionReduction = dragon.getPhase() == 2 ? 0.08 : 0.02; // Less effective in phase 2
                    if (scaling.bossDamageMultiplier > 1.5) {
                        protectionReduction *= 1.5; // High scaling reduces protection effectiveness
                    }
                } else if (message.contains("most")) {
                    protectionReduction = dragon.getPhase() == 2 ? 0.15 : 0.08;
                    if (scaling.bossDamageMultiplier > 1.5) {
                        protectionReduction *= 1.3;
                    }
                } else if (message.contains("some")) {
                    protectionReduction = dragon.getPhase() == 2 ? 0.25 : 0.15;
                    if (scaling.bossDamageMultiplier > 1.5) {
                        protectionReduction *= 1.2;
                    }
                }
                
                damage = (int)(damage * protectionReduction);
            }
            
            // Apply HP-aware damage scaling
            damage = applyHPAwareEliteRuneDragonDamageScaling(damage, player, "enhanced_dragonfire");
            checkAndWarnLowHPForEliteRuneDragon(player, damage);
            
            if (damage > 0) {
                String enhancedText = dragon.getPhase() == 2 ? " enhanced" : "";
                String scalingText = scaling.bossDamageMultiplier > 1.5 ? " intensified" : "";
                player.sendMessage("<col=FF0000>[Elite Dragon Fire]: You are hit by the dragon's" + enhancedText + scalingText + " fiery breath!</col>", true);
            }
        }
        
        dragon.setNextAnimation(new Animation(dragon.getPhase() == 2 ? 26530 : 14245));
        World.sendProjectile(dragon, target, 393, (dragon.getPhase() == 2 ? 56 : 28), 16, 35, 20, 16, 0);
        totalDamageDealt += damage;
        delayHit(dragon, 1, target, getRegularHit(dragon, damage));
        
        // Enhanced dragonfire visual effect
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                target.setNextGraphics(new Graphics(2350)); // Elite dragonfire impact
                this.stop();
            }
        }, 1);
    }
    
    /**
     * Perform rune disruption attack with magical interference and HP-aware scaling
     */
    private void performRuneDisruptionAttack(RuneDragon dragon, Entity target, NPCCombatDefinitions defs, CombatScaling scaling) {
        runeDisruptionCount++;
        
        // Enhanced rune disruption animation
        dragon.setNextAnimation(new Animation(dragon.getPhase() == 2 ? 26530 : 26525));
        dragon.setNextGraphics(new Graphics(2351)); // Rune disruption effect
        
        // Get Boss Balancer stats for disruption attack
        int dragonTier = determineEliteRuneDragonTier(dragon, defs);
        int baseMaxHit = getBaseMaxHit(dragon, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, dragonTier, true); // Magic-based disruption
        
        // Disruption attack does 80% damage but has special effects (enhanced for elite)
        int disruptionDamage = (int)(tierScaledMaxHit * 0.80);
        
        // Apply BossBalancer scaling
        if (target instanceof Player) {
            disruptionDamage = BossBalancer.calculateNPCDamageToPlayer(dragon, (Player) target, disruptionDamage);
            disruptionDamage = applyHPAwareEliteRuneDragonDamageScaling(disruptionDamage, (Player) target, "rune_disruption");
            checkAndWarnLowHPForEliteRuneDragon((Player) target, disruptionDamage);
        }
        
        // Enhanced rune disruption projectile
        World.sendProjectile(dragon, target, 2352, (dragon.getPhase() == 2 ? 56 : 28), 16, 35, 20, 16, 0);
        int damage = getRandomMaxHit(dragon, disruptionDamage, NPCCombatDefinitions.MAGE, target);
        totalDamageDealt += damage;
        delayHit(dragon, 2, target, getMagicHit(dragon, damage));
        
        // Apply rune disruption effects
        if (target instanceof Player) {
            applyRuneDisruptionEffects((Player) target, dragonTier, dragon.getPhase(), scaling);
        }
        
        // Enhanced disruption graphics
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                target.setNextGraphics(new Graphics(2353)); // Rune disruption impact
                this.stop();
            }
        }, 2);
    }
    
    /**
     * Apply rune disruption effects scaled by tier, phase, and BossBalancer scaling
     */
    private void applyRuneDisruptionEffects(final Player player, final int tier, final int phase, final CombatScaling scaling) {
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Rune disruption affects magic-related stats and abilities
                int disruptionType = Utils.getRandom(3);
                
                if (disruptionType == 0) {
                    // Magic level disruption
                    int magicLevel = player.getSkills().getLevel(Skills.MAGIC);
                    int disruption = Math.max(6, tier * 2); // Minimum 6, scales with tier
                    if (phase == 2) disruption += 4; // Bonus disruption in phase 2
                    if (scaling.bossDamageMultiplier > 1.5) disruption += 2; // Scaling bonus
                    
                    int newLevel = Math.max(1, magicLevel - disruption);
                    player.getSkills().set(Skills.MAGIC, newLevel);
                    player.getPackets().sendGameMessage("<col=8A2BE2>[Elite Rune Disruption]: Your magic has been severely disrupted by ancient rune energy!</col>");
                    
                } else if (disruptionType == 1) {
                    // Prayer disruption (enhanced for elite)
                    int currentPrayer = player.getPrayer().getPrayerpoints();
                    int maxPrayer = player.getSkills().getLevelForXp(Skills.PRAYER);
                    
                    double drainPercent = 0.25 + (tier - 1) * 0.04; // 25% + 4% per tier above 1
                    if (phase == 2) drainPercent += 0.12; // 12% bonus in phase 2
                    if (scaling.bossDamageMultiplier > 1.5) drainPercent += 0.08; // Scaling bonus
                    drainPercent = Math.min(drainPercent, 0.50); // Cap at 50%
                    
                    int drainAmount = (int)(maxPrayer * drainPercent);
                    drainAmount = Math.min(drainAmount, currentPrayer);
                    
                    player.getPrayer().drainPrayer(drainAmount);
                    player.getPackets().sendGameMessage("<col=8A2BE2>[Elite Rune Disruption]: The ancient runes severely interfere with your prayers!</col>");
                    
                } else {
                    // Defence and runecrafting disruption (enhanced)
                    int defenceLevel = player.getSkills().getLevel(Skills.DEFENCE);
                    int runecraftingLevel = player.getSkills().getLevel(Skills.RUNECRAFTING);
                    
                    int statDrain = Math.max(4, tier / 2); // Minimum 4, scales with tier
                    if (phase == 2) statDrain += 3; // Bonus drain in phase 2
                    if (scaling.bossDamageMultiplier > 1.5) statDrain += 2; // Scaling bonus
                    
                    player.getSkills().set(Skills.DEFENCE, Math.max(1, defenceLevel - statDrain));
                    player.getSkills().set(Skills.RUNECRAFTING, Math.max(1, runecraftingLevel - statDrain));
                    
                    player.getPackets().sendGameMessage("<col=8A2BE2>[Elite Rune Disruption]: Your defensive and runecrafting abilities have been severely weakened!</col>");
                }
                
                this.stop();
            }
        }, 2);
    }
    
    /**
     * Execute basic dragon attack for non-player targets
     */
    private int executeBasicRuneDragonAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
        int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
        npc.setNextAnimation(new Animation(defs.getAttackEmote()));
        delayHit(npc, 0, target, getMeleeHit(npc, damage));
        return defs.getAttackDelay();
    }
    
    /**
     * Determine Elite Rune Dragon's tier based on Boss Balancer system
     */
    private int determineEliteRuneDragonTier(RuneDragon dragon, NPCCombatDefinitions defs) {
        try {
            int hp = defs.getHitpoints();
            int maxHit = defs.getMaxHit();
            
            // Estimate tier based on Boss Balancer HP/damage ranges for Hybrid boss
            if (hp >= 10000 && hp <= 17000 && maxHit >= 105 && maxHit <= 185) {
                return 6; // Master tier
            } else if (hp >= 6000 && hp <= 10500 && maxHit >= 70 && maxHit <= 125) {
                return 5; // Expert tier
            } else if (hp >= 15000 && hp <= 25000 && maxHit >= 150 && maxHit <= 260) {
                return 7; // Elite tier
            } else if (hp >= 25000 && hp <= 35000 && maxHit >= 200 && maxHit <= 350) {
                return 8; // Legendary tier
            }
            
            return ELITE_RUNE_DRAGON_DEFAULT_TIER; // Default to Master tier
        } catch (Exception e) {
            return ELITE_RUNE_DRAGON_DEFAULT_TIER;
        }
    }
    
    /**
     * Get base max hit safely (NULL SAFE)
     */
    private int getBaseMaxHit(RuneDragon dragon, NPCCombatDefinitions defs) {
        try {
            int maxHit = defs.getMaxHit();
            return maxHit > 0 ? maxHit : 180; // Default Elite Rune Dragon damage if invalid
        } catch (Exception e) {
            return 180; // Fallback Elite Rune Dragon damage
        }
    }
    
    /**
     * Apply Boss Balancer tier scaling for hybrid boss (enhanced for elite rune dragons)
     */
    private int applyBossTierScaling(int baseMaxHit, int tier, boolean isMagicAttack) {
        // Boss Balancer tier scaling: 15% increase per tier above 1
        double tierMultiplier = 1.0 + (tier - 1) * 0.15;
        
        // Hybrid boss type modifier - balanced damage for all attack types
        double typeModifier = 1.0; // Standard damage for hybrid
        
        // Elite Rune Dragons have enhanced magic preference (rune magic specialists)
        if (isMagicAttack) {
            typeModifier = 1.18; // 18% bonus for magic attacks (enhanced from original 12%)
        }
        
        return (int) (baseMaxHit * tierMultiplier * typeModifier);
    }
    
    /**
     * Provide strategic elite dragon guidance based on combat performance and scaling
     */
    private void provideEliteDragonGuidance(Player player, RuneDragon dragon, NPCCombatDefinitions defs, CombatScaling scaling) {
        if (!shouldGiveHint()) return;
        
        double healthPercent = (double) dragon.getHitpoints() / defs.getHitpoints();
        
        // Freeze attack frequency guidance
        if (freezeAttackCount >= 3) {
            String scalingNote = scaling.bossDamageMultiplier > 1.5 ? " (enhanced by scaling)" : "";
            player.getPackets().sendGameMessage("<col=8A2BE2>[Freeze Analysis]: Multiple freeze attacks detected" + scalingNote + "! Consider freedom abilities or teleportation!</col>");
            freezeAttackCount = 0;
            return;
        }
        
        // Dragon fire frequency guidance
        if (dragonFireCount >= 4) {
            String scalingNote = scaling.bossDamageMultiplier > 1.5 ? " (intensified by scaling)" : "";
            player.getPackets().sendGameMessage("<col=FF0000>[Elite Dragon Fire]: Frequent dragonfire attacks" + scalingNote + "! Ensure maximum protection is active!</col>");
            dragonFireCount = 0;
            return;
        }
        
        // Rune disruption guidance
        if (runeDisruptionCount >= 2) {
            String scalingNote = scaling.bossDamageMultiplier > 1.5 ? " (amplified by scaling)" : "";
            player.getPackets().sendGameMessage("<col=8A2BE2>[Rune Disruption]: Multiple disruption attacks used" + scalingNote + ". Monitor your magic and prayer levels!</col>");
            runeDisruptionCount = 0;
            return;
        }
        
        // Phase transition guidance
        if (phaseTransitionCount > 0 && dragon.getPhase() == 2) {
            String scalingNote = scaling.bossDamageMultiplier > 1.5 ? " with scaling enhancement" : "";
            player.getPackets().sendGameMessage("<col=FF8000>[Phase 2 Active]: Enhanced combat mode" + scalingNote + " - all abilities are stronger and more frequent!</col>");
            phaseTransitionCount = 0; // Reset after informing
            return;
        }
        
        // Phase-specific elite dragon guidance with scaling awareness
        if (healthPercent > 0.75) {
            String powerNote = scaling.scalingType.contains("ANTI_FARMING") ? " (power enhanced)" : 
                              scaling.scalingType.contains("UNDERPOWERED") ? " (power restrained)" : "";
            player.getPackets().sendGameMessage("<col=00FFFF>[Elite Strategy]: Elite Rune Dragon uses 4 attack types plus rune disruption" + powerNote + ". Prepare for all combat styles!</col>");
        } else if (healthPercent > 0.50) {
            String intensityNote = scaling.bossDamageMultiplier > 1.3 ? " (enhanced intensity)" : "";
            player.getPackets().sendGameMessage("<col=FFFF00>[Rune Mastery]: Enhanced magical abilities incoming" + intensityNote + ". Watch for increased disruption effects!</col>");
        } else if (healthPercent > 0.25) {
            String enhancementNote = scaling.bossDamageMultiplier > 1.5 ? " (maximum enhancement)" : "";
            player.getPackets().sendGameMessage("<col=FF8000>[Phase 2]: Enhanced combat mode activated" + enhancementNote + ". All abilities significantly more dangerous!</col>");
        } else {
            String ultimateNote = scaling.bossDamageMultiplier > 2.0 ? " (ULTIMATE POWER)" : "";
            player.getPackets().sendGameMessage("<col=FF0000>[Final Desperation]: Elite Rune Dragon at maximum power" + ultimateNote + ". Ultimate protection required!</col>");
        }
    }
    
    /**
     * Check if should give strategic hint (with cooldown)
     */
    private boolean shouldGiveHint() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastHintTime >= HINT_COOLDOWN) {
            if (Utils.getRandom(GUIDANCE_FREQUENCY) == 0) {
                lastHintTime = currentTime;
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if should give phase warning (with cooldown)
     */
    private boolean shouldGivePhaseWarning() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPhaseWarningTime >= PHASE_WARNING_COOLDOWN) {
            lastPhaseWarningTime = currentTime;
            return true;
        }
        return false;
    }
    
    /**
     * Debug method for testing damage scaling and boss balancer integration
     */
    public String getDamageScalingInfo(int combatLevel, boolean isMagic, int phase, CombatScaling scaling) {
        int tier = ELITE_RUNE_DRAGON_DEFAULT_TIER;
        int baseMaxHit = 180;
        int tierScaled = applyBossTierScaling(baseMaxHit, tier, isMagic);
        String attackType = isMagic ? "Magic" : "Physical";
        String scalingInfo = scaling != null ? String.format(" | Scaling: %.2fx", scaling.bossDamageMultiplier) : "";
        
        return String.format("Elite Rune Dragon Tier: %d, Base: %d, %s Scaled: %d, Phase: %d%s", 
                           tier, baseMaxHit, attackType, tierScaled, phase, scalingInfo);
    }
    
    /**
     * Get combat statistics for elite dragon analysis
     */
    public String getCombatStats() {
        return String.format("Freezes: %d, Dragon Fire: %d, Rune Disruption: %d, Phase Transitions: %d, Total Damage: %d", 
                           freezeAttackCount, dragonFireCount, runeDisruptionCount, phaseTransitionCount, totalDamageDealt);
    }
    
    /**
     * NEW v6.0: Handle elite rune dragon combat end with proper BossBalancer cleanup
     */
    public static void onEliteRuneDragonCombatEnd(Player player, RuneDragon dragon) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            
            combatSessions.remove(playerKey);
            lastWarningTimeMap.remove(playerKey);
            warningCount.remove(playerKey);
            lastScalingType.remove(playerKey);
            educationalTipCount.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=8A2BE2>Elite Rune Dragon combat session ended. Runic scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("EliteRuneDragon: Error ending v5.0 combat session: " + e.getMessage());
        }
    }
    
    /**
     * NEW v6.0: Handle prayer changes during elite rune dragon combat
     */
    public static void onPlayerPrayerChanged(Player player) {
        if (player == null) return;
        
        try {
            Integer playerKey = Integer.valueOf(player.getIndex());
            
            // Only handle if in active combat session
            if (combatSessions.containsKey(playerKey)) {
                // Notify BossBalancer v5.0 of prayer change
                BossBalancer.onPrayerChanged(player);
                
                // Send update message
                player.sendMessage("<col=DDA0DD>Prayer change detected. Elite Rune Dragon scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("EliteRuneDragon: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }
    
    /**
     * NEW v6.0: Handle equipment changes during elite rune dragon combat
     */
    public static void onPlayerEquipmentChanged(Player player) {
        if (player == null) return;
        
        try {
            Integer playerKey = Integer.valueOf(player.getIndex());
            EliteRuneDragonCombatSession session = combatSessions.get(playerKey);
            
            if (session != null) {
                // Recalculate power if equipment changed
                double newPlayerPower = BossBalancer.calculateActualPlayerPower(player);
                double oldPowerRatio = session.powerRatio;
                double newPowerRatio = newPlayerPower / session.lockedDragonPower;
                
                // Update session data
                session.lockedPlayerPower = newPlayerPower;
                session.powerRatio = newPowerRatio;
                
                // Check armor coverage
                BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
                boolean newArmorStatus = armorResult.hasFullArmor;
                
                if (newArmorStatus != session.hasFullArmor) {
                    session.hasFullArmor = newArmorStatus;
                    if (newArmorStatus) {
                        player.sendMessage("<col=32CD32>Elite Rune Dragon: Full armor protection restored! Spell damage scaling normalized.</col>");
                    } else {
                        player.sendMessage("<col=FF6600>Elite Rune Dragon: Armor protection compromised! Increased spell damage vulnerability!</col>");
                    }
                }
                
                // Notify of significant power changes
                if (Math.abs(newPowerRatio - oldPowerRatio) > 0.3) {
                    String changeType = newPowerRatio > oldPowerRatio ? "increased" : "decreased";
                    player.sendMessage("<col=8A2BE2>Elite Rune Dragon: Combat power " + changeType + 
                                     "! Scaling analysis updated (Ratio: " + String.format("%.2f", newPowerRatio) + ":1)</col>");
                }
            }
        } catch (Exception e) {
            System.err.println("EliteRuneDragon: Error handling equipment change: " + e.getMessage());
        }
    }
    
    /**
     * NEW v6.0: Get current scaling information for elite rune dragon
     */
    public static String getCurrentScalingInfo(Player player) {
        if (player == null) return "No player data";
        
        try {
            Integer playerKey = Integer.valueOf(player.getIndex());
            EliteRuneDragonCombatSession session = combatSessions.get(playerKey);
            
            if (session != null) {
                return String.format("Elite Rune Dragon Scaling - Power Ratio: %.2f:1 | Type: %s | Phase: %d | Full Armor: %s",
                                   session.powerRatio, session.currentScalingType, session.currentPhase, 
                                   session.hasFullArmor ? "YES" : "NO");
            } else {
                return "No active elite rune dragon combat session";
            }
        } catch (Exception e) {
            return "Error retrieving scaling info: " + e.getMessage();
        }
    }
    
    /**
     * NEW v6.0: Force cleanup for elite rune dragon combat (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onEliteRuneDragonCombatEnd(player, null);
        }
    }
    
    /**
     * NEW v6.0: Initialize BossBalancer configuration for Elite Rune Dragon
     */
    public static void initializeEliteRuneDragonBossConfiguration() {
        try {
            // Auto-configure Elite Rune Dragon if not already configured
            // This should be called during server startup
            
            // Example Elite Rune Dragon IDs - adjust based on your NPC IDs
            int[] eliteRuneDragonIds = {
                // Add your actual Elite Rune Dragon NPC IDs here
                // Example: 1234, 1235, 1236
            };
            
            for (int dragonId : eliteRuneDragonIds) {
                if (!BossBalancer.isBossConfigured(dragonId)) {
                    boolean success = BossBalancer.autoConfigureBoss(
                        dragonId, 
                        ELITE_RUNE_DRAGON_DEFAULT_TIER, 
                        ELITE_RUNE_DRAGON_BOSS_TYPE, 
                        "EliteRuneDragonCombat_AutoConfig",
                        true // Save to file for persistence
                    );
                    
                    if (success) {
                        System.out.println("EliteRuneDragon: Auto-configured boss " + dragonId + 
                                         " as Tier " + ELITE_RUNE_DRAGON_DEFAULT_TIER + " Hybrid Boss");
                    }
                }
            }
            
            System.out.println("EliteRuneDragon: BossBalancer v5.0 integration initialization complete");
            
        } catch (Exception e) {
            System.err.println("EliteRuneDragon: Error initializing BossBalancer configuration: " + e.getMessage());
        }
    }
    
    /**
     * NEW v6.0: Get recommended player stats for elite rune dragon
     */
    public static String getRecommendedPlayerStats(int dragonTier) {
        StringBuilder recommendations = new StringBuilder();
        
        recommendations.append("=== ELITE RUNE DRAGON RECOMMENDATIONS ===\n");
        recommendations.append("Tier ").append(dragonTier).append(" Elite Rune Dragon:\n\n");
        
        // Combat level recommendation
        int recommendedCombatLevel = dragonTier * 14 + 30; // Same formula as BossBalancer
        recommendations.append("Recommended Combat Level: ").append(recommendedCombatLevel).append("+\n");
        
        // Specific stat recommendations
        int baseStatRecommendation = dragonTier * 10 + 50;
        recommendations.append("Magic Level: ").append(baseStatRecommendation + 10).append("+ (primary importance)\n");
        recommendations.append("Defence Level: ").append(baseStatRecommendation).append("+\n");
        recommendations.append("Prayer Level: ").append(baseStatRecommendation - 10).append("+ (for protection prayers)\n");
        recommendations.append("Hitpoints: ").append(Math.max(990, baseStatRecommendation - 20)).append("+ (minimum for survival)\n\n");
        
        // Equipment recommendations
        recommendations.append("Essential Equipment:\n");
        recommendations.append("• Full armor set (Tier ").append(Math.max(1, dragonTier - 1)).append("+ recommended)\n");
        recommendations.append("• Antifire shield or potion\n");
        recommendations.append("• Magic protection prayers\n");
        recommendations.append("• High-tier weapon (Tier ").append(dragonTier).append("+ for optimal damage)\n");
        recommendations.append("• Food for healing (recommended: ").append(Math.min(20, dragonTier * 2 + 10)).append("+ healing)\n\n");
        
        // Special notes for elite rune dragons
        recommendations.append("Elite Rune Dragon Special Notes:\n");
        recommendations.append("• Rune disruption affects magic and prayer levels\n");
        recommendations.append("• Phase 2 significantly increases all damage\n");
        recommendations.append("• Freeze attacks can immobilize for extended periods\n");
        recommendations.append("• Enhanced dragonfire bypasses some protection in phase 2\n");
        recommendations.append("• BossBalancer v5.0 scales difficulty based on your total power\n");
        
        return recommendations.toString();
    }

    @Override
    public Object[] getKeys() {
        return new Object[] { 
            "elite rune dragon", 
            "Elite Rune Dragon",
            "rune dragon",
            "Rune Dragon"
            // Add specific NPC IDs if known
            // Example: 1234, 1235, 1236
        };
    }
}