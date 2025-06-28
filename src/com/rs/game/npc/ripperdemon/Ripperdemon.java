package com.rs.game.npc.ripperdemon;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;

/**
 * 
 * @author Kingkenobi
 *
 */

@SuppressWarnings("serial")
public class Ripperdemon extends NPC{
	
	public Ripperdemon(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		this.setHitpoints(8000);
		this.getCombatDefinitions().setHitpoints(8000);
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
	}
	
	public void handleIngoingHit(Hit hit) {
	}
	
	@Override
    public double getMeleePrayerMultiplier() {
		return 0.5;
    }
	
	
	@Override
	public double getMagePrayerMultiplier() {
		return 0.3;
	}
	
	@Override
    public double getRangePrayerMultiplier() {
		return 0.3;
    }
	
	
	@Override
	public void sendDeath(Entity source) {
	super.sendDeath(source);	
	}

}
