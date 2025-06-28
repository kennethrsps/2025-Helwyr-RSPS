package com.rs.game.npc.others.randoms;

import com.rs.game.Animation;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles the Divination Chronicle fragment NPC.
 * @author Zeus
 */
public class ChronicleFragmentNPC extends NPC {

	private static final long serialVersionUID = -2385681443539672863L;
	
	private Player target;
    private long createTime;

    public ChronicleFragmentNPC(WorldTile tile, Player target) {
    	super(18204, tile, -1, true, true);
    	this.target = target;
    	this.createTime = Utils.currentTimeMillis();
    }

    /**
     * Gives the Player his reward.
     * @param player The player.
     */
    public void giveReward(final Player player) {
    	if (player.isLocked())
    		return;
    	if (player != target) {
    		player.sendMessage(Colors.red+"<shad=000000>This isn't your Chronicle to capture.");
    		return;
    	}
		player.lock(2);
    	player.getInventory().addItem(new Item(29293));
    	if (player.getMemoriesCollected() >= 1000) {
    		if (!player.hasItem(new Item(29869)))
    			player.addItem(new Item(29869));
    	}
    	if (player.getMemoriesCollected() >= 2000) {
    		if (!player.hasItem(new Item(29868)))
    			player.addItem(new Item(29868));
    	}
    	if (player.getMemoriesCollected() >= 3000) {
    		if (!player.hasItem(new Item(29865)) && !player.hasItem(new Item(32279)))
    			player.addItem(new Item(29865));
    	}
    	if (player.getMemoriesCollected() >= 4000) {
    		if (!player.hasItem(new Item(29867)))
    			player.addItem(new Item(29867));
    	}
    	if (player.getMemoriesCollected() >= 5000) {
    		if (!player.hasItem(new Item(29866)))
    			player.addItem(new Item(29866));
    	}
    	if (player.getMemoriesCollected() >= 25000) {
    		if (!player.hasItem(new Item(32275)) && !player.hasItem(new Item(32279)))
    			player.addItem(new Item(32275));
    	}
    	player.setNextAnimation(new Animation(21229));
    	player.getSkills().addXp(Skills.HUNTER, player.getSkills().getLevelForXp(Skills.HUNTER) * 2);
    	finish();
    }

    @Override
    public void processNPC() {
    	if (target.hasFinished() || createTime + 60000 < Utils.currentTimeMillis())
    		finish();
    }

    @Override
    public boolean withinDistance(Player tile, int distance) {
    	return tile == target && super.withinDistance(tile, distance);
    }
}