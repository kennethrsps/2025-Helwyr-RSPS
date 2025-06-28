package com.rs.game.player.actions.crafting;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.pet.Pets;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class GemCutting extends Action {

	/**
	 * Enum for gems
	 * 
	 * @author Raghav
	 * @author Zeus
	 */
	public enum Gem {

		OPAL(1625, 1609, 15.0, 1, 22778),

		JADE(1627, 1611, 20, 13, 22779),

		RED_TOPAZ(1629, 1613, 25, 16, 22780),

		SAPPHIRE(1623, 1607, 40, 20, 22774),

		EMERALD(1621, 1605, 57, 27, 22775),

		RUBY(1619, 1603, 65, 34, 22776),

		DIAMOND(1617, 1601, 85, 43, 22777),

		DRAGONSTONE(1631, 1615, 107.5, 55, 22781),

		ONYX(6571, 6573, 200, 67, 22782),

		HYDRIX(31853, 31855, 300, 79, 22783);

		private double experience;
		private int levelRequired, uncut, cut, emote;

		private Gem(int uncut, int cut, double experience, int levelRequired, int emote) {
			this.uncut = uncut;
			this.cut = cut;
			this.experience = experience;
			this.levelRequired = levelRequired;
			this.emote = emote;
		}

		public int getCut() {
			return cut;
		}

		public int getEmote() {
			return emote;
		}

		public double getExperience() {
			return experience;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getUncut() {
			return uncut;
		}
	}

	public static void cut(Player player, Gem gem) {
		if (player.getInventory().getItems().getNumberOf(new Item(gem.getUncut(), 1)) <= 1)
			player.getActionManager().setAction(new GemCutting(gem, 1));
		else
			player.getDialogueManager().startDialogue("GemCuttingD", gem);
	}

	private Gem gem;
	private int quantity;

	public GemCutting(Gem gem, int quantity) {
		this.gem = gem;
		this.quantity = quantity;
	}

	public boolean checkAll(Player player) {
		if (player.getInterfaceManager().containsScreenInter()
				|| player.getInterfaceManager().containsInventoryInter()) {
			player.getPackets().sendGameMessage("Please finish what you're doing before doing this action.");
			return false;
		}
		if (player.getSkills().getLevel(Skills.CRAFTING) < gem.getLevelRequired()) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You need a crafting level of " + gem.getLevelRequired() + " to cut that gem.");
			return false;
		}
		if (!player.getInventory().containsOneItem(gem.getUncut())) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You don't have any "
					+ ItemDefinitions.getItemDefinitions(gem.getUncut()).getName().toLowerCase() + " to cut.");
			return false;
		}
		if (!player.getInventory().containsItem(1755, 1) && !player.getToolBelt().contains(1755)) {
			player.sendMessage("You don't have a chisel to cut the "
					+ ItemDefinitions.getItemDefinitions(gem.getUncut()).getName().toLowerCase() + ".");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		player.getInventory().deleteItem(gem.getUncut(), 1);
		player.getInventory().addItem(gem.getCut(), 1);
		Pets.checkSkillingPet(player, 38081);
		player.getSkills().addXp(Skills.CRAFTING, gem.getExperience());
		player.addItemsMade();
		player.sendMessage("You cut the " + ItemDefinitions.getItemDefinitions(gem.getUncut()).getName().toLowerCase()
				+ "; items crafted: " + Colors.red + Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
		quantity--;
		if (quantity <= 0)
			return -1;
		player.setNextAnimation(new Animation(gem.getEmote()));
		return 0;
	}

	@Override
	public boolean start(Player player) {
		if (checkAll(player)) {
			setActionDelay(player, 1);
			player.setNextAnimation(new Animation(gem.getEmote()));
			return true;
		}
		return false;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}
}
