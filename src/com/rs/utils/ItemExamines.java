/**
 * Enhanced Item Examination System with Tier Integration and Offhand Weapon Support v3.1
 * Author: Zeus
 * Date: June 09, 2025
 * Java Version: 1.7
 * 
 * MAJOR FEATURES v3.1:
 * - FIXED: Tier ranges now match ItemBalancer v3.0 exactly
 * - FIXED: Immediate cache refresh after adjuststats
 * - FIXED: Proper tier determination logic
 * - ENHANCED: Complete offhand weapon support
 * - NEW: Proper tier calculation for offhand weapons (based on mainhand equivalent)
 * - NEW: Correct class names for offhand weapons
 * - NEW: Show offhand-specific information
 * - NEW: Handle damage calculation for offhand weapons
 * - NEW: Display dual-wield information
 * - IMPROVED: Better fallback to direct stat reading
 * - IMPROVED: Force refresh capabilities
 * 
 * Features:
 * - Works with existing ItemBalancer class (no changes needed)
 * - Displays item tier information by reading existing balance log
 * - Shows tier class names and damage ranges
 * - Full offhand weapon detection and tier calculation
 * - Memory optimized for Java 1.7
 * - Thread-safe operations
 * - Immediate tier updates after adjustments
 */
package com.rs.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.rs.game.item.Item;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.cache.loaders.ItemDefinitions;

public class ItemExamines {

    private static final Map<Integer, String> itemExamines = new ConcurrentHashMap<Integer, String>();
    private static final String PACKED_PATH = "data/items/packedExamines.e";
    private static final String UNPACKED_PATH = "data/items/unpackedExamines.txt";
    
    // Cache for tier information (reads from existing ItemBalancer log)
    private static final Map<Integer, TierInfo> tierCache = new ConcurrentHashMap<Integer, TierInfo>();
    private static final String BALANCE_LOG_FILE = "data/items/balanced_items_log.txt";
    private static long lastTierCacheUpdate = 0;
    private static final long CACHE_REFRESH_INTERVAL = 5 * 60 * 1000L; // 5 minutes
    
    // FIXED: Use same tier ranges as ItemBalancer v3.0 (BALANCED system)
    private static final int[] BALANCED_TIER_MINS = { 
        10,   25,   45,   70,   100,  140,  185,  240,  300,  375   // Same as ItemBalancer v3.0
    };
    private static final int[] BALANCED_TIER_MAXS = { 
        20,   40,   65,   95,   135,  180,  235,  300,  375,  475   // Same as ItemBalancer v3.0
    };

    // NEW: Offhand weapon keywords for detection
    private static final String[] OFFHAND_WEAPON_KEYWORDS = {
        "offhand", "off-hand", "secondary", "dual", "left", "parrying", 
        "throwing", "hand crossbow", "buckler", "main gauche", "sai",
        "offhook", "off hook", "secondary weapon"
    };

    /**
     * ENHANCED: Enhanced examine with offhand weapon support and immediate tier information
     */
    public static final String getExamine(Item item) {
        try {
            int price = GrandExchange.getPrice(item.getId());
            if (price < Integer.MIN_VALUE || price > Integer.MAX_VALUE) {
                price = 1;
            }
            
            // Handle stacked items
            if (item.getAmount() >= 10000) {
                return item.getAmount() + "x of " + item.getDefinitions().getName() + ".";
            }
            
            // Handle noted items
            if (item.getDefinitions().isNoted()) {
                return "Swap this note at any bank for the equivalent item.";
            }
            
            // Get base examine text
            String examine = itemExamines.get(item.getId());
            if (examine == null) {
                examine = "It's an " + item.getDefinitions().getName();
            }
            
            // ENHANCED: Get tier information with offhand support
            String tierInfo = getTierInformationWithOffhandSupport(item);
            if (tierInfo != null && !tierInfo.isEmpty()) {
                examine += "<br>" + tierInfo;
            }
            
            // Add damage information for weapons (including offhand)
            String damageInfo = getDamageInformationWithOffhandSupport(item);
            if (damageInfo != null && !damageInfo.isEmpty()) {
                examine += "<br>" + damageInfo;
            }
            
            // Add Grand Exchange price for tradeable items
            if (ItemConstants.isTradeable(item)) {
                examine += "<br>Grand Exchange guide price: " + Utils.getFormattedNumber(price);
            }
            
            return examine + ".";
            
        } catch (Exception e) {
            Logger.handle(e);
            return "It's an " + item.getDefinitions().getName() + ".";
        }
    }

    /**
     * ENHANCED: Get tier information with offhand weapon support
     */
    private static String getTierInformationWithOffhandSupport(Item item) {
        try {
            // First try cache (but refresh if needed)
            refreshTierCacheIfNeeded();
            
            TierInfo tierInfo = tierCache.get(item.getId());
            if (tierInfo != null) {
                return formatTierDisplayWithOffhandSupport(tierInfo, item.getDefinitions().getName());
            }
            
            // Enhanced fallback with offhand detection
            int[] bonuses = ItemBonuses.getItemBonuses(item.getId());
            if (bonuses != null && bonuses.length > 0) {
                int maxStat = getMaxStat(bonuses);
                if (maxStat > 0) {
                    // Check if it's an offhand weapon
                    boolean isOffhand = isOffhandWeapon(item);
                    
                    // Calculate tier (for offhand, use adjusted calculation)
                    int tier = determineTierFromStatFixed(maxStat);
                    if (isOffhand) {
                        // Offhand weapons have reduced stats, so we need to reverse-calculate the intended tier
                        tier = calculateMainhandEquivalentTier(maxStat);
                    }
                    
                    if (tier > 0) {
                        String itemClass = getItemClassWithOffhandSupport(item.getDefinitions().getName().toLowerCase(), isOffhand);
                        String weaponType = isOffhand ? "Offhand" : getWeaponTypeDescription(item.getDefinitions().getName().toLowerCase());
                        
                        StringBuilder tierDisplay = new StringBuilder();
                        tierDisplay.append("Tier: ").append(tier).append(" (").append(getTierNameShort(tier)).append(")");
                        
                        if (itemClass != null) {
                            tierDisplay.append(" | Class: ").append(itemClass);
                        }
                        
                        if (weaponType != null && !weaponType.equals("Offhand")) {
                            tierDisplay.append(" | Type: ").append(weaponType);
                        }
                        
                        tierDisplay.append(" | Power: ").append(maxStat);
                        
                        // Add offhand-specific information
                        if (isOffhand) {
                            tierDisplay.append("<br><col=9900ff>Offhand Weapon: 70-80% of mainhand stats</col>");
                            tierDisplay.append("<br><col=9900ff>Requires mainhand weapon for optimal use</col>");
                        }
                        
                        return tierDisplay.toString();
                    }
                }
            }
            
            return null;
            
        } catch (Exception e) {
            Logger.handle(e);
            return null;
        }
    }

    /**
     * NEW: Check if item is an offhand weapon
     */
    private static boolean isOffhandWeapon(Item item) {
        try {
            String itemName = item.getDefinitions().getName().toLowerCase();
            
            // Check for offhand keywords
            for (String keyword : OFFHAND_WEAPON_KEYWORDS) {
                if (itemName.contains(keyword)) {
                    return true;
                }
            }
            
            // Check if item name suggests offhand (starts with "off" for balanced items)
            if (itemName.startsWith("off") && !itemName.startsWith("officer")) {
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            Logger.handle(e);
            return false;
        }
    }

    /**
     * NEW: Check if item name suggests offhand weapon
     */
    private static boolean isOffhandWeaponByName(String itemName) {
        if (itemName == null) return false;
        
        for (String keyword : OFFHAND_WEAPON_KEYWORDS) {
            if (itemName.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * NEW: Calculate mainhand equivalent tier for offhand weapons
     */
    private static int calculateMainhandEquivalentTier(int offhandMaxStat) {
        // Offhand weapons have ~75% of mainhand stats
        // So we reverse-calculate: mainhandStat = offhandStat / 0.75
        int estimatedMainhandStat = (int) (offhandMaxStat / 0.75);
        
        // Use the estimated mainhand stat to determine tier
        return determineTierFromStatFixed(estimatedMainhandStat);
    }

    /**
     * ENHANCED: Format tier display with offhand support
     */
    private static String formatTierDisplayWithOffhandSupport(TierInfo tierInfo, String currentName) {
        StringBuilder display = new StringBuilder();
        
        display.append("Tier: ").append(tierInfo.tier)
               .append(" (").append(getTierNameShort(tierInfo.tier)).append(")");
        
        // Check if this is an offhand weapon
        boolean isOffhand = currentName != null && isOffhandWeaponByName(currentName.toLowerCase());
        
        if (tierInfo.type != null) {
            String itemClass = getItemClassFromTypeWithOffhandSupport(tierInfo.type, isOffhand);
            if (itemClass != null) {
                display.append(" | Class: ").append(itemClass);
            }
        } else {
            String itemClass = getItemClassWithOffhandSupport(currentName.toLowerCase(), isOffhand);
            if (itemClass != null) {
                display.append(" | Class: ").append(itemClass);
            }
        }
        
        if (tierInfo.maxStat != null && tierInfo.maxStat > 0) {
            display.append(" | Power: ").append(tierInfo.maxStat);
        }
        
        if (tierInfo.intensity != null && tierInfo.intensity != 1.0) {
            display.append(" | Intensity: ").append(tierInfo.intensity);
        }
        
        // Add offhand-specific information
        if (isOffhand) {
            display.append("<br><col=9900ff>Offhand Weapon: Reduced stats for dual-wielding</col>");
        }
        
        return display.toString();
    }

    /**
     * ENHANCED: Get item class with offhand support
     */
    private static String getItemClassWithOffhandSupport(String itemName, boolean isOffhand) {
        if (itemName == null) return null;
        
        // Get base class
        String baseClass = getItemClass(itemName);
        
        if (isOffhand && baseClass != null) {
            // Add "Offhand" prefix to class name
            return "Offhand " + baseClass;
        }
        
        return baseClass;
    }

    /**
     * ENHANCED: Get item class from ItemBalancer type with offhand support
     */
    private static String getItemClassFromTypeWithOffhandSupport(String type, boolean isOffhand) {
        if (type == null) return null;
        
        // Get base class
        String baseClass = getItemClassFromType(type);
        
        if (isOffhand && baseClass != null) {
            return "Offhand " + baseClass;
        }
        
        return baseClass;
    }

    /**
     * NEW: Get weapon type description
     */
    private static String getWeaponTypeDescription(String itemName) {
        if (itemName == null) return null;
        
        if (isTwoHandedWeaponByName(itemName)) {
            return "2-Handed";
        } else if (isOffhandWeaponByName(itemName)) {
            return "Offhand";
        } else if (isWeapon(itemName)) {
            return "1-Handed";
        }
        
        return null;
    }

    /**
     * NEW: Check if item name suggests 2H weapon
     */
    private static boolean isTwoHandedWeaponByName(String itemName) {
        if (itemName == null) return false;
        
        String[] twoHandedKeywords = {
            "greataxe", "greatsword", "claymore", "battleaxe", "warhammer", 
            "halberd", "spear", "lance", "longbow", "crossbow", "staff", 
            "battlestaff", "godsword", "scythe", "maul", "2h", "two-handed"
        };
        
        for (String keyword : twoHandedKeywords) {
            if (itemName.contains(keyword)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * ENHANCED: Get damage information with offhand support
     */
    private static String getDamageInformationWithOffhandSupport(Item item) {
        try {
            String itemName = item.getDefinitions().getName().toLowerCase();
            
            // Check if it's a weapon
            if (!isWeapon(itemName)) {
                return null;
            }
            
            int[] bonuses = ItemBonuses.getItemBonuses(item.getId());
            if (bonuses == null || bonuses.length < 15) {
                return null;
            }
            
            // Get relevant attack and strength bonuses
            int attackBonus = getMaxAttackBonus(bonuses);
            int strengthBonus = bonuses[14]; // Strength bonus
            
            if (attackBonus <= 0 && strengthBonus <= 0) {
                return null;
            }
            
            // Check if it's an offhand weapon
            boolean isOffhand = isOffhandWeapon(item);
            
            // Calculate damage range based on bonuses
            DamageRange damageRange = calculateDamageRangeWithOffhandSupport(attackBonus, strengthBonus, itemName, isOffhand);
            
            if (damageRange != null) {
                StringBuilder damageInfo = new StringBuilder();
                damageInfo.append("Damage: ").append(damageRange.minDamage).append(" - ").append(damageRange.maxDamage);
                damageInfo.append(" | Accuracy: +").append(attackBonus).append(" | Strength: +").append(strengthBonus);
                
                if (isOffhand) {
                    damageInfo.append("<br><col=9900ff>Offhand: Combines with mainhand for dual-wield attacks</col>");
                }
                
                return damageInfo.toString();
            }
            
            return null;
            
        } catch (Exception e) {
            Logger.handle(e);
            return null;
        }
    }

    /**
     * ENHANCED: Calculate damage range with offhand support
     */
    private static DamageRange calculateDamageRangeWithOffhandSupport(int attackBonus, int strengthBonus, String weaponName, boolean isOffhand) {
        try {
            // Base damage calculation similar to RS combat formula
            int baseDamage = Math.max(1, strengthBonus / 10);
            int maxHit = (int) ((baseDamage + strengthBonus * 0.15) * 1.2);
            int minHit = Math.max(1, maxHit / 4);
            
            // Weapon type modifiers
            double modifier = getWeaponModifier(weaponName);
            
            // Offhand modifier (slightly reduced)
            if (isOffhand) {
                modifier *= 0.9; // 90% of mainhand damage potential
            }
            
            maxHit = (int) (maxHit * modifier);
            minHit = (int) (minHit * modifier);
            
            // Ensure minimum values
            minHit = Math.max(1, minHit);
            maxHit = Math.max(minHit + 1, maxHit);
            
            return new DamageRange(minHit, maxHit);
            
        } catch (Exception e) {
            Logger.handle(e);
            return null;
        }
    }
    
    /**
     * FIXED: Force immediate tier cache refresh (call this after adjuststats)
     */
    public static void forceRefreshTierCache() {
        lastTierCacheUpdate = 0; // Force refresh
        refreshTierCacheIfNeeded();
        Logger.log("ItemExamines", "Forced tier cache refresh for immediate update");
    }
    
    /**
     * Refresh tier cache by reading from existing ItemBalancer log
     */
    private static void refreshTierCacheIfNeeded() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTierCacheUpdate < CACHE_REFRESH_INTERVAL) {
            return; // Cache is still fresh
        }
        
        try {
            File logFile = new File(BALANCE_LOG_FILE);
            if (!logFile.exists()) {
                return; // No log file from ItemBalancer yet
            }
            
            Map<Integer, TierInfo> newCache = new ConcurrentHashMap<Integer, TierInfo>();
            BufferedReader reader = null;
            
            try {
                reader = new BufferedReader(new FileReader(logFile));
                String line;
                
                Integer currentItemId = null;
                String currentItemName = null;
                Integer currentTier = null;
                String currentType = null;
                Double currentIntensity = null;
                Integer currentMaxStat = null;
                
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    
                    if (line.startsWith("=== ") && line.endsWith(" ===")) {
                        // Parse item header: === Item Name (ID: 12345) ===
                        String content = line.substring(4, line.length() - 4);
                        int idStart = content.lastIndexOf("(ID: ");
                        if (idStart > 0) {
                            currentItemName = content.substring(0, idStart).trim();
                            String idPart = content.substring(idStart + 5);
                            int idEnd = idPart.indexOf(")");
                            if (idEnd > 0) {
                                try {
                                    currentItemId = Integer.parseInt(idPart.substring(0, idEnd));
                                } catch (NumberFormatException e) {
                                    currentItemId = null;
                                }
                            }
                        }
                    } else if (line.startsWith("Tier: ") && currentItemId != null) {
                        String tierStr = line.substring(6).split(" ")[0];
                        try {
                            currentTier = Integer.parseInt(tierStr);
                        } catch (NumberFormatException e) {
                            currentTier = null;
                        }
                    } else if (line.startsWith("Type: ")) {
                        currentType = line.substring(6);
                    } else if (line.startsWith("Intensity: ")) {
                        try {
                            currentIntensity = Double.parseDouble(line.substring(11));
                        } catch (NumberFormatException e) {
                            currentIntensity = null;
                        }
                    } else if (line.startsWith("MAX STAT: ")) {
                        String statStr = line.substring(10).split(" ")[0];
                        try {
                            currentMaxStat = Integer.parseInt(statStr);
                        } catch (NumberFormatException e) {
                            currentMaxStat = null;
                        }
                    } else if (line.equals("----------------------------------------")) {
                        // End of entry - save if we have complete info
                        if (currentItemId != null && currentTier != null && currentItemName != null) {
                            TierInfo info = new TierInfo();
                            info.itemId = currentItemId;
                            info.itemName = currentItemName;
                            info.tier = currentTier;
                            info.type = currentType;
                            info.intensity = currentIntensity;
                            info.maxStat = currentMaxStat;
                            
                            newCache.put(currentItemId, info);
                        }
                        
                        // Reset for next entry
                        currentItemId = null;
                        currentItemName = null;
                        currentTier = null;
                        currentType = null;
                        currentIntensity = null;
                        currentMaxStat = null;
                    }
                }
                
            } finally {
                if (reader != null) {
                    reader.close();
                }
            }
            
            // Update cache atomically
            tierCache.clear();
            tierCache.putAll(newCache);
            lastTierCacheUpdate = currentTime;
            
            Logger.log("ItemExamines", "Refreshed tier cache with " + newCache.size() + " entries");
            
        } catch (Exception e) {
            Logger.handle(e);
        }
    }
    
    /**
     * Get item class from ItemBalancer type
     */
    private static String getItemClassFromType(String type) {
        if (type == null) return null;
        
        String typeLower = type.toLowerCase();
        if (typeLower.contains("sword") || typeLower.contains("scimitar")) {
            return "Blade";
        } else if (typeLower.contains("axe")) {
            return "Cleaver";
        } else if (typeLower.contains("mace") || typeLower.contains("hammer")) {
            return "Crusher";
        } else if (typeLower.contains("dagger")) {
            return "Piercer";
        } else if (typeLower.contains("spear")) {
            return "Polearm";
        } else if (typeLower.contains("whip")) {
            return "Lash";
        } else if (typeLower.contains("bow")) {
            return "Archer";
        } else if (typeLower.contains("crossbow")) {
            return "Marksman";
        } else if (typeLower.contains("staff")) {
            return "Caster";
        } else if (typeLower.contains("wand")) {
            return "Channeler";
        } else if (typeLower.contains("melee")) {
            return "Warrior";
        } else if (typeLower.contains("range")) {
            return "Ranger";
        } else if (typeLower.contains("mage")) {
            return "Mage";
        } else if (typeLower.contains("shield")) {
            return "Guardian";
        } else if (typeLower.contains("ring")) {
            return "Band";
        } else if (typeLower.contains("amulet")) {
            return "Jewelry";
        } else if (typeLower.contains("cape")) {
            return "Mantle";
        } else if (typeLower.contains("hybrid")) {
            return "Hybrid";
        } else if (typeLower.contains("utility")) {
            return "Utility";
        } else if (typeLower.contains("tank")) {
            return "Tank";
        }
        
        return null;
    }
    
    /**
     * Get weapon modifier based on weapon type
     */
    private static double getWeaponModifier(String weaponName) {
        if (weaponName.contains("bow") || weaponName.contains("crossbow")) {
            return 1.1; // Ranged weapons slightly higher
        } else if (weaponName.contains("staff") || weaponName.contains("wand")) {
            return 0.9; // Magic weapons slightly lower
        } else if (weaponName.contains("dagger")) {
            return 0.8; // Daggers faster but lower damage
        } else if (weaponName.contains("whip")) {
            return 1.0; // Whips balanced
        } else if (weaponName.contains("axe") || weaponName.contains("mace")) {
            return 1.15; // Heavy weapons higher damage
        }
        return 1.0; // Default modifier
    }
    
    /**
     * Check if item is a weapon
     */
    private static boolean isWeapon(String itemName) {
        String[] weaponKeywords = {
            "sword", "axe", "mace", "dagger", "spear", "whip", "scimitar", 
            "bow", "crossbow", "staff", "wand", "hammer", "lance", "halberd",
            "claw", "knife", "javelin", "dart", "throwing"
        };
        
        for (String keyword : weaponKeywords) {
            if (itemName.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Get maximum attack bonus from bonuses array
     */
    private static int getMaxAttackBonus(int[] bonuses) {
        int maxAttack = 0;
        // Check attack bonuses (indices 0-4: stab, slash, crush, magic, ranged)
        for (int i = 0; i < 5 && i < bonuses.length; i++) {
            if (bonuses[i] > maxAttack) {
                maxAttack = bonuses[i];
            }
        }
        return maxAttack;
    }
    
    /**
     * FIXED: Determine tier based on max stat using BALANCED ranges (matches ItemBalancer v3.0)
     */
    private static int determineTierFromStatFixed(int maxStat) {
        for (int tier = 1; tier <= 10; tier++) {
            int min = BALANCED_TIER_MINS[tier - 1];
            int max = BALANCED_TIER_MAXS[tier - 1];
            if (maxStat >= min && maxStat <= max) {
                return tier;
            }
        }
        
        // Handle stats outside normal ranges
        if (maxStat < BALANCED_TIER_MINS[0]) {
            return 1; // Below tier 1, still consider tier 1
        } else if (maxStat > BALANCED_TIER_MAXS[9]) {
            return 10; // Above tier 10, still consider tier 10
        }
        
        return -1; // Unknown tier
    }
    
    /**
     * FIXED: Get tier name (short version to match the new system)
     */
    private static String getTierNameShort(int tier) {
        switch (tier) {
            case 1: return "Beginner";
            case 2: return "Novice";
            case 3: return "Intermediate";
            case 4: return "Advanced";
            case 5: return "Expert";
            case 6: return "Master";
            case 7: return "Elite";
            case 8: return "Legendary";
            case 9: return "Mythical";
            case 10: return "Divine";
            default: return "Unknown";
        }
    }
    
    /**
     * ENHANCED: Get item class based on name (updated for offhand support)
     */
    private static String getItemClass(String itemName) {
        if (itemName == null) return null;
        
        // Remove offhand prefixes for base classification
        String baseName = itemName;
        if (baseName.startsWith("offhand ")) {
            baseName = baseName.substring(8);
        } else if (baseName.startsWith("off-hand ")) {
            baseName = baseName.substring(9);
        } else if (baseName.startsWith("off")) {
            baseName = baseName.substring(3);
        }
        
        // Classify based on base name
        if (baseName.contains("sword") || baseName.contains("scimitar")) {
            return "Blade";
        } else if (baseName.contains("axe")) {
            return "Cleaver";
        } else if (baseName.contains("mace") || baseName.contains("hammer")) {
            return "Crusher";
        } else if (baseName.contains("dagger")) {
            return "Piercer";
        } else if (baseName.contains("spear") || baseName.contains("lance")) {
            return "Polearm";
        } else if (baseName.contains("whip")) {
            return "Lash";
        } else if (baseName.contains("bow")) {
            return "Archer";
        } else if (baseName.contains("crossbow")) {
            return "Marksman";
        } else if (baseName.contains("staff")) {
            return "Caster";
        } else if (baseName.contains("wand")) {
            return "Channeler";
        } else if (baseName.contains("body") || baseName.contains("plate") || baseName.contains("chest")) {
            return "Armor";
        } else if (baseName.contains("leg") || baseName.contains("skirt")) {
            return "Legwear";
        } else if (baseName.contains("helm") || baseName.contains("hat") || baseName.contains("hood")) {
            return "Headgear";
        } else if (baseName.contains("glove") || baseName.contains("gauntlet")) {
            return "Handwear";
        } else if (baseName.contains("boot") || baseName.contains("shoes")) {
            return "Footwear";
        } else if (baseName.contains("shield")) {
            return "Guardian";
        } else if (baseName.contains("ring")) {
            return "Band";
        } else if (baseName.contains("amulet") || baseName.contains("necklace")) {
            return "Jewelry";
        } else if (baseName.contains("cape") || baseName.contains("cloak")) {
            return "Mantle";
        }
        return null;
    }
    
    /**
     * Get maximum stat from array
     */
    private static int getMaxStat(int[] stats) {
        int max = 0;
        for (int stat : stats) {
            if (stat > max) {
                max = stat;
            }
        }
        return max;
    }
    
    /**
     * Tier information storage class
     */
    private static class TierInfo {
        Integer itemId;
        String itemName;
        Integer tier;
        String type;
        Double intensity;
        Integer maxStat;
    }
    
    /**
     * Damage range helper class
     */
    private static class DamageRange {
        final int minDamage;
        final int maxDamage;
        
        DamageRange(int minDamage, int maxDamage) {
            this.minDamage = minDamage;
            this.maxDamage = maxDamage;
        }
    }

    /**
     * Initialize the examine system
     */
    public static final void init() {
        try {
            if (new File(PACKED_PATH).exists()) {
                loadPackedItemExamines();
            } else {
                loadUnpackedItemExamines();
            }
            Logger.log("ItemExamines", "Enhanced Item Examine System v3.1 with Offhand Support loaded successfully by Zeus");
        } catch (Exception e) {
            Logger.handle(e);
        }
    }

    /**
     * Load packed examines with improved error handling
     */
    private static void loadPackedItemExamines() {
        RandomAccessFile in = null;
        FileChannel channel = null;
        try {
            in = new RandomAccessFile(PACKED_PATH, "r");
            channel = in.getChannel();
            ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
            
            while (buffer.hasRemaining()) {
                int itemId = buffer.getShort() & 0xffff;
                String examine = readAlexString(buffer);
                itemExamines.put(itemId, examine);
            }
            
        } catch (Exception e) {
            Logger.handle(e);
        } finally {
            // Proper resource cleanup for Java 1.7
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException e) {
                    Logger.handle(e);
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Logger.handle(e);
                }
            }
        }
    }

    /**
     * Load unpacked examines with improved error handling
     */
    private static void loadUnpackedItemExamines() {
        Logger.log("ItemExamines", "Packing item examines...");
        
        BufferedReader in = null;
        DataOutputStream out = null;
        
        try {
            in = new BufferedReader(new FileReader(UNPACKED_PATH));
            out = new DataOutputStream(new FileOutputStream(PACKED_PATH));
            
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("//") || line.trim().isEmpty()) {
                    continue;
                }
                
                line = line.replace("﻿", ""); // Remove BOM
                String[] splitedLine = line.split(" - ", 2);
                
                if (splitedLine.length < 2) {
                    Logger.log("ItemExamines", "Invalid examine line: " + line);
                    continue;
                }
                
                try {
                    int itemId = Integer.parseInt(splitedLine[0]);
                    String examine = splitedLine[1];
                    
                    if (examine.length() > 255) {
                        Logger.log("ItemExamines", "Examine too long for item " + itemId + ": " + examine.length() + " chars");
                        continue;
                    }
                    
                    out.writeShort(itemId);
                    writeAlexString(out, examine);
                    itemExamines.put(itemId, examine);
                    
                } catch (NumberFormatException e) {
                    Logger.log("ItemExamines", "Invalid item ID: " + splitedLine[0]);
                }
            }
            
        } catch (FileNotFoundException e) {
            Logger.log("ItemExamines", "Unpacked examines file not found: " + UNPACKED_PATH);
        } catch (IOException e) {
            Logger.handle(e);
        } finally {
            // Proper resource cleanup for Java 1.7
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Logger.handle(e);
                }
            }
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    Logger.handle(e);
                }
            }
        }
    }

    /**
     * Read string from ByteBuffer
     */
    public static String readAlexString(ByteBuffer buffer) {
        int count = buffer.get() & 0xff;
        byte[] bytes = new byte[count];
        buffer.get(bytes, 0, count);
        return new String(bytes);
    }

    /**
     * Write string to DataOutputStream
     */
    public static void writeAlexString(DataOutputStream out, String string) throws IOException {
        byte[] bytes = string.getBytes();
        out.writeByte(bytes.length);
        out.write(bytes);
    }

    /**
     * Get Grand Exchange examine text
     */
    public static final String getGEExamine(Item item) {
        try {
            if (item.getDefinitions().isNoted()) {
                item.setId(item.getDefinitions().getCertId());
            }
            
            String examine = itemExamines.get(item.getId());
            if (examine != null) {
                return examine;
            }
            
            return "It's an " + item.getDefinitions().getName() + ".";
            
        } catch (Exception e) {
            Logger.handle(e);
            return "It's an item.";
        }
    }
    
    /**
     * Clear tier cache for memory management
     */
    public static void clearTierCache() {
        tierCache.clear();
        lastTierCacheUpdate = 0;
        Logger.log("ItemExamines", "Tier cache cleared for memory optimization");
    }
    
    /**
     * Force refresh tier cache (for admin commands)
     */
    public static void refreshTierCache() {
        lastTierCacheUpdate = 0;
        refreshTierCacheIfNeeded();
    }
    
    /**
     * ENHANCED: Get tier info for a specific item with offhand awareness
     */
    public static String getItemTierInfoWithOffhandSupport(int itemId) {
        try {
            refreshTierCacheIfNeeded();
            
            TierInfo tierInfo = tierCache.get(itemId);
            if (tierInfo != null) {
                ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
                boolean isOffhand = itemDef != null && isOffhandWeaponByName(itemDef.getName().toLowerCase());
                
                return "Cached: Tier " + tierInfo.tier + " (" + getTierNameShort(tierInfo.tier) + ")" + 
                       (isOffhand ? " [Offhand]" : "");
            }
            
            // Fallback to direct stat reading with offhand detection
            int[] bonuses = ItemBonuses.getItemBonuses(itemId);
            if (bonuses != null && bonuses.length > 0) {
                int maxStat = getMaxStat(bonuses);
                
                ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
                boolean isOffhand = itemDef != null && isOffhandWeaponByName(itemDef.getName().toLowerCase());
                
                int tier;
                if (isOffhand) {
                    tier = calculateMainhandEquivalentTier(maxStat);
                } else {
                    tier = determineTierFromStatFixed(maxStat);
                }
                
                return "Direct: Tier " + tier + " (" + getTierNameShort(tier) + ") | Power: " + maxStat + 
                       (isOffhand ? " [Offhand - ~" + (int)(maxStat / 0.75) + " mainhand equivalent]" : "");
            }
            
            return "No tier information found";
            
        } catch (Exception e) {
            Logger.handle(e);
            return "Error getting tier info: " + e.getMessage();
        }
    }
    
    /**
     * LEGACY: Get tier info for a specific item (for compatibility)
     */
    public static String getItemTierInfo(int itemId) {
        return getItemTierInfoWithOffhandSupport(itemId);
    }
    
    /**
     * NEW: Debug method to show tier ranges
     */
    public static void showTierRanges() {
        Logger.log("ItemExamines", "=== TIER RANGES (FIXED v3.1 with Offhand Support) ===");
        for (int tier = 1; tier <= 10; tier++) {
            int min = BALANCED_TIER_MINS[tier - 1];
            int max = BALANCED_TIER_MAXS[tier - 1];
            Logger.log("ItemExamines", "Tier " + tier + " (" + getTierNameShort(tier) + "): " + min + "-" + max);
        }
        Logger.log("ItemExamines", "NOTE: Offhand weapons are automatically calculated to their mainhand equivalent tier");
    }
}