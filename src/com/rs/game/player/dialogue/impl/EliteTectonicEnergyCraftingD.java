package com.rs.game.player.dialogue.impl;

import com.rs.game.player.Skills;
import com.rs.game.player.actions.crafting.SirenicScaleCrafting.Sirenic;
import com.rs.game.player.actions.crafting.EliteTectonicEnergyCrafting;
import com.rs.game.player.actions.crafting.EliteTectonicEnergyCrafting.EliteTectonic;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.game.player.dialogue.Dialogue;

/**
 * Handles the Tectonic Energy rcrafting skills dialogue.
 * @author Zeus
 */
public class EliteTectonicEnergyCraftingD extends Dialogue {
	
	private EliteTectonic[] scale;

	@Override
	public void start() {
		scale = (EliteTectonic[]) parameters[1];
		int count = 0;
		int[] ids = new int[scale.length];
		for (EliteTectonic scale : scale)
			ids[count++] = scale.getProduceEnergy().getId();
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE, "Which armour piece would you like to create?", 
				1, ids, new ItemNameFilter() {
			int count = 0;

			@Override
			public String rename(String name) {
				Sirenic scale = Sirenic.values()[count++];
				if (player.getSkills().getLevel(Skills.RUNECRAFTING) < scale.getLevelRequired())
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
		player.getActionManager().setAction(new EliteTectonicEnergyCrafting(scale[idx], SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void finish() {  }
}