package com.rs.game.npc.combat.impl;

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

/**
 * Enhanced Mercenary Mage Combat with Boss Balancer Integration and Guidance System
 * 
 * @author Zeus
 * @date June 02, 2025
 * @version 2.0 - Enhanced with Boss Balancer, Anti-Safespot, and Guidance System
 */
public class MercenaryMageCombat extends CombatScript {

    // Enhanced battle dialogue
    public static final String[] ATTACKS = new String[] {
        "I will make you suffer!", "Death is your only option!",
        "Why fight back?", "It is time for you to die.",
        "IS THIS ALL YOU'VE GOT?", "Face the power of arcane mastery!",
        "Your magic is weak compared to mine!", "Witness true magical might!"
    };

    // Combat tracking variables
    private int attackCount = 0;
    private long lastGuidanceMessage = 0;
    private long lastSafespotCheck = 0;
    private boolean hasGivenInitialGuidance = false;
    private boolean hasWarnedAboutAOE = false;
    private boolean hasWarnedAboutHeal = false;
    
    // Guidance message intervals
    private static final long GUIDANCE_INTERVAL = 18000; // 18 seconds
    private static final long SAFESPOT_CHECK_INTERVAL = 4000; // 4 seconds
    
    // Special attack tracking
    private int obliterateCount = 0;
    private int burnCount = 0;
    private int lineAttackCount = 0;
    private int healCount = 0;
    
    // Anti-safespot tracking
    private int consecutiveMagicMisses = 0;
    private boolean lastAttackConnected = true;

    @Override
    public int attack(final NPC npc, Entity target) {
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        
        // Enhanced null safety
        if (!isValidCombatState(npc, target, defs)) {
            return 5;
        }
        
        // Boss Guidance System
        if (target instanceof Player) {
            Player player = (Player) target;
            provideMercenaryMageGuidance(npc, player);
            checkAndHandleMageSafespot(npc, player);
        }
        
        // Get enhanced max hit using Boss Balancer
        int enhancedMaxHit = getEnhancedMagicMaxHit(npc, defs);
        
        // Increment attack counter
        attackCount++;
        
        // Choose attack style with enhanced mechanics
        int attackStyle = Utils.random(5);
        
        if (attackStyle == 0) {
            obliterateCount++;
            return performObliterateAttack(npc, target, enhancedMaxHit);
        } else if (attackStyle == 1) {
            burnCount++;
            return performBurnAttack(npc, target, enhancedMaxHit);
        } else if (attackStyle == 2) {
            lineAttackCount++;
            return performLineAttack(npc, target, enhancedMaxHit);
        } else if (attackStyle == 3) {
            return performMagicBolt(npc, target, enhancedMaxHit);
        } else {
            healCount++;
            return performMageHeal(npc, target, enhancedMaxHit);
        }
    }
    
    /**
     * Enhanced Obliterate Attack with Boss Balancer scaling
     */
    private int performObliterateAttack(final NPC npc, Entity target, int enhancedMaxHit) {
        npc.setNextAnimation(new Animation(1979));
        final WorldTile center = new WorldTile(target);
        World.sendGraphics(npc, new Graphics(2929), center);
        npc.setNextForceTalk(new ForceTalk("Obliterate!"));
        
        // Announce the attack to nearby players
        announceSpecialAttack(center, "OBLITERATE incoming! 3-tile radius explosion!");
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                for (Player player : World.getPlayers()) {
                    if (player == null || player.isDead() || player.hasFinished())
                        continue;
                        
                    if (player.withinDistance(center, 3)) {
                        if (!player.getMusicsManager().hasMusic(843))
                            player.getMusicsManager().playMusic(843);
                            
                        // Use enhanced damage calculation
                        int damage = calculateEnhancedDamage(npc, enhancedMaxHit, 1.0); // Full power AOE
                        delayHit(npc, 0, player, new Hit(npc, damage, HitLook.MAGIC_DAMAGE));
                        
                        // Provide guidance to hit players
                        if (Utils.random(3) == 0) {
                            player.sendMessage("<col=ff0000>Mage Guide: Obliterate hits a 3x3 area - spread out from other players!");
                        }
                    }
                }
                stop();
            }
        }, 4);
        
        lastAttackConnected = true; // AOE always "connects"
        return 6; // Longer delay for powerful attack
    }
    
    /**
     * Enhanced Burn Attack with Boss Balancer scaling
     */
    private int performBurnAttack(final NPC npc, Entity target, int enhancedMaxHit) {
        npc.setNextAnimation(new Animation(1979));
        final WorldTile center = new WorldTile(target);
        World.sendGraphics(npc, new Graphics(2191), center);
        npc.setNextForceTalk(new ForceTalk("How are the burns?"));
        
        // Announce the attack
        announceSpecialAttack(center, "BURN DOT attack! Multi-hit damage over time!");
        
        WorldTasksManager.schedule(new WorldTask() {
            int count = 0;
            
            @Override
            public void run() {
                for (Player player : World.getPlayers()) {
                    if (player == null || player.isDead() || player.hasFinished())
                        continue;
                        
                    if (player.withinDistance(center, 1)) {
                        // Reduced damage per tick but multiple hits
                        int damage = calculateEnhancedDamage(npc, enhancedMaxHit, 0.4); // 40% per hit
                        delayHit(npc, 0, player, new Hit(npc, damage, HitLook.MAGIC_DAMAGE));
                        
                        // Burn effect guidance
                        if (count == 0) { // First hit
                            player.sendMessage("<col=ff6600>Mage Guide: Burn attack deals damage over time! Move away!");
                        }
                    }
                }
                
                if (count++ == 10) {
                    stop();
                    return;
                }
            }
        }, 0, 1); // Hit every tick
        
        lastAttackConnected = true;
        return 8; // Longer delay for DOT attack
    }
    
    /**
     * Enhanced Line Attack with Boss Balancer scaling
     */
    private int performLineAttack(final NPC npc, Entity target, int enhancedMaxHit) {
        npc.setNextAnimation(new Animation(1979));
        final int dir = Utils.random(Utils.DIRECTION_DELTA_X.length);
        final WorldTile center = new WorldTile(npc.getX() + Utils.DIRECTION_DELTA_X[dir] * 5, 
                                             npc.getY() + Utils.DIRECTION_DELTA_Y[dir] * 5, 0);
        npc.setNextForceTalk(new ForceTalk("I think it's time to clean my room!"));
        
        // Announce line attack
        announceSpecialAttack(center, "LINE ATTACK! Magical beam in a straight line!");
        
        WorldTasksManager.schedule(new WorldTask() {
            int count = 0;
            
            @Override
            public void run() {
                for (Player player : World.getPlayers()) {
                    if (player == null || player.isDead() || player.hasFinished())
                        continue;
                        
                    // Line attack logic (enhanced from original)
                    boolean inLine = false;
                    
                    if (Utils.DIRECTION_DELTA_X[dir] == 0) {
                        if (player.getX() == center.getX()) inLine = true;
                    }
                    if (Utils.DIRECTION_DELTA_Y[dir] == 0) {
                        if (player.getY() == center.getY()) inLine = true;
                    }
                    if (Utils.DIRECTION_DELTA_X[dir] != 0) {
                        if (Math.abs(player.getX() - center.getX()) <= 5) inLine = true;
                    }
                    if (Utils.DIRECTION_DELTA_Y[dir] != 0) {
                        if (Math.abs(player.getY() - center.getY()) <= 5) inLine = true;
                    }
                    
                    if (inLine) {
                        int damage = calculateEnhancedDamage(npc, enhancedMaxHit, 0.9); // 90% damage
                        delayHit(npc, 0, player, new Hit(npc, damage, HitLook.MAGIC_DAMAGE));
                        
                        // Line attack guidance
                        player.sendMessage("<col=9900ff>Mage Guide: Line attacks cover straight paths - move perpendicular!");
                    }
                }
                
                if (count++ == 5) {
                    stop();
                    return;
                }
            }
        }, 0, 1);
        
        World.sendProjectile(npc, center, 2196, 0, 0, 5, 35, 0, 0);
        lastAttackConnected = true;
        return 7;
    }
    
    /**
     * Enhanced Magic Bolt with Boss Balancer scaling
     */
    private int performMagicBolt(final NPC npc, Entity target, int enhancedMaxHit) {
        // Check if this attack will connect (for safespot detection)
        boolean canHit = !npc.clipedProjectile(target, true);
        
        if (canHit) {
            int damage = calculateEnhancedDamage(npc, enhancedMaxHit, 1.0); // Full single-target damage
            delayHit(npc, 2, target, getMagicHit(npc, damage));
            lastAttackConnected = true;
            consecutiveMagicMisses = 0;
        } else {
            lastAttackConnected = false;
            consecutiveMagicMisses++;
        }
        
        World.sendProjectile(npc, target, 2873, 34, 16, 40, 35, 16, 0);
        npc.setNextAnimation(new Animation(14221));
        npc.setNextForceTalk(new ForceTalk(ATTACKS[Utils.random(ATTACKS.length)]));
        
        return 5;
    }
    
    /**
     * Enhanced Mage Heal with Boss Balancer scaling
     */
    private int performMageHeal(final NPC npc, Entity target, int enhancedMaxHit) {
        npc.setNextAnimation(new Animation(1979));
        npc.setNextGraphics(new Graphics(444));
        
        // Scale heal amount based on tier
        int healAmount = getEnhancedHealAmount(npc);
        npc.heal(healAmount);
        
        npc.setNextForceTalk(new ForceTalk("The arcane energies restore me!"));
        
        // Announce heal to all nearby players
        announceToNearbyPlayers(npc, "<col=00ff00>Mercenary Mage healed " + healAmount + " HP!");
        
        if (target instanceof Player) {
            Player player = (Player) target;
            player.sendMessage("<col=ffff00>Mage Guide: The mage can heal! Focus fire to prevent recovery!");
        }
        
        return 6; // Longer delay after healing
    }
    
    /**
     * Boss Guidance System for Mercenary Mage
     */
    private void provideMercenaryMageGuidance(NPC npc, Player player) {
        long currentTime = System.currentTimeMillis();
        
        // Initial guidance
        if (!hasGivenInitialGuidance) {
            hasGivenInitialGuidance = true;
            npc.setNextForceTalk(new ForceTalk("A worthy opponent approaches my magical domain!"));
            player.sendMessage("<col=9900ff>Mage Guide: Mercenary Mage uses powerful AOE magic attacks!");
            player.sendMessage("<col=9900ff>Strategy: Watch for telegraphed attacks and spread out from other players!");
            lastGuidanceMessage = currentTime;
            return;
        }
        
        // AOE warning
        if ((obliterateCount >= 1 || burnCount >= 1) && !hasWarnedAboutAOE) {
            hasWarnedAboutAOE = true;
            player.sendMessage("<col=ff0000>Mage Guide: AREA ATTACKS! Obliterate (3x3) and Burn (DOT) hit multiple players!");
            player.sendMessage("<col=ff6600>AOE Tip: Spread out and don't cluster together!");
            lastGuidanceMessage = currentTime;
        }
        
        // Heal warning
        if (healCount >= 1 && !hasWarnedAboutHeal) {
            hasWarnedAboutHeal = true;
            player.sendMessage("<col=00ff00>Mage Guide: The mage can heal! Focus attacks to prevent recovery!");
            player.sendMessage("<col=ffff00>Heal Tip: Coordinate with other players for maximum DPS!");
            lastGuidanceMessage = currentTime;
        }
        
        // Periodic advanced guidance
        if (currentTime - lastGuidanceMessage >= GUIDANCE_INTERVAL) {
            lastGuidanceMessage = currentTime;
            
            double hpPercentage = (double) npc.getHitpoints() / npc.getMaxHitpoints();
            
            if (hpPercentage < 0.3) {
                player.sendMessage("<col=ff0000>Mage Guide: Low HP - expect more frequent heal attempts!");
            } else if (lineAttackCount >= 2) {
                player.sendMessage("<col=9900ff>Mage Guide: Line attacks cover straight paths - move diagonally!");
            } else if (attackCount > 15) {
                String[] advancedTips = {
                    "Use magic protection prayers to reduce damage!",
                    "Coordinate timing to interrupt heal attempts!",
                    "Stay mobile to avoid area attacks!",
                    "High magic defense gear helps against this boss!"
                };
                player.sendMessage("<col=00ff00>Advanced Tip: " + advancedTips[Utils.random(advancedTips.length)]);
            }
        }
    }
    
    /**
     * Anti-safespot system for mage
     */
    private void checkAndHandleMageSafespot(NPC npc, Player player) {
        long currentTime = System.currentTimeMillis();
        
        if (currentTime - lastSafespotCheck < SAFESPOT_CHECK_INTERVAL) {
            return;
        }
        lastSafespotCheck = currentTime;
        
        // Detect safespotting (mage can't hit with projectiles)
        if (consecutiveMagicMisses >= 3 && !lastAttackConnected) {
            handleMageSafespotting(npc, player);
        }
    }
    
    /**
     * Handle mage safespotting with magical countermeasures
     */
    private void handleMageSafespotting(NPC npc, Player player) {
        int countermeasure = Utils.random(3);
        
        switch (countermeasure) {
            case 0:
                // Magical teleportation
                if (player.getAttackedBy() == npc) {
                    WorldTile newTile = new WorldTile(npc.getX() + Utils.random(3) - 1, 
                                                    npc.getY() + Utils.random(3) - 1, npc.getPlane());
                    player.setNextWorldTile(newTile);
                    player.setNextGraphics(new Graphics(2929)); // Teleport effect
                    player.sendMessage("<col=9900ff>The mage's magic pulls you into the battle!");
                    npc.setNextForceTalk(new ForceTalk("No hiding from arcane power!"));
                }
                break;
                
            case 1:
                // Piercing magic bolt
                npc.setNextForceTalk(new ForceTalk("Magic transcends physical barriers!"));
                player.sendMessage("<col=9900ff>The mage's spell pierces through cover!");
                int damage = calculateEnhancedDamage(npc, npc.getCombatDefinitions().getMaxHit(), 0.7);
                delayHit(npc, 1, player, new Hit(npc, damage, HitLook.MAGIC_DAMAGE));
                break;
                
            case 2:
                // Mage repositioning
                if (Utils.random(2) == 0) {
                    int newX = player.getX() + Utils.random(6) - 3;
                    int newY = player.getY() + Utils.random(6) - 3;
                    WorldTile newTile = new WorldTile(newX, newY, player.getPlane());
                    npc.setNextWorldTile(newTile);
                    npc.setNextGraphics(new Graphics(444)); // Teleport effect
                    npc.setNextForceTalk(new ForceTalk("I shall relocate my magical focus!"));
                    player.sendMessage("<col=ffff00>Mage Guide: The mage teleports to prevent safespotting!");
                }
                break;
        }
        
        // Reset consecutive misses after countermeasure
        consecutiveMagicMisses = 0;
    }
    
    /**
     * Get enhanced max hit using Boss Balancer
     */
    private int getEnhancedMagicMaxHit(NPC npc, NPCCombatDefinitions defs) {
        try {
            int baseMaxHit = defs.getMaxHit();
            
            // Apply Boss Balancer bonuses
            int[] bonuses = NPCBonuses.getBonuses(npc.getId());
            if (bonuses != null && bonuses.length > 3) {
                int magicBonus = bonuses[3]; // Magic attack bonus
                if (magicBonus > 0) {
                    double bonusMultiplier = 1.0 + (magicBonus * 0.0015); // 0.15% per bonus point
                    baseMaxHit = (int) (baseMaxHit * bonusMultiplier);
                }
            }
            
            return Math.max(baseMaxHit, 100); // Minimum for mage boss
        } catch (Exception e) {
            return 400; // Fallback
        }
    }
    
    /**
     * Calculate enhanced damage based on Boss Balancer stats
     */
    private int calculateEnhancedDamage(NPC npc, int baseMaxHit, double multiplier) {
        try {
            int enhancedMax = (int) (baseMaxHit * multiplier);
            return Utils.random(enhancedMax + 1);
        } catch (Exception e) {
            return Utils.random(200); // Safe fallback
        }
    }
    
    /**
     * Get enhanced heal amount based on Boss Balancer tier
     */
    private int getEnhancedHealAmount(NPC npc) {
        try {
            // Base heal of 3000, scale with max HP
            int maxHp = npc.getMaxHitpoints();
            int healAmount = Math.min(3000, maxHp / 3); // Heal 1/3 max HP, capped at 3000
            
            // Apply Boss Balancer scaling
            int[] bonuses = NPCBonuses.getBonuses(npc.getId());
            if (bonuses != null && bonuses.length >= 8) {
                int magicDefense = bonuses[8]; // Magic defense as indicator of tier
                if (magicDefense > 500) {
                    healAmount = (int) (healAmount * 1.5); // Higher tier = better heals
                }
            }
            
            return Math.max(healAmount, 1000); // Minimum 1000 heal
        } catch (Exception e) {
            return 3000; // Default fallback
        }
    }
    
    /**
     * Utility methods
     */
    private boolean isValidCombatState(NPC npc, Entity target, NPCCombatDefinitions defs) {
        return npc != null && target != null && defs != null && 
               !npc.isDead() && !npc.hasFinished() && 
               !target.isDead() && !target.hasFinished();
    }
    
    private void announceSpecialAttack(WorldTile center, String message) {
        for (Player player : World.getPlayers()) {
            if (player != null && player.withinDistance(center, 8)) {
                player.sendMessage("<col=ff6600>[Mage Alert] " + message);
            }
        }
    }
    
    private void announceToNearbyPlayers(NPC npc, String message) {
        for (Player player : World.getPlayers()) {
            if (player != null && player.withinDistance(npc, 10)) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public Object[] getKeys() {
        return new Object[] { 8335 };
    }
}