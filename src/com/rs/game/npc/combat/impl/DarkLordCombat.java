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
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;
import com.rs.utils.Logger;

/**
 * Enhanced Dark Lord Combat - ENVIRONMENTAL MECHANICS BOSS WITH TIER ANNOUNCEMENT
 * 
 * @author Enhanced by Balance System v2.1 (Original by paolo)
 * @date May 31, 2025
 * @version 2.1 - Integrated with ItemBalancer & BossBalancer Systems
 * Features: Tier Announcements, Environmental Education, Balanced Scaling, Water/Fire Mechanics
 */
public class DarkLordCombat extends CombatScript {

    // Enhanced education system for environmental mechanics
    private static final Map<String, Long> playerLastEnvironmentalTip = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Integer> playerEnvironmentalTipStage = new ConcurrentHashMap<String, Integer>();
    private static final Map<Integer, Long> bossLastTierAnnouncement = new ConcurrentHashMap<Integer, Long>();
    private static final long ENVIRONMENTAL_TIP_COOLDOWN = 20000; // 20 seconds between environmental tips
    private static final long TIER_ANNOUNCEMENT_COOLDOWN = 300000; // 5 minutes between tier announcements
    private static final int MAX_ENVIRONMENTAL_TIPS_PER_FIGHT = 5; // More tips for complex environmental boss

    // Boss stats cache for environmental boss
    private static final Map<Integer, EnvironmentalBossStats> environmentalBossStatsCache = new ConcurrentHashMap<Integer, EnvironmentalBossStats>();
    
    @Override
    public Object[] getKeys() {
        return new Object[] { 20374, 19553 }; // Dark Lord NPC IDs
    }

    /**
     * Check if player is at water (preserved from original)
     */
    public boolean isAtWater(Player player) {
        return (player.getX() >= 3801 && player.getX() <= 3818
                && player.getY() <= 4698 && player.getY() >= 4696);
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        NPCCombatDefinitions def = npc.getCombatDefinitions();
        final Player player = (Player) target;
        
        // Get balanced environmental boss stats
        EnvironmentalBossStats bossStats = getBalancedEnvironmentalBossStats(npc);
        
        // Announce tier at the beginning (visible to all players)
        announceBossTier(npc, bossStats);
        
        // Create fire objects for flame wall (preserved from original)
        final WorldObject fire = new WorldObject(95033, 10, 0, player.getX(), player.getY() + 1, 0);
        final WorldObject fire2 = new WorldObject(95033, 10, 0, player.getX() + 1, player.getY(), 0);
        final WorldObject fire3 = new WorldObject(95033, 10, 0, player.getTile().getX() - 1, player.getY(), 0);
        final WorldObject fire4 = new WorldObject(95033, 10, 0, player.getTile().getX(), player.getY() - 1, 0);

        // Determine attack with tier-aware probabilities
        int attackRoll = determineAttackRoll(bossStats);
        
        if (attackRoll <= 3) {
            return executeMeleeAttack(npc, player, bossStats);
        } else if (attackRoll == 4) {
            return executeProjectileAttack(npc, target, player, bossStats);
        } else if (attackRoll == 5) {
            return executeHeatWaveAttack(npc, player, bossStats);
        } else if (attackRoll == 6) {
            return executeWaterHealAttack(npc, player, bossStats);
        } else if (attackRoll == 7) {
            return executeFlameWallAttack(npc, target, player, fire, fire2, fire3, fire4, bossStats);
        } else {
            return executeWeakMeleeAttack(npc, player, bossStats);
        }
    }

    /**
     * Announce boss tier to all players in the area (Simple and Elegant)
     */
    private void announceBossTier(NPC npc, EnvironmentalBossStats bossStats) {
        int npcId = npc.getId();
        long currentTime = System.currentTimeMillis();
        
        // Check if we recently announced for this boss instance
        Long lastAnnouncement = bossLastTierAnnouncement.get(npcId + npc.hashCode());
        if (lastAnnouncement != null && (currentTime - lastAnnouncement) < TIER_ANNOUNCEMENT_COOLDOWN) {
            return; // Too soon for another announcement
        }
        
        // Find all nearby players for the announcement
        if (npc.getPossibleTargets() != null) {
            for (Entity entity : npc.getPossibleTargets()) {
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    
                    // Simple and elegant tier announcement
                    String tierName = getBossTierName(bossStats.tier);
                    String balanceStatus = bossStats.isBalanced ? "PERFECTLY BALANCED" : "ESTIMATED TIER";
                    String difficultyWarning = getDifficultyWarning(bossStats.tier);
                    
                    player.getPackets().sendGameMessage("", true); // Spacer
                    player.getPackets().sendGameMessage(
                        "<col=8B0000>========================================================================</col>", true);
                    player.getPackets().sendGameMessage(
                        "<col=8B0000>|</col>                        <col=FFFF00>DARK LORD EMERGES FROM THE SHADOWS!</col>                       <col=8B0000>|</col>", true);
                    player.getPackets().sendGameMessage(
                        "<col=8B0000>|</col>                            <col=00FF00>" + tierName + "</col>                            <col=8B0000>|</col>", true);
                    player.getPackets().sendGameMessage(
                        "<col=8B0000>|</col>                          <col=CYAN>" + balanceStatus + "</col>                          <col=8B0000>|</col>", true);
                    player.getPackets().sendGameMessage(
                        "<col=8B0000>|</col>                        <col=FF6B35>" + difficultyWarning + "</col>                        <col=8B0000>|</col>", true);
                    player.getPackets().sendGameMessage(
                        "<col=8B0000>========================================================================</col>", true);
                    player.getPackets().sendGameMessage("", true); // Spacer
                    
                    // Additional tier-specific warning
                    if (bossStats.tier >= 6) {
                        player.getPackets().sendGameMessage(
                            "<col=8B0000>[ELEMENTAL MASTER]</col> <col=FFFF00>This lord controls fire and water! Master environmental positioning to survive!</col>", true);
                    }
                }
            }
        }
        
        // Update announcement tracking
        bossLastTierAnnouncement.put(npcId + npc.hashCode(), currentTime);
    }

    /**
     * Get balanced environmental boss stats with caching
     */
    private EnvironmentalBossStats getBalancedEnvironmentalBossStats(NPC npc) {
        int npcId = npc.getId();
        
        // Check cache first
        EnvironmentalBossStats cached = environmentalBossStatsCache.get(npcId);
        if (cached != null && System.currentTimeMillis() - cached.timestamp < 300000) { // 5 min cache
            return cached;
        }

        EnvironmentalBossStats stats = new EnvironmentalBossStats();
        
        try {
            // Estimate tier from combat definitions
            stats.tier = estimateEnvironmentalBossTierFromStats(npc.getCombatDefinitions());
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
                // Fallback: estimate environmental-appropriate bonuses
                stats.attackBonuses = estimateEnvironmentalAttackBonuses(stats.tier);
                stats.defenseBonuses = estimateEnvironmentalDefenseBonuses(stats.tier);
                stats.maxBonus = getMaxBonus(stats.attackBonuses);
                stats.isBalanced = false;
            }
            
            // Calculate environmental-specific stats
            stats.meleeMaxHit = calculateEnvironmentalDamage(stats.maxHit, stats.attackBonuses[1], 1.0); // Slash-based
            stats.rangedMaxHit = calculateEnvironmentalDamage(stats.maxHit, stats.attackBonuses[4], 1.1); // Ranged enhanced
            stats.heatWaveMaxHit = calculateEnvironmentalDamage(stats.maxHit, stats.attackBonuses[3], 0.3); // Heat wave (lighter damage over time)
            stats.flameWallMaxHit = calculateEnvironmentalDamage(stats.maxHit, stats.attackBonuses[3], 2.0); // Flame wall (massive damage)
            stats.waterHealAmount = calculateEnvironmentalDamage(stats.maxHit, stats.attackBonuses[3], 0.8); // Heal amount
            
            // Tier-based environmental mechanics
            stats.specialAttackChance = Math.min(30, 15 + (stats.tier * 2)); // 17% at tier 1, 30% at tier 10
            stats.flameWallDuration = Math.max(4000, 5000 + (stats.tier * 200)); // 5.2-7 seconds based on tier
            stats.heatWaveIntensity = Math.max(8, 6 + stats.tier); // 7-16 waves based on tier
            
            stats.timestamp = System.currentTimeMillis();
            environmentalBossStatsCache.put(npcId, stats);
            
        } catch (Exception e) {
            Logger.handle(e);
            // Safe fallback values for environmental boss
            stats.tier = 7; // Dark Lord is typically high-tier
            stats.maxHit = 400;
            stats.meleeMaxHit = 400;
            stats.rangedMaxHit = 440; // 10% enhanced
            stats.heatWaveMaxHit = 120; // 30% of base for DoT
            stats.flameWallMaxHit = 800; // 200% of base for environmental
            stats.waterHealAmount = 320; // 80% of base for healing
            stats.hitpoints = 20000;
            stats.attackBonuses = new int[]{650, 800, 650, 750, 700}; // Melee/Magic focused
            stats.defenseBonuses = new int[]{700, 700, 700, 750, 650};
            stats.maxBonus = 800;
            stats.specialAttackChance = 29;
            stats.flameWallDuration = 6400; // 6.4 seconds
            stats.heatWaveIntensity = 13;
            stats.isBalanced = false;
        }
        
        return stats;
    }

    /**
     * Determine attack roll with tier-aware probabilities
     */
    private int determineAttackRoll(EnvironmentalBossStats bossStats) {
        // Higher tier bosses use more special attacks
        if (Utils.random(100) < bossStats.specialAttackChance) {
            return Utils.random(4) + 4; // Special attacks (4-7)
        } else {
            return Utils.random(4); // Basic attacks (0-3)
        }
    }

    /**
     * Execute melee attack with balanced damage
     */
    private int executeMeleeAttack(NPC npc, Player player, EnvironmentalBossStats bossStats) {
        npc.setNextAnimation(new Animation(24224));
        
        // Calculate balanced melee damage (includes original poison damage concept)
        int meleeDamage = calculateBalancedMeleeDamage(bossStats, player);
        
        delayHit(npc, 0, player, new Hit(npc, meleeDamage, HitLook.REGULAR_DAMAGE));
        
        // Provide melee education
        if (Utils.random(10) == 0) { // 10% chance
            provideEnvironmentalEducation(player, npc, "MELEE", bossStats);
        }
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Execute weak melee attack with balanced damage
     */
    private int executeWeakMeleeAttack(NPC npc, Player player, EnvironmentalBossStats bossStats) {
        npc.setNextAnimation(new Animation(24224));
        
        // Calculate weak melee damage (much lower)
        int weakDamage = calculateBalancedWeakDamage(bossStats, player);
        
        delayHit(npc, 0, player, new Hit(npc, weakDamage, HitLook.REGULAR_DAMAGE));
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Execute projectile attack with balanced damage
     */
    private int executeProjectileAttack(NPC npc, Entity target, Player player, EnvironmentalBossStats bossStats) {
        npc.setNextAnimation(new Animation(24237));
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                World.sendProjectile(npc, target, 5102, 80, 30, 40, 20, 5, 0);
                
                // Calculate balanced ranged damage
                int rangedDamage = calculateBalancedRangedDamage(bossStats, player);
                
                delayHit(npc, 1, target, getRangeHit(npc, rangedDamage));
            }
        }, 2);
        
        // Provide projectile education
        if (Utils.random(10) == 0) { // 10% chance
            provideEnvironmentalEducation(player, npc, "PROJECTILE", bossStats);
        }
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Execute heat wave attack - player must find water
     */
    private int executeHeatWaveAttack(NPC npc, Player player, EnvironmentalBossStats bossStats) {
        npc.setNextAnimation(new Animation(24232));
        npc.setNextForceTalk(new ForceTalk("Find some water my friend!"));
        player.sm("<col=FF000>Quick find some water to absorb the heat!");
        
        // Provide heat wave education
        provideEnvironmentalEducation(player, npc, "HEAT_WAVE", bossStats);
        
        WorldTasksManager.schedule(new WorldTask() {
            int count = 0;
            
            @Override
            public void run() {
                if (!isAtWater(player)) {
                    player.setNextGraphics(new Graphics(4148));
                    
                    // Calculate balanced heat wave damage
                    int heatDamage = calculateBalancedHeatWaveDamage(bossStats, player);
                    delayHit(npc, 0, player, new Hit(npc, heatDamage, HitLook.MAGIC_DAMAGE));
                }
                
                if (isAtWater(player)) {
                    player.setNextGraphics(new Graphics(4153));
                    player.sm("Standing in the water prevented you from taking damage.");
                    stop();
                    return;
                }
                
                if (count++ >= bossStats.heatWaveIntensity) {
                    if (!isAtWater(player)) {
                        player.setNextGraphics(new Graphics(4148));
                        
                        // Final big heat wave damage
                        int finalHeatDamage = calculateBalancedHeatWaveDamage(bossStats, player) * 3;
                        delayHit(npc, 0, player, new Hit(npc, finalHeatDamage, HitLook.MAGIC_DAMAGE));
                    }
                    stop();
                    return;
                }
            }
        }, 0, 0);
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Execute water heal attack - standing in water heals the boss
     */
    private int executeWaterHealAttack(NPC npc, Player player, EnvironmentalBossStats bossStats) {
        npc.setNextAnimation(new Animation(24232));
        npc.setNextForceTalk(new ForceTalk("Don't stand in my water!"));
        player.sm("<col=FF000>Standing in the water will heal the dark lord incredible.");
        
        // Provide water heal education
        provideEnvironmentalEducation(player, npc, "WATER_HEAL", bossStats);
        
        WorldTasksManager.schedule(new WorldTask() {
            int count = 0;
            
            @Override
            public void run() {
                if (isAtWater(player)) {
                    // Calculate balanced heal amount
                    int healAmount = calculateBalancedWaterHeal(bossStats);
                    npc.applyHit(new Hit(player, healAmount, HitLook.HEALED_DAMAGE));
                }
                
                if (!isAtWater(player)) {
                    stop();
                    return;
                }
                
                if (count++ >= 10) {
                    stop();
                    return;
                }
            }
        }, 0, 0);
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Execute flame wall attack - creates fire trap around player
     */
    private int executeFlameWallAttack(NPC npc, Entity target, Player player, 
                                     WorldObject fire, WorldObject fire2, WorldObject fire3, WorldObject fire4, 
                                     EnvironmentalBossStats bossStats) {
        npc.setNextForceTalk(new ForceTalk("Try to escape my flames!"));
        
        // Spawn fire objects with tier-appropriate duration
        World.spawnTemporaryObject(fire, bossStats.flameWallDuration, true);
        World.spawnTemporaryObject(fire2, bossStats.flameWallDuration, true);
        World.spawnTemporaryObject(fire3, bossStats.flameWallDuration, true);
        World.spawnTemporaryObject(fire4, bossStats.flameWallDuration, true);
        
        final WorldTile center = new WorldTile(target);
        
        // Provide flame wall education
        provideEnvironmentalEducation(player, npc, "FLAME_WALL", bossStats);
        
        WorldTasksManager.schedule(new WorldTask() {
            int count = 0;
            
            @Override
            public void run() {
                Player player = (Player) target;
                if (count++ >= 10) {
                    if (player.withinDistance(center, 1)) {
                        // Calculate balanced flame wall damage (massive)
                        int flameWallDamage = calculateBalancedFlameWallDamage(bossStats, player);
                        delayHit(npc, 0, player, new Hit(npc, flameWallDamage, HitLook.REGULAR_DAMAGE));
                    }
                    stop();
                    return;
                }
            }
        }, 0, 0);
        
        return npc.getCombatDefinitions().getAttackDelay();
    }

    /**
     * Calculate balanced melee damage (includes original poison concept)
     */
    private int calculateBalancedMeleeDamage(EnvironmentalBossStats bossStats, Player player) {
        int baseDamage = Utils.random((int)(bossStats.meleeMaxHit * 0.5), bossStats.meleeMaxHit);
        
        // Add poison damage equivalent (original had getPAdamage())
        int poisonBonus = player.getPoison().isPoisoned() ? Utils.random(20, 50) : 0;
        baseDamage += poisonBonus;
        
        // Apply protection calculations
        if (player.getPrayer().usingPrayer(0, 18) || player.getPrayer().usingPrayer(1, 8)) {
            baseDamage = (int)(baseDamage * 0.6); // 40% reduction with protect from melee
        }
        
        return Math.max(1, baseDamage);
    }

    /**
     * Calculate balanced weak damage
     */
    private int calculateBalancedWeakDamage(EnvironmentalBossStats bossStats, Player player) {
        int baseDamage = Utils.random(1, (int)(bossStats.meleeMaxHit * 0.15)); // Much weaker
        
        // Add small poison bonus
        int poisonBonus = player.getPoison().isPoisoned() ? Utils.random(5, 15) : 0;
        baseDamage += poisonBonus;
        
        return Math.max(1, baseDamage);
    }

    /**
     * Calculate balanced ranged damage
     */
    private int calculateBalancedRangedDamage(EnvironmentalBossStats bossStats, Player player) {
        int baseDamage = Utils.random((int)(bossStats.rangedMaxHit * 0.3), bossStats.rangedMaxHit);
        
        // Add poison damage equivalent
        int poisonBonus = player.getPoison().isPoisoned() ? Utils.random(20, 50) : 0;
        baseDamage += poisonBonus;
        
        // Apply protection calculations
        if (player.getPrayer().usingPrayer(0, 16) || player.getPrayer().usingPrayer(1, 6)) {
            baseDamage = (int)(baseDamage * 0.6); // 40% reduction with protect from missiles
        }
        
        return Math.max(1, baseDamage);
    }

    /**
     * Calculate balanced heat wave damage
     */
    private int calculateBalancedHeatWaveDamage(EnvironmentalBossStats bossStats, Player player) {
        int baseDamage = Utils.random((int)(bossStats.heatWaveMaxHit * 0.3), bossStats.heatWaveMaxHit);
        
        // Apply protection calculations
        if (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)) {
            baseDamage = (int)(baseDamage * 0.6); // 40% reduction with protect from magic
        }
        
        return Math.max(1, baseDamage);
    }

    /**
     * Calculate balanced flame wall damage
     */
    private int calculateBalancedFlameWallDamage(EnvironmentalBossStats bossStats, Player player) {
        int baseDamage = Utils.random((int)(bossStats.flameWallMaxHit * 0.6), bossStats.flameWallMaxHit);
        
        // Flame wall is environmental damage - harder to protect against
        if (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)) {
            baseDamage = (int)(baseDamage * 0.8); // Only 20% reduction (environmental fire)
        }
        
        return Math.max(1, baseDamage);
    }

    /**
     * Calculate balanced water heal amount
     */
    private int calculateBalancedWaterHeal(EnvironmentalBossStats bossStats) {
        return Utils.random((int)(bossStats.waterHealAmount * 0.3), bossStats.waterHealAmount);
    }

    /**
     * Calculate environmental damage based on attack bonus and modifier
     */
    private int calculateEnvironmentalDamage(int baseMaxHit, int attackBonus, double modifier) {
        int damage = (int) (baseMaxHit * modifier);
        
        // Apply attack bonus scaling
        if (attackBonus > 0) {
            damage = (int) (damage * (1.0 + (attackBonus * 0.0008))); // 0.08% per bonus point
        }
        
        return Math.max(1, damage);
    }

    /**
     * Provide environmental education
     */
    private void provideEnvironmentalEducation(Player player, NPC npc, String mechanicType, EnvironmentalBossStats bossStats) {
        String username = player.getUsername();
        long currentTime = System.currentTimeMillis();
        
        // Check cooldown
        Long lastTip = playerLastEnvironmentalTip.get(username);
        if (lastTip != null && (currentTime - lastTip) < ENVIRONMENTAL_TIP_COOLDOWN) {
            return;
        }
        
        // Check tip stage
        Integer tipStage = playerEnvironmentalTipStage.get(username);
        if (tipStage == null) tipStage = 0;
        if (tipStage >= MAX_ENVIRONMENTAL_TIPS_PER_FIGHT) return;
        
        String tipMessage = getEnvironmentalTip(bossStats.tier, mechanicType, tipStage);
        if (tipMessage != null) {
            player.getPackets().sendGameMessage(
                "<col=8B0000>[Environmental Guide]</col> " + tipMessage, true);
            
            playerLastEnvironmentalTip.put(username, currentTime);
            playerEnvironmentalTipStage.put(username, tipStage + 1);
        }
    }

    /**
     * Get environmental tip based on tier, mechanic type, and stage
     */
    private String getEnvironmentalTip(int tier, String mechanicType, int stage) {
        if (mechanicType.equals("HEAT_WAVE")) {
            if (tier <= 4) {
                return "Heat wave attack! Quickly find water to avoid continuous damage. Higher tiers have more waves!";
            } else {
                return "Elite heat wave! Find water immediately - this will be longer and more intense!";
            }
        } else if (mechanicType.equals("WATER_HEAL")) {
            if (tier <= 4) {
                return "Don't stand in water during this attack! You'll heal the Dark Lord significantly.";
            } else {
                return "Elite Dark Lord heals more from water! Avoid water areas during this phase at all costs.";
            }
        } else if (mechanicType.equals("FLAME_WALL")) {
            if (tier <= 4) {
                return "Flame wall trap! Move away from the center quickly to avoid massive fire damage.";
            } else {
                return "Elite flame wall lasts longer! Get to safety immediately - this fire burns much hotter.";
            }
        } else if (stage == 0) {
            if (mechanicType.equals("MELEE")) {
                return "Dark Lord's melee attacks are powerful. Use Protect from Melee when he's close.";
            } else if (mechanicType.equals("PROJECTILE")) {
                return "Dark Lord's projectile attack has a delay. Use Protect from Missiles and watch for the animation.";
            }
        } else if (stage == 1) {
            return "Dark Lord uses environmental attacks! Master water positioning - sometimes you need it, sometimes you don't.";
        } else if (stage == 2) {
            return "Watch Dark Lord's force talk for clues! His words tell you what environmental strategy to use.";
        } else if (stage == 3) {
            return "Higher tier Dark Lords have more intense environmental effects. Longer durations and higher damage!";
        } else if (stage == 4) {
            return "Environmental positioning is key! Water saves you from heat but heals him during water attacks.";
        }
        return null;
    }

    /**
     * Get boss tier name for announcements
     */
    private String getBossTierName(int tier) {
        switch (tier) {
            case 1: return "TIER 1 - SHADOW APPRENTICE";
            case 2: return "TIER 2 - DARK ACOLYTE";
            case 3: return "TIER 3 - SHADOW MAGE";
            case 4: return "TIER 4 - DARK SORCERER";
            case 5: return "TIER 5 - SHADOW LORD";
            case 6: return "TIER 6 - DARK MASTER";
            case 7: return "TIER 7 - ELITE DARK LORD";
            case 8: return "TIER 8 - LEGENDARY SHADOW EMPEROR";
            case 9: return "TIER 9 - MYTHICAL DARK OVERLORD";
            case 10: return "TIER 10 - DIVINE SHADOW GOD";
            default: return "UNKNOWN TIER DARK ENTITY";
        }
    }

    /**
     * Get difficulty warning for tier announcement
     */
    private String getDifficultyWarning(int tier) {
        if (tier <= 3) {
            return "MODERATE THREAT - BASIC ENVIRONMENTAL CONTROL";
        } else if (tier <= 5) {
            return "HIGH THREAT - ENHANCED ELEMENTAL MASTERY";
        } else if (tier <= 7) {
            return "SEVERE THREAT - MASTER ENVIRONMENTAL MANIPULATION";
        } else {
            return "EXTREME THREAT - ABSOLUTE ELEMENTAL DOMINION";
        }
    }

    /**
     * Estimate environmental boss tier from combat stats
     */
    private int estimateEnvironmentalBossTierFromStats(NPCCombatDefinitions defs) {
        int hp = defs.getHitpoints();
        int maxHit = defs.getMaxHit();
        
        // Environmental bosses typically mid-high tier
        if (hp <= 6000 && maxHit <= 80) return 4;       // Dark Sorcerer
        if (hp <= 10500 && maxHit <= 125) return 5;     // Shadow Lord
        if (hp <= 17000 && maxHit <= 185) return 6;     // Dark Master
        if (hp <= 25500 && maxHit <= 260) return 7;     // Elite Dark Lord (typical)
        if (hp <= 36000 && maxHit <= 350) return 8;     // Legendary Shadow Emperor
        if (hp <= 50000 && maxHit <= 460) return 9;     // Mythical Dark Overlord
        return 10; // Divine Shadow God
    }

    /**
     * Estimate environmental attack bonuses for tier
     */
    private int[] estimateEnvironmentalAttackBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Environmental bosses have enhanced melee/magic bonuses for fire/water control
        return new int[]{
            baseStat,                    // stab
            (int)(baseStat * 1.2),      // slash (enhanced for melee)
            baseStat,                    // crush
            (int)(baseStat * 1.3),      // magic (heavily enhanced for environmental)
            (int)(baseStat * 1.1)       // ranged (enhanced)
        };
    }

    /**
     * Estimate environmental defense bonuses for tier
     */
    private int[] estimateEnvironmentalDefenseBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Environmental bosses have strong overall defenses
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

    /**
     * Environmental Boss stats container class
     */
    private static class EnvironmentalBossStats {
        public int tier;
        public int maxHit;
        public int meleeMaxHit;
        public int rangedMaxHit;
        public int heatWaveMaxHit;
        public int flameWallMaxHit;
        public int waterHealAmount;
        public int hitpoints;
        public int[] attackBonuses;
        public int[] defenseBonuses;
        public int maxBonus;
        public int specialAttackChance;
        public int flameWallDuration;
        public int heatWaveIntensity;
        public boolean isBalanced;
        public long timestamp;
    }
}