package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.ancientthrone.ThroneManager;
import com.rs.game.player.dialogue.Dialogue;

public class ClaimThrone extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Would you like to claim the Ancient Throne?", "Yes", "No");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 11) {
			end();
			ThroneManager.startClaimingThrone(player);
		} else {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
