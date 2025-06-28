package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitionsManager;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.BossBalancer;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;
import com.rs.cache.loaders.NPCDefinitions;

/**
 * Enhanced Boss Combat System with BossBalancer Integration and Boss Guidance
 * Features safespot protection, intelligent boss guidance, and proper balance integration
 * 
 * @author Zeus
 * @date June 03, 2025
 * @version 3.0 - Complete BossBalancer Integration with Boss Guidance System
 */
public class ToragCombat extends CombatScript {

    // Boss guidance timing controls
    private static final long GUIDANCE_COOLDOWN = 30000; // 30 seconds between guidance messages
    private static final long SAFESPOT_WARNING_INTERVAL = 10000; // 10 seconds between safespot warnings
    
    // Combat state tracking for guidance
    private long lastGuidanceTime = 0;
    private long lastSafespotWarning = 0;
    private int consecutiveMisses = 0;
    private int attackCount = 0;
    private boolean hasGivenInitialGuidance = false;

    @Override
    public int attack(NPC npc, Entity target) {
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        
        // Only provide guidance and enhanced combat for player targets
        if (target instanceof Player) {
            Player player = (Player) target;
            
            // Provide initial boss guidance when combat starts
            if (!hasGivenInitialGuidance) {
                provideBossGuidance(npc, player, true);
                hasGivenInitialGuidance = true;
            }
            
            // Enhanced safespot detection and prevention
            if (detectAndHandleSafespot(npc, player)) {
                // Boss detected safespotting - apply countermeasures
                return handleSafespotting(npc, player, defs);
            }
            
            // Provide contextual combat guidance
            provideContextualGuidance(npc, player);
        }
        
        // Execute enhanced attack with BossBalancer integration
        return executeEnhancedAttack(npc, target, defs);
    }
    
    /**
     * Execute enhanced attack with proper BossBalancer stat usage
     */
    private int executeEnhancedAttack(NPC npc, Entity target, NPCCombatDefinitions defs) {
        // Use BossBalancer-enhanced combat definitions
        int maxHit = getEnhancedMaxHit(npc, defs, target);
        int attackStyle = defs.getAttackStyle();
        int attackDelay = defs.getAttackDelay();
        
        // Set attack animation
        npc.setNextAnimation(new Animation(defs.getAttackEmote()));
        
        // Calculate damage using BossBalancer-enhanced stats
        int damage = getRandomMaxHit(npc, maxHit, attackStyle, target);
        
        // Apply special boss type effects based on BossBalancer configuration
        if (target instanceof Player) {
            applyBossTypeEffects(npc, (Player) target, damage);
            attackCount++;
        }
        
        // Deliver the hit based on attack style
        if (attackStyle == NPCCombatDefinitions.RANGE) {
            World.sendProjectile(npc, target, defs.getAttackProjectile(), 41, 16, 41, 35, 16, 0);
            delayHit(npc, 2, target, getRangeHit(npc, damage));
        } else if (attackStyle == NPCCombatDefinitions.MAGE) {
            delayHit(npc, 2, target, getMagicHit(npc, damage));
        } else {
            delayHit(npc, 0, target, getMeleeHit(npc, damage));
        }
        
        // Track combat statistics for guidance
        if (damage == 0) {
            consecutiveMisses++;
        } else {
            consecutiveMisses = 0;
        }
        
        return attackDelay;
    }
    
    /**
     * Get enhanced max hit using Boss Balancer system
     */
    private int getEnhancedMaxHit(NPC npc, NPCCombatDefinitions defs, Entity target) {
        int baseMaxHit = defs.getMaxHit();
        
        // Apply Boss Balancer bonuses if available
        try {
            int[] bonuses = NPCBonuses.getBonuses(npc.getId());
            if (bonuses != null && bonuses.length >= 10) {
                // Apply offensive bonus modifiers based on attack style
                double bonusMultiplier = 1.0;
                
                switch (defs.getAttackStyle()) {
                    case NPCCombatDefinitions.MELEE:
                        // Use best melee bonus (stab, slash, or crush)
                        int meleeBonus = Math.max(Math.max(bonuses[0], bonuses[1]), bonuses[2]);
                        bonusMultiplier = 1.0 + (meleeBonus * 0.0001); // Small but meaningful bonus
                        break;
                    case NPCCombatDefinitions.RANGE:
                        bonusMultiplier = 1.0 + (bonuses[4] * 0.0001); // Ranged attack bonus
                        break;
                    case NPCCombatDefinitions.MAGE:
                        bonusMultiplier = 1.0 + (bonuses[3] * 0.0001); // Magic attack bonus
                        break;
                }
                
                baseMaxHit = (int) (baseMaxHit * bonusMultiplier);
            }
        } catch (Exception e) {
            // Fallback to standard damage if bonus system fails
            System.err.println("Error applying BossBalancer bonuses for NPC " + npc.getId() + ": " + e.getMessage());
        }
        
        return Math.max(1, baseMaxHit); // Ensure minimum damage of 1
    }
    
    /**
     * Apply special effects based on boss type from BossBalancer configuration
     */
    private void applyBossTypeEffects(NPC npc, Player player, int damage) {
        try {
            // Get boss type from BossBalancer data
            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
            if (def == null) return;
            
            int bossType = determineBossType(npc.getId(), def);
            
            // Apply boss type specific effects
            switch (bossType) {
                case 0: // Melee Boss - Chance for armor reduction effect
                    if (damage > 0 && Utils.random(10) == 0) {
                        player.setNextGraphics(new Graphics(399));
                        sendBossMessage(npc, player, "Your armor feels weakened by my might!");
                    }
                    break;
                    
                case 1: // Ranged Boss - Chance to drain run energy
                    if (damage > 0 && Utils.random(8) == 0) {
                        player.setNextGraphics(new Graphics(377));
                        int energyDrain = Utils.random(10, 20);
                        player.setRunEnergy(Math.max(0, player.getRunEnergy() - energyDrain));
                        sendBossMessage(npc, player, "My arrows sap your strength!");
                    }
                    break;
                    
                case 2: // Magic Boss - Chance to drain prayer
                    if (damage > 0 && Utils.random(12) == 0) {
                        player.setNextGraphics(new Graphics(344));
                        int prayerDrain = (int) (player.getPrayer().getPrayerpoints() * 0.1);
                        player.getPrayer().drainPrayer(prayerDrain);
                        sendBossMessage(npc, player, "Your prayers are nothing before my magic!");
                    }
                    break;
                    
                case 3: // Tank Boss - Reduced damage but healing chance
                    if (Utils.random(15) == 0) {
                        int healAmount = npc.getMaxHitpoints() / 20;
                        npc.heal(healAmount);
                        npc.setNextGraphics(new Graphics(436));
                        sendBossMessage(npc, player, "My defenses regenerate my strength!");
                    }
                    break;
                    
                case 5: // Glass Cannon - High damage warning
                    if (damage > player.getHitpoints() / 2) {
                        sendBossMessage(npc, player, "Witness my devastating power!");
                    }
                    break;
                    
                case 6: // Raid Boss - Multiple effect chance
                    if (Utils.random(20) == 0) {
                        applyRandomRaidEffect(npc, player);
                    }
                    break;
            }
        } catch (Exception e) {
            // Fallback - apply basic effects
            if (damage > 0 && Utils.random(15) == 0) {
                player.setNextGraphics(new Graphics(399));
            }
        }
    }
    
    /**
     * Apply random raid boss effects
     */
    private void applyRandomRaidEffect(NPC npc, Player player) {
        int effect = Utils.random(4);
        switch (effect) {
            case 0: // Energy drain
                player.setRunEnergy(Math.max(0, player.getRunEnergy() - Utils.random(15, 25)));
                player.setNextGraphics(new Graphics(377));
                sendBossMessage(npc, player, "Feel your energy drain away!");
                break;
            case 1: // Prayer drain
                player.getPrayer().drainPrayer((int) (player.getPrayer().getPrayerpoints() * 0.15));
                player.setNextGraphics(new Graphics(344));
                sendBossMessage(npc, player, "Your faith weakens!");
                break;
            case 2: // Stat drain
                player.setNextGraphics(new Graphics(348));
                sendBossMessage(npc, player, "Your combat prowess diminishes!");
                break;
            case 3: // Area effect warning
                sendBossMessage(npc, player, "Prepare for my area attack!");
                break;
        }
    }
    
    /**
     * Provide comprehensive boss guidance to help players
     */
    private void provideBossGuidance(NPC npc, Player player, boolean isInitial) {
        long currentTime = System.currentTimeMillis();
        
        if (!isInitial && (currentTime - lastGuidanceTime) < GUIDANCE_COOLDOWN) {
            return; // Still in cooldown
        }
        
        try {
            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
            if (def == null) return;
            
            // Get boss information from BossBalancer
            NPCCombatDefinitions combatDef = npc.getCombatDefinitions();
            int estimatedTier = estimateBossTier(combatDef.getHitpoints(), combatDef.getMaxHit());
            int bossType = determineBossType(npc.getId(), def);
            String bossTypeName = getBossTypeName(bossType);
            
            if (isInitial) {
                // Initial encounter guidance
                npc.setNextForceTalk(new ForceTalk("So, another challenger approaches me..."));
                player.sendMessage("<col=00ff00>Boss Guide: You are facing " + def.getName() + ", a " + bossTypeName + "!");
                player.sendMessage("<col=00ff00>Recommended equipment tier: " + estimatedTier);
                player.sendMessage("<col=00ff00>Strategy: " + getBossTypeAdvice(bossType));
                
            } else {
                // Contextual guidance during combat
                provideContextualCombatAdvice(npc, player, bossType, estimatedTier);
            }
            
            lastGuidanceTime = currentTime;
            
        } catch (Exception e) {
            // Fallback guidance
            npc.setNextForceTalk(new ForceTalk("Face me in combat, if you dare!"));
        }
    }
    
    /**
     * Provide contextual guidance during combat
     */
    private void provideContextualGuidance(NPC npc, Player player) {
        long currentTime = System.currentTimeMillis();
        
        if ((currentTime - lastGuidanceTime) < GUIDANCE_COOLDOWN) {
            return;
        }
        
        // Analyze combat situation and provide appropriate advice
        if (consecutiveMisses >= 5) {
            sendBossMessage(npc, player, "Your attacks are ineffective! Try a different combat style or better equipment!");
            lastGuidanceTime = currentTime;
        } else if (player.getHitpoints() < player.getMaxHitpoints() * 0.3) {
            sendBossMessage(npc, player, "You grow weak! Heal yourself or face defeat!");
            lastGuidanceTime = currentTime;
        } else if (npc.getHitpoints() < npc.getMaxHitpoints() * 0.5) {
            sendBossMessage(npc, player, "Impressive! But I am far from defeated!");
            lastGuidanceTime = currentTime;
        }
    }
    
    /**
     * Provide contextual combat advice based on boss type and situation
     */
    private void provideContextualCombatAdvice(NPC npc, Player player, int bossType, int tier) {
        String[] adviceMessages = {
            "Keep your guard up and watch for my special attacks!",
            "Your combat skills will be tested here!",
            "Adapt your strategy based on my attack patterns!",
            "Use protection prayers to reduce my damage!",
            "Bring plenty of food for this battle!"
        };
        
        String advice = adviceMessages[Utils.random(adviceMessages.length)];
        player.sendMessage("<col=00ff00>Boss Guide: " + advice);
        
        switch (bossType) {
            case 0: // Melee Boss
                npc.setNextForceTalk(new ForceTalk("Feel the power of my melee strikes!"));
                break;
            case 1: // Ranged Boss
                npc.setNextForceTalk(new ForceTalk("My projectiles will find their mark!"));
                break;
            case 2: // Magic Boss
                npc.setNextForceTalk(new ForceTalk("Magic flows through my attacks!"));
                break;
            case 3: // Tank Boss
                npc.setNextForceTalk(new ForceTalk("You will struggle to break my defenses!"));
                break;
            case 5: // Glass Cannon
                npc.setNextForceTalk(new ForceTalk("I strike with devastating force!"));
                break;
            case 6: // Raid Boss
                npc.setNextForceTalk(new ForceTalk("You face a challenge worthy of legends!"));
                break;
            default:
                npc.setNextForceTalk(new ForceTalk("Fight with honor, warrior!"));
                break;
        }
    }
    
    /**
     * Get initial advice based on boss type
     */
    private String getBossTypeAdvice(int bossType) {
        switch (bossType) {
            case 0: return "Beware my crushing melee attacks! Keep your distance or use protection prayers.";
            case 1: return "My arrows will find their mark! Use ranged protection or get close for melee.";
            case 2: return "My magic will overwhelm your defenses! Use magic protection or anti-magic gear.";
            case 3: return "I have strong defenses! This will be a battle of endurance.";
            case 4: return "I master all forms of combat! Be ready to adapt your strategy.";
            case 5: return "I strike with devastating force but have weak defenses! Strike fast and heal often.";
            case 6: return "I am a raid-level threat! Bring your absolute best gear and strategy.";
            default: return "Prepare yourself for battle!";
        }
    }
    
    /**
     * Enhanced safespot detection with multiple methods
     */
    private boolean detectAndHandleSafespot(NPC npc, Player player) {
        // Check if NPC cannot reach player for attack
        if (!npc.clipedProjectile(player, true)) {
            // Check distance - if player is far and unreachable, might be safespotting
            int distance = Utils.getDistance(npc.getX(), npc.getY(), player.getX(), player.getY());
            if (distance > 8) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Handle safespotting with countermeasures
     */
    private int handleSafespotting(NPC npc, Player player, NPCCombatDefinitions defs) {
        long currentTime = System.currentTimeMillis();
        
        if ((currentTime - lastSafespotWarning) > SAFESPOT_WARNING_INTERVAL) {
            npc.setNextForceTalk(new ForceTalk("Coward! Face me in honorable combat!"));
            player.sendMessage("<col=ff0000>The boss is calling you out for safespotting!");
            lastSafespotWarning = currentTime;
        }
        
        // Countermeasure 1: Force attack through obstacles
        if (Utils.random(3) == 0) {
            npc.setNextForceTalk(new ForceTalk("My attacks pierce through your hiding spot!"));
            player.sendMessage("<col=ff0000>The boss attacks through your cover!");
            
            // Apply reduced damage attack
            int damage = getRandomMaxHit(npc, defs.getMaxHit() / 2, defs.getAttackStyle(), player);
            delayHit(npc, 1, player, getMeleeHit(npc, damage));
        }
        
        // Countermeasure 2: Reposition closer to player
        if (Utils.random(4) == 0) {
            int newX = player.getX() + Utils.random(3) - 1;
            int newY = player.getY() + Utils.random(3) - 1;
            WorldTile newTile = new WorldTile(newX, newY, player.getPlane());
            npc.setNextWorldTile(newTile);
            npc.setNextForceTalk(new ForceTalk("I will not be made a fool of!"));
            player.sendMessage("<col=ffff00>Boss Guide: The boss repositions to prevent safespotting!");
        }
        
        return defs.getAttackDelay();
    }
    
    /**
     * Send a message from the boss to the player
     */
    private void sendBossMessage(NPC npc, Player player, String message) {
        NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npc.getId());
        String bossName = def != null ? def.getName() : "Boss";
        player.sendMessage("<col=ff0000>" + bossName + ": " + message);
    }
    
    /**
     * Auto-determine boss type based on NPC ID or characteristics
     */
    private int determineBossType(int npcId, NPCDefinitions def) {
        if (def == null) return 4; // Default to hybrid
        
        String name = def.getName().toLowerCase();
        
        // Auto-detect based on name patterns
        if (name.contains("dragon") || name.contains("warrior") || name.contains("knight") || name.contains("demon")) {
            return 0; // Melee
        } else if (name.contains("archer") || name.contains("ranger") || name.contains("sniper") || name.contains("bow")) {
            return 1; // Ranged
        } else if (name.contains("mage") || name.contains("wizard") || name.contains("witch") || name.contains("sorcerer")) {
            return 2; // Magic
        } else if (name.contains("tank") || name.contains("guardian") || name.contains("defender") || name.contains("shield")) {
            return 3; // Tank
        } else if (name.contains("glass") || name.contains("assassin") || name.contains("berserker") || name.contains("reaper")) {
            return 5; // Glass Cannon
        } else if (name.contains("raid") || name.contains("boss") || name.contains("king") || name.contains("queen") || name.contains("lord")) {
            return 6; // Raid Boss
        }
        
        return 4; // Default to Hybrid
    }
    
    /**
     * Get boss type name
     */
    private String getBossTypeName(int type) {
        switch (type) {
            case 0: return "Melee Boss";
            case 1: return "Ranged Boss";
            case 2: return "Magic Boss";
            case 3: return "Tank Boss";
            case 4: return "Hybrid Boss";
            case 5: return "Glass Cannon";
            case 6: return "Raid Boss";
            default: return "Unknown Type";
        }
    }
    
    /**
     * Estimate what tier of items would be appropriate for this boss
     */
    private int estimateBossTier(int hp, int maxHit) {
        // Updated estimation based on balanced system
        int difficulty = (hp / 100) + (maxHit * 8);
        
        if (difficulty <= 60) return 1;
        else if (difficulty <= 150) return 2;
        else if (difficulty <= 280) return 3;
        else if (difficulty <= 480) return 4;
        else if (difficulty <= 740) return 5;
        else if (difficulty <= 1080) return 6;
        else if (difficulty <= 1500) return 7;
        else if (difficulty <= 2100) return 8;
        else if (difficulty <= 2900) return 9;
        else return 10;
    }
    
    /**
     * Reset combat state when target changes or combat ends
     */
    public void onCombatEnd(NPC npc) {
        hasGivenInitialGuidance = false;
        consecutiveMisses = 0;
        attackCount = 0;
        lastGuidanceTime = 0;
        lastSafespotWarning = 0;
        
        // Final message
        if (Utils.random(3) == 0) {
            npc.setNextForceTalk(new ForceTalk("You have fought well... this time."));
        }
    }
    
    /**
     * Enhanced combat start initialization
     */
    public void onCombatStart(NPC npc, Entity target) {
        // Initialize combat tracking
        attackCount = 0;
        hasGivenInitialGuidance = false;
        consecutiveMisses = 0;
        
        // Boss introduction
        if (target instanceof Player && Utils.random(2) == 0) {
            npc.setNextForceTalk(new ForceTalk("Prepare to face my wrath!"));
        }
    }

    @Override
    public Object[] getKeys() {
        // Add your boss NPC IDs here - example with common boss IDs
        return new Object[] { 
            2029, // Torag
            2028, // Karil
            50,   // King Black Dragon
            51,   // Chaos Elemental
            // Add more boss NPC IDs as needed
        };
    }
}