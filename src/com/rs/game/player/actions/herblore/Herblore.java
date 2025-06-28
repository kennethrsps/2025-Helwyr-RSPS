package com.rs.game.player.actions.herblore;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.EliNPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.pet.Pets;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class Herblore extends Action {

	public enum Ingredients {

		GUAM(249, new short[] { Herblore.VIAL, Herblore.SWAMP_TAR }, new short[] { 91, 10142 }, new byte[] { 0, 19 },
				new double[] { 0, 30 }),

		MARRENTILL(251, new short[] { Herblore.VIAL, Herblore.SWAMP_TAR }, new short[] { 93, 10143 },
				new byte[] { 0, 31 }, new double[] { 0, 42.5 }),

		TARROMIN(253, new short[] { Herblore.VIAL, Herblore.SWAMP_TAR }, new short[] { 95, 10144 },
				new byte[] { 0, 39 }, new double[] { 0, 55 }),

		HARRALANDER(255, new short[] { Herblore.VIAL, Herblore.SWAMP_TAR }, new short[] { 97, 10145 },
				new byte[] { 0, 44 }, new double[] { 0, 72.5 }),

		RANARR(257, new short[] { Herblore.VIAL }, new short[] { 99 }, new byte[] { 0 }, new double[] { 0 }),

		FELLSTALK(21624, new short[] { Herblore.VIAL }, new short[] { 21628 }, new byte[] { 0 }, new double[] { 0 }),

		TOADFLAX(2998, new short[] { Herblore.VIAL, Herblore.COCONUT_MILK }, new short[] { 3002, 5942 },
				new byte[] { 0, 0 }, new double[] { 0, 0 }),

		SPIRIT_WEED(12172, new short[] { Herblore.VIAL }, new short[] { 12181 }, new byte[] { 0 }, new double[] { 0 }),

		WERGALI(14854, new short[] { Herblore.VIAL }, new short[] { 14856 }, new byte[] { 0 }, new double[] { 0 }),

		IRIT(259, new short[] { Herblore.VIAL, Herblore.COCONUT_MILK }, new short[] { 101, 5951 }, new byte[] { 0, 0 },
				new double[] { 0, 0 }),

		AVANTOE(261, new short[] { Herblore.VIAL, 2436, 145, 147, 149 },
				new short[] { 103, 15308, 15309, 15310, 15311 }, new byte[] { 0, 88, 88, 88, 88 },
				new double[] { 0, 220, 220, 220, 220 }),

		KWUARM(263, new short[] { Herblore.VIAL }, new short[] { 105 }, new byte[] { 0 }, new double[] { 0 }),

		STARFLOWER(9017, new short[] { Herblore.VIAL }, new short[] { 9019 }, new byte[] { 0 }, new double[] { 0 }),

		SNAPDRAGON(3000, new short[] { Herblore.VIAL }, new short[] { 3004 }, new byte[] { 0 }, new double[] { 0 }),

		CADANTINE(265, new short[] { Herblore.VIAL }, new short[] { 107 }, new byte[] { 0 }, new double[] { 0 }),

		LANTADYME(2481, new short[] { Herblore.VIAL, 2442, 163, 165, 167 },
				new short[] { 2483, 15316, 15317, 15318, 15319 }, new byte[] { 0, 90, 90, 90, 90 },
				new double[] { 0, 240, 240, 240, 240 }),

		DWARF_WEED(267, new short[] { Herblore.VIAL, 2440, 157, 159, 161 },
				new short[] { 109, 15312, 15313, 15314, 15315 }, new byte[] { 0, 89, 89, 89, 89 },
				new double[] { 0, 230, 230, 230, 230 }),

		CACTUS_SPINE(6016, new short[] { Herblore.COCONUT_MILK }, new short[] { 5936 }, new byte[] { 0 },
				new double[] { 0 }),

		TORSTOL(269, new short[] { Herblore.VIAL, 15309, 15313, 15317, 15321, 15325 },
				new short[] { 111, 15332, 15332, 15332, 15332, 15332 }, new byte[] { 0, 96, 96, 96, 96, 96 },
				new double[] { 0, 1000, 1000, 1000, 1000, 1000 }),

		CAVE_NIGHTSHADE(2398, new short[] { Herblore.COCONUT_MILK }, new short[] { 5939 }, new byte[] { 0 },
				new double[] { 0 }),

		EYE_OF_NEWT(221, new short[] { 91, 101 }, new short[] { 121, 145 }, new byte[] { 0, 45 },
				new double[] { 25, 100 }),

		UNICORN_HORN_DUST(235, new short[] { 93, 101 }, new short[] { 175, 181 }, new byte[] { 5, 48 },
				new double[] { 37.5, 106.3 }),

		LIMPWURT_ROOT(225, new short[] { 95, 105 }, new short[] { 115, 157 }, new byte[] { 12, 55 },
				new double[] { 50, 125 }),

		RED_SPIDER_EGGS(223, new short[] { 97, 3004, 5936 }, new short[] { 127, 3026, 5937 }, new byte[] { 22, 63, 73 },
				new double[] { 62.5, 142.5, 165 }),

		BLAMISH_SNAIL_SLIME(1581, new short[] { 97 }, new short[] { 1582 }, new byte[] { 25 }, new double[] { 80 }),

		CHOCOLATE_DUST(1975, new short[] { 97 }, new short[] { 3010 }, new byte[] { 26 }, new double[] { 67.5 }),

		WHITE_BERRIES(239, new short[] { 99, 107 }, new short[] { 133, 163 }, new byte[] { 30, 66 },
				new double[] { 75, 150 }),

		RUBIUM(12630, new short[] { 91 }, new short[] { 12633 }, new byte[] { 31 }, new double[] { 55 }),

		TOAD_LEGS(2152, new short[] { 3002 }, new short[] { 3034 }, new byte[] { 34 }, new double[] { 80 }),

		GOAT_HORN_DUST(9736, new short[] { 97 }, new short[] { 9741 }, new byte[] { 36 }, new double[] { 84 }),

		PHARMAKOS_BERRIES(11807, new short[] { 3002 }, new short[] { 11810 }, new byte[] { 37 }, new double[] { 85 }),

		SNAPE_GRASS(231, new short[] { 99, 103 }, new short[] { 139, 151 }, new byte[] { 38, 50 },
				new double[] { 87.5, 112.5 }),

		COCKATRICE_EGG(12109, new short[] { 12181 }, new short[] { 12142 }, new byte[] { 40 }, new double[] { 92 }),

		FROGSPAWN(10961, new short[] { 14856 }, new short[] { 14840 }, new byte[] { 40 }, new double[] { 92 }),

		CHOPPED_ONION(1871, new short[] { 101 }, new short[] { 18661 }, new byte[] { 46 }, new double[] { 0 }),

		MORT_MYRE_FUNGUS(2970, new short[] { 103 }, new short[] { 3018 }, new byte[] { 52 }, new double[] { 117.5 }),

		SHRUNK_OGLEROOT(11205, new short[] { 95 }, new short[] { 11204 }, new byte[] { 52 }, new double[] { 6 }),

		KEBBIT_TEETH_DUST(10111, new short[] { 103 }, new short[] { 10000 }, new byte[] { 53 }, new double[] { 120 }),

		CRUSHED_GORAK_CLAW(9018, new short[] { 9019 }, new short[] { 9022 }, new byte[] { 57 }, new double[] { 130 }),

		WIMPY_FEATHER(11525, new short[] { 14856 }, new short[] { 14848 }, new byte[] { 58 }, new double[] { 132 }),

		DRAGON_SCALE_DUST(241, new short[] { 105, 2483 }, new short[] { 187, 2454 }, new byte[] { 60, 69 },
				new double[] { 137.5, 157.5 }),

		YEW_ROOTS(6049, new short[] { 5942 }, new short[] { 5945 }, new byte[] { 68 }, new double[] { 155 }),

		WINE_OF_ZAMORAK(245, new short[] { 109 }, new short[] { 169 }, new byte[] { 72 }, new double[] { 162.5 }),

		POTATO_CACTUS(3138, new short[] { 2483 }, new short[] { 3042 }, new byte[] { 76 }, new double[] { 172.5 }),

		JANGERBERRIES(247, new short[] { 111 }, new short[] { 189 }, new byte[] { 78 }, new double[] { 175 }),

		MAGIC_ROOTS(6051, new short[] { 5951 }, new short[] { 5954 }, new byte[] { 79 }, new double[] { 177.5 }),

		CRUSHED_BIRD_NEST(6693, new short[] { 3002 }, new short[] { 6687 }, new byte[] { 81 }, new double[] { 180 }),

		POISON_IVY_BERRIES(6018, new short[] { 5939 }, new short[] { 5940 }, new byte[] { 82 }, new double[] { 190 }),

		PAPAYA_FRUIT(5972, new short[] { 3018 }, new short[] { 15301 }, new byte[] { 84 }, new double[] { 200 }),

		PHOENIX_FEATHER(4621, new short[] { 2452, 2454, 2456, 2458 }, new short[] { 15304, 15305, 15306, 15307 },
				new byte[] { 85, 85, 85, 85 }, new double[] { 210, 210, 210, 210 }),

		GROUND_MUD_RUNES(9594, new short[] { 3040, 3042, 3044, 3046 }, new short[] { 15320, 15321, 15322, 15323 },
				new byte[] { 91, 91, 91, 91 }, new double[] { 250, 250, 250, 250 }),

		GRENWALL_SPIKES(12539, new short[] { 2444, 169, 171, 173 }, new short[] { 15324, 15325, 15326, 15327 },
				new byte[] { 92, 92, 92, 92 }, new double[] { 260, 260, 260, 260 }),

		MORCHELLA_MUSHROOM(21622, new short[] { 21628 }, new short[] { 21632 }, new byte[] { 94 },
				new double[] { 190.0 }),

		BONEMEAL(6810, new short[] { 2434, 139, 141, 143 }, new short[] { 15328, 15329, 15330, 15331 },
				new byte[] { 94, 94, 94, 94 }, new double[] { 270, 270, 270, 270 }),

		MIXTURE_STEP_2(1526, new short[] { 10909, 10911, 10913, 10915 }, new short[] { 10915, 10917, 10919, 10921 },
				new byte[] { 65, 65, 65, 65 }, new double[] { 53.33, 53.33, 53.33, 53.33 }),

		SANFEW_SYRUM(10937, new short[] { 10915, 10917, 10919, 10921 }, new short[] { 10925, 10927, 10929, 10931 },
				new byte[] { 65, 65, 65, 65 }, new double[] { 53.33, 53.33, 53.33, 53.33 }),

		MORCHELLA_MUSHROOMS(21622, new short[] { 21628 }, new short[] { 21632 }, new byte[] { 94 },
				new double[] { 190 }),

		// Dung UNF

		SAGEWORT(17512, new short[] { 17492 }, new short[] { 17538 }, new byte[] { 3 }, new double[] { 0.0 }),

		VALERIAN(17514, new short[] { 17492 }, new short[] { 17540 }, new byte[] { 4 }, new double[] { 0.0 }),

		ALOE(17516, new short[] { 17492 }, new short[] { 17542 }, new byte[] { 8 }, new double[] { 0.0 }),

		WORMWOOD_LEAF(17518, new short[] { 17492 }, new short[] { 17544 }, new byte[] { 34 }, new double[] { 0.0 }),

		MAGEBANE(17520, new short[] { 17492 }, new short[] { 17546 }, new byte[] { 37 }, new double[] { 0.0 }),

		FEATHER_FOIL(17522, new short[] { 17492 }, new short[] { 17548 }, new byte[] { 41 }, new double[] { 0.0 }),

		GRIMY_WINTERS_GRIP(17524, new short[] { 17492 }, new short[] { 17550 }, new byte[] { 67 },
				new double[] { 0.0 }),

		LYCOPUS(17526, new short[] { 17492 }, new short[] { 17552 }, new byte[] { 70 }, new double[] { 0.0 }),

		BUCKTHORN(17528, new short[] { 17492 }, new short[] { 17554 }, new byte[] { 74 }, new double[] { 0.0 }),

		VOID_DUST(17530, new short[] { 17538, 17540, 17542, 17544, 17546, 17548, 17550, 17552, 17554 },
				new short[] { 17556, 17558, 17562, 17582, 17584, 17588, 17606, 17608, 17612 },
				new byte[] { 3, 5, 9, 36, 38, 42, 69, 71, 75 },
				new double[] { 21, 34, 41, 79.5, 83, 89, 155.5, 160, 170.5 }),

		MISHAPEN_CLAWS(17532, new short[] { 17540, 17542, 17538, 17546, 17548, 17544, 17552, 17554, 17550 },
				new short[] { 17560, 17570, 17578, 17586, 17594, 17602, 17610, 17618, 17626 },
				new byte[] { 7, 18, 30, 40, 51, 63, 73, 84, 96 },
				new double[] { 37.5, 57, 72, 86.5, 105.5, 139.5, 164, 189.5, 279 }),

		RED_MOSS(17534, new short[] { 17542, 17538, 17540, 17548, 17544, 17546, 17554, 17550, 17552 },
				new short[] { 17564, 17574, 17576, 17590, 17598, 17600, 17614, 17622, 17624 },
				new byte[] { 12, 24, 27, 45, 57, 60, 78, 90, 93 },
				new double[] { 41, 65, 68.5, 93, 123.5, 131, 173.5, 234, 253 }),

		FIREBREATH_WHISKEY(17536, new short[] { 17542, 17538, 17540, 17548, 17544, 17546, 17554, 17550, 17552 },
				new short[] { 17568, 17572, 17580, 17592, 17596, 17604, 17616, 17620, 17628 },
				new byte[] { 15, 21, 33, 48, 54, 66, 81, 87, 99 },
				new double[] { 53.5, 61, 75, 98.5, 114, 147, 178, 205.5, 315.5 });
		;

		private static Map<Short, Ingredients> ingredients = new HashMap<Short, Ingredients>();

		static {
			for (Ingredients ingredient : Ingredients.values()) {
				ingredients.put(ingredient.itemId, ingredient);
			}
		}

		public static Ingredients forId(int itemId) {
			return ingredients.get((short) itemId);
		}

		private final short itemId;
		private final short[] otherItems;
		private final short[] rewards;
		private final byte[] levels;
		private final double[] experience;

		private Ingredients(int itemId, short[] otherItems, short[] rewards, byte[] levels, double[] experience) {
			this.itemId = (short) itemId;
			this.otherItems = otherItems;
			this.rewards = rewards;
			this.levels = levels;
			this.experience = experience;
		}

		public double[] getExperience() {
			return experience;
		}

		public short getItemId() {
			return itemId;
		}

		public byte[] getLevels() {
			return levels;
		}

		public short[] getOtherItems() {
			return otherItems;
		}

		public short[] getRewards() {
			return rewards;
		}

		public byte getSlot(int itemId) {
			for (byte i = 0; i < otherItems.length; i++) {
				if (itemId == otherItems[i])
					return i;
			}
			return -1;
		}

	}

	public enum RawIngredient {

		UNICORN_HORN(237, new Item(235, 1)),

		CHOCOLATE_BAR(1973, new Item(1975, 1)),

		KEBBIT_TEETH(10109, new Item(10111, 1)),

		GORAK_CLAW(9016, new Item(9018, 1)),

		BIRDS_NEST(5075, new Item(6693, 1)),

		DESERT_GOAT_HORN(9735, new Item(9736, 1)),

		BLUE_DRAGON_SCALES(243, new Item(241, 1)),

		SPRING_SQ_IRK(10844, new Item(10848, 1)),

		SUMMER_SQ_IRK(10845, new Item(10849, 1)),

		AUTUMN_SQ_IRK(10846, new Item(10850, 1)),

		WINTER_SQ_IRK(10847, new Item(10851, 1)),

		CHARCOAL(973, new Item(704, 1)),

		RUNE_SHARDS(6466, new Item(6467, 1)),

		ASHES(592, new Item(8865, 1)),

		POISON_KARAMBWAN(3146, new Item(3152, 1)),

		SUQAH_TOOTH(9079, new Item(9082, 1)),

		FISHING_BAIT(313, new Item(12129, 1)),

		DIAMOND_ROOT(14703, new Item(14704, 1)),

		BLACK_MUSHROOM(4620, new Item(4622, 1)),

		MUD_RUNES(4698, new Item(9594, 1)),

		DUST_OF_ARMADYL(21776, new Item(21774, 8)),

		WYVERN_BONES(6812, new Item(6810, 1)),

		URCHIN_SMALL(35721, new Item(32622, 1)),

		URCHIN_MED(35722, new Item(32622, 1)),

		URCHIN_LARGE(35723, new Item(32622, 1)),

		ARMADYL_DUST(21776, new Item(21774, 8));

		private static Map<Short, RawIngredient> rawIngredients = new HashMap<Short, RawIngredient>();

		static {
			for (RawIngredient rawIngredient : RawIngredient.values())
				rawIngredients.put(rawIngredient.rawId, rawIngredient);
		}

		public static RawIngredient forId(int itemId) {
			return rawIngredients.get((short) itemId);
		}

		private final short rawId;
		private final Item crushedItem;

		private RawIngredient(int rawId, Item crushedItem) {
			this.rawId = (short) rawId;
			this.crushedItem = crushedItem;
		}

		public Item getCrushedItem() {
			return crushedItem;
		}

		public short getRawId() {
			return rawId;
		}
	}

	public static final int URCHINS[] = { 35721, 35722, 35723 };
	public static final short VIAL = 227;
	public static final short CUP_OF_HOT_WATER = 4460;
	public static final short COCONUT_MILK = 5935;
	public static final short PESTLE_AND_MORTAR = 233;
	public static final short SWAMP_TAR = 1939;

	public static final short MUD_RUNE = 4698;

	public static final short RANGE_POT3 = 169;

	public static int isHerbloreSkill(Item first, Item other) {
		Item swap = first;
		Ingredients ingredient = Ingredients.forId(first.getId());
		if (ingredient == null) {
			ingredient = Ingredients.forId(other.getId());
			first = other;
			other = swap;
		}
		if (ingredient != null) {
			int slot = ingredient.getSlot(other.getId());
			return slot > -1 ? ingredient.getRewards()[slot] : -1;
		}
		swap = first;
		RawIngredient raw = RawIngredient.forId(first.getId());
		if (raw == null) {
			raw = RawIngredient.forId(other.getId());
			first = other;
			other = swap;
		}
		if (raw != null) {
			return other.getId() == PESTLE_AND_MORTAR ? raw.getCrushedItem().getId() : -1;
		}
		return -1;
	}

	private Item node;
	private Item otherItem;
	private Ingredients ingredients;
	private RawIngredient rawIngredient;
	private int ticks;

	private byte slot;

	public Herblore(Item node, Item otherNode, int amount) {
		this.otherItem = otherNode;
		this.ticks = amount;
		if (node.getId() == PESTLE_AND_MORTAR || otherNode.getId() == PESTLE_AND_MORTAR) {
			this.rawIngredient = RawIngredient.forId(node.getId());
			if (rawIngredient == null) {
				rawIngredient = RawIngredient.forId(otherNode.getId());
				this.node = node;
				this.otherItem = otherNode;
			} else {
				rawIngredient = RawIngredient.forId(node.getId());
				this.node = otherNode;
				this.otherItem = node;
			}
		} else {
			this.ingredients = Ingredients.forId(node.getId());
			if (ingredients == null) {
				ingredients = Ingredients.forId(otherNode.getId());
				this.node = node;
				this.otherItem = otherNode;
			} else {
				ingredients = Ingredients.forId(node.getId());
				this.node = otherNode;
				this.otherItem = node;
			}
		}
	}

	public int get_amount(int id) {
		switch (id) {
		case 35721:
			return 5;
		case 35722:
			return 3;
		case 35723:
			return 1;
		default:
			return 1;
		}
	}

	@Override
	public boolean process(Player player) {
		if (player.getInterfaceManager().containsScreenInter()
				|| player.getInterfaceManager().containsInventoryInter()) {
			player.sendMessage("Please finish what you're doing before doing this action.");
			return false;
		}
		if (ingredients == Ingredients.TORSTOL && otherItem.getId() != VIAL) {
			if (!player.getInventory().containsOneItem(15309) || !player.getInventory().containsOneItem(15313)
					|| !player.getInventory().containsOneItem(15317) || !player.getInventory().containsOneItem(15321)
					|| !player.getInventory().containsOneItem(15325))
				return false;
		}
		if (otherItem.getId() == 12539 && node.getId() == RANGE_POT3) {
			if (!player.getInventory().containsOneItem(RANGE_POT3) || !player.getInventory().containsItem(12539, 5))
				return false;
			return true;
		}
		if (otherItem.getId() == MUD_RUNE && node.getId() == PESTLE_AND_MORTAR) {
			if (!player.getInventory().containsOneItem(MUD_RUNE)
					|| !player.getInventory().containsOneItem(PESTLE_AND_MORTAR))
				return false;
			return true;
		}
		if (!player.getInventory().containsOneItem(node.getId(), 1)
				|| !player.getInventory().containsOneItem(otherItem.getId(), 1))
			return false;
		if (Utils.random(500) == 0) {
			new EliNPC(player, player);
			player.sendMessage("<col=ff0000>Eli appears from nowhere.", true);
		}
		return true;
	}

	public int getAmount(int item) {
		int amt = 1;
		switch (item) {
		case 35721:
			amt = 5;
			break;
		case 35722:
			amt = Utils.random(2, 39);
			break;
		case 35723:
			amt = 1;
			break;
		default:
			amt = 1;
			break;
		}
		return amt;
	}

	@Override
	public int processWithDelay(Player player) {
		if (node.getId() == PESTLE_AND_MORTAR || otherItem.getId() == PESTLE_AND_MORTAR)
			player.setNextAnimation(new Animation(364));
		else {
			if (player.getAnimations().hasEnhancedPotion && player.getAnimations().enhancedPotion) {
				player.setNextGraphics(new Graphics(3216));
				player.setNextGraphics(new Graphics(3217));
				player.setNextGraphics(new Graphics(3218));
				player.setNextAnimation(new Animation(17097));
			} else
				player.setNextAnimation(new Animation(363));
		}
		ticks--;
		if (otherItem.getId() == SWAMP_TAR || node.getId() == SWAMP_TAR)
			player.sendMessage("You add the " + otherItem.getDefinitions().getName().toLowerCase().replace("clean ", "")
					+ " on the swamp tar.", true);

		else if (otherItem.getId() == PESTLE_AND_MORTAR || node.getId() == PESTLE_AND_MORTAR)
			player.sendMessage("You crush the " + otherItem.getDefinitions().getName().toLowerCase()
					+ " with your pestle and mortar.", true);
		else if (ingredients == Ingredients.TORSTOL && otherItem.getId() != VIAL) {
			player.addPotionsMade();
			player.sendMessage("You combine the torstol with the potions and get an overload; " + "potions made: "
					+ Colors.red + Utils.getFormattedNumber(player.getPotionsMade()) + "</col>.", true);
			player.getInventory().removeItems(new Item(15309), new Item(15313), new Item(15317), new Item(15321),
					new Item(15325), new Item(Ingredients.TORSTOL.getItemId()));

		} else {
			player.addPotionsMade();
			player.sendMessage("You mix the " + otherItem.getDefinitions().getName().toLowerCase()
					+ " into your potion; potions made: " + Colors.red
					+ Utils.getFormattedNumber(player.getPotionsMade()) + "</col>.", true);
		}
		if (otherItem.getId() == PESTLE_AND_MORTAR || node.getId() == PESTLE_AND_MORTAR) {
			int amt = getAmount(otherItem.getId());
			if (!player.getInventory().containsItem(otherItem.getId(), amt)) {
				player.sendMessage(Colors.red + "You don't have enough " + otherItem.getDefinitions().getName()
						+ " left to grind!", true);
				return -1;
			} else
				player.getInventory().removeItems(new Item(otherItem.getId(), getAmount(otherItem.getId())));

		} else {
			if (ingredients != Ingredients.TORSTOL) {
				if (player.hasCleansingActivated() && Utils.random(10) == 0)
					player.sendMessage(
							Colors.green + "<shad=000000>Your Scroll of Cleansing " + "saves you an ingredient!", true);
				else
					player.getInventory().removeItems(new Item(otherItem.getId(), get_amount(otherItem.getId())),
							rawIngredient == null ? new Item(node.getId(), 1) : null);
			}
		}

		player.getInventory().addItem(
				rawIngredient != null ? rawIngredient.getCrushedItem() : new Item(ingredients.getRewards()[slot], 1));
		player.getSkills().addXp(Skills.HERBLORE,
				(rawIngredient != null ? 0 : ingredients.getExperience()[slot]) * botanistSuit(player));
		Pets.checkSkillingPet(player, 38095);
		player.getAchManager().addKeyAmount("herblore", 1);
		if (ticks > 0)
			return player.hasCleansingActivated() && Utils.random(8) == 0 ? 0 : 1;
		return -1;
	}

	@Override
	public boolean start(Player player) {
		if (player.getInterfaceManager().containsScreenInter()
				|| player.getInterfaceManager().containsInventoryInter()) {
			player.sendMessage("Please finish what you're doing before doing this action.");
			return false;
		}
		if (player == null || node == null)
			return false;
		if ((ingredients == null && rawIngredient == null) || otherItem == null)
			return false;
		if (otherItem.getId() == PESTLE_AND_MORTAR || node.getId() == PESTLE_AND_MORTAR) {
			if (!player.getInventory().containsOneItem(PESTLE_AND_MORTAR)) {
				player.sendMessage("You need a pestle and mortar in order to do this.");
				return false;
			}
		}
		if (ingredients != null) {
			this.slot = ingredients.getSlot(otherItem.getId());
			if (slot == -1)
				this.slot = ingredients.getSlot(node.getId());
			if (player.getSkills().getLevel(Skills.HERBLORE) < ingredients.getLevels()[slot]) {
				player.sendMessage("You need a herblore level of " + ingredients.getLevels()[slot]
						+ " to combine these ingredients.");
				return false;
			}
			return true;
		}
		return true;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}

	/**
	 * XP modifier by wearing items.
	 * 
	 * @param player
	 *            The player.
	 * @return the XP modifier.
	 */
	private double botanistSuit(Player player) {
		double xpBoost = 1.0;
		if (player.getEquipment().getHatId() == 25190)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 34923)
			xpBoost *= 1.03;
		if (player.getEquipment().getChestId() == 25191)
			xpBoost *= 1.01;
		if (player.getEquipment().getLegsId() == 25192)
			xpBoost *= 1.01;
		if (player.getEquipment().getBootsId() == 25193)
			xpBoost *= 1.01;
		if (player.getEquipment().getGlovesId() == 25194)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 25190 && player.getEquipment().getChestId() == 25191
				&& player.getEquipment().getLegsId() == 25192 && player.getEquipment().getBootsId() == 25193
				&& player.getEquipment().getGlovesId() == 25194)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 34923 && player.getEquipment().getChestId() == 25191
				&& player.getEquipment().getLegsId() == 25192 && player.getEquipment().getBootsId() == 25193
				&& player.getEquipment().getGlovesId() == 25194)
			xpBoost *= 1.01;
		return xpBoost;
	}
}