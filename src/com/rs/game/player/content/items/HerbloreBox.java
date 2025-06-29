package com.rs.game.player.content.items;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

/**
 * Handles the Herblore Box item.
 * @author Zeus
 */
public class HerbloreBox {

	/**
	 * Handles the item.
	 * @param player The player.
	 */
	public static void open(Player player) {
		if (player.getInventory().deleteOneItem(new Item(3062))) {
    		player.addItem((Utils.random(1000) < 800 ? COMMON[Utils.random(COMMON.length)] 
    				: (Utils.random(1000) >= 800 && Utils.random(1000) < 935 ? 
    						UNCOMMON[Utils.random(UNCOMMON.length)] : RARES[Utils.random(RARES.length)])));
    		player.addItem((Utils.random(1000) < 800 ? COMMON[Utils.random(COMMON.length)] 
    				: (Utils.random(1000) >= 800 && Utils.random(1000) < 935 ? 
    						UNCOMMON[Utils.random(UNCOMMON.length)] : RARES[Utils.random(RARES.length)])));
    		player.addItem((Utils.random(1000) < 800 ? COMMON[Utils.random(COMMON.length)] 
    				: (Utils.random(1000) >= 800 && Utils.random(1000) < 935 ? 
    						UNCOMMON[Utils.random(UNCOMMON.length)] : RARES[Utils.random(RARES.length)])));
    		player.addItem((Utils.random(1000) < 800 ? COMMON[Utils.random(COMMON.length)] 
    				: (Utils.random(1000) >= 800 && Utils.random(1000) < 935 ? 
    						UNCOMMON[Utils.random(UNCOMMON.length)] : RARES[Utils.random(RARES.length)])));
    		player.addItem((Utils.random(1000) < 800 ? COMMON[Utils.random(COMMON.length)] 
    				: (Utils.random(1000) >= 800 && Utils.random(1000) < 935 ? 
    						UNCOMMON[Utils.random(UNCOMMON.length)] : RARES[Utils.random(RARES.length)])));
    		player.addItem((Utils.random(1000) < 800 ? COMMON[Utils.random(COMMON.length)] 
    				: (Utils.random(1000) >= 800 && Utils.random(1000) < 935 ? 
    						UNCOMMON[Utils.random(UNCOMMON.length)] : RARES[Utils.random(RARES.length)])));
    		player.addItem((Utils.random(1000) < 800 ? COMMON[Utils.random(COMMON.length)] 
    				: (Utils.random(1000) >= 800 && Utils.random(1000) < 935 ? 
    						UNCOMMON[Utils.random(UNCOMMON.length)] : RARES[Utils.random(RARES.length)])));
    		player.addItem((Utils.random(1000) < 800 ? COMMON[Utils.random(COMMON.length)] 
    				: (Utils.random(1000) >= 800 && Utils.random(1000) < 935 ? 
    						UNCOMMON[Utils.random(UNCOMMON.length)] : RARES[Utils.random(RARES.length)])));
    		player.addItem((Utils.random(1000) < 800 ? COMMON[Utils.random(COMMON.length)] 
    				: (Utils.random(1000) >= 800 && Utils.random(1000) < 935 ? 
    						UNCOMMON[Utils.random(UNCOMMON.length)] : RARES[Utils.random(RARES.length)])));
    		player.addItem((Utils.random(1000) < 800 ? COMMON[Utils.random(COMMON.length)] 
    				: (Utils.random(1000) >= 800 && Utils.random(1000) < 935 ? 
    						UNCOMMON[Utils.random(UNCOMMON.length)] : RARES[Utils.random(RARES.length)])));
    	} else
    		player.sendMessage("Box Reward error. Go cheat elsewhere you prick :).");
	}
	
	/**
	 * Enum containing all of the common rewards.
	 */
	private static Item[] COMMON = { new Item(200), new Item(202), new Item(204), new Item(206), new Item(5096, Utils.random(1, 3))
			, new Item(5097, Utils.random(1, 3)), new Item(5098, Utils.random(1, 3)), new Item(5099, Utils.random(1, 3)),
			new Item(200), new Item(202), new Item(204), new Item(206)
			, new Item(5100, Utils.random(1, 3)), new Item(5101, Utils.random(1, 3)), new Item(5102, Utils.random(1, 3))
			, new Item(5103, Utils.random(1, 3)), new Item(5104, Utils.random(1, 3)), new Item(5105, Utils.random(1, 3))
			, new Item(5106, Utils.random(1, 3)), new Item(5280, Utils.random(1, 3)), new Item(5281, Utils.random(1, 3)),
			new Item(200), new Item(202), new Item(204), new Item(206)
			, new Item(5282, Utils.random(1, 3)), new Item(5283, Utils.random(1, 3)), new Item(5284, Utils.random(1, 3))
			, new Item(5291, Utils.random(1, 3)), new Item(5292, Utils.random(1, 3)), new Item(5293, Utils.random(1, 3))
			, new Item(5294, Utils.random(1, 3)), new Item(5305, Utils.random(1, 3)), new Item(5306, Utils.random(1, 3)),
			new Item(200), new Item(202), new Item(204), new Item(206)
			, new Item(5307, Utils.random(1, 3)), new Item(5308, Utils.random(1, 3)), new Item(5309, Utils.random(1, 3))
			, new Item(5310, Utils.random(1, 3)), new Item(5311, Utils.random(1, 3)), new Item(5312, Utils.random(1, 3))
			, new Item(5318, Utils.random(1, 3)), new Item(5319, Utils.random(1, 3)), new Item(5320, Utils.random(1, 3)),
			new Item(200), new Item(202), new Item(204), new Item(206)};
	
	/**
	 * Enum containing all of the uncommon rewards.
	 */
	 private static Item[] UNCOMMON = { new Item(210), new Item(212), new Item(214), new Item(216), new Item(5285, Utils.random(1, 3))
			 , new Item(5286, Utils.random(1, 3)), new Item(5287, Utils.random(1, 3)), new Item(5288, Utils.random(1, 3)),
			 new Item(210), new Item(212), new Item(214), new Item(216)
			 , new Item(5289, Utils.random(1, 3)), new Item(5290, Utils.random(1, 3)), new Item(5297, Utils.random(1, 3))
			 , new Item(5298, Utils.random(1, 3)), new Item(5299, Utils.random(1, 3)), new Item(5300, Utils.random(1, 3))
			 , new Item(5301, Utils.random(1, 3)), new Item(5302, Utils.random(1, 3)), new Item(5313, Utils.random(1, 3)),
			 new Item(210), new Item(212), new Item(214), new Item(216)
			 , new Item(5314, Utils.random(1, 3)), new Item(5321, Utils.random(1, 3)), new Item(5322, Utils.random(1, 3))
			 , new Item(5323, Utils.random(1, 3)), new Item(5324, Utils.random(1, 3)),
			 new Item(210), new Item(212), new Item(214), new Item(216)};

	/**
	 * Enum containing all of the rare rewards.
	 */
	 private static Item[] RARES = { new Item(208), new Item(218), new Item(220), new Item(5295, Utils.random(1, 3))
			 , new Item(5296, Utils.random(1, 3)), new Item(5303, Utils.random(1, 3)), new Item(5304, Utils.random(1, 3))
			 , new Item(5315, Utils.random(1, 3)), new Item(5316, Utils.random(1, 3)),
			 new Item(208), new Item(218), new Item(220)};

}