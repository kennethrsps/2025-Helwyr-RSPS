package com.rs.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.Protocol;
import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.content.grandExchange.GrandExchange;

/**
 * A class used to write Logs about various player actions.
 * 
 * @author Zeus
 */
public class LoggingSystem {

	/**
	 * Logs Players IP address.
	 * 
	 * @param player
	 *            The player to Log.
	 */
	public static void logIP(Player player) {
		if (!Settings.DEBUG) {
			String FILE_PATH = Protocol.LOGS_PATH + "/iplogs/";
			player.setLastIP(player.getSession().getIP());
			try {
				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
				Calendar cal = Calendar.getInstance();
				BufferedWriter writer = new BufferedWriter(
						new FileWriter(FILE_PATH + player.getUsername() + ".txt", true));
				writer.write("[" + dateFormat.format(cal.getTime()) + "]: " + "Display name: " + player.getDisplayName()
						+ " (Username: " + player.getUsername() + ") " + "- IP: " + player.getSession().getIP() + "; "
						+ "Current MAC: " + player.getCurrentMac() + " " + "(Registered MAC: "
						+ player.getRegisteredMac() + ").");
				writer.newLine();
				writer.flush();
				writer.close();
			} catch (IOException e) {
				Logger.log(player, e);
			}
		}
	}

	/**
	 * Logs a Private chat message.
	 * 
	 * @param player
	 *            The player to Log.
	 * @param p2
	 *            The player to send the Private message.
	 * @param message
	 *            The message to Log.
	 */
	public static void logPM(Player player, Player p2, String fixChatMessage) {
		if (Settings.DEBUG)
			return;
		String FILE_PATH = Protocol.LOGS_PATH + "/pmlogs/";
		try {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + player.getUsername() + ".txt", true));
			writer.write("[" + dateFormat.format(cal.getTime()) + ", IP: " + player.getSession().getIP() + "] : "
					+ fixChatMessage + ". To user: " + p2.getUsername());
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	/**
	 * Logs a Public chat message.
	 * 
	 * @param player
	 *            The player to Log.
	 * @param message
	 *            The message to Log.
	 */
	public static void logPublicChat(Player player, String message) {
		if (Settings.DEBUG)
			return;
		String FILE_PATH = Protocol.LOGS_PATH + "/chatlogs/";
		try {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + player.getUsername() + ".txt", true));
			writer.write(
					"[" + dateFormat.format(cal.getTime()) + ", IP: " + player.getSession().getIP() + "] : " + message);
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	/**
	 * Logs a Trade session between the Traders.
	 * 
	 * @param player
	 *            The player 1 trading.
	 * @param oldTarget
	 *            The player 2 trading.
	 * @param containedItems
	 *            The traded items.
	 */
	public static void logTrade(Player player, Player oldTarget, CopyOnWriteArrayList<Item> containedItems) {
		if (Settings.DEBUG)
			return;
		try {
			if (containedItems == null)
				return;
			String FILE_PATH = Protocol.LOGS_PATH + "/tradelogs/";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(
					new FileWriter(FILE_PATH + oldTarget.getUsername() + ".txt", true));
			writer.write("[Trade session started]");
			writer.newLine();
			writer.write("Trader Information: Username: " + oldTarget.getUsername() + ". IP "
					+ oldTarget.getSession().getIP() + ". Current Mac: " + oldTarget.getCurrentMac() + ". Location: "
					+ oldTarget.getX() + ", " + oldTarget.getY() + ", " + oldTarget.getPlane() + ".");
			writer.newLine();
			writer.write("Player Information: Username: " + player.getUsername() + ". IP: " + player.getLastIP()
					+ ". Current Mac: " + player.getCurrentMac() + ". Location: " + player.getX() + ", " + player.getY()
					+ ", " + player.getPlane() + ".");
			writer.newLine();
			writer.write("Time: [" + dateFormat.format(cal.getTime()) + "]");
			for (Item item : containedItems) {
				if (item == null)
					continue;
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(item.getId());
				String name = defs == null ? "" : defs.getName().toLowerCase();
				writer.newLine();
				writer.write(oldTarget.getUsername() + " Gave: " + name + ", amount: "
						+ Utils.getFormattedNumber(item.getAmount()));

				if (GrandExchange.getPrice(item.getId()) >= 5000000
						|| (item.getId() == 995 && item.getAmount() >= 5000000))
					World.sendWorldMessage("<img=7><col=ff0000>Trade Log: " + oldTarget.getDisplayName() + " traded "
							+ player.getDisplayName() + ": " + name + " X: "
							+ Utils.getFormattedNumber(item.getAmount()) + ".", true);
			}
			writer.newLine();
			writer.write("[Trade session ended]");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	/**
	 * Logs when the player drops an item.
	 */
	public static void logItemDrop(Player player, Item item, WorldTile tile) {
		if (Settings.DEBUG)
			return;
		String FILE_PATH = Protocol.LOGS_PATH + "/droplogs/";
		try {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + player.getUsername() + ".txt", true));
			writer.write("[" + dateFormat.format(cal.getTime()) + ", IP: " + player.getSession().getIP()
					+ "] : dropped item: " + item.getAmount() + " x " + item.getName() + " at coords: " + tile.getX()
					+ ", " + tile.getY() + ", " + tile.getPlane() + ".");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	/**
	 * Logs when the player picks up an item.
	 */
	public static void logItemPickup(Player player, Item item, WorldTile tile) {
		if (Settings.DEBUG)
			return;
		String FILE_PATH = Protocol.LOGS_PATH + "/itempickuplogs/";
		try {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + player.getUsername() + ".txt", true));
			writer.write("[" + dateFormat.format(cal.getTime()) + ", IP: " + player.getSession().getIP()
					+ "] : picked up item: " + item.getAmount() + " x " + item.getName() + " at coords: " + tile.getX()
					+ ", " + tile.getY() + ", " + tile.getPlane() + ".");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	/**
	 * Logs when the player claims their G.E. items.
	 */
	public static void logGrandExchange(Player player, Item item) {
		if (Settings.DEBUG)
			return;
		String FILE_PATH = Protocol.LOGS_PATH + "/grandexchangelogs/";
		try {
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + player.getUsername() + ".txt", true));
			writer.write("[" + dateFormat.format(cal.getTime()) + ", IP: " + player.getSession().getIP()
					+ "] : collected an item: " + item.getAmount() + " " + "x " + item.getName() + ".");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	/**
	 * Logs Duel Arena stakes.
	 * 
	 * @param player
	 *            to Log.
	 * @param victor
	 *            The winner.
	 * @param wonItems
	 *            Items that have been won.
	 */
	public static void logDuelStake(Player player, Player victor, CopyOnWriteArrayList<Item> wonItems) {
		try {
			String FILE_PATH = Protocol.LOGS_PATH + "/stakelogs/";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + victor.getUsername() + ".txt", true));
			writer.write("[Stake session started]");
			writer.newLine();
			writer.write("Stake Winner Information: Username: " + victor.getUsername() + ". IP "
					+ victor.getSession().getIP() + ". Current Mac: " + victor.getCurrentMac() + ". Location: "
					+ victor.getX() + ", " + victor.getY() + ", " + victor.getPlane() + ".");
			writer.newLine();
			writer.write("Stake Loser Information: Username: " + player.getUsername() + ". IP: " + player.getLastIP()
					+ ". Current Mac: " + player.getCurrentMac() + ". Location: " + player.getX() + ", " + player.getY()
					+ ", " + player.getPlane() + ".");
			writer.newLine();
			writer.write("Time: [" + dateFormat.format(cal.getTime()) + "]");
			for (Item item : wonItems) {
				if (item == null)
					continue;
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(item.getId());
				String name = defs == null ? "" : defs.getName().toLowerCase();
				writer.newLine();
				writer.write(victor.getUsername() + " won " + Utils.getFormattedNumber(item.getAmount()) + " x " + name
						+ " from " + player.getUsername() + ".");

				if (GrandExchange.getPrice(item.getId()) >= 50000000
						|| (item.getId() == 995 && item.getAmount() >= 50000000))
					World.sendWorldMessage("<img=7><col=ff0000>Stake Log: " + victor.getUsername() + " won "
							+ Utils.getFormattedNumber(item.getAmount()) + " x " + name + " from "
							+ player.getUsername() + ".", true);
			}
			writer.newLine();
			writer.write("[Stake session ended]");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(player, e);
		}
	}

	/**
	 * Logs Duel Arena lost stakes.
	 * 
	 * @param player
	 *            the Looser.
	 * @param victor
	 *            The winner.
	 * @param wonItems
	 *            Items that have been lost.
	 */
	public static void logLostDuelStake(Player loser, Player victor, CopyOnWriteArrayList<Item> lostItems) {
		try {
			String FILE_PATH = Protocol.LOGS_PATH + "/stakelogs/";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH + loser.getUsername() + ".txt", true));
			writer.write("[Stake session started]");
			writer.newLine();
			writer.write("Stake Winner Information: Username: " + victor.getUsername() + ". IP "
					+ victor.getSession().getIP() + ". Current Mac: " + victor.getCurrentMac() + ". Location: "
					+ victor.getX() + ", " + victor.getY() + ", " + victor.getPlane() + ".");
			writer.newLine();
			writer.write("Stake Loser Information: Username: " + loser.getUsername() + ". IP: " + loser.getLastIP()
					+ ". Current Mac: " + loser.getCurrentMac() + ". Location: " + loser.getX() + ", " + loser.getY()
					+ ", " + loser.getPlane() + ".");
			writer.newLine();
			writer.write("Time: [" + dateFormat.format(cal.getTime()) + "]");
			for (Item item : lostItems) {
				if (item == null)
					continue;
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(item.getId());
				String name = defs == null ? "" : defs.getName().toLowerCase();
				writer.newLine();
				writer.write(loser.getUsername() + " lost " + Utils.getFormattedNumber(item.getAmount()) + " x " + name
						+ " to " + victor.getUsername() + ".");
			}
			writer.newLine();
			writer.write("[Stake session ended]");
			writer.newLine();
			writer.flush();
			writer.close();
		} catch (IOException e) {
			Logger.log(loser, e);
		}
	}

	/**
	 * Logs everything player-address related.
	 * 
	 * @param player
	 *            The player to log.
	 */
	public static void logAddress(Player player) {
		try {
			String FILE_PATH = Protocol.LOGS_PATH + "/AddressLogs.txt";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			BufferedWriter bf = new BufferedWriter(new FileWriter(FILE_PATH, true));
			bf.write("[" + dateFormat.format(cal.getTime()) + "]: Display name: " + player.getDisplayName()
					+ " (Username: " + player.getUsername() + ") " + "- IP: " + player.getSession().getIP()
					+ "; Current MAC: " + player.getCurrentMac() + " (Registered MAC: " + player.getRegisteredMac()
					+ ").");
			bf.newLine();
			bf.flush();
			bf.close();
		} catch (IOException ignored) {
			Logger.log("LoggingSystem", "Failed 'handleIP(player)'");
		}
	}
}