package com.rs.game.player.newquests.impl;

import com.rs.game.Animation;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Inventory;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.dialogue.impl.quests.piratestreasure.Zambo;
import com.rs.game.player.newquests.Quest;
import com.rs.game.player.newquests.NewQuestManager.Progress;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.network.protocol.codec.decode.WorldPacketsDecoder;
import com.rs.utils.ShopsHandler;

public class PiratesTreasure extends Quest {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5130006272079686573L;
	public static final int KARAMJA_RUM = 431, CHEST_KEY = 432, PIRATE_MESSAGE = 433, CHEST = 7956;
	public static final Item[] CASKET_REWARDS = new Item[] { new Item(995, 1000000), new Item(1635), new Item(1605) };

	private boolean storedRum;
	private boolean atWydins;
	private boolean luthasTask;
	private boolean readPirateMessage;
	private int karamjaBananas;
	private boolean WydinEmployee;

	@Override
	public void start(boolean accept) {
		player.closeInterfaces();
		if (accept) {
			progress = Progress.STARTED;
			sendConfigs();
		}
		player.getDialogueManager().startDialogue("ReadbeardFrank", 375, accept);
	}

	@Override
	public void update() {
		switch (stage) {
		case -1:
			if (!atWydins && !isLegalRum()) {
				sendQuestInformation(
						BLUE + "I have spoken to " + RED + " Redbeard Frank." + BLUE + " He has agreed to tell me",
						BLUE + "the location of some " + RED + "treasure " + BLUE + "for some " + RED + "Karamja Rum.",
						"",
						isLuthasTask()
								? BLUE + "I have taken employment on the " + RED + "banana plantation" + BLUE
										+ ", as the"
								: BLUE + "I need to go to " + RED + "Karamja " + BLUE + "and buy some " + RED + "rum"
										+ BLUE + ". I hope it is not",
						isLuthasTask() ? RED + "Customs Officers " + BLUE + "might not notice the " + RED + "rum "
								+ BLUE + "if it is covered" : BLUE + "too expensive.",
						isLuthasTask() ? BLUE + "in " + RED + "bananas." : "", "",
						isLuthasTask() ? ((player.getInventory().containsItem(KARAMJA_RUM, 1) && isLegalRum())
								? (hasStoredRum() ? BLUE + "I have hidden my " + RED + "rum " + BLUE
										+ "in the crate. I should fill it with"
										: BLUE + "I'm sure I will be able to hide my " + RED + "rum " + BLUE
												+ "in the next crate")
								: BLUE + "Now all I need is some " + RED + " rum " + BLUE + "to hide in the next crate")
								: "",
						isLuthasTask() ? ((player.getInventory().containsItem(KARAMJA_RUM, 1) && isLegalRum())
								? (hasStoredRum()
										? RED + "bananas " + BLUE + "and speak to " + RED + "Luthas " + BLUE
												+ "to have it shipped over."
										: BLUE + "destined for " + RED + "Wydin's store" + BLUE + ".")
								: BLUE + "destined for " + RED + "Wydin's store" + BLUE + "...") : "");
			} else {
				sendQuestInformation("<str>I have spoken to Redbeard Frank. He has agreed to tell me",
						"<str>the location of some treasure for some Karamja Rum.", "",
						"<str>I have taken employment on the banana plantation, as the",
						"<str>Customs Officers might not notice the rum if it is covered", "<str>in bananas.", "",
						BLUE + "I have spoken to " + RED + "Luthas" + BLUE + ", and the crate has been shipped",
						BLUE + "to " + RED + "Wydin's store " + BLUE + "in " + RED + "Port Sarim" + BLUE
								+ ". Now all I have to do is get to",
						BLUE + "it...");
			}
			break;
		case 0:
			sendQuestInformation("<str>I have smuggled some rum off Karamja, and retrieved it",
					"<str>from the back room of Wydin's shop.",
					BLUE + "I have given the rum to " + RED + "Redbeard Frank." + BLUE + " He has told me",
					BLUE + "that the " + RED + "treasure " + BLUE + "is hidden in the chest in the upstairs",
					BLUE + "room of the " + RED + "Blue Moon Inn " + BLUE + "in " + RED + "Varrock.",
					hasReadPirateMessage()
							? BLUE + "The note reads " + RED + "'Visit the city of the White Knights. In the"
							: (player.getInventory().containsItem(PIRATE_MESSAGE, 1)
									? BLUE + "I have opened the chest in the " + RED + "Blue Moon" + BLUE
											+ ", and found a"
									: (player.getInventory().containsItem(CHEST_KEY, 1)
											? BLUE + "I have a " + RED + "key " + BLUE
													+ "that can be used to unlock the chest that"
											: BLUE + "I have lost the " + RED + "key " + BLUE + "that " + RED
													+ "Readbeard Frank ")),
					hasReadPirateMessage() ? RED + "park, Saradomin points to the X which marks the spot.'"
							: (player.getInventory().containsItem(PIRATE_MESSAGE, 1)
									? RED + "note " + BLUE + "inside. I think it will tell me where to dig."
									: (player.getInventory().containsItem(CHEST_KEY, 1) ? BLUE + "holds the treasure."
											: BLUE + "see if he has another.")),
					(hasReadPirateMessage() && (!player.getInventory().containsItem(PIRATE_MESSAGE, 1)
							&& !player.getBank().containsItem(PIRATE_MESSAGE, 1)))
									? BLUE + "It's a good job I remembered that, as I have lost the " + RED + "note."
									: "");
			break;
		case 1:
			sendQuestInformation("<str>The note reads 'Visit the city of the White Knights. In the",
					"<str>park, Saradomin points to the X which marks the spot.'", "<col=FF0000>QUEST COMPLETE!",
					BLUE + "I've found a treasure, gained 2 Quest Points and gained",
					BLUE + "access to the Pay-fare option to travel to and from", BLUE + "Karamja!");
			break;
		}
	}

	@Override
	public void sendQuestJournalInterface(boolean show) {
		player.getInterfaceManager().sendInterface(1243);
		player.getPackets().sendGlobalConfig(699, 520);
		player.getPackets().sendIComponentText(1243, 19, getQuestInformation()[1]);// Start
																					// point
		player.getPackets().sendGlobalString(359, getQuestInformation()[2]);// requirments
		player.getPackets().sendIComponentText(1243, 25, getQuestInformation()[3]);// Required
																					// Items
		player.getPackets().sendIComponentText(1243, 34, getQuestInformation()[4]);// Combat
		player.getPackets().sendIComponentText(1243, 37, getQuestInformation()[5]);// Rewards
		if (getQuestInformation()[5].length() > 10) {

		}
		if (show) {
			player.getPackets().sendHideIComponent(1243, 45, false);
			player.getPackets().sendHideIComponent(1243, 56, true);
			player.getPackets().sendHideIComponent(1243, 57, true);
		}
	}

	@Override
	public void finish() {
		stage = 1;
		progress = Progress.COMPLETED;
		sendConfigs();
		sendRewards();
	}

	private Item[] rewardItems = new Item[] { new Item(7956, 1) };

	@Override
	public void giveRewards() {
		for (int i = 0; i < rewardItems.length; i++) {
			Item item = rewardItems[i];
			if (item == null)
				continue;
			if (!player.getInventory().addItemMoneyPouch(item)) {
				player.getBank().addItem(item.getId(), item.getAmount(), true);
				player.getPackets().sendGameMessage(item.getName() + " has been added to your bank.");
			}
		}
	}

	@Override
	public void teleportToStartPoint() {
		Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3054, 3252, 0));
	}

	@Override
	public void sendConfigs() {
		player.getPackets().sendConfig(71, progress == Progress.STARTED ? 1 : progress == Progress.COMPLETED ? 4 : 0);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().sendRunScriptBlank(2165);
			}
		});
	}

	@Override
	public boolean hasRequirments() {
		return true;
	}

	@Override
	public String[] getQuestInformation() {
		return new String[] { "Pirate's Treasure",
				"I can start this quest by speaking to Redbeard Frank who is at Port Sarim.",
				"There aren't any requirements for this quest.", "None.", "None.",
				"2 Quests Point, Access to One-Eye Hector's Treasure Chest, You can also use the Pay-fare option to go to and from Karamja." };
	}

	@Override
	public int getQuestPoints() {
		return 2;
	}

	@Override
	public int questId() {
		return 9;
	}

	@Override
	public int getRewardItemId() {
		return CHEST;
	}

	public boolean hasStoredRum() {
		return storedRum;
	}

	public void setStoredRum(boolean storedRum) {
		this.storedRum = storedRum;
	}

	public boolean isAtWydins() {
		return atWydins;
	}

	public void setAtWydins(boolean atWydins) {
		this.atWydins = atWydins;
	}

	public boolean isLuthasTask() {
		return luthasTask;
	}

	public void setLuthasTask(boolean luthasTask) {
		this.luthasTask = luthasTask;
	}

	public boolean hasReadPirateMessage() {
		return readPirateMessage;
	}

	public void setReadPirateMessage(boolean readPirateMessage) {
		this.readPirateMessage = readPirateMessage;
	}

	public int getKaramjaBananas() {
		return karamjaBananas;
	}

	public void setKaramjaBananas(int karamjaBananas) {
		this.karamjaBananas = karamjaBananas;
	}

	public boolean isWydinEmployee() {
		return WydinEmployee;
	}

	public void setWydinEmployee(boolean wydinEmployee) {
		WydinEmployee = wydinEmployee;
	}

	private boolean legalRum;

	public boolean isLegalRum() {
		return legalRum;
	}

	public void setLegalRum(boolean legalRum) {
		this.legalRum = legalRum;
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 375) {// Readbeard frank
			player.getDialogueManager().startDialogue("ReadbeardFrank", npc.getId());
			return false;
		} else if (npc.getId() == 379) {// Luthas
			player.getDialogueManager().startDialogue("Luthas", npc.getId());
			return false;
		} else if (npc.getId() == 568) {// Zambo
			player.getDialogueManager().startDialogue("Zambo", npc.getId());
			return false;
		} else if (npc.getId() == 557) {// Wydin
			player.getDialogueManager().startDialogue("Wydin", npc.getId());
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick2(NPC npc) {
		if (npc.getId() == 568) {// Zambo
			ShopsHandler.openShop(player, Zambo.SHOP_KEY);
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 2072) {// karamja crate search
			if (!isLuthasTask()) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You find nothing in the crate.");
				return false;
			}
			player.getDialogueManager().startDialogue("SimpleMessage", "This crate contains " + getKaramjaBananas()
					+ " bananas. " + (hasStoredRum() ? "And it looks like there is a rum hidden in there too." : ""));
			return false;
		} else if (object.getId() == 2069) {// Wydin Door
			boolean isOnWydinSide = (player.getX() >= 3012 && player.getX() <= 3018 && player.getY() >= 3203
					&& player.getY() <= 3206);
			if (player.getEquipment().getChestId() != 1005 && isOnWydinSide) {
				player.getDialogueManager().startDialogue("Wydin", 557, true);
				return false;
			}
			if (World.isSpawnedObject(object)) {
				return false;
			}
			WorldObject openedDoor = new WorldObject(object.getId(), object.getType(), (object.getRotation() + 1) & 0x3,
					object.getX(), object.getY(), object.getPlane());
			World.spawnObjectTemporary(openedDoor, 100);
			player.setNextWorldTile(isOnWydinSide ? new WorldTile(3011, 3204, 0) : new WorldTile(3012, 3204, 0));
			return false;
		} else if (object.getId() == 2071) {// Wydin crate
			if (player.getInventory().getFreeSlots() == 0) {
				player.getPackets().sendGameMessage("Not enough inventory space.");
				return false;
			}
			player.getPackets().sendGameMessage("There are lots of bananas in the crate.");
			player.lock(2);
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					if (isAtWydins()) {
						if (player.getInventory().addItem(PiratesTreasure.KARAMJA_RUM, 1)) {
							setAtWydins(false);
							setStoredRum(false);
							setLegalRum(true);
							player.setNextAnimation(new Animation(832));
							player.getPackets().sendGameMessage("You find your bottle of rum in amongst the bananas.");
						}
						return;
					}
					player.getDialogueManager().startDialogue(new Dialogue() {
						@Override
						public void start() {
							stage = 0;
							sendOptionsDialogue("Do you want to take a banana?", "Yes.", "No.");
						}

						@Override
						public void run(int interfaceId, int componentId) {
							switch (stage) {
							case 0:
								switch (componentId) {
								case OPTION_1:
									if (player.getInventory().addItem(1963, 1)) {
										player.setNextAnimation(new Animation(832));
										player.getPackets().sendGameMessage("You take a banana.");
									}
									end();
									break;
								case OPTION_2:
									end();
									break;
								}
								break;
							}
						}

						@Override
						public void finish() {
						}
					});
					return;
				}
			});
			return false;
		}
		return true;
	}

	@Override
	public boolean processObjectClick2(WorldObject object) {
		if (object.getId() == 2072) {// karamja crate fill
			if (!isLuthasTask()) {
				player.getDialogueManager().startDialogue("SimpleMessage", "You must get task from luthas first.");
				return false;
			}
			int amountBananas = player.getInventory().getAmountOf(1963);
			if (amountBananas == 0)
				return false;
			int amountNeededToFull = 10 - getKaramjaBananas();
			if (amountNeededToFull == 0)
				return false;
			if (amountBananas < amountNeededToFull)
				amountNeededToFull = amountBananas;
			player.getInventory().deleteItem(1963, amountNeededToFull);
			player.setNextAnimation(new Animation(832));
			setKaramjaBananas(getKaramjaBananas() + amountNeededToFull);
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You pack " + amountNeededToFull + " in to the crate.");
			return false;
		}
		return true;
	}

	@Override
	public boolean handleItemOnObject(WorldObject object, Item item) {
		int itemId = item.getId();
		if (object.getId() == 2079 && itemId == PiratesTreasure.CHEST_KEY) {
			if (!player.getInventory().addItem(PiratesTreasure.PIRATE_MESSAGE, 1))
				return false;
			player.getInventory().deleteItem(PiratesTreasure.CHEST_KEY, 1);
			player.getPackets().sendGameMessage("You unlock the chest.");
			player.lock();
			player.getPackets().sendGameMessage("All that's in the chest is a message...");
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.unlock();
					player.getPackets().sendGameMessage("You take the message from the chest.");
				}
			}, 1);
			return false;
		} else if (object.getId() == 2072 && (itemId == 1963 || itemId == PiratesTreasure.KARAMJA_RUM)) {
			if (!isLuthasTask()) {
				player.getPackets().sendGameMessage("You have to get a task from luthas first.");
				return false;
			}
			if (itemId == 431) {
				if (hasStoredRum()) {
					player.getPackets().sendGameMessage("There's already some rum in here...");
					return false;
				}
				if (player.getInventory().removeItems(new Item(PiratesTreasure.KARAMJA_RUM, 1))) {
					player.setNextAnimation(new Animation(832));
					setStoredRum(true);
					player.getDialogueManager().startDialogue("SimpleMessage", "You stash the rum in the crate.");
				}
				return false;
			}
			int current = getKaramjaBananas();
			if (current == 10) {
				player.getPackets().sendGameMessage("The crate is already full.");
				return false;
			}
			if (player.getInventory().removeItems(new Item(1963, 1))) {
				player.lock(2);
				player.setNextAnimation(new Animation(832));
				player.getDialogueManager().startDialogue("SimpleMessage", "You pack a banana into the crate.");
				setKaramjaBananas(current + 1);
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean processButtonClick(int interfaceId, int componentId, int slotId, int slotId2, int packetId) {
		if (interfaceId == Inventory.INVENTORY_INTERFACE) {
			Item item = player.getInventory().getItem(slotId);
			if (item == null || item.getId() != slotId2)
				return false;
			int itemId = item.getId();
			if (packetId == WorldPacketsDecoder.ACTION_BUTTON1_PACKET) {
				if (itemId == PiratesTreasure.PIRATE_MESSAGE) {
					setReadPirateMessage(true);
					player.getInterfaceManager().sendInterface(220);
					player.getPackets().sendIComponentText(220, 8,
							"<col=000000>Visit the city of the White Knights. In the park,");
					player.getPackets().sendIComponentText(220, 9,
							"<col=000000>Saradomin points to the X which marks the spot.");
					return false;
				} else if (itemId == PiratesTreasure.CHEST) {
					player.lock();
					player.getPackets().sendGameMessage("You open the casket...");
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							for (int i = 0; i < PiratesTreasure.CASKET_REWARDS.length; i++) {
								Item item = PiratesTreasure.CASKET_REWARDS[i];
								if (item == null)
									continue;
								if (!player.getInventory().addItem(item)) {
									player.getBank().addItem(item.getId(), item.getAmount(), true);
									player.getPackets()
											.sendGameMessage(item.getName() + " has been added to you bank.");
								}
							}
							player.getInventory().deleteItem(PiratesTreasure.CHEST, 1);
							player.getPackets().sendGameMessage(
									"You find 450 Coins, a Gold ring, and an Emerald inside the casket.");
							player.unlock();
						}
					});
					return false;
				}
			}
			return true;
		}
		return true;
	}

	@Override
	public boolean canTakeItem(FloorItem item) {
		if (item.getId() == 7957) {
			if (World.removeGroundItem(player, item, false)) {
				player.getInventory().addItemMoneyPouch(new Item(1005));
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean useDig() {
		if ((player.getX() == 2999 && player.getY() == 3383) || (player.getX() == 3000 && player.getY() == 3383)) {
			if (stage == 0) {
				player.getPackets().sendGameMessage("..and find a little chest of treasure.");
				finish();
				return false;
			}
			player.getPackets().sendGameMessage("..and find nothing.");
			return false;
		}
		return true;
	}

}
