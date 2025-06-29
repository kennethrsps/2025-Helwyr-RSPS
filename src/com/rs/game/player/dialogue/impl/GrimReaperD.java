package com.rs.game.player.dialogue.impl;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;

public class GrimReaperD extends Dialogue {

	/**
	 * Ints representing the NPC id and the stage of the dialogue.
	 */
	int npcId = 1;
	int stage;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, SCARED,
				"Hello, " + player.getUsername() + " watch out something is coming!!!");
		
		
		
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			end();
			break;
		case 0:
			player.setNextAnimation(new Animation(24492));
			player.setNextGraphics(new Graphics(5109));
			end();
			break;
		case 1:
			sendOptionsDialogue("Would you be able to help me?", "Yes, I'll help you get the  Mystery Gift back",
					"No. I'm noob and scared.");
			stage = 2;
			break;
		case 2:
			switch (componentId) {
			case OPTION_1: {
				sendPlayerDialogue(MILDLY_ANGRY_FACE, "Yes,I'help you get those  Mystery Gift back!");
				player.Hween = true;
				stage = 3;
				return;
			}
			case OPTION_2: {
				sendPlayerDialogue(SCARED_FACE, "No. I'm noob and scared");
				player.Hween = false;
				stage = 7;
				return;
			}
			}
			break;
		case 3:
			sendNPCDialogue(npcId, UNSURE_FACE,
					"Okay!, I'm not sure where the Elfs is, But I know someone in Varrock that can help you locate them.");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, UNSURE_FACE, "uhmmm... ahhhm....");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, UNSURE_FACE,
					"I'm not really sure with his name, but I last saw him on the Varrock Saloon");
			stage = 6;
		case 6:
			sendPlayerDialogue(MILDLY_ANGRY_FACE, "Fuck this shit, I'll find that guy!");
			stage = 8;
			break;
		case 7:
			end();
			break;
		case 8:
			sendNPCDialogue(npcId, UNSURE_FACE,
					"Once you found the elfs get the Mystery gift and open it, opening it is equivalent into 1 Halloween points which you can use @ Wise old Man's Event Shop ");
			stage = 7;
			break;
		case 99:
			end();
			break;

		}

	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}
}