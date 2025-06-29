package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.ShopsHandler;


/**
 * Handles Scavvo's dialogue.
 * @author Zeus
 */
public class ScavvoD extends Dialogue {

	/**
	 * The NPC ID.
	 */
	private int npcId;
	
	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, GOOFY_LAUGH, "'Ello matey! D'ya wanna buy some exiting new toys?");
	}

    @Override
    public void run(int interfaceId, int componentId) {
    	switch (stage) {
    	case -1:
    		sendPlayerDialogue(NORMAL, "Let's have a look then.");
    		stage = 0;
    		break;
    	case 0:
    		ShopsHandler.openShop(player, 4);
    		finish();
    		break;
    	}
    }

    @Override
    public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }

}
