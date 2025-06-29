package com.rs.game.player.dialogue.impl.quests.piratestreasure;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.ShopsHandler;

public class Zambo extends Dialogue {

	private int npcId;
	public static int SHOP_KEY = 65;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		this.sendNPCDialogue(npcId, NORMAL, "Hey, are you wanting to try some of my fine wines",
				"and spririts? All brewed locally on Karamja island.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue("Select an Option", "Yes, please.", "No, thank you");
			break;
		case 0:
			if (componentId == OPTION_1) {
				stage = 1;
				this.sendPlayerDialogue(NORMAL, "Yes, please.");
			} else {
				stage = 2;
				this.sendPlayerDialogue(NORMAL, "No, thank you");
			}
			break;
		case 1:
			end();
			ShopsHandler.openShop(player, SHOP_KEY);
			break;
		case 2:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
