package com.rs.game.player.content;

import java.util.TimerTask;

import com.rs.cores.CoresManager;
import com.rs.game.player.Player;

/**
 * Handles the Loyalty points timer task.
 * 
 * @author Zeus
 */
public class LoyaltyManager {

	private transient Player player;

	public LoyaltyManager(Player player) {
		this.player = player;
	}

	public void addReward(int lps) {
		player.setLoyaltyPoints(player.getLoyaltyPoints() + lps);
	}

	public void addBox() { //
		if (player.getInventory().hasFreeSlots())
			player.getInventory().addItem(43410, 1);
		else
			player.getBank().addItem(43410, 1, true);
	}

	public void startTimer() {
		player.setTimes(0);
		CoresManager.fastExecutor.schedule(new TimerTask() {
			int timer = 1800;

			@Override
			public void run() {
				if (player.hasFinished() || player == null) {
					this.cancel();
					return;
				}
				if (timer == 1) {
					player.setTimes(player.getTimes() + 1);
					timer = 1800;
					player.checkPorts();
					if (player.getTimes() == 1) {
						player.sendMessage("<col=18A300>You've received 10 loyalty points for 30 minute gameplay!");
						addReward(10);
						if (player.getMoneySpent() >= 250) {
							addBox();
						}
					}
					if (player.getTimes() == 2) {
						player.sendMessage("<col=18A300>You've received 11 loyalty points for 1 hour gameplay!");
						addReward(11);
						player.sendMessage("<col=18A300>You've received 1 loyalty Box for 1 hour gameplay!");
						addBox();
					}
					if (player.getTimes() == 3) {
						player.sendMessage(
								"<col=18A300>You've received 12 loyalty points for 1 hour & 30 minute gameplay!");
						addReward(12);
						if (player.getMoneySpent() >= 250) {
							addBox();
						}
					}
					if (player.getTimes() == 4) {
						player.sendMessage("<col=18A300>You've received 13 loyalty points for 2 hour gameplay!");
						addReward(13);
						player.sendMessage("<col=18A300>You've received 1 loyalty Box for 1 hour gameplay!");
						addBox();
					}
					if (player.getTimes() == 5) {
						player.sendMessage(
								"<col=18A300>You've received 14 loyalty points for 2 hour & 30 minute gameplay!");
						addReward(14);
						if (player.getMoneySpent() >= 250) {
							addBox();
						}
					}
					if (player.getTimes() == 6) {
						player.sendMessage("<col=18A300>You've received 15 loyalty points for 3 hour gameplay!");
						addReward(15);
						player.sendMessage("<col=18A300>You've received 1 loyalty Box for 1 hour gameplay!");
						addBox();
					}
					if (player.getTimes() == 7) {
						player.sendMessage(
								"<col=18A300>You've received 16 loyalty points for 3 hour & 30 minute gameplay!");
						addReward(16);
						if (player.getMoneySpent() >= 250) {
							addBox();
						}
					}
					if (player.getTimes() == 8) {
						player.sendMessage("<col=18A300>You've received 17 loyalty points for 4 hour gameplay!");
						addReward(17);
						player.sendMessage("<col=18A300>You've received 1 loyalty Box for 1 hour gameplay!");
						addBox();
					}
					if (player.getTimes() == 9) {
						player.sendMessage(
								"<col=18A300>You've received 18 loyalty points for 4 hour & 30 minute gameplay!");
						addReward(18);
						if (player.getMoneySpent() >= 250) {
							addBox();
						}
					}
					if (player.getTimes() == 10) {
						player.sendMessage("<col=18A300>You've received 19 loyalty points for 5 hour gameplay!");
						addReward(19);
						player.sendMessage("<col=18A300>You've received 1 loyalty Box for 1 hour gameplay!");
						addBox();
					}
					if (player.getTimes() == 11) {
						player.sendMessage(
								"<col=18A300>You've received 20 loyalty points for 5 hour & 30 minute gameplay!");
						addReward(20);
						if (player.getMoneySpent() >= 250) {
							addBox();
						}
					}
					if (player.getTimes() == 12) {
						player.sendMessage("<col=18A300>You've received 21 loyalty points for 6 hour gameplay!");
						addReward(21);
						player.sendMessage("<col=18A300>You've received 1 loyalty Box for 1 hour gameplay!");
						addBox();
					}
					if (player.getTimes() == 13) {
						player.sendMessage(
								"<col=18A300>You've received 22 loyalty points for 6 hour & 30 minute gameplay!");
						addReward(22);
						if (player.getMoneySpent() >= 250) {
							addBox();
						}
					}
					if (player.getTimes() == 14) {
						player.sendMessage("<col=18A300>You've received 23 loyalty points for 7 hour gameplay!");
						addReward(23);
						player.sendMessage("<col=18A300>You've received 1 loyalty Box for 1 hour gameplay!");
						addBox();
					}
					if (player.getTimes() == 15) {
						player.sendMessage(
								"<col=18A300>You've received 24 loyalty points for 7 hour & 30 minute gameplay!");
						addReward(24);
						if (player.getMoneySpent() >= 250) {
							addBox();
						}
					}
					if (player.getTimes() == 16) {
						player.sendMessage("<col=18A300>You've received 25 loyalty points for 8 hour gameplay!");
						addReward(25);
						player.sendMessage("<col=18A300>You've received 1 loyalty Box for 1 hour gameplay!");
						addBox();
					}
					if (player.getTimes() == 17) {
						player.sendMessage(
								"<col=18A300>You've received 26 loyalty points for 8 hour & 30 minute gameplay!");
						addReward(26);
						if (player.getMoneySpent() >= 250) {
							addBox();
						}
					}
					if (player.getTimes() == 18) {
						player.sendMessage("<col=18A300>You've received 27 loyalty points for 9 hour gameplay!");
						addReward(27);
						player.sendMessage("<col=18A300>You've received 1 loyalty Box for 1 hour gameplay!");
						addBox();
					}
					if (player.getTimes() == 19) {
						player.sendMessage(
								"<col=18A300>You've received 28 loyalty points for 9 hour & 30 minute gameplay!");
						addReward(28);
						if (player.getMoneySpent() >= 250) {
							addBox();
						}
					}
					if (player.getTimes() == 20) {
						player.sendMessage("<col=18A300>You've received 29 loyalty points for 10 hour gameplay!");
						addReward(29);
						player.sendMessage("<col=18A300>You've received 1 loyalty Box for 1 hour gameplay!");
						addBox();
					}
					if (player.getTimes() > 20) {
						player.sendMessage("<col=18A300>You've received 30 loyalty points for over 10 hour gameplay!");
						addReward(30);
						if (player.getMoneySpent() >= 250) {
							addBox();
						}
					}
				}
				if (timer > 0)
					timer--;
			}
		}, 0L, 1000L);
	}
}