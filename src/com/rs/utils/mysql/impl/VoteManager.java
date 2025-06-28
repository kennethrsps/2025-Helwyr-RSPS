package com.rs.utils.mysql.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.topweeks.TopWeeklyVoters;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.Utils;
import com.rs.utils.VoteHiscores;

/**
 * Used to handle Vote rewards.
 * 
 * @author Noel
 */
public class VoteManager implements Runnable {

	/**
	 * Database creditentials.
	 */
	public static final String USER = "", PASS = "!", DATABASE = "", HOST = "";

	/**
	 * An Integer containing all server votes.
	 */
	public static int VOTES;
	public static boolean totalGot = false;

	/**
	 * A Byte representing how much parties have been thrown.
	 */
	public static byte PARTIES;

	private Player player;
	private Connection conn;
	private Statement stmt;

	/**
	 * Constructs the class.
	 * 
	 * @param player
	 *            The player to attach to.
	 */
	public VoteManager(Player player) {
		this.player = player;
	}

	@Override
	public void run() {
		try {
			if (player == null)
				return;
			if (!Settings.SQL_ENABLED) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"Your requested process could not be handled at this time; please try again later");
				return;
			}
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Checking for rewards, this might take a while...");
			if (!connect(HOST, DATABASE, USER, PASS))
				return;
			String name = player.getUsername().toLowerCase().replaceAll(" ", "_");

			ResultSet rs = executeQuery(
					"SELECT * FROM fx_votes WHERE username='" + name + "' AND claimed=0 AND callback_date IS NOT NULL");

			while (rs.next()) {
				String timestamp = rs.getTimestamp("callback_date").toString();
				String ipAddress = rs.getString("ip_address");
				int siteId = rs.getInt("site_id");
				handleQueuedReward(player);
				Logger.log(
						"Vote by " + name + ". (sid: " + siteId + ", ip: " + ipAddress + ", time: " + timestamp + ")");

				rs.updateInt("claimed", 1); // do not delete otherwise they can re-claim!
				rs.updateRow();
			}
			player.sendMessage("There are no more votes for you to claim.");
			destroy();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("VoteManager: ERROR CONNECTING, Disable SQL to prevent lagg..");
			player.sendMessage("ERROR 910. Please contact an Administrator!");
		}
		player.getInterfaceManager().closeChatBoxInterface();
	}

	/**
	 * Connects to the Database.
	 * 
	 * @param host
	 *            Host address.
	 * @param database
	 *            Database name.
	 * @param user
	 *            Username.
	 * @param pass
	 *            Password.
	 * @return if Connected.
	 */
	public boolean connect(String host, String database, String user, String pass) {
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, pass);
			return true;
		} catch (SQLException e) {
			System.err.println("Failing connecting to database!");
			return false;
		}
	}

	/*
	 * Gets total amount of votes from database integer
	 */

	/**
	 * Destroys database connection.
	 */
	public void destroy() {
		try {
			conn.close();
			conn = null;
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Executes database update.
	 * 
	 * @param query
	 *            The query to send.
	 * @return result as Integer.
	 */
	public int executeUpdate(String query) {
		try {
			this.stmt = this.conn.createStatement(1005, 1008);
			int results = stmt.executeUpdate(query);
			return results;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return -1;
	}

	/**
	 * Executes the query.
	 * 
	 * @param query
	 *            The query to send.
	 * @return results.
	 */
	public ResultSet executeQuery(String query) {
		try {
			this.stmt = this.conn.createStatement(1005, 1008);
			ResultSet results = stmt.executeQuery(query);
			return results;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * Handles the Vote Reward.
	 * 
	 * @param player
	 *            The player being rewarded.
	 */
	public static long PARTY_DXP;

	public static boolean gotDXP() {
		return PARTY_DXP > Utils.currentTimeMillis();
	}

	public static void handleQueuedReward(Player player) {
		VOTES++;
		player.setVotes(player.getVotes() + 1);
		TopWeeklyVoters.addVote(player);
		player.sendMessage("We've recorded a vote for your account, enjoy your reward; " + "(total votes: " + Colors.red
				+ Utils.getFormattedNumber(player.getVotes()) + "</col>).", true);
		player.addItem(new Item(11640));
		player.getAchManager().addKeyAmount("voters", 1);
		player.getAchManager().addKeyAmount("v450", 1);
		player.setVotePoints(player.getVotePoints() + 1);
		World.setLastVoter(player.getDisplayName());
		VoteHiscores.checkRank(player);

		/** SoF tickets **/
		if (Utils.random(100) < 33 && !player.isIronMan() && !player.isHCIronMan())
			player.addItem(new Item(24154));

		if (VOTES % 5 == 0 && VOTES > 0) {
			World.sendWorldMessage("<img=6><col=008FB2>Total of [" + Colors.red + Utils.getFormattedNumber(VOTES)
					+ "<col=008FB2>] votes have been claimed! " + "Vote now using the ;;vote command!", false);

			/** Custom Vote Party every 100 claimed votes **/
			if (VOTES % 100 == 0) {
				PARTIES++;
				World.edelarParty(); /** Partying for 4 seconds **/
				PARTY_DXP = (Utils.currentTimeMillis() + 1800000);
				int players = World.getPlayers().size();
				World.sendWorldMessage("<img=6><col=008FB2>[Vote Party] "
						+ (players == 1 ? "You" : "You and " + (players - 1) + " others") + " " + "have received "
						+ (PARTIES == 1 ? "an extra vote book" : PARTIES + " extra vote books") + " and "
						+ ((World.isWellActive() || World.isWeekend()) ? "10% XP boost" : "x2 XP") + "!", false);
				World.addItemsAll(new Item(11640, PARTIES));
			}
		}
	}
}