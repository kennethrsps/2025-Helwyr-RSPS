/*package com.rs.game.player.dialogue.impl;

import com.rs.game.player.Skills;
import com.rs.game.player.content.SlayerTask;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

*//**
 * Handles all slayer reward confirmations/purchases.
 * @author Zeus
 *//*
public class SlayerRewards extends Dialogue {
	
	*//**
	 * Reward name.
	 *//*
	String name;
	
	*//**
	 * Point cost.
	 *//*
	int points;

	@Override
	public void start() {
		name = (String) parameters[0];
		points = (int) parameters[1];
		if (name.equalsIgnoreCase("helm")) {
			sendOptionsDialogue("Do you wish to buy a Slayer Helmet?", "Yes", "No");
			stage = 1;
			return;
		}
		if (name.equalsIgnoreCase("xp")) {
			sendOptionsDialogue("Do you wish to buy 10'000 Slayer experience?", "Yes", "No");
			stage = 2;
			return;
		}
		if (name.equalsIgnoreCase("ring")) {
			sendOptionsDialogue("Do you wish to buy a Ring of Slaying?", "Yes", "No");
			stage = 3;
			return;
		}
		if (name.equalsIgnoreCase("runes")) {
			sendOptionsDialogue("Do you wish to buy runes for 250 Slayer dart casts?", "Yes", "No");
			stage = 4;
			return;
		}
		if (name.equalsIgnoreCase("bolts")) {
			sendOptionsDialogue("Do you wish to buy 250 broad-tipped bolts?", "Yes", "No");
			stage = 5;
			return;
		}
		if (name.equalsIgnoreCase("arrows")) {
			sendOptionsDialogue("Do you wish to buy 250 broad-tipped arrows?", "Yes", "No");
			stage = 6;
			return;
		}
		if (name.equalsIgnoreCase("cancel")) {
			sendOptionsDialogue("Do you wish to skip your current assignment?", "Yes", "No");
			stage = 7;
			return;
		}
		if (name.equalsIgnoreCase("charmcol")) {
			sendOptionsDialogue("Do you wish to unlock the Charm Collector perk?", "Yes", "No");
			stage = 8;
			return;
		}
		if (name.equalsIgnoreCase("imbue")) {
			sendItemDialogue(6737, 1, "This feature imbues one of your Dagannoth king rings for 300 slayer points, "
					+ "making it more powerful but <col=ff0000>permanently untradeable</col>.");
			stage = 15;
			return;
		}
		if (name.equalsIgnoreCase("imbueonyx")) {
			sendItemDialogue(6575, 1, "This feature imbues your Onyx ring for 300 slayer points, "
					+ "making it more powerful but <col=ff0000>permanently untradeable</col>.");
			stage = 17;
			return;
		}
		if (name.equalsIgnoreCase("seed")) {
			sendItemDialogue(32625, 1, "You can use this item on a crystal weapon of your choice to permanently "
					+ "attune (upgrade) it making it more powerful.");
			stage = 19;
			return;
		}
		if (name.equalsIgnoreCase("torso")) {
			sendItemDialogue(10551, 1, "The fighter's torso is an <col=ff0000>untradeable</col>"
					+ " chestpiece that is equivalent to a bandos chestplate.");
			stage = 21;
			return;
		}
		sendDialogue("This is a bug, please report it to an administrator!");
		stage = 99;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 1:
			if (componentId == OPTION_1) {
				if (canBuyItem()) {
					player.getInventory().addItem(13263, 1);
					player.setSlayerPoints(player.getSlayerPoints() - 400);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"You have bought a Slayer helmet for 400 slayer points!");
					SlayerTask.sendLearn(player);
				}
				return;
			}
			player.getInterfaceManager().closeChatBoxInterface();
			break;
		case 2:
			if (componentId == OPTION_1) {
				if (canBuyXP()) {
					player.setSlayerPoints(player.getSlayerPoints() - 40);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"You have bought 10'000 Slayer experience for 40 slayer points!");
					player.getSkills().addXp(Skills.SLAYER, 10000);
					SlayerTask.openSlayerShop(player);
				}
				return;
			}
			player.getInterfaceManager().closeChatBoxInterface();
			break;
		case 3:
			if (componentId == OPTION_1) {
				if (canBuyItem()) {
					player.getInventory().addItem(13281, 1);
					player.setSlayerPoints(player.getSlayerPoints() - 75);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"You have bought a Ring of Slaying for 75 slayer points!");
					SlayerTask.openSlayerShop(player);
				}
				return;
			}
			player.getInterfaceManager().closeChatBoxInterface();
			break;
		case 4:
			if (componentId == OPTION_1) {
				if (canBuyItems(560, 558)) {
					player.getInventory().addItem(560, 250);
					player.getInventory().addItem(558, 1000);
					player.setSlayerPoints(player.getSlayerPoints() - 35);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"You have bought runes for 250 Slayer dart casts for 35 slayer points!");
					SlayerTask.openSlayerShop(player);
				}
				return;
			}
			player.getInterfaceManager().closeChatBoxInterface();
			break;
		case 5:
			if (componentId == OPTION_1) {
				if (canBuyItem()) {
					player.getInventory().addItem(13280, 250);
					player.setSlayerPoints(player.getSlayerPoints() - 35);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"You have bought 250 broad-tipped bolts for 35 slayer points!");
					SlayerTask.openSlayerShop(player);
				}
				return;
			}
			player.getInterfaceManager().closeChatBoxInterface();
			break;
		case 6:
			if (componentId == OPTION_1) {
				if (canBuyItem()) {
					player.getInventory().addItem(4160, 250);
					player.setSlayerPoints(player.getSlayerPoints() - 35);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"You have bought 250 broad-tipped arrows for 35 slayer points!");
					SlayerTask.openSlayerShop(player);
				}
				return;
			}
			player.getInterfaceManager().closeChatBoxInterface();
			break;
		case 7:
			if (componentId == OPTION_1) {
				if (canCancelTask()) {
					player.setSlayerPoints(player.getSlayerPoints() - 5);
					player.setTask(null);
					player.getInterfaceManager().closeScreenInterface();
					player.getDialogueManager().startDialogue("KuradalGetTask", 9085);
				}
				return;
			}
			player.getInterfaceManager().closeChatBoxInterface();
			break;
		case 8:
			if (componentId == OPTION_1) {
				if (canBuyItem()) {
					player.getPerkManager().charmCollector = true;
					player.setSlayerPoints(player.getSlayerPoints() - 650);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"You have unlocked the Charm Collector perk for 650 slayer points! "
							+ "It works automatically and is not needed to configure.");
					SlayerTask.sendLearn(player);
				}
				return;
			}
			player.getInterfaceManager().closeChatBoxInterface();
			break;
		case 15:
			sendOptionsDialogue("Which ring do you want to imbue?", 
					"Archers' ring", "Seers' ring", "Warrior ring", "Berserker ring");
			stage = 16;
			break;
		case 16:
			if (componentId == OPTION_1) {
				if (canImbueRing(6733)) {
					player.getInventory().deleteItem(6733, 1);
					player.setSlayerPoints(player.getSlayerPoints() - 300);
					player.getInventory().addItem(15019, 1);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"Your ring has been successfully imbued!");
					SlayerTask.sendLearn(player);
					return;
				}
			}
			if (componentId == OPTION_2) {
				if (canImbueRing(6731)) {
					player.getInventory().deleteItem(6731, 1);
					player.setSlayerPoints(player.getSlayerPoints() - 300);
					player.getInventory().addItem(15018, 1);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"Your ring has been successfully imbued!");
					SlayerTask.sendLearn(player);
					return;
				}
			}
			if (componentId == OPTION_3) {
				if (canImbueRing(6735)) {
					player.getInventory().deleteItem(6735, 1);
					player.setSlayerPoints(player.getSlayerPoints() - 300);
					player.getInventory().addItem(15020, 1);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"Your ring has been successfully imbued!");
					SlayerTask.sendLearn(player);
					return;
				}
			}
			if (componentId == OPTION_4) {
				if (canImbueRing(6737)) {
					player.getInventory().deleteItem(6737, 1);
					player.setSlayerPoints(player.getSlayerPoints() - 300);
					player.getInventory().addItem(15220, 1);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"Your ring has been successfully imbued!");
					SlayerTask.sendLearn(player);
					return;
				}
			}
			break;
		case 17:
			sendOptionsDialogue("Do you wish to imbue an Onyx Ring?", 
					"Yes", "No");
			stage = 18;
			break;
		case 18:
			if (componentId == OPTION_1) {
				if (canImbueRing(6575)) {
					player.getInventory().deleteItem(6575, 1);
					player.setSlayerPoints(player.getSlayerPoints() - 300);
					player.getInventory().addItem(15017, 1);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"Your ring has been successfully imbued!");
					SlayerTask.sendLearn(player);
					return;
				}
			}
			break;
		case 19:
			sendOptionsDialogue("Do you wish to buy a Crystal weapon seed?", "Yes.", "No.");
			stage = 20;
			break;
		case 20:
			if (componentId == OPTION_1) {
				if (canBuyItem()) {
					player.getInventory().addItem(32625, 1);
					player.setSlayerPoints(player.getSlayerPoints() - 200);
					player.getDialogueManager().startDialogue("SimpleMessage", 
							"You have bought an Attuned crystal weapon seed for 200 slayer points!");
					SlayerTask.sendLearn(player);
				}
				return;
			}
			player.getInterfaceManager().closeChatBoxInterface();
			break;
		case 21:
			if(canBuyItem()) {
				sendOptionsDialogue("Do you want to buy a fighter's torso?", "Yes", "No");
				stage = 22;
			} else {
				player.sendMessage(Colors.red+"You don't have enough slayer points for this!");
				player.getInterfaceManager().closeChatBoxInterface();
			}
			break;
		case 22:
			if(componentId == OPTION_1) {
				player.getInventory().addItem(10551, 1);
				player.setSlayerPoints(player.getSlayerPoints() - 200);
				player.getDialogueManager().startDialogue("SimpleMessage", 
						"You have purchased a fighter's torso for 200 slayer points!");
				SlayerTask.sendLearn(player);
			} else
				player.getInterfaceManager().closeChatBoxInterface();
			return;
		case 99:
			end();
		}
	}
	
	*//**
	 * Checks if we can imbue the ring of our choise.
	 * @param ringId The ringID to imbue.
	 * @return true if can imbue.
	 *//*
	private boolean canImbueRing(int ringId) {
		if (player.getSlayerPoints() >= points) {
			if (player.getInventory().containsItem(ringId, 1)) {
				return true;
			}
			player.getDialogueManager().startDialogue("SimpleMessage", 
					"You must have the ring in your inventory to imbue it.");
			return false;
		}
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You do not have enough slayer points to buy this.");
		return false;
	}
	
	*//**
	 * Checks if we can buy the item.
	 * @return true if we can buy the item.
	 *//*
	private boolean canBuyItem() {
		if (player.getSlayerPoints() >= points) {
			if (player.getInventory().hasFreeSlots()) {
				return true;
			}
			player.getDialogueManager().startDialogue("SimpleMessage", 
					"You do not have enough free inventory space to buy this.");
			return false;
		}
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You do not have enough slayer points to buy this.");
		return false;
	}
	
	*//**
	 * Checks if we can buy stackable items.
	 * @param first first item ID.
	 * @param second second item ID.
	 * @return true if we can buy the items.
	 *//*
	private boolean canBuyItems(int first, int second) {
		if (player.getSlayerPoints() >= points) {
			if (player.getInventory().getFreeSlots() >= 2 
				|| (player.getInventory().containsItem(first, 1) && player.getInventory().containsItem(second, 1))) {
				return true;
			}
			player.getDialogueManager().startDialogue("SimpleMessage", 
					"You do not have enough free inventory space to buy this.");
			return false;
		}
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You do not have enough slayer points to buy this.");
		return false;
	}
	
	*//**
	 * Checks if we can buy XP.
	 * @return true if can buy XP.
	 *//*
	private boolean canBuyXP() {
		if (player.getSlayerPoints() >= points) {
			return true;
		}
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You do not have enough slayer points to buy this.");
		return false;
	}
	
	*//**
	 * Checks if we can cancel current slayer task.
	 * @return true if can cancel.
	 *//*
	private boolean canCancelTask() {
		if (player.getSlayerPoints() >= points) {
			return true;
		}
		player.getDialogueManager().startDialogue("SimpleMessage", 
				"You do not have enough slayer points to skip your current Slayer task.");
		return false;
	}

	@Override
	public void finish() {  }
}*/