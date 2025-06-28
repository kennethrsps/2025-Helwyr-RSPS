package com.rs.game.player.dialogue.impl;

import com.rs.Settings;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.WorldTile;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.ShopsHandler;

public class DamageMaster extends Dialogue {

	int npcId;
	int DMG = 13652;//change this - this is the damage currency for reward shop.
	int shopId = 64;//change this -  this is damage reward shop. 

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendEntityDialogue(SEND_4_TEXT_CHAT,
			new String[] { "Damage Master",
				"Hi Welcome to Mummy Minigame, Here you can Hit Mummies and gain tokens to buy from my shop, you can also add a multiplier on your attack but take note everytime you leave the area the multiplier disappear." }, IS_NPC, npcId, 9827);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if(stage == -1) {
			stage++;
			sendOptionsDialogue("Damage Master",
				"I'd Like To See The <col=ff0000>Damage Shop", "I'd Like To Multiply My <col=ff0000>Mummy Damage","Teleport To MUMMY AREA");
		} else if(stage == 0) {//ok
			if(componentId == OPTION_1) {
				if(player.hasOpenedTentShop) {
					ShopsHandler.openShop(player, shopId);
					end();
				} else {
					stage++;
					sendEntityDialogue(SEND_2_TEXT_CHAT,
						new String[] { "Damage Master",
							"It'll cost you 500k damage tokens", "to see what's inside my shop." }, IS_NPC, npcId, 9827);
				}
			} else {
				
				stage = 4;
				sendEntityDialogue(SEND_3_TEXT_CHAT,
						new String[] { "Damage Master",
							"Damage Multipliers only count inside the Mummy Area.", "So don't go thinking you can use this", "in Player vs Player combat!" }, IS_NPC, npcId, 9827);
			}
			if(componentId == OPTION_3) {
				player.getControlerManager().startControler("DamageArea");
			}
			
		} else if(stage == 1) {
			stage++;
			sendOptionsDialogue("Damage Master",
				"Okay, I'll Pay.", "No, I'm Not Paying That!");
		} else if(stage == 2) {
			stage = -1;
			if(componentId == OPTION_1) {
				if(player.getInventory().containsItem(DMG, 100000)) {
					player.hasOpenedTentShop = true;
					player.getInventory().deleteItem(DMG, 100000);
					sendEntityDialogue(SEND_1_TEXT_CHAT,
						new String[] { "Damage Master",
							"Thanks." }, IS_NPC, npcId, 9827);
				} else {
					stage = 3;
					sendEntityDialogue(SEND_1_TEXT_CHAT,
						new String[] { player.getDisplayName(),
							"I don't have 500k damage tokens" }, IS_PLAYER, player.getIndex(), 9827);
				}
			} else {
				sendEntityDialogue(SEND_1_TEXT_CHAT,
						new String[] { "Damage Master",
							"Suit Yourself." }, IS_NPC, npcId, 9827);
			}
		} else if(stage == 3) {
			stage = 11;
			sendEntityDialogue(SEND_4_TEXT_CHAT,
						new String[] { "Damage Master",
							"Well stop standing around and go", "and get some!", "You can get damage tokens", "by hitting the Mummy." }, IS_NPC, npcId, 9827);
		} else if(stage == 4) {
			stage++;
			sendEntityDialogue(SEND_3_TEXT_CHAT,
						new String[] { "Damage Master",
							"The more multipliers you buy, the more damage", "you'll hit the Mummy. Meaning", "the more damage tokens you'll be getting." }, IS_NPC, npcId, 9827);
		} else if(stage == 5) {
			stage++;
			sendEntityDialogue(SEND_2_TEXT_CHAT,
						new String[] { "Damage Master",
							"I can upgrade your multiplier to "+(player.TentMulti + 1), "It'll cost you 10k damage tokens." }, IS_NPC, npcId, 9827);
		} else if(stage == 6) {
			stage++;
			sendOptionsDialogue("Damage Master",
				"Yes, Upgrade My Multiplier To "+(player.TentMulti + 1)+" For 10k DMG", "No Thanks.");
		} else if(stage == 7) {
			if(componentId == OPTION_1) {
				if(player.getInventory().containsItem(DMG, 10000)) {
					stage = -1;
					player.getInventory().deleteItem(DMG, 10000);
					player.TentMulti++;
					sendEntityDialogue(SEND_1_TEXT_CHAT,
						new String[] { player.getDisplayName(),
							"Your Damage Multiplier Is Now "+player.TentMulti+"!" }, IS_PLAYER, player.getIndex(), 9827);
				} else {
					stage = 3;
					sendEntityDialogue(SEND_1_TEXT_CHAT,
						new String[] { player.getDisplayName(),
							"I don't have 500k damage tokens" }, IS_PLAYER, player.getIndex(), 9827);
				}
			} else {
				stage = -1;
				
				sendEntityDialogue(SEND_1_TEXT_CHAT,
						new String[] { "Damage Master",
							"Suit Yourself." }, IS_NPC, npcId, 9827);
			}
		} else
			end();
	}

	@Override
	public void finish() {

	}

}
