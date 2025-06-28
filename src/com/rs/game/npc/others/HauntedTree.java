package com.rs.game.npc.others;

import java.util.ArrayList;

import com.rs.game.Entity;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;

@SuppressWarnings("serial")
public class HauntedTree extends NPC {

	public HauntedTree(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		this.setCantFollowUnderCombat(true);
		this.setAtMultiArea(true);
		this.setForceAgressive(true);
	}

	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> targets = getPossibleTargets(false, true);
		ArrayList<Entity> checkedTargets = new ArrayList<Entity>();
		for (Entity target : targets) {
			if (!withinDistance(target, 2))
				continue;
			checkedTargets.add(target);
		}
		return checkedTargets;
	}

	@Override
	public void processNPC() {
		super.processNPC();
	}
}
