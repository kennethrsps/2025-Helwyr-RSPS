package com.rs.game.npc.gwd2.twinfuries;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.activities.instances.TwinFuriesInstance;
import com.rs.game.npc.Drop;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.utils.NPCDrops;
import com.rs.utils.Utils;

/**
 * @ausky Tom
 * @date April 14, 2017
 */

public class Avaryss extends NPC {

	private static final long serialVersionUID = -3119789621001568251L;
	private TwinFuriesInstance instance;
	private boolean finished;
	private long fireballDelay;

	public Avaryss(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned,
			TwinFuriesInstance instance) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceTargetDistance(50);
		setRun(true);
		setIntelligentRouteFinder(true);
		setForceAgressive(true);
		setForceMultiArea(true);
		this.instance = instance;
		instance.setPhase(0);
		setFreezeDelay(1);
		fireballDelay = Utils.currentTimeMillis() + 5000;
	}
	
	@Override
	public boolean canWalkNPC(int toX, int toY) {
		return true;
	}

	@Override
	public void spawn() {
		super.spawn();
		setNextAnimation(new Animation(28248));
	}
	public void checkReset() {
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && !isUnderCombat() && getPossibleTargets().isEmpty())
			setHitpoints(maxhp);
	}
	
	@Override
	public double getMeleePrayerMultiplier() {
		return 0.5;
	}
	
	@Override
	public void processNPC() {
		checkReset();
		super.processNPC();
		if (getInstance().isHardMode() && fireballDelay < Utils.currentTimeMillis()) {
			new Fireball(this, null).effect();
			fireballDelay = Utils.currentTimeMillis() + 20000;
		}
	}
	
	@Override
	public int getCapDamage() {
		return 1250;
	}
	
	@Override
	public void handleIngoingHit(Hit hit) {
		if (getCapDamage() != -1 && hit.getDamage() > getCapDamage())
			hit.setDamage(getCapDamage());
		if (instance.isChannelling())
			hit.setDamage(hit.getDamage() * 2);
		final int health = getHitpoints() - hit.getDamage();
		getInstance().getNymora().setHitpoints(health);
		super.handleIngoingHit(hit);
	}
	
	@Override
	public void sendDeath(final Entity source) {
		if (finished)
			return;
		finished = true;
		setHitpoints(0);
		super.sendDeath(source);
		getInstance().getNymora().sendDeath(source);
	}
	

	public TwinFuriesInstance getInstance() {
		return instance;
	}

}
