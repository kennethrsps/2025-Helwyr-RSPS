package com.rs.game.player.content.clans.content;

import java.io.Serializable;
import java.util.ArrayList;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.content.ItemConstants;
import com.rs.utils.ItemExamines;
import com.rs.utils.Logger;

public class ClanBank implements Serializable {

	private static final long serialVersionUID = -5779788019572863016L;

	private Item[][] bankTabs;

	private transient ArrayList<Player> players = new ArrayList<Player>();
	private transient Item[] lastContainerCopy;

	public static final long MAX_BANK_SIZE = 506;

	public ClanBank() {
		bankTabs = new Item[1][0];
		players = new ArrayList<Player>();
	}

	public void removeItem(int id) {
		if (bankTabs != null) {
			for (int i = 0; i < bankTabs.length; i++) {
				for (int i2 = 0; i2 < bankTabs[i].length; i2++) {
					if (bankTabs[i][i2].getId() == id)
						bankTabs[i][i2].setId(0); // dwarf remains
				}
			}
		}
	}

	@SuppressWarnings("null")
	public void setItem(Player player, int slotId, int amt) {
		Item item = getItem(slotId);
		if (item == null) {
			item.setAmount(amt);
			refreshItems(player);
			refreshTabs(player);
			player.getBank().refreshViewingTab();
		}
	}

	public void refreshTabs(Player player) {
		for (int slot = 1; slot < 9; slot++)
			player.getBank().refreshTab(slot);
	}

	public int getTabSize(int slot) {
		if (slot >= bankTabs.length)
			return 0;
		return bankTabs[slot].length;
	}

	public void withdrawLastAmount(Player player, int bankSlot) {
		withdrawItem(player, bankSlot, player.getBank().getLastX());
	}

	public void withdrawItemButOne(Player player, int fakeSlot) {
		int[] fromRealSlot = getRealSlot(fakeSlot);
		Item item = getItem(fromRealSlot);
		if (item == null)
			return;
		if (item.getAmount() <= 1) {
			player.getPackets().sendGameMessage("You only have one of this item in your bank");
			return;
		}
		withdrawItem(player, fakeSlot, item.getAmount() - 1);
	}

	public void depositLastAmount(Player player, int bankSlot) {
		depositItem(player, bankSlot, player.getBank().getLastX(), true);
	}

	public void depositAllInventory(Player player, boolean banking) {
		if (ClanBank.MAX_BANK_SIZE - getBankSize() < player.getInventory().getItems().getSize()) {
			player.getPackets().sendGameMessage("Not enough space in your bank.");
			return;
		}
		for (int i = 0; i < 28; i++)
			depositItem(player, i, Integer.MAX_VALUE, false);
		player.getBank().refreshTab(player.getBank().getCurrentTab());
		refreshItems(player);
	}

	public void depositAllBob(Player player, boolean banking) {
		Familiar familiar = player.getFamiliar();
		if (familiar == null || familiar.getBob() == null)
			return;
		int space = addItems(player, familiar.getBob().getBeastItems().getItems(), banking);
		if (space != 0) {
			for (int i = 0; i < space; i++)
				familiar.getBob().getBeastItems().set(i, null);
			familiar.getBob().sendInterItems();
		}
		if (space < familiar.getBob().getBeastItems().getSize()) {
			player.getPackets().sendGameMessage("Not enough space in your bank.");
			return;
		}
	}

	public void depositAllEquipment(Player player, boolean banking) {
		int space = addItems(player, player.getEquipment().getItems().getItems(), banking);
		if (space != 0) {
			for (int i = 0; i < space; i++)
				player.getEquipment().getItems().set(i, null);
			player.getEquipment().init();
			player.getGlobalPlayerUpdater().generateAppearenceData();
		}
		if (space < player.getEquipment().getItems().getSize()) {
			player.getPackets().sendGameMessage("Not enough space in your bank.");
			return;
		}
	}

	public void collapse(Player player, int tabId) {
		if (tabId == 0 || tabId >= bankTabs.length)
			return;
		Item[] items = bankTabs[tabId];
		for (Item item : items)
			removeItem(player, getItemSlot(item.getId()), item.getAmount(), false, true);
		for (Item item : items)
			addItem(player, item.getId(), item.getAmount(), 0, false);
		player.getBank().refreshTabs();
		refreshItems(player);
	}

	public void switchItem(Player player, int fromSlot, int toSlot, int fromComponentId, int toComponentId) {
		if (toSlot == 65535) {
			int toTab = toComponentId >= 76 ? 8 - (84 - toComponentId) : 9 - ((toComponentId - 46) / 2);
			if (toTab < 0 || toTab > 9)
				return;
			if (bankTabs.length == toTab) {
				int[] fromRealSlot = getRealSlot(fromSlot);
				if (fromRealSlot == null)
					return;
				if (toTab == fromRealSlot[0]) {
					switchItem(player, fromSlot, getStartSlot(toTab));
					return;
				}
				Item item = getItem(fromRealSlot);
				if (item == null)
					return;
				removeItem(player, fromSlot, item.getAmount(), false, true);
				createTab();
				bankTabs[bankTabs.length - 1] = new Item[] { item };
				player.getBank().refreshTab(fromRealSlot[0]);
				player.getBank().refreshTab(toTab);
				refreshItems(player);
			} else if (bankTabs.length > toTab) {
				int[] fromRealSlot = getRealSlot(fromSlot);
				if (fromRealSlot == null)
					return;
				if (toTab == fromRealSlot[0]) {
					switchItem(player, fromSlot, getStartSlot(toTab));
					return;
				}
				Item item = getItem(fromRealSlot);
				if (item == null)
					return;
				boolean removed = removeItem(player, fromSlot, item.getAmount(), false, true);
				if (!removed)
					player.getBank().refreshTab(fromRealSlot[0]);
				else if (fromRealSlot[0] != 0 && toTab >= fromRealSlot[0])
					toTab -= 1;
				player.getBank().refreshTab(fromRealSlot[0]);
				addItem(player, item.getId(), item.getAmount(), toTab, true);
			}
		} else {
			if (!player.getBank().isInsertItems())
				switchItem(player, fromSlot, toSlot);//
			else
				insert(player, fromSlot, toSlot);
		}
	}

	public void insert(Player player, int fromSlot, int toSlot) {
		int[] fromRealSlot = getRealSlot(fromSlot);
		Item fromItem = getItem(fromRealSlot);
		if (fromItem == null)
			return;

		int[] toRealSlot = getRealSlot(toSlot);
		Item toItem = getItem(toRealSlot);
		if (toItem == null)
			return;

		if (toRealSlot[0] != fromRealSlot[0])
			return;

		if (toRealSlot[1] > fromRealSlot[1]) {
			for (int slot = fromRealSlot[1]; slot < toRealSlot[1]; slot++) {
				Item temp = bankTabs[toRealSlot[0]][slot];
				bankTabs[toRealSlot[0]][slot] = bankTabs[toRealSlot[0]][slot + 1];
				bankTabs[toRealSlot[0]][slot + 1] = temp;
			}
		} else if (fromRealSlot[1] > toRealSlot[1]) {
			for (int slot = fromRealSlot[1]; slot > toRealSlot[1]; slot--) {
				Item temp = bankTabs[toRealSlot[0]][slot];
				bankTabs[toRealSlot[0]][slot] = bankTabs[toRealSlot[0]][slot - 1];
				bankTabs[toRealSlot[0]][slot - 1] = temp;
			}
		}
		refreshItems(player);
	}

	public void switchItem(Player player, int fromSlot, int toSlot) {
		int[] fromRealSlot = getRealSlot(fromSlot);
		Item fromItem = getItem(fromRealSlot);
		if (fromItem == null)
			return;
		int[] toRealSlot = getRealSlot(toSlot);
		Item toItem = getItem(toRealSlot);
		if (toItem == null)
			return;
		bankTabs[fromRealSlot[0]][fromRealSlot[1]] = toItem;
		bankTabs[toRealSlot[0]][toRealSlot[1]] = fromItem;
		player.getBank().refreshTab(fromRealSlot[0]);
		if (fromRealSlot[0] != toRealSlot[0])
			player.getBank().refreshTab(toRealSlot[0]);
		refreshItems(player);
	}

	public void openBank(Player player) {
		player.getTemporaryAttributtes().put("clanBank", Boolean.TRUE);
		player.getInterfaceManager().sendInterface(762);
		player.getInterfaceManager().sendInventoryInterface(763);
		player.getPackets().sendIComponentText(762, 47, "Clan Bank of: "+player.getClanManager().getClan().getClanName());
		player.getBank().refreshViewingTab();
		refreshTabs(player);
		player.getBank().unlockButtons();
		sendItems(player);
		player.getBank().refreshLastX();
		if (players == null)
			players = new ArrayList<Player>();
		players.add(player);
	}

	public void createTab() {
		int slot = bankTabs.length;
		Item[][] tabs = new Item[slot + 1][];
		System.arraycopy(bankTabs, 0, tabs, 0, slot);
		tabs[slot] = new Item[0];
		bankTabs = tabs;
	}

	public void destroyTab(Player player, int slot) {
		Item[][] tabs = new Item[bankTabs.length - 1][];
		System.arraycopy(bankTabs, 0, tabs, 0, slot);
		System.arraycopy(bankTabs, slot + 1, tabs, slot, bankTabs.length - slot - 1);
		bankTabs = tabs;
		if (player.getBank().getCurrentTab() != 0 && player.getBank().getCurrentTab() >= slot)
			player.getBank().setCurrentTab(player.getBank().getCurrentTab()-1);
	}

	public boolean hasBankSpace() {
		return getBankSize() < MAX_BANK_SIZE;
	}

	public void withdrawItem(Player player, int bankSlot, int quantity) {
		if (quantity < 1)
			return;
		Item item = getItem(getRealSlot(bankSlot));
		if (item == null)
			return;
		if (item.getAmount() < quantity)
			item = new Item(item.getId(), item.getAmount());
		else
			item = new Item(item.getId(), quantity);
		boolean noted = false;
		ItemDefinitions defs = item.getDefinitions();
		if (player.getBank().getWithdrawNotes()) {
			if (!defs.isNoted() && defs.getCertId() != -1) {
				item.setId(defs.getCertId());
				noted = true;
			} else
				player.getPackets().sendGameMessage("You cannot withdraw this item as a note.");
		}
		if (noted || defs.isStackable()) {
			if (player.getInventory().getItems().containsOne(item)) {
				int slot = player.getInventory().getItems().getThisItemSlot(item);
				Item invItem = player.getInventory().getItems().get(slot);
				if (invItem.getAmount() + item.getAmount() <= 0) {
					item.setAmount(Integer.MAX_VALUE - invItem.getAmount());
					player.getPackets().sendGameMessage("Not enough space in your inventory.");
				}
			} else if (!player.getInventory().hasFreeSlots()) {
				player.getPackets().sendGameMessage("Not enough space in your inventory.");
				return;
			}
		} else {
			int freeSlots = player.getInventory().getFreeSlots();
			if (freeSlots == 0) {
				player.getPackets().sendGameMessage("Not enough space in your inventory.");
				return;
			}
			if (freeSlots < item.getAmount()) {
				item.setAmount(freeSlots);
				player.getPackets().sendGameMessage("Not enough space in your inventory.");
			}
		}
		removeItem(player, bankSlot, item.getAmount(), true, false);
		player.getInventory().addItem(item);
	}

	public void sendExamine(Player player, int fakeSlot) {
		int[] slot = getRealSlot(fakeSlot);
		if (slot == null)
			return;
		Item item = bankTabs[slot[0]][slot[1]];
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public void depositItem(Player player, int invSlot, int quantity, boolean refresh) {
		if (quantity < 1 || invSlot < 0 || invSlot > 27)
			return;
		Item item = player.getInventory().getItem(invSlot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item)) {
			player.getPackets().sendGameMessage(item.getDefinitions().getName()+" is not tradeable and therefore cannot be stored in the Clan Bank.");
			return;
		}
		int amt = player.getInventory().getItems().getNumberOf(item);
		if (amt < quantity)
			item = new Item(item.getId(), amt);
		else
			item = new Item(item.getId(), quantity);
		ItemDefinitions defs = item.getDefinitions();
		int originalId = item.getId();
		if (defs.isNoted() && defs.getCertId() != -1)
			item.setId(defs.getCertId());
		Item bankedItem = getItem(item.getId());
		if (bankedItem != null) {
			if (bankedItem.getAmount() + item.getAmount() <= 0) {
				item.setAmount(Integer.MAX_VALUE - bankedItem.getAmount());
				player.getPackets().sendGameMessage("Not enough space in your bank.");
			}
		} else if (!hasBankSpace()) {
			player.getPackets().sendGameMessage("Not enough space in your bank.");
			return;
		}
		player.getInventory().deleteItem(invSlot, new Item(originalId, item.getAmount()));
		addItem(player, item, refresh);
	}


	private void addItem(Player player, Item item, boolean refresh) {
		addItem(player, item.getId(), item.getAmount(), refresh);
	}

	public int addItems(Player player, Item[] items, boolean refresh) {
		int space = (int) (MAX_BANK_SIZE - getBankSize());
		if (space != 0) {
			space = (space < items.length ? space : items.length);
			for (int i = 0; i < space; i++) {
				if (items[i] == null)
					continue;
				addItem(player, items[i], false);
			}
			if (refresh) {
				refreshTabs(player);
				refreshItems(player);
			}
		}
		return space;
	}

	public void addItem(Player player, int id, int quantity, boolean refresh) {
		int amountInBank = (getItem(id) == null ? 0 : getItem(id).getAmount());
		if(amountInBank > Integer.MAX_VALUE - quantity) {
			return;
		}
		addItem(player, id, quantity, player.getBank().getCurrentTab(), refresh);
	}

	public void addItem(Player player, int id, int quantity, int creationTab, boolean refresh) {
		int[] slotInfo = getItemSlot(id);
		if (slotInfo == null) {
			if (creationTab >= bankTabs.length)
				creationTab = bankTabs.length - 1;
			if (creationTab < 0) // fixed now, alex
				creationTab = 0;
			int slot = bankTabs[creationTab].length;
			Item[] tab = new Item[slot + 1];
			System.arraycopy(bankTabs[creationTab], 0, tab, 0, slot);
			tab[slot] = new Item(id, quantity);
			bankTabs[creationTab] = tab;
			if (refresh)
				player.getBank().refreshTab(creationTab);
		} else {
			Item item = bankTabs[slotInfo[0]][slotInfo[1]];
			bankTabs[slotInfo[0]][slotInfo[1]] = new Item(item.getId(), item.getAmount() + quantity);
		}
		if (refresh)
			refreshItems(player);
	}

	public boolean removeItem(Player player, int fakeSlot, int quantity, boolean refresh, boolean forceDestroy) {
		return removeItem(player, getRealSlot(fakeSlot), quantity, refresh, forceDestroy);
	}

	public boolean removeItem(Player player, int[] slot, int quantity, boolean refresh, boolean forceDestroy) {
		if (slot == null)
			return false;
		Item item = bankTabs[slot[0]][slot[1]];
		boolean destroyed = false;
		if (quantity >= item.getAmount()) {
			if (bankTabs[slot[0]].length == 1 && (forceDestroy || bankTabs.length != 1)) {
				destroyTab(player, slot[0]);
				if (refresh)
					refreshTabs(player);
				destroyed = true;
			} else {
				Item[] tab = new Item[bankTabs[slot[0]].length - 1];
				System.arraycopy(bankTabs[slot[0]], 0, tab, 0, slot[1]);
				System.arraycopy(bankTabs[slot[0]], slot[1] + 1, tab, slot[1], bankTabs[slot[0]].length - slot[1] - 1);
				bankTabs[slot[0]] = tab;
				if (refresh)
					player.getBank().refreshTab(slot[0]);
			}
		} else
			bankTabs[slot[0]][slot[1]] = new Item(item.getId(), item.getAmount() - quantity);
		if (refresh)
			refreshItems(player);
		return destroyed;
	}

	public Item getItem(int id) {
		for (int slot = 0; slot < bankTabs.length; slot++) {
			for (Item item : bankTabs[slot])
				if (item.getId() == id)
					return item;
		}
		return null;
	}

	public int[] getItemSlot(int id) {
		for (int tab = 0; tab < bankTabs.length; tab++) {
			for (int slot = 0; slot < bankTabs[tab].length; slot++)
				if (bankTabs[tab][slot].getId() == id)
					return new int[] { tab, slot };
		}
		return null;
	}

	public Item getItem(int[] slot) {
		if (slot == null)
			return null;
		return bankTabs[slot[0]][slot[1]];
	}

	public int getStartSlot(int tabId) {
		int slotId = 0;
		for (int tab = 1; tab < (tabId == 0 ? bankTabs.length : tabId); tab++)
			slotId += bankTabs[tab].length;

		return slotId;

	}

	public int[] getRealSlot(int slot) {
		for (int tab = 1; tab < bankTabs.length; tab++) {
			if (slot >= bankTabs[tab].length)
				slot -= bankTabs[tab].length;
			else
				return new int[] { tab, slot };
		}
		if (slot >= bankTabs[0].length)
			return null;
		return new int[] { 0, slot };
	}

	public void sendItems(Player mainPlayer) {
		if (players == null)
			players = new ArrayList<Player>();
		mainPlayer.getPackets().sendItems(95, getContainerCopy());
		for (Player player : players) {
			if (player != null && !player.hasFinished()) {
				player.getPackets().sendItems(95, getContainerCopy());
			}
		}
	}

	public void refreshItems(Player mainPlayer, int[] slots) {
		if (players == null)
			players = new ArrayList<Player>();
		mainPlayer.getPackets().sendUpdateItems(95, getContainerCopy(), slots);
		for (Player player : players) {
			if (player != null  && !player.hasFinished()) {
				player.getPackets().sendUpdateItems(95, getContainerCopy(), slots);
			}
		}
	}

	public Item[] getContainerCopy() {
		if (lastContainerCopy == null)
			lastContainerCopy = generateContainer();
		return lastContainerCopy;
	}

	public void refreshItems(Player player) {
		refreshItems(player, generateContainer(), getContainerCopy());
	}

	public void refreshItems(Player player, Item[] itemsAfter, Item[] itemsBefore) {
		if (itemsBefore.length != itemsAfter.length) {
			lastContainerCopy = itemsAfter;
			sendItems(player);
			return;
		}
		int[] changedSlots = new int[itemsAfter.length];
		int count = 0;
		for (int index = 0; index < itemsAfter.length; index++) {
			if (itemsBefore[index] != itemsAfter[index])
				changedSlots[count++] = index;
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		lastContainerCopy = itemsAfter;
		refreshItems(player, finalChangedSlots);
	}

	public int getBankSize() {
		int size = 0;
		for (int i = 0; i < bankTabs.length; i++)
			size += bankTabs[i].length;
		return size;
	}

	public Item[] generateContainer() {
		Item[] container = new Item[getBankSize()];
		int count = 0;
		for (int slot = 1; slot < bankTabs.length; slot++) {
			System.arraycopy(bankTabs[slot], 0, container, count, bankTabs[slot].length);
			count += bankTabs[slot].length;
		}
		System.arraycopy(bankTabs[0], 0, container, count, bankTabs[0].length);
		return container;
	}

	public void removePlayer(Player player) {
		if (players == null)
			players = new ArrayList<Player>();
		if (player != null && players.contains(player))
			players.remove(player);
	}
}
