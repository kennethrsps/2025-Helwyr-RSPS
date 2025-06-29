package com.rs.game.player.content.ports;

import java.util.HashMap;
import java.util.Map;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

/**
 * Used to handle exchanging regular port armors for superior ones.
 * @author Zeus
 */
public class SuperiorExchange {

	/**
	 * An enum containing all superior port item data.
	 * @author Zeus
	 */
	public enum PortArmor {
		
		REFINED_ANIMA_CORE_HELM_OF_ZAROS(37034, 37036, 1100),
		REFINED_ANIMA_CORE_LEGS_OF_ZAROS(37040, 37042, 1700),
		REFINED_ANIMA_CORE_BODY_OF_ZAROS(37037, 37039, 2200)
		,
		REFINED_ANIMA_CORE_HELM_OF_ZAMORAK(37043, 37045, 1100),
		REFINED_ANIMA_CORE_LEGS_OF_ZAMORAK(37049, 37051, 1700),
		REFINED_ANIMA_CORE_BODY_OF_ZAMORAK(37046, 37048, 2200)
		,
		REFINED_ANIMA_CORE_HELM_OF_SEREN(37052, 37054, 1100),
		REFINED_ANIMA_CORE_LEGS_OF_SEREN(37058, 37060, 1700),
		REFINED_ANIMA_CORE_BODY_OF_SEREN(37055, 37057, 2200)
		,
		REFINED_ANIMA_CORE_HELM_OF_SLISKE(37061, 37063, 1100),
		REFINED_ANIMA_CORE_LEGS_OF_SLISKE(37067, 37069, 1700),
		REFINED_ANIMA_CORE_BODY_OF_SLISKE(37064, 37066, 2200)
		,
		SUPERIOR_TETSU_HELM(26325, 26322, 1000),
		SUPERIOR_TETSU_BODY(26326, 26323, 2100),
		SUPERIOR_TETSU_LEGS(26327, 26324, 1600)
		,
		SUPERIOR_SEASINGER_HOOD(26337, 26334, 1000),
		SUPERIOR_SEASINGER_ROBE_TOP(26338, 26335, 2100),
		SUPERIOR_SEASINGER_ROBE_BOTTOM(26339, 26336, 1600)
		,
		SUPERIOR_DEATH_LOTUS_HOOD(26346, 26352, 1000),
		SUPERIOR_DEATH_LOTUS_CHESTPLATE(26347, 26353, 2100),
		SUPERIOR_DEATH_LOTUS_CHAPS(26348, 26354, 1600)
		
		/** Regular - Superior - Cost **/;
		
		private int id;
		private int id2;
		private int Price;

		private static Map<Integer, PortArmor> PORTITEMS = new HashMap<Integer, PortArmor>();

		static {
			for (PortArmor brokenitems : PortArmor.values())
				PORTITEMS.put(brokenitems.getId(), brokenitems);
		}

		public static PortArmor forId(int id) {
			return PORTITEMS.get(id);
		}

		private PortArmor(int id, int id2, int Price) {
			this.id = id;
			this.id2 = id2;
			this.Price = Price;
		}

		public int getId() {
			return id;
		}

		public int getId2() {
			return id2;
		}

		public int getPrice() {
			return Price;
		}

	}

	/**
	 * Used to handle exchanging items.
	 * @param player The player exchanging.
	 * @param itemId The itemId to exchange.
	 * @return if exchanged.
	 */
	public static boolean exchange(Player player, int itemId) {
		final PortArmor portItems = PortArmor.forId(itemId);
		int price = portItems.getPrice();
		if (player.getPorts().chime >= price) {
			player.getInventory().deleteItem(itemId, 1);
			player.getInventory().addItem(portItems.getId2(), 1);
			player.getPorts().chime -= price;
			return true;
		} else {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You dont have enough Chime to repair this; you need " + getPrice(itemId) + ".");
			return false;
		}
	}
	
	/**
	 * Checks superior item price to be exchange.
	 * @param player The player checking.
	 * @param itemId The itemId to check.
	 * @return The price as an int.
	 */
	public static int checkPrice(Player player, int itemId) {
		final PortArmor portItems = PortArmor.forId(itemId);
		int price = portItems.getPrice();
		return price;
	}

	/**
	 * Gets the price and formats it.
	 * @param itemId The itemId to get the price.
	 * @return the formatted String.
	 */
	public static String getPrice(int itemId) {
		final PortArmor portItems = PortArmor.forId(itemId);
		int price = portItems.getPrice();
		return Utils.getFormattedNumber(price);
	}
}