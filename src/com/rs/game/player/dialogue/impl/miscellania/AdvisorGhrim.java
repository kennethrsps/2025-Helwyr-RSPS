package com.rs.game.player.dialogue.impl.miscellania;

import com.rs.game.player.dialogue.Dialogue;

public class AdvisorGhrim extends Dialogue {

	public double reputation;
	public int npcId;
	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		reputation = player.getThrone().getReputation();
		sendNPCDialogue(npcId, (reputation == 100 ? HAPPY_FACE : NORMAL), (reputation == 100 ? "Greetings, Your Royal Highness." : "Greeting, " + player.getDisplayName() + "."));
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch(stage) {
		case -1:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "How is the Kingdom faring?", "How do I manage the kingdom?", "What am I meant to be doing again?");
			stage = 1;
			break;
			
		case 1:
			switch(componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, (reputation == 100 ? HAPPY_FACE : NORMAL), "Your subjuects are content, " + (reputation == 100 ? "Your Highness" : player.getDisplayName()) + ".");
				stage = 2;
				break;
			case OPTION_2:
				end();
				player.getInterfaceManager().sendInterface(391);
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, (reputation == 100 ? HAPPY_FACE : NORMAL), (reputation == 100 ? "You shall do no more... " : " The civilians of your new kingdom are requesting some help within the area, I suggest you earn some reputation...") + (reputation == 100 ? "Your Highness" : player.getDisplayName()) + ".");
				stage = -2;
				break;
			}
			break;
			
		case 2:
			sendNPCDialogue(npcId, (reputation < 50 ? SAD : HAPPY_FACE), "Your current approval rating is at " + reputation + "%. Would you like", 
					"to collect the resources gathered by your subjuects?");
			stage = 3;
			break;
			
		case 3:
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes", "No");
			stage = 4;
			break;
			
		case 4:
			switch(componentId) {
			case OPTION_1:
				player.getThrone().openResources();
				end();
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "No, thank you.");
				stage = 5;
				break;
			}
			break;
			
		case 5:
			sendNPCDialogue(npcId, NORMAL, "Very well, " + (reputation == 100 ? "Your Highness" : player.getDisplayName()) + ".");
			stage = 6;
			break;
			
		case 6:
			sendNPCDialogue(npcId, NORMAL, "This is how your subjects are distributing their effort.");
			stage = -3;
			break;
			
		case -3:
			end();
			player.getThrone().displayInterface();
			//TODO King of Miscellania interface
			break;
			
		case -2:
			end();
			break;
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}
	
}
