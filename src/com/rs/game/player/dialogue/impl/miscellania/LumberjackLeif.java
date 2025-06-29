package com.rs.game.player.dialogue.impl.miscellania;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.content.miscellania.ThroneOfMiscellania;
import com.rs.game.player.dialogue.Dialogue;

public class LumberjackLeif extends Dialogue {

	public int npcId;
	
	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		if(hasLogs(player)) {
			sendNPCDialogue(npcId, ASKING_FACE, "Would yer sell them fine logs of yours?");
		} else {
			sendPlayerDialogue(NORMAL, "I probably should bring him some logs...");
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch(stage) {
		case -1:
			if(hasLogs(player)) {
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
			purchaseLogs(player);
			break;

		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

	public static boolean hasLogs(Player player) {
		Item item = null;
		ItemDefinitions defs = null;
		for(int i = 0; i < player.getInventory().getItemsContainerSize(); i++) {
			item = player.getInventory().getItem(i);
			if(item == null)
				continue;
			defs = new ItemDefinitions(item.getId());
			if(defs.getName().toLowerCase().contains("logs"))
				return true;
		}
		return false;
	}


	public static void purchaseLogs(Player player) {
		Item item = null;
		ItemDefinitions defs = null;
		int value = 0;
		for (int i = 0; i < player.getInventory().getItemsContainerSize(); i++) {
			item = player.getInventory().getItem(i);
			if (item == null)
				continue;
			defs = ItemDefinitions.getItemDefinitions(item.getId());
			value = defs.getValue();
			if(!item.getName().toLowerCase().contains("logs")) 
				continue;
			if(value == 0)//shouldn't happen
				return;
			player.getInventory().deleteItem(item);
			player.getMoneyPouch().addMoney(value * 12, false);

		}
	}

	public static void purchaseLogs(Player player, Item item) {
		ItemDefinitions defs = null;
		int value = 0;
		for (int i = 0; i < player.getInventory().getItemsContainerSize(); i++) {
			if(item != null)
				defs = ItemDefinitions.getItemDefinitions(item.getId());
			value = defs.getValue();
			if(!item.getName().toLowerCase().contains("logs")) 
				return;
			if(value == 0)//shouldn't happen
				return;
			player.getInventory().deleteItem(item);
			player.getMoneyPouch().addMoney(value * 12, false);
		}
	}

}
