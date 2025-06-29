package com.rs.game.player.controllers;

import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.content.ArtisansWorkShop;
import com.rs.game.player.content.SkillsDialogue;
import com.rs.game.player.content.ArtisansWorkShop.ArtisansWorkShopAction;
import com.rs.game.player.content.ArtisansWorkShop.Ingot;
import com.rs.game.player.content.ArtisansWorkShop.Track;
import com.rs.game.player.content.SkillsDialogue.ItemNameFilter;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;

public class ArtisansWorkShopControler extends Controller {

	private int currentInstruction = -1;
	private int selectedIngotType;
	private int amountToMake;

	@Override
	public void start() {
		sendInterfaces();
	}

	@Override
	public void process() {
		if (currentInstruction != ArtisansWorkShop.currentInstructions) {
			currentInstruction = ArtisansWorkShop.currentInstructions;
			player.getPackets().sendIComponentText(1073, 11, ArtisansWorkShop.getInstructionText());
		}
	}

	@Override
	public void sendInterfaces() {
		if (isInsideArtisansBurial(player)) {
			player.getInterfaceManager().sendOverlay(1073, false);
			player.getPackets().sendIComponentText(1073, 11, ArtisansWorkShop.getInstructionText());
		}
	}

	@Override
	public void forceClose() {
		player.getInterfaceManager().closeOverlay(false);
	}

	@Override
	public void moved() {
		if (!isInsideArtisansShop(player)) {
			forceClose();
			removeControler();
		} else {
			if (!isInsideArtisansBurial(player)) {
				player.getInterfaceManager().closeOverlay(false);
			} else {
				player.getInterfaceManager().sendOverlay(1073, false);
				player.getPackets().sendIComponentText(1073, 11, ArtisansWorkShop.getInstructionText());
			}
		}
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		if (interfaceId == 1072) {
			switch (componentId) {
			case 138:
			case 150:
			case 162:
			case 174:
				selectedIngotType = (componentId - 138) / 12;
				refreshIngotInterface();
				break;
			case 31:
			case 32:
				boolean add = componentId == 31;
				amountToMake = amountToMake + (add ? (amountToMake == 28 ? 0 : 1) : (amountToMake == 1 ? 0 : -1));
				refreshIngotInterface();
				break;
			case 201:
			case 213:
			case 225:
			case 237:
			case 249:
				int barType = (componentId - 201) / 12;
				Ingot ignot = Ingot.values()[barType * 4 + selectedIngotType];
				if (player.getSkills().getLevelForXp(Skills.SMITHING) < ignot.getRequiredLevel()) {
					player.getPackets().sendGameMessage(
							"You need a smithing level of " + ignot.getRequiredLevel() + " to make this ingot.");
					return false;
				}
				Item[] requiredItems = ignot.getRequiredItems();
				int availableAmount = 0;
				int ore1Index = ArtisansWorkShop.getOreIndex(requiredItems[0].getId());
				int ore2Index = requiredItems.length == 1 ? -1 : ArtisansWorkShop.getOreIndex(requiredItems[1].getId());
				if (requiredItems.length == 1) {
					availableAmount = player.ArtisansWorkShopSupplies[ore1Index] / requiredItems[0].getAmount();
				} else {
					availableAmount = Math.min(
							player.ArtisansWorkShopSupplies[ore1Index] / requiredItems[0].getAmount(),
							player.ArtisansWorkShopSupplies[ore2Index] / requiredItems[1].getAmount());
				}
				if (player.getInventory().getFreeSlots() == 0) {
					player.getPackets()
							.sendGameMessage("You don't have enough inventory space to withdraw this ingot!");
					player.closeInterfaces();
					return false;
				}
				if (amountToMake > player.getInventory().getFreeSlots()) {
					amountToMake = player.getInventory().getFreeSlots();
					player.getPackets()
							.sendGameMessage("You didn't have enough inventory space to withdraw all of the ingots.");
				}

				if (amountToMake > availableAmount) {
					amountToMake = availableAmount;
					if (amountToMake != 0)
						player.getPackets()
								.sendGameMessage("You didn't have enough ores to withdraw all of the ingots.");
				}
				if (amountToMake == 0) {
					player.getPackets().sendGameMessage("You don't have enough ores to withdraw this ingot!");
					player.closeInterfaces();
					return false;
				}
				player.ArtisansWorkShopSupplies[ore1Index] -= requiredItems[0].getAmount() * amountToMake;
				if (requiredItems.length > 1)
					player.ArtisansWorkShopSupplies[ore2Index] -= requiredItems[1].getAmount() * amountToMake;
				player.getInventory().addItem(ignot.getItemId(), amountToMake);
				player.getPackets().sendGameMessage("You withdraw " + amountToMake + " "
						+ ItemDefinitions.getItemDefinitions(ignot.getItemId()).getName() + ".");
				player.closeInterfaces();
				break;
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 29395 || object.getId() == 29394) {
			openWithdrawInterface(false);
			return false;
		} else if (object.getId() == 4046) {
			sendBurialArmourSkillsDialogue(object, null);
			return false;
		} else if (object.getId() == 29396) {
			boolean successful = false;
			for (Ingot ingot : Ingot.values()) {
				if (ingot == null || ingot.getProducts() == null)
					continue;
				for (int itemId : ingot.getProducts()) {
					if (player.getInventory().containsItem(itemId, 1)) {
						player.getInventory().deleteItem(itemId, player.getInventory().getAmountOf(itemId));
						successful = true;
					}
				}
			}
			player.getPackets().sendGameMessage(
					successful ? "You deposit the armour into the machine." : "You don't have anything to deposit.");
			return false;
		} else if (object.getId() == 29386 || object.getId() == 29385) {
			player.useStairs(-1, new WorldTile(3067, 9710, 0), 1, 2);
			return false;
		} else if (object.getId() == 29392) {
			player.useStairs(-1, new WorldTile(3061, 3335, 0), 1, 2);
			leaveArtisansUnderGround();
			return false;
		} else if (object.getId() == 29387) {
			player.useStairs(-1, new WorldTile(3035, 9713, 0), 1, 2);
			return false;
		} else if (object.getId() == 29391) {
			player.useStairs(-1, new WorldTile(3037, 3342, 0), 1, 2);
			leaveArtisansUnderGround();
			return false;
		} else if (object.getId() == 24823 || object.getId() == 24822 || object.getId() == 24821) {
			if (!player.getPerkManager().alchemicSmith) {
				player.getPackets().sendGameMessage("You need to have alchemic smith perk to use this area.");
				return false;
			}
			player.getDialogueManager().startDialogue(new Dialogue() {
				int productId;

				@Override
				public void start() {
					productId = object.getId() == 24823 ? 20504 : object.getId() == 24822 ? 20503 : 20502;
					SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE,
							"How many would you like to make?<br>Choose a number, then click the item to begin.", 28,
							new int[] { productId }, new ItemNameFilter() {
								@Override
								public String rename(String name) {
									int requiredLevel = (productId == 20504 ? 39 : productId == 20503 ? 15 : 1);
									if (player.getSkills().getLevel(Skills.SMITHING) < requiredLevel)
										name = "<col=ff0000>" + name + "<br><col=ff0000>Level " + requiredLevel;
									return name;

								}
							});
				}

				@Override
				public void run(int interfaceId, int componentId) {
					int amount = SkillsDialogue.getQuantity(player);
					if (player.getInventory().getFreeSlots() == 0) {
						player.getPackets().sendGameMessage("You don't have enough inventory space.");
						end();
						return;
					}
					if (amount > player.getInventory().getFreeSlots()) {
						amount = player.getInventory().getFreeSlots();
						player.getPackets()
								.sendGameMessage("You didn't have enough inventory space to take all of the ingots.");
					}
					if (amount == 0) {
						end();
						return;
					}
					player.getInventory().addItem(productId, amount);
					player.getPackets().sendGameMessage("You take " + amount + " "
							+ ItemDefinitions.getItemDefinitions(productId).getName().toLowerCase() + ".");
					end();
				}

				@Override
				public void finish() {

				}
			});
			return false;
		} else if (object.getId() == 24820) {
			if (!player.getPerkManager().alchemicSmith) {
				player.getPackets().sendGameMessage("You need to have alchemic smith perk to use this area.");
				return false;
			}
			sendTrackSkillsDialogue(object, null);
			return false;
		} else if (object.getId() == 24824) {
			if (!player.getPerkManager().alchemicSmith) {
				player.getPackets().sendGameMessage("You need to have alchemic smith perk to use this area.");
				return false;
			}
			boolean sucessful = false;
			for (Track track : Track.values()) {
				if (track == null || track.toString().contains("100"))
					continue;
				if (player.getInventory().containsItem(track.getItemId(), 1)) {
					int amount = player.getInventory().getAmountOf(track.getItemId());
					player.getInventory().deleteItem(track.getItemId(), amount);
					if (track.getRequiredItems().length > 1) {
						player.getSkills().addXp(Skills.SMITHING, track.getXp() * amount);
						player.getPackets().sendGameMessage("You get some xp for depositing "
								+ ItemDefinitions.getItemDefinitions(track.getItemId()).getName().toLowerCase() + ".");
					}
					sucessful = true;
				}
			}
			player.getPackets().sendGameMessage(
					sucessful ? "You deposit components" : "You don't have any component that you can deposit.");
			return false;
		} else if (object.getId() == 24825 || object.getId() == 24826 || object.getId() == 24843) {
			if (!player.getPerkManager().alchemicSmith) {
				player.getPackets().sendGameMessage("You need to have alchemic smith perk to use this area.");
				return false;
			}
			List<Track> tracks = new ArrayList<Track>();
			for (Track track : Track.values()) {
				if (track == null || !track.toString().contains("100"))
					continue;
				if (player.getInventory().containsItem(track.getItemId(), 1))
					tracks.add(track);
			}
			if (tracks.isEmpty()) {
				player.getPackets().sendGameMessage("You don't have any tracks to lay down.");
				return false;
			}
			WorldTile tile = new WorldTile(player);
			WorldTile toTile = new WorldTile(object.getRotation() == 0 ? object.getX() + 1 : object.getX(),
					object.getRotation() == 3 ? object.getY() + 1 : object.getY(), object.getPlane());
			player.lock();
			player.addWalkSteps(toTile.getX(), toTile.getY(), 5, false);
			WorldTasksManager.schedule(new WorldTask() {
				int count = 0;

				@Override
				public void run() {
					if (count == 2) {
						for (int i = 0; i < tracks.size(); i++) {
							Track track = tracks.get(i);
							if (track == null)
								continue;
							int amount = player.getInventory().getAmountOf(track.getItemId());
							player.getInventory().deleteItem(track.getItemId(), amount);
							player.getSkills().addXp(Skills.SMITHING, track.getXp() * amount);
						}
						player.getPackets().sendGameMessage("You lay the tracks down.");
					} else if (count == 4) {
						player.addWalkSteps(tile.getX(), tile.getY(), 5, false);
					} else if (count == 6) {
						player.unlock();
						stop();
					}
					count++;
				}

			}, 0, 1);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(WorldObject object) {
		if (object.getId() == 29395 || object.getId() == 29394) {
			boolean successful = false;
			for (Ingot ingot : Ingot.values()) {
				if (ingot == null)
					continue;
				int itemId = ingot.getItemId();
				if (player.getInventory().containsItem(itemId, 1)) {
					int amount = player.getInventory().getAmountOf(itemId);
					player.getInventory().deleteItem(itemId, amount);
					player.getPackets().sendGameMessage(
							"You return " + amount + " " + ItemDefinitions.getItemDefinitions(itemId).getName() + ".");
					for (Item item : ingot.getRequiredItems()) {
						int oreIndex = ArtisansWorkShop.getOreIndex(item.getId());
						player.ArtisansWorkShopSupplies[oreIndex] += item.getAmount() * amount;
					}
					successful = true;
				}
			}
			if (!successful)
				player.getPackets().sendGameMessage("You don't have any ingots to return to the machine.");
			return false;
		} else if (object.getId() == 24823 || object.getId() == 24822 || object.getId() == 24821) {
			if (!player.getPerkManager().alchemicSmith) {
				player.getPackets().sendGameMessage("You need to have alchemic smith perk to use this area.");
				return false;
			}
			int itemId = object.getId() == 24823 ? 20504 : object.getId() == 24822 ? 20503 : 20502;
			if (player.getInventory().containsItem(itemId, 1)) {
				int amount = player.getInventory().getAmountOf(itemId);
				player.getInventory().deleteItem(itemId, amount);
				player.getPackets().sendGameMessage("You return " + amount + " "
						+ ItemDefinitions.getItemDefinitions(itemId).getName().toLowerCase() + ".");
				return false;
			}
			player.getPackets().sendGameMessage("You don't have any ingots to return.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick3(WorldObject object) {
		if (object.getId() == 29395 || object.getId() == 29394) {
			sendDepositWithdrawDialogue(true);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick4(WorldObject object) {
		if (object.getId() == 29395 || object.getId() == 29394) {
			sendDepositWithdrawDialogue(false);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick5(WorldObject object) {
		return super.processObjectClick5(object);
	}

	@Override
	public boolean handleItemOnObject(WorldObject object, Item item) {
		if (object.getId() == 4046) {
			sendBurialArmourSkillsDialogue(object, item);
			return false;
		} else if (object.getId() == 24820) {
			if (!player.getPerkManager().alchemicSmith) {
				player.getPackets().sendGameMessage("You need to have alchemic smith perk to use this area.");
				return false;
			}
			sendTrackSkillsDialogue(object, item);
			return false;
		}
		return true;
	}

	public void sendBurialArmourSkillsDialogue(WorldObject object, Item item) {
		Ingot ingot = null;
		if (item != null) {
			ingot = Ingot.forId(item.getId());
		} else
			for (int i = Ingot.values().length - 1; i >= 0; i--) {
				Ingot cIngot = Ingot.values()[i];
				if (cIngot == null || cIngot.getProducts() == null
						|| !player.getInventory().containsItem(cIngot.getItemId(), 1))
					continue;
				ingot = cIngot;
				break;
			}
		if (ingot == null || ingot.getProducts() == null) {
			player.getPackets().sendGameMessage(
					item != null ? "You cant use this item on anvil." : "You don't have any bars to use on anvil.");
			return;
		}
		player.getDialogueManager().startDialogue(new Dialogue() {
			Ingot ingot;

			@Override
			public void start() {
				ingot = (Ingot) parameters[0];
				SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE,
						"How many would you like to make?<br>Choose a number, then click the item to begin.", 28,
						ingot.getProducts(), new ItemNameFilter() {
							@Override
							public String rename(String name) {
								if (player.getSkills().getLevel(Skills.SMITHING) < ingot.getRequiredLevel())
									name = "<col=ff0000>" + name + "<br><col=ff0000>Level " + ingot.getRequiredLevel();
								return name;

							}
						});
			}

			@Override
			public void run(int interfaceId, int componentId) {
				player.getActionManager().setAction(new ArtisansWorkShopAction(object, ingot,
						SkillsDialogue.getItemSlot(componentId), SkillsDialogue.getQuantity(player)));
				end();
			}

			@Override
			public void finish() {

			}
		}, ingot);
	}

	public void sendTrackSkillsDialogue(WorldObject object, Item item) {
		final List<Track> availableTracks = new ArrayList<Track>();
		int itemId = item == null ? (player.getInventory().containsItem(20504, 1) ? 20504
				: player.getInventory().containsItem(20503, 1) ? 20503
						: player.getInventory().containsItem(20502, 1) ? 20502 : -1)
				: item.getId();
		for (Track track : Track.values()) {
			if (track == null || (itemId != -1
					? (itemId >= 20502 && itemId <= 20504
							? (track.getRequiredItems().length > 1 || track.getRequiredItems()[0] != itemId)
							: (track.getRequiredItems().length == 1 || (track.getRequiredItems()[0] != itemId
									&& track.getRequiredItems()[1] != itemId)))
					: (!player.getInventory().containsItem(track.getRequiredItems()[0], 1)
							|| !player.getInventory().containsItem(track.getRequiredItems()[1], 1))))
				continue;
			availableTracks.add(track);
		}
		if (availableTracks.isEmpty()) {
			player.getPackets().sendGameMessage(
					item == null ? "You don't enough bars to make anything." : "You can't use this item on anvil.");
			return;
		}
		player.getDialogueManager().startDialogue(new Dialogue() {
			@Override
			public void start() {
				int[] products = new int[availableTracks.size()];
				for (int i = 0; i < products.length; i++)
					products[i] = availableTracks.get(i).getItemId();
				SkillsDialogue.sendSkillsDialogue(player, SkillsDialogue.MAKE,
						"How many would you like to make?<br>Choose a number, then click the item to begin.", 28,
						products, new ItemNameFilter() {
							int count = 0;

							@Override
							public String rename(String name) {
								if (player.getSkills().getLevel(Skills.SMITHING) < availableTracks.get(count)
										.getRequiredLevel())
									name = "<col=ff0000>" + name + "<br><col=ff0000>Level "
											+ availableTracks.get(count).getRequiredLevel();
								count++;
								return name;

							}
						});
			}

			@Override
			public void run(int interfaceId, int componentId) {
				player.getActionManager()
						.setAction(new ArtisansWorkShopAction(object,
								availableTracks.get(SkillsDialogue.getItemSlot(componentId)),
								SkillsDialogue.getQuantity(player)));
				end();
			}

			@Override
			public void finish() {

			}
		});
	}

	public void sendDepositWithdrawDialogue(final boolean deposit) {
		player.closeInterfaces();
		player.getDialogueManager().startDialogue(new Dialogue() {
			int itemId;

			@Override
			public void start() {
				sendOptionsDialogue("CHOOSE ORE YOU WANT TO " + (deposit ? "DEPOSIT" : "WITHDRAW"),
						(deposit ? "Deposit " : "Withdraw ") + "Coal",
						(deposit ? "Deposit " : "Withdraw ") + "Iron ore",
						(deposit ? "Deposit " : "Withdraw ") + "Mithril ore",
						(deposit ? "Deposit " : "Withdraw ") + "Adamantite ore",
						(deposit ? "Deposit " : "Withdraw ") + "Runite ore");
			}

			@Override
			public void run(int interfaceId, int componentId) {
				switch (stage) {
				case -1:
					itemId = componentId == OPTION_1 ? 453
							: componentId == OPTION_2 ? 440
									: componentId == OPTION_3 ? 447 : componentId == OPTION_4 ? 449 : 451;
					String option = (deposit ? "Deposit" : "Withdraw");
					stage = 0;
					sendOptionsDialogue(DEFAULT_OPTIONS_TITLE, option + " 1", option + " 5", option + " 10",
							option + " X", option + " All");
					break;
				case 0:
					end();
					switch (componentId) {
					case OPTION_1:
					case OPTION_2:
					case OPTION_3:
						ArtisansWorkShop.withdrawDepositOre(player, itemId,
								componentId == OPTION_1 ? 1 : componentId == OPTION_2 ? 5 : 10, deposit);
						break;
					case OPTION_4:
						player.getTemporaryAttributtes().put("ArtisansWorkShop", new Object[] { itemId, deposit });
						player.getPackets().sendInputIntegerScript(true,
								"Choose the amount you want to " + (deposit ? "deposit" : "withdraw") + ":");
						break;
					case OPTION_5:
						ArtisansWorkShop.withdrawDepositOre(player, itemId, Integer.MAX_VALUE, deposit);
						break;
					}
					break;
				}

			}

			@Override
			public void finish() {

			}
		});
	}

	public void leaveArtisansUnderGround() {
		for (Track track : Track.values()) {
			if (track == null || !player.getInventory().containsItem(track.getItemId(), 1))
				continue;
			int itemId = track.getItemId();
			int amount = player.getInventory().getAmountOf(itemId);
			player.getInventory().deleteItem(itemId, amount);
			if (track.getRequiredItems().length > 1)
				player.getSkills().addXp(Skills.SMITHING, track.getXp() * amount);
		}
		if (player.getInventory().containsItem(20502, 1))
			player.getInventory().deleteItem(20502, player.getInventory().getAmountOf(20502));
		if (player.getInventory().containsItem(20503, 1))
			player.getInventory().deleteItem(20503, player.getInventory().getAmountOf(20503));
		if (player.getInventory().containsItem(20504, 1))
			player.getInventory().deleteItem(20504, player.getInventory().getAmountOf(20504));
	}

	@Override
	public void magicTeleported(int type) {
		if (isInsideArtisansUnderGround(player))
			leaveArtisansUnderGround();
		forceClose();
		removeControler();
	}

	public static boolean isInsideArtisansShop(Player player) {
		WorldTile loc = new WorldTile(player);
		return (loc.getX() >= 3035 && loc.getX() <= 3040 && loc.getY() >= 3335 && loc.getY() <= 3342)
				|| (loc.getX() >= 3041 && loc.getX() <= 3050 && loc.getY() >= 3332 && loc.getY() <= 3345)
				|| (loc.getX() >= 3051 && loc.getX() <= 3061 && loc.getY() >= 3333 && loc.getY() <= 3344)
				|| (loc.getX() >= 3014 && loc.getX() <= 3071 && loc.getY() >= 9668 && loc.getY() <= 9723);
	}

	public static boolean isInsideArtisansUnderGround(Player player) {
		WorldTile loc = new WorldTile(player);
		return (loc.getX() >= 3014 && loc.getX() <= 3071 && loc.getY() >= 9668 && loc.getY() <= 9723);
	}

	public static boolean isInsideArtisansBurial(Player player) {
		WorldTile loc = new WorldTile(player);
		return (loc.getX() >= 3051 && loc.getX() <= 3061 && loc.getY() >= 3333 && loc.getY() <= 3344);
	}

	public void openWithdrawInterface(boolean IVIngot) {
		player.getInterfaceManager().sendInterface(1072);
		selectedIngotType = IVIngot ? 3 : selectedIngotType == 3 ? 0 : selectedIngotType;
		amountToMake = player.getInventory().getFreeSlots() == 0 ? 1 : player.getInventory().getFreeSlots();
		for (int i = 0; i < player.ArtisansWorkShopSupplies.length; i++) {
			player.getPackets().sendIComponentText(1072, 49 + (4 * i),
					player.ArtisansWorkShopSupplies[i] + (i == 0 ? "/8000" : "/4000"));
		}
		if (IVIngot) {
			player.getPackets().sendHideIComponent(1072, 140, true);
			player.getPackets().sendHideIComponent(1072, 152, true);
			player.getPackets().sendHideIComponent(1072, 129, true);
			player.getPackets().sendHideIComponent(1072, 133, true);
			player.getPackets().sendHideIComponent(1072, 134, true);
			player.getPackets().sendHideIComponent(1072, 136, true);
			player.getPackets().sendHideIComponent(1072, 138, true);
		} else {
			player.getPackets().sendHideIComponent(1072, 165, true);
			player.getPackets().sendHideIComponent(1072, 169, true);
			player.getPackets().sendHideIComponent(1072, 170, true);
			player.getPackets().sendHideIComponent(1072, 172, true);
			player.getPackets().sendHideIComponent(1072, 174, true);
		}
		refreshIngotInterface();
	}

	public void refreshIngotInterface() {
		player.getPackets().sendRunScript(4183, selectedIngotType == 0 ? 70254730
				: selectedIngotType == 1 ? 70254742 : selectedIngotType == 2 ? 70254754 : 70254766);
		player.getPackets().sendIComponentText(1072, 33, amountToMake + "");
		player.getPackets().sendConfigByFile(8877, amountToMake);
		player.getPackets().sendConfigByFile(8857, player.ArtisansWorkShopSupplies[1] + 1);
		player.getPackets().sendConfigByFile(8859, player.ArtisansWorkShopSupplies[2] + 1);
		player.getPackets().sendConfigByFile(8862, player.ArtisansWorkShopSupplies[3] + 1);
		player.getPackets().sendConfigByFile(8863, player.ArtisansWorkShopSupplies[4] + 1);
		player.getPackets().sendConfigByFile(8865, player.ArtisansWorkShopSupplies[0] + 1);
	}

}
