package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

/**
 * Handles the NewSkinsD dialogue.
 * 
 * @author Zeus
 */
public class NewSkinsD extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = 18808;
		sendOptionsDialogue("Unlock Color", (player.getPerkManager().red ? Colors.green : Colors.red) + "Black",
				(player.getPerkManager().voragoblue ? Colors.green : Colors.red) + "Vorago Blue",
				(player.getPerkManager().brassicaprimegreen ? Colors.green : Colors.red) + "Brassica Prime Green",
				(player.getPerkManager().green ? Colors.green : Colors.red) + "Green", Colors.red + " More Options..");
		stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				if (player.getPerkManager().red == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;

				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().red = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Red Skin Skin!");
				end();
				break;

			case OPTION_2:
				if (player.getPerkManager().voragoblue == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().voragoblue = true;
				player.sm(Colors.green + "Congratulations, You've unlocked voragoblue Skin!");
				end();

				break;
			case OPTION_3:
				if (player.getPerkManager().brassicaprimegreen == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().brassicaprimegreen = true;
				player.sm(Colors.green + "Congratulations, You've unlocked brassicaprimegreen Skin!");
				end();

				break;
			case OPTION_4:
				if (player.getPerkManager().green == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().green = true;
				player.sm(Colors.green + "Congratulations, You've unlocked green Skin!");
				end();

				break;
			case OPTION_5:
				sendOptionsDialogue("Unlock Color",
						(player.getPerkManager().nexred ? Colors.green : Colors.red) + "Nex Red",
						(player.getPerkManager().blue ? Colors.green : Colors.red) + "Light Blue",
						(player.getPerkManager().zarospurple ? Colors.green : Colors.red) + "Zaros Purple",
						(player.getPerkManager().pink ? Colors.green : Colors.red) + "Pink",
						Colors.red + " More Options..");
				stage = 1;
				break;
			}
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				if (player.getPerkManager().nexred == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().nexred = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Nex Red Skin!");
				end();

				break;
			case OPTION_2:
				if (player.getPerkManager().blue == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().blue = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Blue Skin!");
				end();

				break;
			case OPTION_3:
				if (player.getPerkManager().zarospurple == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().zarospurple = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Zaros Purple Skin!");
				end();

				break;
			case OPTION_4:
				if (player.getPerkManager().pink == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().pink = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Pink Skin!");
				end();

				break;
			case OPTION_5:

				sendOptionsDialogue("Unlock Color",
						(player.getPerkManager().orange ? Colors.green : Colors.red) + "Orange",
						(player.getPerkManager().yellow ? Colors.green : Colors.red) + "Yellow",
						(player.getPerkManager().grey ? Colors.green : Colors.red) + "Grey",
						(player.getPerkManager().black ? Colors.green : Colors.red) + "Black",
						Colors.red + " More Options..");
				stage = 2;
				break;
			}
			break;
		case 2:
			switch (componentId)

			{
			case OPTION_1:
				if (player.getPerkManager().orange == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().orange = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Orange Skin!");
				end();

				break;
			case OPTION_2:
				if (player.getPerkManager().yellow == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().yellow = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Yellow Skin!");
				end();

				break;
			case OPTION_3:
				if (player.getPerkManager().grey = true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().grey = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Grey Skin!");
				end();

				break;
			case OPTION_4:
				if (player.getPerkManager().black = true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().black = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Black Skin!");
				end();

				break;
			case OPTION_5:
				sendOptionsDialogue("Unlock Color",
						(player.getPerkManager().green ? Colors.green : Colors.red) + "Green",
						(player.getPerkManager().saradominblue ? Colors.green : Colors.red) + "Saradomin Blue",
						(player.getPerkManager().armadylyellow ? Colors.green : Colors.red) + "Armadyl Yellow",
						(player.getPerkManager().purple ? Colors.green : Colors.red) + "Purple",
						Colors.red + " More Options..");
				stage = 3;
				break;
			}
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				if (player.getPerkManager().green = true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().green = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Green Skin!");
				end();

				break;
			case OPTION_2:
				if (player.getPerkManager().saradominblue = true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().saradominblue = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Saradomin Blue Skin!");
				end();

				break;
			case OPTION_3:
				if (player.getPerkManager().armadylyellow == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().armadylyellow = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Armadyl Yellow Skin!");
				end();

				break;
			case OPTION_4:
				if (player.getPerkManager().purple == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().purple = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Purple Skin!");
				end();

				break;
			case OPTION_5:
				sendOptionsDialogue("Unlock Color",
						(player.getPerkManager().brassicaprimegreen ? Colors.green : Colors.red)
								+ "Brassica Prime Green",
						(player.getPerkManager().zamorakred ? Colors.green : Colors.red) + "Zamorak Red",
						(player.getPerkManager().serenblue ? Colors.green : Colors.red) + "Seren Blue",
						(player.getPerkManager().araxxorgrey ? Colors.green : Colors.red) + "Araxxor Grey",
						Colors.red + " More Options..");
				stage = 4;
				break;
			}
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				if (player.getPerkManager().brassicaprimegreen == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().brassicaprimegreen = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Brassica Prime Green Skin!");
				end();

				break;
			case OPTION_2:
				if (player.getPerkManager().zamorakred == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().zamorakred = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Zamorak Red Skin!");
				end();

				break;
			case OPTION_3:
				if (player.getPerkManager().serenblue == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().serenblue = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Seren Blue Skin!");
				end();

				break;
			case OPTION_4:
				if (player.getPerkManager().araxxorgrey == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().araxxorgrey = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Araxxor Grey Skin!");
				end();

				break;
			case OPTION_5:
				sendOptionsDialogue("Unlock Color",
						(player.getPerkManager().voragoblue ? Colors.green : Colors.red) + "Vorago Blue",
						(player.getPerkManager().kalphitekingorange ? Colors.green : Colors.red)
								+ "Kalphite King Orange",
						(player.getPerkManager().qbdblue ? Colors.green : Colors.red) + "Queen Black Dragon Blue",
						Colors.red + " Back..");
				stage = 5;
				break;
			}
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				if (player.getPerkManager().voragoblue == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().voragoblue = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Vorago Blue Skin!");
				end();

				break;
			case OPTION_2:
				if (player.getPerkManager().kalphitekingorange == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().kalphitekingorange = true;
				player.sm(Colors.green + "Congratulations, You've unlocked Kalphite King Orange Skin!");
				end();

				break;
			case OPTION_3:
				if (player.getPerkManager().qbdblue == true) {
					player.sm(Colors.red + "You already have this Color!!");
					end();
					return;
				}
				player.getInventory().deleteItem(34233, 1);
				player.getPerkManager().qbdblue = true;
				player.sm(Colors.green + "Congratulations, You've unlocked QBD Blue Skin!");
				end();

				break;
			case OPTION_4:
				sendOptionsDialogue("Unlock Color", (player.getPerkManager().red ? Colors.green : Colors.red) + "Black",
						(player.getPerkManager().voragoblue ? Colors.green : Colors.red) + "Vorago Blue",
						(player.getPerkManager().brassicaprimegreen ? Colors.green : Colors.red)
								+ "Brassica Prime Green",
						(player.getPerkManager().green ? Colors.green : Colors.red) + "Green",
						Colors.red + " More Options..");
				stage = -1;
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