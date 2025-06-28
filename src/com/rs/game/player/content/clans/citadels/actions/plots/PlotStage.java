package com.rs.game.player.content.clans.citadels.actions.plots;

import java.io.Serializable;

import com.rs.game.player.content.clans.Clan;

public class PlotStage implements Serializable {

	private static final long serialVersionUID = 7663654102920956786L;

	public static final int WOODCUT = 0, MINING = 1, KILN = 2, FURNACE = 3, LOOM = 4, SUMMONING = 5;
	
	public Clan clan;
	public int plot[];
	public int stage[];
	
	public PlotStage() {
		plot = new int[6];
		stage = new int[12];
		for(int plots = 0; plots < plot.length; plots++) {
			plot[plots] = 0;
		}
		for(int stages = 0; stages < stage.length; stages++) {
			stage[stages] = 0;
		}
	}
	
	public void setPlotStage(int plotType, int stage) {
		plot[plotType] = stage;
	}
	
	public int getPlotStage(int stage) {
		return plot[stage];
	}

}
