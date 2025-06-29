package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.PlayerLook;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

/**
 * Used for handling the Player Settings dialogue.
 * @author Zeus
 */
public class PlayerSettings extends Dialogue {

    @Override
    public void start() {
    	sendOptionsDialogue(Colors.cyan+"Appearence Settings", 
				"Make-over Mage", "Hairdresser", "Thessalia's Clothes", "Skin Color", 
				(player.getOverrides() != null ? Colors.green : Colors.red)+ "Cosmetic Overrides");
    }

    @Override
    public void run(int interfaceId, int componentId) {
    	switch (stage) {
    	case -1:
    		switch (componentId) {
    		case OPTION_1:
    			finish();
				PlayerLook.openMageMakeOver(player);
    			break;
    		case OPTION_2:
    			finish();
				PlayerLook.openHairdresserSalon(player);
    			break;
    		case OPTION_3:
    			finish();
				PlayerLook.openThessaliasMakeOver(player);
    			break;
    		case OPTION_4:
    			if (!player.isBronze()) {
    				player.getDialogueManager().startDialogue("SimpleMessage", 
    						"If you'd like to edit your skin tone you must contribute at least $20.");
    				return;
    			}
    			player.getDialogueManager().startDialogue("Skincolor");
    			break;
    		case OPTION_5:
    			if (player.getOverrides() == null) {
    				sendDialogue("You have to have an activated override in order to edit these "
    						+ "settings.");
    				stage = 99;
    				return;
    			}
    			player.getInterfaceManager().closeScreenInterface();
    			sendOptionsDialogue("Cosmetic Override settings", 
    					(!player.getOverrides().showHelm ? Colors.green : Colors.red)+"Show Cosmetic helm", 
    					(!player.getOverrides().showBody ? Colors.green : Colors.red)+"Show Cosmetic body", 
    					(!player.getOverrides().showLegs ? Colors.green : Colors.red)+"Show Cosmetic legs", 
    					(!player.getOverrides().showCape ? Colors.green : Colors.red)+"Show Cosmetic cape", 
    					"More options..");
    			stage = 0;
    			break;
    		}
    		break;
    	case 0:
			stage = 0;
    		switch (componentId) {
    		case OPTION_1:
    			player.getOverrides().showHelm = !player.getOverrides().showHelm;
    			player.getOverrides().setOutfit(player.getOverrides().outfit);
    			player.getGlobalPlayerUpdater().generateAppearenceData();
    			sendOptionsDialogue("Cosmetic Override settings", 
    					(!player.getOverrides().showHelm ? Colors.green : Colors.red)+"Show Cosmetic helm", 
    					(!player.getOverrides().showBody ? Colors.green : Colors.red)+"Show Cosmetic body", 
    					(!player.getOverrides().showLegs ? Colors.green : Colors.red)+"Show Cosmetic legs", 
    					(!player.getOverrides().showCape ? Colors.green : Colors.red)+"Show Cosmetic cape", 
    					"More options..");
    			break;
    		case OPTION_2:
    			player.getOverrides().showBody = !player.getOverrides().showBody;
    			player.getOverrides().setOutfit(player.getOverrides().outfit);
    			player.getGlobalPlayerUpdater().generateAppearenceData();
    			sendOptionsDialogue("Cosmetic Override settings", 
    					(!player.getOverrides().showHelm ? Colors.green : Colors.red)+"Show Cosmetic helm", 
    					(!player.getOverrides().showBody ? Colors.green : Colors.red)+"Show Cosmetic body", 
    					(!player.getOverrides().showLegs ? Colors.green : Colors.red)+"Show Cosmetic legs", 
    					(!player.getOverrides().showCape ? Colors.green : Colors.red)+"Show Cosmetic cape", 
    					"More options..");
    			break;
    		case OPTION_3:
    			player.getOverrides().showLegs = !player.getOverrides().showLegs;
    			player.getOverrides().setOutfit(player.getOverrides().outfit);
    			player.getGlobalPlayerUpdater().generateAppearenceData();
    			sendOptionsDialogue("Cosmetic Override settings", 
    					(!player.getOverrides().showHelm ? Colors.green : Colors.red)+"Show Cosmetic helm", 
    					(!player.getOverrides().showBody ? Colors.green : Colors.red)+"Show Cosmetic body", 
    					(!player.getOverrides().showLegs ? Colors.green : Colors.red)+"Show Cosmetic legs", 
    					(!player.getOverrides().showCape ? Colors.green : Colors.red)+"Show Cosmetic cape", 
    					"More options..");
    			break;
    		case OPTION_4:
    			player.getOverrides().showCape = !player.getOverrides().showCape;
    			player.getOverrides().setOutfit(player.getOverrides().outfit);
    			player.getGlobalPlayerUpdater().generateAppearenceData();
    			sendOptionsDialogue("Cosmetic Override settings", 
    					(!player.getOverrides().showHelm ? Colors.green : Colors.red)+"Show Cosmetic helm", 
    					(!player.getOverrides().showBody ? Colors.green : Colors.red)+"Show Cosmetic body", 
    					(!player.getOverrides().showLegs ? Colors.green : Colors.red)+"Show Cosmetic legs", 
    					(!player.getOverrides().showCape ? Colors.green : Colors.red)+"Show Cosmetic cape", 
    					"More options..");
    			break;
    		case OPTION_5:
    			stage = 1;
    			sendOptionsDialogue("Cosmetic Override settings", 
    					(!player.getOverrides().showBoots ? Colors.green : Colors.red)+"Show Cosmetic boots", 
    					(!player.getOverrides().showGloves ? Colors.green : Colors.red)+"Show Cosmetic gloves", 
    					(!player.getOverrides().showWeapon ? Colors.green : Colors.red)+"Show Cosmetic weapon", 
    					(!player.getOverrides().showShield ? Colors.green : Colors.red)+"Show Cosmetic shield", 
    					"More options..");
    			break;
    		}
    		break;
    	case 1:
			stage = 1;
    		switch (componentId) {
    		case OPTION_1:
    			player.getOverrides().showBoots = !player.getOverrides().showBoots;
    			player.getOverrides().setOutfit(player.getOverrides().outfit);
    			player.getGlobalPlayerUpdater().generateAppearenceData();
    			sendOptionsDialogue("Cosmetic Override settings", 
    					(!player.getOverrides().showBoots ? Colors.green : Colors.red)+"Show Cosmetic boots", 
    					(!player.getOverrides().showGloves ? Colors.green : Colors.red)+"Show Cosmetic gloves", 
    					(!player.getOverrides().showWeapon ? Colors.green : Colors.red)+"Show Cosmetic weapon", 
    					(!player.getOverrides().showShield ? Colors.green : Colors.red)+"Show Cosmetic shield", 
    					"More options..");
    			break;
    		case OPTION_2:
    			player.getOverrides().showGloves = !player.getOverrides().showGloves;
    			player.getOverrides().setOutfit(player.getOverrides().outfit);
    			player.getGlobalPlayerUpdater().generateAppearenceData();
    			sendOptionsDialogue("Cosmetic Override settings", 
    					(!player.getOverrides().showBoots ? Colors.green : Colors.red)+"Show Cosmetic boots", 
    					(!player.getOverrides().showGloves ? Colors.green : Colors.red)+"Show Cosmetic gloves", 
    					(!player.getOverrides().showWeapon ? Colors.green : Colors.red)+"Show Cosmetic weapon", 
    					(!player.getOverrides().showShield ? Colors.green : Colors.red)+"Show Cosmetic shield", 
    					"More options..");
    			break;
    		case OPTION_3:
    			player.getOverrides().showWeapon = !player.getOverrides().showWeapon;
    			player.getOverrides().setOutfit(player.getOverrides().outfit);
    			player.getGlobalPlayerUpdater().generateAppearenceData();
    			sendOptionsDialogue("Cosmetic Override settings", 
    					(!player.getOverrides().showBoots ? Colors.green : Colors.red)+"Show Cosmetic boots", 
    					(!player.getOverrides().showGloves ? Colors.green : Colors.red)+"Show Cosmetic gloves", 
    					(!player.getOverrides().showWeapon ? Colors.green : Colors.red)+"Show Cosmetic weapon", 
    					(!player.getOverrides().showShield ? Colors.green : Colors.red)+"Show Cosmetic shield", 
    					"More options..");
    			break;
    		case OPTION_4:
    			player.getOverrides().showShield = !player.getOverrides().showShield;
    			player.getOverrides().setOutfit(player.getOverrides().outfit);
    			player.getGlobalPlayerUpdater().generateAppearenceData();
    			sendOptionsDialogue("Cosmetic Override settings", 
    					(!player.getOverrides().showBoots ? Colors.green : Colors.red)+"Show Cosmetic boots", 
    					(!player.getOverrides().showGloves ? Colors.green : Colors.red)+"Show Cosmetic gloves", 
    					(!player.getOverrides().showWeapon ? Colors.green : Colors.red)+"Show Cosmetic weapon", 
    					(!player.getOverrides().showShield ? Colors.green : Colors.red)+"Show Cosmetic shield", 
    					"More options..");
    			break;
    		case OPTION_5:
    			stage = 0;
    			sendOptionsDialogue("Cosmetic Override settings", 
    					(!player.getOverrides().showHelm ? Colors.green : Colors.red)+"Show Cosmetic helm", 
    					(!player.getOverrides().showBody ? Colors.green : Colors.red)+"Show Cosmetic body", 
    					(!player.getOverrides().showLegs ? Colors.green : Colors.red)+"Show Cosmetic legs", 
    					(!player.getOverrides().showCape ? Colors.green : Colors.red)+"Show Cosmetic cape", 
    					"More options..");
    			break;
    		}
    		break;
    	case 99:
    		finish();
    		break;
    	}
    }

    @Override
    public void finish() { player.getInterfaceManager().closeChatBoxInterface(); }

}