package com.rs.game.player.actions.divination.impl;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.NatureSpiritNPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.game.player.actions.divination.DivineObject;
import com.rs.game.player.controllers.Wilderness;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/**
 * Handles Divine location Woodcutting.
 * @author Zeus
 */
public final class DivineWoodcutting extends Action {

    public static enum DivineTreeDefinitions {

		DIVINE_MAGIC(75, 250, 1513, 5, 5),
	
		DIVINE_YEW(60, 175, 1515, 5, 5),
	
		DIVINE_MAPLE(45, 100, 1517, 5, 5),
	
		DIVINE_WILLOW(30, 67.5, 1519, 5, 5),
	
		DIVINE_OAK(15, 37.5, 1521, 5, 5),
	
		DIVINE_NORMAL(1, 25, 1511, 5, 5);
	
		private int level;
		private double xp;
		private int logsId;
		private int logBaseTime;
		private int logRandomTime;
	
		private DivineTreeDefinitions(int level, double xp, int logsId, int logBaseTime, int logRandomTime) {
		    this.level = level;
		    this.xp = xp;
		    this.logsId = logsId;
		    this.logBaseTime = logBaseTime;
		    this.logRandomTime = logRandomTime;
		}
	
		public int getLevel() {
		    return level;
		}
	
		public double getXp() {
		    return xp;
		}
	
		public int getLogsId() {
		    return logsId;
		}
	
		public int getLogBaseTime() {
		    return logBaseTime;
		}
	
		public int getLogRandomTime() {
		    return logRandomTime;
		}
    }

    private WorldObject tree;
    private DivineTreeDefinitions definitions;

    private int emoteId;
    private boolean usingBeaver;
    private int axeTime;

    public DivineWoodcutting(WorldObject tree, DivineTreeDefinitions definitions) {
		this.tree = tree;
		this.definitions = definitions;
    }

    @Override
    public boolean start(Player player) {
		if (!checkAll(player))
		    return false;
		setActionDelay(player, getWoodcuttingDelay(player));
		return true;
    }

    private int getWoodcuttingDelay(Player player) {
    	int summoningBonus = player.getFamiliar() != null ? 
    			(player.getFamiliar().getId() == 6808 || player.getFamiliar().getId() == 6807) ? 10 : 0 : 0;
    	int wcTimer = definitions.getLogBaseTime() - (player.getSkills().getLevel(8) + summoningBonus) - Utils.getRandom(axeTime);
    	if (wcTimer < 1 + definitions.getLogRandomTime())
    		wcTimer = 1 + Utils.getRandom(definitions.getLogRandomTime());
    	wcTimer /= player.getAuraManager().getWoodcuttingAccurayMultiplier();
    	return wcTimer;
    }

    private boolean checkAll(Player player) {
		Player owner = tree.getOwner();
		if (player != owner && ((player.isIronMan() || player.isHCIronMan()) && !owner.isDeveloper())) {
			player.getDialogueManager().startDialogue("SimpleMessage", 
					"Ironmen cannot use other player placed Divine locations.");
			return false;
		}
    	if (!hasAxe(player)) {
    		player.sendMessage("You need a hatchet to chop down this tree.");
    	    return false;
    	}
    	if (!setAxe(player)) {
    	    player.sendMessage("You dont have the required level to use that axe.");
    	    return false;
    	}
    	if (!hasWoodcuttingLevel(player))
    	    return false;
    	if (!player.getInventory().hasFreeSlots()) {
			player.sendMessage("Inventory full. To make room, sell, drop or bank something.", true);
    	    return false;
    	}
    	return true;
    }

    private boolean hasWoodcuttingLevel(Player player) {
		if (definitions.getLevel() > player.getSkills().getLevel(Skills.WOODCUTTING)) {
			player.sendMessage("You need a woodcutting level of " + definitions.getLevel() + " to chop down this tree.");
		    return false;
		}
		return true;
    }

    private boolean setAxe(Player player) {
    	int level = player.getSkills().getLevel(8);
    	int weaponId = player.getEquipment().getWeaponId();
    	if (weaponId != -1) {
    	    switch (weaponId) {
    	    case 6739: // dragon axe
    		if (level >= 61) {
    		    emoteId = 2846;
    		    axeTime = 13;
    		    return true;
    		}
    		break;
    	    case 13661: // Inferno adze which noob put inferno adze as the last tool??!?!?
    			if (level >= 61) {
    			    emoteId = 10251;
    			    axeTime = 13;
    			    return true;
    			}
    			break;
    	    case 1359: // rune axe
    		if (level >= 41) {
    		    emoteId = 867;
    		    axeTime = 10;
    		    return true;
    		}
    		break;
    	    case 1357: // adam axe
    		if (level >= 31) {
    		    emoteId = 869;
    		    axeTime = 7;
    		    return true;
    		}
    		break;
    	    case 1355: // mit axe
    		if (level >= 21) {
    		    emoteId = 871;
    		    axeTime = 5;
    		    return true;
    		}
    		break;
    	    case 1361: // black axe
    		if (level >= 11) {
    		    emoteId = 873;
    		    axeTime = 4;
    		    return true;
    		}
    		break;
    	    case 1353: // steel axe
    		if (level >= 6) {
    		    emoteId = 875;
    		    axeTime = 3;
    		    return true;
    		}
    		break;
    	    case 1349: // iron axe
    		emoteId = 877;
    		axeTime = 2;
    		return true;
    	    case 1351: // bronze axe
    		emoteId = 879;
    		axeTime = 1;
    		return true;
    	    }
    	}
    	if (player.getInventory().containsOneItem(6739)) {
    	    if (level >= 61) {
    		emoteId = 2846;
    		axeTime = 13;
    		return true;
    	    }
    	}
    	if (player.getInventory().containsOneItem(13661)) {
    	    if (level >= 61) {
    		emoteId = 10251;
    		axeTime = 13;
    		return true;
    	    }
    	}
    	if (player.getInventory().containsOneItem(1359)) {
    	    if (level >= 41) {
    		emoteId = 867;
    		axeTime = 10;
    		return true;
    	    }
    	}
    	if (player.getInventory().containsOneItem(1357)) {
    	    if (level >= 31) {
    		emoteId = 869;
    		axeTime = 7;
    		return true;
    	    }
    	}
    	if (player.getInventory().containsOneItem(1355)) {
    	    if (level >= 21) {
    		emoteId = 871;
    		axeTime = 5;
    		return true;
    	    }
    	}
    	if (player.getInventory().containsOneItem(1361)) {
    	    if (level >= 11) {
    		emoteId = 873;
    		axeTime = 4;
    		return true;
    	    }
    	}
    	if (player.getInventory().containsOneItem(1353)) {
    	    if (level >= 6) {
    		emoteId = 875;
    		axeTime = 3;
    		return true;
    	    }
    	}
    	if (player.getInventory().containsOneItem(1349)) {
    	    emoteId = 877;
    	    axeTime = 2;
    	    return true;
    	}
    	if (player.getInventory().containsOneItem(1351)) {
    	    emoteId = 879;
    	    axeTime = 1;
    	    return true;
    	}
    	if (player.getToolBelt().contains(1351)) {
    		emoteId = 879;
        	axeTime = 1;
        	return true;
        }
    	return false;
    }

    private boolean hasAxe(Player player) {
		if (player.getInventory().containsOneItem(1351, 1349, 1353, 1355, 1357, 1361, 1359, 6739, 13661))
		    return true;
		if (player.getToolBelt().contains(1351))
			return true;
		int weaponId = player.getEquipment().getWeaponId();
		if (weaponId == -1)
		    return false;
		switch (weaponId) {
		case 1351:// Bronze Axe
		case 1349:// Iron Axe
		case 1353:// Steel Axe
		case 1361:// Black Axe
		case 1355:// Mithril Axe
		case 1357:// Adamant Axe
		case 1359:// Rune Axe
		case 6739:// Dragon Axe
		case 13661: // Inferno adze
		    return true;
		default:
		    return false;
		}
    }
    	

    @Override
    public boolean process(Player player) {
    	player.setNextAnimation(new Animation(usingBeaver ? 1 : emoteId));
    	if (Utils.random(1000) == 0) {
    		new NatureSpiritNPC(player, player);
    		player.sendMessage("<col=ff0000>A Nature Spirit emerges from the tree.");
    	}
		player.faceObject(tree);
		if (!DivineObject.canHarvest(player)) {
		    player.setNextAnimation(new Animation(-1));
			return false;
		}
		return checkTree(player);
    }

    @Override
    public int processWithDelay(Player player) {
		addLog(player);
		if (!player.getInventory().hasFreeSlots()) {
			player.setNextAnimation(new Animation(-1));
			player.sendMessage("Inventory full. To make room, sell, drop or bank something.", true);
		    return -1;
		}
		return getWoodcuttingDelay(player);
    }

    private void addLog(Player player) {
    	Item hItem = new Item(definitions.getLogsId(), 1);
		int roll = Utils.random(100);
		Player owner = tree.getOwner();
		String logName = ItemDefinitions.getItemDefinitions(definitions.getLogsId()).getName().toLowerCase();
		player.getInventory().addItem(hItem);
		player.getSkills().addXp(Skills.WOODCUTTING, definitions.getXp() * woodcuttingSet(player));
		player.addLogsChopped();
		player.sendMessage("You get some " + hItem.getName().toLowerCase()
				+ "; total chopped: "+ Colors.red+Utils.getFormattedNumber(player.getLogsChopped())+"</col>.", true);
		DivineObject.handleHarvest(player);
		if (roll >= 75 && owner != null && player != owner && DivineObject.checkPercentage(owner) < 100) {
			if (Utils.random(100) >= 75)
				owner.gathered ++;
			if (!hItem.getDefinitions().isStackable())
				hItem.setId(hItem.getDefinitions().getCertId());
			owner.getInventory().addItem(hItem);
			owner.sendMessage(player.getDisplayName()+" chopped some "+logName+" for you.", true);
		}
    }

    private boolean checkTree(Player player) {
    	return World.containsObjectWithId(tree, tree.getId());
    }
    
    private double woodcuttingSet(Player player) {
    	double xpBoost = 1.0;
    	if (player.getEquipment().getChestId() == 10939)
    	    xpBoost *= 1.01;
    	if (player.getEquipment().getLegsId() == 10940)
    	    xpBoost *= 1.01;
    	if (player.getEquipment().getHatId() == 10941)
    	    xpBoost *= 1.01;
    	if (player.getEquipment().getBootsId() == 10933)
    	    xpBoost *= 1.01;
    	if (player.getEquipment().getChestId() == 10939
    		&& player.getEquipment().getLegsId() == 10940
    		&& player.getEquipment().getHatId() == 10941
    		&& player.getEquipment().getBootsId() == 10933)
    	    xpBoost *= 1.01;
    	if (Wilderness.isAtWild(player) && player.getEquipment().getGlovesId() == 13850)
    	    xpBoost *= 1.01;
    	return xpBoost;
    }

    @Override
    public void stop(Player player) {
    	setActionDelay(player, 3);
    }
}