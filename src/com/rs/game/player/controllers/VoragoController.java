/*package com.rs.game.player.controllers;

import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.vorago.Vorago;
import com.rs.game.npc.vorago.VoragoHandler;
import com.rs.game.player.dialogue.impl.BossTeleports;
import com.rs.utils.Colors;

*//**
 * Handles the Vorago's borehole controller.
 * @author Zeus
 *//*
public class VoragoController extends Controller {
	
	public static int voragoPhase = 0;

    @Override
    public void forceClose() {
    	remove(false);
    }

    @Override
    public boolean login() {
    	remove(true);
    	VoragoHandler.removePlayer(player);
    	player.setNextWorldTile(new WorldTile(3039, 6124, 0));
    	player.getControlerManager().startControler("VoragoLobbyController");
    	return true;
    }

    @Override
    public boolean logout() {
    	VoragoHandler.removePlayer(player);
    	return false;
    }
    
    public static Vorago vorago;

    @Override
    public void magicTeleported(int type) {
    	remove(false);
    	removeControler();
    }
    
    @Override
    public boolean processItemTeleport(WorldTile toTile) {
    	if (vorago != null) {
    		player.sendMessage(Colors.red+"[Vorago] You cannot teleport out of this area.");
	    	return false;
    	}
    	return true;
    }

    @Override
    public boolean processMagicTeleport(WorldTile toTile) {
    	if (vorago != null) {
    		player.sendMessage(Colors.red+"[Vorago] You cannot teleport out of this area.");
	    	return false;
    	}
    	return true;
    }

    @Override
    public boolean processObjectTeleport(WorldTile toTile) {
    	if (vorago != null) {
    		player.sendMessage(Colors.red+"[Vorago] You cannot teleport out of this area.");
	    	return false;
    	}
    	return true;
    }

    *//**
     * Removes player from Cave.
     *//*
    public void remove(boolean login) {
    	VoragoHandler.removePlayer(player);
    	removeControler();
    }

    @Override
    public boolean sendDeath() {
    	remove(false);
    	removeControler();
    	return true;
    }
    
    @Override
    public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 84960) {
			BossTeleports.teleportPlayer(player, 3041, 6125, 0, "VoragoLobbyController");
			VoragoHandler.removePlayer(player);
			if (VoragoHandler.getPlayersCount() < 0) {
			    World.removeObject(object);
			    VoragoHandler.endFight();
			}
			return false;
		}
    	return true;
    }

    @Override
    public void start() {
    	VoragoHandler.addPlayer(player);
    	if (voragoPhase == 1)
    		player.setNextWorldTile(new WorldTile(3106, 6106, 0));
    	else if (voragoPhase == 2)
    		player.setNextWorldTile(new WorldTile(3104, 6048, 0));
    	else if (voragoPhase == 3)
    		player.setNextWorldTile(new WorldTile(3039, 6048, 0));
    	else if (voragoPhase == 4)
    		player.setNextWorldTile(new WorldTile(3040, 5984, 0));
    	else if (voragoPhase == 5)
    		player.setNextWorldTile(new WorldTile(3100, 5982, 0));
    	else
    		player.setNextWorldTile(new WorldTile(3106, 6106, 0));
    }
}*/