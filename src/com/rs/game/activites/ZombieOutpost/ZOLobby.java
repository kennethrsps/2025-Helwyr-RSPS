package com.rs.game.activites.ZombieOutpost;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class ZOLobby {
	
	public static long startCounter;
	public static NPC announcer;
	
	public static void initiateTick() {
		startCounter = 120000;
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					tickLobby();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}
	
	public static void tickLobby() {
		int ready = getPlayersReady().size();
		for(Player player : World.getPlayers()) {
			if(ZOLobby.inLobby(player)) {
				sendLobbyInterface(player, ready);
			} else {
				if(player.getInterfaceManager().containsInterface(812)) {
					player.getInterfaceManager().closeOverlay(player.getInterfaceManager().hasRezizableScreen());
				}
			}
		}
		if(ZOManager.gameActive) {
			startCounter = 120000;
		} else if(getPlayersReady().size() > 0) {
			startCounter -= 1000;
		}
		if(startCounter <= 0 && !ZOManager.gameActive) {
			ZOGame.startNewGame();
		}
	}
	
	public static void sendLobbyInterface(Player player, int ready) {
		if(!player.getInterfaceManager().containsInterface(812))
			player.getInterfaceManager().sendOverlay(812, player.getInterfaceManager().hasRezizableScreen());
		player.getPackets().sendIComponentText(812, 3, Colors.white+"Status: ");
		player.getPackets().sendIComponentText(812, 4, Colors.white+"Players ready: ");
		player.getPackets().sendIComponentText(812, 5, ""+getStatus());
		player.getPackets().sendIComponentText(812, 6, Colors.white+""+ready);
		player.getPackets().sendIComponentText(812, 7, ""+getMiscMessage(player));
	}
	
	public static String getMiscMessage(Player player) {
		if(player.getEquipment().wearingArmour()) {
			return Colors.red+"You're not ready - Please bank armour.";
		}
		if(player.getInventory().getFreeSlots() != 28) {
			return Colors.red+"You're not ready - Please bank inventory.";
		}
		return Colors.green+"You're ready - please wait!";
	}
	
	public static String getStatus() {
		if(ZOManager.gameActive) {
			return Colors.red+"Game is active! Runtime: "+Utils.formatTime(ZOGame.gameTime);
		}
		if(startCounter > 0) {
			return Colors.green+"Starting in "+Utils.formatTime(startCounter);
		}
		return Colors.orange+"Ready, waiting for green flag...";
	}
	
	public static List<Player> getPlayersReady() {
		List<Player> list = new ArrayList<Player>();
		for(Player player : World.getPlayers()) {
			if(ZOLobby.inLobby(player)) {
				if(!player.getEquipment().wearingArmour()) {
					if(player.getInventory().getFreeSlots() == 28) {
						list.add(player);
					}
				}
			}
		}
		return list;
	}

	public static void spawnHomeLobbyObjects() {
		/**
		 * LOBBY
		 */
		World.spawnObject(new WorldObject(69107, 10, 1, 4309, 850, 0), true);
		World.spawnObject(new WorldObject(69107, 10, 1, 4309, 851, 0), true);
		World.spawnObject(new WorldObject(69107, 10, 0, 4308, 852, 0), true);
		World.spawnObject(new WorldObject(69107, 10, 0, 4306, 852, 0), true);
		World.spawnObject(new WorldObject(69107, 10, 3, 4305, 851, 0), true);
		World.spawnObject(new WorldObject(69107, 10, 3, 4305, 850, 0), true);
		/**
		 * STATIC GAME OBJECTS
		 */
		World.spawnObject(new WorldObject(82224, 10, 2, 5536, 4294, 0), true);
		/**
		 * NPC
		 */
		announcer = new NPC(15055, new WorldTile(5537, 4295, 0), -1, false, true);
		announcer.setRandomWalk(0);
	}
	
	public static boolean inLobby(Player player) {
		return player.getX() >= 4306 && player.getY() >= 850 && player.getX() <= 4308 && player.getY() <= 851;
	}
	
}
