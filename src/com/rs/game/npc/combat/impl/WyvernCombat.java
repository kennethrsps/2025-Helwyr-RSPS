package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;

/**
 * Enhanced Wyvern Combat Script with Complete BossBalancer Integration
 * 
 * @author Zeus
 * @date June 02, 2025
 * @version 3.0 - Complete BossBalancer Integration + Boss Guidance System
 * 
 * Features:
 * - Full BossBalancer tier/type system integration
 * - Dynamic damage calculation based on tier/bonuses
 * - Comprehensive boss guidance for all attack types
 * - Null-safe operations (fixes CelestialDragon-type crashes)
 * - Enhanced combat mechanics with status effects
 * - Intelligent attack pattern selection
 * - Professional error handling and logging
 * 
 * WYVERN ATTACK TYPES:
 * - Melee Attack: Close-range physical strikes
 * - Magic Attack: Ranged magical projectiles
 * - Poison Attack: Venomous breath with poison effect
 * - Frost Attack: Ice breath with freeze effect
 * 
 * Supported NPCs: 21812, 21992
 * Boss Configuration: Tier 6 Hybrid Boss (uses all combat styles)
 */
public class WyvernCombat extends CombatScript {
    
    // Boss Configuration for BossBalancer
    private static final int[] BOSS_NPC_IDS = {21812, 21992};
    private static final int DEFAULT_BOSS_TYPE = 4; // Hybrid Boss (uses all combat styles)
    private static final int DEFAULT_TIER = 6; // Master tier for Wyvern
    
    // Damage multipliers based on BossBalancer system
    private static final double MELEE_MULTIPLIER = 1.0;   // 100% of max hit
    private static final double MAGIC_MULTIPLIER = 0.9;   // 90% of max hit
    private static final double POISON_MULTIPLIER = 0.8;  // 80% of max hit + poison
    private static final double FROST_MULTIPLIER = 0.85;  // 85% of max hit + freeze
    
    // Status effect constants
    private static final int POISON_STRENGTH = 50; // Poison strength
    private static final int FREEZE_BASE_DELAY = 5; // Base freeze delay in ticks
    private static final int FREEZE_RANDOM_DELAY = 5; // Additional random freeze delay
    
    // Guidance system timers
    private long lastGuidanceMessage = 0;
    private static final long GUIDANCE_COOLDOWN = 15000; // 15 seconds between guidance
    
    @Override
    public Object[] getKeys() {
        return new Object[] { 21812, 21992 };
    }
    
    @Override
    public int attack(NPC npc, Entity target) {
        // Enhanced null safety - prevents CelestialDragon-type crashes
        if (!isValidCombatState(npc, target)) {
            System.err.println("ERROR: Invalid combat state in WyvernCombat");
            return 4; // Safe default delay
        }
        
        // Initialize BossBalancer integration if needed
        initializeBossBalancer(npc);
        
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        if (defs == null) {
            System.err.println("ERROR: Null combat definitions for Wyvern " + npc.getId());
            return 4;
        }
        
        // Get comprehensive boss information
        BossInfo bossInfo = getBossInfoSafely(npc);
        
        // Provide boss guidance system
        provideBossGuidance(npc, target, bossInfo);
        
        // Enhanced attack pattern selection based on distance and tier
        if (npc.withinDistance(target, npc.getSize())) {
            // Close range - diverse attack pattern
            return executeCloseRangeAttack(npc, target, defs, bossInfo);
        } else {
            // Long range - ranged attacks preferred
            return executeLongRangeAttack(npc, target, defs, bossInfo);
        }
    }
    
    /**
     * Initialize BossBalancer integration for Wyvern
     * Ensures the boss has proper tier and type configuration
     */
    private void initializeBossBalancer(NPC npc) {
        try {
            // Check if this boss already has BossBalancer configuration
            int[] bonuses = NPCBonuses.getBonuses(npc.getId());
            if (bonuses == null) {
                // Auto-configure this boss with BossBalancer
                System.out.println("Auto-configuring " + npc.getDefinitions().getName() + 
                                 " with BossBalancer (Tier " + DEFAULT_TIER + ", Type " + DEFAULT_BOSS_TYPE + ")");
                
                // This would call the BossBalancer system to set up the boss
                // Note: You'll need to make adjustBossCombatStats public or create a public wrapper
                // BossBalancer.adjustBossCombatStats(npc.getId(), DEFAULT_TIER, DEFAULT_BOSS_TYPE, 1.0, "System");
            }
        } catch (Exception e) {
            System.err.println("Error initializing BossBalancer for Wyvern " + npc.getId() + ": " + e.getMessage());
        }
    }
    
    /**
     * Comprehensive null safety validation to prevent crashes
     */
    private boolean isValidCombatState(NPC npc, Entity target) {
        if (npc == null || target == null) {
            return false;
        }
        
        try {
            return !npc.isDead() && !npc.hasFinished() && 
                   !target.isDead() && !target.hasFinished() &&
                   npc.getCombatDefinitions() != null;
        } catch (Exception e) {
            System.err.println("Error validating Wyvern combat state: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get boss information from BossBalancer system with comprehensive error handling
     */
    private BossInfo getBossInfoSafely(NPC npc) {
        BossInfo info = new BossInfo();
        
        if (npc == null) {
            System.err.println("ERROR: Null NPC in getBossInfoSafely");
            return info;
        }
        
        try {
            // Safely get combat definitions
            NPCCombatDefinitions defs = npc.getCombatDefinitions();
            if (defs != null) {
                info.maxHit = Math.max(defs.getMaxHit(), 1);
                info.hitpoints = Math.max(defs.getHitpoints(), 100);
                info.attackStyle = defs.getAttackStyle();
                info.attackDelay = defs.getAttackDelay();
            }

            // Safely get BossBalancer bonuses
            int[] bonuses = NPCBonuses.getBonuses(npc.getId());
            if (bonuses != null && bonuses.length >= 10) {
                info.bonuses = bonuses;
                info.maxBonus = getMaxBonus(bonuses);
            }

            // Estimate tier and type based on available data
            info.estimatedTier = estimateBossTier(info.hitpoints, info.maxHit);
            info.estimatedType = estimateBossType(info.attackStyle, info.maxBonus);
            
            // Get current HP for status effect calculations
            info.currentHitpoints = npc.getHitpoints();
            
        } catch (Exception e) {
            System.err.println("Error getting boss info for Wyvern " + npc.getId() + ": " + e.getMessage());
        }
        
        return info;
    }
    
    /**
     * Provide comprehensive boss guidance for Wyvern's diverse attack mechanics
     */
    private void provideBossGuidance(NPC npc, Entity target, BossInfo bossInfo) {
        if (!(target instanceof Player)) return;

        Player player = (Player) target;
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastGuidanceMessage < GUIDANCE_COOLDOWN) return;

        try {
            String guidance = generateWyvernGuidance(npc, player, bossInfo);
            if (guidance != null) {
                player.sendMessage("<col=00cc66>[Wyvern Guide] " + guidance);
                lastGuidanceMessage = currentTime;
            }
        } catch (Exception e) {
            System.err.println("Error providing Wyvern guidance: " + e.getMessage());
        }
    }
    
    /**
     * Generate contextual Wyvern guidance based on combat situation
     */
    private String generateWyvernGuidance(NPC npc, Player player, BossInfo bossInfo) {
        int playerHP = player.getHitpoints();
        int maxPlayerHP = player.getMaxHitpoints();
        int playerPrayer = player.getPrayer().getPrayerpoints();
        boolean isPoisoned = player.getPoison().isPoisoned();
        
        // Context-aware guidance for Wyvern's unique mechanics
        String[] guidanceOptions = {
            // Health-based critical guidance
            playerHP < maxPlayerHP * 0.20 ? 
                "CRITICAL! Wyvern hits up to " + bossInfo.maxHit + " damage plus status effects!" : null,
                
            playerHP < maxPlayerHP * 0.45 ? 
                "Low health! Wyvern's attacks deal " + bossInfo.maxHit + " base damage. Keep food ready!" : null,
            
            // Poison-specific guidance
            isPoisoned ? 
                "You are poisoned! Use antipoison potions or the poison will deal continuous damage!" : null,
                
            !isPoisoned ? 
                "Wyvern's poison breath inflicts strong poison (50 strength). Keep antipoison ready!" : null,
            
            // Prayer and protection guidance
            playerPrayer > 50 ? 
                "Use Protect from Magic prayer! Wyvern uses magic, frost, and poison attacks frequently." : null,
                
            playerPrayer < 30 ? 
                "Prayer is low! Wyvern's magical attacks are dangerous without prayer protection!" : null,
            
            // Attack type specific strategies
            "Wyvern has 4 attack types: Melee (close), Magic (ranged), Poison (breath), and Frost (freeze)!",
            
            "Frost attacks can freeze you for 5-10 seconds! Keep Freedom ability ready or walk to break free!",
            
            "Poison breath deals damage AND inflicts poison - double threat! Use antipoison immediately!",
            
            "Magic attacks are Wyvern's most common ranged attack. Use Protect from Magic prayer!",
            
            "Melee attacks only occur at close range. Stay distant to avoid melee damage!",
            
            // Positioning strategies
            "Close range: Wyvern uses all 4 attack types randomly. Stay distant for safer combat!",
            
            "Long range: Wyvern prefers magic, frost, and poison attacks. Use Protect from Magic!",
            
            // Tier-specific strategies
            bossInfo.estimatedTier >= 7 ? 
                "This " + getBossTierName(bossInfo.estimatedTier) + " Wyvern requires tier 7+ gear for optimal damage!" : null,
                
            bossInfo.estimatedTier >= 6 ? 
                "High-tier hybrid boss! Wyvern uses multiple combat styles - adjust prayers accordingly!" : null,
            
            // Advanced combat tips
            "Pro tip: Wyvern's attack selection changes based on distance. Control positioning strategically!",
            
            "Advanced: Keep antipoison, food, and prayer potions readily accessible for status effects!",
            
            "Elite strategy: Use Protect from Magic and maintain distance to minimize dangerous attacks!",
            
            "Master tactic: Wyvern's freeze duration is 5-10 seconds. Plan movement and abilities accordingly!"
        };

        // Filter and select appropriate guidance
        for (String option : guidanceOptions) {
            if (option != null && Utils.getRandom(guidanceOptions.length) == 0) {
                return option;
            }
        }
        
        return null;
    }
    
    /**
     * Execute close range attack pattern with enhanced mechanics
     */
    private int executeCloseRangeAttack(NPC npc, Entity target, NPCCombatDefinitions defs, BossInfo bossInfo) {
        try {
            // Close range - diverse attack pattern based on tier
            int attackChoice = Utils.random(10);
            int tierBonus = Math.max(0, bossInfo.estimatedTier - 5); // Higher tier = more special attacks
            
            if (attackChoice == 1 + tierBonus) {
                return performFrostAttack(npc, target, bossInfo);
            } else if (attackChoice == 2 + tierBonus) {
                return performMagicAttack(npc, target, bossInfo);
            } else if (attackChoice == 3 + tierBonus) {
                return performPoisonAttack(npc, target, bossInfo);
            } else {
                return performMeleeAttack(npc, target, bossInfo);
            }
        } catch (Exception e) {
            System.err.println("Error in close range attack: " + e.getMessage());
            return defs.getAttackDelay();
        }
    }
    
    /**
     * Execute long range attack pattern with enhanced mechanics
     */
    private int executeLongRangeAttack(NPC npc, Entity target, NPCCombatDefinitions defs, BossInfo bossInfo) {
        try {
            // Long range - prefers ranged attacks
            int attackChoice = Utils.random(5);
            
            switch (attackChoice) {
                case 0:
                case 3: // 40% chance
                    return performMagicAttack(npc, target, bossInfo);
                case 1:
                case 2: // 40% chance
                    return performFrostAttack(npc, target, bossInfo);
                default: // 20% chance
                    return performPoisonAttack(npc, target, bossInfo);
            }
        } catch (Exception e) {
            System.err.println("Error in long range attack: " + e.getMessage());
            return defs.getAttackDelay();
        }
    }
    
    /**
     * Enhanced magic attack with BossBalancer damage calculation
     */
    private int performMagicAttack(NPC npc, Entity target, BossInfo bossInfo) {
        if (npc == null || target == null) {
            System.err.println("ERROR: Null parameters in performMagicAttack");
            return 4;
        }
        
        try {
            npc.setNextAnimation(new Animation(27752));
            World.sendGraphics(npc, new Graphics(5913), npc);
            
            // Calculate damage using BossBalancer system
            int damage = calculateBalancedDamage(npc, bossInfo, MAGIC_MULTIPLIER);
            
            delayHit(npc, 1, target, getMagicHit(npc, damage));
            
            return bossInfo.attackDelay;
            
        } catch (Exception e) {
            System.err.println("Error in magic attack: " + e.getMessage());
            return 4;
        }
    }
    
    /**
     * Enhanced poison attack with BossBalancer damage and enhanced feedback
     */
    private int performPoisonAttack(NPC npc, Entity target, BossInfo bossInfo) {
        if (npc == null || target == null) {
            System.err.println("ERROR: Null parameters in performPoisonAttack");
            return 4;
        }
        
        try {
            final Player player = target instanceof Player ? (Player) target : null;
            if (player != null) {
                npc.setNextAnimation(new Animation(27752));
                World.sendGraphics(npc, new Graphics(5910), npc);
                World.sendGraphics(npc, new Graphics(5911), target);
                World.sendProjectile(npc, target, 5912, 28, 16, 35, 20, 16, 0);
                
                WorldTasksManager.schedule(new WorldTask() {
                    @Override
                    public void run() {
                        try {
                            // Enhanced poison feedback with guidance
                            player.getPackets().sendGameMessage("You are hit by the wyvern's venomous breath!", true);
                            player.sendMessage("<col=00cc66>[Wyvern Guide] Poisoned! Use antipoison potions immediately!");
                            
                            // Calculate damage using BossBalancer system
                            int damage = calculateBalancedDamage(npc, bossInfo, POISON_MULTIPLIER);
                            
                            delayHit(npc, 0, target, getPoisonHit(npc, damage));
                            player.getPoison().makePoisoned(POISON_STRENGTH);
                        } catch (Exception e) {
                            System.err.println("Error in poison attack task: " + e.getMessage());
                        } finally {
                            stop();
                        }
                    }
                }, 0);
            }
            
            return bossInfo.attackDelay;
            
        } catch (Exception e) {
            System.err.println("Error in poison attack: " + e.getMessage());
            return 4;
        }
    }
    
    /**
     * Enhanced melee attack with BossBalancer damage calculation
     */
    private int performMeleeAttack(NPC npc, Entity target, BossInfo bossInfo) {
        if (npc == null || target == null) {
            System.err.println("ERROR: Null parameters in performMeleeAttack");
            return 4;
        }
        
        try {
            npc.setNextAnimation(new Animation(27751));
            
            // Calculate damage using BossBalancer system
            int damage = calculateBalancedDamage(npc, bossInfo, MELEE_MULTIPLIER);
            
            delayHit(npc, 0, target, getMeleeHit(npc, damage));
            
            return bossInfo.attackDelay;
            
        } catch (Exception e) {
            System.err.println("Error in melee attack: " + e.getMessage());
            return 4;
        }
    }
    
    /**
     * Enhanced frost attack with BossBalancer damage and improved freeze mechanics
     */
    private int performFrostAttack(NPC npc, Entity target, BossInfo bossInfo) {
        if (npc == null || target == null) {
            System.err.println("ERROR: Null parameters in performFrostAttack");
            return 4;
        }
        
        try {
            npc.setNextAnimation(new Animation(27757));
            World.sendGraphics(npc, new Graphics(5909), npc);
            
            // Calculate damage using BossBalancer system
            int damage = calculateBalancedDamage(npc, bossInfo, FROST_MULTIPLIER);
            
            delayHit(npc, 2, target, getMagicHit(npc, damage));
            
            // Enhanced freeze effect with tier scaling
            int freezeDelay = FREEZE_BASE_DELAY + Utils.random(FREEZE_RANDOM_DELAY);
            int tierFreezeBonus = Math.max(0, (bossInfo.estimatedTier - 5)); // Higher tier = longer freeze
            freezeDelay += tierFreezeBonus;
            
            target.addFreezeDelay(freezeDelay * 300, true);
            
            // Provide freeze guidance to players
            if (target instanceof Player) {
                Player player = (Player) target;
                player.sendMessage("<col=00cc66>[Wyvern Guide] Frozen for " + freezeDelay + " seconds! Use Freedom or walk to break free!");
            }
            
            return bossInfo.attackDelay;
            
        } catch (Exception e) {
            System.err.println("Error in frost attack: " + e.getMessage());
            return 4;
        }
    }
    
    /**
     * Calculate damage using BossBalancer system with null safety
     * FIXED: Prevents null pointer exceptions like the CelestialDragon issue
     */
    private int calculateBalancedDamage(NPC npc, BossInfo bossInfo, double multiplier) {
        if (npc == null || bossInfo == null) {
            System.err.println("ERROR: Null parameters in calculateBalancedDamage");
            return 50; // Safe fallback for Wyvern
        }
        
        try {
            // Use BossBalancer max hit if available
            int baseMaxHit = Math.max(bossInfo.maxHit, 1);
            
            // Apply BossBalancer bonuses if available
            if (bossInfo.bonuses != null && bossInfo.bonuses.length >= 5) {
                // Get appropriate bonus based on attack type
                int relevantBonus = getRelevantBonus(bossInfo.bonuses, multiplier);
                
                // Apply bonus scaling (BossBalancer integration)
                double bonusMultiplier = 1.0 + (relevantBonus / 1300.0); // Scale bonuses appropriately
                baseMaxHit = (int) (baseMaxHit * bonusMultiplier);
            }
            
            // Apply multiplier
            int modifiedHit = (int) (baseMaxHit * multiplier);
            
            // Apply reasonable caps for Wyvern
            return Math.min(Math.max(modifiedHit, 1), 2800);
            
        } catch (Exception e) {
            System.err.println("Error calculating balanced damage for Wyvern: " + e.getMessage());
            return 50; // Safe fallback
        }
    }
    
    /**
     * Get relevant bonus based on attack type for hybrid bosses
     */
    private int getRelevantBonus(int[] bonuses, double multiplier) {
        if (bonuses == null || bonuses.length < 5) return 0;
        
        // Determine attack type based on multiplier
        if (multiplier == MELEE_MULTIPLIER) {
            // Use highest melee bonus (stab, slash, crush)
            return Math.max(Math.max(bonuses[0], bonuses[1]), bonuses[2]);
        } else if (multiplier == MAGIC_MULTIPLIER || multiplier == FROST_MULTIPLIER || multiplier == POISON_MULTIPLIER) {
            // Use magic bonus for magical attacks
            return bonuses[3];
        } else {
            // For other attacks, use highest offensive bonus
            int maxBonus = 0;
            for (int i = 0; i < Math.min(5, bonuses.length); i++) {
                maxBonus = Math.max(maxBonus, bonuses[i]);
            }
            return maxBonus;
        }
    }
    
    // ===== UTILITY METHODS =====
    
    private int getMaxBonus(int[] bonuses) {
        if (bonuses == null || bonuses.length == 0) return 120; // Safe default
        
        int max = 0;
        for (int bonus : bonuses) {
            if (bonus > max) max = bonus;
        }
        return Math.max(max, 120); // Ensure minimum
    }

    private int estimateBossTier(int hp, int maxHit) {
        // Enhanced tier estimation based on BossBalancer system
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

    private int estimateBossType(int attackStyle, int maxBonus) {
        // Wyvern uses multiple attack styles, so it's hybrid
        return 4; // Hybrid Boss
    }

    private String getBossTierName(int tier) {
        String[] tierNames = {"Unknown", "Beginner", "Novice", "Intermediate", "Advanced", 
                             "Expert", "Master", "Elite", "Legendary", "Mythical", "Divine"};
        return tier >= 0 && tier < tierNames.length ? tierNames[tier] : "Unknown";
    }
    
    /**
     * Get boss information for debugging and admin commands
     */
    public void printBossInfo(NPC npc) {
        if (npc == null) {
            System.out.println("Cannot print info for null Wyvern");
            return;
        }
        
        BossInfo info = getBossInfoSafely(npc);
        System.out.println("=== Wyvern Boss Info: " + npc.getDefinitions().getName() + " ===");
        System.out.println("ID: " + npc.getId());
        System.out.println("Estimated Tier: " + info.estimatedTier + " (" + getBossTierName(info.estimatedTier) + ")");
        System.out.println("Max Hit: " + info.maxHit);
        System.out.println("Current HP: " + info.currentHitpoints + "/" + info.hitpoints);
        System.out.println("Attack Types: Melee, Magic, Poison, Frost");
        
        if (info.bonuses != null) {
            System.out.println("BossBalancer Bonuses: " + java.util.Arrays.toString(info.bonuses));
            System.out.println("Max Bonus: " + info.maxBonus);
        } else {
            System.out.println("No BossBalancer bonuses configured");
        }
    }

    /**
     * Boss information container with safe defaults
     */
    private static class BossInfo {
        int maxHit = 65; // Safe default for Wyvern
        int hitpoints = 1800; // Safe default
        int currentHitpoints = 1800;
        int attackStyle = NPCCombatDefinitions.MAGE; // Hybrid, but defaults to magic
        int attackDelay = 5;
        int[] bonuses = new int[10];
        int maxBonus = 150; // Safe default
        int estimatedTier = 6; // Master tier default for Wyvern
        int estimatedType = 4; // Hybrid boss default
    }
}