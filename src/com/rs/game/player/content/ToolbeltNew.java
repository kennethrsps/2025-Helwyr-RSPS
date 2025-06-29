package com.rs.game.player.content;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.rs.game.item.Item;
import com.rs.game.player.Player;/*
import com.rs.game.player.controllers.InstancedPVPControler;*/

/**
 * Handles a {@link Player}'s NEW rs3 toolbelt.
 * 
 * @author Zeus.
 */
public class ToolbeltNew implements Serializable {

	private static final long serialVersionUID = 2054084250525451553L;

	private transient Player player;

	private static final int[] TOOLBELT_ITEMS = new int[] { 1349, 1353, 1355, 1357, 1359, 6739, 32645, // Hatchets
			1267, 1269, 1271, 1273, 1275, 15259, 32646 // Pickaxes
	};

	private Map<Integer, Boolean> items;

	public ToolbeltNew(Player player) {
		this.player = player;
	}

	public void refresh() {
		if (items == null) {
			items = new HashMap<Integer, Boolean>();
			for (int itemId : TOOLBELT_ITEMS)
				items.put(itemId, false);
		}
		for (int i = 0; i < TOOLBELT_ITEMS.length; i++) {
			if (items.get(TOOLBELT_ITEMS[i]) == null)
				continue;
			boolean inToolbelt = items.get(TOOLBELT_ITEMS[i]);
			if (!inToolbelt)
				continue;
		}
	}

	public boolean addItem(Item item) {
		if (items.get(item.getId()) == null)
			return false;
		if (contains(item.getId())) {
			player.sendMessage("The " + item.getName() + " is already in your toolbelt.");
			return false;
		}
		/*if (player.getControlerManager().getControler() instanceof InstancedPVPControler)
			return false;*/
		items.put(item.getId(), true);
		player.sendMessage("You add the " + item.getName() + " to your toolbelt.");
		player.getInventory().deleteItem(item);
		refresh();
		return true;
	}

	public void removeItem(Item item) {
		if (items.get(item.getId()) == null)
			return;
		if (!contains(item.getId()))
			return;
		items.put(item.getId(), false);
		refresh();
	}

	public boolean contains(int itemId) {
		if (items.get(itemId) == null)
			return false;
		return items.get(itemId);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public void switchDungeonneringToolbelt() {
		// TODO

	}
}