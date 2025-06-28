package com.rs.game.activites.ZombieOutpost;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class ZombieManager {
	
	public static int[] ZOMBIES = {24145, 23045, 23044, 23043, 23042};
	public static int getRandomZombie() {
		return ZOMBIES[Utils.random(ZOMBIES.length)];
	}
	public static boolean isZombie(NPC npc) {
		for(int i : ZOMBIES) {
			if(i == npc.getId()) {
				return true;
			}
		}
		return false;
	}
	
	public static void initiateZombies() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					tickWaves();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}
	
	public static void tickWaves() {
		if(!ZOManager.gameActive) {
			return;
		}
		if(!ZOGame.zombies.isEmpty()) {
			for(NPC zombie : ZOGame.zombies) {
				tickZombieLogic(zombie);
			}
			return;//zombies are still alive
		} else {
			if(ZOGame.waveCountdown == 120000) {
				for(Player player : ZOGame.players) {
					player.getPackets().sendGameMessage(Colors.shade(Colors.red)+"Wave "+(ZOGame.waveCount + 1)+" starting in 2 minutes - "+getZombieWaveSpawnAmount()+" zombies @"+(getZombieWaveSpawnAmount() * 100)+" hitpoints.");
				}
			}
		}
		if(ZOGame.waveCountdown > 0) {
			ZOGame.waveCountdown -= 1000;
		}
		if(ZOGame.waveCountdown <= 0) {
			startNewWave();
		}
	}
	
	public static void tickZombieLogic(NPC zombie) {
		zombie.ZOScriptPause--;
		if(zombie.ZOScriptPause > 0) {
			return;
		}
		ShrineObject shrine = getNearestShrine(zombie);
		if(shrine != null) {
			if(zombie.withinDistance(shrine.obj, 1)) {
				zombie.faceObject(shrine.obj);
				zombie.setNextForceTalk(new ForceTalk("Grrrr..."));
				shrine.health -= 5;
				shrine.checkDestroyed();
				ZOLobby.announcer.setNextForceTalk(new ForceTalk("A shrine is under attack!"));
				return;
			} else {
				if(zombie.addWalkSteps(shrine.obj.getX(), shrine.obj.getY(), 1, true)) {
					zombie.ZOScriptPause = 3;
					return;
				}
			}
		}
		WallObject wall = getNearestWall(zombie);
		if(wall != null) {
			if(zombie.withinDistance(wall.obj, 1)) {
				if(wall.obj.getId() == WallObject.FIXED_WALL) {
					zombie.faceObject(wall.obj);
					zombie.setNextForceTalk(new ForceTalk("Grrrr..."));
					wall.health -= 7;
					wall.checkRepairStage();
				} else {
					if(wall.dir == WallObject.NORTH) {
						zombie.addWalkSteps(zombie.getX(), zombie.getY() - 1, 1, false);
					} else if(wall.dir == WallObject.SOUTH) {
						zombie.addWalkSteps(zombie.getX(), zombie.getY() + 1, 1, false);
					} else if(wall.dir == WallObject.WEST) {
						zombie.addWalkSteps(zombie.getX() + 1, zombie.getY(), 1, false);
					} else if(wall.dir == WallObject.EAST) {
						zombie.addWalkSteps(zombie.getX() - 1, zombie.getY(), 1, false);
					}
				}
			} else {
				zombie.addWalkSteps(wall.obj.getX(), wall.obj.getY(), 99, false);
			}
		}
	}
	
	public static WallObject getNearestWall(NPC npc) {
		for(int i=0;i<100;i++) {
			for(WallObject wall : ZOGame.walls) {
				if(npc.withinDistance(wall.obj, i)) {
					return wall;
				}
			}
		}
		return null;
	}
	
	public static ShrineObject getNearestShrine(NPC npc) {
		for(int i=0;i<100;i++) {
			for(ShrineObject shrine : ZOGame.shrines) {
				if(npc.withinDistance(shrine.obj, i)) {
					return shrine;
				}
			}
		}
		return null;
	}
	
	public static void sendZombieDeath(NPC npc) {
		if(ZOGame.zombies.contains(npc)) {
			ZOGame.zombies.remove(npc);
		}
	}
	
	public static final int NORTH = 1, EAST = 2, SOUTH = 3, WEST = 4;
	
	public static void startNewWave() {
		ZOGame.waveCountdown = 120000;
		ZOGame.waveCount++;
		for(Player player : ZOGame.players) {
			player.getPackets().sendGameMessage(Colors.shade(Colors.red)+"Wave "+ZOGame.waveCount+" starting - "+getZombieWaveSpawnAmount()+" zombies @"+(getZombieWaveSpawnAmount() * 100)+" hitpoints.");
		}
		for(int i=0;i<=getZombieWaveSpawnAmount();i++) {
			//int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned
			int type = Utils.random(4);
			NPC zombie = new NPC(getRandomZombie(), getSpawnForType(type), -1, true, true);
			zombie.ZOType = type;
			ZOGame.zombies.add(zombie);
		}
	}
	
	public static WorldTile getSpawnForType(int type) {
		int x = 0;
		int y = 0;
		if(type == NORTH) {
			x = 5505;
			y = 4266;
		} else if(type == EAST) {
			x = 5518;
			y = 4278 + Utils.random(20);
		} else if(type == SOUTH) {
			x = 5556 - Utils.random(24);
			y = 4326;
		} else {
			x = 5557;
			y = 4270 + Utils.random(43);
		}
		return new WorldTile(x, y, 0);
	}
	
	public static int getZombieWaveSpawnAmount() {
		return (((ZOGame.players.size() / 2) + 1) * ZOGame.waveCount) + 1;
	}

	public List<NPC> getZombies() {
		return ZOGame.zombies;
	}
	
}
