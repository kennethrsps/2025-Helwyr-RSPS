package com.rs.game.activities.instances;

import com.rs.game.Animation;
import com.rs.game.MapBuilder;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.npc.gwd2.gregorovic.Gregorovic;
import com.rs.game.player.Player;

/**
 * @ausky Tom
 * @date April 9, 2017
 */

public class GregorovicInstance extends Instance {

	private final WorldTile[] masks;
	
	public GregorovicInstance(Player owner, int instanceDuration, int respawnSpeed, int playersLimit, int password,
			int bossId, boolean hardMode) {
		super(owner, instanceDuration, respawnSpeed, playersLimit, password, bossId, hardMode);
		chunksToBind = new int[] { 407, 879 };
		sizes = new int[] { 8, 10 };
		boundChunks = MapBuilder.findEmptyChunkBound(sizes[0], sizes[1]);
		masks = new WorldTile[] { getWorldTile(32, 37), getWorldTile(43, 55), getWorldTile(55, 37) };
	}

	@Override
	public WorldTile getWorldTile(int x, int y) {
		return new WorldTile((boundChunks[0] * 8) + x, (boundChunks[1] * 8) + y, 1);
	}

	@Override
	public WorldTile getWaitingRoomCoords() {
		return getWorldTile(30, 30);
	}

	@Override
	public void performOnSpawn() {
		boss.setNextAnimation(new Animation(28223));
		//getPlayers().forEach(p -> {
		//	if (p != null && p.getX() <= p.getCurrentInstance().getWorldTile(31, 30).getX())
		//		HeartOfGielinor.switchInterfaces(p, this, getGregorovic(), true);
		//});
	}

	public WorldTile[] getMasks() {
		return masks;
	}

	public NPC getGregorovic() {
		return boss;
	}

	@Override
	public WorldTile getOutsideCoordinates() {
		return null;
	}

	@Override
	public NPC getBossNPC() {
		return new Gregorovic(22443, new WorldTile(getWorldTile(43, 44)), -1, true, true, this);
	}

}
