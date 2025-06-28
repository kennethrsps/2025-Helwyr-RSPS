package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.controllers.AraxyteHyveController;
import com.rs.utils.Utils;

/**
 * Handles the Spitting Spider combat.
 * @author Zeus
 */
public class SpittingSpiderCombat extends CombatScript {

    @Override
    public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		int attack = Utils.random(2);
	    npc.setNextAnimation(new Animation(defs.getAttackEmote()));
	    int damage = Utils.random(0, 30 + (AraxyteHyveController.getPlayers().size() * 5));
		switch (attack) {
		case 0: // Melee
			target.applyHit(new Hit(target, damage, HitLook.MELEE_DAMAGE));
		    break;
		case 1: // Mage
			target.applyHit(new Hit(target, damage, HitLook.RANGE_DAMAGE));
		    break;
		case 2: // Range
			target.applyHit(new Hit(target, damage, HitLook.MAGIC_DAMAGE));
		    break;
		}
		return defs.getAttackDelay();
    }

    @Override
    public Object[] getKeys() {
    	return new Object[] { 19460 };
    }
}