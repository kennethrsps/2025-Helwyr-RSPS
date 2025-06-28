package com.rs.game.player.content.xmas;

import java.io.Serializable;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.Hit.HitLook;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.xmas.XmasRiddles.Riddle;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/*import mysql.impl.NewsManager;*/

public class XmasEvent implements Serializable {

	/**
	 * Serialized id for serializing playersaves
	 */
	private static final long serialVersionUID = 1134341180698471821L;

	/**
	 * The player instance.
	 */
	private transient Player player;

	/**
	 * The player instance saving to.
	 * 
	 * @param player
	 *            The player.
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	/* Player snow energy & snowball upgrades */
		public int snowEnergy;
		public int snowmenKilled;
		public int bossKilled;
		public int damage;
		public boolean speed;
	
	/* Booleans and timers */
		public boolean intro;
		public boolean freedSanta;
		public boolean inXmas;
		public boolean inPresentBox;
		public boolean isSnowman;
		public boolean inThrow;
		public long snowballTick;
		public long thrownTick;
		public long healTime;
		public long bossTick;
	
	/* Riddle information and variables */
		public Riddle riddle;
		public boolean riddle1;
		public boolean riddle2;
		public boolean riddle3; 
		public boolean riddle4;
	
	public boolean finishedRiddles() {
		return (riddle1 && riddle2 && riddle3 && riddle4) ? true : false;
	}
	
	public void removeSnowball() {
		if(player.getEquipment().getWeaponId() == 33590) {
			player.getEquipment().removeAmmo(33590, 1);
			player.getEquipment().refresh(Equipment.SLOT_WEAPON);
		}
		if(player.getInventory().containsItem(33590, 1)) {
			player.getInventory().deleteItem(33590, 1);
			return;
		}
	}
	
	public void enter() {
		if(inXmas) {
			player.unlock();
			return;
		}
		player.setNextAnimation(new Animation(7376));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				FadingScreen.fade(player, 0, new Runnable() {

					@Override
					public void run() {
						player.unlock();
						player.setNextAnimation(new Animation(-1));
						player.setNextWorldTile(new WorldTile(2648, 5670, 0));
						inXmas = true;
					}
				});
			}
		}, 0);
	}
	
	public void leave() {
		player.setNextAnimation(new Animation(7376));
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				FadingScreen.fade(player, 0, new Runnable() {

					@Override
					public void run() {
						player.unlock();
						inXmas = false;
						Dialogue.closeNoContinueDialogue(player);
						player.getHintIconsManager().removeUnsavedHintIcon();
						player.getControlerManager().forceStop();
						player.setNextAnimation(new Animation(-1));
						player.setNextWorldTile(new WorldTile(2332, 3172, 0));
					}
				});
			}
		}, 0);
	}
	
	public int getDistance(NPC npc) {
		int x, y = 0;
		
		if(player.getX() > npc.getX())
			x = player.getX() - npc.getX();
		else
			x = npc.getX() - player.getX();
		
		if(player.getY() > npc.getY())
			x = player.getY() - npc.getY();
		else
			x = npc.getY() - player.getY();
		return x+y;
	}
	
	public void throwSnowball(NPC npc) {
		if(player.getUsername().equals("Zeus"))
			player.getXmas().damage = 100;
		if(Utils.currentTimeMillis() - thrownTick < (speed ? 5 : 2) || inThrow || npc.getHitpoints() == 0 || player.isDead())
			return;
		player.stopAll(true);
		player.faceEntity(npc);
		inThrow = true;
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				removeSnowball();
				thrownTick = Utils.currentTimeMillis();
				player.setNextAnimation(new Animation(385));
				World.sendProjectile(player, player, npc, 1281, 30, 25, 55, 25, 0, 0);
				int xdamage = Utils.random(damage * 100, (damage * 100)+30);
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() { 
						npc.applyHit(new Hit(player, xdamage, HitLook.MAGIC_DAMAGE));
						npc.faceEntity(player);
						npc.setNextGraphics(new Graphics(1282));
					}
				}, (player.withinDistance(npc, 3) ? 0 : 1));
			}
		}, 0);
	}
	
	public void snowballObject(WorldObject object) {
		player.stopAll(true);
		player.faceObject(object);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getInventory().deleteItem(33590, 1);
				thrownTick = Utils.currentTimeMillis();
				player.setNextAnimation(new Animation(385));
				World.sendProjectile(player, player, object, 1281, 30, 25, 55, 25, 0, 0);
			}
		}, 0);
	}
	
	public void announceDrop(String message) {
		String[] colors = { Colors.cyan, Colors.green, Colors.salmon };
 		World.sendWorldMessage(colors[Utils.random(colors.length)] + "<shad=000000><img=6>News: "
				+ player.getDisplayName() + message, false);
		/*new Thread(new NewsManager(player, "<b><img src=\"./bin/images/news/santa.jpg\" height=14> "
				+ player.getDisplayName() + message)).start();*/
	}
	
	public void traverse(boolean mansion) {
		player.setNextAnimation(new Animation(7376));
		WorldTile next = mansion ? new WorldTile(2593, 5577, 0) : new WorldTile(2674, 5661, 0);
		WorldTasksManager.schedule(new WorldTask() {
			
			@Override
			public void run() {
				FadingScreen.fade(player, 0, new Runnable() {
					@Override
					public void run() {
						player.unlock();
						player.setNextAnimation(new Animation(-1));
						player.setNextWorldTile(new WorldTile(next));
					}
				});
			}
		}, 0);
	}
}
