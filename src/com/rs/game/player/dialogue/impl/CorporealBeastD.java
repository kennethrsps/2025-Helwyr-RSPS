package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.map.bossInstance.BossInstanceHandler;
import com.rs.game.map.bossInstance.BossInstanceHandler.Boss;
import com.rs.game.player.dialogue.Dialogue;

public class CorporealBeastD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option:", "Enter public room", "Instance settings");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				player.getInterfaceManager().closeChatBoxInterface();
				player.setNextWorldTile(new WorldTile(2974, 4384, player.getPlane()));
				player.getControlerManager().startControler("CorpBeastController");
				break;
			case OPTION_2:
				BossInstanceHandler.enterInstance(player, Boss.Corporeal_Beast);
				break;
			}
			break;
		}
	}

	@Override
	public void finish() { }
	
}