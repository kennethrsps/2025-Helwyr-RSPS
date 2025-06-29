/*package com.rs.game.player.dialogue.impl;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.player.content.CosmeticsHandler;
import com.rs.game.player.content.CosmeticsHandler.Cosmetics;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class CosmeticShops extends Dialogue {

	public static final int[] REMOTE_FARM_INTERFACE_NAMES = { 30, 32, 34, 36, 38, 49, 51, 53, 55, 57, 59, 62, 64, 66, 68, 70, 72, 74, 76, 190, 79, 81, 83, 85, 88, 90, 92, 94, 97, 99, 101, 104, 106, 108, 110, 115, 117, 119, 121, 123, 125, 131, 127, 129, 2, 173, 175, 177, 182, 184, 186, 188 };
	private List<Cosmetics> availableCosmetics;
	private int[][] pages;
	private int currentPage;
	private int lastOptionIndex;
	private int maxPagesNeeded;
	private int choosenCosmetic;
	private String keyWord;

	@Override
	public void start() {
		currentPage = (int) parameters[0];
		keyWord = (String) this.parameters[1];
		keyWord = keyWord == null ? null : keyWord.toLowerCase();
		choosenCosmetic = -1;
		availableCosmetics = new ArrayList<Cosmetics>();
		for (Cosmetics c : Cosmetics.values()) {
			if (c == Cosmetics.HIDE_ALL || c.getPrice() == -1 || (keyWord != null && !c.getName().toLowerCase().contains(keyWord)))
				continue;
			availableCosmetics.add(c);
		}
		if (keyWord != null && availableCosmetics.isEmpty()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "We couldn't find any available costume with keyword:" + keyWord + " .");
			return;
		}
		for (int i = 0; i < Utils.getInterfaceDefinitionsComponentsSize(1082); i++)
			player.getPackets().sendIComponentText(1082, i, "");
		player.getPackets().sendHideIComponent(1082, 159, true);
		player.getPackets().sendIComponentText(1082, 159, "Cosmetics Store");
		player.getPackets().sendIComponentText(1082, 41, "Costume (Price)");
		player.getPackets().sendIComponentText(1082, 42, "Availability");
		player.getTemporaryAttributtes().put("CosmeticsStore", Boolean.TRUE);
		sendComponents();
		player.getInterfaceManager().sendInterface(1082);
	}

	private void sendComponents() {
		ArrayList<String> components = new ArrayList<String>();
		maxPagesNeeded = ((int) Math.ceil((double) (availableCosmetics.size() - 1 / (50.00))));
		maxPagesNeeded = maxPagesNeeded == 0 ? 1 : maxPagesNeeded;
		pages = new int[maxPagesNeeded][50];
		for (int i = 0; i < pages.length; i++) {
			for (int j = 0; j < (pages[i].length); j++) {
				pages[i][j] = -1;
			}
		}
		int index = 1;
		for (int i = 0; i < pages.length; i++) {
			for (int j = 0; j < pages[i].length; j++) {
				if (index > (availableCosmetics.size() - 1))
					continue;
				pages[i][j] = index;
				index++;
			}
		}
		Cosmetics option = availableCosmetics.get(0);
		int price = option.getPrice();
		String costumeName = option.getName() + " (" + (price == 0 ? "FREE" : price) + ")";
		String availability = CosmeticsHandler.isRestrictedItem(player, option.getCosmetics()) ? Colors.red + "Buy Costume." : Colors.green + "Available.";
		components.add(currentPage == 0 ? costumeName : "Back to previous page");
		components.add(currentPage == 0 ? availability : "Click to go to previous page.");
		int componentsCount = getComponentsCount();
		for (int i = 0; i < componentsCount; i++) {
			option = availableCosmetics.get(pages[currentPage][i]);
			price = option.getPrice();
			costumeName = option.getName() + " (" + price + ")";
			availability = CosmeticsHandler.isRestrictedItem(player, option.getCosmetics()) ? Colors.red + "Buy Costume." : Colors.green + "Available.";
			components.add(costumeName);
			components.add(availability);
		}
		boolean lastPage = currentPage >= (maxPagesNeeded - 1) || getComponentsCount(currentPage + 1) <= 0;
		components.add(lastPage ? "Close Store" : "Move to next page");
		components.add(lastPage ? "Click to close store." : "Click to move to next page.");
		index = 0;
		for (int i = 0; i < components.size(); i += 2) {
			player.getPackets().sendHideIComponent(1082, REMOTE_FARM_INTERFACE_NAMES[index], false);
			player.getPackets().sendHideIComponent(1082, REMOTE_FARM_INTERFACE_NAMES[index] + 1, false);
			player.getPackets().sendIComponentText(1082, REMOTE_FARM_INTERFACE_NAMES[index], components.get(i));
			player.getPackets().sendIComponentText(1082, REMOTE_FARM_INTERFACE_NAMES[index] + 1, components.get(i + 1));
			lastOptionIndex = index;
			index++;
		}
		for (int i = index; i < REMOTE_FARM_INTERFACE_NAMES.length; i++) {
			player.getPackets().sendHideIComponent(1082, REMOTE_FARM_INTERFACE_NAMES[i], true);
			player.getPackets().sendHideIComponent(1082, REMOTE_FARM_INTERFACE_NAMES[i] + 1, true);
		}
		player.getPackets().sendIComponentText(1082, 11, Colors.red + "Red = Not Available" + Colors.white + ".   " + Colors.green + "Green = Available" + Colors.white + ".<br>" + Colors.white + "Click On The \"Buy Costume\" To Buy It. Helwyr Coins Amount: " + player.getHelwyrCoins() + ".");
	}

	private int getComponentsCount() {
		int componentsCount = 0;
		for (int i = 0; i < (pages[currentPage].length); i++) {
			if (pages[currentPage][i] != -1)
				componentsCount++;
		}
		return componentsCount;
	}

	private int getComponentsCount(int page) {
		int componentsCount = 0;
		for (int i = 0; i < (pages[page].length); i++) {
			if (pages[page][i] != -1)
				componentsCount++;
		}
		return componentsCount;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (interfaceId) {
		case 1082:
			int componentsCount = getComponentsCount();
			for (int i = 0; i < REMOTE_FARM_INTERFACE_NAMES.length; i++) {
				if (REMOTE_FARM_INTERFACE_NAMES[i] + 1 == componentId) {
					if (i == 0) {
						if (currentPage == 0) {
							choosenCosmetic = 0;
							sendOptions();
						} else {
							currentPage--;
							sendComponents();
						}
					} else if (i == lastOptionIndex) {
						if (currentPage < (maxPagesNeeded - 1) && getComponentsCount(currentPage + 1) > 0) {
							currentPage++;
							sendComponents();
						} else
							end();
					} else {
						if (componentsCount > i - 1)
							choosenCosmetic = pages[currentPage][i - 1];
						if (choosenCosmetic != -1)
							sendOptions();
						else
							end();
					}
				}
			}
			break;
		case 1188:// options dialogue
			if (choosenCosmetic == -1) {
				player.getInterfaceManager().closeChatBoxInterface();
				return;
			}
			Cosmetics option = availableCosmetics.get(choosenCosmetic);
			switch (componentId) {
			case OPTION_1:
				int price = option.getPrice();
				if (player.getHelwyrCoins() < price) {
					sendDialogue("You don't have enough Helwyr Coins to buy this. You currently have " + player.getHelwyrCoins() + " Helwyr Coins.");
					return;
				}
				player.setHelwyrCoins(player.getHelwyrCoins() - price);
				CosmeticsHandler.UnlockCostume(player, option);
				sendComponents();
				choosenCosmetic = -1;
				player.getInterfaceManager().closeChatBoxInterface();
				break;
			case OPTION_2:
				end();
				CosmeticsHandler.previewCosmetic(player, option, currentPage, keyWord);
				break;
			case OPTION_3:
				choosenCosmetic = -1;
				player.getInterfaceManager().closeChatBoxInterface();
				break;
			}
			break;
		case 1186:// regular dialogue
			choosenCosmetic = -1;
			player.getInterfaceManager().closeChatBoxInterface();
			break;
		}
	}

	private void sendOptions() {
		Cosmetics option = availableCosmetics.get(choosenCosmetic);
		if (!CosmeticsHandler.isRestrictedItem(player, option.getCosmetics())) {
			sendDialogue("You already have this costume unlocked!");
			return;
		}
		int price = option.getPrice();
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, price == 0 ? "Unlock costume" : ("Buy costume" + " (" + price + ")"), "Preview costume", "Cancel");
	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeScreenInterface();
		player.getTemporaryAttributtes().remove("CosmeticsStore");
	}

}
*/