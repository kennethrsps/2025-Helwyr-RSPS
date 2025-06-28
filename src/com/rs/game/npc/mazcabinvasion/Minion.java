package com.rs.game.npc.mazcabinvasion;

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
public class Minion extends NPC{
	
	public Minion(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		this.setHitpoints(10000);
		this.getCombatDefinitions().setHitpoints(10000);
		setForceTargetDistance(64);

	}
	
	@Override
	public void processNPC() {
		super.processNPC();
	}
	
	public void handleIngoingHit(Hit hit) {
	}
	
	@Override
    public double getMeleePrayerMultiplier() {
		return 0.4;
    }
	
	
	@Override
	public double getMagePrayerMultiplier() {
		return 0.4;
	}
	
	@Override
    public double getRangePrayerMultiplier() {
		return 0.4;
    }
	
	
	@Override
	public void sendDeath(Entity source) {
	super.sendDeath(source);	
	}

}
