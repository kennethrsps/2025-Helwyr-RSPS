package com.rs.game.player.content.miscellania.resources;

import com.rs.game.item.Item;
import com.rs.utils.Utils;

/**
 * 
 * @author Frostbite<Abstract>
 * @contact<skype;frostbitersps><email;frostbitersps@gmail.com>
 */

public enum ResourceConstants {
	

		ORE(new Item[] { new Item(452, Utils.random(4, 8)), new Item(450, Utils.random(12, 24)), new Item(448, Utils.random(36, 72)), new Item(445, Utils.random(118, 286)), new Item(453, Utils.random(236, 492))}),
		
		WOOD(new Item[] { new Item(1514, Utils.random(12, 24)), new Item(1516, Utils.random(24, 36)), new Item(1518, Utils.random(135, 250)), new Item(1520, Utils.random(275, 550)), new Item(1512, Utils.random(1512, Utils.random(325, 732))) }),
		
		FISH(new Item[] { new Item(384, Utils.random(30, 55)), new Item(378, Utils.random(104, 225)), new Item(360, Utils.random(218, 436)) }),
		
		//COOKED_FISH(new Item[] {}), TODO
		
		NESTS(new Item[] {new Item(5070), new Item(5071), new Item(5072), new Item(5073), new Item(5074), new Item(5075)}),
		
		;
	
	private Item[] resources;
	
	ResourceConstants(Item[] resources) {
		this.resources = resources;
	}
	
	public Item[] getItems() {
		return resources;
	}
	
	
}
