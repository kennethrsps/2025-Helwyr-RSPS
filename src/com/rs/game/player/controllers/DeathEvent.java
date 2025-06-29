package com.rs.game.player.controllers;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.MapBuilder;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.content.Magic;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class DeathEvent extends Controller {

    private static enum Stages { LOADING, RUNNING, DESTROYING }

    private int[] boundChuncks;

    private Stages stage;

    public void destroyRoom() {
		if (stage != Stages.RUNNING)
		    return;
		stage = Stages.DESTROYING;
		CoresManager.slowExecutor.schedule(new Runnable() {
		    @Override
		    public void run() {
		    	MapBuilder.destroyMap(boundChuncks[0], boundChuncks[1], 8, 8);
		    }
		}, 1200, TimeUnit.MILLISECONDS);
    }

    @Override
    public void forceClose() {
    	player.setLocation(player.getHomeTile());
    	destroyRoom();
    }

    public void loadRoom() {
		stage = Stages.LOADING;
		player.lock(12); // locks player
		CoresManager.slowExecutor.execute(new Runnable() {
		    @Override
		    public void run() {
		    	boundChuncks = MapBuilder.findEmptyChunkBound(2, 2);
		    	MapBuilder.copyMap(246, 662, boundChuncks[0], boundChuncks[1], 2, 2, new int[1], new int[1]);
		    	player.reset();
		    	player.setNextWorldTile(new WorldTile(boundChuncks[0] * 8 + 10, boundChuncks[1] * 8 + 6, 0));
		    	player.setNextAnimation(new Animation(-1));
		    	WorldTasksManager.schedule(new WorldTask() {
		    		@Override
		    		public void run() {
		    			player.getPackets().sendMiniMapStatus(2);
		    			player.unlock(); // unlocks player
		    			stage = Stages.RUNNING;
		    		}
		    	}, 1);
		    }
		});
    }

    @Override
    public boolean login() {
    	Magic.sendObjectTeleportSpell(player, true, player.getHomeTile());
    	return false;
    }

    @Override
    public boolean logout() {
    	return false;
    }

    @Override
    public void magicTeleported(int type) {
    	destroyRoom();
		player.getPackets().sendMiniMapStatus(0);
		removeControler();
    }

    @Override
    public boolean processItemTeleport(WorldTile toTile) {
    	return false;
    }

    @Override
    public boolean processMagicTeleport(WorldTile toTile) {
    	return false;
    }
    
    @Override
    public boolean processObjectClick1(WorldObject object) {
    	if (object.getId() == 45803) {
    		Magic.sendObjectTeleportSpell(player, true, player.getHomeTile());
    		return false;
    	}
    	return true;
    }

    @Override
    public void start() {
    	loadRoom();
    }
}