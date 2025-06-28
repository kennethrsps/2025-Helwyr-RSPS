
package com.rs.game.npc.telos;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;

@SuppressWarnings("serial")
public class Telos extends NPC {

	public Telos(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		this.setLureDelay(0);
	//	this.setCapDamage(2750);
		this.setRun(true);
		this.setForceMultiAttacked(true);
		this.setForceAgressive(true);
		this.setHitpoints(200000);
		this.getCombatDefinitions().setHitpoints(200000);
		this.setForceFollowClose(true);
		this.setIntelligentRouteFinder(true);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isDead())
			return;
		checkReset();

	}
	@Override
    public double getMeleePrayerMultiplier() {
		return 0.30;
    }
	
	
	@Override
	public double getMagePrayerMultiplier() {
		return 0.275;
	}
	
	@Override
    public double getRangePrayerMultiplier() {
		return 0.275;
    }
	public void checkReset() {
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && !isUnderCombat() && getPossibleTargets().isEmpty())
			setHitpoints(maxhp);
	}
	@Override
	public void spawn() {
		super.spawn();
		setNextAnimation(new Animation(28930));
		//setNextGraphics(new Graphics(6244));
	}	
	//test
	@Override
	public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> possibleTarget = new ArrayList<Entity>();
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playerIndexes != null) {
				for (int npcIndex : playerIndexes) {
					Player player = World.getPlayers().get(npcIndex);
					if (player == null || player.isDead() || player.hasFinished() || !player.isRunning()
							|| !player.withinDistance(this, 64)
							|| ((!isAtMultiArea() || !player.isAtMultiArea()) && player.getAttackedBy() != this
									&& player.getAttackedByDelay() > System.currentTimeMillis())
							|| !clipedProjectile(player, false))
						continue;
					possibleTarget.add(player);
				}
			}
		}
		return possibleTarget;
	}
	
	//test

}
