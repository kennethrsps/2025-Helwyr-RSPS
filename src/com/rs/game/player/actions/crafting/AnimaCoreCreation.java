package com.rs.game.player.actions.crafting;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;

/**
 * @author Tom
 * @date May 4, 2017
 */

public class AnimaCoreCreation extends Action {

	public enum AnimaCoreData {

		ANIMA_CORE_HELM_OF_ZAROS(new Item[] { new Item(37009), new Item(37018) }, new Item(37034), 1),
		ANIMA_CORE_LEGS_OF_ZAROS(new Item[] { new Item(37015), new Item(37018) }, new Item(37040), 1),
		ANIMA_CORE_BODY_OF_ZAROS(new Item[] { new Item(37012), new Item(37018) }, new Item(37037), 1),
		
		ANIMA_CORE_HELM_OF_SEREN(new Item[] { new Item(37009), new Item(37027) }, new Item(37052), 1),
		ANIMA_CORE_LEGS_OF_SEREN(new Item[] { new Item(37015), new Item(37027) }, new Item(37058), 1),
		ANIMA_CORE_BODY_OF_SEREN(new Item[] { new Item(37012), new Item(37027) }, new Item(37055), 1),
		
		ANIMA_CORE_HELM_OF_ZAMORAK(new Item[] { new Item(37009), new Item(37024) }, new Item(37043), 1),
		ANIMA_CORE_LEGS_OF_ZAMORAK(new Item[] { new Item(37015), new Item(37024) }, new Item(37049), 1),
		ANIMA_CORE_BODY_OF_ZAMORAK(new Item[] { new Item(37012), new Item(37024) }, new Item(37046), 1),
		
		ANIMA_CORE_HELM_OF_SLISKE(new Item[] { new Item(37009), new Item(37021) }, new Item(37061), 1),
		ANIMA_CORE_LEGS_OF_SLISKE(new Item[] { new Item(37015), new Item(37021) }, new Item(37067), 1),
		ANIMA_CORE_BODY_OF_SLISKE(new Item[] { new Item(37012), new Item(37021) }, new Item(37064), 1);

		private int levelRequired;
		private Item[] material;
		private Item product;

		private AnimaCoreData(Item[] material, Item product, int levelRequired) {
			this.material = material;
			this.product = product;
			this.levelRequired = levelRequired;
		}

		public Item getProduct() {
			return product;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public Item[] getMaterial() {
			return material;
		}
			
		public static AnimaCoreData getProduct(int id) {
			for (AnimaCoreData anima : AnimaCoreData.values()) {
					if (anima.getProduct().getId() == id)
						return anima;
			}
			return null;
		}
	}

	private AnimaCoreData anima;

	public AnimaCoreCreation(AnimaCoreData anima) {
		this.anima = anima;
	}

	public boolean checkRequirements(Player player) {
		if (player.getInterfaceManager().containsScreenInter()
				|| player.getInterfaceManager().containsInventoryInter()) {
			player.getPackets().sendGameMessage("Please finish what you're doing before doing this action.");
			return false;
		}
		if (!player.getInventory().containsItem(anima.getMaterial()[0])) {
			player.getInterfaceManager().closeChatBoxInterface();
			player.getPackets().sendGameMessage(
					"You don't have any " + anima.getMaterial()[0].getName().toLowerCase() + " to create this armour piece.");
			return false;
		}
		if (!player.getInventory().containsItem(anima.getMaterial()[1])) {
			player.getInterfaceManager().closeChatBoxInterface();
			player.getPackets().sendGameMessage(
					"You don't have any " + anima.getMaterial()[1].getName().toLowerCase() + " to create this armour piece.");
			return false;
		}
		return true;
	}

	@Override
	public boolean start(Player player) {
		if (checkRequirements(player)) 
			return true;
		return false;
	}

	@Override
	public boolean process(Player player) {
		return checkRequirements(player);
	}

	@Override
	public int processWithDelay(Player player) {
		for (int x = 0; x < anima.getMaterial().length; x++)
		player.getInventory().deleteItem(anima.getMaterial()[x]);
		player.getInventory().addItem(anima.getProduct());
		player.sendMessage("You have successfully created a " + anima.getProduct().getName() + ".");
		player.getInterfaceManager().closeChatBoxInterface();
		return 2;
	}

	@Override
	public void stop(final Player player) {
		setActionDelay(player, 3);
	}

}
