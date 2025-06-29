package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.activites.ZarosGodwars;
import com.rs.game.player.dialogue.Dialogue;

public final class NexEntrance extends Dialogue {

    @Override
    public void finish() {
	// TODO Auto-generated method stub

    }

    @Override
    public void run(int interfaceId, int componentId) {
    	int players = ZarosGodwars.getPlayers().size();
	if (stage == -1) {
	    stage = 0;
	    sendOptionsDialogue(
		    "There "+(players == 1 ? "is" : "are")+" currently " + players
			    + " "+(players == 1 ? "player" : "players")+" fighting",
		    "Climb down.", "Stay here.");
	} else if (stage == 0) {
	    if (componentId == OPTION_1) {
		player.setNextWorldTile(new WorldTile(2911, 5204, 0));
		player.getControlerManager().startControler("ZGDController");
	    }
	    end();
	}

    }

    @Override
    public void start() {
	sendDialogue("The room beyond this point is a prison!",
		"There is no way out other than death or teleport.",
		"Only those who endure dangerous encounters should proceed.");
    }

}
