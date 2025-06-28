package com.rs.game.player.dialogue.impl.quests;

import com.rs.Settings;
import com.rs.game.item.Item;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.newquests.NewQuestManager.Progress;
import com.rs.game.player.newquests.impl.DoricsQuest;

public class DoricsD extends Dialogue {

	private int npcId;
	private DoricsQuest quest;

	@Override
	public void start() {
		npcId = (int) this.parameters[0];
		quest = (DoricsQuest) player.getNewQuestManager().getQuests().get(3);
		if (parameters.length > 1) {
			boolean accept = (boolean) parameters[1];
			if (accept) {
				stage = 6;
				player.getInventory().addItem(1265, 1);
				sendPlayerDialogue(NORMAL, "Yes, I will get you the materials.");
			} else {
				stage = 4;
				sendPlayerDialogue(NORMAL, "No, hitting rocks if for the boring people, sorry.");
			}
		} else {
			if (quest.progress == Progress.NOT_STARTED) {
				sendNPCDialogue(npcId, NORMAL, "Hello traveller, what brings you to my humble smithy?");
			} else {
				stage = (byte) ((quest.getStage() == -1) ? 17 : 24);
				this.sendNPCDialogue(npcId, NORMAL,
						((quest.getStage() == -1) ? "Have you got my materials yet, traveller?"
								: "Hello traveller, how is your metalworking coming along?"));
			}
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "I wanted to use your anvils.", "I want to use your whetstone.",
					"Mind your own business, shortstuff!", "I was just checking out the landscape.",
					"What do you make here?");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				sendPlayerDialogue(NORMAL, "I wanted to use your anvils.");
				break;
			case OPTION_2:
				stage = 9;
				this.sendPlayerDialogue(NORMAL, "I want to use your whetstone.");
				break;
			case OPTION_3:
				stage = 10;
				this.sendPlayerDialogue(MAD, "Mind your own business, shortstuff!");
				break;
			case OPTION_4:
				stage = 11;
				this.sendPlayerDialogue(NORMAL, "I was just checking out the landscape.");
				break;
			case OPTION_5:
				stage = 13;
				this.sendPlayerDialogue(CONFUSED, "What do you make here?");
				break;
			}
			break;
		case 1:
			stage = 2;
			this.sendNPCDialogue(npcId, NORMAL, "My anvils get enough work with my own use. I make",
					"pickaxes, and it takes a lot of hard work. If you could",
					"get me some more materials, then I could let you use", "them.");
			break;
		case 2:
			stage = 3;
			this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes, I will get you the materials.",
					"No, hitting rocks if for the boring people, sorry.");
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				end();
				player.getNewQuestManager().startQuest(3);
				break;
			case OPTION_2:
				stage = 4;
				this.sendPlayerDialogue(NORMAL, "No, hitting rocks if for the boring people, sorry.");
				break;
			}
			break;
		case 4:
			stage = 5;
			this.sendNPCDialogue(npcId, NORMAL, "That is your choice. Nice to meet you anyway.");
			break;
		case 5:
			end();
			break;
		case 6:
			stage = 7;
			sendNPCDialogue(npcId, NORMAL, "Clay is what I use more than anything, to make casts.",
					"Could you get me 6 clay, 4 copper ore, and 2 iron ore,",
					"please? I could pay a little, and let you use my anvils.",
					"Take this pickaxe with you just in case you need it.");
			break;
		case 7:
			stage = 8;
			this.sendItemDialogue(1265, "Dorics gives you a pickaxe.");
			break;
		case 8:
			stage = 5;
			sendPlayerDialogue(NORMAL, "Certainly, I'll be right back!");
			break;
		case 9:
			stage = 2;
			this.sendNPCDialogue(npcId, NORMAL, "The whetstone is for more advanced smithing, but I",
					"could let you use it as well as my anvils if you could", "get me some more materials.");
			break;
		case 10:
			stage = 5;
			this.sendNPCDialogue(npcId, NORMAL, "How nice to meet someone with such pleasant manners.",
					"Do come again when you need to shout at someone", "smaller than you!");
			break;
		case 11:
			stage = 12;
			this.sendNPCDialogue(npcId, NORMAL, "Hope you like it. I do enjoy the solitude of my little",
					"home. If you get time, please say hi to my friends in", "the Dwarven Mine.");
			break;
		case 12:
			stage = 5;
			this.sendPlayerDialogue(NORMAL, "Will do!");
			break;
		case 13:
			stage = 14;
			this.sendNPCDialogue(npcId, NORMAL, "I make pickaxes. I am the best maker of pickaxes in the",
					"whole of " + Settings.SERVER_NAME + ".");
			break;
		case 14:
			stage = 15;
			this.sendPlayerDialogue(NORMAL, "Do you have any to sell?");
			break;
		case 15:
			stage = 16;
			this.sendNPCDialogue(npcId, NORMAL, "Sorry, but I've got a running order with Nurmof.");
			break;
		case 16:
			stage = 5;
			this.sendPlayerDialogue(NORMAL, "Ah, fair enough.");
			break;
		case 17:
			if (player.getInventory().containsItem(434, 6) && player.getInventory().containsItem(440, 2)
					&& player.getInventory().containsItem(436, 4)) {
				stage = 22;
				this.sendPlayerDialogue(NORMAL, "I have everything you need.");
				break;
			}
			stage = 18;
			this.sendPlayerDialogue(NORMAL, "Sorry, I don't have them all yet.");
			break;
		case 18:
			stage = 19;
			this.sendNPCDialogue(npcId, NORMAL, "Not to worry, stick at it. Remember, I need 6 clay, 4",
					"copper ore, and 2 iron ore.");
			break;
		case 19:
			stage = 20;
			this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Where can I find those?",
					"Certainly, I'll be right back.");
			break;
		case 20:
			switch (componentId) {
			case OPTION_1:
				stage = 21;
				sendPlayerDialogue(NORMAL, "Where can I find those?");
				break;
			case OPTION_2:
				stage = 5;
				this.sendPlayerDialogue(NORMAL, "Certainly, I'll be right back.");
				break;
			}
			break;
		case 21:
			stage = 5;
			this.sendNPCDialogue(npcId, NORMAL, "You'll be able to find all those ores in the rocks just",
					"inside the Dwarven Mine. Head east from here and",
					"you'll find the entrance in the side of Ice Mountain.");
			break;
		case 22:
			stage = 23;
			if (player.getInventory().removeItems(new Item(434, 6), new Item(440, 2), new Item(436, 4)))
				itemsRemoved = true;
			this.sendNPCDialogue(npcId, NORMAL, "Many thanks. Pass them here, please. I can spare you",
					"some coins for your trouble, and please use my anvils", "anytime you want.");
			break;
		case 23:
			stage = 5;
			sendItemDialogue(436, "You hand the clay, copper, and iron to Doric.");
			break;
		case 24:
			stage = 25;
			sendPlayerDialogue(NORMAL, "Not too bad, Doric.");
			break;
		case 25:
			stage = 5;
			this.sendNPCDialogue(npcId, NORMAL, "Good, the love of metal is a thing close to my heart.");
			break;
		}
	}

	private boolean itemsRemoved;

	@Override
	public void finish() {
		if (itemsRemoved)
			quest.finish();
	}

}
