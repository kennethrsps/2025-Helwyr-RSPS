package com.rs.game.player.dialogue.impl;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Utils;


/**
 * @author JTlr Frost | Jun 1, 2018 | 10:51:01 AM
 *
 */
public class FireDrakeLegendaryPet extends Dialogue {

	public Item item;
	
	@Override
	public void start() {
		item = (Item) parameters[0];
		sendOptionsDialogue("Do what with " + item.getName().toLowerCase() + "?", "Alch The Item", "Note The Item");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if(componentId == OPTION_1) {
			int itemId = (item.getDefinitions().isNoted() ? (item.getId() - 1) : item.getId());
			int alchPrice = ItemDefinitions.getItemDefinitions(itemId).getValue();
			alchPrice = (alchPrice / (int) 2);
			alchPrice = (alchPrice * player.getInventory().getAmountOf(item.getId()));
			if(alchPrice == 0)
				return;
			player.getInventory().addItemMoneyPouch(new Item(995, alchPrice));
			player.getInventory().deleteItem(item.getId(), player.getInventory().getAmountOf(item.getId()));
			player.getPackets().sendGameMessage("<col=E86100>" + player.getPet().getName() + " has alched " + item.getName().toLowerCase() + " for " + Utils.formatNumber(alchPrice) + "gp.");
		} else {
			int notedItem = item.getId() + 1;
			ItemDefinitions defs = new ItemDefinitions(notedItem);
			if(defs.isNoted()) {
				for(int i = 0; i < player.getInventory().getItemsContainerSize(); i++) {
					if(!player.getInventory().containsItem(item))
						continue;
					player.getInventory().deleteItem(item.getId(), item.getAmount());
					player.getInventory().addItem(new Item(notedItem));
				}
				player.getPackets().sendGameMessage("<col=E86100>" + player.getPet().getName() + " notes the " + item.getName().toLowerCase() + "...");
			} else {
				player.getPackets().sendGameMessage("You are unable to note this item.");
			}
		}
		end();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
