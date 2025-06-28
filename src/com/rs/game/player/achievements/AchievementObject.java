package com.rs.game.player.achievements;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.item.Item;

public class AchievementObject {

	public String type;//EASY, MED, HARD, ELITE
	public String name;
	public String desc;
	public int amount;
	public String key;
	public Item[] items;
	
	public AchievementObject(String type, String name, String desc, int amount, String key, Item[] items) {
		this.type = type;
		this.name = name;
		this.amount = amount;
		this.desc = desc;
		this.key = key;
		this.items = items;
	}
	
	public static AchievementObject getObjectForKey(String key) {
		for(AchievementObject ach : AchievementManager.achievements) {
			if(ach.key.equals(key)) {
				return ach;
			}
		}
		return null;
	}
	
	public static List<AchievementObject> getObjectsForType(String type) {
		List<AchievementObject> achs = new ArrayList<AchievementObject>();
		for(AchievementObject ach : AchievementManager.achievements) {
			if(ach.type.equals(type)) {
				achs.add(ach);
			}
		}
		return achs;
	}
	
}
