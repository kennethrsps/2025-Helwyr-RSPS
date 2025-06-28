package com.rs.game.player.dialogue.impl;

import com.rs.Settings;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;

public class DrHarlow extends Dialogue {

	/**
	 * Ints representing the NPC id and the stage of the dialogue.
	 */
	int npcId = 1;
	int stage;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];

		if (player.getInventory().containsItem(2364, 10) && player.getInventory().containsItem(4587, 1)
				|| player.saloon == true) {
			sendNPCDialogue(npcId, GOOFY_LAUGH, "Fool! I dont know where are the elfs, I'm not the one Grims talking about. HAHAHAHA!!");
			player.getInventory().deleteItem(2364, 10);
			player.getInventory().deleteItem(4587, 1);
			stage = 7;
			return;
		}
		if (player.Hween == false) {
			sendNPCDialogue(npcId, ANGRY, "Fuck off!?");
			stage = 10;
			return;

		} else {

			sendNPCDialogue(npcId, ASKING_FACE, "Hey'ya, " + player.getUsername() + " What'cha need?");
			stage = -1;

		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendPlayerDialogue(ANGERY, "The Grim Reaper has a big problem, The Halloween Mystery Gift has been stolen");
			stage = 0;
			break;
		case 0:
			sendNPCDialogue(npcId, ANGRY, "What!!, Are you serious?");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(npcId, ANGRY, "Who stole the Mystery Gift?");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(ANGERY, "The Elfs Stole it!");
			stage = 3;
			break;
		case 3:
			sendPlayerDialogue(ANGERY, "Grim Told me you know where i can find those Elfs!");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, ANGERY, "Uhmmm... , you have to give me 10 rune bar(noted) and 1 Dragon Scimitar");
			stage = 5;
			break;
		case 5:
			sendOptionsDialogue("Choose Options", "Okay,Ill talk to you when I have it",
					"Nevermind,I don't care about hallowen.");
			stage = 6;
			break;
		case 6:
			switch (componentId) {
			case OPTION_1: {
				sendPlayerDialogue(MILDLY_ANGRY_FACE, "Okay,I'll talk to you when I have it!");
				stage = 10;
				return;
			}
			case OPTION_2: {
				sendPlayerDialogue(LAUGHING_HYSTERICALLY, "Nevermind, I dont really care about hallowen");
				stage = 10;
				return;
			}
			}
			break;
		case 7:
			sendNPCDialogue(npcId, UNSURE_FACE, "HAHAHA!! Thanks. I Dont know where those elfs are!!");
			stage = 10;
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