package com.rs.utils;

import java.io.File;
import java.util.concurrent.CopyOnWriteArrayList;

import com.rs.game.player.Player;

public final class IPMute {

    public static CopyOnWriteArrayList<String> muteipList;

    private static final String PATH = "data/punishments/IPMute.Zeus";
    private static boolean edited;

    public static void checkCurrent() {
	for (String list : muteipList) {
	    System.out.println(list);
	}
    }

    public static CopyOnWriteArrayList<String> getList() {
	return muteipList;
    }

    @SuppressWarnings("unchecked")
    public static void init() {
	File file = new File(PATH);
	if (file.exists())
	    try {
		muteipList = (CopyOnWriteArrayList<String>) SerializableFilesManager
			.loadSerializedFile(file);
		return;
	    } catch (Throwable e) {
		Logger.handle(e);
	    }
	muteipList = new CopyOnWriteArrayList<String>();
    }

    public static void ipMute(Player player) {
    	player.setMuted(Utils.currentTimeMillis() + (48 * 60 * 60 * 1000));
    	muteipList.add(player.getSession().getIP());
    	edited = true;
    	save();
    }

    public static boolean isMuted(String ip) {
	return muteipList.contains(ip);
    }

    public static final void save() {
		if (!edited)
		    return;
		try {
		    SerializableFilesManager.storeSerializableClass(muteipList, new File(PATH));
		    edited = false;
		    Logger.log("Saved "+muteipList.size()+" muted IP's!");
		} catch (Throwable e) {
		    Logger.handle(e);
		}
    }

    public static void unmute(Player player) {
    	player.setMuted(0);
    	muteipList.remove(player.getLastIP());
    	edited = true;
    	save();
    }
}