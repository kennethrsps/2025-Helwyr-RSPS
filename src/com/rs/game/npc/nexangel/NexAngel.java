package com.rs.game.npc.nexangel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.activites.GodWarsBosses;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.godwars.GodWarMinion;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * 
 * @author Kingkenobi
 *
 */

@SuppressWarnings("serial")
public class NexAngel extends NPC{
	
	public GodWarMinion[] nexangelMinions;
	
	public NexAngel(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		this.setHitpoints(250000);
		this.getCombatDefinitions().setHitpoints(250000);
		setLureDelay(0);
		setCapDamage(2000);
		setRun(true);
		setForceMultiAttacked(true);
		setForceAgressive(true);
		nexangelMinions = new GodWarMinion[4];
			
		
	}
	
	@Override
	public void processNPC() {
		//test

		//end
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
		return 0.50;
    }
	
	
	@Override
	public double getMagePrayerMultiplier() {
		return 0.50;
	}
	
	@Override
    public double getRangePrayerMultiplier() {
		return 0.50;
    }
	
	
	/*@Override
	public void sendDeath(Entity source) {
	super.sendDeath(source);	
	}
	*/
	@Override
	public void sendDeath(Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(defs.getDeathEmote()));
				} else if (loop >= defs.getDeathDelay()) {
					drop();
					reset();
					setLocation(getRespawnTile());
					finish();
					setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}
	

	
	//respawn minion
	public  void respawnNexAngelMinions() {
		for (GodWarMinion minion : nexangelMinions) {
			if (minion.hasFinished() || minion.isDead())
				minion.respawn();
		}
	}
	@Override
	public void setRespawnTask() {
		final NPC npc = this;
		if (getBossInstance() != null && (getBossInstance().isFinished()
				|| (!getBossInstance().isPublic() && !getBossInstance().getSettings().hasTimeRemaining())))
			return;
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		long respawnDelay = getCombatDefinitions().getRespawnDelay() * 600;
		if (getBossInstance() != null)
			respawnDelay /= getBossInstance().getSettings().getSpawnSpeed();
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				setFinished(false);
				World.addNPC(npc);
				npc.setLastRegionId(0);
				World.updateEntityRegion(npc);
				loadMapRegions();
				checkMultiArea();
				respawnNexAngelMinions();
			}
		}, respawnDelay, TimeUnit.MILLISECONDS);
	}
	
	
	//respawn end
	
	
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
