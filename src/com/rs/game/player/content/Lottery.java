package com.rs.game.player.content;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class Lottery {

	public static long LOTTERY_CYCLE_AMOUNT = 3600000; // 1hour
	public static int TICKET_PRICE = 1000000, MAX_TICKETS_PER_PLAYER = 20;
	public static ArrayList<LotteryTicket> LOTTERY_TICKETS;
	public static ArrayList<LotteryTicket> WINNER_TICKETS;
	private static long lotteryCycle;
	private static boolean anouncement1, anouncement2;

	public static void init() {
		LOTTERY_TICKETS = SerializableFilesManager.loadLotteryTickets();
		WINNER_TICKETS = SerializableFilesManager.loadWinnerTickets();
		lotteryCycle = SerializableFilesManager.loadLotteryCycle();
	}

	public static void addLotteryProcessTask() {
		CoresManager.slowExecutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				if (lotteryCycle - Utils.currentTimeMillis() <= 1800000 && !anouncement1) {
					anouncement1 = true;
					int price = LOTTERY_TICKETS.size() * TICKET_PRICE;
					World.sendWorldMessage(
							"<img=2><col=228B22>[Lottery]The jackpot will be given out in 30 Minutes! <col=ff0000>Jackpot Lottery : "
									+ Utils.getFormattedNumber1(price) + " </col>.",
							false);
				} else if (lotteryCycle - Utils.currentTimeMillis() <= 300000 && !anouncement2) {
					anouncement2 = true;
					int price = LOTTERY_TICKETS.size() * TICKET_PRICE;
					World.sendWorldMessage(
							"<img=2><col=228B22>[Lottery]The jackpot will be given out in 5 Minutes! <col=ff0000>Jackpot Lottery : "
									+ Utils.getFormattedNumber1(price) + " </col>.",
							false);
				} else if (lotteryCycle <= Utils.currentTimeMillis()) {
					if (LOTTERY_TICKETS.isEmpty()) {
						World.sendWorldMessage(
								"<img=2><col=228B22>No entries were made to the lottery this round. The Draw has been cancelled",
								false);
						lotteryCycle = Utils.currentTimeMillis() + LOTTERY_CYCLE_AMOUNT;
						return;
					}
					LotteryTicket winnerTicket = LOTTERY_TICKETS.get(Utils.random(LOTTERY_TICKETS.size() - 1));
					int price = LOTTERY_TICKETS.size() * TICKET_PRICE;
					winnerTicket.setWonAmount(price);
					LOTTERY_TICKETS.clear();
					WINNER_TICKETS.add(winnerTicket);
					World.sendWorldMessage("<img=2><col=228B22>[Lottery]" + winnerTicket.getOwner()
							+ " has just won the lottery with a price of " + Utils.getFormattedNumber1(price) + " gold.",
							false);
					World.sendWorldMessage("<img=2><col=228B22>[Lottery]New lottery has started.", false);
					lotteryCycle = Utils.currentTimeMillis() + LOTTERY_CYCLE_AMOUNT;
					anouncement1 = false;
					anouncement2 = false;
				}
			}
		}, 1, 1, TimeUnit.SECONDS);
	}

	public static int getTicketsAmount(Player player) {
		int count = 0;
		for (LotteryTicket ticket : LOTTERY_TICKETS) {
			if (ticket == null)
				continue;
			if (player.getDisplayName().equalsIgnoreCase(ticket.getOwner()))
				count++;
		}
		return count;
	}

	public static int getWinnerTicket(Player player) {
		for (int i = 0; i < WINNER_TICKETS.size(); i++) {
			LotteryTicket ticket = WINNER_TICKETS.get(i);
			if (ticket == null || !player.getDisplayName().equalsIgnoreCase(ticket.getOwner()))
				continue;
			return i;
		}
		return -1;
	}

	public static void buyTicket(Player player, int amount) {
		LotteryTicket ticket = new LotteryTicket(player.getDisplayName());
		for (int i = 0; i < amount; i++) {
			LOTTERY_TICKETS.add(ticket);
		}
	}

	public static final void save() {
		SerializableFilesManager.saveLotteryTickets(LOTTERY_TICKETS);
		SerializableFilesManager.saveWinnerTickets(WINNER_TICKETS);
		SerializableFilesManager.saveLotteryCycle(lotteryCycle);
	}

	public static long getLotteryCycle() {
		return lotteryCycle;
	}

	public static void setLotteryCycle(long lotteryCycle) {
		Lottery.lotteryCycle = lotteryCycle;
	}

	public static class LotteryTicket implements Serializable {
		private static final long serialVersionUID = -6762300902521666689L;
		private String owner;
		private int wonAmount;

		public LotteryTicket(String owner) {
			this.owner = owner;
		}

		public String getOwner() {
			return owner;
		}

		public int getWonAmount() {
			return wonAmount;
		}

		public void setWonAmount(int wonAmount) {
			this.wonAmount = wonAmount;
		}

	}

}
