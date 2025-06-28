package com.rs.game.npc.kalphite;

import java.util.ArrayList;
import java.util.List;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

@SuppressWarnings("serial")
public class KalphiteKing extends NPC {

	private boolean usedImmortality;
	private int spawnCount;
	private int phase;
	private NPC king;
	private boolean started;
	public boolean isShieldActive, hasActivatedShield;
	private boolean siphon;

	/**
	 * Represents the marauder data.
	 */
	private byte spawnedMarauders;
	private NPC[] marauders;

	/**
	 * Constructs and spawns the Kalphite King NPC.
	 * 
	 * @param id
	 *            The NPC ID to spawn.
	 * @param tile
	 *            The WorldTile to spawn in.
	 * @param mapAreaNameHash
	 *            The Name of the Map Area hash.
	 * @param canBeAttackFromOutOfArea
	 *            if can be attacked from out of the area.
	 * @param spawned
	 *            if is a customly spawned NPC.
	 */
	public KalphiteKing(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		if (!Settings.DEBUG)
		this.setCapDamage(1650);
		setLureDelay(120);
		setForceTargetDistance(120);
		setIntelligentRouteFinder(true);
		this.setCantFollowUnderCombat(false);
		setForceAgressive(true);
		setFreezeImmune(true);
		setRun(true);
		start();
		phase = 0;
		king = this;
	}

	@Override
	public void spawn() {
		super.spawn();
		start();
	}

	@Override
	public void sendDeath(final Entity source) {
		if (!usedImmortality && Utils.random(100) < 15) {
			final Entity target = getCombat().getTarget();
			setCantInteract(true);
			resetReceivedDamage();
			setHitpoints((int) (getMaxHitpoints() * 0.40));
			usedImmortality = true;
			setNextAnimation(new Animation(19483));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					setCantInteract(false);
					if (target != null)
						setTarget(target);
				}
			}, 7);
			return;
		}
		removeMarauders();
		setNextNPCTransformation(16697);
		super.sendDeath(source);
	}

	private void start() {
		spawnCount = 0;
		usedImmortality = false;
		getOutsideEarth(null);
		setDirection(Utils.getAngle(0, -1));
		marauders = new NPC[6];
		hasActivatedShield = false;
	}

	public int getHPPercentage() {
		return getHitpoints() * 100 / getMaxHitpoints();
	}
	@Override
	public void processNPC() {
		if((getHPPercentage() < 75 && spawnCount < 1)
				|| (getHPPercentage() < 25 && spawnCount < 2))
			battleCry();		
		if (isDead())
			return;
		if (isShieldActive)
			setNextGraphics(new Graphics(siphon ? 3737 : 3736));

		if (!isCantInteract()) {
			for (Entity t : getPossibleTargets()) {
				if (Utils.colides(this, t))
					t.applyHit(new Hit(this, Utils.random(128) + 110, HitLook.REGULAR_DAMAGE));
			}
		}
		super.processNPC();
	}

	/**
	 * Spawns Exiled Kalphite Marauders to assistance.
	 */
	
	public void battleCry() {
		setNextAnimation(new Animation(19462));
		if (spawnedMarauders >= marauders.length)
			return;
		for (int tryI = 0; tryI < 10; tryI++) {
			WorldTile tile = new WorldTile(new WorldTile(2963 + Utils.random(20), 1749 + Utils.random(20), 0), 2);
			if (World.isTileFree(0, tile.getX(), tile.getY(), 1)) {
				NPC marauder = marauders[spawnedMarauders++] = new NPC(16706, tile, -1, true, true);
				marauder.setNextAnimation(new Animation(19492));
				marauder.setNextGraphics(new Graphics(3748));
				marauder.setForceAgressive(true);
				marauder.setForceTargetDistance(24);
				marauder.setIntelligentRouteFinder(true);
				for (Entity target : marauder.getPossibleTargets()) {
					if (Utils.colides(marauder, target))
						target.applyHit(new Hit(marauder, Utils.random(20, 30), HitLook.MELEE_DAMAGE));
				}
				break;
			}
		}
	}

	/**
	 * Kills all spawned Kalphite Marauders.
	 */
	public void removeMarauders() {
		for (NPC marauder : marauders) {
			if (marauder == null)
				continue;
			marauder.sendDeath(this);
		}
	}

	public void switchPhase() {
		int currentPhase = getCurrentPhase();
		int nextPhase = (currentPhase + Utils.random(2) + 1) % 3;
		setNextNPCTransformation(16697 + nextPhase);
		setNextGraphics(new Graphics(nextPhase == 0 ? 3750 : nextPhase == 1 ? 3749 : 3751));
		phase = 0;
	}

	public int getCurrentPhase() {
		return getId() - 16697;
	}

	@Override
	public void reset() {
		setNPC(16697 + Utils.random(3));
		super.reset();
	}

	@Override
	public void handleIngoingHit(Hit hit) {
		if (isShieldActive && siphon && hit.getDamage() > 0)
			hit.setHealHit();
		if (!siphon)
			super.handleIngoingHit(hit);
	}

	@Override
	public Hit handleOutgoingHit(Hit hit, Entity target) {
		if (isShieldActive && !siphon && hit.getDamage() > 0)
			this.healShield(hit.getDamage());
		return hit;
	}

	private void healShield(int dmg) {
		Hit h = new Hit(null, dmg, HitLook.HEALED_DAMAGE);
		h.setHealHit();
		this.applyHit(h);
		this.heal(dmg);
	}

	public void dig(final Entity target) {
		setNextAnimation(new Animation(19453));
		setNextGraphics(new Graphics(3746));
		setCantInteract(true);
		WorldTasksManager.schedule(new WorldTask() {

			boolean part1 = true;

			@Override
			public void run() {
				if (isDead()) {
					stop();
					return;
				}
				if (part1) {
					setFinished(true);
					part1 = false;
				} else {
					stop();
					if (target instanceof Player) {
						WorldTile loc = new WorldTile(target.getX() - (getSize() / 2), target.getY() - (getSize() / 2),
								target.getPlane());
						if (World.isFloorFree(loc.getPlane(), loc.getX(), loc.getY(), getSize()))
							setLocation(loc);
					}
					setFinished(false);
					getOutsideEarth(target);
				}
			}
		}, 6, 5);
	}

	private void getOutsideEarth(final Entity target) {
		setNextAnimation(new Animation(19451));
		setNextGraphics(new Graphics(3745));
		setCantInteract(true);
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				setCantInteract(false);
				if (target != null)
					setTarget(target);
				getCombat().setCombatDelay(5);
				setNextAnimation(new Animation(-1));
				for (Entity target : getPossibleTargets()) {
					if (Utils.colides(KalphiteKing.this, target)) {
						target.applyHit(new Hit(KalphiteKing.this, Utils.random(300, 900), HitLook.REGULAR_DAMAGE));
						if (target instanceof Player)
							target.setNextAnimation(new Animation(10070));
					}
				}
				if (king.getId() == 16699)
					phase = 0;
				else if (king.getId() == 16698 && started) {
					phase = 5;
					started = true;
				}
				stop();
			}
		}, 4);
	}

	/**
	 * Activated his healing/immortality shield.
	 */
	public void activateShield() {
		setNextAnimation(new Animation(19462));
		isShieldActive = true;
		if (Utils.random(4) == 1)
			siphon = true;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				isShieldActive = false;
				siphon = false;
				hasActivatedShield = true;
				stop();
			}
		}, 17);
	}

	public int getPhase() {
		return phase;
	}

	public void nextPhase() {
		phase++;
	}

	public void setPhase(int phase) {
		this.phase = phase;
	}
}