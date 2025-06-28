package com.rs.game.npc.others.randoms;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

/**
 * Handles the Fire Spirit NPC.
 * @author Zeus
 */
public class FireSpirit extends NPC {

	private static final long serialVersionUID = 2386559197780258816L;
	
	private Player target;
    private long createTime;

    public FireSpirit(WorldTile tile, Player target) {
    	super(15451, tile, -1, true, true);
    	this.target = target;
    	createTime = Utils.currentTimeMillis();
    }

    public void giveReward(final Player player) {
    	if (player != target || player.isLocked())
    		return;
    	player.stopAll(true, false, true);
    	player.lock(3);
    	Item rewards[] = {new Item(439, Utils.random(1, 2)), new Item(437, Utils.random(1, 2)),
    			new Item(441, Utils.random(1, 2)), new Item(454, 4), new Item(443, 1),
    			new Item(445, Utils.random(3, 5)), new Item(448, Utils.random(2, 4)),
    			new Item(450, 1), new Item(452, 1), new Item(556, Utils.random(5, 200)),
    			new Item(554, Utils.random(2, 100)), new Item(562, Utils.random(4, 20)),
    			new Item(559, Utils.random(20, 80)), new Item(562, Utils.random(2, 20)),
    			new Item(563, Utils.random(6, 10)), new Item(560, Utils.random(2, 6)),
    			new Item(558, Utils.random(20, 40)), new Item(1617, 1), new Item(1619, 1),
    			new Item(1621, 1), new Item(1623, 1), new Item(1625, 1), new Item(1627, 1),
    			new Item(1629, 1),new Item(1631, 1), new Item(995, Utils.random(50, 2000)),
    			new Item(12158, Utils.random(1, 3)), new Item(12159, Utils.random(1, 7)),
    			new Item(12160, Utils.random(1, 4)), new Item(12163, Utils.random(1, 2)),
    			new Item(593, Utils.random(1, 6)), new Item(985, 1), new Item(987, 1)};
    	final Item reward = rewards[(Utils.random(rewards.length))];
    	final Item reward2 = rewards[(Utils.random(rewards.length))];
    	final Item reward3 = rewards[(Utils.random(rewards.length))];
    	final Item reward4 = rewards[(Utils.random(rewards.length))];
    	player.setNextAnimation(new Animation(16705));
    	WorldTasksManager.schedule(new WorldTask() {

    		@Override
    		public void run() {
    			player.unlock();
    			if (reward.getId() == 995)
    				player.addMoney(reward.getAmount());
    			else
    				addItem(player, reward);
    			if (reward2.getId() == 995)
    				player.addMoney(reward2.getAmount());
    			else
    				addItem(player, reward2);
    			if (Utils.random(5) >= 3) {
    				if (reward3.getId() == 995)
        				player.addMoney(reward3.getAmount());
        			else
        				addItem(player, reward3);
    			}
    			if (Utils.random(10) <= 2) {
    				if (reward4.getId() == 995)
        				player.addMoney(reward4.getAmount());
        			else
        				addItem(player, reward4);
    			}
    			if (player.getLogsBurned() >= 1000) {
    				if (!player.hasItem(new Item(13659)))
    					player.addItem(new Item(13659));
    			}
    			if (player.getLogsBurned() >= 2000) {
    				if (!player.hasItem(new Item(13660)))
    					player.addItem(new Item(13660));
    			}
    			if (player.getLogsBurned() >= 3000) {
    				if (!player.hasItem(new Item(13661)))
    					player.addItem(new Item(13661));
    			}
    			player.sendMessage("<col=ff0000>The fire spirit gives you a reward before disappearing.");
    			player.unlock();
    			finish();
    			stop();
    		}
    	}, 2);
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
    
    /**
	 * Gives the Player an item.
	 * @param item The item to give.
	 */
	public void addItem(Player player, Item item) {
		if (!player.getInventory().addItem(item)) {
			World.updateGroundItem(item, player, player, 60, 0);
		} else
			player.getInventory().addItem(item);
	}
}