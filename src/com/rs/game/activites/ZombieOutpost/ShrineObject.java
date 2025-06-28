package com.rs.game.activites.ZombieOutpost;

import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.utils.Utils;

public class ShrineObject {

	public int x;
	public int y;
	public int health;
	public WorldObject obj;
	
	public static void spawnGameShrines() {
		spawnShrine(5539, 4291);
		spawnShrine(5538, 4278);
		spawnShrine(5531, 4293);
		spawnShrine(5541, 4307);
		spawnShrine(5548, 4290);
	}
	
	public static void tickShrines() {
		if(ZOGame.shrines.isEmpty()) {
			ZOGame.endGame();
		}
	}
	
	public static void spawnShrine(int x, int y) {
		WorldObject obj = new WorldObject(104728, 10, Utils.random(4), x, y, 0);
		ShrineObject shrine = new ShrineObject(x, y, obj);
		ZOGame.shrines.add(shrine);
	}
	
	public void checkDestroyed() {
		if(health <= 0) {
			health = 0;
		}
		if(health == 0) {
			World.removeObject(obj);
			if(ZOGame.shrines.contains(this)) {
				ZOGame.shrines.remove(this);
			}
			return;
		}
		
	}
	
	public ShrineObject(int x, int y, WorldObject obj) {
		this.x = x;
		this.y = y;
		this.health = 100;
		this.obj = obj;
		World.spawnObject(obj);
	}
	
}
