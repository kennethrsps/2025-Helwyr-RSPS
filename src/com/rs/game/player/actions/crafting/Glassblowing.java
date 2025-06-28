package com.rs.game.player.actions.crafting;

import com.rs.game.Animation;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles Glassblowing.
 * @author Zeus
 */
public class Glassblowing {

	/**
	 * The glassblowing animation.
	 */
	public static final Animation BLOWING_ANIMATION = new Animation(884);

	/**
	 * Can the player blow the Item?
	 *
	 * @param player
	 *            The player.
	 * @param itemId
	 *            The Item's ID.
	 * @return If the player can blow the Item.
	 */
	public static boolean canBlow(Player player, int itemId) {
		for (GlassblowingItem item : GlassblowingItem.values()) {
			if (item.getAfterId() == itemId) {
				blow(player, item);
				return true;
			}
		}
		return false;
	}

	/**
	 * Spins the Item.
	 *
	 * @param player
	 *            The player glassblowing the Item.
	 * @param item
	 *            The GlassblowItem being blowed.
	 */
	private static void blow(Player player, GlassblowingItem item) {
		if (player.getSkills().getLevel(Skills.CRAFTING) < item.getSkillRequirement()) {
			player.sendMessage("You need a Crafting level of " + item.getSkillRequirement() + " to make this.");
			return;
		}
		player.setNextAnimation(BLOWING_ANIMATION);
		player.getSkills().addXp(Skills.CRAFTING, item.getExp());
		player.getInventory().deleteItem(new Item(1775));
		player.getInventory().addItem(new Item(item.getAfterId()));
		player.addItemsMade();
		player.sendMessage("You form a figure out of the glass; "
				+ "items crafted: "+Colors.red+Utils.getFormattedNumber(player.getItemsMade())+"</col>.", true);
	}

	/**
	 * The glassblow'able items.
	 */
	public enum GlassblowingItem {
		
		BEER_GLASS(1919, 1, 17.5),
		OIL_LAMP(4522, 12, 25),
		VIAL(229, 33, 35),
		FISHBOWL(6667, 42, 42.5),
		UNPOWERED_ORB(567, 46, 52.5),
		POTION_FLASK(23191, 89, 100);
		/**
		 * The ID after.
		 */
		private int afterId;
		/**
		 * The required level.
		 */
		private int skillRequirement;
		/**
		 * The experience gained.
		 */
		private double exp;

		/**
		 * The single blowing Item.
		 *
		 * @param beforeId
		 *            The Item ID before.
		 * @param afterId
		 *            The Item ID after.
		 * @param skillRequirement
		 *            The required level of Crafting to make the Item.
		 * @param exp
		 *            The experience gained for blowing the item.
		 */
		GlassblowingItem(int afterId, int skillRequirement, double exp) {
			this.afterId = afterId;
			this.skillRequirement = skillRequirement;
			this.exp = exp;
		}

		/**
		 * Gets the ID after.
		 *
		 * @return The ID after.
		 */
		public int getAfterId() {
			return afterId;
		}

		/**
		 * Gets the Crafting level required.
		 *
		 * @return The Crafting level required.
		 */
		public int getSkillRequirement() {
			return skillRequirement;
		}

		/**
		 * Gets the experience gained.
		 *
		 * @return The experience gained.
		 */
		public double getExp() {
			return exp;
		}
	}
}