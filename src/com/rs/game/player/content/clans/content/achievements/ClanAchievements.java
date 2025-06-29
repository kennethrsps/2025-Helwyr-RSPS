package com.rs.game.player.content.clans.content.achievements;

import java.io.Serializable;
import java.util.Vector;

import com.rs.Settings;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

/**
 * 
 * @author Frostbite
 *
 *<frostbitersps@gmail.com><skype@frostbitersps>
 */

public class ClanAchievements implements Serializable {

	private static final long serialVersionUID = -6924078866267535291L;

	/**
	 * Achievements
	 *
	 */
	
	private int clanSlayerTasksCompleted;
	

	public static enum Achievements {
		/**
		 * Written By Frostbite / Achievements are in order
		 * 
		 * Represent The Clan
		 */
		INFLUENCE, 
		/**
		 * Clan Resources
		 */
		IT_ALL_ADDS_UP, RACKING_UP_RESOURCES, 
		/**
		 * Clan Revenue
		 */
		ROUNDING_UP_CHANGE, NEEDY_GREEDY, SHARING_IS_CARING,
		/**
		 * Clan Experience
		 */
		NOW_THATS_EXPERIENCE, EXPERIENCE_HOGGER,
		/**
		 * Combat Achievements
		 */
		SLAY_OR_BE_SLAYED,
		/**
		 * Clan Level Achievements
		 */
		FROSTBITE_IS_LOVE, FROSTBITE_IS_LOVE_FROSTBITE_IS_LIFE
	}

	private Vector<Achievements> completedAchievements = new Vector<Achievements>();

	public boolean getClanHasCompletedAchievement(Achievements achievements) {
		return completedAchievements.contains(achievements);
	}

	public boolean addClanCompletedAchievement(Achievements achievements) {
		if (completedAchievements.contains(achievements)) {
			return false;
		}
		return completedAchievements.add(achievements);
	}

	public boolean removeClanCompletedAchievement(Achievements achievements) {
		if (!completedAchievements.contains(achievements))
			return false;
		return completedAchievements.remove(achievements);
	}

	public String getAchievementName(Achievements achievements) {
		return Utils.formatString(achievements.name());
	}

	public void checkAchievements(Player player) {
		/**
		 * Clan Experience Experience
		 */
		/**
		 * 500m
		 */
		if(!getClanHasCompletedAchievement(ClanAchievements.Achievements.EXPERIENCE_HOGGER) && player.getClanManager().getClan().getClanLevel().getClanXp() >= 500000000) {
			player.getClanManager().getClan().getClanAchievements().addClanCompletedAchievement(ClanAchievements.Achievements.EXPERIENCE_HOGGER);
			player.getClanManager().sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '" + player.getClanManager().getClan().getClanAchievements().getAchievementName(ClanAchievements.Achievements.EXPERIENCE_HOGGER) + "'.");
			player.getClanManager().getClan().getExperienceType().addHardAchievementExp(player);
		}
		/**
		 * 250m
		 */
		if(!getClanHasCompletedAchievement(ClanAchievements.Achievements.NOW_THATS_EXPERIENCE) && player.getClanManager().getClan().getClanLevel().getClanXp() >= 250000000) {
			player.getClanManager().getClan().getClanAchievements().addClanCompletedAchievement(ClanAchievements.Achievements.NOW_THATS_EXPERIENCE);
			player.getClanManager().sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '" + player.getClanManager().getClan().getClanAchievements().getAchievementName(ClanAchievements.Achievements.NOW_THATS_EXPERIENCE) + "'.");
			player.getClanManager().getClan().getExperienceType().addHardAchievementExp(player);
		}
		/**
		 * Revenue
		 */
		/**
		 * 1b
		 */
		if(!getClanHasCompletedAchievement(ClanAchievements.Achievements.SHARING_IS_CARING) && player.getClanManager().getClan().getClanRevenue().getTotalRevenueEarned() >= 1000000000) {
			player.getClanManager().getClan().getClanAchievements().addClanCompletedAchievement(ClanAchievements.Achievements.SHARING_IS_CARING);
			player.getClanManager().sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '" + player.getClanManager().getClan().getClanAchievements().getAchievementName(ClanAchievements.Achievements.SHARING_IS_CARING) + "'.");
			player.getClanManager().getClan().getExperienceType().addHardAchievementExp(player);
		}
		/**
		 * 500m
		 */
		if(!getClanHasCompletedAchievement(ClanAchievements.Achievements.NEEDY_GREEDY) && player.getClanManager().getClan().getClanRevenue().getTotalRevenueEarned() >= 500000000) {
			player.getClanManager().getClan().getClanAchievements().addClanCompletedAchievement(ClanAchievements.Achievements.NEEDY_GREEDY);
			player.getClanManager().sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '" + player.getClanManager().getClan().getClanAchievements().getAchievementName(ClanAchievements.Achievements.NEEDY_GREEDY) + "'.");
			player.getClanManager().getClan().getExperienceType().addHardAchievementExp(player);
		}
		/**
		 * 100m
		 */
		if(!getClanHasCompletedAchievement(ClanAchievements.Achievements.ROUNDING_UP_CHANGE) && player.getClanManager().getClan().getClanRevenue().getTotalRevenueEarned() >= 100000000) {
			player.getClanManager().getClan().getClanAchievements().addClanCompletedAchievement(ClanAchievements.Achievements.ROUNDING_UP_CHANGE);
			player.getClanManager().sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '" + player.getClanManager().getClan().getClanAchievements().getAchievementName(ClanAchievements.Achievements.ROUNDING_UP_CHANGE) + "'.");
			player.getClanManager().getClan().getExperienceType().addHardAchievementExp(player);
		}
		/**
		 * Resources
		 */
		/**
		 * 4.5m
		 */
		if(!getClanHasCompletedAchievement(ClanAchievements.Achievements.RACKING_UP_RESOURCES) && player.getClanManager().getClan().getClanResources().getTotalResourcesEarned() >= 4500000) {
			player.getClanManager().getClan().getClanAchievements().addClanCompletedAchievement(ClanAchievements.Achievements.RACKING_UP_RESOURCES);
			player.getClanManager().sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '" + player.getClanManager().getClan().getClanAchievements().getAchievementName(ClanAchievements.Achievements.RACKING_UP_RESOURCES) + "'.");
			player.getClanManager().getClan().getExperienceType().addHardAchievementExp(player);
		}
		/**
		 * 1.5m Resources
		 */
		if(!getClanHasCompletedAchievement(ClanAchievements.Achievements.IT_ALL_ADDS_UP) && player.getClanManager().getClan().getClanResources().getTotalResourcesEarned() >= 1500000) {
			player.getClanManager().getClan().getClanAchievements().addClanCompletedAchievement(ClanAchievements.Achievements.IT_ALL_ADDS_UP);
			player.getClanManager().sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '" + player.getClanManager().getClan().getClanAchievements().getAchievementName(ClanAchievements.Achievements.IT_ALL_ADDS_UP) + "'.");
			player.getClanManager().getClan().getExperienceType().addHardAchievementExp(player);
		}
		/**
		 * Clan Levels
		 */
		if(!getClanHasCompletedAchievement(ClanAchievements.Achievements.FROSTBITE_IS_LOVE_FROSTBITE_IS_LIFE) && player.getClanManager().getClan().getClanLevel().getClanLevel() >= 25) {
			player.getClanManager().getClan().getClanAchievements().addClanCompletedAchievement(ClanAchievements.Achievements.FROSTBITE_IS_LOVE_FROSTBITE_IS_LIFE);
			player.getClanManager().sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '" + Settings.SERVER_NAME + " is Love, " + Settings.SERVER_NAME + " is Life'.");
			player.getClanManager().getClan().getExperienceType().addEasyAchievementExp(player);
		}
		if(!getClanHasCompletedAchievement(ClanAchievements.Achievements.FROSTBITE_IS_LOVE) && player.getClanManager().getClan().getClanLevel().getClanLevel() >= 12) {
			player.getClanManager().getClan().getClanAchievements().addClanCompletedAchievement(ClanAchievements.Achievements.FROSTBITE_IS_LOVE);
			player.getClanManager().sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '" + Settings.SERVER_NAME + " is Love'.");
			player.getClanManager().getClan().getExperienceType().addHardAchievementExp(player);
		}
	}
	
	public int setClanSlayerTasksCompleted(int value) {
		return clanSlayerTasksCompleted = value;
	}
	
	public int getClanSlayerTasksCompleted() {
		return clanSlayerTasksCompleted;
	}


	public void sendGlobalMessage(Player player, String message) {
		player.getClanManager().sendGlobalMessage(message);
	}

	public ClanAchievements() {

	}


}
