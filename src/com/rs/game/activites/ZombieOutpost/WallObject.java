package com.rs.game.activites.ZombieOutpost;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class WallObject {

	public static final int BROKEN_WALL = 14945;
	public static final int FIXED_WALL = 14948;
	
	public static final int NORTH = 3, EAST = 0, WEST = 2, SOUTH = 1;
	
	public static void spawnWall(int x, int y, int dir, int type) {
		WallObject wobj = new WallObject(x, y, dir, type);
		ZOGame.walls.add(wobj);
	}
	
	public static void spawnInitialWalls() {
		spawnWall(5542, 4270, SOUTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5541, 4270, SOUTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5540, 4270, SOUTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5539, 4270, SOUTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5538, 4270, SOUTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5537, 4270, SOUTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));

		spawnWall(5532, 4270, SOUTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5531, 4270, SOUTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		
		spawnWall(5548, 4270, SOUTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5547, 4270, SOUTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));

		spawnWall(5526, 4274, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4275, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4276, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4277, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4278, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4279, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4280, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4281, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4282, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4283, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4284, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4285, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));

		spawnWall(5526, 4289, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4290, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4291, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4292, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4293, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4294, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		
		spawnWall(5526, 4298, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4299, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4300, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4301, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4302, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4303, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4304, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4305, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4306, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4307, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4308, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5526, 4309, WEST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));

		spawnWall(5531, 4313, NORTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5532, 4313, NORTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));

		spawnWall(5537, 4313, NORTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5538, 4313, NORTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5539, 4313, NORTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5540, 4313, NORTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5541, 4313, NORTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5542, 4313, NORTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));

		spawnWall(5547, 4313, NORTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5548, 4313, NORTH, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));

		spawnWall(5553, 4309, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4308, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4307, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4306, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4305, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4304, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4303, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4302, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4301, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4300, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4299, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4298, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));

		spawnWall(5553, 4294, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4293, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4292, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4291, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4290, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4289, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));

		spawnWall(5553, 4285, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4284, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4283, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4282, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4281, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4280, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4279, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4278, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4277, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4276, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4275, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
		spawnWall(5553, 4274, EAST, (Utils.random(2) == 1 ? BROKEN_WALL : FIXED_WALL));
	}
	
	public static WallObject getWallForObject(WorldObject object) {
		for(WallObject wall : ZOGame.walls) {
			if(wall.obj.getX() == object.getX() && wall.obj.getY() == object.getY()) {
				return wall;
			}
		}
		return null;
	}
	
	public void repair(Player player, WorldObject faceObject) {
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if(health >= 100) {
					stop();
					return;
				}
				health += 25;
				player.faceObject(faceObject);
				player.setNextAnimation(new Animation(401));
				checkRepairStage();
			}
		}, 0, 3);
	}
	
	public void checkRepairStage() {
		if(health >= 100) {
			health = 100;
		}
		if(health <= 0) {
			health = 0;
		}
		if(health == 100) {
			if(ZOGame.walls.contains(this))
				ZOGame.walls.remove(this);
			spawnWall(this.x, this.y, this.dir, FIXED_WALL);
			return;
		}
		if(health == 0) {
			World.removeObject(obj);
			if(ZOGame.walls.contains(this)) {
				ZOGame.walls.remove(this);
			}
			spawnWall(this.x, this.y, this.dir, BROKEN_WALL);
			return;
		}
		
	}
	
	public int x;
	public int y;
	public int dir;
	public int health;
	public WorldObject obj;
	
	public WallObject(int x, int y, int dir, int id) {
		this.x = x;
		this.y = y;
		this.dir = dir;
		this.health = id == WallObject.BROKEN_WALL ? 0 : 100;
		this.obj = new WorldObject(id, 0, dir, x, y, 0);
		World.spawnObject(obj, id == BROKEN_WALL ? false : true);
	}
	
}
