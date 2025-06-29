package com.rs.game.player.dialogue.impl;

import com.rs.game.player.Skills;
import com.rs.game.player.content.InstancedPVP;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.ShopsHandler;

public class InstancedPVPD extends Dialogue {

	private int npcId;

	@Override
	public void start() {
		npcId = (int) parameters[0];
		this.sendNPCDialogue(npcId, NORMAL, "Welcome to Instanced PVP, How can I help you?");
	}

	private byte endStage = 8;

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			stage = 0;
			this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "What is Instanced PVP?", "Do you have a shop?",
					"I would like to change my combat levels.", "I would like to leave.", "Open GearSet.");
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				stage = 1;
				this.sendPlayerDialogue(NORMAL, "What is Instanced PVP?");
				break;
			case OPTION_2:
				stage = 2;
				this.sendPlayerDialogue(NORMAL, "Do you have a shop?");
				break;
			case OPTION_3:
				stage = 4;
				this.sendPlayerDialogue(NORMAL, "I would like to change my combat levels.");
				break;
			case OPTION_4:
				stage = 9;
				this.sendPlayerDialogue(NORMAL, "I would like to leave.");
				break;
			case OPTION_5:
				stage = 7;
				player.getDialogueManager().startDialogue("PvpSpawn");
				break;
			}
			break;
		case 1:
			stage = -1;
			this.sendNPCDialogue(npcId, NORMAL,
					"Instanced PVP is an area, duplicate of wilderness, "
							+ "where you can fight players for points and rewards."
							+ " In here you can change your combat levels and spawn "
							+ "any item so you can fight the way you want without taking any risk and for rewards.");
			break;
		case 2:
			stage = 3;
			this.sendNPCDialogue(npcId, NORMAL, "Yes, I do.");
			break;
		case 3:
			end();
			if (InstancedPVP.REWARD_SHOP == -1) {
				player.getPackets().sendGameMessage("Rewards has not been decided yet, Check again later.");
				break;
			}
			ShopsHandler.openShop(player, InstancedPVP.REWARD_SHOP);
			break;
		case 4:
			stage = 5;
			this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Attack", "Strength", "Defence", "Constitution", "More");
			break;
		case 5:
			if (componentId == OPTION_5) {
				stage = 6;
				this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Range", "Magic", "Prayer", "Summoning", "Back");
				return;
			}
			int skillId = componentId == OPTION_1 ? Skills.ATTACK
					: componentId == OPTION_2 ? Skills.STRENGTH
							: componentId == OPTION_3 ? Skills.DEFENCE : Skills.HITPOINTS;
			end();
			player.getTemporaryAttributtes().put("ChangeSkillIPVP", skillId);
			player.getPackets().sendInputIntegerScript(true, "Set level for " + Skills.SKILL_NAME[skillId] + ":");
			break;
		case 6:
			if (componentId == OPTION_5) {
				stage = 5;
				this.sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Attack", "Strength", "Defence", "Constitution",
						"More");
				return;
			}
			skillId = componentId == OPTION_1 ? Skills.RANGE
					: componentId == OPTION_2 ? Skills.MAGIC
							: componentId == OPTION_3 ? Skills.PRAYER : Skills.SUMMONING;
			end();
			player.getTemporaryAttributtes().put("ChangeSkillIPVP", skillId);
			player.getPackets().sendInputIntegerScript(true, "Set level for " + Skills.SKILL_NAME[skillId] + ":");
			break;
		case 7:
			player.getDialogueManager().startDialogue("PvpSpawn");
			break;
		case 8:
			end();
			break;
		case 9:
			stage = 10;
			this.sendNPCDialogue(npcId, NORMAL, "As you wish.");
			break;
		case 10:
			end();
			player.getControlerManager().forceStop();
			break;
		}

	}

	@Override
	public void finish() {
	}

}
