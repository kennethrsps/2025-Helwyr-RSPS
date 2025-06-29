package com.rs.game.player.content.items;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles the Bonecrusher item.
 * 
 * @author Kris
 */
public class Bonecrusher {

	private static final String[] BONE_NAMES = new String[] { "Bones", "Big bones", "Jogre bones", "Zogre bones",
			"Ourg bones", "Airut bones", "Bat bones", "Wolf bones", "Monkey bones", "Dagannoth bones",
			"Baby dragon bones", "Dragon bones", "Frost dragon bones", "Wyvern bones", "Adamant drag. bones",
			"Rune dragon bones", "Airut bones", "Burnt bones" };

	private static final int[] BONES = new int[] { 526, 532, 3125, 4812, 4834, 30209, 530, 2859, 3179, 6729, 534, 536,
			18830, 6812, 35008, 35010, 30209, 528 };
	private static final double[] EXPERIENCE = new double[] { 9, 30, 30, 45, 280, 265, 10.6, 9, 10, 250, 60, 144, 360,
			100, 288, 380, 175, 9 };

	public static void openBonecrusher(Player player) {
		player.getTemporaryAttributtes().put("bonecrusher", true);
		player.setCloseInterfacesEvent(new Runnable() {
			@Override
			public void run() {
				player.getTemporaryAttributtes().remove("bonecrusher");
			}
		});
		for (int i = 0; i < player.bonecrusherSettings.length - 1; i++) {
			player.getPackets().sendIComponentText(1006, 14 + i, BONE_NAMES[i]);
			if (player.bonecrusherSettings[i])
				player.getPackets().sendIComponentSprite(1006, 32 + i, 2548);
			if (i == 16)
				player.getPackets().sendGlobalConfig(1605, ItemDefinitions.getItemDefinitions(BONES[15]).getValue());
			else
				player.getPackets().sendGlobalConfig(1336 + i, ItemDefinitions.getItemDefinitions(BONES[i]).getValue());
		}
		player.getPackets().sendHideIComponent(1006, 30, true);
		player.getPackets().sendHideIComponent(1006, 50, true);
		player.getPackets().sendIComponentText(1006, 29, BONE_NAMES[15]);
		player.getPackets().sendIComponentText(1006, 13, "Bonecrusher Settings");
		player.getPackets().sendIComponentText(1006, 31,
				"Instead of being dropped, any bones selected will be instantly crushed for 2x the burying experience.");
		player.getInterfaceManager().sendInterface(1006);
	}

	public static void handleBonecrusher(Player player, int componentId) {
		if (componentId >= 32 && componentId <= 50) {
			int id = componentId == 50 ? 16 : (componentId - 32);
			player.bonecrusherSettings[id] = !player.bonecrusherSettings[id];
			player.sendMessage(BONE_NAMES[id]
					+ (player.bonecrusherSettings[id] ? " are now being crushed for 2x burying experience."
							: "s are no longer being crushed for 2x burying experience."));
			player.getPackets().sendIComponentSprite(1006, componentId, player.bonecrusherSettings[id] ? 2548 : 2549);
		}
	}

	public static boolean handleDrop(Player player, Item item) {
		int i = 0;
		for (int settings : BONES) {
			if (settings == item.getId() && player.bonecrusherSettings[i]) {
				player.getSkills().addXp(Skills.PRAYER, EXPERIENCE[i] * item.getAmount());
				player.addBonesOffered();
				player.getPackets()
						.sendGameMessage("The bonecrusher instantly crushes the " + item.getName().toLowerCase()
								+ ". Bones offered: " + Colors.red + Utils.getFormattedNumber(player.getBonesOffered())
								+ ".", true);
				int restoration = 0;
				boolean aura = false;
				boolean amulet = false;
				if (player.getAuraManager().getPrayerRestoration() != 0) {
					aura = true;
					restoration = ((int) ((int) (Math.floor(player.getSkills().getLevelForXp(Skills.PRAYER)))
							* player.getAuraManager().getPrayerRestoration()));
				}
				if (player.getEquipment().getAmuletId() == 19886) {
					restoration = restoration + ((int) ((int) EXPERIENCE[i] * item.getAmount() * 0.5));
					amulet = true;
				} else if (player.getEquipment().getAmuletId() == 19887) {
					restoration = restoration + ((int) ((int) EXPERIENCE[i] * item.getAmount() * 0.75));
					amulet = true;
				} else if (player.getEquipment().getAmuletId() == 19888) {
					restoration = restoration + ((int) ((int) EXPERIENCE[i] * item.getAmount()));
					amulet = true;
				}
				if (restoration + player.getPrayer().getPrayerpoints() > player.getSkills().getLevelForXp(Skills.PRAYER)
						* 10)
					restoration = (player.getSkills().getLevelForXp(Skills.PRAYER) * 10)
							- player.getPrayer().getPrayerpoints();
				if (restoration > 0) {
					player.getPrayer().restorePrayer(restoration);
					if (aura && amulet)
						player.sendMessage("Your aura along with the amulet restore " + restoration + " prayer points.",
								true);
					else if (aura)
						player.sendMessage("Your aura restores " + restoration + " prayer points.", true);
					else if (amulet)
						player.sendMessage("Your amulet restores " + restoration + " prayer points.", true);
				}
				return true;
			}
			i++;
		}
		return false;
	}
}