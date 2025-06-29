package com.rs.game.player.dialogue.impl;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.ShopsHandler;

/**
 * Grim Gem Dialogue.
 * @author Nate
 * @edited Zeus
 */
public class GrimGem extends Dialogue {

	@Override
	public void start() {
			sendNPCDialogue(14386, CROOKED_HEAD, "What can I do for you?");
			stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		String npcName = "";
		
		if (player.getContract() != null)
			npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName();
		
		switch (stage) {
		case 1:
			sendOptionsDialogue("Death",
					"I would like a new contract.",
					"I would like to see your shop.",
					"I would like to see your titles.");
			stage = 2;
			break;
		case 2:
			if (componentId == OPTION_1) {
				if (player.getContract() == null) {
					ContractHandler.getNewContract(player);
					npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName();
					sendNPCDialogue( 14386,
						    CROOKED_HEAD,
						"Your new contract is to kill:<br>",
						"<col=FF0000>"+player.getContract().getKillAmount()+"x "+npcName+"<br>",
						"Reward: <col=0000FF>"+player.getContract().getRewardAmount()+" Reaper Points");
					stage = 1;
					break;
				} else {
					sendNPCDialogue( 14386,
				    CROOKED_HEAD, "You already have a task, would you like to reset it?",
				    "<br><col=FF0000>"+player.getContract().getKillAmount()+"x "+npcName+"");
					stage = 3;
					break;
				}
			}
			if (componentId == OPTION_2) {
				sendNPCDialogue( 14386, CROOKED_HEAD, "Of course..");
				stage = 4;
				break;

			}
			if (componentId == OPTION_3) {
				if (player.getTotalContract() >= 500 || player.getTotalKills() >= 5000) {
					sendNPCDialogue(14386, CROOKED_HEAD, "Of course..");
					stage = 6;
					break;
				
				} else {
					sendNPCDialogue(14386,
					    CROOKED_HEAD, "You are not worthy of my titles.",
					    "<br><col=FF0000>"+player.getTotalKills()+"/5000 total kills.",
					    "<br><col=FF0000>"+player.getTotalContract()+"/500 total contracts.");
					stage = 1;
					break;
				}
			}
			if (componentId == OPTION_4) {
				end();
				break;
			}
		case 4: 
			ShopsHandler.openShop(player, 58);
			end();
			break;
		
		case 3:
			sendOptionsDialogue("Reset current assignment?", "Yes", "No");
			stage = 5;
			break;
			
		case 6:
			sendOptionsDialogue("Death",
					"<col=8A0808>The Reaper",
					"<col=8A0808><shad=9D1309>The Insane Reaper",
					"<col=8A0808>Final Boss",
					"<col=8A0808><shad=9D1309>Insane Final Boss",
					"Nevermind");
			stage = 7;
			break;

		case 5:
		if (componentId == OPTION_1) {
			if (player.getContract() == null) {
				sendNPCDialogue( 14386,
					    CROOKED_HEAD,
					    "You don't have an active contract, ",
						"collect a new one from me when you're ready.");
				stage = 1;
				break;
			} else {
				if (player.hasMoney(2500000)){
				    player.takeMoney(2500000); 
					player.setContract(null);
					ContractHandler.getNewContract(player);
					npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName();
					sendNPCDialogue( 14386,
						    CROOKED_HEAD,
							"Your new contract is to kill:<br>",
							"<col=FF0000>"+player.getContract().getKillAmount()+"x "+npcName+"<br>",
							"Reward: <col=0000FF>"+player.getContract().getRewardAmount()+" Reaper Points");
					stage = 1;
					break;
				} else {
					sendNPCDialogue( 14386,
						    CROOKED_HEAD,
							"Come back when you have more coins.",
							"You need at least 2,500,000 coins in order to reset your assignment."
);
					stage = 1;
					break;
				}
				
				}
			
			}
		if (componentId == OPTION_2) {
			sendNPCDialogue( 14386,
				    CROOKED_HEAD, "As you wish.");
			stage = 1;
			break;
			
		}
	
	case 7:
		if (componentId == OPTION_1) {
			if (player.getTotalContract() >= 500) {
				setTitle(1512);
				end();
			} else {
				sendNPCDialogue(14386, CROOKED_HEAD, "You need to complete 500 contracts to use this title.");
				stage = 1;
				break;
			}
		}
		if (componentId == OPTION_2) {
			if (player.getTotalContract() >= 1250) {
				setTitle(1513);
				end();
			} else {
				sendNPCDialogue(14386, CROOKED_HEAD, "You need to complete 1'250 contracts to use this title.");
				stage = 1;
				break;
			}
		}
		if (componentId == OPTION_3) {
			if (player.getTotalKills() >= 5000) {
				setTitle(1014);
				end();
			} else {
				sendNPCDialogue(14386, CROOKED_HEAD, "You need to complete 5'000 contract kills to use this title.");
				stage = 1;
				break;
			}
		}
		if (componentId == OPTION_4) {
			if (player.getTotalKills() >= 15000) {
				setTitle(1015);
				end();
			} else {
				sendNPCDialogue(14386, CROOKED_HEAD, "You need to complete 15'000 contract kills to use this title.");
				stage = 1;
				break;
			}
		}
		if (componentId == OPTION_5) {
			stage = 1;
			end();
			break;
		}
	}
}

	private void setTitle(int titleId) {
    	player.getGlobalPlayerUpdater().setTitle(titleId);
    	player.getGlobalPlayerUpdater().generateAppearenceData();
    	player.sendMessage("Your title has been successfully changed.");
    }
	
	@Override
	public void finish() { }
}