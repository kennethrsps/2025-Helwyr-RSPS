package com.rs.game.player.content;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles the player sent World Messages (Yell).
 *
 * @ausky Noel
 */
public class YellManager {

	/**
	 * Sends the World Message.
	 *
	 * @param player  The player sending the message.
	 * @param message The message String to send.
	 */
	public static void sendYell(Player player, String message) {
		if (!player.isDonator() && player.getRights() == 0) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You have to be a donator or a staff Donor in order to yell. Type ::store to rank-up and enjoy the "
							+ "unlimited World Messages :)!");
			return;
		}
		if (message.length() > 80)
			message = message.substring(0, 80);
		String color = (player.getYellColor() == "000000" || player.getYellColor() == null ? player.getDonorColor()
				: "<col=" + player.getYellColor() + ">");
		color = (player.getDisplayName().equalsIgnoreCase("")) ? Colors.purple + Colors.shad
				: (player.isDev() ? Colors.rcyan + Colors.shad
						: (player.isSilver() ? Colors.cyan + Colors.shad
								: (player.isSupport() ? Colors.blue + Colors.shad : Colors.red + Colors.shad)));
		String text = player.getDisplayName() + ": " + player.getDonorColor() + Utils.fixChatMessage(message);
		if (player.getRights() < 1 && !player.isSupport()) {
			String[] invalid = { "<euro", "<img", "<img=", "<col", "<col=", "<shad", "<shad=", "<str>", "<u>" };
			for (String s : invalid) {
				if (message.contains(s)) {
					player.sendMessage("You are not allowed to add additional code to the message.");
					return;
				}
			}
			if (message.toLowerCase().contains("www") || message.toLowerCase().contains(".org")
					|| message.toLowerCase().contains(".com") || message.toLowerCase().contains(".net")
					|| message.toLowerCase().contains(".tv") || message.toLowerCase().contains(".us")
					|| message.toLowerCase().contains("rsps") || message.toLowerCase().contains("scape")
							&& !message.toLowerCase().contains(Settings.SERVER_NAME)) {
				player.sendMessage("You are not allowed to advertise/insert URL's into the yell channel.");
				return;
			}
			if (message.toLowerCase().contains("noele"))
				message.replaceAll("zeus", "");

			if (message.toLowerCase().contains("ataraxia"))
				message.replaceAll("Helwyr3", "Helwyr");

			if (player.getYellDelay() > Utils.currentTimeMillis() && player.getRights() == 0) {
				player.sendMessage("You must wait a bit longer before sending another World message.");
				return;
			}
			if (player.getMuted() > Utils.currentTimeMillis()) {
				player.sendMessage("You are muted and cannot yell.");
				return;
			}
			player.setYellDelay(Utils.currentTimeMillis() + 3000);
			if (player.isSponsor()) {
				World.sendWorldYellMessage("[" + "<col=ff8c00>Sponsor Donor</col>" + "] " + player.getIcon() + text,
						player);
				return;
			}
			if (player.getUsername().equalsIgnoreCase("zeus") || player.getUsername().equalsIgnoreCase("")) {
				World.sendWorldYellMessage(
						"[" + Colors.purple + Colors.shad + "Owner</col></shad>] " + player.getIcon() + text, player);
				return;
			}
			if (player.getUsername().equalsIgnoreCase("")) {
				World.sendWorldYellMessage(
						"[" + Colors.orange + Colors.shad + "Co-owner</col></shad>] " + player.getIcon() + text,
						player);
				return;
			}
			if (player.isDev() || player.getUsername().equalsIgnoreCase("")) {
				World.sendWorldYellMessage(
						"[" + Colors.rcyan + Colors.shad + "Developer</col></shad>] " + player.getIcon() + text,
						player);
				return;
			}
			if (player.getRights() == 2 || player.isAdmin()) {

				World.sendWorldYellMessage("[" + color + "Admin</col>] <img=1>" + text, player);
				return;
			}
			if (player.isCommunityManager()) {
				World.sendWorldYellMessage("[" + color + "Community Manager</col>] <img=22>" + text, player);
				return;
			}
			if (player.isForumManager()) {
				World.sendWorldYellMessage("[" + color + "Discord Manager</col>] <img=21>" + text, player);
				return;
			}
			if (player.getRights() == 1 || player.isMod()) {
				World.sendWorldYellMessage("[" + color + "Mod</col>] <img=0>" + text, player);
				return;
			}
			if (player.isSupport()) {
				World.sendWorldYellMessage("[" + color + "Support</col>] <img=13>" + player.getIcon() + text, player);
				return;
			}
			if (player.isWiki()) {
				World.sendWorldYellMessage("[" + "<col=C96800>Wiki</col>" + "] " + "<img=23>" + text, player);
				return;
			}
			if (player.isDiamond()) {
				World.sendWorldYellMessage(
						"[" + Colors.blue + "<col=05FFC9>Diamond Donor</col>" + "] " + "<img=19>" + text, player);
				return;
			}
			if (player.isPlatinum()) {
				World.sendWorldYellMessage(
						"[" + Colors.purple + "<col=697998>Platinum Donor</col>" + "] " + "<img=12>" + text, player);
				return;
			}
			if (player.isGold()) {
				World.sendWorldYellMessage("[" + Colors.yellow + "Gold Donor</col>" + "] " + "<img=8>" + text, player);
				return;
			}
			if (player.isSilver()) {
				World.sendWorldYellMessage("[" + Colors.gray + "Silver Donor</col>" + "] " + "<img=10>" + text, player);
				return;
			}
			if (player.isBronze()) {
				World.sendWorldYellMessage("[" + "<col=C96800>Bronze Donor</col>" + "] " + "<img=9>" + text, player);
				return;
			}

			return;
		}

	}
}