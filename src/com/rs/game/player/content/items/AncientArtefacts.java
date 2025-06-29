package com.rs.game.player.content.items;

import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Utils;

/**
 * Used to handle exchanging of ancient artifacts.
 * @author Zeus
 * Edited by Nate
 */
public enum AncientArtefacts {
	
	BROKEN_STATUE_HEADDRESS(14892, 5),
	THIRD_AGE_CARAFE(14891, 10),
	BRONZED_DRAGON_CLAW(14890, 15),
	ANCIENT_PSALTERY_BRIDGE(14889, 20),
	SARADOMIN_AMPHORA(14888, 30),
	BANDOS_SCRIMSHAW(14887, 40),
	SARADOMIN_CARVING(14886, 50),
	ZAMORAK_MEDALLION(14885, 70),
	ARMADYL_TOTEM(14884, 90),
	GUTHIXIAN_BRAZIER(14883, 110),
	RUBY_CHALICE(14882, 140),
	BANDOS_STATUETTE(14881, 170),
	SARADOMIN_STATUETTE(14880, 200),
	ZAMORAK_STATUETTE(14879, 250),
	ARMADYL_STATUETTE(14878, 300),
	SEREN_STATUETTE(14877, 350),
	ANCIENT_STATUETTE(14876, 500);
	
	int itemId;
	int value;
	
	private AncientArtefacts(int itemId, int value) {
		this.itemId = itemId;
		this.value = value;
	}
	
	public int getItemId() {
		return itemId;
	}
	
	public int getNotedId() {
		return itemId + 17;
	}
	
	public int getValue() {
		return value;
	}
	
	/**
	 * Gets the total value for statuettes.
	 * @param player The player valuing.
	 * @return the Value.
	 */
	public static int getTotalValue(Player player) {
		int value = 0;
		for(Item item : player.getInventory().getItems().getItems()) {
			if (item == null)
				continue;
			AncientArtefacts statuette = null;
			for (AncientArtefacts stat : AncientArtefacts.values()) {
				if (stat.getItemId() == item.getId() || stat.getNotedId() == item.getId()) {
					statuette = stat;
				}
			}
			if (statuette != null) {
				value += statuette.getValue()*item.getAmount();
			}
		}
		return value;
	}
	
	/**
	 * Exchanges statuettes for pk points.
	 * @param player The player exchanging.
	 */
	public static void exchangeStatuettes(Player player) {
		final int total = getTotalValue(player);
		if (total > 0) {
			Dialogue dialogue = new Dialogue() {

				@Override
				public void start() {
			    	sendNPCDialogue(6539, NORMAL, "I can exchange all of your ancient artefacts for "
			    			+Utils.getFormattedNumber(total)+" PK Points.");
				}

				@Override
				public void run(int interfaceId, int componentId) {
				    switch (stage) {
				    case -1:
					    sendOptionsDialogue("Would you like to exchange all of your statuettes for "
					    		+Utils.getFormattedNumber(total)+" PK Points?",  "Yes.", "No.");
				    	stage = 0;
				    	break;
				    case 0:
						switch (componentId) {
						case OPTION_1:
							for(Item item : player.getInventory().getItems().getItems()) {
								if (item == null)
									continue;
								AncientArtefacts statuette = null;
								for (AncientArtefacts stat : AncientArtefacts.values()) {
									if (stat.getItemId() == item.getId() || stat.getNotedId() == item.getId()) {
										statuette = stat;
									}
								}
								if (statuette != null) {
									player.getInventory().deleteItem(item.getId(), item.getAmount());
									player.setPkPoints(player.getPkPoints() + statuette.getValue() * item.getAmount());
									player.sendMessage("You exchanged "+Utils.getAorAn(item.getName())+" "+item.getName()+" for "+statuette.getValue()+" PK Points.");
								}
							}
							stage = 1;
							break;
						case OPTION_2:
							sendPlayerDialogue(CALM, "No, I'll keep them for myself.");
							stage = 1;
							break;
						}
				    case 1:
				    	finish();
						break;
				    }
				}

				@Override
				public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }
				
			};
			player.getDialogueManager().startDialogue(dialogue);
		} else
			player.getDialogueManager().startDialogue("SimpleNPCMessage", 6539, "You do not have any statuettes to hand in.");
	}
}