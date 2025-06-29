package com.rs.game.player.controllers.zombie;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activites.zombie.ZombieMinigame;
import com.rs.game.player.controllers.Controller;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class ZombieControler extends Controller {

	private transient ZombieMinigame minigame;

	@Override
	public void start() {
		minigame = (ZombieMinigame) this.getArguments()[0];
		this.setArguments(null);
		player.setForceMultiArea(true);
		sendInterfaces();
	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendOverlay(730, false);
		player.getPackets().sendHideIComponent(730, 7, true);
		player.getPackets().sendHideIComponent(730, 23, true);
		player.getPackets().sendIComponentText(730, 26, "Time left: Waiting.");
		player.getPackets().sendIComponentText(730, 17, "Current Wave: Waiting.");
	}

	@Override
	public void magicTeleported(int type) {
		leave(2);
	}

	private int zombiesKillCount;

	public void processNPCDeath(int id) {
		zombiesKillCount++;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.getDialogueManager().startDialogue("SimpleMessage", "You can't leave just like that!");
		return false;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		return false;
	}

	@Override
	public boolean sendDeath() {
		player.lock(7);
		player.stopAll();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.getPackets().sendGameMessage("You have died!");
				} else if (loop == 3) {
					player.reset();
					leave(2);
					player.setNextAnimation(new Animation(-1));
				} else if (loop == 4) {
					player.getPackets().sendMusicEffect(90);
					stop();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean login() {
		leave(2);
		return false;
	}

	@Override
	public boolean logout() {
		leave(1);
		this.setArguments(null);
		return false;
	}

	public void leave(int type) {// 0 win/lose || 1 logout || 2 normal
		if (type == 0 || type == 2) {
			player.setNextWorldTile(Settings.START_PLAYER_LOCATION);
			player.getInterfaceManager().closeOverlay(false);
		} else {
			player.setLocation(Settings.START_PLAYER_LOCATION);
		}
		if (type != 0)
			minigame.removePlayer(player);
		player.setForceMultiArea(false);
		player.getInterfaceManager().closeOverlay(false);
		removeControler();
	}

	@Override
	public void forceClose() {
		leave(0);
	}

	public int getZombiesKillCount() {
		return zombiesKillCount;
	}
}
