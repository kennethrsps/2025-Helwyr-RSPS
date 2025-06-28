package com.rs.game.player.content;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.utils.Utils;

public class ArtisansWorkShop {

	public static enum Ingot {
		// IRON INGOTS
		IRON_I(20632, 30, 101, new int[] { 20572, 20577, 20582, 20587 }, new Item(440, 1)),

		IRON_II(20637, 30, 202, new int[] { 20592, 20597, 20602, 20607 }, new Item(440, 9)),

		IRON_III(20642, 30, 240, new int[] { 20612, 20617, 20622, 20627 }, new Item(440, 12)),

		IRON_IV(20648, 70, 3242, null, new Item(440, 75)),

		// STEEL INGOTS
		STEEL_I(20633, 45, 131, new int[] { 20573, 20578, 20583, 20588 }, new Item(440, 1), new Item(453, 2)),

		STEEL_II(20638, 45, 253, new int[] { 20593, 20598, 20603, 20608 }, new Item(440, 4), new Item(453, 7)),

		STEEL_III(20643, 45, 354, new int[] { 20613, 20618, 20623, 20628 }, new Item(440, 9), new Item(453, 17)),

		STEEL_IV(20649, 75, 4538, null, new Item(440, 40), new Item(453, 80)),

		// MITHRIL INGOTS
		MITHRIL_I(20634, 60, 164, new int[] { 20574, 20579, 20584, 20589 }, new Item(447, 1), new Item(453, 4)),

		MITHRIL_II(20639, 60, 316, new int[] { 20594, 20599, 20604, 20609 }, new Item(447, 3), new Item(453, 12)),

		MITHRIL_III(20644, 60, 404, new int[] { 20614, 20619, 20624, 20629 }, new Item(447, 6), new Item(453, 24)),

		MITHRIL_IV(20650, 80, 5446, null, new Item(447, 30), new Item(453, 120)),

		// ADAMANT INGOTS
		ADAMANT_I(20635, 70, 278, new int[] { 20575, 20580, 20585, 20590 }, new Item(449, 1), new Item(453, 6)),

		ADAMANT_II(20640, 70, 455, new int[] { 20595, 20600, 20605, 20610 }, new Item(449, 3), new Item(453, 14)),

		ADAMANT_III(20645, 70, 568, new int[] { 20615, 20620, 20625, 20630 }, new Item(449, 4), new Item(453, 22)),

		ADAMANT_IV(20651, 85, 6873, null, new Item(449, 25), new Item(453, 150)),

		// RUNE INGOTS
		RUNE_I(20636, 90, 505, new int[] { 20576, 20581, 20586, 20591 }, new Item(451, 1), new Item(453, 8)),

		RUNE_II(20641, 90, 631, new int[] { 20596, 20601, 20606, 20611 }, new Item(451, 2), new Item(453, 16)),

		RUNE_III(20646, 90, 758, new int[] { 20616, 20621, 20626, 20631 }, new Item(451, 4), new Item(453, 30)),

		RUNE_IV(20652, 90, 12585, null, new Item(451, 18), new Item(453, 144)),;
		private static Map<Integer, Ingot> ingots = new HashMap<Integer, Ingot>();

		static {
			for (Ingot ingot : Ingot.values()) {
				ingots.put(ingot.getItemId(), ingot);
			}
		}

		public static Ingot forId(int itemId) {
			return ingots.get(itemId);
		}

		private int itemId;
		private int requiredLevel;
		private int[] products;
		private Item[] requiredItems;
		private double xp;

		private Ingot(int itemId, int requiredLevel, double xp, int[] products, Item... requiredItems) {
			this.itemId = itemId;
			this.xp = xp * 1.5;
			this.products = products;
			this.requiredLevel = requiredLevel;
			this.requiredItems = requiredItems;
		}

		public int getItemId() {
			return itemId;
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public int[] getProducts() {
			return products;
		}

		public Item[] getRequiredItems() {
			return requiredItems;
		}

		public double getXp() {
			return xp;
		}

	}

	public static enum Track {
		BRONZE_RAILS(20506, 1, 1.4, 20502),

		BRONZE_BASE_PLATE(20507, 2, 1.4, 20502),

		BRONZE_SPIKES(20508, 5, 1.4, 20502),

		BRONZE_JOINT(20509, 8, 1.4, 20502),

		BRONZE_TIE(20510, 11, 1.4, 20502),

		BRONZE_TRACK_40(20511, 3, 6, 20506, 20507),

		BRONZE_TRACK_60(20512, 6, 7, 20511, 20508),

		BRONZE_TRACK_80(20513, 9, 9, 20512, 20509),

		BRONZE_TRACK_100(20514, 12, 10, 20513, 20510),

		IRON_RAILS(20515, 15, 5.1, 20503),

		IRON_BASE_PLATE(20516, 19, 5.1, 20503),

		IRON_SPIKES(20517, 24, 5.1, 20503),

		IRON_JOINT(20518, 29, 5.1, 20503),

		IRON_TIE(20519, 34, 5.1, 20503),

		IRON_TRACK_40(20525, 20, 10, 20515, 20516),

		IRON_TRACK_60(20526, 25, 11, 20525, 20517),

		IRON_TRACK_80(20527, 30, 12, 20526, 20518),

		IRON_TRACK_100(20528, 35, 13, 20527, 20519),

		STEEL_RAILS(20520, 39, 8.8, 20504),

		STEEL_BASE_PLATE(20521, 44, 8.8, 20504),

		STEEL_SPIKES(20522, 49, 8.8, 20504),

		STEEL_JOINT(20523, 54, 8.8, 20504),

		STEEL_TIE(20524, 59, 8.8, 20504),

		STEEL_TRACK_40(20529, 45, 13, 20520, 20521),

		STEEL_TRACK_60(20530, 50, 16, 20529, 20522),

		STEEL_TRACK_80(20531, 55, 22, 20530, 20523),

		STEEL_TRACK_100(20532, 60, 25, 20531, 20524);

		private int itemId;
		private int requiredLevel;
		private double xp;
		private int[] requiredItems;

		private Track(int itemId, int requiredLevel, double xp, int... requiredItems) {
			this.itemId = itemId;
			this.requiredLevel = requiredLevel;
			this.xp = xp * 2.5;
			this.requiredItems = requiredItems;
		}

		public int getItemId() {
			return itemId;
		}

		public int getRequiredLevel() {
			return requiredLevel;
		}

		public double getXp() {
			return xp;
		}

		public int[] getRequiredItems() {
			return requiredItems;
		}

	}

	public static int getOreIndex(int itemId) {
		return itemId == 453 ? 0 : itemId == 440 ? 1 : itemId == 447 ? 2 : itemId == 449 ? 3 : 4;
	}

	public static int currentInstructions = 0;

	public static String getInstructionText() {
		return currentInstructions == 0 ? "Helmets"
				: currentInstructions == 1 ? "Boots" : currentInstructions == 2 ? "Chestplates" : "Gauntlets";
	}

	public static void processArtisansWorkShop() {
		int random = Utils.random(4);
		while (currentInstructions == random)
			random = Utils.random(4);
		currentInstructions = random;
	}

	public static void withdrawDepositOre(Player player, int itemId, int amount, boolean deposit) {
		int oreIndex = ArtisansWorkShop.getOreIndex(itemId);
		ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
		if (deposit) {
			int invAmount = player.getInventory().getAmountOf(itemId)
					+ player.getInventory().getAmountOf(defs.getCertId());
			if (amount > invAmount)
				amount = invAmount;
			if (player.ArtisansWorkShopSupplies[oreIndex] == (oreIndex == 0 ? 8000 : 4000)) {
				player.getPackets().sendGameMessage("The machine can't hold any more " + defs.getName() + ".");
				return;
			}
			if (player.ArtisansWorkShopSupplies[oreIndex] + amount > (oreIndex == 0 ? 8000 : 4000)) {
				amount = (oreIndex == 0 ? 8000 : 4000) - player.ArtisansWorkShopSupplies[oreIndex];
				player.getPackets()
						.sendGameMessage("You couldn't deposit all of the ores you wanted.(machine is full)");
			}
			player.ArtisansWorkShopSupplies[oreIndex] += amount;
			player.getPackets().sendGameMessage("You deposit " + amount + " " + defs.getName() + ".");
			if (player.getInventory().containsItem(itemId, 1)) {
				boolean removedAll = true;
				if (amount >= player.getInventory().getAmountOf(itemId)) {
					amount -= player.getInventory().getAmountOf(itemId);
					removedAll = false;
				}
				player.getInventory().deleteItem(itemId,
						removedAll ? amount : player.getInventory().getAmountOf(itemId));
				if (removedAll)
					amount = 0;
			}
			if (amount > 0)
				player.getInventory().deleteItem(defs.getCertId(), amount);
		} else {
			if (amount > player.ArtisansWorkShopSupplies[oreIndex])
				amount = player.ArtisansWorkShopSupplies[oreIndex];
			if (player.getInventory().getFreeSlots() == 0 && !player.getInventory().containsItem(defs.getCertId(), 1)) {
				player.getPackets().sendGameMessage("You don't have enough inventory space.");
				return;
			}
			if (amount == 0) {
				player.getPackets().sendGameMessage("You don't have any " + defs.getName() + " left.");
				return;
			}
			player.getInventory().addItem(defs.getCertId(), amount);
			player.ArtisansWorkShopSupplies[oreIndex] -= amount;
			player.getPackets().sendGameMessage("You withdraw " + amount + " " + defs.getName() + ".");
		}
	}

	public static class ArtisansWorkShopAction extends Action {
		private Ingot ingot;
		private Track track;
		private int productIndex;
		private WorldObject object;
		private int ticks;
		private int hits;

		public ArtisansWorkShopAction(WorldObject object, Ingot ingot, int productIndex, int ticks) {
			this.object = object;
			this.ingot = ingot;
			this.productIndex = productIndex;
			this.ticks = ticks;
		}

		public ArtisansWorkShopAction(WorldObject object, Track track, int ticks) {
			this.object = object;
			this.track = track;
			this.productIndex = -1;
			this.ticks = ticks;
		}

		@Override
		public boolean start(Player player) {
			if (!checkAll(player))
				return false;
			setActionDelay(player, 1);
			hits = ingot != null ? 4 : 2;
			return true;
		}

		private boolean checkAll(Player player) {
			if (ingot != null) {
				if (player.getInventory().getItems().getNumberOf(ingot.getItemId()) < 1) {
					player.getPackets().sendGameMessage("You do not have sufficient bars!");
					return false;
				}
			} else {
				for (int itemId : track.getRequiredItems()) {
					if (!player.getInventory().containsItem(itemId, 1)) {
						player.getPackets().sendGameMessage("You don't have the required items to make this.");
						return false;
					}
				}
			}
			if (!player.getInventory().containsItem(2347, 1)) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You need a hammer in order to work.");
				return false;
			}
			int requiredLevel = ingot != null ? ingot.getRequiredLevel() : track.getRequiredLevel();
			if (player.getSkills().getLevel(Skills.SMITHING) < requiredLevel) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You need a Smithing level of " + requiredLevel + " to create this.");
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
			player.setNextAnimation(new Animation(898));
			World.sendGraphics(player, new Graphics(2123), new WorldTile(object));
			hits--;
			if (hits == 0) {
				ticks--;
				double xp = 0;
				if (ingot != null) {
					player.getInventory().deleteItem(ingot.getItemId(), 1);
					player.getInventory().addItem(ingot.getProducts()[productIndex], 1);

					xp = ingot.getXp();
					player.getPackets()
							.sendGameMessage("You smith the bar into a " + ItemDefinitions
									.getItemDefinitions(ingot.getProducts()[productIndex]).getName().toLowerCase()
									+ ".", true);
					if (productIndex == currentInstructions) {
						xp *= 1.1;
						player.getPackets()
								.sendGameMessage("You receive +10% xp bonus for following suak instructions.", true);
					}
				} else {
					for (int itemId : track.getRequiredItems())
						player.getInventory().deleteItem(itemId, 1);
					player.getInventory().addItem(track.getItemId(), 1);
					xp = track.getXp();
					player.getPackets().sendGameMessage("You smith the bar into a "
							+ ItemDefinitions.getItemDefinitions(track.getItemId()).getName().toLowerCase() + ".",
							true);
				}
				player.getSkills().addXp(Skills.SMITHING, xp);
				hits = ingot != null ? 3 : 1;
			}
			if (ticks > 0) {
				return 4;
			}
			return -1;
		}

		@Override
		public void stop(Player player) {
			this.setActionDelay(player, 3);
		}

	}

}