package com.rs.game.npc.combat.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.utils.Utils;
import com.rs.utils.NPCBonuses;
import com.rs.utils.Logger;

/**
 * Enhanced Abyssal Demon Combat - SHADOW TELEPORTER WITH BATTLE GUIDANCE
 * 
 * @author Zeus
 * @date May 31, 2025
 * @version 2.2 - Complete Overhaul with BossBalancer Integration
 * Features: Tier Integration, Teleportation Education, Shadow Abilities, Enhanced Gameplay
 */
public class AbyssalDemonCombat extends CombatScript {

    // Enhanced education system for teleportation and shadow mechanics
    private static final Map<String, Long> playerLastAbyssalTip = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Integer> playerAbyssalTipStage = new ConcurrentHashMap<String, Integer>();
    private static final Map<String, Long> playerLastGuidance = new ConcurrentHashMap<String, Long>();
    private static final Map<Integer, Long> bossLastTierAnnouncement = new ConcurrentHashMap<Integer, Long>();
    private static final Map<String, Integer> playerCombatStage = new ConcurrentHashMap<String, Integer>();
    
    private static final long ABYSSAL_TIP_COOLDOWN = 30000; // 30 seconds between tips
    private static final long GUIDANCE_COOLDOWN = 35000; // 35 seconds between guidance messages
    private static final long TIER_ANNOUNCEMENT_COOLDOWN = 300000; // 5 minutes between tier announcements
    private static final int MAX_ABYSSAL_TIPS_PER_FIGHT = 5; // More tips for teleportation mechanics

    // Boss stats cache for abyssal demons
    private static final Map<Integer, AbyssalDemonStats> abyssalStatsCache = new ConcurrentHashMap<Integer, AbyssalDemonStats>();
    
    @Override
    public int attack(NPC npc, Entity target) {
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        
        // Get balanced abyssal demon stats
        AbyssalDemonStats abyssalStats = getBalancedAbyssalStats(npc);
        
        // Announce tier at the beginning (visible to all players)
        announceBossTier(npc, abyssalStats);
        
        // Provide dynamic battle guidance
        if (target instanceof Player) {
            provideDynamicBattleGuidance((Player) target, npc, abyssalStats);
        }
        
        // Execute attack based on tier and distance
        if (npc.withinDistance(target, 1)) {
            executeCloseRangeAttack(npc, target, abyssalStats);
        } else {
            executeDistantAttack(npc, target, abyssalStats);
        }
        
        // Tier-based teleportation after attack
        if (Utils.random(100) < abyssalStats.teleportChance) {
            performEnhancedTeleportation(npc, target, abyssalStats);
        }
        
        return defs.getAttackDelay();
    }

    /**
     * Provide dynamic battle guidance based on combat situation
     */
    private void provideDynamicBattleGuidance(Player player, NPC npc, AbyssalDemonStats abyssalStats) {
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
        int maxHp = abyssalStats.hitpoints;
        double hpPercentage = (double) currentHp / maxHp;
        
        String guidanceMessage = null;
        
        // Phase-based guidance
        if (combatStage == 0) {
            // Opening phase
            guidanceMessage = getBossOpeningGuidance(abyssalStats);
            playerCombatStage.put(username, 1);
        } else if (hpPercentage <= 0.75 && combatStage == 1) {
            // 75% HP phase
            guidanceMessage = getBossPhaseGuidance(abyssalStats, "75%");
            playerCombatStage.put(username, 2);
        } else if (hpPercentage <= 0.50 && combatStage == 2) {
            // 50% HP phase
            guidanceMessage = getBossPhaseGuidance(abyssalStats, "50%");
            playerCombatStage.put(username, 3);
        } else if (hpPercentage <= 0.25 && combatStage == 3) {
            // 25% HP phase - urgent warnings
            guidanceMessage = getBossPhaseGuidance(abyssalStats, "25%");
            playerCombatStage.put(username, 4);
        } else if (hpPercentage <= 0.10 && combatStage == 4) {
            // Final phase - critical warnings
            guidanceMessage = getBossPhaseGuidance(abyssalStats, "10%");
            playerCombatStage.put(username, 5);
        }
        
        // Send guidance message as NPC speech
        if (guidanceMessage != null) {
            sendBossGuidance(player, npc, guidanceMessage, abyssalStats);
            playerLastGuidance.put(username, currentTime);
        }
    }

    /**
     * Send boss guidance as NPC speech
     */
    private void sendBossGuidance(Player player, NPC npc, String message, AbyssalDemonStats abyssalStats) {
        // Send as both NPC dialogue and colored message
        String tierPrefix = getTierPrefix(abyssalStats.tier);
        String fullMessage = tierPrefix + " Abyssal Demon: " + message;
        
        // Send as overhead text from the demon
        player.sendMessage("<col=800080>" + fullMessage + "</col>", true);
        
        // For high tier bosses, add additional warning formatting
        if (abyssalStats.tier >= 8) {
            player.sendMessage("<col=FF1493>>>> LEGENDARY SHADOW ENTITY <<<</col>", true);
        }
    }

    /**
     * Get boss opening guidance
     */
    private String getBossOpeningGuidance(AbyssalDemonStats abyssalStats) {
        if (abyssalStats.tier <= 3) {
            return "A lesser demon emerges from the abyss. Beware my teleportation!";
        } else if (abyssalStats.tier <= 6) {
            return "I am an abyssal hunter! Space bends to my will!";
        } else if (abyssalStats.tier <= 8) {
            return "An elite shadow demon awakens! Reality itself cannot contain me!";
        } else {
            return "I AM THE MASTER OF THE VOID! DIMENSIONAL TRAVEL IS MY DOMAIN!";
        }
    }

    /**
     * Get boss phase guidance
     */
    private String getBossPhaseGuidance(AbyssalDemonStats abyssalStats, String phase) {
        switch (phase) {
            case "75%":
                if (abyssalStats.tier <= 5) {
                    return "You strike well, but I can escape anywhere!";
                } else {
                    return "Impressive attacks, but the shadows protect me!";
                }
                
            case "50%":
                if (abyssalStats.tier <= 5) {
                    return "The abyss calls to me! I grow more elusive!";
                } else {
                    return "Shadow magic flows through me! You cannot pin me down!";
                }
                
            case "25%":
                if (abyssalStats.tier <= 5) {
                    return "My teleportation reaches its peak! Catch me if you can!";
                } else {
                    return "The void itself aids my escape! You face dimensional mastery!";
                }
                
            case "10%":
                if (abyssalStats.tier <= 5) {
                    return "Impossible! But I still have the shadows as my ally!";
                } else {
                    return "I AM ONE WITH THE ABYSS! SPACE CANNOT DEFEAT SPACE ITSELF!";
                }
        }
        return null;
    }

    /**
     * Get tier prefix for messages
     */
    private String getTierPrefix(int tier) {
        if (tier <= 3) return "Lesser";
        else if (tier <= 5) return "Greater";
        else if (tier <= 7) return "Elite";
        else if (tier <= 9) return "Legendary";
        else return "Divine";
    }

    /**
     * Announce boss tier to all players in the area
     */
    private void announceBossTier(NPC npc, AbyssalDemonStats abyssalStats) {
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
                String tierName = getBossTierName(abyssalStats.tier);
                String balanceStatus = abyssalStats.isBalanced ? "Balanced" : "Estimated";
                
                player.sendMessage("<col=800080>An Abyssal Demon materializes! " + tierName + " (" + balanceStatus + ")</col>", true);
                
                // Additional warning for high tiers
                if (abyssalStats.tier >= 7) {
                    player.sendMessage("<col=FF6B35>Warning: Elite shadow demon with advanced teleportation abilities!</col>", true);
                }
            }
        }
        
        // Update announcement tracking
        bossLastTierAnnouncement.put(npcId + npc.hashCode(), currentTime);
    }

    /**
     * Get balanced abyssal demon stats with caching
     */
    private AbyssalDemonStats getBalancedAbyssalStats(NPC npc) {
        int npcId = npc.getId();
        
        // Check cache first
        AbyssalDemonStats cached = abyssalStatsCache.get(npcId);
        if (cached != null && System.currentTimeMillis() - cached.timestamp < 300000) { // 5 min cache
            return cached;
        }

        AbyssalDemonStats stats = new AbyssalDemonStats();
        
        try {
            // Try to read tier from boss files first
            stats.tier = readTierFromBossFile(npcId);
            
            // If no boss file found, estimate from combat stats
            if (stats.tier == -1) {
                stats.tier = estimateAbyssalTierFromStats(npc.getCombatDefinitions());
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
                // Fallback: estimate abyssal demon appropriate bonuses
                stats.attackBonuses = estimateAbyssalAttackBonuses(stats.tier);
                stats.defenseBonuses = estimateAbyssalDefenseBonuses(stats.tier);
                stats.maxBonus = getMaxBonus(stats.attackBonuses);
                stats.isBalanced = false;
            }
            
            // Calculate abyssal demon specific stats
            stats.meleeMaxHit = calculateAbyssalDamage(stats.maxHit, stats.attackBonuses[1], 1.0); // Slash-based
            stats.shadowStrikeMaxHit = calculateAbyssalDamage(stats.maxHit, stats.attackBonuses[1], 1.3); // Enhanced shadow attack
            stats.darkMagicMaxHit = calculateAbyssalDamage(stats.maxHit, stats.attackBonuses[3], 1.2); // Dark magic
            
            // Tier-based abilities
            stats.teleportChance = Math.min(50, 15 + (stats.tier * 3)); // 18% at tier 1, 45% at tier 10
            stats.shadowStrikeChance = Math.min(30, 5 + (stats.tier * 2)); // 7% at tier 1, 25% at tier 10
            stats.darkMagicChance = Math.min(25, 2 + (stats.tier * 2)); // 4% at tier 1, 22% at tier 10
            stats.teleportRange = Math.min(8, 3 + (stats.tier / 2)); // 3-8 tile range based on tier
            
            stats.timestamp = System.currentTimeMillis();
            abyssalStatsCache.put(npcId, stats);
            
        } catch (Exception e) {
            Logger.handle(e);
            // Safe fallback values for abyssal demon
            stats.tier = 5; // Mid-tier default
            stats.maxHit = 150;
            stats.meleeMaxHit = 150;
            stats.shadowStrikeMaxHit = 195; // 30% enhanced
            stats.darkMagicMaxHit = 180; // 20% enhanced
            stats.hitpoints = 8000;
            stats.attackBonuses = new int[]{500, 600, 500, 400, 300}; // Melee focused
            stats.defenseBonuses = new int[]{400, 400, 400, 500, 350}; // Magic defense focused
            stats.maxBonus = 600;
            stats.teleportChance = 30;
            stats.shadowStrikeChance = 15;
            stats.darkMagicChance = 12;
            stats.teleportRange = 5;
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
     * Execute close range attack with tier-aware abilities
     */
    private void executeCloseRangeAttack(NPC npc, Entity target, AbyssalDemonStats abyssalStats) {
        int attackRoll = Utils.random(100);
        
        if (attackRoll < abyssalStats.shadowStrikeChance) {
            executeShadowStrike(npc, target, abyssalStats);
        } else {
            executeEnhancedMeleeAttack(npc, target, abyssalStats);
        }
    }

    /**
     * Execute distant attack with tier-aware abilities
     */
    private void executeDistantAttack(NPC npc, Entity target, AbyssalDemonStats abyssalStats) {
        int attackRoll = Utils.random(100);
        
        if (attackRoll < abyssalStats.darkMagicChance) {
            executeDarkMagicAttack(npc, target, abyssalStats);
        } else {
            // Move closer and melee attack
            executeEnhancedMeleeAttack(npc, target, abyssalStats);
        }
    }

    /**
     * Execute enhanced melee attack - FIXED NULL REFERENCE
     */
    private void executeEnhancedMeleeAttack(NPC npc, Entity target, AbyssalDemonStats abyssalStats) {
        npc.setNextAnimation(new Animation(1537));
        
        // FIXED: Calculate balanced melee damage with proper NPC reference
        int meleeDamage = calculateBalancedMeleeDamage(npc, abyssalStats, target);
        
        delayHit(npc, 1, target, getMeleeHit(npc, meleeDamage));
        
        // Provide melee education
        if (target instanceof Player && Utils.random(6) == 0) { // 16% chance
            provideAbyssalEducation((Player) target, npc, "MELEE", abyssalStats);
        }
    }

    /**
     * Execute shadow strike attack (enhanced melee)
     */
    private void executeShadowStrike(NPC npc, Entity target, AbyssalDemonStats abyssalStats) {
        npc.setNextAnimation(new Animation(1537));
        npc.setNextGraphics(new Graphics(409)); // Shadow effect
        
        // Calculate enhanced shadow damage
        int shadowDamage = calculateBalancedShadowDamage(npc, abyssalStats, target);
        
        delayHit(npc, 1, target, getMeleeHit(npc, shadowDamage));
        
        // Provide shadow education
        if (target instanceof Player && Utils.random(4) == 0) { // 25% chance
            provideAbyssalEducation((Player) target, npc, "SHADOW", abyssalStats);
        }
    }

    /**
     * Execute dark magic attack
     */
    private void executeDarkMagicAttack(NPC npc, Entity target, AbyssalDemonStats abyssalStats) {
        npc.setNextAnimation(new Animation(1537));
        npc.setNextGraphics(new Graphics(409));
        World.sendProjectile(npc, target, 409, 41, 16, 41, 35, 16, 0);
        
        // Calculate dark magic damage
        int magicDamage = calculateBalancedDarkMagicDamage(npc, abyssalStats, target);
        
        delayHit(npc, 2, target, getMagicHit(npc, magicDamage));
        
        // Provide magic education
        if (target instanceof Player && Utils.random(5) == 0) { // 20% chance
            provideAbyssalEducation((Player) target, npc, "DARK_MAGIC", abyssalStats);
        }
    }

    /**
     * Perform enhanced teleportation with tier-based improvements
     */
    private void performEnhancedTeleportation(NPC npc, Entity target, AbyssalDemonStats abyssalStats) {
        WorldTile teleTile = new WorldTile(npc); // Default to current position
        
        // Try to find a valid teleportation tile near the target
        for (int trycount = 0; trycount < 15; trycount++) {
            // Generate random tile within tier-based range
            int range = abyssalStats.teleportRange;
            int offsetX = Utils.random(-range, range + 1);
            int offsetY = Utils.random(-range, range + 1);
            
            WorldTile candidateTile = new WorldTile(
                target.getX() + offsetX, 
                target.getY() + offsetY, 
                target.getPlane()
            );
            
            // Check if the NPC can move to this tile
            if (World.canMoveNPC(npc.getPlane(), candidateTile.getX(), 
                                candidateTile.getY(), npc.getSize())) {
                teleTile = candidateTile;
                break; // Found a valid tile
            }
        }
        
        // Only teleport if we found a different valid tile
        if (!teleTile.matches(npc)) {
            npc.setNextGraphics(new Graphics(409)); // Teleport graphics
            npc.setNextWorldTile(teleTile);
            
            // Provide teleportation education
            if (target instanceof Player && Utils.random(3) == 0) { // 33% chance
                provideAbyssalEducation((Player) target, npc, "TELEPORT", abyssalStats);
            }
        }
    }

    /**
     * FIXED: Calculate balanced melee damage with proper NPC reference
     */
    private int calculateBalancedMeleeDamage(NPC npc, AbyssalDemonStats abyssalStats, Entity target) {
        // FIXED: Pass the actual NPC instead of null
        int baseDamage = getRandomMaxHit(npc, abyssalStats.meleeMaxHit, NPCCombatDefinitions.MELEE, target);
        
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
     * FIXED: Calculate balanced shadow damage with proper NPC reference
     */
    private int calculateBalancedShadowDamage(NPC npc, AbyssalDemonStats abyssalStats, Entity target) {
        // FIXED: Pass the actual NPC instead of null
        int baseDamage = getRandomMaxHit(npc, abyssalStats.shadowStrikeMaxHit, NPCCombatDefinitions.MELEE, target);
        
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
     * FIXED: Calculate balanced dark magic damage with proper NPC reference
     */
    private int calculateBalancedDarkMagicDamage(NPC npc, AbyssalDemonStats abyssalStats, Entity target) {
        // FIXED: Pass the actual NPC instead of null
        int baseDamage = getRandomMaxHit(npc, abyssalStats.darkMagicMaxHit, NPCCombatDefinitions.MAGE, target);
        
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
     * Calculate abyssal damage based on attack bonus and modifier
     */
    private int calculateAbyssalDamage(int baseMaxHit, int attackBonus, double modifier) {
        int damage = (int) (baseMaxHit * modifier);
        
        // Apply attack bonus scaling
        if (attackBonus > 0) {
            damage = (int) (damage * (1.0 + (attackBonus * 0.0008))); // 0.08% per bonus point
        }
        
        return Math.max(1, damage);
    }

    /**
     * Provide abyssal demon education
     */
    private void provideAbyssalEducation(Player player, NPC npc, String attackType, AbyssalDemonStats abyssalStats) {
        String username = player.getUsername();
        long currentTime = System.currentTimeMillis();
        
        // Check cooldown
        Long lastTip = playerLastAbyssalTip.get(username);
        if (lastTip != null && (currentTime - lastTip) < ABYSSAL_TIP_COOLDOWN) {
            return;
        }
        
        // Check tip stage
        Integer tipStage = playerAbyssalTipStage.get(username);
        if (tipStage == null) tipStage = 0;
        if (tipStage >= MAX_ABYSSAL_TIPS_PER_FIGHT) return;
        
        String tipMessage = getAbyssalTip(abyssalStats.tier, attackType, tipStage);
        if (tipMessage != null) {
            player.sendMessage("<col=800080>Abyss Knowledge: " + tipMessage + "</col>", true);
            
            playerLastAbyssalTip.put(username, currentTime);
            playerAbyssalTipStage.put(username, tipStage + 1);
        }
    }

    /**
     * Get abyssal tip based on tier, attack type, and stage
     */
    private String getAbyssalTip(int tier, String attackType, int stage) {
        if (stage == 0) {
            if (attackType.equals("MELEE")) {
                if (tier <= 4) {
                    return "Abyssal demons strike with powerful claws! Use Protect from Melee.";
                } else {
                    return "Elite abyssal demons have devastating melee attacks! High defense essential.";
                }
            } else if (attackType.equals("SHADOW")) {
                if (tier <= 4) {
                    return "Shadow strike deals enhanced damage! Watch for the dark energy.";
                } else {
                    return "Elite shadow strikes can pierce through armor! Be extremely careful.";
                }
            } else if (attackType.equals("DARK_MAGIC")) {
                return "Abyssal demons can cast dark magic from range! Use Protect from Magic.";
            } else if (attackType.equals("TELEPORT")) {
                if (tier <= 4) {
                    return "Abyssal demons can teleport nearby! They have " + getTeleportChanceText(tier) + " chance.";
                } else {
                    return "Elite demons teleport frequently with " + getTeleportRangeText(tier) + " range!";
                }
            }
        } else if (stage == 1) {
            return "Higher tier abyssal demons teleport more often and have enhanced abilities.";
        } else if (stage == 2) {
            return "Abyssal demons excel at hit-and-run tactics. Stay alert for teleportation!";
        } else if (stage == 3) {
            return "Elite demons can use shadow strikes and dark magic. Adapt your defenses!";
        } else if (stage == 4) {
            return "Abyssal mastery: These demons control space itself. Predict their movements!";
        }
        return null;
    }

    /**
     * Get teleport chance text based on tier
     */
    private String getTeleportChanceText(int tier) {
        if (tier <= 3) return "low";
        else if (tier <= 6) return "moderate";
        else return "high";
    }

    /**
     * Get teleport range text based on tier
     */
    private String getTeleportRangeText(int tier) {
        if (tier <= 3) return "short";
        else if (tier <= 6) return "medium";
        else return "long";
    }

    /**
     * Get boss tier name for announcements
     */
    private String getBossTierName(int tier) {
        switch (tier) {
            case 1: return "Tier 1 Lesser Demon";
            case 2: return "Tier 2 Lesser Demon";
            case 3: return "Tier 3 Abyssal Demon";
            case 4: return "Tier 4 Abyssal Demon";
            case 5: return "Tier 5 Greater Demon";
            case 6: return "Tier 6 Greater Demon";
            case 7: return "Tier 7 Elite Shadow Demon";
            case 8: return "Tier 8 Legendary Abyssal Lord";
            case 9: return "Tier 9 Mythical Void Master";
            case 10: return "Tier 10 Divine Abyssal God";
            default: return "Unknown Tier Abyssal Entity";
        }
    }

    /**
     * Estimate abyssal demon tier from combat stats
     */
    private int estimateAbyssalTierFromStats(NPCCombatDefinitions defs) {
        int hp = defs.getHitpoints();
        int maxHit = defs.getMaxHit();
        
        // Abyssal demons typically mid-tier
        if (hp <= 3200 && maxHit <= 50) return 3;       // Standard Abyssal Demon
        if (hp <= 6000 && maxHit <= 80) return 4;       // Stronger Abyssal Demon
        if (hp <= 10500 && maxHit <= 125) return 5;     // Greater Demon
        if (hp <= 17000 && maxHit <= 185) return 6;     // Enhanced Greater Demon
        if (hp <= 25500 && maxHit <= 260) return 7;     // Elite Shadow Demon
        if (hp <= 36000 && maxHit <= 350) return 8;     // Legendary Abyssal Lord
        if (hp <= 50000 && maxHit <= 460) return 9;     // Mythical Void Master
        return 10; // Divine Abyssal God
    }

    /**
     * Estimate abyssal attack bonuses for tier
     */
    private int[] estimateAbyssalAttackBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Abyssal demons have enhanced slash and magic bonuses
        return new int[]{
            baseStat,                    // stab
            (int)(baseStat * 1.2),      // slash (enhanced for claws)
            baseStat,                    // crush
            (int)(baseStat * 1.1),      // magic (enhanced for dark magic)
            (int)(baseStat * 0.8)       // ranged (reduced)
        };
    }

    /**
     * Estimate abyssal defense bonuses for tier
     */
    private int[] estimateAbyssalDefenseBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Abyssal demons have enhanced magic defense
        return new int[]{
            baseStat,                    // stab defense
            baseStat,                    // slash defense
            baseStat,                    // crush defense
            (int)(baseStat * 1.2),      // magic defense (enhanced)
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
        return new Object[] { 1615 }; // Abyssal Demon NPC ID
    }

    /**
     * Abyssal Demon stats container class
     */
    private static class AbyssalDemonStats {
        public int tier;
        public int maxHit;
        public int meleeMaxHit;
        public int shadowStrikeMaxHit;
        public int darkMagicMaxHit;
        public int hitpoints;
        public int[] attackBonuses;
        public int[] defenseBonuses;
        public int maxBonus;
        public int teleportChance;
        public int shadowStrikeChance;
        public int darkMagicChance;
        public int teleportRange;
        public boolean isBalanced;
        public long timestamp;
    }
}