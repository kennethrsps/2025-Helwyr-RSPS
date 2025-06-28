package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class NewFeaturesD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Where would you like to go?", "Miscallenia", "Clan", "Check Achievements",
				"Chameleon Skins", Colors.red+ "Cancel");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Miscallenia", "Open Guide", "Go to my Kingdom!", "back");
				stage = 2;
				return;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Clan", "Claim Clan Vexilium", "Go to Clan Citadel", "back");

				stage = 3;

			}
			if (componentId == OPTION_3) {
				end();
				player.getAchManager().sendInterface("EASY");
				return;
			}
			if (componentId == OPTION_4) {
				player.getDialogueManager().startDialogue("ChameleonExtractD");
				return;
			}
			if (componentId == OPTION_5) {
				end();
				return;
			}


			break;
		case 2:
			if (componentId == OPTION_1) {
				player.getPackets().sendOpenURL("www.helwy3.com/forums");
				return;

			}
			if (componentId == OPTION_2) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2581, 3845, 0));
				return;
			}
			if (componentId == OPTION_3) {
				sendOptionsDialogue("Where would you like to go?", "Miscallenia", "Clan", "Check Achievements",
						"cancel");
				stage = -1;
			}
			break;
		case 3:
			if (componentId == OPTION_1) {
				if (player.getInventory().getFreeSlots() > 2)
					player.getInventory().addItem(20709, 1);
				player.getInventory().addItem(20708, 1);
				end();

				return;
			}
			if (componentId == OPTION_2) {
				if (player.getClanManager() != null)
					player.getClanManager().getClan().getClanCitadel().enterClanCitadel(player);
				return;
			}
			if (componentId == OPTION_3) {
				sendOptionsDialogue("Where would you like to go?", "Miscallenia", "Clan", "Check Achievements",
						"cancel");
				stage = -1;
			}
			break;

		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
