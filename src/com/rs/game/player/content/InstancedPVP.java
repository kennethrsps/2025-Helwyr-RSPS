package com.rs.game.player.content;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.Settings;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.MapInstance;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;

public class InstancedPVP {

	public static int MAIN_NPC = 13285, REWARD_SHOP = -1;
	private static List<Player> players;

	private static MapInstance map;

	public static void init() {
		int[] pos = getMapPos();
		int[] size = getMapSize();
		players = new CopyOnWriteArrayList<Player>();
		map = new MapInstance(pos[0], pos[1], size[0], size[1]);
		map.load(new Runnable() {
			@Override
			public void run() {
				NPC npc = new NPC(MAIN_NPC, getTile(3087, 3506, 0), -1, false);
				npc.setRandomWalk(0);
				npc = new NPC(2759, getTile(3097, 3494, 0), -1, false);
				npc.setRandomWalk(0);
				npc = new NPC(2759, getTile(3096, 3493, 0), -1, false);
				npc.setRandomWalk(0);
				npc = new NPC(2759, getTile(3096, 3491, 0), -1, false);
				npc.setRandomWalk(0);
				npc = new NPC(2759, getTile(3096, 3489, 0), -1, false);
				npc.setRandomWalk(0);
			}
		});
	}

	public static void enterInstacedPVP(Player player, boolean loggedIn) {
		if (loggedIn) {
			players.add(player);
			return;
		}
		for (Item item : player.getInventory().getItems().getItems()) {
			if (item != null) {
				player.getPackets().sendGameMessage("You can't carry items with you into spawn pvp area.");
				return;
			}
		}
		for (Item item : player.getEquipment().getItems().getItems()) {
			if (item != null) {
				player.getPackets().sendGameMessage("You can't carry items with you into spawn pvp area.");
				return;
			}
		}
		if (player.getFamiliar() != null || player.getPet() != null) {
			player.getPackets().sendGameMessage("You can't carry familiers with you into spawn pvp area.");
			return;
		}
		players.add(player);
		player.getBanksManager().setPVPBank();
		player.useStairs(-1, getTile(3088, 3501, 0), 0, 2);
		for (int skill = 0; skill < 25; skill++) {
			player.setSavedXP(skill, player.getSkills().getXp(skill));
			player.setSavedLevel(skill, player.getSkills().getLevelForXp(skill));
		}
		player.setXpLocked(true);
		player.getControlerManager().startControler("InstancedPVPControler");
	}

	public static void leaveInstacedPVP(Player player, boolean loggedOut) {
		if (loggedOut) {
			players.remove(player);
			return;
		}
		if (player.getFamiliar() != null)
			player.getFamiliar().dissmissFamiliar(false);
		if (player.getPet() != null)
			player.getPet().pickup();
		player.getInventory().reset();
		player.getEquipment().reset();
		player.getBanksManager().removePVPBank();
		player.setCanPvp(false);
		player.setXpLocked(false);
		player.getInterfaceManager().closeOverlay(false);
		for (int skill = 0; skill < 25; skill++) {
			player.getSkills().set(skill, player.getSavedLevel()[skill]);
			player.getSkills().setXp(skill, player.getSavedXP()[skill]);
		}
		player.getGlobalPlayerUpdater().generateAppearenceData();
		player.setNextWorldTile(Settings.RESPAWN_PLAYER_LOCATION);
		players.remove(player);
	}

	public static WorldTile getTile(WorldTile tile) {
		return getTile(tile.getX(), tile.getY(), tile.getPlane());
	}

	public static WorldTile getTile(int x, int y, int plane) {
		int[] originalPos = map.getOriginalPos();
		WorldTile tile = map.getTile((x - originalPos[0] * 8) % (map.getRatioX() * 64),
				(y - originalPos[1] * 8) % (map.getRatioY() * 64));
		tile.moveLocation(0, 0, plane);
		return tile;
	}

	public static int[] getMapSize() {
		return new int[] { 5, 9 };
	}

	public static int[] getMapPos() {
		return new int[] { 368, 432 };
	}

	public static List<Player> getPlayers() {
		return players;
	}

}
