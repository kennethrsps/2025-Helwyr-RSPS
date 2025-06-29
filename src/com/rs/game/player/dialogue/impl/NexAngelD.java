package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.dialogue.Dialogue;

public class NexAngelD extends Dialogue {

    @Override
    public void finish() {

    }

    @Override
    public void run(int interfaceId, int componentId) {
	if (stage == -1) {
	    stage = 0;
	    sendOptionsDialogue(SEND_DEFAULT_OPTIONS_TITLE,
		    "Something Realy strong is kept captive there.... You sure you want to go there? ");
	} else if (stage == 0) {
	    if (componentId == OPTION_1)
		player.setNextWorldTile(new WorldTile(2848, 1837, 1));
	    end();
	}
  }

    @Override
    public void start() {
	sendDialogue("What is this place?");
    }

}
