package com.rs.game.player.actions.crafting;

import com.rs.game.Animation;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;

/**
 * Handles Malevolent energy crafting into Malevolent armor.
 * @author Kingkenobi
 *.
 */
public class MalevolentCrafting extends Action {
	
	public enum Malevolent {
											//Malevolent energy   //Reinforcing plate  //Malevolent armor
		MALEVOLENT_HELM(91, 500, new Item[] { new Item(30027, 14), new Item(30028, 1) }, new Item(30005)),
		MALEVOLENT_CUIRASS(93, 1500, new Item[] {new Item(30027, 42), new Item(30028, 3)}, new Item(30008)),
		MALEVOLENT_GREAVES(92, 1000, new Item[] {new Item(30027, 28), new Item(30028, 2)}, new Item(30011));
	
		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private Item energyProduce;
	
		private Malevolent(int levelRequired, double experience, Item[] itemsRequired, Item energyProduce) {
		    this.levelRequired = levelRequired;
		    this.experience = experience;
		    this.itemsRequired = itemsRequired;
		    this.energyProduce = energyProduce;
		}
	
		public Item[] getItemsRequired() {
		    return itemsRequired;
		}
	
		public int getLevelRequired() {
		    return levelRequired;
		}
	
		public Item getProduceEnergy() {
		    return energyProduce;
		}
	
		public double getExperience() {
		    return experience;
		}
    }

    public Malevolent scale;
    public int ticks;

    public MalevolentCrafting(Malevolent scale, int ticks) {
		this.scale = scale;
		this.ticks = ticks;
    }

    @Override
    public boolean start(Player player) {
    	if (!process(player))
			return false;
		return true;
    }

    @Override
    public boolean process(Player player) {
		if (scale == null || player == null)
		    return false;
		if (ticks <= 0)
			return false;
		if (player.getSkills().getLevel(Skills.SMITHING) < scale.getLevelRequired()) {
			player.sendMessage("You need a Smithing level of at least " + scale.getLevelRequired() + " to create a " + scale.getProduceEnergy().getDefinitions().getName());
		    return false;
		}
    	int amount = scale.getItemsRequired()[0].getAmount();
		if (!player.getInventory().containsItem(scale.getItemsRequired()[0].getId(), amount)) {
			player.sendMessage("You need "+(amount > 1 ? "x"+amount+" of" : "a")+" " + scale.getItemsRequired()[0].getDefinitions().getName() + " to create " + scale.getProduceEnergy().getDefinitions().getName() + ".");
		    return false;
		}
		if (scale.getItemsRequired().length > 0) {
	    	amount = scale.getItemsRequired()[1].getAmount();
		    if (!player.getInventory().containsItem(scale.getItemsRequired()[1].getId(), amount)) {
				player.sendMessage("You need "+(amount > 1 ? "x"+amount+" of" : "a")+" " + scale.getItemsRequired()[1].getDefinitions().getName() + " to create " + scale.getProduceEnergy().getDefinitions().getName() + ".");
				return false;
		    }
		}
		return true;
    }

    @Override
    public int processWithDelay(Player player) {
		ticks--;
		double xp = scale.getExperience();
		for (Item required : scale.getItemsRequired())
		    player.getInventory().deleteItem(required.getId(), required.getAmount());
		int amount = scale.getProduceEnergy().getAmount();
		player.getInventory().addItem(scale.getProduceEnergy().getId(), amount);
		player.getSkills().addXp(Skills.SMITHING, xp);
		player.sendMessage("You make a "+scale.getProduceEnergy().getDefinitions().getName().toLowerCase()+".", true);
		player.setNextAnimation(new Animation(791));
		if (ticks > 0)
		    return 1;
		return -1;
    }

    @Override
    public void stop(Player player) {
    	setActionDelay(player, 3);
    }
    
    /**
     * MALEVOLENT Armour.
     * Will rename everything once more customs like this gets added
     * and tables will be useful.
     */
    public static final Malevolent[] ARMOUR = new Malevolent[] {
    		Malevolent.MALEVOLENT_HELM,
    		Malevolent.MALEVOLENT_CUIRASS,
    		Malevolent.MALEVOLENT_GREAVES
 };
}