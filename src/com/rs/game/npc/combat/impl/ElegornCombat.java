package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Combat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.game.Hit;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;

/**
 * Enhanced Elegorn Combat System with Boss Balancer Integration
 * 
 * Features:
 * - Integrated with Boss Balancer 10-tier system (Hybrid Boss Type - Tier 7)
 * - Advanced boss guidance system with dragon combat education
 * - Enhanced multi-attack system with 7 distinct attack types
 * - Multi-phase combat with escalating draconic abilities
 * - Intelligent range-based attack selection with tactical decisions
 * - Dragon fire protection mechanics with proper damage scaling
 * - Balanced prayer drain system with strategic implications
 * - Poison mechanics with tier-scaled duration and damage
 * - Player-level scaling for balanced high-tier dragon experience
 * - Null-safe damage calculation system with comprehensive error handling
 * - Dragon-themed force talk messages with ancient dragon lore
 * - Performance tracking for multi-attack analysis and combat balancing
 * 
 * @author Zeus
 * @date May 31, 2025
 * @version 3.0 - Boss Balancer Integration with Advanced Dragon Combat System
 */
public class ElegornCombat extends CombatScript {
    
    // Boss Balancer Integration Constants
    private static final int ELEGORN_BOSS_TYPE = 4; // Hybrid Boss Type (all combat styles)
    private static final int ELEGORN_DEFAULT_TIER = 7; // Elite tier by default
    
    // Combat phase thresholds for the Ancient Dragon
    private static final double ANCIENT_FURY_THRESHOLD = 0.75; // 75% health - enhanced abilities
    private static final double DRACONIC_WRATH_THRESHOLD = 0.50; // 50% health - powerful dragon magic
    private static final double FINAL_RAGE_THRESHOLD = 0.25; // 25% health - desperate dragon fury
    
    // Attack selection probabilities for close range
    private static final int CLOSE_MAGE_CHANCE = 15; // 15% chance for magic at close range
    private static final int CLOSE_DRAGON_FIRE_CHANCE = 12; // 12% chance for dragon fire
    private static final int CLOSE_RANGE_CHANCE = 10; // 10% chance for range at close range
    private static final int CLOSE_POISON_CHANCE = 8; // 8% chance for poison
    private static final int CLOSE_ENHANCED_MELEE_CHANCE = 6; // 6% chance for AOE melee
    private static final int CLOSE_ENHANCED_MAGE_CHANCE = 5; // 5% chance for prayer drain magic
    
    // Attack selection probabilities for distant range
    private static final int DISTANT_RANGE_CHANCE = 20; // 20% chance for range at distance
    private static final int DISTANT_DRAGON_FIRE_CHANCE = 18; // 18% chance for dragon fire
    private static final int DISTANT_ENHANCED_MAGE_CHANCE = 15; // 15% chance for prayer drain magic
    private static final int DISTANT_ENHANCED_MELEE_CHANCE = 12; // 12% chance for AOE melee
    private static final int DISTANT_POISON_CHANCE = 10; // 10% chance for poison
    
    // Guidance system constants
    private static final int GUIDANCE_FREQUENCY = 3; // 1 in 3 chance for strategic hints
    private static final int HINT_COOLDOWN = 16000; // 16 seconds between hints
    private static final int DRAGON_WARNING_COOLDOWN = 25000; // 25 seconds between dragon warnings
    
    // Instance variables for combat tracking
    private long lastHintTime = 0;
    private long lastDragonWarningTime = 0;
    private boolean hasGivenOpeningAdvice = false;
    private boolean hasGivenFuryWarning = false;
    private boolean hasGivenWrathWarning = false;
    private boolean hasGivenRageWarning = false;
    private int dragonFireCount = 0;
    private int poisonAttackCount = 0;
    private int prayerDrainCount = 0;
    private int aoeAttackCount = 0;
    private int totalDamageDealt = 0;
    
    // Enhanced Dragon force talk messages with ancient dragon lore
    private static final String[] AWAKENING_MESSAGES = {
        "Mortal fools dare to challenge ancient power!",
        "I am Elegorn, bane of kingdoms!",
        "Your arrogance shall be your undoing!"
    };
    
    private static final String[] ANCIENT_FURY_MESSAGES = {
        "You have awakened my true wrath!",
        "Feel the power of ancient dragon magic!",
        "I have survived eons - you are nothing!"
    };
    
    private static final String[] DRACONIC_WRATH_MESSAGES = {
        "My fury burns hotter than dragon fire!",
        "I will not fall to mere mortals!",
        "The heavens themselves tremble before me!"
    };
    
    private static final String[] FINAL_RAGE_MESSAGES = {
        "I am eternal! You cannot destroy me!",
        "My rage will consume everything!",
        "I will drag you down with me!"
    };

    @Override
    public int attack(NPC npc, Entity target) {
        // Enhanced null safety checks
        if (npc == null || target == null) {
            return 5; // Default attack delay
        }
        
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) {
            return 5; // No combat definitions available
        }
        
        // Provide opening dragon strategy advice
        if (!hasGivenOpeningAdvice && target instanceof Player) {
            provideOpeningDragonStrategy((Player) target, npc, defs);
            hasGivenOpeningAdvice = true;
        }
        
        // Check combat phases and provide dragon warnings
        checkCombatPhases(npc, target, defs);
        
        // Dragon-themed force talk with ancient atmosphere
        performDragonForceTalk(npc, defs);
        
        // Enhanced attack selection based on range and phase
        performIntelligentDragonAttackSelection(npc, target, defs);
        
        // Provide strategic dragon guidance
        if (target instanceof Player) {
            provideDragonGuidance((Player) target, npc, defs);
        }
        
        return defs.getAttackDelay();
    }
    
    /**
     * Provide opening dragon strategy advice
     */
    private void provideOpeningDragonStrategy(Player player, NPC npc, NPCCombatDefinitions defs) {
        int elegornTier = determineElegornTier(npc, defs);
        
        player.getPackets().sendGameMessage("<col=FF4500>[Ancient Dragon]: Elegorn is a legendary ancient dragon with mastery over all combat forms!");
        player.getPackets().sendGameMessage("<col=FF4500>[Combat Analysis]: Tier " + elegornTier + " Hybrid Boss - Uses 7 distinct attack types!");
        player.getPackets().sendGameMessage("<col=00FFFF>[Critical Strategy]: Dragon fire protection is essential! Antifire potions and prayers reduce massive damage!");
        player.getPackets().sendGameMessage("<col=00FFFF>[Tactical Warning]: Watch for poison attacks, prayer drains, and devastating AOE abilities!");
    }
    
    /**
     * Check combat phases and provide dragon warnings
     */
    private void checkCombatPhases(NPC npc, Entity target, NPCCombatDefinitions defs) {
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        
        if (healthPercent <= FINAL_RAGE_THRESHOLD && !hasGivenRageWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FF0000>[FINAL RAGE]: Elegorn enters a state of ancient fury!");
            player.getPackets().sendGameMessage("<col=FF0000>[MAXIMUM DANGER]: All abilities at peak power - expect devastating attacks!");
            player.getPackets().sendGameMessage("<col=FF0000>[CRITICAL PHASE]: The ancient dragon fights for its eternal existence!");
            hasGivenRageWarning = true;
            
        } else if (healthPercent <= DRACONIC_WRATH_THRESHOLD && !hasGivenWrathWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FF8000>[Draconic Wrath]: Elegorn's ancient power intensifies!");
            player.getPackets().sendGameMessage("<col=FF8000>[Enhanced Threat]: More frequent dragon fire and enhanced magical abilities!");
            player.getPackets().sendGameMessage("<col=FF8000>[Strategic Update]: All attack types become more dangerous and frequent!");
            hasGivenWrathWarning = true;
            
        } else if (healthPercent <= ANCIENT_FURY_THRESHOLD && !hasGivenFuryWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FFFF00>[Ancient Fury]: The dragon's true power begins to awaken!");
            player.getPackets().sendGameMessage("<col=FFFF00>[Tactical Note]: Enhanced attack variety and increased damage output!");
            player.getPackets().sendGameMessage("<col=FFFF00>[Combat Advisory]: Prepare for more frequent special abilities!");
            hasGivenFuryWarning = true;
        }
    }
    
    /**
     * Perform dragon-themed force talk based on combat phase
     */
    private void performDragonForceTalk(NPC npc, NPCCombatDefinitions defs) {
        if (Utils.getRandom(12) != 0) return; // 1 in 12 chance for force talk
        
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        String[] messageArray;
        int soundBase;
        
        // Select messages and sounds based on current phase
        if (healthPercent <= FINAL_RAGE_THRESHOLD) {
            messageArray = FINAL_RAGE_MESSAGES;
            soundBase = 1300; // Furious, roaring dragon sounds
        } else if (healthPercent <= DRACONIC_WRATH_THRESHOLD) {
            messageArray = DRACONIC_WRATH_MESSAGES;
            soundBase = 1298;
        } else if (healthPercent <= ANCIENT_FURY_THRESHOLD) {
            messageArray = ANCIENT_FURY_MESSAGES;
            soundBase = 1296;
        } else {
            messageArray = AWAKENING_MESSAGES;
            soundBase = 1294;
        }
        
        String message = messageArray[Utils.getRandom(messageArray.length)];
        npc.setNextForceTalk(new ForceTalk(message));
        npc.playSound(soundBase + Utils.getRandom(2), 3); // Powerful dragon sound effects
    }
    
    /**
     * Perform intelligent dragon attack selection based on range and phase
     */
    private void performIntelligentDragonAttackSelection(NPC npc, Entity target, NPCCombatDefinitions defs) {
        // Determine if target is in close range
        boolean inCloseRange = npc.withinDistance(target, npc.getSize());
        
        if (inCloseRange) {
            performCloseRangeAttackSelection(npc, target, defs);
        } else {
            performDistantRangeAttackSelection(npc, target, defs);
        }
    }
    
    /**
     * Perform close range attack selection with weighted probabilities
     */
    private void performCloseRangeAttackSelection(NPC npc, Entity target, NPCCombatDefinitions defs) {
        int attackChoice = Utils.random(100);
        
        if (attackChoice < CLOSE_MAGE_CHANCE) {
            mageAttack(npc, target);
        } else if (attackChoice < CLOSE_MAGE_CHANCE + CLOSE_DRAGON_FIRE_CHANCE) {
            dragonFireAttack(npc, target);
            dragonFireCount++;
        } else if (attackChoice < CLOSE_MAGE_CHANCE + CLOSE_DRAGON_FIRE_CHANCE + CLOSE_RANGE_CHANCE) {
            rangeAttack(npc, target);
        } else if (attackChoice < CLOSE_MAGE_CHANCE + CLOSE_DRAGON_FIRE_CHANCE + CLOSE_RANGE_CHANCE + CLOSE_POISON_CHANCE) {
            poisonAttack(npc, target);
            poisonAttackCount++;
        } else if (attackChoice < CLOSE_MAGE_CHANCE + CLOSE_DRAGON_FIRE_CHANCE + CLOSE_RANGE_CHANCE + CLOSE_POISON_CHANCE + CLOSE_ENHANCED_MELEE_CHANCE) {
            meleeAttack2(npc, target);
            aoeAttackCount++;
        } else if (attackChoice < CLOSE_MAGE_CHANCE + CLOSE_DRAGON_FIRE_CHANCE + CLOSE_RANGE_CHANCE + CLOSE_POISON_CHANCE + CLOSE_ENHANCED_MELEE_CHANCE + CLOSE_ENHANCED_MAGE_CHANCE) {
            mageAttack2(npc, target);
            prayerDrainCount++;
        } else {
            meleeAttack(npc, target);
        }
    }
    
    /**
     * Perform distant range attack selection with different probabilities
     */
    private void performDistantRangeAttackSelection(NPC npc, Entity target, NPCCombatDefinitions defs) {
        int attackChoice = Utils.random(100);
        
        if (attackChoice < DISTANT_RANGE_CHANCE) {
            rangeAttack(npc, target);
        } else if (attackChoice < DISTANT_RANGE_CHANCE + DISTANT_DRAGON_FIRE_CHANCE) {
            dragonFireAttack(npc, target);
            dragonFireCount++;
        } else if (attackChoice < DISTANT_RANGE_CHANCE + DISTANT_DRAGON_FIRE_CHANCE + DISTANT_ENHANCED_MAGE_CHANCE) {
            mageAttack2(npc, target);
            prayerDrainCount++;
        } else if (attackChoice < DISTANT_RANGE_CHANCE + DISTANT_DRAGON_FIRE_CHANCE + DISTANT_ENHANCED_MAGE_CHANCE + DISTANT_ENHANCED_MELEE_CHANCE) {
            meleeAttack2(npc, target);
            aoeAttackCount++;
        } else if (attackChoice < DISTANT_RANGE_CHANCE + DISTANT_DRAGON_FIRE_CHANCE + DISTANT_ENHANCED_MAGE_CHANCE + DISTANT_ENHANCED_MELEE_CHANCE + DISTANT_POISON_CHANCE) {
            poisonAttack(npc, target);
            poisonAttackCount++;
        } else {
            mageAttack(npc, target);
        }
    }
    
    /**
     * Enhanced range attack with Boss Balancer scaling
     */
    public void rangeAttack(NPC npc, Entity target) {
        npc.setNextAnimation(new Animation(14244));
        World.sendProjectile(npc, target, 16, 28, 16, 35, 20, 16, 0);
        
        // Get Boss Balancer stats
        int elegornTier = determineElegornTier(npc, npc.getCombatDefinitions());
        int baseMaxHit = getBaseMaxHit(npc, npc.getCombatDefinitions());
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, elegornTier, false); // Range attack (hybrid)
        
        // Apply player scaling if target is a player
        if (target instanceof Player) {
            tierScaledMaxHit = applyPlayerLevelScaling(tierScaledMaxHit, (Player) target, elegornTier);
        }
        
        // Enhanced range damage with proper scaling
        int damage = getRandomMaxHit(npc, tierScaledMaxHit, NPCCombatDefinitions.RANGE, target);
        totalDamageDealt += damage;
        delayHit(npc, 1, target, getRangeHit(npc, damage));
    }
    
    /**
     * Enhanced AOE magic attack with Boss Balancer scaling
     */
    public void mageAttack(NPC npc, Entity target) {
        npc.setNextAnimation(new Animation(14245));
        World.sendGraphics(npc, new Graphics(7011), npc);
        aoeAttackCount++;
        
        // Get Boss Balancer stats
        int elegornTier = determineElegornTier(npc, npc.getCombatDefinitions());
        int baseMaxHit = getBaseMaxHit(npc, npc.getCombatDefinitions());
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, elegornTier, true); // Magic attack
        
        int targetsHit = 0;
        for (Entity t : npc.getPossibleTargets()) {
            targetsHit++;
        }
        
        // AOE damage reduction
        double aoeMultiplier = calculateAOEDamageMultiplier(targetsHit);
        int aoeDamage = (int)(tierScaledMaxHit * aoeMultiplier);
        
        for (Entity t : npc.getPossibleTargets()) {
            if (t instanceof Player) {
                aoeDamage = applyPlayerLevelScaling(aoeDamage, (Player) t, elegornTier);
            }
            
            int damage = getRandomMaxHit(npc, aoeDamage, NPCCombatDefinitions.MAGE, t);
            totalDamageDealt += damage;
            delayHit(npc, 1, t, getMagicHit(npc, damage));
        }
        
        // Provide AOE warning
        if (target instanceof Player && targetsHit > 1) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FF4500>[Dragon Magic]: Elegorn's celestial magic affects all nearby targets!");
        }
    }
    
    /**
     * Enhanced prayer drain magic attack with balanced scaling
     */
    public void mageAttack2(NPC npc, Entity target) {
        npc.setNextAnimation(new Animation(26543));
        prayerDrainCount++;
        
        // Get Boss Balancer stats
        int elegornTier = determineElegornTier(npc, npc.getCombatDefinitions());
        int baseMaxHit = getBaseMaxHit(npc, npc.getCombatDefinitions());
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, elegornTier, true); // Magic attack
        
        // Prayer drain attack does 70% damage but drains prayer
        int drainDamage = (int)(tierScaledMaxHit * 0.70);
        
        for (Entity t : npc.getPossibleTargets()) {
            World.sendGraphics(npc, new Graphics(7010), t);
            
            if (t instanceof Player) {
                Player player = (Player) t;
                drainDamage = applyPlayerLevelScaling(drainDamage, player, elegornTier);
                
                player.getPackets().sendGameMessage("<col=8B008B>[Celestial Drain]: Your prayer has been drained by the celestial rain.");
                
                // Balanced prayer drain based on tier
                int prayerDrain = calculateBalancedPrayerDrain(player, elegornTier);
                player.getPrayer().drainPrayer(prayerDrain);
            }
            
            int damage = getRandomMaxHit(npc, drainDamage, NPCCombatDefinitions.MAGE, t);
            totalDamageDealt += damage;
            delayHit(npc, 1, t, getMagicHit(npc, damage));
        }
    }
    
    /**
     * Calculate balanced prayer drain based on tier and player level
     */
    private int calculateBalancedPrayerDrain(Player player, int tier) {
        int maxPrayer = player.getSkills().getLevelForXp(Skills.PRAYER);
        int currentPrayer = player.getPrayer().getPrayerpoints();
        
        // Base drain: 15-30% of max prayer based on tier
        double drainPercent = 0.15 + (tier - 1) * 0.025; // 15% + 2.5% per tier above 1
        drainPercent = Math.min(drainPercent, 0.30); // Cap at 30%
        
        int drainAmount = (int)(maxPrayer * drainPercent);
        
        // Ensure reasonable drain amount
        drainAmount = Math.max(10, Math.min(drainAmount, currentPrayer));
        
        return drainAmount;
    }
    
    /**
     * Enhanced poison attack with tier-scaled effects
     */
    public void poisonAttack(NPC npc, Entity target) {
        final Player player = target instanceof Player ? (Player) target : null;
        if (player != null) {
            npc.setNextAnimation(new Animation(14244));
            World.sendProjectile(npc, target, 3436, 60, 16, 65, 35, 16, 0);
            poisonAttackCount++;
            
            WorldTasksManager.schedule(new WorldTask() {
                @Override
                public void run() {
                    // Get Boss Balancer stats
                    int elegornTier = determineElegornTier(npc, npc.getCombatDefinitions());
                    int baseMaxHit = getBaseMaxHit(npc, npc.getCombatDefinitions());
                    int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, elegornTier, true); // Magic-based poison
                    
                    // Poison attack does 85% base damage
                    int poisonDamage = (int)(tierScaledMaxHit * 0.85);
                    poisonDamage = applyPlayerLevelScaling(poisonDamage, player, elegornTier);
                    
                    player.getPackets().sendGameMessage("<col=00FF00>[Dragon Poison]: You are hit by the dragon's poisonous breath!");
                    
                    int damage = getRandomMaxHit(npc, poisonDamage, NPCCombatDefinitions.MAGE, target);
                    totalDamageDealt += damage;
                    delayHit(npc, 0, target, getMagicHit(npc, damage));
                    
                    player.setNextGraphics(new Graphics(3437, 50, 0));
                    
                    // Tier-scaled poison duration
                    int poisonStrength = Math.max(100, elegornTier * 25); // Minimum 100, scales with tier
                    player.getPoison().makePoisoned(poisonStrength);
                    
                    stop();
                }
            }, 0);
        }
    }
    
    /**
     * Enhanced melee attack with Boss Balancer scaling
     */
    public void meleeAttack(NPC npc, Entity target) {
        npc.setNextAnimation(new Animation(12252));
        
        // Get Boss Balancer stats
        int elegornTier = determineElegornTier(npc, npc.getCombatDefinitions());
        int baseMaxHit = getBaseMaxHit(npc, npc.getCombatDefinitions());
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, elegornTier, false); // Melee attack
        
        // Apply player scaling if target is a player
        if (target instanceof Player) {
            tierScaledMaxHit = applyPlayerLevelScaling(tierScaledMaxHit, (Player) target, elegornTier);
        }
        
        int damage = getRandomMaxHit(npc, tierScaledMaxHit, NPCCombatDefinitions.MELEE, target);
        totalDamageDealt += damage;
        delayHit(npc, 0, target, getMeleeHit(npc, damage));
    }
    
    /**
     * Enhanced AOE melee attack with Boss Balancer scaling
     */
    public void meleeAttack2(NPC npc, Entity target) {
        npc.setNextAnimation(new Animation(26528));
        World.sendGraphics(npc, new Graphics(7048), npc);
        aoeAttackCount++;
        
        // Get Boss Balancer stats
        int elegornTier = determineElegornTier(npc, npc.getCombatDefinitions());
        int baseMaxHit = getBaseMaxHit(npc, npc.getCombatDefinitions());
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, elegornTier, false); // Melee attack
        
        int targetsHit = 0;
        for (Entity t : npc.getPossibleTargets()) {
            targetsHit++;
        }
        
        // AOE melee damage reduction
        double aoeMultiplier = calculateAOEDamageMultiplier(targetsHit);
        int aoeDamage = (int)(tierScaledMaxHit * aoeMultiplier);
        
        for (Entity t : npc.getPossibleTargets()) {
            if (t instanceof Player) {
                aoeDamage = applyPlayerLevelScaling(aoeDamage, (Player) t, elegornTier);
            }
            
            int damage = getRandomMaxHit(npc, aoeDamage, NPCCombatDefinitions.MELEE, t);
            totalDamageDealt += damage;
            delayHit(npc, 0, t, getMeleeHit(npc, damage));
        }
        
        // Provide AOE warning
        if (target instanceof Player && targetsHit > 1) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FF4500>[Dragon Fury]: Elegorn's rage affects all nearby enemies!");
        }
    }
    
    /**
     * Enhanced dragon fire attack with proper protection mechanics
     */
    public void dragonFireAttack(NPC npc, Entity target) {
        final Player player = target instanceof Player ? (Player) target : null;
        dragonFireCount++;
        
        // Get Boss Balancer stats
        int elegornTier = determineElegornTier(npc, npc.getCombatDefinitions());
        int baseMaxHit = getBaseMaxHit(npc, npc.getCombatDefinitions());
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, elegornTier, true); // Magic-based dragon fire
        
        // Dragon fire base damage (120% of tier-scaled max hit)
        int damage = (int)(tierScaledMaxHit * 1.20);
        
        if (target instanceof Player) {
            damage = applyPlayerLevelScaling(damage, player, elegornTier);
            
            String message = Combat.getProtectMessage(player);
            if (message != null) {
                player.sendMessage(message, true);
                if (message.contains("fully"))
                    damage *= 0.05; // 5% damage with full protection
                else if (message.contains("most"))
                    damage *= 0.1; // 10% damage with most protection
                else if (message.contains("some"))
                    damage *= 0.2; // 20% damage with some protection
            }
            
            if (damage > 0)
                player.sendMessage("<col=FF0000>[Dragon Fire]: You are hit by the dragon's fiery breath!", true);
        }
        
        npc.setNextAnimation(new Animation(26537));
        World.sendProjectile(npc, target, 438, 56, 16, 35, 20, 16, 0);
        totalDamageDealt += damage;
        delayHit(npc, 1, target, getRegularHit(npc, damage));
        
        // Provide dragon fire warning
        if (player != null && shouldGiveDragonWarning()) {
            player.getPackets().sendGameMessage("<col=FF0000>[Dragon Fire Warning]: Use antifire potions and prayers for protection!");
        }
    }
    
    /**
     * Calculate AOE damage multiplier based on number of targets
     */
    private double calculateAOEDamageMultiplier(int targetsHit) {
        switch (targetsHit) {
            case 0:
            case 1: return 0.90; // 90% damage for single target AOE
            case 2: return 0.75; // 75% damage for 2 targets
            case 3: return 0.65; // 65% damage for 3 targets
            case 4: return 0.55; // 55% damage for 4 targets
            default: return 0.45; // 45% damage for 5+ targets
        }
    }
    
    /**
     * Determine Elegorn's tier based on Boss Balancer system
     */
    private int determineElegornTier(NPC npc, NPCCombatDefinitions defs) {
        try {
            int hp = defs.getHitpoints();
            int maxHit = defs.getMaxHit();
            
            // Estimate tier based on Boss Balancer HP/damage ranges for Hybrid boss
            if (hp >= 15000 && hp <= 25000 && maxHit >= 150 && maxHit <= 260) {
                return 7; // Elite tier
            } else if (hp >= 20000 && hp <= 35000 && maxHit >= 200 && maxHit <= 350) {
                return 8; // Legendary tier
            } else if (hp >= 10000 && hp <= 17000 && maxHit >= 105 && maxHit <= 185) {
                return 6; // Master tier
            }
            
            return ELEGORN_DEFAULT_TIER; // Default to Elite tier
        } catch (Exception e) {
            return ELEGORN_DEFAULT_TIER;
        }
    }
    
    /**
     * Get base max hit safely (NULL SAFE)
     */
    private int getBaseMaxHit(NPC npc, NPCCombatDefinitions defs) {
        try {
            int maxHit = defs.getMaxHit();
            return maxHit > 0 ? maxHit : 180; // Default Elegorn damage if invalid
        } catch (Exception e) {
            return 180; // Fallback Elegorn damage
        }
    }
    
    /**
     * Apply Boss Balancer tier scaling for hybrid boss
     */
    private int applyBossTierScaling(int baseMaxHit, int tier, boolean isMagicAttack) {
        // Boss Balancer tier scaling: 15% increase per tier above 1
        double tierMultiplier = 1.0 + (tier - 1) * 0.15;
        
        // Hybrid boss type modifier - balanced damage for all attack types
        double typeModifier = 1.0; // Standard damage for hybrid
        
        // Slight magic preference for dragon (ancient magical creature)
        if (isMagicAttack) {
            typeModifier = 1.08; // 8% bonus for magic attacks
        }
        
        return (int) (baseMaxHit * tierMultiplier * typeModifier);
    }
    
    /**
     * Apply player level scaling for balanced high-tier dragon experience
     */
    private int applyPlayerLevelScaling(int damage, Player player, int tier) {
        int playerCombatLevel = player.getSkills().getCombatLevel();
        int recommendedLevel = tier * 15 + 25; // Tier 7 = level 130 recommended
        
        // Scale damage based on player level vs recommended
        if (playerCombatLevel < recommendedLevel) {
            double scaleFactor = (double) playerCombatLevel / recommendedLevel;
            scaleFactor = Math.max(0.30, scaleFactor); // Minimum 30% damage
            damage = (int) (damage * scaleFactor);
        }
        
        // Ensure damage is reasonable for Elegorn (high-tier dragon)
        damage = Math.max(25, Math.min(damage, 350)); // Cap between 25-350
        
        return damage;
    }
    
    /**
     * Provide strategic dragon guidance based on combat performance
     */
    private void provideDragonGuidance(Player player, NPC npc, NPCCombatDefinitions defs) {
        if (!shouldGiveHint()) return;
        
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        
        // Dragon fire frequency guidance
        if (dragonFireCount >= 3) {
            player.getPackets().sendGameMessage("<col=FF0000>[Dragon Fire Analysis]: Multiple fire attacks detected! Ensure antifire protection is active!");
            dragonFireCount = 0;
            return;
        }
        
        // Poison attack guidance
        if (poisonAttackCount >= 2) {
            player.getPackets().sendGameMessage("<col=00FF00>[Poison Warfare]: Multiple poison attacks used. Consider antidote potions and poison protection!");
            poisonAttackCount = 0;
            return;
        }
        
        // Prayer drain guidance
        if (prayerDrainCount >= 2) {
            player.getPackets().sendGameMessage("<col=8B008B>[Celestial Drain]: Prayer being drained frequently. Monitor prayer levels and consider prayer restoration!");
            prayerDrainCount = 0;
            return;
        }
        
        // AOE attack guidance
        if (aoeAttackCount >= 3) {
            player.getPackets().sendGameMessage("<col=FFFF00>[AOE Tactics]: Multiple area attacks detected. Spread out in group combat to minimize damage!");
            aoeAttackCount = 0;
            return;
        }
        
        // Phase-specific dragon guidance
        if (healthPercent > 0.75) {
            player.getPackets().sendGameMessage("<col=00FFFF>[Dragon Strategy]: Elegorn uses 7 attack types. Adapt your protection and positioning accordingly!");
        } else if (healthPercent > 0.50) {
            player.getPackets().sendGameMessage("<col=FFFF00>[Ancient Fury]: Enhanced attack power and frequency. All protection methods recommended!");
        } else if (healthPercent > 0.25) {
            player.getPackets().sendGameMessage("<col=FF8000>[Draconic Wrath]: Maximum dragon power activated. Expect devastating abilities!");
        } else {
            player.getPackets().sendGameMessage("<col=FF0000>[Final Rage]: Elegorn fights with ancient fury! Ultimate vigilance required!");
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
     * Check if should give dragon fire warning (with cooldown)
     */
    private boolean shouldGiveDragonWarning() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDragonWarningTime >= DRAGON_WARNING_COOLDOWN) {
            lastDragonWarningTime = currentTime;
            return true;
        }
        return false;
    }
    
    /**
     * Debug method for testing damage scaling and boss balancer integration
     */
    public String getDamageScalingInfo(int combatLevel, boolean isMagic) {
        int tier = ELEGORN_DEFAULT_TIER;
        int baseMaxHit = 180;
        int tierScaled = applyBossTierScaling(baseMaxHit, tier, isMagic);
        String attackType = isMagic ? "Magic" : "Physical";
        
        return String.format("Elegorn Tier: %d, Base: %d, %s Scaled: %d", 
                           tier, baseMaxHit, attackType, tierScaled);
    }
    
    /**
     * Get combat statistics for dragon combat analysis
     */
    public String getCombatStats() {
        return String.format("Dragon Fire: %d, Poison: %d, Prayer Drains: %d, AOE: %d, Total Damage: %d", 
                           dragonFireCount, poisonAttackCount, prayerDrainCount, aoeAttackCount, totalDamageDealt);
    }

    @Override
    public Object[] getKeys() {
        return new Object[] { 25695 }; // Elegorn NPC ID
    }
}