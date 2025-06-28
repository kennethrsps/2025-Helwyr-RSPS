package com.rs.game.player.content.clans.interfaces;

import com.rs.game.player.Player;
import com.rs.game.player.content.clans.content.achievements.ClanAchievements;

public class ClanAchievementInterface {

	/**
	 * Written By Anarchy / Achievements are in order
	 * 
	 * Represent The Clan
	 */
	//INFLUENCE, 
	/**
	 * Clan Resources
	 */
	//IT_ALL_ADDS_UP, IT_ALL_REALLY_ADDS_UP, 
	/**
	 * Clan Revenue
	 */
	//ROUNDING_UP_CHANGE, NEEDY_GREEDY, SHARING_IS_CARING,
	/**
	 * Clan Experience
	 */
	//NOW_THATS_EXPERIENCE, EXPERIENCE_HOGGER,
	/**
	 * Combat Achievements
	 */
	//SLAY_OR_BE_SLAYED,
	/**
	 * Clan Level Achievements
	 */
	//Anarchy_IS_LOVE, Anarchy_IS_LOVE_Anarchy_IS_LIFE

	public static int inter = 1156;

	public static void sendInterface(Player player) {
		player.getInterfaceManager().sendInterface(inter);
		player.getPackets().sendIComponentText(inter, 190, player.getClanName() + "'s Achievements");
		
		player.getPackets().sendIComponentText(inter, 108, "Influence");
		player.getPackets().sendIComponentText(inter, 109, "Equip a Clan Vexillum or a Clan Cloak.");
		player.getPackets().sendIComponentText(inter, 90, player.getClanManager().getClan().getClanAchievements().getClanHasCompletedAchievement(ClanAchievements.Achievements.INFLUENCE) ? "<col=269C0E>Completed" : "<col=ff0000>Not Completed");
		
		player.getPackets().sendIComponentText(inter, 113, "Anarchy is Love, Anarchy is Life.");
		player.getPackets().sendIComponentText(inter, 114, "Reach Level 25.");
		player.getPackets().sendIComponentText(inter, 206, player.getClanManager().getClan().getClanAchievements().getClanHasCompletedAchievement(ClanAchievements.Achievements.FROSTBITE_IS_LOVE_FROSTBITE_IS_LIFE) &&  player.getClanManager().getClan().getClanLevel().getClanLevel() >= 25 ? "<col=269C0E>Completed" : "<col=ff0000>Not Completed");
		
		player.getPackets().sendIComponentText(inter, 137, "Anarchy is love");
		player.getPackets().sendIComponentText(inter, 138, "Reach Level 12.");
		player.getPackets().sendIComponentText(inter, 254, player.getClanManager().getClan().getClanAchievements().getClanHasCompletedAchievement(ClanAchievements.Achievements.FROSTBITE_IS_LOVE) && player.getClanManager().getClan().getClanLevel().getClanLevel() >= 12  ? "<col=269C0E>Completed" : "<col=ff0000>Not Completed");
		
		player.getPackets().sendIComponentText(inter, 110, "Experience Hogger");
		player.getPackets().sendIComponentText(inter, 111, "Reach 500 Million Experience.");
		player.getPackets().sendIComponentText(inter, 200, player.getClanManager().getClan().getClanAchievements().getClanHasCompletedAchievement(ClanAchievements.Achievements.EXPERIENCE_HOGGER) ? "<col=269C0E>Completed" : "<col=ff0000>Not Completed");
		
		player.getPackets().sendIComponentText(inter, 116, "Now That's Experience");
		player.getPackets().sendIComponentText(inter, 117, "Reach 250 Million Experience.");
		player.getPackets().sendIComponentText(inter, 212, player.getClanManager().getClan().getClanAchievements().getClanHasCompletedAchievement(ClanAchievements.Achievements.NOW_THATS_EXPERIENCE) ? "<col=269C0E>Completed" : "<col=ff0000>Not Completed");
		
		player.getPackets().sendIComponentText(inter, 134, "Sharing is Caring");
		player.getPackets().sendIComponentText(inter, 135, "Recieve 1 Billion Clan Revenue.");
		player.getPackets().sendIComponentText(inter, 248, player.getClanManager().getClan().getClanAchievements().getClanHasCompletedAchievement(ClanAchievements.Achievements.SHARING_IS_CARING) ? "<col=269C0E>Completed" : "<col=ff0000>Not Completed");
		
		player.getPackets().sendIComponentText(inter, 122, "Needy Greedy");
		player.getPackets().sendIComponentText(inter, 123, "Recieve 500 Million Clan Revenue.");
		player.getPackets().sendIComponentText(inter, 230, player.getClanManager().getClan().getClanAchievements().getClanHasCompletedAchievement(ClanAchievements.Achievements.NEEDY_GREEDY) ? "<col=269C0E>Completed" : "<col=ff0000>Not Completed");
		
		player.getPackets().sendIComponentText(inter, 128, "Rounding Up Change");
		player.getPackets().sendIComponentText(inter, 129, "Recieve 100 Million Clan Revenue.");
		player.getPackets().sendIComponentText(inter, 236, player.getClanManager().getClan().getClanAchievements().getClanHasCompletedAchievement(ClanAchievements.Achievements.ROUNDING_UP_CHANGE) ? "<col=269C0E>Completed" : "<col=ff0000>Not Completed");
		
		player.getPackets().sendIComponentText(inter, 125, "Racking Up Resources");
		player.getPackets().sendIComponentText(inter, 126, "Collect 4.5 Million Clan Resources.");
		player.getPackets().sendIComponentText(inter, 224, player.getClanManager().getClan().getClanAchievements().getClanHasCompletedAchievement(ClanAchievements.Achievements.RACKING_UP_RESOURCES) ? "<col=269C0E>Completed" : "<col=ff0000>Not Completed");
		
		player.getPackets().sendIComponentText(inter, 143, "It All Adds Up");
		player.getPackets().sendIComponentText(inter, 144, "Collect 1.5 Million Clan Resources.");
		player.getPackets().sendIComponentText(inter, 266, player.getClanManager().getClan().getClanAchievements().getClanHasCompletedAchievement(ClanAchievements.Achievements.IT_ALL_ADDS_UP) ? "<col=269C0E>Completed" : "<col=ff0000>Not Completed");
		
		player.getPackets().sendIComponentText(inter, 146, "");
		player.getPackets().sendIComponentText(inter, 147, "");
		player.getPackets().sendIComponentText(inter, 272, "");
	}

}
