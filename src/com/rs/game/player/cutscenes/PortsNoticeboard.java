package com.rs.game.player.cutscenes;

import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.cutscenes.actions.CutsceneAction;
import com.rs.game.player.cutscenes.actions.LookCameraAction;
import com.rs.game.player.cutscenes.actions.PosCameraAction;

public class PortsNoticeboard extends Cutscene {

	@Override
	public boolean hiddenMinimap() {
		return false;
	}

	@Override
	public CutsceneAction[] getActions(Player player) {
		ArrayList<CutsceneAction> actionsList = new ArrayList<CutsceneAction>();
		actionsList
				.add(new PosCameraAction(getX(player, player.getX() + 5), getY(player, player.getY() + 3), 1500, -1));

		actionsList.add(new LookCameraAction(getX(player, 4071), getY(player, 7279), 1500, 3));
		return actionsList.toArray(new CutsceneAction[actionsList.size()]);
	}
}