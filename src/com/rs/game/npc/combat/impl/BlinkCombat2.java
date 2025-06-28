package com.rs.game.npc.combat.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;
import com.rs.utils.Logger;

/**
 * Enhanced Blink Combat System with BossBalancer Integration and Guidance System
 * 
 * @author Zeus
 * @date June 02, 2025
 * @version 3.0 - Integrated with BossBalancer v2.2 and Chaotic Void Guidance System
 * @note Chaotic Void Entity with unpredictable combat patterns and reality-bending abilities
 */
public class BlinkCombat2 extends CombatScript {

    // Enhanced education system for chaotic combat mechanics
    private static final Map<String, Long> playerLastChaosTip = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Integer> playerChaosTipStage = new ConcurrentHashMap<String, Integer>();
    private static final Map<String, Long> playerLastGuidance = new ConcurrentHashMap<String, Long>();
    private static final Map<Integer, Long> bossLastTierAnnouncement = new ConcurrentHashMap<Integer, Long>();
    private static final Map<String, Integer> playerCombatStage = new ConcurrentHashMap<String, Integer>();
    
    // Safe spot prevention for chaotic void entity
    private static final Map<String, Integer> playerSafeSpotViolations = new ConcurrentHashMap<String, Integer>();
    private static final Map<String, Long> playerLastValidHit = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Boolean> playerRealityWarped = new ConcurrentHashMap<String, Boolean>();
    
    private static final long CHAOS_TIP_COOLDOWN = 20000; // 20 seconds between tips
    private static final long GUIDANCE_COOLDOWN = 25000; // 25 seconds between guidance messages
    private static final long TIER_ANNOUNCEMENT_COOLDOWN = 300000; // 5 minutes between tier announcements
    private static final int MAX_CHAOS_TIPS_PER_FIGHT = 7; // More tips for complex chaotic mechanics
    
    // Chaotic safe spot prevention constants
    private static final int CHAOS_SAFE_SPOT_CHECK = 1; // Immediate response for chaos entity
    private static final long MAX_CHAOS_SAFE_SPOT_TIME = 8000; // 8 seconds max for chaotic entity

    // Boss stats cache for chaotic void entities
    private static final Map<Integer, ChaosVoidStats> chaosStatsCache = new ConcurrentHashMap<Integer, ChaosVoidStats>();
    
    // Animation constants
    private static final Animation MELEE = new Animation(12310);
    
    // Combat state variables
    private boolean specialRapidMagic, sprayAttack;
    private long lastGuidanceTime = 0;
    private int guidancePhase = 0;
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        // Get balanced chaos void stats
        ChaosVoidStats chaosStats = getBalancedChaosStats(npc);
        
        // BOSS BALANCER INTEGRATION: Get dynamic boss stats
        int bossType = getBossType(npc);
        int bossTier = getBossTier(npc, chaosStats);
        
        // Announce tier at the beginning (visible to all players)
        announceBossTier(npc, chaosStats);
        
        // Boss Guidance System: Provide contextual chaotic advice
        provideChaosVoidGuidance(npc, target, bossType, bossTier, chaosStats);
        
        // CHAOS SAFE SPOT PREVENTION: Reality-bending anti-safe spot measures
        checkChaosVoidSafeSpotting(npc, target, bossType, bossTier, chaosStats);
        
        // Execute chaotic void combat
        return executeChaosVoidCombat(npc, target, chaosStats, bossTier);
    }

    /**
     * CHAOS VOID SAFE SPOT PREVENTION: Reality-bending anti-safe spot system
     */
    private void checkChaosVoidSafeSpotting(NPC npc, Entity target, int bossType, int bossTier, ChaosVoidStats chaosStats) {
        if (!(target instanceof Player)) {
            return;
        }
        
        Player player = (Player) target;
        String username = player.getUsername();
        
        // Chaotic entities detect safe spotting through reality distortion
        boolean realityDistorted = detectChaosVoidSafeSpotting(npc, player, chaosStats);
        
        if (realityDistorted) {
            Integer violations = playerSafeSpotViolations.get(username);
            if (violations == null) violations = 0;
            violations++;
            playerSafeSpotViolations.put(username, violations);
            
            // Immediate chaotic response
            if (violations >= 1) {
                performChaosVoidRealityWarp(npc, player, bossTier, chaosStats);
                playerSafeSpotViolations.put(username, 0); // Reset after reality warp
            }
        } else {
            // Reset when reality is stable
            if (playerSafeSpotViolations.containsKey(username)) {
                playerSafeSpotViolations.remove(username);
                playerRealityWarped.remove(username);
                sendChaosMessage(player, "Reality stabilizes around me... for now.");
            }
        }
    }
    
    /**
     * CHAOS VOID SAFE SPOT PREVENTION: Detect reality distortion patterns
     */
    private boolean detectChaosVoidSafeSpotting(NPC npc, Player player, ChaosVoidStats chaosStats) {
        String username = player.getUsername();
        long currentTime = System.currentTimeMillis();
        boolean playerAttacking = isPlayerAttackingBoss(player, npc);
        boolean canChaosReach = canChaosVoidReachPlayer(npc, player, chaosStats);
        
        Long lastValidHit = playerLastValidHit.get(username);
        boolean longTimeWithoutHit = lastValidHit == null || (currentTime - lastValidHit) > MAX_CHAOS_SAFE_SPOT_TIME;
        
        // If player is attacking but chaos entity can't reach them, reality is distorted
        return playerAttacking && !canChaosReach && longTimeWithoutHit;
    }
    
    /**
     * CHAOS VOID SAFE SPOT PREVENTION: Apply reality-warping measures
     */
    private void performChaosVoidRealityWarp(NPC npc, Player player, int bossTier, ChaosVoidStats chaosStats) {
        sendChaosMessage(player, "Reality bends to my chaotic will! No hiding from the void!");
        playerRealityWarped.put(player.getUsername(), true);
        
        switch (bossTier) {
            case 1: case 2: case 3: // Basic chaos
                performBasicChaosCounter(npc, player, chaosStats);
                break;
            case 4: case 5: // Advanced chaos
                performAdvancedChaosCounter(npc, player, chaosStats);
                break;
            case 6: case 7: // Elite chaos
                performEliteChaosCounter(npc, player, chaosStats);
                break;
            case 8: case 9: case 10: // Legendary chaos
                performLegendaryChaosCounter(npc, player, chaosStats);
                break;
        }
    }
    
    /**
     * CHAOS VOID SAFE SPOT PREVENTION: Basic chaos counter
     */
    private void performBasicChaosCounter(NPC npc, Player player, ChaosVoidStats chaosStats) {
        sendChaosGuidanceMessage(player, "CHAOS PENALTY: Basic reality distortion in effect!");
        
        // Phase through obstacles with chaotic magic
        npc.setNextForceTalk(new ForceTalk("Walls mean nothing to chaos!"));
        npc.setNextAnimation(new Animation(14956));
        npc.setNextGraphics(new Graphics(2854));
        
        // Penetrating chaos magic that ignores most obstacles
        int damage = (int)(chaosStats.magicMaxHit * 1.2);
        player.applyHit(new Hit(npc, damage, HitLook.MAGIC_DAMAGE, 1));
        player.setNextGraphics(new Graphics(2855, 0, 0));
    }
    
    /**
     * CHAOS VOID SAFE SPOT PREVENTION: Advanced chaos counter
     */
    private void performAdvancedChaosCounter(NPC npc, Player player, ChaosVoidStats chaosStats) {
        sendChaosGuidanceMessage(player, "ADVANCED PENALTY: Chaos reshapes the battlefield!");
        
        // Chaotic teleportation to player's location
        npc.setNextForceTalk(new ForceTalk("I exist everywhere and nowhere!"));
        
        WorldTile playerTile = new WorldTile(player.getX(), player.getY(), player.getPlane());
        WorldTile chaosTile = findChaosPosition(npc, playerTile);
        
        if (chaosTile != null) {
            npc.setNextWorldTile(chaosTile);
            npc.setNextGraphics(new Graphics(342)); // Teleport effect
            
            // Follow with chaotic spray attack
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    executeSprayAttack(npc, player, chaosStats);
                }
            }, 1);
        }
    }
    
    /**
     * CHAOS VOID SAFE SPOT PREVENTION: Elite chaos counter
     */
    private void performEliteChaosCounter(NPC npc, Player player, ChaosVoidStats chaosStats) {
        sendChaosMessage(player, "Elite chaos transcends your pathetic reality!");
        sendChaosGuidanceMessage(player, "ELITE PENALTY: Reality itself becomes unstable!");
        
        // Multi-dimensional assault
        performChaosRealityStorm(npc, player, chaosStats);
        
        // Temporary chaos field effect
        applyChaosField(player);
    }
    
    /**
     * CHAOS VOID SAFE SPOT PREVENTION: Legendary chaos counter
     */
    private void performLegendaryChaosCounter(NPC npc, Player player, ChaosVoidStats chaosStats) {
        sendChaosMessage(player, "LEGENDARY CHAOS COMMANDS ALL EXISTENCE!");
        sendChaosGuidanceMessage(player, "LEGENDARY PENALTY: The void consumes all hiding places!");
        
        // Ultimate reality manipulation
        npc.setNextForceTalk(new ForceTalk("I AM THE VOID! I AM EVERYWHERE!"));
        
        // Teleport player into the chaos
        WorldTile chaosTile = new WorldTile(npc.getX() + 1, npc.getY() + 1, npc.getPlane());
        player.setNextWorldTile(chaosTile);
        player.setNextGraphics(new Graphics(342));
        
        // Ultimate chaos combo
        performUltimateChaosCombo(npc, player, chaosStats);
    }

    /**
     * CHAOS VOID GUIDANCE SYSTEM: Provide comprehensive chaotic guidance
     */
    private void provideChaosVoidGuidance(NPC npc, Entity target, int bossType, int bossTier, ChaosVoidStats chaosStats) {
        if (!(target instanceof Player) || !shouldShowGuidance()) {
            return;
        }
        
        Player player = (Player) target;
        long currentTime = System.currentTimeMillis();
        
        // Initial encounter guidance
        if (guidancePhase == 0) {
            sendChaosMessage(player, "A " + getBossTierName(chaosStats.tier) + " materializes from the void!");
            sendChaosGuidanceMessage(player, "CHAOS ENTITY: Unpredictable attacks, reality-bending abilities!");
            sendChaosGuidanceMessage(player, "VOID TIP: This entity defies logic - expect the unexpected!");
            guidancePhase = 1;
            lastGuidanceTime = currentTime;
            return;
        }
        
        // Health-based chaos guidance
        double healthPercent = (double) npc.getHitpoints() / npc.getMaxHitpoints();
        
        if (healthPercent <= 0.75 && guidancePhase == 1) {
            sendChaosMessage(player, "Reality grows unstable as my form weakens!");
            sendChaosGuidanceMessage(player, "75% HEALTH: Chaos intensifies - more frequent special attacks!");
            guidancePhase = 2;
            lastGuidanceTime = currentTime;
        } else if (healthPercent <= 0.5 && guidancePhase == 2) {
            sendChaosMessage(player, "The void calls... my power grows erratic!");
            sendChaosGuidanceMessage(player, "50% HEALTH: Enhanced chaos abilities - longer special attacks!");
            guidancePhase = 3;
            lastGuidanceTime = currentTime;
        } else if (healthPercent <= 0.25 && guidancePhase == 3) {
            sendChaosMessage(player, "Chaos consumes me! Reality means nothing!");
            sendChaosGuidanceMessage(player, "CHAOS STORM: Maximum unpredictability - all attacks enhanced!");
            guidancePhase = 4;
            lastGuidanceTime = currentTime;
        }
        
        // Special attack guidance
        if (specialRapidMagic && currentTime - lastGuidanceTime > GUIDANCE_COOLDOWN / 2) {
            sendChaosGuidanceMessage(player, "RAPID MAGIC: Extended barrage active - maintain protection!");
            lastGuidanceTime = currentTime;
        } else if (sprayAttack && currentTime - lastGuidanceTime > GUIDANCE_COOLDOWN / 2) {
            sendChaosGuidanceMessage(player, "VOID SPRAY: Area effect incoming - spread out from allies!");
            lastGuidanceTime = currentTime;
        }
        
        // Tier-specific advanced guidance
        if (bossTier >= 7 && currentTime - lastGuidanceTime > GUIDANCE_COOLDOWN) {
            provideTierSpecificChaosGuidance(player, bossTier, chaosStats);
            lastGuidanceTime = currentTime;
        }
    }
    
    /**
     * Provide tier-specific chaos guidance
     */
    private void provideTierSpecificChaosGuidance(Player player, int bossTier, ChaosVoidStats chaosStats) {
        switch (bossTier) {
            case 7: // Elite
                sendChaosGuidanceMessage(player, "ELITE CHAOS: Reality-bending abilities against safe spotters!");
                break;
            case 8: // Legendary
                sendChaosGuidanceMessage(player, "LEGENDARY CHAOS: Multi-dimensional combat capabilities!");
                break;
            case 9: // Mythical
                sendChaosGuidanceMessage(player, "MYTHICAL CHAOS: The void itself fights alongside this entity!");
                break;
            case 10: // Divine
                sendChaosGuidanceMessage(player, "DIVINE CHAOS: Pure chaos incarnate - reality is meaningless!");
                break;
        }
    }

    /**
     * Provide dynamic battle guidance based on combat situation
     */
    private void provideDynamicBattleGuidance(Player player, NPC npc, ChaosVoidStats chaosStats) {
        String username = player.getUsername();
        long currentTime = System.currentTimeMillis();
        
        // Check guidance cooldown
        Long lastGuidance = playerLastGuidance.get(username);
        if (lastGuidance != null && (currentTime - lastGuidance) < GUIDANCE_COOLDOWN) {
            return;
        }
        
        // Get combat stage
        Integer combatStage = playerCombatStage.get(username);
        if (combatStage == null) combatStage = 0;
        
        // Calculate boss health percentage
        int currentHp = npc.getHitpoints();
        int maxHp = chaosStats.hitpoints;
        double hpPercentage = (double) currentHp / maxHp;
        
        String guidanceMessage = null;
        
        // Phase-based guidance
        if (combatStage == 0) {
            guidanceMessage = getBossOpeningGuidance(chaosStats);
            playerCombatStage.put(username, 1);
        } else if (hpPercentage <= 0.75 && combatStage == 1) {
            guidanceMessage = getBossPhaseGuidance(chaosStats, "75%");
            playerCombatStage.put(username, 2);
        } else if (hpPercentage <= 0.50 && combatStage == 2) {
            guidanceMessage = getBossPhaseGuidance(chaosStats, "50%");
            playerCombatStage.put(username, 3);
        } else if (hpPercentage <= 0.25 && combatStage == 3) {
            guidanceMessage = getBossPhaseGuidance(chaosStats, "25%");
            playerCombatStage.put(username, 4);
        } else if (hpPercentage <= 0.10 && combatStage == 4) {
            guidanceMessage = getBossPhaseGuidance(chaosStats, "10%");
            playerCombatStage.put(username, 5);
        }
        
        // Send guidance message as NPC speech
        if (guidanceMessage != null) {
            sendBossGuidance(player, npc, guidanceMessage, chaosStats);
            playerLastGuidance.put(username, currentTime);
        }
    }

    /**
     * Send boss guidance as chaotic speech
     */
    private void sendBossGuidance(Player player, NPC npc, String message, ChaosVoidStats chaosStats) {
        String tierPrefix = getTierPrefix(chaosStats.tier);
        String fullMessage = tierPrefix + " Blink: " + message;
        
        player.sendMessage("<col=FF00FF>" + fullMessage + "</col>", true);
        
        try {
            npc.setNextForceTalk(new ForceTalk(message));
        } catch (Exception e) {
            // ForceTalk failed, continue without it
        }
        
        if (chaosStats.tier >= 8) {
            player.sendMessage("<col=FF1493>>>> LEGENDARY CHAOS INCARNATE <<<</col>", true);
        }
    }

    /**
     * Get boss opening guidance
     */
    private String getBossOpeningGuidance(ChaosVoidStats chaosStats) {
        if (chaosStats.tier <= 3) {
            return "Reality shifts around me! Prepare for chaotic combat!";
        } else if (chaosStats.tier <= 6) {
            return "I exist between dimensions! My attacks defy comprehension!";
        } else if (chaosStats.tier <= 8) {
            return "I am chaos incarnate! Witness the power of the void!";
        } else {
            return "REALITY BENDS TO MY WILL! CHAOS IS ETERNAL!";
        }
    }

    /**
     * Get boss phase guidance
     */
    private String getBossPhaseGuidance(ChaosVoidStats chaosStats, String phase) {
        switch (phase) {
            case "75%":
                if (chaosStats.tier <= 5) {
                    return "Your strikes confuse me... but chaos adapts!";
                } else {
                    return "Impressive attacks! But I am beyond your understanding!";
                }
                
            case "50%":
                if (chaosStats.tier <= 5) {
                    return "The void calls to me! My special attacks grow stronger!";
                } else {
                    return "Elite chaos unleashed! Reality itself becomes my weapon!";
                }
                
            case "25%":
                if (chaosStats.tier <= 5) {
                    return "Rapid magic storms incoming! Chaos cannot be contained!";
                } else {
                    return "You face pure chaos! My void powers reach their peak!";
                }
                
            case "10%":
                if (chaosStats.tier <= 5) {
                    return "Impossible! But chaos is eternal and unpredictable!";
                } else {
                    return "I AM THE ETERNAL VOID! CHAOS TRANSCENDS DESTRUCTION!";
                }
        }
        return null;
    }

    /**
     * Get tier prefix for messages
     */
    private String getTierPrefix(int tier) {
        if (tier <= 3) return "Void Wanderer";
        else if (tier <= 5) return "Chaos Entity";
        else if (tier <= 7) return "Elite Void Lord";
        else if (tier <= 9) return "Legendary Chaos God";
        else return "Divine Void Emperor";
    }

    /**
     * Announce boss tier to all players in the area
     */
    private void announceBossTier(NPC npc, ChaosVoidStats chaosStats) {
        int npcId = npc.getId();
        long currentTime = System.currentTimeMillis();
        
        Long lastAnnouncement = bossLastTierAnnouncement.get(npcId + npc.hashCode());
        if (lastAnnouncement != null && (currentTime - lastAnnouncement) < TIER_ANNOUNCEMENT_COOLDOWN) {
            return;
        }
        
        for (Entity entity : npc.getPossibleTargets()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                
                String tierName = getBossTierName(chaosStats.tier);
                String balanceStatus = chaosStats.isBalanced ? "Balanced" : "Estimated";
                
                player.sendMessage("<col=FF00FF>Blink materializes from the void! " + tierName + " (" + balanceStatus + ")</col>", true);
                
                if (chaosStats.tier >= 7) {
                    player.sendMessage("<col=FF6B35>Warning: Elite chaos entity with reality-bending abilities!</col>", true);
                }
            }
        }
        
        bossLastTierAnnouncement.put(npcId + npc.hashCode(), currentTime);
    }

    /**
     * Get balanced chaos void stats with caching
     */
    private ChaosVoidStats getBalancedChaosStats(NPC npc) {
        int npcId = npc.getId();
        
        ChaosVoidStats cached = chaosStatsCache.get(npcId);
        if (cached != null && System.currentTimeMillis() - cached.timestamp < 300000) {
            return cached;
        }

        ChaosVoidStats stats = new ChaosVoidStats();
        
        try {
            stats.tier = readTierFromBossFile(npcId);
            
            if (stats.tier == -1) {
                stats.tier = estimateChaosTierFromStats(npc.getCombatDefinitions());
            }
            
            stats.maxHit = npc.getCombatDefinitions().getMaxHit();
            stats.hitpoints = npc.getCombatDefinitions().getHitpoints();
            
            int[] bonuses = NPCBonuses.getBonuses(npcId);
            if (bonuses != null && bonuses.length >= 10) {
                stats.attackBonuses = new int[]{bonuses[0], bonuses[1], bonuses[2], bonuses[3], bonuses[4]};
                stats.defenseBonuses = new int[]{bonuses[5], bonuses[6], bonuses[7], bonuses[8], bonuses[9]};
                stats.maxBonus = getMaxBonus(bonuses);
                stats.isBalanced = true;
            } else {
                stats.attackBonuses = estimateChaosAttackBonuses(stats.tier);
                stats.defenseBonuses = estimateChaosDefenseBonuses(stats.tier);
                stats.maxBonus = getMaxBonus(stats.attackBonuses);
                stats.isBalanced = false;
            }
            
            // Calculate chaos void specific stats
            stats.meleeMaxHit = calculateChaosDamage(stats.maxHit, stats.attackBonuses[1], 1.0);
            stats.rangedMaxHit = calculateChaosDamage(stats.maxHit, stats.attackBonuses[4], 1.1);
            stats.magicMaxHit = calculateChaosDamage(stats.maxHit, stats.attackBonuses[3], 1.2);
            stats.sprayMaxHit = calculateChaosDamage(stats.maxHit, stats.attackBonuses[3], 1.5);
            stats.rapidMagicMaxHit = calculateChaosDamage(stats.maxHit, stats.attackBonuses[3], 0.7);
            
            // Tier-based chaos abilities
            stats.rapidMagicChance = Math.min(25, 5 + (stats.tier * 2));
            stats.sprayAttackChance = Math.min(20, 2 + (stats.tier * 1));
            stats.rapidMagicDuration = Math.max(3, 3 + stats.tier);
            stats.lowHpRapidMagicDuration = Math.max(8, 6 + (stats.tier * 2));
            stats.soundEffectChance = Math.max(15, 40 - stats.tier);
            
            stats.timestamp = System.currentTimeMillis();
            chaosStatsCache.put(npcId, stats);
            
        } catch (Exception e) {
            Logger.handle(e);
            // Safe fallback values for chaos entity
            stats.tier = 5;
            stats.maxHit = 280;
            stats.meleeMaxHit = 280;
            stats.rangedMaxHit = 308;
            stats.magicMaxHit = 336;
            stats.sprayMaxHit = 420;
            stats.rapidMagicMaxHit = 196;
            stats.hitpoints = 8000;
            stats.attackBonuses = new int[]{400, 500, 400, 650, 550};
            stats.defenseBonuses = new int[]{350, 350, 350, 500, 450};
            stats.maxBonus = 650;
            stats.rapidMagicChance = 15;
            stats.sprayAttackChance = 7;
            stats.rapidMagicDuration = 8;
            stats.lowHpRapidMagicDuration = 16;
            stats.soundEffectChance = 35;
            stats.isBalanced = false;
        }
        
        return stats;
    }

    /**
     * Read tier from boss file created by BossBalancer
     */
    private int readTierFromBossFile(int npcId) {
        try {
            java.io.File bossFile = new java.io.File("data/npcs/bosses/" + npcId + ".txt");
            if (!bossFile.exists()) {
                return -1;
            }
            
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(bossFile));
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("tier=")) {
                    reader.close();
                    return Integer.parseInt(line.substring(5));
                }
            }
            reader.close();
            
        } catch (Exception e) {
            // File reading failed, will fall back to estimation
        }
        
        return -1;
    }

    /**
     * Execute chaos void combat with tier-based selection
     */
    private int executeChaosVoidCombat(final NPC npc, final Entity target, final ChaosVoidStats chaosStats, int bossTier) {
        int attackStyle = Utils.random(3);
        int distanceX = target.getX() - npc.getX();
        int distanceY = target.getY() - npc.getY();
        int size = npc.getSize();

        // Track successful hits for safe spot prevention
        if (target instanceof Player) {
            playerLastValidHit.put(((Player) target).getUsername(), System.currentTimeMillis());
        }

        if (Utils.random(100) < chaosStats.soundEffectChance) {
            executeAttackSounds(npc);
        }
        
        if (!specialRapidMagic && shouldTriggerRapidMagic(npc, chaosStats) && !sprayAttack) {
            executeSpecialRapidMagic(npc, target, chaosStats);
            return getBalancedAttackDelay(5, bossTier);
        } 
        else if (!sprayAttack && shouldTriggerSprayAttack(npc, chaosStats)) {
            executeSprayAttack(npc, target, chaosStats);
            return getBalancedAttackDelay(5, bossTier);
        }

        if (attackStyle == 2 && !specialRapidMagic && !sprayAttack) {
            return executeMeleeAttack(npc, target, distanceX, distanceY, size, chaosStats, bossTier);
        } else if (attackStyle == 1 && !specialRapidMagic && !sprayAttack) {
            return executeRangedAttack(npc, target, chaosStats, bossTier);
        } else if (attackStyle == 0 && !specialRapidMagic && !sprayAttack) {
            return executeMagicAttack(npc, target, chaosStats, bossTier);
        }
        
        return getBalancedAttackDelay(5, bossTier);
    }

    /**
     * BOSS BALANCER INTEGRATION: Get boss type (Glass Cannon for chaotic entity)
     */
    private int getBossType(NPC npc) {
        // Blink is a Glass Cannon - high damage, erratic defenses, chaos-based
        return 5; // Glass Cannon
    }
    
    /**
     * BOSS BALANCER INTEGRATION: Get boss tier
     */
    private int getBossTier(NPC npc, ChaosVoidStats chaosStats) {
        return chaosStats.tier;
    }
    
    /**
     * BOSS BALANCER INTEGRATION: Get balanced attack delay
     */
    private int getBalancedAttackDelay(int baseDelay, int bossTier) {
        // Glass Cannon bosses have faster, more erratic attacks
        if (bossTier >= 7) {
            return Math.max(2, baseDelay - Utils.random(3)); // Very unpredictable for elite+
        } else if (bossTier >= 5) {
            return Math.max(3, baseDelay - Utils.random(2)); // Moderately unpredictable
        }
        return Math.max(4, baseDelay - 1); // Slightly faster than normal
    }

    /**
     * Determine if rapid magic should trigger
     */
    private boolean shouldTriggerRapidMagic(NPC npc, ChaosVoidStats chaosStats) {
        double hpPercentage = (double) npc.getHitpoints() / npc.getMaxHitpoints();
        if (hpPercentage <= 0.25) {
            return Utils.random(100) < (chaosStats.rapidMagicChance * 2);
        }
        
        return Utils.random(100) < chaosStats.rapidMagicChance;
    }

    /**
     * Determine if spray attack should trigger
     */
    private boolean shouldTriggerSprayAttack(NPC npc, ChaosVoidStats chaosStats) {
        return Utils.random(100) < chaosStats.sprayAttackChance;
    }

    /**
     * Execute enhanced attack sounds with original chaotic personality
     */
    private void executeAttackSounds(NPC npc) {
        switch (Utils.random(5)) {
            case 0:
                npc.playSound(3004, 2);
                break;
            case 1:
                npc.setNextForceTalk(new ForceTalk("A face! A huuuge face!"));
                npc.playSound(3026, 2);
                break;
            case 2:
                npc.setNextForceTalk(new ForceTalk("A whole new world!"));
                npc.playSound(3042, 2);
                break;
            case 3:
                npc.setNextForceTalk(new ForceTalk("There's no place like home"));
                npc.playSound(3046, 2);
                break;
            case 4:
                npc.setNextForceTalk(new ForceTalk("The...spire...doors...everywhere..."));
                npc.playSound(3049, 2);
                break;
        }
    }

    /**
     * Execute enhanced special rapid magic attack
     */
    private void executeSpecialRapidMagic(final NPC npc, final Entity target, final ChaosVoidStats chaosStats) {
        double hpPercentage = (double) npc.getHitpoints() / npc.getMaxHitpoints();
        boolean isLowHp = hpPercentage <= 0.25;
        
        npc.setNextForceTalk(new ForceTalk(
            isLowHp ? "Ah! Grrr... Can't.. Stop me!" : "Aha!"));
        npc.setNextAnimation(new Animation(14956));
        npc.setNextGraphics(new Graphics(2854));
        specialRapidMagic = true;
        
        provideChaosEducation(npc.getPossibleTargets(), npc, "RAPID_MAGIC", chaosStats);
        
        WorldTasksManager.schedule(new WorldTask() {
            int count = 0;
            
            @Override
            public void run() {
                npc.setNextAnimation(new Animation(14956));
                npc.setNextGraphics(new Graphics(2854));
                
                for (Entity t : npc.getPossibleTargets()) {
                    if (!t.withinDistance(npc, 10))
                        continue;
                    
                    int rapidDamage = calculateBalancedRapidMagicDamage(npc, chaosStats, t);
                    delayHit(npc, 1, t, new Hit(npc, rapidDamage, HitLook.MAGIC_DAMAGE));
                    t.setNextGraphics(new Graphics(2855, 0, 0));
                }
                
                int maxDuration = isLowHp ? chaosStats.lowHpRapidMagicDuration : chaosStats.rapidMagicDuration;
                
                if (count++ >= maxDuration || npc.isDead()) {
                    stop();
                    specialRapidMagic = false;
                    return;
                }
            }
        }, 0, 2);
    }

    /**
     * Execute enhanced spray attack
     */
    private void executeSprayAttack(final NPC npc, final Entity target, final ChaosVoidStats chaosStats) {
        npc.setNextForceTalk(new ForceTalk("Taste.... my... Spray!"));
        sprayAttack = true;
        
        provideChaosEducation(npc.getPossibleTargets(), npc, "SPRAY", chaosStats);
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                npc.setNextGraphics(new Graphics(2869));
                
                for (Entity t : npc.getPossibleTargets()) {
                    if (!t.withinDistance(npc, 3))
                        continue;
                    
                    int sprayDamage = calculateBalancedSprayDamage(npc, chaosStats, t);
                    delayHit(npc, 1, t, new Hit(npc, sprayDamage, HitLook.MAGIC_DAMAGE));
                    t.setNextGraphics(new Graphics(2855, 0, 0));
                }
                sprayAttack = false;
            }
        }, 3);
    }

    /**
     * Execute enhanced melee attack
     */
    private int executeMeleeAttack(NPC npc, Entity target, int distanceX, int distanceY, int size, ChaosVoidStats chaosStats, int bossTier) {
        if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
            return executeRangedAttack(npc, target, chaosStats, bossTier);
        } else {
            if (npc.withinDistance(target, 3)) {
                int meleeDamage = calculateBalancedMeleeDamage(npc, chaosStats, target);
                delayHit(npc, 1, target, getMeleeHit(npc, meleeDamage));
                
                if (target instanceof Player && Utils.random(7) == 0) {
                    provideCombatEducation((Player) target, npc, "MELEE", chaosStats);
                }
                
                return getBalancedAttackDelay(4, bossTier);
            }
        }
        return getBalancedAttackDelay(5, bossTier);
    }

    /**
     * Execute enhanced ranged attack
     */
    private int executeRangedAttack(NPC npc, Entity target, ChaosVoidStats chaosStats, int bossTier) {
        if (!npc.withinDistance(target, 3)) {
            npc.setNextAnimation(new Animation(14949, 20));
            World.sendProjectile(npc, target, 2853, 18, 18, 50, 50, 0, 0);
            
            int rangedDamage = calculateBalancedRangedDamage(npc, chaosStats, target);
            delayHit(npc, 1, target, getRangeHit(npc, rangedDamage));
            
            if (target instanceof Player && Utils.random(7) == 0) {
                provideCombatEducation((Player) target, npc, "RANGED", chaosStats);
            }
        }
        return getBalancedAttackDelay(5, bossTier);
    }

    /**
     * Execute enhanced magic attack
     */
    private int executeMagicAttack(NPC npc, Entity target, ChaosVoidStats chaosStats, int bossTier) {
        if (npc.withinDistance(target, 3)) {
            if (Utils.random(3) == 0)
                npc.setNextForceTalk(new ForceTalk("Magicinyaface!"));
            npc.setNextAnimation(new Animation(14956));
            npc.setNextGraphics(new Graphics(2854));
            
            int magicDamage = calculateBalancedMagicDamage(npc, chaosStats, target);
            delayHit(npc, 1, target, getMagicHit(npc, magicDamage));
            
            if (target instanceof Player && Utils.random(7) == 0) {
                provideCombatEducation((Player) target, npc, "MAGIC", chaosStats);
            }
        }
        return getBalancedAttackDelay(5, bossTier);
    }

    /**
     * Calculate balanced damage methods
     */
    private int calculateBalancedMeleeDamage(NPC npc, ChaosVoidStats chaosStats, Entity target) {
        int baseDamage = getRandomMaxHit(npc, chaosStats.meleeMaxHit, NPCCombatDefinitions.MELEE, target);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            if (player.getPrayer().usingPrayer(0, 18) || player.getPrayer().usingPrayer(1, 8)) {
                baseDamage = (int)(baseDamage * 0.6);
            }
        }
        
        return Math.max(1, baseDamage);
    }

    private int calculateBalancedRangedDamage(NPC npc, ChaosVoidStats chaosStats, Entity target) {
        int baseDamage = getRandomMaxHit(npc, chaosStats.rangedMaxHit, NPCCombatDefinitions.RANGE, target);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            if (player.getPrayer().usingPrayer(0, 16) || player.getPrayer().usingPrayer(1, 6)) {
                baseDamage = (int)(baseDamage * 0.6);
            }
        }
        
        return Math.max(1, baseDamage);
    }

    private int calculateBalancedMagicDamage(NPC npc, ChaosVoidStats chaosStats, Entity target) {
        int baseDamage = getRandomMaxHit(npc, chaosStats.magicMaxHit, NPCCombatDefinitions.MAGE, target);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            if (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)) {
                baseDamage = (int)(baseDamage * 0.6);
            }
        }
        
        return Math.max(1, baseDamage);
    }

    private int calculateBalancedSprayDamage(NPC npc, ChaosVoidStats chaosStats, Entity target) {
        int baseDamage = getRandomMaxHit(npc, chaosStats.sprayMaxHit, NPCCombatDefinitions.MAGE, target);
        baseDamage = Math.max((int)(chaosStats.sprayMaxHit * 0.3), baseDamage);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            if (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)) {
                baseDamage = (int)(baseDamage * 0.6);
            }
        }
        
        return Math.max(1, baseDamage);
    }

    private int calculateBalancedRapidMagicDamage(NPC npc, ChaosVoidStats chaosStats, Entity target) {
        int baseDamage = getRandomMaxHit(npc, chaosStats.rapidMagicMaxHit, NPCCombatDefinitions.MAGE, target);
        baseDamage = Math.max((int)(chaosStats.rapidMagicMaxHit * 0.2), baseDamage);
        
        if (target instanceof Player) {
            Player player = (Player) target;
            if (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)) {
                baseDamage = (int)(baseDamage * 0.6);
            }
        }
        
        return Math.max(1, baseDamage);
    }

    /**
     * Calculate chaos damage based on attack bonus and modifier
     */
    private int calculateChaosDamage(int baseMaxHit, int attackBonus, double modifier) {
        int damage = (int) (baseMaxHit * modifier);
        
        if (attackBonus > 0) {
            damage = (int) (damage * (1.0 + (attackBonus * 0.0008)));
        }
        
        return Math.max(1, damage);
    }

    /**
     * Safe spot prevention helper methods
     */
    private boolean canChaosVoidReachPlayer(NPC npc, Player player, ChaosVoidStats chaosStats) {
        int distance = Utils.getDistance(npc, player);
        
        // Chaos entities have reality-bending capabilities
        // Higher tier chaos can reach further through void manipulation
        int maxReach = Math.min(8 + chaosStats.tier, 15);
        
        return distance <= maxReach;
    }
    
    private WorldTile findChaosPosition(NPC npc, WorldTile playerTile) {
        // Find position near player for chaotic teleportation
        for (int distance = 2; distance <= 4; distance++) {
            for (int x = -distance; x <= distance; x++) {
                for (int y = -distance; y <= distance; y++) {
                    WorldTile testTile = new WorldTile(playerTile.getX() + x, playerTile.getY() + y, npc.getPlane());
                    if (isValidChaosPosition(testTile)) {
                        return testTile;
                    }
                }
            }
        }
        return null;
    }
    
    private boolean isValidChaosPosition(WorldTile tile) {
        return tile != null; // Simplified validation
    }
    
    private void performChaosRealityStorm(NPC npc, Player player, ChaosVoidStats chaosStats) {
        // Multi-dimensional chaos assault
        npc.setNextAnimation(new Animation(14956));
        npc.setNextGraphics(new Graphics(2854));
        
        // Triple chaos attack from different dimensions
        for (int i = 0; i < 3; i++) {
            final int phase = i;
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    int damage = (int)(chaosStats.magicMaxHit * 1.1);
                    player.applyHit(new Hit(npc, damage, HitLook.MAGIC_DAMAGE, 0));
                    player.setNextGraphics(new Graphics(2855, 0, 0));
                }
            }, i * 2);
        }
    }
    
    private void applyChaosField(Player player) {
        // Temporary chaos field effect
        player.setNextGraphics(new Graphics(281)); // Chaos field effect
    }
    
    private void performUltimateChaosCombo(NPC npc, Player player, ChaosVoidStats chaosStats) {
        // Ultimate 5-phase chaos combo
        WorldTasksManager.schedule(new WorldTask() {
            private int phase = 0;
            
            @Override
            public void run() {
                switch (phase) {
                    case 0: // Void spray
                        executeSprayAttack(npc, player, chaosStats);
                        break;
                    case 1: // Chaos magic
                        int magicDamage = (int)(chaosStats.magicMaxHit * 1.2);
                        player.applyHit(new Hit(npc, magicDamage, HitLook.MAGIC_DAMAGE, 0));
                        break;
                    case 2: // Void projectile
                        int rangedDamage = (int)(chaosStats.rangedMaxHit * 1.2);
                        player.applyHit(new Hit(npc, rangedDamage, HitLook.RANGE_DAMAGE, 0));
                        break;
                    case 3: // Chaos melee
                        int meleeDamage = (int)(chaosStats.meleeMaxHit * 1.2);
                        player.applyHit(new Hit(npc, meleeDamage, HitLook.MELEE_DAMAGE, 0));
                        break;
                    case 4: // Final chaos burst
                        executeSpecialRapidMagic(npc, player, chaosStats);
                        this.stop();
                        return;
                }
                phase++;
            }
        }, 0, 2);
    }

    /**
     * Check if player is attacking the boss
     */
    private boolean isPlayerAttackingBoss(Player player, NPC npc) {
        try {
            int distance = Utils.getDistance(player, npc);
            return distance <= 15 && player.withinDistance(npc);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Provide chaos-specific education
     */
    private void provideChaosEducation(java.util.ArrayList<Entity> targets, NPC npc, String specialType, ChaosVoidStats chaosStats) {
        for (Entity entity : targets) {
            if (entity instanceof Player && Utils.random(3) == 0) {
                Player player = (Player) entity;
                String tipMessage = getChaosSpecialTip(chaosStats.tier, specialType);
                if (tipMessage != null) {
                    sendChaosGuidanceMessage(player, tipMessage);
                }
                break;
            }
        }
    }

    /**
     * Provide combat education
     */
    private void provideCombatEducation(Player player, NPC npc, String attackStyle, ChaosVoidStats chaosStats) {
        String username = player.getUsername();
        long currentTime = System.currentTimeMillis();
        
        Long lastTip = playerLastChaosTip.get(username);
        if (lastTip != null && (currentTime - lastTip) < CHAOS_TIP_COOLDOWN) {
            return;
        }
        
        Integer tipStage = playerChaosTipStage.get(username);
        if (tipStage == null) tipStage = 0;
        if (tipStage >= MAX_CHAOS_TIPS_PER_FIGHT) return;
        
        String tipMessage = getChaosCombatTip(chaosStats.tier, attackStyle, tipStage);
        if (tipMessage != null) {
            sendChaosGuidanceMessage(player, tipMessage);
            
            playerLastChaosTip.put(username, currentTime);
            playerChaosTipStage.put(username, tipStage + 1);
        }
    }

    /**
     * Get chaos special attack tip
     */
    private String getChaosSpecialTip(int tier, String specialType) {
        if (specialType.equals("RAPID_MAGIC")) {
            if (tier <= 4) {
                return "RAPID MAGIC: Blink's chaos barrage! Maintain Protect from Magic!";
            } else {
                return "ELITE RAPID MAGIC: Extended chaos storm - stay protected!";
            }
        } else if (specialType.equals("SPRAY")) {
            if (tier <= 4) {
                return "VOID SPRAY: Area effect within 3 squares - spread out!";
            } else {
                return "ELITE VOID SPRAY: Enhanced chaos toxicity - maximum distance!";
            }
        }
        return null;
    }

    /**
     * Get chaos combat tip
     */
    private String getChaosCombatTip(int tier, String attackStyle, int stage) {
        if (stage == 0) {
            if (attackStyle.equals("MELEE")) {
                return "CHAOS MELEE: Close-range chaotic strikes - maintain distance!";
            } else if (attackStyle.equals("RANGED")) {
                return "VOID PROJECTILES: Long-range chaos - use Protect from Missiles!";
            } else if (attackStyle.equals("MAGIC")) {
                return "CHAOS MAGIC: Reality-bending attacks - use Protect from Magic!";
            }
        } else if (stage == 1) {
            return "UNPREDICTABLE: Blink defies logic - adapt prayers constantly!";
        } else if (stage == 2) {
            return "TIER " + tier + ": Higher chaos tiers have enhanced special abilities!";
        } else if (stage == 3) {
            return "LOW HEALTH: Chaos intensifies when wounded - extended special attacks!";
        } else if (stage == 4) {
            return "VOID MASTERY: Distance determines attack style - control positioning!";
        } else if (stage == 5) {
            return "REALITY BENDING: Elite chaos can overcome safe spots!";
        } else if (stage == 6) {
            return "CHAOS INCARNATE: Listen for audio cues - they predict attacks!";
        }
        return null;
    }

    /**
     * Helper methods for guidance system
     */
    private boolean shouldShowGuidance() {
        return System.currentTimeMillis() - lastGuidanceTime > GUIDANCE_COOLDOWN;
    }
    
    private void sendChaosMessage(Player player, String message) {
        player.sendMessage("<col=FF00FF>[Blink]: " + message + "</col>");
    }
    
    private void sendChaosGuidanceMessage(Player player, String message) {
        player.sendMessage("<col=9932CC>[Chaos Guide]: " + message + "</col>");
    }

    /**
     * Get boss tier name for announcements
     */
    private String getBossTierName(int tier) {
        switch (tier) {
            case 1: return "Tier 1 Unstable Entity";
            case 2: return "Tier 2 Chaotic Spirit";
            case 3: return "Tier 3 Void Wanderer";
            case 4: return "Tier 4 Dimensional Shifter";
            case 5: return "Tier 5 Chaos Incarnate";
            case 6: return "Tier 6 Elite Void Lord";
            case 7: return "Tier 7 Master Chaos Entity";
            case 8: return "Tier 8 Legendary Void Master";
            case 9: return "Tier 9 Mythical Chaos God";
            case 10: return "Tier 10 Divine Void Emperor";
            default: return "Unknown Tier Chaos Entity";
        }
    }

    /**
     * Estimate chaos entity tier from combat stats
     */
    private int estimateChaosTierFromStats(NPCCombatDefinitions defs) {
        int hp = defs.getHitpoints();
        int maxHit = defs.getMaxHit();
        
        if (hp <= 3200 && maxHit <= 50) return 3;
        if (hp <= 6000 && maxHit <= 80) return 4;
        if (hp <= 10500 && maxHit <= 125) return 5;
        if (hp <= 17000 && maxHit <= 185) return 6;
        if (hp <= 25500 && maxHit <= 260) return 7;
        if (hp <= 36000 && maxHit <= 350) return 8;
        if (hp <= 50000 && maxHit <= 460) return 9;
        return 10;
    }

    /**
     * Estimate chaos attack bonuses for tier
     */
    private int[] estimateChaosAttackBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        return new int[]{
            (int)(baseStat * 0.7),      // stab (reduced)
            (int)(baseStat * 0.9),      // slash (slightly reduced)
            (int)(baseStat * 0.7),      // crush (reduced)
            (int)(baseStat * 1.4),      // magic (heavily enhanced for chaos)
            (int)(baseStat * 1.2)       // ranged (enhanced for void projectiles)
        };
    }

    /**
     * Estimate chaos defense bonuses for tier
     */
    private int[] estimateChaosDefenseBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        return new int[]{
            (int)(baseStat * 0.8),      // stab defense (reduced)
            (int)(baseStat * 0.8),      // slash defense (reduced)
            (int)(baseStat * 0.8),      // crush defense (reduced)
            (int)(baseStat * 1.2),      // magic defense (enhanced)
            baseStat                     // ranged defense (balanced)
        };
    }

    /**
     * Get maximum bonus from array
     */
    private int getMaxBonus(int[] bonuses) {
        int max = 0;
        for (int bonus : bonuses) {
            if (bonus > max) max = bonus;
        }
        return max;
    }

    @Override
    public Object[] getKeys() {
        return new Object[] { 12878 }; // Blink NPC ID
    }

    /**
     * Chaos Void stats container class
     */
    private static class ChaosVoidStats {
        public int tier;
        public int maxHit;
        public int meleeMaxHit;
        public int rangedMaxHit;
        public int magicMaxHit;
        public int sprayMaxHit;
        public int rapidMagicMaxHit;
        public int hitpoints;
        public int[] attackBonuses;
        public int[] defenseBonuses;
        public int maxBonus;
        public int rapidMagicChance;
        public int sprayAttackChance;
        public int rapidMagicDuration;
        public int lowHpRapidMagicDuration;
        public int soundEffectChance;
        public boolean isBalanced;
        public long timestamp;
    }
}