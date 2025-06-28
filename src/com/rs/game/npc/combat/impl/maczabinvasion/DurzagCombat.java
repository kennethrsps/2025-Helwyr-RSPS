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

public class DurzagCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (npc.withinDistance(target, npc.getSize())) {
			switch (Utils.random(10)) {
			case 0:
				meleeAttack(npc, target);
				break;
			case 1:
				meleeAttack(npc, target);
				break;
			case 2:
				rangeAttack(npc, target);
				break;
			case 3:
			    poisonAttack(npc, target);
				break;
			default:
				meleeAttack(npc, target);
				break;
			}
		} else {
			switch (Utils.random(5)) {
			case 0:
				poisonAttack(npc, target);
				break;
			case 1:
				rangeAttack(npc, target);
				break;
			case 2:
				rangeAttack(npc, target);
				break;
			case 3:
				rangeAttack(npc, target);
				break;
			default:
				poisonAttack(npc, target);
				break;
			}
		}
		return defs.getAttackDelay();
	}

	public void rangeAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(26848));
		World.sendProjectile(npc, target, 1192, 41, 16, 41, 35, 16, 0);  //1190
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.RANGE, target);
		damage += Utils.random(150, 200);
		delayHit(npc, 1, target, getRangeHit(npc, damage));
	}

	public void poisonAttack(NPC npc, Entity target) {
		final Player player = target instanceof Player ? (Player) target : null;
		if (player != null) {
			npc.setNextAnimation(new Animation(26848));
			World.sendProjectile(npc, target, 3436, 41, 16, 41, 35, 16, 0);  //1190
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.getPackets().sendGameMessage("You got poison by Durzag!", true);
					delayHit(npc, 0, target,
							getMagicHit(npc, getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target)
									+ Utils.random(150, 200)));
					player.getPoison().makePoisoned(50);
					stop();
				}
			}, 0);
		}
	}

	public void meleeAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(26843));
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		damage += Utils.random(150, 200);
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
	}



	@Override
	public Object[] getKeys() {
		return new Object[] { 21335 };
	}

}
