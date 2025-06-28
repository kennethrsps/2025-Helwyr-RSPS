package com.rs.game.player;

import java.io.Serializable;
import java.util.List;

import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.controllers.Wilderness;
import com.rs.utils.ItemExamines;
import com.rs.utils.ItemWeights;
import com.rs.utils.Lend;
import com.rs.utils.Utils;

public final class Inventory implements Serializable {

	private static final long serialVersionUID = 8842800123753277093L;

	public ItemsContainer<Item> items;

	private transient Player player;
	private transient double inventoryWeight;

	public static final int INVENTORY_INTERFACE = 679;

	public Inventory() {
		items = new ItemsContainer<Item>(28, false);
	}

	public boolean addCoins(Item item, boolean added) {
		if (item.getId() < 0 || item.getAmount() < 0 || !Utils.itemExists(item.getId())
				|| !player.getControlerManager().canAddInventoryItem(item.getId(), item.getAmount()))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (added)
			player.getMoneyPouch().addMoneyMisc(item.getAmount());
		if (!added) {
			if (!items.add(item)) {
				items.add(new Item(item.getId(), items.getFreeSlots()));
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
				refreshItems(itemsBefore);
				return false;
			}
		}
		refreshItems(itemsBefore);
		return true;
	}

	public void refreshConfigs(boolean init) {
		double w = 0;
		for (Item item : items.getItems()) {
			if (item == null)
				continue;
			w += ItemWeights.getWeight(item, false);
		}
		inventoryWeight = w;
		player.getPackets().refreshWeight();
	}

	public double getInventoryWeight() {
		return inventoryWeight;
	}
	
	public boolean addOfflineItem(int itemId, int amount) {
		if (itemId < 0 || amount < 0 || !Utils.itemExists(itemId))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		int amount2 = getNumerOf(itemId);
		int amount3 = (Integer.MAX_VALUE - amount2);
		boolean coinsToPouch = true;
		if (itemId == 995) {
			if (player.getInventory().getNumerOf(995) + amount < 0) {
				return true;
			}
			if (!items.add(new Item(itemId, amount))) {
				items.add(new Item(itemId, items.getFreeSlots()));
				return false;
			}
			return true;
		}
		if (amount + amount2 < 0) {
			if (itemId == 995) {
				if (player.getMoneyPouch().getTotal() + amount < 0) {
					return true;
				}
				player.getMoneyPouch().addOverFlowMoney(amount);
				amount = 0;
				return true;
			}
			return false;
		}
		if (itemId == 995) {
			if (player.getMoneyPouch().getTotal() + amount < 0)
				coinsToPouch = false;
			if (player.getMoneyPouch().getTotal() + amount < Integer.MAX_VALUE && coinsToPouch
					&& !Wilderness.isAtWild(player)) {
				player.getMoneyPouch().addMoneyMisc(amount);
				return true;
			}
			return false;
		}
		if (itemId == 995 && player.getInventory().getNumerOf(995) + amount < 0) {
			player.getMoneyPouch().addMoneyMisc(amount);
			return true;
		}
		if (amount3 <= 0) {
			return false;
		}
		if (!items.add(new Item(itemId, amount))) {
			items.add(new Item(itemId, items.getFreeSlots()));
			return false;
		}
		return true;
	}

	public boolean addItem(int itemId, int amount) {
		if (itemId < 0 || amount < 0 || !Utils.itemExists(itemId)
				|| !player.getControlerManager().canAddInventoryItem(itemId, amount))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		int amount2 = getNumerOf(itemId);
		int amount3 = (Integer.MAX_VALUE - amount2);
		boolean coinsToPouch = true;
		if (itemId == 995) {
			if (player.getInventory().getNumerOf(995) + amount < 0) {
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
				return true;
			}
			if (!items.add(new Item(itemId, amount))) {
				items.add(new Item(itemId, items.getFreeSlots()));
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
				refreshItems(itemsBefore);
				return false;
			}
			refreshItems(itemsBefore);
			return true;
		}
		if (amount + amount2 < 0) {
			if (itemId == 995) {
				if (player.getMoneyPouch().getTotal() + amount < 0) {
					player.sendMessage(Utils.getFormattedNumber(amount) + " coins has been added to the ground.");
					World.updateGroundItem(new Item(995, amount), player, player, 60, 0);
					return true;
				}
				player.getMoneyPouch().addOverFlowMoney(amount);
				amount = 0;
				return true;
			}
			return false;
		}
		if (itemId == 995) {
			if (player.getMoneyPouch().getTotal() + amount < 0)
				coinsToPouch = false;
			if (player.getMoneyPouch().getTotal() + amount < Integer.MAX_VALUE && coinsToPouch
					&& !Wilderness.isAtWild(player)) {
				player.getMoneyPouch().addMoneyMisc(amount);
				return true;
			}
			return false;
		}
		if (itemId == 995 && player.getInventory().getNumerOf(995) + amount < 0) {
			player.getMoneyPouch().addMoneyMisc(amount);
			return true;
		}
		if (amount3 <= 0) {
			player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
			return false;
		}
		if (!items.add(new Item(itemId, amount))) {
			items.add(new Item(itemId, items.getFreeSlots()));
			player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean addItem(Item item) {
		if (item.getId() < 0 || item.getAmount() < 0 || !Utils.itemExists(item.getId())
				|| !player.getControlerManager().canAddInventoryItem(item.getId(), item.getAmount()))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(item)) {
			items.add(new Item(item.getId(), items.getFreeSlots()));
			player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
			refreshItems(itemsBefore);
			return false;
		}
		refreshItems(itemsBefore);
		return true;
	}

	public boolean containsCoins(int amount) {
		if (player.getMoneyPouch().getTotal() >= amount)
			return true;
		return items.contains(new Item(995, amount));
	}
	
	public boolean containsItemsToolBelt(int... id) {
		for (Integer i : id) {
			if(containsOneItem(i) || player.getToolBelt().contains(i));
			return true;
		}
		return false;
	}

	public boolean containsItem(int itemId, int ammount) {
		return items.contains(new Item(itemId, ammount)) || player.getToolBelt().contains(itemId)
				|| player.getToolBeltNew().contains(itemId);
	}

	public boolean containsItems(int[] itemIds, int[] ammounts) {
		int size = itemIds.length > ammounts.length ? ammounts.length : itemIds.length;
		for (int i = 0; i < size; i++)
			if (!items.contains(new Item(itemIds[i], ammounts[i])))
				return false;
		return true;
	}

	public boolean containsItems(Item[] item) {
		for (int i = 0; i < item.length; i++) {
			if (item[i] == null)
				continue;
			if (!items.contains(item[i]))
				return false;
		}
		return true;
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (items.containsOne(new Item(itemId, 1)) || player.getToolBelt().contains(itemId)
					|| player.getToolBeltNew().contains(itemId))
				return true;
		}
		return false;
	}

	public void deleteCoins(int amount) {
		if (player.getMoneyPouch().getTotal() >= amount) {
			player.getMoneyPouch().removeMoneyMisc(amount);
			return;
		}
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(995, amount));
		refreshItems(itemsBefore);
	}

	public boolean deleteOneItem(Item item) {
		if (!player.getControlerManager().canDeleteInventoryItem(item.getId(), item.getAmount())
				|| !containsItem(item.getId(), item.getAmount()))
			return false;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(item);
		refreshItems(itemsBefore);
		return true;
	}

	public void deleteItem(int itemId, int amount) {
		if (!player.getControlerManager().canDeleteInventoryItem(itemId, amount))
			return;
		if (itemId == 995 && player.getMoneyPouch().getTotal() >= amount) {
			player.getMoneyPouch().removeMoneyMisc(amount);
			return;
		}
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public void deleteItem(int slot, Item item) {
		if (!player.getControlerManager().canDeleteInventoryItem(item.getId(), item.getAmount()))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(slot, item);
		refreshItems(itemsBefore);
	}

	public void deleteItem(Item item) {
		if (!player.getControlerManager().canDeleteInventoryItem(item.getId(), item.getAmount()))
			return;
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(item);
		refreshItems(itemsBefore);
	}

	public int getFreeSlots() {
		return items.getFreeSlots();
	}

	public int getAmountOf(int itemId) {
		return items.getNumberOf(itemId);
	}

	public Item getItem(int slot) {
		return items.get(slot);
	}

	public ItemsContainer<Item> getItems() {
		return items;
	}

	public int getItemsContainerSize() {
		return items.getSize();
	}

	public int getNumerOf(int itemId) {
		return items.getNumberOf(itemId);
	}

	public boolean hasFreeSlots() {
		return items.getFreeSlot() != -1;
	}

	public void init() {
		player.getPackets().sendItems(93, items);
	}

	public void refresh() {
		player.getPackets().sendItems(93, items);
		refreshConfigs(true);
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(93, items, slots);
		refreshConfigs(false);
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			if (itemsBefore[index] != items.getItems()[index])
				changedSlots[count++] = index;
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	public boolean removeItems(Item... list) {
		for (Item item : list) {
			if (item == null)
				continue;
			deleteItem(item);
		}
		return true;
	}

	public void reset() {
		Lend lend = LendingManager.getLend(player);
		if (lend != null) {
			Player lender = World.getPlayer(lend.getLendee());
			if (lender.getInventory().containsOneItem(lend.getItem().getDefinitions().getLendId()))
				LendingManager.unLend(lend);
		}
		items.reset();
		init(); // as all slots reseted better just send all again
	}

	public void sendExamine(int slotId) {
		if (slotId >= getItemsContainerSize())
			return;
		Item item = items.get(slotId);
		if (item == null)
			return;
		player.getPackets().sendInventoryMessage(0, slotId, ItemExamines.getExamine(item));
		if (player.isStaff())
			player.getPackets().sendClientConsoleMessage("ItemID: " + item.getId() + " - " + item.getName());
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	/*
	 * No refresh needed its client to who does it :p
	 */
	public void switchItem(int fromSlot, int toSlot) {
		Item[] itemsBefore = items.getItemsCopy();
		Item fromItem = items.get(fromSlot);
		Item toItem = items.get(toSlot);
		items.set(fromSlot, toItem);
		items.set(toSlot, fromItem);
		refreshItems(itemsBefore);
	}

	public void unlockInventoryOptions() {
		player.getPackets().sendIComponentSettings(INVENTORY_INTERFACE, 0, 0, 27, 4554126);
		player.getPackets().sendIComponentSettings(INVENTORY_INTERFACE, 0, 28, 55, 2097152);
	}

	public void set(int i, Item item) {
		items.set(i, item);
		refresh();
	}

	public int getNumberOf(int itemId) {
		return items.getNumberOf(itemId);
	}

	public boolean containsItem(Item item) {
		return items.contains(item);
	}

	public boolean addItemDrop(int itemId, int amount, WorldTile tile) {
		if (itemId < 0 || amount < 0 || !Utils.itemExists(itemId)
				|| !player.getControlerManager().canAddInventoryItem(itemId, amount))
			return false;
		if (itemId == 995) {
			player.getMoneyPouch().addMoney(amount, false);
			return true;
		}
		Item[] itemsBefore = items.getItemsCopy();
		if (!items.add(new Item(itemId, amount)))
			World.updateGroundItem(new Item(itemId, amount), tile, player, 60, 0);
		else
			refreshItems(itemsBefore);
		return true;
	}

	public boolean addItemDrop(int itemId, int amount) {
		return addItemDrop(itemId, amount, new WorldTile(player));
	}

	public boolean addItemMoneyPouch(Item item) {
		if (item.getId() == 995) {
			player.addMoney(item.getAmount());
			return true;
		}
		return addItem(item);
	}

	public void replaceItem(int id, int amount, int slot) {
		Item item = items.get(slot);
		if (item == null)
			return;
		if (id == -1)
			items.set(slot, null);
		else {
			item.setId(id);
			item.setAmount(amount);
		}
		refresh(slot);
	}

	public double getInventoryValue() {
		double value = 0;
		for (int i = 0; i < getItemsContainerSize(); i++) {
			try {
				Item item = getItems().get(i);
				if (item.getId() == 995 || item.getId() == 8851 || item.getId() == 12852)
					value += item.getAmount() / 1000000;
				else
					value += ((GrandExchange.getPrice(item.getId()) / 1000000) * item.getAmount());
			} catch (NullPointerException e) {
			}
		}
		return value;
	}

	public boolean removeItemMoneyPouch(Item item) {
		if (item.getId() == 995)
			return player.getMoneyPouch().removeMoneyMisc(item.getAmount());
		return removeItems(item);
	}

	public int getCoinsAmount() {
		int coins = items.getNumberOf(995) + player.getMoneyPouch().getTotal();
		return coins < 0 ? Integer.MAX_VALUE : coins;
	}

	public boolean containsItemToolBelt(int itemId) {
		return containsItemToolBelt(itemId, 1);
	}

	public boolean containsItemToolBelt(int itemId, int amount) {
		return this.containsItem(itemId, amount);
	}

	public boolean containsItems(List<Item> list) {
		for (Item item : list)
			if (!items.contains(item))
				return false;
		return true;
	}

	public boolean removeItems(List<Item> list) {
		for (Item item : list) {
			if (item == null)
				continue;
			deleteItem(item);
		}
		return true;
	}
}