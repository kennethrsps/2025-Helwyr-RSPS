package com.rs.game.player.dialogue.impl;

import com.rs.game.player.actions.crafting.AnimaCoreCreation;
import com.rs.game.player.actions.crafting.AnimaCoreCreation.AnimaCoreData;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.game.player.dialogue.Dialogue;

/**
 * @author Tom
 * @date May 4, 2017
 */

public class AnimaCoreCreationD extends Dialogue {

	@Override
	public void start() {
		int count = 0;
		int count2 = 0;
		boolean hasAllItems = false;

		for (AnimaCoreData anima : AnimaCoreData.values()) {
			hasAllItems = true;
			for (int i = 0; i < anima.getMaterial().length; i++) 
				if (!player.getInventory().containsItem(anima.getMaterial()[i]))
					hasAllItems = false;
			if (hasAllItems) 
				count++;
		}
		int ids[] = new int[count];
		for (AnimaCoreData anima : AnimaCoreData.values()) {
			hasAllItems = true;
			for (int i = 0; i < anima.getMaterial().length; i++) 
				if (!player.getInventory().containsItem(anima.getMaterial()[i]))
					hasAllItems = false;
			if (hasAllItems) {
				ids[count2++] = anima.getProduct().getId();
			}
		}

		if (count != 0) {
			SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE,
					"Which crystal flask would you like to make?", 1, ids, new ItemNameFilter() {
						int count = 0;

						@Override
						public String rename(String name) {
							@SuppressWarnings("unused")
							AnimaCoreData anima = AnimaCoreData.values()[count++];
							return name;
						}
					});
		} else 
			player.sendMessage("You don't have all the necessary materials to make this armour piece.");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int itemSlot = SkillsDialogue.getItemSlot(componentId);
		int item = SkillsDialogue.getItem(itemSlot);
		AnimaCoreData anima = AnimaCoreData.getProduct(item);
		player.getActionManager().setAction(new AnimaCoreCreation(anima));
	}

	@Override
	public void finish() {
	}

}
