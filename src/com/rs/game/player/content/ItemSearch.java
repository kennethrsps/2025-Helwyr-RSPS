/**
 * Copyrigh 2014 Imperial Development
 *
 * This work is provided "AS IS" and WITHOUT WARRANTY of any kind, to
 * the utmost extent permitted by applicable law, neither express nor
 * implied; without malicious intent or gross negligence. In no event
 * may a licensor, author or contributor be held liable for indirect,
 * direct, other damage, loss, legal issues, or other issues arising 
 * in any way out of dealing in the work, even if advised of the 
 * possibility of such damage or existence of a defect, except proven
 * that it results out of said person's immediate fault when using the
 * work as intended. Any of the work within this project is for educational
 * purposes, and is not to be taken as a threat towards the original
 * authors of this emulator.
 * 
 * This work is not permitted to be sold by anyone except the legal
 * authors, or anyone else who has done excessive work in this project.
 * You have been warned.
 * 
 * ~ David B. Miller <chimerica@fulmination.org>
 */
package com.rs.game.player.content;

import java.util.ArrayList;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class ItemSearch {

    public static int[] COMPONENTS = { 30, 32, 34, 36, 38, 49, 51, 53, 55, 57,
	    59, 62, 64, 66, 68, 70, 72, 74, 76, 190, 79, 81, 83, 85, 88, 90,
	    92, 94, 97, 99, 101, 104, 106, 108, 110, 115, 117, 119, 121, 123,
	    125, 131, 127, 129, 2, 173, 175, 177, 182, 184, 186, 188 };

    public static void searchForItem(Player player, String itemName) {
	ArrayList<String> ITEMS = new ArrayList<String>();

	player.getPackets().sendHideIComponent(1082, 159, true);
	player.getInterfaceManager().sendInterface(1082);

	for (int i = 0; i < Utils.getInterfaceDefinitionsComponentsSize(1082); i++) {
	    player.getPackets().sendIComponentText(1082, i, "");
	}

	player.getPackets().sendIComponentText(1082, 159, Colors.cyan+"<shad=000000>Item Search Results");
	player.getPackets().sendIComponentText(1082, 41, "Item Name");
	player.getPackets().sendIComponentText(1082, 42, "ItemId");

	for (int i = 0; i < Utils.getItemDefinitionsSize(); i++) {
	    Item item = new Item(i);
	    if (item.getDefinitions().getName().toLowerCase()
		    .contains(itemName.toLowerCase())) {
		ITEMS.add(item.getName()
			+ (item.getDefinitions().isNoted() ? "(noted)," : ",")
			+ item.getId());
	    }
	}

	if (ITEMS.size() < 53) {
	    String name = "NULL";
	    String id = "-1";
	    for (int i = 0; i < ITEMS.size(); i++) {
		name = ITEMS.get(i).split(",")[0];
		id = ITEMS.get(i).split(",")[1];
		player.getPackets().sendIComponentText(1082, COMPONENTS[i],
			Utils.formatString(name));
		player.getPackets().sendIComponentText(1082, COMPONENTS[i] + 1,
			id);
	    }
	    player.getPackets().sendIComponentText(
		    1082,
		    11,
		    "Found " + ITEMS.size() + " results for the item "
			    + Utils.formatString(itemName) + ".");
	} else {
	    player.getPackets().sendIComponentText(
		    1082,
		    11,
		    "Found to many results for the item "
			    + Utils.formatString(itemName)
			    + ". Refine your search.");
	}
    }

}
