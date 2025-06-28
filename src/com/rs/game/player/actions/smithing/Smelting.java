package com.rs.game.player.actions.smithing;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.WorldObject;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.DwarvenMinerNPC;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class Smelting extends Action {

	public enum SmeltingBar {

		BRONZE(1, 6.2, new Item[] { new Item(436), new Item(438) }, new Item(2349), 0),

		IRON(15, 12.5, new Item[] { new Item(440) }, new Item(2351), 1),

		SILVER(20, 13.7, new Item[] { new Item(442) }, new Item(2355), 2),

		STEEL(30, 17.5, new Item[] { new Item(440), new Item(453, 2) }, new Item(2353), 3),

		GOLD(40, 22.5, new Item[] { new Item(444) }, new Item(2357), 4),

		MITHRIL(50, 30, new Item[] { new Item(447), new Item(453, 4) }, new Item(2359), 5),

		ADAMANT(70, 37.5, new Item[] { new Item(449), new Item(453, 6) }, new Item(2361), 6),
		
		RUNE(85, 50, new Item[] { new Item(451), new Item(453, 8) }, new Item(2363), 7), 
		
		//OBSIDIAN(80, 50, new Item[] { new Item(26122, 12) }, new Item(26123), 8),
		
		DRAGONBANE(80, 50, new Item[] { new Item(21779) }, new Item(21783, 1), 8),

		CORRUPTED_ORE(89, 150, new Item[] { new Item(32262) }, new Item(7650, 1), 9),   //32262

		
		NOVITE(1, 7, new Item[] { new Item(17630) }, new Item(17650), Skills.SMITHING),

		BATHUS(10, 13.3, new Item[] { new Item(17632) }, new Item(17652), Skills.SMITHING),

		MARMAROS(20, 19.6, new Item[] { new Item(17634) }, new Item(17654), Skills.SMITHING),

		KRATONITE(30, 25.9, new Item[] { new Item(17636) }, new Item(17656), Skills.SMITHING),

		FRACTITE(40, 32.2, new Item[] { new Item(17638) }, new Item(17658), Skills.SMITHING),

		ZEPHYRIUM(50, 38.5, new Item[] { new Item(17640) }, new Item(17660), Skills.SMITHING),

		ARGONITE(60, 44.8, new Item[] { new Item(17642) }, new Item(17662), Skills.SMITHING),

		KATAGON(70, 51.1, new Item[] { new Item(17644) }, new Item(17664), Skills.SMITHING),

		GORGONITE(80, 57.4, new Item[] { new Item(17646) }, new Item(17666), Skills.SMITHING),

		PROMETHIUM(90, 63.7, new Item[] { new Item(17648) }, new Item(17668), Skills.SMITHING),

		CANNON_BALLS(35, 25.6, new Item[] { new Item(2353, 1), new Item(4, 1) }, new Item(2, 4), 10);

		private static Map<Integer, SmeltingBar> bars = new HashMap<Integer, SmeltingBar>();

		static {
			for (SmeltingBar bar : SmeltingBar.values()) {
				bars.put(bar.getButtonId(), bar);
			}
		}

		public static SmeltingBar forId(int buttonId) {
			return bars.get(buttonId);
		}

		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private int buttonId;
		private Item producedBar;

		private SmeltingBar(int levelRequired, double experience, Item[] itemsRequired, Item producedBar,
				int buttonId) {
			this.levelRequired = levelRequired;
			this.experience = experience;
			this.itemsRequired = itemsRequired;
			this.producedBar = producedBar;
			this.buttonId = buttonId;
		}

		public int getButtonId() {
			return buttonId;
		}

		public double getExperience() {
			return experience;
		}

		public Item[] getItemsRequired() {
			return itemsRequired;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public Item getProducedBar() {
			return producedBar;
		}
	}

	public SmeltingBar bar;
	public WorldObject object;
	public int ticks;

	public Smelting(int slotId, WorldObject object, int ticks) {
		this.bar = SmeltingBar.forId(slotId);
		this.object = object;
		this.ticks = ticks;
	}

	public boolean isSuccessFull(Player player) {
		if (bar == SmeltingBar.IRON) {
			if (player.getEquipment().getItem(Equipment.SLOT_RING) != null
					&& player.getEquipment().getItem(Equipment.SLOT_RING).getId() == 2568) {
				return true;
			} else
				return Utils.getRandom(100) <= (player.getSkills().getLevel(Skills.SMITHING) >= 45 ? 80 : 50);
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		if (bar == null || player == null || object == null)
			return false;
		if (!player.getPerkManager().alchemicSmith) {
			if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(),
					bar.getItemsRequired()[0].getAmount())) {
				player.sendMessage("You need " + bar.getItemsRequired()[0].getDefinitions().getName() + " to create a "
						+ bar.getProducedBar().getDefinitions().getName() + ".");
				return false;
			}
			if (bar.getItemsRequired().length > 1) {
				if (!player.getInventory().containsItem(bar.getItemsRequired()[1].getId(),
						bar.getItemsRequired()[1].getAmount())) {
					player.sendMessage("You need " + bar.getItemsRequired()[1].getDefinitions().getName()
							+ " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
					return false;
				}
			}
		} else {
			if (bar.getItemsRequired().length > 1) {
				int alchemAmt = (int) (bar.getItemsRequired()[1].getAmount() - 2) / 2;
				alchemAmt = alchemAmt < 2 ? 1 : alchemAmt;
				if (bar.getItemsRequired()[1].getId() == 438) {
					if (!player.getInventory().containsItem(bar.getItemsRequired()[1].getId(), alchemAmt)
							&& !player.getInventory().containsItem(bar.getItemsRequired()[0].getId(), alchemAmt)) {
						player.sendMessage("You need either 1 tin or copper ore to smith a bronze bar!");
						return false;
					} else
						return true;
				}
				if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(), alchemAmt)
						&& (!player.getInventory().containsItem(438, 1) && bar != SmeltingBar.BRONZE)) {
					player.sendMessage(
							"You need " + alchemAmt + "x " + bar.getItemsRequired()[0].getDefinitions().getName()
									+ " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
					return false;
				}
			}
			if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(),
					bar.getItemsRequired()[0].getAmount())) {
				player.sendMessage("You need " + bar.getItemsRequired()[0].getDefinitions().getName() + " to create a "
						+ bar.getProducedBar().getDefinitions().getName() + ".");
				return false;
			}
		}

		if (player.getSkills().getLevel(Skills.SMITHING) < bar.getLevelRequired()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You need a Smithing level of at least "
					+ bar.getLevelRequired() + " to smelt " + bar.getProducedBar().getDefinitions().getName());
			return false;
		}
		if (Utils.random(500) == 0) {
			new DwarvenMinerNPC(player, player);
			player.sendMessage("<col=ff0000>A Dwarven Miner appears from nowhere.");
		}
		player.faceObject(object);
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		ticks--;
		if (player.getAnimations().hasArcaneSmelt && player.getAnimations().arcaneSmelt) {
			player.setNextAnimation(new Animation(20292));
			player.setNextGraphics(new Graphics(4000));
		} else
			player.setNextAnimation(new Animation(3243));
		player.getSkills().addXp(Skills.SMITHING, getExp(player));
		if (player.getPerkManager().alchemicSmith) {
			int alchemAmt;
			if (bar.getItemsRequired().length > 1) {
				alchemAmt = (int) (bar.getItemsRequired()[1].getAmount() - 2) / 2;
				alchemAmt = alchemAmt < 2 ? 1 : alchemAmt;
			} else
				alchemAmt = 1;
			boolean taken = false;
			if (bar == SmeltingBar.BRONZE) {
				if (player.getInventory().containsItem(436, 1) && player.getInventory().containsItem(438, 1)
						&& !taken) {
					player.getInventory().deleteItem(Utils.random(1) == 0 ? 436 : 438, alchemAmt);
					taken = true;
				}
				if (player.getInventory().containsItem(436, 1) && !player.getInventory().containsItem(438, 1)
						&& !taken) {
					player.getInventory().deleteItem(436, 1);
					taken = true;
				}
				if (player.getInventory().containsItem(438, 1) && !player.getInventory().containsItem(436, 1)
						&& !taken) {
					player.getInventory().deleteItem(438, 1);
					taken = true;
				}
			} else
				player.getInventory().deleteItem(bar.getItemsRequired()[0].getId(), alchemAmt);
		} else {
			for (Item required : bar.getItemsRequired())
				player.getInventory().deleteItem(required.getId(), required.getAmount());
		}
		if (isSuccessFull(player)) {
			if (bar != SmeltingBar.CORRUPTED_ORE) {
				player.addBarsSmelt();
				player.getDailyTaskManager().process(bar.getProducedBar().getId());
				player.getInventory().addItem(bar.getProducedBar());
				if (object.getId() == 97270 && Utils.random(100) > 90) {
					player.sendMessage(Colors.orange + "<shad=000000>You managed to make an extra bar from your "
							+ "ore in this exceptional furnace! It has been sent directly to your bank.");
					player.getDailyTaskManager().process(bar.getProducedBar().getId());
					player.getBank().addItem(bar.getProducedBar().getId(), 1, true);
				}
				player.sendMessage("You retrieve a bar of "
						+ bar.getProducedBar().getDefinitions().getName().toLowerCase().replace(" bar", "") + "; "
						+ "bars smelt: " + Colors.red + Utils.getFormattedNumber(player.getBarsSmelt()) + "</col>.",
						true);
			} else
				player.sendMessage("You've successfully smelt the Corrupted ore.", true);
			if (bar == SmeltingBar.IRON) {
				if (player.getEquipment().getItem(Equipment.SLOT_RING) != null) {
					if (player.getEquipment().getItem(Equipment.SLOT_RING).getId() == 2568) {
						if (player.ironOres >= 140 && !player.getPerkManager().unbreakableForge) {
							player.getEquipment().getItems().remove(Equipment.SLOT_RING, new Item(2568));
							player.ironOres = 0;
							player.sendMessage("You have used up all of your ring of forging charges.");
							player.sendMessage("Purchase the Unbreakable Forge perk to have unlimited charges!", true);
						}
						player.ironOres++;
					}
				}
			}
		} else
			player.sendMessage("The ore is too impure and you fail to refine it.", true);
		if (ticks > 0)
			return 1;
		return -1;
	}

	@Override
	public boolean start(Player player) {
		if (bar == null || player == null || object == null)
			return false;
		if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(),
				bar.getItemsRequired()[0].getAmount())
				&& (player.getPerkManager().alchemicSmith && bar != SmeltingBar.BRONZE)) {
			player.sendMessage("You need " + bar.getItemsRequired()[0].getDefinitions().getName() + " to create a "
					+ bar.getProducedBar().getDefinitions().getName() + ".");
			return false;
		}

		if (player.getPerkManager().alchemicSmith) {
			if (bar.getItemsRequired().length > 1) {
				int alchemAmt = (int) (bar.getItemsRequired()[1].getAmount() - 2) / 2;
				alchemAmt = alchemAmt < 2 ? 1 : alchemAmt;
				if (bar.getItemsRequired()[1].getId() == 438) {
					if (!player.getInventory().containsItem(bar.getItemsRequired()[1].getId(), alchemAmt)
							&& !player.getInventory().containsItem(bar.getItemsRequired()[0].getId(), alchemAmt)) {
						player.sendMessage("You need either 1 tin or copper ore to smith a bronze bar!");
						return false;
					} else
						return true;
				}
				if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(), alchemAmt)
						&& (!player.getInventory().containsItem(438, 1) && bar != SmeltingBar.BRONZE)) {
					player.sendMessage(
							"You need " + alchemAmt + "x " + bar.getItemsRequired()[0].getDefinitions().getName()
									+ " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
					return false;
				}
			}
		}
		if (player.getSkills().getLevel(Skills.SMITHING) < bar.getLevelRequired()) {
			player.sendMessage("You need a Smithing level of at least " + bar.getLevelRequired() + " to smelt "
					+ bar.getProducedBar().getDefinitions().getName());
			return false;
		}
		if (bar != SmeltingBar.CORRUPTED_ORE)
			player.sendMessage("You place the required ore in the furnace and attempt to smelt it.", true);
		else
			player.sendMessage(
					"You place the required ores and attempt to create a bar of "
							+ bar.getProducedBar().getDefinitions().getName().toLowerCase().replace(" bar", "") + ".",
					true);
		return true;
	}

	@Override
	public void stop(Player player) {
		this.setActionDelay(player, 3);
	}

	/**
	 * Calculates how much experience should the player receive.
	 * 
	 * @param player
	 *            The player smelting.
	 * @return the EXP as Double.
	 */
	private double getExp(Player player) {
		double xp = bar.getExperience();
		xp *= blackSmithSuit(player);
		if (object.getId() == 97270)
			xp *= 1.1;
		return xp;
	}

	/**
	 * XP modifier by wearing items.
	 * 
	 * @param player
	 *            The player.
	 * @return the XP modifier.
	 */
	private double blackSmithSuit(Player player) {
		double xpBoost = 1.0;
		if (player.getEquipment().getHatId() == 25195)
			xpBoost *= 1.01;
		if (player.getEquipment().getChestId() == 25196)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 32280)
			xpBoost *= 1.03;
		if (player.getEquipment().getHatId() == 32280 && player.getEquipment().getChestId() == 25196
				&& player.getEquipment().getLegsId() == 25197 && player.getEquipment().getBootsId() == 25198
				&& player.getEquipment().getGlovesId() == 25199)
			xpBoost *= 1.03;
		if (player.getEquipment().getLegsId() == 25197)
			xpBoost *= 1.01;
		if (player.getEquipment().getBootsId() == 25198)
			xpBoost *= 1.01;
		if (player.getEquipment().getGlovesId() == 25199)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 25195 && player.getEquipment().getChestId() == 25196
				&& player.getEquipment().getLegsId() == 25197 && player.getEquipment().getBootsId() == 25198
				&& player.getEquipment().getGlovesId() == 25199)
			xpBoost *= 1.01;
		return xpBoost;
	}
}