package com.rs.game.player.content.clans.interfaces;

import com.rs.game.player.Player;
import com.rs.game.player.content.clans.content.perks.ClanPerk;

public class ClanPerkInterface {

	public static int inter = 1156;

	public static void sendInterface(Player player) {
		player.getInterfaceManager().sendInterface(inter);
		player.getPackets().sendIComponentText(inter, 190, player.getClanName() + "'s Perks");

		player.getPackets().sendIComponentText(inter, 108, player.getClanManager().getClan().getClanPerks().getPerkName(ClanPerk.Perks.INCREASED_EXPERIENCE));
		player.getPackets().sendIComponentText(inter, 109, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 3 ? "<col=269C0E>Reach Level 3." : "<col=ff0000>Reach Level 3."));
		player.getPackets().sendIComponentText(inter, 90, (player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.INCREASED_EXPERIENCE) ? "<col=269C0E>Unlocked" : "<col=ff0000>Locked"));

		player.getPackets().sendIComponentText(inter, 113, player.getClanManager().getClan().getClanPerks().getPerkName(ClanPerk.Perks.INCREASED_RESOURCES));
		player.getPackets().sendIComponentText(inter, 114, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 5 ? "<col=269C0E>Reach Level 5." : "<col=ff0000>Reach Level 5."));
		player.getPackets().sendIComponentText(inter, 206, (player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.INCREASED_RESOURCES) ? "<col=269C0E>Unlocked" : "<col=ff0000>Locked"));

		player.getPackets().sendIComponentText(inter, 137, player.getClanManager().getClan().getClanPerks().getPerkName(ClanPerk.Perks.INCREASED_REVENUE));
		player.getPackets().sendIComponentText(inter, 138, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 8 ? "<col=269C0E>Reach Level 8." : "<col=ff0000>Reach Level 8."));
		player.getPackets().sendIComponentText(inter, 254, (player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.INCREASED_REVENUE) ? "<col=269C0E>Unlocked" : "<col=ff0000>Locked"));

		player.getPackets().sendIComponentText(inter, 110, player.getClanManager().getClan().getClanPerks().getPerkName(ClanPerk.Perks.INCREASED_CAPTIVITY));
		player.getPackets().sendIComponentText(inter, 111, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 10 ? "<col=269C0E>Reach Level 10." : "<col=ff0000>Reach Level 10."));
		player.getPackets().sendIComponentText(inter, 200, (player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.INCREASED_CAPTIVITY) ? "<col=269C0E>Unlocked" : "<col=ff0000>Locked"));

		player.getPackets().sendIComponentText(inter, 116, player.getClanManager().getClan().getClanPerks().getPerkName(ClanPerk.Perks.THICK_SKIN));
		player.getPackets().sendIComponentText(inter, 117, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 12 ? "<col=269C0E>Reach Level 12." : "<col=ff0000>Reach Level 12."));
		player.getPackets().sendIComponentText(inter, 212, (player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.THICK_SKIN) ? "<col=269C0E>Unlocked" : "<col=ff0000>Locked"));

		player.getPackets().sendIComponentText(inter, 134, player.getClanManager().getClan().getClanPerks().getPerkName(ClanPerk.Perks.MOBILE_CLAN_BANKING));
		player.getPackets().sendIComponentText(inter, 135, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 14 ? "<col=269C0E>Reach Level 14." : "<col=ff0000>Reach Level 14."));
		player.getPackets().sendIComponentText(inter, 248, (player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.MOBILE_CLAN_BANKING) ? "<col=269C0E>Unlocked" : "<col=ff0000>Locked"));

		player.getPackets().sendIComponentText(inter, 122, player.getClanManager().getClan().getClanPerks().getPerkName(ClanPerk.Perks.BARTERING));
		player.getPackets().sendIComponentText(inter, 123, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 15 ? "<col=269C0E>Reach Level 15." : "<col=ff0000>Reach Level 15."));
		player.getPackets().sendIComponentText(inter, 230, (player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.BARTERING) ? "<col=269C0E>Unlocked" : "<col=ff0000>Locked"));

		/**
		 * Tier_2_Increased_Experience
		 */
		player.getPackets().sendIComponentText(inter, 128, player.getClanManager().getClan().getClanPerks().getPerkName(ClanPerk.Perks.TIER_2_INCREASED_EXPERIENCE));
		player.getPackets().sendIComponentText(inter, 129, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 17 && player.getClanManager().getClan().getClanResources().getClanResources() >= 15000 ? "<col=269C0E>Requires Level 17 and 15000 Clan Resources." : "<col=ff0000>Requires Level 17 and 15000 Clan Resources."));
		if(!player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.TIER_2_INCREASED_EXPERIENCE)) {
			player.getPackets().sendIComponentText(inter, 236, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 17 && player.getClanManager().getClan().getClanResources().getClanResources() >= 15000 ? "<col=269C0E>Buyable" : "<col=ff0000>Locked"));
		} 
		if(player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.TIER_2_INCREASED_EXPERIENCE)) {
			player.getPackets().sendIComponentText(inter, 129, "<col=269C0E>Requires Level 17 and 15000 Clan Resources.");
			player.getPackets().sendIComponentText(inter, 236, "<col=269C0E>Unlocked");
		}
		if(!player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.TIER_2_INCREASED_EXPERIENCE)) {
			/**
			 * Tier_2_Increased_Resources
			 */
			player.getPackets().sendIComponentText(inter, 125, player.getClanManager().getClan().getClanPerks().getPerkName(ClanPerk.Perks.TIER_2_INCREASED_RESOURCES));
			player.getPackets().sendIComponentText(inter, 126, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 18 && player.getClanManager().getClan().getClanResources().getClanResources() >= 20000 ? "<col=269C0E>Requires Level 18 and 20000 Clan Resources." : "<col=ff0000>Requires Level 18 and 20000 Clan Resources."));
			if(!player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.TIER_2_INCREASED_RESOURCES)) {
				player.getPackets().sendIComponentText(inter, 224, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 18 && player.getClanManager().getClan().getClanResources().getClanResources() >= 20000 ? "<col=269C0E>Buyable" : "<col=ff0000>Locked"));
			}
			if(player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.TIER_2_INCREASED_RESOURCES)) {
				player.getPackets().sendIComponentText(inter, 126, "<col=269C0E>Requires Level 18 and 20000 Clan Resources.");
				player.getPackets().sendIComponentText(inter, 224, "<col=269C0E>Unlocked");
			}

			/**
			 * Tier_2_Increased_Revenue
			 */
			player.getPackets().sendIComponentText(inter, 143, player.getClanManager().getClan().getClanPerks().getPerkName(ClanPerk.Perks.TIER_2_INCREASED_REVENUE));
			player.getPackets().sendIComponentText(inter, 144, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 19 && player.getClanManager().getClan().getClanResources().getClanResources() >= 17500 ? "<col=269C0E>Requires Level 19 and 17500 Clan Resources." : "<col=ff0000>Requires Level 19 and 17500 Clan Resources."));
			if(!player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.TIER_2_INCREASED_REVENUE)) {
				player.getPackets().sendIComponentText(inter, 266, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 19 && player.getClanManager().getClan().getClanResources().getClanResources() >= 17500 ? "<col=269C0E>Buyable" : "<col=ff0000>Locked"));
			}
			if(player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.TIER_2_INCREASED_REVENUE)) {
				player.getPackets().sendIComponentText(inter, 144, "<col=269C0E>Requires Level 19 and 17500 Clan Resources.");
				player.getPackets().sendIComponentText(inter, 266, "<col=269C0E>Unlocked");
			}

			/**
			 * Tier_2_Thick_Skin
			 */
			player.getPackets().sendIComponentText(inter, 146, player.getClanManager().getClan().getClanPerks().getPerkName(ClanPerk.Perks.TIER_2_THICK_SKIN));
			player.getPackets().sendIComponentText(inter, 147, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 20 && player.getClanManager().getClan().getClanResources().getClanResources() >= 12750 ? "<col=269C0E>Requires Level 20 and 12750 Clan Resources." : "<col=ff0000>Requires Level 20 and 12750 Clan Resources."));
			if(!player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.TIER_2_THICK_SKIN)) {
				player.getPackets().sendIComponentText(inter, 272, (player.getClanManager().getClan().getClanLevel().getClanLevel() >= 20 && player.getClanManager().getClan().getClanResources().getClanResources() >= 12750 ? "<col=269C0E>Buyable" : "<col=ff0000>Locked"));
			}
			if(player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.TIER_2_THICK_SKIN)) {
				player.getPackets().sendIComponentText(inter, 147, "<col=269C0E>Requires Level 20 and 12750 Clan Resources.");
				player.getPackets().sendIComponentText(inter, 272, "<col=269C0E>Unlocked");
			}
		}

	}

}
