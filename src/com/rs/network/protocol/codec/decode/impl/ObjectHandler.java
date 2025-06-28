package com.rs.network.protocol.codec.decode.impl;

import com.rs.Settings;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.ForceMovement;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.NewForceMovement;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activites.CastleWars;
import com.rs.game.activites.Crucible;
import com.rs.game.activites.CrystalChest;
import com.rs.game.activites.RaptorChest;
import com.rs.game.activites.FightPits;
import com.rs.game.activites.PuroPuro;
import com.rs.game.activites.ShootingStar;
import com.rs.game.activites.pest.Lander;
import com.rs.game.activities.instances.GregorovicInstance;
import com.rs.game.activities.instances.HelwyrInstance;
import com.rs.game.activities.instances.TwinFuriesInstance;
import com.rs.game.activities.instances.VindictaInstance;
import com.rs.game.item.Item;
import com.rs.game.npc.others.randoms.RogueNPC;
import com.rs.game.player.CombatDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.QuestManager.Quests;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.BonesOnAltar;
import com.rs.game.player.actions.BonesOnAltar.Bones;
import com.rs.game.player.actions.Cooking;
import com.rs.game.player.actions.Cooking.Cookables;
import com.rs.game.player.actions.CowMilkingAction;
import com.rs.game.player.actions.SinewCooking;
import com.rs.game.player.actions.WaterFilling;
import com.rs.game.player.actions.Woodcutting;
import com.rs.game.player.actions.Woodcutting.TreeDefinitions;
import com.rs.game.player.actions.crafting.JewellerySmithing;
import com.rs.game.player.actions.crafting.RobustGlass;
import com.rs.game.player.actions.divination.DivinationConvert;
import com.rs.game.player.actions.divination.DivinationConvert.ConvertMode;
import com.rs.game.player.actions.divination.impl.DivineFishing;
import com.rs.game.player.actions.divination.impl.DivineHerblore;
import com.rs.game.player.actions.divination.impl.DivineHunting;
import com.rs.game.player.actions.divination.impl.DivineMining;
import com.rs.game.player.actions.divination.impl.DivineSimulacrum;
import com.rs.game.player.actions.divination.impl.DivineWoodcutting;
import com.rs.game.player.actions.divination.impl.DivineWoodcutting.DivineTreeDefinitions;
import com.rs.game.player.actions.firemaking.Bonfire;
import com.rs.game.player.actions.hunter.TrapAction;
import com.rs.game.player.actions.magic.ChargeAirOrb;
import com.rs.game.player.actions.magic.ChargeEarthOrb;
import com.rs.game.player.actions.magic.ChargeFireOrb;
import com.rs.game.player.actions.magic.ChargeWaterOrb;
import com.rs.game.player.actions.mining.EssenceMining;
import com.rs.game.player.actions.mining.Mining;
import com.rs.game.player.actions.mining.Mining.RockDefinitions;
import com.rs.game.player.actions.mining.MiningBase;
import com.rs.game.player.actions.mining.defs.EssenceDefinitions;
import com.rs.game.player.actions.miscellania.MiscellaniaMining;
import com.rs.game.player.actions.miscellania.MiscellaniaWoodcutting;
import com.rs.game.player.actions.runecrafting.SiphonActionNodes;
import com.rs.game.player.actions.smithing.Smithing.ForgingBar;
import com.rs.game.player.actions.smithing.Smithing.ForgingInterface;
import com.rs.game.player.actions.summoning.Summoning;
import com.rs.game.player.actions.thieving.Thieving;
import com.rs.game.player.combat.PlayerCombat;
import com.rs.game.player.content.BrimhDungeon;
import com.rs.game.player.content.FadingScreen;
import com.rs.game.player.content.FairyRing;
import com.rs.game.player.content.Incubator;
import com.rs.game.player.content.LividFarm;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.PartyRoom;
import com.rs.game.player.content.Pickables;
import com.rs.game.player.content.PrifddinasCity;
import com.rs.game.player.content.ResourceDungeons;
import com.rs.game.player.content.RouteEvent;
import com.rs.game.player.content.RuneCrafting;
import com.rs.game.player.content.SawMills;
import com.rs.game.player.content.SpiritTree;
import com.rs.game.player.content.WellOfGoodWill;
import com.rs.game.player.content.WildernessObelisk;
import com.rs.game.player.content.agility.Agility;
import com.rs.game.player.content.agility.AgilityPyramid;
import com.rs.game.player.content.agility.BarbarianOutpostAgility;
import com.rs.game.player.content.agility.GnomeAgility;
import com.rs.game.player.content.agility.WildernessCourseAgility;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.contracts.ContractHandler;
import com.rs.game.player.content.dungeoneering.rooms.puzzles.FishingFerretRoom;
import com.rs.game.player.content.miscellania.ThroneOfMiscellania;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.player.controllers.Barrows;
import com.rs.game.player.controllers.BorkController;
import com.rs.game.player.controllers.Falconry;
import com.rs.game.player.controllers.FightCaves;
import com.rs.game.player.controllers.FightKiln;/*
												import com.rs.game.player.controllers.InstancedPVPControler;*/
import com.rs.game.player.controllers.NomadsRequiem;
import com.rs.game.player.controllers.WarriorsGuild;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.player.controllers.bossInstance.VoragoInstanceController;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.dialogue.impl.CasinoEntranceD;
import com.rs.game.player.dialogue.impl.MiningGuildDwarf;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.stream.InputStream;
import com.rs.utils.Colors;
import com.rs.utils.Logger;
import com.rs.utils.PkRank;
import com.rs.utils.ShopsHandler;
import com.rs.utils.Utils;

/**
 * Handles all Objects within Edelar.
 * 
 * @author Zeus
 */
public final class ObjectHandler {

	/**
	 * Handles the Object option clicked.
	 * 
	 * @param player The player interacting.
	 * @param stream The InputStream packet sent.
	 * @param option The option clicked.
	 */
	private static final Object THHAAR_MEJ_JAL = 13633;

	public static void handleOption(final Player player, InputStream stream, int option) {
		if (!player.clientHasLoadedMapRegion() || player.isDead())
			return;
		if (player.isLocked() || player.getEmotesManager().getNextEmoteEnd() >= Utils.currentTimeMillis())
			return;

		boolean forceRun = stream.readUnsignedByte128() == 1;
		final int id = stream.readIntLE();
		int x = stream.readUnsignedShortLE();
		int y = stream.readUnsignedShortLE128();
		final WorldTile tile = new WorldTile(x, y, player.getPlane());
		final int regionId = tile.getRegionId();
		if (!player.getMapRegionsIds().contains(regionId))
			return;
		WorldObject mapObject = World.getObjectWithId(tile, id);
		if (mapObject == null || mapObject.getId() != id)
			return;
		final WorldObject object = mapObject;
		player.stopAll();
		if (forceRun)
			player.setRun(forceRun);
		switch (option) {
		case 1:
			handleOption1(player, object);
			break;
		case 2:
			handleOption2(player, object);
			break;
		case 3:
			handleOption3(player, object);
			break;
		case 4:
			handleOption4(player, object);
			break;
		case 5:
			handleOption5(player, object);
			break;
		case -1:
			handleOptionExamine(player, object);
			break;
		}
	}

	/**
	 * Handles the Object's First option.
	 * 
	 * @param player The player interacting.
	 * @param object The object interacted with.
	 */
	/**
	 * @param player
	 * @param object
	 */
	private static void handleOption1(final Player player, final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		final int x = object.getX();
		final int y = object.getY();
		final int plane = object.getPlane();
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("Option 1:" + object.getId() + "; " + object.getX() + " "
					+ object.getY() + " " + object.getPlane());
		if (player.isLocked())
			return;
		if (id == 101898) {
			if (player.getCurrentInstance() != null && player.getCurrentInstance() instanceof HelwyrInstance) {
				if (player.getY() <= player.getCurrentInstance().getWorldTile(25, 47).getY()) {
					player.sendMessage("You cannot leave just like that.");
					return;
				} else {
					player.setNextWorldTile(player.getCurrentInstance().getWorldTile(25, 46));
					return;
				}
			}
		}
		if (id == 101910) {
			if (player.getCurrentInstance() != null && player.getCurrentInstance() instanceof VindictaInstance) {
				if (player.getX() <= player.getCurrentInstance().getWorldTile(39, 38).getX()) {
					player.sendMessage("You cannot leave just like that.");
					return;
				} else {
					player.setNextWorldTile(player.getCurrentInstance().getWorldTile(38, 38));
					return;
				}
			}
		}
		if (id == 101907) {
			if (player.getCurrentInstance() != null && player.getCurrentInstance() instanceof TwinFuriesInstance) {
				if (player.getX() < player.getCurrentInstance().getWorldTile(43, 20).getX()) {
					player.sendMessage("You cannot leave just like that.");
					return;
				} else {
					player.setNextWorldTile(player.getCurrentInstance().getWorldTile(40, 21));
					return;
				}
			}
		}
		if (id == 101902) {
			if (player.getCurrentInstance() != null && player.getCurrentInstance() instanceof GregorovicInstance) {
				if (player.getX() > player.getCurrentInstance().getWorldTile(31, 30).getX()) {
					player.sendMessage("You cannot leave just like that.");
					return;
				} else {
					player.setNextWorldTile(player.getCurrentInstance().getWorldTile(33, 33));
					return;
				}
			}
		}
		if (LividFarm.HandleLividFarmObjects(player, object, 1))
			return;
		if (id == 43529 && player.getX() >= 2484 && player.getY() >= 3417 && player.getX() <= 2487
				&& player.getX() <= 3422 && player.getPlane() == 3)
			GnomeAgility.PreSwing(player, object);
		if (SiphonActionNodes.siphion(player, object))
			return;
		if (object.getId() == 84960) {// Exit sphere
			VoragoInstanceController controler = (VoragoInstanceController) player.getControlerManager().getControler();
			if (controler == null)
				return;
			player.faceObject(object);
			player.getDialogueManager().startDialogue(new Dialogue() {
				@Override
				public void start() {
					sendOptionsDialogue("ARE YOU READY TO LEAVE?", "Leave.", "Stay here.");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					if (componentId == OPTION_1) {
						controler.resetAttributesOnLeaveBattle();
						controler.getVoragoInstance().handleExitSphereClick(player);
					}
					end();
				}

				@Override
				public void finish() {
				}
			});
			return;
		}
		if (object.getId() == 59463 && object.getX() == 2958 && object.getY() == 3289) {
			ClansManager manager = player.getClanManager();
			if (manager == null) {
				player.getDialogueManager().startDialogue("SimpleMessage",
						"You must be in a clan to access the Clan Citadel.");
			}
			if (!(manager == null) && player.getClanManager().getClan().getClanCitadel().isCitadelGenerated() == true) {
				player.lock(2);
				player.getClanManager().getClan().getClanCitadel().enterClanCitadel(player);

			}
			if (!(manager == null)
					&& player.getClanManager().getClan().getClanCitadel().isCitadelGenerated() == false) {
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
									player.getClanManager().getClan().getClanCitadel().enterClanCitadel(player);
								}
							});
						} catch (Throwable e) {
							Logger.handle(e);
						}
					}

				});
			}
		}
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControlerManager().processObjectClick1(object))
					return;
				if (!player.getNewQuestManager().processObjectClick1(object))
					return;
				if (player.getElderTreeManager().isElderTree(object))
					return;
				if (player.getDungManager().enterResourceDungeon(object))
					return;
				if (SawMills.isMilling(player, object, null))
					return;
				if (CastleWars.handleObjects(player, id))
					return;
				if (ResourceDungeons.handleObjects(player, id))
					return;
				if (PrifddinasCity.handleObjectOption1(player, object))
					return;
				if (id == 87306) {
					player.getDialogueManager().startDialogue("ConvertMemoriesD");
					return;
				}
				if (id == 66205) {
					if (object.getX() == 3808 && object.getY() == 4694)
						player.useStairs(1603, new WorldTile(3810, 4693, 0), 1, 1, "");
					else
						player.getDialogueManager().startDialogue("PreHestoricalDoor");
				}
				if (id >= 10851 && id <= 10888) {
					AgilityPyramid.handlePyramidObjects(player, object);
					return;
				}
				if (id == 36771) {
					player.useStairs(-1, new WorldTile(3207, 3222, 3), 1, 2);
					return;
				}

				if (id == 36772) {
					player.useStairs(-1, new WorldTile(3207, 3224, 2), 1, 2);
					return;
				}
				if (id == 12683) {
					if (!player.canSpawn()) {
						player.sendMessage("You cannot open your bank account at the moment.");
						return;
					}
					if (CasinoEntranceD.CasinoArea(player, player)) {
						player.getPackets().sendGameMessage(Colors.red + "You cannot use bank command in the casino");
						return;
					}
					if (player.isLocked()) {
						player.sendMessage("You can't bank at the moment, please wait.");
						return;
					}
					if (player.getAttackedByDelay() + 3000 > Utils.currentTimeMillis()) {
						player.sendMessage("You can't bank 3 seconds after combat, please wait.");
						return;
					}
					if (player.getClanManager() != null && (player.canTeleport()))
						player.getClanManager().getClan().getClanBank().openBank(player);
					return;
				}
				if (object.getId() == 6448) {
					player.getDialogueManager().startDialogue("TaxChest");
					return;
				}

				if (id == 48496) {
					player.getDungManager().enterDungeon(true);
					return;
				}
				if (id == 50552) {
					player.setNextForceMovement(new ForceMovement(object, 1, ForceMovement.NORTH));
					player.useStairs(13760, new WorldTile(3454, 3725, 0), 2, 3);
					return;
				}

				/* Christmas event */
				if (id == 65958) {
					player.faceObject(object);
					if (object.getX() == 2339 && object.getY() == 3171)
						player.getControlerManager().startControler("XmasController");
					if (object.getX() == 2649 && object.getY() == 5670)
						player.getXmas().leave();
				}

				if (id == 17819) {
					player.getDialogueManager().startDialogue("VoragoInstanceD");
					return;
				}
				/*
				 * if (id == 59356) { player.getNextWorldTile(). return; }
				 */
				if (id == 84829) {
					player.getDialogueManager().startDialogue("VoragoSignpostD");
					return;
				}
				if (id == 84909) {// vorago graveyard exit
					player.useStairs(828, new WorldTile(2972, 3431, 0), 1, 2);
					return;
				}

				if (id == 70795) {
					player.setNextWorldTile(new WorldTile(1199, 6498, 0));
				}

				if (id == 91173) { // lleyta upstairs > down

					if (object.getX() == 2332 && object.getY() == 3157) { // south
						// cooking
						// room
						if (player.addWalkSteps(2334, 3158))
							player.faceObject(object);
						if (player.withinDistance(new WorldTile(2332, 3158, 1)))
							player.setNextWorldTile(new WorldTile(2333, 3159, 0));
					}

					if (object.getX() == 2328 && object.getY() == 3176) { // top
						// floor
						// tower
						// north
						if (player.addWalkSteps(2328, 3176))
							player.faceObject(object);
						if (player.withinDistance(new WorldTile(2328, 3176, 2)))
							player.setNextWorldTile(new WorldTile(2327, 3176, 1));
					}
					if (object.getX() == 2325 && object.getY() == 3168) { // top
						// floor
						// tower
						// south
						if (player.addWalkSteps(2326, 3168))
							player.faceObject(object);
						if (player.withinDistance(new WorldTile(2326, 3168, 2)))
							player.setNextWorldTile(new WorldTile(2326, 3168, 1));
					}
				}

				if (id == 91145) { // left-curved staircase down
					if (player.addWalkSteps(2343, 3175))
						player.faceObject(object);
					if (player.withinDistance(new WorldTile(object.getX(), object.getY(), object.getPlane())))
						player.setNextWorldTile(new WorldTile(2337, 3174, 0));
				}
				if (id == 91146) { // right-curved staircase down
					if (player.addWalkSteps(2343, 3168))
						player.faceObject(object);
					if (player.withinDistance(new WorldTile(object.getX(), object.getY(), object.getPlane())))
						player.setNextWorldTile(new WorldTile(2337, 3169, 0));
				}
				if (id == 91147) { // left-curved staircase up
					if (player.addWalkSteps(2343, 3175))
						player.faceObject(object);
					if (player.withinDistance(new WorldTile(object.getX(), object.getY(), object.getPlane())))
						player.setNextWorldTile(new WorldTile(2343, 3175, 1));
				}
				if (id == 91148) { // right-curved staircase up
					if (player.withinDistance(new WorldTile(2337, 3171, object.getPlane())))
						player.setNextWorldTile(new WorldTile(2343, 3168, 1));
				}
				if (id == 91171) { // upstairs walk in lleyta
					if (object.getX() == 2332 && object.getY() == 3157) {
						if (player.withinDistance(new WorldTile(object.getX(), object.getY(), object.getPlane())))
							player.setNextWorldTile(new WorldTile(2334, 3158, 1));
					}
					if (object.getX() == 2332 && object.getY() == 3185) {
						if (player.withinDistance(new WorldTile(object.getX(), object.getY(), object.getPlane())))
							player.setNextWorldTile(new WorldTile(2333, 3184, 1));
					}
					if (object.getX() == 2328 && object.getY() == 3176) {
						if (player.withinDistance(new WorldTile(object.getX(), object.getY(), object.getPlane())))
							player.setNextWorldTile(new WorldTile(2327, 3176, 1));
					}
					if (object.getX() == 2325 && object.getY() == 3168) {
						if (player.withinDistance(new WorldTile(object.getX(), object.getY(), object.getPlane())))
							player.setNextWorldTile(new WorldTile(2327, 3168, 1));
					}
				}

				if (id == 2296 && y == 3477) {
					if (!Agility.hasLevel(player, 20))
						return;
					player.lock(5);
					player.addWalkSteps(object.getX() + (object.getRotation() == 3 ? 4 : -4), object.getY(), -1, false);
					player.sendMessage("You walk carefully across the slippery log...", true);
					WorldTasksManager.schedule(new WorldTask() {
						boolean secondloop;

						@Override
						public void run() {
							if (!secondloop) {
								secondloop = true;
								player.getGlobalPlayerUpdater().setRenderEmote(155);
							} else {
								player.getGlobalPlayerUpdater().setRenderEmote(-1);
								player.getSkills().addXp(Skills.AGILITY, 7.5);
								player.sendMessage("... and make it safely to the other side.", true);
								stop();
							}
						}
					}, 0, 3);
				}
				if (id == 71902) {
					player.useStairs(-1, new WorldTile(2968, 3219, 1), 1, 0);
					return;
				}
				if (id == 71903) {
					player.useStairs(-1, new WorldTile(2964, 3219, 0), 1, 0);
					return;
				}
				if (id == 47364) {
					player.useStairs(-1, new WorldTile(3108, 3366, 1), 1, 0);
					return;
				}
				if (id == 47657) {
					player.useStairs(-1, new WorldTile(3108, 3361, 0), 1, 0);
					return;
				}
				/*
				 * if (id == 59356) { player.setNextWorldTile(new WorldTile(126, 735, 0));
				 * //player.useStairs(-1, new WorldTile(126, 735, 0), 1, 0); return; }
				 */

				// End of halloween items

				if (id == 97270) {
					ForgingBar bar = ForgingBar.getBar(player);
					if (bar == ForgingBar.DRACONIC_VISAGE) {
						player.getDialogueManager().startDialogue("DFSSmithingD");
						return;
					}

					if (bar != null)
						ForgingInterface.sendSmithingInterface(player, bar, object);
					else
						player.sendMessage("You have no bars for your smithing level.");
				}
				// Invention guild - go inside
				if (id == 100850) {
					if (player.getSkills().getLevelForXp(Skills.DIVINATION) < 80
							|| player.getSkills().getLevelForXp(Skills.SMITHING) < 80
							|| player.getSkills().getLevelForXp(Skills.CRAFTING) < 80) {
						player.getDialogueManager().startDialogue("SimpleMessage",
								"The Invention guild is only available to the Elite.");
						player.sendMessage("You need level 80 in Divination, Smithing and Crafting to go in here.");
						return;
					}
					player.useStairs(-1, new WorldTile(6169, 1038, 0), 0, 0);
					return;
				}
				// Invention guild - go outside
				if (id == 100937) {
					player.useStairs(-1, new WorldTile(2997, 3439, 0), 0, 0);
					return;
				}
				if (id == 2606 && x == 2836 && y == 9600) {
					if (World.isSpawnedObject(object))
						return;
					player.lock(1);
					WorldObject opened = new WorldObject(object.getId(), object.getType(), object.getRotation() - 1,
							object.getX(), object.getY(), object.getPlane());
					World.spawnObjectTemporary(opened, 1200);
					player.addWalkSteps(2836, player.getY() == y ? y - 1 : y, 1, false);
					return;
				}
				if (id == 25213 && x == 2833 && y == 9657) {
					player.useStairs(828, new WorldTile(2834, 3258, 0), 1, 2);
					return;
				}
				if (id == 25154) {
					player.useStairs(827, new WorldTile(2834, 9657, 0), 1, 2);
					return;
				}
				if (object.getDefinitions().getName().toLowerCase().contains("spirit tree")
						|| object.getId() == 26723) {
					player.getDialogueManager().startDialogue("SpiritTreeD",
							(object.getId() == 68973 && object.getId() == 68974) ? 3637 : 3636);
					return;
				} else if (id == 77745 || id == 28779) {
					if (x == 3142 && y == 5545) {
						BorkController.enterBork(player);
						return;
					}
					player.addWalkSteps(object.getX(), object.getY(), -1, false);
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							if (getRepeatedTele(player, 3285, 5474, 0, 3286, 5470, 0))
								return;
							else if (getRepeatedTele(player, 3302, 5469, 0, 3290, 5463, 0))
								return;
							else if (getRepeatedTele(player, 3280, 5460, 0, 3273, 5460, 0))
								return;
							else if (getRepeatedTele(player, 3299, 5450, 0, 3296, 5455, 0))
								return;
							else if (getRepeatedTele(player, 3283, 5448, 0, 3287, 5448, 0))
								return;
							else if (getRepeatedTele(player, 3260, 5491, 0, 3266, 5446, 0))
								return;
							else if (getRepeatedTele(player, 3239, 5498, 0, 3244, 5495, 0))
								return;
							else if (getRepeatedTele(player, 3238, 5507, 0, 3232, 5501, 0))
								return;
							else if (getRepeatedTele(player, 3222, 5488, 0, 3218, 5497, 0))
								return;
							else if (getRepeatedTele(player, 3222, 5474, 0, 3224, 5479, 0))
								return;
							else if (getRepeatedTele(player, 3215, 5475, 0, 3218, 5478, 0))
								return;
							else if (getRepeatedTele(player, 3210, 5477, 0, 3208, 5471, 0))
								return;
							else if (getRepeatedTele(player, 3212, 5452, 0, 3214, 5456, 0))
								return;
							else if (getRepeatedTele(player, 3235, 5457, 0, 3229, 5454, 0))
								return;
							else if (getRepeatedTele(player, 3204, 5445, 0, 3197, 5448, 0))
								return;
							else if (getRepeatedTele(player, 3191, 5495, 0, 3194, 5490, 0))
								return;
							else if (getRepeatedTele(player, 3185, 5478, 0, 3191, 5482, 0))
								return;
							else if (getRepeatedTele(player, 3186, 5472, 0, 3192, 5472, 0))
								return;
							else if (getRepeatedTele(player, 3189, 5444, 0, 3187, 5460, 0))
								return;
							else if (getRepeatedTele(player, 3178, 5460, 0, 3168, 5456, 0))
								return;
							else if (getRepeatedTele(player, 3171, 5478, 0, 3167, 5478, 0))
								return;
							else if (getRepeatedTele(player, 3171, 5473, 0, 3167, 5471, 0))
								return;
							else if (getRepeatedTele(player, 3142, 5489, 0, 3141, 5480, 0))
								return;
							else if (getRepeatedTele(player, 3142, 5462, 0, 3154, 5462, 0))
								return;
							else if (getRepeatedTele(player, 3155, 5449, 0, 3143, 5443, 0))
								return;
							else if (getRepeatedTele(player, 3303, 5477, 0, 3299, 5484, 0))
								return;
							else if (getRepeatedTele(player, 3318, 5481, 0, 3322, 5480, 0))
								return;
							else if (getRepeatedTele(player, 3307, 5496, 0, 3317, 5496, 0))
								return;
							else if (getRepeatedTele(player, 3265, 5491, 0, 3260, 5491, 0))
								return;
							else if (getRepeatedTele(player, 3297, 5510, 0, 3300, 5514, 0))
								return;
							else if (getRepeatedTele(player, 3325, 5518, 0, 3323, 5531, 0))
								return;
							else if (getRepeatedTele(player, 3321, 5554, 0, 3315, 5552, 0))
								return;
							else if (getRepeatedTele(player, 3291, 5555, 0, 3285, 5556, 0))
								return;
							else if (getRepeatedTele(player, 3285, 5508, 0, 3280, 5501, 0))
								return;
							else if (getRepeatedTele(player, 3285, 5527, 0, 3282, 5531, 0))
								return;
							else if (getRepeatedTele(player, 3289, 5532, 0, 3288, 5536, 0))
								return;
							else if (getRepeatedTele(player, 3266, 5552, 0, 3262, 5552, 0))
								return;
							else if (getRepeatedTele(player, 3268, 5534, 0, 3261, 5536, 0))
								return;
							else if (getRepeatedTele(player, 3248, 5547, 0, 3253, 5561, 0))
								return;
							else if (getRepeatedTele(player, 3256, 5561, 0, 3252, 5543, 0))
								return;
							else if (getRepeatedTele(player, 3244, 5526, 0, 3241, 5529, 0))
								return;
							else if (getRepeatedTele(player, 3230, 5547, 0, 3226, 5553, 0))
								return;
							else if (getRepeatedTele(player, 3206, 5553, 0, 3204, 5546, 0))
								return;
							else if (getRepeatedTele(player, 3211, 5533, 0, 3214, 5533, 0))
								return;
							else if (getRepeatedTele(player, 3208, 5527, 0, 3211, 5523, 0))
								return;
							else if (getRepeatedTele(player, 3201, 5531, 0, 3197, 5529, 0))
								return;
							else if (getRepeatedTele(player, 3202, 5516, 0, 3196, 5512, 0))
								return;
							if (getRepeatedTele(player, 3197, 5529, 0, 3201, 5531, 0))
								return;
							else if (getRepeatedTele(player, 3165, 5515, 0, 3173, 5530, 0))
								return;
							else if (getRepeatedTele(player, 3156, 5523, 0, 3152, 5520, 0))
								return;
							else if (getRepeatedTele(player, 3148, 5533, 0, 3153, 5537, 0))
								return;
							else if (getRepeatedTele(player, 3143, 5535, 0, 3147, 5541, 0))
								return;
							else if (getRepeatedTele(player, 3158, 5561, 0, 3162, 5557, 0))
								return;
							else if (getRepeatedTele(player, 3162, 5545, 0, 3166, 5553, 0))
								return;
							else if (getRepeatedTele(player, 3168, 5541, 0, 3171, 5542, 0))
								return;
							else if (getRepeatedTele(player, 3190, 5549, 0, 3190, 5554, 0))
								return;
							else if (getRepeatedTele(player, 3180, 5557, 0, 3174, 5558, 0))
								return;
							else if (getRepeatedTele(player, 3190, 5519, 0, 3190, 5515, 0))
								return;
							else if (getRepeatedTele(player, 3185, 5518, 0, 3181, 5517, 0))
								return;
							else if (getRepeatedTele(player, 3196, 5512, 0, 3202, 5516, 0))
								return;
							else if (getRepeatedTele(player, 3159, 5501, 0, 3169, 5510, 0))
								return;
							else if (getRepeatedTele(player, 3182, 5530, 0, 3187, 5531, 0))
								return;
						}
					});
				}
				if (id == 56805)
					// player.setNextAnimation(new Animation(839));
					player.setNextWorldTile(new WorldTile(2950, 2915, 0));
				if (id == 26130 || id == 26131 || id == 68977 || id == 2306 || id == 26081 || id == 26082 || id == 1551)
					handleGate(player, object, 60000);
				if (id == 2514 || id == 1600 || id == 1601)
					handleDoor(player, object);
				if (id == 35391 || id == 2832) {
					if (!Agility.hasLevel(player, id == 2832 ? 20 : 41))
						return;
					player.addWalkSteps(x, y);
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							boolean isTravelingWest = id == 2832 ? player.getX() >= 2508
									: (x == 2834 && y == 3626) ? player.getX() >= 2834 : player.getX() >= 2900;
							player.useStairs(3303,
									new WorldTile((isTravelingWest ? -2 : 2) + player.getX(), player.getY(), 0), 4, 5,
									null, true);
						}
					});
					return;
				}
				if (id == 2830 || id == 2831) {
					player.useStairs(-1, new WorldTile(player.getX(), id == 2831 ? 3026 : 3029, 0), 1, 2);
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.getDialogueManager().startDialogue("SimplePlayerMessage", "Phew! I barely made it.");
							stop();
						}
					}, 1);
				}
				if (id == 76499) { // Al'kharid palace entrance
					handleDoor(player, object);
					return;
				}
				if (id == 4627) // Exit burthorpe games room
					player.useStairs(-1, new WorldTile(2893, 3567, 0), 1, 2);
				if (id == 32270) { // Yanille staircase - up
					player.useStairs(-1, new WorldTile(2606, 3079, 0), 0, 1);
					return;
				}
				if (id == 32271) { // Yanille staircase - down
					player.useStairs(827, new WorldTile(2602, 9478, 0), 2, 3);
					return;
				}
				if (id == 2559) { // Yanille basement door
					handleDoor(player, object);
					return;
				}
				if (id == 10229) { // Dag King stairs up
					player.useStairs(828, new WorldTile(1912, 4367, 0), 1, 2);
					return;
				}
				if (id == 10230) { // Dag King stairs down
					player.useStairs(827, new WorldTile(2904, 4448, 0), 1, 2);
					return;
				}
				if (id == 24367) { // Varrock palace staircase up
					player.useStairs(-1, new WorldTile(3213, 3476, 1), 0, 1);
					return;
				}
				if (id == 24359) { // Varrock palace staircase down
					player.useStairs(-1, new WorldTile(3213, 3472, 0), 0, 1);
					return;
				}
				if (id == 66973) // Enter burthorpe games room
					player.useStairs(-1, new WorldTile(2206, 4934, 1), 1, 2);
				if (id == 4620)
					player.useStairs(-1, new WorldTile(2207, 4938, 0), 1, 2);
				if (id == 4622 && x == 2212)
					player.useStairs(-1, new WorldTile(2212, 4944, 1), 1, 2);
				if (id == 42794)
					player.useStairs(-1, new WorldTile(object.getX(), object.getY() + 7, 0), 0, 1);
				if (id == 42795)
					player.useStairs(-1, new WorldTile(object.getX(), object.getY() - 6, 0), 0, 1);
				if (id == 48188)
					player.useStairs(-1, new WorldTile(3435, 5646, 0), 0, 1);
				if (id == 48189)
					player.useStairs(-1, new WorldTile(3509, 5515, 0), 0, 1);
				if (id == 492 && x == 2856 && y == 3168) // TO karamja
					// underground
					player.useStairs(827, new WorldTile(2856, 9570, 0), 1, 2);
				if (id == 1764 && x == 2856 && y == 9569) // TO karamja
					// upperground
					player.useStairs(828, new WorldTile(2856, 3170, 0), 1, 2);
				if (id == 68134) // thzaar entrance
					player.useStairs(-1, new WorldTile(4667, 5059, 0), 0, 1);
				if (id == 68135)
					player.useStairs(-1, new WorldTile(2845, 3170, 0), 0, 1);
				if (id == 84702) { // Monastery of Ascension entrance
					if (player.getSkills().getLevel(Skills.SLAYER) < 81) {
						player.getDialogueManager().startDialogue("SimpleMessage",
								"You will need at least a Slayer level of 81 to enter the Ascension's Dungeon.");
						return;
					}
					player.useStairs(-1, new WorldTile(1095, 579, 1), 0, 1);
				}
				if (id == 84724) // Monastery of Ascension exit
					player.useStairs(-1, new WorldTile(2500, 2887, 0), 0, 1);
				// Legio boss exit's
				if (id == 84732)
					player.useStairs(-1, new WorldTile(1025, 632, 1), 0, 1);
				if (id == 84733)
					player.useStairs(-1, new WorldTile(1106, 671, 1), 0, 1);
				if (id == 84734)
					player.useStairs(-1, new WorldTile(1099, 665, 1), 0, 1);
				if (id == 84735)
					player.useStairs(-1, new WorldTile(1176, 634, 1), 0, 1);
				if (id == 84736)
					player.useStairs(-1, new WorldTile(1191, 634, 1), 0, 1);
				if (id == 84737)
					player.useStairs(-1, new WorldTile(1183, 622, 1), 0, 1);
				if (id >= 84726 && id <= 84731) { // Legio boss entrances
					player.getDialogueManager().startDialogue("LegioLaboratoryD", object);
					return;
				}
				if (id == 3379) // rantz cave entrance
					player.useStairs(-1, new WorldTile(2646, 9378, 0), 0, 1);
				if (id == 32069) // rantz cave exit
					player.useStairs(-1, new WorldTile(2631, 2997, 0), 0, 1);
				if (id == 2477) {
					if (object.getX() == 2597 && object.getY() == 3155) {
						player.setNextWorldTile(new WorldTile(2468, 4889, 1));
					} else if (object.getX() == 2468 && object.getY() == 4888) {
						player.setNextWorldTile(new WorldTile(2597, 3156, 0));
					} else {
						player.sendMessage("This portal does not lead anywhere.");
					}
				}
				if (id == 69499) { // east of yanille - hazelmere (clue scrolls)
					// - up
					player.useStairs(828, new WorldTile(2677, 3086, 1), 1, 2);
					return;
				}
				if (id == 69502) { // east of yanille - hazelmere (clue scrolls)
					// - down
					player.useStairs(827, new WorldTile(2677, 3086, 0), 1, 2);
					return;
				}
				if (id == 26118) { // seer's village spinning wheel house
					player.useStairs(828, new WorldTile(2714, 3472, 3), 1, 2);
					return;
				}
				if (id == 26119) { // seer's village spinning wheel house
					player.useStairs(828, new WorldTile(2714, 3472, 1), 1, 2);
					return;
				}
				if (id == 31299) { // market signpost
					CrystalChest.openRewardsInterface(player);
					return;
				}
				if (id == 76651) { // raptor chest sighpost
					RaptorChest.openRewardsInterface(player);
					return;
				}
				if (id == 2186) { // tree gnome village - loose railing
					if (player.getY() <= 3160)
						player.useStairs(-1, new WorldTile(2515, 3161, 0), 0, 0);
					else
						player.useStairs(-1, new WorldTile(2515, 3160, 0), 0, 0);
					return;
				}
				if (id == 15516 || id == 15514 || id == 45206 || id == 45208 || id == 45210 || id == 45212) {
					handleDoor(player, object, 60000);
					return;
				}
				// Gnome stronghold big door
				if (id == 68983) {
					player.setNextWorldTile(new WorldTile(2462, (player.getY() <= 3383 ? 3384 : 3383), 0));
					return;
				}
				if (id == 14922) {
					player.setNextWorldTile(new WorldTile(2344, (player.getY() <= 3652 ? 3655 : 3650), 0));
					return;
				}
				if (id == 14929 || id == 14931) {
					handleDoor(player, object, 60000);
					return;
				}
				// White Wolf Mountain cut
				if (id == 56) {
					player.useStairs(-1, new WorldTile(2879, 3465, 0), 0, 1);
					return;
				} else if (id == 66990)
					player.useStairs(-1, new WorldTile(2875, 9880, 0), 0, 1);
				else if (id == 2811 || id == 2812) {
					player.useStairs(id == 2812 ? 827 : -1,
							id == 2812 ? new WorldTile(2501, 2989, 0) : new WorldTile(2574, 3029, 0), 1, 2);
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.getDialogueManager().startDialogue("SimplePlayerMessage",
									"Wow! That tunnel went a long way.");
						}
					});
				} else if (id == 2890 || id == 2893) {
					if (player.getEquipment().getWeaponId() != 975 && !player.getInventory().containsOneItem(975, 1)) {
						player.sendMessage("You need a machete in order to cutt through the terrain.");
						return;
					}
					player.setNextAnimation(new Animation(910));
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							if (Utils.random(3) == 0) {
								player.sendMessage("You fail to slash through the terrain.");
								return;
							}
							WorldObject o = new WorldObject(object);
							o.setId(id + 1);
							World.spawnObjectTemporary(o, 5000);
						}
					});
				} else if (id == 2231)
					player.useStairs(-1, new WorldTile(x == 2792 ? 2795 : 2791, 2979, 0), 1, 2,
							x == 2792 ? "You climb down the slope." : "You climb up the slope.");
				else if (id == 23157)
					player.useStairs(-1, new WorldTile(2729, 3734, 0), 1, 2);
				else if (id == 492 && x == 2856 && y == 3168) // karamja
					// underground and
					// crandor
					player.useStairs(827, new WorldTile(2856, 9570, 0), 1, 2);
				else if (id == 1764 && x == 2856 && y == 9569)
					player.useStairs(828, new WorldTile(2855, 3169, 0), 1, 2);
				else if (id == 1757 && x == 2594 && y == 9485)
					player.useStairs(828, new WorldTile(2594, 3086, 0), 1, 2);
				else if (id == 2158)
					player.useStairs(-1, new WorldTile(3104, 3163, 2), 0, 1);
				else if (id == 2157)
					player.useStairs(-1, new WorldTile(2908, 3332, 2), 0, 1);
				else if (id == 2156)
					player.useStairs(-1, new WorldTile(2702, 3405, 3), 0, 1);
				else if (id == 1754 && x == 2594 && y == 3085)
					player.useStairs(827, new WorldTile(2594, 9486, 0), 1, 2);
				else if (id == 1722 && x == 2590 && y == 3089)
					player.useStairs(-1, new WorldTile(2590, 3092, 1), 0, 1);
				else if (id == 1723 && x == 2590 && y == 3090)
					player.useStairs(-1, new WorldTile(2590, 3088, 0), 0, 1);
				else if (id == 1722 && x == 2590 && y == 3084)
					player.useStairs(-1, new WorldTile(2590, 3087, 2), 0, 1);
				else if (id == 1723 && x == 2590 && y == 3085)
					player.useStairs(-1, new WorldTile(2591, 3083, 1), 0, 1);
				if (id == 54) {
					player.useStairs(-1, new WorldTile(2820, 3486, 0), 0, 1);
					return;
				}
				if (id == 55) {
					player.useStairs(-1, new WorldTile(2821, 9882, 0), 0, 1);
					return;
				}
				if (id == 1596 || id == 1597) {
					handleGate(player, object, 60000);
					return;
				} else if (id == 73681) {
					WorldTile dest = new WorldTile(player.getX() == 2595 ? 2598 : 2595, 3608, 0);
					player.setNextForceMovement(new NewForceMovement(player, 1, dest, 2,
							Utils.getAngle(dest.getX() - player.getX(), dest.getY() - player.getY())));
					player.useStairs(-1, dest, 1, 2);
					player.setNextAnimation(new Animation(769));
				}
				// sabbot lair
				else if (id == 34395)
					player.useStairs(-1, new WorldTile(2893, 10074, 0), 0, 1);
				else if (id >= 2889 && id <= 2892) {
					boolean isNorth = player.getY() >= 2940;
					player.useStairs(-1, player.transform(0, isNorth ? -8 : 8, 0), 2, 3,
							"You slash your way through the marsh and get to the other side.");
				} else if (id == 32738)
					player.useStairs(-1, new WorldTile(2858, 3577, 0), 0, 1);
				else if (id == 4918 && x == 3445 && y == 3236)
					player.useStairs(4853, new WorldTile(player.getX() == 3446 ? 3444 : 3446, object.getY(), 0), 2, 3);
				else if (id == 12776)
					player.useStairs(4853, new WorldTile(player.getX() == 3474 ? 3473 : 3474, object.getY(), 0), 2, 3);
				else if (id == 17757 || id == 17760)
					player.useStairs(4853,
							new WorldTile(object.getX(), object.getY() == player.getY() ? 3243 : 3244, 0), 2, 3);
				else if (id == 20979)
					player.useStairs(-1, new WorldTile(3149, 4666, 0), 0, 1);
				else if (id == 4913 && x == 3440 && y == 3232)
					player.useStairs(-1, new WorldTile(3436, 9637, 0), 0, 1);
				else if (id == 4920 && x == 3437 && y == 9637)
					player.useStairs(-1, new WorldTile(3441, 3232, 0), 0, 1);
				else if (id == 3522 && y == 3329)
					player.useStairs(-1, new WorldTile(x, 3332, 0), 0, 1);
				else if (id == 3522 && y == 3331)
					player.useStairs(-1, new WorldTile(x, 3329, 0), 0, 1);
				else if ((id == 9738 || id == 9330)) {
					if (object.getX() < player.getX() && object.getY() == player.getY()) {
						player.setNextWorldTile(new WorldTile((player.getX() - 2), (player.getY()), 0));

					} else if ((id == 18411)) {
						if (object.getX() < player.getX() && object.getY() == player.getY()) {
							player.setNextWorldTile(new WorldTile((player.getX() - 0), (player.getY()), +1));

						} else if (object.getX() > player.getX() && object.getY() == player.getY()) {
							player.setNextWorldTile(new WorldTile((player.getX() - 0), (player.getY()), -1));
						} else {
							player.sendMessage("You can't reach that.");
							return;
						}
					}
				} else if (id == 4914 && x == 3430 && y == 3233)
					player.useStairs(-1, new WorldTile(3405, 9631, 0), 0, 1);
				else if (id == 5083 && x == 2743 && y == 3153)
					player.useStairs(-1, new WorldTile(2711, 9564, 0), 0, 1);
				else if (id == 77421 && x == 2711 && y == 9563)
					player.useStairs(-1, new WorldTile(2745, 3152, 0), 0, 1);
				else if (id == 4921 && x == 3404 && y == 9631)
					player.useStairs(-1, new WorldTile(3429, 3233, 0), 0, 1);
				else if (id == 20524 && x == 3408 && y == 9623)
					player.useStairs(-1, new WorldTile(3428, 3225, 0), 0, 1);
				else if (id == 4915 && x == 3429 && y == 3225)
					player.useStairs(-1, new WorldTile(3409, 9623, 0), 0, 1);
				else if (id == 28515)
					player.useStairs(4853, new WorldTile(3420, 2803, 1), 2, 3);
				else if (id == 89742) { // rune dragons - enter
					player.lock();
					player.setNextAnimation(new Animation(23099));
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							FadingScreen.fade(player, 0, new Runnable() {

								@Override
								public void run() {
									player.unlock();
									player.setNextAnimation(new Animation(-1));
									player.setNextWorldTile(new WorldTile(4767, 6078, 1));
								}
							});
						}
					}, 0);
				} else if (id == 97217) { // rune dragons - exit
					player.lock();
					player.setNextAnimation(new Animation(23099));
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							FadingScreen.fade(player, 0, new Runnable() {

								@Override
								public void run() {
									player.unlock();
									player.setNextAnimation(new Animation(-1));
									player.setNextWorldTile(new WorldTile(2367, 3354, 0));
								}
							});
						}
					}, 0);
				} else if (id == 97054) { // adamant dragons - enter
					player.lock();
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							FadingScreen.fade(player, 0, new Runnable() {

								@Override
								public void run() {
									player.unlock();
									player.setNextWorldTile(new WorldTile(4512, 6045, 0));
								}
							});
						}
					}, 0);
				} else if (id == 97055) { // adamant dragons - exit
					player.lock();
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							FadingScreen.fade(player, 0, new Runnable() {

								@Override
								public void run() {
									player.unlock();
									player.setNextWorldTile(new WorldTile(2688, 9482, 0));
								}
							});
						}
					}, 0);
				} else if (id == 97056) {
					if (player.getSkills().getLevelForXp(Skills.SLAYER) < 70) {
						player.getDialogueManager().startDialogue("SimpleMessage",
								"You need a Slayer level of at least 70 to pass this magical barrier.");
						return;
					}
					player.setNextWorldTile(new WorldTile((player.getX() <= 4530 ? 4532 : 4530), 6029, 0));
				} else if (id == 97057) {
					if (player.getSkills().getLevelForXp(Skills.SLAYER) < 80) {
						player.getDialogueManager().startDialogue("SimpleMessage",
								"You need a Slayer level of at least 80 to pass this magical barrier.");
						return;
					}
					player.setNextWorldTile(new WorldTile(4531, (player.getY() <= 6062 ? 6064 : 6062), 0));
				} else if (id == 97183) {
					if (player.getSkills().getLevelForXp(Skills.SLAYER) < 80) {
						player.getDialogueManager().startDialogue("SimpleMessage",
								"You need a Slayer level of at least 80 to pass this magical barrier.");
						return;
					}
					player.setNextWorldTile(new WorldTile(4512, (player.getY() <= 6051 ? 6053 : 6051), 0));
				} else if (id == 97184) {
					if (player.getSkills().getLevelForXp(Skills.SLAYER) < 90) {
						player.getDialogueManager().startDialogue("SimpleMessage",
								"You need a Slayer level of at least 90 to pass this magical barrier.");
						return;
					}
					if (object.getX() == 4501)
						player.setNextWorldTile(new WorldTile((player.getX() >= 4502 ? 4500 : 4502), 6023, 0));
					else if (object.getX() == 4491)
						player.setNextWorldTile(new WorldTile(4492, (player.getY() <= 6054 ? 6056 : 6054), 0));
					else
						player.sendMessage("Nothing interesting happens.");
				}
				// Baxtorian falls
				else if (id == 2020)
					player.useStairs(4855, new WorldTile(2511, 3463, 0), 1, 2);
				else if (id == 2022) {
					player.sendMessage("You get in the barrel and start shaking to fall down..");
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							FadingScreen.fade(player, 0, new Runnable() {

								@Override
								public void run() {
									player.setNextWorldTile(new WorldTile(2532, 3415, 0));
									player.sendMessage("The barrel has finally stopped; "
											+ "you've found yourself in a strange place.");
								}
							});
						}
					}, 4);
				}
				// Baxtorian falls dungeon entrance
				else if (id == 37247) {
					FadingScreen.fade(player, 0, new Runnable() {

						@Override
						public void run() {
							player.setNextWorldTile(new WorldTile(2575, 9861, 0));
						}
					});
				}
				// Baxtorian falls dungeon exit
				else if (id == 32711) {
					FadingScreen.fade(player, 0, new Runnable() {

						@Override
						public void run() {
							player.setNextWorldTile(new WorldTile(2511, 3463, 0));
						}
					});
					return;
				} else if (id == 21306 && x == 2317 && y == 3824) {
					if (!Agility.hasLevel(player, 40))
						return;
					player.lock(5);
					player.addWalkSteps(2317, 3832, -1, false);
				} else if (id == 21307 && x == 2317 && y == 3831) {
					if (!Agility.hasLevel(player, 40))
						return;
					player.lock(5);
					player.addWalkSteps(2317, 3823, -1, false);
				} else if (id == 21308) {
					player.lock(5);
					player.addWalkSteps(2343, 3829, -1, false);
				} else if (id == 21309) {
					player.lock(5);
					player.addWalkSteps(2343, 3820, -1, false);
				} else if (id == 67966) {
					player.lock();
					player.setNextAnimation(new Animation(6723));
					player.setNextForceMovement(new NewForceMovement(new WorldTile(player), 4,
							new WorldTile(2512, 3509, 0), 7, Utils.getAngle(0, -1)));
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							FadingScreen.fade(player, 0, new Runnable() {

								@Override
								public void run() {
									player.sendMessage(
											"You are swept, out of control, thought horrific underwater currents.");
									player.sendMessage(
											"You are swirled beneath the water, dashed agaisnt sharp rocks.");
									player.sendMessage("Mystical forces guide you into a cavern below the whirpool.");
									player.setNextWorldTile(new WorldTile(1763, 5365, 1));
									player.lock(1);
								}
							});
						}
					}, 4);
				} else if (id == 25216) {
					player.lock();
					FadingScreen.fade(player, 0, new Runnable() {
						@Override
						public void run() {
							player.sendMessage("You are swept, out of control, thought horrific underwater currents.");
							player.sendMessage("You are swirled beneath the water, dashed agaisnt sharp rocks.");
							player.sendMessage("You find yourself on the banks of the river, far below the lake.");
							player.setNextWorldTile(new WorldTile(2531, 3446, 0));
							player.lock(1);
						}
					});
					// Kuradal dungeon
				}
				// scabaras florest
				else if (id == 28515)
					player.useStairs(4853, new WorldTile(3420, 2803, 1), 2, 3);
				else if (id == 28516)
					player.useStairs(4853, new WorldTile(3420, 2801, 0), 2, 3);
				// legends guild
				else if (id == 41435 && x == 2732 && y == 3377)
					player.useStairs(-1, new WorldTile(2732, 3380, 1), 0, 1);
				else if (id == 41436 && x == 2732 && y == 3378)
					player.useStairs(-1, new WorldTile(2732, 3376, 0), 0, 1);
				else if (id == 41425 && x == 2724 && y == 3374)
					player.useStairs(-1, new WorldTile(2720, 9775, 0), 0, 1);
				else if (id == 32048 && x == 2717 && y == 9773)
					player.useStairs(-1, new WorldTile(2723, 3375, 0), 0, 1);
				else if (id == 1987)
					player.useStairs(-1, new WorldTile(2513, 3480, 0), 1, 2,
							"The raft is pulled down by the strong currents.");
				else if (id == 10283 || id == 1996)
					player.sendMessage("I don't think that's a very smart idea...");
				if (id == 9270) {
					if (player.getOverloadDelay() > 0) {
						player.sendMessage("You may only use the supply table every five minutes.");
						return;
					}
					if (player.getHitpoints() <= 500 || player.getOverloadDelay() > 480) {
						player.sendMessage("You need more than 500 life points to survive the power of overload.");
						return;
					}
					player.setOverloadDelay((player.getPerkManager().herbivore ? 2 : 1) * 501);
					if (player.getPerkManager().herbivore)
						player.sendMessage("Overload extended from 5 mins. to 10 mins. thanks to Herbivore perk.",
								true);
					WorldTasksManager.schedule(new WorldTask() {
						int count = 4;

						@Override
						public void run() {
							if (count == 0)
								stop();
							player.setNextAnimation(new Animation(3170));
							player.setNextGraphics(new Graphics(560));
							player.applyHit(new Hit(player, 100, HitLook.REGULAR_DAMAGE, 0));
							count--;
						}
					}, 0, 2);
					return;
				}
				if (id == 84715) {
					if (object.getY() == 611) {
						if (!Agility.hasLevel(player, 80))
							return;
						player.useStairs(-1, new WorldTile(player.getX() <= 1132 ? 1135 : 1132, player.getY(), 1), 0,
								1);
					} else if (object.getY() >= 652 && object.getY() <= 654
							&& !(object.getX() == 1065 || object.getX() == 1067)) {
						if (!Agility.hasLevel(player, 90))
							return;
						player.useStairs(-1, new WorldTile(player.getX(), player.getY() <= 652 ? 654 : 652, 1), 0, 1);
					} else if (object.getX() >= 1150 && object.getX() <= 1152) {
						if (!Agility.hasLevel(player, 90))
							return;
						player.useStairs(-1, new WorldTile(player.getX() <= 1150 ? 1152 : 1150, player.getY(), 1), 0,
								1);
					} else if (object.getX() == 1096) {
						if (!Agility.hasLevel(player, 70))
							return;
						player.useStairs(-1, new WorldTile(player.getX(), player.getY() <= 626 ? 628 : 626, 1), 0, 1);
					} else if (object.getX() == 1065 || object.getX() == 1067) {
						if (!Agility.hasLevel(player, 60))
							return;
						player.useStairs(-1, new WorldTile(player.getX() <= 1065 ? 1067 : 1065, player.getY(), 1), 0,
								1);
					}
					return;
				}
				if (id == 2322 || id == 2323) {
					if (!Agility.hasLevel(player, 10))
						return;
					player.lock(4);
					player.setNextAnimation(new Animation(751));
					World.sendObjectAnimation(player, object, new Animation(497));
					final WorldTile toTile = new WorldTile(id == 2323 ? 2709 : 2704, y, object.getPlane());
					player.setNextForceMovement(new ForceMovement(player, 1, toTile, 3,
							id == 2323 ? ForceMovement.EAST : ForceMovement.WEST));
					player.getSkills().addXp(Skills.AGILITY, 22);
					player.sendMessage("You skilfully swing across.", true);
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.setNextWorldTile(toTile);
							stop();
						}
					}, 1);
					return;
				}
				if (id == 24369 || id == 24370 || id == 24560) {
					handleGate(player, object);
					return;
				}
				if (id == 29316 || id == 29320) {
					handleGate(player, object);
					return;
				}
				if (id == 2788 || id == 2789) {
					boolean isEntering = player.getX() >= 2504;
					player.addWalkSteps(x + (isEntering ? -1 : 0), y, 1, false);
					handleGate(player, object, 600);
					return;
				}
				if (id == 15604 || id == 15605) {
					boolean isEntering = player.getX() >= 2555;
					player.addWalkSteps(x + (isEntering ? -1 : +1), y, 1, false);
					handleGate(player, object, 600);
					return;
				}
				if (id == 29099) {
					if (!Agility.hasLevel(player, 29))
						return;
					player.useStairs(1133, new WorldTile(2596, player.getY() <= 2869 ? 2871 : 2869, 0), 1, 2);
					return;
				}
				if (id == 3944 || id == 3945)
					player.addWalkSteps(x, y + (player.getY() >= 3335 ? -1 : 1), -1, false);
				if (id == 31149) {
					boolean isEntering = player.getX() <= 3295;
					player.useStairs(isEntering ? 9221 : 9220, new WorldTile(x + (isEntering ? 1 : 0), y, 0), 1, 2);
					return;
				}
				if (id == 2333 || id == 2334 || id == 2335) {
					if (!Agility.hasLevel(player, 30))
						return;
					player.setNextAnimation(new Animation(741));
					player.setNextForceMovement(new NewForceMovement(object, 1, object, 2,
							Utils.getAngle(object.getX() - player.getX(), object.getY() - player.getY())));
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.setNextWorldTile(object);
						}
					});
				}
				if (id == 90223) {
					player.getActionManager().setAction(
							new DivineFishing(DivineFishing.DivineFishingSpots.DIVINE_CRAYFISH, x, y, plane, object));
					return;
				}
				if (id == 90224) {
					player.getActionManager().setAction(
							new DivineFishing(DivineFishing.DivineFishingSpots.DIVINE_HERRING, x, y, plane, object));
					return;
				}
				if (id == 90225) {
					player.getActionManager().setAction(
							new DivineFishing(DivineFishing.DivineFishingSpots.DIVINE_TROUT, x, y, plane, object));
					return;
				}
				if (id == 90226) {
					player.getActionManager().setAction(
							new DivineFishing(DivineFishing.DivineFishingSpots.DIVINE_SALMON, x, y, plane, object));
					return;
				}
				if (id == 90227) {
					player.getActionManager().setAction(
							new DivineFishing(DivineFishing.DivineFishingSpots.DIVINE_LOBSTER, x, y, plane, object));
					return;
				}
				if (id == 90228) {
					player.getActionManager().setAction(
							new DivineFishing(DivineFishing.DivineFishingSpots.DIVINE_SWORDFISH, x, y, plane, object));
					return;
				}
				if (id == 90229) {
					player.getActionManager().setAction(
							new DivineFishing(DivineFishing.DivineFishingSpots.DIVINE_SHARK, x, y, plane, object));
					return;
				}
				if (id == 90230) {
					player.getActionManager().setAction(
							new DivineFishing(DivineFishing.DivineFishingSpots.DIVINE_CAVEFISH, x, y, plane, object));
					return;
				}
				if (id == 90231) {
					player.getActionManager().setAction(
							new DivineFishing(DivineFishing.DivineFishingSpots.DIVINE_ROCKTAIL, x, y, plane, object));
					return;
				}
				if (id == 87280) {
					player.getActionManager().setAction(
							new DivineHerblore(DivineHerblore.DivineHerbSpots.DIVINE_HERB_I, x, y, plane, object));
					return;
				}
				if (id == 87281) {
					player.getActionManager().setAction(
							new DivineHerblore(DivineHerblore.DivineHerbSpots.DIVINE_HERB_II, x, y, plane, object));
					return;
				}
				if (id == 87282) {
					player.getActionManager().setAction(
							new DivineHerblore(DivineHerblore.DivineHerbSpots.DIVINE_HERB_III, x, y, plane, object));
					return;
				}
				if (id == 87270) {
					player.getActionManager().setAction(new DivineHunting(
							DivineHunting.DivineHuntingSpots.DIVINE_KEBBIT_BURROW, x, y, plane, object));
					return;
				}
				if (id == 87271) {
					player.getActionManager().setAction(
							new DivineHunting(DivineHunting.DivineHuntingSpots.DIVINE_BIRD_SNARE, x, y, plane, object));
					return;
				}
				if (id == 87272) {
					player.getActionManager().setAction(new DivineHunting(
							DivineHunting.DivineHuntingSpots.DIVINE_DEADFALL_TRAP, x, y, plane, object));
					return;
				}
				if (id == 87273) {
					player.getActionManager().setAction(
							new DivineHunting(DivineHunting.DivineHuntingSpots.DIVINE_BOX_TRAP, x, y, plane, object));
					return;
				}
				if (id == 66528) {
					player.getActionManager().setAction(new DivineSimulacrum(
							DivineSimulacrum.DivineSimulacrumSpots.DIVINE_SIMULACRUM_I, x, y, plane, object));
					return;
				}
				if (id == 66531) {
					player.getActionManager().setAction(new DivineSimulacrum(
							DivineSimulacrum.DivineSimulacrumSpots.DIVINE_SIMULACRUM_II, x, y, plane, object));
					return;
				}
				/**
				 * Slayer Tower.
				 */
				if (id == 82728) { /** Entrance **/
					if (player.getY() <= 3538)
						player.setNextWorldTile(new WorldTile(3423, 3540, 0));
					else
						player.setNextWorldTile(new WorldTile(3423, 3538, 0));
					return;
				}
				if (id == 82666 || id == 82669) { /** Corner staircase **/
					player.useStairs(-1, new WorldTile(player.getX(), player.getY(), 1), 0, 0);
					return;
				}
				if (id == 82488) { /** West Stairs - Up **/
					player.useStairs(-1, new WorldTile(3411, player.getY(), 3), 0, 0);
					return;
				}
				if (id == 82489) { /** East Stairs - Up **/
					player.useStairs(-1, new WorldTile(3435, player.getY(), 3), 0, 0);
					return;
				}
				if (id == 82490) { /** West Stairs - Down **/
					player.useStairs(-1, new WorldTile(3415, player.getY(), 2), 0, 0);
					return;
				}
				if (id == 82491) { /** East Stairs - Down **/
					player.useStairs(-1, new WorldTile(3431, player.getY(), 2), 0, 0);
					return;
				}
				if (id == 82605) {
					/** Cross Plank - shortcut **/
					if (player.getSkills().getLevel(Skills.AGILITY) < 71) {
						player.sendMessage("You need a level of 71 Agility to use this shortcut.");
						return;
					}
					if (player.getY() <= 3546) {
						player.useStairs(-1, new WorldTile(3416, 3550, 2), 0, 0);
						return;
					}
					player.useStairs(-1, new WorldTile(3416, 3545, 2), 0, 0);
					return;
				}
				if (id == 82432) {
					handleDoor(player, object);
					return;
				}
				/** End of Slayer Tower **/
				if (id == 2623) { // taverly dungeon door
					handleDoor(player, object);
					return;
				}
				if (id == 2562) {
					player.getDialogueManager().startDialogue("CompCape");
					return;
				}
				if (id == 9038 || id == 9039) {
					WorldTile destination = new WorldTile((player.getX() <= 2816 ? 2817 : 2816), player.getY(), 0);
					player.useStairs(-1, destination, 0, 0);
					return;
				}
				if (id == 75491) {
					player.useStairs(-1, new WorldTile(3088, 3492, 0), 0, 0);
					return;
				}
				if (id == 67968 || id == 94067) {
					RobustGlass.addSandstone(player);
					return;
				}
				if (id == 63093) { // enter poly cave
					player.useStairs(832, new WorldTile(4620, 5458, 3), 2, 2);
					return;
				}
				if (id == 29387) { // enter artisans DS
					player.useStairs(-1, new WorldTile(3067, 9710, 0), 1, 1);
					return;
				}
				if (id == 29386) { // enter artisans DS
					player.useStairs(-1, new WorldTile(3067, 9710, 0), 1, 1);
					return;
				}
				if (id == 29385) { // enter artisans DS
					player.useStairs(-1, new WorldTile(3067, 9710, 0), 1, 1);
					return;
				}
				if (id == 29392) { // enter artisans UP
					player.useStairs(-1, new WorldTile(3061, 3335, 0), 1, 1);
					return;
				}
				if (id == 57225) {
					if (player.getX() <= 2910) {
						player.getDialogueManager().startDialogue("NexEntrance");
					} else
						player.getDialogueManager().startDialogue("SimpleMessage",
								"There is no way to get back up there.");
					return;
				}
				if (id == 75882) {
					if (player.getX() >= 3335) {
						player.useStairs(-1, new WorldTile(3332, player.getY(), 0), 0, 0);
					} else
						player.useStairs(-1, new WorldTile(3335, player.getY(), 0), 0, 0);
					return;
				}
				if (id == 48496) {
					player.setNextWorldTile(new WorldTile(3972, 5562, 0));
					return;
				}
				if (id == 2408) {
					player.useStairs(828, new WorldTile(2823, 9771, 0), 1, 1);
					return;
				}
				// nomads requiem
				if (id == 18425 && !player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM)) {
					NomadsRequiem.enterNomadsRequiem(player);
					// player.sendMessage("Nomad's Requiem is currently
					// disabled, sorry.");
				}
				if (id == 42219) {
					player.getControlerManager().startControler("AreaController");
					player.useStairs(-1, new WorldTile(1886, 3178, 0), 0, 1);
					if (player.getQuestManager().getQuestStage(Quests.NOMADS_REQUIEM) == -2)
						player.getQuestManager().setQuestStageAndRefresh(Quests.NOMADS_REQUIEM, 0);
				}
				if (id == 63094) { // exit poly cave
					player.useStairs(832, new WorldTile(3410, 3329, 0), 2, 2);
					return;
				}
				/**
				 * Nex Angel of death
				 */
				if (id == 57264) {
					player.getDialogueManager().startDialogue("NexAngelD", object);
					return;
				}

				/**
				 * Dye removal system for t90 armor autor: kingkenobi
				 */

				if (id == 110070) {
					if (player.getInventory().getFreeSlots() < 3) {
						player.sm("You need to have 2 Free Inventory Slot before you can use this feature!");
						return;

					}
					/*
					 * Sirenic
					 */
					// Sirenic (barrow)
					if (player.getInventory().containsItem(33348, 1)) {
						player.getInventory().deleteItem(33348, 1);
						player.getInventory().addItem(29854, 1);
						player.getInventory().addItem(33294, 1);
					}
					if (player.getInventory().containsItem(33351, 1)) {
						player.getInventory().deleteItem(33351, 1);
						player.getInventory().addItem(29857, 1);
						player.getInventory().addItem(33294, 1);
					}
					if (player.getInventory().containsItem(33354, 1)) {
						player.getInventory().deleteItem(33354, 1);
						player.getInventory().addItem(29860, 1);
						player.getInventory().addItem(33294, 1);
					}
					// sirenic (shadow)
					if (player.getInventory().containsItem(33414, 1)) {
						player.getInventory().deleteItem(33414, 1);
						player.getInventory().addItem(29854, 1);
						player.getInventory().addItem(33296, 1);
					}
					if (player.getInventory().containsItem(33417, 1)) {
						player.getInventory().deleteItem(33417, 1);
						player.getInventory().addItem(29857, 1);
						player.getInventory().addItem(33296, 1);
					}
					if (player.getInventory().containsItem(33420, 1)) {
						player.getInventory().deleteItem(33420, 1);
						player.getInventory().addItem(29860, 1);
						player.getInventory().addItem(33296, 1);
					}
					// sirenic (Third Age)
					if (player.getInventory().containsItem(33480, 1)) {
						player.getInventory().deleteItem(33480, 1);
						player.getInventory().addItem(29854, 1);
						player.getInventory().addItem(33298, 1);
					}
					if (player.getInventory().containsItem(33483, 1)) {
						player.getInventory().deleteItem(33483, 1);
						player.getInventory().addItem(29857, 1);
						player.getInventory().addItem(33298, 1);
					}
					if (player.getInventory().containsItem(33486, 1)) {
						player.getInventory().deleteItem(33486, 1);
						player.getInventory().addItem(29860, 1);
						player.getInventory().addItem(33298, 1);
					}
					// sirenic (blood)
					if (player.getInventory().containsItem(36285, 1)) {
						player.getInventory().deleteItem(36285, 1);
						player.getInventory().addItem(29854, 1);
						player.getInventory().addItem(36274, 1);
					}
					if (player.getInventory().containsItem(36288, 1)) {
						player.getInventory().deleteItem(36288, 1);
						player.getInventory().addItem(29857, 1);
						player.getInventory().addItem(36274, 1);
					}
					if (player.getInventory().containsItem(36291, 1)) {
						player.getInventory().deleteItem(36291, 1);
						player.getInventory().addItem(29860, 1);
						player.getInventory().addItem(36274, 1);
					}
					// sirenic (ice)
					if (player.getInventory().containsItem(42073, 1)) {
						player.getInventory().deleteItem(42073, 1);
						player.getInventory().addItem(29854, 1);
						player.getInventory().addItem(41887, 1);
					}
					if (player.getInventory().containsItem(42076, 1)) {
						player.getInventory().deleteItem(42076, 1);
						player.getInventory().addItem(29857, 1);
						player.getInventory().addItem(41887, 1);
					}
					if (player.getInventory().containsItem(42079, 1)) {
						player.getInventory().deleteItem(42079, 1);
						player.getInventory().addItem(29860, 1);
						player.getInventory().addItem(41887, 1);
					}

					/*
					 * Tectonic
					 */
					// Tectonic (barrow)
					if (player.getInventory().containsItem(33339, 1)) {
						player.getInventory().deleteItem(33339, 1);
						player.getInventory().addItem(28608, 1);
						player.getInventory().addItem(33294, 1);
					}
					if (player.getInventory().containsItem(33342, 1)) {
						player.getInventory().deleteItem(33342, 1);
						player.getInventory().addItem(28611, 1);
						player.getInventory().addItem(33294, 1);
					}
					if (player.getInventory().containsItem(33345, 1)) {
						player.getInventory().deleteItem(33345, 1);
						player.getInventory().addItem(28614, 1);
						player.getInventory().addItem(33294, 1);
					}
					// Tectonic (shadow)
					if (player.getInventory().containsItem(33405, 1)) {
						player.getInventory().deleteItem(33405, 1);
						player.getInventory().addItem(28608, 1);
						player.getInventory().addItem(33296, 1);
					}
					if (player.getInventory().containsItem(33408, 1)) {
						player.getInventory().deleteItem(33408, 1);
						player.getInventory().addItem(28611, 1);
						player.getInventory().addItem(33296, 1);
					}
					if (player.getInventory().containsItem(33411, 1)) {
						player.getInventory().deleteItem(33411, 1);
						player.getInventory().addItem(28614, 1);
						player.getInventory().addItem(33296, 1);
					}
					// Tectonic (Tird Age)
					if (player.getInventory().containsItem(33471, 1)) {
						player.getInventory().deleteItem(33471, 1);
						player.getInventory().addItem(28608, 1);
						player.getInventory().addItem(33298, 1);
					}
					if (player.getInventory().containsItem(33474, 1)) {
						player.getInventory().deleteItem(33474, 1);
						player.getInventory().addItem(28611, 1);
						player.getInventory().addItem(33298, 1);
					}
					if (player.getInventory().containsItem(33477, 1)) {
						player.getInventory().deleteItem(33477, 1);
						player.getInventory().addItem(28614, 1);
						player.getInventory().addItem(33298, 1);
					}
					// Tectonic (Blood)
					if (player.getInventory().containsItem(36276, 1)) {
						player.getInventory().deleteItem(36276, 1);
						player.getInventory().addItem(28608, 1);
						player.getInventory().addItem(36274, 1);
					}
					if (player.getInventory().containsItem(36279, 1)) {
						player.getInventory().deleteItem(36279, 1);
						player.getInventory().addItem(28611, 1);
						player.getInventory().addItem(36274, 1);
					}
					if (player.getInventory().containsItem(36282, 1)) {
						player.getInventory().deleteItem(36282, 1);
						player.getInventory().addItem(28614, 1);
						player.getInventory().addItem(36274, 1);
					}
					// Tectonic (ICE)
					if (player.getInventory().containsItem(42082, 1)) {
						player.getInventory().deleteItem(42082, 1);
						player.getInventory().addItem(28608, 1);
						player.getInventory().addItem(41887, 1);
					}
					if (player.getInventory().containsItem(42085, 1)) {
						player.getInventory().deleteItem(42085, 1);
						player.getInventory().addItem(28611, 1);
						player.getInventory().addItem(41887, 1);
					}
					if (player.getInventory().containsItem(42088, 1)) {
						player.getInventory().deleteItem(42088, 1);
						player.getInventory().addItem(28614, 1);
						player.getInventory().addItem(41887, 1);
					}
					/*
					 * Malevolent
					 */
					// Malevolent (barrow)
					if (player.getInventory().containsItem(33357, 1)) {
						player.getInventory().deleteItem(33357, 1);
						player.getInventory().addItem(30005, 1);
						player.getInventory().addItem(33294, 1);
					}
					if (player.getInventory().containsItem(33360, 1)) {
						player.getInventory().deleteItem(33360, 1);
						player.getInventory().addItem(30008, 1);
						player.getInventory().addItem(33294, 1);
					}
					if (player.getInventory().containsItem(33363, 1)) {
						player.getInventory().deleteItem(33363, 1);
						player.getInventory().addItem(30011, 1);
						player.getInventory().addItem(33294, 1);
					}
					// Malevolent (Shadow)
					if (player.getInventory().containsItem(33423, 1)) {
						player.getInventory().deleteItem(33423, 1);
						player.getInventory().addItem(30005, 1);
						player.getInventory().addItem(33296, 1);
					}
					if (player.getInventory().containsItem(33426, 1)) {
						player.getInventory().deleteItem(33426, 1);
						player.getInventory().addItem(30008, 1);
						player.getInventory().addItem(33296, 1);
					}
					if (player.getInventory().containsItem(33429, 1)) {
						player.getInventory().deleteItem(33429, 1);
						player.getInventory().addItem(30011, 1);
						player.getInventory().addItem(33296, 1);
					}
					// Malevolent (Tird Age)
					if (player.getInventory().containsItem(33489, 1)) {
						player.getInventory().deleteItem(33489, 1);
						player.getInventory().addItem(30005, 1);
						player.getInventory().addItem(33298, 1);
					}
					if (player.getInventory().containsItem(33492, 1)) {
						player.getInventory().deleteItem(33492, 1);
						player.getInventory().addItem(30008, 1);
						player.getInventory().addItem(33298, 1);
					}
					if (player.getInventory().containsItem(33495, 1)) {
						player.getInventory().deleteItem(33495, 1);
						player.getInventory().addItem(30011, 1);
						player.getInventory().addItem(33298, 1);
					}
					// Malevolent (Blood)
					if (player.getInventory().containsItem(36294, 1)) {
						player.getInventory().deleteItem(36294, 1);
						player.getInventory().addItem(30005, 1);
						player.getInventory().addItem(36274, 1);
					}
					if (player.getInventory().containsItem(36297, 1)) {
						player.getInventory().deleteItem(36297, 1);
						player.getInventory().addItem(30008, 1);
						player.getInventory().addItem(36274, 1);
					}
					if (player.getInventory().containsItem(36300, 1)) {
						player.getInventory().deleteItem(36300, 1);
						player.getInventory().addItem(30011, 1);
						player.getInventory().addItem(36274, 1);
					}
					// Malevolent (Ice)
					if (player.getInventory().containsItem(42040, 1)) {
						player.getInventory().deleteItem(42040, 1);
						player.getInventory().addItem(30005, 1);
						player.getInventory().addItem(41887, 1);
					}
					if (player.getInventory().containsItem(42043, 1)) {
						player.getInventory().deleteItem(42043, 1);
						player.getInventory().addItem(30008, 1);
						player.getInventory().addItem(41887, 1);
					}
					if (player.getInventory().containsItem(42046, 1)) {
						player.getInventory().deleteItem(42046, 1);
						player.getInventory().addItem(30011, 1);
						player.getInventory().addItem(41887, 1);
					}

					return;

				}

				// Rise of the six - jump in well object
				if (id == 87997) {

					if (player.getFamiliar() != null || player.getPet() != null || Pets.hasPet(player)) {
						player.getDialogueManager().startDialogue("SimpleNPCMessage", THHAAR_MEJ_JAL,
								"Pets and Familiars are not allowed in Minigames or your account will Reset to 0!"
										+ " Please Dismiss your pet/familiar and remove your pet/pouches in your inventory");
						return;
					}
					player.getControlerManager().startControler("RiseOfTheSix");
					return;
				}

				if (player.getTreasureTrails().useObject(object))
					return;
				if (object.getId() == 11005) {
					if (object.getX() == 3978 && object.getY() == 5552) {
						if (player.getX() <= 3977) {
							player.dungKills = 0;
							player.inDungeoneering = true;
							player.addWalkSteps(3979, 5552, 2, false);
							player.getControlerManager().startControler("Dungeoneering");
							player.lock(1);
						}
					}
					if (object.getX() == 5024 && object.getY() == 9243) {
						if (player.getY() <= 9244) {
							player.dungKills = 0;
							player.inDungeoneering = true;
							player.addWalkSteps(5024, 9241, 0, false);
							player.getControlerManager().startControler("Dungeoneering");
							player.lock(1);
						}
					}
					return;
				}

				// Dragonkin elite dungeon
				if (object.getId() == 101910) {
					if (object.getX() == 3978 && object.getY() == 5552) {
						if (player.getX() <= 3977) {
							player.dungKills = 0;
							player.inDungeoneering = true;
							player.addWalkSteps(5024, 9242, 0, false);
							player.getControlerManager().startControler("Dungeoneering");
							player.lock(1);
						}
					}
					return;
				}
				// end

				if (BrimhDungeon.handleObjects(player, id))
					return;
				if (id == 51061) {
					final int maxPrayer = player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
					if (player.getPrayer().getPrayerpoints() < maxPrayer) {
						player.setNextAnimation(new Animation(645));
						player.getPrayer().restorePrayer(maxPrayer);
						player.sendMessage("You've recharged your prayer points.");
					}
					player.getDialogueManager().startDialogue("AltarSwapD");
					return;
				}
				/**
				 * Multiple Hiscores scoreboard.
				 */
				if (id == 30205) {
					player.getDialogueManager().startDialogue("ScoreboardD");
					return;
				}
				/**
				 * Zanaris shed / Fairy rings.
				 */
				if (id == 2406) {
					if (FairyRing.checkAll(player))
						player.useStairs(-1, new WorldTile(2452, 4473, 0), 1, 2);
					else
						handleDoor(player, object);
					return;
				}
				if (id == 16944) {
					FairyRing.openRingInterface(player, object, false);
					return;
				}
				/**
				 * fremennik dungeon
				 */
				if (id == 44339) {
					if (player.getSkills().getLevelForXp(Skills.AGILITY) >= 81) {
						if (player.getX() >= 2775)
							player.setNextWorldTile(new WorldTile(player.getX() - 7, player.getY(), player.getPlane()));
						else
							player.setNextWorldTile(new WorldTile(player.getX() + 7, player.getY(), player.getPlane()));
					} else
						player.sendMessage("You need at least a level of 81 agility to use this shortcut.");
					return;
				}
				if (id == 77052) {
					if (player.getSkills().getLevelForXp(Skills.AGILITY) >= 62) {
						if (player.getX() <= 2730)
							player.setNextWorldTile(new WorldTile(player.getX() + 5, player.getY(), player.getPlane()));
						else
							player.setNextWorldTile(new WorldTile(player.getX() - 5, player.getY(), player.getPlane()));
					} else
						player.sendMessage("You need at least a level of 62 agility to use this shortcut.");
					return;
				}
				/**
				 * Ancient cavern stairs.
				 */
				if (id == 25339) {
					player.setNextWorldTile(new WorldTile(1778, 5343, 1));
					return;
				}
				if (id == 16118) {
					if (player.getLastVoteClaim() > Utils.currentTimeMillis()) {
						player.sendMessage("<col=ff0000>You have rejuvenated 50hp, you need to wait:<shad=ffffff> "
								+ Utils.getTimeRemaining(player.getLastVoteClaim())
								+ "</shad> to be able to rejuvenate again.</col>");
						return;
					}
					long time = System.currentTimeMillis() + (60000);
					player.setLastVoteClaim(time);
					player.getNextHits().add(new Hit(player, 100, HitLook.HEALED_DAMAGE));
					player.setNextAnimationForce(new Animation(player.isUnderCombat() ? 18002 : 18001));
					player.heal(100);
					return;
				}

				if (id == 25340) {
					player.setNextWorldTile(new WorldTile(1778, 5346, 0));
					return;
				}
				if (id == 24819) {
					player.setNextWorldTile(new WorldTile(2333, 3171, 0));
					return;
				}
				if (id == 25336) {
					player.setNextWorldTile(new WorldTile(1768, 5366, 1));
					return;
				} else if (id == 24991) {
					player.getControlerManager().startControler("PuroPuro");
				} else if (id == 2873 || id == 2874 || id == 2875) {
					player.sendMessage(
							"You kneel and begin to chant to " + objectDef.name.replace("Statue of ", "") + "...");
					player.setNextAnimation(new Animation(645));
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							player.getDialogueManager().startDialogue("SimpleMessage",
									"You feel a rush of energy charge through your veins. "
											+ "Suddenly a cape appears before you.");
							World.addGroundItem(new Item(id == 2873 ? 2412 : id == 2874 ? 2414 : 2413),
									new WorldTile(object.getX(), object.getY() - 1, 0));
						}
					}, 3);
				}
				if (object.getId() == 77834) {
					player.getDialogueManager().startDialogue("KbdInterface", object);
					return;
				} else if (id >= 65616 && id <= 65622)
					WildernessObelisk.activateObelisk(id, player);
				else if (id == 69827)
					player.activateLodeStone(object, player);
				else if (id == 69828)
					player.activateLodeStone(object, player);
				else if (id == 69829)
					player.activateLodeStone(object, player);
				else if (id == 69830)
					player.activateLodeStone(object, player);
				else if (id == 69831)
					player.activateLodeStone(object, player);
				else if (id == 69832)
					player.activateLodeStone(object, player);
				else if (id == 69833)
					player.activateLodeStone(object, player);
				else if (id == 69834)
					player.activateLodeStone(object, player);
				else if (id == 69835)
					player.activateLodeStone(object, player);
				else if (id == 69837)
					player.activateLodeStone(object, player);
				else if (id == 69838)
					player.activateLodeStone(object, player);
				else if (id == 69839)
					player.activateLodeStone(object, player);
				else if (id == 69840)
					player.activateLodeStone(object, player);
				else if (id == 69841)
					player.activateLodeStone(object, player);
				if (player.getFarmingManager().isFarming(id, null, 1))
					return;
				if (object.getId() == 48189 || object.getId() == 42891) {
					player.setNextWorldTile(new WorldTile(2736, 3731, 0));
					return;
				}
				if (object.getId() == 42793) {
					player.setNextWorldTile(new WorldTile(3485, 5511, 0));
					return;
				}
				if (object.getId() == 48188) {
					player.setNextWorldTile(new WorldTile(3435, 5646, 0));
					return;
				}
				if (id == 24360) {
					player.setNextWorldTile(new WorldTile(3190, 9833, 0));
					return;
				}
				if (id == 24365) {
					player.setNextWorldTile(new WorldTile(3188, 3433, 0));
					return;
				}
				if (object.getId() == 66796) {
					player.setNextWorldTile(new WorldTile(2840, 3534, 2));
					return;
				}
				if (TrapAction.isTrap(player, object, id) || TrapAction.isTrap(player, object))
					return;
				if (object.getId() == 77834) {
					player.getDialogueManager().startDialogue("KbdTeleport", object);
					return;
				}
				if (object.getId() == 52860) {
					if (player.getSkills().getLevelForXp(Skills.MINING) < 75) {
						player.sendMessage("You need atleast 75 mining to enter that dungeon.");
						return;
					}
					player.setNextWorldTile(new WorldTile(1181, 4515, 0));
					return;
				}
				if (object.getId() == 52872) {
					player.setNextWorldTile(new WorldTile(3298, 3307, 0));
					return;
				}
				if (id == 62677) {
					player.getDominionTower().talkToFace();
					return;
				}

				if (id == 38669 || id == 41337) {
					ShootingStar.openNoticeboard(player);
					return;
				}
				if (id == 25591 || id == 7092) {
					ShootingStar.openTelescope(player);
					return;
				}
				if (object.getId() == 16543) {
					if (player.getSkills().getLevelForXp(Skills.THIEVING) < 85) {
						player.sendMessage("You need atleast 85 thieving to enter the pyramid.");
						return;
					}
					player.getControlerManager().startControler("PyramidPlunder");
					return;
				}
				// Reaper portal to home
				if (object.getId() == 96782) {
					player.lock();
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							FadingScreen.fade(player, 0, new Runnable() {

								@Override
								public void run() {
									player.unlock();
									player.setNextWorldTile(player.getHomeTile());
								}
							});
						}
					}, 0);
					return;
				}
				// Enter reaper portal
				if (object.getId() == 92120) {
					player.lock();
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							FadingScreen.fade(player, 0, new Runnable() {

								@Override
								public void run() {
									player.unlock();
									player.setNextWorldTile(new WorldTile(414, 652, 0));
								}
							});
						}
					}, 0);
					return;
				}
				if (object.getId() == 45802) {
					player.getDialogueManager().startDialogue("ContractDialogue");
					return;
				}
				if (id == 9312 || id == 9311) {
					if (player.getSkills().getLevel(Skills.AGILITY) < 21) {
						player.sendMessage("You need 21 agility to use this shortcut.");
						return;
					}
					final WorldTile toTile1 = new WorldTile(3141, 3515, player.getPlane());
					final WorldTile toTile2 = new WorldTile(object.getId() == 9312 ? 3139 : 3143,
							object.getId() == 9312 ? 3517 : 3513, player.getPlane());
					player.lock(4);
					player.setNextAnimation(new Animation(2589));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextWorldTile(toTile1);
							player.setNextAnimation(new Animation(2590));
							stop();
						}
					}, 2);
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextWorldTile(toTile2);
							player.setNextAnimation(new Animation(2591));
							stop();
						}
					}, 4);
					return;
				}
				if (id == 39468) {
					player.setNextWorldTile(new WorldTile(1745, 5325, 0));
					return;
				}
				if (id == 26341) {
					player.useStairs(828, new WorldTile(2882, 5311, 0), 0, 0);
					player.getControlerManager().startControler("GodWars");
					return;
				}
				if (id == 25337) {
					player.setNextWorldTile(new WorldTile(1694, 5296, 1));
					return;
				}
				if (id == 25338) {
					player.setNextWorldTile(new WorldTile(1772, 5366, 0));
					return;
				}

				if (id == 3219) {
					player.getPorts().enterPorts();
					return;
				}
				if (id == 70812) {
					player.getControlerManager().startControler("QueenBlackDragonControler");
					return;
				}
				if (id == 47237) {
					if (player.getSkills().getLevel(Skills.AGILITY) < 90) {
						player.sendMessage("You need 90 agility to use this shortcut.");
						return;
					}
					if (player.getX() == 1641 && player.getY() == 5260 || player.getX() == 1641 && player.getY() == 5259
							|| player.getX() == 1640 && player.getY() == 5259) {
						player.setNextWorldTile(new WorldTile(1641, 5268, 0));
					} else {
						player.setNextWorldTile(new WorldTile(1641, 5260, 0));
					}
				}
				if (id == 66115 || id == 66116) {
					player.resetWalkSteps();
					player.setNextAnimation(new Animation(830));
					player.lock();
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.unlock();
							if (Barrows.digIntoGrave(player))
								return;
							player.sendMessage("You find nothing.");
						}
					});
					return;
				}
				if (id == 47232) {
					player.setNextWorldTile(new WorldTile(1661, 5257, 0));
					return;
				}
				if (id == 2473) {
					player.setNextWorldTile(new WorldTile(3039, 4834, 0));
					return;
				}
				if (id == 64294) {
					int jumpStage;
					if (player.getX() == 4685 && player.getY() == 5476) {
						jumpStage = 4685 - 4;
					} else {
						jumpStage = 4658 + 4;
					}
					final WorldTile toTile = new WorldTile(jumpStage, player.getY(), player.getPlane());
					player.lock(4);
					player.setNextAnimation(new Animation(15461));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextForceMovement(new ForceMovement(player, 0, toTile, 2, ForceMovement.EAST));
							player.setNextWorldTile(toTile);
							player.setNextAnimation(new Animation(-1));
							player.unlock();
							stop();
						}
					}, 2);
					return;
				}
				if (id == 64295) {
					int jumpStage;
					if (player.getX() == 4681 && player.getY() == 5476) {
						jumpStage = 4681 + 4;
					} else {
						jumpStage = 4663 - 5;
					}
					final WorldTile toTile = new WorldTile(jumpStage, player.getY(), player.getPlane());
					player.lock(4);
					player.setNextAnimation(new Animation(15461));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextForceMovement(new ForceMovement(player, 0, toTile, 2, ForceMovement.EAST));
							player.setNextWorldTile(toTile);
							player.setNextAnimation(new Animation(-1));
							player.unlock();
							stop();
						}
					}, 2);
					return;
				}
				if (id == 64360) {
					player.lock(3);
					player.setNextAnimation(new Animation(15458));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextAnimation(new Animation(15457));
							player.setNextWorldTile(new WorldTile(4630, 5452, 2));
							player.setNextAnimation(new Animation(-1));
							player.unlock();
							stop();
						}
					}, 1);
				}
				if (id == 64359) {
					player.lock(3);
					player.setNextAnimation(new Animation(15458));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextAnimation(new Animation(15457));
							player.setNextWorldTile(new WorldTile(player.getX(), player.getY(), player.getPlane() - 1));
							player.setNextAnimation(new Animation(-1));
							player.unlock();
							stop();
						}
					}, 1);
					return;
				}
				if (id == 64361) {
					player.lock(3);
					player.setNextAnimation(new Animation(15456));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							if (player.getX() == 4630 && player.getY() == 5452
									|| player.getX() == 4628 && player.getY() == 5451) {
								player.setNextWorldTile(new WorldTile(4629, 5454, 3));
							} else {
								player.setNextWorldTile(
										new WorldTile(player.getX(), player.getY(), player.getPlane() + 1));
							}
							player.setNextWorldTile(new WorldTile(player.getX(), player.getY(), player.getPlane() + 1));
							player.setNextAnimation(new Animation(-1));
							player.unlock();
							stop();
						}
					}, 1);
					return;
				}
				if (id == 12266) {
					player.getDialogueManager().startDialogue("SimplePlayerMessage",
							"This trapdoor seems to be stuck..");
					return;
				}
				if (id == 44392) {
					player.getDialogueManager().startDialogue("SimplePlayerMessage",
							"Books.. books... boooks.... Well, this is quite boring.");
					return;
				}
				if (id == 12349 || id == 12350 || id == 12356) {
					if (!player.hasTalkedtoCook()) {
						player.getDialogueManager().startDialogue("SimplePlayerMessage",
								"I should probably talk to the Cook at home before venturing in here.");
						return;
					}
					if (player.isKilledCulinaromancer()) {
						player.getDialogueManager().startDialogue("RfdPortal", true);
						return;
					}
					player.getControlerManager().startControler("ImpossibleJadControler");
					return;
				}
				if (id == 15653) {// warriors guild entrance
					if (player.getX() >= 2877)
						WarriorsGuild.canEnter(player);
					else {
						WarriorsGuild.exit(player);
						player.getControlerManager().forceStop();
					}
					return;
				}
				if (id == 34234) {
					player.sendMessage("This door seems to be stuck; I should try the other one.");
					return;
				}
				if (id == 22119) {
					player.sendMessage("This door seems to be stuck.");
					return;
				}
				/**
				 * Port objects.
				 */
				if (id == 81427) { // Stairs NE Building Northside - up
					player.useStairs(-1, new WorldTile(4078, 7294, 1), 0, 0);
					return;
				}
				if (id == 81428) { // Stairs NE Building Northside - down
					player.useStairs(-1, new WorldTile(4081, 7291, 0), 0, 0);
					return;
				}
				if (id == 81198) { // Stairs NE Building Eastside - up
					player.useStairs(-1, new WorldTile(4091, 7285, 1), 0, 0);
					return;
				}
				if (id == 81199 && (object.getX() == 4091 && object.getY() == 7267 && object.getPlane() == 1)) {
					player.useStairs(-1, new WorldTile(4093, 7266, 0), 0, 0); // SE
					// building
					// down
				}
				if (id == 81199 && (object.getX() == 4092 && object.getY() == 7283 && object.getPlane() == 1)) {
					player.useStairs(-1, new WorldTile(4094, 7282, 0), 0, 0);// NE
					// building
					// down
					return;
				}
				if (id == 81181 && (object.getX() == 4091 && object.getY() == 7267 && object.getPlane() == 0)) { // Stairs
					// SE
					// Building
					// Eastside
					// -
					// up
					player.useStairs(-1, new WorldTile(4090, 7269, 1), 0, 0);
					return;
				}
				if (id == 81124) { // ladder SW building up
					player.useStairs(-1, new WorldTile(4039, 7275, 1), 0, 0);
					return;
				}
				if (id == 81125) { // ladder SW building down
					player.useStairs(-1, new WorldTile(4038, 7276, 0), 0, 0);
					return;
				}
				if (id == 81214) { // Stairs bar up
					player.useStairs(-1, new WorldTile(4052, 7293, 1), 0, 0);
					return;
				}
				if (id == 81215) { // Stairs bar down
					player.useStairs(-1, new WorldTile(4049, 7290, 0), 0, 0);
					return;
				}
				/**
				 * Sophanem slayer dungeon
				 */
				if (id == 109155) { // climb rope - up
					player.useStairs(828, new WorldTile(2405, 6863, 1), 1, 2);
					return;
				}
				if (id == 109156) { // climb rope - up
					player.useStairs(828, new WorldTile(2405, 6854, 3), 1, 2);
					return;
				}

				/**
				 * Araxxor objects.
				 */
				if (id == 91553) { // climb rope - up
					player.useStairs(828, new WorldTile(3700, 3419, 0), 1, 2);
					return;
				}
				if (id == 91557) { // enter cave - down
					player.useStairs(-1, new WorldTile(4512, 6289, 1), 0, 0);
					return;
				}
				if (id == 91661) { // cross gap
					if (player.getX() >= 4511) {
						player.useStairs(1603, new WorldTile(4506, player.getY(), 1), 1, 1);
						return;
					}
					player.useStairs(1603, new WorldTile(4511, player.getY(), 1), 1, 1);
					return;
				}
				if (id == 91500) { // webbed entrance
					player.getDialogueManager().startDialogue("AraxHyveD");
					return;
				}
				/** End of Araxxor **/
				if (id == 2403) {
					ShopsHandler.openShop(player, player.isKilledCulinaromancer() ? 45
							: player.isKilledDessourt() ? 44
									: player.isKilledKaramel() ? 43
											: player.isKilledFlambeed() ? 42 : player.isKilledAgrithNaNa() ? 41 : 40);
					return;
				}
				if (id == 47231) {
					player.setNextWorldTile(new WorldTile(1685, 5287, 1));
				}
				if (id == 47236) {
					if (player.getX() == 1650 && player.getY() == 5281 || player.getX() == 1651 && player.getY() == 5281
							|| player.getX() == 1650 && player.getY() == 5281) {
						player.addWalkSteps(1651, 5280, 1, false);
					}
					if (player.getX() == 1652 && player.getY() == 5280 || player.getX() == 1651 && player.getY() == 5280
							|| player.getX() == 1653 && player.getY() == 5280) {
						player.addWalkSteps(1651, 5281, 1, false);
					}
					if (player.getX() == 1650 && player.getY() == 5301 || player.getX() == 1650 && player.getY() == 5302
							|| player.getX() == 1650 && player.getY() == 5303) {
						player.addWalkSteps(1649, 5302, 1, false);
					}
					if (player.getX() == 1649 && player.getY() == 5303 || player.getX() == 1649 && player.getY() == 5302
							|| player.getX() == 1649 && player.getY() == 5301) {
						player.addWalkSteps(1650, 5302, 1, false);
					}
					if (player.getX() == 1626 && player.getY() == 5301 || player.getX() == 1626 && player.getY() == 5302
							|| player.getX() == 1626 && player.getY() == 5303) {
						player.addWalkSteps(1625, 5302, 1, false);
					}
					if (player.getX() == 1625 && player.getY() == 5301 || player.getX() == 1625 && player.getY() == 5302
							|| player.getX() == 1625 && player.getY() == 5303) {
						player.addWalkSteps(1626, 5302, 1, false);
					}
					if (player.getX() == 1609 && player.getY() == 5289 || player.getX() == 1610 && player.getY() == 5289
							|| player.getX() == 1611 && player.getY() == 5289) {
						player.addWalkSteps(1610, 5288, 1, false);
					}
					if (player.getX() == 1609 && player.getY() == 5288 || player.getX() == 1610 && player.getY() == 5288
							|| player.getX() == 1611 && player.getY() == 5288) {
						player.addWalkSteps(1610, 5289, 1, false);
					}
					if (player.getX() == 1606 && player.getY() == 5265 || player.getX() == 1605 && player.getY() == 5265
							|| player.getX() == 1604 && player.getY() == 5265) {
						player.addWalkSteps(1605, 5264, 1, false);
					}
					if (player.getX() == 1606 && player.getY() == 5264 || player.getX() == 1605 && player.getY() == 5264
							|| player.getX() == 1604 && player.getY() == 5264) {
						player.addWalkSteps(1605, 5265, 1, false);
					}
					if (player.getX() == 1634 && player.getY() == 5254 || player.getX() == 1634 && player.getY() == 5253
							|| player.getX() == 1634 && player.getY() == 5252) {
						player.addWalkSteps(1635, 5253, 1, false);
					}
					if (player.getX() == 1635 && player.getY() == 5254 || player.getX() == 1635 && player.getY() == 5253
							|| player.getX() == 1635 && player.getY() == 5252) {
						player.addWalkSteps(1634, 5253, 1, false);
					}
					return;
				}
				if (id == 47233) {
					if (player.getSkills().getLevel(Skills.AGILITY) < 80) {
						player.sendMessage("You need 80 agility to use this shortcut.");
						return;
					}
					if (player.getX() == 1633 && player.getY() == 5294) {
						return;
					}
					player.lock(3);
					player.setNextAnimation(new Animation(4853));
					final WorldTile toTile = new WorldTile(object.getX(), object.getY() + 1, object.getPlane());
					player.setNextForceMovement(new ForceMovement(player, 0, toTile, 2, ForceMovement.EAST));
					WorldTasksManager.schedule(new WorldTask() {
						@Override
						public void run() {
							player.setNextWorldTile(toTile);
						}
					}, 1);
				}
				if (id == 29958) {
					if (player.getSkills().getLevel(23) < player.getSkills().getLevelForXp(23)) {
						player.lock(5);
						player.sendMessage("You feel the obelisk", true);
						player.setNextAnimation(new Animation(8502));
						player.setNextGraphics(new Graphics(1308));
						WorldTasksManager.schedule(new WorldTask() {

							@Override
							public void run() {
								player.getSkills().restoreSummoning();
								player.sendMessage("...and recharge all your skills.", true);
							}
						}, 2);
					}
					return;
				} else if (id == 2350 && (object.getX() == 3352 && object.getY() == 3417 && object.getPlane() == 0))
					player.useStairs(832, new WorldTile(3177, 5731, 0), 1, 2);
				else if (id == 2353 && (object.getX() == 3177 && object.getY() == 5730 && object.getPlane() == 0))
					player.useStairs(828, new WorldTile(3353, 3416, 0), 1, 2);
				else if (id == 11554 || id == 11552)
					player.sendMessage("That rock is currently unavailable.");
				else if (id == 38279)
					player.getDialogueManager().startDialogue("RunespanPortalD");
				else if (id == 2491)
					player.getActionManager()
							.setAction(new EssenceMining(object,
									player.getSkills().getLevel(Skills.MINING) < 30 ? EssenceDefinitions.Rune_Essence
											: EssenceDefinitions.Pure_Essence));
				else if (id == 2481)
					RuneCrafting.craftEssence(player, 557, 9, 6.5, false, 26, 2, 52, 3, 78, 4);
				else if (id == 2478)
					RuneCrafting.craftEssence(player, 556, 1, 5, false, 11, 2, 22, 3, 34, 4, 44, 5, 55, 6, 66, 7, 77, 8,
							88, 9, 99, 10);
				else if (id == 2479)
					RuneCrafting.craftEssence(player, 558, 2, 5.5, false, 14, 2, 28, 3, 42, 4, 56, 5, 70, 6, 84, 7, 98,
							8);
				else if (id == 2480)
					RuneCrafting.craftEssence(player, 555, 5, 6, false, 19, 2, 38, 3, 57, 4, 76, 5, 95, 6);
				else if (id == 2482)
					RuneCrafting.craftEssence(player, 554, 14, 7, false, 35, 2, 70, 3);
				else if (id == 2483)
					RuneCrafting.craftEssence(player, 559, 20, 7.5, false, 46, 2, 92, 3);
				else if (id == 2484)
					RuneCrafting.craftEssence(player, 564, 27, 8, true, 59, 2);
				else if (id == 2487)
					RuneCrafting.craftEssence(player, 562, 35, 8.5, true, 74, 2);
				else if (id == 17010)
					RuneCrafting.craftEssence(player, 9075, 40, 8.7, true, 82, 2);
				else if (id == 2486)
					RuneCrafting.craftEssence(player, 561, 45, 9, true, 91, 2);
				else if (id == 2485)
					RuneCrafting.craftEssence(player, 563, 50, 9.5, true);
				else if (id == 2488)
					RuneCrafting.craftEssence(player, 560, 65, 10, true);
				else if (id == 30624)
					RuneCrafting.craftEssence(player, 565, 77, 10.5, true);
				else if (id == 2452) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.AIR_TIARA || hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1438, 1))
						RuneCrafting.enterAirAltar(player);
				} else if (id == 2455) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.EARTH_TIARA || hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1440, 1))
						RuneCrafting.enterEarthAltar(player);
				} else if (id == 2456) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.FIRE_TIARA || hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1442, 1))
						RuneCrafting.enterFireAltar(player);
				} else if (id == 2454) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.WATER_TIARA || hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1444, 1))
						RuneCrafting.enterWaterAltar(player);
				} else if (id == 2457) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.BODY_TIARA || hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1446, 1))
						RuneCrafting.enterBodyAltar(player);
				} else if (id == 2453) {
					int hatId = player.getEquipment().getHatId();
					if (hatId == RuneCrafting.MIND_TIARA || hatId == RuneCrafting.OMNI_TIARA
							|| player.getInventory().containsItem(1448, 1))
						RuneCrafting.enterMindAltar(player);
				} else if (id == 47120) { // zaros altar
					// recharge if needed
					if (player.getPrayer().getPrayerpoints() < player.getSkills().getLevelForXp(Skills.PRAYER) * 10) {
						player.lock(12);
						player.setNextAnimation(new Animation(12563));
						player.getPrayer()
								.setPrayerpoints((int) ((player.getSkills().getLevelForXp(Skills.PRAYER) * 10) * 1.15));
						player.getPrayer().refreshPrayerPoints();
					}
					player.getDialogueManager().startDialogue("ZarosAltar");
				} else if (id == 19222) {
					if (player.getY() >= 3621)
						Falconry.beginFalconry(player);
				} else if (id == 36786)
					player.getDialogueManager().startDialogue("Banker", 4907);
				else if (id == 42377 || id == 42378)
					player.getDialogueManager().startDialogue("Banker", 2759);
				else if (id == 42217 || id == 782 || id == 34752)
					player.getDialogueManager().startDialogue("Banker", 553);
				else if (id == 57437)
					player.getBank().openPlayerBank();
				else if (id == 42425 && object.getX() == 3220 && object.getY() == 3222) { // zaros
					// portal
					player.useStairs(10256, new WorldTile(3353, 3416, 0), 4, 5,
							"And you find yourself into a digsite.");
					player.addWalkSteps(3222, 3223, -1, false);
					player.sendMessage("You examine portal and it aborves you...");
				} else if (id == 9356) {
					player.lock(3);
					FightCaves.enterFightCaves(player);
					return;
				}
				if (id == 68107)
					FightKiln.enterFightKiln(player, false);
				else if (id == 68223)
					FightPits.enterLobby(player, false);
				else if (id == 46500 && object.getX() == 3351 && object.getY() == 3415) { // zaros
					// portal
					player.useStairs(-1, new WorldTile(player.getHomeTile().getX(), player.getHomeTile().getY(),
							player.getHomeTile().getPlane()), 2, 3, "You found your way back to home.");
					player.addWalkSteps(3351, 3415, -1, false);
					/**
					 * Taverley Dungeon.
					 */
				} else if (id == 74866) {
					player.useStairs(828, new WorldTile(2842, 3423, 0), 1, 1);
				} else if (id == 74867) {
					player.useStairs(828, new WorldTile(2842, 9825, 0), 1, 1);
				} else if (id == 77098) {
					int x = player.getX() <= 2897 ? 2899 : 2897;
					player.setNextWorldTile(new WorldTile(x, player.getY(), 0));
				} else if (id == 9293) {
					if (player.getSkills().getLevel(Skills.AGILITY) < 70) {
						player.sendMessage("You need an agility level of 70 to use this obstacle.", true);
						return;
					}
					final int x = player.getX() == 2886 ? 2892 : 2886;
					player.useStairs(844, new WorldTile(x, 9799, 0), 1, 1,
							"You've successfully squeeze through the pipe.");
				} else if (id == 9294) {
					if (player.getSkills().getLevel(Skills.AGILITY) < 80) {
						player.sendMessage("You need an agility level of 80 to use this obstacle.", true);
						return;
					}
					if (player.getX() == 2880 && player.getY() == 9814)
						player.useStairs(1603, new WorldTile(2878, 9812, 0), 1, 1,
								"You've successfully jumped over the strange floor.");
					else
						player.useStairs(1603, new WorldTile(2880, 9814, 0), 1, 1,
								"You've successfully jumped over the strange floor.");
				} else if (id >= 15477 && id <= 15482)
					player.getDialogueManager().startDialogue("HousePortal");
				else if (id == 74864)
					player.useStairs(-1, new WorldTile(2885, 3395, 0), 0, 0);
				else if (id == 66991)
					player.useStairs(-1, new WorldTile(2885, 9795, 0), 0, 0);
				else if (id == 29370 && (object.getX() == 3150 || object.getX() == 3153) && object.getY() == 9906) { // edgeville
					// dung
					// cut
					if (player.getSkills().getLevel(Skills.AGILITY) < 53) {
						player.sendMessage("You need an agility level of 53 to use this obstacle.");
						return;
					}
					final boolean running = player.getRun();
					player.setRunHidden(false);
					player.lock(8);
					player.addWalkSteps(x == 3150 ? 3155 : 3149, 9906, -1, false);
					player.sendMessage("You pulled yourself through the pipes.", true);
					WorldTasksManager.schedule(new WorldTask() {
						boolean secondloop;

						@Override
						public void run() {
							if (!secondloop) {
								secondloop = true;
								player.getGlobalPlayerUpdater().setRenderEmote(295);
							} else {
								player.getGlobalPlayerUpdater().setRenderEmote(-1);
								player.setRunHidden(running);
								player.getSkills().addXp(Skills.AGILITY, 7);
								stop();
							}
						}
					}, 0, 5);
				}
				// start forinthry dungeon
				else if (id == 18341 && object.getX() == 3036 && object.getY() == 10172)
					player.useStairs(-1, new WorldTile(3039, 3765, 0), 0, 1);
				else if (id == 20599 && object.getX() == 3038 && object.getY() == 3761)
					player.useStairs(-1, new WorldTile(3037, 10171, 0), 0, 1);
				else if (id == 18342 && object.getX() == 3075 && object.getY() == 10057)
					player.useStairs(-1, new WorldTile(3071, 3649, 0), 0, 1);
				else if (id == 20600 && object.getX() == 3072 && object.getY() == 3648)
					player.useStairs(-1, new WorldTile(3077, 10058, 0), 0, 1);
				else if (id == 8689)
					player.getActionManager().setAction(new CowMilkingAction());
				else if (id == 42220)
					player.useStairs(-1, new WorldTile(3082, 3475, 0), 0, 1);
				// start falador mininig
				else if (id == 30942 && object.getX() == 3019 && object.getY() == 3450)
					player.useStairs(828, new WorldTile(3020, 9850, 0), 1, 2);
				else if (id == 6226 && object.getX() == 3019 && object.getY() == 9850)
					player.useStairs(833, new WorldTile(3018, 3450, 0), 1, 2);
				else if (id == 31002 && player.getQuestManager().completedQuest(Quests.PERIL_OF_ICE_MONTAINS))
					player.useStairs(833, new WorldTile(2998, 3452, 0), 1, 2);
				else if (id == 31012 && player.getQuestManager().completedQuest(Quests.PERIL_OF_ICE_MONTAINS))
					player.useStairs(828, new WorldTile(2996, 9845, 0), 1, 2);
				else if (id == 30943 && object.getX() == 3059 && object.getY() == 9776)
					player.useStairs(-1, new WorldTile(3061, 3376, 0), 0, 1);
				else if (id == 30944 && object.getX() == 3059 && object.getY() == 3376)
					player.useStairs(-1, new WorldTile(3058, 9776, 0), 0, 1);
				else if (id == 2112 && object.getX() == 3046 && object.getY() == 9756) {
					if (player.getSkills().getLevelForXp(Skills.MINING) < 60) {
						player.getDialogueManager().startDialogue("SimpleNPCMessage",
								MiningGuildDwarf.getClosestDwarfID(player),
								"Sorry, but you need level 60 Mining to go in there.");
						return;
					}
					handleDoor(player, object);
				} else if (id == 2113) {
					if (player.getSkills().getLevelForXp(Skills.MINING) < 60) {
						player.getDialogueManager().startDialogue("SimpleNPCMessage",
								MiningGuildDwarf.getClosestDwarfID(player),
								"Sorry, but you need level 60 Mining to go in there.");
						return;
					}
					player.useStairs(-1, new WorldTile(3021, 9739, 0), 0, 1);
				} else if (id == 6226 && object.getX() == 3019 && object.getY() == 9740)
					player.useStairs(828, new WorldTile(3019, 3341, 0), 1, 2);
				else if (id == 6226 && object.getX() == 3019 && object.getY() == 9738)
					player.useStairs(828, new WorldTile(3019, 3337, 0), 1, 2);
				else if (id == 6226 && object.getX() == 3018 && object.getY() == 9739)
					player.useStairs(828, new WorldTile(3017, 3339, 0), 1, 2);
				else if (id == 6226 && object.getX() == 3020 && object.getY() == 9739)
					player.useStairs(828, new WorldTile(3021, 3339, 0), 1, 2);
				else if (id == 30963)
					player.getBank().openPlayerBank();
				else if (id == 6045)
					player.sendMessage("You search the cart but find nothing.");
				else if (id == 5906) {
					if (player.getSkills().getLevel(Skills.AGILITY) < 42) {
						player.sendMessage("You need an agility level of 42 to use this obstacle.");
						return;
					}
					player.lock();
					WorldTasksManager.schedule(new WorldTask() {
						int count = 0;

						@Override
						public void run() {
							if (count == 0) {
								player.setNextAnimation(new Animation(2594));
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -2 : +2),
										object.getY(), 0);
								player.setNextForceMovement(new ForceMovement(tile, 4, Utils
										.getMoveDirection(tile.getX() - player.getX(), tile.getY() - player.getY())));
							} else if (count == 2) {
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -2 : +2),
										object.getY(), 0);
								player.setNextWorldTile(tile);
							} else if (count == 5) {
								player.setNextAnimation(new Animation(2590));
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -5 : +5),
										object.getY(), 0);
								player.setNextForceMovement(new ForceMovement(tile, 4, Utils
										.getMoveDirection(tile.getX() - player.getX(), tile.getY() - player.getY())));
							} else if (count == 7) {
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -5 : +5),
										object.getY(), 0);
								player.setNextWorldTile(tile);
							} else if (count == 10) {
								player.setNextAnimation(new Animation(2595));
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -6 : +6),
										object.getY(), 0);
								player.setNextForceMovement(new ForceMovement(tile, 4, Utils
										.getMoveDirection(tile.getX() - player.getX(), tile.getY() - player.getY())));
							} else if (count == 12) {
								WorldTile tile = new WorldTile(object.getX() + (object.getRotation() == 2 ? -6 : +6),
										object.getY(), 0);
								player.setNextWorldTile(tile);
							} else if (count == 14) {
								stop();
								player.unlock();
							}
							count++;
						}

					}, 0, 0);
					// BarbarianOutpostAgility start
				} else if (id == 20210)
					BarbarianOutpostAgility.enterObstaclePipe(player, object);
				else if (id == 43526)
					BarbarianOutpostAgility.swingOnRopeSwing(player, object);
				else if (id == 43595 && x == 2550 && y == 3546)
					BarbarianOutpostAgility.walkAcrossLogBalance(player, object);
				else if (id == 20211 && x == 2538 && y == 3545)
					BarbarianOutpostAgility.climbObstacleNet(player, object);
				else if (id == 2302 && x == 2535 && y == 3547)
					BarbarianOutpostAgility.walkAcrossBalancingLedge(player, object);
				else if (id == 1948)
					BarbarianOutpostAgility.climbOverCrumblingWall(player, object);
				else if (id == 43533)
					BarbarianOutpostAgility.runUpWall(player, object);
				else if (id == 43597)
					BarbarianOutpostAgility.climbUpWall(player, object);
				else if (id == 43587)
					BarbarianOutpostAgility.fireSpringDevice(player, object);
				else if (id == 43527)
					BarbarianOutpostAgility.crossBalanceBeam(player, object);
				else if (id == 43531)
					BarbarianOutpostAgility.jumpOverGap(player, object);
				else if (id == 43532)
					BarbarianOutpostAgility.slideDownRoof(player, object);

				// rock living caverns
				else if (id == 45077) {
					player.lock();
					if (player.getX() != object.getX() || player.getY() != object.getY())
						player.addWalkSteps(object.getX(), object.getY(), -1, false);
					WorldTasksManager.schedule(new WorldTask() {

						private int count;

						@Override
						public void run() {
							if (count == 0) {
								player.setNextFaceWorldTile(new WorldTile(object.getX() - 1, object.getY(), 0));
								player.setNextAnimation(new Animation(12216));
								player.unlock();
							} else if (count == 2) {
								player.setNextWorldTile(new WorldTile(3651, 5122, 0));
								player.setNextFaceWorldTile(new WorldTile(3651, 5121, 0));
								player.setNextAnimation(new Animation(12217));
							} else if (count == 5) {
								player.unlock();
								stop();
							}
							count++;
						}

					}, 1, 0);
				} else if (id == 45076)
					player.getActionManager().setAction(new Mining(object, RockDefinitions.LRC_Gold_Ore));
				else if (id == 5999)
					player.getActionManager().setAction(new Mining(object, RockDefinitions.LRC_Coal_Ore));
				else if (id == 11194 || id == 11195 || id == 11364)
					player.getActionManager().setAction(new Mining(object, RockDefinitions.GEM_ROCK));
				else if (id == 2330)
					player.getActionManager().setAction(new Mining(object, RockDefinitions.Red_Sandstone));
				else if (id == 2561)
					player.getActionManager().setAction(new Mining(object, RockDefinitions.Donor_Ore));
				else if (id == 45078)
					player.useStairs(2413, new WorldTile(3012, 9832, 0), 2, 2);
				else if (id == 45079)
					player.getBank().openDepositBox();
				// champion guild
				else if (id == 24357 && object.getX() == 3188 && object.getY() == 3355)
					player.useStairs(-1, new WorldTile(3189, 3354, 1), 0, 1);
				else if (id == 24359 && object.getX() == 3188 && object.getY() == 3355)
					player.useStairs(-1, new WorldTile(3189, 3358, 0), 0, 1);
				else if (id == 1805 && object.getX() == 3191 && object.getY() == 3363)
					handleDoor(player, object);
				// start of varrock dungeon
				else if (id == 29355 && object.getX() == 3230 && object.getY() == 9904) // varrock
					// dung
					player.useStairs(828, new WorldTile(3229, 3503, 0), 1, 2);
				else if (id == 24264)
					player.useStairs(833, new WorldTile(3229, 9904, 0), 1, 2);
				else if (id == 24366)
					player.useStairs(828, new WorldTile(3237, 3459, 0), 1, 2);
				else if (id == 882 && object.getX() == 3237 && object.getY() == 3458)
					player.useStairs(833, new WorldTile(3237, 9858, 0), 1, 2);
				else if (id == 29355 && object.getX() == 3097 && object.getY() == 9868) // edge
					// dungeon
					player.useStairs(828, new WorldTile(3096, 3468, 0), 1, 2);
				else if (id == 26934)
					player.useStairs(833, new WorldTile(3097, 9868, 0), 1, 2);
				else if (id == 29355 && object.getX() == 3088 && object.getY() == 9971)
					player.useStairs(828, new WorldTile(3087, 3571, 0), 1, 2);
				else if (id == 65453)
					player.useStairs(833, new WorldTile(3089, 9971, 0), 1, 2);
				else if (id == 12389 && object.getX() == 3116 && object.getY() == 3452)
					player.useStairs(833, new WorldTile(3117, 9852, 0), 1, 2);
				else if (id == 29355 && object.getX() == 3116 && object.getY() == 9852)
					player.useStairs(833, new WorldTile(3115, 3452, 0), 1, 2);
				else if (id == 69514)
					GnomeAgility.RunGnomeBoard(player, object);
				else if (id == 69389)
					GnomeAgility.JumpDown(player, object);
				else if (id == 69526)
					GnomeAgility.walkGnomeLog(player);
				else if (id == 69383)
					GnomeAgility.climbGnomeObstacleNet(player);
				else if (id == 69506)
					GnomeAgility.climbUpTree(player);
				else if (id == 69508)
					GnomeAgility.climbUpGnomeTreeBranch(player);
				else if (id == 2312)
					GnomeAgility.walkGnomeRope(player);
				else if (id == 4059)
					GnomeAgility.walkBackGnomeRope(player);
				else if (id == 69507)
					GnomeAgility.climbDownGnomeTreeBranch(player);
				else if (id == 69384)
					GnomeAgility.climbGnomeObstacleNet2(player);
				else if (id == 69377 || id == 69378)
					GnomeAgility.enterGnomePipe(player, object.getX(), object.getY());
				else if (id == 65365)
					WildernessCourseAgility.walkGate(player, object);
				else if (id == 65367)
					WildernessCourseAgility.walkBackGate(player, object);
				else if (id == 65362)
					WildernessCourseAgility.enterObstaclePipe(player, object);
				else if (id == 65734)
					WildernessCourseAgility.climbCliff(player, object);
				else if (id == 64696)
					WildernessCourseAgility.swingOnRopeSwing(player, object);
				else if (id == 64699)
					WildernessCourseAgility.steppingStone(player, object);
				else if (id == 64698)
					WildernessCourseAgility.walkAcrossLogBalance(player);
				else if (Wilderness.isDitch(id)) {// wild ditch
					player.getDialogueManager().startDialogue("WildernessDitch", object);
				} else if (id == 42611) {// Magic Portal
					player.getDialogueManager().startDialogue("MagicPortal");
				} else if (id == 27254) {// Edgeville portal
					player.sendMessage("You enter the portal...");
					player.useStairs(10584, new WorldTile(3087, 3488, 0), 2, 3, "..and are transported to Edgeville.");
					player.addWalkSteps(1598, 4506, -1, false);
				} else if (id == 12202) {// mole entrance
					if (!player.getInventory().containsItem(952, 1)) {
						player.sendMessage("You need a spade to dig this.");
						return;
					}
					if (player.getX() != object.getX() || player.getY() != object.getY()) {
						player.lock();
						player.addWalkSteps(object.getX(), object.getY());
						WorldTasksManager.schedule(new WorldTask() {
							@Override
							public void run() {
								InventoryOptionsHandler.dig(player);
							}

						}, 1);
					} else
						InventoryOptionsHandler.dig(player);
				} else if (id == 12230 && object.getX() == 1752 && object.getY() == 5136) {// mole
					// exit
					player.setNextWorldTile(new WorldTile(2986, 3316, 0));
				} else if (id == 15522) {// portal sign
					if (player.withinDistance(new WorldTile(1598, 4504, 0), 1)) {// PORTAL
						// 1
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13, "Edgeville");
						player.getPackets().sendIComponentText(327, 14, "This portal will take you to edgeville. There "
								+ "you can multi pk once past the wilderness ditch.");
					}
					if (player.withinDistance(new WorldTile(1598, 4508, 0), 1)) {// PORTAL
						// 2
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13, "Mage Bank");
						player.getPackets().sendIComponentText(327, 14, "This portal will take you to the mage bank. "
								+ "The mage bank is a 1v1 deep wilderness area.");
					}
					if (player.withinDistance(new WorldTile(1598, 4513, 0), 1)) {// PORTAL
						// 3
						player.getInterfaceManager().sendInterface(327);
						player.getPackets().sendIComponentText(327, 13, "Magic's Portal");
						player.getPackets().sendIComponentText(327, 14,
								"This portal will allow you to teleport to areas that "
										+ "will allow you to change your magic spell book.");
					}
				} else if (id == 38811 || id == 37929) {// corp beast
					if (object.getX() == 2971 && object.getY() == 4382)
						player.getInterfaceManager().sendInterface(650);
					else if (object.getX() == 2918 && object.getY() == 4382) {
						player.stopAll();
						player.setNextWorldTile(
								new WorldTile(player.getX() == 2921 ? 2917 : 2921, player.getY(), player.getPlane()));
					}

				} // kalphite king
				else if (id == 82014) {
					player.lock();
					player.setNextAnimation(new Animation(19498));
					WorldTasksManager.schedule(new WorldTask() { // to remove at
						// same time
						// it
						// teleports
						@Override
						public void run() {
							player.setNextAnimation(new Animation(-1));
							player.setNextWorldTile(new WorldTile(2973, 1746, 0));
							player.unlock();
							stop();
						}
					}, 3);
					return;
				} else if (id == 37928 && object.getX() == 2883 && object.getY() == 4370) {
					player.stopAll();
					player.setNextWorldTile(new WorldTile(3214, 3782, 0));
					player.getControlerManager().startControler("Wilderness");
				} else if (id == 38815 && object.getX() == 3209 && object.getY() == 3780 && object.getPlane() == 0) {
					if (player.getSkills().getLevelForXp(Skills.WOODCUTTING) < 37
							|| player.getSkills().getLevelForXp(Skills.MINING) < 45
							|| player.getSkills().getLevelForXp(Skills.SUMMONING) < 23
							|| player.getSkills().getLevelForXp(Skills.FIREMAKING) < 47
							|| player.getSkills().getLevelForXp(Skills.PRAYER) < 55) {
						player.sendMessage(
								"You need 23 Summoning, 37 Woodcutting, 45 Mining, 47 Firemaking and 55 Prayer to enter this dungeon.");
						return;
					}
					player.stopAll();
					player.setNextWorldTile(new WorldTile(2885, 4372, 0));
					player.getControlerManager().forceStop();
				} else if (id == 9369) {
					player.getControlerManager().startControler("FightPits");

				} else if (id == 2079) {
					CrystalChest.openChest(object, player);
					// Easy Clue
					if (player.getInventory().containsItem(2677, 1)) {
						if (!player.getPerkManager().investigator) {
							if (!player.getInventory().containsItem(995, 2500000)) {
								player.sm(Colors.red + "You need 2 500 000 coins in you inventory");
								return;
							}
							if (player.getInventory().containsItem(995, 2500000)) {
								player.getTreasureTrails().openReward(0);
								player.getInventory().deleteItem(995, 2500000);
								player.getInventory().deleteItem(2677, 1);
							}
						}
						if (player.getPerkManager().investigator) {
							player.getTreasureTrails().openReward(0);
							player.getInventory().deleteItem(2677, 1);
						}
					}
					// Medium Clue
					if (player.getInventory().containsItem(2801, 1)) {
						if (!player.getPerkManager().investigator) {
							if (!player.getInventory().containsItem(995, 5000000)) {
								player.sm(Colors.red + "You need 5 000 000 coins in you inventory");
								return;
							}
							if (player.getInventory().containsItem(995, 5000000)) {
								player.getTreasureTrails().openReward(1);
								player.getInventory().deleteItem(995, 5000000);
								player.getInventory().deleteItem(2801, 1);
							}
						}
						if (player.getPerkManager().investigator) {
							player.getTreasureTrails().openReward(1);
							player.getInventory().deleteItem(2801, 1);
						}
					}
					// Hard Clue
					if (player.getInventory().containsItem(2722, 1)) {
						if (!player.getPerkManager().investigator) {
							if (!player.getInventory().containsItem(995, 10000000)) {
								player.sm(Colors.red + "You need 10 000 000 coins in you inventory");
								return;
							}
							if (player.getInventory().containsItem(995, 10000000)) {
								player.getTreasureTrails().openReward(2);
								player.getInventory().deleteItem(995, 10000000);
								player.getInventory().deleteItem(2722, 1);
							}
						}
						if (player.getPerkManager().investigator) {
							player.getTreasureTrails().openReward(2);
							player.getInventory().deleteItem(2722, 1);
						}
					}
					// Elite Clue
					if (player.getInventory().containsItem(19043, 1)) {
						if (!player.getPerkManager().investigator) {
							if (!player.getInventory().containsItem(995, 20000000)) {
								player.sm(Colors.red + "You need 20 000 000 coins in you inventory");
								return;
							}
							if (player.getInventory().containsItem(995, 20000000)) {
								player.getTreasureTrails().openReward(3);
								player.getInventory().deleteItem(995, 20000000);
								player.getInventory().deleteItem(19043, 1);
							}
						}
						if (player.getPerkManager().investigator) {
							player.getTreasureTrails().openReward(3);
							player.getInventory().deleteItem(19043, 1);
						}
					}

				} else if (id == 97431) {
					RaptorChest.openChest(object, player);
				} else if (id == 20600) {
					player.setNextWorldTile(new WorldTile(3077, 10058, 0));
				} else if (id == 18342) {
					player.setNextWorldTile(new WorldTile(3071, 3649, 0));
				} else if (id == 52859) {
					if (player.getSkills().getLevel(Skills.DUNGEONEERING) < 85) {
						player.getPackets()
								.sendGameMessage("You need a level of 85 Dungeoneering to enter that dungeon");
						return;
					}
					Magic.sendNormalTeleportSpell(player, 0, 0.0D, new WorldTile(1297, 4510, 0), new int[0]);
				} else if (id == 2475) {
					Magic.sendNormalTeleportSpell(player, 0, 0.0D, new WorldTile(3186, 5725, 0), new int[0]);
					player.getControlerManager().startControler("FunPk");
				} else if (id == 4493) {
					player.setNextWorldTile(new WorldTile(3433, 3538, 1));
				} else if (id == 4494) {
					player.setNextWorldTile(new WorldTile(3438, 3538, 0));
				} else if (id == 4496) {
					player.setNextWorldTile(new WorldTile(3412, 3541, 1));
				} else if (id == 4495) {
					player.setNextWorldTile(new WorldTile(3417, 3541, 2));
				} else if (id == 9319) {
					player.setNextAnimation(new Animation(828));
					if (object.getX() == 3447 && object.getY() == 3576 && object.getPlane() == 1) {
						player.setNextWorldTile(new WorldTile(3446, 3576, 2));
					}
					if (object.getX() == 3422 && object.getY() == 3550 && object.getPlane() == 0) {
						player.setNextWorldTile(new WorldTile(3422, 3551, 1));
					}
					player.stopAll();
				} else if (id == 9320) {
					player.setNextAnimation(new Animation(828));
					if (object.getX() == 3447 && object.getY() == 3576 && object.getPlane() == 2) {
						player.setNextWorldTile(new WorldTile(3446, 3576, 1));
					}
					if (object.getX() == 3422 && object.getY() == 3550 && object.getPlane() == 1) {
						player.setNextWorldTile(new WorldTile(3422, 3551, 0));
					}
					player.stopAll();
				} else if (id == 52875) {
					Magic.sendNormalTeleportSpell(player, 0, 0.0D, new WorldTile(3033, 9598, 0), new int[0]);
				} else if (id == 1817 && object.getX() == 2273 && object.getY() == 4680) { // kbd
					// lever
					Magic.pushLeverTeleport(player, new WorldTile(3067, 10254, 0));
				} else if (id == 1816 && object.getX() == 3067 && object.getY() == 10252) { // kbd
					// out
					// lever
					Magic.pushLeverTeleport(player, new WorldTile(2273, 4681, 0));
					player.getControlerManager().forceStop();
				} else if (id == 32015 && object.getX() == 3069 && object.getY() == 10256) { // kbd
					// stairs
					player.useStairs(828, new WorldTile(3017, 3848, 0), 1, 2);
					player.getControlerManager().startControler("Wilderness");
				} else if (id == 1765 && object.getX() == 3017 && object.getY() == 3849) { // kbd
					// out
					// stairs
					player.stopAll();
					player.setNextWorldTile(new WorldTile(3069, 10255, 0));
				} else if (id == 14315) {
					if (Lander.canEnter(player, 0))
						return;
				} else if (id == 25631) {
					if (Lander.canEnter(player, 1))
						return;
				} else if (id == 25632) {
					if (Lander.canEnter(player, 2))
						return;
				} else if (id == 5959) {
					if (player.getX() == 3089 || player.getX() == 3090)
						Magic.pushLeverTeleport(player, new WorldTile(2539, 4712, 0));
				} else if (id == 5960) {
					Magic.pushLeverTeleport(player, new WorldTile(3089, 3957, 0));
				} else if (id == 1814) {
					Magic.pushLeverTeleport(player, new WorldTile(3155, 3923, 0));
				} else if (id == 1815) {
					Magic.pushLeverTeleport(player, new WorldTile(2561, 3311, 0));
				} else if (id == 62675)
					player.getCutscenesManager().play("DTPreview");
				else if (id == 62681)
					player.getDominionTower().viewScoreBoard();
				else if (id == 62678 || id == 62679)
					// player.getDominionTower().openModes();
					/**
					 * Since only 1 mode working we make it open endurance straight away
					 **/
					player.getDominionTower().openEnduranceMode();
				else if (id == 62688)
					player.getDialogueManager().startDialogue("DTClaimRewards");
				else if (id == 62680)
					player.getDominionTower().openBankChest();

				else if (id == 48797)
					player.getControlerManager().startControler("Dungeoneering");
				else if (id == 48683)
					player.useStairs(-1, new WorldTile(3450, 3726, 0), 0, 1);

				else if (id == 62676) { // dominion exit
					player.useStairs(-1, new WorldTile(3374, 3093, 0), 0, 1);
				} else if (id == 62674) { // dominion entrance
					player.useStairs(-1, new WorldTile(3744, 6405, 0), 0, 1);
				} else if (id == 3192) {
					PkRank.showRanks(player);
				} else if (id == 65349) {
					player.useStairs(-1, new WorldTile(3044, 10325, 0), 0, 1);
				} else if (id == 32048 && object.getX() == 3043 && object.getY() == 10328) {
					player.useStairs(-1, new WorldTile(3045, 3927, 0), 0, 1);
				} else if (id == 26194) {
					player.getDialogueManager().startDialogue("PartyRoomLever");
				} else if (id == 61190 || id == 61191 || id == 61192 || id == 61193) {
					if (objectDef.containsOption(0, "Chop down"))
						player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.NORMAL));

				} else if (id == 12290)
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.JADE_ROOT_HEALTHY));
				else if (id == 12291)
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.JADE_ROOT_MUTATED));
				else if (id == 70001)
					player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.BLOOD));
				else if (id == 9036) {
					if (ThroneOfMiscellania.isInArea(player))
						player.getActionManager().setAction(new MiscellaniaWoodcutting(object,
								com.rs.game.player.actions.miscellania.MiscellaniaWoodcutting.TreeDefinitions.TEAK));
					else
						player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.TEAK));
				} else if (id == 70076) {
					if (ThroneOfMiscellania.isInArea(player))
						player.getActionManager().setAction(new MiscellaniaWoodcutting(object,
								com.rs.game.player.actions.miscellania.MiscellaniaWoodcutting.TreeDefinitions.MAHOGANY));
					else
						player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.MAHOGANY));
				} else if (id == 20573)
					player.getControlerManager().startControler("RefugeOfFear");
				else if (id == 3044)
					player.getDialogueManager().startDialogue("SmeltingD", object);
				// crucible
				else if (id == 67050) {
					player.useStairs(-1, new WorldTile(3359, 6110, 0), 0, 1);
				} else if (id == 67053)
					player.useStairs(-1, new WorldTile(3120, 3519, 0), 0, 1);
				else if (id == 67051)
					player.getDialogueManager().startDialogue("Marv", false);
				else if (id == 67052)
					Crucible.enterCrucibleEntrance(player);
				else {
					switch (objectDef.name.toLowerCase()) {
					case "obelisk":
						if (player.getRegionId() == 11573) {
							Summoning.sendInterface(player);
							player.setNextFaceWorldTile(new WorldTile(2933, 3448, 0));
						}
						break;
					case "banana tree":
						if (player.getInventory().addItem(1963, 1)) {
							player.setNextAnimation(new Animation(827));
							player.lock(2);
						}
						break;
					case "potter's wheel":
						player.getDialogueManager().startDialogue("PotteryWheel");
						break;
					case "pottery oven":
						player.getDialogueManager().startDialogue("PotteryFurnace");
						break;
					case "fairy ring":
					case "enchanted land":
						FairyRing.openRingInterface(player, object, id == 12128);
						break;
					case "nature rift":
						player.setNextWorldTile(new WorldTile(2398, 4841, 0));
						break;
					case "death rift":
						player.setNextWorldTile(new WorldTile(2205, 4834, 0));
						break;
					case "blood rift":
						player.setNextWorldTile(new WorldTile(2457, 4895, 1));
						break;
					case "law rift":
						player.setNextWorldTile(new WorldTile(2464, 4830, 0));
						break;
					case "chaos rift":
						player.setNextWorldTile(new WorldTile(2271, 4840, 0));
						break;
					case "cosmic rift":
						player.setNextWorldTile(new WorldTile(2142, 4831, 0));
						break;
					case "body rift":
						player.setNextWorldTile(new WorldTile(2522, 4833, 0));
						break;
					case "fire rift":
						player.setNextWorldTile(new WorldTile(2585, 4834, 0));
						break;
					case "earth rift":
						player.setNextWorldTile(new WorldTile(2657, 4830, 0));
						break;
					case "mind rift":
						player.setNextWorldTile(new WorldTile(2793, 4828, 0));
						break;
					case "air rift":
						player.setNextWorldTile(new WorldTile(2841, 4829, 0));
						break;
					case "water rift":
						player.setNextWorldTile(new WorldTile(3494, 4832, 0));
						break;
					case "passageway":
						/*
						 * for(Player p : battleTerrace.waiting) { if(p == player) {
						 * battleTerraceWaiting.leaveGame(player); return; } continue; }
						 * battleTerrace.joinLobby(player);
						 */
						break;
					case "trapdoor":
					case "manhole":
						if (objectDef.containsOption(0, "Open")) {
							WorldObject openedHole = new WorldObject(object.getId() + 1, object.getType(),
									object.getRotation(), object.getX(), object.getY(), object.getPlane());
							player.faceObject(openedHole);
							World.spawnObjectTemporary(openedHole, 60000);
						} else {
							player.sendMessage("It won't budge!");
						}
						break;
					case "closed chest":
						if (objectDef.containsOption(0, "Open")) {
							player.setNextAnimation(new Animation(536));
							player.lock(2);
							WorldObject openedChest = new WorldObject(object.getId() + 1, object.getType(),
									object.getRotation(), object.getX(), object.getY(), object.getPlane());
							player.faceObject(openedChest);
							World.spawnObjectTemporary(openedChest, 60000);
						}
						break;
					case "open chest":
						if (objectDef.containsOption(0, "Search"))
							player.sendMessage("You search the chest but find nothing.");
						break;
					case "spiderweb":
						if (object.getRotation() == 2) {
							player.lock(2);
							if (Utils.getRandom(1) == 0) {
								player.addWalkSteps(player.getX(),
										player.getY() < y ? object.getY() + 2 : object.getY() - 1, -1, false);
								player.sendMessage("You squeeze though the web.");
							} else
								player.getPackets().sendGameMessage(
										"You fail to squeeze though the web; perhaps you should try again.");
						}
						break;
					case "bank deposit box":
						player.getBank().openDepositBox();
						break;
					case "web":
						if (objectDef.containsOption(0, "Slash"))
							if (player.getEquipment().getWeaponId() > 0) {
								player.setNextAnimation(new Animation(
										PlayerCombat.getWeaponAttackEmote(player.getEquipment().getWeaponId(),
												player.getCombatDefinitions().getAttackStyle(), player)));
								slashWeb(player, object);
							} else {
								player.sendMessage("You need something sharp to hit through the web.");
							}
						break;
					case "anvil":
						if (objectDef.containsOption(0, "Smith")) {
							ForgingBar bar = ForgingBar.getBar(player);
							if (bar == ForgingBar.DRACONIC_VISAGE) {
								player.getDialogueManager().startDialogue("DFSSmithingD");
								return;
							}
							if (bar != null)
								ForgingInterface.sendSmithingInterface(player, bar, object);
							else
								player.sendMessage("You have no bars for your smithing level.");
						}
						break;
					case "tin ore rocks":
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Tin_Ore));
						break;
					case "crashed star":
						if (objectDef.containsOption(0, "Mine"))
							ShootingStar.mine(player, object);
						break;
					case "gold ore rocks":
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Gold_Ore));
						break;
					case "iron ore rocks":
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Iron_Ore));
						break;
					case "silver ore rocks":
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Silver_Ore));
						break;
					case "coal rocks":
						if (ThroneOfMiscellania.isInArea(player))
							player.getActionManager().setAction(
									new MiscellaniaMining(object, MiscellaniaMining.RockDefinitions.Coal_Ore));
						else
							player.getActionManager().setAction(new Mining(object, RockDefinitions.Coal_Ore));
						break;
					case "clay rocks":
						player.getActionManager().setAction(new Mining(object, RockDefinitions.SOFT_CLAY));
						break;
					case "copper ore rocks":
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Copper_Ore));
						break;
					case "adamantite ore rocks":
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Adamant_Ore));
						break;
					case "runite ore rocks":
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Runite_Ore));
						break;
					case "granite rocks":
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Granite_Ore));
						break;
					case "sandstone rocks":
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Sandstone_Ore));
						break;
					case "mithril ore rocks":
						player.getActionManager().setAction(new Mining(object, RockDefinitions.Mithril_Ore));
						break;
					case "divine runite rock":
						player.getActionManager()
								.setAction(new DivineMining(object, DivineMining.RockDefinitions.DIVINE_RUNE_ORE));
						break;
					case "divine adamantite rock":
						player.getActionManager().setAction(
								new DivineMining(object, DivineMining.RockDefinitions.DIVINE_ADAMANTITE_ORE));
						break;
					case "divine mithril rock":
						player.getActionManager()
								.setAction(new DivineMining(object, DivineMining.RockDefinitions.DIVINE_MITHRIL_ORE));
						break;
					case "divine coal rock":
						player.getActionManager()
								.setAction(new DivineMining(object, DivineMining.RockDefinitions.DIVINE_COAL_ORE));
						break;
					case "divine iron rock":
						player.getActionManager()
								.setAction(new DivineMining(object, DivineMining.RockDefinitions.DIVINE_IRON_ORE));
						break;
					case "divine bronze rock":
						player.getActionManager()
								.setAction(new DivineMining(object, DivineMining.RockDefinitions.DIVINE_BRONZE_ORE));
						break;
					case "deposit box":
						if (objectDef.containsOption(0, "Deposit"))
							player.getBank().openDepositBox();
						break;
					case "bank":
					case "bank chest":
					case "bank booth":
					case "counter":
						if (objectDef.containsOption(0, "Bank") || objectDef.containsOption(0, "Use"))
							player.getBank().openPlayerBank();
						break;
					// Woodcutting start
					case "tree":
						if (objectDef.containsOption(0, "Chop down")) {
							if (ThroneOfMiscellania.isInArea(player))
								player.getActionManager().setAction(new MiscellaniaWoodcutting(object,
										MiscellaniaWoodcutting.TreeDefinitions.NORMAL));
							else
								player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.NORMAL));
						}
						break;
					case "evergreen":
						if (objectDef.containsOption(0, "Chop down")) {
							if (ThroneOfMiscellania.isInArea(player))
								player.getActionManager().setAction(new MiscellaniaWoodcutting(object,
										MiscellaniaWoodcutting.TreeDefinitions.EVERGREEN));
							else
								player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.EVERGREEN));
						}
						break;
					case "dead tree":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.DEAD));
						break;
					case "oak":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.OAK));
						break;
					case "willow":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.WILLOW));
						break;
					case "maple tree":
						if (objectDef.containsOption(0, "Chop down")) {
							if (ThroneOfMiscellania.isInArea(player))
								player.getActionManager().setAction(new MiscellaniaWoodcutting(object,
										MiscellaniaWoodcutting.TreeDefinitions.MAPLE));
							else
								player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.MAPLE));
						}
						break;
					case "ivy":
						if (objectDef.containsOption(0, "Chop"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.IVY));
						break;
					case "yew":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.YEW));
						break;
					case "magic tree":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.MAGIC));
						break;
					case "dramen tree":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.DONOR_TREE));
						break;
					case "cursed magic tree":
						if (objectDef.containsOption(0, "Chop down"))
							player.getActionManager().setAction(new Woodcutting(object, TreeDefinitions.CURSED_MAGIC));
						break;
					case "divine magic tree":
						if (objectDef.containsOption(0, "Chop"))
							player.getActionManager()
									.setAction(new DivineWoodcutting(object, DivineTreeDefinitions.DIVINE_MAGIC));
						break;
					case "divine tree":
						if (objectDef.containsOption(0, "Chop"))
							player.getActionManager()
									.setAction(new DivineWoodcutting(object, DivineTreeDefinitions.DIVINE_NORMAL));
						break;
					case "divine oak tree":
						if (objectDef.containsOption(0, "Chop"))
							player.getActionManager()
									.setAction(new DivineWoodcutting(object, DivineTreeDefinitions.DIVINE_OAK));
						break;
					case "divine willow tree":
						if (objectDef.containsOption(0, "Chop"))
							player.getActionManager()
									.setAction(new DivineWoodcutting(object, DivineTreeDefinitions.DIVINE_WILLOW));
						break;
					case "divine maple tree":
						if (objectDef.containsOption(0, "Chop"))
							player.getActionManager()
									.setAction(new DivineWoodcutting(object, DivineTreeDefinitions.DIVINE_MAPLE));
						break;
					case "divine yew tree":
						if (objectDef.containsOption(0, "Chop"))
							player.getActionManager()
									.setAction(new DivineWoodcutting(object, DivineTreeDefinitions.DIVINE_YEW));
						break;
					// Woodcutting end
					case "gate":
					case "large door":
					case "metal door":
						if (objectDef.containsOption(0, "Open"))
							if (!handleGate(player, object))
								handleDoor(player, object);
						break;
					case "door":
						if ((objectDef.containsOption(0, "Open") || objectDef.containsOption(0, "Unlock")))
							handleDoor(player, object);
						break;
					case "ladder":
						handleLadder(player, object, 1);
						break;
					case "stairs":
					case "staircase":
						handleStaircases(player, object, 1);
						break;
					case "small obelisk":
						if (objectDef.containsOption(0, "Renew-points")) {
							int summonLevel = player.getSkills().getLevelForXp(Skills.SUMMONING);
							if (player.getSkills().getLevel(Skills.SUMMONING) < summonLevel) {
								player.lock(3);
								player.setNextAnimation(new Animation(8502));
								player.getSkills().set(Skills.SUMMONING, summonLevel);
								player.sendMessage("You've recharged your Summoning points.", true);
							} else
								player.sendMessage("You already have full Summoning points.");
						}
						break;
					case "altar":
						if (objectDef.containsOption(0, "Pray") || objectDef.containsOption(0, "Pray-at")) {
							final int maxPrayer = player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
							if (player.getControlerManager().getControler() != null) {
								player.getCombatDefinitions().restoreSpecialAttack(100);
							}
							if (player.getPrayer().getPrayerpoints() < maxPrayer) {
								player.lock(5);
								player.sendMessage("You pray to the gods...", true);
								player.setNextAnimation(new Animation(645));
								WorldTasksManager.schedule(new WorldTask() {
									@Override
									public void run() {
										player.getPrayer().restorePrayer(maxPrayer);
										player.sendMessage("...and recharged your prayer.", true);
									}
								}, 2);
							} else
								player.sendMessage("You already have full prayer.");
							if (id == 6552)
								player.getDialogueManager().startDialogue("AncientAltar");
						}
						break;
					default:
						break;
					}
				}
				if (Settings.DEBUG)
					Logger.log("ObjectHandler",
							"clicked 1 at object id : " + id + ", " + object.getX() + ", " + object.getY() + ", "
									+ object.getPlane() + ", " + object.getType() + ", " + object.getRotation() + ", "
									+ object.getDefinitions().name);
			}
		}, true));
	}

	public static boolean handleGate(Player player, WorldObject object, long delay) {
		if (World.isSpawnedObject(object))
			return false;
		if (object.getRotation() == 0) {
			boolean south = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() - 1, object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				south = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(),
					otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (south) {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor1.setRotation(3);
				openedDoor2.moveLocation(-1, 0, 0);
			} else {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor2.moveLocation(-1, 0, 0);
				openedDoor2.setRotation(3);
			}

			if (World.removeObjectTemporary(object, delay) && World.removeObjectTemporary(otherDoor, delay)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, delay);
				World.spawnObjectTemporary(openedDoor2, delay);
				return true;
			}
		} else if (object.getRotation() == 2) {

			boolean south = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() - 1, object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				south = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(),
					otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (south) {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor2.setRotation(1);
				openedDoor2.moveLocation(1, 0, 0);
			} else {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor1.setRotation(1);
				openedDoor2.moveLocation(1, 0, 0);
			}
			if (World.removeObjectTemporary(object, delay) && World.removeObjectTemporary(otherDoor, delay)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, delay);
				World.spawnObjectTemporary(openedDoor2, delay);
				return true;
			}
		} else if (object.getRotation() == 3) {

			boolean right = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX() - 1, object.getY(), object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX() + 1, object.getY(), object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				right = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(),
					otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (right) {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor2.setRotation(0);
				openedDoor1.setRotation(2);
				openedDoor2.moveLocation(0, -1, 0);
			} else {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.setRotation(2);
				openedDoor2.moveLocation(0, -1, 0);
			}
			if (World.removeObjectTemporary(object, delay) && World.removeObjectTemporary(otherDoor, delay)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, delay);
				World.spawnObjectTemporary(openedDoor2, delay);
				return true;
			}
		} else if (object.getRotation() == 1) {

			boolean right = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX() - 1, object.getY(), object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX() + 1, object.getY(), object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				right = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(),
					otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (right) {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.moveLocation(0, 1, 0);
			} else {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor2.setRotation(0);
				openedDoor2.moveLocation(0, 1, 0);
			}
			if (World.removeObjectTemporary(object, delay) && World.removeObjectTemporary(otherDoor, delay)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, delay);
				World.spawnObjectTemporary(openedDoor2, delay);
				return true;
			}
		}
		return false;
	}

	private static void handleOption2(final Player player, final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("Option 2:" + object.getId() + "; " + object.getX() + " "
					+ object.getY() + " " + object.getPlane());
		if (player.isLocked())
			return;
		if (LividFarm.HandleLividFarmObjects(player, object, 2))
			return;
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControlerManager().processObjectClick2(object))
					return;
				if (!player.getNewQuestManager().processObjectClick2(object))
					return;
				if (Pickables.handlePickable(player, object))
					return;
				if (PrifddinasCity.handleObjectOption2(player, object))
					return;
				switch (objectDef.name.toLowerCase()) {
				case "obelisk":
					if (objectDef.containsOption(1, "Renew-points")) {
						if (player.getSkills().getLevel(23) < player.getSkills().getLevelForXp(23)) {
							player.lock(5);
							player.sendMessage("You feel the obelisk..", true);
							player.setNextAnimation(new Animation(8502));
							player.setNextGraphics(new Graphics(1308));
							WorldTasksManager.schedule(new WorldTask() {

								@Override
								public void run() {
									player.getSkills().restoreSummoning();
									player.sendMessage("..and recharge your Summoning points.", true);
								}
							}, 2);
						} else
							player.sendMessage("You already have full Summoning points.", true);
						return;
					}
					break;
				}
				if (object.getId() == 91172) {
					if (object.getX() == 2328 && object.getY() == 3176) {
						if (player.withinDistance(new WorldTile(object.getX(), object.getY(), object.getPlane())))
							player.setNextWorldTile(new WorldTile(2327, 3176, 2));
					}
					if (object.getX() == 2325 && object.getY() == 3168) {
						if (player.withinDistance(new WorldTile(object.getX(), object.getY(), object.getPlane())))
							player.setNextWorldTile(new WorldTile(2327, 3168, 2));
					}
				}

				if (object.getDefinitions().getName().toLowerCase().contains("spirit tree")
						|| object.getId() == 26723) {
					SpiritTree.openInterface(player, object.getId() != 68973 && object.getId() != 68974);
					return;
				}
				if (object.getDefinitions().name.equalsIgnoreCase("furnace") || object.getId() == 11666)
					player.getDialogueManager().startDialogue("SmeltingD", object);
				else if (id == 17010)
					player.getDialogueManager().startDialogue("LunarAltar");
				if (object.getDefinitions().name.toLowerCase().contains("spinning")) {
					player.getDialogueManager().startDialogue("SpinningD", false);
					return;
				}
				Animation THIEVING_ANIMATION = new Animation(881);
				if (id == 90271 || id == 100955) {
					player.getBank().openPlayerBank();
					return;
				}
				if (id == 38811) { // corp beast
					player.getCutscenesManager().play("CorporealBeastScene");
					return;
				}
				if (id == 97270) {
					player.getDialogueManager().startDialogue("SmeltingD", object);
					return;
				}
				if (id == 87306) {
					player.getActionManager()
							.setAction(new DivinationConvert(player, new Object[] { ConvertMode.CONVERT_TO_ENERGY }));
					return;
				}
				if (id == 83954) {
					player.getBank().openPlayerBank();
					return;
				}
				if (player.getTreasureTrails().useObject(object))
					return;
				if (id == 70812) {
					player.getControlerManager().startControler("QueenBlackDragonControler");
					return;
				}
				/**
				 * Slayer Tower.
				 */
				if (id == 82667 || id == 82483) { /** Staircases **/
					player.useStairs(-1, new WorldTile(player.getX(), player.getY(), 2), 0, 0);
					return;
				}

				if (id == 51061) {
					final int maxPrayer = player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
					if (player.getPrayer().getPrayerpoints() < maxPrayer) {
						player.setNextAnimation(new Animation(645));
						player.getPrayer().restorePrayer(maxPrayer);
						player.sendMessage("You've recharged your prayer points.");
					} else
						player.sendMessage("You already have full Prayer points.");
					return;
				}
				if (player.getFarmingManager().isFarming(id, null, 2))
					return;

				// Legio boss exit's
				if (id == 84732)
					player.useStairs(-1, new WorldTile(1025, 632, 1), 0, 1);
				if (id == 84733)
					player.useStairs(-1, new WorldTile(1106, 671, 1), 0, 1);
				if (id == 84734)
					player.useStairs(-1, new WorldTile(1099, 665, 1), 0, 1);
				if (id == 84735)
					player.useStairs(-1, new WorldTile(1176, 634, 1), 0, 1);
				if (id == 84736)
					player.useStairs(-1, new WorldTile(1191, 634, 1), 0, 1);
				if (id == 84737)
					player.useStairs(-1, new WorldTile(1183, 622, 1), 0, 1);
				/**
				 * Crafting stall.
				 */
				if (id == 4874) {
					Item[] items = { new Item(1739), new Item(1737), new Item(1779), new Item(6287), new Item(1635),
							new Item(1734), new Item(1623), new Item(1625), new Item(1627), new Item(1629),
							new Item(1631), new Item(1621), new Item(1619), new Item(1617) };
					if (player.getThievingDelay() > Utils.currentTimeMillis()
							|| player.getActionManager().getActionDelay() != 0)
						return;
					Item item = items[Utils.random(items.length)];
					if (item.getDefinitions().isStackable())
						item.setAmount(Utils.random(1, 5));
					if (player.getInventory().addItem(item)) {
						if (!player.getPerkManager().sleightOfHand)
							player.applyHit(new Hit(player, Utils.random(1, 3), HitLook.REGULAR_DAMAGE, 1));
						int amount = player.getSkills().getLevelForXp(Skills.THIEVING) * 52;
						player.addMoney(Utils.random(1, amount));
						player.setNextAnimation(THIEVING_ANIMATION);
						player.setThievingDelay(Utils.currentTimeMillis() + 1900);
						player.getSkills().addXp(Skills.THIEVING, 25);
						player.getResourceGather().submitResourceGatherForStats(item, 25, Skills.THIEVING, false);
						player.addTimesStolen();
						player.getAchManager().addKeyAmount("thieve", 1);
						player.getAchManager().addKeyAmount("master", 1);
						player.sendMessage("You've successfully stolen from this stall; " + "times thieved: "
								+ Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);
						Pets.checkSkillingPet(player, 38109);
						if (Utils.random(75) == 0) {
							player.getActionManager().forceStop();
							new RogueNPC(new WorldTile(player), player);
							player.sendMessage("<col=ff0000>A Rogue appears out of nowhere.");
						}
					} else
						player.sendMessage("You do not have enough inventory space to do this.", true);
					return;
				}
				/**
				 * Food stall.
				 */
				if (id == 4875) {
					Item[] items = { new Item(379), new Item(385), new Item(373), new Item(7946), new Item(391),
							new Item(1963), new Item(315), new Item(319), new Item(347), new Item(325), new Item(361),
							new Item(365), new Item(333), new Item(329), new Item(351), new Item(339) };
					if (player.getSkills().getLevel(Skills.THIEVING) < 30) {
						player.sendMessage("You need a Thieving level of 30 to thieve from this stall.");
						return;
					}
					if (player.getThievingDelay() > Utils.currentTimeMillis()
							|| player.getActionManager().getActionDelay() != 0)
						return;
					Item item = items[Utils.random(items.length)];
					if (item.getDefinitions().isStackable())
						item.setAmount(Utils.random(1, 5));
					if (player.getInventory().addItem(item)) {
						if (!player.getPerkManager().sleightOfHand)
							player.applyHit(new Hit(player, Utils.random(1, 4), HitLook.REGULAR_DAMAGE, 1));
						int amount = player.getSkills().getLevelForXp(Skills.THIEVING) * 102;
						player.addMoney(Utils.random(1, amount));
						player.setNextAnimation(THIEVING_ANIMATION);
						player.setThievingDelay(Utils.currentTimeMillis() + 1900);
						player.getSkills().addXp(Skills.THIEVING, 50);
						player.getResourceGather().submitResourceGatherForStats(item, 50, Skills.THIEVING, false);
						player.addTimesStolen();
						Pets.checkSkillingPet(player, 38109);
						player.getAchManager().addKeyAmount("thieve", 1);
						player.sendMessage("You've successfully stolen from this stall; " + "times thieved: "
								+ Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);
						if (Utils.random(75) == 0) {
							player.getActionManager().forceStop();
							new RogueNPC(new WorldTile(player), player);
							player.sendMessage("<col=ff0000>A Rogue appears out of nowhere.");
						}
					} else
						player.sendMessage("You do not have enough inventory space to do this.", true);
					return;
				}
				/**
				 * General stall.
				 */
				if (id == 4876) {
					Item[] items = { new Item(1391), new Item(2357), new Item(1776) };
					if (player.getSkills().getLevel(Skills.THIEVING) < 65) {
						player.sendMessage("You need a Thieving level of 65 to thieve from this stall.");
						return;
					}
					if (player.getThievingDelay() > Utils.currentTimeMillis()
							|| player.getActionManager().getActionDelay() != 0)
						return;
					Item item = items[Utils.random(items.length)];
					if (item.getDefinitions().isStackable())
						item.setAmount(Utils.random(1, 5));
					if (player.getInventory().addItem(item)) {
						if (!player.getPerkManager().sleightOfHand)
							player.applyHit(new Hit(player, Utils.random(1, 5), HitLook.REGULAR_DAMAGE, 1));
						int amount = player.getSkills().getLevelForXp(Skills.THIEVING) * 152;
						player.addMoney(Utils.random(1, amount));
						player.setNextAnimation(THIEVING_ANIMATION);
						player.setThievingDelay(Utils.currentTimeMillis() + 1900);
						player.getSkills().addXp(Skills.THIEVING, 75);
						player.getResourceGather().submitResourceGatherForStats(item, 75, Skills.THIEVING, false);
						player.addTimesStolen();
						Pets.checkSkillingPet(player, 38109);
						player.getAchManager().addKeyAmount("thieve", 1);
						player.sendMessage("You've successfully stolen from this stall; " + "times thieved: "
								+ Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);
						if (Utils.random(75) == 0) {
							player.getActionManager().forceStop();
							new RogueNPC(new WorldTile(player), player);
							player.sendMessage("<col=ff0000>A Rogue appears out of nowhere.");
						}
					} else
						player.sendMessage("You do not have enough inventory space to do this.", true);
					return;
				}
				/**
				 * Magic stall.
				 */
				if (id == 4877) {
					Item[] items = { new Item(577), new Item(579), new Item(1011), new Item(1017), new Item(2579),
							new Item(1381), new Item(1383), new Item(1385), new Item(1387), new Item(7398),
							new Item(7399), new Item(7400), new Item(4089), new Item(4099), new Item(4109),
							new Item(4091), new Item(4093), new Item(4101), new Item(4103), new Item(4111),
							new Item(4113), new Item(4095), new Item(4105), new Item(4115), new Item(4097),
							new Item(4107), new Item(4117), new Item(1437), new Item(7937), new Item(554),
							new Item(555), new Item(556), new Item(557), new Item(558), new Item(559), new Item(560),
							new Item(561), new Item(562), new Item(563), new Item(564), new Item(565), new Item(566),
							new Item(9075) };
					if (player.getSkills().getLevel(Skills.THIEVING) < 85) {
						player.sendMessage("You need a Thieving level of 85 to thieve from this stall.");
						return;
					}
					if (player.getThievingDelay() > Utils.currentTimeMillis()
							|| player.getActionManager().getActionDelay() != 0)
						return;
					Item item = items[Utils.random(items.length)];
					if (item.getDefinitions().isStackable())
						item.setAmount(Utils.random(1, 5));
					if (player.getInventory().addItem(item)) {
						if (!player.getPerkManager().sleightOfHand)
							player.applyHit(new Hit(player, Utils.random(1, 6), HitLook.REGULAR_DAMAGE, 1));
						int amount = player.getSkills().getLevelForXp(Skills.THIEVING) * 202;
						player.addMoney(Utils.random(1, amount));
						player.setNextAnimation(THIEVING_ANIMATION);
						player.setThievingDelay(Utils.currentTimeMillis() + 1900);
						player.getSkills().addXp(Skills.THIEVING, 100);
						player.getResourceGather().submitResourceGatherForStats(item, 100, Skills.THIEVING, false);
						player.addTimesStolen();
						Pets.checkSkillingPet(player, 38109);
						player.getAchManager().addKeyAmount("thieve", 1);
						player.sendMessage("You've successfully stolen from this stall; " + "times thieved: "
								+ Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);
						if (Utils.random(75) == 0) {
							player.getActionManager().forceStop();
							new RogueNPC(new WorldTile(player), player);
							player.sendMessage("<col=ff0000>A Rogue appears out of nowhere.");
						}
					} else
						player.sendMessage("You do not have enough inventory space to do this.", true);
					return;
				}
				/**
				 * Scimitar stall.
				 */
				if (id == 4878) {
					Item[] items = { new Item(1323), new Item(1325), new Item(1327), new Item(1329), new Item(1331),
							new Item(1333), new Item(4587), new Item(6611) };
					if (player.getSkills().getLevel(Skills.THIEVING) < 95) {
						player.sendMessage("You need a Thieving level of 95 to thieve from this stall.");
						return;
					}
					if (player.getThievingDelay() > Utils.currentTimeMillis()
							|| player.getActionManager().getActionDelay() != 0)
						return;
					Item item = items[Utils.random(items.length)];
					if (item.getDefinitions().isStackable())
						item.setAmount(Utils.random(1, 5));
					if (player.getInventory().addItem(item)) {
						if (!player.getPerkManager().sleightOfHand)
							player.applyHit(new Hit(player, Utils.random(1, 7), HitLook.REGULAR_DAMAGE, 1));
						int amount = player.getSkills().getLevelForXp(Skills.THIEVING) * 252;
						player.addMoney(Utils.random(1, amount));
						player.setNextAnimation(THIEVING_ANIMATION);
						player.setThievingDelay(Utils.currentTimeMillis() + 1900);
						player.getSkills().addXp(Skills.THIEVING, 125);
						player.getResourceGather().submitResourceGatherForStats(item, 125, Skills.THIEVING, false);
						player.addTimesStolen();
						Pets.checkSkillingPet(player, 38109);
						player.getAchManager().addKeyAmount("thieve", 1);
						player.sendMessage("You've successfully stolen from this stall; " + "times thieved: "
								+ Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);
						if (Utils.random(75) == 0) {
							player.getActionManager().forceStop();
							new RogueNPC(new WorldTile(player), player);
							player.sendMessage("<col=ff0000>A Rogue appears out of nowhere.");
						}
					} else
						player.sendMessage("You do not have enough inventory space to do this.", true);
					return;
				}
				/**
				 * Donator Zone Gem stall.
				 */
				if (id == 34385 && object.getX() == 4375) {
					if (player.getSkills().getLevel(Skills.THIEVING) < 50) {
						player.sendMessage("You need a Thieving level of 50 to thieve from this stall.");
						return;
					}
					if (player.getThievingDelay() > Utils.currentTimeMillis()
							|| player.getActionManager().getActionDelay() != 0)
						return;
					Item[] rewards = { new Item(1478), new Item(1704), new Item(1706), new Item(1708), new Item(1710),
							new Item(1712), new Item(1725), new Item(1727), new Item(1729), new Item(1731),
							new Item(1601), new Item(1603), new Item(1605), new Item(1607), new Item(1609),
							new Item(1631), new Item(1611), new Item(1613), new Item(1615), new Item(1617),
							new Item(1619), new Item(1621), new Item(1623), new Item(1625), new Item(1627),
							new Item(1629) };
					Item item = rewards[Utils.random(rewards.length)];
					if (item.getDefinitions().isStackable())
						item.setAmount(Utils.random(1, 5));
					if (player.getInventory().addItem(item)) {
						if (!player.getPerkManager().sleightOfHand)
							player.applyHit(new Hit(player, Utils.random(1, 5), HitLook.REGULAR_DAMAGE, 1));
						int amount = player.getSkills().getLevelForXp(Skills.THIEVING) * 277;
						player.addMoney(Utils.random(1, amount));
						player.setNextAnimation(THIEVING_ANIMATION);
						player.setThievingDelay(Utils.currentTimeMillis() + 2100);
						player.getSkills().addXp(Skills.THIEVING, 125);
						player.getResourceGather().submitResourceGatherForStats(item, 125, Skills.THIEVING, false);
						player.addTimesStolen();
						Pets.checkSkillingPet(player, 38109);
						player.getAchManager().addKeyAmount("thieve", 1);
						player.sendMessage("You've successfully stolen from this stall; " + "times thieved: "
								+ Colors.red + Utils.getFormattedNumber(player.getTimesStolen()) + "</col>.", true);
						if (Utils.random(75) == 0) {
							player.getActionManager().forceStop();
							new RogueNPC(new WorldTile(player), player);
							player.sendMessage("<col=ff0000>A Rogue appears out of nowhere.");
						}
					} else
						player.sendMessage("You do not have enough inventory space to do this.", true);
					return;
				} else if (id == 62680)
					player.getGEManager().openCollectionBox();
				else if (id == 62677)
					player.getDominionTower().openRewards();
				else if (id >= 15477 && id <= 15482) {
					if (player.hasHouse) {
						player.getHouse().setBuildMode(false);
						player.getHouse().enterMyHouse();
						return;
					}
					player.sendMessage("You must first purchase a house from the Estate Agent.");
				} else if (id == 62688)
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You've a Dominion Factor of " + player.getDominionTower().getTotalScore() + ".");
				else if (id == 68107)
					FightKiln.enterFightKiln(player, true);
				else if (id == 34384 || id == 34383 || id == 14011 || id == 7053 || id == 34387 || id == 34386)
					Thieving.handleStalls(player, object);
				else if (id == 2418)
					PartyRoom.openPartyChest(player);
				else if (id == 67051)
					player.getDialogueManager().startDialogue("Marv", true);
				else {
					switch (objectDef.name.toLowerCase()) {
					case "crashed star":
						if (objectDef.containsOption("Check-progress"))
							ShootingStar.prospect(player);
						break;
					case "bank":
					case "bank chest":
					case "bank booth":
					case "counter":
						if (objectDef.containsOption(1, "Bank"))
							player.getBank().openPlayerBank();
						break;
					case "gates":
					case "gate":
					case "metal door":
						if (object.getType() == 0 && objectDef.containsOption(1, "Open"))
							handleGate(player, object);
						break;
					case "door":
						if (object.getType() == 0 && objectDef.containsOption(1, "Open"))
							handleDoor(player, object);
						break;
					case "ladder":
						handleLadder(player, object, 2);
						break;
					case "staircase":
						handleStaircases(player, object, 2);
						break;
					default:
						// player.sendMessage("Nothing
						// interesting happens.");
						break;
					}
				}
				if (Settings.DEBUG)
					Logger.log("ObjectHandler", "clicked 2 at object id : " + id + ", " + object.getX() + ", "
							+ object.getY() + ", " + object.getPlane());
			}
		}, true));
	}

	private static void handleOption3(final Player player, final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("Option 3:" + object.getId() + "; " + object.getX() + " "
					+ object.getY() + " " + object.getPlane());
		if (player.isLocked())
			return;
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControlerManager().processObjectClick3(object))
					return;
				if (!player.getNewQuestManager().processObjectClick3(object))
					return;
				if (PrifddinasCity.handleObjectOption3(player, object))
					return;
				if (id == 62688)
					player.sendMessage("Dominion Tower rewards are obtainable from the Tower Head downstairs.");
				if (id == 72927 || id == 72933)
					Crucible.leaveArena(player);
				if (id == 87306) {
					player.getActionManager()
							.setAction(new DivinationConvert(player, new Object[] { ConvertMode.CONVERT_TO_XP }));
					return;
				}
				String objectName = objectDef.name.toLowerCase();

				if (objectName.contains("bank") || objectName.contains("counter")) {
					player.getGEManager().openCollectionBox();
					return;
				}
				if (object.getId() == 91172) {
					if (object.getX() == 2328 && object.getY() == 3176) {
						if (player.withinDistance(new WorldTile(object.getX(), object.getY(), object.getPlane())))
							player.setNextWorldTile(new WorldTile(2327, 3176, 0));
					}
					if (object.getX() == 2325 && object.getY() == 3168) {
						if (player.withinDistance(new WorldTile(object.getX(), object.getY(), object.getPlane())))
							player.setNextWorldTile(new WorldTile(2327, 3168, 0));
					}
				}
				if (id == 45802) {
					if (player.getContract() == null) {
						ContractHandler.getNewContract(player);
						player.sendMessage(Colors.orange + "Reaper Contract: " + player.getContract().getKillAmount()
								+ "x " + NPCDefinitions.getNPCDefinitions(player.getContract().getNpcId()).getName()
								+ ".");
					} else
						player.sendMessage("You already have an active Reaper contract.");
					return;
				}

				/**
				 * Slayer Tower.
				 */
				if (id == 82667 || id == 82483) { /** Staircases **/
					player.useStairs(-1, new WorldTile(player.getX(), player.getY(), 0), 0, 0);
					return;
				} else if (id >= 15477 && id <= 15482) {
					if (player.hasHouse) {
						player.getHouse().setBuildMode(true);
						player.getHouse().enterMyHouse();
						return;
					}
					player.sendMessage("You must first purchase a house from the Estate Agent.");
				}
				if (player.getFarmingManager().isFarming(id, null, 3))
					return;
				switch (objectDef.name.toLowerCase()) {
				case "gate":
				case "metal door":
					if (object.getType() == 0 && objectDef.containsOption(2, "Open"))
						handleGate(player, object);
					break;

				case "door":
					if (object.getType() == 0 && objectDef.containsOption(2, "Open"))
						handleDoor(player, object);
					break;
				case "ladder":
					handleLadder(player, object, 3);
					break;
				case "staircase":
					handleStaircases(player, object, 3);
					break;
				default:
					// player.sendMessage("Nothing interesting happens.");
					break;
				}
				if (Settings.DEBUG)
					Logger.log("ObjectHandler", "cliked 3 at object id : " + id + ", " + object.getX() + ", "
							+ object.getY() + ", " + object.getPlane() + ", ");
			}
		}, true));
	}

	private static void handleOption4(final Player player, final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("Option 4:" + object.getId() + "; " + object.getX() + " "
					+ object.getY() + " " + object.getPlane());
		if (player.isLocked())
			return;
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControlerManager().processObjectClick4(object))
					return;
				if (!player.getNewQuestManager().processObjectClick4(object))
					return;
				// living rock Caverns
				if (id == 87306) {
					player.getActionManager()
							.setAction(new DivinationConvert(player, new Object[] { ConvertMode.CONVERT_TO_MORE_XP }));
					return;
				}
				if (id == 45076)
					MiningBase.propect(player, "This rock contains a large concentration of gold.");
				else if (id == 5999)
					MiningBase.propect(player, "This rock contains a large concentration of coal.");
				else if (id >= 15477 && id <= 15482) {
					player.getTemporaryAttributtes().put("joinguesthouse", Boolean.TRUE);
					player.getPackets().sendInputNameScript("Please enter the name of the player:");
				} else if (player.getFarmingManager().isFarming(id, null, 4))
					return;
				else {
					switch (objectDef.name.toLowerCase()) {
					default:
						// player.sendMessage("Nothing interesting happens.");
						break;
					}
				}
				if (id == 45802) {
					ShopsHandler.openShop(player, 58);
				}
				if (Settings.DEBUG)
					Logger.log("ObjectHandler", "cliked 4 at object id : " + id + ", " + object.getX() + ", "
							+ object.getY() + ", " + object.getPlane() + ", ");
			}
		}, true));
	}

	private static void handleOption5(final Player player, final WorldObject object) {
		final ObjectDefinitions objectDef = object.getDefinitions();
		final int id = object.getId();
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("Option 5:" + object.getId() + "; " + object.getX() + " "
					+ object.getY() + " " + object.getPlane());
		if (player.isLocked())
			return;
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.stopAll();
				player.faceObject(object);
				if (!player.getControlerManager().processObjectClick5(object))
					return;
				if (!player.getNewQuestManager().processObjectClick5(object))
					return;
				if (id == 82016) { // Kalphite King stairs - exit.
					player.lock();
					player.setNextAnimation(new Animation(19499));
					WorldTasksManager.schedule(new WorldTask() { // to remove at
						// same time
						// it
						// teleports
						@Override
						public void run() {
							player.setNextAnimation(new Animation(-1));
							player.setNextWorldTile(new WorldTile(2971, 1656, 0));
							player.unlock();
							stop();
						}
					}, 3);
					return;
				}
				if (objectDef.containsOption("Push-through")) {
					PuroPuro.pushThrough(player, object);
					return;
				}
				if (id == -1) {
					// unused
				} else {
					switch (objectDef.name.toLowerCase()) {
					case "fire":
						if (objectDef.containsOption(4, "Use"))
							Bonfire.addLogs(player, object);
						break;
					default:
						player.sendMessage("Nothing interesting happens.", true);
						break;
					}
				}
				if (Settings.DEBUG)
					Logger.log("ObjectHandler", "cliked 5 at object id : " + id + ", " + object.getX() + ", "
							+ object.getY() + ", " + object.getPlane() + ", ");
			}
		}, true));
	}

	public static void handleItemOnObject(final Player player, final WorldObject object, final int interfaceId,
			final Item item) {
		final int itemId = item.getId();
		final ObjectDefinitions objectDef = object.getDefinitions();
		if (itemId == 11782 && player.isHeadStaff()) {
			World.removeObject(object, true);
			player.sendMessage("You deleted " + object.getId() + " at " + object.getX() + "/" + object.getY());
		}
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("Item: " + item.getId() + "; used on: " + object.getId() + "; "
					+ object.getX() + " " + object.getY() + " " + object.getPlane());
		if (object.getId() == 1996) {
			if (item.getId() != 954)
				return;
			for (int i = 0; i < 7; i++) {
				WorldObject o = new WorldObject(object);
				o.setId(1998);
				o.setLocation(o.getX(), 3475 - i, o.getPlane());
				player.getPackets().sendSpawnedObject(o);
			}
			int amt = player.getInventory().getAmountOf(itemId);
			switch (object.getId()) {
			case 36181:
				if (itemId == 17413) {
					for (int i = 1; i <= amt; i++) {
						player.getSkills().addXp(Skills.HERBLORE, 50);
						player.getInventory().deleteItem(17413, 1);
						player.getInventory().addItem(17568, 1);
						player.sendMessage(Colors.orange
								+ "You siphon some blood into the water. It seems to be drained of red blood cells.",
								true);
					}
				} else
					player.sendMessage(Colors.red + "I don't think that goes here..");
				break;

			case 10158:
				if (itemId == 17568) {
					if (!(player.getSkills().getLevel(Skills.HERBLORE) >= 55))
						player.sendMessage(Colors.red + "You need at least 55 herblore to make this mixture!", true);
					else {
						for (int i = 1; i <= amt; i++) {
							player.getSkills().addXp(Skills.HERBLORE, 175);
							player.getInventory().deleteItem(17568, 1);
							player.getInventory().addItem(17592, 1);
							player.sendMessage(Colors.orange + "You mix the liquids into a stronger dose.", true);
						}
					}
				} else
					player.sendMessage(Colors.red + "I don't think that goes here..", true);
			}

			WorldObject o = new WorldObject(object);
			o.setId(1997);
			player.getPackets().sendSpawnedObject(o);
			player.getGlobalPlayerUpdater().setRenderEmote(188);
			player.setNextForceMovement(new ForceMovement(object, 8, ForceMovement.SOUTH));
			WorldTasksManager.schedule(new WorldTask() {

				@Override
				public void run() {
					for (int i = 0; i < 7; i++) {
						WorldObject o = new WorldObject(object);
						o.setId(1998);
						o.setLocation(o.getX(), 3475 - i, o.getPlane());
						player.getPackets().sendDestroyObject(o);
					}
					WorldObject o = new WorldObject(object);
					o.setId(1996);
					player.getPackets().sendSpawnedObject(o);
					player.getGlobalPlayerUpdater().setRenderEmote(-1);
					player.setNextWorldTile(new WorldTile(2513, 3468, 0));
				}
			}, 7);
		} else if (FishingFerretRoom.handleFerretThrow(player, object, item))
			return;
		player.setRouteEvent(new RouteEvent(object, new Runnable() {
			@Override
			public void run() {
				player.faceObject(object);
				if (!player.getControlerManager().handleItemOnObject(object, item))
					return;
				if (!player.getNewQuestManager().handleItemOnObject(object, item))
					return;
				if (player.getFarmingManager().isFarming(object.getId(), item, 0))
					return;
				if (PrifddinasCity.handleItemOnObject(player, object, item))
					return;
				if (SawMills.isMilling(player, object, item))
					return;
				if (itemId >= 1438 && itemId <= 1450) {
					for (int index = 0; index < RuneCrafting.OBJECTS.length; index++) {
						if (RuneCrafting.OBJECTS[index] == object.getId()) {
							RuneCrafting.infuseTiara(player, index);
							break;
						}
					}
				} else if (object.getId() == 28352 || object.getId() == 28550) {
					Incubator.useEgg(player, itemId);
				} else if (itemId == 1438 && object.getId() == 2452) {
					RuneCrafting.enterAirAltar(player);
				} else if (itemId == 1440 && object.getId() == 2455) {
					RuneCrafting.enterEarthAltar(player);
				} else if (itemId == 1442 && object.getId() == 2456) {
					RuneCrafting.enterFireAltar(player);
				} else if (itemId == 1444 && object.getId() == 2454) {
					RuneCrafting.enterWaterAltar(player);
				} else if (itemId == 1446 && object.getId() == 2457) {
					RuneCrafting.enterBodyAltar(player);
				} else if (itemId == 1448 && object.getId() == 2453) {
					RuneCrafting.enterMindAltar(player);

				} else if (object.getId() == 733 || object.getId() == 64729) {
					player.setNextAnimation(new Animation(PlayerCombat.getWeaponAttackEmote(-1, 0, player)));
					slashWeb(player, object);
				} else if (object.getId() == 48803 && itemId == 954) {
					if (player.isKalphiteLairSetted())
						return;
					player.getInventory().deleteItem(954, 1);
					player.setKalphiteLair();
				} else if (object.getId() == 34576 && itemId == 995) {
					WellOfGoodWill.give(player);
				} else if (object.getId() == 2783 && itemId == 30027) {
					player.getDialogueManager().startDialogue("MalevolentD", object);

				} else if (object.getId() == 11666 || object.getId() == 88261) { // Falador
					// furnace
					if (itemId == 2357) {
						JewellerySmithing.openInterface(player);
						return;
					}
					if (itemId == 2355) {
						player.getDialogueManager().startDialogue("SilverCraftingD", object);
						return;
					}
					if (itemId == 2355 && itemId == 31855) {
						player.getDialogueManager().startDialogue("HydrixCraftingD");
						return;
					}
					player.getDialogueManager().startDialogue("SmeltingD", object);
					return;
				} else if (object.getId() == 48802 && itemId == 954) {
					if (player.isKalphiteLairEntranceSetted())
						return;
					player.getInventory().deleteItem(954, 1);
					player.setKalphiteLairEntrance();
				} else if (object.getId() == 51061 || object.getId() == 8749) {
					Bones bone = BonesOnAltar.isGood(item);
					if (bone != null) {
						player.getDialogueManager().startDialogue("PrayerD", bone, object);
						return;
					} else
						player.sendMessage("Nothing interesting happens.");
					return;
				} else {
					switch (objectDef.name.toLowerCase()) {

					case "anvil":
						ForgingBar bar = ForgingBar.forId(itemId);
						if (bar == ForgingBar.DRACONIC_VISAGE || itemId == 1540) {
							player.getDialogueManager().startDialogue("DFSSmithingD");
							return;
						}

						if (itemId == 14472 || itemId == 14474 || itemId == 14476) {
							player.getDialogueManager().startDialogue("DPlateBodySmithingD");
							return;
						}
						if (bar != null)
							ForgingInterface.sendSmithingInterface(player, bar, object);
						break;

					/**
					 * Charging unpowered orbs.
					 */
					case "obelisk of air":
						if (itemId == 567) {
							player.getActionManager().setAction(new ChargeAirOrb());
							return;
						}
						player.sendMessage("Nothing interesting happens.", true);
						return;

					case "obelisk of water":
						if (itemId == 567) {
							player.getActionManager().setAction(new ChargeWaterOrb());
							return;
						}
						player.sendMessage("Nothing interesting happens.", true);
						return;

					case "obelisk of earth":
						if (itemId == 567) {
							player.getActionManager().setAction(new ChargeEarthOrb());
							return;
						}
						player.sendMessage("Nothing interesting happens.", true);
						return;

					case "obelisk of fire":
						if (itemId == 567) {
							player.getActionManager().setAction(new ChargeFireOrb());
							return;
						}
						player.sendMessage("Nothing interesting happens.", true);
						return;

					case "sink":
					case "fountain":
					case "well":
					case "pump":
					case "water trough":
						WaterFilling.isFilling(player, itemId, false);
						break;

					case "fire":
						Cookables cook = Cooking.isCookingSkill(item);
						if (Bonfire.addLog(player, object, item))
							return;
						else if (cook != null) {
							player.getDialogueManager().startDialogue("CookingD", cook, object);
							return;
						} else
							player.getDialogueManager().startDialogue("SimpleMessage",
									"You can't use that item on the fire.");
						break;
					case "furnace":
						if (itemId == 2357) {
							JewellerySmithing.openInterface(player);
							return;
						}
						if (itemId == 31855) {
							player.getDialogueManager().startDialogue("HydrixCraftingD");
							return;
						}
						if (itemId == 2355) {
							player.getDialogueManager().startDialogue("SilverCraftingD", object);
							return;
						}
						break;
					case "range":
					case "cooking range":
					case "stove":
					case "null":
						if (objectDef.name.toLowerCase().equals("null") && object.getId() != 91162)
							return;
						if (itemId == 2132 || itemId == 2134 || itemId == 2136) {
							Dialogue dialogue = new Dialogue() {

								@Override
								public void start() {
									sendOptionsDialogue("Select an Option", "Dry into sinew", "Regular cooking");
								}

								@Override
								public void run(int interfaceId, int componentId) {
									end();
									switch (componentId) {
									case OPTION_1:
										player.getActionManager().setAction(new SinewCooking());
										break;
									case OPTION_2:
										Cookables cook = Cooking.isCookingSkill(item);
										if (cook != null) {
											player.getDialogueManager().startDialogue("CookingD", cook, object);
											return;
										}
										player.getDialogueManager().startDialogue("SimpleMessage",
												"You can't cook that on a " + objectDef.name + ".");
										break;
									}
								}

								@Override
								public void finish() {
								}

							};
							player.getDialogueManager().startDialogue(dialogue);
							return;
						}
						cook = Cooking.isCookingSkill(item);
						if (cook != null) {
							player.getDialogueManager().startDialogue("CookingD", cook, object);
							return;
						}
						player.getDialogueManager().startDialogue("SimpleMessage",
								"You can't cook that on a " + objectDef.name + ".");
						break;
					default:
						break;
					}
					if (Settings.DEBUG)
						System.out.println(
								"Item on object: " + object.getId() + " named " + object.getDefinitions().getName());
				}
			}
		}));
	}

	private static void handleOptionExamine(final Player player, final WorldObject object) {
		if (player.isHeadStaff())
			player.getPackets().sendPanelBoxMessage("Examined:" + object.getId() + "; " + object.getX() + ", "
					+ object.getY() + ", " + object.getPlane());
		if (Settings.DEBUG)
			if (Settings.DEBUG)

				Logger.log("ObjectHandler",
						"examined object id : " + object.getId() + ", " + object.getX() + ", " + object.getY() + ", "
								+ object.getPlane() + ", " + object.getType() + ", " + object.getRotation() + ", "
								+ object.getDefinitions().name);
		if (!player.getNewQuestManager().handleObjectExamine(object))
			return;
		if (LividFarm.HandleLividFarmObjects(player, object, -1))
			return;
		// easter event
		if (object.getId() == 36780 && player.getEasterStage() == 2 && object.getTileHash() == 589499537
				&& player.getLastWorldTile().withinDistance(object, 2) && player.getInventory().getFreeSlots() > 0) {
			easterEventFound(player, object);
			return;
		}
		if (object.getId() == 409 && player.getEasterStage() == 7 && object.getTileHash() == 320998822
				&& player.getLastWorldTile().withinDistance(object, 2) && player.getInventory().getFreeSlots() > 0) {
			easterEventAltar(player, object);
			return;
		}
		if (object.getId() == 64282) {
			player.getDialogueManager().startDialogue("ClaimThrone");
			player.addWalkSteps(object.getX(), object.getY());
			return;
		}
		// easter event
		if (object.getId() == 9250 && player.getEasterStage() == 11 && object.getTileHash() == 48581941
				&& player.getLastWorldTile().withinDistance(object, 3) && player.getInventory().getFreeSlots() > 0) {
			easterEventStatue(player, object);
			return;
		}

		player.sendMessage(
				"It's an " + (object.getId() == 9250 ? "Statue of Saradomin" : object.getDefinitions().name) + ".");

	}

	private static void easterEventFound(Player player, final WorldObject object) {
		player.setNextForceTalk(new ForceTalk("This must be it! I should bring this to Peale"));
		player.setNextFaceWorldTile(object);
		player.addWalkSteps(object.getX(), object.getY(), 0, true);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextAnimation(new Animation(827));
				player.getInventory().addItem(20475, 1);
				player.sendMessage("You have found an item check your inventory");
				player.setEasterStage(4);
			}
		}, 2);
	}

	private static void easterEventAltar(Player player, final WorldObject object) {
		player.setNextForceTalk(new ForceTalk("This must be the egg that he had been looking for"));
		player.setNextFaceWorldTile(object);
		player.addWalkSteps(object.getX(), object.getY(), 0, true);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextAnimation(new Animation(827));
				player.getInventory().addItem(11023, 1);
				player.sendMessage("You have found an item check your inventory");
				player.setEasterStage(8);
			}
		}, 2);
	}

	private static void easterEventStatue(Player player, final WorldObject object) {
		player.setNextForceTalk(new ForceTalk("This statue looks odd, it looks like something is hidden inside it"));
		player.setNextFaceWorldTile(object);
		player.addWalkSteps(object.getX(), object.getY(), 0, true);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextAnimation(new Animation(827));
				player.getInventory().addItem(11029, 1);
				player.sendMessage("You have found the easter egg you should go back to Mr Bunny");
				player.setEasterStage(12);
			}
		}, 2);
	}

	public static boolean handleDoor(Player player, WorldObject object, long timer) {
		if (World.isSpawnedObject(object))
			return false;
		WorldObject openedDoor = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
				object.getX(), object.getY(), object.getPlane());
		if (object.getRotation() == 0)
			openedDoor.moveLocation(-1, 0, 0);
		else if (object.getRotation() == 1)
			openedDoor.moveLocation(0, 1, 0);
		else if (object.getRotation() == 2)
			openedDoor.moveLocation(1, 0, 0);
		else if (object.getRotation() == 3)
			openedDoor.moveLocation(0, -1, 0);
		if (World.removeObjectTemporary(object, timer, true)) {
			player.faceObject(openedDoor);
			World.spawnObjectTemporary(openedDoor, timer);
			return true;
		}
		return false;
	}

	public static boolean handleDoor(Player player, WorldObject object) {
		return handleDoor(player, object, 60000);
	}

	private static boolean handleStaircases(Player player, WorldObject object, int optionId) {
		String option = object.getDefinitions().getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (player.getPlane() == 3)
				return false;
			player.useStairs(-1, new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), 0, 1);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getPlane() == 0)
				return false;
			player.useStairs(-1, new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), 0, 1);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getPlane() == 3 || player.getPlane() == 0)
				return false;
			player.getDialogueManager().startDialogue("ClimbNoEmoteStairs",
					new WorldTile(player.getX(), player.getY(), player.getPlane() + 1),
					new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), "Go up the stairs.",
					"Go down the stairs.");
		} else
			return false;
		return false;
	}

	private static boolean handleLadder(Player player, WorldObject object, int optionId) {

		// Edits
		if (object.getId() == 39191 && object.getX() == 3241 && object.getY() == 9990) {
			player.setNextAnimation(new Animation(828));
			Magic.sendObjectTeleportSpell(player, true, player.getHomeTile());
			return true;
		}

		String option = object.getDefinitions().getOption(optionId);
		if (option.equalsIgnoreCase("Climb-up")) {
			if (player.getPlane() == 3)
				return false;
			player.useStairs(828, new WorldTile(player.getX(), player.getY(), player.getPlane() + 1), 1, 2);
		} else if (option.equalsIgnoreCase("Climb-down")) {
			if (player.getPlane() == 0)
				return false;
			player.useStairs(828, new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), 1, 2);
		} else if (option.equalsIgnoreCase("Climb")) {
			if (player.getPlane() == 3 || player.getPlane() == 0)
				return false;
			player.getDialogueManager().startDialogue("ClimbEmoteStairs",
					new WorldTile(player.getX(), player.getY(), player.getPlane() + 1),
					new WorldTile(player.getX(), player.getY(), player.getPlane() - 1), "Climb up the ladder.",
					"Climb down the ladder.", 828);
		} else
			return false;
		return true;
	}

	private static void slashWeb(Player player, WorldObject object) {
		boolean usingKnife = false;
		int defs = CombatDefinitions.getMeleeBonusStyle(player.getEquipment().getWeaponId(), 0);
		int defs2 = CombatDefinitions.getMeleeBonusStyle(player.getEquipment().getWeaponId(), 1);
		int defs3 = CombatDefinitions.getMeleeBonusStyle(player.getEquipment().getWeaponId(), 2);

		if (defs != CombatDefinitions.SLASH_ATTACK && defs2 != CombatDefinitions.SLASH_ATTACK
				&& defs3 != CombatDefinitions.SLASH_ATTACK) {
			if (!player.getInventory().containsItem(946, 1)) {
				player.sendMessage("You need something sharp to cut this with.");
				return;
			}
			usingKnife = true;
		}

		int weaponEmote = PlayerCombat.getWeaponAttackEmote(player.getEquipment().getWeaponId(),
				player.getCombatDefinitions().getAttackStyle(), player);
		int knifeEmote = -1;

		player.setNextAnimation(new Animation(usingKnife ? knifeEmote : weaponEmote));

		if (Utils.getRandom(1) == 0) {
			World.spawnObjectTemporary(new WorldObject(object.getId() + 1, object.getType(), object.getRotation(),
					object.getX(), object.getY(), object.getPlane()), 60000);
			player.sendMessage("You slash through the web!");
		} else
			player.sendMessage("You fail to cut through the web.");
	}

	public static boolean handleGate(Player player, WorldObject object) {
		if (World.isSpawnedObject(object))
			return false;
		if (object.getRotation() == 0) {
			boolean south = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() - 1, object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				south = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(),
					otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (south) {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor1.setRotation(3);
				openedDoor2.moveLocation(-1, 0, 0);
			} else {
				openedDoor1.moveLocation(-1, 0, 0);
				openedDoor2.moveLocation(-1, 0, 0);
				openedDoor2.setRotation(3);
			}

			if (World.removeObjectTemporary(object, 60000, true)
					&& World.removeObjectTemporary(otherDoor, 60000, true)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, 60000);
				World.spawnObjectTemporary(openedDoor2, 60000);
				return true;
			}
		} else if (object.getRotation() == 2) {

			boolean south = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX(), object.getY() + 1, object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX(), object.getY() - 1, object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				south = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(),
					otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (south) {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor2.setRotation(1);
				openedDoor2.moveLocation(1, 0, 0);
			} else {
				openedDoor1.moveLocation(1, 0, 0);
				openedDoor1.setRotation(1);
				openedDoor2.moveLocation(1, 0, 0);
			}
			if (World.removeObjectTemporary(object, 60000, true)
					&& World.removeObjectTemporary(otherDoor, 60000, true)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, 60000);
				World.spawnObjectTemporary(openedDoor2, 60000);
				return true;
			}
		} else if (object.getRotation() == 3) {

			boolean right = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX() - 1, object.getY(), object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX() + 1, object.getY(), object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				right = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(),
					otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (right) {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor2.setRotation(0);
				openedDoor1.setRotation(2);
				openedDoor2.moveLocation(0, -1, 0);
			} else {
				openedDoor1.moveLocation(0, -1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.setRotation(2);
				openedDoor2.moveLocation(0, -1, 0);
			}
			if (World.removeObjectTemporary(object, 60000, true)
					&& World.removeObjectTemporary(otherDoor, 60000, true)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, 60000);
				World.spawnObjectTemporary(openedDoor2, 60000);
				return true;
			}
		} else if (object.getRotation() == 1) {

			boolean right = true;
			WorldObject otherDoor = World.getObjectWithType(
					new WorldTile(object.getX() - 1, object.getY(), object.getPlane()), object.getType());
			if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
					|| otherDoor.getType() != object.getType()
					|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name)) {
				otherDoor = World.getObjectWithType(new WorldTile(object.getX() + 1, object.getY(), object.getPlane()),
						object.getType());
				if (otherDoor == null || otherDoor.getRotation() != object.getRotation()
						|| otherDoor.getType() != object.getType()
						|| !otherDoor.getDefinitions().name.equalsIgnoreCase(object.getDefinitions().name))
					return false;
				right = false;
			}
			WorldObject openedDoor1 = new WorldObject(object.getId(), object.getType(), object.getRotation() + 1,
					object.getX(), object.getY(), object.getPlane());
			WorldObject openedDoor2 = new WorldObject(otherDoor.getId(), otherDoor.getType(),
					otherDoor.getRotation() + 1, otherDoor.getX(), otherDoor.getY(), otherDoor.getPlane());
			if (right) {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor1.setRotation(0);
				openedDoor2.moveLocation(0, 1, 0);
			} else {
				openedDoor1.moveLocation(0, 1, 0);
				openedDoor2.setRotation(0);
				openedDoor2.moveLocation(0, 1, 0);
			}
			if (World.removeObjectTemporary(object, 60000, true)
					&& World.removeObjectTemporary(otherDoor, 60000, true)) {
				player.faceObject(openedDoor1);
				World.spawnObjectTemporary(openedDoor1, 60000);
				World.spawnObjectTemporary(openedDoor2, 60000);
				return true;
			}
		}
		return false;
	}

	private static boolean getRepeatedTele(Player player, int x1, int y1, int p1, int x2, int y2, int p2) {
		if (player.getX() == x1 && player.getY() == y1) {
			Magic.sendTeleportSpell(player, 17803, -1, 3447, -1, 1, 0.0, new WorldTile(x2, y2, p2), 2, false,
					Magic.OBJECT_TELEPORT);
			return true;
		} else if (player.getX() == x2 && player.getY() == y2) {
			Magic.sendTeleportSpell(player, 17803, -1, 3447, -1, 1, 0.0, new WorldTile(x1, y1, p1), 2, false,
					Magic.OBJECT_TELEPORT);
			return true;
		}
		return false;
	}
}
