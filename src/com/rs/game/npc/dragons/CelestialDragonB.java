package com.rs.game.npc.dragons;

import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

/*
 * author Movee
 */

@SuppressWarnings("serial")
public class CelestialDragonB extends NPC {

	public boolean familiarTeleported;
	public WorldTile familiarTile = null;
	public Entity playerTarget = null;
	
	public CelestialDragonB(int id, WorldTile tile) {
		super(id, tile, -1, true, true);
	}
	
	@Override
	public void processNPC() {
		super.processNPC();
	}
	
	public void handleIngoingHit(Hit hit) {
		playerTarget = hit.getSource();
	}
	
	@Override
    public double getMeleePrayerMultiplier() {
		return 0.7;
    }
	
	
	@Override
	public double getMagePrayerMultiplier() {
		return 0.7;
	}
	
	@Override
    public double getRangePrayerMultiplier() {
		return 0.7;
    }
	
	
	@Override
	public void sendDeath(Entity source) {
		if(playerTarget != null) {
			Familiar familiar = ((Player) playerTarget).getFamiliar();
			
			if(familiarTile != null && familiar != null) {
				familiar.setNextWorldTile(familiarTile);
				familiar.setCantInteract(false);
				familiar.callBlocked = false;
			}
			
			((Player) playerTarget).setFreezeDelay(0);
			
		}
		
		final NPCCombatDefinitions defs = getCombatDefinitions();

		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0)
					setNextAnimation(new Animation(defs.getDeathEmote()));
				else if (loop == 3) {
					drop();
					reset();
					getCombat().removeTarget();
					setLocation(getRespawnTile());
					finish();
					setRespawnTask();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	
	}

}
