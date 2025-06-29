package com.rs.game.player.content.dailylogin;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Locale;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.content.dailylogin.DailyLoginConstants.LoginRewards;
import com.rs.utils.Colors;

public class DailyLoginInter {
	
	static Calendar c = Calendar.getInstance(new Locale("en","US"));
	static int weekOfMonth = c.get(Calendar.WEEK_OF_MONTH);
	static LocalDateTime date = LocalDateTime.now();
	static int INTER = 3004;
	static int cids[] = {18,25,32,39,46,53,60,67};
	public static void openInter(Player player) {
		player.getInterfaceManager().sendInterface(INTER);
		checkWeek(player);
		perpareLogin(player);
		showRewards(player);
	}

	private static void checkWeek(Player player) {
		if(player.getLoginManager().weekOfMonth == -1)
			player.getLoginManager().weekOfMonth = weekOfMonth;
		if(player.getLoginManager().weekOfMonth != weekOfMonth) {
			reset(player);
		}
	}

	private static void perpareLogin(Player player) {
		int progress = 0;
		for(int i = 0 ; i < cids.length;i++) {
			if(player.getLoginManager().dailyLogin[i]) {
				hideC(player,cids[i]+4,true);
				hideC(player,cids[i]+6,false);
				progress++;
			}
		}
		double percent = (double) progress * (double) 100 / cids.length;
		updateProgressBar(player,percent);
	}
	
	private static void reset(Player player) {
		player.getLoginManager().dailyLogin = new boolean[8];
		player.getLoginManager().daysClaimed = 0;
		player.getLoginManager().weekOfMonth = weekOfMonth;
		player.getLoginManager().lastClaimedDate = -1;
	}

	private static void showRewards(Player player) {
		for(int i = 0;i < cids.length;i++) {
			LoginRewards rewards = LoginRewards.values()[weekOfMonth - 1];
			player.getPackets().sendItems(100+i, new Item[] {rewards.getRewards()[i]});
			player.getPackets().sendUnlockIComponentOptionSlots(INTER, cids[i]+3, 0, 1, 0);
			player.getPackets().sendInterSetItemsOptionsScript(INTER, cids[i]+3, 100+i, 4, 5, "Rewards");
		}
	}
	
	public static void handleButtons(Player player, int componentId, int packetId) {
		LoginRewards rewards = LoginRewards.values()[weekOfMonth - 1];
		if(componentId == 74) {//login button
			giveReward(player,rewards);
			return;
		}
		if(componentId == 71) {
			giveBonus(player,rewards);
			return;
		}
	}
	
	private static void giveBonus(Player player, LoginRewards rewards) {
		if(player.getLoginManager().daysClaimed < 7) {
			sendMessage(player,"You need to complete 7 days of login to be able to recieve bonus reward.");
			return;
		}
		
		if(player.getLoginManager().daysClaimed > 7) {
			sendMessage(player,"You already claimed your bonus reward, please come back next week for another challenge.");
			return;
		}
		boolean toBank = false;
		Item reward = rewards.getRewards()[7];
		if(!player.getInventory().addItem(reward)) {
			player.getBank().addItem(reward, true);
			toBank = true;
		}
		sendMessage(player,"You recieved "+reward.getName()+" "+reward.getAmount()+"x"+(toBank ? " sent to Bank.": ""));
		sendMessage(player,"Congratulations! you have claimed the bonus login reward!");		
		player.getLoginManager().dailyLogin[player.getLoginManager().daysClaimed] = true;
		player.getLoginManager().daysClaimed++;
		openInter(player);
		player.closeInterfaces();
	}

	private static void giveReward(Player player, LoginRewards rewards) {
		boolean toBank = false;
		if(player.getLoginManager().daysClaimed > 6) {
			sendMessage(player,"You have claimed all your daily login rewards this week, please come next week.");
			return;
		}
		if(player.getLoginManager().lastClaimedDate == LocalDateTime.now().getDayOfMonth()) {
			sendMessage(player,"You already claimed your daily login rewards today, please come back tomorrow.");
			return;
		}
		if(player.getLoginManager().daysClaimed <= 0)
			player.getLoginManager().daysClaimed = 0;
		Item reward = rewards.getRewards()[player.getLoginManager().daysClaimed];
		if(!player.getLoginManager().dailyLogin[player.getLoginManager().daysClaimed]) {
			player.getLoginManager().dailyLogin[player.getLoginManager().daysClaimed] = true;
			if(!player.getInventory().addItem(reward)) {
				player.getBank().addItem(reward, true);
				toBank = true;
			}
			sendMessage(player,"You recieved "+reward.getName()+" "+reward.getAmount()+"x"+(toBank ? " sent to Bank.": ""));
			openInter(player);
			player.closeInterfaces();
			player.getInterfaceManager().closeOverlay(player.getInterfaceManager().isResizableScreen() ? false : true);
		}
		player.getLoginManager().daysClaimed++;
		player.getLoginManager().lastClaimedDate = LocalDateTime.now().getDayOfMonth();
	}
	
	

	private static void sendMessage(Player player, String string) {
		player.sm(Colors.blue+"[DailyLogin]</col> "+string);
	}

	private static void updateProgressBar(Player player, double progress) {
		int x = -394;
		double newX = (x * progress / 100);
		player.getPackets().sendMoveIComponent(INTER, 15, (int) (x-newX), 0);
	}
	private static void sendText(Player player, int cid, String string) {
		player.getPackets().sendIComponentText(INTER, cid, string);
	}

	private static void hideC(Player player, int i,boolean hide) {
		player.getPackets().sendHideIComponent(INTER, i, hide);
	}
	
}
