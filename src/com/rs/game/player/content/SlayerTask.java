package com.rs.game.player.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class SlayerTask implements Serializable {

	private static final long serialVersionUID = -3885979679549716755L;

	/**
	 * Represents the Player's slayer partner.
	 */
	private transient Player socialPlayer;

	/**
	 * An enum containing all available Slayer Tasks.
	 * 
	 * @author Zeus
	 */
	public enum Master {
		KURADAL(9085, new Object[][] { { "Rock crab", 1, 20, 10, 20, 10.72 }, { "Crawling hand", 5, 35, 15, 30, 35.81 },
				{ "Cave crawler", 10, 40, 20, 35, 38.654 }, { "Rockslug", 15, 50, 20, 50, 42.15 },
				{ "Jelly", 35, 70, 35, 70, 55.99 }, { "Pyrefiend", 30, 65, 30, 70, 52.65 },
				{ "Cockatrice", 25, 90, 30, 70, 43.44 }, { "Infernal mage", 45, 85, 30, 70, 95.18 },
				{ "Abyssal demon", 85, 100, 70, 200, 209.27 }, { "Nechryael", 80, 100, 50, 150, 135.36 },
				{ "Aberrant spectre", 60, 100, 50, 150, 125.45 }, { "Waterfiend", 60, 100, 50, 150, 145.45 },
				{ "Gargoyle", 75, 100, 50, 150, 170.54 }, { "Dark beast", 90, 100, 50, 150, 170.63 },
				{ "Bloodveld", 50, 100, 50, 150, 140.72 }, { "Green dragon", 60, 100, 60, 150, 110.81 },
				{ "Glacor", 95, 100, 10, 20, 301.99 }, { "Fungal rodent", 25, 60, 20, 40, 15.13 },
				{ "Grifolaroo", 82, 95, 20, 60, 165.38 }, { "Grifolapine", 88, 97, 20, 60, 155.46 },
				{ "Ganodermic runt", 95, 100, 50, 75, 155.55 }, { "Ganodermic beast", 95, 100, 50, 125, 185.54 },
				{ "Frost dragon", 90, 100, 25, 100, 135.83 }, { "Ice strykewyrm", 93, 100, 30, 150, 200.76 },
				{ "Jungle strykewyrm", 73, 100, 30, 125, 146.55 }, { "Desert strykewyrm", 77, 100, 30, 125, 167.43 },
				{ "Iron dragon", 50, 100, 40, 100, 125.15 }, { "Steel dragon", 65, 100, 40, 100, 150.75 },
				{ "Mithril dragon", 75, 100, 45, 55, 175.50 }, { "Adamant dragon", 75, 100, 40, 50, 201.15 },
				{ "Hellhound", 55, 100, 60, 150, 85.55 }, { "Greater demon", 50, 100, 60, 150, 115.75 },
				{ "Bronze dragon", 50, 100, 30, 110, 145.75 }, { "Hill giant", 15, 65, 40, 80, 15.31 },
				{ "Moss giant", 35, 85, 40, 150, 38.73 }, { "Fire giant", 50, 85, 40, 150, 60.15 },
				{ "Turoth", 55, 90, 60, 150, 100.13 }, { "Basilisk", 40, 90, 60, 150, 92.53 },
				{ "Kurask", 70, 99, 60, 150, 100.42 }, { "Black demon", 10, 95, 60, 150, 112.42 },
				{ "Baby blue dragon", 25, 75, 40, 85, 36.16 }, { "Blue dragon", 50, 85, 40, 200, 105.48 },
				{ "Lesser demon", 50, 90, 60, 190, 86.43 }, { "Rune dragon", 90, 100, 45, 74, 225.30 },
				{ "Rorarius", 81, 100, 50, 130, 140 }, { "Gladius", 81, 100, 50, 110, 150.2 },
				{ "Capsarius", 81, 100, 50, 130, 164 }, { "Edimmu", 90, 100, 170, 265, 250.15 },
				{ "Scutarius", 81, 100, 50, 90, 209.5 }, { "Skeleton", 5, 65, 40, 100, 20.243 } });

		/**
		 * name, min slay level, max slay level, min task amount, max task
		 * amount, exp
		 */
		private int id;
		private Object[][] data;

		private Master(int id, Object[][] data) {
			this.id = id;
			this.data = data;
		}

		public static Master forId(int id) {
			for (Master master : Master.values()) {
				if (master.id == id)
					return master;
			}
			return null;
		}

		public int getId() {
			return id;
		}
	}

	private Master master;
	private int taskId;
	private int taskAmount;
	private int amountKilled;

	public SlayerTask(Master master, int taskId, int taskAmount) {
		this.master = master;
		this.taskId = taskId;
		this.taskAmount = taskAmount;
	}

	public String getName(Player player) {
		return (String) master.data[taskId][0];
	}

	/**
	 * Handles setting Slayer Tasks for the player.
	 * 
	 * @param player
	 *            The player to set.
	 * @param master
	 *            The master to set.
	 * @param resetTask
	 *            If we should reset the task.
	 * @return this
	 */
	public static SlayerTask random(Player player, Master master, boolean resetTask) {
		Player partner = World.getPlayerByDisplayName(player.getSlayerPartner());
		List<Integer> availableTasks = new ArrayList<Integer>();
		for (int i = 0; i < master.data.length; i++) {
			Object[] data = master.data[i];
			if (data == null)
				continue;
			int requiredLevel = (Integer) data[1];
			int maxLevel = (Integer) data[2];
			if (player.getSkills().getLevel(Skills.SLAYER) < requiredLevel
					|| player.getSkills().getLevel(Skills.SLAYER) > maxLevel)
				continue;
			availableTasks.add(i);
		}
		if (availableTasks.isEmpty())
			return null;
		int randomData = availableTasks.get(Utils.random(availableTasks.size() - 1));
		int minimum = (Integer) master.data[randomData][3];
		int maximum = (Integer) master.data[randomData][4];
		SlayerTask task = new SlayerTask(master, randomData, Utils.random(minimum, maximum));
		player.setTask(task);
		if (resetTask)
			player.setTaskStreak(0);
		if (partner != null) {
			if (partner.getTask() == null || resetTask) {
				partner.setTask(task);
				partner.sendMessage("You and your partner, " + player.getDisplayName()
						+ ", have received <col=ff0000><shad=000000>" + partner.getTask().getTaskAmount() + " x "
						+ partner.getTask().getName(partner).toLowerCase() + "</col></shad> to kill.");
				player.sendMessage("You and your partner " + partner.getDisplayName()
						+ " have received <col=ff0000><shad=000000>" + player.getTask().getTaskAmount() + " x "
						+ player.getTask().getName(player).toLowerCase() + "</col></shad> to kill.");
				if (resetTask)
					partner.setTaskStreak(0);
			} else {
				player.setTask(null);
				player.sendMessage("Your partner <col=00ff00>" + partner.getDisplayName()
						+ "</col> is still on a task, they have <col=ff0000>" + partner.getTask().getTaskAmount()
						+ "</col> of <col=00ff00>" + partner.getTask().getName(partner).toLowerCase()
						+ "</col> to kill.");
			}
		}
		return task;
	}

	public int getTaskId() {
		return taskId;
	}

	public int getTaskAmount() {
		return taskAmount;
	}

	public void decreaseAmount() {
		taskAmount--;
	}

	public int getXPAmount() {
		Object obj = master.data[taskId][5];
		if (obj instanceof Double)
			return (int) Math.round((Double) obj);
		if (obj instanceof Integer)
			return (Integer) obj;
		return 0;
	}

	public Master getMaster() {
		return master;
	}

	public int getAmountKilled() {
		return amountKilled;
	}

	public void setAmountKilled(int amountKilled) {
		this.amountKilled = amountKilled;
	}

	public Player getSocialPlayer() {
		return socialPlayer;
	}

	/**
	 * Calculates how many Slayer points should we give.
	 * 
	 * @param player
	 *            The player to give.
	 * @return The points to give.
	 */
	private static int givePoints(Player player) {
		int points = 20;
		if (player.getEquipment().getRingId() == 13281)
			points += 20;
		if (player.getTaskStreak() % 10 == 0)
			points += 20;
		if (player.getPerkManager().perslaysion)
			points = (int) (points * 1.2);
		return points;
	}

	/**
	 * Handles Slayer Task monster killing.
	 * 
	 * @param killer
	 *            The killer.
	 * @param npc
	 *            The NPC killed.
	 */
	public static void onKill(Player killer, NPC npc) {
		if (killer.getTask() != null) {
			int lvl = killer.getSkills().getLevelForXp(Skills.SLAYER);
			if (npc.getDefinitions().name.toLowerCase().contains(killer.getTask().getName(killer).toLowerCase())) {
				killer.getSkills().addXp(Skills.SLAYER, killer.getTask().getXPAmount());
				killer.getResourceGather().submitResourceGatherForStats(new Item(npc.getId(), 1), killer.getTask().getXPAmount(), Skills.SLAYER, true);
				killer.getTask().decreaseAmount();
				if (killer.getAnimations().hasBattleCry && killer.getAnimations().battleCry)
					killer.setNextAnimation(new Animation(17072));
				if (killer.getTask().getTaskAmount() <= 0) {
					killer.getSkills().addXp(Skills.SLAYER,
							lvl * 9.1 * (killer.getPerkManager().perslaysion ? 1.50 : 1));
					killer.tasksCompleted++;
					killer.setTaskStreak(killer.getTaskStreak() + 1);
					killer.sendMessage("You've completed <shad=000000>" + Colors.green
							+ Utils.getFormattedNumber(killer.getTaskStreak()) + "</col></shad> "
							+ "slayer tasks in a row and gain <shad=000000>" + Colors.green + givePoints(killer)
							+ "</col></shad> slayer points.");
					killer.setSlayerPoints(killer.getSlayerPoints() + givePoints(killer));
					killer.getPackets().sendMusicEffect(62);
					killer.setTask(null);
				}
			}
			TaskTab.sendTab(killer);
		}
		if (!World.containsPlayer(killer.getSlayerPartner()))
			return;
		Player slayerPartner = World.getPlayerByDisplayName(killer.getSlayerPartner());
		boolean withinDistance = slayerPartner.withinDistance(killer, 14);
		if (killer.getSlayerPartner() != null) {
			if (slayerPartner.getTask() != null) {
				if (npc.getDefinitions().name.toLowerCase()
						.equalsIgnoreCase(slayerPartner.getTask().getName(slayerPartner).toLowerCase())) {
					killer.getSkills().addXp(Skills.SLAYER,
							slayerPartner.getTask().getXPAmount() / (withinDistance ? 2 : 5));
					killer.getResourceGather().submitResourceGatherForStats(new Item(npc.getId(), 1), slayerPartner.getTask().getXPAmount() / (withinDistance ? 2 : 5), Skills.SLAYER, true);
					slayerPartner.getResourceGather().submitResourceGatherForStats(new Item(npc.getId(), 1), slayerPartner.getTask().getXPAmount(), Skills.SLAYER, true);
					slayerPartner.getSkills().addXp(Skills.SLAYER, slayerPartner.getTask().getXPAmount());
					slayerPartner.getTask().decreaseAmount();
					if (slayerPartner.getTask().getTaskAmount() <= 0) {
						int lvl = slayerPartner.getSkills().getLevelForXp(Skills.SLAYER);
						slayerPartner.getSkills().addXp(Skills.SLAYER, (lvl * 9.1) / (withinDistance ? 2 : 5)
								* (killer.getPerkManager().perslaysion ? 1.25 : 1));
						slayerPartner.getResourceGather().submitResourceGatherForStats(new Item(npc.getId(), 1), (lvl * 9.1) / (withinDistance ? 2 : 5)
								* (killer.getPerkManager().perslaysion ? 1.25 : 1), Skills.SLAYER, true);
						slayerPartner.tasksCompleted++;
						slayerPartner.setTaskStreak(slayerPartner.getTaskStreak() + 1);
						slayerPartner.sendMessage("You've completed <shad=000000>" + Colors.green
								+ Utils.getFormattedNumber(slayerPartner.getTaskStreak()) + "</col></shad> "
								+ "slayer tasks in a row and gain <shad=000000>" + Colors.green
								+ givePoints(slayerPartner) + "</col></shad> slayer points.");
						slayerPartner.setSlayerPoints(slayerPartner.getSlayerPoints() + givePoints(slayerPartner));
						slayerPartner.getPackets().sendMusicEffect(62);
						slayerPartner.setTask(null);
					}
				}
			}
		}
		TaskTab.sendTab(killer);
	}

	/**
	 * Opens the Slayer Shop. Interface 1308 is the new ID
	 * 
	 * @param player
	 */
	public static void openSlayerShop(Player player) {
		player.getInterfaceManager().sendInterface(164);
		player.getPackets().sendIComponentText(164, 20, " " + player.getSlayerPoints());
		player.getPackets().sendIComponentText(164, 32, "40 points");
		player.getPackets().sendIComponentText(164, 23, "Reset Task");
	}

	/**
	 * Used for handling Kuradals Slayer reward shop.
	 * 
	 * @param player
	 *            The player.
	 * @param componentId
	 *            The interface componentId.
	 */
	public static void handleShop(Player player, int interfaceId, int componentId) {
		if (interfaceId == 164) {
			if (componentId == 16) {
				sendLearn(player);
			}
			if (componentId == 17) {
				sendAssignment(player);
				return;
			}
			if (componentId == 24) {
				player.getDialogueManager().startDialogue("SlayerRewards", "xp", 40);
				return;
			}
			if (componentId == 26) {
				player.getDialogueManager().startDialogue("SlayerRewards", "ring", 75);
				return;
			}
			if (componentId == 28) {
				player.getDialogueManager().startDialogue("SlayerRewards", "runes", 35);
				return;
			}
			if (componentId == 37) {
				player.getDialogueManager().startDialogue("SlayerRewards", "bolts", 35);
				return;
			}
			if (componentId == 39) {
				player.getDialogueManager().startDialogue("SlayerRewards", "arrows", 35);
				return;
			}
			player.getInterfaceManager().closeChatBoxInterface();
		}
		if (interfaceId == 378) {
			if (componentId == 14) {
				sendAssignment(player);
			}
			if (componentId == 15) {
				openSlayerShop(player);
			}
			if (componentId == 73) {
				player.getDialogueManager().startDialogue("SlayerRewards", "imbueonyx", 300);
				return;
			}
			if (componentId == 74) {
				player.getDialogueManager().startDialogue("SlayerRewards", "imbue", 300);
				return;
			}
			if (componentId == 75) {
				player.getDialogueManager().startDialogue("SlayerRewards", "charmcol", 650);
				return;
			}
			if (componentId == 76) {
				player.getDialogueManager().startDialogue("SlayerRewards", "helm", 400);
				return;
			}
			if (componentId == 77) {
				player.getDialogueManager().startDialogue("SlayerRewards", "seed", 200);
				return;
			}
			if (componentId == 78) {
				player.getDialogueManager().startDialogue("SlayerRewards", "torso", 200);
				return;
			}
			player.getInterfaceManager().closeChatBoxInterface();
		}
	}

	public static void sendAssignment(Player player) {
		player.closeInterfaces();
		player.getDialogueManager().startDialogue("KuradalAssignment", 9085); // TODO
	}

	public static void sendLearn(Player player) {
		player.getInterfaceManager().sendInterface(378);
		player.getPackets().sendIComponentText(378, 79, " " + player.getSlayerPoints());
		player.getPackets().sendIComponentText(378, 82, "Reset Task");
		player.getPackets().sendItemOnIComponent(378, 102, 27996, 1); // LAST
																		// SLOT
		player.getPackets().sendItemOnIComponent(378, 93, 6731, 1); // FIFTH
																	// SLOT
																	// START
		player.getPackets().sendItemOnIComponent(378, 94, 6733, 1);
		player.getPackets().sendItemOnIComponent(378, 95, 6735, 1);
		player.getPackets().sendItemOnIComponent(378, 96, 6737, 1); // FIFTH
																	// SLOT END
		player.getPackets().sendItemOnIComponent(378, 92, 6575, 1); // FOURTH
																	// SLOT
		player.getPackets().sendItemOnIComponent(378, 103, 13263, 1); // slayer
																		// helmet
		player.getPackets().sendItemOnIComponent(378, 104, 13263, 1); // slayer
																		// helmet
		player.getPackets().sendItemOnIComponent(378, 105, 32625, 1); // SECOND
																		// SLOT
		player.getPackets().sendItemOnIComponent(378, 101, 10551, 1); // THIRD
																		// SLOT
		player.getPackets().sendIComponentText(378, 83, "Slayer helmet");
		player.getPackets().sendIComponentText(378, 84, "Imbue onyx ring");
		player.getPackets().sendIComponentText(378, 85, "Imbue ring");
		player.getPackets().sendIComponentText(378, 86, "Charm Collector");
		player.getPackets().sendIComponentText(378, 87, "Crystal Weapon Seed");
		player.getPackets().sendIComponentText(378, 88, "Fighter's torso");
		player.getPackets().sendIComponentText(378, 90, "400 points");
		player.getPackets().sendIComponentText(378, 91, "300 points");
		player.getPackets().sendIComponentText(378, 97, "300 points");
		player.getPackets().sendIComponentText(378, 99, "200 points");
		player.getPackets().sendIComponentText(378, 100, "200 points");
		player.getPackets().sendIComponentText(378, 98, "650 points");
	}

	/**
	 * InterfaceId 1308 hideComponent comoponentId 12 = co-op use food on
	 * partner componentId 42 = hides the navigationButtons componentId 472 =
	 * co-op aquanite pet componentId 493 = co-op potion effects componentId 495
	 * = co-op strykewyrm pet
	 */
	public static void handleNewShop(Player player) {

	}
}