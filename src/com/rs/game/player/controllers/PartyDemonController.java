package com.rs.game.player.controllers;

import com.rs.Settings;
import com.rs.game.WorldTile;
import com.rs.game.player.content.Magic;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class PartyDemonController extends Controller {
	
	@Override
	public void start() {
		Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3105, 3961, 0));
		player.sendMessage("Welcome to Oblivion boss arena! Items are lost here! Good luck!");
		
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
		removeControler();
		player.setLocation(new WorldTile(Settings.START_PLAYER_LOCATION));
		return false;
	}
	
	@Override
	public boolean sendDeath() {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					removeControler();
				}
			}, 6);
			return true;
		}
				
			
			
/*			@Override
/*			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					player.sendItemsOnDeath(player, true);
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
	}*/
	
}
