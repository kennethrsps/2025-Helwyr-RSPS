package com.rs.game.player.content.clans.content;

import java.io.Serializable;

import com.rs.game.player.Player;
import com.rs.game.player.content.clans.ClansManager;

/**
 * 
 * @author Frostbite
 *
 *<frostbitersps@gmail.com><skype@frostbitersps>
 */

public class ClanRevenue implements Serializable {

	private static final long serialVersionUID = 806542375600846439L;

	public static final int EASY_ACHIEVEMENT = 1, MEDIUM_ACHIEVEMENT = 2, HARD_ACHIEVEMENT = 3, 
			EASY_TASK = 4, MEDIUM_TASK = 5, HARD_TASK = 6, 
			EASY_QUEST = 7, MEDIUM_QUEST = 8, HARD_QUEST = 9,
			RESOURCE_CAP = 10, BOSS = 11, MONSTER = 12, 
			DEFAULT = 13;

	private int clanRevenue;
	private int totalRevenueEarned;

	public int getClanRevenue() {
		return clanRevenue;
	}

	public void setClanRevenueEarned(int clanRevenue) {
		this.clanRevenue = clanRevenue;
	}

	public int getTotalRevenueEarned() {
		return totalRevenueEarned;
	}

	public int setTotalRevenueEarned(int amount) {
		return totalRevenueEarned = amount;
	}

	/*public void addClanRevenue(Player player, int amount) {
		ClansManager manager = player.getClanManager();
		if(!(manager == null)) {
			if(manager.getClan().getClanBank().getItem(995).getAmount() > Integer.MAX_VALUE - amount) {
				return;
			}
			player.getClanManager().getClan().getClanBank().addItem(player, 995, amount, true);
			setTotalRevenueEarned(getTotalRevenueEarned() + amount);
			if(amount >= 10000) {
				sendGlobalMessage(player, "<col=269C0E>" + player.getClanName() + "'s Bank has just recieved x" + amount + " revenue.");
			}
		}
	}*/

	public void sendMessage(Player player, String message) {
		player.getPackets().sendGameMessage(message);
	}

	public void sendGlobalMessage(Player player, String message) {
		player.getClanManager().sendGlobalMessage(message);
	}

	/**
	 * Achievement Revenue Types
	 */

	public void addHardAchievementRevenue(Player player) {
		//player.getClanManager().getClan().getClanRevenue().addClanRevenue(player, 123);
	}

	public void addMediumAchievementRevenue(Player player) {
	}

	public void addEasyAchievementRevenue(Player player) {
	}

	/**
	 * Task Revenue Type
	 */

	public void addHardTaskRevenue(Player player) {
	}

	public void addMediumTaskRevenue(Player player) {
	}

	public void addEasyTaskRevenue(Player player) {
	}

	/**
	 * Quest Revenue Type
	 */

	public void addHardQuestRevenue(Player player) {
	}

	public void addMediumQuestRevenue(Player player) {
	}

	public void addEasyQuestRevenue(Player player) {
	}

	/**
	 * Resource Revenue Type 
	 */

	public void addResourceCapRevenue(Player player) {
	}

	/**
	 * Default Revenue Type
	 */

	public void addDefaultRevenue(Player player) {
	}

	public int getExperience(int coins) {
		switch(coins) {
		case HARD_ACHIEVEMENT:
			return 2598;
		case MEDIUM_ACHIEVEMENT:
			return 1972;
		case EASY_ACHIEVEMENT:
			return 1570;
		case HARD_TASK:
			return 1985;
		case MEDIUM_TASK:
			return 1478;
		case EASY_TASK:
			return 1050;
		case HARD_QUEST:
			return 1650;
		case MEDIUM_QUEST:
			return 1240;
		case EASY_QUEST:
			return 750;
		case RESOURCE_CAP:
			return 1875;
		case DEFAULT:
			return 100000;//TESTING PURPOSES
		}
		return 0;
	}

}
