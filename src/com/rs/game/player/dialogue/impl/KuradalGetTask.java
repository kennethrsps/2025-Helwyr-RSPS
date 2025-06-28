package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.SlayerTask;
import com.rs.game.player.content.SlayerTask.Master;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.item.Item;

/**
 * Get-Task option for Kuradal.
 * 
 * @author Zeus
 */
public class KuradalGetTask extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if (!player.hasItem(new Item(4155))) {
			sendNPCDialogue(npcId, 9827, "It seems you don't have a Slayer gem.");
			stage = 80;
		} else
			sendPlayerDialogue(9827, "I need another assignment.");
		stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 80:
			sendItemDialogue(4155, 1, "You have received a Slayer gem from the Slayer Master.");
			player.addItem(new Item(4155));
			stage = 81;
			break;
		case 81:
			sendPlayerDialogue(9827, "I need another assignment.");
			stage = -1;
			break;
		case -1:
			if (player.getTask() == null) {
				SlayerTask.random(player, Master.KURADAL, false);
				sendNPCDialogue(npcId, 9827,
						"Excellent, you're doing great. Your new task is to kill " + player.getTask().getTaskAmount()
								+ " " + player.getTask().getName(player).toLowerCase() + "s.");
				stage = 2;
			} else {
				sendNPCDialogue(npcId, 9827, "You're still hunting " + player.getTask().getName(player).toLowerCase()
						+ "s; come back when you've finished your task.");
				stage = 100;
			}
			break;
		case 2:
		case 100:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}
}