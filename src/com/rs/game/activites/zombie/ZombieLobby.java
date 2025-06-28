package com.rs.game.activites.zombie;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Utils;

public class ZombieLobby {

	private static List<Player> lobby = new ArrayList<Player>();
	public static int MAX_PLAYERS = 10, MIN_PLAYERS = 3;
	private static final int TIME = 90;
	private static LobbyTimer timer;

	public static class LobbyTimer extends TimerTask {

		private int seconds = TIME;

		@Override
		public void run() {
			if (lobby.size() == 0) {
				cancel();
				return;
			}
			if (seconds <= 0) {
				if (lobby.size() >= MIN_PLAYERS)
					startZombieMiniGame();
				seconds = TIME;
				refreshLobbyInterface();
			}
			seconds--;
			if (seconds % 5 == 0)
				refreshLobbyInterface();
		}

		public int getSeconds() {
			return seconds;
		}
	}

	public static void enterLobby(Player player) {
		if (lobby.size() == 0)
			CoresManager.fastExecutor.schedule(timer = new LobbyTimer(), 1000, 1000);
		player.getControlerManager().startControler("ZombieLobbyControler");
		add(player);
		player.useStairs(-1, new WorldTile(2141, 5525, 3), 1, 2, "You enter the zombies minigame lobby.");
	}

	public static void add(Player player) {
		lobby.add(player);
		refreshLobbyInterface();
	}

	public static void remove(Player player) {
		lobby.remove(player);
		refreshLobbyInterface();
	}

	public static void refreshLobbyInterface() {
		for (Player p : lobby) {
			p.getControlerManager().getControler().sendInterfaces();
			p.getPackets().sendIComponentText(407, 3, "");
			p.getPackets().sendIComponentText(407, 13, "Time left:" + timer.getSeconds() + " seconds.");
			p.getPackets().sendIComponentText(407, 14, "Players Ready:" + ZombieLobby.getPlayers().size());
			p.getPackets().sendIComponentText(407, 15,
					(ZombieLobby.getPlayers().size() >= MAX_PLAYERS) ? "(Reached max amount of players)"
							: ("(Need " + ((ZombieLobby.getPlayers().size() >= MIN_PLAYERS)
									? "0 to " + (MAX_PLAYERS - ZombieLobby.getPlayers().size())
									: (MIN_PLAYERS - ZombieLobby.getPlayers().size()) + " to "
											+ (MAX_PLAYERS - ZombieLobby.getPlayers().size()))
									+ " players)"));
			p.getPackets().sendIComponentText(407, 16,
					"Zombies Points:" + Integer.toString(p.getZombiesMinigamePoints()));
			p.getPackets().sendHideIComponent(407, 17, true);
			p.getPackets().sendHideIComponent(407, 18, true);
			p.getInterfaceManager().sendOverlay(407, false);
		}
	}

	public static void exitLooby(Player player) {
		player.useStairs(-1, Settings.RESPAWN_PLAYER_LOCATION, 1, 2, "You leave the lobby.");
		remove(player);
	}

	private static void startZombieMiniGame() {
		final List<Player> playerList = new ArrayList<Player>();
		final List<Player> leftOut = new ArrayList<Player>();
		for (int i = 0; i < lobby.size(); i++) {
			Player player = lobby.get(i);
			if (player == null || player.isDead())
				continue;
			if (playerList.size() >= MAX_PLAYERS)
				leftOut.add(player);
			else
				playerList.add(player);
		}
		lobby.clear();
		for (Player player : leftOut) {
			if (player == null || player.isDead())
				continue;
			player.getPackets().sendGameMessage("You have to wait for next round max players reached.");
			lobby.add(player);
		}
		new ZombieMinigame(playerList).create();
	}

	public static boolean canEnter(Player player) {
		if (player.getPet() != null || player.getFamiliar() != null) {
			player.getPackets().sendGameMessage("You can't take a follower into the lobby!");
			return false;
		}
		long currentTime = Utils.currentTimeMillis();
		if (player.getLockDelay() > currentTime)
			return false;
		if (player.getX() >= 2956 && player.getX() <= 3067 && player.getY() >= 5512 && player.getY() <= 5630
				|| (player.getX() >= 2756 && player.getX() <= 2875 && player.getY() >= 5512 && player.getY() <= 5627)) {
			player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
			return false;
		}
		if (!player.getControlerManager().processMagicTeleport(player))
			return false;
		if (!player.getControlerManager().processItemTeleport(player))
			return false;
		if (!player.getControlerManager().processObjectTeleport(player))
			return false;
		for (Player p : lobby) {
			if (p == null)
				continue;
			if (p.getSession().getIP().equalsIgnoreCase(player.getSession().getIP())
					|| p.getCurrentMac().equalsIgnoreCase(player.getCurrentMac())) {
				player.getPackets().sendGameMessage("Can not enter found another player with the same ip in lobby.");
				return false;
			}
		}
		enterLobby(player);
		player.getControlerManager().magicTeleported(1);
		return true;
	}

	public static List<Player> getPlayers() {
		return lobby;
	}

	public static LobbyTimer getTimer() {
		return timer;
	}
}
