package com.rs.game.player.content.ports;

import com.rs.game.ForceTalk;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.utils.Utils;

/**
 * Handles The Partner's (John Strum) NPC class.
 * 
 * @author Zeus
 */
public class JohnStrum extends NPC {

	/**
	 * Generated serial UID.
	 */
	private static final long serialVersionUID = 3571142366193536879L;

	/**
	 * NPC ticks.
	 */
	int ticks;

	/**
	 * Construct John Strum NPC.
	 * 
	 * @param id
	 *            The NPC ID.
	 * @param tile
	 *            The WorldTile.
	 */
	public JohnStrum(int id, WorldTile tile) {
		super(16554, tile, -1, true, true);
		// Lets auto-face the correct tile when spawning
		this.setNextFaceWorldTile(new WorldTile(4063, 7271, 0));
	}

	/**
	 * Random messages.
	 */
	private String[] randomTalk = { "Arrrr'" };

	@Override
	public void processNPC() {
		super.processNPC();
		if (ticks++ == 25) {
			this.setNextForceTalk(new ForceTalk(randomTalk[Utils.random(randomTalk.length)]));
			ticks = 0;
		}
	}
}
