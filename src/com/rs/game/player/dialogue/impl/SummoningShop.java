package com.rs.game.player.dialogue.impl;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.ShopsHandler;

public class SummoningShop extends Dialogue {

    @Override
    public void finish() {

    }

    @Override
    public void run(int interfaceId, int componentId) {
	if (stage == -1) {
	    sendOptionsDialogue("Choose a shop!",
		    "Shop 1 - Starter Ingredients",
		    "Shop 2 - Intermediate Ingredients",
		    "Shop 3 - Master Ingredients");
	    stage = 1;
	} else if (stage == 1) {
	    if (componentId == OPTION_1) {
		ShopsHandler.openShop(player, 23);
		end();
	    }
	    if (componentId == OPTION_2) {
		ShopsHandler.openShop(player, 24);
		end();
	    }
	    if (componentId == OPTION_3) {
		ShopsHandler.openShop(player, 25);
		end();
	    }
	}
    }

    @Override
    public void start() {
	sendEntityDialogue(Dialogue.SEND_1_TEXT_CHAT, new String[] {
		NPCDefinitions.getNPCDefinitions(6970).name,
		"Would you like to see my shops?" }, IS_NPC, 6970, 9847);
    }

}