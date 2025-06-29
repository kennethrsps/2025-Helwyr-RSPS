package com.rs.game.player.dialogue.impl;
 
import com.rs.game.player.content.CosmeticsHandler;
import com.rs.game.player.content.SkillCapeCustomizer;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
 
 
public class NewCosmeticD extends Dialogue {
 
    @Override
    public void start() {
        sendOptionsDialogue("Choose an Option", "Open Cosmetics",
                "Color Costume", "Save Current Cosmetic","Toggle Search Option",Colors.red+ "More Options");
            }
 
    @Override
    public void run(int interfaceId, int componentId) {
        switch (stage) {
        case -1:
       
            if (componentId == OPTION_1) {
				end();
                CosmeticsHandler.openCosmeticsHandler(player);
            }
			else if (componentId == OPTION_2) {
				end();
                SkillCapeCustomizer.costumeColorCustomize(player);
 
            }
			else if (componentId == OPTION_3) {
				end();
                player.stopAll();
                player.getTemporaryAttributtes().put("SaveCosmetic", Boolean.TRUE);
                player.getPackets().sendInputNameScript("Enter the name you want for your current costume: ");
            }
            else if (componentId == OPTION_4) {
				end();
                player.setShowSearchOption(!player.isShowSearchOption());
                player.getPackets().sendGameMessage("The cosmetics will "
                        + (player.isShowSearchOption() ? "" : "no longer ") + "ask you for search option.");
            }
            else if (componentId == OPTION_5) {
                sendOptionsDialogue("Choose an Option", "Reset Cosmetic",
                        "Rest Costume Color", "Reclaim Keepsake",Colors.red+ "Back");
                stage = 2;
               
                    }
            break;
        case 2:
            if (componentId == OPTION_1) {
				end();
                player.closeInterfaces();
                player.getEquipment().resetCosmetics();
                player.getGlobalPlayerUpdater().generateAppearenceData();
            }
            else if (componentId == OPTION_2) {
				end();
                player.getEquipment().setCostumeColor(12);
            }
            else if (componentId == OPTION_3) {
				end();
                player.stopAll();
                if (!player.canSpawn()) {
                    player.getPackets().sendGameMessage("You can't reclaim your item at this moment.");
                    return;
                }
                player.getDialogueManager().startDialogue("ClaimKeepSake");
            }
           
            else if (componentId == OPTION_4) {
                sendOptionsDialogue("Choose an Option", "Open Cosmetics",
                        "Color Costume", "Save Current Cosmetic","Toggle Search Option",Colors.red+ "More Option");
                stage = -1;
               
                    }
            break;
        }
 
    }
 
    @Override
    public void finish() {
        // TODO Auto-generated method stub
 
    }
 
}