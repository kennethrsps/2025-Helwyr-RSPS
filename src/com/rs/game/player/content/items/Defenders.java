package com.rs.game.player.content.items;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.Logger;

public class Defenders {
	
	
	public static int DRAGON_DEFENDER = 20072;
	
	public static int upgrades[] = {36156, 36159, 36163};
	
	public static int barrowsC[] = { 4718, 4726, 4747, 4755 };
	
	public static int defenders[][] = { 
			{36153, 36176}, {36157, 36179}, {36160, 36181}
	};
	
	public static String rewards[][] = { 
			{ "Corrupted defender", "Tainted repriser" },
			{ "Ancient defender", "Ancient repriser" },
			{ "Kalphite defender", "Kalphite repriser" } 
	};
	
	/**
	 * Range should be true for min, false for max
	 * @param id
	 * @param range 
	 * @return
	 */
	public static int getReduc(int id, boolean range) {
			if(id == 20072)
				return (range ? 20 : 70);
			switch(getTier(id)) {
			case 0:
				id = range ? 35 : 70;
				break;
			case 1:
				id = range ? 45 : 80;
				break;
			case 2:
				id = range ? 55 : 90;
				break;
			}
		return id;
	}

	public static double getModifier(int id) {
		double bonus = 1.0;
		if(id == 20072)
			return 1.05;
		switch(getTier(id)) {
		case 0:
			bonus = 1.09;
			break;
		case 1:
			bonus = 1.13;
			break;
		case 2:
			bonus = 1.17;
			break;
		}
		return bonus;
	}
	
	public static String getDefenderName(int id) {
		if(id == 20072)
			return "Dragon defender";
		int rotation = (isDefender(id)) ? 0 : 1;
		return rewards[getTier(id)][rotation];
	}
	
	public static int getTier(int id) {
		if(id == 36153 || id == 36176 || id == 20072)
			return 0;
		if(id == 36157 || id == 36179)
			return 1;
		if(id == 36160 || id == 36181)
			return 2;
		return -1;
	}
	
	public static boolean isRepriser(int id) {
		for(int i = 0; i < defenders.length; i++) 
			if(id == defenders[i][1] || id == 20072)
				return true;
		return false;
	}
	
	public static boolean isDefender(int id) {
		for(int i = 0; i < defenders.length; i++) 
			if(id == defenders[i][0] || id == 20072)
				return true;
		return false;
	}
	
	public static boolean getCurrentTier(Player player, int tier) {
		if(tier == 0 && (player.getInventory().containsItem(20072, 1) ||
				player.getEquipment().getShieldId() == 20072))
			return true;
		else {
			for(int e = 0; e < defenders.length; e++) {
				for(int i = 0 ; i < 2; i++) {
					if(player.getInventory().containsItem(defenders[tier][i], 1) ||
							player.getEquipment().getShieldId() == defenders[tier][i])
						return true;
				}
			}
			return false;
		}
	}
	
	public static int[] getDef(Player player, int upgrade) {
		int possible[] = { 0, 0 };
		for(int e = 0; e < upgrades.length; e++) {
			if(upgrade == upgrades[e]) {
				for(int z = 0; z < 2; z++) {
					if(e == 0 && player.hasItem(new Item(20072)))
						possible[z] = 20072;
					else
						if(e != 0 && player.hasItem(new Item(defenders[e-1][z])))
							possible[z] = defenders[e-1][z];
				}
			}
		}
		Logger.log(possible[0]+" "+possible[1]);
		return possible;
	}
	
	public static String[] getRewards(int item) {
		String rews[] = {"", ""};
		for(int i = 0; i < upgrades.length; i++)
			if(upgrades[i] == item)
				for(int x = 0; x < 2; x++)
					rews[x] = rewards[i][x];
		return rews;
	}
	
	public static int getReward(int defender, int path) {
		switch(defender) {
		case 20072:
			if(path == 0)
				return 36153;
			else
				return 36176;
		case 36153:
			return 36157;
		case 36176:
			return 36179;
		case 36157:
			return 36160;
		case 36179:
			return 36181;
		default:
			return 0;
		}
	}
	public static int getComp(int defender, int path) {
		switch(defender) {
		case 20072:
			if(path == 0)
				return 4718;
			else
				return 4734;
		case 36153:
			return 36164;
		case 36176:
			return 36164;
		default:
			return 0;
		}
	}
}
