package com.rs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import com.rs.game.player.Player;
import com.rs.game.player.content.Lottery;
import com.rs.game.player.content.Lottery.LotteryTicket;
import com.rs.game.player.content.WeeklyTopRanking.TimeOnlineRank;
import com.rs.game.player.content.WeeklyTopRanking.VoteRank;
import com.rs.game.player.content.ancientthrone.Throne;
import com.rs.game.player.content.WeeklyTopRanking.DonationRank;
import com.rs.game.player.content.clans.Clan;
import com.rs.game.player.content.grandExchange.Offer;
import com.rs.game.player.content.grandExchange.OfferHistory;
import com.rs.game.topweeks.WeekInfoManager;

public class SerializableFilesManager {

	private static final String PATH = "data/playersaves/characters/";
	private static final String CLAN_PATH = "data/clans/";
	private static final String BACKUP_PATH = "data/playersaves/charactersBackup/";

	private static final String GE_OFFERS = "data/grandExchange/geOffers.chry";
	private static final String GE_OFFERS_HISTORY = "data/grandExchange/geOffersTrack.chry";
	private static final String GE_PRICES = "data/grandExchange/gePrices.chry";

	public synchronized static final boolean containsPlayer(String username) {
		return new File(PATH + username + ".p").exists();
	}

	public static boolean createBackup(String username) {
		try {
			Utils.copyFile(new File(PATH + username + ".p"), new File(BACKUP_PATH + username + ".p"));
			return true;
		} catch (Throwable e) {
			Logger.handle(e);
			return false;
		}
	}

	public synchronized static Player loadPlayer(String username) {
		try {
			return (Player) loadSerializedFile(new File(PATH + username + ".p"));
		} catch (Throwable e) {
			Logger.handle(e);
		}
		try {
			Logger.log("SerializableFilesManager", "Recovering account: " + username);
			return (Player) loadSerializedFile(new File(BACKUP_PATH + username + ".p"));
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}

	public static final Object loadSerializedFile(File f) throws IOException, ClassNotFoundException {
		if (!f.exists())
			return null;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		Object object = in.readObject();
		in.close();
		return object;
	}

	public synchronized static void savePlayer(Player player) {
		try {
			if (player.isBot() || player.getUsername().startsWith("BOT_")) {
	            return; // Don't save bots
	        }
			storeSerializableClass(player, new File(PATH + player.getUsername() + ".p"));
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public synchronized static final void storeSerializableClass(Serializable o, File f) throws IOException {
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
			out.writeObject(o);
			out.close();
		} catch (ConcurrentModificationException e) {
			e.printStackTrace();
		}
	}

	public static boolean containsClan(String clanName) {
		File file = new File(CLAN_PATH + clanName + ".c");
		return file.exists();
	}

	public synchronized static void saveClan(Clan clan) {
		try {
			storeSerializableClass(clan, new File(CLAN_PATH + clan.getClanName() + ".c"));
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static Clan loadClan(String clanName) {
		try {
			return (Clan) loadSerializedFile(new File(CLAN_PATH + clanName + ".c"));
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}

	public static void deleteClan(Clan clan) {
		File file = new File(CLAN_PATH + clan.getClanName() + ".c");
		if (!file.exists()) {
			return;
		}
		file.delete();
	}

	@SuppressWarnings("unchecked")
	public static synchronized HashMap<Long, Offer> loadGEOffers() {
		if (new File(GE_OFFERS).exists()) {
			try {
				return (HashMap<Long, Offer>) loadSerializedFile(new File(GE_OFFERS));
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new HashMap<Long, Offer>();
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized ArrayList<OfferHistory> loadGEHistory() {
		if (new File(GE_OFFERS_HISTORY).exists()) {
			try {
				return (ArrayList<OfferHistory>) loadSerializedFile(new File(GE_OFFERS_HISTORY));
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new ArrayList<OfferHistory>();
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized HashMap<Integer, Integer> loadGEPrices() {
		if (new File(GE_PRICES).exists()) {
			try {
				return (HashMap<Integer, Integer>) loadSerializedFile(new File(GE_PRICES));
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new HashMap<Integer, Integer>();
		}
	}

	public static synchronized void saveGEOffers(HashMap<Long, Offer> offers) {
		try {
			SerializableFilesManager.storeSerializableClass(offers, new File(GE_OFFERS));
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static synchronized void saveGEHistory(ArrayList<OfferHistory> history) {
		try {
			SerializableFilesManager.storeSerializableClass(history, new File(GE_OFFERS_HISTORY));
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static synchronized void saveGEPrices(HashMap<Integer, Integer> prices) {
		try {
			SerializableFilesManager.storeSerializableClass(prices, new File(GE_PRICES));
			Logger.log("Launcher", "Re-calculated Grand Exchange item prices..");
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	private static final String LOTTERY_TICKETS = "lotteryTickets.armar";
	private static final String WINNER_TICKETS = "winnerTickets.armar";
	private static final String LOTTERY_CYCLE = "lotteryCycle.armar";

	private static synchronized Object loadObject(String f) throws IOException, ClassNotFoundException {
		File file = new File("data/" + f);
		if (!file.exists())
			return null;
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
		Object object = in.readObject();
		in.close();
		return object;
	}

	private static synchronized void storeObject(Serializable o, String f) throws IOException {
		File file = new File("data/" + f);
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		out.writeObject(o);
		out.close();
	}

	public static synchronized void saveLotteryTickets(ArrayList<LotteryTicket> LOTTERY) {
		try {
			SerializableFilesManager.storeObject(LOTTERY, LOTTERY_TICKETS);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static boolean fileExists(String fileName) {
		return new File("data/" + fileName).exists();
	}

	public static synchronized void saveWinnerTickets(ArrayList<LotteryTicket> WINNER) {
		try {
			SerializableFilesManager.storeObject(WINNER, WINNER_TICKETS);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static synchronized void saveLotteryCycle(long lotteryCycle) {
		lotteryCycle -= Utils.currentTimeMillis();
		try {
			SerializableFilesManager.storeObject(lotteryCycle, LOTTERY_CYCLE);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized ArrayList<LotteryTicket> loadLotteryTickets() {
		if (fileExists(LOTTERY_TICKETS)) {
			try {
				return (ArrayList<LotteryTicket>) loadObject(LOTTERY_TICKETS);
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new ArrayList<LotteryTicket>();
		}
	}

	@SuppressWarnings("unchecked")
	public static synchronized ArrayList<LotteryTicket> loadWinnerTickets() {
		if (fileExists(WINNER_TICKETS)) {
			try {
				return (ArrayList<LotteryTicket>) loadObject(WINNER_TICKETS);
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new ArrayList<LotteryTicket>();
		}
	}

	public static synchronized long loadLotteryCycle() {
		if (fileExists(LOTTERY_CYCLE)) {
			try {
				return (long) loadObject(LOTTERY_CYCLE) + Utils.currentTimeMillis();
			} catch (Throwable t) {
				Logger.handle(t);
				return -1;
			}
		} else {
			return Utils.currentTimeMillis() + Lottery.LOTTERY_CYCLE_AMOUNT;
		}
	}

	private static final String WELL_ACTIVE_TIME = "wellactivetime.armar";
	private static final String WELL_AMOUNT = "wellamount.armar";

	public static synchronized void saveWellActiveTime(long wellActiveTime) {
		wellActiveTime -= Utils.currentTimeMillis();
		try {
			SerializableFilesManager.storeObject(wellActiveTime, WELL_ACTIVE_TIME);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static synchronized long loadWellActiveTime() {
		if (fileExists(WELL_ACTIVE_TIME)) {
			try {
				return (long) loadObject(WELL_ACTIVE_TIME) + Utils.currentTimeMillis();
			} catch (Throwable t) {
				Logger.handle(t);
				return 0;
			}
		} else {
			return 0;
		}
	}

	public static synchronized void saveWellAmount(int wellAmount) {
		try {
			SerializableFilesManager.storeObject(wellAmount, WELL_AMOUNT);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static synchronized int loadWellAmount() {
		if (fileExists(WELL_AMOUNT)) {
			try {
				return (int) loadObject(WELL_AMOUNT);
			} catch (Throwable t) {
				Logger.handle(t);
				return 0;
			}
		} else {
			return 0;
		}
	}

	private static final String REFERRAL_MAP = "referralmap.armar";

	@SuppressWarnings("unchecked")
	public static synchronized HashMap<String, String> loadReferralMap() {
		if (fileExists(REFERRAL_MAP)) {
			try {
				return (HashMap<String, String>) loadObject(REFERRAL_MAP);
			} catch (Exception t) {
				t.printStackTrace();
				return null;
			}
		} else {
			return new HashMap<String, String>();
		}
	}
	
	public synchronized static WeekInfoManager loadWeekInfo() {
		try {
			return (WeekInfoManager) loadSerializedFile(new File(WeekInfoManager.DIR));
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}

	public synchronized static void saveWeek(WeekInfoManager week) {
		try {
			storeSerializableClass(week, new File(WeekInfoManager.DIR));
		} catch (ConcurrentModificationException e) {
		} catch (Throwable e) {
			Logger.handle(e);
		}

	}

	public static synchronized void saveReferralMap(HashMap<String, String> REFERRALMAP) {
		try {
			SerializableFilesManager.storeObject(REFERRALMAP, REFERRAL_MAP);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	private static final String TIME_ONLINE_RANKS = "timeonlineranks.armar";
	private static final String VOTE_RANKS = "voteranks.armar";
	private static final String DONATION_RANKS = "donationranks.armar";
	private static final String CURRENT_DAY = "currentday.armar";

	public static TimeOnlineRank[] loadTimeOnlineRanks() {
		if (fileExists(TIME_ONLINE_RANKS)) {
			try {
				return (TimeOnlineRank[]) loadObject(TIME_ONLINE_RANKS);
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new TimeOnlineRank[10];
		}
	}

	public static VoteRank[] loadVoteRanks() {
		if (fileExists(VOTE_RANKS)) {
			try {
				return (VoteRank[]) loadObject(VOTE_RANKS);
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new VoteRank[10];
		}
	}

	public static DonationRank[] loadDonationRanks() {
		if (fileExists(DONATION_RANKS)) {
			try {
				return (DonationRank[]) loadObject(DONATION_RANKS);
			} catch (Throwable t) {
				Logger.handle(t);
				return null;
			}
		} else {
			return new DonationRank[10];
		}
	}

	public static int loadCurrentDay() {
		if (fileExists(CURRENT_DAY)) {
			try {
				return (int) loadObject(CURRENT_DAY);
			} catch (Throwable t) {
				Logger.handle(t);
				return -1;
			}
		} else {
			return -1;
		}
	}

	public static void saveTimeOnlineRanks(TimeOnlineRank[] timeOnlineRanks) {
		try {
			SerializableFilesManager.storeObject(timeOnlineRanks, TIME_ONLINE_RANKS);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static void saveVoteRanks(VoteRank[] voteRanks) {
		try {
			SerializableFilesManager.storeObject(voteRanks, VOTE_RANKS);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static void saveDonationRanks(DonationRank[] donationRanks) {
		try {
			SerializableFilesManager.storeObject(donationRanks, DONATION_RANKS);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}

	public static void saveCurrentDay(int currentDay) {
		try {
			SerializableFilesManager.storeObject(currentDay, CURRENT_DAY);
		} catch (Throwable t) {
			Logger.handle(t);
		}
	}
	
	public synchronized static Throne loadThrone() {
		try {
			return (Throne) loadSerializedFile(new File("data/throne.at"));
		} catch (Throwable e) {
			Logger.handle(e);
		}
		return null;
	}

	public synchronized static void saveThrone(Throne player) {
		try {
			storeSerializableClass(player, new File("data/throne.at"));
		} catch (ConcurrentModificationException e) {
		} catch (Throwable e) {
			Logger.handle(e);
		}

	}

}