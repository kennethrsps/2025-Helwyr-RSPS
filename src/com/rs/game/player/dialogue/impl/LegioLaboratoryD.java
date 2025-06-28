package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Skills;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.game.item.Item;

/**
 * Handles the Legio boss doors.
 * @author Zeus
 */
public class LegioLaboratoryD extends Dialogue {
	
	/**
	 * Represents the Laboratory door World Object.
	 */
	private WorldObject door;

    @Override
    public void start() {
    	door = (WorldObject) parameters[0];
    	if (player.getSkills().getLevel(Skills.SLAYER) < 95) {
    		sendDialogue("Legio Bosses require at least a Slayer level of 95 to fight.");
    		stage = 99;
    		return;
    	}
    	sendDialogue("The Legiones are significantly more dangerous than the other Ascended.",
    			"Make sure you are prepared before entering the labs.");
    }

    @Override
    public void run(int interfaceId, int componentId) {
    	switch (stage) {
    	case -1:
    		if (!hasKeystone(false)) {
    			sendDialogue("You do not have the requirred keystone in order to enter the boss room.");
    			stage = 99;
    			return;
    		}
    		sendOptionsDialogue("Are you sure you want to enter? This will use up your keystone!", 
    				"Yes, I am ready.", 
    				"No, I need to prepare.");
    		stage = 0;
    		break;
    	case 0:
    		switch (componentId) {
    		case OPTION_1:
				end();
    			if (hasKeystone(true))
    				return;
    			player.sendMessage("ERROR;");
    			break;
    		case OPTION_2:
    			end();
    			break;
    		}
    		break;
    		
    	case 99:
    		end();
    		break;
    	}
    }

    @Override
    public void finish() {  }
    
    /**
     * Checks if the player has the Keystone to enter boss room.
     * @return if the Player has the requirred Keystone.
     */
    private boolean hasKeystone(boolean remove) {
    	if (door.getId() == 84726) { //Primus
    		if (remove) {
    			player.setNextWorldTile(new WorldTile(1023, 632, 1));
    			return player.getInventory().deleteOneItem(new Item(28445));
    		}
    		player.sendMessage(Colors.red+"After killing Primus you will be teleported out so you need to pick the loot up fast!");
    		return player.getInventory().containsOneItem(28445);
    	}
    	if (door.getId() == 84727) { //Secundus
    		if (remove) {
    			player.setNextWorldTile(new WorldTile(1106, 673, 1));
    			return player.getInventory().deleteOneItem(new Item(28447));
    		}
    		player.sendMessage(Colors.red+"After killing Secundus you will be teleported out so you need to pick the loot up fast!");
    		return player.getInventory().containsOneItem(28447);
    	}
    	if (door.getId() == 84728) { //Tertius
    		if (remove) {
    			player.setNextWorldTile(new WorldTile(1099, 663, 1));
    			return player.getInventory().deleteOneItem(new Item(28449));
    		}
    		player.sendMessage(Colors.red+"After killing Tertius you will be teleported out so you need to pick the loot up fast!");
    		return player.getInventory().containsOneItem(28449);
    		
    	}
    	if (door.getId() == 84729) { //Quartus
    		if (remove) {
    			player.setNextWorldTile(new WorldTile(1174, 634, 1));
    			return player.getInventory().deleteOneItem(new Item(28451));
    		}
    		player.sendMessage(Colors.red+"After killing Quartus you will be teleported out so you need to pick the loot up fast!");
    		return player.getInventory().containsOneItem(28451);
    	}
    	if (door.getId() == 84730) { //Quintus
    		if (remove) {
    			player.setNextWorldTile(new WorldTile(1193, 634, 1));
    			return player.getInventory().deleteOneItem(new Item(28453));
    		}
    		player.sendMessage(Colors.red+"After killing Quintus you will be teleported out so you need to pick the loot up fast!");
    		return player.getInventory().containsOneItem(28453);
    	}
    	if (door.getId() == 84731) { //Sextus
    		if (remove) {
    			player.setNextWorldTile(new WorldTile(1184, 620, 1));
    			return player.getInventory().deleteOneItem(new Item(28455));
    		}
    		player.sendMessage(Colors.red+"After killing Sextus you will be teleported out so you need to pick the loot up fast!");
    		return player.getInventory().containsOneItem(28455);
    	}
    	return false;
    }
}