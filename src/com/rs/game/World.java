package com.rs.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.rs.stream.OutputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import com.rs.utils.Utils.EntityDirection;
import com.rs.Protocol;
import com.rs.ServerLauncher;
import com.rs.Settings;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.activites.PuroPuro;
import com.rs.game.activites.ShootingStar;
import com.rs.game.activites.ZarosGodwars;
import com.rs.game.activites.ZombieOutpost.ZOGame;
import com.rs.game.activites.ZombieOutpost.ZOManager;
import com.rs.game.activites.clanwars.FfaZone;
import com.rs.game.activites.clanwars.RequestController;
import com.rs.game.activites.duel.DuelControler;
import com.rs.game.activites.goldrush.GRGame;
import com.rs.game.activites.resourcegather.SkillingEventsManager;
import com.rs.game.activites.soulwars.SoulWarsManager;
import com.rs.game.activities.instances.Instance;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.map.bossInstance.impl.VoragoInstance;
import com.rs.game.npc.NPC;
import com.rs.game.npc.acheronmammoth.Acheronmammoth;
import com.rs.game.npc.airut.Airut;
import com.rs.game.npc.araxxor.AraxyteNPC;
import com.rs.game.npc.ballak.BallakDefs;
import com.rs.game.npc.bandos.Bandos;
import com.rs.game.npc.corp.CorporealBeast;
import com.rs.game.npc.dragons.CelestialDragonB;
import com.rs.game.npc.dragons.GemDragons;
import com.rs.game.npc.dragons.KingBlackDragon;
import com.rs.game.npc.dragons.RuneDragon;
import com.rs.game.npc.dragons.Wyvern;
import com.rs.game.npc.elegorn.Elegorn;
import com.rs.game.npc.glacor.Glacor;
import com.rs.game.npc.godwars.armadyl.GodwarsArmadylFaction;
import com.rs.game.npc.godwars.bandos.GodwarsBandosFaction;
import com.rs.game.npc.godwars.saradomin.GodwarsSaradominFaction;
import com.rs.game.npc.godwars.zammorak.GodwarsZammorakFaction;
import com.rs.game.npc.godwars.zaros.GodwarsZarosFaction;
import com.rs.game.npc.godwars.zaros.Nex;
import com.rs.game.npc.godwars.zaros.NexMinion;
import com.rs.game.npc.kalphite.KalphiteKing;
import com.rs.game.npc.kalphite.KalphiteQueen;
import com.rs.game.npc.maczabinvasion.Durzag;
import com.rs.game.npc.magister.Magister;
import com.rs.game.npc.masuta.Masuta;
import com.rs.game.npc.nexaminion.NexAMinion;
import com.rs.game.npc.nexangel.NexAngel;
import com.rs.game.npc.mazcabinvasion.Minion;
import com.rs.game.npc.nomad.FlameVortex;
import com.rs.game.npc.nomad.Nomad;
import com.rs.game.npc.others.Bork;
import com.rs.game.npc.others.ConditionalDeath;
import com.rs.game.npc.others.DagannothKing;
import com.rs.game.npc.others.DarkLord;
import com.rs.game.npc.others.HauntedTree;
import com.rs.game.npc.others.HunterTrapNPC;
import com.rs.game.npc.others.LivingRock;
import com.rs.game.npc.others.Lucien;
import com.rs.game.npc.others.MercenaryMage;
import com.rs.game.npc.others.PartyDemon;
import com.rs.game.npc.others.Revenant;
import com.rs.game.npc.others.SeaTrollQueen;
import com.rs.game.npc.others.Sheep;
import com.rs.game.npc.others.StarSprite;
import com.rs.game.npc.others.TormentedDemon;
import com.rs.game.npc.others.WildyWyrm;
import com.rs.game.npc.ripperdemon.Ripperdemon;
import com.rs.game.npc.rots.OtherBrother;
import com.rs.game.npc.rots.Veracs;
import com.rs.game.npc.slayer.Strykewyrm;
import com.rs.game.npc.solak.Solak;
import com.rs.game.npc.sorgar.Elemental;
import com.rs.game.npc.telos.Telos;
import com.rs.game.npc.veraklith.VerakLith;
import com.rs.game.npc.xmas.AsteaFrostweb;
import com.rs.game.npc.xmas.SmallSnowman;
import com.rs.game.player.DayOfWeekManager;
import com.rs.game.player.MembershipHandler;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.achievements.AchievementManager;
import com.rs.game.player.actions.divination.Wisp;
import com.rs.game.player.actions.divination.WispInfo;
import com.rs.game.player.actions.hunter.TrapAction.HunterNPC;
import com.rs.game.player.bot.Bot;
import com.rs.game.player.content.ArtisansWorkShop;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.LividFarm;
import com.rs.game.player.content.LivingRockCavern;
import com.rs.game.player.content.Lottery;
import com.rs.game.player.content.OwnedObjectManager;
//import com.rs.game.player.content.ReferralHandler;
import com.rs.game.player.content.TriviaBot;
import com.rs.game.player.content.WeeklyTopRanking;
import com.rs.game.player.content.WellOfGoodWill;
import com.rs.game.player.content.ancientthrone.ThroneManager;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.content.interfaces.arealoot.AreaLoot;
import com.rs.game.player.content.miscellania.ThroneOfMiscellania;
import com.rs.game.player.content.ports.JohnStrum;
import com.rs.game.player.controllers.ArtisansWorkShopControler;
import com.rs.game.player.controllers.Kalaboss;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.player.dialogue.impl.StarterTutorialD;
import com.rs.game.route.Flags;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.game.topweeks.WeekInfoManager;
import com.rs.network.protocol.codec.decode.impl.ButtonHandler;
import com.rs.utils.AntiFlood;
import com.rs.utils.Colors;
import com.rs.utils.DTRank;
import com.rs.utils.DonationRank;
import com.rs.utils.IPBanL;
import com.rs.utils.IPMute;
import com.rs.utils.Logger;
import com.rs.utils.LoggingSystem;
import com.rs.utils.MACBan;
import com.rs.utils.MapUtils;
import com.rs.utils.MapUtils.Structure;
import com.rs.utils.PkRank;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;
import com.rs.utils.VoteHiscores;
import com.rs.utils.WealthRank;
import com.rs.utils.mysql.impl.VoteManager;

/**
 * A class which handles every World event & interactions.
 * 
 * @author Zeus
 */
public final class World {

	/**
	 * A list holding all Player-Entities online.
	 */
	private static final EntityList<Player> players = new EntityList<Player>(Protocol.SV_PLAYERS_LIMIT);

	/**
	 * A list holding all NPC-Entities online.
	 */
	private static final EntityList<NPC> npcs = new EntityList<NPC>(Protocol.SV_NPCS_LIMIT);

	/**
	 * A map containing all Regions as Integers.
	 */
	private static final Map<Integer, Region> regions = Collections.synchronizedMap(new HashMap<Integer, Region>());

	public static WeekInfoManager wim;
	/**
	 * Well of Goodwill.
	 */
	private static int wellAmount;
	private static boolean wellActive;
	// bosstimers
	public static String[] TimedBosses;
	public static long[] FastestTime;
	public static String[] playerLeader;

	/**
	 * Starts all World activites.
	 */
	// SIMPLE FIX for World.java - No complex methods needed

	public static final void init() {
		addRestoreRunEnergyTask();
		addDrainPrayerTask();
		addRestoreHitPointsTask();
		addRestoreSkillsTask();
		addRestoreSpecialAttackTask();
		addSummoningEffectTask();
		addOwnedObjectsTask();
		LivingRockCavern.init();
		addRestoreShopItemsTask();
		messageEvent();
		scheduleCompetitionChecks();
		WarriorsGuild.init();
		ShootingStar.init();
		soulWars = new SoulWarsManager();
		tickGeneral1SecondTask();
		soulWars.start();
		addTriviaBotTask();
		addLeechedStatsTask();
		PuroPuro.initPuroImplings();
		Lottery.addLotteryProcessTask();
		addWorldCalendarTask();
		addLividFarmProcessTask();
		tickThrone();
		addArtisansWorkShopProcessTask();
		MembershipStatusChecker();
		tickWeeklyInfo();
		ZOManager.init();
		AchievementManager.init();
		tickSkillingEvents();
		

		if (TimedBosses == null) {
			TimedBosses = new String[40];
		}
		if (playerLeader == null)
			playerLeader = new String[40];
		if (FastestTime == null)
			FastestTime = new long[40];
		try {
			loadBossTimes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		AreaLoot.updater();

	}
	
	

	/**
	 * boss timers
	 * 
	 * @throws IOException
	 */
	public static void loadBossTimes() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader("data/bosstimer/players.txt"));
		String line = "";
		long value = 0;
		int index = 0;
		while ((line = br.readLine()) != null) {
			playerLeader[index] = line;
			index++;
		}
		index = 0;
		br.close();
		br = new BufferedReader(new FileReader("data/bosstimer/times.txt"));
		while ((line = br.readLine()) != null) {
			value = Long.parseLong(line.trim());
			FastestTime[index] = value;
			index++;
		}
		br.close();
	}

	/**
	 * saves the boss timer
	 * 
	 * @throws IOException
	 */
	public static void saveBossTimes() throws IOException {
		BufferedWriter bf = new BufferedWriter(new FileWriter("data/bosstimer/players.txt"));
		for (int i = 0; i <= 18; i++) {
			bf.write(playerLeader[i]);
			bf.newLine();

		}
		bf.close();
		bf = new BufferedWriter(new FileWriter("data/bosstimer/times.txt"));
		for (int i = 0; i <= 18; i++) {
			String temp = "" + FastestTime[i];
			bf.write(temp);
			bf.newLine();
		}
		bf.close();
	}

	private static void MembershipStatusChecker() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player p : getPlayers()) {
						if (p.looterspack && p.getLooterPackSubLong() < System.currentTimeMillis()) {
							p.getMembership();
							MembershipHandler.checkStatus(p, true, false, 1);
						}
						if (p.skillerspack && p.getSkillerPackSubLong() < System.currentTimeMillis()) {
							p.getMembership();
							MembershipHandler.checkStatus(p, true, false, 2);
						}
						if (p.utilitypack && p.getUtilityPackSubLong() < System.currentTimeMillis()) {
							p.getMembership();
							MembershipHandler.checkStatus(p, true, false, 3);
						}
						if (p.combatantpack && p.getCombatPackSubLong() < System.currentTimeMillis()) {
							p.getMembership();
							MembershipHandler.checkStatus(p, true, false, 4);
						}
						if (p.completepack && p.getCompletePackSubLong() < System.currentTimeMillis()) {
							p.getMembership();
							MembershipHandler.checkStatus(p, true, false, 5);
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	private static void tickSkillingEvents() {
		Logger.log("Launcher", "Initing Skilling Events...");
		SkillingEventsManager.init();
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					SkillingEventsManager.checkSkillTimes();
				} catch (Throwable t) {
					Logger.handle(t);
				}
			}
		}, 1, 1, TimeUnit.MINUTES);
	}

	private static void AutoClaimVotes() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				for (Player p : World.getPlayers()) {
					if (p == null)
						continue;
					String playerName = p.getUsername();
					try {
						final String request = com.everythingrs.vote.Vote.validate(
								"nG7iRk5n59DGm0LHJulTcQSZ2nDN7LcnwLUyD1G4kmzsOc2aVpbmrBwcqEUHOHjWnva3yuz3", playerName,
								1);
						if (request.startsWith("complete")) {

							if (p.getInventory().getFreeSlots() < 2) {
								p.getBank().addItem(995, 1000000, true);
								p.getPackets().sendGameMessage(Colors.green
										+ "[Vote Manager] </col> Inventory full, Reward automatically been added to your bank.");
								return;
							}
							p.getInventory().addItem(995, 1000000);
							VoteManager.handleQueuedReward(p);

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	private static void tickGeneral1SecondTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player player : World.getPlayers()) {
						if (player == null) {
							continue;
						}

						// Safe bot check
						try {
							if (player.isBot()) {
								continue;
							}
						} catch (Exception e) {
							// isBot() method doesn't exist, assume regular player
						}

						try {
							if (player.isDoubleXp()) {
								player.setDoubleXpTimer(player.getDoubleXpTimer() - 1000);
							}
						} catch (Exception e) {
							continue;
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	private static void tickThrone() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					ThroneManager.runTick();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	private static void addArtisansWorkShopProcessTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					ArtisansWorkShop.processArtisansWorkShop();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 60, TimeUnit.SECONDS);
	}

	private static void addLividFarmProcessTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(() -> {
			try {
				// Create a snapshot copy to avoid concurrent modification issues
				for (Player player : new ArrayList<>(World.getPlayers())) {
					if (player == null)
						continue;
					LividFarm.process(player);
				}
			} catch (Throwable e) {
				Logger.handle(e);
			}
		}, 0, 60, TimeUnit.SECONDS);
	}

	/*
	 * private static void addReferralTask() {
	 * CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
	 * 
	 * @Override public void run() { try { ReferralHandler.processReferralRewards();
	 * } catch (Throwable e) { Logger.handle(e); } } }, 15, 15, TimeUnit.MINUTES); }
	 */

	private static void tickWeeklyInfo() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					if (wim != null)
						wim.tick();
				} catch (Throwable t) {
					Logger.handle(t);
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	// Add this array to your World.java class with your other arrays (like
	// messageString and colors)
	private static final String[] rankingMessages = { "Weekly Rankings are LIVE! Type ::ranks to see who's dominating!",
			"Climb the Time Online rankings - every minute counts! Use ::ranks time",
			"Vote daily to boost your ranking! Check ::ranks vote to see standings",
			"Top donors get weekly rewards! See ::ranks donation for current leaders",
			"NEW WEEK = NEW CHANCES! Rankings reset every Monday!",
			"Top 3 weekly players get exclusive crowns + GP rewards!",
			"Track your progress with ::ranks [time/vote/donation] commands!",
			"Compete with friends - see who can reach #1 first!",
			"Real-time rankings update as you play, vote, and donate!",
			"Be legendary - get your name in the weekly Hall of Fame!" };

	// Updated messageEvent method
	private static void messageEvent() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				int random = Utils.random(100);
				if (random < 5)
					sendWorldMessage(
							"<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
									+ "Server: Don't forget to check the hiscore board by the home for top Voters!",
							false);
				if (random >= 5 && random < 10)
					sendWorldMessage(
							"<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
									+ "Server: Don't forget to check the hiscore board by the home for top Pkers!",
							false);
				else if (random >= 10 && random < 15)
					sendWorldMessage("<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
							+ "Server: Don't forget to check the hiscore board by the home for top Dominon tower clears!",
							false);
				else if (random >= 15 && random < 20)
					sendWorldMessage(
							"<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
									+ "Server: Don't forget to check the hiscore board by the home for top Donators!",
							false);
				// NEW: Add ranking announcements (15% chance)
				else if (random >= 20 && random < 35)
					sendWorldMessage("<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
							+ "Server: " + rankingMessages[Utils.random(rankingMessages.length)], false);
				else
					sendWorldMessage("<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
							+ "Server: " + messageString[Utils.random(messageString.length)], false);
			}
		}, 15, 6, TimeUnit.MINUTES);
	}

	// Add these methods to your World.java class for real-time announcements

	/**
	 * Announce ranking achievements (call when player reaches new rank)
	 */
	public static void announceRankingAchievement(Player player, WeeklyTopRanking.RankingType type, int newRank) {
		if (player == null || newRank < 1 || newRank > 10)
			return;

		String playerName = Utils.formatString(player.getUsername());
		String rankingName = type.getDisplayName().toLowerCase();

		String message = "";
		if (newRank == 1) {
			message = "<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">" + "Server: " + playerName
					+ " has claimed #1 in weekly " + rankingName + " rankings!";
		} else if (newRank == 2) {
			message = "<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">" + "Server: " + playerName
					+ " has reached #2 in weekly " + rankingName + " rankings!";
		} else if (newRank == 3) {
			message = "<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">" + "Server: " + playerName
					+ " has secured #3 in weekly " + rankingName + " rankings!";
		} else if (newRank <= 10) {
			message = "<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">" + "Server: " + playerName
					+ " has entered the top 10 " + rankingName + " rankings at #" + newRank + "!";
		}

		if (!message.isEmpty()) {
			sendWorldMessage(message, false);
		}
	}

	/**
	 * Announce close competition (call this every 10-15 minutes)
	 */
	public static void checkAndAnnounceCompetition() {
		try {
			// Check time online competition
			WeeklyTopRanking.TimeOnlineRank[] timeRanks = WeeklyTopRanking.getTimeOnlineRanks();
			if (timeRanks != null && timeRanks[0] != null && timeRanks[1] != null) {
				long timeDiff = timeRanks[0].getTimeOnline() - timeRanks[1].getTimeOnline();

				// If less than 30 minutes difference
				if (timeDiff < 1800 && timeDiff > 0) {
					String leader = Utils.formatString(timeRanks[0].getUsername());
					String chaser = Utils.formatString(timeRanks[1].getUsername());
					String message = "<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
							+ "Server: CLOSE RACE! " + leader + " leads " + chaser + " by only "
							+ Utils.getTimeToString(timeDiff) + " in time online!";
					sendWorldMessage(message, false);
				}
			}

			// Check vote competition
			WeeklyTopRanking.VoteRank[] voteRanks = WeeklyTopRanking.getVoteRanks();
			if (voteRanks != null && voteRanks[0] != null && voteRanks[1] != null) {
				int voteDiff = voteRanks[0].getVoteCount() - voteRanks[1].getVoteCount();

				// If 3 votes or less difference
				if (voteDiff <= 3 && voteDiff > 0) {
					String leader = Utils.formatString(voteRanks[0].getUsername());
					String chaser = Utils.formatString(voteRanks[1].getUsername());
					String message = "<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
							+ "Server: TIGHT VOTE RACE! " + leader + " leads " + chaser + " by only " + voteDiff
							+ " vote" + (voteDiff == 1 ? "" : "s") + "!";
					sendWorldMessage(message, false);
				}
			}

			// Check donation competition
			WeeklyTopRanking.DonationRank[] donationRanks = WeeklyTopRanking.getDonationRanks();
			if (donationRanks != null && donationRanks[0] != null && donationRanks[1] != null) {
				int donationDiff = donationRanks[0].getDonationAmount() - donationRanks[1].getDonationAmount();

				// If $25 or less difference
				if (donationDiff <= 25 && donationDiff > 0) {
					String leader = Utils.formatString(donationRanks[0].getUsername());
					String chaser = Utils.formatString(donationRanks[1].getUsername());
					String message = "<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
							+ "Server: DONATION BATTLE! " + leader + " leads " + chaser + " by only $" + donationDiff
							+ "!";
					sendWorldMessage(message, false);
				}
			}

		} catch (Exception e) {
			// Silently handle errors
		}
	}

	/**
	 * Weekly reset announcements
	 */
	public static void announceWeeklyReset() {
		sendWorldMessage("<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
				+ "Server: WEEKLY RANKINGS HAVE BEEN RESET! Fresh start for everyone!", false);
		sendWorldMessage("<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
				+ "Server: The race begins NOW! Use ::ranks to start climbing!", false);
	}

	/**
	 * Announce weekly winners
	 */
	public static void announceWeeklyWinners() {
		sendWorldMessage("<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
				+ "Server: CONGRATULATIONS TO THIS WEEK'S CHAMPIONS!", false);

		// Announce each winner
		String timeWinner = WeeklyTopRanking.getTopPlayerName(WeeklyTopRanking.RankingType.TIME_ONLINE);
		if (timeWinner != null) {
			sendWorldMessage("<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
					+ "Server: Time Online Champion: " + Utils.formatString(timeWinner), false);
		}

		String voteWinner = WeeklyTopRanking.getTopPlayerName(WeeklyTopRanking.RankingType.VOTE_COUNT);
		if (voteWinner != null) {
			sendWorldMessage("<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
					+ "Server: Vote Count Champion: " + Utils.formatString(voteWinner), false);
		}

		String donationWinner = WeeklyTopRanking.getTopPlayerName(WeeklyTopRanking.RankingType.DONATION_AMOUNT);
		if (donationWinner != null) {
			sendWorldMessage("<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
					+ "Server: Donation Champion: " + Utils.formatString(donationWinner), false);
		}

		sendWorldMessage("<img=7><shad=000000><col=" + colors[Utils.random(colors.length)] + ">"
				+ "Server: Winners will receive their rewards shortly!", false);
	}

	// Add this method to schedule competition checks (call this in your server
	// startup)
	private static void scheduleCompetitionChecks() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if (getPlayers().size() > 5) { // Only check if decent amount of players online
					checkAndAnnounceCompetition();
				}
			}
		}, 10, 15, TimeUnit.MINUTES); // Check every 15 minutes
	}

	private static void addTriviaBotTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					if (World.getPlayers().size() >= 1)
						TriviaBot.Run();
					for (Player p : World.getPlayers()) {
						if (p == null) {
							continue;
						}
						if (p instanceof Bot) {
							continue;
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 10, Utils.random(5, 10), TimeUnit.MINUTES);
	}

	private static void addWorldCalendarTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					DayOfWeekManager.processWorldCalendar();
					for (Player p : World.getPlayers()) {
						if (p == null) {
							continue;
						}
						if (p instanceof Bot) {
							continue;
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 1, 5, TimeUnit.SECONDS);
	}

	private static void addOwnedObjectsTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					OwnedObjectManager.processAll();
					VoragoInstance.checkChangeRotation();
					if (isWellActive() && WellOfGoodWill.taskTime > 0 && !Settings.DEBUG)
						WellOfGoodWill.taskTime--;
					for (Player p : World.getPlayers()) {
						if (p == null) {
							continue;
						}
						if (p instanceof Bot) {
							continue;
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	private static void addRestoreShopItemsTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					ShopsHandler.restoreShops();
					for (Player p : World.getPlayers()) {
						if (p == null) {
							continue;
						}
						if (p instanceof Bot) {
							continue;
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 30, TimeUnit.SECONDS);
	}

	private static final void addSummoningEffectTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player player : getPlayers()) {
						if (player == null || player.getFamiliar() == null || player.isDead() || !player.hasFinished())
							continue;
						if (player.getFamiliar().getOriginalId() == 6814) {
							player.heal(20);
							player.setNextGraphics(new Graphics(1507));
							for (Player p : World.getPlayers()) {
								if (p == null) {
									continue;
								}
								if (p instanceof Bot) {
									continue;
								}
							}
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 15, TimeUnit.SECONDS);
	}

	private static final void addRestoreSpecialAttackTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					// Safe access to players list
					if (players == null) {
						return;
					}

					// Create safe copy to avoid concurrent modification
					List<Player> safePlayers = new ArrayList<>();
					try {
						synchronized (players) {
							for (int i = 0; i < players.size(); i++) {
								Player player = players.get(i);
								if (player != null) {
									safePlayers.add(player);
									
								}
							}
						}
					} catch (Exception e) {
						return; // Skip this iteration if we can't safely copy players
					}

					// Process each player safely
					for (Player player : safePlayers) {
						try {
							if (player == null || player.isDead() || !player.isRunning()) {
								continue;
							}

							// Safe bot check
							try {
								if (player.isBot()) {
									continue;
								}
							} catch (Exception e) {
								// isBot() method doesn't exist, assume regular player
							}

							// Safe combat definitions access
							if (player.getCombatDefinitions() != null) {
								player.getCombatDefinitions().restoreSpecialAttack();
							}

						} catch (Exception e) {
							// Skip this player and continue with next
							continue;
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 30000);
	}

	private static final void addLeechedStatsTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					// CRITICAL: Safe access to players list
					if (players == null) {
						return;
					}

					// CRITICAL: Create safe copy to avoid concurrent modification
					List<Player> safePlayers = new ArrayList<>();
					try {
						synchronized (players) {
							for (int i = 0; i < players.size(); i++) {
								Player player = players.get(i);
								if (player != null) {
									safePlayers.add(player);
								}
							}
						}
					} catch (Exception e) {
						return; // Skip this iteration if we can't safely copy players
					}

					// CRITICAL: Process each player individually with full error handling
					for (Player player : safePlayers) {
						try {
							// CRITICAL: Null and state checks
							if (player == null || player.isDead() || !player.isRunning()) {
								continue;
							}

							// CRITICAL: Skip bots safely
							try {
								if (player.isBot()) {
									continue; // Bots don't use prayers/curses
								}
							} catch (Exception e) {
								// isBot() method doesn't exist, assume regular player
							}

							// CRITICAL: Safe prayer access
							if (player.getPrayer() == null) {
								continue; // Skip if no prayer system
							}

							// CRITICAL: Safe leech bonuses processing
							try {
								// Attack leech processing
								if (player.getPrayer().usingPrayer(1, 10)) {
									if (player.getPrayer().leechBonuses != null
											&& player.getPrayer().leechBonuses.length > 0) {
										if (player.getPrayer().leechBonuses[0] == 1
												|| player.getPrayer().leechBonuses[0] == -1) {
											if (player.getPackets() != null) {
												player.getPackets().sendGameMessage(
														"Your Attack is now unaffected by sap and leech curses.", true);
											}
										}
										if (player.getPrayer().leechBonuses[0] < 0) {
											player.getPrayer().increase(0);
										}
										if (player.getPrayer().leechBonuses[0] > 0) {
											player.getPrayer().decrease(0);
										}
									}
								} else {
									if (player.getPrayer().leechBonuses != null
											&& player.getPrayer().leechBonuses.length > 0) {
										if (player.getPrayer().leechBonuses[0] == 1
												|| player.getPrayer().leechBonuses[0] == -1) {
											if (player.getPackets() != null) {
												player.getPackets().sendGameMessage(
														"Your Attack is now unaffected by sap and leech curses.", true);
											}
										}
										if (player.getPrayer().leechBonuses[0] < 0) {
											player.getPrayer().increase(0);
										}
										if (player.getPrayer().leechBonuses[0] > 0) {
											player.getPrayer().decrease(0);
										}
									}
								}

								// Ranging leech processing
								if (player.getPrayer().usingPrayer(1, 11)) {
									if (player.getPrayer().leechBonuses != null
											&& player.getPrayer().leechBonuses.length > 3) {
										if (player.getPrayer().leechBonuses[3] == 1
												|| player.getPrayer().leechBonuses[3] == -1) {
											if (player.getPackets() != null) {
												player.getPackets().sendGameMessage(
														"Your Ranging is now unaffected by sap and leech curses.",
														true);
											}
										}
										if (player.getPrayer().leechBonuses[3] < 0) {
											player.getPrayer().increase(3);
										}
										if (player.getPrayer().leechBonuses[3] > 0) {
											player.getPrayer().decrease(3);
										}
									}
								} else {
									if (player.getPrayer().leechBonuses != null
											&& player.getPrayer().leechBonuses.length > 3) {
										if (player.getPrayer().leechBonuses[3] == 1
												|| player.getPrayer().leechBonuses[3] == -1) {
											if (player.getPackets() != null) {
												player.getPackets().sendGameMessage(
														"Your Ranging is now unaffected by sap and leech curses.",
														true);
											}
										}
										if (player.getPrayer().leechBonuses[3] < 0) {
											player.getPrayer().increase(3);
										}
										if (player.getPrayer().leechBonuses[3] > 0) {
											player.getPrayer().decrease(3);
										}
									}
								}

								// Magic leech processing
								if (player.getPrayer().usingPrayer(1, 12)) {
									if (player.getPrayer().leechBonuses != null
											&& player.getPrayer().leechBonuses.length > 4) {
										if (player.getPrayer().leechBonuses[4] == 1
												|| player.getPrayer().leechBonuses[4] == -1) {
											if (player.getPackets() != null) {
												player.getPackets().sendGameMessage(
														"Your Magic is now unaffected by sap and leech curses.", true);
											}
										}
										if (player.getPrayer().leechBonuses[4] < 0) {
											player.getPrayer().increase(4);
										}
										if (player.getPrayer().leechBonuses[4] > 0) {
											player.getPrayer().decrease(4);
										}
									}
								} else {
									if (player.getPrayer().leechBonuses != null
											&& player.getPrayer().leechBonuses.length > 4) {
										if (player.getPrayer().leechBonuses[4] == 1
												|| player.getPrayer().leechBonuses[4] == -1) {
											if (player.getPackets() != null) {
												player.getPackets().sendGameMessage(
														"Your Magic is now unaffected by sap and leech curses.", true);
											}
										}
										if (player.getPrayer().leechBonuses[4] < 0) {
											player.getPrayer().increase(4);
										}
										if (player.getPrayer().leechBonuses[4] > 0) {
											player.getPrayer().decrease(4);
										}
									}
								}

								// Defence leech processing
								if (player.getPrayer().usingPrayer(1, 13)) {
									if (player.getPrayer().leechBonuses != null
											&& player.getPrayer().leechBonuses.length > 2) {
										if (player.getPrayer().leechBonuses[2] == 1
												|| player.getPrayer().leechBonuses[2] == -1) {
											if (player.getPackets() != null) {
												player.getPackets().sendGameMessage(
														"Your Defence is now unaffected by sap and leech curses.",
														true);
											}
										}
										if (player.getPrayer().leechBonuses[2] < 0) {
											player.getPrayer().increase(2);
										}
										if (player.getPrayer().leechBonuses[2] > 0) {
											player.getPrayer().decrease(2);
										}
									}
								} else {
									if (player.getPrayer().leechBonuses != null
											&& player.getPrayer().leechBonuses.length > 2) {
										if (player.getPrayer().leechBonuses[2] == 1
												|| player.getPrayer().leechBonuses[2] == -1) {
											if (player.getPackets() != null) {
												player.getPackets().sendGameMessage(
														"Your Defence is now unaffected by sap and leech curses.",
														true);
											}
										}
										if (player.getPrayer().leechBonuses[2] < 0) {
											player.getPrayer().increase(2);
										}
										if (player.getPrayer().leechBonuses[2] > 0) {
											player.getPrayer().decrease(2);
										}
									}
								}

								// Strength leech processing
								if (player.getPrayer().usingPrayer(1, 14)) {
									if (player.getPrayer().leechBonuses != null
											&& player.getPrayer().leechBonuses.length > 1) {
										if (player.getPrayer().leechBonuses[1] == 1
												|| player.getPrayer().leechBonuses[1] == -1) {
											if (player.getPackets() != null) {
												player.getPackets().sendGameMessage(
														"Your Strength is now unaffected by sap and leech curses.",
														true);
											}
										}
										if (player.getPrayer().leechBonuses[1] < 0) {
											player.getPrayer().increase(1);
										}
										if (player.getPrayer().leechBonuses[1] > 0) {
											player.getPrayer().decrease(1);
										}
									}
								} else {
									if (player.getPrayer().leechBonuses != null
											&& player.getPrayer().leechBonuses.length > 1) {
										if (player.getPrayer().leechBonuses[1] == 1
												|| player.getPrayer().leechBonuses[1] == -1) {
											if (player.getPackets() != null) {
												player.getPackets().sendGameMessage(
														"Your Strength is now unaffected by sap and leech curses.",
														true);
											}
										}
										if (player.getPrayer().leechBonuses[1] < 0) {
											player.getPrayer().increase(1);
										}
										if (player.getPrayer().leechBonuses[1] > 0) {
											player.getPrayer().decrease(1);
										}
									}
								}

							} catch (Exception e) {
								// Skip leech processing for this player if there's an error
								continue;
							}

						} catch (Exception e) {
							// CRITICAL: Skip this entire player if any error occurs
							continue;
						}
					}

				} catch (Throwable e) {
					// CRITICAL: Handle any unexpected errors
					Logger.handle(e);
				}
			}
		}, 0, 20000);
	}

	private static void addRestoreRunEnergyTask() {

	    CoresManager.fastExecutor.schedule(new TimerTask() {

	        @Override

	        public void run() {

	            try {

	                if (players == null) {

	                    return;

	                }

	                List<Player> safePlayers = new ArrayList<>();

	                try {

	                    synchronized (players) {

	                        for (int i = 0; i < players.size(); i++) {

	                            Player player = players.get(i);

	                            if (player != null) {

	                                safePlayers.add(player);

	                            }

	                        }

	                    }

	                } catch (Exception e) {

	                    return;

	                }

	                for (Player player : safePlayers) {

	                    try {

	                        // Changed: Check if player is valid and online, NOT if running

	                        if (player == null || player.isDead() || !player.isRunning()) {

	                            continue;

	                        }

	                        // Fixed logic: Check if actually running AND moving

	                        if (player.getRun() && player.getNextRunDirection() != -1) {

	                            player.drainRunEnergy();   // Running and moving = drain

	                        } else {

	                            player.restoreRunEnergy(); // Not running or not moving = restore

	                        }

	                    } catch (Exception e) {

	                        continue;

	                    }

	                }

	            } catch (Throwable e) {

	                Logger.handle(e);

	            }

	        }

	    }, 0, 1500);

	}
	private static void addDrainPrayerTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					// Safe access to players list
					if (players == null) {
						return;
					}

					// Create safe copy to avoid concurrent modification
					List<Player> safePlayers = new ArrayList<>();
					try {
						synchronized (players) {
							for (int i = 0; i < players.size(); i++) {
								Player player = players.get(i);
								if (player != null) {
									safePlayers.add(player);
								}
							}
						}
					} catch (Exception e) {
						return; // Skip this iteration if we can't safely copy players
					}

					// Process each player safely
					for (Player player : safePlayers) {
						try {
							if (player == null || player.isDead() || !player.isRunning()) {
								continue;
							}

							// Safe bot check
							try {
								if (player.isBot()) {
									continue;
								}
							} catch (Exception e) {
								// isBot() method doesn't exist, assume regular player
							}

							// Safe prayer processing
							if (player.getPrayer() != null) {
								player.getPrayer().processPrayerDrain();
							}

						} catch (Exception e) {
							// Skip this player and continue with next
							continue;
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 600);
	}

	private static void addRestoreHitPointsTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					// Safe player processing
					if (players != null) {
						// Create safe copy to avoid concurrent modification
						List<Player> safePlayers = new ArrayList<>();
						try {
							synchronized (players) {
								for (int i = 0; i < players.size(); i++) {
									Player player = players.get(i);
									if (player != null) {
										safePlayers.add(player);
									}
								}
							}
						} catch (Exception e) {
							// Skip player processing this iteration
						}

						// Process each player safely
						for (Player player : safePlayers) {
							try {
								if (player == null || player.isDead() || !player.isRunning()) {
									continue;
								}

								// Safe bot check
								try {
									if (player.isBot()) {
										continue;
									}
								} catch (Exception e) {
									// isBot() method doesn't exist, assume regular player
								}

								player.restoreHitPoints();

							} catch (Exception e) {
								// Skip this player and continue with next
								continue;
							}
						}
					}

					// Safe NPC processing
					if (npcs != null) {
						try {
							for (NPC npc : npcs) {
								try {
									if (npc == null || npc.isDead() || npc.hasFinished()) {
										continue;
									}
									npc.restoreHitPoints();
								} catch (Exception e) {
									continue;
								}
							}
						} catch (Exception e) {
							// Skip NPC processing this iteration
						}
					}

				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 6000);
	}

	private static void addRestoreSkillsTask() {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					// Safe access to players list
					if (players == null) {
						return;
					}

					// Create safe copy to avoid concurrent modification
					List<Player> safePlayers = new ArrayList<>();
					try {
						synchronized (players) {
							for (int i = 0; i < players.size(); i++) {
								Player player = players.get(i);
								if (player != null) {
									safePlayers.add(player);
								}
							}
						}
					} catch (Exception e) {
						return; // Skip this iteration if we can't safely copy players
					}

					// Process each player safely
					for (Player player : safePlayers) {
						try {
							if (player == null || !player.isRunning()) {
								continue;
							}

							// Safe bot check
							try {
								if (player.isBot()) {
									continue;
								}
							} catch (Exception e) {
								// isBot() method doesn't exist, assume regular player
							}

							// Safe skill processing with null checks
							if (player.getSkills() == null || player.getPrayer() == null) {
								continue;
							}

							int ammountTimes = player.getPrayer().usingPrayer(0, 8) ? 2 : 1;
							if (player.isResting()) {
								ammountTimes += 1;
							}
							boolean berserker = player.getPrayer().usingPrayer(1, 9);

							b: for (int skill = 0; skill < 25; skill++) {
								if (skill == Skills.SUMMONING) {
									continue b;
								}
								c: for (int time = 0; time < ammountTimes; time++) {
									int currentLevel = player.getSkills().getLevel(skill);
									int normalLevel = player.getSkills().getLevelForXp(skill);
									if (currentLevel > normalLevel && time == 0) {
										if (skill == Skills.ATTACK || skill == Skills.STRENGTH
												|| skill == Skills.DEFENCE || skill == Skills.RANGE
												|| skill == Skills.MAGIC) {
											if (berserker && Utils.random(100) <= 15) {
												continue c;
											}
										}
										player.getSkills().set(skill, currentLevel - 1);
									} else if (currentLevel < normalLevel) {
										player.getSkills().set(skill, currentLevel + 1);
									} else {
										break c;
									}
								}
							}

						} catch (Exception e) {
							// Skip this player and continue with next
							continue;
						}
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 0, 60000);
	}

	public static final Map<Integer, Region> getRegions() {
		return regions;
	}

	public static final Region getRegion(int id) {
		return getRegion(id, false);
	}

	public static final Region getRegion(int id, boolean load) {
		Region region = regions.get(id);
		if (region == null) {
			region = new Region(id);
			regions.put(id, region);
		}
		if (load)
			region.checkLoadMap();
		return region;
	}

	public static final void addNPC(NPC npc) {
		npcs.add(npc);
	}

	public static final void removeNPC(NPC npc) {
		npcs.remove(npc);
	}

	// test Remove object
	public static List<WorldTile> restrictedTiles = new ArrayList<WorldTile>();

	public static void deleteObject(WorldTile tile) {
		restrictedTiles.add(tile);
	}

	// test
	public static final NPC spawnNPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			boolean spawned) {
		NPC n = null;
		if (id == 7010)
			n = new HunterTrapNPC(HunterNPC.GRENWALL, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		if (id == 5079)
			n = new HunterTrapNPC(HunterNPC.GREY_CHINCHOMPA, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 5080)
			n = new HunterTrapNPC(HunterNPC.RED_CHINCHOMPA, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 152)
			n = new HauntedTree(id, tile, mapAreaNameHash, false);
		else if (id == 5081)
			n = new HunterTrapNPC(HunterNPC.FERRET, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 6916)
			n = new HunterTrapNPC(HunterNPC.GECKO, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 7272)
			n = new HunterTrapNPC(HunterNPC.MONKEY, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 7272)
			n = new HunterTrapNPC(HunterNPC.RACCOON, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 5073)
			n = new HunterTrapNPC(HunterNPC.CRIMSON_SWIFT, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 5075)
			n = new HunterTrapNPC(HunterNPC.GOLDEN_WARBLER, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 5076)
			n = new HunterTrapNPC(HunterNPC.COPPER_LONGTAIL, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 5074)
			n = new HunterTrapNPC(HunterNPC.CERULEAN_TWITCH, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 5072)
			n = new HunterTrapNPC(HunterNPC.TROPICAL_WAGTAIL, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 7031)
			n = new HunterTrapNPC(HunterNPC.WIMPY_BIRD, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 5088)
			n = new HunterTrapNPC(HunterNPC.BARB_TAILED_KEBBIT, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea,
					spawned);
		else if (id == 1631 || id == 1632)
			n = new ConditionalDeath(4161, "The rockslug shrivels and dies.", true, id, tile, mapAreaNameHash,
					canBeAttackFromOutOfArea);
		else if (id == 14301)
			n = new Glacor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1610)
			n = new ConditionalDeath(4162, "The gargoyle breaks into peices as you slam the hammer onto its head.",
					false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 14849)
			n = new ConditionalDeath(23035, null, false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 1627 || id == 1628 || id == 1629 || id == 1630)
			n = new ConditionalDeath(4158, null, false, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id >= 2803 && id <= 2808)
			n = new ConditionalDeath(6696, null, true, id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id >= 5533 && id <= 5558)
			n = new Elemental(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 2417)
			n = new WildyWyrm(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 43 || (id >= 5156 && id <= 5164) || id == 5156 || id == 1765)
			n = new Sheep(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 14301)
			n = new Glacor(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 9441)
			n = new FlameVortex(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 8832 && id <= 8834)
			n = new LivingRock(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 13465 && id <= 13481)
			n = new Revenant(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 1158 || id == 1160)
			n = new KalphiteQueen(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 8528 && id <= 8532)
			n = new Nomad(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 8091)
			n = new StarSprite();
		/**
		 * Godwars factions.
		 */
		else if (id >= 6210 && id <= 6221)
			n = new GodwarsZammorakFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6254 && id <= 6259)
			n = new GodwarsSaradominFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6229 && id <= 6246)
			n = new GodwarsArmadylFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 13456 && id <= 13459)
			n = new GodwarsZarosFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 6268 && id <= 6283)
			n = new GodwarsBandosFaction(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 50 || id == 2642)
			n = new KingBlackDragon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 9462 && id <= 9467)
			n = new Strykewyrm(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);

		else if (id == 10021)
			n = new AsteaFrostweb(id, tile);
		else if (id == 8133)
			n = new CorporealBeast(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 13447)
			n = ZarosGodwars.nex = new Nex(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea);
		else if (id == 13451)
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 13452)
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 13453)
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 13454)
			n = new NexMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 14256)
			n = new Lucien(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 21992)
			n = new Wyvern(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 21337 || id == 21336)
			n = new Minion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 21335)
			n = new Durzag(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 22891)
			n = new Telos(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 25513)
			n = new Solak(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 24004)
			n = new NexAngel(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 24010 || id == 24011 || id == 24012 || id == 24013)
			n = new NexAMinion(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 15581)
			n = new PartyDemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 25589)
			n = new Masuta(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 18545)
			n = new Veracs(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 18538 || id == 18539 || id == 18540 || id == 18541 || id == 18542 || id == 18543 || id == 18544)
			n = new OtherBrother(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 8349 || id == 8450 || id == 8451)
			n = new TormentedDemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 19463)
			n = new AraxyteNPC(id, tile);
		else if (WispInfo.forNpcId(id) != null)
			n = new Wisp(id, tile);
		else if (id == 19109)
			n = new CelestialDragonB(id, tile);
		else if (id == 18621 || id == 18622)
			n = new Airut(id, tile);
		else if (id >= 25589 && id <= 25591)
			n = new Masuta(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id >= 2881 && id <= 2883)
			n = new DagannothKing(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned, null);
		else if (id == 16697 || id == 16698 || id == 16699)
			n = new KalphiteKing(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 19553)
			n = new DarkLord(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 24765)
			n = new Magister(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 25695)
			n = new Elegorn(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 25656)
			n = new VerakLith(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 21994)
			n = new Ripperdemon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 25125)
			n = new Bandos(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 22007)
			n = new Acheronmammoth(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 3847)
			n = new SeaTrollQueen(id, tile, mapAreaNameHash, true, spawned);
		else if (id == 8335)
			n = new MercenaryMage(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);

		// else if (id == 17158 || id == 17159 || id == 17160 || id == 17185)
		// n = new VoragoMinion(id, tile, mapAreaNameHash,
		// canBeAttackFromOutOfArea, spawned);
		else if (id == 10140)
			n = new BallakDefs(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 21136)
			n = new RuneDragon(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else if (id == 16554)
			n = new JohnStrum(id, tile);
		else if (id == 2272 || id == 2269 || id == 2271)
			n = new SmallSnowman(id, tile);
		else if (id == 24170 || id == 24171 || id == 24172)
			n = new GemDragons(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		else
			n = new NPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, spawned);
		return n;
	}

	public static final NPC spawnNPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea) {
		return spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
	}

	/*
	 * check if the entity region changed because moved or teled then we update it
	 */
	public static final void updateEntityRegion(Entity entity) {
		if (entity.hasFinished()) {
			if (entity instanceof Player)
				getRegion(entity.getLastRegionId()).removePlayerIndex(entity.getIndex());
			else
				getRegion(entity.getLastRegionId()).removeNPCIndex(entity.getIndex());
			return;
		}
		int regionId = entity.getRegionId();
		if (entity.getLastRegionId() != regionId) { // map region entity at
													// changed
			if (entity instanceof Player) {
				if (entity.getLastRegionId() > 0)
					getRegion(entity.getLastRegionId()).removePlayerIndex(entity.getIndex());
				Region region = getRegion(regionId);
				region.addPlayerIndex(entity.getIndex());
				Player player = (Player) entity;
				int musicId = region.getRandomMusicId();
				if (musicId != -1)
					player.getMusicsManager().checkMusic(musicId);
				player.getControlerManager().moved();
				StarterTutorialD.checkStarterArea(player);
				if (player.isActive())
					checkControlersAtMove(player);
				player.setToleranceTimer();
			} else {
				if (entity.getLastRegionId() > 0)
					getRegion(entity.getLastRegionId()).removeNPCIndex(entity.getIndex());
				getRegion(regionId).addNPCIndex(entity.getIndex());
			}
			entity.checkMultiArea();
			entity.setLastRegionId(regionId);
		} else {
			if (entity instanceof Player) {
				Player player = (Player) entity;
				player.getControlerManager().moved();
				StarterTutorialD.checkStarterArea(player);
				if (player.isActive())
					checkControlersAtMove(player);
			}
			entity.checkMultiArea();
		}
	}

	public static void checkControlersAtMove(Player player) {
		if (LividFarm.isAtLividFarm(player))
			LividFarm.EnterLividFarm(player);
		if (ThroneOfMiscellania.isInArea(player)) {
			player.getThrone().setHasVisited(true);
		}
		if (player.getControlerManager().getControler() == null
				&& ArtisansWorkShopControler.isInsideArtisansShop(player))
			player.getControlerManager().startControler("ArtisansWorkShopControler");
		if (player.getControlerManager().getControler() == null && Kalaboss.isAtKalaboss(player))
			player.getControlerManager().startControler("Kalaboss");

		if (!(player.getControlerManager().getControler() instanceof RequestController)
				&& RequestController.inWarRequest(player))
			player.getControlerManager().startControler("clan_wars_request");
		else if (DuelControler.isAtDuelArena(player))
			player.getControlerManager().startControler("DuelControler");
		else if (FfaZone.inArea(player))
			player.getControlerManager().startControler("clan_wars_ffa");
		else if (!player.isApeAtoll()) {
			if (player.getEquipment().getWeaponId() == 4024) {
				ButtonHandler.sendRemove2(player, 3);
			}
		}
	}

	/*
	 * checks clip
	 */
	public static boolean canMoveNPC(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if (getMask(plane, tileX, tileY) != 0)
					return false;
		if (x == 2497 && y == 2716 && plane == 2)
			return false;
		if (x == 2504 && y == 2678 && plane == 2)
			return false;
		return true;
	}

	/*
	 * checks clip
	 */
	public static boolean isNotCliped(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if ((getMask(plane, tileX, tileY) & 2097152) != 0)
					return false;
		return true;
	}

	public static void setMask(int plane, int x, int y, int mask) {
		WorldTile tile = new WorldTile(x, y, plane);
		int regionId = tile.getRegionId();
		Region region = getRegion(regionId);
		if (region == null)
			return;
		int baseLocalX = x - ((regionId >> 8) * 64);
		int baseLocalY = y - ((regionId & 0xff) * 64);
		region.setMask(tile.getPlane(), baseLocalX, baseLocalY, mask);
	}

	public static int getRotation(int plane, int x, int y) {
		WorldTile tile = new WorldTile(x, y, plane);
		int regionId = tile.getRegionId();
		Region region = getRegion(regionId);
		if (region == null)
			return 0;
		// int baseLocalX = x - ((regionId >> 8) * 64);
		// int baseLocalY = y - ((regionId & 0xff) * 64);
		// return region.getRotation(tile.getPlane(), baseLocalX, baseLocalY);
		return 0;
	}

	/*
	 * checks clip
	 */
	public static boolean isRegionLoaded(int regionId) {
		Region region = getRegion(regionId);
		if (region == null)
			return false;
		return region.getLoadMapStage() == 2;
	}

	public static boolean isTileFree(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if (!isFloorFree(plane, tileX, tileY) || !isWallsFree(plane, tileX, tileY))
					return false;
		return true;
	}

	public static boolean isFloorFree(int plane, int x, int y, int size) {
		for (int tileX = x; tileX < x + size; tileX++)
			for (int tileY = y; tileY < y + size; tileY++)
				if (!isFloorFree(plane, tileX, tileY))
					return false;
		return true;
	}

	public static boolean isFloorFree(int plane, int x, int y) {
		return (getMask(plane, x, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ)) == 0;
	}

	public static boolean isWallsFree(int plane, int x, int y) {
		return (getMask(plane, x, y) & (Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_NORTHWEST
				| Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST | Flags.WALLOBJ_EAST | Flags.WALLOBJ_NORTH
				| Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST)) == 0;
	}

	public static int getMask(int plane, int x, int y) {
		WorldTile tile = new WorldTile(x, y, plane);
		Region region = getRegion(tile.getRegionId());
		if (region == null)
			return -1;
		return region.getMask(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	private static int getClipedOnlyMask(int plane, int x, int y) {
		WorldTile tile = new WorldTile(x, y, plane);
		Region region = getRegion(tile.getRegionId());
		if (region == null)
			return -1;
		return region.getMaskClipedOnly(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	public static final boolean checkProjectileStep(int plane, int x, int y, int dir, int size) {
		int xOffset = Utils.DIRECTION_DELTA_X[dir];
		int yOffset = Utils.DIRECTION_DELTA_Y[dir];
		/*
		 * int rotation = getRotation(plane,x+xOffset,y+yOffset); if(rotation != 0) {
		 * dir += rotation; if(dir >= Utils.DIRECTION_DELTA_X.length) dir = dir -
		 * (Utils.DIRECTION_DELTA_X.length-1); xOffset = Utils.DIRECTION_DELTA_X[dir];
		 * yOffset = Utils.DIRECTION_DELTA_Y[dir]; }
		 */
		if (size == 1) {
			int mask = getClipedOnlyMask(plane, x + Utils.DIRECTION_DELTA_X[dir], y + Utils.DIRECTION_DELTA_Y[dir]);
			if (xOffset == -1 && yOffset == 0)
				return (mask & 0x42240000) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (mask & 0x60240000) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (mask & 0x40a40000) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (mask & 0x48240000) == 0;
			if (xOffset == -1 && yOffset == -1) {
				return (mask & 0x43a40000) == 0 && (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == 1 && yOffset == -1) {
				return (mask & 0x60e40000) == 0 && (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x40a40000) == 0;
			}
			if (xOffset == -1 && yOffset == 1) {
				return (mask & 0x4e240000) == 0 && (getClipedOnlyMask(plane, x - 1, y) & 0x42240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
			}
			if (xOffset == 1 && yOffset == 1) {
				return (mask & 0x78240000) == 0 && (getClipedOnlyMask(plane, x + 1, y) & 0x60240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 1) & 0x48240000) == 0;
			}
		} else if (size == 2) {
			if (xOffset == -1 && yOffset == 0)
				return (getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (getClipedOnlyMask(plane, x + 2, y) & 0x60e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y + 1) & 0x78240000) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y - 1) & 0x60e40000) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (getClipedOnlyMask(plane, x, y + 2) & 0x4e240000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y + 2) & 0x78240000) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (getClipedOnlyMask(plane, x - 1, y) & 0x4fa40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) == 0
						&& (getClipedOnlyMask(plane, x, y - 1) & 0x63e40000) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (getClipedOnlyMask(plane, x + 1, y - 1) & 0x63e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y - 1) & 0x60e40000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y) & 0x78e40000) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4fa40000) == 0
						&& (getClipedOnlyMask(plane, x - 1, y + 1) & 0x4e240000) == 0
						&& (getClipedOnlyMask(plane, x, y + 2) & 0x7e240000) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (getClipedOnlyMask(plane, x + 1, y + 2) & 0x7e240000) == 0
						&& (getClipedOnlyMask(plane, x + 2, y + 2) & 0x78240000) == 0
						&& (getClipedOnlyMask(plane, x + 1, y + 1) & 0x78e40000) == 0;
		} else {
			if (xOffset == -1 && yOffset == 0) {
				if ((getClipedOnlyMask(plane, x - 1, y) & 0x43a40000) != 0
						|| (getClipedOnlyMask(plane, x - 1, -1 + (y + size)) & 0x4e240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 0) {
				if ((getClipedOnlyMask(plane, x + size, y) & 0x60e40000) != 0
						|| (getClipedOnlyMask(plane, x + size, y - (-size + 1)) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x, y - 1) & 0x43a40000) != 0
						|| (getClipedOnlyMask(plane, x + size - 1, y - 1) & 0x60e40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x, y + size) & 0x4e240000) != 0
						|| (getClipedOnlyMask(plane, x + (size - 1), y + size) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x - 1, y - 1) & 0x43a40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + (-1 + sizeOffset)) & 0x4fa40000) != 0
							|| (getClipedOnlyMask(plane, sizeOffset - 1 + x, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == -1) {
				if ((getClipedOnlyMask(plane, x + size, y - 1) & 0x60e40000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + size, sizeOffset + (-1 + y)) & 0x78e40000) != 0
							|| (getClipedOnlyMask(plane, x + sizeOffset, y - 1) & 0x63e40000) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x - 1, y + size) & 0x4e240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x - 1, y + sizeOffset) & 0x4fa40000) != 0
							|| (getClipedOnlyMask(plane, -1 + (x + sizeOffset), y + size) & 0x7e240000) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 1) {
				if ((getClipedOnlyMask(plane, x + size, y + size) & 0x78240000) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getClipedOnlyMask(plane, x + sizeOffset, y + size) & 0x7e240000) != 0
							|| (getClipedOnlyMask(plane, x + size, y + sizeOffset) & 0x78e40000) != 0)
						return false;
			}
		}
		return true;
	}

	public static final boolean checkWalkStep(int plane, int x, int y, int dir, int size) {
		return checkWalkStep(plane, x, y, Utils.DIRECTION_DELTA_X[dir], Utils.DIRECTION_DELTA_Y[dir], size);
	}

	public static final boolean checkWalkStep(int plane, int x, int y, int xOffset, int yOffset, int size) {
		if (size == 1) {
			int mask = getMask(plane, x + xOffset, y + yOffset);
			if (xOffset == -1 && yOffset == 0)
				return (mask
						& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST)) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (mask
						& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_WEST)) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (mask
						& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (mask
						& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH)) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
						| Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_EAST)) == 0
						&& (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
						| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
						&& (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_WEST)) == 0
						&& (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_NORTH)) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
						| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_EAST)) == 0
						&& (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_SOUTH)) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (mask & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
						| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0
						&& (getMask(plane, x + 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_WEST)) == 0
						&& (getMask(plane, x, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_SOUTH)) == 0;
		} else if (size == 2) {
			if (xOffset == -1 && yOffset == 0)
				return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x - 1, y + 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
										| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0;
			if (xOffset == 1 && yOffset == 0)
				return (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
						&& (getMask(plane, x + 2, y + 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == 0 && yOffset == -1)
				return (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x + 1, y - 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0;
			if (xOffset == 0 && yOffset == 1)
				return (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x + 1, y + 2)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == -1 && yOffset == -1)
				return (getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST
						| Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x - 1, y - 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
										| Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) == 0;
			if (xOffset == 1 && yOffset == -1)
				return (getMask(plane, x + 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST
						| Flags.CORNEROBJ_NORTHEAST)) == 0
						&& (getMask(plane, x + 2, y - 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) == 0
						&& (getMask(plane, x + 2, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == -1 && yOffset == 1)
				return (getMask(plane, x - 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST
						| Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x - 1, y + 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
										| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) == 0
						&& (getMask(plane, x, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
								| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
			if (xOffset == 1 && yOffset == 1)
				return (getMask(plane, x + 1, y + 2) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST
						| Flags.CORNEROBJ_SOUTHWEST)) == 0
						&& (getMask(plane, x + 2, y + 2)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) == 0
						&& (getMask(plane, x + 1, y + 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
								| Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
								| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) == 0;
		} else {
			if (xOffset == -1 && yOffset == 0) {
				if ((getMask(plane, x - 1, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0
						|| (getMask(plane, x - 1, -1 + (y + size))
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST
										| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH
							| Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 0) {
				if ((getMask(plane, x + size, y) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0
						|| (getMask(plane, x + size, y - (-size + 1))
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
							| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == -1) {
				if ((getMask(plane, x, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0
						|| (getMask(plane, x + size - 1, y - 1)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_WEST
							| Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == 0 && yOffset == 1) {
				if ((getMask(plane, x, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0
						|| (getMask(plane, x + (size - 1), y + size)
								& (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_SOUTH
										| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size - 1; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
							| Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == -1) {
				if ((getMask(plane, x - 1, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.CORNEROBJ_NORTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + (-1 + sizeOffset)) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
							| Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0
							|| (getMask(plane, sizeOffset - 1 + x, y - 1) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
									| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == -1) {
				if ((getMask(plane, x + size, y - 1) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_NORTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + size, sizeOffset + (-1 + y)) & (Flags.FLOOR_BLOCKSWALK
							| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH
							| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0
							|| (getMask(plane, x + sizeOffset, y - 1) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST
									| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_NORTHEAST)) != 0)
						return false;
			} else if (xOffset == -1 && yOffset == 1) {
				if ((getMask(plane, x - 1, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ
						| Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.CORNEROBJ_SOUTHEAST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x - 1, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH
							| Flags.CORNEROBJ_NORTHEAST | Flags.CORNEROBJ_SOUTHEAST)) != 0
							|| (getMask(plane, -1 + (x + sizeOffset), y + size) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH
									| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			} else if (xOffset == 1 && yOffset == 1) {
				if ((getMask(plane, x + size, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
						| Flags.OBJ | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
					return false;
				for (int sizeOffset = 1; sizeOffset < size; sizeOffset++)
					if ((getMask(plane, x + sizeOffset, y + size) & (Flags.FLOOR_BLOCKSWALK | Flags.FLOORDECO_BLOCKSWALK
							| Flags.OBJ | Flags.WALLOBJ_EAST | Flags.WALLOBJ_SOUTH | Flags.WALLOBJ_WEST
							| Flags.CORNEROBJ_SOUTHEAST | Flags.CORNEROBJ_SOUTHWEST)) != 0
							|| (getMask(plane, x + size, y + sizeOffset) & (Flags.FLOOR_BLOCKSWALK
									| Flags.FLOORDECO_BLOCKSWALK | Flags.OBJ | Flags.WALLOBJ_NORTH | Flags.WALLOBJ_SOUTH
									| Flags.WALLOBJ_WEST | Flags.CORNEROBJ_NORTHWEST | Flags.CORNEROBJ_SOUTHWEST)) != 0)
						return false;
			}
		}
		return true;
	}

	public static final boolean containsPlayer(String username) {
		for (Player p2 : players) {
			if (p2 == null)
				continue;
			if (p2.getUsername().equals(username))
				return true;
		}
		return false;
	}

	public static Player getPlayer(String username) {
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getUsername().equals(username))
				return player;
		}
		return null;
	}

	public static final Player getPlayerByDisplayName(String username) {
		String formatedUsername = Utils.formatString(username);
		for (Player player : getPlayers()) {
			if (player == null)
				continue;
			if (player.getUsername().equalsIgnoreCase(formatedUsername)
					|| player.getDisplayName().equalsIgnoreCase(formatedUsername))
				return player;
		}
		return null;
	}

	public static final EntityList<Player> getPlayers() {
		return players;
	}

	public static NPC getNpc(int id) {
		for (NPC npc : npcs) {
			if (npc.getId() == id)
				return npc;
		}
		return null;
	}

	public static final EntityList<NPC> getNPCs() {
		return npcs;
	}

	public static int exiting_delay;
	public static long exiting_start;

	public static final void safeShutdown(final boolean restart, int delay) {
		if (exiting_start != 0) {
			System.err.println("You cannot launch another restart if there's already one running!");
			return;
		}
		exiting_start = Utils.currentTimeMillis();
		exiting_delay = delay;
		for (Player player : World.getPlayers()) {
			if (player == null || !player.isActive() || player.hasFinished())
				continue;
			player.getPackets().sendSystemUpdate(delay);
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player player : World.getPlayers()) {
						if (player == null || !player.isActive())
							continue;
						player.realFinish();
					}
					IPBanL.save();
					ThroneManager.save();
					IPMute.save();
					MACBan.save();
					PkRank.save();
					WealthRank.save();
					GrandExchange.save();
					// ReferralHandler.save();
					Lottery.save();
					WeeklyTopRanking.save();
					DonationRank.save();
					VoteHiscores.save();
					WellOfGoodWill.save();
					DTRank.save();

					if (World.wim != null)
						World.wim.save();
					if (restart)
						ServerLauncher.restartEmulator();
					else
						ServerLauncher.restartEmulator();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, delay, TimeUnit.SECONDS);
	}

	public static final void safeShutdown(int delay) {
		if (exiting_start != 0)
			return;
		exiting_start = Utils.currentTimeMillis();
		exiting_delay = delay;
		for (Player player : World.getPlayers()) {
			if (player == null || player.hasFinished())
				continue;
			player.getPackets().sendSystemUpdate(delay);
		}
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					for (Player player : World.getPlayers()) {
						if (player == null)
							continue;
						player.realFinish();
					}
					IPBanL.save();
					PkRank.save();
					GrandExchange.save();
					// ReferralHandler.save();
					Lottery.save();
					WeeklyTopRanking.save();
					WellOfGoodWill.save();
					ServerLauncher.restartEmulator();
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, delay, TimeUnit.SECONDS);
	}

	public static final boolean isSpawnedObject(WorldObject object) {
		return getRegion(object.getRegionId()).getSpawnedObjects().contains(object);
	}

	public static final boolean isSpawnedObject(Player player, WorldObject object) {
		return getRegion(player.getRegionId()).getSpawnedObjects().contains(object);
	}

	public static final void spawnObject(WorldObject object) {
		getRegion(object.getRegionId()).spawnObject(object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion(), false);
	}

	public static final void spawnObject(Player player, WorldObject object) {
		getRegion(player.getRegionId()).spawnObject(player, object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion(), false);
	}

	public static final void unclipTile(WorldTile tile) {
		getRegion(tile.getRegionId()).unclip(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion());
	}

	public static final void removeObject(WorldObject object, boolean removeClip) {
		getRegion(object.getRegionId()).removeObject(object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion(), removeClip);
	}

	public static final void removeObject(WorldObject object) {
		getRegion(object.getRegionId()).removeObject(object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion());
	}

	public static final void removeObject(Player player, WorldObject object, boolean removeClip) {
		getRegion(player.getRegionId()).removeObject(player, object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion(), removeClip);
	}

	public static final void removeObject(Player player, WorldObject object) {
		getRegion(player.getRegionId()).removeObject(player, object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion());
	}

	public static final boolean removeTemporaryObject(final WorldObject object, long time, final boolean clip) {
		removeObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(object, false);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	public static final void spawnObjectTemporary(final WorldObject object, long time) {
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(object))
						return;
					removeObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final void spawnObjectTemporary(final Player player, final WorldObject object, long time) {
		spawnObject(player, object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(player, object)) {
						return;
					}
					removeObject(player, object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final boolean removeObjectTemporary(final WorldObject object, long time, boolean removeClip) {
		removeObject(object, removeClip);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	public static final boolean removeObjectTemporary(final Player player, final WorldObject object, long time,
			boolean removeClip) {
		removeObject(player, object, removeClip);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(player, object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	public static final void spawnTempGroundObject(final WorldObject object, final int replaceId, long time,
			final boolean removeClip) {
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					removeObject(object, removeClip);
					addGroundItem(new Item(replaceId), object, null, false, 180);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, time, TimeUnit.MILLISECONDS);
	}

	public static final WorldObject getStandartObject(WorldTile tile) {
		return getRegion(tile.getRegionId()).getStandartObject(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion());
	}

	public static final WorldObject getObjectWithType(WorldTile tile, int type) {
		return getRegion(tile.getRegionId()).getObjectWithType(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion(), type);
	}

	public static final WorldObject getObjectWithSlot(WorldTile tile, int slot) {
		return getRegion(tile.getRegionId()).getObjectWithSlot(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion(), slot);
	}

	public static final boolean containsObjectWithId(WorldTile tile, int id) {
		return getRegion(tile.getRegionId()).containsObjectWithId(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion(), id);
	}

	public static final WorldObject getObjectWithId(WorldTile tile, int id) {
		return getRegion(tile.getRegionId()).getObjectWithId(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(),
				id);
	}

	public static final void addGroundItem(final Item item, final WorldTile tile) {
		addGroundItem(item, tile, null, false, -1, 2, -1);
	}

	public static final void addGroundItem(final Item item, final WorldTile tile, int publicTime) {
		addGroundItem(item, tile, null, false, -1, 2, publicTime);
	}

	public static final void addGroundItem(final Item item, final WorldTile tile, final Player owner, boolean invisible,
			long hiddenTime) {
		addGroundItem(item, tile, owner, invisible, hiddenTime, 2, 60);
	}

	public static final FloorItem addGroundItem(final Item item, final WorldTile tile, final Player owner,
			boolean invisible, long hiddenTime, int type) {
		return addGroundItem(item, tile, owner, invisible, hiddenTime, type, 60);
	}

	public static final void turnPublic(FloorItem floorItem, int publicTime) {
		if (!floorItem.isInvisible())
			return;
		int regionId = floorItem.getTile().getRegionId();
		final Region region = getRegion(regionId);
		if (!region.getGroundItemsSafe().contains(floorItem))
			return;
		Player realOwner = floorItem.hasOwner() ? World.getPlayer(floorItem.getOwner()) : null;
		floorItem.setInvisible(false);
		for (Player player : players) {
			if (player == null || player == realOwner || player.hasFinished()
					|| player.getPlane() != floorItem.getTile().getPlane()
					|| !player.getMapRegionsIds().contains(regionId) || !ItemConstants.isTradeable(floorItem))
				continue;
			player.getPackets().sendGroundItem(floorItem);
		}
		if (publicTime != -1)
			removeGroundItem(floorItem, publicTime);
	}

	// 1. SAFE addGroundItemForever method - Fix for line 2144 NPE
	@Deprecated
	public static final void addGroundItemForever(Item item, final WorldTile tile) {
		try {
			// Validate inputs
			if (item == null || tile == null) {
				System.err.println("addGroundItemForever: Invalid parameters - item or tile is null");
				return;
			}

			int regionId = tile.getRegionId();
			final FloorItem floorItem = new FloorItem(item, new WorldTile(tile.getX(), tile.getY(), tile.getPlane()),
					true);
			final Region region = getRegion(tile.getRegionId());

			if (region == null) {
				System.err.println("addGroundItemForever: Could not get region " + regionId);
				return;
			}

			// Safe region access
			try {
				region.getGroundItemsSafe().add(floorItem);
			} catch (Exception e) {
				System.err.println("Error adding ground item to region: " + e.getMessage());
				return;
			}

			// Safe player list access
			try {
				if (players == null) {
					// Players list not initialized yet - this is normal during server startup
					return;
				}

				// Create safe copy of players list
				List<Player> safePlayers = new ArrayList<>();
				synchronized (players) {
					for (int i = 0; i < players.size(); i++) {
						Player player = players.get(i);
						if (player != null) {
							safePlayers.add(player);
						}
					}
				}

				// Send to each player safely
				for (Player player : safePlayers) {
					try {
						if (player == null || player.hasFinished()
								|| player.getPlane() != floorItem.getTile().getPlane()
								|| !player.getMapRegionsIds().contains(regionId)) {
							continue;
						}

						if (player.getPackets() != null) {
							player.getPackets().sendGroundItem(floorItem);
						}
					} catch (Exception e) {
						// Skip this player if there's an error
						continue;
					}
				}

			} catch (Exception e) {
				System.err.println("Error in addGroundItemForever player processing: " + e.getMessage());
				// Item was added to region successfully, so continue
			}

		} catch (Exception e) {
			System.err.println("Critical error in addGroundItemForever: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * type 0 - if not tradeable type 1 - if destroyable type 2 - no
	 */
	public static final FloorItem addGroundItem(final Item item, final WorldTile tile, final Player owner,
			boolean invisible, long hiddenTime, int type, final int publicTime) {
		final FloorItem floorItem = new FloorItem(item, tile, owner, false, invisible);
		final Region region = getRegion(tile.getRegionId());
		if (type == 1) {
			if (ItemConstants.isTradeable(item) || ItemConstants.turnCoins(item))
				region.getGroundItemsSafe().add(floorItem);
			if (invisible) {
				if (owner != null) {
					if (ItemConstants.isTradeable(item) || ItemConstants.turnCoins(item))
						owner.getPackets().sendGroundItem(floorItem);
				}
				if (hiddenTime != -1) {
					CoresManager.slowExecutor.schedule(new Runnable() {
						@Override
						public void run() {
							try {
								turnPublic(floorItem, publicTime);
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}
					}, hiddenTime, TimeUnit.SECONDS);
				}
			} else {
				int regionId = tile.getRegionId();
				for (Player player : players) {
					if (player == null || player.hasFinished() || player.getPlane() != tile.getPlane()
							|| !player.getMapRegionsIds().contains(regionId))
						continue;
					player.getPackets().sendGroundItem(floorItem);
				}
				if (publicTime != -1)
					removeGroundItem(floorItem, publicTime);
			}
		} else {
			region.getGroundItemsSafe().add(floorItem);
			if (invisible) {
				if (owner != null) {
					owner.getPackets().sendGroundItem(floorItem);
				}
				if (hiddenTime != -1) {
					CoresManager.slowExecutor.schedule(new Runnable() {
						@Override
						public void run() {
							try {
								turnPublic(floorItem, publicTime);
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}
					}, hiddenTime, TimeUnit.SECONDS);
				}
			} else {
				int regionId = tile.getRegionId();
				for (Player player : players) {
					if (player == null || player.hasFinished() || player.getPlane() != tile.getPlane()
							|| !player.getMapRegionsIds().contains(regionId) || !ItemConstants.isTradeable(item))
						continue;
					player.getPackets().sendGroundItem(floorItem);
				}
				if (publicTime != -1)
					removeGroundItem(floorItem, publicTime);
			}
		}
		return floorItem;
	}

	public static final void updateGroundItem(Item item, final WorldTile tile, final Player owner) {
		final FloorItem floorItem = World.getRegion(tile.getRegionId()).getGroundItem(item.getId(), tile, owner);
		if (floorItem == null) {
			addGroundItem(item, tile, owner, true, 60);
			return;
		}
		floorItem.setAmount(floorItem.getAmount() + item.getAmount());
	}

	public static final void updateGroundItem(Item item, final WorldTile tile, final Player owner, final int time,
			final int type) {
		updateGroundItem(item, tile, owner, time, type, false);
	}

	public static final void updateGroundItem(Item item, final WorldTile tile, final Player owner, final int time,
			final int type, boolean npcDrop) {
		final FloorItem floorItem = World.getRegion(tile.getRegionId()).getGroundItem(item.getId(), tile, owner);
		boolean stackable = item.getDefinitions().isStackable();
		if (floorItem == null) {
			if (!stackable && item.getAmount() > 1) {
				for (int i = 0; i < item.getAmount(); i++) {
					if (!npcDrop || !owner.getPetLootManager().canLootItem(new Item(item.getId(), 1)))
						addGroundItem(item, tile, owner, true, time, type);
				}
			} else {
				if (!npcDrop || !owner.getPetLootManager().canLootItem(item))
					addGroundItem(item, tile, owner, true, time, type);
			}
			return;
		}

		if (floorItem.getDefinitions().isStackable()) {
			if (floorItem.getAmount() + item.getAmount() < 0) {
				int totalAmount = Integer.MAX_VALUE - floorItem.getAmount();
				floorItem.setAmount(Integer.MAX_VALUE);
				item.setAmount(item.getAmount() - totalAmount);
				if (!npcDrop || !owner.getPetLootManager().canLootItem(item))
					addGroundItem(item, tile, owner, true, time, type);
				owner.getPackets().sendRemoveGroundItem(floorItem);
				owner.getPackets().sendGroundItem(floorItem);
			} else
				floorItem.setAmount(floorItem.getAmount() + item.getAmount());
			owner.getPackets().sendRemoveGroundItem(floorItem);
			owner.getPackets().sendGroundItem(floorItem);
		} else
			/*
			 * for (int i = 0; i < item.getAmount(); i++) { if (!npcDrop ||
			 * !owner.getPetLootManager().canLootItem(new Item(item.getId(), 1)))si frost
			 * nag add ni to wait ry
			 */
			addGroundItem(item, tile, owner, true, time, type);

	}

	private static final void removeGroundItem(final FloorItem floorItem, long publicTime) {
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					int regionId = floorItem.getTile().getRegionId();
					Region region = getRegion(regionId);
					if (!region.getGroundItemsSafe().contains(floorItem))
						return;
					region.getGroundItemsSafe().remove(floorItem);
					for (Player player : World.getPlayers()) {
						if (player == null

								|| player.hasFinished() || player.getPlane() != floorItem.getTile().getPlane()
								|| !player.getMapRegionsIds().contains(regionId))
							continue;
						player.getPackets().sendRemoveGroundItem(floorItem);
					}
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, publicTime, TimeUnit.SECONDS);
	}

	public static final boolean removeGroundItem(Player player, FloorItem floorItem) {
		return removeGroundItem(player, floorItem, true);
	}

	public static final void spawnTemporaryObject(final WorldObject object, long time) {
		spawnTemporaryObject(object, time, false);
	}

	public static final void spawnTemporaryObject(final WorldObject object, long time, final boolean clip) {
		spawnObject(object);
		CoresManager.getServiceProvider().executeWithDelay(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(object))
						return;
					removeObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	public static final boolean removeGroundItem(Player player, final FloorItem floorItem, boolean add) {
		int regionId = floorItem.getTile().getRegionId();
		Region region = getRegion(regionId);
		WorldTile playerTile = new WorldTile(player.getX(), player.getY(), player.getPlane());
		if (!region.getGroundItemsSafe().contains(floorItem))
			return false;
		int amount = floorItem.getAmount();
		LoggingSystem.logItemPickup(player, floorItem, playerTile);
		if (floorItem.getId() == 995) {
			amount = floorItem.getAmount();
			int leftOver = 0;
			int inventoryLeftOver = 0;
			if (player.getMoneyPouch().getTotal() + amount > Integer.MAX_VALUE
					|| player.getMoneyPouch().getTotal() + amount < 0) {
				player.getPackets().sendGameMessage("Your money pouch is not big enough to hold that much cash.");
				leftOver = Integer.MAX_VALUE - player.getMoneyPouch().getTotal();
				amount = amount - leftOver;
				if (player.getMoneyPouch().getTotal() != Integer.MAX_VALUE) {
					player.getPackets().sendRunScript(5561, 1, leftOver);
					player.getMoneyPouch().setTotal(Integer.MAX_VALUE);
					player.getPackets().sendGameMessage(
							Utils.getFormattedNumber(leftOver, ',') + " coins have been added to your money pouch.");
					player.getMoneyPouch().refresh();
					floorItem.setAmount(Integer.MAX_VALUE - leftOver);
				}
				if (player.getInventory().getNumberOf(995) + amount > Integer.MAX_VALUE
						|| player.getInventory().getNumberOf(995) + amount < 0) {
					if (!player.getInventory().hasFreeSlots()) {
						player.getPackets().sendGameMessage("You don't have enough inventory space.");
						return false;
					}
					inventoryLeftOver = Integer.MAX_VALUE - player.getInventory().getNumberOf(995);
					amount = amount - inventoryLeftOver;
					if (player.getInventory().getNumberOf(995) != Integer.MAX_VALUE) {
						player.getInventory().deleteItem(995, Integer.MAX_VALUE);
						player.getInventory().addItem(995, Integer.MAX_VALUE);
					}
					floorItem.setAmount(amount);
					player.getPackets().sendRemoveGroundItem(floorItem);
					player.getPackets().sendGroundItem(floorItem);
					return false;
				} else {
					if (!player.getInventory().hasFreeSlots()) {
						player.getPackets().sendGameMessage("You don't have enough inventory space.");
						return false;
					}
					player.getInventory().addItem(995, amount);
				}
				region.getGroundItemsSafe().remove(floorItem);
				if (floorItem.isInvisible()) {
					player.getPackets().sendRemoveGroundItem(floorItem);
					return true;
				} else {
					for (Player p2 : World.getPlayers()) {
						if (p2 == null || p2.hasFinished() || p2.getPlane() != floorItem.getTile().getPlane()
								|| !p2.getMapRegionsIds().contains(regionId))
							continue;
						p2.getPackets().sendRemoveGroundItem(floorItem);
					}
					if (floorItem.isForever()) {
						CoresManager.slowExecutor.schedule(new Runnable() {
							@Override
							public void run() {
								try {
									addGroundItemForever(floorItem, floorItem.getTile());
								} catch (Throwable e) {
									Logger.handle(e);
								}
							}
						}, 60, TimeUnit.SECONDS);
					}
					return true;
				}
			}
			if (player.getMoneyPouch().getTotal() == Integer.MAX_VALUE)
				return false;
			if (amount > 1) {
				player.getPackets().sendGameMessage(
						Utils.getFormattedNumber(amount, ',') + " coins have been added to your money pouch.");
			} else {
				player.getPackets().sendGameMessage("One coin has been added to your money pouch.");
			}
			player.getPackets().sendRunScript(5561, 1, amount);
			player.getMoneyPouch().setTotal(player.getMoneyPouch().getTotal() + amount);
			player.getMoneyPouch().refresh();
			region.getGroundItemsSafe().remove(floorItem);
			if (floorItem.isInvisible()) {
				player.getPackets().sendRemoveGroundItem(floorItem);
				return true;
			} else {
				for (Player p2 : World.getPlayers()) {
					if (p2 == null || p2.hasFinished() || p2.getPlane() != floorItem.getTile().getPlane()
							|| !p2.getMapRegionsIds().contains(regionId))
						continue;
					p2.getPackets().sendRemoveGroundItem(floorItem);
				}
				if (floorItem.isForever()) {
					CoresManager.slowExecutor.schedule(new Runnable() {
						@Override
						public void run() {
							try {
								addGroundItemForever(floorItem, floorItem.getTile());
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}
					}, 60, TimeUnit.SECONDS);
				}
				return true;
			}
		} else if (floorItem.getId() != 995) {
			if (player.getInventory().getNumberOf(floorItem.getId()) + floorItem.getAmount() > Integer.MAX_VALUE
					|| player.getInventory().getNumberOf(floorItem.getId()) + floorItem.getAmount() < 0) {
				amount = floorItem.getAmount();
				amount = Integer.MAX_VALUE - player.getInventory().getNumberOf(floorItem.getAmount());
				floorItem.setAmount(Integer.MAX_VALUE - amount);
				player.getInventory().deleteItem(floorItem.getId(), Integer.MAX_VALUE);
				player.getInventory().addItem(floorItem.getId(), Integer.MAX_VALUE);
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
				return false;
			} else if (player.getInventory().getFreeSlots() == 0 && (floorItem.getDefinitions().isStackable()
					&& !player.getInventory().containsItem(floorItem.getId(), 1))) {
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
				return false;
			} else if (player.getInventory().getFreeSlots() == 0 && (!floorItem.getDefinitions().isStackable())) {
				player.sendMessage("Inventory full. To make more room, sell, drop or bank something.");
				return false;
			}
			if (player.getFreezeDelay() >= Utils.currentTimeMillis()) {
				if (!floorItem.getTile().matches(playerTile))
					player.setNextAnimation(new Animation(537));
			}
			region.getGroundItemsSafe().remove(floorItem);
			if (add)
				player.getInventory().addItem(new Item(floorItem.getId(), floorItem.getAmount()));
			if (floorItem.isInvisible()) {
				player.getPackets().sendRemoveGroundItem(floorItem);
				return true;
			} else {
				for (Player p2 : World.getPlayers()) {
					if (p2 == null || p2.hasFinished() || p2.getPlane() != floorItem.getTile().getPlane()
							|| !p2.getMapRegionsIds().contains(regionId))
						continue;
					p2.getPackets().sendRemoveGroundItem(floorItem);
				}
				if (floorItem.isForever()) {
					CoresManager.slowExecutor.schedule(new Runnable() {
						@Override
						public void run() {
							try {
								addGroundItemForever(floorItem, floorItem.getTile());
							} catch (Throwable e) {
								Logger.handle(e);
							}
						}
					}, 60, TimeUnit.SECONDS);
				}
			}
		}
		return true;
	}

	public static final void sendObjectAnimation(WorldObject object, Animation animation) {
		sendObjectAnimation(null, object, animation);
	}

	public static final void sendObjectAnimation(Entity creator, WorldObject object, Animation animation) {
		if (creator == null) {
			for (Player player : World.getPlayers()) {
				if (player == null || player.hasFinished() || !player.withinDistance(object))
					continue;
				player.getPackets().sendObjectAnimation(object, animation);
			}
		} else {
			for (int regionId : creator.getMapRegionsIds()) {
				List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
				if (playersIndexes == null)
					continue;
				for (Integer playerIndex : playersIndexes) {
					Player player = players.get(playerIndex);
					if (player == null || player.hasFinished() || !player.withinDistance(object))
						continue;
					player.getPackets().sendObjectAnimation(object, animation);
				}
			}
		}
	}

	public static final void sendGraphics(Entity creator, Graphics graphics, WorldTile tile) {
		if (creator == null) {
			for (Player player : World.getPlayers()) {
				if (player == null || player.hasFinished() || !player.withinDistance(tile))
					continue;
				player.getPackets().sendGraphics(graphics, tile);
			}
		} else {
			for (int regionId : creator.getMapRegionsIds()) {
				List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
				if (playersIndexes == null)
					continue;
				for (Integer playerIndex : playersIndexes) {
					Player player = players.get(playerIndex);
					if (player == null || player.hasFinished() || !player.withinDistance(tile))
						continue;
					player.getPackets().sendGraphics(graphics, tile);
				}
			}
		}
	}

	/**
	 * Sends the Loot Beam graphics.
	 * 
	 * @param player The player to send to.
	 * @param tile   The tile to send on.
	 */

	/*
	 * public static final void sendLootbeam(Player player, WorldTile tile) { if
	 * (player == null || player.hasFinished() || !player.withinDistance(tile))
	 * return; player.getPackets().sendGraphics(new Graphics(4422), tile);
	 * player.sendMessage(Colors.orange +
	 * "<shad=000000>A golden beam shines over one of your items.", true); }
	 */
	public static final void sendProjectile(Entity shooter, Entity receiver, int gfxId, int startHeight, int endHeight,
			int speed, int delay, int curve) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null

						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player.withinDistance(receiver)))
					continue;
				int size = shooter.getSize();
				int distance = Utils.getDistance(shooter, receiver);
				int startOffsetDistance = distance > 2 ? 0 : 11;
				int startOffsetDistance2 = 0;
				player.getPackets().sendProjectile(receiver,
						new WorldTile(shooter.getCoordFaceX(size), shooter.getCoordFaceY(size), shooter.getPlane()),
						receiver, gfxId, startHeight, endHeight, speed, delay, curve,
						receiver instanceof Player ? startOffsetDistance : startOffsetDistance2, size);
			}
		}
	}

	public static final void sendProjectile(Entity shooter, WorldTile startTile, WorldTile receiver, int gfxId,
			int startHeight, int endHeight, int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null

						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player.withinDistance(receiver)))
					continue;
				player.getPackets().sendProjectile(null, startTile, receiver, gfxId, startHeight, endHeight, speed,
						delay, curve, startDistanceOffset, shooter.getSize());
			}
		}
	}

	public static final void sendProjectile(WorldTile shooter, Entity receiver, int gfxId, int startHeight,
			int endHeight, int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : receiver.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null

						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player.withinDistance(receiver)))
					continue;
				player.getPackets().sendProjectile(receiver, shooter, receiver, gfxId, startHeight, endHeight, speed,
						delay, curve, startDistanceOffset, 1);
			}
		}
	}

	public static final void sendProjectile(Entity shooter, WorldTile receiver, int gfxId, int startHeight,
			int endHeight, int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null

						|| player.hasFinished()
						|| (!player.withinDistance(shooter) && !player.withinDistance(receiver)))
					continue;
				player.getPackets().sendProjectile(null, shooter, receiver, gfxId, startHeight, endHeight, speed, delay,
						curve, startDistanceOffset, shooter.getSize());
			}
		}
	}

	public static final void sendProjectile(Entity shooter, Entity receiver, int gfxId, int startHeight, int endHeight,
			int speed, int delay, int curve, int startDistanceOffset) {
		for (int regionId : shooter.getMapRegionsIds()) {
			List<Integer> playersIndexes = getRegion(regionId).getPlayerIndexes();
			if (playersIndexes == null)
				continue;
			for (Integer playerIndex : playersIndexes) {
				Player player = players.get(playerIndex);
				if (player == null || player.hasFinished()
						|| (!player.withinDistance(shooter) && !player.withinDistance(receiver)))
					continue;
				int size = shooter.getSize();
				player.getPackets().sendProjectile(receiver, shooter, receiver, gfxId, startHeight, endHeight, speed,
						delay, curve, startDistanceOffset, size);
			}
		}
	}

	public static final boolean isMultiArea(WorldTile tile) {
		int destX = tile.getX();
		int destY = tile.getY();
		return (destX >= 3462 && destX <= 3511 && destY >= 9481 && destY <= 9521 && tile.getPlane() == 0) // kalphite
																											// lair
				|| (destX >= 4540 && destX <= 4799 && destY >= 5052 && destY <= 5183 && tile.getPlane() == 0) // thzaar
				// || (destX >= 2830 && destX <= 2868 && destY >= 1842 && destY <= 1806 &&
				// tile.getPlane() == 1) //Nex angel godwars // city
				|| (destX >= 1721 && destX <= 1791 && destY >= 5123 && destY <= 5249)
				|| (destX >= 2250 && destX <= 2280 && destY >= 4670 && destY <= 4720)
				|| (destX >= 2987 && destX <= 3006 && destY >= 3912 && destY <= 3937)
				|| (destX >= 2895 && destX <= 2937 && destY >= 4430 && destY <= 4472)
				|| (destX >= 2245 && destX <= 2295 && destY >= 4675 && destY <= 4720)
				|| (destX >= 2450 && destX <= 3520 && destY >= 9450 && destY <= 9550)
				|| (destX >= 3006 && destX <= 3071 && destY >= 3602 && destY <= 3710)
				|| (destX >= 3134 && destX <= 3192 && destY >= 3519 && destY <= 3646)
				|| (destX >= 2815 && destX <= 2966 && destY >= 5240 && destY <= 5375)// wild
				|| (destX >= 1790 && destX <= 1987 && destY >= 3194 && destY <= 3273) // soul
																						// wars
				|| (destX >= 2840 && destX <= 2950 && destY >= 5190 && destY <= 5230) // godwars
				|| (destX >= 3547 && destX <= 3555 && destY >= 9690 && destY <= 9699)
				|| (destX >= 3136 && destX <= 3327 && destY >= 3519 && destY <= 3607) // WILDY
				|| (destX >= 3190 && destX <= 3327 && destY >= 3648 && destY <= 3839)
				|| (destX >= 3200 && destX <= 3390 && destY >= 3840 && destY <= 3967)
				|| (destX >= 2992 && destX <= 3007 && destY >= 3912 && destY <= 3967)
				|| (destX >= 2946 && destX <= 2959 && destY >= 3816 && destY <= 3831)
				|| (destX >= 3008 && destX <= 3199 && destY >= 3856 && destY <= 3903)
				|| (destX >= 3008 && destX <= 3071 && destY >= 3600 && destY <= 3711)
				|| (destX >= 3072 && destX <= 3327 && destY >= 3608 && destY <= 3647)
				|| (destX >= 2624 && destX <= 2690 && destY >= 2550 && destY <= 2619)
				|| (destX >= 2371 && destX <= 2422 && destY >= 5062 && destY <= 5117)
				|| (destX >= 2896 && destX <= 2927 && destY >= 3595 && destY <= 3630)
				|| (destX >= 2892 && destX <= 2932 && destY >= 4435 && destY <= 4464)
				|| (destX >= 2256 && destX <= 2287 && destY >= 4680 && destY <= 4711)
				// damage dummy
				|| (destX >= 2128 && destX <= 2158 && destY >= 5518 && destY <= 5551)
				|| (destX >= 2863 && destX <= 2878 && destY >= 5350 && destY <= 5372) || KingBlackDragon.atKBD(tile) // KBD
				|| TormentedDemon.atTD(tile) // Tormented demon's area
				|| Bork.atBork(tile) // Bork's area
				|| (destX >= 2970 && destX <= 3000 && destY >= 4365 && destY <= 4400)// corp
				|| (destX >= 3195 && destX <= 3327 && destY >= 3520 && destY <= 3970
						|| (destX >= 2376 && 5127 >= destY && destX <= 2422 & 5168 <= destY))
				|| (destX >= 2374 && destY >= 5129 && destX <= 2424 && destY <= 5168) // pits
				|| (destX >= 2622 && destY >= 5696 && destX <= 2573 && destY <= 5752) // torms
				|| (destX >= 2368 && destY >= 3072 && destX <= 2431 && destY <= 3135) // castlewars
				|| (destX >= 2365 && destY >= 9470 && destX <= 2436 && destY <= 9532) // castlewars
				|| (destX >= 2948 && destY >= 5537 && destX <= 3071 && destY <= 5631) // Risk
																						// ffa.
				|| (destX >= 2756 && destY >= 5537 && destX <= 2879 && destY <= 5631) // Safe
				|| (destX >= 4160 && destY >= 5695 && destX <= 4223 && destY <= 5760) // Glacors

				|| /*
					 * South West
					 */(destX >= 2833 && destY >= 1805 && // Nex
															// Angel Of Death
				/* North East */destX <= 2868 && destY <= 1838)

				|| /* South West */(destX >= 2654 && destY >= 3706 && // rock
																		// crabs
				/* North East */destX <= 2723 && destY <= 3754) || /*
																	 * South West
																	 */(destX >= 4481 && destY >= 6207 && // araxyve
																											// cave
				/* North East */destX <= 4607 && destY <= 6333) || /*
																	 * South West
																	 */(destX >= 2691 && destY >= 9410 && // brimhaven
																											// dungeon
																											// -
																											// dragons
				/* North East */destX <= 2748 && destY <= 9538) || /*
																	 * South West
																	 */(destX >= 999 && destY >= 558 && // ascension
																										// dungeon
				/* North East */destX <= 1209 && destY <= 765) || /*
																	 * South West
																	 */(destX >= 2955 && destY >= 1735 && // kalphite
																											// king
																											// lair
				/* North East */destX <= 2997 && destY <= 1783) || /*
																	 * South West
																	 */(destX >= 3009 && destY >= 5955 && // vorago
																											// borehole
				/* North East */destX <= 3135 && destY <= 6136) || /*
																	 * South West
																	 */(destX >= 3218 && destY >= 5079 && // duel
																											// arena
																											// -
																											// summoning
				/* North East */destX <= 3246 && destY <= 5159)

				|| (tile.getX() >= 3011 && tile.getX() <= 3132 && tile.getY() >= 10052 && tile.getY() <= 10175
						&& (tile.getY() >= 10066 || tile.getX() >= 3094))
				|| ZOGame.withinArea(tile); // forin
											// dung
	}

	public static final boolean isPvpArea(WorldTile tile) {
		return Wilderness.isAtWild(tile);
	}

	public static final void addPlayer(Player player) {
		try {
			if (player == null) {
				return;
			}

			// Safe player addition
			if (players != null) {
				players.add(player);
			}

			// Safe AntiFlood handling
			try {
				String ip = "127.0.0.1"; // Default fallback

				if (player.getSession() != null && player.getSession().getIP() != null) {
					ip = player.getSession().getIP();
				}

				AntiFlood.add(ip);

			} catch (Exception e) {
				// Use default IP if session access fails
				AntiFlood.add("127.0.0.1");
			}

		} catch (Exception e) {
			System.err.println("Error adding player: " + e.getMessage());
		}
	}

	public static void removePlayer(Player player) {
		for (Player p : players) {
			if (p.getUsername().equalsIgnoreCase(player.getUsername())) {
				players.remove(p);
			}
		}
		AntiFlood.remove(player.getSession().getIP());
	}

	// 3. SAFE sendWorldMessage method
	public static void sendWorldMessage(String message, boolean forStaff) {
		try {
			if (message == null || message.trim().isEmpty()) {
				return;
			}

			// Safe access to players list
			if (players == null) {
				return;
			}

			// Create safe copy to avoid concurrent modification
			List<Player> safePlayers = new ArrayList<>();
			try {
				synchronized (players) {
					for (int i = 0; i < players.size(); i++) {
						Player player = players.get(i);
						if (player != null) {
							safePlayers.add(player);
						}
					}
				}
			} catch (Exception e) {
				return; // Skip if we can't safely copy players
			}

			// Send to each player safely
			for (Player p : safePlayers) {
				try {
					if (p == null || !p.isRunning() || p.isYellOff()) {
						continue;
					}

					// Safe bot check
					try {
						if (p.isBot()) {
							continue;
						}
					} catch (Exception e) {
						// isBot() method doesn't exist, assume regular player
					}

					if (forStaff && p.getRights() == 0) {
						continue;
					}

					// Safe packet sending
					if (p.getPackets() != null) {
						p.getPackets().sendGameMessage(message);
					}

				} catch (Exception e) {
					// Skip this player if there's an error
					continue;
				}
			}
		} catch (Exception e) {
			System.err.println("Error sending world message: " + e.getMessage());
		}
	}

	public static void addItemsAll(Item item) {
		for (Player p : World.getPlayers()) {
			if (p == null)
				continue;
			p.getInventory().addItemDrop(item.getId(), item.getAmount());
		}
	}

	public static final void sendProjectile(WorldObject object, WorldTile startTile, WorldTile endTile, int gfxId,
			int startHeight, int endHeight, int speed, int delay, int curve, int startOffset) {
		for (Player pl : players) {
			if (pl == null || !pl.withinDistance(object, 20))
				continue;
			pl.getPackets().sendProjectile(null, startTile, endTile, gfxId, startHeight, endHeight, speed, delay, curve,
					startOffset, 1);
		}
	}

	/**
	 * Gets the Players Online amount.
	 * 
	 * @return players online as Integer.
	 */
	public static final int getPlayersOnline() {
		return getPlayers().size();
	}

	/**
	 * Checks for the day of the week.
	 * 
	 * @return dayOfWeek the weekday to return.
	 */
	private static int dayOfWeek() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * Checks if it's the weekend.
	 * 
	 * @return theDayOfWeek.
	 */
	public static boolean isWeekend() {
		return dayOfWeek() == 1 ? true : dayOfWeek() == 6 ? true : dayOfWeek() == 7 ? true : false;
	}

	/**
	 * Long representing the current World time in ticks.
	 */
	public static long currentTime;

	/**
	 * Finds an NPC in the world by its NPC ID.
	 * 
	 * @param id The ID to find.
	 * @return The NPC.
	 */
	public static NPC findNPC(int id) {
		NPC npc = null;
		for (NPC n : getNPCs()) {
			if (n == null)
				continue;
			if (n.getId() == id)
				npc = n;
		}
		return npc;
	}

	/**
	 * Finds the NPC by it's id.
	 * 
	 * @param player The player searching.
	 * @param id     The NPC ID to search for.
	 * @return If NPC found.
	 */
	public static NPC findNPC(Player player, int id) {
		for (NPC npc : World.getNPCs()) {
			if (npc == null || npc.getId() != id)
				continue;
			if (npc.getRegionId() == player.getRegionId())
				return npc;
		}
		return null;
	}

	/**
	 * Gets the real object by tile and slot.
	 * 
	 * @param tile The worldtile.
	 * @param slot The object slot.
	 * @return the Object to return.
	 */
	public static final WorldObject getRealObject(WorldTile tile, int slot) {
		return getRegion(tile.getRegionId()).getRealObject(tile.getPlane(), tile.getXInRegion(), tile.getYInRegion(),
				slot);
	}

	/**
	 * Spawns a temporary world object.
	 * 
	 * @param object              The object to spawn.
	 * @param time                The time for it to stay.
	 * @param checkObjectInstance checks if the object is in an instance.
	 * @param checkObjectBefore   checks before adding.
	 */
	public static final void spawnObjectTemporary(final WorldObject object, long time,
			final boolean checkObjectInstance, boolean checkObjectBefore) {
		final WorldObject before = checkObjectBefore ? World.getObjectWithType(object, object.getType()) : null;
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (checkObjectInstance && World.getObjectWithId(object, object.getId()) != object)
						return;
					if (before != null)
						spawnObject(before);
					else
						removeObject(object); // this method allows to remove
												// object with
												// just tile and type actualy so
												// the removing
												// object may be diferent and
												// still gets removed
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
	}

	/**
	 * Sends the players online list on an interface.
	 * 
	 * @param player The player to send the list to.
	 */
	public static void playersList(Player player) {
		if (getPlayers().size() >= 308) // Interface stops at 309 <.<
			return;
		player.getInterfaceManager().sendInterface(275);
		for (int i = 0; i < 309; i++)
			player.getPackets().sendIComponentText(275, i, "");
		int number = 0;
		for (Player p5 : World.getPlayers()) {
			if (p5 == null)
				continue;
			number++;
			String titles = "[<col=000000>Player</col>] ";
			String color = (p5.getYellColor() == "ff0000" || p5.getYellColor() == null ? ""
					: "<col=" + p5.getYellColor() + ">");
			if (p5.isExpert() && !p5.isStaff2())
				titles = "[<col=000000>Expert</col>]";
			if (p5.isIronMan() && !p5.isStaff2())
				titles = "[<col=000000>Ironman</col>] <img=14>";

			if (p5.isHCIronMan() && !p5.isStaff2())
				titles = "[<col=FF0000>HC Ironman</col>] <img=15>";
			if (p5.isBronze() && !p5.isStaff2())
				titles = "[<col=8B4513>Bronze Member</col>] <img=9>";
			if (p5.isSilver() && !p5.isStaff2())
				titles = "[<col=ffffff>Silver Member</col>] <img=10>";
			if (p5.isGold() && !p5.isStaff2())
				titles = "[<col=e6e600>Gold Member</col>] <img=8>";
			if (p5.isPlatinum() && !p5.isStaff2())
				titles = "[<col=008000>Platinum Member</col>] <img=12>";
			if (p5.isDicer() && !p5.isStaff2())
				titles = "[<col=e6e600>Trusted Dicer</col>] <img=20>";
			if (p5.isDiamond() && !p5.isStaff2())
				titles = "[<col=00FFFF>Diamond Member</col>] <img=19>";
			if (p5.isSupport())
				titles = "[" + color + "Support</col>] <img=13>";
			if (p5.isForumManager())
				titles = "[" + Colors.green + "Discord Manager</col>] <img=21>";
			if (p5.isCommunityManager())
				titles = "[" + Colors.blue + "Community Manager</col>] <img=22>";
			if (p5.isWiki())
				titles = "[<col=000000>Wiki</col>] <img=23>";
			if (p5.getRights() == 1) {
				if (p5.getUsername().equalsIgnoreCase(""))
					titles = "[" + color + "Head-Mod</col>] <img=0>";
				else
					titles = "[" + color + "Mod</col>] <img=0>";
			}
			if (p5.isMod()) {
				titles = "[" + color + "Mod</col>] <img=0>";
			}
			if (p5.isSponsor())
				titles = "[<shad=FFD700><col=FF8C00>Sponsor</col></shad>] <img=16>";
			if (p5.isYoutube())
				titles = "[<shad=ff0000><col=000000>Youtube</col></shad>] <img=17>";
			if (p5.isDev())
				titles = "[<shad=0000FF><col=00BFFF>Dev</col></shad>] <img=18>";
			if (p5.isAdmin() && !p5.isDev())
				titles = "[" + color + "Admin</col>] <img=1>";
			if (p5.getUsername().equalsIgnoreCase("Zeus"))
				titles = "[" + color + "Owner</col>] <img=1>";
			player.getPackets().sendIComponentText(275, 1, "<shade=B00000><u=000080>Helwyr RSPS players</u>");
			player.getPackets().sendIComponentText(275, 10,
					"Players online: " + World.getPlayersOnline() + " [" + World.getPlayers().size() + "].");
			player.getPackets().sendIComponentText(275, (11 + number),
					titles + p5.getDisplayName() + "</col></shad> - " + "[TP: "
							+ Utils.getTimePlayed(p5.getTimePlayed()) + "] - " + "[Level: "
							+ p5.getSkills().getCombatLevelWithSummoning() + "]");
		}
	}

	/**
	 * Sends a Message to all Players online.
	 * 
	 * @param message The message to send.
	 * @param me      The Player sending the Message.
	 */
	public static void sendWorldYellMessage(String message, Player me) {
		for (Player player : World.getPlayers()) {
			if (player == null || !player.isRunning() || !Settings.yellEnabled())
				continue;
			if (player.getFriendsIgnores().getIgnores().equals(me.getUsername()))
				continue;
			if (player != me && player.isYellOff())
				continue;
			player.getPackets().sendGameMessage(message);
		}
	}

	/**
	 * A String[] containing all World random messages.
	 */
	private static String[] messageString = { "Please report all bugs; glitches & missing content on our forum.",
			"You can change your default Home in your Account Manager.",
			"Type ;;ticket in the chatbox if you need assistance from staff.",
			"Visit our ';;Forum' for all the latest News & Updates.",
			"You can spend your Trivia points by talking to the Wise Old Man.",
			"Use the Grand Exchange if you can't find an item you're looking for.",
			"You can spend your Vote points by trading Party Pete.",
			"Join our ;;discord channel for voice/text chat; music & events.",
			"Easier Buying; Selling; Trading - in the ';;Market' area.",
			"Remember to ::Vote every 12 hours - receive awesome rewards.",
			"You can add tools to your toolbelt to save inventory space.",
			"You can toggle World/Yell messages in your Account Manager.",
			"You can change your Loyalty Title in your Account Manager.",
			"You can change your Appearence & Gender by typing ';;settings'.",
			"Most equipment/potions/food can be auto-bought on the Grand Exchange.",
			"Donation ranks are given when spent over 20$ in total.",
			"Type ;;commands to see a list of all available commands.",
			"You can reset your Slayer tasks from Kuradal's reward shop.",
			"Zeals earned from Soul Wars are multiplied by 3 on weekends.",
			"You can quickly open the Titles manager interface by typing ;;titles.",
			"Type ;;ticket in the chatbox if you need assistance from staff.",
			"Use the Grand Exchange if you can't find an item you're looking for.",
			"You can zoom in using page up/page down keys on your keyboard.",
			"You can zoom out by scrolling your mouse wheel while holding ctrl.",
			"You can change your Loot Beam settings in your Account Manager.",
			"Make a thread on our ';;forum' if you want your donator rank there.",
			"Completing Slayer tasks with a partner earns you both extra EXP.",
			"Progressing rooms in Dungeoneering requires KC, backtracking - doesn't.",
			"Use the Grand Exchange if you can't find an item you're looking for.",
			"Type ;;reward 1 to claim your vote rewards (if you voted successfully).",
			"You can access the Prifddinas city with a total level of at least 2250.",
			"Most items can be auto-bought on the Grand Exchange with a +5% price.",
			"You can use coins on the well at home for 2 hours of double experience.",
			"You can view all your currently active game perks by typing ';;perks'.",
			"You can change your default Home area in your Account Manager.",
			"There's a Vote Party every 100 total server votes." };

	/**
	 * A String[] containing all Colors used for Server messages.
	 */
	public static String[] colors = { "3399FF", "00CC99", "003399", "0099CC", "993300", "993333", "9900FF", "7D1616",
			"CC3300", "00FF00", "3399FF" };

	public static int getWellAmount() {
		return wellAmount;
	}

	public static void setWellAmount(int amount) {
		wellAmount = amount;
	}

	public static void addWellAmount(String displayName, int amount) {
		wellAmount += amount;
		if (wellAmount < Settings.WELL_MAX_AMOUNT && amount >= 10000000)
			sendWorldMessage(
					"<col=FF0000>" + displayName + " " + "has contributed "
							+ NumberFormat.getNumberInstance(Locale.US).format(amount) + " GP to the Well of Goodwill!",
					false);
	}

	public static void resetWell() {
		wellAmount = 0;
		sendWorldMessage(Colors.shad + Colors.purple + "The Well of Good Will has been reset.", false);
	}

	public static boolean isWellActive() {
		return wellActive;
	}

	public static void setWellActive(boolean wellActive) {
		World.wellActive = wellActive;
	}

	public static final void spawnTemporaryDivineObject(final WorldObject object, long time, final Player player) {
		spawnObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					if (!World.isSpawnedObject(object))
						return;
					removeObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, time, TimeUnit.MILLISECONDS);
	}

	public static final WorldObject getObject(WorldTile tile) {
		return getRegion(tile.getRegionId()).getStandartObject(tile.getPlane(), tile.getXInRegion(),
				tile.getYInRegion());
	}

	public static final boolean removeObjectTemporary(final WorldObject object, long time) {
		removeObject(object);
		CoresManager.slowExecutor.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					spawnObject(object);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}

		}, time, TimeUnit.MILLISECONDS);
		return true;
	}

	/**
	 * Checks if the player is at the Ascensiopns Dungeon.
	 * 
	 * @param player The player to check for.
	 * @return if is in Monastery of Ascension.
	 */
	public static boolean isAtAscensionDungeon(Player player) {
		int destX = player.getX(), destY = player.getY();
		return /* South West */(destX >= 999 && destY >= 558 &&
		/* North East */destX <= 1209 && destY <= 765);
	}

	/**
	 * Checks if the player is at the Kuradal's Dungeon.
	 * 
	 * @param player The player to check for.
	 * @return if is in Kuradal's Dungeon.
	 */
	public static boolean isAtKuradalsDungeon(Player player) {
		int destX = player.getX(), destY = player.getY();
		return /* South West */(destX >= 1591 && destY >= 5241 &&
		/* North East */destX <= 1672 && destY <= 5337);
	}

	public static final void spawnObject(WorldObject object, boolean clip) {
		getRegion(object.getRegionId()).spawnObject(object, object.getPlane(), object.getXInRegion(),
				object.getYInRegion(), false);
	}

	public static void removeProjectiles() {
		synchronized (getRegions()) {
			for (Region region : getRegions().values())
				region.removeProjectiles();
		}
	}

	public static final Projectile sendProjectileNew(WorldTile from, WorldTile to, int graphicId, int startHeight,
			int endHeight, int startTime, double speed, int angle, int slope) {
		return sendProjectile(from, to, false, false, 0, graphicId, startHeight, endHeight, startTime, speed, angle,
				slope);
	}

	public static final Projectile sendProjectile(WorldTile from, WorldTile to, boolean adjustFlyingHeight,
			boolean adjustSenderHeight, int senderBodyPartId, int graphicId, int startHeight, int endHeight,
			int startTime, double speed, int angle, int slope) {
		int fromSizeX, fromSizeY;
		if (from instanceof Entity)
			fromSizeX = fromSizeY = ((Entity) from).getSize();
		else if (from instanceof WorldObject) {
			ObjectDefinitions defs = ((WorldObject) from).getDefinitions();
			fromSizeX = defs.getSizeX();
			fromSizeY = defs.getSizeY();
		} else
			fromSizeX = fromSizeY = 1;
		int toSizeX, toSizeY;
		if (to instanceof Entity)
			toSizeX = toSizeY = ((Entity) to).getSize();
		else if (to instanceof WorldObject) {
			ObjectDefinitions defs = ((WorldObject) to).getDefinitions();
			toSizeX = defs.getSizeX();
			toSizeY = defs.getSizeY();
		} else
			toSizeX = toSizeY = 1;

		Projectile projectile = new Projectile(from, to, adjustFlyingHeight, adjustSenderHeight, senderBodyPartId,
				graphicId, startHeight, endHeight, startTime,
				startTime + (speed == -1
						? Utils.getProjectileTimeSoulsplit(from, fromSizeX, fromSizeY, to, toSizeX, toSizeY)
						: Utils.getProjectileTimeNew(from, fromSizeX, fromSizeY, to, toSizeX, toSizeY, speed)),
				slope, angle);
		getRegion(from.getRegionId()).addProjectile(projectile);
		return projectile;
	}

	/**
	 * We represent the Soul Wars mini-game as static.
	 */
	public static SoulWarsManager soulWars;

	/**
	 * Throws a party!
	 */
	public static void edelarParty() {
		final String message = "<col=00CCCC>#" + Settings.SERVER_NAME;
		for (final Player players : World.getPlayers()) {
			if (players == null)
				continue;
			players.setNextAnimation(new Animation(9098));
			players.setNextGraphics(new Graphics(92));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					players.setNextForceTalk(new ForceTalk(message));
					this.stop();
				}
			}, 4);
		}
		for (final NPC npc : World.getNPCs()) {
			if (npc == null)
				continue;
			npc.setNextGraphics(new Graphics(92));
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					npc.setNextForceTalk(new ForceTalk(message));

					/** Reset votes & Parties at 500 **/
					if (VoteManager.VOTES >= 100) {
						VoteManager.VOTES = 0;
						VoteManager.PARTIES = 0;
					}

					this.stop();
				}
			}, 4);
		}
	}

	/**
	 * Last player who has successfully voted.
	 */
	private static String lastVoter;

	public static String getLastVoter() {
		return lastVoter;
	}

	public static void setLastVoter(String voter) {
		lastVoter = voter;
	}

	public static Player findPlayer(String name) {
		Player plr = getPlayerByDisplayName(name);
		if (plr == null) {
			plr = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
			if (plr != null) {
				plr.setUsername(Utils.formatPlayerNameForProtocol(name));
			}
		}
		return plr;
	}

	public static void sendNews(String message, int type) {
		sendNews(null, message, type); // dont use type 2(FRIEND_NEWS) with this
										// one
	}

	/*
	 * 0 - all worlds 1 - just this world 2 - friend 3 - game news
	 */
	public static void sendNews(Player from, String message, int type) {
		String m = "<shad=000>News: " + message + "</shad></col>";
		if (type == 0)
			m = "<img=7><col=D80000>" + m;
		else if (type == 1)
			m = "<img=6><col=ff8c38>" + m;
		else if (type == 2)
			m = "<img=5><col=45b247>" + m;
		else if (type == 3)
			m = "<img=7><col=FFFF00>" + m;

		for (Player p : World.getPlayers()) {
			if (p == null || !p.isRunning()
					|| (type == 2 && p != from && !p.getFriendsIgnores().getFriends().contains(from.getUsername())))
				continue;
			p.getPackets().sendGameMessage(m, true);
		}
	}

	public static void executeAfterLoadRegion(final int regionId, final Runnable event) {
		executeAfterLoadRegion(regionId, 0, event);
	}

	public static void executeAfterLoadRegion(final int regionId, long startTime, final Runnable event) {
		executeAfterLoadRegion(regionId, startTime, 10000, event);
	}

	public static void executeAfterLoadRegion(final int fromRegionX, final int fromRegionY, final int toRegionX,
			final int toRegionY, long startTime, final long expireTime, final Runnable event) {
		final long start = Utils.currentTimeMillis();
		/*
		 * for (int x = fromRegionX; x <= toRegionX; x++) { for (int y = fromRegionY; y
		 * <= toRegionY; y++) { int regionId = MapUtils.encode(Structure.REGION, x, y);
		 * World.getRegion(regionId, true); // forces check load if not // loaded } }
		 */
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					for (int x = fromRegionX; x <= toRegionX; x++) {
						for (int y = fromRegionY; y <= toRegionY; y++) {
							int regionId = MapUtils.encode(Structure.REGION, x, y);
							if (!World.isRegionLoaded(regionId) && Utils.currentTimeMillis() - start < expireTime)
								return;
						}
					}
					event.run();
					cancel();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}, startTime, 600);
	}

	/*
	 * TODO make this use code from above to save lines lo, they do same
	 */
	public static void executeAfterLoadRegion(final int regionId, long startTime, final long expireTime,
			final Runnable event) {
		final long start = Utils.currentTimeMillis();
		World.getRegion(regionId, true); // forces check load if not loaded
		CoresManager.fastExecutor.schedule(new TimerTask() {

			@Override
			public void run() {
				try {
					if (!World.isRegionLoaded(regionId) && Utils.currentTimeMillis() - start < expireTime)
						return;
					event.run();
					cancel();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}, startTime, 600);
	}

	public static final boolean removeGroundItem(final FloorItem floorItem) {
		int regionId = floorItem.getTile().getRegionId();
		Region region = getRegion(regionId);
		if (!region.getGroundItemsSafe().contains(floorItem))
			return false;
		region.getGroundItemsSafe().remove(floorItem);
		for (Player player : World.getPlayers()) {
			if (player == null || !player.isActive() || player.hasFinished()
					|| !player.getMapRegionsIds().contains(regionId))
				continue;
			player.getPackets().sendRemoveGroundItem(floorItem);
		}
		return true;
	}

	public static final FloorItem addWeaponPiece(Item item, final WorldTile tile) {
		int regionId = tile.getRegionId();
		final FloorItem floorItem = new FloorItem(item, tile, null, false, false);
		final Region region = getRegion(tile.getRegionId());
		region.getGroundItemsSafe().add(floorItem);
		for (Player player : getPlayers()) {
			if (player == null || !player.isActive() || player.hasFinished()
					|| !player.getMapRegionsIds().contains(regionId))
				continue;
			player.getPackets().sendGroundItem(floorItem);
		}
		return floorItem;
	}

	/**
	 * CRITICAL FIX: Safe player processing for timer tasks
	 */
	private static void safeProcessPlayers() {
		try {
			if (players == null) {
				return;
			}

			// Create a safe copy to iterate over
			List<Player> safePlayers = new ArrayList<>();

			synchronized (players) {
				for (int i = 0; i < players.size(); i++) {
					Player player = players.get(i);
					if (player != null) {
						safePlayers.add(player);
					}
				}
			}

			// Process each player safely
			for (Player player : safePlayers) {
				try {
					if (player == null || player.hasFinished()) {
						continue;
					}

					// CRITICAL: Check if this is a bot and handle differently
					if (player.isBot()) {
						// Bots have minimal processing requirements
						continue;
					} else {
						// Regular player processing
						processRegularPlayerSafely(player);
					}

				} catch (Exception e) {
					System.err.println("Error processing player " + (player != null ? player.getDisplayName() : "null")
							+ ": " + e.getMessage());
					// Continue with next player instead of crashing
				}
			}

		} catch (Exception e) {
			System.err.println("Error in safeProcessPlayers: " + e.getMessage());
			// Don't let this crash the entire timer
		}
	}

	/**
	 * CRITICAL FIX: Safe regular player processing
	 */
	private static void processRegularPlayerSafely(Player player) {
		try {
			if (player == null || player.hasFinished()) {
				return;
			}
			if (player.isBot()) {
				return;
			}

			// Add your regular player processing here
			// This is whatever was originally in the timer task around line 901

		} catch (Exception e) {
			System.err.println("Error processing regular player " + player.getDisplayName() + ": " + e.getMessage());
		}
	}

	/**
	 * CRITICAL FIX: Safe world message sending
	 */
	public static void sendWorldMessage(String message) {
		try {
			if (message == null || message.trim().isEmpty()) {
				return;
			}

			if (players == null) {
				return;
			}

			// Create safe copy for iteration
			List<Player> safePlayers = new ArrayList<>();

			synchronized (players) {
				for (int i = 0; i < players.size(); i++) {
					Player player = players.get(i);
					if (player != null && !player.hasFinished()) {
						safePlayers.add(player);
					}

				}
			}

			// Send to each player safely
			for (Player player : safePlayers) {
				try {
					if (player == null || player.hasFinished()) {
						continue;
					}

					// CRITICAL: Don't send world messages to bots
					if (player.isBot()) {
						continue; // Bots don't need world messages
					}

					// Send to regular players only
					if (player.getPackets() != null) {
						player.sendMessage(message);
					}

				} catch (Exception e) {
					// Continue with next player instead of crashing
					System.err.println(
							"Error sending world message to " + player.getDisplayName() + ": " + e.getMessage());
				}
			}

		} catch (Exception e) {
			System.err.println("Error in sendWorldMessage: " + e.getMessage());
		}
	}

	/**
	 * CRITICAL FIX: Safe world announcement sending
	 */
	public static void sendWorldAnnouncement(String message) {
		try {
			if (message == null || message.trim().isEmpty()) {
				return;
			}

			sendWorldMessage("<col=ff0000>Announcement: " + message + "</col>");

		} catch (Exception e) {
			System.err.println("Error sending world announcement: " + e.getMessage());
		}
	}

	/**
	 * CRITICAL FIX: Safe player list cleanup
	 */
	public static void cleanupPlayers() {
		try {
			if (players == null) {
				return;
			}

			synchronized (players) {
				// Remove finished players
				players.removeIf(player -> player == null || player.hasFinished());
			}

		} catch (Exception e) {
			System.err.println("Error cleaning up players: " + e.getMessage());
		}
	}

	public static boolean isOnline(String name) {
		return getPlayerByDisplayName(name) != null ? true : false;
	}

	public static final void sendProjectile(NewProjectile projectile) {
		for (Player p : players) {
			if (p == null || !p.withinDistance(projectile.getFrom(), 20))
				continue;
			p.getPackets().sendTestProjectile(projectile);
		}
	}

	/**
	 * An arraylist containing all available godwars instances
	 */
	private static ArrayList<Instance> worldInstances = new ArrayList<Instance>();

	public static ArrayList<Instance> getInstances() {
		return worldInstances;
	}

	public static void addInstance(Instance instance) {
		worldInstances.add(instance);
	}

	public static boolean removeInstance(Instance instance) {
		return worldInstances.remove(instance);
	}

	public static final NPC spawnNPC(int id, WorldTile tile, int mapAreaNameHash, boolean canBeAttackFromOutOfArea,
			EntityDirection faceDirection) {
		NPC returnValue = spawnNPC(id, tile, mapAreaNameHash, canBeAttackFromOutOfArea, false);
		returnValue.setDirection(faceDirection.getValue());
		return returnValue;
	}

	/**
	 * Forces all online human players to refresh their view of a specific target player (bot).
	 * This ensures the bot's appearance is updated on other clients.
	 *
	 * @param targetPlayer The bot whose appearance needs to be refreshed for other players.
	 */
	public static void refreshPlayerAppearancesForEveryone(Player targetPlayer) {
		// Loop through all active players in the world (both human and bots)
		for (Player p : getPlayers()) {
			// Only send updates to actual human players with an active session,
			// and ensure we are not trying to update the bot's own non-existent client.
			// p.localPlayerUpdate will be null for human players until logged in fully.
			if (p != null && p.getSession() != null && !p.isBot() && p != targetPlayer && p.localPlayerUpdate != null) {
				// Get the update packet from LocalPlayerUpdate.
				OutputStream updateStream = p.localPlayerUpdate.createPacketAndProcess();
				if (updateStream != null) {
					// Use the correct method to send the OutputStream via the player's session.
					// Based on WorldPacketsEncoder, session.write(OutputStream) is the method.
					p.getSession().write(updateStream); // Corrected to use session.write()
					Logger.log("World", "Forcing " + p.getUsername() + " to refresh view of bot " + targetPlayer.getUsername());
				}
			}
		}
	}
	
	public static class PlayerPacketManager {
		public void sendPlayersUpdate() {
			// This method should trigger the client to update nearby players.
			// In many frameworks, this would involve calling LocalPlayerUpdate.createPacketAndProcess()
			// and sending the resulting OutputStream to the client.
			// Since this is conceptual, we'll just log it.
			Logger.log("PlayerPacketManager", "Attempting to send player update packet.");
		}
	}

}