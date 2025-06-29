package com.rs.game.player.dialogue.impl.dungeoneering;

import com.rs.game.player.actions.fletching.Fletching;
import com.rs.game.player.content.dungeoneering.rooms.PuzzleRoom;
import com.rs.game.player.content.dungeoneering.rooms.puzzles.FremennikCampRoom;
import com.rs.game.player.content.dungeoneering.skills.DungeoneeringSmithing;
import com.rs.game.player.dialogue.Dialogue;

public class FremennikScoutD extends Dialogue {

	@Override
	public void start() {
		PuzzleRoom room = (PuzzleRoom) parameters[0];
		if (room.isComplete()) {
			sendNPCDialogue(FremennikCampRoom.FREMENNIK_SCOUT, NORMAL,
					"Wonderful! That was the last of them. As promised, I'll unlock the door for you.");
			stage = 100;
		} else {
			sendNPCDialogue(FremennikCampRoom.FREMENNIK_SCOUT, NORMAL, "Need some tools?");
			stage = 1;
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == 1) {
			if (!player.getInventory().containsItemToolBelt(DungeoneeringSmithing.DUNG_HAMMER)) {
				player.getInventory().addItem(DungeoneeringSmithing.DUNG_HAMMER, 1);
			}
			if (!player.getInventory().containsItemToolBelt(Fletching.DUNGEONEERING_KNIFE)) {
				player.getInventory().addItem(Fletching.DUNGEONEERING_KNIFE, 1);
			}
			stage = 100;
		}
		if (stage == 100) {
			end();
		}
	}

	@Override
	public void finish() {

	}

}
