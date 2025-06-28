package com.rs.game.player.dialogue.impl;

import com.rs.Settings;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;

/**
 * Class used to handle the Xuan's dialogue.
 * 
 * @author Zeus
 */
public class XuanD extends Dialogue {

	/**
	 * Ints representing the NPC id and the stage of the dialogue.
	 */
	int npcId, stage;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		stage = (Integer) parameters[1];
		if (stage == 1) {
			sendNPCDialogue(npcId, NORMAL, "Hello, what can I do for you?");
			stage = 1;
		} else {
			sendOptionsDialogue("Choose an Option", "Regular Aura shop", "Greater Aura shop", "Master Aura shop",
					"Supreme Aura shop", "More options...");
			stage = 4;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 1:
			sendPlayerDialogue(NORMAL, "Hello.. what could you do for me..?");
			stage = 2;
			break;
		case 2:
			sendNPCDialogue(npcId, NORMAL,
					"I offer a wide variety of Auras that you can use to help your " + "game-play throughout "
							+ Settings.SERVER_NAME + ". Donators also have access to " + "higher-tier auras.");
			stage = 3;
			break;
		case 3:
			sendOptionsDialogue("Choose an Option", "Regular Aura shop", "Greater Aura shop", "Master Aura shop",
					"Supreme Aura shop");
			stage = 4;
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				if (Utils.getMinutesPlayed(player) <= 1) {
					stage = 3;
					return;
				}
				finish();
				ShopsHandler.openShop(player, 53);
				break;
			case OPTION_2:
				if (Utils.getMinutesPlayed(player) <= 1) {
					stage = 3;
					return;
				}
				finish();
				ShopsHandler.openShop(player, 54);
				break;
			case OPTION_3:
				if (!player.isBronze()) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but master tier auras are only available to Bronze members and higher..");
					stage = 3;
					return;
				}
				finish();
				ShopsHandler.openShop(player, 55);
				break;
			case OPTION_4:
				if (!player.isGold()) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but supreme tier auras are only available to Gold members and higher..");
					stage = 3;
					return;
				}
				finish();
				ShopsHandler.openShop(player, 56);
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Option", "...","...","...","...","..."); //Legendary Aura shop100th Hour wings
				stage = 5;
			}
		case 5:
			switch (componentId) {
			/**case OPTION_1:
				if (!player.isUltimateDonator()) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but legendary tier auras are only available to Diamond members..");
					stage = 3;
					return;
				}

				finish();
				ShopsHandler.openShop(player, 57);
				break;**/
			/**case OPTION_5:
				if (Utils.getMinutesPlayed(player) <= 6000) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but this is only available to players who have morethan 100 hours of playtime");
					stage = 3;
					return;
				}
				finish();
				ShopsHandler.openShop(player, 114);
				break;**/
			}
		}

	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}
}