package com.rs.game.player.actions.crafting;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.Animation;
import com.rs.game.WorldObject;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.DwarvenMinerNPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles the Crafting of Silver Bar products.
 * @author Zeus
 */
public class SilverCrafting extends Action {

    public enum SmeltingBar {

    	SILVER_SICKLE(18, 50, new Item[] { new Item(2355, 1) }, new Item(2961, 1), 0),

    	HOLY_SYMBOL(16, 50, new Item[] { new Item(2355, 1) }, new Item(1718, 1), 1),

    	UNHOLY_SYMBOL(16, 50, new Item[] { new Item(2355, 1) }, new Item(1724, 1), 2),

    	UNCHARGED_TIARA(23, 52.5, new Item[] { new Item(2355, 1) }, new Item(5525, 1), 3);

		private static Map<Integer, SmeltingBar> bars = new HashMap<Integer, SmeltingBar>();
	
		static {
		    for (SmeltingBar bar : SmeltingBar.values()) {
		    	bars.put(bar.getButtonId(), bar);
			}
		}
	
		public static SmeltingBar forId(int buttonId) {
		    return bars.get(buttonId);
		}
	
		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private int buttonId;
		private Item producedBar;
	
		private SmeltingBar(int levelRequired, double experience,
			Item[] itemsRequired, Item producedBar, int buttonId) {
		    this.levelRequired = levelRequired;
		    this.experience = experience;
		    this.itemsRequired = itemsRequired;
		    this.producedBar = producedBar;
		    this.buttonId = buttonId;
		}
	
		public int getButtonId() {
		    return buttonId;
		}
	
		public double getExperience() {
		    return experience;
		}
	
		public Item[] getItemsRequired() {
		    return itemsRequired;
		}
	
		public int getLevelRequired() {
		    return levelRequired;
		}
	
		public Item getProducedBar() {
		    return producedBar;
		}
    }

    public SmeltingBar bar;
    public WorldObject object;
    public int ticks;

    public SilverCrafting(int slotId, WorldObject object, int ticks) {
    	this.bar = SmeltingBar.forId(slotId);
    	this.object = object;
    	this.ticks = ticks;
    }

    @Override
    public boolean process(Player player) {
    	if (bar == null || player == null || object == null)
    		return false;
    	if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(), bar.getItemsRequired()[0].getAmount())) {
    		player.sendMessage("You need " + bar.getItemsRequired()[0].getDefinitions().getName() + " to create a "
			    + bar.getProducedBar().getDefinitions().getName() + ".");
    		return false;
    	}
		if (bar.getItemsRequired().length > 1) {
		    if (!player.getInventory().containsItem(bar.getItemsRequired()[1].getId(), bar.getItemsRequired()[1].getAmount())) {
		    	player.sendMessage("You need " + bar.getItemsRequired()[1].getDefinitions().getName()
					+ " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
		    	return false;
		    }
		}
		if (player.getSkills().getLevel(Skills.CRAFTING) < bar.getLevelRequired()) {
		    player.getDialogueManager().startDialogue(
			    "SimpleMessage",
			    "You need a Crafting level of at least "
				    + bar.getLevelRequired() + " to smelt "
				    + bar.getProducedBar().getDefinitions().getName());
		    return false;
		}
		if (Utils.random(500) == 0) {
			new DwarvenMinerNPC(player, player);
			player.sendMessage("<col=ff0000>A Dwarven Miner appears from nowhere.");
		}
		player.faceObject(object);
		return true;
    }

    @Override
    public int processWithDelay(Player player) {
    	ticks--;
    	player.setNextAnimation(new Animation(3243));
    	player.getSkills().addXp(Skills.CRAFTING, bar.getExperience());
    	for (Item required : bar.getItemsRequired()) {
    		player.getInventory().deleteItem(required.getId(), required.getAmount());
    	}
    	player.getInventory().addItem(bar.getProducedBar());
    	player.addItemsMade();
		player.sendMessage("You make a " + bar.getProducedBar().getDefinitions().getName().toLowerCase() + "; "
				+ "items crafted: "+Colors.red+Utils.getFormattedNumber(player.getItemsMade())+"</col>.", true);
    	if (ticks > 0)
    		return 1;
    	return -1;
    }

    @Override
    public boolean start(Player player) {
		if (bar == null || player == null || object == null)
		    return false;
		if (!player.getInventory().containsItem(bar.getItemsRequired()[0].getId(), bar.getItemsRequired()[0].getAmount())) {
			player.sendMessage("You need " + bar.getItemsRequired()[0].getDefinitions().getName() + " to create a "
			    + bar.getProducedBar().getDefinitions().getName() + ".");
			return false;
		}
		if (bar.getItemsRequired().length > 1) {
		    if (!player.getInventory().containsItem(bar.getItemsRequired()[1].getId(), bar.getItemsRequired()[1].getAmount())) {
		    	player.sendMessage("You need " + bar.getItemsRequired()[1].getDefinitions().getName()
					+ " to create a " + bar.getProducedBar().getDefinitions().getName() + ".");
		    	return false;
		    }
		}
		if (player.getSkills().getLevel(Skills.CRAFTING) < bar.getLevelRequired()) {
			player.sendMessage("You need a Crafting level of at least "
				    + bar.getLevelRequired() + " to make a "  + bar.getProducedBar().getDefinitions().getName());
		    return false;
		}
		player.sendMessage("You place the Silver bar and attempt to create a " 
				+ bar.getProducedBar().getDefinitions().getName().toLowerCase() + ".", true);
		return true;
    }

    @Override
    public void stop(Player player) {
    	this.setActionDelay(player, 3);
    }
}