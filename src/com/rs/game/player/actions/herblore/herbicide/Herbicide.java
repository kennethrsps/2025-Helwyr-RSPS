package com.rs.game.player.actions.herblore.herbicide;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;

public class Herbicide {
	
	private static final String[] HERB_NAMES = new String[] {"Guam leave", "Marrentill", "Tarromin", "Harralander", "Ranarr", "Toadflax", "Spirit weed", "Irit", "Wergali", "Avantoe", 
			"Kwuarm", "Snapdragon", "Cadantine", "Lantadyme", "Dwarf weed", "Fellstalk", "Torstol"};
	
	private static final int[] HERBS = new int[] { 249, 251, 253, 255, 257, 2998, 12172, 259, 14854, 261, 263, 3000, 265, 2481, 267, 21624, 269 };

	public static void openHerbicide(Player player) {
		for (int i = 0; i < player.herbicideSettings.length; i++) {
			if (player.herbicideSettings[i]) 
				player.getPackets().sendIComponentSprite(1006, 32 + (i == 16 ? 18 : i), 2548);
			if (i == 16)
				player.getPackets().sendGlobalConfig(1605, ItemDefinitions.getItemDefinitions(HERBS[15]).getValue());
			else 
				player.getPackets().sendGlobalConfig(1336 + i, ItemDefinitions.getItemDefinitions(HERBS[i]).getValue());
		}
		player.getInterfaceManager().sendInterface(1006);
	}
	
	public static void handleHerbicide(Player player, int componentId) {
		if (componentId >= 32 && componentId <= 50) {
			int id = componentId == 50 ? 16 : (componentId - 32);
			player.herbicideSettings[id] = !player.herbicideSettings[id];
			player.sendMessage(HERB_NAMES[id] + (player.herbicideSettings[id] ? "s are now being burnt for 2x cleaning experience." : "s are no longer being burnt for 2x cleaning experience."));
			player.getPackets().sendIComponentSprite(1006, componentId, player.herbicideSettings[id] ? 2548 : 2549);
		}
	}
	
	public static boolean handleDrop(Player player, Item item) {
		int i = 0;
		for (HerbicideSettings settings : HerbicideSettings.values()) {
			if (settings.isHerb(item.getId()) && player.herbicideSettings[i]) {
				player.getSkills().addXp(Skills.HERBLORE, settings.getExperience() * item.getAmount());
				player.getPackets().sendGameMessage("The herbicide instantly incinerates the" + item.getName().replaceAll("Grimy", "").replaceAll("Clean", "") + ".", true);
				return true;
			}
			i++;
		}
		return false;
	}
	
}
