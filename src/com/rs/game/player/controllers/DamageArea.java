package com.rs.game.player.controllers;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.ForceMovement;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Magic;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles everything Godwars dungeon related.
 * @author Zeus
 */
public class DamageArea extends Controller {

    @Override
    public void start() {
    	Magic.sendNormalTeleportSpell(player, 0, 0.0D, new WorldTile(2142, 5532, 3), new int[0]);
    	
    }

    /**
     * Ints containing our killcount.
     */
    private int mummyDamage;

    /**
     * Closing all interfaces upon controller removal.
     */
  

   

    @Override
    public boolean login() {
    	player.setTentMulti(0);
    	return false;
    }

    @Override
    public boolean logout() {
    	player.setTentMulti(0);
    	return false;
    }

    @Override
    public void magicTeleported(int type) {
    	player.setTentMulti(0);
    }
    
    


    /**
     * Resets all kill count.
     */
 

    @Override
    public boolean sendDeath() {
    	player.setTentMulti(0);
    	return true;
    }

    

    
   
}