package com.rs.game.player.content;

import com.rs.Settings;
import com.rs.game.player.content.InterfaceManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;

/**
 * Handles everything related to the PvP Teleports Interface.
 */
public class PvPTeleports {
	

	/**
	 * Sends the actual interface with all available options.
	 * @param player The player to send the interface to.
	 */
	public static void sendInterface(Player player) {
		player.getInterfaceManager().closeChatBoxInterface();
		player.getInterfaceManager().sendInterface(813);
		player.getPackets().sendItemOnIComponent(813, 33, 30005, 1);
		player.getPackets().sendIComponentText(813, 80, Colors.green + "HELWYR"); // Title
		player.getPackets().sendIComponentText(813, 15, "Where would you like to go, " + player.getDisplayName() + "?"); // Desc
		player.getPackets().sendIComponentText(813, 29, Colors.green + "PvP I"); // Boss Teleports
		player.getPackets().sendIComponentText(813, 52, Colors.white + "East Dragons");
		player.getPackets().sendIComponentText(813, 53, Colors.white + "Forinthry Dungeon");
		player.getPackets().sendIComponentText(813, 54, Colors.white + "Agility Course" +Colors.red+" (Lvl 50 Wild)</col>");
		player.getPackets().sendIComponentText(813, 55, Colors.white + "Mage Bank");
		player.getPackets().sendIComponentText(813, 56, Colors.white + "New Gates" +Colors.red+" (Lvl 47 Wild)</col>");
		player.getPackets().sendIComponentText(813, 60, Colors.white + "");
		player.getPackets().sendIComponentText(813, 57, Colors.white + "");
		player.getPackets().sendIComponentText(813, 58, Colors.white + "");
		player.getPackets().sendIComponentText(813, 59,	Colors.white + "");
		player.getPackets().sendIComponentText(813, 61, Colors.white + "");

		player.getPackets().sendIComponentText(813, 30, Colors.green + "");
		player.getPackets().sendIComponentText(813, 43, Colors.white + "");
		player.getPackets().sendIComponentText(813, 44, Colors.white + "");
		player.getPackets().sendIComponentText(813, 45, Colors.white + "");
		player.getPackets().sendIComponentText(813, 46, Colors.white + "");
		player.getPackets().sendIComponentText(813, 47, Colors.white + "");
		player.getPackets().sendIComponentText(813, 48, Colors.white + "");
		player.getPackets().sendIComponentText(813, 49, Colors.white + "");
		player.getPackets().sendIComponentText(813, 50, Colors.white + "");
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
		player.getPackets().sendIComponentText(813, 101, Colors.red + "PvM Pts:" + Utils.formatNumber(player.getPVMPoints())+" ");
		player.getPackets().sendIComponentText(813, 103, Colors.red + "DToken: " + player.dungTokens + "");
		player.getPackets().sendIComponentText(813, 105, Colors.red + "");
		player.getPackets().sendIComponentText(813, 107, Colors.red + "");
	}
	
	/**
	 * Handles the actual interfaces buttons.
	 * @param player The players interface to handle.
	 * @param componentId The players interface pressed component ID.
	 */
	public static void handleInterface(Player player, int componentId) {
		InterfaceManager.setPlayerInterfaceSelected(4);
		player.getInterfaceManager().closeChatBoxInterface();
		if (componentId == 52)
			Magic.vineTeleport(player, new WorldTile(3359, 3671, 0));
		
		if (componentId == 53)
			Magic.vineTeleport(player, new WorldTile(3071, 3649, 0));
		
		if (componentId == 54)
			Magic.vineTeleport(player, new WorldTile(2998, 3912, 0));
			
		if (componentId == 55)
			Magic.vineTeleport(player, new WorldTile(2539, 4715, 0));
		
		if (componentId == 56)
			Magic.vineTeleport(player, new WorldTile(3337, 3889, 0));
	}
}