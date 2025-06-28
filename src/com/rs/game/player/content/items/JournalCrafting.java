package com.rs.game.player.content.items;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;

/**
 * Handles Hexquiste weapon assembly.
 * @author Kingkenobi
 */
public class JournalCrafting {
	
	/**
	 * Handles creating the SoulFragment.
	 * @param player
	 */
	public static void handleSoulFragment(Player player) {
		if (!player.getInventory().containsItem(40663, 1) || !player.getInventory().containsItem(40664, 1)
				 || !player.getInventory().containsItem(40665, 1) || !player.getInventory().containsItem(40666, 1)
				 || !player.getInventory().containsItem(40667, 1)) {
			player.sendMessage("You'll need all 5 Magister's Journal in order to do this.");
			return;
		}
		if (player.getSkills().getLevelForXp(Skills.DIVINATION) < 92) {
			player.sendMessage("You'll need a Divination level of at least 92 in order to do this.");
			return;
		}
		player.getInventory().deleteItem(40663, 1);
		player.getInventory().deleteItem(40664, 1);
		player.getInventory().deleteItem(40665, 1);
		player.getInventory().deleteItem(40666, 1);
		player.getInventory().deleteItem(40667, 1);
		player.getInventory().addItem(40668, 1);
		player.setNextAnimation(new Animation(11625));
		World.sendGraphics(player, new Graphics(94), player);
		player.sendMessage("You've successfully extract a Soul Fragment!");
	}
	
	
	// * Checks if we can craft a Exquiste weapon.
    // * @param player
	// * @return true if yes
	 
	/**public static boolean canCraftWeapon(Player player, int bodyID) {
		if (!player.getInventory().containsOneItem(40668)) {
			player.sendMessage("You do not have a Soul Fragment to work with.");
			return false;
		}
		if (!player.getInventory().containsOneItem(bodyID)) {
			player.sendMessage("You do not have an Weapon piece to work with.");
			return false;
		}
		if (player.getSkills().getLevel(Skills.SMITHING) < 90) {
			player.sendMessage("You'll need a Smithing level of at least 92 in order to do this.");
			return false;
		}
		return true;
	}*/
}
