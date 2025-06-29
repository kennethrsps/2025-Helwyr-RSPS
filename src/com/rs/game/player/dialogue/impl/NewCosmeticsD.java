package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

/**
 * Handles the NewCosmeticsD dialogue.
 * 
 * @author Zeus
 */
public class NewCosmeticsD extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = 18808;
		sendOptionsDialogue("What do you want to unlock?",
				(player.getOverrides().paladin ? Colors.green : Colors.red) + "Paladin Cosmetical",
				(player.getOverrides().warlord ? Colors.green : Colors.red) + "Warlord Cosmetical",
				(player.getOverrides().obsidian ? Colors.green : Colors.red) + "Obsidian Cosmetical",
				(player.getOverrides().kalphite ? Colors.green : Colors.red) + "Kalphite Cosmetical",
				" More Options..");
		stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				if (player.getOverrides().paladin == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;

				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().paladin = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Paladin Cosmetical!");
				end();
				break;

			case OPTION_2:
				if (player.getOverrides().warlord == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().warlord = true;
				player.sm(Colors.green + "Congratulations, You've unlocked WardLord Cosmetical!");
				end();

				break;
			case OPTION_3:
				if (player.getOverrides().obsidian == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().obsidian = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Obsidian Cosmetical!");
				end();

				break;
			case OPTION_4:
				if (player.getOverrides().kalphite == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().kalphite = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Kalphite Cosmetical!");
				end();

				break;
			case OPTION_5:
				sendOptionsDialogue("Unlock Color",
						(player.getOverrides().demonflesh ? Colors.green : Colors.red) + "Demonflesh Cosmetical",
						(player.getOverrides().remokee ? Colors.green : Colors.red) + "Remokee Cosmetical",
						(player.getOverrides().assassin ? Colors.green : Colors.red) + "Assasin  Cosmetical",
						(player.getOverrides().skeleton ? Colors.green : Colors.red) + "Skeleton Cosmetical",
						" More Options..");
				stage = 1;
				break;
			}
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				if (player.getOverrides().demonflesh == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().demonflesh = true;
				player.sm(Colors.green + "Congratulations, You've Unlocked Demon Flesh Cosmetical!");
				end();

				break;
			case OPTION_2:
				if (player.getOverrides().remokee == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().remokee = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Remokee Cosmetical!");
				end();

				break;
			case OPTION_3:
				if (player.getOverrides().assassin == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().assassin = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Assain Cosmetical!");
				end();

				break;
			case OPTION_4:
				if (player.getOverrides().skeleton == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().skeleton = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Skeleton Cosmetical!");
				end();

				break;
			case OPTION_5:

				sendOptionsDialogue("Unlock Color",
						(player.getOverrides().goth ? Colors.green : Colors.red) + "Goth  Cosmetical",
						(player.getOverrides().mummy ? Colors.green : Colors.red) + "Mummy Cosmetical",
						(player.getOverrides().sentinel ? Colors.green : Colors.red) + "Replica Dragon Cosmetical",
						(player.getOverrides().replicaDragon ? Colors.green : Colors.red) + "Sentinel Cosmetical",
						" More Options..");
				stage = 2;
				break;
			}
			break;
		case 2:
			switch (componentId)

			{
			case OPTION_1:
				if (player.getOverrides().goth == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().goth = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Goth Cosmetical!");
				end();

				break;
			case OPTION_2:
				if (player.getOverrides().mummy == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().mummy = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Mummy Cosmetical!");
				end();

				break;
			case OPTION_3:
				if (player.getOverrides().sentinel = true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().sentinel = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Sentinel Cosmetical!");
				end();

				break;
			case OPTION_4:
				if (player.getOverrides().replicaDragon = true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().replicaDragon = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Replica Dragon Cosmetical!");
				end();

				break;
			case OPTION_5:
				sendOptionsDialogue("Unlock Color",
						(player.getOverrides().reaver ? Colors.green : Colors.red) + "Reaver Cosmetical",
						(player.getOverrides().hiker ? Colors.green : Colors.red) + "Hiker Cosmetical",
						(player.getOverrides().skyguard ? Colors.green : Colors.red) + "Skyguard Cosmetical",
						(player.getOverrides().vyrewatch ? Colors.green : Colors.red) + "Vyrewatch Cosmetical",
						" More Options..");
				stage = 3;
				break;
			}
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				if (player.getOverrides().reaver = true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().reaver = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Reaver Cosmetical!");
				end();

				break;
			case OPTION_2:
				if (player.getOverrides().hiker = true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().hiker = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Hiker Cosmetical!");
				end();

				break;
			case OPTION_3:
				if (player.getOverrides().skyguard == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().skyguard = true;
				player.sm(Colors.green + "Congratulations, You've unlocked SkyGuard Cosmetical!");
				end();

				break;
			case OPTION_4:
				if (player.getOverrides().vyrewatch == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().vyrewatch = true;
				player.sm(Colors.green + "Congratulations, You've unlocked VyreWatch Cosmetical!");
				end();

				break;
			case OPTION_5:
				sendOptionsDialogue("Unlock Color",
						(player.getOverrides().snowman ? Colors.green : Colors.red) + "Snowman Cosmetical",
						(player.getOverrides().samurai ? Colors.green : Colors.red) + "Samurai Cosmetical",
						(player.getOverrides().warmWinter ? Colors.green : Colors.red) + "Warm Winter Cosmetical",
						(player.getOverrides().darkLord ? Colors.green : Colors.red) + "Dark Lord Cosmetical",
						" More Options..");
				stage = 4;
				break;
			}
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				if (player.getOverrides().snowman == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().snowman = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Snowman Cosmetical!");
				end();

				break;
			case OPTION_2:
				if (player.getOverrides().samurai == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().samurai = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Samurai Cosmetical!");
				end();

				break;
			case OPTION_3:
				if (player.getOverrides().warmWinter == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().warmWinter = true;
				player.sm(Colors.green + "Congratulations, You've unlocked WarmWinter Cosmetical!");
				end();

				break;
			case OPTION_4:
				if (player.getOverrides().darkLord == true) {
					player.sm(Colors.red + "You already have this Cosmetic override!!");
					end();
					return;
				}
				player.getInventory().deleteItem(9477, 1);
				player.getOverrides().darkLord = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Dark Lord Cosmetical!");
				end();

				break;
			case OPTION_5:
				sendOptionsDialogue("What do you want to unlock?",
						(player.getOverrides().paladin ? Colors.green : Colors.red) + "Paladin Cosmetical",
						(player.getOverrides().warlord ? Colors.green : Colors.red) + "Warlord Cosmetical",
						(player.getOverrides().obsidian ? Colors.green : Colors.red) + "Obsidian Cosmetical",
						(player.getOverrides().kalphite ? Colors.green : Colors.red) + "Kalphite Cosmetical",
						" More Options..");
				stage = 5;
				break;
			}
			break;

		}

	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();

	}
}