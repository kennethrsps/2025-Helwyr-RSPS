package com.rs;

import java.util.Calendar;

import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;

/**
 * A class used to store all server-wide/in-game configurations.
 * 
 * @author Zeus
 */
public final class Settings {

	public static boolean doubleDrop;

	/** Server data configuration **/
	public static final String SERVER_NAME = "Helwyr";
	public static final String REPOSITORY = "information/";

	public static final int DROP_RATE = 3;

	/** Message to send on login **/
	public static String LATEST_UPDATE = "Helwyr has been updated , ::UPDATE for more details!";

	/* Server management */
	public static final int REVISION = 718; // revision of the client
	public static final int SUB_REVISION = 1; // client sub-build
	public static final int MINIMUM_RAM_ALLOCATED = 1000000000; // 1000mb
	public static int SERVER_PORT = 43596;// 43594;
	public static int WORLD_ID = 1;

	/** SQL IP connection **/
	public static String WEBHOST_IP = Settings.DEBUG ? "1.1.1.1" : "localhost";
	public static boolean SQL_ENABLED = true;

	/** Player configurations **/
	public static final int START_PLAYER_HITPOINTS = 100;
	// public static final WorldTile START_PLAYER_LOCATION = new WorldTile(2474,
	// 2709, 2);
	// public static final WorldTile RESPAWN_PLAYER_LOCATION = new
	// WorldTile(2495, 2712, 2);
	public static final WorldTile START_PLAYER_LOCATION = new WorldTile(3227, 2725, 0);
	public static final WorldTile RESPAWN_PLAYER_LOCATION = new WorldTile(990, 4122, 0);
	public static final int MAX_STARTER_COUNT = 300;
	public static final int MAX_CONNECTED_SESSIONS_PER_IP = 2;
	// test//

	/** Game configuration **/
	public static final int AIR_GUITAR_MUSICS_COUNT = 250;
	public static final int QUESTS = 183;
	public static final long MAX_PACKETS_DECODER_PING_DELAY = 30000;
	public static final int WORLD_CYCLE_TIME = 600; // recycle 600 milliseconds

	/** Home region **/
	public static int MARKET_REGION_ID = 11324;
	public static int HOME_REGION_ID = 10026;

	/** Instancing the system Calendar **/
	private static Calendar cal = Calendar.getInstance();

	/** Special weekend boosts **/
	public static boolean SLAYER_WEEKEND = cal.get(Calendar.MONTH) == 11
			&& (cal.get(Calendar.DATE) >= 25 && cal.get(Calendar.DATE) <= 27),

			DUNGEONEERING_WEEKEND = cal.get(Calendar.MONTH) == 11
					&& (cal.get(Calendar.DATE) >= 25 && cal.get(Calendar.DATE) <= 27);

	// Website links
	public static String WEBSITE = "https://Helwyr3.org";
	public static String DONATE = "https://app.gpay.io/store/helwyr3server"; // "https://helwyr3.org/forums/index.php?/donate/";
	public static String FORUM = WEBSITE;
	public static String HISCORES = "https://helwyr3.org/forums/index.php?/highscores/";
	public static String VOTE = "https://helwyr3ps.everythingrs.com/services/vote";
	public static String UPDATES = "https://helwyr3.org/forums/index.php?/forum/7-server-updates/";
	public static String DISCORD = "https://discord.gg/9EBQYyTk9F";
	public static String GUIDES = "https://helwyr3.org/forums/index.php?/forum/19-guides/";
	public static String TWITCH = "";
	public static String LIVECODE = "";

	// staffs
	public static String[] OWNER = { "", "zeus" };
	public static String[] DEV = { "yaso", "", "" };
	public static String[] ADMIN = { "", "", ""};
	public static String[] MOD = { "", "", "" };
	public static String[] SUPPORT = { "", "", "", "", "" };
	public static String[] YOUTUBE = { "" };
	public static String[] WIKI = { "", "" };

	// All set using parameters on server launch
	public static boolean ECONOMY_MODE, DEBUG, GUI_MODE, SUPERLOG;

	// XP configuration
	public static int VET_XP = 25, INTERM_XP = 100, EASY_XP = 200, IRONMAN_XP = 10, EXPERT_XP = 5;

	// Drop configuration
	/*
	 * public static double VET_DROP = 3, INTERM_DROP = 2, EASY_DROP = 1,
	 * IRONMAN_DROP = 2, HCIRONMAN_DROP = 3, EXPERT_DROP = 4;
	 */

	public static double VET_DROP = 3, INTERM_DROP = 1.50, EASY_DROP = 1, IRONMAN_DROP = 3.5, HCIRONMAN_DROP = 3.5,
			EXPERT_DROP = 4.5;

	/** Well of Goodwill **/
	public static final int WELL_MAX_AMOUNT = 25_000_000;

	// NPC Settings
	public static final int[] NON_WALKING_NPCS = { 522, 557, 44, 554, 537, 549, 546, 550, 2676, 548, 598, 531, 659,
			2824, 2234, 5913, 4247, 6539, 9400, 19519, 278, 22153, 14381, 10021, 526, 527, 15085, 2465, 587, 12878,
			2998, 7402 };

	public static final int[] FORCE_WALKING_NPCS = { 15309, 18150, 18151, 18153, 18155, 18157, 18159, 18161, 18163,
			18165, 18167, 18169, 18171, 182041, 18540, 18545, 18541, 18544, 18538, 18543, 22891, 21335, 21336, 21337 };

	// Disable or Enable Yell.
	public static boolean serverYell = true;
	public static String yellChangedBy;

	public static boolean yellEnabled() {
		return serverYell;
	}

	// MISC
	public static boolean LENDING_DISABLED = true;

	public static final String[] FORBIDDEN_SOUL_WARS_ITEMS = { "torva", "virtus", "pernix", "divine spirit shield",
			"arcane spirit shield", "spectral spirit shield", "elysian spirit shield", "ganodermic", "knife", "sled",
			"ascension", "zaryte", "sirenic", "noxious", "drygore" };

	public static int ZEAL_MODIFIER = World.isWeekend() ? 3 : 2;

	/**
	 * Checks for drop rates.
	 * 
	 * @param player The player.
	 * @return The rate.
	 */
	public static double getDropQuantityRate(Player player) {
		if (player.isIronMan())
			return IRONMAN_DROP;
		if (player.isHCIronMan())
			return HCIRONMAN_DROP;
		if (player.isVeteran())
			return VET_DROP;
		if (player.isIntermediate())
			return INTERM_DROP;
		if (player.isExpert())
			return EXPERT_DROP;
		if (player.isEasy())
			return EASY_DROP;
		return 1;
	}

	// 35% for uncommon
	// 0.089% for rare
	// 0.09% for jackpot
	public static final double[] SOF_CHANCES = new double[] { 1.0D, 0.52D, 0.0050D, 0.008D };

	public static final int[] SOF_COMMON_CASH_AMOUNTS = new int[] { 150000, 250000, 500000, 1000000, 5000000 };
	public static final int[] SOF_UNCOMMON_CASH_AMOUNTS = new int[] { 500000, 1000000, 1250000, 1500000, 1750000 };
	public static final int[] SOF_RARE_CASH_AMOUNTS = new int[] { 10000000, 20000000, 30000000, 40000000, 50000000 };
	public static final int[] SOF_JACKPOT_CASH_AMOUNTS = new int[] { 50 * 1000000, 100 * 1000000, 200 * 1000000,
			500 * 1000000 };
	public static final int[] SOF_COMMON_LAMPS = new int[] { 23713, 23717, 23721, 23725, 23729, 23737, 23733, 23741,
			23745, 23749, 23753, 23757, 23761, 23765, 23769, 23778, 23774, 23786, 23782, 23794, 23790, 23802, 23798,
			23810, 23806, 23814 };
	public static final int[] SOF_UNCOMMON_LAMPS = new int[] { 23714, 23718, 23722, 23726, 23730, 23738, 23734, 23742,
			23746, 23750, 23754, 23758, 23762, 23766, 23770, 23779, 23775, 23787, 23783, 23795, 23791, 23803, 23799,
			23811, 23807, 23815 };
	public static final int[] SOF_RARE_LAMPS = new int[] { 23715, 23719, 23723, 23727, 23731, 23739, 23735, 23743,
			23747, 23751, 23755, 23759, 23763, 23767, 23771, 23780, 23776, 23788, 23784, 23796, 23792, 23804, 23800,
			23812, 23808, 23816 };
	public static final int[] SOF_JACKPOT_LAMPS = new int[] { 23716, 23720, 23724, 23728, 23732, 23740, 23736, 23744,
			23748, 23752, 23756, 23760, 23764, 23768, 23773, 23781, 23777, 23789, 23785, 23797, 23793, 23805, 23801,
			23813, 23809, 23817 };
	public static final int[] SOF_COMMON_OTHERS = new int[] { 13666, 11732, 11126, 15271, 384, 537, 1359, 1275, 2364,
			23048, 1745, 13103, 2506, 2508, 2510, 29304, 29301, 29305, 29306, 29310, 31080, 31081, 29294, 29295, 29296,
			29300 };
	public static final int[] SOF_UNCOMMON_OTHERS = new int[] { 35968, 35969, 35970, 35971, 35972, 35973, 35974, 35975,
			35976, 35977, 35963, 35964, 35965, 35966, 35967, 35886, 29307, 29312, 31088, 29298, 29299, 29302, 29303,
			31310 };
	public static final int[] SOF_RARE_OTHERS = new int[] { 995, 995, 995, 995, 995, 30372, 31041, 23691, 23684, 23685,
			23686, 23687, 23688, 23689, 23690, 23683, 23692, 23693, 23694, 23675, 23676, 23677, 23678, 23673 };
	public static final int[] SOF_JACKPOT_OTHERS = new int[] { 995, 995, 995, 995, 962, 14484, 23679, 23680, 23681,
			23682, 23697, 23698, 23699, 23700, 20929, 23674, 24433, 26568, 26569, 30368 };

	public static final int LOCAL_NPCS_LIMIT = 250;
	public static final int PACKET_SIZE_LIMIT = 7500;
	public static final int PLAYERS_LIMIT = 2000;
	public static Object[][] PRODUCTS = {
			{ 1000000, 5, "Familiar Expert",
					"Increases your Familiar timer by 50%. Also increases Familiar health by 25%.", 1 },
			{ 2000000, 15, "Charge Befriender",
					"With this perk, your items will never degrade. This perk covers any and all degradable items.",
					1 },
			{ 3000000, 3, "Charm Collector",
					"Charms are automatically sent to your Bank account after killing monsters. Also increases charm drop rate by 25%.",
					1 },
			{ 4000000, 3, "Coin Collector",
					"Coins that are dropped while killing monsters are automatically picked up. Also increases coin drop rate and coin drop amounts by 25%.",
					1 },
			{ 5000000, 10, "Prayer Betrayer",
					"Your Prayer points decrease at a 25% lower rate. Also increases experience gained by 25%.", 1 },
			{ 6000000, 5, "Avas Secret",
					"Acts as a permanently equipped Ava\"s Alerter no matter what cape you\"re wearing. You also have a higher chance of recovering fired ammunition.",
					1 },
			{ 7000000, 6, "Key Expert", "Receive double the loot upon opening the Crystal chest at Home.", 1 },
			{ 8000000, 5, "Dragon Trainer",
					"Acts as a permanently equipped anti-dragon shield. Also increases the chance of receiving a baby pet dragon by 25%.",
					1 },
			{ 9000000, 3, "GWD Specialist	",
					"Removes the requirement of having kill-count to enter any of the 5 Godwars dungeon boss rooms.",
					1 },
			{ 10000000, 10, "Dungeons Master",
					"Reduced kill-count requirement while progressing in Dungeoneering from 15 to 5. Also increases EXP and token ratios by 25%.",
					1 },
			{ 11000000, 3, "Petchanter", "Increases Boss pet drop rates by 25%.", 1 },
			{ 12000000, 10, "Perslaysion",
					"This perk allows to persuade Kuradal to reset Slayer tasks free of charge. Also increases EXP and point ratios by 25% on task completion.",
					1 },
			{ 13000000, 3, "The Pyromaniac",
					"There\"s now a 25% higher chance of receiving Fire spirits, Bonfiring will now grant +15% more experience, and health boost from stoking a Bonfire increased to 200%.",
					1 },
			{ 14000000, 4, "Ports Master",
					"Player-Ports: With this perk your ships will have a 20% increased return rate and you will receive +15% more rewards from successful voyages.",
					1 },
			{ 15000000, 3, "Green Thumb",
					"Increases the chance of Farming plants growing up healthy and increases their yield. Stacks with Magic Secateurs effect.",
					2 },
			{ 16000000, 1, "Bird Man", "Increases the rate of receiving Bird Nest drops while Woodcutting.", 2 },
			{ 17000000, 1, "Unbreakable Forge", "Your Ring of Forging will never deplete {Iron ore smelting}.", 2 },
			{ 18000000, 4, "Sleight of Hand",
					"Sleight of Hand ensures your success rates to 100% in all aspects of Thieving. This includes pick-pocketing and removes damage taken from Home thieving stalls.",
					2 },
			{ 19000000, 8, "Herbivore",
					"All monsters now have the ability to drop a box that contains 10 random grimy herbs. Also increases Herblore experience by 25% and extends potion timers by 200%.",
					2 },
			{ 20000000, 5, "Master Fisherman",
					"This perk allows you at a chance to catch double the fish at once. Exp is given for both fish caught and stacks with Agility\"s multi-catch. Also removes the requirement of having appropriate baits.",
					2 },
			{ 21000000, 3, "Delicate Craftsman",
					"Gain an extra 10% experience whilst spinning on the wheel, 25% extra experience for creating dragonhide armours and removes the requirement of having thread.",
					2 },
			{ 22000000, 4, "Elf Fiend", "Allows instant access to the Prifddinas city.", 2 },
			{ 23000000, 3, "Master Chef",
					"Decreases the rate at which your food burns, increases Cooking EXP gained by 25% and increases the speed of Cooking by 33%.",
					2 },
			{ 24000000, 10, "Master Diviner",
					"Increases the rate at which you receive Chronicle Fragments by 25%, higher chance of receiving enriched memories while harvesting wisps by 25% and increases Divination EXP gained by 25%.",
					2 },
			{ 25000000, 5, "Quarrymaster",
					"Increases the speed of mining ores by 33%, and increases the Mining EXP gained by 25%. Also, has a 5% chance of converting your ore into the bar of the ore you\"re currently mining.",
					2 },
			{ 26000000, 6, "Huntsman",
					"You can now place an extra 2 traps at once {7 traps at level 80+}, gain an additional +25% experience on successful catches, +15% success rate increase AND a 10% chance to receive double loot.",
					2 },
			{ 27000000, 10, "Divine Doubler", "This perk allows you to gather double your divine limit every day!", 2 },
			{ 28000000, 5, "Imbued Focus",
					"This perk allows you to drain the energy from Runespan creatures at a much slower rate! Creatures will stay alive for longer, allowing you to get more XP.",
					2 },
			{ 29000000, 7, "Alchemic Smithing",
					"Grants you the ability to smith bars without coal {adamant bars require 2 adamantite ore, and runite bars require 3 runite ore.The bonus is effectively equal to x = {amount of coal}-2/2, if x < 2, then it will only require one ore.",
					2 },
			{ 30000000, 20, "Bank Command",
					"This game perk allows you to access your Bank Account all across Helwyr via typing ;;bank in the chat-box! Does not work in Mini-games/PvP/Controllers.",
					3 },
			{ 31000000, 5, "Stamina Boost",
					"With this perk, your run energy will never deplete and stay at 100% forever.", 3 },
			{ 32000000, 6, "Overclocked", "Your Aura active times are now doubled. Also halves recharge time.", 3 },
			{ 33000000, 5, "The Mini-Gamer",
					"Doubles the Points received from Mini-game rewards {Warriors Guild, Soul Wars, Pest Control}.",
					3 },
			{ 34000000, 10, "Investigator",
					"This perk allows you to get the reward from any clue after finishing only one step!", 3 },
			{ 35000000, 10, "+1 Bank Container", "Adds an additional container to your bank!", 3 },
			{ 36000000, 25, "+3 Bank Containers", "Adds 3 containers to your bank list!", 3 },
			{ 37000000, 50, "+7 Bank Containers", "Adds 7 Bank containers to your list!", 3 },
			{ 38000000, 10, "Petloot",
					"This will allow your pet to automatically loot all the drops {besides Coins and Charms} and choose either put it in your inventory or bank.",
					3 },
			{ 39000000, 15, "Looters Perk Package",
					"This Perk package contains the following game perks with a small discount: Bird Man; Charm Collector; Coin Collector; Key Expert; Petchanter.",
					4 },
			{ 40000000, 56, "Skillers Perk Package",
					"This Perk package contains ALL of the perks in the - Skilling Perks - category.", 4 },
			{ 41000000, 50, "Utility Perk Package",
					"This Perk package contains the following game perks with a small discount: Bank Command; Stamina Boost; Overclocked; Elf Fiend, The Mini-Gamer.",
					4 },
			{ 42000000, 60, "Combatants Perk Package",
					"This Perk package contains Familiar Exper, Charge befriender , prayer betrayer, avas secret , dragon trainer , gwd specialist , dungeon and perslaysion.",
					4 },
			{ 43000000, 175, "Complete Perk Package",
					"This Perk package contains greenthumb, unbreakable forge, sleightofhand, herbivore, masterfisherman, delicate craftsman, familiar expert, charge befriender, prayer betrayer, avas secret, dragon trainer, gwd specialist, dungeon, perslaysion, birdman, charmcollector, coin collector, keyexpert, petchanter, bankcommand, staminaboost, elffiend, overclocked, master chef, master diviner, minigamer, quarrymaster, huntsman, masterfledger, the piromaniac, portsmaster, divine double and investigator",
					4 },
			{ 44000000, 3, "Infernal Gaze", "This aura allows you to override your eye color to red!", 5 },
			{ 45000000, 3, "Serene Gaze", "This aura allows you to override your eye color to blue!", 5 },
			{ 46000000, 3, "Vernal Gaze", "This aura allows you to override your eye color to green", 5 },
			{ 47000000, 3, "Mystical Gaze", "This aura allows you to override your eye color to purple!", 5 },
			{ 48000000, 3, "Blazing Gaze", "This aura allows you to override your eye color to orange!", 5 },
			{ 49000000, 3, "Nocturnal Gaze", "This aura allows you to override your eye color to yellow!", 5 },
			{ 50000000, 3, "Abyssal Gaze", "This aura allows you to override your eye color to black!", 5 },
			{ 51000000, 1, "Keepsake x 1", "converts a wearable item into a cosmetic override", 5 },
			{ 52000000, 2, "Keepsake x 3", "converts a wearable item into a cosmetic override", 5 },
			{ 53000000, 3, "Keepsake x 7", "converts a wearable item into a cosmetic override", 5 },
			{ 54000000, 5, "Keepsake x 10", "converts a wearable item into a cosmetic override", 5 }
			/*
			 * { 55000000, 2, "Arcane Fishing",
			 * "An animation override that plays while fishing any standard fish.", 6 }, {
			 * 56000000, 2, "Strongarm Burial",
			 * "An animation override that plays while burying bones and offering bones at an altar."
			 * , 6 }, { 57000000, 2, "Arcane Cooking",
			 * "An animation override that plays while cooking using a range or log fire.",
			 * 6 }, { 58000000, 2, "Power Divination",
			 * "An animation override that plays while gathering energies from a location.",
			 * 6 }, { 59000000, 2, "Powerful Conversion",
			 * "An animation override that plays while converting energies at a rift.", 6 },
			 * { 60000000, 2, "Agile Divination",
			 * "An animation override that plays while gathering energies from a location.",
			 * 6 }, { 61000000, 2, "Sinister Slumber",
			 * "An animation override that plays while resting.", 6 }, { 62000000, 2,
			 * "Crystal Impling Resting", "An animation override that plays while resting.",
			 * 6 }, { 63000000, 2, "Headbutt Mining",
			 * "An animation override that plays while mining any standard ore.", 6 }, {
			 * 64000000, 4, "Sandstorm Walk",
			 * "An animation override that plays while walking, running, or idling.", 6 }, {
			 * 65000000, 4, "Proud Walk",
			 * "An animation override that plays while walking, running, or idling.", 6 }, {
			 * 66000000, 4, "Barbarian Walk",
			 * "An animation override that plays while walking, running, or idling.", 6 }, {
			 * 67000000, 4, "Revenant Walk",
			 * "An animation override that plays while walking, running, or idling.", 6 }, {
			 * 68000000, 2, "Slayer Battle Cry",
			 * "An animation that plays after killing an assigned slayer monster.", 6 }, {
			 * 69000000, 2, "Enhanced Potion Making",
			 * "An animation override that plays while brewing potions.", 6 }, { 70000000,
			 * 2, "Lumberjack Woodcutting",
			 * "An animation override that plays while woodcutting any standard tree.", 6 },
			 * { 71000000, 2, "Deep-Sea Fishing",
			 * "An animation override that plays while fishing any standard fish.", 6 }, {
			 * 72000000, 2, "Zen Resting",
			 * "An animation override that plays while resting.", 6 }, { 73000000, 2,
			 * "Karate Chop Fletching",
			 * "An animation override that plays while fletching any logs.", 6 }, {
			 * 74000000, 2, "Iron-Fist Smithing",
			 * "An animation override that plays while smithing with an anvil.", 6 }, {
			 * 75000000, 2, "Chi-Blast Mining",
			 * "An animation override that plays while mining any standard ore.", 6 }, {
			 * 76000000, 2, "Samurai Cooking",
			 * "An animation override that plays while cooking using a range or log fire.",
			 * 6 }, { 77000000, 2, "Roundhouse Woodcutting",
			 * "An animation override that plays while woodcutting any standard tree.", 6 },
			 * { 78000000, 2, "Chi-Blast Mining",
			 * "An animation override that plays while mining any standard ore.", 6 }, {
			 * 79000000, 2, "Arcane Smelting",
			 * "An animation override that plays while smelting ores to bars.", 6 }, {
			 * 80000000, 2, "Arcane Resting",
			 * "An animation override that plays while resting.", 6 }, { 81000000, 2,
			 * "Strongarm Woodcutting",
			 * "An animation override that plays while woodcutting any standard tree.", 6 },
			 * { 82000000, 2, "Strongarm Mining",
			 * "An animation override that plays while mining any standard ore", 6 }, {
			 * 83000000, 4, "Sad Walk",
			 * "An animation override that plays while walking, running, or idling.", 6 }, {
			 * 84000000, 4, "Happy Walk",
			 * "An animation override that plays while walking, running, or idling.", 6 }, {
			 * 85000000, 2, "Agile Conversion",
			 * "An animation override that plays while converting energies at a rift.", 6 },
			 * { 86000000, 2, "Strongarm Resting",
			 * "An animation override that plays while resting.", 6 }, { 87000000, 2,
			 * "Energy Drain Resting", "An animation override that plays while resting.", 6
			 * }, // { 88000000, 2, "Armchair Warrior", "An animation override that plays
			 * while // resting.", 6 }, { 89000000, 2, "SoF Spins x5",
			 * "Purchase x5 Squeal of Fortune spins. Warning: Spins will NOT be received on ironman accounts."
			 * , 7 }, { 90000000, 8, "SoF Spins x25 + 2 FREE",
			 * "Purchase x25 + 2 Squeal of Fortune spins. Warning: Spins will NOT be received on ironman accounts"
			 * , 7 }, { 91000000, 16, "SoF Spins x50 + 5 FREE",
			 * "SoF Spins x50 + 5 FREE. Warning: Spins will NOT be received on ironman accounts."
			 * , 7 }, { 92000000, 40, "SoF Spins x150 + 25 FREE",
			 * "Purchase x150 + 25 Squeal of Fortune spins. Warning: Spins will NOT be received on ironman accounts."
			 * , 7 }, { 93000000, 80, "SoF Spins x300 + 50 FREE",
			 * "Purchase x300 + 50 free Squeal of Fortune spins. Warning: Spins will NOT be received on ironman accounts."
			 * , 7 }, { 94000000, 5, "x5 Pot Of Gold",
			 * "Pot of Gold Use to buy Items,Armors and Weapon in Donator Store..", 8 }, {
			 * 95000000, 10, "x10 Pot Of Gold",
			 * "Pot of Gold Use to buy Items,Armors and Weapon in Donator Store..", 8 }, {
			 * 96000000, 20, "x20 Pot Of Gold",
			 * "Pot of Gold Use to buy Items,Armors and Weapon in Donator Store..", 8 }, {
			 * 97000000, 50, "x50 Pot Of Gold",
			 * "Pot of Gold Use to buy Items,Armors and Weapon in Donator Store..", 8 }, {
			 * 98000000, 15, "Hazelmere Luck", "1/500 to double your drop on 1 kill", 9 }, {
			 * 99000000, 15, "Corruption Blast",
			 * "Have the chance to cast Breath, on of magister spell when using t90+ magic weapon. This spell hit multiple enemy"
			 * , 9 }, { 100000000, 5, "200 Cosmetic Coins",
			 * "Use to Buy Cosmetic Override at Solomon", 9 }, { 100000001, 10,
			 * "500 Cosmetic Coins", "Use to Buy Cosmetic Override at Solomon", 9 }
			 */ };

	public static String[] TABS = { "Combat Perks", "Skilling Perks", "Utility Perks", "Perk Packages",
			"Cosmetics Overrides"/*
									 * , "Animation Overrides", "Squeal Of Fortune", "Pot Of Gold",
									 * "Special/Cosmetic Perk",
									 */ };
	/**
	 * all the bosses that should be timed ALWAYS ADD A NEW BOSS AT THE END OF THE
	 * ARRAY
	 */
	public static final int[] BOSS_IDS = new int[] { // always add the end,
			// because of the
			// bossTimers
			2883, 2882, 2881, // dks
			3200, // chaos
			8133, // Crop
			50, // kbd
			6260, // bandos
			6222, // Arma
			6203, // zamorak
			13450, // nex
			6247, // saradomin
			1160, // kq
			15454, // qbd
			2745, // jad
			15208, // jad
			15211, // kiln
			12878, // blink
			16697, // kalphite king
			19463, // araxxor
			7134, // bork
			5666, // barrelchest
			17182, // vorago
			15581, // party demon
			19553, // darklord
			22891 // Telos

	};
	/*
	 * public static String[] VORAGO_ROTATION_NAMES = { "Ceiling Collapse",
	 * "Scopulus", "Vitalis", "Green Bomb", "Team Split", "The End" };
	 */
	public static String[] VORAGO_ROTATION_NAMES = { "Ceiling Collapse", "Scopulus", "Vitalis", "Green Bomb",
			"Team Split", "The End" };
	public static int VORAGO_ROTATION = 0;
	public static int DAYS_TO_CHANGE_ROTATION = 100;
	public static String VORAGO_RELEASE_DATE = "2017-07-20";

	public static String[][] expBoosts = { { "Regular", "25" }, { "Bronze", "25" }, { "Silver", "27.5" },
			{ "Gold", "30" }, { "Platinum", "32.5" }, { "Diamond", "35" }, { "Master", "40" } };

}
