package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;

@SuppressWarnings("serial")
public class DarkLord extends NPC {

	public DarkLord(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		//setCapDamage(1000);
		setLureDelay(3000);
		setForceTargetDistance(64);
		setForceFollowClose(false);
		setForceMultiAttacked(true);
		setForceMultiAttacked(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		checkReset();

	}

	public void checkReset() {
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && !isUnderCombat() && getPossibleTargets().isEmpty())
			setHitpoints(maxhp);
	}

	@Override
	public void sendDeath(Entity source) {
		super.sendDeath(source);
	}
}