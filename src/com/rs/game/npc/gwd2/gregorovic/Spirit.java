package com.rs.game.npc.gwd2.gregorovic;

import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.activities.instances.GregorovicInstance;
import com.rs.game.npc.NPC;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class Spirit extends NPC {

	private static final long serialVersionUID = -4645265833552120473L;
	private GregorovicInstance instance;

	public Spirit(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned, GregorovicInstance instance) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		this.instance = instance;
		((Gregorovic) instance.getGregorovic()).getSpirits().add(this);
		setIntelligentRouteFinder(true);
		setForceMultiArea(true);
		setNextAnimation(new Animation(24724));
		faceEntity(instance.getGregorovic());
		lock(1500);
	}
	
	@Override
	public boolean canWalkNPC(int toX, int toY) {
		return true;
	}
	
	@Override
	public void applyHit(final Hit hit) {
		super.applyHit(hit);
		if (!isLock() && hit.getSource() != null && !(hit.getSource() instanceof Gregorovic)) {
			resetWalkSteps();
			setLocked(true);
		}
	}

	@Override
	public void processNPC() {
		if (!isLock()) {
			if (!isUnderCombat()) {
				addWalkStepsInteract(instance.getGregorovic().getX(), instance.getGregorovic().getY(), 1, 1, true);
				if (getDistance(instance.getGregorovic()) < 1) {
					setLock(true);
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							resetWalkSteps();
							if (isDead())
								return;
							if (hasFinished())
								return;
							switch (getId()) {
							case 22450:
								setNextForceTalk(new ForceTalk("Hehehahahe."));
								((Gregorovic) getInstance().getGregorovic()).setManiaBuff(((Gregorovic) getInstance().getGregorovic()).getManiaBuff() + 1);
								break;
							case 22451:
								setNextForceTalk(new ForceTalk("Accept the unreal."));
								if (getInstance().getGregorovic().getCombat().getTarget() != null) {
									int damage = (int) (getInstance().getGregorovic().getCombat().getTarget().getPoison().getPoisonDamage() * 1.2);
									getInstance().getGregorovic().getCombat().getTarget().getPoison().setPoisonDamage(damage > 600 ? damage = (Utils.random(50) + 600) : damage);
								}
								break;
							case 22452:
								setNextForceTalk(new ForceTalk("Hatred is power."));
								getInstance().getGregorovic().setRangedBonuses((int) (getInstance().getGregorovic().getBonus(4) * 1.2));
								((Gregorovic) getInstance().getGregorovic()).boostDamage();
								break;
							}
							sendDeath(null);
							setNextAnimation(new Animation(24727));
						}
					});
				}
			} else
				setLock(true);
		}
		super.processNPC();
	}

	public GregorovicInstance getInstance() {
		return instance;
	}

}
