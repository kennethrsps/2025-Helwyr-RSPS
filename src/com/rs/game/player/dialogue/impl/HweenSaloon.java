package com.rs.game.player.dialogue.impl;

import com.rs.Settings;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;

public class HweenSaloon extends Dialogue {

	/**
	 * Ints representing the NPC id and the stage of the dialogue.
	 */
	int npcId = 1;
	int stage;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];

		if (player.getInventory().containsItem(15243, 100) && player.getInventory().containsItem(868, 10)
				|| player.saloon == true) {
			sendNPCDialogue(npcId, GOOFY_LAUGH, "Ohh nice! you've got it all!");
			player.getInventory().deleteItem(15243, 100);
			player.getInventory().deleteItem(868, 10);
			player.saloon = true;
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
			sendNPCDialogue(npcId, ANGERY,
					"Yes I know where they are but before I tell you, you have to give me 10 rune knife and 100 hand canon shot");
			stage = 5;
			break;
		case 5:
			sendOptionsDialogue("Choose Options", "Okay,Ill talk to you when I have it",
					"Nevermind,i dont care about hallowen.");
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
				sendPlayerDialogue(LAUGHING_HYSTERICALLY, "Nevermind, I dont really care about hallowen");
				stage = 10;
				return;
			}
			}
			break;
		case 7:
			sendNPCDialogue(npcId, UNSURE_FACE, "I last Saw them on Port Sarim");
			stage = 8;
			break;
		case 8:
			sendNPCDialogue(npcId, UNSURE_FACE, "You need to hurry though they might have been gone already!");
			stage = 9;
		case 9:
			sendPlayerDialogue(MILDLY_ANGRY_FACE, "Okay, Thanks!");
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