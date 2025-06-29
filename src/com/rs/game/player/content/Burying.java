package com.rs.game.player.content;

import java.util.HashMap;
import java.util.Map;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.PrayerRandom;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class Burying {

	/**
	 * Handles Bone Burying.
	 * 
	 * @author Zeus
	 */
	public enum Bone {

		BONES(526, 5), WOLF_BONES(2859, 1), // nerfed from 8 because it can be bought from shop
		BIG_BONES(532, 15), JOGRE_BONES(3125, 15), BURNT_JOGRE_BONES(3127, 16), BABYDRAGON_BONES(534, 30),
		WYVERN_BONES(6812, 50), DRAGON_BONES(536, 72), OURG_BONES(4834, 140), ADAMANT_DRAGON_BONES(35008, 144),
		FROST_DRAGON_BONES(18830, 180), RUNE_DRAGON_BONES(35010, 190), DAGANNOTH_BONES(6729, 125), AIRUTS(30209, 175),
		BURNT_BONES(528, 5);
		private int id;
		private double experience;

		private static Map<Integer, Bone> bones = new HashMap<Integer, Bone>();

		static {
			for (Bone bone : Bone.values()) {
				bones.put(bone.getId(), bone);
			}
		}

		private static final Animation BURY_ANIMATION = new Animation(827);

		public static void bury(final Player player, int inventorySlot) {
			final Item item = player.getInventory().getItem(inventorySlot);
			if (item == null || Bone.forId(item.getId()) == null)
				return;
			if (player.isLocked())
				return;
			final Bone bone = Bone.forId(item.getId());
			final ItemDefinitions itemDef = new ItemDefinitions(item.getId());
			final double xp = increasedExperience(player, bone.getExperience());
			player.lock(1);
			player.getPackets().sendSound(2738, 0, 1);
			if (player.getAnimations().hasStrongBurial && player.getAnimations().strongBurial) {
				player.setNextAnimation(new Animation(20294));
				player.setNextGraphics(new Graphics(4001));
			} else
				player.setNextAnimation(BURY_ANIMATION);
			player.getPackets().sendGameMessage("You dig a hole in the ground...", true);
			player.getInventory().deleteItem(item.getId(), 1);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.addBonesOffered();
					if (Utils.random(600) == 0) {
						new PrayerRandom(player, player);
						player.sendMessage("<col=ff0000>An odd looking monk approaches you.");
					}
					player.sendMessage("You bury the " + itemDef.getName().toLowerCase() + "; " + "bones offered: "
							+ Colors.red + Utils.getFormattedNumber(player.getBonesOffered()) + "</col>.", true);
					player.getSkills().addXp(Skills.PRAYER, xp);
					if (player.getAuraManager().getPrayerRestoration() != 0)
						player.getPrayer().restorePrayer(
								(int) ((int) (Math.floor(player.getSkills().getLevelForXp(Skills.PRAYER)))
										* player.getAuraManager().getPrayerRestoration()));
					/** Twisted Bird Skull necklace **/
					if (player.getEquipment().getAmuletId() == 19886)
						player.getPrayer().restorePrayer((int) ((int) bone.getExperience() * 0.5));
					/** Split dragontooth necklace **/
					else if (player.getEquipment().getAmuletId() == 19887)
						player.getPrayer().restorePrayer((int) ((int) bone.getExperience() * 0.75));
					/** Demon horn necklace **/
					else if (player.getEquipment().getAmuletId() == 19888)
						player.getPrayer().restorePrayer((int) bone.getExperience());
					stop();
				}
			}, 1);
		}

		public static Bone forId(int id) {
			return bones.get(id);
		}

		public static double increasedExperience(Player player, double totalXp) {
			if (Wilderness.isAtWild(player) && player.getEquipment().getGlovesId() == 13848)
				totalXp *= 1.030;
			if (player.getEquipment().getHatId() == 27587)
				totalXp *= 1.01;
			if (player.getEquipment().getHatId() == 34925)
				totalXp *= 1.03;
			if (player.getEquipment().getCapeId() == 27588)
				totalXp *= 1.01;
			if (player.getEquipment().getAmuletId() == 27589)
				totalXp *= 1.01;
			if (player.getEquipment().getRingId() == 27590)
				totalXp *= 1.01;
			if (player.getEquipment().getGlovesId() == 27591)
				totalXp *= 1.01;
			if (player.getEquipment().getHatId() == 27587 && player.getEquipment().getCapeId() == 27588
					&& player.getEquipment().getAmuletId() == 27589 && player.getEquipment().getRingId() == 27590
					&& player.getEquipment().getGlovesId() == 27591)
				totalXp *= 1.01;
			if (player.getEquipment().getHatId() == 34925 && player.getEquipment().getCapeId() == 27588
					&& player.getEquipment().getAmuletId() == 27589 && player.getEquipment().getRingId() == 27590
					&& player.getEquipment().getGlovesId() == 27591)
				totalXp *= 1.03;
			totalXp *= player.getAuraManager().getPrayerMultiplier();
			return totalXp;
		}

		private Bone(int id, double experience) {
			this.id = id;
			this.experience = experience;
		}

		public double getExperience() {
			return experience;
		}

		public int getId() {
			return id;
		}
	}
}