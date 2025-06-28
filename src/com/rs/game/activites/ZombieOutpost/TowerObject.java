package com.rs.game.activites.ZombieOutpost;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class TowerObject {

	public static final int TOWER1 = 60051;
	public static final int TOWER2 = 60052;
	public static final int TOWER3 = 60054;
	public static final int TOWER4 = 60053;
	public static final int TOWERDAMAGED = 55472;
	
	public int objectId;
	public String name;
	public String description;
	public int projectileId;
	public int gfxId;
	public int fireRate;
	public int damage;
	public WorldObject obj;
	public String owner;
	
	public NPC getNearestZombie() {
		int distance = getDistance();
		for(NPC zombie : ZOGame.zombies) {
			if(zombie.withinDistance(new WorldTile(obj.getX(), obj.getY(), 0), distance)) {
				return zombie;
			}
		}
		return null;
	}
	
	public int getDistance() {
		if(obj.getId() == TOWER1) {
			return 4;
		}
		if(obj.getId() == TOWER2) {
			return 5;
		}
		if(obj.getId() == TOWER3) {
			return 6;
		}
		if(obj.getId() == TOWER4) {
			return 7;
		}
		return 0;
	}
	
	public int getDamage() {
		if(obj.getId() == TOWER1) {
			return 100;
		}
		if(obj.getId() == TOWER2) {
			return 125;
		}
		if(obj.getId() == TOWER3) {
			return 150;
		}
		if(obj.getId() == TOWER4) {
			return 175;
		}
		return 0;
	}
	
	public int getGFX() {
		if(obj.getId() == TOWER1) {
			return 1000;
		}
		if(obj.getId() == TOWER2) {
			return 1001;
		}
		if(obj.getId() == TOWER3) {
			return 1002;
		}
		if(obj.getId() == TOWER4) {
			return 1003;
		}
		return 0;
	}
	
	public int getUpgradeCost() {
		if(obj.getId() == TOWERDAMAGED) {
			return 2500;
		}
		if(obj.getId() == TOWER1) {
			return 7500;
		}
		if(obj.getId() == TOWER2) {
			return 10000;
		}
		if(obj.getId() == TOWER3) {
			return 12500;
		}
		if(obj.getId() == TOWER4) {
			return 15000;
		}
		return Integer.MAX_VALUE;
	}
	
	public int getNextUpgrade() {
		if(obj.getId() == TOWERDAMAGED) {
			return TOWER1;
		}
		if(obj.getId() == TOWER1) {
			return TOWER2;
		}
		if(obj.getId() == TOWER2) {
			return TOWER3;
		}
		if(obj.getId() == TOWER3) {
			return TOWER4;
		}
		return TOWER4;
	}
	
	public void upgradeTower(Player player) {
		if(obj.getId() == TOWER4) {
			player.errorMessage("This tower already has the max upgrade.");
			return;
		}
		int cost = getUpgradeCost();
		if(cost > player.ZOPoints) {
			player.errorMessage("You don't have enough points to upgrade this tower.");
			return;
		}
		player.faceObject(obj);
		player.setNextAnimation(new Animation(401));
		player.ZOPoints -= cost;
		player.closeInterfaces();
		WorldObject nobj = new WorldObject(getNextUpgrade(), 10, 0, obj.getX(), obj.getY(), 0);
		World.removeObject(obj);
		obj = nobj;
		World.spawnObject(obj);
		owner = player.getUsername();
	}
	
	//732 inter
	
	public static void spawnDamagedTower(int x, int y) {
		WorldObject obj = new WorldObject(TOWERDAMAGED, 10, 0, x, y, 0);
		TowerObject tower = new TowerObject();
		tower.obj = obj;
		World.spawnObject(tower.obj);
		ZOGame.towers.add(tower);
	}
	
	public static void sendInterface(Player player, TowerObject tower) {
		if(tower == null) {
			return;
		}
		if(tower.obj.getId() != TowerObject.TOWERDAMAGED) {
			if(!tower.owner.equals(player.getUsername())) {
				player.errorMessage("This isn't your tower to upgrade!");
				return;
			}
		}
		player.lastTowerViewed = tower;
		player.getInterfaceManager().sendInterface(1008);
		player.getPackets().sendIComponentText(1008, 18, "Tower Upgrade");
		player.getPackets().sendIComponentText(1008, 23, "It will cost "+tower.getUpgradeCost()+" points to upgrade this tower.");
		player.getPackets().sendIComponentText(1008, 29, "Upgrade");
		player.getPackets().sendIComponentText(1008, 28, "Cancel");
		player.getPackets().sendIComponentText(1008, 32, "You have "+player.ZOPoints+" points.");
	}
	
	public static void handleButtons(Player player, int c) {
		if(c == 29) { 
			if(player.lastTowerViewed != null) {
				player.lastTowerViewed.upgradeTower(player);
			}
		}
		if(c == 28) {
			player.closeInterfaces();
			return;
		}
	}
	
	public static void initiateDamagedTowers() {
		spawnDamagedTower(5530, 4275);
		spawnDamagedTower(5530, 4278);
		spawnDamagedTower(5530, 4281);
		spawnDamagedTower(5530, 4284);
		spawnDamagedTower(5530, 4287);
		spawnDamagedTower(5530, 4296);
		spawnDamagedTower(5530, 4299);
		spawnDamagedTower(5530, 4302);
		spawnDamagedTower(5530, 4305);
		spawnDamagedTower(5530, 4308);

		spawnDamagedTower(5532, 4310);
		spawnDamagedTower(5535, 4310);
		spawnDamagedTower(5538, 4310);
		spawnDamagedTower(5541, 4310);
		spawnDamagedTower(5544, 4310);
		spawnDamagedTower(5547, 4310);
		spawnDamagedTower(5550, 4310);

		spawnDamagedTower(5550, 4307);
		spawnDamagedTower(5550, 4304);
		spawnDamagedTower(5550, 4301);
		spawnDamagedTower(5550, 4298);
		spawnDamagedTower(5550, 4293);
		spawnDamagedTower(5550, 4290);
		spawnDamagedTower(5550, 4286);
		spawnDamagedTower(5550, 4283);
		spawnDamagedTower(5550, 4280);
		spawnDamagedTower(5550, 4277);
		spawnDamagedTower(5550, 4274);

		spawnDamagedTower(5548, 4273);
		spawnDamagedTower(5545, 4273);
		spawnDamagedTower(5541, 4273);
		spawnDamagedTower(5538, 4273);
		spawnDamagedTower(5534, 4273);
		spawnDamagedTower(5531, 4273);
	}
	
	public static void tickTowers() {
		if(!ZOManager.gameActive) {
			return;
		}
		if(ZOGame.towers.isEmpty()) {
			return;
		}
		if(ZOGame.zombies.isEmpty()) {
			return;
		}
		for(TowerObject tower : ZOGame.towers) {
			if(tower.obj.getId() == TowerObject.TOWERDAMAGED) {
				continue;
			}
			NPC zombie = tower.getNearestZombie();
			if(zombie == null) {
				continue;
			}
			Player owner = World.getPlayer(tower.owner);
			if(owner == null) {
				continue;
			}
			int damage = tower.getDamage();
			Hit hit = new Hit(owner, (damage / 2) + Utils.random(damage / 2), HitLook.CRITICAL_DAMAGE, 2);
			zombie.applyHit(hit);
			World.sendProjectile(owner, new WorldTile(tower.obj.getX(), tower.obj.getY(), 0), zombie, tower.getGFX(), 100, 50, 10, 1, 1, 1);
		}
	}
	
	public static void initiateTick() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					tickTowers();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 3, 3, TimeUnit.SECONDS);
	}
	
	public static TowerObject getTowerForObject(WorldObject obj) {
		for(TowerObject tower : ZOGame.towers) {
			if(tower.obj.getX() == obj.getX() && tower.obj.getY() == obj.getY()) {
				return tower;
			}
		}
		return null;
	}
	
	
	
}
