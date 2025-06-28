package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class SkillTeleport extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option", Colors.green+ "Skilling Area", "Mining Area", "Agility Courses", "WoodCutting Area",
				Colors.red + "More Options");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:

			if (componentId == OPTION_1) {
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1376, 5673, 0));
				player.sendMessage(Colors.green +"You can use auto-skilling ;;skilling to start and stop");
				end();
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Mining Area", "Al-Kharid Mine", "Karamja Mining", "Living Rock Caverns",
						"Red Sandstone", Colors.red + "Next");
				stage = 2;

			}
			if (componentId == OPTION_3) {
				sendOptionsDialogue("Agility Course", "Gnome Agility Course", "Barbarian Outpost Agility Course",
						"Pyramid Plunder", Colors.red + "Wilderness </col>Agility Course", Colors.red + "Back");
				stage = 3;
			}
			if (componentId == OPTION_4) {
				sendOptionsDialogue("WoodCutting", "Jungle Woodcutting", "Seer's Village Woodcutting",
						"Sorcerer's Tower", Colors.red + "More Option");
				stage = 4;
			}
			if (componentId == OPTION_5) {
				player.getDialogueManager().startDialogue("SkillTeleport2");
			}
			break;
		case 2:
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(3300, 3312, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2849, 3033, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(3652, 5122, 0));
				end();
			}
			if (componentId == OPTION_4) {
				Magic.vineTeleport(player, new WorldTile(2590, 2880, 0));
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "PureEssence Mining", "Back");
				stage = 5;

			}
			break;
		case 3:
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2470, 3436, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2552, 3563, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(3358, 2828, 0));
				end();
			}
			if (componentId == OPTION_4) {
				Magic.vineTeleport(player, new WorldTile(2998, 3911, 0));
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "Fishihng Guild", "Mining Area", "Agility Courses",
						"WoodCutting Area", Colors.red + "More Options");
				stage = -1;

			}
			break;
		case 4:
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2817, 3083, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2726, 3477, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(2701, 3390, 0));
				end();
			}
			if (componentId == OPTION_4) {
				sendOptionsDialogue("Choose an Option", "Fishihng Guild", "Mining Area", "Agility Courses",
						"WoodCutting Area", Colors.red + "More Options");
				stage = -1;

			}
			break;
		case 5:
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2932, 4821, 0));
				end();
			}

			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose an Option", "Fishihng Guild", "Mining Area", "Agility Courses",
						"WoodCutting Area", Colors.red + "More Options");
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
