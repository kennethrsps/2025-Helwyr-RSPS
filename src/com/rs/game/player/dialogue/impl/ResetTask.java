package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

/**
 * Handles the Boss Teleport dialogue.
 * 
 * @author Zeus
 */
public class ResetTask extends Dialogue {
	int npcId;

	@Override
	public void start() {
		stage = 2;
		sendOptionsDialogue("Reset Slayer Task", "Yes, I would like to reset my" + Colors.red + " Slayer Task",
				"No, I don't like to reset my " + Colors.red + "Slayer Task");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (componentId == OPTION_1) {
				stage = 2;
				sendOptionsDialogue("Reset Slayer Task", "Yes, I would like to reset my" + Colors.red + " Slayer Task",
						"No, I don't like to reset my " + Colors.red + "Slayer Task");

			}
			break;
		case 2:
			if (componentId == OPTION_1) {
				long currentTime = Utils.currentTimeMillis();
				String pUsername = Utils.formatString(player.getUsername());
				String tUsername = Utils.formatString(player.getUsername());
				if (player == null) {
					player = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(tUsername));
					if (player != null)
						player.setUsername(Utils.formatPlayerNameForProtocol(tUsername));
				}

				if (!player.getInventory().containsItem(27360, 1)) {
					player.sm(Colors.red + "You do not have Slayer contract scroll in your inventory");
					return;

				}

				if (player == null) {
					return;
				} else {

					if (player.getSlayerManager().getCurrentTask() != null) {
						SerializableFilesManager.savePlayer(player);
						if (player.hasLogedIn())
							player.sendMessage(Colors.red + "Your Slayer Task has been reset!");
								player.getSlayerManager().skipCurrentTask();
						player.getInventory().deleteItem(27360, 1);
						end();
					} else
						sendNPCDialogue(9085, CROOKED_HEAD, tUsername + " you do not have an active Slayer Task!");
					stage = 99;
					return;
				}
			}
			if (componentId == OPTION_2) {
				end();
			}
		case 99:
			end();
			return;
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}
}