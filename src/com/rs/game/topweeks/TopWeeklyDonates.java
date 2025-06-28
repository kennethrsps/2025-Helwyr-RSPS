package com.rs.game.topweeks;

import java.util.Map;

import com.discord.Discord;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.content.InterfaceManager;
import com.rs.utils.Colors;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class TopWeeklyDonates {

	@SuppressWarnings("unchecked")
	public static void openInterface(Player player) {
		if (World.wim.getDonateMap().isEmpty()) {
			player.errorMessage("Currently no donations recorded for this week. Get donating for top!");
			return;
		}
		player.closeInterfaces();
		player.getInterfaceManager().clear275Body();
		player.getInterfaceManager().sendInterface(275);
		player.getPackets().sendIComponentText(275, InterfaceManager.INTER_275_TITLE, "Weekly Top Donators");
		Object[] objects = World.wim.getTopDonators();
		int index = 0;
		for (int i = InterfaceManager.INTER_275_START_BODY; i <= InterfaceManager.INTER_275_END_BODY; i++) {
			if (index >= objects.length) {
				return;
			}
			Object vote = objects[index];
			player.getPackets().sendIComponentText(275, i,
					(index == 0 ? Colors.green : index == 1 ? Colors.orange : index == 2 ? Colors.blue : "") + ""
							+ Utils.formatString(((Map.Entry<String, Double>) vote).getKey()) + " - $"
							+ ((Map.Entry<String, Double>) vote).getValue());
			index++;
		}
	}

	@SuppressWarnings("unchecked")
	public static void drawWinners() {
		if (World.wim.getDonateMap().isEmpty())
			return;
		String top = "";
		Object[] objects = World.wim.getTopDonators();
		try {
			if (objects.length > 0) {
				String user = ((Map.Entry<String, Double>) objects[0]).getKey();
				top += "1st: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		try {
			if (objects.length > 1) {
				String user = ((Map.Entry<String, Double>) objects[1]).getKey();
				top += " 2nd: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		try {
			if (objects.length > 2) {
				String user = ((Map.Entry<String, Double>) objects[2]).getKey();
				top += " 3rd: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		World.sendWorldMessage(Colors.green + "A new week has begun! Top 3 donators: " + top, false);
		Discord.sendAnnouncementsMessage("A new week has begun! Top 3 donators: " + top);

	}

	public static void addDonate(Player player, double amount) {
		if (player.getRights() == 2) {
			return;
		}
		String name = player.getUsername();
		if (World.wim.getDonateMap().containsKey(name)) {
			double current$ = World.wim.getDonateMap().get(name);
			World.wim.getDonateMap().put(name, current$ + amount);
		} else {
			World.wim.getDonateMap().put(name, amount);
		}
		World.wim.save();
	}

}