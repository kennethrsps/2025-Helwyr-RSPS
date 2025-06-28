package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.utils.Utils;

/**
 * @author Jesper
 */

public class WildyWyrmCombat extends CombatScript {

    @Override
    public int attack(final NPC npc, final Entity target) {
	final NPCCombatDefinitions defs = npc.getCombatDefinitions();
	int attackStyle = Utils.getRandom(2);
	int hit = Utils.getRandom(10);
	switch (attackStyle) {
	case 0:
	    hit = getRandomMaxHit(npc, defs.getMaxHit(),
		    NPCCombatDefinitions.MAGE, target);
	    npc.setNextAnimation(new Animation(defs.getAttackEmote()));
	    World.sendProjectile(npc, target, 3442, 34, 16, 30, 35, 16, 0);
	    delayHit(npc, 1, target, getMagicHit(npc, hit));
	    break;
	case 1:
	    hit = getRandomMaxHit(npc, defs.getMaxHit(),
		    NPCCombatDefinitions.RANGE, target);
	    npc.setNextAnimation(new Animation(defs.getAttackEmote()));
	    delayHit(npc, 1, target, getMeleeHit(npc, hit));
	    break;
	}
	return defs.getAttackDelay();
    }

    @Override
    public Object[] getKeys() {
	return (new Object[] { Integer.valueOf(3334) });
    }
}