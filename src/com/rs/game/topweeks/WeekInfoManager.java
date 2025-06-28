package com.rs.game.topweeks;

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.utils.Logger;
import com.rs.utils.SerializableFilesManager;

public class WeekInfoManager implements Serializable {
	private static final long serialVersionUID = -8035113863212780262L;
	public static final String DIR = "data/currentweek.wim";//this right? all g yeah
	
	private String lastDateRecorded;
	private HashMap<String, Integer> voteMap;
	private HashMap<String, Double> donateMap;
	private HashMap<String, Long> timeMap;
	private byte weekCycle;
	
	public void tick() {
		if(!lastDateRecorded.equals(getDate())) {
			lastDateRecorded = getDate();
			weekCycle++;
		}
		if(weekCycle >= 7) {
			drawWinners();
			generateNewWeek();
		}
		for(Player player : World.getPlayers()) {
			TopWeeklyTime.addTime(player);
		}
	}
	
	public void drawWinners() {
		TopWeeklyVoters.drawWinners();
		//TopWeeklyTime.drawWinners();
	}
	
	@SuppressWarnings("unchecked")
	public Object[] getTopVoters() {
		Object[] a = voteMap.entrySet().toArray();
		Arrays.sort(a, new Comparator() {
		    public int compare(Object o1, Object o2) {
		        return ((Map.Entry<String, Integer>) o2).getValue()
		                   .compareTo(((Map.Entry<String, Integer>) o1).getValue());
		    }
		});
		return a;
	}
	
	@SuppressWarnings("unchecked")
	public Object[] getTopDonators() {
		Object[] a = donateMap.entrySet().toArray();
		Arrays.sort(a, new Comparator() {
		    public int compare(Object o1, Object o2) {
		        return ((Map.Entry<String, Integer>) o2).getValue()
		                   .compareTo(((Map.Entry<String, Integer>) o1).getValue());
		    }
		});
		return a;
	}
	
	@SuppressWarnings("unchecked")
	public Object[] getTopTimes() {
		Object[] a = timeMap.entrySet().toArray();
		Arrays.sort(a, new Comparator() {
		    public int compare(Object o1, Object o2) {
		        return ((Map.Entry<String, Long>) o2).getValue()
		                   .compareTo(((Map.Entry<String, Long>) o1).getValue());
		    }
		});
		return a;
	}
	
	public static void init() {
		Logger.log("WeekInfoManager", "Initiated weekly information system...");
		if(new File(DIR).exists()) {
			World.wim = SerializableFilesManager.loadWeekInfo();
			Logger.log("WeekInfoManager", "Loaded current week. Cycle: "+World.wim.weekCycle);
		} else {
			generateNewWeek();
			Logger.log("WeekInfoManager", "No week currently found. Generating fresh week...");
		}
		
	}
	
	public void save() {
		SerializableFilesManager.saveWeek(this);
	}
	
	public void delete() {
		File file = new File(DIR);
		if(file.exists())
			file.delete();
	}
	
	public static void generateNewWeek() {
		if(World.wim != null)
			World.wim.delete();
		WeekInfoManager wim = new WeekInfoManager();
		World.wim = wim;
		wim.save();
	}
	
	public WeekInfoManager() {
		this.lastDateRecorded = "";
		this.weekCycle = 0;
		this.donateMap = new HashMap<String, Double>();
		this.voteMap = new HashMap<String, Integer>();
		this.timeMap = new HashMap<String, Long>();
	}

	public String getLastDateRecorded() {
		return lastDateRecorded;
	}

	public void setLastDateRecorded(String lastDateRecorded) {
		this.lastDateRecorded = lastDateRecorded;
	}

	public byte getWeekCycle() {
		return weekCycle;
	}

	public void setWeekCycle(byte weekCycle) {
		this.weekCycle = weekCycle;
	}
	
	public static String getDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		Date date = new Date();
		return ""+dateFormat.format(date);
	}

	public HashMap<String, Integer> getVoteMap() {
		return voteMap;
	}

	public void setVoteMap(HashMap<String, Integer> voteMap) {
		this.voteMap = voteMap;
	}

	public HashMap<String, Double> getDonateMap() {
		return donateMap;
	}

	public void setDonateMap(HashMap<String, Double> donateMap) {
		this.donateMap = donateMap;
	}

	public HashMap<String, Long> getTimeMap() {
		return timeMap;
	}

	public void setTimeMap(HashMap<String, Long> timeMap) {
		this.timeMap = timeMap;
	}
	
}
