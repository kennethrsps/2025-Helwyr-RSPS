package com.rs.game.npc.gwd2.twinfuries;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.NewForceMovement;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class CeilingCollapse extends TwinSpecialAttack {
	
	private static final int[][] COLLAPSE_TILES = new int[][] { { 23, 23 }, { 28, 23 }, { 33, 23 }, { 38, 23 }, { 23, 28 }, { 28, 28 }, { 33, 28 }, { 38, 28 }, { 23, 33 }, { 28, 33 }, { 33, 33 }, { 38, 33 }, { 23, 38 }, { 28, 38 }, { 33, 38 }, { 38, 38 } };
	
	public CeilingCollapse(NPC npc, Entity entity) {
		super(npc, entity);
	}
	
	/**
	 * The 2 red fire balls bounce around the room throughout the kill, its only the newly added 4 phases which is where the blue blue is introduced
[+] Area of damage is 3x3
[+] 300-400 Typeless damage if within that 3x3 area (3000-4000 rs3)
[+] Location of the bounce is shown via a icon on the ground
[+]Blue ball is introduced after the explosion special has detonated 
[+] If you are under the blue ball when it bounces 600-680 damage to each of the twins (6000-6800 rs3)
[+]Blue ball bounces 3 times if the player isnt under it by the 3rd time it bounces one the red fireball released by on of the twins locks onto the player and deals upto 600 typless damage (6000 rs3)
[+]the blue ball stops at before the end of phase 4/start of phase 1 again

there some little bits ill keep going to find some of the more indepth stuff(edited)
Correction only one red fireball is bouncing during phase 1,2,3
Stage 4 the red twin releases another red/fireball and the other twin releases the blue ball
thats shitty to understand lol 1 sec
	 */

	@Override
	public void effect() {
		Nymora nymora = (Nymora) npc;
		final int random = Utils.random(COLLAPSE_TILES.length);
		final WorldTile t = nymora.getInstance().getWorldTile(COLLAPSE_TILES[random][0] - 1, COLLAPSE_TILES[random][1] - 1);
		nymora.setCantInteract(true);
		nymora.setNextFaceWorldTile(t);
		nymora.setNextForceTalk(new ForceTalk("We will purge them all!"));
		nymora.setNextForceMovement(new NewForceMovement(nymora, 0, t, 1, nymora.getDirection()));
		nymora.setNextAnimation(new Animation(28509));
		WorldTasksManager.schedule(new WorldTask() {
			int ticks;

			@Override
			public void run() {
				if (npc.isDead()) {
					stop();
					return;
				}
				if (ticks == 0) {
					nymora.setNextWorldTile(t);
					nymora.setNextFaceWorldTile(nymora.getInstance().getWorldTile(30, 31));
				} else if (ticks == 1) { 
					nymora.setNextAnimation(new Animation(28517));
				} else if (ticks == 2) 
					nymora.setNextAnimation(new Animation(28518));
				else if (ticks == 17) {
					nymora.setNextAnimation(new Animation(28519));
					nymora.setCantInteract(false);
					stop();
				} if (ticks > 2 && ticks % 2 == 0)
					sendStalagmites(nymora, random);
				else if (ticks > 3 && ticks % 2 == 1) {
					final WorldTile tile = new WorldTile(nymora.getCoordFaceX(nymora.getSize()), nymora.getCoordFaceY(nymora.getSize()), nymora.getPlane());
					loop: for (Player players : nymora.getInstance().getPlayers()) {
						for (int[] collapseTiles : COLLAPSE_TILES)
							if (players.withinDistance(nymora.getInstance().getWorldTile(collapseTiles[0], collapseTiles[1]), 3) && players.getDistance(tile) > 1) {
								players.applyHit(new Hit(nymora, Utils.random(50, 100), HitLook.REGULAR_DAMAGE));
								continue loop;
							}
					}
				}
				ticks++;
			}
		}, 0, 0);
	}
	
	private final void sendStalagmites(final Nymora nymora, final int random) {
		for (int i = 0; i < COLLAPSE_TILES.length; i++) {
			if (i == random)
				continue;
			for (Player p : nymora.getInstance().getPlayers())
				p.getPackets().sendGraphics(new Graphics(6145), nymora.getInstance().getWorldTile(COLLAPSE_TILES[i][0], COLLAPSE_TILES[i][1]));
		}
	}

}
