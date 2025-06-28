package com.rs.game.player.controllers;

import java.util.TimerTask;

import com.rs.cores.CoresManager;
import com.rs.game.Hit;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.Hit.HitLook;
import com.rs.game.player.Skills;
import com.rs.utils.Utils;

public class PyramidPlunder extends Controller {

    private boolean snakeUrn = false;

    private boolean snakeUrn2 = false;

    private boolean snakeUrn3 = false;

    private boolean snakeUrn4 = false;

    private boolean snakeUrn5 = false;

    private boolean snakeUrn6 = false;

    private boolean snakeUrn7 = false;
    private boolean snakeUrn8 = false;
    private boolean snakeUrn9 = false;
    private boolean snakeUrn10 = false;
    private boolean snakeUrn11 = false;
    private boolean snakeUrn12 = false;
    private boolean snakeUrn13 = false;
    private boolean Sarcophagus = false;
    private boolean GoldenChest = false;

    @Override
    public boolean login() {
	player.setNextWorldTile(new WorldTile(3288, 2801, 0));
	player.closeInterfaces();
	removeControler();
	return false;
    }

    @Override
    public boolean logout() {
	return true; // so doesnt remove script
    }

    @Override
    public void magicTeleported(int type) {
	player.closeInterfaces();
	removeControler();
    }

    @Override
    public void process() {
	if (snakeUrn == true && snakeUrn2 == true && snakeUrn3 == true
		&& snakeUrn4 == true && snakeUrn5 == true && snakeUrn6 == true
		&& snakeUrn7 == true && snakeUrn8 == true && snakeUrn9 == true
		&& snakeUrn10 == true && snakeUrn11 == true
		&& snakeUrn12 == true && snakeUrn13 == true
		&& Sarcophagus == true) {
	    player.getPackets().sendGameMessage(
		    "You've completed the Pyramid Plunder minigame!");
	    player.setNextWorldTile(new WorldTile(3288, 2801, 0));
	    player.getSkills().addXp(Skills.THIEVING, Utils.random(1000));
	    player.closeInterfaces();
	    removeControler();
	}
    }

    @Override
    public boolean processItemTeleport(WorldTile toTile) {
	player.closeInterfaces();
	removeControler();
	return true;
    }

    @Override
    public boolean processMagicTeleport(WorldTile toTile) {
	player.closeInterfaces();
	removeControler();
	return true;
    }

    @Override
    public boolean processObjectClick1(WorldObject object) {
	player.lock();
	CoresManager.fastExecutor.schedule(new TimerTask() {
	    @Override
	    public void run() {
		player.unlock();
	    }
	}, 800);
	if (object.getId() == 16531) {
	    if (snakeUrn == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2359, 3);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2359, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2359, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn = true;
	    }
	}
	if (object.getId() == 16522) {
	    if (snakeUrn2 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2350, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2350, 1);
		    }
		}, 500);
	    } else {
		player.getPackets().sendConfigByFile(2350, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn2 = true;
	    }
	}
	if (object.getId() == 16527) {
	    if (snakeUrn3 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2355, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2355, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2355, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn3 = true;
	    }
	}
	if (object.getId() == 16529) {
	    if (snakeUrn4 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2357, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2357, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2357, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn4 = true;
	    }
	}
	if (object.getId() == 16547) {
	    if (Sarcophagus == true) {
		player.getPackets().sendGameMessage(
			"You've already searched here.");
		return true;
	    }
	    player.getPackets().sendConfigByFile(2362, 2);
	    CoresManager.fastExecutor.schedule(new TimerTask() {
		@Override
		public void run() {
		    player.getPackets().sendConfigByFile(2362, 1);
		    player.getPackets().sendGameMessage(
			    "You've found a rune scimitar!");
		    player.getInventory().addItem(1333, 1);
		    Sarcophagus = true;
		}
	    }, 1000);
	}
	if (object.getId() == 16537) {
	    if (GoldenChest == true) {
		player.getPackets().sendGameMessage(
			"You've already searched here.");
		return true;
	    }
	    if (Utils.random(500) == 0) {
		player.getInventory().addItem(9044, 1);
		player.getPackets().sendGameMessage(
			"You found the rare item Pharaoh's sceptre.");
	    }
	    if (Utils.random(3) == 0) {
		player.getInventory().addItem(995, 100000);
	    } else {
		player.getPackets().sendGameMessage("You found nothing.");
	    }
	    player.getPackets().sendConfigByFile(2363, 1);
	}
	if (object.getId() == 16532) {
	    if (snakeUrn5 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2360, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2360, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2360, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn5 = true;
	    }
	}
	if (object.getId() == 16528) {
	    if (snakeUrn6 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2356, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2356, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2356, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn6 = true;
	    }
	}
	if (object.getId() == 16523) {
	    if (snakeUrn7 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2351, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2351, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2351, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn7 = true;
	    }
	}
	if (object.getId() == 16518) {
	    if (snakeUrn8 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2346, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2346, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2346, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn8 = true;
	    }
	}
	if (object.getId() == 16526) {
	    if (snakeUrn9 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2354, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2354, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2354, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn9 = true;
	    }
	}
	if (object.getId() == 16521) {
	    if (snakeUrn10 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2349, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2349, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2349, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn10 = true;
	    }
	}
	if (object.getId() == 16530) {
	    if (snakeUrn11 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2358, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2358, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2358, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn11 = true;
	    }
	}
	if (object.getId() == 16525) {
	    if (snakeUrn12 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2353, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2353, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2353, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn12 = true;
	    }
	}
	if (object.getId() == 16520) {
	    if (snakeUrn13 == true) {
		player.getPackets().sendGameMessage(
			"You've already searched in that urn.");
		return true;
	    }
	    if (Utils.random(3) == 0) {
		player.getPackets().sendConfigByFile(2348, 2);
		player.applyHit(new Hit(player, Utils.random(150),
			HitLook.DESEASE_DAMAGE, 0));
		player.getPackets().sendGameMessage("Ouch!");
		CoresManager.fastExecutor.schedule(new TimerTask() {
		    @Override
		    public void run() {
			player.getPackets().sendConfigByFile(2348, 1);
		    }
		}, 1500);
	    } else {
		player.getPackets().sendConfigByFile(2348, 1);
		player.getInventory().addItem(995, Utils.random(50000));
		snakeUrn13 = true;
	    }
	}
	if (object.getId() == 59795) {
	    player.getPackets().sendGameMessage("GOD!!!!!!");
	}
	return false;
    }

    @Override
    public boolean processObjectTeleport(WorldTile toTile) {
	player.closeInterfaces();
	removeControler();
	return true;
    }

    public void resetAllConfigs() {
	snakeUrn = false;
	snakeUrn2 = false;
	snakeUrn3 = false;
	snakeUrn4 = false;
	snakeUrn5 = false;
	snakeUrn6 = false;
	snakeUrn7 = false;
	snakeUrn8 = false;
	snakeUrn9 = false;
	snakeUrn10 = false;
	snakeUrn11 = false;
	snakeUrn12 = false;
	snakeUrn13 = false;
	Sarcophagus = false;
	GoldenChest = false;
	player.getPackets().sendConfigByFile(2359, 0);
	player.getPackets().sendConfigByFile(2350, 0);
	player.getPackets().sendConfigByFile(2355, 0);
	player.getPackets().sendConfigByFile(2357, 0);
	player.getPackets().sendConfigByFile(2362, 0);
	player.getPackets().sendConfigByFile(2363, 0);
	player.getPackets().sendConfigByFile(2360, 0);
	player.getPackets().sendConfigByFile(2356, 0);
	player.getPackets().sendConfigByFile(2351, 0);
	player.getPackets().sendConfigByFile(2346, 0);
	player.getPackets().sendConfigByFile(2354, 0);
	player.getPackets().sendConfigByFile(2358, 0);
	player.getPackets().sendConfigByFile(2353, 0);
	player.getPackets().sendConfigByFile(2348, 0);
    }

    @Override
    public boolean sendDeath() {
	player.setNextWorldTile(new WorldTile(3288, 2801, 0));
	player.closeInterfaces();
	removeControler();
	return true;
    }

    public void sendInterfaceConfigs() {
	player.getPackets().sendConfig(822, 8); // Level 8
    }

    @Override
    public void start() {
	resetAllConfigs();
	player.setNextWorldTile(new WorldTile(1974, 4424, 0));
	player.getInterfaceManager()
		.sendTab(
			player.getInterfaceManager().hasRezizableScreen() ? 11
				: 0, 428);
	sendInterfaceConfigs();
    }
}