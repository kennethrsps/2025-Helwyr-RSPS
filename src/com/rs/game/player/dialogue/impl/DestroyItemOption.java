package com.rs.game.player.dialogue.impl;

import com.rs.game.item.Item;
import com.rs.game.player.dialogue.Dialogue;

public class DestroyItemOption extends Dialogue {

	int slotId;
	Item item;

	@Override
	public void start() {
		slotId = (Integer) parameters[0];
		item = (Item) parameters[1];
		player.getInterfaceManager().sendChatBoxInterface(1183);
		player.getPackets().sendIComponentText(1183, 7, item.getName());
		player.getPackets().sendItemOnIComponent(1183, 13, item.getId(), 1);
		player.getPackets().sendIComponentText(1183, 22, "Are you sure you want to destroy this item?");
		player.getPackets().sendIComponentText(1183, 12, "You can re-claim this item from the place you got it.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (interfaceId == 1183 && componentId == 9) {
			player.getInventory().deleteItem(slotId, item);
			player.getCharges().degradeCompletly(item);
			player.getPackets().sendSound(4500, 0, 1);
			if (item.getDefinitions().isBinded())
				player.getDungManager().unbind(item);
			player.getInterfaceManager().closeChatBoxInterface();
			end();
			return;
		}
		player.getInterfaceManager().closeChatBoxInterface();
		end();
	}

	@Override
	public void finish() {
	}
}