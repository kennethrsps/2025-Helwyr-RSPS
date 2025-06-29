package com.rs.game.player.content.clans.citadels.region.generation.upgrades;

public class CitadelUpgradeConstants {
	
	public enum UpgradeConstants {
		
		TIER_1(1, -1, 1, 488, 512, 504, 608, 16, 16);
		
		public int resourcesRequired;
		public int clanLevelRequired;
		private int plotAmount;
		public int dayCitadelRx;
		public int dayCitadelRy;
		public int nightCitadelRx;
		public int nightCitadelRy;
		public int heightRegions;
		public int widthRegions;
		
		
		UpgradeConstants(int clanLevelRequired, int resourcesRequired, int plotAmount, int dayCitadelRx, int dayCitadelRy, int nightCitadelRx, int nightCitadelRy, int heightRegions, int widthRegions) {
			this.clanLevelRequired = clanLevelRequired;
			this.resourcesRequired = resourcesRequired;
			this.plotAmount = plotAmount;
			this.dayCitadelRx = dayCitadelRx;
			this.dayCitadelRy = dayCitadelRy;
			this.nightCitadelRx = nightCitadelRx;
			this.nightCitadelRy = nightCitadelRy;
			this.heightRegions = heightRegions;
			this.widthRegions = widthRegions;
		}
		
		
		public int getResourcesRequired() {
			return resourcesRequired;
		}
		
		public int getClanLevelRequired() {
			return clanLevelRequired;
		}
		
		public int getPlotAmount() {
			return plotAmount;
		}
		
		public int getDayCitadelRx() {
			return dayCitadelRx;
		}
		
		public int getDayCitadelRy() {
			return dayCitadelRy;
		}
		
		public int getNightCitadelRx() {
			return nightCitadelRx;
		}
		
		public int getNightCitadelRy() {
			return nightCitadelRy;
		}
		
		public int getHeightRegions() {
			return heightRegions;
		}
		
		public int getWidthRegions() {
			return widthRegions;
		}
		
	}

}
