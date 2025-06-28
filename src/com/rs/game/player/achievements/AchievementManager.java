package com.rs.game.player.achievements;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.EconomyPrices;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

public class AchievementManager implements Serializable {
	private static final long serialVersionUID = -2861767191290093306L;

	private Player player;
	private HashMap<String, Integer> achData;
	private List<String> achRewardsComplete;

	public transient List<AchievementObject> achs;
	
	public void sendInterface(String type) {
		achs = AchievementObject.getObjectsForType(type);
		int index = 0;
		player.getInterfaceManager().sendInterface(1083);
		for(AchievementObject ach : achs) {
			int[] comps = COMPS[index];
			player.getPackets().sendIComponentText(1083, comps[0], "'"+ach.name+"':");
			player.getPackets().sendIComponentText(1083, comps[1], getKeyAmount(ach.key)+"/"+ach.amount+" - "+(int)(Utils.getPercentage(getKeyAmount(ach.key), ach.amount, true))+"%");
			index++;
		}
		player.getPackets().sendIComponentText(1083, 85, "");
		player.getPackets().sendIComponentText(1083, 87, "");
		player.getPackets().sendIComponentText(1083, 89, "");
		player.getPackets().sendHideIComponent(1083, 90, true);
		player.getPackets().sendHideIComponent(1083, 161, false);
		player.getPackets().sendHideIComponent(1083, 160, false);
		player.getPackets().sendHideIComponent(1083, 165, false);
		player.getPackets().sendHideIComponent(1083, 162, true);
	}
	
	public void handleButtons(int c) {
		if(c == 244) {
			sendInterface("EASY");
		} else if(c == 254) {
			sendInterface("MEDIUM");
		} else if(c == 139) {
			sendInterface("HARD");
		} else if(c == 109) {
			sendInterface("ELITE");
		} else {
			int index = getIndexForComponentButton(c);
			if(index == -1) {
				return;
			}
			AchievementObject ach = achs.get(index);
			player.getPackets().sendIComponentText(1083, 85, "'"+ach.name+"'");
			player.getPackets().sendIComponentText(1083, 87, getKeyAmount(ach.key)+"/"+ach.amount+" - "+(Utils.getPercentage(getKeyAmount(ach.key), ach.amount, true))+"%");
			player.getPackets().sendIComponentText(1083, 89, ""+ach.desc);
			player.getPackets().sendHideIComponent(1083, 90, true);
		}
	}
	
	public static int getIndexForComponentButton(int c) {
		int index = 0;
		for(int[] comp : COMPS) {
			if(comp[2] == c) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
								//name string, completion string, button id
	public static int[][] COMPS = {
			{136, 137, 131}, 
			{132, 133, 74}, 
			{128, 129, 69}, 
			{124, 125, 64}, 
			{120, 121, 59}, 
			{116, 117, 54}, 
			{112, 113, 49}, 
			{108, 109, 44}, 
			{434, 435, 177}, 
			{445, 446, 188}, 
			{456, 457, 199}};
	public static List<AchievementObject> achievements;
	public static void init() {
		Logger.log("[AchievementManager]", "Initiating achievements...");
		achievements = new ArrayList<AchievementObject>();
		/**
		 * Each achievement has its own object in the "achievements" list.
		 * 
		 * Types:
		 * EASY
		 * MEDIUM
		 * HARD
		 * ELITE
		 * 
		 * -Try keep descriptions short if you can
		 * Reward format:
		 * new Item[]{new Item(4151, 1), new Item(995, 1000)}
		 * 
		 * 
		 */
												//type	//achieve name		//achieve description  //amo	//key	//rewards
		//achievements.add(new AchievementObject("EASY", "Kill Zeus 1 times", "Kill him with a sword", 1, "killzeus", new Item[] {new Item(4151, 1), new Item(995, 69000000)}));
		achievements.add(new AchievementObject("EASY", "Learning the skill", "Thieve 200 times in any thieving stall", 200, "thieve", new Item[] { new Item(995, 1000000)}));
		achievements.add(new AchievementObject("EASY", "Training 101", "Kill 100 Rock Crabs", 100, "rocky", new Item[] { new Item(995, 1000000)}));
		achievements.add(new AchievementObject("EASY", "Frost Dragon Slayer", "Kill Frost Dragon 10x", 1, "dragon", new Item[] { new Item(995, 1000000)}));
		achievements.add(new AchievementObject("EASY", "Bon Voyage", "Send a ship to voyage", 1, "ships", new Item[] { new Item(995, 1000000)}));
		achievements.add(new AchievementObject("EASY", "Death Contract", "Get a contract from  Death", 1, "death", new Item[] { new Item(995, 1000000)}));
		achievements.add(new AchievementObject("EASY", "GOTV", "Vote 7 Times", 7, "voters", new Item[] { new Item(995, 1000000)}));
		achievements.add(new AchievementObject("EASY", "Crystal Chest", "Open Crystal Chest 5 times", 5, "Crystal", new Item[] { new Item(995, 1000000)}));
		achievements.add(new AchievementObject("EASY", "Home Sweet Home", "Enter your House (construction)", 1, "house", new Item[] { new Item(995, 1000000)}));
		achievements.add(new AchievementObject("EASY", "Loot Beam", "Change your lootbeam amount to anything you like", 1, "beam", new Item[] { new Item(995, 1000000)}));
		achievements.add(new AchievementObject("EASY", "Security Lock", "Mac Lock your account on account manager", 1, "lock", new Item[] { new Item(995, 1000000)}));
		achievements.add(new AchievementObject("EASY", "Well of Goodwill", "Donate any amount on well of goodwill", 1, "donation", new Item[] { new Item(995, 1000000)}));
		
		achievements.add(new AchievementObject("MEDIUM", "Thieving Master", "Thieve 2000 Times in any thieving stall", 2000, "master", new Item[] { new Item(995, 2_500_000)}));
		achievements.add(new AchievementObject("MEDIUM", "Fishing Master", "Fish 3500 Times", 3500, "fisher", new Item[] { new Item(995, 2_500_000)}));
		achievements.add(new AchievementObject("MEDIUM", "Mining Master", "Mine any ore 1500 Times", 1500, "miner", new Item[] { new Item(995, 2_500_000)}));
		achievements.add(new AchievementObject("MEDIUM", "Woodcutting Master", "Cut any tree 1500 Times", 1500, "logger", new Item[] { new Item(995, 2_500_000)}));
		achievements.add(new AchievementObject("MEDIUM", "Agility Master", "Skill agility 3500 Times", 3500, "agility", new Item[] { new Item(995, 2_500_000)}));
		achievements.add(new AchievementObject("MEDIUM", "Herblore Master", "Skill Herblore 3500 Times", 3500, "herblore", new Item[] { new Item(995, 2_500_000)}));
		achievements.add(new AchievementObject("MEDIUM", "RC Master", "Skill Runecrafting 3500 Times", 3500, "runecrafting", new Item[] { new Item(995, 2_500_000)}));
		achievements.add(new AchievementObject("MEDIUM", "Fletching Master", "Fletch 3500 Times", 3500, "fletching", new Item[] { new Item(995, 2_500_000)}));
		achievements.add(new AchievementObject("MEDIUM", "Firemaking Master", "Skill Firemaking 3500 Times", 3500, "firemaking", new Item[] { new Item(995, 2_500_000)}));
		achievements.add(new AchievementObject("MEDIUM", "Hunter Master", "Skill Hunter 3500 Times", 3500, "hunter", new Item[] { new Item(995, 2_500_000)}));
		achievements.add(new AchievementObject("MEDIUM", "Construction Master", "Skill Construction 1000 Times", 1000, "hunter", new Item[] { new Item(995, 2_500_000)}));
		
		achievements.add(new AchievementObject("HARD", "Glacor Slayer", "Kill Glacors 800 times", 800, "glacor", new Item[] { new Item(995, 5_000_000),new Item(21793, 1)}));
		achievements.add(new AchievementObject("HARD", "Bandos Slayer", "Kill Bandos 400 times", 400, "bandos", new Item[] { new Item(995, 5_000_000),new Item(25019, 1)}));
		achievements.add(new AchievementObject("HARD", "Armadyl Slayer", "Kill Armadyl 400 times",400, "armadyl", new Item[] { new Item(995, 5_000_000),new Item(25013, 1)}));
		achievements.add(new AchievementObject("HARD", "Zamorak Slayer", "Kill Zamorak 400 times",400, "zamorak", new Item[] { new Item(995, 5_000_000)}));
		achievements.add(new AchievementObject("HARD", "Saradomin Slayer", "Kill Saradomin 400 times",400, "saradomin", new Item[] { new Item(995, 5_000_000),new Item(11730, 1)}));
		achievements.add(new AchievementObject("HARD", "KBD Slayer", "Kill KBD 150 times",150, "kbd", new Item[] { new Item(995, 5_000_000), new Item(11286, 1)}));
		achievements.add(new AchievementObject("HARD", "Barrel chest Slayer", "Kill Barrel Chest 200 times",200, "barrel", new Item[] { new Item(995, 5_000_000), new Item(10887, 1)}));
		achievements.add(new AchievementObject("HARD", "Primus Slayer", "Kill Legio Primus 200 times",200, "primus", new Item[] { new Item(995, 5_000_000)}));
		achievements.add(new AchievementObject("HARD", "Secundus Slayer", "Kill Legio Secundus 200 times",200, "secundus", new Item[] { new Item(995, 5_000_000)}));
		achievements.add(new AchievementObject("HARD", "Tertius Slayer", "Kill Legio Tertius 200 times",200, "tertius", new Item[] { new Item(995, 5_000_000)}));
		achievements.add(new AchievementObject("HARD", "Quartus Slayer", "Kill Legio Quartus 200 times",200, "quartus", new Item[] { new Item(995, 5_000_000)}));
		achievements.add(new AchievementObject("ELITE", "Fight Caves Champion", "Finish Fight Caves 3 times", 3, "fightcaves", new Item[] { new Item(995, 5_000_000),new Item(6529, 10000)}));
		achievements.add(new AchievementObject("ELITE", "Party Demon Slayer", "Kill Party Demon 5 times", 5, "partydemon", new Item[] { new Item(995, 15_000_000)}));
		achievements.add(new AchievementObject("ELITE", "Blink Slayer", "Kill Blink 150 times", 150, "blink", new Item[] { new Item(995, 5_000_000)}));
		achievements.add(new AchievementObject("ELITE", "Kalphite Queen Slayer", "Kill Kalphite Queen 25 times", 25, "kalphitequeen", new Item[] { new Item(995, 10_000_000)}));
		achievements.add(new AchievementObject("ELITE", "Kalphite King Slayer", "Kill Kalphite King 100 times", 100, "kalphiteking", new Item[] { new Item(995, 10_000_000)}));
		achievements.add(new AchievementObject("ELITE", "Nex Slayer", "Kill Nex 150 times", 150, "nex", new Item[] { new Item(995, 5_000_000), new Item(20171, 1)}));
		achievements.add(new AchievementObject("ELITE", "Corporeal Slayer", "Kill Corporeal 100 times",100, "corporeal", new Item[] { new Item(995, 5_000_000), new Item(13754, 1)}));
		achievements.add(new AchievementObject("ELITE", "Vorago Slayer", "Kill Vorago 100 times",100, "vorago", new Item[] { new Item(995, 10_000_000)}));
		achievements.add(new AchievementObject("ELITE", "Araxxor Slayer", "Kill Araxxor 100 times",100, "araxxor", new Item[] { new Item(995, 10_000_000)}));
		achievements.add(new AchievementObject("ELITE", "The Great Voter", "Vote 450 Times",450, "v450", new Item[] { new Item(995, 5_000_000)}));
		achievements.add(new AchievementObject("ELITE", "The Great Donator", "Donat Atleast $1",1, "donator", new Item[] { new Item(995, 5_000_000)}));
		achievements.add(new AchievementObject("ELITE", "Elite Dungeon Elegorn", "Kill Elegorn the Celestian 20 times",20, "eregorn", new Item[] {new Item(995, 10_000_000) })) ;
				
		
		
	/*	player.getAchManager().addKeyAmount("thieve", 1);
		getAchManager().addKeyAmount("rocky", 1);
		player.getAchManager().addKeyAmount("ports", 1);
		player.getAchManager().addKeyAmount("ships", 1);
		player.getAchManager().addKeyAmount("voters", 1);
		player.getAchManager().addKeyAmount("death", 1);
		player.getAchManager().addKeyAmount("Crystal", 1);
		player.getAchManager().addKeyAmount("house", 1);
		player.getAchManager().addKeyAmount("beam", 1);
		player.getAchManager().addKeyAmount("lock", 1);
		player.getAchManager().addKeyAmount("donation", 1);*/
		
		
		
	}
	
	/**
	 * Adding to an achievement
	 * player.getAchManager().addKeyAmount("achievementKey", 69);
	 */
	
	public transient AchievementObject ach;
	
	public void checkAchComplete(String key) {
		ach = AchievementObject.getObjectForKey(key);
		if(getKeyAmount(key) == ach.amount) {
			player.getInterfaceManager().sendInterface(1219);
			player.getPackets().sendUnlockIComponentOptionSlots(1219, 59, 0, 54, 0, 1, 2, 3, 4, 5, 6);
			player.getPackets().sendItems(90, ach.items);
			player.getPackets().sendIComponentText(1219, 60, ach.desc+"<br>complete!<br>---<br>"+getKeyAmount(key)+"/"+ach.amount+" - "+(Utils.getPercentage(getKeyAmount(key), ach.amount))+"%");
			for (int index = 0; index < ach.items.length; index++) {
				Item item = ach.items[index];
					player.getPackets().sendGlobalConfig(700 + index,
							item == null ? 0 : EconomyPrices.getPrice(item.getId()));
			}
			player.setCloseInterfacesEvent(new Runnable() {
				@Override
				public void run() {
					openRewards();
				}
			});
		}
	}
	
	public void openRewards() {
		if(getAchRewardsComplete() == null) {
			setAchRewardsComplete(new ArrayList<String>());
		}
		if(ach != null) {
			if(!getAchRewardsComplete().contains(ach.key)) {
				for(int i=0;i<ach.items.length;i++) {
					Item item = ach.items[i];
					player.getBank().addItem(item, true);
				}
				player.succeedMessage("Rewards added to bank!");
				player.setCloseInterfacesEvent(null);
				player.closeInterfaces();
				getAchRewardsComplete().add(ach.key);
			}
		}
	}
	
	public void addKeyAmount(String key, int amount) {
		if(getAchData().containsKey(key)) {
			Integer am = getAchData().get(key);
			am += amount;
			getAchData().put(key, am);
		} else {
			getAchData().put(key, amount);
		}
		checkAchComplete(key);
	}
	
	public int getKeyAmount(String key) {
		if(getAchData().containsKey(key)) {
			return getAchData().get(key);
		}
		return 0;
	}
	
	public AchievementManager(Player player) {
		this.player = player;
		this.setAchData(new HashMap<String, Integer>());
		this.setAchRewardsComplete(new ArrayList<String>());
	}
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}

	public HashMap<String, Integer> getAchData() {
		return achData;
	}

	public void setAchData(HashMap<String, Integer> achData) {
		this.achData = achData;
	}

	public List<String> getAchRewardsComplete() {
		return achRewardsComplete;
	}

	public void setAchRewardsComplete(List<String> achRewardsComplete) {
		this.achRewardsComplete = achRewardsComplete;
	}
}
