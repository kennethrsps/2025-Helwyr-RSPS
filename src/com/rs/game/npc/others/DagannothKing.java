package com.rs.game.npc.others;

import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.WorldTile;
import com.rs.game.activities.instances.DagannothKingsInstance;
import com.rs.game.npc.NPC;
import com.rs.utils.Logger;

/**
 * Creates the Dagannoth King NPC's.
 *
 * @ausky Noel
 */
public class DagannothKing extends NPC {

	/**
	 * Generated serial UID.
	 */
	private static final long serialVersionUID = 2820530565457808392L;
	
	private DagannothKingsInstance instance;
	
	public DagannothKing(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned, DagannothKingsInstance instance) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceTargetDistance(7);
		setLureDelay(1000);
		setForceAgressive(false);
		this.instance = instance;
	}
	
	@Override
	public void setRespawnTask() {
		if (!hasFinished()) {
			reset();
			setLocation(getRespawnTile());
			finish();
		}
		long respawnDelay = getCombatDefinitions().getRespawnDelay() * 600;
		if (instance != null)
			respawnDelay = instance.getRespawnSpeed() * 1000;
		if (Settings.DEBUG)
			System.out.println("Respawn task initiated: [" + getName() + "]; time: [" + respawnDelay + "].");
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawn();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, respawnDelay, TimeUnit.MILLISECONDS);
	}

	@Override
	public void processNPC() {
		if (isDead() || isLocked())
			return;
		if (!withinDistance(getRespawnTile(), 7)) {
			removeTarget();
			forceWalkRespawnTile();
		}
		super.processNPC();
	}
	
	/**
	 * 2881 supreme 2882 prime 2883 rex
	 */
	@Override
	public void handleIngoingHit(Hit hit) {
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		if ((getId() == 2881 && hit.getLook() != HitLook.MELEE_DAMAGE)
				|| (getId() == 2882 && hit.getLook() != HitLook.RANGE_DAMAGE)
				|| (getId() == 2883 && hit.getLook() != HitLook.MAGIC_DAMAGE))
			hit.setDamage(hit.getDamage() / 5);
		super.handleIngoingHit(hit);
	}
}