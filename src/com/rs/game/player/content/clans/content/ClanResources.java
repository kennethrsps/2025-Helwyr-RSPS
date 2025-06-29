package com.rs.game.player.content.clans.content;

import java.io.Serializable;
import java.util.Vector;

import com.rs.game.player.Player;
import com.rs.game.player.content.clans.Clan;
import com.rs.game.player.content.clans.content.perks.ClanPerk;
import com.rs.utils.Utils;

/**
 * 
 * @author Frostbite
 *
 *<frostbitersps@gmail.com><skype@frostbitersps>
 */

public class ClanResources implements Serializable {

	private static final long serialVersionUID = 669446789607334378L;

	public static final int WOODCUTTING = 0, FISHING = 1, MINING = 2, SMELTING = 3;
	public final static String[] RESOURCE_NAME = {"Woodcutting", "Fishing", "Mining", "Smelting"};
	private ClanPerk perks;
	public int clanResources;
	public int totalResources;
	public int resources[];

	public ClanResources(Clan clan) {
		resources = new int[4];
		for (int type = 0; type < resources.length; type++) {
			resources[type] = 0;
		}
	}


	public static enum Captivity {
		WOODCUTTING_CAP, 
		FISHING_CAP,
		MINING_CAP, 
		SMELTING_CAP
	}

	private Vector<Captivity> resourceCaptivity = new Vector<Captivity>();

	public boolean hasCaptivity(Captivity resource) {
		return resourceCaptivity.contains(resource);
	}

	public boolean addCaptivity(Captivity resource) {
		if (resourceCaptivity.contains(resource)) {
			return false;
		}
		return resourceCaptivity.add(resource);
	}

	public boolean removeCaptivity(Captivity resource) {
		if (!resourceCaptivity.contains(resource))
			return false;
		return resourceCaptivity.remove(resource);
	}

	public String getCaptivityName(Captivity resource) {
		return Utils.formatString(resource.name());
	} 
	

	public void addResources(Player player, int type, int ramount) {
		if(getResources(ClanResources.WOODCUTTING) >= getCaptivity(player, WOODCUTTING)) {
			player.getClanManager().sendGlobalMessage("<col=ff0000>" + player.getClanName() + " has reached the captivity of Woodcutting Resources.");
			resourceCaptivity.add(Captivity.WOODCUTTING_CAP);

		} else if(getResources(ClanResources.FISHING) >= getCaptivity(player, FISHING)) {
			player.getClanManager().sendGlobalMessage("<col=ff0000>" + player.getClanName() + " has reached the captivity of Fishing Resources.");
			resourceCaptivity.add(Captivity.FISHING_CAP);

		} else if(getResources(ClanResources.MINING) >= getCaptivity(player, MINING)) {
			player.getClanManager().sendGlobalMessage("<col=ff0000>" + player.getClanName() + " has reached the captivity of Mining Resources.");
			resourceCaptivity.add(Captivity.MINING_CAP);

		} else if(getResources(ClanResources.SMELTING) >= getCaptivity(player, SMELTING)) {
			player.getClanManager().sendGlobalMessage("<col=ff0000>" + player.getClanName() + " has reached the captivity of Smelting Resources.");
			resourceCaptivity.add(Captivity.SMELTING_CAP);
		}
		resources[type] += ramount;
		clanResources += ramount;
		totalResources += ramount;
	}

	public int getCaptivity(Player player, int type) {
		switch(type) {
		case WOODCUTTING:
			if(player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.INCREASED_CAPTIVITY))
				return player.getClanManager().getClan().getMembers().size() * 1500;
			else 
				return player.getClanManager().getClan().getMembers().size() * 1000;
		case FISHING:
			if(player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.INCREASED_CAPTIVITY))
				return player.getClanManager().getClan().getMembers().size() * 1500;
			else 
				return player.getClanManager().getClan().getMembers().size() * 1000;
		case MINING:
			if(player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.INCREASED_CAPTIVITY))
				return player.getClanManager().getClan().getMembers().size() * 1500;
			else 
				return player.getClanManager().getClan().getMembers().size() * 1000;
		case SMELTING:
			if(player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.INCREASED_CAPTIVITY))
				return player.getClanManager().getClan().getMembers().size() * 1500;
			else 
				return player.getClanManager().getClan().getMembers().size() * 1000;
		}
		return player.getClanManager().getClan().getMembers().size() * 1000;
	}


	public void resetResources(Player player) {
		resourceCaptivity.removeAllElements();
		player.getClanManager().getClan().getClanResources().setResources(ClanResources.WOODCUTTING, 0);
		player.getClanManager().getClan().getClanResources().setResources(ClanResources.FISHING, 0);
		player.getClanManager().getClan().getClanResources().setResources(ClanResources.MINING, 0);
		player.getClanManager().getClan().getClanResources().setResources(ClanResources.SMELTING, 0);
		player.getClanManager().sendGlobalMessage("<col=ff0000>" + player.getClanName() + "'s Captivity has just been removed.");
	}


	public int getResourceType(int type) {
		switch(type) {
		case WOODCUTTING:
			return 0;
		case FISHING:
			return 1;
		case MINING:
			return 2;
		case SMELTING:
			return 3;
		default:
			return -1;
		}
	}

	public void setResources(int type) {
		this.resources[type] = resources[type];
	}

	public void setResources(int type, int res) {
		resources[type] = res;
	}

	public int[] getResources() {
		return resources;
	}

	public int getResources(int type) {
		return resources[type];
	}
	
	public int getClanResources() {
		return clanResources;
	}
	
	public int setTotalResourcesEarned(int amount) {
		return totalResources = amount;
	}
	
	public int getTotalResourcesEarned() {
		return totalResources;
	}
	
	public int setTotalResources(int amount) {
		return totalResources = amount;
	}

	public void sendGlobalMessage(Player player, String message) {
		player.getClanManager().sendGlobalMessage(message);
	}


	public ClanPerk getClanPerks() {
		return perks;
	}

}
