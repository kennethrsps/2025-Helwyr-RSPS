package com.rs.game.npc.others.randoms;

import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Handles Wizard Finix NPC (Runecrafting random).
 * @author Zeus
 */
public class WizardFinixNPC extends NPC {

	private static final long serialVersionUID = -2385681443539672863L;
	
	private Player target;
    private long createTime;
    private boolean stop;

    public WizardFinixNPC(WorldTile tile, Player target) {
    	super(15419, tile, -1, true, true);
    	this.target = target;
    	createTime = Utils.currentTimeMillis();
		setNextForceTalk(new ForceTalk("Hey, "+target.getDisplayName()+", talk to me."));
		setRun(true);
    }

    /**
     * Gives the Player his reward.
     * @param player The player.
     */
    public void giveReward(final Player player) {
    	player.stopAll(true, false, true);
    	player.setNextAnimation(new Animation(-1));
    	if (player != target || player.isLocked()) {
    		player.getDialogueManager().startDialogue("SimpleNPCMessage", 
    				15419, "I don't have time for chit-chats.");
    		return;
    	}
    	if (player.getRunesMade() >= 1000) {
			if (!player.hasItem(new Item(21487)))
				player.addItem(new Item(21487, 1));
		}
    	if (player.getRunesMade() >= 2000) {
			if (!player.hasItem(new Item(21485)))
				player.addItem(new Item(21485, 1));
		}
    	if (player.getAirRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13631)))
				player.addItem(new Item(13631, 1));
    	}
    	if (player.getWaterRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13632)))
				player.addItem(new Item(13632, 1));
    	}
    	if (player.getEarthRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13633)))
				player.addItem(new Item(13633, 1));
    	}
    	if (player.getMindRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13630)))
				player.addItem(new Item(13630, 1));
    	}
    	if (player.getFireRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13634)))
				player.addItem(new Item(13634, 1));
    	}
    	if (player.getBodyRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13635)))
				player.addItem(new Item(13635, 1));
    	}
    	if (player.getCosmicRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13636)))
				player.addItem(new Item(13636, 1));
    	}
    	if (player.getChaosRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13637)))
				player.addItem(new Item(13637, 1));
    	}
    	if (player.getNatureRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13638)))
				player.addItem(new Item(13638, 1));
    	}
    	if (player.getLawRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13639)))
				player.addItem(new Item(13639, 1));
    	}
    	if (player.getDeathRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13640)))
				player.addItem(new Item(13640, 1));
    	}
    	if (player.getBloodRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13641)))
				player.addItem(new Item(13641, 1));
    	}
    	if (player.getAirRunesMade() >= 2500 && player.getMindRunesMade() >= 2500 && player.getWaterRunesMade() >= 2500
    			 && player.getEarthRunesMade() >= 2500 && player.getFireRunesMade() >= 2500 && player.getBodyRunesMade() >= 2500
    			 && player.getCosmicRunesMade() >= 2500 && player.getChaosRunesMade() >= 2500 && player.getNatureRunesMade() >= 2500
    			 && player.getLawRunesMade() >= 2500 && player.getDeathRunesMade() >= 2500 && player.getBloodRunesMade() >= 2500) {
			if (!player.hasItem(new Item(13642)))
				player.addItem(new Item(13642, 1));
    	}
    	if (player.getRunesMade() >= 3000) {
			if (!player.hasItem(new Item(21486)))
				player.addItem(new Item(21486, 1));
		}
    	if (player.getRunesMade() >= 4000) {
			if (!player.hasItem(new Item(21484)))
				player.addItem(new Item(21484, 1));
		}
		stop = true;
		player.addMoney(Utils.random(250000));
		setNextForceTalk(new ForceTalk("See you later, "+target.getDisplayName()+"!"));
		player.sendMessage("<col=ff0000>The Wizard gives you a reward before leaving.", true);
		player.lock(3);
    	WorldTasksManager.schedule(new WorldTask() {
    		@Override
    		public void run() {
    			player.unlock();
    			stop = false;
    			finish();
    			stop();
    		}
    	}, 1);
    }

    @Override
    public void processNPC() {
    	sendFollow(target);
    	if (target.hasFinished() || createTime + 60000 < Utils.currentTimeMillis())
    		finish();
    	if (!stop) {
    		if (Utils.random(50) <= 2) {
    			setNextForceTalk(new ForceTalk(target.getDisplayName()+" talk to me!"));
    		}
    		else if (Utils.random(50) >= 48) {
    			setNextForceTalk(new ForceTalk("Talk to me, "+target.getDisplayName()+"!"));
    		}
    	}
    }

    @Override
    public boolean withinDistance(Player tile, int distance) {
    	return tile == target && super.withinDistance(tile, distance);
    }
    
    /**
     * Follows the player.
     */
    private void sendFollow(Player player) {
    	if (!withinDistance(player, 2))
    		setNextWorldTile(player);
    	if (getLastFaceEntity() != player.getClientIndex())
    		setNextFaceEntity(player);
    	if (isFrozen())
    		return;
    	int size = getSize();
    	int targetSize = player.getSize();
    	if (Utils.colides(getX(), getY(), size, player.getX(), player.getY(), targetSize) && !player.hasWalkSteps()) {
    		resetWalkSteps();
    		if (!addWalkSteps(player.getX() + targetSize, getY())) {
    			resetWalkSteps();
    			if (!addWalkSteps(player.getX() - size, getY())) {
    				resetWalkSteps();
    				if (!addWalkSteps(getX(), player.getY() + targetSize)) {
    					resetWalkSteps();
    					if (!addWalkSteps(getX(), player.getY() - size)) {
    						return;
    					}
    				}
    			}
    		}
    		return;
    	}
    	resetWalkSteps();
    	if (!clipedProjectile(player, true) || 
    			!Utils.isOnRange(getX(), getY(), size, player.getX(), player.getY(), targetSize, 0))
    		calcFollow(player, 2, true, false);
    }
}