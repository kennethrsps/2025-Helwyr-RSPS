package com.rs.game.player.content.clans.citadels.region.generation;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.MapBuilder;
import com.rs.game.player.Player;
import com.rs.game.player.content.clans.citadels.ClanCitadel;
import com.rs.game.player.content.clans.citadels.region.generation.upgrades.CitadelUpgradeConstants;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;


/**
 * 
 * @author Frostbite
 *
 *<contact@frostbitersps@gmail.com><skype@frostbitersps>
 */

public class CitadelGeneration {
	
	/**
	 * Tier 1
	 */

	public static void generateDayTimeTier1Citadel(final Player player) {
		ClanCitadel citadel = player.getClanManager().getClan().getClanCitadel();
		player.lock();
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				citadel.boundChuncks = MapBuilder.findEmptyChunkBound(16, 16);
				MapBuilder.copyAllPlanesMap(CitadelUpgradeConstants.UpgradeConstants.TIER_1.getDayCitadelRx(), CitadelUpgradeConstants.UpgradeConstants.TIER_1.getDayCitadelRy(), citadel.getBoundChuncks()[0],
						citadel.getBoundChuncks()[1], CitadelUpgradeConstants.UpgradeConstants.TIER_1.getHeightRegions(), CitadelUpgradeConstants.UpgradeConstants.TIER_1.getWidthRegions());
				SkillPlotGeneration.generateTier1SkillPlots(player);
				player.setNextAnimation(new Animation(-1));
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						citadel.setCitadelGenerated(true);
						player.getClanManager().getClan().getCitadelNpcs().initalizeNPCS(player);
						player.getClanManager().getClan().getCitadelObjects().initalizeObjects(player);
						player.getClanManager().getClan().getClanCitadel().teleportPlayerToCitadel(player);
						player.unlock();
					}

				}, 1);
			}
		});
	}
	

	public void generateNightTimeTier1Citadel(final Player player) {
		ClanCitadel citadel = player.getClanManager().getClan().getClanCitadel();
		player.lock();
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				citadel.boundChuncks = MapBuilder.findEmptyChunkBound(16, 16);
				MapBuilder.copyAllPlanesMap(CitadelUpgradeConstants.UpgradeConstants.TIER_1.getNightCitadelRx(), CitadelUpgradeConstants.UpgradeConstants.TIER_1.getNightCitadelRy(), citadel.getBoundChuncks()[0],
						citadel.getBoundChuncks()[1], CitadelUpgradeConstants.UpgradeConstants.TIER_1.getHeightRegions(), CitadelUpgradeConstants.UpgradeConstants.TIER_1.getWidthRegions());
				SkillPlotGeneration.generateTier1SkillPlots(player);//TODO Gen Dark too
				player.setNextAnimation(new Animation(-1));
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						citadel.setCitadelGenerated(true);
						player.getClanManager().getClan().getCitadelNpcs().initalizeNPCS(player);
						player.getClanManager().getClan().getCitadelObjects().initalizeObjects(player);
						player.getClanManager().getClan().getClanCitadel()
								.teleportPlayerToCitadel(player);
						player.unlock();
					}

				}, 1);
			}
		});
	}
	
	public static void destroyCitadel(final Player player) {
		ClanCitadel citadel = player.getClanManager().getClan().getClanCitadel();
		player.lock(2);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				citadel.setCitadelGenerated(false);
				MapBuilder.destroyMap(citadel.getBoundChuncks()[0], citadel.getBoundChuncks()[1], CitadelUpgradeConstants.UpgradeConstants.TIER_1.getHeightRegions(), CitadelUpgradeConstants.UpgradeConstants.TIER_1.getWidthRegions());
				System.out.println("[Clan Citadel] - Destroyed "
						+ player.getClanName() + "'s Clan Citadel.");
			}
		}, 1200, TimeUnit.MILLISECONDS);
	}

}
