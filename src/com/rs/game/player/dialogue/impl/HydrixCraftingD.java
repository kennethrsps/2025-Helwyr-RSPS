package com.rs.game.player.dialogue.impl;

import com.rs.game.Animation;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class HydrixCraftingD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("What would you like to make?", "Hydrix Ring", "Amulet of Souls", "Hydrix Necklace", "cancel");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (componentId == OPTION_1) {
				if (!player.getInventory().containsItem(2357, 1) || !player.getInventory().containsItem(31855, 1)) {
					player.sendMessage("You need Goldbar and Hydrix to make Hydrix Ring!");
					end();
					return;
				}
				if (!player.getToolBelt().contains(1592)) {
					player.sendMessage("You need ring mould in your toolbelt!");
					end();
					return;
				}
				player.setNextAnimation(new Animation(3243));
				player.getSkills().addXp(Skills.CRAFTING,100);
				player.getInventory().deleteItem(2357, 1);
				player.getInventory().deleteItem(31855, 1);
				player.getInventory().addItem(31857, 1);
				end();
				return;
			}
			if (componentId == OPTION_2) {
				if (!player.getInventory().containsItem(2357, 1) || !player.getInventory().containsItem(31855, 1) || !player.getInventory().containsItem(1759, 1)) {
					player.sendMessage("You need Goldbar,ball of wool and Hydrix to make Amulet of Souls!");
					end();
					return;
				}
				if (!player.getToolBelt().contains(1595)) {
					player.sendMessage("You need Amulet mould in your Toolbelt!");
					end();
					return;
				}
				player.setNextAnimation(new Animation(3243));
				player.getSkills().addXp(Skills.CRAFTING,100);
				player.getInventory().deleteItem(2357, 1);
				player.getInventory().deleteItem(31855, 1);
				player.getInventory().deleteItem(1759, 1);
				player.getInventory().addItem(31875, 1);
				end();
				return;
			}
			if (componentId == OPTION_3) {
				if (!player.getInventory().containsItem(2357, 1) || !player.getInventory().containsItem(31855, 1) || !player.getInventory().containsItem(1759, 1)) {
					player.sendMessage("You need Goldbar,ball of wool and Hydrix to make Hydrix Ring!");
					end();
					return;
				}
				if (!player.getToolBelt().contains(1597)) {
					player.sendMessage("You need Nacklace mould in your Toolbelt!");
					end();
					return;
				}
				player.setNextAnimation(new Animation(3243));
				player.getSkills().addXp(Skills.CRAFTING,100);
				player.getInventory().deleteItem(2357, 1);
				player.getInventory().deleteItem(31855, 1);
				player.getInventory().deleteItem(1759, 1);
				player.getInventory().addItem(31859, 1);
				end();
				return;
			}
			if (componentId == OPTION_4) {
				end();
				return;
			}

			break;

		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
