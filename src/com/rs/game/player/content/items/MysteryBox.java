package com.rs.game.player.content.items;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.content.ItemConstants;
import com.rs.utils.Colors;
/*
import mysql.impl.NewsManager;*/

public class MysteryBox {

	public static int MYSTERY_BOX = 6199;
	
	private static int low[] = { 
			10330, 10332, 10334, //3a range
			10338, 10340, 10342, 10344, //3a mage
			10346, 10348, 10350, 10352, //3a melee
			1053, 1055, 1057, //h'ween masks
			23675, 23676, 23677, 23678, 28020, //squeal horns
	};

	private static int high[] = {
			27606, 27608, 27610, 27612, //flaming skulls
			20929, 37131, 34028, 24474, //ornate katana, golden cane, lucky coin, boogie bow
			33733, 33734, 33735, //snowboards 
			34006, 34007, 34008, //haters medalion, halo, demonic horns
			37130, 962 //chic scarf and cracker
	};
	
	public static int tableRandom(int[] table) {
		return (int)(Math.random() * (table.length));
	}
	
	public static boolean checkReward(Player player, Item item) {
		if(!ItemConstants.isTradeable(item)) {
			if(player.getInventory().containsItem(item) ||
					player.getBank().containsItem(item.getId(), 1) ||
					player.getEquipment().containsOneItem(item.getId()))
				return true;
			else
				return false;
		} else
			return false;
	}
	
	public static Item getReward(Player player) {
		int roll = 1 +(int)(Math.random() * ((3-1)+1));
		Item reward;
		if(roll != 3) {
			do { reward = new Item(low[tableRandom(low)]);
			} while (checkReward(player, reward));
		} else {
			do { reward = new Item(high[tableRandom(high)]);
			} while (checkReward(player, reward));
		}
		return reward;
	}
	
	public static void reward(Player player) {
		Item reward = getReward(player);
		ItemDefinitions itemDef = new ItemDefinitions(reward.getId());
		int amount = reward.getId() == 34027 ? 10 : 1;
		player.getInventory().deleteItem(MYSTERY_BOX, 1);
		player.getInventory().addItem(reward.getId(), amount);
		World.sendWorldMessage(Colors.cyan + "<shad=000000><img=6>News: "
				+ player.getDisplayName() + " has received a "+ itemDef.getName()+" from the Rare cosmetic shop!", false);
		/*new Thread(new NewsManager(player, "<b><img src=\"./bin/images/news/rare.png\" width=15> "
				+ player.getDisplayName()+" has received a "+ itemDef.getName()+" from the Rare cosmetic shop!")).start();*/
	}
}
