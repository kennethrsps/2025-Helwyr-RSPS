package com.rs.game.player.content.dungeoneering;

import com.rs.cache.loaders.ClientScriptMap;
import com.rs.game.TemporaryAtributtes.Key;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;

public class DungeonRewardShop {

	public static final int REWARD_SHOP = 940;

	public static Object[][] REWARDS = {
			{ 981, 18337, 21, 34000, 5, 21, "You need a Prayer level of 21 to access this item." },
			{ 1032, 19675, 21, 34000, 15, 21, "You need a Herblore level of 21 to access this item." },
			{ 982, 18338, 25, 2000, 12, 25, "You need a Crafting level of 25 to access this item." },
			{ 980, 18336, 25, 10000, 19, 25, "You need a Farming level of 25 to access this item." },
			{ 977, 18333, 30, 6500, 6, 30, "You need a Magic level of 30 to access this item." },
			{ 1154, 19886, 30, 8500, 5, 30, "You need a Prayer level of 30 to access this item." },
			{ 1031, 19671, 30, 40000, 6, 30, "You need Magic and Runecrafting levels of 30 to access this item." },
			{ 983, 18339, 35, 4000, 14, 35, "You need a Mining level of 35 to access this item." },
			{ 1158, 19890, 49, 20000, 15, 49, "You need a Herblore level of 49 to access this item." },
			{ 976, 18330, 45, 10000, 4, 45, "You need a Ranged level of 45 to access this item." },
			{ 999, 18365, 45, 40000, 0, 45, "You need an Attack level of 45 to access this item." },
			{ 1000, 18367, 45, 40000, 0, 45, "You need an Attack level of 45 to access this item." },
			{ 1001, 18369, 45, 40000, 0, 45, "You need an Attack level of 45 to access this item." },
			{ 1002, 18371, 45, 40000, 6, 45, "You need a Magic level of 45 to access this item." },
			{ 1003, 18373, 45, 40000, 4, 45, "You need a Ranged level of 45 to access this item." },
			{ 986, 18342, 45, 10000, 6, 45, "You need a Magic level of 45 to access this item." },
			{ 989, 18346, 48, 43000, 6, 48, "You need a Magic level of 48 to access this item." },
			{ 1160, 19892, 48, 40000, 5, 48, "You need a Prayer level of 48 to access this item." },
			{ 978, 18334, 50, 15500, 6, 50, "You need a Magic level of 50 to access this item." },
			{ 1161, 19893, 50, 45000, 1, 50, "You need Defence and Summoning levels of 50 to access this item." },
			{ 985, 18341, 53, 12500, 6, 53, "You need a Magic level of 53 to access this item." },
			{ 1034, 19670, 55, 20000, 13, 55, "You need a Smithing level of 55 to access this item." },
			{ 984, 18340, 60, 44000, 1, 60,
					"You need a Defence level of 60 and a Herblore level of 70 to access this item." },
			{ 1155, 19887, 60, 17000, 5, 60, "You need a Prayer level of 60 to access this item." },
			{ 1030, 19669, 62, 50000, 0, 62, "You need an Attack level of 62 to access this item." },
			{ 987, 18343, 65, 107000, 5, 65, "You need a Prayer level of 65 to access this item." },
			{ 979, 18335, 70, 30500, 6, 70, "You need a Magic level of 70 to access this item." },
			{ 1157, 19889, 70, 65000, 6, 70, "You need Magic and Runecrafting levels of 70 to access this item." },
			{ 990, 18347, 73, 48500, 4, 73, "You need a Ranged level of 73 to access this item." },
			{ 1033, 18839, 74, 140000, 5, 74, "You need a Prayer level of 74 to access this item." },
			{ 988, 18344, 77, 153000, 5, 77, "You need a Prayer level of 77 to access this item." },
			{ 991, 18349, 80, 200000, 0, 80, "You need an Attack level of 80 to access this item." },
			{ 992, 18351, 80, 200000, 0, 80, "You need an Attack level of 80 to access this item." },
			{ 993, 18353, 80, 200000, 0, 80, "You need an Attack level of 80 to access this item." },
			{ 994, 18355, 80, 200000, 6, 80, "You need a Magic level of 80 to access this item." },
			{ 995, 18357, 80, 200000, 4, 80, "You need a Ranged level of 80 to access this item." },
			{ 996, 18359, 80, 200000, 1, 80, "You need a Defence level of 80 to access this item." },
			{ 997, 18361, 80, 200000, 1, 80, "You need a Defence level of 80 to access this item." },
			{ 998, 18363, 80, 200000, 1, 80, "You need a Defence level of 80 to access this item." },
			{ 1159, 19894, 80, 85000, 23, 80, "You need a Summoning level of 80 to access this item." },
			{ 1156, 19888, 90, 35000, 5, 90, "You need a Prayer level of 90 to access this item." },
			{ 1004, 18348, 1, 1, 0, 0, "" } };

	public static void openRewardShop(final Player player) {
		player.getInterfaceManager().sendInterface(REWARD_SHOP);
		player.getPackets().sendUnlockIComponentOptionSlots(REWARD_SHOP, 2, 0, 205, 0, 1, 2);
		refreshPoints(player);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getTemporaryAttributtes().remove(Key.DUNGEON_REWARD_SLOT);
			}
		});
	}

	public static void purchase(Player player) {
		int[] canPurchase = canPurchase(player);
		if (canPurchase == null || canPurchase[0] == -1 || canPurchase[1] == -1)
			return;
		removeConfirmationPurchase(player);
		player.getInventory().addItemDrop(canPurchase[0], 1);
		player.getDungManager().addTokens(-canPurchase[1]);
		refreshPoints(player);
	}

	public static void sendConfirmationPurchase(Player player) {
		if (canPurchase(player) == null)
			return;
		player.getPackets().sendHideIComponent(REWARD_SHOP, 42, false);
	}

	public static void removeConfirmationPurchase(Player player) {
		player.getPackets().sendHideIComponent(REWARD_SHOP, 42, true);
	}

	public static void select(Player player, int slot) {
		player.getTemporaryAttributtes().put(Key.DUNGEON_REWARD_SLOT, ClientScriptMap.getMap(3015).getValue(slot / 5));
	}

	private static int[] canPurchase(Player player) {
		if (player.getTemporaryAttributtes().get(Key.DUNGEON_REWARD_SLOT) == null)
			return null;
		int slot = (int) player.getTemporaryAttributtes().get(Key.DUNGEON_REWARD_SLOT);
		if (slot == 1004) {
			player.getPackets().sendGameMessage("You can not purchase this item!");
			return null;
		}
		boolean purchaseEnabled = true;
		for (int i = 0; i < REWARDS.length; i++) {
			int slotId = (int) REWARDS[i][0];
			if (slotId == slot) {
				int itemId = (int) REWARDS[i][1];
				int levelRequirment = (int) REWARDS[i][2];
				int price = (int) REWARDS[i][3];
				int skill = (int) REWARDS[i][4];
				int dungeoneeringLevel = (int) REWARDS[i][5];
				String message = (String) REWARDS[i][6];
				if (player.getPerkManager().dungeon) {
					price -= price * 0.1;
					player.getPackets()
							.sendGameMessage("You recieve a discount of 10% for having the dungeon master perk.");
				}
				if (player.getSkills().getLevel(skill) < levelRequirment
						|| player.getSkills().getLevel(Skills.DUNGEONEERING) < dungeoneeringLevel
						|| player.getDungManager().getTokens() < price)
					purchaseEnabled = false;
				if (purchaseEnabled)
					return new int[] { itemId, price };
				if (player.getDungManager().getTokens() < price) {
					player.getPackets().sendGameMessage("You do not have enough tokens to purchase this item.");
					return null;
				}
				player.getPackets().sendGameMessage("You do not meet the requirements to purchase this item.");
				player.getPackets().sendGameMessage(message);
				break;
			}
		}
		return null;
	}

	private static void refreshPoints(Player player) {
		player.getPackets().sendIComponentText(REWARD_SHOP, 31, "" + player.getDungManager().getTokens());
	}
}
