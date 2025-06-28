/*package com.rs.game.player.dialogue.impl;

import com.rs.game.Animation;
import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.npc.vorago.VoragoHandler;
import com.rs.game.Hit.HitLook;
import com.rs.game.player.controllers.VoragoLobbyController;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;

public class VoragoChallenge extends Dialogue {

	@Override
	public void start() {
		if (!player.isFirstTime()) {
			sendNPCDialogue(17161, NORMAL, "So impatient you surfacers are. Speak to me first, "
					+ "to engage in battle with me. Unprepared could mean your death.");
			stage = 99;
		} else {
			if (VoragoHandler.getPlayersCount() <= 0) {
				sendNPCDialogue(17161, NORMAL, "There is no fight in progress. " + "Would you like to start one?");
				stage = 0;
			} else {
				sendNPCDialogue(17161, LOOK_DOWN, "Very well. The current instance contains "
						+ VoragoHandler.getPlayersCount() + " player(s)." + "<br>Would you like to join?");
				stage = 10;
			}
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 0:
			sendOptionsDialogue("Challenge Vorago?", "Yes!", "No!");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(17161, GLANCE_DOWN, "Very well.");
				stage = 2;
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case 2:
			if (VoragoHandler.getPlayersCount() > 0) {
				sendNPCDialogue(17161, CALM, "Some challengers are still in the area, thus you cannot "
						+ "start a new challenge against me.");
				stage = 99;
				return;
			}
			if (VoragoHandler.startedChallenge) {
				sendNPCDialogue(17161, CALM, "A challenge has already been started, please wait.");
				stage = 99;
				return;
			}
			VoragoLobbyController.startFight();
			end();
			break;
		case 10:
			sendOptionsDialogue("Join the fight?", "Yes!", "No!");
			stage = 11;
			break;
		case 11:
			switch (componentId) {
			case OPTION_1:
				player.sendMessage(Colors.red+"[Vorago] You get hit by Vorago's raw power.");
				player.setNextWorldTile(new WorldTile(3037, 6120, 0));
				WorldTasksManager.schedule(new WorldTask() {
					int count = 0;

					@Override
					public void run() {
						switch (count) {
						case 1:
							player.setNextFaceWorldTile(new WorldTile(3037, 6125, 0));
							break;
						case 2:
							player.setNextAnimation(new Animation(20389));
							player.applyHit(new Hit(player, 1250, HitLook.CRITICAL_DAMAGE));
							break;
						case 3:
							player.setNextAnimation(new Animation(-1));
							if (player.getHitpoints() <= 0) {
								this.stop();
								return;
							}
							player.getControlerManager().startControler("VoragoController");
							break;
						case 4:
							player.setNextAnimation(new Animation(20401));
							stop();
							break;
						}
						count++;
					}
				}, 0, 1);
				break;
			case OPTION_2:
				end();
				break;
			}
			stage = 99;
			break;
		case 99:
			end();
			break;
		}
	}

	@Override
	public void finish() {
	}
}*/