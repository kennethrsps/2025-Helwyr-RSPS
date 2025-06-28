package com.rs.game.npc.magister;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;

/**
 * 
 * @author Kingkenobi
 *
 */

@SuppressWarnings("serial")
public class Magister extends NPC{
	
	public Magister(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		this.setHitpoints(60000);
		this.getCombatDefinitions().setHitpoints(60000);
		setLureDelay(0);
		setCapDamage(1750);
		setRun(true);
		setForceMultiAttacked(true);
		setForceAgressive(true);
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
	}
	public void checkReset() {
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && !isUnderCombat() && getPossibleTargets().isEmpty())
			setHitpoints(maxhp);

	}
	public void handleIngoingHit(Hit hit) {
	}
	
	@Override
    public double getMeleePrayerMultiplier() {
		return 0.35;
    }
	
	
	@Override
	public double getMagePrayerMultiplier() {
		return 0.25;
	}
	
	@Override
    public double getRangePrayerMultiplier() {
		return 0.25;
    }
	
	
	@Override
	public void sendDeath(Entity source) {
	super.sendDeath(source);	
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
