package com.rs.game.player.dialogue.impl;

import com.rs.game.player.Skills;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;

public class Bob extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendOptionsDialogue("Select an Option", "Can you repair my items for me?", "Open skilling tools shop.",
				"Prestige me", "Prestige Shop");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(9827, "Can you repair my items for me?");
				stage = 0;
				break;
			case OPTION_2:
				finish();
				ShopsHandler.openShop(player, 16);
				break;
			case OPTION_3:
				sendDialogue(
						"Are you sure you want to prestige ALL your skills that are level 99? This includes combat skills. This can't be reverted");
				stage = 2;
				break;
			case OPTION_4:
				sendOptionsDialogue("Select a skill to reset", "Prestige Shop Tier 1", "Prestige Shop Tier 2");
				stage = 6;
				break;

			}
			break;
		case 6: {
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 110);
				end();
				break;
			case OPTION_2:
				ShopsHandler.openShop(player, 111);
				end();
				break;
			}
			break;
		}
		case 0:
			sendNPCDialogue(npcId, 9827, "Of course I can, though the materials may cost you. Just hand me the item "
					+ "and I'll have a look.");
			player.sendMessage("Simply use the broken item on Bob if you wish to repair it!");
			stage = 1;
			break;
		case 1:
			finish();
			break;
		case 2:
			sendOptionsDialogue("Select an option", "Yes I am sure.", "No, nevermind:");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				end();
				for (int i = 0; i < 18; i++) {
					if (player.getEquipment().getItems().get(i) != null) {
						player.sm("Please unequip: " + player.getEquipment().getItem(i).getName()
								+ " before you prestige.");
						return;
					}
				}
				if (player.getFamiliar() != null) {
					player.sm("Please dismiss your familiar before prestige.");
					return;
				}

				player.prestige();

				break;
			case OPTION_2:
				end();
				break;
			}
		}
	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}
}