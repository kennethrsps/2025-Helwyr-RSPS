package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class CosmeticD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("What Cosmetic Override would you like to buy?",
				(player.getOverrides().paladin ? Colors.green : Colors.red) + "Paladin Cosmetical Override",
				(player.getOverrides().warlord ? Colors.green : Colors.red) + "Warlod Cosmetical Override",
				(player.getOverrides().obsidian ? Colors.green : Colors.red) + "Obisidian Cosmetical Override",
				(player.getOverrides().kalphite ? Colors.green : Colors.red) + "Kalhpite Cosmetical Override",
				"More Options...");

	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (componentId == OPTION_1) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().paladin = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Paladin Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;

				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_2) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().warlord = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Warlord Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;

				} else {
					player.sm("you do not have enought vote points!");
					end();
					return;
				}
			}
			if (componentId == OPTION_3) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().obsidian = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Obsidian Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;

				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_4) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().kalphite = true;
					player.sendMessage("You've purchased: [" + Colors.red + "KALPHITE Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("What Cosmetic Override would you like to buy?",
						(player.getOverrides().demonflesh ? Colors.green : Colors.red)
								+ "Demonflesh Cosmetical Override",
						(player.getOverrides().remokee ? Colors.green : Colors.red) + "Remokee Cosmetical Override",
						(player.getOverrides().assassin ? Colors.green : Colors.red) + "Assassin Cosmetical Override",
						(player.getOverrides().skeleton ? Colors.green : Colors.red) + "Skeleton Cosmetical Override",
						"More Options...");
				stage = 2;
			}
			break;

		case 2:
			if (componentId == OPTION_1) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().demonflesh = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Demonflesh Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;

				}
			}
			if (componentId == OPTION_2) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().remokee = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Remokee Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_3) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().assassin = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Assassin Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_4) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().skeleton = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Skeleton Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("What Cosmetic Override would you like to buy?",
						(player.getOverrides().goth ? Colors.green : Colors.red) + "Goth Cosmetical Override",
						(player.getOverrides().mummy ? Colors.green : Colors.red) + "Mummy Cosmetical Override",
						(player.getOverrides().replicaDragon ? Colors.green : Colors.red)
								+ "Replica Dragon Cosmetical Override",
						(player.getOverrides().sentinel ? Colors.green : Colors.red) + "Sentinel Cosmetical Override",
						"More Options...");

				stage = 3;
				break;
			}
		case 3:
			if (componentId == OPTION_1) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().goth = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Goth Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;

				}
			}
			if (componentId == OPTION_2) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().mummy = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Mummy Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_3) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().replicaDragon = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Replicate Dragon Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_4) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().sentinel = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Sentinel Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("What Cosmetic Override would you like to buy?",
						(player.getOverrides().reaver ? Colors.green : Colors.red) + "Reaver Cosmetical Override",
						(player.getOverrides().hiker ? Colors.green : Colors.red) + "Hiker Cosmetical Override",
						(player.getOverrides().skyguard ? Colors.green : Colors.red)
								+ "Skyguard Dragon Cosmetical Override",
						(player.getOverrides().vyrewatch ? Colors.green : Colors.red) + "Vyrewatch Cosmetical Override",
						"More Options...");
				stage = 4;
				break;
			}
		case 4:
			if (componentId == OPTION_1) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().reaver = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Reaver Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;

				}
			}
			if (componentId == OPTION_2) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().hiker = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Hiker Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_3) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().skyguard = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Skyguard Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_4) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().vyrewatch = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Vyrewatch Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("What Cosmetic Override would you like to buy?",
						(player.getOverrides().snowman ? Colors.green : Colors.red) + "Snowman Cosmetical Override",
						(player.getOverrides().samurai ? Colors.green : Colors.red) + "Samurai Cosmetical Override",
						(player.getOverrides().warmWinter ? Colors.green : Colors.red)
								+ "Warm Winter Cosmetical Override",
						(player.getOverrides().darkLord ? Colors.green : Colors.red) + "Dark Lord Cosmetical Override",
						"More Options...");
				stage = 5;
				break;
			}
		case 5:
			if (componentId == OPTION_1) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().snowman = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Snowman Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;

				}
			}
			if (componentId == OPTION_2) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().samurai = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Samurai Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_3) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().warmWinter = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Warm Winter Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_4) {
				if (!(player.getVotePoints() < 7)) {
					player.setVotePoints(player.getVotePoints() - 7);
					player.getOverrides().darkLord = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Dark Lord Override</col>]. "
							+ "Talk to Xenia to toggle it on/off!");
					end();
					return;
				} else {
					player.sm(Colors.red + "You need 7 Vote Points to purchase this Cosmetic Override!");

					end();
					return;
				}
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("What Cosmetic Override would you like to buy?",
						(player.getOverrides().paladin ? Colors.green : Colors.red) + "Paladin Cosmetical Override",
						(player.getOverrides().warlord ? Colors.green : Colors.red) + "Warlod Cosmetical Override",
						(player.getOverrides().obsidian ? Colors.green : Colors.red) + "Obisidian Cosmetical Override",
						(player.getOverrides().kalphite ? Colors.green : Colors.red) + "Kalhpite Cosmetical Override",
						"More Options...");
				stage = -1;
				break;
			}

		}

	}

	@Override
	public void finish() {

	}
}
