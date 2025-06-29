package com.rs.game.player.controllers;

import com.rs.game.Animation;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Handles the Player Jail Controller.
 * @author Zeus
 */
public class JailController extends Controller {

	@Override
	public void start() {
		if (player.getJailed() > Utils.currentTimeMillis())
			sendRandomJail(player);
	}

	@Override
	public void process() {
		if (player.getJailed() <= Utils.currentTimeMillis()) {
			player.getControlerManager().getControler().removeControler();
			player.sendMessage("Your account has been unjailed.");
			player.setNextWorldTile(new WorldTile(player.getHomeTile()));
		}
	}

	public static void stopControler(Player p) {
		p.getControlerManager().getControler().removeControler();
	}

	@Override
	public boolean sendDeath() {
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				player.stopAll();
				if (loop == 0) {
					player.setNextAnimation(new Animation(836));
				} else if (loop == 1) {
					player.sendMessage("Oh dear, you have died.");
				} else if (loop == 3) {
					player.setNextAnimation(new Animation(-1));
					player.reset();
					player.setCanPvp(false);
					sendRandomJail(player);
					player.unlock();
				}
				loop++;
			}
		}, 0, 1);
		return false;
	}

	@Override
	public boolean login() {
		return false;
	}

	@Override
	public boolean logout() {
		return false;
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		player.sendMessage("You are currently jailed for your delinquent acts.");
		return false;
	}

	@Override
	public boolean processObjectTeleport(WorldTile toTile) {
		player.sendMessage("You are currently jailed for your delinquent acts.");
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		player.sendMessage("You are currently jailed for your delinquent acts.");
		return false;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		player.sendMessage("You cannot do any activities while being jailed.");
		return false;
	}
	
	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
		if (interfaceId == 506 || interfaceId == 193) {
			player.sendMessage("You cannot do any activities while being jailed.");
			return false;
		}
		return true;
	}
	
	/**
	 * Sends the player to a random Jail Cell.
	 * @param p The Player to send.
	 */
	public void sendRandomJail(Player p) {
		p.stopAll(true, true, true);
		p.resetWalkSteps();
		int random = Utils.getRandom(7);
		switch (random) {
		case 0:
			p.setNextWorldTile(new WorldTile(2669, 10387, 0));
			break;
		case 1:
			p.setNextWorldTile(new WorldTile(2669, 10383, 0));
			break;
		case 2:
			p.setNextWorldTile(new WorldTile(2669, 10379, 0));
			break;
		case 3:
			p.setNextWorldTile(new WorldTile(2673, 10379, 0));
			break;
		case 4:
			p.setNextWorldTile(new WorldTile(2673, 10385, 0));
			break;
		case 5:
			p.setNextWorldTile(new WorldTile(2677, 10387, 0));
			break;
		case 6:
			p.setNextWorldTile(new WorldTile(2677, 10383, 0));
			break;
		}
	}
}