package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.controllers.AraxyteHyveController;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

/**
 * Handles the Webbed entrance.
 * @author Zeus
 */
public class AraxHyveD extends Dialogue {

    @Override
    public void start() {
		sendDialogue(Colors.darkRed+"Beyond this point is the Araxyte hive.", 
				Colors.darkRed+"There is no way out other than death or Victory.",
				Colors.darkRed+"Only those who can endure dangerous encounters should proceed.");
    }

    @Override
    public void run(int interfaceId, int componentId) {
    	switch (stage) {
    	case -1:
    		int players = AraxyteHyveController.getPlayers().size();
    		stage = 0;
    		sendOptionsDialogue("There "+(players == 1 ? "is" : "are")+" currently " + players + " "
    				+ (players == 1 ? "player" : "players")+" fighting", "Climb down.", "Stay here.");
    		break;
    	case 0:
    		if (componentId == OPTION_1) {
	    		player.setNextWorldTile(new WorldTile(4490, 6266, 1));
	    		player.getControlerManager().startControler("AraxyteHyveController");
 		    }
 		    end();
    		break;
    	}
    }

    @Override
    public void finish() {  }

}