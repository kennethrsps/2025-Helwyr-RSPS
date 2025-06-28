package com.rs.game.player.content;

import com.rs.Settings;
import com.rs.game.player.content.InterfaceManager;
import com.rs.game.player.controllers.NomadsRequiem;
import com.rs.game.player.controllers.RunespanController;
import com.rs.utils.Colors;
import com.rs.utils.Utils;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.QuestManager.Quests;

/**
 * Handles everything related to the Minigame Teleports Interface.
 */
public class MinigameTeleports {

	/**
	 * Sends the actual interface with all available options.
	 * 
	 * @param player The player to send the interface to.
	 */
	public static void sendInterface(Player player) {
		player.getInterfaceManager().closeChatBoxInterface();
		player.getInterfaceManager().sendInterface(813);
		player.getPackets().sendItemOnIComponent(813, 33, 30005, 1);
		player.getPackets().sendIComponentText(813, 80, Colors.green + "HELWYR"); // Title
		player.getPackets().sendIComponentText(813, 15, "Where would you like to go, " + player.getDisplayName() + "?"); // Desc
		player.getPackets().sendIComponentText(813, 29, Colors.green + "Minigames I"); // Boss Teleports
		player.getPackets().sendIComponentText(813, 52, Colors.white + "Barrows Mastery" + Colors.yellow + "New");
		player.getPackets().sendIComponentText(813, 53, Colors.white + "Clan Wars");
		player.getPackets().sendIComponentText(813, 54, Colors.white + "Pest Control");
		player.getPackets().sendIComponentText(813, 55, Colors.white + "Fight kiln");
		player.getPackets().sendIComponentText(813, 56, Colors.white + "Fight Caves");
		player.getPackets().sendIComponentText(813, 60, Colors.white + "Recipe for Disaster");
		player.getPackets().sendIComponentText(813, 57, Colors.white + "Duel Arena");
		player.getPackets().sendIComponentText(813, 58, Colors.white + "Warrios Guild");
		player.getPackets().sendIComponentText(813, 59, Colors.white + "Soul Wars");
		player.getPackets().sendIComponentText(813, 61, Colors.white + "Dominion Tower");

		player.getPackets().sendIComponentText(813, 30, Colors.green + "Minigames II");
		player.getPackets().sendIComponentText(813, 43, Colors.white + "Nomad's Requiem");
		player.getPackets().sendIComponentText(813, 44, Colors.white + "New Dungeoneering");
		player.getPackets().sendIComponentText(813, 45, Colors.white + "Old Dungeoneering");
		player.getPackets().sendIComponentText(813, 46, Colors.white + "Livid Farm");
		player.getPackets().sendIComponentText(813, 47, Colors.white + "Artisan's Workshop");
		player.getPackets().sendIComponentText(813, 48, Colors.white + "Runespan");
		player.getPackets().sendIComponentText(813, 49, Colors.white + "Puro-Puro");
		player.getPackets().sendIComponentText(813, 50, Colors.white + "Tusken invasion");
		player.getPackets().sendIComponentText(813, 51, Colors.white + "");

		player.getPackets().sendIComponentText(813, 31, Colors.green + "");
		player.getPackets().sendIComponentText(813, 37, Colors.white + "");
		player.getPackets().sendIComponentText(813, 38, Colors.white + "");
		player.getPackets().sendIComponentText(813, 39, Colors.white + "");
		player.getPackets().sendIComponentText(813, 40, Colors.white + "");
		player.getPackets().sendIComponentText(813, 41, Colors.white + "");
		player.getPackets().sendIComponentText(813, 42, Colors.white + "");

		player.getPackets().sendIComponentText(813, 32, Colors.green + "");
		player.getPackets().sendIComponentText(813, 64, Colors.white + "");
		player.getPackets().sendIComponentText(813, 65, Colors.white + "");
		player.getPackets().sendIComponentText(813, 66, Colors.white + "");
		player.getPackets().sendIComponentText(813, 67, Colors.white + "");
		player.getPackets().sendIComponentText(813, 68, Colors.white + "");
		player.getPackets().sendIComponentText(813, 69, Colors.white + "");
		player.getPackets().sendIComponentText(813, 70, Colors.white + "");
		player.getPackets().sendIComponentText(813, 71, Colors.white + "");

		player.getPackets().sendIComponentText(813, 36, Colors.red + "Information");
		player.getPackets().sendIComponentText(813, 99,
				Colors.red + "Combat Lvl: " + player.getSkills().getCombatLevelWithSummoning() + "");
		player.getPackets().sendIComponentText(813, 101,
				Colors.red + "PvM Pts:" + Utils.formatNumber(player.getPVMPoints()) + " ");
		player.getPackets().sendIComponentText(813, 103, Colors.red + "DToken: " + player.dungTokens + "");
		player.getPackets().sendIComponentText(813, 105, Colors.red + "");
		player.getPackets().sendIComponentText(813, 107, Colors.red + "");

	}

	/**
	 * Handles the actual interfaces buttons.
	 * 
	 * @param player      The players interface to handle.
	 * @param componentId The players interface pressed component ID.
	 */
	public static void handleInterface(Player player, int componentId) {
		InterfaceManager.setPlayerInterfaceSelected(2);
		player.getInterfaceManager().closeChatBoxInterface();
		if (componentId == 52)
			Magic.vineTeleport(player, new WorldTile(3542, 3307, 0)); //barrows

		if (componentId == 53)
			Magic.vineTeleport(player, new WorldTile(2994, 9679, 0)); // clanwars

		if (componentId == 54)
			Magic.vineTeleport(player, new WorldTile(2663, 2653, 0)); // pest control

		if (componentId == 112)
			Magic.vineTeleport(player, new WorldTile(3972, 5562, 0)); // dungeoneering

		if (componentId == 55)
			Magic.vineTeleport(player, new WorldTile(4743, 5170, 0)); // fight kiln

		if (componentId == 56)
			Magic.vineTeleport(player, new WorldTile(4613, 5129, 0)); // fight caves

		if (componentId == 60)
			Magic.vineTeleport(player, new WorldTile(1866, 5346, 0)); // reciepe for dist

		if (componentId == 57)
			Magic.vineTeleport(player, new WorldTile(3325, 3232, 0)); // duel arena

		if (componentId == 58)
			Magic.vineTeleport(player, new WorldTile(2879, 3542, 0)); // wguild

		if (componentId == 59)
			Magic.vineTeleport(player, new WorldTile(3081, 3475, 0)); // soulwars

		if (componentId == 61)
			Magic.vineTeleport(player, new WorldTile(3367, 3083, 0)); // dom tower
		if (componentId == 43)
			if (!player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM)) {
				NomadsRequiem.enterNomadsRequiem(player);
				player.getDialogueManager().finishDialogue();
			} else {
				player.sendMessage(Colors.red + "You have already completed Nomad's Quest!");
				player.getDialogueManager().finishDialogue();
				// player.sendMessage("Nomad's Requiem is currently
				// disabled, sorry.");
			}
		if (componentId == 44) // new dung
			Magic.vineTeleport(player, new WorldTile(3972, 5561, 0));
		if (componentId == 45) // old dung
			Magic.vineTeleport(player, new WorldTile(3449, 3727, 0));
		if (componentId == 46) // livid farm
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2111, 3937, 0));
		if (componentId == 47) // artisans
			Magic.vineTeleport(player, new WorldTile(3032, 3338, 0));
		if (componentId == 48) // runespan
			RunespanController.enterRunespan(player);
		if (componentId == 49) { // puro puro
			if (player.isAtWild()) {
				player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
				return;
			}
			player.getControlerManager().startControler("PuroPuro");
		}
		if (componentId == 50) {// Tusken
			if (player.isAtWild()) {
				player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
				return;
			}
			player.getControlerManager().startControler("tuskenraid");
		}

	}

}