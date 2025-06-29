package com.rs.game.player.dialogue.impl;

import com.discord.Discord;
import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Equipment;
import com.rs.game.player.MembershipHandler;
import com.rs.game.player.Player;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.StarterMap;

/**
 * Handles the Starter tutorial for new players.
 *
 * @ausky Noel
 */
public class StarterTutorialD extends Dialogue {

	/**
	 * Teleports the player.
	 *
	 * @param player The player.
	 */
	public static void teleport(final Player player) {
		player.setNextFaceWorldTile(new WorldTile(3168, 3155, 0));
		player.setNextWorldTile(Settings.START_PLAYER_LOCATION);
		player.lock(5);
		WorldTasksManager.schedule(new WorldTask() {
			int tick;

			@Override
			public void run() {
				tick++;
				if (tick == 1) {
					player.setNextWorldTile(Settings.START_PLAYER_LOCATION);
					player.setNextAnimation(new Animation(-1));
					player.getDialogueManager().startDialogue("StarterTutorialD");
					player.setLogedIn();
					player.lock();
					stop();
				}
			}
		}, 0, 1);
	}

	/**
	 * The starter area square coords.
	 *
	 * @param tile   The tiles.
	 * @param player The player.
	 * @return if Inside square.
	 */
	private static boolean starterArea(WorldTile tile, Player player) {
		int destX = player.getX();
		int destY = player.getY();
		return (destX >= 2861 && destY >= 5074 && destX <= 2863 && destY <= 5076);
	}

	/**
	 * Checks the starter area.
	 *
	 * @param player The player to check.
	 * @return if In area.
	 */
	public static boolean checkStarterArea(Player player) {
		if (!player.hasCompleted()) {
			if (!starterArea(player, player)) {
				player.setNextWorldTile(Settings.START_PLAYER_LOCATION);
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds hint icon to the players map.
	 *
	 * @param player The player.
	 */
	public static void addNPCHintIcon(Player player) {
		NPC guide = World.findNPC(player, 25273);
		if (guide != null) {
			player.getHintIconsManager().addHintIcon(guide, 0, -1, false);
			guide.faceEntity(player);
		}
	}

	@Override
	public void start() {
		if (player.hasCompleted()) {
			sendNPCDialogue(25273, SAD, "These shoes will kill me...");
			Item shardBag = new Item(33262);
			if (!player.hasItem(shardBag))
				player.addItem(shardBag);
			// TODO add the actual town crier dialogue.
			stage = 99;
			return;
		}
		// sendDialogue(25273, NORMAL, "Hello, and Welcome to " + Settings.SERVER_NAME +
		// "!");
		sendDialogue("Hello, and Welcome to " + Settings.SERVER_NAME + "!");
		player.lock();
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("Choose your Gamemode:",
					"Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>" + Settings.EXPERT_DROP
							+ "</col> drop rate)",
					"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
							+ "</col> drop rate)",
					"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
							+ Settings.INTERM_DROP + "</col> drop rate)",
					"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
							+ "</col> drop rate)",
					"<img=14>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
							+ Settings.IRONMAN_DROP + "</col> drop rate)");
			stage = 0;
			break;
		case 0:
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Choose the <col=ff0000>Expert</col> gamemode?", "Yes", "No");
				stage = 50;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose the <col=ff0000>Veteran</col> gamemode?", "Yes", "No");
				stage = 1;
			}
			if (componentId == OPTION_3) {
				sendOptionsDialogue("Choose the <col=ff0000>Intermediate</col> gamemode?", "Yes", "No");
				stage = 10;
			}
			if (componentId == OPTION_4) {
				sendOptionsDialogue("Choose the <col=ff0000>Easy</col> gamemode?", "Yes", "No");
				stage = 20;
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Which Ironman mode would you like to play on?",
						"<img=14>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)",
						"<img=15>HC IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.HCIRONMAN_DROP + "</col> drop rate)" + Colors.red,
						"Back to previous options..");
				stage = 2;
			}
			break;
		case 2:
			if (componentId == OPTION_1) {
				sendOptionsDialogue("Choose the <col=ff0000>Ironman</col> gamemode?", "Yes", "No");
				stage = 30;
			}
			if (componentId == OPTION_2) {
				sendNPCDialogue(6139, NORMAL, Colors.red
						+ "Note: If HC IronMan died even on non PvP area your stats will reset and degrade to Regular IronMan");

				stage = 60;
			}
			if (componentId == OPTION_3) {
				sendOptionsDialogue("Choose your Gamemode:",
						"Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=14>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 1:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(2500000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Veteran</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.VET_XP + "</col> and a Drop Rate of x<col=ff0000>" + Settings.VET_DROP
								+ "</col>.");
				player.setVeteran(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=14>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 10:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(2500000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Intermediate</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.INTERM_XP + "</col> and a Drop Rate of x<col=ff0000>" + Settings.INTERM_DROP
								+ "</col>.");
				player.setIntermediate(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=14>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 20:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(2500000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Easy</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.EASY_XP + "</col> and a Drop Rate of x<col=ff0000>" + Settings.EASY_DROP
								+ "</col>.");
				player.setEasy(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=14>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 30:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(2500000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Ironman</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.IRONMAN_XP + "</col> and a Drop Rate of x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col>.");
				player.setIronMan(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=14>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 40:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(2500000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Hardcore Ironman</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.IRONMAN_XP + "</col> and a Drop Rate of x<col=ff0000>"
								+ Settings.HCIRONMAN_DROP + "</col>.");
				player.setHCIronMan(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=14>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 50:
			if (componentId == OPTION_1) {
				if (canAddReward())
					player.addMoney(100000);
				sendNPCDialogue(6139, NORMAL,
						"You've chosen the <col=ff0000>Expert</col> Game Mode! You now have an EXP multiplier of x<col=ff0000>"
								+ Settings.EXPERT_XP + "</col> and a Drop Rate of x<col=ff0000>" + Settings.EXPERT_DROP
								+ "</col>.");
				player.setExpert(true);
				completeTutorial();
				stage = 99;
			}
			if (componentId == OPTION_2) {
				sendOptionsDialogue("Choose your Gamemode:",
						"Expert (x<col=ff0000>" + Settings.EXPERT_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.EXPERT_DROP + "</col> drop rate)",
						"Veteran (x<col=ff0000>" + Settings.VET_XP + "</col> EXP & x<col=ff0000>" + Settings.VET_DROP
								+ "</col> drop rate)",
						"Intermediate (x<col=ff0000>" + Settings.INTERM_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.INTERM_DROP + "</col> drop rate)",
						"Easy (x<col=ff0000>" + Settings.EASY_XP + "</col> EXP & x<col=ff0000>" + Settings.EASY_DROP
								+ "</col> drop rate)",
						"<img=14>IronMan (x<col=ff0000>" + Settings.IRONMAN_XP + "</col> EXP & x<col=ff0000>"
								+ Settings.IRONMAN_DROP + "</col> drop rate)");
				stage = 0;
			}
			break;
		case 60:
			sendNPCDialogue(6139, NORMAL, Colors.red + "Except if your total level is below " + Colors.green
					+ "500</col>" + Colors.red + " and if you're a </col>" + Colors.green + "Donator.");
			stage = 61;
			break;
		case 61:
			sendOptionsDialogue("Choose the <col=ff0000>Hardcore Ironman</col> gamemode? <br>", "Yes", "No");
			stage = 40;
			break;
		case 99:
			player.getInterfaceManager().sendHelpInterface();
			player.getInterfaceManager().closeChatBoxInterface();
			end();

			break;
		}
	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}

	/**
	 * Completes tutorial - hands out rewards.
	 */
	public void completeTutorial() {
		player.getHintIconsManager().removeUnsavedHintIcon();
		Dialogue.closeNoContinueDialogue(player);
		player.setCompleted();
		player.setDoubleXpTimer(12000);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		player.getInterfaceManager().sendTaskSystem();
		player.getInterfaceManager().openGameTab(1);
		player.unlock();
		Magic.vineTeleport(player, new WorldTile(991, 4118, 0));
		if (!canAddReward()) {
			player.sendMessage(
					Colors.red + "You did not receive your starter kit, you've already received it 2 times.");
			return;
		}
		player.getEquipment().set(Equipment.SLOT_HAT, new Item(42320, 1));
		player.getEquipment().set(Equipment.SLOT_CHEST, new Item(42321, 1));
		player.getEquipment().set(Equipment.SLOT_LEGS, new Item(42322, 1));
		player.getEquipment().set(Equipment.SLOT_AMULET, new Item(1712, 1));
		player.getEquipment().set(Equipment.SLOT_FEET, new Item(42323, 1));
		player.getEquipment().set(Equipment.SLOT_HANDS, new Item(42324, 1));
		player.getEquipment().set(Equipment.SLOT_RING, new Item(2552, 1));
		player.getEquipment().set(Equipment.SLOT_CAPE, new Item(42325, 1));
		player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(882, 100));
		player.getEquipment().set(Equipment.SLOT_AURA, new Item(22302));
		player.getEquipment().refresh(Equipment.SLOT_HAT, Equipment.SLOT_CHEST, Equipment.SLOT_LEGS,
				Equipment.SLOT_AMULET, Equipment.SLOT_FEET, Equipment.SLOT_HANDS, Equipment.SLOT_AMULET,
				Equipment.SLOT_RING, Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS, Equipment.SLOT_AURA);
		player.getInventory().addItem(new Item(42944, 1));
		player.getInventory().addItem(new Item(42945, 1));
		player.getInventory().addItem(new Item(42946, 1));
		player.getInventory().addItem(new Item(11814, 1));
		player.getInventory().addItem(new Item(1129, 1));
		player.getInventory().addItem(new Item(1095, 1));
		player.getInventory().addItem(new Item(577, 1));
		player.getInventory().addItem(new Item(1011, 1));
		player.getInventory().addItem(new Item(8013, 15));
		player.getInventory().addItem(new Item(554, 200));
		player.getInventory().addItem(new Item(558, 200));
		player.getInventory().addItem(new Item(555, 200));
		player.getInventory().addItem(new Item(386, 100));
		player.getInventory().addItem(new Item(2677, 1));
		player.getInventory().addItem(new Item(4155, 1));
		player.getEquipment().refreshConfigs(false);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		player.heal(player.getMaxHitpoints());
		Magic.vineTeleport(player, new WorldTile(991, 4118, 0));
		// Magic.sendNormalTeleportSpell(player, 0, 0, player.getHomeTile());

		player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_FEET, Equipment.SLOT_HANDS,
				Equipment.SLOT_HAT, Equipment.SLOT_CHEST, Equipment.SLOT_LEGS, Equipment.SLOT_CAPE,
				Equipment.SLOT_WEAPON, Equipment.SLOT_SHIELD);

		player.getEquipment().refreshConfigs(false);
		World.sendWorldMessage(Colors.red + "<img=7>News:</col> Welcome: [" + player.getDisplayName() + "] - "
				+ "mode: [" + player.getXPMode() + "] - to " + Settings.SERVER_NAME + "!", false);

		StarterMap.getSingleton().addIP(player.getSession().getIP());
		player.getGlobalPlayerUpdater().generateAppearenceData();
		player.getMembership();
		MembershipHandler.completepack(player, true);
		player.sendMessage("You've Activated your free: [" + Colors.red + "Complete perk pack membership</col>]. "
				+ "Free 1 Week Complete Perk Package. Type ;;perks to see all your game perks.");
		Discord.sendNewmembers("Cheers to " + player.getUsername() + ", now a part of Helwyr in " + player.getXPMode()
				+ " mode! Welcome aboard!");
	}

	/**
	 * If the player's eligible for item reward.
	 *
	 * @return If eligible.
	 */
	public boolean canAddReward() {
		int count = StarterMap.getSingleton().getCount(player.getSession().getIP());
		if (count > Settings.MAX_STARTER_COUNT)
			return false;
		return true;
	}
}