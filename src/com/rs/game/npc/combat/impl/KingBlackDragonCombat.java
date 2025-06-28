package com.rs.game.npc.combat.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.Combat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;
import com.rs.utils.Logger;

/**
 * Enhanced King Black Dragon Combat - PERFECTLY BALANCED INTEGRATION
 * 
 * @author Enhanced by Balance System v2.1
 * @date May 31, 2025
 * @version 2.1 - Integrated with ItemBalancer & BossBalancer Systems
 */
public class KingBlackDragonCombat extends CombatScript {

    // Education system - prevents spam while providing helpful guidance
    private static final Map<String, Long> playerLastTip = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Integer> playerTipStage = new ConcurrentHashMap<String, Integer>();
    private static final long TIP_COOLDOWN = 30000; // 30 seconds between tips
    private static final int MAX_TIPS_PER_FIGHT = 3; // Maximum tips per combat session

    // Boss tier detection cache for performance
    private static final Map<Integer, BossStats> bossStatsCache = new ConcurrentHashMap<Integer, BossStats>();
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        
        // Get balanced boss stats (integrates with BossBalancer system)
        BossStats bossStats = getBalancedBossStats(npc);
        
        // Determine attack style with tier-aware probabilities
        int attackStyle = determineAttackStyle(bossStats.tier);
        int size = npc.getSize();

        // Provide educational guidance (smart, contextual, non-spammy)
        if (target instanceof Player) {
            provideEducationalGuidance((Player) target, npc, attackStyle, bossStats);
        }

        // Execute attack based on style with balanced damage
        if (attackStyle == 0) {
            return executeMeleeAttack(npc, target, defs, size, bossStats);
        } else if (attackStyle == 1 || attackStyle == 2) {
            return executeFireBreathAttack(npc, target, defs, bossStats);
        } else if (attackStyle == 3) {
            return executePoisonBreathAttack(npc, target, defs, bossStats);
        } else if (attackStyle == 4) {
            return executeFreezeBreathAttack(npc, target, defs, bossStats);
        } else {
            return executeShockBreathAttack(npc, target, defs, bossStats);
        }
    }

    /**
     * Get balanced boss stats from BossBalancer system with caching
     */
    private BossStats getBalancedBossStats(NPC npc) {
        int npcId = npc.getId();
        
        // Check cache first for performance
        BossStats cached = bossStatsCache.get(npcId);
        if (cached != null && System.currentTimeMillis() - cached.timestamp < 300000) { // 5 min cache
            return cached;
        }

        BossStats stats = new BossStats();
        
        try {
            // Try to get balanced stats from BossBalancer system
            stats.tier = estimateBossTierFromStats(npc.getCombatDefinitions());
            stats.maxHit = npc.getCombatDefinitions().getMaxHit();
            stats.hitpoints = npc.getCombatDefinitions().getHitpoints();
            
            // Get NPC bonuses from the balanced system
            int[] bonuses = NPCBonuses.getBonuses(npcId);
            if (bonuses != null && bonuses.length >= 10) {
                stats.attackBonuses = new int[]{bonuses[0], bonuses[1], bonuses[2], bonuses[3], bonuses[4]};
                stats.defenseBonuses = new int[]{bonuses[5], bonuses[6], bonuses[7], bonuses[8], bonuses[9]};
                stats.maxBonus = getMaxBonus(bonuses);
            } else {
                // Fallback to estimated bonuses if not balanced yet
                stats.attackBonuses = estimateAttackBonuses(stats.tier);
                stats.defenseBonuses = estimateDefenseBonuses(stats.tier);
                stats.maxBonus = getMaxBonus(stats.attackBonuses);
            }
            
            stats.timestamp = System.currentTimeMillis();
            
            // Cache the result
            bossStatsCache.put(npcId, stats);
            
        } catch (Exception e) {
            Logger.handle(e);
            // Fallback to safe default values
            stats.tier = 5;
            stats.maxHit = 250;
            stats.hitpoints = 6000;
            stats.attackBonuses = new int[]{400, 400, 400, 400, 400};
            stats.defenseBonuses = new int[]{400, 400, 400, 400, 400};
            stats.maxBonus = 400;
        }
        
        return stats;
    }

    /**
     * Estimate boss tier from combat stats (integrates with BossBalancer tier system)
     */
    private int estimateBossTierFromStats(NPCCombatDefinitions defs) {
        int hp = defs.getHitpoints();
        int maxHit = defs.getMaxHit();
        
        // Use BossBalancer tier ranges for perfect integration
        if (hp <= 600 && maxHit <= 15) return 1;       // Beginner
        if (hp <= 1500 && maxHit <= 30) return 2;      // Novice  
        if (hp <= 3200 && maxHit <= 50) return 3;      // Intermediate
        if (hp <= 6000 && maxHit <= 80) return 4;      // Advanced
        if (hp <= 10500 && maxHit <= 125) return 5;    // Expert
        if (hp <= 17000 && maxHit <= 185) return 6;    // Master
        if (hp <= 25500 && maxHit <= 260) return 7;    // Elite
        if (hp <= 36000 && maxHit <= 350) return 8;    // Legendary
        if (hp <= 50000 && maxHit <= 460) return 9;    // Mythical
        return 10; // Divine
    }

    /**
     * Determine attack style with tier-aware probabilities
     */
    private int determineAttackStyle(int tier) {
        // Higher tier bosses use more special attacks
        int specialChance = Math.min(80, 20 + (tier * 6)); // 26% at tier 1, 80% at tier 10
        
        if (Utils.getRandom(100) < specialChance) {
            return Utils.getRandom(4) + 1; // Special attacks (1-4)
        } else {
            return 0; // Melee attack
        }
    }

    /**
     * Execute melee attack with balanced damage
     */
    private int executeMeleeAttack(NPC npc, Entity target, NPCCombatDefinitions defs, int size, BossStats bossStats) {
        int distanceX = target.getX() - npc.getX();
        int distanceY = target.getY() - npc.getY();
        
        if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
            // Too far for melee, use ranged attack instead
            return executeFireBreathAttack(npc, target, defs, bossStats);
        } else {
            // Calculate balanced melee damage
            int balancedMaxHit = calculateBalancedDamage(bossStats.maxHit, bossStats.attackBonuses[0], target);
            
            delayHit(npc, 0, target, 
                getMeleeHit(npc, getRandomMaxHit(npc, balancedMaxHit, NPCCombatDefinitions.MELEE, target)));
            npc.setNextAnimation(new Animation(defs.getAttackEmote()));
            return defs.getAttackDelay();
        }
    }

    /**
     * Execute fire breath attack with balanced damage
     */
    private int executeFireBreathAttack(NPC npc, Entity target, NPCCombatDefinitions defs, BossStats bossStats) {
        // Calculate balanced fire damage (uses magic attack bonus)
        int baseDamage = calculateBalancedDamage(bossStats.maxHit, bossStats.attackBonuses[3], target);
        int damage = Utils.getRandom(baseDamage);
        
        final Player player = target instanceof Player ? (Player) target : null;
        npc.setNextAnimation(new Animation(17784));
        npc.setNextGraphics(new Graphics(3441, 0, 100));
        World.sendProjectile(npc, target, 3442, 60, 16, 65, 47, 16, 0);
        
        // Check protections
        if (Combat.hasAntiDragProtection(target) || 
            (player != null && (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)))) {
            damage = 0;
        }
        
        if (player != null && player.getFireImmune() > Utils.currentTimeMillis()) {
            if (damage != 0) {
                damage = Utils.getRandom(Math.max(1, baseDamage / 4)); // Reduced damage with fire immunity
            }
        } else if (damage == 0) {
            damage = Utils.getRandom(Math.max(1, baseDamage / 4));
        } else if (player != null) {
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    player.sendMessage("You are hit by the dragon's fiery breath!", true);
                    player.setNextGraphics(new Graphics(3443, 50, 0));
                }
            }, 0);
        }
        
        delayHit(npc, 2, target, getRegularHit(npc, damage));
        return defs.getAttackDelay();
    }

    /**
     * Execute poison breath attack with balanced damage
     */
    private int executePoisonBreathAttack(NPC npc, Entity target, NPCCombatDefinitions defs, BossStats bossStats) {
        int damage;
        final Player player = target instanceof Player ? (Player) target : null;
        
        npc.setNextAnimation(new Animation(17783));
        npc.setNextGraphics(new Graphics(3435, 0, 100));
        World.sendProjectile(npc, target, 3436, 60, 16, 65, 35, 16, 0);
        
        // Calculate balanced poison damage
        int baseDamage = calculateBalancedDamage(bossStats.maxHit, bossStats.attackBonuses[3], target);
        
        if (Combat.hasAntiDragProtection(target)) {
            damage = getRandomMaxHit(npc, Math.max(1, baseDamage / 4), NPCCombatDefinitions.MAGE, target);
            if (player != null) {
                player.getPackets().sendGameMessage(
                    "Your shield absorbs most of the dragon's poisonous breath!", true);
            }
        } else if (player != null && 
                  (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7))) {
            damage = getRandomMaxHit(npc, Math.max(1, baseDamage / 4), NPCCombatDefinitions.MAGE, target);
            if (player != null) {
                player.getPackets().sendGameMessage(
                    "Your prayer absorbs most of the dragon's poisonous breath!", true);
            }
        } else {
            damage = Utils.getRandom(baseDamage);
            if (player != null) {
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        player.getPackets().sendGameMessage(
                            "You are hit by the dragon's poisonous breath!", true);
                        player.setNextGraphics(new Graphics(3437, 50, 0));
                    }
                }, 0);
            }
        }
        
        // Poison chance scales with tier
        int poisonChance = Math.min(70, 30 + (bossStats.tier * 4)); // 34% at tier 1, 70% at tier 10
        if (Utils.getRandom(100) < poisonChance) {
            int poisonDamage = Math.max(20, bossStats.tier * 8); // Tier-appropriate poison
            target.getPoison().makePoisoned(poisonDamage);
        }
        
        delayHit(npc, 2, target, getRegularHit(npc, damage));
        return defs.getAttackDelay();
    }

    /**
     * Execute freeze breath attack with balanced damage
     */
    private int executeFreezeBreathAttack(NPC npc, Entity target, NPCCombatDefinitions defs, BossStats bossStats) {
        int damage;
        final Player player = target instanceof Player ? (Player) target : null;
        
        npc.setNextAnimation(new Animation(17784));
        npc.setNextGraphics(new Graphics(3438, 0, 100));
        World.sendProjectile(npc, target, 3439, 60, 16, 100, 45, 16, 0);
        
        // Calculate balanced freeze damage
        int baseDamage = calculateBalancedDamage(bossStats.maxHit, bossStats.attackBonuses[3], target);
        
        if (Combat.hasAntiDragProtection(target)) {
            damage = getRandomMaxHit(npc, Math.max(1, baseDamage / 4), NPCCombatDefinitions.MAGE, target);
            if (player != null) {
                player.getPackets().sendGameMessage(
                    "Your shield absorbs most of the dragon's freezing breath!", true);
            }
        } else if (player != null && 
                  (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7))) {
            damage = getRandomMaxHit(npc, Math.max(1, baseDamage / 4), NPCCombatDefinitions.MAGE, target);
            if (player != null) {
                player.getPackets().sendGameMessage(
                    "Your prayer absorbs most of the dragon's freezing breath!", true);
            }
        } else {
            damage = Utils.getRandom(baseDamage);
            if (player != null) {
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        player.getPackets().sendGameMessage(
                            "You are hit by the dragon's freezing breath!", true);
                        player.setNextGraphics(new Graphics(3440, 50, 0));
                    }
                }, 1);
            }
        }
        
        // Freeze chance and duration scale with tier
        int freezeChance = Math.min(60, 20 + (bossStats.tier * 4)); // 24% at tier 1, 60% at tier 10
        if (Utils.getRandom(100) < freezeChance) {
            int freezeDuration = Math.max(5000, 10000 + (bossStats.tier * 1000)); // 11s at tier 1, 20s at tier 10
            target.addFreezeDelay(freezeDuration);
        }
        
        delayHit(npc, 2, target, getRegularHit(npc, damage));
        return defs.getAttackDelay();
    }

    /**
     * Execute shock breath attack with balanced damage
     */
    private int executeShockBreathAttack(NPC npc, Entity target, NPCCombatDefinitions defs, BossStats bossStats) {
        int damage;
        final Player player = target instanceof Player ? (Player) target : null;
        
        npc.setNextAnimation(new Animation(17785));
        npc.setNextGraphics(new Graphics(3432, 0, 100));
        World.sendProjectile(npc, target, 3433, 60, 16, 120, 35, 16, 0);
        
        // Calculate balanced shock damage (highest damage special attack)
        int baseDamage = (int) (calculateBalancedDamage(bossStats.maxHit, bossStats.attackBonuses[3], target) * 1.1);
        
        if (Combat.hasAntiDragProtection(target)) {
            damage = getRandomMaxHit(npc, Math.max(1, baseDamage / 4), NPCCombatDefinitions.MAGE, target);
            if (player != null) {
                player.getPackets().sendGameMessage(
                    "Your shield absorbs most of the dragon's shocking breath!", true);
            }
        } else if (player != null && 
                  (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7))) {
            damage = getRandomMaxHit(npc, Math.max(1, baseDamage / 4), NPCCombatDefinitions.MAGE, target);
            if (player != null) {
                player.getPackets().sendGameMessage(
                    "Your prayer absorbs most of the dragon's shocking breath!", true);
            }
        } else {
            damage = Utils.getRandom(baseDamage);
            if (player != null) {
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        player.getPackets().sendGameMessage(
                            "You are hit by the dragon's shocking breath!", true);
                        player.setNextGraphics(new Graphics(3434, 25, 0));
                    }
                }, 0);
            }
        }
        
        delayHit(npc, 2, target, getRegularHit(npc, damage));
        return defs.getAttackDelay();
    }

    /**
     * Calculate balanced damage using boss stats and target defenses
     */
    private int calculateBalancedDamage(int baseMaxHit, int attackBonus, Entity target) {
        // Apply tier-appropriate damage scaling
        int scaledDamage = baseMaxHit;
        
        // Factor in attack bonuses (realistic combat calculation)
        if (attackBonus > 0) {
            scaledDamage = (int) (baseMaxHit * (1.0 + (attackBonus * 0.0008))); // 0.08% per bonus point
        }
        
        // Factor in target's defense if it's a player with gear
        if (target instanceof Player) {
            Player player = (Player) target;
            // This could integrate with player's defense bonuses if available
            // For now, keep it simple but realistic
        }
        
        return Math.max(1, scaledDamage);
    }

    /**
     * Provide educational guidance to help players learn boss mechanics
     * SMART SYSTEM: Contextual, progressive, and non-spammy
     */
    private void provideEducationalGuidance(Player player, NPC npc, int attackStyle, BossStats bossStats) {
        String username = player.getUsername();
        long currentTime = System.currentTimeMillis();
        
        // Check if player needs guidance (not spammy)
        Long lastTip = playerLastTip.get(username);
        if (lastTip != null && (currentTime - lastTip) < TIP_COOLDOWN) {
            return; // Too soon for another tip
        }
        
        // Check tip stage progression
        Integer tipStage = playerTipStage.get(username);
        if (tipStage == null) tipStage = 0;
        
        if (tipStage >= MAX_TIPS_PER_FIGHT) {
            return; // Already gave enough tips this fight
        }
        
        // Provide contextual education based on attack style and tier
        String tipMessage = null;
        boolean shouldGiveTip = false;
        
        // Progressive tip system
        if (tipStage == 0 && attackStyle != 0) {
            // First tip: Basic defense strategy
            tipMessage = getBossDefenseTip(bossStats.tier);
            shouldGiveTip = true;
        } else if (tipStage == 1 && (attackStyle == 3 || attackStyle == 4)) {
            // Second tip: Special attack mechanics
            tipMessage = getSpecialAttackTip(attackStyle);
            shouldGiveTip = true;
        } else if (tipStage == 2 && Utils.getRandom(3) == 0) {
            // Third tip: Gear recommendation (random chance to not overwhelm)
            tipMessage = getGearRecommendationTip(bossStats.tier);
            shouldGiveTip = true;
        }
        
        // Send tip if appropriate
        if (shouldGiveTip && tipMessage != null) {
            player.getPackets().sendGameMessage(
                "<col=4169E1>[Boss Guide]</col> " + tipMessage, true);
            
            // Update tracking
            playerLastTip.put(username, currentTime);
            playerTipStage.put(username, tipStage + 1);
            
            // Clean up old entries to prevent memory leaks
            cleanupEducationTracking();
        }
    }

    /**
     * Get boss defense tip based on tier
     */
    private String getBossDefenseTip(int tier) {
        if (tier <= 3) {
            return "Use Protect from Magic prayer to reduce breath attack damage. An Anti-dragon shield also helps!";
        } else if (tier <= 6) {
            return "This dragon is powerful! Use Protect from Magic, Anti-dragon shield, and bring plenty of food.";
        } else if (tier <= 8) {
            return "Elite dragon! Combine Protect from Magic, Anti-dragon shield, and consider Antifire potions for extra protection.";
        } else {
            return "Legendary dragon! Maximum protection needed: Protect from Magic, Anti-dragon shield, Super Antifire, and top-tier food.";
        }
    }

    /**
     * Get special attack tip
     */
    private String getSpecialAttackTip(int attackStyle) {
        switch (attackStyle) {
            case 3: return "Poison breath incoming! Consider bringing anti-poison potions or the Vengeance spell.";
            case 4: return "Freeze breath can stop you from moving! Time your attacks carefully and keep distance.";
            default: return "Watch for the dragon's special breath attacks - each one has different effects!";
        }
    }

    /**
     * Get gear recommendation based on boss tier
     */
    private String getGearRecommendationTip(int tier) {
        if (tier <= 2) {
            return "For this tier: Iron/Steel gear with Tier 1-2 weapons should work well.";
        } else if (tier <= 4) {
            return "Recommended gear: Mithril/Adamant equipment or Tier 3-4 balanced weapons.";
        } else if (tier <= 6) {
            return "Strong gear needed: Rune equipment or Tier 5-6 balanced weapons recommended.";
        } else if (tier <= 8) {
            return "High-tier gear required: Dragon equipment or Tier 7-8 balanced weapons strongly recommended.";
        } else {
            return "Endgame content! Tier 9-10 balanced weapons and best armor essential for survival.";
        }
    }

    /**
     * Clean up education tracking to prevent memory leaks
     */
    private void cleanupEducationTracking() {
        if (Utils.getRandom(50) == 0) { // 2% chance per tip
            long currentTime = System.currentTimeMillis();
            long maxAge = 1800000; // 30 minutes
            
            playerLastTip.entrySet().removeIf(entry -> 
                (currentTime - entry.getValue()) > maxAge);
            playerTipStage.entrySet().removeIf(entry -> 
                !playerLastTip.containsKey(entry.getKey()));
        }
    }

    /**
     * Estimate attack bonuses for tier (fallback)
     */
    private int[] estimateAttackBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        return new int[]{baseStat, baseStat, baseStat, baseStat, baseStat};
    }

    /**
     * Estimate defense bonuses for tier (fallback)
     */
    private int[] estimateDefenseBonuses(int tier) {
        return estimateAttackBonuses(tier); // Same calculation for simplicity
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
        return new Object[] { 50 }; // King Black Dragon NPC ID
    }

    /**
     * Boss stats container class
     */
    private static class BossStats {
        public int tier;
        public int maxHit;
        public int hitpoints;
        public int[] attackBonuses;
        public int[] defenseBonuses;
        public int maxBonus;
        public long timestamp;
    }
}