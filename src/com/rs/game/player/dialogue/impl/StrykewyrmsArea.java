package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.Skills;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class StrykewyrmsArea extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Strykewyrms Area", "Jungle Strykewyrms", "Desert Strykewyrms", "Ice Strykewyrms");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:

			if (componentId == OPTION_1) {
				if (player.getSkills().getLevelForXp(Skills.SLAYER) < 73) {
					player.sendMessage("You need at least a level of 73 Slayer to go there!");
					end();
					return;
				}
				Magic.vineTeleport(player, new WorldTile(2452, 2911, 0));
			}
			if (componentId == OPTION_2) {
				if (player.getSkills().getLevelForXp(Skills.SLAYER) < 77) {
					player.sendMessage("You need at least a level of 77 Slayer to go there!");
					return;
				}
				Magic.vineTeleport(player, new WorldTile(3356, 3160, 0));
			}
			if (componentId == OPTION_3) {
				if (player.getSkills().getLevelForXp(Skills.SLAYER) < 93) {
					player.sendMessage("You need at least a level of 93 Slayer to go there!");
					return;
				}
				Magic.vineTeleport(player, new WorldTile(3435, 5648, 0));
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
