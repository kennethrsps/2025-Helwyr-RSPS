package com.rs.game.player.actions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Utils;

/**
 * @author ARMAR X K1NG && HELPED With Enum Miles
 */

public class CombinationPotions extends Action {

	public enum CombinedPotion {

		GRAND_STRENGTH_POTION(32958, new int[] { 2440, 113 }, 75, 123.8),

		GRAND_RANGING_POTION(32970, new int[] { 2444, 27504 }, 76, 144.4),

		GRAND_MAGIC_POTION(32982, new int[] { 3040, 27512 }, 77, 155.7),

		GRAND_ATTACK_POTION(32994, new int[] { 2436, 2428 }, 78, 93.8),

		GRAND_DEFENCE_POTION(33006, new int[] { 2442, 2432 }, 79, 146.3),

		SUPER_MELEE_POTION(33018, new int[] { 2436, 2440, 2442 }, 81, 281.3),

		SUPER_WARMASTERS_POTION(33030, new int[] { 2436, 2440, 2442, 2444, 3040 }, 85, 500.0),

		REPLENISHMENT_POTION(33042, new int[] { 15300, 3024 }, 87, 256.9),

		WYRMFIRE_POTION(33054, new int[] { 2452, 15304 }, 89, 275.7),

		EXTREME_BRAWLERS_POTION(33066, new int[] { 15308, 15312, 15316 }, 91, 367.5),

		EXTREME_BATTLEMAGES_POTION(33078, new int[] { 15320, 15316 }, 91, 367.5),

		EXTREME_SHARPSHOOTERS_POTION(33090, new int[] { 15324, 15316 }, 91, 367.5),

		EXTREME_WARMASTERS_POTION(33102, new int[] { 15308, 15312, 15316, 15320, 15324 }, 93, 500),

		SUPREME_STRENGTH_POTION(33114, new int[] { 15312, 2440 }, 93, 266.3),

		SUPREME_ATTACK_POTION(33126, new int[] { 15308, 2436 }, 93, 240),

		SUPREME_DEFENCE_POTION(33138, new int[] { 15316, 2442 }, 93, 292.5),

		SUPREME_MAGIC_POTION(33150, new int[] { 15320, 3040 }, 93, 316.9),

		SUPREME_RANGING_POTION(33162, new int[] { 15324, 2444 }, 93, 217.5),

		BRIGHTFIRE_POTION(33174, new int[] { 21630, 15304 }, 94, 300),

		SUPER_PRAYER_RENEWAL_POTION(33186, new int[] { 21630, 2434 }, 96, 208.2),

		HOLY_OVERLOAD_POTION(33246, new int[] { 15332, 21630 }, 97, 350),

		SEARING_OVERLOAD_POTION(33258, new int[] { 15332, 15304 }, 97, 350),

		OVERLOAD_SALVE(-1, new int[] { 15332, 15304, 21630, 2434, 2448 }, 97, 500),

		SUPREME_OVERLOAD_POTION(33210, new int[] { 15332, 2436, 2440, 2442, 2444, 3040 }, 98, 600),

		SUPREME_OVERLOAD_SALVE(-1, new int[] { 33210, 15332, 15304, 21630, 2434, 2448 }, 99, 600),

		PERFECT_PLUS_POTION(33234, new int[] { 33210, 32947, 32270 }, 99, 1000);

		private int potionId;
		private int[] requiredItemsIds;
		private int level;
		private double experience;

		private static HashMap<Integer, CombinedPotion> potions;

		static {
			potions = new HashMap<Integer, CombinedPotion>();
			for (CombinedPotion potion : CombinedPotion.values())
				potions.put(potion.getPotionId(), potion);
		}

		public static CombinedPotion forId(int potionId) {
			return potions.get(potionId);
		}

		CombinedPotion(int potionId, int[] requiredItemsIds, int level, double experience) {
			this.potionId = potionId;
			this.requiredItemsIds = requiredItemsIds;
			this.level = level;
			this.experience = experience;
		}

		public int getPotionId() {
			return potionId;
		}

		public int[] getRequiredItemsIds() {
			return requiredItemsIds;
		}

		public int getLevel() {
			return level;
		}

		public double getExperience() {
			return experience;
		}
	}

	public static int CRYSTAL_FLASK = 32843;

	public static boolean combine(Player player, Item item) {
		if (item.getId() == CRYSTAL_FLASK && player.getInventory().containsItem(CRYSTAL_FLASK, 1)) {
			player.getDialogueManager().startDialogue("CombinationPotionsD");
			return true;
		}
		return false;
	}

	public static void startCombinationAction(Player player, int amount, int potionId) {
		int availableAmount = getAvailableAmount(player, potionId);
		if (amount == 0 || availableAmount == 0)
			return;
		if (amount > availableAmount)
			amount = availableAmount;
		player.stopAll();
		player.getActionManager().setAction(new CombinationPotions(amount, potionId));
	}

	public static int getAvailableAmount(Player player, int potionId) {
		CombinedPotion potion = CombinedPotion.forId(potionId);
		if (potion == null)
			return 0;
		int amount = Integer.MAX_VALUE;
		for (int i = 0; i < potion.getRequiredItemsIds().length; i++) {
			int requiredItemAmount = player.getInventory().getAmountOf(potion.getRequiredItemsIds()[i]);
			if (amount > requiredItemAmount)
				amount = requiredItemAmount;
		}
		return amount;
	}

	public static List<CombinedPotion> getAvailableCombinedPotions(Player player) {
		List<CombinedPotion> availableCombinedPotions = new ArrayList<CombinedPotion>();
		for (CombinedPotion potion : CombinedPotion.values()) {
			if (potion == null)
				continue;
			boolean hasAllRequiredItems = true;
			for (int requiredItemId : potion.getRequiredItemsIds()) {
				if (!player.getInventory().containsItem(requiredItemId, 1))
					hasAllRequiredItems = false;
			}
			if (hasAllRequiredItems)
				availableCombinedPotions.add(potion);
		}
		return availableCombinedPotions;
	}

	private CombinedPotion potion;
	private int amountToMake;
	private int potionId;

	public CombinationPotions(int amountToMake, int potionId) {
		this.amountToMake = amountToMake;
		this.potionId = potionId;
	}

	@Override
	public boolean start(Player player) {
		potion = CombinedPotion.forId(potionId);
		if (!checkAll(player))
			return false;
		return true;
	}

	private boolean checkAll(Player player) {
		if (!player.getInventory().containsItem(CRYSTAL_FLASK, 1))
			return false;
		if (potion != null) {
			for (int requiredItemId : potion.getRequiredItemsIds()) {
				if (!player.getInventory().containsItem(requiredItemId, 1)) {
					player.getPackets()
							.sendGameMessage("You don't have all the required ingredients to combine this potion.");
					return false;
				}
			}
			if (player.getSkills().getLevel(Skills.HERBLORE) < potion.getLevel()) {
				player.getPackets().sendGameMessage(
						"You need a herblore level of " + potion.getLevel() + " to combine these potions.");
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player) && amountToMake > 0;
	}

	@Override
	public int processWithDelay(Player player) {
		amountToMake--;
		player.setNextAnimation(new Animation(24896));
		for (int requiredItemId : potion.getRequiredItemsIds())
			player.getInventory().deleteItem(requiredItemId, 1);
		player.getInventory().deleteItem(CRYSTAL_FLASK, 1);
		player.getInventory().addItem(potion.getPotionId(), 1);
		player.getSkills().addXp(Skills.HERBLORE, potion.getExperience());
		player.getPackets()
				.sendGameMessage("You have succefully combined the potions into "
						+ Utils.formatString(
								ItemDefinitions.getItemDefinitions(potion.getPotionId()).getName().replace(" (6)", ""))
						+ ".");
		return 1;
	}

	@Override
	public void stop(Player player) {

	}

}
