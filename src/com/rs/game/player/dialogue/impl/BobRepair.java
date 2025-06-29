package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.RepairItems;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.item.Item;

public class BobRepair extends Dialogue {

    private int npcId;
    private Item item;

    @Override
    public void start() {
    	npcId = (Integer) parameters[0];
    	item = (Item) parameters[1];
    	sendNPCDialogue(npcId, NORMAL, "Would you like me to repair this item for you?");
    }

    @Override
    public void run(int interfaceId, int componentId) {
    	switch (stage) {
    	case -1:
    		sendOptionsDialogue("Repair <col=ff0000>"+item.getName()+"</col> for <col=ff0000>"+
    				RepairItems.getPrice(item.getId())+"</col>?", "Yes, please.", "No, thank you.");
    		stage = 0;
    		break;
    	case 0:
    		switch (componentId) {
    		case OPTION_1:
    			sendPlayerDialogue(NORMAL, "Yes, please, repair it for me.");
    			stage = 1;
    			break;	
    		case OPTION_2:
    			sendPlayerDialogue(CALM, "No, thank you.");
    			stage = 4;
    			break;
    		}
    		break;
    	case 1:
    		if (RepairItems.repair(player, item.getId())) {
    			sendNPCDialogue(npcId, NORMAL, "There you go, sir.");
    			stage = 2;
    		} else {
    			sendNPCDialogue(npcId, SAD, "It seems that you don't have enough gold. Come back later when you do.");
    			stage = 3;
    		}
    		break;
    	case 2:
			sendPlayerDialogue(NORMAL, "Thank you.");
    		stage = 99;
    		break;
    	case 3:
			sendPlayerDialogue(SAD, "Awh.. I'm sorry. I'll make sure to bring some gold next time.");
    		stage = 99;
    		break;
    	case 4:
			sendNPCDialogue(npcId, CALM, "Sure. I'll just be right here if you ever need me.");
			stage = 99;
    		break;
    	case 99:
    		finish();
    		break;
    	}
    }

    @Override
    public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }
}