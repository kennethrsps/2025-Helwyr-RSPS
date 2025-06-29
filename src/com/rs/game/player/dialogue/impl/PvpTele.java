package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;


public class PvpTele extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option", "East Dragons",
				"Forinthry Dungeon", "Agility Course (50 Wildy)", "Mage Bank",Colors.red+ "More Options");
			}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
		
			if (componentId == OPTION_1) {
				Magic.vineTeleport(player, new WorldTile(3359, 3671, 0));
    			end();
    		}
    		if (componentId == OPTION_2) {
    			Magic.vineTeleport(player, new WorldTile(3071, 3649, 0));
    			end();
    			
    		}
    		if (componentId == OPTION_3) {
    			Magic.vineTeleport(player, new WorldTile(2998, 3912, 0));
    			end();
    		}
    		if (componentId == OPTION_4) {
    			Magic.vineTeleport(player, new WorldTile(2539, 4715, 0));
    			end();
    		}
    		if (componentId == OPTION_5) {
    			sendOptionsDialogue("Choose an Option", "New Gates (47 Wildy)",Colors.red+ "Back");
    			stage = 2;
    			
    				}
    		break;
    	case 2:
    		if (componentId == OPTION_1) {
    			Magic.vineTeleport(player, new WorldTile(3337, 3889, 0));
    			end();
    		}
    		if (componentId == OPTION_2) {
    			sendOptionsDialogue("Choose an Option", "East Dragons",
    					"Forinthry Dungeon", "Agility Course (50 Wildy)", "Mage Bank",Colors.red+ "More Options");
    			stage = -1;
    			
    				}
    		break;
    	default:
			end();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
