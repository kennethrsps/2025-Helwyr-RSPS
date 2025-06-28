package com.rs.game.player.content;

import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.Settings;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.player.LendingManager;
import com.rs.game.player.Player;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;
import com.rs.utils.EconomyPrices;
import com.rs.utils.ItemExamines;
import com.rs.utils.Lend;
import com.rs.utils.LoggingSystem;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class Trade {

	public static enum CloseTradeStage {
		CANCEL, NO_SPACE, DONE
	}

	private Player player, target;
	private ItemsContainer<Item> items;
	private boolean tradeModified, accepted, noSpace;

	public Trade(Player player) {
		this.player = player; // player reference
		items = new ItemsContainer<Item>(28, false);
	}

	private Item lendedItem;
	private int lendTime;

	public Player getTarget() {
		return target;
	}

	public Item getLendedItem() {
		return lendedItem;
	}

	public int getLendedTime() {
		return lendTime;
	}

	public void setLendedItem(Item item) {
		this.lendedItem = item;
	}

	public void setLendedTime(int lendTime) {
		this.lendTime = lendTime;
	}

	public void handleCollectButton(Player player) {
		Lend hasLendedOut = LendingManager.getHasLendedItemsOut(player);
		if (player.getBank().getCollectableItem() != 0) {
			if (!player.getInventory().hasFreeSlots()) {
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
				return;
			}
			player.getPackets().sendIComponentText(109, 59, "<col=FF0000>Nothing!");
			for (int i = 540; i < 541; i++) {
				for (int j = -1; j < 0; j++) {
					Item[] default_ = new Item[] { new Item(-1, 1) };
					player.getPackets().sendItems(i, default_);
				}
			}
			player.getInventory().addItem(player.getBank().getCollectableItem(), 1);
			player.getBank().addCollectableItem(0);
			player.getPackets().sendGameMessage("Your item has been returned.");
		} else if (hasLendedOut != null) {
			player.getPackets().sendGameMessage("Your item is still on loan.");
		} else {
			player.getPackets().sendGameMessage("The item has already been collected from your Collection Box.");
		}
	}

	public void accept(boolean firstStage) {
		synchronized (this) {
			synchronized (target.getTrade()) {
				if (target.getTrade().accepted) {
					for (Item item : target.getTrade().items.getItems()) {
						if (item == null)
							continue;
						if (item.getId() != 995) {
							if (player.getInventory().getNumerOf(item.getId()) + item.getAmount() < 0) {
								player.setCloseInterfacesEvent(null);
								player.closeInterfaces();
								closeTrade(CloseTradeStage.NO_SPACE);
								break;
							}
							continue;
						}
						if (player.getInventory().getNumerOf(item.getId()) + item.getAmount() < 0) {
							if (item.getId() == 995) {
								if (player.getMoneyPouch().getTotal() + item.getAmount() < 0) {
									player.setCloseInterfacesEvent(null);
									player.closeInterfaces();
									closeTrade(CloseTradeStage.NO_SPACE);
									break;
								}
							}
							continue;
						}
					}
					if (firstStage) {
						if (nextStage())
							target.getTrade().nextStage();
					} else {
						player.setCloseInterfacesEvent(null);
						player.closeInterfaces();
						closeTrade(CloseTradeStage.DONE);
					}
					return;
				}
				accepted = true;
				refreshBothStageMessage(firstStage);
			}
		}
	}

	public void addItem(int slot, int amount) {
		synchronized (this) {
			synchronized (target.getTrade()) {
				Item item = player.getInventory().getItem(slot);
				if (item == null)
					return;
				if (!ItemConstants.isTradeable(item) && !player.isHeadStaff()) {
					player.getPackets().sendGameMessage("That item isn't tradeable.");
					return;
				}
				if (!ItemConstants.isTradeable(item) && player.isHeadStaff()) {
					player.getPackets().sendGameMessage(
							"<col=ff0000>[WARNING] The item you added to the trade is an untradeable item.");
				}
				Item[] itemsBefore = items.getItemsCopy();
				int maxAmount = player.getInventory().getItems().getNumberOf(item);
				if (amount < maxAmount)
					item = new Item(item.getId(), amount);
				else
					item = new Item(item.getId(), maxAmount);
				if (item.getAmount() + items.getNumberOf(item) < 0
						|| item.getAmount() + player.getTrade().items.getNumberOf(item) < 0) {
					return;
				}
				items.add(item);
				player.getInventory().deleteItem(slot, item);
				refreshItems(itemsBefore);
				cancelAccepted();
			}
		}
	}

	public void addPouch(int slot, int amount) {
		synchronized (this) {
			synchronized (target.getTrade()) {
				Item item = null;
				Item[] itemsBefore = items.getItemsCopy();
				if (amount >= player.getMoneyPouch().getTotal())
					amount = player.getMoneyPouch().getTotal();
				item = new Item(995, amount);
				if (item.getAmount() + items.getNumberOf(item) < 0
						|| item.getAmount() + player.getTrade().items.getNumberOf(item) < 0) {
					return;
				}
				if (player.getMoneyPouch().removeMoneyMisc(amount)) {
					items.add(item);
					refreshItems(itemsBefore);
					cancelAccepted();
				} else {
					closeTrade(CloseTradeStage.CANCEL);
				}
			}
		}
	}

	public void cancelAccepted() {
		boolean canceled = false;
		if (accepted) {
			accepted = false;
			canceled = true;
		}
		if (target.getTrade().accepted) {
			target.getTrade().accepted = false;
			canceled = true;
		}
		if (canceled)
			refreshBothStageMessage(canceled);
	}

	public void closeTrade(CloseTradeStage stage) {
		synchronized (this) {
			if (target == null)
				return;
			synchronized (target.getTrade()) {
				Player oldTarget = target;
				target = null;
				Item lendItem = getLendedItem();
				int time = getLendedTime();
				tradeModified = false;
				accepted = false;
				if (CloseTradeStage.DONE != stage) {
					for (Item item : player.getTrade().items.getItems()) {
						if (item == null)
							continue;
						if (item.getId() == 995) {
							player.getMoneyPouch().addMoneyMisc(item.getAmount());
							continue;
						}
						player.getInventory().addItem(item);
					}
					if (lendItem != null) {
						player.getInventory().addItem(new Item(lendedItem.getId()));
					}
					player.getInventory().init();
					items.clear();
				} else {
					CopyOnWriteArrayList<Item> containedItems = new CopyOnWriteArrayList<Item>();
					for (Item item : oldTarget.getTrade().items.getItems()) {
						if (item == null)
							continue;
						containedItems.add(item);
					}
					LoggingSystem.logTrade(player, oldTarget, containedItems);
					player.getPackets().sendGameMessage("Accepted trade.");
					for (Item item : oldTarget.getTrade().items.getItems()) {
						if (item == null)
							continue;
						if (player.getInventory().getNumerOf(item.getId()) + item.getAmount() < 0) {
							if (item.getId() == 995)
								player.getMoneyPouch().addMoneyMisc(item.getAmount());
							continue;
						}
						player.getInventory().addItem(item);
					}
					if (lendItem != null) {
						Lend lend = new Lend(player.getUsername(), oldTarget.getUsername(), lendItem,
								getLendedTime() < 1 ? 1337 : Utils.currentTimeMillis() + (time * 60 * 60 * 1000));
						LendingManager.lend(lend);
						oldTarget.getInventory().addItem(lendItem.getDefinitions().getLendId(), 1);
					}
					player.getTrade().setLendedItem(null);
					player.getInventory().init();
					SerializableFilesManager.savePlayer(player);
					oldTarget.getTrade().items.clear();
				}
				if (oldTarget.getTrade().isTrading() || oldTarget.getTrade().noSpace) {
					oldTarget.setCloseInterfacesEvent(null);
					oldTarget.closeInterfaces();
					oldTarget.getTrade().closeTrade(stage);
					if (CloseTradeStage.CANCEL == stage) {
						oldTarget.getPackets().sendGameMessage("<col=ff0000>Other player declined trade!");
						oldTarget.getTrade().setLendedItem(null);
						player.getTrade().setLendedItem(null);
					} else if (CloseTradeStage.NO_SPACE == stage) {
						player.sendMessage("Inventory full. To make room, sell, drop or bank something.");
						oldTarget.sendMessage(
								"Other player doesn't have enough space in their inventory for this trade.");
					}
				}
			}
		}
	}

	public void changeHours() {
		player.getTemporaryAttributtes().put("loanHrs", true);
		player.getPackets().sendRunScript(109, "How long do you wish to lend this item for?");
		player.getTemporaryAttributtes().put("ChangeLoanHours", Boolean.TRUE);
	}

	public void updateHours(int hours) {
		if (hours > 24 || hours < 1) {
			hours = hours > 24 ? 24 : 0;
		}
		String h = (hours == 1) ? " Hour" : " Hours";
		if (hours != 0) {
			player.getPackets().sendIComponentText(335, 62, hours + h);
			target.getPackets().sendIComponentText(335, 58, hours + h);
		} else {
			player.getPackets().sendIComponentText(335, 62, "<col=ff0000>Until</col><br><col=ff0000>logout");
			target.getPackets().sendIComponentText(335, 58, "<col=ff0000>Until</col><br><col=ff0000>logout");
		}
		player.getPackets().sendGameMessage("Lent time set to " + hours + " hours.");
		setLendedTime(hours);
	}

	public void lendItem(int slot) {
		synchronized (this) {
			if (!isTrading())
				return;
			synchronized (target.getTrade()) {
				Item item = player.getInventory().getItem(slot);
				if (item == null)
					return;
				if (item.getDefinitions().getLendId() == -1) {
					player.getPackets().sendGameMessage("You can't lend that.");
					return;
				}
				if (player.getControlerManager().getControler() != null) {
					player.getPackets().sendGameMessage("You're too busy to lend an item.");
					return;
				}
				if (target.getControlerManager().getControler() != null) {
					player.getPackets().sendGameMessage(target.getDisplayName() + " are too busy to lend an item.");
					return;
				}
				if (LendingManager.hasLendedItem(target)) {
					player.getPackets().sendGameMessage("Your target already has a lended item.");
					return;
				}
				if (item.getDefinitions().isLended()) {
					player.sendMessage("You can't lend an already lent item to you.");
					return;
				}
				if (Settings.LENDING_DISABLED) {
					player.sendMessage("Item lending is disabled.");
					return;
				}
				// SET INTERFACE
				player.getPackets().sendIComponentSettings(335, 62, -1, -1, 6);
				player.getPackets().sendItems(541, false, new Item[] { new Item(item.getId(), -1) });
				target.getPackets().sendItems(541, true, new Item[] { new Item(item.getId(), -1) });
				// END SET INTERFACE
				setLendedItem(item);
				setLendedTime(0); // UNTILL LOGOUT
				Item[] itemsBefore = items.getItemsCopy();
				player.getInventory().deleteItem(slot, item);
				refreshItems(itemsBefore);
				cancelAccepted();
			}
		}
	}

	public String getAcceptMessage(boolean firstStage) {
		if (accepted)
			return "Waiting for other player...";
		if (target.getTrade().accepted)
			return "Other player has accepted.";
		return firstStage ? "" : "Are you sure you want to make this trade?";
	}

	public int getTradeWealth() {
		int wealth = 0;
		for (Item item : items.getItems()) {
			if (item == null)
				continue;
			if (wealth + GrandExchange.getPrice(item.getId()) < 0) {
				wealth = Integer.MAX_VALUE + 1;
				continue;
			}
			wealth += GrandExchange.getPrice(item.getId());
		}
		return wealth;
	}

	public void handleButtons(Player player, int interfaceId, int packetId, int componentId, int slotId) {

		if (interfaceId == 334) {
			if (componentId == 22)
				player.closeInterfaces();
			else if (componentId == 21)
				player.getTrade().accept(false);
		} else if (interfaceId == 335) {
			if (componentId == 62)
				player.getTrade().changeHours();
			else if (componentId == 18)
				player.getTrade().accept(true);
			else if (componentId == 20)
				player.closeInterfaces();
			else if (componentId == 53) {
				player.getTemporaryAttributtes().put("trade_X_money", 995);
				player.getTemporaryAttributtes().put("trade_money", Boolean.TRUE);
				player.getPackets().sendRunScript(108,
						new Object[] { "                          Your money pouch contains "
								+ Utils.getFormattedNumber(player.getMoneyPouch().getTotal()) + " coins."
								+ "                           How many would you like to add?" });
			} else if (componentId == 32) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getTrade().removeItem(slotId, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getTrade().removeItem(slotId, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getTrade().removeItem(slotId, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getTrade().removeItem(slotId, Integer.MAX_VALUE);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("trade_item_X_Slot", slotId);
					player.getTemporaryAttributtes().put("trade_isRemove", Boolean.TRUE);
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					player.getTrade().sendValue(slotId, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getTrade().sendExamine(slotId, false);
			} else if (componentId == 35) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getTrade().sendValue(slotId, true);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getTrade().sendExamine(slotId, true);
			}
		} else if (interfaceId == 336) {
			if (componentId == 0) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getTrade().addItem(slotId, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getTrade().addItem(slotId, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getTrade().addItem(slotId, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getTrade().addItem(slotId, Integer.MAX_VALUE);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("trade_item_X_Slot", slotId);
					player.getTemporaryAttributtes().remove("trade_isRemove");
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				} else if (packetId == 37) {
					player.getTrade().lendItem(slotId);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					player.getTrade().sendValue(slotId);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getInventory().sendExamine(slotId);
			}

		}
	}

	public boolean isTrading() {
		return (target != null && player != null);
	}

	public boolean nextStage() {
		if (target == null || player == null) {
			player.setCloseInterfacesEvent(null);
			player.closeInterfaces();
			target.setCloseInterfacesEvent(null);
			target.closeInterfaces();
			closeTrade(CloseTradeStage.CANCEL);
			return false;
		}
		if (player.getInventory().getItems().getUsedSlots() + target.getTrade().items.getUsedSlots() > 28
				|| player.getInventory().getItems().goesOverAmount(items)) {
			player.setCloseInterfacesEvent(null);
			player.closeInterfaces();
			closeTrade(CloseTradeStage.NO_SPACE);
			return false;
		}
		accepted = false;
		player.getInterfaceManager().sendInterface(334);
		if (target.getTrade().getLendedItem() != null) {
			player.getPackets().sendIComponentText(334, 58,
					"<col=ffffff>" + target.getTrade().getLendedItem().getDefinitions().getName() + ", "
							+ target.getTrade().getLendedTime() + " hours");
		}
		if (getLendedItem() != null) {
			target.getPackets().sendIComponentText(334, 58,
					"<col=ffffff>" + getLendedItem().getDefinitions().getName() + ", " + getLendedTime() + " hours");
		}
		player.getInterfaceManager().closeInventoryInterface();
		player.getPackets().sendHideIComponent(334, 55, !(tradeModified || target.getTrade().tradeModified));
		refreshBothStageMessage(false);
		return true;
	}

	/*
	 * called to both players
	 */
	public void openTrade(Player target) {
		synchronized (this) {
			synchronized (target.getTrade()) {
				this.target = target;
				if (player.getSession().getIP() == target.getSession().getIP()
						|| target.getSession().getLocalAddress() == player.getSession().getLocalAddress()) {
					player.sendMessage("You can't trade the same IP/MAC address!");
					return;
				}
				/**
				 * if (player.checkTotalLevel(300) < 300) {
				 * closeTrade(CloseTradeStage.CANCEL); player.sendMessage("You
				 * can not trade until your total level is 300."); return; }
				 */
				player.getPackets().sendItems(541, false, new Item[] { new Item(-1, -1) });
				target.getPackets().sendItems(541, true, new Item[] { new Item(-1, -1) });
				player.getPackets().sendIComponentText(335, 17, "Trading With: " + target.getDisplayName());
				player.getPackets().sendGlobalString(203, target.getDisplayName());
				sendInterItems();
				sendOptions();
				sendTradeModified();
				refreshFreeInventorySlots();
				refreshTradeWealth();
				refreshStageMessage(true);
				player.getInterfaceManager().sendInterface(335);
				player.getInterfaceManager().sendInventoryInterface(336);
				player.setCloseInterfacesEvent(new Runnable() {
					@Override
					public void run() {
						closeTrade(CloseTradeStage.CANCEL);
					}
				});
			}
		}
	}

	public void refresh(int... slots) {
		player.getPackets().sendUpdateItems(90, items, slots);
		target.getPackets().sendUpdateItems(90, true, items.getItems(), slots);
		if (target.getTrade().getLendedItem() != null) {
			player.getPackets().sendItemOnIComponent(335, 57, target.getTrade().getLendedItem().getId(), 1);
			player.getPackets().sendIComponentText(335, 58,
					target.getTrade().getLendedTime() + " hour" + (getLendedTime() > 1 ? "s" : ""));
			target.getPackets().sendItemOnIComponent(335, 61, target.getTrade().getLendedItem().getId(), 1);
			target.getPackets().sendIComponentText(335, 62,
					target.getTrade().getLendedTime() + " hour" + (getLendedTime() > 1 ? "s" : ""));
		}
		if (getLendedItem() != null) {
			target.getPackets().sendItemOnIComponent(335, 57, getLendedItem().getId(), 1);
			target.getPackets().sendIComponentText(335, 58,
					getLendedTime() + " hour" + (getLendedTime() > 1 ? "s" : ""));
			player.getPackets().sendItemOnIComponent(335, 61, getLendedItem().getId(), 1);
			player.getPackets().sendIComponentText(335, 62,
					getLendedTime() + " hour" + (getLendedTime() > 1 ? "s" : ""));
		}
	}

	public void refreshBothStageMessage(boolean firstStage) {
		refreshStageMessage(firstStage);
		target.getTrade().refreshStageMessage(firstStage);
	}

	public void refreshFreeInventorySlots() {
		int freeSlots = player.getInventory().getFreeSlots();
		target.getPackets().sendIComponentText(335, 23,
				"has " + (freeSlots == 0 ? "no" : freeSlots) + " free" + "<br>inventory slots");
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			Item item = items.getItems()[index];
			if (itemsBefore[index] != item) {
				if (itemsBefore[index] != null && (item == null || item.getId() != itemsBefore[index].getId()
						|| item.getAmount() < itemsBefore[index].getAmount()))
					sendFlash(index);
				changedSlots[count++] = index;
			}
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
		refreshFreeInventorySlots();
		refreshTradeWealth();
	}

	public void refreshStageMessage(boolean firstStage) {
		player.getPackets().sendIComponentText(firstStage ? 335 : 334, firstStage ? 39 : 34,
				getAcceptMessage(firstStage));
	}

	public void refreshTradeWealth() {
		int wealth = getTradeWealth();
		player.getPackets().sendGlobalConfig(729, wealth);
		target.getPackets().sendGlobalConfig(697, wealth);
	}

	public void removeItem(final int slot, int amount) {
		synchronized (this) {
			synchronized (target.getTrade()) {
				Item item = items.get(slot);
				if (item == null)
					return;
				Item[] itemsBefore = items.getItemsCopy();
				int maxAmount = items.getNumberOf(item);
				if (amount < maxAmount)
					item = new Item(item.getId(), amount);
				else
					item = new Item(item.getId(), maxAmount);
				items.remove(slot, item);
				player.getInventory().addItem(item);
				refreshItems(itemsBefore);
				cancelAccepted();
				setTradeModified(true);
			}
		}
	}

	public void sendExamine(int slot, boolean traders) {
		Item item = traders ? target.getTrade().items.get(slot) : items.get(slot);
		if (item == null)
			return;
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public void sendFlash(int slot) {
		player.getPackets().sendInterFlashScript(335, 33, 4, 7, slot);
		target.getPackets().sendInterFlashScript(335, 36, 4, 7, slot);
	}

	public void sendInterItems() {
		player.getPackets().sendItems(90, items);
		target.getPackets().sendItems(90, true, items);
	}

	public void sendOptions() {
		player.getPackets().sendInterSetItemsOptionsScript(336, 0, 93, 4, 7, "Offer", "Offer-5", "Offer-10",
				"Offer-All", "Offer-X", "Value<col=FF9040>", "Lend");
		player.getPackets().sendIComponentSettings(336, 0, 0, 27, 1278);
		player.getPackets().sendInterSetItemsOptionsScript(335, 32, 90, 4, 7, "Remove", "Remove-5", "Remove-10",
				"Remove-All", "Remove-X", "Value");
		player.getPackets().sendIComponentSettings(335, 32, 0, 27, 1150);
		player.getPackets().sendInterSetItemsOptionsScript(335, 35, 90, true, 4, 7, "Value");
		player.getPackets().sendIComponentSettings(335, 35, 0, 27, 1026);

	}

	public void sendTradeModified() {
		player.getPackets().sendConfig(1042, tradeModified ? 1 : 0);
		target.getPackets().sendConfig(1043, tradeModified ? 1 : 0);
	}

	public void sendValue(int slot) {
		Item item = player.getInventory().getItem(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item)) {
			player.getPackets().sendGameMessage("That item isn't tradeable.");
			return;
		}
		int price = EconomyPrices.getPrice(item.getId());
		player.getPackets().sendGameMessage(item.getDefinitions().getName() + ": market price is " + price + " coins.");
	}

	public void sendValue(int slot, boolean traders) {
		Item item = traders ? target.getTrade().items.get(slot) : items.get(slot);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item)) {
			player.getPackets().sendGameMessage("That item isn't tradeable.");
			return;
		}
		int price = GrandExchange.getPrice(item.getId());
		player.getPackets().sendGameMessage(item.getDefinitions().getName() + ": market price is " + price + " coins.");
	}

	public void setTradeModified(boolean modified) {
		if (modified == tradeModified)
			return;
		tradeModified = modified;
		sendTradeModified();
	}
}