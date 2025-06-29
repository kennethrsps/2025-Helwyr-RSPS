package com.rs.utils;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import com.rs.game.item.Item;
import com.rs.game.player.Player;

public final class WealthRank implements Serializable {

	private static final long serialVersionUID = 5403480618483552509L;

	private String username;
	private double wealh;
	
	private static WealthRank[] ranks;

	private static final String PATH = "data/hiscores/wealthranks.chry";

	public WealthRank(Player player) {
		this.username = player.getUsername();
		this.wealh = getWealth(player);
	}

	public static void init() {
		File file = new File(PATH);
		if (file.exists())
			try {
				ranks = (WealthRank[]) SerializableFilesManager.loadSerializedFile(file);
				return;
			} catch (Throwable e) {
				Logger.handle(e);
			}
		ranks = new WealthRank[10];
	}

	public static final void save() {
		try {
			SerializableFilesManager.storeSerializableClass(ranks, new File(PATH));
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static void showRanks(Player player) {
		player.getInterfaceManager().sendInterface(1158);
		player.getPackets().sendIComponentText(1158, 74, Colors.white+"Top 10 Wealthiest Players</col>");
		int count = 0;
		for (WealthRank rank : ranks) {
			if (rank == null)
				return;
			player.getPackets().sendIComponentText(1158, 9 + count * 5,
					Utils.formatString(rank.username));
			player.getPackets().sendIComponentText(
					1158,
					10 + count * 5,
					"Wealth: "+Utils.getFormattedNumber((int)rank.wealh)+""+getMark(rank.wealh));
			player.getPackets().sendIComponentText(1158, 11 + count * 5, "");
			count++;
		}
	}
	
	public static String getMark(double wealth) {
		if(wealth >= 1000000) {//b
			return "b";
		}
		if(wealth >= 1000) {//m
			return "m";
		}
		return "k";
	}

	public static void sort() {
		Arrays.sort(ranks, new Comparator<WealthRank>() {
			@Override
			public int compare(WealthRank arg0, WealthRank arg1) {
				if (arg0 == null)
					return 1;
				if (arg1 == null)
					return -1;
				if (arg0.wealh < arg1.wealh)
					return 1;
				else if (arg0.wealh > arg1.wealh)
					return -1;
				else
					return 0;
			}

		});
	}
	
	public static double getWealth(Player player) {//in k
		double total = 0;
		for(Item item : player.getInventory().getItems().getItems()) {
			if(item != null)
				total += (item.getDefinitions().getValue() * item.getAmount()) / 1000;
		}
		for(Item item : player.getEquipment().getItems().getItems()) {
			if(item != null)
				total += (item.getDefinitions().getValue() * item.getAmount()) / 1000;
		}
		for(Item item : player.getBank().getContainerCopy()) {
			if(item != null)
				total += (item.getDefinitions().getValue() * item.getAmount()) / 1000;
		}
		total += player.getMoneyPouchValue() / 1000;
		return total;
	}

	public static void checkRank(Player player) {
		if (player.isDeveloper()) {
			//return;
		}
		double wealth = getWealth(player);
		for (int i = 0; i < ranks.length; i++) {
			WealthRank rank = ranks[i];
			if (rank == null)
				break;
			if (rank.username.equalsIgnoreCase(player.getUsername())) {
				ranks[i] = new WealthRank(player);
				sort();
				return;
			}
		}
		for (int i = 0; i < ranks.length; i++) {
			WealthRank rank = ranks[i];
			if (rank == null) {
				ranks[i] = new WealthRank(player);
				sort();
				return;
			}
		}
		for (int i = 0; i < ranks.length; i++) {
			if (ranks[i].wealh < wealth) {
				ranks[i] = new WealthRank(player);
				sort();
				return;
			}
		}
	}
	
	/**
	 * Gets the Top 3 PK ranks.
	 * @return the top 3 player names as String.
	 */
	public static String getPkerTop() {
		short count = 0;
		String top1 = null, top2 = null, top3 = null;
		for (WealthRank rank : ranks) {
		    if (rank == null)
		    	return null;
		    switch (count) {
		    case 0:
		    	top1 = Utils.formatString(rank.username);
		    	break;
		    case 1:
		    	top2 = Utils.formatString(rank.username);
		    	break;
		    case 2:
		    	top3 = Utils.formatString(rank.username);
		    	break;
		    }
		    count ++;
		}
		return "Top 3 serial killers (PvP)  -  "+top1+"  -  "+top2+"  -  "+top3+".";
	}
}