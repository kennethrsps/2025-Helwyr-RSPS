package com.rs.game.player.dialogue.impl.quests;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.newquests.NewQuestManager.Progress;
import com.rs.game.player.newquests.impl.CooksAssistant;

public class CookDNew extends Dialogue {

	private int npcId;
	private CooksAssistant quest;

	@Override
	public void start() {
		npcId = (int) this.parameters[0];
		quest = (CooksAssistant) player.getNewQuestManager().getQuests().get(1);
		if (parameters.length > 1) {
			boolean accept = (boolean) parameters[1];
			if (accept) {
				stage = 4;
				sendPlayerDialogue(HAPPY_FACE, "I'm always happy to help a cook in distress.");
			} else {
				stage = 8;
				sendPlayerDialogue(HAPPY_FACE, "I can't right now. Maybe later.");
			}
			return;
		}
		if (quest.progress == Progress.STARTED) {
			if (quest.givenItems[0] && quest.givenItems[1] && quest.givenItems[2]) {
				stage = 19;
				this.sendNPCDialogue(npcId, HAPPY_FACE, "You've brought me everything I need! I am saved!",
						"Thank you!");
				return;
			}
			stage = 14;
			sendNPCDialogue(npcId, ASKING_FACE, "How are you getting on with finding the ingredients?");
			return;
		}
		if (quest.progress == Progress.COMPLETED) {
			stage = 24;
			this.sendNPCDialogue(npcId, HAPPY_FACE,
					"Hi there, " + (player.getGlobalPlayerUpdater().isMale() ? "sir" : "ma'am") + "!",
					"What can I help you with today?");
		} else {
			stage = -1;
			sendNPCDialogue(npcId, SAD, "What am I to do?");
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue("Select an Option", "What's wrong?", "Can you make me a cake?",
					"You don't look very happy.", "Nice hat!");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				sendPlayerDialogue(ASKING_FACE, "What's wrong?");
				break;
			case OPTION_2:
				stage = 9;
				sendPlayerDialogue(ASKING_FACE, "Can you make me a cake?");
				break;
			case OPTION_3:
				stage = 12;
				sendPlayerDialogue(NORMAL, "You don't look very happy.");
				break;
			case OPTION_4:
				stage = 13;
				sendPlayerDialogue(HAPPY_FACE, "Nice hat!");
				break;
			}
			break;
		case 1:
			stage = 2;
			this.sendNPCDialogue(npcId, SAD, "Oh dear, oh dear, oh dear, I'm in a terrible, terrible mess!",
					"It's the Duke's birthday today, and I should be making him",
					"a lovely, big birthday cake using special ingredients...");
			break;
		case 2:
			stage = 3;
			this.sendNPCDialogue(npcId, SAD, "...but I've forgotten to get the ingredients. I'll never get",
					"them in time now. He'll sack me! Whatever will I do? I have",
					"four children anda goat to look after. Would you help me?", "Please?");
			break;
		case 3:
			end();
			player.getNewQuestManager().startQuest(1);
			break;
		case 4:
			stage = 5;
			sendNPCDialogue(npcId, HAPPY_FACE, "Oh, thank you. I must tell you that this is no ",
					"ordinary cake, though - only the best ingredients will do! I",
					"need a super large egg, top-quality milk and some extra", "fine flour.");
			break;
		case 5:
			stage = 6;
			sendPlayerDialogue(ASKING_FACE, "Where can I find those, then?");
			break;
		case 6:
			stage = 7;
			this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Where do I find some Extra fine flour?",
					"How about Top-quality milk?", "And Super large eggs? Where are they found?",
					"Actually, I know where to find this stuff.");
			/*
			 * sendNPCDialogue( npcId, NORMAL,
			 * "That's the problem: I don't exactly know. I usually send my assistant"
			 * ,
			 * "to get them for me but he quit. I've marked some places on your world"
			 * , "map in red. You might want to consider investigating them.");
			 */
			break;
		case 7:
			switch (componentId) {
			case OPTION_1:
				stage = 39;
				sendPlayerDialogue(NORMAL, "Where do I find some Extra fine flour?");
				break;
			case OPTION_2:
				stage = 40;
				sendPlayerDialogue(NORMAL, "How about Top-quality milk?");
				break;
			case OPTION_3:
				stage = 41;
				sendPlayerDialogue(NORMAL, "And Super large eggs? Where are they found?");
				break;
			case OPTION_4:
				stage = 8;
				sendPlayerDialogue(NORMAL, "Actually, I know where to find this stuff.");
				break;
			}
			/*
			 * stage = 8; sendPlayerDialogue(NORMAL,
			 * "That's all the information I need. Thanks!");
			 */
			break;
		case 8:
			end();
			break;
		case 9:
			stage = 10;
			this.sendNPCDialogue(npcId, MILDLY_ANGRY_FACE,
					"Oh I don't have time to make you a cake, can't you see I'm a bit busy?");
			break;
		case 10:
			stage = 11;
			sendOptionsDialogue("Select an Option", "What's wrong?", "Nevermind");
			break;
		case 11:
			if (componentId == OPTION_1) {
				stage = 1;
				sendPlayerDialogue(ASKING_FACE, "What's wrong?");
			} else {
				stage = 8;
				sendPlayerDialogue(NORMAL, "Nevermind, I can't right now. Maybe later.");
			}
			break;
		case 12:
			stage = 10;
			this.sendNPCDialogue(npcId, SAD, "I know, I've been busy trying to figure out what I should do.");
			break;
		case 13:
			stage = 10;
			sendNPCDialogue(npcId, SAD, "I don't have time for your pesky compliments!");
			break;
		case 14:
			if (player.getInventory().containsItem(CooksAssistant.BUCKET_OF_MILK, 1)
					|| player.getInventory().containsItem(CooksAssistant.EGG, 1)
					|| player.getInventory().containsItem(CooksAssistant.POT_OF_FLOUR, 1)) {
				stage = 15;
				sendPlayerDialogue(HAPPY_FACE, "I think have what you asked for.");
			} else {
				stage = 43;
				this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "I'll get right on it.",
						"Can you remind me how to find these things again?");
			}
			break;
		case 15:
			if (player.getInventory().containsItem(CooksAssistant.BUCKET_OF_MILK, 1)) {
				stage = 16;
				itemId = CooksAssistant.BUCKET_OF_MILK;
				player.getInventory().deleteItem(itemId, 1);
				quest.givenItems[0] = true;
				sendPlayerDialogue(HAPPY_FACE,
						"Here's some " + ItemDefinitions.getItemDefinitions(itemId).getName() + ".");
			} else if (player.getInventory().containsItem(CooksAssistant.POT_OF_FLOUR, 1)) {
				stage = 16;
				itemId = CooksAssistant.POT_OF_FLOUR;
				player.getInventory().deleteItem(itemId, 1);
				quest.givenItems[1] = true;
				sendPlayerDialogue(HAPPY_FACE,
						"Here's some " + ItemDefinitions.getItemDefinitions(itemId).getName() + ".");
			} else if (player.getInventory().containsItem(CooksAssistant.EGG, 1)) {
				stage = 16;
				itemId = CooksAssistant.EGG;
				player.getInventory().deleteItem(itemId, 1);
				quest.givenItems[2] = true;
				sendPlayerDialogue(HAPPY_FACE,
						"Here's some " + ItemDefinitions.getItemDefinitions(itemId).getName() + ".");
			} else if (!quest.givenItems[0] || !quest.givenItems[1] || !quest.givenItems[2]) {
				stage = 18;
				sendNPCDialogue(npcId, HAPPY_FACE,
						"You still need to get: " + ((!quest.givenItems[0]) ? "A Top-quality milk " : "")
								+ ((!quest.givenItems[1]) ? "An Extra fine flour " : "")
								+ ((!quest.givenItems[2]) ? "A Super large egg " : ""));
			} else {
				stage = 19;
				this.sendNPCDialogue(npcId, HAPPY_FACE, "You've brought me everything I need! I am saved!",
						"Thank you!");
			}
			break;
		case 16:
			stage = 17;
			sendItemDialogue(itemId,
					"You give the " + ItemDefinitions.getItemDefinitions(itemId).getName() + " to the cook.");
			break;
		case 17:
			stage = 15;
			this.sendNPCDialogue(npcId, HAPPY_FACE, "Ta' very much!");
			break;
		case 18:
			stage = 8;
			this.sendPlayerDialogue(NORMAL, "I'll get right on it.");
			break;
		case 19:
			stage = 20;
			this.sendPlayerDialogue(HAPPY_FACE, "So, do I get to go to the Duke's party?");
			break;
		case 20:
			stage = 21;
			this.sendNPCDialogue(npcId, NORMAL, "I'm afraid not. Only the big cheeses get to dine with the Duke.");
			break;
		case 21:
			stage = 22;
			this.sendPlayerDialogue(NORMAL,
					"Well, maybe one day, I'll be important enough to sit at the Duke's table.");
			break;
		case 22:
			stage = 23;
			this.sendNPCDialogue(npcId, NORMAL, "Maybe, but I won't be holding my breath.");
			break;
		case 23:
			end();
			quest.finish();
			break;
		case 24:
			stage = 25;
			sendOptionsDialogue("Select an Option", "Do you have any other quests for me?",
					"I am getting strong and mighty.", "I keep on dying.", "Can I use your range?");
			break;
		case 25:
			switch (componentId) {
			case OPTION_1:
				stage = 26;
				sendPlayerDialogue(ASKING_FACE, "Do you have any other quests for me?");
				break;
			case OPTION_2:
				stage = 31;
				sendPlayerDialogue(ASKING_FACE, "I am getting strong and mighty.");
				break;
			case OPTION_3:
				stage = 32;
				sendPlayerDialogue(NORMAL, "I keep on dying.");
				break;
			case OPTION_4:
				stage = 33;
				sendPlayerDialogue(HAPPY_FACE, "Can I use your range?");
				break;
			/*
			 * case OPTION_5: sendPlayerDialogue(HAPPY_FACE,
			 * "Can you tell me anything about that chest in the basement?");
			 * stage = 23; break;
			 */
			}
			break;
		case 26:
			stage = 27;
			sendPlayerDialogue(HAPPY_FACE, "That last one was fun!");
			break;
		case 27:
			stage = 28;
			sendNPCDialogue(npcId, SAD, "Ooh dear, yes I do.");
			break;
		case 28:
			stage = 29;
			sendNPCDialogue(npcId, SAD, "It's the Duke of Lumbridge's birthday today, and",
					"I need to bake him a cake!");
			break;
		case 29:
			stage = 30;
			sendNPCDialogue(npcId, NORMAL, "I need you to bring me some eggs, some flour, ",
					"some milk and a chocolate bar...");
			break;
		case 30:
			stage = 24;
			sendNPCDialogue(npcId, LAUGHING, "Nah, not really, I'm just messing with you! ",
					"Thanks for all your help, I know I can count on you", "again in the future!");
			break;
		case 31:
			stage = 8;
			sendNPCDialogue(npcId, HAPPY_FACE, "Glad to hear it.");
			break;
		case 32:
			stage = 8;
			sendNPCDialogue(npcId, HAPPY_FACE, "Ah, well atleast you keep coming back to life too!");
			break;
		case 33:
			stage = 34;
			sendNPCDialogue(npcId, HAPPY_FACE, "Go ahead! It's a very good range; it's better than ",
					"most other ranges.");
			break;
		case 34:
			stage = 35;
			sendNPCDialogue(npcId, HAPPY_FACE, "It's called the Cook-o-Matic 25 and it uses a ",
					"combination of state-of-the-art temperature ", "regulation and magic.");
			break;
		case 35:
			stage = 36;
			sendPlayerDialogue(HAPPY_FACE, "Will it mean my food will burn less often?");
			break;
		case 36:
			stage = 37;
			sendNPCDialogue(npcId, HAPPY_FACE, "As long as the food is fairly easy to cook in ", "the first place!");
			break;
		case 37:
			stage = 38;
			sendNPCDialogue(npcId, NORMAL, "Here, take this manual. It should tell you ",
					"everything you need to know about this range.");
			break;
		case 38:
			stage = 8;
			player.getInventory().addItem(15411, 1);
			this.sendItemDialogue(15411, "The cook hands you a manuel.");
			break;
		case 39:
			stage = 42;
			this.sendNPCDialogue(npcId, NORMAL, "There is a Mill fairly close, Go North and then West.",
					"Mill Lane Mill is just off the road to Draynor. I", "usually get my flour from there.");
			break;
		case 40:
			stage = 42;
			this.sendNPCDialogue(npcId, NORMAL, "There is a cattle field on the other side of the river,",
					"just across the road from the Groats' Farm.");
			break;
		case 41:
			stage = 42;
			this.sendNPCDialogue(npcId, NORMAL, "I normally get my eggs from the Groats' farm, on the",
					"other side of the river.");
			break;
		case 42:
			stage = 7;
			this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Where do I find some Extra fine flour?",
					"How about Top-quality milk?", "And Super large eggs? Where are they found?",
					"Actually, I know where to find this stuff.");
			break;
		case 43:
			if (componentId == OPTION_1) {
				stage = 8;
				this.sendPlayerDialogue(NORMAL, "I'll get right on it.");
			} else {
				stage = 6;
				this.sendPlayerDialogue(ASKING_FACE, "Can you remind me how to find these things again?");
			}
			break;
		}
	}

	private int itemId;

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
