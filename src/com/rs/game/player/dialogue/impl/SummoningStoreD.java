package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.ShopsHandler;

public class SummoningStoreD extends Dialogue {
	
	/**
	 * Handles Pikkustix's dialogue.
	 * @author Zeus
	 */
	@Override
	public void start() {
		sendOptionsDialogue("Which Shop to Open", 
				"Basic Ingredients shop", 
				"Starter Ingredients shop",
				"Intermediate Ingredients shop", 
				"Novice Ingredients shop");
	}

    @Override
    public void run(int interfaceId, int componentId) {
    	switch (stage) {
    	case -1:
    		switch (componentId) {
    		case OPTION_1:
    			ShopsHandler.openShop(player, 9);
    			finish();
    			break;
    		case OPTION_2:
    			ShopsHandler.openShop(player, 10);
    			finish();
    			break;
    		case OPTION_3:
    			ShopsHandler.openShop(player, 11);
    			finish();
    			break;
    		case OPTION_4:
    			ShopsHandler.openShop(player, 12);
    			finish();
    			break;
    			default:
        			finish();
    				break;
    		}
    	}
    }

    @Override
    public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }

}