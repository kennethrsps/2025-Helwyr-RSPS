package com.rs.game.player.dialogue.impl;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.player.Skills;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class MalevolentD extends Dialogue {

	@Override
	public void start() {
		if (!player.getInventory().containsItem(30027, 1)) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You will need a Malevolent Energy to do this!");
			return;
		}
		if (!player.getInventory().containsItem(30028, 1)) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You will need a Reinforcing plate to do this!");
			return;
		}

		if (player.getSkills().getLevel(Skills.SMITHING) < 90) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You need a Smithing level of 90 to create this.");
			return;
		}
		if (!player.getInventory().containsOneItem(2347)) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You need a hammer to work on the anvil!");
			return;
		}
		sendItemDialogue(30027, 1, "It is used to produce Malevolent armour");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			sendOptionsDialogue("How would you like to forge Malevolent?", "Forge Malevolent Helm(20)",
					"Forge Malevolent Cuirass(35)", "Forge Malevolent Greaves(30)", "Nevermind");
			stage = 0;
			break;

		case 0:
			switch (componentId) {// helm
			case OPTION_1:
				if (!player.getInventory().containsItem(30027, 14)) {
					player.sm(Colors.red + "You need 14 Malevolent Energy to craft Malevolent Helm!");
					end();
					return;
				}
				if (!player.getInventory().containsItem(30028, 1)) {
					player.sm(Colors.red + "You need 1 Reinforcing plate to craft Malevolent Helm!");
					end();
					return;
				}
				if (!player.getInventory().hasFreeSlots()) {
					player.sm(Colors.red + "You dont have free slot in your inventory!");
					end();
					return;
				}

				player.setNextAnimation(new Animation(898));
				player.getInventory().deleteItem(30027, 14);
				player.getInventory().deleteItem(30028, 1);
				player.getSkills().addXp(Skills.SMITHING, 2000);
				player.getInventory().addItem(30005, 1);
				player.sm(Colors.green + "Congratulations, You've Successfully crafted Malevolent Helm");
				World.sendWorldMessage(Colors.green + "[Rise of the Six] " + player.getUsername()
						+ " Successfully crafted a Malevolent Helm!", false);
				end();
				break;

			case OPTION_2:
				if (!player.getInventory().containsItem(30027, 42)) {
					player.sm(Colors.red + "You need 42 Malevolent Energy to craft Malevolent Cuirass!");
					end();
					return;
				}
				if (!player.getInventory().containsItem(30028, 3)) {
					player.sm(Colors.red + "You need 3 Reinforcing plate to craft Malevolent Helm!");
					end();
					return;
				}
				if (!player.getInventory().hasFreeSlots()) {
					player.sm(Colors.red + "You dont have free slot in your inventory!");
					end();
					return;
				}
				player.setNextAnimation(new Animation(898));
				player.getInventory().deleteItem(30027, 42);
				player.getInventory().deleteItem(30028, 3);
				player.getSkills().addXp(Skills.SMITHING, 2000);
				player.getInventory().addItem(30008, 1);
				player.sm(Colors.green + "Congratulations, You've Successfully crafted Malevolent Cuirass");
				World.sendWorldMessage(Colors.green + "[Rise of the Six] " + player.getUsername()
						+ " Successfully crafted a Malevolent Cuirass!", false);
				end();
				break;
			case OPTION_3:
				if (!player.getInventory().containsItem(30027, 28)) {
					player.sm(Colors.red + "You need 28 Malevolent Energy to craft Malevolent Greaves!");
					end();
					return;
				}
				if (!player.getInventory().containsItem(30028, 2)) {
					player.sm(Colors.red + "You need 2 Reinforcing plate to craft Malevolent Helm!");
					end();
					return;
				}
				if (!player.getInventory().hasFreeSlots()) {
					player.sm(Colors.red + "You dont have free slot in your inventory!");
					end();
					return;
				}
				player.setNextAnimation(new Animation(898));
				player.getInventory().deleteItem(30027, 28);
				player.getInventory().deleteItem(30028, 2);
				player.getSkills().addXp(Skills.SMITHING, 2000);
				player.getInventory().addItem(30011, 1);
				player.sm(Colors.green + "Congratulations, You've Successfully crafted Malevolent Greaves");
				World.sendWorldMessage(Colors.green + "[Rise of the Six] " + player.getUsername()
						+ " Successfully crafted a Malevolent Greaves!", false);
				end();
				break;
			case OPTION_4:
				end();
				break;
			}
		}
	}

	@Override
	public void finish() {
		player.getInterfaceManager().closeChatBoxInterface();
	}
}