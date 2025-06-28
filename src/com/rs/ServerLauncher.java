package com.rs;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alex.store.Index;
import com.discord.Discord;
import com.rs.cache.Cache;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.ItemsEquipIds;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.BotManager;
import com.rs.game.MapBuilder;
import com.rs.game.World;
import com.rs.game.map.bossInstance.BossInstanceHandler;
import com.rs.game.npc.ballak.BallakSpawner;
import com.rs.game.npc.combat.CombatScriptsHandler;
import com.rs.game.npc.combat.NPCCombatDefinitionsManager;
import com.rs.game.player.LendingManager;
import com.rs.game.player.Player;
import com.rs.game.player.actions.runecrafting.SiphonActionNodes;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.FishingSpotsHandler;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.InstancedPVP;
import com.rs.game.player.content.LividFarm;
import com.rs.game.player.content.Lottery;
import com.rs.game.player.content.WeeklyTopRanking;
import com.rs.game.player.content.WellOfGoodWill;
import com.rs.game.player.content.ancientthrone.ThroneManager;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.content.interfaces.arealoot.AreaLoot;
import com.rs.game.player.controllers.ControllerHandler;
import com.rs.game.player.cutscenes.CutscenesHandler;
import com.rs.game.player.dialogue.DialogueHandler;
import com.rs.game.player.newquests.NewQuestManager;
import com.rs.game.topweeks.WeekInfoManager;
import com.rs.game.worldlist.WorldList;
import com.rs.network.ServerChannelHandler;
import com.rs.utils.*;
import com.rs.utils.huffman.Huffman;

/**
 * Main server launcher and initialization class. Handles server startup,
 * scheduled tasks, and shutdown procedures.
 * 
 * @author Zeus
 */
public final class ServerLauncher {

	// Scheduled task references for proper cleanup
	private static ScheduledFuture<?> saveTask;
	private static ScheduledFuture<?> memoryCleanTask;
	private static ScheduledFuture<?> priceRecalcTask;

	// Server state tracking
	private static final AtomicBoolean isShuttingDown = new AtomicBoolean(false);
	private static final AtomicBoolean isInitialized = new AtomicBoolean(false);

	// Constants
	private static final int SAVE_INTERVAL_MINUTES = 1;
	private static final int MEMORY_CLEAN_INTERVAL_MINUTES = 60;
	private static final int PRICE_RECALC_INTERVAL_HOURS = 3;

	/**
	 * Main server launch method.
	 * 
	 * @param args Launch arguments (currently unused)
	 * @throws Exception if initialization fails
	 */
	public static void main(String[] args) throws Exception {
		configureServerSettings();
		init();
	}

	/**
	 * Configure server settings and parameters.
	 */
	private static void configureServerSettings() {
		Settings.GUI_MODE = false;
		Settings.DEBUG = false;
		Settings.ECONOMY_MODE = true;
		Settings.SERVER_PORT = 43596;
		Settings.WORLD_ID = 1;
	}

	/**
	 * Initialize all server components and start services.
	 * 
	 * @throws IOException if initialization fails
	 */
	private static void init() throws IOException {
		if (isInitialized.get()) {
			Logger.log("Server is already initialized!");
			return;
		}

		try {
			Logger.log("Starting server initialization...");
			initializeBackupServices();
			initializeDiscordServices();
			initializeCoreComponents();
			initializePlayerData();
			initializeBasicGameSystems();
			initializeNetworking();
			initializeAdvancedGameContent();
			schedulePeriodicTasks();

			isInitialized.set(true);
			logServerLaunchSuccess();
			

		} catch (IOException e) {
			Logger.log("Server initialization failed due to IO error: " + e.getMessage());
			e.printStackTrace();
			shutdown();
		} catch (Exception e) {
			Logger.log("Server initialization failed: " + e.getMessage());
			e.printStackTrace();
			shutdown();
		}
	}

	/*
	 * private static AutoSkillingWebServer webServer; public static void
	 * startWebServer() { try { webServer = new AutoSkillingWebServer(43594); //
	 * Choose your port webServer.start();
	 * System.out.println("Auto-skilling web server started on port 43594"); } catch
	 * (Exception e) { e.printStackTrace(); } }
	 */
	/**
	 * Initialize backup services if not in debug mode.
	 */
	private static void initializeBackupServices() {
		if (!Settings.DEBUG) {
			AutoBackup.init();
		}
	}

	/**
	 * Initialize Discord integration.
	 */
	private static void initializeDiscordServices() {
		Discord.startUp();
	}

	/**
	 * Initialize core game components.
	 * 
	 * @throws IOException if core component initialization fails
	 */
	private static void initializeCoreComponents() throws IOException {
		Logger.log("Initializing core components...");

		try {
			Cache.init();
			ItemsEquipIds.init();
			Huffman.init();
			WorldList.init();

		} catch (IOException e) {
			Logger.log("Failed to initialize core components: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Initialize player-related data and systems.
	 */
	private static void initializePlayerData() {
		Logger.log("Initializing player data systems...");

		DisplayNames.init();
		IPBanL.init();
		MACBan.init();
		IPMute.init();
		PkRank.init();
		WealthRank.init();
		DTRank.init();
		DonationRank.init();
		VoteHiscores.init();
		AreaLoot.startUpdaterDelayed();
		 BotManager.init();
		//startWebServer();
	}

	/**
	 * Initialize basic game systems that don't depend on World being fully
	 * initialized.
	 * 
	 * @throws IOException if basic game system initialization fails
	 */
	private static void initializeBasicGameSystems() throws IOException {
		Logger.log("Initializing basic game systems...");

		// Initialize basic data first
		initializeItemSystems();
		initializeNPCSystems();
		initializeQuestSystems();
	}

	/**
	 * Initialize advanced game content that requires World and other systems to be
	 * ready.
	 * 
	 * @throws IOException if advanced game content initialization fails
	 */
	private static void initializeAdvancedGameContent() throws IOException {
		Logger.log("Initializing advanced game content...");

		// World and map initialization (after World.init() in networking)
		initializeWorldData();

		// Game features that may depend on world/map data
		initializeGameFeatures();
	}

	/**
	 * Initialize world and map data.
	 */
	private static void initializeWorldData() {
		ItemSpawns.init();
		ObjectSpawns.init();
		MapBuilder.init();
		ObjectSpawns.addCustomSpawns();
	}

	/**
	 * Initialize NPC-related systems.
	 */
	private static void initializeNPCSystems() {
		npcNames.init();
		NPCSpawns.init();
		NPCCombatDefinitionsL.init();
		NPCBonuses.init();
		NPCDrops.init();
		NPCExamines.init();
		CombatScriptsHandler.init();
		FishingSpotsHandler.init();
		NPCSpawns.addCustomSpawns();
		NPCBonuses.init();
		NPCCombatDefinitionsManager.loadCombatDefinitions();
		NPCCombatDefinitionsManager.loadCustomBossStats();
		NPCBonuses.loadCustomBossBonuses();
		BossBalancer.loadAllBossConfigurations();

		System.out.println("Boss Balancer System loaded successfully!");
	}

	/**
	 * Initialize item-related systems.
	 */
	private static void initializeItemSystems() {
		ItemExamines.init();
		ItemWeights.init();
		ItemBonuses.init();
		ShopsHandler.init();
	}

	/**
	 * Initialize game features and content.
	 * 
	 * @throws IOException if game feature initialization fails
	 */
	private static void initializeGameFeatures() throws IOException {
		try {
			LividFarm.init();
			WeeklyTopRanking.init();
			Lottery.init();
			GrandExchange.init();
			MusicHints.init();
			ClansManager.init();
			WellOfGoodWill.load();
			LendingManager.init();
			SiphonActionNodes.init();
			InstancedPVP.init();
			BossInstanceHandler.init();
			//ThroneManager.init();
			//WeekInfoManager.init();
			StarterMap.getSingleton().init();
			/*
			 * try { initializeBotSystemSafely();
			 * System.out.println("Clean Bot System initialized!"); } catch (Exception e) {
			 * System.err.println("Failed to initialize bot system: " + e.getMessage()); }
			 */
		} catch (IOException e) {
			Logger.log("Failed to initialize game features: " + e.getMessage());
			throw e;
		}
	}

	/**
	 * Initialize quest and dialogue systems.
	 */
	private static void initializeQuestSystems() {
		DialogueHandler.init();
		ControllerHandler.init();
		CutscenesHandler.init();
		FriendChatsManager.init();
		NewQuestManager.Quests.init();
	}

	/**
	 * Initialize networking components and World system.
	 * 
	 * @throws Exception if networking initialization fails
	 */
	private static void initializeNetworking() throws Exception {
		Logger.log("Initializing networking and world systems...");

		// Initialize cores and world first - these are required for map operations
		CoresManager.init();
		World.init();

		// Now it's safe to initialize networking
		ServerChannelHandler.init();

		// Schedule NPC spawning after world is ready
		BallakSpawner.scheduleSpawn();
	}

	/**
	 * Schedule all periodic maintenance tasks.
	 */
	private static void schedulePeriodicTasks() {
		Logger.log("Scheduling periodic tasks...");

		scheduleDataSavingTask();

		if (!Settings.DEBUG) {
			scheduleMemoryCleaningTask();
			schedulePriceRecalculationTask();
		}
	}

	/**
	 * Schedule the periodic data saving task.
	 */
	private static void scheduleDataSavingTask() {
		saveTask = CoresManager.slowExecutor.scheduleWithFixedDelay(new DataSavingTask(), SAVE_INTERVAL_MINUTES,
				SAVE_INTERVAL_MINUTES, TimeUnit.MINUTES);
	}

	/**
	 * Schedule the memory cleaning task.
	 */
	private static void scheduleMemoryCleaningTask() {
		memoryCleanTask = CoresManager.slowExecutor.scheduleWithFixedDelay(new MemoryCleaningTask(),
				MEMORY_CLEAN_INTERVAL_MINUTES, MEMORY_CLEAN_INTERVAL_MINUTES, TimeUnit.MINUTES);
	}

	/**
	 * Schedule the price recalculation task.
	 */
	private static void schedulePriceRecalculationTask() {
		priceRecalcTask = CoresManager.slowExecutor.scheduleWithFixedDelay(new PriceRecalculationTask(),
				PRICE_RECALC_INTERVAL_HOURS, PRICE_RECALC_INTERVAL_HOURS, TimeUnit.HOURS);
	}

	/**
	 * Log successful server launch.
	 */
	private static void logServerLaunchSuccess() {
		String mode = Settings.ECONOMY_MODE ? (Settings.DEBUG ? "DEVELOPMENT" : "ECONOMY") : "PVP";
		Logger.log("Server launched successfully in " + mode + " mode; " + "data: " + Settings.REVISION + "/"
				+ Settings.SUB_REVISION + "/" + Settings.SERVER_PORT + ".");
		
	}

	/**
	 * Runnable task for periodic data saving.
	 */
	private static class DataSavingTask implements Runnable {
		@Override
		public void run() {
			if (isShuttingDown.get())
				return;

			try {
				saveAllData();
				Logger.log("All files successfully saved; players: " + World.getPlayersOnline() + ".");
			} catch (Throwable e) {
				Logger.log("Data saving task failed: " + e.getMessage());
				Logger.handle(e);
			}
		}
	}

	/**
	 * Runnable task for memory cleaning.
	 */
	private static class MemoryCleaningTask implements Runnable {
		@Override
		public void run() {
			if (isShuttingDown.get())
				return;

			try {
				performMemoryCleanup();

				if (!Settings.SQL_ENABLED) {
					Settings.SQL_ENABLED = true;
					Logger.log("Memory cleanup completed; SQL re-enabled.");
				}
			} catch (Throwable e) {
				Logger.log("Memory cleanup task failed: " + e.getMessage());
				Logger.handle(e);
				forceMemoryCleanup();
			}
		}
	}

	/**
	 * Runnable task for price recalculation.
	 */
	private static class PriceRecalculationTask implements Runnable {
		@Override
		public void run() {
			if (isShuttingDown.get())
				return;

			try {
				GrandExchange.recalcPrices();
			} catch (Throwable e) {
				Logger.log("Price recalculation task failed: " + e.getMessage());
				Logger.handle(e);
			}
		}
	}

	/**
	 * Perform standard memory cleanup operations.
	 */
	private static void performMemoryCleanup() {
		resetCacheIndexes();
		CoresManager.fastExecutor.purge();
		System.gc();
	}

	/**
	 * Force aggressive memory cleanup when standard cleanup fails.
	 */
	private static void forceMemoryCleanup() {
		boolean needsForceClean = Runtime.getRuntime().freeMemory() < Settings.MINIMUM_RAM_ALLOCATED;
		cleanMemory(needsForceClean);
	}

	/**
	 * Clean memory with optional force mode.
	 * 
	 * @param force If true, performs aggressive cleanup including clearing
	 *              definitions
	 */
	public static void cleanMemory(boolean force) {
		if (force) {
			clearGameDefinitions();
		}
		performMemoryCleanup();
	}

	/**
	 * Clear cached game definitions to free memory.
	 */
	private static void clearGameDefinitions() {
		ItemDefinitions.clearItemsDefinitions();
		NPCDefinitions.clearNPCDefinitions();
		ObjectDefinitions.clearObjectDefinitions();
	}

	/**
	 * Reset all cache indexes to free memory.
	 */
	private static void resetCacheIndexes() {
		if (Cache.STORE != null && Cache.STORE.getIndexes() != null) {
			for (Index index : Cache.STORE.getIndexes()) {
				if (index != null) {
					index.resetCachedFiles();
				}
			}
		}
	}

	/**
	 * Save all player and system data.
	 * 
	 * @throws Exception if saving fails
	 */
	private static void saveAllData() throws Exception {
		savePlayerData();

		if (!Settings.DEBUG) {
			saveSystemData();
		}
	}

	/**
	 * Save all active player data.
	 */
	private static void savePlayerData() {
		for (Player player : World.getPlayers()) {
			if (player != null && player.isActive() && !player.hasFinished()) {
				try {
					SerializableFilesManager.savePlayer(player);
				} catch (Exception e) {
					Logger.log("Failed to save player: " + player.getUsername() + " - " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Save all system data.
	 */
	private static void saveSystemData() {
		try {
			GrandExchange.save();
			IPBanL.save();
			IPMute.save();
			MACBan.save();
			DisplayNames.save();
			WellOfGoodWill.save();
			PkRank.save();
			DTRank.save();
			DonationRank.save();
			VoteHiscores.save();
			Lottery.save();
			WeeklyTopRanking.save();
		} catch (Exception e) {
			Logger.log("Failed to save system data: " + e.getMessage());
		}
	}

	/**
	 * Properly shutdown all scheduled tasks.
	 */
	private static void shutdownScheduledTasks() {
		if (saveTask != null && !saveTask.isCancelled()) {
			saveTask.cancel(false);
		}
		if (memoryCleanTask != null && !memoryCleanTask.isCancelled()) {
			memoryCleanTask.cancel(false);
		}
		if (priceRecalcTask != null && !priceRecalcTask.isCancelled()) {
			priceRecalcTask.cancel(false);
		}
	}

	/**
	 * Close all server services and networking.
	 */
	public static void closeServices() {
		if (isShuttingDown.getAndSet(true)) {
			return; // Already shutting down
		}

		Logger.log("Shutting down server services...");

		try {
			// Save data before shutdown
			saveAllData();
		} catch (Exception e) {
			Logger.log("Failed to save data during shutdown: " + e.getMessage());
		}

		shutdownScheduledTasks();
		ServerChannelHandler.shutdown();
		CoresManager.shutdown();

		Logger.log("Server services shutdown complete.");
	}

	/**
	 * Restart the server by launching a new instance.
	 */
	public static void restartEmulator() {
		Logger.log("Restarting server...");
		closeServices();
		System.gc();

		try {
			Runtime.getRuntime().exec("cmd /c start run.bat");
			System.exit(0);
		} catch (Throwable e) {
			Logger.handle(e);
			System.exit(1);
		}
	}
	/**
	
	/**
	 * Completely shutdown the server.
	 */
	public static void shutdown() {
		Logger.log("Server shutdown initiated...");

		try {
			closeServices();
		} finally {
			System.exit(0);
		}
	}
}