package com.rs.game.player.newquests.impl;

import java.util.concurrent.TimeUnit;

import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.newquests.Quest;
import com.rs.game.player.newquests.NewQuestManager.Progress;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Logger;

public class ErnestTheChicken extends Quest {

	private static final long serialVersionUID = -1584525134300198199L;

	public static Item OIL_CAN = new Item(277), PRESSURE_GAUGE = new Item(271), RUBBER_TUBE = new Item(276);

	private boolean[] levers = new boolean[6];

	private boolean poisonedPiranhas;

	@Override
	public void start(boolean accept) {
		player.closeInterfaces();
		if (accept) {
			progress = Progress.STARTED;
			sendConfigs();
		}
		player.getDialogueManager().startDialogue("Veronica", 285, accept);
	}

	@Override
	public void update() {
		switch (stage) {
		case -1:
			sendQuestInformation("<str>I have Spoken to Veronica", "", "", BLUE + "I need to find " + RED + "Ernst",
					BLUE + "He went into" + RED + " Draynor Manor" + BLUE + " and hasn't returned");
			break;
		case 0:
			sendQuestInformation("<str>I have Spoken to Veronica", "", "",
					"<str>I've spoken to Dr Oddenstein, and discovered Ernest is a chicken", "",
					BLUE + "I need to bring " + RED + "Dr Oddenstein " + BLUE + "parts for his machine",
					(player.getInventory().containsItem(OIL_CAN.getId(), 1) ? ("<str>1 Oil Can") : (RED + "1 Oil Can")),
					(player.getInventory().containsItem(PRESSURE_GAUGE.getId(), 1) ? ("<str>1 Pressure Gauge")
							: (RED + "1 Pressure Gauge")),
					(player.getInventory().containsItem(RUBBER_TUBE.getId(), 1) ? ("<str>1 Rubber Tube")
							: (RED + "1 Rubber Tube")));
			break;
		case 1:
			sendQuestInformation("<str>I have Spoken to Veronica", "", "",
					"<str>I have collected all the parts for the machine", "", "",
					"<str>Dr Oddenstein thanked me for helping fix his machine",
					"<str>We turned Ernest back to normal and he rewarded me", "<col=FF0000>QUEST COMPLETE!");
			break;
		}
	}

	@Override
	public void sendQuestJournalInterface(boolean show) {
		player.getInterfaceManager().sendInterface(1243);
		player.getPackets().sendGlobalConfig(699, 516);
		player.getPackets().sendGlobalString(359, getQuestInformation()[2]);// requirments
		player.getPackets().sendIComponentText(1243, 37, getQuestInformation()[5]);// Rewards
		if (show) {
			player.getPackets().sendHideIComponent(1243, 45, false);
			player.getPackets().sendHideIComponent(1243, 56, true);
			player.getPackets().sendHideIComponent(1243, 57, true);
		}
	}

	@Override
	public void finish() {
		progress = Progress.COMPLETED;
		stage = 1;
		sendConfigs();
		sendRewards();
	}

	private Item[] rewardItems = new Item[] { new Item(314, 300), new Item(995, 2000000), new Item(1945, 10) };

	@Override
	public void giveRewards() {
		// 2 spins squeal
		player.getSquealOfFortune().giveEarnedSpins(2);
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
	public void sendConfigs() {
		refreshLevers();
		player.getPackets().sendConfig(32, progress == Progress.STARTED ? 1 : progress == Progress.COMPLETED ? 3 : 0);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().sendRunScriptBlank(2165);
			}
		});
	}

	public void refreshLevers() {
		for (int i = 0; i < levers.length; i++) {
			player.getPackets().sendConfigByFile(1788 + i, levers[i] ? 0 : 1);
		}
		player.getPackets().sendConfigByFile(1794, (levers[4] && levers[5] && !levers[1]) ? 1 : 0);
		player.getPackets().sendConfigByFile(1795, (levers[3] && levers[5] && !levers[0]) ? 1 : 0);
		player.getPackets().sendConfigByFile(1796, (levers[1] && !levers[2] && !levers[4] && !levers[5]) ? 1 : 0);
		player.getPackets().sendConfigByFile(1797, (levers[3] && !levers[4] && !levers[5]) ? 1 : 0);
		player.getPackets().sendConfigByFile(1798, (levers[2] && levers[3] && levers[5]) ? 1 : 0);
		player.getPackets().sendConfigByFile(1799, (levers[5] && !levers[4]) ? 1 : 0);
		player.getPackets().sendConfigByFile(1800, (levers[3] && !levers[0] && !levers[5]) ? 1 : 0);
		player.getPackets().sendConfigByFile(1801, (levers[0] && levers[1] && !levers[2] && !levers[5]) ? 1 : 0);
		player.getPackets().sendConfigByFile(1802, (levers[3] && !levers[4]) ? 1 : 0);
	}

	@Override
	public boolean hasRequirments() {
		return true;
	}

	@Override
	public String[] getQuestInformation() {
		return new String[] { "Ernest the Chicken", "Speak to Veronica outside Draynor Manor.",
				"There aren't any requirements for this quest.", "None.",
				"None, but watch out for those trees in the manor grounds.",
				"4 Quests Point, 1M coins, 10 eggs, 300 feathers and 2 spins on the Squeal of Fortune." };
	}

	@Override
	public int getQuestPoints() {
		return 4;
	}

	@Override
	public int questId() {
		return 5;
	}

	@Override
	public int getRewardItemId() {
		return 314;
	}

	@Override
	public boolean processObjectClick1(WorldObject object) {
		if (object.getId() == 47424 || object.getId() == 47421) {// large door
			if (player.getY() != object.getY()) {
				player.getPackets().sendGameMessage("The doors won't open.");
				return false;
			}
			player.lock();
			player.addWalkSteps(player.getX(), object.getY() + 1, 1, false);
			WorldObject Door1 = World.getObjectWithId(new WorldTile(3109, object.getY(), object.getPlane()), 47424);
			WorldObject Door2 = World.getObjectWithId(new WorldTile(3108, object.getY(), object.getPlane()), 47421);
			WorldObject openedDoor1 = new WorldObject(47424, object.getType(), 2, 3109, object.getY() + 1,
					object.getPlane());
			WorldObject openedDoor2 = new WorldObject(47421, object.getType(), 0, 3108, object.getY() + 1,
					object.getPlane());
			World.removeObject(Door1);
			World.removeObject(Door2);
			World.spawnObject(openedDoor1);
			World.spawnObject(openedDoor2);
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						player.unlock();
						player.getPackets().sendGameMessage("The doors slam shut behind you.");
						World.spawnObject(Door1);
						World.spawnObject(Door2);
						World.removeObject(openedDoor1);
						World.removeObject(openedDoor2);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}

			}, 800, TimeUnit.MILLISECONDS);
			return false;
		} else if (object.getId() == 47657 || object.getId() == 47364) {
			String option = object.getDefinitions().getOption(1);
			if (option.equalsIgnoreCase("Climb-up")) {
				player.useStairs(-1, new WorldTile(player.getX(), player.getY() + 5, 1), 0, 1);
			} else if (option.equalsIgnoreCase("Climb-down")) {
				player.useStairs(-1, new WorldTile(player.getX(), player.getY() - 5, 0), 0, 1);
			}
			return false;
		} else if (object.getId() == 47574 || object.getId() == 47575) {
			String option = object.getDefinitions().getOption(1);
			if (option.equalsIgnoreCase("Climb-up")) {
				player.useStairs(828, new WorldTile(player.getX(), player.getY() - 2, player.getPlane() + 1), 1, 2);
			} else if (option.equalsIgnoreCase("Climb-down")) {
				player.useStairs(827, new WorldTile(player.getX(), player.getY() + 2, player.getPlane() - 1), 1, 2);
			}
			return false;
		} else if (object.getId() == 47404 || object.getId() == 160) {
			player.lock();
			if (object.getId() == 160) {
				player.setNextAnimation(new Animation(834));
				player.getPackets().sendGameMessage("The lever opens the secret door!");
			}
			player.addWalkSteps(player.getX(), object.getId() == 160 ? (player.getY() + 1) : (player.getY() - 1), 1,
					false);
			WorldObject Door1 = World.getObjectWithId(new WorldTile(3097, 3359, 0), 47531);
			WorldObject Door2 = World.getObjectWithId(new WorldTile(3097, 3358, 0), 47529);
			WorldObject openedDoor1 = new WorldObject(47531, Door1.getType(), 1, Door1.getX() - 1, Door1.getY(),
					Door1.getPlane());
			WorldObject openedDoor2 = new WorldObject(47529, Door2.getType(), 3, Door2.getX() - 1, Door2.getY(),
					Door2.getPlane());
			WorldTasksManager.schedule(new WorldTask() {
				int count = 0;

				@Override
				public void run() {
					switch (count) {
					case 1:
						player.addWalkSteps(object.getId() == 160 ? (player.getX() + 1) : (player.getX() - 1),
								player.getY(), 1, false);
						World.removeObject(Door1);
						World.removeObject(Door2);
						World.spawnObject(openedDoor1);
						World.spawnObject(openedDoor2);
						break;
					case 2:
						player.unlock();
						if (object.getId() == 47404)
							player.getPackets().sendGameMessage("You've found a secret door!");
						World.spawnObject(Door1);
						World.spawnObject(Door2);
						World.removeObject(openedDoor1);
						World.removeObject(openedDoor2);
						break;
					}
					count++;
				}

			}, 0, 1);
			return false;
		} else if (object.getId() == 133 || object.getId() == 32015) {
			String option = object.getDefinitions().getOption(1);
			if (option.equalsIgnoreCase("Climb-up")) {
				player.useStairs(828, new WorldTile(3092, 3361, 0), 1, 2);
			} else if (option.equalsIgnoreCase("Climb-down")) {
				player.useStairs(827, new WorldTile(3117, 9753, 0), 1, 2);
			}
			return false;
		} else if (object.getId() >= 146 && object.getId() <= 151) {// levers
			int index = getLeverIndex(object.getId());
			String[] letters = { "A", "B", "C", "D", "E", "F" };
			levers[index] = !levers[index];
			String upDown = (levers[index] ? "down" : "up");
			player.lock(1);
			player.setNextAnimation(new Animation(levers[index] ? 2140 : 2139));
			player.getPackets().sendGameMessage("You pull lever " + letters[index] + " " + upDown + ".");
			refreshLevers();
			player.getPackets().sendGameMessage("You hear a clunk.");
			return false;
		} else if (object.getId() == 47443) {
			player.lock();
			player.addWalkSteps(object.getX(), player.getY() < object.getY() ? object.getY() : (object.getY() - 1), 1,
					false);
			WorldObject Door = World.getObjectWithId(new WorldTile(3123, 3364, 0), 47443);
			WorldObject openedDoor = new WorldObject(47443, object.getType(), 0, Door.getX(), object.getY() - 1,
					object.getPlane());
			World.removeObject(Door);
			World.spawnObject(openedDoor);
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						player.unlock();
						World.spawnObject(Door);
						player.faceObject(Door);
						World.removeObject(openedDoor);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}

			}, 800, TimeUnit.MILLISECONDS);
			return false;
		} else if (object.getId() == 152) {
			if (!player.getInventory().containsItem(952, 1)) {
				player.getDialogueManager().startDialogue("SimplePlayerMessage",
						"I'm not looking through that with my hands!");
				return false;
			}
			player.resetWalkSteps();
			player.setNextAnimation(new Animation(830));
			player.lock();
			player.getPackets().sendGameMessage("You dig through the compost...");
			WorldTasksManager.schedule(new WorldTask() {
				@Override
				public void run() {
					player.unlock();
					boolean hasKey = player.getInventory().containsOneItem(275)
							|| player.getBank().containsItem(275, 1);
					if (hasKey) {
						player.getPackets().sendGameMessage("... but you find nothing of interest.");
						return;
					}
					if (!player.getInventory().addItem(new Item(275))) {
						player.getPackets().sendGameMessage("You don't have enough space.");
						return;
					}
					player.getPackets().sendGameMessage("... and find a small key.");
				}
			});
			return false;
		} else if (object.getId() == 131) {
			boolean hasKey = player.getInventory().containsOneItem(275);
			boolean outSide = player.getY() > object.getY();
			if (!hasKey && outSide) {
				player.getPackets().sendGameMessage("The door is locked.");
				return false;
			}
			if (outSide)
				player.getPackets().sendGameMessage("You unlock the door.");
			player.lock();
			player.addWalkSteps(object.getX(), outSide ? object.getY() : (object.getY() + 1), 1, false);
			WorldObject Door = World.getObjectWithId(new WorldTile(3107, 3368, 0), 131);
			WorldObject openedDoor = new WorldObject(131, 0, 2, Door.getX(), object.getY() + 1, object.getPlane());
			World.removeObject(Door);
			World.spawnObject(openedDoor);
			CoresManager.slowExecutor.schedule(new Runnable() {
				@Override
				public void run() {
					try {
						player.unlock();
						World.spawnObject(Door);
						player.faceObject(Door);
						World.removeObject(openedDoor);
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}

			}, 800, TimeUnit.MILLISECONDS);
			return false;
		} else if (object.getId() == 153) {
			player.lock();
			player.setNextAnimation(new Animation(881));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					player.unlock();
					player.getDialogueManager().startDialogue(new Dialogue() {

						@Override
						public void start() {
							stage = -1;
							sendPlayerDialogue(NORMAL, "There seems to be a pressure gauge in here...");
						}

						@Override
						public void run(int interfaceId, int componentId) {
							switch (stage) {
							case -1:
								if (!poisonedPiranhas) {
									stage = 1;
									player.applyHit(new Hit(player, 10, HitLook.REGULAR_DAMAGE));
									player.setNextForceTalk(new ForceTalk("Ow!"));
									player.getPackets().sendGameMessage("Something in the water bites you.");
									sendPlayerDialogue(NORMAL, "... and a lot of piranhas!",
											"I can't get the gauge out.");
									return;
								}
								stage = 0;
								sendPlayerDialogue(NORMAL, "... and a lot of dead fish.");
								break;
							case 0:
								boolean hasGauge = player.getInventory().containsOneItem(271)
										|| player.getBank().containsItem(271, 1);
								end();
								if (progress == Progress.COMPLETED || hasGauge
										|| player.getInventory().getFreeSlots() == 0)
									return;
								player.getInventory().addItem(new Item(271));
								break;
							case 1:
								end();
								break;
							}
						}

						@Override
						public void finish() {
						}
					});
				}

			}, 2);
			return false;
		}
		return true;
	}

	@Override
	public boolean canTakeItem(FloorItem item) {
		if (item.getId() == 276 || item.getId() == 277 || item.getId() == 272 || item.getId() == 273) {
			boolean hasItem = player.getInventory().containsOneItem(item.getId())
					|| player.getBank().containsItem(item.getId(), 1);
			if (hasItem || progress == Progress.COMPLETED)
				return false;
			World.removeGroundItem(player, item);
			return false;
		}
		return true;
	}

	@Override
	public boolean handleItemOnObject(WorldObject object, Item item) {
		if (object.getId() == 153 && item.getId() == 274) {
			if (poisonedPiranhas)
				return true;
			player.lock();
			player.setNextAnimation(new Animation(883));
			player.getPackets().sendGameMessage("You pour the poisoned fish food into the fountain.");
			player.getInventory().removeItems(item);
			WorldTasksManager.schedule(new WorldTask() {
				int count = 0;

				@Override
				public void run() {
					switch (count) {
					case 1:
						player.getPackets().sendGameMessage("The piranhas start eating the food...");
						break;
					case 2:
						player.unlock();
						poisonedPiranhas = true;
						player.getPackets().sendGameMessage("... then die and float to the surface.");
						stop();
						break;
					}
					count++;
				}

			}, 0, 1);
			return false;
		}
		return true;
	}

	public int getLeverIndex(int objectId) {
		int index = 5 - (151 - objectId);
		return index;
	}

	@Override
	public boolean processObjectClick2(WorldObject object) {
		if (object.getId() >= 146 && object.getId() <= 151) {
			// player.getPackets().sendGameMessage("The lever is "
			// +
			// (player.getPackets().getBitValue(object.getDefinitions().configFileId)
			// == 1 ? "up" : "down")
			// + ".");
			return false;
		}
		return true;
	}

	@Override
	public boolean handleObjectExamine(WorldObject object) {
		if (object.getId() >= 146 && object.getId() <= 151) {
			// player.getPackets()
			// .sendGameMessage("A lever pointing "
			// +
			// (player.getPackets().getBitValue(object.getDefinitions().configFileId)
			// == 1
			// ? "upwards" : "downwards")
			// + ".");
			return false;
		}
		return true;
	}

	@Override
	public boolean canUseItemOnItem(Item itemUsed, Item usedWith) {
		if ((itemUsed.getId() == 272 && usedWith.getId() == 273)
				|| (itemUsed.getId() == 273 && usedWith.getId() == 272)) {
			player.getInventory().removeItems(itemUsed, usedWith);
			player.getInventory().addItem(new Item(274));
			player.getPackets().sendGameMessage("You poison the fish food.");
			return false;
		}
		return true;
	}

	@Override
	public boolean processNPCClick1(NPC npc) {
		if (npc.getId() == 285) {
			player.getDialogueManager().startDialogue("Veronica", npc.getId());
			return false;
		} else if (npc.getId() == 286) {
			if (progress == Progress.NOT_STARTED || progress == Progress.COMPLETED) {
				player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
						"There is nothing going on atm.");
				return false;
			}
			player.getDialogueManager().startDialogue("ProfessorOddenstein", npc.getId());
			return false;
		}
		return true;
	}

	@Override
	public void teleportToStartPoint() {
		Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3111, 3328, 0));
	}

}
