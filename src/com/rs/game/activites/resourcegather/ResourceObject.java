package com.rs.game.activites.resourcegather;

import java.io.Serializable;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.utils.Utils;

public class ResourceObject implements Serializable {
	private static final long serialVersionUID = -5895593170985018769L;

	public int itemId;
	public int amount;
	public double xpGain;
	public int skillId;
	public long timeStampStarted;
	public boolean isNpc;//item id acts as npc Id, amount acts as amount killed.
	
	public double getAmountPerHour() {
		int secondsPassed = (int) ((int) (Utils.currentTimeMillis() / 1000) - (timeStampStarted / 1000));
		double pm = (double) amount / secondsPassed;
		double ph = pm * 1000;
		return ph;
	}
	
	public double getXPGainPerHour() {
		int secondsPassed = (int) ((int) (Utils.currentTimeMillis() / 1000) - (timeStampStarted / 1000));
		double pm = (double) xpGain / secondsPassed;
		double ph = pm * 1000;
		return ph;
	}
	
	public double getGPGainPerHour() {
		int secondsPassed = (int) ((int) (Utils.currentTimeMillis() / 1000) - (timeStampStarted / 1000));
		double pm = (double) getTotalValue() / secondsPassed;
		double ph = pm * 1000;
		return ph;
	}
	
	public static String getFormattedStat(double stat) {
		String mark = "";
		int display = (int) stat;
		if(stat >= 10000) {
			mark = "k";
			display = (int) stat / 1000;
		}
		if(stat >= 10000000) {
			mark = "m";
			display = (int) stat / 1000000;
		}
		return display + "" + mark;
	}
	
	public int getTotalValue() {
		return ItemDefinitions.getItemDefinitions(itemId).getValue() * amount;
	}
	
	public ResourceObject(int item, int amount, double xp, long time) {
		this.itemId = item;
		this.amount = amount;
		this.xpGain = xp;
		this.timeStampStarted = time;
		this.isNpc = false;
	}
	
}
