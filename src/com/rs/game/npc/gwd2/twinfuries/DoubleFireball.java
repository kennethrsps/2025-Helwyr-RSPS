package com.rs.game.npc.gwd2.twinfuries;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.cores.FixedLengthRunnable;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.NewProjectile;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class DoubleFireball extends Fireball {

	private WorldTile blueTile;
	private boolean forceTarget;
	
	public DoubleFireball(NPC npc, Entity entity) {
		super(npc, entity);
	}

	@Override
	public void effect() {
		tile = instance.getWorldTile(Utils.random(21, 41), Utils.random(21, 41));
		sendBlueBall();
		CoresManager.getServiceProvider().scheduleFixedLengthTask(new FixedLengthRunnable() {
			private int ticks, nextStep, stage;

			@Override
			public boolean repeat() {
				try {
					if (ticks == nextStep) {
						if (stage == 5) {
							instance.getPlayers().forEach(p -> {
								p.getPackets().sendGraphics(new Graphics(3522), tile);
								if (p.withinDistance(tile, 3))
									p.applyHit(new Hit(npc, Utils.random(600, 680), HitLook.REGULAR_DAMAGE));
							});
							return false;
						}
						WorldTile to;
						while (true) {
							if (withinBoundaries(to = getNextTile()))
								break;
						}
						if (forceTarget)
							to = instance.getPlayers().get(Utils.random(instance.getPlayers().size()));
						final NewProjectile projectile = new NewProjectile(tile, to, 6151, 25, 25, 0, 35, 15, 0);
						World.sendProjectile(projectile);
						nextStep += projectile.getTime();
						tile = to;
						stage++;
					}
					ticks++;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}

		}, 0, 1, TimeUnit.MILLISECONDS);
	}
	
	private final void sendBlueBall() {
		blueTile = instance.getWorldTile(Utils.random(21, 41), Utils.random(21, 41));
		CoresManager.getServiceProvider().scheduleFixedLengthTask(new FixedLengthRunnable() {
			private int ticks, nextStep, stage;

			@Override
			public boolean repeat() {
				try {
					if (ticks == nextStep) {
						if (stage != 0) {
							for (Player p : instance.getPlayers()) {
								if (p.getDistance(blueTile) < 2) {
									instance.getPlayers().forEach(pl -> {
										pl.getPackets().sendGraphics(new Graphics(6152), new WorldTile(instance.getAvaryss().getX(), instance.getAvaryss().getY() + 2, instance.getAvaryss().getPlane()));
										pl.getPackets().sendGraphics(new Graphics(6152), new WorldTile(instance.getNymora().getX(), instance.getNymora().getY() + 2, instance.getNymora().getPlane()));
									});
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											instance.getAvaryss().applyHit(new Hit(null, Utils.random(600, 680), HitLook.MAGIC_DAMAGE));
											instance.getNymora().applyHit(new Hit(null, Utils.random(600, 680), HitLook.MAGIC_DAMAGE));
										}
									}, 2);
									return false;
								}
							}
						}
						if (stage == 3) {
							forceTarget = true;
							return false;
						}
						WorldTile to;
						while (true) {
							if (withinBlueBoundaries(to = getNextBlueTile()))
								break;
						}
						final NewProjectile projectile = new NewProjectile(blueTile, to, 6147, 25, 25, 0, 35, 15, 0);
						World.sendProjectile(projectile);
						nextStep += projectile.getTime();
						blueTile = to;
						stage++;
					}
					ticks++;
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		}, 0, 1, TimeUnit.MILLISECONDS);
	}
	
	private final WorldTile getNextBlueTile() {
		return new WorldTile(blueTile.getX() + Utils.random(-3, 4), blueTile.getY() + Utils.random(-3, 4), blueTile.getPlane());
	}

	private final boolean withinBlueBoundaries(final WorldTile tile) {
		if (tile.getDistance(this.tile) != 3)
			return false;
		final WorldTile min = instance.getWorldTile(21, 21);
		final WorldTile max = instance.getWorldTile(40, 40);
		return tile.getX() >= min.getX() && tile.getY() >= min.getY() && tile.getX() <= max.getX() && tile.getY() <= max.getY();
	}
	
}
