package com.rs.game.npc.combat.impl;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
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
 * Enhanced Ballak Combat - EARTHQUAKE TITAN WITH BATTLE GUIDANCE
 * 
 * @author Zeus
 * @date May 31, 2025
 * @version 2.2 - Complete Overhaul with BossBalancer Integration
 * Features: Tier Integration, Earthquake Education, Enhanced Seismic Combat, Dynamic Guidance
 */
public class BallakCombat extends CombatScript {

    // Enhanced education system for earthquake mechanics
    private static final Map<String, Long> playerLastEarthquakeTip = new ConcurrentHashMap<String, Long>();
    private static final Map<String, Integer> playerEarthquakeTipStage = new ConcurrentHashMap<String, Integer>();
    private static final Map<String, Long> playerLastGuidance = new ConcurrentHashMap<String, Long>();
    private static final Map<Integer, Long> bossLastTierAnnouncement = new ConcurrentHashMap<Integer, Long>();
    private static final Map<String, Integer> playerCombatStage = new ConcurrentHashMap<String, Integer>();
    
    private static final long EARTHQUAKE_TIP_COOLDOWN = 25000; // 25 seconds between tips
    private static final long GUIDANCE_COOLDOWN = 30000; // 30 seconds between guidance messages
    private static final long TIER_ANNOUNCEMENT_COOLDOWN = 300000; // 5 minutes between tier announcements
    private static final int MAX_EARTHQUAKE_TIPS_PER_FIGHT = 6; // More tips for complex earthquake mechanics

    // Boss stats cache for earthquake titans
    private static final Map<Integer, EarthquakeTitanStats> earthquakeStatsCache = new ConcurrentHashMap<Integer, EarthquakeTitanStats>();
    
    // Attack animation arrays
    int[] attackEmotes = { 19563, 19562 };
    
    @Override
    public int attack(NPC npc, Entity target) {
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        
        // Get balanced earthquake titan stats
        EarthquakeTitanStats titanStats = getBalancedEarthquakeStats(npc);
        
        // Announce tier at the beginning (visible to all players)
        announceBossTier(npc, titanStats);
        
        // Provide dynamic battle guidance
        if (target instanceof Player) {
            provideDynamicBattleGuidance((Player) target, npc, titanStats);
        }
        
        // Execute earthquake titan attack based on tier and probability
        executeEarthquakeTitanAttack(npc, target, titanStats);
        
        return defs.getAttackDelay();
    }

    /**
     * Provide dynamic battle guidance based on combat situation
     */
    private void provideDynamicBattleGuidance(Player player, NPC npc, EarthquakeTitanStats titanStats) {
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
        int maxHp = titanStats.hitpoints;
        double hpPercentage = (double) currentHp / maxHp;
        
        String guidanceMessage = null;
        
        // Phase-based guidance
        if (combatStage == 0) {
            // Opening phase
            guidanceMessage = getBossOpeningGuidance(titanStats);
            playerCombatStage.put(username, 1);
        } else if (hpPercentage <= 0.75 && combatStage == 1) {
            // 75% HP phase
            guidanceMessage = getBossPhaseGuidance(titanStats, "75%");
            playerCombatStage.put(username, 2);
        } else if (hpPercentage <= 0.50 && combatStage == 2) {
            // 50% HP phase
            guidanceMessage = getBossPhaseGuidance(titanStats, "50%");
            playerCombatStage.put(username, 3);
        } else if (hpPercentage <= 0.25 && combatStage == 3) {
            // 25% HP phase - urgent warnings
            guidanceMessage = getBossPhaseGuidance(titanStats, "25%");
            playerCombatStage.put(username, 4);
        } else if (hpPercentage <= 0.10 && combatStage == 4) {
            // Final phase - critical warnings
            guidanceMessage = getBossPhaseGuidance(titanStats, "10%");
            playerCombatStage.put(username, 5);
        }
        
        // Send guidance message as NPC speech
        if (guidanceMessage != null) {
            sendBossGuidance(player, npc, guidanceMessage, titanStats);
            playerLastGuidance.put(username, currentTime);
        }
    }

    /**
     * Send boss guidance as titan speech
     */
    private void sendBossGuidance(Player player, NPC npc, String message, EarthquakeTitanStats titanStats) {
        // Send as both NPC dialogue and colored message
        String tierPrefix = getTierPrefix(titanStats.tier);
        String fullMessage = tierPrefix + " Ballak: " + message;
        
        // Send as overhead text from the earthquake titan
        player.sendMessage("<col=8B4513>" + fullMessage + "</col>", true);
        
        // For high tier bosses, add additional warning formatting
        if (titanStats.tier >= 8) {
            player.sendMessage("<col=FF1493>>>> LEGENDARY SEISMIC EMPEROR <<<</col>", true);
        }
    }

    /**
     * Get boss opening guidance
     */
    private String getBossOpeningGuidance(EarthquakeTitanStats titanStats) {
        if (titanStats.tier <= 3) {
            return "The earth trembles beneath my might. Feel my seismic power!";
        } else if (titanStats.tier <= 6) {
            return "I am the master of earthquakes! The ground itself obeys my will!";
        } else if (titanStats.tier <= 8) {
            return "An elite seismic titan awakens! I command devastating earthquakes!";
        } else {
            return "I AM THE ETERNAL EARTHQUAKE EMPEROR! THE EARTH ITSELF IS MY WEAPON!";
        }
    }

    /**
     * Get boss phase guidance
     */
    private String getBossPhaseGuidance(EarthquakeTitanStats titanStats, String phase) {
        switch (phase) {
            case "75%":
                if (titanStats.tier <= 5) {
                    return "You strike well, but the earth shields me from harm!";
                } else {
                    return "Impressive attacks, but my seismic power grows stronger!";
                }
                
            case "50%":
                if (titanStats.tier <= 5) {
                    return "The earth's fury awakens! Feel my growing earthquake power!";
                } else {
                    return "Elite seismic mastery unleashed! The ground trembles with rage!";
                }
                
            case "25%":
                if (titanStats.tier <= 5) {
                    return "My earthquake power reaches its peak! Devastating tremors await!";
                } else {
                    return "You face the seismic emperor's wrath! The earth itself rebels!";
                }
                
            case "10%":
                if (titanStats.tier <= 5) {
                    return "Impossible! But the earth's power cannot be truly conquered!";
                } else {
                    return "I AM THE ETERNAL EARTHQUAKE TITAN! SEISMIC FORCE IS ETERNAL!";
                }
        }
        return null;
    }

    /**
     * Get tier prefix for messages
     */
    private String getTierPrefix(int tier) {
        if (tier <= 3) return "Earth Shaker";
        else if (tier <= 5) return "Seismic Titan";
        else if (tier <= 7) return "Elite Earthquake Lord";
        else if (tier <= 9) return "Legendary Seismic Emperor";
        else return "Divine Earthquake God";
    }

    /**
     * Announce boss tier to all players in the area
     */
    private void announceBossTier(NPC npc, EarthquakeTitanStats titanStats) {
        int npcId = npc.getId();
        long currentTime = System.currentTimeMillis();
        
        // Check if we recently announced for this boss instance
        Long lastAnnouncement = bossLastTierAnnouncement.get(npcId + npc.hashCode());
        if (lastAnnouncement != null && (currentTime - lastAnnouncement) < TIER_ANNOUNCEMENT_COOLDOWN) {
            return; // Too soon for another announcement
        }
        
        // Get all possible targets for the announcement
        ArrayList<Entity> targets = npc.getPossibleTargets();
        if (targets.isEmpty()) return;
        
        // Send announcement to all players
        for (Entity entity : targets) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                
                // Simple tier announcement
                String tierName = getBossTierName(titanStats.tier);
                String balanceStatus = titanStats.isBalanced ? "Balanced" : "Estimated";
                
                player.sendMessage("<col=8B4513>Ballak the Earthshaker awakens! " + tierName + " (" + balanceStatus + ")</col>", true);
                
                // Additional warning for high tiers
                if (titanStats.tier >= 7) {
                    player.sendMessage("<col=FF6B35>Warning: Elite earthquake titan with devastating seismic abilities!</col>", true);
                }
            }
        }
        
        // Update announcement tracking
        bossLastTierAnnouncement.put(npcId + npc.hashCode(), currentTime);
    }

    /**
     * Get balanced earthquake titan stats with caching
     */
    private EarthquakeTitanStats getBalancedEarthquakeStats(NPC npc) {
        int npcId = npc.getId();
        
        // Check cache first
        EarthquakeTitanStats cached = earthquakeStatsCache.get(npcId);
        if (cached != null && System.currentTimeMillis() - cached.timestamp < 300000) { // 5 min cache
            return cached;
        }

        EarthquakeTitanStats stats = new EarthquakeTitanStats();
        
        try {
            // Try to read tier from boss files first
            stats.tier = readTierFromBossFile(npcId);
            
            // If no boss file found, estimate from combat stats
            if (stats.tier == -1) {
                stats.tier = estimateEarthquakeTierFromStats(npc.getCombatDefinitions());
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
                // Fallback: estimate earthquake titan appropriate bonuses
                stats.attackBonuses = estimateEarthquakeAttackBonuses(stats.tier);
                stats.defenseBonuses = estimateEarthquakeDefenseBonuses(stats.tier);
                stats.maxBonus = getMaxBonus(stats.attackBonuses);
                stats.isBalanced = false;
            }
            
            // Calculate earthquake titan specific stats (seismic combat specialist)
            stats.meleeMaxHit = calculateEarthquakeDamage(stats.maxHit, stats.attackBonuses[2], 1.0); // Crush-based melee
            stats.earthquakeMaxHit = calculateEarthquakeDamage(stats.maxHit, stats.attackBonuses[3], 1.4); // Enhanced seismic magic
            stats.shockwaveMaxHit = calculateEarthquakeDamage(stats.maxHit, stats.attackBonuses[3], 1.2); // Secondary earthquake
            stats.trembleMaxHit = calculateEarthquakeDamage(stats.maxHit, stats.attackBonuses[3], 1.6); // Ultimate earthquake
            
            // Tier-based earthquake abilities
            stats.earthquakeChance = Math.min(50, 15 + (stats.tier * 3)); // 18% at tier 1, 45% at tier 10
            stats.shockwaveChance = Math.min(30, 5 + (stats.tier * 2)); // 7% at tier 1, 25% at tier 10
            stats.trembleChance = Math.min(20, stats.tier > 5 ? (stats.tier - 5) * 3 : 0); // 0% tiers 1-5, 3-15% tiers 6-10
            
            // Tier-based seismic effects
            stats.earthquakeIntensity = Math.min(60, 15 + (stats.tier * 4)); // Camera shake intensity
            stats.earthquakeDuration = Math.min(6000, 2000 + (stats.tier * 400)); // Duration scaling
            stats.earthquakeWaves = Math.min(4, 2 + (stats.tier / 3)); // Number of waves
            
            stats.timestamp = System.currentTimeMillis();
            earthquakeStatsCache.put(npcId, stats);
            
        } catch (Exception e) {
            Logger.handle(e);
            // Safe fallback values for earthquake titan
            stats.tier = 6; // Ballak is typically high-tier
            stats.maxHit = 300;
            stats.meleeMaxHit = 300;
            stats.earthquakeMaxHit = 420; // 40% enhanced
            stats.shockwaveMaxHit = 360; // 20% enhanced
            stats.trembleMaxHit = 480; // 60% enhanced
            stats.hitpoints = 15000;
            stats.attackBonuses = new int[]{600, 650, 750, 800, 550}; // Crush/Magic focused
            stats.defenseBonuses = new int[]{700, 700, 750, 650, 650}; // Strong physical defense
            stats.maxBonus = 800;
            stats.earthquakeChance = 33;
            stats.shockwaveChance = 17;
            stats.trembleChance = 3;
            stats.earthquakeIntensity = 39;
            stats.earthquakeDuration = 4400;
            stats.earthquakeWaves = 3;
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
     * Execute earthquake titan attack with tier-based selection
     */
    private void executeEarthquakeTitanAttack(NPC npc, Entity target, EarthquakeTitanStats titanStats) {
        int attackRoll = Utils.random(100);
        
        if (attackRoll < titanStats.trembleChance) {
            executeTrembleAttack(npc, target, titanStats);
        } else if (attackRoll < titanStats.trembleChance + titanStats.shockwaveChance) {
            executeShockwaveAttack(npc, target, titanStats);
        } else if (attackRoll < titanStats.trembleChance + titanStats.shockwaveChance + titanStats.earthquakeChance) {
            executeEarthquakeAttack(npc, target, titanStats);
        } else {
            executeMeleeAttack(npc, target, titanStats);
        }
    }

    /**
     * Execute standard earthquake attack
     */
    private void executeEarthquakeAttack(NPC npc, Entity target, EarthquakeTitanStats titanStats) {
        // Get all targets for AoE
        ArrayList<Entity> targets = npc.getPossibleTargets();
        
        npc.setNextAnimation(new Animation(19561));
        
        // Execute earthquake on all targets
        for (Entity t : targets) {
            if (t instanceof Player) {
                Player p = (Player) t;
                
                // Calculate balanced earthquake damage
                int earthquakeDamage = calculateBalancedEarthquakeDamage(npc, titanStats, p);
                p.applyHit(new Hit(npc, earthquakeDamage, HitLook.MAGIC_DAMAGE));
            }
        }
        
        // Start earthquake effects
        startEarthquakeSequence(npc, targets, titanStats, "EARTHQUAKE");
        
        // Provide earthquake education
        if (target instanceof Player && Utils.random(4) == 0) { // 25% chance
            provideEarthquakeEducation((Player) target, npc, "EARTHQUAKE", titanStats);
        }
    }

    /**
     * Execute enhanced shockwave attack
     */
    private void executeShockwaveAttack(NPC npc, Entity target, EarthquakeTitanStats titanStats) {
        // Get all targets for AoE
        ArrayList<Entity> targets = npc.getPossibleTargets();
        
        npc.setNextAnimation(new Animation(19561));
        
        // Execute shockwave on all targets
        for (Entity t : targets) {
            if (t instanceof Player) {
                Player p = (Player) t;
                
                // Calculate balanced shockwave damage
                int shockwaveDamage = calculateBalancedShockwaveDamage(npc, titanStats, p);
                p.applyHit(new Hit(npc, shockwaveDamage, HitLook.MAGIC_DAMAGE));
            }
        }
        
        // Start enhanced shockwave effects
        startEarthquakeSequence(npc, targets, titanStats, "SHOCKWAVE");
        
        // Provide shockwave education
        if (target instanceof Player && Utils.random(3) == 0) { // 33% chance
            provideEarthquakeEducation((Player) target, npc, "SHOCKWAVE", titanStats);
        }
    }

    /**
     * Execute ultimate tremble attack (high tier only)
     */
    private void executeTrembleAttack(NPC npc, Entity target, EarthquakeTitanStats titanStats) {
        // Get all targets for AoE
        ArrayList<Entity> targets = npc.getPossibleTargets();
        
        npc.setNextAnimation(new Animation(19561));
        
        // Execute devastating tremble on all targets
        for (Entity t : targets) {
            if (t instanceof Player) {
                Player p = (Player) t;
                
                // Calculate balanced tremble damage
                int trembleDamage = calculateBalancedTrembleDamage(npc, titanStats, p);
                p.applyHit(new Hit(npc, trembleDamage, HitLook.MAGIC_DAMAGE));
            }
        }
        
        // Start ultimate tremble effects
        startEarthquakeSequence(npc, targets, titanStats, "TREMBLE");
        
        // Provide tremble education
        if (target instanceof Player && Utils.random(2) == 0) { // 50% chance
            provideEarthquakeEducation((Player) target, npc, "TREMBLE", titanStats);
        }
    }

    /**
     * Execute melee attack - FIXED TO USE PROPER NPC REFERENCE
     */
    private void executeMeleeAttack(NPC npc, Entity target, EarthquakeTitanStats titanStats) {
        // FIXED: Calculate balanced melee damage with proper NPC reference
        int meleeDamage = calculateBalancedMeleeDamage(npc, titanStats, target);
        
        target.applyHit(new Hit(npc, meleeDamage, HitLook.MELEE_DAMAGE));
        
        // Random melee animation
        npc.setNextAnimation(new Animation(attackEmotes[Utils.random(attackEmotes.length)]));
        
        // Provide melee education
        if (target instanceof Player && Utils.random(6) == 0) { // 16% chance
            provideEarthquakeEducation((Player) target, npc, "MELEE", titanStats);
        }
    }

    /**
     * Start enhanced earthquake sequence with multiple waves
     */
    private void startEarthquakeSequence(final NPC npc, final ArrayList<Entity> targets, final EarthquakeTitanStats titanStats, final String attackType) {
        WorldTasksManager.schedule(new WorldTask() {
            int wave = 0;
            
            @Override
            public void run() {
                if (wave < titanStats.earthquakeWaves) {
                    // Execute additional earthquake wave
                    for (Entity target : targets) {
                        if (target instanceof Player) {
                            Player p = (Player) target;
                            
                            // Calculate wave damage (decreasing each wave)
                            int waveDamage = 0;
                            double damageMultiplier = 1.0 - (wave * 0.2); // 100%, 80%, 60%, 40% etc.
                            
                            if (attackType.equals("EARTHQUAKE")) {
                                waveDamage = (int)(calculateBalancedEarthquakeDamage(npc, titanStats, p) * damageMultiplier);
                            } else if (attackType.equals("SHOCKWAVE")) {
                                waveDamage = (int)(calculateBalancedShockwaveDamage(npc, titanStats, p) * damageMultiplier);
                            } else if (attackType.equals("TREMBLE")) {
                                waveDamage = (int)(calculateBalancedTrembleDamage(npc, titanStats, p) * damageMultiplier);
                            }
                            
                            if (waveDamage > 0 && wave > 0) { // Skip first wave (already done)
                                p.applyHit(new Hit(npc, waveDamage, HitLook.MAGIC_DAMAGE));
                            }
                            
                            // Apply camera shake
                            int shakeIntensity = Math.max(1, titanStats.earthquakeIntensity - (wave * 10));
                            p.getPackets().sendCameraShake(3, shakeIntensity, 50, shakeIntensity, 50);
                        }
                    }
                } else {
                    // Stop camera shake and finish
                    for (Entity target : targets) {
                        if (target instanceof Player) {
                            Player p = (Player) target;
                            p.getPackets().sendStopCameraShake();
                        }
                    }
                    stop();
                }
                wave++;
            }
        }, 0, Math.max(1, titanStats.earthquakeDuration / (titanStats.earthquakeWaves * 1000))); // Distribute over duration
    }

    /**
     * FIXED: Calculate balanced earthquake damage with proper NPC reference
     */
    private int calculateBalancedEarthquakeDamage(NPC npc, EarthquakeTitanStats titanStats, Entity target) {
        // FIXED: Pass the actual NPC instead of using Utils.random
        int baseDamage = getRandomMaxHit(npc, titanStats.earthquakeMaxHit, NPCCombatDefinitions.MAGE, target);
        
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
     * FIXED: Calculate balanced shockwave damage with proper NPC reference
     */
    private int calculateBalancedShockwaveDamage(NPC npc, EarthquakeTitanStats titanStats, Entity target) {
        // FIXED: Pass the actual NPC instead of using Utils.random
        int baseDamage = getRandomMaxHit(npc, titanStats.shockwaveMaxHit, NPCCombatDefinitions.MAGE, target);
        
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
     * FIXED: Calculate balanced tremble damage with proper NPC reference
     */
    private int calculateBalancedTrembleDamage(NPC npc, EarthquakeTitanStats titanStats, Entity target) {
        // FIXED: Pass the actual NPC instead of using Utils.random
        int baseDamage = getRandomMaxHit(npc, titanStats.trembleMaxHit, NPCCombatDefinitions.MAGE, target);
        
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
     * FIXED: Calculate balanced melee damage with proper NPC reference
     */
    private int calculateBalancedMeleeDamage(NPC npc, EarthquakeTitanStats titanStats, Entity target) {
        // FIXED: Pass the actual NPC instead of using Utils.random
        int baseDamage = getRandomMaxHit(npc, titanStats.meleeMaxHit, NPCCombatDefinitions.MELEE, target);
        
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
     * Calculate earthquake damage based on attack bonus and modifier
     */
    private int calculateEarthquakeDamage(int baseMaxHit, int attackBonus, double modifier) {
        int damage = (int) (baseMaxHit * modifier);
        
        // Apply attack bonus scaling
        if (attackBonus > 0) {
            damage = (int) (damage * (1.0 + (attackBonus * 0.0008))); // 0.08% per bonus point
        }
        
        return Math.max(1, damage);
    }

    /**
     * Provide earthquake education
     */
    private void provideEarthquakeEducation(Player player, NPC npc, String attackType, EarthquakeTitanStats titanStats) {
        String username = player.getUsername();
        long currentTime = System.currentTimeMillis();
        
        // Check cooldown
        Long lastTip = playerLastEarthquakeTip.get(username);
        if (lastTip != null && (currentTime - lastTip) < EARTHQUAKE_TIP_COOLDOWN) {
            return;
        }
        
        // Check tip stage
        Integer tipStage = playerEarthquakeTipStage.get(username);
        if (tipStage == null) tipStage = 0;
        if (tipStage >= MAX_EARTHQUAKE_TIPS_PER_FIGHT) return;
        
        String tipMessage = getEarthquakeTip(titanStats.tier, attackType, tipStage);
        if (tipMessage != null) {
            player.sendMessage("<col=8B4513>Seismic Knowledge: " + tipMessage + "</col>", true);
            
            playerLastEarthquakeTip.put(username, currentTime);
            playerEarthquakeTipStage.put(username, tipStage + 1);
        }
    }

    /**
     * Get earthquake tip based on tier, attack type, and stage
     */
    private String getEarthquakeTip(int tier, String attackType, int stage) {
        if (stage == 0) {
            if (attackType.equals("MELEE")) {
                if (tier <= 4) {
                    return "Ballak's melee is crush-based and powerful! Use Protect from Melee when close.";
                } else {
                    return "Elite Ballak's melee can shatter even the strongest defenses! Stay protected.";
                }
            } else if (attackType.equals("EARTHQUAKE")) {
                if (tier <= 4) {
                    return "Standard earthquake hits all nearby players! Use Protect from Magic to reduce damage.";
                } else {
                    return "Elite earthquake has devastating multi-wave attacks! Stay protected throughout.";
                }
            } else if (attackType.equals("SHOCKWAVE")) {
                if (tier <= 4) {
                    return "Shockwave is an enhanced earthquake with stronger damage and effects!";
                } else {
                    return "Elite shockwave can devastate entire teams! Coordinate protection prayers.";
                }
            } else if (attackType.equals("TREMBLE")) {
                return "Tremble is Ballak's ultimate earthquake! Only high-tier titans can use this devastating attack.";
            }
        } else if (stage == 1) {
            return "Higher tier Ballaks use " + getEarthquakeWaveText(tier) + " Stay alert for multiple waves!";
        } else if (stage == 2) {
            return "Ballak has " + getAttackTypeText(tier) + " Adapt your strategy accordingly!";
        } else if (stage == 3) {
            return "All earthquake attacks hit everyone nearby. Team coordination is essential!";
        } else if (stage == 4) {
            return "Prayer protection works on all of Ballak's attacks. Switch prayers based on attack type!";
        } else if (stage == 5) {
            return "Seismic mastery: Higher tiers have more frequent and powerful earthquake attacks!";
        }
        return null;
    }

    /**
     * Get earthquake wave count text
     */
    private String getEarthquakeWaveText(int tier) {
        if (tier <= 3) return "2-wave earthquakes.";
        else if (tier <= 6) return "3-wave earthquakes.";
        else return "4-wave earthquakes.";
    }

    /**
     * Get attack type variety text
     */
    private String getAttackTypeText(int tier) {
        if (tier <= 3) return "basic earthquake attacks.";
        else if (tier <= 6) return "earthquake and shockwave attacks.";
        else return "earthquake, shockwave, AND tremble attacks.";
    }

    /**
     * Get boss tier name for announcements
     */
    private String getBossTierName(int tier) {
        switch (tier) {
            case 1: return "Tier 1 Earth Shaker";
            case 2: return "Tier 2 Earth Shaker";
            case 3: return "Tier 3 Seismic Warrior";
            case 4: return "Tier 4 Seismic Warrior";
            case 5: return "Tier 5 Earthquake Titan";
            case 6: return "Tier 6 Earthquake Titan";
            case 7: return "Tier 7 Elite Seismic Lord";
            case 8: return "Tier 8 Legendary Earthquake Emperor";
            case 9: return "Tier 9 Mythical Seismic God";
            case 10: return "Tier 10 Divine Earthquake Deity";
            default: return "Unknown Tier Earthquake Titan";
        }
    }

    /**
     * Estimate earthquake titan tier from combat stats
     */
    private int estimateEarthquakeTierFromStats(NPCCombatDefinitions defs) {
        int hp = defs.getHitpoints();
        int maxHit = defs.getMaxHit();
        
        // Earthquake titans are typically mid-high tier AoE specialists
        if (hp <= 3200 && maxHit <= 50) return 3;       // Seismic Warrior
        if (hp <= 6000 && maxHit <= 80) return 4;       // Enhanced Seismic Warrior
        if (hp <= 10500 && maxHit <= 125) return 5;     // Earthquake Titan
        if (hp <= 17000 && maxHit <= 185) return 6;     // Enhanced Earthquake Titan (typical Ballak)
        if (hp <= 25500 && maxHit <= 260) return 7;     // Elite Seismic Lord
        if (hp <= 36000 && maxHit <= 350) return 8;     // Legendary Earthquake Emperor
        if (hp <= 50000 && maxHit <= 460) return 9;     // Mythical Seismic God
        return 10; // Divine Earthquake Deity
    }

    /**
     * Estimate earthquake attack bonuses for tier
     */
    private int[] estimateEarthquakeAttackBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Earthquake titans have enhanced crush and magic bonuses
        return new int[]{
            baseStat,                    // stab
            (int)(baseStat * 1.1),      // slash (enhanced)
            (int)(baseStat * 1.3),      // crush (primary melee - heavily enhanced)
            (int)(baseStat * 1.4),      // magic (primary earthquake - heavily enhanced)
            (int)(baseStat * 0.8)       // ranged (reduced)
        };
    }

    /**
     * Estimate earthquake defense bonuses for tier
     */
    private int[] estimateEarthquakeDefenseBonuses(int tier) {
        int[] tierMins = {10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900};
        int[] tierMaxs = {75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500};
        
        int baseStat = (tierMins[tier - 1] + tierMaxs[tier - 1]) / 2;
        
        // Earthquake titans have very strong physical defense, good magic defense
        return new int[]{
            (int)(baseStat * 1.2),      // stab defense (enhanced earth armor)
            (int)(baseStat * 1.2),      // slash defense (enhanced earth armor)
            (int)(baseStat * 1.3),      // crush defense (very enhanced earth armor)
            (int)(baseStat * 1.1),      // magic defense (enhanced)
            baseStat                     // ranged defense (balanced)
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
        return new Object[] { 10140 }; // Ballak NPC ID
    }

    /**
     * Earthquake Titan stats container class
     */
    private static class EarthquakeTitanStats {
        public int tier;
        public int maxHit;
        public int meleeMaxHit;
        public int earthquakeMaxHit;
        public int shockwaveMaxHit;
        public int trembleMaxHit;
        public int hitpoints;
        public int[] attackBonuses;
        public int[] defenseBonuses;
        public int maxBonus;
        public int earthquakeChance;
        public int shockwaveChance;
        public int trembleChance;
        public int earthquakeIntensity;
        public int earthquakeDuration;
        public int earthquakeWaves;
        public boolean isBalanced;
        public long timestamp;
    }
}