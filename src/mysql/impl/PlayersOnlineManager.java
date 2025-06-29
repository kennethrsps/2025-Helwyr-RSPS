package mysql.impl;

import java.io.IOException;
import java.net.URL;

import com.rs.Settings;
import com.rs.game.World;
//import com.rs.utils.Logger;

/**
 * Updates Players online on the website.
 * @author Zeus
 */
public class PlayersOnlineManager {

	/**
	 * Connects to the link.
	 * @param amount Players online.
	 * @throws IOException throws if can't connect.
	 */
	private static void setWebsitePlayersOnline(int amount) throws IOException {
		URL url = new URL("http://Helwyr.com/playercount.php?key=da2Xkw3key&amount=" + amount);
		url.openStream().available();
		 System.err.println("Error updating players online - website.");
	}

	/**
	 * Updates the players online.
	 */
	public static void updatePlayersOnline() {
		if (!Settings.SQL_ENABLED)
			return;
		try {
			setWebsitePlayersOnline(World.getPlayersOnline());
		} catch (Throwable e) {
			e.printStackTrace();
			System.err.println("Error updating players online - website.");
			Settings.SQL_ENABLED = false;
		}
	}
}