package com.rs.game.npc.ballak;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.utils.Utils;

public enum BallakSpawner {

	ALKHARID_DESERT(new WorldTile(3324, 3294, 0), "east of the Al-Kharid Mine."),
	YANILLE(new WorldTile(2519, 3102, 0), "northest of the Yanille lodestone."),
	CAMELOT_COURT(new WorldTile(2744, 3473, 0), "next to the Camelot Courthouse."),
	BANDIT_CAMP(new WorldTile(3217, 2960, 0), "near the Bandit Camp lodestone."),
	KBD_ENTRANCE(new WorldTile(3053, 3514, 0), "near the KBD entrance.");

	private WorldTile tile;
	private String location;

	BallakSpawner(WorldTile tile, String location) {
		this.tile = tile;
		this.location = location;
	}

	public WorldTile getTile() {
		return tile;
	}

	public String getLocation() {
		return location;
	}

	public static void scheduleSpawn() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			BallakSpawner spawn;
			NPC npc;
			String[] messages = { "Will no warrior come forth to defeat Bal'Lak ", "Bal'lak still stands strong! ",
					"Has no one the strength to defeat Bal'Lak ", "Who will step forth to defeat Bal'lak? ", };

			@Override
			public void run() {

				if (npc != null && !npc.hasFinished()) {
					if (npc.isUnderCombat()) {
						System.out.println(
								"<span style='#00FF00'>Unable to spawn Bal'lak. Currently under combat.</span>");
						return;
					}
					World.sendWorldMessage(
							"<col=FF0000>" + messages[Utils.random(messages.length - 1)] + spawn.getLocation() + ". ",
							false);
					//Discord.sendEventsMessage(messages[Utils.random(messages.length - 1)] + spawn.getLocation() + ". ");

					return;
				}

				spawn = BallakSpawner.values()[Utils.random(BallakSpawner.values().length - 1)];

				if (spawn.getTile() == null || spawn.getLocation() == null) {
					System.out.println("<font color=red>Unable to spawn Bal'lak. Tile or Location is null.</font>");
					return;
				}

				World.spawnNPC(10140, spawn.getTile(), -1, true, true);
				npc = World.getNpc(10140);
				World.sendWorldMessage("--- <col=FF0000>Notice: Bal'Lak has appeared " + spawn.getLocation() + "!",
						false);
			//	Discord.sendEventsMessage("Notice: Bal'Lak has appeared " + spawn.getLocation() + "!");
				System.out.println(
						"<span style='color:green;'>Bal'lak has been spawned " + spawn.getLocation() + "</span>");
			}
		}, 3, 15, TimeUnit.MINUTES);
	}

}
