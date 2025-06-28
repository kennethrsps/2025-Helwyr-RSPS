package com.rs.game.npc.gwd2.twinfuries;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.cores.FixedLengthRunnable;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.NewForceMovement;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class WallCharge extends TwinSpecialAttack {

	public WallCharge(NPC npc, Entity entity) {
		super(npc, entity);
	}

	private static final String[] WALL_CHARGE_MESSAGES = new String[] { "Ha ha ha!", "Keep them busy, sister!", "Think you can dodge me?" };

	@Override
	public void effect() {
		final Avaryss npc = (Avaryss) this.npc;
		CoresManager.getServiceProvider().scheduleFixedLengthTask(new FixedLengthRunnable() {
			private int ms, ticks, distance;
			private WorldTile tile = npc.getInstance().getWorldTile(entity.getXInRegion() - 1, 20);
			private WorldTile tt;
			private boolean flying;
			final int x = (npc.getInstance().getWorldTile(0, 0).getX() + 21);

			@Override
			public boolean repeat() {
				if (flying) {
					if (ms % 63 == 0) {
						byte[] dirs = Utils.DIRS[npc.getDirection() / 2048];
						final WorldTile npcTile = new WorldTile(npc.getCoordFaceX(npc.getSize()) + (distance * dirs[0]), npc.getCoordFaceY(npc.getSize()) + (distance * dirs[1]), npc.getPlane());
						npc.getInstance().getPlayers().forEach(p -> {
							if (p.getTileHash() == npcTile.getTileHash() && !p.isLocked()) {
								final WorldTile pTile = new WorldTile(tile.getX() + 1, tile.getY() + 1, tile.getPlane());
								p.stopAll();
								p.lock();
								final NewForceMovement movement = new NewForceMovement(p, 0, pTile, p.getDistance(pTile) * 9, npc.getDirection());
								movement.setForceMovementPrecise(true);
								p.setNextForceMovement(movement);
								p.setNextAnimationForce(new Animation(10070));
								if (p.equals(entity))
									tt = pTile;
								WorldTasksManager.schedule(new WorldTask() {
									private boolean force;

									@Override
									public void run() {
//										if (force) {
//											p.addWalkSteps(p.getX() + 1, p.getY(), 1, false);
//											stop();
//											return;
//										}
										p.setNextWorldTile(pTile);
										p.unlock();
										p.applyHit(new Hit(npc, npc.getInstance().isHardMode() ? Utils.random(250, 400) : Utils.random(150, 200), HitLook.REGULAR_DAMAGE));
										if (npc.getInstance().isHardMode())
											p.getPrayer().closeAllPrayers();
										if (pTile.getX() <= x)
											force = true;
										else
											stop();

									}
								}, 2, 0);
							}
						});
						distance++;
					}
				}
				if (ms % 600 == 0) {
					if (npc.isDead())
						return false;
					switch (ticks) {
					case 0:
						npc.setCantInteract(true);
						npc.setTarget(null);
						npc.setNextForceTalk(new ForceTalk(WALL_CHARGE_MESSAGES[Utils.random(3)]));
						npc.setNextAnimation(new Animation(28502));
						npc.setNextForceMovement(new ForceMovement(npc.getInstance().getWorldTile(tt != null ? tt.getXInRegion() - 1 : entity.getXInRegion() - 1, 39), 1, getDirection(ticks)));
						npc.setNextFaceWorldTile(tile);
						break;
					case 1:
					case 7:
					case 13:
					case 19:
						if (ticks == 1)
							npc.setNextWorldTile(npc.getInstance().getWorldTile(tt != null ? tt.getXInRegion() - 1 : entity.getXInRegion() - 1, 39));
						npc.setNextAnimation(new Animation(28503));
						break;
					case 2:
					case 8:
					case 14:
					case 20:
						flying = true;
						tt = null;
						npc.setNextForceMovement(new ForceMovement(tile, 3, getDirection(ticks)));
						npc.setNextAnimation(new Animation(28504));
						npc.setNextGraphics(new Graphics(6146));
						break;
					case 4:
					case 16:
						flying = false;
						distance = 0;
						npc.setNextAnimation(new Animation(28502));
						npc.setNextFaceWorldTile(npc.getInstance().getWorldTile(ticks == 3 ? 39 : 20, tt != null ? tt.getYInRegion() - 1 : entity.getYInRegion() - 1));
						break;
					case 5:
					case 11:
					case 17:
						npc.setNextForceTalk(new ForceTalk(WALL_CHARGE_MESSAGES[Utils.random(3)]));
						npc.setNextAnimation(new Animation(28506));
						npc.setNextForceMovement(new ForceMovement(ticks == 11 ? (npc.getInstance().getWorldTile(tt != null ? tt.getXInRegion() - 1 : entity.getXInRegion() - 1, 20)) : npc.getInstance().getWorldTile(ticks == 5 ? 20 : 39, tt != null ? tt.getYInRegion() - 1 : entity.getYInRegion() - 1), 1, getDirection(ticks)));
						npc.setNextWorldTile(tile);
						break;
					case 6:
					case 18:
						npc.setNextWorldTile(npc.getInstance().getWorldTile(ticks == 6 ? 20 : 39, tt != null ? tt.getYInRegion() - 1 : entity.getYInRegion() - 1));
						npc.setNextAnimation(new Animation(28502));
						tile = npc.getInstance().getWorldTile(ticks == 6 ? 39 : 20, tt != null ? tt.getYInRegion() - 1 : entity.getYInRegion() - 1);
						npc.setNextFaceWorldTile(tile);
						break;
					case 10:
						flying = false;
						distance = 0;
						npc.setNextAnimation(new Animation(28502));
						npc.setNextFaceWorldTile(npc.getInstance().getWorldTile(tt != null ? tt.getXInRegion() - 1 : entity.getXInRegion() - 1, 20));
						break;
					case 12:
						npc.setNextWorldTile(npc.getInstance().getWorldTile(tt != null ? tt.getXInRegion() - 1 : entity.getXInRegion() - 1, 20));
						npc.setNextAnimation(new Animation(28502));
						tile = npc.getInstance().getWorldTile(tt != null ? tt.getXInRegion() - 1 : entity.getXInRegion() - 1, 39);
						npc.setNextFaceWorldTile(tile);
						break;
					case 23:
						flying = false;
						distance = 0;
						npc.setNextWorldTile(tile);
						npc.setNextAnimation(new Animation(28505));
						break;
					case 24:
						if (npc.getX() < x)
							npc.addWalkSteps(npc.getX() + 2, npc.getY(), 2, false);
						npc.getCombat().setTarget(entity);
						npc.setCantInteract(false);
						if (!npc.getRun())
							npc.setRun(true);
						return false;
					}
					ticks++;
				}
				ms++;
				return true;
			}

		}, 0, 1, TimeUnit.MILLISECONDS);
	}

	private final int getDirection(int phase) {
		switch (phase) {
		case 0:
		case 2:
			return ForceMovement.SOUTH;
		case 5:
		case 11:
		case 8:
			return ForceMovement.EAST;
		case 14:
		case 17:
			return ForceMovement.NORTH;
		default:
			return ForceMovement.WEST;
		}
	}

}
