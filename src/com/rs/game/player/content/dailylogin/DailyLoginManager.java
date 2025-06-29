package com.rs.game.player.content.dailylogin;

import java.io.Serializable;

public class DailyLoginManager implements Serializable{

	/**
	 * @author Era
	 */
	private static final long serialVersionUID = -3818409376475387631L;
	
	public DailyLoginManager(){
		dailyLogin = new boolean[8];
	}
	
	public boolean[] dailyLogin = new boolean[8];
	public int weekOfMonth;
	public int daysClaimed = 0;
	public int lastClaimedDate; 
	
}
