package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.controllers.Barrows;

@SuppressWarnings("serial")
public class BarrowsBrother extends NPC {

    private Barrows barrows;

    public BarrowsBrother(int id, WorldTile tile, Barrows barrows) {
		super(id, tile, -1, true, true);
			this.barrows = barrows;
	    }
	
	    public void disapear() {
		barrows = null;
		finish();
    }

    @Override
    public void finish() {
		if (hasFinished())
		    return;
		if (barrows != null) {
		    barrows.targetFinishedWithoutDie();
		    barrows = null;
		}
		super.finish();
    }

    @Override
    public void sendDeath(Entity source) {
		if (barrows != null) {
		    barrows.targetDied();
		    barrows = null;
		}
		super.sendDeath(source);
    }

}
