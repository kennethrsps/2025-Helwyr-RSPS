package com.rs.game.player.content.items;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.content.ItemConstants;
import com.rs.utils.Colors;

/**
 * Handles the Magic notepaper item.
 * @author Zeus
 */
public class MagicNotepaper {
	
	/**
	 * Represents the notepaper item.
	 */
	public static Item notepaper = new Item(30372);

	/**
	 * Handles using Magic notepaper on the item to note it.
	 * @param player The player using.
	 * @param item The Item to note.
	 */
	public static void handleNote(Player player, Item item) {
		if (!player.getInventory().containsItem(notepaper)) { //Just incase.
			player.sendMessage("You don't have any "+notepaper.getName()+" left in your inventory.");
			return;
		}
		if (!player.getInventory().containsItem(item)) { //Just incase.
			player.sendMessage("You don't have any "+item.getName()+" left in your inventory.");
			return;
		}
		if (item.getDefinitions().isStackable() || !ItemConstants.isTradeable(item)) {
			player.sendMessage("You can't use "+notepaper.getName()+" on "+item.getName()+".");
			return;
		}
		if (item.getDefinitions().isNoted()) {
			player.sendMessage(item.getName()+" is already noted and can not be unnoted this way.");
			return;
		}
		if (player.getInventory().deleteOneItem(notepaper)) {
			int toNote = item.getDefinitions().getCertId();
			player.getInventory().deleteItem(item);
			player.getInventory().addItemDrop(toNote, 1);
		} else
			player.sendMessage(Colors.red+"[err.code: 30372] Please report this error to an Administrator.");
		return;
	}
}