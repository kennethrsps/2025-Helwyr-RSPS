package com.rs.game.player.actions.miscellania;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.game.player.content.FishingSpotsHandler;
import com.rs.utils.Utils;

public class MiscellaniaFishing extends Action {

	public enum Fish {

		LOBSTER(377, 40, 90),

		SHARK(383, 76, 110),

		SWORDFISH(371, 50, 100),

		TUNA(359, 35, 80);

		private final int id, level;
		private final double xp;

		private Fish(int id, int level, double xp) {
			this.id = id;
			this.level = level;
			this.xp = xp;
		}

		public int getId() {
			return id;
		}

		public int getLevel() {
			return level;
		}

		public double getXp() {
			return xp;
		}
	}

	public enum FishingSpots {
		
		CAGE(1399, 1, 301, -1, new Animation(619), Fish.LOBSTER),

		HARPOON(312, 2, 311, -1, new Animation(618), Fish.TUNA, Fish.SWORDFISH, Fish.SHARK),

		;

		private final Fish[] fish;
		private final int id, option, tool, bait;
		private final Animation animation;

		static final Map<Integer, FishingSpots> spot = new HashMap<Integer, FishingSpots>();

		public static FishingSpots forId(int id) {
			return spot.get(id);
		}

		static {
			for (FishingSpots spots : FishingSpots.values())
				spot.put(spots.id | spots.option << 24, spots);
		}

		private FishingSpots(int id, int option, int tool, int bait,
				Animation animation, Fish... fish) {
			this.id = id;
			this.tool = tool;
			this.bait = bait;
			this.animation = animation;
			this.fish = fish;
			this.option = option;
		}

		public Fish[] getFish() {
			return fish;
		}

		public int getId() {
			return id;
		}

		public int getOption() {
			return option;
		}

		public int getTool() {
			return tool;
		}

		public int getBait() {
			return bait;
		}

		public Animation getAnimation() {
			return animation;
		}
	}

	private FishingSpots spot;

	private NPC npc;
	private WorldTile tile;
	private int fishId;

	private final int[] BONUS_FISH = { 341, 349, 401, 407 };

	private boolean multipleCatch;

	public MiscellaniaFishing(FishingSpots spot, NPC npc) {
		this.spot = spot;
		this.npc = npc;
		tile = new WorldTile(npc);
	}

	@Override
	public boolean start(Player player) {
		if (!checkAll(player))
			return false;
		fishId = getRandomFish(player);
		if (spot.getFish()[fishId] == Fish.TUNA
				|| spot.getFish()[fishId] == Fish.SHARK
				|| spot.getFish()[fishId] == Fish.SWORDFISH) {
			if (Utils.getRandom(50) <= 5) {
				if (player.getSkills().getLevel(Skills.AGILITY) >= spot
						.getFish()[fishId].getLevel())
					multipleCatch = true;
			}
		}
		player.getPackets().sendGameMessage("You attempt to capture a fish...",
				true);
		setActionDelay(player, getFishingDelay(player));
		return true;
	}

	@Override
	public boolean process(Player player) {
		player.setNextAnimation(spot.getAnimation());
		return checkAll(player);
	}

	private int getFishingDelay(Player player) {
		int playerLevel = player.getSkills().getLevel(Skills.FISHING);
		int fishLevel = spot.getFish()[fishId].getLevel();
		int modifier = spot.getFish()[fishId].getLevel();
		int randomAmt = Utils.random(4);
		double cycleCount = 1, otherBonus = 0;
		if (player.getFamiliar() != null)
			otherBonus = getSpecialFamiliarBonus(player.getFamiliar().getId());
		cycleCount = Math
				.ceil(((fishLevel + otherBonus) * 50 - playerLevel * 10)
						/ modifier * 0.25 - randomAmt * 4);
		if (cycleCount < 1)
			cycleCount = 1;
		int delay = (int) cycleCount + 1;
		delay /= player.getAuraManager().getFishingAccurayMultiplier();
		return delay;

	}

	private int getSpecialFamiliarBonus(int id) {
		switch (id) {
		case 6796:
		case 6795:// rock crab
			return 1;
		}
		return -1;
	}

	private int getRandomFish(Player player) {
		int random = Utils.random(spot.getFish().length);
		int difference = player.getSkills().getLevel(Skills.FISHING)
				- spot.getFish()[random].getLevel();
		if (difference <= -1) {
			return 0;
		}
		if (random <= -1) {
			return 0;
		}
		return random;
	}

	@Override
	public int processWithDelay(Player player) {
		addFish(player);
		return getFishingDelay(player);
	}

	private void addFish(Player player) {
		Item fish = new Item(spot.getFish()[fishId].getId(), multipleCatch ? 2
				: 1);
		player.getPackets().sendGameMessage(getMessage(fish), true);
		player.getInventory().deleteItem(spot.getBait(), 1);
		double totalXp = spot.getFish()[fishId].getXp();
		if (hasFishingSuit(player))
			totalXp *= 1.025;
		player.getSkills().addXp(Skills.FISHING, totalXp);
		if(player.getThrone().getReputation() < 100.0) {
			player.getThrone().addReputation(.35);
		} else {
			player.getInventory().addItem(fish);
			if (player.getFamiliar() != null) {
				if (Utils.getRandom(50) == 0
						&& getSpecialFamiliarBonus(player.getFamiliar().getId()) > 0) {
					player.getInventory().addItem(new Item(BONUS_FISH[Utils.random(BONUS_FISH.length)]));
					player.getSkills().addXp(Skills.FISHING, 5.5);
				}
			}
		}
		fishId = getRandomFish(player);
		if (Utils.getRandom(50) == 0 && FishingSpotsHandler.moveSpot(npc))
			player.setNextAnimation(new Animation(-1));
	}

	private boolean hasFishingSuit(Player player) {
		if (player.getEquipment().getHatId() == 24427
				&& player.getEquipment().getChestId() == 24428
				&& player.getEquipment().getLegsId() == 24429
				&& player.getEquipment().getBootsId() == 24430)
			return true;
		return false;
	}

	private String getMessage(Item fish) {
		 if (multipleCatch)
			return "Your quick reactions allow you to catch two "
			+ fish.getDefinitions().getName().toLowerCase() + ".";
		else
			return "You manage to catch a "
			+ fish.getDefinitions().getName().toLowerCase() + ".";
	}

	private boolean checkAll(Player player) {
		if (player.getSkills().getLevel(Skills.FISHING) < spot.getFish()[fishId]
				.getLevel()) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage",
					"You need a fishing level of "
							+ spot.getFish()[fishId].getLevel()
							+ " to fish here.");
			return false;
		}
		if (!player.getInventory().containsOneItem(spot.getTool()) 
				&& !player.getToolBelt().contains(spot.getTool())) {
			player.getPackets()
			.sendGameMessage(
					"You need a "
							+ new Item(spot.getTool()).getDefinitions()
							.getName().toLowerCase()
							+ " to fish here.");
			return false;
		}
		if (spot.getBait() != -1
				&& !player.getInventory().containsOneItem(spot.getBait())) {
			player.getPackets()
			.sendGameMessage(
					"You don't have "
							+ new Item(spot.getBait()).getDefinitions()
							.getName().toLowerCase()
							+ " to fish here.");
			return false;
		}
		if (!player.getInventory().hasFreeSlots()) {
			player.setNextAnimation(new Animation(-1));
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You don't have enough inventory space.");
			return false;
		}
		if (tile.getX() != npc.getX() || tile.getY() != npc.getY())
			return false;
		return true;
	}

	public static boolean containsSmallNet(Player player) {
		if (player.getInventory().containsItemToolBelt(303)) {
			return true;
		}
		return false;
	}

	public static boolean containsLobsterPot(Player player) {
		if (player.getInventory().containsItemToolBelt(301)) {
			return true;
		}
		return false;
	}

	public static boolean containsFishRod(Player player) {
		if (player.getInventory().containsItemToolBelt(307)) {
			return true;
		}
		return false;
	}

	public static boolean containsFlyFishRod(Player player) {
		if (player.getInventory().containsItemToolBelt(309)) {
			return true;
		}
		return false;
	}

	public static boolean containsBigNet(Player player) {
		if (player.getInventory().containsItemToolBelt(305)) {
			return true;
		}
		return false;
	}

	public static boolean containsHarpoon(Player player) {
		if (player.getInventory().containsItemToolBelt(311)) {
			return true;
		}
		return false;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}
}
