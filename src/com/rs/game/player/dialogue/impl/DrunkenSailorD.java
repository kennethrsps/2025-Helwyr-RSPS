package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class DrunkenSailorD extends Dialogue {

	/**
	 * Ints representing the NPC id and the stage of the dialogue.
	 */
	int npcId = 1;
	int stage;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];

		if (player.getInventory().containsItem(571, 5) && player.getInventory().containsItem(316, 100)
				&& player.getInventory().containsItem(15273, 100) || player.DrunkenSailor == true) {
			sendNPCDialogue(npcId, GOOFY_LAUGH, "Now Lets GO and Sail!!");
			player.getInventory().deleteItem(571, 5);
			player.getInventory().deleteItem(316, 100);
			player.getInventory().deleteItem(15273, 100);
			player.DrunkenSailor = true;
			stage = 7;
			return;
		}
		if (player.Hween == false || player.Hween == false) {
			sendNPCDialogue(npcId, ANGRY, "Fuck off NOOB!?");
			stage = 10;
			return;

		} else {

			sendNPCDialogue(npcId, ASKING_FACE, "Noobie, " + player.getUsername() + " What do you need?");
			stage = -1;

		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendPlayerDialogue(ANGERY, "The Drunkard at Saloon told me the Elfs are here?");
			stage = 0;
			break;
		case 0:
			sendNPCDialogue(npcId, ANGRY, "Yes,they've just left");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(npcId, ANGRY, "Why?");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(ANGERY, "The Elfs Stole the Mystery Gift for Halloween Event!");
			stage = 3;
			break;
		case 3:
			sendPlayerDialogue(ANGERY, "I need to get thosem, Grim cannot start the event without the Mystery Gift!");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, ANGERY,
					"WTF?!, Let's go and sail but we need some supply before we sail, We need 100 Shrimps(noted), 5 water orb and 100 Rocktails (noted)");
			stage = 5;
			break;
		case 5:
			sendOptionsDialogue("Choose Options", "Okay, I'll get the supplies", "Nevermind,I'm already tired!.");
			stage = 6;
			break;
		case 6:
			switch (componentId) {
			case OPTION_1: {
				sendPlayerDialogue(MILDLY_ANGRY_FACE, "Okay,Ill talk to you when I have it!");
				stage = 10;
				return;
			}
			case OPTION_2: {
				sendPlayerDialogue(SHAKING_NO_FACE, "Nevermind, I dont really care about hallowen");
				stage = 10;
				return;
			}
			}
			break;
		case 7:
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2193, 3238,0));
			player.sm(Colors.red+"Kill the Elf Warriors and check out for the Mystery Gift, 1 Mystery Gift is equal to 1 Halloween Points.");
			break;

		case 10:
			end();
			break;

		}

	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}
}