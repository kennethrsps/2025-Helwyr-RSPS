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
 * Enhanced Dark Beast Combat System with Boss Balancer Integration
 * 
 * Features:
 * - Integrated with Boss Balancer 10-tier system (Hybrid Boss Type - Tier 5)
 * - Dynamic boss guidance system with strategic slayer advice
 * - Enhanced shadow magic attacks with darkness manipulation
 * - Multi-phase combat with stealth and darkness mechanics
 * - Intelligent range-based attack selection with special abilities
 * - Shadow step teleportation mechanic for tactical positioning
 * - Player-level scaling for balanced slayer experience
 * - Null-safe damage calculation system with comprehensive error handling
 * - Darkness-themed force talk messages with atmospheric sound effects
 * - Performance tracking for combat analysis and balancing
 * 
 * @author Zeus
 * @date May 31, 2025
 * @version 3.0 - Boss Balancer Integration with Advanced Guidance System
 */
public class DarkBeastCombat extends CombatScript {
    
    // Boss Balancer Integration Constants
    private static final int DARK_BEAST_BOSS_TYPE = 4; // Hybrid Boss Type (melee + magic)
    private static final int DARK_BEAST_DEFAULT_TIER = 5; // Expert tier by default
    
    // Combat phase thresholds for the Dark Beast
    private static final double SHADOW_PHASE_THRESHOLD = 0.75; // 75% health - enhanced stealth
    private static final double DARKNESS_PHASE_THRESHOLD = 0.50; // 50% health - shadow magic intensifies
    private static final double FINAL_SHADOW_THRESHOLD = 0.25; // 25% health - desperate shadow tactics
    
    // Attack ranges and probabilities
    private static final int MELEE_RANGE = 3;
    private static final int MAGIC_RANGE = 8;
    private static final int SHADOW_STEP_CHANCE = 15; // 1 in 15 chance for shadow step
    private static final int ENHANCED_STEP_CHANCE = 8; // 1 in 8 in later phases
    private static final int DARKNESS_ATTACK_CHANCE = 25; // 1 in 25 for special darkness attack
    
    // Guidance system constants
    private static final int GUIDANCE_FREQUENCY = 5; // 1 in 5 chance for strategic hints
    private static final int HINT_COOLDOWN = 12000; // 12 seconds between hints
    private static final int SHADOW_WARNING_COOLDOWN = 20000; // 20 seconds between shadow warnings
    
    // Instance variables for combat tracking
    private long lastHintTime = 0;
    private long lastShadowWarningTime = 0;
    private boolean hasGivenOpeningAdvice = false;
    private boolean hasGivenShadowWarning = false;
    private boolean hasGivenDarknessWarning = false;
    private boolean hasGivenFinalWarning = false;
    private int shadowStepCount = 0;
    private int darknessAttacksUsed = 0;
    private int magicAttackStreak = 0;
    private int meleeAttackStreak = 0;
    
    // Enhanced Dark Beast force talk messages with shadow theme
    private static final String[] AWAKENING_MESSAGES = {
        "You disturb my eternal slumber...",
        "Darkness shall consume you, mortal!",
        "The shadows whisper your doom..."
    };
    
    private static final String[] SHADOW_MESSAGES = {
        "I am one with the darkness!",
        "The shadows are my allies!",
        "You cannot fight what you cannot see!"
    };
    
    private static final String[] DARKNESS_MESSAGES = {
        "Behold the true power of darkness!",
        "I command the very shadows themselves!",
        "Your light fades before my darkness!"
    };
    
    private static final String[] FINAL_SHADOW_MESSAGES = {
        "I will not return to the void!",
        "The darkness is eternal!",
        "You cannot banish what is already dead!"
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
        
        // Provide opening slayer strategy advice
        if (!hasGivenOpeningAdvice && target instanceof Player) {
            provideOpeningSlayerStrategy((Player) target, npc, defs);
            hasGivenOpeningAdvice = true;
        }
        
        // Check combat phases and provide strategic warnings
        checkCombatPhases(npc, target, defs);
        
        // Shadow step teleportation mechanic
        handleShadowStepMechanic(npc, target, defs);
        
        // Phase-appropriate force talk with shadow atmosphere
        performDarknessThemedForceTalk(npc, defs);
        
        // Determine attack type based on range and phase
        performIntelligentAttackSelection(npc, target, defs);
        
        // Provide strategic slayer guidance
        if (target instanceof Player) {
            provideSlayerGuidance((Player) target, npc, defs);
        }
        
        return defs.getAttackDelay();
    }
    
    /**
     * Provide opening slayer strategy advice
     */
    private void provideOpeningSlayerStrategy(Player player, NPC npc, NPCCombatDefinitions defs) {
        int darkBeastTier = determineDarkBeastTier(npc, defs);
        
        player.getPackets().sendGameMessage("<col=800080>[Slayer Knowledge]: Dark Beasts are shadow creatures that blend melee and magic combat!");
        player.getPackets().sendGameMessage("<col=800080>[Combat Analysis]: Tier " + darkBeastTier + " Hybrid Boss - Uses range-based attack selection!");
        player.getPackets().sendGameMessage("<col=00FFFF>[Tactical Advice]: Maintain optimal distance - too close triggers powerful melee, too far enables magic!");
        player.getPackets().sendGameMessage("<col=00FFFF>[Shadow Warning]: Watch for shadow step teleportation - Dark Beasts can reposition unexpectedly!");
    }
    
    /**
     * Check combat phases and provide strategic warnings
     */
    private void checkCombatPhases(NPC npc, Entity target, NPCCombatDefinitions defs) {
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        
        if (healthPercent <= FINAL_SHADOW_THRESHOLD && !hasGivenFinalWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FF0000>[FINAL SHADOW]: The Dark Beast calls upon ultimate darkness!");
            player.getPackets().sendGameMessage("<col=FF0000>[CRITICAL PHASE]: Expect frequent shadow steps and desperate shadow magic!");
            player.getPackets().sendGameMessage("<col=FF0000>[Last Stand]: The creature fights with the fury of the condemned!");
            hasGivenFinalWarning = true;
            
        } else if (healthPercent <= DARKNESS_PHASE_THRESHOLD && !hasGivenDarknessWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FF8000>[Darkness Rises]: The Dark Beast's shadow magic intensifies!");
            player.getPackets().sendGameMessage("<col=FF8000>[Enhanced Threat]: More frequent special attacks and shadow manipulation!");
            player.getPackets().sendGameMessage("<col=FF8000>[Strategic Update]: Expect increased teleportation and darkness abilities!");
            hasGivenDarknessWarning = true;
            
        } else if (healthPercent <= SHADOW_PHASE_THRESHOLD && !hasGivenShadowWarning && target instanceof Player) {
            Player player = (Player) target;
            player.getPackets().sendGameMessage("<col=FFFF00>[Shadow Phase]: The Dark Beast becomes more elusive and dangerous!");
            player.getPackets().sendGameMessage("<col=FFFF00>[Tactical Note]: Enhanced stealth abilities and improved shadow magic incoming!");
            player.getPackets().sendGameMessage("<col=FFFF00>[Combat Advisory]: Stay alert for sudden position changes!");
            hasGivenShadowWarning = true;
        }
    }
    
    /**
     * Handle shadow step teleportation mechanic
     */
    private void handleShadowStepMechanic(NPC npc, Entity target, NPCCombatDefinitions defs) {
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        int stepChance = healthPercent <= DARKNESS_PHASE_THRESHOLD ? ENHANCED_STEP_CHANCE : SHADOW_STEP_CHANCE;
        
        if (Utils.getRandom(stepChance) == 0) {
            // Find a suitable teleportation position
            int targetX = target.getX();
            int targetY = target.getY();
            
            // Try to teleport to a tactical position (flanking or optimal range)
            for (int attempts = 0; attempts < 5; attempts++) {
                int newX = targetX + Utils.getRandom(7) - 3; // 3 tile radius
                int newY = targetY + Utils.getRandom(7) - 3;
                
                if (World.canMoveNPC(npc.getPlane(), newX, newY, npc.getSize())) {
                    // Perform shadow step
                    npc.setNextGraphics(new Graphics(1576)); // Shadow effect at old position
                    npc.setNextWorldTile(new com.rs.game.WorldTile(newX, newY, npc.getPlane()));
                    npc.setNextGraphics(new Graphics(1577)); // Shadow effect at new position
                    
                    shadowStepCount++;
                    
                    // Provide shadow step warning
                    if (target instanceof Player && shouldGiveShadowWarning()) {
                        Player player = (Player) target;
                        player.getPackets().sendGameMessage("<col=800080>[Shadow Step]: The Dark Beast teleports through the shadows!");
                    }
                    
                    break;
                }
            }
        }
    }
    
    /**
     * Perform darkness-themed force talk based on combat phase
     */
    private void performDarknessThemedForceTalk(NPC npc, NPCCombatDefinitions defs) {
        if (Utils.getRandom(8) != 0) return; // 1 in 8 chance for force talk
        
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        String[] messageArray;
        int soundBase;
        
        // Select messages and sounds based on current phase
        if (healthPercent <= FINAL_SHADOW_THRESHOLD) {
            messageArray = FINAL_SHADOW_MESSAGES;
            soundBase = 1200; // Desperate, echoing sounds
        } else if (healthPercent <= DARKNESS_PHASE_THRESHOLD) {
            messageArray = DARKNESS_MESSAGES;
            soundBase = 1198;
        } else if (healthPercent <= SHADOW_PHASE_THRESHOLD) {
            messageArray = SHADOW_MESSAGES;
            soundBase = 1196;
        } else {
            messageArray = AWAKENING_MESSAGES;
            soundBase = 1194;
        }
        
        String message = messageArray[Utils.getRandom(messageArray.length)];
        npc.setNextForceTalk(new ForceTalk(message));
        npc.playSound(soundBase + Utils.getRandom(2), 2); // Shadow-themed sound effects
    }
    
    /**
     * Perform intelligent attack selection based on range and phase
     */
    private void performIntelligentAttackSelection(NPC npc, Entity target, NPCCombatDefinitions defs) {
        // Calculate distance to target
        boolean inMeleeRange = Utils.isOnRange(target.getX(), target.getY(), 
                                             npc.getX(), npc.getY(), npc.getSize(), MELEE_RANGE);
        
        boolean inMagicRange = Utils.isOnRange(target.getX(), target.getY(), 
                                             npc.getX(), npc.getY(), npc.getSize(), MAGIC_RANGE);
        
        // Special darkness attack chance
        if (Utils.getRandom(DARKNESS_ATTACK_CHANCE) == 0) {
            performSpecialDarknessAttack(npc, target, defs);
            return;
        }
        
        // Range-based attack selection with enhanced intelligence
        if (inMeleeRange) {
            // Close range - prefer melee but occasionally use magic for surprise
            if (Utils.getRandom(4) == 0) { // 25% chance for surprise magic at close range
                performEnhancedMagicAttack(npc, target, defs);
                magicAttackStreak++;
                meleeAttackStreak = 0;
            } else {
                performEnhancedMeleeAttack(npc, target, defs);
                meleeAttackStreak++;
                magicAttackStreak = 0;
            }
        } else if (inMagicRange) {
            // Medium range - prefer magic but can close distance for melee
            if (Utils.getRandom(3) == 0) { // 33% chance to close distance for melee
                performEnhancedMeleeAttack(npc, target, defs);
                meleeAttackStreak++;
                magicAttackStreak = 0;
            } else {
                performEnhancedMagicAttack(npc, target, defs);
                magicAttackStreak++;
                meleeAttackStreak = 0;
            }
        } else {
            // Long range - magic only
            performEnhancedMagicAttack(npc, target, defs);
            magicAttackStreak++;
            meleeAttackStreak = 0;
        }
    }
    
    /**
     * Perform enhanced melee attack with Boss Balancer scaling
     */
    private void performEnhancedMeleeAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
        npc.setNextAnimation(new Animation(2731)); // Dark Beast melee animation
        
        // Get Boss Balancer stats
        int darkBeastTier = determineDarkBeastTier(npc, defs);
        int baseMaxHit = getBaseMaxHit(npc, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, darkBeastTier, false); // Melee attack
        
        // Apply player scaling if target is a player
        if (target instanceof Player) {
            tierScaledMaxHit = applyPlayerLevelScaling(tierScaledMaxHit, (Player) target, darkBeastTier);
        }
        
        // Calculate final damage safely
        Hit meleeHit = calculateSafeMeleeDamage(npc, tierScaledMaxHit, target);
        delayHit(npc, 0, target, meleeHit);
        
        // Add shadow effect to melee attacks
        target.setNextGraphics(new Graphics(2140)); // Dark impact effect
    }
    
    /**
     * Perform enhanced magic attack with Boss Balancer scaling
     */
    private void performEnhancedMagicAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
        npc.setNextAnimation(new Animation(2731)); // Dark Beast magic animation
        
        // Get Boss Balancer stats
        int darkBeastTier = determineDarkBeastTier(npc, defs);
        int baseMaxHit = getBaseMaxHit(npc, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, darkBeastTier, true); // Magic attack
        
        // Apply player scaling if target is a player
        if (target instanceof Player) {
            tierScaledMaxHit = applyPlayerLevelScaling(tierScaledMaxHit, (Player) target, darkBeastTier);
        }
        
        // Calculate final damage safely
        int finalDamage = calculateSafeMagicDamage(npc, tierScaledMaxHit);
        
        // Send shadow projectile
        World.sendProjectile(npc, target, 2181, 41, 16, 41, 35, 16, 0);
        delayHit(npc, 2, target, getMagicHit(npc, finalDamage));
    }
    
    /**
     * Perform special darkness attack with enhanced effects
     */
    private void performSpecialDarknessAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
        npc.setNextAnimation(new Animation(2731));
        darknessAttacksUsed++;
        
        // Get Boss Balancer stats for special attack
        int darkBeastTier = determineDarkBeastTier(npc, defs);
        int baseMaxHit = getBaseMaxHit(npc, defs);
        int tierScaledMaxHit = applyBossTierScaling(baseMaxHit, darkBeastTier, true); // Magic-based special
        
        // Special attack does 90% damage but has additional effects
        int specialDamage = (int)(tierScaledMaxHit * 0.90);
        
        if (target instanceof Player) {
            specialDamage = applyPlayerLevelScaling(specialDamage, (Player) target, darkBeastTier);
        }
        
        // Enhanced darkness projectile
        World.sendProjectile(npc, target, 2181, 41, 16, 41, 35, 16, 0);
        int finalDamage = calculateSafeMagicDamage(npc, specialDamage);
        delayHit(npc, 2, target, getMagicHit(npc, finalDamage));
        
        // Apply darkness debuff effect
        if (target instanceof Player) {
            applyDarknessDebuff((Player) target, darkBeastTier);
        }
        
        // Enhanced shadow graphics
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                target.setNextGraphics(new Graphics(2143)); // Enhanced darkness effect
                this.stop();
            }
        }, 2);
    }
    
    /**
     * Apply darkness debuff effect scaled by tier
     */
    private void applyDarknessDebuff(final Player player, final int tier) {
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Temporary accuracy reduction based on tier
                int accuracyDrain = Math.max(3, tier); // Minimum 3, scales with tier
                
                // Temporarily reduce attack and strength levels
                int attackLevel = player.getSkills().getLevel(Skills.ATTACK);
                int strengthLevel = player.getSkills().getLevel(Skills.STRENGTH);
                
                player.getSkills().set(Skills.ATTACK, Math.max(1, attackLevel - accuracyDrain));
                player.getSkills().set(Skills.STRENGTH, Math.max(1, strengthLevel - accuracyDrain));
                
                player.getPackets().sendGameMessage("<col=800080>[Darkness Debuff]: The shadows weaken your combat prowess temporarily!");
                
                // Restore stats after 10 seconds
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        player.getSkills().restoreSkills();
                        player.getPackets().sendGameMessage("<col=00FFFF>[Shadow Lifted]: Your combat stats have been restored.");
                        this.stop();
                    }
                }, 17); // ~10 seconds
                
                this.stop();
            }
        }, 2);
    }
    
    /**
     * Determine Dark Beast's tier based on Boss Balancer system
     */
    private int determineDarkBeastTier(NPC npc, NPCCombatDefinitions defs) {
        try {
            int hp = defs.getHitpoints();
            int maxHit = defs.getMaxHit();
            
            // Estimate tier based on Boss Balancer HP/damage ranges for Hybrid boss
            if (hp >= 4500 && hp <= 8000 && maxHit >= 55 && maxHit <= 100) {
                return 5; // Expert tier
            } else if (hp >= 3000 && hp <= 5500 && maxHit >= 40 && maxHit <= 75) {
                return 4; // Advanced tier
            } else if (hp >= 6500 && hp <= 10500 && maxHit >= 70 && maxHit <= 125) {
                return 6; // Master tier
            }
            
            return DARK_BEAST_DEFAULT_TIER; // Default to Expert tier
        } catch (Exception e) {
            return DARK_BEAST_DEFAULT_TIER;
        }
    }
    
    /**
     * Get base max hit safely (NULL SAFE)
     */
    private int getBaseMaxHit(NPC npc, NPCCombatDefinitions defs) {
        try {
            int maxHit = defs.getMaxHit();
            return maxHit > 0 ? maxHit : 75; // Default Dark Beast damage if invalid
        } catch (Exception e) {
            return 75; // Fallback Dark Beast damage
        }
    }
    
    /**
     * Apply Boss Balancer tier scaling for hybrid boss
     */
    private int applyBossTierScaling(int baseMaxHit, int tier, boolean isMagicAttack) {
        // Boss Balancer tier scaling: 15% increase per tier above 1
        double tierMultiplier = 1.0 + (tier - 1) * 0.15;
        
        // Hybrid boss type modifier - balanced damage for both attack types
        double typeModifier = 1.0; // Standard damage for hybrid
        
        // Slight preference for magic attacks (Dark Beasts are magical creatures)
        if (isMagicAttack) {
            typeModifier = 1.05; // 5% bonus for magic attacks
        }
        
        return (int) (baseMaxHit * tierMultiplier * typeModifier);
    }
    
    /**
     * Apply player level scaling for balanced slayer experience
     */
    private int applyPlayerLevelScaling(int damage, Player player, int tier) {
        int playerCombatLevel = player.getSkills().getCombatLevel();
        int recommendedLevel = tier * 10 + 50; // Tier 5 = level 100 recommended
        
        // Scale damage based on player level vs recommended
        if (playerCombatLevel < recommendedLevel) {
            double scaleFactor = (double) playerCombatLevel / recommendedLevel;
            scaleFactor = Math.max(0.50, scaleFactor); // Minimum 50% damage
            damage = (int) (damage * scaleFactor);
        }
        
        // Ensure damage is reasonable for Dark Beast (mid-tier slayer monster)
        damage = Math.max(15, Math.min(damage, 150)); // Cap between 15-150
        
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
                    double bonusMultiplier = 1.0 + (magicBonus / 2000.0); // Moderate scaling
                    damage = (int) (damage * Math.min(bonusMultiplier, 1.4)); // Cap at 40% bonus
                }
            }
            
            return Math.max(0, Math.min(damage, maxHit));
            
        } catch (Exception e) {
            return Utils.getRandom(maxHit / 2) + (maxHit / 4);
        }
    }
    
    /**
     * Calculate safe melee damage with bonuses (NULL SAFE)
     */
    private Hit calculateSafeMeleeDamage(NPC npc, int maxHit, Entity target) {
        try {
            int damage = Utils.getRandom(maxHit + 1);
            
            // Apply melee bonuses if they exist
            int[] bonuses = NPCBonuses.getBonuses(npc.getId());
            if (bonuses != null && bonuses.length > 2) {
                int meleeBonus = Math.max(bonuses[0], Math.max(bonuses[1], bonuses[2]));
                if (meleeBonus > 0) {
                    double bonusMultiplier = 1.0 + (meleeBonus / 2000.0); // Moderate scaling
                    damage = (int) (damage * Math.min(bonusMultiplier, 1.4)); // Cap at 40% bonus
                }
            }
            
            damage = Math.max(0, Math.min(damage, maxHit));
            return getMeleeHit(npc, damage);
            
        } catch (Exception e) {
            return getMeleeHit(npc, Utils.getRandom(maxHit / 2) + (maxHit / 4));
        }
    }
    
    /**
     * Provide strategic slayer guidance based on combat performance
     */
    private void provideSlayerGuidance(Player player, NPC npc, NPCCombatDefinitions defs) {
        if (!shouldGiveHint()) return;
        
        double healthPercent = (double) npc.getHitpoints() / defs.getHitpoints();
        
        // Shadow step frequency guidance
        if (shadowStepCount >= 3) {
            player.getPackets().sendGameMessage("<col=800080>[Shadow Tactics]: Multiple shadow steps detected! Dark Beasts become more mobile when injured!");
            shadowStepCount = 0;
            return;
        }
        
        // Attack pattern guidance
        if (magicAttackStreak >= 4) {
            player.getPackets().sendGameMessage("<col=00FFFF>[Combat Pattern]: Dark Beast favoring magic attacks. Consider magic protection or closing distance!");
            magicAttackStreak = 0;
            return;
        }
        
        if (meleeAttackStreak >= 4) {
            player.getPackets().sendGameMessage("<col=00FFFF>[Combat Pattern]: Dark Beast using frequent melee. Create distance to force magic attacks!");
            meleeAttackStreak = 0;
            return;
        }
        
        // Darkness attack guidance
        if (darknessAttacksUsed >= 2) {
            player.getPackets().sendGameMessage("<col=FFFF00>[Special Ability]: Multiple darkness attacks used. These can temporarily weaken your combat stats!");
            darknessAttacksUsed = 0;
            return;
        }
        
        // Phase-specific slayer guidance
        if (healthPercent > 0.75) {
            player.getPackets().sendGameMessage("<col=00FFFF>[Slayer Strategy]: Dark Beast at full strength. Maintain optimal range for your combat style!");
        } else if (healthPercent > 0.50) {
            player.getPackets().sendGameMessage("<col=FFFF00>[Shadow Phase]: Dark Beast becomes more elusive. Expect increased teleportation!");
        } else if (healthPercent > 0.25) {
            player.getPackets().sendGameMessage("<col=FF8000>[Darkness Rising]: Enhanced shadow magic and frequent special attacks incoming!");
        } else {
            player.getPackets().sendGameMessage("<col=FF0000>[Final Shadow]: Dark Beast fights desperately with maximum shadow power!");
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
     * Check if should give shadow step warning (with cooldown)
     */
    private boolean shouldGiveShadowWarning() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastShadowWarningTime >= SHADOW_WARNING_COOLDOWN) {
            lastShadowWarningTime = currentTime;
            return true;
        }
        return false;
    }
    
    /**
     * Debug method for testing damage scaling and boss balancer integration
     */
    public String getDamageScalingInfo(int combatLevel, boolean isMagic) {
        int tier = DARK_BEAST_DEFAULT_TIER;
        int baseMaxHit = 75;
        int tierScaled = applyBossTierScaling(baseMaxHit, tier, isMagic);
        String attackType = isMagic ? "Magic" : "Melee";
        
        return String.format("Dark Beast Tier: %d, Base: %d, %s Scaled: %d", 
                           tier, baseMaxHit, attackType, tierScaled);
    }
    
    /**
     * Get combat statistics for performance analysis
     */
    public String getCombatStats() {
        return String.format("Shadow Steps: %d, Darkness Attacks: %d, Magic Streak: %d, Melee Streak: %d", 
                           shadowStepCount, darknessAttacksUsed, magicAttackStreak, meleeAttackStreak);
    }

    @Override
    public Object[] getKeys() {
        return new Object[] { 2783 }; // Dark Beast NPC ID
    }
}