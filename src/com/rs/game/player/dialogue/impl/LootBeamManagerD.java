package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;

public class LootBeamManagerD extends Dialogue {

	@Override
	public void start() {
		sendNPCDialogue(6537, 9827, "Hello there "+player.getDisplayName()+", would you like to set your loot beam?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
		sendOptionsDialogue("Change Loot Beam Prices?", "Yes please!", "No thank you");
		stage = 0;
		} else if (stage == 0) {
			if (componentId == OPTION_1) {
				sendPlayerDialogue(9827, "Yes please!");
				stage = 1;
			}
			if (componentId == OPTION_2) {
				end();
			}
		} else if (stage == 1) {
			sendOptionsDialogue("Pick A Loot Beam Price", "100K", "500K", "1 Milion", "10 Million", "25 Million");
			stage = 2;
		} else if (stage == 2) {
			if (componentId == OPTION_1) {
				sendNPCDialogue(6537, 9827, "Your loot beam is now set to; 100K.");
				player.setLootBeam = 100000;
				player.toggleLootBeam();
				player.getAchManager().addKeyAmount("beam", 1);
				stage = 3;
			}
			if (componentId == OPTION_2) {
				sendNPCDialogue(6537, 9827, "Your loot beam is now set to; 500K.");
				player.setLootBeam = 500000;
				player.toggleLootBeam();
				player.getAchManager().addKeyAmount("beam", 1);
				stage = 3;
			}
			if (componentId == OPTION_3) {
				sendNPCDialogue(6537, 9827, "Your loot beam is now set to; 1 Million.");
				player.setLootBeam = 1000000;
				player.toggleLootBeam();
				player.getAchManager().addKeyAmount("beam", 1);
				stage = 3;
			}
			if (componentId == OPTION_4) {
				sendNPCDialogue(6537, 9827, "Your loot beam is now set to; 10 Million.");
				player.setLootBeam = 10000000;
				player.toggleLootBeam();
				player.getAchManager().addKeyAmount("beam", 1);
				stage = 3;
			}
			if (componentId == OPTION_5) {
				sendNPCDialogue(6537, 9827, "Your loot beam is now set to; 25 Million.");
				player.setLootBeam = 25000000;
				player.toggleLootBeam();
				player.getAchManager().addKeyAmount("beam", 1);
				stage = 3;
			}
		} else if (stage == 3) {
			end();
		}
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		
	}
	
}