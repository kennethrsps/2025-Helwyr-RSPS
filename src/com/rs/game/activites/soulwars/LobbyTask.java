package com.rs.game.activites.soulwars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;

import com.rs.game.activites.soulwars.SoulWarsManager.Teams;
import com.rs.game.player.Player;

/**
 * 
 * @author Savions Sw
 * 
 */
public class LobbyTask extends TimerTask {

	private ArrayList<Player> players = new ArrayList<Player>(500);
	
	@Override
	public void run() {
		try {
			for (Iterator<Player> it = players.iterator(); it.hasNext();) {
				Player player = it.next();
				if (player != null && player.getControlerManager().getControler() instanceof LobbyController)
					player.getControlerManager().sendInterfaces();
				else
					it.remove();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public ArrayList<Player> getPlayers(Teams team) {
		ArrayList<Player> members = new ArrayList<Player>(players.size());
		for (Player player : players) {
			if (player != null) {
				final int cape = player.getEquipment().getCapeId()
						- SoulWarsManager.TEAM_CAPE_INDEX;
				if (cape < 0 || cape > 1)
					continue;
				if (Teams.values()[cape].equals(team))
					members.add(player);
			}
		}
		return members;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
}