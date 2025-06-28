package com.rs.network.protocol.codec.decode.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TimerTask;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.activites.Crucible;
import com.rs.game.activites.PuroPuro;
import com.rs.game.activites.ZombieOutpost.TowerObject;
import com.rs.game.activites.ZombieOutpost.ZOControler;
import com.rs.game.activites.ZombieOutpost.ZombieShop;
import com.rs.game.activites.duel.DuelControler;
import com.rs.game.activites.pest.CommendationExchange;
import com.rs.game.activites.resourcegather.SkillingEventsManager;
import com.rs.game.item.Item;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.familiar.Familiar.SpecialAttack;
import com.rs.game.player.EmotesManager;
import com.rs.game.player.Equipment;
import com.rs.game.player.Inventory;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.FightPitsViewingOrb;
import com.rs.game.player.actions.HomeTeleport;
import com.rs.game.player.actions.Rest;
import com.rs.game.player.actions.crafting.JewellerySmithing;
import com.rs.game.player.actions.herblore.herbicide.Herbicide;
import com.rs.game.player.actions.magic.EnchantBolt;
import com.rs.game.player.actions.magic.EnchantBolt.Bolt;
import com.rs.game.player.actions.smithing.Smithing.ForgingInterface;
import com.rs.game.player.content.AccountInterfaceManager;
import com.rs.game.player.content.BossBalancer;
import com.rs.game.player.content.BossTeleports;
import com.rs.game.player.content.BossTimerManager;
import com.rs.game.player.content.FairyRing;
import com.rs.game.player.content.InterfaceManager;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.ItemSets;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.MinigameTeleports;
import com.rs.game.player.content.PlayerDesign;
import com.rs.game.player.content.PlayerLook;
import com.rs.game.player.content.PvPTeleports;
import com.rs.game.player.content.QuestTab;
import com.rs.game.player.content.Shop;
import com.rs.game.player.content.SkillCapeCustomizer;

import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.SpiritTree;
import com.rs.game.player.content.TaskTab;
import com.rs.game.player.content.TeleportSystem;
import com.rs.game.player.content.TrainingTeleports;
import com.rs.game.player.content.ancientthrone.ThroneManager;
import com.rs.game.player.content.clans.Clan;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.clans.content.achievements.ClanAchievements;
import com.rs.game.player.content.construction.House;
import com.rs.game.player.content.dailylogin.DailyLoginInter;
import com.rs.game.player.content.death.Gravestone;
import com.rs.game.player.content.dungeoneering.DungeonRewardShop;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.content.interfaces.arealoot.AreaLoot;
import com.rs.game.player.content.interfaces.potionTimer.PotionTimersInter;
import com.rs.game.player.content.items.Bonecrusher;
import com.rs.game.player.content.items.PrayerBooks;
import com.rs.game.player.content.summoning.Summoning;
import com.rs.game.player.controllers.ImpossibleJad;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.dialogue.impl.CasinoEntranceD;
import com.rs.game.player.dialogue.impl.LevelUp;
import com.rs.game.player.dialogue.impl.Transportation;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;
import com.rs.stream.InputStream;
import com.rs.utils.Colors;
import com.rs.utils.ItemExamines;
import com.rs.utils.Logger;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;

public class ButtonHandler {

	public static void handleButtons(final Player player, InputStream stream, int packetId) {
		int interfaceHash = stream.readIntV2();
		int interfaceId = interfaceHash >> 16;
		if (Utils.getInterfaceDefinitionsSize() <= interfaceId) {
			// hack, or server error or client error
			// player.getSession().getChannel().close();
			// player.sendMessage(Colors.red+"Interface Handler ERROR; Please
			// report this to an Admin ASAP!");
			return;
		}
		if (player.isDead())
			return;
		final int componentId = interfaceHash & 0xFF;
		// System.err.println("inter: "+interfaceId+" comp: "+componentId);
		int weaponId = player.getEquipment().getWeaponId();
		if (componentId != 65535 && Utils.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId) {
			// player.sendMessage(Colors.red+"Interface Handler ERROR; Please
			// report this to an Admin ASAP!");
			return;
		}
		final int slotId2 = stream.readUnsignedShort128();
		final int slotId = stream.readUnsignedShortLE128();
		if (player.getUsername().equalsIgnoreCase("Zeus") || player.getUsername().equalsIgnoreCase(""))
			player.getPackets().sendPanelBoxMessage("ID: " + interfaceId + "; compId: " + componentId + "; slotId: "
					+ slotId + "; slotId2: " + slotId2 + "; packetId: " + packetId + ".");
		if (!player.getControlerManager().processButtonClick(interfaceId, componentId, slotId, packetId))
			return;
		if (!player.getControlerManager().processButtonClick(interfaceId, componentId, slotId, slotId2, packetId))
			return;
		if (!player.getNewQuestManager().processButtonClick(interfaceId, componentId, slotId, slotId2, packetId))
			return;
		if (interfaceId == 919) {
			player.getResourceGather().getResourceMapTemp().clear();
			player.getInterfaceManager().closeChatBoxInterface();
		}
		if (interfaceId == 922) {
			if (componentId == 232) {
				player.closeInterfaces();
			}
		}
		if (player.getDeathManager() != null) {
			if (player.getDeathManager().handleInterface(interfaceId, componentId, packetId, slotId, slotId2))
				return;
		}

		if (interfaceId == 652) {
			if (componentId == 34) {
				Gravestone.sendBuySelection(player, slotId);
				return;
			}
		}
		if (interfaceId == 3006) {
			AreaLoot.handleButtons(player, componentId, slotId, slotId2);
			return;
		}
		if (interfaceId == 3005) {
			PotionTimersInter.handleButtons(player, componentId, slotId, slotId2, packetId);
			return;
		}
		if (interfaceId == 3004) {
			DailyLoginInter.handleButtons(player, componentId, packetId);
			return;
		}
		if (interfaceId == 1006) {
			if (player.getTemporaryAttributtes().get("bonecrusher") != null) {
				Bonecrusher.handleBonecrusher(player, componentId);
				return;
			}
			Herbicide.handleHerbicide(player, componentId);
			return;

		}
		if (interfaceId == 979) {
			player.getGrManager().handleButtons(componentId);
			return;
		}
		if (interfaceId == 1157) {
			AccountInterfaceManager.handleInterface(player, componentId);
			return;
		}
		if (interfaceId == 1083) {
			player.getAchManager().handleButtons(componentId);
			return;
		}
		if (interfaceId == 1219) {
			player.getAchManager().openRewards();
			return;
		}
		if (interfaceId == 392 || interfaceId == 1284) {
			player.getThrone().handleCollectedResources(componentId, slotId, packetId);
		}
		if (interfaceId == 391) {
			player.getThrone().handleKingdomInterface(componentId);
		}
		if (interfaceId == 1117) {
			SkillingEventsManager.handleButtons(player, componentId);
		}
		if (interfaceId == 109) {
			if (componentId == 58) {
				if (packetId == 32) {
					player.getPackets().sendGameMessage(sendLoanItemExamine(slotId2));
					return;
				}
				player.getTrade().handleCollectButton(player);
			}
		} else if (interfaceId == 3024) {
			if (componentId == 1) {
				player.closeInterfaces();
			} else if (componentId == 2) {
				if (player.timerPage == 0) {
					player.timerPage = 1;
					BossTimerManager.sendInterface(player, 1);
				} else if (player.timerPage == 1) {
					player.timerPage = 2;
					BossTimerManager.sendInterface(player, 2);
				}
			} else if (componentId == 3) {
				if (player.timerPage == 0) {
					BossTimerManager.sendInterface(player, 2);
					player.timerPage = 2;
				} else if (player.timerPage == 1) {
					BossTimerManager.sendInterface(player, 0);
					player.timerPage = 0;
				}
				if (player.timerPage == 2) {
					BossTimerManager.sendInterface(player, 1);
					player.timerPage = 1;
				}
			}
		}
		if (interfaceId == 206 && componentId == 15
				&& (player.getControlerManager().getControler() instanceof ZOControler)) {
			if (packetId == 14) {
				ZombieShop.sendPrice(player, slotId2);
			} else {
				ZombieShop.buyItem(player, slotId2);
			}
			return;
		}
		if (interfaceId == 1008) {
			TowerObject.handleButtons(player, componentId);
			return;
		}
		if (interfaceId == 164 || interfaceId == 161 || interfaceId == 378) {
			player.getSlayerManager().handleRewardButtons(interfaceId, componentId);
			return;
		}
		if (interfaceId == 1310) {
			if (componentId == 0) {
				player.getSlayerManager().createSocialGroup(true);
				player.setCloseInterfacesEvent(null);
			}
			player.closeInterfaces();
			return;
		}

		if (interfaceId == 1309) {// TODO
			if (componentId == 20)
				player.getPackets().sendGameMessage(
						"Use your enchanted stone ring onto the player that you would like to invite.", true);
			else if (componentId == 22) {
				Player p2 = player.getSlayerManager().getSocialPlayer();
				if (p2 == null)
					player.getPackets().sendGameMessage("You have no slayer group, invite a player to start one.");
				else
					player.getPackets().sendGameMessage(
							"Your current slayer group consists of you and " + p2.getDisplayName() + ".");
			} else if (componentId == 24)
				player.getSlayerManager().resetSocialGroup(true);
			player.closeInterfaces();
			return;
		}
		if (interfaceId == 1312 || interfaceId == 960 || interfaceId == 1263 || interfaceId == 668
				|| interfaceId == 1074
				|| (interfaceId == 1082 && player.getTemporaryAttributtes().get("CosmeticsStore") != null)) {
			player.getDialogueManager().continueDialogue(interfaceId, componentId);
			if (Settings.DEBUG)
				Logger.log("Continue dialogue - interface: " + interfaceId + "; component: " + componentId + ".");
			return;
		}
		if (interfaceId == 105 || interfaceId == 107 || interfaceId == 109 || interfaceId == 449) {
			player.getGEManager().handleButtons(interfaceId, componentId, slotId, packetId);
			return;
		}
		if (interfaceId == 1312 || interfaceId == 960 || interfaceId == 1263 || interfaceId == 668) {
			player.getDialogueManager().continueDialogue(interfaceId, componentId);
			if (Settings.DEBUG)
				Logger.log("Continue dialogue - interface: " + interfaceId + "; component: " + componentId + ".");
			return;
		}
		if (interfaceId == 375) {
			if (componentId == 3) {
				JewellerySmithing.resetTransformation(player);
				return;
			}
		}
		if (interfaceId == 1243) {
			switch (componentId) {
			case 46:
			case 51:
				player.getNewQuestManager().getCurrent().start(componentId == 46 ? true : false);
				break;
			}
			return;
		}
		if (interfaceId == 640) {
			if (player.getControlerManager().getControler() != null) {
				if (componentId == 18 || componentId == 22) {
					player.getTemporaryAttributtes().put("WillDuelFriendly", true);
					player.getVarsManager().sendVar(283, 67108864);
				} else if (componentId == 19 || componentId == 21) {
					player.getTemporaryAttributtes().put("WillDuelFriendly", false);
					player.getVarsManager().sendVar(283, 134217728);
				} else if (componentId == 20) {
					DuelControler.challenge(player, false);
				}
				return;
			}
			if (componentId == 18 || componentId == 22) {
				player.getTemporaryAttributtes().put("WillDuelFriendly", true);
				player.getVarsManager().sendVar(283, 67108864);
			} else if (componentId == 19 || componentId == 21) {
				player.getPackets().sendGameMessage("Stake option is disabled atm.");
				return;
				// player.getTemporaryAttributtes().put("WillDuelFriendly",
				// false);
				// player.getVarsManager().sendVar(283, 134217728);
			} else if (componentId == 20) {
				DuelControler.challenge(player, true);
			}
			return;
		}
		if (interfaceId == DungeonRewardShop.REWARD_SHOP) {
			if (componentId == 2) {
				if (slotId % 5 == 0) {
					DungeonRewardShop.select(player, slotId);
				}
			} else if (componentId == 64)
				DungeonRewardShop.sendConfirmationPurchase(player);
			else if (componentId == 48)
				DungeonRewardShop.purchase(player);
			else if (componentId == 50)
				DungeonRewardShop.removeConfirmationPurchase(player);
			return;
		}

		else if (interfaceId == 939) {
			if (componentId >= 59 && componentId <= 72) {
				int playerIndex = (componentId - 59) / 3;
				if ((componentId & 0x3) != 0)
					player.getDungManager().pressOption(playerIndex,
							packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET ? 0
									: packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET ? 1 : 2);
				else
					player.getDungManager().pressOption(playerIndex, 3);
			} else if (componentId == 45)
				player.getDungManager().formParty();
			else if (componentId == 36 || componentId == 33)
				player.getDungManager().checkLeaveParty();
			else if (componentId == 43)
				player.getDungManager().invite();
			else if (componentId == 102)
				player.getDungManager().changeComplexity();
			else if (componentId == 108)
				player.getDungManager().changeFloor();
			else if (componentId == 87)
				player.getDungManager().openResetProgress();
			else if (componentId == 94)
				player.getDungManager().switchGuideMode();
			else if (componentId == 112)
				player.getInterfaceManager().closeDungPartyInterface();
		} else if (interfaceId == 949) {
			if (componentId == 65)
				player.getDungManager().acceptInvite();
			else if (componentId == 61 || componentId == 63)
				player.closeInterfaces();
		} else if (interfaceId == 938) {
			if (componentId >= 56 && componentId <= 81)
				player.getDungManager().selectComplexity((componentId - 56) / 5 + 1);
			else if (componentId == 39)
				player.getDungManager().confirmComplexity();
		} else if (interfaceId == 947) {
			if (componentId >= 48 && componentId <= 107)
				player.getDungManager().selectFloor((componentId - 48) + 1);
			else if (componentId == 254)
				player.getDungManager().confirmFloor();
		} else if (interfaceId == 953 || interfaceId == 954 || interfaceId == 946 || interfaceId == 936) {
			int base = interfaceId == 953 ? 10 : interfaceId == 954 ? 50 : interfaceId == 946 ? 12 : 134;
			int multi = 3;
			int max = (base + (3 * multi));
			if (componentId >= base && componentId <= max)
				player.getDungManager().openInspectInterface(((componentId - base) / multi));
			if ((interfaceId == 936 && componentId == 146) || (interfaceId == 946 && componentId == 7)
					|| (interfaceId == 953 && componentId == 5) || (interfaceId == 954 && componentId == 6))
				player.getDungManager().closeInspectInterface();
		} else if (interfaceId == 946) {
			if (componentId >= 10 && componentId <= 19)
				player.getDungManager().openInspectInterface(((componentId - 52) / 3));
		}
		if (interfaceId == 813) { // starts new quest tab teleport handling
			int selection = InterfaceManager.getPlayerInterfaceSelected();
			if (selection == 1) {
				BossTeleports.handleInterface(player, componentId);
			}
			if (selection == 2) {
				MinigameTeleports.handleInterface(player, componentId);
			}
			if (selection == 3) {
				TrainingTeleports.handleInterface(player, componentId);
			}
			if (selection == 4) {
				PvPTeleports.handleInterface(player, componentId);
			}
			if (selection == 5) {
				player.getDialogueManager().startDialogue("SkillingTeleports");
			}

		}

		if (interfaceId == 1156) {
			player.getDominionTower().handleButtons(interfaceId, componentId);
		}
		if (interfaceId == 540) {
			if (componentId == 69)
				PuroPuro.confirmPuroSelection(player);
			else if (componentId == 71)
				ShopsHandler.openShop(player, 32);
			else
				PuroPuro.handlePuroInterface(player, componentId);
			return;
		} else if (interfaceId == 3056) {
			TeleportSystem.handleButtons(componentId, packetId, player);
		}
		if (interfaceId == 363) {
			if (componentId == 4)
				player.getTreasureTrails().movePuzzlePeice(slotId);
		}

		else if (interfaceId == 864) {
			SpiritTree.handleSpiritTree(player, slotId);
		}
		if (interfaceId == 365)
			player.getTreasureTrails().handleSextant(componentId);
		if (interfaceId == 432) {
			switch (componentId) {
			case 14:
				EnchantBolt.enchant(player, Bolt.OPAL);
				break;
			case 29:
				EnchantBolt.enchant(player, Bolt.SAPPHIRE);
				break;
			case 18:
				EnchantBolt.enchant(player, Bolt.JADE);
				break;
			case 22:
				EnchantBolt.enchant(player, Bolt.PEARL);
				break;
			case 32:
				EnchantBolt.enchant(player, Bolt.EMERALD);
				break;
			case 26:
				EnchantBolt.enchant(player, Bolt.REDTOPAZ);
				break;
			case 35:
				EnchantBolt.enchant(player, Bolt.RUBY);
				break;
			case 38:
				EnchantBolt.enchant(player, Bolt.DIAMOND);
				break;
			case 41:
				EnchantBolt.enchant(player, Bolt.DRAGONSTONE);
				break;
			case 44:
				EnchantBolt.enchant(player, Bolt.ONYX);
				break;
			}
		}
		if (interfaceId == 1139 || interfaceId == 1252 || interfaceId == 1253) {
			player.getSquealOfFortune().processClick(packetId, interfaceId, componentId);
			return;
		}
		if (interfaceId == 675) {
			JewellerySmithing.handleButtonClick(player, componentId, packetId == 14 ? 1 : packetId == 67 ? 5 : 10);
			return;
		}
		if (interfaceId == 1082) {
			player.getTitles().handleShop(componentId);
			return;
		}
		if (interfaceId == 746) {
			if (componentId == 75)
				TaskTab.sendTab(player);
		}

		if (interfaceId == 644 || interfaceId == 645) {
			ItemSets.handleButtons(player, interfaceId, slotId, slotId2, packetId);
			return;
		}
		if (interfaceId == 548) {
			if (componentId == 99)
				TaskTab.sendTab(player);
		}
		if (interfaceId == 17) {
			if (componentId == 28) {
				openItemsKeptOnDeath(player, true);
			}
		}

		if (interfaceId == 1311) {
			// PlayerCustomization.handleButtons(player, componentId);
		}

		if (interfaceId == 1253 && componentId == 2) {
			player.closeInterfaces();
		}

		if (interfaceId == 1311 && componentId == 88) {
			player.getPackets().closeInterface(1311);
		}
		if (interfaceId == 734) {
			if (componentId == 21)
				FairyRing.confirmRingHash(player);
			else
				FairyRing.handleDialButtons(player, componentId);
			return;
		}
		if (interfaceId == 735) {
			if (componentId >= 14 && componentId <= 14 + 64)
				FairyRing.sendRingTeleport(player, componentId - 14);
			return;
		}
		if (interfaceId == 548 || interfaceId == 746) {
			if ((interfaceId == 548 && componentId == 148) || (interfaceId == 746 && componentId == 199)) {
				if (player.getInterfaceManager().containsScreenInter()
						|| player.getInterfaceManager().containsInventoryInter()) {
					player.sendMessage("Please finish what you're doing before opening the world map.");
					return;
				}
				int posHash = player.getX() << 14 | player.getY();
				player.getPackets().sendGlobalConfig(622, posHash);
				player.getPackets().sendGlobalConfig(674, posHash);
				player.getPackets().sendWindowsPane(755, 0);
				return;
			} else if ((interfaceId == 746 && componentId == 207) || (interfaceId == 548 && componentId == 159)) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					player.getMoneyPouch().switchPouch();
					player.getMoneyPouch().refresh();
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					if (player.getInterfaceManager().containsScreenInter()) {
						player.sendMessage("You cannot withdraw your coins right now.");
						return;
					}
					if (CasinoEntranceD.CasinoArea(player, player)) {
						player.getPackets()
								.sendGameMessage(Colors.red + "You cannot withdraw your coins in the casino");
						return;
					}
					if (player.getMoneyPouch().getTotal() > 2) {
						player.getTemporaryAttributtes().put("money_pouch_remove", Boolean.TRUE);
						player.getPackets().sendRunScript(108,
								new Object[] { "Your money pouch contains "
										+ Utils.getFormattedNumber(player.getMoneyPouch().getTotal()) + "."
										+ "<br>How many would you like to withdraw?" });
					} else if (player.getMoneyPouch().getTotal() == 1) {
						player.getMoneyPouch().removeMoneyMisc(1);
					} else
						player.sendMessage("Your money pouch is empty.");
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					player.getMoneyPouch().sendExamine();
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					if (player.getInterfaceManager().containsScreenInter()) {
						player.sendMessage("You cannot open the price checker right now.");
						return;
					}
					player.getPriceCheckManager().openPriceCheck();
				}
			} else if ((interfaceId == 548 && componentId == 17) || (interfaceId == 746 && componentId == 54)) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getSkills().switchXPDisplay();
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getSkills().switchXPPopup();
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getSkills().setupXPCounter();
			}
		} else if (interfaceId == 34) {// TODO notes interface
			if (Settings.DEBUG)
				System.out.println(packetId + " and comp id: " + componentId + " and slot id: " + slotId);
			switch (componentId) {
			case 35:
			case 37:
			case 39:
			case 41:
				player.getNotes().colour((componentId - 35) / 2);
				player.getPackets().sendHideIComponent(34, 16, true);
				break;
			case 3:
				player.getPackets().sendInputLongTextScript("Add note:");
				player.getTemporaryAttributtes().put("entering_note", Boolean.TRUE);
				break;
			case 9:
				switch (packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					if (player.getNotes().getCurrentNote() == slotId)
						player.getNotes().removeCurrentNote();
					else
						player.getNotes().setCurrentNote(slotId);
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					player.getPackets().sendInputLongTextScript("Edit note:");
					player.getNotes().setCurrentNote(slotId);
					player.getTemporaryAttributtes().put("editing_note", Boolean.TRUE);
					break;
				case WorldPacketsDecoder.ACTION_BUTTON3_PACKET:
					player.getNotes().setCurrentNote(slotId);
					player.getPackets().sendHideIComponent(34, 16, false);
					break;
				case WorldPacketsDecoder.ACTION_BUTTON4_PACKET:
					player.getNotes().delete(slotId);
					break;
				}
				break;
			case 8:
				switch (packetId) {
				case WorldPacketsDecoder.ACTION_BUTTON1_PACKET:
					player.getNotes().delete();
					break;
				case WorldPacketsDecoder.ACTION_BUTTON2_PACKET:
					player.getNotes().deleteAll();
					break;
				}
				break;
			}
		} else if (interfaceId == 1011) {
			CommendationExchange.handleButtonOptions(player, componentId);
		} else if (interfaceId == 182) {
			if (player.getInterfaceManager().containsInventoryInter())
				return;
			if (componentId == 6 || componentId == 13)
				if (!player.hasFinished())
					player.logout(false);
		} else if (interfaceId == 1165) {
			if (componentId == 22)
				Summoning.closeDreadnipInterface(player);
		} else if (interfaceId == 880) {
			if (componentId >= 7 && componentId <= 19)
				Familiar.setLeftclickOption(player, (componentId - 7) / 2);
			else if (componentId == 21)
				Familiar.confirmLeftOption(player);
			else if (componentId == 25)
				Familiar.setLeftclickOption(player, 7);
		} else if (interfaceId == 662) {
			if (player.getFamiliar() == null) {
				if (player.getPet() == null) {
					return;
				}
				if (componentId == 49)
					player.getPet().call();
				else if (componentId == 51)
					player.getDialogueManager().startDialogue("DismissD");
				return;
			}
			if (componentId == 49)
				player.getFamiliar().call();
			else if (componentId == 51)
				player.getDialogueManager().startDialogue("DismissD");
			else if (componentId == 67)
				player.getFamiliar().takeBob();
			else if (componentId == 69)
				player.getFamiliar().renewFamiliar();
			else if (componentId == 74) {
				if (player.getFamiliar().getSpecialAttack() == SpecialAttack.CLICK)
					player.getFamiliar().setSpecial(true);
				if (player.getFamiliar().hasSpecialOn())
					player.getFamiliar().submitSpecial(player);
			}
		} else if (interfaceId == 747) {
			if (componentId == 8)
				Familiar.selectLeftOption(player);
			else if (player.getPet() != null) {
				if (componentId == 11 || componentId == 20) {
					player.getPet().call();
				} else if (componentId == 12 || componentId == 21) {
					player.getDialogueManager().startDialogue("DismissD");
				} else if (componentId == 10 || componentId == 19 || componentId == 27) {
					player.getPet().sendFollowerDetails();
				}
			} else if (player.getFamiliar() != null) {
				if (componentId == 11 || componentId == 20)
					player.getFamiliar().call();
				else if (componentId == 12 || componentId == 21)
					player.getDialogueManager().startDialogue("DismissD");
				else if (componentId == 13 || componentId == 22)
					player.getFamiliar().takeBob();
				else if (componentId == 14 || componentId == 23)
					player.getFamiliar().renewFamiliar();
				else if (componentId == 19 || componentId == 10 || componentId == 27)
					player.getFamiliar().sendFollowerDetails();
				else if (componentId == 18) {
					if (player.getFamiliar().getSpecialAttack() == SpecialAttack.CLICK)
						player.getFamiliar().setSpecial(true);
					if (player.getFamiliar().hasSpecialOn())
						player.getFamiliar().submitSpecial(player);
				}
			}
		} else if (interfaceId == 309)
			PlayerLook.handleHairdresserSalonButtons(player, componentId, slotId);
		else if (interfaceId == 729)
			PlayerLook.handleThessaliasMakeOverButtons(player, componentId, slotId);
		else if (interfaceId == 187) {
			if (componentId == 1) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getMusicsManager().playAnotherMusic(slotId / 2);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getMusicsManager().sendHint(slotId / 2);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getMusicsManager().addToPlayList(slotId / 2);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getMusicsManager().removeFromPlayList(slotId / 2);
			} else if (componentId == 4)
				player.getMusicsManager().addPlayingMusicToPlayList();
			else if (componentId == 11)
				player.getMusicsManager().switchPlayListOn();
			else if (componentId == 12)
				player.getMusicsManager().clearPlayList();
			else if (componentId == 14)
				player.getMusicsManager().switchShuffleOn();
		} else if (interfaceId == 275) {
			if (componentId == 14) {
				player.getPackets().sendOpenURL(Settings.WEBSITE);
			}
			
		} else if ((interfaceId == 590 && componentId == 8) || interfaceId == 464) {
			player.getEmotesManager()
					.useBookEmote(interfaceId == 464 ? componentId : EmotesManager.getId(slotId, packetId));
		} else if (interfaceId == 506) {
			QuestTab.handleTab(player, componentId);
		} else if (interfaceId == 192) {
			if (componentId == 2)
				player.getCombatDefinitions().switchDefensiveCasting();
			else if (componentId == 7)
				player.getCombatDefinitions().switchShowCombatSpells();
			else if (componentId == 9)
				player.getCombatDefinitions().switchShowTeleportSkillSpells();
			else if (componentId == 11)
				player.getCombatDefinitions().switchShowMiscallaneousSpells();
			else if (componentId == 13)
				player.getCombatDefinitions().switchShowSkillSpells();
			else if (componentId >= 15 & componentId <= 17)
				player.getCombatDefinitions().setSortSpellBook(componentId - 15);
			else
				Magic.processNormalSpell(player, componentId, packetId);
		} else if (interfaceId >= 334 && interfaceId < 337) {
			player.getTrade().handleButtons(player, interfaceId, packetId, componentId, slotId);
		} else if (interfaceId == 300) {
			ForgingInterface.handleIComponents(player, componentId);
		} else if (interfaceId == 206) {
			if (componentId == 15) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getPriceCheckManager().removeItem(slotId, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getPriceCheckManager().removeItem(slotId, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getPriceCheckManager().removeItem(slotId, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getPriceCheckManager().removeItem(slotId, Integer.MAX_VALUE);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("pc_item_X_Slot", slotId);
					player.getTemporaryAttributtes().put("pc_isRemove", Boolean.TRUE);
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				}
			}
		} else if (interfaceId == 672 || interfaceId == 666) {
			boolean pouch = interfaceId == 672;
			if (componentId == 16) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					if (pouch)
						Summoning.createPouch(player, slotId2, 1);
					else
						Summoning.transformScrolls(player, slotId2, 1);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					if (pouch)
						Summoning.createPouch(player, slotId2, 5);
					else
						Summoning.transformScrolls(player, slotId2, 6);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					if (pouch)
						Summoning.createPouch(player, slotId2, 10);
					else
						Summoning.transformScrolls(player, slotId2, 10);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					if (pouch)
						Summoning.createPouch(player, slotId2, 28);
					else
						Summoning.transformScrolls(player, slotId2, 28);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					if (pouch) {
						player.getTemporaryAttributtes().put("creatingPouches", slotId2);
						player.getPackets().sendInputIntegerScript(true,
								"Enter the amount of pouches you'd like to create: ");
					} else {
						player.getTemporaryAttributtes().put("creatingScrolls", slotId2);
						player.getPackets().sendInputIntegerScript(true,
								"Enter the amount of scrolls you'd like to create: ");
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET)
					Summoning.listRequirements(player, slotId2);
			} else if (componentId == 19 && pouch)
				Summoning.sendScrollInterface(player);
			else if (componentId == 18 && !pouch)
				Summoning.sendPouchInterface(player);
		} else if (interfaceId == 207) {
			if (componentId == 0) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getPriceCheckManager().addItem(slotId, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getPriceCheckManager().addItem(slotId, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getPriceCheckManager().addItem(slotId, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getPriceCheckManager().addItem(slotId, Integer.MAX_VALUE);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("pc_item_X_Slot", slotId);
					player.getTemporaryAttributtes().remove("pc_isRemove");
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					player.getInventory().sendExamine(slotId);
			}
		} else if (interfaceId == 665) {
			if (player.getFamiliar() == null || player.getFamiliar().getBob() == null)
				return;
			if (componentId == 0) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getFamiliar().getBob().addItem(slotId, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getFamiliar().getBob().addItem(slotId, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getFamiliar().getBob().addItem(slotId, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getFamiliar().getBob().addItem(slotId, Integer.MAX_VALUE);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("bob_item_X_Slot", slotId);
					player.getTemporaryAttributtes().remove("bob_isRemove");
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					player.getInventory().sendExamine(slotId);
			}
		} else if (interfaceId == 671) {
			if (player.getFamiliar() == null || player.getFamiliar().getBob() == null)
				return;
			if (componentId == 27) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getFamiliar().getBob().removeItem(slotId, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getFamiliar().getBob().removeItem(slotId, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getFamiliar().getBob().removeItem(slotId, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getFamiliar().getBob().removeItem(slotId, Integer.MAX_VALUE);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("bob_item_X_Slot", slotId);
					player.getTemporaryAttributtes().put("bob_isRemove", Boolean.TRUE);
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				}
			} else if (componentId == 29)
				player.getFamiliar().takeBob();
		} else if (interfaceId == 916) {
			SkillsDialogue.handleSetQuantityButtons(player, componentId);
		} else if (interfaceId == 193) {
			if (componentId == 5)
				player.getCombatDefinitions().switchShowCombatSpells();
			else if (componentId == 7)
				player.getCombatDefinitions().switchShowTeleportSkillSpells();
			else if (componentId >= 9 && componentId <= 11)
				player.getCombatDefinitions().setSortSpellBook(componentId - 9);
			else if (componentId == 18)
				player.getCombatDefinitions().switchDefensiveCasting();
			else
				Magic.processAncientSpell(player, componentId, packetId);
		} else if (interfaceId == 430) {
			if (componentId == 5)
				player.getCombatDefinitions().switchShowCombatSpells();
			else if (componentId == 7)
				player.getCombatDefinitions().switchShowTeleportSkillSpells();
			else if (componentId == 9)
				player.getCombatDefinitions().switchShowMiscallaneousSpells();
			else if (componentId >= 11 & componentId <= 13)
				player.getCombatDefinitions().setSortSpellBook(componentId - 11);
			else if (componentId == 20)
				player.getCombatDefinitions().switchDefensiveCasting();
			else
				Magic.processLunarSpell(player, componentId, packetId);
		} else if (interfaceId == 261) {
			if (player.getInterfaceManager().containsInventoryInter())
				return;
			if (componentId == 6) {
				player.switchReportOption();
			} else if (componentId == 11) {
				player.switchProfanityFilter();
			} else if (componentId == 15) {
				player.switchAcceptAid();
			} else if (componentId == 22) {
				if (player.getInterfaceManager().containsScreenInter()) {
					player.getPackets().sendGameMessage(
							"Please close the interface you have open before setting your graphic options.");
					return;
				}
				player.stopAll();
				player.getInterfaceManager().sendInterface(742);
			} else if (componentId == 12)
				player.switchAllowChatEffects();
			else if (componentId == 13) { // chat setup
				player.getInterfaceManager().sendSettings(982);
			} else if (componentId == 14)
				player.switchMouseButtons();
			else if (componentId == 24) // audio options
				player.getInterfaceManager().sendSettings(429);
			else if (componentId == 16) // house options
				player.getInterfaceManager().sendSettings(398);
		} else if (interfaceId == 398) {
			if (componentId == 19)
				player.getInterfaceManager().sendSettings();
			else if (componentId == 15 || componentId == 1)
				player.getHouse().setBuildMode(componentId == 15);
			else if (componentId == 25 || componentId == 26)
				player.getHouse().setArriveInPortal(componentId == 25);
			else if (componentId == 27)
				player.getHouse().expelGuests();
			else if (componentId == 29)
				House.leaveHouse(player);
		} else if (interfaceId == 402) {
			if (componentId >= 93 && componentId <= 115)
				player.getHouse().createRoom(componentId - 93);
		} else if (interfaceId == 394 || interfaceId == 396) {
			if (componentId == 11)
				player.getHouse().build(slotId);
		} else if (interfaceId == 429) {
			if (componentId == 18)
				player.getInterfaceManager().sendSettings();
		} else if (interfaceId == 982) {
			if (componentId == 5)
				player.getInterfaceManager().sendSettings();
			else if (componentId == 41)
				player.setPrivateChatSetup(player.getPrivateChatSetup() == 0 ? 1 : 0);
			else if (componentId >= 49 && componentId <= 66)
				player.setPrivateChatSetup(componentId - 48);
			else if (componentId >= 72 && componentId <= 91)
				player.setFriendChatSetup(componentId - 72);
		} else if (interfaceId == 271) {
			if (player.getControlerManager().getControler() instanceof ImpossibleJad) {
				player.getPackets().sendGameMessage("You're not allowed to use prayer in here.");
				return;
			}
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (componentId == 8 || componentId == 42)
						player.getPrayer().switchPrayer(slotId);

					else if (componentId == 43) {
						if (player.getPrayer().isUsingQuickPrayer()) {
							player.getPrayer().switchSettingQuickPrayer();
							// return;
						}
						// player.sendMessage("You do not have any quick-prayers
						// selected.");
					}
				}
			});
		}
		if (interfaceId == 320) {
			if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
				player.stopAll();
				int lvlupSkill = -1;
				int skillMenu = -1;

				switch (componentId) {
				case 150: // Attack
					skillMenu = 1;
					player.prestige(Skills.ATTACK);
					if (player.getTemporaryAttributtes().remove("leveledUp[0]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 1);
					} else {
						lvlupSkill = 0;
						player.getPackets().sendConfig(1230, 10);
					}
					break;
				case 9: // Strength
					player.prestige(Skills.STRENGTH);
					skillMenu = 2;
					if (player.getTemporaryAttributtes().remove("leveledUp[2]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 2);
					} else {
						lvlupSkill = 2;
						player.getPackets().sendConfig(1230, 20);
					}
					break;
				case 22: // Defence
					player.prestige(Skills.DEFENCE);
					skillMenu = 5;
					if (player.getTemporaryAttributtes().remove("leveledUp[1]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 5);
					} else {
						lvlupSkill = 1;
						player.getPackets().sendConfig(1230, 40);
					}
					break;
				case 40: // Ranged
					skillMenu = 3;
					player.prestige(Skills.RANGE);
					if (player.getTemporaryAttributtes().remove("leveledUp[4]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 3);
					} else {
						lvlupSkill = 4;
						player.getPackets().sendConfig(1230, 30);
					}
					break;
				case 58: // Prayer
					player.prestige(Skills.PRAYER);
					if (player.getTemporaryAttributtes().remove("leveledUp[5]") != Boolean.TRUE) {
						skillMenu = 7;
						player.getPackets().sendConfig(965, 7);
					} else {
						lvlupSkill = 5;
						player.getPackets().sendConfig(1230, 60);
					}
					break;
				case 71: // Magic
					player.prestige(Skills.MAGIC);
					if (player.getTemporaryAttributtes().remove("leveledUp[6]") != Boolean.TRUE) {
						skillMenu = 4;
						player.getPackets().sendConfig(965, 4);
					} else {
						lvlupSkill = 6;
						player.getPackets().sendConfig(1230, 33);
					}
					break;
				case 84: // Runecrafting
					player.prestige(Skills.RUNECRAFTING);
					if (player.getTemporaryAttributtes().remove("leveledUp[20]") != Boolean.TRUE) {
						skillMenu = 12;
						player.getPackets().sendConfig(965, 12);
					} else {
						lvlupSkill = 20;
						player.getPackets().sendConfig(1230, 100);
					}
					break;
				case 102: // Construction
					skillMenu = 22;
					player.prestige(Skills.CONSTRUCTION);
					if (player.getTemporaryAttributtes().remove("leveledUp[21]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 22);
					} else {
						lvlupSkill = 21;
						player.getPackets().sendConfig(1230, 698);
					}
					break;
				case 145: // Hitpoints
					player.prestige(Skills.HITPOINTS);
					skillMenu = 6;
					if (player.getTemporaryAttributtes().remove("leveledUp[3]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 6);
					} else {
						lvlupSkill = 3;
						player.getPackets().sendConfig(1230, 50);
					}
					break;
				case 15: // Agility
					skillMenu = 8;
					player.prestige(Skills.AGILITY);
					if (player.getTemporaryAttributtes().remove("leveledUp[16]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 8);
					} else {
						lvlupSkill = 16;
						player.getPackets().sendConfig(1230, 65);
					}
					break;
				case 28: // Herblore
					player.prestige(Skills.HERBLORE);
					skillMenu = 9;
					if (player.getTemporaryAttributtes().remove("leveledUp[15]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 9);
					} else {
						lvlupSkill = 15;
						player.getPackets().sendConfig(1230, 75);
					}
					break;
				case 46: // Thieving
					player.prestige(Skills.THIEVING);
					skillMenu = 10;
					if (player.getTemporaryAttributtes().remove("leveledUp[17]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 10);
					} else {
						lvlupSkill = 17;
						player.getPackets().sendConfig(1230, 80);
					}
					break;
				case 64: // Crafting
					player.prestige(Skills.CRAFTING);
					skillMenu = 11;
					if (player.getTemporaryAttributtes().remove("leveledUp[12]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 11);
					} else {
						lvlupSkill = 12;
						player.getPackets().sendConfig(1230, 90);
					}
					break;
				case 77: // Fletching
					player.prestige(Skills.FLETCHING);
					skillMenu = 19;
					if (player.getTemporaryAttributtes().remove("leveledUp[9]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 19);
					} else {
						lvlupSkill = 9;
						player.getPackets().sendConfig(1230, 665);
					}
					break;
				case 90: // Slayer
					player.prestige(Skills.SLAYER);
					skillMenu = 20;
					if (player.getTemporaryAttributtes().remove("leveledUp[18]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 20);
					} else {
						lvlupSkill = 18;
						player.getPackets().sendConfig(1230, 673);
					}
					break;
				case 108: // Hunter
					player.prestige(Skills.HUNTER);
					skillMenu = 23;
					if (player.getTemporaryAttributtes().remove("leveledUp[22]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 23);
					} else {
						lvlupSkill = 22;
						player.getPackets().sendConfig(1230, 689);
					}
					break;
				case 140: // Mining
					player.prestige(Skills.MINING);
					skillMenu = 13;
					if (player.getTemporaryAttributtes().remove("leveledUp[14]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 13);
					} else {
						lvlupSkill = 14;
						player.getPackets().sendConfig(1230, 110);
					}
					break;
				case 135: // Smithing
					player.prestige(Skills.SMITHING);
					skillMenu = 14;
					if (player.getTemporaryAttributtes().remove("leveledUp[13]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 14);
					} else {
						lvlupSkill = 13;
						player.getPackets().sendConfig(1230, 115);
					}
					break;
				case 34: // Fishing
					skillMenu = 15;
					player.prestige(Skills.FISHING);
					if (player.getTemporaryAttributtes().remove("leveledUp[10]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 15);
					} else {
						lvlupSkill = 10;
						player.getPackets().sendConfig(1230, 120);
					}
					break;
				case 52: // Cooking
					skillMenu = 16;
					player.prestige(Skills.COOKING);
					if (player.getTemporaryAttributtes().remove("leveledUp[7]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 16);
					} else {
						lvlupSkill = 7;
						player.getPackets().sendConfig(1230, 641);
					}
					break;
				case 130: // Firemaking
					skillMenu = 17;
					player.prestige(Skills.FIREMAKING);
					if (player.getTemporaryAttributtes().remove("leveledUp[11]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 17);
					} else {
						lvlupSkill = 11;
						player.getPackets().sendConfig(1230, 649);
					}
					break;
				case 125: // Woodcutting
					skillMenu = 18;
					player.prestige(Skills.WOODCUTTING);
					if (player.getTemporaryAttributtes().remove("leveledUp[8]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 18);
					} else {
						lvlupSkill = 8;
						player.getPackets().sendConfig(1230, 660);
					}
					break;
				case 96: // Farming
					player.prestige(Skills.FARMING);
					skillMenu = 21;
					if (player.getTemporaryAttributtes().remove("leveledUp[19]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 21);
					} else {
						lvlupSkill = 19;
						player.getPackets().sendConfig(1230, 681);
					}
					break;
				case 114: // Summoning
					skillMenu = 24;
					player.prestige(Skills.SUMMONING);
					if (player.getTemporaryAttributtes().remove("leveledUp[23]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 24);
					} else {
						lvlupSkill = 23;
						player.getPackets().sendConfig(1230, 705);
					}
					break;
				case 120: // Dung
					player.prestige(Skills.DUNGEONEERING);
					skillMenu = 25;
					if (player.getTemporaryAttributtes().remove("leveledUp[24]") != Boolean.TRUE) {
						player.getPackets().sendConfig(965, 25);
					} else {
						lvlupSkill = 24;
						player.getPackets().sendConfig(1230, 705);
					}
					break;
				}
				if (lvlupSkill != -1) {
					LevelUp.switchFlash(player, lvlupSkill, false);
				}
				if (skillMenu != -1) {
					player.getTemporaryAttributtes().put("skillMenu", skillMenu);
				}
			} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET
					|| packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
				int skillId = player.getSkills().getTargetIdByComponentId(componentId);
				boolean usingLevel = packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET;
				player.getTemporaryAttributtes().put(usingLevel ? "levelSkillTarget" : "xpSkillTarget", skillId);
				player.getPackets().sendInputIntegerScript(true,
						"Please enter target " + (usingLevel ? "level" : "xp") + " you want to set: ");

			} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) { // clear
																				// target
				int skillId = player.getSkills().getTargetIdByComponentId(componentId);
				player.getSkills().setSkillTargetEnabled(skillId, false);
				player.getSkills().setSkillTargetValue(skillId, 0);
				player.getSkills().setSkillTargetUsingLevelMode(skillId, false);
			}
		} else if (interfaceId == 1218) { // okay all g
			if ((componentId >= 33 && componentId <= 55) || componentId == 120 || componentId == 151
					|| componentId == 189)
				player.getPackets().sendInterface(false, 1218, 1, 1217);
			if (componentId == 73)
				player.getPackets().sendWindowsPane(player.getInterfaceManager().hasRezizableScreen() ? 746 : 548, 2);
		} else if (interfaceId == 499) {
			int skillMenu = -1;
			if (player.getTemporaryAttributtes().get("skillMenu") != null)
				skillMenu = (Integer) player.getTemporaryAttributtes().get("skillMenu");
			if (componentId >= 10 && componentId <= 25)
				player.getPackets().sendConfig(965, ((componentId - 10) * 1024) + skillMenu);
			else if (componentId == 29)
				player.stopAll();

			/*
			 * case 37: //East RockCrabs Magic.vineTeleport(player, new WorldTile(2710,
			 * 3710, 0)); break; case 38: //Glacor Cave Magic.vineTeleport(player, new
			 * WorldTile(4181, 5726, 0)); break; case 39: //Dwarf BattleField
			 * Magic.vineTeleport(player, new WorldTile(1519, 4704, 0)); break; case 40:
			 * //Frost Draogns if (player.getSkills().getLevel(Skills.DUNGEONEERING) < 85) {
			 * player.
			 * sendMessage("This area requires at least level 85 Dungeoneering to access!");
			 * end(); return; } Magic.vineTeleport(player, new WorldTile(1298, 4510, 0)); //
			 * frosts break; case 41: //Celestial Dragons if
			 * (player.getSkills().getLevel(Skills.DUNGEONEERING) < 95) { player.
			 * sendMessage("This area requires at least level 95 Dungeoneering to access!");
			 * end(); return; } Magic.vineTeleport(player, new WorldTile(2285, 5972, 0)); //
			 * celestial Dragons break; case 42: //Kuradal's Dungeon
			 * Magic.vineTeleport(player, new WorldTile(1690, 5286, 1)); break; case 64:
			 * //Jadinko Lair if (player.getSkills().getLevel(Skills.SLAYER) < 80) {
			 * player.sendMessage("You need a slayer level of 80 to use this teleport.");
			 * end(); return; } Magic.vineTeleport(player, new WorldTile(3012, 9274, 0));
			 * break; case 65: //Polypore Dungeon Magic.vineTeleport(player, new
			 * WorldTile(4625, 5457, 3)); break; case 66: //Slayer Tower
			 * Magic.vineTeleport(player, new WorldTile(3423, 3543, 0)); break; case
			 * 67://Ancient Cavern Magic.vineTeleport(player, new WorldTile(1763, 5365, 1));
			 * break; case 68://Dungeon Area
			 * player.getDialogueManager().startDialogue("TrainingTeleport"); break; case
			 * 69://Strykewyrms Area
			 * player.getDialogueManager().startDialogue("StrykewyrmsArea"); break; case 70:
			 * //West RockCrabs Magic.vineTeleport(player, new WorldTile(2672, 3710, 0));
			 * break; case 73: player.sendMessage("Coming Soon"); break;
			 */

		} else if (interfaceId == 387) {
			if (player.getInterfaceManager().containsInventoryInter())
				return;
			player.closeInterfaces();
			if (componentId == 6) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					if (Settings.DEBUG)
						Logger.log("Action button 2 packet SLOT_HAT");
					int hatId = player.getEquipment().getHatId();
					if (hatId == 24437 || hatId == 24439 || hatId == 24440 || hatId == 24441) {
						player.getDialogueManager().startDialogue("FlamingSkull",
								player.getEquipment().getItem(Equipment.SLOT_HAT), -1);
						return;
					}
					if (hatId == 34030 || hatId == 34031 || hatId == 34032 || hatId == 34033 || hatId == 34034
							|| hatId == 34035 || hatId == 34036) {
						player.getDialogueManager().startDialogue(new Dialogue() {

							String options[] = { "Normal", "Dark blue", "Green", "Light blue", "Red", "Orange",
									"Black" };
							ArrayList<String> options_ = new ArrayList<String>();

							@Override
							public void start() {
								for (int i = 0; i < options.length; i++) {
									if (i + 34030 != hatId)
										options_.add(options[i]);
								}
								sendOptionsDialogue("Choose a color", options_.get(0), options_.get(1), options_.get(2),
										options_.get(3), "More options..");
								stage = 0;
							}

							@Override
							public void run(int interfaceId, int componentId) {
								switch (stage) {
								case 0:
									int glassesGet = componentId == 11 ? 0 : componentId - 12;
									switch (componentId) {
									case OPTION_1:
									case OPTION_2:
									case OPTION_3:
									case OPTION_4:
										for (int i = 0; i <= options.length - 1; i++) {
											if (options[i] == options_.get(glassesGet)) {
												sendItemDialogue(34030 + i, 1,
														"You recolored your sunglasses " + options[i] + "!");
												player.getEquipment().set(0, new Item(34030 + i));
												player.getEquipment().refresh(0);
												player.getGlobalPlayerUpdater().generateAppearenceData();
												stage = 2;
											}
										}
										break;
									case OPTION_5:
										sendOptionsDialogue("Choose a color", options_.get(4), options_.get(5),
												"Previous options..");
										stage = 1;
										break;
									}
									break;
								case 1:
									switch (componentId) {
									case OPTION_1:
									case OPTION_2:
										glassesGet = componentId == 11 ? 4 : 5;
										for (int i = 0; i <= options.length - 1; i++) {
											if (options[i] == options_.get(glassesGet)) {
												sendItemDialogue(34030 + i, 1,
														"You recolored your sunglasses " + options[i] + "!");
												player.getEquipment().set(0, new Item(34030 + i));
												player.getEquipment().refresh(0);
												player.getGlobalPlayerUpdater().generateAppearenceData();
												stage = 2;
											}
										}
										break;
									case OPTION_3:
										sendOptionsDialogue("Choose a color", options_.get(0), options_.get(1),
												options_.get(2), options_.get(3), "More options..");
										stage = 0;
										break;
									}
									break;
								case 2:
									finish();
									break;
								}
							}

							@Override
							public void finish() {
								player.getInterfaceManager().closeChatBoxInterface();
							}

						});
					}
					if (hatId == 35963 || hatId == 35968 || hatId == 35973 || hatId == 35978) {
						Magic.sendNormalTeleportSpell(player, 1, 0, new WorldTile(3134, 3228, 0));
						return;
					}
					if (hatId >= 15493 && hatId <= 15494) {
						player.getDialogueManager().startDialogue("Kuradal");
						return;
					}
					if (hatId == 10507) {
						player.setNextGraphics(new Graphics(263));
						player.setNextAnimation(new Animation(7531));
						player.sendMessage("You act like a reindeer.. I think.");
						player.lock(2);
						return;
					}

					final Item idam = player.getEquipment().getItem(Equipment.SLOT_HAT);
					if (idam != null) {
						if (player.getCharges().checkCharges(idam))
							return;
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_HAT, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					if (Settings.DEBUG)
						Logger.log("Action button 2 packet SLOT_HAT");
					int hatId = player.getEquipment().getHatId();
					if (hatId == 34200 || hatId == 34205 || hatId == 34210 || hatId == 34215) {
						player.getDialogueManager().startDialogue("SharkConsumeOption");
						return;
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_HAT);
			} else if (componentId == 9) {
				int capeId = player.getEquipment().getCapeId();
				if (capeId == 10498 || capeId == 10499 || capeId == 20068) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						player.sendMessage("You can not operate that.");
				}
				if (capeId == 20769 || capeId == 20771 || capeId == 32152 || capeId == 32153) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
						SkillCapeCustomizer.startCustomizing(player, capeId);
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
						Magic.compCapeTeleport(player, 2606, 3222, 0);
						return;
					}
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
						Magic.compCapeTeleport(player, 2664, 3375, 0);
						return;
					}
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
						Magic.compCapeTeleport(player, 2274, 3340, 1);
						return;
					}
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
						Long sumRestore = (Long) player.getTemporaryAttributtes().get("sum_restore");
						if (sumRestore != null && sumRestore + 1500000 > Utils.currentTimeMillis()) {
							player.sendMessage("You can only restore your summoning points once every 15 minutes.");
							return;
						}
						player.getSkills().set(Skills.SUMMONING, 99);
						player.getTemporaryAttributtes().put("sum_restore", Utils.currentTimeMillis());
						player.sendMessage("Summoning points restored, you can do this again in 15 minutes.");
					}
				}
				if (capeId == 20767 || capeId == 32151) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						SkillCapeCustomizer.startCustomizing(player, capeId);
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
						Magic.compCapeTeleport(player, 2274, 3340, 1);
				}
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_CAPE, false);
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_CAPE);
				final Item idam = player.getEquipment().getItem(Equipment.SLOT_CAPE);
				if (idam != null) {
					if (player.getCharges().checkCharges(idam))
						return;
				}
			} else if (componentId == 12) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					int amuletId = player.getEquipment().getAmuletId();
					if (amuletId <= 1712 && amuletId >= 1706) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(3087, 3496, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() - 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					} else if (amuletId >= 11105 && amuletId <= 11111) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(2614, 3382, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					} else if (amuletId == 1704 || amuletId == 10352) {
						player.sendMessage("The amulet has ran out of charges.");
						return;
					} else if (amuletId == 11113) {
						player.sendMessage("The amulet has ran out of charges.");
						return;
					} else if (amuletId >= 3853 && amuletId <= 3867) {
						player.getDialogueManager().startDialogue("Transportation2", "Burthrope Games Room",
								new WorldTile(2880, 3559, 0), "Barbarian Outpost", new WorldTile(2519, 3571, 0),
								"Gamers' Grotto", new WorldTile(2970, 9679, 0), "Corporeal Beast",
								new WorldTile(2886, 4377, 0), amuletId);
						return;
					}
					final Item idam = player.getEquipment().getItem(Equipment.SLOT_AMULET);
					if (idam != null) {
						if (player.getCharges().checkCharges(idam))
							return;
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					int amuletId = player.getEquipment().getAmuletId();
					if (amuletId <= 1712 && amuletId >= 1706) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(2918, 3176, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() - 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					} else if (amuletId >= 11105 && amuletId <= 11111) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(3021, 3339, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					int amuletId = player.getEquipment().getAmuletId();
					if (amuletId <= 1712 && amuletId >= 1706) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(3105, 3251, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() - 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					} else if (amuletId >= 11105 && amuletId <= 11111) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(2933, 3296, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					int amuletId = player.getEquipment().getAmuletId();
					if (amuletId <= 1712 && amuletId >= 1706) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(3293, 3163, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() - 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					} else if (amuletId >= 11105 && amuletId <= 11111) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(3142, 3440, 0))) {
							Item amulet = player.getEquipment().getItem(Equipment.SLOT_AMULET);
							if (amulet != null) {
								amulet.setId(amulet.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_AMULET);
							}
						}
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_AMULET, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_AMULET);
			} else if (componentId == 15) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_WEAPON, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					int wepId = player.getEquipment().getWeaponId();
					if (wepId == 15426)
						player.setNextAnimation(new Animation(12664));
					if (wepId == 20084)
						player.setNextAnimation(new Animation(15150));
					final Item idam = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
					if (idam != null) {
						if (player.getCharges().checkCharges(idam)) {
							return;
						}
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					int wepId = player.getEquipment().getWeaponId();
					if (wepId == 20084) {
						player.setNextGraphics(new Graphics(2953));
						player.setNextAnimation(new Animation(15149));
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_WEAPON);
			} else if (componentId == 18) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_CHEST, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_CHEST);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					final Item idam = player.getEquipment().getItem(Equipment.SLOT_CHEST);
					if (idam != null) {
						if (player.getCharges().checkCharges(idam)) {
							return;
						}
					}
				}
			} else if (componentId == 21) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_SHIELD, false);

				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					final Item idam = player.getEquipment().getItem(Equipment.SLOT_SHIELD);
					if (idam != null) {
						if (PrayerBooks.isGodBook(idam.getId(), true)) {
							PrayerBooks.sermanize(player, idam.getId());
							return;
						}
					}
					int shieldId = player.getEquipment().getShieldId();
					if (shieldId == 11283 || shieldId == 25558 || shieldId == 25561) {
						if (player.getDFSDelay() >= Utils.currentTimeMillis()) {
							player.sendMessage("You must wait two minutes before performing this attack once more.");
							return;
						}
						player.getTemporaryAttributtes().put("dfs_shield_active", true);
					} else if (shieldId == 11284 || shieldId == 25559 || shieldId == 25562)
						player.sendMessage("You don't have any charges left in your shield.");
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					final Item idam = player.getEquipment().getItem(Equipment.SLOT_SHIELD);
					if (idam != null) {
						if (player.getCharges().checkCharges(idam))
							return;
					}
				}

				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_SHIELD);
			} else if (componentId == 24) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_LEGS, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_LEGS);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					final Item idam = player.getEquipment().getItem(Equipment.SLOT_LEGS);
					if (idam != null) {
						if (player.getCharges().checkCharges(idam)) {
							return;
						}
					}
				}
			} else if (componentId == 27) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_HANDS, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					int glovesId = player.getEquipment().getGlovesId();
					if (glovesId >= 11118 && glovesId <= 11124) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(2880, 3542, 0))) {
							Item gloves = player.getEquipment().getItem(Equipment.SLOT_HANDS);
							if (gloves != null) {
								gloves.setId(gloves.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_HANDS);
							}
						}
					} else if (glovesId == 11126) {
						player.sendMessage("The bracelet has ran out of charges.");
						return;
					}
					final Item idam = player.getEquipment().getItem(Equipment.SLOT_HANDS);
					if (idam != null) {
						if (player.getCharges().checkCharges(idam)) {
							return;
						}
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					int glovesId = player.getEquipment().getGlovesId();
					if (glovesId >= 11118 && glovesId <= 11124) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(3191, 3367, 0))) {
							Item gloves = player.getEquipment().getItem(Equipment.SLOT_HANDS);
							if (gloves != null) {
								gloves.setId(gloves.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_HANDS);
							}
						}
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					int glovesId = player.getEquipment().getGlovesId();
					if (glovesId >= 11118 && glovesId <= 11124) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(3051, 3491, 0))) {
							Item gloves = player.getEquipment().getItem(Equipment.SLOT_HANDS);
							if (gloves != null) {
								gloves.setId(gloves.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_HANDS);
							}
						}
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					int glovesId = player.getEquipment().getGlovesId();
					if (glovesId >= 11118 && glovesId <= 11124) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(2655, 3441, 0))) {
							Item gloves = player.getEquipment().getItem(Equipment.SLOT_HANDS);
							if (gloves != null) {
								gloves.setId(gloves.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_HANDS);
							}
						}
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_HANDS);
			} else if (componentId == 30) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_FEET, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_FEET);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					final Item idam = player.getEquipment().getItem(Equipment.SLOT_FEET);
					if (idam != null) {
						if (player.getCharges().checkCharges(idam)) {
							return;
						}
					}
				}
			} else if (componentId == 33) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_RING, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					int ringId = player.getEquipment().getRingId();
					if (ringId == 15707) {
						player.sendMessage("You cannot do this.");
						return;
					}
					if (ringId >= 13281 && ringId <= 13288) {
						player.getDialogueManager().startDialogue("SlayerRing", ringId);
						return;
					}
					if (ringId >= 34987 && ringId <= 34995) {
						player.getDialogueManager().startDialogue("KethsiRing", ringId, false);
						return;
					}
					if (ringId >= 2552 && ringId <= 2566) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(3315, 3234, 0))) {
							Item ring = player.getEquipment().getItem(Equipment.SLOT_RING);
							if (ring != null) {
								if (ring.getId() == 2566)
									ring.setId(1635);
								else
									ring.setId(ring.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_RING);
							}
						}
						return;
					}
					if (ringId == 2568) {
						int left = 140 - player.ironOres;
						player.sendMessage("Your ring of forging has " + Colors.red + left + "</col> charges left.");
						return;
					}
					if (ringId >= 20655 && ringId <= 20659) {
						Magic.sendItemTeleportSpell(player, true, 9603, 1684, 4, new WorldTile(2581, 3845, 0));
						Item ring = player.getEquipment().getItem(Equipment.SLOT_RING);
						ring.setId(ring.getId() - 2);
						player.getEquipment().refresh(Equipment.SLOT_RING);
						return;
					}
					if (ringId == 20653) {
						Magic.sendItemTeleportSpell(player, true, 9603, 1684, 4, new WorldTile(2581, 3845, 0));
						Item ring = player.getEquipment().getItem(Equipment.SLOT_RING);
						player.getEquipment().refresh(Equipment.SLOT_RING);
						ring.setId(2572);
						return;
					}
					final Item idam = player.getEquipment().getItem(Equipment.SLOT_RING);
					if (idam != null) {
						if (player.getCharges().checkCharges(idam)) {
							return;
						}
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					int ringId = player.getEquipment().getRingId();
					if (ringId == 15707) {
						Magic.daemonheimTeleport(player, new WorldTile(3448, 3699, 0));
						return;
					}
					if (ringId >= 13281 && ringId <= 13288) {
						player.getSlayerManager().checkKillsLeft();
						return;
					}
					if (ringId >= 2552 && ringId <= 2566) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(2442, 3088, 0))) {
							Item ring = player.getEquipment().getItem(Equipment.SLOT_RING);
							if (ring != null) {
								if (ring.getId() == 2566)
									ring.setId(1635);
								else
									ring.setId(ring.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_RING);
							}
						}
					}
					if (ringId >= 20655 && ringId <= 20659) {
						Magic.sendItemTeleportSpell(player, true, 9603, 1684, 4, new WorldTile(3164, 3468, 0));
						Item ring = player.getEquipment().getItem(Equipment.SLOT_RING);
						ring.setId(ring.getId() - 2);
						player.getEquipment().refresh(Equipment.SLOT_RING);
						return;
					}
					if (ringId == 20653) {
						Magic.sendItemTeleportSpell(player, true, 9603, 1684, 4, new WorldTile(3164, 3468, 0));
						Item ring = player.getEquipment().getItem(Equipment.SLOT_RING);
						ring.setId(2572);
						player.getEquipment().refresh(Equipment.SLOT_RING);
						return;
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					int ringId = player.getEquipment().getRingId();
					if (ringId == 15707) {
						player.sendMessage("You cannot customise your ring.");
						return;
					}
					if (ringId >= 13281 && ringId <= 13288) {
						player.getInterfaceManager().sendInterface(1309);
						return;
					}
					if (ringId >= 2552 && ringId <= 2566) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(2413, 2848, 0))) {
							Item ring = player.getEquipment().getItem(Equipment.SLOT_RING);
							if (ring != null) {
								if (ring.getId() == 2566)
									ring.setId(1635);
								else
									ring.setId(ring.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_RING);
							}
						}
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					int ringId = player.getEquipment().getRingId();
					if (ringId >= 2552 && ringId <= 2566) {
						if (Magic.sendItemTeleportSpell(player, true, Transportation.EMOTE, Transportation.GFX, 4,
								new WorldTile(1679, 5599, 0))) {
							Item ring = player.getEquipment().getItem(Equipment.SLOT_RING);
							if (ring != null) {
								if (ring.getId() == 2566)
									ring.setId(1635);
								else
									ring.setId(ring.getId() + 2);
								player.getEquipment().refresh(Equipment.SLOT_RING);
							}
						}
					}
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_RING);
			} else if (componentId == 36) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					ButtonHandler.sendRemove(player, Equipment.SLOT_ARROWS, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_ARROWS);
			} else if (componentId == 45) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					ButtonHandler.sendRemove(player, Equipment.SLOT_AURA, false);
					player.getAuraManager().removeAura();
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getEquipment().sendExamine(Equipment.SLOT_AURA);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getAuraManager().activate();
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getAuraManager().sendAuraRemainingTime();
			} else if (componentId == 37) {
				openEquipmentBonuses(player, false);
			} else if (componentId == 40) {
				player.stopAll();
				openItemsKeptOnDeath(player, false);
			} else if (componentId == 41) {
				player.stopAll();
				player.getInterfaceManager().sendInterface(1178);
				player.getToolBelt().refresh();
			}
		} else if (interfaceId == 1265) {
			Shop shop = (Shop) player.getTemporaryAttributtes().get("Shop");
			if (shop == null)
				return;
			Integer slot = (Integer) player.getTemporaryAttributtes().get("ShopSelectedSlot");
			boolean isBuying = player.getTemporaryAttributtes().get("shop_buying") != null;
			if (componentId == 20 || componentId == 201) {
				player.getTemporaryAttributtes().put("ShopSelectedSlot", slotId);
				if (componentId == 20 && !isBuying) {
					shop.sendInfo(player, slotId, isBuying);
					player.getPackets().sendConfig(2561, 93);
					return;
				}
				if (componentId == 201) {
					shop.handleShop(player, slot, 1);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					shop.sendInfo(player, slotId, isBuying);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					shop.handleShop(player, slotId, 1);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					shop.handleShop(player, slotId, 5);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET) {
					shop.handleShop(player, slotId, 10);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					shop.handleShop(player, slotId, 50);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) {
					shop.handleShop(player, slotId, 500);
				}
			} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET) {
				shop.sendExamine(player, slotId);
			} else if (componentId == 29) {
				player.getTemporaryAttributtes().put("shop_buying", true);// player.getTemporaryAttributtes().remove("shop_buying");
				player.getPackets().sendConfig(2561, -1);
				player.getPackets().sendConfig(2562, -1);
				player.getPackets().sendConfig(2563, -1);
				player.getPackets().sendConfig(2565, 0);
				shop.setAmount(player, 1);
				player.sendMessage("Right-click items to sell them.");
			} else if (componentId == 28) {
				player.getPackets().sendConfig(2561, -1);
				player.getPackets().sendConfig(2562, -1);
				player.getPackets().sendConfig(2563, -1);
				player.getPackets().sendConfig(2565, 0);
				player.getTemporaryAttributtes().put("shop_buying", true);
				shop.setAmount(player, 1);
			}
		} else if (interfaceId == 1266) {
			if (componentId == 0) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET) {
					if (slotId == -1)
						return;
					player.getInventory().sendExamine(slotId);
				} else {
					Shop shop = (Shop) player.getTemporaryAttributtes().get("Shop");
					if (shop == null)
						return;
					player.getPackets().sendConfig(2563, slotId);
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
						shop.sendValue(player, slotId);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						shop.sell(player, slotId, 1);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
						shop.sell(player, slotId, 5);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
						shop.sell(player, slotId, 10);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
						shop.sell(player, slotId, 500);
				}
			}
		} else if (interfaceId == 634) {
			if (componentId == 28) {
				Item item = new Item(slotId2);
				player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
			}
		} else if (interfaceId == 650) {
			if (componentId == 15) {
				player.stopAll();
				player.closeInterfaces();
				player.getDialogueManager().startDialogue("CorporealBeastD");
				// player.setNextWorldTile(new WorldTile(2974, 4384,
				// player.getPlane()));
				// player.getControlerManager().startControler("CorpBeastControler");
			} else if (componentId == 16)
				player.closeInterfaces();
		} else if (interfaceId == 667) {
			if (player.getTemporaryAttributtes().get("Cosmetics") != null)
				return;
			if (componentId == 9 && slotId == 0) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					ButtonHandler.sendRemove(player, Equipment.SLOT_HAT, false);
					ButtonHandler.refreshEquipBonuses(player);
				}
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					player.getPackets().sendGameMessage("Button 2");
				}
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET) {
					player.getPackets().sendGameMessage("Button 3");
				}
			} else if (componentId == 9 && slotId == 1) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_CAPE, false);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 2) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_AMULET, false);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 3) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_WEAPON, false);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 4) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_CHEST, false);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 5) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_SHIELD, false);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 7) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_LEGS, false);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 9) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_HANDS, false);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 10) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_FEET, false);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 12) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_RING, false);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 13) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_ARROWS, false);
				ButtonHandler.refreshEquipBonuses(player);
			} else if (componentId == 9 && slotId == 14) {
				ButtonHandler.sendRemove(player, Equipment.SLOT_AURA, false);
				ButtonHandler.refreshEquipBonuses(player);
			}
			if (componentId == 14) {
				if (slotId >= 14)
					return;
				Item item = player.getEquipment().getItem(slotId);
				if (item == null)
					return;
				if (packetId == 3)
					player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
				else if (packetId == 216) {

					sendRemove(player, slotId, false);
					ButtonHandler.refreshEquipBonuses(player);
				}
			} else if (componentId == 46 && player.getTemporaryAttributtes().remove("Banking") != null) {
				player.getBank().openPlayerBank(player);
			}
		} else if (interfaceId == 670) {
			if (componentId == 0) {
				if (slotId >= player.getInventory().getItemsContainerSize())
					return;

				Item item = player.getInventory().getItem(slotId);
				if (item == null)
					return;
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					if (

					sendWear(player, slotId, item.getId()))
						ButtonHandler.refreshEquipBonuses(player);
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getInventory().sendExamine(slotId);
			}
		} else if (interfaceId == Inventory.INVENTORY_INTERFACE) { // inventory
			if (componentId == 0) {
				if (slotId > 27 || player.getInterfaceManager().containsInventoryInter())
					return;
				Item item = player.getInventory().getItem(slotId);
				if (item == null || item.getId() != slotId2)
					return;
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					InventoryOptionsHandler.handleItemOption1(player, slotId, slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					InventoryOptionsHandler.handleItemOption2(player, slotId, slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					InventoryOptionsHandler.handleItemOption3(player, slotId, slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					InventoryOptionsHandler.handleItemOption4(player, slotId, slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
					InventoryOptionsHandler.handleItemOption5(player, slotId, slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET)
					InventoryOptionsHandler.handleItemOption6(player, slotId, slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON7_PACKET)
					InventoryOptionsHandler.handleItemOption7(player, slotId, slotId2, item);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					InventoryOptionsHandler.handleItemOption8(player, slotId, slotId2, item);
			}
		} else if (interfaceId == 742) {
			if (componentId == 46) // close
				player.stopAll();
		} else if (interfaceId == 743) {
			if (componentId == 20) // close
				player.stopAll();
		} else if (interfaceId == 741) {
			if (componentId == 9) // close
				player.stopAll();
		} else if (interfaceId == 749) {
			if (componentId == 4) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) // activate
					player.getPrayer().switchQuickPrayers();
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) // switch
					player.getPrayer().switchSettingQuickPrayer();
			}
		} else if (interfaceId == 750) {
			if (componentId == 4) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
					player.toogleRun(player.isResting() ? false : true);
					if (player.isResting())
						player.stopAll();
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET) {
					if (player.isResting()) {
						player.stopAll();
						return;
					}
					long currentTime = Utils.currentTimeMillis();
					if (player.getEmotesManager().getNextEmoteEnd() >= currentTime) {
						player.getPackets().sendGameMessage("You can't rest while perfoming an emote.");
						return;
					}
					if (player.getLockDelay() >= currentTime) {
						player.getPackets().sendGameMessage("You can't rest while perfoming an action.");
						return;
					}
					player.stopAll();
					player.getActionManager().setAction(new Rest());
				}
			}
		} else if (interfaceId == 11) {
			if (componentId == 17) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getBank().depositItem(slotId, 1, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getBank().depositItem(slotId, 5, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getBank().depositItem(slotId, 10, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getBank().depositItem(slotId, Integer.MAX_VALUE, false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("bank_item_X_Slot", slotId);
					player.getTemporaryAttributtes().remove("bank_isWithdraw");
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					player.getInventory().sendExamine(slotId);
			} else if (componentId == 18)
				player.getBank().depositAllInventory(false);
			else if (componentId == 20)
				player.getBank().depositMoneyPouch(false);
			else if (componentId == 22)
				player.getBank().depositAllEquipment(false);
			else if (componentId == 24)
				player.getBank().depositAllBob(false);
		} else if (interfaceId == 762) {
			if (componentId == 15)
				player.getBank().switchInsertItems();
			else if (componentId == 19)
				player.getBank().switchWithdrawNotes();
			else if (componentId == 33)
				player.getBank().depositAllInventory(true);
			else if (componentId == 35)
				player.getBank().depositMoneyPouch(true);
			else if (componentId == 37)
				player.getBank().depositAllEquipment(true);
			else if (componentId == 39)
				player.getBank().depositAllBob(true);
			else if (componentId == 46) {
				player.closeInterfaces();
				player.getInterfaceManager().sendInterface(767);

			} else if (componentId >= 46 && componentId <= 64) {
				int tabId = 9 - ((componentId - 46) / 2);
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getBank().setCurrentTab(tabId);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getBank().collapse(tabId);
			} else if (componentId == 95) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
					player.getBank().withdrawItem(slotId, 1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getBank().withdrawItem(slotId, 5);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getBank().withdrawItem(slotId, 10);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getBank().withdrawLastAmount(slotId);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
					player.getTemporaryAttributtes().put("bank_item_X_Slot", slotId);
					player.getTemporaryAttributtes().put("bank_isWithdraw", Boolean.TRUE);
					player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
				} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
					player.getBank().withdrawItem(slotId, Integer.MAX_VALUE);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON6_PACKET)
					player.getBank().withdrawItemButOne(slotId);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
					player.getBank().sendExamine(slotId);
			} else if (componentId == 119) {
				if ((Boolean) player.getTemporaryAttributtes().get("viewingOtherBank") != null
						&& (Boolean) player.getTemporaryAttributtes().get("viewingOtherBank") == true)
					return;
				openEquipmentBonuses(player, true);

			}

		} else if (interfaceId == 763) {
			if (player.getTemporaryAttributtes().get("clanBank") != null
					&& player.getTemporaryAttributtes().get("clanBank") == Boolean.TRUE) {
				if (componentId == 0) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
						player.getClanManager().getClan().getClanBank().depositItem(player, slotId, 1, true);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						player.getClanManager().getClan().getClanBank().depositItem(player, slotId, 5, true);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
						player.getClanManager().getClan().getClanBank().depositItem(player, slotId, 10, true);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
						player.getClanManager().getClan().getClanBank().depositLastAmount(player, slotId);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
						player.getTemporaryAttributtes().put("clanbank_item_X_Slot", slotId);
						player.getTemporaryAttributtes().remove("clanbank_isWithdraw");
						player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
					} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
						player.getClanManager().getClan().getClanBank().depositItem(player, slotId, Integer.MAX_VALUE,
								true);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
						player.getInventory().sendExamine(slotId);
				}
			} else {
				if (componentId == 0) {
					if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET)
						player.getBank().depositItem(slotId, 1, true);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
						player.getBank().depositItem(slotId, 5, true);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
						player.getBank().depositItem(slotId, 10, true);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
						player.getBank().depositLastAmount(slotId);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET) {
						player.getTemporaryAttributtes().put("bank_item_X_Slot", slotId);
						player.getTemporaryAttributtes().remove("bank_isWithdraw");
						player.getPackets().sendRunScript(108, new Object[] { "Enter Amount:" });
					} else if (packetId == WorldPacketsDecoder.ACTION_BUTTON9_PACKET)
						player.getBank().depositItem(slotId, Integer.MAX_VALUE, true);
					else if (packetId == WorldPacketsDecoder.ACTION_BUTTON8_PACKET)
						player.getInventory().sendExamine(slotId);
				}
			}
		} else if (interfaceId == 767) {
			if (componentId == 10)
				player.getBank().openPlayerBank();
		} else if (interfaceId == 884) {
			if (componentId == 4) {
				// int weaponId = player.getEquipment().getWeaponId();
				if (player.hasInstantSpecial(weaponId)) {
					player.performInstantSpecial(weaponId);
					return;
				}
				submitSpecialRequest(player);
			} else if (componentId >= 7 && componentId <= 10)
				player.getCombatDefinitions().setAttackStyle(componentId - 7);
			else if (componentId == 11)
				player.getCombatDefinitions().switchAutoRelatie();
		} else if (interfaceId == 755) {
			if (componentId == 44)
				player.getPackets().sendWindowsPane(player.getInterfaceManager().hasRezizableScreen() ? 746 : 548, 2);
			else if (componentId == 42) {
				player.getHintIconsManager().removeAll();
				player.getPackets().sendConfig(1159, 1);
			}
		} else if (interfaceId == 20)
			SkillCapeCustomizer.handleSkillCapeCustomizer(player, componentId);
		else if (interfaceId == 1056) {
			if (componentId == 173)
				player.getInterfaceManager().sendInterface(917);
		} else if (interfaceId == 751) {
			if (componentId == 26) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getFriendsIgnores().setPrivateStatus(0);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getFriendsIgnores().setPrivateStatus(1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getFriendsIgnores().setPrivateStatus(2);
			} else if (componentId == 32) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.setFilterGame(false);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.setFilterGame(true);
			} else if (componentId == 29) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.setPublicStatus(0);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.setPublicStatus(1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.setPublicStatus(2);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON5_PACKET)
					player.setPublicStatus(3);
			} else if (componentId == 0) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.getFriendsIgnores().setFriendsChatStatus(0);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.getFriendsIgnores().setFriendsChatStatus(1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.getFriendsIgnores().setFriendsChatStatus(2);
			} else if (componentId == 23) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.setClanStatus(0);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.setClanStatus(1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.setClanStatus(2);
			} else if (componentId == 20) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.setTradeStatus(0);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.setTradeStatus(1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.setTradeStatus(2);
			} else if (componentId == 23) {
				if (packetId == WorldPacketsDecoder.ACTION_BUTTON2_PACKET)
					player.setClanStatus(0);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON3_PACKET)
					player.setClanStatus(1);
				else if (packetId == WorldPacketsDecoder.ACTION_BUTTON4_PACKET)
					player.setClanStatus(2);

			}
			if (componentId == 14) {
				AreaLoot.toggleInterface(player);
			}
		} else if (interfaceId == 1163 || interfaceId == 1164 || interfaceId == 1168 || interfaceId == 1170
				|| interfaceId == 1173)
			InterfaceManager.setPlayerInterfaceSelected(6);
		player.getDominionTower().handleButtons(interfaceId, componentId);
		if (interfaceId == 900)
			PlayerLook.handleMageMakeOverButtons(player, componentId);
		else if (interfaceId == 1028)
			PlayerDesign.handle(player, componentId, slotId);
		else if (interfaceId == 1028)
			PlayerLook.handleCharacterCustomizingButtons(player, componentId);
		else if (interfaceId == 1108 || interfaceId == 1109)
			player.getFriendsIgnores().handleFriendChatButtons(interfaceId, componentId, packetId);
		else if (interfaceId == 1079)
			player.closeInterfaces();
		else if (interfaceId == 374) {
			if (componentId >= 5 && componentId <= 9)
				player.setNextWorldTile(new WorldTile(FightPitsViewingOrb.ORB_TELEPORTS[componentId - 5]));
			else if (componentId == 15)
				player.stopAll();
		} else if (interfaceId == 1092) {
			player.stopAll();
			WorldTile destTile = null;
			switch (componentId) {
			case 47:
				destTile = HomeTeleport.LUMBRIDGE_LODE_STONE;
				break;
			case 42:
				destTile = HomeTeleport.BURTHORPE_LODE_STONE;
				break;
			case 39:
				destTile = HomeTeleport.LUNAR_ISLE_LODE_STONE;
				break;
			case 7:
				destTile = HomeTeleport.BANDIT_CAMP_LODE_STONE;
				break;
			case 50:
				destTile = HomeTeleport.TAVERLY_LODE_STONE;
				break;
			case 40:
				destTile = HomeTeleport.ALKARID_LODE_STONE;
				break;
			case 51:
				destTile = HomeTeleport.VARROCK_LODE_STONE;
				break;
			case 45:
				destTile = HomeTeleport.EDGEVILLE_LODE_STONE;
				break;
			case 46:
				destTile = HomeTeleport.FALADOR_LODE_STONE;
				break;
			case 48:
				destTile = HomeTeleport.PORT_SARIM_LODE_STONE;
				break;
			case 44:
				destTile = HomeTeleport.DRAYNOR_VILLAGE_LODE_STONE;
				break;
			case 41:
				destTile = HomeTeleport.ARDOUGNE_LODE_STONE;
				break;
			case 43:
				destTile = HomeTeleport.CATHERBAY_LODE_STONE;
				break;
			case 52:
				destTile = HomeTeleport.YANILLE_LODE_STONE;
				break;
			case 49:
				destTile = HomeTeleport.SEERS_VILLAGE_LODE_STONE;
				break;
			}
			if (destTile != null)
				player.getActionManager().setAction(new HomeTeleport(destTile));
		} else if (interfaceId == 1089) {
			if (componentId == 30)
				player.getTemporaryAttributtes().put("clanflagselection", slotId);
			else if (componentId == 26) {
				Integer flag = (Integer) player.getTemporaryAttributtes().remove("clanflagselection");
				player.stopAll();
				if (flag != null)
					ClansManager.setClanFlagInterface(player, flag);
			}
		} else if (interfaceId == 1096) {
			if (componentId == 41)
				ClansManager.viewClammateDetails(player, slotId);
			else if (componentId == 62)
				ClansManager.saveClanmateDetails(player);
			else if (componentId == 94)
				ClansManager.switchGuestsInChatCanEnterInterface(player);
			else if (componentId == 95)
				ClansManager.switchGuestsInChatCanTalkInterface(player);
			else if (componentId == 96)
				ClansManager.switchRecruitingInterface(player);
			else if (componentId == 97)
				ClansManager.switchClanTimeInterface(player);
			else if (componentId == 124)
				ClansManager.openClanMottifInterface(player);
			else if (componentId == 131)
				ClansManager.openClanMottoInterface(player);
			else if (componentId == 240)
				ClansManager.setTimeZoneInterface(player, -720 + slotId * 10);
			else if (componentId == 6)
				player.getTemporaryAttributtes().put("editclanmatejob", slotId);
			else if (componentId == 20)
				player.getTemporaryAttributtes().put("editclanmaterank", slotId);
			else if (componentId == 53)
				ClansManager.kickClanmate(player);
			else if (componentId == 318)
				ClansManager.saveClanmateDetails(player);
			else if (componentId == 290)
				ClansManager.setWorldIdInterface(player, slotId);
			else if (componentId == 41)
				ClansManager.openForumThreadInterface(player);
			else if (componentId == 90)
				ClansManager.openNationalFlagInterface(player);
			else if (componentId == 113)
				ClansManager.showClanSettingsClanMates(player);
			else if (componentId == 120)
				ClansManager.showClanSettingsSettings(player);
			else if (componentId == 130)
				ClansManager.showClanSettingsPermissions(player);
			else if (componentId >= 139 && componentId <= 219) {
				int selectedRank = (componentId - 139) / 8;
				if (selectedRank == 10)
					selectedRank = 125;
				else if (selectedRank > 5)
					selectedRank = 100 + selectedRank - 6;
				ClansManager.selectPermissionRank(player, selectedRank);
			} else if (componentId == 233)
				ClansManager.selectPermissionTab(player, 1);
			else if (componentId == 242)
				ClansManager.selectPermissionTab(player, 2);
			else if (componentId == 250)
				ClansManager.selectPermissionTab(player, 3);
			else if (componentId == 514)
				ClansManager.selectPermissionTab(player, 4);
			else if (componentId == 522)
				ClansManager.selectPermissionTab(player, 5);
		} else if (interfaceId == 1105) {
			// if (componentId == 63 || componentId == 66)
			// ClansManager.setClanMottifTextureInterface(player, componentId ==
			// 66, slotId);
			if (componentId == 35)
				ClansManager.openSetMottifColor(player, 0);
			else if (componentId == 80)
				ClansManager.openSetMottifColor(player, 1);
			else if (componentId == 92)
				ClansManager.openSetMottifColor(player, 2);
			else if (componentId == 104)
				ClansManager.openSetMottifColor(player, 3);
			else if (componentId == 120)
				player.stopAll();
		} else if (interfaceId == 1110) {
			if (componentId == 82)
				ClansManager.joinClanChatChannel(player);
			else if (componentId == 75)
				ClansManager.openClanInformation(player);
			else if (componentId == 78)
				ClansManager.openClanSettings(player);
			else if (componentId == 91)
				ClansManager.joinGuestClanChat(player);
			else if (componentId == 95)
				ClansManager.banPlayer(player);
			else if (componentId == 99)
				ClansManager.unbanPlayer(player);
			else if (componentId == 11)
				ClansManager.unbanPlayer(player, slotId);
			else if (componentId == 109)
				ClansManager.leaveClan(player);
		} else if (interfaceId == 1079) {
			player.closeInterfaces();

		} else if (interfaceId == 1214)
			player.getSkills().handleSetupXPCounter(componentId);
		else if (interfaceId == 1292) {
			if (componentId == 12)
				Crucible.enterArena(player);
			else if (componentId == 13)
				player.closeInterfaces();
		}
	}

	private static void end() {
		// TODO Auto-generated method stub

	}

	public static void openItemsKeptOnDeath(Player player, boolean wildInter) {
		player.getInterfaceManager().sendInterface(17);
		if (Wilderness.isAtWild(player) || wildInter) {
			sendItemsKeptOnDeath(player, Wilderness.isAtWild(player));
			return;
		} else {
			sendItemsKeptOnDeath(player, false);
			return;
		}
	}

	public static void openEquipmentBonuses(final Player player, boolean banking) {
		player.stopAll();
		player.getInterfaceManager().sendInventoryInterface(670);
		player.getInterfaceManager().sendInterface(667);
		player.getVarsManager().sendVarBit(8348, 1);
		player.getPackets().sendConfigByFile(4894, banking ? 1 : 0);
		player.getPackets().sendRunScript(787, 1);
		player.getPackets().sendItems(93, player.getInventory().getItems());
		player.getPackets().sendInterSetItemsOptionsScript(670, 0, 93, 4, 7, "Equip", "Examine");
		player.getPackets().sendUnlockIComponentOptionSlots(670, 0, 0, 27, 0, 1, 2, 3);
		player.getPackets().sendUnlockIComponentOptionSlots(667, 9, 0, 14, 0);
		player.getPackets().sendIComponentSettings(667, 14, 0, 13, 1030);
		refreshEquipBonuses(player);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getVarsManager().sendVarBit(8348, 1);
				player.getPackets().sendRunScriptBlank(2319);
			}
		});
		if (banking) {
			player.getTemporaryAttributtes().put("Banking", Boolean.TRUE);
			player.setCloseInterfacesEvent(new Runnable() {
				@Override
				public void run() {
					player.getTemporaryAttributtes().remove("Banking");
					player.getPackets().sendConfigByFile(4894, 0);
				}
			});
		}
	}

	public static Item[][] getItemsKeptOnDeath(Player player, Integer[][] slots) {
		ArrayList<Item> droppedItems = new ArrayList<Item>();
		ArrayList<Item> keptItems = new ArrayList<Item>();
		for (int i : slots[0]) { // items kept on death
			Item item = i >= 16 ? player.getInventory().getItem(i - 16) : player.getEquipment().getItem(i - 1);
			if (item == null) // shouldnt
				continue;
			if (item.getAmount() > 1) {
				droppedItems.add(new Item(item.getId(), item.getAmount() - 1));
				item.setAmount(1);
			}
			keptItems.add(item);
		}
		for (int i : slots[1]) { // items droped on death
			Item item = i >= 16 ? player.getInventory().getItem(i - 16) : player.getEquipment().getItem(i - 1);
			if (item == null) // shouldnt
				continue;
			droppedItems.add(item);
		}
		for (int i : slots[2]) { // items protected by default
			Item item = i >= 16 ? player.getInventory().getItem(i - 16) : player.getEquipment().getItem(i - 1);
			if (item == null) // shouldnt
				continue;
			keptItems.add(item);
		}
		return new Item[][] { keptItems.toArray(new Item[keptItems.size()]),
				droppedItems.toArray(new Item[droppedItems.size()]) };
	}

	public static Integer[][] getItemSlotsKeptOnDeath(final Player player, boolean atWilderness, boolean skulled,
			boolean protectPrayer) {
		ArrayList<Integer> droppedItems = new ArrayList<Integer>();
		ArrayList<Integer> protectedItems = atWilderness ? null : new ArrayList<Integer>();
		ArrayList<Integer> lostItems = new ArrayList<Integer>();
		for (int i = 1; i < 44; i++) {
			Item item = i >= 16 ? player.getInventory().getItem(i - 16) : player.getEquipment().getItem(i - 1);
			if (item == null)
				continue;
			int stageOnDeath = item.getDefinitions().getStageOnDeath();
			if (!atWilderness && stageOnDeath == 1)
				protectedItems.add(i);
			else if (stageOnDeath == -1)
				lostItems.add(i);
			else
				droppedItems.add(i);
		}
		int keptAmount = skulled ? 0 : 3;
		if (protectPrayer)
			keptAmount++;
		if (droppedItems.size() < keptAmount)
			keptAmount = droppedItems.size();
		Collections.sort(droppedItems, new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				Item i1 = o1 >= 16 ? player.getInventory().getItem(o1 - 16) : player.getEquipment().getItem(o1 - 1);
				Item i2 = o2 >= 16 ? player.getInventory().getItem(o2 - 16) : player.getEquipment().getItem(o2 - 1);
				int price1 = i1 == null ? 0 : i1.getDefinitions().value;
				int price2 = i2 == null ? 0 : i2.getDefinitions().value;
				if (price1 > price2)
					return -1;
				if (price1 < price2)
					return 1;
				return 0;
			}
		});
		Integer[] keptItems = new Integer[keptAmount];
		for (int i = 0; i < keptAmount; i++)
			keptItems[i] = droppedItems.remove(0);
		return new Integer[][] { keptItems, droppedItems.toArray(new Integer[droppedItems.size()]),
				atWilderness ? new Integer[0] : protectedItems.toArray(new Integer[protectedItems.size()]),
				atWilderness ? new Integer[0] : lostItems.toArray(new Integer[lostItems.size()]) };
	}

	public static void sendItemsKeptOnDeath(Player player, boolean wilderness) {
		boolean skulled = player.hasSkull();
		Integer[][] slots = getItemSlotsKeptOnDeath(player, wilderness, skulled, player.getPrayer().isProtectingItem());
		Item[][] items = getItemsKeptOnDeath(player, slots);
		long riskedWealth = 0;
		long carriedWealth = 0;
		for (Item item : items[1]) {
			if (carriedWealth + riskedWealth + GrandExchange.getPrice(item.getId()) > Integer.MAX_VALUE) {
				carriedWealth = Integer.MAX_VALUE;
				break;
			}
			carriedWealth = riskedWealth += GrandExchange.getPrice(item.getId());
		}
		for (Item item : items[0]) {
			if (carriedWealth + GrandExchange.getPrice(item.getId()) > Integer.MAX_VALUE) {
				carriedWealth = Integer.MAX_VALUE;
				break;
			}
			carriedWealth += GrandExchange.getPrice(item.getId());
		}
		if (slots[0].length > 0) {
			for (int i = 0; i < slots[0].length; i++)
				player.getPackets().sendConfigByFile(9222 + i, slots[0][i]);
			player.getPackets().sendConfigByFile(9227, slots[0].length);
		} else {
			player.getPackets().sendConfigByFile(9222, -1);
			player.getPackets().sendConfigByFile(9227, 1);
		}
		player.getPackets().sendConfigByFile(9226, wilderness ? 1 : 0);
		player.getPackets().sendConfigByFile(9229, skulled ? 1 : 0);
		StringBuffer text = new StringBuffer();
		text.append("The number of items kept on").append("<br>").append("death is normally 3.").append("<br>")
				.append("<br>").append("<br>");
		if (wilderness) {
			text.append("Your gravestone will not").append("<br>").append("appear.");
		}
		text.append("<br>").append("<br>").append("Carried wealth:").append("<br>")
				.append(carriedWealth > Integer.MAX_VALUE ? "Too high!" : Utils.getFormattedNumber((int) carriedWealth))
				.append("<br>").append("<br>").append("Risked wealth:").append("<br>")
				.append(riskedWealth > Integer.MAX_VALUE ? "Too high!" : Utils.getFormattedNumber((int) riskedWealth))
				.append("<br>").append("<br>");
		if (wilderness) {
			text.append("Your hub will be set to:").append("<br>").append("Edgeville.");
		} else {
			text.append("Your hub will be set to:").append("<br>").append("Sarah's Kitchen.");
		}
		player.getPackets().sendGlobalString(352, text.toString());
	}

	public static String getPrefix(Player player, int slotId) {
		int i = player.getCombatDefinitions().getBonuses()[slotId];
		String n = String.valueOf(i);
		return n.startsWith("-") || n.contentEquals("0") ? "" : "+";
	}

	public static void refreshEquipBonuses(Player player) {
		player.getPackets().sendGlobalConfig(779, player.getEquipment().getWeaponStance());
		player.getPackets().refreshWeight();
		player.getPackets().sendIComponentText(667, 28,
				"Stab: " + getPrefix(player, 0) + player.getCombatDefinitions().getBonuses()[0]);
		player.getPackets().sendIComponentText(667, 29,
				"Slash: " + getPrefix(player, 1) + player.getCombatDefinitions().getBonuses()[1]);
		player.getPackets().sendIComponentText(667, 30,
				"Crush: " + getPrefix(player, 2) + player.getCombatDefinitions().getBonuses()[2]);
		player.getPackets().sendIComponentText(667, 31,
				"Magic: " + getPrefix(player, 3) + player.getCombatDefinitions().getBonuses()[3]);
		player.getPackets().sendIComponentText(667, 32,
				"Range: " + getPrefix(player, 4) + player.getCombatDefinitions().getBonuses()[4]);
		player.getPackets().sendIComponentText(667, 33,
				"Stab: " + getPrefix(player, 5) + player.getCombatDefinitions().getBonuses()[5]);
		player.getPackets().sendIComponentText(667, 34,
				"Slash: " + getPrefix(player, 6) + player.getCombatDefinitions().getBonuses()[6]);
		player.getPackets().sendIComponentText(667, 35,
				"Crush: " + getPrefix(player, 7) + player.getCombatDefinitions().getBonuses()[7]);
		player.getPackets().sendIComponentText(667, 36,
				"Magic: " + getPrefix(player, 8) + player.getCombatDefinitions().getBonuses()[8]);
		player.getPackets().sendIComponentText(667, 37,
				"Range: " + getPrefix(player, 9) + player.getCombatDefinitions().getBonuses()[9]);
		player.getPackets().sendIComponentText(667, 38,
				"Summoning: " + getPrefix(player, 10) + player.getCombatDefinitions().getBonuses()[10]);
		player.getPackets().sendIComponentText(667, 39,
				"Absorb Melee: " + getPrefix(player, 11) + player.getCombatDefinitions().getBonuses()[11] + "%");
		player.getPackets().sendIComponentText(667, 40,
				"Absorb Magic: " + getPrefix(player, 12) + player.getCombatDefinitions().getBonuses()[12] + "%");
		player.getPackets().sendIComponentText(667, 41,
				"Absorb Ranged: " + getPrefix(player, 13) + player.getCombatDefinitions().getBonuses()[13] + "%");
		player.getPackets().sendIComponentText(667, 42,
				"Strength: " + getPrefix(player, 14) + player.getCombatDefinitions().getBonuses()[14]);
		player.getPackets().sendIComponentText(667, 43,
				"Ranged Str: " + getPrefix(player, 15) + player.getCombatDefinitions().getBonuses()[15]);
		player.getPackets().sendIComponentText(667, 44,
				"Prayer: " + getPrefix(player, 16) + player.getCombatDefinitions().getBonuses()[16]);
		player.getPackets().sendIComponentText(667, 45,
				"Magic Damage: " + getPrefix(player, 17) + player.getCombatDefinitions().getBonuses()[17] + "%");
		player.getPackets().sendIComponentText(667, 22, "0 kg");
	}

	public static boolean sendRemove(Player player, int slotId, boolean toBank) {
		if (slotId >= 15)
			return false;
		player.stopAll(false, false);
		Item item = player.getEquipment().getItem(slotId);
		if (item == null)
			return true;
		else if (!player.getControlerManager().canRemoveEquip(slotId, item.getId()))
			return false;
		if (toBank) {
			int[] slots = player.getBank().getItemSlot(item.getId());
			if (slots == null && !player.getBank().hasBankSpace())
				return false;
			player.getBank().addItem(item.getId(), slots == null ? player.getBank().getCurrentTab() : slots[0],
					item.getAmount(), true);
		} else if (!player.getInventory().addItem(item.getId(), item.getAmount()))
			return false;
		player.getEquipment().getItems().set(slotId, null);
		player.getEquipment().refresh(slotId);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		if (slotId == Equipment.SLOT_WEAPON)
			player.getCombatDefinitions().decreaseSpecialAttack(0);
		else if (slotId == Equipment.SLOT_AURA)
			player.getAuraManager().removeAura();
		// if (player.getInterfaceManager().containsInterface(1463))
		// player.getEquipment().refreshEquipmentInterfaceBonuses();
		player.getPackets().sendSound(item.getDefinitions().getCSOpcode(118), 0, 1);
		return true;
	}

	public static void sendRemove2(Player player, int slotId) {
		player.stopAll(false, false);
		Item item = player.getEquipment().getItem(slotId);
		player.getEquipment().getItems().set(slotId, null);
		player.getEquipment().refresh(slotId);
		if (item.getId() == 4024)
			player.getGlobalPlayerUpdater().transformIntoNPC(-1);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		if (slotId == 3)
			player.getCombatDefinitions().decreaseSpecialAttack(0);
	}

	private static void openWorldMap(Player player) {
		int posHash = player.getX() << 14 | player.getY();
		player.getPackets().sendGlobalConfig(622, posHash);
		player.getPackets().sendGlobalConfig(674, posHash);
		player.getPackets().sendWindowsPane(755, 0);

		// player.getInterfaceManager().sendInterface(1092);

	}

	public static void sendWear(Player player, int[] slotIds) {
		if (player.hasFinished() || player.isDead())
			return;
		boolean worn = false;
		Item[] copy = player.getInventory().getItems().getItemsCopy();
		for (int slotId : slotIds) {
			Item item = player.getInventory().getItem(slotId);
			if (item == null)
				continue;
			if (sendWear2(player, slotId, item.getId()))
				worn = true;
		}
		player.getInventory().refreshItems(copy);
		if (worn) {
			player.getGlobalPlayerUpdater().generateAppearenceData();
			player.getPackets().sendSound(2240, 0, 1);
		}
	}

	public static boolean sendWear(Player player, int slotId, int itemId) {
		if (player.hasFinished() || player.isDead())
			return false;
		player.stopAll(false, false);
		Item item = player.getInventory().getItem(slotId);
		String itemName = item.getDefinitions() == null ? "" : item.getDefinitions().getName().toLowerCase();
		if (item == null || item.getId() != itemId)
			return false;
		int targetSlot = Equipment.getItemSlot(itemId);
		// Just a placeholder for now
		if (itemName.contains("wings") && item.getId() >= 30000)
			targetSlot = Equipment.SLOT_CAPE;
		if (targetSlot == -1 || item.getDefinitions().isNoted() || targetSlot == 17) {
			player.getPackets().sendGameMessage("You can't wear this item; if it's a bug, report it on the forums.");
			return true;
		}
		if (!ItemConstants.canWear(item, player))
			return true;
		ClansManager manager = player.getClanManager();
		if (item.getId() == 20708 || item.getId() == 20709) {
			if (!(manager == null) && !player.getClanManager().getClan().getClanAchievements()
					.getClanHasCompletedAchievement(ClanAchievements.Achievements.INFLUENCE)) {
				player.getClanManager().getClan().getClanAchievements()
						.addClanCompletedAchievement(ClanAchievements.Achievements.INFLUENCE);
				player.getClanManager()
						.sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '"
								+ player.getClanManager().getClan().getClanAchievements()
										.getAchievementName(ClanAchievements.Achievements.INFLUENCE)
								+ "'.");
				player.getClanManager().getClan().getExperienceType().addEasyAchievementExp(player);
			}
		}
		boolean isTwoHandedWeapon = targetSlot == 3 && Equipment.isTwoHandedWeapon(item);
		if (isTwoHandedWeapon && !player.getInventory().hasFreeSlots() && player.getEquipment().getWeaponId() != -1
				&& player.getEquipment().hasShield()) {
			player.getPackets().sendGameMessage("Not enough free space in your inventory.");
			return false;
		}
		if (itemId == ThroneManager.ANCIENT_CROWN || itemId == ThroneManager.ANCIENT_SWORD) {
			if (!ThroneManager.getThrone().getKing().equals(player.getUsername())) {
				player.getInventory().deleteItem(itemId, 28);
				return false;
			}
		}
		HashMap<Integer, Integer> requiriments = item.getDefinitions().getWearingSkillRequiriments();
		boolean hasRequiriments = true;
		if (requiriments != null) {
			for (int skillId : requiriments.keySet()) {
				if (skillId > 24 || skillId < 0)
					continue;

				int level = requiriments.get(skillId);
				if (level < 0 || level > 120)
					continue;
				if (itemName.equalsIgnoreCase("dark bow") && skillId == Skills.DEFENCE)
					level = 42;
				if (player.getSkills().getLevelForXp(skillId) < level) {
					if (hasRequiriments)
						player.sendMessage("You are not high enough level to use this item.");
					hasRequiriments = false;
					String name = Skills.SKILL_NAME[skillId].toLowerCase();
					player.sendMessage("You need to have a" + (name.startsWith("a") ? "n" : "") + " " + name
							+ " level of " + level + ".");
				}
			}
		}
		if (!hasRequiriments)
			return true;
		if (!player.getControlerManager().canEquip(targetSlot, itemId))
			return false;
		player.stopAll(false, false);
		player.getInventory().deleteItem(slotId, item);
		if (targetSlot == 3) {
			if (isTwoHandedWeapon && player.getEquipment().getItem(5) != null) {
				if (!player.getInventory().addItem(player.getEquipment().getItem(5).getId(),
						player.getEquipment().getItem(5).getAmount())) {
					player.getInventory().getItems().set(slotId, item);
					player.getInventory().refresh(slotId);
					return true;
				}
				player.getEquipment().getItems().set(5, null);
			}
		} else if (targetSlot == 5) {
			if (player.getEquipment().getItem(3) != null
					&& Equipment.isTwoHandedWeapon(player.getEquipment().getItem(3))) {
				if (!player.getInventory().addItem(player.getEquipment().getItem(3).getId(),
						player.getEquipment().getItem(3).getAmount())) {
					player.getInventory().getItems().set(slotId, item);
					player.getInventory().refresh(slotId);
					return true;
				}
				player.getEquipment().getItems().set(3, null);
			}
		}
		if (player.getEquipment().getItem(targetSlot) != null
				&& (itemId != player.getEquipment().getItem(targetSlot).getId()
						|| !item.getDefinitions().isStackable())) {
			if (player.getInventory().getItems().get(slotId) == null) {
				player.getInventory().getItems().set(slotId, new Item(player.getEquipment().getItem(targetSlot).getId(),
						player.getEquipment().getItem(targetSlot).getAmount()));
				player.getInventory().refresh(slotId);
			} else
				player.getInventory().addItem(new Item(player.getEquipment().getItem(targetSlot).getId(),
						player.getEquipment().getItem(targetSlot).getAmount()));
			player.getEquipment().getItems().set(targetSlot, null);
		}
		if (targetSlot == Equipment.SLOT_AURA)
			player.getAuraManager().removeAura();
		int oldAmt = 0;
		if (player.getEquipment().getItem(targetSlot) != null)
			oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
		Item item2 = new Item(itemId, oldAmt + item.getAmount());
		player.getEquipment().getItems().set(targetSlot, item2);
		player.getEquipment().refresh(targetSlot, targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		player.getPackets().sendSound(2240, 0, 1);
		if (!player.getPerkManager().chargeBefriender)
			player.getCharges().wear(targetSlot);
		if (player.getHitpoints() > (player.getMaxHitpoints() * 1.15)) {
			player.setHitpoints(player.getMaxHitpoints());
			player.refreshHitPoints();
		}
		if (targetSlot == Equipment.SLOT_WEAPON && itemId != 15486) {
			if (player.polDelay > Utils.currentTimeMillis()) {
				player.setPolDelay(0);
				player.sendMessage("The power of the light fades. Your resistance to melee attacks return to normal.");
			}
		}
		return true;
	}

	public static boolean sendWear2(Player player, int slotId, int itemId) {
		if (player.hasFinished() || player.isDead())
			return false;
		player.stopAll(false, false);
		Item item = player.getInventory().getItem(slotId);
		if (item == null || item.getId() != itemId)
			return false;
		String itemName = item.getDefinitions() == null ? "" : item.getDefinitions().getName().toLowerCase();
		int targetSlot = Equipment.getItemSlot(itemId);
		if (itemName.contains("wings") && item.getId() >= 30000)
			targetSlot = Equipment.SLOT_CAPE;
		if (targetSlot == -1 || item.getDefinitions().isNoted() || targetSlot == 20) {
			player.getPackets().sendGameMessage("You can't wear this item; if it's a bug, report it on the forums.");
			return true;
		}
		if (!ItemConstants.canWear(item, player))
			return false;
		ClansManager manager = player.getClanManager();
		if (item.getId() == 20708 || item.getId() == 20709) {
			if (!(manager == null) && !player.getClanManager().getClan().getClanAchievements()
					.getClanHasCompletedAchievement(ClanAchievements.Achievements.INFLUENCE)) {
				player.getClanManager().getClan().getClanAchievements()
						.addClanCompletedAchievement(ClanAchievements.Achievements.INFLUENCE);
				player.getClanManager()
						.sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '"
								+ player.getClanManager().getClan().getClanAchievements()
										.getAchievementName(ClanAchievements.Achievements.INFLUENCE)
								+ "'.");
				player.getClanManager().getClan().getExperienceType().addEasyAchievementExp(player);
			}
		}
		boolean isTwoHandedWeapon = targetSlot == 3 && Equipment.isTwoHandedWeapon(item);
		if (isTwoHandedWeapon && !player.getInventory().hasFreeSlots() && player.getEquipment().getWeaponId() != -1
				&& player.getEquipment().hasShield()) {
			player.sendMessage("Not enough free space in your inventory.");
			return false;
		}
		if (itemId == ThroneManager.ANCIENT_CROWN || itemId == ThroneManager.ANCIENT_SWORD) {
			if (!ThroneManager.getThrone().getKing().equals(player.getUsername())) {
				player.getInventory().deleteItem(itemId, 28);
				return false;
			}
		}
		HashMap<Integer, Integer> requiriments = item.getDefinitions().getWearingSkillRequiriments();
		boolean hasRequiriments = true;
		if (requiriments != null) {
			for (int skillId : requiriments.keySet()) {
				if (skillId > 24 || skillId < 0)
					continue;
				int level = requiriments.get(skillId);
				if (level < 0 || level > 120)
					continue;
				if (player.getSkills().getLevelForXp(skillId) < level) {
					if (hasRequiriments)
						player.sendMessage("You are not high enough level to use this item.");
					hasRequiriments = false;
					String name = Skills.SKILL_NAME[skillId].toLowerCase();
					player.sendMessage("You need to have a" + (name.startsWith("a") ? "n" : "") + " " + name
							+ " level of " + level + ".");
				}
			}
		}
		if (!hasRequiriments)
			return false;
		if (!player.getControlerManager().canEquip(targetSlot, itemId))
			return false;
		player.getInventory().getItems().remove(slotId, item);
		if (targetSlot == 3) {
			if (isTwoHandedWeapon && player.getEquipment().getItem(5) != null) {
				if (!player.getInventory().getItems().add(player.getEquipment().getItem(5))) {
					player.getInventory().getItems().set(slotId, item);
					return false;
				}
				player.getEquipment().getItems().set(5, null);
			}
		} else if (targetSlot == 5) {
			if (player.getEquipment().getItem(3) != null
					&& Equipment.isTwoHandedWeapon(player.getEquipment().getItem(3))) {
				if (!player.getInventory().getItems().add(player.getEquipment().getItem(3))) {
					player.getInventory().getItems().set(slotId, item);
					return false;
				}
				player.getEquipment().getItems().set(3, null);
			}

		}
		if (player.getEquipment().getItem(targetSlot) != null
				&& (itemId != player.getEquipment().getItem(targetSlot).getId()
						|| !item.getDefinitions().isStackable())) {
			if (player.getInventory().getItems().get(slotId) == null) {
				player.getInventory().getItems().set(slotId, new Item(player.getEquipment().getItem(targetSlot).getId(),
						player.getEquipment().getItem(targetSlot).getAmount()));
			} else
				player.getInventory().getItems().add(new Item(player.getEquipment().getItem(targetSlot).getId(),
						player.getEquipment().getItem(targetSlot).getAmount()));
			player.getEquipment().getItems().set(targetSlot, null);

		}
		if (targetSlot == Equipment.SLOT_AURA)
			player.getAuraManager().removeAura();
		int oldAmt = 0;
		if (player.getEquipment().getItem(targetSlot) != null)
			oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
		Item item2 = new Item(itemId, oldAmt + item.getAmount());
		player.getEquipment().getItems().set(targetSlot, item2);
		player.getEquipment().refresh(targetSlot, targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
		if (targetSlot == 3)
			player.getCombatDefinitions().decreaseSpecialAttack(0);
		if (!player.getPerkManager().chargeBefriender)
			player.getCharges().wear(targetSlot);
		if (player.getHitpoints() > (player.getMaxHitpoints() * 1.15)) {
			player.setHitpoints(player.getMaxHitpoints());
			player.refreshHitPoints();
		}
		if (targetSlot == Equipment.SLOT_WEAPON && itemId != 15486) {
			if (player.polDelay > Utils.currentTimeMillis()) {
				player.setPolDelay(0);
				player.sendMessage("The power of the light fades. Your resistance to melee attacks return to normal.");
			}
		}
		return true;
	}

	public static void submitSpecialRequest(final Player player) {
		CoresManager.fastExecutor.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.getCombatDefinitions().switchUsingSpecialAttack();
							this.stop();
						}
					}, 0);
				} catch (Throwable e) {
					Logger.handle(e);
				}
			}
		}, 200);
	}

	public static String sendLoanItemExamine(int slotId2) {
		Item item = new Item(slotId2);
		return item.getDefinitions().getExamine();
	}

	public static boolean wear(Player player, int slotId, int itemId, boolean fromBank) {
		if (player.hasFinished() || player.isDead())
			return false;
		player.stopAll(false, false);
		Item item = player.getInventory().getItem(slotId);
		if (item == null || item.getId() != itemId)
			return false;
		if (item.getDefinitions().isNoted()
				|| !item.getDefinitions().isWearItem(player.getGlobalPlayerUpdater().isMale()) && itemId != 4084) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return false;
		}
		int targetSlot = Equipment.getItemSlot(itemId);
		if (itemId == 4084)
			targetSlot = 3;
		if (targetSlot == -1) {
			player.getPackets().sendGameMessage("You can't wear that.");
			return false;
		}
		if (!ItemConstants.canWear(item, player))
			return false;
		ClansManager manager = player.getClanManager();
		if (item.getId() == 20708 || item.getId() == 20709) {
			if (!(manager == null) && !player.getClanManager().getClan().getClanAchievements()
					.getClanHasCompletedAchievement(ClanAchievements.Achievements.INFLUENCE)) {
				player.getClanManager().getClan().getClanAchievements()
						.addClanCompletedAchievement(ClanAchievements.Achievements.INFLUENCE);
				player.getClanManager()
						.sendGlobalMessage("<col=CF8D1D>" + player.getClanName() + " has just earned the achievement '"
								+ player.getClanManager().getClan().getClanAchievements()
										.getAchievementName(ClanAchievements.Achievements.INFLUENCE)
								+ "'.");
				player.getClanManager().getClan().getExperienceType().addEasyAchievementExp(player);
			}
		}
		boolean isTwoHandedWeapon = targetSlot == 3 && Equipment.isTwoHandedWeapon(item);
		if (isTwoHandedWeapon && player.getEquipment().getItem(Equipment.SLOT_SHIELD) != null
				&& !player.getInventory().hasFreeSlots()) {
			player.getPackets().sendGameMessage("Not enough free space in your inventory.");
			return false;
		}
		HashMap<Integer, Integer> requiriments = item.getDefinitions().getWearingSkillRequiriments();
		boolean hasRequiriments = true;
		if (requiriments != null) {
			for (int skillId : requiriments.keySet()) {
				if (skillId > 24 || skillId < 0)
					continue;
				int level = requiriments.get(skillId);
				if (level < 0 || level > 120)
					continue;
				if (player.getSkills().getLevelForXp(skillId) < level) {
					if (hasRequiriments)
						player.getPackets().sendGameMessage("You are not high enough level to use this item.");
					hasRequiriments = false;
					String name = Skills.SKILL_NAME[skillId].toLowerCase();
					player.getPackets().sendGameMessage("You need to have a" + (name.startsWith("a") ? "n" : "") + " "
							+ name + " level of " + level + ".");
				}

			}
		}
		if (!hasRequiriments)
			return false;
		if (!player.getControlerManager().canEquip(targetSlot, itemId))
			return false;
		player.getInventory().getItems().remove(slotId, item);
		if (targetSlot == 3) {
			if (isTwoHandedWeapon && player.getEquipment().getItem(5) != null) {
				if (!player.getInventory().getItems().add(player.getEquipment().getItem(5))) {
					player.getInventory().getItems().set(slotId, item);
					return false;
				}
				player.getEquipment().getItems().set(5, null);
			}
		} else if (targetSlot == 5) {
			if (player.getEquipment().getItem(3) != null
					&& Equipment.isTwoHandedWeapon(player.getEquipment().getItem(3))) {
				if (!player.getInventory().getItems().add(player.getEquipment().getItem(3))) {
					player.getInventory().getItems().set(slotId, item);
					return false;
				}
				player.getEquipment().getItems().set(3, null);
			}

		}
		if (player.getEquipment().getItem(targetSlot) != null
				&& (itemId != player.getEquipment().getItem(targetSlot).getId()
						|| !item.getDefinitions().isStackable())) {
			if (player.getInventory().getItems().get(slotId) == null) {
				player.getInventory().getItems().set(slotId, new Item(player.getEquipment().getItem(targetSlot).getId(),
						player.getEquipment().getItem(targetSlot).getAmount()));
			} else
				player.getInventory().getItems().add(new Item(player.getEquipment().getItem(targetSlot).getId(),
						player.getEquipment().getItem(targetSlot).getAmount()));
			player.getEquipment().getItems().set(targetSlot, null);
		}
		if (targetSlot == Equipment.SLOT_AURA)
			player.getAuraManager().removeAura();
		int oldAmt = 0;
		if (player.getEquipment().getItem(targetSlot) != null) {
			oldAmt = player.getEquipment().getItem(targetSlot).getAmount();
		}
		Item item2 = new Item(itemId, oldAmt + item.getAmount());
		player.getEquipment().getItems().set(targetSlot, item2);
		player.getEquipment().refresh(targetSlot, targetSlot == 3 ? 5 : targetSlot == 3 ? 0 : 3);
		if (targetSlot == 3)
			player.getCombatDefinitions().desecreaseSpecialAttack(0);
		player.getCharges().wear(targetSlot);
		if (player.getInterfaceManager().containsInterface(1463))
			refreshEquipBonuses(player);
		// player.getPackets().sendSoundEffect(item.getDefinitions().getCSOpcode(118),
		// 0, 1);
		return true;
	}

	public static void teleportPlayer(Player player, final int placeX, final int placeY, final int placePlane,
			String controller) {

		Magic.vineTeleport(player, new WorldTile(placeX, placeY, placePlane));
		final WorldTile teleTile = new WorldTile(placeX, placeY, placePlane);
		if (!player.getControlerManager().processMagicTeleport(teleTile))
			return;
		player.lock(4);
		player.stopAll();
		player.setNextGraphics(new Graphics(1228));
		player.setNextAnimation(new Animation(7084));

		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextAnimation(new Animation(7084));
				player.setNextGraphics(new Graphics(1228));
				player.setNextWorldTile(teleTile);
				player.getControlerManager().magicTeleported(Magic.MAGIC_TELEPORT);
				player.checkMovement(placeX, placeY, placePlane);
				if (player.getControlerManager().getControler() == null)
					Magic.teleControlersCheck(player, teleTile);
				if (controller != null)
					player.getControlerManager().startControler(controller);
				player.unlock();
				stop();
			}
		}, 4);
	}
}