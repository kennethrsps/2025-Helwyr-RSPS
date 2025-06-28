package com.rs.game.npc.xmas;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

@SuppressWarnings("serial")
public class SmallSnowman extends NPC {
	
	public Player recip;
	int x; int y;
	
	public SmallSnowman(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		super.setNextFaceWorldTile(new WorldTile(Utils.random(0, 2)+tile.getX()-Utils.random(0, 2), Utils.random(0, 2)+tile.getY()-Utils.random(0, 2), tile.getPlane()));
	}
	
	@Override
	public void handleIngoingHit(Hit hit) {
		recip = (Player) hit.getSource();
		faceEntity(recip);
		recip.getXmas().inThrow = false;
		super.handleIngoingHit(hit);
	}
	
    @Override
    public void sendDeath(Entity source) {
    	Player killer = getMostDamageReceivedSourcePlayer();
    	if(killer == null) {
    		finish();
    		setRespawnTask();
    		super.sendDeath(source);
    		return;
    	}
    	killer.getXmas().snowEnergy += 15;
    	killer.getXmas().inThrow = false;
    	super.setNextAnimation(null);
    	WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				if(killer.getInventory().hasFreeSlots())
					killer.getInventory().addItem(new Item(33590, 10));
				killer.sendMessage("Total snow energy: "+Colors.rcyan+Colors.shad+killer.getXmas().snowEnergy+"</shad></col> and total snowman kills: "+Colors.shad+Colors.dcyan+killer.getXmas().snowmenKilled+"!",true);
				killer.getXmas().snowmenKilled += 1;
				finish();
				setRespawnTask();
			}
    		
    	}, 1);
		super.sendDeath(source);
    }
    
    @Override
    public void setRespawnTask() {
    	final NPC npc = this;
    	if(!hasFinished()) {
    		reset();
    		setLocation(getRespawnTile());
    		finish();
    	}
    	WorldTasksManager.schedule(new WorldTask() {
    		
    		@Override
    		public void run() {
    			setFinished(false);
    			World.addNPC(npc);
    			npc.setLastRegionId(0);
    			npc.reset();
    			npc.setNextFaceWorldTile(new WorldTile(Utils.random(0, 2)+npc.getX()-Utils.random(0, 2), Utils.random(0, 2)+npc.getY()-Utils.random(0, 2), npc.getPlane()));
				World.updateEntityRegion(npc);
				loadMapRegions();
    		}
    	}, 4);
    }
    
	@Override
	public void processNPC() {
		super.processNPC();
		if(isDead())
			return;
	}
}
