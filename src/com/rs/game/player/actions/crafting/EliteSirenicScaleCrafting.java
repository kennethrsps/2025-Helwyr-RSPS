package com.rs.game.player.actions.crafting;

import com.rs.game.Animation;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles Sirenic Scale crafting into  Elite Sirenic armour.
 * @author Kingkenobi.
 *
 */
public class EliteSirenicScaleCrafting extends Action {
	
	public enum EliteSirenic {
											//Ancient scale     // Armour piece    //Elite Armour piece
		ELITE_MASK(91, 500, new Item[] { new Item(43164, 140), new Item(29854, 1) }, new Item(43155)),
		ELITE_HAUBERK(93, 1500, new Item[] {new Item(43164, 420), new Item(29857, 1)}, new Item(43158)),
		ELITE_CHAPS(92, 1000, new Item[] {new Item(43164, 280), new Item(29860, 1)}, new Item(43161));
		
		/**ELITE_MASK_BARROW(91, 500, new Item[] { new Item(43164, 140), new Item(33348, 1) }, new Item(42982)),
		ELITE_HAUBERK_BARROW(93, 1500, new Item[] {new Item(43164, 420), new Item(33351, 1)}, new Item(42985)),
		ELITE_CHAPS_BARROW(92, 1000, new Item[] {new Item(43164, 280), new Item(33354, 1)}, new Item(42988)),
		
		ELITE_MASK_SHADOW(91, 500, new Item[] { new Item(43164, 140), new Item(33414, 1) }, new Item(43000)),
		ELITE_HAUBERK_SHADOW(93, 1500, new Item[] {new Item(43164, 420), new Item(33417, 1)}, new Item(43003)),
		ELITE_CHAPS_SHADOW(92, 1000, new Item[] {new Item(43164, 280), new Item(33420, 1)}, new Item(43006)),
		
		ELITE_MASK_TA(91, 500, new Item[] { new Item(43164, 140), new Item(33480, 1) }, new Item(43018)),
		ELITE_HAUBERK_TA(93, 1500, new Item[] {new Item(43164, 420), new Item(33483, 1)}, new Item(43021)),
		ELITE_CHAPS_TA(92, 1000, new Item[] {new Item(43164, 280), new Item(33486, 1)}, new Item(43024)),
		
		ELITE_MASK_BLOOD(91, 500, new Item[] { new Item(43164, 140), new Item(36285, 1) }, new Item(42955)),
		ELITE_HAUBERK_BLOOD(93, 1500, new Item[] {new Item(43164, 420), new Item(36288, 1)}, new Item(42958)),
		ELITE_CHAPS_BLOOD(92, 1000, new Item[] {new Item(43164, 280), new Item(36291, 1)}, new Item(42961)),
		
		ELITE_MASK_ICE(91, 500, new Item[] { new Item(43164, 140), new Item(42073, 1) }, new Item(43027)),
		ELITE_HAUBERK_ICE(93, 1500, new Item[] {new Item(43164, 420), new Item(42076, 1)}, new Item(43030)),
		ELITE_CHAPS_ICE(92, 1000, new Item[] {new Item(43164, 280), new Item(42079, 1)}, new Item(43033));
		**/

			
	
		private int levelRequired;
		private double experience;
		private Item[] itemsRequired;
		private Item energyProduce;
	
		private EliteSirenic(int levelRequired, double experience, Item[] itemsRequired, Item energyProduce) {
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

    public EliteSirenic scale;
    public int ticks;

    public EliteSirenicScaleCrafting(EliteSirenic scale, int ticks) {
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
		if (player.getSkills().getLevel(Skills.CRAFTING) < scale.getLevelRequired()) {
			player.sendMessage("You need a Crafting level of at least " + scale.getLevelRequired() + " to create a " + scale.getProduceEnergy().getDefinitions().getName());
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
		if (!player.getInventory().containsOneItem(1733)) {
		    player.sendMessage("You need a needle to craft Ancient scale.");
		    return false;
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
		player.getSkills().addXp(Skills.CRAFTING, xp);
		player.addItemsMade();
		player.sendMessage("You make a " + scale.getProduceEnergy().getDefinitions().getName().toLowerCase() + "; "
				+ "items crafted: "+Colors.red+Utils.getFormattedNumber(player.getItemsMade())+"</col>.", true);
		player.setNextAnimation(new Animation(1249));
		if (ticks > 0)
		    return 1;
		return -1;
    }

    @Override
    public void stop(Player player) {
    	setActionDelay(player, 3);
    }
    
    /**
     * Sirenic Armour.
     * Will rename everything once more customs like this gets added
     * and tables will be useful.
     */
    public static final EliteSirenic[] ARMOUR = new EliteSirenic[] {
    		EliteSirenic.ELITE_MASK,
    		EliteSirenic.ELITE_HAUBERK,
    		EliteSirenic.ELITE_CHAPS
    		/**
    		EliteSirenic.ELITE_MASK_BARROW,
    		EliteSirenic.ELITE_HAUBERK_BARROW,
    		EliteSirenic.ELITE_CHAPS_BARROW,
    		
    		EliteSirenic.ELITE_MASK_SHADOW,
    		EliteSirenic.ELITE_HAUBERK_SHADOW,
    		EliteSirenic.ELITE_CHAPS_SHADOW,
    		
    		EliteSirenic.ELITE_MASK_TA,
    		EliteSirenic.ELITE_HAUBERK_TA,
    		EliteSirenic.ELITE_CHAPS_TA,
    		
    		EliteSirenic.ELITE_MASK_BLOOD,
    		EliteSirenic.ELITE_HAUBERK_BLOOD,
    		EliteSirenic.ELITE_CHAPS_BLOOD,
    		
    		EliteSirenic.ELITE_MASK_ICE,
    		EliteSirenic.ELITE_HAUBERK_ICE,
    		EliteSirenic.ELITE_CHAPS_ICE
    		**/
    };
}