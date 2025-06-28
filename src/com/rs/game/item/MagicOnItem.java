package com.rs.game.item;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.activites.Sawmill;
import com.rs.game.activites.Sawmill.Plank;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.crafting.Enchanting;
import com.rs.game.player.actions.smithing.SuperHeating;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.Magic;/*
import com.rs.game.player.controllers.InstancedPVPControler;*/
import com.rs.utils.EconomyPrices;

/**
 * Handles Magic interface being used on inventory items.
 * @author Zeus.
 */
public class MagicOnItem {

	public static final int LOW_ALCHEMY = 38;
	public static final int HIGH_ALCHEMY = 59;
	public static final int SUPER_HEAT = 50;
	public static final int LV1_ENCHANT = 29;
	public static final int LV2_ENCHANT = 41;
	public static final int LV3_ENCHANT = 53;
	public static final int LV4_ENCHANT = 61;
	public static final int LV5_ENCHANT = 76;
	public static final int LV6_ENCHANT = 88;
	public static final int PLANK_MAKE = 33;

	/**
	 * Handles all of the available spell ID's on items.
	 * @param player The magician.
	 * @param magicId The spell ID.
	 * @param item The Item used on.
	 */
	public static void handleMagic(Player player, int magicId, Item item) {
		int itemId = item.getId();
		
		switch (magicId) {

		case LOW_ALCHEMY:
			processAlchemy(player, item, true);
			break;

		case HIGH_ALCHEMY:
			processAlchemy(player, item, false);
			break;

		case SUPER_HEAT:
			SuperHeating.process(player, itemId, item);
			break;

		case LV1_ENCHANT:
			Enchanting.startEnchant(player, itemId, 1);
			break;

		case LV2_ENCHANT:
			Enchanting.startEnchant(player, itemId, 2);
			break;

		case LV3_ENCHANT:
			Enchanting.startEnchant(player, itemId, 3);
			break;

		case LV4_ENCHANT:
			Enchanting.startEnchant(player, itemId, 4);
			break;

		case LV5_ENCHANT:
			Enchanting.startEnchant(player, itemId, 5);
			break;

		case LV6_ENCHANT:
			Enchanting.startEnchant(player, itemId, 6);
			break;
			
		case PLANK_MAKE: //plank make
			player.getInterfaceManager().openGameTab(7);
			Plank plank = Sawmill.getPlankForLog(item.getId());
			if (plank == null) {
				player.sendMessage("You can only convert plain, oak, teak and mahogany logs into planks.");
				return;
			}
			if (!Magic.checkRunes(player, true, 9075, 2, 557, 15, 561, 1))
				return;
			player.lock(2);
			player.getInventory().deleteItem(item);
			player.getInventory().addItem(new Item(plank.getId()));
			player.getSkills().addXp(Skills.MAGIC, 90);
			player.setNextAnimation(new Animation(4413));
			player.setNextGraphics(new Graphics(1063, 0, 100));
			break;
		default:
			if (player.isDeveloper())
				player.sendMessage("Invalid Magic Id: " + magicId + "; itemId: "+item.getId()+".");
			break;
		}
	}

	/**
	 * Processes Low & High Alchemy.
	 * @param player The magician.
	 * @param item The item being used.
	 * @param low if low alchemy.
	 */
	public static void processAlchemy(Player player, Item item, boolean low) {
		int weaponId = player.getEquipment().getWeaponId();
		int shieldId = player.getEquipment().getShieldId();
		if (player.isLocked())
			return;
		/*if(player.getControlerManager().getControler() instanceof InstancedPVPControler){
			return;
		}*/
		player.getInterfaceManager().openGameTab(7);
		if (player.getActionManager().getActionDelay() != 0)
			return;
		if (Magic.checkSpellRequirements(player, (low == true ? 21 : 55), true, 554, (low == true ? 3 : 5), 561, 1)) {
			if (!ItemConstants.isTradeable(item)) {
				player.sendMessage("You can't " + (low == true ? "low" : "high") + " alch this!");
				return;
			}
			if (Magic.hasInfiniteRunes(554, weaponId, shieldId) && !player.getCombatDefinitions().isSheathe()) {
				player.setNextAnimation(new Animation(low == true ? 9625 : 9633));
				player.setNextGraphics(new Graphics(low == true ? 1692 : 1693));
				player.getPackets().sendSound(low ? 98 : 97, 0, 1);
			} else {
				player.setNextAnimation(new Animation(low == true ? 712 : 713));
				player.setNextGraphics(new Graphics(low == true ? 112 : 113));
				player.getPackets().sendSound(low ? 98 : 97, 0, 1);
			}
			player.getActionManager().setActionDelay(low == true ? 3 : 5);
			player.getInventory().deleteItem(item.getId(), 1);
			player.getSkills().addXp(Skills.MAGIC, (low == true ? 125 : 225));
			if (item.getDefinitions().isNoted())
				item.setId(item.getDefinitions().certId);
			player.addMoney(EconomyPrices.getAlchPrice(item, low));
		}
	}
}