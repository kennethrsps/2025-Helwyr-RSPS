package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class QuadExp extends Dialogue {

	int expChest;
	@Override
	public void start() {
		expChest =  (int) parameters[0];
		sendOptionsDialogue("Do you want to enable "+(expChest == 40191 ? "1 hour" : "4 hours")+" hour of 4x exp?", "YES","NO");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch(stage) {
		case -1:
			switch(componentId) {
			case OPTION_1:
				if(player.getInventory().containsOneItem(expChest)) {
					player.getInventory().deleteItem(expChest, 1);
				if(expChest == 40191)
				player.setQuadExp(expChest == 40191 ? 
						Utils.currentTimeMillis() + 1000
						* 60 //1 min 
						* 60 
						: 
						Utils.currentTimeMillis() + 1000
						* 60 //60 seconds
						* 60 //one hour
						* 4); //four hours); //one hour
				else
				player.setQuadExp(Utils.currentTimeMillis() + 1000
						* 60 //60 seconds
						* 60 //one hour
						* 4); //four hours
				player.sm(Colors.orange+"You have activated Quad Exp for "+(expChest == 40191 ? "1 hour" : "4 hours")+"! You may relog to be able to see the time left.");
				}else {
					player.sm("You need to have the chest on your inventory");
				}
				end();
				break;
				default:
					end();
					break;
			}
			break;
		}
	}

	@Override
	public void finish() {
	}
}