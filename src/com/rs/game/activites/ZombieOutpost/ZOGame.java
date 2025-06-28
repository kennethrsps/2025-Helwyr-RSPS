package com.rs.game.activites.ZombieOutpost;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class ZOGame {
	
	public static long gameTime;
	public static int waveCount;
	public static long waveCountdown;
	public static List<NPC> zombies;
	public static List<Player> players;
	public static List<WallObject> walls;
	public static List<ShrineObject> shrines;
	public static List<TowerObject> towers;
	
	public static void tickGame() {
		if(!ZOManager.gameActive) {
			return;
		}
		for(Player player : players) {
			if(World.getPlayer(player.getUsername()) == null) {
				players.remove(player);
				continue;
			}
			sendInterface(player);
		}
		if(players.isEmpty()) {
			endGame();
		}
		ShrineObject.tickShrines();
		gameTime += 1000;
		
	}
	
	public static void startNewGame() {
		gameTime = 0;
		waveCount = 0;
		waveCountdown = 120000;
		zombies = new CopyOnWriteArrayList<NPC>();
		players = new CopyOnWriteArrayList<Player>();
		walls = new CopyOnWriteArrayList<WallObject>();
		shrines = new CopyOnWriteArrayList<ShrineObject>();
		towers = new CopyOnWriteArrayList<TowerObject>();
		ZOManager.gameActive = true;
		initiatePlayers();
		WallObject.spawnInitialWalls();
		ShrineObject.spawnGameShrines();
		TowerObject.initiateDamagedTowers();
		World.sendWorldMessage(Colors.shade(Colors.red)+"New Zombie Outpost game started! "+players.size()+" warriors have gone in!", false);
	}
	
	public static void initiatePlayers() {
		for(Player player : ZOLobby.getPlayersReady()) {
			player.getBank().depositAllEquipment(false);
			player.getBank().depositAllInventory(false);
			player.getControlerManager().startControler("ZOControler");
			player.setNextWorldTile(new WorldTile(5537, 4291, 0));
			player.getInventory().addItem(2347, 1);
			player.ZOPoints = 2000;
			player.ZOTotalPoints = 2000;
			player.ZOKills = 0;
			sendInterface(player);
			players.add(player);
		}
	}
	
	public static void endGame() {
		World.sendWorldMessage(Colors.shade(Colors.red)+"Zombie Outpost ended! The game has ended on wave "+waveCount+" lasting "+(Utils.formatTime(gameTime))+"!", false);
		for(Player player : players) {
			sendRewards(player);
		}
		gameTime = 0;
		waveCount = 1;
		waveCountdown = 120000;
		for(NPC zombie : zombies) {
			zombie.sendDeath(null);
		}
		zombies.clear();
		for(Player player : players) {
			removePlayer(player);
		}
		players.clear();
		for(WallObject wall : walls) {
			World.removeObject(wall.obj);
		}
		walls.clear();
		for(ShrineObject shrine : shrines) {
			World.removeObject(shrine.obj);
		}
		shrines.clear();
		for(TowerObject tower : towers) {
			World.removeObject(tower.obj);
		}
		towers.clear();
		ZOManager.gameActive = false;
	}
	
	public static void removePlayer(Player player) {
		player.getControlerManager().removeControlerWithoutCheck();
		player.setNextWorldTile(new WorldTile(4307, 854, 0));
		if(players != null) {
			if(players.contains(player)) {
				players.remove(player);
			}
		}
	}
	
	public static void sendRewards(Player player) {
		
	}
	
	public static void sendInterface(Player player) {
		if(!player.getInterfaceManager().containsInterface(804))
			player.getInterfaceManager().sendOverlay(804, player.getInterfaceManager().hasRezizableScreen());
		player.getPackets().sendIComponentText(804, 3, "Wave:");
		player.getPackets().sendIComponentText(804, 10, ""+waveCount);
		player.getPackets().sendIComponentText(804, 5, "Kills:");
		player.getPackets().sendIComponentText(804, 4, "Points:");
		player.getPackets().sendIComponentText(804, 6, ""+player.ZOKills);
		player.getPackets().sendIComponentText(804, 7, ""+player.ZOPoints);
		player.getPackets().sendIComponentText(804, 8, "");
		player.getPackets().sendIComponentText(804, 9, "");
		player.getPackets().sendIComponentText(804, 1, "Game: "+Utils.formatTime(gameTime));
	}
	
	public static boolean withinArea(WorldTile tile) {
		return tile.getX() >= 5526 && tile.getY() >= 4270 && tile.getX() <= 5553 && tile.getY() <= 4313;
	}

	public static void initiateTick() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					tickGame();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}
	
}
