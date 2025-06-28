package com.rs.game.player.actions.crafting;

import com.rs.game.Animation;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;

/**
 * Handles Tectonic energy crafting into Tectonic armor.
 * @author Zeus.
 */
public class EliteTectonicEnergyCrafting extends Action {
	
	public enum EliteTectonic {
											//Tectonic energy         //Armour piece
		Elite_TECTONIC_MASK(91, 500, new Item[] { new Item(43165, 140), new Item(28608, 1) }, new Item(43166)),
		Elite_TECTONIC_ROBE_TOP(93, 1500, new Item[] {new Item(43165, 420), new Item(28611, 1)}, new Item(43169)),
		Elite_TECTONIC_ROBE_BOTTOM(92, 1000, new Item[] {new Item(43165, 280), new Item(28614, 1)}, new Item(43172));
	
		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private Item energyProduce;
	
		private EliteTectonic(int levelRequired, double experience, Item[] itemsRequired, Item energyProduce) {
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

    public EliteTectonic scale;
    public int ticks;

    public EliteTectonicEnergyCrafting(EliteTectonic scale, int ticks) {
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
		if (player.getSkills().getLevel(Skills.RUNECRAFTING) < scale.getLevelRequired()) {
			player.sendMessage("You need a Runecrafting level of at least " + scale.getLevelRequired() + " to create a " + scale.getProduceEnergy().getDefinitions().getName());
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
		player.getSkills().addXp(Skills.RUNECRAFTING, xp);
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
     * Tectonic Armour.
     * Will rename everything once more customs like this gets added
     * and tables will be useful.
     */
    public static final EliteTectonic[] ARMOUR = new EliteTectonic[] {
    		EliteTectonic.Elite_TECTONIC_MASK,
    		EliteTectonic.Elite_TECTONIC_ROBE_TOP,
    		EliteTectonic.Elite_TECTONIC_ROBE_BOTTOM };
}