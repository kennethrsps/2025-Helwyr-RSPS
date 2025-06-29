package com.rs.game.player.dialogue.impl;

import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Skills;
import com.rs.game.player.content.CosmeticsHandler;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.SkillCapeCustomizer;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;


public class BankExtensionD extends Dialogue {


	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option", "Manage BankExtension",
				"Manage PetLoot Perk",Colors.red+ "Close");
			}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
		
			if (componentId == OPTION_1) {
				player.getDialogueManager().startDialogue("BanksManagerD");
				break;
    		}
    		if (componentId == OPTION_2) {
    			player.getDialogueManager().startDialogue("PetLootManagerD");
    			break;
    		}
    		if (componentId == OPTION_3) {
    			end();
    		}
    		
    
    	
		default:
			end();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
