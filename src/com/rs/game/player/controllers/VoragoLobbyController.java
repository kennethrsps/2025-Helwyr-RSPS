/*package com.rs.game.player.controllers;

import java.util.LinkedList;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.ForceMovement;
import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.vorago.VoragoHandler;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;

*//**
 * Handles the Voragos lobby controller.
 * 
 * @author Zeus
 *//*
public class VoragoLobbyController extends Controller {

	@Override
	public void forceClose() {
		remove();
	}

	@Override
	public boolean logout() {
		removePlayer(player);
		return false;
	}

	@Override
	public boolean login() {
		addPlayer(player);
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		remove();
		removeControler();
	}

	*//**
	 * Removes player from lobby.
	 *//*
	public void remove() {
		removePlayer(player);
		removeControler();
	}

	@Override
	public boolean sendDeath() {
		remove();
		removeControler();
		return true;
	}

	@Override
	public void start() {
		addPlayer(player);
	}

	*//**
	 * Adds player to lobby.
	 * 
	 * @param player
	 *            The player to add.
	 *//*
	public static void addPlayer(final Player player) {
		synchronized (players) {
			players.add(player);
		}
	}

	*//**
	 * Removes player from lobby.
	 * 
	 * @param player
	 *            The player to remove.
	 *//*
	public static void removePlayer(Player player) {
		synchronized (players) {
			players.remove(player);
		}
	}

	*//**
	 * Gets players in lobby.
	 * 
	 * @return amount of players.
	 *//*
	public static List<Player> getPlayers() {
		return players;
	}

	*//**
	 * List of all players in the lobby.
	 *//*
	private static final List<Player> players = new LinkedList<Player>();

	*//**
	 * Handles Voragos lobby and starts the fight.
	 * 
	 * @return if starting fight.
	 *//*
	public static void startFight() {

		// Pass all players to the fight
		VoragoHandler.startedChallenge = true;

		for (Player p : getPlayers()) {
			WorldTasksManager.schedule(new WorldTask() {
				int count = 0;

				@Override
				public void run() {
					switch (count) {
					case 5:
						p.sendMessage(Colors.red + "[Vorago] You get hit by Vorago's raw power.");
						p.applyHit(new Hit(null, 500, HitLook.CRITICAL_DAMAGE));
						p.setNextAnimation(new Animation(10070));
						break;
					case 6:
						if (p.getHitpoints() < 1) {
							stop();
							VoragoHandler.startedChallenge = false;
							return;
						}
						break;
					case 7:
						removePlayer(p); // remove from lobby list
						p.setNextAnimation(new Animation(20389));
						WorldTile toTile = p.transform(0, -8, 0);
						p.setNextForceMovement(new ForceMovement(new WorldTile(p), 1, toTile, 2, ForceMovement.SOUTH));
						break;
					case 8:
						p.getControlerManager().startControler("VoragoController");
						p.setNextAnimation(new Animation(-1));
						VoragoHandler.startedChallenge = false;
						stop();
						break;
					}
					count++;
				}
			}, 0, 1);
		}
	}
}*/