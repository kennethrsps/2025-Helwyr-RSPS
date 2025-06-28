package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
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
 * Enhanced Chaos Elemental Combat - MULTI-EFFECT CHAOS BOSS WITH TIER ANNOUNCEMENT
 * 
 * @author Enhanced by Balance System v2.1
 * @date May 31, 2025
 * @version 2.1 - Integrated with ItemBalancer & BossBalancer Systems
 * Features: Tier Announcements, Chaos Education, Balanced Scaling, Multiple Special Effects
 */
public class ChaosElementalCombat extends CombatScript {

    // Enhanced education system for chaos mechanics
    private static final Map<String, Long> playerLastChaosEffectTip = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Integer> playerChaosEffectTipStage = new ConcurrentHashMap<String, Integer>();
    private static final Map<Integer, Long> bossLastTierAnnouncement = new ConcurrentHashMap<Integer, Long>();
    private static final long CHAOS_EFFECT_TIP_COOLDOWN = 25000; // 25 seconds between chaos tips
    private static final long TIER_ANNOUNCEMENT_COOLDOWN = 300000; // 5 minutes between tier announcements
    private static final int MAX_CHAOS_EFFECT_TIPS_PER_FIGHT = 5; // More tips for complex chaos boss

    // Boss stats cache for chaos elemental
    private static final Map<Integer, ChaosElementalStats> chaosElementalStatsCache = new ConcurrentHashMap<Integer, ChaosElementalStats>();
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        
        // Get balanced chaos elemental stats
        ChaosElementalStats bossStats = getBalancedChaosElementalStats(npc);
        
        // Announce tier at the beginning (visible to all players)
        announceBossTier(npc, bossStats);
        
        // Determine attack style with tier-aware probabilities
        int attackStyle = determineAttackStyle(bossStats);
        int size = npc.getSize();

        if (attackStyle == 0) {
            return executeMeleeAttack(npc, target, defs, size, bossStats);
        } else if (attackStyle == 1 || attackStyle == 2) {
            return executeFreezeAttack(npc, target, defs, bossStats);
        } else if (attackStyle == 3) {
            return executePoisonAttack(npc, target, defs, bossStats);
        } else if (attackStyle == 4) {
            return executeRangedAttack(npc, target, defs, bossStats);
        } else if (attackStyle == 5) {
            return executeTeleportAttack(npc, target, defs, bossStats);
        }
        
        return defs.getAttackDelay();
    }

    /**
     * Announce boss tier to all players in the area (Simple and Elegant)
     */
    private void announceBossTier(NPC npc, ChaosElementalStats bossStats) {
        int npcId = npc.getId();
        long currentTime = System.currentTimeMillis();
        
        // Check if we recently announced for this boss instance
        Long lastAnnouncement = bossLastTierAnnouncement.get(npcId + npc.hashCode());
        if (lastAnnouncement != null && (currentTime - lastAnnouncement) < TIER_ANNOUNCEMENT_COOLDOWN) {
            return; // Too soon for another announcement
        }
        
        // Find all nearby players for the announcement
        ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
        for (Entity entity : possibleTargets) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                
                // Simple and elegant tier announcement
                String tierName = getBossTierName(bossStats.tier);
                String balanceStatus = bossStats.isBalanced ? "PERFECTLY BALANCED" : "ESTIMATED TIER";
                String difficultyWarning = getDifficultyWarning(bossStats.tier);
                
                player.getPackets().sendGameMessage("", true); // Spacer
                player.getPackets().sendGameMessage(
                    "<col=800080>========================================================================</col>", true);
                player.getPackets().sendGameMessage(
                    "<col=800080>|</col>                       <col=FFFF00>CHAOS ELEMENTAL WARPS INTO EXISTENCE!</col>                      <col=800080>|</col>", true);
                player.getPackets().sendGameMessage(
                    "<col=800080>|</col>                            <col=00FF00>" + tierName + "</col>                            <col=800080>|</col>", true);
                player.getPackets().sendGameMessage(
                    "<col=800080>|</col>                          <col=CYAN>" + balanceStatus + "</col>                          <col=800080>|</col>", true);
                player.getPackets().sendGameMessage(
                    "<col=800080>|</col>                        <col=FF6B35>" + difficultyWarning + "</col>                        <col=800080>|</col>", true);
                player.getPackets().sendGameMessage(
                    "<col=800080>========================================================================</col>", true);
                player.getPackets().sendGameMessage("", true); // Spacer
                
                // Additional tier-specific warning
                if (bossStats.tier >= 6) {
                    player.getPackets().sendGameMessage(
                        "<col=800080>[CHAOS INCARNATE]</col> <col=FFFF00>This elemental can freeze, poison, and teleport you! Prepare for unpredictable chaos!</col>", true);
                }
            }
        }
        
        // Update announcement tracking
        bossLastTierAnnouncement.put(npcId + npc.hashCode(), currentTime);
    }

    /**
     * Get balanced chaos elemental stats with caching
     */
    private ChaosElementalStats getBalancedChaosElementalStats(NPC npc) {
        int npcId = npc.getId();
        
        // Check cache first
        ChaosElementalStats cached = chaosElementalStatsCache.get(npcId);
        if (cached != null && System.currentTimeMillis() - cached.timestamp < 300000) { // 5 min cache
            return cached;
        }

        ChaosElementalStats stats = new ChaosElementalStats();
        
        try {
            // Estimate tier from combat definitions
            stats.tier = estimateChaosElementalTierFromStats(npc.getCombatDefinitions());
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
                // Fallback: estimate chaos-appropriate bonuses
                stats.attackBonuses = estimateChaosElementalAttackBonuses(stats.tier);
                stats.defenseBonuses = estimateChaosElementalDefenseBonuses(stats.tier);
                stats.maxBonus = getMaxBonus(stats.attackBonuses);
                stats.isBalanced = false;
            }
            
            // Calculate chaos-specific stats (balanced multi-style with special effects)
            stats.meleeMaxHit = calculateChaosElementalDamage(stats.maxHit, stats.attackBonuses[1], 1.0); // Slash-based
            stats.magicMaxHit = calculateChaosElementalDamage(stats.maxHit, stats.attackBonuses[3], 1.1); // Magic enhanced
            stats.rangedMaxHit = calculateChaosElementalDamage(stats.maxHit, stats.attackBonuses[4], 1.0); // Ranged balanced
            
            // Tier-based chaos effect mechanics
            stats.specialAttackChance = Math.min(70, 40 + (stats.tier * 3)); // 43% at tier 1, 70% at tier 10
            stats.freezeDuration = Math.max(10000, 12000 + (stats.tier * 800)); // 12.8-18.4 seconds
            stats.poisonDamage = Math.max(60, 60 + (stats.tier * 15)); // 75-210 poison damage
            stats.teleportRange = Math.max(2, 1 + (stats.tier / 3)); // 1-4 tile teleport range
            
            stats.timestamp = System.currentTimeMillis();
            chaosElementalStatsCache.put(npcId, stats);
            
        } catch (Exception e) {
            Logger.handle(e);
            // Safe fallback values for chaos elemental
            stats.tier = 5; // Chaos Elemental is typically mid-tier
            stats.maxHit = 189; // Original hard-coded value as fallback
            stats.meleeMaxHit = 189;
            stats.magicMaxHit = 208; // 10% enhanced
            stats.rangedMaxHit = 189;
            stats.hitpoints = 8000;
            stats.attackBonuses = new int[]{400, 500, 400, 600, 500}; // Balanced chaos
            stats.defenseBonuses = new int[]{450, 450, 450, 500, 450};
            stats.maxBonus = 600;
            stats.specialAttackChance = 55;
            stats.freezeDuration = 16000; // 16 seconds
            stats.poisonDamage = 135;
            stats.teleportRange = 2;
            stats.isBalanced = false;
        }
        
        return stats;
    }

    /**
     * Determine attack style with tier-aware chaos probabilities
     */
    private int determineAttackStyle(ChaosElementalStats bossStats) {
        // Higher tier chaos elementals use more special attacks
        if (Utils.getRandom(100) < bossStats.specialAttackChance) {
            // Special attacks (1-5) - weighted towards chaos effects
            int specialRoll = Utils.getRandom(10);
            if (specialRoll <= 2) return 1; // Freeze attack (30%)
            else if (specialRoll <= 4) return 2; // Freeze attack (20%)
            else if (specialRoll <= 6) return 3; // Poison attack (20%)
            else if (specialRoll <= 8) return 4; // Ranged attack (20%)
            else return 5; // Teleport attack (10%)
        } else {
            return 0; // Melee attack
        }
    }

    /**
     * Execute melee attack with balanced damage
     */
    private int executeMeleeAttack(NPC npc, Entity target, NPCCombatDefinitions defs, int size, ChaosElementalStats bossStats) {
        int distanceX = target.getX() - npc.getX();
        int distanceY = target.getY() - npc.getY();
        
        if (distanceX > size || distanceX < -1 || distanceY > size || distanceY < -1) {
            // Too far for melee, use special attack instead
            return executeRandomSpecialAttack(npc, target, defs, bossStats);
        } else {
            // Calculate balanced melee damage
            int meleeDamage = calculateBalancedMeleeDamage(bossStats, target);
            
            delayHit(npc, 0, target, getMeleeHit(npc, meleeDamage));
            npc.setNextAnimation(new Animation(defs.getAttackEmote()));
            
            // Provide melee education
            if (target instanceof Player && Utils.random(8) == 0) { // 12.5% chance
                provideChaosEducation((Player) target, npc, "MELEE", bossStats);
            }
            
            return defs.getAttackDelay();
        }
    }

    /**
     * Execute random special attack when melee is out of range
     */
    private int executeRandomSpecialAttack(NPC npc, Entity target, NPCCombatDefinitions defs, ChaosElementalStats bossStats) {
        int specialAttack = Utils.getRandom(4) + 1; // 1-4 (excluding teleport for range fallback)
        
        switch (specialAttack) {
            case 1: return executeFreezeAttack(npc, target, defs, bossStats);
            case 2: return executePoisonAttack(npc, target, defs, bossStats);
            case 3: return executeRangedAttack(npc, target, defs, bossStats);
            default: return executeFreezeAttack(npc, target, defs, bossStats);
        }
    }

    /**
     * Execute freeze attack with tier-appropriate duration
     */
    private int executeFreezeAttack(NPC npc, Entity target, NPCCombatDefinitions defs, ChaosElementalStats bossStats) {
        // Calculate balanced magic damage
        int magicDamage = calculateBalancedMagicDamage(bossStats, target);
        
        npc.setNextAnimation(new Animation(defs.getAttackEmote()));
        target.setNextGraphics(new Graphics(556));
        delayHit(npc, 2, target, getMagicHit(npc, magicDamage));
        
        // Apply freeze effect to all players
        ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
        boolean frozeAnyPlayer = false;
        
        for (Entity t : possibleTargets) {
            if (t instanceof Player) {
                Player p = (Player) t;
                if (p.getFreezeDelay() == 0) {
                    p.sendMessage("<col=F7A6A6>The Chaos Elemental has frozen you!", true);
                    p.addFreezeDelay(bossStats.freezeDuration);
                    frozeAnyPlayer = true;
                }
            }
        }
        
        // Provide freeze education
        if (frozeAnyPlayer) {
            provideChaosEffectEducation(possibleTargets, npc, "FREEZE", bossStats);
        }
        
        return defs.getAttackDelay();
    }

    /**
     * Execute poison attack with tier-appropriate damage
     */
    private int executePoisonAttack(NPC npc, Entity target, NPCCombatDefinitions defs, ChaosElementalStats bossStats) {
        // Calculate balanced melee damage for poison attack
        int meleeDamage = calculateBalancedMeleeDamage(bossStats, target);
        
        npc.setNextAnimation(new Animation(defs.getAttackEmote()));
        npc.setNextGraphics(new Graphics(550));
        delayHit(npc, 2, target, getMeleeHit(npc, meleeDamage));
        
        // Apply poison effect to all players
        ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
        boolean poisonedAnyPlayer = false;
        
        for (Entity t : possibleTargets) {
            if (t instanceof Player) {
                Player p = (Player) t;
                if (!p.getPoison().isPoisoned()) {
                    p.getPoison().makePoisoned(bossStats.poisonDamage);
                    p.sendMessage("<col=F7A6A6>The Chaos Elemental has poisoned you!", true);
                    poisonedAnyPlayer = true;
                }
            }
        }
        
        // Provide poison education
        if (poisonedAnyPlayer) {
            provideChaosEffectEducation(possibleTargets, npc, "POISON", bossStats);
        }
        
        return defs.getAttackDelay();
    }

    /**
     * Execute ranged attack with balanced damage
     */
    private int executeRangedAttack(NPC npc, Entity target, NPCCombatDefinitions defs, ChaosElementalStats bossStats) {
        final Player player = target instanceof Player ? (Player) target : null;
        
        // Calculate balanced ranged damage
        int rangedDamage = calculateBalancedRangedDamage(bossStats, target);
        
        npc.setNextAnimation(new Animation(defs.getAttackEmote()));
        npc.setNextGraphics(new Graphics(553));
        
        if (player != null) {
            delayHit(npc, 1, target, getRangeHit(npc, rangedDamage));
            
            // Provide ranged education
            if (Utils.random(8) == 0) { // 12.5% chance
                provideChaosEducation(player, npc, "RANGED", bossStats);
            }
        }
        
        return defs.getAttackDelay();
    }

    /**
     * Execute teleport attack with tier-appropriate range
     */
    private int executeTeleportAttack(NPC npc, Entity target, NPCCombatDefinitions defs, ChaosElementalStats bossStats) {
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                int way = Utils.random(4); // 0-3 for four directions
                ArrayList<Entity> possibleTargets = npc.getPossibleTargets();
                boolean teleportedAnyPlayer = false;
                
                for (Entity t : possibleTargets) {
                    if (t instanceof Player) {
                        Player p = (Player) t;
                        
                        // Calculate teleport destination with tier-appropriate range
                        int teleportRange = bossStats.teleportRange;
                        WorldTile newTile;
                        
                        switch (way) {
                            case 0: // Southwest
                                newTile = new WorldTile(p.getX() - Utils.random(1, teleportRange + 1), 
                                                      p.getY() - Utils.random(1, teleportRange + 1), p.getPlane());
                                break;
                            case 1: // Northwest
                                newTile = new WorldTile(p.getX() - Utils.random(1, teleportRange + 1), 
                                                      p.getY() + Utils.random(1, teleportRange + 1), p.getPlane());
                                break;
                            case 2: // Northeast
                                newTile = new WorldTile(p.getX() + Utils.random(1, teleportRange + 1), 
                                                      p.getY() + Utils.random(1, teleportRange + 1), p.getPlane());
                                break;
                            default: // Southeast
                                newTile = new WorldTile(p.getX() + Utils.random(1, teleportRange + 1), 
                                                      p.getY() - Utils.random(1, teleportRange + 1), p.getPlane());
                                break;
                        }
                        
                        p.setNextWorldTile(newTile);
                        p.sendMessage("<col=F7A6A6>The Chaos Elemental teleports you!", true);
                        teleportedAnyPlayer = true;
                    }
                }
                
                // Provide teleport education
                if (teleportedAnyPlayer) {
                    provideChaosEffectEducation(possibleTargets, npc, "TELEPORT", bossStats);
                }
            }
        }, 1);
        
        return defs.getAttackDelay();
    }

    /**
     * Calculate balanced melee damage
     */
    private int calculateBalancedMeleeDamage(ChaosElementalStats bossStats, Entity target) {
        int baseDamage = getRandomMaxHit(null, bossStats.meleeMaxHit, NPCCombatDefinitions.MELEE, target);
        
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
     * Calculate balanced magic damage
     */
    private int calculateBalancedMagicDamage(ChaosElementalStats bossStats, Entity target) {
        int baseDamage = getRandomMaxHit(null, bossStats.magicMaxHit, NPCCombatDefinitions.MAGE, target);
        
        // Apply protection calculations if target is player
        if (target instanceof Player) {
            Player player = (Player) target;
            if (player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 7)) {
                baseDamage = (int)(baseDamage * 0.6); // 40% reduction with protect from magic
            }
        }
        
        return Math.max(1, baseDamage);
    }

    /**
     * Calculate balanced ranged damage
     */
    private int calculateBalancedRangedDamage(ChaosElementalStats bossStats, Entity target) {
        int baseDamage = getRandomMaxHit(null, bossStats.rangedMaxHit, NPCCombatDefinitions.RANGE, target);
        
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
     * Calculate chaos elemental damage based on attack bonus and modifier
     */
    private int calculateChaosElementalDamage(int baseMaxHit, int attackBonus, double modifier) {
        int damage = (int) (baseMaxHit * modifier);
        
        // Apply attack bonus scaling
        if (attackBonus > 0) {
            damage = (int) (damage * (1.0 + (attackBonus * 0.0008))); // 0.08% per bonus point
        }
        
        return Math.max(1, damage);
    }

    /**
     * Provide chaos effect education for special attacks
     */
    private void provideChaosEffectEducation(ArrayList<Entity> targets, NPC npc, String effectType, ChaosElementalStats bossStats) {
        for (Entity entity : targets) {
            if (entity instanceof Player && Utils.random(3) == 0) { // 33% chance for special effects
                Player player = (Player) entity;
                String tipMessage = getChaosEffectTip(bossStats.tier, effectType);
                if (tipMessage != null) {
                    player.getPackets().sendGameMessage(
                        "<col=800080>[Chaos Guide]</col> " + tipMessage, true);
                }
                break; // Only one tip per special effect
            }
        }
    }

    /**
     * Provide general chaos education
     */
    private void provideChaosEducation(Player player, NPC npc, String attackType, ChaosElementalStats bossStats) {
        String username = player.getUsername();
        long currentTime = System.currentTimeMillis();
        
        // Check cooldown
        Long lastTip = playerLastChaosEffectTip.get(username);
        if (lastTip != null && (currentTime - lastTip) < CHAOS_EFFECT_TIP_COOLDOWN) {
            return;
        }
        
        // Check tip stage
        Integer tipStage = playerChaosEffectTipStage.get(username);
        if (tipStage == null) tipStage = 0;
        if (tipStage >= MAX_CHAOS_EFFECT_TIPS_PER_FIGHT) return;
        
        String tipMessage = getChaosGeneralTip(bossStats.tier, attackType, tipStage);
        if (tipMessage != null) {
            player.getPackets().sendGameMessage(
                "<col=800080>[Chaos Guide]</col> " + tipMessage, true);
            
            playerLastChaosEffectTip.put(username, currentTime);
            playerChaosEffectTipStage.put(username, tipStage + 1);
        }
    }

    /**
     * Get chaos effect tip for special attacks
     */
    private String getChaosEffectTip(int tier, String effectType) {
        if (effectType.equals("FREEZE")) {
            if (tier <= 4) {
                return "Chaos freeze affects all nearby players! The duration increases with higher tiers.";
            } else {
                return "Elite Chaos freeze lasts much longer! Prepare for extended immobilization.";
            }
        } else if (effectType.equals("POISON")) {
            if (tier <= 4) {
                return "Chaos poison spreads to all players! Bring antipoison potions for the group.";
            } else {
                return "Elite Chaos poison is more potent! Super antipoison recommended for higher tiers.";
            }
        } else if (effectType.equals("TELEPORT")) {
            if (tier <= 4) {
                return "Chaos teleport moves you randomly! Higher tiers teleport you further away.";
            } else {
                return "Elite Chaos teleport has greater range! Be prepared to be moved much further.";
            }
        }
        return null;
    }

    /**
     * Get general chaos tip
     */
    private String getChaosGeneralTip(int tier, String attackType, int stage) {
        if (stage == 0) {
            if (attackType.equals("MELEE")) {
                return "Chaos Elemental's melee is dangerous up close. Keep distance to avoid it.";
            } else if (attackType.equals("RANGED")) {
                return "Chaos Elemental uses ranged attacks at distance. Use Protect from Missiles.";
            }
        } else if (stage == 1) {
            return "Chaos Elemental has 6 different attacks! Watch for freeze, poison, and teleport effects.";
        } else if (stage == 2) {
            return "Higher tier Chaos Elementals use special attacks more frequently. Stay alert!";
        } else if (stage == 3) {
            return "All special effects target everyone nearby. Coordinate with your team for maximum survival.";
        } else if (stage == 4) {
            return "Chaos effects scale with tier - freezes last longer, poison hits harder, teleports go further!";
        }
        return null;
    }

    /**
     * Get boss tier name for announcements
     */
    private String getBossTierName(int tier) {
        switch (tier) {
            case 1: return "TIER 1 - MINOR CHAOS SPIRIT";
            case 2: return "TIER 2 - CHAOS WISP";
            case 3: return "TIER 3 - CHAOS ENTITY";
            case 4: return "TIER 4 - CHAOS ELEMENTAL";
            case 5: return "TIER 5 - GREATER CHAOS ELEMENTAL";
            case 6: return "TIER 6 - CHAOS LORD";
            case 7: return "TIER 7 - ELITE CHAOS MASTER";
            case 8: return "TIER 8 - LEGENDARY CHAOS INCARNATE";
            case 9: return "TIER 9 - MYTHICAL CHAOS GOD";
            case 10: return "TIER 10 - DIVINE CHAOS EMPEROR";
            default: return "UNKNOWN TIER CHAOS ENTITY";
        }
    }

    /**
     * Get difficulty warning for tier announcement
     */
    private String getDifficultyWarning(int tier) {
        if (tier <= 3) {
            return "MODERATE THREAT - BASIC CHAOS EFFECTS";
        } else if (tier <= 5) {
            return "HIGH THREAT - ENHANCED CHAOS ABILITIES";
        } else if (tier <= 7) {
            return "SEVERE THREAT - MASTER CHAOS MANIPULATION";
        } else {
            return "EXTREME THREAT - PURE CHAOS INCARNATE";
        }
    }

    /**
     * Estimate chaos elemental tier from combat stats
     */
    private int estimateChaosElementalTierFromStats(NPCCombatDefinitions defs) {
        int hp = defs.getHitpoints();
        int maxHit = defs.getMaxHit();
        
        // Chaos elementals typically low-mid tier with special abilities
        if (hp <= 1500 && maxHit <= 30) return 2;       // Chaos Wisp
        if (hp <= 3200 && maxHit <= 50) return 3;       // Chaos Entity
        if (hp <= 6000 && maxHit <= 80) return 4;       // Chaos Elemental
        if (hp <= 10500 && maxHit <= 125) return 5;     // Greater Chaos Elemental (typical)
        if (hp <= 17000 && maxHit <= 185) return 6;     // Chaos Lord
        if (hp <= 25500 && maxHit <= 260) return 7;     // Elite Chaos Master
        if (hp <= 36000 && maxHit <= 350) return 8;     // Legendary Chaos Incarnate
        if (hp <= 50000 && maxHit <= 460) return 9;     // Mythical Chaos God
        return 10; // Divine Chaos Emperor
    }

    /**
     * Estimate chaos elemental attack bonuses for tier
     */
    private int[] estimateChaosElementalAttackBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Chaos elementals have balanced bonuses for multi-style chaos
        return new int[]{
            baseStat,                    // stab
            baseStat,                    // slash
            baseStat,                    // crush
            (int)(baseStat * 1.1),      // magic (enhanced for freeze/poison)
            baseStat                     // ranged
        };
    }

    /**
     * Estimate chaos elemental defense bonuses for tier
     */
    private int[] estimateChaosElementalDefenseBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Chaos elementals have slightly enhanced magical defenses
        return new int[]{
            baseStat,                    // stab defense
            baseStat,                    // slash defense
            baseStat,                    // crush defense
            (int)(baseStat * 1.1),      // magic defense (enhanced)
            baseStat                     // ranged defense
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
        return new Object[] { 3200 }; // Chaos Elemental NPC ID
    }

    /**
     * Chaos Elemental stats container class
     */
    private static class ChaosElementalStats {
        public int tier;
        public int maxHit;
        public int meleeMaxHit;
        public int magicMaxHit;
        public int rangedMaxHit;
        public int hitpoints;
        public int[] attackBonuses;
        public int[] defenseBonuses;
        public int maxBonus;
        public int specialAttackChance;
        public int freezeDuration;
        public int poisonDamage;
        public int teleportRange;
        public boolean isBalanced;
        public long timestamp;
    }
}