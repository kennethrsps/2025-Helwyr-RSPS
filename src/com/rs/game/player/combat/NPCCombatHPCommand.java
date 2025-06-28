/*
 * package com.rs.game.player.combat;
 * 
 * import java.io.*; import java.nio.file.Files; import java.nio.file.Paths;
 * import java.util.HashMap; import java.util.Map; import
 * java.util.concurrent.ConcurrentHashMap;
 * 
 * import com.rs.cache.loaders.NPCDefinitions; import com.rs.game.World; import
 * com.rs.game.npc.NPC; import com.rs.game.player.Player; import
 * com.rs.utils.Logger; import com.rs.utils.Utils;
 * 
 *//**
	 * Complete NPC HP management system with proper max HP handling
	 */
/*
 * public class NPCCombatHPCommand {
 * 
 * // File paths for saving modifications private static final String
 * HP_MODIFICATIONS_FILE = "data/npcs/hp_modifications.ser"; private static
 * final String MODIFICATIONS_LOG = "data/npcs/hp_changes.log";
 * 
 * // Store HP modifications that override default HP private static final
 * Map<Integer, Integer> hpOverrides = new ConcurrentHashMap<>();
 * 
 * static { loadHPModifications(); }
 * 
 *//**
	 * Set HP modification for an NPC
	 */
/*
 * public static void setHPModification(int npcId, int newHP) { if (newHP <= 0)
 * { return; // Prevent invalid HP values } hpOverrides.put(npcId, newHP);
 * saveHPModifications(); logHPChange(npcId, newHP); }
 * 
 *//**
	 * Remove HP modification for an NPC
	 */
/*
 * public static void removeHPModification(int npcId) {
 * hpOverrides.remove(npcId); saveHPModifications(); }
 * 
 *//**
	 * Get modified HP for an NPC (with overrides applied)
	 */
/*
 * public static int getModifiedHP(int npcId) { return
 * hpOverrides.getOrDefault(npcId, getDefaultHP(npcId)); }
 * 
 *//**
	 * Get modified max HP for an NPC (same as current for modified NPCs)
	 */
/*
 * public static int getModifiedMaxHP(int npcId) { if (hasHPModification(npcId))
 * { return hpOverrides.get(npcId); } return getDefaultHP(npcId); }
 * 
 *//**
	 * Get default HP for an NPC based on combat level and hardcoded values
	 */
/*
 * private static int getDefaultHP(int npcId) { // Get NPC definitions
 * NPCDefinitions npcDef = NPCDefinitions.getNPCDefinitions(npcId); if (npcDef
 * != null) { // Use combat level to estimate HP if available if
 * (npcDef.combatLevel > 0) { // Simple calculation: combat level * 10 (you can
 * adjust this formula) return Math.max(1, npcDef.combatLevel * 10); }
 * 
 * // Hardcoded HP values for specific NPCs (bosses and important monsters)
 * switch (npcId) { // Major Bosses case 2745: return 10000; // TzTok-Jad case
 * 50: return 750; // King Black Dragon case 3200: return 305; // Chaos
 * Elemental case 2883: return 255; // Dagannoth Rex case 2882: return 255; //
 * Dagannoth Prime case 2881: return 255; // Dagannoth Supreme case 6222: return
 * 510; // Kree'arra case 6203: return 650; // K'ril Tsutsaroth case 6260:
 * return 580; // Graardor case 6247: return 550; // Zilyana case 8133: return
 * 2000; // Corporeal Beast case 3334: return 1500; // Kalphite Queen case 1160:
 * return 1500; // Kalphite Queen (form 2)
 * 
 * // Slayer Monsters case 1615: return 105; // Abyssal Demon case 1624: return
 * 52; // Bloodveld case 1613: return 50; // Nechryael case 1648: return 90; //
 * Gargoyle case 1610: return 77; // Dark Beast case 1604: return 35; // Cave
 * Crawler case 1627: return 58; // Dust Devil case 1633: return 75; // Jellies
 * case 1608: return 65; // Basilisk
 * 
 * // Dragons case 51: return 188; // Green Dragon case 52: return 188; // Blue
 * Dragon case 53: return 188; // Red Dragon case 54: return 188; // Black
 * Dragon case 55: return 300; // Baby Dragon case 941: return 100; // Green
 * Dragon (baby)
 * 
 * // Common Monsters case 1265: return 12; // Rock Crab case 1266: return 12;
 * // Rock Crab case 1267: return 12; // Rock Crab case 41: return 30; //
 * Chicken - Fixed default HP case 81: return 15; // Cow case 2693: return 25;
 * // Giant Rat case 1338: return 35; // Goblin case 9: return 22; // Man case
 * 1: return 22; // Woman
 * 
 * // Add more as needed... default: // Fallback based on NPC name if we
 * recognize patterns if (npcDef.getName() != null) { String name =
 * npcDef.getName().toLowerCase(); if (name.contains("dragon")) return 150; if
 * (name.contains("demon")) return 80; if (name.contains("giant")) return 60; if
 * (name.contains("goblin")) return 25; if (name.contains("skeleton")) return
 * 30; if (name.contains("zombie")) return 35; if (name.contains("rat")) return
 * 15; if (name.contains("spider")) return 20; if (name.contains("wolf")) return
 * 25; if (name.contains("bear")) return 40; } return 100; // Default HP for
 * unknown NPCs } } return 1; // Absolute fallback }
 * 
 *//**
	 * Check if an NPC has HP modifications
	 */
/*
 * public static boolean hasHPModification(int npcId) { return
 * hpOverrides.containsKey(npcId); }
 * 
 *//**
	 * Apply HP modification when NPC spawns - PROPER VERSION This sets both current
	 * and max HP correctly
	 */
/*
 * public static void applyHPModification(NPC npc) { if (npc != null &&
 * hpOverrides.containsKey(npc.getId())) { int modifiedHP =
 * hpOverrides.get(npc.getId());
 * 
 * // Try different methods to set max HP based on your NPC class try { //
 * Option 1: Try setMaxHitpoints if it exists
 * npc.getClass().getMethod("setMaxHitpoints", int.class).invoke(npc,
 * modifiedHP); } catch (Exception e1) { try { // Option 2: Try setMaxHP if it
 * exists npc.getClass().getMethod("setMaxHP", int.class).invoke(npc,
 * modifiedHP); } catch (Exception e2) { // Option 3: Try accessing maxHitpoints
 * field directly try { java.lang.reflect.Field maxHpField =
 * npc.getClass().getDeclaredField("maxHitpoints");
 * maxHpField.setAccessible(true); maxHpField.set(npc, modifiedHP); } catch
 * (Exception e3) { try { // Option 4: Try maxHP field java.lang.reflect.Field
 * maxHpField = npc.getClass().getDeclaredField("maxHP");
 * maxHpField.setAccessible(true); maxHpField.set(npc, modifiedHP); } catch
 * (Exception e4) { // Fallback: Just set current HP (will show as
 * modified/original)
 * System.out.println("Warning: Could not set max HP for NPC " + npc.getId() +
 * ". Only current HP modified."); } } } }
 * 
 * // Always set current HP npc.setHitpoints(modifiedHP); } }
 * 
 *//**
	 * Alternative method for NPCs that are already spawned Call this to update
	 * existing NPCs with new HP values
	 */
/*
 * public static void updateExistingNPCs(int npcId) { if
 * (!hasHPModification(npcId)) { return; }
 * 
 * int modifiedHP = getModifiedHP(npcId); for (NPC npc : World.getNPCs()) { if
 * (npc != null && npc.getId() == npcId && !npc.isDead()) {
 * applyHPModification(npc); } } }
 * 
 *//**
	 * Send modifications list to player
	 */
/*
 * public static void sendModificationsList(Player player) { if
 * (hpOverrides.isEmpty()) { player.sendMessage("No HP modifications found.");
 * return; }
 * 
 * player.sendMessage("=== NPC HP Modifications ==="); int count = 0; for
 * (Map.Entry<Integer, Integer> entry : hpOverrides.entrySet()) { int npcId =
 * entry.getKey(); int newHP = entry.getValue(); int originalHP =
 * getDefaultHP(npcId);
 * 
 * NPCDefinitions npcDef = NPCDefinitions.getNPCDefinitions(npcId); String
 * npcName = npcDef != null ? npcDef.getName() : "Unknown";
 * 
 * String change = newHP > originalHP ? "(+" + (newHP - originalHP) + ")" : "("
 * + (newHP - originalHP) + ")"; player.sendMessage("ID: " + npcId + " | " +
 * npcName + " | " + formatNumber(originalHP) + " -> " + formatNumber(newHP) +
 * " " + change); count++;
 * 
 * if (count >= 15) { player.sendMessage("... and " + (hpOverrides.size() - 15)
 * + " more modifications"); break; } }
 * player.sendMessage("Total modifications: " + hpOverrides.size()); }
 * 
 *//**
	 * Export modifications to text file
	 */
/*
 * public static String exportModifications() throws IOException { String
 * filename = "data/npcs/hp_export_" + System.currentTimeMillis() + ".txt";
 * 
 * // Ensure directory exists Files.createDirectories(Paths.get("data/npcs"));
 * 
 * try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
 * writer.println("NPC HP Modifications Export"); writer.println("Generated: " +
 * new java.util.Date()); writer.println("Total Modifications: " +
 * hpOverrides.size());
 * writer.println("==================================================");
 * writer.println();
 * 
 * for (Map.Entry<Integer, Integer> entry : hpOverrides.entrySet()) { int npcId
 * = entry.getKey(); int newHP = entry.getValue(); int originalHP =
 * getDefaultHP(npcId);
 * 
 * NPCDefinitions npcDef = NPCDefinitions.getNPCDefinitions(npcId); String
 * npcName = npcDef != null ? npcDef.getName() : "Unknown";
 * 
 * writer.println("NPC ID: " + npcId); writer.println("Name: " + npcName);
 * writer.println("Combat Level: " + (npcDef != null ? npcDef.combatLevel :
 * "Unknown")); writer.println("Original HP: " + originalHP);
 * writer.println("Modified HP: " + newHP); writer.println("Change: " + (newHP >
 * originalHP ? "+" : "") + (newHP - originalHP));
 * writer.println("------------------------------"); } }
 * 
 * return filename; }
 * 
 *//**
	 * Log HP change to file
	 */
/*
 * private static void logHPChange(int npcId, int newHP) { try { // Ensure
 * directory exists Files.createDirectories(Paths.get("data/npcs"));
 * 
 * NPCDefinitions npcDef = NPCDefinitions.getNPCDefinitions(npcId); String
 * npcName = npcDef != null ? npcDef.getName() : "Unknown"; int originalHP =
 * getDefaultHP(npcId);
 * 
 * try (PrintWriter writer = new PrintWriter(new FileWriter(MODIFICATIONS_LOG,
 * true))) { writer.println(new java.util.Date() + " | NPC: " + npcId + " (" +
 * npcName + ") | HP: " + originalHP + " -> " + newHP); } } catch (IOException
 * e) { Logger.log("NPCHPCommand", "Error logging HP change: " +
 * e.getMessage()); } }
 * 
 *//**
	 * Save HP modifications to file
	 */
/*
 * private static void saveHPModifications() { try { // Ensure directory exists
 * Files.createDirectories(Paths.get("data/npcs"));
 * 
 * try (ObjectOutputStream oos = new ObjectOutputStream(new
 * FileOutputStream(HP_MODIFICATIONS_FILE))) { oos.writeObject(new
 * HashMap<>(hpOverrides)); }
 * 
 * } catch (Exception e) { Logger.log("NPCHPCommand",
 * "Failed to save HP modifications: " + e.getMessage()); } }
 * 
 *//**
	 * Load HP modifications from file
	 */
/*
 * @SuppressWarnings("unchecked") private static void loadHPModifications() {
 * try { if (Files.exists(Paths.get(HP_MODIFICATIONS_FILE))) { try
 * (ObjectInputStream ois = new ObjectInputStream(new
 * FileInputStream(HP_MODIFICATIONS_FILE))) { Map<Integer, Integer> loaded =
 * (Map<Integer, Integer>) ois.readObject(); hpOverrides.putAll(loaded);
 * System.out.println("Loaded " + hpOverrides.size() + " NPC HP modifications");
 * } } } catch (Exception e) { Logger.log("NPCHPCommand",
 * "Failed to load HP modifications: " + e.getMessage()); } }
 * 
 *//**
	 * Format numbers (fallback if Utils.getFormattedNumber doesn't exist)
	 */
/*
 * private static String formatNumber(int number) { try { return
 * Utils.getFormattedNumber(number); } catch (Exception e) { return
 * String.valueOf(number); } }
 * 
 *//**
	 * Get all current HP modifications (for debugging or admin tools)
	 */
/*
 * public static Map<Integer, Integer> getAllModifications() { return new
 * HashMap<>(hpOverrides); }
 * 
 *//**
	 * Clear all HP modifications
	 */
/*
 * public static void clearAllModifications() { hpOverrides.clear();
 * saveHPModifications(); }
 * 
 *//**
	 * Validate and fix an NPC's HP display
	 *//*
		 * public static void validateNPCHP(NPC npc) { if (npc != null &&
		 * hasHPModification(npc.getId())) { applyHPModification(npc); } } }
		 */