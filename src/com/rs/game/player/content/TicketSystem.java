package com.rs.game.player.content;

import java.util.ArrayList;
import java.util.Iterator;

import com.rs.game.ForceTalk;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.actions.PlayerFollow;
import com.rs.utils.Colors;

public class TicketSystem {

	public static class TicketEntry {
		private Player player;
		private WorldTile tile;

		public TicketEntry(Player player) {
			this.player = player;
			this.tile = player;
		}

		public Player getPlayer() {
			return player;
		}

		public WorldTile getTile() {
			return tile;
		}
	}

	public static final ArrayList<TicketEntry> tickets = new ArrayList<TicketEntry>();

	public static void answerTicket(Player player) {
		removeTicket(player);
		filterTickets();
		if (tickets.isEmpty())
			player.sendMessage("There are currently no tickets open.");
		else if (player.getTemporaryAttributtes().get("ticketTarget") != null)
			removeTicket(player);
		while (tickets.size() > 0) {
			TicketEntry ticket = tickets.get(0);// next in line
			Player target = ticket.player;
			if (target == null)
				continue; // shouldn't happen but k
			player.getTemporaryAttributtes().put("ticketTarget", ticket);
			player.setNextWorldTile(target);
			tickets.remove(ticket);
			player.setNextForceTalk(new ForceTalk("Hello, how may I help you?"));
			player.sendMessage("You're now assisting: " + target.getDisplayName() + " (" + target.getUsername() + ").");
			player.getActionManager().setAction(new PlayerFollow(target));
			for (Player mod : World.getPlayers()) {
				if (!mod.isStaff() || mod.isStaff2())
					continue;
				mod.sendMessage("<img=15>" + Colors.blue + "<shad=00FF66>" + player.getDisplayName() + " has answered "
						+ target.getDisplayName() + "'s ticket!");
			}
			break;
		}
	}

	public static boolean canSubmitTicket() {
		filterTickets();
		return true;
	}

	public static void filterTickets() {
		for (Iterator<TicketEntry> it = tickets.iterator(); it.hasNext();) {
			TicketEntry entry = it.next();
			if (entry.player.hasFinished())
				it.remove();
		}
	}

	public static void removeTicket(Player player) {
		Object att = player.getTemporaryAttributtes().get("ticketTarget");
		if (att == null)
			return;
		TicketEntry ticket = (TicketEntry) att;
		Player target = ticket.getPlayer();
		target.setNextWorldTile(ticket.getTile());
		target.getTemporaryAttributtes().remove("ticketRequest");
		player.getTemporaryAttributtes().remove("ticketTarget");
	}

	public static void requestTicket(Player player) {
		if (player.getInterfaceManager().containsChatBoxInter() || player.getInterfaceManager().containsInventoryInter()
				|| player.getInterfaceManager().containsScreenInter()) {
			player.sendMessage("Please finish what you're doing before requesting a ticket.");
			return;
		}
		if (!canSubmitTicket() || player.getTemporaryAttributtes().get("ticketRequest") != null
				|| player.getControlerManager().getControler() != null) {
			player.sendMessage("You cannot send a ticket at the moment.");
			return;
		}
		player.getTemporaryAttributtes().put("ticketRequest", true);
		player.sendMessage(
				Colors.red + "<shad=000000>The ticket request has been sent; a staff member will be with you shortly.");
		tickets.add(new TicketEntry(player));
		for (Player mod : World.getPlayers()) {
			if (!mod.isStaff())
				continue;
			mod.sendMessage("<img=15>" + Colors.blue + "<shad=00FF66>A ticket has been submitted by "
					+ player.getDisplayName() + "! Do ::ticket to solve it!");
			mod.sendMessage("<img=15>" + Colors.blue + "<shad=00FF66>There is currently " + tickets.size()
					+ " ticket(s) active.");
		}
	}
}