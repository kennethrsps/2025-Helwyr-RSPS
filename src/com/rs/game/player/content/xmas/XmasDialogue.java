package com.rs.game.player.content.xmas;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;
import com.rs.game.item.Item;

public class XmasDialogue extends Dialogue {

	private int npcId;
	private int upgradeAmt;
	
	@Override
	public void start() {
		npcId = (int) parameters[0];
		Dialogue.closeNoContinueDialogue(player);
		/* Queen of Snow */
		if(npcId == 9398) {
			if(!player.getXmas().intro) {
				sendNPCDialogue(npcId, SCARED, "Oh, thank heavens! An adventurer! Can you save Santa Claus?");
				stage = 0;
			} else {
				sendNPCDialogue(npcId, SCARED, "Welcome back, "+player.getDisplayName()+"! How can I help?");
				stage = 10;
			}
		}
		
		/* Santa's cage */
		if(npcId == 17539) {
			if(!player.getXmas().freedSanta) {
				sendItemDialogue(1050, 1, "It seems that Santa is...asleep inside the cage!");
				stage = 99;
			} else {
				sendNPCDialogue(9400, GOOFY_LAUGH, "Thank you for rescuing me! I think I'll rest in here...I'm pretty tired.");
				stage = 99;
			}
		}
		
		
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch(stage) {
		/* Snow queen intro dialogue */
		case 0:
			sendPlayerDialogue(GOOFY_LAUGH, "U-uhm! Hello to you too! What's going on?");
			stage = 1;
			break;
		case 1:
			sendNPCDialogue(npcId, SCARED, "Santa has been locked away in the mansion by a powerful sorcerer! The wizard has summoned his..creatures everywhere!");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(CALM, "What could I possibly do to save him..how strong is this mage?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, CALM, "He is beyond even my abilities..we'll all need to work together to defeat him.");
			player.getXmas().intro = true;
			player.getHintIconsManager().removeUnsavedHintIcon();
			stage = 10;
			break;
			
		/* How can I help ~ options */
		case 4:
			switch(componentId) {
				case OPTION_1:
					sendOptionsDialogue("How can I help?",
							"Vanquish snow creatures",
							"Solve a riddle",
							"Nevermind");
					stage = 5;
					break;
				case OPTION_2:
					sendOptionsDialogue("What can I help you with?", "What is snow energy?",
						Colors.rcyan+"Exchange snow energy</col>",
						Colors.red+"Christmas titles</col>",
						"Nevermind");
					stage = 6;
					break;
				case OPTION_3:
					String damage = player.getXmas().damage == 4 
						? Colors.green+"Damage fully upgraded!" : "Upgrade snowball damage - "+Colors.red+(4-player.getXmas().damage)+" left!"; 
					sendOptionsDialogue("Choose an upgrade",
							damage,
							Colors.check(player.getXmas().speed)+"Upgrade throw speed");
					stage = 21;
					break;
			}
			break;
			
		case 21:
			int amount[] = { 400, 1000, 3500 };
			switch(componentId) {
				case OPTION_1:
					sendItemDialogue(33590, 100, "Do you want to spend "+amount[player.getXmas().damage-1]+" snow energy to upgrade x1 (+100%)?");
					upgradeAmt = amount[player.getXmas().damage-1];
					stage = 22;
					break;
				case OPTION_2:
					sendItemDialogue(33590, 1, "Do you want to spend 500 snow energy to upgrade your snowball throw speed?");
					upgradeAmt = 1000;
					stage = 23;
					break;
			}
			break;
		
		case 22:
			if(player.getXmas().snowEnergy >= upgradeAmt)
				sendOptionsDialogue("Do you wish to upgrade?", "Yes", "No");
			else {
				player.sendMessage(Colors.red+"You need "+(upgradeAmt-player.getXmas().snowEnergy)+" more snow energy!", true);
				finish();
			}
			stage = 24;
			break;
			
		case 23:
			if(player.getXmas().snowEnergy >= upgradeAmt)
				sendOptionsDialogue("Do you wish to upgrade?", "Yes", "No");
			else {
				player.sendMessage(Colors.red+"You need "+(upgradeAmt-player.getXmas().snowEnergy)+" more snow energy!", true);
				finish();
			}
			stage= 25;
			break;
		
		case 24:
			switch(componentId) {
			case OPTION_1:
				player.getXmas().snowEnergy -= upgradeAmt;
				player.getXmas().damage += 1;
				player.sendMessage(Colors.rcyan+"You have upgraded your snowball damage by 1x (+100%)!", true);
				finish();
				break;
			case OPTION_2:
				finish();
				break;
			}
			break;
			
		case 25:
			switch(componentId) {
			case OPTION_1:
				player.getXmas().snowEnergy -= upgradeAmt;
				player.getXmas().speed = true;
				player.sendMessage(Colors.rcyan+"You have upgraded your snowball speed!", true);
				finish();
				break;
			case OPTION_2:
				finish();
				break;
			}
			break;
			
		case 5:
			switch(componentId) {
			case OPTION_1:
				sendPlayerDialogue(CALM_TALKING, "So what about these fierce snowmen roaming around?");
				stage = 9;
				break;
			case OPTION_2:
				sendPlayerDialogue(CALM_TALKING, "Do you have any riddles for me, your Icyness?");
				stage = 13;
				break;
			case OPTION_3:
				finish();
				break;
			}
			break;
			
		case 6:
			switch(componentId) {
			case OPTION_1:
				sendPlayerDialogue(CALM, "What exactly is snow energy?");
				stage = 7;
				break;
			case OPTION_2:
				finish();
				ShopsHandler.openShop(player, 64);
				break;
			case OPTION_3:
				sendOptionsDialogue("What title would you like?",
						player.getDisplayName()+Colors.salmon + " of</col>"+Colors.green+" Christmas</col>",
						player.getDisplayName()+Colors.rcyan + " of Winter</col>",
			            player.getDisplayName()+Colors.green + " the Grinch</col>",
			            player.getDisplayName()+Colors.cyan + " Frostweb</col>",
						"Nevermind");
				stage = 8;
				break;
			}
			break;
			
		case 7:
			sendNPCDialogue(npcId, CALM, Colors.dcyan+"Snow energy"+"</col> can be used to buy rewards and participate in the event!");
			componentId = OPTION_2;
			stage = 4;
			break;
			
		case 8:
			switch(componentId) {
			case OPTION_1:
				if(!player.xmasTitle1) {
					finish();
					if(player.getXmas().snowEnergy >= 250) {
						player.xmasTitle1 = true;
						player.sendMessage("You have received a title!");
					} else {
						player.sendMessage("You need at least 250 saved snow energy for this title!!");
						break;
					}	
				} else
					player.sendMessage("You already have this title!");
				finish();
				break;
			case OPTION_2:
				if(!player.xmasTitle2) {
					finish();
					if(player.getXmas().snowmenKilled >= 100) {
						player.xmasTitle2 = true;
						player.sendMessage("You have received a title!");
					} else {
						player.sendMessage("You need at least kill 100 snowmen for this title!");
						break;
					}	
				} else
					player.sendMessage("You already have this title!");
				finish();
				break;
			case OPTION_3:
				if(!player.xmasTitle3) {
					finish();
					if(player.getXmas().freedSanta) {
						player.xmasTitle3 = true;
						player.sendMessage("You have received a title!");
					} else {
						player.sendMessage("You need at least kill the boss once for this title!!");
						break;
					}	
				} else
					player.sendMessage("You already have this title!");
				finish();
				break;
			case OPTION_4:
				if(!player.xmasTitle4) {
					finish();
					if(player.getXmas().bossKilled >= 25) {
						player.xmasTitle4 = true;
						player.sendMessage("You have received a title!");
					} else {
						player.sendMessage("You need at least kill the boss 25 times for this title!!");
						break;
					}	
				} else
					player.sendMessage("You already have this title!");
				finish();
				break;
			case OPTION_5:
				finish();
				break;
			}
			break;
			
			
		case 9: //response to case5 op1
			sendNPCDialogue(npcId, CALM, "You can pelt the various snowmen with snowballs for "+Colors.dcyan+"snow energy.</col>");
			stage = 10;
			break;
			
		case 13:
			sendNPCDialogue(npcId, ANGRY, "Was that supposed to be funny? It wasn't.");
			stage = 11;
			break;
			
		case 11: //case5 op2
			//if(player.getXmas().riddle == null) {
				sendOptionsDialogue("Choose a riddle!", 
						Colors.check(player.getXmas().riddle1)+"Riddle #1",
						Colors.check(player.getXmas().riddle2)+"Riddle #2",
						Colors.check(player.getXmas().riddle3)+"Riddle #3",
						Colors.check(player.getXmas().riddle4)+"Riddle #4",
						"Nevermind");
				stage = 12;
			//} else {
				//sendNPCDialogue(npcId, ANGRY, "You already have an active riddle to solve!");
				//stage = 99;
			//}
			break;
			
		case 12:
			boolean parchment = !player.getInventory().hasFreeSlots() && !player.hasItem(new Item(11036));
			int smartSwitch = (componentId == OPTION_1) ? 1 : componentId - 11;
			boolean[] riddles = { player.getXmas().riddle1, player.getXmas().riddle2,
					player.getXmas().riddle3, player.getXmas().riddle4 };
			if(smartSwitch != 5 && riddles[smartSwitch-1]) {
				sendNPCDialogue(npcId, WHY, "You've already completed this riddle!");
				stage = 11;
				break;
			}
			
			if(parchment) {
				sendNPCDialogue(npcId, ANGRY, "Why don't you come back when you have room in your inventory?");
				stage = 99;
				break;
			}
			
			switch(componentId) {
			case OPTION_1:
			case OPTION_2:
			case OPTION_3:
			case OPTION_4:
				if(player.getInventory().hasFreeSlots() && !player.hasItem(new Item(11036)))
					player.getInventory().addItem(new Item(11036));
				XmasRiddles.setRiddle(player, smartSwitch, false);
				sendItemDialogue(11036, 1, Colors.green+Colors.shad+"You've received a riddle to solve!</shad></col>");
				stage = 99;
				break;
				
			case OPTION_5:
				XmasRiddles.setRiddle(player, 0, true);
				player.sendMessage("You've reset the riddle.");
				finish();
				break;
			}
			break;
	
		case 10:
			sendOptionsDialogue("What would you like to do?", "How can I help?", 
					Colors.rcyan+"Snow energy</col>",
					Colors.dcyan+Colors.shad+"Upgrade snowball!"+Colors.eshad);
			stage = 4;
			break;
		case 99:
			finish();
			break;
		}
		
	}

	@Override
	public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }

}
