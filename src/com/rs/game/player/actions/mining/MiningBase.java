package com.rs.game.player.actions.mining;

import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Action;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public abstract class MiningBase extends Action {
	
    public static void propect(final Player player, final String endMessage) {
    	propect(player, "You examine the rock for ores....", endMessage);
    }

    public static void propect(final Player player, String startMessage, final String endMessage) {
		player.getPackets().sendGameMessage(startMessage, true);
		player.lock(5);
		WorldTasksManager.schedule(new WorldTask() {
		    @Override
		    public void run() {
		    	player.getPackets().sendGameMessage(endMessage);
		    }
		}, 4);
    }

    protected int emoteId;

    protected int pickaxeTime;

    public static boolean hasPickaxe(Player player) {
		if (player.getInventory().containsOneItem(15259, 1275, 1271, 1273, 1269, 
				1267, 1265, 13661, 32646))
		    return true;
		int weaponId = player.getEquipment().getWeaponId();
		if (weaponId == -1)
		    return false;
		switch (weaponId) {
		case 1265:// Bronze PickAxe
		case 1267:// Iron PickAxe
		case 1269:// Steel PickAxe
		case 1273:// Mithril PickAxe
		case 1271:// Adamant PickAxe
		case 1275:// Rune PickAxe
		case 15259:// Dragon PickAxe
		case 13661: // Inferno adze
		case 32646: // Crystal pickaxe
		    return true;
		default:
		    return false;
		}
    }

    protected boolean setPickaxe(Player player) {
		int level = player.getSkills().getLevel(Skills.MINING);
		int weaponId = player.getEquipment().getWeaponId();
		if (weaponId != -1) {
		    switch (weaponId) {
		    case 32646: // crystal pickaxe
				if (level >= 71) {
				    emoteId = 25157;
				    pickaxeTime = 15;
				    return true;
				}
				break;
		    case 15259: // dragon pickaxe
				if (level >= 61) {
				    emoteId = 12190;
				    pickaxeTime = 13;
				    return true;
				}
				break;
		    case 13661: // Inferno adze
				if (level >= 61) {
				    emoteId = 10222;
				    pickaxeTime = 13;
				    return true;
				}
				break;
		    case 1275: // rune pickaxe
				if (level >= 41) {
				    emoteId = 624;
				    pickaxeTime = 9;
				    return true;
				}
				break;
		    case 1271: // adam pickaxe
				if (level >= 31) {
				    emoteId = 628;
				    pickaxeTime = 7;
				    return true;
				}
				break;
		    case 1273: // mith pickaxe
				if (level >= 21) {
				    emoteId = 629;
				    pickaxeTime = 5;
				    return true;
				}
				break;
		    case 1269: // steel pickaxe
				if (level >= 6) {
				    emoteId = 627;
				    pickaxeTime = 3;
				    return true;
				}
				break;
		    case 1267: // iron pickaxe
				emoteId = 626;
				pickaxeTime = 2;
				return true;
		    case 1265: // bronze pickaxe
				emoteId = 625;
				pickaxeTime = 1;
				return true;
		    }
		}
		if (player.getInventory().containsOneItem(32646)) {
		    if (level >= 71) {
				emoteId = 25157;
				pickaxeTime = 15;
				return true;
		    }
		}
		if (player.getInventory().containsOneItem(15259)) {
		    if (level >= 61) {
				emoteId = 12190;
				pickaxeTime = 13;
				return true;
		    }
		}
		if (player.getInventory().containsOneItem(13661)) {
		    if (level >= 61) {
				emoteId = 10222;
				pickaxeTime = 13;
				return true;
		    }
		}
		if (player.getInventory().containsOneItem(1275)) {
		    if (level >= 41) {
				emoteId = 624;
				pickaxeTime = 9;
				return true;
		    }
		}
		if (player.getInventory().containsOneItem(1271)) {
		    if (level >= 31) {
				emoteId = 628;
				pickaxeTime = 7;
				return true;
		    }
		}
		if (player.getInventory().containsOneItem(1273)) {
		    if (level >= 21) {
				emoteId = 629;
				pickaxeTime = 5;
				return true;
		    }
		}
		if (player.getInventory().containsOneItem(1269)) {
		    if (level >= 6) {
				emoteId = 627;
				pickaxeTime = 3;
				return true;
		    }
		}
		if (player.getInventory().containsOneItem(1267)) {
		    emoteId = 626;
		    pickaxeTime = 2;
		    return true;
		}
		if (player.getInventory().containsOneItem(1265)) {
		    emoteId = 625;
		    pickaxeTime = 1;
		    return true;
		}
		return false;
    }

    @Override
    public void stop(Player player) {
    	player.setNextAnimation(new Animation (-1));
    	setActionDelay(player, 3);
    }
    
    /**
     * Starts/sets the animation and graphics for mining.
     * @param player The player to handle.
     */
    public void setAnimationAndGFX(Player player) {
    	if (player.getAnimations().hasChiMining && player.getAnimations().chiMining) {
			player.setNextAnimation(new Animation(17310));
			player.setNextGraphics(new Graphics(3304));
		}
    	else if (player.getAnimations().hasBlastMining && player.getAnimations().blastMining) {
			player.setNextAnimation(new Animation(17947));
			player.setNextGraphics(new Graphics(3918));
		} 
    	else if (player.getAnimations().hasStrongMining && player.getAnimations().strongMining) {
			player.setNextAnimation(new Animation(20284));
			player.setNextGraphics(new Graphics(3998));
		} 
    	else if (player.getAnimations().hasHeadMining && player.getAnimations().headMining)
			player.setNextAnimation(new Animation(17083));
		else
			player.setNextAnimation(new Animation(emoteId));
    }
}