package com.rs.game.player.content.slayer;

import java.io.Serializable;

import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;

/**
 * Handles Cooperative Slayer and all necessary components.
 * @author Zeus.
 */
public class CooperativeSlayer implements Serializable {

private static final long serialVersionUID = -5833463661237303707L;
	
	public void handleLogout(Player player) {
		Player newPartner;
		String host;
		host = player.getSlayerHost();
		newPartner = World.getPlayerByDisplayName(host);
		if (player.hasOngoingInvite == true && newPartner != null) {
			newPartner.hasInvited = false;
			newPartner.sendMessage("<col=C43140>" + player.getDisplayName() + " has logged out, your invite request has been deleted.</col>");
		}
		resetPartner(player);
	}
	
	public void sendInvite(final Player player) {
		if (player.isIronMan() || player.isHCIronMan()) {
			player.sendMessage("Ironmen cannot do Co-op slayer.");
			return;
		}
		player.sendMessage("<col=BF00C9>You have received a slayer invitation from " + player.getSlayerHost() + "</col>");
		player.hasHost = true;
		player.getInterfaceManager().sendInterface(1310);
		Player host = World.getPlayerByDisplayName(player.getSlayerHost());
		player.getPackets().sendIComponentText(1310, 8, ""+host.getSkills().getLevelForXp(Skills.SLAYER));
		player.getPackets().sendIComponentText(1310, 6, ""+host.getDisplayName());
		player.getPackets().sendIComponentText(1310, 10, ""+host.getSkills().getCombatLevelWithSummoning());
	}

	public void handleInviteButtons(Player invited, int interfaceId, int componentId) {
		Player newPartner;
		String host;
		host = invited.getSlayerHost();
		newPartner = World.getPlayerByDisplayName(host);
		if (interfaceId == 1310) {
			if (newPartner == null) {
				invited.hasOngoingInvite = false;
				invited.closeInterfaces();
				invited.hasHost = false;
				invited.hasInvited = false;
				invited.hasGroup = false;
				invited.setSlayerHost(null);
				invited.setSlayerInvite(null);
				invited.setSlayerPartner(null);
				invited.sendMessage("<col=C43140>Your potential partner has not been found, disbanding party.</col>");
				return;
			}
			switch (componentId) {
			case 0:
				invited.sendMessage("<col=22C78D>Accepted " + invited.getSlayerHost() + "'s invitation, you are now their Slayer partner.</col>");
				invited.setSlayerPartner(host);
				invited.hasGroup = true;
				invited.hasOngoingInvite = false;
				invited.hasInvited = false;
				newPartner.hasInvited = false;
				newPartner.sendMessage("<col=22C78D>Your invitation to " + newPartner.getSlayerInvite() + " has been accepted, you are now their slayer partner.</col>");
				newPartner.setSlayerPartner(newPartner.getSlayerInvite());
				newPartner.hasGroup = true;
				invited.closeInterfaces();
				break;
			case 1:
				invited.sendMessage("<col=C43140>You have declined " + invited.getSlayerHost() + "'s invitation.</col>");
				newPartner.sendMessage("<col=C43140>Your invitation has been declined.</col>");
				invited.setSlayerHost(null);
				newPartner.setSlayerInvite(null);
				invited.hasHost = false;
				newPartner.hasInvited = false;
				invited.hasOngoingInvite = false;
				invited.closeInterfaces();
				break;
			}
		}
	}
	
	public void handleCoOpSlayerInterface(Player player, int componentId) {
		Player newPartner;
		String host;
		host = player.getSlayerHost();
		newPartner = World.getPlayerByDisplayName(host);
		switch (componentId) {
		case 20:
			player.sendMessage("Use an Enchanted gem, a Slayer helmet or a Ring of slaying on the player to invite!");
			break;
		case 22:
			if (player.hasOngoingInvite == false) {
				player.sendMessage("You have not been invited by anyone yet!");
				return;
			}
			player.getInterfaceManager().sendInterface(1310);
			player.getPackets().sendIComponentText(1310, 8, ""+player.getSkills().getLevelForXp(Skills.SLAYER));
			player.getPackets().sendIComponentText(1310, 6, ""+player.getDisplayName());
			player.getPackets().sendIComponentText(1310, 10, ""+player.getSkills().getCombatLevelWithSummoning());
			break;
		case 24:
			if (player.hasGroup == false) {
				player.sendMessage("You're not in a Slayer party at the moment.");
				return;
			}
			player.sendMessage("<col=C43140>You have left your current Slayer party.</col>");
			player.setSlayerHost(null);
			player.setSlayerPartner(null);
			player.setSlayerInvite(null);
			player.hasHost = false;
			player.hasGroup = false;
			player.hasOngoingInvite = false;
			if (World.containsPlayer(host)) {
				newPartner.sendMessage("<col=C43140>Your Slayer party has been disbaned.</col>");
				newPartner.setSlayerInvite(null);
				newPartner.setSlayerPartner(null);
				newPartner.setSlayerHost(null);
				newPartner.hasInvited = false;
				newPartner.hasGroup = false;
				newPartner.hasHost = false;
			}
			player.closeInterfaces();
			break;
		}
	}
	
	public void resetPartner(Player player) {
		player.sendMessage("<col=C43140>Your Slayer party has been disbanded.</col>");
		player.hasHost = false;
		player.hasGroup = false;
		player.hasInvited = false;
		player.hasOngoingInvite = false;
		player.setSlayerPartner(null);
		player.setSlayerInvite(null);
		player.setSlayerHost(null);
	}
}