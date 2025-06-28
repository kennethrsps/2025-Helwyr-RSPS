package com.rs.game.player.dialogue.impl;

import com.rs.game.player.Skills;
import com.rs.game.player.content.items.AncientArtefacts;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;

/**
 * Handles Nastroth's Dialogue.
 * 
 * @author Zeus
 */
public class NastrothD extends Dialogue {

	/**
	 * An Int representing the NPC's ID.
	 */
	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendOptionsDialogue("Select an Option", "Sell ancient artefacts", "Reset my combat stats", "PK Point Shop",
				 "PvM Shop");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				AncientArtefacts.exchangeStatuettes(player);
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "Could you reset some of my combat skills?");
				stage = 0;
				break;
			case OPTION_3:
				sendPlayerDialogue(NORMAL, "Can I see the PK shop?");
				stage = 4;
				break;
			case OPTION_4:
				sendPlayerDialogue(NORMAL, "Can I see the PvM shop?");
				stage = 7;
				break;
			
			}
			break;
		case 0:
			if (player.getEquipment().wearingArmour()) {
				sendNPCDialogue(npcId, SAD, "I will not be able to do this with you wearing any equipment.");
				stage = 99;
				break;
			}
			sendNPCDialogue(npcId, NORMAL, "Certainly!");
			stage = 1;
			break;
		case 1:
			sendOptionsDialogue("Select a skill to reset", "Attack", "Defence", "Strength", "Constitution",
					"More options..");
			stage = 2;
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, NORMAL, "I've set your Attack level back to 1.");
				player.getSkills().set(Skills.ATTACK, 1);
				player.getSkills().setXp(Skills.ATTACK, 0);
				stage = 99;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, NORMAL, "I've set your Defence level back to 1.");
				player.getSkills().set(Skills.DEFENCE, 1);
				player.getSkills().setXp(Skills.DEFENCE, 0);
				stage = 99;
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, NORMAL, "I've set your Strength level back to 1.");
				player.getSkills().set(Skills.STRENGTH, 1);
				player.getSkills().setXp(Skills.STRENGTH, 0);
				stage = 99;
				break;
			case OPTION_4:
				sendNPCDialogue(npcId, NORMAL, "I've set your Constitution level back to 10.");
				player.getSkills().set(Skills.HITPOINTS, 10);
				player.getSkills().setXp(Skills.HITPOINTS, 1154);
				stage = 99;
				break;
			case OPTION_5:
				sendOptionsDialogue("Select a skill to reset", "Ranged", "Prayer", "Magic", "Summoning",
						"More options..");
				stage = 3;
				break;
			}
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, NORMAL, "I've set your Ranged level back to 1.");
				player.getSkills().set(Skills.RANGE, 1);
				player.getSkills().setXp(Skills.RANGE, 0);
				stage = 99;
				break;
			case OPTION_2:
				sendNPCDialogue(npcId, NORMAL, "I've set your Prayer level back to 1.");
				player.getSkills().set(Skills.PRAYER, 1);
				player.getSkills().setXp(Skills.PRAYER, 0);
				stage = 99;
				break;
			case OPTION_3:
				sendNPCDialogue(npcId, NORMAL, "I've set your Magic level back to 1.");
				player.getSkills().set(Skills.MAGIC, 1);
				player.getSkills().setXp(Skills.MAGIC, 0);
				stage = 99;
				break;
			case OPTION_4:
				sendNPCDialogue(npcId, NORMAL, "I've set your Summoning level back to 1.");
				player.getSkills().set(Skills.SUMMONING, 1);
				player.getSkills().setXp(Skills.SUMMONING, 0);
				stage = 99;
				break;
			case OPTION_5:
				sendOptionsDialogue("Select a skill to reset", "Attack", "Defence", "Strength", "Constitution",
						"More options..");
				stage = 2;
				break;
			}
			break;
		case 4:
			ShopsHandler.openShop(player, 60);
			end();
			break;

		
			
		case 7:
			sendOptionsDialogue("Select a skill to reset", "PvM Shop Tier 1", "PvM Shop Tier 2", "PvM Shop Tier 3");
			stage = 8;
			break;
		case 8:
			switch (componentId) {
			case OPTION_1:
				ShopsHandler.openShop(player, 108);
				end();
				break;
			case OPTION_2:
				ShopsHandler.openShop(player, 119);
				end();
				break;
			case OPTION_3:
				ShopsHandler.openShop(player, 109);
				end();
				break;
			}
			break;
		case 99:
			finish();
			break;
		}
	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}
}