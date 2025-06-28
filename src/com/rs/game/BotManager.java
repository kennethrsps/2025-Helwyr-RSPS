package com.rs.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.discord.Discord;
import com.rs.Settings;
import com.rs.game.activites.BountyHunter;
import com.rs.game.item.Item;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.BotPlayer;
import com.rs.game.player.Equipment;
import com.rs.game.player.MembershipHandler;
import com.rs.game.player.actions.ActionManager;
import com.rs.game.player.content.HintIconsManager;
import com.rs.game.player.content.InterfaceManager;
import com.rs.game.player.content.LocalNPCUpdate;
import com.rs.game.player.content.LocalPlayerUpdate;
import com.rs.game.player.content.LogicPacket;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.MoneyPouch;
import com.rs.game.player.content.PriceCheckManager;
import com.rs.game.player.content.Toolbelt;
import com.rs.game.player.content.ToolbeltNew;
import com.rs.game.player.content.construction.House;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.content.grandExchange.GrandExchangeManager;
import com.rs.game.player.content.pet.PetManager;
import com.rs.game.player.content.slayer.CooperativeSlayer;
import com.rs.game.player.content.xmas.XmasEvent;
import com.rs.game.player.cutscenes.CutscenesManager;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.dialogue.DialogueManager;
import com.rs.game.player.dialogue.impl.StarterTutorialD;
import com.rs.game.player.newquests.NewQuestManager;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.StarterMap;
import com.rs.utils.Utils;

import com.rs.game.player.Skills;
import com.rs.game.player.CombatDefinitions;
import com.rs.game.player.ControlerManager;
import com.rs.game.player.Prayer;
import com.rs.game.player.Bank;
import com.rs.game.player.MusicsManager;
import com.rs.game.player.EmotesManager;
import com.rs.game.player.FriendsIgnores;
import com.rs.game.player.DominionTower;
import com.rs.game.player.DungManager;
import com.rs.game.player.AuraManager;
import com.rs.game.player.QuestManager;
import com.rs.game.player.ChargesManager;
import com.rs.game.player.GlobalPlayerUpdater;
import com.rs.game.player.Inventory;
import com.rs.game.player.Equipment;

import com.rs.game.player.BanksManager;
import com.rs.game.player.SlayerManager;
import com.rs.game.player.GearPresets;
import com.rs.game.player.ElderTreeManager;
import com.rs.game.player.SquealOfFortune;
import com.rs.game.player.TreasureTrails;
import com.rs.game.player.FarmingManager;
import com.rs.game.player.PlayerOwnedPort;
import com.rs.game.player.PetLootManager;
import com.rs.game.player.PerkManager;
import com.rs.game.player.Titles;
import com.rs.game.player.DayOfWeekManager;
import com.rs.game.player.DailyTaskManager;
import com.rs.game.player.Notes;
import com.rs.network.Session; // Needed for WorldPacketsEncoder constructor
import com.rs.network.protocol.codec.encode.WorldPacketsEncoder; // <--- CORRECTED IMPORT FOR WORLDPACKETSENCODER


/**
 * Manages all BotPlayers on the server. This class is responsible for creating,
 * storing, processing, and saving bot characters.
 */
public class BotManager {

	private static final Map<String, BotPlayer> bots = new ConcurrentHashMap<>();

	public static void init() {
		Logger.log("BotManager", "Bot Manager Initialized.");
	}

	public static void process() {
		for (BotPlayer bot : bots.values()) {
			try {
				bot.processEntity();
			} catch (Exception e) {
				Logger.log("BotManager", "Error processing bot: " + bot.getUsername() + ". Error: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Creates and adds a new bot to the world. This method now handles the full
	 * initialization of the bot, mirroring the logic from your LoginManager.java
	 * and safely bypassing the tutorial.
	 */
	public static void createBot(String username) {
		String formattedUsername = Utils.formatPlayerNameForProtocol(username);
		if (bots.containsKey(formattedUsername) || World.getPlayer(username) != null) {
			System.err.println("Bot or player with name " + username + " already exists.");
			return;
		}

		// 1. Create the bot instance
		BotPlayer bot = new BotPlayer();

		// 2. Set username and critical pre-initialization settings
		bot.setUsername(username);
		bot.setHasCompleted(true); // Ensure tutorial is bypassed
		bot.setExpert(true);
		bot.setCurrentFriendChatOwner("zeus"); // Prevents bot from trying to join a friend chat on creation.
		bot.setLogedIn(); // Mark bot as logged in to bypass potential login screen/tutorial checks

		// --- Start of immediate bot-specific initialization ---
		// These coordinates are confirmed to be your skilling area.
		WorldTile desiredSpawnLocation = new WorldTile(1375, 5669, 0);
		bot.setNextWorldTile(desiredSpawnLocation);

		// --- START OF MANAGER INITIALIZATION AND LINKING ---
        // This section ensures all transient managers that are instantiated in Player.java's
        // constructor, but need their internal 'player' field set, are properly linked.
        // It also handles managers accessed by getters that lazy-load.

		// Managers initialized directly with 'new Manager(bot)' or similar in Player constructor:
        bot.interfaceManager = new InterfaceManager(bot);
        bot.dialogueManager = new DialogueManager(bot);
        bot.hintIconsManager = new HintIconsManager(bot);
        bot.priceCheckManager = new PriceCheckManager(bot);
        bot.localPlayerUpdate = new LocalPlayerUpdate(bot);
        bot.localNPCUpdate = new LocalNPCUpdate(bot);
        bot.actionManager = new ActionManager(bot);
        bot.cutscenesManager = new CutscenesManager(bot);
        bot.pouch = new MoneyPouch(bot);
        bot.inventory = new Inventory();
        bot.dungManager = new DungManager();
        bot.dayOfWeekManager = new DayOfWeekManager(); // Instantiated here, player field set below
        bot.dailyTaskManager = new DailyTaskManager(); // Instantiated here, player field set below
        bot.banksManager = new BanksManager();
        bot.newQuestManager = new NewQuestManager();
        bot.slayerManager = new SlayerManager();
        bot.gearPresets = new GearPresets();
        bot.equipment = new Equipment();
        bot.skills = new Skills();
        bot.bountyHunter = new BountyHunter();
        bot.elderTreeManager = new ElderTreeManager(); // Instantiated here, player field set below
        bot.cHandler = new ContractHandler();
        bot.squealOfFortune = new SquealOfFortune(); // Instantiated here, player field set below
        bot.coOpSlayer = new CooperativeSlayer();
        bot.combatDefinitions = new CombatDefinitions();
        bot.prayer = new Prayer();
        bot.bank = new Bank();
        bot.controlerManager = new ControlerManager();
        bot.treasureTrails = new TreasureTrails();
        bot.farmingManager = new FarmingManager(); // Instantiated here, player field set below
        bot.musicsManager = new MusicsManager();
        bot.emotesManager = new EmotesManager();
        bot.ports = new PlayerOwnedPort();
        bot.xmas = new XmasEvent();
        bot.friendsIgnores = new FriendsIgnores();
        bot.petLootManager = new PetLootManager();
        bot.dominionTower = new DominionTower();
        bot.house = new House();
        bot.charges = new ChargesManager();
        bot.auraManager = new AuraManager();
        bot.questManager = new QuestManager();
        bot.petManager = new PetManager(); // Instantiated here, player field set below
        bot.geManager = new GrandExchangeManager();
        bot.perkManager = new PerkManager();
        bot.membership = new MembershipHandler();
        bot.titles = new Titles();

        // Notes & Familiar are instantiated in Player's constructor, but their access for linking caused issues.
        // We now use their getters to ensure they are available, then link if needed.
        // --- IMPORTANT: FIX FOR NOTES AND FAMILIAR ---
        // These fields are private in Player.java, so they must be accessed via getters.
        // Their instantiation is handled by Player's constructor, so we just call the getter to ensure it's linked/available.
        // If they have setPlayer() methods, we call them.
        bot.getNotes(); // Calls the getter, instantiates if null.
        // If Notes has setPlayer(Player p): bot.getNotes().setPlayer(bot); // Uncomment this if needed.

        bot.getFamiliar(); // Calls the getter, instantiates if null.
        // If Familiar has setPlayer(Player p): bot.getFamiliar().setPlayer(bot); // Uncomment this if needed.
        // But Familiar's constructor usually takes 'this', so setPlayer is often not needed.
        // --- END FIX FOR NOTES AND FAMILIAR ---


        // --- EXPLICITLY INITIALIZE MANAGERS THAT CAUSE NPEs OR ARE NOT SET BY PLAYER CONSTRUCTOR ---
        // This is due to Player(String, String) not setting 'packets', and Toolbelt constructor issues with 'BotPlayer'
        bot.setPackets(new com.rs.network.protocol.codec.encode.WorldPacketsEncoder(null, bot)); // Use setter for 'packets' field
        bot.toolBelt = new Toolbelt(bot); // Explicitly re-initialize toolBelt due to potential constructor issues with BotPlayer.
        bot.toolBeltNew = new ToolbeltNew(bot); // Explicitly re-initialize toolBeltNew similarly.
        // --- END EXPLICIT INITIALIZATIONS ---

        // Now, explicitly link managers to the Player object (bot) where their 'player' field is not set by constructor
        // or through their own constructor (like Familiar).
        // This is a comprehensive list based on Player.java.
        bot.getGlobalPlayerUpdater().setPlayer(bot);
        bot.getInventory().setPlayer(bot);
        bot.getEquipment().setPlayer(bot);
        bot.getSkills().setPlayer(bot);
        bot.getCombatDefinitions().setPlayer(bot);
        bot.getPrayer().setPlayer(bot);
        bot.getBank().setPlayer(bot);
        bot.getControlerManager().setPlayer(bot);
        bot.getMusicsManager().setPlayer(bot);
        bot.getEmotesManager().setPlayer(bot);
        bot.getFriendsIgnores().setPlayer(bot);
        bot.getDominionTower().setPlayer(bot);
        bot.getAuraManager().setPlayer(bot);
        bot.getQuestManager().setPlayer(bot);
        bot.getPetManager().setPlayer(bot);
        bot.getCharges().setPlayer(bot);

        // --- Explicit setPlayer CALLS for managers where it's needed (based on Player.java and NPEs) ---
        // These are managers that are instantiated (either directly or via getter),
        // and have an internal 'player' field that needs to be explicitly set.
        bot.getDayOfWeekManager().setPlayer(bot); // Fixes DayOfWeekManager NPE
        bot.getDailyTaskManager().setPlayer(bot); // Fixes DailyTaskManager NPE
        bot.getElderTreeManager().setPlayer(bot); // Processed in Player.processEntity()
        bot.getSquealOfFortune().setPlayer(bot); // Processed in Player.processEntity()
        bot.getFarmingManager().setPlayer(bot); // Processed in Player.processEntity()
        //bot.getResourceGather().setPlayer(bot); // Used by AutoThievingHandler
        //bot.getNotes().setPlayer(bot); // Correct way to link Notes

        // Managers from Player.java that were commented out before due to missing getters/setters:
        // Uncomment if you implement their getters/setters and need their functionality.
        // bot.getRouteFinderManager().setPlayer(bot);
        // bot.getSettingsManager().setPlayer(bot);
        // bot.getWeeklyTopRanking().setPlayer(bot);
        // bot.getLoyaltyManager().setPlayer(bot);
        // bot.getAchievementManager().setPlayer(bot);

        // --- END OF MANAGER LINKING ---


        bot.setDirection(Utils.getFaceDirection(0, -1));
        bot.setTemporaryMoveType(-1);
        bot.logicPackets = new ConcurrentLinkedQueue<LogicPacket>();
        bot.setSwitchItemCache(Collections.synchronizedList(new ArrayList<Integer>()));

        // 3. Final setup steps for Entity-level (not player-specific managers)
        bot.initEntity();

        // 4. Initialize the controller.
        bot.getControlerManager().login();

        // 5. Add to world and update region
        bot.unlock();
        World.addPlayer(bot); // Add the bot to the World list
        bot.setActive(true); // Mark the bot as active so WorldThread processes it


        // CRITICAL STEP: Schedule appearance generation and forced region update for ALL PLAYERS.
        WorldTasksManager.schedule(new WorldTask() {
            @Override
            public void run() {
                // Ensure the bot's current location is definitively set.
                bot.setLocation(desiredSpawnLocation);

                // Generate appearance data. This flags the bot for a visual update.
                bot.getGlobalPlayerUpdater().generateAppearenceData();

                // Update the bot's region on the server.
                World.updateEntityRegion(bot);

                // IMPORTANT: This line caused an NPE before. Keep it commented for now.
                // World.refreshPlayerAppearancesForEveryone(bot);

                System.out.println("Bot " + bot.getUsername() + " - Final location set, appearance generated, and region updated. Current location: " + bot.getX() + ", " + bot.getY() + ", " + bot.getPlane());
                stop(); // Run once
            }
        }, 2); // Schedule to run after 2 game ticks for stability


        // --- END OF INITIALIZATION ---

        // 6. Attach the AI brain
        bot.attachAIHandler(new BotAIHandler(bot));

        bots.put(formattedUsername, bot);
        Logger.log("BotManager", "Successfully created and added bot: " + username);

    }

	public static void removeBot(String username) {
		String formattedUsername = Utils.formatPlayerNameForProtocol(username);
		BotPlayer bot = bots.get(formattedUsername);
		if (bot != null) {
			bot.finish();
			bots.remove(formattedUsername);
			Logger.log("BotManager", "Successfully removed bot: " + username);
		} else {
			System.err.println("Could not find bot with name " + username + " to remove.");
		}
	}

	public static Map<String, BotPlayer> getBots() {
		return Collections.unmodifiableMap(bots);
	}
}