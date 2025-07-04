package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.construction.House.RoomReference;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Utils;

public class RemoveRoomD extends Dialogue {

	private RoomReference room;

	@Override
	public void start() {
		this.room = (RoomReference) parameters[0];
		sendOptionsDialogue("Remove the "
						+ Utils.formatString(room.getRoom()
								.toString()) + "?", "Yes.", "No.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_1)
			player.getHouse().removeRoom(room);
		end();
	}

	@Override
	public void finish() {
	}

}
