package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

/**
 * Handles the Party Pete dialogue.
 * 
 * @author Zeus
 */
public class AnimationStoreD extends Dialogue {

	int npcId;
	int price = -1;

	@Override
	public void start() {
		sendOptionsDialogue("Select an Override to buy",
				(player.getAnimations().hasBattleCry
						? Colors.green + "Slayer Battle Cry "
								+ (player.getAnimations().battleCry ? "enabled" : Colors.red + "disabled")
						: Colors.red + "Slayer Battle Cry"),
				(player.getAnimations().hasEnhancedPotion
						? Colors.green + "Enhanced Potion Making "
								+ (player.getAnimations().enhancedPotion ? "enabled" : Colors.red + "disabled")
						: Colors.red + "Enhanced Potion Making"),
				(player.getAnimations().hasLumberjackWc
						? Colors.green + "Lumberjack Woodcutting "
								+ (player.getAnimations().lumberjackWc ? "enabled" : Colors.red + "disabled")
						: Colors.red + "Lumberjack Woodcutting"),
				(player.getAnimations().hasDeepFishing
						? Colors.green + "Deep-Sea Fishing "
								+ (player.getAnimations().deepFishing ? "enabled" : Colors.red + "disabled")
						: Colors.red + "Deep-Sea Fishing"),
				"More options..");
		stage = 14;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {

		case 14:
			stage = 13;
			switch (componentId) {
			case OPTION_1:
				if (player.getAnimations().hasBattleCry) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}

				player.getAnimations().hasBattleCry = true;
				player.sm(
						Colors.green + "You've Enabled the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_2:
				if (player.getAnimations().hasEnhancedPotion) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasEnhancedPotion = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_3:
				if (player.getAnimations().hasLumberjackWc) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasLumberjackWc = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_4:
				if (player.getAnimations().hasDeepFishing) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasDeepFishing = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to buy",
						(player.getAnimations().hasZenResting
								? Colors.green + "Zen Resting "
										+ (player.getAnimations().zenResting ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Zen Resting"),
						(player.getAnimations().hasKarateFletch
								? Colors.green + "Karate-Chop Fletching "
										+ (player.getAnimations().karateFletch ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Karate-Chop Fletching"),
						(player.getAnimations().hasIronSmith
								? Colors.green + "Iron-Fist Smithing "
										+ (player.getAnimations().ironSmith ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Iron-Fist Smithing"),
						(player.getAnimations().hasChiMining
								? Colors.green + "Chi-Blast Mining "
										+ (player.getAnimations().chiMining ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Chi-Blast Mining"),
						"More options..");
				stage = 15;
				break;
			}
			break;
		case 15:
			stage = 90;
			switch (componentId) {
			case OPTION_1:
				if (player.getAnimations().hasZenResting) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");

					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasZenResting = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_2:
				if (player.getAnimations().hasKarateFletch) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");

					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasKarateFletch = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_3:
				if (player.getAnimations().hasIronSmith) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasIronSmith = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_4:
				if (player.getAnimations().hasChiMining) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasChiMining = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to buy",
						(player.getAnimations().hasSamuraiCook
								? Colors.green + "Samurai Cooking "
										+ (player.getAnimations().samuraiCook ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Samurai Cooking"),
						(player.getAnimations().hasRoundHouseWc
								? Colors.green + "Roundhouse Woodcutting "
										+ (player.getAnimations().roundHouseWc ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Roundhouse Woodcutting"),
						(player.getAnimations().hasBlastMining
								? Colors.green + "Blast Mining "
										+ (player.getAnimations().blastMining ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Blast Mining"),
						(player.getAnimations().hasStrongResting
								? Colors.green + "Strongarm Resting "
										+ (player.getAnimations().strongResting ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Strongarm Resting"),
						"More options..");
				stage = 16;
				break;
			}
			break;

		case 16:
			stage = 91;
			switch (componentId) {
			case OPTION_1:
				if (player.getAnimations().hasSamuraiCook) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasSamuraiCook = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_2:
				if (player.getAnimations().hasRoundHouseWc) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasRoundHouseWc = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_3:
				if (player.getAnimations().hasBlastMining) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasBlastMining = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_4:
				if (player.getAnimations().hasStrongResting) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasStrongResting = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to buy",
						(player.getAnimations().hasArcaneSmelt
								? Colors.green + "Arcane Smelting "
										+ (player.getAnimations().arcaneSmelt ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Arcane Smelting"),
						(player.getAnimations().hasArcaneResting
								? Colors.green + "Arcane Resting "
										+ (player.getAnimations().arcaneResting ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Arcane Resting"),
						(player.getAnimations().hasStrongWc
								? Colors.green + "Strongarm Woodcutting "
										+ (player.getAnimations().strongWc ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Strongarm Woodcutting"),
						(player.getAnimations().hasStrongMining
								? Colors.green + "Strongarm Mining "
										+ (player.getAnimations().strongMining ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Strongarm Mining"),
						"More options..");
				stage = 17;
				break;
			}
			break;

		case 17:
			stage = 92;
			switch (componentId) {
			case OPTION_1:
				if (player.getAnimations().hasArcaneSmelt) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasArcaneSmelt = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_2:
				if (player.getAnimations().hasArcaneResting) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasArcaneResting = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_3:
				if (player.getAnimations().hasStrongWc) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasStrongWc = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_4:
				if (player.getAnimations().hasStrongMining) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasStrongMining = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to buy",
						(player.getAnimations().hasArcaneFishing
								? Colors.green + "Arcane Fishing "
										+ (player.getAnimations().arcaneFishing ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Arcane Fishing"),
						(player.getAnimations().hasStrongBurial
								? Colors.green + "Strongarm Burial "
										+ (player.getAnimations().strongBurial ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Strongarm Burial"),
						(player.getAnimations().hasArcaneCook
								? Colors.green + "Arcane Cooking "
										+ (player.getAnimations().arcaneCook ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Arcane Cooking"),
						(player.getAnimations().hasPowerDivination
								? Colors.green + "Powerful Divination "
										+ (player.getAnimations().powerDivination ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Powerful Divination"),
						"More options..");
				stage = 18;
				break;
			}
			break;

		case 18:
			stage = 93;
			switch (componentId) {
			case OPTION_1:
				if (player.getAnimations().hasArcaneFishing) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasArcaneFishing = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_2:
				if (player.getAnimations().hasStrongBurial) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasStrongBurial = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_3:
				if (player.getAnimations().hasArcaneCook) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasArcaneCook = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_4:
				if (player.getAnimations().hasPowerDivination) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasPowerDivination = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to buy",
						(player.getAnimations().hasPowerConversion
								? Colors.green + "Powerful Conversion "
										+ (player.getAnimations().powerConversion ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Powerful Conversion"),
						(player.getAnimations().hasAgileDivination
								? Colors.green + "Agile Divination "
										+ (player.getAnimations().agileDivination ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Agile Divination"),
						(player.getAnimations().hasAgileConversion
								? Colors.green + "Agile Conversion "
										+ (player.getAnimations().agileConversion ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Agile Conversion"),
						(player.getAnimations().hasSinisterSlumber
								? Colors.green + "Sinister Slumber "
										+ (player.getAnimations().sinisterSlumber ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Sinister Slumber"),
						"More options..");
				stage = 19;
				break;
			}
			break;

		case 19:
			stage = 94;
			switch (componentId) {
			case OPTION_1:
				if (player.getAnimations().hasPowerConversion) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasPowerConversion = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_2:
				if (player.getAnimations().hasAgileDivination) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasAgileDivination = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_3:
				if (player.getAnimations().hasAgileConversion) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasAgileConversion = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_4:
				if (player.getAnimations().hasSinisterSlumber) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasSinisterSlumber = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to buy",
						(player.getAnimations().hasArmWarrior
								? Colors.green + "Armchair Warrior "
										+ (player.getAnimations().armWarrior ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Armchair Warrior"),
						(player.getAnimations().hasEneResting
								? Colors.green + "Energy Drain Resting "
										+ (player.getAnimations().eneResting ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Energy Drain Resting"),
						(player.getAnimations().hasCrystalResting
								? Colors.green + "Crystal Impling Resting "
										+ (player.getAnimations().crystalResting ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Crystal Impling Resting"),
						(player.getAnimations().hasHeadMining
								? Colors.green + "Headbutt Mining "
										+ (player.getAnimations().headMining ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Headbutt Mining"),
						"More options..");
				stage = 20;
				break;
			}
			break;

		case 20:
			stage = 95;
			switch (componentId) {
			case OPTION_1:
				if (player.getAnimations().hasArmWarrior) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasArmWarrior = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_2:
				if (player.getAnimations().hasEneResting) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasEneResting = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_3:
				if (player.getAnimations().hasCrystalResting) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasCrystalResting = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_4:
				if (player.getAnimations().hasHeadMining) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasHeadMining = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to buy",
						(player.getAnimations().hasSandWalk
								? Colors.green + "Sandstorm Walk "
										+ (player.getAnimations().sandWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Sandstorm Walk"),
						(player.getAnimations().hasSadWalk
								? Colors.green + "Sad Walk "
										+ (player.getAnimations().sadWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Sad Walk"),
						(player.getAnimations().hasAngryWalk
								? Colors.green + "Angry Walk "
										+ (player.getAnimations().angryWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Angry Walk"),
						(player.getAnimations().hasProudWalk
								? Colors.green + "Proud Walk "
										+ (player.getAnimations().proudWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Proud Walk"),
						"More options..");
				stage = 21;
				break;
			}
			break;

		case 21:
			stage = 96;
			switch (componentId) {
			case OPTION_1:
				if (player.getAnimations().hasSandWalk) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasSandWalk = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_2:
				if (player.getAnimations().hasSadWalk) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasSadWalk = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_3:
				if (player.getAnimations().hasAngryWalk) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasAngryWalk = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_4:
				if (player.getAnimations().hasProudWalk) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasProudWalk = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to buy",
						(player.getAnimations().hasHappyWalk
								? Colors.green + "Happy Walk "
										+ (player.getAnimations().happyWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Happy Walk"),
						(player.getAnimations().hasBarbarianWalk
								? Colors.green + "Barbarian Walk "
										+ (player.getAnimations().barbarianWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Barbarian Walk"),
						(player.getAnimations().hasRevenantWalk
								? Colors.green + "Revenant Walk "
										+ (player.getAnimations().revenantWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Revenant Walk"),
						"More options..");
				stage = 22;
				break;
			}
			break;
		case 22:
			stage = 97;
			switch (componentId) {
			case OPTION_1:
				if (player.getAnimations().hasHappyWalk) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasHappyWalk = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_2:
				if (player.getAnimations().hasBarbarianWalk) {
					sendNPCDialogue(npcId, SAD, "You already have this Animation!!");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasBarbarianWalk = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_3:
				if (player.getAnimations().hasRevenantWalk) {
					sendNPCDialogue(npcId, SAD, "I'm sorry, but you already have this animation");
					stage = 99;
					return;
				}
				if (!player.isDonator()) {
					sendDialogue("Sorry but this perk is exclusive for donator only!");
					stage = 99;
					return;
				}
				player.getAnimations().hasRevenantWalk = true;
				player.sm(Colors.green
						+ "You've purchased the Animation Successfully, Activate it by talking to Solomon");
				end();
				break;
			case OPTION_4:
				sendOptionsDialogue("Select an Override to buy",
						(player.getAnimations().hasBattleCry
								? Colors.green + "Slayer Battle Cry "
										+ (player.getAnimations().battleCry ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Slayer Battle Cry"),
						(player.getAnimations().hasEnhancedPotion
								? Colors.green + "Enhanced Potion Making "
										+ (player.getAnimations().enhancedPotion ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Enhanced Potion Making"),
						(player.getAnimations().hasLumberjackWc
								? Colors.green + "Lumberjack Woodcutting "
										+ (player.getAnimations().lumberjackWc ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Lumberjack Woodcutting"),
						(player.getAnimations().hasDeepFishing
								? Colors.green + "Deep-Sea Fishing "
										+ (player.getAnimations().deepFishing ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Deep-Sea Fishing"),
						"More options..");
				stage = 14;
				break;
			}
			break;

		case 90:
			sendOptionsDialogue("Select an Override to buy",
					(player.getAnimations().hasZenResting
							? Colors.green + "Zen Resting "
									+ (player.getAnimations().zenResting ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Zen Resting"),
					(player.getAnimations().hasKarateFletch
							? Colors.green + "Karate-Chop Fletching "
									+ (player.getAnimations().karateFletch ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Karate-Chop Fletching"),
					(player.getAnimations().hasIronSmith
							? Colors.green + "Iron-Fist Smithing "
									+ (player.getAnimations().ironSmith ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Iron-Fist Smithing"),
					(player.getAnimations().hasChiMining
							? Colors.green + "Chi-Blast Mining "
									+ (player.getAnimations().chiMining ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Chi-Blast Mining"),
					"More options..");
			stage = 15;
			break;
		case 91:
			sendOptionsDialogue("Select an Override to buy",
					(player.getAnimations().hasSamuraiCook
							? Colors.green + "Samurai Cooking "
									+ (player.getAnimations().samuraiCook ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Samurai Cooking"),
					(player.getAnimations().hasRoundHouseWc
							? Colors.green + "Roundhouse Woodcutting "
									+ (player.getAnimations().roundHouseWc ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Roundhouse Woodcutting"),
					(player.getAnimations().hasBlastMining
							? Colors.green + "Blast Mining "
									+ (player.getAnimations().blastMining ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Blast Mining"),
					(player.getAnimations().hasStrongResting
							? Colors.green + "Strongarm Resting "
									+ (player.getAnimations().strongResting ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Strongarm Resting"),
					"More options..");
			stage = 16;
			break;
		case 92:
			sendOptionsDialogue("Select an Override to buy",
					(player.getAnimations().hasArcaneSmelt
							? Colors.green + "Arcane Smelting "
									+ (player.getAnimations().arcaneSmelt ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Arcane Smelting"),
					(player.getAnimations().hasArcaneResting
							? Colors.green + "Arcane Resting "
									+ (player.getAnimations().arcaneResting ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Arcane Resting"),
					(player.getAnimations().hasStrongWc
							? Colors.green + "Strongarm Woodcutting "
									+ (player.getAnimations().strongWc ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Strongarm Woodcutting"),
					(player.getAnimations().hasStrongMining
							? Colors.green + "Strongarm Mining "
									+ (player.getAnimations().strongMining ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Strongarm Mining"),
					"More options..");
			stage = 17;
			break;
		case 93:
			sendOptionsDialogue("Select an Override to buy",
					(player.getAnimations().hasArcaneFishing
							? Colors.green + "Arcane Fishing "
									+ (player.getAnimations().arcaneFishing ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Arcane Fishing"),
					(player.getAnimations().hasStrongBurial
							? Colors.green + "Strongarm Burial "
									+ (player.getAnimations().strongBurial ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Strongarm Burial"),
					(player.getAnimations().hasArcaneCook
							? Colors.green + "Arcane Cooking "
									+ (player.getAnimations().arcaneCook ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Arcane Cooking"),
					(player.getAnimations().hasPowerDivination
							? Colors.green + "Powerful Divination "
									+ (player.getAnimations().powerDivination ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Powerful Divination"),
					"More options..");
			stage = 18;
			break;
		case 94:
			sendOptionsDialogue("Select an Override to buy",
					(player.getAnimations().hasPowerConversion
							? Colors.green + "Powerful Conversion "
									+ (player.getAnimations().powerConversion ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Powerful Conversion"),
					(player.getAnimations().hasAgileDivination
							? Colors.green + "Agile Divination "
									+ (player.getAnimations().agileDivination ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Agile Divination"),
					(player.getAnimations().hasAgileConversion
							? Colors.green + "Agile Conversion "
									+ (player.getAnimations().agileConversion ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Agile Conversion"),
					(player.getAnimations().hasSinisterSlumber
							? Colors.green + "Sinister Slumber "
									+ (player.getAnimations().sinisterSlumber ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Sinister Slumber"),
					"More options..");
			stage = 19;
			break;
		case 95:
			sendOptionsDialogue("Select an Override to buy",
					(player.getAnimations().hasArmWarrior
							? Colors.green + "Armchair Warrior "
									+ (player.getAnimations().armWarrior ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Armchair Warrior"),
					(player.getAnimations().hasEneResting
							? Colors.green + "Energy Drain Resting "
									+ (player.getAnimations().eneResting ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Energy Drain Resting"),
					(player.getAnimations().hasCrystalResting
							? Colors.green + "Crystal Impling Resting "
									+ (player.getAnimations().crystalResting ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Crystal Impling Resting"),
					(player.getAnimations().hasHeadMining
							? Colors.green + "Headbutt Mining "
									+ (player.getAnimations().headMining ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Headbutt Mining"),
					"More options..");
			stage = 20;
			break;
		case 96:
			sendOptionsDialogue("Select an Override to buy",
					(player.getAnimations().hasSandWalk
							? Colors.green + "Sandstorm Walk "
									+ (player.getAnimations().sandWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Sandstorm Walk"),
					(player.getAnimations().hasSadWalk
							? Colors.green + "Sad Walk "
									+ (player.getAnimations().sadWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Sad Walk"),
					(player.getAnimations().hasAngryWalk
							? Colors.green + "Angry Walk "
									+ (player.getAnimations().angryWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Angry Walk"),
					(player.getAnimations().hasProudWalk
							? Colors.green + "Proud Walk "
									+ (player.getAnimations().proudWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Proud Walk"),
					"More options..");
			stage = 21;
			break;
		case 97:
			sendOptionsDialogue("Select an Override to buy",
					(player.getAnimations().hasHappyWalk
							? Colors.green + "Happy Walk "
									+ (player.getAnimations().happyWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Happy Walk"),
					(player.getAnimations().hasBarbarianWalk
							? Colors.green + "Barbarian Walk "
									+ (player.getAnimations().barbarianWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Barbarian Walk"),
					(player.getAnimations().hasRevenantWalk
							? Colors.green + "Revenant Walk "
									+ (player.getAnimations().revenantWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Revenant Walk"),
					"More options..");
			stage = 22;
			break;

		case 99:
			end();
			break;

		}

	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();

	}
}