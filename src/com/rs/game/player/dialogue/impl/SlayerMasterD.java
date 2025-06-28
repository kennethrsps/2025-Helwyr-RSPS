package com.rs.game.player.dialogue.impl;

import com.rs.game.player.Skills;
import com.rs.game.player.content.Slayer.SlayerMaster;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;

public class SlayerMasterD extends Dialogue {

	private int npcId;
	private SlayerMaster master;
	private int slayerShopId = 14;
	private SlayerMaster currentMaster;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		master = (SlayerMaster) parameters[1];
		sendNPCDialogue(npcId, 9827, "'Ello and what are you after then?");
		if (player.getSlayerManager().getCurrentMaster() != master)
			stage = 2;
		else
			stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (currentMaster == SlayerMaster.KURADAL && player.getSlayerManager().getCurrentTask() != null) {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "How many monsters do I have left?",
						"What do you have in your shop?", "Give me a tip.", "Check slayer rewards.",
						"Check Special slayer Shop.");
				stage = 0;

			} else {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Please give me a task.", "What do you have in your shop?",
						"Check slayer rewards.", "Check Special slayer Shop.");
				stage = 1;
			}
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				player.getSlayerManager().checkKillsLeft();
				end();
			} else if (componentId == OPTION_2) {
				sendNPCDialogue(npcId, 9827, "Only the best slayer equipment money could buy. Come check it out.");
				stage = 5;
			} else if (componentId == OPTION_3) {
				stage = 6;
				if (player.getSlayerManager().getCurrentTask() == null) {
					sendNPCDialogue(npcId, 9827, "You currently don't have a task.");
					return;
				}
				String[] tipDialouges = player.getSlayerManager().getCurrentTask().getTips();
				if (tipDialouges != null && tipDialouges.length != 0) {
					String chosenDialouge = tipDialouges[Utils.random(tipDialouges.length)];
					if (chosenDialouge == null || chosenDialouge.equals(""))
						sendNPCDialogue(npcId, 9827, "I don't have any tips for you currently.");
					else
						sendNPCDialogue(npcId, 9827, chosenDialouge);
				} else
					sendNPCDialogue(npcId, 9827, "I don't have any tips for you currently.");
			} else if (componentId == OPTION_4) {
				stage = 7;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Slayer helm (400 points)", "Charm collector (650 points)",
						"Imbue ring (300 points)", "Crystal Weapon Seed (200 points)", "Fighter's torso (200 points)");
			} else if (currentMaster == SlayerMaster.KURADAL) {
				end();
			} else {
				ShopsHandler.openShop(player, 115);
				end();
			}
		} else if (stage == 1) {
			if (componentId == OPTION_1) {
				player.getSlayerManager().setCurrentTask(true);
				sendNPCDialogue(npcId, 9827,
						"Your new assignment is: " + player.getSlayerManager().getCurrentTask().getName() + "; only "
								+ player.getSlayerManager().getCount() + " more to go.");
				stage = 6;
			} else if (componentId == OPTION_2) {
				sendNPCDialogue(npcId, 9827, "Only the best slayer equipment money could buy. Come check it out.");
				stage = 5;
			} else if (componentId == OPTION_3) {
				stage = 7;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Slayer helm (400 points)", "Charm collector (650 points)",
						"Imbue ring (300 points)", "Crystal Weapon Seed (200 points)", "Fighter's torso (200 points)");
			} else if (currentMaster == SlayerMaster.KURADAL) {
				end();
			} else {
				ShopsHandler.openShop(player, 115);
				end();
			}
		} else if (stage == 2) {
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Can you become my master?", "What do you have in your shop?",
					"Check slayer rewards.", "Check Special slayer Shop.");
			stage = 3;
		} else if (stage == 3) {
			if (componentId == OPTION_1) {
				stage = 6;
				if (player.getSlayerManager().getCurrentTask() != null) {
					sendNPCDialogue(npcId, 9827, "I don't think that "
							+ player.getSlayerManager().getCurrentMaster().toString().toLowerCase()
							+ " would be happy if I took one of his students just like that. Complete your task then return to me.");
				} else if (player.getSkills().getCombatLevelWithSummoning() < master.getRequiredCombatLevel())
					sendNPCDialogue(npcId, 9827, "Your too weak overall, come back when you've become stronger.");
				else if (player.getSkills().getLevel(Skills.SLAYER) < master.getRequiredSlayerLevel()) {
					sendNPCDialogue(npcId, 9827,
							"Your slayer level is too low to take on my challenges, come back when you have a level of at least "
									+ master.getRequiredSlayerLevel() + " slayer.");
				} else {
					sendNPCDialogue(npcId, 9827,
							"You meet my requirements, so therefore I will agree to leading you. Good luck adventurer, you will need it.");
					player.getSlayerManager().setCurrentMaster(master);
				}
			} else if (componentId == OPTION_2) {
				stage = 5;
				sendNPCDialogue(npcId, 9827, "Only the best slayer equipment money could buy. Come check it out.");
			} else if (componentId == OPTION_3) {
				stage = 7;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Slayer helm (400 points)", "Charm collector (650 points)",
						"Imbue ring (300 points)", "Crystal Weapon Seed (200 points)", "Fighter's torso (200 points)");
			} else if (currentMaster == SlayerMaster.KURADAL) {
				end();
			} else {
				ShopsHandler.openShop(player, 115);
				end();

			}
		} else if (stage == 5)

		{
			ShopsHandler.openShop(player, slayerShopId);
			end();
		} else if (stage == 6)

			end();
		else if (stage == 7)

		{
			if (componentId == OPTION_1) {
				stage = 8;
				sendOptionsDialogue("Do you wish to buy a Slayer Helmet?", "Yes", "No");
			} else if (componentId == OPTION_2) {
				stage = 9;
				sendOptionsDialogue("Do you wish to unlock the Charm Collector perk?", "Yes", "No");
			} else if (componentId == OPTION_3) {
				stage = 10;
				sendDialogue("This feature imbues one of your rings for 300 slayer points, "
						+ "making it more powerful but <col=ff0000>permanently untradeable</col>.");
			} else if (componentId == OPTION_4) {
				stage = 12;
				sendItemDialogue(32625, 1, "You can use this item on a crystal weapon of your choice to permanently "
						+ "attune (upgrade) it making it more powerful.");
			} else if (componentId == OPTION_5) {
				stage = 14;
				sendOptionsDialogue("Do you want to buy a fighter's torso?", "Yes", "No");
			}
		} else if (stage == 8) {
			end();
			if (componentId == OPTION_1) {
				if (canBuyItem(400)) {
					player.getInventory().addItem(13263, 1);
					player.getSlayerManager().setPoints(player.getSlayerManager().getSlayerPoints() - 400);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You have bought a Slayer helmet for 400 slayer points!");
				}
			}
		} else if (stage == 9) {
			end();
			if (componentId == OPTION_1) {
				if (canBuyItem(650)) {
					player.getPerkManager().charmCollector = true;
					player.getSlayerManager().setPoints(player.getSlayerManager().getSlayerPoints() - 650);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You have unlocked the Charm Collector perk for 650 slayer points! "
									+ "It works automatically and is not needed to configure.");
				}
			}
		} else if (stage == 10) {
			stage = 11;
			sendOptionsDialogue("Which ring do you want to imbue?", "Archers' ring", "Seers' ring", "Warrior ring",
					"Berserker ring", "Onyx ring");
		} else if (stage == 11) {
			end();
			if (componentId == OPTION_1) {
				if (canImbueRing(6733)) {
					player.getInventory().deleteItem(6733, 1);
					player.getSlayerManager().setPoints(player.getSlayerManager().getSlayerPoints() - 300);
					player.getInventory().addItem(15019, 1);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"Your ring has been successfully imbued!");
				}
			} else if (componentId == OPTION_2) {
				if (canImbueRing(6731)) {
					player.getInventory().deleteItem(6731, 1);
					player.getSlayerManager().setPoints(player.getSlayerManager().getSlayerPoints() - 300);
					player.getInventory().addItem(15018, 1);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"Your ring has been successfully imbued!");
				}
			} else if (componentId == OPTION_3) {
				if (canImbueRing(6735)) {
					player.getInventory().deleteItem(6735, 1);
					player.getSlayerManager().setPoints(player.getSlayerManager().getSlayerPoints() - 300);
					player.getInventory().addItem(15020, 1);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"Your ring has been successfully imbued!");
				}
			} else if (componentId == OPTION_4) {
				if (canImbueRing(6737)) {
					player.getInventory().deleteItem(6737, 1);
					player.getSlayerManager().setPoints(player.getSlayerManager().getSlayerPoints() - 300);
					player.getInventory().addItem(15220, 1);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"Your ring has been successfully imbued!");
				}
			} else if (componentId == OPTION_5) {
				if (canImbueRing(6575)) {
					player.getInventory().deleteItem(6575, 1);
					player.getSlayerManager().setPoints(player.getSlayerManager().getSlayerPoints() - 300);
					player.getInventory().addItem(15017, 1);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"Your ring has been successfully imbued!");
				}
			}
		} else if (stage == 12) {
			stage = 13;
			sendOptionsDialogue("Do you wish to buy a crystal weapon seed?", "Yes", "No");
		} else if (stage == 13) {
			end();
			if (componentId == OPTION_1) {
				if (canBuyItem(200)) {
					player.getInventory().addItem(32625, 1);
					player.getSlayerManager().setPoints(player.getSlayerManager().getSlayerPoints() - 200);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You have bought an Attuned crystal weapon seed for 200 slayer points!");
				}
				return;
			}
		} else if (stage == 14) {
			end();
			if (componentId == OPTION_1) {
				if (canBuyItem(200)) {
					player.getInventory().addItem(10551, 1);
					player.getSlayerManager().setPoints(player.getSlayerManager().getSlayerPoints() - 200);
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You have purchased a fighter's torso for 200 slayer points!");
				}
			}
		}
	}

	private boolean canBuyItem(int points) {
		if (player.getSlayerManager().getSlayerPoints() >= points) {
			if (player.getInventory().hasFreeSlots()) {
				return true;
			}
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You do not have enough free inventory space to buy this.");
			return false;
		}
		player.getDialogueManager().startDialogue("SimpleMessage", "You do not have enough slayer points to buy this.");
		return false;
	}

	private boolean canImbueRing(int ringId) {
		if (player.getSlayerManager().getSlayerPoints() >= 300) {
			if (player.getInventory().containsItem(ringId, 1)) {
				return true;
			}
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You must have the ring in your inventory to imbue it.");
			return false;
		}
		player.getDialogueManager().startDialogue("SimpleMessage", "You do not have enough slayer points to buy this.");
		return false;
	}

	@Override
	public void finish() {

	}

}
