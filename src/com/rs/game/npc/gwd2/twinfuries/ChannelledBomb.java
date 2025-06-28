package com.rs.game.npc.gwd2.twinfuries;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceMovement;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.NewProjectile;
import com.rs.game.WorldTile;
import com.rs.game.activities.instances.TwinFuriesInstance;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class ChannelledBomb extends TwinSpecialAttack {

	private static final int[][] FIRE_TILES = new int[][] { { 38, 23 }, { 33, 23 }, { 28, 23 }, { 23, 23 }, { 23, 28 }, { 23, 33 }, { 23, 38 }, { 28, 38 }, { 33, 38 }, { 38, 38 }, { 38, 33 }, { 38, 28 }, { 38, 23 } };
	private Avaryss avaryss;
	private Nymora nymora;
	private TwinFuriesInstance instance;
	private final WorldTile tile;

	public ChannelledBomb(NPC npc, Entity entity) {
		super(npc, entity);
		this.avaryss = (Avaryss) npc;
		this.instance = avaryss.getInstance();
		tile = instance.getWorldTile(30, 31);
		this.nymora = (Nymora) instance.getNymora();
	}

	@Override
	public void effect() {
		avaryss.setNextFaceWorldTile(instance.getWorldTile(30, 28));
		nymora.setNextFaceWorldTile(instance.getWorldTile(27, 31));
		avaryss.setNextForceMovement(new ForceMovement(instance.getWorldTile(30, 28), 1, npc.getDirection()));
		nymora.setNextForceMovement(new ForceMovement(instance.getWorldTile(27, 31), 1, npc.getDirection()));
		avaryss.getCombat().removeTarget();
		nymora.getCombat().removeTarget();
		avaryss.setNextAnimation(new Animation(28509));
		nymora.setNextAnimation(new Animation(28509));
		avaryss.setCannotMove(true);
		nymora.setCannotMove(true);
		instance.setChannelling(true);
		WorldTasksManager.schedule(new WorldTask() {
			private int ticks;
			private WorldTile nym;

			public void run() {
				if (avaryss.isDead() || nymora.isDead()) {
					instance.setChannelling(false);
					avaryss.setNextGraphics(new Graphics(-1));
					nymora.setNextGraphics(new Graphics(-1));
					stop();
					return;
				}
				if (ticks == 0) {
					avaryss.setNextWorldTile(instance.getWorldTile(30, 28));
					nymora.setNextWorldTile(instance.getWorldTile(27, 31));
					avaryss.setNextForceTalk(new ForceTalk("Burn, burn, BURN!"));
					nymora.setNextForceTalk(new ForceTalk("Come to me, my sister!"));
					avaryss.setNextFaceWorldTile(instance.getWorldTile(27, 28));
					nymora.setNextFaceWorldTile(instance.getWorldTile(42, 29));
					avaryss.setNextAnimation(new Animation(28508));
					nymora.setNextAnimation(new Animation(28507));
					avaryss.setNextGraphics(new Graphics(6141, 1, 1, 8));
					nymora.setNextGraphics(new Graphics(6142, 1, 1, 8));
					//avaryss.setNextSecondaryBar(new SecondaryBar(0, 850, 1, false));
					//nymora.setNextSecondaryBar(new SecondaryBar(0, 850, 1, false));
					for (int[] tiles : FIRE_TILES)
						instance.getPlayers().forEach(p -> p.getPackets().sendGraphics(new Graphics(6144), instance.getWorldTile(tiles[0], tiles[1])));
					nym = new WorldTile(nymora.getCoordFaceX(nymora.getSize()), nymora.getCoordFaceY(nymora.getSize()), nymora.getPlane());
				} else if (ticks >= 1 && ticks <= 27) {
					if (ticks == 27) {
						for (int[] tiles : FIRE_TILES)
							instance.getPlayers().forEach(p -> p.getPackets().sendGraphics(new Graphics(-1), instance.getWorldTile(tiles[0], tiles[1])));
					}
					avaryss.setNextFaceWorldTile(instance.getWorldTile(27, 28));
					nymora.setNextFaceWorldTile(instance.getWorldTile(42, 29));
					if (ticks % 1 == 0) {
						loop: for (Player players : instance.getPlayers()) {
							final NewProjectile projectile = new NewProjectile(tile, players, 2729, 90, 25, 0, 5, 30, 0);
							nymora.getInstance().getPlayers().forEach(p -> p.getPackets().sendTestProjectile(projectile));
							CoresManager.getServiceProvider().executeWithDelay(new Runnable() {

								@Override
								public void run() {
									try {
										players.applyHit(new Hit(nymora, nymora.getInstance().isHardMode() ? Utils.random(20, 70) : Utils.random(10, 35), HitLook.REGULAR_DAMAGE));
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								
							}, projectile.getTime(), TimeUnit.MILLISECONDS);

							for (int[] fireTiles : FIRE_TILES)
								if (players.withinDistance(instance.getWorldTile(fireTiles[0], fireTiles[1]), 2) && !players.withinDistance(nym, 2)) {
									players.applyHit(new Hit(nymora, Utils.random(100, 250), HitLook.REGULAR_DAMAGE));
									continue loop;
								}
						}
					}
				} else if (ticks == 29) {
					avaryss.setCannotMove(false);
					nymora.setCannotMove(false);
					avaryss.setNextGraphics(new Graphics(-1));
					nymora.setNextGraphics(new Graphics(-1));
					nymora.setNextAnimationForce(new Animation(28514));
					avaryss.setNextAnimationForce(new Animation(28513));
					instance.getPlayers().forEach(p -> {
						p.getPackets().sendGraphics(new Graphics(6143), tile);
						if (p.withinDistance(tile, 5))
							p.applyHit(new Hit(avaryss, Utils.random(300, 700), HitLook.REGULAR_DAMAGE));
					});

					instance.setChannelling(false);
				} else if (ticks == 30) {
					avaryss.setNextAnimationForce(new Animation(-1));
					nymora.setNextAnimationForce(new Animation(-1));
					avaryss.getCombat().setTarget(entity);
					nymora.getCombat().setTarget(entity);
					try {
					if (instance.isHardMode())
						new DoubleFireball(avaryss, null).effect();
					} catch (Exception e) {
						e.printStackTrace();
					}
					stop();
				}
				ticks++;
			}
		}, 0, 0);
	}

}
