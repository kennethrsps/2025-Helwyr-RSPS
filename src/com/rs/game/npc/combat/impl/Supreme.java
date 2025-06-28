package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;

public class Supreme extends CombatScript {

    @Override
    public int attack(final NPC npc, final Entity target) {
	npc.setNextAnimation(new Animation(29372));
	for (Entity t : npc.getPossibleTargets()) {
	    delayHit(
		    npc,
		    1,
		    t,
		    getRangeHit(
			    npc,
			    getRandomMaxHit(npc, 400,
				    NPCCombatDefinitions.RANGE, target)));
	    World.sendProjectile(npc, t, 475, 41, 16, 60, 30, 16, 0);
	}
	return 4;
    }

    @Override
    public Object[] getKeys() {
	return new Object[] { 2881 };
    }
}
