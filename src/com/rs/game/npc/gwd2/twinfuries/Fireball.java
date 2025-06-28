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
import com.rs.game.activities.instances.TwinFuriesInstance;
import com.rs.game.npc.NPC;
import com.rs.utils.Utils;

public class Fireball extends TwinSpecialAttack {

	protected final TwinFuriesInstance instance;
	protected WorldTile tile;
	
	public Fireball(NPC npc, Entity entity) {
		super(npc, entity);
		instance = ((Avaryss) npc).getInstance();
	}

	@Override
	public void effect() {
		tile = instance.getWorldTile(Utils.random(21, 41), Utils.random(21, 41));
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
						final NewProjectile projectile = new NewProjectile(tile, to, 6151, 25, 25, 0, 35, 15, 0);
						World.sendProjectile(projectile);
						nextStep += projectile.getTime();
						tile = to;
						stage++;
					}
					ticks++;
				} catch (Exception e) {
				}
				return true;
			}
		}, 0, 1, TimeUnit.MILLISECONDS);
	}
	
	final WorldTile getNextTile() {
		return new WorldTile(tile.getX() + Utils.random(-10, 10), tile.getY() + Utils.random(-10, 10), tile.getPlane());
	}
	
	final boolean withinBoundaries(final WorldTile tile) {
		final WorldTile min = instance.getWorldTile(21, 21);
		final WorldTile max = instance.getWorldTile(40, 40);
		return tile.getX() >= min.getX() && tile.getY() >= min.getY() && tile.getX() <= max.getX() && tile.getY() <= max.getY();
	}

}
