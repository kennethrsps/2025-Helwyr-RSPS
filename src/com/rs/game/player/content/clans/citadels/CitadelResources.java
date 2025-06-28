package com.rs.game.player.content.clans.citadels;

import java.io.Serializable;

import com.rs.utils.Utils;

/**
 * 
 * @author Frostbite
 *
 *<frostbitersps@gmail.com><skype@frostbitersps>
 */

public class CitadelResources implements Serializable{

	private static final long serialVersionUID = -8831290056333774281L;
	
	public static final int WOODCUTTING = 0,
			MINING = 1,
			KILN = 2,
			FURANCE = 3,
			LOOM = 4,
			SUMMONING = 5,
			BARBEQUE = 6;
	
	
	public static final String[] PLOT_NAMES = {"Woodcutting",
			"Mining", "Kiln", "Firemaking", "Loom", "Summoning", "Barbeque"};
	
	public enum Plots {
		
		WOODCUTTING_PLOT_1(-1, 250, 75, Utils.random(250, 1500)),

		MINING_PLOT_1(-1, 250, 75, Utils.random(250, 2510)),

		KILN_PLOT_1(-1, 250, 75, Utils.random(250, 2510)),

		FIREMAING_PLOT_1(-1, 250, 75, Utils.random(250, 2510)),

		LOOM_PLOT_1(-1, 250, 75, Utils.random(250, 2510)),

		SUMMONING_PLOT_1(-1, 250, 75, Utils.random(250, 2510)),

		BARBEQUE_PLOT_1(-1, 250, 75, Utils.random(250, 2510));

		private int objectId;
		private int experience;
		private int resources;
		private int revenue;

		private Plots(int objectId, int experience, int resources, int revenue) {
			this.objectId = objectId;
			this.experience = experience;
			this.resources = resources;
			this.revenue = revenue;
		}

		public int getPlotObjectId() {
			return objectId;
		}

		public int getPlotExperience() {
			return experience;
		}

		public int getPlotResources() {
			return resources;
		}

		public int getPlotRevenue() {
			return revenue;
		}

	}

}
