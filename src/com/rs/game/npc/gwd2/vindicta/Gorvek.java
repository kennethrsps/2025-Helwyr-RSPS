package com.rs.game.npc.gwd2.vindicta;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;

/**
 * @ausky Tom
 * @date April 9, 2017
 */

public class Gorvek extends NPC {

	private static final long serialVersionUID = -4771707128153113580L;

	public Gorvek(int id, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setRun(true);
	}

	@Override
	public void sendDeath(final Entity source) {
		super.sendDeath(source);
	}
	
	@Override
	public boolean canWalkNPC(int toX, int toY) {
		return true;
	}

	@Override
	public void spawn() {
		super.spawn();
		setNextAnimation(new Animation(28264));
	}

}