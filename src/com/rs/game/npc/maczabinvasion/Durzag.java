package com.rs.game.npc.maczabinvasion;

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
public class Durzag extends NPC{
	
	public Durzag(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		this.setHitpoints(35000);
		this.getCombatDefinitions().setHitpoints(35000);
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
		return 0.5;
    }
	
	
	@Override
	public double getMagePrayerMultiplier() {
		return 0.5;
	}
	
	@Override
    public double getRangePrayerMultiplier() {
		return 0.5;
    }
	
	
	@Override
	public void sendDeath(Entity source) {
	super.sendDeath(source);	
	}

}
