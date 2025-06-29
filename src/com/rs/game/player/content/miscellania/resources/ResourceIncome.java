package com.rs.game.player.content.miscellania.resources;

import com.rs.game.player.Player;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public class ResourceIncome implements ThroneResource {

	private Player king;
	private StashType type;
	

	public ResourceIncome(Player king, StashType type) {
		this.setKing(king);
		this.type = type;
		generateStashType();
	}
	
	@Override
	public void generateStashType() {
		int followers = 1/*king.getThrone().getFollowers().length*/;
		if(king.getThrone().getReputation() < 75.0)
			return;
		for (int i = 0; i < followers; i++) {
			for (int r = 0; r < ResourceConstants.values().length;r++) {
				king.getThrone().addResource(ResourceConstants.WOOD.getItems()[r]);
				king.getThrone().addResource(ResourceConstants.ORE.getItems()[r]);
				king.getThrone().addResource(ResourceConstants.FISH.getItems()[r]);
				king.getThrone().addResource(ResourceConstants.NESTS.getItems()[r]);
			}
		}
		alertKing();
	}

	@Override
	public void alertKing() {
		king.getPackets().sendGameMessage("<img=5><col=ff0000>Your throne has deposited their resources.");//TODO
	}

	public StashType getType() {
		return type;
	}

	public StashType setType(StashType type) {
		this.type = type;
		return type;
	}

	public Player getKing() {
		return king;
	}

	public void setKing(Player king) {
		this.king = king;
	}

}
