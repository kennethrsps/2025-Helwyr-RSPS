package com.rs.game.player.dialogue.impl;

import java.util.ArrayList;
import java.util.List;

import com.rs.Settings;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class ReferralDonationD extends Dialogue {

	private int[][] pages;
	private int currentPage;
	private int maxPagesNeeded;
	private int choosenOption;
	private List<Integer> options;
	private int choosenTab;

	@Override
	public void start() {
		choosenTab = -1;
		fillOptions();
		currentPage = 0;
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, getDialogueOptions());
	}

	public void fillOptions() {
		options = new ArrayList<Integer>();
		if (choosenTab == -1)
			for (int i = 0; i < Settings.TABS.length; i++)
				options.add(i);
		else
			for (int i = 0; i < Settings.PRODUCTS.length; i++) {
				if (((int) Settings.PRODUCTS[i][4]) != choosenTab)
					continue;
				options.add(i);
			}
	}

	public String getOptionName(int index) {
		int i = options.get(index);
		return choosenTab == -1 ? Settings.TABS[i]
				: (((String) Settings.PRODUCTS[i][2]) + " $" + ((int) Settings.PRODUCTS[i][1]));
	}

	private String[] getDialogueOptions() {
		maxPagesNeeded = ((int) Math.ceil((double) (options.size() / (currentPage == 0 ? 4.00 : 3.00))));
		maxPagesNeeded = maxPagesNeeded == 0 ? 1 : maxPagesNeeded;
		choosenOption = -1;
		int index = 0;
		pages = new int[maxPagesNeeded][4];
		for (int i = 0; i < pages.length; i++) {
			for (int j = 0; j < (i > 0 ? (pages[i].length - 1) : pages[i].length); j++) {
				if (index > (options.size() - 1))
					continue;
				pages[i][j] = index;
				index++;
			}
		}
		ArrayList<String> dialogueOptions = new ArrayList<String>(5);
		dialogueOptions.add(currentPage == 0 ? (getOptionName(pages[currentPage][0])) : Colors.red+"Back");
		int optionsCount = getOptionsCount();
		if (currentPage == 0)
			optionsCount--;
		for (int i = 0; i < optionsCount; i++) {
			dialogueOptions.add(getOptionName(pages[currentPage][currentPage == 0 ? (i + 1) : i]));
		}

		if (currentPage < (maxPagesNeeded - 1) && getOptionsCount((maxPagesNeeded - 1)) > 0)
			dialogueOptions.add(Colors.red+"More");
		else
			dialogueOptions.add(Colors.red+"Cancel");
		String[] options = new String[dialogueOptions.size()];
		for (int i = 0; i < options.length; i++) {
			String option = dialogueOptions.get(i);
			if (option == null)
				continue;
			options[i] = option;
		}
		return options;
	}

	public int getOptionsCount() {
		int optionsCount = currentPage == 0 ? 1 : 0;
		for (int i = 0; i < (currentPage == 0 ? pages[currentPage].length : (pages[currentPage].length - 1)); i++) {
			if (pages[currentPage][i] != 0)
				optionsCount++;
		}
		return optionsCount;
	}

	public int getOptionsCount(int page) {
		int optionsCount = page == 0 ? 1 : 0;
		for (int i = 0; i < (page == 0 ? pages[page].length : (pages[page].length - 1)); i++) {
			if (pages[page][i] != 0)
				optionsCount++;
		}
		return optionsCount;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			int optionsCount = getOptionsCount();
			switch (componentId) {
			case OPTION_1:
				if (currentPage == 0) {
					choosenOption = pages[currentPage][0];
					sendOptions();
				} else {
					currentPage--;
					sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, getDialogueOptions());
				}
				break;
			case OPTION_2:
				if (currentPage == 0) {
					if (optionsCount > 1)
						choosenOption = pages[currentPage][1];
				} else {
					if (optionsCount > 0)
						choosenOption = pages[currentPage][0];
				}
				if (choosenOption != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_3:
				if (currentPage == 0) {
					if (optionsCount > 2)
						choosenOption = pages[currentPage][2];
				} else {
					if (optionsCount > 1)
						choosenOption = pages[currentPage][1];
				}
				if (choosenOption != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_4:
				if (currentPage == 0) {
					if (optionsCount > 3)
						choosenOption = pages[currentPage][3];
				} else {
					if (optionsCount > 2)
						choosenOption = pages[currentPage][2];
				}
				if (choosenOption != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_5:
				if (currentPage < (maxPagesNeeded - 1) && getOptionsCount((maxPagesNeeded - 1)) > 0) {
					currentPage++;
					sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, getDialogueOptions());
				} else
					end();
				break;
			}
			break;
		case 0:
			int i = options.get(choosenOption);
			switch (componentId) {
			case OPTION_1:
				end();
				int productId = ((int) Settings.PRODUCTS[i][0]);
				int price = ((int) Settings.PRODUCTS[i][1]);
				if (player.getReferralPoints() < price) {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You don't have enough Store Creditss.");
					return;
				}
				player.setReferralPoints(player.getReferralPoints() - price);
				player.completeDonationProcess(player, ""+productId, ""+price, true);
				break;
			case OPTION_2:
				stage = 1;
				sendDialogue(((String) Settings.PRODUCTS[i][3]));
				break;
			case OPTION_3:
				stage = -1;
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, getDialogueOptions());
				break;
			}
			break;
		case 1:
			sendOptions();
			break;
		}
	}

	public void sendOptions() {
		if (choosenTab == -1) {
			choosenTab = options.get(choosenOption) + 1;
			choosenOption = -1;
			fillOptions();
			currentPage = 0;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, getDialogueOptions());
		} else {
			stage = 0;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Purchase",Colors.green+ "See description", Colors.red+"Back to previous menu");
		}
	}

	@Override
	public void finish() {

	}
}
