package com.rs.game.player.content.clans.interfaces;

import com.rs.game.player.Player;

public class CitadelControl {

	static int tab1 = 1259;
	static int tab2 = 1261;
	static int tab3 = 1258;
	static int tab4 = 1260;

	public static void sendCitadelControlTab1(Player player) {
		player.getInterfaceManager().sendInterface(tab1);
		player.getPackets().sendHideIComponent(tab1, 96, false);//hide kick all
		player.getPackets().sendGlobalConfig(1559, 2);
		//player.getPackets().sendRunScript(4897, 2);
		player.getPackets().sendRunScript(4920);
		//player.getPackets().sendRunScript(5991);
	}

	public static void sendCitadelControlTab2(Player player) {
		player.getInterfaceManager().sendInterface(tab2);
		//player.getPackets().sendRunScript(4899);
		//player.getPackets().sendRunScript(4937);
		//player.getPackets().sendRunScript(5001);
		//player.getPackets().sendHideIComponent(tab2, , true);
		player.getPackets().sendRunScript(5007);
		player.getPackets().sendRunScript(5009);
	}

	public static void sendCitadelControlTab3(Player player) {
		player.getInterfaceManager().sendInterface(tab3);
		player.getPackets().sendConfig(2260, 0);
		//player.getPackets().sendRunScript(4851);
	}

	//player.getPackets().runScript(scriptId);
	public static void sendCitadelControlTab4(Player player) {
		player.getInterfaceManager().sendInterface(tab4);
		//player.getPackets().sendRunScript(5957);
		//player.getPackets().sendRunScript(5958);
	}

}
