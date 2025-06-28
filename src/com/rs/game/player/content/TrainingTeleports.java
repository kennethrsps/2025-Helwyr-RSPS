package com.rs.game.player.content;

import com.rs.Settings;
import com.rs.game.player.content.InterfaceManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;

/**
 * Handles everything related to the training teleports interface.
* @author Zeus.
 */
public class TrainingTeleports {
	

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
		player.getPackets().sendIComponentText(813, 29, Colors.green + "NPC I"); // Boss Teleports
		player.getPackets().sendIComponentText(813, 52, Colors.white + "East Rock Crabs");
		player.getPackets().sendIComponentText(813, 53, Colors.white + "Glacor Cave");
		player.getPackets().sendIComponentText(813, 54, Colors.white + "Dwarf BattleField");
		player.getPackets().sendIComponentText(813, 55, Colors.white + "Frost Dragons");
		player.getPackets().sendIComponentText(813, 56, Colors.white + "Celestian Dragons");
		player.getPackets().sendIComponentText(813, 60, Colors.white + "Kuradals Dungeon");
		player.getPackets().sendIComponentText(813, 57, Colors.white + "Jadinko Lair");
		player.getPackets().sendIComponentText(813, 58, Colors.white + "Polpore Dungeon");
		player.getPackets().sendIComponentText(813, 59,	Colors.white + "Ancient Cavern");
		player.getPackets().sendIComponentText(813, 61, Colors.white + "Slayer Tower");

		player.getPackets().sendIComponentText(813, 30, Colors.green + "NPC II");
		player.getPackets().sendIComponentText(813, 43, Colors.white + "Brimhaven Dungeon");
		player.getPackets().sendIComponentText(813, 44, Colors.white + "Fremmenik Dungeon");
		player.getPackets().sendIComponentText(813, 45, Colors.white + "Taverly Dungeon");
		player.getPackets().sendIComponentText(813, 46, Colors.white + "Ascension Dungeon");
		player.getPackets().sendIComponentText(813, 47, Colors.white + "Jungle Strykewyrm");
		player.getPackets().sendIComponentText(813, 48, Colors.white + "Desert Strykewyrm");
		player.getPackets().sendIComponentText(813, 49, Colors.white + "Ice Strykewyrm");
		player.getPackets().sendIComponentText(813, 50, Colors.white + "Rune Dragon");
		player.getPackets().sendIComponentText(813, 51, Colors.white + "Airut");

		player.getPackets().sendIComponentText(813, 31, Colors.red + "Slayer NPC");
		player.getPackets().sendIComponentText(813, 37, Colors.white + "Gem Dragon");
		player.getPackets().sendIComponentText(813, 38, Colors.white + "Ripper Demon");
		player.getPackets().sendIComponentText(813, 39, Colors.white + "Acheron mammoth");
		player.getPackets().sendIComponentText(813, 40, Colors.white + "Wyvern");
		player.getPackets().sendIComponentText(813, 41, Colors.white + "The Magister");
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

		int[] componentIds = { 140, 141, 260, 278, 149, 150, 152, 153, 284, 167, 167, 168, 308, 155, 157, 156, 290, 159,
				161, 160, 296, 163, 165, 164, 302, 170, 171, 314, 318, 319, 326 };
		// let's clear everything else
		for (int id : componentIds)
			player.getPackets().sendIComponentText(1156, id, "Coming Soon");
	}
	
	/**
	 * Handles the actual interfaces buttons.
	 * @param player The players interface to handle.
	 * @param componentId The players interface pressed component ID.
	 */
	public static void handleInterface(Player player, int componentId) {
		InterfaceManager.setPlayerInterfaceSelected(3);
		player.getInterfaceManager().closeChatBoxInterface();
		if (componentId == 52)
			Magic.vineTeleport(player, new WorldTile(2710, 3710, 0)); // rocks #1
		
		if (componentId == 53)
			Magic.vineTeleport(player, new WorldTile(4181, 5726, 0)); // Glacor Cave
		
		if (componentId == 54)
			Magic.vineTeleport(player, new WorldTile(1519, 4704, 0)); // dwarf
			
		if (componentId == 55)  { //frost dragons
			if (player.getSkills().getLevel(Skills.DUNGEONEERING) < 85) {
				player.sendMessage("This area requires at least level 85 Dungeoneering to access!");
				player.getDialogueManager().finishDialogue();
				return;
			}
			Magic.vineTeleport(player, new WorldTile(1298, 4510, 0)); // frosts
			
		}
			
		if (componentId == 56) {//celestian dragons
			if (player.getSkills().getLevel(Skills.DUNGEONEERING) < 95) {
				player.sendMessage("This area requires at least level 95 Dungeoneering to access!");
				player.getDialogueManager().finishDialogue();
				return;
			}
			Magic.vineTeleport(player, new WorldTile(2285, 5972, 0)); // celestial Dragons
			
		}
		
		if (componentId == 51) {//Airut
			if (player.getSkills().getLevel(Skills.SLAYER) < 92) {
				player.sendMessage("This area requires at least level 92 Slayer to access!");
				player.getDialogueManager().finishDialogue();
				return;
			}
			Magic.vineTeleport(player, new WorldTile(1641, 5317, 0)); // celestial Dragons			
		
		}
		if (componentId == 60) { //kuradal dungeon
			Magic.vineTeleport(player, new WorldTile(1690, 5286, 1));
		}
		if (componentId == 57){
			if (player.getSkills().getLevel(Skills.SLAYER) < 80) {
				player.sendMessage("You need a slayer level of 80 to use this teleport.");
				return;
		}
			Magic.vineTeleport(player, new WorldTile(3012, 9274, 0)); // jadinko lair
			
		}
		
		if (componentId == 58)
			Magic.vineTeleport(player, new WorldTile(4625, 5457, 3)); // polypore
		
		if (componentId == 37) //Gem Dragon
			Magic.vineTeleport(player, new WorldTile(2971, 8978, 0)); // dragon dungeon
		
		if (componentId == 61)
			Magic.vineTeleport(player, new WorldTile(3423, 3543, 0)); // slayer tower
		
		if (componentId == 59)
			Magic.vineTeleport(player, new WorldTile(1763, 5365, 1)); // Ancient cavern.
		
		if (componentId == 43) // Brimhaven Dungeon
			Magic.vineTeleport(player, new WorldTile(2699, 9564, 0));
		
		if (componentId == 44) // fremennik Dungeon
			Magic.vineTeleport(player, new WorldTile(2808, 10002, 0));
		
		if (componentId == 45) // Taverley Dungeon
			Magic.vineTeleport(player, new WorldTile(2884, 9799, 0));
		if (componentId == 46) // Ascension Dungeon
			Magic.vineTeleport(player, new WorldTile(2508, 2886, 0));
		
		if (componentId == 47) { // Jungle Strykewyrms
			if (player.getSkills().getLevelForXp(Skills.SLAYER) < 73) {
				player.sendMessage("You need at least a level of 73 Slayer to go there!");
				return;
			}
			Magic.vineTeleport(player, new WorldTile(2452, 2911, 0));
		}
		
		if (componentId == 48) { // Desert Strykewyrms
			if (player.getSkills().getLevelForXp(Skills.SLAYER) < 77) {
				player.sendMessage("You need at least a level of 77 Slayer to go there!");
				return;
			}
			Magic.vineTeleport(player, new WorldTile(3356, 3160, 0));
		}
		
		if (componentId == 38) { // Ripper Demon
			if (player.getSkills().getLevelForXp(Skills.SLAYER) < 96) {
				player.sendMessage("You need at least a level of 96 Slayer to go there!");
				return;
			}
			Magic.vineTeleport(player, new WorldTile(5152, 7584, 0));
        }
	
	
	    if (componentId == 39) { // Acheron Mammoth
		    if (player.getSkills().getLevelForXp(Skills.SLAYER) < 96) {
			    player.sendMessage("You need at least a level of 96 Slayer to go there!");
			    return;
		   }
		   Magic.vineTeleport(player, new WorldTile(3424, 4378, 0));
        }
	
		if (componentId == 40) { // Wyvern
			if (player.getSkills().getLevelForXp(Skills.SLAYER) < 96) {
				player.sendMessage("You need at least a level of 96 Slayer to go there!");
				return;
			}
			Magic.vineTeleport(player, new WorldTile(5158, 7536, 0));		
		}
		 if (componentId == 41) { // MAGISTER
			    if (player.getSkills().getLevelForXp(Skills.SLAYER) < 99) {
				    player.sendMessage("You need at least a level of 99 Slayer to go there!");
				    return;
			   }
			   Magic.vineTeleport(player, new WorldTile(2207, 6850, 0));
	        }
		
		if (componentId == 49) {// ice Strykewyrms
			if (player.getSkills().getLevelForXp(Skills.SLAYER) < 93) {
				player.sendMessage("You need at least a level of 93 Slayer to go there!");
				return;
			}
			Magic.vineTeleport(player, new WorldTile(3435, 5648, 0));
			
	}
		if (componentId == 50) { // Rune Dragon
			Magic.vineTeleport(player, new WorldTile(2367, 3356, 0));
			
	}
		if (componentId == 154) // other crabs
			Magic.vineTeleport(player, new WorldTile(2672, 3710, 0));
	
	}

}