package com.rs.game.activites.resourcegather;

import java.util.HashMap;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.utils.Utils;

public class SkillingEventsManager {
	
	public static HashMap<Integer, SkillingEventSkillObject> eventObjects;

	public static void sendTab(Player player, boolean sendTab) {
		if(sendTab)
			player.getInterfaceManager().sendTab(player.getInterfaceManager().resizableScreen ? 126 : 186, 1117);
		loadEvent(player, Skills.WOODCUTTING);
	}
	
	public static void handleButtons(Player player, int c) {
		c--;
		if(c == 60) {
			loadEvent(player, Skills.WOODCUTTING);
		}
		if(c == 61) {
			loadEvent(player, Skills.MINING);
		}
		if(c == 63) {
			loadEvent(player, Skills.FISHING);
		}
		if(c == 65) {
			loadEvent(player, Skills.FARMING);
		}
		if(c == 67) {
			loadEvent(player, Skills.SLAYER);
		}
		if(c == 69) {
			loadEvent(player, Skills.HUNTER);
		}
		if(c == 71) {
			loadEvent(player, Skills.THIEVING);
		}
		if(c == 135) {
			loadStats(player, player.lastSkillEventViewed);
		}
	}
	
	public static void loadStats(Player player, int skillId) {
		SkillingEventSkillObject skillEvent = eventObjects.get(skillId);
		SkillingEventObject playerEvent = skillEvent.getPlayerEvent(player.getUsername());
		if(playerEvent == null) {
			player.errorMessage("Please participate in the "+Skills.SKILL_NAME[skillId].toLowerCase()+" skill to view this.");
			return;
		}
		player.getInterfaceManager().sendInterface(922);
		player.getPackets().sendIComponentText(922, 233, "Viewing details for "+Skills.SKILL_NAME[skillId].toLowerCase()+" event.");
		player.getPackets().sendIComponentText(922, 68, player.getDisplayName()+"'s scoreboard:");
		player.getPackets().sendIComponentText(922, 215, "Scores");
		player.getPackets().sendIComponentText(922, 98, skillId == Skills.SLAYER ? "Kills" : "Resources");
		player.getPackets().sendIComponentText(922, 100, "Score");
		player.getPackets().sendIComponentText(922, 69, skillId == Skills.SLAYER ? "Monster" : "Resource");
		player.getPackets().sendIComponentText(922, 70, "Amount");
		player.getPackets().sendIComponentText(922, 71, "Score");
		player.getPackets().sendIComponentText(922, 4, "Skilling points earned: "+(int) playerEvent.getXpTotal());
		player.getPackets().sendIComponentText(922, 58, "Reward<br>Favours");
		int pos = SkillingEventRewards.getPositionInSkillEvent(skillEvent, player);
		player.getPackets().sendIComponentText(922, 59, "Rare reward:");
		player.getPackets().sendIComponentText(922, 60, pos == 1 ? "1 / 1000" : pos == 2 ? "1 / 1500" : pos == 3 ? "1 / 2000" : "1 / 5000");
		if(skillId == Skills.WOODCUTTING) {
			player.getPackets().sendIComponentText(922, 62, "Dragon hatchet:");
			player.getPackets().sendIComponentText(922, 63, pos > 0 ? "1 / 100" : "Rank too low");
			player.getPackets().sendIComponentText(922, 65, "Inferno adze:");
			player.getPackets().sendIComponentText(922, 66, pos > 0 ? "1 / 500" : "Rank too low");
		}
		if(skillId == Skills.MINING) {
			player.getPackets().sendIComponentText(922, 62, "Dragon pickaxe:");
			player.getPackets().sendIComponentText(922, 63, pos > 0 ? "1 / 100" : "Rank too low");
			player.getPackets().sendIComponentText(922, 65, "Inferno adze:");
			player.getPackets().sendIComponentText(922, 66, pos > 0 ? "1 / 500" : "Rank too low");
		}
		player.getPackets().sendIComponentText(922, 16, "Misc.");
		player.getPackets().sendIComponentText(922, 18, "XP Gain<br> "+((int) player.getSkills().getModifiedXP(skillId, playerEvent.getXpTotal())));
		player.getPackets().sendIComponentText(922, 20, skillId == Skills.SLAYER ? "" : "GP Gain<br> "+(int) playerEvent.getValueTotal());
		for(int i=0;i<SCORE_COMPS.length;i++) {
			int[] comps = SCORE_COMPS[i];
			for(int k=0;k<comps.length;k++) {
				player.getPackets().sendIComponentText(922, comps[k], "");
			}
		}
		int resIndex = 0;
		y:for(int i=0;i<SCORE_COMPS.length;i++) {
			int[] comps = SCORE_COMPS[i];
			if(playerEvent.resources.size() < resIndex+1) {
				break y;
			}
			ResourceObject resource = playerEvent.getIndex(resIndex);
			if(resource == null)
				continue;
			resIndex++;
			String resName = resource.isNpc ? NPCDefinitions.getNPCDefinitions(resource.itemId).name : ItemDefinitions.getItemDefinitions(resource.itemId).getName();
			int amount = resource.amount;
			double score = resource.xpGain;
			player.getPackets().sendIComponentText(922, comps[0], resName);
			player.getPackets().sendIComponentText(922, comps[1], ""+amount);
			player.getPackets().sendIComponentText(922, comps[2], ""+(int) score);
		}
		player.getPackets().sendIComponentText(922, 99, ""+playerEvent.getResourceAmount());
		player.getPackets().sendIComponentText(922, 101, ""+(int) playerEvent.getXpTotal());
		int size = skillEvent.eventList.size();
		SkillingEventObject[] events = skillEvent.getTop5Events();
		if(size >= 1) {
			SkillingEventObject event = events[0];
			player.getPackets().sendIComponentText(922, 34, ""+Utils.formatPlayerNameForDisplay(event.player));
			player.getPackets().sendIComponentText(922, 35, "Score: "+(int) event.getXpTotal());
		} else {
			player.getPackets().sendIComponentText(922, 34, "");
			player.getPackets().sendIComponentText(922, 35, "");
		}
		if(size >= 2) {
			SkillingEventObject event = events[1];
			player.getPackets().sendIComponentText(922, 39, ""+Utils.formatPlayerNameForDisplay(event.player));
			player.getPackets().sendIComponentText(922, 40, "Score: "+(int) event.getXpTotal());
		} else {
			player.getPackets().sendIComponentText(922, 39, "");
			player.getPackets().sendIComponentText(922, 40, "");
		}
		if(size >= 3) {
			SkillingEventObject event = events[2];
			player.getPackets().sendIComponentText(922, 44, ""+Utils.formatPlayerNameForDisplay(event.player));
			player.getPackets().sendIComponentText(922, 45, "Score: "+(int) event.getXpTotal());
		} else {
			player.getPackets().sendIComponentText(922, 44, "");
			player.getPackets().sendIComponentText(922, 45, "");
		}
		if(size >= 4) {
			SkillingEventObject event = events[3];
			player.getPackets().sendIComponentText(922, 49, ""+Utils.formatPlayerNameForDisplay(event.player));
			player.getPackets().sendIComponentText(922, 50, "Score: "+(int) event.getXpTotal());
		} else {
			player.getPackets().sendIComponentText(922, 49, "");
			player.getPackets().sendIComponentText(922, 50, "");
		}
		if(size >= 5) {
			SkillingEventObject event = events[4];
			player.getPackets().sendIComponentText(922, 54, ""+Utils.formatPlayerNameForDisplay(event.player));
			player.getPackets().sendIComponentText(922, 55, "Score: "+(int) event.getXpTotal());
		} else {
			player.getPackets().sendIComponentText(922, 54, "");
			player.getPackets().sendIComponentText(922, 55, "");
		}
	}
	
	public static void loadEvent(Player player, int skillId) {
		player.lastSkillEventViewed = skillId;
		SkillingEventSkillObject skillEvent = eventObjects.get(skillId);
		player.getPackets().sendIComponentText(1117, 32, skillEvent.skillName);
		player.getPackets().sendIComponentText(1117, 34, ""+skillEvent.eventList.size());
		player.getPackets().sendIComponentText(1117, 121, "Events:");
		player.getPackets().sendIComponentText(1117, 122, "Rewards: "+Utils.getFormattedTime(skillEvent.timeStarted - Utils.currentTimeMillis(), false));
		int size = skillEvent.eventList.size();
		SkillingEventObject[] events = skillEvent.getTop5Events();
		if(size >= 1) {
			SkillingEventObject event = events[0];
			player.getPackets().sendIComponentText(1117, 45, ""+Utils.formatPlayerNameForDisplay(event.player));
			//player.getPackets().sendIComponentText(1117, 48, "(Rank 1)");
			player.getPackets().sendIComponentText(1117, 44, "Score:");
			player.getPackets().sendIComponentText(1117, 47, skillId == 18 ? "Kills:" : "Resources:");
			player.getPackets().sendIComponentText(1117, 46, ""+(int) event.getXpTotal());
			player.getPackets().sendIComponentText(1117, 50, ""+event.getResourceAmount());
		} else {
			player.getPackets().sendIComponentText(1117, 45, "");
			player.getPackets().sendIComponentText(1117, 48, "");
			player.getPackets().sendIComponentText(1117, 44, "");
			player.getPackets().sendIComponentText(1117, 47, "");
			player.getPackets().sendIComponentText(1117, 46, "");
			player.getPackets().sendIComponentText(1117, 50, "");
		}
		if(size >= 2) {
			SkillingEventObject event = events[1];
			player.getPackets().sendIComponentText(1117, 100, ""+Utils.formatPlayerNameForDisplay(event.player));
			//player.getPackets().sendIComponentText(1117, 102, "(Rank 2)");
			player.getPackets().sendIComponentText(1117, 99, "Score:");
			player.getPackets().sendIComponentText(1117, 102, skillId == 18 ? "Kills:" : "Resources:");
			player.getPackets().sendIComponentText(1117, 101, ""+(int) event.getXpTotal());
			player.getPackets().sendIComponentText(1117, 105, ""+event.getResourceAmount());
		} else {
			player.getPackets().sendIComponentText(1117, 100, "");
			player.getPackets().sendIComponentText(1117, 99, "");
			player.getPackets().sendIComponentText(1117, 102, "");
			player.getPackets().sendIComponentText(1117, 101, "");
			player.getPackets().sendIComponentText(1117, 105, "");
			player.getPackets().sendIComponentText(1117, 102, "");
		}
		if(size >= 3) {
			SkillingEventObject event = events[2];
			player.getPackets().sendIComponentText(1117, 118, ""+Utils.formatPlayerNameForDisplay(event.player));
			//player.getPackets().sendIComponentText(1117, 119, "(Rank 3)");
			player.getPackets().sendIComponentText(1117, 117, "Score:");
			player.getPackets().sendIComponentText(1117, 120, skillId == 18 ? "Kills:" : "Resources:");
			player.getPackets().sendIComponentText(1117, 119, ""+(int) event.getXpTotal());
			player.getPackets().sendIComponentText(1117, 123, ""+event.getResourceAmount());
		} else {
			player.getPackets().sendIComponentText(1117, 118, "");
			player.getPackets().sendIComponentText(1117, 119, "");
			player.getPackets().sendIComponentText(1117, 117, "");
			player.getPackets().sendIComponentText(1117, 120, "");
			player.getPackets().sendIComponentText(1117, 119, "");
			player.getPackets().sendIComponentText(1117, 123, "");
		}
	}
	
	public static void checkSkillTimes() {
		long time = Utils.currentTimeMillis();
		long eventTime = eventObjects.get(Skills.SLAYER).timeStarted;
		if((eventTime - time) < 0) {
			SkillingEventRewards.rewardAllSkillEvents();
		}
	}
	
	public static final void init() {
		eventObjects = new HashMap<Integer, SkillingEventSkillObject>();
		long time = Utils.currentTimeMillis();
		eventObjects.put(Skills.WOODCUTTING, new SkillingEventSkillObject(Skills.WOODCUTTING, "Woodcutting", time));
		eventObjects.put(Skills.MINING, new SkillingEventSkillObject(Skills.MINING, "Mining", time));
		eventObjects.put(Skills.FISHING, new SkillingEventSkillObject(Skills.FISHING, "Fishing", time));
		eventObjects.put(Skills.FARMING, new SkillingEventSkillObject(Skills.FARMING, "Farming", time));
		eventObjects.put(Skills.SLAYER, new SkillingEventSkillObject(Skills.SLAYER, "Slayer", time));
		eventObjects.put(Skills.HUNTER, new SkillingEventSkillObject(Skills.HUNTER, "Hunter", time));
		eventObjects.put(Skills.THIEVING, new SkillingEventSkillObject(Skills.THIEVING, "Thieving", time));
	}
	
	public static int[][] SCORE_COMPS = {{73, 74, 75},
			{77, 78, 79},
			{87, 88, 89},
			{90, 91, 92},
			{93, 94, 96},
			{83, 95, 97},
			{82, 84, 85}};
	
}
