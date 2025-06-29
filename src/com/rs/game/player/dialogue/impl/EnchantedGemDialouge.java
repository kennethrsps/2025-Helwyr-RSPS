package com.rs.game.player.dialogue.impl;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.content.Magic;
import com.rs.game.player.content.Slayer;
import com.rs.game.player.content.Slayer.Location;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Utils;

public class EnchantedGemDialouge extends Dialogue {

	private int npcId;
	private int[][] pages;
	private int currentPage;
	private int maxPagesNeeded;
	private int choosenLocation;
	private List<Integer> availableLocations;

	@Override
	public void start() {
		availableLocations = new ArrayList<Integer>();
		if (player.getSlayerManager().getCurrentTask() != null) {
			Location[] locations = null;
			if (Slayer.getCustomeLocations(player.getSlayerManager().getCurrentTask()) != null)
				locations = Slayer.getCustomeLocations(player.getSlayerManager().getCurrentTask());
			else
				locations = player.getSlayerManager().getCurrentTask().getLocations();
			for (int i = 0; i < locations.length; i++) {
				Location loc = locations[i];
				if (loc == null)
					continue;
				availableLocations.add(i);
			}
		}
		currentPage = 0;
		npcId = (int) this.parameters[0];
		sendNPCDialogue(npcId, 9827, "'Ello and what are you after then?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			stage = 0;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "How many monsters do I have left?", "Give me a tip.",
					"Teleport me to slayer monster.", "Nothing, Nevermind.");
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				player.getSlayerManager().checkKillsLeft();
				end();
			} else if (componentId == OPTION_2) {
				stage = 1;
				if (player.getSlayerManager().getCurrentTask() == null) {
					sendNPCDialogue(npcId, 9827, "You currently don't have a task.");
					return;
				}
				String[] tipDialouges = player.getSlayerManager().getCurrentTask().getTips();
				if (tipDialouges != null && tipDialouges.length != 0) {
					String chosenDialouge = tipDialouges[Utils.random(tipDialouges.length)];
					if (chosenDialouge == null || chosenDialouge.equals(""))
						sendNPCDialogue(npcId, 9827, "I don't have any tips for you currently.");
					else
						sendNPCDialogue(npcId, 9827, chosenDialouge);
				} else
					sendNPCDialogue(npcId, 9827, "I don't have any tips for you currently.");
			} else if (componentId == OPTION_3) {
				if (player.getSlayerManager().getCurrentTask() == null) {
					stage = 1;
					sendNPCDialogue(npcId, 9827, "You currently don't have a task.");
					return;
				}
				stage = 2;
				sendOptionsDialogue("CHOOSE THE LOCATION YOU WANT", getDialogueOptions());
			} else
				end();
		} else if (stage == 1) {
			end();
		} else if (stage == 2) {
			int locationCount = getLocationCount();
			switch (componentId) {
			case OPTION_1:
				if (currentPage == 0) {
					choosenLocation = pages[currentPage][0];
					sendOptions();
				} else {// back
					currentPage--;
					sendOptionsDialogue("CHOOSE THE LOCATION YOU WANT", getDialogueOptions());
				}
				break;
			case OPTION_2:
				if (currentPage == 0) {
					if (locationCount > 1)
						choosenLocation = pages[currentPage][1];
				} else {
					if (locationCount > 0)
						choosenLocation = pages[currentPage][0];
				}
				if (choosenLocation != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_3:
				if (currentPage == 0) {
					if (locationCount > 2)
						choosenLocation = pages[currentPage][2];
				} else {
					if (locationCount > 1)
						choosenLocation = pages[currentPage][1];
				}
				if (choosenLocation != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_4:
				if (currentPage == 0) {
					if (locationCount > 3)
						choosenLocation = pages[currentPage][3];
				} else {
					if (locationCount > 2)
						choosenLocation = pages[currentPage][2];
				}
				if (choosenLocation != -1)
					sendOptions();
				else
					end();
				break;
			case OPTION_5:
				if (currentPage < (maxPagesNeeded - 1) && getLocationCount((maxPagesNeeded - 1)) > 0) {
					currentPage++;
					sendOptionsDialogue("CHOOSE THE LOCATION YOU WANT", getDialogueOptions());
				} else
					end();
				break;
			}
		} else if (stage == 3) {
			switch (componentId) {
			case OPTION_1:
				end();
				Location loc = null;
				if (Slayer.getCustomeLocations(player.getSlayerManager().getCurrentTask()) != null)
					loc = Slayer.getCustomeLocations(player.getSlayerManager().getCurrentTask())[choosenLocation];
				else
					loc = player.getSlayerManager().getCurrentTask().getLocations()[choosenLocation];
				Magic.sendNormalTeleportSpell(player, 0, 0, loc.getTile());
				if (loc.getControler() != null)
					player.getControlerManager().startControler(loc.getControler());
				break;
			case OPTION_2:
				stage = 2;
				sendOptionsDialogue("CHOOSE THE LOCATION YOU WANT", getDialogueOptions());
				break;
			}
		}
	}

	public String getLocationName(int index) {
		Location[] locations = null;
		if (Slayer.getCustomeLocations(player.getSlayerManager().getCurrentTask()) != null)
			locations = Slayer.getCustomeLocations(player.getSlayerManager().getCurrentTask());
		else
			locations = player.getSlayerManager().getCurrentTask().getLocations();
		int i = availableLocations.get(index);
		return locations[i].getName();
	}

	private String[] getDialogueOptions() {
		maxPagesNeeded = ((int) Math.ceil((double) (availableLocations.size() / (currentPage == 0 ? 4.00 : 3.00))));
		maxPagesNeeded = maxPagesNeeded == 0 ? 1 : maxPagesNeeded;
		choosenLocation = -1;
		int index = 0;
		pages = new int[maxPagesNeeded][4];
		for (int i = 0; i < pages.length; i++) {
			for (int j = 0; j < (i > 0 ? (pages[i].length - 1) : pages[i].length); j++) {
				if (index > (availableLocations.size() - 1))
					continue;
				pages[i][j] = index;
				index++;
			}
		}
		ArrayList<String> dialogueOptions = new ArrayList<String>(5);
		dialogueOptions.add(currentPage == 0 ? (getLocationName(pages[currentPage][0])) : "Back");
		int locationCount = getLocationCount();
		if (currentPage == 0)
			locationCount--;
		for (int i = 0; i < locationCount; i++) {
			dialogueOptions.add(getLocationName(pages[currentPage][currentPage == 0 ? (i + 1) : i]));
		}

		if (currentPage < (maxPagesNeeded - 1) && getLocationCount((maxPagesNeeded - 1)) > 0)
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

	public int getLocationCount() {
		int locationCount = currentPage == 0 ? 1 : 0;
		for (int i = 0; i < (currentPage == 0 ? pages[currentPage].length : (pages[currentPage].length - 1)); i++) {
			if (pages[currentPage][i] != 0)
				locationCount++;
		}
		return locationCount;
	}

	public int getLocationCount(int page) {
		int locationCount = page == 0 ? 1 : 0;
		for (int i = 0; i < (page == 0 ? pages[page].length : (pages[page].length - 1)); i++) {
			if (pages[page][i] != 0)
				locationCount++;
		}
		return locationCount;
	}

	public void sendOptions() {
		stage = 3;
		sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Teleport to location", "Back to previous page.");
	}

	@Override
	public void finish() {

	}
}
