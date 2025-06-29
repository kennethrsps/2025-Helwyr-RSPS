package com.rs.game.player.dialogue.impl.quests.piratestreasure;

import com.rs.game.item.Item;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.newquests.impl.PiratesTreasure;
import com.rs.game.player.newquests.NewQuestManager.Progress;

public class ReadbeardFrank extends Dialogue {

	private int npcId;
	private PiratesTreasure quest;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		quest = (PiratesTreasure) player.getNewQuestManager().getQuests().get(9);
		if (parameters.length > 1) {
			boolean accept = (boolean) parameters[1];
			if (accept) {
				sendPlayerDialogue(NORMAL, "Ok, I will bring you some rum.");
				stage = 17;
			} else {
				sendPlayerDialogue(NORMAL, "Not right now.");
				stage = 16;
			}
			return;
		}
		switch (quest.getStage()) {
		default:
			stage = 0;
			sendNPCDialogue(npcId, NORMAL, "Arr, Matey!");
			break;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (quest.progress == Progress.NOT_STARTED || parameters.length > 1) {
			switch (stage) {
			case 0:
				sendOptionsDialogue("Select an Option", "I'm in search of treasure.", "Arr!",
						"Do you have anything for trade?");
				stage = 1;
				break;
			case 1:
				switch (componentId) {
				case OPTION_1:
					sendPlayerDialogue(NORMAL, "I'm in search of treasure.");
					stage = 10;
					break;
				case OPTION_2:
					sendPlayerDialogue(NORMAL, "Arr!");
					stage = 20;
					break;
				case OPTION_3:
					sendPlayerDialogue(CONFUSED, "Do you have anything for trade?");
					stage = 30;
					break;
				}
				break;
			case 10:
				sendNPCDialogue(npcId, TALK_SWING, "Arr, treasure you be after eh? Well I might be able to",
						"tell you where to find some... For a price...");
				stage = 11;
				break;
			case 11:
				sendPlayerDialogue(LOOKING_DOWN, "What sort of price?");
				stage = 12;
				break;
			case 12:
				sendNPCDialogue(npcId, TALK_SWING, "Well for example if you go and get me a bottle of rum...",
						"Not just any rum mind...");
				stage = 13;
				break;
			case 13:
				sendNPCDialogue(npcId, NORMAL, "I'd like some rum made on Karamja Island. There's no",
						"rum like Karamaja Rum!");
				stage = 14;
				break;
			case 14:
				sendOptionsDialogue("Select an Option", "Ok, I will bring you some rum", "Not right now");
				stage = 15;
				break;
			case 15:
				switch (componentId) {
				case OPTION_1:// QUEST START ACCEPT
					end();
					player.getNewQuestManager().startQuest(9);
					break;
				case OPTION_2:// QUEST START DECLINE
					sendPlayerDialogue(NORMAL, "Not right now.");
					stage = 16;
					break;
				}
				break;
			case 17:
				sendNPCDialogue(npcId, NORMAL, "Yer a saint, although it'll take a miracle to get it off", "Karamja.");
				stage = 18;
				break;
			case 18:
				sendPlayerDialogue(CONFUSED, "What do you mean?");
				stage = 19;
				break;
			case 19:
				sendNPCDialogue(npcId, NORMAL, "The Customs office has been clampin' down on the",
						"export of spirits. You seem like a resourceful young lad,",
						"I'm sure ye'll be able to find a way to slip the stuff past", "them.");
				stage = 21;
				break;
			case 21:
				sendPlayerDialogue(NORMAL, "Well I'll give it a shot.");
				stage = 22;
				break;
			case 22:
				sendNPCDialogue(npcId, NORMAL, "Arr, that's the spirit!");
				stage = 23;
				break;
			case 23:
				end();
				break;
			case 16:
				sendNPCDialogue(npcId, NORMAL, "Fair enough. I'll still be here and thirsty whenever you",
						"feel like helpin' out.");
				stage = 31;
				break;
			case 20:
				sendNPCDialogue(npcId, NORMAL, "Arr!");
				stage = 31;
				break;
			case 30:
				sendNPCDialogue(npcId, NORMAL, "Nothin' at the moment, but then again the Customs",
						"Agents are on the warpath right now.");
				stage = 31;
				break;
			case 31:
				end();
				break;
			}
		} else {
			switch (quest.getStage()) {
			case 0:
				switch (stage) {
				case 0:
					if (quest.getStage() == 0 && !player.getInventory().containsItem(PiratesTreasure.CHEST_KEY, 1)
							&& !player.getBank().containsItem(PiratesTreasure.CHEST_KEY, 1)) {
						sendPlayerDialogue(SAD, "I seem to have lost my chest key...");
						stage = 100;
						return;
					}
					sendOptionsDialogue("Select an Option", "Arr!", "Do you have anything for trade?");
					stage = 1;
					break;
				case 1:
					switch (componentId) {
					case OPTION_1:
						sendPlayerDialogue(NORMAL, "Arr!");
						stage = 10;
						break;
					case OPTION_2:
						sendPlayerDialogue(CONFUSED, "Do you have anything for trade?");
						stage = 20;
						break;
					}

					break;
				case 10:
					sendNPCDialogue(npcId, NORMAL, "Arr!");
					stage = 11;
					break;
				case 11:
					sendOptionsDialogue("Select an Option", "Arr!", "Do you have anything for trade?");
					stage = 1;
					break;
				case 20:
					sendNPCDialogue(npcId, NORMAL, "Nothin' at the moment, but then again the Customs",
							"Agents are on the warpath right now.");
					stage = 21;
					break;
				case 21:
					end();
					break;
				case 100:
					sendNPCDialogue(npcId, GRUMPY, "Arr, silly you. Fortunately I took the precaution to have",
							"another one made.");
					stage = 101;
					break;
				case 101:
					if (!player.getInventory().addItem(PiratesTreasure.CHEST_KEY, 1)) {
						stage = 102;
						this.sendNPCDialogue(npcId, NORMAL, "Talk to me when you have enough space.");
						return;
					}
					this.sendItemDialogue(PiratesTreasure.CHEST_KEY, "Frank hands you a chest key.");
					stage = 102;
					break;
				case 102:
					end();
					break;
				case 14:
					sendNPCDialogue(npcId, NORMAL, "This be Hector's key. I believe it opens his chest in his",
							"old room in the Blue Moon Inn in Varrock.");
					stage = 15;
					break;
				case 15:
					sendNPCDialogue(npcId, NORMAL, "With any luck his treasure will be in there.");
					stage = 16;
					break;
				case 16:
					sendPlayerDialogue(NORMAL, "Ok thanks, I'll go and get it.");
					stage = 17;
					break;
				case 17:
					end();
					break;
				}
				break;
			case -1:
				switch (stage) {
				case 0:
					sendNPCDialogue(npcId, CONFUSED, "Have ye brought some rum for yer ol' mate Frank?");
					stage = 1;
					break;
				case 1:
					if (!player.getInventory().containsItem(PiratesTreasure.KARAMJA_RUM, 1)) {
						sendPlayerDialogue(SAD, "No, not yet.");
						stage = 2;
					} else {
						if (!quest.isLegalRum()) {
							stage = 15;
							player.getInventory().deleteItem(PiratesTreasure.KARAMJA_RUM, 1);
							this.sendNPCDialogue(npcId, ANGERY, "Where did you get that rum from!");
							return;
						}
						sendPlayerDialogue(NORMAL, "Yes, I've got some.");
						stage = 10;
					}
					break;
				case 15:
					stage = 17;
					this.sendPlayerDialogue(CONFUSED, "Looks like i have to get rum from karamja by the right way.");
					break;
				case 17:
					end();
					break;
				case 2:
					sendNPCDialogue(npcId, NORMAL, "Not suprising, tis no easy task to get it off Karamja.");
					stage = 3;
					break;
				case 3:
					sendPlayerDialogue(CONFUSED, "What do you mean?");
					stage = 4;
					break;
				case 4:
					sendNPCDialogue(npcId, NORMAL, "The Customs office has been clampin' down on the",
							"export of spirits. You seem like a resourceful young lad,",
							"I'm sure ye'll be able to find a way to slip the stuff past", "them.");
					stage = 5;
					break;
				case 5:
					sendPlayerDialogue(NORMAL, "Well I'll give it another shot.");
					stage = 6;
					break;
				case 6:
					end();
					break;
				case 10:
					sendNPCDialogue(npcId, NORMAL, "Now a deal's a deal, I'll tell ye about the treasure. I",
							"used to serve under a pirate captain called One-Eyed", "Hector.");
					stage = 11;
					break;
				case 11:
					sendNPCDialogue(npcId, NORMAL, "Hector were very successful and became very rich.",
							"But about a year ago we were boarded by the Customs", "and Excise Agents.");
					stage = 12;
					break;
				case 12:
					sendNPCDialogue(npcId, NORMAL, "Hector were killed along with many of the crew, I were",
							"one of the few to escape and I escaped with this.");
					stage = 13;
					break;
				case 13:
					if (player.getInventory().removeItems(new Item(PiratesTreasure.KARAMJA_RUM, 1))
							&& player.getInventory().addItem(PiratesTreasure.CHEST_KEY, 1)) {
						quest.setStage(0);
						this.sendItemDialogue(PiratesTreasure.CHEST_KEY,
								"Frank happily takes the rum... ... and hands you a key");
						stage = 14;
					} else {
						end();
					}
					break;
				case 14:
					end();
					break;
				}
				break;
			default:
				switch (stage) {
				case 0:
					if (quest.getStage() == 0 && !player.getInventory().containsItem(PiratesTreasure.CHEST_KEY, 1)
							&& !player.getBank().containsItem(PiratesTreasure.CHEST_KEY, 1)) {
						sendPlayerDialogue(LOOKING_DOWN, "I seem to have lost my chest key...");
						stage = 100;
						return;
					}
					sendOptionsDialogue("Select an Option", "Arr!", "Do you have anything for trade?");
					stage = 1;
					break;
				case 1:
					switch (componentId) {
					case OPTION_1:
						sendPlayerDialogue(NORMAL, "Arr!");
						stage = 10;
						break;
					case OPTION_2:
						sendPlayerDialogue(CONFUSED, "Do you have anything for trade?");
						stage = 20;
						break;
					}

					break;
				case 10:
					sendNPCDialogue(npcId, NORMAL, "Arr!");
					stage = 11;
					break;
				case 11:
					sendOptionsDialogue("Select an Option", "Arr!", "Do you have anything for trade?");
					stage = 1;
					break;
				case 20:
					sendNPCDialogue(npcId, NORMAL, "Nothin' at the moment, but then again the Customs",
							"Agents are on the warpath right now.");
					stage = 21;
					break;
				case 21:
					end();
					break;
				case 100:
					sendNPCDialogue(npcId, GRUMPY, "Arr, silly you. Fortunately I took the precaution to have",
							"another one made.");
					stage = 101;
					break;
				case 101:
					if (!player.getInventory().addItem(PiratesTreasure.CHEST_KEY, 1)) {
						stage = 102;
						this.sendNPCDialogue(npcId, NORMAL, "Talk to me when you have enough inventory space.");
						return;
					}
					this.sendItemDialogue(PiratesTreasure.CHEST_KEY, "Frank hands you a chest key.");
					stage = 102;
					break;
				case 102:
					end();
					break;
				}
				break;
			}
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
