package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;

/**
 * Enhanced Edimmu Combat System with Boss Balancer Integration
 * 
 * Features:
 * - Integrated with Boss Balancer 10-tier system (Magic Boss Type - Tier 4)
 * - Advanced boss guidance system with soul drain mechanics education
 * - Enhanced life drain abilities with strategic healing patterns
 * - Multi-phase combat with escalating soul manipulation powers
 * - Intelligent attack selection with enhanced range-based decisions
 * - Soul corruption mechanics affecting player stats and prayer
 * - Advanced life steal system with tactical healing implications
 * - Player-level scaling for balanced undead encounter experience
 * - Null-safe damage calculation system with comprehensive error handling
 * - Undead-themed force talk messages with soul manipulation atmosphere
 * - Performance tracking for soul drain analysis and combat balancing
 * 
 * @author Zeus
 * @date May 31, 2025
 * @version 3.0 - Boss Balancer Integration with Advanced Soul Drain System
 */
public class EdimmuCombat extends CombatScript {
    
    // Boss Balancer Integration Constants
    private static final int EDIMMU_BOSS_TYPE = 3; // Magic Boss Type (soul magic specialist)
    private static final int EDIMMU_DEFAULT_TIER = 4; // Advanced tier by default
    
    // Combat phase thresholds for the Edimmu
    private static final double SOUL_HUNGER_THRESHOLD = 0.75; // 75% health - enhanced life drain
    private static final double SOUL_FEAST_THRESHOLD = 0.50; // 50% health - powerful soul manipulation
    private static final double SOUL_DESPERATION_THRESHOLD = 0.25; // 25% health - maximum life steal
    
    // Life drain and healing mechanics
    private static final int BASE_HEAL_CHANCE = 5; // 1 in 5 chance normally (20%)
    private static final int ENHANCED_HEAL_CHANCE = 4; // 1 in 4 when soul hungry (25%)
    private static final int FEAST_HEAL_CHANCE = 3; // 1 in 3 when soul feasting (33%)
    private static final int DESPERATE_HEAL_CHANCE = 2; // 1 in 2 when desperate (50%)
    
    private static final double BASE_HEAL_RATIO = 0.25; // 25% of damage dealt
    private static final double ENHANCED_HEAL_RATIO = 0.35; // 35% in enhanced phases
    private static final double DESPERATE_HEAL_RATIO = 0.50; // 50% in desperate phase
    
    // Soul corruption and special abilities
    private static final int SOUL_CORRUPTION_CHANCE = 12; // 1 in 12 chance for soul corruption
    private static final int ENHANCED_CORRUPTION_CHANCE = 8; // 1 in 8 in later phases
    private static final int SOUL_SCREAM_CHANCE = 20; // 1 in 20 for soul scream AOE
    
    // Guidance system constants
    private static final int GUIDANCE_FREQUENCY = 4; // 1 in 4 chance for strategic hints
    private static final int HINT_COOLDOWN = 14000; // 14 seconds between hints
    private static final int DRAIN_WARNING_COOLDOWN = 22000; // 22 seconds between drain warnings
    
    // Instance variables for combat tracking
    private long lastHintTime = 0;
    private long lastDrainWarningTime = 0;
    private boolean hasGivenOpeningAdvice = false;
    private boolean hasGivenHungerWarning = false;
    private boolean hasGivenFeastWarning = false;
    private boolean hasGivenDesperationWarning = false;
    private int healingCount = 0;
    private int soulCorruptionCount = 0;
    private int totalDamageHealed = 0;
    private int magicAttackCount = 0;
    private int meleeAttackCount = 0;
    
    // Enhanced Edimmu force talk messages with soul manipulation theme
    private static final String[] AWAKENING_MESSAGES = {
        "Your soul calls to me, mortal...",
        "I hunger for your life essence!",
        "Death is but a doorway I have crossed..."
    };
    
    private static final String[] SOUL_HUNGER_MESSAGES = {
        "I feast upon your life force!",
        "Your soul strengthens my undead form!",
        "The boundary between life and death fades!"
    };
    
    private static final String[] SOUL_FEAST_MESSAGES = {
        "Your essence flows into my being!",
        "I am sustained by your very soul!",
        "Death empowers me beyond mortal comprehension!"
    };
    
    private static final String[] SOUL_DESPERATION_MESSAGES = {
        "I will not return to the void!",
        "Your life will anchor me to this realm!",
        "I must feed to maintain my existence!"
    };

    @Override
    public int attack(final NPC npc, final Entity target) {
        // Enhanced null safety checks
        if (npc == null || target == null) {
            return 4; // Default attack delay
        }
        
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) {
            return 4; // No combat definitions available
        }
        
        // Maintain close following behavior for aggressive undead nature
        npc.setForceFollowClose(true);
        
        // Provide opening undead strategy advice
        if (!hasGivenOpeningAdvice && target instanceof Player) {
            provideOpeningUndeadStrategy((Player) target, npc, defs);
            hasGivenOpeningAdvice = true;
        }
        
        // Check combat phases and provide soul drain warnings
        checkCombatPhases(npc, target, defs);
        
        // Soul-themed force talk with undead atmosphere
        performSoulManipulationForceTalk(npc, defs);
        
        // Enhanced attack selection with soul corruption chances
        performEnhancedAttackSelection(npc, target, defs);
        
        // Set animation after attack logic
        npc.setNextAnimation(new Animation(defs.getAttackEmote()));
        
        // Provide strategic undead guidance
        if (target instanceof Player) {
            provideUndeadGuidance((Player) target, npc, defs);
        }
        
        return defs.getAttackDelay();
    }
    
    /**
     * Provide opening undead strategy advice
     */
    private void provideOpeningUndeadStrategy(Player player, NPC npc, NPCCombatDefinitions defs) {
        int edimmuTier = determineEdimmuTier(npc, defs);
        
        player.getPackets().sendGameMessage("<col=8B0000>[Undead Knowledge]: Edimmu are soul-draining undead that feed on life essence!");
        player.getPackets().sendGameMessage("<col=8B0000>[Combat Analysis]: Tier " + edimmuTier + " Magic Boss - Specializes in life drain and soul manipulation!");
        player.getPackets().sendGameMessage("<col=00FFFF>[Critical Strategy]: Edimmu heal themselves by draining your life force - deal high burst damage!");
        player.getPackets().sendGameMessage("<col=00FFFF>[Tactical Warning]: Watch for soul corruption attacks that can affect your stats and prayer!");
    }
    
    /**
     * Check combat phases and provide soul drain warnings
     */
    private void checkCombatPhases(NPC npc, Entity target, NPCCombatDefinitions defs) {
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        
        if (healthPercent <= SOUL_DESPERATION_THRESHOLD && !hasGivenDesperationWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FF0000>[SOUL DESPERATION]: The Edimmu fights for its undead existence!");
            player.getPackets().sendGameMessage("<col=FF0000>[MAXIMUM DRAIN]: 50% chance to heal from attacks - expect massive life steal!");
            player.getPackets().sendGameMessage("<col=FF0000>[CRITICAL PHASE]: Soul corruption abilities at maximum power!");
            hasGivenDesperationWarning = true;
            
        } else if (healthPercent <= SOUL_FEAST_THRESHOLD && !hasGivenFeastWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FF8000>[Soul Feast]: The Edimmu's life drain powers intensify!");
            player.getPackets().sendGameMessage("<col=FF8000>[Enhanced Healing]: 33% chance to heal with increased life steal efficiency!");
            player.getPackets().sendGameMessage("<col=FF8000>[Soul Corruption]: Expect more frequent stat and prayer drain effects!");
            hasGivenFeastWarning = true;
            
        } else if (healthPercent <= SOUL_HUNGER_THRESHOLD && !hasGivenHungerWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FFFF00>[Soul Hunger]: The Edimmu's hunger for life essence grows!");
            player.getPackets().sendGameMessage("<col=FFFF00>[Enhanced Drain]: 25% chance to heal with improved life steal!");
            player.getPackets().sendGameMessage("<col=FFFF00>[Tactical Note]: Undead resilience increases - prepare for prolonged combat!");
            hasGivenHungerWarning = true;
        }
    }
    
    /**
     * Perform soul manipulation-themed force talk based on combat phase
     */
    private void performSoulManipulationForceTalk(NPC npc, NPCCombatDefinitions defs) {
        if (Utils.getRandom(10) != 0) return; // 1 in 10 chance for force talk
        
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        String[] messageArray;
        int soundBase;
        
        // Select messages and sounds based on current phase
        if (healthPercent <= SOUL_DESPERATION_THRESHOLD) {
            messageArray = SOUL_DESPERATION_MESSAGES;
            soundBase = 1150; // Desperate, echoing undead sounds
        } else if (healthPercent <= SOUL_FEAST_THRESHOLD) {
            messageArray = SOUL_FEAST_MESSAGES;
            soundBase = 1148;
        } else if (healthPercent <= SOUL_HUNGER_THRESHOLD) {
            messageArray = SOUL_HUNGER_MESSAGES;
            soundBase = 1146;
        } else {
            messageArray = AWAKENING_MESSAGES;
            soundBase = 1144;
        }
        
        String message = messageArray[Utils.getRandom(messageArray.length)];
        npc.setNextForceTalk(new ForceTalk(message));
        npc.playSound(soundBase + Utils.getRandom(2), 2); // Haunting undead sound effects
    }
    
    /**
     * Perform enhanced attack selection with soul corruption mechanics
     */
    private void performEnhancedAttackSelection(NPC npc, Entity target, NPCCombatDefinitions defs) {
        // Soul corruption chance check
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        int corruptionChance = healthPercent <= SOUL_FEAST_THRESHOLD ? ENHANCED_CORRUPTION_CHANCE : SOUL_CORRUPTION_CHANCE;
        
        if (Utils.getRandom(corruptionChance) == 0) {
            performSoulCorruptionAttack(npc, target, defs);
            return;
        }
        
        // Soul scream AOE attack chance
        if (Utils.getRandom(SOUL_SCREAM_CHANCE) == 0) {
            performSoulScreamAttack(npc, target, defs);
            return;
        }
        
        // Enhanced standard attack selection (50/50 magic/melee with intelligence)
        final int attackChoice = Utils.random(2);
        
        switch (attackChoice) {
            case 0:
                sendMageAttack(npc, target);
                magicAttackCount++;
                break;
            default:
                sendMeleeAttack(npc, target);
                meleeAttackCount++;
                break;
        }
    }
    
    /**
     * Executes Edimmu's enhanced magic attack with soul drain mechanics.
     * 
     * @param npc The Edimmu NPC.
     * @param target The target.
     */
    private void sendMageAttack(NPC npc, Entity target) {
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        
        // Get Boss Balancer stats
        int edimmuTier = determineEdimmuTier(npc, defs);
        int baseMaxHit = getBaseMaxHit(npc, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, edimmuTier, true); // Magic attack
        
        // Apply player scaling if target is a player
        if (target instanceof Player) {
            tierScaledMaxHit = applyPlayerLevelScaling(tierScaledMaxHit, (Player) target, edimmuTier);
        }
        
        // Calculate enhanced magic hit
        Hit hit = getMagicHit(npc, getRandomMaxHit(npc, tierScaledMaxHit, NPCCombatDefinitions.MAGE, target));
        
        // Enhanced life drain mechanics based on phase
        handleLifeDrainMechanic(npc, target, hit, defs);
        
        // Send soul drain projectile
        World.sendProjectile(npc, target, 2263, 41, 16, 41, 35, 16, 0); // Soul projectile
        delayHit(npc, 2, target, hit);
        
        // Soul drain visual effect
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                target.setNextGraphics(new Graphics(2264)); // Soul drain effect
                this.stop();
            }
        }, 2);
    }
    
    /**
     * Executes Edimmu's enhanced melee attack with life steal mechanics.
     * 
     * @param npc The Edimmu NPC.
     * @param target The target.
     */
    private void sendMeleeAttack(NPC npc, Entity target) {
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        
        // Check if target is in melee range
        if (!target.withinDistance(npc, 1)) {
            // If not in melee range, use magic attack instead
            sendMageAttack(npc, target);
            return;
        }
        
        // Get Boss Balancer stats
        int edimmuTier = determineEdimmuTier(npc, defs);
        int baseMaxHit = getBaseMaxHit(npc, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, edimmuTier, false); // Melee attack
        
        // Apply player scaling if target is a player
        if (target instanceof Player) {
            tierScaledMaxHit = applyPlayerLevelScaling(tierScaledMaxHit, (Player) target, edimmuTier);
        }
        
        // Calculate enhanced melee hit
        Hit hit = getMeleeHit(npc, getRandomMaxHit(npc, tierScaledMaxHit, NPCCombatDefinitions.MELEE, target));
        
        // Enhanced life drain mechanics based on phase
        handleLifeDrainMechanic(npc, target, hit, defs);
        
        delayHit(npc, 0, target, hit);
        
        // Life steal visual effect
        target.setNextGraphics(new Graphics(2265)); // Life steal impact effect
    }
    
    /**
     * Handle enhanced life drain mechanics based on combat phase
     */
    private void handleLifeDrainMechanic(NPC npc, Entity target, Hit hit, NPCCombatDefinitions defs) {
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        
        // Determine heal chance and ratio based on phase
        int healChance;
        double healRatio;
        
        if (healthPercent <= SOUL_DESPERATION_THRESHOLD) {
            healChance = DESPERATE_HEAL_CHANCE;
            healRatio = DESPERATE_HEAL_RATIO;
        } else if (healthPercent <= SOUL_FEAST_THRESHOLD) {
            healChance = FEAST_HEAL_CHANCE;
            healRatio = ENHANCED_HEAL_RATIO;
        } else if (healthPercent <= SOUL_HUNGER_THRESHOLD) {
            healChance = ENHANCED_HEAL_CHANCE;
            healRatio = ENHANCED_HEAL_RATIO;
        } else {
            healChance = BASE_HEAL_CHANCE;
            healRatio = BASE_HEAL_RATIO;
        }
        
        // Life drain healing check
        if (Utils.random(healChance) == 0) {
            int healAmount = (int) (hit.getDamage() * healRatio);
            
            // Ensure heal amount is reasonable
            healAmount = Math.max(1, Math.min(healAmount, defs.getHitpoints() / 10)); // Cap at 10% max HP
            
            npc.heal(healAmount);
            healingCount++;
            totalDamageHealed += healAmount;
            
            // Enhanced soul drain graphics
            npc.setNextGraphics(new Graphics(2266)); // Healing soul energy effect
            
            // Provide life drain warning
            if (target instanceof Player && shouldGiveDrainWarning()) {
                Player player = (Player) target;
                player.getPackets().sendGameMessage("<col=8B0000>[Life Drain]: The Edimmu feeds on your life force, healing " + healAmount + " health!");
            }
        }
    }
    
    /**
     * Perform soul corruption attack with stat drain effects
     */
    private void performSoulCorruptionAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
        soulCorruptionCount++;
        
        // Get Boss Balancer stats for corruption attack
        int edimmuTier = determineEdimmuTier(npc, defs);
        int baseMaxHit = getBaseMaxHit(npc, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, edimmuTier, true); // Magic-based corruption
        
        // Corruption attack does 80% damage but has soul drain effects
        int corruptionDamage = (int)(tierScaledMaxHit * 0.80);
        
        if (target instanceof Player) {
            corruptionDamage = applyPlayerLevelScaling(corruptionDamage, (Player) target, edimmuTier);
        }
        
        // Enhanced soul corruption projectile
        World.sendProjectile(npc, target, 2267, 41, 16, 41, 35, 16, 0);
        Hit corruptionHit = getMagicHit(npc, getRandomMaxHit(npc, corruptionDamage, NPCCombatDefinitions.MAGE, target));
        delayHit(npc, 2, target, corruptionHit);
        
        // Apply soul corruption debuff effect
        if (target instanceof Player) {
            applySoulCorruptionDebuff((Player) target, edimmuTier);
        }
        
        // Enhanced corruption graphics
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                target.setNextGraphics(new Graphics(2268)); // Soul corruption effect
                this.stop();
            }
        }, 2);
        
        // Handle life drain for corruption attack
        handleLifeDrainMechanic(npc, target, corruptionHit, defs);
    }
    
    /**
     * Apply soul corruption debuff effect scaled by tier
     */
    private void applySoulCorruptionDebuff(final Player player, final int tier) {
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Soul corruption affects different aspects based on randomness
                int corruptionType = Utils.getRandom(3);
                
                if (corruptionType == 0) {
                    // Prayer point corruption
                    int prayerDrain = Math.max(10, tier * 5); // Minimum 10, scales with tier
                    player.getPrayer().drainPrayer(prayerDrain + Utils.getRandom(15));
                    player.getPackets().sendGameMessage("<col=8B0000>[Soul Corruption]: Your prayer points have been drained by dark soul magic!");
                    
                } else if (corruptionType == 1) {
                    // Combat stat corruption
                    int statDrain = Math.max(3, tier / 2); // Minimum 3, scales with tier
                    int skill = Utils.getRandom(3); // Attack, Strength, Defence
                    skill = skill == 0 ? Skills.ATTACK : (skill == 1 ? Skills.STRENGTH : Skills.DEFENCE);
                    
                    int currentLevel = player.getSkills().getLevel(skill);
                    int newLevel = Math.max(1, currentLevel - statDrain - Utils.getRandom(3));
                    
                    player.getSkills().set(skill, newLevel);
                    player.getPackets().sendGameMessage("<col=8B0000>[Soul Corruption]: Your " + Skills.SKILL_NAME[skill] + " has been corrupted by undead energy!");
                    
                } else {
                    // Magic and hitpoints corruption
                    int magicLevel = player.getSkills().getLevel(Skills.MAGIC);
                    int hpLevel = player.getSkills().getLevel(Skills.HITPOINTS);
                    
                    int magicDrain = Math.max(2, tier / 3);
                    int hpDrain = Math.max(1, tier / 4);
                    
                    player.getSkills().set(Skills.MAGIC, Math.max(1, magicLevel - magicDrain));
                    player.getSkills().set(Skills.HITPOINTS, Math.max(10, hpLevel - hpDrain));
                    
                    player.getPackets().sendGameMessage("<col=8B0000>[Soul Corruption]: Your soul has been tainted, weakening your magic and vitality!");
                }
                
                this.stop();
            }
        }, 2);
    }
    
    /**
     * Perform soul scream AOE attack
     */
    private void performSoulScreamAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
        // Enhanced soul scream animation
        npc.setNextAnimation(new Animation(defs.getAttackEmote()));
        npc.setNextGraphics(new Graphics(2269)); // Soul scream effect
        
        // Get Boss Balancer stats for AOE attack
        int edimmuTier = determineEdimmuTier(npc, defs);
        int baseMaxHit = getBaseMaxHit(npc, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, edimmuTier, true);
        
        // AOE attack does 70% damage to all nearby targets
        int aoeDamage = (int)(tierScaledMaxHit * 0.70);
        int targetsHit = 0;
        
        // Hit all entities within 3 tiles
        for (Entity entity : npc.getPossibleTargets()) {
            if (entity.withinDistance(npc, 3)) {
                targetsHit++;
                
                if (entity instanceof Player) {
                    aoeDamage = applyPlayerLevelScaling(aoeDamage, (Player) entity, edimmuTier);
                }
                
                Hit screamHit = getMagicHit(npc, getRandomMaxHit(npc, aoeDamage, NPCCombatDefinitions.MAGE, entity));
                delayHit(npc, 1, entity, screamHit);
                entity.setNextGraphics(new Graphics(2270)); // Soul scream impact
                
                // Life drain from AOE attack (reduced chance)
                if (Utils.random(8) == 0) { // 1 in 8 chance for AOE heal
                    int healAmount = screamHit.getDamage() / 6; // Reduced healing from AOE
                    npc.heal(healAmount);
                    totalDamageHealed += healAmount;
                }
            }
        }
        
        // Provide AOE warning
        if (target instanceof Player && targetsHit > 1) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=8B0000>[Soul Scream]: The Edimmu's wail affects all nearby beings!");
        }
    }
    
    /**
     * Determine Edimmu's tier based on Boss Balancer system
     */
    private int determineEdimmuTier(NPC npc, NPCCombatDefinitions defs) {
        try {
            int hp = defs.getHitpoints();
            int maxHit = defs.getMaxHit();
            
            // Estimate tier based on Boss Balancer HP/damage ranges for Magic boss
            if (hp >= 2500 && hp <= 4500 && maxHit >= 35 && maxHit <= 65) {
                return 4; // Advanced tier
            } else if (hp >= 1800 && hp <= 3200 && maxHit >= 25 && maxHit <= 50) {
                return 3; // Intermediate tier
            } else if (hp >= 4000 && hp <= 6500 && maxHit >= 50 && maxHit <= 85) {
                return 5; // Expert tier
            }
            
            return EDIMMU_DEFAULT_TIER; // Default to Advanced tier
        } catch (Exception e) {
            return EDIMMU_DEFAULT_TIER;
        }
    }
    
    /**
     * Get base max hit safely (NULL SAFE)
     */
    private int getBaseMaxHit(NPC npc, NPCCombatDefinitions defs) {
        try {
            int maxHit = defs.getMaxHit();
            return maxHit > 0 ? maxHit : 50; // Default Edimmu damage if invalid
        } catch (Exception e) {
            return 50; // Fallback Edimmu damage
        }
    }
    
    /**
     * Apply Boss Balancer tier scaling for magic boss
     */
    private int applyBossTierScaling(int baseMaxHit, int tier, boolean isMagicAttack) {
        // Boss Balancer tier scaling: 15% increase per tier above 1
        double tierMultiplier = 1.0 + (tier - 1) * 0.15;
        
        // Magic boss type modifier - enhanced magic damage, reduced melee
        double typeModifier = 1.0;
        if (isMagicAttack) {
            typeModifier = 1.15; // 15% bonus for magic attacks (magic boss specialty)
        } else {
            typeModifier = 0.95; // 5% reduction for melee attacks (not specialty)
        }
        
        return (int) (baseMaxHit * tierMultiplier * typeModifier);
    }
    
    /**
     * Apply player level scaling for balanced undead encounter experience
     */
    private int applyPlayerLevelScaling(int damage, Player player, int tier) {
        int playerCombatLevel = player.getSkills().getCombatLevel();
        int recommendedLevel = tier * 12 + 35; // Tier 4 = level 83 recommended
        
        // Scale damage based on player level vs recommended
        if (playerCombatLevel < recommendedLevel) {
            double scaleFactor = (double) playerCombatLevel / recommendedLevel;
            scaleFactor = Math.max(0.45, scaleFactor); // Minimum 45% damage
            damage = (int) (damage * scaleFactor);
        }
        
        // Ensure damage is reasonable for Edimmu (mid-tier undead)
        damage = Math.max(12, Math.min(damage, 120)); // Cap between 12-120
        
        return damage;
    }
    
    /**
     * Provide strategic undead guidance based on combat performance
     */
    private void provideUndeadGuidance(Player player, NPC npc, NPCCombatDefinitions defs) {
        if (!shouldGiveHint()) return;
        
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        
        // Healing frequency guidance
        if (healingCount >= 4) {
            player.getPackets().sendGameMessage("<col=8B0000>[Life Drain Analysis]: Multiple healing instances detected! Consider burst damage to overwhelm regeneration!");
            healingCount = 0;
            return;
        }
        
        // Soul corruption guidance
        if (soulCorruptionCount >= 2) {
            player.getPackets().sendGameMessage("<col=00FFFF>[Soul Protection]: Multiple soul corruption attacks used. Monitor your stats and prayer levels!");
            soulCorruptionCount = 0;
            return;
        }
        
        // Attack pattern guidance
        if (magicAttackCount >= 5) {
            player.getPackets().sendGameMessage("<col=FFFF00>[Combat Pattern]: Edimmu favoring magic attacks. Magic protection prayers recommended!");
            magicAttackCount = 0;
            return;
        }
        
        if (meleeAttackCount >= 5) {
            player.getPackets().sendGameMessage("<col=FFFF00>[Combat Pattern]: Edimmu using frequent melee. Maintain distance to force magic attacks!");
            meleeAttackCount = 0;
            return;
        }
        
        // Phase-specific undead guidance
        if (healthPercent > 0.75) {
            player.getPackets().sendGameMessage("<col=00FFFF>[Undead Strategy]: Edimmu at full strength. Watch for 20% life drain chance on attacks!");
        } else if (healthPercent > 0.50) {
            player.getPackets().sendGameMessage("<col=FFFF00>[Soul Hunger]: Enhanced life drain phase - 25% heal chance with improved efficiency!");
        } else if (healthPercent > 0.25) {
            player.getPackets().sendGameMessage("<col=FF8000>[Soul Feast]: Dangerous phase - 33% heal chance and frequent soul corruption!");
        } else {
            player.getPackets().sendGameMessage("<col=FF0000>[Soul Desperation]: Maximum danger - 50% heal chance and constant soul manipulation!");
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
     * Check if should give drain warning (with cooldown)
     */
    private boolean shouldGiveDrainWarning() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDrainWarningTime >= DRAIN_WARNING_COOLDOWN) {
            lastDrainWarningTime = currentTime;
            return true;
        }
        return false;
    }
    
    /**
     * Debug method for testing damage scaling and boss balancer integration
     */
    public String getDamageScalingInfo(int combatLevel, boolean isMagic) {
        int tier = EDIMMU_DEFAULT_TIER;
        int baseMaxHit = 50;
        int tierScaled = applyBossTierScaling(baseMaxHit, tier, isMagic);
        String attackType = isMagic ? "Magic" : "Melee";
        
        return String.format("Edimmu Tier: %d, Base: %d, %s Scaled: %d", 
                           tier, baseMaxHit, attackType, tierScaled);
    }
    
    /**
     * Get combat statistics for life drain analysis
     */
    public String getCombatStats() {
        return String.format("Heals: %d, Total Healed: %d, Soul Corruptions: %d, Magic: %d, Melee: %d", 
                           healingCount, totalDamageHealed, soulCorruptionCount, magicAttackCount, meleeAttackCount);
    }

    @Override
    public Object[] getKeys() {
        return new Object[] { "Edimmu" }; // Edimmu NPC ID/Name
    }
}