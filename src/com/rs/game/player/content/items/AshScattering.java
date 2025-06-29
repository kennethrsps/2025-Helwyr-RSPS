package com.rs.game.player.content.items;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;

/**
 * Handles the Scattering of various prayer Ashes.
 * 
 * @author Zeus.
 */
public class AshScattering {

	public enum AshesData {

		IMPIOUS(20264, 4, 56), 
		ACCURSED(20266, 6, 47), 
		INFERNAL(20268, 8, 40), 
		TORTURED(32945, 10, 40), 
		SEARING(34159, 12, 40);

		private int itemId, gfx;

		private double exp;

		public int getItemId() {
			return itemId;
		}

		public double getExp() {
			return exp;
		}

		public int getGFX() {
			return gfx;
		}

		private static Map<Integer, AshesData> ashes = new HashMap<Integer, AshesData>();

		static {
			for (AshesData ash : AshesData.values()) {
				ashes.put(ash.getItemId(), ash);
			}
		}

		public static AshesData forId(int id) {
			return ashes.get(id);
		}

		private AshesData(int itemId, double exp, int gfx) {
			this.itemId = itemId;
			this.exp = exp;
			this.gfx = gfx;
		}
	}

	/**
	 * Handles the scattering of the ashes
	 * 
	 * @param player
	 *            - the player
	 * @param slot
	 *            - the inventory slot
	 * @return
	 */
	public static boolean scatter(final Player player, final int slot) {
		final Item item = player.getInventory().getItem(slot);
		if (item == null || AshesData.forId(item.getId()) == null || player.isLocked())
			return false;
		final AshesData ashesData = AshesData.forId(item.getId());
		player.lock(1);
		player.setNextAnimation(new Animation(445));
		player.setNextGraphics(new Graphics(ashesData.getGFX()));
		player.sendMessage("You scatter the ashes in the wind.", true);
		player.getSkills().addXp(Skills.PRAYER, ashesData.getExp());
		player.getInventory().deleteItem(item.getId(), 1);
		return true;
	}

}