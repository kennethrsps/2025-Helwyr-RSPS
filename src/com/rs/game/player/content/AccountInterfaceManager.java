package com.rs.game.player.content;

import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles everything related to the Account Manager.
 * 
 * @author Zeus.
 */
public class AccountInterfaceManager {
	public static WorldTile[] spawns = { new WorldTile(4315, 866, 0), // sandy box
			new WorldTile(2332, 3172, 0), // ashdale
			// new WorldTile(3607, 3365, 0), // sanguine

			new WorldTile(3234, 2729, 0), // sandy castle default
			new WorldTile(990, 4122, 0), // Gold Home

	};

	/**
	 * Sends the actual interface with all available options.
	 * 
	 * @param player
	 *            The player to send the interface to.
	 */
	public static void sendInterface(Player player) {
		player.getInterfaceManager().sendInterface(1157);
		player.getPackets().sendIComponentText(1157, 92,
				"<col=FFFF00>" + player.getDisplayName() + "'s Helwyr Settings");
		player.getPackets().sendIComponentText(1157, 95, ""); // bottom line, under all scrollables
		player.getPackets().sendIComponentText(1157, 33, "Setting"); // table name
		player.getPackets().sendIComponentText(1157, 34, "Toggle"); // table name

		player.getPackets().sendIComponentText(1157, 46, "");
		player.getPackets().sendIComponentText(1157, 47, "Appearence");
		player.getPackets().sendIComponentText(1157, 48, "Press to customize");

		player.getPackets().sendIComponentText(1157, 49, "");
		player.getPackets().sendIComponentText(1157, 50, "Spawn Location");
		player.getPackets().sendIComponentText(1157, 51, Colors.rcyan + Colors.shad + player.getHomeName() + "</col>");

		player.getPackets().sendIComponentText(1157, 52, "");
		player.getPackets().sendIComponentText(1157, 53, "Loyalty titles");
		player.getPackets().sendIComponentText(1157, 54, "Press to customize");

		player.getPackets().sendIComponentText(1157, 55, "");
		player.getPackets().sendIComponentText(1157, 56, "Loot Beam");
		player.getPackets().sendIComponentText(1157, 57,
				"Trigger price: " + Colors.green + Utils.getFormattedNumber(player.setLootBeam) + ".</col>");

		player.getPackets().sendIComponentText(1157, 58, "");
		player.getPackets().sendIComponentText(1157, 59, "World Messages");
		player.getPackets().sendIComponentText(1157, 60,
				(player.isHidingWorldMessages() ? Colors.red + "Disabled" : Colors.green + "Enabled")
						+ "</col>. Press to customize");

		player.getPackets().sendIComponentText(1157, 61, "");
		player.getPackets().sendIComponentText(1157, 62, "Yell Messages");
		player.getPackets().sendIComponentText(1157, 63,
				(player.isYellOff() ? Colors.red + "Disabled" : Colors.green + "Enabled")
						+ "</col>. Press to customize");

		player.getPackets().sendIComponentText(1157, 64, "");
		player.getPackets().sendIComponentText(1157, 65, "MacLock");
		player.getPackets().sendIComponentText(1157, 66,
				(player.iplocked ? Colors.green + "Enabled to : " + player.lockedwith
						: Colors.red + "Disabled" + "</col>.") + " Press to customize");

		player.getPackets().sendIComponentText(1157, 67, "");
		player.getPackets().sendIComponentText(1157, 68, "MoneyPouch Messages");
		player.getPackets().sendIComponentText(1157, 69, "Press to customize");
		player.getPackets().sendIComponentText(1157, 70, "");
		player.getPackets().sendIComponentText(1157, 71, "Skilling Interface");
		player.getPackets().sendIComponentText(1157, 72, "Toggle Interface");

		int[] componentIds = { 73, 74, 75, 76 };
		// let's clear everything else
		for (int id : componentIds)
			player.getPackets().sendIComponentText(1157, id, "");
	}

	/**
	 * Handles the actual interfaces buttons.
	 * 
	 * @param player
	 *            The players interface to handle.
	 * @param componentId
	 *            The players interface pressed component ID.
	 */
	public static void handleInterface(Player player, int componentId) {
		player.getInterfaceManager().closeChatBoxInterface();
		if (componentId == 0)
			player.getDialogueManager().startDialogue("PlayerSettings");
		if (componentId == 1) {
			player.getInterfaceManager().closeScreenInterface();
			player.getDialogueManager().startDialogue(new Dialogue() {

				@Override
				public void start() {
					sendOptionsDialogue(Colors.rcyan + "Where would you like to set your home?",
							Colors.green + "Sandy Box", /* Colors.GREEN + "Sanguine District", */
							Colors.green + "Ashdale", Colors.green + "Sand Castle ",
							Colors.green + "Gold Mine (default)", "Nevermind");
					stage = 1;
				}

				@Override
				public void run(int interfaceId, int componentId) {
					switch (stage) {
					case 1:
						end();
						if (componentId == OPTION_5)
							break;
						switch (componentId) {
						case OPTION_1:
							player.setHome(spawns[0], "Sandy Box");
							player.sendMessage(
									Colors.green + Colors.shad + "Your respawn location has been set to Sandy Box!",
									true);
							break;
						/*
						 * case OPTION_2: player.setHome(spawns[1], "Sanguine District");
						 * player.sendMessage(Colors.GREEN + Colors.SHAD +
						 * "Your respawn location has been set to the Sanguine District!", true); break;
						 */
						case OPTION_2:

							player.setHome(spawns[1], "Ashdale");
							player.sendMessage(
									Colors.green + Colors.shad + "Your respawn location has been set to Ashalde!",
									true);

						
						case OPTION_3:
							player.setHome(spawns[2], "Sandy Castle");
							player.sendMessage(
									Colors.green + Colors.shad + "Your respawn location has been set to Sandy Castle!",
									true);
						case OPTION_4:
							player.setHome(spawns[3], "Gold Mine");
							player.sendMessage(
									Colors.green + Colors.shad + "Your respawn location has been set to Gold Mine!",
									true);
						}
						break;
					}
				}

				@Override
				public void finish() {
					player.getInterfaceManager().closeChatBoxInterface();
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							AccountInterfaceManager.sendInterface(player);
						}
					}, 1);
				}

			});
		}

		if (componentId == 2)
			player.getTitles().openShop();

		if (componentId == 3)
			player.getDialogueManager().startDialogue("LootBeamManagerD");

		if (componentId == 4) {
			if (!player.isHidingWorldMessages()) {
				player.setHideWorldMessages(player.isHidingWorldMessages() ? false : true);
				AccountInterfaceManager.sendInterface(player);
				player.sendMessage("You have turned off World Messages");
				return;
			}
			player.setHideWorldMessages(player.isHidingWorldMessages() ? false : true);
			AccountInterfaceManager.sendInterface(player);
			player.sendMessage("You have turned on World Messages");
		}
		if (componentId == 5) {
			if (!player.isYellOff()) {
				player.setYellOff(player.isYellOff() ? false : true);
				AccountInterfaceManager.sendInterface(player);
				player.sendMessage("You have turned off Yell Messages");
				return;
			}
			player.setYellOff(player.isYellOff() ? false : true);
			AccountInterfaceManager.sendInterface(player);
			player.sendMessage("You have turned on Yell Messages");
		}
		if (componentId == 6) {
			player.getDialogueManager().startDialogue("setIplock");
		}
		if (componentId == 7) {
			player.togglePouchMessages = !player.togglePouchMessages;
			player.succeedMessage(Colors.red + "Money pouch messages: " + Colors.green + player.togglePouchMessages);
		}

	}
}