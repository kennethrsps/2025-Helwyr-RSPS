package com.rs.game.player.controllers;

import com.rs.game.WorldObject;
import com.rs.game.activites.ZarosGodwars;

public class ZGDController extends Controller {

    @Override
    public void forceClose() {
    	remove();
    }

    @Override
    public boolean login() {
    	ZarosGodwars.addPlayer(player);
    	sendInterfaces();
    	return false; // so doesnt remove script
    }

    @Override
    public boolean logout() {
    	ZarosGodwars.removePlayer(player);
    	return false; // so doesnt remove script
    }

    @Override
    public void magicTeleported(int type) {
    	remove();
    	removeControler();
    }

    public void remove() {
    	ZarosGodwars.removePlayer(player);
    	player.getInterfaceManager().closeOverlay(false);
    	removeControler();
    }

    @Override
    public boolean sendDeath() {
    	remove();
    	removeControler();
    	return true;
    }

    @Override
    public void sendInterfaces() {
		player.getInterfaceManager().sendOverlay(601, false);
    }
    
    @Override
    public boolean processObjectClick1(final WorldObject object) {
		if (object.getId() == 57225) {
			player.getDialogueManager().startDialogue(
					"SimpleMessage", "The prison barrier is closed from this side.");
		}
    	return true;
    }

    @Override
    public void start() {
    	ZarosGodwars.addPlayer(player);
    	sendInterfaces();
    }
}