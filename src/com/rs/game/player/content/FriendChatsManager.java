package com.rs.game.player.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.activites.clanwars.ClanWars;
import com.rs.game.player.FriendsIgnores;
import com.rs.game.player.Player;
import com.rs.stream.OutputStream;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public class FriendChatsManager {

	private static HashMap<String, FriendChatsManager> cachedFriendChats;
	private String owner;
	private String ownerDisplayName;
	private FriendsIgnores settings;
	private CopyOnWriteArrayList<Player> players;
	private ConcurrentHashMap<String, Long> bannedPlayers;

	private byte[] dataBlock;

	/**
	 * The clan wars instance (if the clan is in a war).
	 */
	private ClanWars clanWars;

	private FriendChatsManager(Player player) {
		owner = player.getUsername();
		ownerDisplayName = player.getDisplayName();
		settings = player.getFriendsIgnores();
		players = new CopyOnWriteArrayList<Player>();
		bannedPlayers = new ConcurrentHashMap<String, Long>();
	}

	public static void destroyChat(Player player) {
		synchronized (cachedFriendChats) {
			FriendChatsManager chat = cachedFriendChats.get(player.getUsername());
			if (chat == null)
				return;
			chat.destroyChat();
			player.disableLootShare();
			player.getPackets().sendGameMessage("Your friends chat channel has now been disabled!");
		}
	}

	public static void init() {
		cachedFriendChats = new HashMap<String, FriendChatsManager>();
	}

	public static void joinChat(String ownerName, Player player) {
		synchronized (cachedFriendChats) {
			if (player.getCurrentFriendChat() != null)
				return;
			player.sendMessage("Attempting to join channel...", true);
			String formatedName = Utils.formatPlayerNameForProtocol(ownerName);
			FriendChatsManager chat = cachedFriendChats.get(formatedName);
			if (chat == null) {
				Player owner = World.getPlayerByDisplayName(ownerName);
				if (owner == null) {
					if (!SerializableFilesManager.containsPlayer(formatedName)) {
						player.sendMessage("The channel you tried to join does not exist.");
						return;
					}
					owner = SerializableFilesManager.loadPlayer(formatedName);
					if (owner == null) {
						player.sendMessage("The channel you tried to join does not exist.");
						return;
					}
					owner.setUsername(formatedName);
				}
				FriendsIgnores settings = owner.getFriendsIgnores();
				if (!settings.hasFriendChat()) {
					player.sendMessage("The channel you tried to join does not exist.");
					return;
				}
				if (!player.getUsername().equals(ownerName) && !settings.hasRankToJoin(player.getUsername())
						&& player.getRights() < 2) {
					player.sendMessage("You do not have a enough rank to join this friends chat channel.");
					return;
				}
				chat = new FriendChatsManager(owner);
				cachedFriendChats.put(ownerName, chat);
				chat.joinChatNoCheck(player);
			} else
				chat.joinChat(player);
		}
	}

	public static void linkSettings(Player player) {
		synchronized (cachedFriendChats) {
			FriendChatsManager chat = cachedFriendChats.get(player.getUsername());
			if (chat == null)
				return;
			chat.settings = player.getFriendsIgnores();
		}
	}

	public static void refreshChat(Player player) {
		synchronized (cachedFriendChats) {
			FriendChatsManager chat = cachedFriendChats.get(player.getUsername());
			if (chat == null)
				return;
			chat.refreshChannel();
		}
	}

	public static List<Player> getLootSharingPeople(Player player) {
		if (!player.isToogleLootShare())
			return null;
		FriendChatsManager chat = player.getCurrentFriendChat();
		if (chat == null)
			return null;
		List<Player> players = new ArrayList<Player>();
		for (Player p2 : players) {
			if (p2.isToogleLootShare() && p2.withinDistance(player, 32))
				players.add(p2);
		}
		return players;

	}

	public void destroyChat() {
		synchronized (this) {
			for (Player player : players) {
				player.setCurrentFriendChat(null);
				player.setCurrentFriendChatOwner(null);
				player.getPackets().sendFriendsChatChannel();
				player.sendMessage("You have been removed from this channel!");
			}
		}
		synchronized (cachedFriendChats) {
			cachedFriendChats.remove(owner);
		}
	}

	public String getChannelName() {
		return settings.getChatName().replaceAll("<img=", "");
	}

	/**
	 * Gets the clanWars.
	 *
	 * @return The clanWars.
	 */
	public ClanWars getClanWars() {
		return clanWars;
	}

	/**
	 * Sets the clanWars.
	 *
	 * @param clanWars
	 *            The clanWars to set.
	 */
	public void setClanWars(ClanWars clanWars) {
		this.clanWars = clanWars;
	}

	public byte[] getDataBlock() {
		return dataBlock;
	}

	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}

	public String getOwnerName() {
		return owner;
	}

	public Player getPlayerByDisplayName(String username) {
		String formatedUsername = Utils.formatPlayerNameForProtocol(username);
		for (Player player : players) {
			if (player.getUsername().equals(formatedUsername) || player.getDisplayName().equals(username)) {
				return player;
			}
		}
		return null;
	}

	public CopyOnWriteArrayList<Player> getPlayers() {
		return players;
	}

	public int getRank(int rights, String username) {
		if (rights == 2)
			return 127;
		if (username.equals(owner))
			return 7;
		return settings.getRank(username);
	}

	public int getWhoCanKickOnChat() {
		return settings.getWhoCanKickOnChat();
	}

	private void joinChat(Player player) {
		synchronized (this) {
			if (!player.getUsername().equals(owner) && !settings.hasRankToJoin(player.getUsername())
					&& player.getRights() < 2) {
				player.sendMessage("You do not have a enough rank to join this friends chat channel.");
				return;
			}
			if (players.size() >= 200) {
				player.getPackets().sendGameMessage("This chat is full.");
				return;
			}
			Long bannedSince = bannedPlayers.get(player.getUsername());
			if (bannedSince != null) {
				if (bannedSince + 3600000 > Utils.currentTimeMillis()) {
					player.sendMessage("You have been banned from this channel.");
					return;
				}
				bannedPlayers.remove(player.getUsername());
			}
			joinChatNoCheck(player);
		}
	}

	private void joinChatNoCheck(Player player) {
		synchronized (this) {
			players.add(player);
			player.setCurrentFriendChat(this);
			player.setCurrentFriendChatOwner(owner);
			player.sendMessage("You are now talking in the friends chat channel " + settings.getChatName(), true);
			refreshChannel();
		}
	}

	public void kickPlayerFromChat(Player player, String username) {
		String name = "";
		for (char character : username.toCharArray()) {
			name += Utils.containsInvalidCharacter(character) ? " " : character;
		}
		final FriendChatsManager chat = player.getCurrentFriendChat();
		if (chat == null) {
			player.sendMessage("You need to be in a friends chat channel to use this option.");
			return;
		}
		synchronized (this) {
			int rank = getRank(player.getRights(), player.getUsername());
			if (rank < getWhoCanKickOnChat()) {
				return;
			}
			Player kicked = getPlayerByDisplayName(name);
			if (kicked == null) {
				player.sendMessage("This player is not this channel.");
				return;
			}
			if (rank <= getRank(kicked.getRights(), kicked.getUsername())) {
				return;
			}
			kicked.setCurrentFriendChat(null);
			kicked.setCurrentFriendChatOwner(null);
			players.remove(kicked);
			kicked.disableLootShare();
			bannedPlayers.put(kicked.getUsername(), Utils.currentTimeMillis());
			kicked.getPackets().sendFriendsChatChannel();
			kicked.getPackets().sendGameMessage("You have been kicked from the friends chat channel.");
			player.getPackets()
					.sendGameMessage("You have kicked " + kicked.getUsername() + " from friends chat channel.");
			chat.sendDiceMessage(player,
					"[" + settings.getChatName() + "] <col=ff0000>" + player.getDisplayName() + "</col> "
							+ "has just kicked player: <col=ff0000>" + kicked.getDisplayName()
							+ "</col> from this friends chat!");
			refreshChannel();

		}
	}

	public void leaveChat(Player player, boolean logout) {
		synchronized (this) {
			player.setCurrentFriendChat(null);
			players.remove(player);
			if (players.size() == 0) {
				synchronized (cachedFriendChats) {
					cachedFriendChats.remove(owner);
				}
			} else {
				refreshChannel();
			}
			if (!logout) {
				player.setCurrentFriendChatOwner(null);
				player.sendMessage("You have left the channel.", true);
				player.disableLootShare();
				player.getPackets().sendFriendsChatChannel();
			}
			if (clanWars != null) {
				clanWars.leave(player, false);
			}
		}
	}

	private void refreshChannel() {
		synchronized (this) {
			OutputStream stream = new OutputStream();
			stream.writeString(ownerDisplayName);
			String ownerName = Utils.formatPlayerNameForDisplay(owner);
			stream.writeByte(getOwnerDisplayName().equals(ownerName) ? 0 : 1);
			if (!getOwnerDisplayName().equals(ownerName)) {
				stream.writeString(ownerName);
			}
			stream.writeLong(Utils.stringToLong(getChannelName()));
			int kickOffset = stream.getOffset();
			stream.writeByte(0);
			stream.writeByte(getPlayers().size());
			for (Player player : getPlayers()) {
				String displayName = player.getDisplayName();
				String name = Utils.formatPlayerNameForDisplay(player.getUsername());
				stream.writeString(displayName);
				stream.writeByte(displayName.equals(name) ? 0 : 1);
				if (!displayName.equals(name)) {
					stream.writeString(name);
				}
				stream.writeShort(1);
				int rank = getRank(player.getRights(), player.getUsername());
				stream.writeByte(rank);
				stream.writeString(Settings.SERVER_NAME);
			}
			dataBlock = new byte[stream.getOffset()];
			stream.setOffset(0);
			stream.getBytes(dataBlock, 0, dataBlock.length);
			for (Player player : players) {
				dataBlock[kickOffset] = (byte) (player.getUsername().equals(owner) ? 0 : getWhoCanKickOnChat());
				player.getPackets().sendFriendsChatChannel();
			}
		}
	}

	public void sendDiceMessage(Player player, String message) {
		synchronized (this) {
			if (!player.getUsername().equals(owner) && !settings.canTalk(player) && player.getRights() < 2) {
				player.sendMessage("You do not have a enough rank to talk on this friends chat channel.");
				return;
			}
			for (Player p2 : players) {
				p2.getPackets().sendGameMessage(message);
			}
		}
	}

	public void sendMessage(Player player, String message) {
		synchronized (this) {
			if (!player.getUsername().equals(owner) && !settings.canTalk(player) && player.getRights() < 2) {
				player.sendMessage("You do not have a enough rank to talk on this friends chat channel.");
				return;
			}
			String formatedName = Utils.formatPlayerNameForDisplay(player.getUsername());
			String displayName = player.getDisplayName();
			int rights = player.getMessageIcon();
			for (Player p2 : players) {
				p2.getPackets().receiveFriendChatMessage(formatedName, displayName, rights, settings.getChatName(),
						message);
			}
		}
	}

	public void sendQuickMessage(Player player, QuickChatMessage message) {
		synchronized (this) {
			if (!player.getUsername().equals(owner) && !settings.canTalk(player) && player.getRights() < 2) {
				player.sendMessage("You do not have a enough rank to talk on this friends chat channel.");
				return;
			}
			String formatedName = Utils.formatPlayerNameForDisplay(player.getUsername());
			String displayName = player.getDisplayName();
			int rights = player.getMessageIcon();
			for (Player p2 : players) {
				p2.getPackets().receiveFriendChatQuickMessage(formatedName, displayName, rights, settings.getChatName(),
						message);
			}
		}
	}

	public void toogleLootShare(Player player) {
		if (player.getCurrentFriendChat() == null) {
			player.sendMessage("You need to be in a Friends Chat channel to activate LootShare.");
			player.refreshToogleLootShare();
			return;
		}
		if (!player.getUsername().equals(player.getCurrentFriendChat().getOwnerName())
				&& !player.getCurrentFriendChat().settings.hasRankToLootShare(player.getUsername())) {
			player.sendMessage("You must be on channel owner's Friend List to use LootShare on this channel.");
			player.refreshToogleLootShare();
			return;
		}
		player.toogleLootShare();
		if (player.isToogleLootShare()) {
			player.getPackets().sendGameMessage("LootShare has been enabled.");
			// sendMessage(player, "LootShare has been enabled.");
		} else {
			player.getPackets().sendGameMessage("LootShare has been disabled.");
			// sendMessage(player, "LootShare has been disabled.");
		}
	}
}