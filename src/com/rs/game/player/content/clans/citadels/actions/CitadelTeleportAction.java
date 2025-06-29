package com.rs.game.player.content.clans.citadels.actions;

import com.rs.game.WorldTile;
import com.rs.game.player.Player;

public class CitadelTeleportAction {
	
	public static WorldTile getCitadelWorldTile(Player player, int mapX, int mapY) {
		return new WorldTile(player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[0] * 16 + mapX, player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[1] * 16 + mapY, 0);
	}

}
