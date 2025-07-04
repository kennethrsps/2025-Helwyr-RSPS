package com.rs.game.player.content.items;

import com.rs.game.player.Player;
import com.rs.game.player.Skills;

/**
 * Handles Telos weapon assembly.
 * @author KINGKENOBI
 */
public class TelosCrafting {
	
	/**
	 * Handles creating the Ancien Sigil.
	 * @param player
	 */
	public static void handleAncientSigil(Player player) {
		if (!player.getInventory().containsItem(37619, 1) || !player.getInventory().containsItem(37620, 1)
				 || !player.getInventory().containsItem(37621, 1)) {
			player.sendMessage("You'll need all 3 orb in order to do this.");
			return;
		}
		if (player.getSkills().getLevelForXp(Skills.CRAFTING) < 30) {
			player.sendMessage("You'll need a Crafting level of at least 30 in order to do this.");
			return;
		}
		player.getInventory().deleteItem(37619, 1);
		player.getInventory().deleteItem(37620, 1);
		player.getInventory().deleteItem(37621, 1);
		player.getInventory().addItem(37614, 1);
		player.sendMessage("You've successfully made a Ancient Sigil!");
	}
	
	/**
	 * Checks if we can craft a Telos weapon.
	 * @param player
	 * @return true if yes
	 */
	public static boolean canCraftWeapon(Player player, int bodyID) {
		if (!player.getInventory().containsOneItem(37614)) {
			player.sendMessage("You do not have a Ancient sigil to work with.");
			return false;
		}
		if (!player.getInventory().containsOneItem(bodyID)) {
			player.sendMessage("You do not have a Dormant weapon do work with.");
			return false;
		}
		if (player.getSkills().getLevel(Skills.CRAFTING) < 90) {
			player.sendMessage("You'll need a Crafting level of at least 90 in order to do this.");
			return false;
		}
		return true;
	}
}
