package com.rs.game.player.dialogue.impl.quests.piratestreasure;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.newquests.impl.PiratesTreasure;
import com.rs.utils.ShopsHandler;

public class Wydin extends Dialogue {

	private int npcId;
	private PiratesTreasure quest;
	public static int SHOP_KEY = 5;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		quest = (PiratesTreasure) player.getNewQuestManager().getQuests().get(9);
		if (parameters.length > 1) {
			if (quest.isWydinEmployee()) {
				sendNPCDialogue(npcId, TALK_SWING, "Can you put your white apron on before going in there", "please.");
				stage = 100;
			} else {
				sendNPCDialogue(npcId, ANGERY, "Hey, you can't go in there. Only emplyees of the",
						"grocery store can go in.");
				stage = 100;
			}
			return;
		}
		if (quest.isWydinEmployee()) {
			sendNPCDialogue(npcId, CONFUSED, "Is it nice and tidy round the back now?");
			stage = 0;
		} else {
			sendNPCDialogue(npcId, NORMAL, "Welcome to my food store! Would you like to buy", "anything?");
			stage = 0;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (quest.isWydinEmployee()) {
			switch (stage) {
			case 0:
				sendOptionsDialogue("Select an Option", "Yes, can I work out front now?",
						"Yes, are you going to pay me yet?", "No, it's a complete mess.",
						"Can I buy something please?");
				stage = 1;
				break;
			case 1:
				switch (componentId) {
				case OPTION_1:
					sendPlayerDialogue(CONFUSED, "Yes, can I work out front now?");
					stage = 10;
					break;
				case OPTION_2:
					sendPlayerDialogue(CONFUSED, "Yes, are you going to pay me yet?");
					stage = 20;
					break;
				case OPTION_3:
					stage = 31;
					sendPlayerDialogue(NORMAL, "No, it's a complete mess.");
					break;
				case OPTION_4:
					sendPlayerDialogue(CONFUSED, "Can I buy something please?");
					stage = 40;
					break;
				}
				break;
			case 10:
				sendNPCDialogue(npcId, TALK_SWING, "No, I'm the one who works here.");
				stage = 11;
				break;
			case 11:
				end();
				break;
			case 20:
				sendNPCDialogue(npcId, LOOKING_DOWN, "Umm... No, not yet.");
				stage = 21;
				break;
			case 21:
				end();
				break;
			case 31:
				sendNPCDialogue(npcId, TALK_SWING, "Ah well, it'll give you something to do, won't it.");
				stage = 32;
				break;
			case 32:
				end();
				break;
			case 40:
				sendNPCDialogue(npcId, NORMAL, "Yes, of course.");
				stage = 41;
				break;
			case 41:
				end();
				ShopsHandler.openShop(player, SHOP_KEY);
				break;
			case 100:
				end();
				break;
			}
		} else {
			switch (stage) {
			case 0:
				sendOptionsDialogue("Select an Option", "Yes please.", "No, thank you.", "What can you recommend?",
						"Can I get a job here?");
				stage = 1;
				break;
			case 1:
				switch (componentId) {
				case OPTION_1:
					sendPlayerDialogue(NORMAL, "Yes please.");
					stage = 10;
					break;
				case OPTION_2:
					sendPlayerDialogue(NORMAL, "No, thank you.");
					stage = 20;
					break;
				case OPTION_3:
					sendPlayerDialogue(CONFUSED, "What can you recommend?");
					stage = 30;
					break;
				case OPTION_4:
					sendPlayerDialogue(CONFUSED, "Can I get a job here?");
					stage = 40;
					break;
				}
				break;
			case 10:
				end();
				ShopsHandler.openShop(player, SHOP_KEY);
				break;
			case 20:
				end();
				break;
			case 30:
				sendNPCDialogue(npcId, NORMAL, "We have this really exotic fruit all the way from",
						"Karamja. It's called a banana.");
				stage = 31;
				break;
			case 31:
				sendOptionsDialogue("Select an Option", "Hmm, I think I'll try one.",
						"I don't like the sound of that.");
				stage = 32;
				break;
			case 32:
				switch (componentId) {
				case OPTION_1:
					sendPlayerDialogue(NORMAL, "Hmm, I think I'll try one.");
					stage = 10;
					break;
				case OPTION_2:
					sendPlayerDialogue(NORMAL, "I don't like the sound of that.");
					stage = 100;
					break;
				}
				break;
			case 40:
				sendNPCDialogue(npcId, NORMAL, "Well, you're keen, I'll give you that. Okay, I'll give you",
						"a go. Have you got your own white apron?");
				stage = 41;
				break;
			case 41:
				if (!player.getInventory().containsItem(1005, 1) && player.getEquipment().getChestId() != 1005) {
					sendPlayerDialogue(NORMAL, "No, I haven't.");
					stage = 42;
				} else {
					stage = 55;
					sendPlayerDialogue(NORMAL, "Yes, I have one.");
				}
				break;
			case 42:
				sendNPCDialogue(npcId, NORMAL, "Well, you can't work here unless you have a white",
						"apron. Health and safety regulations, you understand.");
				stage = 43;
				break;
			case 43:
				sendPlayerDialogue(CONFUSED, "Where can I get one of those?");
				stage = 44;
				break;
			case 44:
				sendNPCDialogue(npcId, NORMAL, "Well, I get all of mine over at the clothing shop in",
						"Varrock. They sell them cheap there.");
				stage = 45;
				break;
			case 45:
				sendNPCDialogue(npcId, NORMAL, "Oh, and I'm sure that I've seen a spare one over in",
						"Gerrant's fish store somewhere. It's the little place just", "north of here.");
				stage = 46;
				break;
			case 46:
				end();
				break;
			case 55:
				stage = 100;
				quest.setWydinEmployee(true);
				sendNPCDialogue(npcId, NORMAL, "Wow - you are well prepared! You're hired. Go through",
						"to the back and tidy up for me, please.");
				break;
			case 100:
				end();
				break;
			}
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
