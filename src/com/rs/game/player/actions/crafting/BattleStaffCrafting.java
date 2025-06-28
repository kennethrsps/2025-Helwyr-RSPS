package com.rs.game.player.actions.crafting;

import java.util.HashMap;
import java.util.Map;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class BattleStaffCrafting extends Action{
	
	public enum Orbs{

		Water(571, 1391, 1395, 100, 54),
		
		Earth(575, 1391, 1399, 112.5, 58),
		
		Fire(569, 1391, 1393, 125, 62),
		
		Air(573, 1391, 1397, 137.5, 66),

		;

		private double experience;
		private int levelRequired, rawstaff, finishedstaff, orb;

		private Orbs(int orb, int rawstaff, int finishedstaff, double experience, int levelRequired) {
			this.orb = orb;
			this.rawstaff = rawstaff;
			this.finishedstaff = finishedstaff;
			this.experience = experience;
			this.levelRequired = levelRequired;
		}

		public double getExperience() {
			return experience;
		}

		public int getLevelRequired() {
			return levelRequired;
		}

		public int getRawstaff() {
			return rawstaff;
		}

		public int getFinishedstaff() {
			return finishedstaff;
		}

		public int getOrb() {
			return orb;
		}
		
		private static Map<Integer, Orbs> requiredproduct = new HashMap<Integer, Orbs>();
		
		static {
		    for (Orbs orb : Orbs.values()) {
		    	requiredproduct.put(orb.getOrb(), orb);
		    }
		}
	
		public static Orbs forId(int id) {
		    return requiredproduct.get(id);
		}
	
		
	}
	
	private Orbs orb;
	
	public BattleStaffCrafting(Player player, Orbs orb, int itemId){
		this.orb = orb;
		Orbs.forId(itemId);
		start(player);
	}
	
	
	@Override
	public boolean process(Player player) {
		if (player.getInterfaceManager().containsScreenInter()
				|| player.getInterfaceManager().containsInventoryInter()) {
			player.sendMessage("Please finish what you're doing before doing this action.");
			return false;
		}
			if(!player.getInventory().containsItem(orb.getOrb() ,1) ||
					!player.getInventory().containsItem(orb.getRawstaff() ,1)){
				player.sendMessage("no more required item left");
				return false;
			}
			if(orb.getOrb() != orb.orb){
				player.sendMessage("no more same type orb found");
				return false;
			}
		return true;
	}

	@Override
	public int processWithDelay(Player player) {
		player.getInventory().deleteItem(orb.getOrb(),1);
		player.getInventory().deleteItem(orb.getRawstaff(),1);
		player.getInventory().addItem(orb.finishedstaff,1);
		player.getSkills().addXp(Skills.CRAFTING, orb.getExperience());
		player.setNextAnimation(new Animation(9625));
		player.setNextGraphics(new Graphics(1692));
		player.addItemsMade();
		player.sendMessage("You attach the orb to the battlestaff; " + "items crafted: " + Colors.red
				+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
		return 2;
	}

	@Override
	public boolean start(Player player) {
			if (player.getSkills().getLevel(Skills.CRAFTING) < orb.getLevelRequired()) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You need a Crafting level of "+orb.getLevelRequired()+" to do this.");
				return false;
				}
		return true;
	}

	@Override
	public void stop(Player player) {
		setActionDelay(player, 2);
	}

	
	
	
}
