package com.rs.game.player.dialogue.impl;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Skills;
import com.rs.game.player.content.DungeonRewardShop;
import com.rs.game.player.content.DungeonRewardShop.DungeonReward;
import com.rs.game.player.dialogue.Dialogue;

public class DungRewardConfirm extends Dialogue {

    DungeonReward item;

	@Override
	public void finish() { }

	@Override
	public void run(int interfaceId, int componentId) {
		if (interfaceId == 1183 && componentId == 9) {
			if (player.getInventory().getFreeSlots() >= 1) {
			    if (player.getDungeoneeringTokens() < item.getCost())
			    	return;
			    if (player.getSkills().getLevel(Skills.DUNGEONEERING) < item.getRequirement())
			    	return;
				player.setDungeoneeringTokens(player.getDungeoneeringTokens() - item.getCost());
				player.getInventory().addItem(item.getId(), 1);
				DungeonRewardShop.refreshPoints(player);
			} else {
				player.getPackets().sendGameMessage("Inventory too full. Sell, drop or bank something for more space.");
			}
		}
		end();
	}

	@Override
	public void start() {
		item = (DungeonReward) parameters[0];
		player.getInterfaceManager().sendChatBoxInterface(1183);
		player.getPackets().sendItemOnIComponent(1183, 13, item.getId(), 1);
		player.getPackets().sendIComponentText(1183, 22, "Are you sure you want to buy this?");
		player.getPackets().sendIComponentText(1183, 3, "Confirm");
		player.getPackets().sendIComponentText(1183, 26, "Cancel");
		player.getPackets().sendIComponentText(1183, 7, ItemDefinitions.getItemDefinitions(item.getId()).getName());
	}
}