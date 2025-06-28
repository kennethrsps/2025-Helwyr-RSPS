package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Combat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class NexMinionCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (npc.withinDistance(target, npc.getSize())) {
			switch (Utils.random(10)) {
			case 1:
				mageAttack(npc, target);
				break;
			case 2:
				mageAttack(npc, target);
				break;
			case 3:
				mageAttack(npc, target);
				break;
			default:
				mageAttack(npc, target);
				break;
			}
		} else {
			switch (Utils.random(5)) {
			case 0:
				mageAttack(npc, target);
				break;
			case 1:
				mageAttack(npc, target);
				break;
			case 2:
				mageAttack(npc, target);
				break;
			case 3:
				mageAttack(npc, target);
				break;
			default:
				mageAttack(npc, target);
				break;
			}
		}
		return defs.getAttackDelay();
	}

	public void mageAttack(NPC npc, Entity target ) {
		npc.setNextAnimation(new Animation(711));
		World.sendProjectile(npc, target, 88, 41, 16, 41, 35, 16, 0);
		npc.setNextGraphics(new Graphics(87));
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target);
		damage += Utils.random(25, 60);
		Player Player = (Player) target;
		delayHit(npc, 1, Player, getMagicHit(npc, damage));
		int activeLevel = Player.getPrayer().getPrayerpoints();
		if (activeLevel > 0) {
			int level = Player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
			Player.getPrayer().drainPrayer(level / 40);
		}
	}


	@Override
	public Object[] getKeys() {
		return new Object[] { 24010,24011,24012,24013 };
	}

}
