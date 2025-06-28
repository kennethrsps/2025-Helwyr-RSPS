package com.rs.game.player.content;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.WizardFinixNPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.pet.Pets;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public final class RuneCrafting {

    public final static int[] LEVEL_REQ = { 1, 25, 50, 75 };

    public final static int RUNE_ESSENCE = 1436, PURE_ESSENCE = 7936,
	    AIR_TIARA = 5527, MIND_TIARA = 5529, WATER_TIARA = 5531,
	    BODY_TIARA = 5533, EARTH_TIARA = 5535, FIRE_TIARA = 5537,
	    COSMIC_TIARA = 5539, NATURE_TIARA = 5541, CHAOS_TIARA = 5543,
	    LAW_TIARA = 5545, DEATH_TIARA = 5547, BLOOD_TIARA = 5549,
	    SOUL_TIARA = 5551, ASTRAL_TIARA = 9106, OMNI_TIARA = 13655;
    
    public static final int[] POUCH_SIZE = { 3, 6, 9, 12 };

    public static void checkPouch(Player p, int i) {
		if (i < 0)
		    return;
		p.sendMessage("This pouch has " + p.getPouches()[i] + " rune essences in it.", false);
    }

    public static void craftEssence(Player player, int rune, int level, double experience, boolean pureEssOnly, int... multipliers) {
		int actualLevel = player.getSkills().getLevel(Skills.RUNECRAFTING);
		if (actualLevel < level) {
		    player.getDialogueManager().startDialogue("SimpleMessage",
			    "You need a Runecrafting level of " + level + " to craft this rune.");
		    return;
		}
		int runes = player.getInventory().getItems().getNumberOf(PURE_ESSENCE);
		
		//Armadyl runes
		if (rune == 556 && actualLevel >= 70) {
			int shards = player.getInventory().getAmountOf(21774);
			if (shards > 0) {
				runes = shards;
				rune = 21773;
				player.getInventory().deleteItem(21774, runes);
			}
		}
		
		if (runes > 0)
		    player.getInventory().deleteItem(PURE_ESSENCE, runes);
		if (!pureEssOnly) {
		    int normalEss = player.getInventory().getItems().getNumberOf(RUNE_ESSENCE);
		    if (normalEss > 0) {
		    	player.getInventory().deleteItem(RUNE_ESSENCE, normalEss);
		    	runes += normalEss;
		    }
		}
		if (runes == 0) {
		    player.getDialogueManager().startDialogue("SimpleMessage",
			    "You don't have " + (pureEssOnly ? "pure" : "rune") + " essence.");
		    return;
		}
		if (player.isLocked())
			return;
		player.lock(1);
		double totalXp = experience * runes;
		if (RuneCrafting.hasEtherealOutfit(player))
			totalXp *= 1.15;
		if (Utils.random(50 - runes) == 0) {
			new WizardFinixNPC(player, player);
			player.sendMessage("<col=ff0000>A Runecrafting Wizard appears out of nowhere.");
		}
		player.getSkills().addXp(Skills.RUNECRAFTING, totalXp);
		
		String runeName = ItemDefinitions.getItemDefinitions(rune).getName().toLowerCase();
		String message = null;
		if (runeName.contains("air")) {
			player.addAirRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getAirRunesMade()) + "</col>";
		} 
		if (runeName.contains("water")) {
			player.addWaterRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getWaterRunesMade()) + "</col>";
		} 
		if (runeName.contains("earth")) {
			player.addEarthRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getEarthRunesMade()) + "</col>";
		} 
		if (runeName.contains("mind")) {
			player.addMindRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getMindRunesMade()) + "</col>";
		} 
		if (runeName.contains("fire")) {
			player.addFireRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getFireRunesMade()) + "</col>";
		} 
		if (runeName.contains("body")) {
			player.addBodyRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getBodyRunesMade()) + "</col>";
		} 
		if (runeName.contains("cosmic")) {
			player.addCosmicRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getCosmicRunesMade()) + "</col>";
		} 
		if (runeName.contains("chaos")) {
			player.addChaosRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getChaosRunesMade()) + "</col>";
		} 
		if (runeName.contains("nature")) {
			player.addNatureRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getNatureRunesMade()) + "</col>";
		} 
		if (runeName.contains("law")) {
			player.addLawRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getLawRunesMade()) + "</col>";
		} 
		if (runeName.contains("death")) {
			player.addDeathRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getDeathRunesMade()) + "</col>";
		} 
		if (runeName.contains("blood")) {
			player.addBloodRunesMade(runes);
			message = Colors.red + Utils.getFormattedNumber(player.getBloodRunesMade()) + "</col>";
		}
		player.addRunesMade(runes);
		for (int i = multipliers.length - 2; i >= 0; i -= 2) {
			if (actualLevel >= multipliers[i]) {
				runes *= multipliers[i + 1];
				break;
			}
		}
		player.setNextGraphics(new Graphics(186, 0, 110));
		player.setNextAnimation(new Animation(791));
		player.getDailyTaskManager().process(rune, runes);
		player.getInventory().addItem(rune, runes);
		Pets.checkSkillingPets(player, 38103);
		player.getAchManager().addKeyAmount("runecrafting", 1);
		player.sendMessage("You bind the temple's power into " + runeName + "s; runes made: "+message+"; "
				+ "total: "+Colors.red+Utils.getFormattedNumber(player.getRunesMade())+"</col>.");
    }

    public static void emptyPouch(Player p, int i) {
	if (i < 0)
	    return;
	int toAdd = p.getPouches()[i];
	if (toAdd > p.getInventory().getFreeSlots())
	    toAdd = p.getInventory().getFreeSlots();
	if (toAdd > 0) {
	    p.getInventory().addItem(1436, toAdd);
	    p.getPouches()[i] -= toAdd;
	}
	if (toAdd == 0) {
	    p.getPackets().sendGameMessage(
		    "Your pouch has no essence left in it.", false);
	    return;
	}
    }

    public static void enterAirAltar(Player player) {
	enterAltar(player, new WorldTile(2841, 4829, 0));
    }

    private static void enterAltar(Player player, WorldTile dest) {
	player.getPackets().sendGameMessage(
		"A mysterious force grabs hold of you.");
	player.useStairs(-1, dest, 0, 1);
    }

    public static void enterBodyAltar(Player player) {
	enterAltar(player, new WorldTile(2522, 4825, 0));
    }

    public static void enterEarthAltar(Player player) {
	enterAltar(player, new WorldTile(2655, 4830, 0));
    }

    public static void enterFireAltar(Player player) {
	enterAltar(player, new WorldTile(2574, 4848, 0));
    }

    public static void enterMindAltar(Player player) {
	enterAltar(player, new WorldTile(2792, 4827, 0));
    }

    public static void enterWaterAltar(Player player) {
	enterAltar(player, new WorldTile(3482, 4838, 0));
    }

    public static void fillPouch(Player p, int i) {
	if (i < 0)
	    return;
	if (LEVEL_REQ[i] > p.getSkills().getLevel(Skills.RUNECRAFTING)) {
	    p.getPackets().sendGameMessage(
		    "You need a runecrafting level of " + LEVEL_REQ[i]
			    + " to fill this pouch.", false);
	    return;
	}
	int essenceToAdd = POUCH_SIZE[i] - p.getPouches()[i];
	if (essenceToAdd > p.getInventory().getItems().getNumberOf(1436))
	    essenceToAdd = p.getInventory().getItems().getNumberOf(1436);
	if (essenceToAdd > POUCH_SIZE[i] - p.getPouches()[i])
	    essenceToAdd = POUCH_SIZE[i] - p.getPouches()[i];
	if (essenceToAdd > 0) {
	    p.getInventory().deleteItem(1436, essenceToAdd);
	    p.getPouches()[i] += essenceToAdd;
	}
	if (!p.getInventory().containsOneItem(1436)) {
	    p.getPackets().sendGameMessage(
		    "You don't have any essence with you.", false);
	    return;
	}
	if (essenceToAdd == 0) {
	    p.getPackets().sendGameMessage("Your pouch is full.", false);
	    return;
	}
    }

    public static boolean hasRcingSuit(Player player) {
		if (player.getEquipment().getHatId() == 21485
			&& player.getEquipment().getChestId() == 21484
			&& player.getEquipment().getLegsId() == 21486
			&& player.getEquipment().getBootsId() == 21487)
		    return true;
	return false;
    }
    
    /**
     * Ethereal Outfits.
     * http://runescape.wikia.com/wiki/Infinity_ethereal_outfit
     */
    public static boolean hasEtherealOutfit(Player player) {
    	if (player.getEquipment().getHatId() == 32342
    			&& player.getEquipment().getChestId() == 32343
    			&& player.getEquipment().getLegsId() == 32344
    			&& player.getEquipment().getGlovesId() == 32345
    			&& player.getEquipment().getBootsId() == 32346)
    	    return true;
    	if (player.getEquipment().getHatId() == 32347
    			&& player.getEquipment().getChestId() == 32348
    			&& player.getEquipment().getLegsId() == 32349
    			&& player.getEquipment().getGlovesId() == 32350
    			&& player.getEquipment().getBootsId() == 32351)
    	    return true;
    	if (player.getEquipment().getHatId() == 32352
    			&& player.getEquipment().getChestId() == 32353
    			&& player.getEquipment().getLegsId() == 32354
    			&& player.getEquipment().getGlovesId() == 32355
    			&& player.getEquipment().getBootsId() == 32356)
    	    return true;
    	if (player.getEquipment().getHatId() == 32357
    			&& player.getEquipment().getChestId() == 32358
    			&& player.getEquipment().getLegsId() == 32359
    			&& player.getEquipment().getGlovesId() == 32360
    			&& player.getEquipment().getBootsId() == 32361)
    	    return true;
    	return false;
    }
    
    /**
     * XP modifier by wearing items.
     * @param player The player.
     * @return the XP modifier.
     */
    public static double runecrafterSuit(Player player) {
    	double xpBoost = 1.0;
    	if (player.getEquipment().getHatId() == 21485)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getChestId() == 21484)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getLegsId() == 21486)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getBootsId() == 21487)
    		xpBoost *= 1.01;
    	if (player.getEquipment().getHatId() == 21485
    			&& player.getEquipment().getChestId() == 21484
    			&& player.getEquipment().getLegsId() == 21486
    			&& player.getEquipment().getBootsId() == 21487)
    		xpBoost *= 1.01;
    	return xpBoost;
    }

    public static boolean isTiara(int id) {
	return id == AIR_TIARA || id == MIND_TIARA || id == WATER_TIARA
		|| id == BODY_TIARA || id == EARTH_TIARA || id == FIRE_TIARA
		|| id == COSMIC_TIARA || id == NATURE_TIARA
		|| id == CHAOS_TIARA || id == LAW_TIARA || id == DEATH_TIARA
		|| id == BLOOD_TIARA || id == SOUL_TIARA || id == ASTRAL_TIARA
		|| id == OMNI_TIARA;
    }

    public static void locate(Player p, int xPos, int yPos) {
	String x = "";
	String y = "";
	int absX = p.getX();
	int absY = p.getY();
	if (absX >= xPos)
	    x = "west";
	if (absY > yPos)
	    y = "South";
	if (absX < xPos)
	    x = "east";
	if (absY <= yPos)
	    y = "North";
	p.getPackets().sendGameMessage(
		"The talisman pulls towards " + y + "-" + x + ".", false);
    }
    
    /**
     * Blank Tiara infusing.
     */
	public static final int[] OBJECTS =
		{ 2478, 2481, 2482, 2480, 2483, 2479, 30624, 2487, 2484, 2488, 2485, 2478, 2486 };
	private static final int[] TIARA =
		{ AIR_TIARA, EARTH_TIARA, FIRE_TIARA, WATER_TIARA, BODY_TIARA, MIND_TIARA, BLOOD_TIARA, 
				CHAOS_TIARA, COSMIC_TIARA, DEATH_TIARA, LAW_TIARA, SOUL_TIARA, NATURE_TIARA };

	public static void infuseTiara(Player player, int index) {//up to blood tiara
		int tiaraId = TIARA[index];
		int talismanId = (index * 2) + 1438;
		if (player.getInventory().containsItem(5525, 1) && player.getInventory().containsItem(talismanId, 1)) {
			player.getInventory().deleteItem(new Item(talismanId, 1));
			player.getInventory().deleteItem(new Item(5525, 1));
			player.getInventory().addItem(new Item(tiaraId));
			player.getPackets().sendGameMessage("You infuse the tiara with the power of your talisman.");
		}
	}
}