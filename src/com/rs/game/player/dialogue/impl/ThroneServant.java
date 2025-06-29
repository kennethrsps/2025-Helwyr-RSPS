package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.ancientthrone.ThroneManager;
import com.rs.game.player.dialogue.Dialogue;

public class ThroneServant extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Servant", "Adjust taxes", "Claim Ancient Sword", "Claim Ancient Crown");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		System.err.println("comp: "+componentId);
		int c = componentId;
		if (!ThroneManager.getThrone().getKing().equals(player.getUsername())) {
			end();
			player.getDialogueManager().startDialogue("SimpleMessage", "Get away from me, I don't serve you!");
			return;
		}
		if (c == 11) {
			end();
			player.getTemporaryAttributtes().put("set_tax", Boolean.TRUE);
			player.getPackets().sendRunScript(108, new Object[] { "Set tax (20% max)" });
		} else if (c == 13) {
			end();
			player.getInventory().addItem(ThroneManager.ANCIENT_SWORD, 1);
		} else if (c == 14) {
			end();
			player.getInventory().addItem(ThroneManager.ANCIENT_CROWN, 1);
		}
	}

	@Override
	public void finish() {

	}

}
