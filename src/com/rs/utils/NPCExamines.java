/**
 * Enhanced NPC Examination System - BossBalancer v4.1 Integration
 * ENHANCED: Equipment analysis, prayer integration, effective tier display, and improved scaling info
 * MAINTAINED: Packet overflow protection and concise display
 * 
 * @author Zeus
 * @date June 09, 2025
 * @version 4.2.0 - EQUIPMENT ANALYSIS + PRAYER INTEGRATION + ENHANCED SCALING INFO
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

import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitionsManager;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.Player;
import com.rs.game.player.Equipment;
import com.rs.game.item.Item;

public class NPCExamines {

    private static final Map<Integer, String> npcExamines = new ConcurrentHashMap<Integer, String>();
    private static final String PACKED_PATH = "data/npcs/packedNPCExamines.e";
    private static final String UNPACKED_PATH = "data/npcs/unpackedNPCExamines.txt";
    
    // CRITICAL: Packet size limits to prevent crashes
    private static final int MAX_EXAMINE_LENGTH = 300; // Increased for equipment analysis
    private static final int MAX_ADDON_LENGTH = 150;   // Increased for enhanced info

    /**
     * SAFE examine with BossBalancer v4.1 enhanced info - PACKET SIZE PROTECTED
     */
    public static final String getExamine(NPC npc) {
        return getExamine(npc, null);
    }

    /**
     * ENHANCED: BossBalancer v4.1 integration with equipment analysis and prayer
     */
    public static final String getExamine(NPC npc, Player examiningPlayer) {
        // CRITICAL: Null safety checks first
        if (npc == null) {
            return "It's an unknown creature.";
        }

        try {
            // Safe NPC definitions check
            NPCDefinitions npcDef = null;
            String npcName = "unknown creature";
            int npcId = -1;

            try {
                npcDef = npc.getDefinitions();
                if (npcDef != null) {
                    npcName = npcDef.getName();
                    if (npcName == null || npcName.trim().isEmpty()) {
                        npcName = "unknown creature";
                    }
                }
                npcId = npc.getId();
            } catch (Exception e) {
                Logger.handle(e);
                return "It's an unknown creature.";
            }

            // Get base examine text safely
            String baseExamine = null;
            try {
                baseExamine = npcExamines.get(Integer.valueOf(npcId));
            } catch (Exception e) {
                Logger.handle(e);
            }

            if (baseExamine == null || "unknown".equals(baseExamine)) {
                baseExamine = "It's " + Utils.getAorAn(npcName) + " " + npcName;
            }

            // ENHANCED: Add comprehensive info for head staff
            String finalExamine = baseExamine;

            if (examiningPlayer != null && examiningPlayer.isHeadStaff()) {
                try {
                    StringBuilder enhancedInfo = new StringBuilder();
                    
                    // Add BossBalancer info
                    String tierInfo = getEnhancedBossBalancerInfo(npc, examiningPlayer);
                    if (tierInfo != null && !tierInfo.trim().isEmpty()) {
                        enhancedInfo.append(" ").append(tierInfo);
                    }
                    
                    // Add equipment analysis
                    String equipmentInfo = getCompactEquipmentAnalysis(npc);
                    if (equipmentInfo != null && !equipmentInfo.trim().isEmpty()) {
                        enhancedInfo.append(" ").append(equipmentInfo);
                    }
                    
                    // Add combat assessment
                    String combatInfo = getCompactCombatAssessment(npc);
                    if (combatInfo != null && !combatInfo.trim().isEmpty()) {
                        enhancedInfo.append(" ").append(combatInfo);
                    }
                    
                    String combined = baseExamine + enhancedInfo.toString();
                    
                    // CRITICAL: Check packet size limit
                    if (combined.length() <= MAX_EXAMINE_LENGTH) {
                        finalExamine = combined;
                    } else {
                        // Use compact version if too long
                        finalExamine = baseExamine + " " + 
                                     (tierInfo != null ? tierInfo : "[Enhanced NPC]");
                    }
                } catch (Exception e) {
                    // Don't add anything if there's an error
                }
            }

            return finalExamine + ".";

        } catch (Exception e) {
            // Ultimate safety fallback
            Logger.handle(e);

            try {
                NPCDefinitions safeDef = npc.getDefinitions();
                if (safeDef != null && safeDef.getName() != null) {
                    return "It's " + Utils.getAorAn(safeDef.getName()) + " " + safeDef.getName() + ".";
                }
            } catch (Exception e2) {
                // Ignore
            }

            return "It's an unknown creature.";
        }
    }

    /**
     * ENHANCED: Get comprehensive BossBalancer v4.1 info with prayer integration
     */
    private static String getEnhancedBossBalancerInfo(NPC npc, Player player) {
        try {
            // Check if BossBalancer is available
            if (!isBossBalancerAvailable()) {
                return null;
            }

            StringBuilder info = new StringBuilder();

            // Get boss tier and configuration status
            int bossTier = BossBalancer.getBossEffectiveTier(npc);
            boolean isConfigured = BossBalancer.isBossConfigured(npc.getId());
            
            info.append("[");
            if (isConfigured) {
                info.append("T").append(bossTier).append("★"); // Star indicates configured
            } else {
                info.append("T").append(bossTier).append("?"); // Question mark indicates auto-calculated
            }

            // Get player comparison with prayer integration
            BossBalancer.CombatScaling scaling = BossBalancer.getCombatScaling(player, npc);
            if (scaling != null) {
                // Show gear tier vs effective tier
                info.append(" vs G").append(scaling.playerTier);
                
                // Add prayer tier if significant
                if (scaling.prayerTier >= 0.5) {
                    info.append("+P").append(String.format("%.1f", scaling.prayerTier));
                }
                
                // Show effective tier
                info.append("=").append(String.format("%.1f", scaling.effectivePlayerTier));
                
                // Show scaling status with more detail
                String scalingType = scaling.scalingType;
                if ("UNDERGEARED".equals(scalingType)) {
                    info.append(" UNDER");
                } else if (scalingType.contains("OVERGEARED")) {
                    int penalty = (int)((scaling.bossHpMultiplier - 1.0) * 100);
                    if (scalingType.contains("PRAYER")) {
                        info.append(" PRAY+").append(penalty).append("%");
                    } else {
                        info.append(" OVER+").append(penalty).append("%");
                    }
                } else if (scalingType.contains("BALANCED")) {
                    if (scalingType.contains("PRAYER")) {
                        int penalty = (int)((scaling.bossHpMultiplier - 1.0) * 100);
                        info.append(" BAL+").append(penalty).append("%");
                    } else {
                        info.append(" BAL");
                    }
                }

                // Dual-wield indicator
                if (BossBalancer.isDualWielding(player)) {
                    info.append(" DW");
                }
            }

            info.append("]");

            // CRITICAL: Check length limit
            String result = info.toString();
            if (result.length() > MAX_ADDON_LENGTH) {
                // Fallback to compact version
                if (scaling != null) {
                    return "[T" + bossTier + " vs " + String.format("%.1f", scaling.effectivePlayerTier) + "]";
                } else {
                    return "[T" + bossTier + " Boss]";
                }
            }

            return result;

        } catch (Exception e) {
            return null; // Don't log errors to avoid spam
        }
    }

    /**
     * ENHANCED: Get compact equipment analysis for examine
     */
    private static String getCompactEquipmentAnalysis(NPC npc) {
        try {
            Map<String, Integer> equippedItems = getNPCEquippedItems(npc);
            if (equippedItems == null || equippedItems.isEmpty()) {
                return null;
            }

            StringBuilder equipInfo = new StringBuilder();
            
            // Analyze weapon setup (compact version)
            String weaponSetup = analyzeNPCWeaponSetupCompact(equippedItems);
            if (weaponSetup != null) {
                equipInfo.append("[").append(weaponSetup).append("]");
            }

            // Add armor tier summary
            String armorSummary = getArmorTierSummary(equippedItems);
            if (armorSummary != null) {
                if (equipInfo.length() > 0) equipInfo.append(" ");
                equipInfo.append("[").append(armorSummary).append("]");
            }

            return equipInfo.length() > 0 ? equipInfo.toString() : null;

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * ENHANCED: Get compact combat assessment
     */
    private static String getCompactCombatAssessment(NPC npc) {
        try {
            int npcCombatLevel = npc.getCombatLevel();
            Map<String, Integer> equippedItems = getNPCEquippedItems(npc);
            int gearBonus = estimateGearBonus(equippedItems);
            
            if (npcCombatLevel <= 0 && gearBonus <= 0) {
                return null;
            }

            StringBuilder combatInfo = new StringBuilder();
            combatInfo.append("[CB").append(npcCombatLevel);
            
            if (gearBonus > 0) {
                combatInfo.append("+").append(gearBonus);
            }
            
            String difficulty = assessDifficulty(npcCombatLevel, gearBonus);
            combatInfo.append(" ").append(difficulty.substring(0, Math.min(4, difficulty.length())).toUpperCase());
            combatInfo.append("]");

            return combatInfo.toString();

        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get NPC equipped items (adapt this to your NPC system)
     */
    private static Map<String, Integer> getNPCEquippedItems(NPC npc) {
        Map<String, Integer> equippedItems = new HashMap<String, Integer>();
        
        try {
            // ADAPT THIS SECTION TO YOUR SERVER'S NPC SYSTEM
            int npcId = npc.getId();
            
            // Method 1: If NPCs have an equipment container like players
            /*
            if (npc.getEquipment() != null) {
                ItemsContainer<Item> equipment = npc.getEquipment().getItems();
                String[] slotNames = {"helmet", "cape", "amulet", "weapon", "body", "shield", "legs", "gloves", "boots", "ring", "arrows"};
                
                for (int slot = 0; slot < equipment.getSize() && slot < slotNames.length; slot++) {
                    Item item = equipment.get(slot);
                    if (item != null && item.getId() > 0) {
                        equippedItems.put(slotNames[slot], item.getId());
                    }
                }
            }
            */
            
            // Method 2: If NPCs have equipment defined in their definitions
            /*
            NPCDefinitions def = npc.getDefinitions();
            if (def.getEquipment() != null) {
                int[] equipment = def.getEquipment();
                String[] slotNames = {"helmet", "cape", "amulet", "weapon", "body", "shield", "legs", "gloves", "boots", "ring", "arrows"};
                
                for (int i = 0; i < equipment.length && i < slotNames.length; i++) {
                    if (equipment[i] > 0) {
                        equippedItems.put(slotNames[i], equipment[i]);
                    }
                }
            }
            */
            
            // Method 3: Example equipment setups for common NPCs (FOR TESTING)
            if (npcId == 1234) { // Example: High-level warrior NPC
                equippedItems.put("weapon", 1277); // Dragon longsword
                equippedItems.put("shield", 1187); // Dragon square shield
                equippedItems.put("body", 1149);   // Dragon chainbody
                equippedItems.put("legs", 1087);   // Dragon platelegs
            } else if (npcId == 5678) { // Example: Dual-wielding NPC
                equippedItems.put("weapon", 1277); // Dragon longsword
                equippedItems.put("shield", 1333); // Dragon scimitar (as offhand)
            } else if (npcId == 9999) { // Example: 2H weapon NPC
                equippedItems.put("weapon", 1305); // Dragon longsword (as 2H)
            }
            
            return equippedItems;
            
        } catch (Exception e) {
            Logger.handle(e);
            return new HashMap<String, Integer>();
        }
    }

    /**
     * Analyze NPC weapon setup (compact version for examine)
     */
    private static String analyzeNPCWeaponSetupCompact(Map<String, Integer> equippedItems) {
        try {
            Integer weaponId = equippedItems.get("weapon");
            Integer shieldId = equippedItems.get("shield");
            
            if (weaponId == null) {
                return "Unarmed";
            }
            
            // Determine combat style
            boolean isTwoHanded = isTwoHandedWeaponById(weaponId);
            boolean hasShield = shieldId != null && !isOffhandWeaponById(shieldId);
            boolean hasOffhand = shieldId != null && isOffhandWeaponById(shieldId);
            
            if (isTwoHanded) {
                return "2H";
            } else if (hasOffhand) {
                return "DW";
            } else if (hasShield) {
                return "S&S";
            } else {
                return "1H";
            }
            
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get armor tier summary
     */
    private static String getArmorTierSummary(Map<String, Integer> equippedItems) {
        try {
            String[] armorSlots = {"helmet", "body", "legs", "gloves", "boots"};
            int totalTiers = 0;
            int armorPieces = 0;
            
            for (String slot : armorSlots) {
                Integer itemId = equippedItems.get(slot);
                if (itemId != null) {
                    // Try to get tier info from ItemExamines if available
                    try {
                        // Estimate tier based on item bonuses
                        int[] bonuses = ItemBonuses.getItemBonuses(itemId);
                        if (bonuses != null) {
                            int maxDefense = 0;
                            for (int i = 5; i <= 10; i++) {
                                if (i < bonuses.length && bonuses[i] > maxDefense) {
                                    maxDefense = bonuses[i];
                                }
                            }
                            
                            // Convert defense to tier estimate
                            int estimatedTier = Math.min(10, Math.max(1, maxDefense / 20));
                            if (estimatedTier > 1) {
                                totalTiers += estimatedTier;
                                armorPieces++;
                            }
                        }
                    } catch (Exception e) {
                        // Ignore individual item errors
                    }
                }
            }
            
            if (armorPieces > 0) {
                double avgTier = (double) totalTiers / armorPieces;
                return "Armor T" + String.format("%.1f", avgTier);
            }
            
            return null;
            
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Estimate gear bonus based on equipment tiers
     */
    private static int estimateGearBonus(Map<String, Integer> equippedItems) {
        if (equippedItems == null || equippedItems.isEmpty()) {
            return 0;
        }
        
        int totalTiers = 0;
        int itemCount = 0;
        
        for (Integer itemId : equippedItems.values()) {
            if (itemId != null) {
                // Estimate tier based on max stat
                int[] bonuses = ItemBonuses.getItemBonuses(itemId);
                if (bonuses != null) {
                    int maxStat = 0;
                    for (int bonus : bonuses) {
                        if (bonus > maxStat) maxStat = bonus;
                    }
                    
                    // Convert max stat to tier estimate
                    int estimatedTier = Math.min(10, Math.max(1, maxStat / 50));
                    totalTiers += estimatedTier;
                    itemCount++;
                }
            }
        }
        
        if (itemCount == 0) return 0;
        
        double avgTier = (double) totalTiers / itemCount;
        return (int) (avgTier * 5); // Each tier adds ~5 combat bonus
    }

    /**
     * Assess difficulty based on combat level and gear
     */
    private static String assessDifficulty(int combatLevel, int gearBonus) {
        int totalPower = combatLevel + gearBonus;
        
        if (totalPower < 50) {
            return "Easy";
        } else if (totalPower < 100) {
            return "Medium";
        } else if (totalPower < 150) {
            return "Hard";
        } else if (totalPower < 200) {
            return "Very Hard";
        } else {
            return "Extreme";
        }
    }

    /**
     * Helper: Check if item is 2H weapon by ID
     */
    private static boolean isTwoHandedWeaponById(int itemId) {
        try {
            ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
            if (itemDef == null) return false;
            
            String itemName = itemDef.getName().toLowerCase();
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
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Helper: Check if item is offhand weapon by ID  
     */
    private static boolean isOffhandWeaponById(int itemId) {
        try {
            ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
            if (itemDef == null) return false;
            
            String itemName = itemDef.getName().toLowerCase();
            String[] offhandKeywords = {
                "offhand", "off-hand", "secondary", "dual", "left", "parrying", 
                "throwing", "hand crossbow", "buckler", "main gauche", "sai"
            };
            
            for (String keyword : offhandKeywords) {
                if (itemName.contains(keyword)) {
                    return true;
                }
            }
            
            return false;
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * ENHANCED: Get detailed BossBalancer info for debugging (separate method)
     */
    public static String getDetailedBossBalancerInfo(NPC npc, Player player) {
        try {
            if (!isBossBalancerAvailable() || npc == null || player == null) {
                return "BossBalancer not available or invalid parameters";
            }

            StringBuilder detail = new StringBuilder();
            
            // Boss information
            int bossTier = BossBalancer.getBossEffectiveTier(npc);
            boolean isConfigured = BossBalancer.isBossConfigured(npc.getId());
            
            detail.append("=== BOSS ANALYSIS ===\n");
            detail.append("Boss ID: ").append(npc.getId()).append("\n");
            detail.append("Boss Tier: ").append(bossTier).append(" (").append(BossBalancer.getBossTierName(bossTier)).append(")\n");
            detail.append("Configured: ").append(isConfigured ? "YES" : "NO (Auto-calculated)").append("\n");

            // Player information
            int playerGearTier = BossBalancer.calculatePlayerGearTier(player);
            double playerPrayerTier = BossBalancer.calculatePlayerPrayerTier(player);
            
            detail.append("\n=== PLAYER ANALYSIS ===\n");
            detail.append("Gear Tier: ").append(playerGearTier).append(" (").append(BossBalancer.getBossTierName(playerGearTier)).append(")\n");
            detail.append("Prayer Tier: ").append(String.format("%.2f", playerPrayerTier)).append("\n");
            
            // Combat scaling
            BossBalancer.CombatScaling scaling = BossBalancer.getCombatScaling(player, npc);
            if (scaling != null) {
                detail.append("Effective Tier: ").append(String.format("%.2f", scaling.effectivePlayerTier)).append("\n");
                detail.append("Tier Difference: ").append(String.format("%.2f", scaling.effectivePlayerTier - scaling.bossTier)).append("\n");
                
                detail.append("\n=== SCALING EFFECTS ===\n");
                detail.append("Boss HP: x").append(String.format("%.3f", scaling.bossHpMultiplier)).append("\n");
                detail.append("Boss Damage: x").append(String.format("%.3f", scaling.bossDamageMultiplier)).append("\n");
                detail.append("Boss Accuracy: x").append(String.format("%.3f", scaling.bossAccuracyMultiplier)).append("\n");
                detail.append("Scaling Type: ").append(scaling.scalingType).append("\n");
            }

            // Equipment analysis
            detail.append("\n=== EQUIPMENT ANALYSIS ===\n");
            Map<String, Integer> equippedItems = getNPCEquippedItems(npc);
            if (equippedItems != null && !equippedItems.isEmpty()) {
                for (Map.Entry<String, Integer> entry : equippedItems.entrySet()) {
                    ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(entry.getValue());
                    String itemName = (itemDef != null) ? itemDef.getName() : "Unknown";
                    detail.append(entry.getKey()).append(": ").append(itemName).append(" (").append(entry.getValue()).append(")\n");
                }
            } else {
                detail.append("No equipment detected\n");
            }

            return detail.toString();

        } catch (Exception e) {
            return "Error generating detailed info: " + e.getMessage();
        }
    }

    /**
     * ENHANCED: Check boss configuration status for admins
     */
    public static String getBossConfigurationStatus(int npcId) {
        try {
            if (!isBossBalancerAvailable()) {
                return "BossBalancer not available";
            }

            NPCDefinitions def = NPCDefinitions.getNPCDefinitions(npcId);
            String npcName = (def != null && def.getName() != null) ? def.getName() : "Unknown";
            
            boolean isConfigured = BossBalancer.isBossConfigured(npcId);
            int tier = BossBalancer.getBossEffectiveTierById(npcId);
            
            StringBuilder status = new StringBuilder();
            status.append("NPC ").append(npcId).append(" (").append(npcName).append("): ");
            
            if (isConfigured) {
                status.append("Configured as ").append(BossBalancer.getBossTierName(tier));
            } else {
                status.append("Auto-calculated as ").append(BossBalancer.getBossTierName(tier));
                status.append(" (Use ;;adjustboss ").append(npcId).append(" <tier> <type> to configure)");
            }
            
            return status.toString();

        } catch (Exception e) {
            return "Error checking configuration: " + e.getMessage();
        }
    }

    /**
     * Check if BossBalancer is available - NO ERROR MESSAGES
     */
    private static boolean isBossBalancerAvailable() {
        try {
            Class.forName("com.rs.game.player.content.BossBalancer");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Initialize the examine system
     */
    public static final void init() {
        try {
            if (new File(PACKED_PATH).exists()) {
                loadPackedNPCExamines();
            } else {
                loadUnpackedNPCExamines();
            }
            Logger.log("NPCExamines", "Enhanced NPC Examine System (BossBalancer v4.2 EQUIPMENT + PRAYER INTEGRATION) loaded");
        } catch (Exception e) {
            Logger.handle(e);
        }
    }

    /**
     * Load packed examines (Java 1.7 compatible)
     */
    private static void loadPackedNPCExamines() {
        RandomAccessFile in = null;
        FileChannel channel = null;
        try {
            in = new RandomAccessFile(PACKED_PATH, "r");
            channel = in.getChannel();
            ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());

            while (buffer.hasRemaining()) {
                int npcId = buffer.getShort() & 0xffff;
                String examine = readAlexString(buffer);
                
                // PACKET SAFETY: Limit examine length
                if (examine.length() > MAX_EXAMINE_LENGTH) {
                    examine = examine.substring(0, MAX_EXAMINE_LENGTH - 3) + "...";
                }
                
                npcExamines.put(Integer.valueOf(npcId), examine);
            }

        } catch (Exception e) {
            Logger.handle(e);
        } finally {
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
     * Load unpacked examines (Java 1.7 compatible) with packet safety
     */
    private static void loadUnpackedNPCExamines() {
        Logger.log("NPCExamines", "Packing NPC examines...");

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

                line = line.replace("ï»¿", ""); // Remove BOM
                String[] splitedLine = line.split(" - ", 2);

                if (splitedLine.length < 2) {
                    continue;
                }

                try {
                    int npcId = Integer.parseInt(splitedLine[0]);
                    String examine = splitedLine[1];

                    // PACKET SAFETY: Limit examine length during loading
                    if (examine.length() > MAX_EXAMINE_LENGTH) {
                        examine = examine.substring(0, MAX_EXAMINE_LENGTH - 3) + "...";
                    }

                    out.writeShort(npcId);
                    writeAlexString(out, examine);
                    npcExamines.put(Integer.valueOf(npcId), examine);

                } catch (NumberFormatException e) {
                    // Skip invalid lines
                }
            }

        } catch (Exception e) {
            Logger.handle(e);
        } finally {
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
     * Write string to DataOutputStream with length validation
     */
    public static void writeAlexString(DataOutputStream out, String string) throws IOException {
        // PACKET SAFETY: Ensure string isn't too long
        if (string.length() > 255) {
            string = string.substring(0, 252) + "...";
        }
        
        byte[] bytes = string.getBytes();
        out.writeByte(bytes.length);
        out.write(bytes);
    }

    /**
     * Add custom examine with packet safety
     */
    public static void addCustomExamine(int npcId, String examine) {
        if (examine != null && !examine.trim().isEmpty()) {
            // PACKET SAFETY: Limit length
            if (examine.length() > MAX_EXAMINE_LENGTH) {
                examine = examine.substring(0, MAX_EXAMINE_LENGTH - 3) + "...";
            }
            npcExamines.put(Integer.valueOf(npcId), examine);
        }
    }

    /**
     * Remove custom examine for specific NPC ID
     */
    public static void removeCustomExamine(int npcId) {
        npcExamines.remove(Integer.valueOf(npcId));
    }

    /**
     * Get current examine count for monitoring
     */
    public static int getExamineCount() {
        return npcExamines.size();
    }

    /**
     * Clear all examines (for reloading)
     */
    public static void clearAllExamines() {
        npcExamines.clear();
    }

    /**
     * ENHANCED: Admin command to examine NPC with full BossBalancer details
     */
    public static void handleNPCExamineCommand(Player player, String[] cmd) {
        if (player == null || !player.isHeadStaff()) {
            return;
        }

        try {
            if (cmd.length < 2) {
                player.sendMessage("Usage: ;;npcexamine <npcId>");
                return;
            }

            int npcId = Integer.parseInt(cmd[1]);
            
            // Find NPC around player using compatible method
            NPC targetNpc = findNearbyNPC(player, npcId);

            if (targetNpc == null) {
                // Show configuration status even without NPC instance
                String configStatus = getBossConfigurationStatus(npcId);
                player.sendMessage("NPC " + npcId + " not found nearby.");
                player.sendMessage(configStatus);
                return;
            }

            // Send detailed analysis
            String detailedInfo = getDetailedBossBalancerInfo(targetNpc, player);
            String[] lines = detailedInfo.split("\n");
            
            for (String line : lines) {
                if (!line.trim().isEmpty()) {
                    player.sendMessage(line);
                }
            }

        } catch (NumberFormatException e) {
            player.sendMessage("Invalid NPC ID. Use: ;;npcexamine <npcId>");
        } catch (Exception e) {
            player.sendMessage("Error examining NPC: " + e.getMessage());
        }
    }

    /**
     * Find NPC near player using compatible methods
     */
    private static NPC findNearbyNPC(Player player, int npcId) {
        try {
            // Method 1: Try using World.getNPCs() if available
            try {
                for (NPC npc : com.rs.game.World.getNPCs()) {
                    if (npc != null && npc.getId() == npcId) {
                        // Check if NPC is reasonably close to player (within 10 tiles)
                        if (isNearPlayer(player, npc, 10)) {
                            return npc;
                        }
                    }
                }
            } catch (Exception e) {
                // World.getNPCs() might not be available, try other methods
            }

            // Method 2: Try alternative approaches if World.getNPCs() doesn't work
            // You can customize this based on your server's available methods
            
        } catch (Exception e) {
            // Ignore errors in NPC finding
        }
        
        return null;
    }

    /**
     * Check if NPC is near player (distance check)
     */
    private static boolean isNearPlayer(Player player, NPC npc, int maxDistance) {
        try {
            if (player == null || npc == null) {
                return false;
            }

            // Check if on same plane
            if (player.getPlane() != npc.getPlane()) {
                return false;
            }

            // Calculate distance
            int deltaX = Math.abs(player.getX() - npc.getX());
            int deltaY = Math.abs(player.getY() - npc.getY());
            int distance = Math.max(deltaX, deltaY);

            return distance <= maxDistance;

        } catch (Exception e) {
            return false;
        }
    }
}