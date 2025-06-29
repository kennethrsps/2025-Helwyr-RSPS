package com.rs.game.player.content.clans.citadels;

import java.io.Serializable;
import java.util.ArrayList;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.clans.Clan;
import com.rs.game.player.content.clans.citadels.region.generation.CitadelGeneration;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Logger;

/**
 * 
 * @author Frostbite
 * 
 *         <frostbitersps@gmail.com><skype@frostbitersps>
 */

public class ClanCitadel implements Serializable {

	/**
	 * 5607 4895 tier 4 Left side portal - tier 3 right side protal - tier 2
	 */

	private static final long serialVersionUID = -758157165250863955L;


	private final ArrayList<Player> members = new ArrayList<Player>();
	private boolean citadelIsNightTime;
	public int[] boundChuncks;
	private transient boolean citadelGenerated;
	
	/**
	 * Animations
	 */

	public static final Animation FURANCE_ANIMATION = new Animation(6361);
	
	public ClanCitadel(Clan clan) {
	}


	public boolean getCitadelIsNightTime() {
		return citadelIsNightTime;
	}

	public boolean setCitadelIsNightTime(boolean value) {
		return citadelIsNightTime = value;
	}

	public ArrayList<Player> getMembersInCitadel() {
		return members;
	}

	public void removeMember(Player player) {
		members.remove(player);
		checkMembersInCitadel(player);
		player.getInterfaceManager().sendSquealOfFortune();
	}

	public void addMember(Player player) {
		members.add(player);
		checkMembersInCitadel(player);
	}

	public void loggedOut(Player player) {
		members.remove(player);
		checkMembersInCitadel(player);
	}

	public void checkMembersInCitadel(Player player) {
		if (members.isEmpty() == true) {
			CitadelGeneration.destroyCitadel(player);
		}
	}

	public void kickAll(Player player) {
	}

	public void removeFromCitadel(Player player) {
		members.remove(player);
		checkMembersInCitadel(player);
		player.sm("You have been removed from the Clan Citadel.");
		player.getInterfaceManager().sendSquealOfFortune();
		player.getControlerManager().removeControlerWithoutCheck();
	}

	public void leaveCitadel(Player player) {
		player.setNextWorldTile(new WorldTile(player.getHomeTile()));
		members.remove(player);
		checkMembersInCitadel(player);
		player.sm("You have left the Clan Citadel.");
		player.getInterfaceManager().sendSquealOfFortune();
		player.getControlerManager().removeControlerWithoutCheck();
		for (Player members : getMembersInCitadel()) {
			members.getPackets().sendGameMessage(
					"[" + player.getClanManager().getRankByName(player)
					+ "]"
					+ player.getDisplayName()
					+ " has just left the Clan Citadel!");
		}
	}

	public void enterClanCitadel(Player player) {
		members.add(player);
		joinCitadel(player);
		for (Player members : getMembersInCitadel()) {
			members.getPackets().sendGameMessage(
					"[" + player.getClanManager().getRankByName(player)
					+ "]"
					+ player.getDisplayName()
					+ " has just joined the Clan Citadel!");
			
		}
	}
	public void enterClanBank(Player player) {
		members.add(player);
		EnterBank(player);
		for (Player members : getMembersInCitadel()) {
			members.getPackets().sendGameMessage(
					"[" + player.getClanManager().getRankByName(player)
					+ "]"
					+ player.getDisplayName()
					+ " has entered the Clan Bank!");
			
		}
	}

	public boolean joinCitadel(final Player player) {
		player.getControlerManager().startControler("CitadelControler", this);
		if (player.getClanManager().getClan().getClanCitadel().isCitadelGenerated() == true) {
			teleportPlayerToCitadel(player);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.lock(0);
				}
			}, 4);
		} else {
			CoresManager.slowExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						getCitadelGenerationLayout(player);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			});
		}
		return true;
	}
	public boolean EnterBank(final Player player) {
		player.getControlerManager().startControler("CitadelControler", this);
		if (player.getClanManager().getClan().getClanCitadel().isCitadelGenerated() == true) {
			teleportPlayertoClanbank(player);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.lock(0);
				}
			}, 4);
		} else {
			CoresManager.slowExecutor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						getCitadelGenerationLayout(player);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			});
		}
		return true;
	}
	public void teleportPlayertoClanbank(Player player) {
		player.setNextWorldTile(new WorldTile(player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[0] * 5 + 38,
				player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[1] * 8 + 12, 0));
}

	public void teleportPlayerToCitadel(Player player) {
			player.setNextWorldTile(new WorldTile(player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[0] * 15 + 7,
					player.getClanManager().getClan().getClanCitadel().getBoundChuncks()[1] * 8 + 35, 0));
	}

	public void getCitadelGenerationLayout(Player player) {
			CitadelGeneration.generateDayTimeTier1Citadel(player);
			player.getInterfaceManager().closeChatBoxInterface();
			System.out.println("[Clan Citadel] - Loaded Day Time Citadel Tier 1 for "
					+ player.getClanName());
	}


	public boolean isCitadelGenerated() {
		return citadelGenerated;
	}


	public void setCitadelGenerated(boolean citadelGenerated) {
		this.citadelGenerated = citadelGenerated;
	}


	public int[] getBoundChuncks() {
		return boundChuncks;
	}


	public void setBoundChuncks(int[] boundChuncks) {
		this.boundChuncks = boundChuncks;
	}


	public static void startConfigs(Player player) {
		ClanCitadel citadel = player.getClanManager().getClan().getClanCitadel();
		if(citadel.isCitadelGenerated())
		player.getPackets().sendObjectAnimation(new WorldObject(26198, 10, 2, citadel.boundChuncks[0] + 98, citadel.boundChuncks[1] + 136, 0), FURANCE_ANIMATION);
	}

}