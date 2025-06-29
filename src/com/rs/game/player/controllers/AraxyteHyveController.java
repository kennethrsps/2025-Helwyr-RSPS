package com.rs.game.player.controllers;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.npc.araxxor.AraxyteNPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.OwnedObjectManager;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

/**
 * Handles the Araxyte Cave controller.
 * @author Zeus
 */
public class AraxyteHyveController extends Controller {

    @Override
    public void forceClose() {
    	remove();
    }

    @Override
    public boolean login() {
    	removePlayer(player);
    	player.setNextWorldTile(new WorldTile(4485, 6266, 1));
    	return true;
    }

    @Override
    public boolean logout() {
    	removePlayer(player);
    	return false;
    }

    @Override
    public void magicTeleported(int type) {
    	remove();
    	removeControler();
    }
    
    @Override
    public boolean processItemTeleport(WorldTile toTile) {
    	if (araxxor != null) {
	    	player.sendMessage(Colors.darkRed+"You can not leave the Araxyte Hyve this way; you must die or win.");
	    	return false;
    	}
    	return true;
    }

    @Override
    public boolean processMagicTeleport(WorldTile toTile) {
    	if (araxxor != null) {
	    	player.sendMessage(Colors.darkRed+"You can not leave the Araxyte Hyve this way; you must die or win.");
	    	return false;
    	}
    	return true;
    }

    @Override
    public boolean processObjectTeleport(WorldTile toTile) {
    	if (araxxor != null) {
	    	player.sendMessage(Colors.darkRed+"You can not leave the Araxyte Hyve this way; you must die or win.");
	    	return false;
    	}
    	return true;
    }

    /**
     * Removes player from Cave.
     */
    public void remove() {
    	removePlayer(player);
    	removeControler();
    }

    @Override
    public boolean sendDeath() {
    	remove();
    	removeControler();
    	return true;
    }
    
    @Override
    public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 91500) {
			if (player.getX() >= 4489) {
				player.getDialogueManager().startDialogue("SimpleMessage", 
						Colors.darkRed+"The Webbed entrance is closed from this side.");
			}
			return false;
		}
		if (object.getId() == 91673) {
			if (!OwnedObjectManager.isPlayerObject(player, object)) {
				player.sendMessage("It is not your loot.");
				return false;
			}
			AraxyteNPC.openRewards(player);
			//OwnedObjectManager.removeObject(player, object);
			return false;
		}
    	return true;
    }
    
    @Override
    public boolean processObjectClick2(final WorldObject object) {
    	if (object.getId() == 91673) {
    		Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(4485, 6266, 1));
    		return false;
    	}
    	return true;
    }

    @Override
    public void start() {
    	addPlayer(player);
    }
    
    @Override
    public boolean processButtonClick(int interfaceId, int componentId, int slotId, int packetId) {
    	if (interfaceId == 1284) {
    		switch (componentId) {
    	    case 8:
    	    	player.getBank().addItems(AraxyteNPC.getRewards().toArray(), true);
    	    	AraxyteNPC.getRewards().clear();
    	    	player.closeInterfaces();
    	    	player.sendMessage("All the items were moved to your bank.");
    	    	break;
    	    case 9:
    	    	AraxyteNPC.getRewards().clear();
    	    	player.closeInterfaces();
    	    	player.sendMessage("All the items were removed from the chest.");
    	    	break;
    	    case 10:
    	    	for (int slot = 0; slot < AraxyteNPC.getRewards().toArray().length; slot++) {
    	    		Item item = AraxyteNPC.getRewards().get(slot);
    	    		if (item == null) {
    	    			continue;
    	    		}
    	    		boolean added = true;
    	    		if (item.getDefinitions().isStackable() || item.getAmount() < 2) {
    	    			added = player.getInventory().addItem(item);
    	    			if (added) {
    	    				AraxyteNPC.getRewards().toArray()[slot] = null;
    	    			}
    	    		} else {
    	    			for (int i = 0; i < item.getAmount(); i++) {
    	    				Item single = new Item(item.getId());
    	    				if (!player.getInventory().addItem(single)) {
    	    					added = false;
    	    					break;
    	    				}
    	    				AraxyteNPC.getRewards().remove(single);
    	    			}
    	    		}
    	    		if (!added) {
    	    			player.sendMessage("You only had enough space in your inventory to accept some of the items.");
    	    			break;
    	    		}
    	    	}
    	    	break;
    	    case 7:
    	    	Item item = AraxyteNPC.getRewards().get(slotId);
    	    	if (item == null)
    	    		return true;
    	    	switch (packetId) {
    	    	case WorldPacketsDecoder.ACTION_BUTTON4_PACKET:
    	    		player.sendMessage("It's a " + item.getDefinitions().getName());
    	    		return false;
    	    	case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
    	    		AraxyteNPC.getRewards().toArray()[slotId] = null;
    	    		break;
    	    	case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
    	    		player.getBank().addItems( new Item[] { AraxyteNPC.getRewards().toArray()[slotId] }, false);
    	    		AraxyteNPC.getRewards().toArray()[slotId] = null;
    	    		break;
    	    	case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
    	    		boolean added = true;
    	    		if (item.getDefinitions().isStackable() || item.getAmount() < 2) {
    	    			added = player.getInventory().addItem(item);
    	    			if (added) {
    	    				AraxyteNPC.getRewards().toArray()[slotId] = null;
    	    			}
    	    		} else {
    	    			for (int i = 0; i < item.getAmount(); i++) {
    	    				Item single = new Item(item.getId());
    	    				if (!player.getInventory().addItem(single)) {
    	    					added = false;
    	    					break;
    	    				}
    	    				AraxyteNPC.getRewards().remove(single);
    	    			}
    	    		}
    	    		if (!added) {
    	    			player.sendMessage("You only had enough space in your inventory to accept some of the items.");
    	    			break;
    	    		}
    	    		break;
    	    	default:
    	    		return true;
    	    	}
    		}
    		return false;
    	}
    	return true;
    }
    
    /**
     * Adds player to cave.
     * @param player The player to add.
     */
    public static void addPlayer(final Player player) {
		synchronized (players) {
		    players.add(player);
		    if (players.size() == 1)
		    	startFight();
		}
    }

    /**
     * Removes player from cave.
     * @param player The player to remove.
     */
    public static void removePlayer(Player player) {
		synchronized (players) {
		    players.remove(player);
		    if (players.size() == 0)
		    	endFight();
		}
    }
    
    /**
     * Gets players in cave.
     * @return amount of players.
     */
    public static List<Player> getPlayers() {
    	return players;
    }
    
    /**
     * Starts Araxxor.
     */
    public static void startFight() {
    	WorldTasksManager.schedule(new WorldTask() {
    	    int stage = 0;

    	    @Override
    	    public void run() {
	    		if (players.isEmpty()) {
	    		    //Just incase.
	    		    NPC rax = World.findNPC(19463);
	    		    if (rax != null) {
	    		    	rax.finish();
	    		    	Logger.log("Araxxor force finished!");
	    		    }
	    		    stop();
	    		    return;
	    		}
	    		if (stage == 0) {
	    			araxxor = new AraxyteNPC(19463, new WorldTile(4503, 6262, 1));
	    			araxxor.setNextAnimation(new Animation(24076));
	    			araxxor.setCantInteract(true);
	    		} else if (stage == 3) {
	    			araxxor.setCantInteract(false);
	    			araxxor.start();
	    		    stop();
	    		}
	    		stage++;
    	    }
    	}, 5, 2);
    }
    
    public static AraxyteNPC araxxor;
    
    /**
     * Ends Araxxor NPC.
     */
    public static void endFight() {
		synchronized (players) {
		    if (araxxor != null) {
			    araxxor.removeSpider();
		    	araxxor.finish();
		    	araxxor = null;
		    }
		    //Just incase.
		    NPC rax = World.findNPC(19463);
		    if (rax != null) {
		    	rax.finish();
		    	Logger.log("Araxxor force finished!");
		    }
		    if (players.isEmpty())
		    	return;
		    
		    CoresManager.slowExecutor.schedule(new Runnable() {
	
				@Override
				public void run() {
				    try {
						if (players.isEmpty() || araxxor != null)
						    return;
						startFight();
				    } catch (Throwable e) {
				    	e.printStackTrace();
				    }
				}
		    }, 1, TimeUnit.MINUTES);
		}
    }
    
    /**
     * Gets a random target from Arax's cave.
     * @return The target.
     */
    public static Entity getRandomTarget() {
		synchronized (players) {
		    if (players.isEmpty())
		    	return null;
		    return players.get(Utils.random(players.size()));
		}
    }
    
    /**
     * List of players in Araxyte's Cave.
     */
    private static final List<Player> players = new LinkedList<Player>();

}