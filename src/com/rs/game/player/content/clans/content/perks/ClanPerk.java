package com.rs.game.player.content.clans.content.perks;

import java.io.Serializable;
import java.util.Vector;

import com.rs.game.player.Player;
import com.rs.utils.Utils;

/**
 * 
 * @author Frostbite
 *
 *<frostbitersps@gmail.com><skype@frostbitersps>
 */

public class ClanPerk implements Serializable {

	private static final long serialVersionUID = -5990125382009899068L;

	public static enum Perks {
		/**
		 * Miscellaneous 
		 */
		MOBILE_CLAN_BANKING,
		BARTERING,
		/**
		 * Experience
		 */
		INCREASED_EXPERIENCE,
		TIER_2_INCREASED_EXPERIENCE,
		TIER_3_INCREASED_EXPERIENCE,
		/**
		 * Increased Resources
		 */
		INCREASED_RESOURCES,
		TIER_2_INCREASED_RESOURCES,
		TIER_3_INCREASED_RESORUCES,
		/**
		 * Revenue
		 */
		INCREASED_REVENUE,
		TIER_2_INCREASED_REVENUE,
		TIER_3_INCREASED_REVENUE,
		/**
		 * Captivity
		 */
		INCREASED_CAPTIVITY,
		TIER_2_INCREASED_CAPTIVITY,
		TIER_3_INCREASED_CAPTIVITY,
		/**
		 * Healing
		 */
		THICK_SKIN,
		TIER_2_THICK_SKIN,
		TIER_3_THICK_SKIN,
		
		LIFE_RESTORE,
		TIER_2_LIFE_RESTORE,
		TIER_3_LIFE_RESTORE
	}

	private Vector<Perks> unlockedPerks = new Vector<Perks>();

	public boolean hasPerk(Perks perk) {
		return unlockedPerks.contains(perk);
	}

	public boolean unlockPerk(Perks perk) {
		if (unlockedPerks.contains(perk)) {
			return false;
		}
		return unlockedPerks.add(perk);
	}

	public boolean removePerk(Perks perk) {
		if (!unlockedPerks.contains(perk))
			return false;
		return unlockedPerks.remove(perk);
	}

	public String getPerkName(Perks perk) {
		return Utils.formatString(perk.name());
	}

	public void getPerkUnlockLevel(Player player) {
		if(!hasPerk(Perks.BARTERING) && player.getClanManager().getClan().getClanLevel().getClanLevel() >= 15) {
			unlockPerk(Perks.BARTERING);
			sendGlobalMessage(player, "<col=269C0E>" + player.getClanName() + " has just unlocked '" + getPerkName(Perks.BARTERING) + "'.");
			
		} else if(!hasPerk(Perks.MOBILE_CLAN_BANKING) && player.getClanManager().getClan().getClanLevel().getClanLevel() >= 14) {
			unlockPerk(Perks.MOBILE_CLAN_BANKING);
			sendGlobalMessage(player, "<col=269C0E>" + player.getClanName() + " has just unlocked '" + getPerkName(Perks.MOBILE_CLAN_BANKING) + "'.");
			
			
		} else if(!hasPerk(Perks.THICK_SKIN) && player.getClanManager().getClan().getClanLevel().getClanLevel() >= 12) {
			unlockPerk(Perks.THICK_SKIN);
			sendGlobalMessage(player, "<col=269C0E>" + player.getClanName() + " has just unlocked '" + getPerkName(Perks.THICK_SKIN) + "'.");

		} else if(!hasPerk(Perks.INCREASED_CAPTIVITY) && player.getClanManager().getClan().getClanLevel().getClanLevel() >= 10) {
			unlockPerk(Perks.INCREASED_CAPTIVITY);
			sendGlobalMessage(player, "<col=269C0E>" + player.getClanName() + " has just unlocked '" + getPerkName(Perks.INCREASED_CAPTIVITY) + "'.");

		} else if(!hasPerk(Perks.INCREASED_REVENUE) && player.getClanManager().getClan().getClanLevel().getClanLevel() >= 8) {
			unlockPerk(Perks.INCREASED_REVENUE);
			sendGlobalMessage(player, "<col=269C0E>" + player.getClanName() + " has just unlocked '" + getPerkName(Perks.INCREASED_REVENUE) + "'.");

		} else if(!hasPerk(Perks.INCREASED_RESOURCES) && player.getClanManager().getClan().getClanLevel().getClanLevel() >= 5) {
			unlockPerk(Perks.INCREASED_RESOURCES);
			sendGlobalMessage(player, "<col=269C0E>" + player.getClanName() + " has just unlocked '" + getPerkName(Perks.INCREASED_RESOURCES) + "'.");

		} else if(!hasPerk(Perks.INCREASED_EXPERIENCE) && player.getClanManager().getClan().getClanLevel().getClanLevel() >= 3) {
			unlockPerk(Perks.INCREASED_EXPERIENCE);
			sendGlobalMessage(player, "<col=269C0E>" + player.getClanName() + " has just unlocked '" + getPerkName(Perks.INCREASED_EXPERIENCE) + "'.");
		}
	}

	public void sendGlobalMessage(Player player, String message) {
		player.getClanManager().sendGlobalMessage(message);
	}
}
