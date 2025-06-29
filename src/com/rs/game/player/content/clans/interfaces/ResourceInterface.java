package com.rs.game.player.content.clans.interfaces;

import com.rs.game.player.Player;
import com.rs.game.player.content.clans.content.ClanResources;

/**
 * 
 * @author Frostbite
 *
 *<frostbitersps@gmail.com><skype@frostbitersps>
 */

public class ResourceInterface {
	
	static int inter = 1117;
	
	public static void sendWoodCuttingComponents(Player player) {
		player.getPackets().sendIComponentText(inter, 32, "Woodcutting Plot");
		//player.getPackets().sendIComponentText(inter, 34, "" + CitadelResources.Plots.WOODCUTTING_PLOT_1.getPlotId());
		
		player.getPackets().sendIComponentText(inter, 45, "Woodcutting Plot 1");
		player.getPackets().sendIComponentText(inter, 44, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 47, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 46, "0");
		player.getPackets().sendIComponentText(inter, 50, "0");
		
		player.getPackets().sendIComponentText(inter, 100, "Woodcutting Plot 2");
		player.getPackets().sendIComponentText(inter, 99, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 102, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 101, "0");
		player.getPackets().sendIComponentText(inter, 105, "0");
		
		player.getPackets().sendIComponentText(inter, 118, "Woodcutting Plot 3");
		player.getPackets().sendIComponentText(inter, 117, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 120, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 119, "0");
		player.getPackets().sendIComponentText(inter, 123, "0");
		
		player.getPackets().sendIComponentText(inter, 124, "Resource Cap:");
		player.getPackets().sendIComponentText(inter, 125, "" + player.getClanManager().getClan().getClanResources().getCaptivity(player, ClanResources.WOODCUTTING));
	}
	
	public static void sendMiningComponents(Player player) {
		player.getPackets().sendIComponentText(inter, 32, "Mining Plot");
		//player.getPackets().sendIComponentText(inter, 34, "" + CitadelResources.Plots.MINING_PLOT_1.getPlotId());
		
		player.getPackets().sendIComponentText(inter, 45, "Mining Plot 1");
		player.getPackets().sendIComponentText(inter, 44, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 47, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 46, "0");
		player.getPackets().sendIComponentText(inter, 50, "0");
		
		player.getPackets().sendIComponentText(inter, 100, "Mining Plot 2");
		player.getPackets().sendIComponentText(inter, 99, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 102, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 101, "0");
		player.getPackets().sendIComponentText(inter, 105, "0");
		
		player.getPackets().sendIComponentText(inter, 118, "Mining Plot 3");
		player.getPackets().sendIComponentText(inter, 117, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 120, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 119, "0");
		player.getPackets().sendIComponentText(inter, 123, "0");
		
		player.getPackets().sendIComponentText(inter, 124, "Resource Cap:");
		player.getPackets().sendIComponentText(inter, 125, "" + player.getClanManager().getClan().getClanResources().getCaptivity(player, ClanResources.MINING));
	}
	
	public static void sendKilnComponents(Player player) {
		player.getPackets().sendIComponentText(inter, 32, "Kiln Plot");
		//player.getPackets().sendIComponentText(inter, 34, "" + CitadelResources.Plots.KILN_PLOT_1.getPlotId());
		
		player.getPackets().sendIComponentText(inter, 45, "Kiln Plot 1");
		player.getPackets().sendIComponentText(inter, 44, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 47, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 46, "0");
		player.getPackets().sendIComponentText(inter, 50, "0");
		
		player.getPackets().sendIComponentText(inter, 100, "Kiln Plot 2");
		player.getPackets().sendIComponentText(inter, 99, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 102, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 101, "0");
		player.getPackets().sendIComponentText(inter, 105, "0");
		
		player.getPackets().sendIComponentText(inter, 118, "Kiln Plot 3");
		player.getPackets().sendIComponentText(inter, 117, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 120, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 119, "0");
		player.getPackets().sendIComponentText(inter, 123, "0");
		
		player.getPackets().sendIComponentText(inter, 124, "Resource Cap:");
		player.getPackets().sendIComponentText(inter, 125, "1500");
	}
	
	public static void sendFurnaceComponents(Player player) {
		player.getPackets().sendIComponentText(inter, 32, "Furnace Plot");
		//player.getPackets().sendIComponentText(inter, 34, "" + CitadelResources.Plots.FIREMAING_PLOT_1.getPlotId());
		
		player.getPackets().sendIComponentText(inter, 45, "Furnace Plot 1");
		player.getPackets().sendIComponentText(inter, 44, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 47, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 46, "0");
		player.getPackets().sendIComponentText(inter, 50, "0");
		
		player.getPackets().sendIComponentText(inter, 100, "Furnace Plot 2");
		player.getPackets().sendIComponentText(inter, 99, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 102, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 101, "0");
		player.getPackets().sendIComponentText(inter, 105, "0");
		
		player.getPackets().sendIComponentText(inter, 118, "Furnace Plot 3");
		player.getPackets().sendIComponentText(inter, 117, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 120, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 119, "0");
		player.getPackets().sendIComponentText(inter, 123, "0");
		
		player.getPackets().sendIComponentText(inter, 124, "Resource Cap:");
		player.getPackets().sendIComponentText(inter, 125, "1500");
	}
	
	public static void sendLoomComponents(Player player) {
		player.getPackets().sendIComponentText(inter, 32, "Loom Plot");
	//	player.getPackets().sendIComponentText(inter, 34, "" + CitadelResources.Plots.LOOM_PLOT_1.getPlotId());
		
		player.getPackets().sendIComponentText(inter, 45, "Loom Plot 1");
		player.getPackets().sendIComponentText(inter, 44, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 47, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 46, "0");
		player.getPackets().sendIComponentText(inter, 50, "0");
		
		player.getPackets().sendIComponentText(inter, 100, "Loom Plot 2");
		player.getPackets().sendIComponentText(inter, 99, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 102, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 101, "0");
		player.getPackets().sendIComponentText(inter, 105, "0");
		
		player.getPackets().sendIComponentText(inter, 118, "Loom Plot 3");
		player.getPackets().sendIComponentText(inter, 117, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 120, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 119, "0");
		player.getPackets().sendIComponentText(inter, 123, "0");
		
		player.getPackets().sendIComponentText(inter, 124, "Resource Cap:");
		player.getPackets().sendIComponentText(inter, 125, "1500");
	}
	
	public static void sendSummoningComponents(Player player) {
		player.getPackets().sendIComponentText(inter, 32, "Summoning Plot");
	//	player.getPackets().sendIComponentText(inter, 34, "" + CitadelResources.Plots.SUMMONING_PLOT_1.getPlotId());
		
		player.getPackets().sendIComponentText(inter, 45, "Summoning Plot 1");
		player.getPackets().sendIComponentText(inter, 44, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 47, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 46, "0");
		player.getPackets().sendIComponentText(inter, 50, "0");
		
		player.getPackets().sendIComponentText(inter, 100, "Summoning Plot 2");
		player.getPackets().sendIComponentText(inter, 99, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 102, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 101, "0");
		player.getPackets().sendIComponentText(inter, 105, "0");
		
		player.getPackets().sendIComponentText(inter, 118, "Summoning Plot 3");
		player.getPackets().sendIComponentText(inter, 117, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 120, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 119, "0");
		player.getPackets().sendIComponentText(inter, 123, "0");
		
		player.getPackets().sendIComponentText(inter, 124, "Resource Cap:");
		player.getPackets().sendIComponentText(inter, 125, "1500");
	}
	
	public static void sendBarbequeComponents(Player player) {
		player.getPackets().sendIComponentText(inter, 32, "Barbeque Plot");
	//	player.getPackets().sendIComponentText(inter, 34, "" + CitadelResources.Plots.BARBEQUE_PLOT_1.getPlotId());
		
		player.getPackets().sendIComponentText(inter, 45, "Barbaque Plot 1");
		player.getPackets().sendIComponentText(inter, 44, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 47, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 46, "0");
		player.getPackets().sendIComponentText(inter, 50, "0");
		
		player.getPackets().sendIComponentText(inter, 100, "Barbeque Plot 2");
		player.getPackets().sendIComponentText(inter, 99, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 102, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 101, "0");
		player.getPackets().sendIComponentText(inter, 105, "0");
		
		player.getPackets().sendIComponentText(inter, 118, "Barbeque Plot 3");
		player.getPackets().sendIComponentText(inter, 117, "Clan Resources: ");
		player.getPackets().sendIComponentText(inter, 120, "You Contributed: ");
		player.getPackets().sendIComponentText(inter, 119, "0");
		player.getPackets().sendIComponentText(inter, 123, "0");
		
		player.getPackets().sendIComponentText(inter, 124, "Resource Cap:");
		player.getPackets().sendIComponentText(inter, 125, "1500");
	}

}
