package com.rs.game.topweeks;

import java.util.Map;

import com.discord.Discord;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.content.InterfaceManager;
import com.rs.utils.Colors;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class TopWeeklyVoters {

	@SuppressWarnings("unchecked")
	public static void openInterface(Player player) {
		if (World.wim.getVoteMap().isEmpty()) {
			player.errorMessage("Currently no votes recorded for this week. Get voting for top!");
			return;
		}
		player.closeInterfaces();
		player.getInterfaceManager().clear275Body();
		player.getInterfaceManager().sendInterface(275);
		player.getPackets().sendIComponentText(275, InterfaceManager.INTER_275_TITLE, "Weekly Top Voters");
		Object[] objects = World.wim.getTopVoters();
		int index = 0;
		for (int i = InterfaceManager.INTER_275_START_BODY; i <= InterfaceManager.INTER_275_END_BODY; i++) {
			if (index >= objects.length) {
				return;
			}
			Object vote = objects[index];
			player.getPackets().sendIComponentText(275, i, (index == 0 ? Colors.green
					: index == 1 ? Colors.orange
							: index == 2 ? Colors.yellow : index == 3 ? Colors.cyan : index == 4 ? Colors.blue : "")
					+ "" + Utils.formatString(((Map.Entry<String, Integer>) vote).getKey()) + " - "
					+ ((Map.Entry<String, Integer>) vote).getValue() + " votes "
					+ (index == 0 ? "reward: x5 Store Credit"
							: index == 1 ? "reward: x4 Store Credit"
									: index == 2 ? "reward: x3 Store Credit"
											: index == 3 ? "reward: x2 Store Credit"
													: index == 4 ? "reward: x1 Store Credit" : ""));
			index++;
		}
	}

	@SuppressWarnings("unchecked")
	public static void drawWinners() {
		if (World.wim.getVoteMap().isEmpty())
			return;
		String top = "";
		Object[] objects = World.wim.getTopVoters();
		try {
			if (objects.length > 0) {
				String user = ((Map.Entry<String, Integer>) objects[0]).getKey();
				addReward(user, 0);
				top += "1st: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		try {
			if (objects.length > 1) {
				String user = ((Map.Entry<String, Integer>) objects[1]).getKey();
				addReward(user, 1);
				top += " 2nd: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		try {
			if (objects.length > 2) {
				String user = ((Map.Entry<String, Integer>) objects[2]).getKey();
				addReward(user, 2);
				top += " 3rd: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		try {
			if (objects.length > 3) {
				String user = ((Map.Entry<String, Integer>) objects[3]).getKey();
				addReward(user, 3);
				top += " 4th: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		try {
			if (objects.length > 4) {
				String user = ((Map.Entry<String, Integer>) objects[4]).getKey();
				addReward(user, 4);
				top += " 5th: " + Utils.formatString(user);
			}
		} catch (Exception e) {
		}
		Discord.sendAnnouncementsMessage("A new week has begun! Top 5 voters: " + top);
		World.sendWorldMessage(Colors.green + "A new week has begun! Top 5 voters: " + top, false);
	}

	public static void addReward(String username, int rank) {
		Player winner = World.getPlayer(username);
		boolean online = true;
		if (winner == null) {
			winner = SerializableFilesManager.loadPlayer(username);
			if (winner == null)
				return;
			winner.setUsername(username);
			online = false;
		}
		if (rank == 0) {// 1st place rewards
			if (online) {
				winner.getInventory().addItem(34896, 5);

			} else {
				winner.getInventory().addOfflineItem(34896, 5);
			}

		}
		if (rank == 1) {// 2nd place rewards
			if (online) {
				winner.getInventory().addItem(34896, 4);
			} else {
				winner.getInventory().addOfflineItem(34896, 4);
			}
		}
		if (rank == 2) {// 3rd place rewards
			if (online) {
				winner.getInventory().addItem(34896, 3);
			} else {
				winner.getInventory().addOfflineItem(34896, 3);
			}

		}
		if (rank == 3) {// 4th place rewards
			if (online) {
				winner.getInventory().addItem(34896, 2);
			} else {
				winner.getInventory().addOfflineItem(34896, 2);
			}

		}
		if (rank == 4) {// 5th place rewards
			if (online) {
				winner.getInventory().addItem(34896, 1);
			} else {
				winner.getInventory().addOfflineItem(34896, 1);
			}

		}
		if (!online)
			SerializableFilesManager.savePlayer(winner);
	}

	public static void addVote(Player player) {
		if (player.getRights() == 2) {
			return;
		}
		String name = player.getUsername();
		if (World.wim.getVoteMap().containsKey(name)) {
			int currentVotes = World.wim.getVoteMap().get(name);
			World.wim.getVoteMap().put(name, currentVotes + 1);
		} else {
			World.wim.getVoteMap().put(name, 1);
		}
		World.wim.save();
	}

}