package com.rs.game.worldlist;

import java.util.HashMap;

public class WorldList {

    public static final HashMap<Integer, WorldEntry> WORLDS = new HashMap<Integer, WorldEntry>();

    public static WorldEntry getWorld(int worldId) {
    	return WORLDS.get(worldId);
    }

    // String activity, String ip, int countryId, String countryName, boolean members
    public static void init() {
    	WORLDS.put(1, new WorldEntry("World 1 - Economy", "127.0.0.1", 191, "Sweden", true));
    	WORLDS.put(2, new WorldEntry("World 2 - PvP", "127.0.0.1", 191, "Sweden", true));
    }
}