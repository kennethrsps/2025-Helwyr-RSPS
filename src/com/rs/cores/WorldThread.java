package com.rs.cores;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public final class WorldThread extends Thread {
	public static volatile long WORLD_CYCLE;

	protected WorldThread() {
		setPriority(Thread.MAX_PRIORITY);
		setName("World Thread");
	}

	@Override
	public final void run() {
		while (!CoresManager.shutdown) {
			WORLD_CYCLE++; // made the cycle update at begin instead of end cuz
							// at end theres 600ms then to next cycle
			long currentTime = Utils.currentTimeMillis();
			// long debug = Utils.currentTimeMillis();

			try {
				WorldTasksManager.processTasks();
			} catch (Throwable e) {
				Logger.handle(e);
			}

			// CLEAN BOT SYSTEM: Process bot logic (before player processing)

			try {
				for (Player player : World.getPlayers()) {
					if (player == null || !player.isActive() || player.hasFinished())
						continue;

					// CLEAN BOT INTEGRATION: Safe processing for bots
					else {
						player.processEntity();
					}
				}
				for (NPC npc : World.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					npc.processEntity();
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}

			try {
				for (Player player : World.getPlayers()) {
					if (player == null || !player.isActive() || player.hasFinished())
						continue;

					// CLEAN BOT INTEGRATION: Safe entity update for bots
					else {
						player.processEntityUpdate();
					}
				}
				for (NPC npc : World.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					npc.processEntityUpdate();
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}

			try {
				// //
				// System.out.print(" ,NPCS PROCESS:
				// "+(Utils.currentTimeMillis()-debug));
				// debug = Utils.currentTimeMillis();
				for (Player player : World.getPlayers()) {
					if (player == null || !player.isActive() || player.hasFinished())
						continue;

					// CLEAN BOT INTEGRATION: Skip packet sending for bots
					if (!player.isBot()) {
						player.getPackets().sendLocalPlayersUpdate();
						player.getPackets().sendLocalNPCsUpdate();
					}
					// Bots don't need to receive updates since they're not real clients
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}

			try {
				// System.out.print(" ,PLAYER UPDATE:
				// "+(Utils.currentTimeMillis()-debug)+",
				// "+World.getPlayers().size()+", "+World.getNPCs().size());
				// debug = Utils.currentTimeMillis();
				for (Player player : World.getPlayers()) {
					if (player == null || !player.isActive() || player.hasFinished())
						continue;

					// CLEAN BOT INTEGRATION: Safe mask reset for bots
					else {
						player.resetMasks();
					}
				}
				for (NPC npc : World.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					npc.resetMasks();
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}

			// //
			// System.out.println(" ,TOTAL:
			// "+(Utils.currentTimeMillis()-currentTime));
			long sleepTime = Settings.WORLD_CYCLE_TIME + currentTime - Utils.currentTimeMillis();
			if (sleepTime <= 0)
				continue;
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				Logger.handle(e);
			}
		}
	}

}
