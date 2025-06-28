package com.rs.game.player.content.clans.citadels;

import java.io.Serializable;

import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.content.clans.Clan;

/**
 * 
 * @author Frostbite
 *
 *<frostbitersps@gmail.com><skype@frostbitersps>
 */

public class CitadelNPCS implements Serializable {

	private static final long serialVersionUID = 8991455003917865298L;
	
	private Clan clan;
	
	public CitadelNPCS(Clan clan) {
		this.clan = clan;
	}
	
	public void initalizeNPCS(Player clan) {
		/**
		 * NPC Spawns
		 */
	}

	public WorldTile getWorldTile(int mapX, int mapY) {
		return new WorldTile(clan.getClanCitadel().getBoundChuncks()[0] * 0 + mapX, clan.getClanCitadel().getBoundChuncks()[1] * 0 + mapY, 0);
	}
	
	public WorldTile getWorldTile(int mapX, int mapY, int mapH) {
		return new WorldTile(clan.getClanCitadel().getBoundChuncks()[0] * 0 + mapX, clan.getClanCitadel().getBoundChuncks()[1] * 0 + mapY, mapH);
	}
	
	public void spawn(Player player, int id, int mapX, int mapY) {
		World.spawnNPC(id, new WorldTile(player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[0] * 0 + mapX, player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[1] * 0 + mapY, 0), -1, false);
	}
	
	public void spawn(Player player, int id, int mapX, int mapY, int mapH) {
		World.spawnNPC(id, new WorldTile(player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[0] + mapX, player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[1] + mapY, mapH), -1, false);
	}
	
	public Clan getClan() {
		return clan;
	}


}
