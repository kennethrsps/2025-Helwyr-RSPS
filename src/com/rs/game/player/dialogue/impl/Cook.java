package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;

public class Cook extends Dialogue {

	private int npcId;
	private boolean isQuest;

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue("Would you like to do a favour for the Cook?", "Yes", "No");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				sendPlayerDialogue(NORMAL, "Sure, What do you need help with?");
				break;
			case OPTION_2:
				stage = 1;
				end();
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, CROOKED_HEAD,
					"An Evil Old Chef from Lumbridge has gone crazy! He's hiding in that portal over there.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(SCARED, "Woah, that sounds crazy! Do you need help?");
			stage = 3;
			break;
		case 3:
			sendNPCDialogue(npcId, NORMAL, "Yes, please. If you help me get rid of him, I will reward you.");
			stage = 4;
			break;
		case 4:
			sendPlayerDialogue(CROOKED_HEAD, "Okay, how prepared should I be?");
			stage = 5;
			break;
		case 5:
			sendNPCDialogue(npcId, UNSURE,
					"I'm not sure, but I think he got his friends with him. So be prepared to fight for your life!");
			stage = 6;
			break;
		case 6:
			player.setTalkedToCook();
			sendPlayerDialogue(NORMAL, "Okay, I'll do it, wish me luck!");
			stage = 7;
			break;
		case 7:
			sendNPCDialogue(npcId, NORMAL, "Goodluck! Please kill them all, I'm counting on you!");
			stage = 8;
			break;
		case 8:
			player.getControlerManager().startControler("ImpossibleJadControler");
			end();
			break;
		case 10:
			if (componentId == OPTION_1) {
				end();
				player.getDialogueManager().startDialogue("CookDNew", 278);
				return;
			}
			if (player.isKilledCulinaromancer()) {
				stage = 8;
				sendNPCDialogue(npcId, 9827, "Godbless you " + player.getDisplayName() + "! Use my chest as reward!!");
				return;
			} else if (!player.hasTalkedtoCook()) {
				stage = -1;
				sendNPCDialogue(npcId, 9827,
						"Hello " + player.getDisplayName() + ". Before we talk, can you do me a favour?");
				return;
			} else if (player.hasTalkedtoCook()) {
				stage = 8;
				sendNPCDialogue(npcId, ANGRY, "Keep going " + player.getDisplayName() + ", kill them all!");
				return;
			}
			break;
		default:
			end();
			break;
		}

	}

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		isQuest = parameters.length >= 2 ? (Boolean) parameters[1] : false;
		if (isQuest) {
			stage = 10;
			sendOptionsDialogue("Would you like to do a favour for the Cook?", "Talk about cook's assistant",
					"talk about other stuff");
			return;
		}
		if (player.isKilledCulinaromancer()) {
			stage = 8;
			sendNPCDialogue(npcId, 9827, "Godbless you " + player.getDisplayName() + "! Use my chest as reward!!");
			return;
		} else if (!player.hasTalkedtoCook()) {
			stage = -1;
			sendNPCDialogue(npcId, 9827,
					"Hello " + player.getDisplayName() + ". Before we talk, can you do me a favour?");
			return;
		} else if (player.hasTalkedtoCook()) {
			stage = 8;
			sendNPCDialogue(npcId, ANGRY, "Keep going " + player.getDisplayName() + ", kill them all!");
			return;
		}
	}

	@Override
	public void finish() {

	}

}