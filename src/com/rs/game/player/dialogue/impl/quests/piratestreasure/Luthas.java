package com.rs.game.player.dialogue.impl.quests.piratestreasure;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.newquests.impl.PiratesTreasure;

public class Luthas extends Dialogue {

	private int npcId;
	private PiratesTreasure quest;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		quest = (PiratesTreasure) player.getNewQuestManager().getQuests().get(9);
		if (quest.isLuthasTask()) {
			final int current = quest.getKaramjaBananas();
			if (current >= 10) {
				player("I've filled a crate with bananas.");
				stage = 50;
				return;
			}
			sendNPCDialogue(npcId, CONFUSED, "Have you completed your task yet?");
			stage = 100;
			return;
		}
		sendNPCDialogue(npcId, NORMAL, "Hello I'm Luthas, I run the banana plantation here.");
		stage = 0;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 0:
			sendOptionsDialogue("Select an Option", "Could you offer me employment on your plantation?",
					"That customs officer is annoying isn't he?");
			stage = 1;
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CONFUSED, "Could you offer me employment on your plantation?");
				stage = 10;
				break;
			case OPTION_2:
				sendPlayerDialogue(CONFUSED, "That customs officer is annoying isn't she?");
				stage = 20;
				break;
			}
			break;
		case 10:
			sendNPCDialogue(npcId, NORMAL, "Yes, I can sort something out. There's a crate ready to",
					"be loaded onto the ship.");
			stage = 11;
			break;
		case 11:
			sendNPCDialogue(npcId, NORMAL, "You wouldn't believe the demand for bananas from",
					"Wydin's shop over in Port Sarim. I think this is the",
					"third crate I've shipped him this month..");
			stage = 12;
			break;
		case 12:
			sendNPCDialogue(npcId, TALK_SWING, "If you could go fill it up with bananas, I'll pay you 30", "gold.");
			stage = 13;
			quest.setLuthasTask(true);
			break;
		case 13:
			end();
			break;
		case 20:
			sendNPCDialogue(npcId, LOOKING_DOWN, "I don't know about that.");
			stage = 21;
			break;
		case 21:
			end();
			break;
		case 100:
			player("No, the crate isn't full yet.");
			stage = 101;
			break;
		case 101:
			sendNPCDialogue(npcId, NORMAL, "Well come back when it is.");
			stage = 21;
			break;
		case 50:
			sendNPCDialogue(npcId, NORMAL, "Well done, here's your payment.");
			stage = 51;
			break;
		case 51:
			end();
			player.getPackets().sendGameMessage("Luthas hands you 30 coins.");
			quest.setKaramjaBananas(0);
			quest.setLuthasTask(false);
			if (quest.hasStoredRum()) {
				quest.setStoredRum(false);
				quest.setAtWydins(true);
			}
			player.getInventory().addItemDrop(995, 30);
			break;
		}
	}

	public void player(String string) {
		sendPlayerDialogue(NORMAL, string);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
