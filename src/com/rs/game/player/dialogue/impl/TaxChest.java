package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.ancientthrone.Taxation;
import com.rs.game.player.content.ancientthrone.ThroneManager;
import com.rs.game.player.dialogue.Dialogue;

public class TaxChest extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Tax Chest", "View Taxes", "Collect Taxes");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 11) {
			end();
			Taxation.displayTaxes(player);
		} else {
			if (!ThroneManager.getThrone().getKing().equals(player.getUsername())) {
				end();
				player.getDialogueManager().startDialogue("SimpleMessage", "Only the King/Queen can collect taxes.");
				return;
			}
			end();
			Taxation.collectTaxes(player);
		}
	}

	@Override
	public void finish() {

	}

}
