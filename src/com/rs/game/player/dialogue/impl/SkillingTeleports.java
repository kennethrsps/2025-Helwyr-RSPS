package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.content.Magic;
import com.rs.game.player.controllers.RunespanController;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class SkillingTeleports extends Dialogue {

	@Override
	public void finish() {

	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2596, 3410, 0));
				end();
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your destination", "Al-kharid Mining", "Karamja Mining",
						"Living rock caverns (LRC)", "Red Sandstone");
				stage = 3;
			}
			if (componentId == OPTION_3) {
				sendOptionsDialogue("Choose your destination", "Gnome Agility Course",
						"Barbarian Outpost Agility Course", Colors.red+"Wilderness </col>Agility Course", "Agility Pyramid");
				stage = 2;
			}
			if (componentId == OPTION_4) {
				sendOptionsDialogue("Choose your destination", "Jungle", "Seer's Village");
				stage = 7;
			}
			if (componentId == OPTION_5) {
				stage = 1;
				sendOptionsDialogue("Choose your destination", "Runecrafting", "Summoning", "Farming", "Hunter",
						Colors.red + "More Options..");
			}
		} else if (stage == 7) {
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2817, 3083, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2726, 3477, 0));
				end();
			}
		} else if (stage == 5) {
			if (componentId == OPTION_1) {
				end();
				if (!player.isCanPvp())
					RunespanController.enterRunespan(player);
				else
					player.sendMessage(Colors.red + "You can't teleport from the wilderness with this!");
			}
			if (componentId == OPTION_2) {
				end();
				Magic.vineTeleport(player, new WorldTile(2598, 3157, 0));
			}
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				stage = 5;
				sendOptionsDialogue("Choose your destination", "Runespan", "Classic Altars");
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2923, 3449, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(3052, 3304, 0));
				end();
			}
			if (componentId == OPTION_4) {
				stage = 6;
				sendOptionsDialogue("Choose your destination", "Falconry", "Feldip hills", "Impetuous Impulses",
						"Isafdar", Colors.red + "More Options..");
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose your destination", "Divination", Colors.red + "First page..");
				stage = 9;
			}
		} else if (stage == 6) {
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2526, 2916, 0));
				end();
			}
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2362, 3623, 0));
				end();
			}
			if (componentId == OPTION_3) {
				if (!player.isCanPvp())
					player.getControlerManager().startControler("PuroPuro");
				else
					player.sendMessage(Colors.red + "You cannot teleport from the wilderness with this!");
				end();
			}
			if (componentId == OPTION_4) {
				Magic.vineTeleport(player, new WorldTile(2251, 3183, 0));
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose your destination", "Tree Gnome Stronghold Hunter area",
						"Port Phasmatys Hunter area", "Rellekka Hunter area", "Desert Quarry Hunter area",
						Colors.red + "More Options..");
				stage = 8;
			}
		} else if (stage == 2) {
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2470, 3436, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(2552, 3563, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(2998, 3911, 0));
				end();
			}
			if (componentId == OPTION_4) {
				Magic.vineTeleport(player, new WorldTile(3358, 2828, 0));
				end();
			}
		} else if (stage == 3) {
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
		} else if (stage == 8) {
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(2457, 3538, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(3660, 3429, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(2729, 3864, 0));
				end();
			}
			if (componentId == OPTION_4) {
				Magic.vineTeleport(player, new WorldTile(3169, 2867, 0));
				end();
			}
			if (componentId == OPTION_5) {
				stage = 6;
				sendOptionsDialogue("Choose your destination", "Falconry", "Feldip hills", "Impetuous Impulses",
						"Isafdar", Colors.red + "More Options..");
			}
		} else if (stage == 9) {
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Choose your destination", "Pale wisp", "Flickering wisp", "Bright wisp",
						"Glowing wisp", Colors.red + "More Options..");
				stage = 10;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your destination", "Fishing Guild", "Mining teleports", "Agility teleports",
						"Woodcutting", Colors.red + "More Options..");
				stage = -1;
			}
		} else if (stage == 10) {
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(3129, 3215, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(3002, 3403, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(3309, 3400, 0));
				end();
			}
			if (componentId == OPTION_4) {
				Magic.vineTeleport(player, new WorldTile(2730, 3420, 0));
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose your destination", "Sparkling wisp", "Gleaming wisp", "Vibrant wisp",
						"Lustrous wisp", Colors.red + "More Options..");
				stage = 11;
			}
		} else if (stage == 11) {
			if (componentId == OPTION_1) {// sparkling
				Magic.vineTeleport(player, new WorldTile(2777, 3598, 0));
				end();
			}
			if (componentId == OPTION_2) {// gleaming
				Magic.vineTeleport(player, new WorldTile(2888, 3041, 0));
				end();
			}
			if (componentId == OPTION_3) {// Vibrant
				Magic.vineTeleport(player, new WorldTile(2418, 2860, 0));
				end();
			}
			if (componentId == OPTION_4) {// Lustrous
				Magic.vineTeleport(player, new WorldTile(3460, 3539, 0));
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose your destination", "Brilliant wisp", "Radiant wisp", "Luminous wisp",
						"Incandescent wisp", Colors.red + "More Options..");
				stage = 12;
			}
		} else if (stage == 12) {
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(3403, 3300, 0));
				end();
			}
			if (componentId == OPTION_2) {
				Magic.vineTeleport(player, new WorldTile(3800, 3551, 0));
				end();
			}
			if (componentId == OPTION_3) {
				Magic.vineTeleport(player, new WorldTile(3309, 2651, 0));
				end();
			}
			if (componentId == OPTION_4) {
				Magic.vineTeleport(player, new WorldTile(2284, 3053, 0));
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose your destination", "Pale wisp", "Flickering wisp", "Bright wisp",
						"Glowing wisp", Colors.red + "More Options..");
				stage = 10;
			}
		}
	}

	@Override
	public void start() {
		sendOptionsDialogue("Choose your destination", "Fishing Guild", "Mining teleports", "Agility teleports",
				"Woodcutting", Colors.red + "More Options..");
	}

}