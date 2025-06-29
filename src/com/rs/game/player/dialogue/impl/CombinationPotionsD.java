package com.rs.game.player.dialogue.impl;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.actions.CombinationPotions;
import com.rs.game.player.actions.CombinationPotions.CombinedPotion;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Utils;
import com.rs.game.item.Item;

public class CombinationPotionsD extends Dialogue {

	private int[][] pages;
	private int currentPage;
	private int maxPagesNeeded;
	private Item choosenItem;
	private List<Integer> potionsIds;

	@Override
	public void start() {
		List<CombinedPotion> availableCombinedPotions = CombinationPotions.getAvailableCombinedPotions(player);
		if (availableCombinedPotions == null || availableCombinedPotions.isEmpty()) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You don't have any available combination to mix.");
			return;
		}
		potionsIds = new ArrayList<Integer>();
		for (CombinedPotion availableCombinedPotion : availableCombinedPotions) {
			if (availableCombinedPotion == null || potionsIds.contains(availableCombinedPotion.getPotionId()))
				continue;
			potionsIds.add(availableCombinedPotion.getPotionId());
		}
		currentPage = 0;
		sendOptionsDialogue("CHOOSE THE COMBINATION POTION YOU WANT TO MAKE", getDialogueOptions());
	}

	private String[] getDialogueOptions() {
		maxPagesNeeded = ((int) Math.ceil((double) (potionsIds.size() / (currentPage == 0 ? 4.00 : 3.00))));
		maxPagesNeeded = maxPagesNeeded == 0 ? 1 : maxPagesNeeded;
		choosenItem = null;
		int index = 0;
		pages = new int[maxPagesNeeded][4];
		for (int i = 0; i < pages.length; i++) {
			for (int j = 0; j < (i > 0 ? (pages[i].length - 1) : pages[i].length); j++) {
				if (index > (potionsIds.size() - 1))
					continue;
				pages[i][j] = potionsIds.get(index);
				index++;
			}
		}
		ArrayList<String> dialogueOptions = new ArrayList<String>(5);
		dialogueOptions.add(currentPage == 0
				? (Utils.formatString(new Item(pages[currentPage][0]).getName().replace(" (6)", "")))
				: "Back");
		int itemsCount = getItemsCount();
		if (currentPage == 0)
			itemsCount--;
		for (int i = 0; i < itemsCount; i++) {
			Item item = new Item(pages[currentPage][currentPage == 0 ? (i + 1) : i]);
			if (item.getId() != 0) {
				String name = Utils.formatString(item.getName().replace(" (6)", ""));
				dialogueOptions.add(name);
			}
		}

		if (currentPage < (maxPagesNeeded - 1))
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

	public int getItemsCount() {
		int itemsCount = 0;
		for (int i = 0; i < (currentPage == 0 ? pages[currentPage].length : (pages[currentPage].length - 1)); i++) {
			Item item = new Item(pages[currentPage][i]);
			if (item.getId() != 0)
				itemsCount++;
		}
		return itemsCount;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			int itemsCount = getItemsCount();
			switch (componentId) {
			case OPTION_1:
				if (currentPage == 0) {
					choosenItem = new Item(pages[currentPage][0]);
					sendChooseAmount();
				} else {// back
					currentPage--;
					sendOptionsDialogue("CHOOSE THE COMBINATION POTION YOU WANT TO MAKE", getDialogueOptions());
				}
				break;
			case OPTION_2:
				if (currentPage == 0) {
					if (itemsCount > 1)
						choosenItem = new Item(pages[currentPage][1]);
				} else {
					if (itemsCount > 0)
						choosenItem = new Item(pages[currentPage][0]);
				}
				if (choosenItem != null)
					sendChooseAmount();
				else
					end();
				break;
			case OPTION_3:
				if (currentPage == 0) {
					if (itemsCount > 2)
						choosenItem = new Item(pages[currentPage][2]);
				} else {
					if (itemsCount > 1)
						choosenItem = new Item(pages[currentPage][1]);
				}
				if (choosenItem != null)
					sendChooseAmount();
				else
					end();
				break;
			case OPTION_4:
				if (currentPage == 0) {
					if (itemsCount > 3)
						choosenItem = new Item(pages[currentPage][3]);
				} else {
					if (itemsCount > 2)
						choosenItem = new Item(pages[currentPage][2]);
				}
				if (choosenItem != null)
					sendChooseAmount();
				else
					end();
				break;
			case OPTION_5:
				if (currentPage < (maxPagesNeeded - 1)) {
					currentPage++;
					sendOptionsDialogue("CHOOSE THE COMBINATION POTION YOU WANT TO MAKE", getDialogueOptions());
				} else
					end();
				break;
			}
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
			case OPTION_2:
			case OPTION_4:
				int amount = componentId == OPTION_1 ? 1 : componentId == OPTION_2 ? 5 : Integer.MAX_VALUE;
				CombinationPotions.startCombinationAction(player, amount, choosenItem.getId());
				end();
				break;
			case OPTION_3:// mix X
				player.getTemporaryAttributtes().put("Combination-X", choosenItem.getId());
				player.getPackets().sendInputIntegerScript(true,
						"Enter the amount of " + choosenItem.getName().replace(" (6)", "") + " you want to make:");
				end();
				break;
			case OPTION_5:
				stage = -1;
				sendOptionsDialogue("CHOOSE THE COMBINATION POTION YOU WANT TO MAKE", getDialogueOptions());
				break;
			}
			break;
		}
	}

	public void sendChooseAmount() {
		stage = 0;
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Combine 1", "Combine 5", "Combine X", "Combine All",
				"Back to previous page.");
	}

	@Override
	public void finish() {

	}
}
