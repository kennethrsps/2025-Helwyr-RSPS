package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;

public class Prime extends CombatScript {

    @Override
    public int attack(final NPC npc, final Entity target) {
	npc.setNextAnimation(new Animation(29379));
	for (Entity t : npc.getPossibleTargets()) {
	    delayHit(
		    npc,
		    1,
		    t,
		    getMagicHit(
			    npc,
			    getRandomMaxHit(npc, 800,
				    NPCCombatDefinitions.MAGE, t)));
	    World.sendProjectile(npc, t, 2707, 18, 18, 50, 50, 3, 0);
	}
	return 4;
    }

    @Override
    public Object[] getKeys() {
	return new Object[] { 2882 };
    }
}