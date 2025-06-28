package com.rs.game.player.controllers.zombie;

import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activites.zombie.ZombieLobby;
import com.rs.game.player.controllers.Controller;
import com.rs.game.player.dialogue.Dialogue;

public class ZombieLobbyControler extends Controller {

	@Override
	public void start() {

	}

	@Override
	public void sendInterfaces() {
		player.getInterfaceManager().sendOverlay(407, false);
	}

	@Override
	public boolean processMagicTeleport(WorldTile toTile) {
		this.forceClose();
		return false;
	}

	@Override
	public boolean processItemTeleport(WorldTile toTile) {
		this.forceClose();
		return false;
	}

	@Override
	public void forceClose() {
		player.closeInterfaces();
		player.getInterfaceManager().closeOverlay(false);
		ZombieLobby.exitLooby(player);
	}

	@Override
	public boolean login() {
		return false;
	}

	@Override
	public boolean logout() {
		this.setArguments(null);
		return false;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		switch (object.getId()) {
		case 24819:
			player.getDialogueManager().startDialogue(new Dialogue() {
				@Override
				public void start() {
					sendDialogue("Are you sure you would like to leave the Lobby?");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					if (stage == -1) {
						stage = 0;
						sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, "Yes, get me out of here!", "No, I want to stay.");
					} else if (stage == 0) {
						if (componentId == OPTION_1)
							player.getControlerManager().forceStop();
						end();
					}
				}

				@Override
				public void finish() {

				}
			});
			return false;
		}
		return true;
	}

}
