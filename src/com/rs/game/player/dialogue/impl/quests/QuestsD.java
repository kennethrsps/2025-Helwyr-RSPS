package com.rs.game.player.dialogue.impl.quests;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.newquests.Quest;
import com.rs.game.player.newquests.NewQuestManager.Progress;

public class QuestsD extends Dialogue {

	private int[][] pages;
	private int currentPage;
	private int maxPagesNeeded;
	private int choosenQuest;
	private List<Integer> availableQuests;

	@Override
	public void start() {
		availableQuests = new ArrayList<Integer>();
		for (Quest quest : player.getNewQuestManager().getQuests()) {
			if (quest == null)
				continue;
			availableQuests.add(quest.questId());
		}
		currentPage = 0;
		sendOptionsDialogue("CHOOSE THE QUEST YOU WANT TO CHECK", getDialogueOptions());
	}

	public String getQuestName(int index) {
		return getQuestName(index, true);
	}

	public String getQuestName(int index, boolean withColour) {
		int questId = availableQuests.get(index);
		Progress progress = player.getNewQuestManager().getProgress(questId);
		String colour = progress == Progress.NOT_STARTED ? "<col=ff0000>"
				: progress == Progress.STARTED ? "<col=ffff00>" : "<col=00ff00>";
		return (withColour ? (colour) : (""))
				+ player.getNewQuestManager().getQuests().get(questId).getQuestInformation()[0];
	}

	private String[] getDialogueOptions() {
		maxPagesNeeded = ((int) Math.ceil((double) (availableQuests.size() / (currentPage == 0 ? 4.00 : 3.00))));
		maxPagesNeeded = maxPagesNeeded == 0 ? 1 : maxPagesNeeded;
		choosenQuest = -1;
		int index = 0;
		pages = new int[maxPagesNeeded][4];
		for (int i = 0; i < pages.length; i++) {
			for (int j = 0; j < (i > 0 ? (pages[i].length - 1) : pages[i].length); j++) {
				if (index > (availableQuests.size() - 1))
					continue;
				pages[i][j] = index;
				index++;
			}
		}
		ArrayList<String> dialogueOptions = new ArrayList<String>(5);
		dialogueOptions.add(currentPage == 0 ? (getQuestName(pages[currentPage][0])) : "Back");
		int questsCount = getQuestsCount();
		if (currentPage == 0)
			questsCount--;
		for (int i = 0; i < questsCount; i++) {
			dialogueOptions.add(getQuestName(pages[currentPage][currentPage == 0 ? (i + 1) : i]));
		}

		if (currentPage < (maxPagesNeeded - 1) && getQuestsCount((maxPagesNeeded - 1)) > 0)
			dialogueOptions.add("More");
		else
			dialogueOptions.add("Cancel");
		String[] options = new String[dialogueOptions.size()];
		for (int i = 0; i < options.length; i++) {
			String option = dialogueOptions.get(i);
			if (option == null)
				continue;
			options[i] = option;
		}
		return options;
	}

	public int getQuestsCount() {
		int questsCount = currentPage == 0 ? 1 : 0;
		for (int i = 0; i < (currentPage == 0 ? pages[currentPage].length : (pages[currentPage].length - 1)); i++) {
			if (pages[currentPage][i] != 0)
				questsCount++;
		}
		return questsCount;
	}

	public int getQuestsCount(int page) {
		int questsCount = page == 0 ? 1 : 0;
		for (int i = 0; i < (page == 0 ? pages[page].length : (pages[page].length - 1)); i++) {
			if (pages[page][i] != 0)
				questsCount++;
		}
		return questsCount;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			int questsCount = getQuestsCount();
			switch (componentId) {
			case OPTION_1:
				if (currentPage == 0) {
					choosenQuest = pages[currentPage][0];
					sendOptions();
				} else {// back
					currentPage--;
					sendOptionsDialogue("CHOOSE THE QUEST YOU WANT TO CHECK", getDialogueOptions());
				}
				break;
			case OPTION_2:
				if (currentPage == 0) {
					if (questsCount > 1)
						choosenQuest = pages[currentPage][1];
				} else {
					if (questsCount > 0)
						choosenQuest = pages[currentPage][0];
				}
				if (choosenQuest != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_3:
				if (currentPage == 0) {
					if (questsCount > 2)
						choosenQuest = pages[currentPage][2];
				} else {
					if (questsCount > 1)
						choosenQuest = pages[currentPage][1];
				}
				if (choosenQuest != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_4:
				if (currentPage == 0) {
					if (questsCount > 3)
						choosenQuest = pages[currentPage][3];
				} else {
					if (questsCount > 2)
						choosenQuest = pages[currentPage][2];
				}
				if (choosenQuest != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_5:
				if (currentPage < (maxPagesNeeded - 1) && getQuestsCount((maxPagesNeeded - 1)) > 0) {
					currentPage++;
					sendOptionsDialogue("CHOOSE THE QUEST YOU WANT TO CHECK", getDialogueOptions());
				} else
					end();
				break;
			}
			break;
		case 0:
			int questId = availableQuests.get(choosenQuest);
			switch (componentId) {
			case OPTION_1:
				end();
				player.getNewQuestManager().sendQuestJournalInterface(questId);
				break;
			case OPTION_2:
				end();
				player.getNewQuestManager().getQuests().get(questId).teleportToStartPoint();
				break;
			case OPTION_3:
				stage = -1;
				sendOptionsDialogue("CHOOSE THE QUEST YOU WANT TO CHECK", getDialogueOptions());
				break;
			}
			break;
		}
	}

	public void sendOptions() {
		stage = 0;
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "View quest journal", "Teleport to start point",
				"Back to previous menu");
	}

	@Override
	public void finish() {
	}

}
