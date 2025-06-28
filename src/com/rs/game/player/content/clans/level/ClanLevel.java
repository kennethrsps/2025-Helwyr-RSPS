package com.rs.game.player.content.clans.level;

import java.io.Serializable;

import com.rs.game.player.Player;
import com.rs.game.player.content.clans.Clan;
import com.rs.game.player.content.clans.content.perks.ClanPerk;

public class ClanLevel implements Serializable{

	private static final long serialVersionUID = -1418099332507531185L;

	public static int cXp[] = { 4756027, 12887656, 26141610, 42568926, 68879827, 96876249, 146078264, 178485916, 205782651, 225782629, 264862964, 298278493, 341658723, 376983673, 409782562, 
			449758262, 501728262, 55067282, 625728125, 717792652, 78478127, 867978127, 987618625, 1075000000};
	private Player player;
	private ClanPerk perks;
	public int clanXp;
	private int clanLevel = 1;

	public ClanLevel(Clan clan) {
	}


	public int getClanXp() {
		return clanXp;
	}

	public void setClanXp(int clanXp) {
		this.clanXp = clanXp;
	}

	public int getClanLevel() {
		return clanLevel;
	}

	public int[] getcXp() {
		return cXp;
	}

	public void setClanLevel(int clanLevel) {
		this.clanLevel = clanLevel;
	}


	public void addClanXp(Player player, int cxp) {
		if(clanXp >= ClanLevel.cXp[23]) 	
			return;
		cxp *= 22;
		if(player.getEquipment().getCapeId() == 20708) 
			cxp *= 1.025;
		if(player.getEquipment().getWeaponId() == 20709) 
			cxp *= 1.010;
		if(player.getClanManager().getClan().getClanPerks().hasPerk(ClanPerk.Perks.INCREASED_EXPERIENCE)) 
			cxp *= 1.50;
		player.getClanManager().getClan().getClanLevel().setClanXp(player.getClanManager().getClan().getClanLevel().getClanXp() + cxp);
		if(player.getClanManager().getClan().getClanLevel().getClanXp() >= ClanLevel.cXp[player.getClanManager().getClan().getClanLevel().getClanLevel() - 1]) {
			player.getClanManager().getClan().getClanLevel().setClanLevel(player.getClanManager().getClan().getClanLevel().getClanLevel() + 1);
			player.getClanManager().getClan().getClanLevel().achieveClanLevel(player);
			player.getClanManager().getClan().getClanPerks().getPerkUnlockLevel(player);
			player.getClanManager().getClan().getClanAchievements().checkAchievements(player);
		}
	}


	public void achieveClanLevel(Player player) {
		sendGlobalMessage(player, "<col=0840C4>" + player.getClanName() + " has just achieved level " + player.getClanManager().getClan().getClanLevel().getClanLevel() + ".");
	}

	public void gainExperience(Player player, int type) {
		//sendGlobalMessage(player, "<col=4661FA>" + player.getClanName() + " has just gained " + player.getClanManager().getClan().getExperienceType().getExperience(type) + " (+" + player.getClanManager().getClan().getExperienceType().getExperience(type) * cxp + ")" + " experience.");
		sendGlobalMessage(player, "<col=4661FA>" + player.getClanName() + " has just gained " + player.getClanManager().getClan().getExperienceType().getExperience(type) + " experience.");

	}


	public void sendGlobalMessage(Player player, String message) {
		player.getClanManager().sendGlobalMessage(message);
	}


	public void sendMessage(Player player, String message) {
		player.getPackets().sendGameMessage(message);
	}

	public void getMembers(Player player, String string) {
		player.getClanManager().getClan().getMembers();
	}

	public void getMembers() {
		player.getClanManager().getClan().getMembers();
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}


	public ClanPerk getClanPerks() {
		return perks;
	}


}
