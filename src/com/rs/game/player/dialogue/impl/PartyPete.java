package com.rs.game.player.dialogue.impl;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;

/**
 * Handles the Party Pete dialogue.
 * 
 * @author Zeus
 */
public class PartyPete extends Dialogue {

	int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendOptionsDialogue("Select an Option", "Store Credit Shop", "Quests", "Vote Store", Colors.red+"[2 Coins] </col>Animation Store </col>",
				"Special Perks" + Colors.green + " [500hrs Playtime]");
		stage = -1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			switch (componentId) {
			case OPTION_1:
				player.getDialogueManager().startDialogue("ReferralDonationD");
				break;
			case OPTION_2:
				player.getDialogueManager().startDialogue("QuestsD");
				break;
			case OPTION_3:
				finish();
				ShopsHandler.openShop(player, 47);
				break;
			case OPTION_4:
				player.getDialogueManager().startDialogue("AnimationStoreD");
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Option", "D'Companion </col>" + Colors.gold + "[2Bil]",
						"Stag·ger </col>" + Colors.gold + "[2Bil]", "Annihilator  </col>" + Colors.gold + "[1Bil]",
						"Dominator </col>" + Colors.gold + "[1Bil]",
						"Heart of tarrasque </col>" + Colors.gold + "[1Bil]");
				stage = 1;
				break;
			}
			break;
		case 1:
			switch (componentId) {
			case OPTION_1:
				sendOptionsDialogue("Select an Option", "Details", "Buy'Companion", "Cancel");
				stage = 2;
				break;
			case OPTION_2:
				sendOptionsDialogue("Select an Option", "Details", "Buy Stag·ger", "Cancel");
				stage = 3;
				break;
			case OPTION_3:
				sendOptionsDialogue("Select an Option", "Details", "Buy Annihilator", "Cancel");
				stage = 4;
				break;
			case OPTION_4:
				sendOptionsDialogue("Select an Option", "Details", "Buy Dominator", "Cancel");
				stage = 5;
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Option", "Details", "Buy Heart of tarrasque", "Cancel");
				stage = 6;
				break;
			}
			break;
		case 2:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CALM, "Ability to make your Pets attack!");
				stage = 61;
				break;
			case OPTION_2:
				if (player.getPerkManager().petmaster == true) {
					player.sm("You already have this perk!");
					finish();
					return;

				}
				if (!player.getInventory().containsCoins(2000000000)) {
					player.sm(Colors.red + "You need 2Billion to buy this Special Perk!");
					finish();
					return;
				}
				if (Utils.getMinutesPlayed(player) <= 30000) {
					player.sm("You need to have atleast 500 hours playtime to buy this!,  You only have "
							+ Utils.getMinutesPlayed(player) / 60 + " Hour/s playtime");
					finish();
					return;

				}
				if (player.getInventory().containsCoins(2000000000)) {
					player.getInventory().deleteCoins(2000000000);
					player.getPerkManager().petmaster = true;
					player.sendMessage("You've purchased: [" + Colors.red + "D'Companion</col>]. "
							+ "Type ;;perks to see all your game perks.");
					finish();
					return;
				}
				break;

			case OPTION_3:
				finish();
				break;

			}
			break;

		case 3:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CALM, "Ability to have Permanent chance to Stun your enemy for 3 seconds.");
				stage = 61;
				break;
			case OPTION_2:
				if (player.getPerkManager().basher == true) {
					player.sm("You already have this perk!");
					finish();
					return;

				}
				if (Utils.getMinutesPlayed(player) <= 30000) {
					player.sm("You need to have atleast 500 hours playtime to buy this!,  You only have "
							+ Utils.getMinutesPlayed(player) / 60 + " Hour/s playtime");
					finish();
					return;

				}
				if (!player.getInventory().containsCoins(2000000000)) {
					player.sm(Colors.red + "You need 2Billion to buy this Special Perk!");
					finish();
					return;
				}
				if (player.getInventory().containsCoins(2000000000)) {
					player.getInventory().deleteCoins(2000000000);
					player.getPerkManager().basher = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Stag·ger</col>]. "
							+ "Type ;;perks to see all your game perks.");
					finish();
					return;
				}
				break;

			case OPTION_3:
				finish();
				break;

			}
			break;
		case 4:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CALM, "Chance to deal damage to nearby NPC's.");
				stage = 61;
				break;
			case OPTION_2:
				if (player.getPerkManager().butcher == true) {
					player.sm("You already have this perk!");
					finish();
					return;

				}
				if (!player.getInventory().containsCoins(1000000000)) {
					player.sm(Colors.red + "You need 1Billion to buy this Special Perk!");
					finish();
					return;
				}
				if (Utils.getMinutesPlayed(player) <= 30000) {
					player.sm("You need to have atleast 500 hours playtime to buy this!,  You only have "
							+ Utils.getMinutesPlayed(player) / 60 + " Hour/s playtime");
					finish();
					return;

				}
				if (player.getInventory().containsCoins(1000000000)) {
					player.getInventory().deleteCoins(1000000000);
					player.getPerkManager().butcher = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Annihilator</col>]. "
							+ "Type ;;perks to see all your game perks.");
					finish();
					return;
				}
				break;

			case OPTION_3:
				finish();
				break;

			}
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CALM, "Grants you a chance to lifesteal 10% of your damage to any monster.");
				stage = 61;
				break;
			case OPTION_2:
				if (player.getPerkManager().lifeSteal == true) {
					player.sm("You already have this perk!");
					finish();
					return;

				}
				if (Utils.getMinutesPlayed(player) <= 30000) {
					player.sm("You need to have atleast 500 hours playtime to buy this!,  You only have "
							+ Utils.getMinutesPlayed(player) / 60 + " Hour/s playtime");
					finish();
					return;

				}
				if (!player.getInventory().containsCoins(1000000000)) {
					player.sm(Colors.red + "You need 1Billion to buy this Special Perk!");
					finish();
					return;
				}
				if (player.getInventory().containsCoins(1000000000)) {
					player.getInventory().deleteCoins(1000000000);
					player.getPerkManager().lifeSteal = true;
					player.sendMessage("You've purchased: [" + Colors.red + "Dominator</col>]. "
							+ "Type ;;perks to see all your game perks.");
					finish();
					return;
				}
				break;

			case OPTION_3:
				finish();
				break;

			}
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				sendPlayerDialogue(CALM, "Grants you a chance to lifesteal 10% of your damage to any monster.");
				stage = 61;
				break;
			case OPTION_2:

				if (player.getPerkManager().regenerator == true) {
					player.sm("You already have this perk!");
					finish();
					return;

				}

				if (Utils.getMinutesPlayed(player) <= 30000) {
					player.sm("You need to have atleast 500 hours playtime to buy this!,  You only have "
							+ Utils.getMinutesPlayed(player) / 60 + " Hour/s playtime");
					finish();
					return;

				}
				if (!player.getInventory().containsCoins(1000000000)) {
					player.sm(Colors.red + "You need 1Billion to buy this Special Perk!");
					finish();
					return;
				}
				if (player.getInventory().containsCoins(1000000000)) {
					player.getInventory().deleteCoins(1000000000);
					player.getPerkManager().regenerator = true;
					player.sendMessage("You've purchased: [" + Colors.red + "	Heart of tarrasque</col>]. "
							+ "Type ;;perks to see all your game perks.");
					finish();
					return;
				}
				break;

			case OPTION_3:
				finish();
				break;

			}
			break;
		case 60:
			finish();
			break;
		case 61:
			sendOptionsDialogue("Select an Option", "D'Companion", "Stag·ger", "Annihilator", "Dominator",
					"Heart of tarrasque");
			stage = 1;
			break;
		}

	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();

	}
}