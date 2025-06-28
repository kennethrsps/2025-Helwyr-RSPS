package com.rs.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.rs.game.player.Player;
import com.rs.game.player.content.FriendChatsManager;

public final class DisplayNames {

    private static ArrayList<String> cachedNames;

    private static final String PATH = "data/displayNames.ser";

    @SuppressWarnings("unchecked")
    public static void init() {
		File file = new File(PATH);
		if (file.exists())
		    try {
				cachedNames = (ArrayList<String>) SerializableFilesManager.loadSerializedFile(file);
				return;
		    } catch (Throwable e) {
		    	Logger.handle(e);
		    }
		cachedNames = new ArrayList<String>();
    }

    public static boolean removeDisplayName(Player player) {
		if (!player.hasDisplayName()) {
			player.sendMessage("You do not have a display name set.");
		    return false;
		}
		synchronized (cachedNames) {
		    cachedNames.remove(player.getDisplayName());
		}
		player.setDisplayName(null);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		player.getPackets().sendGameMessage("Display name successfully removed!");
		save();
		return true;
    }

    public static void save() {
		try {
		    SerializableFilesManager.storeSerializableClass(cachedNames, new File(PATH));
		} catch (IOException e) {
		    Logger.handle(e);
		}
    }

    public static boolean setDisplayName(Player player, String displayName) {
		synchronized (cachedNames) {
		    if ((SerializableFilesManager.containsPlayer(Utils.formatPlayerNameForProtocol(displayName)) 
		    		|| cachedNames.contains(displayName) || !cachedNames.add(displayName)))
		    	return false;
		    if (player.hasDisplayName())
		    	cachedNames.remove(player.getDisplayName());
		}
		player.setDisplayName(displayName);
		FriendChatsManager.refreshChat(player);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		save();
		return true;
    }
}