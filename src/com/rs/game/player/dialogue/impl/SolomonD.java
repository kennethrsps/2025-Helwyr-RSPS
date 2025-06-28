package com.rs.game.player.dialogue.impl;

import com.rs.Settings;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

/**
 * Handles Solomons dialogue and all shop things.
 * 
 * @author Zeus
 */
public class SolomonD extends Dialogue {

	/**
	 * Integer representing Solomon's ID.
	 */
	private int npcId;

	@Override
	public void start() {
		npcId = (Integer) parameters[0];
		sendNPCDialogue(npcId, NORMAL, "Welcome to Solomon's General store! <br>How may I be of assistance?");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (player.getOverrides().outfit == null)
				sendOptionsDialogue("Select an Option", "What is this place?", "Manage BankExtension & Petlooter",
						"I'd like to buy something.", "I'd like to check Cosmetics & Animation.");
			else
				sendOptionsDialogue("Select an Option", "What is this place?", "Manage BankExtension & Petlooter",
						"I'd like to buy something.", "I'd like to check Cosmetics & Animation.",
						"Could you remove my cosmetic override, please?");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				sendNPCDialogue(npcId, JUST_LISTEN,
						"This is Solomon's General store, the finest retailer " + "in the corporeal realm.");
				stage = 1;
				break;
			case OPTION_2:
				player.getDialogueManager().startDialogue("BankExtensionD");
				break;
			case OPTION_3:
				sendOptionsDialogue("Visit the online store?", "Yes.", "No.");
				stage = 3;
				break;
			case OPTION_4:
				sendNPCDialogue(npcId, NORMAL,
						"Certainly! If the item is marked in " + Colors.green
								+ "green</col> - it means you can use it. If it's marked in " + Colors.red
								+ "red</col> - it means you have to unlock it by " + "visiting our ;;store page!");
				stage = 11;
				break;
			case OPTION_5:
				sendNPCDialogue(npcId, NORMAL, "As you wish!");
				player.getOverrides().resetCosmetics();
				stage = 10;
				break;
			}
			break;
		case 1:
			sendNPCDialogue(npcId, NORMAL, "Here you can buy a selection of fine cosmetic items.");
			stage = -1;
			break;
		case 2:
			sendNPCDialogue(npcId, NORMAL, "It couldn't be simpler.");
			stage = -1;
			break;
		case 3:
			switch (componentId) {
			case OPTION_1:
				sendDialogue("Note: Players with a space in there names are advised to use an underscore instead for your donation to work properly");
				player.getPackets().sendOpenURL(Settings.DONATE + "");
				stage = 99;
				break;
			case OPTION_2:
				sendOptionsDialogue("Select an Option", "What is this place?", "How do I buy things here?",
						"I'd like to buy something.", "I'd like to see all available items.");
				stage = 0;
				break;
			}
			break;
		case 4:
			sendOptionsDialogue("Select an Override to use",
					(player.getOverrides().paladin ? Colors.green : Colors.red) + "Paladin Cosmetical Override",
					(player.getOverrides().warlord ? Colors.green : Colors.red) + "Warlord Cosmetical Override",
					(player.getOverrides().obsidian ? Colors.green : Colors.red) + "Obsidian Cosmetical Override",
					(player.getOverrides().kalphite ? Colors.green : Colors.red) + "Kalphite Cosmetical Override",
					"More options..");
			stage = 5;
			break;
		case 5:
			switch (componentId) {
			case OPTION_1:
				if (!player.getOverrides().paladin) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setPaladinOutfit();
				stage = 99;
				break;
			case OPTION_2:
				if (!player.getOverrides().warlord) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setWarlordOutfit();
				stage = 99;
				break;
			case OPTION_3:
				if (!player.getOverrides().obsidian) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setObsidianOutfit();
				stage = 99;
				break;
			case OPTION_4:
				if (!player.getOverrides().kalphite) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setKalphiteOutfit();
				stage = 99;
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getOverrides().demonflesh ? Colors.green : Colors.red)
								+ "Demonflesh Cosmetical Override",
						(player.getOverrides().remokee ? Colors.green : Colors.red) + "Remokee Cosmetical Override",
						(player.getOverrides().assassin ? Colors.green : Colors.red) + "Assassin Cosmetical Override",
						(player.getOverrides().skeleton ? Colors.green : Colors.red) + "Skeleton Cosmetical Override",
						"More options..");
				stage = 6;
				break;
			}
			break;
		case 6:
			switch (componentId) {
			case OPTION_1:
				if (!player.getOverrides().demonflesh) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setDemonfleshOutfit();
				stage = 99;
				break;
			case OPTION_2:
				if (!player.getOverrides().remokee) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setRemokeeOutfit();
				stage = 99;
				break;
			case OPTION_3:
				if (!player.getOverrides().assassin) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setAssassinOutfit();
				stage = 99;
				break;
			case OPTION_4:
				if (!player.getOverrides().skeleton) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setSkeletonOutfit();
				stage = 99;
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getOverrides().goth ? Colors.green : Colors.red) + "Goth Cosmetical Override",
						(player.getOverrides().mummy ? Colors.green : Colors.red) + "Mummy Cosmetical Override",
						(player.getOverrides().replicaDragon ? Colors.green : Colors.red)
								+ "Replica Dragon Cosmetical Override",
						(player.getOverrides().sentinel ? Colors.green : Colors.red) + "Sentinel Cosmetical Override",
						"More options..");
				stage = 7;
				break;
			}
			break;
		case 7:
			switch (componentId) {
			case OPTION_1:
				if (!player.getOverrides().goth) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setGothOutfit();
				stage = 99;
				break;
			case OPTION_2:
				if (!player.getOverrides().mummy) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setMummyOutfit();
				stage = 99;
				break;
			case OPTION_3:
				if (!player.getOverrides().replicaDragon) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setReplicaDragonOutfit();
				stage = 99;
				break;
			case OPTION_4:
				if (!player.getOverrides().sentinel) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setSentinelOutfit();
				stage = 99;
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getOverrides().reaver ? Colors.green : Colors.red) + "Reaver Cosmetical Override",
						(player.getOverrides().hiker ? Colors.green : Colors.red) + "Hiker Cosmetical Override",
						(player.getOverrides().skyguard ? Colors.green : Colors.red) + "Skyguard Cosmetical Override",
						(player.getOverrides().vyrewatch ? Colors.green : Colors.red) + "Vyrewatch Cosmetical Override",
						"More options..");
				stage = 8;
				break;
			}
			break;
		case 8:
			switch (componentId) {
			case OPTION_1:
				if (!player.getOverrides().reaver) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setReaverOutfit();
				stage = 99;
				break;
			case OPTION_2:
				if (!player.getOverrides().hiker) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setHikerOutfit();
				stage = 99;
				break;
			case OPTION_3:
				if (!player.getOverrides().skyguard) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setSkyguardOutfit();
				stage = 99;
				break;
			case OPTION_4:
				if (!player.getOverrides().vyrewatch) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setVyrewatchOutfit();
				stage = 99;
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getOverrides().snowman ? Colors.green : Colors.red) + "Snowman Cosmetical Override",
						(player.getOverrides().samurai ? Colors.green : Colors.red) + "Samurai Cosmetical Override",
						(player.getOverrides().warmWinter ? Colors.green : Colors.red)
								+ "Warm Winter Cosmetical Override",
						(player.getOverrides().darkLord ? Colors.green : Colors.red) + "Dark Lord Cosmetical Override",
						"More options..");
				stage = 9;
				break;
			}
			break;
		case 9:
			switch (componentId) {
			case OPTION_1:
				if (!player.getOverrides().snowman) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setSnowmanOutfit();
				stage = 99;
				break;
			case OPTION_2:
				if (!player.getOverrides().samurai) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setSamuraiOutfit();
				stage = 99;
				break;
			case OPTION_3:
				if (!player.getOverrides().warmWinter) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setWarmWinterOutfit();
				stage = 99;
				break;
			case OPTION_4:
				if (!player.getOverrides().darkLord) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at Party Pete " + "in order to unlock it.");
					stage = 4;
					return;
				}
				player.getOverrides().setDarkLordOutfit();
				stage = 99;
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getOverrides().paladin ? Colors.green : Colors.red) + "Paladin Cosmetical Override",
						(player.getOverrides().warlord ? Colors.green : Colors.red) + "Warlord Cosmetical Override",
						(player.getOverrides().obsidian ? Colors.green : Colors.red) + "Obsidian Cosmetical Override",
						(player.getOverrides().kalphite ? Colors.green : Colors.red) + "Kalphite Cosmetical Override",
						"More options..");
				stage = 5;
				break;
			}
			break;
		case 10:
			sendDialogue("Xenia has removed your Cosmetic Overrides.");
			stage = 99;
			break;

		case 11:
			sendOptionsDialogue("Select an Option", "Cosmetic Overrides", "Animation Overrides");
			stage = 12;
			break;
		case 12:
			switch (componentId) {
			case OPTION_1:
				player.getDialogueManager().startDialogue("CosmeticsManagersD");
				
				break;
			case OPTION_2:
				sendPlayerDialogue(NORMAL, "I'd like to view all of your available Animation Overrides!");
				stage = 13;
				break;
			}
			break;
		case 13:
			sendOptionsDialogue("Select an Override to use",
					(player.getAnimations().hasBattleCry
							? Colors.green + "Slayer Battle Cry - unlocked - "
									+ (player.getAnimations().battleCry ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Slayer Battle Cry - LOCKED"),
					(player.getAnimations().hasEnhancedPotion
							? Colors.green + "Enhanced Potion Making - unlocked - "
									+ (player.getAnimations().enhancedPotion ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Enhanced Potion Making - LOCKED"),
					(player.getAnimations().hasLumberjackWc
							? Colors.green + "Lumberjack Woodcutting - unlocked - "
									+ (player.getAnimations().lumberjackWc ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Lumberjack Woodcutting - LOCKED"),
					(player.getAnimations().hasDeepFishing
							? Colors.green + "Deep-Sea Fishing - unlocked - "
									+ (player.getAnimations().deepFishing ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Deep-Sea Fishing - LOCKED"),
					"More options..");
			stage = 14;
			break;
		case 14:
			stage = 13;
			switch (componentId) {
			case OPTION_1:
				if (!player.getAnimations().hasBattleCry) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().battleCry = !player.getAnimations().battleCry;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().battleCry ? "enabled" : "disabled")
						+ " the 'Slayer Battle Cry' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_2:
				if (!player.getAnimations().hasEnhancedPotion) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().enhancedPotion = !player.getAnimations().enhancedPotion;
				sendNPCDialogue(npcId, NORMAL,
						"I've " + (player.getAnimations().enhancedPotion ? "enabled" : "disabled")
								+ " the 'Enhanced Potion Making' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_3:
				if (!player.getAnimations().hasLumberjackWc) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().lumberjackWc = !player.getAnimations().lumberjackWc;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().lumberjackWc ? "enabled" : "disabled")
						+ " the 'Lumberjack Woodcutting' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_4:
				if (!player.getAnimations().hasDeepFishing) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().deepFishing = !player.getAnimations().deepFishing;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().deepFishing ? "enabled" : "disabled")
						+ " the 'Deep-Sea Fishing' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getAnimations().hasZenResting
								? Colors.green + "Zen Resting - unlocked - "
										+ (player.getAnimations().zenResting ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Zen Resting - LOCKED"),
						(player.getAnimations().hasKarateFletch
								? Colors.green + "Karate-Chop Fletching - unlocked - "
										+ (player.getAnimations().karateFletch ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Karate-Chop Fletching - LOCKED"),
						(player.getAnimations().hasIronSmith
								? Colors.green + "Iron-Fist Smithing - unlocked - "
										+ (player.getAnimations().ironSmith ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Iron-Fist Smithing - LOCKED"),
						(player.getAnimations().hasChiMining
								? Colors.green + "Chi-Blast Mining - unlocked - "
										+ (player.getAnimations().chiMining ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Chi-Blast Mining - LOCKED"),
						"More options..");
				stage = 15;
				break;
			}
			break;
		case 15:
			stage = 90;
			switch (componentId) {
			case OPTION_1:
				if (!player.getAnimations().hasZenResting) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().zenResting = !player.getAnimations().zenResting;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().zenResting ? "enabled" : "disabled")
						+ " the 'Zen Resting' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_2:
				if (!player.getAnimations().hasKarateFletch) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().karateFletch = !player.getAnimations().karateFletch;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().karateFletch ? "enabled" : "disabled")
						+ " the 'Karate-Chop Fletching' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_3:
				if (!player.getAnimations().hasIronSmith) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().ironSmith = !player.getAnimations().ironSmith;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().ironSmith ? "enabled" : "disabled")
						+ " the 'Iron-Fist Smithing' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_4:
				if (!player.getAnimations().hasChiMining) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().chiMining = !player.getAnimations().chiMining;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().chiMining ? "enabled" : "disabled")
						+ " the 'Chi-Blast Mining' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getAnimations().hasSamuraiCook
								? Colors.green + "Samurai Cooking - unlocked - "
										+ (player.getAnimations().samuraiCook ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Samurai Cooking - LOCKED"),
						(player.getAnimations().hasRoundHouseWc
								? Colors.green + "Roundhouse Woodcutting - unlocked - "
										+ (player.getAnimations().roundHouseWc ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Roundhouse Woodcutting - LOCKED"),
						(player.getAnimations().hasBlastMining
								? Colors.green + "Blast Mining - unlocked - "
										+ (player.getAnimations().blastMining ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Blast Mining - LOCKED"),
						(player.getAnimations().hasStrongResting
								? Colors.green + "Strongarm Resting - unlocked - "
										+ (player.getAnimations().strongResting ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Strongarm Resting - LOCKED"),
						"More options..");
				stage = 16;
				break;
			}
			break;

		case 16:
			stage = 91;
			switch (componentId) {
			case OPTION_1:
				if (!player.getAnimations().hasSamuraiCook) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().samuraiCook = !player.getAnimations().samuraiCook;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().samuraiCook ? "enabled" : "disabled")
						+ " the 'Samurai Cooking' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_2:
				if (!player.getAnimations().hasRoundHouseWc) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().roundHouseWc = !player.getAnimations().roundHouseWc;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().roundHouseWc ? "enabled" : "disabled")
						+ " the 'Roundhouse Woodcutting' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_3:
				if (!player.getAnimations().hasBlastMining) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().blastMining = !player.getAnimations().blastMining;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().blastMining ? "enabled" : "disabled")
						+ " the 'Blast Mining' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_4:
				if (!player.getAnimations().hasStrongResting) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().strongResting = !player.getAnimations().strongResting;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().strongResting ? "enabled" : "disabled")
						+ " the 'Strongarm Resting' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getAnimations().hasArcaneSmelt
								? Colors.green + "Arcane Smelting - unlocked - "
										+ (player.getAnimations().arcaneSmelt ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Arcane Smelting - LOCKED"),
						(player.getAnimations().hasArcaneResting
								? Colors.green + "Arcane Resting - unlocked - "
										+ (player.getAnimations().arcaneResting ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Arcane Resting - LOCKED"),
						(player.getAnimations().hasStrongWc
								? Colors.green + "Strongarm Woodcutting - unlocked - "
										+ (player.getAnimations().strongWc ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Strongarm Woodcutting - LOCKED"),
						(player.getAnimations().hasStrongMining
								? Colors.green + "Strongarm Mining - unlocked - "
										+ (player.getAnimations().strongMining ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Strongarm Mining - LOCKED"),
						"More options..");
				stage = 17;
				break;
			}
			break;

		case 17:
			stage = 92;
			switch (componentId) {
			case OPTION_1:
				if (!player.getAnimations().hasArcaneSmelt) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().arcaneSmelt = !player.getAnimations().arcaneSmelt;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().arcaneSmelt ? "enabled" : "disabled")
						+ " the 'Arcane Smelting' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_2:
				if (!player.getAnimations().hasArcaneResting) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().arcaneResting = !player.getAnimations().arcaneResting;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().arcaneResting ? "enabled" : "disabled")
						+ " the 'Arcane Resting' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_3:
				if (!player.getAnimations().hasStrongWc) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().strongWc = !player.getAnimations().strongWc;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().strongWc ? "enabled" : "disabled")
						+ " the 'Strongarm Woodcutting' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_4:
				if (!player.getAnimations().hasStrongMining) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().strongMining = !player.getAnimations().strongMining;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().strongMining ? "enabled" : "disabled")
						+ " the 'Strongarm Mining' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getAnimations().hasArcaneFishing
								? Colors.green + "Arcane Fishing - unlocked - "
										+ (player.getAnimations().arcaneFishing ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Arcane Fishing - LOCKED"),
						(player.getAnimations().hasStrongBurial
								? Colors.green + "Strongarm Burial - unlocked - "
										+ (player.getAnimations().strongBurial ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Strongarm Burial - LOCKED"),
						(player.getAnimations().hasArcaneCook
								? Colors.green + "Arcane Cooking - unlocked - "
										+ (player.getAnimations().arcaneCook ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Arcane Cooking - LOCKED"),
						(player.getAnimations().hasPowerDivination
								? Colors.green + "Powerful Divination - unlocked - "
										+ (player.getAnimations().powerDivination ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Powerful Divination - LOCKED"),
						"More options..");
				stage = 18;
				break;
			}
			break;

		case 18:
			stage = 93;
			switch (componentId) {
			case OPTION_1:
				if (!player.getAnimations().hasArcaneFishing) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().arcaneFishing = !player.getAnimations().arcaneFishing;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().arcaneFishing ? "enabled" : "disabled")
						+ " the 'Arcane Fishing' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_2:
				if (!player.getAnimations().hasStrongBurial) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().strongBurial = !player.getAnimations().strongBurial;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().strongBurial ? "enabled" : "disabled")
						+ " the 'Strongarm Burial' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_3:
				if (!player.getAnimations().hasArcaneCook) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().arcaneCook = !player.getAnimations().arcaneCook;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().arcaneCook ? "enabled" : "disabled")
						+ " the 'Arcane Cooking' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_4:
				if (!player.getAnimations().hasPowerDivination) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().powerDivination = !player.getAnimations().powerDivination;
				sendNPCDialogue(npcId, NORMAL,
						"I've " + (player.getAnimations().powerDivination ? "enabled" : "disabled")
								+ " the 'Powerful Divination' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getAnimations().hasPowerConversion
								? Colors.green + "Powerful Conversion - unlocked - "
										+ (player.getAnimations().powerConversion ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Powerful Conversion - LOCKED"),
						(player.getAnimations().hasAgileDivination
								? Colors.green + "Agile Divination - unlocked - "
										+ (player.getAnimations().agileDivination ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Agile Divination - LOCKED"),
						(player.getAnimations().hasAgileConversion
								? Colors.green + "Agile Conversion - unlocked - "
										+ (player.getAnimations().agileConversion ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Agile Conversion - LOCKED"),
						(player.getAnimations().hasSinisterSlumber
								? Colors.green + "Sinister Slumber - unlocked - "
										+ (player.getAnimations().sinisterSlumber ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Sinister Slumber - LOCKED"),
						"More options..");
				stage = 19;
				break;
			}
			break;

		case 19:
			stage = 94;
			switch (componentId) {
			case OPTION_1:
				if (!player.getAnimations().hasPowerConversion) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().powerConversion = !player.getAnimations().powerConversion;
				sendNPCDialogue(npcId, NORMAL,
						"I've " + (player.getAnimations().powerConversion ? "enabled" : "disabled")
								+ " the 'Powerful Conversion' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_2:
				if (!player.getAnimations().hasAgileDivination) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().agileDivination = !player.getAnimations().agileDivination;
				sendNPCDialogue(npcId, NORMAL,
						"I've " + (player.getAnimations().agileDivination ? "enabled" : "disabled")
								+ " the 'Agile Divination' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_3:
				if (!player.getAnimations().hasAgileConversion) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().agileConversion = !player.getAnimations().agileConversion;
				sendNPCDialogue(npcId, NORMAL,
						"I've " + (player.getAnimations().agileConversion ? "enabled" : "disabled")
								+ " the 'Agile Conversion' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_4:
				if (!player.getAnimations().hasSinisterSlumber) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().sinisterSlumber = !player.getAnimations().sinisterSlumber;
				sendNPCDialogue(npcId, NORMAL,
						"I've " + (player.getAnimations().sinisterSlumber ? "enabled" : "disabled")
								+ " the 'Sinister Slumber' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getAnimations().hasArmWarrior
								? Colors.green + "Armchair Warrior - unlocked - "
										+ (player.getAnimations().armWarrior ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Armchair Warrior - LOCKED"),
						(player.getAnimations().hasEneResting
								? Colors.green + "Energy Drain Resting - unlocked - "
										+ (player.getAnimations().eneResting ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Energy Drain Resting - LOCKED"),
						(player.getAnimations().hasCrystalResting
								? Colors.green + "Crystal Impling Resting - unlocked - "
										+ (player.getAnimations().crystalResting ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Crystal Impling Resting - LOCKED"),
						(player.getAnimations().hasHeadMining
								? Colors.green + "Headbutt Mining - unlocked - "
										+ (player.getAnimations().headMining ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Headbutt Mining - LOCKED"),
						"More options..");
				stage = 20;
				break;
			}
			break;

		case 20:
			stage = 95;
			switch (componentId) {
			case OPTION_1:
				if (!player.getAnimations().hasArmWarrior) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().armWarrior = !player.getAnimations().armWarrior;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().armWarrior ? "enabled" : "disabled")
						+ " the 'Armchair Warrior' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_2:
				if (!player.getAnimations().hasEneResting) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().eneResting = !player.getAnimations().eneResting;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().eneResting ? "enabled" : "disabled")
						+ " the 'Energy Drain Resting' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_3:
				if (!player.getAnimations().hasCrystalResting) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().crystalResting = !player.getAnimations().crystalResting;
				sendNPCDialogue(npcId, NORMAL,
						"I've " + (player.getAnimations().crystalResting ? "enabled" : "disabled")
								+ " the 'Crystal Impling Resting' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_4:
				if (!player.getAnimations().hasHeadMining) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().headMining = !player.getAnimations().headMining;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().headMining ? "enabled" : "disabled")
						+ " the 'Headbutt Mining' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getAnimations().hasSandWalk
								? Colors.green + "Sandstorm Walk - unlocked - "
										+ (player.getAnimations().sandWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Sandstorm Walk - LOCKED"),
						(player.getAnimations().hasSadWalk
								? Colors.green + "Sad Walk - unlocked - "
										+ (player.getAnimations().sadWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Sad Walk - LOCKED"),
						(player.getAnimations().hasAngryWalk
								? Colors.green + "Angry Walk - unlocked - "
										+ (player.getAnimations().angryWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Angry Walk - LOCKED"),
						(player.getAnimations().hasProudWalk
								? Colors.green + "Proud Walk - unlocked - "
										+ (player.getAnimations().proudWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Proud Walk - LOCKED"),
						"More options..");
				stage = 21;
				break;
			}
			break;

		case 21:
			stage = 96;
			switch (componentId) {
			case OPTION_1:
				if (!player.getAnimations().hasSandWalk) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().sandWalk = !player.getAnimations().sandWalk;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().sandWalk ? "enabled" : "disabled")
						+ " the 'Sandstorm Walk' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_2:
				if (!player.getAnimations().hasSadWalk) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().sadWalk = !player.getAnimations().sadWalk;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().sadWalk ? "enabled" : "disabled")
						+ " the 'Sad Walk' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_3:
				if (!player.getAnimations().hasAngryWalk) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().angryWalk = !player.getAnimations().angryWalk;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().angryWalk ? "enabled" : "disabled")
						+ " the 'Angry Walk' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_4:
				if (!player.getAnimations().hasProudWalk) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().proudWalk = !player.getAnimations().proudWalk;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().proudWalk ? "enabled" : "disabled")
						+ " the 'Proud Walk' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_5:
				sendOptionsDialogue("Select an Override to use",
						(player.getAnimations().hasHappyWalk
								? Colors.green + "Happy Walk - unlocked - "
										+ (player.getAnimations().happyWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Happy Walk - LOCKED"),
						(player.getAnimations().hasBarbarianWalk
								? Colors.green + "Barbarian Walk - unlocked - "
										+ (player.getAnimations().barbarianWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Barbarian Walk - LOCKED"),
						(player.getAnimations().hasRevenantWalk
								? Colors.green + "Revenant Walk - unlocked - "
										+ (player.getAnimations().revenantWalk ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Revenant Walk - LOCKED"),
						"More options..");
				stage = 22;
				break;
			}
			break;
		case 22:
			stage = 97;
			switch (componentId) {
			case OPTION_1:
				if (!player.getAnimations().hasHappyWalk) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().happyWalk = !player.getAnimations().happyWalk;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().happyWalk ? "enabled" : "disabled")
						+ " the 'Happy Walk' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_2:
				if (!player.getAnimations().hasBarbarianWalk) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().barbarianWalk = !player.getAnimations().barbarianWalk;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().barbarianWalk ? "enabled" : "disabled")
						+ " the 'Barbarian Walk' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_3:
				if (!player.getAnimations().hasRevenantWalk) {
					sendNPCDialogue(npcId, SAD,
							"I'm sorry, but you will have to purchase this item at ;;store " + "in order to unlock it.");
					return;
				}
				player.getAnimations().revenantWalk = !player.getAnimations().revenantWalk;
				sendNPCDialogue(npcId, NORMAL, "I've " + (player.getAnimations().revenantWalk ? "enabled" : "disabled")
						+ " the 'Revenant Walk' Animation Override for you!");
				player.getGlobalPlayerUpdater().generateAppearenceData();
				break;
			case OPTION_4:
				sendOptionsDialogue("Select an Override to use",
						(player.getAnimations().hasBattleCry
								? Colors.green + "Slayer Battle Cry - unlocked - "
										+ (player.getAnimations().battleCry ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Slayer Battle Cry - LOCKED"),
						(player.getAnimations().hasEnhancedPotion
								? Colors.green + "Enhanced Potion Making - unlocked - "
										+ (player.getAnimations().enhancedPotion ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Enhanced Potion Making - LOCKED"),
						(player.getAnimations().hasLumberjackWc
								? Colors.green + "Lumberjack Woodcutting - unlocked - "
										+ (player.getAnimations().lumberjackWc ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Lumberjack Woodcutting - LOCKED"),
						(player.getAnimations().hasDeepFishing
								? Colors.green + "Deep-Sea Fishing - unlocked - "
										+ (player.getAnimations().deepFishing ? "enabled" : Colors.red + "disabled")
								: Colors.red + "Deep-Sea Fishing - LOCKED"),
						"More options..");
				stage = 14;
				break;
			}
			break;

		case 90:
			sendOptionsDialogue("Select an Override to use",
					(player.getAnimations().hasZenResting
							? Colors.green + "Zen Resting - unlocked - "
									+ (player.getAnimations().zenResting ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Zen Resting - LOCKED"),
					(player.getAnimations().hasKarateFletch
							? Colors.green + "Karate-Chop Fletching - unlocked - "
									+ (player.getAnimations().karateFletch ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Karate-Chop Fletching - LOCKED"),
					(player.getAnimations().hasIronSmith
							? Colors.green + "Iron-Fist Smithing - unlocked - "
									+ (player.getAnimations().ironSmith ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Iron-Fist Smithing - LOCKED"),
					(player.getAnimations().hasChiMining
							? Colors.green + "Chi-Blast Mining - unlocked - "
									+ (player.getAnimations().chiMining ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Chi-Blast Mining - LOCKED"),
					"More options..");
			stage = 15;
			break;
		case 91:
			sendOptionsDialogue("Select an Override to use",
					(player.getAnimations().hasSamuraiCook
							? Colors.green + "Samurai Cooking - unlocked - "
									+ (player.getAnimations().samuraiCook ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Samurai Cooking - LOCKED"),
					(player.getAnimations().hasRoundHouseWc
							? Colors.green + "Roundhouse Woodcutting - unlocked - "
									+ (player.getAnimations().roundHouseWc ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Roundhouse Woodcutting - LOCKED"),
					(player.getAnimations().hasBlastMining
							? Colors.green + "Blast Mining - unlocked - "
									+ (player.getAnimations().blastMining ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Blast Mining - LOCKED"),
					(player.getAnimations().hasStrongResting
							? Colors.green + "Strongarm Resting - unlocked - "
									+ (player.getAnimations().strongResting ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Strongarm Resting - LOCKED"),
					"More options..");
			stage = 16;
			break;
		case 92:
			sendOptionsDialogue("Select an Override to use",
					(player.getAnimations().hasArcaneSmelt
							? Colors.green + "Arcane Smelting - unlocked - "
									+ (player.getAnimations().arcaneSmelt ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Arcane Smelting - LOCKED"),
					(player.getAnimations().hasArcaneResting
							? Colors.green + "Arcane Resting - unlocked - "
									+ (player.getAnimations().arcaneResting ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Arcane Resting - LOCKED"),
					(player.getAnimations().hasStrongWc
							? Colors.green + "Strongarm Woodcutting - unlocked - "
									+ (player.getAnimations().strongWc ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Strongarm Woodcutting - LOCKED"),
					(player.getAnimations().hasStrongMining
							? Colors.green + "Strongarm Mining - unlocked - "
									+ (player.getAnimations().strongMining ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Strongarm Mining - LOCKED"),
					"More options..");
			stage = 17;
			break;
		case 93:
			sendOptionsDialogue("Select an Override to use",
					(player.getAnimations().hasArcaneFishing
							? Colors.green + "Arcane Fishing - unlocked - "
									+ (player.getAnimations().arcaneFishing ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Arcane Fishing - LOCKED"),
					(player.getAnimations().hasStrongBurial
							? Colors.green + "Strongarm Burial - unlocked - "
									+ (player.getAnimations().strongBurial ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Strongarm Burial - LOCKED"),
					(player.getAnimations().hasArcaneCook
							? Colors.green + "Arcane Cooking - unlocked - "
									+ (player.getAnimations().arcaneCook ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Arcane Cooking - LOCKED"),
					(player.getAnimations().hasPowerDivination
							? Colors.green + "Powerful Divination - unlocked - "
									+ (player.getAnimations().powerDivination ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Powerful Divination - LOCKED"),
					"More options..");
			stage = 18;
			break;
		case 94:
			sendOptionsDialogue("Select an Override to use",
					(player.getAnimations().hasPowerConversion
							? Colors.green + "Powerful Conversion - unlocked - "
									+ (player.getAnimations().powerConversion ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Powerful Conversion - LOCKED"),
					(player.getAnimations().hasAgileDivination
							? Colors.green + "Agile Divination - unlocked - "
									+ (player.getAnimations().agileDivination ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Agile Divination - LOCKED"),
					(player.getAnimations().hasAgileConversion
							? Colors.green + "Agile Conversion - unlocked - "
									+ (player.getAnimations().agileConversion ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Agile Conversion - LOCKED"),
					(player.getAnimations().hasSinisterSlumber
							? Colors.green + "Sinister Slumber - unlocked - "
									+ (player.getAnimations().sinisterSlumber ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Sinister Slumber - LOCKED"),
					"More options..");
			stage = 19;
			break;
		case 95:
			sendOptionsDialogue("Select an Override to use",
					(player.getAnimations().hasArmWarrior
							? Colors.green + "Armchair Warrior - unlocked - "
									+ (player.getAnimations().armWarrior ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Armchair Warrior - LOCKED"),
					(player.getAnimations().hasEneResting
							? Colors.green + "Energy Drain Resting - unlocked - "
									+ (player.getAnimations().eneResting ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Energy Drain Resting - LOCKED"),
					(player.getAnimations().hasCrystalResting
							? Colors.green + "Crystal Impling Resting - unlocked - "
									+ (player.getAnimations().crystalResting ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Crystal Impling Resting - LOCKED"),
					(player.getAnimations().hasHeadMining
							? Colors.green + "Headbutt Mining - unlocked - "
									+ (player.getAnimations().headMining ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Headbutt Mining - LOCKED"),
					"More options..");
			stage = 20;
			break;
		case 96:
			sendOptionsDialogue("Select an Override to use",
					(player.getAnimations().hasSandWalk
							? Colors.green + "Sandstorm Walk - unlocked - "
									+ (player.getAnimations().sandWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Sandstorm Walk - LOCKED"),
					(player.getAnimations().hasSadWalk
							? Colors.green + "Sad Walk - unlocked - "
									+ (player.getAnimations().sadWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Sad Walk - LOCKED"),
					(player.getAnimations().hasAngryWalk
							? Colors.green + "Angry Walk - unlocked - "
									+ (player.getAnimations().angryWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Angry Walk - LOCKED"),
					(player.getAnimations().hasProudWalk
							? Colors.green + "Proud Walk - unlocked - "
									+ (player.getAnimations().proudWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Proud Walk - LOCKED"),
					"More options..");
			stage = 21;
			break;
		case 97:
			sendOptionsDialogue("Select an Override to use",
					(player.getAnimations().hasHappyWalk
							? Colors.green + "Happy Walk - unlocked - "
									+ (player.getAnimations().happyWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Happy Walk - LOCKED"),
					(player.getAnimations().hasBarbarianWalk
							? Colors.green + "Barbarian Walk - unlocked - "
									+ (player.getAnimations().barbarianWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Barbarian Walk - LOCKED"),
					(player.getAnimations().hasRevenantWalk
							? Colors.green + "Revenant Walk - unlocked - "
									+ (player.getAnimations().revenantWalk ? "enabled" : Colors.red + "disabled")
							: Colors.red + "Revenant Walk - LOCKED"),
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
	}

}