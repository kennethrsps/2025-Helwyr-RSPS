package com.rs.game.player.dialogue.impl.quests.ernestthechicken;

import java.util.ArrayList;

import com.rs.game.item.Item;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.newquests.impl.ErnestTheChicken;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class ProfessorOddenstein extends Dialogue {

	private int npcId;
	private ErnestTheChicken quest;
	private ArrayList<Item> itemsYetToObtain;
	private ArrayList<Item> itemsObtained;

	@Override
	public void start() {
		npcId = (int) this.parameters[0];
		quest = (ErnestTheChicken) player.getNewQuestManager().getQuests().get(5);
		itemsYetToObtain = new ArrayList<Item>();
		itemsObtained = new ArrayList<Item>();
		switch (quest.getStage()) {
		case -1:
			stage = -1;
			this.sendNPCDialogue(npcId, NORMAL, "Be careful in here, there's lots of dangerous equipment.");
			break;
		case 0:
			stage = 23;
			sendNPCDialogue(npcId, NORMAL, "Have you found anything yet?");
			break;
		case 1:
			stage = 29;
			sendNPCDialogue(287, HAPPY_FACE, "Thank you, " + (player.getGlobalPlayerUpdater().isMale() ? "sir" : "my lady")
					+ ". It was dreadfully irritating being a chicken. How can *cluck* I ever thank you?");
			break;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "I'm looking for a guy called Ernest.",
					"What does this machine do?", "Is this your house?");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 4;
				this.sendPlayerDialogue(NORMAL, "I'm looking for a guy called Ernest.");
				break;
			case OPTION_2:
				stage = 2;
				this.sendPlayerDialogue(NORMAL, "What does this machine do?");
				break;
			case OPTION_3:
				stage = 1;
				this.sendPlayerDialogue(NORMAL, "Is this your house?");
				break;
			}
			break;
		case 1:
			stage = END_STAGE;
			this.sendNPCDialogue(npcId, NORMAL,
					"No, I'm just one of the tenants. It belongs to the count who lives in the basement.");
			break;
		case 2:
			stage = 3;
			this.sendNPCDialogue(npcId, NORMAL,
					"Nothing at the moment...it's broken. It's meant to be a transmutation machine.");
			break;
		case 3:
			stage = END_STAGE;
			sendNPCDialogue(npcId, NORMAL,
					"It has also spent time as a time travel machine, a dramatic lightning generator, and a thing for generating monsters.");
			break;
		case 4:
			stage = 5;
			sendNPCDialogue(npcId, NORMAL, "Ah, Ernest - top notch bloke - he's helping me with my experiments.");
			break;
		case 5:
			stage = 6;
			sendPlayerDialogue(NORMAL, "So you know where he is?");
			break;
		case 6:
			stage = 7;
			sendNPCDialogue(npcId, NORMAL, "He's that chicken over there.");
			break;
		case 7:
			stage = 8;
			sendPlayerDialogue(NORMAL, "Ernest is a chicken..? Are you sure..?");
			break;
		case 8:
			stage = 9;
			sendNPCDialogue(npcId, NORMAL,
					"Oh, he isn't normally a chicken, or, at least, he wasn't until he helped me test my pouletmorph machine.");
			break;
		case 9:
			stage = 10;
			sendNPCDialogue(npcId, NORMAL,
					"It was originally going to be called a transmutation machine, but after testing it, pouletmorph seems more appropriate.");
			break;
		case 10:
			stage = 11;
			this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE,
					"I'm glad Veronica didn't actually get engaged to a chicken.", "Change him back this instant!");
			break;
		case 11:
			switch (componentId) {
			case OPTION_1:
				stage = 12;
				this.sendPlayerDialogue(NORMAL, "I'm glad Veronica didn't actually get engaged to a chicken.");
				break;
			case OPTION_2:
				stage = 16;
				sendPlayerDialogue(NORMAL, "Change him back this instant!");
				break;
			}
			break;
		case 12:
			stage = 13;
			sendNPCDialogue(npcId, NORMAL, "Who's Veronica?");
			break;
		case 13:
			stage = 14;
			this.sendPlayerDialogue(NORMAL, "Ernest's fiancée. She probably doesn't want to marry a chicken.");
			break;
		case 14:
			stage = 15;
			sendNPCDialogue(npcId, NORMAL, "Oh, I dunno. She could have free eggs for breakfast every morning.");
			break;
		case 15:
			stage = 16;
			sendPlayerDialogue(NORMAL, "I think you'd better change him back.");
			break;
		case 16:
			stage = 17;
			this.sendNPCDialogue(npcId, NORMAL, "Umm... It's not so easy...");
			break;
		case 17:
			stage = 18;
			this.sendNPCDialogue(npcId, NORMAL,
					"My machine is broken, and this house has long been pestered by poltergeists.");
			break;
		case 18:
			stage = 19;
			this.sendNPCDialogue(npcId, NORMAL,
					"They seem to reap joy from hiding my belongings and, in this case, the vital parts to my machine.");
			break;
		case 19:
			stage = 20;
			sendPlayerDialogue(NORMAL, "Well, I can look for them.");
			break;
		case 20:
			stage = 21;
			this.sendNPCDialogue(npcId, NORMAL,
					"That would be a huge help. They'll be somewhere in the manor house or its grounds outside. I'm missing the pressure gauge and a rubber tube. They've also taken my oil can, which I'm going to need to get this thing started again.");
			break;
		case 21:
			quest.setStage(0);
			stage = 22;
			sendPlayerDialogue(NORMAL, "Any clues?");
			break;
		case 22:
			stage = END_STAGE;
			this.sendNPCDialogue(npcId, NORMAL,
					"Well, I wouldn't be surprised if you found something in my IQ-testing room under the manor where I test my...creations. Oh, and if I were you, I'd stay away from the coffin in the basement. Even the poltergeists daren't go near it.");
			break;
		case 23:
			Item[] neededItems = new Item[] { ErnestTheChicken.OIL_CAN, ErnestTheChicken.PRESSURE_GAUGE,
					ErnestTheChicken.RUBBER_TUBE };
			for (int i = 0; i < neededItems.length; i++) {
				Item neededItem = neededItems[i];
				if (neededItem == null)
					continue;
				if (player.getInventory().containsOneItem(neededItem.getId()))
					itemsObtained.add(neededItem);
				else
					itemsYetToObtain.add(neededItem);
			}
			switch (itemsObtained.size()) {
			case 0:
				stage = 24;
				sendPlayerDialogue(NORMAL, "I'm afraid I don't have any of them yet.");
				break;
			case 1:
				stage = 25;
				sendPlayerDialogue(NORMAL,
						"I have only got the " + itemsObtained.get(0).getName().toLowerCase() + " so far.");
				break;
			case 2:
				stage = 25;
				sendPlayerDialogue(NORMAL, "I've got the " + itemsObtained.get(0).getName().toLowerCase() + " and the "
						+ itemsObtained.get(1).getName().toLowerCase() + ".");
				break;
			case 3:
				stage = 27;
				sendPlayerDialogue(NORMAL, "I have everything!");
				break;
			}
			break;
		case 24:
			stage = END_STAGE;
			this.sendNPCDialogue(npcId, NORMAL,
					"I need a rubber tube, a pressure gauge and a can of oil. Then your friend can stop being a chicken.");
			break;
		case 25:
			stage = 26;
			String[] itemName = new String[2];
			for (int i = 0; i < itemsYetToObtain.size(); i++) {
				Item item = itemsYetToObtain.get(i);
				if (item == null)
					continue;
				itemName[i] = item.getName().toLowerCase();
			}
			sendNPCDialogue(npcId, NORMAL, (itemName[1] == null
					? ("That's good, but I still need my " + itemName[0] + ".")
					: ("It's a good start, but I'll need my " + itemName[0] + " and " + itemName[1] + " too.")));
			break;
		case 26:
			stage = END_STAGE;
			this.sendPlayerDialogue(NORMAL,
					"Okay, I'll go look for " + (itemsYetToObtain.size() == 1 ? ("it") : ("them")) + ".");
			break;
		case 27:
			stage = 28;
			this.sendNPCDialogue(npcId, NORMAL, "Give 'em here then.");
			break;
		case 28:
			end();
			player.lock();
			WorldTasksManager.schedule(new WorldTask() {
				int count = 0;

				@Override
				public void run() {
					switch (count) {
					case 1:
						player.getInventory().removeItems(ErnestTheChicken.OIL_CAN, ErnestTheChicken.PRESSURE_GAUGE,
								ErnestTheChicken.RUBBER_TUBE);
						quest.setStage(1);
						player.getPackets().sendGameMessage(
								"You give a rubber tube, a pressure gauge, and a can of oil to the professor.");
						break;
					case 2:
						player.getPackets().sendGameMessage("Oddenstein starts up the machine.");
						break;
					case 3:
						player.getPackets().sendGameMessage("The machine hums and shakes.");
						break;
					case 4:
						player.unlock();
						player.getDialogueManager().startDialogue("ProfessorOddenstein", npcId);
						stop();
						break;
					}
					count++;
				}
			}, 0, 1);
			break;
		case 29:
			stage = 30;
			this.sendPlayerDialogue(NORMAL, "A cash reward is always nice...");
			break;
		case 30:
			stage = END_STAGE;
			sendNPCDialogue(287, HAPPY_FACE,
					"Of course, of course. You may as well have these eggs and *cluck* feathers I mysteriously *bwark* found in my pockets.");
			break;
		case 31:
			end();
			break;
		}

	}

	private static byte END_STAGE = 31;

	@Override
	public void finish() {
		if (quest.getStage() >= 1) {
			quest.finish();
		}

	}

}
