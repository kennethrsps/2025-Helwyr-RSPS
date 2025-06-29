package com.rs.game.player.content.clans.citadels.region.generation;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.MapBuilder;
import com.rs.game.player.Player;
import com.rs.game.player.content.clans.citadels.ClanCitadel;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Logger;

public class SkillPlotGeneration {
	
	
	public static void generateTier1SkillPlots(final Player player) {
		ClanCitadel citadel = player.getClanManager().getClan().getClanCitadel();
		player.lock();
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					MapBuilder.copyMap(490, 506, citadel.getBoundChuncks()[0] * 1 + 10, citadel.getBoundChuncks()[1] + 5, 1, 2, new int[1], new int[1]); // wc plot
					MapBuilder.copyAllPlanesMap(490, 510, citadel.getBoundChuncks()[0] * 1 + 5, citadel.getBoundChuncks()[1] + 5, 1, 2);// mining plot
					MapBuilder.copyMap(508, 506, citadel.getBoundChuncks()[0] * 1 + 2, citadel.getBoundChuncks()[1] + 6, 1, 2, new int[1], new int[1]);// crafting plot (thread)
					MapBuilder.copyMap(508, 510, citadel.getBoundChuncks()[0] * 1 + 4, citadel.getBoundChuncks()[1] + 9, 1, 2, new int[1], new int[1]);// furnace
					MapBuilder.copyMap(499, 506, citadel.getBoundChuncks()[0] * 1 + 2, citadel.getBoundChuncks()[1] + 9, 1, 2, new int[1], new int[1]);// kiln
					MapBuilder.copyMap(514, 510, citadel.getBoundChuncks()[0] * 1 + 3, citadel.getBoundChuncks()[1] + 12, 1, 2, new int[1], new int[1]);// kiln
					player.reset();
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextAnimation(new Animation(-1));
							player.unlock(); // unlocks player
						}
					}, 1);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		});
	}

}
