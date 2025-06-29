package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

/**
 * Handles the 'Talk-to' option on Vorago's head.
 * @author Zeus
 */
public class VoragoD extends Dialogue {
	
	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if (!player.isFirstTime()) {
			sendNPCDialogue(npcId, NORMAL, "Welcome, stranger.");
			stage = 90;
		} else {
			sendNPCDialogue(npcId, NORMAL, "Player returns.");
			stage = 0;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 0:
			sendOptionsDialogue("Select an option", "What are you?", "What are you doing here?",
					"Buy stone of binding", "I don't want to talk to you.");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(GLANCE_DOWN, "What are you?");
				stage = 2;
				break;
			case OPTION_2:
				sendPlayerDialogue(GLANCE_DOWN, "What are you doing here?");
				stage = 22;
				break;
			case OPTION_3: //a
				sendPlayerDialogue(CALM, "Can I buy a stone of binding?");
				stage = 101;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL, "I don't want to talk to you.");
				stage = 35;
				break;
			}
			break;
		case 101:
			sendNPCDialogue(npcId, NORMAL, "Yes, player, you may use this stone to craft tectonic armor.");
			stage = 102;
			break;
		case 102:
			sendOptionsDialogue("Do you want to buy a stone of binding?", "Yes", "No");
			stage = 103;
			break;
		case 103:
			switch(componentId) {
			case OPTION_1:
				finish();
				if(player.getMoneyPouchValue() >= 500000) {
					player.getMoneyPouch().removeMoneyMisc(500000);
					player.getInventory().addItem(28628, 1);
					player.sendMessage(Colors.cyan+"You have purchased a stone of binding!", true);
					return;
				}
				if(player.getInventory().containsCoins(500000)) {
					player.getInventory().deleteCoins(500000);
					player.getInventory().addItem(28628, 1);
					player.sendMessage(Colors.cyan+"You have purchased a stone of binding!", true);
					return;
				}
				player.sendMessage(Colors.red+"You need 500,000 coins for a stone of binding!");
				break;
			case OPTION_2:
				finish();
				break;
			}
			break;
		case 2:
			sendNPCDialogue(npcId, NORMAL, "I know only that I am of the earth, and the earth "
					+ "and I are one. More than those above see and know. I am here to ensure "
					+ "the continuation of that life.");
			stage = 3;
			break;
		case 3:
			sendPlayerDialogue(GLANCE_DOWN, "I've heard of living rock creatures, not far from here. "
					+ "Are you of the same kind?");
			stage = 4;
			break;
		case 4:
			sendNPCDialogue(npcId, NORMAL, "They are known to me. I am not as they are, however. "
					+ "The earth soul and I are as one, and they are my wards.");
			stage = 5;
			break;
		case 5:
			sendOptionsDialogue("Select an option", "You talk like you don't know what you are.", 
					"So where did you come from?", "Do you know much about gods?");
			stage = 6;
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "You talk like you don't know what you are.");
				stage = 7;
				break;
			case OPTION_2:
				sendPlayerDialogue(GLANCE_DOWN, "So where did you come from?");
				stage = 12;
				break;
			case OPTION_3:
				sendPlayerDialogue(GLANCE_DOWN, "Do you know much about gods?");
				stage = 17;
				break;
			}
			break;
		case 7:
			sendNPCDialogue(npcId, NORMAL, "It is hard to describe in words that you will understand. "
					+ "I am one with the earth soul. It permeates me, and grants me the power to "
					+ "command the earth.");
			stage = 8;
			break;
		case 8:
			sendPlayerDialogue(GLANCE_DOWN, "Command it? What do you mean by that?");
			stage = 9;
			break;
		case 9:
			sendNPCDialogue(npcId, NORMAL, "I feel the fury of the battles that have taken place on this "
					+ "good earth. I can command it to take shape and fight back, if necessary.");
			stage = 10;
			break;
		case 10:
			sendPlayerDialogue(GLANCE_DOWN, "You could fight?");
			stage = 11;
			break;
		case 11:
			sendNPCDialogue(npcId, NORMAL, "Should I deem it necessary. Should something be of "
					+ "significant enough worth to warrant it, yes.");
			stage = 5;
			break;
		case 12:
			sendNPCDialogue(npcId, NORMAL, "That is an unknown. I feel reawakened of late, as "
					+ "if the world has changed.");
			stage = 13;
			break;
		case 13:
			sendPlayerDialogue(GLANCE_DOWN, "A lot has changed recently for us all. Do you "
					+ "remember anything before this reawakening?");
			stage = 14;
			break;
		case 14:
			sendNPCDialogue(npcId, NORMAL, "I remember taking shape at one point in my past, "
					+ "using the power of the earth to destroy. I was defeated by a powerful weapon.");
			stage = 15;
			break;
		case 15:
			sendPlayerDialogue(GLANCE_DOWN, "Interesting. Where would that weapon be now?");
			stage = 16;
			break;
		case 16:
			sendNPCDialogue(npcId, NORMAL, "I have it. I may be stopped but not destroyed, "
					+ "I will simply reform in new earth once more. Besides, I hold the only "
					+ "weapon ever to defeat me within my rock itself.");
			stage = 5;
			break;
		case 17:
			sendNPCDialogue(npcId, NORMAL, "They are known to me, yes. ");
			stage = 18;
			break;
		case 18:
			sendPlayerDialogue(GLANCE_DOWN, "Are you aligned to any?");
			stage = 19;
			break;
		case 19:
			sendNPCDialogue(npcId, NORMAL, "No. Their concerns are not mine. I seek only to "
					+ "defend the deep earth, and to repel those who would do it harm.");
			stage = 20;
			break;
		case 20:
			sendPlayerDialogue(GLANCE_DOWN, "You'd fight them?");
			stage = 21;
			break;
		case 21:
			sendNPCDialogue(npcId, NORMAL, "I have done. The world devourer came to destroy "
					+ "this earth. This earth rose up to meet the challenger. The challenger "
					+ "lies motionless in the ocean. The earth remains.");
			stage = 5;
			break;
			
		case 22:
			sendNPCDialogue(npcId, NORMAL, "I am waiting.");
			stage = 23;
			break;
		case 23:
			sendPlayerDialogue(GLANCE_DOWN, "Waiting for what?");
			stage = 24;
			break;
		case 24:
			sendNPCDialogue(npcId, NORMAL, "Those worthy of facing me in battle.");
			stage = 25;
			break;
		case 25:
			sendOptionsDialogue("Select an Option", "Worthy of facing you?", "Can I face you?");
			stage = 26;
			break;
		case 26:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(NORMAL, "Worthy of facing you?");
				stage = 27;
				break;
			case OPTION_2:
				sendPlayerDialogue(GLANCE_DOWN, "Can I face you?");
				stage = 30;
				break;
			}
			break;
		case 27:
			sendNPCDialogue(npcId, NORMAL, "Yes. I must do battle with the strongest ones of "
					+ "this world, to defend against what is to come.");
			stage = 28;
			break;
		case 28:
			sendPlayerDialogue(GLANCE_DOWN, "Who are the unworthy, then?");
			stage = 29;
			break;
		case 29:
			sendNPCDialogue(npcId, NORMAL, "To prepare for what is to come, I must fight at "
					+ "the peak of my strength. Those who cannot stand before my power are "
					+ "not worthy. To fight me would be a waste of their lives and of time.");
			stage = 26;
			break;
		case 30:
			sendNPCDialogue(npcId, NORMAL, "I offer a fair test to those who wish to "
					+ "challenge me - a test of resilience and resolve.");
			stage = 31;
			break;
		case 31:
			sendPlayerDialogue(NORMAL, "I'm listening.");
			stage = 32;
			break;
		case 32:
			sendNPCDialogue(npcId, NORMAL, "If you or a group of your allies can withstand my "
					+ "raw power, I will face you in combat inside the borehole below.");
			stage = 33;
			break;
		case 33:
			sendPlayerDialogue(GLANCE_DOWN, "When you say 'raw power', how much are we talking about "
					+ "here?");
			stage = 34;
			break;
		case 34:
			sendNPCDialogue(npcId, NORMAL, "I won't hold back. Ensure anyone you bring with "
					+ "you has spoken to me about this as well before I take them with us into "
					+ "the borehole.");
			stage = 99;
			break;
		case 35:
			sendNPCDialogue(npcId, NORMAL, "It is wise of you to leave. Farewell.");
			stage = 99;
			break;
			
		case 90:
			sendPlayerDialogue(GLANCE_DOWN, "I'm no stranger. Call me Player.");
			stage = 91;
			break;
		case 91:
			sendNPCDialogue(npcId, NORMAL, "So be it, Player. I am the Immoveable. The Enduring. "
					+ "You may call me Vorago.");
			player.setFirstTime(true);
			stage = 0;
			break;
			
		case 99:
			end();
			break;
		}
	}

	@Override
	public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }
}