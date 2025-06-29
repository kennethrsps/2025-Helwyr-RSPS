package com.rs.game.player.dialogue.impl;

import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.game.item.Item;

/**
 * Handles Clue scroll item dyeing.
 * 
 * @author Zeus
 */
public class ClueScrollDyes extends Dialogue {

	/**
	 * Represents the 2 items being used one on each other.
	 */
	Item item, dye;

	@Override
	public void start() {
		item = (Item) parameters[0];
		dye = (Item) parameters[1];
		if (handleDye(item, dye, false))
			sendItemDialogue(item.getId(), item.getAmount(),
					"Dyeing your " + item.getName() + " will make it permanently "
							+ "untradable. Dyed items will no longer turn into ashes upon reaching 0 charges "
							+ "(Doesn't affect you if you have the Charge Befriender perk).");
		else {
			player.sendMessage("You cannot use the " + dye.getName() + " on " + item.getName() + ".");
			end();
		}
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue(Colors.red + "Do you want to " + dye.getName() + " your " + item.getName() + "?",
					"Yes.", "No.");
			stage = 0;
			break;
		case 0:
			switch (componentId) {
			case OPTION_1:
				if (handleDye(item, dye, true))
					sendDialogue(
							Colors.red + "You've successfully " + dye.getName() + "d your " + item.getName() + "!");
				else
					sendDialogue(Colors.red + "You cannot dye this item at the moment.");
				stage = 1;
				break;
			default:
				end();
				break;
			}
			break;
		case 1:
			end();
			break;
		}
	}

	@Override
	public void finish() {
	}

	/**
	 * Handles the actual item Dyeing.
	 * 
	 * @param item
	 *            The item to dye.
	 * @param dye
	 *            The dye to use.
	 * @return if Item is Dye'able.
	 */
	private boolean handleDye(Item item, Item dye, boolean remove) {
		if (item == null || dye == null)
			return false;
		if (!player.getInventory().containsItem(item))
			return false;
		if (!player.getInventory().containsItem(dye))
			return false;
		if (dye.getId() == 33294) { // Barrows dye.
			if (item.getId() == 29854 || item.getId() == 29856) { // Sirenic Mask
				replaceItems(item, dye, new Item(33348), remove);
				return true;
			}
			if (item.getId() == 29857 || item.getId() == 29859) { // Sirenic Hauberk
				replaceItems(item, dye, new Item(33351), remove);
				return true;
			}
			if (item.getId() == 29860 || item.getId() == 29862) { // Sirenic Chaps
				replaceItems(item, dye, new Item(33354), remove);
				return true;
			}
			if (item.getId() == 30005 || item.getId() == 30006 || item.getId() == 30007) { // malevolent helm
				replaceItems(item, dye, new Item(33357), remove);
				return true;
			}
			if (item.getId() == 30008 || item.getId() == 30009 || item.getId() == 30010) { // malevolent cuirass
				replaceItems(item, dye, new Item(33360), remove);
				return true;
			}
			if (item.getId() == 30011 || item.getId() == 30012 || item.getId() == 30013) { // malevolent greaves
				replaceItems(item, dye, new Item(33363), remove);
				return true;
			}

			if (item.getId() == 28437 || item.getId() == 28439 || item.getId() == 28440) { // Ascension Crossbow
				replaceItems(item, dye, new Item(item.getId() == 28440 ? 33320 : 33318), remove);
				return true;
			}
			if (item.getId() == 31725 || item.getId() == 31727 || item.getId() == 31728) { // Noxious Scythe
				replaceItems(item, dye, new Item(item.getId() == 31728 ? 33332 : 33330), remove);
				return true;
			}
			if (item.getId() == 31729 || item.getId() == 31731 || item.getId() == 31732) { // Noxious Staff
				replaceItems(item, dye, new Item(item.getId() == 31732 ? 33335 : 33333), remove);
				return true;
			}
			if (item.getId() == 31733 || item.getId() == 31735 || item.getId() == 31736) { // Noxious Longbow
				replaceItems(item, dye, new Item(item.getId() == 31736 ? 33338 : 33336), remove);
				return true;
			}
			if (item.getId() == 26579 || item.getId() == 26581 || item.getId() == 26582) { // Drygore Rapier
				replaceItems(item, dye, new Item(item.getId() == 26582 ? 33308 : 33306), remove);
				return true;
			}
			if (item.getId() == 26587 || item.getId() == 26589 || item.getId() == 26590) { // Drygore Longsword
				replaceItems(item, dye, new Item(item.getId() == 26590 ? 33314 : 33312), remove);
				return true;
			}
			if (item.getId() == 26595 || item.getId() == 26597 || item.getId() == 26598) { // Drygore Mace
				replaceItems(item, dye, new Item(item.getId() == 26598 ? 33302 : 33300), remove);
				return true;
			}
			if (item.getId() == 28608 || item.getId() == 28610) { // Tectonic hat
				replaceItems(item, dye, new Item(33339), remove);
				return true;
			}
			if (item.getId() == 28611 || item.getId() == 28613) { // Tectonic top
				replaceItems(item, dye, new Item(33342), remove);
				return true;
			}
			if (item.getId() == 28614 || item.getId() == 28616) { // Tectonic bottoms
				replaceItems(item, dye, new Item(33345), remove);
				return true;
			}
			return false;
		} else if (dye.getId() == 33296) { // Shadow dye.
			if (item.getId() == 30005 || item.getId() == 30006 || item.getId() == 30007) { // malevolent helm
				replaceItems(item, dye, new Item(33423), remove);
				return true;
			}
			if (item.getId() == 30008 || item.getId() == 30009 || item.getId() == 30010) { // malevolent cuirass
				replaceItems(item, dye, new Item(33426), remove);
				return true;
			}
			if (item.getId() == 30011 || item.getId() == 30012 || item.getId() == 30013) { // malevolent greaves
				replaceItems(item, dye, new Item(33429), remove);
				return true;
			}
			if (item.getId() == 28608 || item.getId() == 28610) { // Tectonic hat
				replaceItems(item, dye, new Item(33405), remove);
				return true;
			}
			if (item.getId() == 28611 || item.getId() == 28613) { // Tectonic top
				replaceItems(item, dye, new Item(33408), remove);
				return true;
			}
			if (item.getId() == 28614 || item.getId() == 28616) { // Tectonic bottoms
				replaceItems(item, dye, new Item(33411), remove);
				return true;
			}
			if (item.getId() == 29854 || item.getId() == 29856) { // Sirenic Mask
				replaceItems(item, dye, new Item(33414), remove);
				return true;
			}
			if (item.getId() == 29857 || item.getId() == 29859) { // Sirenic Hauberk
				replaceItems(item, dye, new Item(33417), remove);
				return true;
			}
			if (item.getId() == 29860 || item.getId() == 29862) { // Sirenic Chaps
				replaceItems(item, dye, new Item(33420), remove);
				return true;
			}
			if (item.getId() == 28437 || item.getId() == 28439 || item.getId() == 28440) { // Ascension Crossbow
				replaceItems(item, dye, new Item(item.getId() == 28440 ? 33386 : 33384), remove);
				return true;
			}
			if (item.getId() == 31725 || item.getId() == 31727 || item.getId() == 31728) { // Noxious Scythe
				replaceItems(item, dye, new Item(item.getId() == 31728 ? 33398 : 33396), remove);
				return true;
			}
			if (item.getId() == 31729 || item.getId() == 31731 || item.getId() == 31732) { // Noxious Staff
				replaceItems(item, dye, new Item(item.getId() == 31732 ? 33401 : 33399), remove);
				return true;
			}
			if (item.getId() == 31733 || item.getId() == 31735 || item.getId() == 31736) { // Noxious Longbow
				replaceItems(item, dye, new Item(item.getId() == 31736 ? 33404 : 33402), remove);
				return true;
			}
			if (item.getId() == 26579 || item.getId() == 26581 || item.getId() == 26582) { // Drygore Rapier
				replaceItems(item, dye, new Item(item.getId() == 26582 ? 33374 : 33372), remove);
				return true;
			}
			if (item.getId() == 26587 || item.getId() == 26589 || item.getId() == 26590) { // Drygore Longsword
				replaceItems(item, dye, new Item(item.getId() == 26590 ? 33380 : 33378), remove);
				return true;
			}
			if (item.getId() == 26595 || item.getId() == 26597 || item.getId() == 26598) { // Drygore Mace
				replaceItems(item, dye, new Item(item.getId() == 26598 ? 33368 : 33366), remove);
				return true;
			}
			return false;
		} else if (dye.getId() == 33298) { // Third-Age dye.
			if (item.getId() == 30005 || item.getId() == 30006  || item.getId() == 30007) { //malevolent helm
    			replaceItems(item, dye, new Item(33489), remove);
    			return true;
    		}
    		if (item.getId() == 30008 || item.getId() == 30009  || item.getId() == 30010) { //malevolent cuirass
    			replaceItems(item, dye, new Item(33492), remove);
    			return true;
    		}
    		if (item.getId() == 30011 || item.getId() == 30012  || item.getId() == 30013) { //malevolent greaves
    			replaceItems(item, dye, new Item(33495), remove);
    			return true;
    		}
			if (item.getId() == 28608 || item.getId() == 28610) { // Tectonic hat
				replaceItems(item, dye, new Item(33471), remove);
				return true;
			}
			if (item.getId() == 28611 || item.getId() == 28613) { // Tectonic top
				replaceItems(item, dye, new Item(33474), remove);
				return true;
			}
			if (item.getId() == 28614 || item.getId() == 28616) { // Tectonic bottoms
				replaceItems(item, dye, new Item(33477), remove);
				return true;
			}
			if (item.getId() == 29854 || item.getId() == 29856) { // Sirenic Mask
				replaceItems(item, dye, new Item(33480), remove);
				return true;
			}
			if (item.getId() == 29857 || item.getId() == 29859) { // Sirenic Hauberk
				replaceItems(item, dye, new Item(33483), remove);
				return true;
			}
			if (item.getId() == 29860 || item.getId() == 29862) { // Sirenic Chaps
				replaceItems(item, dye, new Item(33486), remove);
				return true;
			}
			if (item.getId() == 28437 || item.getId() == 28439 || item.getId() == 28440) { // Ascension Crossbow
				replaceItems(item, dye, new Item(item.getId() == 28440 ? 33452 : 33450), remove);
				return true;
			}
			if (item.getId() == 31725 || item.getId() == 31727 || item.getId() == 31728) { // Noxious Scythe
				replaceItems(item, dye, new Item(item.getId() == 31728 ? 33464 : 33462), remove);
				return true;
			}
			if (item.getId() == 31729 || item.getId() == 31731 || item.getId() == 31732) { // Noxious Staff
				replaceItems(item, dye, new Item(item.getId() == 31732 ? 33467 : 33465), remove);
				return true;
			}
			if (item.getId() == 31733 || item.getId() == 31735 || item.getId() == 31736) { // Noxious Longbow
				replaceItems(item, dye, new Item(item.getId() == 31736 ? 33470 : 33468), remove);
				return true;
			}
			if (item.getId() == 26579 || item.getId() == 26581 || item.getId() == 26582) { // Drygore Rapier
				replaceItems(item, dye, new Item(item.getId() == 26582 ? 33440 : 33438), remove);
				return true;
			}
			if (item.getId() == 26587 || item.getId() == 26589 || item.getId() == 26590) { // Drygore Longsword
				replaceItems(item, dye, new Item(item.getId() == 26590 ? 33446 : 33444), remove);
				return true;
			}
			if (item.getId() == 26595 || item.getId() == 26597 || item.getId() == 26598) { // Drygore Mace
				replaceItems(item, dye, new Item(item.getId() == 26598 ? 33434 : 33432), remove);
				return true;
			}
			return false;
		} else if (dye.getId() == 36274) { // Blood dye.
			if (item.getId() == 30005 || item.getId() == 30006  || item.getId() == 30007) { //malevolent helm
    			replaceItems(item, dye, new Item(36294), remove);
    			return true;
    		}
    		if (item.getId() == 30008 || item.getId() == 30009  || item.getId() == 30010) { //malevolent cuirass
    			replaceItems(item, dye, new Item(36297), remove);
    			return true;
    		}
    		if (item.getId() == 30011 || item.getId() == 30012  || item.getId() == 30013) { //malevolent greaves
    			replaceItems(item, dye, new Item(36300), remove);
    			return true;
    		}
			if (item.getId() == 28608 || item.getId() == 28610) { // Tectonic hat
				replaceItems(item, dye, new Item(36276), remove);
				return true;
			}
			if (item.getId() == 28611 || item.getId() == 28613) { // Tectonic top
				replaceItems(item, dye, new Item(36279), remove);
				return true;
			}
			if (item.getId() == 28614 || item.getId() == 28616) { // Tectonic bottoms
				replaceItems(item, dye, new Item(36282), remove);
				return true;
			}
			if (item.getId() == 29854 || item.getId() == 29856) { // Sirenic Mask
				replaceItems(item, dye, new Item(36285), remove);
				return true;
			}
			if (item.getId() == 29857 || item.getId() == 29859) { // Sirenic Hauberk
				replaceItems(item, dye, new Item(36288), remove);
				return true;
			}
			if (item.getId() == 29860 || item.getId() == 29862) { // Sirenic Chaps
				replaceItems(item, dye, new Item(36291), remove);
				return true;
			}
			if (item.getId() == 28437 || item.getId() == 28439 || item.getId() == 28440) { // Ascension Crossbow
				replaceItems(item, dye, new Item(item.getId() == 28440 ? 36323 : 36321), remove);
				return true;
			}
			if (item.getId() == 31725 || item.getId() == 31727 || item.getId() == 31728) { // Noxious Scythe
				replaceItems(item, dye, new Item(item.getId() == 31728 ? 36335 : 36334), remove);
				return true;
			}
			if (item.getId() == 31729 || item.getId() == 31731 || item.getId() == 31732) { // Noxious Staff
				replaceItems(item, dye, new Item(item.getId() == 31732 ? 36338 : 36336), remove);
				return true;
			}
			if (item.getId() == 31733 || item.getId() == 31735 || item.getId() == 31736) { // Noxious Longbow
				replaceItems(item, dye, new Item(item.getId() == 31736 ? 36341 : 36339), remove);
				return true;
			}
			if (item.getId() == 26579 || item.getId() == 26581 || item.getId() == 26582) { // Drygore Rapier
				replaceItems(item, dye, new Item(item.getId() == 26582 ? 36311 : 36309), remove);
				return true;
			}
			if (item.getId() == 26587 || item.getId() == 26589 || item.getId() == 26590) { // Drygore Longsword
				replaceItems(item, dye, new Item(item.getId() == 26590 ? 36317 : 36315), remove);
				return true;
			}
			if (item.getId() == 26595 || item.getId() == 26597 || item.getId() == 26598) { // Drygore Mace
				replaceItems(item, dye, new Item(item.getId() == 26598 ? 36305 : 36303), remove);
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * Replaces the Dye, the Dyeing Item with Product Item.
	 * 
	 * @param item
	 *            The item to delete.
	 * @param dye
	 *            The dye to delete.
	 * @param product
	 *            The product to add.
	 */
	private void replaceItems(Item item, Item dye, Item product, boolean remove) {
		if (!remove)
			return;
		player.getInventory().deleteItem(item);
		player.getInventory().deleteItem(dye);
		player.getInventory().addItem(product);
	}
}