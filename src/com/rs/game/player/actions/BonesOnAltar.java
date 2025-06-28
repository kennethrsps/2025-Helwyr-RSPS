package com.rs.game.player.actions;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.PrayerRandom;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles Bones on Altar action.
 * 
 * @author Zeus
 */
public class BonesOnAltar extends Action {

	public enum Bones {

		BONES(new Item(526, 1), 5), WOLF_BONES(new Item(2859, 1), 1), // nerfed from 8 because it can be bought from
		BURNT_BONES(new Item(528, 1), 5),														// shop
		BIG_BONES(new Item(532, 1), 15), JOGRE_BONES(new Item(3125, 1), 15), BURNT_JOGRE_BONES(new Item(3127, 1), 16),
		BABYDRAGON_BONES(new Item(534, 1), 30), WYVERN_BONES(new Item(6812, 1), 50), DRAGON_BONES(new Item(536, 1), 72),
		OURG_BONES(new Item(4834, 1), 140), ADAMANT_DRAGON_BONES(new Item(35008), 144),
		FROST_DRAGON_BONES(new Item(18830, 1), 180), RUNE_DRAGON_BONES(new Item(35010, 1), 190),
		DAGANNOTH_BONES(new Item(6729, 1), 125), AIRUT_BONES(new Item(30209, 1), 175);

		private static Map<Short, Bones> bones = new HashMap<Short, Bones>();

		static {
			for (Bones bone : Bones.values()) {
				bones.put((short) bone.getBone().getId(), bone);
			}
		}

		public static Bones forId(short itemId) {
			return bones.get(itemId);
		}

		private Item item;
		private int xp;

		private Bones(Item item, int xp) {
			this.item = item;
			this.xp = xp;
		}

		public Item getBone() {
			return item;
		}

		public int getXP() {
			return xp;
		}
	}

	public static Bones isGood(Item item) {
		return Bones.forId((short) item.getId());
	}

	public final String MESSAGE = "The gods are very pleased with your offerings.";

	public final double MULTIPLIER = 2.5;
	private Bones bone;
	private Item item;
	private WorldObject object;

	private Animation USING = new Animation(896);

	public BonesOnAltar(WorldObject object, Item item) {
		this.item = item;
		this.object = object;
	}

	private double firstAge(Player player) {
		double xpBoost = 1.0;
		if (player.getEquipment().getHatId() == 27587)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 34925)
			xpBoost *= 1.03;
		if (player.getEquipment().getCapeId() == 27588)
			xpBoost *= 1.01;
		if (player.getEquipment().getAmuletId() == 27589)
			xpBoost *= 1.01;
		if (player.getEquipment().getRingId() == 27590)
			xpBoost *= 1.01;
		if (player.getEquipment().getGlovesId() == 27591)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 27587 && player.getEquipment().getCapeId() == 27588
				&& player.getEquipment().getAmuletId() == 27589 && player.getEquipment().getRingId() == 27590
				&& player.getEquipment().getGlovesId() == 27591)
			xpBoost *= 1.01;
		if (player.getEquipment().getHatId() == 34925 && player.getEquipment().getCapeId() == 27588
				&& player.getEquipment().getAmuletId() == 27589 && player.getEquipment().getRingId() == 27590
				&& player.getEquipment().getGlovesId() == 27591)
			xpBoost *= 1.03;
		return xpBoost;
	}

	@Override
	public boolean process(Player player) {
		if (!World.containsObjectWithId(object, object.getId()))
			return false;
		if (!player.getInventory().containsItem(item.getId(), 1))
			return false;
		if (!player.getInventory().containsItem(bone.getBone().getId(), 1))
			return false;
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		player.closeInterfaces();
		double xpBoost = firstAge(player);
		if (player.getAnimations().hasStrongBurial && player.getAnimations().strongBurial) {
			player.setNextAnimation(new Animation(20294));
			player.setNextGraphics(new Graphics(4001));
		} else {
			player.setNextAnimation(USING);
			player.getPackets().sendGraphics(new Graphics(624), object);
		}
		player.getInventory().deleteItem(item.getId(), 1);
		player.getSkills().addXp(Skills.PRAYER,
				bone.getXP() * player.getAuraManager().getPrayerMultiplier() * MULTIPLIER * xpBoost);
		player.addBonesOffered();
		if (Utils.random(300) == 0) {
			new PrayerRandom(player, player);
			player.sendMessage("<col=ff0000>An odd monk emerges from the altar.");
		}
		player.sendMessage("The Gods are very pleased with your offerings; " + "bones offered: " + Colors.red
				+ Utils.getFormattedNumber(player.getBonesOffered()) + "</col>.", true);
		player.getInventory().refresh();
		player.faceObject(object);
		return 3;
	}

	@Override
	public boolean start(Player player) {
		if ((this.bone = Bones.forId((short) item.getId())) == null) {
			return false;
		}
		player.faceObject(object);
		return true;
	}

	@Override
	public void stop(final Player player) {
		this.setActionDelay(player, 3);
	}
}