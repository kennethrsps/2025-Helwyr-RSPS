package com.rs.game.npc.combat.impl;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
//import com.rs.game.player.Player;
//import com.rs.game.player.content.Combat;
//import com.rs.game.tasks.WorldTask;
//import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

// Created by Kingkenobi

public class MagisterCombat extends CombatScript {

	@Override
	public int attack(NPC npc, Entity target) {
		final NPCCombatDefinitions defs = npc.getCombatDefinitions();
		if (npc.withinDistance(target, npc.getSize())) {
			switch (Utils.random(10)) {
			case 1:
				mageAttack(npc, target);
				break;
			case 2:
				rangeAttack(npc, target);
				break;
			case 3:
				mageAttack2(npc, target);
				break; 
			case 4:
				aoeAttack(npc, target);
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
				mageAttack2(npc, target);
				break;
			case 3:
				aoeAttack(npc, target);
				break;
			case 4:
				mageAttack(npc, target);
				break;
			default:
				mageAttack(npc, target);
				break;
			}
		}
		return defs.getAttackDelay();
	}

	public void mageAttack(NPC npc, Entity target) {
		
		for (Entity t : npc.getPossibleTargets()) {
		npc.setNextAnimation(new Animation(23989));   //14224
		World.sendGraphics(npc, new Graphics(5028), t);
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, t);
		damage += Utils.random(100, 250);
		delayHit(npc, 0, t, getMagicHit(npc, damage));
		  if (Utils.getRandom(2) == 0) {
			  npc.setNextForceTalk(new ForceTalk("You think you stand a chance"));
		      WorldTile teleTile = npc;
		      for (int trycount = 0; trycount < 2; trycount++) {
			  teleTile = new WorldTile(target, 2);
			  if (World.canMoveNPC(target.getPlane(), teleTile.getX(),
				teleTile.getY(), target.getSize()))
			    continue;
		    }
		    if (World.canMoveNPC(npc.getPlane(), teleTile.getX(),
			    teleTile.getY(), npc.getSize())) {
			npc.setNextGraphics(new Graphics(5019));
			npc.setNextWorldTile(teleTile);
		        }
		    }
		}
	}

	public void mageAttack2(NPC npc, Entity target) {      //(final NPC npc, Entity target, int n)
		npc.setNextForceTalk(new ForceTalk("Breath"));
		npc.setNextAnimation(new Animation(17958));
		World.sendGraphics(npc, new Graphics(5025), target);  //5516
		World.sendGraphics(npc, new Graphics(5022), npc); //5022
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MAGE, target);
		damage += Utils.random(150, 275);
		delayHit(npc, 2, target, getMagicHit(npc, damage));
		//STR reduction
	    Player targetPlayer = (Player) target;
	    int currentLevel = targetPlayer.getSkills().getLevel(
		    Skills.STRENGTH);
	    targetPlayer.getSkills().set(Skills.STRENGTH,
		    currentLevel < 15 ? 0 : currentLevel - 15);
	    //
		//World.sendGraphics(npc, new Graphics(1629), target);
		if (Utils.getRandom(2) == 0) {
			npc.setNextForceTalk(new ForceTalk("You think you stand a chance"));
		    WorldTile teleTile = npc;
		    for (int trycount = 0; trycount < 2; trycount++) {
			teleTile = new WorldTile(target, 2);
			if (World.canMoveNPC(target.getPlane(), teleTile.getX(),
				teleTile.getY(), target.getSize()))
			    continue;
		    }
		    if (World.canMoveNPC(npc.getPlane(), teleTile.getX(),
			    teleTile.getY(), npc.getSize())) {
			npc.setNextGraphics(new Graphics(3607));
			npc.setNextWorldTile(teleTile);
		    }
		}
	}
	
	public void rangeAttack(NPC npc, Entity target) {
		npc.setNextForceTalk(new ForceTalk("Try to catch this"));
		npc.setNextAnimation(new Animation(4841));
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.RANGE, target);
		World.sendProjectile(npc, target, 90, 28, 16, 35, 20, 16, 0);
		World.sendGraphics(npc, new Graphics(5030), npc);
		//World.sendGraphics(npc, new Graphics(5106), target);
		damage += Utils.random(150, 275);
	    delayHit(npc, 1, target, getRangeHit(npc, damage));
	}

	public void meleeAttack(NPC npc, Entity target) {
		World.sendGraphics(npc, new Graphics(5014), target);
		npc.setNextAnimation(new Animation(7041));
		int damage = getRandomMaxHit(npc, npc.getMaxHit(), NPCCombatDefinitions.MELEE, target);
		damage += Utils.random(100, 150);
		delayHit(npc, 0, target, getMeleeHit(npc, damage));
		Player targetPlayer = (Player) target;
	    int currentLevel = targetPlayer.getSkills().getLevel(
		    Skills.DEFENCE);
	    targetPlayer.getSkills().set(Skills.DEFENCE,
		    currentLevel < 15 ? 0 : currentLevel - 15);
	}
	//test
    public void aoeAttack(NPC npc, Entity target) {
    	for (Entity t : npc.getPossibleTargets()) {
    	npc.setNextAnimation(new Animation(1979));
	    final WorldTile center = new WorldTile(t);
	    World.sendGraphics(npc, new Graphics(5586), center);   //5649
	    npc.setNextForceTalk(new ForceTalk("Why you are scared?"));
	    WorldTasksManager.schedule(new WorldTask() {
		int count = 0;

		@Override
		public void run() {
		    for (Player player : World.getPlayers()) { // lets just loop
							       // all players
							       // for massive  
							       // moves

			if (player == null || player.isDead()
				|| player.hasFinished())
			    continue;
			if (player.withinDistance(center, 1)) {
			    delayHit(npc, 0, player,
				    new Hit(npc, Utils.random(125),
					    HitLook.REGULAR_DAMAGE));
			}
		    }
		    if (count++ == 10) {
			stop();
			return;
		      }
		   
		}
	    }, 0, 0);
    	}	
    }	
    
    //test
    
	@Override
	public Object[] getKeys() {
		return new Object[] { 24765 };
	}

}