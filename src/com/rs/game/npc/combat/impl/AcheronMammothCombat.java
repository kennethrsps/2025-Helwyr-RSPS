package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
//import com.rs.game.player.Player;
//import com.rs.game.player.content.Combat;
//import com.rs.game.tasks.WorldTask;
//import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

// Created by Kingkenobi

public class AcheronMammothCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (npc.withinDistance(target, npc.getSize())) {
			switch (Utils.random(10)) {
			case 1:
				meleeAttack(npc, target);
				break;
			case 2:
				rangeAttack(npc, target);
				break;
			case 3:
				meleeAttack(npc, target);
				break; 
			case 4:
				rushAttack(npc, target);
				break;
			default:
				meleeAttack(npc, target);
				break;
			}
		} else {
			switch (Utils.random(5)) {
			case 0:
			case 1:
				rangeAttack(npc, target);
				break;
			case 2:
				rangeAttack(npc, target);
				break;
			case 3:
				rangeAttack(npc, target);
				break;
			case 4:
				rangeAttack(npc, target);
				break;
			default:
				rangeAttack(npc, target);
				break;
			}
		}
		return defs.getAttackDelay();
	}

	public void mageAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(28959));
		World.sendGraphics(npc, new Graphics(5513), target);
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target);
		damage += Utils.random(150, 200);
		delayHit(npc, 0, target, getMagicHit(npc, damage));
	}

	public void rangeAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(27819));
		World.sendProjectile(npc, target, 1627, 41, 16, 41, 180, 16, 0);//39, 36, 41, 50, 0, 100);   35
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.RANGE, target);
		damage += Utils.random(300, 400);
		delayHit(npc, 12, target, getRangeHit(npc, damage));
		//World.sendGraphics(npc, new Graphics(1629), target);
	}

	public void rushAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(27820));
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		World.sendGraphics(npc, new Graphics(5106), target);
		damage += Utils.random(300, 350);
	    delayHit(npc, 4, target, getMeleeHit(npc, damage));
	}

	public void meleeAttack(NPC npc, Entity target) {
		npc.setNextAnimation(new Animation(27818));
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		World.sendGraphics(npc, new Graphics(5106), target);
		damage += Utils.random(125, 175);
		delayHit(npc, 0, target, getMeleeHit(npc, damage));

	}

	@Override
	public Object[] getKeys() {
		return new Object[] { 22007 };
	}

}