package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;

public class CosmeticsSelect extends Dialogue {

	private int slotId;

	@Override
	public void start() {
		slotId = (int) this.parameters[0];
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Search outfits by name.", "View all outfits.", "Cancel.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (componentId) {
		case OPTION_1:
			end();
			player.getTemporaryAttributtes().put("CosmeticsKeyWord", slotId);
			player.getPackets().sendInputNameScript("Enter the name of the outfit you would like to search for: ");
			break;
		case OPTION_2:
			end();
			player.getDialogueManager().startDialogue("CosmeticsD", slotId);
			break;
		case OPTION_3:
			end();
			break;
		}
	}

	@Override
	public void finish() {
	}
}
