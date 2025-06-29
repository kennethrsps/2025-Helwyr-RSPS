package com.rs.game.player.dialogue.impl;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.actions.Action;
import com.rs.game.player.actions.crafting.Glassblowing;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.dialogue.Dialogue;

/**
 * The dialogue of Glassblowing.
 * @author Zeus
 */
public class GlassblowingD extends Dialogue {

	private int itemId;

	@Override
	public void start() {
		SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE,
			"Choose how many you wish to make, then click on the chosen item to begin.", 28,
			new int[] { 1919, 4522, 229, 6667, 567, 23191 }, new SkillsDialogue.ItemNameFilter() {
			@Override
			public String rename(String name) {
				if (name.equalsIgnoreCase(ItemDefinitions.getItemDefinitions(1919).getName())) {
					return "Beer Glass";
				} 
				else if (name.equalsIgnoreCase(ItemDefinitions.getItemDefinitions(4522).getName())) {
					return "Oil lamp";
				} 
				else if (name.equalsIgnoreCase(ItemDefinitions.getItemDefinitions(229).getName())
						&& SkillsDialogue.getItemSlot(16) == 2) {
					return "Vial";
				} 
				else if (name.equalsIgnoreCase(ItemDefinitions.getItemDefinitions(6667).getName())
						&& SkillsDialogue.getItemSlot(17) == 3) {
					return "Fishbowl";
				} 
				else if (name.equalsIgnoreCase(ItemDefinitions.getItemDefinitions(567).getName())) {
					return "Unpowered orb";
				} 
				else if (name.equalsIgnoreCase(ItemDefinitions.getItemDefinitions(23191).getName())) {
					return "Potion flask";
				}
				return name;
			}
		});
	}

	@Override
	public void run(int interfaceId, int componentId) {
		SkillsDialogue.setItem(new int[] { 1919, 4522, 229, 6667, 567, 23191 });
		int option = SkillsDialogue.getItemSlot(componentId);
		itemId = SkillsDialogue.getItem(option);
		//Logger.log("Player: "+player.getDisplayName()+" glassblowing itemId: "+itemId+" [option: "+option+"].");
		player.getActionManager().setAction(new Action() {
			int ticks;
			@Override
			public boolean start(Player player) {
				if (!player.getInventory().containsOneItem(1785)) {
					player.sendMessage("You do not have a glassblowing pipe to work the molten glass with.");
					return false;
				}
				int moltenGlassQ = player.getInventory().getAmountOf(1775);
				if (moltenGlassQ == 0) {
					player.getPackets().sendGameMessage("You've ran out of Molten glass to work with.", true);
					end();
					return false;
				}
				int requestedAmount = SkillsDialogue.getQuantity(player);
				if (requestedAmount > moltenGlassQ)
					requestedAmount = moltenGlassQ;
				ticks = requestedAmount;
				return true;
			}

			@Override
			public boolean process(Player player) {
				return ticks > 0;
			}

			@Override
			public int processWithDelay(Player player) {
				ticks--;
				Glassblowing.canBlow(player, itemId);
				return 2;
			}

			@Override
			public void stop(Player player) {
				setActionDelay(player, 2);
			}
		});
		end();
	}

	@Override
	public void finish() { }
}