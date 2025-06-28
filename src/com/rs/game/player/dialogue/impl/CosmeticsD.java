package com.rs.game.player.dialogue.impl;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.item.Item;
import com.rs.game.player.Equipment;
import com.rs.game.player.Equipment.SavedCosmetic;
import com.rs.game.player.content.CosmeticsHandler;
import com.rs.game.player.content.CosmeticsHandler.Cosmetic;
import com.rs.game.player.content.CosmeticsHandler.Cosmetics;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Utils;

public class CosmeticsD extends Dialogue {

	private int slotId;
	private ArrayList<Object[]> availableCostumes;
	private int[][] pages;
	private int currentPage;
	private int maxPagesNeeded;
	private int choosenCosmetic;
	private String keyWord;
	private int choosenOption;

	@Override
	public void start() {
		slotId = (int) this.parameters[0];
		keyWord = parameters.length > 1 ? ((String) this.parameters[1]) : null;
		keyWord = keyWord == null ? null : keyWord.toLowerCase();
		choosenOption = parameters.length > 2 ? ((int) this.parameters[2]) : -1;
		availableCostumes = new ArrayList<Object[]>();
		if (slotId == Equipment.SLOT_ARROWS)
			slotId = 11;
		if (slotId == Equipment.SLOT_RING) {
			if (choosenOption == -1) {
				stage = 1;
				sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Full outfits", "Saved costumes", "Reset costume", "Nevermind");
				return;
			}
			if (choosenOption == 0) {
				for (int i = 0; i <= Cosmetics.MENAPHITE_ANCIENT_OUTFIT.ordinal(); i++) {
					Cosmetics cos = Cosmetics.values()[i];
					if ((keyWord != null && !cos.getName().toLowerCase().contains(keyWord)) || (player.isFilterLocked() && CosmeticsHandler.isRestrictedItem(player, cos.getCosmetics())))
						continue;
					availableCostumes.add(new Object[] { cos.getName(), cos.getCosmetics() });
				}
			} else {
				if (player.getEquipment().getSavedCosmetics().isEmpty()) {
					player.getDialogueManager().startDialogue("SimpleMessage", "You don't have any saved costumes. To save your current costume do ::savecurrentcostume or ::savecurrentcosmetic .");
					return;
				}
				for (int i = 0; i < player.getEquipment().getSavedCosmetics().size(); i++) {
					SavedCosmetic cosmetic = player.getEquipment().getSavedCosmetics().get(i);
					if (cosmetic == null)
						continue;
					if ((keyWord != null && !cosmetic.getCosmeticName().toLowerCase().contains(keyWord)))
						continue;
					availableCostumes.add(new Object[] { cosmetic.getCosmeticName(), i });
				}
			}
		} else {
			for (Cosmetic hides : Cosmetics.HIDE_ALL.getCosmetics()) {
				if (hides.getSlot() != slotId)
					continue;
				availableCostumes.add(new Object[] { hides.getName(), new Cosmetic[] { hides } });
			}
			for (Item item : player.getEquipment().getKeepSakeItems()) {
				if (item == null)
					continue;
				if (item.getDefinitions().getEquipSlot() != slotId || (keyWord != null && !item.getName().toLowerCase().contains(keyWord)))
					continue;
				availableCostumes.add(new Object[] { item.getName(), new Cosmetic[] { new Cosmetic(item.getName(), item.getId(), item.getDefinitions().getEquipSlot()) } });
			}
			for (Cosmetics cos : Cosmetics.values()) {
				if (cos.ordinal() == Cosmetics.HIDE_ALL.ordinal())
					continue;
				for (Cosmetic cosmetic : cos.getCosmetics()) {
					if (cosmetic.getSlot() != slotId || (keyWord != null && !cosmetic.getName().toLowerCase().contains(keyWord)) || (player.isFilterLocked() && CosmeticsHandler.isRestrictedItem(player, cosmetic)))
						continue;
					availableCostumes.add(new Object[] { cosmetic.getName(), new Cosmetic[] { cosmetic } });
				}
			}
		}
		if ((keyWord != null || player.isFilterLocked()) && availableCostumes.isEmpty()) {
			player.getDialogueManager().startDialogue("SimpleMessage", player.isFilterLocked() ? "You don't have any unlocked cosmetic in this slot." : "We couldn't find any available costume with keyword:" + keyWord + " .");
			return;
		}
		currentPage = 0;
		if (player.getEquipment().getCosmeticItems().get(slotId) != null) {
			sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Remove current costume", "Nevermind");
		} else
			sendOptionsDialogue("CHOOSE THE COSTUME YOU WANT, PAGE: " + (currentPage + 1), getDialogueOptions());
	}

	private String[] getDialogueOptions() {
		ArrayList<String> dialogueOptions = new ArrayList<String>(5);
		maxPagesNeeded = ((int) Math.ceil((double) (availableCostumes.size() / (3.00))));
		maxPagesNeeded = maxPagesNeeded == 0 ? 1 : maxPagesNeeded;
		choosenCosmetic = -1;
		pages = new int[maxPagesNeeded][3];
		for (int i = 0; i < pages.length; i++) {
			for (int j = 0; j < (pages[i].length); j++) {
				pages[i][j] = -1;
			}
		}
		int index = 1;
		for (int i = 0; i < pages.length; i++) {
			for (int j = 0; j < pages[i].length; j++) {
				if (index > (availableCostumes.size() - 1))
					continue;
				pages[i][j] = index;
				index++;
			}
		}

		Object[] firstOption = availableCostumes.get(0);
		String firstName = (choosenOption == 1 ? "" : (CosmeticsHandler.isRestrictedItem(player, (Cosmetic[]) firstOption[1]) ? "<col=ff0000>" : "<col=00ff00>")) + Utils.formatPlayerNameForDisplay((String) firstOption[0]);
		dialogueOptions.add(currentPage == 0 ? firstName : "Back");
		int itemsCount = getItemsCount();
		for (int i = 0; i < itemsCount; i++) {
			Object[] option = availableCostumes.get(pages[currentPage][i]);
			String name = (choosenOption == 1 ? "" : (CosmeticsHandler.isRestrictedItem(player, (Cosmetic[]) option[1]) ? "<col=ff0000>" : "<col=00ff00>")) + Utils.formatPlayerNameForDisplay((String) option[0]);
			dialogueOptions.add(name);
		}
		if (currentPage < (maxPagesNeeded - 1) && getItemsCount(currentPage + 1) > 0)
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
	public void run(int interfaceId, int componentId) {
		if (player.getEquipment().getCosmeticItems().get(slotId) != null) {
			if (componentId == OPTION_1)
				player.getEquipment().getCosmeticItems().set(slotId, null);
			player.getGlobalPlayerUpdater().generateAppearenceData();
			end();
			return;
		}
		if (stage == 1) {
			if (componentId == OPTION_3 || componentId == OPTION_4) {
				end();
				if (componentId == OPTION_3) {
					player.getEquipment().resetCosmetics();
					player.getGlobalPlayerUpdater().generateAppearenceData();
					player.getPackets().sendGameMessage("Your current costume has been reset.");
				}
				return;
			}
			choosenOption = componentId == OPTION_1 ? 0 : 1;
			if (choosenOption == 0) {
				for (int i = 0; i <= Cosmetics.MENAPHITE_ANCIENT_OUTFIT.ordinal(); i++) {
					Cosmetics cos = Cosmetics.values()[i];
					if (keyWord != null && !cos.getName().toLowerCase().contains(keyWord) || (player.isFilterLocked() && CosmeticsHandler.isRestrictedItem(player, cos.getCosmetics())))
						continue;
					availableCostumes.add(new Object[] { cos.getName(), cos.getCosmetics() });
				}
			} else {
				if (player.getEquipment().getSavedCosmetics().isEmpty()) {
					player.getDialogueManager().startDialogue("SimpleMessage", "You don't have any saved costumes. To save your current costume do ::savecurrentcostume or ::savecurrentcosmetic .");
					return;
				}
				for (int i = 0; i < player.getEquipment().getSavedCosmetics().size(); i++) {
					SavedCosmetic cosmetic = player.getEquipment().getSavedCosmetics().get(i);
					if (cosmetic == null)
						continue;
					if (keyWord != null && !cosmetic.getCosmeticName().toLowerCase().contains(keyWord))
						continue;
					availableCostumes.add(new Object[] { cosmetic.getCosmeticName(), i });
				}
			}
			stage = -1;
			currentPage = 0;
			sendOptionsDialogue("CHOOSE THE COSTUME YOU WANT, PAGE: " + (currentPage + 1), getDialogueOptions());
			return;
		}
		int itemsCount = getItemsCount();
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				if (currentPage == 0) {
					choosenCosmetic = 0;
					if (slotId == Equipment.SLOT_RING && choosenOption == 1)
						sendSavedCosmeticOptions();
					else
						setCostume();
				} else {// back
					currentPage--;
					sendOptionsDialogue("CHOOSE THE COSTUME YOU WANT, PAGE: " + (currentPage + 1), getDialogueOptions());
				}
				break;
			case OPTION_2:
				if (itemsCount > 0)
					choosenCosmetic = pages[currentPage][0];
				if (choosenCosmetic != -1) {
					if (slotId == Equipment.SLOT_RING && choosenOption == 1)
						sendSavedCosmeticOptions();
					else
						setCostume();
				} else
					end();
				break;
			case OPTION_3:
				if (itemsCount > 1)
					choosenCosmetic = pages[currentPage][1];
				if (choosenCosmetic != -1) {
					if (slotId == Equipment.SLOT_RING && choosenOption == 1)
						sendSavedCosmeticOptions();
					else
						setCostume();
				} else
					end();
				break;
			case OPTION_4:
				if (itemsCount > 2)
					choosenCosmetic = pages[currentPage][2];
				if (choosenCosmetic != -1) {
					if (slotId == Equipment.SLOT_RING && choosenOption == 1)
						sendSavedCosmeticOptions();
					else
						setCostume();
				} else
					end();
				break;
			case OPTION_5:
				if (currentPage < (maxPagesNeeded - 1) && getItemsCount(currentPage + 1) > 0) {
					currentPage++;
					sendOptionsDialogue("CHOOSE THE COSTUME YOU WANT, PAGE: " + (currentPage + 1), getDialogueOptions());
				} else
					end();
				break;
			}
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				List<SavedCosmetic> savedCosmetics = player.getEquipment().getSavedCosmetics();
				player.getEquipment().resetCosmetics();
				Object[] option = availableCostumes.get(choosenCosmetic);
				for (Item item : savedCosmetics.get((int) option[1]).getCosmeticItems().getItems()) {
					if (item == null)
						continue;
					setCostume(new Cosmetic(item.getName(), item.getId(), item.getDefinitions().getEquipSlot()));
				}
				player.getGlobalPlayerUpdater().generateAppearenceData();
				end();
				break;
			case OPTION_2:
				option = availableCostumes.get(choosenCosmetic);
				int index = (int) option[1];
				if (index == -1) {
					end();
					return;
				}
				player.getEquipment().getSavedCosmetics().remove(index);
				player.getPackets().sendGameMessage("You have removed " + ((String) option[0]) + " from your saved cosmetics.");
				end();
				break;
			case OPTION_3:
				stage = -1;
				sendOptionsDialogue("CHOOSE THE COSTUME YOU WANT, PAGE: " + (currentPage + 1), getDialogueOptions());
				break;
			case OPTION_4:
				end();
				break;
			}
			break;
		}

	}

	public int getIndex() {
		if (choosenCosmetic == -1)
			return -1;
		Object[] option = availableCostumes.get(choosenCosmetic);
		return (int) option[1];
	}

	public void sendSavedCosmeticOptions() {
		stage = 0;
		sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE, "Choose this outfit.", "Remove this outfit.", "Back.", "Cancel.");
	}

	public void setCostume() {
		Object[] option = availableCostumes.get(choosenCosmetic);
		Cosmetic[] cosmetics = (Cosmetic[]) option[1];
		if (CosmeticsHandler.isRestrictedItem(player, cosmetics) && cosmetics.length > 1) {
			player.getPackets().sendGameMessage("Some or all of the outfit pieces are locked.");
			end();
			return;
		}
		if (slotId == Equipment.SLOT_RING)
			player.getEquipment().resetCosmetics();
		for (Cosmetic costume : cosmetics) {
			setCostume(costume);
		}
		end();
	}

	public void setCostume(Cosmetic costume) {
		if (CosmeticsHandler.isRestrictedItem(player, costume)) {
			String message = CosmeticsHandler.getEarnedMessageRequirement(player, costume.getItemId());
			player.getPackets().sendGameMessage(message != null ? message : "You don't have this costume unlocked.");
			end();
			return;
		}
		Item item = new Item(costume.getItemId());
		if (costume.getSlot() == Equipment.SLOT_WEAPON) {
			boolean twoHanded = Equipment.isTwoHandedWeapon(item);
			boolean hasShield = player.getEquipment().getCosmeticItems().get(Equipment.SLOT_SHIELD) != null || player.getEquipment().getShieldId() != -1;
			if (twoHanded && hasShield) {
				player.getPackets().sendGameMessage("You can't put on two handed weapon while having a shield.");
				return;
			}
		}
		boolean hasItem = player.getEquipment().containsKeepSakeItem(costume.getItemId());
		x: for (Cosmetics cos : Cosmetics.values()) {
			for (Cosmetic cosmetic : cos.getCosmetics()) {
				if (costume.getItemId() == cosmetic.getItemId()) {
					hasItem = true;
					break x;
				}
			}
		}
		if (!hasItem) {
			player.getPackets().sendGameMessage("<col=ff0000>We couldn't find " + costume.getName() + " in your available outfits.");
			return;
		}
		player.getEquipment().getCosmeticItems().set(costume.getSlot(), item);
		player.getGlobalPlayerUpdater().generateAppearenceData();
	}

	public int getItemsCount() {
		int itemsCount = 0;
		for (int i = 0; i < (pages[currentPage].length); i++) {
			if (pages[currentPage][i] != -1)
				itemsCount++;
		}
		return itemsCount;
	}

	public int getItemsCount(int page) {
		int itemsCount = 0;
		for (int i = 0; i < (pages[page].length - 1); i++) {
			if (pages[page][i] != -1)
				itemsCount++;
		}
		return itemsCount;
	}

	@Override
	public void finish() {
	}

}
