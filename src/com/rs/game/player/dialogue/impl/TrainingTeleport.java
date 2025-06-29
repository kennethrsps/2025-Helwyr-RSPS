package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class TrainingTeleport extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option", "East RockCrabs", "Glacor Cave", "Dwarf BattleField", "Dragons",
				Colors.red + "More Options");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:

			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2710, 3710, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(4181, 5726, 0));
				end();

			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(1519, 4704, 0));
				end();
			}
			if (componentId == OPTION_4) {

				sendOptionsDialogue("Choose an Option", "Frost Draogns", "Celestial Dragons", Colors.red + "Back");
				stage = 20;
			}

			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "Kuradal's Dungeon", "Jadinko Lair", "Polypore Dungeon",
						"Slayer Tower", Colors.red + "More Options");
				stage = 2;

			}
			break;
		case 20:
			if (componentId == OPTION_1) {
				if (player.getSkills().getLevel(Skills.DUNGEONEERING) < 85) {
					player.sendMessage("This area requires at least level 85 Dungeoneering to access!");
					end();
					return;
				}
				Magic.vineTeleport(player, new WorldTile(1298, 4510, 0)); // frosts
				end();
			}
			if (componentId == OPTION_2) {
				if (player.getSkills().getLevel(Skills.DUNGEONEERING) < 95) {
					player.sendMessage("This area requires at least level 95 Dungeoneering to access!");
					end();
					return;
				}
				Magic.vineTeleport(player, new WorldTile(2285, 5972, 0)); // celestial Dragons
				end();
			}
			if (componentId == OPTION_3) {
			sendOptionsDialogue("Choose an Option", "East RockCrabs", "Glacor Cave", "Dwarf BattleField", "Dragons",
					Colors.red + "More Options");
			stage = -1;
			}
			break;
		case 2:
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(1690, 5286, 1));
				end();
			}
			if (componentId == OPTION_2) {
				if (player.getSkills().getLevel(Skills.SLAYER) < 80) {
					player.sendMessage("You need a slayer level of 80 to use this teleport.");
					end();
					return;
				}
				Magic.vineTeleport(player, new WorldTile(3012, 9274, 0)); // jadinko
																			// lair
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(4625, 5457, 3));
				end();
			}
			if (componentId == OPTION_4) {
				Magic.vineTeleport(player, new WorldTile(3423, 3543, 0));
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "Ancient Cavern", "Dungeon Area", "Strykewyrms Area",
						"West RockCrabs", Colors.red + "Back");
				stage = 3;

			}
			break;
		case 3:
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(1763, 5365, 1));
				end();
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Dungeon Area", "Brimhaven Dungeon", "Fremennik Dungeon", "Taverley Dungeon","Ascension Dungeon",
						Colors.red + "Back");
				stage = 4;

			}
			if (componentId == OPTION_3) {
				sendOptionsDialogue("Strykewyrms Area", "Jungle Strykewyrms", "Desert Strykewyrms", "Ice Strykewyrms",
						Colors.red + "Back");
				stage = 5;

			}
			if (componentId == OPTION_4) {
				Magic.vineTeleport(player, new WorldTile(2672, 3710, 0));
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "East RockCrabs", "Glacor Cave", "Dwarf BattleField", "Dragons",
						Colors.red + "More Options");
				stage = -1;

			}
			break;
		case 4:
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2699, 9564, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2808, 10002, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(2884, 9799, 0));
				end();
			}
			if (componentId == OPTION_4) {
				Magic.vineTeleport(player, new WorldTile(2508, 2886, 0));
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "East RockCrabs", "Glacor Cave", "Dwarf BattleField", "Dragons",
						Colors.red + "Back");
				stage = -1;

			}
			break;

		case 5:
			if (componentId == OPTION_1) {
				if (player.getSkills().getLevelForXp(Skills.SLAYER) < 73) {
					player.sendMessage("You need at least a level of 73 Slayer to go there!");
					end();
					return;
				}
				Magic.vineTeleport(player, new WorldTile(2452, 2911, 0));
			}
			if (componentId == OPTION_2) {
				if (player.getSkills().getLevelForXp(Skills.SLAYER) < 77) {
					player.sendMessage("You need at least a level of 77 Slayer to go there!");
					return;
				}
				Magic.vineTeleport(player, new WorldTile(3356, 3160, 0));
			}
			if (componentId == OPTION_3) {
				if (player.getSkills().getLevelForXp(Skills.SLAYER) < 93) {
					player.sendMessage("You need at least a level of 93 Slayer to go there!");
					return;
				}
				Magic.vineTeleport(player, new WorldTile(3435, 5648, 0));
			}
			if (componentId == OPTION_4) {
				sendOptionsDialogue("Choose an Option", "East RockCrabs", "Glacor Cave", "Dwarf BattleField", "Dragons",
						Colors.red + "Back");
				stage = -1;

			}
			break;
		default:
			end();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
