package com.rs.game.npc.ballak;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.WorldTile;
import com.rs.game.player.controllers.Controller;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class BallakController extends Controller {

	@Override
	public void start() {
		
	}
	
	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		removeControler();
		return true;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		removeControler();
		return true;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		removeControler();
		return true;
	}
	
	@Override
	public boolean logout() {
		player.setLocation(new WorldTile(Settings.START_PLAYER_LOCATION));
		return true;
	}
	
	@Override
	public boolean sendDeath() {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;
			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					player.sendItemsOnDeath(null, false);
					player.getEquipment().init();
					player.getInventory().init();
					player.reset();
					player.setNextWorldTile(new WorldTile(Settings.RESPAWN_PLAYER_LOCATION));
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					removeControler();
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

}
