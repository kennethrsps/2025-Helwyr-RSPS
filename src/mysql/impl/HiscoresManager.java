package mysql.impl;

import java.sql.PreparedStatement;

import com.rs.Settings;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

import mysql.Database;
 
/**
 * Handles updating of website Hiscores.
 * @author Zeus
 */
public class HiscoresManager implements Runnable {

	/**
	 * The player to update.
	 */
	private Player player;
	
	/**
	 * The XP-Mode hiscores to update.
	 */
	private String table;
	
	/**
	 * Creates it.
	 * @param player The player.
	 * @param table The XP-Mode table.
	 */
	public HiscoresManager(Player player, String table) {
		this.player = player;
		this.table = table;
	}
	
	@Override
	public void run() {
		if (!Settings.SQL_ENABLED)
			return;
		try {
			/*if (player.getSkills().getTotalLevel(player) < 50)
				return;
			if (player.isDeveloper())
				return;*/
			String name = Utils.formatString(player.getUsername());
			
			Database db = new Database("", "", "", "");
			
			if (!db.init()) {
				System.err.println("Failing to update "+name+" hiscores.. Database could not connect.");
				return;
			}
			
			PreparedStatement stmt1 = db.prepare("DELETE FROM "+table+" WHERE username=?");
			stmt1.setString(1, name);
			stmt1.execute();
			
			int gameMode = 0;
			
			if(player.isEasy()) {
				gameMode = 0;
			} else if(player.isIntermediate()) {
				gameMode = 1;
			} else if(player.isVeteran()) {
				gameMode = 2;
			} else if(player.isIronMan()) {
				gameMode = 4;
			} else if(player.isExpert()) {
				gameMode = 3;
			}
				
			PreparedStatement stmt2 = db.prepare(generateQuery());
			stmt2.setString(1, name);
			stmt2.setInt(2, getRights());
			stmt2.setInt(3, gameMode);
			stmt2.setLong(4, player.getSkills().getTotalXp());
			
			for (int i = 0; i < Skills.SKILL_NAME.length; i++)
				stmt2.setInt(5 + i, (int)player.getSkills().getXp()[i]);
			stmt2.execute();
			db.destroyAll();
			
			if (Settings.DEBUG)
				Logger.log("Successfully updated "+table+" for "+player.getDisplayName()+".");

			try {
				finalize();
			} catch (Throwable e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			//Settings.SQL_ENABLED = false;
			System.err.println("Could not update hiscores; turn SQL off to prevent lag!");
			if (Settings.DEBUG) {
				Logger.log("Unable to update "+table+" for "+player.getDisplayName()+".");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Gets players rights to an Integer.
	 * @return the rights.
	 */
	private int getRights() {
		if (player.getRights() == 2)
			return 2;
		if (player.getRights() == 1)
			return 1;
		if (player.isDiamond())
			return 7;
		if (player.isPlatinum())
			return 6;
		if (player.isGold())
			return 5;
		if (player.isSilver())
			return 4;
		if (player.isBronze())
			return 3;
		return 0;
	}
	
	/**
	 * Generates the query to a String.
	 * @return the String to generate.
	 */
	private String generateQuery() {
		StringBuilder sb = new StringBuilder();
		sb.append("INSERT INTO "+table+" (");
		sb.append("username, ");
		sb.append("rights, ");
		sb.append("game_mode, ");
		sb.append("overall_xp, ");
		sb.append("attack_xp, ");
		sb.append("defence_xp, ");
		sb.append("strength_xp, ");
		sb.append("constitution_xp, ");
		sb.append("ranged_xp, ");
		sb.append("prayer_xp, ");
		sb.append("magic_xp, ");
		sb.append("cooking_xp, ");
		sb.append("woodcutting_xp, ");
		sb.append("fletching_xp, ");
		sb.append("fishing_xp, ");
		sb.append("firemaking_xp, ");
		sb.append("crafting_xp, ");
		sb.append("smithing_xp, ");
		sb.append("mining_xp, ");
		sb.append("herblore_xp, ");
		sb.append("agility_xp, ");
		sb.append("thieving_xp, ");
		sb.append("slayer_xp, ");
		sb.append("farming_xp, ");
		sb.append("runecrafting_xp, ");
		sb.append("hunter_xp, ");
		sb.append("construction_xp, ");
		sb.append("summoning_xp, ");
		sb.append("dungeoneering_xp, ");
		sb.append("divination_xp, ");
		sb.append("invention_xp)");
		sb.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		return sb.toString();
	}
}