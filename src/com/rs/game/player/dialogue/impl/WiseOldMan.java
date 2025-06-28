package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.ShopsHandler;

/**
 * Class used to handle the WiseOldMan dialogue.
 * @author Zeus
 */
public class WiseOldMan extends Dialogue {

    @Override
    public void start() {
    	sendOptionsDialogue("Choose an Option", 
    			"Trivia Point exchange",
    			"Skill Capes store", 
    			"Skill Hoods store",
    			"Mastery Capes store",
    			"Halloween Event store");
    }

    @Override
    public void run(int interfaceId, int componentId) {
    	switch (stage) {
    	case -1:
    		switch (componentId) {
    		case OPTION_1:
        		finish();
        		ShopsHandler.openShop(player, 46);
    			break;
    		case OPTION_2:
    			sendOptionsDialogue("Which skillcapes would you like?", "RS3 Capes", "Retro Capes");
    			stage = 0;
    			break;
    		case OPTION_3:
    			sendOptionsDialogue("Which skill hoods would you like?", "RS3 Hoods", "Retro hooded capes");
    			stage = 1;
    			break;
    		case OPTION_4:
        		finish();
        		ShopsHandler.openShop(player, 50);
    			break;
    		case OPTION_5:
        		finish();
        		ShopsHandler.openShop(player, 116);
    			break;	
    		}
    		break;
    	case 0:
    		switch(componentId) {
    		case OPTION_1:
    			finish();
    			ShopsHandler.openShop(player, 48);
    			break;
    		case OPTION_2:
    			finish();
    			ShopsHandler.openShop(player, 62);
    			break;
    		}
    		break;
    	case 1:
    		switch(componentId) {
    		case OPTION_1:
    			finish();
        		ShopsHandler.openShop(player, 49);
    			break;
    		case OPTION_2:
    			finish();
    			ShopsHandler.openShop(player, 63);
    			break;
    		}
    		break;
    	}
    }

    @Override
    public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }

}