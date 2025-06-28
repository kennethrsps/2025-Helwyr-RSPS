package com.rs.game.player.dialogue.impl;

import com.rs.game.player.SquealOfFortune;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.Settings;
import com.rs.game.item.Item;

public class SpinRewardExchanger extends Dialogue {
	private int npcId;
	private Item item;
	private int rarity;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		if (parameters.length > 1)
			item = (Item) parameters[1];
		if (item != null) {
			rarity = -1;
			for (int itemId : Settings.SOF_RARE_LAMPS)
				if (item.getId() == itemId) {
					rarity = SquealOfFortune.RARITY_RARE;
					break;
				}
			if (rarity == -1)
				for (int itemId : Settings.SOF_RARE_OTHERS)
					if (item.getId() == itemId) {
						rarity = SquealOfFortune.RARITY_RARE;
					}
			if (rarity == -1)
				for (int itemId : Settings.SOF_JACKPOT_LAMPS)
					if (item.getId() == itemId) {
						rarity = SquealOfFortune.RARITY_JACKPOT;
					}
			if (rarity == -1)
				for (int itemId : Settings.SOF_JACKPOT_OTHERS)
					if (item.getId() == itemId) {
						rarity = SquealOfFortune.RARITY_JACKPOT;
					}
			if (rarity == -1 || item.getDefinitions().isStackable()) {
				player.getDialogueManager().startDialogue("SimpleNPCMessage", npcId,
						"I am not interested in this item.");
				return;
			}
			stage = 1;
			sendNPCDialogue(npcId, CHEERFULLY_TALK,
					"Wow what a lucky guy! Will you give me it for " + (rarity == SquealOfFortune.RARITY_RARE ? 15 : 25)
							+ " spins? the transaction will cost you 2M GP.");
			return;
		}
		sendNPCDialogue(npcId, CHEERFULLY_TALK,
				"Hello, I can exhange your unwanted spin reward for spins! Note that im only interested in rare and very rare rewards and it will cost you 2m per transaction.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			sendNPCDialogue(npcId, CHEERFULLY_TALK, "Use the item on me to exchange it.");
			break;
		case 0:
			end();
			break;
		case 1:
			stage = 2;
			sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes, exchange it please.", "No thanks.");
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				int cost = 2000000;
				if (player.getInventory().getCoinsAmount() < cost) {
					player.getDialogueManager().startDialogue("SimpleNPCMessage", npcId,
							"You don't have enough coins to covert the transaction cost.");
					return;
				}
				if (!player.getInventory().containsItem(item)) {
					player.getDialogueManager().startDialogue("SimpleNPCMessage", npcId,
							"Ops something went wrong use the item on me again.");
					return;
				}
				end();
				int amount = (rarity == SquealOfFortune.RARITY_RARE ? 15 : 25);
				player.getInventory().removeItemMoneyPouch(new Item(995, cost));
				player.getInventory().deleteItem(item);
				player.getSquealOfFortune().giveEarnedSpins(amount);
				player.getPackets().sendGameMessage("You have recieved " + amount + " spins.");
				break;
			default:
				end();
				break;
			}
			break;
		}
	}

	@Override
	public void finish() {
	}

}
