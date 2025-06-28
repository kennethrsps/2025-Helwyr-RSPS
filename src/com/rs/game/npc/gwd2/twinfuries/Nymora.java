package com.rs.game.npc.gwd2.twinfuries;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.activities.instances.TwinFuriesInstance;
import com.rs.game.npc.NPC;

/**
 * @ausky Tom
 * @date April 14, 2017
 */

public class Nymora extends NPC {

	private static final long serialVersionUID = 1739739909683329194L;
	private TwinFuriesInstance instance;
	private boolean finished;

	public Nymora(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, TwinFuriesInstance instance) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceTargetDistance(50);
		setRun(true);
		setIntelligentRouteFinder(true);
		setForceAgressive(true);
		setForceMultiArea(true);
		this.instance = instance;
		instance.setPhase(0);
		setFreezeDelay(1);
	}
	
	@Override
	public boolean canWalkNPC(int toX, int toY) {
		return true;
	}
	
	@Override
	public void spawn() {
		super.spawn();
		setNextAnimation(new Animation(28242));
	}
	
	@Override
	public double getRangePrayerMultiplier() {
		return 0.5;
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
		getInstance().getAvaryss().setHitpoints(health);
		super.handleIngoingHit(hit);
	}
	
	@Override
	public void sendDeath(final Entity source) {
		if (finished)
			return;
		finished = true;
		setHitpoints(0);
		super.sendDeath(source);
		getInstance().getAvaryss().sendDeath(source);
	}

//	public void drop() {
//		try {
//			final Drop[] drops = NPCDrops.getDrops(id);
//			final Player killer = getMostDamageReceivedSourcePlayer();
//			if (killer == null)
//				return;
//			increaseKillStatistics(killer, getInstance().isHardMode() ? "twin furies'(cm)" : "twin furies");
//			handleRingOfDeath(killer);
//			final Drop[] possibleDrops = new Drop[drops.length];
//			int possibleDropsCount = 0;
//			for (Drop drop : drops) {
//				if (drop.getRate() == 100)
//					sendDrop(killer, drop, false);
//				else {
//					double rate = killer.getHeart().getDropRate(getId(), drop);
//					double random = Utils.getRandomDouble(100);
//					if (rate < 30)
//						rate *= Settings.getDropQuantityRate(killer);
//					if (random <= rate && random != 100 && random != 0)
//						possibleDrops[possibleDropsCount++] = drop;
//				}
//			}
//			if (possibleDropsCount > 0)
//				sendDrop(killer, possibleDrops[Utils.getRandom(possibleDropsCount - 1)], false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		} catch (Error e) {
//			e.printStackTrace();
//		}
//	}


	public TwinFuriesInstance getInstance(){
		return instance;
	}

}
