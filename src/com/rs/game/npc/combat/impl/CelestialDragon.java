package com.rs.game.npc.combat.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.dragons.CelestialDragonB;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.content.Combat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;
import com.rs.utils.Logger;

/**
 * Enhanced Celestial Dragon Combat - TIME MANIPULATION BOSS WITH BATTLE GUIDANCE
 * 
 * @author Enhanced by Balance System v2.1 - NULL REFERENCE FIXED
 * @date May 31, 2025
 * @version 2.2 - FIXED Null References + Enhanced Battle Guidance
 * Features: Battle Guidance, Time Trap Education, Balanced Scaling, Familiar Mechanics
 */
public class CelestialDragon extends CombatScript {

    // Enhanced education system for time manipulation mechanics
    private static final Map<String, Long> playerLastTimeTip = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Integer> playerTimeTipStage = new ConcurrentHashMap<String, Integer>();
    private static final Map<String, Long> playerLastGuidance = new ConcurrentHashMap<String, Long>();
    private static final Map<Integer, Long> bossLastTierAnnouncement = new ConcurrentHashMap<Integer, Long>();
    private static final Map<String, Integer> playerCombatStage = new ConcurrentHashMap<String, Integer>();
    
    private static final long TIME_TIP_COOLDOWN = 30000; // 30 seconds between time tips
    private static final long GUIDANCE_COOLDOWN = 45000; // 45 seconds between guidance messages
    private static final long TIER_ANNOUNCEMENT_COOLDOWN = 300000; // 5 minutes between tier announcements
    private static final int MAX_TIME_TIPS_PER_FIGHT = 6; // More tips for complex time mechanics

    // Boss stats cache for time manipulation boss
    private static final Map<Integer, TimeBossStats> timeBossStatsCache = new ConcurrentHashMap<Integer, TimeBossStats>();
    
    // Combat state variables (maintained from original)
    int tick = 0;
    boolean timeTrap;
    
    @Override
    public int attack(NPC npc, Entity target) {
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        
        // Get balanced time manipulation boss stats
        TimeBossStats bossStats = getBalancedTimeBossStats(npc);
        
        // Announce tier at the beginning (visible to all players)
        announceBossTier(npc, bossStats);
        
        // Provide dynamic battle guidance
        if (target instanceof Player) {
            provideDynamicBattleGuidance((Player) target, npc, bossStats);
        }
        
        // Maintain original tick-based attack system with tier enhancements
        if (tick < bossStats.timeTrapInterval) {
            if (npc.withinDistance(target, 2)) {
                executeMeleeAttack(npc, target, bossStats);
            } else {
                executeRangedAttack(npc, target, bossStats);
            }
        } else {
            if (!timeTrap) {
                executeTimeTrap(npc, target, bossStats);
            }
            tick = 0;
        }
        
        tick++;
        return defs.getAttackDelay();
    }

    /**
     * Provide dynamic battle guidance based on combat situation
     */
    private void provideDynamicBattleGuidance(Player player, NPC npc, TimeBossStats bossStats) {
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
        int maxHp = bossStats.hitpoints;
        double hpPercentage = (double) currentHp / maxHp;
        
        String guidanceMessage = null;
        
        // Phase-based guidance
        if (combatStage == 0) {
            // Opening phase
            guidanceMessage = getBossOpeningGuidance(bossStats);
            playerCombatStage.put(username, 1);
        } else if (hpPercentage <= 0.75 && combatStage == 1) {
            // 75% HP phase
            guidanceMessage = getBossPhaseGuidance(bossStats, "75%");
            playerCombatStage.put(username, 2);
        } else if (hpPercentage <= 0.50 && combatStage == 2) {
            // 50% HP phase
            guidanceMessage = getBossPhaseGuidance(bossStats, "50%");
            playerCombatStage.put(username, 3);
        } else if (hpPercentage <= 0.25 && combatStage == 3) {
            // 25% HP phase - urgent warnings
            guidanceMessage = getBossPhaseGuidance(bossStats, "25%");
            playerCombatStage.put(username, 4);
        } else if (hpPercentage <= 0.10 && combatStage == 4) {
            // Final phase - critical warnings
            guidanceMessage = getBossPhaseGuidance(bossStats, "10%");
            playerCombatStage.put(username, 5);
        } else if (tick == bossStats.timeTrapInterval - 1 && combatStage >= 1) {
            // Time trap warning
            guidanceMessage = "The dragon's temporal energy builds... Time trap incoming!";
        }
        
        // Send guidance message as NPC speech
        if (guidanceMessage != null) {
            sendBossGuidance(player, npc, guidanceMessage, bossStats);
            playerLastGuidance.put(username, currentTime);
        }
    }

    /**
     * Send boss guidance as NPC speech
     */
    private void sendBossGuidance(Player player, NPC npc, String message, TimeBossStats bossStats) {
        // Send as both NPC dialogue and colored message
        String tierPrefix = getTierPrefix(bossStats.tier);
        String fullMessage = tierPrefix + " Celestial Dragon: " + message;
        
        // Send as overhead text from the dragon
        player.sendMessage("<col=9932CC>" + fullMessage + "</col>", true);
        
        // For high tier bosses, add additional warning formatting
        if (bossStats.tier >= 8) {
            player.sendMessage("<col=FF1493>>>> LEGENDARY TEMPORAL ENTITY <<<</col>", true);
        }
    }

    /**
     * Get boss opening guidance
     */
    private String getBossOpeningGuidance(TimeBossStats bossStats) {
        if (bossStats.tier <= 3) {
            return "A young celestial awakens. Beware my time magic, mortal.";
        } else if (bossStats.tier <= 6) {
            return "You face a master of time itself. Prepare for temporal warfare!";
        } else if (bossStats.tier <= 8) {
            return "An elite time lord rises! Your familiar will not save you from my power!";
        } else {
            return "I am the master of time and space! Reality bends to my will!";
        }
    }

    /**
     * Get boss phase guidance
     */
    private String getBossPhaseGuidance(TimeBossStats bossStats, String phase) {
        switch (phase) {
            case "75%":
                if (bossStats.tier <= 5) {
                    return "You fight well, but my temporal powers grow stronger!";
                } else {
                    return "Impressive... but now you face my true temporal mastery!";
                }
                
            case "50%":
                if (bossStats.tier <= 5) {
                    return "Time itself fights against you! Feel my growing power!";
                } else {
                    return "The fabric of reality warps around me! Your doom approaches!";
                }
                
            case "25%":
                if (bossStats.tier <= 5) {
                    return "My time magic reaches its peak! Beware the temporal storm!";
                } else {
                    return "You have awakened my rage! Time will consume you!";
                }
                
            case "10%":
                if (bossStats.tier <= 5) {
                    return "Impossible! But I will not fall easily, time bender!";
                } else {
                    return "I AM THE ETERNAL DRAGON! TIME CANNOT DEFEAT TIME ITSELF!";
                }
        }
        return null;
    }

    /**
     * Get tier prefix for messages
     */
    private String getTierPrefix(int tier) {
        if (tier <= 3) return "Young";
        else if (tier <= 5) return "Elder";
        else if (tier <= 7) return "Elite";
        else if (tier <= 9) return "Legendary";
        else return "Divine";
    }

    /**
     * Announce boss tier to all players in the area (Simple One-Line Version)
     */
    private void announceBossTier(NPC npc, TimeBossStats bossStats) {
        int npcId = npc.getId();
        long currentTime = System.currentTimeMillis();
        
        // Check if we recently announced for this boss instance
        Long lastAnnouncement = bossLastTierAnnouncement.get(npcId + npc.hashCode());
        if (lastAnnouncement != null && (currentTime - lastAnnouncement) < TIER_ANNOUNCEMENT_COOLDOWN) {
            return; // Too soon for another announcement
        }
        
        // Find all nearby players for the announcement
        for (Entity entity : npc.getPossibleTargets()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                
                // Simple one-line announcement
                String tierName = getSimpleTierName(bossStats.tier);
                String balanceStatus = bossStats.isBalanced ? "Balanced" : "Estimated";
                
                player.sendMessage("<col=FFD700>A " + tierName + " Celestial Dragon awakens! (" + balanceStatus + " - Time Manipulation Boss)</col>", true);
                
                // Additional warning only for high tiers
                if (bossStats.tier >= 7) {
                    player.sendMessage("<col=FF6B35>Warning: Elite time dragon with devastating temporal abilities!</col>", true);
                }
            }
        }
        
        // Update announcement tracking
        bossLastTierAnnouncement.put(npcId + npc.hashCode(), currentTime);
    }

    /**
     * Get balanced time manipulation boss stats with caching - FIXED to read actual balanced tier
     */
    private TimeBossStats getBalancedTimeBossStats(NPC npc) {
        int npcId = npc.getId();
        
        // Check cache first
        TimeBossStats cached = timeBossStatsCache.get(npcId);
        if (cached != null && System.currentTimeMillis() - cached.timestamp < 300000) { // 5 min cache
            return cached;
        }

        TimeBossStats stats = new TimeBossStats();
        
        try {
            // FIXED: Try to read tier from boss files first
            stats.tier = readTierFromBossFile(npcId);
            
            // If no boss file found, estimate from combat stats
            if (stats.tier == -1) {
                stats.tier = estimateTimeBossTierFromStats(npc.getCombatDefinitions());
            }
            
            stats.maxHit = npc.getCombatDefinitions().getMaxHit();
            stats.hitpoints = npc.getCombatDefinitions().getHitpoints();
            
            // Get balanced bonuses from NPCBonuses system
            int[] bonuses = NPCBonuses.getBonuses(npcId);
            if (bonuses != null && bonuses.length >= 10) {
                stats.attackBonuses = new int[]{bonuses[0], bonuses[1], bonuses[2], bonuses[3], bonuses[4]};
                stats.defenseBonuses = new int[]{bonuses[5], bonuses[6], bonuses[7], bonuses[8], bonuses[9]};
                stats.maxBonus = getMaxBonus(bonuses);
                stats.isBalanced = true;
            } else {
                // Fallback: estimate time-manipulation appropriate bonuses
                stats.attackBonuses = estimateTimeAttackBonuses(stats.tier);
                stats.defenseBonuses = estimateTimeDefenseBonuses(stats.tier);
                stats.maxBonus = getMaxBonus(stats.attackBonuses);
                stats.isBalanced = false;
            }
            
            // Calculate time-manipulation specific stats using the CORRECT tier
            stats.meleeMaxHit = calculateTimeDamage(stats.maxHit, stats.attackBonuses[1], 1.0); // Slash-based
            stats.rangedMaxHit = calculateTimeDamage(stats.maxHit, stats.attackBonuses[4], 1.1); // Ranged enhanced
            stats.dragonFireMaxHit = calculateTimeDamage(stats.maxHit, stats.attackBonuses[3], 1.3); // Dragon fire (magic-based, enhanced)
            
            // Tier-based time manipulation mechanics using the CORRECT tier
            stats.timeTrapInterval = Math.max(3, 7 - (stats.tier / 3)); // 6-7 attacks at tier 1, 3-4 at tier 10
            stats.timeTrapDuration = Math.max(6000, 8000 + (stats.tier * 500)); // 8.5-13 seconds based on tier
            stats.familiarBanishDuration = Math.max(8000, 10000 + (stats.tier * 800)); // 10.8-18 seconds based on tier
            
            stats.timestamp = System.currentTimeMillis();
            timeBossStatsCache.put(npcId, stats);
            
        } catch (Exception e) {
            Logger.handle(e);
            // Safe fallback values - use tier 6 as default
            stats.tier = 6;
            stats.maxHit = 320;
            stats.meleeMaxHit = 320;
            stats.rangedMaxHit = 352;
            stats.dragonFireMaxHit = 416;
            stats.hitpoints = 16000;
            stats.attackBonuses = new int[]{600, 700, 600, 800, 750};
            stats.defenseBonuses = new int[]{650, 650, 650, 700, 700};
            stats.maxBonus = 800;
            stats.timeTrapInterval = 5;
            stats.timeTrapDuration = 11000;
            stats.familiarBanishDuration = 14800;
            stats.isBalanced = false;
        }
        
        return stats;
    }

    /**
     * ADDED: Read tier from boss file created by BossBalancer
     */
    private int readTierFromBossFile(int npcId) {
        try {
            java.io.File bossFile = new java.io.File("data/npcs/bosses/" + npcId + ".txt");
            if (!bossFile.exists()) {
                return -1; // No boss file found
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
        
        return -1; // Couldn't read tier from file
    }

    /**
     * Execute enhanced melee attack with balanced damage - FIXED NULL REFERENCE
     */
    private void executeMeleeAttack(NPC npc, Entity target, TimeBossStats bossStats) {
        NPCCombatDefinitions defs = npc.getCombatDefinitions();
        npc.setNextAnimation(new Animation(defs.getAttackEmote()));
        
        // FIXED: Calculate balanced melee damage with proper NPC reference
        int meleeDamage = calculateBalancedMeleeDamage(npc, bossStats, target);
        
        delayHit(npc, 0, target, getMeleeHit(npc, meleeDamage));
        
        // Provide melee education (reduced frequency)
        if (target instanceof Player && Utils.random(15) == 0) { // 6.7% chance
            provideTimeEducation((Player) target, npc, "MELEE", bossStats);
        }
    }

    /**
     * Execute enhanced ranged attack with balanced damage
     */
    private void executeRangedAttack(NPC npc, Entity target, TimeBossStats bossStats) {
        // Distance-based attack selection (maintained from original)
        switch (Utils.random(4)) {
            case 0:
            case 1:
                executeDragonFireAttack(npc, target, bossStats);
                break;
            case 2:
            case 3:
                executeRangeAttack(npc, target, bossStats);
                break;
        }
    }

    /**
     * Execute enhanced range attack with balanced damage - FIXED NULL REFERENCE
     */
    private void executeRangeAttack(NPC npc, Entity target, TimeBossStats bossStats) {
        npc.setNextAnimation(new Animation(26524));
        World.sendProjectile(npc, target, 16, 28, 16, 35, 20, 16, 0);
        
        // FIXED: Calculate balanced ranged damage with proper NPC reference
        int rangedDamage = calculateBalancedRangedDamage(npc, bossStats, target);
        
        delayHit(npc, 1, target, getRangeHit(npc, rangedDamage));
        
        // Provide ranged education (reduced frequency)
        if (target instanceof Player && Utils.random(15) == 0) { // 6.7% chance
            provideTimeEducation((Player) target, npc, "RANGED", bossStats);
        }
    }

    /**
     * Execute enhanced dragon fire attack with balanced damage - FIXED NULL REFERENCE
     */
    private void executeDragonFireAttack(NPC npc, Entity target, TimeBossStats bossStats) {
        final Player player = target instanceof Player ? (Player) target : null;
        
        // FIXED: Calculate balanced dragon fire damage with proper NPC reference
        int damage = calculateBalancedDragonFireDamage(npc, bossStats, target);
        
        if (target instanceof Player) {
            String message = Combat.getProtectMessage(player);
            if (message != null) {
                player.sendMessage(message, true);
                if (message.contains("fully"))
                    damage = 0;
                else if (message.contains("most"))
                    damage = (int)(damage * 0.05);
                else if (message.contains("some"))
                    damage = (int)(damage * 0.1);
            }
            if (damage > 0)
                player.sendMessage("You are hit by the dragon's fiery breath!", true);
        }
        
        npc.setNextAnimation(new Animation(14245));
        World.sendProjectile(npc, target, 439, 28, 16, 35, 20, 16, 0);
        delayHit(npc, 1, target, getRegularHit(npc, damage));
        
        // Provide dragon fire education (reduced frequency)
        if (target instanceof Player && Utils.random(12) == 0) { // 8.3% chance
            provideTimeEducation((Player) target, npc, "DRAGONFIRE", bossStats);
        }
    }

    /**
     * Execute enhanced time trap with tier-appropriate scaling
     */
    private void executeTimeTrap(NPC npc, Entity target, TimeBossStats bossStats) {
        final Player player = target instanceof Player ? (Player) target : null;
        CelestialDragonB celdragon = (CelestialDragonB) npc;
        
        if (target instanceof Player) {
            Familiar familiar = player.getFamiliar();
            timeTrap = true;
            
            // Provide time trap education
            provideTimeEducation(player, npc, "TIME_TRAP", bossStats);
            
            // Enhanced boss dialogue for time trap
            sendBossGuidance(player, npc, "Time bends to my will! You are trapped in temporal stasis!", bossStats);
            
            WorldTasksManager.schedule(new WorldTask() {
                int stage;
                
                @Override
                public void run() {
                    switch(stage) {
                        case 0:
                            npc.setNextGraphics(new Graphics(4614));
                            break;
                        case 1:
                            player.setNextGraphics(new Graphics(4613));
                            tick = 0;
                            
                            // Enhanced familiar teleportation with tier scaling
                            if (familiar != null) {
                                celdragon.familiarTeleported = true;
                                celdragon.familiarTile = new WorldTile(familiar.getX(), familiar.getY(), familiar.getPlane());
                                familiar.setNextWorldTile(new WorldTile(familiar.getX(), familiar.getY(), familiar.getPlane() + 1));
                                familiar.callBlocked = true;
                                familiar.setCantInteract(true);
                                
                                // Provide familiar banishment education with boss dialogue
                                sendBossGuidance(player, npc, "Your familiar is banished to another dimension!", bossStats);
                            }
                            
                            // Tier-appropriate freeze duration
                            player.addFreezeDelay(bossStats.timeTrapDuration);
                            
                            // Enhanced feedback message
                            player.sendMessage("You are trapped in a temporal prison! Time moves differently around you...", true);
                            break;
                        case 7:
                            player.setFreezeDelay(0);
                            if (familiar != null) {
                                familiar.setCantInteract(false);
                                familiar.callBlocked = false;
                                
                                sendBossGuidance(player, npc, "Your familiar returns from temporal banishment.", bossStats);
                            }
                            
                            player.sendMessage("The temporal prison dissipates. You can move freely again!", true);
                            
                            timeTrap = false;
                            tick = 0;
                            stop();
                            return;
                    }
                    stage++;
                }
            }, 1, 1);
        }
    }

    /**
     * FIXED: Calculate balanced melee damage with proper NPC reference
     */
    private int calculateBalancedMeleeDamage(NPC npc, TimeBossStats bossStats, Entity target) {
        // FIXED: Pass the actual NPC instead of null
        int baseDamage = getRandomMaxHit(npc, bossStats.meleeMaxHit, NPCCombatDefinitions.MELEE, target);
        
        // Apply protection calculations if target is player
        if (target instanceof Player) {
            Player player = (Player) target;
            if (player.getPrayer().usingPrayer(0, 18) || player.getPrayer().usingPrayer(1, 8)) {
                baseDamage = (int)(baseDamage * 0.6); // 40% reduction with protect from melee
            }
        }
        
        return Math.max(1, baseDamage);
    }

    /**
     * FIXED: Calculate balanced ranged damage with proper NPC reference
     */
    private int calculateBalancedRangedDamage(NPC npc, TimeBossStats bossStats, Entity target) {
        // FIXED: Pass the actual NPC instead of null
        int baseDamage = getRandomMaxHit(npc, bossStats.rangedMaxHit, NPCCombatDefinitions.RANGE, target);
        
        // Apply protection calculations if target is player
        if (target instanceof Player) {
            Player player = (Player) target;
            if (player.getPrayer().usingPrayer(0, 16) || player.getPrayer().usingPrayer(1, 6)) {
                baseDamage = (int)(baseDamage * 0.6); // 40% reduction with protect from missiles
            }
        }
        
        return Math.max(1, baseDamage);
    }

    /**
     * FIXED: Calculate balanced dragon fire damage with proper NPC reference
     */
    private int calculateBalancedDragonFireDamage(NPC npc, TimeBossStats bossStats, Entity target) {
        // FIXED: Pass the actual NPC instead of null for proper bonus calculations
        int baseDamage = getRandomMaxHit(npc, bossStats.dragonFireMaxHit, NPCCombatDefinitions.MAGE, target);
        
        // Base damage calculation using tier-appropriate range
        int minDamage = (int)(baseDamage * 0.2);
        int maxDamage = baseDamage;
        
        return Utils.random(minDamage, maxDamage);
    }

    /**
     * Calculate time damage based on attack bonus and modifier
     */
    private int calculateTimeDamage(int baseMaxHit, int attackBonus, double modifier) {
        int damage = (int) (baseMaxHit * modifier);
        
        // Apply attack bonus scaling
        if (attackBonus > 0) {
            damage = (int) (damage * (1.0 + (attackBonus * 0.0008))); // 0.08% per bonus point
        }
        
        return Math.max(1, damage);
    }

    /**
     * Provide time manipulation education
     */
    private void provideTimeEducation(Player player, NPC npc, String attackType, TimeBossStats bossStats) {
        String username = player.getUsername();
        long currentTime = System.currentTimeMillis();
        
        // Check cooldown
        Long lastTip = playerLastTimeTip.get(username);
        if (lastTip != null && (currentTime - lastTip) < TIME_TIP_COOLDOWN) {
            return;
        }
        
        // Check tip stage
        Integer tipStage = playerTimeTipStage.get(username);
        if (tipStage == null) tipStage = 0;
        if (tipStage >= MAX_TIME_TIPS_PER_FIGHT) return;
        
        String tipMessage = getTimeTip(bossStats.tier, attackType, tipStage);
        if (tipMessage != null) {
            player.sendMessage("<col=87CEEB>Time flows strangely... " + tipMessage + "</col>", true);
            
            playerLastTimeTip.put(username, currentTime);
            playerTimeTipStage.put(username, tipStage + 1);
        }
    }

    /**
     * Get time manipulation tip based on tier, attack type, and stage
     */
    private String getTimeTip(int tier, String attackType, int stage) {
        if (attackType.equals("TIME_TRAP")) {
            if (tier <= 4) {
                return "Time trap incoming! You'll be frozen and your familiar banished briefly.";
            } else if (tier <= 7) {
                return "Elite time trap! Extended freeze and familiar banishment.";
            } else {
                return "Legendary temporal prison! Long freeze with complete familiar banishment.";
            }
        } else if (stage == 0) {
            if (attackType.equals("MELEE")) {
                return "Celestial claws strike with temporal energy. Keep distance when possible.";
            } else if (attackType.equals("RANGED")) {
                return "Celestial magic arrows phase through normal defenses.";
            } else if (attackType.equals("DRAGONFIRE")) {
                return "Dragon fire burns through time itself. Use all fire protection.";
            }
        } else if (stage == 1) {
            return "Time trap occurs every " + getTimeTrapIntervalText(tier) + " attacks. Count and prepare!";
        } else if (stage == 2) {
            return "Higher tier Celestial Dragons have stronger time manipulation.";
        } else if (stage == 3) {
            return "Summoners: Your familiar will be banished during time traps!";
        } else if (stage == 4) {
            return "Use Protect from Magic for dragon fire, Protect from Melee for claws.";
        } else if (stage == 5) {
            return "The dragon's power scales with its tier. Prepare accordingly!";
        }
        return null;
    }

    /**
     * Get time trap interval text based on tier
     */
    private String getTimeTrapIntervalText(int tier) {
        if (tier <= 3) return "6-7";
        else if (tier <= 6) return "5-6";
        else if (tier <= 8) return "4-5";
        else return "3-4";
    }

    /**
     * Get simple tier name for one-line announcements
     */
    private String getSimpleTierName(int tier) {
        switch (tier) {
            case 1: return "Tier 1";
            case 2: return "Tier 2"; 
            case 3: return "Tier 3";
            case 4: return "Tier 4";
            case 5: return "Tier 5";
            case 6: return "Tier 6";
            case 7: return "Tier 7 Elite";
            case 8: return "Tier 8 Legendary";
            case 9: return "Tier 9 Mythical";
            case 10: return "Tier 10 Divine";
            default: return "Unknown Tier";
        }
    }

    /**
     * Estimate time boss tier from combat stats
     */
    private int estimateTimeBossTierFromStats(NPCCombatDefinitions defs) {
        int hp = defs.getHitpoints();
        int maxHit = defs.getMaxHit();
        
        // Time manipulation bosses typically mid-high tier
        if (hp <= 3200 && maxHit <= 50) return 3;       // Celestial Drake
        if (hp <= 6000 && maxHit <= 80) return 4;       // Celestial Dragon
        if (hp <= 10500 && maxHit <= 125) return 5;     // Elder Celestial
        if (hp <= 17000 && maxHit <= 185) return 6;     // Celestial Master (typical)
        if (hp <= 25500 && maxHit <= 260) return 7;     // Elite Celestial Lord
        if (hp <= 36000 && maxHit <= 350) return 8;     // Legendary Time Dragon
        if (hp <= 50000 && maxHit <= 460) return 9;     // Mythical Chrono Dragon
        return 10; // Divine Temporal Emperor
    }

    /**
     * Estimate time attack bonuses for tier
     */
    private int[] estimateTimeAttackBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Time manipulation bosses have enhanced magic bonuses for dragon fire and time magic
        return new int[]{
            baseStat,                    // stab
            (int)(baseStat * 1.1),      // slash (enhanced for melee)
            baseStat,                    // crush
            (int)(baseStat * 1.3),      // magic (heavily enhanced for dragon fire)
            (int)(baseStat * 1.2)       // ranged (enhanced)
        };
    }

    /**
     * Estimate time defense bonuses for tier
     */
    private int[] estimateTimeDefenseBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Time manipulation bosses have strong overall defenses
        return new int[]{baseStat, baseStat, baseStat, baseStat, baseStat};
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
        return new Object[] { 19109 }; // Celestial Dragon NPC ID
    }

    /**
     * Time Boss stats container class
     */
    private static class TimeBossStats {
        public int tier;
        public int maxHit;
        public int meleeMaxHit;
        public int rangedMaxHit;
        public int dragonFireMaxHit;
        public int hitpoints;
        public int[] attackBonuses;
        public int[] defenseBonuses;
        public int maxBonus;
        public int timeTrapInterval;
        public int timeTrapDuration;
        public int familiarBanishDuration;
        public boolean isBalanced;
        public long timestamp;
    }
}