package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;

public class DailyTaskD extends Dialogue {
	private int npcId;

	@Override
	public void start() {
		npcId = (int) this.parameters[0];
		stage = -1;
		this.sendNPCDialogue(npcId, NORMAL, "Hello , how can i help you?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "What is my current daily task?", "Filter update message",
					"Check rewards.", "nevermind.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 3;
				if (player.getDailyTaskManager().getCurrentTask() == null)
					sendNPCDialogue(npcId, NORMAL,
							"You currently dont have an active daily task, You can only get 1 task per day.");
				else
					sendNPCDialogue(npcId, NORMAL,
							"Your daily task is: "
									+ player.getDailyTaskManager().getCurrentTask().getTaskMessage(player) + "; only "
									+ player.getDailyTaskManager().getAmountLeft() + " more to go.");
				break;
			case OPTION_2:
				stage = 3;
				player.getDailyTaskManager().setFilterMessage(!player.getDailyTaskManager().isFilterMessage());
				sendNPCDialogue(npcId, NORMAL,
						"You will " + (player.getDailyTaskManager().isFilterMessage() ? "not receive" : "receive")
								+ " task status update message now.");
				break;
			case OPTION_3:
				if (player.getDailyTaskManager().hasDoubleXpActivated()) {
					stage = 3;
					sendNPCDialogue(npcId, NORMAL, "You already have double xp activated, It will run out in "
							+ player.getDailyTaskManager().getDoubleXpTimeLeft() + ".");
				} else if (player.getDailyTaskManager().canClaimReward()) {
					stage = 1;
					sendNPCDialogue(npcId, NORMAL,
							"I have some double xp for you, do you want to activate your 2 hours of double xp?");
				} else {
					stage = 3;
					sendNPCDialogue(npcId, NORMAL,
							"I dont have anything for you, come back to me when u finish a daily task for a vote token and 2 hours of double xp.");
				}
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		case 1:
			stage = 2;
			this.sendOptionsDialogue("ACTIVATE DOUBLE XP FOR 2 HOURS?", "Yes, please.",
					"No i wand to save it for later.");
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				stage = 3;
				player.getDailyTaskManager().activateDoubleXp();
				sendNPCDialogue(npcId, NORMAL, "I have activated your double xp, It will run out in "
						+ player.getDailyTaskManager().getDoubleXpTimeLeft() + ".");
				break;
			case OPTION_2:
				stage = 3;
				sendNPCDialogue(npcId, NORMAL,
						"Alright but make sure to activate it by today because you will lose it next day.");
				break;
			}
			break;
		case 3:
			end();
			break;
		}
	}

	@Override
	public void finish() {

	}

}
