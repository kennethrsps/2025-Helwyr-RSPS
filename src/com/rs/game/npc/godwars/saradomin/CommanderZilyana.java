package com.rs.game.npc.godwars.saradomin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import com.rs.utils.Utils;

@SuppressWarnings("serial")
public class CommanderZilyana extends NPC {

	public GodWarMinion[] commanderMinions;

	public CommanderZilyana(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setCapDamage(1000);
		setLureDelay(1000);
		setForceTargetDistance(64);
		setForceFollowClose(true);
		setIntelligentRouteFinder(true);
		commanderMinions = new GodWarMinion[3];
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
									&& player.getAttackedByDelay() > Utils.currentTimeMillis())
							|| !clipedProjectile(player, false))
						continue;
					possibleTarget.add(player);
				}
			}
		}
		return possibleTarget;
	}

	/*
	 * gotta override else setRespawnTask override doesnt work
	 */

	public void respawnSaradominMinions() {
		for (GodWarMinion minion : commanderMinions) {
			if (minion.hasFinished() || minion.isDead())
				minion.respawn();
		}
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

	@Override
	public void setRespawnTask() {
		final NPC npc = this;
		if (getBossInstance() != null && (getBossInstance().isFinished()
				|| (!getBossInstance().isPublic() && !getBossInstance().getSettings().hasTimeRemaining())))
			return;
		// Particulary for Dominion Tower.
		if (!GodWarsBosses.isAtGodwars(npc))
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
				try {
					setFinished(false);
					World.addNPC(npc);
					npc.setLastRegionId(0);
					World.updateEntityRegion(npc);
					loadMapRegions();
					checkMultiArea();
					respawnSaradominMinions();
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				}
			}
		}, respawnDelay, TimeUnit.MILLISECONDS);
	}
}