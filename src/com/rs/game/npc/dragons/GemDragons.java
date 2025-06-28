package com.rs.game.npc.dragons;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;

/**
 * 
 * @author Smart
 *
 */

@SuppressWarnings("serial")
public class GemDragons extends NPC{
	
	public GemDragons(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		//setHitpoints(10000);
		//getCombatDefinitions().setHitpoints(10000);

		
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
	}
	
	public void handleIngoingHit(Hit hit) {
	}
	
	@Override
    public double getMeleePrayerMultiplier() {
		return 0.25;
    }
	
	
	@Override
	public double getMagePrayerMultiplier() {
		return 0.25;
	}
	
	@Override
    public double getRangePrayerMultiplier() {
		return 0.25;
    }
	
	
	@Override
	public void sendDeath(Entity source) {
	super.sendDeath(source);	
	}

}
