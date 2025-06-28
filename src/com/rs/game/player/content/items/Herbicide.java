package com.rs.game.player.content.items;

import com.rs.game.npc.Drop;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.herblore.HerbCleaning;

/**
 * Handles the Herbicide item.
 * @author Zeus
 */
public class Herbicide {

	/**
	 * Handles Herbicide herb cleaning.
	 * @param player The player.
	 * @param dropName The dropName.
	 * @param item The item.
	 * @param drop The drop.
	 * @return if Can clean.
	 */
	public static boolean handleHerbicide(Player player, String dropName, Drop drop) {
		if (player.getInventory().containsItem(19675, 1) && dropName.toLowerCase().contains("grimy")) {
			if (HerbCleaning.getHerb(drop.getItemId()) != null) {
				if (player.getSkills().getLevelForXp(Skills.HERBLORE) >= 
						HerbCleaning.getHerb(drop.getItemId()).getLevel()) {
					player.getSkills().addXp(Skills.HERBLORE, HerbCleaning.getHerb(drop.getItemId()).getExperience() * 2);
					return true;
				}
				return false;
			}
			return false;
		}
		return false;
	}
}