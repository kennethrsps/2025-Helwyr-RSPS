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
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;
import com.rs.utils.Logger;

/**
 * Enhanced Adamant Dragon Combat - MULTI-STYLE METALLIC DRAGON WITH BATTLE GUIDANCE
 * 
 * @author Zeus
 * @date May 31, 2025
 * @version 2.2 - Complete Overhaul with BossBalancer Integration
 * Features: Tier Integration, Dragon Combat Education, Enhanced Dragonfire, Multi-Style Combat
 */
public class AdamantDragonCombat extends CombatScript {

    // Enhanced education system for multi-style dragon combat
    private static final Map<String, Long> playerLastDragonTip = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Integer> playerDragonTipStage = new ConcurrentHashMap<String, Integer>();
    private static final Map<String, Long> playerLastGuidance = new ConcurrentHashMap<String, Long>();
    private static final Map<Integer, Long> bossLastTierAnnouncement = new ConcurrentHashMap<Integer, Long>();
    private static final Map<String, Integer> playerCombatStage = new ConcurrentHashMap<String, Integer>();
    
    private static final long DRAGON_TIP_COOLDOWN = 30000; // 30 seconds between tips
    private static final long GUIDANCE_COOLDOWN = 35000; // 35 seconds between guidance messages
    private static final long TIER_ANNOUNCEMENT_COOLDOWN = 300000; // 5 minutes between tier announcements
    private static final int MAX_DRAGON_TIPS_PER_FIGHT = 6; // More tips for complex dragon mechanics

    // Boss stats cache for adamant dragons
    private static final Map<Integer, AdamantDragonStats> adamantStatsCache = new ConcurrentHashMap<Integer, AdamantDragonStats>();
    
    @Override
    public int attack(NPC npc, Entity target) {
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        
        // Get balanced adamant dragon stats
        AdamantDragonStats dragonStats = getBalancedAdamantStats(npc);
        
        // Announce tier at the beginning (visible to all players)
        announceBossTier(npc, dragonStats);
        
        // Provide dynamic battle guidance
        if (target instanceof Player) {
            provideDynamicBattleGuidance((Player) target, npc, dragonStats);
        }
        
        // Enhanced attack selection based on distance and tier
        boolean inMeleeRange = Utils.isOnRange(npc.getX(), npc.getY(), npc.getSize(), 
                                              target.getX(), target.getY(), target.getSize(), 0);
        
        executeAdamantDragonAttack(npc, target, dragonStats, inMeleeRange);
        
        return defs.getAttackDelay();
    }

    /**
     * Provide dynamic battle guidance based on combat situation
     */
    private void provideDynamicBattleGuidance(Player player, NPC npc, AdamantDragonStats dragonStats) {
        String username = player.getUsername();
        long currentTime = System.currentTimeMillis();
        
        // Check guidance cooldown
        Long lastGuidance = playerLastGuidance.get(username);
        if (lastGuidance != null && (currentTime - lastGuidance) < GUIDANCE_COOLDOWN) {
            return;
        }
        
        // Get combat stage
        Integer combatStage = playerCombatStage.get(username);
        if (combatStage == null) combatStage = 0;
        
        // Calculate boss health percentage
        int currentHp = npc.getHitpoints();
        int maxHp = dragonStats.hitpoints;
        double hpPercentage = (double) currentHp / maxHp;
        
        String guidanceMessage = null;
        
        // Phase-based guidance
        if (combatStage == 0) {
            // Opening phase
            guidanceMessage = getBossOpeningGuidance(dragonStats);
            playerCombatStage.put(username, 1);
        } else if (hpPercentage <= 0.75 && combatStage == 1) {
            // 75% HP phase
            guidanceMessage = getBossPhaseGuidance(dragonStats, "75%");
            playerCombatStage.put(username, 2);
        } else if (hpPercentage <= 0.50 && combatStage == 2) {
            // 50% HP phase
            guidanceMessage = getBossPhaseGuidance(dragonStats, "50%");
            playerCombatStage.put(username, 3);
        } else if (hpPercentage <= 0.25 && combatStage == 3) {
            // 25% HP phase - urgent warnings
            guidanceMessage = getBossPhaseGuidance(dragonStats, "25%");
            playerCombatStage.put(username, 4);
        } else if (hpPercentage <= 0.10 && combatStage == 4) {
            // Final phase - critical warnings
            guidanceMessage = getBossPhaseGuidance(dragonStats, "10%");
            playerCombatStage.put(username, 5);
        }
        
        // Send guidance message as NPC speech
        if (guidanceMessage != null) {
            sendBossGuidance(player, npc, guidanceMessage, dragonStats);
            playerLastGuidance.put(username, currentTime);
        }
    }

    /**
     * Send boss guidance as NPC speech
     */
    private void sendBossGuidance(Player player, NPC npc, String message, AdamantDragonStats dragonStats) {
        // Send as both NPC dialogue and colored message
        String tierPrefix = getTierPrefix(dragonStats.tier);
        String fullMessage = tierPrefix + " Adamant Dragon: " + message;
        
        // Send as overhead text from the dragon
        player.sendMessage("<col=00CED1>" + fullMessage + "</col>", true);
        
        // For high tier bosses, add additional warning formatting
        if (dragonStats.tier >= 8) {
            player.sendMessage("<col=FF1493>>>> LEGENDARY METALLIC WYRM <<<</col>", true);
        }
    }

    /**
     * Get boss opening guidance
     */
    private String getBossOpeningGuidance(AdamantDragonStats dragonStats) {
        if (dragonStats.tier <= 3) {
            return "A young adamant dragon awakens. Prepare for my metallic fury!";
        } else if (dragonStats.tier <= 6) {
            return "I am forged from adamant itself! My breath burns and my claws rend!";
        } else if (dragonStats.tier <= 8) {
            return "An elite metallic dragon rises! I master all forms of combat!";
        } else {
            return "I AM THE ETERNAL ADAMANT WYRM! METAL AND FIRE ARE MY DOMAIN!";
        }
    }

    /**
     * Get boss phase guidance
     */
    private String getBossPhaseGuidance(AdamantDragonStats dragonStats, String phase) {
        switch (phase) {
            case "75%":
                if (dragonStats.tier <= 5) {
                    return "You strike true, but my metallic hide protects me!";
                } else {
                    return "Impressive attacks, but my adamant scales deflect your blows!";
                }
                
            case "50%":
                if (dragonStats.tier <= 5) {
                    return "My dragonfire grows hotter! Feel the power of molten adamant!";
                } else {
                    return "Elite dragon fury awakens! My multi-style mastery intensifies!";
                }
                
            case "25%":
                if (dragonStats.tier <= 5) {
                    return "My combat prowess reaches its peak! Metallic devastation awaits!";
                } else {
                    return "You face the true power of adamant! All combat styles serve me!";
                }
                
            case "10%":
                if (dragonStats.tier <= 5) {
                    return "Impossible! But my adamant heart still burns with fury!";
                } else {
                    return "I AM THE ETERNAL METALLIC DRAGON! ADAMANT CANNOT BE DEFEATED!";
                }
        }
        return null;
    }

    /**
     * Get tier prefix for messages
     */
    private String getTierPrefix(int tier) {
        if (tier <= 3) return "Young";
        else if (tier <= 5) return "Mature";
        else if (tier <= 7) return "Elite";
        else if (tier <= 9) return "Legendary";
        else return "Divine";
    }

    /**
     * Announce boss tier to all players in the area
     */
    private void announceBossTier(NPC npc, AdamantDragonStats dragonStats) {
        int npcId = npc.getId();
        long currentTime = System.currentTimeMillis();
        
        // Check if we recently announced for this boss instance
        Long lastAnnouncement = bossLastTierAnnouncement.get(npcId + npc.hashCode());
        if (lastAnnouncement != null && (currentTime - lastAnnouncement) < TIER_ANNOUNCEMENT_COOLDOWN) {
            return; // Too soon for another announcement
        }
        
        // Find all nearby players for the announcement
        for (Entity entity : npc.getPossibleTargets()) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                
                // Simple tier announcement
                String tierName = getBossTierName(dragonStats.tier);
                String balanceStatus = dragonStats.isBalanced ? "Balanced" : "Estimated";
                
                player.sendMessage("<col=00CED1>An Adamant Dragon emerges! " + tierName + " (" + balanceStatus + ")</col>", true);
                
                // Additional warning for high tiers
                if (dragonStats.tier >= 7) {
                    player.sendMessage("<col=FF6B35>Warning: Elite metallic dragon with devastating multi-style combat!</col>", true);
                }
            }
        }
        
        // Update announcement tracking
        bossLastTierAnnouncement.put(npcId + npc.hashCode(), currentTime);
    }

    /**
     * Get balanced adamant dragon stats with caching
     */
    private AdamantDragonStats getBalancedAdamantStats(NPC npc) {
        int npcId = npc.getId();
        
        // Check cache first
        AdamantDragonStats cached = adamantStatsCache.get(npcId);
        if (cached != null && System.currentTimeMillis() - cached.timestamp < 300000) { // 5 min cache
            return cached;
        }

        AdamantDragonStats stats = new AdamantDragonStats();
        
        try {
            // Try to read tier from boss files first
            stats.tier = readTierFromBossFile(npcId);
            
            // If no boss file found, estimate from combat stats
            if (stats.tier == -1) {
                stats.tier = estimateAdamantTierFromStats(npc.getCombatDefinitions());
            }
            
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
                // Fallback: estimate adamant dragon appropriate bonuses
                stats.attackBonuses = estimateAdamantAttackBonuses(stats.tier);
                stats.defenseBonuses = estimateAdamantDefenseBonuses(stats.tier);
                stats.maxBonus = getMaxBonus(stats.attackBonuses);
                stats.isBalanced = false;
            }
            
            // Calculate adamant dragon specific stats (multi-style metallic dragon)
            stats.meleeMaxHit = calculateAdamantDamage(stats.maxHit, stats.attackBonuses[1], 1.0); // Slash-based claws
            stats.magicMaxHit = calculateAdamantDamage(stats.maxHit, stats.attackBonuses[3], 1.1); // Enhanced magic
            stats.rangedMaxHit = calculateAdamantDamage(stats.maxHit, stats.attackBonuses[4], 1.1); // Enhanced ranged
            stats.dragonfireMaxHit = calculateAdamantDamage(stats.maxHit, stats.attackBonuses[3], 1.5); // Powerful dragonfire
            
            // Tier-based attack probabilities for multi-style dragon
            stats.meleeChance = Math.max(20, 40 - (stats.tier * 2)); // 38% at tier 1, 20% at tier 10
            stats.magicChance = Math.min(30, 15 + (stats.tier * 1)); // 16% at tier 1, 25% at tier 10
            stats.rangedChance = Math.min(25, 10 + (stats.tier * 1)); // 11% at tier 1, 20% at tier 10
            stats.dragonfireChance = Math.min(45, 20 + (stats.tier * 2)); // 22% at tier 1, 40% at tier 10
            
            stats.timestamp = System.currentTimeMillis();
            adamantStatsCache.put(npcId, stats);
            
        } catch (Exception e) {
            Logger.handle(e);
            // Safe fallback values for adamant dragon
            stats.tier = 6; // Adamant dragons are typically high-tier
            stats.maxHit = 280;
            stats.meleeMaxHit = 280;
            stats.magicMaxHit = 308; // 10% enhanced
            stats.rangedMaxHit = 308; // 10% enhanced
            stats.dragonfireMaxHit = 420; // 50% enhanced
            stats.hitpoints = 14000;
            stats.attackBonuses = new int[]{650, 750, 650, 700, 700}; // Balanced multi-style
            stats.defenseBonuses = new int[]{800, 800, 850, 700, 750}; // High physical defense
            stats.maxBonus = 850;
            stats.meleeChance = 28;
            stats.magicChance = 21;
            stats.rangedChance = 16;
            stats.dragonfireChance = 32;
            stats.isBalanced = false;
        }
        
        return stats;
    }

    /**
     * Read tier from boss file created by BossBalancer
     */
    private int readTierFromBossFile(int npcId) {
        try {
            java.io.File bossFile = new java.io.File("data/npcs/bosses/" + npcId + ".txt");
            if (!bossFile.exists()) {
                return -1; // No boss file found
            }
            
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(bossFile));
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("tier=")) {
                    reader.close();
                    return Integer.parseInt(line.substring(5));
                }
            }
            reader.close();
            
        } catch (Exception e) {
            // File reading failed, will fall back to estimation
        }
        
        return -1; // Couldn't read tier from file
    }

    /**
     * Execute enhanced adamant dragon attack with tier-based selection
     */
    private void executeAdamantDragonAttack(NPC npc, Entity target, AdamantDragonStats dragonStats, boolean inMeleeRange) {
        int totalChances = dragonStats.meleeChance + dragonStats.magicChance + 
                          dragonStats.rangedChance + dragonStats.dragonfireChance;
        int attackRoll = Utils.random(totalChances);
        
        // Adjust probabilities based on range
        if (inMeleeRange) {
            // Increase melee chance when close
            if (attackRoll < dragonStats.meleeChance + 10) {
                executeMeleeAttack(npc, target, dragonStats);
            } else if (attackRoll < dragonStats.meleeChance + dragonStats.dragonfireChance + 10) {
                executeDragonfireAttack(npc, target, dragonStats);
            } else if (attackRoll < dragonStats.meleeChance + dragonStats.dragonfireChance + dragonStats.magicChance + 10) {
                executeMagicAttack(npc, target, dragonStats);
            } else {
                executeRangedAttack(npc, target, dragonStats);
            }
        } else {
            // Prefer ranged attacks when distant
            if (attackRoll < dragonStats.dragonfireChance + 15) {
                executeDragonfireAttack(npc, target, dragonStats);
            } else if (attackRoll < dragonStats.dragonfireChance + dragonStats.magicChance + 15) {
                executeMagicAttack(npc, target, dragonStats);
            } else if (attackRoll < dragonStats.dragonfireChance + dragonStats.magicChance + dragonStats.rangedChance + 15) {
                executeRangedAttack(npc, target, dragonStats);
            } else {
                executeMeleeAttack(npc, target, dragonStats);
            }
        }
    }

    /**
     * Execute enhanced melee attack - FIXED NULL REFERENCE
     */
    private void executeMeleeAttack(NPC npc, Entity target, AdamantDragonStats dragonStats) {
        npc.setNextAnimation(new Animation(npc.getCombatDefinitions().getAttackEmote()));
        
        // FIXED: Calculate balanced melee damage with proper NPC reference
        int meleeDamage = calculateBalancedMeleeDamage(npc, dragonStats, target);
        
        delayHit(npc, 0, target, getMeleeHit(npc, meleeDamage));
        
        // Provide melee education
        if (target instanceof Player && Utils.random(6) == 0) { // 16% chance
            provideDragonEducation((Player) target, npc, "MELEE", dragonStats);
        }
    }

    /**
     * Execute enhanced magic attack - FIXED NULL REFERENCE
     */
    private void executeMagicAttack(NPC npc, Entity target, AdamantDragonStats dragonStats) {
        npc.setNextAnimation(new Animation(14244));
        World.sendProjectile(npc, target, 2721, 28, 16, 35, 20, 16, 0);
        
        // FIXED: Calculate balanced magic damage with proper NPC reference
        int magicDamage = calculateBalancedMagicDamage(npc, dragonStats, target);
        
        delayHit(npc, 1, target, getMagicHit(npc, magicDamage));
        
        // Provide magic education
        if (target instanceof Player && Utils.random(5) == 0) { // 20% chance
            provideDragonEducation((Player) target, npc, "MAGIC", dragonStats);
        }
    }

    /**
     * Execute enhanced ranged attack - FIXED NULL REFERENCE
     */
    private void executeRangedAttack(NPC npc, Entity target, AdamantDragonStats dragonStats) {
        npc.setNextAnimation(new Animation(14244));
        World.sendProjectile(npc, target, 16, 28, 16, 35, 20, 16, 0);
        
        // FIXED: Calculate balanced ranged damage with proper NPC reference
        int rangedDamage = calculateBalancedRangedDamage(npc, dragonStats, target);
        
        delayHit(npc, 1, target, getRangeHit(npc, rangedDamage));
        
        // Provide ranged education
        if (target instanceof Player && Utils.random(5) == 0) { // 20% chance
            provideDragonEducation((Player) target, npc, "RANGED", dragonStats);
        }
    }

    /**
     * Execute enhanced dragonfire attack - FIXED HARDCODED DAMAGE
     */
    private void executeDragonfireAttack(NPC npc, Entity target, AdamantDragonStats dragonStats) {
        final Player player = target instanceof Player ? (Player) target : null;
        
        // FIXED: Calculate balanced dragonfire damage instead of hardcoded values
        int damage = calculateBalancedDragonfireDamage(npc, dragonStats, target);
        
        if (target instanceof Player) {
            String message = Combat.getProtectMessage(player);
            if (message != null) {
                player.sendMessage(message, true);
                if (message.contains("fully"))
                    damage = 0;
                else if (message.contains("most"))
                    damage = (int)(damage * 0.05);
                else if (message.contains("some"))
                    damage = (int)(damage * 0.1);
            }
            if (damage > 0)
                player.sendMessage("You are hit by the dragon's fiery breath!", true);
        }
        
        npc.setNextAnimation(new Animation(14245));
        World.sendProjectile(npc, target, 393, 28, 16, 35, 20, 16, 0);
        delayHit(npc, 1, target, getRegularHit(npc, damage));
        
        // Provide dragonfire education
        if (target instanceof Player && Utils.random(4) == 0) { // 25% chance
            provideDragonEducation((Player) target, npc, "DRAGONFIRE", dragonStats);
        }
    }

    /**
     * FIXED: Calculate balanced melee damage with proper NPC reference
     */
    private int calculateBalancedMeleeDamage(NPC npc, AdamantDragonStats dragonStats, Entity target) {
        // FIXED: Pass the actual NPC instead of null
        int baseDamage = getRandomMaxHit(npc, dragonStats.meleeMaxHit, NPCCombatDefinitions.MELEE, target);
        
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
     * FIXED: Calculate balanced magic damage with proper NPC reference
     */
    private int calculateBalancedMagicDamage(NPC npc, AdamantDragonStats dragonStats, Entity target) {
        // FIXED: Pass the actual NPC instead of null
        int baseDamage = getRandomMaxHit(npc, dragonStats.magicMaxHit, NPCCombatDefinitions.MAGE, target);
        
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
     * FIXED: Calculate balanced ranged damage with proper NPC reference
     */
    private int calculateBalancedRangedDamage(NPC npc, AdamantDragonStats dragonStats, Entity target) {
        // FIXED: Pass the actual NPC instead of null
        int baseDamage = getRandomMaxHit(npc, dragonStats.rangedMaxHit, NPCCombatDefinitions.RANGE, target);
        
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
     * FIXED: Calculate balanced dragonfire damage with proper NPC reference
     */
    private int calculateBalancedDragonfireDamage(NPC npc, AdamantDragonStats dragonStats, Entity target) {
        // FIXED: Pass the actual NPC instead of null for proper bonus calculations
        int baseDamage = getRandomMaxHit(npc, dragonStats.dragonfireMaxHit, NPCCombatDefinitions.MAGE, target);
        
        // Base damage calculation using tier-appropriate range
        int minDamage = (int)(baseDamage * 0.3);
        int maxDamage = baseDamage;
        
        return Utils.random(minDamage, maxDamage);
    }

    /**
     * Calculate adamant damage based on attack bonus and modifier
     */
    private int calculateAdamantDamage(int baseMaxHit, int attackBonus, double modifier) {
        int damage = (int) (baseMaxHit * modifier);
        
        // Apply attack bonus scaling
        if (attackBonus > 0) {
            damage = (int) (damage * (1.0 + (attackBonus * 0.0008))); // 0.08% per bonus point
        }
        
        return Math.max(1, damage);
    }

    /**
     * Provide adamant dragon education
     */
    private void provideDragonEducation(Player player, NPC npc, String attackType, AdamantDragonStats dragonStats) {
        String username = player.getUsername();
        long currentTime = System.currentTimeMillis();
        
        // Check cooldown
        Long lastTip = playerLastDragonTip.get(username);
        if (lastTip != null && (currentTime - lastTip) < DRAGON_TIP_COOLDOWN) {
            return;
        }
        
        // Check tip stage
        Integer tipStage = playerDragonTipStage.get(username);
        if (tipStage == null) tipStage = 0;
        if (tipStage >= MAX_DRAGON_TIPS_PER_FIGHT) return;
        
        String tipMessage = getDragonTip(dragonStats.tier, attackType, tipStage);
        if (tipMessage != null) {
            player.sendMessage("<col=00CED1>Dragon Knowledge: " + tipMessage + "</col>", true);
            
            playerLastDragonTip.put(username, currentTime);
            playerDragonTipStage.put(username, tipStage + 1);
        }
    }

    /**
     * Get dragon tip based on tier, attack type, and stage
     */
    private String getDragonTip(int tier, String attackType, int stage) {
        if (stage == 0) {
            if (attackType.equals("MELEE")) {
                if (tier <= 4) {
                    return "Adamant dragons have powerful claw attacks! Use Protect from Melee when close.";
                } else {
                    return "Elite adamant claws can shred through armor! High defense gear essential.";
                }
            } else if (attackType.equals("MAGIC")) {
                if (tier <= 4) {
                    return "Adamant dragons cast metallic magic! Use Protect from Magic and magic defense.";
                } else {
                    return "Elite adamant magic is devastating! Magic defense gear highly recommended.";
                }
            } else if (attackType.equals("RANGED")) {
                return "Adamant dragons launch metallic projectiles! Protect from Missiles helps.";
            } else if (attackType.equals("DRAGONFIRE")) {
                if (tier <= 4) {
                    return "Adamant dragonfire burns with molten metal! Use all dragonfire protection.";
                } else {
                    return "Elite adamant dragonfire is superheated! Maximum protection required.";
                }
            }
        } else if (stage == 1) {
            return "Adamant dragons use all four combat styles. Quick prayer switching is essential!";
        } else if (stage == 2) {
            return "Higher tier adamant dragons favor dragonfire attacks. Stay protected!";
        } else if (stage == 3) {
            return "Adamant dragons adapt attack style by distance. Melee close, magic/ranged distant.";
        } else if (stage == 4) {
            return "Elite adamant dragons have enhanced damage on all attacks. Be prepared!";
        } else if (stage == 5) {
            return "Metallic mastery: Adamant dragons excel at multi-style combat. Adapt constantly!";
        }
        return null;
    }

    /**
     * Get boss tier name for announcements
     */
    private String getBossTierName(int tier) {
        switch (tier) {
            case 1: return "Tier 1 Young Adamant Dragon";
            case 2: return "Tier 2 Young Adamant Dragon";
            case 3: return "Tier 3 Adamant Dragon";
            case 4: return "Tier 4 Adamant Dragon";
            case 5: return "Tier 5 Mature Adamant Dragon";
            case 6: return "Tier 6 Mature Adamant Dragon";
            case 7: return "Tier 7 Elite Adamant Wyrm";
            case 8: return "Tier 8 Legendary Adamant Lord";
            case 9: return "Tier 9 Mythical Adamant Emperor";
            case 10: return "Tier 10 Divine Adamant God";
            default: return "Unknown Tier Adamant Dragon";
        }
    }

    /**
     * Estimate adamant dragon tier from combat stats
     */
    private int estimateAdamantTierFromStats(NPCCombatDefinitions defs) {
        int hp = defs.getHitpoints();
        int maxHit = defs.getMaxHit();
        
        // Adamant dragons are typically high-tier
        if (hp <= 3200 && maxHit <= 50) return 3;       // Young Adamant Dragon
        if (hp <= 6000 && maxHit <= 80) return 4;       // Standard Adamant Dragon
        if (hp <= 10500 && maxHit <= 125) return 5;     // Mature Adamant Dragon
        if (hp <= 17000 && maxHit <= 185) return 6;     // Enhanced Adamant Dragon
        if (hp <= 25500 && maxHit <= 260) return 7;     // Elite Adamant Wyrm
        if (hp <= 36000 && maxHit <= 350) return 8;     // Legendary Adamant Lord
        if (hp <= 50000 && maxHit <= 460) return 9;     // Mythical Adamant Emperor
        return 10; // Divine Adamant God
    }

    /**
     * Estimate adamant attack bonuses for tier
     */
    private int[] estimateAdamantAttackBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Adamant dragons have balanced attack bonuses across all styles
        return new int[]{
            baseStat,                    // stab
            (int)(baseStat * 1.1),      // slash (enhanced for claws)
            baseStat,                    // crush
            (int)(baseStat * 1.1),      // magic (enhanced for dragonfire)
            (int)(baseStat * 1.1)       // ranged (enhanced)
        };
    }

    /**
     * Estimate adamant defense bonuses for tier
     */
    private int[] estimateAdamantDefenseBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Adamant dragons have high physical defense, good magic defense
        return new int[]{
            (int)(baseStat * 1.2),      // stab defense (enhanced metal armor)
            (int)(baseStat * 1.2),      // slash defense (enhanced metal armor)
            (int)(baseStat * 1.3),      // crush defense (very enhanced metal armor)
            baseStat,                    // magic defense (balanced)
            (int)(baseStat * 1.1)       // ranged defense (enhanced)
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
        return new Object[] { "Adamant dragon" }; // Adamant Dragon NPC name
    }

    /**
     * Adamant Dragon stats container class
     */
    private static class AdamantDragonStats {
        public int tier;
        public int maxHit;
        public int meleeMaxHit;
        public int magicMaxHit;
        public int rangedMaxHit;
        public int dragonfireMaxHit;
        public int hitpoints;
        public int[] attackBonuses;
        public int[] defenseBonuses;
        public int maxBonus;
        public int meleeChance;
        public int magicChance;
        public int rangedChance;
        public int dragonfireChance;
        public boolean isBalanced;
        public long timestamp;
    }
}