package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class Skincolor extends Dialogue {

    @Override
    public void start() {
    	sendOptionsDialogue(Colors.cyan+"What skin Color would you like?", "Blue", "Green", "Black");
	}
	
    @Override
    public void run(int interfaceId, int componentId) {
    	switch (componentId) {
    	case OPTION_1:
    		player.getGlobalPlayerUpdater().setSkinColor(12);
	    	player.getGlobalPlayerUpdater().generateAppearenceData();
    	    end();
    	    break;
    	case OPTION_2:
    		player.getGlobalPlayerUpdater().setSkinColor(13);
			player.getGlobalPlayerUpdater().generateAppearenceData();
    	    end();
    	    break;
    	case OPTION_3:
    		player.getGlobalPlayerUpdater().setSkinColor(11);
			player.getGlobalPlayerUpdater().generateAppearenceData();
			end();
			break;
    	}
    }

    @Override
    public void finish() { }
}