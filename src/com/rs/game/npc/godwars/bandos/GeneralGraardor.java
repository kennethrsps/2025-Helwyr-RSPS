package com.rs.game.npc.godwars.bandos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.activites.GodWarsBosses;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.godwars.GodWarMinion;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

@SuppressWarnings("serial")
public class GeneralGraardor extends NPC {

	public GodWarMinion[] graardorMinions;

	public GeneralGraardor(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setCapDamage(1000);
		setLureDelay(1000);
		setForceTargetDistance(64);
		setForceFollowClose(true);
		setIntelligentRouteFinder(true);
		graardorMinions = new GodWarMinion[3];
	}

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

	public  void respawnBandosMinions() {
		for (GodWarMinion minion : graardorMinions) {
			if (minion.hasFinished() || minion.isDead())
				minion.respawn();
		}
	}

	@Override
	public void setRespawnTask() {
		final NPC npc = this;
		// Particulary for Dominion Tower.
		if (getBossInstance() != null && (getBossInstance().isFinished()
				|| (!getBossInstance().isPublic() && !getBossInstance().getSettings().hasTimeRemaining())))
			return;
		if (!GodWarsBosses.isAtGodwars(npc)) {
			if (Settings.DEBUG)
				System.out.println("We no respawn here <.< " + npc.getName());
			return;
		}
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
				respawnBandosMinions();
			}
		}, respawnDelay, TimeUnit.MILLISECONDS);
	}
}