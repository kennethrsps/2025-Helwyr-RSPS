package com.rs.game.player.dialogue.impl;

import com.rs.Settings;
import com.rs.game.activites.ShootingStar;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Utils;

/**
 * Handles the Star Sprite dialogue.
 * @author Zeus
 */
public class StarSpriteD extends Dialogue { 

	@Override
	public void start() {
		if (player.getInventory().containsOneItem(ShootingStar.STARDUST) 
				&& (Utils.currentTimeMillis() - player.getLastStarSprite()) >  (1 * 60 * 60 * 1000)) {
			sendNPCDialogue(ShootingStar.SPRITE, NORMAL, "Thank you for helping me out of here.");
			stage = 0;
		} else {
			sendNPCDialogue(ShootingStar.SPRITE, NORMAL, "I'm a star sprite! "
					+ "I was in my star in the sky, when it lost control and crashed into the ground. "
					+ "With half my star sticking into the ground, I became stuck. Fortunately, "
					+ "I was mined out by the kind creatures of");
			stage = 1; 
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case 0:
			int stardust = player.getInventory().getAmountOf(ShootingStar.STARDUST);
			if (stardust > 200)
				stardust = 200;
			player.getInventory().deleteItem(ShootingStar.STARDUST, stardust);
			int amtCosmic = (int) (stardust * 152 / 200 * Settings.getDropQuantityRate(player));
			int amtAstral = (int) (stardust * 52 / 200 * Settings.getDropQuantityRate(player));
			int amtGold = (int) (stardust * 20 / 200 * Settings.getDropQuantityRate(player));
			int amtCoins = (int) (stardust * 50002 / 200 * Settings.getDropQuantityRate(player));
			player.getInventory().addItemDrop(564, amtCosmic);
			player.getInventory().addItemDrop(9075, amtAstral);
			player.getInventory().addItemDrop(445, amtGold);
			player.addMoney(amtCoins);
			player.setLastStarSprite(Utils.currentTimeMillis());
			sendNPCDialogue(ShootingStar.SPRITE, NORMAL, "I have rewarded you by mine extra ore for the "
					+ "next 15 minutes. Also, have "+amtCosmic+" cosmic runes, "+amtAstral+" astral runes, "
							+ amtGold+" gold ores and "+amtCoins+" coins.");
			stage = -2;
			break;
		case 1:
			sendNPCDialogue(ShootingStar.SPRITE, NORMAL, "your race.");
			stage = 2;
			break;
		case 2:
			sendPlayerDialogue(NORMAL, "Well, I'm glad you're okay.");
			stage = -2;
			break;
		default:
			end();
			break;
		}
	}

	@Override
	public void finish() { }
}