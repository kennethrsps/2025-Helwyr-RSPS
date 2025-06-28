package com.rs.game.player.content.clans.citadels.actions.plots;

import com.rs.game.Animation;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;

public class Obelisk extends Action {

	public Animation OBELISK_ANIMATION = new Animation(5800);
	
	int object;
	
	@Override
	public boolean start(Player player) {
		if(!checkAll(player)) {
			return false;
		}
		return true;
	}
	
	public boolean checkAll(Player player) {
		if(player.getSkills().getLevel(Skills.SUMMONING) < 15) {
			player.getPackets().sendGameMessage("You do not have the required level to interact with this object.");
			return false;
		}
		return true;
	}

	@Override
	public boolean process(Player player) {
		return checkAll(player);
	}

	@Override
	public int processWithDelay(Player player) {
		player.setNextAnimationNoPriority(OBELISK_ANIMATION);
		//player.getPackets().sendObjectAnimation(15335, animation);
		player.getSkills().addXp(Skills.SUMMONING, 171);
		player.getPackets().sendGameMessage("Siphoned from obelisk.");
		return 5;
	}
	
	@Override
	public void stop(Player player) {
		setActionDelay(player, 3);
	}

}
