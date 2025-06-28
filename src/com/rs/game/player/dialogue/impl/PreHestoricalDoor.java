package com.rs.game.player.dialogue.impl;

import com.rs.game.Animation;
import com.rs.game.WorldTile;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class PreHestoricalDoor extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Pick an option", "Enter room.", "Check damage increase.", "What is this ?");
		stage = 2;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 2) {
			if (componentId == OPTION_1) {
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						FadingScreen.fade(player, 0, new Runnable() {

							@Override
							public void run() {
								player.unlock();
								player.setNextAnimation(new Animation(-1));
								player.setNextWorldTile(new WorldTile(3810, 4719, 0));
							}
						});
					}
				}, 0);
				end();
			} else if (componentId == OPTION_2) {
				sendDialogue("The Dark Lord his attacks deal  " + player.getPAdamage() + " extra damage against you.");
				stage = 3;
			} else if (componentId == OPTION_3) {
				sendDialogue(
						"this mystical creature is captured and trained by admin, his power increases with every kill. The increased damage resets every day.");
				stage = 3;
			}
		} else if (stage == 3)
			end();

	}

	@Override
	public void finish() {

	}
}
