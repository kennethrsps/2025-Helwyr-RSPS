package com.rs.game.player.dialogue;

import com.rs.game.player.Player;
import com.rs.game.player.controllers.ArtisansWorkShopControler;
import com.rs.game.player.dialogue.impl.ClanInvite;
import com.rs.game.player.dialogue.impl.CosmeticStoreD;

public class DialogueManager {

	private Player player;
	private Dialogue lastDialogue;

	public DialogueManager(Player player) {
		this.player = player;
	}

	public void startDialogue(Object key, Object... parameters) {
		if (!player.getControlerManager().useDialogueScript(key))
			return;
		if (lastDialogue != null)
			lastDialogue.finish();
		lastDialogue = DialogueHandler.getDialogue(key);
		if (lastDialogue == null)
			return;
		lastDialogue.parameters = parameters;
		lastDialogue.setPlayer(player);
		lastDialogue.start();
	}

	public void continueDialogue(int interfaceId, int componentId) {
		if (lastDialogue == null)
			return;
		if (!player.getInterfaceManager().containsChatBoxInter() && !(lastDialogue instanceof ClanInvite)
				&& !(lastDialogue instanceof CosmeticStoreD)
				&& !(player.getControlerManager().getControler() instanceof ArtisansWorkShopControler))
			return;
		lastDialogue.run(interfaceId, componentId);
	}

	public void finishDialogue() {
		if (lastDialogue == null)
			return;
		lastDialogue.finish();
		lastDialogue = null;
		if (player.getInterfaceManager().containsChatBoxInter())
			player.getInterfaceManager().closeChatBoxInterface();
	}
}