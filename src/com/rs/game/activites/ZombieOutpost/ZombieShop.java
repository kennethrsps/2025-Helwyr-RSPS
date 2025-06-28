package com.rs.game.activites.ZombieOutpost;

import java.util.HashMap;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

public class ZombieShop {
	
	public static HashMap<Integer,Integer> zombieShopPrices = new HashMap<>();
	public static HashMap<Integer,Integer> zombieShopAmounts = new HashMap<>();
	
	public static ItemsContainer<Item> zombieShop1 = new ItemsContainer<Item>(56, false);
	public static ItemsContainer<Item> zombieShop2 = new ItemsContainer<Item>(56, false);
	
	public static void openStore(Player player) {
		player.getInterfaceManager().sendInterface(206);
		player.getPackets().sendHideIComponent(206, 18, true);
		refreshTitle(player);	
		sendOptions(player);
		//player.getInterfaceManager().sendInventoryInterface(621);
	}
	
	public static void openZombieShop1(Player player) {
		openStore(player);
		player.getPackets().sendItems(100, zombieShop1);
	}
	public static void openZombieShop2(Player player) {
		openStore(player);
		player.getPackets().sendItems(100, zombieShop2);
	}

	
	public static void refreshTitle(Player player) {
		player.getPackets().sendIComponentText(206, 14, 
				"You currently have<col=ff0000> " 
				+ player.ZOPoints
				+ " </col>Points."
		);
	}
	
	public static void sendOptions(Player player) {
		player.getPackets().sendUnlockIComponentOptionSlots(621, 0, 0, 27, 0, 1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(621, 0, 93, 4, 7, "Value", "Sell 1", "Sell 5", "Sell 10", "Sell 50", "Examine");
		player.getPackets().sendUnlockIComponentOptionSlots(206, 15, 0, 54, 0, 1, 2, 3, 4,5,6);
		player.getPackets().sendInterSetItemsOptionsScript(206, 15, 100, 8, 8, "Value", "Purchase", "", "", "");
	}
	
	public static void sendPrice(Player player, int itemId) {
		player.getPackets().sendHideIComponent(206, 18, false);
		player.getPackets().sendIComponentText(206, 18, 
				ItemDefinitions.getItemDefinitions(itemId).getName()
				+ " currently costs<col=ff0000> " 
				+ Utils.formatNumber(zombieShopPrices.get(itemId))
				+ " </col>Points."
		);
	}
	
	public static void generateZombieShopPrices() {
		addCustomPrice(15775, 1000);
		addCustomPrice(15776, 2000);
		addCustomPrice(15777, 4000);
		addCustomPrice(15778, 8000);
		addCustomPrice(15779, 16000);
		addCustomPrice(15780, 32000);
		addCustomPrice(15781, 64000);
		addCustomPrice(15782, 128000);
		addCustomPrice(15783, 256000);
		addCustomPrice(15784, 512000);
		addCustomPrice(15785, 1024000);
	}
	
	public static void addCustomPrice(int itemId, int cost) {
		zombieShopPrices.put(itemId, cost);
	}
	
	public static void addCustomAmount(int itemId, int cost) {
		zombieShopPrices.put(itemId, cost);
	}
	
	public static boolean containsItem(int itemId) {
		if(zombieShop1 == null) {
			return false;
		}
		for(Item item : zombieShop1.getItems()) {
			if(item == null) {
				continue;
			}
			if(item.getId() == itemId) {
				return true;
			}
		}
		return false;
	}
	

	public static void populateZombieShop1() {
		int[] items = {15775, 15776, 15777, 15778, 15779, 15780, 15781, 15782, 15783, 15784, 15785};
		for (int i = 0; i < 8; i++) {				
			zombieShop1.add(new Item(-1));
		}
		for (int i = 0; i < items.length; i++) {
			Item item = new Item(items[i], 1);
			zombieShop1.add(item);
			zombieShopPrices.put(items[i], item.getDefinitions().value);
		}
	}

	public static void populateZombieShop2() {
		int[] items = {};
		for (int i = 0; i < 8; i++) {
			zombieShop2.add(new Item(-1));
		}
		for (int i = 0; i < items.length; i++) {
			zombieShop2.add(new Item(items[i], 1));
		}
	}
	
	public static void init() {
		populateZombieShop1();
		generateZombieShopPrices();
	}
	
	public static void buyItem(Player player, int itemId) {
		if (player.getInventory().getFreeSlots() <= 1) {
			player.getPackets().sendGameMessage("You need atleast 2 inventory slots to purchase items.");
			return;
		}
		if (!(player.ZOPoints >= zombieShopPrices.get(itemId))) {
			player.errorMessage("You don't have enough points to purchase this item.");
			return;
		} else {
			player.ZOPoints -= zombieShopPrices.get(itemId);
			player.getInventory().addItem(itemId, 1);
			refreshTitle(player);
			ZOGame.sendInterface(player);
		}
	}
}