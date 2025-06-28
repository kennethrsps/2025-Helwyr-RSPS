/*package mysql.impl;

import java.sql.PreparedStatement;

import com.rs.Settings;
import com.rs.game.player.Player;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

import mysql.Database;
 
*//**
 * Handles updating of Website News.
* @author Zeus
 *//*
public class NewsManager implements Runnable {

	*//**
	 * The player to update.
	 *//*
	private Player player;
	
	*//**
	 * The XP-Mode hiscores to update.
	 *//*
	private String message;
	
	*//**
	 * Creates it.
	 * @param player The player.
	 * @param table The XP-Mode table.
	 *//*
	public NewsManager(Player player, String message) {
		this.player = player;
		this.message = message;
	}
	
	@Override
	public void run() {
		if (!Settings.SQL_ENABLED)
			return;
		if (player == null || player.isDeveloper())
			return;
		
		String name = Utils.formatPlayerNameForDisplay(player.getUsername());
		
		try {

			Database db = new Database("", "", "", "");
			
			if (!db.init()) {
				System.err.println("Failed updating "+name+" website news feed.. Database could not connect.");
				return;
			}
			
			PreparedStatement stmt2 = db.prepare("INSERT INTO message (username,news) VALUES (?, ?)");
			stmt2.setString(1, name);
			stmt2.setString(2, message);
			stmt2.execute();
			stmt2.close();
			db.destroyAll();
			
			if (Settings.DEBUG)
				Logger.log("Successfully sent "+name+": "+message+".");
			
			try {
				finalize();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			Settings.SQL_ENABLED = false;
			System.err.println("Could not update News; turn off SQL to prevent lag!");
			Logger.log("Unable to send "+name+": "+message+".");
			e.printStackTrace();
		}
	}
}*/