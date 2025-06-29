package com.rs.utils;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.item.Item;

/**
 * Handles the setting & getting of Item values.
 * @author Zeus
 */
public final class EconomyPrices {

	/**
	 * Gets the item value.
	 * @param itemId The item ID.
	 * @return the Value.
	 */
    public static int getPrice(int itemId) {
    	ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
    	if (defs.isNoted())
    		itemId = defs.getCertId();
    	else if (defs.isLended())
    		itemId = defs.getLendId();
    	if (itemId == 995)
    		return 1;
    	return defs.getValue();
    }

    /**
     * Gets the alchemy price.
     * @param item The item's price to get.
     * @param lowAlch If low alchemy.
     * @return The alchemy value.
     */
    public static int getAlchPrice(Item item, boolean lowAlch) {
    	return (int) (item.getDefinitions().getValue() * (lowAlch ? 0.65 : 0.85));
    }
}