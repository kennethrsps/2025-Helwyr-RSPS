package com.rs.game.player;

import java.io.Serializable;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.MapBuilder;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.cutscenes.Cutscene;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.DTRank;
import com.rs.utils.Utils;

public final class DominionTower implements Serializable {

	public static final int ENDURANCE = 1, FREE = 1, MAX_FACTOR = 10000000;
	private static final long serialVersionUID = -5230255619877910203L;

	private transient Player player;
	private transient int[] mapBaseCoords;

	private int nextBossIndex;
	private int progress;
	public int dominionFactor;
	private long totalScore;
	private boolean talkedWithFace;
	public int killedBossesCount;
	private int maxFloorEndurance;

	@SuppressWarnings("unused")
	private static final int[] NORMAL_ARENA =
			{ 456, 768 }, LARGE_PILAR_ARENA =
				{ 456, 776 }, SMALL_PILAR_ARENA =
					{ 456, 784 }, PODIUM_ARENA =
						{ 456, 760 };

	public void setPlayer(Player player) {
		this.player = player;
	}

	public DominionTower() {
		nextBossIndex = -1;
	}

	public boolean hasRequiriments() {
		return player.getSkills().getCombatLevelWithSummoning() >= 110;
	}

	public void growFace() { 
		player.getPackets().sendSound(7913, 0, 2);
		player.getDialogueManager()
				.startDialogue(
						"SimpleMessage",
						"The face on the wall groans and cowls at you. Perhaps you should",
						" talk to it first.");
	}

	public void openModes() {
		if (!hasRequiriments()) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You do not meet the requirements to play this minigame!");
			return;
		}
		if (!talkedWithFace) {
			growFace();
			return;
		}
		if (progress == 256) {
			player.sendMessage("You can't go back into the arena, you must go to the next floor on entrance.");
			return;
		}
		player.getInterfaceManager().sendInterface(1164);
		player.getPackets().sendIComponentText(
				1164,
				27,
				progress == 0 ? "Ready for a new match" : "Floor progress: "
						+ progress);
	}

	public void handleButtons(int interfaceId, int componentId) {
		if (interfaceId == 1163) {
			if (componentId == 89)
				player.closeInterfaces();

		} else if (interfaceId == 1160) {
			if (componentId == 55)
				openRewards();
		} else if (interfaceId == 1168) {
			if (componentId == 255)
				player.closeInterfaces();
		} else if (interfaceId == 1156) {
			if (componentId == 88) {
				if (player.getDominionTower().getKilledBossesCount() < 100) {
					player.sendMessage("You need to have at least 100 DT Kills to obtain this.");
					return;
				}
				if (player.hasItem(new Item(22358))
						|| player.hasItem(new Item(22359))
						|| player.hasItem(new Item(22360))
						|| player.hasItem(new Item(22361))) {
					player.sendMessage("You already have this reward.");
					player.getPackets().sendVoice(7894);
					return;
				}
				player.getDialogueManager().startDialogue("Goliaths");
				player.getDominionTower().setKilledBossesCount(killedBossesCount - 100);
				return;
			} else if (componentId == 115) {
				if (player.getDominionTower().getKilledBossesCount() < 100) {
					player.sendMessage("You need to have at least 100 DT Kills to obtain this.");
					return;
				}
				if (player.hasItem(new Item(22366))
						|| player.hasItem(new Item(22367))
						|| player.hasItem(new Item(22368))
						|| player.hasItem(new Item(22369))) {
					player.sendMessage("You already have this reward.");
					player.getPackets().sendVoice(7894);
					return;
				}
				player.getDialogueManager().startDialogue("SpellCasters");
				player.getDominionTower().setKilledBossesCount(killedBossesCount - 100);
				return;
			} else if (componentId == 139) {
				if (player.getDominionTower().getKilledBossesCount() < 100) {
					player.sendMessage("You need to have at least 100 DT Kills to obtain this.");
					return;
				}
				if (player.hasItem(new Item(22362))
						|| player.hasItem(new Item(22363))
						|| player.hasItem(new Item(22364))
						|| player.hasItem(new Item(22365))) {
					player.sendMessage("You already have this reward.");
					player.getPackets().sendVoice(7894);
					return;
				}
				player.getDialogueManager().startDialogue("Swifts");
				player.getDominionTower().setKilledBossesCount(killedBossesCount - 100);
				return;
			} else if (componentId == 112) {
				if (player.getDominionTower().getKilledBossesCount() < 150) {
					player.sendMessage("You need to have at least 150 DT Kills to obtain this.");
					return;
				}
				if (player.getInventory().containsItem(22346, 1)
						|| player.getEquipment().getWeaponId() == 22346
						|| player.getBank().getItem(22346) != null) {
					player.sendMessage("You already have this reward.");
					player.getPackets().sendVoice(7894);
					return;
				}
				player.getInventory().addItem(22346, 1);
				player.getDominionTower().setKilledBossesCount(killedBossesCount - 150);
				return;
			} else if (componentId == 118) {
				if (player.getDominionTower().getKilledBossesCount() < 125) {
					player.sendMessage("You need to have at least 125 DT Kills to obtain this.");
					return;
				}
				if (player.getInventory().containsItem(22347, 1)
						|| player.getEquipment().getWeaponId() == 22347
						|| player.getBank().getItem(22347) != null) {
					player.sendMessage("You already have this reward.");
					player.getPackets().sendVoice(7894);
					return;
				}
				player.getInventory().addItem(22347, 1);
				player.getDominionTower().setKilledBossesCount(killedBossesCount - 125);
				return;
			} else if (componentId == 136) {
				if (player.getDominionTower().getKilledBossesCount() < 200) {
					player.sendMessage("You need to have at least 200 DT Kills to obtain this.");
					return;
				}
				if (player.getInventory().containsItem(22348, 1)
						|| player.getEquipment().getWeaponId() == 22348
						|| player.getBank().getItem(22348) != null) {
					player.sendMessage("You already have this reward.");
					player.getPackets().sendVoice(7894);
					return;
				}
				player.getInventory().addItem(22348, 1);
				player.getDominionTower().setKilledBossesCount(killedBossesCount - 200);
				return;
			}
		} else if (interfaceId == 1170) {
			if (componentId == 85)
				player.closeInterfaces();
		} else if (interfaceId == 1173) {
			if (componentId == 58)
				player.closeInterfaces();
			else if (componentId == 59)
				startEnduranceMode();
		} else if (interfaceId == 1164) {
			if (componentId == 28)
			    openEnduranceMode();
			else
				player.sendMessage("Only Endurance Mode is currently working.");
		} 
	}

	private static final int[] MUSICS = { 1015, 1022, 1018, 1016, 1021 };

	public static final class Boss {

		private String name;
		private String text;
		private int[] ids;
		private boolean forceMulti;
		private Item item;
		private int voice;
		private int[] arena;

		public Boss(String name, String text, int... ids) {
			this(name, text, -1, false, null, NORMAL_ARENA, ids);
		}

		public Boss(String name, String text, int voice, boolean forceMulti,
				Item item, int[] arena, int... ids) {
			this.name = name;
			this.text = text;
			this.forceMulti = forceMulti;
			this.ids = ids;
			this.item = item;
			this.voice = voice;
			this.arena = arena;
		}

		public boolean isForceMulti() {
			return forceMulti;
		}

		public String getName() {
			return name;
		}
	}

	private static final Boss[] BOSSES = {
			new Boss("Elvarg", "Grrrr", 14548),
			new Boss("Evil Chicken", "Bwak bwak bwak", 3375),
			new Boss("Bouncer", "Roaarr", 14483),
			new Boss("Black knight titan", "You will suffer my wrath!", -1,
					false, null, NORMAL_ARENA, 14436),
			new Boss("General Graardor", "CHAAAARGEE", 3220, true, null, NORMAL_ARENA, 6260),
			new Boss("K'ril Tsutsaroth", "ATTAACK!", 3282, true, null, NORMAL_ARENA, 6203),
			new Boss("Commander Zilyana", "Saradomin lend me strength!", 3263, true, null, NORMAL_ARENA, 6247),
			new Boss("Kree'Arra", "Storms align to me!", -1, true, null, NORMAL_ARENA, 6222), new Boss("Jad", null, 2745),
			new Boss("Barrelchest", null, 13579),
			new Boss("King Black Dragon", "Grrrr", 50),
			new Boss("Chaos elemental", "Fus-Ro-Dah!", 3200)};

	public void startEnduranceMode() {
		if (progress == 256) {
			player.getDialogueManager().startDialogue("SimpleMessage",
							"You have some dominion factor which you must exchange before ",
							"starting another match.");
			player.sendMessage("You can't go back into the arena, you must go to the next floor on entrance.");
			return;
		}
		createArena(ENDURANCE);
	}

	public void createArena(final int mode) {
		player.closeInterfaces();
		player.lock(15);
		CoresManager.slowExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					boolean needDestroy = mapBaseCoords != null;
					final int[] oldMapBaseCoords = mapBaseCoords;
					mapBaseCoords = MapBuilder.findEmptyChunkBound(8, 8);
					MapBuilder.copyAllPlanesMap(
							BOSSES[getNextBossIndex()].arena[0],
							BOSSES[getNextBossIndex()].arena[1],
							mapBaseCoords[0], mapBaseCoords[1], 8);
					teleportToArena(mode);
					if (needDestroy) {
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								CoresManager.slowExecutor
										.execute(new Runnable() {
											@Override
											public void run() {
												try {
													MapBuilder
															.destroyMap(
																	oldMapBaseCoords[0],
																	oldMapBaseCoords[1],
																	8, 8);
												} catch (Exception e) {
													e.printStackTrace();
												} catch (Error e) {
													e.printStackTrace();
												}
											}
										});
							}
						});
					}
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Error e) {
					e.printStackTrace();
				}

			}
		});
	}

	private void teleportToArena(int mode) {
		player.setNextFaceWorldTile(new WorldTile(getBaseX() + 11, getBaseY() + 29, 0));
		player.getControlerManager().startControler("DTControler", mode);
		player.setNextWorldTile(new WorldTile(getBaseX() + 10, getBaseY() + 29, 2));
		player.getMusicsManager().playMusic(MUSICS[Utils.getRandom(MUSICS.length - 1)]);
		player.unlock();
	}

	public String getStartFightText(int message) {
		switch (message) {
		case 0:
			return "Argh! Lets get this over with!";
		case 1:
			return "Prepare to die!";
		case 2:
			return "I'll crush you!";
		case 3:
			return "You don't stand a chance!";
		default:
			return "Bring it on, weakling!";
		}
	}

	public int getNextBossIndex() {
		if (nextBossIndex < 0 || nextBossIndex >= BOSSES.length)
			selectBoss();
		return nextBossIndex;
	}

	public void startFight(final NPC[] bosses) {
		for (NPC boss : bosses) {
			boss.setCantInteract(true);
			boss.setNextFaceWorldTile(new WorldTile(boss.getX() - 1, boss.getY(), 0));
		}
		player.lock(15);
		player.setNextWorldTile(new WorldTile(getBaseX() + 25, getBaseY() + 32, 2));
		player.setNextFaceWorldTile(new WorldTile(getBaseX() + 26, getBaseY() + 32, 0));
		final int index = getNextBossIndex();
		WorldTasksManager.schedule(new WorldTask() {

			private int count;

			@Override
			public void run() {
				if (count == 0) {
					player.getInterfaceManager().sendTab(
							player.getInterfaceManager()
									.hasRezizableScreen() ? 11 : 0, 1172);
					player.getPackets().sendHideIComponent(1172, 2, true);
					player.getPackets().sendHideIComponent(1172, 7, true);
					player.getPackets().sendIComponentText(1172, 4,
							player.getDisplayName());
					player.getPackets().sendConfig(1241, 1);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 25),
							Cutscene.getY(player, getBaseY() + 38), 1800);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 25),
							Cutscene.getY(player, getBaseY() + 29), 800);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 32),
							Cutscene.getY(player, getBaseY() + 38), 1800,
							6, 6);
				} else if (count == 1) {
					player.setNextForceTalk(new ForceTalk(
							getStartFightText(Utils.getRandom(1))));
				} else if (count == 3) {
					player.getPackets().sendHideIComponent(1172, 2, false);
					player.getPackets().sendHideIComponent(1172, 5, true);
					player.getPackets().sendIComponentText(1172, 6,
							BOSSES[index].name);
					player.getPackets().sendConfig(1241, 0);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 37), 1800);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 28), 800);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 42),
							Cutscene.getY(player, getBaseY() + 37), 1800,
							6, 6);
				} else if (count == 4) {
					if (BOSSES[index].text != null)
						bosses[0].setNextForceTalk(new ForceTalk(
								BOSSES[index].text));
					if (BOSSES[index].voice != -1)
						player.getPackets().sendVoice(BOSSES[index].voice);
				} else if (count == 6) {

					player.getControlerManager().sendInterfaces();
					player.getInterfaceManager().sendInterface(1172);
					player.getPackets().sendHideIComponent(1172, 2, true);
					player.getPackets().sendHideIComponent(1172, 5, true);
					player.getPackets().sendIComponentText(1172, 8,
							"Fight!");
					player.getPackets().sendHideIComponent(1172, 10, true);
					//player.getPackets().sendCameraLook(Cutscene.getX(player, getBaseX() + 32),
					//		Cutscene.getY(player, getBaseY() + 36), 0);
					//player.getPackets().sendCameraPos(Cutscene.getX(player, getBaseX() + 32),
					//		Cutscene.getY(player, getBaseY() + 16), 5000);
					player.getPackets().sendVoice(7882);
				} else if (count == 8) {
					//if (nextBossIndex != -1 && BOSSES[index].item != null)
					//	World.addGroundItem(BOSSES[index].item, new WorldTile(getBaseX() + 26, getBaseY() + 33, 2));
					player.closeInterfaces();
					player.getPackets().sendResetCamera();
					for (NPC boss : bosses) {
						boss.setCantInteract(false);
						boss.setTarget(player);
					}
					player.unlock();
					stop();
				}
				count++;
			}

		}, 0, 1);
	}

	public void removeItem() {
		if (nextBossIndex == -1)
			return;
		if (BOSSES[nextBossIndex].item != null) {
			player.getInventory().deleteItem(BOSSES[nextBossIndex].item.getId(), BOSSES[nextBossIndex].item.getAmount());
			player.getEquipment().deleteItem(BOSSES[nextBossIndex].item.getId(), BOSSES[nextBossIndex].item.getAmount());
			player.getGlobalPlayerUpdater().generateAppearenceData();
		}
	}

	public void loss(final int mode) {
		nextBossIndex = -1;
		player.lock(15);
		player.setNextWorldTile(new WorldTile(getBaseX() + 35, getBaseY() + 31, 2));
		player.setNextFaceWorldTile(new WorldTile(player.getX() + 1, player.getY(), 0));

		WorldTasksManager.schedule(new WorldTask() {
			int count;

			@Override
			public void run() {
				if (count == 0) {
					player.setNextAnimation(new Animation(836));
					player.getPackets().closeInterface(player.getInterfaceManager().hasRezizableScreen() ? 11 : 0);
					player.getInterfaceManager().sendInterface(1172);
					player.getPackets().sendHideIComponent(1172, 2, true);
					player.getPackets().sendHideIComponent(1172, 5, true);
					player.getPackets().sendIComponentText(1172, 8, "DEFEATED!");
					player.getPackets().sendIComponentText(1172, 10, 
							"You leave with a dominion factor of: " + dominionFactor);
					player.getPackets().sendCameraPos(Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 37), 2500);
					player.getPackets().sendCameraLook(Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 28), 800);
					player.getPackets().sendCameraPos(Cutscene.getX(player, getBaseX() + 42),
							Cutscene.getY(player, getBaseY() + 37), 2500, 6, 6);
					player.getPackets().sendVoice(7874);
				} else if (count == 4) {
					player.setForceMultiArea(false);
					player.reset();
					player.setNextAnimation(new Animation(-1));
					player.closeInterfaces();
					player.getPackets().sendResetCamera();
					player.unlock();
					destroyArena(false, mode);
					stop();
				}
				count++;
			}
		}, 0, 1);
	}

	public void win(int mode) {
		removeItem();
		int factor = getBossesTotalLevel() * 10;
		progress++;
		if (progress > maxFloorEndurance)
			maxFloorEndurance = progress;
		killedBossesCount += player.getPerkManager().miniGamer ? 2 : 1;
		dominionFactor += factor;
		totalScore += factor;
		if (dominionFactor > MAX_FACTOR) {
			dominionFactor = MAX_FACTOR;
			player.sendMessage("You've reached the maximum Dominion Factor you can get!");
		}
		DTRank.checkRank(player, mode, BOSSES[getNextBossIndex()].name);
		nextBossIndex = -1;
		player.lock(15);
		player.setNextWorldTile(new WorldTile(getBaseX() + 35, getBaseY() + 31, 2));
		player.setNextFaceWorldTile(new WorldTile(getBaseX() + 36, getBaseY() + 31, 0));

		WorldTasksManager.schedule(new WorldTask() {

			private int count;

			@Override
			public void run() {
				if (count == 0) {
					player.getPackets()
							.closeInterface(
									player.getInterfaceManager()
											.hasRezizableScreen() ? 11 : 0);
					player.getInterfaceManager().sendInterface(1172);
					player.getPackets().sendHideIComponent(1172, 2, true);
					player.getPackets().sendHideIComponent(1172, 5, true);
					player.getPackets().sendIComponentText(1172, 8, "YOU WIN!");
					player.getPackets().sendIComponentText(1172, 10,
							"Your dominion factor is now: " + dominionFactor);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 37), 2500);
					player.getPackets().sendCameraLook(
							Cutscene.getX(player, getBaseX() + 35),
							Cutscene.getY(player, getBaseY() + 28), 800);
					player.getPackets().sendCameraPos(
							Cutscene.getX(player, getBaseX() + 42),
							Cutscene.getY(player, getBaseY() + 37), 2500, 6, 6);
					player.getPackets().sendVoice(7897);
				} else if (count == 4) {
					player.reset();
					player.closeInterfaces();
					player.getPackets().sendResetCamera();
					player.unlock();
					stop();
				}
				count++;
			}
		}, 0, 1);

	}

	public void destroyArena(final boolean logout, int mode) {
		WorldTile tile = new WorldTile(3744, 6425, 0);
		if (logout)
			player.setLocation(tile);
		else {
			player.getControlerManager().removeControlerWithoutCheck();
			player.lock(15);
			player.setNextWorldTile(tile);
			if (mode == ENDURANCE)
				progress = 0;
		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				CoresManager.slowExecutor.execute(new Runnable() {
					@Override
					public void run() {
						try {
							MapBuilder.destroyMap(mapBaseCoords[0], mapBaseCoords[1], 8, 8);
							if (!logout) {
								mapBaseCoords = null;
								player.unlock();
							}
						} catch (Exception e) {
							System.err.println("Failed destroying Dominion Tower map.");
						} catch (Error e) {
							System.err.println("Failed destroying Dominion Tower map.");
						}
					}
				});
			}
		}, 1);
	}

	public NPC[] createBosses() {
		NPC[] bosses = new NPC[BOSSES[getNextBossIndex()].ids.length];
		for (int i = 0; i < BOSSES[getNextBossIndex()].ids.length; i++)
			bosses[i] = World.spawnNPC(BOSSES[getNextBossIndex()].ids[i], new WorldTile(
							getBaseX() + 37 + (i * 2), getBaseY() + 31, 2), -1, true, true);
		return bosses;
	}

	public int getBaseX() {
		return mapBaseCoords[0] << 3;
	}

	public int getBaseY() {
		return mapBaseCoords[1] << 3;
	}

	public void selectBoss() {
		if (nextBossIndex < 0 || nextBossIndex >= BOSSES.length)
			nextBossIndex = Utils.random(BOSSES.length);
	}

	public void openEnduranceMode() {
		selectBoss();
		int factorGain = getBossesTotalLevel() * 10;
		player.getInterfaceManager().sendScreenInterface(96, 1173);
		player.getPackets().sendIComponentText(1173, 25, BOSSES[getNextBossIndex()].name); // current
		player.getPackets().sendIComponentText(1173, 38, String.valueOf(progress + 1)); // current
		player.getPackets().sendIComponentText(1173, 52, "<col=00ff00>If you succeed to slay "
			+ BOSSES[getNextBossIndex()].name + ", " + "I will grant you 1 Dominion tower "
			+ "boss kill. You currently have " + getKilledBossesCount() + " boss kills."
			+ "<br><br><col=00ff00>On death, you won't loose or gain any points, nor you will loose your equipment. "
			+ "<br><br>" + Colors.red + "Don't die in there. " + "<br><br>" + Colors.red + "Good luck, warrior!");
		player.getPackets().sendIComponentText(1173, 29, "" + dominionFactor);
		player.getPackets().sendIComponentText(1173, 31, "" + factorGain);
		player.getPackets().sendIComponentText(1173, 33, "" + Math.round(dominionFactor + factorGain));
	}

	public int getBossesTotalLevel() {
		int level = 0;
		for (int id : BOSSES[getNextBossIndex()].ids)
			level = +NPCDefinitions.getNPCDefinitions(id).combatLevel;
		return level;
	}

	public void talkToFace() {
		talkToFace(false);
	}

	public void talkToFace(boolean fromDialogue) {
		if (!hasRequiriments()) {
			player.getDialogueManager()
					.startDialogue("SimpleMessage",
							"You need at least level 110 combat to play this minigame!");
			return;
		}
		if (!talkedWithFace)
			player.getDialogueManager().startDialogue("StrangeFace");
		else {
			if (!fromDialogue)
				player.getPackets().sendVoice(7893);
			player.getInterfaceManager().sendInterface(1160);
		}
	}

	public void openRewards() {
		if (!talkedWithFace) {
			talkToFace();
			return;
		}
		player.getPackets().sendVoice(7893);
		player.getPackets().sendConfig(2414, -1);
		player.getPackets().sendIComponentText(1156, 190, "Dominion Tower rewards");
		player.getPackets().sendIComponentText(1156, 108,
				"Goliath Gloves <col=B22222>(100 Dominion Tower Kills)");
		player.getPackets().sendIComponentText(1156, 109,
				"One of the best gloves in-game for Melee");
		player.getPackets().sendIComponentText(1156, 113,
				"Spellcaster Gloves <col=B22222>(100 Dominion Tower Kills)");
		player.getPackets().sendIComponentText(1156, 114,
				"One of the best gloves in-game for Mage");
		player.getPackets().sendIComponentText(1156, 137,
				"Swift Gloves <col=B22222>(100 Dominion Tower Kills)");
		player.getPackets().sendIComponentText(1156, 138,
				"One of the best gloves in-game for Ranging");
		player.getPackets().sendIComponentText(1156, 110,
				"Dominion Sword <col=B22222>(150 Dominion Tower Kills)");
		player.getPackets().sendIComponentText(1156, 111,
				"One of the best weapons in-game for Melee");
		player.getPackets().sendIComponentText(1156, 116,
				"Dominion Staff <col=B22222>(125 Dominion Tower Kills)");
		player.getPackets().sendIComponentText(1156, 117,
				"One of the strongest staves in-game for Mage");
		player.getPackets().sendIComponentText(1156, 134,
				"Dominion crossbow <col=B22222>(200 Dominion Tower Kills)");
		player.getPackets().sendIComponentText(1156, 135,
				"One of the best crossbows in-game for Ranging");

		// hide scroll buttons
		player.getPackets().sendHideIComponent(1156, 100, true);
		player.getPackets().sendHideIComponent(1156, 102, true);
		player.getPackets().sendHideIComponent(1156, 103, true);
		player.getPackets().sendHideIComponent(1156, 104, true);
		player.getPackets().sendHideIComponent(1156, 105, true);
		player.getPackets().sendHideIComponent(1156, 106, true);
		
		// remove claim buttons if can't claim the reward
		player.getPackets().sendHideIComponent(1156, 88, getKilledBossesCount() < 100);
		player.getPackets().sendHideIComponent(1156, 115, getKilledBossesCount() < 100);
		player.getPackets().sendHideIComponent(1156, 139, getKilledBossesCount() < 100);
		player.getPackets().sendHideIComponent(1156, 112, getKilledBossesCount() < 150);
		player.getPackets().sendHideIComponent(1156, 136, getKilledBossesCount() < 200);
		player.getPackets().sendHideIComponent(1156, 118, getKilledBossesCount() < 125);
		player.getPackets().sendHideIComponent(1156, 198, true); //removes lock
		player.getPackets().sendHideIComponent(1156, 246, true); //removes lock
		//TODO Find second (from bottom) lock removal component id
		
		// overwrite text
		player.getPackets().sendIComponentText(1156, 90, Colors.green+"Claim");
		player.getPackets().sendIComponentText(1156, 206, Colors.green+"Claim");
		player.getPackets().sendIComponentText(1156, 254, Colors.green+"Claim");
		player.getPackets().sendIComponentText(1156, 200, Colors.green+"Claim");
		player.getPackets().sendIComponentText(1156, 212, Colors.green+"Claim");
		player.getPackets().sendIComponentText(1156, 248, Colors.green+"Claim");
		
		player.getInterfaceManager().sendInterface(1156);
	}

	public void openBankChest() {
		if (!talkedWithFace) {
			growFace();
			return;
		}
		player.getBank().openPlayerBank();
	}

	public void viewScoreBoard() {
		DTRank.showRanks(player);
	}

	public boolean isTalkedWithFace() {
		return talkedWithFace;
	}

	public void setTalkedWithFace(boolean talkedWithFace) {
		this.talkedWithFace = talkedWithFace;
	}

	public int getProgress() {
		return progress;
	}

	public long getTotalScore() {
		return totalScore;
	}

	public int getDominionFactor() {
		return dominionFactor;
	}

	public Boss getNextBoss() {
		return BOSSES[getNextBossIndex()];
	}

	public int getMaxFloorEndurance() {
		return maxFloorEndurance;
	}

	public int getKilledBossesCount() {
		return killedBossesCount;
	}
	public void setKilledBossesCount(int killedBossesCount) {
		this.killedBossesCount = killedBossesCount;
	}
}