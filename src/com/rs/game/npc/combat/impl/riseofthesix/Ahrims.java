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
 * Enhanced Ahrims (Rise of the Six) Combat System with FULL BossBalancer v5.0 Integration and HP-Aware Damage Scaling
 * Features: Advanced ice magic, teleport strikes, prayer drain, HP-aware damage scaling for minigame combat
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 3.0 - FULL BossBalancer v5.0 Integration with Intelligent Power Scaling & HP-Aware System for ROTS
 */
public class Ahrims extends CombatScript {

    // ===== ICE MAGIC PHASES - Enhanced for v5.0 =====
    private static final double APPRENTICE_THRESHOLD = 0.80;  // 80% HP - ice apprentice
    private static final double ADEPT_THRESHOLD = 0.60;       // 60% HP - ice adept
    private static final double MASTER_THRESHOLD = 0.35;      // 35% HP - ice master
    private static final double ARCHMASTER_THRESHOLD = 0.15;  // 15% HP - arch ice master

    // ===== ENHANCED GUIDANCE SYSTEM - Minigame-aware =====
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> currentIcePhase = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, String> lastScalingType = new ConcurrentHashMap<Integer, String>();
    private static final Map<Integer, Integer> attackCounter = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> freezeAttemptCount = new ConcurrentHashMap<Integer, Integer>();
    
    // ===== TIMING CONSTANTS - Enhanced for v5.0 =====
    private static final long WARNING_COOLDOWN = 120000; // 2 minutes between warnings (shorter for minigame)
    private static final long SCALING_UPDATE_INTERVAL = 20000; // 20 seconds for scaling updates
    private static final long PRE_ATTACK_WARNING_TIME = 2000; // 2 seconds before big attacks
    private static final int MAX_WARNINGS_PER_FIGHT = 4; // More warnings for complex mechanics

    // ===== HP-AWARE DAMAGE SCALING CONSTANTS - MINIGAME BALANCED =====
    private static final double MAX_DAMAGE_PERCENT_OF_HP = 0.28; // Max 28% of player HP per hit (ice magic is precise)
    private static final double CRITICAL_DAMAGE_PERCENT = 0.40;  // Max 40% for master spells
    private static final double FREEZE_DAMAGE_PERCENT = 0.32;    // Max 32% for freeze attacks
    private static final double TELEPORT_DAMAGE_PERCENT = 0.44;  // Max 44% for teleport strikes
    private static final int MIN_PLAYER_HP = 990;
    private static final int MAX_PLAYER_HP = 1500;
    private static final int ABSOLUTE_MAX_DAMAGE = 420;          // Hard cap (28% of 1500 HP)
    private static final int MINIMUM_DAMAGE = 22;               // Minimum damage to prevent 0 hits

    // ===== FREEZE AND DRAIN MECHANICS =====
    private static final int FREEZE_CHANCE = 5; // 1 in 5 chance for freeze effects
    private static final int MAX_FREEZES_PER_FIGHT = 8; // Prevent excessive freezing
    private static final int BASE_STRENGTH_DRAIN = 5; // Base strength drain amount
    private static final int BASE_PRAYER_DRAIN = 12; // Base prayer drain amount

    // ===== ICE MAGIC ATTACK PATTERNS with v5.0 intelligence =====
    private static final IceMagicAttackPattern[] ICE_MAGIC_ATTACK_PATTERNS = {
        new IceMagicAttackPattern(18300, 0, 0, "ice_bolt", false, ""),
        new IceMagicAttackPattern(18300, 400, 0, "strength_drain", true, "STRENGTH DRAIN incoming - ice magic saps your power!"),
        new IceMagicAttackPattern(18301, 369, 0, "ice_prison", true, "ICE PRISON incoming - prepare to break free!"),
        new IceMagicAttackPattern(18300, 348, 0, "mana_burn", true, "MANA BURN incoming - spiritual energy drain!"),
        new IceMagicAttackPattern(18302, 342, 342, "teleport_strike", true, "TELEPORT STRIKE incoming - shadow magic assault!")
    };

    // ===== SAFE SPOT PREVENTION - Ice-themed =====
    private static final Map<Integer, Integer> consecutiveAvoidedSpells = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> safeSpotWarnings = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Boolean> lastSpellHit = new ConcurrentHashMap<Integer, Boolean>();

    // ===== ANIMATION AND GRAPHICS CONSTANTS =====
    private static final int NORMAL_CAST_ANIM = 18300;
    private static final int ENHANCED_CAST_ANIM = 21925;
    private static final int ICE_PRISON_ANIM = 18301;
    private static final int TELEPORT_ANIM = 18302;
    private static final int STRENGTH_DRAIN_GFX = 400;
    private static final int ICE_PRISON_GFX = 369;
    private static final int MANA_BURN_GFX = 348;
    private static final int TELEPORT_GFX = 342;
    private static final int MAGIC_SHIELD_GFX = 94;
    private static final int MAGIC_PROJECTILE = 374;
    private static final int ICE_PROJECTILE = 369;
    private static final int MANA_BURN_PROJECTILE = 348;

    @Override
    public Object[] getKeys() {
        return new Object[] { 18538, 18539 }; // Rise of the Six Ahrims forms
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 4;
        }

        Player player = (Player) target;
        
        // ===== FULL BOSS BALANCER v5.0 INTEGRATION =====
        
        // Initialize combat session if needed
        initializeIceMagicCombatSession(player, npc);
        
        // Get INTELLIGENT combat scaling v5.0 (special handling for minigames)
        CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
        
        // Enhanced guidance system with intelligent scaling awareness
        provideIntelligentIceMagicGuidance(player, npc, scaling);
        
        // Monitor scaling changes during combat
        monitorIceMagicScalingChanges(player, scaling);
        
        // Update ice magic phase tracking with v5.0 scaling
        updateIntelligentIceMagicPhaseTracking(npc, scaling);
        
        // Check for ice magic-themed safe spotting
        checkIceMagicSafeSpotting(player, npc, scaling);
        
        // Enhanced ice magic taunts with scaling-based frequency
        performEnhancedIceMagicTaunts(npc, scaling);
        
        // Execute attack with v5.0 warnings and HP-aware scaling
        return performIntelligentIceMagicAttackWithWarning(npc, player, scaling);
    }

    /**
     * Initialize ice magic combat session using BossBalancer v5.0
     */
    private void initializeIceMagicCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer v5.0 combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            attackCounter.put(sessionKey, Integer.valueOf(0));
            lastScalingType.put(sessionKey, "UNKNOWN");
            freezeAttemptCount.put(sessionKey, Integer.valueOf(0));
            consecutiveAvoidedSpells.put(sessionKey, Integer.valueOf(0));
            safeSpotWarnings.put(sessionKey, Integer.valueOf(0));
            lastSpellHit.put(sessionKey, Boolean.TRUE);
            
            // Send v5.0 enhanced ice magic combat message
            CombatScaling scaling = BossBalancer.getIntelligentCombatScaling(player, npc);
            String welcomeMsg = getIntelligentIceMagicWelcomeMessage(scaling, npc);
            player.sendMessage(welcomeMsg);
            
            // Perform initial armor analysis for ice magic combat
            performInitialIceMagicArmorAnalysis(player);
        }
    }

    /**
     * NEW v5.0: Perform initial ice magic armor analysis
     */
    private void performInitialIceMagicArmorAnalysis(Player player) {
        try {
            // Use BossBalancer v5.0 armor analysis
            BossBalancer.ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            
            if (!armorResult.hasFullArmor) {
                player.sendMessage("<col=00BFFF>Ice Magic Analysis: Exposed areas detected. The cold seeks vulnerability!</col>");
            } else {
                double reductionPercent = armorResult.damageReduction * 100;
                player.sendMessage("<col=4682B4>Ice Magic Analysis: Full protection detected (" + 
                                 String.format("%.1f", reductionPercent) + "% damage resistance). Ice adapts to barriers...</col>");
            }
        } catch (Exception e) {
            // Ignore armor analysis errors
        }
    }

    /**
     * NEW v5.0: Apply HP-aware damage scaling to prevent instant kills from ice magic attacks
     */
    private int applyHPAwareIceMagicDamageScaling(int scaledDamage, Player player, String attackType) {
        if (player == null) {
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
        
        try {
            int currentHP = player.getHitpoints();
            int maxHP = player.getSkills().getLevelForXp(Skills.HITPOINTS);
            
            // Use current HP for calculation (ice magic is precise and calculated)
            int effectiveHP = Math.max(currentHP, MIN_PLAYER_HP);
            
            // Determine damage cap based on ice magic attack type
            double damagePercent;
            switch (attackType.toLowerCase()) {
                case "teleport_strike":
                case "shadow_assault":
                    damagePercent = TELEPORT_DAMAGE_PERCENT;
                    break;
                case "master_spell":
                case "arch_ice":
                    damagePercent = CRITICAL_DAMAGE_PERCENT;
                    break;
                case "ice_prison":
                case "freeze_attack":
                    damagePercent = FREEZE_DAMAGE_PERCENT;
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
            
            // Additional safety check - never deal more than 76% of current HP for ice magic
            if (currentHP > 0) {
                int emergencyCap = (int)(currentHP * 0.76);
                safeDamage = Math.min(safeDamage, emergencyCap);
            }
            
            return safeDamage;
            
        } catch (Exception e) {
            // Fallback to absolute cap
            return Math.min(scaledDamage, ABSOLUTE_MAX_DAMAGE);
        }
    }

    /**
     * NEW v5.0: Send HP warning if player is in danger from ice magic attacks
     */
    private void checkAndWarnLowHPForIceMagic(Player player, int incomingDamage) {
        if (player == null) return;
        
        try {
            int currentHP = player.getHitpoints();
            
            // Warn if incoming ice magic damage is significant relative to current HP
            if (currentHP > 0) {
                double damagePercent = (double)incomingDamage / currentHP;
                
                if (damagePercent >= 0.66) {
                    player.sendMessage("<col=ff0000>ICE MAGIC WARNING: This frozen spell will deal " + incomingDamage + 
                                     " damage! (" + currentHP + " HP remaining)</col>");
                } else if (damagePercent >= 0.46) {
                    player.sendMessage("<col=00BFFF>ICE MAGIC WARNING: Heavy ice damage incoming (" + incomingDamage + 
                                     ")! Consider healing (" + currentHP + " HP)</col>");
                }
            }
        } catch (Exception e) {
            // Ignore warning errors
        }
    }

    /**
     * ENHANCED v5.0: Generate intelligent ice magic welcome message based on power analysis
     */
    private String getIntelligentIceMagicWelcomeMessage(CombatScaling scaling, NPC npc) {
        StringBuilder message = new StringBuilder();
        
        // Get NPC name for personalized message
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Ahrims";
        
        message.append("<col=00BFFF>").append(npcName).append(" channels ice magic, analyzing your thermal defenses (v5.0).</col>");
        
        // Add v5.0 intelligent scaling information
        if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int diffIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            message.append(" <col=4682B4>[Ice mastery: +").append(diffIncrease).append("% frozen power]</col>");
        } else if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistance = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            message.append(" <col=32CD32>[Ice mercy: -").append(assistance).append("% ice damage]</col>");
        } else if (scaling.scalingType.contains("WITH_ABSORPTION")) {
            message.append(" <col=DDA0DD>[Ice resistance scaling active]</col>");
        } else if (scaling.scalingType.contains("FULL_ARMOR")) {
            message.append(" <col=708090>[Ice magic acknowledges your thermal protection]</col>");
        }
        
        return message.toString();
    }

    /**
     * ENHANCED v5.0: Intelligent ice magic guidance with power-based scaling awareness
     */
    private void provideIntelligentIceMagicGuidance(Player player, NPC npc, CombatScaling scaling) {
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
        String guidanceMessage = getIntelligentIceMagicGuidanceMessage(player, npc, scaling, currentStage);
        
        // Send guidance if applicable
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }

    /**
     * NEW v5.0: Get intelligent ice magic guidance message based on power analysis
     */
    private String getIntelligentIceMagicGuidanceMessage(Player player, NPC npc, CombatScaling scaling, int stage) {
        int phase = getCurrentIceMagicPhase(npc);
        
        switch (stage) {
            case 0:
                // First warning: Power analysis and scaling type
                return getIceMagicScalingAnalysisMessage(scaling, npc);
                
            case 1:
                // Second warning: Equipment effectiveness or phase progression
                if (scaling.scalingType.contains("INCOMPLETE_ARMOR")) {
                    return "<col=4682B4>Ice Magic Analysis: Missing armor exposes you to freezing! Ice damage increased by 25%!</col>";
                } else if (phase >= 3) {
                    String difficultyNote = scaling.bossDamageMultiplier > 2.0 ? " (EXTREME ice power due to scaling!)" : "";
                    return "<col=00BFFF>Ice Magic Analysis: Ice master phase reached. Frozen magic dramatically increased" + difficultyNote + " Use protection prayers!</col>";
                }
                break;
                
            case 2:
                // Third warning: Final phase or extreme scaling
                if (phase >= 4) {
                    return "<col=000080>Ice Magic Analysis: Arch ice master transformation! Maximum frozen power unleashed!</col>";
                } else if (scaling.bossDamageMultiplier > 2.5) {
                    return "<col=4682B4>Ice Magic Analysis: Extreme ice scaling detected! Consider facing greater frost mages!</col>";
                }
                break;
                
            case 3:
                // Fourth warning: Advanced tactics
                return "<col=00CED1>Ice Magic Tactics: Watch for teleport strikes and ice prisons. Keep restore potions ready!</col>";
        }
        
        return null;
    }

    /**
     * NEW v5.0: Get ice magic scaling analysis message
     */
    private String getIceMagicScalingAnalysisMessage(CombatScaling scaling, NPC npc) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "Ahrims";
        
        String baseMessage = "<col=DDA0DD>Ice Magic Power Analysis:</col> ";
        
        if (scaling.scalingType.contains("UNDERPOWERED_ASSISTANCE")) {
            int assistancePercent = (int)((1.0 - scaling.bossDamageMultiplier) * 100);
            return baseMessage + "<col=32CD32>" + npcName + "'s ice magic restrained! Frozen damage reduced by " + 
                   assistancePercent + "% due to insufficient magical preparation.</col>";
                   
        } else if (scaling.scalingType.contains("INTELLIGENT_ANTI_FARMING")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=4682B4>" + npcName + "'s ice mastery escalated! Frozen power increased by " + 
                   difficultyIncrease + "% due to superior magical defenses.</col>";
                   
        } else if (scaling.scalingType.contains("BALANCED_ENCOUNTER")) {
            return baseMessage + "<col=00BFFF>Balanced ice magic encounter. Optimal frozen resistance achieved!</col>";
            
        } else if (scaling.scalingType.contains("MILD_OVERGEAR")) {
            int difficultyIncrease = (int)((scaling.bossDamageMultiplier - 1.0) * 100);
            return baseMessage + "<col=FF6600>Slight magical advantage detected. " + npcName + "'s ice intensity increased by " + 
                   difficultyIncrease + "% for thermal balance.</col>";
        }
        
        return baseMessage + "<col=A9A9A9>Ice magic power ratio: " + String.format("%.2f", scaling.powerRatio) + ":1</col>";
    }

    /**
     * NEW v5.0: Monitor scaling changes during ice magic combat
     */
    private void monitorIceMagicScalingChanges(Player player, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        String currentScalingType = scaling.scalingType;
        String lastType = lastScalingType.get(playerKey);
        
        // Check if scaling type changed (prayer activation, gear swap, etc.)
        if (lastType != null && !lastType.equals(currentScalingType)) {
            // Scaling changed - notify player
            String changeMessage = getIceMagicScalingChangeMessage(lastType, currentScalingType, scaling);
            if (changeMessage != null) {
                player.sendMessage(changeMessage);
            }
        }
        
        lastScalingType.put(playerKey, currentScalingType);
    }

    /**
     * NEW v5.0: Get ice magic scaling change message
     */
    private String getIceMagicScalingChangeMessage(String oldType, String newType, CombatScaling scaling) {
        if (oldType.contains("UNDERPOWERED") && newType.contains("BALANCED")) {
            return "<col=32CD32>Ice Magic Update: Thermal balance improved! Ice restraint reduced.</col>";
        } else if (oldType.contains("BALANCED") && newType.contains("ANTI_FARMING")) {
            return "<col=4682B4>Ice Magic Update: Ice mastery now active due to increased magical power!</col>";
        } else if (newType.contains("WITH_ABSORPTION")) {
            return "<col=DDA0DD>Ice Magic Update: Magic resistance bonuses detected and factored into scaling!</col>";
        } else if (newType.contains("FULL_ARMOR") && oldType.contains("INCOMPLETE")) {
            return "<col=00BFFF>Ice Magic Update: Thermal protection restored! Ice damage scaling normalized.</col>";
        }
        
        return null;
    }

    /**
     * ENHANCED v5.0: Intelligent ice magic phase tracking with BossBalancer integration
     */
    private void updateIntelligentIceMagicPhaseTracking(NPC npc, CombatScaling scaling) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        int newPhase = getCurrentIceMagicPhase(npc);
        
        Integer lastPhase = currentIcePhase.get(npcKey);
        if (lastPhase == null) lastPhase = 1;
        
        if (newPhase > lastPhase) {
            currentIcePhase.put(npcKey, newPhase);
            handleIntelligentIceMagicPhaseTransition(npc, newPhase, scaling);
        }
    }

    /**
     * Get current ice magic phase based on HP (accounts for BossBalancer HP scaling)
     */
    private int getCurrentIceMagicPhase(NPC npc) {
        try {
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return 1;
            
            // Calculate phase based on percentage (works regardless of HP scaling)
            double hpPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercent <= ARCHMASTER_THRESHOLD) return 4;
            if (hpPercent <= MASTER_THRESHOLD) return 3;
            if (hpPercent <= ADEPT_THRESHOLD) return 2;
            return 1;
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ice magic phase transitions with scaling integration
     */
    private void handleIntelligentIceMagicPhaseTransition(NPC npc, int newPhase, CombatScaling scaling) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String npcName = (def != null && def.getName() != null) ? def.getName() : "The ice mage";
        
        switch (newPhase) {
        case 2:
            npc.setNextForceTalk(new ForceTalk("The ice grows colder!"));
            npc.setNextGraphics(new Graphics(MAGIC_SHIELD_GFX));
            break;
            
        case 3:
            String phase3Message = scaling.bossDamageMultiplier > 2.0 ? 
                "ENHANCED ICE MASTERY UNLEASHED!" : "Ancient ice magic flows through me!";
            npc.setNextForceTalk(new ForceTalk(phase3Message));
            npc.setNextGraphics(new Graphics(ICE_PRISON_GFX));
            break;
            
        case 4:
            String finalFormMessage = scaling.bossDamageMultiplier > 2.5 ? 
                "ULTIMATE ICE MASTER TRANSFORMATION - MAXIMUM FROZEN POWER!" : 
                scaling.bossDamageMultiplier > 1.5 ? 
                "ENHANCED ARCH ICE MASTER!" : "I become one with the eternal frost!";
            npc.setNextForceTalk(new ForceTalk(finalFormMessage));
            npc.setNextGraphics(new Graphics(ICE_PRISON_GFX));
            break;
        }
    }

    /**
     * NEW v5.0: Check for ice magic safe spotting
     */
    private void checkIceMagicSafeSpotting(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        
        // Get tracking values
        Integer consecutiveCount = consecutiveAvoidedSpells.get(playerKey);
        Integer warningCount = safeSpotWarnings.get(playerKey);
        Boolean lastHit = lastSpellHit.get(playerKey);
        
        if (consecutiveCount == null) consecutiveCount = 0;
        if (warningCount == null) warningCount = 0;
        if (lastHit == null) lastHit = true;
        
        // Detect ice magic-themed safe spotting
        boolean playerDistanced = !Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                                 player.getX(), player.getY(), player.getSize(), 0);
        boolean iceMageFrustrated = consecutiveCount > 3; // Ice mages get frustrated with spell avoidance
        boolean recentAvoidance = !lastHit;
        boolean cannotReachWithProjectile = !npc.clipedProjectile(player, true);
        
        boolean iceMagicSafeSpot = playerDistanced && iceMageFrustrated && (recentAvoidance || cannotReachWithProjectile);
        
        if (iceMagicSafeSpot) {
            warningCount++;
            safeSpotWarnings.put(playerKey, warningCount);
            
            // Escalating ice magic-themed responses
            if (warningCount >= 3) {
                performIceMagicAntiSafeSpotMeasure(npc, player, scaling);
                safeSpotWarnings.put(playerKey, 0);
            }
        } else {
            // Reset when fighting fairly
            if (warningCount > 0) {
                safeSpotWarnings.put(playerKey, 0);
                player.sendMessage("<col=00BFFF>The ice magic stabilizes as you engage in direct combat...</col>");
            }
        }
    }

    /**
     * NEW v5.0: Perform ice magic anti-safe spot measure
     */
    private void performIceMagicAntiSafeSpotMeasure(NPC npc, Player player, CombatScaling scaling) {
        player.sendMessage("<col=4682B4>Ice magic seeks those who avoid frozen justice!</col>");
        
        // Ice shard that penetrates through obstacles
        npc.setNextAnimation(new Animation(NORMAL_CAST_ANIM));
        npc.setNextForceTalk(new ForceTalk("COWARD! The frost will find you!"));
        
        // Enhanced damage based on scaling
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        int baseDamage = defs != null ? (int)(defs.getMaxHit() * 1.3) : 150; // Ice pursuit
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, baseDamage);
        int safeDamage = applyHPAwareIceMagicDamageScaling(scaledDamage, player, "ice_pursuit");
        
        // Send ice projectile
        World.sendProjectile(npc, player, ICE_PROJECTILE, 18, 18, 50, 50, 0, 0);
        delayHit(npc, 2, player, getMagicHit(npc, safeDamage));
        
        player.sendMessage("<col=FF6600>ICE MAGIC PENALTY: Safe spotting detected - ice shards pierce through!</col>");
    }

    /**
     * ENHANCED v5.0: Enhanced ice magic taunts with scaling-based frequency
     */
    private void performEnhancedIceMagicTaunts(NPC npc, CombatScaling scaling) {
        // Increase taunt frequency based on ice magic phase and scaling
        int icePhase = getCurrentIceMagicPhase(npc);
        int tauntChance = 8 + (icePhase * 4); // Base 12% to 24% based on phase
        
        if (scaling.bossDamageMultiplier > 2.0) {
            tauntChance += 12; // More frequent for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.5) {
            tauntChance += 8; // More frequent for high scaling
        }
        
        if (Utils.random(100) < tauntChance) {
            // Enhanced ice magic taunts based on phase and scaling
            performScaledIceMagicTaunt(npc, icePhase, scaling);
        }
    }

    /**
     * NEW v5.0: Perform scaled ice magic taunt based on phase and scaling
     */
    private void performScaledIceMagicTaunt(NPC npc, int icePhase, CombatScaling scaling) {
        boolean isHighScaling = scaling.bossDamageMultiplier > 1.8;
        
        String[] basicTaunts = {
            "Feel the bite of frost!",
            "Ice magic flows through me!",
            "Your strength will freeze!",
            "The cold embraces all!",
            "Ice and shadow combined!",
            "Frozen magic commands you!",
            "The eternal winter comes!"
        };
        
        String[] masterTaunts = {
            "ICE MASTERY AWAKENS!",
            "FROZEN POWER BEYOND MORTAL KEN!",
            "THE ETERNAL FROST OBEYS ME!",
            "WITNESS TRUE ICE MAGIC FURY!",
            "COLD AND SHADOW UNITE!"
        };
        
        String[] enhancedTaunts = {
            "ENHANCED ICE MASTERY ACTIVATED!",
            "YOUR SUPERIOR DEFENSES FUEL MY FROZEN RAGE!",
            "MAXIMUM ICE MAGIC UNLEASHED!",
            "ULTIMATE FROST MAGE'S DOMINION!",
            "TRANSCENDENT ICE MASTER POWER!"
        };
        
        String selectedTaunt;
        if (isHighScaling && icePhase >= 3) {
            // Use enhanced taunts for high scaling + high ice mastery
            selectedTaunt = enhancedTaunts[Utils.random(enhancedTaunts.length)];
        } else if (icePhase >= 2) {
            // Use master taunts for high ice phases
            selectedTaunt = masterTaunts[Utils.random(masterTaunts.length)];
        } else {
            // Use basic taunts
            selectedTaunt = basicTaunts[Utils.random(basicTaunts.length)];
        }
        
        npc.setNextForceTalk(new ForceTalk(selectedTaunt));
    }

    /**
     * ENHANCED v5.0: Perform attack with intelligent ice magic warning system
     */
    private int performIntelligentIceMagicAttackWithWarning(NPC npc, Player player, CombatScaling scaling) {
        try {
            // Increment attack counter
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer currentCount = attackCounter.get(playerKey);
            if (currentCount == null) currentCount = 0;
            attackCounter.put(playerKey, currentCount + 1);
            
            // Select attack pattern with v5.0 intelligence
            int icePhase = getCurrentIceMagicPhase(npc);
            IceMagicAttackPattern pattern = selectIntelligentIceMagicAttackPattern(icePhase, scaling, currentCount);
            
            // Enhanced pre-attack warning system
            if (pattern.requiresWarning && shouldGiveIntelligentIceMagicWarning(scaling, currentCount)) {
                sendIntelligentIceMagicPreAttackWarning(player, pattern.warningMessage, scaling);
                
                // Adaptive delay based on scaling difficulty
                int warningDelay = scaling.bossDamageMultiplier > 2.5 ? 3 : 2; // More time for extreme scaling
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        executeIntelligentScaledIceMagicAttack(npc, player, pattern, scaling);
                        this.stop();
                    }
                }, warningDelay);
                
                return getIntelligentIceMagicAttackDelay(npc, icePhase, scaling) + warningDelay;
            } else {
                // Execute immediately for basic attacks
                executeIntelligentScaledIceMagicAttack(npc, player, pattern, scaling);
                return getIntelligentIceMagicAttackDelay(npc, icePhase, scaling);
            }
            
        } catch (Exception e) {
            return 3;
        }
    }

    /**
     * ENHANCED v5.0: Intelligent ice magic warning probability based on scaling
     */
    private boolean shouldGiveIntelligentIceMagicWarning(CombatScaling scaling, int attackCount) {
        // More frequent warnings for undergeared players facing ice magic
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
     * ENHANCED v5.0: Intelligent ice magic pre-attack warning with scaling context
     */
    private void sendIntelligentIceMagicPreAttackWarning(Player player, String warning, CombatScaling scaling) {
        String scalingNote = "";
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingNote = " (EXTREME ice power due to scaling!)";
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingNote = " (Intense frozen magic due to scaling!)";
        }
        
        player.sendMessage("<col=4682B4>ICE MAGIC WARNING: " + warning + scalingNote + "</col>");
    }

    /**
     * ENHANCED v5.0: Intelligent ice magic attack pattern selection with scaling consideration
     */
    private IceMagicAttackPattern selectIntelligentIceMagicAttackPattern(int icePhase, CombatScaling scaling, int attackCount) {
        int roll = Utils.random(100);
        
        // Enhanced special attack chances based on ice phase, scaling, and progression
        int baseSpecialChance = (icePhase - 1) * 13; // 13% per ice phase above 1
        int scalingBonus = scaling.bossDamageMultiplier > 1.8 ? 12 : 0; // More specials for scaled fights
        int progressionBonus = attackCount > 7 ? 6 : 0; // More specials as fight progresses
        int specialChance = baseSpecialChance + scalingBonus + progressionBonus;
        
        // v5.0 intelligent pattern selection for ice magic attacks
        boolean isOvergeared = scaling.scalingType.contains("ANTI_FARMING");
        
        if (isOvergeared) {
            // More aggressive ice magic patterns for overgeared players
            if (roll < 9 + specialChance) return ICE_MAGIC_ATTACK_PATTERNS[4]; // Teleport strike
            if (roll < 21 + specialChance) return ICE_MAGIC_ATTACK_PATTERNS[3]; // Mana burn  
            if (roll < 34 + specialChance) return ICE_MAGIC_ATTACK_PATTERNS[2]; // Ice prison
            if (roll < 46 + specialChance) return ICE_MAGIC_ATTACK_PATTERNS[1]; // Strength drain
        } else {
            // Standard ice magic pattern selection
            if (roll < 6 + specialChance) return ICE_MAGIC_ATTACK_PATTERNS[4]; // Teleport strike
            if (roll < 16 + specialChance) return ICE_MAGIC_ATTACK_PATTERNS[3]; // Mana burn  
            if (roll < 28 + specialChance) return ICE_MAGIC_ATTACK_PATTERNS[2]; // Ice prison
            if (roll < 38 + specialChance) return ICE_MAGIC_ATTACK_PATTERNS[1]; // Strength drain
        }
        
        return ICE_MAGIC_ATTACK_PATTERNS[0]; // Ice bolt
    }

    /**
     * ENHANCED v5.0: Execute ice magic attack with intelligent BossBalancer integration and HP-aware scaling
     */
    private void executeIntelligentScaledIceMagicAttack(NPC npc, Player player, IceMagicAttackPattern pattern, CombatScaling scaling) {
        try {
            // Set animation and graphics
            npc.setNextAnimation(new Animation(pattern.animation));
            if (pattern.startGraphics > 0) {
                npc.setNextGraphics(new Graphics(pattern.startGraphics));
            }
            
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs == null) return;
            
            // Enhanced ice magic damage calculation with v5.0 intelligence
            int icePhase = getCurrentIceMagicPhase(npc);
            double iceModifier = 1.0 + (icePhase - 1) * 0.15; // 15% per ice phase (frozen mastery)
            
            // Enhanced max hit calculation with v5.0 BossBalancer integration
            int baseMaxHit = defs.getMaxHit();
            int scaledMaxHit = BossBalancer.applyBossMaxHitScaling(baseMaxHit, player, npc);
            int baseDamage = (int)(scaledMaxHit * iceModifier);
            
            // Execute different ice magic attack types with v5.0 scaling and HP-aware damage
            if ("teleport_strike".equals(pattern.name)) {
                executeIntelligentTeleportStrike(npc, player, baseDamage, scaling);
            } else if ("mana_burn".equals(pattern.name)) {
                executeIntelligentManaBurn(npc, player, baseDamage, scaling);
            } else if ("ice_prison".equals(pattern.name)) {
                executeIntelligentIcePrison(npc, player, baseDamage, scaling);
            } else if ("strength_drain".equals(pattern.name)) {
                executeIntelligentStrengthDrain(npc, player, baseDamage, scaling);
            } else {
                executeIntelligentSingleIceMagicAttack(npc, player, baseDamage, 2, scaling, "ice_bolt");
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
            // Enhanced fallback - execute basic ice magic attack with v5.0 scaling
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                int scaledDamage = BossBalancer.applyBossMaxHitScaling(defs.getMaxHit(), player, npc);
                executeIntelligentSingleIceMagicAttack(npc, player, scaledDamage, 2, scaling, "ice_bolt");
            }
        }
    }

    /**
     * ENHANCED v5.0: Intelligent teleport strike attack with HP-aware scaling
     */
    private void executeIntelligentTeleportStrike(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        WorldTile originalTile = npc.getWorldTile();
        player.sendMessage("Ahrims vanishes into frozen shadows!");
        
        // Teleport effect at current location
        npc.setNextGraphics(new Graphics(TELEPORT_GFX));
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Calculate position behind player
                WorldTile playerTile = player.getWorldTile();
                WorldTile behindPlayer = new WorldTile(
                    playerTile.getX() - 1, 
                    playerTile.getY() - 1, 
                    playerTile.getPlane()
                );
                
                // Teleport behind player
                npc.setNextWorldTile(behindPlayer);
                npc.setNextGraphics(new Graphics(TELEPORT_GFX));
                
                // Enhanced teleport strike damage (200% base for surprise attack)
                int damage = (int)(baseDamage * 2.0) + Utils.random(baseDamage / 3);
                int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
                
                // CRITICAL: Apply HP-aware damage scaling for teleport strikes
                int safeDamage = applyHPAwareIceMagicDamageScaling(scaledDamage, player, "teleport_strike");
                checkAndWarnLowHPForIceMagic(player, safeDamage);
                
                // Immediate surprise attack
                npc.setNextAnimation(new Animation(ENHANCED_CAST_ANIM));
                player.applyHit(new Hit(npc, safeDamage, Hit.HitLook.MAGIC_DAMAGE));
                
                player.sendMessage("Ahrims strikes from behind with devastating ice magic!");
                
                // Return to original position after 3 ticks
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        npc.setNextWorldTile(originalTile);
                        npc.setNextGraphics(new Graphics(TELEPORT_GFX));
                        stop();
                    }
                }, 3);
                
                stop();
            }
        }, 2);
    }

    /**
     * ENHANCED v5.0: Intelligent mana burn attack with HP-aware scaling
     */
    private void executeIntelligentManaBurn(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Send mana burn projectile
        World.sendProjectile(npc, player, MANA_BURN_PROJECTILE, 18, 18, 50, 50, 0, 0);
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Prayer drain effect
                player.setNextGraphics(new Graphics(MANA_BURN_GFX));
                
                // Scale drain based on scaling difficulty
                double drainMultiplier = scaling.bossDamageMultiplier > 1.5 ? 1.4 : 1.0;
                int drainAmount = (int)(BASE_PRAYER_DRAIN * drainMultiplier);
                
                // Drain prayer points
                int currentPrayer = player.getSkills().getLevel(Skills.PRAYER);
                int actualDrain = Math.min(currentPrayer, drainAmount + Utils.random(10));
                player.getSkills().set(Skills.PRAYER, currentPrayer - actualDrain);
                
                // Mana burn damage (160% base for spiritual damage)
                int damage = (int)(baseDamage * 1.6) + Utils.random(baseDamage / 4);
                int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
                
                // CRITICAL: Apply HP-aware damage scaling for mana burn
                int safeDamage = applyHPAwareIceMagicDamageScaling(scaledDamage, player, "mana_burn");
                checkAndWarnLowHPForIceMagic(player, safeDamage);
                
                player.applyHit(new Hit(npc, safeDamage, Hit.HitLook.MAGIC_DAMAGE));
                
                player.sendMessage("Ahrims burns away " + actualDrain + " prayer points with ice magic!");
                stop();
            }
        }, 2);
    }

    /**
     * ENHANCED v5.0: Intelligent ice prison attack with HP-aware scaling
     */
    private void executeIntelligentIcePrison(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        Integer freezeCount = freezeAttemptCount.get(playerKey);
        if (freezeCount == null) freezeCount = 0;
        
        // Limit freezes per fight
        if (freezeCount >= MAX_FREEZES_PER_FIGHT) {
            // Fall back to regular ice attack
            executeIntelligentSingleIceMagicAttack(npc, player, baseDamage, 2, scaling, "ice_bolt");
            return;
        }
        
        // Make variable effectively final for lambda
        final int currentFreezeCount = freezeCount;
        
        player.sendMessage("Ahrims conjures an ice prison around you!");
        
        // Send ice projectile
        World.sendProjectile(npc, player, ICE_PROJECTILE, 18, 18, 50, 50, 0, 0);
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Ice prison effect
                player.setNextGraphics(new Graphics(ICE_PRISON_GFX));
                
                // Scale freeze duration based on scaling
                int freezeDuration = scaling.bossDamageMultiplier > 2.0 ? 6000 : 5000; // 5-6 seconds
                player.addFreezeDelay(freezeDuration);
                player.sendMessage("You are trapped in ice! Break free by moving!");
                
                // Ice prison damage (140% base for imprisonment)
                int damage = (int)(baseDamage * 1.4) + Utils.random(baseDamage / 4);
                int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
                
                // CRITICAL: Apply HP-aware damage scaling for ice prison
                int safeDamage = applyHPAwareIceMagicDamageScaling(scaledDamage, player, "ice_prison");
                checkAndWarnLowHPForIceMagic(player, safeDamage);
                
                player.applyHit(new Hit(npc, safeDamage, Hit.HitLook.MAGIC_DAMAGE));
                
                // Update freeze counter
                freezeAttemptCount.put(playerKey, currentFreezeCount + 1);
                
                stop();
            }
        }, 2);
    }

    /**
     * ENHANCED v5.0: Intelligent strength drain attack with HP-aware scaling
     */
    private void executeIntelligentStrengthDrain(NPC npc, Player player, int baseDamage, CombatScaling scaling) {
        // Send magic projectile
        World.sendProjectile(npc, player, MAGIC_PROJECTILE, 18, 18, 50, 50, 0, 0);
        
        // Strength drain damage (150% base for stat drain)
        int damage = (int)(baseDamage * 1.5) + Utils.random(baseDamage / 4);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling for strength drain
        int safeDamage = applyHPAwareIceMagicDamageScaling(scaledDamage, player, "strength_drain");
        checkAndWarnLowHPForIceMagic(player, safeDamage);
        
        delayHit(npc, 2, player, getMagicHit(npc, safeDamage));
        
        // Apply enhanced strength drain
        if (safeDamage > 0) {
            applyIceMagicStrengthDrain(player, scaling);
        }
    }

    /**
     * ENHANCED v5.0: Intelligent single ice magic attack with proper scaling and HP-aware damage
     */
    private void executeIntelligentSingleIceMagicAttack(NPC npc, Player player, int baseDamage, int delay, CombatScaling scaling, String attackType) {
        int damage = Utils.random(baseDamage + 1);
        int scaledDamage = BossBalancer.calculateNPCDamageToPlayer(npc, player, damage);
        
        // CRITICAL: Apply HP-aware damage scaling
        int safeDamage = applyHPAwareIceMagicDamageScaling(scaledDamage, player, attackType);
        if (!"ice_bolt".equals(attackType)) {
            checkAndWarnLowHPForIceMagic(player, safeDamage);
        }
        
        // Send appropriate projectile
        int projectileId = npc.getId() == 18539 ? MAGIC_PROJECTILE : MAGIC_PROJECTILE;
        World.sendProjectile(npc, player, projectileId, 18, 18, 50, 50, 0, 0);
        delayHit(npc, delay, player, getMagicHit(npc, safeDamage));
        
        // Apply strength drain chance for ice bolts
        if (safeDamage > 0 && "ice_bolt".equals(attackType) && Utils.random(FREEZE_CHANCE) == 0) {
            applyIceMagicStrengthDrain(player, scaling);
        }
        
        // Track if attack hit
        Integer playerKey = Integer.valueOf(player.getIndex());
        lastSpellHit.put(playerKey, safeDamage > 0);
        
        // Update avoided spells counter
        Integer avoidedCount = consecutiveAvoidedSpells.get(playerKey);
        if (avoidedCount == null) avoidedCount = 0;
        if (safeDamage <= 0) {
            consecutiveAvoidedSpells.put(playerKey, avoidedCount + 1);
        } else {
            consecutiveAvoidedSpells.put(playerKey, 0);
        }
    }

    /**
     * ENHANCED v5.0: Apply ice magic strength drain
     */
    private void applyIceMagicStrengthDrain(Player player, CombatScaling scaling) {
        player.setNextGraphics(new Graphics(STRENGTH_DRAIN_GFX, 0, 100));
        
        // Scale drain based on scaling difficulty
        double drainMultiplier = scaling.bossDamageMultiplier > 1.5 ? 1.3 : 1.0;
        int drainAmount = (int)(BASE_STRENGTH_DRAIN * drainMultiplier);
        
        int currentLevel = player.getSkills().getLevel(Skills.STRENGTH);
        int newLevel = Math.max(0, currentLevel - drainAmount);
        player.getSkills().set(Skills.STRENGTH, newLevel);
        
        player.sendMessage("Ahrims' ice magic freezes " + drainAmount + " strength levels!");
    }

    /**
     * ENHANCED v5.0: Intelligent ice magic attack delay with scaling consideration
     */
    private int getIntelligentIceMagicAttackDelay(NPC npc, int icePhase, CombatScaling scaling) {
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) return 3;
        
        int baseDelay = 3; // Standard ice magic delay
        int iceSpeedBonus = Math.max(0, icePhase - 1); // Ice mastery makes casting faster
        
        // v5.0 intelligent scaling can affect attack speed for ice magic
        int scalingSpeedBonus = 0;
        if (scaling.bossDamageMultiplier > 2.5) {
            scalingSpeedBonus = 1; // Faster for extreme scaling
        } else if (scaling.bossDamageMultiplier > 1.8) {
            scalingSpeedBonus = 1; // Slightly faster for high scaling
        }
        
        return Math.max(2, baseDelay - iceSpeedBonus - scalingSpeedBonus);
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
     * ENHANCED v5.0: Handle ice magic combat end with proper cleanup
     */
    public static void onIceMagicCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            // End BossBalancer v5.0 combat session
            BossBalancer.endCombatSession(player);
            
            // Clear local tracking maps
            Integer playerKey = Integer.valueOf(player.getIndex());
            Integer npcKey = Integer.valueOf(npc != null ? npc.getIndex() : 0);
            
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            currentIcePhase.remove(npcKey);
            combatSessionActive.remove(playerKey);
            lastScalingType.remove(playerKey);
            attackCounter.remove(playerKey);
            freezeAttemptCount.remove(playerKey);
            consecutiveAvoidedSpells.remove(playerKey);
            safeSpotWarnings.remove(playerKey);
            lastSpellHit.remove(playerKey);
            
            // Clear BossBalancer player cache
            BossBalancer.clearPlayerCache(player.getIndex());
            
            // Send completion message with v5.0 info
            player.sendMessage("<col=00BFFF>Ice magic combat session ended. Frozen scaling data cleared.</col>");
            
        } catch (Exception e) {
            System.err.println("Ahrims: Error ending v5.0 combat session: " + e.getMessage());
        }
    }

    /**
     * ENHANCED v5.0: Handle prayer changes during ice magic combat
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
                player.sendMessage("<col=DDA0DD>Prayer change detected. Ice magic scaling analysis updated.</col>");
            }
        } catch (Exception e) {
            System.err.println("Ahrims: Error handling v5.0 prayer change: " + e.getMessage());
        }
    }

    /**
     * Force cleanup (call on logout/death)
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onIceMagicCombatEnd(player, null);
        }
    }

    /**
     * Enhanced ice magic attack pattern data structure
     */
    private static class IceMagicAttackPattern {
        final int animation;
        final int startGraphics;
        final int endGraphics;
        final String name;
        final boolean requiresWarning;
        final String warningMessage;

        IceMagicAttackPattern(int animation, int startGraphics, int endGraphics, String name, 
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