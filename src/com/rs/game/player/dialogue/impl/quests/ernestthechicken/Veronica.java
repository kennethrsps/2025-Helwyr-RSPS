package com.rs.game.player.dialogue.impl.quests.ernestthechicken;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.newquests.NewQuestManager.Progress;
import com.rs.game.player.newquests.impl.ErnestTheChicken;

public class Veronica extends Dialogue {

	private int npcId;
	private ErnestTheChicken quest;

	@Override
	public void start() {
		npcId = (int) this.parameters[0];
		quest = (ErnestTheChicken) player.getNewQuestManager().getQuests().get(5);
		if (parameters.length > 1) {
			boolean accept = (boolean) parameters[1];
			if (accept) {
				stage = 7;
				sendPlayerDialogue(NORMAL, "Ok, I'll see what I can do.");
			} else {
				stage = 6;
				sendPlayerDialogue(NORMAL, "Not Right Now.");
			}
		} else {
			Progress questProgress = player.getNewQuestManager().getProgress(5);
			switch (questProgress) {
			case NOT_STARTED:
				stage = -1;
				sendNPCDialogue(npcId, NORMAL, "Can you please help me? I'm in a terrible spot of trouble.");
				break;
			case STARTED:
				switch (quest.getStage()) {
				case -1:
					stage = 9;
					sendNPCDialogue(npcId, NORMAL, "Have you found my sweetheart yet?");
					break;
				case 0:
					stage = 10;
					sendNPCDialogue(npcId, NORMAL, "Have you found my sweetheart yet?");
					break;
				}
				break;
			case COMPLETED:
				stage = 16;
				sendNPCDialogue(npcId, NORMAL, "Thanks for rescuing Ernest.");
				break;
			}
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Aha, sounds like a quest. I'll help.",
					"No, I'm looking for something to kill.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 2;
				sendPlayerDialogue(NORMAL, "Aha, sounds like a quest. I'll help.");
				break;
			case OPTION_2:
				stage = 1;
				sendPlayerDialogue(NORMAL, "No, I'm looking for something to kill.");
				break;
			}
			break;
		case 1:
			stage = END_STAGE;
			sendNPCDialogue(npcId, NORMAL, "Oooh, you violent person you.");
			break;
		case 2:
			stage = 3;
			sendNPCDialogue(npcId, NORMAL,
					"Yes yes, I suppose it is a quest. My fiancé, Ernest, and I came upon this house.");
			break;
		case 3:
			stage = 4;
			sendNPCDialogue(npcId, NORMAL,
					"Seeing as we were a little lost, Ernest decided to go in and ask for directions.");
			break;
		case 4:
			stage = 5;
			sendNPCDialogue(npcId, NORMAL,
					"That was an hour ago...and that house looks spooky. Can you go and see if you can find him for me?");
			break;
		case 5:
			end();
			player.getNewQuestManager().startQuest(5);
			break;
		case 6:// decline
			stage = END_STAGE;
			sendNPCDialogue(npcId, SAD, "Oh. I'm so worried. I hope someone will help me soon.");
			break;
		case 7:// accept
			stage = 8;
			sendNPCDialogue(npcId, HAPPY_FACE, "Thank you, thank you. I'm very grateful.");
			break;
		case 8:
			stage = END_STAGE;
			sendNPCDialogue(npcId, HAPPY_FACE,
					"I think I spotted some lights flashing in one of the top floor windows, so you may want to head up there first.");
			break;
		case 9:
			stage = END_STAGE;
			sendPlayerDialogue(NORMAL, "No, not yet.");
			break;
		case 10:
			stage = 11;
			sendPlayerDialogue(NORMAL, "Yes, he's a chicken.");
			break;
		case 11:
			stage = 12;
			this.sendNPCDialogue(npcId, NORMAL, "I know he’s not exactly brave, but I think you’re being a bit harsh.");
			break;
		case 12:
			stage = 13;
			sendPlayerDialogue(NORMAL, "No, no, he’s been turned into an actual chicken by a mad scientist.");
			break;
		case 13:
			stage = 14;
			this.sendNPCDialogue(npcId, SCARED, "Eeeeeek! My poor darling! Why must these things happen to us?");
			break;
		case 14:
			stage = 15;
			sendPlayerDialogue(NORMAL, "I’m doing my best to turn him back.");
			break;
		case 15:
			stage = END_STAGE;
			sendNPCDialogue(npcId, SCARED, "Well, be quick. I’m sure being a chicken can’t be good for him.");
			break;
		case 16:
			stage = 17;
			sendPlayerDialogue(NORMAL, "Where is he now?");
			break;
		case 17:
			stage = END_STAGE;
			sendNPCDialogue(npcId, NORMAL,
					"Oh he went off to talk to some green warty guy. I'm sure he'll be back soon.");
			break;
		case 18:
			end();
			break;
		}
	}

	private static byte END_STAGE = 18;

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
