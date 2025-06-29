package com.rs.game.player.content.dropprediction;

import java.util.HashMap;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.npc.Drop;
import com.rs.game.player.Player;
import com.rs.utils.NPCDrops;
import com.rs.utils.Utils;

public class DropPrediction implements Runnable {

	private Item[] loots = null;
	private HashMap<Integer, Integer> rareDrops = new HashMap<Integer, Integer>();

	private void addItems(Drop drop) {

		Item item = ItemDefinitions.getItemDefinitions(drop.getItemId()).isStackable()
				? new Item(drop.getItemId(),
						drop.getMinAmount()
								+ Utils.getRandom(drop.getExtraAmount()))
				: new Item(drop.getItemId(), drop.getMinAmount() + Utils.getRandom(drop.getExtraAmount()));
		if (drop.getRate() <= 25)
			rareDrops.put((int) item.getId(), 0);
		for (int i = 0; i < loots.length; i++) {
			if (loots[i] != null && loots[i].getId() == item.getId()) {
				loots[i].setAmount(loots[i].getAmount() + item.getAmount());
				break;
			}
			if (loots[i] == null) {
				loots[i] = item;
				break;
			}
		}
		return;
	}
	
	private int npcId, amount;
	private Player player;
	
	public DropPrediction(Player player, int npcId, int amount) {
		this.npcId = npcId;
		this.amount = amount;
		this.player = player;
	}

	private void generateDrop(Player player, int npcId, int amount) {
		Drop[] drops = NPCDrops.getDrops(npcId);
		if (drops == null)
			return;
		Drop[] possibleDrops = new Drop[drops.length];
		int possibleDropsCount = 0;
		for (Drop drop : drops) {
			if (drop == null)
				continue;
			if (drop.getRate() == 100)
				addItems(drop);
			else {
				double rate = drop.getRate();
				double random = Utils.getRandomDouble(100);
				if (rate < 30)
					rate *= Settings.getDropQuantityRate(player);
				if (random <= rate && random != 100 && random != 0)
					possibleDrops[possibleDropsCount++] = drop;
			}
		}
		if (possibleDropsCount > 0)
			addItems(possibleDrops[Utils.getRandom(possibleDropsCount - 1)]);
		return;
	}

	private void sendInterface(Player player, int npcId, int amount) {
		player.getInterfaceManager().sendInterface(762);
		player.getPackets().sendItems(95, loots);
		rareDrops.forEach((k, v) -> {
			double rate = 0.0000;
			for (Item item : loots) {
				if (item != null && item.getDefinitions().getValue() > 5000)
					if (item.getId() == k) {
						rate = Utils.round(((double) item.getAmount() / amount) * 100, 4);
						player.sendMessage("<col=00ff00>The drop rate for "
								+ ItemDefinitions.getItemDefinitions(k).getName() + ", based off of " + amount
								+ " kills is " + rate + "% (1:" + (amount / item.getAmount()) + ").");
					}
			}

		});
	}

	@Override
	public void run() {
		loots = new Item[100];
		for (int i = 0; i < amount; i++) {
			generateDrop(player, npcId, amount);
		}
		sendInterface(player, npcId, amount);
		return;
	}

}
