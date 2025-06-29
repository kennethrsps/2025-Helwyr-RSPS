package com.rs.game.player.dialogue.impl;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.content.BobBarter;
import com.rs.game.player.dialogue.Dialogue;

/**
 * Decants all potions that are currently in players inventory.
 * @author Zeus.
 */
public class BobBarterD extends Dialogue {
	
    int npcId;

    @Override
    public void start() {
		npcId = (int) parameters[0];
		sendEntityDialogue(SEND_1_TEXT_CHAT,
			new String[] { NPCDefinitions.getNPCDefinitions(npcId).name,
				"Good day, How can I help you?" }, IS_NPC, npcId, 9827);
		stage = 0;
    }

    @Override
    public void run(int interfaceId, int componentId) {
    	switch(stage) {
    	case 0:
		    if (componentId == OPTION_1) {
	    		BobBarter decanter = new BobBarter(player);
				decanter.decant();
				sendNPCDialogue(npcId, CALM, "There ya go chum..");
				stage = 1;
			} else
				end();
		    break;
    	case 1:
    		BobBarter decanter = new BobBarter(player);
    		switch(componentId) {
				case OPTION_1:
				case OPTION_2:
					decanter.decant();
					sendNPCDialogue(npcId, CALM, "There ya go chum..");
					stage = 1;
					break;
				case OPTION_3:
					end();
					break;
			}
		}
    }

    @Override
    public void finish() {

    }
}