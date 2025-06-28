package com.rs.game.npc.others;

import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.controllers.RunespanController;
import com.rs.utils.Utils;

@SuppressWarnings("serial")
public class YellowWizard extends NPC {

    public static void giveReward(Player player) {

    }

    private RunespanController controler;
    private long spawnTime;

    public YellowWizard(WorldTile tile, RunespanController controler) {
	super(15430, tile, -1, true, true);
	spawnTime = Utils.currentTimeMillis();
	this.controler = controler;
    }

    @Override
    public void finish() {
	controler.removeWizard();
	super.finish();
    }

    @Override
    public void processNPC() {
	if (spawnTime + 300000 < Utils.currentTimeMillis())
	    finish();
    }

    @Override
    public boolean withinDistance(Player tile, int distance) {
	return tile == controler.getPlayer()
		&& super.withinDistance(tile, distance);
    }

}
