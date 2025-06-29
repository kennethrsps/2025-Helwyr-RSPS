package com.rs.game.player.dialogue.impl;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.content.contracts.Contract;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.ShopsHandler;

public class ContractDialogue extends Dialogue {

	public  static final int idNo = 14386;
	
	@Override
	public void start() {
		sendNPCDialogue( idNo,
			    CROOKED_HEAD,
				"Ahh.. "+player.getUsername()+" the only soul ",
				"I cannot claim. You're here about my",
				"special tasks, no?");
			stage = 1;
	}

	@Override
	public void run(int interfaceId, int componentId) {
		String npcName = "";
		
		if (player.getContract() != null) {
			npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName();
		}
		
		if (stage == 1) {
			sendOptionsDialogue("Select an Option",
					"I need another contract.",
					"I wish to check on my progress.",
					"I want to reset my task.",
					"I'd like to see your shop");
			stage = 2;
		} else if (stage == 2) {
			if (componentId == OPTION_1) {
				if (player.getContract() != null) {
					if (player.getContract().hasCompleted()) {
						player.setReaperPoints(player.getReaperPoints() + Contract.givePoints(player)); {
							player.setContract(null);
							sendNPCDialogue( idNo,
								    CROOKED_HEAD,
								"It looks like you haven't collected your reward yet.<br>",
								"Here you are, "+player.getUsername()+". Talk to me ",
								"for another contract.");
								stage = 1;
						}
							
					} else {
						sendNPCDialogue( idNo,
							    CROOKED_HEAD,
								"You already have an active contract.<br>",
								"Complete this one or get a new one if you want.<br>",
								"Current Contract: <col=FF0000>"+npcName+"</col><br>");
						stage = 1;
					}
				} else {
					ContractHandler.getNewContract(player);
					npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName();
					sendNPCDialogue( idNo,
						    CROOKED_HEAD,
						"Your new contract is to kill:<br>",
						"<col=FF0000>"+player.getContract().getKillAmount()+"x "+npcName+"<br>",
						"Reward: <col=0000FF>"+player.getContract().getRewardAmount()+" Reaper Points");
					player.getAchManager().addKeyAmount("death", 1);
					
					stage = 1;
				}
			}
			
			
			if (componentId == OPTION_2) {
				if (player.getContract() == null) {
					sendNPCDialogue( idNo,
						    CROOKED_HEAD,
						    "You don't have an active contract, ",
							"collect a new one from me when you're ready.");
						stage = 1;
				} else {
					if (player.getContract().hasCompleted()) {
						sendNPCDialogue( idNo,
							    CROOKED_HEAD,
								"You have a completed contract to turn in, ",
								"you must turn it in before starting a new one.");
						stage = 1;
					} else {
						sendNPCDialogue( idNo,
							    CROOKED_HEAD,
							"You still have an active contract!<br>",
							"Current: <col=FF0000>"+player.getContract().getKillAmount()+"x "+npcName+"<br>",
							"Reward: <col=0000FF>"+player.getContract().getRewardAmount()+" Reaper Points ");
						stage = 1;
					}
				}
			}
			
			if (componentId == OPTION_3) {
				sendNPCDialogue( idNo,
					    CROOKED_HEAD,
					"Choosing this will cost you <col=FF0000>2,500,000 coins</col><br>",
					"Do you wish to proceed?");
				stage = 3;				
			}
			
			if (componentId == OPTION_4) {
				sendNPCDialogue( idNo,
					    CROOKED_HEAD,
					"Of course...");
				stage = 5;
			}
			if (componentId == OPTION_5) {
				end();
			}
			
		} else if (stage == 3) {
			sendOptionsDialogue("Select an Option",
					"Yes, I wish to change my contract",
					"No, I do not want to change my contract");
			stage = 4;
		} else if (stage == 4) {
			if (componentId == OPTION_1) {
				if (player.getContract() == null) {
					sendNPCDialogue( idNo,
						    CROOKED_HEAD,
						    "You don't have an active contract, ",
							"collect a new one from me when you're ready.");
					stage = 1;
				} else {
					if (player.hasMoney(2500000)){
					     player.takeMoney(2500000); 
						player.setContract(null);
						ContractHandler.getNewContract(player);
						npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName();
						sendNPCDialogue( idNo,
							    CROOKED_HEAD,
								"Your new contract is to kill:<br>",
								"<col=FF0000>"+player.getContract().getKillAmount()+"x "+npcName+"<br>",
								"Reward: <col=0000FF>"+player.getContract().getRewardAmount()+" Reaper Points");
						stage = 1;
					} else {
						sendNPCDialogue( idNo,
							    CROOKED_HEAD,
								"Come back when you have more coins.",
								"You need atleast 2,500,000 coins to reset your Contract."
);
						stage = 1;
					}
				}
			} else {
				end();
			}
		} else if (stage == 5) {
				ShopsHandler.openShop(player, 58);
				end();
		}
	}

	@Override
	public void finish() {  }
}