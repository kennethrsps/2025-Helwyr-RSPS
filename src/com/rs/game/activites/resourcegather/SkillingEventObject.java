package com.rs.game.activites.resourcegather;

import java.util.HashMap;

import com.rs.cache.loaders.ItemDefinitions;

public class SkillingEventObject {

	public String player;
	public HashMap<Integer, ResourceObject> resources;
	
	public ResourceObject getIndex(int index) {
		int temp = 0;
		for(ResourceObject res : resources.values()) {
			if(temp == index) {
				return res;
			}
			temp++;
		}
		return null;
	}
	
	public int getResourceAmount() {
		int amount = 0;
		for(ResourceObject res : resources.values()) {
			amount += res.amount;
		}
		return amount;
	}
	
	public double getValueTotal() {
		double gold = 0;
		for(ResourceObject res : resources.values()) {
			gold += ItemDefinitions.getItemDefinitions(res.itemId).getValue() * res.amount;
		}
		return gold;
	}
	
	public double getXpTotal() {
		double xp = 0;
		for(ResourceObject res : resources.values()) {
			xp += res.xpGain;
		}
		return xp;
	}
	
	public SkillingEventObject(String player) {
		this.player = player;
		this.resources = new HashMap<Integer, ResourceObject>();
	}
	
}
