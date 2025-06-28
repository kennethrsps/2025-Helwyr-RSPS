package com.rs.game.player.actions.mining;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.LiquidGoldNymph;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.ancientthrone.Taxation;
import com.rs.game.player.content.clans.content.ClanResources;
import com.rs.game.player.content.clans.content.perks.ClanPerk;
import com.rs.game.player.content.pet.Pets;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public final class Mining extends MiningBase {

	public static enum RockDefinitions {

		SOFT_CLAY(1, 5, 434, 10, 1, 11552, 5, Utils.random(1, 3)), Copper_Ore(1, 17.5, 436, 10, 1, 11552, 5,
				Utils.random(1, 3)), Tin_Ore(1, 17.5, 438, 15, 1, 11552, 5, Utils.random(1, 3)), Iron_Ore(15, 35, 440,
						15, 1, 11552, 10, Utils.random(1, 3)), Sandstone_Ore(35, 30, 6971, 30, 1, 11552, 10,
								Utils.random(1, 3)), Silver_Ore(20, 40, 442, 25, 1, 11552, 20, Utils.random(1,
										3)), Coal_Ore(30, 50, 453, 50, 10, 11552, 30, Utils.random(1, 3)), Granite_Ore(
												45, 50, 6979, 50, 10, 11552, 20, Utils.random(1, 3)), Gold_Ore(40, 60,
														444, 80, 20, 11554, 40, Utils.random(1, 3)), Mithril_Ore(55, 80,
																447, 100, 20, 11552, 60,
																Utils.random(1, 3)), Adamant_Ore(70, 95, 449, 130, 25,
																		11552, 180, Utils.random(1, 3)), Runite_Ore(85,
																				125, 451, 150, 30, 11552, 360,
																				Utils.random(1, 3)), LRC_Coal_Ore(77,
																						50, 453, 50, 10, -1, -1,
																						-1), LRC_Gold_Ore(80, 60, 444,
																								40, 10, -1, -1,
																								-1), Red_Sandstone(81,
																										15, 23194, 41,
																										11, -1, -1,
																										-1), Bane_Ore(
																												77, 90,
																												21778,
																												110, 23,
																												11552,
																												135,
																												2), Donor_Ore(
																														1,
																														-1,
																														-1,
																														80,
																														10,
																														-1,
																														-1,
																														-1),

		GEM_ROCK(40, 60, 1625, 90, 10, 11554, 90, 0),

		CRYSTAL_SANDSTONE(81, 15, 32847, 41, 11, -1, -1, -1),

		SEREN_STONE(89, 296.7, 32262, 150, 25, 92716, 300, 50),

		STONE_CLIPPINGS(35, 2, 15286, 8, 10, -1, 12, 0)

		;

		private int level;
		private double xp;
		private int oreId;
		private int oreBaseTime;
		private int oreRandomTime;
		private int emptySpot;
		private int respawnDelay;
		private int randomLifeProbability;

		private RockDefinitions(int level, double xp, int oreId, int oreBaseTime, int oreRandomTime, int emptySpot,
				int respawnDelay, int randomLifeProbability) {
			this.level = level;
			this.xp = xp;
			this.oreId = oreId;
			this.oreBaseTime = oreBaseTime;
			this.oreRandomTime = oreRandomTime;
			this.emptySpot = emptySpot;
			this.respawnDelay = respawnDelay;
			this.randomLifeProbability = randomLifeProbability;
		}

		public int getEmptyId() {
			return emptySpot;
		}

		public int getLevel() {
			return level;
		}

		public int getOreBaseTime() {
			return oreBaseTime;
		}

		public int getOreId() {
			return oreId;
		}

		public int getOreRandomTime() {
			return oreRandomTime;
		}

		public int getRandomLifeProbability() {
			return randomLifeProbability;
		}

		public int getRespawnDelay() {
			return respawnDelay;
		}

		public double getXp() {
			return xp;
		}
	}

	private WorldObject rock;
	private RockDefinitions definitions;

	private boolean usedDeplateAurora;

	public Mining(WorldObject rock, RockDefinitions definitions) {
		this.rock = rock;
		this.definitions = definitions;
	}

	private void addOre(Player player) {
		double xpBoost = 0;
		int idSome = 0;
		if (definitions == RockDefinitions.Granite_Ore) {
			idSome = Utils.getRandom(2) * 2;
			if (idSome == 2)
				xpBoost += 10;
			else if (idSome == 4)
				xpBoost += 25;
		} else if (definitions == RockDefinitions.Sandstone_Ore) {
			idSome = Utils.getRandom(3) * 2;
			xpBoost += idSome / 2 * 10;
		}
		if (definitions == RockDefinitions.Donor_Ore) {
			int ore = Utils.random(11);
			double xp = 17.5;
			Item item = null;
			Item bar = null;
			String message = "some";
			switch (ore) {
			case 0: /** Clay **/
				item = new Item(434);
				xp = 5;
				break;
			case 1: /** Tin **/
				item = new Item(438);
				bar = new Item(2349);
				break;
			case 2: /** Copper **/
				item = new Item(436);
				bar = new Item(2349);
				break;
			case 3: /** Iron **/
				item = new Item(440);
				bar = new Item(2351);
				xp = 35;
				break;
			case 4: /** Silver **/
				item = new Item(442);
				bar = new Item(2355);
				xp = 40;
				break;
			case 5: /** Coal **/
				item = new Item(453);
				xp = 50;
				break;
			case 6: /** Gold **/
				item = new Item(444);
				bar = new Item(2357);
				xp = 60;
				break;
			case 7: /** Mithril **/
				item = new Item(447);
				bar = new Item(2359);
				xp = 80;
				break;
			case 8: /** Adamant **/
				item = new Item(449);
				bar = new Item(2361);
				xp = 95;
				break;
			case 9: /** Runite **/
				item = new Item(451);
				bar = new Item(2363);
				xp = 125;
				break;
			case 10: /** Gem **/
			case 11: /** Gem **/
				int gem = Utils.random(5);
				switch (gem) {
				case 0:
					item = new Item(1625);
					break;
				case 1:
					item = new Item(1627);
					break;
				case 2:
					item = new Item(1623);
					break;
				case 3:
					item = new Item(1621);
					break;
				case 4:
					item = new Item(1619);
					break;
				case 5:
					item = new Item(1617);
					break;
				}
				xp = 60;
				message = "an";
				break;
			}
			double totalXp = xp * miningSuit(player) * Quarrymasterperk(player);
			player.getSkills().addXp(Skills.MINING, totalXp);
			player.getResourceGather().submitResourceGatherForStats(new Item(definitions.getOreId(), 1), totalXp,
					Skills.MINING, false);
			player.addOresMined();
			player.sendMessage("You mine " + message + " " + item.getName().toLowerCase() + "; ores mined: "
					+ Utils.getFormattedNumber(player.getOresMined()) + ".", true);
			player.getDailyTaskManager().process(item.getId());
			Pets.checkSkillingPet(player, 38101);
			if (bar != null && Utils.random(100) >= 90 && player.getPerkManager().quarryMaster) {
				player.getInventory().addItem(bar);
				player.sendMessage("The " + item.getName().toLowerCase() + " turned into a "
						+ bar.getName().toLowerCase() + " thanks to your Quarrymaster perk.", true);
				player.getSkills().addXp(Skills.SMITHING, Utils.random(0.1, 3.0));
			} else
				player.getInventory().addItem(item);
		}

		double totalXp = (definitions.getXp() + xpBoost) * miningSuit(player);
		player.getSkills().addXp(Skills.MINING, totalXp);
		player.getResourceGather().submitResourceGatherForStats(new Item(definitions.getOreId(), 1), totalXp,
				Skills.MINING, false);
		if (definitions == RockDefinitions.GEM_ROCK) {
			if (rock.getId() == 93024 || rock.getId() == 93025) {
				if (Utils.random(10000) == 0) {
					player.addOresMined();
					player.getInventory().addItem(new Item(6571));
					player.sendMessage("You mine a rare uncut onyx gem; ores mined: " + Colors.red
							+ Utils.getFormattedNumber(player.getOresMined()) + "</col>.", true);
					return;
				}
			}
			int gem = Utils.random(5);
			switch (gem) {
			case 0:
				player.getInventory().addItem(1625, 1);
				break;
			case 1:
				player.getInventory().addItem(1627, 1);
				break;
			case 2:
				player.getInventory().addItem(1623, 1);
				break;
			case 3:
				player.getInventory().addItem(1621, 1);
				break;
			case 4:
				player.getInventory().addItem(1619, 1);
				break;
			case 5:
				player.getInventory().addItem(1617, 1);
				break;
			}
			player.addOresMined();
			player.sendMessage("You mine a gem; ores mined: " + Utils.getFormattedNumber(player.getOresMined()) + ".",
					true);
			return;
		}
		if (definitions.getOreId() != -1) {
			Item bar = null;
			if (Utils.random(100) >= 90) {
				if (definitions == RockDefinitions.Tin_Ore || definitions == RockDefinitions.Copper_Ore)
					bar = new Item(2349);
				if (definitions == RockDefinitions.Iron_Ore)
					bar = new Item(2351);
				if (definitions == RockDefinitions.Silver_Ore)
					bar = new Item(2355);
				if (definitions == RockDefinitions.Gold_Ore || definitions == RockDefinitions.LRC_Gold_Ore)
					bar = new Item(2357);
				if (definitions == RockDefinitions.Mithril_Ore)
					bar = new Item(2359);
				if (definitions == RockDefinitions.Adamant_Ore)
					bar = new Item(2361);
				if (definitions == RockDefinitions.Runite_Ore)
					bar = new Item(2363);
			}
			if (definitions == RockDefinitions.SEREN_STONE) {
				if (player.getSerenStonesMined() < 100) {
					player.addSerenStonesMined();
					player.addOresMined();
					if (player.getSerenStonesMined() == 100)
						player.sendMessage(
								"Congratulations! You've unlocked '<col=FF08A0>of the Trahaearn</col>' Loyalty Title.");
				}
			}
			String oreName = ItemDefinitions.getItemDefinitions(definitions.getOreId() + idSome).getName()
					.toLowerCase();
			player.addOresMined();
			player.sendMessage("You mine some " + oreName + "; ores mined: " + Colors.red
					+ Utils.getFormattedNumber(player.getOresMined()) + "</col>.", true);
			player.getDailyTaskManager().process(definitions.getOreId());
			if (Utils.random(25) == 0) {
				if (player.getClanManager() != null) {
					if (definitions.getOreId() != RockDefinitions.STONE_CLIPPINGS.getOreId()
							|| definitions.getOreId() != 13727) {
						if (player.getClanManager().getClan().getClanPerks()
								.hasPerk(ClanPerk.Perks.TIER_2_INCREASED_RESOURCES)
								&& !player.getClanManager().getClan().getClanResources()
										.hasCaptivity(ClanResources.Captivity.MINING_CAP)) {
							player.getClanManager().getClan().getClanResources().addResources(player,
									ClanResources.MINING, 1);
							player.getClanManager().getClan().getClanBank().addItem(player, definitions.getOreId(), 2,
									true);
							player.getPackets().sendGameMessage("<col=269C0E>" + player.getClanName()
									+ "'s Bank has recieved x2 " + oreName + " as a resource.", true);

						} else if (!player.getClanManager().getClan().getClanResources()
								.hasCaptivity(ClanResources.Captivity.MINING_CAP)) {
							player.getClanManager().getClan().getClanResources().addResources(player,
									ClanResources.MINING, 1);
							player.getClanManager().getClan().getClanBank().addItem(player, definitions.getOreId(), 1,
									true);
							player.getPackets().sendGameMessage("<col=269C0E>" + player.getClanName()
									+ "'s Bank has recieved x1 " + oreName + " as a resource.", true);
						}
					}
				}
			}
			if (bar != null && player.getPerkManager().quarryMaster) {
				player.getInventory().addItem(bar);
				player.sendMessage("The " + oreName + " turned into a " + bar.getName().toLowerCase()
						+ " thanks to your Quarrymaster perk.", true);
				player.getSkills().addXp(Skills.SMITHING, Utils.random(0.1, 3.0));
			} else /*if (Taxation.taxItem(player, definitions.getOreId(), 1)) {*/
				player.getInventory().addItem(definitions.getOreId() + idSome, 1);
		/*	}*/
		}
	}

	private boolean checkAll(Player player) {
		player.closeInterfaces();
		if (!hasPickaxe(player)) {
			player.sendMessage("You need a pickaxe to mine this rock.");
			return false;
		}
		if (!setPickaxe(player)) {
			player.sendMessage("You dont have the required level to use this pickaxe.");
			return false;
		}
		if (!hasMiningLevel(player))
			return false;
		if (!player.getInventory().hasFreeSlots()) {
			player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
			return false;
		}
		return true;
	}

	private boolean checkRock(Player player) {
		return World.containsObjectWithId(rock, rock.getId());
	}

	private int getMiningDelay(Player player) {
		int summoningBonus = 0;
		if (player.getFamiliar() != null) {
			if (player.getFamiliar().getId() == 7342 || player.getFamiliar().getId() == 7342)
				summoningBonus += 10;
			else if (player.getFamiliar().getId() == 6832 || player.getFamiliar().getId() == 6831)
				summoningBonus += 1;
		}
		int mineTimer = definitions.getOreBaseTime() - (player.getSkills().getLevel(Skills.MINING) + summoningBonus)
				- Utils.getRandom(pickaxeTime);
		if (mineTimer < 1 + definitions.getOreRandomTime())
			mineTimer = 1 + Utils.getRandom(definitions.getOreRandomTime());
		mineTimer /= player.getAuraManager().getMiningAccurayMultiplier();
		if (player.getPerkManager().quarryMaster)
			mineTimer /= 1.33;
		if (hasGolemOutfit(player))
			mineTimer /= 1.07;
		return mineTimer;
	}

	private boolean hasMiningLevel(Player player) {
		if (definitions.getLevel() > player.getSkills().getLevel(Skills.MINING)) {
			player.sendMessage("You need a mining level of " + definitions.getLevel() + " to mine this rock.");
			return false;
		}
		return true;
	}

	private double Quarrymasterperk(Player player) {
		double Qxp = 1.0;
		if (player.getPerkManager().quarryMaster)
			Qxp = 1.25;
		return Qxp;
	}

	/**
	 * XP modifier by wearing items.
	 * 
	 * @param player
	 *            The player.
	 * @return the XP modifier.
	 */
	private double miningSuit(Player player) {
		double xpBoost = 1.0;
		if (player.getEquipment().getHatId() == 20789)
			xpBoost *= 1.01;
		if (player.getEquipment().getChestId() == 20791)
			xpBoost *= 1.01;
		if (player.getEquipment().getLegsId() == 20790)
			xpBoost *= 1.01;
		if (player.getEquipment().getBootsId() == 20788)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 20789 && player.getEquipment().getChestId() == 20791
				&& player.getEquipment().getLegsId() == 20790 && player.getEquipment().getBootsId() == 20788)
			xpBoost *= 1.01;
		if (hasGolemOutfit(player))
			xpBoost *= 1.07;
		return xpBoost;
	}

	/**
	 * Checks if the player has any of the 4 golem outfits.
	 * 
	 * @param player
	 *            The player to check.
	 * @return if has the full outfit.
	 */
	private boolean hasGolemOutfit(Player player) {
		if (player.getEquipment().getHatId() == 31575 && player.getEquipment().getChestId() == 31576
				&& player.getEquipment().getLegsId() == 31577 && player.getEquipment().getGlovesId() == 31578
				&& player.getEquipment().getBootsId() == 31579)
			return true;
		if (player.getEquipment().getHatId() == 31580 && player.getEquipment().getChestId() == 31581
				&& player.getEquipment().getLegsId() == 31582 && player.getEquipment().getGlovesId() == 31583
				&& player.getEquipment().getBootsId() == 31584)
			return true;
		if (player.getEquipment().getHatId() == 31585 && player.getEquipment().getChestId() == 31586
				&& player.getEquipment().getLegsId() == 31587 && player.getEquipment().getGlovesId() == 31588
				&& player.getEquipment().getBootsId() == 31589)
			return true;
		if (player.getEquipment().getHatId() == 31590 && player.getEquipment().getChestId() == 31591
				&& player.getEquipment().getLegsId() == 31592 && player.getEquipment().getGlovesId() == 31593
				&& player.getEquipment().getBootsId() == 31594)
			return true;
		return false;
	}

	@Override
	public boolean process(Player player) {
		setAnimationAndGFX(player);
		if (Utils.random(500) == 0) {
			new LiquidGoldNymph(player, player);
			player.sendMessage("<col=ff0000>A Liquid Gold Nymph emerges from the rock.");
			player.getActionManager().forceStop();
		}
		player.faceObject(rock);
		return checkRock(player);
	}

	@Override
	public int processWithDelay(Player player) {
		addOre(player);
		player.getAchManager().addKeyAmount("miner", 1);
		if (definitions.getEmptyId() != -1) {
			if (!usedDeplateAurora && (1 + Math.random()) < player.getAuraManager().getChanceNotDepleteMN_WC()) {
				usedDeplateAurora = true;
			} else if (Utils.getRandom(definitions.getRandomLifeProbability()) == 0) {
				World.spawnObjectTemporary(new WorldObject(definitions.getEmptyId(), rock.getType(), rock.getRotation(),
						rock.getX(), rock.getY(), rock.getPlane()), definitions.respawnDelay * 600);
				player.setNextAnimation(new Animation(-1));
				return -1;
			}
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.setNextAnimation(new Animation(-1));
			player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
			return -1;
		}
		return getMiningDelay(player);
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		player.sendMessage("You swing your pickaxe at the rock..", true);
		setActionDelay(player, getMiningDelay(player));
		return true;
	}
}