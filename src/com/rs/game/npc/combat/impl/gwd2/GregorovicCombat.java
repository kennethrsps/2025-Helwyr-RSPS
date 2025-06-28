package com.rs.game.npc.combat.impl.gwd2;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.cores.FixedLengthRunnable;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.NewProjectile;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScript;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.gwd2.gregorovic.Gregorovic;
import com.rs.game.npc.gwd2.gregorovic.Spirit;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class GregorovicCombat extends CombatScript {

	@Override
	public Object[] getKeys() {
		return new Object[] { 22443 };
	}

	/**
	 * Sends the basic glaive attack.
	 */
	private final int shurikenAttack(Gregorovic npc, Entity target) {
		npc.setNextAnimation(new Animation(28228));
		final NewProjectile projectile = new NewProjectile(npc, target, 6132, 35, 40, 25, 10, 35, 0);
		World.sendProjectile(projectile);
		try {
			CoresManager.getServiceProvider().executeWithDelay(new Runnable() {

				@Override
				public void run() {
					delayHit(npc, 0, target, getRangeHit(npc, getRandomMaxHit(npc, (int) (Utils.random(115) * npc.getDamageBoost()), NPCCombatDefinitions.RANGE, target)));
				}
				
			}, projectile.getTime(), TimeUnit.MILLISECONDS);
		} catch (Exception e) {}
		return 3;
	}

	/**
	 * Sends the trick knife attack:
	 * NPC -> Target -> NPC -> Random possible target -> NPC -> Random possible target.
	 */
	private final int trickKnife(Gregorovic npc, Entity t) {
		npc.setNextAnimation(new Animation(28229));
		if (t != null) {
			try {
				CoresManager.getServiceProvider().scheduleFixedLengthTask(new FixedLengthRunnable() {
					private int stage, time, offset;
					private Entity target = t;
					private NewProjectile projectile = new NewProjectile(npc, target, 6135, 35, 40, 70, 10, 35, 0);

					@Override
					public boolean repeat() {
						try {
							if (time == 0)
								World.sendProjectile(projectile);
							if (time == projectile.getTime() + offset) {
								boolean even = stage % 2 == 0;
								if (even)
									target.applyHit(new Hit(npc, (int) (Utils.random(230) * npc.getDamageBoost()), HitLook.RANGE_DAMAGE));
								if (stage != 4) {
									List<Entity> targets = npc.getPossibleTargets(true, true);
									if (targets.size() == 0) {
										return false;
									}
									target = targets.get(Utils.random(targets.size()));
									offset += projectile.getTime();
									if (even)
										projectile = new NewProjectile(target, npc, 6135, 35, 40, 25, 10, 35, -2);
									else
										projectile = new NewProjectile(new WorldTile(npc.getCoordFaceX(npc.getSize()), npc.getCoordFaceY(npc.getSize()), npc.getPlane()), target, 6135, 35, 40, 25, 10, 35, 0);
									World.sendProjectile(projectile);
								}
								if (stage == 4)
									return false;
								stage++;
							}
							time++;
							} catch (Exception e) {
								e.printStackTrace();
							}
						return true;
					}
					
				}, 0, 1, TimeUnit.MILLISECONDS);

			} catch (Exception e) {}
		}
		return 4;
	}

	/**
	 * Summons the spirit closest to him.
	 */
	private final int summonSpirit(Gregorovic gregorovic, Entity target) {
		final int type = getMaskType(gregorovic);
		gregorovic.setNextForceTalk(new ForceTalk("RISE, CHILD!"));
		NewProjectile projectile = new NewProjectile(gregorovic.getFrom()[type], gregorovic.getTo()[type], 2263, 50, 50, 0, 20, 30, 0);
		//World.sendProjectile(projectile);
		try {
			CoresManager.getServiceProvider().executeWithDelay(new Runnable() {

				@Override
				public void run() {
					new Spirit(22450 + type, gregorovic.getTo()[type], -1, true, true, gregorovic.getInstance());
				}
				
			}, projectile.getTime(), TimeUnit.MILLISECONDS);
		} catch (Exception e) {
		}
		return 4;
	}

	/**
	 * Gets the type of the closest mask.
	 */
	private final int getMaskType(Gregorovic gregorovic) {
		int type = 0;
		for (int i = 1; i < 3; i++)
			if (gregorovic.getDistance(gregorovic.getInstance().getMasks()[i]) < gregorovic.getDistance(gregorovic.getInstance().getMasks()[type]))
				type = i;
		return type;
	}

	/**
	 * Sends the glaives up in the sky which will fall down into random spots after about 8 seconds.
	 */
	private final int glaiveThrow(Gregorovic gregorovic, Entity target) {
		gregorovic.setNextAnimation(new Animation(28494));
		loop : for (int x = 0; x < 50; x++) {
			final WorldTile t = gregorovic.getInstance().getWorldTile(Utils.random(33, 54), Utils.random(33, 54));
			for (WorldTile tile : gregorovic.getTiles()) {
				if (tile.getTileHash() == t.getTileHash())
					continue loop;
			}
			gregorovic.getTiles().add(t);
		}
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks = 0;
			private final List<WorldTile> t = new ArrayList<WorldTile>();
			//private final List<WorldTile> t2 = new ArrayList<WorldTile>();

			@Override
			public void run() {
				switch (ticks) {
				case 0:
					gregorovic.getTiles().forEach(tile -> {
						if (Utils.random(100) > 50) {
							t.add(tile);
							for (Player p : gregorovic.getInstance().getPlayers())
								p.getPackets().sendGraphics(new Graphics(6139), tile);
						}
					});
					break;
				//case 1:
				//	gregorovic.getTiles().forEach(tile -> {
				//		if (!t.contains(tile)) {
				//			t2.add(tile);
				//			for (Player p : gregorovic.getInstance().getPlayers())
				////				p.getPackets().sendGraphics(new Graphics(6139), tile);
				//		}
				//	});
				//	break;
				case 7:
					for (WorldTile tile : t)
						if (target.withinDistance(tile, 1))
							target.applyHit(new Hit(target, (int) (Utils.random(100, 170) * gregorovic.getDamageBoost()), HitLook.MAGIC_DAMAGE));
					break;
				//case 8:
				//	for (WorldTile tile : t2)
				//		if (target.withinDistance(tile, 1))
				//			target.applyHit(new Hit(target, (int) (Utils.random(100, 170) * gregorovic.getDamageBoost()), HitLook.MAGIC_DAMAGE));
				//	break;
				case 9:
					gregorovic.getTiles().clear();
					stop();
					break;
				}
				ticks++;
			}
		}, 1, 0);
		return 6;
	}

	@Override
	public int attack(NPC npc, Entity target) {
		Gregorovic gregorovic = (Gregorovic) npc;
		if (Utils.random(10) < gregorovic.getManiaBuff())
			gregorovic.skipBasicAttacks();
		final int phase = gregorovic.getPhase();
		gregorovic.nextPhase();
		if (gregorovic.getPhase() < 0 || gregorovic.getPhase() > 12)
			gregorovic.setPhase(0);
		if (Utils.random(5) == 1) {
			final int damage = (int) (Utils.random(10, 40) * gregorovic.getDamageBoost()) + (target.getPoison().getPoisonDamage() > 300 ? 300 : target.getPoison().getPoisonDamage());
			target.getPoison().makePoisoned(damage);
		}
		switch (phase) {
		case 3:
			return trickKnife(gregorovic, target);
		case 7:
			return summonSpirit(gregorovic, target);
		case 11:
			return glaiveThrow(gregorovic, target);
		default:
			return shurikenAttack(gregorovic, target);
		}
	}

}