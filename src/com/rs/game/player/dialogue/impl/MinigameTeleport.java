package com.rs.game.player.dialogue.impl;

import java.awt.Color;

import com.rs.game.WorldTile;
import com.rs.game.activites.zombie.ZombieLobby;
import com.rs.game.player.QuestManager.Quests;
import com.rs.game.player.content.Magic;
import com.rs.game.player.controllers.NomadsRequiem;
import com.rs.game.player.controllers.RunespanController;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class MinigameTeleport extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option", "Barrows", "Clan Wars", "Pest Control", "Dungeoneering",
				Colors.red + "More Options");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:

			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(3563, 3288, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2994, 9679, 0));
				end();

			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(2663, 2653, 0));
				end();
			}
			if (componentId == OPTION_4) {
				stage = 5;
				sendOptionsDialogue("Choose an Option", "New Dungeoneering", "Old Dungeoneering",
						"Back to previous options.");
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "Fight Kiln", "Fight Caves", "Recipe for Disaster",
						"Duel Arena", Colors.red + "More Options");
				stage = 2;
			}
			break;
		case 2:
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(4743, 5170, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(4613, 5129, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(1866, 5346, 0));
				end();
			}
			if (componentId == OPTION_4) {
				Magic.vineTeleport(player, new WorldTile(3325, 3232, 0));
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "Warriors Guild", "Soul Wars", "Dominion Tower", "Livid Farm",
						Colors.red + "More Options");
				stage = 3;

			}
			break;
		case 3:
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2879, 3542, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(3081, 3475, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(3367, 3083, 0));
				end();
			}
			if (componentId == OPTION_4) {
				end();
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2111, 3937, 0));
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "Nomads Requiem", "Artisans Workshop", "Runespan", "Puro-Puro",
						Colors.red + "Back");
				stage = 4;
			}
			break;
		case 4:
			if (componentId == OPTION_1) {
				if (!player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM)) {
					NomadsRequiem.enterNomadsRequiem(player);
					end();
				} else {
					player.sendMessage(Colors.red + "You have already completed Nomad's Quest!");
					end();
					// player.sendMessage("Nomad's Requiem is currently
					// disabled, sorry.");
				}
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(3032, 3338, 0));
				end();
			}
			if (componentId == OPTION_3) {
				if (!canTeleport()) {
					end();
					return;
				}
				RunespanController.enterRunespan(player);
				player.sendMessage("Welcome to Runespan!");
				end();

			}
			if (componentId == OPTION_4) {
				if (!canTeleport()) {
					end();
					return;
				}
				player.getControlerManager().startControler("PuroPuro");
				end();
			}
			if (componentId == OPTION_5) {
				stage = -1;
				sendOptionsDialogue("Choose an Option", "Barrows", "Clan Wars", "Pest Control", "Dungeoneering",
						Colors.red + "More Options");
			}
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				// player.sendMessage("<col=ff0000>New Dungeoneering is
				// currently disabled.</col>");
				Magic.vineTeleport(player, new WorldTile(3449, 3727, 0));
				end();
				break;
			case OPTION_2:
				Magic.vineTeleport(player, new WorldTile(3972, 5561, 0));
				end();
				break;
			case OPTION_3:
				stage = -1;
				sendOptionsDialogue("Choose an Option", "Barrows", "Clan Wars", "Pest Control", "Dungeoneering",
						Colors.red + "More Options");
				break;
			}
			break;
		default:
			end();
		}

	}

	public boolean canTeleport() {
		long currentTime = Utils.currentTimeMillis();
		if (player.getLockDelay() > currentTime)
			return false;
		if (player.getX() >= 2956 && player.getX() <= 3067 && player.getY() >= 5512 && player.getY() <= 5630
				|| (player.getX() >= 2756 && player.getX() <= 2875 && player.getY() >= 5512 && player.getY() <= 5627)) {
			player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
			return false;
		}
		if (!player.getControlerManager().processMagicTeleport(player))
			return false;
		if (!player.getControlerManager().processItemTeleport(player))
			return false;
		if (!player.getControlerManager().processObjectTeleport(player))
			return false;
		return true;
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
