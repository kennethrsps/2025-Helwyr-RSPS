package com.rs.game.player.dialogue.impl;

import com.rs.game.player.Skills;
import com.rs.game.player.actions.divination.WeavingEnergy;
import com.rs.game.player.actions.divination.WeavingEnergy.Energy;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.game.player.dialogue.Dialogue;

public class WeavingD extends Dialogue {
	
	private Energy[] bars;

	@Override
	public void start() {
		bars = (Energy[]) parameters[1];
		int count = 0;
		int[] ids = new int[bars.length];
		for (Energy bar : bars)
			ids[count++] = bar.getProduceEnergy().getId();
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE, "Which Divine location would you like to create?", 
				1, ids, new ItemNameFilter() {
			int count = 0;

			@Override
			public String rename(String name) {
				Energy bar = Energy.values()[count++];
				if (player.getSkills().getLevel(Skills.DIVINATION) < bar.getLevelRequired())
					name = "<col=ff0000>" + name + "<br><col=ff0000>Level " + bar.getLevelRequired();
				return name;

			}
		});
	}

	@Override
	public void run(int interfaceId, int componentId) {
		int idx = SkillsDialogue.getItemSlot(componentId);
		if (idx > bars.length) {
			end();
			return;
		}
		player.getActionManager().setAction(new WeavingEnergy(bars[idx], SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void finish() {
	}
}