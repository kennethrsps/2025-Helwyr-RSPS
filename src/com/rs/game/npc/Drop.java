package com.rs.game.npc;

public class Drop {

	private int itemId, minAmount, maxAmount;
	private double rate;
	//private boolean rare;

	public Drop(int itemId, double rate, int minAmount, int maxAmount/*, boolean rare*/) {
		this.itemId = itemId;
		this.rate = rate;
		this.minAmount = minAmount;
		this.maxAmount = maxAmount;
		//this.rare = rare;
	}

	public Drop(int itemId, double rate, int minAmount) {
		this.itemId = itemId;
		this.rate = rate;
		this.minAmount = minAmount;
		this.maxAmount = minAmount;
	//	this.rare = false;
	}

	public static Drop create(int itemId, double rate, int minAmount, int maxAmount/*, boolean rare*/) {
		return new Drop((short) itemId, rate, minAmount, maxAmount/*, rare*/);
	}

	public int getExtraAmount() {
		return maxAmount - minAmount;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(short itemId) {
		this.itemId = itemId;
	}

	public int getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(int amount) {
		this.maxAmount = amount;
	}

	public int getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(int amount) {
		this.minAmount = amount;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	public boolean isFromRareTable() {
		// TODO Auto-generated method stub
		return false;
	}

	/*public boolean isFromRareTable() {
		return rare;
	}*/
}