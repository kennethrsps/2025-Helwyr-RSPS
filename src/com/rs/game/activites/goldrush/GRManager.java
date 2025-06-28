package com.rs.game.activites.goldrush;

import java.io.Serializable;
import java.text.DecimalFormat;

import com.rs.game.player.Player;

public class GRManager implements Serializable {
	private static final long serialVersionUID = 6463112777935502089L;

	private Player player;
	private double balance;// in 1000s
	public int betAmount;
	public double autoCashout;

	public boolean cashedOut;
	public double cashoutMark;

	public void sendInterface() {
		player.getPackets().sendIComponentText(979, 85, "Balance: " + new DecimalFormat("#.###").format(balance) + "k");
		player.getPackets().sendIComponentText(979, 18, betAmount + "k");
		player.getPackets().sendIComponentText(979, 81, "x" + autoCashout);
		if (GRGame.PLAYERS.contains(player) && !cashedOut) {
			player.getPackets().sendIComponentText(979, 13, "Cash Out!");
		} else {
			player.getPackets().sendIComponentText(979, 13, "Place Bet");
		}
		player.getPackets().sendItemOnIComponent(979, 84, 995, 100000);
		if (!player.getInterfaceManager().containsInterface(979)) {
			player.getInterfaceManager().sendInterface(979);
		}
	}

	public void handleButtons(int c) {
		if (c == 99) {
			player.getTemporaryAttributtes().put("gr_withdraw", Boolean.TRUE);
			player.getPackets().sendRunScript(108, new Object[] { "Enter withdraw amount (k)" });
			return;
		}
		if (c == 98) {
			player.getTemporaryAttributtes().put("gr_deposit", Boolean.TRUE);
			player.getPackets().sendRunScript(108, new Object[] { "Enter deposit amount (k)" });
			return;
		}
		if (c == 101) {
			player.getTemporaryAttributtes().put("gr_bet", Boolean.TRUE);
			player.getPackets().sendRunScript(108, new Object[] { "Enter bet amount (k)" });
			return;
		}
		if (c == 102) {
			player.getTemporaryAttributtes().put("gr_cashout", Boolean.TRUE);
			player.getPackets().sendRunScript(109, new Object[] { "Enter auto cash-out mark" });
			return;
		}
		if (c == 103) {
			player.closeInterfaces();
			return;
		}
		if (c == 104) {
			player.getPackets().sendOpenURL("");
			return;
		}
		if (c == 100) {
			if (GRGame.PLAYERS.contains(player) && !this.cashedOut) {
				if (GRGame.GAME_ACTIVE) {
					cashout(true);
				}
				return;
			}
			placeBet();
			return;
		}
	}

	public void placeBet() {
		if (GRGame.GAME_ACTIVE) {
			player.errorMessage("Please wait until next round.");
			return;
		}
		if (betAmount > balance) {
			changeBet(balance);
		}
		if (betAmount < 1) {
			player.errorMessage("You can't bet nothing!");
			return;
		}
		if (autoCashout < 1 && autoCashout != 0) {
			player.errorMessage("Auto cash-out must be 0 or above 1. (0 = no auto cash-out)");
			return;
		}
		cashedOut = false;
		cashoutMark = 0;
		balance -= betAmount;
		player.getPackets().sendIComponentText(979, 85, "Balance: " + new DecimalFormat("#.###").format(balance) + "k");
		player.getPackets().sendIComponentText(979, 13, "Locked In @" + betAmount + "k");
		GRGame.PLAYERS.add(player);
	}

	public void cashout(boolean win) {
		cashoutMark = GRGame.MULTIPLIER;
		cashedOut = true;
		if (win) {
			// cashoutMark = autoCashout;
			double winnings = (betAmount * GRGame.MULTIPLIER);
			balance += winnings;
		}
		player.getPackets().sendIComponentText(979, 85, "Balance: " + new DecimalFormat("#.###").format(balance) + "k");
		player.getPackets().sendIComponentText(979, 13, "Place Bet");
	}

	public void withdraw(int amount) {
		if (amount > balance)
			amount = (int) balance;
		if (((player.getInventory().getAmountOf(995) / 1000) + amount) > 2147000) {
			amount = 2147000 - (player.getInventory().getAmountOf(995) / 1000);
		}
		if (amount < 1) {
			return;
		}
		balance -= amount;
		player.getInventory().addItem(995, amount * 1000);
		player.getPackets().sendIComponentText(979, 85, "Balance: " + new DecimalFormat("#.###").format(balance) + "k");
	}

	public void deposit(int amount) {
		if (amount >= 2147000)
			amount = 2147000;
		if (!player.getInventory().containsCoins(amount * 1000)) {
			amount = player.getInventory().getAmountOf(995) / 1000;
		}
		balance += amount;
		player.getInventory().deleteItem(995, amount * 1000);
		player.getPackets().sendIComponentText(979, 85, "Balance: " + new DecimalFormat("#.###").format(balance) + "k");
	}

	public void changeBet(double value) {
		betAmount = (int) value;
		player.getPackets().sendIComponentText(979, 18, betAmount + "k");
	}

	public void changeCashout(String value) {
		try {
			autoCashout = Double.parseDouble(value);
			player.getPackets().sendIComponentText(979, 81, "x" + autoCashout);
		} catch (Exception e) {
			player.errorMessage("Please enter a valid integer or double amount!");
		}
	}

	public GRManager(Player player) {
		this.setPlayer(player);
		this.betAmount = 0;
		this.autoCashout = 2.0;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

}
