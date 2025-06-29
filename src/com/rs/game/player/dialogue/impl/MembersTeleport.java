package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;

/**
 * Class used to handle the WiseOldMan dialogue.
 * @author Zeus
 */
public class MembersTeleport extends Dialogue {

    @Override
    public void start() {
    	sendOptionsDialogue("Choose an Option", 
    			"Member's Area",
    			"Gold's Area",
    			"Platinum Area","Close");
    }

    @Override
    public void run(int interfaceId, int componentId) {
    	switch (stage) {
    	case -1:
    		switch (componentId) {
    		case OPTION_1:
    			if (!player.isBronze()) {
    				player.getDialogueManager().startDialogue("SimpleMessage", 
    						"If you'd like to visit the Donator Zone, you'll have to ;;donate at least 20$ first!");
    				return;
    			}
    			Magic.sendAncientTeleportSpell(player, 1, 0, new WorldTile(4382, 5919, 0));
    			break;
    		case OPTION_2:
    			if (player.getMoneySpent() >= 100)  {
    				Magic.sendAncientTeleportSpell(player, 1, 0, new WorldTile(2140, 6944, 0));
    				player.sendMessage(Colors.red+"Welcome to Gold Members Area!");
    				end();
    				return;
    			}
    			player.getDialogueManager().startDialogue("SimpleMessage", 
						"If you'd like to visit the Gold Members Area, you'll have to ;;donate at least 100$ first!");
    			stage = 0;
    			break;
    		case OPTION_3:
    			if (player.getMoneySpent() >= 250)  {
    				Magic.sendAncientTeleportSpell(player, 1, 0, new WorldTile(2012, 11811, 0));
    				player.sendMessage(Colors.red+"Welcome to Platinum Members Area!");
    				end();
    				return;
    			}
    			player.getDialogueManager().startDialogue("SimpleMessage", 
						"If you'd like to visit the Platinum Members Area, you'll have to ;;donate at least 250$ first!");
    			stage = 0;
    			break;
    		case OPTION_4:
    			end();
    			break;
    		
    		}
    		break;
    	}
    }

    @Override
    public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }

}