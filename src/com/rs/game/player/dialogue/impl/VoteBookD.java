package com.rs.game.player.dialogue.impl;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

/**
 * Handles opening of Vote Books.
 *
 * @ausky Noel
 */
public class VoteBookD extends Dialogue {

	@Override
	public void start() {
		sendItemDialogue(11640, 1, "Opening this Vote Book will give you a reward of your choice. To get "
				+ "more Vote Books simply ::vote for Helwyr on the most popular Gaming Top Sites.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		String[] amounts = { World.isWeekend() ? "500,000" : "250,000", World.isWeekend() ? "1,000,000" : "500,000",
				World.isWeekend() ? "1,500,000" : "750,000", World.isWeekend() ? "2,000,000" : "1,000,000" };
		switch (stage) {
		case -1:
			sendOptionsDialogue(Colors.cyan + "Select your Reward",
					"No coins and " + getPercentage() + "% bonus experience for 2 hours.",
					amounts[0] + " coins and " + getPercentage() + "% bonus experience for 1.5 hours.",
					amounts[1] + " coins and " + getPercentage() + "% bonus experience for 1 hour.",
					amounts[2] + " coins and " + getPercentage() + "% bonus experience for 30 minutes.",
					amounts[3] + " coins and no bonus experience.");
			stage = 0;
			break;
		case 0:
			int amount;
			switch (componentId) {
			case OPTION_1:
				amount = 0;
				handleReward(7200000, amount);
				sendDialogue("You've chosen no coins and " + getPercentage() + "% bonus experience for 2 hours.");
				stage = 1;
				break;
			case OPTION_2:
				amount = World.isWeekend() ? 500000 : 250000;
				handleReward(5400000, amount);
				sendDialogue("You've chosen " + amounts[0] + " coins and " + getPercentage()
						+ "% bonus experience for 1.5 hour.");

				stage = 1;
				break;
			case OPTION_3:
				amount = World.isWeekend() ? 1000000 : 500000;
				handleReward(3600000, World.isWeekend() ? 1000000 : 500000);
				sendDialogue("You've chosen " + amounts[1] + " coins and " + getPercentage()
						+ "% bonus experience for 1 hour.");

				stage = 1;
				break;
			case OPTION_4:
				amount = World.isWeekend() ? 1500000 : 750000;
				handleReward(1800000, World.isWeekend() ? 1500000 : 750000);
				sendDialogue("You've chosen " + amounts[2] + " coins and " + getPercentage()
						+ "% bonus experience for 30 minutes.");

				stage = 1;
				break;
			case OPTION_5:
				amount = World.isWeekend() ? 2000000 : 1000000;
				handleReward(0, amount);
				sendDialogue("You've chosen " + amounts[3] + " coins and no bonus experience.");
				stage = 1;
				break;
			}
			break;

		case 1:
			finish();
			break;
		}
	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}

	public String getPercentage() {
		if (player.isSponsor())
			return Settings.expBoosts[6][1];
		if (player.isDiamond())
			return Settings.expBoosts[5][1];
		else if (player.isPlatinum())
			return Settings.expBoosts[4][1];
		else if (player.isGold())
			return Settings.expBoosts[3][1];
		else if (player.isSilver())
			return Settings.expBoosts[2][1];
		else if (player.isBronze())
			return Settings.expBoosts[1][1];
		else
			return "25";
	}

	/**
	 * Handles the chosen vote book reward.
	 *
	 * @param xpBonus
	 *            The xpBonus in millisecs.
	 * @param coins
	 *            The amount of coins.
	 */
	private boolean handleReward(long xpBonus, int coins) {
		if (player.getInventory().containsItem(new Item(11640, 1))) {
			if (xpBonus > 0)
				player.addDoubleXpTimer(xpBonus);
			if (coins > 0)
				player.addMoney(coins);
			player.getInventory().deleteItem(new Item(11640, 1));
			return true;
		}
		return false;
	}
}