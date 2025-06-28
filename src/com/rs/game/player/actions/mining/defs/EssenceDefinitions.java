package com.rs.game.player.actions.mining.defs;

/**
 * EssenceDefinitions.java | 11:48:02 AM
 * @author Chryonic
 * @date Apr 15, 2017
 */
public enum EssenceDefinitions {

	Rune_Essence(1, 5, 1436, 1, 1), Pure_Essence(30, 5, 7936, 1, 1);

	private int level;
	private double xp;
	private int oreId;
	private int oreBaseTime;
	private int oreRandomTime;

	private EssenceDefinitions(int level, double xp, int oreId, int oreBaseTime, int oreRandomTime) {
		this.level = level;
		this.xp = xp;
		this.oreId = oreId;
		this.oreBaseTime = oreBaseTime;
		this.oreRandomTime = oreRandomTime;
	}

	public int getLevel() {
		return level;
	}

	public int getOreBaseTime() {
		return oreBaseTime;
	}

	public int getOreId() {
		return oreId;
	}

	public int getOreRandomTime() {
		return oreRandomTime;
	}

	public double getXp() {
		return xp;
	}
}
