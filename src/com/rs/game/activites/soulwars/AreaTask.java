package com.rs.game.activites.soulwars;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;

import com.rs.game.World;
import com.rs.game.player.Player;

/**
 * 
 * @author Savions Sw
 *
 */
public class AreaTask extends TimerTask {

	private ArrayList<Player> players = new ArrayList<Player>(500);
	
	@Override
	public void run() {
		try {
			if (World.soulWars.decrementMinute()) {
				for (Iterator<Player> it = players.iterator(); it.hasNext();) {
					Player player = it.next();
					if (player != null
							&& !player.hasFinished()
							&& player.getControlerManager().getControler() instanceof AreaController)
						player.getControlerManager().sendInterfaces();
					else
						it.remove();
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}
}