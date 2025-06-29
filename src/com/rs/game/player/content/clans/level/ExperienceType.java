package com.rs.game.player.content.clans.level;

import java.io.Serializable;

import com.rs.game.player.Player;

/**
 * 
 * @author Frostbite
 *
 *<frostbitersps@gmail.com><skype@frostbitersps>
 */

public class ExperienceType implements Serializable {

	private static final long serialVersionUID = 6125421600908653228L;

	public static final int EASY_ACHIEVEMENT = 1, MEDIUM_ACHIEVEMENT = 2, HARD_ACHIEVEMENT = 3, 
			EASY_TASK = 4, MEDIUM_TASK = 5, HARD_TASK = 6, 
				EASY_QUEST = 7, MEDIUM_QUEST = 8, HARD_QUEST = 9,
					RESOURCE_CAP = 10, 
						BOSS = 11, MONSTER = 12, 
								DEFAULT_LEVELING = 13;
	
	/**
	 * Achievement Experience Types
	 */
	
	public void addHardAchievementExp(Player player) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23])
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, getExperience(HARD_ACHIEVEMENT));
		player.getClanManager().getClan().getClanLevel().gainExperience(player, getExperience(HARD_ACHIEVEMENT));
	}
	
	public void addMediumAchievementExp(Player player) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23])
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, getExperience(MEDIUM_ACHIEVEMENT));
		player.getClanManager().getClan().getClanLevel().gainExperience(player, getExperience(MEDIUM_ACHIEVEMENT));
	}

	public void addEasyAchievementExp(Player player) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23])
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, getExperience(EASY_ACHIEVEMENT));
		player.getClanManager().getClan().getClanLevel().gainExperience(player, getExperience(EASY_ACHIEVEMENT));
	}
	
	/**
	 * Task Experience Type
	 */
	
	public void addHardTaskExp(Player player) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23]) 	
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, getExperience(HARD_TASK));
		player.getClanManager().getClan().getClanLevel().gainExperience(player, getExperience(HARD_TASK));
	}
	
	public void addMediumTaskExp(Player player) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23]) 	
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, getExperience(MEDIUM_TASK));
		player.getClanManager().getClan().getClanLevel().gainExperience(player, getExperience(MEDIUM_TASK));
	}

	public void addEasyTaskExp(Player player) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23]) 	
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, getExperience(EASY_TASK));
		player.getClanManager().getClan().getClanLevel().gainExperience(player, getExperience(EASY_TASK));
	}
	
	/**
	 * Quest Experience Type
	 */
	
	public void addHardQuestExp(Player player) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23])
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, getExperience(HARD_QUEST));
		player.getClanManager().getClan().getClanLevel().gainExperience(player, getExperience(HARD_QUEST));
	}
	
	public void addMediumQuestExp(Player player) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23])
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, getExperience(MEDIUM_QUEST));
		player.getClanManager().getClan().getClanLevel().gainExperience(player, getExperience(MEDIUM_QUEST));
	}

	public void addEasyQuestExp(Player player) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23])
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, getExperience(EASY_QUEST));
		player.getClanManager().getClan().getClanLevel().gainExperience(player, getExperience(EASY_QUEST));
	}
	
	/**
	 * Resource Experience Type 
	 */

	public void addResourceCapExp(Player player) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23])
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, getExperience(RESOURCE_CAP));
		player.getClanManager().getClan().getClanLevel().gainExperience(player, getExperience(RESOURCE_CAP));
	}
	
	/**
	 * Default Experience Type
	 */

	public void addDefaultExp(Player player) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23]) 
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, getExperience(DEFAULT_LEVELING));
	}
	
	public void addDefaultLevelingExp(Player player, int xp) {
		if(player.getClanManager().getClan().getClanLevel().clanXp >= ClanLevel.cXp[23]) 
			return;
		player.getClanManager().getClan().getClanLevel().addClanXp(player, xp);
	}

	public int getExperience(int type) {
		switch(type) {
		case HARD_ACHIEVEMENT:
			return 2598;
		case MEDIUM_ACHIEVEMENT:
			return 1972;
		case EASY_ACHIEVEMENT:
			return 1570;
		case HARD_TASK:
			return 1985;
		case MEDIUM_TASK:
			return 1478;
		case EASY_TASK:
			return 1050;
		case HARD_QUEST:
			return 1650;
		case MEDIUM_QUEST:
			return 1240;
		case EASY_QUEST:
			return 750;
		case RESOURCE_CAP:
			return 1875;
		case DEFAULT_LEVELING:
			return 1;
		}
		return 0;
	}



	public void sendMessage(Player player, String message) {
		player.getPackets().sendGameMessage(message);
	}

}
