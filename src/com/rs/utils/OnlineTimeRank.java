package com.rs.utils;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;

import com.rs.game.player.Player;

public final class OnlineTimeRank implements Serializable {

    private static final long serialVersionUID = 5403480618483552509L;

    public static void checkRank(Player player, int mode, String boss) {
		long floorId = player.getDominionTower().getProgress();
		for (int i = 0; i < ranks.length; i++) {
		    OnlineTimeRank rank = ranks[i];
		    if (rank == null)
		    	break;
		    if (rank.username.equalsIgnoreCase(player.getUsername())) {
		    	ranks[i] = new OnlineTimeRank(player, mode, boss);
		    	sort();
		    	return;
		    }
		}
		for (int i = 0; i < ranks.length; i++) {
		    OnlineTimeRank rank = ranks[i];
		    if (rank == null) {
		    	ranks[i] = new OnlineTimeRank(player, mode, boss);
				sort();
				return;
		    }
		}
		for (int i = 0; i < ranks.length; i++) {
		    if (ranks[i].floorId < floorId) {
		    	ranks[i] = new OnlineTimeRank(player, mode, boss);
		    	sort();
		    	return;
		    }
		}
    }

    public static void init() {
		File file = new File(PATH);
		if (file.exists())
		    try {
		    	ranks = (OnlineTimeRank[]) SerializableFilesManager.loadSerializedFile(file);
		    	return;
		    } catch (Throwable e) {
		    	Logger.handle(e);
		    }
			ranks = new OnlineTimeRank[10];
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
		int count = 0;
		for (OnlineTimeRank rank : ranks) {
		    if (rank == null)
		    	return;
		    player.getPackets().sendIComponentText(1158, 9 + count * 5, 
		    		Utils.formatString(rank.username));
		    player.getPackets().sendIComponentText(1158, 10 + count * 5, 
		    		"Reached floor " + rank.floorId + ", killing: " + rank.bossName  + ".");
		    player.getPackets().sendIComponentText(1158, 11 + count * 5, 
		    		Utils.getFormattedNumber((int) rank.dominionFactor));
		    count++;
		}
    }

    public static void sort() {
		Arrays.sort(ranks, new Comparator<OnlineTimeRank>() {
		    @Override
		    public int compare(OnlineTimeRank arg0, OnlineTimeRank arg1) {
				if (arg0 == null)
				    return 1;
				if (arg1 == null)
				    return -1;
				if (arg0.floorId < arg1.floorId)
				    return 1;
				else if (arg0.floorId > arg1.floorId)
				    return -1;
				else
				    return 0;
		    }
		});
    }

    private String username;

    private long dominionFactor;

    private String bossName;

    private int floorId;

    private static OnlineTimeRank[] ranks;

    private static final String PATH = "data/hiscores/dTower.Zeus";

    public OnlineTimeRank(Player player, int mode, String bossName) {
		this.username = player.getUsername();
		this.bossName = bossName;
		this.floorId = player.getDominionTower().getProgress();
		dominionFactor = player.getDominionTower().getTotalScore();
    }
    
	/**
	 * Gets the Top 3 Domnion Tower ranks.
	 * @return the top 3 player names as String.
	 */
	public static String getDominionTowerTop() {
		short count = 0;
		String top1 = null, top2 = null, top3 = null;
		for (OnlineTimeRank rank : ranks) {
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
		return "Top 3 Dominion Tower fighters  -  "+top1+"  -  "+top2+"  -  "+top3+".";
	}
}