package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

/**
 * Handles the ChameleonExtractD dialogue.
 * 
 * @author Zeus
 */
public class ChameleonExtractD extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = 18808;
		sendOptionsDialogue("Choose Color", (player.getPerkManager().red ? Colors.green : Colors.red) + "Black",
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
				if (player.getPerkManager().red) {
					player.getGlobalPlayerUpdater().setSkinColor(11);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_2:
				if (player.getPerkManager().voragoblue) {
					player.getGlobalPlayerUpdater().setSkinColor(12);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_3:
				if (player.getPerkManager().brassicaprimegreen) {
					player.getGlobalPlayerUpdater().setSkinColor(13);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_4:
				if (player.getPerkManager().green) {
					player.getGlobalPlayerUpdater().setSkinColor(14);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_5:
				sendOptionsDialogue("Choose Color",
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
				if (player.getPerkManager().nexred) {
					player.getGlobalPlayerUpdater().setSkinColor(15);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_2:
				if (player.getPerkManager().blue) {
					player.getGlobalPlayerUpdater().setSkinColor(16);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_3:

				if (player.getPerkManager().zarospurple) {
					player.getGlobalPlayerUpdater().setSkinColor(17);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_4:
				if (player.getPerkManager().pink) {
					player.getGlobalPlayerUpdater().setSkinColor(18);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_5:

				sendOptionsDialogue("Choose Color",
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
				if (player.getPerkManager().orange) {
					player.getGlobalPlayerUpdater().setSkinColor(19);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_2:
				if (player.getPerkManager().yellow) {
					player.getGlobalPlayerUpdater().setSkinColor(20);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_3:
				if (player.getPerkManager().grey) {
					player.getGlobalPlayerUpdater().setSkinColor(21);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_4:
				if (player.getPerkManager().black) {
					player.getGlobalPlayerUpdater().setSkinColor(22);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_5:
				sendOptionsDialogue("Choose Color",
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
				if (player.getPerkManager().green) {
					player.getGlobalPlayerUpdater().setSkinColor(23);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_2:
				if (player.getPerkManager().saradominblue) {
					player.getGlobalPlayerUpdater().setSkinColor(24);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_3:
				if (player.getPerkManager().armadylyellow) {
					player.getGlobalPlayerUpdater().setSkinColor(25);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_4:
				if (player.getPerkManager().purple) {
					player.getGlobalPlayerUpdater().setSkinColor(26);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_5:
				sendOptionsDialogue("Choose Color",
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
				if (player.getPerkManager().brassicaprimegreen) {
					player.getGlobalPlayerUpdater().setSkinColor(27);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_2:
				if (player.getPerkManager().zamorakred) {
					player.getGlobalPlayerUpdater().setSkinColor(28);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_3:
				if (player.getPerkManager().serenblue) {
					player.getGlobalPlayerUpdater().setSkinColor(29);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_4:
				if (player.getPerkManager().araxxorgrey) {
					player.getGlobalPlayerUpdater().setSkinColor(30);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_5:
				sendOptionsDialogue("Choose Color",
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
				if (player.getPerkManager().voragoblue) {
					player.getGlobalPlayerUpdater().setSkinColor(31);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_2:
				if (player.getPerkManager().kalphitekingorange) {
					player.getGlobalPlayerUpdater().setSkinColor(32);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_3:
				if (player.getPerkManager().qbdblue) {
					player.getGlobalPlayerUpdater().setSkinColor(34);
					player.getGlobalPlayerUpdater().generateAppearenceData();
					end();
				} else {
					player.sm(Colors.red + "You need to purchase this color to be able to use it.");
					end();
				}
				return;
			case OPTION_4:
				sendOptionsDialogue("Choose Color", (player.getPerkManager().red ? Colors.green : Colors.red) + "Black",
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