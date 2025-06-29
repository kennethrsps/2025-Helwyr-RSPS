package com.rs.game.topweeks;

import java.util.Map;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.content.InterfaceManager;
import com.rs.utils.Colors;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class TopWeeklyTime {

	@SuppressWarnings("unchecked")
	public static void openInterface(Player player) {
		if (World.wim.getTimeMap().isEmpty()) {
			player.errorMessage("Currently no game times recorded for this week. Get playing for top!");
			return;
		}
		player.closeInterfaces();
		player.getInterfaceManager().clear275Body();
		player.getInterfaceManager().sendInterface(275);
		player.getPackets().sendIComponentText(275, InterfaceManager.INTER_275_TITLE, "Weekly Top Game Times");
		Object[] objects = World.wim.getTopTimes();
		int index = 0;
		for (int i = InterfaceManager.INTER_275_START_BODY; i <= InterfaceManager.INTER_275_END_BODY; i++) {
			if (index >= objects.length) {
				return;
			}
			Object vote = objects[index];
			player.getPackets().sendIComponentText(275, i,
					(index == 0 ? Colors.green : index == 1 ? Colors.orange : index == 2 ? Colors.yellow : index == 3 ? Colors.cyan : index == 4 ? Colors.blue : "") + ""
							+ Utils.formatString(((Map.Entry<String, Long>) vote).getKey()) + " - "
							+ Utils.getFormattedTime(((Map.Entry<String, Long>) vote).getValue(), true) + " "
							+ (index == 0 ? ""
									: index == 1 ? ""
											: index == 2 ? ""
													: index == 3 ? ""
															: index == 4 ? "" : ""));
			index++;
		}
	}
/**
	@SuppressWarnings("unchecked")
	public static void drawWinners() {
		if (World.wim.getTimeMap().isEmpty())
			return;
		String top = "";
		Object[] objects = World.wim.getTopTimes();
		try {
			if (objects.length > 0) {
				String user = ((Map.Entry<String, Long>) objects[0]).getKey();
				TopWeeklyVoters.addReward(user, 0);
				top += "1st: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		try {
			if (objects.length > 1) {
				String user = ((Map.Entry<String, Long>) objects[1]).getKey();
				TopWeeklyVoters.addReward(user, 1);// all done
				top += " 2nd: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		try {
			if (objects.length > 2) {
				String user = ((Map.Entry<String, Long>) objects[2]).getKey();
				TopWeeklyVoters.addReward(user, 2);
				top += " 3rd: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		try {
			if (objects.length > 3) {
				String user = ((Map.Entry<String, Long>) objects[3]).getKey();
				TopWeeklyVoters.addReward(user, 3);
				top += " 4th: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		try {
			if (objects.length > 4) {
				String user = ((Map.Entry<String, Long>) objects[4]).getKey();
				TopWeeklyVoters.addReward(user, 4);
				top += " 5th: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		World.sendWorldMessage(Colors.green + "" + top, false);
	}
**/
	public static void addTime(Player player) {
		if (player.getRights() == 2) {
			return;
		}
		String name = player.getUsername();
		if (World.wim.getTimeMap().containsKey(name) && !player.getUsername().contains("zeus")) {
			long currentTime = World.wim.getTimeMap().get(name);
			World.wim.getTimeMap().put(name, (long) currentTime + 1000);
		} else {
			World.wim.getTimeMap().put(name, (long) 1000);
		}
		World.wim.save();
	}

}