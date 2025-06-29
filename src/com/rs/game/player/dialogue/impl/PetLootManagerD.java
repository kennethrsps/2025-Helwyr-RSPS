package com.rs.game.player.dialogue.impl;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.PetLootManager;
import com.rs.game.player.PetLootManager.RestrictedItem;
import com.rs.game.player.dialogue.Dialogue;

public class PetLootManagerD extends Dialogue {

	private PetLootManager manager;
	private int[][] pages;
	private int currentPage;
	private int maxPagesNeeded;
	private int choosenItem;
	private List<RestrictedItem> restrictedItems;

	@Override
	public void start() {
		manager = player.getPetLootManager();
		restrictedItems = manager.getRestrictedItems();
		currentPage = 0;
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
				(manager.isToInventory() ? "Send loot to bank" : "Send loot to inventory"), "Manage restricted items",
				"Cancel");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				manager.setToInventory(!manager.isToInventory());
				player.getDialogueManager().startDialogue("SimpleMessage",
						"Your pet will send items to your " + (manager.isToInventory() ? "Inventory" : "Bank") + ".");
				break;
			case OPTION_2:
				stage = 0;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Check restricted items", "Add restricted item by name",
						"Add restricted item by id", "Back to previous menu", "Cancel");
				break;
			case OPTION_3:
				end();
				break;
			}
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				if (restrictedItems.isEmpty()) {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You don't have any restricted items on the list.");
					return;
				}
				stage = 1;
				currentPage = 0;
				sendOptionsDialogue("CHOOSE THE RESTRICTED ITEM YOU WANT TO EDIT", getDialogueOptions());
				break;
			case OPTION_2:
				end();
				player.getTemporaryAttributtes().put("ADD_RESTRICT_NAME", Boolean.TRUE);
				player.getPackets().sendInputNameScript("Enter the Name of the item you want to restrict: ");
				break;
			case OPTION_3:
				end();
				player.getTemporaryAttributtes().put("ADD_RESTRICT_ID", Boolean.TRUE);
				player.getPackets().sendInputIntegerScript(true, "Enter the id of the item you want to restrict: ");
				break;
			case OPTION_4:
				stage = -1;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
						(manager.isToInventory() ? "Send loot to bank" : "Send loot to inventory"),
						"Manage restricted items", "Cancel");
				break;
			case OPTION_5:
				end();
				break;
			}
			break;
		case 1:
			int itemsCount = getItemsCount();
			switch (componentId) {
			case OPTION_1:
				if (currentPage == 0) {
					choosenItem = pages[currentPage][0];
					sendOptions();
				} else {// back
					currentPage--;
					sendOptionsDialogue("CHOOSE THE RESTRICTED ITEM YOU WANT TO EDIT", getDialogueOptions());
				}
				break;
			case OPTION_2:
				if (currentPage == 0) {
					if (itemsCount > 1)
						choosenItem = pages[currentPage][1];
				} else {
					if (itemsCount > 0)
						choosenItem = pages[currentPage][0];
				}
				if (choosenItem != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_3:
				if (currentPage == 0) {
					if (itemsCount > 2)
						choosenItem = pages[currentPage][2];
				} else {
					if (itemsCount > 1)
						choosenItem = pages[currentPage][1];
				}
				if (choosenItem != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_4:
				if (currentPage == 0) {
					if (itemsCount > 3)
						choosenItem = pages[currentPage][3];
				} else {
					if (itemsCount > 2)
						choosenItem = pages[currentPage][2];
				}
				if (choosenItem != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_5:
				if (currentPage < (maxPagesNeeded - 1) && getItemsCount((maxPagesNeeded - 1)) > 0) {
					currentPage++;
					sendOptionsDialogue("CHOOSE THE RESTRICTED ITEM YOU WANT TO EDIT", getDialogueOptions());
				} else
					end();
				break;
			}
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				end();
				manager.getRestrictedItems().get(choosenItem).setDisabled(!isDisabled(choosenItem));
				player.getDialogueManager().startDialogue("SimpleMessage",
						"Your pet " + (isDisabled(choosenItem) ? "will not loot " : "will loot ")
								+ restrictedItems.get(choosenItem).getName() + " from now on.");
				break;
			case OPTION_2:
				end();
				player.getDialogueManager().startDialogue("SimpleMessage", restrictedItems.get(choosenItem).getName()
						+ " has been removed from your restricted items list.");
				manager.getRestrictedItems().remove(choosenItem);
				break;
			case OPTION_3:
				stage = 1;
				sendOptionsDialogue("CHOOSE THE RESTRICTED ITEM YOU WANT TO EDIT", getDialogueOptions());
				break;
			}
			break;
		}

	}

	public void sendOptions() {
		stage = 2;
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, (isDisabled(choosenItem) ? "Enable item" : "Disable item"),
				"Remove item", "Back to previous page.");
	}

	public String getItemName(int index) {
		return restrictedItems.get(index).getName() + (isDisabled(index) ? " (Disabled)" : " (Enabled)");
	}

	public boolean isDisabled(int index) {
		return restrictedItems.get(index).isDisabled();
	}

	public int getItemsCount() {
		int itemsCount = currentPage == 0 ? 1 : 0;
		for (int i = 0; i < (currentPage == 0 ? pages[currentPage].length : (pages[currentPage].length - 1)); i++) {
			if (pages[currentPage][i] != 0)
				itemsCount++;
		}
		return itemsCount;
	}

	public int getItemsCount(int page) {
		int itemsCount = page == 0 ? 1 : 0;
		for (int i = 0; i < (page == 0 ? pages[page].length : (pages[page].length - 1)); i++) {
			if (pages[page][i] != 0)
				itemsCount++;
		}
		return itemsCount;
	}

	private String[] getDialogueOptions() {
		maxPagesNeeded = ((int) Math.ceil((double) (restrictedItems.size() / (currentPage == 0 ? 4.00 : 3.00))));
		maxPagesNeeded = maxPagesNeeded == 0 ? 1 : maxPagesNeeded;
		choosenItem = -1;
		int index = 0;
		pages = new int[maxPagesNeeded][4];
		for (int i = 0; i < pages.length; i++) {
			for (int j = 0; j < (i > 0 ? (pages[i].length - 1) : pages[i].length); j++) {
				if (index > (restrictedItems.size() - 1))
					continue;
				pages[i][j] = index;
				index++;
			}
		}
		ArrayList<String> dialogueOptions = new ArrayList<String>(5);
		dialogueOptions.add(currentPage == 0 ? (getItemName(pages[currentPage][0])) : "Back");
		int itemsCount = getItemsCount();
		if (currentPage == 0)
			itemsCount--;
		for (int i = 0; i < itemsCount; i++) {
			dialogueOptions.add(getItemName(pages[currentPage][currentPage == 0 ? (i + 1) : i]));
		}

		if (currentPage < (maxPagesNeeded - 1) && getItemsCount((maxPagesNeeded - 1)) > 0)
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

	@Override
	public void finish() {
	}

}
