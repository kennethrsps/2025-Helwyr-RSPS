package com.rs.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import com.rs.Settings;
import com.rs.cache.Cache;

public final class ItemBonuses {

	private final static String PACKED_PATH = "data/items/bonuses.ib";
	private static final int[] DEFAULT = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	public static HashMap<Integer, int[]> itemBonuses;

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		Cache.init();
		File file = new File(PACKED_PATH);
		try {
			itemBonuses = (HashMap<Integer, int[]>) loadFile(file);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 50000; i++) {
			int[] bonuses = itemBonuses.get(i) == null ? DEFAULT : itemBonuses.get(i);
			writeBonuses(i, bonuses);
		}
		System.out.println("Done");
	}

	private ItemBonuses() {
	}

	/**
	 * FIXED: Never return null bonuses
	 */
	public static final int[] getItemBonuses(int itemId) {
		int[] bonuses = itemBonuses.get(itemId);
		return bonuses != null ? bonuses : DEFAULT.clone();
	}

	@SuppressWarnings("unchecked")
	public static final void init() {
		File file = new File(PACKED_PATH);
		if (file.exists()) {
			try {
				itemBonuses = (HashMap<Integer, int[]>) loadFile(file);
				return;
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		itemBonuses = new HashMap<Integer, int[]>();
		loadBonuses();
	}

	public static final Object loadFile(File f) throws IOException, ClassNotFoundException {
		if (!f.exists())
			return null;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		Object object = in.readObject();
		in.close();
		return object;
	}

	public static void writeBonuses(int itemId, int[] bonuses) {
		File file = new File("./data/items/bonuses/" + itemId + ".txt");
		if (file.exists()) {
			file.delete();
		}
		try {
			PrintWriter out = new PrintWriter(
					new BufferedWriter(new FileWriter("./data/items/bonuses/" + itemId + ".txt", true)));
			out.println(bonuses[0]);
			out.println(bonuses[1]);
			out.println(bonuses[2]);
			out.println(bonuses[3]);
			out.println(bonuses[4]);
			out.println("");
			out.println(bonuses[5]);
			out.println(bonuses[6]);
			out.println(bonuses[7]);
			out.println(bonuses[8]);
			out.println(bonuses[9]);
			out.println(bonuses[10]);
			out.println("");
			out.println(bonuses[11]);
			out.println(bonuses[12]);
			out.println(bonuses[13]);
			out.println("");
			out.println(bonuses[14]);
			out.println(bonuses[15]);
			out.println(bonuses[16]);
			out.println(bonuses[17]);
			out.close();
		} catch (IOException e) {
			System.err.println(e);
		}
	}

	/**
	 * ENHANCED: Flexible loadBonuses method that handles multiple file formats
	 */
	public static final void loadBonuses() {
		int successCount = 0;
		int errorCount = 0;
		
		try {
			File file = null;
			BufferedReader reader = null;

			for (int itemId = 0; itemId < Utils.getItemDefinitionsSize(); itemId++) {
				file = new File("data/items/bonuses/" + itemId + ".txt");
				if (!file.exists()) {
					continue;
				}
				
				try {
					reader = new BufferedReader(new FileReader(file));
					
					// Try to parse the file flexibly
					int[] bonuses = parseFlexibleBonusFile(reader, itemId);
					if (bonuses != null) {
						itemBonuses.put(itemId, bonuses);
						successCount++;
					} else {
						errorCount++;
					}
					
					reader.close();
					
				} catch (Exception e) {
					errorCount++;
					System.err.println("Error loading bonuses for item " + itemId + ": " + e.getMessage());
					if (reader != null) {
						try { reader.close(); } catch (IOException ex) {}
					}
				}
			}
			
			System.out.println("Loaded bonuses - Success: " + successCount + ", Errors: " + errorCount);
			
			if (successCount > 0) {
				save();
				System.out.println("Done Packing item bonuses...");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Flexible parser that handles multiple file formats
	 */
	private static int[] parseFlexibleBonusFile(BufferedReader reader, int itemId) throws IOException {
		try {
			List<String> allLines = new ArrayList<>();
			String line;
			
			// Read all lines first
			while ((line = reader.readLine()) != null) {
				allLines.add(line.trim());
			}
			
			// Extract only the numbers from the file
			List<Integer> numbers = new ArrayList<>();
			for (String currentLine : allLines) {
				if (isValidNumber(currentLine)) {
					numbers.add(Integer.parseInt(currentLine));
				}
			}
			
			// Check if we have exactly 18 numbers (the bonuses we need)
			if (numbers.size() == 18) {
				// Convert to array
				int[] bonuses = new int[18];
				for (int i = 0; i < 18; i++) {
					bonuses[i] = numbers.get(i);
				}
				return bonuses;
			}
			
			// If not exactly 18 numbers, try the original strict format
			return parseOriginalFormat(allLines, itemId);
			
		} catch (Exception e) {
			System.err.println("Failed to parse bonus file for item " + itemId + ": " + e.getMessage());
			return null;
		}
	}

	/**
	 * Parse using the original strict format (for files that follow the exact structure)
	 */
	private static int[] parseOriginalFormat(List<String> lines, int itemId) {
		try {
			if (lines.size() < 22) {
				return null; // Not enough lines for original format
			}
			
			int lineIndex = 0;
			int[] bonuses = new int[18];
			int bonusIndex = 0;
			
			// Skip first line if it's not a number (comment/header)
			if (lineIndex < lines.size() && !isValidNumber(lines.get(lineIndex))) {
				lineIndex++;
			}
			
			// Attack bonuses (5 values)
			for (int i = 0; i < 5; i++) {
				if (lineIndex >= lines.size()) return null;
				String line = lines.get(lineIndex++);
				if (!isValidNumber(line)) return null;
				bonuses[bonusIndex++] = Integer.parseInt(line);
			}
			
			// Skip separator line
			if (lineIndex < lines.size() && lines.get(lineIndex).isEmpty()) {
				lineIndex++; 
			}
			
			// Defense bonuses (6 values)
			for (int i = 0; i < 6; i++) {
				if (lineIndex >= lines.size()) return null;
				String line = lines.get(lineIndex++);
				if (!isValidNumber(line)) return null;
				bonuses[bonusIndex++] = Integer.parseInt(line);
			}
			
			// Skip separator line
			if (lineIndex < lines.size() && lines.get(lineIndex).isEmpty()) {
				lineIndex++;
			}
			
			// Absorption bonuses (3 values)
			for (int i = 0; i < 3; i++) {
				if (lineIndex >= lines.size()) return null;
				String line = lines.get(lineIndex++);
				if (!isValidNumber(line)) return null;
				bonuses[bonusIndex++] = Integer.parseInt(line);
			}
			
			// Skip separator line
			if (lineIndex < lines.size() && lines.get(lineIndex).isEmpty()) {
				lineIndex++;
			}
			
			// Other bonuses (4 values)
			for (int i = 0; i < 4; i++) {
				if (lineIndex >= lines.size()) return null;
				String line = lines.get(lineIndex++);
				if (!isValidNumber(line)) return null;
				bonuses[bonusIndex++] = Integer.parseInt(line);
			}
			
			return bonuses;
			
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Check if string is a valid number
	 */
	private static boolean isValidNumber(String str) {
		if (str == null || str.trim().isEmpty()) {
			return false;
		}
		try {
			Integer.parseInt(str.trim());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	private static final void loadItemBonuses() {
		try {
			RandomAccessFile in = new RandomAccessFile(PACKED_PATH, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			itemBonuses = new HashMap<Integer, int[]>(buffer.remaining() / 38);
			while (buffer.hasRemaining()) {
				int itemId = buffer.getShort() & 0xffff;
				int[] bonuses = new int[18];
				for (int index = 0; index < bonuses.length; index++)
					bonuses[index] = buffer.getShort();
				itemBonuses.put(itemId, bonuses);
				// writeBonuses(itemId,bonuses); // ENABLE MO LANG TO PRE TAZ RESTART PARA MAG
				// REFRESH DUN SA BONUSES FOLDER
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	/**
	 * FIXED: Safe setBonus method that handles null properly
	 */
	public static void setBonus(int id, int slot, int value) {
		int[] bonuses = itemBonuses.get(id);
		if (bonuses == null) {
			bonuses = DEFAULT.clone();
		} else {
			bonuses = bonuses.clone();
		}
		
		if (slot < 0 || slot >= bonuses.length) {
			System.err.println("Invalid bonus slot: " + slot + " for item " + id);
			return;
		}
		
		bonuses[slot] = value;
		itemBonuses.put(id, bonuses);
		save();
	}

	/**
	 * NEW: Set all bonuses at once (more efficient for Item Balancer)
	 */
	public static void setBonuses(int id, int[] newBonuses) {
		if (newBonuses == null || newBonuses.length != 18) {
			System.err.println("Invalid bonuses array for item " + id);
			return;
		}
		
		itemBonuses.put(id, newBonuses.clone());
	}

	/**
	 * NEW: Manual save method (for controlled saving)
	 */
	public static void saveManually() {
		try {
			storeFile(itemBonuses, new File(PACKED_PATH));
			System.out.println("Item bonuses saved successfully.");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static final void save() {
		try {
			storeFile(itemBonuses, new File(PACKED_PATH));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static final void storeFile(Serializable o, File f) throws IOException {
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(o);
		out.close();
	}

	/**
	 * DEBUG METHOD: Check what's in memory
	 */
	public static void debugBonuses() {
		System.out.println("=== ITEM BONUSES DEBUG ===");
		System.out.println("HashMap size: " + (itemBonuses != null ? itemBonuses.size() : "NULL"));
		
		if (itemBonuses != null && itemBonuses.size() > 0) {
			System.out.println("Sample items with bonuses:");
			int count = 0;
			for (Integer itemId : itemBonuses.keySet()) {
				if (count >= 10) break; // Show first 10
				int[] bonuses = itemBonuses.get(itemId);
				System.out.println("Item " + itemId + ": " + java.util.Arrays.toString(bonuses));
				count++;
			}
		} else {
			System.out.println("NO BONUSES LOADED!");
		}
		
		// Test specific common items
		System.out.println("=== TESTING COMMON ITEMS ===");
		testItemBonuses(4151); // Abyssal whip
		testItemBonuses(1215); // Dragon dagger
		testItemBonuses(1127); // Rune platebody
		testItemBonuses(1201); // Rune kiteshield
	}

	/**
	 * DEBUG METHOD: Test specific item bonuses
	 */
	private static void testItemBonuses(int itemId) {
		int[] bonuses = getItemBonuses(itemId);
		boolean hasNonZero = false;
		for (int bonus : bonuses) {
			if (bonus != 0) {
				hasNonZero = true;
				break;
			}
		}
		System.out.println("Item " + itemId + ": " + (hasNonZero ? "HAS BONUSES" : "ALL ZEROS") + 
						  " - " + java.util.Arrays.toString(bonuses));
	}

	/**
	 * EMERGENCY METHOD: Force load bonuses from files
	 */
	public static void forceLoadBonuses() {
		System.out.println("=== FORCE LOADING BONUSES ===");
		
		if (itemBonuses == null) {
			itemBonuses = new HashMap<Integer, int[]>();
		}
		
		// Clear existing bonuses
		itemBonuses.clear();
		
		// Load from files
		loadBonuses();
		
		System.out.println("Force load complete. Loaded " + itemBonuses.size() + " items with bonuses.");
	}

	public static int getItemAttackSpeed(int itemId) {
		int attackspeed = 4;
		if (!new File("data/items/AspdBonusesDump.txt").exists())
			throw new RuntimeException("Couldn't find attackspeed data");
		try {
			BufferedReader in = new BufferedReader(new FileReader("data/items/AspdBonusesDump.txt"));
			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				if (line.startsWith("//"))
					continue;
				String[] split = line.split("-");
				int id = Integer.parseInt(split[0]);
				// System.out.println("id="+id);
				int aspd = Integer.parseInt(split[1]);
				// System.out.println("aspd="+aspd);
				if (id == itemId) {
					attackspeed = aspd;
					// System.out.println("attackspeed="+attackspeed);
				}
			}
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return attackspeed;
	}
}