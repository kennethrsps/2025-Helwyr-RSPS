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
 * Enhanced Frost Dragon Combat Script with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Educational combat system, dragonic phases, frost breath mechanics, HP-aware damage caps
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 3.0 - FULL BossBalancer v5.0 Integration with Intelligent Dragon Combat Scaling & HP-Aware System
 */
public class FrostDragonCombat extends CombatScript {

    // ===== DRAGONIC PHASES - Enhanced for v5.0 =====
    private static final double HATCHLING_THRESHOLD = 0.85;      // 85% HP - young dragon
    private static final double MATURE_THRESHOLD = 0.60;         // 60% HP - mature dragon  
    private static final double ANCIENT_THRESHOLD = 0.35;        // 35% HP - ancient dragon
    private static final double ELDER_THRESHOLD = 0.15;          // 15% HP - elder wyrm

    // ===== ENHANCED GUIDANCE SYSTEM - Educational-aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentDragonPhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> educationalTipCount = new ConcurrentHashMap<Integer, Integer>();

    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 90000; // 1.5 minutes between warnings (educational)
    private static final long SCALING_UPDATE_INTERVAL = 20000; // 20 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 2000; // 2 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 5; // More warnings for educational

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - DRAGON BALANCED =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.28; // Max 28% of player HP per hit (dragon fire)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.42;  // Max 42% for ancient breath
    private static final double DRAGONFIRE_DAMAGE_PERCENT = 0.38; // Max 38% for dragonfire
    private static final double ELDER_DAMAGE_PERCENT = 0.45;     // Max 45% for elder techniques
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 420;          // Hard cap (28% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 20;               // Minimum damage to prevent 0 hits

    // ===== DRAGON FIRE PROTECTION AND EFFECTS =====
    private static final int GUIDANCE_CHANCE = 12; // 1 in 12 chance for educational messages
    private static final int MAX_EDUCATIONAL_TIPS_PER_FIGHT = 8; // Prevent spam
    
    // ===== SAFE SPOT PREVENTION - Dragon-themed =====
    private static final Map<Integer, Integer> consecutiveAvoidedBreaths = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastBreathHit = new ConcurrentHashMap<Integer, Boolean>();

    @Override
    public Object[] getKeys() {
        return new Object[] {
            // Frost dragon IDs
            51, 52, 53, 54, 55,
            // Common dragon IDs
            941, 942, // Black dragons
            1974, 1975, // Green dragons
            2642, 2643, // Blue dragons
            3068, 3069, // Red dragons
            9462, 9463, // Frost dragons
            // Register by name
            "Frost dragon", "frost dragon", "Dragon", "dragon"
        };
    }

    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        final Player player = target instanceof Player ? (Player) target : null;

        if (player == null) {
            return executeBasicDragonAttack(npc, target, defs);
        }

        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeDragonCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentDragonGuidance(player, npc, scaling, defs);
        
        // Monitor scaling changes during combat
        monitorDragonScalingChanges(player, scaling);
        
        // Update dragon phase tracking with v5.0 scaling
        updateIntelligentDragonPhaseTracking(npc, scaling);
        
        // Check for dragon-themed safe spotting
        checkDragonSafeSpotting(player, npc, scaling);
        
        // Enhanced dragon taunts with scaling-based frequency
        performEnhancedDragonTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentDragonAttackWithWarning(npc, player, defs, scaling);
    }

    /**
     * Initialize dragon combat session using BossBalancer v5.0
     */
    private void initializeDragonCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            educationalTipCount.put(sessionKey, Integer.valueOf(0));
            consecutiveAvoidedBreaths.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastBreathHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced dragon combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentDragonWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for dragon combat
            performInitialDragonArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial dragon armor analysis
     */
    private void performInitialDragonArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=0066cc>Dragon Analysis: Exposed flesh detected. Dragonfire seeks unprotected skin!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=004080>Dragon Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance). Ancient fire still burns...</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from dragon attacks
     */
    private int applyHPAwareDragonDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (dragon attacks are fire-focused)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on dragon attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "elder_wyrm":
                case "ancient_roar":
                    damagePercent = ELDER_DAMAGE_PERCENT;
                    break;
                case "dragonfire":
                case "ancient_breath":
                    damagePercent = DRAGONFIRE_DAMAGE_PERCENT;
                    break;
                case "frost_breath":
                case "ice_storm":
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
            
            // Additional safety check - never deal more than 78% of current HP for dragons
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
     * NEW v5.0: Send HP warning if player is in danger from dragon attacks
     */
    private void checkAndWarnLowHPForDragon(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming dragon damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.65) {
                    player.sendMessage("<col=ff0000>DRAGON WARNING: This breath will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.45) {
                    player.sendMessage("<col=0066cc>DRAGON WARNING: Heavy dragonfire incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent dragon welcome message based on power analysis
     */
    private String getIntelligentDragonWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Frost Dragon";
        
        message.append("<col=0066cc>").append(npcName).append(" spreads its wings, sensing your combat readiness (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=004080>[Ancient fury enhanced: +").append(diffIncrease).append("% dragonfire intensity]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Dragon restraint: -").append(assistance).append("% breath damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Dragon resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Full dragon protection detected]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent dragon guidance with power-based scaling awareness
     */
    private void provideIntelligentDragonGuidance(Player player, NPC npc, CombatScaling scaling, NPCCombatDefinitions defs) {
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
        
        // Educational guidance (separate from warnings)
        if (Utils.random(GUIDANCE_CHANCE) == 0) {
            sendEducationalGuidance(npc, player, scaling);
        }
        
        // Get guidance message based on v5.0 intelligent scaling
        String guidanceMessage = getIntelligentDragonGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent dragon guidance message based on power analysis
     */
    private String getIntelligentDragonGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentDragonPhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getDragonScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=004080>Dragon Analysis: Missing armor exposes you to dragonfire! Fire damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME dragon fury due to scaling!)" : "";
                    return "<col=0066cc>Dragon Analysis: Ancient phase reached. Dragonfire dramatically intensified" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=003366>Dragon Analysis: Elder wyrm transformation! Maximum draconic power unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=004080>Dragon Analysis: Extreme dragon scaling detected! Consider facing greater wyrms!</col>";
                }
                break;
                
            case 3:
                // Fourth warning: Advanced tactics
                return "<col=0066aa>Dragon Tactics: Watch for breath attacks and ice storms. Maintain antifire protection!</col>";
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get dragon scaling analysis message
     */
    private String getDragonScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Dragon";
        
        String baseMessage = "<col=DDA0DD>Dragon Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>" + npcName + "'s ancient fury restrained! Dragonfire reduced by " + 
                   assistancePercent + "% due to insufficient fire preparation.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=004080>" + npcName + "'s draconic mastery escalated! Fire damage increased by " + 
                   difficultyIncrease + "% due to superior fire defenses.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=0066cc>Balanced dragon encounter. Optimal fire resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight fire advantage detected. " + npcName + "'s draconic intensity increased by " + 
                   difficultyIncrease + "% for balanced challenge.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Dragon power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * Enhanced educational guidance system
     */
    private void sendEducationalGuidance(NPC npc, Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer tipCount = educationalTipCount.get(playerKey);
        if (tipCount == null) tipCount = 0;
        
        if (tipCount >= MAX_EDUCATIONAL_TIPS_PER_FIGHT) {
            return; // Prevent spam
        }
        
        String[] wisdomMessages = { 
            "Learn from my attacks, young warrior.", 
            "Each strike teaches you something new.",
            "Observe and adapt to survive.", 
            "Protection magic serves you well against dragonfire.",
            "Anti-dragon shields are a warrior's wisdom.", 
            "Distance controls the battlefield.",
            "Close combat requires courage and skill.", 
            "Preparation separates the living from the dead.",
            "Every dragon has weaknesses to exploit." 
        };

        // Safe array access with bounds checking
        int randomIndex = Math.abs(Utils.random(wisdomMessages.length)) % wisdomMessages.length;
        String wisdom = wisdomMessages[randomIndex];
        
        // Add scaling context to educational messages
        if (scaling != null && scaling.bossDamageMultiplier > 1.5) {
            wisdom += " [This dragon is enhanced due to your combat prowess.]";
        } else if (scaling != null && scaling.bossDamageMultiplier < 0.9) {
            wisdom += " [This dragon shows restraint due to your current preparation.]";
        }
        
        player.sendMessage(wisdom, true);

        try {
            npc.setNextForceTalk(new ForceTalk(wisdom));
        } catch (Exception e) {
            // ForceTalk failed silently
        }

        // Occasional contextual tips
        if (Utils.random(4) == 0) {
            sendContextualGuidance(player, npc);
        }
        
        educationalTipCount.put(playerKey, tipCount + 1);
    }

    /**
     * Enhanced contextual combat tips
     */
    private void sendContextualGuidance(Player player, NPC npc) {
        if (!npc.withinDistance(player, 1)) {
            // Player at range
            String[] rangedTips = { 
                "Distance limits my attacks to ice and fire.",
                "Ranged combat requires different defenses.", 
                "My claws cannot reach you there." 
            };

            if (rangedTips.length > 0) {
                int randomIndex = Math.abs(Utils.random(rangedTips.length)) % rangedTips.length;
                String tip = rangedTips[randomIndex];
                player.sendMessage(tip, true);
            }

        } else {
            // Player in melee range
            String[] meleeTips = { 
                "Close quarters favor my deadly claws.", 
                "Melee protection would serve you well.",
                "Bold to face a dragon in close combat." 
            };

            if (meleeTips.length > 0) {
                int randomIndex = Math.abs(Utils.random(meleeTips.length)) % meleeTips.length;
                String tip = meleeTips[randomIndex];
                player.sendMessage(tip, true);
            }
        }
    }

    /**
     * NEW v5.0: Monitor scaling changes during dragon combat
     */
    private void monitorDragonScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getDragonScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get dragon scaling change message
     */
    private String getDragonScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Dragon Update: Fire balance improved! Draconic restraint reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=004080>Dragon Update: Ancient fury now active due to increased fire power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Dragon Update: Fire resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=0066cc>Dragon Update: Fire protection restored! Dragon damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent dragon phase tracking with BossBalancer integration
     */
    private void updateIntelligentDragonPhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentDragonPhase(npc);
        
        Integer lastPhase = currentDragonPhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentDragonPhase.put(npcKey, newPhase);
            handleIntelligentDragonPhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current dragon phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentDragonPhase(NPC npc) {
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
     * ENHANCED v5.0: Intelligent dragon phase transitions with scaling integration
     */
    private void handleIntelligentDragonPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "The ancient dragon";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("My scales grow harder!"));
            npc.setNextGraphics(new Graphics(2465));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED ANCIENT FURY UNLEASHED!" : "Ancient power courses through me!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(2707));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE ELDER WYRM TRANSFORMATION - MAXIMUM DRACONIC POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED ELDER FORM!" : "Witness the power of ages!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(369));
            break;
        }
    }

    /**
     * NEW v5.0: Check for dragon safe spotting
     */
    private void checkDragonSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveAvoidedBreaths.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastBreathHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect dragon-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean dragonFrustrated = consecutiveCount > 4; // Dragons have long reach
        boolean recentAvoidance = !lastHit;
        
        boolean dragonSafeSpot = playerDistanced && dragonFrustrated && recentAvoidance;
        
        if (dragonSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating dragon-themed responses
            if (warningCount >= 3) {
                performDragonAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=0066cc>The dragon settles as you engage in honorable combat...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform dragon anti-safe spot measure
     */
    private void performDragonAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=004080>Dragon fury seeks those who avoid direct confrontation!</col>");
        
        // Dragon roar that reaches through obstacles
        npc.setNextAnimation(new Animation(13155));
        npc.setNextForceTalk(new ForceTalk("COWARD! Face my flames with honor!"));
        
        // Enhanced damage based on scaling
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.3) : 250; // Dragon pursuit
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareDragonDamageScaling(scaledDamage, player, "dragon_pursuit");
        
        delayHit(npc, 1, player, getRegularHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>DRAGON PENALTY: Safe spotting detected - draconic roar ignores barriers!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced dragon taunts with scaling-based frequency
     */
    private void performEnhancedDragonTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on dragon phase and scaling
        int dragonPhase = getCurrentDragonPhase(npc);
        int tauntChance = 12 + (dragonPhase * 4); // Base 16% to 28% based on phase
        
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance += 10; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance += 6; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced dragon taunts based on phase and scaling
            performScaledDragonTaunt(npc, dragonPhase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled dragon taunt based on phase and scaling
     */
    private void performScaledDragonTaunt(NPC npc, int dragonPhase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "Feel the heat of dragonfire!",
            "My scales are harder than steel!",
            "Ancient wisdom guides my flames!",
            "Ice and fire are my domain!",
            "Dragons rule the skies!",
            "Your courage is noted, mortal!",
            "Test yourself against ages of power!"
        };
        
        String[] dragonTaunts = {
            "ANCIENT POWER AWAKENS!",
            "DRAGONFIRE BURNS ETERNAL!",
            "WITNESS DRACONIC SUPREMACY!",
            "THE WYRM'S FURY UNLEASHED!",
            "ICE AND FLAME OBEY MY WILL!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED DRACONIC MASTERY ACTIVATED!",
            "YOUR SUPERIOR DEFENSES FUEL MY RAGE!",
            "MAXIMUM DRAGON POWER UNLEASHED!",
            "ULTIMATE ELDER WYRM'S DOMINION!",
            "TRANSCENDENT DRACONIC FURY!"
        };
        
        String selectedTaunt;
        if (isHighScaling && dragonPhase >= 3) {
            // Use enhanced taunts for high scaling + high dragon power
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else if (dragonPhase >= 2) {
            // Use dragon taunts for high dragon phases
            selectedTaunt = dragonTaunts[Utils.random(dragonTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent dragon warning system
     */
    private int performIntelligentDragonAttackWithWarning(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Attack selection with enhanced mechanics
            int attackChoice = Utils.random(4);
            
            switch (attackChoice) {
                case 0: // Melee Attack
                    if (npc.withinDistance(player, 1)) {
                        return performEnhancedMeleeAttack(npc, player, defs, scaling);
                    } else {
                        return performEnhancedDragonfireAttack(npc, player, defs, scaling);
                    }
                    
                case 1: // Ice Breath
                    return performEnhancedIceBreath(npc, player, defs, scaling);
                    
                case 2: // Ice Arrows
                    return performEnhancedIceArrows(npc, player, defs, scaling);
                    
                case 3: // Dragonfire
                    return performEnhancedDragonfireAttack(npc, player, defs, scaling);
                    
                default:
                    return performEnhancedDragonfireAttack(npc, player, defs, scaling);
            }
            
        } catch (Exception e) {
            return defs.getAttackDelay();
        }
    }

    /**
     * Enhanced melee attack with BossBalancer integration
     */
    private int performEnhancedMeleeAttack(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        int baseDamage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, player);
        
        // Apply BossBalancer scaling and HP-aware damage
        if (scaling != null) {
            baseDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        }
        
        int safeDamage = applyHPAwareDragonDamageScaling(baseDamage, player, "melee_claw");
        
        npc.setNextAnimation(new Animation(defs.getAttackEmote()));
        delayHit(npc, 0, player, getMeleeHit(npc, safeDamage));
        
        // Track hit success
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastBreathHit.put(playerKey, safeDamage > 0);
        
        return defs.getAttackDelay();
    }

    /**
     * Enhanced ice breath attack with scaling
     */
    private int performEnhancedIceBreath(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        int baseDamage = getRandomMaxHit(npc, (int) (defs.getMaxHit() * 0.75), NPCCombatDefinitions.RANGE, player);
        
        // Apply BossBalancer scaling and HP-aware damage
        if (scaling != null) {
            baseDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        }
        
        int safeDamage = applyHPAwareDragonDamageScaling(baseDamage, player, "frost_breath");
        checkAndWarnLowHPForDragon(player, safeDamage);
        
        npc.setNextAnimation(new Animation(13155));
        World.sendProjectile(npc, player, 2707, 28, 16, 35, 35, 16, 0);
        delayHit(npc, 1, player, getRangeHit(npc, safeDamage));
        
        // Track hit success
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastBreathHit.put(playerKey, safeDamage > 0);
        updateAvoidedBreaths(playerKey, safeDamage > 0);
        
        return defs.getAttackDelay();
    }

    /**
     * Enhanced ice arrows attack with scaling
     */
    private int performEnhancedIceArrows(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        int baseDamage = getRandomMaxHit(npc, (int) (defs.getMaxHit() * 0.70), NPCCombatDefinitions.RANGE, player);
        
        // Apply BossBalancer scaling and HP-aware damage
        if (scaling != null) {
            baseDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        }
        
        int safeDamage = applyHPAwareDragonDamageScaling(baseDamage, player, "ice_arrows");
        
        npc.setNextAnimation(new Animation(13155));
        World.sendProjectile(npc, player, 369, 28, 16, 35, 35, 16, 0);
        delayHit(npc, 1, player, getRangeHit(npc, safeDamage));
        
        // Track hit success
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastBreathHit.put(playerKey, safeDamage > 0);
        updateAvoidedBreaths(playerKey, safeDamage > 0);
        
        return defs.getAttackDelay();
    }

    /**
     * Enhanced dragonfire attack with educational feedback and scaling
     */
    private int performEnhancedDragonfireAttack(NPC npc, Player player, NPCCombatDefinitions defs, CombatScaling scaling) {
        int baseDamage = getRandomMaxHit(npc, (int) (defs.getMaxHit() * 0.90), NPCCombatDefinitions.MAGE, player);
        int finalDamage = baseDamage;

        // Enhanced dragonfire protection system
        String message = Combat.getProtectMessage(player);
        if (message != null) {
            player.sendMessage(message, true);

            if (message.contains("fully")) {
                finalDamage = 0;
                if (Utils.random(5) == 0) {
                    player.sendMessage("The dragon nods approvingly at your protection.", true);
                }
            } else if (message.contains("most")) {
                finalDamage = (int) (baseDamage * 0.05);
            } else if (message.contains("some")) {
                finalDamage = (int) (baseDamage * 0.10);
            }
        } else {
            // No protection - apply full scaling
            if (scaling != null) {
                finalDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
            }
            
            finalDamage = applyHPAwareDragonDamageScaling(finalDamage, player, "dragonfire");
            checkAndWarnLowHPForDragon(player, finalDamage);
            
            if (Utils.random(8) == 0) {
                player.sendMessage("The dragon's fire burns through your unprotected skin!", true);
            }
        }

        npc.setNextAnimation(new Animation(13155));
        npc.setNextGraphics(new Graphics(2465));
        World.sendProjectile(npc, player, 393, 28, 16, 35, 35, 16, 0);
        delayHit(npc, 1, player, getRegularHit(npc, finalDamage));
        
        // Track hit success
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastBreathHit.put(playerKey, finalDamage > 0);
        updateAvoidedBreaths(playerKey, finalDamage > 0);

        return defs.getAttackDelay();
    }

    /**
     * Execute basic dragon attack for non-player targets
     */
    private int executeBasicDragonAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
        int damage = getRandomMaxHit(npc, defs.getMaxHit(), NPCCombatDefinitions.MELEE, target);
        npc.setNextAnimation(new Animation(defs.getAttackEmote()));
        delayHit(npc, 0, target, getMeleeHit(npc, damage));
        return defs.getAttackDelay();
    }

    /**
     * Update avoided breaths tracking
     */
    private void updateAvoidedBreaths(Integer playerKey, boolean hit) {
        Integer avoidedCount = consecutiveAvoidedBreaths.get(playerKey);
        if (avoidedCount == null) avoidedCount = 0;
        if (!hit) {
            consecutiveAvoidedBreaths.put(playerKey, avoidedCount + 1);
        } else {
            consecutiveAvoidedBreaths.put(playerKey, 0);
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
     * ENHANCED v5.0: Handle dragon combat end with proper cleanup
     */
    public static void onDragonCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentDragonPhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            educationalTipCount.remove(playerKey);
            consecutiveAvoidedBreaths.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastBreathHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=0066cc>Dragon combat session ended. Draconic scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("FrostDragon: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during dragon combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Dragon scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("FrostDragon: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onDragonCombatEnd(player, null);
        }
    }
}