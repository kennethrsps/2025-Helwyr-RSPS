package com.rs.game.player.content.contracts;

import java.io.Serializable;

import com.rs.game.player.Player;

public class Contract implements Serializable {
	
	private static final long serialVersionUID = -8769578633982303218L;
	
	private int npcId, rewardId, rewardAmount, KillAmount, TotalKills, addamount, taskAmount, TotalContract;
	private boolean completed;
	
	public static Contract Rtask;

	public static boolean setCompleted;
	
	public Contract(int npcId, int reward, int rewardAmount, int randomminmax) {
		this.npcId = npcId;
		this.rewardId = reward;
		this.rewardAmount = rewardAmount;
		this.completed = false;
		this.KillAmount = randomminmax;
	}
	
	public static int givePoints(Player player) {
		int points = player.getContract().getRewardAmount();
		return points;
	}
	
	public int getNpcId() {
		return npcId;
	}
	
	public int getRewardId() {
		return rewardId;
	}
	
	public int getTaskAmount() {
		return taskAmount;
	}

	public void decreaseAmount() {
		KillAmount--;
	}
	
	public void increaseTotal() {
		TotalContract++;
	}
	
	
	public int getaddamount() {
		return addamount;
	}
	
	public int getRewardAmount() {
		return rewardAmount;
	}
	
	public int getKillAmount() {
		return KillAmount;
	}
	
	public int getTotal() {
		return TotalKills;
	}
	
	public int getTotalkill() {
		return TotalContract;
	}
	
	public boolean hasCompleted() {
		return completed;
	}
	
	public void setCompleted(boolean value) {
		this.completed = value;
	}
	
	public static Contract getRTask() {
		return Rtask;
	}
}