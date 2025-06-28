package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

@SuppressWarnings("serial")
public class SeaTrollQueen extends NPC {

	Item[] drops = {
			new Item(33886,1),
			new Item(33889,1),
			new Item(995,Utils.random(100000,10000000)),
			
	};
	
	public SeaTrollQueen(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, true, spawned);
		//setCapDamage(500);
		setLureDelay(3000);
	//	setForceTargetDistance(64);
		setForceFollowClose(false);
	}

	@Override
	public void processNPC() {
		super.processNPC();
	}

	@Override
	public void sendDeath(Entity source) {
		if (source instanceof Player) {
			int random = Utils.random(300);
			if(random <= 1)
		World.updateGroundItem(drops[Utils.random(1)], (Player) source,(Player)source);
			else 
		World.updateGroundItem(drops[2], (Player) source,(Player)source);
		}
		super.sendDeath(source);
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}
	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

}
