package com.rs.game.activites.resourcegather;

import java.util.ArrayList;
import java.util.List;

import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class SkillingEventRewards {
	
	public static void checkLoginAndSendInterface(Player player) {
		if(player.skillEventPoints > 0) {
			int pos = player.skillEventPosition;
			player.getInterfaceManager().sendInterface(1021);
			player.getPackets().sendIComponentText(1021, 31, "Woot!");
			player.getPackets().sendIComponentText(1021, 21, "Skilling Event Rewards");
			player.getPackets().sendIComponentText(1021, 23, "You participated in <br>skilling event(s) while offline");
			player.getPackets().sendIComponentText(1021, 24, "+Loot box full of goodies<br>+"+player.skillEventPoints / 10+" skill points");
			player.getPackets().sendIComponentText(1021, 29, pos > 0 ? "You came "+(pos == 1 ? "1st" : pos == 2 ? "2nd" : "3rd")+" in the event!" : "");
			player.getPackets().sendHideIComponent(1021, 27, true);
			//add skilling points
			player.skillEventPoints = 0;
			player.skillEventPosition = -1;
		}
	}
	
	public static void addReward(Player player, int pos, double score, int skillId) {
		Item reward = new Item(995, (4 - pos) * 1000000);
		boolean online = World.containsPlayer(player.getUsername());
		player.skillEventPoints += (int) score / 10;
		player.skillEventPosition = pos;
		if(online) { 
			player.getBank().addItem(reward, true);
			player.errorMessage("You came #"+pos+" in the "+Skills.SKILL_NAME[skillId]+" tournament and won "+(4 - pos)+"m gold!");
		} else {
			player.getBank().addItem(reward, false);
		}
		if(!online)
			SerializableFilesManager.savePlayer(player);
	}
	
	/*public static Item randomCosmetic() {
		return new Item(RewardCaskets.CASKETS.SKILLING.items[Utils.random(RewardCaskets.CASKETS.SKILLING.items.length)][0]);
	}*/
	
	public static void rewardAllSkillEvents() {
		World.sendWorldMessage("<col=6e9700>All skilling events have been rewarded & reset!</col>", false);
		for(SkillingEventSkillObject object : SkillingEventsManager.eventObjects.values()) {
			if(object.eventList.size() < 4) {
				continue;
			}
			rewardSkill(object);
		}
		destroyAndResetAllSkillEvents();
	}

	public static void rewardSkill(SkillingEventSkillObject skill) {
		if(skill.eventList.isEmpty()) {
			return;
		}
		for(SkillingEventObject event : skill.eventList) {
			Player player = Utils.getOnlineorOfflinePlayer(event.player);
			int pos = getPositionInSkillEvent(skill, player);
			double score = event.getXpTotal();
			addReward(player, pos, score, skill.skillId);
		}
	}
	
	public static void destroyAndResetAllSkillEvents() {
		SkillingEventsManager.eventObjects.clear();
		SkillingEventsManager.init();
	}
	
	public static int getPositionInSkillEvent(SkillingEventSkillObject skill, Player player) {
		int size = skill.eventList.size();
		SkillingEventObject[] events = skill.getTop5Events();
		try {
			if(size >= 1) {
				if(events[0].player.equals(player.getUsername())) {
					return 1;
				}
			} else if(size >= 2) {
				if(events[1].player.equals(player.getUsername())) {
					return 2;
				}
			} else if(size >= 3) {
				if(events[2].player.equals(player.getUsername())) {
					return 3;
				}
			}
		} catch (Exception e) {
		}
		return -1;
	}
	
	public static final int[] RARE_ITEMS = {1037, 1038, 1040, 1042, 1044, 1046, 1048, 1050, 1052, 1053, 1055, 1057};
	
}
