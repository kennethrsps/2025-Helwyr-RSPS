package com.rs.game.player;

import com.rs.game.player.actions.automation.AutoSkillingManager;
import com.rs.game.player.actions.automation.AutoSkillingManager.AutoSkillingState;
import com.rs.game.player.actions.automation.AutoSkillingManager.SkillingType;
import com.rs.game.player.actions.automation.AutoSkillingManager.InventoryAction;
import com.rs.game.player.actions.Woodcutting.TreeDefinitions;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import com.rs.network.Session; // Often needed for PlayerPackets constructor
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.Channel;

import com.discord.Discord;
//import com.rs.DiscordMessageHandler;
import com.rs.Settings;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.Projectile;
import com.rs.game.Region;
import com.rs.game.World;
import com.rs.game.World.PlayerPacketManager;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activites.BountyHunter;
import com.rs.game.activites.ZombieOutpost.TowerObject;
import com.rs.game.activites.ZombieOutpost.ZOControler;
import com.rs.game.activites.ZombieOutpost.ZOGame;
import com.rs.game.activites.clanwars.FfaZone;
import com.rs.game.activites.clanwars.WarControler;
import com.rs.game.activites.duel.DuelArena;
import com.rs.game.activites.duel.DuelRules;
import com.rs.game.activites.goldrush.GRManager;
import com.rs.game.activites.resourcegather.ResourceGatherBuff;
import com.rs.game.activities.instances.Instance;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.map.bossInstance.InstanceSettings;
import com.rs.game.npc.NPC;
import com.rs.game.npc.dragons.Wyvern;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.masuta.Masuta;
import com.rs.game.npc.pet.Pet;
import com.rs.game.npc.sundfreet.Sunfreet;
import com.rs.game.player.BanksManager.ExtraBank;
import com.rs.game.player.achievements.AchievementManager;
import com.rs.game.player.actions.ActionManager;
import com.rs.game.player.actions.Fishing;
import com.rs.game.player.actions.Fishing.FishingSpots;
import com.rs.game.player.actions.divination.DivineObject;
import com.rs.game.player.actions.mining.Mining.RockDefinitions;
import com.rs.game.player.bot.Bot;
import com.rs.game.player.combat.PlayerCombat;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossTimerManager;
import com.rs.game.player.content.ChatMessage;
import com.rs.game.player.content.CombatMastery;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.HintIconsManager;
import com.rs.game.player.content.InterfaceManager;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.LocalNPCUpdate;
import com.rs.game.player.content.LocalPlayerUpdate;
import com.rs.game.player.content.LogicPacket;
import com.rs.game.player.content.LoyaltyManager;
//import com.rs.game.player.content.HourlyBoxManager;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.MoneyPouch;
import com.rs.game.player.content.Pots;
import com.rs.game.player.content.PriceCheckManager;
import com.rs.game.player.content.PublicChatMessage;
import com.rs.game.player.content.QuickChatMessage;
//import com.rs.game.player.content.ReferralHandler;
import com.rs.game.player.content.RouteEvent;
import com.rs.game.player.content.SkillCapeCustomizer;
import com.rs.game.player.content.SlayerTask;
import com.rs.game.player.content.TeleportSystem.TeleportLocation;
import com.rs.game.player.content.TeleportSystem.Teleports;
import com.rs.game.player.content.Toolbelt;
import com.rs.game.player.content.ToolbeltNew;
import com.rs.game.player.content.Trade;
import com.rs.game.player.content.Trade.CloseTradeStage;
import com.rs.game.player.content.VarBitManager;
import com.rs.game.player.content.WeeklyTopRanking;
import com.rs.game.player.content.ancientthrone.ThroneManager;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.clans.content.perks.ClanPerk;
import com.rs.game.player.content.construction.House;
import com.rs.game.player.content.contracts.Contract;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.content.dailylogin.DailyLoginManager;
import com.rs.game.player.content.death.DeathManager;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.content.grandExchange.GrandExchangeManager;
import com.rs.game.player.content.interfaces.potionTimer.PotionTimers;
import com.rs.game.player.content.interfaces.potionTimer.PotionTimersInter;
import com.rs.game.player.content.items.Defenders;
import com.rs.game.player.content.items.PrayerBooks;
import com.rs.game.player.content.miscellania.ThroneOfMiscellania;
import com.rs.game.player.content.pet.PetManager;
import com.rs.game.player.content.slayer.CooperativeSlayer;
import com.rs.game.player.content.xmas.XmasEvent;
import com.rs.game.player.controllers.Controller;
import com.rs.game.player.controllers.CorpBeastController;
import com.rs.game.player.controllers.CrucibleController;
import com.rs.game.player.controllers.DTController;
import com.rs.game.player.controllers.DeathEvent;
import com.rs.game.player.controllers.DungeonController;
import com.rs.game.player.controllers.FightCaves;
import com.rs.game.player.controllers.FightKiln;
import com.rs.game.player.controllers.GodWars;
import com.rs.game.player.controllers.ImpossibleJad;/*
													import com.rs.game.player.controllers.InstancedPVPControler;*/
import com.rs.game.player.controllers.JailController;
import com.rs.game.player.controllers.NomadsRequiem;
import com.rs.game.player.controllers.QueenBlackDragonController;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.player.controllers.ZGDController;
import com.rs.game.player.controllers.castlewars.CastleWarsPlaying;
import com.rs.game.player.controllers.castlewars.CastleWarsWaiting;
import com.rs.game.player.controllers.fightpits.FightPitsArena;
import com.rs.game.player.controllers.pestcontrol.PestControlGame;
import com.rs.game.player.controllers.pestcontrol.PestControlLobby;
import com.rs.game.player.controllers.zombie.ZombieControler;
import com.rs.game.player.cutscenes.Cutscene;
import com.rs.game.player.cutscenes.CutscenesManager;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.dialogue.DialogueManager;
import com.rs.game.player.dialogue.impl.StarterTutorialD;
import com.rs.game.player.newquests.NewQuestManager;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.game.topweeks.TopWeeklyDonates;
import com.rs.network.Session;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;
import com.rs.network.protocol.codec.decode.impl.ButtonHandler;
import com.rs.network.protocol.codec.encode.BotWorldPacketsEncoder;
import com.rs.network.protocol.codec.encode.WorldPacketsEncoder;
import com.rs.utils.Colors;
import com.rs.utils.DonationRank;
import com.rs.utils.Donations;
import com.rs.utils.IsaacKeyPair;
import com.rs.utils.Lend;
import com.rs.utils.Logger;
import com.rs.utils.MachineInformation;
import com.rs.utils.PkRank;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;
/*import com.rs.utils.impl.Highscores;*/

import mysql.impl.Donation;
import mysql.impl.Highscores;
import mysql.impl.StoreManager;

public class Player extends Entity {

	private static final long serialVersionUID = 2011932556974180375L;

	public static final int TELE_MOVE_TYPE = 127, WALK_MOVE_TYPE = 1, RUN_MOVE_TYPE = 2;

	public com.rs.game.npc.Sotapana.Sotapana npcSotapana;
	public int npcSotapanaDmg;
	public static boolean isMainhand = false;
	private double autoSkillingStartXP = 0.0;

	public double getAutoSkillingStartXP() {
		return autoSkillingStartXP;
	}

	public void setAutoSkillingStartXP(double xp) {
		this.autoSkillingStartXP = xp;
	}

	public boolean isBot() {
		return isBot; // CHANGE THIS FROM 'return false;'
	}

	public void setBot(boolean isBot) {
		this.isBot = isBot;
	}

	public boolean isPlayer() {
	    return !isBot(); // Returns true if it's NOT a bot, implying it's a human player
	}
	// NEW METHOD: Added to provide a render distance for player updates
    public int getRenderDistance() {
        // You can adjust these values based on your client's actual view distance
        // 14 is a common default for standard view, 126 for large scene view
        return hasLargeSceneView() ? 126 : 14;
    }
    


	public transient ActionManager actionManager = new ActionManager(this);

	/**
	 * SKILLING EVENTS
	 */
	private ResourceGatherBuff resourceGather;
	public int lastSkillEventViewed;
	public int skillEventPoints = -1;
	public int skillEventPosition = -1;

	/**
	 * ZOMBIE OUTPOST
	 */
	public transient int ZOPoints;
	public transient int ZOTotalPoints;
	public transient int ZOKills;
	public transient TowerObject lastTowerViewed;

	public boolean togglePouchMessages;

	private AchievementManager achManager;
	private GRManager grManager;

	/* prestige */
	public int prestigePoints;
	public int[] prestigedSkills;

	public void prestige() {
		if (this.ironman || this.hcironman || this.isWiki()) {
			sm("You cannot prestige as an ironman.");
			return;
		}
		int total = 0;

		for (int i = 0; i < Skills.SKILL_NAME.length - 1; i++) {

			if (getSkills().getLevelForXp(i) < 99) {
				errorMessage("You need 99 " + Skills.SKILL_NAME[i] + " to prestige.");
				return;
			}
			total++;
			getSkills().setXp(i, 0);
			getSkills().set(i, getSkills().getLevelForXp(i));// test
			prestigePoints += prestigePoints();
			prestigedSkills[i]++;
		}
		World.sendWorldMessage("<img=7><col=ff0000>News:" + getDisplayName() + " has just prestiged " + total
				+ " skills on " + getXPMode() + "!", false);
		sm("Congratulations! You prestiged multiple skill(s). You now have a " + prestigePoints + " prestige points.");
	}

	public void prestige(int skill) {
		if (this.ironman || this.hcironman || this.isWiki()) {
			sm("You cannot prestige as an ironman.");
			getInterfaceManager().sendScreenInterface(317, 1218);
			getPackets().sendInterface(false, 1218, 1, 1217);
			return;
		}
		if (skill >= 0 && skill <= 6) {
			sm("In order to prestige combat stats, please refer to the NPC that allows you to prestige.");
			getInterfaceManager().sendScreenInterface(317, 1218);
			getPackets().sendInterface(false, 1218, 1, 1217);
			return;
		}
		if (getSkills().getXp(skill) < 13034431) {
			sm("You cannot prestige this skill yet! You need at least 13,034,431 xp in " + Skills.SKILL_NAME[skill]
					+ ".");
			getInterfaceManager().sendScreenInterface(317, 1218);
			getPackets().sendInterface(false, 1218, 1, 1217);
			return;
		}

		getDialogueManager().startDialogue(new Dialogue() {

			@Override
			public void start() {
				sendOptionsDialogue(
						"Are you sure you want to prestige " + Colors.red + Skills.SKILL_NAME[skill]
								+ "</col>?<br> This is a process that cannot be undoed after.",
						"Yes, I am aware that my " + Colors.red + Skills.SKILL_NAME[skill] + "</col> will reset",
						"No thanks.", "Open Skill Advance Guides");
			}

			@Override
			public void run(int interfaceId, int componentId) {
				switch (componentId) {
				case OPTION_1:
					// 13,034,431
					getSkills().setXp(skill, 0);
					getSkills().set(skill, getSkills().getLevelForXp(skill));// test
					prestigePoints += prestigePoints();
					prestigedSkills[skill]++;
					World.sendWorldMessage("<img=7><col=ff0000>News:" + getDisplayName() + " has just prestiged "
							+ Colors.green + Skills.SKILL_NAME[skill] + " on " + getXPMode()
							+ " Mode</col><col=ff0000> and is level " + prestigedSkills[skill] + "!", false);
					sm("Congratulations! You prestiged a skill. You now have a " + prestigePoints
							+ " prestige points.");
					end();
					break;
				case OPTION_2:
					end();
					break;

				case OPTION_3:
					end();
					player.getInterfaceManager().sendScreenInterface(317, 1218);
					player.getPackets().sendInterface(false, 1218, 1, 1217);
					break;

				}

			}

			@Override
			public void finish() {
				// TODO Auto-generated method stub

			}

		});
	}

	public void prestigeWarning(int skill) {
		getDialogueManager().startDialogue(new Dialogue() {

			@Override
			public void start() {
				sendOptionsDialogue(
						"<col=ff0000>This will reset your " + Skills.SKILL_NAME[skill].toLowerCase()
								+ " to level 1.<br>This is for prestige levels over 10.</col>",
						"Yes, reset my " + Skills.SKILL_NAME[skill].toLowerCase() + " to level 1", "Nevermind");
			}

			@Override
			public void run(int interfaceId, int componentId) {
				switch (componentId) {
				case OPTION_1:
					// 13,034,431
					getSkills().setXp(skill, 0);
					getSkills().set(skill, 1);// test
					prestigePoints += prestigePoints();
					prestigedSkills[skill]++;
					World.sendWorldMessage("<img=7><col=ff0000>News:" + getDisplayName() + " has just prestiged "
							+ Colors.green + Skills.SKILL_NAME[skill] + " on " + getXPMode()
							+ " Mode</col><col=ff0000> and is level " + prestigedSkills[skill] + "!", false);
					sm("Congratulations! You prestiged a skill. You now have a " + prestigePoints
							+ " prestige points.");
					end();
					break;
				case OPTION_2:
					end();
					break;

				}

			}

			@Override
			public void finish() {
				// TODO Auto-generated method stub

			}

		}, skill);
	}

	public int prestigePoints() {
		if (veteran)
			return 4;
		else if (easy)
			return 1;
		else if (intermediate)
			return 2;
		else if (expert)
			return 20;
		return 0;
	}

	// hween
	public boolean Hween;
	public boolean saloon;
	public boolean DrunkenSailor;
	private XPSharing xpSharing;
	private transient Channel channel;
	public transient LoyaltyManager loyaltyManager;
	// public transient HourlyBoxManager HourlyBoxManager;
	private boolean agrithNaNa;
	private boolean allowChatEffects;
	private GlobalPlayerUpdater globalPlayerUpdater;
	public AuraManager auraManager = new AuraManager();
	public Bank bank;
	private int barrowsKillCount;
	private int barrowsRunsDone;
	private transient long boneDelay;
	private transient boolean canPvp;
	private transient boolean cantTrade;
	// damage dummy
	public int damagepoints = 0;
	// points system
	public int TentMulti = 0;
	public boolean hasOpenedTentShop;
	public boolean hasOpenedTentShop2;
	/**
	 * boss timers
	 */
	public long[] FastestTime;
	private transient BossTimerManager bossTimerManager;
	// newpkstuff
	public int highestKillStreak, killStreak, killStreakPoints, totalkillStreakPoints;
	public int highestKill; // what windows is this? home ? i think so i forgot XD
	/**
	 * teleport system
	 */
	public TeleportLocation lastTeleport;
	public List<TeleportLocation> favorite_teleport = new ArrayList();
	public Teleports clickTeleport;

	// Ip Lock
	public boolean iplocked; // Is player using iplock
	public String lockedwith; // which ip is account locked to

	private transient boolean castedVeng;

	public ChargesManager charges;

	private int clanStatus;

	public transient boolean clientLoadedMapRegion;

	private transient Runnable closeInterfacesEvent;
	public CombatDefinitions combatDefinitions;
	// completionistcape reqs
	private boolean completedFightCaves;
	private boolean completedFightKiln;
	private boolean completedRfd;
	private int[] completionistCapeCustomized;
	public ControlerManager controlerManager;
	private long creationDate;
	private int crucibleHighScore;
	private boolean culinaromancer;
	public transient FriendChatsManager currentFriendChat;
	private transient ClansManager clanManager, guestClanManager;
	private int coal;
	public String currentFriendChatOwner;
	private String clanName;
	private boolean connectedClanChannel;
	public transient CutscenesManager cutscenesManager = new CutscenesManager(this);
	private boolean dessourt;
	public transient DialogueManager dialogueManager = new DialogueManager(this);
	private transient boolean disableEquip;
	public transient int displayMode;
	public String displayName;
	public DominionTower dominionTower = new DominionTower();
	private boolean donator;
	private long donatorTill;
	private DuelArena duelarena;
	public EmotesManager emotesManager;
	public TreasureTrails treasureTrails;
	public BountyHunter bountHunter;
	public Equipment equipment;
	private boolean extremeDonator;

	private long extremeDonatorTill;
	public int[] fairyRingCombination = new int[3];
	public Familiar familiar;
	private boolean filterGame;
	private transient boolean finishing;
	private long fireImmune;
	private long superAntiFire;
	private boolean flamBeed;
	private transient long foodDelay;
	private boolean forceNextMapLoadRefresh;
	private int friendChatSetup;
	public FriendsIgnores friendsIgnores;
	private int hiddenBrother;

	private boolean hideWorldAnnouncements;

	public transient HintIconsManager hintIconsManager = new HintIconsManager(this);
	private transient double hpBoostMultiplier;
	private boolean inAnimationRoom;
	public transient InterfaceManager interfaceManager = new InterfaceManager(this);
	public Inventory inventory;
	private transient boolean invulnerable;
	public transient IsaacKeyPair isaacKeyPair;
	private boolean isInDefenderRoom;
	private long jailed;
	private boolean karamel;
	// objects
	private boolean khalphiteLairEntranceSetted;
	private boolean khalphiteLairSetted;
	// honor
	private int killCount, deathCount;
	// barrows
	private boolean[] killedBarrowBrothers;
	private boolean killedBork;
	private boolean killedQueenBlackDragon;
	private transient boolean largeSceneView;
	private int lastBonfire;
	private transient DuelRules duelRules;
	private String lastIP;
	@SuppressWarnings("unused")
	private String lastKillIP;
	private long lastLoggedIn;
	private transient long lastPublicMessage;

	public transient LocalNPCUpdate localNPCUpdate;
	// used for update
	public transient LocalPlayerUpdate localPlayerUpdate = new LocalPlayerUpdate(this);
	private transient long lockDelay; // used for doors and stuff like that
	// used for packets logic
	public transient ConcurrentLinkedQueue<LogicPacket> logicPackets;

	private transient boolean toogleLootShare;

	// skill capes customizing
	private int[] maxedCapeCustomized;
	public int money;
	private boolean mouseButtons;
	public MusicsManager musicsManager;

	public Notes notesL;

	private int overloadDelay;
	private List<String> ownedObjectsManagerKeys;
	private transient long packetsDecoderPing;
	// saving stuff
	private String password;
	private String purePassword;
	private boolean permBanned;

	private boolean permMuted;
	private int pestControlGames;

	private int pestPoints;
	public transient Pet pet;
	public PetManager petManager = new PetManager();
	private int pkPoints;
	private int pkPointReward;
	private int pointsHad;
	private long poisonImmune;
	public transient long polDelay;
	public transient long bloodDelay;
	public boolean defenderPassive;
	private transient long potDelay;
	public MoneyPouch pouch = new MoneyPouch(this);
	private transient long lunarDelay;
	private int[] pouches;
	public Prayer prayer;
	private int prayerRenewalDelay;

	public transient PriceCheckManager priceCheckManager = new PriceCheckManager(this);

	private int privateChatSetup;

	// game bar status
	private int publicStatus;

	public QuestManager questManager;

	private String registeredMac, currentMac;
	private boolean reportOption;
	private transient boolean resting;
	private int rights;
	private byte runEnergy;
	private int runeSpanPoints;
	private transient boolean running;

	public transient Session session;

	public Skills skills;

	public StoreManager store;

	private int skullDelay;

	private int skullId;

	private int slayerPoints;
	private int slayerPoints2;

	private transient boolean spawnsMode;

	private int specRestoreTimer;

	private int spins;

	public int infusedPouches;

	// player stages
	private transient boolean active;

	private int summoningLeftClickOption;

	private transient List<Integer> switchItemCache;

	private boolean talkedtoCook;

	// Slayer
	private SlayerTask task;

	public Contract Rtask;

	public int tasksCompleted;

	private boolean talkedWithVannaka, talkedWithMarv;

	private int taskStreak;

	public void setTaskStreak(int amount) {
		this.taskStreak = amount;
	}

	public int getTaskStreak() {
		return taskStreak;
	}

	// reaper
	public ContractHandler cHandler;
	private Contract cContracts;

	private int temporaryMovementType;

	public transient Trade trade = new Trade(this);

	private int tradeStatus;
	private boolean updateMovementType;

	public int usedMacs;
	private int gravestone;

	// transient stuff
	public transient String username;
	private transient long yellDelay;

	private int vecnaTimer;

	private int votePoints;
	private int HweenPoints;
	private int TuskenPoints;
	private int ElitePoints;
	private int StarfirePoints;
	private boolean wonFightPits;

	private boolean xpLocked;

	private String yellColor = "ff0000";

	private boolean yellDisabled;

	private boolean yellOff;

	private long muted;

	private long banned;

	public boolean xmasTitle1;
	public boolean xmasTitle2;
	public boolean xmasTitle3;
	public boolean xmasTitle4;

	ThroneOfMiscellania throne;

	/**
	 * Legendary Pets Data
	 */

	/* Consitution Pet */
	private long petLastPreventedDeath;
	private long petLastHealCd;

	private boolean logedIn;

	/**
	 * @return
	 */
	public boolean hasXmasTitleUnlocked() {
		return (xmasTitle1 || xmasTitle2 || xmasTitle3 || xmasTitle4);
	}
	
	// Add this method to your Player.java class (the complete safe version)

	public void fullyInitializeForBot() {
	    try {
	        System.out.println("Starting bot initialization for: " + getUsername());
	        
	        // Initializes core variables from the Entity class
	        initEntity(); 
	        
	        // --- Initialize all Player-specific managers with error handling ---
	        try {
	            packets = new BotWorldPacketsEncoder(this);
	        } catch (Exception e) {
	            System.err.println("Error initializing packets encoder: " + e.getMessage());
	        }
	        
	        try {
	            actionManager = new ActionManager(this);
	            dialogueManager = new DialogueManager(this);
	            cutscenesManager = new CutscenesManager(this);
	            interfaceManager = new InterfaceManager(this);
	            hintIconsManager = new HintIconsManager(this);
	            priceCheckManager = new PriceCheckManager(this);
	            localPlayerUpdate = new LocalPlayerUpdate(this);
	            localNPCUpdate = new LocalNPCUpdate(this);
	            pouch = new MoneyPouch(this);
	            charges = new ChargesManager();
	            auraManager = new AuraManager();
	            questManager = new QuestManager();
	            petManager = new PetManager();
	            notesL = new Notes();
	            skills = new Skills();
	            inventory = new Inventory();
	            equipment = new Equipment();
	            bank = new Bank();
	            prayer = new Prayer();
	            combatDefinitions = new CombatDefinitions();
	            friendsIgnores = new FriendsIgnores();
	            musicsManager = new MusicsManager();
	            emotesManager = new EmotesManager();
	            dominionTower = new DominionTower();
	            house = new House();
	            controlerManager = new ControlerManager();
	            geManager = new GrandExchangeManager();
	            slayerManager = new SlayerManager();
	            farmingManager = new FarmingManager();
	        } catch (Exception e) {
	            System.err.println("Error initializing basic managers: " + e.getMessage());
	        }
	        
	        // CRITICAL: Initialize toolbelts safely - these cause the NPE
	        try {
	            // Don't initialize toolbelts for bots - they use the safe override
	            toolBelt = null; // Will be handled by getToolBelt() override
	            toolBeltNew = null; // Will be handled safely
	        } catch (Exception e) {
	            System.err.println("Toolbelt initialization skipped for bot");
	        }
	        
	        try {
	            dungManager = new DungManager();
	            dayOfWeekManager = new DayOfWeekManager();
	            dailyTaskManager = new DailyTaskManager();
	            banksManager = new BanksManager();
	            newQuestManager = new NewQuestManager();
	            gearPresets = new GearPresets();
	            bountyHunter = new BountyHunter();
	            elderTreeManager = new ElderTreeManager();
	            cHandler = new ContractHandler();
	            squealOfFortune = new SquealOfFortune();
	            coOpSlayer = new CooperativeSlayer();
	            treasureTrails = new TreasureTrails();
	            ports = new PlayerOwnedPort();
	            xmas = new XmasEvent();
	            petLootManager = new PetLootManager();
	            perkManager = new PerkManager();
	            membership = new MembershipHandler();
	            titles = new Titles();
	            throne = new ThroneOfMiscellania();
	            setDeathManager(new DeathManager());
	            setBossTimerManager(new BossTimerManager(this));
	            setAchManager(new AchievementManager(this));
	            setGrManager(new GRManager(this));
	            setResourceGather(new ResourceGatherBuff(this));
	            setLoginManager(new DailyLoginManager());
	            setPotionTimers(new PotionTimers(this));
	            setGlobalPlayerUpdater(new GlobalPlayerUpdater());
	        } catch (Exception e) {
	            System.err.println("Error initializing advanced managers: " + e.getMessage());
	        }

	        // --- Link all managers back to the player object with error handling ---
	        try {
	            if (getGlobalPlayerUpdater() != null) getGlobalPlayerUpdater().setPlayer(this);
	            if (getInventory() != null) getInventory().setPlayer(this);
	            if (getEquipment() != null) getEquipment().setPlayer(this);
	            if (getSkills() != null) getSkills().setPlayer(this);
	            if (getCombatDefinitions() != null) getCombatDefinitions().setPlayer(this);
	            if (getPrayer() != null) getPrayer().setPlayer(this);
	            if (getBank() != null) getBank().setPlayer(this);
	            if (getControlerManager() != null) getControlerManager().setPlayer(this);
	            if (getMusicsManager() != null) getMusicsManager().setPlayer(this);
	            if (getEmotesManager() != null) getEmotesManager().setPlayer(this);
	            if (getFriendsIgnores() != null) getFriendsIgnores().setPlayer(this);
	            if (getDominionTower() != null) getDominionTower().setPlayer(this);
	            if (getAuraManager() != null) getAuraManager().setPlayer(this);
	            if (getQuestManager() != null) getQuestManager().setPlayer(this);
	            if (getPetManager() != null) getPetManager().setPlayer(this);
	            if (getCharges() != null) getCharges().setPlayer(this);
	            if (getFarmingManager() != null) getFarmingManager().setPlayer(this);
	            if (getDayOfWeekManager() != null) getDayOfWeekManager().setPlayer(this);
	            if (getDailyTaskManager() != null) getDailyTaskManager().setPlayer(this);
	            if (getSquealOfFortune() != null) getSquealOfFortune().setPlayer(this);
	            if (getNotes() != null) getNotes().setPlayer(this);
	            if (getDeathManager() != null) getDeathManager().setPlayer(this);
	            if (getBountyHunter() != null) getBountyHunter().setPlayer(this);
	            if (getThrone() != null) getThrone().setPlayer(this);
	            
	            // SKIP toolbelt linking - handled by override
	            
	            if (getDungManager() != null) getDungManager().setPlayer(this);
	            if (getHouse() != null) getHouse().setPlayer(this);
	            if (getGearPresets() != null) getGearPresets().setPlayer(this);
	            if (getBanksManager() != null) getBanksManager().setPlayer(this);
	            if (getSlayerManager() != null) getSlayerManager().setPlayer(this);
	            if (getPetLootManager() != null) getPetLootManager().setPlayer(this);
	            if (getElderTreeManager() != null) getElderTreeManager().setPlayer(this);
	            if (getTitles() != null) getTitles().setPlayer(this);
	            if (getNewQuestManager() != null) getNewQuestManager().setPlayer(this);
	            if (getOverrides() != null) getOverrides().setPlayer(this);
	            if (getAnimations() != null) getAnimations().setPlayer(this);
	            if (getGEManager() != null) getGEManager().setPlayer(this);
	            if (getPorts() != null) getPorts().setPlayer(this);
	            if (getXmas() != null) getXmas().setPlayer(this);
	            if (getTreasureTrails() != null) getTreasureTrails().setPlayer(this);
	            if (getPerkManager() != null) getPerkManager().setPlayer(this);
	        } catch (Exception e) {
	            System.err.println("Error linking managers: " + e.getMessage());
	        }

	        // --- Set default states ---
	        try {
	            setDirection(Utils.getFaceDirection(0, -1));
	            setTemporaryMoveType(-1);
	            logicPackets = new ConcurrentLinkedQueue<LogicPacket>();
	            setSwitchItemCache(Collections.synchronizedList(new ArrayList<Integer>()));
	        } catch (Exception e) {
	            System.err.println("Error setting default states: " + e.getMessage());
	        }
	        
	        System.out.println("Bot " + getUsername() + " fully initialized successfully");
	        
	    } catch (Exception e) {
	        System.err.println("CRITICAL error in fullyInitializeForBot: " + e.getMessage());
	        e.printStackTrace();
	    }
	}

	// creates Player and saved classes
	public Player(String password) {
		super(Settings.START_PLAYER_LOCATION);
		setHitpoints(Settings.START_PLAYER_HITPOINTS);
		this.password = password;
		setGlobalPlayerUpdater(new GlobalPlayerUpdater());
		inventory = new Inventory();
		dungManager = new DungManager();
		dayOfWeekManager = new DayOfWeekManager();
		dailyTaskManager = new DailyTaskManager();
		banksManager = new BanksManager();
		setBossTimerManager(new BossTimerManager(this));
		newQuestManager = new NewQuestManager();
		slayerManager = new SlayerManager();
		ArtisansWorkShopSupplies = new int[5];
		gearPresets = new GearPresets();
		equipment = new Equipment();
		skills = new Skills();
		bountyHunter = new BountyHunter();
		elderTreeManager = new ElderTreeManager();
		cHandler = new ContractHandler();
		squealOfFortune = new SquealOfFortune();
		coOpSlayer = new CooperativeSlayer();
		combatDefinitions = new CombatDefinitions();
		prayer = new Prayer();
		bank = new Bank();
		overrides = new CosmeticOverrides();
		mauledWeeksNM = new boolean[6];
		mauledWeeksHM = new boolean[6];
		unlockedCostumesIds = new ArrayList<Integer>();
		animations = new AnimationOverrides();
		controlerManager = new ControlerManager();
		treasureTrails = new TreasureTrails();
		prayerBook = new boolean[PrayerBooks.BOOKS.length];
		farmingManager = new FarmingManager();
		musicsManager = new MusicsManager();
		emotesManager = new EmotesManager();
		ports = new PlayerOwnedPort();
		xmas = new XmasEvent();
		FastestTime = new long[40];
		friendsIgnores = new FriendsIgnores();
		petLootManager = new PetLootManager();
		dominionTower = new DominionTower();
		house = new House();
		charges = new ChargesManager();
		auraManager = new AuraManager();
		questManager = new QuestManager();
		petManager = new PetManager();
		geManager = new GrandExchangeManager();
		toolBelt = new Toolbelt(this);
		toolBeltNew = new ToolbeltNew(this);
		perkManager = new PerkManager();
		membership = new MembershipHandler();
		nonPermaLootersPerks = new ArrayList<String>();
		nonPermaSkillersPerks = new ArrayList<String>();
		nonPermaUtilityPerks = new ArrayList<String>();
		nonPermaCombatantPerks = new ArrayList<String>();
		titles = new Titles();
		runEnergy = 100;
		allowChatEffects = true;
		profanityFilter = true;
		mouseButtons = true;
		pouches = new int[4];
		warriorPoints = new double[6];
		resetBarrows();
		killStats = new int[512];
		boons = new boolean[12];
		SkillCapeCustomizer.resetSkillCapes(this);
		ownedObjectsManagerKeys = new LinkedList<String>();
		setCreationDate(Utils.currentTimeMillis());
		currentFriendChatOwner = "Zeus";
		this.isBot = false;
        this.hasCompleted = false; // Default for normal players
        this.logedIn = false;
	}
	 public boolean isLogedIn() {
	        return logedIn;
	    }

	   

	public Player() {
		this("bot_password");
	}

	public void addFoodDelay(long time) {
		foodDelay = time + Utils.currentTimeMillis();
	}

	public void addLogicPacketToQueue(LogicPacket packet) {
		for (LogicPacket p : logicPackets) {
			if (p.getId() == packet.getId()) {
				logicPackets.remove(p);
				break;
			}
		}
		logicPackets.add(packet);
	}

	public void addPoisonImmune(long time) {
		poisonImmune = time + Utils.currentTimeMillis();
		getPoison().reset();
		potionTimer.slotTimerArray[PotionTimersInter.ANTIPOISON] = poisonImmune;
	}

	public void addPolDelay(long delay) {
		polDelay = delay + Utils.currentTimeMillis();
	}

	public void addBloodDelay(long time) {
		bloodDelay = time + Utils.currentTimeMillis();
	}

	public int getdamagepoints() {
		return damagepoints;
	}

	public void setdamagepoints(int damagepoints) {
		this.damagepoints = damagepoints;
	}

	public void addPotDelay(long time) {
		potDelay = time + Utils.currentTimeMillis();
	}

	/**
	 * Adds points
	 * 
	 * @param points
	 */
	public void addRunespanPoints(int points) {
		this.runeSpanPoints += points;
	}

	public boolean canSpawn() {
		if (Wilderness.isAtWild(this) || getControlerManager().getControler() instanceof FightPitsArena
				|| getControlerManager().getControler() instanceof CorpBeastController
				|| getControlerManager().getControler() instanceof PestControlLobby
				|| getControlerManager().getControler() instanceof PestControlGame
				|| getControlerManager().getControler() instanceof ZGDController
				|| getControlerManager().getControler() instanceof DungeonController
				|| getControlerManager().getControler() instanceof GodWars
				|| getControlerManager().getControler() instanceof JailController
				|| getControlerManager().getControler() instanceof DTController
				|| getControlerManager().getControler() instanceof WarControler
				|| getControlerManager().getControler() instanceof DeathEvent
				|| getControlerManager().getControler() instanceof DuelArena
				|| getControlerManager().getControler() instanceof CastleWarsPlaying
				|| getControlerManager().getControler() instanceof CastleWarsWaiting
				|| getControlerManager().getControler() instanceof FightCaves
				|| getControlerManager().getControler() instanceof FightKiln
				|| getControlerManager().getControler() instanceof ImpossibleJad
				|| getControlerManager().getControler() instanceof NomadsRequiem
				|| getControlerManager().getControler() instanceof QueenBlackDragonController
				|| getControlerManager().getControler() instanceof ZombieControler || dungManager.isInside()
				|| World.isAtAscensionDungeon(this) || /* South West */(getX() >= 2955 && getY() >= 1735 && // kalphite
				// king
				// lair
				/* North East */getX() <= 2997 && getY() <= 1783)

				|| /* South West */(getX() >= 3009 && getY() >= 5955 && // vorago
				// borehole
				/* North East */getX() <= 3135 && getY() <= 6136))
			return false;

		if (getControlerManager().getControler() instanceof CrucibleController) {
			CrucibleController controler = (CrucibleController) getControlerManager().getControler();
			return !controler.isInside();
		}
		return true;
	}

	public void checkMovement(int x, int y, int plane) {
		Magic.teleControlersCheck(this, new WorldTile(x, y, plane));
	}

	@Override
	public void checkMultiArea() {
		if (!isActive())
			return;
		boolean isAtMultiArea = isForceMultiArea() ? true : World.isMultiArea(this);
		if (isAtMultiArea && !isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 1);
		} else if (!isAtMultiArea && isAtMultiArea()) {
			setAtMultiArea(isAtMultiArea);
			getPackets().sendGlobalConfig(616, 0);
		}
	}

	public boolean clientHasLoadedMapRegion() {
		return clientLoadedMapRegion;
	}

	/**
	 * Closes all on-screen interfaces.
	 */
	public void closeInterfaces() {
		if (interfaceManager.containsScreenInter())
			interfaceManager.closeScreenInterface();
		if (interfaceManager.containsInventoryInter())
			interfaceManager.closeInventoryInterface();
		dialogueManager.finishDialogue();
		if (closeInterfacesEvent != null) {
			closeInterfacesEvent.run();
			closeInterfacesEvent = null;
		}
		getInterfaceManager().closeChatBoxInterface();
		getInterfaceManager().closeFadingInterface();
		// getInterfaceManager().closeOverlay(getInterfaceManager().isResizableScreen()
		// ? false : true);
		// getInterfaceManager().sendWindowPane();
	}

	public static void fade(final Player player) {
		final long time = FadingScreen.fade(player);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				try {
					FadingScreen.unfade(player, time, new Runnable() {
						@Override
						public void run() {
							player.lock(0);
						}
					});
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1);
	}

	public void drainRunEnergy() {
		if (dungManager.isInside())
			return;

		// Only drain if actually running and moving
		if (!getRun() || getNextRunDirection() == -1)
			return;

		if (!getPerkManager().staminaBoost) {
			runEnergy--; // Fixed: decrement directly instead of setRunEnergy
			if (runEnergy < 0)
				runEnergy = 0;
			getPackets().sendRunEnergy();
		}
	}

	@Override
	public void finish() {
		finish(0);
	}

	public void finish(final int tryCount) {
		if (finishing || hasFinished())
			return;
		finishing = true;
		stopAll(false, true, !(actionManager.getAction() instanceof PlayerCombat));
		if (isUnderCombat(tryCount) || getEmotesManager().isDoingEmote() || isLocked() || isDead()) {
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {

					try {
						packetsDecoderPing = Utils.currentTimeMillis();
						finishing = false;
						finish(tryCount + 1);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, 10, TimeUnit.SECONDS);

			return;
		}
		realFinish();
	}

	public String lowestShip() {
		String finale = "";
		int minX = Integer.MAX_VALUE;
		int[] ships = { getPorts().getFirstVoyageMinsLeft(), getPorts().getSecondVoyageMinsLeft(),
				getPorts().getThirdVoyageMinsLeft(), getPorts().getFourthVoyageMinsLeft(),
				getPorts().getFifthVoyageMinsLeft() };
		String[] names = { "first", "second", "third", "fourth", "fifth" };
		for (int i = 0; i < ships.length; i++) {
			if (ships[i] > 0 && ships[i] < minX)
				minX = ships[i];
		}
		for (int i = 0; i < ships.length; i++) {
			if (minX == ships[i])
				finale += names[i] + " ship has " + minX + " minutes left!";
		}
		return finale;
	}

	public void checkPorts() {
		if (!getPorts().hasFirstShip || getPorts().firstShipVoyage == 0 && getPorts().secondShipVoyage == 0
				&& getPorts().thirdShipVoyage == 0 && getPorts().fourthShipVoyage == 0
				&& getPorts().fifthShipVoyage == 0)
			return;
		if (getPorts().hasFirstShipReturned() && getPorts().firstShipVoyage != 0
				|| getPorts().hasSecondShipReturned() && getPorts().secondShipVoyage != 0
				|| getPorts().hasThirdShipReturned() && getPorts().thirdShipVoyage != 0
				|| getPorts().hasFourthShipReturned() && getPorts().fourthShipVoyage != 0
				|| getPorts().hasFifthShipReturned() && getPorts().fifthShipVoyage != 0)
			sendMessage("<img=7>" + Colors.red + "[" + getDisplayName() + "'s Port]:" + Colors.green
					+ " One or more of your ships have returned!", false);
		else
			sendMessage("<img=7>" + Colors.red + "[" + getDisplayName() + "'s Port]:" + Colors.orange + " Your "
					+ lowestShip());
	}

	public int bloodNeckHeal(int cap, int heal) {
		if ((heal + cap) > 350)
			return 350;
		heal(heal);
		cap += heal;
		return cap;
	}

	private long lastVoteClaim;

	public long getLastVoteClaim() {
		return lastVoteClaim;
	}

	public void setLastVoteClaim(long time) {
		this.lastVoteClaim = time;
	}

	public boolean isUnderCombat(int tryCount) {
		return (getAttackedByDelay() + 10000 > Utils.currentTimeMillis());
	}

	public void forceLogout() {
		getPackets().sendLogout();
		setRunning(false);
		realFinish();
	}

	public void forceSession() {
		setRunning(false);
		realFinish();
	}

	public ActionManager getActionManager() {
		return actionManager;
	}

	public AuraManager getAuraManager() {
		return auraManager;
	}

	public Bank getBank() {
		return bank;
	}

	public long getBanned() {
		return banned;
	}

	public int getBarrowsKillCount() {
		return barrowsKillCount;
	}

	public int getBarrowsRunsDone() {
		return barrowsRunsDone;
	}

	public long getBoneDelay() {
		return boneDelay;
	}

	public ChargesManager getCharges() {
		return charges;
	}

	public int getClanStatus() {
		return clanStatus;
	}

	public CombatDefinitions getCombatDefinitions() {
		return combatDefinitions;
	}

	public int[] getCompletionistCapeCustomized() {
		return completionistCapeCustomized;
	}

	public int getTentMulti() {
		return TentMulti;
	}

	public void setTentMulti(int TentMulti) {
		this.TentMulti = TentMulti;
	}

	public ControlerManager getControlerManager() {
		return controlerManager;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public int getCrucibleHighScore() {
		return crucibleHighScore;
	}

	public FriendChatsManager getCurrentFriendChat() {
		return currentFriendChat;
	}

	public String getCurrentFriendChatOwner() {
		return currentFriendChatOwner;
	}

	public String getCurrentMac() {
		return currentMac;
	}

	public CutscenesManager getCutscenesManager() {
		return cutscenesManager;
	}

	public int getDeathCount() {
		return deathCount;
	}

	public DialogueManager getDialogueManager() {
		return dialogueManager;
	}

	public int getDisplayMode() {
		return displayMode;
	}

	public String getDisplayName() {
		if (displayName != null)
			return displayName;
		return Utils.formatString(username);
	}

	public DominionTower getDominionTower() {
		return dominionTower;
	}

	@SuppressWarnings("deprecation")
	public String getDonatorTill() {
		return (donator ? "never" : new Date(donatorTill).toGMTString()) + ".";
	}

	public DuelArena getDuelArena() {
		return duelarena;
	}

	public EmotesManager getEmotesManager() {
		return emotesManager;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	@SuppressWarnings("deprecation")
	public String getExtremeDonatorTill() {
		return (extremeDonator ? "never" : new Date(extremeDonatorTill).toGMTString()) + ".";
	}

	public Familiar getFamiliar() {
		return familiar;
	}

	public long getFireImmune() {
		return fireImmune;
	}

	public long getFoodDelay() {
		return foodDelay;
	}

	public FriendsIgnores getFriendsIgnores() {
		return friendsIgnores;
	}

	public int getHiddenBrother() {
		return hiddenBrother;
	}

	public HintIconsManager getHintIconsManager() {
		return hintIconsManager;
	}

	public double getHpBoostMultiplier() {
		return hpBoostMultiplier;
	}

	public InterfaceManager getInterfaceManager() {
		return interfaceManager;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public IsaacKeyPair getIsaacKeyPair() {
		return isaacKeyPair;
	}

	public long getJailed() {
		return jailed;
	}

	public int getKillCount() {
		return killCount;
	}

	public int getKillStreak() {
		return killStreak;
	}

	public int getKillStreakPoints() {
		return killStreakPoints;
	}

	public int getTotalKillStreakPoints() {
		return totalkillStreakPoints;
	}

	public boolean[] getKilledBarrowBrothers() {
		return killedBarrowBrothers;
	}

	public int getLastBonfire() {
		return lastBonfire;
	}

	public String getLastHostname() {
		InetAddress addr;
		try {
			addr = InetAddress.getByName(getLastIP());
			String hostname = addr.getHostName();
			return hostname;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getLastIP() {
		return lastIP;
	}

	public long getLastLoggedIn() {
		return lastLoggedIn;
	}

	public long getLastPublicMessage() {
		return lastPublicMessage;
	}

	public LocalNPCUpdate getLocalNPCUpdate() {
		return localNPCUpdate;
	}

	public LocalPlayerUpdate getLocalPlayerUpdate() {
		return localPlayerUpdate;
	}

	public long getLockDelay() {
		return lockDelay;
	}

	@Override
	public double getMagePrayerMultiplier() {
		return 0.6;
	}

	public int[] getMaxedCapeCustomized() {
		return maxedCapeCustomized;
	}

	@Override
	public int getMaxHitpoints() {
		ClansManager manager = getClanManager();
		if (!(manager == null)) {
			if (getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.TIER_3_THICK_SKIN)) {
				return skills.getLevel(Skills.HITPOINTS) * 17 + equipment.getEquipmentHpIncrease();
			}
			if (getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.TIER_2_THICK_SKIN)) {
				return skills.getLevel(Skills.HITPOINTS) * 14 + equipment.getEquipmentHpIncrease();
			}
			if (getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.THICK_SKIN)) {
				return skills.getLevel(Skills.HITPOINTS) * 12 + equipment.getEquipmentHpIncrease();
			}
		}
		return skills.getLevel(Skills.HITPOINTS) * 10 + equipment.getEquipmentHpIncrease();
	}

	@Override
	public double getMeleePrayerMultiplier() {
		return 0.6;
	}

	public int getMessageIcon() {
		return isSupport() ? 13
				: isForumManager() ? 21
						: isCommunityManager() ? 22
								: isDicer() ? 20
										: isMod() ? 1
												: isWiki() ? 23
														: isYoutube() ? 16
																: isDev() ? 2
																		: isOwner() ? 2
																				: isSponsor() && !isIronMan()
																						&& !isHCIronMan()
																								? 18
																								: isDiamond()
																										& !isIronMan()
																										&& !isHCIronMan()
																												? 19
																												: isPlatinum()
																														&& !isIronMan()
																														&& !isHCIronMan()
																																? 12
																																: isGold()
																																		&& !isIronMan()
																																		&& !isHCIronMan()
																																				? 8
																																				: isSilver()
																																						&& !isIronMan()
																																						&& !isHCIronMan()
																																								? 10
																																								: isBronze()
																																										&& !isIronMan()
																																										&& !isHCIronMan()
																																												? 9
																																												: isHCIronMan()
																																														? 15
																																														: isIronMan()
																																																? 14
																																																: -1;
	}

	/**
	 * Checks if @this is a staff member.
	 * 
	 * @return if Staff.
	 */
	public boolean isHeadStaff() {
		return isOwner() || isDev();
	}

	public boolean isStaff() {
		return isOwner() || isDev() || getRights() == 2 || isAdmin();
	}

	public boolean isStaff2() {
		return isStaff() || isSupport() || isMod() || isForumManager() || isCommunityManager();
	}

	public boolean isStaff3() {
		return isStaff2() || isYoutube();
	}

	public MoneyPouch getMoneyPouch() {
		return pouch;
	}

	public int getMoneyPouchValue() {
		return money;
	}

	public int getMovementType() {
		if (getTemporaryMoveType() != -1)
			return getTemporaryMoveType();
		return getRun() ? RUN_MOVE_TYPE : WALK_MOVE_TYPE;
	}

	public MusicsManager getMusicsManager() {
		return musicsManager;
	}

	public long getMuted() {
		return muted;
	}

	public Notes getNotes() {
		return notesL;
	}

	public int getOverloadDelay() {
		return overloadDelay;
	}

	public List<String> getOwnedObjectManagerKeys() {
		if (ownedObjectsManagerKeys == null) // temporary
			ownedObjectsManagerKeys = new LinkedList<String>();
		return ownedObjectsManagerKeys;
	}

	// In Player.java, replace the getPackets() method with this

	// This is the correct version for Player.java

	// In Player.java, this is the final, correct version.

	public WorldPacketsEncoder getPackets() {
		// For real players with a live connection, get packets from the session.
		if (session != null) {
			return session.getWorldPackets();
		}
		// For bots (who have no session), get packets from the variable we created for them.
		return packets;
	}
	public void initIsaacKeyPair() {
		// Creates a simple, non-random key pair. The numbers don't matter for the bot.
		isaacKeyPair = new IsaacKeyPair(new int[] { 0, 0, 0, 0 });
	}
	
	public long getPacketsDecoderPing() {
		return packetsDecoderPing;
	}

	public boolean nearDummy() {
		return getX() >= 2128 && getY() >= 5518 && getX() <= 2158 && getY() <= 5551;
	}

	public String getPassword() {
		return password;
	}

	public int getPestControlGames() {
		return pestControlGames;
	}

	public int getPestPoints() {
		return pestPoints;
	}

	/**
	 * Gets the pet.
	 * 
	 * @return The pet.
	 */
	public Pet getPet() {
		return pet;
	}

	/**
	 * Gets the petManager.
	 * 
	 * @return The petManager.
	 */
	public PetManager getPetManager() {
		return petManager;
	}

	public int getPkPoints() {
		return pkPoints;
	}

	public long getPoisonImmune() {
		return poisonImmune;
	}

	public long getPolDelay() {
		return polDelay;
	}

	public long getBloodDelay() {
		return bloodDelay;
	}

	public long getPotDelay() {
		return potDelay;
	}

	public int[] getPouches() {
		return pouches;
	}

	public Prayer getPrayer() {
		return prayer;
	}

	public long getPrayerDelay() {
		Long teleblock = (Long) getTemporaryAttributtes().get("PrayerBlocked");
		if (teleblock == null)
			return 0;
		return teleblock;
	}

	public PriceCheckManager getPriceCheckManager() {
		return priceCheckManager;
	}

	public int getPrivateChatSetup() {
		return privateChatSetup;
	}

	public int getPublicStatus() {
		return publicStatus;
	}

	public QuestManager getQuestManager() {
		return questManager;
	}

	@Override
	public double getRangePrayerMultiplier() {
		return 0.6;
	}

	public String getRegisteredMac() {
		return registeredMac;
	}

	public int getRights() {
		return rights;
	}

	public byte getRunEnergy() {
		return runEnergy;
	}

	/**
	 * @return the runeSpanPoint
	 */
	public int getRuneSpanPoints() {
		return runeSpanPoints;
	}

	public Session getSession() {
		return session;
	}

	@Override
	public int getSize() {
		return getGlobalPlayerUpdater().getSize();
	}

	public Skills getSkills() {
		return skills;
	}

	public StoreManager getStore() {
		return store;
	}

	public int getSkullId() {
		return skullId;
	}

	public int getSlayerPoints() {
		return slayerPoints;
	}

	public int getSlayerPoints2() {
		return slayerPoints2;
	}

	public int getSpecRestoreTimer() {
		return specRestoreTimer;
	}

	public int getSpins() {
		return spins;
	}

	public void setSpins(int spins) {
		this.spins = spins;
	}

	public int getSummoningLeftClickOption() {
		return summoningLeftClickOption;
	}

	public List<Integer> getSwitchItemCache() {
		return switchItemCache;
	}

	/**
	 * @return the task
	 */
	public SlayerTask getTask() {
		return task;
	}

	public long getTeleBlockDelay() {
		Long teleblock = (Long) getTemporaryAttributtes().get("TeleBlocked");
		if (teleblock == null)
			return 0;
		return teleblock;
	}

	public int getTemporaryMoveType() {
		return temporaryMovementType;
	}

	public void setTemporaryMoveType(int temporaryMovementType) {
		this.temporaryMovementType = temporaryMovementType;
	}

	public Trade getTrade() {
		return trade;
	}

	public int getTradeStatus() {
		return tradeStatus;
	}

	public String getUsername() {
		return username;
	}

	public int getVecnaTimer() {
		return vecnaTimer;
	}

	public int getVotePoints() {
		return votePoints;
	}

	public int getHweenPoints() {
		return HweenPoints;
	}

	public int getTuskenPoints() {
		return TuskenPoints;
	}

	public int getElitePoints() {
		return ElitePoints;
	}

	public int getStarfirePoints() {
		return StarfirePoints;
	}

	public String getYellColor() {
		return yellColor;
	}

	public long getYellDelay() {
		return yellDelay;
	}

	@Override
	public void handleIngoingHit(final Hit hit) {
		/*
		 * if (getControlerManager().getControler() instanceof DeathEvent && !isDead())
		 * { reset(); return; }
		 */

		/** Deathtouch bracelet */
		// if ((getEquipment().getGlovesId() == 31878 ))
		// if (hit.getLook() == HitLook.POISON_DAMAGE || hit.getLook() ==
		// HitLook.REFLECTED_DAMAGE
		// || this.inDungeoneering)
		// return;

		/** Blood necklaces */
		if ((getEquipment().getAmuletId() == 32694 || getEquipment().getAmuletId() == 32697
				|| getEquipment().getAmuletId() == 32700 || getEquipment().getAmuletId() == 32703) && bloodDelay == 0) {
			if (hit.getLook() == HitLook.POISON_DAMAGE || hit.getLook() == HitLook.REFLECTED_DAMAGE
					|| this.inDungeoneering)
				return;
			int healCap = 0;
			Entity source = hit.getSource();
			if (source == null || source instanceof Player)
				return;

			PlayerCombat pc = new PlayerCombat(source);
			Entity[] targets = pc.getMultiAttackTargets(this, 2, 8);

			if (targets.length <= 1) {
				int bloodRoll = Utils.random(50, 125);
				source.applyHit(new Hit(this, bloodRoll, HitLook.REFLECTED_DAMAGE));
				if (healCap < 350)
					healCap += bloodNeckHeal(healCap, bloodRoll);
			} else {
				for (Entity target : targets) {
					if (target instanceof Player)
						return;
					if (healCap < 350) {
						int bloodRoll = Utils.random(50, 125);
						target.applyHit(new Hit(this, bloodRoll, HitLook.REFLECTED_DAMAGE));
						healCap = bloodNeckHeal(healCap, bloodRoll);
					}
				}
			}
			sendMessage(Colors.red + "Your blood necklace heals you for " + healCap + " hitpoints!");
			setBloodDelay(28);
		}
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE
				&& hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		if (invulnerable) {
			hit.setDamage(0);
			return;
		}
		if (auraManager.usingPenance()) {
			int amount = (int) (hit.getDamage() * 0.2);
			if (amount > 0)
				prayer.restorePrayer(amount);
		}
		Entity source = hit.getSource();
		if (source == null)
			return;
		if (polDelay > Utils.currentTimeMillis())
			hit.setDamage((int) (hit.getDamage() * 0.5));
		if (prayer.hasPrayersOn() && hit.getDamage() != 0) {
			if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				if (prayer.usingPrayer(0, 17))
					hit.setDamage((int) (hit.getDamage() * source.getMagePrayerMultiplier()));
				else if (prayer.usingPrayer(1, 7)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getMagePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2228));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				if (prayer.usingPrayer(0, 18))
					hit.setDamage((int) (hit.getDamage() * source.getRangePrayerMultiplier()));
				else if (prayer.usingPrayer(1, 8)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getRangePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2229));
						setNextAnimation(new Animation(12573));
					}
				}
			} else if (hit.getLook() == HitLook.MELEE_DAMAGE) {
				if (prayer.usingPrayer(0, 19))
					hit.setDamage((int) (hit.getDamage() * source.getMeleePrayerMultiplier()));
				else if (prayer.usingPrayer(1, 9)) {
					int deflectedDamage = source instanceof Nex ? 0 : (int) (hit.getDamage() * 0.1);
					hit.setDamage((int) (hit.getDamage() * source.getMeleePrayerMultiplier()));
					if (deflectedDamage > 0) {
						source.applyHit(new Hit(this, deflectedDamage, HitLook.REFLECTED_DAMAGE));
						setNextGraphics(new Graphics(2230));
						setNextAnimation(new Animation(12573));
					}
				}
			}
		}
		if (hit.getDamage() >= 200) {
			if (hit.getLook() == HitLook.MELEE_DAMAGE) {
				int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_MELEE_BONUS] / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.RANGE_DAMAGE) {
				int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_RANGE_BONUS] / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			} else if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
				int reducedDamage = hit.getDamage()
						* combatDefinitions.getBonuses()[CombatDefinitions.ABSORVE_MAGE_BONUS] / 100;
				if (reducedDamage > 0) {
					hit.setDamage(hit.getDamage() - reducedDamage);
					hit.setSoaking(new Hit(source, reducedDamage, HitLook.ABSORB_DAMAGE));
				}
			}
		}
		int shieldId = equipment.getShieldId();

		/** Defenders passive */
		if ((Defenders.isDefender(shieldId) || Defenders.isRepriser(shieldId)) && Utils.random(99) <= 14
				&& defenderPassive != true && hit.getDamage() != 0) {
			int reduc = Utils.random(Defenders.getReduc(shieldId, true), Defenders.getReduc(shieldId, false));
			double reduction = (double) (100 - reduc) / 100;
			int damage = (int) (hit.getDamage() * reduction);
			sendMessage(Colors.yellow + "Your defender has reduced the damage by " + (hit.getDamage() - damage)
					+ " points!", true);
			// Logger.log("Reduc: "+reduction+" / o/n damage:
			// "+hit.getDamage()+"/"+damage+" MIN/MAX:
			// "+Defenders.getReduc(shieldId,
			// true)+"/"+Defenders.getReduc(shieldId, false));
			hit.setDamage(damage);
			defenderPassive = true;
		}
		if (shieldId == 13742 || shieldId == 23699 || shieldId == 24884) { // elsyian
			if (Utils.getRandom(100) <= 70)
				hit.setDamage((int) (hit.getDamage() * 0.75));
		}
		if (shieldId == 13740 || shieldId == 23698 || shieldId == 24884) { // divine
			int drain = (int) (Math.ceil(hit.getDamage() * 0.3) / 2);
			if (prayer.getPrayerpoints() >= drain) {
				hit.setDamage((int) (hit.getDamage() * 0.70));
				prayer.drainPrayer(drain);
			}
		}
		if (equipment.getGlovesId() == 31878 && Utils.random(100) >= 80) {
			double damage = hit.getDamage() * Utils.random(0.25, 0.50);
			if (damage >= 150)
				damage = Utils.random(145.0, 151.9);
			source.applyHit(new Hit(this, (int) damage, HitLook.REGULAR_DAMAGE));
		}
		if (castedVeng && hit.getDamage() >= 4) {
			castedVeng = false;
			setNextForceTalk(new ForceTalk("Taste vengeance!"));
			source.applyHit(new Hit(this, (int) (hit.getDamage() * 0.75), HitLook.REGULAR_DAMAGE));
		}
		getControlerManager().processIngoingHit(hit);
		if (source instanceof Player) {
			final Player p2 = (Player) source;
			p2.getControlerManager().processIncommingHit(hit, this);
			if (p2.prayer.hasPrayersOn()) {
				if (p2.prayer.usingPrayer(0, 24)) { // smite
					int drain = hit.getDamage() / 4;
					if (drain > 0)
						prayer.drainPrayer(drain);
				} else {
					if (hit.getDamage() == 0)
						return;
					if (!p2.prayer.isBoostedLeech()) {
						if (hit.getLook() == HitLook.MELEE_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 19) || p2.prayer.usingPrayer(1, 20)
									|| p2.prayer.usingPrayer(1, 21) || p2.prayer.usingPrayer(1, 22)
									|| p2.prayer.usingPrayer(1, 23) || p2.prayer.usingPrayer(1, 24)) {
								if (Utils.getRandom(4) == 0) {
									int type = p2.prayer.usingPrayer(1, 19) ? 0
											: p2.prayer.usingPrayer(1, 20) ? 1
													: p2.prayer.usingPrayer(1, 21) ? 2
															: p2.prayer.usingPrayer(1, 22) ? 3
																	: p2.prayer.usingPrayer(1, 23) ? 4 : 5;
									p2.prayer.increaseTurmoilBonus(this, type);
									p2.prayer.setBoostedLeech(true);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 1)) { // sap att
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(0)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your sap curse has no effect.",
												true);
									} else {
										p2.prayer.increaseLeechBonus(0);
										p2.getPackets().sendGameMessage(
												"Your curse drains Attack from the enemy, boosting your Attack.", true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2214));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2215, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2216));
										}
									}, 1);
									return;
								}
							} else {
								if (p2.prayer.usingPrayer(1, 10)) {
									if (Utils.getRandom(7) == 0) {
										if (p2.prayer.reachedMax(3)) {
											p2.getPackets().sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
										} else {
											p2.prayer.increaseLeechBonus(3);
											p2.getPackets().sendGameMessage(
													"Your curse drains Attack from the enemy, boosting your Attack.",
													true);
										}
										p2.setNextAnimation(new Animation(12575));
										p2.prayer.setBoostedLeech(true);
										World.sendProjectile(p2, this, 2231, 35, 35, 20, 5, 0, 0);
										WorldTasksManager.schedule(new WorldTask() {

											@Override
											public void run() {
												setNextGraphics(new Graphics(2232));
											}
										}, 1);
										return;
									}
								}
								if (p2.prayer.usingPrayer(1, 14)) {
									if (Utils.getRandom(7) == 0) {
										if (p2.prayer.reachedMax(7)) {
											p2.getPackets().sendGameMessage(
													"Your opponent has been weakened so much that your leech curse has no effect.",
													true);
										} else {
											p2.prayer.increaseLeechBonus(7);
											p2.getPackets().sendGameMessage(
													"Your curse drains Strength from the enemy, boosting your Strength.",
													true);
										}
										p2.setNextAnimation(new Animation(12575));
										p2.prayer.setBoostedLeech(true);
										World.sendProjectile(p2, this, 2248, 35, 35, 20, 5, 0, 0);
										WorldTasksManager.schedule(new WorldTask() {
											@Override
											public void run() {
												setNextGraphics(new Graphics(2250));
											}
										}, 1);
										return;
									}
								}

							}
						}
						if (hit.getLook() == HitLook.RANGE_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 2)) { // sap range
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(1)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your sap curse has no effect.",
												true);
									} else {
										p2.prayer.increaseLeechBonus(1);
										p2.getPackets().sendGameMessage(
												"Your curse drains Range from the enemy, boosting your Range.", true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2217));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2218, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {

										@Override
										public void run() {
											setNextGraphics(new Graphics(2219));
										}
									}, 1);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 11)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.prayer.reachedMax(4)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your leech curse has no effect.",
												true);
									} else {
										p2.prayer.increaseLeechBonus(4);
										p2.getPackets().sendGameMessage(
												"Your curse drains Range from the enemy, boosting your Range.", true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2236, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2238));
										}
									});
									return;
								}
							}
						}
						if (hit.getLook() == HitLook.MAGIC_DAMAGE) {
							if (p2.prayer.usingPrayer(1, 3)) { // sap mage
								if (Utils.getRandom(4) == 0) {
									if (p2.prayer.reachedMax(2)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your sap curse has no effect.",
												true);
									} else {
										p2.prayer.increaseLeechBonus(2);
										p2.getPackets().sendGameMessage(
												"Your curse drains Magic from the enemy, boosting your Magic.", true);
									}
									p2.setNextAnimation(new Animation(12569));
									p2.setNextGraphics(new Graphics(2220));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2221, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {

										@Override
										public void run() {
											setNextGraphics(new Graphics(2222));
										}
									}, 1);
									return;
								}
							} else if (p2.prayer.usingPrayer(1, 12)) {
								if (Utils.getRandom(7) == 0) {
									if (p2.prayer.reachedMax(5)) {
										p2.getPackets().sendGameMessage(
												"Your opponent has been weakened so much that your leech curse has no effect.",
												true);
									} else {
										p2.prayer.increaseLeechBonus(5);
										p2.getPackets().sendGameMessage(
												"Your curse drains Magic from the enemy, boosting your Magic.", true);
									}
									p2.setNextAnimation(new Animation(12575));
									p2.prayer.setBoostedLeech(true);
									World.sendProjectile(p2, this, 2240, 35, 35, 20, 5, 0, 0);
									WorldTasksManager.schedule(new WorldTask() {
										@Override
										public void run() {
											setNextGraphics(new Graphics(2242));
										}
									}, 1);
									return;
								}
							}
						}

						// overall

						if (p2.prayer.usingPrayer(1, 13)) { // leech defence
							if (Utils.getRandom(10) == 0) {
								if (p2.prayer.reachedMax(6)) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your leech curse has no effect.",
											true);
								} else {
									p2.prayer.increaseLeechBonus(6);
									p2.getPackets().sendGameMessage(
											"Your curse drains Defence from the enemy, boosting your Defence.", true);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								World.sendProjectile(p2, this, 2244, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {

									@Override
									public void run() {
										setNextGraphics(new Graphics(2246));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 15)) {
							if (Utils.getRandom(10) == 0) {
								if (getRunEnergy() <= 0) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your leech curse has no effect.",
											true);
								} else {
									p2.setRunEnergy(p2.getRunEnergy() > 90 ? 100 : p2.getRunEnergy() + 10);
									setRunEnergy(p2.getRunEnergy() > 10 ? getRunEnergy() - 10 : 0);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								World.sendProjectile(p2, this, 2256, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2258));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 16)) {
							if (Utils.getRandom(10) == 0) {
								if (combatDefinitions.getSpecialAttackPercentage() <= 0) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your leech curse has no effect.",
											true);
								} else {
									p2.combatDefinitions.restoreSpecialAttack();
									combatDefinitions.decreaseSpecialAttack(10);
								}
								p2.setNextAnimation(new Animation(12575));
								p2.prayer.setBoostedLeech(true);
								World.sendProjectile(p2, this, 2252, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {

									@Override
									public void run() {
										setNextGraphics(new Graphics(2254));
									}
								}, 1);
								return;
							}
						}

						if (p2.prayer.usingPrayer(1, 4)) { // sap spec
							if (Utils.getRandom(10) == 0) {
								p2.setNextAnimation(new Animation(12569));
								p2.setNextGraphics(new Graphics(2223));
								p2.prayer.setBoostedLeech(true);
								if (combatDefinitions.getSpecialAttackPercentage() <= 0) {
									p2.getPackets().sendGameMessage(
											"Your opponent has been weakened so much that your sap curse has no effect.",
											true);
								} else {
									combatDefinitions.decreaseSpecialAttack(10);
								}
								World.sendProjectile(p2, this, 2224, 35, 35, 20, 5, 0, 0);
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										setNextGraphics(new Graphics(2225));
									}
								}, 1);
								return;
							}
						}
					}
				}
			}
		} else {

			NPC n = (NPC) source;
			if (n.getId() == 13448)

				sendSoulSplit(hit, n);
		}

	}

	public boolean hasDisabledYell() {
		return yellDisabled;
	}

	public boolean hasDisplayName() {
		return displayName != null;
	}

	public boolean hasInstantSpecial(final int weaponId) {
		switch (weaponId) {
		case 4153:
		case 15486:
		case 22207:
		case 22209:
		case 22211:
		case 22213:
		case 1377:
		case 13472:
		case 35:// Excalibur
		case 8280:
		case 14632:
			return true;
		default:
			return false;
		}
	}

	public boolean hasLargeSceneView() {
		return largeSceneView;
	}

	public boolean hasSkull() {
		return skullDelay > 0;
	}

	public boolean isActive() {
		return active;
	}

	public boolean hasTalkedtoCook() {
		return talkedtoCook;
	}

	@Override
	public void heal(int ammount, int extra) {
		super.heal(ammount, extra);
		refreshHitPoints();
	}

	public void increaseCrucibleHighScore() {
		crucibleHighScore++;
	}

	/**
	 * Increases kill count and all adjustments to kill streaks.
	 * 
	 * @param killed The Enemy player killed.
	 */
	public void increaseKillCount(Player killed) {
		if (killed == null || this == null)
			return;
		if (getLastKilled() == null || getLastKilledIP() == null) {
			setLastKilled("Zeus");
			setLastKilledIP("127.0.0.1");
		}
		if (killed.getSession().getIP().equals(getSession().getIP())
				|| (killed.getUsername().equalsIgnoreCase(getLastKilled()))
				|| (killed.getSession().getIP().equals(getLastKilledIP())))
			return;
		killed.deathCount++;
		killStreak += 1;

		if (killStreak > highestKillStreak)
			highestKillStreak = killStreak;

		sendMessage(Colors.red + "<img=10>You are now on a " + killStreak + " kill streak!");

		if (killStreak % 5 == 0 && killStreak > 0) {
			Discord.sendAchievement(getDisplayName() + " is on a " + killStreak
					+ " killstreak. Their highest streak is " + highestKillStreak);

			World.sendWorldMessage(Colors.red + "<img=11>" + getDisplayName() + " is on a <col=ff0000>" + killStreak
					+ "</col>" + Colors.red + " killstreak. " + "Their highest streak is <col=ff0000>"
					+ highestKillStreak + "</col>.", false);
		}

		if (killStreak >= 5) {
			int streakPoints = killStreakPoints;
			int totalstreakPoints = totalkillStreakPoints;
			setTotalKillStreakPoints(getTotalKillStreakPoints() + 1);
			streakPoints = 1;
			if (isGold()) {
				setTotalKillStreakPoints(getTotalKillStreakPoints() + 1);
				streakPoints = 2;
			}

			sendMessage(Colors.blue + "You have reached a milestone killstreak and have been rewared with "
					+ streakPoints + " killstreak Point(s)");
			sendMessage(Colors.blue + "You now have " + totalstreakPoints + " killstreak points!");
		}

		PkRank.checkRank(killed);

		killCount++;

		sendMessage(Colors.red + "You have killed " + killed.getDisplayName() + ", " + "you now have " + killCount
				+ " kills.");

		PkRank.checkRank(this);

		addPoints();

	}

	public int getPkPointReward() {
		return pkPointReward;
	}

	public int setPkPointReward(int PkPointReward) {
		return this.pkPointReward = PkPointReward;
	}

	public int getPointsHad() {
		return pointsHad;
	}

	public int setPointsHad(int PointsHad) {
		return this.pointsHad = PointsHad;
	}

	public void addPoints() {
		if (getPkPointReward() <= 500000) {
			setPointsHad(getPkPoints());
			setPkPoints(getPkPoints() + 10 + Utils.random(10));
			int pointsgiven = getPkPoints() - pointsHad;
			sendMessage("You have received " + pointsgiven + " Pk Points");
		}
		if (getPkPointReward() >= 500000 && getPkPointReward() <= 999999) {
			setPointsHad(getPkPoints());
			setPkPoints(getPkPoints() + 10 + Utils.random(25));
			int pointsgiven = getPkPoints() - pointsHad;
			sendMessage("You have received " + pointsgiven + " Pk Points");
		}
		if (getPkPointReward() >= 1000000 && getPkPointReward() <= 4999999) {
			setPkPoints(getPkPoints() + 10 + Utils.random(50));
			setPointsHad(getPkPoints());
			int pointsgiven = getPkPoints() - pointsHad;
			sendMessage("You have received " + pointsgiven + " Pk Points");
		}
		if (getPkPointReward() >= 5000000) {
			setPointsHad(getPkPoints());
			setPkPoints(getPkPoints() + 10 + Utils.random(90));
			int pointsgiven = getPkPoints() - pointsHad;
			sendMessage("You have received " + pointsgiven + " Pk Points");
		}
	}

	public void addKill(Player dead, boolean safe) {
		setLastKilled(dead.getUsername());
		setLastKilledIP(dead.getSession().getIP());
		if (dead.getControlerManager().getControler() != null)
			return;
		int risk = safe ? 0 : checkHighestKill(dead);
		setPkPointReward(risk);
	}

	public boolean isApeAtoll() {
		return (getX() >= 2693 && getX() <= 2821 && getY() >= 2693 && getY() <= 2817);
	}

	/**
	 * PvP
	 */
	public int totalpkPoints;

	public BountyHunter bountyHunter;

	public BountyHunter getBountyHunter() {
		return bountyHunter;
	}

	private String lastKilled;

	private String lastKilledIP;

	@SuppressWarnings("unused")
	private double dropRate;

	public String getLastKilled() {
		return lastKilled;
	}

	public String getLastKilledIP() {
		return lastKilledIP;
	}

	public void setLastKilled(String player) {
		lastKilled = player;
	}

	public void setLastKilledIP(String ip) {
		lastKilledIP = ip;
	}

	public boolean isCanPvp() {
		return canPvp;
	}

	public boolean isCantTrade() {
		return cantTrade;
	}

	public boolean isCastVeng() {
		return castedVeng;
	}

	public boolean isCompletedFightCaves() {
		return completedFightCaves;
	}

	public boolean isCompletedFightKiln() {
		return completedFightKiln;
	}

	public boolean isCompletedRfd() {
		return completedRfd;
	}

	public boolean isBronze() {
		return donator || donatorTill > Utils.currentTimeMillis();
	}

	public boolean isEquipDisabled() {
		return disableEquip;
	}

	public boolean isSilver() {
		return extremeDonator || extremeDonatorTill > Utils.currentTimeMillis();
	}

	public boolean isExtremePermDonator() {
		return extremeDonator;
	}

	public boolean isFilterGame() {
		return filterGame;
	}

	public boolean isForceNextMapLoadRefresh() {
		return forceNextMapLoadRefresh;
	}

	public boolean isHidingWorldMessages() {
		return hideWorldAnnouncements;
	}

	public boolean isInAnimationRoom() {
		return inAnimationRoom;
	}

	public boolean isInDefenderRoom() {
		return isInDefenderRoom;
	}

	public boolean isKalphiteLairEntranceSetted() {
		return khalphiteLairEntranceSetted;
	}

	public boolean isKalphiteLairSetted() {
		return khalphiteLairSetted;
	}

	public boolean isKilledAgrithNaNa() {
		return agrithNaNa;
	}

	public boolean isKilledBork() {
		return killedBork;
	}

	/**
	 * RFD
	 */

	public boolean isKilledCulinaromancer() {
		return culinaromancer;
	}

	public boolean isKilledDessourt() {
		return dessourt;
	}

	public boolean isKilledFlambeed() {
		return flamBeed;
	}

	public boolean isKilledKaramel() {
		return karamel;
	}

	/**
	 * Gets the killedQueenBlackDragon.
	 * 
	 * @return The killedQueenBlackDragon.
	 */
	public boolean isKilledQueenBlackDragon() {
		return killedQueenBlackDragon;
	}

	public boolean isLocked() {
		return lockDelay >= Utils.currentTimeMillis();
	}

	/**
	 * Checks if @this username should have access to all commands.
	 * 
	 * @return true if has access.
	 */
	public boolean isOwner() {
		for (String s : Settings.OWNER) {
			if (getUsername().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean isDev() {
		for (String s : Settings.DEV) {
			if (getUsername().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean isAdmin() {
		for (String s : Settings.ADMIN) {
			if (getUsername().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean isWiki() {
		for (String s : Settings.WIKI) {
			if (getUsername().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean isMod() {
		for (String s : Settings.MOD) {
			if (getUsername().equalsIgnoreCase(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean isSupport() {
		for (String s : Settings.SUPPORT) {
			if (getUsername().equalsIgnoreCase(s) || support) {
				return true;
			}
		}
		return false;
	}

	public boolean canBan() {
		return isOwner() || isDev() || isMod() || isSupport() || isForumManager() || isCommunityManager() ? true
				: false;
	}

	public boolean isPermBanned() {
		return permBanned;
	}

	public boolean isPermMuted() {
		return permMuted;
	}

	public boolean isResting() {
		return resting;
	}

	public boolean isRunning() {
		return running;
	}

	public boolean isSpawnsMode() {
		return spawnsMode;
	}

	public boolean isTalkedWithMarv() {
		return talkedWithMarv;
	}

	public boolean isTalkedWithVannaka() {
		return talkedWithVannaka;
	}

	public boolean isUpdateMovementType() {
		return updateMovementType;
	}

	public boolean isUsingReportOption() {
		return reportOption;
	}

	public boolean isWonFightPits() {
		return wonFightPits;
	}

	public boolean isXpLocked() {
		return xpLocked;
	}

	public boolean isYellOff() {
		return yellOff;
	}

	public void kickPlayerFromFriendsChannel(String name) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.kickPlayerFromChat(this, name);
	}

	@Override
	public void loadMapRegions() {
		boolean wasAtDynamicRegion = isAtDynamicRegion();
		super.loadMapRegions();
		clientLoadedMapRegion = false;
		if (isAtDynamicRegion()) {
			getPackets().sendDynamicMapRegion(!isActive());
			// if (!wasAtDynamicRegion)
			localNPCUpdate.reset();
		} else {
			getPackets().sendMapRegion(!isActive());
			if (wasAtDynamicRegion)
				localNPCUpdate.reset();
		}
		forceNextMapLoadRefresh = false;
	}

	public void lock() {
		lockDelay = Long.MAX_VALUE;
	}

	public void lock(long time) {
		lockDelay = Utils.currentTimeMillis() + (time * 600);
	}

	/**
	 * Logs the player out.
	 * 
	 * @param lobby If we're logging out to the lobby.
	 */
	public void logout(boolean force) {
		if (!force) {
			long currentTime = Utils.currentTimeMillis();
			if (!isRunning())
				return;
			if (lockDelay >= currentTime || lunarDelay >= currentTime) {
				getPackets().sendGameMessage("You can't log out while performing an action.");
				return;
			}
			if (isUnderCombat(10)) {
				sendMessage("You can't log out until 10 seconds after the end of combat.");
				return;
			}
			if (getEmotesManager().isDoingEmote()) {
				sendMessage("You can't log out while performing an emote.");
				return;
			}
			if (isLocked()) {
				sendMessage("You can't log out while performing an action.");
				return;
			}
		}
		AutoSkillingManager.handlePlayerLogout(this);
		CombatMastery.onPlayerLogout(this);
		getPackets().sendLogout();
		setRunning(false);
		BossBalancer.clearPlayerCache(getIndex());
	}

	@SuppressWarnings("deprecation")
	public void makeDonator(int months) {
		if (donatorTill < Utils.currentTimeMillis())
			donatorTill = Utils.currentTimeMillis();
		Date date = new Date(donatorTill);
		date.setMonth(date.getMonth() + months);
		donatorTill = date.getTime();
	}

	@Override
	public boolean needMasksUpdate() {
		return super.needMasksUpdate() || temporaryMovementType != -1 || isUpdateMovementType();
	}

	public void performInstantSpecial(final int weaponId) {
		int specAmt = PlayerCombat.getSpecialAmmount(weaponId);
		if (combatDefinitions.hasRingOfVigour())
			specAmt *= 0.9;
		if (combatDefinitions.getSpecialAttackPercentage() < specAmt) {
			sendMessage("You don't have enough power left.");
			combatDefinitions.decreaseSpecialAttack(0);
			return;
		}
		if (getSwitchItemCache().size() > 0) {
			ButtonHandler.submitSpecialRequest(this);
			return;
		}
		if (!isUnderCombat()) // cuz of sheating
			PlayerCombat.addAttackingDelay(this);
		switch (weaponId) {
		case 4153:
			combatDefinitions.setInstantAttack(true);
			combatDefinitions.switchUsingSpecialAttack();
			Entity target = (Entity) getTemporaryAttributtes().get("last_target");
			if (target != null && target.getTemporaryAttributtes().get("last_attacker") == this) {
				if (!(getActionManager().getAction() instanceof PlayerCombat)
						|| ((PlayerCombat) getActionManager().getAction()).getTarget() != target) {
					getActionManager().setAction(new PlayerCombat(target));
				}
			}
			break;
		case 1377:
		case 13472:
			setNextAnimation(new Animation(1056));
			setNextGraphics(new Graphics(246));
			setNextForceTalk(new ForceTalk("Raarrrrrgggggghhhhhhh!"));
			int defence = (int) (skills.getLevelForXp(Skills.DEFENCE) * 0.90D);
			int attack = (int) (skills.getLevelForXp(Skills.ATTACK) * 0.90D);
			int range = (int) (skills.getLevelForXp(Skills.RANGE) * 0.90D);
			int magic = (int) (skills.getLevelForXp(Skills.MAGIC) * 0.90D);
			int strength = (int) (skills.getLevelForXp(Skills.STRENGTH) * 1.2D);
			skills.set(Skills.DEFENCE, defence);
			skills.set(Skills.ATTACK, attack);
			skills.set(Skills.RANGE, range);
			skills.set(Skills.MAGIC, magic);
			skills.set(Skills.STRENGTH, strength);
			combatDefinitions.decreaseSpecialAttack(specAmt);
			break;
		case 35:// Excalibur
		case 8280:
		case 14632:
			setNextAnimation(new Animation(1168));
			setNextGraphics(new Graphics(247));
			final boolean enhanced = weaponId == 14632;
			skills.set(Skills.DEFENCE, enhanced ? (int) (skills.getLevelForXp(Skills.DEFENCE) * 1.15D)
					: (skills.getLevel(Skills.DEFENCE) + 8));
			WorldTasksManager.schedule(new WorldTask() {
				int count = 5;

				@Override
				public void run() {
					if (isDead() || hasFinished() || getHitpoints() >= getMaxHitpoints()) {
						stop();
						return;
					}
					heal(enhanced ? 80 : 40);
					if (count-- == 0) {
						stop();
						return;
					}
				}
			}, 4, 2);
			combatDefinitions.decreaseSpecialAttack(specAmt);
			break;
		case 15486:
		case 22207:
		case 22209:
		case 22211:
		case 22213:
			setNextAnimation(new Animation(12804));
			setNextGraphics(new Graphics(2319));// 2320
			setNextGraphics(new Graphics(2321));
			addPolDelay(60000);
			combatDefinitions.decreaseSpecialAttack(specAmt);
			break;
		}
	}

	@Override
	public void processEntity() {

		processLogicPackets();
		cutscenesManager.process();
		if (routeEvent != null && routeEvent.processEvent(this))
			routeEvent = null;
		super.processEntity();
		charges.process();
		auraManager.process();
		actionManager.process();
		dayOfWeekManager.process();
		elderTreeManager.process();
		prayer.processPrayer();
		controlerManager.process();

		if (musicsManager.musicEnded())
			musicsManager.replayMusic();
		if (hasSkull()) {
			skullDelay--;
			if (!hasSkull())
				getGlobalPlayerUpdater().generateAppearenceData();
		}
		if (!(getControlerManager().getControler() instanceof ZOControler) && ZOGame.withinArea(this)) {
			ZOGame.removePlayer(this);
		}
		if (!(getControlerManager().getControler() instanceof DeathEvent) && !isDead()) {
			if (Wilderness.isAtWild(this)) {
				if (!(getControlerManager().getControler() instanceof Wilderness)) {
					getControlerManager().startControler("Wilderness");
				}
			} else {
				if (getControlerManager().getControler() instanceof Wilderness) {
					getControlerManager().removeControlerWithoutCheck();
					getControlerManager().forceStop();
				}
			}
		}
		if (getEquipment().getHatId() == ThroneManager.ANCIENT_CROWN) {
			if (!ThroneManager.getThrone().getKing().equals(getUsername())) {
				getEquipment().getItems().set(Equipment.SLOT_HAT, null);
				getEquipment().refresh(Equipment.SLOT_HAT);
				getGlobalPlayerUpdater().generateAppearenceData();
			}
		}
		if (getEquipment().getWeaponId() == ThroneManager.ANCIENT_SWORD) {
			if (!ThroneManager.getThrone().getKing().equals(getUsername())) {
				getEquipment().getItems().set(Equipment.SLOT_WEAPON, null);
				getEquipment().refresh(Equipment.SLOT_WEAPON);
				getGlobalPlayerUpdater().generateAppearenceData();
			}
		}
		if (polDelay != 0 && polDelay <= Utils.currentTimeMillis()) {
			sendMessage("The power of the light fades. Your resistance to melee attacks return to normal.");
			polDelay = 0;
		}
		if (bloodDelay > 0)
			bloodDelay--;

		if (doubleXpTimer > 0)
			doubleXpTimer--;

		if (overloadDelay > 0) {
			if (overloadDelay == 1 || isDead()) {
				Pots.resetOverLoadEffect(this);
				return;
			} else if ((overloadDelay - 1) % 25 == 0)
				Pots.applyOverLoadEffect(this);
			overloadDelay--;
		}
		if (prayerRenewalDelay > 0) {
			if (prayerRenewalDelay == 1 || isDead()) {
				sendMessage(Colors.red + "Your prayer renewal effect has run out.");
				prayerRenewalDelay = 0;
				return;
			} else {
				if (prayerRenewalDelay == 50)
					sendMessage(Colors.red + "Your prayer renewal effect will run out in 30 seconds..", true);
				if (!prayer.hasFullPrayerpoints()) {
					getPrayer().restorePrayer(1);
					if ((prayerRenewalDelay - 1) % 25 == 0)
						setNextGraphics(new Graphics(1295));
				}
			}
			prayerRenewalDelay--;
		}
		int hp = getMaxHitpoints();
		if (getPetManager().isConstitutionPet() && petCanHealAgain() && !isAtWild() && !isCanPvp()
				&& getHitpoints() < hp) {
			double level = (getPet().getDetails().getLevel() * .02);
			heal((int) ((getMaxHitpoints() * (level))));
			setPetLastHealCd();
			getPackets()
					.sendGameMessage("<col=109C03>Your pet heals you for " + (level) + "% of your maximum hitpoints.");
		}

		/**
		 * Prifddinas Thiev timers. If you can think of a better way to handle this - do
		 * tell.
		 */
		if (thievIthell > 0) {
			if (thievIthell == 1 || isDead()) {
				sendMessage("Clan Ithell has forgotten about your pickpocketing.");
				thievIthell = 0;
			} else
				thievIthell--;
		}
		if (thievIorwerth > 0) {
			if (thievIorwerth == 1 || isDead()) {
				sendMessage("Clan Iorwerth has forgotten about your pickpocketing.");
				thievIorwerth = 0;
			} else
				thievIorwerth--;
		}
		if (thievCadarn > 0) {
			if (thievCadarn == 1 || isDead()) {
				sendMessage("Clan Cadarn has forgotten about your pickpocketing.");
				thievCadarn = 0;
			} else
				thievCadarn--;
		}
		if (thievAmlodd > 0) {
			if (thievAmlodd == 1 || isDead()) {
				sendMessage("Clan Amlodd has forgotten about your pickpocketing.");
				thievAmlodd = 0;
			} else
				thievAmlodd--;
		}
		if (thievTrahaearn > 0) {
			if (thievTrahaearn == 1 || isDead()) {
				sendMessage("Clan Trahaearn has forgotten about your pickpocketing.");
				thievTrahaearn = 0;
			} else
				thievTrahaearn--;
		}
		if (thievHefin > 0) {
			if (thievHefin == 1 || isDead()) {
				sendMessage("Clan Hefin has forgotten about your pickpocketing.");
				thievHefin = 0;
			} else
				thievHefin--;
		}
		if (thievCrwys > 0) {
			if (thievCrwys == 1 || isDead()) {
				sendMessage("Clan Crwys has forgotten about your pickpocketing.");
				thievCrwys = 0;
			} else
				thievCrwys--;
		}
		if (thievMeilyr > 0) {
			if (thievMeilyr == 1 || isDead()) {
				sendMessage("Clan Meilyr has forgotten about your pickpocketing.");
				thievMeilyr = 0;
			} else
				thievMeilyr--;
		}
		if (specRestoreTimer > 0)
			specRestoreTimer--;
		if (yellDelay > 0)
			yellDelay--;
		if (lastBonfire > 0) {
			lastBonfire--;
			if (lastBonfire == 500)
				sendMessage("<col=ffff00>The health boost from stoking a bonfire will run out in 5 minutes..", true);
			else if (lastBonfire == 0) {
				sendMessage("<col=ff0000>The health boost you received from stoking a bonfire has ran out.");
				equipment.refreshConfigs(false);
			}
		}
		farmingManager.process();
		if (getInventory().containsItem(10501, 1) || getEquipment().getWeaponId() == 10501)
			getPackets().sendPlayerOption("Pelt", 6, true);
		else
			getPackets().sendPlayerOption("Null", 6, true);

		// LendingManager.processs();

		DivineObject.resetGatherLimit(this);
		getSquealOfFortune().giveDailySpins();

		if (isAFK()) {
			realFinish();
			World.sendWorldMessage("Player [" + getDisplayName() + " (" + getUsername() + ")] has been kicked for AFK.",
					true);
		}

		getCombatDefinitions().processCombatStance();

		PotionTimersInter.process(this);
	}

	@SuppressWarnings("unused")
	// Taken from MX3, the packet settings aren't compatible :/
	public void processProjectiles() {

		for (int regionId : getMapRegionsIds()) {
			Region region = World.getRegion(regionId);
			for (Projectile projectile : region.getProjectiles()) {

				int fromSizeX, fromSizeY;
				if (projectile.getFrom() instanceof Entity)
					fromSizeX = fromSizeY = ((Entity) projectile.getFrom()).getSize();
				else if (projectile.getFrom() instanceof WorldObject) {
					ObjectDefinitions defs = ((WorldObject) projectile.getFrom()).getDefinitions();
					fromSizeX = defs.getSizeX();
					fromSizeY = defs.getSizeY();
				} else
					fromSizeX = fromSizeY = 1;
				int toSizeX, toSizeY;
				if (projectile.getTo() instanceof Entity)
					toSizeX = toSizeY = ((Entity) projectile.getTo()).getSize();
				else if (projectile.getTo() instanceof WorldObject) {
					ObjectDefinitions defs = ((WorldObject) projectile.getTo()).getDefinitions();
					toSizeX = defs.getSizeX();
					toSizeY = defs.getSizeY();
				} else
					toSizeX = toSizeY = 1;

				// getPackets().sendProjectileNew(projectile.getFrom(),
				// fromSizeX, fromSizeY, projectile.getTo(), toSizeX,
				// toSizeY, projectile.getFrom() instanceof Entity ? (Entity)
				// projectile.getFrom() : null,
				// projectile.getTo() instanceof Entity ? (Entity)
				// projectile.getTo() : null,
				// projectile.isAdjustFlyingHeight(),
				// projectile.isAdjustSenderHeight(),
				// projectile.getSenderBodyPart(), projectile.getGraphicId(),
				// projectile.getStartHeight(),
				// projectile.getEndHeight(), projectile.getStartTime(),
				// projectile.getEndTime(),
				// projectile.getSlope(), projectile.getAngle(), 0);
			}
		}
	}

	public void processLogicPackets() {
		if (isBot()) {
			return; // Bots don't process packets
		}
		LogicPacket packet;
		while ((packet = logicPackets.poll()) != null) {
			WorldPacketsDecoder.decodeLogicPacket(this, packet);
		}
	}

	@Override
	public void processReceivedHits() {
		if (isLocked())
			return;
		super.processReceivedHits();
	}

	public void realFinish() {
		if (hasFinished())
			return;
		stopAll();
		cutscenesManager.logout();
		controlerManager.logout();
		/* ITEM LEDNING */
		Lend lend = LendingManager.getLend(this);
		Lend hasLendedOut = LendingManager.getHasLendedItemsOut(this);
		if (lend != null) {
			if (getTrade().getLendedTime() == 0)
				LendingManager.unLend(lend);
		}
		if (hasLendedOut != null) {
			if (getTrade().getLendedTime() == 0)
				LendingManager.unLend(hasLendedOut);
		}
		if (slayerManager.getSocialPlayer() != null)
			slayerManager.resetSocialGroup(true);
		/* END OF ITEM LEDNING */
		house.finish();
		dungManager.finish();
		// coOpSlayer.handleLogout(this);
		setRunning(false);
		friendsIgnores.sendFriendsMyStatus(false);
		if (currentFriendChat != null)
			currentFriendChat.leaveChat(this, true);
		if (clanManager != null)
			clanManager.disconnect(this, false);
		if (guestClanManager != null)
			guestClanManager.disconnect(this, true);
		if (familiar != null && !familiar.isFinished())
			familiar.dissmissFamiliar(true);
		else if (pet != null)
			pet.finish();
		// new Thread(new Highscores(this)).start();
		lastLoggedIn = System.currentTimeMillis();
		setTotalPlayTime(getTotalPlayTime() + (getRecordedPlayTime() - Utils.currentTimeMillis()));
		setTimePlayedWeekly(getTimePlayedWeekly());
		setFinished(true);
		if (!(this instanceof Bot)) {
			session.setDecoder(-1);
		}
		SerializableFilesManager.savePlayer(this);
		World.updateEntityRegion(this);
		World.removePlayer(this);
		if (Settings.DEBUG)
			Logger.log(this, "Finished Player: " + username);
		Logger.log("Player " + getUsername() + " has logged out, " + "there are " + World.getPlayers().size()
				+ " players on.");

		// DiscordMessageHandler.sendMessage("ingame-live-feed", "`" + getDisplayName()
		// + " has logged out.`");
		// PlayersOnlineManager.updatePlayersOnline();
	}

	public void refreshAllowChatEffects() {
		getPackets().sendConfig(171, allowChatEffects ? 0 : 1);
	}

	public void awardDonation(Player player, String id) {
		Donations.donationList(player, id);
	}

	public void sendLoginMessages() {
		/*
		 * if (getUsername().equalsIgnoreCase("Zeus")) World.
		 * sendWorldMessage("<img=2>[<col=b30059>Owner</col>]<col=b30059> Zeus, has just logged in!"
		 * , false);
		 */
		if (isOwner())
			World.sendWorldMessage("[<col=739900>" + Settings.SERVER_NAME + " Owner</col>] <img=1>" + getDisplayName()
					+ ", has just logged in!", false);

		if (isAdmin())
			World.sendWorldMessage(
					"[" + Colors.gold + "Administrator</col>] <img=1>" + getDisplayName() + ", has just logged in!",
					false);
		if (isCommunityManager())
			World.sendWorldMessage("[" + Colors.blue + "Community Manager</col>] <img=22>" + getDisplayName()
					+ ", has just logged in!", false);
		if (isMod())
			World.sendWorldMessage(
					"[" + Colors.gray + "Moderator</col>] <img=0>" + getDisplayName() + ", has just logged in!", false);
		if (isSupport())
			World.sendWorldMessage(
					"[" + Colors.blue + "Support</col>] <img=13>" + getDisplayName() + ", has just logged in!", false);
	}

	/*
	 * Donation function - webcall to rsps-pay
	 */
	public void completeDonationProcess(Player player, String productId, String price, boolean referral) {
		/*
		 * if (!referral) ReferralHandler.processReferralDonation(player, (int)
		 * Double.parseDouble(price));
		 */
		TopWeeklyDonates.addDonate(player, Double.parseDouble(price));// test:P
		Donations.donationList(player, productId);
	}

	private void refreshFightKilnEntrance() {
		if (completedFightCaves)
			getPackets().sendConfigByFile(10838, 1);
	}

	public void refreshHitPoints() {
		getPackets().sendConfigByFile(7198, getHitpoints());// go
	}

	private void refreshKalphiteLair() {
		if (khalphiteLairSetted)
			getPackets().sendConfigByFile(7263, 1);
	}

	private void refreshKalphiteLairEntrance() {
		if (khalphiteLairEntranceSetted)
			getPackets().sendConfigByFile(7262, 1);
	}

	private void refreshLodestoneNetwork() {
		if (lodestone == null || lodestone[9] != true)
			lodestone = new boolean[] { false, false, false, false, false, false, false, false, false, true, false,
					false, false, false, false };
		getPackets().sendConfigByFile(358, lodestone[0] ? 15 : 14);
		getPackets().sendConfigByFile(2448, lodestone[1] ? 190 : 189);
		for (int i = 10900; i < 10913; i++) {
			getPackets().sendConfigByFile(i, lodestone[(i - 10900) + 2] ? 1 : -1);
		}
	}

	public void refreshMoneyPouch() {
		getPackets().sendRunScript(5560, getMoneyPouch().getTotal());
	}

	public void refreshMouseButtons() {
		getPackets().sendConfig(170, mouseButtons ? 0 : 1);
	}

	public void refreshOtherChatsSetup() {
		int value = friendChatSetup << 6;
		getPackets().sendConfig(1438, value);
	}

	public void refreshPrivateChatSetup() {
		getPackets().sendConfig(287, privateChatSetup);
	}

	public void refreshSpawnedItems() {
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId).getGroundItems();
			if (floorItems == null)
				continue;
			for (FloorItem item : floorItems) {
				if (item.isInvisible() && (item.hasOwner() && !getUsername().equals(item.getOwner()))
						|| item.getTile().getPlane() != getPlane() || !getUsername().equals(item.getOwner())
								&& (!ItemConstants.isTradeable(item) && !item.isForever()))
					continue;
				getPackets().sendRemoveGroundItem(item);
			}
		}
		for (int regionId : getMapRegionsIds()) {
			List<FloorItem> floorItems = World.getRegion(regionId).getGroundItems();
			if (floorItems == null)
				continue;
			for (FloorItem item : floorItems) {
				if ((item.isInvisible()) && (item.hasOwner() && !getUsername().equals(item.getOwner()))
						|| item.getTile().getPlane() != getPlane() || !getUsername().equals(item.getOwner())
								&& (!ItemConstants.isTradeable(item) && !item.isForever()))
					continue;
				getPackets().sendGroundItem(item);
			}
		}
	}

	public void refreshSpawnedObjects() {
		for (int regionId : getMapRegionsIds()) {
			List<WorldObject> spawnedObjects = World.getRegion(regionId).getSpawnedObjects();
			if (spawnedObjects != null) {
				for (WorldObject object : spawnedObjects) {
					if (object.getPlane() == getPlane())
						getPackets().sendSpawnedObject(object);
				}
			}
			List<WorldObject> removedObjects = World.getRegion(regionId).getRemovedOriginalObjects();
			if (removedObjects != null) {
				for (WorldObject object : removedObjects)
					if (object.getPlane() == getPlane())
						getPackets().sendDestroyObject(object);
			}
		}
	}

	@Override
	public void removeHitpoints(Hit hit) {
		super.removeHitpoints(hit);
		refreshHitPoints();
	}

	public void removeSkull() {
		skullDelay = -1;
		getGlobalPlayerUpdater().generateAppearenceData();
	}

	@Override
	public void reset() {
		reset(true);
	}

	@Override
	public void reset(boolean attributes) {
		super.reset(attributes);
		hintIconsManager.removeAll();
		skills.restoreSkills();
		combatDefinitions.resetSpecialAttack();
		prayer.reset();
		combatDefinitions.resetSpells(true);
		resting = false;
		skullDelay = 0;
		foodDelay = 0;
		cantWalk = false;
		potDelay = 0;
		karamDelay = 0;
		poisonImmune = 0;
		fireImmune = 0;
		superAntiFire = 0;
		prayerRenewalDelay = 0;
		castedVeng = false;
		lastBonfire = 0;
		if (getOverloadDelay() > 0)
			Pots.resetOverLoadEffect(this);
		setRunEnergy(100);
		removeDamage(this);
		getEquipment().refreshConfigs(false);
		refreshHitPoints();
		getGlobalPlayerUpdater().generateAppearenceData();
		potionTimer.slotTimerArray[PotionTimersInter.ANTIPOISON] = poisonImmune;
		potionTimer.slotTimerArray[PotionTimersInter.ANTIFIRE] = fireImmune;
		potionTimer.slotTimerArray[PotionTimersInter.RENEWAL] = prayerRenewalDelay;
	}

	public void resetBarrows() {
		hiddenBrother = -1;
		killedBarrowBrothers = new boolean[7]; // includes new bro for future
		// use
		barrowsKillCount = 0;
	}

	@Override
	public void resetMasks() {
		super.resetMasks();
		temporaryMovementType = -1;
		setUpdateMovementType(false);
		if (!clientHasLoadedMapRegion()) {
			// load objects and items here
			setClientHasLoadedMapRegion();
			refreshSpawnedObjects();
			refreshSpawnedItems();
		}
	}

	@Override
	public boolean restoreHitPoints() {
		boolean update = super.restoreHitPoints();
		if (update) {
			if (prayer.usingPrayer(0, 9))
				super.restoreHitPoints();
			if (resting)
				super.restoreHitPoints();
			refreshHitPoints();
		}
		return update;
	}

	public void restoreRunEnergy() {
		if (runEnergy == 40)
			setRun(true);

		// Only prevent restoration if actually RUNNING (not just moving)
		if ((getRun() && getNextRunDirection() != -1) || runEnergy >= 100)
			return;

		runEnergy++;
		if (runEnergy > 100)
			runEnergy = 100;
		getPackets().sendRunEnergy();
	}

	@Override
	public void sendDeath(final Entity source) {
		/*
		 * if(source instanceof NPC) { NPC src = (NPC) source; src.resetCombat();//try }
		 */
		if (isHCIronMan()) {
			if (getSkills().getTotalLevel(this) <= 500) {
				sm(Colors.green + "[HC Notice] Your stats did not reset because you're total level is to low."
						+ Colors.red + " After you reached total level of and died your stats will reset.</col>");

			}
			if (isDonator()) {
				this.setHCIronMan(false);
				this.setIronMan(true);
				World.sendWorldMessage(Colors.red + "<shad=000000><img=15>[News] " + getDisplayName()
						+ " just died in Hardcore Ironman mode with a skill total of " + getSkills().getTotalLevel(this)
						+ "!", false);
				sm(Colors.green + "[Notice] Your stats did not reset because you're a donator");

			}
			if (getSkills().getTotalLevel(this) >= 500 && !isDonator()) {
				World.sendWorldMessage(Colors.red + "<shad=000000><img=15>[News] " + getDisplayName()
						+ " just died in Hardcore Ironman mode with a skill total of " + getSkills().getTotalLevel(this)
						+ "!", false);
				sm(Colors.red
						+ "[Notice] You died as a Hardcore IronMan, your stats reset and Degrades to Regular IronMan");
				getSkills().resetAllSkills();
				setHCIronMan(false);
				setIronMan(true);
				SerializableFilesManager.savePlayer(this);
			}

		}
		if (prayer.hasPrayersOn() &&

				getTemporaryAttributtes().get("startedDuel") != Boolean.TRUE) {
			if (prayer.usingPrayer(0, 22)) {
				setNextGraphics(new Graphics(437));
				final Player target = this;
				if (isAtMultiArea()) {
					for (int regionId : getMapRegionsIds()) {
						List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
						if (playersIndexes != null) {
							for (int playerIndex : playersIndexes) {
								Player player = World.getPlayers().get(playerIndex);
								if (player == null || !player.isActive() || player.isDead() || player.hasFinished()
										|| !player.withinDistance(this, 1) || !player.isCanPvp()
										|| !target.getControlerManager().canHit(player))
									continue;
								player.applyHit(new Hit(target,
										Utils.getRandom((int) (skills.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
							}
						}
						List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
						if (npcsIndexes != null) {
							for (int npcIndex : npcsIndexes) {
								NPC npc = World.getNPCs().get(npcIndex);
								if (npc == null || npc.isDead() || npc.hasFinished() || !npc.withinDistance(this, 1)
										|| !npc.getDefinitions().hasAttackOption()
										|| !target.getControlerManager().canHit(npc))
									continue;
								npc.applyHit(new Hit(target,
										Utils.getRandom((int) (skills.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
							}
						}
					}
				} else {
					if (source != null && source != this && !source.isDead() && !source.hasFinished()
							&& source.withinDistance(this, 1))
						source.applyHit(
								new Hit(target, Utils.getRandom((int) (skills.getLevelForXp(Skills.PRAYER) * 2.5)),
										HitLook.REGULAR_DAMAGE));
				}
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1, target.getY(), target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1, target.getY(), target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX(), target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX(), target.getY() + 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1, target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() - 1, target.getY() + 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1, target.getY() - 1, target.getPlane()));
						World.sendGraphics(target, new Graphics(438),
								new WorldTile(target.getX() + 1, target.getY() + 1, target.getPlane()));
					}
				});
			} else if (prayer.usingPrayer(1, 17)) {
				World.sendProjectile(this, new WorldTile(getX() + 2, getY() + 2, getPlane()), 2260, 24, 0, 41, 35, 30,
						0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY(), getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() + 2, getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30,
						0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY() + 2, getPlane()), 2260, 41, 0, 41, 35, 30,
						0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY(), getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX() - 2, getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30,
						0);

				World.sendProjectile(this, new WorldTile(getX(), getY() + 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				World.sendProjectile(this, new WorldTile(getX(), getY() - 2, getPlane()), 2260, 41, 0, 41, 35, 30, 0);
				final Player target = this;
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						setNextGraphics(new Graphics(2259));

						if (isAtMultiArea()) {
							for (int regionId : getMapRegionsIds()) {
								List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
								if (playersIndexes != null) {
									for (int playerIndex : playersIndexes) {
										Player player = World.getPlayers().get(playerIndex);
										if (player == null || !player.isActive() || player.isDead()
												|| player.hasFinished() || !player.isCanPvp()
												|| !player.withinDistance(target, 2)
												|| !target.getControlerManager().canHit(player))
											continue;
										player.applyHit(new Hit(target,
												Utils.getRandom((skills.getLevelForXp(Skills.PRAYER) * 3)),
												HitLook.REGULAR_DAMAGE));
									}
								}
								List<Integer> npcsIndexes = World.getRegion(regionId).getNPCsIndexes();
								if (npcsIndexes != null) {
									for (int npcIndex : npcsIndexes) {
										NPC npc = World.getNPCs().get(npcIndex);
										if (npc == null || npc.isDead() || npc.hasFinished()
												|| !npc.withinDistance(target, 2)
												|| !npc.getDefinitions().hasAttackOption()
												|| !target.getControlerManager().canHit(npc))
											continue;
										npc.applyHit(new Hit(target,
												Utils.getRandom((skills.getLevelForXp(Skills.PRAYER) * 3)),
												HitLook.REGULAR_DAMAGE));
									}
								}
							}
						} else {
							if (source != null && source != target && !source.isDead() && !source.hasFinished()
									&& source.withinDistance(target, 2))
								source.applyHit(
										new Hit(target, Utils.getRandom((skills.getLevelForXp(Skills.PRAYER) * 3)),
												HitLook.REGULAR_DAMAGE));
						}

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 2, getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() + 2, getY(), getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 2, getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 2, getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX() - 2, getY(), getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 2, getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX(), getY() + 2, getPlane()));
						World.sendGraphics(target, new Graphics(2260), new WorldTile(getX(), getY() - 2, getPlane()));

						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 1, getY() + 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() + 1, getY() - 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 1, getY() + 1, getPlane()));
						World.sendGraphics(target, new Graphics(2260),
								new WorldTile(getX() - 1, getY() - 1, getPlane()));
					}
				});
			}
		}
		if (getPetManager().isConstitutionPet() && petCanPreventDeathAgain() && !isAtWild() && !isCanPvp()) {
			getPackets().sendGameMessage(
					"<col=109C03>Your pet saves your life and heals you for 25% of your maximum health.");
			heal((int) (getMaxHitpoints() * .25));
			setPetHasSavedPlayer();
			return;
		}
		if (currentInstance != null) {
			currentInstance.removePlayer(this);
			setForceMultiArea(false);
		}
		setNextAnimation(new Animation(-1));
		if (!controlerManager.sendDeath())
			return;
		lock();
		stopAll();
		if (familiar != null)
			familiar.sendDeath(this);
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					setNextAnimation(new Animation(836));
					sendMessage("Oh dear, you have died.");
					if (source instanceof Player) {
						if (familiar != null)
							familiar.sendDeath(source);
						Player killer = (Player) source;
						killer.setAttackedByDelay(4);
						killer = getMostDamageReceivedSourcePlayer();
						/*
						 * if (killer != null) ClanPvPPoints(killer, p);
						 */
					}
				}
				if (loop == 3) {
					setNextAnimation(new Animation(-1));
					getPackets().sendMusicEffect(90);
					// if (!isOwner())
					// getDeathManager().reset();
					// if (safe)
					getDeathManager().setSafe(true);
					/*
					 * if (deathTile != null) getDeathManager().setDeathCoordinates(deathTile);
					 */
					reset();
					unlock();
					setNextAnimation(new Animation(-1));
					// if (!isOwner())
					controlerManager.startControler("DeathEvent");
					/*
					 * else { setNextAnimation(new Animation(-1)); setNextWorldTile(getHomeTile());
					 * }
					 */
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public Animation getDeathAnimation() {
		setNextGraphics(new Graphics(Utils.random(2) == 0 ? 4399 : 4398));
		return new Animation(836);
	}

	public void sendDefaultPlayersOptions() {
		getPackets().sendPlayerOption("Challenge", 1, false);
		getPackets().sendPlayerOption("Follow", 2, false);
		getPackets().sendPlayerOption("Trade with", 4, false);
		getPackets().sendPlayerOption("Examine", 5, false);
	}

	public void sendFriendsChannelMessage(String message) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.sendMessage(this, message);
	}

	public void sendFriendsChannelQuickMessage(QuickChatMessage message) {
		if (currentFriendChat == null)
			return;
		currentFriendChat.sendQuickMessage(this, message);
	}

	public void sendItemsOnDeath(Player killer, boolean dropItems) {
		Integer[][] slots = ButtonHandler.getItemSlotsKeptOnDeath(this, true, hasSkull(),
				getPrayer().usingPrayer(0, 10) || getPrayer().usingPrayer(1, 0));
		sendItemsOnDeath(killer, new WorldTile(this), new WorldTile(this), true, slots);
	}

	public void sendItemsOnDeath(Player killer, WorldTile deathTile, WorldTile respawnTile, boolean wilderness,
			Integer[][] slots) {
		if (isHCIronMan()) {
			if (getSkills().getTotalLevel(this) >= 500)
				World.sendWorldMessage(Colors.red + "<shad=000000><img=11>News: " + getDisplayName()
						+ " just died in Hardcore Ironman mode with a skill total of " + getSkills().getTotalLevel(this)
						+ "!", false);
			this.setHCIronMan(false);
			this.setIronMan(true);
			this.setIntermediate(false);
			this.setEasy(false);
			this.setVeteran(false);
			this.setExpert(false);
			if (this.isDonator()) {
				this.getSkills().resetAllSkills();
				return;
			}
			SerializableFilesManager.savePlayer(this);
			getSession().getChannel().close();
			return;
		}
		// if (killer == null)
		// return;
		if (killer != null) {
			if (killer.isIronMan() || killer.isHCIronMan()) {
				killer.sm("Iron man accounts don't get anything for killing a player.");
				return;
			}
			if (killer.isWiki() || isWiki()) {
				killer.sm("Wiki account cannot participate on any PvP events.");
				return;
			}
		}

		if (!isCanPvp())
			return;
		/*
		 * if (getUsername().equalsIgnoreCase("") ||
		 * killer.getUsername().equalsIgnoreCase("")) return; if
		 * (getUsername().equalsIgnoreCase("") ||
		 * killer.getUsername().equalsIgnoreCase("")) return; if
		 * (getUsername().equalsIgnoreCase("") ||
		 * killer.getUsername().equalsIgnoreCase("")) return;
		 */
		/*
		 * if (killer.isOwner()) {
		 * sendMessage("You didn't loose your items on death due to a Developer killing you."
		 * ); World.addGroundItem(new Item(526, 1), deathTile, 60); return; } if
		 * (isOwner()) {
		 * sendMessage("You didn't loose your items on death due to being an Administrator."
		 * ); World.addGroundItem(new Item(526, 1), deathTile, 60); return; }
		 */
		/*
		 * if (killer.getCurrentMac().equalsIgnoreCase(getCurrentMac())) {
		 * Logger.log("Killer: " + getUsername() + " killed " + killer.getUsername() +
		 * " on same computer."); World.addGroundItem(new Item(526, 1), deathTile, 60);
		 * return; } if
		 * (killer.getSession().getIP().equalsIgnoreCase(getSession().getIP())) {
		 * Logger.log("Killer: " + getUsername() + " killed " + killer.getUsername() +
		 * " on same computer."); World.addGroundItem(new Item(526, 1), deathTile, 60);
		 * return; }
		 */
		charges.die();
		auraManager.removeAura();
		Item[][] items = ButtonHandler.getItemsKeptOnDeath(this, slots);
		inventory.reset();
		equipment.reset();
		getGlobalPlayerUpdater().generateAppearenceData();
		for (Item item : items[0]) {
			if (!ItemConstants.keptOnDeath(item))
				World.addGroundItem(item, deathTile, this, true, 60);
			else
				inventory.addItem(item.getId(), item.getAmount());
		}
		
		World.addGroundItem(new Item(526, 1), deathTile,
				killer == null ? this : (killer.isIronMan() || killer.isHCIronMan()) ? this : killer, true, 60);
		if (items[1].length != 0) {
			for (Item item : items[1]) {
				if (ItemConstants.keptOnDeath(item)) {
					getInventory().addItem(item);
					continue;
				}
				if (ItemConstants.degradeOnDrop(item))
					getCharges().degradeCompletly(item);
				if (ItemConstants.removeAttachedId(item) != -1) {
					if (ItemConstants.removeAttachedId2(item) != -1)
						if (killer.getControlerManager().getControler() == null
						/*
						 * || !(killer.getControlerManager().getControler() instanceof
						 * InstancedPVPControler)
						 */)
							World.updateGroundItem(new Item(ItemConstants.removeAttachedId2(item), 1), deathTile,
									killer == null || killer.isIronMan() || killer.isHCIronMan() ? this : killer, 60,
									1);
					item.setId(ItemConstants.removeAttachedId(item));
				}
				if (ItemConstants.turnCoins(item) && (isAtWild() || FfaZone.inPvpArea(this))) {
					int price = GrandExchange.getPrice(item.getId()) / 4;
					item.setId(995);
					item.setAmount(price);
				}
				World.updateGroundItem(item, deathTile,
						killer == null ? this : (killer.isIronMan() || killer.isHCIronMan()) ? this : killer, 60, 1,
						false);
			}
		}
	}

	public final boolean isAtWild() {
		return (getX() >= 3011 && getX() <= 3132 && getY() >= 10052 && getY() <= 10175)
				|| (getX() >= 2940 && getX() <= 3395 && getY() >= 3525 && getY() <= 4000)
				|| (getX() >= 3264 && getX() <= 3279 && getY() >= 3279 && getY() <= 3672)
				|| (getX() >= 3158 && getX() <= 3181 && getY() >= 3679 && getY() <= 3697)
				|| (getX() >= 3280 && getX() <= 3183 && getY() >= 3885 && getY() <= 3888)
				|| (getX() >= 3012 && getX() <= 3059 && getY() >= 10303 && getY() <= 10351)
				|| (getX() >= 3060 && getX() <= 3072 && getY() >= 10251 && getY() <= 10263);
	}

	public void sendMessage(String message, boolean filter) {
		getPackets().sendGameMessage(message, filter);
	}

	public void sendMessage(String message) {
		getPackets().sendGameMessage(message);
	}

	public void sendPublicChatMessage(PublicChatMessage message) {
		for (int regionId : getMapRegionsIds()) {
			List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player p = World.getPlayers().get(playerIndex);
				if (p == null || !p.isActive() || p.hasFinished()
						|| p.getLocalPlayerUpdate().getLocalPlayers()[getIndex()] == null)
					continue;
				p.getPackets().sendPublicMessage(this, message);
			}
		}
	}

	public void sendRunButtonConfig() {
		getPackets().sendConfig(173, resting ? 3 : getRun() ? 1 : 0);
	}

	public void sendSoulSplit(final Hit hit, final Entity user, int healPercent) {
		final Player target = this;

		// Visual effect
		if (hit.getDamage() > 0) {
			World.sendProjectile(user, this, 2263, 11, 11, 20, 5, 0, 0);
		}

		// Calculate healing
		int healAmount = (int) ((double) hit.getDamage() * (healPercent / 100.0));

		// Cap to max HP
		if (healAmount > 0) {
			int effectiveHeal = Math.min(healAmount, user.getMaxHitpoints() - user.getHitpoints());
			if (effectiveHeal > 0) {
				user.heal(effectiveHeal);
			}
		}

		// Prayer drain
		prayer.drainPrayer(hit.getDamage() / 10);

		// Delayed visual feedback
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				setNextGraphics(new Graphics(2264));
				if (hit.getDamage() > 0) {
					World.sendProjectile(target, user, 2263, 11, 11, 20, 5, 0, 0);
				}
			}
		}, 0);
	}

	/**
	 * Overloaded version: defaults to 20% healing.
	 */
	public void sendSoulSplit(final Hit hit, final Entity user) {
		sendSoulSplit(hit, user, 20);
	}

	public void sendUnlockedObjectConfigs() {
		refreshKalphiteLairEntrance();
		refreshKalphiteLair();
		refreshLodestoneNetwork();
		refreshFightKilnEntrance();
	}

	public void setBanned(long banned) {
		this.banned = banned;
	}

	public int setBarrowsKillCount(int barrowsKillCount) {
		return this.barrowsKillCount = barrowsKillCount;
	}

	public void incrementBarrowsRunsDone() {
		this.barrowsRunsDone++;
	}

	public void setCanPvp(boolean canPvp) {
		this.canPvp = canPvp;
		getGlobalPlayerUpdater().generateAppearenceData();
		getPackets().sendPlayerOption(canPvp ? "Attack" : "null", 1, true);
		getPackets().sendPlayerUnderNPCPriority(canPvp);
	}

	public void setCantTrade(boolean canTrade) {
		this.cantTrade = canTrade;
	}

	public void setCastVeng(boolean castVeng) {
		this.castedVeng = castVeng;
	}

	public void setClanStatus(int clanStatus) {
		this.clanStatus = clanStatus;
	}

	public void setClientHasLoadedMapRegion() {
		clientLoadedMapRegion = true;
	}

	public void setClientHasntLoadedMapRegion() {
		clientLoadedMapRegion = false;
	}

	public void setCloseInterfacesEvent(Runnable closeInterfacesEvent) {
		this.closeInterfacesEvent = closeInterfacesEvent;
	}

	public void setClueReward(int clueReward) {
	}

	public void setCompletedFightCaves() {
		if (!completedFightCaves) {
			completedFightCaves = true;
			refreshFightKilnEntrance();
		}
	}

	public void setCompletedFightCaves2() {
		completedFightCaves = true;
	}

	public void setCompletedFightKiln() {
		completedFightKiln = true;
	}

	public void setCompletedRfd() {
		completedRfd = true;
	}

	public void setCompletionistCapeCustomized(int[] skillcapeCustomized) {
		this.completionistCapeCustomized = skillcapeCustomized;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public void setCurrentFriendChat(FriendChatsManager currentFriendChat) {
		this.currentFriendChat = currentFriendChat;
	}

	public void setCurrentFriendChatOwner(String currentFriendChatOwner) {
		this.currentFriendChatOwner = currentFriendChatOwner;
	}

	public void setCurrentMac(String currentMac) {
		this.currentMac = currentMac;
	}

	public int setDeathCount(int deathCount) {
		return this.deathCount = deathCount;
	}

	public void setDefenderRoom(boolean isInDefenderRoom) {
		this.isInDefenderRoom = isInDefenderRoom;
	}

	public void setDisableEquip(boolean equip) {
		disableEquip = equip;
	}

	public void setDisplayMode(int displayMode) {
		this.displayMode = displayMode;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setDonator(boolean donator) {
		this.donator = donator;
	}

	public void setEmailAttached(String email) {
	}

	public void setFamiliar(Familiar familiar) {
		this.familiar = familiar;
	}

	public void setFightPitsSkull() {
		skullDelay = Integer.MAX_VALUE;
		skullId = 1;
		getGlobalPlayerUpdater().generateAppearenceData();
	}

	public void setFilterGame(boolean filterGame) {
		this.filterGame = filterGame;
	}

	public void setForceNextMapLoadRefresh(boolean forceNextMapLoadRefresh) {
		this.forceNextMapLoadRefresh = forceNextMapLoadRefresh;
	}

	public void setFriendChatSetup(int friendChatSetup) {
		this.friendChatSetup = friendChatSetup;
	}

	public void setHiddenBrother(int hiddenBrother) {
		this.hiddenBrother = hiddenBrother;
	}

	public void setHideWorldMessages(boolean hideWorldAnnouncements) {
		this.hideWorldAnnouncements = hideWorldAnnouncements;
	}

	public void setHpBoostMultiplier(double hpBoostMultiplier) {
		this.hpBoostMultiplier = hpBoostMultiplier;
	}

	public void setInAnimationRoom(boolean inAnimationRoom) {
		this.inAnimationRoom = inAnimationRoom;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public void setIsInLobby(boolean isInLobby) {
	}

	public void setJailed(long jailed) {
		this.jailed = jailed;
	}

	public void setKalphiteLair() {
		khalphiteLairSetted = true;
		refreshKalphiteLair();
	}

	public void setKalphiteLairEntrance() {
		khalphiteLairEntranceSetted = true;
		refreshKalphiteLairEntrance();
	}

	public int setKillCount(int killCount) {
		return this.killCount = killCount;
	}

	public int setDropRate(double d) {
		return (int) (this.dropRate = d);
	}

	public int setTotalKillStreakPoints(int totalkillStreakPoints) {
		return this.totalkillStreakPoints = totalkillStreakPoints;
	}

	public int setKillStreakPoints(int killStreakPoints) {
		return this.killStreakPoints = killStreakPoints;
	}

	public void setKilledAgrithNaNa(boolean agrithNaNa) {
		this.agrithNaNa = agrithNaNa;
	}

	public void setKilledBork(boolean killedBork) {
		this.killedBork = killedBork;
	}

	public void setKilledCulinaromancer(boolean culinaromancer) {
		this.culinaromancer = culinaromancer;
	}

	public void setKilledDessourt(boolean dessourt) {
		this.dessourt = dessourt;
	}

	public void setKilledFlamBeed(boolean flamBeed) {
		this.flamBeed = flamBeed;
	}

	public void setKilledKaramel(boolean karamel) {
		this.karamel = karamel;
	}

	/**
	 * Sets the killedQueenBlackDragon.
	 * 
	 * @param killedQueenBlackDragon The killedQueenBlackDragon to set.
	 */
	public void setKilledQueenBlackDragon(boolean killedQueenBlackDragon) {
		this.killedQueenBlackDragon = killedQueenBlackDragon;
	}

	public void setLargeSceneView(boolean largeSceneView) {
		this.largeSceneView = largeSceneView;
	}

	public void setLastBonfire(int lastBonfire) {
		this.lastBonfire = lastBonfire;
	}

	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}

	public void setLastPublicMessage(long lastPublicMessage) {
		this.lastPublicMessage = lastPublicMessage;
	}

	public void setMaxedCapeCustomized(int[] maxedCapeCustomized) {
		this.maxedCapeCustomized = maxedCapeCustomized;
	}

	public void setMoneyPouchValue(int money) {
		this.money = money;
	}

	public void setMuted(long muted) {
		this.muted = muted;
	}

	public void setPacketsDecoderPing(long packetsDecoderPing) {
		this.packetsDecoderPing = packetsDecoderPing;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPermBanned(boolean permBanned) {
		this.permBanned = permBanned;
	}

	public void setPermMuted(boolean permMuted) {
		this.permMuted = permMuted;
	}

	public void setPestControlGames(int pestControlGames) {
		this.pestControlGames = pestControlGames;
	}

	public void setPestPoints(int pestPoints) {
		this.pestPoints = pestPoints;
	}

	/**
	 * Sets the pet.
	 * 
	 * @param pet The pet to set.
	 */
	public void setPet(Pet pet) {
		this.pet = pet;
	}

	/**
	 * Sets the petManager.
	 * 
	 * @param petManager The petManager to set.
	 */
	public void setPetManager(PetManager petManager) {
		this.petManager = petManager;
	}

	public void setPkPoints(int pkPoints) {
		this.pkPoints = pkPoints;
	}

	public void setPolDelay(long delay) {
		this.polDelay = delay;
	}

	public void setBloodDelay(long delay) {
		this.bloodDelay = delay;
	}

	public void setPrayerDelay(long teleDelay) {
		getTemporaryAttributtes().put("PrayerBlocked", teleDelay + Utils.currentTimeMillis());
		prayer.closeAllPrayers();
	}

	public void setOverloadDelay(int overloadDelay) {
		this.overloadDelay = overloadDelay;
		potionTimer.slotTimerArray[PotionTimersInter.OVL] = (long) (overloadDelay * 0.6 * 1000
				+ Utils.currentTimeMillis());

	}

	public void setPrayerRenewalDelay(int delay) {
		this.prayerRenewalDelay = delay;
		potionTimer.slotTimerArray[PotionTimersInter.RENEWAL] = (long) (prayerRenewalDelay * 0.6 * 1000
				+ Utils.currentTimeMillis());
	}

	public void addFireImmune(long time) {
		fireImmune = time + Utils.currentTimeMillis();
		potionTimer.slotTimerArray[PotionTimersInter.ANTIFIRE] = fireImmune;
	}

	public void setPrivateChatSetup(int privateChatSetup) {
		this.privateChatSetup = privateChatSetup;
	}

	public void setPublicStatus(int publicStatus) {
		this.publicStatus = publicStatus;
	}

	public void setRegisteredMac(String registeredMac) {
		this.registeredMac = registeredMac;
	}

	public void setResting(boolean resting) {
		this.resting = resting;
		sendRunButtonConfig();
	}

	public void setRights(int rights) {
		this.rights = rights;
	}

	@Override
	public void setRun(boolean run) {
		if (run != getRun()) {
			super.setRun(run);
			setUpdateMovementType(true);
			sendRunButtonConfig();
		}
	}

	public void setRunEnergy(int runEnergy) {
		this.runEnergy = (byte) runEnergy;
		getPackets().sendRunEnergy();
	}

	public void errorMessage(String msg) {
		getPackets().sendGameMessage("<col=ff0000>" + msg);
	}

	public void succeedMessage(String msg) {
		getPackets().sendGameMessage(msg);
	}

	public void yell(String msg) {
		World.sendWorldMessage(msg, false);
	}

	/**
	 * @param runeSpanPoint the runeSpanPoint to set
	 */
	public void setRuneSpanPoint(int runeSpanPoints) {
		this.runeSpanPoints = runeSpanPoints;
	}

	public void setRunHidden(boolean run) {
		super.setRun(run);
		setUpdateMovementType(true);
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public int setSkullDelay(int delay) {
		return this.skullDelay = delay;
	}

	public void setSkullId(int skullId) {
		this.skullId = skullId;
	}

	public void setSkullInfiniteDelay(int skullId) {
		skullDelay = Integer.MAX_VALUE;
		this.skullId = skullId;
		getGlobalPlayerUpdater().generateAppearenceData();
	}

	public void setSlayerPoints(int slayerPoints) {
		this.slayerPoints = slayerPoints;
	}

	public void setSlayerPoints2(int slayerPoints2) {
		this.slayerPoints2 = slayerPoints2;
	}

	public void setSpawnsMode(boolean spawnsMode) {
		this.spawnsMode = spawnsMode;
	}

	public void setSpecRestoreTimer(int specRestoreTimer) {
		this.specRestoreTimer = specRestoreTimer;
	}

	public void setSummoningLeftClickOption(int summoningLeftClickOption) {
		this.summoningLeftClickOption = summoningLeftClickOption;
	}

	public void setSwitchItemCache(List<Integer> switchItemCache) {
		this.switchItemCache = switchItemCache;
	}

	public void setTalkedToCook() {
		talkedtoCook = true;
	}

	public void setTalkedWithMarv() {
		talkedWithMarv = true;
	}

	public void setTalkedWithVannaka(boolean talkedWithVannaka) {
		this.talkedWithVannaka = talkedWithVannaka;
	}

	/**
	 * @param task the task to set
	 */
	public void setTask(SlayerTask task) {
		this.task = task;
	}

	public void setRTask(Contract rtask) {
		this.Rtask = rtask;
	}

	public void setTeleBlockDelay(long teleDelay) {
		getTemporaryAttributtes().put("TeleBlocked", teleDelay + Utils.currentTimeMillis());
	}

	public void setTradeStatus(int tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public void setUpdateMovementType(boolean updateMovementType) {
		this.updateMovementType = updateMovementType;
	}

	/*
	 * do not use this, only used by pm
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	public void setVecnaTimer(int vecnaTimer) {
		this.vecnaTimer = vecnaTimer;
	}

	public void setVotePoints(int votePoints) {
		this.votePoints = votePoints;
	}

	public void setHweenPoints(int HweenPoints) {
		this.HweenPoints = HweenPoints;
	}

	public void setTuskenPoints(int TuskenPoints) {
		this.TuskenPoints = TuskenPoints;
	}

	public void setElitePoints(int ElitePoints) {
		this.ElitePoints = ElitePoints;
	}

	public void setStarfirePoints(int StarfirePoints) {
		this.StarfirePoints = StarfirePoints;
	}

	public void setWildernessSkull() {
		skullDelay = 3000;
		skullId = 0;
		getGlobalPlayerUpdater().generateAppearenceData();
	}

	public void setWonFightPits() {
		wonFightPits = true;
	}

	public void setXpLocked(boolean locked) {
		this.xpLocked = locked;
	}

	public void setYellColor(String yellColor) {
		this.yellColor = yellColor;
	}

	public void setYellDelay(long l) {
		yellDelay = l;
	}

	public void setYellDisabled(boolean yellDisabled) {
		this.yellDisabled = yellDisabled;
	}

	public void setYellOff(boolean yellOff) {
		this.yellOff = yellOff;
	}

	// now that we inited we can start showing game
	public void start() {
		loadMapRegions();
		LoginManager.sendLogin(this);
		if (isDead() || getHitpoints() <= 0)
			sendDeath(null);
		hasCompleted();
		setActive(true);
		
	}

	public void stopAll() {
		stopAll(true);

	}

	public void stopAll(boolean stopWalk) {
		stopAll(stopWalk, true);
		closeInterfaces();
	}

	public void stopAll(boolean stopWalk, boolean stopInterface) {
		stopAll(stopWalk, stopInterface, true);
	}

	// as walk done clientsided
	public void stopAll(boolean stopWalk, boolean stopInterfaces, boolean stopActions) {
		routeEvent = null;
		if (stopInterfaces)
			closeInterfaces();
		if (stopWalk && !cantWalk)
			resetWalkSteps();
		if (stopActions)
			actionManager.forceStop();
		combatDefinitions.resetSpells(false);
	}

	public void switchAllowChatEffects() {
		allowChatEffects = !allowChatEffects;
		refreshAllowChatEffects();
	}

	public void switchMouseButtons() {
		mouseButtons = !mouseButtons;
		refreshMouseButtons();
	}

	public void switchReportOption() {
		reportOption = !reportOption;
		refreshReportOption();
	}

	public void refreshReportOption() {
		getPackets().sendConfig(1056, isUsingReportOption() ? 2 : 0);
	}

	public boolean isToogleLootShare() {
		return toogleLootShare;
	}

	public void disableLootShare() {
		if (isToogleLootShare())
			toogleLootShare();
	}

	public boolean atThroneArea() {
		int destX = getX();
		int destY = getY();
		if (destX >= 3061 && destY >= 3545 && destX <= 3111 && destY <= 3595) {
			return true;
		}
		return false;
	}

	public void toogleLootShare() {
		this.toogleLootShare = !toogleLootShare;
		refreshToogleLootShare();
	}

	public void refreshToogleLootShare() {
		// need to force cuz autoactivates when u click on it even if no chat
		VBM.forceSendVarBit(4071, toogleLootShare ? 1 : 0);
	}

	public void toogleRun(boolean update) {
		super.setRun(!getRun());
		setUpdateMovementType(true);
		if (update)
			sendRunButtonConfig();
	}

	public void unlock() {
		lockDelay = 0;
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay) {
		useStairs(emoteId, dest, useDelay, totalDelay, null);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message) {
		useStairs(emoteId, dest, useDelay, totalDelay, message, false);
	}

	public void useStairs(int emoteId, final WorldTile dest, int useDelay, int totalDelay, final String message,
			final boolean resetAnimation) {
		stopAll();
		lock(totalDelay);
		if (emoteId != -1)
			setNextAnimation(new Animation(emoteId));
		if (useDelay == 0)
			setNextWorldTile(dest);
		else {
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (isDead())
						return;
					if (resetAnimation)
						setNextAnimation(new Animation(-1));
					setNextWorldTile(dest);
					if (message != null)
						getPackets().sendGameMessage(message);
				}
			}, useDelay - 1);
		}
	}

	public void vecnaTimer(int amount) {
		if (getVecnaTimer() > 0) {
			CoresManager.fastExecutor.schedule(new TimerTask() {
				@Override
				public void run() {
					if (hasFinished())
						cancel();
					if (getVecnaTimer() > 0)
						setVecnaTimer(getVecnaTimer() - 1);
					if (getVecnaTimer() == 0) {
						getPackets()
								.sendGameMessage("<col=FFCC00>Your skull of Vecna has regained its mysterious aura.");
						cancel();
					}
				}
			}, 10, 1);
		}
	}

	public int lendMessage;

	private transient RouteEvent routeEvent;

	public void setRouteEvent(RouteEvent routeEvent) {
		this.routeEvent = routeEvent;
	}

	public ClansManager getClanManager() {
		return clanManager;
	}

	public void setClanManager(ClansManager clanManager) {
		this.clanManager = clanManager;
	}

	public String getClanName() {
		return clanName;
	}

	public void setClanName(String clanName) {
		this.clanName = clanName;
	}

	public boolean isConnectedClanChannel() {
		return connectedClanChannel;
	}

	public void setConnectedClanChannel(boolean connectedClanChannel) {
		this.connectedClanChannel = connectedClanChannel;
	}

	public ClansManager getGuestClanManager() {
		return guestClanManager;
	}

	public void setGuestClanManager(ClansManager guestClanManager) {
		this.guestClanManager = guestClanManager;
	}

	public void sendClanChannelMessage(ChatMessage message) {
		if (clanManager == null)
			return;
		clanManager.sendMessage(this, message);
	}

	public void sendGuestClanChannelMessage(ChatMessage message) {
		if (guestClanManager == null)
			return;
		guestClanManager.sendMessage(this, message);
	}

	public void sendClanChannelQuickMessage(QuickChatMessage message) {
		if (clanManager == null)
			return;
		clanManager.sendQuickMessage(this, message);
	}

	public void sendGuestClanChannelQuickMessage(QuickChatMessage message) {
		if (guestClanManager == null)
			return;
		guestClanManager.sendQuickMessage(this, message);
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	private double[] warriorPoints;

	public double[] getWarriorPoints() {
		return warriorPoints;
	}

	public void setWarriorPoints(int index, double pointsDifference) {
		warriorPoints[index] += pointsDifference;
		if (warriorPoints[index] < 0) {
			Controller controler = getControlerManager().getControler();
			if (controler == null || !(controler instanceof WarriorsGuild))
				return;
			WarriorsGuild guild = (WarriorsGuild) controler;
			guild.inCyclopse = false;
			setNextWorldTile(WarriorsGuild.CYCLOPS_LOBBY);
			warriorPoints[index] = 0;
		} else if (warriorPoints[index] > 65535)
			warriorPoints[index] = 65535;
		refreshWarriorPoints(index);
	}

	public void refreshWarriorPoints(int index) {
		getPackets().sendConfigByFile(index + 8662, (int) warriorPoints[index]);
	}

	public void warriorCheck() {
		if (warriorPoints == null || warriorPoints.length != 6)
			warriorPoints = new double[6];
	}

	public void setExtremeDonator(boolean extremeDonator) {
		this.extremeDonator = extremeDonator;
	}

	public GlobalPlayerUpdater getGlobalPlayerUpdater() {
		return globalPlayerUpdater;
	}

	public void setGlobalPlayerUpdater(GlobalPlayerUpdater globalPlayerUpdater) {
		this.globalPlayerUpdater = globalPlayerUpdater;
	}

	private long thievingDelay;

	public long getThievingDelay() {
		return thievingDelay;
	}

	public void setThievingDelay(long thievingDelay) {
		this.thievingDelay = thievingDelay;
	}

	public Channel getChannel() {
		return channel;
	}

	public boolean isDeveloper() {
		return isOwner() || isDev() ? true : false;
	}

	/**
	 * Custom Game Mode ranks.
	 */
	public boolean veteran, intermediate, easy, ironman, hcironman, expert;

	public boolean isVeteran() {
		return veteran;
	}

	public boolean isIntermediate() {
		return intermediate;
	}

	public boolean isEasy() {
		return easy;
	}

	public boolean isIronMan() {
		return ironman;
	}

	public boolean isHCIronMan() {
		return hcironman;
	}

	public boolean isExpert() {
		return expert;
	}

	public void setVeteran(boolean vet) {
		this.veteran = vet;
	}

	public void setIntermediate(boolean interm) {
		this.intermediate = interm;
	}

	public void setEasy(boolean ez) {
		this.easy = ez;
	}

	public void setIronMan(boolean ironm) {
		this.ironman = ironm;
	}

	public void setHCIronMan(boolean hardcoreim) {
		this.hcironman = hardcoreim;
	}

	public void setExpert(boolean expert) {
		this.expert = expert;
	}

	public void setContract(Contract contract) {
		this.cContracts = contract;
	}

	public Contract getContract() {
		return cContracts;
	}

	/**
	 * New player starter stuff.
	 */
	public boolean hasCompleted, hasLogedIn;

	public void setCompleted() {
		getHintIconsManager().removeUnsavedHintIcon();
		this.hasCompleted = true;
	}

	public void setHasCompleted(boolean hasCompleted) {
		this.hasCompleted = hasCompleted;
	}

	public boolean hasCompleted() {
		return hasCompleted;
	}

	public void setLogedIn() {
		this.hasLogedIn = true;
	}

	public boolean hasLogedIn() {
		return hasLogedIn;
	}

	/**
	 * Handles a donation made.
	 * 
	 * @param price The donation price.
	 */
	public void handleDonation(int price, String perk) {

		try {
			BufferedWriter bf = new BufferedWriter(new FileWriter("data/playersaves/logs/donationLogs.txt", true));
			bf.write("[Player: " + getDisplayName() + ", on " + DateFormat.getDateTimeInstance().format(new Date())
					+ "]: has donated: " + price + "$ for " + perk + ".");
			bf.newLine();
			bf.flush();
			bf.close();

		} catch (IOException ignored) {
			Logger.log("LoggsHandler", "Failed saving Donation Logs...");
		}

		getInterfaceManager().closeChatBoxInterface();
		setMoneySpent(getMoneySpent() + price);
		if (!isStaff())
			setDonationAmountWeekly(getDonationAmountWeekly() + price);
		DonationRank.checkRank(this);
		Donations.HandlePromotion(this, price);
		Discord.sendAchievement("[Donation] " + getUsername() + " Donated for [" + perk + "] and has now a total of $"
				+ getMoneySpent() + " Donation.");

		World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + getUsername() + " </col> Donated for ["
				+ Colors.yellow + perk + "</col>] and has now a total of " + Colors.red + "$" + getMoneySpent()
				+ " Donation.", false);
	}

	/**
	 * IRL Money spent.
	 */
	private int moneySpent;

	public void setMoneySpent(int money) {
		this.moneySpent = money;
	}

	public int getMoneySpent() {
		return moneySpent;
	}

	/**
	 * Used to take coins.
	 * 
	 * @param amount the Amount to take.
	 * @return if The money has been taken.
	 */
	public boolean takeMoney(int amount) {
		if (!hasMoney(amount)) {
			return false;
		}
		if (amount < 0) {
			return false;
		}
		int inPouch = getMoneyPouch().getTotal();
		int inInventory = getInventory().getNumerOf(995);
		if (inPouch >= amount) {
			getMoneyPouch().removeMoneyMisc(amount);
			return true;
		}
		if (inInventory >= amount) {
			getInventory().deleteItem(new Item(995, amount));
			return true;
		}
		if (inPouch + inInventory >= amount) {
			amount = amount - inPouch;
			getMoneyPouch().removeMoneyMisc(inPouch);
			getInventory().deleteItem(new Item(995, amount));
			return true;
		}
		return false;
	}

	/**
	 * Used to add coins.
	 * 
	 * @param amount the Amount to add.
	 * @return if The money has been added.
	 */
	public void addMoney(int amount) {
		if (money + amount < 0) {
			int amountPouch = Integer.MAX_VALUE - money;
			amount = amount - amountPouch;
			if (getInventory().hasFreeSlots() || getInventory().containsItem(995, 1)) {
				int has = getInventory().getNumerOf(995);
				if (has + amount < 0) {
					int amountAdd = Integer.MAX_VALUE - has;
					int toDrop = amount - amountAdd;
					getInventory().addItem(995, amountAdd);
					World.addGroundItem(new Item(995, toDrop), new WorldTile(this), this, true, 60);
					sendMessage(Colors.red + Utils.getFormattedNumber(toDrop)
							+ " coins have been dropped due to insufficient coin inventory space.");
					return;
				}
				getInventory().addItem(995, amount);
				return;
			}
			sendMessage(Colors.red + Utils.getFormattedNumber(amount)
					+ " coins have been dropped due to insufficient coin inventory space.");
			World.addGroundItem(new Item(995, amount), new WorldTile(this), this, true, 60);
			return;
		}
		getMoneyPouch().addMoney(amount, false);
	}

	/**
	 * Used for checking if the player has money.
	 * 
	 * @param amount the Amount to check for.
	 * @return if the player has the required amount either in their money pouch or
	 *         their inventory.
	 */
	public boolean hasMoney(int amount) {
		int money = getInventory().getNumerOf(995) + getMoneyPouch().getTotal();
		return money >= amount;
	}

	/**
	 * Varbit manager.
	 */
	public transient VarBitManager VBM;

	public VarBitManager getVarBitManager() {
		return VBM;
	}

	/**
	 * Farming.
	 */
	public FarmingManager farmingManager;

	public FarmingManager getFarmingManager() {
		return farmingManager;
	}

	/**
	 * LodeStones.
	 */
	public boolean[] lodestone;

	public void activateLodeStone(final WorldObject object, final Player p) {
		lock(5);
		WorldTasksManager.schedule(new WorldTask() {
			int count = 0;

			@Override
			public void run() {
				if (count == 0) {
					getPackets().sendCameraPos(Cutscene.getX(p, p.getX() - 6), Cutscene.getY(p, p.getY()), 3000);
					getPackets().sendCameraLook(Cutscene.getX(p, object.getX()), Cutscene.getY(p, object.getY()), 50);
					getPackets().sendGraphics(new Graphics(3019), object);
				}
				if (count == 2) {
					getPackets().sendResetCamera();
					lodestone[object.getId() - 69827] = true;
					refreshLodestoneNetwork();
				}
				if (count == 3) {
					unlock();
					stop();
				}
				count++;
			}
		}, 0, 1);
	}

	/**
	 * Grand Exchange.
	 */
	public GrandExchangeManager geManager;

	public GrandExchangeManager getGEManager() {
		return geManager;
	}

	/**
	 * Gets the XP mode.
	 * 
	 * @return the XP mode.
	 */
	public String getXPMode() {
		if (isExpert())
			return "Expert";
		if (isVeteran())
			return "Veteran";
		if (isIntermediate())
			return "Intermediate";
		if (isEasy())
			return "Easy";
		if (isIronMan())
			return "Ironman";
		if (isHCIronMan())
			return "HC Ironman";
		return "HACKER";
	}

	/**
	 * Gets the Drop rate.
	 * 
	 * @return the Drop rate.
	 */
	public double getDropRate() {
		if (isVeteran())
			return Settings.VET_DROP;
		if (isIntermediate())
			return Settings.INTERM_DROP;
		if (isEasy())
			return Settings.EASY_DROP;
		if (isIronMan())
			return Settings.IRONMAN_DROP;
		if (isHCIronMan())
			return Settings.HCIRONMAN_DROP;
		return 1;
	}

	/**
	 * Checks if the Player has the item.
	 * 
	 * @param item The item to check.
	 * @return if has item or not.
	 */
	public boolean hasItem(Item item) {
		if (getInventory().containsItem(item))
			return true;
		if (getEquipment().getItemsContainer().contains(item))
			return true;
		if (getBank().getItem(item.getId()) != null)
			return true;
		return false;
	}

	/**
	 * Gives the Player an item.
	 * 
	 * @param item The item to give.
	 */
	public void addItem(Item item) {
		if (!getInventory().hasFreeSlots()
				&& !(item.getDefinitions().isStackable() && getInventory().containsOneItem(item.getId()))) {
			if (!getBank().hasBankSpace())
				World.updateGroundItem(item, this, this, 60, 0);
			else {
				if (item.getDefinitions().isNoted())
					item.setId(item.getDefinitions().getCertId());
				getBank().addItem(item.getId(), item.getAmount(), true);
				sendMessage(item.getName() + " has been added to your bank account.");
			}
		} else
			getInventory().addItem(item);
	}

	/**
	 * Used to display players icon (if any).
	 */
	public String getIcon() {
		if (getRights() == 2)
			return "<img=1>";
		if (getRights() == 1)
			return "<img=0>";
		if (isSupport())
			return "<img=13>";
		if (isHCIronMan())
			return "<img=15>";
		if (isIronMan())
			return "<img=14>";
		if (isSilver())
			return "<img=10>";
		if (isBronze())
			return "<img=9>";
		if (isGold())
			return "<img=8>";
		if (isPlatinum())
			return "<img=12>";
		if (isDiamond())
			return "<img=19>";
		if (isForumManager())
			return "<img=21>";
		if (isCommunityManager())
			return "<img=22>";
		if (isDicer())
			return "<img=20>";
		return "";
	}

	/**
	 * Completionist Cape requirements.
	 */
	private int oresMined, barsSmelt;

	public void addOresMined() {
		this.oresMined++;
	}

	public int getOresMined() {
		return oresMined;
	}

	public void addBarsSmelt() {
		this.barsSmelt++;
	}

	public int getBarsSmelt() {
		return barsSmelt;
	}

	private int logsChopped, logsBurned;

	public void addLogsChopped() {
		this.logsChopped++;
	}

	public int getLogsChopped() {
		return logsChopped;
	}

	public void addLogsBurned() {
		this.logsBurned++;
	}

	public int getLogsBurned() {
		return logsBurned;
	}

	private int lapsRan;

	public void addLapsRan() {
		this.lapsRan++;
	}

	public int getLapsRan() {
		return lapsRan;
	}

	private int bonesOffered;

	public void addBonesOffered() {
		this.bonesOffered++;
	}

	public int getBonesOffered() {
		return bonesOffered;
	}

	private int potionsMade;

	public void addPotionsMade() {
		this.potionsMade++;
	}

	public int getPotionsMade() {
		return potionsMade;
	}

	private int timesStolen;

	public void addTimesStolen() {
		this.timesStolen++;
	}

	public int getTimesStolen() {
		return timesStolen;
	}

	private int itemsMade;

	public void addItemsMade() {
		this.itemsMade++;
	}

	public int getItemsMade() {
		return itemsMade;
	}

	private int itemsFletched;

	public void addItemsFletched() {
		this.itemsFletched++;
	}

	public int getItemsFletched() {
		return itemsFletched;
	}

	private int creaturesCaught;

	public void addCreaturesCaught() {
		this.creaturesCaught++;
	}

	public int getCreaturesCaught() {
		return creaturesCaught;
	}

	private int fishCaught;

	public void addFishCaught(boolean perk) {
		this.fishCaught += perk ? 2 : 1;
	}

	public int getFishCaught() {
		return fishCaught;
	}

	private int foodCooked;

	public void addFoodCooked() {
		this.foodCooked++;
	}

	public int getFoodCooked() {
		return foodCooked;
	}

	public int produceGathered;

	public void addProduceGathered() {
		this.produceGathered++;
	}

	public int getProduceGathered() {
		return produceGathered;
	}

	public int pouchesMade;

	public void setPouchesMade(int pouches) {
		this.pouchesMade = pouches;
	}

	public void setTimesStolen(int stolen) {
		this.timesStolen = stolen;
	}

	public void setPotionsMade(int potionsMade) {
		this.potionsMade = potionsMade;
	}

	public void setBonesOffered(int bonesOffered) {
		this.bonesOffered = bonesOffered;
	}

	public void setLogsBurned(int logsBurned) {
		this.logsBurned = logsBurned;
	}

	public void setLogsChopped(int logsChopped) {
		this.logsChopped = logsChopped;
	}

	public void setBarsSmelted(int barsSmelted) {
		this.barsSmelt = barsSmelted;
	}

	public void setOresMined(int oresMined) {
		this.oresMined = oresMined;
	}

	public void setItemsFletched(int itemsFletched) {
		this.itemsFletched = itemsFletched;
	}

	public void setFishCaught(int fishCaught) {
		this.fishCaught = fishCaught;
	}

	public void setFoodCooked(int foodCooked) {
		this.foodCooked = foodCooked;
	}

	public void setProduceGathered(int produceGathered) {
		this.produceGathered = produceGathered;
	}

	public void setLapsRan(int lapsRan) {
		this.lapsRan = lapsRan;
	}

	public void setMemoriesCollected(int memoriesCollected) {
		this.memoriesCollected = memoriesCollected;
	}

	public void setRunesMade(int runesMade) {
		this.runesMade = runesMade;
	}

	public void setCreaturesCaught(int creaturesCaught) {
		this.creaturesCaught = creaturesCaught;
	}

	public void setItemsMade(int items) {
		this.itemsMade = items;
	}

	public int getPouchesMade() {
		return pouchesMade;
	}

	private int memoriesCollected;

	public int getMemoriesCollected() {
		return memoriesCollected;
	}

	public void addMemoriesCollected() {
		this.memoriesCollected++;
	}

	private int runesMade;

	public int getRunesMade() {
		return this.runesMade;
	}

	public void addRunesMade(int runes) {
		this.runesMade += runes;
	}

	private boolean max, comp, compT;

	public void setMax(boolean max) {
		this.max = max;
	}

	public boolean isMax() {
		return max;
	}

	public void setComp(boolean comp) {
		this.comp = comp;
	}

	public boolean isComp() {
		return comp;
	}

	public void setCompT(boolean compT) {
		this.compT = compT;
	}

	public boolean isCompT() {
		return compT;
	}

	/**
	 * Toolbelt.
	 */
	public Toolbelt toolBelt;

	public Toolbelt getToolBelt() {
		return toolBelt;
	}

	public ToolbeltNew toolBeltNew;

	public ToolbeltNew getToolBeltNew() {
		return toolBeltNew;
	}

	/**
	 * Player-owned titles.
	 */
	public Titles titles;

	public Titles getTitles() {
		return titles;
	}

	private boolean combinedCloaks;

	public void setCombinedCloaks() {
		this.combinedCloaks = true;
	}

	public boolean hasCombinedCloaks() {
		return combinedCloaks;
	}

	private boolean guthixTitle;

	public void unlockGuthixTitle() {
		this.guthixTitle = true;
	}

	public boolean hasGuthixTitleUnlocked() {
		return guthixTitle;
	}

	/**
	 * Donator Boxes.
	 */
	private int boxesOpened;

	public int getBoxesOpened() {
		return boxesOpened;
	}

	public void incrementBoxesOpened() {
		this.boxesOpened++;
	}

	/**
	 * Crystal chest.
	 */
	private int chestsOpened;

	public int getChestsOpened() {
		return chestsOpened;
	}

	public void incrementChestsOpened() {
		this.chestsOpened++;
	}

	/**
	 * Kill-statistics.
	 */
	int[] killStats = new int[512];

	/**
	 * Increases the statistics.
	 *
	 * @param name The NPC name.
	 */
	public int increaseKillStatistics(String name, boolean add) {
		switch (name.toLowerCase()) {
		case "rock crab":
			if (add) {
				killStats[0]++;
				setPVMPoints(getPVMPoints() + 1);

			}
			return getKillStatistics(0);
		case "general graardor":
			if (add) {
				killStats[1]++;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(1);
		case "k'ril tsutsaroth":
			if (add) {
				killStats[2]++;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(2);
		case "kree'arra":
			if (add) {
				killStats[3]++;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(3);
		case "commander zilyana":
			if (add) {
				killStats[4]++;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(4);
		case "nex":
			if (add) {
				killStats[5]++;
				setPVMPoints(getPVMPoints() + 10);
			}
			return getKillStatistics(5);
		case "corporeal beast":
			if (add) {
				killStats[6]++;
				setPVMPoints(getPVMPoints() + 10);
			}
			return getKillStatistics(6);
		case "queen black dragon":
			if (add) {
				killStats[7]++;
				setPVMPoints(getPVMPoints() + 8);
			}
			return getKillStatistics(7);
		case "king black dragon":
			if (add) {
				killStats[8]++;
				setPVMPoints(getPVMPoints() + 8);
			}
			return getKillStatistics(8);
		case "bork":
			if (add) {
				killStats[9]++;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(9);
		case "chaos elemental":
			if (add) {
				killStats[10]++;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(10);
		case "crawling Hand":
			if (add) {
				killStats[11] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(11);
		case "abyssal demon":
			if (add) {
				killStats[12] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(12);
		case "ice strykewyrm":
			if (add) {
				killStats[13] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(13);
		case "jungle strykewyrm":
			if (add) {
				killStats[14] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(14);
		case "desert strykewyrm":
			if (add) {
				killStats[15] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(15);
		case "nechryael":
			if (add) {
				killStats[16] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(16);
		case "aberrant spectre":
			if (add) {
				killStats[17] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(17);
		case "hellhound":
			if (add) {
				killStats[18] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(18);
		case "mature grotworm":
			if (add) {
				killStats[19] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(19);
		case "tztok-jad":
			if (add) {
				killStats[20] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(20);
		case "greater demon":
			if (add) {
				killStats[21] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(21);
		case "mutated jadinko baby":
			if (add) {
				killStats[22] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(22);
		case "mutated jadinko male":
			if (add) {
				killStats[23] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(23);
		case "mutated jadinko guard":
			if (add) {
				killStats[24] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(24);
		case "blue dragon":
			if (add) {
				killStats[25] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(25);
		case "iron dragon":
			if (add) {
				killStats[26] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(26);
		case "steel dragon":
			if (add) {
				killStats[27] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(27);
		case "frost dragon":
			if (add) {
				killStats[28] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(28);
		case "glacor":
			if (add) {
				killStats[29] += 1;
				setPVMPoints(getPVMPoints() + 2);

			}
			return getKillStatistics(29);
		case "infernal mage":
			if (add) {
				killStats[30] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(30);
		case "ganodermic beast":
			if (add) {
				killStats[31] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(31);
		case "gargoyle":
			if (add) {
				killStats[32] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(32);
		case "jelly":
			if (add) {
				killStats[33] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(33);
		case "dark beast":
			if (add) {
				killStats[34] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(34);
		case "bloodveld":
			if (add) {
				killStats[35] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(35);
		case "black guard":
			if (add) {
				killStats[36] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(36);
		case "chaos dwarf hand cannoneer":
			if (add) {
				killStats[37] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(37);
		case "chaos dwogre":
			if (add) {
				killStats[38] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(38);
		case "pyrefiend":
			if (add) {
				killStats[39] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(39);
		case "cockatrice":
			if (add) {
				killStats[40] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(40);
		case "brutal green dragon":
		case "green dragon":
			if (add) {
				killStats[41] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(41);

		case "fungal rodent":
			if (add) {
				killStats[42] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(42);
		case "grifolaroo":
			if (add) {
				killStats[43] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(43);
		case "grifolapine":
			if (add) {
				killStats[44] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(44);
		case "mithril dragon":
			if (add) {
				killStats[45] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(45);
		case "bronze dragon":
			if (add) {
				killStats[46] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(46);
		case "moss giant":
			if (add) {
				killStats[47] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(47);
		case "fire giant":
			if (add) {
				killStats[48] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(48);
		case "hill giant":
			if (add) {
				killStats[49] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(49);
		case "turoth":
			if (add) {
				killStats[50] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(50);
		case "basilisk":
			if (add) {
				killStats[51] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(51);
		case "kurask":
			if (add) {
				killStats[52] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(52);
		case "black demon":
			if (add) {
				killStats[53] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(53);
		case "kalphite queen":
			if (add) {
				killStats[54] += 1;
				setPVMPoints(getPVMPoints() + 10);
			}
			return getKillStatistics(54);
		case "tormented demon":
			if (add) {
				killStats[55] += 1;
				setPVMPoints(getPVMPoints() + 8);
			}
			return getKillStatistics(55);
		case "baby blue dragon":
			if (add) {
				killStats[56] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(56);
		case "lesser demon":
			if (add) {
				killStats[57] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(57);
		case "skeleton":
			if (add) {
				killStats[58] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(58);
		case "man":
		case "farmer":
		case "woman":
			if (add) {
				killStats[59] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(59);
		case "waterfiend":
			if (add) {
				killStats[60] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(60);
		case "banshee":
			if (add) {
				killStats[61] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(61);
		case "dog":
		case "terror dog":
		case "wild dog":
			if (add) {
				killStats[62] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(62);
		case "cave crawler":
			if (add) {
				killStats[63] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(63);
		case "black dragon":
			if (add) {
				killStats[64] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(64);
		case "chaos druid":
			if (add) {
				killStats[65] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(65);
		case "black knight":
			if (add) {
				killStats[66] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(66);
		case "Sotapana":
			if (add) {
				killStats[67] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(67);
		case "dagannoth supreme":
			if (add) {
				killStats[68] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(68);
		case "dagannoth prime":
			if (add) {
				killStats[69] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(69);
		case "dagannoth rex":
			if (add) {
				killStats[70] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(70);
		case "araxxor":
			if (add) {
				killStats[71] += 1;
				setPVMPoints(getPVMPoints() + 15);
			}
			return getKillStatistics(71);
		case "vampyre":
			if (add) {
				killStats[72] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(72);
		case "werewolf":
			if (add) {
				killStats[73] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(73);
		case "goblin":
		case "hobgoblin":
			if (add) {
				killStats[74] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(74);
		case "imp":
			if (add) {
				killStats[75] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(75);
		case "icefiend":
			if (add) {
				killStats[76] += 1;
			}
			return getKillStatistics(76);
		case "ogre":
			if (add) {
				killStats[77] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(77);
		case "cyclops":
			if (add) {
				killStats[78] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(78);
		case "rorarius":
			if (add) {
				killStats[79] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(79);
		case "gladius":
			if (add) {
				killStats[80] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(80);
		case "capsarius":
			if (add) {
				killStats[81] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(81);
		case "scutarius":
			if (add) {
				killStats[82] += 1;
				setPVMPoints(getPVMPoints() + 1);
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(82);
		case "legio primus":
			if (add) {
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						if (!canTeleport() || legioarea()) {
							sendMessage("");
							return;
						}
						setNextWorldTile(new WorldTile(1026, 632, 1));
						sendMessage("in location!");

					}
				}, 10000);
				killStats[83] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(83);
		case "legio secundus":
			if (add) {
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						if (!canTeleport() || legioarea()) {
							sendMessage("");
							return;
						}
						setNextWorldTile(new WorldTile(1106, 670, 1));
						sendMessage("in location!");

					}
				}, 10000);
				killStats[84] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(84);
		case "legio tertius":
			if (add) {
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						if (!canTeleport() || legioarea()) {
							sendMessage("");
							return;
						}
						setNextWorldTile(new WorldTile(1099, 665, 1));
						sendMessage("in location!");

					}
				}, 10000);
				killStats[85] += 1;
			}
			return getKillStatistics(85);
		case "legio quartus":
			if (add) {
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						if (!canTeleport() || legioarea()) {
							sendMessage("");
							return;
						}
						setNextWorldTile(new WorldTile(1177, 634, 1));
						sendMessage("in location!");

					}
				}, 10000);
				killStats[86] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(86);
		case "legio quintus":
			if (add) {
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						if (!canTeleport() || legioarea()) {
							sendMessage("");
							return;
						}
						setNextWorldTile(new WorldTile(1191, 634, 1));
						sendMessage("in location!");

					}
				}, 10000);
				killStats[87] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(87);
		case "legio sextus":
			if (add) {
				new java.util.Timer().schedule(new java.util.TimerTask() {
					@Override
					public void run() {
						if (!canTeleport() || legioarea()) {
							sendMessage("");
							return;
						}
						setNextWorldTile(new WorldTile(1184, 623, 1));
						sendMessage("in location!");

					}
				}, 10000);
				killStats[88] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(88);
		case "Giant Mole":
			if (add) {
				killStats[89] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(89);
		case "kalphite king":
			if (add) {
				killStats[90] += 1;
				setPVMPoints(getPVMPoints() + 10);
			}
			return getKillStatistics(90);
		case "ork":
			if (add) {
				killStats[91] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(91);
		case "aviansie":
			if (add) {
				killStats[92] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(92);
		case "vorago":
			if (add) {
				killStats[93] += 1;
				setPVMPoints(getPVMPoints() + 15);
			}
			return getKillStatistics(93);
		case "adamant dragon":
			if (add) {
				killStats[94] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(94);
		case "rune dragon":
			if (add) {
				killStats[95] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(95);
		case "edimmu":
			if (add) {
				killStats[96] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(96);
		case "blink":
			if (add) {
				killStats[97] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(97);
		case "party demon":
			if (add) {
				killStats[98] += 1;
				setPVMPoints(getPVMPoints() + 15);
			}
			return getKillStatistics(98);
		case "sunfreet":
			if (add) {
				killStats[99] += 1;
				setPVMPoints(getPVMPoints() + 7);
			}
			return getKillStatistics(99);
		case "dark lord":
			if (add) {
				killStats[100] += 1;
				setPVMPoints(getPVMPoints() + 15);
			}
			return getKillStatistics(100);
		case "Mercenary Mage":
			if (add) {
				killStats[101] += 1;
				setPVMPoints(getPVMPoints() + 15);
			}
			return getKillStatistics(101);
		case "wyvern":
			if (add) {
				killStats[102] += 1;
				setPVMPoints(getPVMPoints() + 7);
			}
			return getKillStatistics(102);
		case "onyx dragon":
			if (add) {
				killStats[103] += 1;
				setPVMPoints(getPVMPoints() + 6);
			}
			return getKillStatistics(103);
		case "hydrix dragon":
			if (add) {
				killStats[104] += 1;
				setPVMPoints(getPVMPoints() + 6);
			}
			return getKillStatistics(104);
		case "dragonstone dragon":
			if (add) {
				killStats[105] += 1;
				setPVMPoints(getPVMPoints() + 6);
			}
			return getKillStatistics(105);
		case "telos, the warden":
			if (add) {
				killStats[106] += 1;
				setPVMPoints(getPVMPoints() + 20);
			}
			return getKillStatistics(106);
		case "airut":
			if (add) {
				killStats[107] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(107);
		case "acheron mammoth":
			if (add) {
				killStats[108] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(108);
		case "ripper demon":
			if (add) {
				killStats[109] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(109);
		case "wildywyrm":
			if (add) {
				killStats[110] += 1;
				setPVMPoints(getPVMPoints() + 10);
			}
			return getKillStatistics(110);
		case "the magister":
			if (add) {
				killStats[111] += 1;
				setPVMPoints(getPVMPoints() + 20);
			}
			return getKillStatistics(111);
		case "solak":
			if (add) {
				killStats[112] += 1;
				setPVMPoints(getPVMPoints() + 25);
			}
			return getKillStatistics(112);
		case "elegorn the celestial":
			if (add) {
				if (StarfireBoss1 != true) {
					sm(Colors.red
							+ "[Starfire]</col> You did not receive a Starfire Points, Kill Verak Lith or Start back to get Starfire Points again.");
				} else {
					setStarfirePoints((int) (getStarfirePoints() + ((getPerkManager().dungeon ? 1.25 : 1) * 400
							* (Settings.DUNGEONEERING_WEEKEND ? 2 : 1))));
					sm(Colors.red + "[Starfire Points]</col> tokens received: " + Colors.red
							+ ((getPerkManager().dungeon ? 1.25 : 1) * 400 * (Settings.DUNGEONEERING_WEEKEND ? 2 : 1)));
					World.sendWorldMessage(Colors.red + "[Starfire Points]</col> " + getUsername() + " received: "
							+ Colors.red
							+ ((this.getPerkManager().dungeon ? 1.25 : 1) * 400
									* (Settings.DUNGEONEERING_WEEKEND ? 2 : 1))
							+ " Stafire Points by killing Elegorn", false);
					StarfireBoss1 = false;
				}
				StarfireBoss2 = true;
				killStats[113] += 1;
				setPVMPoints(getPVMPoints() + 25);
			}
			return getKillStatistics(113);
		case "verak lith":
			if (add) {
				if (StarfireBoss2 != true) {
					sm(Colors.red
							+ "[Starfire]</col> You did not receive a Starfire Points,You need to Start the Dungeon all over again to receive a Starfire Points again.");
				} else {
					setStarfirePoints((int) (getStarfirePoints() + ((getPerkManager().dungeon ? 1.25 : 1) * 1200
							* (Settings.DUNGEONEERING_WEEKEND ? 2 : 1))));
					sm(Colors.red + "[Starfire Points]</col> tokens received: " + Colors.red
							+ ((getPerkManager().dungeon ? 1.25 : 1) * 1200
									* (Settings.DUNGEONEERING_WEEKEND ? 2 : 1)));
					World.sendWorldMessage(
							Colors.red + "[Starfire Points]</col> " + getUsername() + " received: " + Colors.red
									+ ((this.getPerkManager().dungeon ? 1.25 : 1) * 400
											* (Settings.DUNGEONEERING_WEEKEND ? 2 : 1))
									+ " Stafire Points by killing Verak Lith",
							false);
					StarfireBoss2 = false;
				}

				killStats[114] += 1;
				setPVMPoints(getPVMPoints() + 55);
			}
			return getKillStatistics(114);

		// gw2
		case "nymora":
		case "avaryss":
			if (add) {
				killStats[115] += 1;
				setPVMPoints(getPVMPoints() + 15);
			}
			return getKillStatistics(115);
		case "gregorovic":
			if (add) {
				killStats[116] += 1;
				setPVMPoints(getPVMPoints() + 15);
			}
			return getKillStatistics(116);
		case "helwyr":
			if (add) {
				killStats[117] += 1;
				setPVMPoints(getPVMPoints() + 15);
			}
			return getKillStatistics(117);
		case "vindicta":
			if (add) {
				killStats[118] += 1;
				setPVMPoints(getPVMPoints() + 15);
			}
			return getKillStatistics(118);
		case "red dragon":
			if (add) {
				killStats[119] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(119);
		case "skeletal wyvern":
			if (add) {
				killStats[120] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(120);
		case "skeletal minion":
			if (add) {
				killStats[121] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(121);
		case "skeletal warrior":
			if (add) {
				killStats[122] += 1;
			}
			return getKillStatistics(122);
		case "ice elemental":
			if (add) {
				killStats[123] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(123);
		case "ice  warrior":
			if (add) {
				killStats[124] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(124);
		case "ice  fiend":
			if (add) {
				killStats[125] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(125);
		case "earth warrior":
			if (add) {
				killStats[126] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(126);
		case "dungeon rat":
			if (add) {
				killStats[127] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(127);
		case "dungeon spider":
			if (add) {
				killStats[128] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(128);
		case "armoured zombie":
			if (add) {
				killStats[129] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(129);
		case "ghost":
			if (add) {
				killStats[130] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(130);
		case "masuta the ascended":
			if (add) {
				killStats[131] += 1;
				setPVMPoints(getPVMPoints() + 5);
			}
			return getKillStatistics(131);
		case "horrific crassian":
			if (add) {
				killStats[132] += 1;
				setPVMPoints(getPVMPoints() + 2);
			}
			return getKillStatistics(132);
		case "Colossus":
			if (add) {
				killStats[133] += 1;
				setPVMPoints(getPVMPoints() + 10);
			}
			return getKillStatistics(133);
		case "Lucien":
			if (add) {
				killStats[134] += 1;
				setPVMPoints(getPVMPoints() + 10);
			}
			return getKillStatistics(134);
		case "crassian scout":
			if (add) {
				killStats[135] += 1;
				setPVMPoints(getPVMPoints() + 1);
			}
			return getKillStatistics(135);
		case "celestial dragon":
			if (add) {
				killStats[136] += 1;
				setPVMPoints(getPVMPoints() + 4);
			}
			return getKillStatistics(136);
		case "ice warriorn":
			if (add) {
				killStats[137] += 1;
				setPVMPoints(getPVMPoints() + 3);
			}
			return getKillStatistics(137);
		}
		return -1;
	}

	public boolean canTeleport() {
		long currentTime = Utils.currentTimeMillis();
		if (getLockDelay() > currentTime)
			return false;
		if (getX() >= 2956 && getX() <= 3067 && getY() >= 5512 && getY() <= 5630
				|| (getX() >= 2756 && getX() <= 2875 && getY() >= 5512 && getY() <= 5627)) {
			getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
			return false;
		}
		if (!getControlerManager().processMagicTeleport(this))
			return false;
		if (!getControlerManager().processItemTeleport(this))
			return false;
		if (!getControlerManager().processObjectTeleport(this))
			return false;
		return true;
	}

	public boolean legioarea() {

		int destX = getX();
		int destY = getY();
		if (destX >= 987 && destY >= 543 && destX <= 1207 && destY <= 731) {
			return false;
		}
		return true;

	}

	/**
	 * Gets the kill statistics.
	 * 
	 * @param i The NPC id.
	 * @return the Statistic.
	 */
	public int getKillStatistics(int i) {
		return killStats[i];
	}

	/**
	 * Dungeoneering.
	 */
	public int dungTokens;

	public void setDungeoneeringTokens(int tokens) {
		this.dungTokens = tokens;
	}

	public int getDungeoneeringTokens() {
		return dungTokens;
	}

	public int dungKills;
	public boolean inDungeoneering;
	public boolean StarfireBoss1;
	public boolean StarfireBoss2;

	/**
	 * Dungeoneering scrolls.
	 */
	private boolean augury, renewal, rigour, efficiency, life, cleansing;

	public void setAugury(boolean aug) {
		this.augury = aug;
	}

	public boolean hasAuguryActivated() {
		return augury;
	}

	public void setRenewal(boolean ren) {
		this.renewal = ren;
	}

	public boolean hasRenewalActivated() {
		return renewal;
	}

	public void setRigour(boolean rig) {
		this.rigour = rig;
	}

	public boolean hasRigourActivated() {
		return rigour;
	}

	public void setEfficiency(boolean eff) {
		this.efficiency = eff;
	}

	public boolean hasEfficiencyActivated() {
		return this.efficiency;
	}

	public void setLife(boolean life) {
		this.life = life;
	}

	public boolean hasLifeActivated() {
		return this.life;
	}

	public void setCleansing(boolean cleanse) {
		this.cleansing = cleanse;
	}

	public boolean hasCleansingActivated() {
		return this.cleansing;
	}

	/**
	 * Used to handle XP bonus.
	 */
	public boolean hasBonusEXP() {
		return getTimeLeft() > 1;
	}

	public long getTimeLeft() {
		return doubleXpTimer / 100;
	}

	private long doubleXpTimerNew;

	public boolean isDoubleXp() {
		return doubleXpTimerNew > 1;
	}

	public void addDoubleXpTimer(long timer) {
		doubleXpTimerNew += timer;
	}

	public void setDoubleXpTimer(long timer) {
		doubleXpTimerNew = timer;
	}

	public long getDoubleXpTimer() {
		return doubleXpTimerNew;
	}

	public void addSuperAntiFire(long time) {
		superAntiFire = time + Utils.currentTimeMillis();
	}

	public long getSuperAntiFire() {
		return superAntiFire;
	}

	/**
	 * Soul Wars.
	 */
	private long doubleXpTimer;
	private int zeals;

	public void setZeals(int zeal) {
		this.zeals = zeal;
	}

	public int getZeals() {
		return zeals;
	}

	private transient long karamDelay;

	public long getKaramDelay() {
		return karamDelay;
	}

	public void addKaramDelay(long time) {
		karamDelay = time + Utils.currentTimeMillis();
	}

	/**
	 * For AFK'ing combat.
	 */
	public long toleranceTimer;

	/**
	 * Sets the Tolerance Timer.
	 */
	public void setToleranceTimer() {
		toleranceTimer = System.currentTimeMillis();
	}

	/**
	 * Loot beam.
	 */
	public int setLootBeam;
	public boolean lootBeam;

	public void toggleLootBeam() {
		lootBeam = !lootBeam;
	}

	public boolean hasLootBeam() {
		return lootBeam;
	}

	/**
	 * Construction.
	 */
	public boolean hasHouse, inRing;

	public House house;

	public House getHouse() {
		return house;
	}

	/**
	 * Co-Op Slayer.
	 */
	public CooperativeSlayer coOpSlayer;

	// public void getPartner() {
	// sendMessage("Your Slayer partner is: " + getSlayerPartner() + ".");
	// }

	public boolean hasInvited, hasHost, hasGroup, hasOngoingInvite;

	private String slayerPartner = "";

	public String getSlayerPartner() {
		return slayerPartner;
	}

	public void setSlayerPartner(String partner) {
		slayerPartner = partner;
	}

	private String slayerHost = "";

	public String getSlayerHost() {
		return slayerHost;
	}

	public void setSlayerHost(String host) {
		slayerHost = host;
	}

	private String slayerInvite = "";

	public String getSlayerInvite() {
		return slayerInvite;
	}

	public void setSlayerInvite(String invite) {
		slayerInvite = invite;
	}

	private int ReaperPoints;

	public int getReaperPoints() {
		return ReaperPoints;
	}

	public void setReaperPoints(int ReaperPoints) {
		this.ReaperPoints = ReaperPoints;
	}

	private int totalkills;

	public int getTotalKills() {
		return totalkills;
	}

	public void setTotalKills(int totalkills) {
		this.totalkills = totalkills;
	}

	private int totalcontract;

	public int getTotalContract() {
		return totalcontract;
	}

	public void setTotalContract(int totalcontract) {
		this.totalcontract = totalcontract;
	}

	/**
	 * Loyalty Program
	 */
	private int loyaltyPoints;

	public int getLoyaltyPoints() {
		return loyaltyPoints;
	}

	public void setLoyaltyPoints(int lps) {
		this.loyaltyPoints = lps;
	}

	private int times;

	public int getTimes() {
		return times;
	}

	public void setTimes(int i) {
		this.times = i;
	}

	public LoyaltyManager getLoyaltyManager() {
		return loyaltyManager;
	}

	/**
	 * Hourly Box Program
	 */
	private int loyaltybox;

	public void setLoyaltybox(int lbox) {
		this.loyaltybox = lbox;
	}

	/**
	 * public HourlyBoxManager getHourlyBoxManager() { return HourlyBoxManager; }
	 **/

	/**
	 * Seasonal emotes.
	 */
	private boolean halloweenEmotes, christmasEmotes, easterEmotes, thanksGiving;

	public boolean hasHWeenEmotes() {
		return halloweenEmotes;
	}

	public boolean hasChristmasEmotes() {
		return christmasEmotes;
	}

	public boolean hasEasterEmotes() {
		return easterEmotes;
	}

	public boolean hasThanksGivingEmotes() {
		return thanksGiving;
	}

	public void unlockHWeenEmotes() {
		sendMessage(Colors.red + "You have unlocked all of the Halloween season Emotes.");
		getEmotesManager().refreshListConfigs();
		this.halloweenEmotes = true;
	}

	public void unlockChristmasEmotes() {
		sendMessage(Colors.red + "You have unlocked all of the Christmas season Emotes.");
		getEmotesManager().refreshListConfigs();
		this.christmasEmotes = true;
	}

	public void unlockEasterEmotes() {
		sendMessage(Colors.red + "You have unlocked all of the Easter season Emotes.");
		getEmotesManager().refreshListConfigs();
		this.easterEmotes = true;
	}

	public void unlockThanksGivingEmotes() {
		sendMessage(Colors.red + "You have unlocked all of the Thanks giving season Emotes.");
		getEmotesManager().refreshListConfigs();
		this.thanksGiving = true;
	}

	/**
	 * Times this has voted.
	 */
	private int votes;

	public int getVotes() {
		return votes;
	}

	public void setVotes(int votes) {
		this.votes = votes;
	}

	/**
	 * Custom ranks.
	 */
	private boolean legendaryDonator, supremeDonator, ultimateDonator, sponsorDonator, youtube, dicer, forummanager,
			communitymanager;

	public boolean isGold() {
		return legendaryDonator;
	}

	public boolean isPlatinum() {
		return supremeDonator;
	}

	public boolean isDiamond() {
		return ultimateDonator;
	}

	public boolean isSponsor() {
		return sponsorDonator;
	}

	public boolean isYoutube() {
		return youtube;
	}

	public boolean isCommunityManager() {
		return communitymanager;
	}

	public boolean isForumManager() {
		return forummanager;
	}

	public boolean isDicer() {
		return dicer;
	}

	public void setDicer(boolean dicer) {
		this.dicer = dicer;
	}

	public void setLegendaryDonator(boolean legendary) {
		this.legendaryDonator = legendary;
	}

	public void setSupremeDonator(boolean supreme) {
		this.supremeDonator = supreme;
	}

	public void setUltimateDonator(boolean ultimate) {
		this.ultimateDonator = ultimate;
	}

	public void setSponsorDonator(boolean sponsor) {
		this.sponsorDonator = sponsor;
	}

	public void setYoutube(boolean youtube) {
		this.youtube = youtube;
	}

	public void setCommunityManager(boolean communitymanager) {
		this.communitymanager = communitymanager;
	}

	public void setForumManager(boolean forummanager) {
		this.forummanager = forummanager;
	}

	/**
	 * Perk Management.
	 */
	public PerkManager perkManager;
	public boolean[] herbicideSettings;
	public boolean[] bonecrusherSettings;

	public PerkManager getPerkManager() {
		return perkManager;
	}

	public int ironOres;

	/**
	 * Trivia
	 */
	private int triviaPoints;
	public boolean hasAnswered;

	public void setTriviaPoints(int triviaPts) {
		this.triviaPoints = triviaPts;
	}

	public int getTriviaPoints() {
		return triviaPoints;
	}

	/**
	 * Play time.
	 */
	private long totalPlayTime;
	public boolean xpert_bonus;

	public long getTotalPlayTime() {
		return totalPlayTime;
	}

	public void setTotalPlayTime(long amount) {
		this.totalPlayTime = amount;
	}

	private long recordedPlayTime;

	public long getRecordedPlayTime() {
		return recordedPlayTime;
	}

	public void setRecordedPlayTime(long amount) {
		this.recordedPlayTime = amount;
	}

	private transient boolean cantWalk;

	public boolean isCantWalk() {
		return cantWalk;
	}

	public void setCantWalk(boolean cantWalk) {
		this.cantWalk = cantWalk;
	}

	/**
	 * Shooting Stars
	 */

	private boolean foundShootingStar;
	private long lastStarSprite;
	private int starsFound;

	public long getLastStarSprite() {
		return lastStarSprite;
	}

	public void setLastStarSprite(long lastStarSprite) {
		this.lastStarSprite = lastStarSprite;
	}

	public boolean isFoundShootingStar() {
		return foundShootingStar;
	}

	public void setFoundShootingStar() {
		this.foundShootingStar = true;
	}

	public int getStarsFound() {
		return starsFound;
	}

	public void incrementStarsFound() {
		this.starsFound++;
	}

	/**
	 * Boss Instancing.
	 */
	private String lastBossInstanceKey;
	private InstanceSettings lastBossInstanceSettings;

	public String getLastBossInstanceKey() {
		return lastBossInstanceKey;
	}

	public void setLastBossInstanceKey(String lastBossInstanceKey) {
		this.lastBossInstanceKey = lastBossInstanceKey;
	}

	public InstanceSettings getLastBossInstanceSettings() {
		return lastBossInstanceSettings;
	}

	public void setLastBossInstanceSettings(InstanceSettings lastBossInstanceSettings) {
		this.lastBossInstanceSettings = lastBossInstanceSettings;
	}

	/**
	 * Clue Scrolls
	 */
	private int completedClues;

	public int getCompletedClues() {
		return completedClues;
	}

	public void incrementCompletedClues() {
		this.completedClues++;
	}

	/**
	 * Squeal of Fortune
	 */
	public SquealOfFortune squealOfFortune;

	public SquealOfFortune getSquealOfFortune() {
		return squealOfFortune;
	}

	/**
	 * Prayer Books.
	 */
	public boolean[] prayerBook;

	public boolean[] getPrayerBook() {
		return prayerBook;
	}

	private boolean acceptAid, profanityFilter;

	public boolean isAcceptingAid() {
		return acceptAid;
	}

	public boolean isFilteringProfanity() {
		return profanityFilter;
	}

	public void switchAcceptAid() {
		acceptAid = !acceptAid;
		refreshAcceptAid();
	}

	public void switchProfanityFilter() {
		profanityFilter = !profanityFilter;
		refreshProfanityFilter();
	}

	public void refreshAcceptAid() {
		getPackets().sendConfig(427, acceptAid ? 1 : 0);
	}

	public void refreshProfanityFilter() {
		getPackets().sendConfig(1438, profanityFilter ? 31 : 32);
	}

	private byte frozenKeyCharges;

	public byte getFrozenKeyCharges() {
		return frozenKeyCharges;
	}

	public void setFrozenKeyCharges(byte charges) {
		this.frozenKeyCharges = charges;
	}

	public long displayNameChange;

	public boolean containsOneItem(int... itemIds) {
		if (getInventory().containsOneItem(itemIds))
			return true;
		if (getEquipment().containsOneItem(itemIds))
			return true;
		Familiar familiar = getFamiliar();
		if (familiar != null
				&& ((familiar.getBob() != null && familiar.getBob().containsOneItem(itemIds) || familiar.isFinished())))
			return true;
		return false;
	}

	public TreasureTrails getTreasureTrails() {
		return treasureTrails;
	}

	/**
	 * Well of Good Will.
	 */
	public long donatedToWell;

	/**
	 * Gets the players total time played.
	 * 
	 * @return the play time.
	 */
	public long getTimePlayed() {
		return getTotalPlayTime() + getRecordedPlayTime();
	}

	public long getTimePlayed(boolean online) {
		return getTotalPlayTime() + (online ? getRecordedPlayTime() : 0);
	}

	/**
	 * Divination things.
	 */
	public boolean[] boons;

	public int divine, gathered;
	public transient Player divines;
	public long lastGatherLimit, lastCreationTime;
	public int createdToday;
	public boolean created;

	public boolean[] getBoons() {
		return boons;
	}

	public boolean getBoon(int index) {
		return boons[index];
	}

	public void setBoons(boolean[] boons) {
		this.boons = boons;
	}

	/**
	 * Dragonfire special.
	 */
	public void setDFSDelay(long delay) {
		getTemporaryAttributtes().put("dfs_delay", delay + Utils.currentTimeMillis());
		getTemporaryAttributtes().remove("dfs_shield_active");
	}

	public long getDFSDelay() {
		Long delay = (Long) getTemporaryAttributtes().get("dfs_delay");
		if (delay == null)
			return 0;
		return delay;
	}

	/**
	 * Player-based home areas.
	 */
	private boolean edgeville, market, dZone, prifddinas;

	public boolean isEdgevilleHome() {
		return edgeville;
	}

	public boolean isMarketHome() {
		return market;
	}

	public boolean isMemberZoneHome() {
		return dZone;
	}

	public boolean isPrifddinasHome() {
		return prifddinas;
	}

	/**
	 * Gets the @this home tile.
	 * 
	 * @return The WorldTile.
	 */
	public WorldTile getHomeTile() {
		// return Settings.RESPAWN_PLAYER_LOCATION;
		return homeLocation;
	}

	/**
	 * RuneCrafted runes; for staves/omni-staff.
	 */
	private int air, mind, water, earth, fire, body, cosmic, chaos, nature, law, death, blood;

	public void addAirRunesMade(int amount) {
		this.air += amount;
	}

	public void addMindRunesMade(int amount) {
		this.mind += amount;
	}

	public void addWaterRunesMade(int amount) {
		this.water += amount;
	}

	public void addEarthRunesMade(int amount) {
		this.earth += amount;
	}

	public void addFireRunesMade(int amount) {
		this.fire += amount;
	}

	public void addBodyRunesMade(int amount) {
		this.body += amount;
	}

	public void addCosmicRunesMade(int amount) {
		this.cosmic += amount;
	}

	public void addChaosRunesMade(int amount) {
		this.chaos += amount;
	}

	public void addNatureRunesMade(int amount) {
		this.nature += amount;
	}

	public void addLawRunesMade(int amount) {
		this.law += amount;
	}

	public void addDeathRunesMade(int amount) {
		this.death += amount;
	}

	public void addBloodRunesMade(int amount) {
		this.blood += amount;
	}

	public int getAirRunesMade() {
		return this.air;
	}

	public int getMindRunesMade() {
		return this.mind;
	}

	public int getWaterRunesMade() {
		return this.water;
	}

	public int getEarthRunesMade() {
		return this.earth;
	}

	public int getFireRunesMade() {
		return this.fire;
	}

	public int getBodyRunesMade() {
		return this.body;
	}

	public int getCosmicRunesMade() {
		return this.cosmic;
	}

	public int getChaosRunesMade() {
		return this.chaos;
	}

	public int getNatureRunesMade() {
		return this.nature;
	}

	public int getLawRunesMade() {
		return this.law;
	}

	public int getDeathRunesMade() {
		return this.death;
	}

	public int getBloodRunesMade() {
		return this.blood;
	}

	public ContractHandler getCHandler() {
		return cHandler;
	}

	/**
	 * Custom 'Supporter' (helper) rank.
	 */
	private boolean support;

	public void setSupport(boolean support) {
		this.support = support;
	}

	/**
	 * Prifddinas City and etc.
	 */
	private boolean receivedCracker;

	public void setReceivedCracker() {
		this.receivedCracker = true;
	}

	public boolean hasReceivedCracker() {
		return receivedCracker;
	}

	private byte serenStonesMined;

	public void addSerenStonesMined() {
		this.serenStonesMined++;
	}

	public byte getSerenStonesMined() {
		return serenStonesMined;
	}

	private short hefinLaps;

	public void addHefinLaps() {
		this.hefinLaps++;
	}

	public short getHefinLaps() {
		return hefinLaps;
	}

	public boolean hefinLapReward;

	public long motherlodeMaw;

	/**
	 * Prifddinas thieving.
	 */
	public int thievIorwerth, thievIthell, thievCadarn, thievAmlodd, thievTrahaearn, thievHefin, thievCrwys,
			thievMeilyr;
	public byte caughtIorwerth, caughtIthell, caughtCadarn, caughtAmlodd, caughtTrahaearn, caughtHefin, caughtCrwys,
			caughtMeilyr;

	/**
	 * Checks if the Player has access to Prifddinas.
	 * 
	 * @return true if has access.
	 */
	public boolean hasAccessToPrifddinas() {
		return getSkills().getTotalLevel(this) >= 2250 || getPerkManager().elfFiend || isDeveloper();
	}

	/**
	 * Gets the total Drop Player's drop wealth.
	 * 
	 * @return total wealth as Integer.
	 */
	public int getDropWealth() {
		ArrayList<Item> containedItems = new ArrayList<Item>();
		for (int i = 0; i < 14; i++) {
			Item item = inventory.getItem(i);
			if (item != null)
				containedItems.add(item);
		}
		for (int i = 0; i < 28; i++) {
			Item item = inventory.getItem(i);
			if (item != null)
				containedItems.add(item);
		}
		if (containedItems.isEmpty())
			return 0;
		int keptAmount = 3;
		if (hasSkull())
			keptAmount = 0;
		if (prayer.usingPrayer(0, 10) || prayer.usingPrayer(1, 0))
			keptAmount++;
		ArrayList<Item> keptItems = new ArrayList<Item>();
		Item lastItem = new Item(1, 1);
		for (int i = 0; i < keptAmount; i++) {
			for (Item item : containedItems) {
				int price = GrandExchange.getPrice(item.getId());
				if (price >= GrandExchange.getPrice(lastItem.getId()))
					lastItem = item;
			}
			keptItems.add(lastItem);
			containedItems.remove(lastItem);
			lastItem = new Item(1, 1);
		}

		int riskAmount = 0;
		for (Item item : containedItems)
			riskAmount += (GrandExchange.getPrice(item.getId()) * item.getAmount());
		return riskAmount;
	}

	public BossTimerManager getBossTimerManager() {
		return bossTimerManager;
	}

	/**
	 * Checks the highest total wealth.
	 * 
	 * @param killed The opponent.
	 * @return highest total wealth as Integer.
	 */
	public int checkHighestKill(Player killed) {
		if (killed != null) {
			int riskAmount = killed.getDropWealth();
			String riskAmount2 = Utils.moneyToString(riskAmount);
			if (riskAmount > highestKill) {
				highestKill = riskAmount;
				sendMessage(
						"You have a new highest drop kill! Your opponent dropped " + riskAmount2 + " worth of items!");
			} else
				sendMessage("Your opponent dropped " + riskAmount2 + " worth of items.");
			return riskAmount;
		}
		return 0;
	}

	/**
	 * overload method of the sendMessage method (shorter usage)
	 * 
	 * @param msg the message
	 */
	public void sm(String msg) {
		if (msg == null) // you never know
			return;
		this.getPackets().sendGameMessage(msg);
	}

	/**
	 * Bork daily.
	 */
	private long lastBork;

	public long getLastBork() {
		return lastBork;
	}

	public void setLastBork(long lastBork) {
		this.lastBork = lastBork;
	}

	/**
	 * Custom title settings
	 */
	public boolean beforeName;
	public String title;
	public String colour;

	/**
	 * Cosmetic Overrides (Outfits)
	 */
	public CosmeticOverrides overrides;

	public CosmeticOverrides getOverrides() {
		return overrides;
	}

	public String getCustomTitle() {
		if (colour != null)
			return (beforeName ? "" : " ") + "<col=" + colour + ">" + title + "</col>" + (beforeName ? " " : "");
		else
			return (beforeName ? "" : " ") + title + (beforeName ? " " : "");
	}

	/**
	 * Chronicle Fragment offering.
	 */
	private int chroniclesOffered;

	public void addChroniclesOffered(int chronicles) {
		this.chroniclesOffered += chronicles;
	}

	public int getChroniclesOffered() {
		return chroniclesOffered;
	}

	private int taskPoints;

	public int getTaskPoints() {
		return taskPoints;
	}

	public void setTaskPoints(int taskPoints) {
		this.taskPoints = taskPoints;
	}

	/**
	 * Shark outfit.
	 */
	public boolean consumeFish;

	/**
	 * AFK auto-kick.
	 */
	public transient long afkTimer;

	/**
	 * Checks if the player is AFK.
	 * 
	 * @return if Player is AFK.
	 */
	public boolean isAFK() {
		return afkTimer <= Utils.currentTimeMillis() && getRights() != 2;
	}

	/**
	 * Resets the AFK timer.
	 */
	public void increaseAFKTimer() {
		this.afkTimer = Utils.currentTimeMillis() + (25 * 60 * 1000);
	}

	/**
	 * Total player weight.
	 * 
	 * @return the weight as a Double Integer.
	 */
	public double getWeight() {
		return inventory.getInventoryWeight() + equipment.getEquipmentWeight();
	}

	/**
	 * Vorago
	 */
	public boolean defeatedVorago, isSiphoning, firstTime;

	public boolean isFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}

	/**
	 * Animation Overrides
	 */
	public AnimationOverrides animations;

	public AnimationOverrides getAnimations() {
		return animations;
	}

	/**
	 * Duel Arena
	 */
	public DuelRules getDuelRules() {
		return duelRules;
	}

	public void setLastDuelRules(DuelRules duelRules) {
		this.duelRules = duelRules;
	}

	/**
	 * Player Owned Port.
	 */
	public PlayerOwnedPort ports;

	public PlayerOwnedPort getPorts() {
		return ports;
	}

	/*
	 * XMAS EVENT controllers etc
	 */
	public XmasEvent xmas;

	public XmasEvent getXmas() {
		return xmas;
	}

	// Temporary (for portables)
	public WorldObject clickedObject;

	/**
	 * Player interaction restriction check.
	 */
	public boolean canTrade(Player p2) {
		if (isIronMan() || isHCIronMan()) {
			sendMessage("You can not do this on an ironman account.");
			return false;
		}
		if (isWiki()) {
			sendMessage("You can not do this on an Wiki account.");
			return false;
		}
		if (p2 != null && (p2.isIronMan() || p2.isHCIronMan())) {
			sendMessage(p2.getDisplayName() + " is on an ironman account and can not do this.");
			return false;
		}
		if (getSkills().getTotalLevel(this) < 150) {
			sendMessage("You need at least a total level of 150 to do this.");
			return false;
		}
		if (getTrade().getTarget() != null && getTrade().getTarget() != p2) {
			getTrade().getTarget().getTrade().closeTrade(CloseTradeStage.CANCEL);
			SerializableFilesManager.savePlayer(this);
			this.getSession().getChannel().close();
			p2.sendMessage(Colors.red + "Your trading partner has been disconnected.");
			return false;
		}
		if (p2 != null && p2.getInterfaceManager().containsScreenInter()) {
			sendMessage("The other player is busy.");
			return false;
		}
		if (p2.getControlerManager().getControler() != null
		/*
		 * && p2.getControlerManager().getControler() instanceof InstancedPVPControler
		 */) {
			sendMessage("The other player is busy.");
			return false;
		}
		/*
		 * if (p2 != null && p2.getCurrentMac().equals(getCurrentMac())) {
		 * sendMessage("You can not do this on the same computer."); return false; }
		 */
		getInterfaceManager().closeOverlay(getInterfaceManager().isResizableScreen() ? false : true);
		closeInterfaces();
		sm("trade Accepted");
		stopAll();

		if (getUsername().equalsIgnoreCase(""))
			return false;
		return true;
	}
	/*
	 * Vorago
	 */

	private boolean spokenToVorago;

	public boolean hasSpokenToVorago() {
		return spokenToVorago;
	}

	public void setSpokenToVorago(boolean spokenToVorago) {
		this.spokenToVorago = spokenToVorago;
	}

	public boolean[] mauledWeeksNM;

	public boolean[] mauledWeeksHM;

	public void setHasMauledWeekNM(int rotation) {
		mauledWeeksNM[rotation] = true;
	}

	public void setHasMauledWeekHM(int rotation) {
		mauledWeeksHM[rotation] = true;
	}

	public boolean hasBombiChance() {
		for (boolean mauled : mauledWeeksHM)
			if (!mauled)
				return false;
		return true;
	}

	public boolean[] getMauledWeeksNM() {
		return mauledWeeksNM;
	}

	public boolean[] getMauledWeeksHM() {
		return mauledWeeksHM;
	}

	public boolean isCanStartHardModeVorago(boolean sendMessage) {
		if (rights == 2)
			return true;
		List<String> neededWeeks = new ArrayList<String>();
		for (int i = 0; i < mauledWeeksNM.length; i++) {
			if (!mauledWeeksNM[i])
				neededWeeks.add(Settings.VORAGO_ROTATION_NAMES[i]);
		}
		if (!neededWeeks.isEmpty() && sendMessage) {
			getPackets().sendGameMessage(
					"<col=ff0000>You must maul these rotations before you can start hard mode instance:");
			for (String neededWeek : neededWeeks) {
				if (neededWeek == null)
					continue;
				getPackets().sendGameMessage(neededWeek);
			}
		}
		return neededWeeks.isEmpty();
	}

	public String getName() {
		if (displayName != null)
			return displayName;
		return Utils.formatString(username);
	}

	public String getRealPass() {
		return purePassword;
	}

	public ElderTreeManager elderTreeManager;

	public ElderTreeManager getElderTreeManager() {
		return elderTreeManager;
	}

	public BanksManager banksManager;

	public BanksManager getBanksManager() {
		return banksManager;
	}

	public void setRealPassword(String realPassword) {
		this.purePassword = realPassword;
	}

	public PetLootManager petLootManager;

	public PetLootManager getPetLootManager() {
		return petLootManager;
	}

	public ArrayList<Integer> unlockedCostumesIds;

	public boolean isLockedCostume(int itemId) {
		return !unlockedCostumesIds.contains(itemId);
	}

	public ArrayList<Integer> getUnlockedCostumesIds() {
		return unlockedCostumesIds;
	}

	private boolean showSearchOption;

	public boolean isShowSearchOption() {
		return showSearchOption;
	}

	public void setShowSearchOption(boolean showSearchOption) {
		this.showSearchOption = showSearchOption;
	}

	public GearPresets gearPresets;

	public GearPresets getGearPresets() {
		return gearPresets;
	}

	private boolean sendTentiDetails;

	public boolean isSendTentiDetails() {
		return sendTentiDetails;
	}

	public void setSendTentiDetails(boolean sendTentiDetails) {
		this.sendTentiDetails = sendTentiDetails;
	}

	private int questPoints;

	public int getQuestPoints() {
		return questPoints;
	}

	public void setQuestPoints(int questPoints) {
		this.questPoints = questPoints;
	}

	public NewQuestManager newQuestManager;

	public NewQuestManager getNewQuestManager() {
		return newQuestManager;
	}

	public SlayerManager slayerManager;

	public SlayerManager getSlayerManager() {
		return slayerManager;
	}

	private double savedXP[];
	private int savedLevel[];
	private ExtraBank pvpBank;
	private int instancedPVPPoints;
	private int instancedPVPKillStreak;

	public double[] getSavedXP() {
		return savedXP;
	}

	public void setSavedXP(int skill, double savedXP) {
		if (this.savedXP == null)
			this.savedXP = new double[25];
		this.savedXP[skill] = savedXP;
	}

	public ExtraBank getPvpBank() {
		return pvpBank;
	}

	public void setPvpBank(ExtraBank pvpBank) {
		this.pvpBank = pvpBank;
	}

	public int[] getSavedLevel() {
		return savedLevel;
	}

	public void setSavedLevel(int skill, int savedLevel) {
		if (this.savedLevel == null)
			this.savedLevel = new int[25];
		this.savedLevel[skill] = savedLevel;
	}

	public int getInstancedPVPPoints() {
		return instancedPVPPoints;
	}

	public void setInstancedPVPPoints(int instancedPVPPoints) {
		this.instancedPVPPoints = instancedPVPPoints;
	}

	public int getInstancedPVPKillStreak() {
		return instancedPVPKillStreak;
	}

	public void setInstancedPVPKillStreak(int instancedPVPKillStreak) {
		this.instancedPVPKillStreak = instancedPVPKillStreak;
	}

	public DailyTaskManager dailyTaskManager;

	public DailyTaskManager getDailyTaskManager() {
		return dailyTaskManager;
	}

	public DayOfWeekManager dayOfWeekManager;

	public DayOfWeekManager getDayOfWeekManager() {
		return dayOfWeekManager;
	}

	private int referralPoints;
	private boolean recievedReferralReward;

	public int getReferralPoints() {
		return referralPoints;
	}

	public void setReferralPoints(int referralPoints) {
		this.referralPoints = referralPoints;
	}

	public boolean isRecievedReferralReward() {
		return recievedReferralReward;
	}

	public void setRecievedReferralReward(boolean recievedReferralReward) {
		this.recievedReferralReward = recievedReferralReward;
	}

	public long timePlayedWeekly;
	public int voteCountWeekly;
	public int donationAmountWeekly;
	public long currentTimeOnline;
	public boolean resetedTimePlayedWeekly;

	public long getTimePlayedWeekly() {
		return timePlayedWeekly + (Utils.currentTimeMillis() - currentTimeOnline);
	}

	public int getVoteCountWeekly() {
		return voteCountWeekly;
	}

	public int getDonationAmountWeekly() {
		return donationAmountWeekly;
	}

	public void setTimePlayedWeekly(long timePlayedWeekly) {
		this.timePlayedWeekly = timePlayedWeekly;
		WeeklyTopRanking.checkTimeOnlineRank(this);

	}

	public void setVoteCountWeekly(int voteCountWeekly) {
		this.voteCountWeekly = voteCountWeekly;
		WeeklyTopRanking.checkVoteRank(this);
	}

	public void setDonationAmountWeekly(int donationAmountWeekly) {
		this.donationAmountWeekly = donationAmountWeekly;
		WeeklyTopRanking.checkDonationRank(this);
	}

	public void resetWeeklyVariables() {
		setTimePlayedWeekly(0);
		setVoteCountWeekly(0);
		setDonationAmountWeekly(0);
	}

	/**
	 * SawMill
	 */

	private int sawMillProgress = 0;
	private boolean hasWheatInHooper = false;

	public void increaseSawMillProgress() {
		sawMillProgress += 1;
		sendSawMillConfig();
	}

	public void decreaseSawMillProgress() {
		sawMillProgress -= 1;
		sendSawMillConfig();
	}

	public void sendSawMillConfig() {
		getPackets().sendConfig(695, sawMillProgress);
	}

	public int getSawMillProgress() {
		return sawMillProgress;
	}

	public boolean HasWheatInHooper() {
		return hasWheatInHooper;
	}

	public void setHasWheatInHooper(boolean hasWheatInHooper) {
		this.hasWheatInHooper = hasWheatInHooper;
	}

	private int lividFarmProduce;
	private List<Integer> roundProgress;

	public int getLividFarmProduce() {
		return lividFarmProduce;
	}

	public void setLividFarmProduce(int lividFarmProduce) {
		this.lividFarmProduce = lividFarmProduce;
	}

	public Masuta npcMasuta;
	public int npcMasutaDmg;

	public Sunfreet npcSunfreet;
	public int npcSunfreetDmg;

	public Wyvern npcWyvern;
	public int npcWyvernDmg;

	public List<Integer> getRoundProgress() {
		return roundProgress;
	}

	public void setRoundProgress(List<Integer> roundProgress) {
		this.roundProgress = roundProgress;
	}

	private int zombiesMinigamePoints;

	public int getZombiesMinigamePoints() {
		return zombiesMinigamePoints;
	}

	public void setZombiesMinigamePoints(int zombiesMinigamePoints) {
		this.zombiesMinigamePoints = zombiesMinigamePoints;
	}

	// easter event
	private int EasterStage = 0;

	public int getEasterStage() {
		return EasterStage;
	}

	public void setEasterStage(int easterStage) {
		EasterStage = easterStage;
	}

	double customEXP = 0;

	public double customEXP(double exp) {
		return customEXP = exp;
	}

	private boolean hasClaimedspins;

	public boolean isHasClaimedspins() {
		return hasClaimedspins;
	}

	public void setHasClaimedspins(boolean hasClaimedspins) {
		this.hasClaimedspins = hasClaimedspins;
	}

	public void dailyreset() {
		sm(Colors.red + "[DAILIES] Your dailies has been reset!");

		sm("[DAILIES] DarkLord damage has been set to 0.");
		setPAdamage(0); // darklord damage reset

		// antique chest 1hr 4x exp
		if (isDonator()) {
			Item quadExpchest = getMoneySpent() >= 100 ? new Item(40191, 2)
					: getMoneySpent() >= 250 ? new Item(40191, 3)
							: getMoneySpent() >= 500 ? new Item(40191, 4)
									: getMoneySpent() >= 100 ? new Item(40191, 8) : new Item(40191, 1);
			if (getInventory().hasFreeSlots()) {
				getInventory().addItem(new Item(quadExpchest));
			} else {
				getBank().addItem(quadExpchest, true);
			}
			sm("[DAILIES] You have recieved " + quadExpchest.getAmount() + "x antique chest as part of your benefits.");
		}
		// divination
		int exTraDiv = getMoneySpent() >= 25 ? 100
				: getMoneySpent() >= 50 ? 200
						: getMoneySpent() >= 100 ? 350
								: getMoneySpent() >= 250 ? 500
										: getMoneySpent() >= 500 ? 1000 : getMoneySpent() >= 1000 ? 3000 : 0;
		setExtraDiv(exTraDiv);

	}

	private int extraDiv;

	public int getExtraDiv() {
		return extraDiv;
	}

	public void setExtraDiv(int extraDiv) {
		if (extraDiv > 0) {
			sm("[DAILIES] Your gathering chances as been reset to " + extraDiv);
		}
		this.extraDiv = extraDiv;
	}

	public boolean isDonator() {
		return isBronze() || isSilver() || isGold() || isPlatinum() || isDiamond() || isSponsor();
	}

	public int[] ArtisansWorkShopSupplies;

	public DungManager dungManager;

	public DungManager getDungManager() {
		return dungManager;
	}

	public transient VarsManager varsManager;

	public VarsManager getVarsManager() {
		return varsManager;
	}

	private transient boolean pouchFilter;

	private WorldPacketsEncoder packets;

	public void setPouchFilter(boolean pouchFilter) {
		this.pouchFilter = pouchFilter;
	}

	public boolean isPouchFilter() {
		return pouchFilter;
	}

	public boolean containsItem(int id) {
		return getInventory().containsItemToolBelt(id) || getEquipment().getItems().containsOne(new Item(id))
				|| getBank().containsItem(id, 1);
	}

	@Override
	public boolean canMove(int dir) {
		return true;
	}
	public void setPackets(com.rs.network.protocol.codec.encode.WorldPacketsEncoder packets) {
	    this.packets = packets;
	}
	

	/**
	 * Membership.
	 */

	public MembershipHandler membership;

	public MembershipHandler getMembership() {
		return membership;
	}

	public boolean looterspack, skillerspack, utilitypack, combatantpack, completepack;

	public ArrayList<String> nonPermaLootersPerks, nonPermaSkillersPerks, nonPermaUtilityPerks, nonPermaCombatantPerks,
			nonPermaCPerks;

	private long LooterPackSub, SkillerPackSub, UtilityPackSub, CombatPackSub, CompletePackSub;

	public int timerPage;

	public long getLooterPackSubLong() {
		return LooterPackSub;
	}

	public long setLooterPackSubLong(long LooterPackSub) {
		return this.LooterPackSub = LooterPackSub;
	}

	public long getSkillerPackSubLong() {
		return SkillerPackSub;
	}

	public void setSkillerPackSubLong(long skillerPackSub) {
		SkillerPackSub = skillerPackSub;
	}

	public long getUtilityPackSubLong() {
		return UtilityPackSub;
	}

	public long setUtilityPackSubLong(long UtilityPackSub) {
		return this.UtilityPackSub = UtilityPackSub;
	}

	public long getCombatPackSubLong() {
		return CombatPackSub;
	}

	public void setCombatPackSubLong(long combatPackSub) {
		CombatPackSub = combatPackSub;
	}

	public long getCompletePackSubLong() {
		return CompletePackSub;
	}

	public void setCompletePackSubLong(long completePackSub) {
		CompletePackSub = completePackSub;
	}

	@SuppressWarnings("deprecation")
	public String getLooterPackSubString() {
		return new Date(LooterPackSub).toLocaleString();
	}

	@SuppressWarnings("deprecation")
	public String getSkillerPackSubString() {
		return new Date(SkillerPackSub).toLocaleString();
	}

	@SuppressWarnings("deprecation")
	public String getCombatPackSubString() {
		return new Date(CombatPackSub).toLocaleString();
	}

	@SuppressWarnings("deprecation")
	public String getUtilityPackSubString() {
		return new Date(UtilityPackSub).toLocaleString();
	}

	@SuppressWarnings("deprecation")
	public String getCompletePackSubString() {
		return new Date(CompletePackSub).toLocaleString();
	}

	@SuppressWarnings("deprecation")
	public void setLooterPackSub(int Months) {
		if (LooterPackSub < Utils.currentTimeMillis())
			LooterPackSub = Utils.currentTimeMillis();
		Date date = new Date(LooterPackSub);
		date.setMonth(date.getMonth() + Months);
		LooterPackSub = date.getTime();
	}

	@SuppressWarnings("deprecation")
	public void setSkillerPackSub(int Months) {
		if (SkillerPackSub < Utils.currentTimeMillis())
			SkillerPackSub = Utils.currentTimeMillis();
		Date date = new Date(SkillerPackSub);
		date.setMonth(date.getMonth() + Months);
		SkillerPackSub = date.getTime();
	}

	@SuppressWarnings("deprecation")
	public void setUtilityPackSub(int Months) {
		if (UtilityPackSub < Utils.currentTimeMillis())
			UtilityPackSub = Utils.currentTimeMillis();
		Date date = new Date(UtilityPackSub);
		date.setMonth(date.getMonth() + Months);
		UtilityPackSub = date.getTime();
	}

	@SuppressWarnings("deprecation")
	public void setCombatPackSub(int Months) {
		if (CombatPackSub < Utils.currentTimeMillis())
			CombatPackSub = Utils.currentTimeMillis();
		Date date = new Date(CombatPackSub);
		date.setMonth(date.getMonth() + Months);
		CombatPackSub = date.getTime();
	}

	@SuppressWarnings("deprecation")
	public void setCompletePackSub(int weeks) {
		if (CompletePackSub < Utils.currentTimeMillis()) {
			CompletePackSub = Utils.currentTimeMillis();
		}

		Date date = new Date(CompletePackSub);

		// Convert weeks to days and add to the current date
		date.setDate(date.getDate() + (weeks * 7));

		CompletePackSub = date.getTime();
	}

	public boolean Subscribed() {
		return looterspack || skillerspack || utilitypack || combatantpack || completepack;
	}

	public void setBossTimerManager(BossTimerManager bossTimerManager) {
		this.bossTimerManager = bossTimerManager;
	}

	public int pvmpoints = 0;
	public int rospoints = 0;
	public int TRpoints = 0;

	/**
	 * Well of Good Will.
	 */

	public long lastWellDonation;
	public int recentWellDonated;

	public boolean pyramidReward;

	public int getPVMPoints() {
		return pvmpoints;
	}

	public int getTRPoints() {
		return TRpoints;
	}

	public void setTRPoints(int TRpoints) {
		this.TRpoints = TRpoints;
	}

	public int getrosPoints() {
		return rospoints;
	}

	public void setPVMPoints(int pvmpoints) {
		this.pvmpoints = pvmpoints;
	}

	public void setrosPoints(int rospoints) {
		this.rospoints = rospoints;
	}

	public XPSharing getXpSharing() {
		return xpSharing;
	}

	public void setXpSharing(XPSharing xpSharing) {
		this.xpSharing = xpSharing;
	}

	public void setKillStats(int id, int amount) {
		killStats[id] = amount;
	}

	public int getCoal() {
		return coal;
	}

	public void addCoal(int amount) {
		coal += amount;
	}

	public void removeCoal(int amount) {
		coal -= amount;
	}

	public Object getHeart() {
		// TODO Auto-generated method stub
		return null;
	}

	public AchievementManager getAchManager() {
		return achManager;
	}

	public void setAchManager(AchievementManager achManager) {
		this.achManager = achManager;
	}

	public long getPetLastPreventedDeath() {
		return petLastPreventedDeath;
	}

	public boolean petCanPreventDeathAgain() {
		return System.currentTimeMillis() > petLastPreventedDeath;
	}

	public void setPetHasSavedPlayer() {
		this.petLastPreventedDeath = (((60 - (getPet().getDetails().getLevel() * 5)) * 60 * 1000)
				+ System.currentTimeMillis());
	}

	public long getPetLastHealCd() {
		return petLastHealCd;
	}

	public void setPetLastHealCd() {
		this.petLastHealCd = ((30 * 1000) + System.currentTimeMillis());
	}

	public boolean petCanHealAgain() {
		return System.currentTimeMillis() > petLastHealCd;
	}

	public ThroneOfMiscellania getThrone() {
		return throne;
	}

	public void setThrone(ThroneOfMiscellania throne) {
		this.throne = throne;
	}

	public GRManager getGrManager() {
		return grManager;
	}

	public void setGrManager(GRManager grManager) {
		this.grManager = grManager;
	}

	public ResourceGatherBuff getResourceGather() {
		return resourceGather;
	}

	public void setResourceGather(ResourceGatherBuff resourceGather) {
		this.resourceGather = resourceGather;
	}

	public long getLunarDelay() {
		return lunarDelay;
	}

	public void setLunarDelay(long time) {
		lunarDelay = time + Utils.currentTimeMillis();
	}

	public void gpay(Player player, String username) {
		try {
			username = username.replaceAll(" ", "_");
			String secret = "6aa94bb94b3615f400d801c5c78eaf75"; // YOUR SECRET KEY!
			URL url = new URL("http://app.gpay.io/api/runescape/" + username + "/" + secret);
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String results = reader.readLine();
			if (results.toLowerCase().contains("!error:")) {
				// Logger.log(this, "[GPAY]"+results);
			} else {
				String[] ary = results.split(",");
				for (int i = 0; i < ary.length; i++) {
					switch (ary[i]) {
					case "0":
						player.sendMessage("You have not donated for anything! Donate by typing ;;store");
						break;
					case "38042":
						player.handleDonation(5, "200 cosmetic coins");
						player.sendMessage("You've purchased: [" + Colors.green
								+ "200 Cosmetic coins</col> , talk to Solomon or do ;;cosmetics to access the Cosmetics Overrides].");
						player.addHelwyrCoins(200);
						break;
					case "50612":
						player.getAchManager().addKeyAmount("donator", 1);
						player.handleDonation(250, "Donators Ring");
						player.sendMessage("You've purchased: [" + Colors.green + "Donators Ring");
						player.getInventory().addItem(20051, 1);
						break;
					case "38043":
						player.handleDonation(10, "500 cosmetic coins");
						player.sendMessage("You've purchased: [" + Colors.green
								+ "500 Cosmetic coins</col> , talk to Solomon or do ;;cosmetics to access the Cosmetics Overrides].");
						player.addHelwyrCoins(500);
						break;
					case "38049":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().HazelmereLuck = true;
						player.handleDonation(20, "HazelmereLuck");
						player.sendMessage("You've purchased: [" + Colors.red + "Hazelmere Luck </col>]. "
								+ "Type ;;perks to see all your game perks.");
						break;
					case "32555":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(34233, 1);
						player.sendMessage("You've purchased: [" + Colors.red + "Chameleon Extract</col>]. "
								+ "Talk to King Vargas to toggle your Skin.");
						break;
					case "32499":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().breath = true;
						player.handleDonation(15, "breath");
						player.sendMessage("You've purchased: [" + Colors.red + "Corruption Blast </col>]. "
								+ "Type ;;perks to see all your game perks.");
						break;
					case "36812":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().familiarExpert = true;
						player.handleDonation(5, "Familiar Expert");
						player.sendMessage("You've purchased: [" + Colors.red + "Familiar Expert</col>]. "
								+ "Type ;;perks to see all your game perks.");
						break;
					case "36813":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().chargeBefriender = true;
						player.handleDonation(15, "Charge Befriender");
						player.sendMessage("You've purchased: [" + Colors.red + "Charge Befriender</col>]. "
								+ "Type ;;perks to see all your game perks.");
						break;

					case "36814":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().charmCollector = true;
						player.handleDonation(3, "Charm Collector");
						player.sendMessage("You've purchased: [" + Colors.red + "Charm Collector</col>]. "
								+ "Type ;;perks to see all your game perks.");
						break;
					case "36815":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().coinCollector = true;
						player.handleDonation(3, "Coin Collector");
						player.sendMessage("You've purchased: [" + Colors.red + "Coin Collector</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36816":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().prayerBetrayer = true;
						player.handleDonation(10, "Prayer Betrayer");
						player.sendMessage("You've purchased: [" + Colors.red + "Prayer Betrayer</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36817":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().avasSecret = true;
						player.handleDonation(5, "Avas Secret");
						player.sendMessage("You've purchased: [" + Colors.red + "Avas Secret</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36818":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().keyExpert = true;
						player.handleDonation(6, "Key Expert");
						player.sendMessage("You've purchased: [" + Colors.red + "Key Expert</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36819":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().dragonTrainer = true;
						player.handleDonation(5, "Dragon Trainer");
						player.sendMessage("You've purchased: [" + Colors.red + "Dragon Trainer</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36820":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().gwdSpecialist = true;
						player.handleDonation(3, "GWD Specialist");
						player.sendMessage("You've purchased: [" + Colors.red + "GWD Specialist</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36821":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().dungeon = true;
						player.handleDonation(10, "Dungeons Master");
						player.sendMessage("You've purchased: [" + Colors.red + "Dungeons Master</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36822":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().petChanter = true;
						player.handleDonation(3, "Petchanter");
						player.sendMessage("You've purchased: [" + Colors.red + "Petchanter</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36823":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().perslaysion = true;
						player.handleDonation(10, "Perslaysion");
						player.sendMessage("You've purchased: [" + Colors.red + "Per'slay'sion</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36824":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().thePiromaniac = true;
						player.handleDonation(6, "Huntsman");
						player.sendMessage("You've purchased: [" + Colors.red + "The Pyromaniac</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36825":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().portsMaster = true;
						player.handleDonation(4, "Ports Master");
						player.sendMessage("You've purchased: [" + Colors.red + "Ports Master</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36826":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().greenThumb = true;
						player.handleDonation(3, "Green Thumb");
						player.sendMessage("You've purchased: [" + Colors.red + "Green Thumb</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36827":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().birdMan = true;
						player.handleDonation(1, "Bird Man");
						player.sendMessage("You've purchased: [" + Colors.red + "Bird Man</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36828":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().unbreakableForge = true;
						player.handleDonation(1, "Unbreakable Forge");
						player.sendMessage("You've purchased: [" + Colors.red + "Unbreakable Forge</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36829":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().sleightOfHand = true;
						player.handleDonation(4, "Sleight of Hand");
						player.sendMessage("You've purchased: [" + Colors.red + "Sleight of Hand</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36830":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().herbivore = true;
						player.handleDonation(8, "Herbivore");
						player.sendMessage("You've purchased: [" + Colors.red + "Herbivore</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36831":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().masterFisherman = true;
						player.handleDonation(5, "Master Fisherman");
						player.sendMessage("You've purchased: [" + Colors.red + "Master Fisherman</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36832":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().delicateCraftsman = true;
						player.handleDonation(3, "Delicate Craftsman");
						player.sendMessage("You've purchased: [" + Colors.red + "Delicate Craftsman</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36833":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().elfFiend = true;
						player.handleDonation(4, "Elf Fiend");
						player.sendMessage("You've purchased: [" + Colors.red + "Elf Fiend</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36834":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().masterChef = true;
						player.handleDonation(3, "Master Chefs Man");
						player.sendMessage("You've purchased: [" + Colors.red + "Master Chefs Man</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36835":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().masterDiviner = true;
						player.handleDonation(10, "Master Diviner");
						player.sendMessage("You've purchased: [" + Colors.red + "Master Diviner</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36836":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().quarryMaster = true;
						player.handleDonation(5, "Quarrymaster");
						player.sendMessage("You've purchased: [" + Colors.red + "Quarrymaster</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36837":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().huntsman = true;
						player.handleDonation(6, "Huntsman");
						player.sendMessage("You've purchased: [" + Colors.red + "Huntsman</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36838":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().divineDoubler = true;
						player.handleDonation(11, "Divine Doubler");
						player.sendMessage("You've purchased: [" + Colors.red + "Divine Doubler</col>]. ");

						break;

					case "36840":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().imbuedFocus = true;
						player.handleDonation(5, "Imbued Focus");
						player.sendMessage("You've purchased: [" + Colors.red + "Imbued Focus</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;

					case "36841":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().alchemicSmith = true;
						player.handleDonation(10, "Alchemic Smithing");
						player.sendMessage("You've purchased: [" + Colors.red + "Alchemic Smithing</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36842":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().bankCommand = true;
						player.handleDonation(25, "Bank Command");
						player.sendMessage("You've purchased: [" + Colors.red + "Bank Command</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36843":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().staminaBoost = true;
						player.handleDonation(5, "Stamina Boost");
						player.sendMessage("You've purchased: [" + Colors.red + "Stamina Boost</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36844":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().overclocked = true;
						player.handleDonation(6, "Overclocked");
						player.sendMessage("You've purchased: [" + Colors.red + "Overclocked</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36845":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().miniGamer = true;
						player.handleDonation(5, "The Mini-Gamer");
						player.sendMessage("You've purchased: [" + Colors.red + "The Mini-Gamer</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36846":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().investigator = true;
						player.handleDonation(10, "Investigator");
						player.sendMessage("You've purchased: [" + Colors.red + "Investigator</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "30466":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(13663, 1);
						player.handleDonation(10, "200 Bank");
						player.sendMessage("You've purchased: [" + Colors.red + "+1 Bank Containers</col>]. "
								+ "tear the circus ticket to use.");

						break;
					case "30467":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(13663, 3);
						player.handleDonation(25, "+3 Bank Containers");
						player.sendMessage("You've purchased: [" + Colors.red + "+3 Bank Containers</col>]. "
								+ "tear the circus ticket to use.");

						break;
					case "30468":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(13663, 7);
						player.handleDonation(50, "+7 Bank Containers");
						player.sendMessage("You've purchased: [" + Colors.red + "+7 Bank Containers</col>]. "
								+ "tear the circus ticket to use.");

						break;
					case "36891":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().petLoot = true;
						player.handleDonation(10, "petLoot");
						player.sendMessage("You've purchased: [" + Colors.red + "petLoot</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36890":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().masterFledger = true;
						player.handleDonation(6, "masterFledger");
						player.sendMessage("You've purchased: [" + Colors.red + "Master Fledger</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "30478":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().birdMan = true;
						player.getPerkManager().charmCollector = true;
						player.getPerkManager().coinCollector = true;
						player.getPerkManager().keyExpert = true;
						player.getPerkManager().petChanter = true;
						player.getPerkManager().petLoot = true;
						player.handleDonation(21, "Looters perk pack");
						player.sendMessage("You've purchased: [" + Colors.red + "Looters perk pack</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36851":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().greenThumb = true;
						player.getPerkManager().unbreakableForge = true;
						player.getPerkManager().sleightOfHand = true;
						player.getPerkManager().herbivore = true;
						player.getPerkManager().masterFisherman = true;
						player.getPerkManager().delicateCraftsman = true;
						player.getPerkManager().masterChef = true;
						player.getPerkManager().masterDiviner = true;
						player.getPerkManager().quarryMaster = true;
						player.getPerkManager().masterFledger = true;
						player.getPerkManager().thePiromaniac = true;
						player.getPerkManager().huntsman = true;
						player.getPerkManager().divineDoubler = true;
						player.getPerkManager().imbuedFocus = true;
						player.getPerkManager().alchemicSmith = true;
						player.handleDonation(81, "Skillers perk pack");
						player.sendMessage("You've purchased: [" + Colors.red + "Skillers perk pack</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36852":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().bankCommand = true;
						player.getPerkManager().staminaBoost = true;
						player.getPerkManager().overclocked = true;
						player.getPerkManager().elfFiend = true;
						player.getPerkManager().miniGamer = true;
						player.getPerkManager().portsMaster = true;
						player.getPerkManager().investigator = true;
						player.handleDonation(54, "Utility perk pack");
						player.sendMessage("You've purchased: [" + Colors.red + "Utility perk pack</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36853":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().familiarExpert = true;
						player.getPerkManager().chargeBefriender = true;
						player.getPerkManager().prayerBetrayer = true;
						player.getPerkManager().avasSecret = true;
						player.getPerkManager().dragonTrainer = true;
						player.getPerkManager().gwdSpecialist = true;
						player.getPerkManager().dungeon = true;
						player.getPerkManager().perslaysion = true;
						player.handleDonation(55, "Combatants perk pack");
						player.sendMessage("You've purchased: [" + Colors.red + "Combatants perk pack</col>]. "
								+ "Type ;;perks to see all your game perks.");

						break;
					case "36854":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().birdMan = true;
						player.getPerkManager().charmCollector = true;
						player.getPerkManager().coinCollector = true;
						player.getPerkManager().keyExpert = true;
						player.getPerkManager().petChanter = true;
						player.getPerkManager().petLoot = true;
						player.getPerkManager().greenThumb = true;
						player.getPerkManager().unbreakableForge = true;
						player.getPerkManager().sleightOfHand = true;
						player.getPerkManager().herbivore = true;
						player.getPerkManager().masterFisherman = true;
						player.getPerkManager().delicateCraftsman = true;
						player.getPerkManager().masterChef = true;
						player.getPerkManager().masterDiviner = true;
						player.getPerkManager().quarryMaster = true;
						player.getPerkManager().masterFledger = true;
						player.getPerkManager().thePiromaniac = true;
						player.getPerkManager().huntsman = true;
						player.getPerkManager().divineDoubler = true;
						player.getPerkManager().imbuedFocus = true;
						player.getPerkManager().alchemicSmith = true;
						player.getPerkManager().birdMan = true;
						player.getPerkManager().bankCommand = true;
						player.getPerkManager().staminaBoost = true;
						player.getPerkManager().overclocked = true;
						player.getPerkManager().elfFiend = true;
						player.getPerkManager().miniGamer = true;
						player.getPerkManager().portsMaster = true;
						player.getPerkManager().investigator = true;
						player.getPerkManager().familiarExpert = true;
						player.getPerkManager().chargeBefriender = true;
						player.getPerkManager().prayerBetrayer = true;
						player.getPerkManager().avasSecret = true;
						player.getPerkManager().dragonTrainer = true;
						player.getPerkManager().gwdSpecialist = true;
						player.getPerkManager().dungeon = true;
						player.getPerkManager().perslaysion = true;
						player.handleDonation(213, "Complete perk pack");
						player.sendMessage(" You've purchased: [" + Colors.red + "Complete perk pack</col>]. "
								+ "Type ;;perks to see all your game perks!");

						break;
					case "44000000":
						player.getAchManager().addKeyAmount("donator", 1);
						if (!player.getInventory().hasFreeSlots())
							player.getBank().addItem(23880, 1, true);
						else
							player.getInventory().addItem(23880, 1);
						player.handleDonation(3, "Infernal gaze aura");
						player.sendMessage("You've purchased: [" + Colors.red + "Infernal gaze aura]!</col>");
						break;

					case "45000000":
						player.getAchManager().addKeyAmount("donator", 1);
						if (!player.getInventory().hasFreeSlots())
							player.getBank().addItem(23882, 1, true);
						else
							player.getInventory().addItem(23882, 1);
						player.handleDonation(3, "Serene gaze aura");
						player.sendMessage("You've purchased: [" + Colors.red + "Serene gaze aura]!</col>");

						break;

					case "46000000":
						player.getAchManager().addKeyAmount("donator", 1);
						if (!player.getInventory().hasFreeSlots())
							player.getBank().addItem(23884, 1, true);
						else
							player.getInventory().addItem(23884, 1);
						player.handleDonation(3, "Vernal gaze aura");
						player.sendMessage("You've purchased: [" + Colors.red + "Vernal gaze aura]!</col>");

						break;

					case "47000000":
						player.getAchManager().addKeyAmount("donator", 1);
						if (!player.getInventory().hasFreeSlots())
							player.getBank().addItem(23888, 1, true);
						else
							player.getInventory().addItem(23888, 1);
						player.handleDonation(3, "Mystical gaze aura");
						player.sendMessage("You've purchased: [" + Colors.red + "Mystical gaze aura]!</col>");

						break;

					case "48000000":
						player.getAchManager().addKeyAmount("donator", 1);
						if (!player.getInventory().hasFreeSlots())
							player.getBank().addItem(23890, 1, true);
						else
							player.getInventory().addItem(23890, 1);
						player.handleDonation(3, "Blazing gaze aura");
						player.sendMessage("You've purchased: [" + Colors.red + "Blazing gaze aura]!</col>");

						break;
					case "49000000":
						player.getAchManager().addKeyAmount("donator", 1);
						if (!player.getInventory().hasFreeSlots())
							player.getBank().addItem(23886, 1, true);
						else
							player.getInventory().addItem(23886, 1);
						player.handleDonation(3, "Nocturnal gaze aura");
						player.sendMessage("You've purchased: [" + Colors.red + "Nocturnal gaze aura]!</col>");

						break;
					case "50000000":
						player.getAchManager().addKeyAmount("donator", 1);
						if (!player.getInventory().hasFreeSlots())
							player.getBank().addItem(23892, 1, true);
						else
							player.getInventory().addItem(23892, 1);
						player.handleDonation(3, "Abyssal gaze aura");
						player.sendMessage("You've purchased: [" + Colors.red + "Abyssal gaze aura]!</col>");

						break;
					case "30483":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(25430, 1);
						player.handleDonation(1, "+1 Keepsake Key");
						player.sendMessage("You've purchased: [" + Colors.red + "+1 Keepsake Key</col>]. ");

						break;

					case "30489":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(25430, 3);
						player.handleDonation(2, "+3 Keepsake Key");
						player.sendMessage("You've purchased: [" + Colors.red + "+3 Keepsake Key</col>]. ");

						break;

					case "30490":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(25430, 7);
						player.handleDonation(3, "+7 Keepsake Key");
						player.sendMessage("You've purchased: [" + Colors.red + "+7 Keepsake Key</col>]. ");

						break;

					case "54000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(25430, 10);
						player.handleDonation(5, "+10 Keepsake Key");
						player.sendMessage("You've purchased: [" + Colors.red + "+10 Keepsake Key</col>]. ");

						break;
					case "55000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasArcaneFishing = true;
						player.handleDonation(2, "Arcane Fishing");
						player.sendMessage("You've purchased: [" + Colors.red + "Arcane Fishing</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "56000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasStrongBurial = true;
						player.handleDonation(2, "Strongarm Burial");
						player.sendMessage("You've purchased: [" + Colors.red + "Strongarm Burial</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "57000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasArcaneCook = true;
						player.handleDonation(2, "Arcane Cooking");
						player.sendMessage("You've purchased: [" + Colors.red + "Arcane Cooking</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "58000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasPowerDivination = true;
						player.handleDonation(2, "Powerful Divination");
						player.sendMessage("You've purchased: [" + Colors.red + "Powerful Divination</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "59000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasPowerConversion = true;
						player.handleDonation(2, "Powerful Conversion");
						player.sendMessage("You've purchased: [" + Colors.red + "Powerful Conversion</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "60000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasAgileDivination = true;
						player.handleDonation(2, "Agile Divination");
						player.sendMessage("You've purchased: [" + Colors.red + "Agile Divination</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "61000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasSinisterSlumber = true;
						player.handleDonation(2, "Sinister Slumber");
						player.sendMessage("You've purchased: [" + Colors.red + "Sinister Slumber</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "62000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasCrystalResting = true;
						player.handleDonation(2, "Crystal Impling Resting");
						player.sendMessage("You've purchased: [" + Colors.red + "Crystal Impling Resting</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "63000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasHeadMining = true;
						player.handleDonation(2, "Headbutt Mining");
						player.sendMessage("You've purchased: [" + Colors.red + "Headbutt Mining</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "64000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasSandWalk = true;
						player.handleDonation(4, "Sandstorm Walk");
						player.sendMessage("You've purchased: [" + Colors.red + "Sandstorm Walk</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "65000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasProudWalk = true;
						player.handleDonation(4, "Proud Walk");
						player.sendMessage("You've purchased: [" + Colors.red + "Proud Walk</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "66000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasBarbarianWalk = true;
						player.handleDonation(4, "Barbarian Walk");
						player.sendMessage("You've purchased: [" + Colors.red + "Barbarian Walk</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "67000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasRevenantWalk = true;
						player.handleDonation(4, "Revenant Walk");
						player.sendMessage("You've purchased: [" + Colors.red + "Revenant Walk</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "68000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasBattleCry = true;
						player.handleDonation(2, "Slayer Battle Cry");
						player.sendMessage("You've purchased: [" + Colors.red + "Slayer Battle Cry</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "69000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasEnhancedPotion = true;
						player.handleDonation(2, "Enhanced Potion Making");
						player.sendMessage("You've purchased: [" + Colors.red + "Enhanced Potion Making</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "70000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasLumberjackWc = true;
						player.handleDonation(2, "Lumberjack Woodcutting");
						player.sendMessage("You've purchased: [" + Colors.red + "Lumberjack Woodcutting</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "71000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasDeepFishing = true;
						player.handleDonation(2, "Deep-Sea Fishing");
						player.sendMessage("You've purchased: [" + Colors.red + "Deep-Sea Fishing</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "72000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasZenResting = true;
						player.handleDonation(2, "Zen Resting");
						player.sendMessage("You've purchased: [" + Colors.red + "Zen Resting</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "73000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasKarateFletch = true;
						player.handleDonation(2, "Karate-Chop Fletching");
						player.sendMessage("You've purchased: [" + Colors.red + "Karate-Chop Fletching</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "74000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasIronSmith = true;
						player.handleDonation(2, "Iron-Fist Smithing");
						player.sendMessage("You've purchased: [" + Colors.red + "Iron-Fist Smithing</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "75000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasChiMining = true;
						player.handleDonation(2, "Chi-Blast Mining");
						player.sendMessage("You've purchased: [" + Colors.red + "Chi-Blast Mining</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "76000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasSamuraiCook = true;
						player.handleDonation(2, "Samurai Cooking");
						player.sendMessage("You've purchased: [" + Colors.red + "Samurai Cooking</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "77000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasRoundHouseWc = true;
						player.handleDonation(2, "Roundhouse Woodcutting");
						player.sendMessage("You've purchased: [" + Colors.red + "Roundhouse Woodcutting</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;

					case "78000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasChiMining = true;
						player.handleDonation(2, "Chi-Blast Mining");
						player.sendMessage("You've purchased: [" + Colors.red + "Chi-Blast Mining</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "79000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasArcaneSmelt = true;
						player.handleDonation(2, "Arcane Smelting");
						player.sendMessage("You've purchased: [" + Colors.red + "Arcane Smelting</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "80000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasArcaneResting = true;
						player.handleDonation(2, "Arcane Resting");
						player.sendMessage("You've purchased: [" + Colors.red + "Arcane Resting</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "81000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasStrongWc = true;
						player.handleDonation(2, "Strongarm Woodcutting");
						player.sendMessage("You've purchased: [" + Colors.red + "Strongarm Woodcutting</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "82000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasStrongMining = true;
						player.handleDonation(2, "Strongarm Mining");
						player.sendMessage("You've purchased: [" + Colors.red + "Strongarm Mining</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "83000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasSadWalk = true;
						player.handleDonation(4, "Sad Walk");
						player.sendMessage("You've purchased: [" + Colors.red + "Sad Walk</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "84000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasHappyWalk = true;
						player.handleDonation(4, "Happy Walk");
						player.sendMessage("You've purchased: [" + Colors.red + "Happy Walk</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "85000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasAgileConversion = true;
						player.handleDonation(2, "Agile Conversion");
						player.sendMessage("You've purchased: [" + Colors.red + "Agile Conversion</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "86000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasStrongResting = true;
						player.handleDonation(2, "Strongarm Resting");
						player.sendMessage("You've purchased: [" + Colors.red + "Strongarm Resting</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "87000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasEneResting = true;
						player.handleDonation(2, "Energy Drain Resting");
						player.sendMessage("You've purchased: [" + Colors.red + "Energy Drain Resting</col>]. "
								+ "Talk to Solomon to toggle it on/off!");

						break;
					case "88000000":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getAnimations().hasArmWarrior = true;
						player.handleDonation(2, "Armchair Warrior");
						player.sendMessage("You've purchased: [" + Colors.red + "Armchair Warrior</col>]. "
								+ "Talk to Solomon to toggle it on/off!");
						break;
					case "30491":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getSquealOfFortune().giveBoughtSpins(5);
						player.handleDonation(2, "x5 SoF spins");
						player.sendMessage("You've purchased: [" + Colors.red + "x5 SoF spins</col>]. "
								+ "Open the Squeal of Fortune tab to use them.");
						World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
								+ " </col> Donated for </col>  [" + Colors.yellow + "x5 SoF spins"
								+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
								+ " Donation.", false);
						break;
					case "36849":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getSquealOfFortune().giveBoughtSpins(27);
						player.handleDonation(10, "x25 SoF spins");
						player.sendMessage("You've purchased: [" + Colors.red + "x27 SoF spins</col>]. "
								+ "Open the Squeal of Fortune tab to use them.");
						World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
								+ " </col> Donated for </col>  [" + Colors.yellow + "x25 SoF spins"
								+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
								+ " Donation.", false);
						break;
					case "36850":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getSquealOfFortune().giveBoughtSpins(55);
						player.handleDonation(20, "x50 SoF spins");
						player.sendMessage("You've purchased: [" + Colors.red + "x55 SoF spins</col>]. "
								+ "Open the Squeal of Fortune tab to use them.");
						World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
								+ " </col> Donated for </col>  [" + Colors.yellow + "x50 SoF spins"
								+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
								+ " Donation.", false);
						break;
					case "30494":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getSquealOfFortune().giveBoughtSpins(175);
						player.handleDonation(50, "x150 SoF spins");
						player.sendMessage("You've purchased: [" + Colors.red + "x175 SoF spins</col>]. "
								+ "Open the Squeal of Fortune tab to use them.");
						World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
								+ " </col> Donated for </col>  [" + Colors.yellow + "x150 SoF spins"
								+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
								+ " Donation.", false);
						break;
					case "30496":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getSquealOfFortune().giveBoughtSpins(350);
						player.handleDonation(100, "x300 SoF spins");
						player.sendMessage("You've purchased: [" + Colors.red + "x350 SoF spins</col>]. "
								+ "Open the Squeal of Fortune tab to use them.");
						World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
								+ " </col> Donated for </col>  [" + Colors.yellow + "x300 SoF spins"
								+ "</col>] and has now a total of " + Colors.red + "$" + player.getMoneySpent()
								+ " Donation.", false);
						break;
					case "36847":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(41397, 5);
						player.handleDonation(5, "x5 Donator Token");
						player.sendMessage("You've purchased: [" + Colors.red + "5 Donator Token</col>]. "
								+ "Go to Members Area to Check the Shop.");
						break;
					case "30498":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(41397, 10);
						player.handleDonation(10, "x10 Pot of Gold");
						player.sendMessage("You've purchased: [" + Colors.red + "10 Pot of Gold</col>]. "
								+ "Go to Members Area to Check the Shop.");
						break;

					case "36848":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(41397, 22);
						player.handleDonation(20, "x20 Donator Token");
						player.sendMessage("You've purchased: [" + Colors.red + "20 + 2[FREE] Donator Token</col>]. "
								+ "Go to Members Area to Check the Shop.");
						break;
					case "30500":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getInventory().addItem(41397, 55);
						player.handleDonation(50, "x50 Pot of Gold");
						player.sendMessage("You've purchased: [" + Colors.red + "50 +5[FREE] Pot of Gold</col>]. "
								+ "Go to Members Area to Check the Shop.");
						break;

					case "36565":
						player.getInventory().addItem(34896, 1);
						player.sendMessage("You've purchased: [" + Colors.red + "1usd Store credit.] "
								+ "Open the Astomancer credit to claim the credit!.");
						World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
								+ " </col> Donated </col>" + "</col> 1 usd Store Credit! and has now a total of "
								+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
						break;
					case "36566":
						player.getInventory().addItem(34896, 3);
						player.sendMessage("You've purchased: [" + Colors.red + "3usd Store credit.] "
								+ "Open the Astomancer credit to claim the credit!.");
						World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
								+ " </col> Donated </col>" + "</col> 3 usd Store Credit! and has now a total of "
								+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
						break;
					case "36567":
						player.getInventory().addItem(34896, 5);
						player.sendMessage("You've purchased: [" + Colors.red + "5usd Store credit.] "
								+ "Open the Astomancer credit to claim the credit!.");
						World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
								+ " </col> Donated </col>" + "</col> 5 usd Store Credit! and has now a total of "
								+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
						break;
					case "36568":
						player.getInventory().addItem(34896, 10);
						player.sendMessage("You've purchased: [" + Colors.red + "10usd Store credit.] "
								+ "Open the Astomancer credit to claim the credit!.");
						World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
								+ " </col> Donated </col>" + "</col> 10 usd Store Credit! and has now a total of "
								+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
						break;
					case "36569":
						player.getInventory().addItem(34896, 20);
						player.sendMessage("You've purchased: [" + Colors.red + "20usd Store credit.] "
								+ "Open the Astomancer credit to claim the credit!.");
						World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
								+ " </col> Donated </col>" + "</col> 20 usd Store Credit! and has now a total of "
								+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
						break;

					case "50620": // valkyrie set
						player.handleDonation(480, "Valkyrie Set");
						player.getInventory().addItem(44330, 1);
						player.getInventory().addItem(44331, 1);
						player.getInventory().addItem(44332, 1);
						player.getInventory().addItem(44335, 1);
						player.sendMessage("You've purchased: [" + Colors.red + "Valkyrie Set!.");

						break;
					case "36570":
						player.getInventory().addItem(34896, 50);
						player.sendMessage("You've purchased: [" + Colors.red + "50usd Store credit.] "
								+ "Open the Astomancer credit to claim the credit!.");
						World.sendWorldMessage(Colors.green + "[Donation] </col>" + Colors.red + player.getUsername()
								+ " </col> Donated </col>" + "</col> 50 usd Store Credit! and has now a total of "
								+ Colors.red + "$" + player.getMoneySpent() + " Donation.", false);
						break;
					case "38044":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().petmaster = true;
						player.handleDonation(20, "D'Companion");
						player.sendMessage("You've purchased: [" + Colors.red + "D'Companion</col>]. "
								+ "Type ;;perks to see all your game perks.");
						break;
					case "38045":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().basher = true;
						player.handleDonation(20, "Stagï¿½ger");
						player.sendMessage("You've purchased: [" + Colors.red + "Stagï¿½ger</col>]. "
								+ "Type ;;perks to see all your game perks.");
						break;
					case "38046":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().butcher = true;
						player.handleDonation(20, "Annihilator");
						player.sendMessage("You've purchased: [" + Colors.red + "Annihilator</col>]. "
								+ "Type ;;perks to see all your game perks.");
						break;

					case "38047":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().lifeSteal = true;
						player.handleDonation(20, "Dominator");
						player.sendMessage("You've purchased: [" + Colors.red + "Dominator</col>]. "
								+ "Type ;;perks to see all your game perks.");
						break;
					case "38048":
						player.getAchManager().addKeyAmount("donator", 1);
						player.getPerkManager().regenerator = true;
						player.handleDonation(20, "Heart of tarrasque");
						player.sendMessage("You've purchased: [" + Colors.red + "Heart of tarrasque</col>]. "
								+ "Type ;;perks to see all your game perks.");
						break;
					}
				}
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Home locations
	 */

	private WorldTile homeLocation;
	public String homeName;
	public boolean isDefskill;

	public boolean isDefskill() {
		return isDefskill;
	}

	public WorldTile getHome() {
		return homeLocation;
	}

	public void setHome(WorldTile home, String name) {
		this.homeLocation = home;
		this.homeName = name;
	}

	public String getHomeName() {
		return homeName;
	}

	public void setDeathManager(DeathManager manager) {
		deathManager = manager;
	}

	private int PAdamage;

	public void setPAdamage(int PAdamage) {
		this.PAdamage = PAdamage;
	}

	// darklord
	public WorldTile getTile() {
		return new WorldTile(getX(), getY(), getPlane());
	}

	public int getPAdamage() {
		return PAdamage;
	}

	public int getGravestone() {
		return gravestone;
	}

	public void setGravestone(int id) {
		this.gravestone = id;
	}

	public DeathManager getDeathManager() {
		return deathManager;
	}

	private DeathManager deathManager;

	private transient Instance currentInstance;

	public Instance getCurrentInstance() {
		return currentInstance;
	}

	public void setCurrentInstance(Instance instance) {
		currentInstance = instance;
	}

	public String getDonorColor() {
		String color = getUsername().contentEquals("zeus") ? Colors.rcyan + Colors.shad
				: getUsername().equalsIgnoreCase("") ? Colors.darkRed + Colors.shad
						: isBronze() ? "<col=C96800>"
								: isSilver() ? Colors.gray
										: isGold() ? Colors.yellow
												: isPlatinum() ? "<col=697998>"
														: isDiamond() ? "<col=05FFC9>"
																: isSponsor() ? "<col=ff8c00>" : "";
		return color;
	}

	public String getDonorRank() {
		String rank = isBronze() ? "<col=C96800>Bronze Donor</col>"
				: isSilver() ? Colors.gray + "Silver Donor</col>"
						: isGold() ? Colors.yellow + "Gold Donor</col>"
								: isPlatinum() ? Colors.purple + "<col=697998>Platinum Donor</col>"
										: isDiamond() ? Colors.blue + "<col=05FFC9>Diamond Donor</col>"
												: isSponsor() ? "<col=ff8c00>Sponsor Donor</col>" : "";
		return rank;
	}

	private boolean filterLocked;

	public boolean isFilterLocked() {
		return filterLocked;
	}

	public void setFilterLocked(boolean filterLocked) {
		this.filterLocked = filterLocked;
	}

	private int HelwyrCoins;

	public int getHelwyrCoins() {
		return HelwyrCoins;
	}

	public void setHelwyrCoins(int HelwyrCoins) {
		this.HelwyrCoins = HelwyrCoins;
	}

	public void addHelwyrCoins(int amount) {
		this.HelwyrCoins += amount;
	}

	private long quadExp;

	public long getQuadExp() {
		return quadExp;
	}

	public void setQuadExp(long quadExp) {
		this.quadExp = quadExp;
		potionTimer.slotTimerArray[PotionTimersInter.QUADXP] = quadExp;
	}

	public int ballakdmg;

	public DailyLoginManager getLoginManager() {
		return loginManager;
	}

	public void setLoginManager(DailyLoginManager loginManager) {
		this.loginManager = loginManager;
	}

	private DailyLoginManager loginManager;

	PotionTimers potionTimer;

	public PotionTimers getPotiontimers() {
		return potionTimer;
	}

	public void setPotionTimers(PotionTimers timers) {
		this.potionTimer = timers;
	}

	public long arealootValue;

	public int authclaimed;

	public int arealootFilterMode = 0;

	private boolean isBot = false;


	public void enhancedHeal(int amount, int extraHP) {
		try {
			double healMultiplier = 1.0;

			// Check for Swift Recovery effect
			if (isUnderCombat()) {
				Object lastTarget = getTemporaryAttributtes().get("last_target");
				if (lastTarget instanceof com.rs.game.npc.NPC) {
					com.rs.game.npc.NPC targetNPC = (com.rs.game.npc.NPC) lastTarget;

					java.util.Set<CombatMastery.SpecialEffect> effects = CombatMastery.getAvailableEffects(this,
							targetNPC);

					if (effects.contains(CombatMastery.SpecialEffect.SWIFT_RECOVERY)) {
						healMultiplier = 1.15; // +15% food healing bonus

						// Occasional message (don't spam)
						if (com.rs.utils.Utils.random(8) == 0) {
							sendMessage(com.rs.utils.Colors.lightGray + "Swift Recovery enhances healing!"
									+ com.rs.utils.Colors.eshad);
						}
					}
				}
			}

			// Apply enhanced healing
			int enhancedAmount = (int) (amount * healMultiplier);
			int enhancedExtra = (int) (extraHP * healMultiplier);
			heal(enhancedAmount, enhancedExtra);

		} catch (Exception e) {
			// Fallback to normal healing if mastery check fails
			heal(amount, extraHP);
		}
	}

	// ===== ADD THESE FIELDS TO YOUR Player.java CLASS =====

	// Auto-thieving specific fields (similar to woodcutting)
	private transient int autoThievingStall = -1; // Current target stall index (-1 = none selected)
	private String autoThievingMode = "AUTO"; // "AUTO" or "SPECIFIC" mode
	private boolean stopOnRogue = true; // Stop when rogue appears

	// ===== ADD THESE METHODS TO YOUR Player.java CLASS =====

	// Auto-thieving stall management
	public int getAutoThievingStall() {
		return autoThievingStall;
	}

	public void setAutoThievingStall(int stall) {
		this.autoThievingStall = stall;
	}

	// Auto-thieving mode management (AUTO vs SPECIFIC)
	public String getAutoThievingMode() {
		// Ensure mode is never null, default to AUTO
		if (autoThievingMode == null || autoThievingMode.isEmpty()) {
			autoThievingMode = "AUTO";
		}
		return autoThievingMode;
	}

	public void setAutoThievingMode(String mode) {
		this.autoThievingMode = mode;
	}

	// Rogue encounter setting
	public boolean isStopOnRogue() {
		return stopOnRogue;
	}

	public void setStopOnRogue(boolean stopOnRogue) {
		this.stopOnRogue = stopOnRogue;
	}

	// Add these fields to your Player class:
	private long autoSkillingUsedTime = 0; // Total time used today for unified skills
	private long autoSkillingLastReset = 0; // Last daily reset timestamp
	private long autoSkillingStartTime = 0; // Session start time
	private AutoSkillingState autoSkillingState = AutoSkillingState.STOPPED;
	private SkillingType autoSkillingType = null; // Current skill being automated
	private TreeDefinitions autoWoodcuttingTree = null; // Current tree type for woodcutting
	private InventoryAction skillingInventoryAction = InventoryAction.AUTO_BANK;

	// Add these getter and setter methods to your Player class:

	// Auto-skilling used time
	public long getAutoSkillingUsedTime() {
		return autoSkillingUsedTime;
	}

	public void setAutoSkillingUsedTime(long autoSkillingUsedTime) {
		this.autoSkillingUsedTime = autoSkillingUsedTime;
	}

	// Auto-skilling last reset time
	public long getAutoSkillingLastReset() {
		return autoSkillingLastReset;
	}

	public void setAutoSkillingLastReset(long autoSkillingLastReset) {
		this.autoSkillingLastReset = autoSkillingLastReset;
	}

	// Auto-skilling start time
	public long getAutoSkillingStartTime() {
		return autoSkillingStartTime;
	}

	public void setAutoSkillingStartTime(long autoSkillingStartTime) {
		this.autoSkillingStartTime = autoSkillingStartTime;
	}

	// Auto-skilling state
	public AutoSkillingState getAutoSkillingState() {
		return autoSkillingState;
	}

	public void setAutoSkillingState(AutoSkillingState autoSkillingState) {
		this.autoSkillingState = autoSkillingState;
	}

	// Auto-skilling type
	public SkillingType getAutoSkillingType() {
		return autoSkillingType;
	}

	public void setAutoSkillingType(SkillingType autoSkillingType) {
		this.autoSkillingType = autoSkillingType;
	}

	// Auto-woodcutting tree
	public TreeDefinitions getAutoWoodcuttingTree() {
		return autoWoodcuttingTree;
	}

	public void setAutoWoodcuttingTree(TreeDefinitions autoWoodcuttingTree) {
		this.autoWoodcuttingTree = autoWoodcuttingTree;
	}

	// Skilling inventory action
	public InventoryAction getSkillingInventoryAction() {
		return skillingInventoryAction;
	}

	public void setSkillingInventoryAction(InventoryAction skillingInventoryAction) {
		this.skillingInventoryAction = skillingInventoryAction;
	}

	private String autoWoodcuttingMode = "AUTO"; // Default to automatic

	public String getAutoWoodcuttingMode() {
		return autoWoodcuttingMode;
	}

	public void setAutoWoodcuttingMode(String mode) {
		this.autoWoodcuttingMode = mode;
	}

	// Mining mode and rock selection
	private String autoMiningMode; // "AUTO" or "SPECIFIC"
	private RockDefinitions autoMiningRock; // Target rock type

	// Getters and setters
	public String getAutoMiningMode() {
		return autoMiningMode;
	}

	public void setAutoMiningMode(String mode) {
		this.autoMiningMode = mode;
	}

	public RockDefinitions getAutoMiningRock() {
		return autoMiningRock;
	}

	public void setAutoMiningRock(RockDefinitions rock) {
		this.autoMiningRock = rock;
	}

	private String autoFishingMode = "AUTO";
	private Fishing.FishingSpots autoFishingSpot = null;

	public String getAutoFishingMode() {
		return autoFishingMode;
	}

	public void setAutoFishingMode(String mode) {
		this.autoFishingMode = mode;
	}

	public Fishing.FishingSpots getAutoFishingSpot() {
		return autoFishingSpot;
	}

	public void setAutoFishingSpot(Fishing.FishingSpots spot) {
		this.autoFishingSpot = spot;
	}

	public void init(Session session, String string, IsaacKeyPair isaacKeyPair) {
		username = string;
		this.session = session;
		this.isaacKeyPair = isaacKeyPair;
		World.addPlayer(this);// .addLobbyPlayer(this);
		if (Settings.DEBUG) {
			Logger.log(this, new StringBuilder("Lobby Inited Player: ").append(string).append(", pass: ").append(password).toString());
		}
	}
	public void init(Session session, String username, int displayMode, int screenWidth, int screenHeight, MachineInformation machineInformation, IsaacKeyPair isaacKeyPair) {
		
		if (dominionTower == null) {
			dominionTower = new DominionTower();
		}
		if (farmingManager == null) {
			farmingManager = new FarmingManager();
		}
		if (auraManager == null) {
			auraManager = new AuraManager();
		}
		
		
		if (house == null) {
			house = new House();
		}
		
		if (squealOfFortune == null) {
			squealOfFortune = new SquealOfFortune();
		}
		// if (RoomConstruction == null)
		// RoomConstruction = new RoomConstruction(this);
		if (questManager == null) {
			questManager = new QuestManager();
		}
		

//		}
		this.session = session;
		this.username = username;
		this.displayMode = displayMode;
	
		this.isaacKeyPair = isaacKeyPair;

		
		interfaceManager = new InterfaceManager(this);
		loyaltyManager = new LoyaltyManager(this);
		
		
		squealOfFortune.setPlayer(this);

	
		getLoyaltyManager().startTimer();
		dungManager = new DungManager();
        dayOfWeekManager = new DayOfWeekManager();
        dailyTaskManager = new DailyTaskManager();
        banksManager = new BanksManager();
        newQuestManager = new NewQuestManager();
        gearPresets = new GearPresets();
        bountyHunter = new BountyHunter();
        elderTreeManager = new ElderTreeManager();
        cHandler = new ContractHandler();
        squealOfFortune = new SquealOfFortune();
        coOpSlayer = new CooperativeSlayer();
        treasureTrails = new TreasureTrails();
        ports = new PlayerOwnedPort();
        xmas = new XmasEvent();
        petLootManager = new PetLootManager();
        perkManager = new PerkManager();
        membership = new MembershipHandler();
        titles = new Titles();
        throne = new ThroneOfMiscellania();
        setDeathManager(new DeathManager());
        setBossTimerManager(new BossTimerManager(this));
        setAchManager(new AchievementManager(this));
        setGrManager(new GRManager(this));
        setResourceGather(new ResourceGatherBuff(this));
        setLoginManager(new DailyLoginManager());
        setPotionTimers(new PotionTimers(this));
        setGlobalPlayerUpdater(new GlobalPlayerUpdater());
		
		dialogueManager = new DialogueManager(this);
		hintIconsManager = new HintIconsManager(this);
		/**
		 * yell colours
		 */

		String yellcolor = "" + getYellColor() + "";
		String yellshad = "" + getYellColor() + "";
		setYellColor(yellcolor);
		setYellColor(yellshad);
		
		priceCheckManager = new PriceCheckManager(this);
	
		localPlayerUpdate = new LocalPlayerUpdate(this);
		localNPCUpdate = new LocalNPCUpdate(this);
		actionManager = new ActionManager(this);
		cutscenesManager = new CutscenesManager(this);
		trade = new Trade(this);
		// loads player on saved instances
		
		// customGear.setPlayer(this);
		inventory.setPlayer(this);
		equipment.setPlayer(this);
		
		// toolbelt.setPlayer(this);
		
		skills.setPlayer(this);
		combatDefinitions.setPlayer(this);
		prayer.setPlayer(this);
		bank.setPlayer(this);
		
		controlerManager.setPlayer(this);
		gearPresets.setPlayer(this);
		farmingManager.setPlayer(this);
		musicsManager.setPlayer(this);
		emotesManager.setPlayer(this);
		friendsIgnores.setPlayer(this);
		dominionTower.setPlayer(this);
		auraManager.setPlayer(this);
		charges.setPlayer(this);
		questManager.setPlayer(this);
		petManager.setPlayer(this);
		DivineObject.resetGatherLimit(this);
		house.setPlayer(this);
		setDirection(Utils.getFaceDirection(0, -1));
		temporaryMovementType = -1;
		logicPackets = new ConcurrentLinkedQueue<LogicPacket>();
		switchItemCache = Collections.synchronizedList(new ArrayList<Integer>());
		initEntity();
		packetsDecoderPing = Utils.currentTimeMillis();
		World.addPlayer(this);
		
		
		World.updateEntityRegion(this);
		
		if (Settings.DEBUG) {
			Logger.log(this, "Initiated player: " + username + ", pass: " + password);
		}

	}

}
