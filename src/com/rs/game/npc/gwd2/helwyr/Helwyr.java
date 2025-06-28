package com.rs.game.npc.gwd2.helwyr;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activities.instances.HelwyrInstance;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * @ausky Tom
 * @date April 8, 2017
 */

public class Helwyr extends NPC {

	private static final long serialVersionUID = 7050003141268362974L;
	private int phase;
	private HelwyrInstance instance;

	public Helwyr(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned,
			HelwyrInstance instance) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setRun(true);
		setIntelligentRouteFinder(true);
		setForceMultiArea(true);
		setForceTargetDistance(50);
		setNoDistanceCheck(true);
		setCantInteract(true);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				setCantInteract(false);
			}
		}, 6);
		this.instance = instance;
		instance.getPlayers().forEach(p -> p.getTemporaryAttributtes().remove("bleed"));// Clearing out previously
																						// cached bleed effect.
	}

	@Override
	public boolean canWalkNPC(int toX, int toY) {
		return true;
	}

	@Override
	public double getMeleePrayerMultiplier() {
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
		super.handleIngoingHit(hit);
		// if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() !=
		// HitLook.RANGE_DAMAGE && hit.getLook() != HitLook.MAGIC_DAMAGE) {
		// HeartOfGielinor.refreshHealth(instance, getHitpoints() - hit.getDamage(),
		// getMaxHitpoints());
		// return;
		// }
		// handlePrayers(hit);
		// HeartOfGielinor.refreshHealth(instance, getHitpoints() - hit.getDamage(),
		// getMaxHitpoints());
	}

	@Override
	public void processNPC() {
		checkReset();
		super.processNPC();
		for (Player p : instance.getPlayers()) {
			if (p == null)
				continue;
			if (p.getTemporaryAttributtes().get("bleed") != null) {
				if (p.getTemporaryAttributtes().remove("skiptick") != null)
					continue;// This damage is applied every two ticks.
				final int bleed = (int) p.getTemporaryAttributtes().get("bleed");
				p.applyHit(new Hit(this, bleed, HitLook.REGULAR_DAMAGE));
				if (Utils.currentTimeMillis() - (p.getTemporaryAttributtes().get("bleedTime") == null ? 0
						: (long) p.getTemporaryAttributtes().get("bleedTime")) > 15000) {
					if (bleed <= 5) {
						p.getTemporaryAttributtes().remove("bleed");
						continue;
					}
					p.getTemporaryAttributtes().put("bleed", bleed - 5);
					p.getTemporaryAttributtes().put("bleedTime", Utils.currentTimeMillis());
				}
				p.getTemporaryAttributtes().put("skiptick", true);
			}
		}
		if (instance.getTiles().size() == 0)
			return;
		instance.getPlayers().forEach(p -> {
			boolean inGas = false;
			for (int i = 0; i < instance.getTiles().size(); i++) {
				if (instance.getTiles().get(i) != null && p.withinDistance(instance.getTiles().get(i), 2)) {
					p.applyHit(new Hit(this, Utils.random(10, 25), HitLook.REGULAR_DAMAGE));
					inGas = true;
					final long stunDelay = p.getTemporaryAttributtes().get("stunDelay") == null ? 0
							: (long) p.getTemporaryAttributtes().get("stunDelay");
					if (stunDelay == 0) {
						if (!p.isFrozen())
							p.getTemporaryAttributtes().put("stunDelay", Utils.currentTimeMillis());
					} else {
						if (stunDelay + 5000 < Utils.currentTimeMillis()) {
							p.addFreezeDelay(3000);
							p.resetWalkSteps();
							p.sendMessage("You feel a little dizzy after standing in the gas for too long.");
							p.getTemporaryAttributtes().remove("stunDelay");
						}
					}
				}
			}
			if (!inGas)
				p.getTemporaryAttributtes().remove("stunDelay");
		});
	}

	@Override
	public void sendDeath(final Entity source) {
		instance.getWolves().forEach(n -> {
			n.sendDeath(source);
			n.setNextAnimation(new Animation(23579));
		});
		setNextAnimation(new Animation(28204));
		instance.getPlayers().forEach(player -> {
			for (int x = 0; x < instance.getTiles().size(); x++) {
				World.sendGraphics(player, new Graphics(-1), instance.getTiles().get(x));
				World.spawnObject(new WorldObject(101899, 11, 1, instance.getTiles().get(x)));
			}
		});
		instance.getTiles().clear();
		super.sendDeath(source);
	}

	@Override
	public void spawn() {
		super.spawn();
		setNextAnimation(new Animation(28200));
		setNextGraphics(new Graphics(6120));
		setNextGraphics(new Graphics(6085));
	}

	public int getPhase() {
		return phase;
	}

	public void nextPhase() {
		phase++;
	}

	public void checkReset() {
		int maxhp = getMaxHitpoints();
		if (maxhp > getHitpoints() && !isUnderCombat() && getPossibleTargets().isEmpty())
			setHitpoints(maxhp);

	}

	public void setPhase(int phase) {
		this.phase = phase;
	}

	public HelwyrInstance getInstance() {
		return instance;
	}

}
