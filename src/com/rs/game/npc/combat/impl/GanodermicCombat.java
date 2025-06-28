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
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.game.Hit;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;

/**
 * Enhanced Ganodermics Combat System with Boss Balancer Integration
 * 
 * Features:
 * - Integrated with Boss Balancer 10-tier system (Magic Boss Type - Tier 5)
 * - Advanced boss guidance system with fungal combat education
 * - Enhanced spore-based magic attacks with tactical variety
 * - Multi-phase combat with escalating fungal abilities
 * - Intelligent spore cloud mechanics with area denial
 * - Advanced regeneration system with strategic healing patterns
 * - Spore infection mechanics causing damage over time
 * - Fungal growth effects affecting player stats and abilities
 * - Player-level scaling for balanced fungal encounter experience
 * - Null-safe damage calculation system with comprehensive error handling
 * - Fungal-themed force talk messages with ancient spore lore
 * - Performance tracking for spore mechanics and regeneration analysis
 * 
 * @author Zeus
 * @date June 04, 2025
 * @version 3.1 - Fixed ArrayIndexOutOfBoundsException and improved memory management
 */
public class GanodermicCombat extends CombatScript {
    
    // Constants to avoid magic numbers
    private static final int GANODERMICS_ID_1 = 14696;
    private static final int GANODERMICS_ID_2 = 14697;
    private static final int SPORE_ANIMATION = 15470;
    private static final int DEFAULT_FALLBACK_DAMAGE = 80;
    private static final int CLEANUP_FREQUENCY = 40; // 2.5% chance for cleanup
    
    // Graphics constants
    private static final int SPORE_GRAPHICS = 2034;
    private static final int SPORE_PROJECTILE = 2034;
    private static final int SPORE_IMPACT_GRAPHICS = 2036;
    private static final int TOXIC_CLOUD_GRAPHICS = 2037;
    private static final int TOXIC_EFFECT_GRAPHICS = 2038;
    private static final int LINGERING_TOXIC_GRAPHICS = 2039;
    private static final int REGEN_GRAPHICS = 2040;
    private static final int HEALING_BURST_GRAPHICS = 2041;
    private static final int INFECTION_PROJECTILE = 2042;
    private static final int INFECTION_DAMAGE_GRAPHICS = 2043;
    private static final int INFECTION_IMPACT_GRAPHICS = 2044;
    private static final int GROWTH_GRAPHICS = 2045;
    private static final int GROWTH_PROJECTILE = 2046;
    private static final int GROWTH_IMPACT_GRAPHICS = 2047;
    private static final int REGEN_EFFECT_GRAPHICS = 2035;
    
    // Sound constants
    private static final int BASE_SOUND = 1400;
    private static final int GROWTH_SOUND = 1402;
    private static final int BLOOM_SOUND = 1404;
    private static final int FINAL_SOUND = 1406;
    private static final int HEALING_SOUND = 1408;
    
    // Boss Balancer Integration Constants
    private static final int GANODERMICS_BOSS_TYPE = 3; // Magic Boss Type (fungal magic specialist)
    private static final int GANODERMICS_DEFAULT_TIER = 5; // Expert tier by default
    
    // Combat phase thresholds for the Ganodermics
    private static final double SPORE_GROWTH_THRESHOLD = 0.75; // 75% health - enhanced spore abilities
    private static final double TOXIC_BLOOM_THRESHOLD = 0.50; // 50% health - frequent toxic attacks
    private static final double FINAL_SPORE_THRESHOLD = 0.25; // 25% health - desperate regeneration
    
    // Attack probabilities and mechanics
    private static final int TOXIC_CLOUD_CHANCE = 15; // 1 in 15 chance for toxic spore cloud
    private static final int ENHANCED_TOXIC_CHANCE = 10; // 1 in 10 in toxic bloom phase
    private static final int REGENERATION_CHANCE = 20; // 1 in 20 chance for regenerative spores
    private static final int ENHANCED_REGEN_CHANCE = 12; // 1 in 12 in final phase
    private static final int SPORE_INFECTION_CHANCE = 18; // 1 in 18 chance for infection
    private static final int FUNGAL_GROWTH_CHANCE = 25; // 1 in 25 chance for stat drain
    
    // Regeneration mechanics
    private static final double BASE_REGEN_RATIO = 0.08; // 8% of max health
    private static final double ENHANCED_REGEN_RATIO = 0.12; // 12% in final phase
    private static final double DESPERATE_REGEN_RATIO = 0.18; // 18% when under 25% health
    
    // Guidance system constants
    private static final int GUIDANCE_FREQUENCY = 4; // 1 in 4 chance for strategic hints
    private static final long HINT_COOLDOWN = 13000L; // 13 seconds between hints
    private static final long SPORE_WARNING_COOLDOWN = 20000L; // 20 seconds between spore warnings
    private static final long CLEANUP_MAX_AGE = 600000L; // 10 minutes for cleanup
    
    // Instance variables for combat tracking - WILL BE CLEANED UP
    private long lastHintTime = 0L;
    private long lastSporeWarningTime = 0L;
    private boolean hasGivenOpeningAdvice = false;
    private boolean hasGivenGrowthWarning = false;
    private boolean hasGivenBloomWarning = false;
    private boolean hasGivenFinalWarning = false;
    private int toxicCloudCount = 0;
    private int regenerationCount = 0;
    private int sporeInfectionCount = 0;
    private int fungalGrowthCount = 0;
    private int totalDamageHealed = 0;
    private long lastRegenerationTime = 0L;
    
    // Enhanced Ganodermics force talk messages with fungal theme - FIXED ARRAY BOUNDS
    private static final String[] AWAKENING_MESSAGES = {
        "The ancient spores awaken to your presence...",
        "You disturb the fungal consciousness!",
        "Mortals... you shall feed the spore network!",
        "The fungal network stirs with ancient power!"
    };
    
    private static final String[] SPORE_GROWTH_MESSAGES = {
        "The spores spread and multiply!",
        "Our fungal network grows stronger!",
        "You cannot stop the spore proliferation!",
        "Feel the growth of eternal fungi!"
    };
    
    private static final String[] TOXIC_BLOOM_MESSAGES = {
        "Breathe deep the toxic spores!",
        "The bloom of death spreads!",
        "Our toxins shall overwhelm you!",
        "Witness the poisonous flowering!"
    };
    
    private static final String[] FINAL_SPORE_MESSAGES = {
        "The spore network will not be destroyed!",
        "We regenerate... we endure... we persist!",
        "Death only feeds our fungal growth!",
        "From decay comes eternal life!"
    };

    @Override
    public int attack(NPC npc, Entity target) {
        // Enhanced null safety checks
        if (npc == null || target == null) {
            return 4; // Default attack delay
        }
        
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) {
            return 4; // No combat definitions available
        }
        
        // Periodic cleanup to prevent memory leaks
        performPeriodicCleanup();
        
        // Provide opening fungal strategy advice
        if (!hasGivenOpeningAdvice && target instanceof Player) {
            provideOpeningFungalStrategy((Player) target, npc, defs);
            hasGivenOpeningAdvice = true;
        }
        
        // Check combat phases and provide spore warnings
        checkCombatPhases(npc, target, defs);
        
        // Handle passive regeneration mechanics
        handlePassiveRegeneration(npc, defs);
        
        // Fungal-themed force talk with spore atmosphere - FIXED
        performFungalForceTalk(npc, defs);
        
        // Enhanced attack selection with spore mechanics
        performIntelligentFungalAttackSelection(npc, target, defs);
        
        // Provide strategic fungal guidance
        if (target instanceof Player) {
            provideFungalGuidance((Player) target, npc, defs);
        }
        
        return defs.getAttackDelay();
    }
    
    /**
     * Perform periodic cleanup to prevent memory leaks
     */
    private void performPeriodicCleanup() {
        if (Utils.getRandom(CLEANUP_FREQUENCY) == 0) { // 2.5% chance for cleanup
            // Reset counters to prevent infinite growth
            if (toxicCloudCount > 20) toxicCloudCount = 0;
            if (regenerationCount > 20) regenerationCount = 0;
            if (sporeInfectionCount > 20) sporeInfectionCount = 0;
            if (fungalGrowthCount > 20) fungalGrowthCount = 0;
            if (totalDamageHealed > 50000) totalDamageHealed = 0;
            
            // Reset cooldowns if they're too old (over 10 minutes)
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastHintTime > CLEANUP_MAX_AGE) lastHintTime = 0L;
            if (currentTime - lastSporeWarningTime > CLEANUP_MAX_AGE) lastSporeWarningTime = 0L;
            if (currentTime - lastRegenerationTime > CLEANUP_MAX_AGE) lastRegenerationTime = 0L;
        }
    }
    
    /**
     * Provide opening fungal strategy advice
     */
    private void provideOpeningFungalStrategy(Player player, NPC npc, NPCCombatDefinitions defs) {
        int ganodermicsTier = determineGanodermicsTier(npc, defs);
        
        player.getPackets().sendGameMessage("<col=8FBC8F>[Fungal Knowledge]: Ganodermics are ancient fungal beings with powerful spore-based magic!");
        player.getPackets().sendGameMessage("<col=8FBC8F>[Combat Analysis]: Tier " + ganodermicsTier + " Magic Boss - Specializes in toxic spores and regeneration!");
        player.getPackets().sendGameMessage("<col=00FFFF>[Critical Strategy]: Watch for toxic spore clouds and interrupt regeneration abilities!");
        player.getPackets().sendGameMessage("<col=00FFFF>[Tactical Warning]: Spore infections cause damage over time - consider antidote protection!");
    }
    
    /**
     * Check combat phases and provide spore warnings
     */
    private void checkCombatPhases(NPC npc, Entity target, NPCCombatDefinitions defs) {
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        
        if (healthPercent <= FINAL_SPORE_THRESHOLD && !hasGivenFinalWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FF0000>[FINAL SPORE]: The Ganodermics enters desperate regeneration mode!");
            player.getPackets().sendGameMessage("<col=FF0000>[CRITICAL PHASE]: Maximum regeneration attempts and toxic spore proliferation!");
            player.getPackets().sendGameMessage("<col=FF0000>[URGENT STRATEGY]: Focus on burst damage to overwhelm regeneration!");
            hasGivenFinalWarning = true;
            
        } else if (healthPercent <= TOXIC_BLOOM_THRESHOLD && !hasGivenBloomWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FF8000>[Toxic Bloom]: The Ganodermics' toxic abilities intensify!");
            player.getPackets().sendGameMessage("<col=FF8000>[Enhanced Threat]: More frequent toxic spore clouds and infection attacks!");
            player.getPackets().sendGameMessage("<col=FF8000>[Strategic Update]: Antidote potions and area awareness critical!");
            hasGivenBloomWarning = true;
            
        } else if (healthPercent <= SPORE_GROWTH_THRESHOLD && !hasGivenGrowthWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FFFF00>[Spore Growth]: The fungal network's power increases!");
            player.getPackets().sendGameMessage("<col=FFFF00>[Enhanced Abilities]: Improved spore attacks and fungal growth effects!");
            player.getPackets().sendGameMessage("<col=FFFF00>[Combat Advisory]: Monitor your stats for fungal growth interference!");
            hasGivenGrowthWarning = true;
        }
    }
    
    /**
     * Handle passive regeneration mechanics
     */
    private void handlePassiveRegeneration(NPC npc, NPCCombatDefinitions defs) {
        long currentTime = System.currentTimeMillis();
        
        // Passive regeneration every 8-12 seconds
        if (currentTime - lastRegenerationTime >= 8000L + Utils.getRandom(4000)) {
            double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
            
            // Determine regeneration amount based on phase
            double regenRatio;
            if (healthPercent <= FINAL_SPORE_THRESHOLD) {
                regenRatio = DESPERATE_REGEN_RATIO;
            } else if (healthPercent <= TOXIC_BLOOM_THRESHOLD) {
                regenRatio = ENHANCED_REGEN_RATIO;
            } else {
                regenRatio = BASE_REGEN_RATIO;
            }
            
            int maxHealth = defs.getHitpoints();
            int regenAmount = (int)(maxHealth * regenRatio);
            
            // Cap regeneration to not exceed max health
            int currentHealth = npc.getHitpoints();
            regenAmount = Math.min(regenAmount, maxHealth - currentHealth);
            
            if (regenAmount > 0) {
                npc.heal(regenAmount);
                totalDamageHealed += regenAmount;
                regenerationCount++;
                
                // Visual regeneration effect
                npc.setNextGraphics(new Graphics(REGEN_EFFECT_GRAPHICS));
                npc.playSound(BASE_SOUND, 2); // Organic healing sound
                
                lastRegenerationTime = currentTime;
            }
        }
    }
    
    /**
     * Perform fungal-themed force talk based on combat phase - FIXED BOUNDS ERROR
     */
    private void performFungalForceTalk(NPC npc, NPCCombatDefinitions defs) {
        if (Utils.getRandom(20) != 0) return; // 1 in 20 chance for force talk
        
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        String[] messageArray;
        int soundBase;
        
        // Select messages and sounds based on current phase
        if (healthPercent <= FINAL_SPORE_THRESHOLD) {
            messageArray = FINAL_SPORE_MESSAGES;
            soundBase = FINAL_SOUND; // Desperate, organic sounds
        } else if (healthPercent <= TOXIC_BLOOM_THRESHOLD) {
            messageArray = TOXIC_BLOOM_MESSAGES;
            soundBase = BLOOM_SOUND;
        } else if (healthPercent <= SPORE_GROWTH_THRESHOLD) {
            messageArray = SPORE_GROWTH_MESSAGES;
            soundBase = GROWTH_SOUND;
        } else {
            messageArray = AWAKENING_MESSAGES;
            soundBase = BASE_SOUND;
        }
        
        // FIXED: Proper bounds checking
        if (messageArray == null || messageArray.length == 0) {
            return; // Safety check
        }
        
        String message = messageArray[Utils.getRandom(messageArray.length)];
        npc.setNextForceTalk(new ForceTalk(message));
        
        // FIXED: Safe sound playing with bounds check
        try {
            int soundVariation = Utils.getRandom(2); // 0 or 1
            npc.playSound(soundBase + soundVariation, 2);
        } catch (Exception e) {
            // Fallback to base sound if there's an issue
            npc.playSound(soundBase, 2);
        }
    }
    
    /**
     * Perform intelligent fungal attack selection with spore mechanics
     */
    private void performIntelligentFungalAttackSelection(NPC npc, Entity target, NPCCombatDefinitions defs) {
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        
        // Toxic spore cloud chance check
        int toxicChance = healthPercent <= TOXIC_BLOOM_THRESHOLD ? ENHANCED_TOXIC_CHANCE : TOXIC_CLOUD_CHANCE;
        if (Utils.getRandom(toxicChance) == 0) {
            performToxicSporeCloud(npc, target, defs);
            return;
        }
        
        // Regenerative spores chance check
        int regenChance = healthPercent <= FINAL_SPORE_THRESHOLD ? ENHANCED_REGEN_CHANCE : REGENERATION_CHANCE;
        if (Utils.getRandom(regenChance) == 0) {
            performRegenerativeSpores(npc, target, defs);
            return;
        }
        
        // Spore infection chance check
        if (Utils.getRandom(SPORE_INFECTION_CHANCE) == 0) {
            performSporeInfection(npc, target, defs);
            return;
        }
        
        // Fungal growth chance check
        if (Utils.getRandom(FUNGAL_GROWTH_CHANCE) == 0) {
            performFungalGrowth(npc, target, defs);
            return;
        }
        
        // Standard enhanced spore magic attack
        performEnhancedSporeAttack(npc, target, defs);
    }
    
    /**
     * Perform enhanced spore magic attack with Boss Balancer scaling
     */
    private void performEnhancedSporeAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
        npc.setNextAnimation(new Animation(SPORE_ANIMATION));
        npc.setNextGraphics(new Graphics(SPORE_GRAPHICS));
        World.sendProjectile(npc, target, SPORE_PROJECTILE, 10, 18, 50, 50, 0, 0);
        
        // Get Boss Balancer stats
        int ganodermicsTier = determineGanodermicsTier(npc, defs);
        int baseMaxHit = getBaseMaxHit(npc, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, ganodermicsTier, true); // Magic attack
        
        // Apply player scaling if target is a player
        if (target instanceof Player) {
            tierScaledMaxHit = applyPlayerLevelScaling(tierScaledMaxHit, (Player) target, ganodermicsTier);
        }
        
        int damage = calculateSafeMagicDamage(npc, tierScaledMaxHit);
        delayHit(npc, 1, target, getMagicHit(npc, damage));
        
        // Enhanced spore impact effect
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                target.setNextGraphics(new Graphics(SPORE_IMPACT_GRAPHICS));
                this.stop();
            }
        }, 1);
    }
    
    /**
     * Perform toxic spore cloud AOE attack
     */
    private void performToxicSporeCloud(NPC npc, Entity target, NPCCombatDefinitions defs) {
        toxicCloudCount++;
        
        npc.setNextAnimation(new Animation(SPORE_ANIMATION));
        npc.setNextGraphics(new Graphics(TOXIC_CLOUD_GRAPHICS));
        
        // Get Boss Balancer stats for toxic cloud
        int ganodermicsTier = determineGanodermicsTier(npc, defs);
        int baseMaxHit = getBaseMaxHit(npc, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, ganodermicsTier, true);
        
        // Toxic cloud does 70% damage to all nearby targets
        int toxicDamage = (int)(tierScaledMaxHit * 0.70);
        int targetsHit = 0;
        
        // Hit all entities within 3 tiles
        for (Entity entity : npc.getPossibleTargets()) {
            if (entity.withinDistance(npc, 3)) {
                targetsHit++;
                
                int entityDamage = toxicDamage;
                if (entity instanceof Player) {
                    entityDamage = applyPlayerLevelScaling(toxicDamage, (Player) entity, ganodermicsTier);
                }
                
                int damage = calculateSafeMagicDamage(npc, entityDamage);
                delayHit(npc, 2, entity, getMagicHit(npc, damage));
                
                // Toxic effect on target
                entity.setNextGraphics(new Graphics(TOXIC_EFFECT_GRAPHICS));
                
                // Apply poison effect to players
                if (entity instanceof Player) {
                    Player player = (Player) entity;
                    int poisonStrength = Math.max(80, ganodermicsTier * 15); // Tier-scaled poison
                    player.getPoison().makePoisoned(poisonStrength);
                    player.getPackets().sendGameMessage("<col=8FBC8F>[Toxic Spores]: You are poisoned by the fungal toxins!");
                }
            }
        }
        
        // Provide toxic cloud warning
        if (target instanceof Player && shouldGiveSporeWarning()) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FF8000>[Toxic Cloud]: Ganodermics releases poisonous spores affecting all nearby beings!");
        }
        
        // Create lingering toxic area effect
        final int lingeringDamage = toxicDamage;
        WorldTasksManager.schedule(new WorldTask() {
            private int ticks = 5; // 5 ticks of lingering effect
            
            @Override
            public void run() {
                if (ticks <= 0) {
                    this.stop();
                    return;
                }
                
                // Lingering toxic damage to anyone still in area
                for (Entity entity : npc.getPossibleTargets()) {
                    if (entity.withinDistance(npc, 2)) {
                        int damage = lingeringDamage / 5; // 20% of original damage per tick
                        delayHit(npc, 0, entity, getMagicHit(npc, damage));
                        entity.setNextGraphics(new Graphics(LINGERING_TOXIC_GRAPHICS));
                    }
                }
                
                ticks--;
            }
        }, 3);
    }
    
    /**
     * Perform regenerative spores healing ability
     */
    private void performRegenerativeSpores(NPC npc, Entity target, NPCCombatDefinitions defs) {
        regenerationCount++;
        
        npc.setNextAnimation(new Animation(SPORE_ANIMATION));
        npc.setNextGraphics(new Graphics(REGEN_GRAPHICS));
        
        // Get healing amount based on tier and phase
        int ganodermicsTier = determineGanodermicsTier(npc, defs);
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        
        double healRatio = ENHANCED_REGEN_RATIO;
        if (healthPercent <= FINAL_SPORE_THRESHOLD) {
            healRatio = DESPERATE_REGEN_RATIO;
        }
        
        int maxHealth = defs.getHitpoints();
        int healAmount = (int)(maxHealth * healRatio);
        
        // Tier scaling for regeneration
        healAmount = (int)(healAmount * (1.0 + (ganodermicsTier - 1) * 0.1)); // 10% per tier above 1
        
        // Cap healing to not exceed max health
        int currentHealth = npc.getHitpoints();
        healAmount = Math.min(healAmount, maxHealth - currentHealth);
        
        if (healAmount > 0) {
            npc.heal(healAmount);
            totalDamageHealed += healAmount;
            
            // Enhanced regeneration visual effects
            npc.setNextGraphics(new Graphics(HEALING_BURST_GRAPHICS));
            npc.playSound(HEALING_SOUND, 3); // Powerful healing sound
        }
        
        // Provide regeneration warning
        if (target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=8FBC8F>[Regenerative Spores]: The Ganodermics heals " + healAmount + " health from spore regeneration!");
        }
    }
    
    /**
     * Perform spore infection attack with damage over time
     */
    private void performSporeInfection(NPC npc, Entity target, NPCCombatDefinitions defs) {
        sporeInfectionCount++;
        
        npc.setNextAnimation(new Animation(SPORE_ANIMATION));
        World.sendProjectile(npc, target, INFECTION_PROJECTILE, 10, 18, 50, 50, 0, 0);
        
        // Get Boss Balancer stats for infection
        int ganodermicsTier = determineGanodermicsTier(npc, defs);
        int baseMaxHit = getBaseMaxHit(npc, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, ganodermicsTier, true);
        
        // Infection attack does 60% initial damage
        int infectionDamage = (int)(tierScaledMaxHit * 0.60);
        
        if (target instanceof Player) {
            infectionDamage = applyPlayerLevelScaling(infectionDamage, (Player) target, ganodermicsTier);
        }
        
        int initialDamage = calculateSafeMagicDamage(npc, infectionDamage);
        delayHit(npc, 1, target, getMagicHit(npc, initialDamage));
        
        // Apply spore infection DOT effect
        if (target instanceof Player) {
            final Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=8FBC8F>[Spore Infection]: You are infected with fungal spores!");
            
            // DOT effect over 10 seconds
            WorldTasksManager.schedule(new WorldTask() {
                private int ticks = 10;
                
                @Override
                public void run() {
                    if (ticks <= 0 || player.getHitpoints() <= 0) {
                        this.stop();
                        return;
                    }
                    
                    // DOT damage scaled by tier
                    int dotDamage = Math.max(10, ganodermicsTier * 5);
                    Hit dotHit = new Hit(npc, dotDamage, Hit.HitLook.POISON_DAMAGE);
                    player.applyHit(dotHit);
                    player.setNextGraphics(new Graphics(INFECTION_DAMAGE_GRAPHICS));
                    
                    if (ticks == 1) {
                        player.getPackets().sendGameMessage("<col=00FF00>[Recovery]: The spore infection has run its course.");
                    }
                    
                    ticks--;
                }
            }, 2);
        }
        
        // Infection impact effect
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                target.setNextGraphics(new Graphics(INFECTION_IMPACT_GRAPHICS));
                this.stop();
            }
        }, 1);
    }
    
    /**
     * Perform fungal growth attack affecting player stats
     */
    private void performFungalGrowth(NPC npc, Entity target, NPCCombatDefinitions defs) {
        fungalGrowthCount++;
        
        npc.setNextAnimation(new Animation(SPORE_ANIMATION));
        npc.setNextGraphics(new Graphics(GROWTH_GRAPHICS));
        
        // Get Boss Balancer stats for growth attack
        int ganodermicsTier = determineGanodermicsTier(npc, defs);
        int baseMaxHit = getBaseMaxHit(npc, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, ganodermicsTier, true);
        
        // Growth attack does 65% damage but affects stats
        int growthDamage = (int)(tierScaledMaxHit * 0.65);
        
        if (target instanceof Player) {
            growthDamage = applyPlayerLevelScaling(growthDamage, (Player) target, ganodermicsTier);
        }
        
        World.sendProjectile(npc, target, GROWTH_PROJECTILE, 10, 18, 50, 50, 0, 0);
        int damage = calculateSafeMagicDamage(npc, growthDamage);
        delayHit(npc, 2, target, getMagicHit(npc, damage));
        
        // Apply fungal growth effects to players
        if (target instanceof Player) {
            applyFungalGrowthEffects((Player) target, ganodermicsTier);
        }
        
        // Growth impact effect
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                target.setNextGraphics(new Graphics(GROWTH_IMPACT_GRAPHICS));
                this.stop();
            }
        }, 2);
    }
    
    /**
     * Apply fungal growth effects to player stats
     */
    private void applyFungalGrowthEffects(final Player player, final int tier) {
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Fungal growth affects different stats based on randomness
                int growthType = Utils.getRandom(3);
                
                if (growthType == 0) {
                    // Combat stat reduction
                    int statDrain = Math.max(4, tier * 2); // Minimum 4, scales with tier
                    int skill = Utils.getRandom(3); // Attack, Strength, Defence
                    skill = skill == 0 ? Skills.ATTACK : (skill == 1 ? Skills.STRENGTH : Skills.DEFENCE);
                    
                    int currentLevel = player.getSkills().getLevel(skill);
                    int newLevel = Math.max(1, currentLevel - statDrain - Utils.getRandom(3));
                    
                    player.getSkills().set(skill, newLevel);
                    player.getPackets().sendGameMessage("<col=8FBC8F>[Fungal Growth]: Parasitic fungi weaken your " + Skills.SKILL_NAME[skill] + "!");
                    
                } else if (growthType == 1) {
                    // Magic and prayer interference
                    int magicLevel = player.getSkills().getLevel(Skills.MAGIC);
                    int prayerPoints = player.getPrayer().getPrayerpoints();
                    
                    int magicDrain = Math.max(3, tier / 2);
                    int prayerDrain = Math.max(15, tier * 8);
                    
                    player.getSkills().set(Skills.MAGIC, Math.max(1, magicLevel - magicDrain));
                    player.getPrayer().drainPrayer(prayerDrain);
                    
                    player.getPackets().sendGameMessage("<col=8FBC8F>[Fungal Growth]: The fungi interfere with your magical and spiritual energies!");
                    
                } else {
                    // Movement and agility impairment
                    int agilityLevel = player.getSkills().getLevel(Skills.AGILITY);
                    int hpLevel = player.getSkills().getLevel(Skills.HITPOINTS);
                    
                    int agilityDrain = Math.max(3, tier / 2);
                    int hpDrain = Math.max(2, tier / 3);
                    
                    player.getSkills().set(Skills.AGILITY, Math.max(1, agilityLevel - agilityDrain));
                    player.getSkills().set(Skills.HITPOINTS, Math.max(10, hpLevel - hpDrain));
                    
                    player.getPackets().sendGameMessage("<col=8FBC8F>[Fungal Growth]: Parasitic growth impairs your mobility and vitality!");
                }
                
                this.stop();
            }
        }, 2);
    }
    
    /**
     * Determine Ganodermics' tier based on Boss Balancer system
     */
    private int determineGanodermicsTier(NPC npc, NPCCombatDefinitions defs) {
        try {
            int hp = defs.getHitpoints();
            int maxHit = defs.getMaxHit();
            
            // Estimate tier based on Boss Balancer HP/damage ranges for Magic boss
            if (hp >= 4500 && hp <= 8000 && maxHit >= 55 && maxHit <= 100) {
                return 5; // Expert tier
            } else if (hp >= 3000 && hp <= 5500 && maxHit >= 40 && maxHit <= 75) {
                return 4; // Advanced tier
            } else if (hp >= 6500 && hp <= 10500 && maxHit >= 70 && maxHit <= 125) {
                return 6; // Master tier
            }
            
            return GANODERMICS_DEFAULT_TIER; // Default to Expert tier
        } catch (Exception e) {
            return GANODERMICS_DEFAULT_TIER;
        }
    }
    
    /**
     * Get base max hit safely (NULL SAFE)
     */
    private int getBaseMaxHit(NPC npc, NPCCombatDefinitions defs) {
        try {
            int maxHit = defs.getMaxHit();
            return maxHit > 0 ? maxHit : DEFAULT_FALLBACK_DAMAGE;
        } catch (Exception e) {
            return DEFAULT_FALLBACK_DAMAGE; // Fallback Ganodermics damage
        }
    }
    
    /**
     * Apply Boss Balancer tier scaling for magic boss
     */
    private int applyBossTierScaling(int baseMaxHit, int tier, boolean isMagicAttack) {
        // Boss Balancer tier scaling: 15% increase per tier above 1
        double tierMultiplier = 1.0 + (tier - 1) * 0.15;
        
        // Magic boss type modifier - enhanced magic damage
        double typeModifier = 1.0;
        if (isMagicAttack) {
            typeModifier = 1.18; // 18% bonus for magic attacks (magic boss specialty)
        }
        
        return (int) (baseMaxHit * tierMultiplier * typeModifier);
    }
    
    /**
     * Apply player level scaling for balanced fungal encounter experience
     */
    private int applyPlayerLevelScaling(int damage, Player player, int tier) {
        int playerCombatLevel = player.getSkills().getCombatLevel();
        int recommendedLevel = tier * 11 + 45; // Tier 5 = level 100 recommended
        
        // Scale damage based on player level vs recommended
        if (playerCombatLevel < recommendedLevel) {
            double scaleFactor = (double) playerCombatLevel / recommendedLevel;
            scaleFactor = Math.max(0.45, scaleFactor); // Minimum 45% damage
            damage = (int) (damage * scaleFactor);
        }
        
        // Ensure damage is reasonable for Ganodermics (mid-tier fungal boss)
        damage = Math.max(15, Math.min(damage, 160)); // Cap between 15-160
        
        return damage;
    }
    
    /**
     * Calculate safe magic damage with bonuses (NULL SAFE)
     */
    private int calculateSafeMagicDamage(NPC npc, int maxHit) {
        try {
            int damage = Utils.getRandom(maxHit + 1);
            
            // Apply magic bonuses if they exist
            int[] bonuses = NPCBonuses.getBonuses(npc.getId());
            if (bonuses != null && bonuses.length > 3) {
                int magicBonus = bonuses[3]; // Magic attack bonus
                if (magicBonus > 0) {
                    double bonusMultiplier = 1.0 + (magicBonus / 1900.0); // Moderate scaling
                    damage = (int) (damage * Math.min(bonusMultiplier, 1.5)); // Cap at 50% bonus
                }
            }
            
            return Math.max(0, Math.min(damage, maxHit));
            
        } catch (Exception e) {
            return Utils.getRandom(maxHit / 2) + (maxHit / 4);
        }
    }
    
    /**
     * Provide strategic fungal guidance based on combat performance
     */
    private void provideFungalGuidance(Player player, NPC npc, NPCCombatDefinitions defs) {
        if (!shouldGiveHint()) return;
        
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        
        // Toxic cloud frequency guidance
        if (toxicCloudCount >= 3) {
            player.getPackets().sendGameMessage("<col=8FBC8F>[Toxic Analysis]: Multiple spore clouds detected! Stay mobile and use antidote protection!");
            toxicCloudCount = 0;
            return;
        }
        
        // Regeneration frequency guidance
        if (regenerationCount >= 4) {
            player.getPackets().sendGameMessage("<col=00FF00>[Regeneration Analysis]: Frequent healing detected! Focus on high burst damage to overwhelm regeneration!");
            regenerationCount = 0;
            return;
        }
        
        // Spore infection guidance
        if (sporeInfectionCount >= 2) {
            player.getPackets().sendGameMessage("<col=FF8000>[Infection Warning]: Multiple spore infections applied. Monitor your health for damage over time!");
            sporeInfectionCount = 0;
            return;
        }
        
        // Fungal growth guidance
        if (fungalGrowthCount >= 2) {
            player.getPackets().sendGameMessage("<col=8FBC8F>[Growth Effects]: Multiple fungal growth attacks used. Check your stats for parasitic interference!");
            fungalGrowthCount = 0;
            return;
        }
        
        // Phase-specific fungal guidance
        if (healthPercent > 0.75) {
            player.getPackets().sendGameMessage("<col=00FFFF>[Fungal Strategy]: Ganodermics uses spore-based magic with passive regeneration. Interrupt healing when possible!");
        } else if (healthPercent > 0.50) {
            player.getPackets().sendGameMessage("<col=FFFF00>[Spore Growth]: Enhanced fungal abilities incoming. Watch for toxic clouds and stat interference!");
        } else if (healthPercent > 0.25) {
            player.getPackets().sendGameMessage("<col=FF8000>[Toxic Bloom]: Frequent toxic attacks and stronger regeneration! Antidote protection critical!");
        } else {
            player.getPackets().sendGameMessage("<col=FF0000>[Final Spore]: Desperate regeneration mode! Use maximum burst damage to prevent healing!");
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
     * Check if should give spore warning (with cooldown)
     */
    private boolean shouldGiveSporeWarning() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSporeWarningTime >= SPORE_WARNING_COOLDOWN) {
            lastSporeWarningTime = currentTime;
            return true;
        }
        return false;
    }
    
    /**
     * Debug method for testing damage scaling and boss balancer integration
     */
    public String getDamageScalingInfo(int combatLevel) {
        int tier = GANODERMICS_DEFAULT_TIER;
        int baseMaxHit = DEFAULT_FALLBACK_DAMAGE;
        int tierScaled = applyBossTierScaling(baseMaxHit, tier, true);
        
        return String.format("Ganodermics Tier: %d, Base: %d, Magic Scaled: %d", 
                           tier, baseMaxHit, tierScaled);
    }
    
    /**
     * Get combat statistics for fungal combat analysis
     */
    public String getCombatStats() {
        return String.format("Toxic Clouds: %d, Regenerations: %d, Infections: %d, Growth: %d, Total Healed: %d", 
                           toxicCloudCount, regenerationCount, sporeInfectionCount, fungalGrowthCount, totalDamageHealed);
    }

    @Override
    public Object[] getKeys() {
        return new Object[] { GANODERMICS_ID_1, GANODERMICS_ID_2 }; // Ganodermics NPC IDs
    }
}