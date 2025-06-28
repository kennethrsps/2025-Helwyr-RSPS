package com.rs.game.player;

import java.io.Serializable;

import com.discord.Discord;
import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.controllers.Wilderness;
import com.rs.utils.Colors;
import com.rs.utils.Utils;
/*
import mysql.impl.NewsManager;*/

public class SquealOfFortune implements Serializable {

	private static final long serialVersionUID = -5330047553089876572L;

	public static final int RARITY_COMMON = 0;
	public static final int RARITY_UNCOMMON = 1;
	public static final int RARITY_RARE = 2;
	public static final int RARITY_JACKPOT = 3;

	private static final int SPIN_TYPE_DAILY = 0;
	private static final int SPIN_TYPE_EARNED = 1;
	private static final int SPIN_TYPE_BOUGHT = 2;

	private static final int SOF_STATUS_CLAIMINVOK = 1;
	private static final int SOF_STATUS_CLAIMINVBAD = 2;
	private static final int SOF_STATUS_CLAIMBANKOK = 3;
	private static final int SOF_STATUS_CLAIMBANKBAD = 4;
	private static final int SOF_STATUS_CLAIMPOUCHOK = 5;
	private static final int SOF_STATUS_CLAIMPOUCHBAD = 6;
	private static final int SOF_STATUS_DISABLED = 7;

	private transient Player player;

	private long lastDailySpinsGiveaway;

	private int dailySpins;
	private int earnedSpins;
	private int boughtSpins;

	private int rewardSlot;
	private int jackpotSlot;
	private Item[] rewards;

	public SquealOfFortune() {
		rewardSlot = -1;
		jackpotSlot = -1;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void processClick(int packetId, int interfaceId, int componentId) {
		if (interfaceId == 1139) { // squeal tab
			if (componentId == 18)
				openSpinInterface();
			else {
				player.getPackets().sendOpenURL(Settings.DONATE);
			}
		} else if (interfaceId == 1252) { // squeal overlay
			if (componentId == 3)
				openSpinInterface();
			player.getInterfaceManager().closeSquealOverlay();
		} else if (interfaceId == 1253) { // squeal main
			if (componentId == 106 || componentId == 258 || componentId == 2) { // hide/close
																				// button
				player.getInterfaceManager().sendWindowPane();
			} else if (componentId == 7 || componentId == 65) { // buy spins on
																// main/reward
				player.getPackets().sendOpenURL(Settings.DONATE);
			} else if (componentId == 93 && jackpotSlot != -1 && rewardSlot == -1) { // spin
																						// button
				pickReward();
			} else if ((componentId == 192 || componentId == 239) && rewardSlot != -1) { // picking
																							// reward
				obtainReward(componentId == 239);
			} else if (componentId == 273 && jackpotSlot == -1 && getTotalSpins() > 0) { // play
																							// again
				generateRewards(getNextSpinType());
				player.getVarBitManager().forceSendVarBit(11155, jackpotSlot + 1);
				player.getPackets().sendItems(665, rewards);
				player.getVarBitManager().forceSendVarBit(10861, 0);
				player.getPackets().sendRunScript(5879); // sof_setupHooks();
				sendSpinCounts();
			}
		}
	}

	public void processItemClick(int slotId, int itemId, Item item) {
		if (itemId == 24154 || itemId == 24155) { // spin ticket and double spin
													// ticket
			player.getInventory().deleteItem(item);
			giveEarnedSpins(itemId == 24154 ? 1 : 2);
		}
	}

	private void pickReward() {
		if (!useSpin())
			return;
		int rewardRarity = RARITY_COMMON;
		double roll = Utils.randomDouble();
		if (roll <= Settings.SOF_CHANCES[RARITY_JACKPOT])
			rewardRarity = RARITY_JACKPOT;
		else if (roll <= Settings.SOF_CHANCES[RARITY_RARE])
			rewardRarity = RARITY_RARE;
		else if (roll <= Settings.SOF_CHANCES[RARITY_UNCOMMON])
			rewardRarity = RARITY_UNCOMMON;
		int[] possibleSlots = new int[13];
		int possibleSlotsCount = 0;
		for (int i = 0; i < 13; i++) {
			if (getSlotRarity(i, jackpotSlot) == rewardRarity)
				possibleSlots[possibleSlotsCount++] = i;
		}
		rewardSlot = possibleSlots[Utils.random(possibleSlotsCount)];

		if (rewardRarity >= RARITY_JACKPOT)
			announceWin();

		player.getPackets().sendConfigByFile(10860, rewardSlot);
		player.getPackets().sendConfigByFile(10861, 1); // block spin & set
														// reward
		player.getPackets().sendGlobalConfig(1790, getRewardStatusType());
		player.getPackets().sendGlobalConfig(1781, getBestRewardSpoofSlot());
	}

	private void obtainReward(boolean discard) {
		int type = getRewardStatusType();
		Item reward = rewards[rewardSlot];
		if (reward.getId() == 30372)
			reward.setAmount(Utils.random(15, 250));
		if ((discard && type == SOF_STATUS_DISABLED) || (!discard && type != SOF_STATUS_CLAIMINVOK
				&& type != SOF_STATUS_CLAIMPOUCHOK && type != SOF_STATUS_CLAIMBANKOK))
			return;
		player.getPackets().sendConfigByFile(10861, 0); // prepare for next spin
		player.getPackets().sendGlobalConfig(1790, 0);
		player.getPackets().sendItems(665, new Item[13]);
		if (!discard) {
			if (type == SOF_STATUS_CLAIMPOUCHOK)
				player.getInventory().addItem(reward);
			else if (type == SOF_STATUS_CLAIMINVOK)
				player.getInventory().addItem(reward);
			else if (type == SOF_STATUS_CLAIMBANKOK) {
				if (reward.getDefinitions().isNoted())
					reward.setId(reward.getDefinitions().getCertId());
				player.getBank().addItem(reward.getId(), rewards[rewardSlot].getAmount(), false);
			}
		}
		rewards = null;
		jackpotSlot = -1;
		rewardSlot = -1;
		openSpinInterface();
	}

	/**
	 * Announces a rare reward.
	 */
	private void announceWin() {
		Item item = rewards[rewardSlot];
		String message = "News: " + player.getDisplayName() + " has just won " + "x"
				+ Utils.getFormattedNumber(item.getAmount()) + " of " + item.getName() + " on Squeal of Fortune";
		World.sendWorldMessage(Colors.orange + "<img=7>" + message + "!", false);
		Discord.sendAchievement(message + "!");

		/*
		 * new Thread(new NewsManager(player,
		 * "<b><img src=\"./bin/images/news/sof.png\" width=15> " + message + "."))
		 * .start();
		 */
	}

	public void openSpinInterface() {
		if (player.getInterfaceManager().containsInventoryInter()
				|| player.getInterfaceManager().containsScreenInter()) {
			player.getPackets()
					.sendGameMessage("Please finish what you are doing before opening the Squeal of Fortune.");
			return;
		}
		if (player.getAttackedByDelay() + 5000 > Utils.currentTimeMillis()) {
			player.sendMessage("You can't play the Squeal of Fortune until 5 seconds after the end of combat.");
			return;
		}
		if (Wilderness.isAtWild(player)) {
			player.getPackets().sendGameMessage("You can't play the Squeal of Fortune here.");
			return;
		}
		player.stopAll();
		sendSpinCounts();
		if (rewardSlot != -1) {
			openExistingReward();
		} else if (getTotalSpins() < 1) {
			openNoSpinsLeft();
		} else {
			openSpin();
		}
	}

	private void openExistingReward() {
		player.getPackets().sendConfigByFile(11155, jackpotSlot + 1); // need to
																		// send
																		// all
																		// items
																		// because
																		// otherwise
																		// it
																		// will
																		// set
																		// wrong
																		// color
																		// for
																		// rarity
																		// etc
		player.getPackets().sendItems(665, rewards);
		player.getPackets().sendWindowsPane(1253, 0);
		player.getPackets().sendConfigByFile(10860, rewardSlot);
		player.getPackets().sendConfigByFile(10861, 1); // block spin & set
														// reward
		player.getPackets().sendGlobalConfig(1790, getRewardStatusType());
		player.getPackets().sendRunScript(5906); // force call to
													// sof_displayPrize();

	}

	private void openNoSpinsLeft() {
		player.getPackets().sendConfigByFile(11155, Utils.random(13) + 1);
		player.getPackets().sendItems(665, new Item[13]);
		player.getPackets().sendWindowsPane(1253, 0);
		player.getPackets().sendConfigByFile(10861, 0);
		player.getPackets().sendGlobalConfig(1790, 0);
		player.getPackets().sendRunScript(5906); // force call to
													// sof_displayPrize();
	}

	private void openSpin() {
		if (rewards == null)
			generateRewards(getNextSpinType());
		player.getPackets().sendConfigByFile(11155, jackpotSlot + 1);
		player.getPackets().sendItems(665, rewards);
		player.getPackets().sendWindowsPane(1253, 0);
		player.getPackets().sendConfigByFile(10861, 0); // force allow spin
	}

	public void sendSpinCounts() {
		player.getPackets().sendConfigByFile(10862, dailySpins);
		player.getPackets().sendConfigByFile(11026, earnedSpins);
		player.getPackets().sendGlobalConfig(1800, boughtSpins);
		// must send all three otherwise it wont trigger refresh code @ cs2
	}

	private void generateRewards(int spinType) {
		jackpotSlot = Utils.random(13);
		rewards = new Item[13];
		for (int i = 0; i < rewards.length; i++)
			rewards[i] = generateReward(spinType, getSlotRarity(i, jackpotSlot));
	}

	public static Item generateReward(int spinType, int rarityType) {
		boolean isLamp = Utils.random(3) == 0; // lamp 1/3 others 2/3
		if (isLamp) {
			int[] lamps = Settings.SOF_COMMON_LAMPS;
			if (rarityType == RARITY_JACKPOT)
				lamps = Settings.SOF_JACKPOT_LAMPS;
			else if (rarityType == RARITY_RARE)
				lamps = Settings.SOF_RARE_LAMPS;
			else if (rarityType == RARITY_UNCOMMON)
				lamps = Settings.SOF_UNCOMMON_LAMPS;
			return new Item(lamps[Utils.random(lamps.length)], 1);
		} else {
			int[] items = Settings.SOF_COMMON_OTHERS;
			if (rarityType == RARITY_JACKPOT)
				items = Settings.SOF_JACKPOT_OTHERS;
			else if (rarityType == RARITY_RARE)
				items = Settings.SOF_RARE_OTHERS;
			else if (rarityType == RARITY_UNCOMMON)
				items = Settings.SOF_UNCOMMON_OTHERS;

			int itemId = items[Utils.random(items.length)];
			int amount;
			if (itemId == 995) {
				int[] amounts = Settings.SOF_COMMON_CASH_AMOUNTS;
				if (rarityType == RARITY_JACKPOT)
					amounts = Settings.SOF_JACKPOT_CASH_AMOUNTS;
				else if (rarityType == RARITY_RARE)
					amounts = Settings.SOF_RARE_CASH_AMOUNTS;
				else if (rarityType == RARITY_UNCOMMON)
					amounts = Settings.SOF_UNCOMMON_CASH_AMOUNTS;
				amount = amounts[Utils.random(amounts.length)];
			} else {
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
				amount = rarityType > RARITY_COMMON || (!defs.isStackable() && !defs.isNoted()) ? 1
						: Utils.random(5, 50);
			}
			return new Item(itemId, amount);
		}
	}

	private int getRewardStatusType() {
		Item reward = rewards[rewardSlot];
		if (reward.getId() == 995) { // coins go to pouch
			long amt = player.getInventory().getNumberOf(995) + reward.getAmount();
			return amt > Integer.MAX_VALUE || amt <= 0 ? SOF_STATUS_CLAIMPOUCHBAD : SOF_STATUS_CLAIMPOUCHOK;
		} else if (reward.getAmount() == 1 || reward.getDefinitions().isNoted()
				|| reward.getDefinitions().isStackable()) {
			return player.getInventory().hasFreeSlots() ? SOF_STATUS_CLAIMINVOK : SOF_STATUS_CLAIMINVBAD;
		} else { // other items go to bank
			if (player.getBank().getItem(reward.getId()) != null) {
				long amt = player.getBank().getItem(reward.getId()).getAmount() + reward.getAmount();
				return amt > Integer.MAX_VALUE || amt <= 0 ? SOF_STATUS_CLAIMBANKBAD : SOF_STATUS_CLAIMBANKOK;
			} else
				return player.getBank().hasBankSpace() ? SOF_STATUS_CLAIMBANKOK : SOF_STATUS_CLAIMBANKBAD;
		}
	}

	private int getBestRewardSpoofSlot() {
		int wonRarity = getSlotRarity(rewardSlot, jackpotSlot);
		if (wonRarity == RARITY_JACKPOT)
			return rewardSlot; // nothing to spoof
		int spoofMinType = wonRarity == RARITY_RARE ? RARITY_JACKPOT : RARITY_RARE;
		int bestSlot = -1;
		int bestDistance = Integer.MAX_VALUE;
		for (int i = 0; i < 13; i++) {
			if (i == rewardSlot || getSlotRarity(i, jackpotSlot) < spoofMinType)
				continue; // skip self & not rare
			int distance = distanceTo(i, rewardSlot);
			if (bestSlot == -1 || distance < bestDistance) {
				bestSlot = i;
				bestDistance = distance;
			}
		}
		return bestSlot;
	}

	private int distanceTo(int from, int to) {
		if (from == to)
			return 0;
		else if (from > to)
			return (13 - from) + to;
		else // (from < to)
			return (to - from);
	}

	public int getSlotRarity(int slot, int jackpotSlot) {
		if (slot == jackpotSlot) // jackpot overrides the slot
			return RARITY_JACKPOT;
		switch (slot) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 10:
		case 12:
			return RARITY_COMMON;
		case 2:
		case 6:
		case 9:
		case 11:
			return RARITY_UNCOMMON;
		case 0:
		case 4:
		case 8:
			return RARITY_RARE;
		default: // default case added so compiler can add tableswitch
					// instruction instead of lookupswitch
			throw new RuntimeException("Bad slot");
		}
	}

	public int getTotalSpins() {
		return dailySpins + earnedSpins + boughtSpins;
	}

	public int getNextSpinType() {
		if (dailySpins > 0)
			return SPIN_TYPE_DAILY;
		else if (earnedSpins > 0)
			return SPIN_TYPE_EARNED;
		else if (boughtSpins > 0)
			return SPIN_TYPE_BOUGHT;
		else
			return -1;
	}

	public boolean useSpin() {
		int type = getNextSpinType();
		if (type == -1)
			return false;
		if (type == SPIN_TYPE_DAILY)
			setDailySpins(dailySpins - 1);
		else if (type == SPIN_TYPE_EARNED)
			setEarnedSpins(earnedSpins - 1);
		else if (type == SPIN_TYPE_BOUGHT)
			setBoughtSpins(boughtSpins - 1);
		return true;
	}

	public void giveDailySpins() {
		if (Utils.getMinutesPlayed(player) < 10)
			return;
		if ((Utils.currentTimeMillis() - lastDailySpinsGiveaway) < (24 * 60 * 60 * 1000)) // 24 hours
			return;
		lastDailySpinsGiveaway = Utils.currentTimeMillis();
		player.dailyreset();
		int previous = dailySpins;

		if (player.isBronze())
			dailySpins += 5;
		else if (player.isSilver())
			dailySpins += 8;
		else if (player.isGold())
			dailySpins += 12;
		else if (player.isPlatinum())
			dailySpins += 15;
		else if (player.isDiamond())
			dailySpins += 15;
		else if (player.isSponsor())
			dailySpins += 20;
		else
			dailySpins++;

		if (dailySpins > previous)
			player.sendMessage("<col=FF0000>You have been awarded " + (dailySpins - previous) + " "
					+ "daily Squeal of Fortune spin" + (dailySpins == 1 ? "." : "s."));
		if (dailySpins != previous)
			sendSpinCounts();
	}

	public void giveEarnedSpins(int amount) {
		if (player.isIronMan() || player.isHCIronMan()) {
			return;
		}
		int previous = earnedSpins;
		earnedSpins += amount;
		if (earnedSpins != previous)
			sendSpinCounts();
	}

	public void giveBoughtSpins(int amount) {
		if (player.isIronMan() || player.isHCIronMan())
			return;
		int previous = boughtSpins;
		boughtSpins += amount;
		if (boughtSpins != previous)
			sendSpinCounts();
	}

	public void resetSpins() {
		dailySpins = 0;
		earnedSpins = 0;
		boughtSpins = 0;
		sendSpinCounts();
	}

	public int getDailySpins() {
		return dailySpins;
	}

	public void setDailySpins(int dailySpins) {
		int previous = this.dailySpins;
		this.dailySpins = dailySpins;
		if (this.dailySpins != previous)
			sendSpinCounts();
	}

	public int getEarnedSpins() {
		return earnedSpins;
	}

	public void setEarnedSpins(int earnedSpins) {
		int previous = this.earnedSpins;
		this.earnedSpins = earnedSpins;
		if (this.earnedSpins != previous)
			sendSpinCounts();
	}

	public int getBoughtSpins() {
		return boughtSpins;
	}

	public void setBoughtSpins(int boughtSpins) {
		int previous = this.boughtSpins;
		this.boughtSpins = boughtSpins;
		if (this.boughtSpins != previous)
			sendSpinCounts();
	}
}