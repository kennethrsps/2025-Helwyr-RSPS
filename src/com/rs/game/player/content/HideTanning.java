package com.rs.game.player.content;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

public class HideTanning {

	public static int[][] hides = { { 1739, 1741 }, { 1753, 1745 },
			{ 1751, 2505 }, { 1749, 2507 }, { 1747, 2509 }, { 6287, 6289 }, { 24372, 24374 } };

	public static void tanHides(Player player) {
		int index = getIndex(player);
		for (Item item : player.getInventory().getItems().getItems()) {
			if (item != null) {
				if (index != -1) {
					if (item.getId() == hides[index][0]) {
						int amount = player.getInventory().getAmountOf(hides[index][0]);
						int cost = amount * 100;
						if (player.hasMoney(cost)) {
							player.getInventory().deleteItem(hides[index][0], amount);
							player.getInventory().addItem(hides[index][1], amount);
							player.sendMessage("The Tanner tans "+amount+" hides for you.", true);
							player.getDialogueManager().startDialogue("SimpleNPCMessage", 2824, "There you go, sir!");
							player.getInventory().refresh();
							player.takeMoney(cost);
							return;
						} else
							player.getDialogueManager().startDialogue("SimpleNPCMessage", 2824,
									"It seems that you don't have enough coins on you, "
									+ "you'll need "+Utils.getFormattedNumber(cost)+" coins to tan all your hides.");
					}
				}
			}
		}
		player.getDialogueManager().startDialogue("SimpleNPCMessage", 2824,
				"You don't have any hides on you, come back later when you do.");
		return;
	}

	private static int getIndex(Player player) {
		for (int i = 0; i < hides.length; i++) {
			for (Item item : player.getInventory().getItems().getItems()) {
				if (item != null) {
					if (item.getId() == hides[i][0])
						return i;
				}
			}
		}
		return -1;
	}
}