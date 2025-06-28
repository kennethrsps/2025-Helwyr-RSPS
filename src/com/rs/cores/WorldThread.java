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
            WORLD_CYCLE++;
            long currentTime = Utils.currentTimeMillis();

            try {
                WorldTasksManager.processTasks();
            } catch (Throwable e) {
                Logger.handle(e);
            }

            try {
                for (Player player : World.getPlayers()) {
                    // --- ADD THESE DEBUG LINES ---
                    if (player.isBot()) {
                        System.out.println("DEBUG (WorldThread): Found bot: " + player.getUsername() +
                                           " Active: " + player.isActive() +
                                           " Finished: " + player.hasFinished());
                    }
                    // --- END DEBUG LINES ---

                    if (player == null || !player.isActive() || player.hasFinished()) {
                        if (player.isBot()) { // Log why bot is skipped
                            System.out.println("DEBUG (WorldThread): Skipping bot " + player.getUsername() +
                                               " because isActive=" + player.isActive() +
                                               ", hasFinished=" + player.hasFinished());
                        }
                        continue;
                    }
                    player.processEntity(); // This should call BotPlayer.processEntity()
                }
                for (NPC npc : World.getNPCs()) {
					if (npc == null || npc.hasFinished())
						continue;
					npc.processEntityUpdate();
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
            // --- FIX END ---

			try {
				// //
				// System.out.print(" ,NPCS PROCESS:
				// "+(Utils.currentTimeMillis()-debug));
				// debug = Utils.currentTimeMillis();
				for (Player player : World.getPlayers()) {
					if (player == null || !player.isActive() || player.hasFinished())
						continue;

					// CLEAN BOT INTEGRATION: Skip packet sending for bots (this is correct)
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

					// CLEAN BOT INTEGRATION: Mask reset should happen for all
					player.resetMasks();
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