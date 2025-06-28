package com.rs.game.player.dialogue.impl;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.BanksManager.ExtraBank;
import com.rs.game.player.dialogue.Dialogue;

public class BanksManagerD extends Dialogue {// this dialogue is harder than the
												// system lol

	private int[][] pages;
	private int currentPage;
	private int maxPagesNeeded;
	private int choosenBank;
	private List<ExtraBank> availableBanks;

	@Override
	public void start() {
		availableBanks = player.getBanksManager().getBanks();
		currentPage = 0;
		sendOptionsDialogue("CHOOSE THE BANK YOU WANT TO EDIT", getDialogueOptions());
	}

	public String getBankName(int index) {
		return availableBanks.get(index).getName()
				+ (player.getBanksManager().getActiveBankId() == index ? " (Active)" : "");
	}

	private String[] getDialogueOptions() {
		maxPagesNeeded = ((int) Math.ceil((double) (availableBanks.size() / (currentPage == 0 ? 4.00 : 3.00))));
		maxPagesNeeded = maxPagesNeeded == 0 ? 1 : maxPagesNeeded;
		choosenBank = -1;
		int index = 0;
		pages = new int[maxPagesNeeded][4];
		for (int i = 0; i < pages.length; i++) {
			for (int j = 0; j < (i > 0 ? (pages[i].length - 1) : pages[i].length); j++) {
				if (index > (availableBanks.size() - 1))
					continue;
				pages[i][j] = index;
				index++;
			}
		}
		ArrayList<String> dialogueOptions = new ArrayList<String>(5);
		dialogueOptions.add(currentPage == 0 ? (getBankName(pages[currentPage][0])) : "Back");
		int banksCount = getBanksCount();
		if (currentPage == 0)
			banksCount--;
		for (int i = 0; i < banksCount; i++) {
			dialogueOptions.add(getBankName(pages[currentPage][currentPage == 0 ? (i + 1) : i]));
		}

		if (currentPage < (maxPagesNeeded - 1) && getBanksCount((maxPagesNeeded - 1)) > 0)
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
		System.out.println("needed " + maxPagesNeeded);
		return options;
	}

	public int getBanksCount() {
		int banksCount = currentPage == 0 ? 1 : 0;
		for (int i = 0; i < (currentPage == 0 ? pages[currentPage].length : (pages[currentPage].length - 1)); i++) {
			if (pages[currentPage][i] != 0)
				banksCount++;
		}
		return banksCount;
	}

	public int getBanksCount(int page) {
		int banksCount = page == 0 ? 1 : 0;
		for (int i = 0; i < (page == 0 ? pages[page].length : (pages[page].length - 1)); i++) {
			if (pages[page][i] != 0)
				banksCount++;
		}
		return banksCount;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			int banksCount = getBanksCount();
			switch (componentId) {
			case OPTION_1:
				if (currentPage == 0) {
					choosenBank = pages[currentPage][0];
					sendOptions();
				} else {// back
					currentPage--;
					sendOptionsDialogue("CHOOSE THE BANK YOU WANT TO EDIT", getDialogueOptions());
				}
				break;
			case OPTION_2:
				if (currentPage == 0) {
					if (banksCount > 1)
						choosenBank = pages[currentPage][1];
				} else {
					if (banksCount > 0)
						choosenBank = pages[currentPage][0];
				}
				if (choosenBank != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_3:
				if (currentPage == 0) {
					if (banksCount > 2)
						choosenBank = pages[currentPage][2];
				} else {
					if (banksCount > 1)
						choosenBank = pages[currentPage][1];
				}
				if (choosenBank != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_4:
				if (currentPage == 0) {
					if (banksCount > 3)
						choosenBank = pages[currentPage][3];
				} else {
					if (banksCount > 2)
						choosenBank = pages[currentPage][2];
				}
				if (choosenBank != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_5:
				if (currentPage < (maxPagesNeeded - 1) && getBanksCount((maxPagesNeeded - 1)) > 0) {
					currentPage++;
					sendOptionsDialogue("CHOOSE THE BANK YOU WANT TO EDIT", getDialogueOptions());
				} else
					end();
				break;
			}
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				end();
				player.getTemporaryAttributtes().put("RENAME_BANK", choosenBank);
				player.getPackets().sendInputNameScript("Enter new name: ");
				break;
			case OPTION_2:
				if (player.getBanksManager().getActiveBankId() == choosenBank) {
					stage = -1;
					sendOptionsDialogue("CHOOSE THE BANK YOU WANT TO EDIT", getDialogueOptions());
					break;
				}
				player.getPackets().sendGameMessage(getBankName(choosenBank) + " has been set as your active bank.");
				player.getBanksManager().setActiveBankId(choosenBank, true);
				end();
				break;
			case OPTION_3:
				stage = -1;
				sendOptionsDialogue("CHOOSE THE BANK YOU WANT TO EDIT", getDialogueOptions());
				break;
			}
			break;
		}
	}

	public void sendOptions() {
		stage = 0;
		if (player.getBanksManager().getActiveBankId() != choosenBank)
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Rename Bank", "Set Active", "Back to previous page.");
		else
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Rename Bank", "Back to previous page.");
	}

	@Override
	public void finish() {

	}
}
