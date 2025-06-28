package com.rs.game.player;

import java.io.Serializable;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.npc.NPC;
import com.rs.game.player.content.BossBalancer;
import com.rs.utils.Utils;

/**
 * Prayer system implementation for RuneScape game server
 * Handles both normal prayers and ancient curses
 * 
 * @author Zeus
 * @since June 07, 2025
 */
public class Prayer implements Serializable {

	private static final long serialVersionUID = -2082861520556582824L;

	// Prayer level requirements for each prayer book
	private final static int[][] PRAYER_LEVELS = {
			// normal prayer book
			{ 1, 4, 7, 8, 9, 10, 13, 16, 19, 22, 25, 26, 27, 28, 31, 34, 35, 37, 40, 43, 44, 45, 46, 49, 52, 60, 65, 70,
					74, 77 },
			// ancient prayer book  
			{ 50, 50, 52, 54, 56, 59, 62, 65, 68, 71, 74, 76, 78, 80, 82, 84, 86, 89, 92, 95, 95, 95, 99, 99, 99 } };

	// Prayer groups that close each other when activated
	private final static int[][][] CLOSE_PRAYERS = { { // normal prayer book
			{ 0, 5, 13 }, // Skin prayers 0
			{ 1, 6, 14 }, // Strength prayers 1
			{ 2, 7, 15 }, // Attack prayers 2
			{ 3, 11, 21, 28 }, // Range prayers 3
			{ 4, 12, 20, 29 }, // Magic prayers 4
			{ 8, 9, 26 }, // Restore prayers 5
			{ 10 }, // Protect item prayers 6
			{ 17, 18, 19 }, // Protect prayers 7
			{ 16 }, // Other protect prayers 8
			{ 22, 23, 24 }, // Other special prayers 9
			{ 25, 27 } // Other prayers 10
			}, { // ancient prayer book
					{ 0 }, // Protect item prayers 0
					{ 1, 2, 3, 4 }, // sap prayers 1
					{ 5 }, // other prayers 2
					{ 7, 8, 9, 17, 18 }, // protect prayers 3
					{ 6 }, // other protect prayers 4
					{ 10, 11, 12, 13, 14, 15, 16 }, // leech prayers 5
					{ 19, 20, 21, 22, 23, 24 }, // other prayers
			} };

	// Prayer slot values for config calculations
	private final static int[] PRAYER_SLOT_VALUES = { 1, 2, 4, 262144, 524288, 8, 16, 32, 64, 128, 256, 1048576, 2097152,
			512, 1024, 2048, 16777216, 4096, 8192, 16384, 4194304, 8388608, 32768, 65536, 131072, 33554432, 134217728,
			67108864, 268435456 * 2, 268435456, 268435456 * 4, 268435456 * 6, 268435456 * 8, 268435456 * 10,
			268435456 * 12 };

	// Prayer drain rates per second for each prayer
	private final static double[][] PRAYER_DRAIN_RATE = {
			{ 1.2, 1.2, 1.2, 1.2, 1.2, 0.6, 0.6, 0.6, 3.6, 1.8, 1.8, 0.6, 0.6, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3,
					0.3, 1.2, 0.6, 0.18, 0.18, 0.24, 0.15, 0.2, 0.18 },
			{ 1.8, 0.24, 0.24, 0.24, 0.24, 1.8, 0.3, 0.3, 0.3, 0.3, 0.36, 0.36, 0.36, 0.36, 0.36, 0.36, 0.36, 1.2, 0.2,
					0.2, 0.2, 0.2, 0.15, 0.15, 0.15 } };

	// Constants for array indices and limits
	private static final int NORMAL_PRAYERS_COUNT = 30;
	private static final int ANCIENT_PRAYERS_COUNT = 25;
	private static final int MAX_LEECH_BONUS = 25;
	private static final int LEECH_BONUSES_COUNT = 11;

	// Transient fields (not serialized)
	private transient Player player;
	private transient boolean[][] onPrayers;
	private transient boolean usingQuickPrayer;
	private transient int onPrayersCount;
	private transient long[] nextDrain;
	private transient boolean boostedLeech;
	
	// Serialized fields
	private boolean[][] quickPrayers;
	private int prayerpoints;
	public transient int[] leechBonuses;
	private boolean ancientcurses;

	public Prayer() {
		quickPrayers = new boolean[2][];
		quickPrayers[0] = new boolean[NORMAL_PRAYERS_COUNT];
		quickPrayers[1] = new boolean[ANCIENT_PRAYERS_COUNT];
		prayerpoints = 10;
	}

	/**
	 * Adjusts stat display for leech bonuses
	 */
	public void adjustStat(int stat, int percentage) {
		if (player != null && player.getPackets() != null) {
			player.getPackets().sendConfigByFile(6857 + stat, 30 + percentage);
		}
	}

	/**
	 * Checks if player has prayer points remaining
	 */
	private boolean checkPrayer() {
		if (prayerpoints <= 0) {
			if (player != null && player.getPackets() != null) {
				player.getPackets().sendSound(2672, 0, 1);
				player.getPackets().sendGameMessage("Please recharge your prayer at the Lumbridge Church.");
			}
			return false;
		}
		return true;
	}

	/**
	 * Closes all active prayers
	 */
	public void closeAllPrayers() {
		if (onPrayers != null) {
			onPrayers[0] = new boolean[NORMAL_PRAYERS_COUNT];
			onPrayers[1] = new boolean[ANCIENT_PRAYERS_COUNT];
		}
		if (leechBonuses != null) {
			leechBonuses = new int[LEECH_BONUSES_COUNT];
		}
		onPrayersCount = 0;
		
		if (player != null && player.getPackets() != null) {
			player.getPackets().sendGlobalConfig(182, 0);
			player.getPackets().sendConfig(ancientcurses ? 1582 : 1395, 0);
		}
		
		if (player != null && player.getGlobalPlayerUpdater() != null) {
			player.getGlobalPlayerUpdater().generateAppearenceData();
		}
		
		resetStatAdjustments();
		
		if (player != null) {
			BossBalancer.onPrayerChanged(player);
		}
	}

	/**
	 * Handles specific prayer deactivation effects for ancient curses
	 */
	public void closePrayers(int prayerId) {
		if (!ancientcurses || leechBonuses == null) {
			return;
		}

		// Handle leech bonus resets for ancient curses
		switch (prayerId) {
			case 1:
				if (leechBonuses[0] > 0) {
					sendLeechMessage("Attack");
					resetLeechStats(new int[]{0, 1, 2}, 0);
				}
				break;
			case 2:
				if (leechBonuses[1] > 0) {
					sendLeechMessage("Range");
					resetLeechStats(new int[]{2, 4}, 1);
				}
				break;
			case 3:
				if (leechBonuses[2] > 0) {
					sendLeechMessage("Magic");
					resetLeechStats(new int[]{2, 5}, 2);
				}
				break;
			case 10:
				if (leechBonuses[3] > 0) {
					sendLeechMessage("Attack");
					resetLeechStats(new int[]{0}, 3);
				}
				break;
			case 11:
				if (leechBonuses[4] > 0) {
					sendLeechMessage("Ranged");
					resetLeechStats(new int[]{4}, 4);
				}
				break;
			case 12:
				if (leechBonuses[5] > 0) {
					sendLeechMessage("Magic");
					resetLeechStats(new int[]{5}, 5);
				}
				break;
			case 13:
				if (leechBonuses[6] > 0) {
					sendLeechMessage("Defence");
					resetLeechStats(new int[]{2}, 6);
				}
				break;
			case 14:
				if (leechBonuses[7] > 0) {
					sendLeechMessage("Strength");
					resetLeechStats(new int[]{1}, 7);
				}
				break;
			case 19:
			case 22:
				resetTurmoilStats(new int[]{8, 9, 10}, new int[]{0, 1, 2});
				break;
			case 20:
			case 23:
				resetTurmoilStats(new int[]{4, 9}, new int[]{2, 3});
				break;
			case 21:
			case 24:
				resetTurmoilStats(new int[]{5, 9}, new int[]{2, 4});
				break;
		}
	}

	/**
	 * Helper method to send leech curse messages
	 */
	private void sendLeechMessage(String skill) {
		if (player != null && player.getPackets() != null) {
			player.getPackets().sendGameMessage("Your " + skill + " is now unaffected by sap and leech curses.", true);
		}
	}

	/**
	 * Helper method to reset leech stats
	 */
	private void resetLeechStats(int[] stats, int bonusIndex) {
		for (int stat : stats) {
			adjustStat(stat, 0);
		}
		leechBonuses[bonusIndex] = 0;
	}

	/**
	 * Helper method to reset turmoil stats
	 */
	private void resetTurmoilStats(int[] bonusIndices, int[] stats) {
		for (int bonusIndex : bonusIndices) {
			leechBonuses[bonusIndex] = 0;
		}
		for (int stat : stats) {
			adjustStat(stat, 0);
		}
	}

	/**
	 * Closes multiple prayer groups
	 */
	private void closePrayers(int[]... prayers) {
		for (int[] prayer : prayers) {
			for (int prayerId : prayer) {
				if (usingQuickPrayer) {
					quickPrayers[getPrayerBook()][prayerId] = false;
				} else {
					if (onPrayers != null && onPrayers[getPrayerBook()][prayerId]) {
						onPrayersCount--;
					}
					if (onPrayers != null) {
						onPrayers[getPrayerBook()][prayerId] = false;
					}
					closePrayers(prayerId);
				}
			}
		}
	}

	/**
	 * Drains prayer points with various modifiers
	 */
	public void drainPrayer(int amount) {
		if (player == null) {
			return;
		}

		// Apply prayer betrayer perk reduction
		if (player.getPerkManager() != null && player.getPerkManager().prayerBetrayer) {
			if (Utils.random(6) < 3) {
				return;
			}
			if (amount > 2) {
				amount = amount / 2;
			}
		}

		// Apply prayer pet reduction
		if (player.getPetManager() != null && player.getPetManager().isPrayerPet()) {
			if (Utils.random(6) < 3) {
				return;
			}
			if (amount > 2) {
				amount = amount / 2;
			}
		}

		// Apply dungeoneering reduction
		if (player.getDungManager() != null && player.getDungManager().isInside()) {
			amount = (int) (amount * 0.9); // 10% reduction
		}

		prayerpoints = Math.max(0, prayerpoints - amount);
		refreshPrayerPoints();
	}

	/**
	 * Drains prayer to half (fixed redundancy)
	 */
	public void drainPrayerOnHalf() {
		if (player == null || (player.getPerkManager() != null && player.getPerkManager().prayerBetrayer)) {
			return;
		}
		
		if (prayerpoints > 0) {
			prayerpoints = prayerpoints / 2;
			refreshPrayerPoints();
		}
	}

	/**
	 * Calculates attack multiplier from active prayers
	 */
	public double getAttackMultiplier() {
		if (onPrayersCount == 0) {
			return 1.0;
		}
		
		double value = 1.0;
		
		// Normal prayers
		if (usingPrayer(0, 2)) {
			value += 0.05;
		} else if (usingPrayer(0, 7)) {
			value += 0.10;
		} else if (usingPrayer(0, 15)) {
			value += 0.15;
		} else if (usingPrayer(0, 25)) {
			value += 0.15;
		} else if (usingPrayer(0, 27)) {
			value += 0.20;
		}
		
		// Ancient curses
		if (leechBonuses != null) {
			if (usingPrayer(1, 1)) {
				value += leechBonuses[0] / 100.0;
			} else if (usingPrayer(1, 10)) {
				value += (5 + leechBonuses[3]) / 100.0;
			} else if (usingPrayer(1, 19)) {
				value += (15 + leechBonuses[8]) / 100.0;
			} else if (usingPrayer(1, 22)) {
				value += (17 + leechBonuses[8]) / 100.0;
			}
		}
		
		// Prayer pet bonus in wilderness
		if (player != null && player.getPetManager() != null && player.getPetManager().isPrayerPet() 
				&& player.isAtWild() && !player.isCanPvp() && player.getPet() != null) {
			int petLevel = player.getPet().getDetails().getLevel();
			value += petLevel * 0.02;
		}
		
		return value;
	}

	/**
	 * Calculates defence multiplier from active prayers
	 */
	public double getDefenceMultiplier() {
		if (onPrayersCount == 0) {
			return 1.0;
		}
		
		return calculateMultiplier(new int[]{0, 5, 13, 25, 27, 28, 29}, 
								 new double[]{0.05, 0.10, 0.15, 0.20, 0.25, 0.25, 0.25},
								 new int[]{1, 13, 19, 20, 21, 22, 23, 24},
								 new int[]{0, 6, 9, 9, 9, 9, 9, 9});
	}

	/**
	 * Calculates magic multiplier from active prayers
	 */
	public double getMageMultiplier() {
		if (onPrayersCount == 0) {
			return 1.0;
		}
		
		return calculateMultiplier(new int[]{4, 12, 21, 29}, 
								 new double[]{0.05, 0.10, 0.15, 0.20},
								 new int[]{3, 12, 21, 24},
								 new int[]{2, 5, 5, 5});
	}

	/**
	 * Calculates range multiplier from active prayers
	 */
	public double getRangeMultiplier() {
		if (onPrayersCount == 0) {
			return 1.0;
		}
		
		return calculateMultiplier(new int[]{3, 11, 20, 28}, 
								 new double[]{0.05, 0.10, 0.15, 0.20},
								 new int[]{2, 11, 20, 23},
								 new int[]{1, 4, 4, 4});
	}

	/**
	 * Calculates strength multiplier from active prayers
	 */
	public double getStrengthMultiplier() {
		if (onPrayersCount == 0) {
			return 1.0;
		}
		
		return calculateMultiplier(new int[]{1, 6, 14, 25, 27}, 
								 new double[]{0.05, 0.10, 0.15, 0.18, 0.23},
								 new int[]{1, 14, 19, 22},
								 new int[]{0, 7, 10, 10});
	}

	/**
	 * Helper method to calculate prayer multipliers (reduces code duplication)
	 */
	private double calculateMultiplier(int[] normalPrayers, double[] normalBonuses, 
									 int[] cursePrayers, int[] leechIndices) {
		double value = 1.0;
		
		// Check normal prayers
		for (int i = 0; i < normalPrayers.length; i++) {
			if (usingPrayer(0, normalPrayers[i])) {
				value += normalBonuses[i];
				return value; // Only one can be active at a time
			}
		}
		
		// Check ancient curses
		if (leechBonuses != null) {
			for (int i = 0; i < cursePrayers.length; i++) {
				if (usingPrayer(1, cursePrayers[i])) {
					int baseBonus = getBaseCurseBonus(cursePrayers[i]);
					value += (baseBonus + leechBonuses[leechIndices[i]]) / 100.0;
					return value; // Only one can be active at a time
				}
			}
		}
		
		return value;
	}

	/**
	 * Gets base bonus for curse prayers
	 */
	private int getBaseCurseBonus(int prayerId) {
		switch (prayerId) {
			case 1: case 2: case 3: return 0;
			case 10: case 11: case 12: case 13: case 14: return 5;
			case 19: case 20: case 21: return 15;
			case 22: case 23: case 24: return 17;
			default: return 0;
		}
	}

	public int getOnPrayersCount() {
		return onPrayersCount;
	}

	private int getPrayerBook() {
		return ancientcurses ? 1 : 0;
	}

	/**
	 * Gets prayer head icon for overhead display
	 */
	public int getPrayerHeadIcon() {
		if (onPrayersCount == 0) {
			return -1;
		}
		
		int value = -1;
		
		// Normal prayers
		if (usingPrayer(0, 16)) value += 8;
		if (usingPrayer(0, 17)) value += 3;
		else if (usingPrayer(0, 18)) value += 2;
		else if (usingPrayer(0, 19)) value += 1;
		else if (usingPrayer(0, 22)) value += 4;
		else if (usingPrayer(0, 23)) value += 6;
		else if (usingPrayer(0, 24)) value += 5;
		
		// Ancient curses
		else if (usingPrayer(1, 6)) {
			value += 16;
			if (usingPrayer(1, 8)) value += 2;
			else if (usingPrayer(1, 7)) value += 3;
			else if (usingPrayer(1, 9)) value += 1;
		} else if (usingPrayer(1, 7)) value += 14;
		else if (usingPrayer(1, 8)) value += 15;
		else if (usingPrayer(1, 9)) value += 13;
		else if (usingPrayer(1, 17)) value += 20;
		else if (usingPrayer(1, 18)) value += 21;
		
		return value;
	}

	public int getPrayerpoints() {
		return prayerpoints;
	}

	public boolean hasFullPrayerpoints() {
		return player != null && player.getSkills() != null && 
			   getPrayerpoints() >= player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
	}

	public boolean hasPrayersOn() {
		return onPrayersCount > 0;
	}

	/**
	 * Increases leech bonus and updates stats
	 */
	public void increaseLeechBonus(int bonus) {
		if (leechBonuses == null || bonus < 0 || bonus >= leechBonuses.length) {
			return;
		}
		
		leechBonuses[bonus]++;
		updateLeechStat(bonus);
	}

	/**
	 * Helper method to update leech stats
	 */
	private void updateLeechStat(int bonus) {
		switch (bonus) {
			case 0:
				adjustStat(0, leechBonuses[bonus]);
				adjustStat(1, leechBonuses[bonus]);
				adjustStat(2, leechBonuses[bonus]);
				break;
			case 1:
				adjustStat(2, leechBonuses[bonus]);
				adjustStat(3, leechBonuses[bonus]);
				break;
			case 2:
				adjustStat(2, leechBonuses[bonus]);
				adjustStat(4, leechBonuses[bonus]);
				break;
			case 3:
				adjustStat(0, leechBonuses[bonus]);
				break;
			case 4:
				adjustStat(3, leechBonuses[bonus]);
				break;
			case 5:
				adjustStat(4, leechBonuses[bonus]);
				break;
			case 6:
				adjustStat(2, leechBonuses[bonus]);
				break;
			case 7:
				adjustStat(1, leechBonuses[bonus]);
				break;
		}
	}

	/**
	 * Increases turmoil bonus against target entity
	 */
	public void increaseTurmoilBonus(Entity e, int type) {
		if (leechBonuses == null) {
			return;
		}

		if (e instanceof Player) {
			increaseTurmoilBonusPlayer((Player) e, type);
		} else if (e instanceof NPC) {
			increaseTurmoilBonusNPC((NPC) e, type);
		}
	}

	/**
	 * Helper method for turmoil bonus against players
	 */
	private void increaseTurmoilBonusPlayer(Player p2, int type) {
		if (p2.getSkills() == null) {
			return;
		}

		double multiplier = (type == 3 || type == 4 || type == 5) ? 0.17 : 0.15;
		
		switch (type) {
			case 0:
			case 3:
				calculatePlayerTurmoil(p2, multiplier, Skills.ATTACK, Skills.DEFENCE, Skills.STRENGTH, 
									 new int[]{8, 9, 10}, new int[]{0, 2, 1});
				break;
			case 1:
			case 4:
				calculatePlayerTurmoil(p2, multiplier, Skills.RANGE, Skills.DEFENCE, -1,
									 new int[]{4, 9}, new int[]{3, 2});
				break;
			case 2:
			case 5:
				calculatePlayerTurmoil(p2, multiplier, Skills.MAGIC, Skills.DEFENCE, -1,
									 new int[]{5, 9}, new int[]{4, 2});
				break;
		}
	}

	/**
	 * Helper method for turmoil calculations against players
	 */
	private void calculatePlayerTurmoil(Player p2, double multiplier, int skill1, int skill2, int skill3,
										int[] bonusIndices, int[] statIndices) {
		int level1 = p2.getSkills().getLevelForXp(skill1);
		int level2 = p2.getSkills().getLevelForXp(skill2);
		
		leechBonuses[bonusIndices[0]] = (int) ((100 * Math.floor(multiplier * level1)) / level1);
		leechBonuses[bonusIndices[1]] = (int) ((100 * Math.floor(multiplier * level2)) / level2);
		
		adjustStat(statIndices[0], leechBonuses[bonusIndices[0]]);
		adjustStat(statIndices[1], leechBonuses[bonusIndices[1]]);
		
		if (skill3 != -1 && bonusIndices.length > 2) {
			int level3 = p2.getSkills().getLevelForXp(skill3);
			double strMultiplier = (multiplier == 0.17) ? 0.125 : 0.1;
			leechBonuses[bonusIndices[2]] = (int) ((100 * Math.floor(strMultiplier * level3)) / level3);
			adjustStat(statIndices[2], leechBonuses[bonusIndices[2]]);
		}
	}

	/**
	 * Helper method for turmoil bonus against NPCs
	 */
	private void increaseTurmoilBonusNPC(NPC n, int type) {
		int[] bonuses = n.getBonuses();
		int defaultLevel = 80;
		
		double multiplier = (type == 3 || type == 4 || type == 5) ? 0.17 : 0.15;
		
		switch (type) {
			case 0:
			case 3:
				int atkLevel = bonuses == null ? defaultLevel : bonuses[0];
				int defLevel = bonuses == null ? defaultLevel : bonuses[5];
				int strLevel = bonuses == null ? defaultLevel : bonuses[2];
				
				leechBonuses[8] = calculateNPCBonus(atkLevel, multiplier);
				leechBonuses[9] = calculateNPCBonus(defLevel, multiplier);
				double strMultiplier = (type == 3) ? 0.125 : 0.1;
				leechBonuses[10] = calculateNPCBonus(strLevel, strMultiplier);
				
				adjustStat(0, leechBonuses[8]);
				adjustStat(1, leechBonuses[10]);
				adjustStat(2, leechBonuses[9]);
				break;
			case 1:
			case 4:
				int rangedLevel = bonuses == null ? defaultLevel : bonuses[4];
				defLevel = bonuses == null ? defaultLevel : bonuses[5];
				
				leechBonuses[4] = calculateNPCBonus(rangedLevel, multiplier);
				leechBonuses[9] = calculateNPCBonus(defLevel, multiplier);
				
				adjustStat(2, leechBonuses[9]);
				adjustStat(3, leechBonuses[4]);
				break;
			case 2:
			case 5:
				int mageLevel = bonuses == null ? defaultLevel : bonuses[3];
				defLevel = bonuses == null ? defaultLevel : bonuses[5];
				
				leechBonuses[5] = calculateNPCBonus(mageLevel, multiplier);
				leechBonuses[9] = calculateNPCBonus(defLevel, multiplier);
				
				adjustStat(2, leechBonuses[9]);
				adjustStat(4, leechBonuses[5]);
				break;
		}
	}

	/**
	 * Helper method to calculate NPC bonus
	 */
	private int calculateNPCBonus(int level, double multiplier) {
		return (int) ((100 * Math.floor(multiplier * level)) / level);
	}

	/**
	 * Initializes prayer system for player
	 */
	public void init() {
		if (player != null && player.getPackets() != null) {
			player.getPackets().sendGlobalConfig(181, usingQuickPrayer ? 1 : 0);
			player.getPackets().sendConfig(1584, ancientcurses ? 1 : 0);
		}
		resetStatAdjustments();
	}

	public boolean isAncientCurses() {
		return ancientcurses;
	}

	public boolean isBoostedLeech() {
		return boostedLeech;
	}

	public boolean isUsingQuickPrayer() {
		return usingQuickPrayer;
	}

	/**
	 * Processes prayer effects each game tick
	 */
	public void processPrayer() {
		if (!hasPrayersOn()) {
			return;
		}
		boostedLeech = false;
	}

	/**
	 * Processes prayer point drainage (fixed array bounds issue)
	 */
	public void processPrayerDrain() {
		if (!hasPrayersOn() || onPrayers == null || nextDrain == null) {
			return;
		}
		
		int prayerBook = getPrayerBook();
		long currentTime = Utils.currentTimeMillis();
		int drain = 0;
		int prayerPoints = 0;
		
		if (player != null && player.getCombatDefinitions() != null) {
			prayerPoints = player.getCombatDefinitions().getBonuses()[CombatDefinitions.PRAYER_BONUS];
		}
		
		// Fixed: Use correct array length based on prayer book
		int maxIndex = prayerBook == 0 ? NORMAL_PRAYERS_COUNT : ANCIENT_PRAYERS_COUNT;
		
		for (int index = 0; index < maxIndex && index < onPrayers[prayerBook].length; index++) {
			if (onPrayers[prayerBook][index] && index < nextDrain.length) {
				long drainTimer = nextDrain[index];
				if (drainTimer != 0 && drainTimer <= currentTime) {
					int rate = (int) ((PRAYER_DRAIN_RATE[prayerBook][index] * 1000) + (prayerPoints * 50));
					int passedTime = (int) (currentTime - drainTimer);
					drain++;
					int count = 0;
					while (passedTime >= rate && count++ < 10) {
						drain++;
						passedTime -= rate;
					}
					nextDrain[index] = (currentTime + rate) - passedTime;
				}
			}
		}
		
		if (drain > 0) {
			drainPrayer(drain);
			if (!checkPrayer()) {
				closeAllPrayers();
			}
		}
	}

	/**
	 * Checks if leech bonus has reached maximum
	 */
	public boolean reachedMax(int bonus) {
		if (leechBonuses == null || bonus < 0 || bonus >= leechBonuses.length) {
			return false;
		}
		
		if (bonus != 8 && bonus != 9 && bonus != 10) {
			return leechBonuses[bonus] >= MAX_LEECH_BONUS;
		}
		return false;
	}

	/**
	 * Recalculates prayer configuration value
	 */
	private void recalculatePrayer() {
		if (player == null || player.getVarsManager() == null) {
			return;
		}
		
		int value = 0;
		int index = 0;
		boolean[] prayers = usingQuickPrayer ? quickPrayers[getPrayerBook()] : onPrayers[getPrayerBook()];
		
		for (boolean prayer : prayers) {
			if (prayer) {
				if (ancientcurses) {
					value += Math.pow(2, index);
				} else {
					value += PRAYER_SLOT_VALUES[index];
				}
			}
			index++;
		}
		
		int configId = ancientcurses ? (usingQuickPrayer ? 1587 : 1582) : (usingQuickPrayer ? 1397 : 1395);
		player.getVarsManager().sendVar(configId, value);
	}

	/**
	 * Refreshes prayer interface
	 */
	public void refresh() {
		if (player != null && player.getPackets() != null) {
			player.getPackets().sendGlobalConfig(181, usingQuickPrayer ? 1 : 0);
			player.getPackets().sendConfig(1584, ancientcurses ? 1 : 0);
		}
		unlockPrayerBookButtons();
	}

	/**
	 * Refreshes prayer points display
	 */
	public void refreshPrayerPoints() {
		if (player != null && player.getPackets() != null) {
			player.getPackets().sendConfig(2382, prayerpoints);
		}
	}

	/**
	 * Resets all prayers and restores full prayer points
	 */
	public void reset() {
		closeAllPrayers();
		if (player != null && player.getSkills() != null) {
			prayerpoints = player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
		}
		refreshPrayerPoints();
	}

	/**
	 * Resets prayer drain timer for specific prayer
	 */
	public void resetDrainPrayer(int index) {
		if (nextDrain == null || index < 0 || index >= nextDrain.length) {
			return;
		}
		
		int prayerBook = getPrayerBook();
		long baseRate = (long) (PRAYER_DRAIN_RATE[prayerBook][index] * 1000);
		long bonusReduction = 0;
		
		if (player != null && player.getCombatDefinitions() != null) {
			bonusReduction = player.getCombatDefinitions().getBonuses()[CombatDefinitions.PRAYER_BONUS] * 50;
		}
		
		nextDrain[index] = Utils.currentTimeMillis() + baseRate + bonusReduction;
	}

	/**
	 * Resets all stat adjustments to 0
	 */
	public void resetStatAdjustments() {
		for (int i = 0; i < 5; i++) {
			adjustStat(i, 0);
		}
	}

	/**
	 * Restores prayer points
	 */
	public void restorePrayer(int amount) {
		if (player == null || player.getSkills() == null) {
			return;
		}
		
		int maxPrayer = player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
		prayerpoints = Math.min(maxPrayer, prayerpoints + amount);
		refreshPrayerPoints();
	}

	public void setBoostedLeech(boolean boostedLeech) {
		this.boostedLeech = boostedLeech;
	}

	/**
	 * Sets player reference and initializes transient fields
	 */
	public void setPlayer(Player player) {
		this.player = player;
		onPrayers = new boolean[2][];
		onPrayers[0] = new boolean[NORMAL_PRAYERS_COUNT];
		onPrayers[1] = new boolean[ANCIENT_PRAYERS_COUNT];
		
		// Ensure quick prayers array is correct size
		if (quickPrayers[1].length < ANCIENT_PRAYERS_COUNT) {
			boolean[] temp = new boolean[ANCIENT_PRAYERS_COUNT];
			System.arraycopy(quickPrayers[1], 0, temp, 0, quickPrayers[1].length);
			quickPrayers[1] = temp;
		}
		
		nextDrain = new long[NORMAL_PRAYERS_COUNT]; // Use larger array size for safety
		leechBonuses = new int[LEECH_BONUSES_COUNT];
	}

	/**
	 * Sets prayer book type
	 */
	public void setPrayerBook(boolean ancientcurses) {
		closeAllPrayers();
		this.ancientcurses = ancientcurses;
		if (player != null && player.getInterfaceManager() != null) {
			player.getInterfaceManager().sendPrayerBook();
		}
		refresh();
	}

	public void setPrayerpoints(int prayerpoints) {
		this.prayerpoints = Math.max(0, prayerpoints);
	}

	/**
	 * Switches prayer on/off
	 */
	public void switchPrayer(int prayerId) {
		if (!usingQuickPrayer && !checkPrayer()) {
			return;
		}
		
		usePrayer(prayerId);
		recalculatePrayer();
		
		if (player != null) {
			BossBalancer.onPrayerChanged(player);
		}
	}

	/**
	 * Switches quick prayers on/off
	 */
	public void switchQuickPrayers() {
		if (!checkPrayer()) {
			return;
		}
		
		if (hasPrayersOn()) {
			closeAllPrayers();
		} else {
			boolean hasOn = false;
			int index = 0;
			for (boolean prayer : quickPrayers[getPrayerBook()]) {
				if (prayer && usePrayer(index)) {
					hasOn = true;
				}
				index++;
			}
			if (hasOn && player != null && player.getPackets() != null) {
				player.getPackets().sendGlobalConfig(182, 1);
				recalculatePrayer();
			}
		}
	}

	/**
	 * Switches quick prayer setup mode
	 */
	public void switchSettingQuickPrayer() {
		usingQuickPrayer = !usingQuickPrayer;
		if (player != null && player.getPackets() != null) {
			player.getPackets().sendGlobalConfig(181, usingQuickPrayer ? 1 : 0);
			if (usingQuickPrayer) {
				player.getPackets().sendGlobalConfig(168, 6);
			}
		}
		unlockPrayerBookButtons();
	}

	/**
	 * Unlocks prayer book interface buttons
	 */
	public void unlockPrayerBookButtons() {
		if (player != null && player.getPackets() != null) {
			player.getPackets().sendUnlockIComponentOptionSlots(271, usingQuickPrayer ? 42 : 8, 0, 29, 0);
		}
	}

	/**
	 * Core prayer activation/deactivation logic
	 */
	private boolean usePrayer(int prayerId) {
		int prayerBook = getPrayerBook();
		
		// Validate prayer ID
		if (prayerId < 0 || prayerId >= PRAYER_LEVELS[prayerBook].length) {
			return false;
		}
		
		// Check level requirement
		if (player == null || player.getSkills() == null || 
			player.getSkills().getLevelForXp(Skills.PRAYER) < PRAYER_LEVELS[prayerBook][prayerId]) {
			if (player != null && player.getPackets() != null) {
				player.getPackets().sendGameMessage("You need a prayer level of at least "
						+ PRAYER_LEVELS[prayerBook][prayerId] + " to use this prayer.");
			}
			return false;
		}
		
		// Check special requirements
		if (!checkSpecialRequirements(prayerId, prayerBook)) {
			return false;
		}
		
		// Check prayer delay (injury protection)
		if (checkPrayerDelay(prayerId, prayerBook)) {
			return false;
		}
		
		// Handle prayer toggling
		if (usingQuickPrayer) {
			return toggleQuickPrayer(prayerId, prayerBook);
		} else {
			return toggleActivePrayer(prayerId, prayerBook);
		}
	}

	/**
	 * Checks special requirements for certain prayers
	 */
	private boolean checkSpecialRequirements(int prayerId, int prayerBook) {
		if (player == null) {
			return false;
		}
		
		if (prayerBook == 0) { // Normal prayers
			switch (prayerId) {
				case 26:
					if (!player.hasRenewalActivated()) {
						player.sendMessage("You need to activate the scroll of renewal to use this prayer.");
						return false;
					}
					break;
				case 28:
					if (!player.hasRigourActivated()) {
						player.sendMessage("You need to activate the scroll of rigour to use this prayer.");
						return false;
					}
					return checkDefenceLevel(70);
				case 29:
					if (!player.hasAuguryActivated()) {
						player.sendMessage("You need to activate the scroll of augury to use this prayer.");
						return false;
					}
					return checkDefenceLevel(70);
				case 25:
					return checkDefenceLevel(60);
				case 27:
					return checkDefenceLevel(70);
			}
		} else { // Ancient curses
			return checkDefenceLevel(30);
		}
		
		return true;
	}

	/**
	 * Helper method to check defence level requirement
	 */
	private boolean checkDefenceLevel(int requiredLevel) {
		if (player.getSkills().getLevelForXp(Skills.DEFENCE) < requiredLevel) {
			player.getPackets().sendGameMessage("You need a defence level of at least " + requiredLevel + " to use this prayer.");
			return false;
		}
		return true;
	}

	/**
	 * Checks if prayer is blocked due to injury delay
	 */
	private boolean checkPrayerDelay(int prayerId, int prayerBook) {
		if (player.getPrayerDelay() >= Utils.currentTimeMillis()) {
			player.getPackets().sendGameMessage("You are currently injured and cannot use protection prayers!");
			
			// Block protection prayers when injured
			if (prayerBook == 1 && prayerId >= 6 && prayerId <= 9) {
				return true;
			}
			if (prayerBook == 0 && prayerId >= 16 && prayerId <= 19) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Toggles quick prayer setting
	 */
	private boolean toggleQuickPrayer(int prayerId, int prayerBook) {
		if (quickPrayers[prayerBook][prayerId]) {
			quickPrayers[prayerBook][prayerId] = false;
			if (player != null && player.getPackets() != null) {
				player.getPackets().sendSound(2663, 0, 1);
			}
			return true;
		}
		
		// Activate quick prayer
		return activatePrayer(prayerId, prayerBook, true);
	}

	/**
	 * Toggles active prayer
	 */
	private boolean toggleActivePrayer(int prayerId, int prayerBook) {
		if (onPrayers != null && onPrayers[prayerBook][prayerId]) {
			// Deactivate prayer
			onPrayers[prayerBook][prayerId] = false;
			closePrayers(prayerId);
			onPrayersCount--;
			if (player != null) {
				player.getGlobalPlayerUpdater().generateAppearenceData();
				player.getPackets().sendSound(2663, 0, 1);
			}
			return true;
		}
		
		// Activate prayer
		return activatePrayer(prayerId, prayerBook, false);
	}

	/**
	 * Activates a prayer and handles conflicting prayers
	 */
	private boolean activatePrayer(int prayerId, int prayerBook, boolean isQuickPrayer) {
		boolean needAppearanceGenerate = false;
		
		// Close conflicting prayers based on prayer book and ID
		if (prayerBook == 0) {
			needAppearanceGenerate = handleNormalPrayerConflicts(prayerId);
		} else {
			needAppearanceGenerate = handleAncientCurseConflicts(prayerId, isQuickPrayer);
		}
		
		// Activate the prayer
		if (isQuickPrayer) {
			quickPrayers[prayerBook][prayerId] = true;
		} else {
			if (onPrayers != null) {
				onPrayers[prayerBook][prayerId] = true;
			}
			resetDrainPrayer(prayerId);
			onPrayersCount++;
			if (needAppearanceGenerate && player != null && player.getGlobalPlayerUpdater() != null) {
				player.getGlobalPlayerUpdater().generateAppearenceData();
			}
		}
		
		if (player != null && player.getPackets() != null) {
			player.getPackets().sendSound(2662, 0, 1);
		}
		
		return true;
	}

	/**
	 * Handles conflicting prayers for normal prayer book
	 */
	private boolean handleNormalPrayerConflicts(int prayerId) {
		switch (prayerId) {
			case 0: case 5: case 13:
				closePrayers(CLOSE_PRAYERS[0][0], CLOSE_PRAYERS[0][10]);
				break;
			case 1: case 6: case 14:
				closePrayers(CLOSE_PRAYERS[0][1], CLOSE_PRAYERS[0][3], CLOSE_PRAYERS[0][4], CLOSE_PRAYERS[0][10]);
				break;
			case 2: case 7: case 15:
				closePrayers(CLOSE_PRAYERS[0][2], CLOSE_PRAYERS[0][3], CLOSE_PRAYERS[0][4], CLOSE_PRAYERS[0][10]);
				break;
			case 3: case 11: case 20:
				closePrayers(CLOSE_PRAYERS[0][1], CLOSE_PRAYERS[0][2], CLOSE_PRAYERS[0][3], CLOSE_PRAYERS[0][10]);
				break;
			case 4: case 12: case 21:
				closePrayers(CLOSE_PRAYERS[0][1], CLOSE_PRAYERS[0][2], CLOSE_PRAYERS[0][4], CLOSE_PRAYERS[0][10]);
				break;
			case 8: case 9: case 26:
				closePrayers(CLOSE_PRAYERS[0][5]);
				break;
			case 10:
				closePrayers(CLOSE_PRAYERS[0][6]);
				break;
			case 17: case 18: case 19:
				closePrayers(CLOSE_PRAYERS[0][7], CLOSE_PRAYERS[0][9]);
				return true;
			case 16:
				closePrayers(CLOSE_PRAYERS[0][8], CLOSE_PRAYERS[0][9]);
				return true;
			case 22: case 23: case 24:
				closePrayers(CLOSE_PRAYERS[0][7], CLOSE_PRAYERS[0][8], CLOSE_PRAYERS[0][9]);
				return true;
			case 25: case 27: case 28: case 29:
				closePrayers(CLOSE_PRAYERS[0][0], CLOSE_PRAYERS[0][1], CLOSE_PRAYERS[0][2], 
						   CLOSE_PRAYERS[0][3], CLOSE_PRAYERS[0][4], CLOSE_PRAYERS[0][10]);
				break;
		}
		return false;
	}

	/**
	 * Handles conflicting prayers for ancient curse book
	 */
	private boolean handleAncientCurseConflicts(int prayerId, boolean isQuickPrayer) {
		switch (prayerId) {
			case 0:
				if (!isQuickPrayer && player != null) {
					player.setNextAnimation(new Animation(12567));
					player.setNextGraphics(new Graphics(2213));
				}
				closePrayers(CLOSE_PRAYERS[1][0]);
				break;
			case 1: case 2: case 3: case 4:
				closePrayers(CLOSE_PRAYERS[1][5], CLOSE_PRAYERS[1][6]);
				break;
			case 5:
				if (!isQuickPrayer && player != null) {
					player.setNextAnimation(new Animation(12589));
					player.setNextGraphics(new Graphics(2266));
				}
				closePrayers(CLOSE_PRAYERS[1][2]);
				break;
			case 7: case 8: case 9: case 17: case 18:
				closePrayers(CLOSE_PRAYERS[1][3]);
				return true;
			case 6:
				closePrayers(CLOSE_PRAYERS[1][4]);
				return true;
			case 10: case 11: case 12: case 13: case 14: case 15: case 16:
				closePrayers(CLOSE_PRAYERS[1][1], CLOSE_PRAYERS[1][6]);
				break;
			case 19: case 20: case 21:
				if (!isQuickPrayer && player != null) {
					player.setNextAnimation(new Animation(12565));
					player.setNextGraphics(new Graphics(2226));
				}
				closePrayers(CLOSE_PRAYERS[1][1], CLOSE_PRAYERS[1][5], CLOSE_PRAYERS[1][6]);
				break;
			case 22: case 23: case 24:
				if (!isQuickPrayer && player != null) {
					player.setNextAnimation(new Animation(30131));
					player.setNextGraphics(new Graphics(6528));
				}
				closePrayers(CLOSE_PRAYERS[1][1], CLOSE_PRAYERS[1][5], CLOSE_PRAYERS[1][6]);
				break;
			default:
				return false;
		}
		return false;
	}

	/**
	 * Checks if specific prayer is active
	 */
	public boolean usingPrayer(int book, int prayerId) {
		return onPrayers != null && onPrayers[book] != null && 
			   prayerId >= 0 && prayerId < onPrayers[book].length && 
			   onPrayers[book][prayerId];
	}

	/**
	 * Checks if protect item prayer is active
	 */
	public boolean isProtectingItem() {
		return usingPrayer(0, 10) || usingPrayer(1, 0);
	}

	/**
	 * Decreases leech bonus
	 */
	public void decrease(int bonus) {
		if (leechBonuses == null || bonus < 0 || bonus >= leechBonuses.length) {
			return;
		}
		
		leechBonuses[bonus]--;
		adjustStat(bonus, leechBonuses[bonus]);
	}

	/**
	 * Increases leech bonus
	 */
	public void increase(int bonus) {
		if (leechBonuses == null || bonus < 0 || bonus >= leechBonuses.length) {
			return;
		}
		
		leechBonuses[bonus]++;
		adjustStat(bonus, leechBonuses[bonus]);
	}

	// Protection prayer check methods
	public boolean isMageProtecting() {
		return ancientcurses ? usingPrayer(1, 7) : usingPrayer(0, 17);
	}

	public boolean isRangeProtecting() {
		return ancientcurses ? usingPrayer(1, 8) : usingPrayer(0, 18);
	}

	public boolean isMeleeProtecting() {
		return ancientcurses ? usingPrayer(1, 9) : usingPrayer(0, 19);
	}

	/**
	 * Drains all prayer points
	 */
	public void drainPrayer() {
		prayerpoints = 0;
		refreshPrayerPoints();
	}

	/**
	 * Checks if any protection prayer is active
	 */
	public boolean isUsingProtectionPrayer() {
		return isRangeProtecting() || isMeleeProtecting() || isMageProtecting();
	}
}