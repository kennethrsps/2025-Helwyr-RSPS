package com.rs.game;

import com.rs.utils.Utils;

public class ForceMovement {

	public static final int NORTH = 0, EAST = 1, SOUTH = 2, WEST = 3, NORTH_EAST = 4, NORTH_WEST = 5, SOUTH_WEST = 6,
			SOUTH_EAST = 7;
	
	private WorldTile toFirstTile;
	private WorldTile toSecondTile;
	private int firstTileTicketDelay;
	private int secondTileTicketDelay;
	protected int direction;

	/*
	 * USE: moves to firsttile firstTileTicketDelay: the delay in game tickets
	 * between your tile and first tile the direction
	 */
	public ForceMovement(WorldTile toFirstTile, int firstTileTicketDelay,
			int direction) {
		this(toFirstTile, firstTileTicketDelay, null, 0, direction);
	}
	/*
	 * USE: moves to firsttile and from first tile to second tile
	 * firstTileTicketDelay: the delay in game tickets between your tile and
	 * first tile secondTileTicketDelay: the delay in game tickets between first
	 * tile and second tile the direction
	 */
	public ForceMovement(WorldTile toFirstTile, int firstTileTicketDelay,
			WorldTile toSecondTile, int secondTileTicketDelay, int direction) {
		this.toFirstTile = toFirstTile;
		this.firstTileTicketDelay = firstTileTicketDelay;
		this.toSecondTile = toSecondTile;
		this.secondTileTicketDelay = secondTileTicketDelay;
		this.direction = direction;
	}
	
	
	public int getDirection() {
		switch (direction) {
		case NORTH:
			return Utils.getAngle(0, 1);
		case EAST:
			return Utils.getAngle(1, 0);
		case SOUTH:
			return Utils.getAngle(0, -1);
		case NORTH_EAST:
			return Utils.getAngle(1, 1);
		case NORTH_WEST:
			return Utils.getAngle(-1, 1);
		case SOUTH_EAST:
			return Utils.getAngle(1, -1);
		case SOUTH_WEST:
			return Utils.getAngle(-1, -1);
		default:
		case WEST:
			return Utils.getAngle(-1, 0);
		}
	}

	public WorldTile getToFirstTile() {
		return toFirstTile;
	}

	public WorldTile getToSecondTile() {
		return toSecondTile;
	}

	public int getFirstTileTicketDelay() {
		return firstTileTicketDelay;
	}

	public int getSecondTileTicketDelay() {
		return secondTileTicketDelay;
	}
	
	private boolean preciseMovement;
	
	public boolean preciseMovement() {
		return preciseMovement;
	}
	
	/**
	 * Using a setter as this shouldn't be used unless you know what you're doing.
	 * Using it currently only for the Twin Furies for precision.
	 */
	public void setForceMovementPrecise(boolean val) {
		preciseMovement = val;
	}

}
