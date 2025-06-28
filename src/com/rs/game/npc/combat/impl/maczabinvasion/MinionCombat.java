package com.rs.game.npc.combat.impl.maczabinvasion;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.Combat;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class MinionCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (npc.withinDistance(target, npc.getSize())) {
			switch (Utils.random(10)) {
			case 1:
			case 2:
				rangeAttack(npc, target);
				break;
			case 3:
				//poisonAttack(npc, target);
				//break;
			default:
				meleeAttack(npc, target);
				break;
			}
		} else {
			switch (Utils.random(5)) {
			case 0:
			case 1:
			case 2:
			case 3:
				//poisonAttack(npc, target);
				//break;
			default:
				rangeAttack(npc, target);
				break;
			}
		}
		return defs.getAttackDelay();
	}

	public void rangeAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(26861));
		World.sendProjectile(npc, target, 1190, 41, 16, 41, 35, 16, 0);
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.RANGE, target);
		damage += Utils.random(150, 200);
		delayHit(npc, 1, target, getRangeHit(npc, damage));
	}

	public void poisonAttack(NPC npc, Entity target) {
		final Player player = target instanceof Player ? (Player) target : null;
		if (player != null) {
			npc.setNextAnimation(new Animation(27752));
			World.sendProjectile(npc, target, 1017, 41, 16, 41, 35, 16, 0);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.getPackets().sendGameMessage("", true);
					delayHit(npc, 0, target,
							getMagicHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target)
									+ Utils.random(150, 200)));
					player.setNextGraphics(new Graphics(2119, 50, 0));
					player.getPoison().makePoisoned(50);
					stop();
				}
			}, 0);
		}
	}

	public void meleeAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(26857));
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		damage += Utils.random(150, 200);
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
	}


	@Override
	public Object[] getKeys() {
		return new Object[] { 21336,21337 };
	}

}
