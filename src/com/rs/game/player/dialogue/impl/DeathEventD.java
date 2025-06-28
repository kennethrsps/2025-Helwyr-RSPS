package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;

public class DeathEventD extends Dialogue {

	@Override
	public void start() {
		sendNPCDialogue(14386, CROOKED_HEAD, "Before I let you go, you must choose whether you wish to pay a price to keep your items or have them drop at your gravestone.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1)
			sendOptionsDialogue("Select an Option", "Pay the price of " + player.getDeathManager().getCostOfItemsLost() + " coins.", "Drop the items under my gravestone.");
		else if (stage == 2)
			end();
		else {
			if (componentId == OPTION_1) {
				if (player.getMoneyPouch().getTotal() >= player.getDeathManager().getCostOfItemsLost()) {
					player.getMoneyPouch().removeAmount(player.getDeathManager().getCostOfItemsLost());
					Magic.sendObjectTeleportSpell(player, true, player.getHomeTile());
					player.getDeathManager().finish();
					end();
				} else
					sendNPCDialogue(14386, CROOKED_HEAD, "You do not have enough coins to pay for your items.");
			} else {
				player.getDeathManager().sendInterface();
				end();
			}
		}
		stage++;
	}

	@Override
	public void finish() {
		
	}

}
