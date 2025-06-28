package com.rs.game.player.content.ancientthrone;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.SerializableFilesManager;

public class Taxation {

	public static double TAX_FLOW = 0;

	public static void displayTaxes(Player player) {
		player.getInterfaceManager().sendInterface(275);
		player.getPackets().sendIComponentText(275, 1, "Taxed Items");
		int index = 10;
		for (Item item : ThroneManager.getThrone().getTax()) {
			String name = item.getDefinitions().getName();
			int amount = item.getAmount();
			player.getPackets().sendIComponentText(275, index, name + " x" + amount);
			index++;
		}
		for (int i = index; i < 316; i++) {
			player.getPackets().sendIComponentText(275, i, "");
		}
	}

	public static void collectTaxes(Player player) {
		if (ThroneManager.getThrone().getTax().isEmpty()) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"The tax chest is empty my " + (player.getGlobalPlayerUpdater().isMale() ? "king" : "queen") + ".");
			return;
		}
		if (player.getInventory().getFreeSlots() == 0) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You have no inventory spaces free to collect taxes.");
			return;
		}
		Item item = ThroneManager.getThrone().getTax().get(0);
		ThroneManager.getThrone().getTax().remove(item);
		if (!item.getDefinitions().isNoted() && item.getDefinitions().getCertId() != -1) {
			item.setId(item.getDefinitions().getCertId());
		}
		player.getInventory().addItem(item);
		ThroneManager.save();
		SerializableFilesManager.savePlayer(player);
	}

	/*public static boolean taxItem(Player player, int itemId, int amount) {
		if (ThroneManager.getThrone().getKing().equals("")) {
			return true;
		}
		if (ThroneManager.getThrone().getTaxRate() == 0) {
			return true;
		}
		if (amount <= 0) {
			return true;
		}
		double div = (ThroneManager.getThrone().getTaxRate() * amount);
		TAX_FLOW += div / 100;
		if (TAX_FLOW >= amount) {
			TAX_FLOW -= amount;
			addTaxItem(itemId, amount);
			player.errorMessage(
					"You've been taxed " + amount + " " + ItemDefinitions.getItemDefinitions(itemId).getName());
			return false;
		}
		return true;
	}*/

	public static void addTaxItem(int itemId, int amount) {
		Item item = new Item(itemId, amount);
		if (item.getDefinitions().getCertId() != -1 && !item.getDefinitions().isNoted()) {
			item.setId(item.getDefinitions().getCertId());
		}
		if (item.getDefinitions().getName().toLowerCase().contains("null")) {
			return;
		}
		if (containsItem(item)) {
			for (Item items : ThroneManager.getThrone().getTax()) {
				if (items.getId() == item.getId()) {
					items.setAmount(items.getAmount() + item.getAmount());
				}
			}
		} else {
			ThroneManager.getThrone().getTax().add(item);
		}
	}

	public static boolean containsItem(Item item) {
		for (Item items : ThroneManager.getThrone().getTax()) {
			if (items.getId() == item.getId()) {
				return true;
			}
		}
		return false;
	}

}
