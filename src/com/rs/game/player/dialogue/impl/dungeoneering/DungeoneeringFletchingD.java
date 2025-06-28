package com.rs.game.player.dialogue.impl.dungeoneering;

import com.rs.game.player.Skills;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.game.player.content.dungeoneering.skills.DungeoneeringFletching;
import com.rs.game.player.content.dungeoneering.skills.DungeoneeringFletching.Fletch;
import com.rs.game.player.dialogue.Dialogue;

public class DungeoneeringFletchingD extends Dialogue {

	private Fletch fletch;

	@Override
	public void start() {
		fletch = (Fletch) parameters[0];
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE,
				"How many logs you would like to fletch?<br>Choose a number, then click the bar to begin.", 28,
				fletch.getProduct(), new ItemNameFilter() {
					int count = 0;

					@Override
					public String rename(String name) {
						int requiredLevel = fletch.getLevel()[count++];
						if (player.getSkills().getLevel(Skills.SMITHING) < requiredLevel)
							name = "<col=ff0000>" + name + "<br><col=ff0000>Level " + requiredLevel;
						return name;

					}
				});
	}

	@Override
	public void run(int interfaceId, int componentId) {
		player.getActionManager().setAction(new DungeoneeringFletching(fletch, SkillsDialogue.getItemSlot(componentId),
				SkillsDialogue.getQuantity(player)));
		end();
	}

	@Override
	public void finish() {
	}

}
