package com.rs.game.player.content;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;
import com.rs.utils.Colors;
import com.rs.utils.ItemBonuses;

/**
 * Enhanced Boss Balancer System - PLAYER-FRIENDLY VERSION v5.0 COMPLETE
 * 
 * MAJOR IMPROVEMENTS v5.0 PLAYER-FRIENDLY:
 * - FIXED: Intelligent scaling based on actual player power, not just tier
 * - REMOVED: All punishment scaling for overpowered players
 * - IMPROVED: Equipment synergy detection and combat triangle awareness  
 * - ENHANCED: Absorption damage calculation with proper caps
 * - OPTIMIZED: Power ratio analysis for dynamic difficulty
 * - SECURED: Total absorption validation to prevent invincibility
 * - BALANCED: Prayer scaling integration with gear effectiveness
 * - PLAYER-FRIENDLY: Only assistance for weak players, no punishment for strong ones
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 5.0 - PLAYER-FRIENDLY COMPLETE INTELLIGENT SCALING
 */
public class BossBalancer {
    // Core data structures with strict size limits
    private static final Map<Integer, RuntimeBossConfig> runtimeConfigurations = new ConcurrentHashMap<Integer, RuntimeBossConfig>();
    private static final Set<Integer> configuredBosses = new HashSet<Integer>();

    // Configuration constants - optimized for intelligent scaling
    private static final int MAX_CACHE_ENTRIES = 500;
    private static final int MAX_MESSAGE_COOLDOWN_ENTRIES = 200;
    private static final int MAX_COMBAT_SESSIONS = 100;
    private static final long MESSAGE_COOLDOWN_MS = 30000L;
    private static final long CLEANUP_AGE_MS = 12L * 60L * 60L * 1000L;
    private static final long CLEANUP_INTERVAL_MS = 3L * 60L * 1000L;
    private static final long COMBAT_SESSION_TIMEOUT_MS = 10L * 60L * 1000L;

    // ===== INTELLIGENT SCALING CONSTANTS v5.0 PLAYER-FRIENDLY =====
    
    // Power calculation weights
    private static final double GEAR_POWER_WEIGHT = 0.4;           // Reduced from 0.7
    private static final double PRAYER_POWER_WEIGHT = 0.3;         // Increased from 0.3  
    private static final double ABSORPTION_POWER_WEIGHT = 0.2;     // New: absorption effectiveness
    private static final double SYNERGY_POWER_WEIGHT = 0.1;       // New: equipment synergy

    // Intelligent scaling thresholds - PLAYER-FRIENDLY
    private static final double UNDERPOWERED_THRESHOLD = 0.7;      // Below 70% = assistance
    private static final double BALANCED_THRESHOLD_LOW = 0.8;      // 80% - 120% = balanced
    private static final double BALANCED_THRESHOLD_HIGH = 1.2;
    private static final double OVERPOWERED_THRESHOLD = 2.0;       // Above 200% = positive message only

    // Logarithmic scaling factors (prevents extreme multipliers)
    private static final double MAX_SCALING_MULTIPLIER = 2.0;      // Reduced from 6.0
    private static final double SCALING_CURVE_FACTOR = 0.6;        // Logarithmic curve control
    private static final double DAMAGE_SCALING_REDUCTION = 0.6;    // Damage scales less than HP
    private static final double ACCURACY_SCALING_REDUCTION = 0.0;  // Accuracy scales least

    // Absorption caps matching ItemBalancer
    private static final int MAX_TOTAL_MELEE_ABSORPTION = 25;
    private static final int MAX_TOTAL_MAGIC_ABSORPTION = 20;
    private static final int MAX_TOTAL_RANGED_ABSORPTION = 22;
    private static final double MIN_DAMAGE_MULTIPLIER = 0.1;       // Always take at least 10% damage

    // Tier validation constants
    private static final int MIN_TIER = 1;
    private static final int MAX_TIER = 10;
    private static final int DEFAULT_TIER = 3;

    // Memory management with strict size limits
    private static final Map<Integer, Long> playerMessageCooldowns = new ConcurrentHashMap<Integer, Long>();
    private static final Map<Integer, PlayerPowerCache> playerPowerCache = new ConcurrentHashMap<Integer, PlayerPowerCache>();
    private static final Map<Integer, Integer> bossTierCache = new ConcurrentHashMap<Integer, Integer>();

    // Thread-safe service management
    private static volatile ScheduledExecutorService cleanupService = null;
    private static volatile boolean isInitialized = false;
    private static final Object initLock = new Object();

    // Performance tracking
    private static volatile long lastCleanupTime = 0L;
    private static volatile int totalCalculations = 0;
    private static volatile int intelligentCalculations = 0;

    // Combat state management
    private static final Map<Integer, CombatSession> activeCombatSessions = new ConcurrentHashMap<Integer, CombatSession>();

    /**
     * Player power cache for performance optimization
     */
    public static class PlayerPowerCache {
        public final double totalPower;
        public final double gearPower;
        public final double prayerPower;
        public final double absorptionPower;
        public final double synergyPower;
        public final long timestamp;
        public final boolean hasFullArmor;

        public PlayerPowerCache(double totalPower, double gearPower, double prayerPower, 
                               double absorptionPower, double synergyPower, boolean hasFullArmor) {
            this.totalPower = totalPower;
            this.gearPower = gearPower;
            this.prayerPower = prayerPower;
            this.absorptionPower = absorptionPower;
            this.synergyPower = synergyPower;
            this.hasFullArmor = hasFullArmor;
            this.timestamp = System.currentTimeMillis();
        }
    }

    /**
     * Combat session tracking for consistent scaling during fights
     */
    public static class CombatSession {
        public final int playerId;
        public final int bossId;
        public volatile double lockedPlayerPower;
        public volatile double lockedBossPower;
        public final long sessionStart;
        public volatile boolean powerLocked;

        public CombatSession(int playerId, int bossId, double playerPower, double bossPower) {
            this.playerId = playerId;
            this.bossId = bossId;
            this.lockedPlayerPower = playerPower;
            this.lockedBossPower = bossPower;
            this.sessionStart = System.currentTimeMillis();
            this.powerLocked = false;
        }
    }

    // Initialize cleanup service with proper synchronization
    static {
        initializeCleanupService();
    }

    /**
     * Initialize automatic cleanup service - Java 7 compatible
     */
    private static void initializeCleanupService() {
        synchronized (initLock) {
            if (!isInitialized) {
                try {
                    cleanupService = Executors.newSingleThreadScheduledExecutor();
                    cleanupService.scheduleAtFixedRate(new Runnable() {
                        public void run() {
                            try {
                                performAutomaticCleanup();
                            } catch (Exception e) {
                                System.err.println("BossBalancer: Error in automatic cleanup: " + e.getMessage());
                            }
                        }
                    }, CLEANUP_INTERVAL_MS, CLEANUP_INTERVAL_MS, TimeUnit.MILLISECONDS);

                    isInitialized = true;
                    System.out.println("BossBalancer v5.0: Player-Friendly intelligent scaling system initialized");
                } catch (Exception e) {
                    System.err.println("BossBalancer: Failed to initialize cleanup service: " + e.getMessage());
                }
            }
        }
    }

    // ===== MAIN INTELLIGENT COMBAT SCALING SYSTEM - PLAYER-FRIENDLY =====

    /**
     * MAIN INTELLIGENT COMBAT SCALING METHOD - v5.0 PLAYER-FRIENDLY
     */
    public static CombatScaling getIntelligentCombatScaling(Player player, NPC boss) {
        // Early null validation
        if (player == null) {
            System.err.println("BossBalancer: getIntelligentCombatScaling called with null player");
            return createDefaultScaling("ERROR_NULL_PLAYER");
        }

        // Rise of the Six exclusion with null safety
        try {
            if (player.getControlerManager() != null && player.getControlerManager().getControler() != null) {
                String controllerClass = player.getControlerManager().getControler().getClass().getSimpleName();
                if ("RiseOfTheSix".equals(controllerClass)) {
                    return createDefaultScaling("MINIGAME_EXCLUDED");
                }
            }
        } catch (Exception e) {
            // Ignore controller errors and continue
        }

        totalCalculations++;
        intelligentCalculations++;

        try {
            // Calculate actual player and boss power
            double playerPower = calculateActualPlayerPower(player);
            double bossPower = calculateBossPower(boss);

            // Power ratio determines scaling approach
            double powerRatio = playerPower / bossPower;

            // Create scaling based on intelligent analysis
            CombatScaling scaling = new CombatScaling();
            
            // Store power analysis
            scaling.playerPower = playerPower;
            scaling.bossPower = bossPower;
            scaling.powerRatio = powerRatio;

            // Apply PLAYER-FRIENDLY scaling logic (keeps assistance, removes punishment)
            if (powerRatio < UNDERPOWERED_THRESHOLD) {
                applyUnderpoweredAssistance(scaling, player, boss);
            } else {
                // ALWAYS use balanced encounter for normal/overpowered players
                applyBalancedEncounter(scaling, player, boss);
                
                // Optional: Add positive message for overpowered players
                if (powerRatio > OVERPOWERED_THRESHOLD) {
                    scaling.shouldWarnPlayer = true;
                    scaling.warningMessage = getPositiveOverpoweredMessage(powerRatio, boss);
                }
            }

            // Apply absorption damage reduction
            applyAbsorptionEffects(scaling, player);

            // Apply armor coverage analysis (PLAYER-FRIENDLY - no punishment)
            applyArmorCoverageAnalysis(scaling, player);

            return scaling;

        } catch (Exception e) {
            System.err.println("BossBalancer: Error in getIntelligentCombatScaling: " + e.getMessage());
            e.printStackTrace();
            return createDefaultScaling("ERROR_EXCEPTION");
        }
    }

    /**
     * Calculate actual player power using multiple factors
     */
    public static double calculateActualPlayerPower(Player player) {
        if (player == null) {
            return 1.0;
        }

        try {
            // Check cache first
            Integer playerId = Integer.valueOf(player.getIndex());
            PlayerPowerCache cached = playerPowerCache.get(playerId);
            
            if (cached != null && (System.currentTimeMillis() - cached.timestamp) < 30000L) {
                return cached.totalPower; // Use cached value if less than 30 seconds old
            }

            // Calculate gear power (logarithmic scaling)
            int gearTier = calculatePlayerGearTier(player);
            double gearPower = Math.pow(gearTier, 1.4); // Slower growth than quadratic

            // Calculate prayer power (more significant now)
            double prayerTier = calculatePlayerPrayerTier(player);
            double prayerPower = 1.0 + (prayerTier * 0.4); // 40% multiplier per prayer tier

            // Calculate absorption effectiveness
            double absorptionPower = calculateAbsorptionEffectiveness(player);

            // Calculate equipment synergy
            double synergyPower = calculateEquipmentSynergy(player);

            // Weighted total power calculation
            double totalPower = (gearPower * GEAR_POWER_WEIGHT) +
                               (prayerPower * PRAYER_POWER_WEIGHT) +
                               (absorptionPower * ABSORPTION_POWER_WEIGHT) +
                               (synergyPower * SYNERGY_POWER_WEIGHT);

            // Cache the result
            boolean hasFullArmor = checkFullArmorCoverage(player);
            PlayerPowerCache newCache = new PlayerPowerCache(totalPower, gearPower, prayerPower, 
                                                           absorptionPower, synergyPower, hasFullArmor);
            
            if (playerPowerCache.size() < MAX_CACHE_ENTRIES) {
                playerPowerCache.put(playerId, newCache);
            }

            return Math.max(1.0, totalPower);

        } catch (Exception e) {
            System.err.println("BossBalancer: Error calculating player power: " + e.getMessage());
            return 1.0;
        }
    }

    /**
     * Calculate player's gear tier with enhanced validation
     */
    public static int calculatePlayerGearTier(Player player) {
        if (player == null || player.getEquipment() == null) {
            return MIN_TIER;
        }

        try {
            ItemsContainer<Item> equipment = player.getEquipment().getItems();
            if (equipment == null) {
                return MIN_TIER;
            }

            int totalTierPoints = 0;
            int itemCount = 0;
            boolean hasMainhand = false;
            boolean hasOffhand = false;

            int equipmentSize = equipment.getSize();
            for (int slot = 0; slot < Math.min(equipmentSize, 11); slot++) {
                try {
                    Item item = equipment.get(slot);
                    if (item != null && item.getId() > 0) {
                        int itemTier = getItemEffectiveTier(item.getId());
                        if (itemTier > 0) {
                            totalTierPoints += itemTier;
                            itemCount++;

                            if (slot == 3) {
                                hasMainhand = true;
                            } else if (slot == 5 && isOffhandWeapon(item.getId())) {
                                hasOffhand = true;
                            }
                        }
                    }
                } catch (Exception e) {
                    continue;
                }
            }

            if (itemCount == 0) {
                return MIN_TIER;
            }

            int averageTier = Math.round((float) totalTierPoints / itemCount);

            // Dual-wield bonus
            if (hasMainhand && hasOffhand) {
                averageTier = Math.min(averageTier + 1, MAX_TIER);
            }

            return Math.max(MIN_TIER, Math.min(MAX_TIER, averageTier));

        } catch (Exception e) {
            System.err.println("BossBalancer: Error calculating player gear tier: " + e.getMessage());
            return MIN_TIER;
        }
    }

    /**
     * Calculate player's prayer tier with enhanced null safety
     */
    public static double calculatePlayerPrayerTier(Player player) {
        if (player == null || player.getPrayer() == null) {
            return 0.0;
        }

        try {
            // Get prayer multipliers safely
            double attackMult = player.getPrayer().getAttackMultiplier();
            double strengthMult = player.getPrayer().getStrengthMultiplier();
            double defenceMult = player.getPrayer().getDefenceMultiplier();
            double rangeMult = player.getPrayer().getRangeMultiplier();
            double mageMult = player.getPrayer().getMageMultiplier();

            // Calculate total prayer bonus (more accurate)
            double totalPrayerBonus = (attackMult - 1.0) + (strengthMult - 1.0) + (defenceMult - 1.0)
                    + (rangeMult - 1.0) + (mageMult - 1.0);

            // Convert to tier with improved scaling
            double prayerTier = 0.0;
            if (totalPrayerBonus >= 1.2) {
                prayerTier = 3.0; // Very high prayers
            } else if (totalPrayerBonus >= 0.8) {
                prayerTier = 2.5;
            } else if (totalPrayerBonus >= 0.6) {
                prayerTier = 2.0;
            } else if (totalPrayerBonus >= 0.4) {
                prayerTier = 1.5;
            } else if (totalPrayerBonus >= 0.25) {
                prayerTier = 1.0;
            } else if (totalPrayerBonus >= 0.15) {
                prayerTier = 0.5;
            } else if (totalPrayerBonus > 0.05) {
                prayerTier = 0.3;
            }

            // Bonus for high-level prayers
            try {
                if (player.getPrayer().usingPrayer(0, 27) || player.getPrayer().usingPrayer(0, 28) ||
                    player.getPrayer().usingPrayer(0, 29) || player.getPrayer().usingPrayer(1, 19) ||
                    player.getPrayer().usingPrayer(1, 22) || player.getPrayer().usingPrayer(1, 23) ||
                    player.getPrayer().usingPrayer(1, 24)) {
                    prayerTier += 0.5;
                }
            } catch (Exception e) {
                // Ignore prayer check errors
            }

            return Math.min(3.5, prayerTier); // Cap at 3.5

        } catch (Exception e) {
            System.err.println("BossBalancer: Error calculating prayer tier: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Calculate absorption effectiveness (damage reduction power)
     */
    private static double calculateAbsorptionEffectiveness(Player player) {
        if (player == null || player.getEquipment() == null) {
            return 1.0;
        }

        try {
            ItemsContainer<Item> equipment = player.getEquipment().getItems();
            if (equipment == null) {
                return 1.0;
            }

            int totalMeleeAbs = 0, totalMagicAbs = 0, totalRangedAbs = 0;

            for (int slot = 0; slot < equipment.getSize(); slot++) {
                Item item = equipment.get(slot);
                if (item != null && item.getId() > 0) {
                    int[] bonuses = ItemBonuses.getItemBonuses(item.getId());
                    if (bonuses != null && bonuses.length >= 14) {
                        totalMeleeAbs += Math.max(0, bonuses[11]);
                        totalMagicAbs += Math.max(0, bonuses[12]);
                        totalRangedAbs += Math.max(0, bonuses[13]);
                    }
                }
            }

            // Apply caps
            totalMeleeAbs = Math.min(totalMeleeAbs, MAX_TOTAL_MELEE_ABSORPTION);
            totalMagicAbs = Math.min(totalMagicAbs, MAX_TOTAL_MAGIC_ABSORPTION);
            totalRangedAbs = Math.min(totalRangedAbs, MAX_TOTAL_RANGED_ABSORPTION);

            // Average absorption across combat types
            double avgAbsorption = (totalMeleeAbs + totalMagicAbs + totalRangedAbs) / 3.0;

            // Convert absorption to power multiplier
            // 20% absorption = 25% more effective (1 / 0.8 = 1.25)
            double absorptionMultiplier = 1.0 / (1.0 - Math.min(0.35, avgAbsorption / 100.0));

            return Math.min(2.5, absorptionMultiplier); // Cap at 2.5x effectiveness

        } catch (Exception e) {
            return 1.0;
        }
    }

    /**
     * Calculate equipment synergy (matching sets, combat triangle)
     */
    private static double calculateEquipmentSynergy(Player player) {
        if (player == null || player.getEquipment() == null) {
            return 1.0;
        }

        try {
            ItemsContainer<Item> equipment = player.getEquipment().getItems();
            if (equipment == null) {
                return 1.0;
            }

            // Check for matching armor sets
            Map<String, Integer> armorTypes = new HashMap<String, Integer>();
            int totalPieces = 0;

            for (int slot = 0; slot < equipment.getSize(); slot++) {
                Item item = equipment.get(slot);
                if (item != null && item.getId() > 0) {
                    String armorType = getArmorType(item.getId());
                    if (armorType != null) {
                        armorTypes.put(armorType, armorTypes.containsKey(armorType) ? 
                                      armorTypes.get(armorType) + 1 : 1);
                        totalPieces++;
                    }
                }
            }

            // Calculate set bonus
            double setBonusMultiplier = 1.0;
            for (Map.Entry<String, Integer> entry : armorTypes.entrySet()) {
                int count = entry.getValue();
                if (count >= 3) { // 3+ pieces of same type
                    setBonusMultiplier += (count * 0.04); // 4% bonus per matching piece
                }
            }

            // Full armor bonus
            if (checkFullArmorCoverage(player)) {
                setBonusMultiplier += 0.15; // 15% bonus for full armor
            }

            return Math.min(1.8, setBonusMultiplier); // Cap at 80% bonus

        } catch (Exception e) {
            return 1.0;
        }
    }

    /**
     * Check if player has full armor coverage
     */
    private static boolean checkFullArmorCoverage(Player player) {
        if (player == null || player.getEquipment() == null) {
            return false;
        }

        try {
            ItemsContainer<Item> equipment = player.getEquipment().getItems();
            if (equipment == null) {
                return false;
            }

            // Check essential slots: helmet, body, legs, gloves, boots
            int[] essentialSlots = {0, 4, 6, 7, 8}; // helmet, body, legs, gloves, boots
            
            for (int slot : essentialSlots) {
                if (slot < equipment.getSize()) {
                    Item item = equipment.get(slot);
                    if (item == null || item.getId() <= 0) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Calculate boss power based on tier and stats
     */
    private static double calculateBossPower(NPC boss) {
        if (boss == null) {
            return 1.0;
        }

        try {
            int bossTier = getBossEffectiveTier(boss);
            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(boss.getId());

            if (def == null) {
                return Math.pow(bossTier, 1.4);
            }

            // Base power from tier (logarithmic)
            double basePower = Math.pow(bossTier, 1.4);

            // Adjust for combat level
            double levelMultiplier = Math.max(0.6, Math.min(2.0, def.combatLevel / 150.0));

            // Adjust for size (larger = more powerful)
            double sizeMultiplier = 1.0 + (def.size * 0.08);

            return basePower * levelMultiplier * sizeMultiplier;

        } catch (Exception e) {
            return 1.0;
        }
    }

    // ===== PLAYER-FRIENDLY SCALING LOGIC =====

    /**
     * Apply underpowered assistance (make boss easier)
     */
    private static void applyUnderpoweredAssistance(CombatScaling scaling, Player player, NPC boss) {
        double assistanceLevel = (UNDERPOWERED_THRESHOLD - scaling.powerRatio) * 1.5;
        assistanceLevel = Math.min(0.4, assistanceLevel); // Max 40% assistance

        scaling.bossHpMultiplier = 1.0 - assistanceLevel;
        scaling.bossDamageMultiplier = 1.0 - (assistanceLevel * 0.8);
        scaling.bossAccuracyMultiplier = 1.0;
        scaling.scalingType = "UNDERPOWERED_ASSISTANCE";
        scaling.shouldWarnPlayer = true;
        scaling.warningMessage = getUnderpoweredWarning(scaling.powerRatio, boss, assistanceLevel);

        if (player != null) {
            sendWarningWithCooldown(player, scaling.warningMessage);
        }
    }

    /**
     * Apply balanced encounter (no scaling) - PLAYER-FRIENDLY DEFAULT
     */
    private static void applyBalancedEncounter(CombatScaling scaling, Player player, NPC boss) {
        scaling.bossHpMultiplier = 1.0;
        scaling.bossDamageMultiplier = 1.0;
        scaling.bossAccuracyMultiplier = 1.0;
        scaling.scalingType = "BALANCED_ENCOUNTER";
        scaling.shouldWarnPlayer = false;
        scaling.warningMessage = "";
    }

    /**
     * DISABLED: Apply mild overgear scaling (make it do nothing - PLAYER-FRIENDLY)
     */
    private static void applyMildOvergearScaling(CombatScaling scaling, Player player, NPC boss) {
        // DISABLED: No longer makes bosses harder for geared players
        // Just apply balanced scaling instead
        applyBalancedEncounter(scaling, player, boss);
    }

    /**
     * DISABLED: Apply intelligent anti-farming (make it do nothing - PLAYER-FRIENDLY)
     */
    private static void applyIntelligentAntiFarming(CombatScaling scaling, Player player, NPC boss) {
        // DISABLED: No longer punishes players for being strong
        // Just apply balanced scaling instead
        applyBalancedEncounter(scaling, player, boss);
        
        // Keep the message for awareness, but make it positive
        scaling.shouldWarnPlayer = true;
        scaling.warningMessage = getPositiveOverpoweredMessage(scaling.powerRatio, boss);
    }

    /**
     * ADD: Positive message for overpowered players
     */
    private static String getPositiveOverpoweredMessage(double powerRatio, NPC boss) {
        try {
            String bossName = "Boss";
            if (boss != null) {
                NPCDefinitions def = NPCDefinitions.getNPCDefinitions(boss.getId());
                if (def != null && def.getName() != null) {
                    bossName = def.getName();
                }
            }

            if (powerRatio >= 3.0) {
                return "<col=00ff00>MASTER: Your power vastly exceeds " + bossName + 
                       "! You've mastered this content. Consider higher-tier challenges for greater rewards!</col>";
            } else if (powerRatio >= 2.0) {
                return "<col=00ff00>EXPERT: You're significantly stronger than " + bossName + 
                       "! This battle will be easy. Higher-tier bosses offer better rewards!</col>";
            } else {
                return "<col=00ff00>STRONG: Your equipment outclasses " + bossName + 
                       "! You've progressed well. Try higher-tier content for optimal challenge!</col>";
            }

        } catch (Exception e) {
            return "<col=00ff00>You are strong! Consider trying higher-tier content for better rewards!</col>";
        }
    }

    /**
     * Apply absorption effects to damage calculation
     */
    private static void applyAbsorptionEffects(CombatScaling scaling, Player player) {
        if (player == null || player.getEquipment() == null) {
            return;
        }

        try {
            PlayerPowerCache powerCache = playerPowerCache.get(Integer.valueOf(player.getIndex()));
            if (powerCache != null) {
                // Reduce boss damage based on absorption effectiveness
                double absorptionReduction = 1.0 / powerCache.absorptionPower;
                scaling.bossDamageMultiplier *= absorptionReduction;
                
                // Ensure minimum damage
                scaling.bossDamageMultiplier = Math.max(MIN_DAMAGE_MULTIPLIER, scaling.bossDamageMultiplier);
                
                // Update scaling type if significant absorption
                if (powerCache.absorptionPower > 1.2) {
                    scaling.scalingType += "_WITH_ABSORPTION";
                }
            }

        } catch (Exception e) {
            System.err.println("BossBalancer: Error applying absorption effects: " + e.getMessage());
        }
    }

    /**
     * FIXED: Apply armor coverage analysis WITHOUT punishment
     */
    private static void applyArmorCoverageAnalysis(CombatScaling scaling, Player player) {
        if (player == null) {
            return;
        }

        try {
            ArmorCoverageResult armorResult = analyzeArmorCoverage(player);
            
            // REMOVED: Punishment for missing armor
            /*
            if (!armorResult.hasFullArmor) {
                scaling.bossDamageMultiplier *= 1.25; // 25% more damage - REMOVED!
                scaling.scalingType += "_INCOMPLETE_ARMOR";
                scaling.shouldWarnPlayer = true;
                if (!scaling.warningMessage.isEmpty()) {
                    scaling.warningMessage += " ";
                }
                scaling.warningMessage += "WARNING: Missing armor increases damage taken!";
            }
            */
            
            // KEEP: Only positive effects for full armor
            if (armorResult.hasFullArmor) {
                // Slight damage reduction with full armor (reward, not punishment)
                scaling.bossDamageMultiplier *= (1.0 - armorResult.damageReduction * 0.3);
                scaling.scalingType += "_FULL_ARMOR_BONUS";
            }

            // Store armor analysis (keep for diagnostics)
            scaling.armorAnalysis = armorResult.analysis;

        } catch (Exception e) {
            System.err.println("BossBalancer: Error applying armor coverage analysis: " + e.getMessage());
        }
    }

    // ===== LEGACY COMPATIBILITY METHODS =====

    /**
     * Legacy method for backward compatibility
     */
    public static CombatScaling getCombatScaling(Player player, NPC boss) {
        return getIntelligentCombatScaling(player, boss);
    }

    /**
     * Apply boss scaling with intelligent calculation
     */
    public static int applyBossScaling(int originalDamage, Player player, NPC boss) {
        if (originalDamage <= 0 || player == null || boss == null) {
            return originalDamage;
        }

        try {
            CombatScaling scaling = getIntelligentCombatScaling(player, boss);
            if (scaling == null) {
                return originalDamage;
            }
            
            int scaledDamage = (int) Math.round(originalDamage * scaling.bossDamageMultiplier);
            
            // Ensure minimum damage of 1 if original was > 0
            return Math.max(1, scaledDamage);
            
        } catch (Exception e) {
            System.err.println("BossBalancer: Error applying boss scaling: " + e.getMessage());
            return originalDamage;
        }
    }

    /**
     * DISABLED: ACCURACY SCALING COMPLETELY (Player-Friendly Fix)
     */
    public static int applyBossAccuracyScaling(int originalAccuracy, Player player, NPC boss) {
        // ALREADY DISABLED - Good!
        return originalAccuracy;
    }

    /**
     * Apply boss HP scaling
     */
    public static int applyBossHpScaling(int originalHp, Player player, NPC boss) {
        if (originalHp <= 0 || player == null || boss == null) {
            return originalHp;
        }

        try {
            CombatScaling scaling = getIntelligentCombatScaling(player, boss);
            if (scaling == null) {
                return originalHp;
            }
            return (int) Math.round(originalHp * scaling.bossHpMultiplier);
        } catch (Exception e) {
            System.err.println("BossBalancer: Error applying boss HP scaling: " + e.getMessage());
            return originalHp;
        }
    }

    /**
     * Apply boss max hit scaling
     */
    public static int applyBossMaxHitScaling(int originalMaxHit, Player player, NPC boss) {
        if (originalMaxHit <= 0 || player == null || boss == null) {
            return originalMaxHit;
        }

        try {
            CombatScaling scaling = getIntelligentCombatScaling(player, boss);
            if (scaling == null) {
                return originalMaxHit;
            }
            return (int) Math.round(originalMaxHit * scaling.bossDamageMultiplier);
        } catch (Exception e) {
            System.err.println("BossBalancer: Error applying boss max hit scaling: " + e.getMessage());
            return originalMaxHit;
        }
    }

    /**
     * Calculate NPC damage to player with boss balancer integration
     */
    public static int calculateNPCDamageToPlayer(NPC npc, Player player, int baseDamage) {
        if (npc == null || player == null || baseDamage <= 0) {
            return baseDamage;
        }

        try {
            return applyBossScaling(baseDamage, player, npc);
        } catch (Exception e) {
            System.err.println("BossBalancer: Error calculating NPC damage to player: " + e.getMessage());
            return baseDamage;
        }
    }

    // ===== WARNING SYSTEM =====

    /**
     * Generate underpowered warning
     */
    private static String getUnderpoweredWarning(double powerRatio, NPC boss, double assistanceLevel) {
        try {
            String bossName = "Boss";
            if (boss != null) {
                NPCDefinitions def = NPCDefinitions.getNPCDefinitions(boss.getId());
                if (def != null && def.getName() != null) {
                    bossName = def.getName();
                }
            }

            int assistancePercent = (int) (assistanceLevel * 100);
            
            return "<col=00ff00>ASSISTANCE MODE: You're underpowered for " + bossName + 
                   "! Boss difficulty reduced by " + assistancePercent + 
                   "% to help you. Consider upgrading your equipment!</col>";

        } catch (Exception e) {
            return "<col=00ff00>You're underpowered for this boss! Boss difficulty reduced to help you!";
        }
    }

    /**
     * Send warning with cooldown and null safety
     */
    public static void sendWarningWithCooldown(Player player, String message) {
        if (player == null || message == null || message.isEmpty()) {
            return;
        }

        try {
            int playerId = player.getIndex();
            long currentTime = System.currentTimeMillis();

            Integer playerIdObj = Integer.valueOf(playerId);
            Long lastWarning = playerMessageCooldowns.get(playerIdObj);

            if (lastWarning != null && (currentTime - lastWarning.longValue()) < MESSAGE_COOLDOWN_MS) {
                return;
            }

            player.sendMessage(message);

            // Manage cooldown map size
            if (playerMessageCooldowns.size() < MAX_MESSAGE_COOLDOWN_ENTRIES) {
                playerMessageCooldowns.put(playerIdObj, Long.valueOf(currentTime));
            }

        } catch (Exception e) {
            System.err.println("BossBalancer: Error sending warning message: " + e.getMessage());
        }
    }

    /**
     * Send armor coverage warning to player
     */
    public static void sendArmorCoverageWarning(Player player) {
        if (player == null) {
            return;
        }
        
        try {
            ArmorCoverageResult result = analyzeArmorCoverage(player);
            
            if (!result.hasFullArmor) {
                player.sendMessage("<col=ff3300>⚠ ARMOR WARNING: You're missing essential armor pieces!</col>");
                player.sendMessage("<col=ff6600>While this won't increase boss damage, full armor provides protection bonuses.</col>");
                
                // Send quick missing items list
                ItemsContainer<Item> equipment = player.getEquipment().getItems();
                if (equipment != null) {
                    StringBuilder missing = new StringBuilder("Missing: ");
                    boolean first = true;
                    
                    if (equipment.get(0) == null || equipment.get(0).getId() <= 0) {
                        missing.append("Helmet");
                        first = false;
                    }
                    if (equipment.get(4) == null || equipment.get(4).getId() <= 0) {
                        if (!first) missing.append(", ");
                        missing.append("Body");
                        first = false;
                    }
                    if (equipment.get(6) == null || equipment.get(6).getId() <= 0) {
                        if (!first) missing.append(", ");
                        missing.append("Legs");
                        first = false;
                    }
                    if (equipment.get(7) == null || equipment.get(7).getId() <= 0) {
                        if (!first) missing.append(", ");
                        missing.append("Gloves");
                        first = false;
                    }
                    if (equipment.get(8) == null || equipment.get(8).getId() <= 0) {
                        if (!first) missing.append(", ");
                        missing.append("Boots");
                    }
                    
                    player.sendMessage("<col=ffaa00>" + missing.toString() + "</col>");
                }
            } else {
                double reductionPercent = result.damageReduction * 100;
                player.sendMessage("<col=00ff00>✓ Full armor protection active! " + 
                                 String.format("%.1f", reductionPercent) + "% damage reduction bonus.</col>");
            }
            
        } catch (Exception e) {
            System.err.println("BossBalancer: Error sending armor warning: " + e.getMessage());
        }
    }

    // ===== UTILITY METHODS =====

    /**
     * Get item's effective tier with null safety
     */
    private static int getItemEffectiveTier(int itemId) {
        if (itemId <= 0) {
            return 0;
        }

        try {
            int[] bonuses = ItemBonuses.getItemBonuses(itemId);
            if (bonuses == null || bonuses.length == 0) {
                return 0;
            }

            int maxBonus = 0;
            for (int bonus : bonuses) {
                if (bonus > maxBonus) {
                    maxBonus = bonus;
                }
            }

            return estimateItemTierFromBonus(maxBonus);

        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Estimate tier from bonus with updated ranges
     */
    private static int estimateItemTierFromBonus(int maxBonus) {
        if (maxBonus >= 375) return 10;
        if (maxBonus >= 300) return 9;
        if (maxBonus >= 240) return 8;
        if (maxBonus >= 185) return 7;
        if (maxBonus >= 140) return 6;
        if (maxBonus >= 100) return 5;
        if (maxBonus >= 70) return 4;
        if (maxBonus >= 45) return 3;
        if (maxBonus >= 25) return 2;
        if (maxBonus >= 10) return 1;
        return 0;
    }

    /**
     * Enhanced offhand weapon detection
     */
    private static boolean isOffhandWeapon(int itemId) {
        if (itemId <= 0) {
            return false;
        }

        try {
            ItemDefinitions def = ItemDefinitions.getItemDefinitions(itemId);
            if (def == null || def.getName() == null) {
                return false;
            }

            String itemName = def.getName().toLowerCase();
            return itemName.contains("off-hand") || itemName.contains("offhand");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Determine armor type for synergy calculations
     */
    private static String getArmorType(int itemId) {
        try {
            ItemDefinitions def = ItemDefinitions.getItemDefinitions(itemId);
            if (def == null || def.getName() == null) {
                return null;
            }

            String name = def.getName().toLowerCase();

            if (name.contains("melee") || name.contains("plate") || name.contains("chainmail") || 
                name.contains("rune") || name.contains("dragon") || name.contains("barrows")) {
                return "melee";
            } else if (name.contains("magic") || name.contains("robe") || name.contains("wizard") ||
                      name.contains("mystic") || name.contains("lunar")) {
                return "magic";
            } else if (name.contains("range") || name.contains("leather") || name.contains("dragonhide") ||
                      name.contains("void")) {
                return "ranged";
            }

            return "hybrid";

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get boss effective tier with caching
     */
    public static int getBossEffectiveTier(NPC boss) {
        if (boss == null) {
            return DEFAULT_TIER;
        }

        try {
            int bossId = boss.getId();
            
            // Check cache first
            Integer cachedTier = bossTierCache.get(Integer.valueOf(bossId));
            if (cachedTier != null) {
                return cachedTier.intValue();
            }

            // Check configuration
            BossConfiguration config = getBossConfiguration(bossId);
            if (config != null) {
                int tier = Math.max(MIN_TIER, Math.min(MAX_TIER, config.tier));
                bossTierCache.put(Integer.valueOf(bossId), Integer.valueOf(tier));
                return tier;
            }

            // Calculate from stats
            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(bossId);
            if (def == null) {
                return DEFAULT_TIER;
            }

            int calculatedTier = calculateBossTierFromStats(def.combatLevel, def.size, bossId, def);
            bossTierCache.put(Integer.valueOf(bossId), Integer.valueOf(calculatedTier));
            return calculatedTier;

        } catch (Exception e) {
            System.err.println("BossBalancer: Error getting boss tier: " + e.getMessage());
            return DEFAULT_TIER;
        }
    }

    /**
     * Calculate boss tier from stats with improved scaling
     */
    private static int calculateBossTierFromStats(int combatLevel, int size, int bossId, NPCDefinitions def) {
        try {
            // Base tier from combat level (improved ranges)
            int tierFromLevel = 1;
            if (combatLevel >= 1000) tierFromLevel = 10;
            else if (combatLevel >= 800) tierFromLevel = 9;
            else if (combatLevel >= 600) tierFromLevel = 8;
            else if (combatLevel >= 450) tierFromLevel = 7;
            else if (combatLevel >= 300) tierFromLevel = 6;
            else if (combatLevel >= 200) tierFromLevel = 5;
            else if (combatLevel >= 130) tierFromLevel = 4;
            else if (combatLevel >= 80) tierFromLevel = 3;
            else if (combatLevel >= 40) tierFromLevel = 2;

            // Tier from size
            int tierFromSize = Math.min(size + 2, 10);
            if (tierFromSize == 0) tierFromSize = 1;

            // Tier from properties
            int tierFromProperties = getBossTierFromProperties(def);

            // Weighted average
            int calculatedTier = (int) Math.round(tierFromLevel * 0.6 + tierFromSize * 0.2 + tierFromProperties * 0.2);
            return Math.max(MIN_TIER, Math.min(MAX_TIER, calculatedTier));

        } catch (Exception e) {
            return DEFAULT_TIER;
        }
    }

    /**
     * Get boss tier from properties
     */
    private static int getBossTierFromProperties(NPCDefinitions def) {
        if (def == null || def.name == null) {
            return DEFAULT_TIER;
        }

        try {
            String name = def.name.toLowerCase();

            if (name.contains("king") || name.contains("queen") || name.contains("lord") || 
                name.contains("master") || name.contains("ancient")) {
                return 8;
            }
            if (name.contains("general") || name.contains("commander") || name.contains("champion") ||
                name.contains("prime") || name.contains("supreme")) {
                return 7;
            }
            if (name.contains("boss") || name.contains("giant") || name.contains("dragon") ||
                name.contains("demon") || name.contains("greater")) {
                return 6;
            }
            if (name.contains("captain") || name.contains("chief") || name.contains("elite") ||
                name.contains("sergeant")) {
                return 5;
            }
            if (def.hasAttackOption()) {
                return 4;
            }

            return DEFAULT_TIER;

        } catch (Exception e) {
            return DEFAULT_TIER;
        }
    }

    /**
     * Get boss effective tier by ID
     */
    public static int getBossEffectiveTierById(int bossId) {
        try {
            // First check runtime configuration
            RuntimeBossConfig runtimeConfig = runtimeConfigurations.get(Integer.valueOf(bossId));
            if (runtimeConfig != null) {
                return runtimeConfig.tier;
            }

            // Then check file configuration
            BossConfiguration config = getBossConfiguration(bossId);
            if (config != null) {
                return config.tier;
            }

            // Fall back to automatic calculation
            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(bossId);
            if (def == null) {
                return DEFAULT_TIER;
            }

            int tier = calculateBossTierFromStats(def.combatLevel, def.size, bossId, def);
            return Math.max(MIN_TIER, Math.min(MAX_TIER, tier));

        } catch (Exception e) {
            System.err.println("BossBalancer: Error getting boss tier for ID " + bossId + ": " + e.getMessage());
            return DEFAULT_TIER;
        }
    }

    // ===== DIAGNOSTIC AND COMMAND METHODS =====

    /**
     * Enhanced diagnostic command with intelligent scaling info
     */
    public static void handleBalanceDiagnosticCommand(Player player, String[] cmd) {
        if (player == null) {
            return;
        }

        try {
            if (cmd.length > 1) {
                String subcommand = cmd[1].toLowerCase();

                if ("power".equals(subcommand)) {
                    showPlayerPowerAnalysis(player);
                    
                } else if ("stats".equals(subcommand)) {
                    player.sendMessage(getPerformanceStats());
                    
                } else if ("cache".equals(subcommand)) {
                    showCacheStats(player);
                    
                } else if ("clear".equals(subcommand)) {
                    clearPlayerCache(player.getIndex());
                    player.sendMessage("Your cache entries cleared.");
                    
                } else if ("save".equals(subcommand)) {
                    if (!player.isAdmin()) {
                        player.sendMessage("You need admin rights to save configurations.");
                        return;
                    }
                    handleSaveConfigsCommand(player, cmd);
                    
                } else if ("armor".equals(subcommand)) {
                    handleArmorCheckCommand(player, cmd);
                    
                } else {
                    player.sendMessage("Usage: ;;balance [power|stats|cache|clear|save|armor]");
                }
            } else {
                player.sendMessage("Boss Balancer v5.0 (Player-Friendly Complete) - " + getPerformanceStats());
                if (player.isAdmin()) {
                    player.sendMessage("Admin: ;;balance save - Save runtime configs to files");
                }
            }

        } catch (Exception e) {
            player.sendMessage("Error in diagnostic command: " + e.getMessage());
        }
    }

    /**
     * Show detailed player power analysis
     */
    private static void showPlayerPowerAnalysis(Player player) {
        try {
            double totalPower = calculateActualPlayerPower(player);
            int gearTier = calculatePlayerGearTier(player);
            double prayerTier = calculatePlayerPrayerTier(player);
            double absorptionPower = calculateAbsorptionEffectiveness(player);
            double synergyPower = calculateEquipmentSynergy(player);
            boolean hasFullArmor = checkFullArmorCoverage(player);

            player.sendMessage("=== PLAYER-FRIENDLY POWER ANALYSIS ===");
            player.sendMessage("Total Power Level: " + String.format("%.2f", totalPower));
            player.sendMessage("Gear Tier: " + gearTier + " | Prayer Tier: " + String.format("%.1f", prayerTier));
            player.sendMessage("Absorption Power: " + String.format("%.2f", absorptionPower) + "x");
            player.sendMessage("Synergy Power: " + String.format("%.2f", synergyPower) + "x");
            player.sendMessage("Full Armor Coverage: " + (hasFullArmor ? "YES" : "NO"));
            player.sendMessage("");
            
            // Show scaling examples
            player.sendMessage("=== PLAYER-FRIENDLY SCALING EXAMPLES ===");
            showPlayerFriendlyScalingExample(player, "Tier 3 Boss", 3, totalPower);
            showPlayerFriendlyScalingExample(player, "Tier 5 Boss", 5, totalPower);
            showPlayerFriendlyScalingExample(player, "Tier 8 Boss", 8, totalPower);

        } catch (Exception e) {
            player.sendMessage("Error analyzing power: " + e.getMessage());
        }
    }

    /**
     * Show player-friendly scaling example for a specific boss tier
     */
    private static void showPlayerFriendlyScalingExample(Player player, String bossName, int bossTier, double playerPower) {
        try {
            double bossPower = Math.pow(bossTier, 1.4);
            double powerRatio = playerPower / bossPower;
            
            String scalingType;
            String difficultyChange;
            
            if (powerRatio < UNDERPOWERED_THRESHOLD) {
                scalingType = "ASSISTANCE";
                double assistance = (UNDERPOWERED_THRESHOLD - powerRatio) * 1.5;
                assistance = Math.min(0.4, assistance);
                difficultyChange = "-" + (int)(assistance * 100) + "%";
            } else {
                scalingType = "BALANCED";
                difficultyChange = "No Change";
                if (powerRatio > OVERPOWERED_THRESHOLD) {
                    difficultyChange += " (Positive Message)";
                }
            }
            
            player.sendMessage(bossName + ": " + scalingType + " (" + difficultyChange + ")");
            
        } catch (Exception e) {
            player.sendMessage(bossName + ": Error calculating scaling");
        }
    }

    /**
     * Show cache statistics
     */
    private static void showCacheStats(Player player) {
        player.sendMessage("=== CACHE STATISTICS ===");
        player.sendMessage("Player Power Cache: " + playerPowerCache.size() + "/" + MAX_CACHE_ENTRIES);
        player.sendMessage("Message Cooldowns: " + playerMessageCooldowns.size() + "/" + MAX_MESSAGE_COOLDOWN_ENTRIES);
        player.sendMessage("Boss Tier Cache: " + bossTierCache.size() + "/" + MAX_CACHE_ENTRIES);
        player.sendMessage("Active Combat Sessions: " + activeCombatSessions.size() + "/" + MAX_COMBAT_SESSIONS);
        player.sendMessage("Runtime Boss Configs: " + runtimeConfigurations.size());
    }

    /**
     * Get performance statistics
     */
    public static String getPerformanceStats() {
        return "BossBalancer v5.0 Player-Friendly Stats - Total: " + totalCalculations + 
               ", Intelligent: " + intelligentCalculations + 
               ", Power Cache: " + playerPowerCache.size() + 
               ", Boss Cache: " + bossTierCache.size() + 
               ", Configs: " + runtimeConfigurations.size() + 
               ", Last Cleanup: " + (lastCleanupTime > 0 ? 
               (System.currentTimeMillis() - lastCleanupTime) + "ms ago" : "Never");
    }

    // ===== MEMORY MANAGEMENT =====

    /**
     * Enhanced automatic cleanup
     */
    private static void performAutomaticCleanup() {
        try {
            long currentTime = System.currentTimeMillis();
            lastCleanupTime = currentTime;

            // Clean message cooldowns
            cleanupMapBySize(playerMessageCooldowns, MAX_MESSAGE_COOLDOWN_ENTRIES, "message cooldowns");

            // Clean power cache
            cleanupPowerCache(currentTime);

            // Clean boss tier cache
            cleanupMapBySize(bossTierCache, MAX_CACHE_ENTRIES, "boss tier cache");

            // Clean combat sessions
            cleanupCombatSessions(currentTime);

        } catch (Exception e) {
            System.err.println("BossBalancer: Error in automatic cleanup: " + e.getMessage());
        }
    }

    /**
     * Clean power cache by age and size
     */
    private static void cleanupPowerCache(long currentTime) {
        if (playerPowerCache.size() <= MAX_CACHE_ENTRIES) {
            return;
        }

        int removedCount = 0;
        Iterator<Map.Entry<Integer, PlayerPowerCache>> iterator = playerPowerCache.entrySet().iterator();

        while (iterator.hasNext() && playerPowerCache.size() > MAX_CACHE_ENTRIES / 2) {
            Map.Entry<Integer, PlayerPowerCache> entry = iterator.next();
            PlayerPowerCache cache = entry.getValue();

            // Remove old cache entries (older than 5 minutes)
            if (cache != null && (currentTime - cache.timestamp) > 300000L) {
                iterator.remove();
                removedCount++;
            }
        }

        if (removedCount > 0) {
            System.out.println("BossBalancer: Cleaned up " + removedCount + " old power cache entries. Current: " + playerPowerCache.size());
        }
    }

    /**
     * Clean map by size limit
     */
    @SuppressWarnings("rawtypes")
    private static void cleanupMapBySize(Map map, int maxSize, String mapName) {
        if (map.size() <= maxSize) {
            return;
        }

        int removedCount = 0;
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext() && map.size() > maxSize / 2) {
            iterator.next();
            iterator.remove();
            removedCount++;
        }

        if (removedCount > 0) {
            System.out.println("BossBalancer: Cleaned up " + removedCount + " " + mapName + " entries. Current: " + map.size());
        }
    }

    /**
     * Clean combat sessions
     */
    private static void cleanupCombatSessions(long currentTime) {
        if (activeCombatSessions.size() <= MAX_COMBAT_SESSIONS) {
            return;
        }

        int removedCount = 0;
        Iterator<Map.Entry<Integer, CombatSession>> iterator = activeCombatSessions.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Integer, CombatSession> entry = iterator.next();
            CombatSession session = entry.getValue();

            if (session == null || (currentTime - session.sessionStart) > COMBAT_SESSION_TIMEOUT_MS) {
                iterator.remove();
                removedCount++;
            }
        }

        if (removedCount > 0) {
            System.out.println("BossBalancer: Cleaned up " + removedCount + " old combat sessions. Active: " + activeCombatSessions.size());
        }
    }

    /**
     * Clear player cache on logout
     */
    public static void clearPlayerCache(int playerId) {
        if (playerId <= 0) {
            return;
        }

        Integer playerIdObj = Integer.valueOf(playerId);
        playerMessageCooldowns.remove(playerIdObj);
        playerPowerCache.remove(playerIdObj);
        activeCombatSessions.remove(playerIdObj);
    }

    // ===== DEFAULT CREATION METHODS =====

    /**
     * Create default scaling object
     */
    private static CombatScaling createDefaultScaling(String errorType) {
        CombatScaling defaultScaling = new CombatScaling();
        defaultScaling.bossHpMultiplier = 1.0;
        defaultScaling.bossDamageMultiplier = 1.0;
        defaultScaling.bossAccuracyMultiplier = 1.0;
        defaultScaling.scalingType = errorType != null ? errorType : "UNKNOWN";
        defaultScaling.playerPower = 1.0;
        defaultScaling.bossPower = 1.0;
        defaultScaling.powerRatio = 1.0;
        defaultScaling.shouldWarnPlayer = false;
        defaultScaling.warningMessage = "";
        return defaultScaling;
    }

    // ===== ENHANCED COMBAT SCALING CLASS =====

    public static class CombatScaling {
        // Legacy fields
        public int playerTier = DEFAULT_TIER;
        public double effectivePlayerTier = DEFAULT_TIER;
        public double prayerTier = 0.0;
        public int bossTier = DEFAULT_TIER;
        public int tierDifference = 0;
        
        // Scaling multipliers
        public double bossHpMultiplier = 1.0;
        public double bossDamageMultiplier = 1.0;
        public double bossAccuracyMultiplier = 1.0;
        
        // Enhanced v5.0 fields
        public double playerPower = 1.0;           // Actual calculated player power
        public double bossPower = 1.0;             // Actual calculated boss power
        public double powerRatio = 1.0;            // playerPower / bossPower
        
        // Metadata
        public String scalingType = "BALANCED";
        public boolean shouldWarnPlayer = false;
        public String warningMessage = "";
        public String armorAnalysis = "";
    }

    // ===== CONFIGURATION SYSTEM =====
    
    /**
     * Runtime boss configuration
     */
    public static class RuntimeBossConfig {
        public final int bossId;
        public final int tier;
        public final int bossType;
        public final String configuredBy;
        public final long configuredTime;

        public RuntimeBossConfig(int bossId, int tier, int bossType, String configuredBy) {
            this.bossId = bossId;
            this.tier = Math.max(MIN_TIER, Math.min(MAX_TIER, tier));
            this.bossType = bossType;
            this.configuredBy = configuredBy != null ? configuredBy : "Unknown";
            this.configuredTime = System.currentTimeMillis();
        }
    }

    /**
     * Boss configuration class
     */
    public static class BossConfiguration {
        public int bossId;
        public int tier;
        public int bossType;
        public int maxHit;
        public String configuredBy;
        public long configuredTime;
        public boolean isRuntimeConfig;

        public BossConfiguration() {
            this.tier = DEFAULT_TIER;
            this.bossType = 0;
            this.maxHit = 150;
            this.configuredBy = "Unknown";
            this.configuredTime = System.currentTimeMillis();
            this.isRuntimeConfig = false;
        }
    }

    /**
     * Get boss configuration with null safety
     */
    public static BossConfiguration getBossConfiguration(int bossId) {
        try {
            RuntimeBossConfig runtimeConfig = runtimeConfigurations.get(Integer.valueOf(bossId));
            if (runtimeConfig != null) {
                BossConfiguration config = new BossConfiguration();
                config.bossId = runtimeConfig.bossId;
                config.tier = runtimeConfig.tier;
                config.bossType = runtimeConfig.bossType;
                config.configuredBy = runtimeConfig.configuredBy;
                config.configuredTime = runtimeConfig.configuredTime;
                config.maxHit = estimateMaxHitFromTier(runtimeConfig.tier);
                config.isRuntimeConfig = true;
                return config;
            }

            // Try file-based config
            File bossFile = new File("data/npcs/bosses/" + bossId + ".txt");
            if (bossFile.exists()) {
                return loadFileBasedConfig(bossId, bossFile);
            }

        } catch (Exception e) {
            System.err.println("BossBalancer: Error getting boss configuration for " + bossId + ": " + e.getMessage());
        }

        return null;
    }

    /**
     * Load file-based configuration
     */
    private static BossConfiguration loadFileBasedConfig(int bossId, File bossFile) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(bossFile));
            BossConfiguration config = new BossConfiguration();
            config.bossId = bossId;
            config.configuredTime = bossFile.lastModified();
            config.isRuntimeConfig = false;

            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#") || line.startsWith("//")) {
                    continue;
                }

                String[] parts = line.split("=");
                if (parts.length != 2) continue;

                String key = parts[0].trim().toLowerCase();
                String value = parts[1].trim();

                try {
                    switch (key) {
                    case "tier":
                        config.tier = Math.max(MIN_TIER, Math.min(MAX_TIER, Integer.parseInt(value)));
                        break;
                    case "type":
                    case "bosstype":
                        config.bossType = Integer.parseInt(value);
                        break;
                    case "maxhit":
                    case "max_hit":
                        config.maxHit = Integer.parseInt(value);
                        break;
                    case "configured_by":
                        config.configuredBy = value;
                        break;
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid entries
                }
            }

            return config;

        } catch (IOException e) {
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Ignore close errors
                }
            }
        }
    }

    /**
     * Estimate max hit from tier
     */
    private static int estimateMaxHitFromTier(int tier) {
        switch (tier) {
        case 1: return 50;
        case 2: return 100;
        case 3: return 150;
        case 4: return 200;
        case 5: return 250;
        case 6: return 300;
        case 7: return 400;
        case 8: return 500;
        case 9: return 650;
        case 10: return 800;
        default: return 150;
        }
    }

    /**
     * Get boss tier name
     */
    public static String getBossTierName(int tier) {
        switch (tier) {
        case 1: return "Tier 1 (Beginner)";
        case 2: return "Tier 2 (Novice)"; 
        case 3: return "Tier 3 (Intermediate)";
        case 4: return "Tier 4 (Advanced)";
        case 5: return "Tier 5 (Expert)";
        case 6: return "Tier 6 (Master)";
        case 7: return "Tier 7 (Elite)";
        case 8: return "Tier 8 (Legendary)";
        case 9: return "Tier 9 (Mythical)";
        case 10: return "Tier 10 (Divine)";
        default: return "Unknown Tier (" + tier + ")";
        }
    }

    // ===== COMBAT SESSION MANAGEMENT =====

    /**
     * Start combat session with null safety
     */
    public static void startCombatSession(Player player, NPC boss) {
        if (player == null || boss == null) {
            return;
        }

        try {
            if (activeCombatSessions.size() >= MAX_COMBAT_SESSIONS) {
                // Clean old sessions first
                cleanupCombatSessions(System.currentTimeMillis());
            }

            int playerId = player.getIndex();
            int bossId = boss.getId();
            double playerPower = calculateActualPlayerPower(player);
            double bossPower = calculateBossPower(boss);

            CombatSession session = new CombatSession(playerId, bossId, playerPower, bossPower);
            activeCombatSessions.put(Integer.valueOf(playerId), session);

        } catch (Exception e) {
            System.err.println("BossBalancer: Error starting combat session: " + e.getMessage());
        }
    }

    /**
     * End combat session with null safety
     */
    public static void endCombatSession(Player player) {
        if (player == null) {
            return;
        }

        try {
            Integer playerId = Integer.valueOf(player.getIndex());
            activeCombatSessions.remove(playerId);
        } catch (Exception e) {
            System.err.println("BossBalancer: Error ending combat session: " + e.getMessage());
        }
    }

    /**
     * Handle prayer change events
     */
    public static void onPrayerChanged(Player player) {
        if (player == null) {
            return;
        }

        try {
            Integer playerId = Integer.valueOf(player.getIndex());
            
            // Clear prayer cache to force recalculation
            playerPowerCache.remove(playerId);

            CombatSession session = activeCombatSessions.get(playerId);
            if (session != null && !session.powerLocked) {
                double newPlayerPower = calculateActualPlayerPower(player);
                session.lockedPlayerPower = newPlayerPower;

                // Optional: Send message about scaling change
                if (Math.abs(newPlayerPower - session.lockedPlayerPower) > 0.5) {
                    String message = newPlayerPower > session.lockedPlayerPower
                            ? "<col=ffaa00>Prayer activated! Combat effectiveness updated."
                            : "<col=99ff99>Prayer effects reduced. Combat effectiveness updated.";
                    sendWarningWithCooldown(player, message);
                }
            }
        } catch (Exception e) {
            System.err.println("BossBalancer: Error handling prayer change: " + e.getMessage());
        }
    }

    // ===== BOSS CONFIGURATION METHODS =====

    /**
     * Save a single boss configuration to file
     */
    public static boolean saveBossConfigurationToFile(int bossId, int tier, int bossType, String configuredBy) {
        try {
            // Ensure the directory exists
            File bossDir = new File("data/npcs/bosses/");
            if (!bossDir.exists()) {
                if (!bossDir.mkdirs()) {
                    System.err.println("BossBalancer: Failed to create boss directory");
                    return false;
                }
            }

            // Create the file
            File bossFile = new File(bossDir, bossId + ".txt");
            
            // Use try-with-resources for proper resource management (Java 7+)
            try (java.io.FileWriter writer = new java.io.FileWriter(bossFile);
                 java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(writer)) {
                
                // Write configuration with comments
                bufferedWriter.write("# Boss Configuration for NPC ID: " + bossId);
                bufferedWriter.newLine();
                bufferedWriter.write("# Generated by BossBalancer v5.0 Player-Friendly");
                bufferedWriter.newLine();
                bufferedWriter.write("# Configured by: " + (configuredBy != null ? configuredBy : "Unknown"));
                bufferedWriter.newLine();
                bufferedWriter.write("# Date: " + new java.util.Date());
                bufferedWriter.newLine();
                bufferedWriter.newLine();
                
                // Write actual configuration
                bufferedWriter.write("tier=" + Math.max(MIN_TIER, Math.min(MAX_TIER, tier)));
                bufferedWriter.newLine();
                bufferedWriter.write("type=" + bossType);
                bufferedWriter.newLine();
                bufferedWriter.write("maxhit=" + estimateMaxHitFromTier(Math.max(MIN_TIER, Math.min(MAX_TIER, tier))));
                bufferedWriter.newLine();
                bufferedWriter.write("configured_by=" + (configuredBy != null ? configuredBy : "Unknown"));
                bufferedWriter.newLine();
                
                bufferedWriter.flush();
                
                System.out.println("BossBalancer: Saved configuration for boss " + bossId + " to file");
                return true;
                
            } catch (java.io.IOException e) {
                System.err.println("BossBalancer: Error writing boss configuration file for " + bossId + ": " + e.getMessage());
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("BossBalancer: Unexpected error saving boss configuration: " + e.getMessage());
            return false;
        }
    }

    /**
     * Enhanced auto-configure boss with file persistence option
     */
    public static boolean autoConfigureBoss(int bossId, int tier, int bossType, String configuredBy, boolean saveToFile) {
        try {
            if (bossId <= 0) {
                return false;
            }

            tier = Math.max(MIN_TIER, Math.min(MAX_TIER, tier));

            // Create runtime configuration
            RuntimeBossConfig config = new RuntimeBossConfig(bossId, tier, bossType, configuredBy);
            runtimeConfigurations.put(Integer.valueOf(bossId), config);
            configuredBosses.add(Integer.valueOf(bossId));

            // Clear cache to force recalculation
            bossTierCache.remove(Integer.valueOf(bossId));

            // Save to file if requested
            if (saveToFile) {
                boolean fileSaved = saveBossConfigurationToFile(bossId, tier, bossType, configuredBy);
                if (!fileSaved) {
                    System.err.println("BossBalancer: Warning - Boss " + bossId + " configured in memory but failed to save to file");
                }
            }

            return true;

        } catch (Exception e) {
            System.err.println("BossBalancer: Error auto-configuring boss " + bossId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Overloaded method for backward compatibility (defaults to saving to file)
     */
    public static boolean autoConfigureBoss(int bossId, int tier, int bossType, String configuredBy) {
        return autoConfigureBoss(bossId, tier, bossType, configuredBy, true);
    }

    /**
     * Save all runtime configurations to files
     */
    public static int saveAllRuntimeConfigurationsToFiles() {
        int savedCount = 0;
        int errorCount = 0;

        try {
            System.out.println("BossBalancer: Saving " + runtimeConfigurations.size() + " runtime configurations to files...");

            for (Map.Entry<Integer, RuntimeBossConfig> entry : runtimeConfigurations.entrySet()) {
                try {
                    RuntimeBossConfig config = entry.getValue();
                    boolean success = saveBossConfigurationToFile(
                        config.bossId, 
                        config.tier, 
                        config.bossType, 
                        config.configuredBy
                    );

                    if (success) {
                        savedCount++;
                    } else {
                        errorCount++;
                    }

                } catch (Exception e) {
                    errorCount++;
                    System.err.println("BossBalancer: Error saving runtime config for boss " + entry.getKey() + ": " + e.getMessage());
                }
            }

            System.out.println("BossBalancer: Successfully saved " + savedCount + " runtime configurations" + 
                              (errorCount > 0 ? " (" + errorCount + " errors)" : ""));

        } catch (Exception e) {
            System.err.println("BossBalancer: Error in saveAllRuntimeConfigurationsToFiles: " + e.getMessage());
        }

        return savedCount;
    }

    /**
     * New command to save runtime configurations manually
     */
    public static void handleSaveConfigsCommand(Player player, String[] cmd) {
        if (player == null) {
            return;
        }

        if (!player.isAdmin()) {
            player.sendMessage("You need admin rights to save configurations.");
            return;
        }

        try {
            if (runtimeConfigurations.isEmpty()) {
                player.sendMessage("No runtime configurations to save.");
                return;
            }

            player.sendMessage("Saving " + runtimeConfigurations.size() + " runtime configurations...");
            int savedCount = saveAllRuntimeConfigurationsToFiles();
            
            if (savedCount > 0) {
                player.sendMessage("<col=00ff00>Successfully saved " + savedCount + " configurations to files.</col>");
                player.sendMessage("These configurations will now persist through server restarts.");
            } else {
                player.sendMessage("<col=ff6600>Failed to save any configurations.</col>");
            }

        } catch (Exception e) {
            player.sendMessage("Error saving configurations: " + e.getMessage());
        }
    }

    /**
     * Enhanced adjust boss command handler with persistence option
     */
    public static void handleAdjustBossCommand(Player player, String[] cmd) {
        if (player == null || cmd.length < 4) {
            player.sendMessage("Usage: ;;adjustboss <npcId> <tier> <class> [temp]");
            player.sendMessage("Add 'temp' at the end for temporary configuration (memory only)");
            return;
        }

        try {
            int npcId = Integer.parseInt(cmd[1]);
            int tier = Integer.parseInt(cmd[2]);
            int bossClass = Integer.parseInt(cmd[3]);
            
            // Check if this should be temporary (memory only)
            boolean saveToFile = true;
            if (cmd.length > 4 && "temp".equalsIgnoreCase(cmd[4])) {
                saveToFile = false;
            }

            if (tier < 1 || tier > 10) {
                player.sendMessage("Tier must be between 1 and 10!");
                return;
            }

            if (bossClass < 0 || bossClass > 6) {
                player.sendMessage("Class must be between 0 and 6!");
                return;
            }

            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npcId);
            if (def == null) {
                player.sendMessage("NPC ID " + npcId + " not found!");
                return;
            }

            boolean success = autoConfigureBoss(npcId, tier, bossClass, player.getDisplayName(), saveToFile);

            if (success) {
                String className = getBossClassName(bossClass);
                String npcName = (def.getName() != null) ? def.getName() : "Unknown";
                String persistenceType = saveToFile ? "PERMANENT (saved to file)" : "TEMPORARY (memory only)";

                player.sendMessage("Successfully adjusted boss:");
                player.sendMessage("NPC: " + npcName + " (ID: " + npcId + ")");
                player.sendMessage("Tier: " + tier + " (" + getBossTierName(tier) + ")");
                player.sendMessage("Class: " + className);
                player.sendMessage("Type: " + persistenceType);
                player.sendMessage("<col=00ff00>Player-Friendly Mode: No punishment scaling!</col>");

                clearPlayerCache(player.getIndex());
                bossTierCache.remove(Integer.valueOf(npcId));

                int verifyTier = getBossEffectiveTierById(npcId);
                player.sendMessage("Verification: Boss " + npcId + " now reads as Tier " + verifyTier);
                
                if (saveToFile) {
                    player.sendMessage("<col=00ff00>Configuration will persist through server restarts.</col>");
                } else {
                    player.sendMessage("<col=ffaa00>Configuration is temporary and will be lost on restart.</col>");
                }
            } else {
                player.sendMessage("Failed to adjust boss " + npcId + "!");
            }

        } catch (NumberFormatException e) {
            player.sendMessage("Invalid number format in command!");
        } catch (Exception e) {
            player.sendMessage("Error adjusting boss: " + e.getMessage());
        }
    }

    /**
     * Get boss class name for display
     */
    public static String getBossClassName(int bossClass) {
        switch (bossClass) {
        case 0: return "Melee";
        case 1: return "Ranged";
        case 2: return "Magic";
        case 3: return "Mixed Combat";
        case 4: return "Hybrid";
        case 5: return "Special";
        case 6: return "Raid";
        default: return "Unknown (" + bossClass + ")";
        }
    }

    /**
     * Check if boss is configured
     */
    public static boolean isBossConfigured(int bossId) {
        try {
            if (configuredBosses.contains(Integer.valueOf(bossId))) {
                return true;
            }

            if (runtimeConfigurations.containsKey(Integer.valueOf(bossId))) {
                return true;
            }

            File bossFile = new File("data/npcs/bosses/" + bossId + ".txt");
            if (bossFile.exists()) {
                configuredBosses.add(Integer.valueOf(bossId));
                return true;
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Check if player is dual wielding
     */
    public static boolean isDualWielding(Player player) {
        if (player == null || player.getEquipment() == null) {
            return false;
        }

        try {
            ItemsContainer<Item> equipment = player.getEquipment().getItems();
            if (equipment == null) {
                return false;
            }

            Item mainhand = equipment.get(3);
            Item offhand = equipment.get(5);

            if (mainhand == null || offhand == null || mainhand.getId() <= 0 || offhand.getId() <= 0) {
                return false;
            }

            return isOffhandWeapon(offhand.getId());

        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Load all boss configurations
     */
    public static void loadAllBossConfigurations() {
        System.out.println("BossBalancer v5.0 Player-Friendly: Loading saved boss configurations...");

        try {
            File bossDir = new File("data/npcs/bosses/");
            if (!bossDir.exists()) {
                if (bossDir.mkdirs()) {
                    System.out.println("BossBalancer: Boss directory created successfully");
                }
                return;
            }

            File[] bossFiles = bossDir.listFiles();
            if (bossFiles == null || bossFiles.length == 0) {
                System.out.println("BossBalancer: No boss files found");
                return;
            }

            int loadedCount = 0;
            int errorCount = 0;

            for (File bossFile : bossFiles) {
                if (bossFile != null && bossFile.getName().endsWith(".txt")) {
                    try {
                        String fileName = bossFile.getName();
                        int npcId = Integer.parseInt(fileName.replace(".txt", ""));

                        BossConfiguration config = loadFileBasedConfig(npcId, bossFile);
                        if (config != null) {
                            configuredBosses.add(Integer.valueOf(npcId));
                            if (bossTierCache.size() < MAX_CACHE_ENTRIES) {
                                bossTierCache.put(Integer.valueOf(npcId), Integer.valueOf(config.tier));
                            }
                            loadedCount++;
                        } else {
                            errorCount++;
                        }

                    } catch (NumberFormatException e) {
                        errorCount++;
                        System.err.println("BossBalancer: Invalid boss file name: " + bossFile.getName());
                    } catch (Exception e) {
                        errorCount++;
                        System.err.println(
                                "BossBalancer: Error loading boss file " + bossFile.getName() + ": " + e.getMessage());
                    }
                }
            }

            System.out.println("BossBalancer v5.0 Player-Friendly: Successfully loaded " + loadedCount + " boss configurations"
                    + (errorCount > 0 ? " (" + errorCount + " errors)" : ""));

        } catch (Exception e) {
            System.err.println("BossBalancer: Error during startup loading: " + e.getMessage());
        }
    }

    /**
     * Enhanced shutdown method with configuration saving
     */
    public static void shutdown() {
        synchronized (initLock) {
            try {
                // Save all runtime configurations before shutdown
                if (!runtimeConfigurations.isEmpty()) {
                    System.out.println("BossBalancer: Saving runtime configurations before shutdown...");
                    saveAllRuntimeConfigurationsToFiles();
                }

                // Clear runtime data
                runtimeConfigurations.clear();
                configuredBosses.clear();
                playerMessageCooldowns.clear();
                playerPowerCache.clear();
                bossTierCache.clear();
                activeCombatSessions.clear();

                // Shutdown cleanup service
                if (cleanupService != null && !cleanupService.isShutdown()) {
                    try {
                        cleanupService.shutdown();
                        if (!cleanupService.awaitTermination(5, TimeUnit.SECONDS)) {
                            cleanupService.shutdownNow();
                            if (!cleanupService.awaitTermination(5, TimeUnit.SECONDS)) {
                                System.err.println("BossBalancer: Cleanup service did not terminate properly");
                            }
                        }
                    } catch (InterruptedException e) {
                        cleanupService.shutdownNow();
                        Thread.currentThread().interrupt();
                    } finally {
                        isInitialized = false;
                        System.out.println("BossBalancer: Player-friendly shutdown completed with configuration persistence");
                    }
                }

            } catch (Exception e) {
                System.err.println("BossBalancer: Error during shutdown: " + e.getMessage());
            }
        }
    }

    // ===== ARMOR ANALYSIS SYSTEM =====

    /**
     * Comprehensive armor coverage analysis
     */
    public static ArmorCoverageResult analyzeArmorCoverage(Player player) {
        if (player == null || player.getEquipment() == null) {
            return new ArmorCoverageResult(false, 0.0, "No equipment data");
        }

        try {
            ItemsContainer<Item> equipment = player.getEquipment().getItems();
            if (equipment == null) {
                return new ArmorCoverageResult(false, 0.0, "No equipment container");
            }

            // Check essential armor pieces
            ArmorPiece helmet = analyzeArmorPiece(equipment.get(0), "Helmet", 0);
            ArmorPiece body = analyzeArmorPiece(equipment.get(4), "Body", 4);
            ArmorPiece legs = analyzeArmorPiece(equipment.get(6), "Legs", 6);
            ArmorPiece gloves = analyzeArmorPiece(equipment.get(7), "Gloves", 7);
            ArmorPiece boots = analyzeArmorPiece(equipment.get(8), "Boots", 8);

            // Additional protective items
            ArmorPiece shield = analyzeArmorPiece(equipment.get(5), "Shield/Offhand", 5);
            ArmorPiece cape = analyzeArmorPiece(equipment.get(1), "Cape", 1);

            // Calculate coverage statistics
            boolean hasFullEssentialArmor = helmet.isEquipped && body.isEquipped && 
                                           legs.isEquipped && gloves.isEquipped && boots.isEquipped;
            
            int essentialPiecesCount = 0;
            double totalDefensiveValue = 0.0;
            double totalTierValue = 0.0;
            
            ArmorPiece[] essentialPieces = {helmet, body, legs, gloves, boots};
            for (ArmorPiece piece : essentialPieces) {
                if (piece.isEquipped) {
                    essentialPiecesCount++;
                    totalDefensiveValue += piece.defensiveValue;
                    totalTierValue += piece.tier;
                }
            }

            // Bonus for shield/offhand and cape
            if (shield.isEquipped) {
                totalDefensiveValue += shield.defensiveValue * 0.8; // Shield worth 80% of armor piece
            }
            if (cape.isEquipped) {
                totalDefensiveValue += cape.defensiveValue * 0.3; // Cape worth 30% of armor piece
            }

            // Calculate damage reduction percentage (POSITIVE ONLY - Player-Friendly)
            double damageReduction = calculateArmorDamageReduction(
                hasFullEssentialArmor, essentialPiecesCount, totalDefensiveValue, shield.isEquipped
            );

            // Create detailed analysis
            String analysis = createPlayerFriendlyArmorAnalysis(hasFullEssentialArmor, essentialPiecesCount, 
                                                helmet, body, legs, gloves, boots, shield, damageReduction);

            return new ArmorCoverageResult(hasFullEssentialArmor, damageReduction, analysis);

        } catch (Exception e) {
            System.err.println("BossBalancer: Error analyzing armor coverage: " + e.getMessage());
            return new ArmorCoverageResult(false, 0.0, "Error analyzing armor");
        }
    }

    /**
     * Analyze individual armor piece
     */
    private static ArmorPiece analyzeArmorPiece(Item item, String slotName, int slot) {
        if (item == null || item.getId() <= 0) {
            return new ArmorPiece(false, slotName, 0, 0.0, 0, "None");
        }

        try {
            ItemDefinitions def = ItemDefinitions.getItemDefinitions(item.getId());
            String itemName = (def != null && def.getName() != null) ? def.getName() : "Unknown";
            
            // Get defensive bonuses
            int[] bonuses = ItemBonuses.getItemBonuses(item.getId());
            double defensiveValue = 0.0;
            
            if (bonuses != null && bonuses.length > 0) {
                // Calculate defensive value from bonuses (stab, slash, crush, magic, ranged defense)
                // Assuming defensive bonuses are in positions 0-4 of the bonuses array
                for (int i = 0; i < Math.min(5, bonuses.length); i++) {
                    if (bonuses[i] > 0) {
                        defensiveValue += bonuses[i];
                    }
                }
            }

            int tier = getItemEffectiveTier(item.getId());
            
            return new ArmorPiece(true, slotName, item.getId(), defensiveValue, tier, itemName);
            
        } catch (Exception e) {
            return new ArmorPiece(true, slotName, item.getId(), 0.0, 1, "Unknown Item");
        }
    }

    /**
     * Calculate damage reduction based on armor coverage (POSITIVE ONLY - Player-Friendly)
     */
    private static double calculateArmorDamageReduction(boolean hasFullArmor, int armorPieces, 
                                                       double totalDefensiveValue, boolean hasShield) {
        double baseReduction = 0.0;
        
        // Base reduction from armor pieces (5% per essential piece) - POSITIVE BONUS ONLY
        baseReduction += armorPieces * 0.05;
        
        // Full armor set bonus (additional 10% if all 5 essential pieces) - POSITIVE BONUS ONLY
        if (hasFullArmor) {
            baseReduction += 0.10;
        }
        
        // Shield bonus (additional 8% damage reduction) - POSITIVE BONUS ONLY
        if (hasShield) {
            baseReduction += 0.08;
        }
        
        // Defensive value bonus (every 100 defensive points = 1% reduction, max 15%) - POSITIVE BONUS ONLY
        double defensiveBonus = Math.min(0.15, totalDefensiveValue / 100.0 * 0.01);
        baseReduction += defensiveBonus;
        
        // Cap total damage reduction at 35% from armor alone - POSITIVE BONUS ONLY
        return Math.min(0.35, Math.max(0.0, baseReduction));
    }

    /**
     * Create detailed player-friendly armor analysis string
     */
    private static String createPlayerFriendlyArmorAnalysis(boolean hasFullArmor, int armorCount, 
                                            ArmorPiece helmet, ArmorPiece body, ArmorPiece legs, 
                                            ArmorPiece gloves, ArmorPiece boots, ArmorPiece shield, 
                                            double damageReduction) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("=== PLAYER-FRIENDLY ARMOR ANALYSIS ===\n");
        analysis.append("Essential Armor: ").append(armorCount).append("/5 pieces equipped\n");
        analysis.append("Full Protection: ").append(hasFullArmor ? "YES" : "NO").append("\n");
        analysis.append("Damage Reduction Bonus: ").append(String.format("%.1f", damageReduction * 100)).append("%\n\n");
        
        analysis.append("Equipment Status:\n");
        analysis.append("• Helmet: ").append(helmet.isEquipped ? helmet.name + " (T" + helmet.tier + ")" : "MISSING").append("\n");
        analysis.append("• Body: ").append(body.isEquipped ? body.name + " (T" + body.tier + ")" : "MISSING").append("\n");
        analysis.append("• Legs: ").append(legs.isEquipped ? legs.name + " (T" + legs.tier + ")" : "MISSING").append("\n");
        analysis.append("• Gloves: ").append(gloves.isEquipped ? gloves.name + " (T" + gloves.tier + ")" : "MISSING").append("\n");
        analysis.append("• Boots: ").append(boots.isEquipped ? boots.name + " (T" + boots.tier + ")" : "MISSING").append("\n");
        analysis.append("• Shield: ").append(shield.isEquipped ? shield.name + " (T" + shield.tier + ")" : "None").append("\n");
        
        // Player-Friendly Recommendations
        analysis.append("\nPlayer-Friendly System:\n");
        if (!hasFullArmor) {
            analysis.append("ℹ INFO: Missing armor pieces reduce your defensive bonuses.\n");
            analysis.append("✓ GOOD NEWS: Boss difficulty is never increased for missing armor!\n");
            if (!helmet.isEquipped) analysis.append("• Consider equipping a helmet for better defense\n");
            if (!body.isEquipped) analysis.append("• Consider equipping body armor for better defense\n");
            if (!legs.isEquipped) analysis.append("• Consider equipping leg armor for better defense\n");
            if (!gloves.isEquipped) analysis.append("• Consider equipping gloves for better defense\n");
            if (!boots.isEquipped) analysis.append("• Consider equipping boots for better defense\n");
        } else {
            analysis.append("✓ Excellent! Full armor coverage provides maximum defensive bonuses.\n");
            if (!shield.isEquipped) {
                analysis.append("• Consider equipping a shield for additional defensive bonuses\n");
            }
        }
        
        analysis.append("\n<col=00ff00>Player-Friendly Mode: No punishment for missing gear!</col>");
        
        return analysis.toString();
    }

    /**
     * Handle armor check command
     */
    public static void handleArmorCheckCommand(Player player, String[] cmd) {
        if (player == null) {
            return;
        }
        
        try {
            ArmorCoverageResult result = analyzeArmorCoverage(player);
            
            // Send analysis to player
            String[] lines = result.analysis.split("\n");
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    player.sendMessage(line);
                }
            }
            
        } catch (Exception e) {
            player.sendMessage("Error analyzing armor: " + e.getMessage());
        }
    }

    /**
     * Data classes for armor analysis
     */
    public static class ArmorCoverageResult {
        public final boolean hasFullArmor;
        public final double damageReduction;
        public final String analysis;
        
        public ArmorCoverageResult(boolean hasFullArmor, double damageReduction, String analysis) {
            this.hasFullArmor = hasFullArmor;
            this.damageReduction = damageReduction;
            this.analysis = analysis;
        }
    }

    public static class ArmorPiece {
        public final boolean isEquipped;
        public final String slotName;
        public final int itemId;
        public final double defensiveValue;
        public final int tier;
        public final String name;
        
        public ArmorPiece(boolean isEquipped, String slotName, int itemId, 
                         double defensiveValue, int tier, String name) {
            this.isEquipped = isEquipped;
            this.slotName = slotName;
            this.itemId = itemId;
            this.defensiveValue = defensiveValue;
            this.tier = tier;
            this.name = name;
        }
    }

    // ===== COMPREHENSIVE COMMAND SYSTEM =====

    /**
     * Handle boss config command
     */
    public static void handleBossConfigCommand(Player player, String[] cmd) {
        if (player == null || cmd.length < 2) {
            return;
        }

        try {
            String subcommand = cmd[1].toLowerCase();

            if ("list".equals(subcommand)) {
                player.sendMessage("=== Boss Configuration Summary ===");
                player.sendMessage("Total configured bosses: " + configuredBosses.size());
                player.sendMessage("Runtime configurations: " + runtimeConfigurations.size());
                player.sendMessage("<col=00ff00>Player-Friendly Mode: Active</col>");

            } else if ("info".equals(subcommand)) {
                if (cmd.length < 3) {
                    player.sendMessage("Usage: ;;bossconfig info <npcId>");
                    return;
                }

                int npcId = Integer.parseInt(cmd[2]);
                boolean isConfigured = isBossConfigured(npcId);

                if (isConfigured) {
                    int tier = getBossEffectiveTierById(npcId);
                    player.sendMessage("=== Boss Configuration " + npcId + " ===");
                    player.sendMessage("Tier: " + tier + " (" + getBossTierName(tier) + ")");
                    player.sendMessage("Status: Configured");
                    player.sendMessage("<col=00ff00>Player-Friendly: No punishment scaling</col>");
                } else {
                    player.sendMessage("Boss " + npcId + " is not configured.");
                }

            } else {
                player.sendMessage("Usage: ;;bossconfig <list|info>");
            }

        } catch (NumberFormatException e) {
            player.sendMessage("Invalid number format in command");
        } catch (Exception e) {
            player.sendMessage("Error in boss config command: " + e.getMessage());
        }
    }

    /**
     * Handle boss tier check command
     */
    public static void handleBossTierCheckCommand(Player player, String[] cmd) {
        if (player == null || cmd.length < 2) {
            return;
        }

        try {
            int npcId = Integer.parseInt(cmd[1]);

            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npcId);
            if (def == null) {
                player.sendMessage("NPC ID " + npcId + " not found!");
                return;
            }

            boolean isConfigured = isBossConfigured(npcId);
            int effectiveTier = getBossEffectiveTierById(npcId);

            player.sendMessage("=== Boss Tier Check for NPC " + npcId + " ===");
            player.sendMessage("Name: " + (def.getName() != null ? def.getName() : "Unknown"));
            player.sendMessage("Combat Level: " + def.combatLevel);
            player.sendMessage("Size: " + def.size);

            if (isConfigured) {
                player.sendMessage("Configured: YES");
                player.sendMessage("Tier: " + effectiveTier + " (" + getBossTierName(effectiveTier) + ")");
            } else {
                player.sendMessage("Configured: NO (Using automatic calculation)");
                player.sendMessage("Auto Tier: " + effectiveTier + " (" + getBossTierName(effectiveTier) + ")");
            }
            
            player.sendMessage("<col=00ff00>Player-Friendly: No punishment for strong players</col>");

        } catch (NumberFormatException e) {
            player.sendMessage("Invalid NPC ID. Use: ;;bosstiercheck <npcId>");
        } catch (Exception e) {
            player.sendMessage("Error checking boss tier: " + e.getMessage());
        }
    }

    /**
     * Handle combat scaling command
     */
    public static void handleCombatScalingCommand(Player player, String[] cmd) {
        if (player == null || cmd.length < 2) {
            return;
        }

        try {
            int npcId = Integer.parseInt(cmd[1]);

            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npcId);
            if (def == null) {
                player.sendMessage("NPC ID " + npcId + " not found!");
                return;
            }

            double playerPower = calculateActualPlayerPower(player);
            int playerGearTier = calculatePlayerGearTier(player);
            double playerPrayerTier = calculatePlayerPrayerTier(player);
            int bossTier = getBossEffectiveTierById(npcId);
            double bossPower = Math.pow(bossTier, 1.4); // Same calculation as in calculateBossPower
            double powerRatio = playerPower / bossPower;

            player.sendMessage("=== Player-Friendly Combat Scaling Preview ===");
            player.sendMessage("NPC: " + (def.getName() != null ? def.getName() : "Unknown") + " (ID: " + npcId + ")");
            player.sendMessage("Your Gear Tier: " + playerGearTier + " (" + getBossTierName(playerGearTier) + ")");
            player.sendMessage("Your Prayer Tier: " + String.format("%.1f", playerPrayerTier));
            player.sendMessage("Your Total Power: " + String.format("%.2f", playerPower));
            player.sendMessage("Boss Tier: " + bossTier + " (" + getBossTierName(bossTier) + ")");
            player.sendMessage("Boss Power: " + String.format("%.2f", bossPower));
            player.sendMessage("Power Ratio: " + String.format("%.2f", powerRatio) + ":1");

            // Show what scaling would be applied
            String scalingPreview = getPlayerFriendlyScalingPreview(powerRatio);
            player.sendMessage("Expected Scaling: " + scalingPreview);

        } catch (NumberFormatException e) {
            player.sendMessage("Invalid NPC ID. Use: ;;combatscaling <npcId>");
        } catch (Exception e) {
            player.sendMessage("Error calculating combat scaling: " + e.getMessage());
        }
    }

    /**
     * Get player-friendly scaling preview text
     */
    private static String getPlayerFriendlyScalingPreview(double powerRatio) {
        if (powerRatio < UNDERPOWERED_THRESHOLD) {
            double assistance = (UNDERPOWERED_THRESHOLD - powerRatio) * 1.5;
            assistance = Math.min(0.4, assistance);
            return "<col=00ff00>ASSISTANCE (-" + (int)(assistance * 100) + "% difficulty)</col>";
        } else {
            String message = "<col=ffffff>BALANCED (no scaling)</col>";
            if (powerRatio > OVERPOWERED_THRESHOLD) {
                message += " <col=00ff00>+ Positive Message</col>";
            }
            return message;
        }
    }

    /**
     * Handle gear tier command
     */
    public static void handleGearTierCommand(Player player, String[] cmd) {
        if (player == null) {
            return;
        }

        try {
            double totalPower = calculateActualPlayerPower(player);
            int playerGearTier = calculatePlayerGearTier(player);
            double playerPrayerTier = calculatePlayerPrayerTier(player);
            double absorptionPower = calculateAbsorptionEffectiveness(player);
            double synergyPower = calculateEquipmentSynergy(player);
            boolean hasFullArmor = checkFullArmorCoverage(player);
            boolean dualWield = isDualWielding(player);

            player.sendMessage("=== Player-Friendly Combat Analysis ===");
            player.sendMessage("Total Power Level: " + String.format("%.2f", totalPower));
            player.sendMessage("Gear Tier: " + playerGearTier + " (" + getBossTierName(playerGearTier) + ")");
            player.sendMessage("Prayer Tier: " + String.format("%.1f", playerPrayerTier));
            player.sendMessage("Absorption Power: " + String.format("%.2f", absorptionPower) + "x");
            player.sendMessage("Synergy Power: " + String.format("%.2f", synergyPower) + "x");
            player.sendMessage("Full Armor: " + (hasFullArmor ? "YES" : "NO"));
            player.sendMessage("Dual-wielding: " + (dualWield ? "YES" : "NO"));
            player.sendMessage("<col=00ff00>System: Player-Friendly (No punishment scaling)</col>");

            // Show active prayer bonuses
            if (player.getPrayer() != null && player.getPrayer().hasPrayersOn()) {
                double attackMult = player.getPrayer().getAttackMultiplier();
                double strengthMult = player.getPrayer().getStrengthMultiplier();
                double defenceMult = player.getPrayer().getDefenceMultiplier();

                player.sendMessage("=== Active Prayer Bonuses ===");
                if (attackMult > 1.0)
                    player.sendMessage("Attack: +" + String.format("%.0f", (attackMult - 1.0) * 100) + "%");
                if (strengthMult > 1.0)
                    player.sendMessage("Strength: +" + String.format("%.0f", (strengthMult - 1.0) * 100) + "%");
                if (defenceMult > 1.0)
                    player.sendMessage("Defence: +" + String.format("%.0f", (defenceMult - 1.0) * 100) + "%");
            }

        } catch (Exception e) {
            player.sendMessage("Error analyzing gear: " + e.getMessage());
        }
    }

    /**
     * Debug combat scaling method
     */
    public static void debugCombatScaling(Player player, NPC boss) {
        if (player == null || boss == null) {
            return;
        }

        try {
            CombatScaling scaling = getIntelligentCombatScaling(player, boss);

            System.out.println("=== PLAYER-FRIENDLY COMBAT SCALING DEBUG v5.0 ===");
            System.out.println("Player: " + player.getDisplayName());
            System.out.println("Player Power: " + String.format("%.2f", scaling.playerPower));
            System.out.println("Boss Power: " + String.format("%.2f", scaling.bossPower));
            System.out.println("Power Ratio: " + String.format("%.2f", scaling.powerRatio));
            System.out.println("HP Multiplier: " + String.format("%.3f", scaling.bossHpMultiplier));
            System.out.println("Damage Multiplier: " + String.format("%.3f", scaling.bossDamageMultiplier));
            System.out.println("Accuracy Multiplier: " + String.format("%.3f", scaling.bossAccuracyMultiplier));
            System.out.println("Scaling Type: " + scaling.scalingType);
            System.out.println("Player-Friendly Mode: ACTIVE");

            // Prayer details
            if (player.getPrayer() != null && player.getPrayer().hasPrayersOn()) {
                System.out.println("=== ACTIVE PRAYER BONUSES ===");
                System.out.println("Attack: " + String.format("%.3f", player.getPrayer().getAttackMultiplier()));
                System.out.println("Strength: " + String.format("%.3f", player.getPrayer().getStrengthMultiplier()));
                System.out.println("Defence: " + String.format("%.3f", player.getPrayer().getDefenceMultiplier()));
                System.out.println("Range: " + String.format("%.3f", player.getPrayer().getRangeMultiplier()));
                System.out.println("Magic: " + String.format("%.3f", player.getPrayer().getMageMultiplier()));
            }

            System.out.println("=====================================");
        } catch (Exception e) {
            System.err.println("BossBalancer: Error in debug combat scaling: " + e.getMessage());
        }
    }

    /**
     * Force cleanup method
     */
    public static void forceCleanup() {
        try {
            performAutomaticCleanup();
        } catch (Exception e) {
            System.err.println("BossBalancer: Error in force cleanup: " + e.getMessage());
        }
    }

    /**
     * Updated show boss command help with new player-friendly v5.0 features
     */
    public static void showBossCommandHelp(Player player) {
        if (player == null) {
            return;
        }

        player.sendMessage("=== Boss Balancer v5.0 Player-Friendly Commands ===");
        player.sendMessage(";;geartier - Complete combat analysis");
        player.sendMessage(";;balance power - Detailed power analysis"); 
        player.sendMessage(";;balance armor - Armor coverage check");
        player.sendMessage(";;bosstiercheck <id> - Check specific boss");
        player.sendMessage(";;combatscaling <id> - Preview scaling");
        player.sendMessage(";;balance stats - Performance statistics");
        player.sendMessage(";;balance cache - Cache statistics");
        player.sendMessage(";;balance clear - Clear your cache");

        if (player.isStaff2()) {
            player.sendMessage("");
            player.sendMessage("=== Staff Commands ===");
            player.sendMessage(";;bossconfig list - List configurations");
            player.sendMessage(";;bossconfig info <id> - Detailed config");
        }

        if (player.isAdmin()) {
            player.sendMessage("");
            player.sendMessage("=== Admin Commands ===");
            player.sendMessage(";;adjustboss <id> <tier> <class> - Configure boss (PERMANENT)");
            player.sendMessage(";;adjustboss <id> <tier> <class> temp - Configure boss (TEMPORARY)");
            player.sendMessage(";;balance save - Save all runtime configs to files");
            player.sendMessage("");
            player.sendMessage("<col=00ff00>PERMANENT configs persist through restarts</col>");
            player.sendMessage("<col=ffaa00>TEMPORARY configs are lost on restart</col>");
            player.sendMessage("");
            player.sendMessage("=== v5.0 Player-Friendly Features ===");
            player.sendMessage("• <col=00ff00>REMOVED: All punishment scaling for strong players</col>");
            player.sendMessage("• <col=00ff00>KEPT: Assistance for underpowered players</col>");
            player.sendMessage("• <col=00ff00>ADDED: Positive messages for overpowered players</col>");
            player.sendMessage("• <col=00ff00>DISABLED: Armor penalties (only bonuses now)</col>");
            player.sendMessage("• Equipment synergy detection & absorption calculation");
            player.sendMessage("• Comprehensive armor analysis (no penalties)");
        }
    }
}