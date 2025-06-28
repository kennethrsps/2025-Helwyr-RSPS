package com.rs.game.npc.xmas;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.Drop;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

@SuppressWarnings("serial")
public class AsteaFrostweb extends NPC {
	
	public WorldObject portal;
	
	public AsteaFrostweb(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
		super.setNextForceTalk(new ForceTalk("You dare challenge me? Perish in snow mortal!"));
		super.processMovement();
	}
	
	@Override
	public void processMovement() {
		
	}
	
	@Override
	public void handleIngoingHit(Hit hit) {
		faceEntity(hit.getSource());
		if(hit.getSource() instanceof Player)
			((Player) hit.getSource()).getXmas().inThrow = false;
		super.handleIngoingHit(hit);
	}
	
    @Override
    public void sendDeath(Entity source) {
    	super.setNextAnimation(null);
    	WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				/** Spawn exit portal immediately **/
				portal = new WorldObject(11899, 10, 0, 2589, 5602, 1);
    			World.spawnObject(portal);
	    		/** Define killer by player that dealt the most damage */
		    	Player killer = getMostDamageReceivedSourcePlayer();
		    	if(killer == null || source == null) {
		    		finish();
		    		setRespawnTask();
		    		sendDeath(source);
		    		return;
		    	}
		    		/** Get targets nearby, award non-killers with some things  */
		    	for(Entity target : getReceivedDamageSources()) {
		    		Player player = (Player) target;
		    		if(!player.getXmas().freedSanta) {
		    			player.getXmas().freedSanta = true;
		    			player.sendMessage(Colors.rcyan+Colors.shad+"You have defeated the evil wizard who imprisoned Santa and saved Christmas!", false);
		    		}
		    		if(player != killer && player != null && Utils.currentTimeMillis() - player.getXmas().bossTick >= 100) {
		    			if(player.getInventory().hasFreeSlots())
		    				player.getInventory().addItem(33590, 50);
		    			player.getXmas().snowEnergy += 120;
		    			player.sendMessage("You received 120 "+Colors.rcyan+Colors.shad+"snow energy</col></shad> for your help with Astea!", true);
		    		}	
		    	}
		    	
		    	if(killer.getInventory().hasFreeSlots())
		    		killer.getInventory().addItem(33590, 120);
		    	
		    	/*if(Utils.random(1, 150) == 0) {
		    		sendDrop(killer, new Drop(33611, 1, 1, 1, false));
		    		killer.getXmas().announceDrop(" has received a big Christmas present from Astea Frostweb!");
		    	}*/
		    	if(Utils.random(1, 1000) == 1) {
		    		sendDrop(killer, new Drop(1050, 1, 1, 1));
		    		killer.getXmas().announceDrop(" has received a santa hat from Astea Frostweb!");
		    	}
		    	killer.getXmas().snowEnergy += 250;
		    	killer.setNextAnimation(new Animation(17072));
		    	killer.setNextForceTalk(new ForceTalk(Colors.rcyan+"I have defeated Astea Frostweb!"));
		    	killer.sendMessage("You have defeated Astea Frostweb and received "+ Colors.dcyan+Colors.shad+"250 snow energy</col></shad>!", true);
				killer.getXmas().bossKilled += 1;
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
    			World.removeObject(portal);
    			World.addNPC(npc);
    			npc.setLastRegionId(0);
    			npc.reset();
    			npc.setNextFaceWorldTile(new WorldTile(npc.getX(), npc.getY()-1, npc.getPlane()));
				World.updateEntityRegion(npc);
				loadMapRegions();
				npc.setNextForceTalk(new ForceTalk("You dare challenge me? Perish in snow mortal!"));

    		}
    	}, 300);
    }
}