package com.rs.game.player.content.grandExchange;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rs.game.item.Item;
import com.rs.game.player.CombatDefinitions;
import com.rs.game.player.LendingManager;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.Shop;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.content.grandExchange.Offer;
import com.rs.game.player.content.grandExchange.OfferHistory;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.utils.ItemExamines;
import com.rs.utils.Lend;
import com.rs.utils.Utils;

/**
 * Handles the Grand Exchange.
 * @author Zeus
 */
public class GrandExchangeManager implements Serializable {

	private static final long serialVersionUID = -866326987352331696L;

	private transient Player player;
	
	private long[] offerUIds;
	private OfferHistory[] history;

	public GrandExchangeManager() {
		offerUIds = new long[6];
		history = new OfferHistory[5];
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void init() {
		GrandExchange.linkOffers(player);
	}

	public void stop() {
		GrandExchange.unlinkOffers(player);
	}

	public long[] getOfferUIds() {
		return offerUIds;
	}

	public boolean isSlotFree(int slot) {
		return offerUIds[slot] == 0;
	}

	public void addOfferHistory(OfferHistory o) {
		OfferHistory[] dest = new OfferHistory[history.length];
		dest[0] = o;
		System.arraycopy(history, 0, dest, 1, history.length - 1);
		history = dest;
	}

	public void openHistory() {
		if (!canUse()) {
			player.sendMessage("Ironmen cannot use the Grand Exchange.");
			player.getPackets().sendSound(4039, 1, 1);
			return;
		}
		player.getInterfaceManager().sendInterface(643);
		for (int i = 0; i < history.length; i++) {
			OfferHistory o = history[i];
			player.getPackets().sendIComponentText(643, 25 + i,
					o == null ? "" : o.isBought() ? "You bought" : "You sold");
			player.getPackets().sendIComponentText(
					643,
					35 + i,
					o == null ? "" : ItemDefinitions.getItemDefinitions(
							o.getId()).getName());
			player.getPackets().sendIComponentText(643, 30 + i,
					o == null ? "" : Utils.getFormattedNumber(o.getQuantity()));
			player.getPackets().sendIComponentText(643, 40 + i,
					o == null ? "" : Utils.getFormattedNumber(o.getPrice()));
		}
	}

	public void openGrandExchange() {
		if (!canUse()) {
			player.sendMessage("Ironmen cannot use the Grand Exchange.");
			player.getPackets().sendSound(4039, 1, 1);
			return;
		}
        player.getInterfaceManager().sendInterface(105);
        player.getInterfaceManager().closeInventoryInterface();
		player.getPackets().sendUnlockIComponentOptionSlots(105, 206, -1, 0, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(105, 208, -1, 0, 0, 1);
		
		cancelOffer();
        player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				if (getType() == 0)
					player.getPackets().sendRunScript(571);
				player.getInterfaceManager().closeInterface(752, 7);
			}
		});
	}

	public void openCollectionBox() {
		if (!canUse()) {
			player.sendMessage("Ironmen cannot use the Grand Exchange.");
			player.getPackets().sendSound(4039, 1, 1);
			return;
		}
		Lend hasLendedOut = LendingManager.getHasLendedItemsOut(player);
		Item item = null;
		if (player.getBank().getCollectableItem() != 0) {
			for (int i = 540; i < 541; i++) {
				for (int j = player.getBank().getCollectableItem(); j < player
						.getBank().getCollectableItem() + 1; j++) {
					Item[] default_ = new Item[] { new Item(player.getBank()
							.getCollectableItem(), 1) };
					player.getPackets().sendItems(i, default_);
				}
			}
			if (LendingManager.getHasLendedItemsOut(player) == null) {
				player.getPackets().sendIComponentText(109, 59, "<col=00ff00>Available");
			}
		} else if (hasLendedOut != null) {
			for (Lend lend : LendingManager.list) {
				if (lend.getLender().equals(player.getUsername()))
					item = lend.getItem();
			}
			int id2 = item.getId();
			System.out.println(id2);
			for (int i = 540; i < 541; i++) {
				for (int j = id2; j < id2 + 1; j++) {
					Item[] default_ = new Item[] { new Item(id2, 1) };
					player.getPackets().sendItems(i, default_);
				}
			}
			player.getPackets().sendIComponentText(109, 59, "<col=FF0000>Still on loan");
		} else {
			player.getPackets().sendHideIComponent(109, 58, true);
			for (int i = 540; i < 541; i++) {
				for (int j = -1; j < 0; j++) {
					Item[] default_ = new Item[] { new Item(-1, 1) };
					player.getPackets().sendItems(i, default_);
				}
			}
			player.getPackets().sendIComponentText(109, 59, "<col=FF0000>Nothing!");
		}
		player.getInterfaceManager().sendInterface(109);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 19, 0, 2, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 23, 0, 2, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 27, 0, 2, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 32, 0, 2, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 37, 0, 2, 0, 1);
		player.getPackets().sendUnlockIComponentOptionSlots(109, 42, 0, 2, 0, 1);
	}

	public void setSlot(int slot) {
		player.getVarBitManager().sendVar(1112, slot);
	}

	public void setMarketPrice(int price) {
		player.getVarBitManager().sendVar(1114, price);
		player.getPackets().sendSound(4041, 1, 1);
	}

	public void setPricePerItem(int price) {
		player.getVarBitManager().sendVar(1111, price);
		player.getPackets().sendSound(4041, 1, 1);
	}

	public int getPricePerItem() {
		return player.getVarBitManager().getValue(1111);
	}

	public int getCurrentSlot() {
		return player.getVarBitManager().getValue(1112);
	}

	public void setItemId(int id) {
		player.getPackets().sendSound(4041, 1, 1);
		player.getVarBitManager().sendVar(1109, id);
	}

	public int getItemId() {
		return player.getVarBitManager().getValue(1109);
	}

	public void setAmount(int amount) {
		player.getPackets().sendSound(4041, 1, 1);
		player.getVarBitManager().sendVar(1110, amount);
	}

	public int getAmount() {
		return player.getVarBitManager().getValue(1110);
	}

	public void setType(int amount) {
		player.getVarBitManager().sendVar(1113, amount);
	}

	public int getType() {
		return player.getVarBitManager().getValue(1113);
	}
	
	public void viewSellOrders() {
		int id = this.getItemId();
		if(id == -1) {
			player.getPackets().sendHideIComponent(105, 233, true);
			return;
		}
		List<Offer> offers = new ArrayList<Offer>(315);
		y:for(Offer offer : GrandExchange.getOffers().values()) {
			//System.err.println("reading: "+offer.getId());
			if(offers.size() > 315) {
				break y;
			}
			if(offer.getId() == id) {
				offers.add(offer);
			}
		}
		if(offers.isEmpty()) {
			player.errorMessage("Nobody is selling "+ItemDefinitions.getItemDefinitions(id).getName().toLowerCase()+" currently.");
			return;
		}
		for(int i=10;i<=309;i++) {
			player.getPackets().sendIComponentText(275, i, "");
		}
		player.getInterfaceManager().sendInterface(275);
		player.getPackets().sendIComponentText(275, 1, "Sell offers: "+ItemDefinitions.getItemDefinitions(id).getName().toLowerCase());//test
		int index = 10;
		for(Offer offer : offers) {
			player.getPackets().sendIComponentText(275, index, 
					ItemDefinitions.getItemDefinitions(offer.getId()).getName()+" is being sold x"
			+offer.getAmount()+" @"
							+Utils.getFormattedNumber(offer.getPrice())+" each");
			index++;
		}
		//System.err.println("item: "+id);
		
	}

	public void handleButtons(int interfaceId, int componentId, int slotId, int packetId) {
		
		if (interfaceId == 105) {
			switch (componentId) {
			case 223://list
				viewSellOrders();
				break;
			case 19:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					viewOffer(0);
				else
					abortOffer(0);
				break;
			case 35:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					viewOffer(1);
				else
					abortOffer(1);
				break;
			case 51:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					viewOffer(2);
				else
					abortOffer(2);
				break;
			case 70:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					viewOffer(3);
				else
					abortOffer(3);
				break;
			case 89:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					viewOffer(4);
				else
					abortOffer(4);
				break;
			case 108:
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					viewOffer(5);
				else
					abortOffer(5);
				break;
			case 200:
				abortCurrentOffer();
				break;
			case 31:
				makeOffer(0, false);
				break;
			case 32:
				makeOffer(0, true);
				break;
			case 47:
				makeOffer(1, false);
				break;
			case 48:
				makeOffer(1, true);
				break;
			case 63:
				makeOffer(2, false);
				break;
			case 64:
				makeOffer(2, true);
				break;
			case 82:
				makeOffer(3, false);
				break;
			case 83:
				makeOffer(3, true);
				break;
			case 101:
				makeOffer(4, false);
				break;
			case 102:
				makeOffer(4, true);
				break;
			case 120:
				makeOffer(5, false);
				break;
			case 121:
				makeOffer(5, true);
				break;
			case 128:
				cancelOffer();
				break;
			case 155:
				modifyAmount(getAmount() - 1);
				break;
			case 157:
				modifyAmount(getAmount() + 1);
				break;
			case 160:
				modifyAmount(getAmount() + 1);
				break;
			case 162:
				modifyAmount(getAmount() + 10);
				break;
			case 164:
				modifyAmount(getAmount() + 100);
				break;
			case 166:
				modifyAmount(getType() == 0 ? getAmount() + 1000
						: getItemAmount(new Item(getItemId())));
				break;
			case 168:
				editAmount();
				break;
			case 169:
				modifyPricePerItem(getPricePerItem() - 1);
				break;
			case 171:
				modifyPricePerItem(getPricePerItem() + 1);
				break;
			case 175:
				modifyPricePerItem(GrandExchange.getPrice(getItemId()));
				break;
			case 177:
				editPrice();
				break;
			case 179:
				modifyPricePerItem((int) (Math.ceil(getPricePerItem() * 1.05)));
				break;
			case 181:
				modifyPricePerItem((int) (getPricePerItem() * 0.95));
				break;
			case 186:
				confirmOffer();
				break;
			case 190:
				chooseItem();
				break;
			case 206:
				collectItems(
						getCurrentSlot(),
						0,
						packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 0
								: 1);
				break;
			case 208:
				collectItems(
						getCurrentSlot(),
						1,
						packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 0
								: 1);
				break;
			}
		} else if (interfaceId == 107 && componentId == 18)
			offer(slotId);
		else if (interfaceId == 449 && componentId == 1)
			player.getInterfaceManager().closeInventoryInterface();
		else if (interfaceId == 109) {
			switch (componentId) {
			case 19:
				collectItems(
						0,
						slotId == 0 ? 0 : 1,
						packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 0
								: 1);
				break;
			case 23:
				collectItems(
						1,
						slotId == 0 ? 0 : 1,
						packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 0
								: 1);
				break;
			case 27:
				collectItems(
						2,
						slotId == 0 ? 0 : 1,
						packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 0
								: 1);
				break;
			case 32:
				collectItems(
						3,
						slotId == 0 ? 0 : 1,
						packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 0
								: 1);
				break;
			case 37:
				collectItems(
						4,
						slotId == 0 ? 0 : 1,
						packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 0
								: 1);
				break;
			case 42:
				collectItems(
						5,
						slotId == 0 ? 0 : 1,
						packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 0 : 1);
				break;
			}
		}
	}
	
	public void cancelOffer() {
		setItemId(-1);
		setAmount(0);
		setPricePerItem(1);
		setMarketPrice(0);
		setSlot(-1);
		if (getType() == 0)
			player.getPackets().sendRunScript(571);
		player.getInterfaceManager().closeInventoryInterface();
		player.getInterfaceManager().closeInterface(752, 7);
		setType(-1);
	}

	public void editAmount() {
		if (getType() == -1)
			return;
		player.getTemporaryAttributtes().put("edit_quantity", Boolean.TRUE);
		player.getPackets().sendInputIntegerScript(true, "Enter the quantity:");
	}

	public void editPrice() {
		if (getType() == -1)
			return;
		if (getItemId() == -1) {
			player.sendMessage("You must choose an item first.");
			player.getPackets().sendSound(4039, 1, 1);
			return;
		}
		player.getTemporaryAttributtes().put("edit_price", Boolean.TRUE);
		player.getPackets().sendInputIntegerScript(true, "Enter the price:");
	}

	public void modifyPricePerItem(int value) {
		if (getType() == -1)
			return;
		if (getItemId() == -1) {
			player.sendMessage("You must choose an item first.");
			player.getPackets().sendSound(4039, 1, 1);
			return;
		}
		if (value < 1)
			value = 1;
		setPricePerItem(value);
	}

	public void modifyAmount(int value) {
		if (getType() == -1)
			return;
		if (value < 0)
			value = 0;
		setAmount(value);
	}

	public void abortCurrentOffer() {
		int slot = getCurrentSlot();
		if (slot == -1)
			return;
		abortOffer(slot);
	}

	public void abortOffer(int slot) {
		if (isSlotFree(slot))
			return;
		GrandExchange.abortOffer(player, slot);
		player.sendMessage("Please be aware that your offer may have already been completed.");
	}

	public void collectItems(int slot, int invSlot, int option) {
		if (slot == -1 || isSlotFree(slot))
			return;
		GrandExchange.collectItems(player, slot, invSlot, option);
		player.getPackets().sendSound(4040, 1, 1);
	}

	public void viewOffer(int slot) {
		if (isSlotFree(slot) || getCurrentSlot() != -1)
			return;
		Offer offer = GrandExchange.getOffer(player, slot);
		if (offer == null)
			return;
		setSlot(slot);
		setExtraDetails(offer.getId());
		player.getPackets().sendSound(4041, 1, 1);
	}

	/*
	 * includes noted
	 */
	public int getItemAmount(Item item) {
		int notedId = item.getDefinitions().certId;
		return player.getInventory().getNumberOf(item.getId())
				+ player.getInventory().getNumberOf(notedId);
	}

	public void chooseItem(int id) {
		if (!player.getInterfaceManager().containsInterface(105))
			return;
		setItem(new Item(id), false);
	}

	public void sendInfo(Item item) {
		player.getInterfaceManager().sendInventoryInterface(449);
		player.getPackets().sendGlobalConfig(741, item.getId());
		player.getPackets().sendGlobalString(25, ItemExamines.getGEExamine(item)); 
		player.getPackets().sendGlobalString(34, ""); // quest id for some items
		int[] bonuses = new int[18];
		ItemDefinitions defs = item.getDefinitions();
		bonuses[CombatDefinitions.STAB_ATTACK] += defs.getStabAttack();
		bonuses[CombatDefinitions.SLASH_ATTACK] += defs.getSlashAttack();
		bonuses[CombatDefinitions.CRUSH_ATTACK] += defs.getCrushAttack();
		bonuses[CombatDefinitions.MAGIC_ATTACK] += defs.getMagicAttack();
		bonuses[CombatDefinitions.RANGE_ATTACK] += defs.getRangeAttack();
		bonuses[CombatDefinitions.STAB_DEF] += defs.getStabDef();
		bonuses[CombatDefinitions.SLASH_DEF] += defs.getSlashDef();
		bonuses[CombatDefinitions.CRUSH_DEF] += defs.getCrushDef();
		bonuses[CombatDefinitions.MAGIC_DEF] += defs.getMagicDef();
		bonuses[CombatDefinitions.RANGE_DEF] += defs.getRangeDef();
		bonuses[CombatDefinitions.SUMMONING_DEF] += defs.getSummoningDef();
		bonuses[CombatDefinitions.ABSORVE_MELEE_BONUS] += defs.getAbsorveMeleeBonus();
		bonuses[CombatDefinitions.ABSORVE_MAGE_BONUS] += defs.getAbsorveMageBonus();
		bonuses[CombatDefinitions.ABSORVE_RANGE_BONUS] += defs.getAbsorveRangeBonus();
		bonuses[CombatDefinitions.STRENGTH_BONUS] += defs.getStrengthBonus();
		bonuses[CombatDefinitions.RANGED_STR_BONUS] += defs.getRangedStrBonus();
		bonuses[CombatDefinitions.PRAYER_BONUS] += defs.getPrayerBonus();
		bonuses[CombatDefinitions.MAGIC_DAMAGE] += defs.getMagicDamage();
		boolean hasBonus = false;
		for (int bonus : bonuses)
			if (bonus != 0) {
				hasBonus = true;
				break;
			}
		if (hasBonus) {
			HashMap<Integer, Integer> requiriments = item.getDefinitions()
					.getWearingSkillRequiriments();
			if (requiriments != null && !requiriments.isEmpty()) {
				String reqsText = "";
				for (int skillId : requiriments.keySet()) {
					if (skillId > 24 || skillId < 0)
						continue;
					int level = requiriments.get(skillId);
					if (level < 0 || level > 120)
						continue;
					boolean hasReq = player.getSkills().getLevelForXp(skillId) >= level;
					reqsText += "<br>"
							+ (hasReq ? "<col=00ff00>" : "<col=ff0000>")
							+ "Level " + level + " "
							+ Skills.SKILL_NAME[skillId];
				}
				player.getPackets().sendGlobalString(26,
						"<br>Worn on yourself, requiring: " + reqsText+".");
			} else
				player.getPackets()
						.sendGlobalString(26, "<br>Worn on yourself.");
			player.getPackets().sendGlobalString(
					35,
					"<br>Attack<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.STAB_ATTACK]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.SLASH_ATTACK]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.CRUSH_ATTACK]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.MAGIC_ATTACK]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.RANGE_ATTACK]
							+ "<br><col=ffff00>---" + "<br>Strength"
							+ "<br>Ranged Strength" + "<br>Magic Damage"
							+ "<br>Absorve Melee" + "<br>Absorve Magic"
							+ "<br>Absorve Ranged" + "<br>Prayer Bonus");
			player.getPackets()
					.sendGlobalString(36,
							"<br><br>Stab<br>Slash<br>Crush<br>Magic<br>Ranged<br>Summoning");
			player.getPackets().sendGlobalString(
					52,
					"<<br>Defence<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.STAB_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.SLASH_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.CRUSH_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.MAGIC_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.RANGE_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.SUMMONING_DEF]
							+ "<br><col=ffff00>+"
							+ bonuses[CombatDefinitions.STRENGTH_BONUS]
							+ "<br><col=ffff00>"
							+ bonuses[CombatDefinitions.RANGED_STR_BONUS]
							+ "<br><col=ffff00>"
							+ bonuses[CombatDefinitions.MAGIC_DAMAGE]
							+ "%<br><col=ffff00>"
							+ bonuses[CombatDefinitions.ABSORVE_MELEE_BONUS]
							+ "%<br><col=ffff00>"
							+ bonuses[CombatDefinitions.ABSORVE_MAGE_BONUS]
							+ "%<br><col=ffff00>"
							+ bonuses[CombatDefinitions.ABSORVE_RANGE_BONUS]
							+ "%<br><col=ffff00>"
							+ bonuses[CombatDefinitions.PRAYER_BONUS]);
		} else {
			player.getPackets().sendGlobalString(26, "");
			player.getPackets().sendGlobalString(35, "");
			player.getPackets().sendGlobalString(36, "");
			player.getPackets().sendGlobalString(52, "");
		}
	}

	public void chooseItem() {
		if (getType() != 0)
			return;
		setItemId(-1);
		setAmount(0);
		setPricePerItem(1);
		setMarketPrice(0);
		player.getInterfaceManager().closeInventoryInterface();
		player.getPackets().sendConfig(1109, -1);
		player.getPackets().sendConfig(1112, 0);
		player.getPackets().sendConfig(1113, 0);
		player.getPackets().sendConfig1(1241, 16750848);
		player.getPackets().sendConfig1(1242, 15439903);
		player.getPackets().sendConfig1(741, -1);
		player.getPackets().sendConfig1(743, -1);
		player.getPackets().sendConfig1(744, 0);
		player.getPackets().sendInterface(true, 752, 7, 389);
		player.getPackets().sendRunScript(570,
				new Object[] { "Grand Exchange Item Search" });
	}

	public void offer(int slot) {
		Item item = player.getInventory().getItem(slot);
		if (item == null)
			return;
		setItem(item, true);
	}

	public void setExtraDetails(int id) {
		setItemId(id);
		setMarketPrice(GrandExchange.getPrice(id));
		int totalsellamount = GrandExchange.getTotalSellQuantity(id);
		int totalbuyamount = GrandExchange.getTotalBuyQuantity(id);
		int sellprice = GrandExchange.getCheapestSellPrice(id);
		int sellamount = GrandExchange.getSellQuantity(id);
		int buyprice = GrandExchange.getBestBuyPrice(id);
		int buyamount = GrandExchange.getBuyQuantity(id);
		player.getPackets().sendIComponentText(105, 143,
				ItemExamines.getGEExamine(new Item(id)));
		if (getType() == 1) { //Sell
			if (totalbuyamount > 0) {
				player.getPackets().sendIComponentText(105, 143, ""
					+ ItemExamines.getGEExamine(new Item(id))
					+ "<br><br>Quantity: " + Utils.getFormattedNumber(buyamount, ',')
					+ "<br>Best sell price: " + Utils.getFormattedNumber(buyprice, ','));
			} else
				player.getPackets().sendIComponentText(105, 143, ItemExamines.getGEExamine(new Item(id))
					+ "<br><br>There are currently no buy offers for this item.");
		}
		if (getType() == 0) { //Buy
			if (UnlimitedGEReader.itemIsLimited(id)) {
				player.getPackets().sendIComponentText(105, 143, 
						ItemExamines.getGEExamine(new Item(id)) + "<br> <br>Quantity: Unlimited.");
			} else {
				if (totalsellamount > 0) {
					player.getPackets().sendIComponentText(105, 143, ""
						+ ItemExamines.getGEExamine(new Item(id))
						+ "<br><br>Quantity: " + Utils.getFormattedNumber(sellamount, ',')
						+ "<br>Lowest price: " + Utils.getFormattedNumber(sellprice, ','));
				} else
					player.getPackets().sendIComponentText(105, 143, ""
						+ ItemExamines.getGEExamine(new Item(id))
						+ "<br> <br>There are currently no sell offers for this item.");

			}
		}
	}

	public void setItem(Item item, boolean sell) {
		if (item.getId() == Shop.COINS || !ItemConstants.isTradeable(item) || item.getName().contains("charm")
				 || item.getId() == 11640) {
			player.sendMessage("This item cannot be traded on the Grand Exchange.");
			player.getPackets().sendSound(4039, 1, 1);
			return;
		}
		if (item.getId() == 18832)
			item.setId(18830);
		if (item.getDefinitions().isNoted() && item.getDefinitions().getCertId() != -1)
			item = new Item(item.getDefinitions().getCertId(), item.getAmount());
		int price = GrandExchange.getPrice(item.getId());
		setPricePerItem(price);
		setAmount(item.getAmount());
		setExtraDetails(item.getId());
		if (!sell)
			sendInfo(item);
		player.getPackets().sendHideIComponent(105, 233, false);//reload
	}

	public void confirmOffer() {
		int type = getType();
		if (type == -1)
			return;
		int slot = getCurrentSlot();
		if (slot == -1 || !isSlotFree(slot))
			return;
		boolean buy = type == 0;
		int itemId = getItemId();
		if (itemId == -1) {
			player.sendMessage("You must choose an item to " + (buy ? "buy" : "sell") + "!");
			player.getPackets().sendSound(4039, 1, 1);
			return;
		}
		int amount = getAmount();
		if (amount == 0) {
			player.sendMessage("You must choose the quantity you wish to " + (buy ? "buy" : "sell") + "!");
			player.getPackets().sendSound(4039, 1, 1);
			return;
		}
		int pricePerItem = getPricePerItem();
		if (pricePerItem != 0) {
			if (amount > 2147483647 / pricePerItem) { // TOO HIGH
				player.sendMessage("You do not have enough coins to cover the offer.");
				player.getPackets().sendSound(4039, 1, 1);
				return;
			}
		}
		if (buy) {
			int price = pricePerItem * amount;
			if (!player.hasMoney(price)) {
				player.sendMessage("You do not have enough coins to cover the offer.");
				player.getPackets().sendSound(4039, 1, 1);
				return;
			} else
				player.takeMoney(price);
		} else {
			int inventoryAmount = getItemAmount(new Item(itemId));
			if (amount > inventoryAmount) {
				player.sendMessage("You do not have enough of this item in your inventory.");
				player.getPackets().sendSound(4039, 1, 1);
				return;
			}
			int notedId = ItemDefinitions.getItemDefinitions(itemId).certId;
			int notedAmount = player.getInventory().getNumberOf(notedId);
			if (notedAmount < amount) {
				player.getInventory().deleteItem(notedId, notedAmount);
				player.getInventory().deleteItem(itemId, amount - notedAmount);
			} else
				player.getInventory().deleteItem(notedId, amount);
		}
		GrandExchange.sendOffer(player, slot, itemId, amount, pricePerItem, buy);
		player.getPackets().sendSound(4043, 1, 1);
		cancelOffer();
	}

	public void makeOffer(int slot, boolean sell) {
		if (!isSlotFree(slot) || getCurrentSlot() != -1)
			return;
		setType(sell ? 1 : 0);
		setSlot(slot);
		player.getPackets().sendHideIComponent(105, 233, true);
		if (sell) {
			player.getPackets().sendHideIComponent(105, 196, true);
	        player.getInterfaceManager().sendInventoryInterface(107);
	        player.getPackets().sendAccessMask(player, 0, 27, 107, 18, 1026);
	        player.getPackets().sendUnlockIComponentOptionSlots(107, 18, 0, 27, 0);
	        player.getPackets().sendInterSetItemsOptionsScript(107, 18, 93, 4, 7, "Offer");
		} else {
			player.getPackets().sendInterface(false, 752, 7, 389);
			player.getPackets().sendRunScript(570, new Object[] { "Grand Exchange Item Search" });
		}
	}
	
	/**
	 * Checks if the player can use the G.E.
	 * @return true if The player can use it.
	 */
	public boolean canUse() {
		return !player.isIronMan() && !player.isHCIronMan();
	}

	/**
	 * Opens the item sets interface.
	 */
	public void openItemSets() {
		player.getInterfaceManager().sendInterface(645);
		player.getInterfaceManager().sendInventoryInterface(644);
		player.getPackets().sendIComponentSettings(645, 16, 0, 115, 14);
		player.getPackets().sendUnlockIComponentOptionSlots(644, 0, 0, 27, 0, 1, 2);
		player.getPackets().sendInterSetItemsOptionsScript(644, 0, 93, 4, 7, "Components", "Exchange", "Examine");
		player.getPackets().sendRunScriptBlank(676);
	}
}