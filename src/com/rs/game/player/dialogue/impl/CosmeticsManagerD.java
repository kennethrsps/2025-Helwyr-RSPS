/*package com.rs.game.player.dialogue.impl;

import com.rs.game.player.content.CosmeticsHandler;
import com.rs.game.player.content.SkillCapeCustomizer;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class CosmeticsManagerD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option", "Open Cosmetics", "Open Cosmetics store", "Save Current Cosmetic", "Toggle Search Option", Colors.red + "More Options");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (componentId == OPTION_1) {
				end();
				CosmeticsHandler.openCosmeticsHandler(player);
			} else if (componentId == OPTION_2) {
				end();
				if (player.isShowSearchOption()) {
					player.getTemporaryAttributtes().put("CosmeticsStoreKeyWord", Boolean.TRUE);
					player.getPackets().sendInputNameScript("Enter the name of the outfit you would like to search for: ");
					return;
				}
				CosmeticsHandler.openCosmeticsStore(player, 0);
			} else if (componentId == OPTION_3) {
				end();
				player.stopAll();
				player.getTemporaryAttributtes().put("SaveCosmetic", Boolean.TRUE);
				player.getPackets().sendInputNameScript("Enter the name you want for your current costume: ");
			} else if (componentId == OPTION_4) {
				end();
				player.setShowSearchOption(!player.isShowSearchOption());
				player.getPackets().sendGameMessage("The cosmetics will " + (player.isShowSearchOption() ? "" : "no longer ") + "ask you for search option.");
			} else if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "Toggle filter locked cosmetics", "Reset Cosmetic", "Color Costume", "Reset Costume Color", Colors.red + "More Options");
				stage = 2;
			}
			break;
		case 2:
			if (componentId == OPTION_1) {
				end();
				player.setFilterLocked(!player.isFilterLocked());
				player.getPackets().sendGameMessage("You will now see " + (!player.isFilterLocked() ? "all the cosmetics" : "only the cosmetics u have unlocked") + ".");
			} else if (componentId == OPTION_2) {
				end();
				player.closeInterfaces();
				player.getEquipment().resetCosmetics();
				player.getGlobalPlayerUpdater().generateAppearenceData();

			} else if (componentId == OPTION_3) {
				end();
				SkillCapeCustomizer.costumeColorCustomize(player);

			} else if (componentId == OPTION_4) {
				end();
				player.getEquipment().setCostumeColor(12);

			} else if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "Reclaim Keepsake", Colors.red + "First page..");
				stage = 3;

			}
			break;
		case 3:
			if (componentId == OPTION_1) {
				end();
				player.stopAll();
				if (!player.canSpawn()) {
					player.getPackets().sendGameMessage("You can't reclaim your item at this moment.");
					return;
				}
				player.getDialogueManager().startDialogue("ClaimKeepSake");
			} else if (componentId == OPTION_2) {
				stage = -1;
				sendOptionsDialogue("Choose an Option", "Open Cosmetics", "Open Cosmetics store", "Save Current Cosmetic", "Toggle Search Option", Colors.red + "More Options");
			}
			break;
		}

	}

	@Override
	public void finish() {
	}

}*/