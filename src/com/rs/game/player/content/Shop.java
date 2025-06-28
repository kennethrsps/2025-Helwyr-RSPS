package com.rs.game.player.content;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.QuestManager.Quests;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.clans.content.perks.ClanPerk;
import com.rs.utils.Colors;
import com.rs.utils.ItemExamines;
import com.rs.utils.ItemSetsKeyGenerator;
import com.rs.utils.Logger;
import com.rs.utils.Utils;
/*
import mysql.impl.NewsManager;*/
//Starfire Shop

public class Shop {

	private static final int MAIN_STOCK_ITEMS_KEY = ItemSetsKeyGenerator.generateKey();

	private static final int MAX_SHOP_ITEMS = 40;
	public static final int COINS = 995;

	private String name;
	private Item[] mainStock;
	private int[] defaultQuantity;
	private Item[] generalStock;
	private int money;
	private int amount;

	private CopyOnWriteArrayList<Player> viewingPlayers;

	public static int[][] pkShop = { { 13896, 5000 }, { 13884, 5000 }, { 13890, 5000 }, { 13887, 5000 },
			{ 13893, 5000 }, { 13876, 5000 }, { 13870, 5000 }, { 13873, 5000 }, { 13864, 5000 }, { 13858, 5000 },
			{ 13861, 5000 }, { 13899, 2500 }, { 13905, 2500 }, { 13902, 2500 }, { 13867, 2500 }, { 13879, 2500 },
			{ 13883, 2500 }, { 11846, 3500 }, { 11848, 3500 }, { 11850, 3500 }, { 11852, 3500 }, { 11854, 3500 },
			{ 11856, 3500 }, { 21768, 3500 }, { 23680, 5000 }, { 23682, 5000 }, { 23679, 7500 }, { 23681, 7500 } };

	public static int[][] Starfire = { { 35588, 6000 }, { 35589, 7000 }, { 35590, 6500 }, { 35593, 6500 },
			{ 35594, 7500 }, { 35595, 6800 }, { 35598, 6000 }, { 35599, 6500 }, { 35600, 6500 } };

	public static int[][] reaperShop = { { 31875, 500 }, { 31872, 550 }, { 31869, 350 }, { 31878, 450 }, { 11789, 300 },
			{ 1419, 300 }, { 27996, 1000 }, { 31846, 0 }, { 31851, 350 }, { 39501, 750 }, { 39922, 100 },
			{ 39921, 250 } };

	public static int[][] ZombieMinigame = { { 24154, 1500 }, { 34409, 5550 }, { 34411, 5350 }, { 34413, 4450 },
			{ 34415, 3300 }, { 34417, 3300 }, { 7592, 4500 }, { 7593, 4500 }, { 7595, 4100 }, { 7596, 4800 } };
	public static int[][] FightCaves = { { 26128, 15000 }, { 26132, 15000 }, { 26130, 15000 }, { 26140, 10000 },
			{ 26136, 9000 }, { 26124, 5000 }, { 26126, 5000 }, { 6523, 8000 }, { 6524, 7000 }, { 6525, 8000 },
			{ 6526, 7000 }, { 6528, 7000 }, { 6527, 7000 } };

	public static int[][] PrestigeShop = { { 21472, 15 }, { 21473, 20 }, { 21474, 20 }, { 21475, 2 }, { 21476, 3 },
			{ 21467, 10 }, { 21468, 15 }, { 21469, 15 }, { 21470, 2 }, { 21471, 3 }, { 21462, 10 }, { 21463, 15 },
			{ 21464, 15 }, { 21465, 2 }, { 21466, 3 }, { 14295, 25 }, { 14305, 20 }, { 14385, 20 }, { 14315, 25 },
			{ 24186, 20 }, { 34200, 7 }, { 34201, 10 }, { 34202, 9 }, { 34203, 2 }, { 34204, 3 }, { 31580, 7 },
			{ 31581, 10 }, { 31582, 9 }, { 31583, 2 }, { 31584, 3 } };
	public static int[][] PrestigeShop2 = { { 32347, 7 }, { 32348, 10 }, { 32349, 9 }, { 32350, 3 }, { 32351, 2 } };
	public static int[][] PvmShop1 = { { 34836, 150 }, { 29441, 150 }, { 29442, 250 }, { 17273, 550 }, { 28566, 350 },
			{ 35886, 350 }, { 24154, 250 }, { 4503, 500 }, { 4504, 500 }, { 4505, 500 }, { 4506, 500 }, { 4507, 500 },
			{ 10499, 1000 }, { 24954, 300 } };

	public static int[][] PvmShop2 = { { 11663, 4000 }, { 11664, 4000 }, { 11665, 4000 }, { 8839, 6000 },
			{ 8840, 6000 }, { 8842, 3000 }, { 25430, 15000 }, { 32622, 4 }, { 989, 500 }, { 6746, 7500 },
			{ 13663, 50000 }, { 25202, 2000 }, { 40191, 6000 } };

	public static int[][] PvmShop3 = { { 11780, 150 }, { 27360, 150 }, { 10330, 15000 }, { 10332, 18000 },
			{ 10334, 17000 }, { 10336, 15000 }, { 10338, 12000 }, { 10340, 15000 }, { 10342, 13500 }, { 10344, 12000 },
			{ 10346, 15000 }, { 10348, 15500 }, { 10350, 15250 }, { 10352, 13250 } };

	public static int[][] dungShop = { { 36164, 100000 }, { 25991, 250000 }, { 25993, 250000 }, { 16955, 1000000 },
			{ 16403, 1000000 }, { 27663, 1200000 }, { 27913, 1200000 }, { 16337, 1000000 }, { 17017, 1000000 },
			{ 10025, 12500 }, { 25995, 250000 }, { 31445, 50000 }, { 36164, 100000 }, { 31463, 250000 },
			{ 27069, 180000 }, { 27071, 160000 } };

	public static int[][] voteShop = {

			{ 25430, 40 }, { 34233, 25 },
			/** First tower **/
			{ 26107, 19 }, { 26108, 19 }, { 26119, 19 }, { 6665, 5 }, { 6666, 5 }, { 6199, 7 },

			/** Barrows box sets **/
			{ 11846, 45 }, { 11848, 45 }, { 11850, 45 }, { 11852, 45 }, { 11854, 45 }, { 11856, 45 }, { 21768, 45 },

			/** Clue Scroll dyes **/
			{ 33294, 160 }, { 33296, 160 }, { 33298, 160 }, { 36274, 160 }, { 41887, 160 },

			/** Ringmaster outfit **/
			// { 13672, 15 }, { 13673, 30 }, { 13674, 12 }, { 13675, 8 },

			/** Darklight C. Key D.Bone Kit **/
			{ 6746, 14 }, { 989, 3 }, { 24352, 6 }, { 34889, 10 },

			/** Imbued Rings **/
			{ 15018, 45 }, { 15019, 45 }, { 15020, 25 }, { 15220, 80 },

			/** Relic Helm **/
			{ 28355, 200 }, { 28358, 200 }, { 28353, 200 },

			/** Rare cosmetic tokens and new box **/ // circus ticket for bank
			{ 34027, 29 }, { 35886, 19 }, { 3188, 25 }, { 13663, 180 } };

	public static int[][] HweenShop = { { 11789, 60 }, { 39569, 100 }, { 24154, 3 }, { 34409, 30 }, { 34411, 30 },
			{ 34413, 30 }, { 34415, 30 }, { 34417, 30 }, { 7592, 30 }, { 7593, 30 }, { 7595, 30 }, { 7596, 30 },
			{ 6106, 5 }, { 6107, 10 }, { 6109, 10 }, { 6110, 5 }, { 6111, 8 } };

	public static int[][] SilverShop = { { 4151, 2 }, { 8839, 3 }, { 8842, 2 }, { 8840, 2 }, { 11665, 2 }, { 11663, 2 },
			{ 11664, 2 }, { 989, 2 }, { 6585, 3 }, { 11694, 7 }, { 11696, 5 }, { 11700, 5 }, { 11698, 5 }, { 6739, 3 },
			{ 15259, 3 }, };

	public static int[][] GoldShop = { { 19785, 4 }, { 19786, 4 }, { 25025, 2 }, { 25022, 3 }, { 11724, 6 },
			{ 11726, 5 }, { 25019, 3 }, { 25013, 2 }, { 25016, 2 }, { 11728, 2 }, { 11718, 3 }, { 11720, 5 },
			{ 11722, 4 }, { 25010, 2 }, };
	public static int[][] PlatinumShop = { { 1038, 21 }, { 1040, 20 }, { 1042, 21 }, { 1044, 20 }, { 1046, 20 },
			{ 1048, 24 }, { 1050, 15 }, { 1053, 10 }, { 1055, 10 }, { 1057, 10 }, { 39448, 10 }, { 39252, 10 },
			{ 35892, 10 }, { 35889, 10 }, { 34478, 10 }, { 34196, 10 }, { 31025, 10 }, { 44239, 10 }, };

	public static int[][] auraShops = { { 22889, 200 }, { 22895, 200 },
			// Tier 1
			{ 22897, 200 }, { 20966, 200 }, { 22905, 200 }, { 22891, 200 }, { 22294, 200 }, { 23848, 200 },
			{ 22296, 200 }, { 22280, 200 }, { 22300, 200 }, { 20958, 200 }, { 22284, 200 }, { 22893, 200 },
			{ 22292, 200 }, { 20965, 200 }, { 20962, 200 }, { 22899, 200 }, { 20967, 200 }, { 20964, 200 },
			{ 22927, 200 }, { 22298, 200 }, { 30784, 200 },
			// Tier 2
			{ 22268, 400 }, { 22270, 400 }, { 22274, 400 }, { 22276, 400 }, { 22278, 400 }, { 22282, 400 },
			{ 22286, 400 }, { 22290, 400 }, { 22885, 400 }, { 22901, 400 }, { 22907, 400 }, { 22929, 400 },
			{ 23842, 400 }, { 23850, 400 }, { 22302, 400 }, { 22272, 400 }, { 30786, 400 },
			// Tier 3
			{ 22887, 800 }, { 22903, 800 }, { 22909, 800 }, { 22911, 800 }, { 22917, 800 }, { 22919, 800 },
			{ 22921, 800 }, { 22923, 800 }, { 22925, 800 }, { 22931, 800 }, { 22933, 800 }, { 23844, 800 },
			{ 23852, 800 }, { 22913, 800 }, { 22915, 800 }, { 30788, 800 },
			// Tier 4
			{ 23854, 1300 }, { 23856, 1300 }, { 23858, 1300 }, { 23860, 1300 }, { 23862, 1300 }, { 23864, 1300 },
			{ 23866, 1300 }, { 23868, 1300 }, { 23870, 1300 }, { 23872, 1300 }, { 23874, 1300 }, { 23876, 1300 },
			{ 23878, 1300 }, { 30790, 1300 },
			// Tier 5
			{ 30792, 2600 }, { 30794, 2600 }, { 30796, 2600 }, { 30798, 2600 }, { 30800, 2600 }, { 30802, 2600 },
			{ 30804, 2600 },
			// cosmetics
			{ 33655, 6000 }, { 33853, 6000 }, { 34123, 5000 }, { 34124, 5000 }, { 34125, 5000 }, { 34126, 5500 },
			{ 34129, 5000 }, { 34130, 5000 }, { 34131, 5000 }, { 34132, 5500 }, };

	public static int[][] triviaShop = { { 15027, 8 }, { 15028, 15 }, { 15029, 12 }, { 15031, 5 }, { 15032, 5 },
			{ 15033, 16 }, { 15034, 30 }, { 15035, 24 }, { 15037, 10 }, { 15038, 10 }, { 5554, 10 }, { 5553, 15 },
			{ 5555, 12 }, { 5556, 5 }, { 5557, 5 }, { 18744, 50 }, { 18745, 50 }, { 18746, 50 }, { 18747, 50 },
			{ 32228, 100 }, { 32240, 85 }, { 1763, 3 }, { 1765, 3 }, { 1767, 3 }, { 29760, 60 }, { 29761, 60 },
			{ 15043, 5 }, { 15044, 5 }, // sliske masks lord marshal boots &
										// gloves
			{ 15039, 10 }, { 15040, 15 }, { 15041, 12 }, { 15042, 12 }, // lord
																		// marshall
																		// armor
																		// and
																		// hat
			{ 28140, 30 }, { 9005, 40 }, { 15598, 75 }, // cake hat, fancy
														// boots, caitlin staff
			{ 15602, 45 }, { 15600, 90 }, { 15604, 70 }, // white trivia dye
															// items
			{ 15620, 25 }, { 15618, 50 }, { 15622, 35 }, // dark infinity items
			{ 32210, 100 }, { 36164, 125 } // crystal staff
	};

	public static int[][] raresShop = { { 23675, 3 }, { 23676, 3 }, { 23677, 3 }, { 23678, 3 }, { 28020, 3 }, // squeal
																												// horns
			{ 37130, 4 }, { 34007, 5 }, { 34008, 5 }, { 34036, 5 }, // chic
																	// scarf,
																	// horns,
																	// halo,
																	// sunglasses
			{ 33733, 7 }, { 33734, 7 }, { 33735, 7 }, // snowboards
			{ 20929, 7 }, { 24474, 7 }, { 37131, 10 }, // katana, boogie bow,
														// coin
			{ 34006, 10 }, { 962, 10 }, { 34028, 15 }

	};
	public static int[][] damageShop = { { 24950, 10000 }, { 11335, 2000000 }, { 25758, 3000000 }, { 14479, 5000000 },
			{ 27360, 1000000 }, { 21472, 4000000 }, { 11780, 1000000 }, { 21473, 7000000 }, { 21474, 5000000 },
			{ 21475, 2000000 }, { 21476, 1000000 }, { 10346, 17000000 }, { 10348, 17000000 }, { 10350, 5000000 },
			{ 10352, 10000000 }, { 10330, 12000000 }, { 10332, 12000000 }, { 10334, 2000000 }, { 10336, 5000000 },
			{ 10342, 5000000 }, { 10338, 17000000 }, { 10340, 17000000 }, { 10344, 8000000 } };

	public static int[][] xmasShop = {
			/** Santa costume */
			{ 14595, 400 }, { 14602, 150 }, { 14605, 150 }, { 14603, 400 }, { 14596, 250 },

			/** Reindeer and Yo-yo */
			{ 10507, 150 }, { 4079, 450 },

			/** Christmas ghostly outfit */
			{ 15422, 200 }, { 15423, 250 }, { 15425, 250 },

			/** Penguin outfit */
			{ 36082, 200 }, { 36084, 200 }, { 36085, 200 }, { 36086, 150 }, { 36087, 150 },

	};
	public static int[][] SlayerShop = {

			{ 30656, 650 }, { 30686, 650 }, { 30716, 650 }, { 40377, 650 }, { 40312, 500 }, { 40316, 500 },
			{ 36066, 150 }, { 37125, 750 }, { 37126, 700 } };

	public static int[][] TuskenShop = { { 35159, 200 }, { 35165, 600 }, { 35167, 450 }, { 35161, 100 }, { 35163, 100 },
			{ 35174, 200 }, { 35180, 600 }, { 35182, 450 }, { 35176, 100 }, { 35178, 100 }, { 35189, 200 },
			{ 35195, 600 }, { 35197, 450 }, { 35191, 100 }, { 35193, 100 } };

	public static int[][] EliteDungeonShop = { { 43164, 18000 }, { 43165, 18000 }, { 43375, 200000 },
			{ 28095, 1800000 }, { 28099, 1800000 }, { 28103, 1800000 }

	};

	public Shop(String name, int money, Item[] mainStock, boolean isGeneralStore) {
		viewingPlayers = new CopyOnWriteArrayList<Player>();
		this.name = name;
		this.money = money;
		this.mainStock = mainStock;
		defaultQuantity = new int[mainStock.length];
		for (int i = 0; i < defaultQuantity.length; i++)
			defaultQuantity[i] = mainStock[i].getAmount();
		if (isGeneralStore && mainStock.length < MAX_SHOP_ITEMS)
			generalStock = new Item[MAX_SHOP_ITEMS - mainStock.length];
	}

	private boolean addItem(int itemId, int quantity) {
		for (Item item : mainStock) {
			if (item.getId() == itemId) {
				item.setAmount(item.getAmount() + quantity);
				refreshShop();
				return true;
			}
		}
		if (generalStock != null) {
			for (Item item : generalStock) {
				if (item == null)
					continue;
				if (item.getId() == itemId) {
					item.setAmount(item.getAmount() + quantity);
					refreshShop();
					return true;
				}
			}
			for (int i = 0; i < generalStock.length; i++) {
				if (generalStock[i] == null) {
					generalStock[i] = new Item(itemId, quantity);
					refreshShop();
					return true;
				}
			}
		}
		return false;
	}

	public void addPlayer(final Player player) {
		viewingPlayers.add(player);
		player.getTemporaryAttributtes().put("Shop", this);
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				viewingPlayers.remove(player);
				player.getTemporaryAttributtes().remove("Shop");
			}
		});
		player.getPackets().sendConfig(118, MAIN_STOCK_ITEMS_KEY);
		sendStore(player);
		player.getInterfaceManager().sendInterface(1265);
		player.getPackets().sendGlobalConfig(1876, -1);
		player.getPackets().sendConfig(1496, -1);
		player.getPackets().sendConfig(532, 995);
		player.getPackets().sendIComponentSettings(1265, 20, 0, getStoreSize() * 6, 1150);
		player.getPackets().sendIComponentSettings(1265, 26, 0, getStoreSize() * 6, 82903066);
		player.getPackets().sendIComponentText(1265, 85, name);
		if (isGeneralStore())
			player.getPackets().sendHideIComponent(1265, 52, false);
		sendInventory(player);
		sendExtraConfigs(player);
	}

	public void buy(Player player, int slotId, int quantity) {
		if (slotId >= getStoreSize())
			return;
		Item item = slotId >= mainStock.length ? generalStock[slotId - mainStock.length] : mainStock[slotId];
		if (item == null)
			return;
		if (slotId >= mainStock.length) {
			if (!player.canTrade(null))
				return;
		}
		if (item.getAmount() == 0) {
			player.getPackets().sendGameMessage("There is no stock of that item at the moment.");
			return;
		}
		int price = item.getDefinitions().getValue();
		ClansManager manager = player.getClanManager();
		if (!(manager == null)) {
			if (player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.BARTERING)) {
				price = (int) (item.getDefinitions().getValue() / 1.50);
			}
		}
		if (price < 1)
			price = 1;
		HashMap<Integer, Integer> requiriments = item.getDefinitions().getWearingSkillRequiriments();
		int amountCoins = player.getInventory().getItems().getNumberOf(money);
		int maxQuantity = amountCoins / price;
		int buyQ = item.getAmount() > quantity ? quantity : item.getAmount();
		boolean hasRequiriments = true;
		if (item.getName().contains("cape (t)") || item.getName().contains(" master cape")
				|| item.getName().equalsIgnoreCase("Dungeoneering cape")) {
			int skill = 0, level = 99;
			for (int skillId : requiriments.keySet()) {
				if (skillId > 24 || skillId < 0)
					continue;
				level = requiriments.get(skillId);
				skill = skillId;
				if (player.getSkills().getLevelForXp(skillId) < requiriments.get(skillId))
					hasRequiriments = false;
			}
			if (!hasRequiriments) {
				player.sendMessage("You need " + Utils.getAorAn(item.getName()) + " "
						+ player.getSkills().getSkillName(skill) + " of " + level + " to buy "
						+ Utils.getAorAn(item.getName()) + " " + item.getName() + ".");
				return;
			}
		}
		if (item.getName().equalsIgnoreCase("blue cape") || item.getName().equalsIgnoreCase("red cape")) {
			if (!player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM)) {
				player.sendMessage("You need to complete Nomad's Requiem to buy a " + item.getName() + ".");
				return;
			}
		}
		if (money != 995) {
			if (!player.getInventory().hasFreeSlots()) {
				player.sendMessage("You do not have enough inventory space to buy this.");
				return;
			}
			for (int i11 = 0; i11 < dungShop.length; i11++) {
				if (item.getId() == dungShop[i11][0] && name.contains("Dungeoneering")) {
					if (player.getDungeoneeringTokens() < dungShop[i11][1] * quantity) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(dungShop[i11][1])
								+ "</col> dungeoneering tokens to buy " + Colors.red + Utils.getAorAn(item.getName())
								+ " " + item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col> from the " + name + ".");
						player.getPackets().sendMusicEffect(28);
						player.getInventory().addItem(dungShop[i11][0], 1);
						player.getDungManager().addTokens(-dungShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < ZombieMinigame.length; i11++) {
				if (item.getId() == ZombieMinigame[i11][0] && name.contains("ZombieMinigame")) {
					if (player.getZombiesMinigamePoints() < ZombieMinigame[i11][1] * quantity) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(ZombieMinigame[i11][1])
								+ "</col> Zombie Points to buy " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col> from the " + name + ".");
						player.getPackets().sendMusicEffect(28);
						player.getInventory().addItem(ZombieMinigame[i11][0], 1);
						player.setZombiesMinigamePoints(player.getZombiesMinigamePoints() - ZombieMinigame[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}

			for (int i11 = 0; i11 < PrestigeShop.length; i11++) {
				if (item.getId() == PrestigeShop[i11][0] && name.contains("PrestigeShop")) {
					if (player.prestigePoints < PrestigeShop[i11][1] * quantity) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(PrestigeShop[i11][1])
								+ "</col> Prestige Points to buy " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col> from the " + name + ".");
						player.getPackets().sendMusicEffect(28);
						player.getInventory().addItem(PrestigeShop[i11][0], 1);
						player.prestigePoints -= PrestigeShop[i11][1];
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < PrestigeShop2.length; i11++) {
				if (item.getId() == PrestigeShop2[i11][0] && name.contains("PrestigeShop2")) {
					if (player.prestigePoints < PrestigeShop2[i11][1] * quantity) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(PrestigeShop2[i11][1])
								+ "</col> Prestige Points to buy " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col> from the " + name + ".");
						player.getPackets().sendMusicEffect(28);
						player.getInventory().addItem(PrestigeShop2[i11][0], 1);
						player.prestigePoints -= PrestigeShop2[i11][1];
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < PvmShop1.length; i11++) {
				if (item.getId() == PvmShop1[i11][0] && name.contains("PvmShop1")) {
					if (player.getPVMPoints() < PvmShop1[i11][1] * quantity) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(PvmShop1[i11][1])
								+ "</col> PvM Points to buy " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col> from the " + name + ".");
						player.getPackets().sendMusicEffect(28);
						player.getInventory().addItem(PvmShop1[i11][0], 1);
						player.setPVMPoints(player.getPVMPoints() - PvmShop1[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < PvmShop2.length; i11++) {
				if (item.getId() == PvmShop2[i11][0] && name.contains("PvmShop2")) {
					if (player.getPVMPoints() < PvmShop2[i11][1] * quantity) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(PvmShop2[i11][1])
								+ "</col> PvM Points to buy " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col> from the " + name + ".");
						player.getPackets().sendMusicEffect(28);
						player.getInventory().addItem(PvmShop2[i11][0], 1);
						player.setPVMPoints(player.getPVMPoints() - PvmShop2[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < PvmShop3.length; i11++) {
				if (item.getId() == PvmShop3[i11][0] && name.contains("PvmShop3")) {
					if (player.getPVMPoints() < PvmShop3[i11][1] * quantity) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(PvmShop3[i11][1])
								+ "</col> PvM Points to buy " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col> from the " + name + ".");
						player.getPackets().sendMusicEffect(28);
						player.getInventory().addItem(PvmShop3[i11][0], 1);
						player.setPVMPoints(player.getPVMPoints() - PvmShop3[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < voteShop.length; i11++) {
				if (item.getId() == voteShop[i11][0] && name.contains("Vote")) {
					if (player.getVotePoints() < voteShop[i11][1] * quantity) {
						player.sendMessage("You need " + Utils.getFormattedNumber(voteShop[i11][1]) + " Vote Point"
								+ (voteShop[i11][1] == 1 ? "" : "s") + " to buy " + Colors.red
								+ Utils.getAorAn(item.getName()) + " " + item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getDefinitions().getName() + "</col> from the " + name + ".");
						player.getInventory().addItem(voteShop[i11][0], 1);
						player.setVotePoints(player.getVotePoints() - voteShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < HweenShop.length; i11++) {
				if (item.getId() == HweenShop[i11][0] && name.contains("HweenShop")) {
					if (player.getHweenPoints() < HweenShop[i11][1] * quantity) {
						player.sendMessage("You need " + Utils.getFormattedNumber(HweenShop[i11][1])
								+ " Halloween Point" + (HweenShop[i11][1] == 1 ? "" : "s") + " to buy " + Colors.red
								+ Utils.getAorAn(item.getName()) + " " + item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getDefinitions().getName() + "</col> from the " + name + ".");
						player.getInventory().addItem(HweenShop[i11][0], 1);
						player.setHweenPoints(player.getHweenPoints() - HweenShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < pkShop.length; i11++) {
				if (item.getId() == pkShop[i11][0] && name.contains("PK Point Shop")) {
					if (player.getPkPoints() < pkShop[i11][1] * quantity) {
						player.sendMessage("You need " + Utils.getFormattedNumber(pkShop[i11][1]) + " PK Point"
								+ (pkShop[i11][1] == 1 ? "" : "s") + " to buy " + Colors.red
								+ Utils.getAorAn(item.getName()) + " " + item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getDefinitions().getName() + "</col> from the " + name + ".");
						player.getInventory().addItem(pkShop[i11][0], 1);
						player.setPkPoints(player.getPkPoints() - pkShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < xmasShop.length; i11++) {
				if (item.getId() == xmasShop[i11][0] && name.contains("Christmas")) {
					if (player.getXmas().snowEnergy < xmasShop[i11][1] * quantity) {
						player.sendMessage("You need " + Utils.getFormattedNumber(xmasShop[i11][1]) + " snow energy"
								+ (pkShop[i11][1] == 1 ? "" : "s") + " to buy " + Colors.red
								+ Utils.getAorAn(item.getName()) + " " + item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getDefinitions().getName() + "</col> from the " + name + ".");
						player.getInventory().addItem(xmasShop[i11][0], 1);
						player.getXmas().snowEnergy = player.getXmas().snowEnergy - xmasShop[i11][1];
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}

			for (int i11 = 0; i11 < SlayerShop.length; i11++) {
				if (item.getId() == SlayerShop[i11][0] && name.contains("SlayerShop")) {
					if (player.getSlayerManager().getSlayerPoints2() < SlayerShop[i11][1] * quantity) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(SlayerShop[i11][1])
								+ "</col> Special Slayer points to buy " + Colors.red + Utils.getAorAn(item.getName())
								+ " " + item.getName() + "</col>, you "
								+ ((player.getSlayerManager().getSlayerPoints2() == 0) ? "have 0."
										: "only have: " + Colors.red
												+ Utils.getFormattedNumber(player.getSlayerManager().getSlayerPoints2())
												+ "</col>."));
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + " " + "</col>from the Slayer shop!");
						player.getInventory().addItem(SlayerShop[i11][0], 1);
						player.getSlayerManager()
								.setPoints2(player.getSlayerManager().getSlayerPoints2() - SlayerShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			/**
			 * Tusken invasion shop
			 */
			for (int i11 = 0; i11 < TuskenShop.length; i11++) {
				if (item.getId() == TuskenShop[i11][0] && name.contains("TuskenShop")) {
					if (player.getTuskenPoints() < TuskenShop[i11][1] * quantity) {
						player.sendMessage("You need " + Utils.getFormattedNumber(TuskenShop[i11][1]) + " Tusken Point"
								+ (TuskenShop[i11][1] == 1 ? "" : "s") + " to buy " + Colors.red
								+ Utils.getAorAn(item.getName()) + " " + item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getDefinitions().getName() + "</col> from the " + name + ".");
						player.getInventory().addItem(TuskenShop[i11][0], 1);
						player.setTuskenPoints(player.getTuskenPoints() - TuskenShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}

			// tusken invasion shop end

			for (int i11 = 0; i11 < EliteDungeonShop.length; i11++) {
				if (item.getId() == EliteDungeonShop[i11][0] && name.contains("EliteDungeonShop")) {
					if (player.getElitePoints() < EliteDungeonShop[i11][1] * quantity) {
						player.sendMessage("You need " + Utils.getFormattedNumber(EliteDungeonShop[i11][1])
								+ " Elite Dungeon Point" + (EliteDungeonShop[i11][1] == 1 ? "" : "s") + " to buy "
								+ Colors.red + Utils.getAorAn(item.getName()) + " " + item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getDefinitions().getName() + "</col> from the " + name + ".");
						player.getInventory().addItem(EliteDungeonShop[i11][0], 1);
						player.setElitePoints(player.getElitePoints() - EliteDungeonShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < Starfire.length; i11++) {
				if (item.getId() == Starfire[i11][0] && name.contains("Starfire Shop")) {
					if (player.getStarfirePoints() < Starfire[i11][1] * quantity) {
						player.sendMessage("You need " + Utils.getFormattedNumber(Starfire[i11][1]) + " Starfire Point"
								+ (Starfire[i11][1] == 1 ? "" : "s") + " to buy " + Colors.red
								+ Utils.getAorAn(item.getName()) + " " + item.getName() + "</col>.");
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getDefinitions().getName() + "</col> from the " + name + ".");
						player.getInventory().addItem(Starfire[i11][0], 1);
						player.setStarfirePoints(player.getStarfirePoints() - Starfire[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}

			for (int i11 = 0; i11 < auraShops.length; i11++) {
				if (item.getId() == auraShops[i11][0] && name.contains("Aura")) {
					if (player.getLoyaltyPoints() < auraShops[i11][1] * quantity) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(auraShops[i11][1])
								+ "</col> loyalty points to buy " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col>, you "
								+ ((player.getLoyaltyPoints() == 0) ? "have 0."
										: "only have: " + Colors.red
												+ Utils.getFormattedNumber(player.getLoyaltyPoints()) + "</col>."));
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + " " + "</col>from the Aura shop!");
						player.getInventory().addItem(auraShops[i11][0], 1);
						player.setLoyaltyPoints(player.getLoyaltyPoints() - auraShops[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < FightCaves.length; i11++) {
				if (item.getId() == FightCaves[i11][0] && name.contains("FightCaves")) {
					if (player.getInventory().getAmountOf(6529) < FightCaves[i11][1]) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(FightCaves[i11][1])
								+ " Tokkul </col> to buy " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + "</col>, " + "you "
								+ ((player.getInventory().getAmountOf(6529) == 0) ? "have 0."
										: "only have: " + Colors.red
												+ Utils.getFormattedNumber(player.getInventory().getAmountOf(6529))
												+ "</col>."));
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + " " + "</col>from the FightCaves!");
						player.getInventory().addItem(FightCaves[i11][0], 1);
						player.getInventory().deleteItem(6529, FightCaves[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < SilverShop.length; i11++) {
				if (item.getId() == SilverShop[i11][0] && name.contains("POG Shop 1")) {
					if (player.getInventory().getAmountOf(41397) < SilverShop[i11][1]) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(SilverShop[i11][1])
								+ " 	Donator tokens</col> to buy " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + "</col>, " + "you "
								+ ((player.getInventory().getAmountOf(41397) == 0) ? "have 0."
										: "only have: " + Colors.red
												+ Utils.getFormattedNumber(player.getInventory().getAmountOf(41397))
												+ "</col>."));
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + " " + "</col>from the Silvershop!");
						player.getInventory().addItem(SilverShop[i11][0], 1);
						player.getInventory().deleteItem(41397, SilverShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < GoldShop.length; i11++) {
				if (item.getId() == GoldShop[i11][0] && name.contains("POG Shop 2")) {
					if (player.getInventory().getAmountOf(41397) < GoldShop[i11][1]) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(GoldShop[i11][1])
								+ " 	Donator tokens</col> to buy " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + "</col>, " + "you "
								+ ((player.getInventory().getAmountOf(41397) == 0) ? "have 0."
										: "only have: " + Colors.red
												+ Utils.getFormattedNumber(player.getInventory().getAmountOf(41397))
												+ "</col>."));
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + " " + "</col>from the GoldShop!");
						player.getInventory().addItem(GoldShop[i11][0], 1);
						player.getInventory().deleteItem(41397, GoldShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < PlatinumShop.length; i11++) {
				if (item.getId() == PlatinumShop[i11][0] && name.contains("Platinum")) {
					if (player.getInventory().getAmountOf(41397) < PlatinumShop[i11][1]) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(PlatinumShop[i11][1])
								+ " 	Donator tokens</col> to buy " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + "</col>, " + "you "
								+ ((player.getInventory().getAmountOf(41397) == 0) ? "have 0."
										: "only have: " + Colors.red
												+ Utils.getFormattedNumber(player.getInventory().getAmountOf(41397))
												+ "</col>."));
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + " " + "</col>from the PlatinumShop!");
						player.getInventory().addItem(PlatinumShop[i11][0], 1);
						player.getInventory().deleteItem(41397, PlatinumShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}

			for (int i11 = 0; i11 < triviaShop.length; i11++) {
				if (item.getId() == triviaShop[i11][0] && name.contains("Trivia")) {
					if (player.getTriviaPoints() < triviaShop[i11][1] * quantity) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(triviaShop[i11][1])
								+ "</col> trivia points to buy " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col>, " + "you "
								+ ((player.getTriviaPoints() == 0) ? "have 0."
										: "only have: " + Colors.red
												+ Utils.getFormattedNumber(player.getTriviaPoints()) + "</col>."));
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + " " + "</col>from the Trivia shop!");
						player.getInventory().addItem(triviaShop[i11][0], 1);
						player.setTriviaPoints(player.getTriviaPoints() - triviaShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < raresShop.length; i11++) {
				if (item.getId() == raresShop[i11][0] && name.contains("Rare")) {
					if (player.getInventory().getAmountOf(34027) < raresShop[i11][1]) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(raresShop[i11][1])
								+ " 	rare item tokens</col> to buy " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + "</col>, " + "you "
								+ ((player.getInventory().getAmountOf(34027) == 0) ? "have 0."
										: "only have: " + Colors.red
												+ Utils.getFormattedNumber(player.getInventory().getAmountOf(34027))
												+ "</col>."));
						return;
					} else {
						ItemDefinitions itemDef = new ItemDefinitions(item.getId());
						World.sendWorldMessage(
								Colors.cyan + "<shad=000000><img=6>News: " + player.getDisplayName()
										+ " has received a " + itemDef.getName() + " from the Rare cosmetic shop!",
								false);
						/*
						 * new Thread(new NewsManager(player,
						 * "<b><img src=\"./bin/images/news/rare.png\" width=15> " +
						 * player.getDisplayName() + " has received a " + itemDef.getName() +
						 * " from the Rare cosmetic shop!")) .start();
						 */
						player.getInventory().deleteItem(34027, raresShop[i11][1]);
						player.getInventory().addItem(raresShop[i11][0], 1);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < reaperShop.length; i11++) {
				if (item.getId() == reaperShop[i11][0] && name.contains("Soul")) {
					if (player.getReaperPoints() < reaperShop[i11][1] * quantity) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(reaperShop[i11][1])
								+ "</col> reaper points to buy " + Colors.red + Utils.getAorAn(item.getName()) + " "
								+ item.getName() + "</col>, " + "you "
								+ ((player.getReaperPoints() == 0) ? "have 0."
										: "only have: " + Colors.red
												+ Utils.getFormattedNumber(player.getReaperPoints()) + "</col>."));
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + " " + "</col>from the reaper shop!");
						player.getInventory().addItem(reaperShop[i11][0], 1);
						player.setReaperPoints(player.getReaperPoints() - reaperShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
			for (int i11 = 0; i11 < damageShop.length; i11++) {
				if (item.getId() == damageShop[i11][0] && name.contains("Damage")) {
					if (player.getInventory().getAmountOf(13652) < damageShop[i11][1]) {
						player.sendMessage("You need: " + Colors.red + Utils.getFormattedNumber(damageShop[i11][1])
								+ " 	Damage item tokens</col> to buy " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + "</col>, " + "you "
								+ ((player.getInventory().getAmountOf(13652) == 0) ? "have 0."
										: "only have: " + Colors.red
												+ Utils.getFormattedNumber(player.getInventory().getAmountOf(13652))
												+ "</col>."));
						return;
					} else {
						player.sendMessage("You have bought " + Colors.red + Utils.getAorAn(item.getName())
								+ item.getName() + " " + "</col>from the Damage shop!");
						player.getInventory().addItem(damageShop[i11][0], 1);
						player.getInventory().deleteItem(13652, damageShop[i11][1]);
						player.getPackets().sendMusicEffect(28);
						item.setAmount(item.getAmount() - 1);
						refreshShop();
						sendInventory(player);
						return;
					}
				}
			}
		}
		if (price <= 0) {
			player.getPackets().sendGameMessage("This item has no shop value, please report this to an Administrator.");
			return;
		}
		if (!player.hasMoney(quantity)) {
			player.getPackets().sendGameMessage("You don't have enough coins.");
			buyQ = maxQuantity;
			return;
		}
		if (quantity > buyQ) {
			quantity = buyQ;
		}
		if (item.getDefinitions().isStackable()) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
				return;
			}
		} else {
			int freeSlots = player.getInventory().getFreeSlots();
			if (buyQ > freeSlots) {
				buyQ = freeSlots;
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
			}
		}
		if (buyQ != 0) {
			int totalPrice = price * buyQ;
			if (amountCoins + price > 0) {
				if (money == 995) {
					if (player.hasMoney(totalPrice)) {
						player.takeMoney(totalPrice);
						player.getInventory().addItem(item.getId(), buyQ);
					} else {
						player.getPackets().sendGameMessage("You can't afford to buy that many");
						return;
					}
				} else if (money != 995) {
					if (player.getInventory().containsItem(money, totalPrice)) {
						player.getInventory().deleteItem(money, totalPrice);
						player.getInventory().addItem(item.getId(), buyQ);
					} else {
						player.sendMessage("You don't have enough "
								+ ItemDefinitions.getItemDefinitions(money).getName().toLowerCase() + ".");
						return;
					}
				}
			}
			item.setAmount(item.getAmount() - buyQ);
			if (item.getAmount() <= 0 && slotId >= mainStock.length)
				generalStock[slotId - mainStock.length] = null;
			refreshShop();
			sendInventory(player);
		}
	}

	public int getAmount() {
		return this.amount;
	}

	public int getDefaultQuantity(int itemId) {
		for (int i = 0; i < mainStock.length; i++)
			if (mainStock[i].getId() == itemId)
				return defaultQuantity[i];
		return -1;
	}

	public Item[] getMainStock() {
		return this.mainStock;
	}

	public int getStoreSize() {
		return mainStock.length + (generalStock != null ? generalStock.length : 0);
	}

	/**
	 * Checks if the player is buying an item or selling it.
	 * 
	 * @param player The player
	 * @param slotId The slot id
	 * @param amount The amount
	 */
	public void handleShop(Player player, int slotId, int amount) {
		boolean isBuying = player.getTemporaryAttributtes().get("shop_buying") != null;
		if (isBuying)
			buy(player, slotId, amount);
		else
			sell(player, slotId, amount);
	}

	public boolean isGeneralStore() {
		return generalStock != null;
	}

	public void refreshShop() {
		for (Player player : viewingPlayers) {
			sendStore(player);
			player.getPackets().sendIComponentSettings(620, 25, 0, getStoreSize() * 6, 1150);
		}
	}

	public void restoreItems() {
		boolean needRefresh = false;
		for (int i = 0; i < mainStock.length; i++) {
			if (mainStock[i].getAmount() < defaultQuantity[i]) {
				mainStock[i].setAmount(mainStock[i].getAmount() + 1);
				needRefresh = true;
			} else if (mainStock[i].getAmount() > defaultQuantity[i]) {
				mainStock[i].setAmount(mainStock[i].getAmount() + -1);
				needRefresh = true;
			}
		}
		if (generalStock != null) {
			for (int i = 0; i < generalStock.length; i++) {
				Item item = generalStock[i];
				if (item == null)
					continue;
				generalStock[i] = null;
				needRefresh = true;
			}
		}
		if (needRefresh)
			refreshShop();
	}

	public void sell(Player player, int slotId, int quantity) {
		if (player.getInventory().getItemsContainerSize() < slotId)
			return;
		Item item = player.getInventory().getItem(slotId);
		if (item == null)
			return;
		int originalId = item.getId();
		if (ItemConstants.getItemDefaultCharges(item.getId()) != -1 || !ItemConstants.isTradeable(item)
				|| item.getId() == money) {
			player.getPackets().sendGameMessage("You can't sell this item.");
			return;
		}
		int dq = getDefaultQuantity(item.getId());
		if (dq == -1 && generalStock == null) {
			player.sendMessage("You can't sell this item to this shop.");
			return;
		}
		if (money != 995) {
			player.sendMessage("You can't sell items to this shop.");
			return;
		}
		ClansManager manager = player.getClanManager();
		int price = item.getDefinitions().getValue();
		if (!(manager == null)) {
			if (player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.BARTERING)) {
				price = (int) (item.getDefinitions().getTipitPrice() / 3.3);
			}
		}
		if (price < 1)
			price = 1;
		if (item.getDefinitions().isNoted()) {
			Item newItem = new Item(item.getDefinitions().getCertId());
			price = newItem.getDefinitions().getValue();
		}
		int numberOff = player.getInventory().getItems().getNumberOf(originalId);
		if (quantity > numberOff)
			quantity = numberOff;
		if (numberOff + quantity >= Integer.MAX_VALUE || numberOff + quantity < 0) {
			player.sendMessage("The shop would go over max if you sold all these items.");
			return;
		}
		int numberOffCash = player.getInventory().getItems().getNumberOf(995);
		if (numberOffCash + (price * quantity) < 0 && player.getMoneyPouch().getTotal() + price * quantity < 0) {
			player.sendMessage("You can't hold anymore coins.");
			return;
		}
		if (!addItem(item.getId(), quantity)) {
			player.getPackets().sendGameMessage("Shop is currently full.");
			return;
		}
		if (!(player.getMoneyPouch().getTotal() + price * quantity < 0)) {
			player.getMoneyPouch().addMoneyMisc(price * quantity);
		} else if (!(numberOffCash + price * quantity < 0)) {
			player.getInventory().addItem(money, price * quantity);
		}
		player.getInventory().deleteItem(originalId, quantity);
		refreshShop();
	}

	public void sendExamine(Player player, int slotId) {
		if (slotId >= getStoreSize())
			return;
		Item item = slotId >= mainStock.length ? generalStock[slotId - mainStock.length] : mainStock[slotId];
		if (item == null)
			return;
		player.getPackets().sendGameMessage(ItemExamines.getGEExamine(item));
	}

	public void sendExtraConfigs(Player player) {
		player.getPackets().sendConfig(2561, -1);
		player.getPackets().sendConfig(2562, -1);
		player.getPackets().sendConfig(2563, -1);
		player.getPackets().sendConfig(2565, 0);
		player.getTemporaryAttributtes().put("shop_buying", true);
		setAmount(player, 1);
	}

	public void sendInfo(Player player, int slotId, boolean isBuying) {
		if (isBuying) {
			if (slotId >= getStoreSize())
				return;
			Item item = slotId >= mainStock.length ? generalStock[slotId - mainStock.length] : mainStock[slotId];
			if (item == null)
				return;
			if (Settings.DEBUG)
				Logger.log("ButtonHandler", "Item ID: " + item.getId() + ".");
			int price = item.getDefinitions().getValue();
			player.getTemporaryAttributtes().put("ShopSelectedSlot", slotId);
			player.getPackets().sendConfig(2561, MAIN_STOCK_ITEMS_KEY);
			player.getPackets().sendConfig(2562, item.getId());
			player.getPackets().sendConfig(2563, item.getId() == 995 ? -1 : slotId);
			player.getPackets().sendGlobalConfig(1876, ItemDefinitions.getEquipType(item.getName()));
			player.getPackets().sendConfig(2564, 1);
			player.getPackets().sendIComponentText(1265, 40, ItemExamines.getGEExamine(item));
			player.getPackets().sendIComponentText(1265, 205, "" + price);
			for (int i = 0; i < dungShop.length; i++) {
				if (item.getId() == dungShop[i][0] && !isGeneralStore() && name.contains("Dungeoneering")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(dungShop[i][1]) + "</col> " + "dungeoneering tokens, you have "
							+ Colors.red + Utils.getFormattedNumber(player.getDungManager().getTokens()) + "</col>.");
					player.getPackets().sendIComponentText(1265, 205, "" + dungShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < ZombieMinigame.length; i++) {
				if (item.getId() == ZombieMinigame[i][0] && !isGeneralStore() && name.contains("ZombieMinigame")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(ZombieMinigame[i][1]) + "</col> " + "Zombie Points, you have "
							+ Colors.red + Utils.getFormattedNumber(player.getZombiesMinigamePoints()) + "</col>.");
					player.getPackets().sendIComponentText(1265, 205, "" + ZombieMinigame[i][1]);
					return;
				}
			}
			for (int i = 0; i < PrestigeShop.length; i++) {
				if (item.getId() == PrestigeShop[i][0] && !isGeneralStore() && name.contains("PrestigeShop")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(PrestigeShop[i][1]) + "</col> " + "Prestige Points, you have "
							+ Colors.red + Utils.getFormattedNumber(player.prestigePoints) + "</col>.");
					player.getPackets().sendIComponentText(1265, 205, "" + PrestigeShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < PrestigeShop2.length; i++) {
				if (item.getId() == PrestigeShop2[i][0] && !isGeneralStore() && name.contains("PrestigeShop2")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(PrestigeShop2[i][1]) + "</col> " + "Prestige Points, you have "
							+ Colors.red + Utils.getFormattedNumber(player.prestigePoints) + "</col>.");
					player.getPackets().sendIComponentText(1265, 205, "" + PrestigeShop2[i][1]);
					return;
				}
			}
			for (int i = 0; i < PvmShop1.length; i++) {
				if (item.getId() == PvmShop1[i][0] && !isGeneralStore() && name.contains("PvmShop1")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(PvmShop1[i][1]) + "</col> " + "PvM Points, you have "
							+ Colors.red + Utils.getFormattedNumber(player.getPVMPoints()) + "</col>.");
					player.getPackets().sendIComponentText(1265, 205, "" + PvmShop1[i][1]);
					return;
				}
			}
			for (int i = 0; i < PvmShop2.length; i++) {
				if (item.getId() == PvmShop2[i][0] && !isGeneralStore() && name.contains("PvmShop2")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(PvmShop2[i][1]) + "</col> " + "PvM Points, you have "
							+ Colors.red + Utils.getFormattedNumber(player.getPVMPoints()) + "</col>.");
					player.getPackets().sendIComponentText(1265, 205, "" + PvmShop2[i][1]);
					return;
				}
			}
			for (int i = 0; i < PvmShop3.length; i++) {
				if (item.getId() == PvmShop3[i][0] && !isGeneralStore() && name.contains("PvmShop3")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(PvmShop3[i][1]) + "</col> " + "PvM Points, you have "
							+ Colors.red + Utils.getFormattedNumber(player.getPVMPoints()) + "</col>.");
					player.getPackets().sendIComponentText(1265, 205, "" + PvmShop3[i][1]);
					return;
				}
			}
			for (int i = 0; i < reaperShop.length; i++) {
				if (item.getId() == reaperShop[i][0] && !isGeneralStore() && name.contains("Soul")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(reaperShop[i][1]) + "</col> " + "reaper points, you have "
							+ Colors.red + Utils.getFormattedNumber(player.getReaperPoints()) + "</col>.");
					player.getPackets().sendIComponentText(1265, 205, "" + reaperShop[i][1]);
					return;
				}
			}

			for (int i = 0; i < pkShop.length; i++) {
				if (item.getId() == pkShop[i][0] && !isGeneralStore() && name.contains("PK Point Shop")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(pkShop[i][1]) + "</col> " + "Pk Points, you have " + Colors.red
							+ Utils.getFormattedNumber(player.getPkPoints()) + "</col>.");
					player.getPackets().sendIComponentText(1265, 205, "" + pkShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < xmasShop.length; i++) {
				if (item.getId() == xmasShop[i][0] && !isGeneralStore() && name.contains("Christmas")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(xmasShop[i][1]) + "</col> " + "snow energy, you have "
							+ Colors.red + Utils.getFormattedNumber(player.getXmas().snowEnergy) + "</col>.");
					player.getPackets().sendIComponentText(1265, 205, "" + xmasShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < voteShop.length; i++) {
				if (item.getId() == voteShop[i][0] && !isGeneralStore() && name.contains("Vote")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(voteShop[i][1]) + " " + "</col>Vote point"
							+ (voteShop[i][1] == 1 ? "" : "s") + ".");
					player.getPackets().sendConfig(2564, voteShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < HweenShop.length; i++) {
				if (item.getId() == HweenShop[i][0] && !isGeneralStore() && name.contains("HweenShop")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(HweenShop[i][1]) + " " + "</col>Halloween point"
							+ (HweenShop[i][1] == 1 ? "" : "s") + ".");
					player.getPackets().sendConfig(2564, HweenShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < SlayerShop.length; i++) {
				if (item.getId() == SlayerShop[i][0] && !isGeneralStore() && name.contains("SlayerShop")) {
					player.sendMessage(
							Colors.red + item.getDefinitions().getName() + "</col> Slayer costs " + Colors.red
									+ Utils.getFormattedNumber(SlayerShop[i][1]) + " </col>Special Slayer points.");
					player.getPackets().sendIComponentText(1265, 205, "" + SlayerShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < TuskenShop.length; i++) {
				if (item.getId() == TuskenShop[i][0] && !isGeneralStore() && name.contains("TuskenShop")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> Tusken points "
							+ Colors.red + Utils.getFormattedNumber(TuskenShop[i][1]) + " </col>Tusken points.");
					player.getPackets().sendIComponentText(1265, 205, "" + TuskenShop[i][1]);
					return;
				}
			}

			for (int i = 0; i < EliteDungeonShop.length; i++) {
				if (item.getId() == EliteDungeonShop[i][0] && !isGeneralStore() && name.contains("EliteDungeonShop")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> Elite Dungeon points "
							+ Colors.red + Utils.getFormattedNumber(EliteDungeonShop[i][1])
							+ " </col>Elite Dungeon points.");
					player.getPackets().sendIComponentText(1265, 205, "" + EliteDungeonShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < Starfire.length; i++) {
				if (item.getId() == Starfire[i][0] && !isGeneralStore() && name.contains("Starfire Shop")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> Starfire Points "
							+ Colors.red + Utils.getFormattedNumber(Starfire[i][1]) + " </col>Starfire points.");
					player.getPackets().sendIComponentText(1265, 205, "" + Starfire[i][1]);
					return;
				}
			}

			for (int i = 0; i < auraShops.length; i++) {
				if (item.getId() == auraShops[i][0] && !isGeneralStore() && name.contains("Aura")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> aura costs " + Colors.red
							+ Utils.getFormattedNumber(auraShops[i][1]) + " </col>Loyalty points.");
					player.getPackets().sendIComponentText(1265, 205, "" + auraShops[i][1]);
					return;
				}
			}
			for (int i = 0; i < triviaShop.length; i++) {
				if (item.getId() == triviaShop[i][0] && !isGeneralStore() && name.contains("Trivia")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(triviaShop[i][1]) + " </col>trivia points.");
					player.getPackets().sendIComponentText(1265, 205, "" + triviaShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < raresShop.length; i++) {
				if (item.getId() == raresShop[i][0] && !isGeneralStore() && name.contains("Rare")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(raresShop[i][1]) + " </col>rare item tokens.");
					player.getPackets().sendIComponentText(1265, 205, "" + raresShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < SilverShop.length; i++) {
				if (item.getId() == SilverShop[i][0] && !isGeneralStore() && name.contains("POG Shop 1")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(SilverShop[i][1]) + " </col>Donator tokens.");
					player.getPackets().sendIComponentText(1265, 205, "" + SilverShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < FightCaves.length; i++) {
				if (item.getId() == FightCaves[i][0] && !isGeneralStore() && name.contains("FightCaves")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(FightCaves[i][1]) + " </col>Tokkul.");
					player.getPackets().sendIComponentText(1265, 205, "" + FightCaves[i][1]);
					return;
				}
			}
			for (int i = 0; i < GoldShop.length; i++) {
				if (item.getId() == GoldShop[i][0] && !isGeneralStore() && name.contains("POG Shop 2")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(GoldShop[i][1]) + " </col>Donator tokens.");
					player.getPackets().sendIComponentText(1265, 205, "" + GoldShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < PlatinumShop.length; i++) {
				if (item.getId() == PlatinumShop[i][0] && !isGeneralStore() && name.contains("Platinum")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(PlatinumShop[i][1]) + " </col>Donator tokens.");
					player.getPackets().sendIComponentText(1265, 205, "" + PlatinumShop[i][1]);
					return;
				}
			}
			for (int i = 0; i < damageShop.length; i++) {
				if (item.getId() == damageShop[i][0] && !isGeneralStore() && name.contains("Damage")) {
					player.sendMessage(Colors.red + item.getDefinitions().getName() + "</col> costs " + Colors.red
							+ Utils.getFormattedNumber(damageShop[i][1]) + " </col>Damage tokens.");
					player.getPackets().sendIComponentText(1265, 205, "" + damageShop[i][1]);
					return;
				}
			}
			if (isGeneralStore()) {
				player.getPackets().sendHideIComponent(1265, 52, false);
			}
			player.getPackets()
					.sendGameMessage(item.getDefinitions().getName() + ": shop will " + (isBuying ? "sell" : "buy")
							+ " for " + Utils.getFormattedNumber(price) + " "
							+ ItemDefinitions.getItemDefinitions(money).getName().toLowerCase() + ".");
		} else if (!isBuying) {
			Item item = player.getInventory().getItem(slotId);
			if (item == null)
				return;
			int price = item.getDefinitions().getValue();
			player.getTemporaryAttributtes().put("SellSelectedSlot", slotId);
			player.getPackets().sendConfig(2561, MAIN_STOCK_ITEMS_KEY);
			player.getPackets().sendConfig(2562, item.getId());
			player.getPackets().sendConfig(2563, item.getId() == 995 ? -1 : slotId);
			player.getPackets().sendConfig(1496, item.getId());
			player.getPackets().sendGlobalConfig(1876, ItemDefinitions.getEquipType(item.getName().toLowerCase()));
			player.getPackets().sendConfig(2564, 1);
			player.getPackets().sendIComponentText(1265, 40, ItemExamines.getGEExamine(item));
			player.getPackets()
					.sendGameMessage(item.getDefinitions().getName() + ": shop will " + (isBuying ? "sell" : "buy")
							+ " for " + Utils.getFormattedNumber(price) + " "
							+ ItemDefinitions.getItemDefinitions(money).getName().toLowerCase() + ".");
		}
	}

	public void sendInventory(Player player) {
		player.getInterfaceManager().sendInventoryInterface(1266);
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().sendUnlockIComponentOptionSlots(1266, 0, 0, 28, 0, 1, 2, 3, 4, 5);
		player.getPackets().sendInterSetItemsOptionsScript(1266, 0, 93, 4, 7, "Value", "Sell 1", "Sell 5", "Sell 10",
				"Sell 500", "Examine");
	}

	public void sendSellStore(Player player, Item[] inventory) {
		Item[] stock = new Item[inventory.length + (generalStock != null ? generalStock.length : 0)];
		System.arraycopy(inventory, 0, stock, 0, inventory.length);
		if (generalStock != null)
			System.arraycopy(generalStock, 0, stock, inventory.length, generalStock.length);
		player.getPackets().sendItems(MAIN_STOCK_ITEMS_KEY, stock);
	}

	public void sendStore(Player player) {
		Item[] stock = new Item[mainStock.length + (generalStock != null ? generalStock.length : 0)];
		System.arraycopy(mainStock, 0, stock, 0, mainStock.length);
		if (generalStock != null)
			System.arraycopy(generalStock, 0, stock, mainStock.length, generalStock.length);
		player.getPackets().sendItems(MAIN_STOCK_ITEMS_KEY, stock);
	}

	/**
	 * Sends Shop item value.
	 * 
	 * @param player The player to send to.
	 * @param slotId The Shops item slotID.
	 */
	public void sendValue(Player player, int slotId) {
		if (player.getInventory().getItemsContainerSize() < slotId)
			return;
		Item item = player.getInventory().getItem(slotId);
		if (item == null)
			return;
		if (!ItemConstants.isTradeable(item) || item.getId() == money) {
			player.getPackets().sendGameMessage(item.getName() + " cannot be sold to shops.");
			return;
		}
		int dq = getDefaultQuantity(item.getId());
		if (dq == -1 && generalStock == null) {
			player.getPackets().sendGameMessage("You can't sell " + item.getName() + " to this shop.");
			return;
		}
		int price = item.getDefinitions().getValue();
		if (item.getDefinitions().isNoted()) {
			Item newItem = new Item(item.getDefinitions().getCertId());
			price = newItem.getDefinitions().getValue();
		}
		if (money == 995)
			player.sendMessage(item.getDefinitions().getName() + ": shop will buy for: "
					+ Utils.getFormattedNumber(price) + " "
					+ ItemDefinitions.getItemDefinitions(money).getName().toLowerCase() + "; right-click to sell.");

	}

	public void setAmount(Player player, int amount) {
		this.amount = amount;
		player.getPackets().sendIComponentText(1265, 67, String.valueOf(amount));
	}
}