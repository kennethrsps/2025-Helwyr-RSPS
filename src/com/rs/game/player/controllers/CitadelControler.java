package com.rs.game.player.controllers;

import com.rs.cores.CoresManager;
import com.rs.game.Graphics;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.clans.Clan;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.clans.citadels.CitadelObjects;
import com.rs.game.player.content.clans.citadels.ClanCitadel;
import com.rs.game.player.content.clans.interfaces.CitadelControl;
import com.rs.game.player.content.clans.interfaces.ResourceInterface;
import com.rs.utils.Logger;

/**
 * 
 * @author Frostbite
 *
 *<frostbitersps@gmail.com><skype@frostbitersps>
 */

public class CitadelControler extends Controller {

	private ClanCitadel citadel;
	private int clanTabSelected;

	/**
	 * Citadel Objects
	 * 22713 - Range
	 * 22714 - Range
	 * 34536 - range
	 * 34565 - range
	 * 52576 - range
	 * 
	 * 61333 - range
	 * 63209 - range
	 * 71899 - range
	 * 73286 - Range
	 * 73287 - Range
	 * 73288 - Range
	 * 65602 - range
	 */

	@Override
	public void start() {
		this.citadel = (ClanCitadel) getArguments()[0];
		getArguments()[0] = null;
		//player.getInterfaceManager().sendClanTaskTab();
		ResourceInterface.sendWoodCuttingComponents(player);
		ClanCitadel.startConfigs(player);
	}


	@Override
	public boolean processObjectClick1(WorldObject object) {
		if(CitadelObjects.handleOption1(player, object))
			return true;
		if (object.getId() == 58847) {
			if (player.getX() >= 0000 && player.getY() <= 0000) {
			player.setNextWorldTile(new WorldTile(player.getX(), player.getY() + 1, 0));
			return false;
			}
		}
		if(object.getId() == 59462) {
			ClansManager manager = player.getClanManager();
			if(!(manager == null)) {
				final long time = FadingScreen.fade(player);
				player.setNextGraphics(new Graphics(536));
				player.lock(3);
				CoresManager.slowExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							FadingScreen.unfade(player, time, new Runnable() {
								@Override
								public void run() {
									player.getClanManager().getClan().getClanCitadel().leaveCitadel(player);
								}
							});
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}

				});
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(WorldObject object) {
		if(player.getClanManager().getRank(player) >= Clan.DEPUTY_OWNER && object.getId() == 59464) {
			CitadelControl.sendCitadelControlTab1(player);
		} else {
			player.getDialogueManager().startDialogue("SimpleMessage", "You are not the required clan rank to access this.");
			return false;
		}
		return true;
	}

	public boolean processNPCClick1(NPC npc) {
		return true;
	}

	@Override
	public boolean processNPCClick2(NPC npc) {
		return true;
	}

	@Override
	public boolean processNPCClick3(NPC npc) {
		return true;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId,
			int slotId, int slotId2, int packetId) {
		if(interfaceId == 1117) {
			if(componentId == 61) {
				//player.getInterfaceManager().sendClanTaskTab();
				ResourceInterface.sendWoodCuttingComponents(player);
				return false;

			} else if(componentId == 62) {
				ResourceInterface.sendMiningComponents(player);
				return false;

			} else if(componentId == 64) {
				ResourceInterface.sendKilnComponents(player);
				return false;

			} else if(componentId == 66) {
				ResourceInterface.sendFurnaceComponents(player);
				return false;

			} else if(componentId == 68) {
				ResourceInterface.sendLoomComponents(player);
				return false;

			} else if(componentId == 70) {
				ResourceInterface.sendSummoningComponents(player);
				return false;

			} else if(componentId == 72) {
				ResourceInterface.sendBarbequeComponents(player);
				return false;
			}
		}

		if(interfaceId == 1258) {
			if(componentId == 124) {
				CitadelControl.sendCitadelControlTab1(player);
				return false;

			} else if(componentId == 125) {
				CitadelControl.sendCitadelControlTab2(player);
				return false;

			} else if(componentId == 127) {
				CitadelControl.sendCitadelControlTab4(player);
				return false;

			} else if(componentId == 228) {
				player.getPackets().sendConfigByFile(9577, 1);
				player.getPackets().sendConfig(2262, 1);
				player.getPackets().sendIComponentText(1258, 50, "Number");


			} else if(componentId == 219) {
				player.getPackets().sendConfigByFile(9565, 1);
				player.getPackets().sendIComponentText(1258, 50, "Number 2");
				return false;

			} else if(componentId == 210) {
				player.getPackets().sendConfigByFile(9561, 1);
				return false;
			}
		}

		if(interfaceId == 1259) {
			if(componentId == 38) {
				CitadelControl.sendCitadelControlTab2(player);
				return false;

			} else if(componentId == 39) {
				CitadelControl.sendCitadelControlTab3(player);
				return false;

			} else if(componentId == 40) {
				CitadelControl.sendCitadelControlTab4(player);
				return false;

			} else if(componentId == 108) {
				if(player.getClanManager().getClan().getClanCitadel().getCitadelIsNightTime() == true) {
					player.getClanManager().getClan().getClanCitadel().setCitadelIsNightTime(false);
					player.getClanManager().sendGlobalMessage("[<col=" + player.getClanManager().getClan().getCmHex() + ">" + player.getClanName() + " Announcement</col>]" + "<col=" +player.getClanManager().getClan().getCmHex() + ">" + player.getClanName() + " has just changed the Clan Citadel Time of Day to Day Time!");
					player.getDialogueManager().startDialogue("SimpleMessage", "You have changed your Clan Citadel time of day to Day Time.");
					return false;
				} else {
					player.getDialogueManager().startDialogue("SimpleMessage", "Your Clan Citadel Time of Day is already Day Time!");
				}

			} else if(componentId == 102) {
				if(player.getClanManager().getClan().getClanCitadel().getCitadelIsNightTime() == false) {
					player.getClanManager().getClan().getClanCitadel().setCitadelIsNightTime(true);
					player.getClanManager().sendGlobalMessage("[<col=" + player.getClanManager().getClan().getCmHex() + ">" + player.getClanName() + " Announcement</col>]" + "<col=" +player.getClanManager().getClan().getCmHex() + ">" + player.getClanName() + " has just changed the Clan Citadel Time of Day to Night Time!");
					player.getDialogueManager().startDialogue("SimpleMessage", "You have changed your Clan Citadel time of day to Night Time.");
					return false;
				} else {
					player.getDialogueManager().startDialogue("SimpleMessage", "Your Clan Citadel Time of Day is already Night Time!");
				}


			} else if(componentId == 93) {
				player.getClanManager().getClan().getClanCitadel().kickAll(player);
				return false;



//			} else if(componentId == 175) {//|| componentId == 176 || componentId == 177 || componentId == 178
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 1) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 50, -54)));// tier 1
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Portal.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 2) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 48, -24)));// tier 2
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Portal.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 3) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, -34, -51)));// tier 3
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Portal.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 0, -51)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Portal.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}
//
//			} else if(componentId == 174) {
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 1) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 0, 7)));// tier 1
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Keep.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 2) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, -32, 23)));// tier 2
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel keep.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 3) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 0, 24)));// tier 3
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Keep.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 0, -10)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Keep.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}
//
//			} else if(componentId == 173) {
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 1) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 11, -16)));// tier 1
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 1.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 2) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, -20, 0)));// tier 2
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 1.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 3) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 16, -11)));// tier 3
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 1.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, -52, -8)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 1.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}
//
//			} else if(componentId == 172) {
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 1) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, -13, -16)));// tier 1
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 2.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 2) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, -5, 11)));// Tier 2
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 2.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 3) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 40, -11)));// tier 3
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 2.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, -22, -8)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 2.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}
//
//			} else if(componentId == 171) {
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 2) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, -13, 45)));// Tier 2
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 3.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 3) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 12, -30)));// tier 3
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 3.");
//					return false;
//				}
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 21, -8)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 3.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}
//
//			} else if(componentId == 170) {
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 51, -8)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 4.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}
//
//			} else if(componentId == 169) {
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, -52, 16)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 5.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}
//
//			} else if(componentId == 168) {
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, -22, 16)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 6.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}
//
//			} else if(componentId == 167) {
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 21, 16)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 7.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}
//
//			} else if(componentId == 166) {
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 51, 16)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 8.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}
//
//			} else if(componentId == 165) {
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, -26, 31)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 9.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}
//
//			} else if(componentId == 164) {
//				if(player.getClanManager().getClan().getClanCitadel().getCitadelLayout() == 4) {
//					player.setNextWorldTile(new WorldTile(CitadelTeleportAction.getCitadelWorldTile(player, 27, 32)));// tier 4
//					player.closeInterfaces();
//					player.getDialogueManager().startDialogue("SimpleMessage", "You have been teleported to the Citadel Skill Plot 10.");
//					return false;
//				} else {
//					player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
//					return false;
//				}

			} else if(componentId == 163) {
				player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
				return false;

			} else if(componentId == 162) {
				player.getDialogueManager().startDialogue("SimpleMessage", player.getClanName() + "'s Traveling System could not take you there at this time.");
				return false;
			}

		} else if(interfaceId == 1260) {
			if(componentId == 95) {
				CitadelControl.sendCitadelControlTab1(player);
				return false;

			} else if(componentId == 96) {
				CitadelControl.sendCitadelControlTab2(player);
				return false;

			} else if(componentId == 97) {
				CitadelControl.sendCitadelControlTab3(player);
				return false;
			}

		} else if(interfaceId == 1261) {
			if(componentId == 131) {
				CitadelControl.sendCitadelControlTab1(player);
				return false;

			} else if(componentId == 132) {
				CitadelControl.sendCitadelControlTab3(player);
				return false;

			} else if(componentId == 133) {
				CitadelControl.sendCitadelControlTab4(player);
				return false;
			}
		}
		return true;
	}

	public int getClanTabSelected() {
		return clanTabSelected;
	}


	@Override
	public boolean login() {
		ClansManager manager = player.getClanManager();
		if(!(manager == null) && player.getClanManager().getClan().getClanCitadel().isCitadelGenerated() == true) {
			player.getClanManager().getClan().getClanCitadel().addMember(player);
			return false;

		} else if(!(player.getClanManager() == null)) {
			player.setNextWorldTile(new WorldTile(player.getHomeTile()));
			player.sm("You have been removed from the Clan Citadel.");
			player.getInterfaceManager().sendSquealOfFortune();
			player.getControlerManager().removeControlerWithoutCheck();
			return false;
		} else 
			player.getControlerManager().removeControlerWithoutCheck();
		player.setNextWorldTile(new WorldTile(new WorldTile(player.getHomeTile())));
		player.getInterfaceManager().sendSquealOfFortune();
		return false;
	}

	@Override
	public boolean logout() {
		if(!(player.getClanManager() == null))
			player.getClanManager().getClan().getClanCitadel().loggedOut(player);
		player.getInterfaceManager().sendSquealOfFortune();
		return false;
	}

	@Override
	public void magicTeleported(int type) {
		if(!(player.getClanManager() == null))
			player.getClanManager().getClan().getClanCitadel().leaveCitadel(player);
		player.getInterfaceManager().sendSquealOfFortune();
	}

	@Override
	public void forceClose() {
		player.getClanManager().getClan().getClanCitadel().loggedOut(player);
		player.getInterfaceManager().sendSquealOfFortune();
	}

	@Override
	public boolean checkWalkStep(int lastX, int lastY, int nextX, int nextY) {
		if(player.getClanManager() == null) {
			player.setNextWorldTile(new WorldTile(new WorldTile(player.getHomeTile())));
			player.getInterfaceManager().sendSquealOfFortune();
			player.getControlerManager().removeControlerWithoutCheck();
			player.lock();
		}
		return true;
	}


	public void sendMessage(String message) {
		player.getPackets().sendGameMessage(message);
	}


	public ClanCitadel getCitadel() {
		return citadel;
	}
}
