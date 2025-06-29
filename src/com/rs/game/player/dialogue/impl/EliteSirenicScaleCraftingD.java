package com.rs.game.player.dialogue.impl;

import com.rs.game.player.Skills;
import com.rs.game.player.actions.crafting.EliteSirenicScaleCrafting;
import com.rs.game.player.actions.crafting.EliteSirenicScaleCrafting.EliteSirenic;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.game.player.dialogue.Dialogue;

/**
 * Handles the Sirenic Scale crafting skills dialogue.
 * @author Zeus
 */
public class EliteSirenicScaleCraftingD extends Dialogue {
	
	private EliteSirenic[] scale;

	@Override
	public void start() {
		scale = (EliteSirenic[]) parameters[1];
		int count = 0;
		int[] ids = new int[scale.length];
		for (EliteSirenic scale : scale)
			ids[count++] = scale.getProduceEnergy().getId();
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE, "Which armour piece would you like to create?", 
				1, ids, new ItemNameFilter() {
			int count = 0;

			@Override
			public String rename(String name) {
				EliteSirenic scale = EliteSirenic.values()[count++];
				if (player.getSkills().getLevel(Skills.CRAFTING) < scale.getLevelRequired())
					name = "<col=ff0000>" + name + "<br><col=ff0000>Level " + scale.getLevelRequired();
				return name;

			}
		});
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int idx = SkillsDialogue.getItemSlot(componentId);
		if (idx > scale.length) {
			end();
			return;
		}
		player.getActionManager().setAction(new EliteSirenicScaleCrafting(scale[idx], SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void finish() {  }
}