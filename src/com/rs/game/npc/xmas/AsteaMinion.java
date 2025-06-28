package com.rs.game.npc.xmas;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

@SuppressWarnings("serial")
public class AsteaMinion extends NPC {
	
	public Player recip;
	
	public AsteaMinion(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
	}
	
	@Override
	public void handleIngoingHit(Hit hit) {
		recip = (Player) hit.getSource();
		faceEntity(recip);
		if(Utils.currentTimeMillis() - recip.getXmas().thrownTick < 3)
			return;
		super.handleIngoingHit(hit);
	}
	
    @Override
    public void sendDeath(Entity source) {
    	Player killer = getMostDamageReceivedSourcePlayer();
    	super.setNextAnimation(null);
    	WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				if(killer.getInventory().hasFreeSlots())
					killer.getInventory().addItem(new Item(33590, 25));
				killer.getXmas().snowmenKilled += 1;
				finish();
			}
    		
    	}, 1);
		super.sendDeath(source);
    }
  
	@Override
	public void processNPC() {
		super.processNPC();
		if(isDead())
			return;
	}
}
