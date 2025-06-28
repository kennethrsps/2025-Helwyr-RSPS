package com.rs.game.npc.others;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

@SuppressWarnings("serial")
public class LivingRock extends NPC {

    private Entity source;
    private long deathTime;

    public LivingRock(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea, boolean spawned) {
		super(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		setForceTargetDistance(4);
    }

    public boolean canMine(Player player) {
    	return Utils.currentTimeMillis() - deathTime > 60000 || player == source;
    }
    
    @Override
    public ArrayList<Entity> getPossibleTargets() {
		ArrayList<Entity> targets = getPossibleTargets(true, true);
		ArrayList<Entity> targetsCleaned = new ArrayList<Entity>();
		for (Entity t : targets) {
			if (t instanceof Player && hasItems((Player) t))
			    continue;
			targetsCleaned.add(t);
		}
		return targetsCleaned;
    }
    
    /**
     * Checks if the player has full skilling outfits.
     * @param player The player to check.
     * @return if has required items.
     */
    private boolean hasItems(Player player) {
    	if (player == null)
    		return false;
    	Equipment equipment = player.getEquipment();
    	if ((equipment.getHatId() == 20789 || equipment.getHatId() == 24427) &&
    			(equipment.getChestId() == 20791 || equipment.getChestId() == 24428) &&
    			(equipment.getLegsId() == 20790 || equipment.getLegsId() == 24429) &&
    			(equipment.getBootsId() == 20788 || equipment.getBootsId() == 24430))
    		return true;
    	return false;
    }

    @Override
    public void sendDeath(final Entity source) {
		final NPCCombatDefinitions defs = getCombatDefinitions();
		resetWalkSteps();
		getCombat().removeTarget();
		setNextAnimation(null);
		WorldTasksManager.schedule(new WorldTask() {
		    int loop;
	
		    @Override
		    public void run() {
				if (loop == 0)
				    setNextAnimation(new Animation(defs.getDeathEmote()));
				else if (loop >= defs.getDeathDelay()) {
				    drop();
				    reset();
				    transformIntoRemains(source);
				    stop();
				}
				loop++;
		    }
		}, 0, 1);
    }

    public void takeRemains() {
		setNPC(getId() - 5);
		setLocation(getRespawnTile());
		setRandomWalk(1);
		finish();
		if (!isSpawned())
		    setRespawnTask();
    }

    public void transformIntoRemains(Entity source) {
		this.source = source;
		deathTime = Utils.currentTimeMillis();
		final int remainsId = getId() + 5;
		transformIntoNPC(remainsId);
		setRandomWalk(0);
		CoresManager.slowExecutor.schedule(new Runnable() {
		    @Override
		    public void run() {
				try {
				    if (remainsId == getId())
					takeRemains();
				} catch (Throwable e) {
				    Logger.handle(e);
				}
		    }
		}, 3, TimeUnit.MINUTES);
    }
}