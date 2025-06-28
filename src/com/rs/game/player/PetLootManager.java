package com.rs.game.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;

public class PetLootManager implements Serializable {

	private static final long serialVersionUID = 2210738623689267475L;

	private List<RestrictedItem> restrictedItems;
	private boolean toInventory;

	private transient Player player;

	public PetLootManager() {
		restrictedItems = new ArrayList<RestrictedItem>();
	}

	public boolean canLootItem(Item item) {
		if (player.getPet() == null || !player.getPerkManager().petLoot)
			return false;
		if(player.isAtWild()) {
			return false;
		}
		if (item.getId() == 995 || item.getName().toLowerCase().contains("charm"))
			return false;
		for (RestrictedItem restricted : restrictedItems) {
			if (restricted == null || restricted.isDisabled())
				continue;
			if (restricted.getIdentifier() instanceof String) {
				if (item.getName().equalsIgnoreCase((String) restricted.getIdentifier()))
					return false;
			} else if (restricted.getIdentifier() instanceof Integer) {
				if (item.getId() == (int) restricted.getIdentifier())
					return false;
			}
		}
		if (toInventory) {
			if (item.getDefinitions().isStackable() && player.getInventory().getFreeSlots() == 0
					&& !player.getInventory().containsItem(item.getId(), 1)) {
				player.getPackets().sendGameMessage(
						"You don't have enough space in your inventory. Your pet can't loot anymore items.", true);
				return false;
			}
			if (!item.getDefinitions().isStackable() && player.getInventory().getFreeSlots() == 0) {
				player.getPackets().sendGameMessage(
						"You don't have enough space in your inventory. Your pet can't loot anymore items.", true);
				return false;
			}
			player.getInventory().addItem(item);
			player.getPackets().sendGameMessage("Your pet has sent " + item.getName() + " to your inventory.", true);
		} else {
			if (!player.getBank().hasBankSpace() && !player.getBank().containsItem(
					(item.getDefinitions().isNoted() ? item.getDefinitions().getCertId() : item.getId()), 1)) {
				player.getPackets().sendGameMessage(
						"You don't have enough space in your bank. Your pet can't loot anymore items.", true);
				return false;
			}
			player.getBank().addItem(item.getId(), item.getAmount(), true);
			player.getPackets().sendGameMessage("Your pet has sent " + item.getName() + " to your bank.", true);
		}
		return true;
	}

	public List<RestrictedItem> getRestrictedItems() {
		return restrictedItems;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean isToInventory() {
		return toInventory;
	}

	public void setToInventory(boolean toInventory) {
		this.toInventory = toInventory;
	}

	public static class RestrictedItem implements Serializable {

		private static final long serialVersionUID = 4860216178546760228L;

		private Object identifier;
		private boolean disabled;

		public RestrictedItem(Object identifier) {
			this.identifier = identifier;
		}

		public boolean isDisabled() {
			return disabled;
		}

		public void setDisabled(boolean disabled) {
			this.disabled = disabled;
		}

		public Object getIdentifier() {
			return identifier;
		}

		public String getName() {
			return (identifier instanceof String) ? ((String) identifier)
					: (ItemDefinitions.getItemDefinitions((int) identifier).getName());
		}
	}
}
