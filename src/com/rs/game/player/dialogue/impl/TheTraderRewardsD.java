package com.rs.game.player.dialogue.impl;

import com.rs.game.player.Skills;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles The Trader's dialogue for rewards option.
 */
public class TheTraderRewardsD extends Dialogue {

	// The NPC ID.
	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if (player.getPorts().firstTimer) {
			sendDialogue("The Trader has no goods for sale at the moment. Check bank once you have "
					+ "completed the Port tutorial.");
			stage = 99;
		} else
			sendOptionsDialogue("Select an Option", "See available Loyalty titles", 
					"Exchange resources for Chime", "Exchange resources for Equipment");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				sendOptionsDialogue(Colors.red+"Loyalty Titles #1", 
						player.getDisplayName()+Colors.darkRed+" the Cabin "+(player.getGlobalPlayerUpdater().isMale() ? "Boy" : "Girl")+"</col> - "+(player.getPorts().portScore > 0 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 1 Port Score to unlock."), 
						Colors.darkRed+"Bo'sun</col> "+player.getDisplayName()+"</col> - "+(player.getPorts().portScore >= 400 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 400 Port Score to unlock."),
						Colors.darkRed+"First Mate</col> "+player.getDisplayName()+"</col> - "+(player.getPorts().portScore >= 800 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 800 Port Score to unlock."),
						Colors.darkRed+"Cap'n</col> "+player.getDisplayName()+"</col> - "+(player.getPorts().portScore >= 1200 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 1'200 Port Score to unlock."),
						"More Options..");
				stage = 0;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, NORMAL, "I will exchange any of the 3 resource types for Chime at a 1:5 rate. "
						+ "Simply select which resource you would like to get rid of and enter the amount to exchange.");
				stage = 2;
				break;
			case OPTION_3:
				sendOptionsDialogue(Colors.red+"Which equipment would you like?", 
						"Tetsu equipment (requires level 90 Smithing)", 
						"Seasinger's equipment (requires level 90 Runecrafting)", 
						"Death Lotus equipment (requires level 90 Crafting)");
				stage = 4;
				break;
			}
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				if (player.getPorts().portScore > 0) {
					sendNPCDialogue(npcId, NORMAL, "I've successfully changed your Loyalty title!");
					player.getGlobalPlayerUpdater().setTitle(1517);
					break;
				}
				sendNPCDialogue(npcId, CALM, "Sadly you do not have enough Port Score to use this Loyalty title. "
						+ "Send some ships on soccessful voyages to increase it.");
				stage = 99;
				break;
			case OPTION_2:
				if (player.getPorts().portScore >= 400) {
					sendNPCDialogue(npcId, NORMAL, "I've successfully changed your Loyalty title!");
					player.getGlobalPlayerUpdater().setTitle(1026);
					break;
				}
				sendNPCDialogue(npcId, CALM, "Sadly you do not have enough Port Score to use this Loyalty title. "
						+ "Send some ships on soccessful voyages to increase it.");
				stage = 99;
				break;
			case OPTION_3:
				if (player.getPorts().portScore >= 800) {
					sendNPCDialogue(npcId, NORMAL, "I've successfully changed your Loyalty title!");
					player.getGlobalPlayerUpdater().setTitle(1027);
					break;
				}
				sendNPCDialogue(npcId, CALM, "Sadly you do not have enough Port Score to use this Loyalty title. "
						+ "Send some ships on soccessful voyages to increase it.");
				stage = 99;
				break;
			case OPTION_4:
				if (player.getPorts().portScore >= 1200) {
					sendNPCDialogue(npcId, NORMAL, "I've successfully changed your Loyalty title!");
					player.getGlobalPlayerUpdater().setTitle(1028);
					break;
				}
				sendNPCDialogue(npcId, CALM, "Sadly you do not have enough Port Score to use this Loyalty title. "
						+ "Send some ships on soccessful voyages to increase it.");
				stage = 99;
				break;
			case OPTION_5:
				sendOptionsDialogue(Colors.red+"Loyalty Titles #2", 
						Colors.darkRed+"Commodore</col> "+player.getDisplayName()+"</col> - "+(player.getPorts().portScore >= 1600 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 1'600 Port Score to unlock."), 
						Colors.darkRed+"Admiral</col> "+player.getDisplayName()+"</col> - "+(player.getPorts().portScore >= 2000 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 2'00 Port Score to unlock."),
						Colors.darkRed+"Admiral of the Fleet</col> "+player.getDisplayName()+"</col> - "+(player.getPorts().portScore >= 3500 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 3'500 Port Score to unlock."),
						Colors.darkRed+"Portmaster</col> "+player.getDisplayName()+"</col> - "+(player.getPorts().portScore >= 4500 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 4'500 Port Score to unlock."),
						"More Options..");
				stage = 1;
				break;
			}
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				if (player.getPorts().portScore >= 1600) {
					sendNPCDialogue(npcId, NORMAL, "I've successfully changed your Loyalty title!");
					player.getGlobalPlayerUpdater().setTitle(1029);
					break;
				}
				sendNPCDialogue(npcId, CALM, "Sadly you do not have enough Port Score to use this Loyalty title. "
						+ "Send some ships on soccessful voyages to increase it.");
				stage = 99;
				break;
			case OPTION_2:
				if (player.getPorts().portScore >= 2000) {
					sendNPCDialogue(npcId, NORMAL, "I've successfully changed your Loyalty title!");
					player.getGlobalPlayerUpdater().setTitle(1030);
					break;
				}
				sendNPCDialogue(npcId, CALM, "Sadly you do not have enough Port Score to use this Loyalty title. "
						+ "Send some ships on soccessful voyages to increase it.");
				stage = 99;
				break;
			case OPTION_3:
				if (player.getPorts().portScore >= 3500) {
					sendNPCDialogue(npcId, NORMAL, "I've successfully changed your Loyalty title!");
					player.getGlobalPlayerUpdater().setTitle(1031);
					break;
				}
				sendNPCDialogue(npcId, CALM, "Sadly you do not have enough Port Score to use this Loyalty title. "
						+ "Send some ships on soccessful voyages to increase it.");
				stage = 99;
				break;
			case OPTION_4:
				if (player.getPorts().portScore >= 4500) {
					sendNPCDialogue(npcId, NORMAL, "I've successfully changed your Loyalty title!");
					player.getGlobalPlayerUpdater().setTitle(1032);
					break;
				}
				sendNPCDialogue(npcId, CALM, "Sadly you do not have enough Port Score to use this Loyalty title. "
						+ "Send some ships on soccessful voyages to increase it.");
				stage = 99;
				break;
			case OPTION_5:
				sendOptionsDialogue(Colors.red+"Loyalty Titles #1", 
						player.getDisplayName()+Colors.darkRed+" the Cabin "+(player.getGlobalPlayerUpdater().isMale() ? "Boy" : "Girl")+"</col> - "+(player.getPorts().portScore > 0 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 1 Port Score to unlock."), 
						Colors.darkRed+"Bo'sun</col> "+player.getDisplayName()+"</col> - "+(player.getPorts().portScore >= 400 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 400 Port Score to unlock."),
						Colors.darkRed+"First Mate</col> "+player.getDisplayName()+"</col> - "+(player.getPorts().portScore >= 800 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 800 Port Score to unlock."),
						Colors.darkRed+"Cap'n</col> "+player.getDisplayName()+"</col> - "+(player.getPorts().portScore >= 1200 ? Colors.green+"Unlocked" : Colors.red+"Locked</col> - need 1'200 Port Score to unlock."),
						"More Options..");
				stage = 0;
				break;
			}
			break;
		case 2:
			sendOptionsDialogue(Colors.red+"Exchange Resources for Chime", 
					"Plate (you have: "+Utils.getFormattedNumber(player.getPorts().plate)+")", 
					"Chi Globe (you have: "+Utils.getFormattedNumber(player.getPorts().chiGlobe)+")", 
					"Lacquer (you have: "+Utils.getFormattedNumber(player.getPorts().lacquer)+")");
			stage = 3;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				end();
				player.getTemporaryAttributtes().put("ports_plate", Boolean.TRUE);
				player.getPackets().sendRunScript(108, new Object[] {
		    		"How many would you like to exchange?" });
				break;
			case OPTION_2:
				end();
				player.getTemporaryAttributtes().put("ports_chiGlobe", Boolean.TRUE);
				player.getPackets().sendRunScript(108, new Object[] {
		    		"How many would you like to exchange?" });
				break;
			case OPTION_3:
				end();
				player.getTemporaryAttributtes().put("ports_lacquer", Boolean.TRUE);
				player.getPackets().sendRunScript(108, new Object[] {
		    		"How many would you like to exchange?" });
				break;
			}
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				if (player.getSkills().getLevelForXp(Skills.SMITHING) < 90) {
					sendNPCDialogue(npcId, SAD, "You do not have the appropriate level to unlock this equipment..");
					stage = 98;
					break;
				}
				sendOptionsDialogue(Colors.red+"Tetsu Equipment<col> (you have "+Utils.getFormattedNumber(player.getPorts().plate)+" plate).", 
						(player.getPorts().plate < 60 ? Colors.red : Colors.green)+"Tetsu Helm - 60 plate", 
						(player.getPorts().plate < 105 ? Colors.red : Colors.green)+"Tetsu Body - 105 plate", 
						(player.getPorts().plate < 80 ? Colors.red : Colors.green)+"Tetsu Legs - 80 plate", 
						(player.getPorts().plate < 130 ? Colors.red : Colors.green)+"Tetsu Katana - 130 plate",
						(player.getPorts().plate < 80 ? Colors.red : Colors.green)+"Tetsu Wakizashi - 80 plate");
				stage = 5;
				break;
			case OPTION_2:
				if (player.getSkills().getLevelForXp(Skills.RUNECRAFTING) < 90) {
					sendNPCDialogue(npcId, SAD, "You do not have the appropriate level to unlock this equipment..");
					stage = 98;
					break;
				}
				sendOptionsDialogue(Colors.red+"Seasinger's Equipment<col> (you have "+Utils.getFormattedNumber(player.getPorts().chiGlobe)+" chi globe).", 
						(player.getPorts().chiGlobe < 60 ? Colors.red : Colors.green)+"Seasinger's Hood - 60 chi globe", 
						(player.getPorts().chiGlobe < 105 ? Colors.red : Colors.green)+"Seasinger's Robe Top - 105 chi globe", 
						(player.getPorts().chiGlobe < 80 ? Colors.red : Colors.green)+"Seasinger's Robe Bottom - 80 chi globe", 
						(player.getPorts().chiGlobe < 130 ? Colors.red : Colors.green)+"Seasinger's Kiba - 130 chi globe", 
						(player.getPorts().chiGlobe < 80 ? Colors.red : Colors.green)+"Seasinger's Makigai - 80 chi globe");
				stage = 6;
				break;
			case OPTION_3:
				if (player.getSkills().getLevelForXp(Skills.CRAFTING) < 90) {
					sendNPCDialogue(npcId, SAD, "You do not have the appropriate level to unlock this equipment..");
					stage = 98;
					break;
				}
				sendOptionsDialogue(Colors.red+"Death Lotus Equipment<col> (you have "+Utils.getFormattedNumber(player.getPorts().lacquer)+" lacquer).", 
						(player.getPorts().lacquer < 60 ? Colors.red : Colors.green)+"Death Lotus Hood - 60 lacquer", 
						(player.getPorts().lacquer < 105 ? Colors.red : Colors.green)+"Death Lotus Chestplate - 105 lacquer", 
						(player.getPorts().lacquer < 80 ? Colors.red : Colors.green)+"Death Lotus Chaps - 80 lacquer", 
						(player.getPorts().lacquer < 1 ? Colors.red : Colors.green)+"Death Lotus Dart (x25) - 1 lacquer");
				stage = 7;
				break;
			}
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				if (player.getPorts().plate < 60) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough plate to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().plate -= 60;
				player.getInventory().addItemDrop(26325, 1);
				player.getSkills().addXp(Skills.SMITHING, 1000);
				sendItemDialogue(26325, 1, "The Trader hands you your item in exchange for plate.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			case OPTION_2:
				if (player.getPorts().plate < 105) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough plate to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().plate -= 105;
				player.getInventory().addItemDrop(26326, 1);
				player.getSkills().addXp(Skills.SMITHING, 3000);
				sendItemDialogue(26326, 1, "The Trader hands you your item in exchange for plate.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			case OPTION_3:
				if (player.getPorts().plate < 80) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough plate to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().plate -= 80;
				player.getInventory().addItemDrop(26327, 1);
				player.getSkills().addXp(Skills.SMITHING, 2000);
				sendItemDialogue(26327, 1, "The Trader hands you your item in exchange for plate.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			case OPTION_4:
				if (player.getPorts().plate < 130) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough plate to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().plate -= 130;
				player.getInventory().addItemDrop(33879, 1);
				player.getSkills().addXp(Skills.SMITHING, 3000);
				sendItemDialogue(33879, 1, "The Trader hands you your item in exchange for plate.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			case OPTION_5:
				if (player.getPorts().plate < 80) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough plate to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().plate -= 80;
				player.getInventory().addItemDrop(33882, 1);
				player.getSkills().addXp(Skills.SMITHING, 2000);
				sendItemDialogue(33879, 1, "The Trader hands you your item in exchange for plate.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			}
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				if (player.getPorts().chiGlobe < 60) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough chi globe to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().chiGlobe -= 60;
				player.getInventory().addItemDrop(26337, 1);
				player.getSkills().addXp(Skills.RUNECRAFTING, 1000);
				sendItemDialogue(26337, 1, "The Trader hands you your item in exchange chi globe.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			case OPTION_2:
				if (player.getPorts().chiGlobe < 105) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough chi globe to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().chiGlobe -= 105;
				player.getInventory().addItemDrop(26338, 1);
				player.getSkills().addXp(Skills.RUNECRAFTING, 3000);
				sendItemDialogue(26338, 1, "The Trader hands you your item in exchange chi globe.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			case OPTION_3:
				if (player.getPorts().chiGlobe < 80) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough chi globe to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().chiGlobe -= 80;
				player.getInventory().addItemDrop(26339, 1);
				player.getSkills().addXp(Skills.RUNECRAFTING, 2000);
				sendItemDialogue(26339, 1, "The Trader hands you your item in exchange chi globe.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			case OPTION_4:
				if (player.getPorts().chiGlobe < 130) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough chi globe to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().chiGlobe -= 130;
				player.getInventory().addItemDrop(33886, 1);
				player.getSkills().addXp(Skills.RUNECRAFTING, 3000);
				sendItemDialogue(33886, 1, "The Trader hands you your item in exchange chi globe.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			case OPTION_5:
				if (player.getPorts().chiGlobe < 80) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough chi globe to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().chiGlobe -= 80;
				player.getInventory().addItemDrop(33889, 1);
				player.getSkills().addXp(Skills.RUNECRAFTING, 1000);
				sendItemDialogue(33889, 1, "The Trader hands you your item in exchange chi globe.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			}
			break;
		case 7:
			switch (componentId) {
			case OPTION_1:
				if (player.getPorts().lacquer < 60) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough lacquer to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().lacquer -= 60;
				player.getInventory().addItemDrop(26346, 1);
				player.getSkills().addXp(Skills.CRAFTING, 1000);
				sendItemDialogue(26346, 1, "The Trader hands you your item in exchange for lacquer.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			case OPTION_2:
				if (player.getPorts().lacquer < 105) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough lacquer to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().lacquer -= 105;
				player.getInventory().addItemDrop(26347, 1);
				player.getSkills().addXp(Skills.CRAFTING, 3000);
				sendItemDialogue(26347, 1, "The Trader hands you your item in exchange for lacquer.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			case OPTION_3:
				if (player.getPorts().lacquer < 80) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough lacquer to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				player.getPorts().lacquer -= 80;
				player.getInventory().addItemDrop(26348, 1);
				player.getSkills().addXp(Skills.CRAFTING, 2000);
				sendItemDialogue(26348, 1, "The Trader hands you your item in exchange for lacquer.<br>"
						+ "You can use this item on The Trader in order to create a Superior version.");
				stage = 99;
				break;
			case OPTION_4:
				if (player.getPorts().lacquer < 1) {
					sendNPCDialogue(npcId, CALM, "Seems like you do not have enough lacquer to get this item. "
							+ "Send some of your ships on voyages to get more.");
					stage = 98;
					break;
				}
				if (player.getSkills().getLevelForXp(Skills.FLETCHING) < 92) {
					sendNPCDialogue(npcId, SAD, "You do not have the appropriate level to unlock this equipment..");
					stage = 98;
					break;
				}
				player.getPorts().lacquer -= 1;
				player.getInventory().addItemDrop(30574, 25);
				player.getSkills().addXp(Skills.FLETCHING, 250);
				sendItemDialogue(30574, 25, "The Trader hands you your item in exchange for lacquer.");
				stage = 98;
				break;
			}
			break;

		case 98:
			sendOptionsDialogue(Colors.red+"Which equipment would you like?",
					"Tetsu equipment (requires level 90 Smithing)",
					"Seasinger's equipment (requires level 90 Runecrafting)",
					"Death Lotus equipment (requires level 90 Crafting)");
			stage = 4;
			break;
		case 99:
			end();
			break;
		}
	}

	@Override
	public void finish() {
	}
}