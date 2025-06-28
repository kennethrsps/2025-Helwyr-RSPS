package com.rs.game.activites.ZombieOutpost;

import com.rs.utils.Logger;

public class ZOManager {
	
	public static boolean gameActive;
	
	/**
	 * OBJECTS
	 * bloody skulls : 70469
	 */

	public static void init() {
		Logger.log("ZOManager", "Initiated Zombie Outpost...");
		ZOLobby.spawnHomeLobbyObjects();
		ZOLobby.initiateTick();
		ZOGame.initiateTick();
		ZombieManager.initiateZombies();
		ZombieShop.init();
		TowerObject.initiateTick();
	}
	
	
	
}
