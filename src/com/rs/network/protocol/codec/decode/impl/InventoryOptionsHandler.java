package com.rs.network.protocol.codec.decode.impl;

import java.util.List;
import java.util.ArrayList;

import com.discord.Discord;
import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cores.WorldThread;
import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.TemporaryAtributtes;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activites.CrystalChest;
import com.rs.game.item.Item;
import com.rs.game.item.MagicOnItem;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar.SpecialAttack;
import com.rs.game.npc.others.ConditionalDeath;
import com.rs.game.npc.pet.Pet;
import com.rs.game.player.ChargesManager;
import com.rs.game.player.Equipment;
import com.rs.game.player.FarmingManager;
import com.rs.game.player.Inventory;
import com.rs.game.player.LendingManager;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.XPSharing;
import com.rs.game.player.actions.CombinationPotions;
import com.rs.game.player.actions.crafting.AnimaCoreCreation.AnimaCoreData;
import com.rs.game.player.actions.crafting.BattleStaffCrafting;
import com.rs.game.player.actions.crafting.BattleStaffCrafting.Orbs;
import com.rs.game.player.actions.crafting.CrystalGlassBlowing;
import com.rs.game.player.actions.crafting.GemCutting;
import com.rs.game.player.actions.crafting.GemCutting.Gem;
import com.rs.game.player.actions.crafting.JewellerySmithing;
import com.rs.game.player.actions.crafting.LeatherCrafting;
import com.rs.game.player.actions.crafting.MalevolentCrafting;
import com.rs.game.player.actions.crafting.SirenicScaleCrafting;
import com.rs.game.player.actions.crafting.EliteSirenicScaleCrafting;
import com.rs.game.player.actions.crafting.TectonicEnergyCrafting;
import com.rs.game.player.actions.crafting.EliteTectonicEnergyCrafting;
import com.rs.game.player.actions.divination.DivineObject;
import com.rs.game.player.actions.divination.WeavingEnergy;
import com.rs.game.player.actions.divination.WeavingEnergy.Energy;
import com.rs.game.player.actions.firemaking.Firemaking;
import com.rs.game.player.actions.fletching.BoltTipFletching;
import com.rs.game.player.actions.fletching.BoltTipFletching.BoltTips;
import com.rs.game.player.actions.fletching.Fletching;
import com.rs.game.player.actions.fletching.Fletching.Fletch;
import com.rs.game.player.actions.herblore.FlaskDecanting;
import com.rs.game.player.actions.herblore.HerbCleaning;
import com.rs.game.player.actions.herblore.Herblore;
import com.rs.game.player.actions.herblore.PotionDecanting;
import com.rs.game.player.actions.herblore.herbicide.Herbicide;
import com.rs.game.player.actions.hunter.FlyingEntityHunter;
import com.rs.game.player.actions.hunter.FlyingEntityHunter.FlyingEntities;
import com.rs.game.player.actions.hunter.TrapAction;
import com.rs.game.player.actions.summoning.Summoning;
import com.rs.game.player.actions.summoning.Summoning.Pouches;
import com.rs.game.player.content.BossTeleports;
import com.rs.game.player.content.Burying.Bone;
import com.rs.game.player.content.CosmeticsHandler;
import com.rs.game.player.content.Dicing;
import com.rs.game.player.content.FlowerGame;
import com.rs.game.player.content.Foods;
import com.rs.game.player.content.ItemCombining;
import com.rs.game.player.content.ItemCombining.Combinings;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.content.ItemSets;
import com.rs.game.player.content.ItemSets.Sets;
import com.rs.game.player.content.LividFarm;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.PolyporeDungeon;
import com.rs.game.player.content.Pots;
import com.rs.game.player.content.RepairItems.BrokenItems;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.content.dungeoneering.rooms.puzzles.ColouredRecessRoom.Block;
import com.rs.game.player.content.RouteEvent;
import com.rs.game.player.content.RuneCrafting;
import com.rs.game.player.content.SkillCapeCustomizer;
import com.rs.game.player.content.Slayer;
import com.rs.game.player.content.UpgradeSystem;
import com.rs.game.player.content.UpgradeSystem.Upgradables;
import com.rs.game.player.content.WeaponPoison;
import com.rs.game.player.content.XPLamps;
import com.rs.game.player.content.items.*;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.player.content.ports.SuperiorExchange.PortArmor;
import com.rs.game.player.content.xmas.XmasRiddles;
import com.rs.game.player.content.xmas.XmasRiddles.Riddle;
import com.rs.game.player.controllers.Barrows;
import com.rs.game.player.controllers.FightKiln;/*
												import com.rs.game.player.controllers.InstancedPVPControler;*/
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.dialogue.impl.CasinoEntranceD;
import com.rs.game.player.dialogue.impl.CombinationsD.Combinations;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.stream.InputStream;
import com.rs.utils.Colors;
import com.rs.utils.Lend;
import com.rs.utils.Logger;
import com.rs.utils.LoggingSystem;
import com.rs.utils.Utils;

public class InventoryOptionsHandler {

	public static boolean contains(int id1, int id2, Item... items) {
		boolean containsId1 = false;
		boolean containsId2 = false;
		for (Item item : items) {
			if (item.getId() == id1)
				containsId1 = true;
			else if (item.getId() == id2)
				containsId2 = true;
		}
		return containsId1 && containsId2;
	}

	/*
	 * returns the other
	 */
	public static Item contains(int id1, Item item1, Item item2) {
		if (item1.getId() == id1)
			return item2;
		if (item2.getId() == id1)
			return item1;
		return null;
	}

	public static void dig(final Player player) {
		player.resetWalkSteps();
		player.setNextAnimation(new Animation(830));
		player.lock();
		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.unlock();
				if (player.getXmas().inXmas) { // riddles 1 and 2
					if (player.getXmas().riddle == null)
						return;
					Riddle ours = player.getXmas().riddle;
					if (ours.getIndex() == 1 || ours.getIndex() == 2) {
						if (player.getX() == ours.getCoords()[0] && player.getY() == ours.getCoords()[1]) {
							XmasRiddles.finishRiddle(player);
							player.sendMessage(
									Colors.green + "You have managed to solve the riddle and are greatly rewarded!",
									false);
							if (!player.getXmas().finishedRiddles())
								player.sendMessage(Colors.dcyan + "You can get another riddle from the Queen of Snow!");
							if (ours.getIndex() == 1)
								player.getXmas().riddle1 = true;
							else
								player.getXmas().riddle2 = true;
						}
						return;
					}
				}
				if (player.getTreasureTrails().useDig())
					return;
				if (Barrows.digIntoGrave(player))
					return;
				if (!player.getNewQuestManager().useDig())
					return;
				if (player.getX() == 3005 && player.getY() == 3376 || player.getX() == 2999 && player.getY() == 3375
						|| player.getX() == 2996 && player.getY() == 3377
						|| player.getX() == 2989 && player.getY() == 3378
						|| player.getX() == 2987 && player.getY() == 3387
						|| player.getX() == 2984 && player.getY() == 3387) {
					// mole
					player.setNextWorldTile(new WorldTile(1752, 5137, 0));
					player.sendMessage("You seem to have dropped down into a network of mole tunnels.");
					return;
				}
				player.sendMessage("You find nothing.");
			}

		});
	}

	public static void handleItemOnItem(final Player player, InputStream stream) {
		int itemUsedWithId = stream.readUnsignedShort();
		int toSlot = stream.readUnsignedShortLE128();
		int interfaceId = stream.readUnsignedShort();
		int interfaceComponent = stream.readUnsignedShort();
		int interfaceId2 = stream.readUnsignedShort();
		@SuppressWarnings("unused")
		int interfaceId2Component = stream.readUnsignedShort();
		int fromSlot = stream.readUnsignedShort();
		int itemUsedId = stream.readUnsignedShortLE128();
		Item fromItem = player.getInventory().getItem(fromSlot);
		Item toItem = player.getInventory().getItem(toSlot);
		player.stopAll();

		if (interfaceId2 == 679) {
			if (interfaceId == 192 || interfaceId == 430) {
				MagicOnItem.handleMagic(player, interfaceComponent, player.getInventory().getItem(toSlot));
				return;
			}
		}
		if ((interfaceId == 747 || interfaceId == 662) && interfaceId2 == Inventory.INVENTORY_INTERFACE) {
			if (player.getFamiliar() != null) {
				player.getFamiliar().setSpecial(true);
				if (player.getFamiliar().getSpecialAttack() == SpecialAttack.ITEM) {
					if (player.getFamiliar().hasSpecialOn())
						player.getFamiliar().submitSpecial(toSlot);
				}
			}
			return;
		}
		if (Combinings.forItem(itemUsedId, itemUsedWithId) != null) {
			if (ItemCombining.CombineItems(player, itemUsedId, itemUsedWithId)) {
				return;
			}
		}
		if (Combinings.forProduct(itemUsedId) != null) {
			if (ItemCombining.RemoveDyeFromItems(player, itemUsedId, itemUsedWithId)) {
				return;
			}
		} else if (Combinings.forProduct(itemUsedWithId) != null) {
			if (ItemCombining.RemoveDyeFromItems(player, itemUsedWithId, itemUsedId)) {
				return;
			}
		}
		if (interfaceId == Inventory.INVENTORY_INTERFACE && interfaceId == interfaceId2
				&& !player.getInterfaceManager().containsInventoryInter()) {
			if (toSlot >= 28 || fromSlot >= 28)
				return;
			Item usedWith = player.getInventory().getItem(toSlot);
			Item itemUsed = player.getInventory().getItem(fromSlot);
			if (itemUsed == null || usedWith == null || itemUsed.getId() != itemUsedId
					|| usedWith.getId() != itemUsedWithId)
				return;
			if (itemUsed == usedWith || usedWith == itemUsed)
				return;
			if (!player.getControlerManager().canUseItemOnItem(itemUsed, usedWith))
				return;
			if (!player.getNewQuestManager().canUseItemOnItem(itemUsed, usedWith))
				return;
			if (CosmeticsHandler.keepSakeItem(player, itemUsed, usedWith))
				return;
			if (itemUsed.getId() == CombinationPotions.CRYSTAL_FLASK
					|| usedWith.getId() == CombinationPotions.CRYSTAL_FLASK) {
				player.getPackets().sendGameMessage("Try clicking on the flask to mix potions.");
				return;
			}
			Combinations combination = Combinations.isCombining(itemUsedId, itemUsedWithId);
			if (combination != null) {
				player.getDialogueManager().startDialogue("CombinationsD", combination);
				return;
			}
			if (Pots.mixPot(player, itemUsed, usedWith, fromSlot, toSlot, true) != -1)
				return;
			Fletch fletch = Fletching.isFletching(usedWith, itemUsed);
			if (fletch != null) {
				player.getDialogueManager().startDialogue("FletchingD", fletch);
				return;
			}
			if (itemUsed.getId() == 27360 || usedWith.getId() == 995) {
				if (!player.getInventory().containsItem(27360, 1)) {
					return;
				}
				player.getDialogueManager().startDialogue("ResetTask");
				return;
			}

			if ((itemUsed.getId() == 20680 && usedWith.getId() == 20685)
					|| itemUsed.getId() == 20685 && usedWith.getId() == 20680) {
				if (player.getEquipment().getHatId() == 1506) {
					player.getInventory().deleteItem(20680, 1);
					player.getInventory().deleteItem(20685, 1);
					player.getInventory().addItem(20692, 1);
					player.sendMessage(Colors.green + "You successfully made a barrel of gunpowder.");
				} else
					player.sendMessage(Colors.red + "You must be wearing a gas mask to make this!");
			}
			if (usedWith.getId() == 34889) {
				if (!player.getInventory().containsItem(34889, 1)) {
					player.sm(Colors.red + "You relic of life to re-charge your weapon/armour!");
				}
				player.getCharges().addCharges(itemUsedId, ItemConstants.getItemDefaultCharges(itemUsedId), 0);
				player.getInventory().deleteItem(34889, 1);
				player.sm(Colors.red + "[Re-Charge]</col>" + Colors.green
						+ " You successfully recharged your weapon/armor");

				return;
			}
			Upgradables upgrade = Upgradables.getBaseItem(usedWith.getId());

			if (upgrade != null && upgrade.isArmour() && itemUsed.getId() == UpgradeSystem.UPGRADESTONE) {
				player.sm("You can check the list of upgradeable weapon and armors by doing ;;upgradelist");
				UpgradeSystem.Upgrade(player, usedWith, itemUsed, upgrade, UpgradeSystem.UPGRADESTONE);
				player.sm(Colors.red + "[Upgrade]:</col>" + Colors.green + " Upgrading...");
				return;
			}
			if (upgrade == null && itemUsed.getId() == UpgradeSystem.UPGRADESTONE) {
				player.sm(Colors.red + "[Upgrade]:</col> This item is not Upgradeable.");
				player.sm(Colors.green
						+ "You can check the list of upgradeable weapon and armors by doing ;;upgradelist");
				return;
			}
			if (upgrade != null && upgrade.isArmour() && itemUsed.getId() == UpgradeSystem.PERFECT_UPGRADESTONE) {
				UpgradeSystem.Upgrade(player, usedWith, itemUsed, upgrade, UpgradeSystem.PERFECT_UPGRADESTONE);
				return;
			}
			if (itemUsed.getId() == 34838 || usedWith.getId() == 34838) {
				if (itemUsed.getId() != 31725 && usedWith.getId() != 31725)
					return;
				else {
					player.getDialogueManager().startDialogue(new Dialogue() {

						@Override
						public void start() {
							sendItemDialogue(33625, 1, "Are you sure you want to dye your noxious scythe " + Colors.shad
									+ Colors.dcyan + "Christmas color</col></shad>?");
							stage = 0;
						}

						@Override
						public void run(int interfaceId, int componentId) {
							switch (stage) {

							case 0:
								sendOptionsDialogue("Do you want to do this?", "Yes", "No");
								stage = 1;
								break;
							case 1:
								switch (componentId) {
								case OPTION_1:
									finish();
									player.getInventory().deleteItem(34838, 1);
									player.getInventory().deleteItem(31725, 1);
									player.getInventory().addItem(33625, 1);
									player.getXmas().announceDrop(" has crafted a Christmas-dyed noxious scythe!");
									break;
								case OPTION_2:
									finish();
									break;
								}
								break;
							}
						}

						@Override
						public void finish() {
							player.getInterfaceManager().closeChatBoxInterface();
						}

					});
				}
			}

			if (itemUsed.getId() == 3188 && DyedItems.isDyed(usedWith.getId())
					|| usedWith.getId() == 3188 && DyedItems.isDyed(itemUsed.getId())) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					int dyed;
					int undyed;

					@Override
					public void start() {
						dyed = usedWith.getId() == 3188 ? itemUsed.getId() : usedWith.getId();
						undyed = DyedItems.getUndye(dyed);
						sendItemDialogue(dyed, 1,
								"This action will destroy your cleaning cloth and you will lose the dye!");
						stage = 0;
					}

					@Override
					public void run(int interfaceId, int componentId) {
						switch (stage) {
						case 0:
							sendOptionsDialogue("Do you want to undye this item?", "Yes", "No");
							stage = 1;
							break;
						case 1:
							if (componentId == OPTION_1) {
								player.getInventory().deleteItem(dyed, 1);
								player.getInventory().deleteItem(3188, 1);
								player.getInventory().addItem(undyed, 1);
								player.sendMessage(
										Colors.green + "You have undyed an item! Your cleansing cloth disappears!",
										true);
							}
							finish();
							break;
						}
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}
				});
				return;
			}

			if (itemUsed.getId() == 27587 && usedWith.getId() == 34921
					|| usedWith.getId() == 27587 && itemUsed.getId() == 34921) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						int reward = 34925;
						player.getInventory().deleteItem(itemUsed.getId(), 1);
						player.getInventory().deleteItem(usedWith.getId(), 1);
						player.getInventory().addItem(reward, 1);
						sendItemDialogue(reward, 1, "You have created a modified first age tiara!");
					}

					@Override
					public void run(int interfaceId, int componentId) {
						finish();
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}

				});
				return;
			}
			if (itemUsed.getId() == 25185 && usedWith.getId() == 32277
					|| usedWith.getId() == 25185 && itemUsed.getId() == 32277)

			{
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						int reward = 32281;
						player.getInventory().deleteItem(itemUsed.getId(), 1);
						player.getInventory().deleteItem(usedWith.getId(), 1);
						player.getInventory().addItem(reward, 1);
						sendItemDialogue(reward, 1, "You have created a modified artisan's bandana!");
					}

					@Override
					public void run(int interfaceId, int componentId) {
						finish();
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}

				});
				return;
			}

			if (itemUsed.getId() == 25190 && usedWith.getId() == 34919
					|| usedWith.getId() == 25190 && itemUsed.getId() == 34919) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						int reward = 34923;
						player.getInventory().deleteItem(itemUsed.getId(), 1);
						player.getInventory().deleteItem(usedWith.getId(), 1);
						player.getInventory().addItem(reward, 1);
						sendItemDialogue(reward, 1, "You have created a modified botanist's mask!");
					}

					@Override
					public void run(int interfaceId, int componentId) {
						finish();
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}
				});
				return;
			}

			if (itemUsed.getId() == 29865 && usedWith.getId() == 32275
					|| usedWith.getId() == 29865 && itemUsed.getId() == 32275) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						int reward = 32279;
						player.getInventory().deleteItem(itemUsed.getId(), 1);
						player.getInventory().deleteItem(usedWith.getId(), 1);
						player.getInventory().addItem(reward, 1);
						sendItemDialogue(reward, 1, "You have created a modified diviner's headwear!");
					}

					@Override
					public void run(int interfaceId, int componentId) {
						finish();
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}

				});
				return;
			}
			if (itemUsed.getId() == 28995 && usedWith.getId() == 32274
					|| usedWith.getId() == 28995 && itemUsed.getId() == 32274) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						int reward = 32278;
						player.getInventory().deleteItem(itemUsed.getId(), 1);
						player.getInventory().deleteItem(usedWith.getId(), 1);
						player.getInventory().addItem(reward, 1);
						sendItemDialogue(reward, 1, "You have created a modified shaman's headdress!");
					}

					@Override
					public void run(int interfaceId, int componentId) {
						finish();
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}

				});
				return;
			}
			if (itemUsed.getId() == 25180 && usedWith.getId() == 34920
					|| usedWith.getId() == 25180 && itemUsed.getId() == 34920) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						int reward = 34924;
						player.getInventory().deleteItem(itemUsed.getId(), 1);
						player.getInventory().deleteItem(usedWith.getId(), 1);
						player.getInventory().addItem(reward, 1);
						sendItemDialogue(reward, 1, "You have created a modified sous chef's toque!");
					}

					@Override
					public void run(int interfaceId, int componentId) {
						finish();
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}

				});
				return;
			}
			if (itemUsed.getId() == 25195 && usedWith.getId() == 32276
					|| usedWith.getId() == 25195 && itemUsed.getId() == 32276) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						int reward = 32280;
						player.getInventory().deleteItem(itemUsed.getId(), 1);
						player.getInventory().deleteItem(usedWith.getId(), 1);
						player.getInventory().addItem(reward, 1);
						sendItemDialogue(reward, 1, "You have created a modified blacksmith's helmet!");
					}

					@Override
					public void run(int interfaceId, int componentId) {
						finish();
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}

				});
				return;
			}
			if (itemUsed.getId() == 31347 && usedWith.getId() == 34922
					|| usedWith.getId() == 31347 && itemUsed.getId() == 34922) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						int reward = 34926;
						player.getInventory().deleteItem(itemUsed.getId(), 1);
						player.getInventory().deleteItem(usedWith.getId(), 1);
						player.getInventory().addItem(reward, 1);
						sendItemDialogue(reward, 1, "You have created a modified farmer's hat!");
					}

					@Override
					public void run(int interfaceId, int componentId) {
						finish();
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}
				});
				return;
			}
			if (ItemRecolor.itemRecolor(player, itemUsedId, itemUsedWithId))
				return;
			if (ItemRecolor.itemRecolor(player, itemUsedWithId, itemUsedId))
				return;
			if (HoodedCapes.handleHooding(player, itemUsedId, itemUsedWithId))
				return;
			if (HoodedCapes.handleHooding(player, itemUsedWithId, itemUsedId))
				return;
			if (ShadeSkull.attachSkull(player, itemUsed, usedWith))
				return;
			if (WeaponPoison.poison(player, itemUsed, usedWith, false))
				return;
			if (itemUsed.getId() == 1775 && usedWith.getId() == 1785
					|| usedWith.getId() == 1775 && itemUsed.getId() == 1785) {
				player.getDialogueManager().startDialogue("GlassblowingD");
				return;
			}
			if (PrayerBooks.isGodBook(itemUsedId, false) || PrayerBooks.isGodBook(itemUsedWithId, false)) {
				PrayerBooks.bindPages(player, itemUsed.getName().contains(" page ") ? itemUsedWithId : itemUsedId);
				return;
			}
			if (itemUsed.getId() >= 31721 && itemUsed.getId() <= 31724 && usedWith.getId() >= 31721
					&& usedWith.getId() <= 31724) {
				player.getDialogueManager().startDialogue("NoxiousCreateD");
				return;
			}
			if (itemUsed.getId() >= 31718 && itemUsed.getId() <= 31720 && usedWith.getId() >= 31718
					&& usedWith.getId() <= 31720) {
				AraxCrafting.handleSpiderLeg(player);
				return;
			}

			if (itemUsed.getId() >= 40663 && itemUsed.getId() <= 40667 && usedWith.getId() >= 40663
					&& usedWith.getId() <= 40667) {
				JournalCrafting.handleSoulFragment(player);
				return;
			}
			if (itemUsed.getId() >= 37619 && itemUsed.getId() <= 37620 && usedWith.getId() >= 37621
					&& usedWith.getId() <= 37614) {
				TelosCrafting.handleAncientSigil(player);
				return;
			}
			if (itemUsed.getId() == 30372 && usedWith != null) {
				MagicNotepaper.handleNote(player, usedWith);
				return;
			}
			if (usedWith.getId() == 30372 && itemUsed != null) {
				MagicNotepaper.handleNote(player, itemUsed);
				return;
			}
			if (itemUsed.getId() == 33294 || itemUsed.getId() == 33296 || itemUsed.getId() == 33298
					|| itemUsed.getId() == 36274) {
				player.getDialogueManager().startDialogue("ClueScrollDyes", usedWith, itemUsed);
				return;
			}
			if (itemUsed.getId() == 34972 || itemUsed.getId() == 34974 || itemUsed.getId() == 34976) {
				player.getDialogueManager().startDialogue("GlacorBootUpgrade", usedWith, itemUsed);
				return;
			}
			if (usedWith.getId() == 34972 || usedWith.getId() == 34974 || usedWith.getId() == 34976) {
				player.getDialogueManager().startDialogue("GlacorBootUpgrade", itemUsed, usedWith);
				return;
			}
			if (usedWith.getId() == 33294 || usedWith.getId() == 33296 || usedWith.getId() == 33298
					|| itemUsed.getId() == 36274) {
				player.getDialogueManager().startDialogue("ClueScrollDyes", itemUsed, usedWith);
				return;
			}
			if (itemUsed.getId() == 2368 && usedWith.getId() == 2366
					|| itemUsed.getId() == 2366 && usedWith.getId() == 2368) {
				player.getInventory().deleteItem(itemUsed);
				player.getInventory().deleteItem(usedWith);
				player.getInventory().addItem(1187, 1);
				player.sendMessage("You combine both shield parts into a full square shield!");
				return;
			}
			if (itemUsed.getId() == 32625 && usedWith.getId() == 32228
					|| itemUsed.getId() == 32228 && usedWith.getId() == 32625) {
				player.getInventory().deleteItem(32625, 1);
				player.getInventory().deleteItem(32228, 1);
				player.getInventory().addItem(32653, 1);
				player.sendMessage("You've attuned (upgraded) your Crystal bow!");
				return;
			}
			if (itemUsed.getId() == 32625 && usedWith.getId() == 32240
					|| itemUsed.getId() == 32240 && usedWith.getId() == 32625) {
				player.getInventory().deleteItem(32625, 1);
				player.getInventory().deleteItem(32240, 1);
				player.getInventory().addItem(32627, 1);
				player.sendMessage("You've attuned (upgraded) your Crystal shield!");
				return;

			}
			if (itemUsed.getId() == 32625 && usedWith.getId() == 32210
					|| itemUsed.getId() == 32210 && usedWith.getId() == 32625) {
				player.getInventory().deleteItem(32625, 1);
				player.getInventory().deleteItem(32210, 1);
				player.getInventory().addItem(32659, 1);
				player.sendMessage("You've attuned (upgraded) your Crystal staff!");
				return;
			}
			/** Arcane blood necklace **/
			if (itemUsed.getId() == 32692 && usedWith.getId() == 18335
					|| usedWith.getId() == 32692 && itemUsed.getId() == 18335) {
				if (player.getSkills().getLevel(Skills.CRAFTING) < 80) {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You need a Crafting level of 80 to do this.");
					return;
				}
				player.getSkills().addXp(Skills.CRAFTING, 200);
				player.getInventory().deleteItem(itemUsed);
				player.getInventory().deleteItem(usedWith);
				player.getInventory().addItem(new Item(32694));
				player.addItemsMade();
				player.sendMessage("You attach the shard onto the amulet; " + "items crafted: " + Colors.red
						+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
				return;
			}
			/** Blood amulet of fury **/
			if (itemUsed.getId() == 32692 && usedWith.getId() == 6585
					|| usedWith.getId() == 32692 && itemUsed.getId() == 6585) {
				if (player.getSkills().getLevel(Skills.CRAFTING) < 80) {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You need a Crafting level of 80 to do this.");
					return;
				}
				player.getSkills().addXp(Skills.CRAFTING, 200);
				player.getInventory().deleteItem(itemUsed);
				player.getInventory().deleteItem(usedWith);
				player.getInventory().addItem(new Item(32703));
				player.addItemsMade();
				player.sendMessage("You attach the shard onto the amulet; " + "items crafted: " + Colors.red
						+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
				return;

			}

			/**
			 * Air Battlestaff if (itemUsed.getId() == 573 && usedWith.getId() == 1391 ||
			 * usedWith.getId() == 573 && itemUsed.getId() == 1391) { if
			 * (player.getSkills().getLevel(Skills.CRAFTING) < 66) {
			 * player.getDialogueManager().startDialogue("SimpleMessage", "You need a
			 * Crafting level of 66 to do this."); return; }
			 * player.getSkills().addXp(Skills.CRAFTING, 137.5);
			 * player.getInventory().deleteItem(itemUsed);
			 * player.getInventory().deleteItem(usedWith); player.getInventory().addItem(new
			 * Item(1397)); player.addItemsMade(); player.sendMessage("You attach the orb to
			 * the battlestaff; " + "items crafted: " + Colors.red +
			 * Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true); return; }
			 * /** Water Battlestaff if (itemUsed.getId() == 571 && usedWith.getId() == 1391
			 * || usedWith.getId() == 571 && itemUsed.getId() == 1391) { if
			 * (player.getSkills().getLevel(Skills.CRAFTING) < 54) {
			 * player.getDialogueManager().startDialogue("SimpleMessage", "You need a
			 * Crafting level of 54 to do this."); return; }
			 * player.getSkills().addXp(Skills.CRAFTING, 100);
			 * player.getInventory().deleteItem(itemUsed);
			 * player.getInventory().deleteItem(usedWith); player.getInventory().addItem(new
			 * Item(1395)); player.addItemsMade(); player.sendMessage("You attach the orb to
			 * the battlestaff; " + "items crafted: " + Colors.red +
			 * Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true); return; }
			 * /** Earth Battlestaff if (itemUsed.getId() == 575 && usedWith.getId() == 1391
			 * || usedWith.getId() == 575 && itemUsed.getId() == 1391) { if
			 * (player.getSkills().getLevel(Skills.CRAFTING) < 58) {
			 * player.getDialogueManager().startDialogue("SimpleMessage", "You need a
			 * Crafting level of 58 to do this."); return; }
			 * player.getSkills().addXp(Skills.CRAFTING, 112.5);
			 * player.getInventory().deleteItem(itemUsed);
			 * player.getInventory().deleteItem(usedWith); player.getInventory().addItem(new
			 * Item(1399)); player.addItemsMade(); player.sendMessage("You attach the orb to
			 * the battlestaff; " + "items crafted: " + Colors.red +
			 * Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true); return; }
			 * /** Fire Battlestaff if (itemUsed.getId() == 569 && usedWith.getId() == 1391
			 * || usedWith.getId() == 569 && itemUsed.getId() == 1391) { if
			 * (player.getSkills().getLevel(Skills.CRAFTING) < 62) {
			 * player.getDialogueManager().startDialogue("SimpleMessage", "You need a
			 * Crafting level of 62 to do this."); return; }
			 * player.getSkills().addXp(Skills.CRAFTING, 125);
			 * player.getInventory().deleteItem(itemUsed);
			 * player.getInventory().deleteItem(usedWith); player.getInventory().addItem(new
			 * Item(1393)); player.addItemsMade(); player.sendMessage("You attach the orb to
			 * the battlestaff; " + "items crafted: " + Colors.red +
			 * Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true); return; }
			 **/
			/** Uncut hydrix **/
			if (itemUsed.getId() == 6573 && usedWith.getId() == 31851
					|| usedWith.getId() == 6573 && itemUsed.getId() == 31851) {
				if (player.getSkills().getLevel(Skills.CRAFTING) < 79) {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You need a Crafting level of 79 to do this.");
					return;
				}
				player.getSkills().addXp(Skills.CRAFTING, 19.75);
				player.getInventory().deleteItem(itemUsed);
				player.getInventory().deleteItem(usedWith);
				player.getInventory().addItem(new Item(31853));
				player.addItemsMade();
				player.sendMessage("You smash the onyx onto the hydrix; " + "items crafted: " + Colors.red
						+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
				return;
			}
			if (player.getEquipment().getWeaponId() == -1 && itemUsed.getId() == 22448 && usedWith.getId() == 22498) {
				if (player.getInventory().containsItem(22448, 3000)
						&& (player.getInventory().containsItem(554, 15000))) {
					player.setNextAnimation(new Animation(15434));
					player.setNextGraphics(new Graphics(2032));
					player.getInventory().deleteItem(554, 15000);
					player.getInventory().deleteItem(22448, 3000);
					player.getInventory().deleteItem(22498, 1);
					player.getInventory().addItem(22494, 1);
					return;
				}
			}
			if (itemUsed.getId() == 985 && usedWith.getId() == 987
					|| itemUsed.getId() == 987 && usedWith.getId() == 985) {
				CrystalChest.makeKey(player);
				return;
			}
			if (itemUsed.getName().startsWith("Royal") && usedWith.getName().startsWith("Royal")) {
				if (player.getInventory().containsItem(24344, 1) && player.getInventory().containsItem(24346, 1)
						&& player.getInventory().containsItem(24342, 1)
						&& player.getInventory().containsItem(24340, 1)) {
					if (player.getSkills().getLevel(Skills.CRAFTING) >= 70) {
						player.getInventory().deleteItem(24340, 1);
						player.getInventory().deleteItem(24342, 1);
						player.getInventory().deleteItem(24344, 1);
						player.getInventory().deleteItem(24346, 1);
						player.getInventory().addItem(24337, 1);
					} else
						player.getDialogueManager().startDialogue("SimpleMessage",
								"You need at least a crafting level of 70 to make a royal crossbow!");
				} else
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You'll need these Royal items: torsion sight, spring, frame and bolt stabiliser in order to make a Royal Crossbow!");
				return;
			}
			int string = 1759; // Stringing necklaces
			if ((itemUsed.getId() == string || usedWith.getId() == string)
					&& (itemUsed.getId() == 1673 || usedWith.getId() == 1673)) {// Gold
																				// amulet
				if (player.getSkills().getLevel(Skills.CRAFTING) >= 8) {
					player.addItemsMade();
					player.sendMessage("You've successfully made jewellery; " + "items crafted: " + Colors.red
							+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
					player.getInventory().deleteItem(string, 1);
					player.getInventory().deleteItem(1673, 1);
					player.getInventory().addItem(1692, 1);
				} else
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You will need a crafting level of 8 to string this amulet!");
				return;
			}
			if ((itemUsed.getId() == string || usedWith.getId() == string)
					&& (itemUsed.getId() == 1675 || usedWith.getId() == 1675)) {// Sapphire
																				// amulet
				if (player.getSkills().getLevel(Skills.CRAFTING) >= 24) {
					player.addItemsMade();
					player.sendMessage("You've successfully made jewellery; " + "items crafted: " + Colors.red
							+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
					player.getInventory().deleteItem(string, 1);
					player.getInventory().deleteItem(1675, 1);
					player.getInventory().addItem(1694, 1);
				} else
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You will need a crafting level of 24 to string this amulet!");
				return;
			}
			if ((itemUsed.getId() == string || usedWith.getId() == string)
					&& (itemUsed.getId() == 1677 || usedWith.getId() == 1677)) {// Emerald
																				// amulet
				if (player.getSkills().getLevel(Skills.CRAFTING) >= 31) {
					player.addItemsMade();
					player.sendMessage("You've successfully made jewellery; " + "items crafted: " + Colors.red
							+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
					player.getInventory().deleteItem(string, 1);
					player.getInventory().deleteItem(1677, 1);
					player.getInventory().addItem(1696, 1);
				} else
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You will need a crafting level of 31 to string this amulet!");
				return;
			}
			if ((itemUsed.getId() == string || usedWith.getId() == string)
					&& (itemUsed.getId() == 1679 || usedWith.getId() == 1679)) {// Ruby
																				// amulet
				if (player.getSkills().getLevel(Skills.CRAFTING) >= 50) {
					player.addItemsMade();
					player.sendMessage("You've successfully made jewellery; " + "items crafted: " + Colors.red
							+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
					player.getInventory().deleteItem(string, 1);
					player.getInventory().deleteItem(1679, 1);
					player.getInventory().addItem(1698, 1);
				} else
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You will need a crafting level of 50 to string this amulet!");
				return;
			}
			if ((itemUsed.getId() == string || usedWith.getId() == string)
					&& (itemUsed.getId() == 1681 || usedWith.getId() == 1681)) {// Diamond
																				// amulet
				if (player.getSkills().getLevel(Skills.CRAFTING) >= 70) {
					player.addItemsMade();
					player.sendMessage("You've successfully made jewellery; " + "items crafted: " + Colors.red
							+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
					player.getInventory().deleteItem(string, 1);
					player.getInventory().deleteItem(1681, 1);
					player.getInventory().addItem(1700, 1);
				} else
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You will need a crafting level of 70 to string this amulet!");
				return;
			}
			if ((itemUsed.getId() == string || usedWith.getId() == string)
					&& (itemUsed.getId() == 1683 || usedWith.getId() == 1683)) {// Dragonstone
																				// amulet
				if (player.getSkills().getLevel(Skills.CRAFTING) >= 80) {
					player.addItemsMade();
					player.sendMessage("You've successfully made jewellery; " + "items crafted: " + Colors.red
							+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
					player.getInventory().deleteItem(string, 1);
					player.getInventory().deleteItem(1683, 1);
					player.getInventory().addItem(1702, 1);
				} else
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You will need a crafting level of 80 to string this amulet!");
				return;
			}
			if ((itemUsed.getId() == string || usedWith.getId() == string)
					&& (itemUsed.getId() == 6579 || usedWith.getId() == 6579)) {// Onyx
																				// amulet
				if (player.getSkills().getLevel(Skills.CRAFTING) >= 90) {
					player.addItemsMade();
					player.sendMessage("You've successfully made jewellery; " + "items crafted: " + Colors.red
							+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
					player.getInventory().deleteItem(string, 1);
					player.getInventory().deleteItem(6579, 1);
					player.getInventory().addItem(6581, 1);
				} else
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You will need a crafting level of 90 to string this amulet!");
				return;
			}
			if ((itemUsed.getId() == string || usedWith.getId() == string)
					&& (itemUsed.getId() == 31853 || usedWith.getId() == 31853)) {// Hydrix
																					// ring
				if (player.getSkills().getLevel(Skills.CRAFTING) >= 79) {
					player.addItemsMade();
					player.sendMessage("You've successfully made jewellery; " + "items crafted: " + Colors.red
							+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
					player.getInventory().deleteItem(string, 1);
					player.getInventory().deleteItem(31853, 1);
					player.getInventory().addItem(6581, 1);
				} else
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You will need a crafting level of 90 to string this amulet!");
				return;
			}
			if (itemUsed.getId() == 985 && usedWith.getId() == 987
					|| itemUsed.getId() == 987 && usedWith.getId() == 985) {
				CrystalChest.makeKey(player);
				return;
			}
			if (Slayer.createSlayerHelmet(player, itemUsed.getId(), usedWith.getId()))
				return;
			// guthix's book of balance
			if (itemUsed.getId() == 3843 && usedWith.getId() == 3835
					|| itemUsed.getId() == 3843 && usedWith.getId() == 3836
					|| itemUsed.getId() == 3843 && usedWith.getId() == 3837
					|| itemUsed.getId() == 3843 && usedWith.getId() == 3838
					|| itemUsed.getId() == 3835 && usedWith.getId() == 3843
					|| itemUsed.getId() == 3836 && usedWith.getId() == 3843
					|| itemUsed.getId() == 3837 && usedWith.getId() == 3843
					|| itemUsed.getId() == 3838 && usedWith.getId() == 3843) {
				if (!player.getInventory().containsItem(3835, 1) || !player.getInventory().containsItem(3836, 1)
						|| !player.getInventory().containsItem(3837, 1)
						|| !player.getInventory().containsItem(3838, 1)) {
					player.sendMessage("You will need all 4 pages in order to complete your book!");
					return;
				}
				player.sendMessage("You carefully put all 4 pages back in the book.");
				player.getInventory().deleteItem(3843, 1);
				player.getInventory().deleteItem(3835, 1);
				player.getInventory().deleteItem(3836, 1);
				player.getInventory().deleteItem(3837, 1);
				player.getInventory().deleteItem(3838, 1);
				player.getInventory().addItem(3844, 1);
				return;
			} // zamorak's unholy book
			if (itemUsed.getId() == 3841 && usedWith.getId() == 3831
					|| itemUsed.getId() == 3841 && usedWith.getId() == 3832
					|| itemUsed.getId() == 3841 && usedWith.getId() == 3833
					|| itemUsed.getId() == 3841 && usedWith.getId() == 3834
					|| itemUsed.getId() == 3831 && usedWith.getId() == 3841
					|| itemUsed.getId() == 3832 && usedWith.getId() == 3841
					|| itemUsed.getId() == 3833 && usedWith.getId() == 3841
					|| itemUsed.getId() == 3834 && usedWith.getId() == 3841) {
				if (!player.getInventory().containsItem(3831, 1) || !player.getInventory().containsItem(3832, 1)
						|| !player.getInventory().containsItem(3833, 1)
						|| !player.getInventory().containsItem(3834, 1)) {
					player.sendMessage("You will need all 4 pages in order to complete your book!");
					return;
				}
				player.sendMessage("You carefully put all 4 pages back in the book.");
				player.getInventory().deleteItem(3841, 1);
				player.getInventory().deleteItem(3831, 1);
				player.getInventory().deleteItem(3832, 1);
				player.getInventory().deleteItem(3833, 1);
				player.getInventory().deleteItem(3834, 1);
				player.getInventory().addItem(3842, 1);
				return;
			}
			// saradomin's holy book
			if (itemUsed.getId() == 3839 && usedWith.getId() == 3827
					|| itemUsed.getId() == 3839 && usedWith.getId() == 3828
					|| itemUsed.getId() == 3839 && usedWith.getId() == 3829
					|| itemUsed.getId() == 3839 && usedWith.getId() == 3830
					|| itemUsed.getId() == 3827 && usedWith.getId() == 3839
					|| itemUsed.getId() == 3828 && usedWith.getId() == 3839
					|| itemUsed.getId() == 3829 && usedWith.getId() == 3839
					|| itemUsed.getId() == 3830 && usedWith.getId() == 3839) {
				if (!player.getInventory().containsItem(3827, 1) || !player.getInventory().containsItem(3828, 1)
						|| !player.getInventory().containsItem(3829, 1)
						|| !player.getInventory().containsItem(3830, 1)) {
					player.sendMessage("You will need all 4 pages in order to complete your book!");
					return;
				}
				player.sendMessage("You carefully put all 4 pages back in the book.");
				player.getInventory().deleteItem(3839, 1);
				player.getInventory().deleteItem(3827, 1);
				player.getInventory().deleteItem(3828, 1);
				player.getInventory().deleteItem(3829, 1);
				player.getInventory().deleteItem(3830, 1);
				player.getInventory().addItem(3840, 1);
				return;
			}
			// ancient book
			if (itemUsed.getId() == 19616 && usedWith.getId() == 19608
					|| itemUsed.getId() == 19616 && usedWith.getId() == 19609
					|| itemUsed.getId() == 19616 && usedWith.getId() == 19610
					|| itemUsed.getId() == 19616 && usedWith.getId() == 19611
					|| itemUsed.getId() == 19608 && usedWith.getId() == 19616
					|| itemUsed.getId() == 19609 && usedWith.getId() == 19616
					|| itemUsed.getId() == 19610 && usedWith.getId() == 19616
					|| itemUsed.getId() == 19611 && usedWith.getId() == 19616) {
				if (!player.getInventory().containsItem(19608, 1) || !player.getInventory().containsItem(19609, 1)
						|| !player.getInventory().containsItem(19610, 1)
						|| !player.getInventory().containsItem(19611, 1)) {
					player.sendMessage("You will need all 4 pages in order to complete your book!");
					return;
				}
				player.sendMessage("You carefully put all 4 pages back in the book.");
				player.getInventory().deleteItem(19616, 1);
				player.getInventory().deleteItem(19608, 1);
				player.getInventory().deleteItem(19609, 1);
				player.getInventory().deleteItem(19610, 1);
				player.getInventory().deleteItem(19611, 1);
				player.getInventory().addItem(19617, 1);
				return;
			}
			// armadyl's book of law
			if (itemUsed.getId() == 19614 && usedWith.getId() == 19604
					|| itemUsed.getId() == 19614 && usedWith.getId() == 19605
					|| itemUsed.getId() == 19614 && usedWith.getId() == 19606
					|| itemUsed.getId() == 19614 && usedWith.getId() == 19607
					|| itemUsed.getId() == 19604 && usedWith.getId() == 19614
					|| itemUsed.getId() == 19605 && usedWith.getId() == 19614
					|| itemUsed.getId() == 19606 && usedWith.getId() == 19614
					|| itemUsed.getId() == 19607 && usedWith.getId() == 19614) {
				if (!player.getInventory().containsItem(19604, 1) || !player.getInventory().containsItem(19605, 1)
						|| !player.getInventory().containsItem(19606, 1)
						|| !player.getInventory().containsItem(19607, 1)) {
					player.sendMessage("You will need all 4 pages in order to complete your book!");
					return;
				}
				player.sendMessage("You carefully put all 4 pages back in the book.");
				player.getInventory().deleteItem(19614, 1);
				player.getInventory().deleteItem(19604, 1);
				player.getInventory().deleteItem(19605, 1);
				player.getInventory().deleteItem(19606, 1);
				player.getInventory().deleteItem(19607, 1);
				player.getInventory().addItem(19615, 1);
				return;
			}
			// bandos' book of war
			if (itemUsed.getId() == 19612 && usedWith.getId() == 19600
					|| itemUsed.getId() == 19612 && usedWith.getId() == 19601
					|| itemUsed.getId() == 19612 && usedWith.getId() == 19602
					|| itemUsed.getId() == 19612 && usedWith.getId() == 19603
					|| itemUsed.getId() == 19600 && usedWith.getId() == 19612
					|| itemUsed.getId() == 19601 && usedWith.getId() == 19612
					|| itemUsed.getId() == 19602 && usedWith.getId() == 19612
					|| itemUsed.getId() == 19603 && usedWith.getId() == 19612) {
				if (!player.getInventory().containsItem(19600, 1) || !player.getInventory().containsItem(19601, 1)
						|| !player.getInventory().containsItem(19602, 1)
						|| !player.getInventory().containsItem(19603, 1)) {
					player.sendMessage("You will need all 4 pages in order to complete your book!");
					return;
				}
				player.sendMessage("You carefully put all 4 pages back in the book.");
				player.getInventory().deleteItem(19612, 1);
				player.getInventory().deleteItem(19600, 1);
				player.getInventory().deleteItem(19601, 1);
				player.getInventory().deleteItem(19602, 1);
				player.getInventory().deleteItem(19603, 1);
				player.getInventory().addItem(19613, 1);
				return;
			}
			if (itemUsed.getId() == 1765 && usedWith.getId() == 1767
					|| usedWith.getId() == 1765 && itemUsed.getId() == 1767) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendItemDialogue(1771, 1, "Are you sure you want to combine these two dyes?");
						stage = 0;
					}

					@Override
					public void run(int interfaceId, int componentId) {
						switch (stage) {
						case 0:
							sendOptionsDialogue("Choose an option", "Yes", "No");
							stage = 1;
							break;
						case 1:
							finish();
							switch (componentId) {
							case OPTION_1:
								player.getInventory().deleteItem(1765, 1);
								player.getInventory().deleteItem(1767, 1);
								player.getInventory().addItem(1771, 1);
								player.sendMessage(Colors.green + "You combine the two dyes into green!", true);
								break;
							case OPTION_2:
								break;
							}
							break;
						}
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}

				});

			}
			// dragonbone upgrade kits
			if (itemUsed.getId() == 24352 && usedWith.getId() == 6918
					|| itemUsed.getId() == 6918 && usedWith.getId() == 24352) {
				player.sendMessage("You attach the upgrade kit on the equipment piece!");
				player.getInventory().deleteItem(24352, 1);
				player.getInventory().deleteItem(6918, 1);
				player.getInventory().addItem(24354, 1);
				return;
			} // dragonbone upgrade kits
			if (itemUsed.getId() == 24352 && usedWith.getId() == 6916
					|| itemUsed.getId() == 6916 && usedWith.getId() == 24352) {
				player.sendMessage("You attach the upgrade kit on the equipment piece!");
				player.getInventory().deleteItem(24352, 1);
				player.getInventory().deleteItem(6916, 1);
				player.getInventory().addItem(24355, 1);
				return;
			} // dragonbone upgrade kits
			if (itemUsed.getId() == 24352 && usedWith.getId() == 6924
					|| itemUsed.getId() == 6924 && usedWith.getId() == 24352) {
				player.sendMessage("You attach the upgrade kit on the equipment piece!");
				player.getInventory().deleteItem(24352, 1);
				player.getInventory().deleteItem(6924, 1);
				player.getInventory().addItem(24356, 1);
				return;
			} // dragonbone upgrade kits
			if (itemUsed.getId() == 24352 && usedWith.getId() == 6920
					|| itemUsed.getId() == 6920 && usedWith.getId() == 24352) {
				player.sendMessage("You attach the upgrade kit on the equipment piece!");
				player.getInventory().deleteItem(24352, 1);
				player.getInventory().deleteItem(6920, 1);
				player.getInventory().addItem(24358, 1);
				return;
			} // dragonbone upgrade kits
			if (itemUsed.getId() == 24352 && usedWith.getId() == 6922
					|| itemUsed.getId() == 6922 && usedWith.getId() == 24352) {
				player.sendMessage("You attach the upgrade kit on the equipment piece!");
				player.getInventory().deleteItem(24352, 1);
				player.getInventory().deleteItem(6922, 1);
				player.getInventory().addItem(24357, 1);
				return;
			} // dragonbone upgrade kits
			if (itemUsed.getId() == 24352 && usedWith.getId() == 11335
					|| itemUsed.getId() == 11335 && usedWith.getId() == 24352) {
				player.sendMessage("You attach the upgrade kit on the equipment piece!");
				player.getInventory().deleteItem(24352, 1);
				player.getInventory().deleteItem(11335, 1);
				player.getInventory().addItem(24359, 1);
				return;
			} // dragonbone upgrade kits
			if (itemUsed.getId() == 24352 && usedWith.getId() == 14479
					|| itemUsed.getId() == 14479 && usedWith.getId() == 24352) {
				player.sendMessage("You attach the upgrade kit on the equipment piece!");
				player.getInventory().deleteItem(24352, 1);
				player.getInventory().deleteItem(14479, 1);
				player.getInventory().addItem(24360, 1);
				return;
			} // dragonbone upgrade kits
			if (itemUsed.getId() == 24352 && usedWith.getId() == 4087
					|| itemUsed.getId() == 4087 && usedWith.getId() == 24352) {
				player.sendMessage("You attach the upgrade kit on the equipment piece!");
				player.getInventory().deleteItem(24352, 1);
				player.getInventory().deleteItem(4087, 1);
				player.getInventory().addItem(24363, 1);
				return;
			} // dragonbone upgrade kits
			if (itemUsed.getId() == 24352 && usedWith.getId() == 4585
					|| itemUsed.getId() == 4585 && usedWith.getId() == 24352) {
				player.sendMessage("You attach the upgrade kit on the equipment piece!");
				player.getInventory().deleteItem(24352, 1);
				player.getInventory().deleteItem(4585, 1);
				player.getInventory().addItem(24364, 1);
				return;
			} // dragonbone upgrade kits
			if (itemUsed.getId() == 24352 && usedWith.getId() == 11732
					|| itemUsed.getId() == 11732 && usedWith.getId() == 24352) {
				player.sendMessage("You attach the upgrade kit on the equipment piece!");
				player.getInventory().deleteItem(24352, 1);
				player.getInventory().deleteItem(11732, 1);
				player.getInventory().addItem(24362, 1);
				return;
			} // dragonbone upgrade kits
			if (itemUsed.getId() == 24352 && usedWith.getId() == 7461
					|| itemUsed.getId() == 7461 && usedWith.getId() == 24352) {
				player.sendMessage("You attach the upgrade kit on the equipment piece!");
				player.getInventory().deleteItem(24352, 1);
				player.getInventory().deleteItem(7461, 1);
				player.getInventory().addItem(24361, 1);
				return;
			} // godsword shard 1+2 = godsword shards
			if (itemUsed.getId() == 11712 && usedWith.getId() == 11710
					|| itemUsed.getId() == 11710 && usedWith.getId() == 11712) {
				if (player.getSkills().getLevel(Skills.SMITHING) >= 80) {
					player.sendMessage("You use your smithing knowledge to attach the 2 shards together!");
					player.getInventory().deleteItem(11712, 1);
					player.getInventory().deleteItem(11710, 1);
					player.getInventory().addItem(11686, 1);
					player.getSkills().addXp(Skills.SMITHING, 100);
				} else
					player.sendMessage("You need a smithing level of 80 to do this!");
				return;
			} // Holy elixir + spirit shield = Blessed spirit shield
			if (itemUsed.getId() == 13734 && usedWith.getId() == 13754
					|| itemUsed.getId() == 13754 && usedWith.getId() == 13734) {
				if (player.getSkills().getLevel(Skills.PRAYER) >= 85) {
					player.sendMessage(
							"You combine the holy elixir with the the spirit shield to make a Blessed spirit shield!");
					player.getInventory().deleteItem(13734, 1);
					player.getInventory().deleteItem(13754, 1);
					player.getInventory().addItem(13736, 1);
					player.getSkills().addXp(Skills.PRAYER, 1500);
				} else
					player.sendMessage("You need a Prayer level of 85 to bless the shield.");
				return;
			}
			// Blessed spirit shield + Arcane sigil = Arcane spirit shield
			if (itemUsed.getId() == 13736 && usedWith.getId() == 13746
					|| itemUsed.getId() == 13746 && usedWith.getId() == 13736) {
				if (player.getSkills().getLevel(Skills.PRAYER) >= 90
						&& player.getSkills().getLevel(Skills.SMITHING) >= 85) {
					player.sendMessage("You combine the sigil with the spirit shield to make an Arcane spirit shield!");
					player.getInventory().deleteItem(13736, 1);
					player.getInventory().deleteItem(13746, 1);
					player.getInventory().addItem(13738, 1);
					player.getSkills().addXp(Skills.SMITHING, 1800);
				} else
					player.sendMessage("You need a Smithing level of 85 and a Prayer level of 90 to do this.");
				return;
			}
			// Blessed spirit shield + Divine sigil = Divine spirit shield
			if (itemUsed.getId() == 13736 && usedWith.getId() == 13748
					|| itemUsed.getId() == 13748 && usedWith.getId() == 13736) {
				if (player.getSkills().getLevel(Skills.PRAYER) >= 90
						&& player.getSkills().getLevel(Skills.SMITHING) >= 85) {
					player.sendMessage("You combine the sigil with the spirit shield to make a Divine spirit shield!");
					player.getInventory().deleteItem(13736, 1);
					player.getInventory().deleteItem(13748, 1);
					player.getInventory().addItem(13740, 1);
					player.getSkills().addXp(Skills.SMITHING, 1800);
				} else
					player.sendMessage("You need a Smithing level of 85 and a Prayer level of 90 to do this.");
				return;
			}
			// Blessed spirit shield + Elysian sigil = Elysian spirit shield
			if (itemUsed.getId() == 13736 && usedWith.getId() == 13750
					|| itemUsed.getId() == 13750 && usedWith.getId() == 13736) {
				if (player.getSkills().getLevel(Skills.PRAYER) >= 90
						&& player.getSkills().getLevel(Skills.SMITHING) >= 85) {
					player.sendMessage(
							"You combine the sigil with the spirit shield to make an Elysian spirit shield!");
					player.getInventory().deleteItem(13736, 1);
					player.getInventory().deleteItem(13750, 1);
					player.getInventory().addItem(13742, 1);
					player.getSkills().addXp(Skills.SMITHING, 1800);
				} else
					player.sendMessage("You need a Smithing level of 85 and a Prayer level of 90 to do this.");
				return;
			}
			// Blessed spirit shield + Spectral sigil = Spectral spirit shield
			if (itemUsed.getId() == 13736 && usedWith.getId() == 13752
					|| itemUsed.getId() == 13752 && usedWith.getId() == 13736) {
				if (player.getSkills().getLevel(Skills.PRAYER) >= 90
						&& player.getSkills().getLevel(Skills.SMITHING) >= 85) {
					player.sendMessage(
							"You combine the sigil with the spirit shield to make a Spectral spirit shield!");
					player.getInventory().deleteItem(13736, 1);
					player.getInventory().deleteItem(13752, 1);
					player.getInventory().addItem(13744, 1);
					player.getSkills().addXp(Skills.SMITHING, 1800);
				} else
					player.sendMessage("You need a Smithing level of 85 and a Prayer level of 90 to do this.");
				return;
			} // whip vine
			if (itemUsed.getId() == 21369 && usedWith.getId() == 4151
					|| itemUsed.getId() == 4151 && usedWith.getId() == 21369) {
				player.getInventory().deleteItem(21369, 1);
				player.getInventory().deleteItem(4151, 1);
				player.getInventory().addItem(21371, 1);
				player.sendMessage("You attach the whip vine onto your abyssal whip!");
				return;
			} // Dragon full helm (or)
			if (itemUsed.getId() == 19346 && usedWith.getId() == 11335
					|| itemUsed.getId() == 11335 && usedWith.getId() == 19346) {
				player.getInventory().deleteItem(11335, 1);
				player.getInventory().deleteItem(19346, 1);
				player.getInventory().addItem(19336, 1);
				player.sendMessage("You upgrade your Dragon full helm to Dragon full helm (or) with the ornament kit!");
				return;
			}
			// Dragon platebody (or)
			if (itemUsed.getId() == 19350 && usedWith.getId() == 14479
					|| itemUsed.getId() == 14479 && usedWith.getId() == 19350) {
				player.getInventory().deleteItem(14479, 1);
				player.getInventory().deleteItem(19350, 1);
				player.getInventory().addItem(19337, 1);
				player.sendMessage("You upgrade your Dragon platebody to Dragon platebody (or) with the ornament kit!");
				return;
			}
			// Dragon platelegs (or)
			if (itemUsed.getId() == 19348 && usedWith.getId() == 4087
					|| itemUsed.getId() == 4087 && usedWith.getId() == 19348) {
				player.getInventory().deleteItem(4087, 1);
				player.getInventory().deleteItem(19348, 1);
				player.getInventory().addItem(19338, 1);
				player.sendMessage("You upgrade your Dragon platelegs to Dragon platelegs (or) with the ornament kit!");
				return;
			}
			// Dragon plateskirt (or)
			if (itemUsed.getId() == 19348 && usedWith.getId() == 4585
					|| itemUsed.getId() == 4585 && usedWith.getId() == 19348) {
				player.getInventory().deleteItem(4585, 1);
				player.getInventory().deleteItem(19348, 1);
				player.getInventory().addItem(19339, 1);
				player.sendMessage(
						"You upgrade your Dragon plateskirt to Dragon plateskirt (or) with the ornament kit!");
				return;
			}
			// Dragon square shield (or)
			if (itemUsed.getId() == 19352 && usedWith.getId() == 1187
					|| itemUsed.getId() == 1187 && usedWith.getId() == 19352) {
				player.getInventory().deleteItem(1187, 1);
				player.getInventory().deleteItem(19352, 1);
				player.getInventory().addItem(19340, 1);
				player.sendMessage(
						"You upgrade your Dragon square shield to Dragon square shield (or) with the ornament kit!");
				return;
			}
			// Dragon kiteshield (or)
			if (itemUsed.getId() == 24365 && usedWith.getId() == 25312
					|| itemUsed.getId() == 25312 && usedWith.getId() == 24365) {
				player.getInventory().deleteItem(itemUsed);
				player.getInventory().deleteItem(usedWith);
				player.getInventory().addItem(25320, 1);
				player.sendMessage(
						"You upgrade your Dragon kiteshield to Dragon kiteshield (or) with the ornament kit!");
				return;
			}
			// Dragon kiteshield (sp)
			if (itemUsed.getId() == 24365 && usedWith.getId() == 25314
					|| itemUsed.getId() == 25314 && usedWith.getId() == 24365) {
				player.getInventory().deleteItem(itemUsed);
				player.getInventory().deleteItem(usedWith);
				player.getInventory().addItem(25321, 1);
				player.sendMessage(
						"You upgrade your Dragon kiteshield to Dragon kiteshield (sp) with the ornament kit!");
				return;
			}

			// Dragon full helm (sp)
			if (itemUsed.getId() == 19354 && usedWith.getId() == 11335
					|| itemUsed.getId() == 11335 && usedWith.getId() == 19354) {
				player.getInventory().deleteItem(11335, 1);
				player.getInventory().deleteItem(19354, 1);
				player.getInventory().addItem(19341, 1);
				player.sendMessage("You upgrade your Dragon full helm to Dragon full helm (sp) with the ornament kit!");
				return;
			}
			// Dragon platebody (sp)
			if (itemUsed.getId() == 19358 && usedWith.getId() == 14479
					|| itemUsed.getId() == 14479 && usedWith.getId() == 19358) {
				player.getInventory().deleteItem(14479, 1);
				player.getInventory().deleteItem(19358, 1);
				player.getInventory().addItem(19342, 1);
				player.sendMessage("You upgrade your Dragon platebody to Dragon platebody (sp) with the ornament kit!");
				return;
			}
			// Dragon platelegs (sp)
			if (itemUsed.getId() == 19356 && usedWith.getId() == 4087
					|| itemUsed.getId() == 4087 && usedWith.getId() == 19356) {
				player.getInventory().deleteItem(4087, 1);
				player.getInventory().deleteItem(19356, 1);
				player.getInventory().addItem(19343, 1);
				player.sendMessage("You upgrade your Dragon platelegs to Dragon platelegs (sp) with the ornament kit!");
				return;
			}
			// Dragon plateskirt (sp)
			if (itemUsed.getId() == 19356 && usedWith.getId() == 4585
					|| itemUsed.getId() == 4585 && usedWith.getId() == 19356) {
				player.getInventory().deleteItem(4585, 1);
				player.getInventory().deleteItem(19356, 1);
				player.getInventory().addItem(19344, 1);
				player.sendMessage(
						"You upgrade your Dragon plateskirt to Dragon plateskirt (sp) with the ornament kit!");
				return;
			}
			// Dragon square shield (sp)
			if (itemUsed.getId() == 19360 && usedWith.getId() == 1187
					|| itemUsed.getId() == 1187 && usedWith.getId() == 19360) {
				player.getInventory().deleteItem(1187, 1);
				player.getInventory().deleteItem(19360, 1);
				player.getInventory().addItem(19345, 1);
				player.sendMessage(
						"You upgrade your Dragon square shield to Dragon square shield (sp) with the ornament kit!");
				return;
			}
			// godsword shards+shard3 = blade
			if (itemUsed.getId() == 11686 && usedWith.getId() == 11714
					|| itemUsed.getId() == 11714 && usedWith.getId() == 11686) {
				if (player.getSkills().getLevel(Skills.SMITHING) >= 80) {
					player.sendMessage("You use your smithing knowledge to attach the last shard and make a blade!");
					player.getInventory().deleteItem(11714, 1);
					player.getInventory().deleteItem(11686, 1);
					player.getInventory().addItem(11690, 1);
					player.getSkills().addXp(Skills.SMITHING, 100);
				} else
					player.sendMessage("You need a smithing level of 80 to do this!");
				return;
			} // godsword shard 2+3 = godsword shards
			if (itemUsed.getId() == 11712 && usedWith.getId() == 11714
					|| itemUsed.getId() == 11714 && usedWith.getId() == 11712) {
				if (player.getSkills().getLevel(Skills.SMITHING) >= 80) {
					player.sendMessage("You use your smithing knowledge to attach the 2 shards together!");
					player.getInventory().deleteItem(11712, 1);
					player.getInventory().deleteItem(11714, 1);
					player.getInventory().addItem(11688, 1);
					player.getSkills().addXp(Skills.SMITHING, 100);
				} else
					player.sendMessage("You need a smithing level of 80 to do this!");
				return;
			} // godsword shards+shard1 = blade
			if (itemUsed.getId() == 11688 && usedWith.getId() == 11710
					|| itemUsed.getId() == 11710 && usedWith.getId() == 11688) {
				if (player.getSkills().getLevel(Skills.SMITHING) >= 80) {
					player.sendMessage("You use your smithing knowledge to attach the last shard and make a blade!");
					player.getInventory().deleteItem(11710, 1);
					player.getInventory().deleteItem(11688, 1);
					player.getInventory().addItem(11690, 1);
					player.getSkills().addXp(Skills.SMITHING, 100);
				} else
					player.sendMessage("You need a smithing level of 80 to do this!");
				return;
			} // godsword shard 1+3 = godsword shards
			if (itemUsed.getId() == 11710 && usedWith.getId() == 11714
					|| itemUsed.getId() == 11714 && usedWith.getId() == 11710) {
				if (player.getSkills().getLevel(Skills.SMITHING) >= 80) {
					player.sendMessage("You use your smithing knowledge to attach the 2 shards together!");
					player.getInventory().deleteItem(11710, 1);
					player.getInventory().deleteItem(11714, 1);
					player.getInventory().addItem(11692, 1);
					player.getSkills().addXp(Skills.SMITHING, 100);
				} else
					player.sendMessage("You need a smithing level of 80 to do this!");
				return;
			} // godsword shards+shard2 = blade
			if (itemUsed.getId() == 11692 && usedWith.getId() == 11712
					|| itemUsed.getId() == 11712 && usedWith.getId() == 11692) {
				if (player.getSkills().getLevel(Skills.SMITHING) >= 80) {
					player.sendMessage("You use your smithing knowledge to attach the last shard and make a blade!");
					player.getInventory().deleteItem(11712, 1);
					player.getInventory().deleteItem(11692, 1);
					player.getInventory().addItem(11690, 1);
					player.getSkills().addXp(Skills.SMITHING, 100);
				} else
					player.sendMessage("You need a smithing level of 80 to do this!");
				return;
			} // godsword blade+bandos hilt = godsword
			if (itemUsed.getId() == 11690 && usedWith.getId() == 11704
					|| itemUsed.getId() == 11704 && usedWith.getId() == 11690) {
				player.getInventory().deleteItem(11704, 1);
				player.getInventory().deleteItem(11690, 1);
				player.getInventory().addItem(11696, 1);
				return;
			} // godsword blade+saradomin hilt = godsword
			if (itemUsed.getId() == 11690 && usedWith.getId() == 11706
					|| itemUsed.getId() == 11706 && usedWith.getId() == 11690) {
				player.getInventory().deleteItem(11706, 1);
				player.getInventory().deleteItem(11690, 1);
				player.getInventory().addItem(11698, 1);
				return;
			} // godsword blade+zamorak hilt = godsword
			if (itemUsed.getId() == 11690 && usedWith.getId() == 11708
					|| itemUsed.getId() == 11708 && usedWith.getId() == 11690) {
				player.getInventory().deleteItem(11708, 1);
				player.getInventory().deleteItem(11690, 1);
				player.getInventory().addItem(11700, 1);
				return;
			} // godsword blade+armadyl hilt = godsword
			if (itemUsed.getId() == 11690 && usedWith.getId() == 11702
					|| itemUsed.getId() == 11702 && usedWith.getId() == 11690) {
				player.getInventory().deleteItem(11702, 1);
				player.getInventory().deleteItem(11690, 1);
				player.getInventory().addItem(11694, 1);
				return;
			}
			// fury ornament
			if (itemUsed.getId() == 19333 && usedWith.getId() == 6585
					|| itemUsed.getId() == 6585 && usedWith.getId() == 19333) {
				player.getInventory().deleteItem(6585, 1);
				player.getInventory().deleteItem(19333, 1);
				player.getInventory().addItem(19335, 1);
				player.sendMessage("You upgrade your fury with an ornament kit!");
				return;
			}
			if (itemUsed.getId() == 22448 && usedWith.getId() == 22498) {
				if (!player.getInventory().containsItem(22448, 3000)
						&& (player.getInventory().containsItem(554, 15000))) {
					player.sendMessage("<col=B00000>You need 3000 Polypore spore's to make a Polypore Staff!");
					return;
				}
			}
			if (itemUsed.getId() == 22448 && usedWith.getId() == 22498) {
				if (!player.getInventory().containsItem(22448, 3000)
						&& (!player.getInventory().containsItem(554, 15000))) {
					player.sendMessage(
							"<col=B00000>You need 3000 Polypore spore's and 15000 fire runes to make a Polypore Staff!");
					return;
				}
			}
			if (itemUsed.getId() == 991 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 991) {
				if (Utils.random(10) != 5) {
					if (Utils.random(3) != 1) {
						player.getInventory().deleteItem(991, 1);
						player.getInventory().addItem(989, 1);
						player.sendMessage("You successfully clean off the mud from the key and get a crystal key.");
						return;
					}
					player.getInventory().deleteItem(991, 1);
					player.sendMessage("The key was too old and disintigrated during the cleaning.");
					return;
				}
				player.getInventory().deleteItem(3188, 1);
				player.getInventory().deleteItem(991, 1);
				player.getInventory().addItem(989, 1);
				player.sendMessage("You successfully clean off the mud from the key and get a crystal key.");
				return;
			}
			// removing colors from items
			if (itemUsed.getId() == 24100 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 24100) {
				player.getInventory().deleteItem(24100, 1);
				player.getInventory().addItem(6889, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 24102 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 24102) {
				player.getInventory().deleteItem(24102, 1);
				player.getInventory().addItem(6889, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 24104 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 24104) {
				player.getInventory().deleteItem(24104, 1);
				player.getInventory().addItem(6889, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 24106 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 24106) {
				player.getInventory().deleteItem(24106, 1);
				player.getInventory().addItem(6889, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22552 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22552) {
				player.getInventory().deleteItem(22552, 1);
				player.getInventory().addItem(2577, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22554 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22554) {
				player.getInventory().deleteItem(22554, 1);
				player.getInventory().addItem(2577, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22556 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22556) {
				player.getInventory().deleteItem(22556, 1);
				player.getInventory().addItem(2577, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22558 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22558) {
				player.getInventory().deleteItem(22558, 1);
				player.getInventory().addItem(2577, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 24092 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 24092) {
				player.getInventory().deleteItem(24092, 1);
				player.getInventory().addItem(4675, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 24094 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 24094) {
				player.getInventory().deleteItem(24094, 1);
				player.getInventory().addItem(4675, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 24096 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 24096) {
				player.getInventory().deleteItem(24096, 1);
				player.getInventory().addItem(4675, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 24098 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 24098) {
				player.getInventory().deleteItem(24098, 1);
				player.getInventory().addItem(4675, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 20949 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 20949) {
				player.getInventory().deleteItem(20949, 1);
				player.getInventory().addItem(2581, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 20950 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 20950) {
				player.getInventory().deleteItem(20950, 1);
				player.getInventory().addItem(2581, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 20951 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 20951) {
				player.getInventory().deleteItem(20951, 1);
				player.getInventory().addItem(2581, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22528 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22528) {
				player.getInventory().deleteItem(22528, 1);
				player.getInventory().addItem(15492, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22534 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22534) {
				player.getInventory().deleteItem(22534, 1);
				player.getInventory().addItem(15492, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22540 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22540) {
				player.getInventory().deleteItem(22540, 1);
				player.getInventory().addItem(15492, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22546 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22546) {
				player.getInventory().deleteItem(22546, 1);
				player.getInventory().addItem(15492, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 15701 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 15701) {
				player.getInventory().deleteItem(15701, 1);
				player.getInventory().addItem(11235, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 15702 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 15702) {
				player.getInventory().deleteItem(15702, 1);
				player.getInventory().addItem(11235, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 15703 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 15703) {
				player.getInventory().deleteItem(15703, 1);
				player.getInventory().addItem(11235, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 15704 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 15704) {
				player.getInventory().deleteItem(15704, 1);
				player.getInventory().addItem(11235, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 15441 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 15441) {
				player.getInventory().deleteItem(15441, 1);
				player.getInventory().addItem(4151, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 15442 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 15442) {
				player.getInventory().deleteItem(15442, 1);
				player.getInventory().addItem(4151, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 15443 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 15443) {
				player.getInventory().deleteItem(15443, 1);
				player.getInventory().addItem(4151, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 15444 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 15444) {
				player.getInventory().deleteItem(15444, 1);
				player.getInventory().addItem(4151, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22207 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22207) {
				player.getInventory().deleteItem(22207, 1);
				player.getInventory().addItem(15486, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22209 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22209) {
				player.getInventory().deleteItem(22209, 1);
				player.getInventory().addItem(15486, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22211 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22211) {
				player.getInventory().deleteItem(22211, 1);
				player.getInventory().addItem(15486, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 22213 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 22213) {
				player.getInventory().deleteItem(22213, 1);
				player.getInventory().addItem(15486, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 21372 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 21372) {
				player.getInventory().deleteItem(21372, 1);
				player.getInventory().addItem(21371, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 21373 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 21373) {
				player.getInventory().deleteItem(21373, 1);
				player.getInventory().addItem(15486, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 21374 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 21374) {
				player.getInventory().deleteItem(21374, 1);
				player.getInventory().addItem(15486, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			if (itemUsed.getId() == 21375 && usedWith.getId() == 3188
					|| itemUsed.getId() == 3188 && usedWith.getId() == 21375) {
				player.getInventory().deleteItem(21375, 1);
				player.getInventory().addItem(15486, 1);
				player.sendMessage("You use your cleaning cloth to remove the coloring from your item!");
				return;
			}
			// FARMING PLANT POTS
			// TODO proper timers for these + fruit trees
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5312
					|| itemUsed.getId() == 5312 && usedWith.getId() == 5354) { // acorn
																				// -
																				// oak
				if (player.getSkills().getLevel(Skills.FARMING) < 15) {
					player.sendMessage("You need a Farming level of at least 15 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5370, 1);
				}
				return;
			}
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5313
					|| itemUsed.getId() == 5313 && usedWith.getId() == 5354) { // willow
				if (player.getSkills().getLevel(Skills.FARMING) < 30) {
					player.sendMessage("You need a Farming level of at least 30 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1.1);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5371, 1);
				}
				return;
			}
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5314
					|| itemUsed.getId() == 5314 && usedWith.getId() == 5354) { // maple
				if (player.getSkills().getLevel(Skills.FARMING) < 45) {
					player.sendMessage("You need a Farming level of at least 45 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1.2);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5372, 1);
				}
				return;
			}
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5315
					|| itemUsed.getId() == 5315 && usedWith.getId() == 5354) { // yew
				if (player.getSkills().getLevel(Skills.FARMING) < 60) {
					player.sendMessage("You need a Farming level of at least 60 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1.3);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5373, 1);
				}
				return;
			}
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5316
					|| itemUsed.getId() == 5316 && usedWith.getId() == 5354) { // magic
				if (player.getSkills().getLevel(Skills.FARMING) < 75) {
					player.sendMessage("You need a Farming level of at least 75 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1.4);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5374, 1);
				}
				return;
			}
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5283
					|| itemUsed.getId() == 5283 && usedWith.getId() == 5354) { // apple
				if (player.getSkills().getLevel(Skills.FARMING) < 27) {
					player.sendMessage("You need a Farming level of at least 27 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1.4);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5496, 1);
				}
				return;
			}
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5284
					|| itemUsed.getId() == 5284 && usedWith.getId() == 5354) { // banana
				if (player.getSkills().getLevel(Skills.FARMING) < 33) {
					player.sendMessage("You need a Farming level of at least 33 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1.4);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5497, 1);
				}
				return;
			}
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5285
					|| itemUsed.getId() == 5285 && usedWith.getId() == 5354) { // orange
				if (player.getSkills().getLevel(Skills.FARMING) < 39) {
					player.sendMessage("You need a Farming level of at least 39 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1.4);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5498, 1);
				}
				return;
			}
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5286
					|| itemUsed.getId() == 5286 && usedWith.getId() == 5354) { // curry
				if (player.getSkills().getLevel(Skills.FARMING) < 42) {
					player.sendMessage("You need a Farming level of at least 42 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1.4);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5499, 1);
				}
				return;
			}
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5287
					|| itemUsed.getId() == 5287 && usedWith.getId() == 5354) { // pineapple
				if (player.getSkills().getLevel(Skills.FARMING) < 51) {
					player.sendMessage("You need a Farming level of at least 51 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1.4);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5500, 1);
				}
				return;
			}
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5288
					|| itemUsed.getId() == 5288 && usedWith.getId() == 5354) { // papaya
				if (player.getSkills().getLevel(Skills.FARMING) < 57) {
					player.sendMessage("You need a Farming level of at least 57 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1.4);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5501, 1);
				}
				return;
			}
			if (itemUsed.getId() == 5354 && usedWith.getId() == 5289
					|| itemUsed.getId() == 5289 && usedWith.getId() == 5354) { // palm
				if (player.getSkills().getLevel(Skills.FARMING) < 68) {
					player.sendMessage("You need a Farming level of at least 68 in order to do this.");
					return;
				}
				if (!player.getFarmingManager().checkWaterCan(player)) {
					player.getSkills().addXp(Skills.FARMING, 1.4);
					player.getInventory().deleteItem(itemUsed.getId(), 1);
					player.getInventory().deleteItem(usedWith.getId(), 1);
					player.getInventory().addItem(5502, 1);
				}
				return;
			}
			/**
			 * Flask Making
			 */

			// Attack (4)
			if (usedWith.getId() == 2428 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(121, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(121, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23201, 1);
					return;
				}
			}
			// Attack (3)
			if (usedWith.getId() == 121 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(121, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(121, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23201, 1);
					return;
				}
			}
			// Attack (2)
			if (usedWith.getId() == 123 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(123, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(123, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23203, 1);
					return;
				}
			}
			// Attack (1)
			if (usedWith.getId() == 125 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(125, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(125, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23205, 1);
					return;
				}
			}
			// Super Attack (1) into Super Attack Flask (5)
			if (usedWith.getId() == 149 || usedWith.getId() == 23257) {
				if (player.getInventory().containsItem(149, 1) && player.getInventory().containsItem(23257, 1)) {
					player.getInventory().deleteItem(149, 1);
					player.getInventory().deleteItem(23257, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23255, 1);
					return;
				}
			}
			// Super Attack (1) into Super Attack Flask (4)
			if (usedWith.getId() == 149 || usedWith.getId() == 23259) {
				if (player.getInventory().containsItem(149, 1) && player.getInventory().containsItem(23259, 1)) {
					player.getInventory().deleteItem(149, 1);
					player.getInventory().deleteItem(23259, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23257, 1);
					return;
				}
			}
			// Super Attack (1) into Super Attack Flask (3)
			if (usedWith.getId() == 149 || usedWith.getId() == 23261) {
				if (player.getInventory().containsItem(149, 1) && player.getInventory().containsItem(23261, 1)) {
					player.getInventory().deleteItem(149, 1);
					player.getInventory().deleteItem(23261, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23259, 1);
					return;
				}
			}
			// Super Attack (1) into Super Attack Flask (2)
			if (usedWith.getId() == 149 || usedWith.getId() == 23263) {
				if (player.getInventory().containsItem(149, 1) && player.getInventory().containsItem(23263, 1)) {
					player.getInventory().deleteItem(149, 1);
					player.getInventory().deleteItem(23263, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23261, 1);
					return;
				}
			}
			// Super Attack (1) into Super Attack Flask (1)
			if (usedWith.getId() == 149 || usedWith.getId() == 23265) {
				if (player.getInventory().containsItem(149, 1) && player.getInventory().containsItem(23265, 1)) {
					player.getInventory().deleteItem(149, 1);
					player.getInventory().deleteItem(23265, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23263, 1);
					return;
				}
			}
			// Super Attack (2) into Super Attack Flask (5)
			if (usedWith.getId() == 147 || usedWith.getId() == 23257) {
				if (player.getInventory().containsItem(147, 1) && player.getInventory().containsItem(23257, 1)) {
					player.getInventory().deleteItem(147, 1);
					player.getInventory().deleteItem(23257, 1);
					player.getInventory().addItem(149, 1);
					player.getInventory().addItem(23255, 1);
					return;
				}
			}
			// Super Attack (2) into Super Attack Flask (4)
			if (usedWith.getId() == 147 || usedWith.getId() == 23259) {
				if (player.getInventory().containsItem(147, 1) && player.getInventory().containsItem(23259, 1)) {
					player.getInventory().deleteItem(147, 1);
					player.getInventory().deleteItem(23259, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23255, 1);
					return;
				}
			}
			// Super Attack (2) into Super Attack Flask (3)
			if (usedWith.getId() == 147 || usedWith.getId() == 23261) {
				if (player.getInventory().containsItem(147, 1) && player.getInventory().containsItem(23261, 1)) {
					player.getInventory().deleteItem(147, 1);
					player.getInventory().deleteItem(23261, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23257, 1);
					return;
				}
			}
			// Super Attack (2) into Super Attack Flask (2)
			if (usedWith.getId() == 147 || usedWith.getId() == 23263) {
				if (player.getInventory().containsItem(147, 1) && player.getInventory().containsItem(23263, 1)) {
					player.getInventory().deleteItem(147, 1);
					player.getInventory().deleteItem(23263, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23259, 1);
					return;
				}
			}
			// Super Attack (2) into Super Attack Flask (1)
			if (usedWith.getId() == 147 || usedWith.getId() == 23265) {
				if (player.getInventory().containsItem(147, 1) && player.getInventory().containsItem(23265, 1)) {
					player.getInventory().deleteItem(147, 1);
					player.getInventory().deleteItem(23265, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23261, 1);
					return;
				}
			}
			// Super Attack (3) into Super Attack Flask (5)
			if (usedWith.getId() == 145 || usedWith.getId() == 23257) {
				if (player.getInventory().containsItem(145, 1) && player.getInventory().containsItem(23257, 1)) {
					player.getInventory().deleteItem(145, 1);
					player.getInventory().deleteItem(23257, 1);
					player.getInventory().addItem(147, 1);
					player.getInventory().addItem(23255, 1);
					return;
				}
			}
			// Super Attack (3) into Super Attack Flask (4)
			if (usedWith.getId() == 145 || usedWith.getId() == 23259) {
				if (player.getInventory().containsItem(145, 1) && player.getInventory().containsItem(23259, 1)) {
					player.getInventory().deleteItem(145, 1);
					player.getInventory().deleteItem(23259, 1);
					player.getInventory().addItem(149, 1);
					player.getInventory().addItem(23255, 1);
					return;
				}
			}
			// Super Attack (3) into Super Attack Flask (3)
			if (usedWith.getId() == 145 || usedWith.getId() == 23261) {
				if (player.getInventory().containsItem(145, 1) && player.getInventory().containsItem(23261, 1)) {
					player.getInventory().deleteItem(145, 1);
					player.getInventory().deleteItem(23261, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23255, 1);
					return;
				}
			}
			// Super Attack (3) into Super Attack Flask (2)
			if (usedWith.getId() == 145 || usedWith.getId() == 23263) {
				if (player.getInventory().containsItem(145, 1) && player.getInventory().containsItem(23263, 1)) {
					player.getInventory().deleteItem(145, 1);
					player.getInventory().deleteItem(23263, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23257, 1);
					return;
				}
			}
			// Super Attack (3) into Super Attack Flask (1)
			if (usedWith.getId() == 145 || usedWith.getId() == 23265) {
				if (player.getInventory().containsItem(145, 1) && player.getInventory().containsItem(23265, 1)) {
					player.getInventory().deleteItem(145, 1);
					player.getInventory().deleteItem(23265, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23259, 1);
					return;
				}
			}
			// Super Attack (4) into Super Attack Flask (5)
			if (usedWith.getId() == 2436 || usedWith.getId() == 23257) {
				if (player.getInventory().containsItem(2436, 1) && player.getInventory().containsItem(23257, 1)) {
					player.getInventory().deleteItem(2436, 1);
					player.getInventory().deleteItem(23257, 1);
					player.getInventory().addItem(145, 1);
					player.getInventory().addItem(23255, 1);
					return;
				}
			}
			// Super Attack (4) into Super Attack Flask (4)
			if (usedWith.getId() == 2436 || usedWith.getId() == 23259) {
				if (player.getInventory().containsItem(2436, 1) && player.getInventory().containsItem(23259, 1)) {
					player.getInventory().deleteItem(2436, 1);
					player.getInventory().deleteItem(23259, 1);
					player.getInventory().addItem(147, 1);
					player.getInventory().addItem(23255, 1);
					return;
				}
			}
			// Super Attack (4) into Super Attack Flask (3)
			if (usedWith.getId() == 2436 || usedWith.getId() == 23261) {
				if (player.getInventory().containsItem(2436, 1) && player.getInventory().containsItem(23261, 1)) {
					player.getInventory().deleteItem(2436, 1);
					player.getInventory().deleteItem(23261, 1);
					player.getInventory().addItem(149, 1);
					player.getInventory().addItem(23255, 1);
					return;
				}
			}
			// Super Attack (4) into Super Attack Flask (2)
			if (usedWith.getId() == 2436 || usedWith.getId() == 23263) {
				if (player.getInventory().containsItem(2436, 1) && player.getInventory().containsItem(23263, 1)) {
					player.getInventory().deleteItem(2436, 1);
					player.getInventory().deleteItem(23263, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23255, 1);
					return;
				}
			}
			// Super Attack (4) into Super Attack Flask (1)
			if (usedWith.getId() == 2436 || usedWith.getId() == 23265) {
				if (player.getInventory().containsItem(2436, 1) && player.getInventory().containsItem(23265, 1)) {
					player.getInventory().deleteItem(2436, 1);
					player.getInventory().deleteItem(23265, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23257, 1);
					return;
				}
			}
			// Super Attack (4) into Empty Flask
			if (usedWith.getId() == 2436 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(2436, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(2436, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23259, 1);
					return;
				}
			}
			// Super Attack (3) into Empty Flask
			if (usedWith.getId() == 145 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(145, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(145, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23261, 1);
					return;
				}
			}
			// Super Attack (2) into Empty Flask
			if (usedWith.getId() == 147 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(147, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(147, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23263, 1);
					return;
				}
			}
			// Super Attack (1) into Empty Flask
			if (usedWith.getId() == 149 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(149, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(149, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23265, 1);
					return;
				}
			}
			// Super Strength (1) into Super Strength Flask (5)
			if (usedWith.getId() == 161 || usedWith.getId() == 23281) {
				if (player.getInventory().containsItem(161, 1) && player.getInventory().containsItem(23281, 1)) {
					player.getInventory().deleteItem(161, 1);
					player.getInventory().deleteItem(23281, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23279, 1);
					return;
				}
			}
			// Super Strength (1) into Super Strength Flask (4)
			if (usedWith.getId() == 161 || usedWith.getId() == 23283) {
				if (player.getInventory().containsItem(161, 1) && player.getInventory().containsItem(23283, 1)) {
					player.getInventory().deleteItem(161, 1);
					player.getInventory().deleteItem(23283, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23281, 1);
					return;
				}
			}
			// Super Strength (1) into Super Strength Flask (3)
			if (usedWith.getId() == 161 || usedWith.getId() == 23285) {
				if (player.getInventory().containsItem(161, 1) && player.getInventory().containsItem(23285, 1)) {
					player.getInventory().deleteItem(161, 1);
					player.getInventory().deleteItem(23285, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23283, 1);
					return;
				}
			}
			// Super Strength (1) into Super Strength Flask (2)
			if (usedWith.getId() == 161 || usedWith.getId() == 23287) {
				if (player.getInventory().containsItem(161, 1) && player.getInventory().containsItem(23287, 1)) {
					player.getInventory().deleteItem(161, 1);
					player.getInventory().deleteItem(23287, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23285, 1);
					return;
				}
			}
			// Super Strength (1) into Super Strength Flask (1)
			if (usedWith.getId() == 161 || usedWith.getId() == 23289) {
				if (player.getInventory().containsItem(161, 1) && player.getInventory().containsItem(23289, 1)) {
					player.getInventory().deleteItem(161, 1);
					player.getInventory().deleteItem(23289, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23287, 1);
					return;
				}
			}
			// Super Strength (2) into Super Strength Flask (5)
			if (usedWith.getId() == 159 || usedWith.getId() == 23281) {
				if (player.getInventory().containsItem(159, 1) && player.getInventory().containsItem(23281, 1)) {
					player.getInventory().deleteItem(159, 1);
					player.getInventory().deleteItem(23281, 1);
					player.getInventory().addItem(161, 1);
					player.getInventory().addItem(23279, 1);
					return;
				}
			}
			// Super Strength (2) into Super Strength Flask (4)
			if (usedWith.getId() == 159 || usedWith.getId() == 23283) {
				if (player.getInventory().containsItem(159, 1) && player.getInventory().containsItem(23283, 1)) {
					player.getInventory().deleteItem(159, 1);
					player.getInventory().deleteItem(23283, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23279, 1);
					return;
				}
			}
			// Super Strength (2) into Super Strength Flask (3)
			if (usedWith.getId() == 159 || usedWith.getId() == 23285) {
				if (player.getInventory().containsItem(159, 1) && player.getInventory().containsItem(23285, 1)) {
					player.getInventory().deleteItem(159, 1);
					player.getInventory().deleteItem(23285, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23281, 1);
					return;
				}
			}
			// Super Strength (2) into Super Strength Flask (2)
			if (usedWith.getId() == 159 || usedWith.getId() == 23287) {
				if (player.getInventory().containsItem(159, 1) && player.getInventory().containsItem(23287, 1)) {
					player.getInventory().deleteItem(159, 1);
					player.getInventory().deleteItem(23287, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23283, 1);
					return;
				}
			}
			// Super Strength (2) into Super Strength Flask (1)
			if (usedWith.getId() == 159 || usedWith.getId() == 23289) {
				if (player.getInventory().containsItem(159, 1) && player.getInventory().containsItem(23289, 1)) {
					player.getInventory().deleteItem(159, 1);
					player.getInventory().deleteItem(23289, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23285, 1);
					return;
				}
			}
			// Super Strength (3) into Super Strength Flask (5)
			if (usedWith.getId() == 157 || usedWith.getId() == 23281) {
				if (player.getInventory().containsItem(157, 1) && player.getInventory().containsItem(23281, 1)) {
					player.getInventory().deleteItem(157, 1);
					player.getInventory().deleteItem(23281, 1);
					player.getInventory().addItem(159, 1);
					player.getInventory().addItem(23279, 1);
					return;
				}
			}
			// Super Strength (3) into Super Strength Flask (4)
			if (usedWith.getId() == 157 || usedWith.getId() == 23283) {
				if (player.getInventory().containsItem(157, 1) && player.getInventory().containsItem(23283, 1)) {
					player.getInventory().deleteItem(157, 1);
					player.getInventory().deleteItem(23283, 1);
					player.getInventory().addItem(161, 1);
					player.getInventory().addItem(23279, 1);
					return;
				}
			}
			// Super Strength (3) into Super Strength Flask (3)
			if (usedWith.getId() == 157 || usedWith.getId() == 23285) {
				if (player.getInventory().containsItem(157, 1) && player.getInventory().containsItem(23285, 1)) {
					player.getInventory().deleteItem(157, 1);
					player.getInventory().deleteItem(23285, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23279, 1);
					return;
				}
			}
			// Super Strength (3) into Super Strength Flask (2)
			if (usedWith.getId() == 157 || usedWith.getId() == 23287) {
				if (player.getInventory().containsItem(157, 1) && player.getInventory().containsItem(23287, 1)) {
					player.getInventory().deleteItem(157, 1);
					player.getInventory().deleteItem(23287, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23281, 1);
					return;
				}
			}
			// Super Strength (3) into Super Strength Flask (1)
			if (usedWith.getId() == 157 || usedWith.getId() == 23289) {
				if (player.getInventory().containsItem(157, 1) && player.getInventory().containsItem(23289, 1)) {
					player.getInventory().deleteItem(157, 1);
					player.getInventory().deleteItem(23289, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23283, 1);
					return;
				}
			}
			// Super Strength (4) into Super Strength Flask (5)
			if (usedWith.getId() == 2440 || usedWith.getId() == 23281) {
				if (player.getInventory().containsItem(2440, 1) && player.getInventory().containsItem(23281, 1)) {
					player.getInventory().deleteItem(2440, 1);
					player.getInventory().deleteItem(23281, 1);
					player.getInventory().addItem(157, 1);
					player.getInventory().addItem(23279, 1);
					return;
				}
			}
			// Super Strength (4) into Super Strength Flask (4)
			if (usedWith.getId() == 2440 || usedWith.getId() == 23283) {
				if (player.getInventory().containsItem(2440, 1) && player.getInventory().containsItem(23283, 1)) {
					player.getInventory().deleteItem(2440, 1);
					player.getInventory().deleteItem(23283, 1);
					player.getInventory().addItem(159, 1);
					player.getInventory().addItem(23279, 1);
					return;
				}
			}
			// Super Strength (4) into Super Strength Flask (3)
			if (usedWith.getId() == 2440 || usedWith.getId() == 23285) {
				if (player.getInventory().containsItem(2440, 1) && player.getInventory().containsItem(23285, 1)) {
					player.getInventory().deleteItem(2440, 1);
					player.getInventory().deleteItem(23285, 1);
					player.getInventory().addItem(161, 1);
					player.getInventory().addItem(23279, 1);
					return;
				}
			}
			// Super Strength (4) into Super Strength Flask (2)
			if (usedWith.getId() == 2440 || usedWith.getId() == 23287) {
				if (player.getInventory().containsItem(2440, 1) && player.getInventory().containsItem(23287, 1)) {
					player.getInventory().deleteItem(2440, 1);
					player.getInventory().deleteItem(23287, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23279, 1);
					return;
				}
			}
			// Super Strength (4) into Super Strength Flask (1)
			if (usedWith.getId() == 2440 || usedWith.getId() == 23289) {
				if (player.getInventory().containsItem(2440, 1) && player.getInventory().containsItem(23289, 1)) {
					player.getInventory().deleteItem(2440, 1);
					player.getInventory().deleteItem(23289, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23281, 1);
					return;
				}
			}
			// Super Strength (4) into Empty Flask
			if (usedWith.getId() == 2440 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(2440, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(2440, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23283, 1);
					return;
				}
			}
			// Super Strength (3) into Empty Flask
			if (usedWith.getId() == 157 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(157, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(157, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23285, 1);
					return;
				}
			}
			// Super Strength (2) into Empty Flask
			if (usedWith.getId() == 159 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(159, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(159, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23287, 1);
					return;
				}
			}
			// Super Strength (1) into Empty Flask
			if (usedWith.getId() == 161 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(161, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(161, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23289, 1);
					return;
				}
			}
			// Super Defence (1) into Super Defence Flask (5)
			if (usedWith.getId() == 167 || usedWith.getId() == 23293) {
				if (player.getInventory().containsItem(167, 1) && player.getInventory().containsItem(23293, 1)) {
					player.getInventory().deleteItem(167, 1);
					player.getInventory().deleteItem(23293, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23291, 1);
					return;
				}
			}
			// Super Defence (1) into Super Defence Flask (4)
			if (usedWith.getId() == 167 || usedWith.getId() == 23295) {
				if (player.getInventory().containsItem(167, 1) && player.getInventory().containsItem(23295, 1)) {
					player.getInventory().deleteItem(167, 1);
					player.getInventory().deleteItem(23295, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23293, 1);
					return;
				}
			}
			// Super Defence (1) into Super Defence Flask (3)
			if (usedWith.getId() == 167 || usedWith.getId() == 23297) {
				if (player.getInventory().containsItem(167, 1) && player.getInventory().containsItem(23297, 1)) {
					player.getInventory().deleteItem(167, 1);
					player.getInventory().deleteItem(23297, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23295, 1);
					return;
				}
			}
			// Super Defence (1) into Super Defence Flask (2)
			if (usedWith.getId() == 167 || usedWith.getId() == 23299) {
				if (player.getInventory().containsItem(167, 1) && player.getInventory().containsItem(23299, 1)) {
					player.getInventory().deleteItem(167, 1);
					player.getInventory().deleteItem(23299, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23297, 1);
					return;
				}
			}
			// Super Defence (1) into Super Defence Flask (1)
			if (usedWith.getId() == 167 || usedWith.getId() == 23301) {
				if (player.getInventory().containsItem(167, 1) && player.getInventory().containsItem(23301, 1)) {
					player.getInventory().deleteItem(167, 1);
					player.getInventory().deleteItem(23301, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23299, 1);
					return;
				}
			}
			// Super Defence (2) into Super Defence Flask (5)
			if (usedWith.getId() == 165 || usedWith.getId() == 23293) {
				if (player.getInventory().containsItem(165, 1) && player.getInventory().containsItem(23293, 1)) {
					player.getInventory().deleteItem(165, 1);
					player.getInventory().deleteItem(23293, 1);
					player.getInventory().addItem(167, 1);
					player.getInventory().addItem(23291, 1);
					return;
				}
			}
			// Super Defence (2) into Super Defence Flask (4)
			if (usedWith.getId() == 165 || usedWith.getId() == 23295) {
				if (player.getInventory().containsItem(165, 1) && player.getInventory().containsItem(23295, 1)) {
					player.getInventory().deleteItem(165, 1);
					player.getInventory().deleteItem(23295, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23291, 1);
					return;
				}
			}
			// Super Defence (2) into Super Defence Flask (3)
			if (usedWith.getId() == 165 || usedWith.getId() == 23297) {
				if (player.getInventory().containsItem(165, 1) && player.getInventory().containsItem(23297, 1)) {
					player.getInventory().deleteItem(165, 1);
					player.getInventory().deleteItem(23297, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23293, 1);
					return;
				}
			}
			// Super Defence (2) into Super Defence Flask (2)
			if (usedWith.getId() == 165 || usedWith.getId() == 23299) {
				if (player.getInventory().containsItem(165, 1) && player.getInventory().containsItem(23299, 1)) {
					player.getInventory().deleteItem(165, 1);
					player.getInventory().deleteItem(23299, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23295, 1);
					return;
				}
			}
			// Super Defence (2) into Super Defence Flask (1)
			if (usedWith.getId() == 165 || usedWith.getId() == 23301) {
				if (player.getInventory().containsItem(165, 1) && player.getInventory().containsItem(23301, 1)) {
					player.getInventory().deleteItem(165, 1);
					player.getInventory().deleteItem(23301, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23297, 1);
					return;
				}
			}
			// Super Defence (3) into Super Defence Flask (5)
			if (usedWith.getId() == 163 || usedWith.getId() == 23293) {
				if (player.getInventory().containsItem(163, 1) && player.getInventory().containsItem(23293, 1)) {
					player.getInventory().deleteItem(163, 1);
					player.getInventory().deleteItem(23293, 1);
					player.getInventory().addItem(165, 1);
					player.getInventory().addItem(23291, 1);
					return;
				}
			}
			// Super Defence (3) into Super Defence Flask (4)
			if (usedWith.getId() == 163 || usedWith.getId() == 23295) {
				if (player.getInventory().containsItem(163, 1) && player.getInventory().containsItem(23295, 1)) {
					player.getInventory().deleteItem(163, 1);
					player.getInventory().deleteItem(23295, 1);
					player.getInventory().addItem(167, 1);
					player.getInventory().addItem(23291, 1);
					return;
				}
			}
			// Super Defence (3) into Super Defence Flask (3)
			if (usedWith.getId() == 163 || usedWith.getId() == 23297) {
				if (player.getInventory().containsItem(163, 1) && player.getInventory().containsItem(23297, 1)) {
					player.getInventory().deleteItem(163, 1);
					player.getInventory().deleteItem(23297, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23291, 1);
					return;
				}
			}
			// Super Defence (3) into Super Defence Flask (2)
			if (usedWith.getId() == 163 || usedWith.getId() == 23299) {
				if (player.getInventory().containsItem(163, 1) && player.getInventory().containsItem(23299, 1)) {
					player.getInventory().deleteItem(163, 1);
					player.getInventory().deleteItem(23299, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23293, 1);
					return;
				}
			}
			// Super Defence (3) into Super Defence Flask (1)
			if (usedWith.getId() == 163 || usedWith.getId() == 23301) {
				if (player.getInventory().containsItem(163, 1) && player.getInventory().containsItem(23301, 1)) {
					player.getInventory().deleteItem(163, 1);
					player.getInventory().deleteItem(23301, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23295, 1);
					return;
				}
			}
			// Super Defence (4) into Super Defence Flask (5)
			if (usedWith.getId() == 2442 || usedWith.getId() == 23293) {
				if (player.getInventory().containsItem(2442, 1) && player.getInventory().containsItem(23293, 1)) {
					player.getInventory().deleteItem(2442, 1);
					player.getInventory().deleteItem(23293, 1);
					player.getInventory().addItem(163, 1);
					player.getInventory().addItem(23291, 1);
					return;
				}
			}
			// Super Defence (4) into Super Defence Flask (4)
			if (usedWith.getId() == 2442 || usedWith.getId() == 23295) {
				if (player.getInventory().containsItem(2442, 1) && player.getInventory().containsItem(23295, 1)) {
					player.getInventory().deleteItem(2442, 1);
					player.getInventory().deleteItem(23295, 1);
					player.getInventory().addItem(165, 1);
					player.getInventory().addItem(23291, 1);
					return;
				}
			}
			// Super Defence (4) into Super Defence Flask (3)
			if (usedWith.getId() == 2442 || usedWith.getId() == 23297) {
				if (player.getInventory().containsItem(2442, 1) && player.getInventory().containsItem(23297, 1)) {
					player.getInventory().deleteItem(2442, 1);
					player.getInventory().deleteItem(23297, 1);
					player.getInventory().addItem(167, 1);
					player.getInventory().addItem(23291, 1);
					return;
				}
			}
			// Super Defence (4) into Super Defence Flask (2)
			if (usedWith.getId() == 2442 || usedWith.getId() == 23299) {
				if (player.getInventory().containsItem(2442, 1) && player.getInventory().containsItem(23299, 1)) {
					player.getInventory().deleteItem(2442, 1);
					player.getInventory().deleteItem(23299, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23291, 1);
					return;
				}
			}
			// Super Defence (4) into Super Defence Flask (1)
			if (usedWith.getId() == 2442 || usedWith.getId() == 23301) {
				if (player.getInventory().containsItem(2442, 1) && player.getInventory().containsItem(23301, 1)) {
					player.getInventory().deleteItem(2442, 1);
					player.getInventory().deleteItem(23301, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23293, 1);
					return;
				}
			}
			// Super Defence (4)
			if (usedWith.getId() == 2442 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(2442, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(2442, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23295, 1);
					return;
				}
			}
			// Super Defence (3)
			if (usedWith.getId() == 163 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(163, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(163, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23297, 1);
					return;
				}
			}
			// Super Defence (2)
			if (usedWith.getId() == 165 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(165, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(165, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23299, 1);
					return;
				}
			}
			// Super Defence (1)
			if (usedWith.getId() == 167 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(167, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(167, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23301, 1);
					return;
				}
			}
			// Prayer pot (1)
			if (usedWith.getId() == 143 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(143, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(143, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23253, 1);
					return;
				}
			}
			// Prayer pot (2)
			if (usedWith.getId() == 141 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(141, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(141, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23251, 1);
					return;
				}
			}
			// Prayer pot (3)
			if (usedWith.getId() == 139 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(139, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(139, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23249, 1);
					return;
				}
			}
			// Prayer pot (4)
			if (usedWith.getId() == 2434 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(2434, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(2434, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23247, 1);
					return;
				}
			}
			// Prayer pot (1) into prayer flask (1)
			if (usedWith.getId() == 143 || usedWith.getId() == 23253) {
				if (player.getInventory().containsItem(143, 1) && player.getInventory().containsItem(23253, 1)) {
					player.getInventory().deleteItem(143, 1);
					player.getInventory().deleteItem(23253, 1);
					player.getInventory().addItem(23251, 1);
					return;
				}
			}
			// Prayer pot (2) into prayer flask (1)
			if (usedWith.getId() == 141 || usedWith.getId() == 23253) {
				if (player.getInventory().containsItem(141, 1) && player.getInventory().containsItem(23253, 1)) {
					player.getInventory().deleteItem(141, 1);
					player.getInventory().deleteItem(23253, 1);
					player.getInventory().addItem(23249, 1);
					return;
				}
			}
			// Prayer pot (3) into prayer flask (1)
			if (usedWith.getId() == 139 || usedWith.getId() == 23253) {
				if (player.getInventory().containsItem(139, 1) && player.getInventory().containsItem(23253, 1)) {
					player.getInventory().deleteItem(139, 1);
					player.getInventory().deleteItem(23253, 1);
					player.getInventory().addItem(23247, 1);
					return;
				}
			}
			// Prayer pot (4) into prayer flask (1)
			if (usedWith.getId() == 2434 || usedWith.getId() == 23253) {
				if (player.getInventory().containsItem(2434, 1) && player.getInventory().containsItem(23253, 1)) {
					player.getInventory().deleteItem(2434, 1);
					player.getInventory().deleteItem(23253, 1);
					player.getInventory().addItem(23245, 1);
					return;
				}
			}
			// Prayer pot (1) into prayer flask (2)
			if (usedWith.getId() == 143 || usedWith.getId() == 23251) {
				if (player.getInventory().containsItem(143, 1) && player.getInventory().containsItem(23251, 1)) {
					player.getInventory().deleteItem(143, 1);
					player.getInventory().deleteItem(23251, 1);
					player.getInventory().addItem(23249, 1);
					return;
				}
			}
			// Prayer pot (2) into prayer flask (2)
			if (usedWith.getId() == 141 || usedWith.getId() == 23251) {
				if (player.getInventory().containsItem(141, 1) && player.getInventory().containsItem(23251, 1)) {
					player.getInventory().deleteItem(141, 1);
					player.getInventory().deleteItem(23251, 1);
					player.getInventory().addItem(23247, 1);
					return;
				}
			}
			// Prayer pot (3) into prayer flask (2)
			if (usedWith.getId() == 139 || usedWith.getId() == 23251) {
				if (player.getInventory().containsItem(139, 1) && player.getInventory().containsItem(23251, 1)) {
					player.getInventory().deleteItem(139, 1);
					player.getInventory().deleteItem(23251, 1);
					player.getInventory().addItem(23245, 1);
					return;
				}
			}
			// Prayer pot (4) into prayer flask (2)
			if (usedWith.getId() == 2434 || usedWith.getId() == 23251) {
				if (player.getInventory().containsItem(2434, 1) && player.getInventory().containsItem(23251, 1)) {
					player.getInventory().deleteItem(2434, 1);
					player.getInventory().deleteItem(23251, 1);
					player.getInventory().addItem(23243, 1);
					return;
				}
			}
			// Prayer pot (1) into prayer flask (3)
			if (usedWith.getId() == 143 || usedWith.getId() == 23249) {
				if (player.getInventory().containsItem(143, 1) && player.getInventory().containsItem(23249, 1)) {
					player.getInventory().deleteItem(143, 1);
					player.getInventory().deleteItem(23249, 1);
					player.getInventory().addItem(23247, 1);
					return;
				}
			}
			// Prayer pot (2) into prayer flask (3)
			if (usedWith.getId() == 141 || usedWith.getId() == 23249) {
				if (player.getInventory().containsItem(141, 1) && player.getInventory().containsItem(23249, 1)) {
					player.getInventory().deleteItem(141, 1);
					player.getInventory().deleteItem(23249, 1);
					player.getInventory().addItem(23245, 1);
					return;
				}
			}
			// Prayer pot (3) into prayer flask (3)
			if (usedWith.getId() == 139 || usedWith.getId() == 23249) {
				if (player.getInventory().containsItem(139, 1) && player.getInventory().containsItem(23249, 1)) {
					player.getInventory().deleteItem(139, 1);
					player.getInventory().deleteItem(23249, 1);
					player.getInventory().addItem(23243, 1);
					return;
				}
			}
			// Prayer pot (1) into prayer flask (4)
			if (usedWith.getId() == 143 || usedWith.getId() == 23247) {
				if (player.getInventory().containsItem(143, 1) && player.getInventory().containsItem(23247, 1)) {
					player.getInventory().deleteItem(143, 1);
					player.getInventory().deleteItem(23247, 1);
					player.getInventory().addItem(23245, 1);
					return;
				}
			}
			// Prayer pot (2) into prayer flask (4)
			if (usedWith.getId() == 141 || usedWith.getId() == 23247) {
				if (player.getInventory().containsItem(141, 1) && player.getInventory().containsItem(23247, 1)) {
					player.getInventory().deleteItem(141, 1);
					player.getInventory().deleteItem(23247, 1);
					player.getInventory().addItem(23243, 1);
					return;
				}
			}
			// Prayer pot (1) into prayer flask (5)
			if (usedWith.getId() == 143 || usedWith.getId() == 23245) {
				if (player.getInventory().containsItem(143, 1) && player.getInventory().containsItem(23245, 1)) {
					player.getInventory().deleteItem(143, 1);
					player.getInventory().deleteItem(23245, 1);
					player.getInventory().addItem(23243, 1);
					return;
				}
			}

			/**
			 * Overloads
			 */

			// Overload (1) into Overload Flask (5)
			if (usedWith.getId() == 15335 || usedWith.getId() == 23532) {
				if (player.getInventory().containsItem(15335, 1) && player.getInventory().containsItem(26751, 1)) {
					player.getInventory().deleteItem(15335, 1);
					player.getInventory().deleteItem(23532, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23531, 1); // id for (6)
					return;
				}
			}
			// Overload (1) into Overload Flask (4)
			if (usedWith.getId() == 15335 || usedWith.getId() == 23533) {
				if (player.getInventory().containsItem(15335, 1) && player.getInventory().containsItem(23533, 1)) {
					player.getInventory().deleteItem(15335, 1);
					player.getInventory().deleteItem(23533, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23532, 1); // id for (5)
					return;
				}
			}
			// Overload (1) into Overload Flask (3)
			if (usedWith.getId() == 15335 || usedWith.getId() == 23534) {
				if (player.getInventory().containsItem(15335, 1) && player.getInventory().containsItem(23534, 1)) {
					player.getInventory().deleteItem(15335, 1);
					player.getInventory().deleteItem(23534, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23533, 1); // id for (4)
					return;
				}
			}
			// Overload (1) into Overload Flask (2)
			if (usedWith.getId() == 15335 || usedWith.getId() == 23535) {
				if (player.getInventory().containsItem(15335, 1) && player.getInventory().containsItem(23535, 1)) {
					player.getInventory().deleteItem(15335, 1);
					player.getInventory().deleteItem(23535, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23534, 1); // id for (3)
					return;
				}
			}
			// Overload (1) into Overload Flask (1)
			if (usedWith.getId() == 15335 || usedWith.getId() == 23536) {
				if (player.getInventory().containsItem(15335, 1) && player.getInventory().containsItem(23536, 1)) {
					player.getInventory().deleteItem(15335, 1);
					player.getInventory().deleteItem(23536, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23535, 1); // id for (2)
					return;
				}
			}
			// Overload (2) into Overload Flask (5)
			if (usedWith.getId() == 15334 || usedWith.getId() == 23532) {
				if (player.getInventory().containsItem(15334, 1) && player.getInventory().containsItem(23532, 1)) {
					player.getInventory().deleteItem(15334, 1);
					player.getInventory().deleteItem(23532, 1);
					player.getInventory().addItem(15335, 1);
					player.getInventory().addItem(23531, 1); // id for (6)
					return;
				}
			}
			// Overload (2) into Overload Flask (4)
			if (usedWith.getId() == 15334 || usedWith.getId() == 23533) {
				if (player.getInventory().containsItem(15334, 1) && player.getInventory().containsItem(23533, 1)) {
					player.getInventory().deleteItem(15334, 1);
					player.getInventory().deleteItem(23533, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23531, 1); // id for (6)
					return;
				}
			}
			// Overload (2) into Overload Flask (3)
			if (usedWith.getId() == 15334 || usedWith.getId() == 23534) {
				if (player.getInventory().containsItem(15334, 1) && player.getInventory().containsItem(23534, 1)) {
					player.getInventory().deleteItem(15334, 1);
					player.getInventory().deleteItem(23534, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23532, 1); // id for (5)
					return;
				}
			}
			// Overload (2) into Overload Flask (2)
			if (usedWith.getId() == 15334 || usedWith.getId() == 23535) {
				if (player.getInventory().containsItem(15334, 1) && player.getInventory().containsItem(23535, 1)) {
					player.getInventory().deleteItem(15334, 1);
					player.getInventory().deleteItem(23535, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23533, 1); // id for (4)
					return;
				}
			}
			// Overload (2) into Overload Flask (1)
			if (usedWith.getId() == 15334 || usedWith.getId() == 23536) {
				if (player.getInventory().containsItem(15334, 1) && player.getInventory().containsItem(23536, 1)) {
					player.getInventory().deleteItem(15334, 1);
					player.getInventory().deleteItem(23536, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23534, 1); // id for (3)
					return;
				}
			}
			// Overload (3) into Overload Flask (5)
			if (usedWith.getId() == 15333 || usedWith.getId() == 23532) {
				if (player.getInventory().containsItem(15333, 1) && player.getInventory().containsItem(23532, 1)) {
					player.getInventory().deleteItem(15333, 1);
					player.getInventory().deleteItem(23532, 1);
					player.getInventory().addItem(15334, 1);
					player.getInventory().addItem(23531, 1); // id for (6)
					return;
				}
			}
			// Overload (3) into Overload Flask (4)
			if (usedWith.getId() == 15333 || usedWith.getId() == 23533) {
				if (player.getInventory().containsItem(15333, 1) && player.getInventory().containsItem(23533, 1)) {
					player.getInventory().deleteItem(15333, 1);
					player.getInventory().deleteItem(23533, 1);
					player.getInventory().addItem(15335, 1);
					player.getInventory().addItem(23531, 1); // id for (6)
					return;
				}
			}
			// Overload (3) into Overload Flask (3)
			if (usedWith.getId() == 15333 || usedWith.getId() == 23534) {
				if (player.getInventory().containsItem(15333, 1) && player.getInventory().containsItem(23534, 1)) {
					player.getInventory().deleteItem(15333, 1);
					player.getInventory().deleteItem(23534, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23531, 1); // id for (6)
					return;
				}
			}
			// Overload (3) into Overload Flask (2)
			if (usedWith.getId() == 15333 || usedWith.getId() == 23535) {
				if (player.getInventory().containsItem(15333, 1) && player.getInventory().containsItem(23535, 1)) {
					player.getInventory().deleteItem(15333, 1);
					player.getInventory().deleteItem(23535, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23532, 1); // id for (5)
					return;
				}
			}
			// Overload (3) into Overload Flask (1)
			if (usedWith.getId() == 15333 || usedWith.getId() == 23536) {
				if (player.getInventory().containsItem(15333, 1) && player.getInventory().containsItem(23536, 1)) {
					player.getInventory().deleteItem(15333, 1);
					player.getInventory().deleteItem(23536, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23533, 1); // id for (4)
					return;
				}
			}
			// Overload (4) into Overload Flask (5)
			if (usedWith.getId() == 15332 || usedWith.getId() == 23532) {
				if (player.getInventory().containsItem(15332, 1) && player.getInventory().containsItem(23532, 1)) {
					player.getInventory().deleteItem(15332, 1);
					player.getInventory().deleteItem(23532, 1);
					player.getInventory().addItem(15333, 1);
					player.getInventory().addItem(23531, 1); // id for (6)
					return;
				}
			}
			// Overload (4) into Overload Flask (4)
			if (usedWith.getId() == 15332 || usedWith.getId() == 23533) {
				if (player.getInventory().containsItem(15332, 1) && player.getInventory().containsItem(23533, 1)) {
					player.getInventory().deleteItem(15332, 1);
					player.getInventory().deleteItem(23533, 1);
					player.getInventory().addItem(15334, 1);
					player.getInventory().addItem(23531, 1); // id for (6)
					return;
				}
			}
			// Overload (4) into Overload Flask (3)
			if (usedWith.getId() == 15332 || usedWith.getId() == 23534) {
				if (player.getInventory().containsItem(15332, 1) && player.getInventory().containsItem(23534, 1)) {
					player.getInventory().deleteItem(15332, 1);
					player.getInventory().deleteItem(23534, 1);
					player.getInventory().addItem(15335, 1);
					player.getInventory().addItem(23531, 1); // id for (6)
					return;
				}
			}
			// Overload (4) into Overload Flask (2)
			if (usedWith.getId() == 15332 || usedWith.getId() == 23535) {
				if (player.getInventory().containsItem(15332, 1) && player.getInventory().containsItem(23535, 1)) {
					player.getInventory().deleteItem(15332, 1);
					player.getInventory().deleteItem(23535, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23531, 1); // id for (6)
					return;
				}
			}
			// Overload (4) into Overload Flask (1)
			if (usedWith.getId() == 15332 || usedWith.getId() == 23536) {
				if (player.getInventory().containsItem(15332, 1) && player.getInventory().containsItem(23536, 1)) {
					player.getInventory().deleteItem(15332, 1);
					player.getInventory().deleteItem(23536, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23532, 1); // id for (5)
					return;
				}
			}
			// Overload (1)
			if (usedWith.getId() == 15335 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(15335, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(15335, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23536, 1);
					return;
				}
			}
			// Overload (2)
			if (usedWith.getId() == 15334 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(15334, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(15334, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23535, 1);
					return;
				}
			}
			// Overload (3)
			if (usedWith.getId() == 15333 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(15333, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(15333, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23534, 1);
					return;
				}
			}
			// Overload (4)
			if (usedWith.getId() == 15332 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(15332, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(15332, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23533, 1);
					return;
				}
			}
			// Sarabrews (4)
			if (usedWith.getId() == 6685 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(6685, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(6685, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23355, 1);
					return;
				}
			}
			// Sarabrews (3)
			if (usedWith.getId() == 6687 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(6687, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(6687, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23357, 1);
					return;
				}
			}
			// Sarabrews (2)
			if (usedWith.getId() == 6689 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(6689, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(6689, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23359, 1);
					return;
				}
			}
			// Sarabrews (1)
			if (usedWith.getId() == 6691 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(6691, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(6691, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23361, 1);
					return;
				}
			}
			// sara (1) into sara Flask (5)
			if (usedWith.getId() == 6691 || usedWith.getId() == 23353) {
				if (player.getInventory().containsItem(6691, 1) && player.getInventory().containsItem(23353, 1)) {
					player.getInventory().deleteItem(6691, 1);
					player.getInventory().deleteItem(23353, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23351, 1);
					return;
				}
			}
			// Sara (1) into Sara Flask (4)
			if (usedWith.getId() == 6691 || usedWith.getId() == 23355) {
				if (player.getInventory().containsItem(6691, 1) && player.getInventory().containsItem(23355, 1)) {
					player.getInventory().deleteItem(6691, 1);
					player.getInventory().deleteItem(23355, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23353, 1);
					return;
				}
			}
			// Sara (1) into Sara (3)
			if (usedWith.getId() == 6691 || usedWith.getId() == 23357) {
				if (player.getInventory().containsItem(6691, 1) && player.getInventory().containsItem(23357, 1)) {
					player.getInventory().deleteItem(6691, 1);
					player.getInventory().deleteItem(23357, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23355, 1);
					return;
				}
			}
			// Sara (1) into Sara flask (2)
			if (usedWith.getId() == 6691 || usedWith.getId() == 23359) {
				if (player.getInventory().containsItem(6691, 1) && player.getInventory().containsItem(23359, 1)) {
					player.getInventory().deleteItem(6691, 1);
					player.getInventory().deleteItem(23359, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23357, 1);
					return;
				}
			}
			// Sara(1) into Sara Flask (1)
			if (usedWith.getId() == 6691 || usedWith.getId() == 23361) {
				if (player.getInventory().containsItem(6691, 1) && player.getInventory().containsItem(23361, 1)) {
					player.getInventory().deleteItem(6691, 1);
					player.getInventory().deleteItem(23361, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23359, 1);
					return;
				}
			}
			// sara (2) into sara Flask (5)
			if (usedWith.getId() == 6689 || usedWith.getId() == 23353) {
				if (player.getInventory().containsItem(6689, 1) && player.getInventory().containsItem(23353, 1)) {
					player.getInventory().deleteItem(6689, 1);
					player.getInventory().deleteItem(23353, 1);
					player.getInventory().addItem(6691, 1);
					player.getInventory().addItem(23351, 1);
					return;
				}
			}
			// Sara (2) into Sara Flask (4)
			if (usedWith.getId() == 6689 || usedWith.getId() == 23355) {
				if (player.getInventory().containsItem(6689, 1) && player.getInventory().containsItem(23355, 1)) {
					player.getInventory().deleteItem(6689, 1);
					player.getInventory().deleteItem(23355, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23351, 1);
					return;
				}
			}
			// Sara (2) into Sara (3)
			if (usedWith.getId() == 6689 || usedWith.getId() == 23357) {
				if (player.getInventory().containsItem(6689, 1) && player.getInventory().containsItem(23357, 1)) {
					player.getInventory().deleteItem(6689, 1);
					player.getInventory().deleteItem(23357, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23353, 1);
					return;
				}
			}
			// Sara (2) into Sara flask (2)
			if (usedWith.getId() == 6689 || usedWith.getId() == 23359) {
				if (player.getInventory().containsItem(6689, 1) && player.getInventory().containsItem(23359, 1)) {
					player.getInventory().deleteItem(6689, 1);
					player.getInventory().deleteItem(23359, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23355, 1);
					return;
				}
			}
			// Sara(2) into Sara Flask (1)
			if (usedWith.getId() == 6689 || usedWith.getId() == 23361) {
				if (player.getInventory().containsItem(6689, 1) && player.getInventory().containsItem(23361, 1)) {
					player.getInventory().deleteItem(6689, 1);
					player.getInventory().deleteItem(23361, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23358, 1);
					return;
				}
			}
			// sara (3) into sara Flask (5)
			if (usedWith.getId() == 6687 || usedWith.getId() == 23353) {
				if (player.getInventory().containsItem(6687, 1) && player.getInventory().containsItem(23353, 1)) {
					player.getInventory().deleteItem(6687, 1);
					player.getInventory().deleteItem(23353, 1);
					player.getInventory().addItem(6689, 1);
					player.getInventory().addItem(23351, 1);
					return;
				}
			}
			// Sara (3) into Sara Flask (4)
			if (usedWith.getId() == 6687 || usedWith.getId() == 23355) {
				if (player.getInventory().containsItem(6687, 1) && player.getInventory().containsItem(23355, 1)) {
					player.getInventory().deleteItem(6687, 1);
					player.getInventory().deleteItem(23355, 1);
					player.getInventory().addItem(6691, 1);
					player.getInventory().addItem(23351, 1);
					return;
				}
			}
			// Sara (3) into Sara (3)
			if (usedWith.getId() == 6687 || usedWith.getId() == 23357) {
				if (player.getInventory().containsItem(6687, 1) && player.getInventory().containsItem(23357, 1)) {
					player.getInventory().deleteItem(6687, 1);
					player.getInventory().deleteItem(23357, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23351, 1);
					return;
				}
			}
			// Sara (3) into Sara flask (2)
			if (usedWith.getId() == 6687 || usedWith.getId() == 23359) {
				if (player.getInventory().containsItem(6687, 1) && player.getInventory().containsItem(23359, 1)) {
					player.getInventory().deleteItem(6687, 1);
					player.getInventory().deleteItem(23359, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23353, 1);
					return;
				}
			}
			// Sara(3) into Sara Flask (1)
			if (usedWith.getId() == 6687 || usedWith.getId() == 23361) {
				if (player.getInventory().containsItem(6687, 1) && player.getInventory().containsItem(23361, 1)) {
					player.getInventory().deleteItem(6687, 1);
					player.getInventory().deleteItem(23361, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23355, 1);
					return;
				}
			}
			// sara (4) into sara Flask (5)
			if (usedWith.getId() == 6685 || usedWith.getId() == 23353) {
				if (player.getInventory().containsItem(6685, 1) && player.getInventory().containsItem(23353, 1)) {
					player.getInventory().deleteItem(6685, 1);
					player.getInventory().deleteItem(23353, 1);
					player.getInventory().addItem(6687, 1);
					player.getInventory().addItem(23351, 1);
					return;
				}
			}
			// Sara (4) into Sara Flask (4)
			if (usedWith.getId() == 6685 || usedWith.getId() == 23355) {
				if (player.getInventory().containsItem(6685, 1) && player.getInventory().containsItem(23355, 1)) {
					player.getInventory().deleteItem(6685, 1);
					player.getInventory().deleteItem(23355, 1);
					player.getInventory().addItem(6689, 1);
					player.getInventory().addItem(23351, 1);
					return;
				}
			}
			// Sara (4) into Sara (3)
			if (usedWith.getId() == 6685 || usedWith.getId() == 23357) {
				if (player.getInventory().containsItem(6685, 1) && player.getInventory().containsItem(23357, 1)) {
					player.getInventory().deleteItem(6685, 1);
					player.getInventory().deleteItem(23357, 1);
					player.getInventory().addItem(6691, 1);
					player.getInventory().addItem(23351, 1);
					return;
				}
			}
			// Sara (4) into Sara flask (2)
			if (usedWith.getId() == 6685 || usedWith.getId() == 23359) {
				if (player.getInventory().containsItem(6685, 1) && player.getInventory().containsItem(23359, 1)) {
					player.getInventory().deleteItem(6685, 1);
					player.getInventory().deleteItem(23359, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23351, 1);
					return;
				}
			}
			// Sara(4) into Sara Flask (1)
			if (usedWith.getId() == 6685 || usedWith.getId() == 23361) {
				if (player.getInventory().containsItem(6685, 1) && player.getInventory().containsItem(23361, 1)) {
					player.getInventory().deleteItem(6685, 1);
					player.getInventory().deleteItem(23361, 1);
					player.getInventory().addItem(229, 1);
					player.getInventory().addItem(23353, 1);
					return;
				}
			}
			// Super restore (4)
			if (usedWith.getId() == 3024 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(3024, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(3024, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23403, 1);
					return;
				}
			}
			// Super restore (3)
			if (usedWith.getId() == 3026 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(3026, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(3026, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23405, 1);
					return;
				}
			}
			// Super restore (2)
			if (usedWith.getId() == 3028 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(3028, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(3028, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23407, 1);
					return;
				}
			}
			// Super restore (1)
			if (usedWith.getId() == 3030 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(3030, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(3030, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23409, 1);
					return;
				}
			}
			// Super restore (1) into flask (1)
			if (usedWith.getId() == 3030 || usedWith.getId() == 23409) {
				if (player.getInventory().containsItem(3030, 1) && player.getInventory().containsItem(23409, 1)) {
					player.getInventory().deleteItem(3030, 1);
					player.getInventory().deleteItem(23409, 1);
					player.getInventory().addItem(23407, 1);
					return;
				}
			}
			// Super restore (2) into flask (1)
			if (usedWith.getId() == 3028 || usedWith.getId() == 23409) {
				if (player.getInventory().containsItem(3028, 1) && player.getInventory().containsItem(23409, 1)) {
					player.getInventory().deleteItem(3028, 1);
					player.getInventory().deleteItem(23409, 1);
					player.getInventory().addItem(23405, 1);
					return;
				}
			}
			// Super restore (3) into flask (1)
			if (usedWith.getId() == 3026 || usedWith.getId() == 23409) {
				if (player.getInventory().containsItem(3026, 1) && player.getInventory().containsItem(23409, 1)) {
					player.getInventory().deleteItem(3026, 1);
					player.getInventory().deleteItem(23409, 1);
					player.getInventory().addItem(23403, 1);
					return;
				}
			}
			// Super restore (4) into flask (1)
			if (usedWith.getId() == 3024 || usedWith.getId() == 23409) {
				if (player.getInventory().containsItem(3024, 1) && player.getInventory().containsItem(23409, 1)) {
					player.getInventory().deleteItem(3024, 1);
					player.getInventory().deleteItem(23409, 1);
					player.getInventory().addItem(23401, 1);
					return;
				}
			}
			// Super restore (1) into flask (2)
			if (usedWith.getId() == 3030 || usedWith.getId() == 23407) {
				if (player.getInventory().containsItem(3030, 1) && player.getInventory().containsItem(23407, 1)) {
					player.getInventory().deleteItem(3030, 1);
					player.getInventory().deleteItem(23407, 1);
					player.getInventory().addItem(23405, 1);
					return;
				}
			}
			// Super restore (2) into flask (2)
			if (usedWith.getId() == 3028 || usedWith.getId() == 23407) {
				if (player.getInventory().containsItem(3028, 1) && player.getInventory().containsItem(23407, 1)) {
					player.getInventory().deleteItem(3028, 1);
					player.getInventory().deleteItem(23407, 1);
					player.getInventory().addItem(23403, 1);
					return;
				}
			}
			// Super restore (3) into flask (2)
			if (usedWith.getId() == 3026 || usedWith.getId() == 23407) {
				if (player.getInventory().containsItem(3026, 1) && player.getInventory().containsItem(23407, 1)) {
					player.getInventory().deleteItem(3026, 1);
					player.getInventory().deleteItem(23407, 1);
					player.getInventory().addItem(23401, 1);
					return;
				}
			}
			// Super restore (4) into flask (2)
			if (usedWith.getId() == 3024 || usedWith.getId() == 23407) {
				if (player.getInventory().containsItem(3024, 1) && player.getInventory().containsItem(23407, 1)) {
					player.getInventory().deleteItem(3024, 1);
					player.getInventory().deleteItem(23407, 1);
					player.getInventory().addItem(23399, 1);
					return;
				}
			}
			// Super restore (1) into flask (3)
			if (usedWith.getId() == 3030 || usedWith.getId() == 23405) {
				if (player.getInventory().containsItem(3030, 1) && player.getInventory().containsItem(23405, 1)) {
					player.getInventory().deleteItem(3030, 1);
					player.getInventory().deleteItem(23405, 1);
					player.getInventory().addItem(23403, 1);
					return;
				}
			}
			// Super restore (2) into flask (3)
			if (usedWith.getId() == 3028 || usedWith.getId() == 23405) {
				if (player.getInventory().containsItem(3028, 1) && player.getInventory().containsItem(23405, 1)) {
					player.getInventory().deleteItem(3028, 1);
					player.getInventory().deleteItem(23405, 1);
					player.getInventory().addItem(23401, 1);
					return;
				}
			}
			// Super restore (3) into flask (3)
			if (usedWith.getId() == 3026 || usedWith.getId() == 23405) {
				if (player.getInventory().containsItem(3026, 1) && player.getInventory().containsItem(23405, 1)) {
					player.getInventory().deleteItem(3026, 1);
					player.getInventory().deleteItem(23405, 1);
					player.getInventory().addItem(23399, 1);
					return;
				}
			}
			// Super restore (1) into flask (4)
			if (usedWith.getId() == 3030 || usedWith.getId() == 23403) {
				if (player.getInventory().containsItem(3030, 1) && player.getInventory().containsItem(23403, 1)) {
					player.getInventory().deleteItem(3030, 1);
					player.getInventory().deleteItem(23403, 1);
					player.getInventory().addItem(23401, 1);
					return;
				}
			}
			// Super restore (2) into flask (4)
			if (usedWith.getId() == 3028 || usedWith.getId() == 23403) {
				if (player.getInventory().containsItem(3028, 1) && player.getInventory().containsItem(23403, 1)) {
					player.getInventory().deleteItem(3028, 1);
					player.getInventory().deleteItem(23403, 1);
					player.getInventory().addItem(23399, 1);
					return;
				}
			}
			// Super restore (1) into flask (5)
			if (usedWith.getId() == 3030 || usedWith.getId() == 23401) {
				if (player.getInventory().containsItem(3030, 1) && player.getInventory().containsItem(23401, 1)) {
					player.getInventory().deleteItem(3030, 1);
					player.getInventory().deleteItem(23401, 1);
					player.getInventory().addItem(23399, 1);
					return;
				}
			}
			// Prayer renewal (1)
			if (usedWith.getId() == 21636 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(21636, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(21636, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23619, 1);
					return;
				}
			}
			// Prayer renewal (2)
			if (usedWith.getId() == 21634 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(21634, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(21634, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23617, 1);
					return;
				}
			}
			// Prayer renewal (3)
			if (usedWith.getId() == 21632 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(21632, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(21632, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23615, 1);
					return;
				}
			}
			// Prayer renewal (4)
			if (usedWith.getId() == 21630 || usedWith.getId() == 23191) {
				if (player.getInventory().containsItem(21630, 1) && player.getInventory().containsItem(23191, 1)) {
					player.getInventory().deleteItem(21630, 1);
					player.getInventory().deleteItem(23191, 1);
					player.getInventory().addItem(23613, 1);
					return;
				}
			}
			// Prayer renewal (1) into flask (1)
			if (usedWith.getId() == 21636 || usedWith.getId() == 23619) {
				if (player.getInventory().containsItem(21636, 1) && player.getInventory().containsItem(23619, 1)) {
					player.getInventory().deleteItem(21636, 1);
					player.getInventory().deleteItem(23619, 1);
					player.getInventory().addItem(23617, 1);
					return;
				}
			}
			// Prayer renewal (2) into flask (1)
			if (usedWith.getId() == 21634 || usedWith.getId() == 23619) {
				if (player.getInventory().containsItem(21634, 1) && player.getInventory().containsItem(23619, 1)) {
					player.getInventory().deleteItem(21634, 1);
					player.getInventory().deleteItem(23619, 1);
					player.getInventory().addItem(23615, 1);
					return;
				}
			}
			// Prayer renewal (3) into flask (1)
			if (usedWith.getId() == 21632 || usedWith.getId() == 23619) {
				if (player.getInventory().containsItem(21632, 1) && player.getInventory().containsItem(23619, 1)) {
					player.getInventory().deleteItem(21632, 1);
					player.getInventory().deleteItem(23619, 1);
					player.getInventory().addItem(23613, 1);
					return;
				}
			}
			// Prayer renewal (4) into flask (1)
			if (usedWith.getId() == 21630 || usedWith.getId() == 23619) {
				if (player.getInventory().containsItem(21630, 1) && player.getInventory().containsItem(23619, 1)) {
					player.getInventory().deleteItem(21630, 1);
					player.getInventory().deleteItem(23619, 1);
					player.getInventory().addItem(23611, 1);
					return;
				}
			}
			// Prayer renewal (1) into flask (2)
			if (usedWith.getId() == 21636 || usedWith.getId() == 23617) {
				if (player.getInventory().containsItem(21636, 1) && player.getInventory().containsItem(23617, 1)) {
					player.getInventory().deleteItem(21636, 1);
					player.getInventory().deleteItem(23617, 1);
					player.getInventory().addItem(23615, 1);
					return;
				}
			}
			// Prayer renewal (2) into flask (2)
			if (usedWith.getId() == 21634 || usedWith.getId() == 23617) {
				if (player.getInventory().containsItem(21634, 1) && player.getInventory().containsItem(23617, 1)) {
					player.getInventory().deleteItem(21634, 1);
					player.getInventory().deleteItem(23617, 1);
					player.getInventory().addItem(23613, 1);
					return;
				}
			}
			// Prayer renewal (3) into flask (2)
			if (usedWith.getId() == 21632 || usedWith.getId() == 23617) {
				if (player.getInventory().containsItem(21632, 1) && player.getInventory().containsItem(23617, 1)) {
					player.getInventory().deleteItem(21632, 1);
					player.getInventory().deleteItem(23617, 1);
					player.getInventory().addItem(23611, 1);
					return;
				}
			}
			// Prayer renewal (4) into flask (2)
			if (usedWith.getId() == 21630 || usedWith.getId() == 23617) {
				if (player.getInventory().containsItem(21630, 1) && player.getInventory().containsItem(23617, 1)) {
					player.getInventory().deleteItem(21630, 1);
					player.getInventory().deleteItem(23617, 1);
					player.getInventory().addItem(23609, 1);
					return;
				}
			}
			// Prayer renewal (1) into flask (3)
			if (usedWith.getId() == 21636 || usedWith.getId() == 23615) {
				if (player.getInventory().containsItem(21636, 1) && player.getInventory().containsItem(23615, 1)) {
					player.getInventory().deleteItem(21636, 1);
					player.getInventory().deleteItem(23615, 1);
					player.getInventory().addItem(23613, 1);
					return;
				}
			}
			// Prayer renewal (2) into flask (3)
			if (usedWith.getId() == 21634 || usedWith.getId() == 23615) {
				if (player.getInventory().containsItem(21634, 1) && player.getInventory().containsItem(23615, 1)) {
					player.getInventory().deleteItem(21634, 1);
					player.getInventory().deleteItem(23615, 1);
					player.getInventory().addItem(23611, 1);
					return;
				}
			}
			// Prayer renewal (3) into flask (3)
			if (usedWith.getId() == 21632 || usedWith.getId() == 23615) {
				if (player.getInventory().containsItem(21632, 1) && player.getInventory().containsItem(23615, 1)) {
					player.getInventory().deleteItem(21632, 1);
					player.getInventory().deleteItem(23615, 1);
					player.getInventory().addItem(23609, 1);
					return;
				}
			}
			// Prayer renewal (1) into flask (4)
			if (usedWith.getId() == 21636 || usedWith.getId() == 23613) {
				if (player.getInventory().containsItem(21636, 1) && player.getInventory().containsItem(23613, 1)) {
					player.getInventory().deleteItem(21636, 1);
					player.getInventory().deleteItem(23613, 1);
					player.getInventory().addItem(23611, 1);
					return;
				}
			}
			// Prayer renewal (2) into flask (4)
			if (usedWith.getId() == 21634 || usedWith.getId() == 23613) {
				if (player.getInventory().containsItem(21634, 1) && player.getInventory().containsItem(23613, 1)) {
					player.getInventory().deleteItem(21634, 1);
					player.getInventory().deleteItem(23613, 1);
					player.getInventory().addItem(23609, 1);
					return;
				}
			}
			// Prayer renewal (1) into flask (5)
			if (usedWith.getId() == 21636 || usedWith.getId() == 23611) {
				if (player.getInventory().containsItem(21636, 1) && player.getInventory().containsItem(23611, 1)) {
					player.getInventory().deleteItem(21636, 1);
					player.getInventory().deleteItem(23611, 1);
					player.getInventory().addItem(23609, 1);
					return;
				}
			}
			if (FlaskDecanting.mixPot(player, fromItem, toItem, fromSlot, toSlot))
				return;
			if (PotionDecanting.mixPot(player, fromItem, toItem, fromSlot, toSlot))
				return;
			if (PolyporeDungeon.handleItemOnItem(player, itemUsed, usedWith))
				return;
			if (itemUsed.getId() > 0 && usedWith.getId() > 0) {
				int herbloreVersa = Herblore.isHerbloreSkill(usedWith, itemUsed);
				if (herbloreVersa > -1) {
					player.getDialogueManager().startDialogue("HerbloreD", herbloreVersa, itemUsed, usedWith);
					return;
				}
			}
			if (itemUsed.getId() == LeatherCrafting.NEEDLE.getId()
					|| usedWith.getId() == LeatherCrafting.NEEDLE.getId()) {
				if (LeatherCrafting.handleItemOnItem(player, itemUsed, usedWith))
					return;
			}
			if (Firemaking.isFiremaking(player, itemUsed, usedWith))
				return;
			else if (contains(1755, Gem.OPAL.getUncut(), itemUsed, usedWith))
				GemCutting.cut(player, Gem.OPAL);
			else if (contains(1755, Gem.JADE.getUncut(), itemUsed, usedWith))
				GemCutting.cut(player, Gem.JADE);
			else if (contains(1755, Gem.RED_TOPAZ.getUncut(), itemUsed, usedWith))
				GemCutting.cut(player, Gem.RED_TOPAZ);
			else if (contains(1755, Gem.SAPPHIRE.getUncut(), itemUsed, usedWith))
				GemCutting.cut(player, Gem.SAPPHIRE);
			else if (contains(1755, Gem.EMERALD.getUncut(), itemUsed, usedWith))
				GemCutting.cut(player, Gem.EMERALD);
			else if (contains(1755, Gem.RUBY.getUncut(), itemUsed, usedWith))
				GemCutting.cut(player, Gem.RUBY);
			else if (contains(1755, Gem.DIAMOND.getUncut(), itemUsed, usedWith))
				GemCutting.cut(player, Gem.DIAMOND);
			else if (contains(1755, Gem.DRAGONSTONE.getUncut(), itemUsed, usedWith))
				GemCutting.cut(player, Gem.DRAGONSTONE);
			else if (contains(1755, Gem.ONYX.getUncut(), itemUsed, usedWith))
				GemCutting.cut(player, Gem.ONYX);
			else if (contains(1755, Gem.HYDRIX.getUncut(), itemUsed, usedWith))
				GemCutting.cut(player, Gem.HYDRIX);
			else if (contains(1755, BoltTips.OPAL.getgemId(), itemUsed, usedWith))
				BoltTipFletching.boltFletch(player, BoltTips.OPAL);
			else if (contains(1755, BoltTips.JADE.getgemId(), itemUsed, usedWith))
				BoltTipFletching.boltFletch(player, BoltTips.JADE);
			else if (contains(1755, BoltTips.RED_TOPAZ.getgemId(), itemUsed, usedWith))
				BoltTipFletching.boltFletch(player, BoltTips.RED_TOPAZ);
			else if (contains(1755, BoltTips.SAPPHIRE.getgemId(), itemUsed, usedWith))
				BoltTipFletching.boltFletch(player, BoltTips.SAPPHIRE);
			else if (contains(1755, BoltTips.EMERALD.getgemId(), itemUsed, usedWith))
				BoltTipFletching.boltFletch(player, BoltTips.EMERALD);
			else if (contains(1755, BoltTips.RUBY.getgemId(), itemUsed, usedWith))
				BoltTipFletching.boltFletch(player, BoltTips.RUBY);
			else if (contains(1755, BoltTips.DIAMOND.getgemId(), itemUsed, usedWith))
				BoltTipFletching.boltFletch(player, BoltTips.DIAMOND);
			else if (contains(1755, BoltTips.DRAGONSTONE.getgemId(), itemUsed, usedWith))
				BoltTipFletching.boltFletch(player, BoltTips.DRAGONSTONE);
			else if (contains(1755, BoltTips.ONYX.getgemId(), itemUsed, usedWith))
				BoltTipFletching.boltFletch(player, BoltTips.ONYX);
			else if (contains(1755, BoltTips.HYDRIX.getgemId(), itemUsed, usedWith))
				BoltTipFletching.boltFletch(player, BoltTips.HYDRIX);
			else

			if (Settings.DEBUG)
				Logger.log("ItemHandler", "Used:" + itemUsed.getId() + ", With:" + usedWith.getId());
		}

	}

	public static void handleItemOnNPC(final Player player, final NPC npc, final Item item) {
		player.setRouteEvent(new RouteEvent(npc, new Runnable() {
			@Override
			public void run() {
				if (!player.getInventory().containsItem(item.getId(), item.getAmount())) {
					return;
				}
				if (!player.getNewQuestManager().processItemOnNPC(npc, item))
					return;
				if (npc instanceof Block) {
					Block block = (Block) npc;
					if (!block.useItem(player, item)) {
						return;
					}
				}
				if (npc instanceof ConditionalDeath)
					((ConditionalDeath) npc).useHammer(player);
				if (npc.getId() == 15085) {// TODO
					player.faceEntity(npc);
					npc.faceEntity(player);
					player.getDialogueManager().startDialogue("SpinRewardExchanger", 15085, item);
					return;
				}
				if (npc.getId() == 519) {
					player.faceEntity(npc);
					npc.faceEntity(player);
					if (item.getId() == 20120) {
						if (player.getFrozenKeyCharges() < 10) {
							int amount = 4000000 - (player.getSkills().getLevelForXp(Skills.SMITHING) * 40000);
							if (player.hasMoney(amount)) {
								player.takeMoney(amount);
								player.setFrozenKeyCharges((byte) 10);
								player.getDialogueManager().startDialogue("SimpleNPCMessage", 519,
										"I've recharged your Frozen key for " + Utils.getFormattedNumber(amount)
												+ " coins.");
								return;
							}
							player.getDialogueManager().startDialogue("SimpleNPCMessage", 519, "You will need "
									+ Utils.getFormattedNumber(amount) + " coins " + "to recharge your Frozen key.");
							return;
						}
						player.getDialogueManager().startDialogue("SimpleNPCMessage", 519,
								"Your Frozen key is already fully charged.");
						return;
					}

					if (BrokenItems.forId(item.getId()) == null) {
						player.getDialogueManager().startDialogue("SimpleNPCMessage", 519,
								"I can't repair this item for you.");
						return;
					}
					player.getDialogueManager().startDialogue("BobRepair", npc.getId(), item);
					return;
				}
				if (npc.getId() == 18891) {
					if (PortArmor.forId(item.getId()) == null) {
						player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
								"This item does not have a superior version.");
						return;
					}
					player.getDialogueManager().startDialogue("SuperiorExchangeD", npc.getId(), item);
					return;
				}
				if (npc.getId() == 5956 && item.getId() == 19711) {
					player.getDialogueManager().startDialogue(new Dialogue() {

						@Override
						public void start() {
							sendItemDialogue(item.getId(), 1, "You trade in your non-working defender for a new one!");
							stage = 0;
						}

						@Override
						public void run(int interfaceId, int componentId) {
							switch (stage) {
							case 0:
								player.getInventory().deleteItem(19711, 1);
								player.getInventory().addItem(new Item(19712));
								finish();
							}

						}

						@Override
						public void finish() {
							player.getInterfaceManager().closeChatBoxInterface();
						}

					});
				}
				if (npc.getDefinitions().name.contains("ool leprech")) {
					for (int produceId : FarmingManager.PRODUCES) {
						if (produceId == item.getId()) {
							int num = player.getInventory().getNumberOf(produceId);
							player.getInventory().deleteItem(produceId, num);
							player.getInventory()
									.addItem(new Item(ItemDefinitions.getItemDefinitions(produceId).getCertId(), num));
							player.sendMessage("The Tool Leprechaun has noted some products for you.");
							return;
						}
					}
					player.sendMessage("The leprechaun can not note that item for you.");
				}
				if (npc instanceof Pet) {
					player.faceEntity(npc);
					player.getPetManager().eat(item.getId(), (Pet) npc);
					return;
				}
			}
		}));
	}

	public static void handleItemOnPlayer(final Player player, final Player usedOn, final int itemId) {
		long time = Utils.currentTimeMillis();
		if (usedOn == player)
			return;
		if (player.getLockDelay() >= time || player.getEmotesManager().getNextEmoteEnd() >= time)
			return;
		player.setRouteEvent(new RouteEvent(usedOn, new Runnable() {
			@Override
			public void run() {
				player.faceEntity(usedOn);

				if (usedOn.getInterfaceManager().containsScreenInter()) {
					player.sendMessage(usedOn.getDisplayName() + " is busy.");
					return;
				}
				if (player.getAttackedByDelay() + 10000 > Utils.currentTimeMillis()) {
					player.sendMessage("You can't do this during combat.");
					return;
				}
				if (usedOn.getAttackedByDelay() + 10000 > Utils.currentTimeMillis()) {
					player.sendMessage("You cannot send a request to a player in combat.");
					return;
				}
				switch (itemId) {
				case 962:

					if (player.getInventory().getFreeSlots() < 3 || usedOn.getInventory().getFreeSlots() < 3) {
						player.sendMessage(
								(player.getInventory().getFreeSlots() < 3 ? "You do" : "The other player does")
										+ " not have enough inventory space to open this cracker.");
						return;
					}
					if (!player.getInventory().containsOneItem(962))
						return;
					if (player.getUsername().equalsIgnoreCase("youtube"))
						return;
					player.getDialogueManager().startDialogue("ChristmasCrackerD", usedOn, itemId);
					break;
				case 20083:
					if (!player.getInventory().containsOneItem(20083))
						return;
					player.getInventory().deleteItem(20083, 1);
					player.getInventory().addItem(20084, 1);
					usedOn.faceEntity(player);
					player.setNextAnimation(new Animation(15153));
					usedOn.setNextAnimation(new Animation(15153));
					break;
				case 4155:
				case 13263:
				case 13281:
				case 13282:
				case 13283:
				case 13284:
				case 13285:
				case 13286:
				case 13287:
				case 13288:
					if (!player.getInventory().containsOneItem(itemId))
						return;
					player.getSlayerManager().invitePlayer(usedOn);
					break;
				default:
					player.sendMessage("Nothing interesting happens.");
					break;
				}
			}
		}));
	}

	private static int[] eggBelowCommonDrop = {
			// sof key
			24154,
			// ovl
			26750,
			// sets
			10286, 19179, 19185, 7336, 10288, 19200, 19203, 7342, 10288, 19200, 19206, 7342, 10290, 19221, 19224, 7348,
			10290, 19221, 19227, 7348, 10292, 19242, 19245, 7354, 10292, 19242, 19248, 7354, 10294, 19263, 19266, 7360,
			2627, 2623, 2625, 2629, 2627, 2623, 3477, 2629, 2619, 2615, 2617, 2621, 2619, 2615, 3672, 2621, 2673, 2669,
			2671, 2675, 2673, 2669, 3480, 2675, 2665, 2661, 2663, 2667, 2665, 2661, 3479, 2667, 2657, 2653, 2657, 2653,
			3478, 2659, 2655, 2659 };

	private static int[] eggCommonDrop = {
			// ckey
			989,
			// sof key
			24154,
			// vote box
			11640,
			// barrows
			4708, 4710, 4712, 4714, 4716, 4718, 4720, 4722, 4724, 4726, 4728, 4730, 4732, 4734, 4736, 4738, 4745, 4747,
			4749, 4751, 4753, 4755, 4757, 4759,
			// revs
			13908, 13911, 13914, 13917, 13920, 13923, 13941, 13944, 13947, 13950, 13938, 13926, 13929, 13932, 13935,
			// void
			11676, 11675, 11674, 8839, 8840, 8842, 8841, 19712, 11666 };
	private static int[] eggRareDrop = {
			// elitevoid
			19785, 19786,
			// gwd
			11724, // bandos chestplate
			11726, // bandos tassets
			25022, // bandos helmet
			11720, // armadyl chestplate
			25013, // armadyl buckler
			25010, // armadyl boots
			25016, // arma gloves
			// basket of eggs
			4565 };
	private static int[] eggSemiRareDrop = {
			// bunny ears
			1037,

	};
	private static int[] eggSuperRareDrop = {
			// phats
			1038, 1040, 1042, 1044, 1046, 1048 };

	private static void eatingEasterEgg(Player player, int itemId, int slotid) {
		int BelowCommonPrize = eggBelowCommonDrop[Utils.random(eggBelowCommonDrop.length)];
		int CommonPrize = eggCommonDrop[Utils.random(eggCommonDrop.length)];
		int RarePrize = eggRareDrop[Utils.random(eggRareDrop.length)];
		int SemiRarePrize = eggSemiRareDrop[Utils.random(eggSemiRareDrop.length)];
		int LegendPrize = eggSuperRareDrop[Utils.random(eggSuperRareDrop.length)];
		int rng = Utils.random(100);
		int classifier = 0;
		player.getInventory().deleteItem(slotid, new Item(itemId, 1));
		if (rng >= 0 && rng <= 70) {
			classifier = 0;
			player.getInventory().addItem(BelowCommonPrize, 1);
		} else if (rng > 70 && rng <= 90) {
			classifier = 1;
			player.getInventory().addItem(CommonPrize, 1);
		} else if (rng > 90 && rng <= 97) {
			player.getInventory().addItem(RarePrize, 1);
			classifier = 2;
		} else if (rng > 97 && rng <= 99) {
			player.getInventory().addItem(SemiRarePrize, 1);
			classifier = 3;
		} else {
			player.getInventory().addItem(LegendPrize, 1);
		}
		String name = ItemDefinitions.getItemDefinitions(itemId).getName();
		if (classifier == 0) {
			name = ItemDefinitions.getItemDefinitions(BelowCommonPrize).getName();
		} else if (classifier == 1) {
			name = ItemDefinitions.getItemDefinitions(CommonPrize).getName();
		} else if (classifier == 2) {
			name = ItemDefinitions.getItemDefinitions(RarePrize).getName();
		} else if (classifier == 3) {
			name = ItemDefinitions.getItemDefinitions(SemiRarePrize).getName();
		} else {
			name = ItemDefinitions.getItemDefinitions(LegendPrize).getName();
		}
		player.setNextAnimationForce(new Animation(player.isUnderCombat() ? 18002 : 18001));
		World.sendWorldMessage((classifier == 0 || classifier == 1 ? Colors.green
				: classifier == 2 || classifier == 3 ? Colors.cyan : Colors.purple) + "<shad=000000><img=6>EASTER: "
				+ player.getDisplayName() + " received " + name + " from easter egg! Happy Easter Everyone!", false);
	}

	public static void handleItemOption1(final Player player, final int slotId, final int itemId, Item item) {
		if (player.getLockDelay() >= Utils.currentTimeMillis()
				|| player.getEmotesManager().getNextEmoteEnd() >= Utils.currentTimeMillis())
			return;
		player.stopAll(false, true);
		if (itemId >= 7928 && itemId <= 7933 && player.getControlerManager().getControler() == null) {
			eatingEasterEgg(player, itemId, slotId);
			return;
		}
		for (AnimaCoreData data : AnimaCoreData.values()) {
			if (item.getId() == data.getMaterial()[0].getId()) {
				player.getDialogueManager().startDialogue("AnimaCoreCreationD");
				return;
			}
		}

		if (item.getId() == 744) {
			player.setHitpoints(player.getHitpoints() + 5000);
			player.refreshHitPoints();
			return;
		}

		if (item.getId() == 18337) {
			Bonecrusher.openBonecrusher(player);
			return;
		}
		if (item.getId() == 34233) {
			player.getDialogueManager().startDialogue("NewSkinsD");
			return;
		}
		if (item.getId() == 9477) {
			player.getDialogueManager().startDialogue("NewCosmeticsD");
			return;
		}
		if (item.getId() == 10025) {
			if (!player.getInventory().hasFreeSlots()) {
				player.sm(Colors.red + "You don't have inventory slot to open this box!");
				return;
			}
			if (!player.getInventory().containsItem(10025, 1)) {
				player.sm(Colors.red + "You don't have the Arrow box to open!");
				return;
			}
			if (player.getInventory().containsItem(10025, 1)) {
				player.getInventory().deleteItem(10025, 1);
				player.getInventory().addItem(16477, 25);
			}

		}
		if (item.getId() == 19675) {
			Herbicide.openHerbicide(player);
			return;
		}
		if (item.getId() == 34354) {
			int[] Common = { 29441, 29441, 29441, 29441, 29442, 35886, 34836, 28566, 24154, 24154, 24154, 24154, 24154,
					14529, 14531, 11842, 11844, 29441, 29441, 29441 };
			int[] Rare = { 6739, 15259, 13661, 11846, 11848, 11850, 11852, 11854, 11856, 25758 };
			int[] Legendary = { 1053, 1055, 1057, 1506, 2631, 3057, 4164, 6188, 6335, 7003 };
			player.setNextGraphics(new Graphics(184));
			player.getInventory().deleteItem(34354, 1);
			player.sm(Colors.red + "You Opened 1 Mystery Gift and received 1 Halloween Points. ");
			player.setHweenPoints(player.getHweenPoints() + 1);
			int rarity = Utils.getRandom(1000);
			if (rarity > 0 && rarity <= 800) {
				int length = Common.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Common[reward], 1);
				World.sendWorldMessage("<col=fff000>" + player.getDisplayName()
						+ " recieved a Common Item from the Mystery Gift from Halloween Event!", false);
			}
			if (rarity > 800 && rarity <= 990) {
				int length = Rare.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Rare[reward], 1);
				World.sendWorldMessage("<col=ff00ff>" + player.getDisplayName()
						+ " recieved a Rare Item from the Mystery Gift from Halloween Event!", false);
			}
			if (rarity > 990 && rarity <= 1000) {
				int length = Legendary.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Legendary[reward], 1);
				World.sendWorldMessage("<col=ff00ff><shad=ff0000>" + player.getDisplayName()
						+ " recieved a Legendary Item from the Mystery Gift!", false);
			}

			return;
		}
		if (itemId == 27360) {
			if (!player.getInventory().containsItem(27360, 1)) {
				return;
			}
			player.getDialogueManager().startDialogue("ResetTask");
			return;
		}
		if (item.getDefinitions().containsInventoryOption(0, "Drop")) {
			handleItemOption7(player, slotId, itemId, item);
			return;
		}
		if (itemId == 40188 || itemId == 40191) {// experience chest
			player.getDialogueManager().startDialogue("QuadExp", itemId);
			return;
		}
		if (Foods.eat(player, item, slotId))
			return;
		if (Fletching.isFletching(player, itemId))
			return;
		else if (LividFarm.handleItemOption1(player, item))
			return;
		if (itemId == 4079) {
			player.setNextAnimationNoPriority(new Animation(1457));
			return;
		}

		/**
		 * Christmas gift
		 */

		if (itemId == 33610) {
			int[] Coal = { 453 };

			int[] Common = { 41517, 41518, 41519, 41520, 41521, 41524, 41525, 39273, 39275, 33593, 33594, 33595, 26493,
					15422, 15423, 15425 };

			int[] Rare = { 26518, 14600, 14602, 14604, 33625, 39265, 39269 };

			int[] Pets = { 41526, 41538, 41559 };

			int[] Legendary = { 36079, 36080 };

			int[] Legendary1 = { 30412 };

			player.getInventory().deleteItem(33610, 1);
			int rarity = Utils.getRandom(1000);

			if (rarity > 0 && rarity <= 250) {
				int length = Coal.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Coal[reward], 1);
				World.sendWorldMessage(
						"<col=fff000>" + player.getDisplayName() + "Was a Naughty kid and received coal! ", false);
			}

			if (rarity > 250 && rarity <= 750) {
				int length = Common.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Common[reward], 1);
				World.sendWorldMessage(
						"<col=fff000>" + player.getDisplayName() + " recieved a Common Item from the christmas gift!",
						false);
			}
			if (rarity > 750 && rarity <= 920) {
				int length = Rare.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Rare[reward], 1);
				World.sendWorldMessage(
						"<col=ff00ff>" + player.getDisplayName() + " recieved a Rare Item from the christmas gift!",
						false);
			}
			if (rarity > 920 && rarity <= 980) {
				int length = Pets.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Pets[reward], 1);
				World.sendWorldMessage(
						"<col=ff00ff>" + player.getDisplayName() + " recieved a PET from the christmas gift!", false);

			}
			if (rarity > 980 && rarity <= 995) {
				int length = Legendary.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Legendary[reward], 1);
				World.sendWorldMessage("<col=ff00ff><shad=ff0000>" + player.getDisplayName()
						+ " recieved a Legendary item from the christmas gift!", false);
			}
			if (rarity > 995 && rarity <= 1000) {
				int length = Legendary1.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Legendary[reward], 1);
				World.sendWorldMessage("<col=ff00ff><shad=ff0000>" + player.getDisplayName()
						+ " recieved a black santa hat from the christmas gift!", false);
			}

			return;
		}

		/**
		 * Hourly reward Box
		 */
		if (itemId == 43410) {
			int[] Common = { 1149, 1187, 1215, 1305, 1434, 2513, 3204, 4087, 4585, 4587, 6739, 15332, 15328, 23713,
					23717, 23721, 23725, 23729, 23737, 23733, 23741, 23745, 23749, 23753, 23757, 23761, 23765, 23769,
					23778, 23774, 23786, 23782, 23794, 23790, 23802, 23798, 23810, 23806, 23814, 23714, 23718, 23722,
					23726, 23730, 23738, 23734, 23742, 23746, 23750, 23754, 23758, 23762, 23766, 23770, 23779, 23775,
					23787, 23783, 23795, 23791, 23803, 23799, 23811, 23807, 23815, 23715, 23719, 23723, 23727, 23731,
					23739, 23735, 23743, 23747, 23751, 23755, 23759, 23763, 23767, 23771, 23780, 23776, 23788, 23784,
					23796, 23792, 23804, 23800, 23812, 23808, 23816

			};

			int[] Rare = { 43416, 43418, 43420, 43422, 43424, 43426, 43428, 43430, 43432, 43434, 43436, 34896, 42706,
					42707, 42708, 42709, 42710, 42711, 42712, 42713, 42714, 42715, 42716, 42717, 42718, 42719, 42720,
					42721, 31041, 23691, 23684, 23685, 23686, 23687, 23688, 23689, 23690, 23683, 23692, 23693, 23694,
					23675, 23676, 23677, 23678, 23673 };

			int[] Pets = { 42722, 42725, 43293, 43292, 42743 };

			int[] Legendary = { 29494 };

			int[] Legendary1 = { 29492 };

			player.getInventory().deleteItem(43410, 1);
			int rarity = Utils.getRandom(1000);
			if (rarity > 0 && rarity <= 800) {
				int length = Common.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Common[reward], 1);
				World.sendWorldMessage(
						"<col=fff000>" + player.getDisplayName() + " recieved a Common Item from the Loyality Box!",
						false);
			}
			if (rarity > 800 && rarity <= 950) {
				int length = Rare.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Rare[reward], 1);
				Discord.sendAchievement(player.getDisplayName() + " recieved a Rare Item from the Loyality Box!");
				World.sendWorldMessage(
						"<col=ff00ff>" + player.getDisplayName() + " recieved a Rare Item from the Loyality Box!",
						false);
			}
			if (rarity > 950 && rarity <= 990) {
				int length = Pets.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Pets[reward], 1);
				Discord.sendAchievement(player.getDisplayName() + " recieved a PET from the Loyality Box!");
				World.sendWorldMessage(
						"<col=ff00ff>" + player.getDisplayName() + " recieved a PET from the Loyality Box!", false);

			}
			if (rarity > 990 && rarity <= 998) {
				int length = Legendary.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Legendary[reward], 1);
				Discord.sendAchievement(
						player.getDisplayName() + " recieved a untradeable Bond from the Loyality Box!");
				World.sendWorldMessage("<col=ff00ff><shad=ff0000>" + player.getDisplayName()
						+ " recieved a untradeable Bond from the Loyality Box!", false);
			}
			if (rarity > 998 && rarity <= 1000) {
				int length = Legendary1.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Legendary[reward], 1);
				Discord.sendAchievement(player.getDisplayName() + " recieved a Tradeable Bond from the Loyality Box!");
				World.sendWorldMessage("<col=ff00ff><shad=ff0000>" + player.getDisplayName()
						+ " recieved a Bond from the Loyality Box!", false);
			}

			return;
		}

		// PegLegs
		if (itemId == 32108) {
			int[] Common = { 32102, 32103, 32104, 32105, 32106, 32107, 2651, 2997, 7110, 7112, 7114, 7116, 7122, 7124,
					7126 };

			player.getInventory().deleteItem(32108, 1);
			int rarity = Utils.getRandom(1000);
			if (rarity > 0 && rarity <= 1000) {
				int length = Common.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Common[reward], 1);

			}
			return;
		}

		if (itemId == 6199) {
			int[] Common = { 29441, 29441, 29441, 29441, 29442, 35886, 34836, 28566, 24154, 24154, 24154, 24154, 24154,
					14529, 14531, 11842, 11844, 29441, 29441, 29441 };
			int[] Rare = { 600, 6739, 15259, 13661, 11846, 11848, 11850, 11852, 11854, 11856, 25758

			};
			int[] Legendary = { 28828,
					// 34785, alchemy pet FIRE_DRAKE
					34786, 26569,
					// 26568, receive pet skypouncer
					28833, 30368, 27215, };
			player.getInventory().deleteItem(6199, 1);
			int rarity = Utils.getRandom(1000);
			if (rarity > 0 && rarity <= 800) {
				int length = Common.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Common[reward], 1);
				World.sendWorldMessage(
						"<col=fff000>" + player.getDisplayName() + " recieved a Common Item from the Mystery Box!",
						false);
			}
			if (rarity > 800 && rarity <= 990) {
				int length = Rare.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Rare[reward], 1);
				World.sendWorldMessage(
						"<col=ff00ff>" + player.getDisplayName() + " recieved a Rare Item from the Mystery Box!",
						false);
			}
			if (rarity > 990 && rarity <= 1000) {
				int length = Legendary.length;
				length--;
				int reward = Utils.getRandom(length);
				player.getInventory().addItem(Legendary[reward], 1);
				Discord.sendAchievement(player.getDisplayName() + " recieved a Legendary Item from the Mystery Box!");
				World.sendWorldMessage("<col=ff00ff><shad=ff0000>" + player.getDisplayName()
						+ " recieved a Legendary Item from the Mystery Box!", false);
			}
			return;
		}
		/**
		 * ABILITY CODEX
		 */
		// Corruption Blast Ability Codex
		if (itemId == 35152) {
			player.getPerkManager().breath = true;
			player.getInventory().deleteItem(35152, 1);
			return;
		}
		if (itemId == 39584) {
			player.getPerkManager().superprayer = true;
			player.getInventory().deleteItem(39584, 1);
			return;
		}
		if (itemId == 15707) {
			player.getDungManager().openPartyInterface();
			return;
		}

		if (itemId == 29492) {
			player.getInventory().deleteItem(29492, 1);
			player.setReferralPoints(player.getReferralPoints() + 5);
			player.sm(Colors.red + "Succefully claimed your $5 Store Credit!");
			return;
		}
		if (itemId == 29494) {
			player.getInventory().deleteItem(29494, 1);
			player.setReferralPoints(player.getReferralPoints() + 5);
			player.sm(Colors.red + "Succefully claimed your bond!");
			return;
		}
		if (itemId == 42919) {
			player.getInventory().deleteItem(42919, 1);
			player.setHelwyrCoins(player.getHelwyrCoins() + 5);
			player.sm(Colors.green + "Succefully claimed your 5 Helwyr Coins! </col>" + Colors.red
					+ "You can use it to buy Animation at PartyPete");
			return;
		}
		if (itemId == 34896) {
			player.getInventory().deleteItem(34896, 1);
			player.setReferralPoints(player.getReferralPoints() + 1);
			player.sm(Colors.red
					+ "Succefully claimed Store Credit!, Talk to Partypete to buy Perks, Animation, Squeal or Cosmetic Overrides");
			return;
			/**
			 * Nex minion Morph item
			 */
		}
		if (itemId == 39586) {
			player.getGlobalPlayerUpdater().transformIntoNPC(24011);
			player.setNextForceTalk(new ForceTalk("For the glory of Zaros!"));
			World.sendGraphics(player, new Graphics(5028), player);
			player.setNextAnimation(new Animation(26494));
			return;
		}
		if (itemId == 39588) {
			player.getGlobalPlayerUpdater().transformIntoNPC(24010);
			player.setNextForceTalk(new ForceTalk("For the glory of Zaros!"));
			World.sendGraphics(player, new Graphics(5028), player);
			player.setNextAnimation(new Animation(26494));
			return;
		}
		if (itemId == 39592) {
			player.getGlobalPlayerUpdater().transformIntoNPC(24012);
			player.setNextForceTalk(new ForceTalk("For the glory of Zaros!"));
			World.sendGraphics(player, new Graphics(5028), player);
			player.setNextAnimation(new Animation(26494));
			return;
		}
		if (itemId == 39590) {
			player.getGlobalPlayerUpdater().transformIntoNPC(24013);
			player.setNextForceTalk(new ForceTalk("For the glory of Zaros!"));
			World.sendGraphics(player, new Graphics(5028), player);
			player.setNextAnimation(new Animation(26494));
			return;
		}
		if (itemId == XPSharing.ITEM_ID) {
			player.getXpSharing().sendEntry();
			return;
		}
		if (item.getId() == 13663) {
			player.getPackets().sendInputNameScript("choose name for the extra bank: (you can change at anytime)");
			player.getTemporaryAttributtes().put("NAME_BANK", Boolean.TRUE);
			return;
		}
		if (item.getId() == CosmeticsHandler.KEEP_SAKE_KEY) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You currently have "
					+ player.getEquipment().getKeepSakeItems().size() + " items in your keepsake box.");
			return;
		}
		/**
		 * Portables.
		 */
		if (item.getDefinitions().containsOption("Deploy")) {
			if (itemId == 31041)
				Portables.placePortable(player, item, 97270);
			return;
		}
		// BATTLESTAFF CRAFTING
		if (Orbs.forId(itemId) != null) {
			Orbs orb = Orbs.forId(itemId);
			player.getActionManager().setAction(new BattleStaffCrafting(player, orb, itemId));
			return;
		} // coalbag
		if (itemId == 18339) {
			int coalAmount = player.getInventory().getAmountOf(453);
			if (coalAmount == 0) {
				player.sendMessage("You haven't got any coal to add to the coal bag.");
				return;
			}
			if (player.getCoal() == 100) {
				player.sendMessage("You haven't got enough space in your coal bag to add any more coal.");
				return;
			}
			if (coalAmount + player.getCoal() > 100)
				coalAmount = 100 - player.getCoal();
			player.getInventory().deleteItem(453, coalAmount);
			player.addCoal(coalAmount);
			player.sendMessage("You add " + coalAmount + " pieces of coal to your coal bag.");
			return;
		}

		// 1b bag
		// 1b bag
		if (itemId == 27155/*
							 * && !(player.getControlerManager().getControler() instanceof
							 * InstancedPVPControler)
							 */) {
			int billion = 1000000000;
			if (player.getInventory().getNumerOf(995) <= (Integer.MAX_VALUE - billion)) {
				player.getInventory().deleteItem(27155, 1);
				player.getInventory().addItem(995, billion);
				player.sendMessage("You turn your cash bag into coins!");
			} else {
				player.sendMessage("Your gp will go beyond max cash please reduce to fit the requirements");
			}
		}
		if (itemId == 24954) {
			player.getDialogueManager().startDialogue(new Dialogue() {

				@Override
				public void start() {
					sendOptionsDialogue("Godwars Bosses", "Godwars: Bandos", "Godwars: Armadyl", "Godwars: Saradomin",
							"Godwars: Zamorak", "Godwars: Nex");
					stage = 0;
				}

				@Override
				public void run(int interfaceId, int componentId) {
					if (stage == 0) {
						if (componentId == OPTION_1) { // bandos
							player.getInventory().deleteItem(24954, 1);
							BossTeleports.teleportPlayer(player, 2861, 5357, 0, "GodWars");
							// Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2861, 5357, 0));
							end();

						}
						if (componentId == OPTION_2) { // armadyl
							player.getInventory().deleteItem(24954, 1);
							BossTeleports.teleportPlayer(player, 2832, 5292, 0, "GodWars");
							// Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2832, 5292, 0));
							end();
						}
						if (componentId == OPTION_3) { // saradmin
							player.getInventory().deleteItem(24954, 1);
							BossTeleports.teleportPlayer(player, 2919, 5261, 0, "GodWars");
							// Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2919, 5261, 0));
							end();
						}
						if (componentId == OPTION_4) { // zamorak
							player.getInventory().deleteItem(24954, 1);
							BossTeleports.teleportPlayer(player, 2925, 5337, 0, "GodWars");
							// Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2925, 5337, 0));
							end();
						}
						if (componentId == OPTION_5) { // nex
							player.getInventory().deleteItem(24954, 1);
							BossTeleports.teleportPlayer(player, 2904, 5203, 0, "GodWars");
							// Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2904, 5203, 0));
							end();
						}

					}
				}

				@Override
				public void finish() {
					player.getInterfaceManager().closeChatBoxInterface();
				}

			});

		}

		// xmas stuff
		if (itemId == 744)

		{
			if (!player.getXmas().inXmas)
				return;
			if (Utils.currentTimeMillis() - player.getXmas().healTime >= 10000) {
				player.sendMessage("time diff: " + (Utils.currentTimeMillis() - player.getXmas().healTime));
				player.getXmas().healTime = Utils.currentTimeMillis();
				player.setNextAnimation(new Animation(9098));
				player.setNextGraphics(new Graphics(84));
				player.heal(150);
			} else
				player.sendMessage(Colors.salmon + "You cannot heal again yet");
			return;
		}
		if (itemId == 33611) {
			int[] rewards = { 22973, 15426, 22985, 34838 };
			player.getDialogueManager().startDialogue(new Dialogue() {

				@Override
				public void start() {
					sendItemDialogue(33611, 1, "You peek inside the box and face a difficult choice..");
					stage = 1;
				}

				@Override
				public void run(int interfaceId, int componentId) {
					switch (stage) {
					case 1:
						sendOptionsDialogue("Pick a reward!",
								Colors.check(player.hasItem(new Item(rewards[0]))) + "Sparkles",
								Colors.check(player.hasItem(new Item(rewards[1]))) + "Candy cane",
								Colors.check(player.hasItem(new Item(rewards[2]))) + "Christmas wand",
								Colors.check(player.hasItem(new Item(rewards[3])) || player.hasItem(new Item(33625))
										|| player.hasItem(new Item(33627))) + "Prismatic dye",
								"Nevermind");
						stage = 2;
						break;
					case 2:
						int item = componentId == OPTION_1 ? 0 : componentId - 12;
						switch (componentId) {
						case OPTION_1:
						case OPTION_2:
						case OPTION_3:
						case OPTION_4:
							finish();
							if (item == 3
									&& (player.hasItem(new Item(rewards[3])) || player.hasItem(new Item(33625))
											|| player.hasItem(new Item(33627)))
									|| player.hasItem(new Item(rewards[item])))
								player.sendMessage("You already have this reward! Don't waste your points!", false);
							else {
								player.getInventory().deleteItem(33611, 1);
								player.getInventory().addItem(rewards[item], 1);
							}
							break;
						case OPTION_5:
							finish();
							break;
						}
						break;
					}
				}

				@Override
				public void finish() {
					player.getInterfaceManager().closeChatBoxInterface();
				}

			});
			return;
		}
		if (itemId == 11036) {
			if (!player.getXmas().inXmas) // not even in the event
				return;

			if (player.getXmas().finishedRiddles()) { // finished all riddles
				player.sendMessage(Colors.green + "You have finished all of the available riddles!", false);
				player.getInventory().deleteItem(new Item(11036));
				return;
			}

			if (player.getXmas().riddle == null && !player.getXmas().finishedRiddles()) {
				player.sendMessage(Colors.red + "Please return to the Queen of Snow for another riddle!");
				return;
			}

			if (player.getXmas().riddle != null) // do riddle stuff
				XmasRiddles.writeInterface(player);
			return;
		}

		if (itemId == 34027) {
			player.getDialogueManager().startDialogue(new Dialogue() {

				@Override
				public void start() {
					sendItemDialogue(34027, player.getInventory().getAmountOf(34027),
							"Do you want to teleport to cosmetic store?");
					stage = 0;
				}

				@Override
				public void run(int interfaceId, int componentId) {
					switch (stage) {
					case 0:
						sendOptionsDialogue("Would you like to teleport?", "Yes", "No");
						stage = 1;
						break;
					case 1:
						finish();
						switch (componentId) {
						case OPTION_1:
							Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2326, 3177, 0));
							break;
						case OPTION_2:
							break;
						}
					}

				}

				@Override
				public void finish() {
					player.getInterfaceManager().closeChatBoxInterface();
				}

			});
		}

		if (itemId == 36156 || itemId == 36159 || itemId == 36163) {
			player.getDialogueManager().startDialogue(new Dialogue() {

				int upgrade;
				int component;
				int reward;
				int defenders[] = { 0, 0 };

				@Override
				public void start() {
					upgrade = (int) parameters[0];
					for (int i = 0; i < 2; i++)
						defenders[i] = Defenders.getDef(player, upgrade)[i];
					sendItemDialogue(upgrade, 1,
							"You look carefully over the " + new Item(upgrade).getName().toLowerCase() + ".");
					stage = -1;
					if (defenders[0] == 0 && defenders[1] == 0)
						stage = 4;
				}

				@Override
				public void run(int interfaceId, int componentId) {
					switch (stage) {
					case -1:
						sendOptionsDialogue("What type of offhand are you making?", "Melee / Defender",
								"Ranged / Repriser", "Magic / Rebounder or Lantern");
						stage = 0;
						break;
					case 0:
						switch (componentId) {
						case OPTION_1:
						case OPTION_3:
						case OPTION_2:
							int choice = componentId == OPTION_1 ? 0 : 1;
							boolean chosen = false;
							reward = Defenders.getReward(defenders[choice], choice);
							component = Defenders.getComp(defenders[choice], choice);
							if (reward == 36153) {
								for (int upgrade : Defenders.barrowsC) {
									if (player.getInventory().containsItem(upgrade, 1) && !chosen) {
										component = upgrade;
										chosen = true;
									}
								}
								if (!chosen) {
									sendItemDialogue(36156, 1, "You need a Dharok's greataxe, Verac's flail,"
											+ "Guthan's warspear, or Torag's hammer to make this!");
									stage = 3;
								}
								break;
							}
							if (reward != 0 && component != 0 && !player.getInventory().containsItem(component, 1)) {
								sendItemDialogue(component, 1,
										"You need a " + new Item(component).getName() + " to make this!");
								stage = 3;
							} else {
								if (reward != 0)
									sendItemDialogue(reward, 1,
											"You decide to make a " + new Item(reward).getName() + "!");
								stage = 1;
							}
							if (reward == 0) {
								sendItemDialogue(itemId, 1,
										"You don't have the necessary defender / repriser for this!");
								stage = 3;
							}
							break;
						}
						break;
					case 1:
						finish();
						if (player.getInventory().containsItem(component, 1)
								|| (!player.getInventory().containsItem(component, 1) && component == 0)) {
							if (component != 0)
								player.getInventory().deleteItem(component, 1);
							player.getInventory().deleteItem(itemId, 1);
							player.getInventory().addItem(reward, 1);
						} else {
							player.sendMessage(Colors.red + "You need " + Utils.getAorAn(new Item(component).getName())
									+ new Item(component).getName() + " to make this!");
							break;
						}
						break;
					case 3:
						finish();
						break;
					case 4:
						finish();
						player.sendMessage(Colors.red + "It seems you are missing the neccessary defender or repriser!",
								false);
					}
				}

				@Override
				public void finish() {
					player.getInterfaceManager().closeChatBoxInterface();
				}
			}, itemId);
		}
		if (itemId == 28566)

		{
			if (player.getInventory().getFreeSlots() >= 4) {
				player.getInventory().deleteItem(28566, 1);
				player.getInventory().addItem(26750, 50);
				player.getInventory().addItem(23352, 50);
				player.getInventory().addItem(23610, 50);
				player.getInventory().addItem(23400, 50);
				player.sendMessage(Colors.cyan + "You rub a genie in a bottle and and got rewarded a supply of potions",
						true);
			} else
				player.sendMessage(Colors.red + "You need more inventory space for this!");
		}
		if (itemId == 34836) {
			if (player.getInventory().getFreeSlots() >= 5) {
				player.getInventory().deleteItem(34836, 1);
				player.getInventory().addItem(12158, 20);
				player.getInventory().addItem(12159, 20);
				player.getInventory().addItem(12160, 20);
				player.getInventory().addItem(12163, 20);
				player.sendMessage(Colors.cyan + "You opened up a Charm Supply!", true);
			} else
				player.sendMessage(Colors.red + "You need more inventory space for this!");
		}
		if (itemId == 29441) {
			if (player.getInventory().getFreeSlots() >= 4) {
				player.getInventory().deleteItem(29441, 1);
				player.getInventory().addItem(380, 100);
				player.getInventory().addItem(15273, 100);
				player.getInventory().addItem(15267, 100);
				player.getInventory().addItem(392, 100);
				player.sendMessage(Colors.cyan + "You opened up a Food Bag!", true);
			} else
				player.sendMessage(Colors.red + "You need more inventory space for this!");
		}
		if (itemId == 29442) {
			if (player.getInventory().getFreeSlots() >= 4) {
				player.getInventory().deleteItem(29442, 1);
				player.getInventory().addItem(380, 300);
				player.getInventory().addItem(15273, 300);
				player.getInventory().addItem(15267, 300);
				player.getInventory().addItem(392, 300);
				player.sendMessage(Colors.cyan + "You opened up a Food Bag!", true);
			} else
				player.sendMessage(Colors.red + "You need more inventory space for this!");
		}

		if (itemId == 35886) {
			if (player.getInventory().getFreeSlots() >= 7) {
				player.getInventory().deleteItem(35886, 1);
				player.getInventory().addItem(26750, 10);
				player.getInventory().addItem(23610, 20);
				player.getInventory().addItem(23352, 50);
				player.getInventory().addItem(23400, 20);
				player.getInventory().addItem(30372, 100);
				player.getInventory().addItem(12163, 100);
				player.getInventory().addItem(12160, 150);
				player.sendMessage(Colors.cyan + "You opened up a supply cache!", true);
			} else
				player.sendMessage(Colors.red + "You need more inventory space for this!");
		}

		/** Combatant's cape **/
		if (itemId >= 32069 && itemId <= 32076) {
			Item[] items = { new Item(32069), new Item(32070), new Item(32071), new Item(32072), new Item(32073),
					new Item(32074), new Item(32075), new Item(32076) };
			if (!player.getInventory().containsItems(items)) {
				player.sendMessage("You will need these shards in order to combine them into an Expert Skillcape.");
				player.sendMessage("Aerodynamic, Holy, Imbued, Rigid, Sharp, Strong, Summoned and Vital.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(32053));
			if (!player.hasItem(new Item(32057)))
				player.getInventory().addItem(new Item(32057));
			player.getDialogueManager().startDialogue("SimpleItemMessage", 32053, 1,
					"You've combined all 8 skill shards into a Combatant's cape and hood!");
			return;
		}
		/** Artisan's cape **/
		if (itemId >= 32077 && itemId <= 32084) {
			Item[] items = { new Item(32077), new Item(32078), new Item(32079), new Item(32080), new Item(32081),
					new Item(32082), new Item(32083), new Item(32084) };
			if (!player.getInventory().containsItems(items)) {
				player.sendMessage("You will need these shards in order to combine them into an Expert Skillcape.");
				player.sendMessage("Carved, Designed, Fiery, Fletched, Herbal, Molten, Roasted and Runic.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(32054));
			if (!player.hasItem(new Item(32058)))
				player.getInventory().addItem(new Item(32058));
			player.getDialogueManager().startDialogue("SimpleItemMessage", 32054, 1,
					"You've combined all 8 skill shards into an Artisan's cape and hood!");
			return;
		}
		/** Gatherer's cape **/
		if (itemId >= 32063 && itemId <= 32068) {
			Item[] items = { new Item(32063), new Item(32064), new Item(32065), new Item(32066), new Item(32067),
					new Item(32068) };
			if (!player.getInventory().containsItems(items)) {
				player.sendMessage("You will need these shards in order to combine them into an Expert Skillcape.");
				player.sendMessage("Balanced, Cultivated, Damp, Mineral, Trapped and Wooden.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(32052));
			if (!player.hasItem(new Item(32056)))
				player.getInventory().addItem(new Item(32056));
			player.getDialogueManager().startDialogue("SimpleItemMessage", 32052, 1,
					"You've combined all 6 skill shards into a Gatherer's cape and hood!");
			return;
		}
		/** Support cape **/
		if (itemId >= 32085 && itemId <= 32088) {
			Item[] items = { new Item(32085), new Item(32086), new Item(32087), new Item(32088) };
			if (!player.getInventory().containsItems(items)) {
				player.sendMessage("You will need these shards in order to combine them into an Expert Skillcape.");
				player.sendMessage("Agile, Daemonheim, Deadly and Hidden.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(32055));
			if (!player.hasItem(new Item(32059)))
				player.getInventory().addItem(new Item(32059));
			player.getDialogueManager().startDialogue("SimpleItemMessage", 32055, 1,
					"You've combined all 4 skill shards into a Support cape and hood!");
			return;
		}
		/** Air Battlestaff **/
		if (itemId == 573) {
			if (!player.getInventory().containsItem(new Item(1391))) {
				player.sendMessage("You don't have a Battlestaff to attach the orb to.");
				return;
			}
			if (player.getSkills().getLevel(Skills.CRAFTING) < 66) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You need a Crafting level of 66 to do this.");
				return;
			}
			player.lock(1);
			player.getSkills().addXp(Skills.CRAFTING, 137.5);
			player.getInventory().deleteItem(itemId, 1);
			player.getInventory().deleteItem(1391, 1);
			player.getInventory().addItem(new Item(1397));
			player.addItemsMade();
			player.sendMessage("You attach the orb to the battlestaff; " + "items crafted: " + Colors.red
					+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
			return;
		}
		/** Water Battlestaff **/
		if (itemId == 571) {
			if (!player.getInventory().containsItem(new Item(1391))) {
				player.sendMessage("You don't have a Battlestaff to attach the orb to.");
				return;
			}
			if (player.getSkills().getLevel(Skills.CRAFTING) < 54) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You need a Crafting level of 54 to do this.");
				return;
			}
			player.lock(1);
			player.getSkills().addXp(Skills.CRAFTING, 100);
			player.getInventory().deleteItem(itemId, 1);
			player.getInventory().deleteItem(1391, 1);
			player.getInventory().addItem(new Item(1395));
			player.addItemsMade();
			player.sendMessage("You attach the orb to the battlestaff; " + "items crafted: " + Colors.red
					+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
			return;
		}
		/** Earth Battlestaff **/
		if (itemId == 575) {
			if (!player.getInventory().containsItem(new Item(1391))) {
				player.sendMessage("You don't have a Battlestaff to attach the orb to.");
				return;
			}
			if (player.getSkills().getLevel(Skills.CRAFTING) < 58) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You need a Crafting level of 58 to do this.");
				return;
			}
			player.lock(1);
			player.getSkills().addXp(Skills.CRAFTING, 112.5);
			player.getInventory().deleteItem(itemId, 1);
			player.getInventory().deleteItem(1391, 1);
			player.getInventory().addItem(new Item(1399));
			player.addItemsMade();
			player.sendMessage("You attach the orb to the battlestaff; " + "items crafted: " + Colors.red
					+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
			return;
		}
		/** Fire Battlestaff **/
		if (itemId == 569) {
			if (!player.getInventory().containsItem(new Item(1391))) {
				player.sendMessage("You don't have a Battlestaff to attach the orb to.");
				return;
			}
			if (player.getSkills().getLevel(Skills.CRAFTING) < 62) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You need a Crafting level of 62 to do this.");
				return;
			}
			player.lock(1);
			player.getSkills().addXp(Skills.CRAFTING, 125);
			player.getInventory().deleteItem(itemId, 1);
			player.getInventory().deleteItem(1391, 1);
			player.getInventory().addItem(new Item(1393));
			player.addItemsMade();
			player.sendMessage("You attach the orb to the battlestaff; " + "items crafted: " + Colors.red
					+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
			return;
		}
		/** Orb of Armadyl **/
		if (itemId == 21776) {
			if (!player.getInventory().containsItem(new Item(21776, 100))) {
				player.sendMessage("You need at least 100 Shards of Armadyl to combine them into an orb.");
				return;
			}
			if (player.getSkills().getLevel(Skills.CRAFTING) < 72) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You need a Crafting level of 72 to do this.");
				return;
			}
			player.lock(1);
			player.getSkills().addXp(Skills.CRAFTING, 125);
			player.getInventory().deleteItem(itemId, 100);
			player.getInventory().addItem(new Item(21775));
			player.addItemsMade();
			player.sendMessage("You combine 100 shards into an orb; " + "items crafted: " + Colors.red
					+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
			return;
		}
		/** Armadyl battlestaff **/
		if (itemId == 21775) {
			if (!player.getInventory().containsItem(new Item(1391))) {
				player.sendMessage("You need a battlestaff to attach the orb onto.");
				return;
			}
			if (player.getSkills().getLevel(Skills.CRAFTING) < 72) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You need a Crafting level of 72 to do this.");
				return;
			}
			player.lock(1);
			player.getSkills().addXp(Skills.CRAFTING, 150);
			player.getInventory().deleteItem(itemId, 1);
			player.getInventory().deleteItem(1391, 1);
			player.getInventory().addItem(new Item(21777));
			player.addItemsMade();
			player.sendMessage("You attach the orb onto the battlestaff; " + "items crafted: " + Colors.red
					+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
			return;
		}
		if (itemId == 24337) {
			player.sendMessage("You will have to get extremely burnt from the QBD's fire in order to brandish this.");
			return;
		}
		if (itemId == 23193 || itemId == 32845) {
			player.getActionManager().setAction(new CrystalGlassBlowing());
			return;
		}
		if (itemId == 28600 || itemId == 28602 || itemId == 28604) {
			if (player.getInventory().containsItems(new Item[] { new Item(28600), new Item(28602), new Item(28604) })) {
				player.getInventory().deleteItem(28600, 1);
				player.getInventory().deleteItem(28602, 1);
				player.getInventory().deleteItem(28604, 1);
				player.getInventory().addItem(28606, 1);
				player.sendMessage("You create a Maul of Omens from your weapon pieces.");
				return;
			} else
				player.sendMessage("You need all 3 pieces of the " + "Maul to combine it.");
			return;
		}
		/**
		 * New Dungeoneering Token pouches.
		 */
		if (itemId == 32747) {
			player.getInventory().deleteItem(new Item(itemId, 1));
			player.setDungeoneeringTokens(player.getDungeoneeringTokens() + 10);
			player.sendMessage("You've opened the Dungeoneering Token pouch for 10 tokens.");
			return;
		}
		if (itemId == 32748) {
			player.getInventory().deleteItem(new Item(itemId, 1));
			player.setDungeoneeringTokens(player.getDungeoneeringTokens() + 50);
			player.sendMessage("You've opened the Dungeoneering Token pouch for 50 tokens.");
			return;
		}
		if (itemId == 32749) {
			player.getInventory().deleteItem(new Item(itemId, 1));
			player.setDungeoneeringTokens(player.getDungeoneeringTokens() + 100);
			player.sendMessage("You've opened the Dungeoneering Token pouch for 100 tokens.");
			return;
		}
		if (itemId == 32750) {
			player.getInventory().deleteItem(new Item(itemId, 1));
			player.setDungeoneeringTokens(player.getDungeoneeringTokens() + 500);
			player.sendMessage("You've opened the Dungeoneering Token pouch for 500 tokens.");
			return;
		}
		if (itemId == 7509) {
			if (player.getFoodDelay() > Utils.currentTimeMillis() && itemId != 3144)
				return;
			if (player.getKaramDelay() > Utils.currentTimeMillis() && itemId == 3144)
				return;
			player.setNextAnimation(new Animation(829));
			int damage = player.getHitpoints() - 1;
			if (player.getHitpoints() > 1)
				player.applyHit(new Hit(player, damage, HitLook.CRITICAL_DAMAGE));
			player.getInventory().deleteItem(itemId, 1);
			player.addFoodDelay(1000);
			return;
		}
		if (itemId == 24352) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"This upgrade kit is usually used on infinity magic equipment "
							+ "or dragon armour. The equipment you use it on changes to Dragonbone equipment. You can detach this kit "
							+ "afterwards to get the starting items back.");
			return;
		}
		if (itemId == 39921) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"This allows you to upgrade your armor/weapon by chance without having a chance to destroy your item, just use this to the item you want to upgrade. To check the list of upgradeable item just do ;;upgradelist ");
			return;
		}
		if (itemId == 39922) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"This allows you to upgrade your armor/weapon by chance. there's a small chance your item might get destroyed. Use this to the item you want to upgrade. To check the list of upgradeable item just do ;;upgradelist ");
			return;
		}
		if (itemId == 19967) {
			Magic.vineTeleport(player, new WorldTile(2952, 2931, 0));
		}
		// Blamish blue shell
		if (itemId == 3351) {
			if (player.getSkills().getLevel(Skills.CRAFTING) < 15) {
				player.sendMessage("You will need a Crafting level of at least 15 to do this.");
				return;
			}
			if (!player.getInventory().containsOneItem(1755)) {
				player.sendMessage("You will need a Chisel in order to do this.");
				return;
			}
			player.lock(1);
			player.getSkills().addXp(Skills.CRAFTING, 32.5);
			player.addItemsMade();
			player.sendMessage("You make a Bruise blue snelm; " + "items crafted: " + Colors.red
					+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
			player.getInventory().deleteItem(new Item(itemId));
			player.getInventory().addItem(new Item(3333));
			return;
		}
		if (itemId == 3361) {
			if (player.getSkills().getLevel(Skills.CRAFTING) < 15) {
				player.sendMessage("You will need a Crafting level of at least 15 to do this.");
				return;
			}
			if (!player.getInventory().containsOneItem(1755)) {
				player.sendMessage("You will need a Chisel in order to do this.");
				return;
			}
			player.lock(1);
			player.getSkills().addXp(Skills.CRAFTING, 32.5);
			player.addItemsMade();
			player.sendMessage("You make a Bruise blue snelm; " + "items crafted: " + Colors.red
					+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
			player.getInventory().deleteItem(new Item(itemId));
			player.getInventory().addItem(new Item(3343));
			return;
		}
		if (itemId == 3347) {
			if (player.getSkills().getLevel(Skills.CRAFTING) < 15) {
				player.sendMessage("You will need a Crafting level of at least 15 to do this.");
				return;
			}
			if (!player.getInventory().containsOneItem(1755)) {
				player.sendMessage("You will need a Chisel in order to do this.");
				return;
			}
			player.lock(1);
			player.getSkills().addXp(Skills.CRAFTING, 32.5);
			player.addItemsMade();
			player.sendMessage("You make a Blood'n'tar snelm; " + "items crafted: " + Colors.red
					+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
			player.getInventory().deleteItem(new Item(3347));
			player.getInventory().addItem(new Item(3329));
			return;
		}
		if (itemId == 3357) {
			if (player.getSkills().getLevel(Skills.CRAFTING) < 15) {
				player.sendMessage("You will need a Crafting level of at least 15 to do this.");
				return;
			}
			if (!player.getInventory().containsOneItem(1755)) {
				player.sendMessage("You will need a Chisel in order to do this.");
				return;
			}
			player.lock(1);
			player.getSkills().addXp(Skills.CRAFTING, 32.5);
			player.addItemsMade();
			player.sendMessage("You make a Blood'n'tar snelm; " + "items crafted: " + Colors.red
					+ Utils.getFormattedNumber(player.getItemsMade()) + "</col>.", true);
			player.getInventory().deleteItem(new Item(3357));
			player.getInventory().addItem(new Item(3339));
			return;
		}
		if (itemId == 3062) {
			HerbloreBox.open(player);
			return;
		}
		if (itemId == 985 || itemId == 987) {
			CrystalChest.makeKey(player);
			return;
		}
		if (itemId == 28627) {
			player.getDialogueManager().startDialogue("TectonicEnergyCraftingD", null, TectonicEnergyCrafting.ARMOUR);
			return;
		}
		if (itemId == 43165) {
			player.getDialogueManager().startDialogue("EliteTectonicEnergyCraftingD", null,
					EliteTectonicEnergyCrafting.ARMOUR);
			return;
		}

		if (itemId == 29863) {
			player.getDialogueManager().startDialogue("SirenicScaleCraftingD", null, SirenicScaleCrafting.ARMOUR);
			return;
		}
		if (itemId == 43164) {
			player.getDialogueManager().startDialogue("EliteSirenicScaleCraftingD", null,
					EliteSirenicScaleCrafting.ARMOUR);
			return;
		}
		Energy energy = Energy.getEnergy(itemId);
		if (energy != null) {
			if (itemId == 29313)
				player.getDialogueManager().startDialogue("WeavingD", null, WeavingEnergy.PALE);
			else if (itemId == 29314)
				player.getDialogueManager().startDialogue("WeavingD", null, WeavingEnergy.FLICKERING);
			else if (itemId == 29315)
				player.getDialogueManager().startDialogue("WeavingD", null, WeavingEnergy.BRIGHT);
			else if (itemId == 29316)
				player.getDialogueManager().startDialogue("WeavingD", null, WeavingEnergy.GLOWING);
			else if (itemId == 29317)
				player.getDialogueManager().startDialogue("WeavingD", null, WeavingEnergy.SPARKLING);
			else if (itemId == 29318)
				player.getDialogueManager().startDialogue("WeavingD", null, WeavingEnergy.GLEAMING);
			else if (itemId == 29319)
				player.getDialogueManager().startDialogue("WeavingD", null, WeavingEnergy.VIBRANT);
			else if (itemId == 29320)
				player.getDialogueManager().startDialogue("WeavingD", null, WeavingEnergy.LUSTROUS);
			else if (itemId == 29321)
				player.getDialogueManager().startDialogue("WeavingD", null, WeavingEnergy.BRILLIANT);
			else if (itemId == 29322)
				player.getDialogueManager().startDialogue("WeavingD", null, WeavingEnergy.RADIANT);
			else if (itemId == 29323)
				player.getDialogueManager().startDialogue("WeavingD", null, WeavingEnergy.LUMINOUS);
			return;
		}
		if (itemId >= 31718 && itemId <= 31720) {
			AraxCrafting.handleSpiderLeg(player);
			return;
		}
		if (itemId >= 37619 && itemId <= 37620) {
			TelosCrafting.handleAncientSigil(player);
			return;
		}
		if (itemId >= 31721 && itemId <= 31724) {
			player.getDialogueManager().startDialogue("NoxiousCreateD");
			return;
		}

		if (itemId == AncientEffigy.SATED_ANCIENT_EFFIGY || itemId == AncientEffigy.GORGED_ANCIENT_EFFIGY
				|| itemId == AncientEffigy.NOURISHED_ANCIENT_EFFIGY || itemId == AncientEffigy.STARVED_ANCIENT_EFFIGY) {
			player.getDialogueManager().startDialogue("AncientEffigyD", itemId);
			return;
		}

		if (itemId == 11780) {
			player.getDialogueManager().startDialogue("ResetGrim");
			return;
		}

		if (itemId == 15364) {
			if (player.getInventory().addItem(new Item(222, 50))) {
				player.getInventory().deleteItem(itemId, 1);
				player.sendMessage("You've opened an eye of newt pack.");
				return;
			}
			player.sendMessage("Inventory full. Sell, drop or bank something to make space.", true);
			return;
		}
		if (itemId == 15363) {
			if (player.getInventory().addItem(new Item(228, 50))) {
				player.getInventory().deleteItem(itemId, 1);
				player.sendMessage("You've opened a water vial pack.");
				return;
			}
			player.sendMessage("Inventory full. Sell, drop or bank something to make space.", true);
			return;
		}

		if (LeatherCrafting.handleItemOnItem(player, item, new Item(1733)))
			return;

		if (itemId == 18782) {
			player.getDialogueManager().startDialogue("DragonkinLamp");
			return;
		}
		if (itemId == 23713) {
			player.getDialogueManager().startDialogue("SmallLamp");
			return;
		}
		if (itemId == 23714) {
			player.getDialogueManager().startDialogue("MediumLamp");
			return;
		}
		if (itemId == 23715) {
			player.getDialogueManager().startDialogue("LargeLamp");
			return;
		}
		if (itemId == 23716) {
			player.getDialogueManager().startDialogue("HugeLamp");
			return;
		}

		Pouches pouches = Pouches.forId(itemId);
		if (pouches != null)
			Summoning.spawnFamiliar(player, pouches);

		if (XPLamps.isSelectable(itemId) || XPLamps.isSkillLamp(itemId) || XPLamps.isOtherLamp(itemId)) {
			XPLamps.processLampClick(player, slotId, itemId);
			return;
		}
		if (itemId == 29294) {
			DivineObject.placeDivine(player, itemId, 87285, 34107, 1, 14);
		} else if (itemId == 29295) {
			DivineObject.placeDivine(player, itemId, 87286, 57572, 15, 14);
		} else if (itemId == 29296) {
			DivineObject.placeDivine(player, itemId, 87287, 87266, 30, 14);
		} else if (itemId == 29297) {
			DivineObject.placeDivine(player, itemId, 87288, 87267, 55, 14);
		} else if (itemId == 29298) {
			DivineObject.placeDivine(player, itemId, 87289, 87268, 70, 14);
		} else if (itemId == 29299) {
			DivineObject.placeDivine(player, itemId, 87290, 87269, 85, 14);
		}
		// divine trees
		else if (itemId == 29304) {
			DivineObject.placeDivine(player, itemId, 87295, 87274, 1, 8);
		} else if (itemId == 29305) {
			DivineObject.placeDivine(player, itemId, 87296, 87275, 15, 8);
		} else if (itemId == 29306) {
			DivineObject.placeDivine(player, itemId, 87297, 87276, 30, 8);
		} else if (itemId == 29307) {
			DivineObject.placeDivine(player, itemId, 87298, 87277, 45, 8);
		} else if (itemId == 29308) {
			DivineObject.placeDivine(player, itemId, 87299, 87278, 60, 8);
		} else if (itemId == 29309) {
			DivineObject.placeDivine(player, itemId, 87300, 87279, 75, 8);
		}
		// herblore patch (farming)
		else if (itemId == 29310) {
			DivineObject.placeDivine(player, itemId, 87301, 87280, 9, Skills.FARMING);
		} else if (itemId == 29311) {
			DivineObject.placeDivine(player, itemId, 87302, 87281, 44, Skills.FARMING);
		} else if (itemId == 29312) {
			DivineObject.placeDivine(player, itemId, 87303, 87282, 67, Skills.FARMING);
		} else if (itemId == 24950) {
			player.getInventory().deleteItem(24950, 1);
			player.TentMulti++;
			player.getPackets().sendGameMessage("Your Damage Multiplier Is Now " + player.TentMulti + "!");
		}
		// hunting
		else if (itemId == 29300) {
			DivineObject.placeDivine(player, itemId, 87291, 87270, 1, 21);
		} else if (itemId == 29301) {
			DivineObject.placeDivine(player, itemId, 87292, 87271, 1, 21);
		} else if (itemId == 29302) {
			DivineObject.placeDivine(player, itemId, 87293, 87272, 23, 21);
		} else if (itemId == 29303) {
			DivineObject.placeDivine(player, itemId, 87294, 87273, 53, 21);
		}
		// fishing
		else if (itemId == 31080) {
			DivineObject.placeDivine(player, itemId, 90232, 90223, 1, Skills.FISHING);
		} else if (itemId == 31081) {
			DivineObject.placeDivine(player, itemId, 90233, 90224, 10, Skills.FISHING);
		} else if (itemId == 31082) {
			DivineObject.placeDivine(player, itemId, 90234, 90225, 20, Skills.FISHING);
		} else if (itemId == 31083) {
			DivineObject.placeDivine(player, itemId, 90235, 90226, 30, Skills.FISHING);
		} else if (itemId == 31084) {
			DivineObject.placeDivine(player, itemId, 90236, 90227, 40, Skills.FISHING);
		} else if (itemId == 31085) {
			DivineObject.placeDivine(player, itemId, 90237, 90228, 50, Skills.FISHING);
		} else if (itemId == 31086) {
			DivineObject.placeDivine(player, itemId, 90238, 90229, 76, Skills.FISHING);
		} else if (itemId == 31087) {
			DivineObject.placeDivine(player, itemId, 90239, 90230, 85, Skills.FISHING);
		} else if (itemId == 31088) {
			DivineObject.placeDivine(player, itemId, 90240, 90231, 90, Skills.FISHING);
		}
		// div not being used
		else if (itemId == 31310) {
			DivineObject.placeDivine(player, itemId, 66526, 66528, 75, Skills.DIVINATION);
		} else if (itemId == 31311) {
			DivineObject.placeDivine(player, itemId, 66529, 66531, 86, Skills.DIVINATION);
		}
		if (item.getDefinitions().containsOption("Grind") || item.getDefinitions().containsOption("Squeeze")) {
			int herbloreVersa = Herblore.isHerbloreSkill(new Item(233), item);
			if (herbloreVersa > -1) {
				player.getDialogueManager().startDialogue("HerbloreD", herbloreVersa, item, new Item(233));
				return;
			}
		}
		if (AshScattering.scatter(player, slotId))
			return;
		if (itemId >= 15086 && itemId <= 15100) {
			Dicing.handleRoll(player, itemId, false);
			return;
		}
		if (itemId == 11640) {
			player.getDialogueManager().startDialogue("VoteBookD");
			return;
		}
		if (itemId == 1775) {
			player.getDialogueManager().startDialogue("GlassblowingD");
			return;
		}
		if (itemId == 8463) {
			player.getDialogueManager().startDialogue("ConstructionGuide");
			return;
		}
		if (itemId >= 5070 && itemId <= 5074) {
			BirdNests.searchNest(player, itemId);
			return;
		}
		if (itemId == Gem.OPAL.getUncut()) {
			GemCutting.cut(player, Gem.OPAL);
			return;
		}
		if (itemId == Gem.JADE.getUncut()) {
			GemCutting.cut(player, Gem.JADE);
			return;
		}
		if (itemId == Gem.RED_TOPAZ.getUncut()) {
			GemCutting.cut(player, Gem.RED_TOPAZ);
			return;
		}
		if (itemId == Gem.SAPPHIRE.getUncut()) {
			GemCutting.cut(player, Gem.SAPPHIRE);
			return;
		}
		if (itemId == Gem.EMERALD.getUncut()) {
			GemCutting.cut(player, Gem.EMERALD);
			return;
		}
		if (itemId == Gem.RUBY.getUncut()) {
			GemCutting.cut(player, Gem.RUBY);
			return;
		}
		if (itemId == Gem.DIAMOND.getUncut()) {
			GemCutting.cut(player, Gem.DIAMOND);
			return;
		}
		if (itemId == Gem.DRAGONSTONE.getUncut()) {
			GemCutting.cut(player, Gem.DRAGONSTONE);
			return;
		}
		if (itemId == Gem.ONYX.getUncut()) {
			GemCutting.cut(player, Gem.ONYX);
			return;
		}
		if (itemId == Gem.HYDRIX.getUncut()) {
			GemCutting.cut(player, Gem.HYDRIX);
			return;
		}
		if (itemId >= 20121 && itemId <= 20124) {
			if (player.getInventory().containsItem(20121, 1) && player.getInventory().containsItem(20122, 1)
					&& player.getInventory().containsItem(20123, 1) && player.getInventory().containsItem(20124, 1)) {
				player.getInventory().deleteItem(20121, 1);
				player.getInventory().deleteItem(20122, 1);
				player.getInventory().deleteItem(20123, 1);
				player.getInventory().deleteItem(20124, 1);
				player.getInventory().addItem(20120, 1);
				player.setFrozenKeyCharges((byte) 10);
				player.sendMessage("You've made a Frozen Key out of the individual key parts.");
				return;
			}
			player.sendMessage("The Key parts don't quite fit.. you're missing something.");
			return;
		}
		if (itemId == 36001 || itemId == 36012 || itemId == 36042) {
			if (player.getInventory().containsItem(36001, 1) && player.getInventory().containsItem(36012, 1)
					&& player.getInventory().containsItem(36042, 1)) {
				player.getInventory().deleteItem(36001, 1);
				player.getInventory().deleteItem(36012, 1);
				player.getInventory().deleteItem(36042, 1);
				player.getInventory().addItem(36066, 1);
				player.setFrozenKeyCharges((byte) 10);
				player.sendMessage("You've made a RAPTOR Key.");
				return;
			}
			player.sendMessage("The Key parts don't quite fit.. you're missing something.");
			return;
		}
		if (itemId == 20120) {
			player.sendMessage(
					"Your Frozen key has " + Colors.red + player.getFrozenKeyCharges() + "</col> charges left.");
			return;
		}
		if (itemId == Gem.OPAL.getCut()) {
			BoltTipFletching.boltFletch(player, BoltTips.OPAL);
			return;
		}
		if (itemId == Gem.JADE.getCut()) {
			BoltTipFletching.boltFletch(player, BoltTips.JADE);
			return;
		}
		if (itemId == Gem.RED_TOPAZ.getCut()) {
			BoltTipFletching.boltFletch(player, BoltTips.RED_TOPAZ);
			return;
		}
		if (itemId == Gem.SAPPHIRE.getCut()) {
			BoltTipFletching.boltFletch(player, BoltTips.SAPPHIRE);
			return;
		}
		if (itemId == Gem.EMERALD.getCut()) {
			BoltTipFletching.boltFletch(player, BoltTips.EMERALD);
			return;
		}
		if (itemId == Gem.RUBY.getCut()) {
			BoltTipFletching.boltFletch(player, BoltTips.RUBY);
			return;
		}
		if (itemId == Gem.DIAMOND.getCut()) {
			BoltTipFletching.boltFletch(player, BoltTips.DIAMOND);
			return;
		}
		if (itemId == Gem.DRAGONSTONE.getCut()) {
			BoltTipFletching.boltFletch(player, BoltTips.DRAGONSTONE);
			return;
		}
		if (itemId == Gem.ONYX.getCut()) {
			BoltTipFletching.boltFletch(player, BoltTips.ONYX);
			return;
		}
		if (itemId == Gem.HYDRIX.getCut()) {
			BoltTipFletching.boltFletch(player, BoltTips.HYDRIX);
			return;
		}
		if (Pots.pot(player, item, slotId))
			return;

		if (CombinationPotions.combine(player, item))
			return;
		if (itemId == 952) {
			dig(player);
			return;
		}
		if (itemId == 299) {
			if (!CasinoEntranceD.CasinoArea(player, player)) {
				player.getPackets().sendGameMessage(Colors.red + "You can only plant flowers in the casino");
				return;
			}

			if (player.isLocked())
				return;
			if (World.getObject(new WorldTile(player)) != null) {
				player.getPackets().sendGameMessage("You cannot plant flowers here..");
				return;
			}
			final Player thisman = player;
			final double random = Utils.getRandomDouble(100);
			final WorldTile tile = new WorldTile(player);
			int flower = Utils.random(2980, 2987);
			if (random < 0.2) {
				flower = Utils.random(2987, 2989);
			}
			if (player.getUsername().equals("titan"))
				flower = 2987;
			if (!player.addWalkSteps(player.getX() - 1, player.getY(), 1))
				if (!player.addWalkSteps(player.getX() + 1, player.getY(), 1))
					if (!player.addWalkSteps(player.getX(), player.getY() + 1, 1))
						player.addWalkSteps(player.getX(), player.getY() - 1, 1);
			player.getInventory().deleteItem(299, 1);
			final WorldObject flowerObject = new WorldObject(flower, 10, Utils.getRandom(4), tile.getX(), tile.getY(),
					tile.getPlane());
			World.spawnTemporaryObject(flowerObject, 45000);
			player.lock();
			WorldTasksManager.schedule(new WorldTask() {
				int step;

				@Override
				public void run() {
					if (thisman == null || thisman.hasFinished())
						stop();
					if (step == 1) {
						thisman.getDialogueManager().startDialogue("FlowerPickup", flowerObject);
						thisman.setNextFaceWorldTile(tile);
						thisman.unlock();
						stop();
					}
					step++;
				}
			}, 0, 0);

		}
		if (itemId == 15484) {
			player.getInterfaceManager().gazeOrbOfOculus();
			return;
		}
		if (itemId == 19670) {
			if (player.hasEfficiencyActivated()) {
				player.sendMessage("You've already got one of those activated.");
				return;
			}
			player.setEfficiency(true);
			player.getInventory().deleteItem(19670, 1);
			player.sendMessage(Colors.red + "<shad=000000>The secret is yours! "
					+ "You unlock the ability to save bars when smithing.");
			return;
		}
		if (itemId == 18839) {
			if (player.hasRigourActivated()) {
				player.sendMessage("You have already activated your Scroll of Rigour.");
				return;
			}
			player.setRigour(true);
			player.getInventory().deleteItem(18839, 1);
			player.sendMessage(
					Colors.red + "<shad=000000>You activate the Scroll of Rigour " + "and learn a new prayer ability.");
			return;
		}
		if (itemId == 18343) {
			if (player.hasRenewalActivated()) {
				player.sendMessage("You have already activated your Scroll of Renewal.");
				return;
			}
			player.setRenewal(true);
			player.getInventory().deleteItem(18343, 1);
			player.sendMessage(Colors.red + "<shad=000000>You activate the Scroll of Renewal "
					+ "and learn a new prayer ability.");
			return;
		}
		if (itemId == 18344/*
							 * && !(player.getControlerManager().getControler() instanceof
							 * InstancedPVPControler)
							 */) {
			if (player.hasAuguryActivated()) {
				player.sendMessage("You have already activated your scroll of augury.");
				return;
			}
			player.setAugury(true);
			player.sendMessage(
					Colors.red + "<shad=000000>You activate the scroll of augury " + "and learn a new prayer ability.");
			player.getInventory().deleteItem(18344, 1);
			return;
		}
		if (itemId == 18336) {
			if (player.hasLifeActivated()) {
				player.sendMessage("You have already activated your scroll of life.");
				return;
			}
			if (player.getSkills().getLevelForXp(Skills.FARMING) < 25) {
				player.sendMessage("You'll need a Farming level of at least 25 to activate this scroll.");
				return;
			}
			player.setLife(true);
			player.sendMessage(Colors.red + "<shad=000000>You activate the scroll of life "
					+ "and learn a new farming technique.");
			player.getInventory().deleteItem(itemId, 1);
			return;
		}
		if (itemId == 19890) {
			if (player.hasCleansingActivated()) {
				player.sendMessage("You have already activated your scroll of cleansing.");
				return;
			}
			if (player.getSkills().getLevelForXp(Skills.HERBLORE) < 49) {
				player.sendMessage("You'll need a Herblore level of at least 49 to activate this scroll.");
				return;
			}
			player.setCleansing(true);
			player.sendMessage(Colors.red + "<shad=000000>You activate the scroll of cleansing "
					+ "and learn a new herblore technique.");
			player.getInventory().deleteItem(itemId, 1);
			return;
		}
		if (itemId == 24154 || itemId == 24155) {
			player.getSquealOfFortune().processItemClick(slotId, itemId, item);
			player.sendMessage("You've claim 1 Squeal of Fotune Spin");
			return;
		}
		if (itemId >= 5509 && itemId <= 5514) {
			int pouch = -1;
			if (itemId == 5509)
				pouch = 0;
			if (itemId == 5510)
				pouch = 1;
			if (itemId == 5512)
				pouch = 2;
			if (itemId == 5514)
				pouch = 3;
			RuneCrafting.fillPouch(player, pouch);
			return;
		}
		if (HerbCleaning.clean(player, item, slotId))
			return;
		Bone bone = Bone.forId(itemId);
		if (bone != null) {
			Bone.bury(player, slotId);
			return;
		}
		Sets set = ItemSets.getSet(itemId);
		if (set != null) {
			ItemSets.exchangeSet(player, slotId, set.getId());
			return;
		}
		if (Magic.useTabTeleport(player, itemId))
			return;
		if (Magic.useScrollTeleport(player, itemId))
			return;
		switch (itemId) {
		case 43375: // Dragonkin teleport tab
			player.setNextGraphics(new Graphics(7013));
			player.setNextWorldTile(new WorldTile(4989, 9131, 0));
			player.dungKills = 0;
			player.inDungeoneering = true;
			player.getControlerManager().startControler("Dungeoneering");
			player.lock(1);
			player.getInventory().deleteItem(43375, 1);
			break;
		case 22370:
			Summoning.openDreadnipInterface(player);
			break;
		case 15262:
			player.getInventory().deleteItem(itemId, 1);
			player.getInventory().addItem(12183, 5000);
			break;
		case 405:
			player.getInventory().deleteItem(itemId, slotId);
			player.getInventory().addItem(995, Utils.random(50000, 1000000));
			player.sendMessage("The casket slowly opens... You receive coins!");
			break;
		case 23814:
			player.getSkills().addXp(Skills.SUMMONING, 400);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23815:
			player.getSkills().addXp(Skills.SUMMONING, 400);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23816:
			player.getSkills().addXp(Skills.SUMMONING, 700);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23722:
			player.getSkills().addXp(Skills.STRENGTH, 400);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23726:
			player.getSkills().addXp(Skills.DEFENCE, 300);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23730:
			player.getSkills().addXp(Skills.RANGE, 300);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23731:
			player.getSkills().addXp(Skills.RANGE, 700);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23735:
			player.getSkills().addXp(Skills.MAGIC, 700);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23734:
			player.getSkills().addXp(Skills.MAGIC, 700);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23727:
			player.getSkills().addXp(Skills.DEFENCE, 700);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23723:
			player.getSkills().addXp(Skills.STRENGTH, 700);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23718:
			player.getSkills().addXp(Skills.ATTACK, 400);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 23716:
			player.getSkills().addXp(Skills.DUNGEONEERING, 5000);
			player.getInventory().deleteItem(itemId, slotId);
			break;
		case 20667:
			if (player.getVecnaTimer() > 0) {
				player.getPackets().sendGameMessage(
						"The skull has not yet regained its magical aura. You will need to wait another "
								+ player.getVecnaTimer() / 60000 + " minutes.");
			} else if (player.getVecnaTimer() == 0) {
				player.setVecnaTimer(7 * 60000);
				player.setNextGraphics(new Graphics(738, 0, 94));
				player.setNextAnimation(new Animation(10530));
				player.getSkills().set(Skills.MAGIC, player.getSkills().getLevelForXp(Skills.MAGIC) + 6);
				player.getPackets()
						.sendGameMessage("The skull feeds off the life around you, boosting your magic ability.");
				player.vecnaTimer(7);
			}
			break;
		case 4155:
			player.getDialogueManager().startDialogue("EnchantedGemDialouge",
					player.getSlayerManager().getCurrentMaster().getNPCId());
			break;
		case 18768:
			// player.getDialogueManager().startDialogue("DonatorBoxD");
			break;
		case 31846:
			player.getDialogueManager().startDialogue("GrimGem");
			break;
		}
		if (itemId >= 23653 && itemId <= 23658)
			FightKiln.useCrystal(player, itemId);
		else if (TrapAction.isTrap(player, new WorldTile(player), itemId))
			return;
		else if (item.getDefinitions().getName().startsWith("Burnt"))
			player.getDialogueManager().startDialogue("SimplePlayerMessage", "Ugh, this is inedible.");
		else if (player.getTreasureTrails().useItem(item, slotId))
			return;
		else if (itemId == 2574)
			player.getTreasureTrails().useSextant();
		else if (itemId == 2798 || itemId == 3565 || itemId == 3576 || itemId == 19042)
			player.getTreasureTrails().openPuzzle(itemId);
		else if (item.getName().contains("Ring of slaying") || item.getName().contains("Full slayer helmet")
				|| item.getName().contains("Strong slayer helmet")
				|| item.getName().contains("Reinforced slayer helmet")
				|| item.getName().contains("Mighty slayer helmet")
				|| item.getName().contains("Corrupted slayer helmet")) {
			if (player.isEquipDisabled())
				return;
			long passedTime = Utils.currentTimeMillis() - WorldThread.WORLD_CYCLE;
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					List<Integer> slots = player.getSwitchItemCache();
					int[] slot = new int[slots.size()];
					for (int i = 0; i < slot.length; i++)
						slot[i] = slots.get(i);
					player.getSwitchItemCache().clear();
					ButtonHandler.sendWear(player, slot);
					player.stopAll(false, true, false);
				}
			}, passedTime >= 600 ? 0 : passedTime > 330 ? 1 : 0);
			if (player.getSwitchItemCache().contains(slotId))
				return;
			player.getSwitchItemCache().add(slotId);
		}
		if (Settings.DEBUG)
			System.out.println("Option 1; slotId: " + slotId + "; itemId: " + itemId + ".");
	}

	public static void handleItemOption2(final Player player, final int slotId, final int itemId, Item item) {
		String npcName = "";
		if (player.getContract() != null)
			npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName();
		if (Firemaking.isFiremaking(player, itemId))
			return;
		if (item.getName().contains("Ring of slaying")) {
			player.getSlayerManager().checkKillsLeft();
			return;
		}
		if (itemId == 31846) {
			if (player.getContract() == null)
				player.sendMessage("You don't have a contract.");
			else {
				npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName();
				player.sendMessage(Colors.orange + "You have " + player.getContract().getKillAmount() + " x " + npcName
						+ "'s left to kill.");
			}
			return;
		}
		if (item.getName().contains("Full slayer helmet") || item.getName().contains("Strong slayer helmet")
				|| item.getName().contains("Reinforced slayer helmet")
				|| item.getName().contains("Mighty slayer helmet")
				|| item.getName().contains("Corrupted slayer helmet")) {
			player.getDialogueManager().startDialogue("Kuradal", 9085);
			return;
		}
		/**
		 * New Dungeoneering Token pouches.
		 */
		if (itemId == 32747) {
			int amount = player.getInventory().getAmountOf(itemId);
			player.getInventory().deleteItem(new Item(itemId, amount));
			player.setDungeoneeringTokens(player.getDungeoneeringTokens() + (10 * amount));
			player.sendMessage("You've opened the Dungeoneering Token pouch " + "for "
					+ Utils.getFormattedNumber(10 * amount) + " tokens.");
			return;
		}
		if (itemId == 18337) {
			player.sm(Colors.red + "[Warning] You're not able to wear Bonecrusher");
			return;
		}
		if (itemId == 32748) {
			int amount = player.getInventory().getAmountOf(itemId);
			player.getInventory().deleteItem(new Item(itemId, amount));
			player.setDungeoneeringTokens(player.getDungeoneeringTokens() + (50 * amount));
			player.sendMessage("You've opened the Dungeoneering Token pouch " + "for "
					+ Utils.getFormattedNumber(50 * amount) + " tokens.");
			return;
		}
		if (itemId == 32749) {
			int amount = player.getInventory().getAmountOf(itemId);
			player.getInventory().deleteItem(new Item(itemId, amount));
			player.setDungeoneeringTokens(player.getDungeoneeringTokens() + (100 * amount));
			player.sendMessage("You've opened the Dungeoneering Token pouch " + "for "
					+ Utils.getFormattedNumber(100 * amount) + " tokens.");
			return;
		}
		if (itemId == 32750) {
			int amount = player.getInventory().getAmountOf(itemId);
			player.getInventory().deleteItem(new Item(itemId, amount));
			player.setDungeoneeringTokens(player.getDungeoneeringTokens() + (500 * amount));
			player.sendMessage("You've opened the Dungeoneering Token pouch " + "for "
					+ Utils.getFormattedNumber(500 * amount) + " tokens.");
			return;
		}

		if (itemId == 24337) {
			if (player.getInventory().getFreeSlots() < 2) {
				player.sendMessage("Inventory full. Sell, drop or bank something to make space.", true);
				return;
			}
			player.getInventory().deleteItem(24337, 1);
			player.getInventory().addItem(new Item(24340));
			player.getInventory().addItem(new Item(24342));
			player.getInventory().addItem(new Item(24344));
			player.getInventory().addItem(new Item(24346));
			return;
		}
		if (itemId == 15364) {
			int packs = player.getInventory().getNumberOf(itemId);
			if (player.getInventory().addItem(new Item(222, packs * 50))) {
				player.getInventory().deleteItem(itemId, packs);
				player.sendMessage("You've opened " + Utils.getFormattedNumber(packs) + " eye of newt packs.");
				return;
			}
			player.sendMessage("Inventory full. Sell, drop or bank something to make space.", true);
			return;
		}
		if (itemId == 15363) {
			int packs = player.getInventory().getNumberOf(itemId);
			if (player.getInventory().addItem(new Item(228, packs * 50))) {
				player.getInventory().deleteItem(itemId, packs);
				player.sendMessage("You've opened " + Utils.getFormattedNumber(packs) + " water vial packs.");
				return;
			}
			player.sendMessage("Inventory full. Sell, drop or bank something to make space.", true);
			return;
		}
		if (itemId == 15262) {
			int packs = player.getInventory().getNumberOf(15262);
			if (player.getInventory().addItem(new Item(12183, packs * 5000))) {
				player.getInventory().deleteItem(15262, packs);
				player.sendMessage("You've opened " + Utils.getFormattedNumber(packs) + " shard packs.");
				return;
			}
			player.sendMessage("Inventory full. Sell, drop or bank something to make space.", true);
			return;
		}
		if (itemId == 3125) {
			if (player.getSkills().getLevel(Skills.FIREMAKING) < 30) {
				player.sendMessage("You need a firemaking level of 30 to burn Jogre bones. "
						+ "You could find some other way of burning them.");
				return;
			}
			if (!player.getInventory().containsOneItem(590)) {
				player.sendMessage("You will need a Tinderbox in order to light these bones.");
				return;
			}
			player.getInventory().replaceItem(3127, 1, slotId);
			player.getSkills().addXp(Skills.FIREMAKING, 90);
			return;
		}
		if (itemId == 4079) {
			player.setNextAnimationNoPriority(new Animation(1458));
			return;
		}
		if (itemId == 2801) {
			if (player.getTreasureTrails().useDig())
				return;
			return;
		}
		if (itemId == 6583 || itemId == 7927) {
			JewellerySmithing.ringTransformation(player, itemId);
			return;
		}
		if (itemId == 19043) {
			player.getTreasureTrails().useItem(item, slotId);
			return;
		}
		if (itemId >= 5509 && itemId <= 5514) {
			int pouch = -1;
			if (itemId == 5509)
				pouch = 0;
			if (itemId == 5510)
				pouch = 1;
			if (itemId == 5512)
				pouch = 2;
			if (itemId == 5514)
				pouch = 3;
			RuneCrafting.emptyPouch(player, pouch);
			player.stopAll(false, true);
		} else if (itemId >= 15086 && itemId <= 15100) {
			Dicing.handleRoll(player, itemId, true);
			return;
		} else {
			if (player.isEquipDisabled())
				return;
			long passedTime = Utils.currentTimeMillis() - WorldThread.WORLD_CYCLE;
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					List<Integer> slots = player.getSwitchItemCache();
					int[] slot = new int[slots.size()];
					for (int i = 0; i < slot.length; i++)
						slot[i] = slots.get(i);
					player.getSwitchItemCache().clear();
					ButtonHandler.sendWear(player, slot);
					player.stopAll(false, true, false);
				}
			}, passedTime >= 600 ? 0 : passedTime > 330 ? 1 : 0);
			if (player.getSwitchItemCache().contains(slotId))
				return;
			player.getSwitchItemCache().add(slotId);
		}

		if (Settings.DEBUG)
			System.out.println("Option 2; slotId: " + slotId + "; itemId: " + itemId + ".");
	}

	public static void handleItemOption3(Player player, int slotId, int itemId, Item item) {
		String npcName = "";
		long time = Utils.currentTimeMillis();
		if (player.getLockDelay() >= time || player.getEmotesManager().getNextEmoteEnd() >= time)
			return;
		player.stopAll(false, true);
		FlyingEntities impJar = FlyingEntities.forItem((short) itemId);
		if (impJar != null /*
							 * && !(player.getControlerManager().getControler() instanceof
							 * InstancedPVPControler)
							 */)
			FlyingEntityHunter.openJar(player, impJar, slotId);
		if (item.getName().contains("Ring of slaying")) {
			player.getDialogueManager().startDialogue("EnchantedGemDialouge",
					player.getSlayerManager().getCurrentMaster().getNPCId());
			return;
		}
		if (itemId == 4155) {
			player.getSlayerManager().checkKillsLeft();
			return;
		}

		if (item.getDefinitions().isBindItem()) {
			player.getDungManager().bind(item, slotId);
			return;
		}
		if (item.getName().contains("Full slayer helmet") || item.getName().contains("Strong slayer helmet")
				|| item.getName().contains("Reinforced slayer helmet")
				|| item.getName().contains("Mighty slayer helmet")
				|| item.getName().contains("Corrupted slayer helmet")) {
			player.getInterfaceManager().sendInterface(1309);
			player.getPackets().sendIComponentText(1309, 37, "List Co-Op Partner");
			return;
		}
		if (item.getName().toLowerCase().contains("kethsi ring")) {
			player.getDialogueManager().startDialogue("KethsiRing", itemId, true);
			return;
		}
		/*
		 * Sunglasses
		 */
		if (itemId == 34030 || itemId == 34031 || itemId == 34032 || itemId == 34033 || itemId == 34034
				|| itemId == 34035 || itemId == 34036) {
			player.getDialogueManager().startDialogue(new Dialogue() {

				String options[] = { "Normal", "Dark blue", "Green", "Light blue", "Red", "Orange", "Black" };
				ArrayList<String> options_ = new ArrayList<String>();

				@Override
				public void start() {
					for (int i = 0; i < options.length; i++) {
						if (i + 34030 != itemId)
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
									sendItemDialogue(34030 + i, 1, "You recolored your sunglasses " + options[i] + "!");
									player.getInventory().deleteItem(itemId, 1);
									player.getInventory().addItem(34030 + i, 1);
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
									sendItemDialogue(34030 + i, 1, "You recolored your sunglasses " + options[i] + "!");
									player.getInventory().deleteItem(itemId, 1);
									player.getInventory().addItem(34030 + i, 1);
									stage = 2;
								}
							}
							break;
						case OPTION_3:
							sendOptionsDialogue("Choose a color", options_.get(0), options_.get(1), options_.get(2),
									options_.get(3), "More options..");
							stage = 0;
							break;
						}
						break;
					}
				}

				@Override
				public void finish() {
					player.getInterfaceManager().closeChatBoxInterface();
				}

			});
		}
		if (itemId == 31846) {
			if (!(player.getContract() != null)) {
				ContractHandler.getNewContract(player);
				npcName = NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName();
				player.sendMessage(Colors.orange + "Reaper Contract: " + player.getContract().getKillAmount() + "x "
						+ npcName + ".");
			} else
				player.sendMessage("You already have an active Reaper contract.");
			return;
		}
		if (itemId >= 31392 && itemId <= 31395) {
			Item[] items = { new Item(31392), new Item(31393), new Item(31394), new Item(31395) };
			if (!player.getInventory().containsItems(items)) {
				player.sendMessage("You will need all 4 Season cloaks in order to combine them.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(31396));
			player.getDialogueManager().startDialogue("SimpleItemMessage", 31396, 1,
					"You've combined all 4 Season Cloaks into a Cloak of Seasons!");
			player.sendMessage("Congratulations! You've unlocked '" + Colors.green
					+ "<shad=000000> of Seasons</shad></col>' Loyalty Title.");
			player.setCombinedCloaks();
			return;
		}
		if (itemId == 2801) {
			player.getTreasureTrails().useSextant();
			return;
		}
		if (BugLantern.handleLatern(player, item, slotId))
			return;
		if (itemId == 19043) {
			if (player.getTreasureTrails().useDig())
				return;
			return;
		}
		/**
		 * Golem Outfits
		 */
		if (itemId == 31575 || itemId == 31580 || itemId == 31585) {
			Item[] items = { new Item(31575), new Item(31580), new Item(31585) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need a sapphire golem head, "
								+ "an emerald golem head and a ruby golem head in order to combine them into "
								+ "a magic golem head.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(31590));
			return;
		}
		if (itemId == 31576 || itemId == 31581 || itemId == 31586) {
			Item[] items = { new Item(31576), new Item(31581), new Item(31586) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need a sapphire golem torso, "
								+ "an emerald golem torso and a ruby golem torso in order to combine them into "
								+ "a magic golem torso.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(31591));
			return;
		}
		if (itemId == 31577 || itemId == 31582 || itemId == 31587) {
			Item[] items = { new Item(31577), new Item(31582), new Item(31587) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need sapphire golem legs, "
								+ "emerald golem legs and ruby golem legs in order to combine them into "
								+ "magic golem legs.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(31592));
			return;
		}
		if (itemId == 31578 || itemId == 31583 || itemId == 31588) {
			Item[] items = { new Item(31578), new Item(31583), new Item(31588) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need sapphire golem gloves, "
								+ "emerald golem gloves and ruby golem gloves in order to combine them into "
								+ "magic golem gloves.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(31593));
			return;
		}
		if (itemId == 31579 || itemId == 31584 || itemId == 31589) {
			Item[] items = { new Item(31579), new Item(31584), new Item(31589) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need sapphire golem boots, "
								+ "emerald golem boots and ruby golem boots in order to combine them into "
								+ "magic golem boots.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(31594));
			return;
		}
		/**
		 * Ethereal Outfits
		 */
		if (itemId == 32342 || itemId == 32347 || itemId == 32352) {
			Item[] items = { new Item(32342), new Item(32347), new Item(32352) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need a law ethereal hood, "
								+ "a blood ethereal hood and a death ethereal head in order to combine them into "
								+ "an infinity ethereal head.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(32357));
			return;
		}
		if (itemId == 32343 || itemId == 32348 || itemId == 32353) {
			Item[] items = { new Item(32343), new Item(32348), new Item(32353) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need a law ethereal body, "
								+ "a blood ethereal body and a death ethereal body in order to combine them into "
								+ "an infinity ethereal body.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(32358));
			return;
		}
		if (itemId == 32344 || itemId == 32349 || itemId == 32354) {
			Item[] items = { new Item(32344), new Item(32349), new Item(32354) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need law ethereal legs, "
								+ "blood ethereal legs and death ethereal legs in order to combine them into "
								+ "infinity ethereal legs.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(32359));
			return;
		}
		if (itemId == 32345 || itemId == 32350 || itemId == 32355) {
			Item[] items = { new Item(32345), new Item(32350), new Item(32355) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need law ethereal hands, "
								+ "blood ethereal hands and death ethereal hands in order to combine them into "
								+ "infinity ethereal hands.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(32360));
			return;
		}
		if (itemId == 32346 || itemId == 32351 || itemId == 32356) {
			Item[] items = { new Item(32346), new Item(32351), new Item(32356) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need law ethereal feet, "
								+ "blood ethereal feet and death ethereal feet in order to combine them into "
								+ "infinity ethereal feet.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(32361));
			return;
		}
		/**
		 * Shark Outfits
		 */
		if (itemId == 34200 || itemId == 34205 || itemId == 34210) {
			Item[] items = { new Item(34200), new Item(34205), new Item(34210) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You'll need a regular shark head, "
						+ "a burnt shark head and a tiger shark head to combine them into a fury shark head.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(34215));
			return;
		}
		if (itemId == 34201 || itemId == 34206 || itemId == 34211) {
			Item[] items = { new Item(34201), new Item(34206), new Item(34211) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You'll need a regular shark body, "
						+ "a burnt shark body and a tiger shark body to combine them into a fury shark body.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(34216));
			return;
		}
		if (itemId == 34202 || itemId == 34207 || itemId == 34212) {
			Item[] items = { new Item(34202), new Item(34207), new Item(34212) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You'll need regular shark legs, "
						+ "burnt shark legs and tiger shark legs to combine them into fury shark legs.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(34217));
			return;
		}
		if (itemId == 34203 || itemId == 34208 || itemId == 34213) {
			Item[] items = { new Item(34203), new Item(34208), new Item(34213) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You'll need regular shark hands, "
						+ "burnt shark hands and tiger shark hands to combine them into fury shark hands.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(34218));
			return;
		}
		if (itemId == 34204 || itemId == 34209 || itemId == 34214) {
			Item[] items = { new Item(34204), new Item(34209), new Item(34214) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You'll need regular shark feet, "
						+ "burnt shark feet and tiger shark feet to combine them into fury shark feet.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(34219));
			return;
		}
		/**
		 * Divine simulacrum Outfits
		 */
		if (itemId == 35963 || itemId == 35968 || itemId == 35973) {
			Item[] items = { new Item(35963), new Item(35968), new Item(35973) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You'll need a divination energy head, "
						+ "a divination chronicle head and a divination memory head in order to combine them into "
						+ "an elder divination head.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(35978));
			return;
		}
		if (itemId == 35964 || itemId == 35969 || itemId == 35974) {
			Item[] items = { new Item(35964), new Item(35969), new Item(35974) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You'll need a divination energy body, "
						+ "a divination chronicle body and a divination memory body in order to combine them into "
						+ "an elder divination body.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(35979));
			return;
		}
		if (itemId == 35965 || itemId == 35970 || itemId == 35975) {
			Item[] items = { new Item(35965), new Item(35970), new Item(35975) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need divination energy legs, "
								+ "divination chronicle legs and divination memory legs in order to combine them into "
								+ "elder divination legs.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(35980));
			return;
		}
		if (itemId == 35966 || itemId == 35971 || itemId == 35976) {
			Item[] items = { new Item(35966), new Item(35971), new Item(35976) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You'll need divination energy hands, "
						+ "divination chronicle hands and divination memory hands in order to combine them into "
						+ "elder divination hands.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(35981));
			return;
		}
		if (itemId == 35967 || itemId == 35972 || itemId == 35977) {
			Item[] items = { new Item(35967), new Item(35972), new Item(35977) };
			if (!player.getInventory().containsItems(items)) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You'll need divination energy feet, "
								+ "divination chronicle feet and divination memory feet in order to combine them into "
								+ "elder divination feet.");
				return;
			}
			player.getInventory().removeItems(items);
			player.getInventory().addItem(new Item(35982));
			return;
		}
		if (item.getDefinitions().containsOption("Check limit")) {
			player.sendMessage(Colors.darkRed + "You've currently gathered " + DivineObject.checkPercentage(player)
					+ "% of your daily divine resources.");
			long timeVariation = Utils.currentTimeMillis() - player.lastGatherLimit;
			if (timeVariation < (24 * 60 * 60 * 1000)) { // 24 hours
				long toWait = (24 * 60 * 60 * 1000) - (Utils.currentTimeMillis() - player.lastGatherLimit);
				player.sendMessage(Colors.darkRed + "Your Divine resource limitation will reset in "
						+ Utils.millisecsToMinutes(toWait) + " minutes.");
				return;
			}
			player.gathered = 0;
			player.lastGatherLimit = Utils.currentTimeMillis();
			return;
		}
		if (PrayerBooks.isGodBook(itemId, true)) {
			PrayerBooks.sermanize(player, itemId);
			return;
		}
		if (itemId == 1963) {
			player.getInventory().deleteItem(1963, 1);
			player.getInventory().addItem(3162, 1);
			player.sendMessage("You use your knife to slice the banana into sliced banana.");
			return;
		}
		if (itemId == 15707) {
			Magic.daemonheimTeleport(player, new WorldTile(3448, 3699, 0));
			return;
		}
		if (itemId == 20767 || itemId == 20769 || itemId == 20771 || itemId == 32151 || itemId == 32152
				|| itemId == 32153) {
			SkillCapeCustomizer.startCustomizing(player, itemId);
			return;
		}
		if (itemId >= 15084 && itemId <= 15100) {
			player.getDialogueManager().startDialogue("DiceBag", itemId);
			return;
		}
		if (itemId == 24437 || itemId == 24439 || itemId == 24440 || itemId == 24441) {
			player.getDialogueManager().startDialogue("FlamingSkull", item, slotId);
			return;
		}
		if (itemId == 24338) {
			player.sendMessage("This weapon does not degrade!");
			return;
		}
		if (Equipment.getItemSlot(itemId) == Equipment.SLOT_AURA) {
			player.getAuraManager().sendTimeRemaining(itemId);
			return;
		}
		// ags
		if (itemId == 11694) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to dismantle your godsword!");
				return;
			}
			player.getInventory().deleteItem(11694, 1);
			player.getInventory().addItem(11702, 1);
			player.getInventory().addItem(11690, 1);
		}

		// dragonbone mage hat
		if (itemId == 24354) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the upgrade kit!");
				return;
			}
			player.getInventory().deleteItem(24354, 1);
			player.getInventory().addItem(24352, 1);
			player.getInventory().addItem(6918, 1);
		}
		// dragonbone mage top
		if (itemId == 24355) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the upgrade kit!");
				return;
			}
			player.getInventory().deleteItem(24355, 1);
			player.getInventory().addItem(24352, 1);
			player.getInventory().addItem(6916, 1);
		}
		// dragonbone mage bot
		if (itemId == 24356) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the upgrade kit!");
				return;
			}
			player.getInventory().deleteItem(24356, 1);
			player.getInventory().addItem(24352, 1);
			player.getInventory().addItem(6924, 1);
		}
		// dragonbone mage boots
		if (itemId == 24358) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the upgrade kit!");
				return;
			}
			player.getInventory().deleteItem(24358, 1);
			player.getInventory().addItem(24352, 1);
			player.getInventory().addItem(6920, 1);
		}
		// dragonbone mage gloves
		if (itemId == 24357) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the upgrade kit!");
				return;
			}
			player.getInventory().deleteItem(24357, 1);
			player.getInventory().addItem(24352, 1);
			player.getInventory().addItem(6922, 1);
		}
		// dragonbone full helm
		if (itemId == 24359) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the upgrade kit!");
				return;
			}
			player.getInventory().deleteItem(24359, 1);
			player.getInventory().addItem(24352, 1);
			player.getInventory().addItem(11335, 1);
		}
		// dragonbone plate
		if (itemId == 24360) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the upgrade kit!");
				return;
			}
			player.getInventory().deleteItem(24360, 1);
			player.getInventory().addItem(24352, 1);
			player.getInventory().addItem(14479, 1);
		}
		// dragonbone legs
		if (itemId == 24363) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the upgrade kit!");
				return;
			}
			player.getInventory().deleteItem(24363, 1);
			player.getInventory().addItem(24352, 1);
			player.getInventory().addItem(4087, 1);
		}
		// dragonbone skirt
		if (itemId == 24364) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the upgrade kit!");
				return;
			}
			player.getInventory().deleteItem(24364, 1);
			player.getInventory().addItem(24352, 1);
			player.getInventory().addItem(4585, 1);
		}
		// dragonbone boots
		if (itemId == 24362) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the upgrade kit!");
				return;
			}
			player.getInventory().deleteItem(24362, 1);
			player.getInventory().addItem(24352, 1);
			player.getInventory().addItem(11732, 1);
		}
		// dragonbone gloves
		if (itemId == 24361) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the upgrade kit!");
				return;
			}
			player.getInventory().deleteItem(24361, 1);
			player.getInventory().addItem(24352, 1);
			player.getInventory().addItem(7461, 1);
		}
		// abyssal vine whip
		if (itemId == 21371) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove the whip vine!");
				return;
			}
			player.getInventory().deleteItem(21371, 1);
			player.getInventory().addItem(4151, 1);
			player.getInventory().addItem(21369, 1);
		}
		// Dragon full helm (or)
		if (itemId == 19336) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(19336, 1);
			player.getInventory().addItem(11335, 1);
			player.getInventory().addItem(19346, 1);
		}
		// Dragon platebody (or)
		if (itemId == 19337) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(19337, 1);
			player.getInventory().addItem(14479, 1);
			player.getInventory().addItem(19350, 1);
		}
		// Dragon platelegs (or)
		if (itemId == 19338) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(19338, 1);
			player.getInventory().addItem(4087, 1);
			player.getInventory().addItem(19348, 1);
		}
		// Dragon plateskirt (or)
		if (itemId == 19339) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(19339, 1);
			player.getInventory().addItem(4585, 1);
			player.getInventory().addItem(19348, 1);
		}
		// Dragon sq shield (or)
		if (itemId == 19340) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(19340, 1);
			player.getInventory().addItem(1187, 1);
			player.getInventory().addItem(19352, 1);
		}
		// Dragon full helm (sp)
		if (itemId == 19341) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(19341, 1);
			player.getInventory().addItem(11335, 1);
			player.getInventory().addItem(19354, 1);
		}
		// Dragon platebody (sp)
		if (itemId == 19342) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(19342, 1);
			player.getInventory().addItem(14479, 1);
			player.getInventory().addItem(19358, 1);
		}
		// Dragon platelegs (sp)
		if (itemId == 19343) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(19343, 1);
			player.getInventory().addItem(4087, 1);
			player.getInventory().addItem(19356, 1);
		}
		// Dragon plateskirt (sp)
		if (itemId == 19344) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(19344, 1);
			player.getInventory().addItem(4585, 1);
			player.getInventory().addItem(19356, 1);
		}
		// Dragon sq shield (sp)
		if (itemId == 19345) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(19345, 1);
			player.getInventory().addItem(1187, 1);
			player.getInventory().addItem(19360, 1);
		}
		if (itemId == 4079) {
			player.setNextAnimationNoPriority(new Animation(1459));
			return;
		}
		// Dragon kiteshield (sp)
		if (itemId == 25321) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(item);
			player.getInventory().addItem(24365, 1);
			player.getInventory().addItem(25314, 1);
		}
		// Dragon kiteshield (or)
		if (itemId == 25320) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(item);
			player.getInventory().addItem(24365, 1);
			player.getInventory().addItem(25312, 1);
		}
		if (itemId == 19335) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to remove your ornament kit!");
				return;
			}
			player.getInventory().deleteItem(19335, 1);
			player.getInventory().addItem(6585, 1);
			player.getInventory().addItem(19333, 1);
		}
		if (itemId == 11696) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to dismantle your godsword!");
				return;
			}
			player.getInventory().deleteItem(11696, 1);
			player.getInventory().addItem(11704, 1);
			player.getInventory().addItem(11690, 1);
			return;
		}
		if (itemId == 11698) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to dismantle your godsword!");
				return;
			}
			player.getInventory().deleteItem(11698, 1);
			player.getInventory().addItem(11706, 1);
			player.getInventory().addItem(11690, 1);
			return;
		}
		if (itemId == 11700) {
			if (player.getInventory().getFreeSlots() < 1) {
				player.sendMessage("You need at least 1 free empty inventory slots to dismantle your godsword!");
				return;
			}
			player.getInventory().deleteItem(11700, 1);
			player.getInventory().addItem(11708, 1);
			player.getInventory().addItem(11690, 1);
			return;
		}
		if (player.getCharges().checkCharges(item))
			return;
		if (Settings.DEBUG)
			System.out.println("Option 3; slotId: " + slotId + "; itemId: " + itemId + ".");
	}

	public static void handleItemOption4(Player player, int slotId, int itemId, Item item) {
		if (Settings.DEBUG)
			System.out.println("Option 4; slotId: " + slotId + "; itemId: " + itemId + ".");
		if (itemId == 18778) {
			player.sendMessage("It sounds like something is contained within this relic.");
			return;
		}
	}

	public static void handleItemOption5(Player player, int slotId, int itemId, Item item) {

		if (Settings.DEBUG)
			System.out.println("Option 5; slotId: " + slotId + "; itemId: " + itemId + ".");
	}

	public static void handleItemOption6(Player player, int slotId, int itemId, Item item) {
		long time = Utils.currentTimeMillis();
		if (player.getLockDelay() >= time || player.getEmotesManager().getNextEmoteEnd() >= time)
			return;
		player.stopAll(false, true);
		if (itemId == 4079) {
			player.setNextAnimationNoPriority(new Animation(1460));
			return;
		}

		if (item.getDefinitions().containsInventoryOption(3, "Drop-X")) {
			player.getTemporaryAttributtes().put(TemporaryAtributtes.Key.DROP_X, new Object[] { item, slotId });
			player.getPackets().sendInputIntegerScript(true,
					"How many " + item.getName().toLowerCase() + " would you like to drop?");
			return;
		}

		if (item.getDefinitions().isBindItem()) {
			player.getDungManager().bind(item, slotId);
			return;
		}

		if (itemId == 34205 || itemId == 34200 || itemId == 34210 || itemId == 34215) {
			player.getDialogueManager().startDialogue("SharkConsumeOption");
			return;
		}
		if (item.getName().toLowerCase().contains("kethsi ring")) {
			player.sendMessage("You cannot recharge this ring.");
			return;
		}
		if (Pots.emptyPot(player, item, slotId))
			return;
		if (ShadeSkull.detachSkull(player, item))
			return;
		if (itemId == 11113) {
			player.sendMessage("Your Skills necklace has ran out of charges.");
			return;
		}
		if (item.getDefinitions().containsOption("Powder") || item.getDefinitions().containsOption("Grind")) {
			int herbloreVersa = Herblore.isHerbloreSkill(new Item(233), item);
			if (herbloreVersa > -1) {
				player.getDialogueManager().startDialogue("HerbloreD", herbloreVersa, item, new Item(233));
				return;
			}
		}
		if (itemId == 20767 || itemId == 32151) {
			Magic.compCapeTeleport(player, 2274, 3340, 1);
			return;
		}
		if (itemId == 20769 || itemId == 20771 || itemId == 32152 || itemId == 32153) {
			player.getDialogueManager().startDialogue("CompCapeD");
			return;
		}
		if (itemId == 4155) {
			player.getInterfaceManager().sendInterface(1309);
			player.getPackets().sendIComponentText(1309, 37, "List Co-Op Partner");
			return;
		}
		if (item.getName().contains("Ring of slaying")) {
			player.getInterfaceManager().sendInterface(1309);
			player.getPackets().sendIComponentText(1309, 37, "List Co-Op Partner");
			return;
		}
		if (itemId == 15492 || itemId == 13263) {
			Slayer.dissasembleSlayerHelmet(player, itemId == 15492);
			return;
		}
		if (item.getName().contains("Bucket of") || item.getName().contains("ompost")) {
			player.getInventory().deleteItem(itemId, 1);
			player.getInventory().addItem(1925, 1);
			player.sendMessage("You empty the contents on the floor.");
			return;
		}
		if (item.getName().startsWith("Super ") || itemId == 6036) {
			player.getInventory().deleteItem(itemId, 1);
			player.getInventory().addItem(229, 1);
			player.sendMessage("You empty the contents on the floor.");
			return;
		}
		if (itemId == 15707) {
			player.sendMessage("You cannot customise your ring.");
			return;
		}
		if (itemId == 13263) {
			player.sendMessage("Now why on " + Settings.SERVER_NAME + " would I do this..?");
			return;
		}
		if (player.getToolBelt().addItem(item))
			return;
		if (player.getToolBeltNew().addItem(item))
			return;
		else if (itemId == 1438)
			RuneCrafting.locate(player, 3127, 3405);
		else if (itemId == 1440)
			RuneCrafting.locate(player, 3306, 3474);
		else if (itemId == 1442)
			RuneCrafting.locate(player, 3313, 3255);
		else if (itemId == 1444)
			RuneCrafting.locate(player, 3185, 3165);
		else if (itemId == 1446)
			RuneCrafting.locate(player, 3053, 3445);
		else if (itemId == 1448)
			RuneCrafting.locate(player, 2982, 3514);
		else if (itemId >= 20653 && itemId <= 20659)
			player.getDialogueManager().startDialogue("Transportation3", "Miscellania", new WorldTile(2581, 3845, 0),
					"Grand Exchange", new WorldTile(3164, 3468, 0), itemId);
		else if (itemId <= 1712 && itemId >= 1706 || itemId >= 10354 && itemId <= 10362)
			player.getDialogueManager().startDialogue("Transportation", "Edgeville", new WorldTile(3087, 3496, 0),
					"Karamja", new WorldTile(2918, 3176, 0), "Draynor Village", new WorldTile(3104, 3263, 0),
					"Al Kharid", new WorldTile(3293, 3163, 0), itemId);
		else if (itemId == 995) {
			if (player.isCanPvp()) {
				player.sendMessage("You cannot acess your money pouch within a player-vs-player zone.");
				return;
			}
			player.getMoneyPouch().addMoney(player.getInventory().getItems().getNumberOf(995), true);
		} else if (itemId == 1704 || itemId == 10352)
			player.getPackets().sendGameMessage("The amulet has ran out of charges.");
		else if (itemId >= 3853 && itemId <= 3867)
			player.getDialogueManager().startDialogue("Transportation", "Burthrope Games Room",
					new WorldTile(2880, 3559, 0), "Barbarian Outpost", new WorldTile(2519, 3571, 0), "Gamers' Grotto",
					new WorldTile(2970, 9679, 0), "Corporeal Beast", new WorldTile(2886, 4377, 0), itemId);
		if (itemId >= 11118 && itemId <= 11124) {
			player.getDialogueManager().startDialogue("Transportation", "Warriors' Guild", new WorldTile(2880, 3542, 0),
					"Champions' Guild", new WorldTile(3191, 3367, 0), "Monastery", new WorldTile(3051, 3491, 0),
					"Ranging Guild", new WorldTile(2655, 3441, 0), itemId);
			return;
		}
		if (itemId >= 2552 && itemId <= 2566) {
			player.getDialogueManager().startDialogue("Transportation", "Duel Arena", new WorldTile(3315, 3234, 0),
					"Castle Wars", new WorldTile(2442, 3088, 0), "Mobilising Armies", new WorldTile(2413, 2848, 0),
					"Fist of Guthix", new WorldTile(1679, 5599, 0), itemId);
			return;
		}
		if (itemId >= 11105 && itemId <= 11111) {
			player.getDialogueManager().startDialogue("Transportation", "Fishing Guild", new WorldTile(2614, 3382, 0),
					"Mining Guild", new WorldTile(3021, 3339, 0), "Crafting Guild", new WorldTile(2933, 3296, 0),
					"Cooking Guild", new WorldTile(3142, 3440, 0), itemId);
			return;
		}
		if (itemId == 1704 || itemId == 10362) {
			player.sendMessage("The amulet has ran out of charges.");
			return;
		}
		if (Settings.DEBUG)
			System.out.println("Option 6; slotId: " + slotId + "; itemId: " + itemId + ".");
	}

	public static void handleItemOption7(Player player, int slotId, int itemId, Item item) {
		if (Settings.DEBUG)
			System.out.println("Option 7; slotId: " + slotId + "; itemId: " + itemId + ".");
		long time = Utils.currentTimeMillis();
		if (player.getLockDelay() >= time || player.getEmotesManager().getNextEmoteEnd() >= time)
			return;
		if (!player.getControlerManager().canDropItem(item))
			return;
		if (!player.getNewQuestManager().canDropItem(item))
			return;
		player.stopAll(false, true);
		if (LendingManager.isLendedItem(player, item)) {
			Lend lend = LendingManager.getLend(player);
			if (lend != null && lend.getItem().getDefinitions().getLendId() == item.getId())
				player.getDialogueManager().startDialogue("DiscardLend", lend);
			return;
		}
		if (player.getPetManager().spawnPet(itemId, true))
			return;
		if (item.getDefinitions().isOverSized()) {
			player.sendMessage(Colors.red + "The item appears to be oversized.", true);
			player.getInventory().deleteItem(item);
			return;
		}
		if (!ItemConstants.isTradeable(item) || item.getDefinitions().isDestroyItem()) {
			player.getDialogueManager().startDialogue("DestroyItemOption", slotId, item);
			return;
		}
		if (player.getSkills().getTotalLevel(player) < 150) {
			player.sendMessage("You need at least a total level of 150 to do this.");
			return;
		}
		if (player.isWiki() || player.isIronMan() || player.isHCIronMan()) {
			// player.sendMessage("This account cannot drop an item.");
			player.getDialogueManager().startDialogue("DestroyItemOption", slotId, item);
			return;

			// return;
		}
		if (player.getUsername().equalsIgnoreCase(""))
			return;
		player.getInventory().deleteItem(slotId, item);// TODO
		World.updateGroundItem(item, new WorldTile(player), player, 60, 0);
		LoggingSystem.logItemDrop(player, item, player);
		player.getPackets().sendSound(2739, 0, 1);
	}

	public static void handleItemOption8(Player player, int slotId, int itemId, Item item) {
		if (Settings.DEBUG)
			System.out.println("Option 8; slotId: " + slotId + "; itemId: " + itemId + ".");
		player.getInventory().sendExamine(slotId);
	}
}