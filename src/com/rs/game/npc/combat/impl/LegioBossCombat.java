package com.rs.game.npc.combat.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossBalancer.CombatScaling;
import com.rs.game.player.content.BossBalancer.ArmorCoverageResult;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Enhanced Legio Boss Combat with BossBalancer Integration
 * Features: HP scaling instead of damage scaling for overgeared players
 * 
 * @author Zeus
 * @date June 06, 2025
 * @version 2.0 - BossBalancer Integration with Smart HP Scaling
 * 
 * Key Features:
 * - Full BossBalancer integration with armor analysis
 * - HP scaling for overgeared players (instead of damage increases)
 * - 990 HP optimized damage ranges  
 * - Maintains familiar mechanics while adding appropriate challenge
 * - Feels fair: same damage, just longer fights for better gear
 * - Auto-configuration for all 6 Legio bosses
 */
public class LegioBossCombat extends CombatScript {
    
    // Boss Configuration - All 6 Legio bosses
    private static final int[] LEGIO_NPC_IDS = {17149, 17150, 17151, 17152, 17153, 17154};
    private static final int DEFAULT_BOSS_TYPE = 2; // Magic-based bosses
    private static final int DEFAULT_TIER = 6; // Tier 6 (Master) for Legio bosses
    
    // 990 HP OPTIMIZED DAMAGE RANGES for Tier 6 Magic Bosses
    private static final int BASIC_MIN_DAMAGE = 90;    // 9.1% of 990 HP
    private static final int BASIC_MAX_DAMAGE = 170;   // 17.2% of 990 HP
    private static final int AREA_MIN_DAMAGE = 110;    // 11.1% of 990 HP (area attack)
    private static final int AREA_MAX_DAMAGE = 200;    // 20.2% of 990 HP (area attack)
    
    // HP Scaling Constants (The Smart Solution!)
    private static final double BASE_HP_MULTIPLIER = 1.0;     // Base HP (no scaling)
    private static final double HP_SCALING_PER_TIER = 0.25;   // +25% HP per tier difference
    private static final double MAX_HP_MULTIPLIER = 3.0;      // Maximum 300% HP
    private static final double DAMAGE_STAY_SAME = 1.0;       // Damage NEVER increases (feels fair!)
    
    // Combat session tracking
    private static final Map<Integer, Boolean> combatSessionActive = new ConcurrentHashMap<Integer, Boolean>();
    private static final Map<Integer, ArmorCoverageResult> playerArmorStatus = new ConcurrentHashMap<Integer, ArmorCoverageResult>();
    private static final Map<Integer, Double> appliedHPMultiplier = new ConcurrentHashMap<Integer, Double>();
    
    // Warning system
    private static final Map<Integer, Long> lastWarning = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, Integer> warningStage = new ConcurrentHashMap<Integer, Integer>();
    private static final long WARNING_COOLDOWN = 120000; // 2 minutes between warnings
    private static final int MAX_WARNINGS_PER_FIGHT = 2;
    
    @Override
    public Object[] getKeys() {
        return new Object[] { 17149, 17150, 17151, 17152, 17153, 17154 };
    }
    
    @Override
    public int attack(final NPC npc, final Entity target) {
        if (!isValidCombatState(npc, target)) {
            return 5;
        }
        
        final NPCCombatDefinitions defs = npc.getCombatDefinitions();
        Player player = (Player) target;
        
        // ===== ENHANCED BOSS BALANCER INTEGRATION =====
        initializeEnhancedCombatSession(player, npc);
        
        // Get combat scaling and apply HP scaling (NOT damage scaling!)
        CombatScaling scaling = BossBalancer.getCombatScaling(player, npc);
        applyIntelligentHPScaling(npc, scaling, player);
        
        // Enhanced guidance with HP scaling awareness
        provideEnhancedGuidance(player, npc, scaling);
        
        // Enhanced war cries with scaling awareness
        performEnhancedWarCries(npc, scaling);
        
        // Execute the SAME familiar attack (damage stays consistent!)
        return executeEnhancedProjectileAttack(npc, target, defs, scaling);
    }
    
    /**
     * Initialize enhanced combat session with full BossBalancer integration
     */
    private void initializeEnhancedCombatSession(Player player, NPC npc) {
        Integer sessionKey = Integer.valueOf(player.getIndex());
        
        if (!combatSessionActive.containsKey(sessionKey)) {
            // Start BossBalancer combat session
            BossBalancer.startCombatSession(player, npc);
            combatSessionActive.put(sessionKey, Boolean.TRUE);
            
            // Auto-configure boss if not already configured
            if (!BossBalancer.isBossConfigured(npc.getId())) {
                BossBalancer.autoConfigureBoss(npc.getId(), DEFAULT_TIER, DEFAULT_BOSS_TYPE, "System");
                System.out.println("Auto-configured Legio " + getLegioName(npc.getId()) + " as Tier " + DEFAULT_TIER + " boss");
            }
            
            // Analyze armor and store for session
            ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            playerArmorStatus.put(sessionKey, armorResult);
            
            // Enhanced welcome message
            CombatScaling scaling = BossBalancer.getCombatScaling(player, npc);
            String welcomeMsg = getEnhancedWelcomeMessage(scaling, armorResult, npc);
            player.sendMessage(welcomeMsg);
            
            // Send armor coverage warning
            BossBalancer.sendArmorCoverageWarning(player);
        }
    }
    
    /**
     * Apply intelligent HP scaling - the SMART solution!
     * Instead of making damage higher, we make fights longer for overgeared players
     */
    private void applyIntelligentHPScaling(NPC npc, CombatScaling scaling, Player player) {
        Integer npcKey = Integer.valueOf(npc.getIndex());
        
        // Check if we've already applied scaling to this NPC
        Double existingMultiplier = appliedHPMultiplier.get(npcKey);
        if (existingMultiplier != null && existingMultiplier > 1.0) {
            return; // Already scaled
        }
        
        // Calculate HP multiplier based on tier difference
        double hpMultiplier = BASE_HP_MULTIPLIER;
        
        if (scaling.tierDifference > 0) {
            // Player is overgeared - increase boss HP (not damage!)
            hpMultiplier = BASE_HP_MULTIPLIER + (scaling.tierDifference * HP_SCALING_PER_TIER);
            hpMultiplier = Math.min(hpMultiplier, MAX_HP_MULTIPLIER);
        }
        
        // Apply HP scaling if needed
        if (hpMultiplier > 1.1) {
            int currentMaxHP = npc.getMaxHitpoints();
            int newMaxHP = (int)(currentMaxHP * hpMultiplier);
            
            // Scale both current and max HP
            npc.setHitpoints(newMaxHP);
            npc.getCombatDefinitions().setHitpoints(newMaxHP);
            
            // Store the applied multiplier
            appliedHPMultiplier.put(npcKey, hpMultiplier);
            
            // Notify player with POSITIVE framing
            String legioName = getLegioName(npc.getId());
            int hpIncrease = (int)((hpMultiplier - 1.0) * 100);
            player.sendMessage("<col=4169E1>" + legioName + " senses your power and adapts! +" + hpIncrease + "% HP for a worthy challenge.</col>");
            
            // Enhanced force talk based on scaling
            if (hpMultiplier >= 2.0) {
                npc.setNextForceTalk(new ForceTalk("Your strength demands my full power!"));
            } else {
                npc.setNextForceTalk(new ForceTalk("I must adapt to your might!"));
            }
        }
    }
    
    /**
     * Generate enhanced welcome message with positive HP scaling framing
     */
    private String getEnhancedWelcomeMessage(CombatScaling scaling, ArmorCoverageResult armorResult, NPC npc) {
        StringBuilder message = new StringBuilder();
        String legioName = getLegioName(npc.getId());
        message.append("<col=8A2BE2>").append(legioName).append(" awakens! Adaptive challenge system active (990 HP optimized).</col>");
        
        // Positive framing for scaling
        if (scaling.tierDifference > 0) {
            int hpIncrease = (int)(scaling.tierDifference * HP_SCALING_PER_TIER * 100);
            message.append(" <col=00ff00>[Superior gear detected: +").append(hpIncrease).append("% boss HP for worthy challenge]</col>");
        } else if (scaling.tierDifference < 0) {
            message.append(" <col=ffaa00>[Developing gear: Standard challenge level]</col>");
        } else {
            message.append(" <col=99ff99>[Perfect gear match: Balanced challenge]</col>");
        }
        
        // Armor status (always helpful)
        if (armorResult.hasFullArmor) {
            double protection = armorResult.damageReduction * 100;
            message.append(" <col=00ccff>[Full armor: ").append(String.format("%.0f", protection)).append("% protection]</col>");
        } else {
            message.append(" <col=ff6600>[Incomplete armor: Equip missing pieces!]</col>");
        }
        
        return message.toString();
    }
    
    /**
     * Enhanced guidance with HP scaling awareness
     */
    private void provideEnhancedGuidance(Player player, NPC npc, CombatScaling scaling) {
        Integer playerKey = Integer.valueOf(player.getIndex());
        long currentTime = System.currentTimeMillis();
        
        // Check cooldown
        Long lastWarningTime = lastWarning.get(playerKey);
        if (lastWarningTime != null && (currentTime - lastWarningTime) < WARNING_COOLDOWN) {
            return;
        }
        
        Integer currentStage = warningStage.get(playerKey);
        if (currentStage == null) currentStage = 0;
        if (currentStage >= MAX_WARNINGS_PER_FIGHT) {
            return;
        }
        
        ArmorCoverageResult armorResult = playerArmorStatus.get(playerKey);
        String guidanceMessage = null;
        
        if (currentStage == 0) {
            // First warning: Explain the POSITIVE HP scaling system
            if (scaling.tierDifference > 0) {
                int hpIncrease = (int)(scaling.tierDifference * HP_SCALING_PER_TIER * 100);
                guidanceMessage = "<col=4169E1>990 HP ADAPTIVE SYSTEM: Your superior gear triggers +" + hpIncrease + 
                                "% boss HP (damage stays the same!). Longer fight, same risk, worthy challenge!</col>";
            } else {
                guidanceMessage = "<col=99ff99>990 HP COMBAT: Standard Legio mechanics active. Dodge area attacks, expect 90-200 damage hits!</col>";
            }
        } else if (currentStage == 1) {
            // Second warning: Combat strategy
            String legioName = getLegioName(npc.getId());
            Double hpMult = appliedHPMultiplier.get(Integer.valueOf(npc.getIndex()));
            if (hpMult != null && hpMult > 1.5) {
                guidanceMessage = "<col=ffaa00>EXTENDED COMBAT: " + legioName + " has " + String.format("%.0f", hpMult * 100) + 
                                "% HP! Maintain food supplies, stay mobile, damage output is key!</col>";
            } else {
                guidanceMessage = "<col=99ccff>LEGIO STRATEGY: Stay mobile to avoid area attacks, watch for projectile telegraphs, use area healing!</col>";
            }
        }
        
        if (guidanceMessage != null) {
            player.sendMessage(guidanceMessage);
            lastWarning.put(playerKey, currentTime);
            warningStage.put(playerKey, currentStage + 1);
        }
    }
    
    /**
     * Enhanced war cries with scaling awareness
     */
    private void performEnhancedWarCries(NPC npc, CombatScaling scaling) {
        if (Utils.getRandom(10) == 0) { // 10% chance (same as original)
            Double hpMultiplier = appliedHPMultiplier.get(Integer.valueOf(npc.getIndex()));
            
            if (hpMultiplier != null && hpMultiplier >= 2.0) {
                // High HP scaling - power adaptation cries
                switch (Utils.getRandom(4)) {
                    case 0: npc.setNextForceTalk(new ForceTalk("I match your strength!")); break;
                    case 1: npc.setNextForceTalk(new ForceTalk("You deserve my full power!")); break;
                    case 2: npc.setNextForceTalk(new ForceTalk("A worthy challenge at last!")); break;
                    case 3: npc.setNextForceTalk(new ForceTalk("My true form emerges!")); break;
                }
            } else if (hpMultiplier != null && hpMultiplier > 1.2) {
                // Medium HP scaling - adaptation cries
                switch (Utils.getRandom(4)) {
                    case 0: npc.setNextForceTalk(new ForceTalk("I sense your power!")); break;
                    case 1: npc.setNextForceTalk(new ForceTalk("My strength grows!")); break;
                    case 2: npc.setNextForceTalk(new ForceTalk("You push me further!")); break;
                    case 3: npc.setNextForceTalk(new ForceTalk("I adapt to your might!")); break;
                }
            } else {
                // Standard cries (same as original)
                switch (Utils.getRandom(3)) {
                    case 0: npc.setNextForceTalk(new ForceTalk("My power increases!")); break;
                    case 1: npc.setNextForceTalk(new ForceTalk("Nothing will stop my power!")); break;
                    case 2: npc.setNextForceTalk(new ForceTalk("You will not survive this!")); break;
                }
            }
        }
    }
    
    /**
     * Execute enhanced projectile attack with 990 HP optimized damage
     * NOTE: Damage calculation stays the SAME regardless of player tier!
     */
    private int executeEnhancedProjectileAttack(final NPC npc, final Entity target, 
                                               final NPCCombatDefinitions defs, CombatScaling scaling) {
        // Create the familiar projectile (same as original)
        final WorldTile tile = new WorldTile(target);
        World.sendProjectile(npc, tile, 3978, 41, 16, 30, 0, 50, 0);
        
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                final WorldTile newTile = new WorldTile(tile);
                WorldTasksManager.schedule(new WorldTask() {
                    int count = 0;
                    @Override
                    public void run() {
                        if (count == 0) {
                            World.sendGraphics(npc, new Graphics(3974), newTile);
                            if (target.withinDistance(newTile, 2)) {
                                // Calculate 990 HP optimized damage (SAME for all players!)
                                int damage = calculateOptimizedDamage(npc, target, scaling);
                                target.applyHit(new Hit(target, damage, HitLook.MAGIC_DAMAGE));
                            }
                        }
                        if (count == 1) {
                            if (target.withinDistance(newTile, 2)) {
                                // Second hit with same damage calculation
                                int damage = calculateOptimizedDamage(npc, target, scaling);
                                target.applyHit(new Hit(target, damage, HitLook.MAGIC_DAMAGE));
                            }
                            this.stop();
                        }
                        count++;
                    }
                }, 0, 1);
                this.stop();
            }
        });
        
        return defs.getAttackDelay();
    }
    
    /**
     * Calculate 990 HP optimized damage (consistent for fairness!)
     */
    private int calculateOptimizedDamage(NPC npc, Entity target, CombatScaling scaling) {
        // Base damage range for area attack
        int baseDamage = AREA_MIN_DAMAGE + Utils.getRandom(AREA_MAX_DAMAGE - AREA_MIN_DAMAGE + 1);
        
        // Apply BossBalancer player-specific scaling (armor, prayer, etc.)
        // But NOT tier-based difficulty scaling - that's handled by HP instead!
        if (target instanceof Player) {
            Player player = (Player) target;
            
            // Apply armor reduction as normal
            ArmorCoverageResult armorResult = BossBalancer.analyzeArmorCoverage(player);
            baseDamage = (int)(baseDamage * (1.0 - armorResult.damageReduction));
            
            // Apply prayer reduction as normal
            if (scaling.prayerTier > 0) {
                double prayerReduction = Math.min(0.20, scaling.prayerTier * 0.05); // Max 20% prayer reduction
                baseDamage = (int)(baseDamage * (1.0 - prayerReduction));
            }
        }
        
        // Ensure minimum damage
        return Math.max(10, baseDamage);
    }
    
    /**
     * Get Legio boss name for messages
     */
    private String getLegioName(int npcId) {
        switch (npcId) {
            case 17149: return "Legio Primus";
            case 17150: return "Legio Secundus";
            case 17151: return "Legio Tertius";
            case 17152: return "Legio Quartus";
            case 17153: return "Legio Quintus";
            case 17154: return "Legio Sextus";
            default: return "Legio Boss";
        }
    }
    
    /**
     * Enhanced combat state validation
     */
    private boolean isValidCombatState(NPC npc, Entity target) {
        return npc != null && target != null && 
               !npc.isDead() && !target.isDead() && 
               target instanceof Player &&
               npc.getCombatDefinitions() != null;
    }
    
    /**
     * Handle equipment changes during combat
     */
    public static void onPlayerEquipmentChanged(Player player) {
        if (player == null) return;
        
        try {
            Integer playerKey = Integer.valueOf(player.getIndex());
            
            if (combatSessionActive.containsKey(playerKey)) {
                // Update armor status
                ArmorCoverageResult newArmorResult = BossBalancer.analyzeArmorCoverage(player);
                playerArmorStatus.put(playerKey, newArmorResult);
                
                // Provide positive feedback
                if (newArmorResult.hasFullArmor) {
                    player.sendMessage("<col=00ff00>Armor protection optimized! Better damage reduction active.</col>");
                } else {
                    player.sendMessage("<col=ffaa00>Armor incomplete. Consider equipping missing pieces for better protection.</col>");
                }
                
                // Clear cache for recalculation
                BossBalancer.clearPlayerCache(player.getIndex());
            }
        } catch (Exception e) {
            System.err.println("Error handling equipment change: " + e.getMessage());
        }
    }
    
    /**
     * Enhanced combat end handling
     */
    public static void onCombatEnd(Player player, NPC npc) {
        if (player == null) return;
        
        try {
            BossBalancer.endCombatSession(player);
            
            Integer playerKey = Integer.valueOf(player.getIndex());
            
            // Clear player-specific tracking data
            combatSessionActive.remove(playerKey);
            playerArmorStatus.remove(playerKey);
            lastWarning.remove(playerKey);
            warningStage.remove(playerKey);
            
            // Clear NPC-specific scaling data when boss dies
            if (npc != null && npc.isDead()) {
                Integer npcKey = Integer.valueOf(npc.getIndex());
                appliedHPMultiplier.remove(npcKey);
            }
            
            BossBalancer.clearPlayerCache(player.getIndex());
            
        } catch (Exception e) {
            System.err.println("Error ending Legio combat session: " + e.getMessage());
        }
    }
    
    /**
     * Force cleanup for logout/death
     */
    public static void forceCleanup(Player player) {
        if (player != null) {
            onCombatEnd(player, null);
        }
    }
}