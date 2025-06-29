package com.rs.game.player.dialogue.impl.miscellania;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.content.miscellania.ThroneOfMiscellania;
import com.rs.game.player.dialogue.Dialogue;

public class MinerMagnus extends Dialogue {

	public int npcId;
	
	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if(hasOre(player)) {
			npc(ASKING_FACE, "Would yer sell them fine ore of yours?");
		} else {
			sendPlayerDialogue(NORMAL, "I probably shouldn't bother him, He looks rather busy...");
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch(stage) {
		case -1:
			if(hasOre(player)) {
				sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes", "No");
				stage = 1;
			} else {
				end();
			}
			break;
		case 1:
			switch(componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, HAPPY_FACE, "There yer go " + player.getDisplayName() + "!");
				stage = -3;
				break;
			case OPTION_2:
				end();
				break;
			}
			break;
		case -2:
			end();
			break;
		case -3:
			end();
			purchaseOre(player);
			break;

		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	public static boolean hasOre(Player player) {
		Item item = null;
		ItemDefinitions defs = null;
		for(int i = 0; i < player.getInventory().getItemsContainerSize(); i++) {
			item = player.getInventory().getItem(i);
			if(item == null)
				continue;
			defs = new ItemDefinitions(item.getId());
			if(defs.getName().toLowerCase().contains("coal"))
				return true;
		}
		return false;
	}
	

	public static void purchaseOre(Player player) {
		Item item = null;
		ItemDefinitions defs = null;
		int value = 0;
		for (int i = 0; i < player.getInventory().getItemsContainerSize(); i++) {
			item = player.getInventory().getItem(i);
			if(item == null)
				continue;
			if(item != null)
				defs = ItemDefinitions.getItemDefinitions(item.getId());
				value = defs.getValue();
			if(!item.getName().toLowerCase().contains("coal")) 
				continue;
			if(value == 0)//shouldn't happen
				return;
			player.getInventory().deleteItem(item);
			player.getMoneyPouch().addMoney(value * 5, false);

		}
	}
	
	public static void purchaseOre(Player player, Item item) {
		ItemDefinitions defs = null;
		int value = 0;
		for (int i = 0; i < player.getInventory().getItemsContainerSize(); i++) {
			if(item != null)
				defs = ItemDefinitions.getItemDefinitions(item.getId());
				value = defs.getValue();
			if(!item.getName().toLowerCase().contains("coal")) 
				return;
			if(value == 0)//shouldn't happen
				return;
			player.getInventory().deleteItem(item);
			player.getMoneyPouch().addMoney(value * 5, false);
		}
	}

}
