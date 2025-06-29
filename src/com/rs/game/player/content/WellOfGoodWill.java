package com.rs.game.player.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Locale;

import com.discord.Discord;
import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles the Well Of Good Will.
 *
 * @author Noel
 */
public class WellOfGoodWill {

	/**
	 * The amount of time for the WorldTask.
	 */
	public static int taskTime;

	/**
	 * Sends a dialogue for the amount to give.
	 *
	 * @param player The Player giving the amount.
	 */
	public static void give(Player player) {
		if (World.isWellActive()) {
			player.getDialogueManager().startDialogue("SimpleMessage", "The XP well is already active.");
			return;
		}
		if (World.isWeekend()) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Well of Good Will does not stack with Weekend bonus EXP.");
			return;
		}
		player.getPackets().sendInputIntegerScript(true,
				"Progress: " + NumberFormat.getNumberInstance(Locale.US).format(World.getWellAmount()) + " GP ("
						+ ((World.getWellAmount() * 100) / Settings.WELL_MAX_AMOUNT) + "% of Goal); " + "Goal: "
						+ NumberFormat.getNumberInstance(Locale.US).format(Settings.WELL_MAX_AMOUNT) + " GP");
		player.getTemporaryAttributtes().put("donate_xp_well", Boolean.TRUE);
	}

	/**
	 * Donates to the well the amount to give.
	 *
	 * @param player The Player donating.
	 * @param amount The amount to give.
	 */
	public static void donate(Player player, int amount) {
		if (amount < 0)
			return;
		if ((World.getWellAmount() + amount) > Settings.WELL_MAX_AMOUNT)
			amount = Settings.WELL_MAX_AMOUNT - World.getWellAmount();
		if (!player.hasMoney(amount)) {
			player.sendMessage("You don't have that much money.");
			return;
		}
		if (player.getUsername().equalsIgnoreCase("kingkenobi")) {
			player.takeMoney(amount);
			player.donatedToWell += amount;
			player.recentWellDonated += amount;
			player.sendMessage("You've donated a total of " + Colors.red + Utils.moneyToString(player.donatedToWell)
					+ "</col> coins to the Well of Good Will.");
			player.setNextAnimation(new Animation(21841));
			World.addWellAmount(player.getDisplayName(), amount);
			postDonation();
			save();
		}
		if (amount < 1) {
			player.sendMessage("You must donate at least 1 GP to the Well of Good Will.");
			return;
		}
		if (player.getSkills().getTotalLevel(player) < 100) {
			player.sendMessage("New players cannot donate to the Well of Good Will.");
			return;
		}

		if ((Utils.currentTimeMillis() - player.lastWellDonation) < (24 * 60 * 60 * 1000)) { // 24h wait time
			player.sendMessage(Colors.darkRed + Colors.shad + "You have donated 100M+ to the well in the last 24h!",
					false);
			return;
		} else {
			if (player.recentWellDonated >= 1000000000)
				player.recentWellDonated = 0;
		}

		if (player.recentWellDonated + amount >= 1000000000) {
			player.lastWellDonation = Utils.currentTimeMillis();
			player.sendMessage(Colors.green + Colors.shad
					+ "You have reached the total amount you can donate to the well for today!", false);
		}
		player.takeMoney(amount);
		player.donatedToWell += amount;
		player.recentWellDonated += amount;
		player.sendMessage("You've donated a total of " + Colors.red + Utils.moneyToString(player.donatedToWell)
				+ "</col> coins to the Well of Good Will.");
		player.setNextAnimation(new Animation(21841));
		World.addWellAmount(player.getDisplayName(), amount);
		postDonation();
		save();
	}

	/**
	 * A check after donating to the well to see if the x2 XP should start.
	 */
	private static void postDonation() {
		if (World.getWellAmount() >= Settings.WELL_MAX_AMOUNT) {
			Discord.sendEventsMessage(
					"The goal of " + NumberFormat.getNumberInstance(Locale.US).format(Settings.WELL_MAX_AMOUNT)
							+ " GP has been reached! Double XP for 2 hours begins now!");
			World.sendWorldMessage("<col=FF0000>The goal of "
					+ NumberFormat.getNumberInstance(Locale.US).format(Settings.WELL_MAX_AMOUNT)
					+ " GP has been reached! Double XP for 2 hours begins now!", false);
			taskTime = 12000; // ~2 hours
			setWellTask();
			World.setWellActive(true);
			save();
		}
	}

	/**
	 * Sets the task for the reset of the well.
	 */
	public static void setWellTask() {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				World.setWellActive(false);
				World.resetWell();
				save();
			}
		}, taskTime);
	}

	/**
	 * Saves the progress of the well. If the x2 event is already active, this sends
	 * the amount left in gameticks.
	 */
	public static void save() {
		if (Settings.DEBUG)
			return;
		File output = new File("./data/goodwillWell.txt");
		if (output.canWrite()) {
			BufferedWriter out = null;
			try {
				out = new BufferedWriter(new FileWriter(output, false));
				if (World.isWellActive())
					out.write("true " + taskTime);
				else
					out.write("false " + World.getWellAmount());
			} catch (IOException ignored) {
				ignored.getStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException ignored) {
						ignored.getStackTrace();
					}
				}
			}
		}
	}

	@SuppressWarnings("resource")
	public static void load() throws IOException {
		if (Settings.DEBUG)
			return;
		BufferedReader reader = new BufferedReader(new FileReader("./data/goodwillWell.txt"));
		String line = null;
		while ((line = reader.readLine()) != null) {
			String[] args = line.split(" ");
			if (args[0].contains("true")) {
				World.setWellActive(true);
				taskTime = Integer.parseInt(args[1]);
				setWellTask();
			} else
				World.setWellAmount(Integer.parseInt(args[1]));
		}
	}
}