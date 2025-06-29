package com.rs.game.player.dialogue.impl;

import java.awt.Color;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;

/**
 * Class used to handle the WiseOldMan dialogue.
 * 
 * @author Zeus
 */
public class DonatorShop extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option", "Donator Shop", "Donator Shop 1", "Donator Shop 2",
				"Platinum Members Shop", "Close");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				if (player.getMoneySpent() >= 20) {
					ShopsHandler.openShop(player, 67);
					finish();
					return;
				}
				finish();
				player.sendMessage(Colors.red + "Only Bronze Members ++ can open this shop.");
				break;
			case OPTION_2:
				if (player.getMoneySpent() >= 20) {
					ShopsHandler.openShop(player, 68);
					finish();
					return;
				}
				finish();
				player.sendMessage(Colors.red + "Only Bronze Members ++ can open this shop.");
				break;
			case OPTION_3:
				if (player.getMoneySpent() >= 20) {
					ShopsHandler.openShop(player, 69);
					finish();
					return;
				}
				finish();
				player.sendMessage(Colors.red + "Only Bronze Members ++ can open this shop.");
				break;
			case OPTION_4:
				if (player.getMoneySpent() >= 250) {
					ShopsHandler.openShop(player, 70);
					finish();
					return;
				}
				finish();
				player.sendMessage(Colors.red + "Only Platinum Members ++ can open this shop.");
				break;
			case OPTION_5:
				finish();
				break;
			}
		}
	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}

}