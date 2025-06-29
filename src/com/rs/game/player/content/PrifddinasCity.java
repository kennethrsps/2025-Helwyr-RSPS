package com.rs.game.player.content;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activites.CrystalChest;
import com.rs.game.activites.MotherlodeMaw;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Cooking;
import com.rs.game.player.actions.Cooking.Cookables;
import com.rs.game.player.actions.SandBucketFillAction;
import com.rs.game.player.actions.SerenStoneCleansing;
import com.rs.game.player.actions.WaterFilling;
import com.rs.game.player.actions.crafting.PrifddinasHarp;
import com.rs.game.player.actions.firemaking.Bonfire;
import com.rs.game.player.actions.mining.Mining;
import com.rs.game.player.actions.mining.Mining.RockDefinitions;
import com.rs.game.player.actions.summoning.Summoning;
import com.rs.game.player.actions.thieving.prifddinas.PickPocketAction;
import com.rs.game.player.actions.thieving.prifddinas.PickPocketableNPC;
import com.rs.game.player.content.Slayer.SlayerMaster;
import com.rs.game.player.content.agility.PrifddinasAgilityCourse;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;
import com.rs.utils.ShopsHandler;

/**
 * Handles Most (if not all) interactions in the Prifddinas city.
 * 
 * @author Zeus
 */
public class PrifddinasCity {

	/**
	 * Handles the actual Prifddinas Object interactions - Option 1.
	 * 
	 * @param player
	 *            The player interacting.
	 * @param object
	 *            The object interacting with.
	 * @return if is a handable Prifddinas object.
	 */
	public static boolean handleObjectOption1(Player player, WorldObject object) {
		switch (object.getId()) {
		case 92435: /** Main Stairs UP **/
			player.useStairs(835, new WorldTile(2214, 3363, 2), 1, 2);
			break;
		case 92436: /** Main Stairs DOWN **/
			player.useStairs(832, new WorldTile(2215, 3364, 1), 1, 2);
			break;
		case 92885: /** Bonfire **/
		case 92903:
			Bonfire.addLogs(player, object);
			break;
		case 92694: /** Holiday Portal **/
			// TODO Make the portal send a config to make it active on holidays.
			player.getDialogueManager().startDialogue("SimpleMessage",
					"The Portal is currently inactive; no events " + "are currently taking place.");
			break;
		case 92415: /** Dungeoneering Portal **/
			Magic.daemonheimTeleport(player, new WorldTile(3448, 3699, 0));
			break;
		case 92413: /** Clan Portal **/
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2972, 3289, 0));
			break;
		case 111763: /** dragonkin portal **/
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(5024, 9260, 0));
			break;
			 //player.sm(Colors.red +
			 //"This is not ready yet.... Stay tuned!");
			 //player.getDialogueManager().finishDialogue();
			// break;
		case 111737: /** dragonkin lab **/
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(5024, 9260, 0));
			break;
			 //player.sm(Colors.red +
			// "This is not ready yet.... Stay tuned!");
			// player.getDialogueManager().finishDialogue();
			// break;
		case 93249: /** Port Portal **/
			player.getPorts().enterPorts();
			break;
		case 92713: /** Seren Stone **/
			player.getActionManager().setAction(new Mining(object, RockDefinitions.SEREN_STONE));
			break;
		case 94098: /** Useless crates **/
		case 94102:
		case 94100:
		case 94101:
		case 92708:
			player.sendMessage("You search the crates, but find nothing of interest..");
			break;
		case 92627: /** Crystal Chest **/
			CrystalChest.openChest(object, player);
			break;
		case 93052: /** Morvran's Cage **/
			player.getDialogueManager().startDialogue("SimplePlayerMessage",
					"Now why on " + Settings.SERVER_NAME + " would I do that!?");
			break;
		case 93024: /** Gem Rocks **/
		case 93025:
			player.getActionManager().setAction(new Mining(object, RockDefinitions.GEM_ROCK));
			break;
		case 92237: /** The Tears of Seren **/
			rechargeJewellery(player);
			break;
		case 2564: /** Elven Grimoire **/
			player.getDialogueManager().startDialogue("AltarSwapD");
			break;
		case 92703: /** Memoriam Crystal **/
		case 94197:
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Crystals can not yet be retrieved; " + "stay tuned!");
			break;
		case 94179: /** Loom **/
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Looms currently have no purpose on " + Settings.SERVER_NAME + "; "
							+ "make a suggestion on our ;;forums and we'll we what we can do about this!");
			break;
		/** Dungeoneering Resource Dungeons **/
		case 94320:
			Magic.resourcesTeleport(player, new WorldTile(1374, 4610, 0), 115);
			break;
		case 94322:
			Magic.resourcesTeleport(player, new WorldTile(2234, 3398, 1));
			break;
		case 94273: // motherlode maw
			MotherlodeMaw.handleMotherlodeMaw(player);
			break;
		case 94078:
			Dialogue dialogue = new Dialogue() {

				@Override
				public void start() {
					sendDialogue("There's no doubt that this resource dungeon is the same material and "
							+ "design as Brimhaven Dungeon. They must both be made by the Dragonkin.");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					switch (stage) {
					case -1:
						sendDialogue("That is not to say that the entrance teleports into Brimhaven Dungeon. An "
								+ "oppressive magic fills the room. It must be located deep below Daemonheim.");
						stage = 0;
						break;
					case 0:
						end();
						break;
					}
				}

				@Override
				public void finish() {
				}
			};
			player.getDialogueManager().startDialogue(dialogue);
			break;
		case 94079:
			player.getDialogueManager().startDialogue("SimpleMessage",
					"The same symbol has been etched into the altar over and over again, as if "
							+ "its owner was bored or distracted. It looks like the letter K.");
			break;
		case 94077:
			if (player.getSlayerManager().isValidTask("EDIMMU")) {
				player.setNextWorldTile(new WorldTile(player.getX(), (player.getY() < 4636 ? 4637 : 4635), 0));
				break;
			}
			player.getDialogueManager().startDialogue("SimpleNPCMessage", 20283,
					"I can not allow you to enter this place. Only those with permission from a Slayer "
							+ "master may enter.");
			break;

		case 94319: // 95 Dungeoneering on the north side containing a bloodwood
					// tree,
					// pawyas and grenwalls, implings, and random divine
					// locations.
			player.getDialogueManager().startDialogue("SimpleMessage",
					"This resource dungeon is currently " + "not supported; stay tuned!");
			break;
		case 94047:
			/** Hefin Cithadel Altar **/
			final int maxPrayer = player.getSkills().getLevelForXp(Skills.PRAYER) * 10;
			if (player.getPrayer().getPrayerpoints() < maxPrayer) {
				player.setNextAnimation(new Animation(645));
				player.getPrayer().restorePrayer(maxPrayer);
				player.sendMessage("You've recharged your Prayer points.");
				break;
			}
			player.sendMessage("You already have full Prayer points.");
			break;
		case 94048:
			player.getActionManager().setAction(new SerenStoneCleansing(object));
			break;
		case 93115:
			// TODO Serenity posts -
			// http://runescape.wikia.com/wiki/Serenity_posts
			player.getDialogueManager().startDialogue("SimpleMessage",
					"This Agility D&D is not " + "yet supported; stay tuned!");
			break;
		case 94230: /** Summoning Obelisk **/
			Summoning.sendInterface(player);
			break;
		case 94218:
		case 94219:
			player.getActionManager().setAction(new Mining(object, RockDefinitions.SOFT_CLAY));
			break;
		case 94068:
		case 94069:
			player.getActionManager().setAction(new Mining(object, RockDefinitions.CRYSTAL_SANDSTONE));
			break;
		case 92238: /** Prifddinas Max Guild **/
			if (player.getY() >= 3338) {
				if (!player.isMax()) {
					player.getDialogueManager().startDialogue("SimpleNPCMessage", 19818,
							"You're not strong enough to enter that gate.");
					break;
				}
				player.setNextWorldTile(new WorldTile(2276, 3336, 1));
				break;
			}
			player.setNextWorldTile(new WorldTile(2276, 3338, 1));
			break;
		case 92239:
		case 92240:
		case 92256:
			player.getDialogueManager().startDialogue("SimpleMessage", "Teleport attuning is not yet added; "
					+ "stay tuned! - We do not have the right object config ID's for attuned versions.");
			break;
		case 92358:
		case 92349:
		case 92298:
		case 92319:
		case 92304:
		case 92307:
		case 92331:
		case 92301:
		case 92316:
		case 92343:
		case 92322:
		case 92362:
		case 92368:
		case 92365:
		case 92337:
		case 92310:
		case 92313:
		case 92352:
		case 92355:
		case 92346:
		case 92334:
		case 92328:
		case 92325:
		case 92340:
		case 92371:
		case 92374:
			player.getDialogueManager().startDialogue("SimpleMessage", "Banner unfurling is not yet added; "
					+ "stay tuned! - We do not have the right object config ID's for the unfurled versions.");
			break;
		case 92255:
			player.setNextWorldTile(new WorldTile(player.getX(), (player.getY() >= 3318 ? 3316 : 3318), 1));
			break;
		case 94065: // Well
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Use the item you want to fill " + "on the well to fill it!");
			break;
		case 94066: // Sand pit
			player.getActionManager().setAction(new SandBucketFillAction());
			break;
		// Start of Hefin's Agility course.
		case 94050:
			PrifddinasAgilityCourse.leapAcrossWalkway(player, object);
			break;
		case 94051:
			PrifddinasAgilityCourse.traverseCliff(player, object);
			break;
		case 94055:
			PrifddinasAgilityCourse.scaleCathedral(player, object);
			break;
		case 94056:
			PrifddinasAgilityCourse.vaultRoof(player, object);
			break;
		case 94057:
			PrifddinasAgilityCourse.slideDownZipLine(player, object);
			break;
		// Start of Waterfall Fishing objects.
		case 99374:
			PrifddinasAgilityCourse.jumpToOutcrop(player, object);
			break;
		case 99373:
			PrifddinasAgilityCourse.jumpToBridge(player, object);
			break;
		case 99396:
			PrifddinasAgilityCourse.climbFirstHandholds(player, object);
			break;
		case 99397:
			PrifddinasAgilityCourse.climbSecondHandholds(player, object);
			break;
		case 99398:
			PrifddinasAgilityCourse.climbThirdHandholds(player, object);
			break;
		case 99399:
			PrifddinasAgilityCourse.grappleCliffFace(player, object);
			break;
		case 99380:
			PrifddinasAgilityCourse.descendCliff(player, object);
			break;
		case 99400:
			PrifddinasAgilityCourse.climbFourthHandholds(player, object);
			break;
		case 94059:
			if (player.getActionManager().getActionDelay() > 0)
				break;
			player.getActionManager().setAction(new PrifddinasHarp(object));
			break;
		case 94076:
			player.getDialogueManager().startDialogue("SimpleMessage",
					"Smithing Crystal items is currently not available; stay tuned!");
			break;
		default:
			return false;
		}
		return false;
	}

	/**
	 * Handles the actual Prifddinas Object interactions - Option 2.
	 * 
	 * @param player
	 *            The player interacting.
	 * @param object
	 *            The object interacting with.
	 * @return if is a handable Prifddinas object.
	 */
	public static boolean handleObjectOption2(Player player, WorldObject object) {
		switch (object.getId()) {
		case 92415: /** Dungeoneering Portal **/
			// TODO Start on Talsar and make players teleport there.
			player.getDialogueManager().startDialogue("SimpleMessage", "Talsar is currently not supported; "
					+ "make a suggestion on our ;;forums and we'll see what we can do about it!");
			break;
		case 92413: /** Clan Portal **/
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2972, 3289, 0));
			player.sendMessage("Clan Citadels are not yet supported; stay tuned!");
			break;
		case 2564: /** Elven Grimoire **/
			player.getDialogueManager().startDialogue("AltarSwapD");
			break;
		case 92692: /** Bank Chest **/
			player.getBank().openPlayerBank();
			break;
		case 94230: /** Summoning Obelisk **/
			int summonLevel = player.getSkills().getLevelForXp(Skills.SUMMONING);
			if (player.getSkills().getLevel(Skills.SUMMONING) < summonLevel) {
				player.lock(3);
				player.setNextAnimation(new Animation(8502));
				player.getSkills().set(Skills.SUMMONING, summonLevel);
				player.sendMessage("You've recharged your Summoning points.", true);
			} else
				player.sendMessage("You already have full Summoning points.");
			break;
		default:
			return false;
		}
		return false;
	}

	/**
	 * Handles the actual Prifddinas Object interactions - Option 3.
	 * 
	 * @param player
	 *            The player interacting.
	 * @param object
	 *            The object interacting with.
	 * @return if is a handable Prifddinas object.
	 */
	public static boolean handleObjectOption3(Player player, WorldObject object) {
		switch (object.getId()) {
		case 2564: /** Elven Grimoire **/
			player.getDialogueManager().startDialogue("AltarSwapD");
			break;
		default:
			return false;
		}
		return false;
	}

	/**
	 * Handles the actual Prifddinas object interactions - Item on Object.
	 * 
	 * @param player
	 *            The player interacting.
	 * @param object
	 *            The object interacting with.
	 * @param item
	 *            The item being used on the Object.
	 * @return if is a handable Prifddinas object.
	 */
	public static boolean handleItemOnObject(Player player, WorldObject object, Item item) {
		switch (object.getId()) {
		case 92885: /** Bonfire **/
		case 92903:
			Cookables cook = Cooking.isCookingSkill(item);
			if (Bonfire.addLog(player, object, item))
				return true;
			else if (cook != null) {
				player.getDialogueManager().startDialogue("CookingD", cook, object);
				return true;
			} else
				player.getDialogueManager().startDialogue("SimpleMessage", "You can't use that item on the fire.");
			break;
		case 92237: /** The Tears of Seren **/
			rechargeJewellery(player);
			break;
		case 94065: /** Well **/
			WaterFilling.isFilling(player, item.getId(), false);
			break;
		case 94066: /** Sand Pit **/
			if (item.getId() == 1925) {
				player.getActionManager().setAction(new SandBucketFillAction());
				return true;
			}
			player.sendMessage("You can not fill this item with sand.");
			break;
		default:
			return false;
		}
		return false;
	}

	/**
	 * Handles the actual Prifddinas NPC interactions - Option 1.
	 * 
	 * @param player
	 *            The player interacting.
	 * @param npc
	 *            The NPC interacting with.
	 * @return if is a handable Prifddinas NPC.
	 */
	public static boolean handleNPCOption1(Player player, final NPC npc) {
		if (npc.getId() >= 19852 && npc.getId() <= 19863) { /** All Heralds **/
			player.getDialogueManager().startDialogue("PrifddinasHerald", npc, (byte) 1);
			return true;
		}
		PickPocketableNPC pocket = PickPocketableNPC.get(npc.getId());
		if (pocket != null) {
			player.getActionManager().setAction(new PickPocketAction(npc, pocket));
			return true;
		}
		switch (npc.getId()) {
		case 19921:
			player.getDialogueManager().startDialogue("Ianto", npc.getId());
			break;
		case 19927:
			player.getDialogueManager().startDialogue("LadyTrahaearn", npc.getId());
			break;
		case 20106: /** 'Leprechaun' and 'Gardener' **/
		case 20107:
		case 20108:
		case 20109:
		case 20110:
		case 20111:
		case 20310:
		case 20311:
			player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
					"Don't worry, I'll look after your plants.");
			break;
		case 20294:
		case 20295:
			player.getDialogueManager().startDialogue("MwynenAndEssyllt", npc);
			break;
		case 19873:
			player.getDialogueManager().startDialogue("LordIorwerth", npc.getId());
			break;
		case 20112:
			player.getDialogueManager().startDialogue(SlayerMaster.startInteractionForId(player, npc.getId(), 1));
			break;
		case 19828:
			Dialogue dialogue = new Dialogue() {

				@Override
				public void start() {
					sendNPCDialogue(npc.getId(), CALM,
							"Hello there. " + "Can I tempt you with a slightly magical stick? "
									+ "I hear they're all the rage in Varrock!");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					switch (stage) {
					case -1:
						sendOptionsDialogue("Open Haf's Battlestaff shop?", "Sure.", " No thanks.");
						stage = 0;
						break;
					case 0:
						switch (componentId) {
						case OPTION_1:
							end();
							ShopsHandler.openShop(player, 24);
							break;
						default:
							end();
							break;
						}
						break;
					}
				}

				@Override
				public void finish() {
				}
			};
			player.getDialogueManager().startDialogue(dialogue);
			break;
		case 19882:
			player.getDialogueManager().startDialogue("LordCrwys", npc.getId());
			break;
		case 19883:
			dialogue = new Dialogue() {

				@Override
				public void start() {
					sendNPCDialogue(npc.getId(), CALM, "Hello there. Can I tempt you with some seeds?");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					switch (stage) {
					case -1:
						sendOptionsDialogue("Open Coeden's Seed Shop?", "Yes.", " No.");
						stage = 0;
						break;
					case 0:
						switch (componentId) {
						case OPTION_1:
							end();
							ShopsHandler.openShop(player, 25);
							break;
						default:
							end();
							break;
						}
						break;
					}
				}

				@Override
				public void finish() {
				}
			};
			player.getDialogueManager().startDialogue(dialogue);
			break;
		case 20270: /** Hefin Monk Store **/
		case 20271:
			ShopsHandler.openShop(player, 27);
			break;
		case 20272:
			player.getDialogueManager().startDialogue("LadyHefin", npc.getId());
			break;
		case 20230:
			player.getDialogueManager().startDialogue("Daffyd", npc.getId());
			break;
		case 19919: /** Rhobert Dail **/
			player.getDialogueManager().startDialogue("BobBarterD", npc.getId());
			break;
		case 19920: /** Dilwyn **/
			player.getDialogueManager().startDialogue("XuanD", npc.getId(), 1);
			break;
		case 19727:
			player.getDialogueManager().startDialogue("ElenAnterth", npc.getId());
			break;
		case 20275: /** Light Creature - Hefin's agil. course **/
			player.getDialogueManager().startDialogue("LightCreature");
			break;
		case 20471: /** Chronicle hand-in **/
			player.getDialogueManager().startDialogue("MayStormbrewerD", npc.getId(), true);
			break;
		case 21781: /** Waterfall fishing **/
			player.getDialogueManager().startDialogue("Mhistyll", npc.getId());
			break;
		case 20282: /** Lady Ithell **/
			player.getDialogueManager().startDialogue("LadyIthell", npc.getId());
			break;
		default:
			return false;
		}
		return false;
	}

	/**
	 * Handles the actual Prifddinas NPC interactions - Option 2.
	 * 
	 * @param player
	 *            The player interacting.
	 * @param npc
	 *            The NPC interacting with.
	 * @return if is a handable Prifddinas NPC.
	 */
	public static boolean handleNPCOption2(Player player, NPC npc) {
		if (npc.getId() >= 19852 && npc.getId() <= 19863) { /** All Heralds **/
			player.getDialogueManager().startDialogue("PrifddinasHerald", npc, (byte) 2);
			return true;
		}
		switch (npc.getId()) {
		case 19921:
			ShopsHandler.openShop(player, 23);
			break;
		case 20109:
		case 20110:
		case 20111:
		case 20311:
			ShopsHandler.openShop(player, 13);
			break;
		case 20106:
		case 20107:
		case 20108:
		case 20310:
			player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
					"Don't worry, I'll look after your plants.");
			break;
		case 20304:
			player.getDialogueManager().startDialogue("MusicianD", npc.getId());
			break;
		case 20112:
			player.getDialogueManager().startDialogue("KuradalGetTask", npc.getId());
			break;
		case 19828: /** Haf's Battlestaves **/
			ShopsHandler.openShop(player, 24);
			break;
		case 19883: /** Coeden's Seed Store **/
			ShopsHandler.openShop(player, 25);
			break;
		case 19920: /** Dilwyn **/
			player.getDialogueManager().startDialogue("XuanD", npc.getId(), 2);
			break;
		case 21781: /** Waterfall fishing **/
			player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
					"I do not have anything for sale right now.");
			break;
		case 20282: /** Lady Ithell - exhcnage option **/
			player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
					"Sadly I cannot store your crystal seeds yet, come back later.");
			break;
		default:
			return false;
		}
		return false;
	}

	/**
	 * Handles the actual Prifddinas NPC interactions - Option 3.
	 * 
	 * @param player
	 *            The player interacting.
	 * @param npc
	 *            The NPC interacting with.
	 * @return if is a handable Prifddinas NPC.
	 */
	public static boolean handleNPCOption3(Player player, NPC npc) {
		if (npc.getId() >= 19852 && npc.getId() <= 19863) { /** All Heralds **/
			player.getDialogueManager().startDialogue("PrifddinasHerald", npc, (byte) 3);
			return true;
		}
		switch (npc.getId()) {
		case 19919: /** Rhobert Dail **/
			BobBarter decanter = new BobBarter(player);
			player.getDialogueManager().startDialogue(new Dialogue() {

				@Override
				public void start() {
					decanter.decant();
					sendNPCDialogue(npc.getId(), CALM, "There ya go chum..");
					stage = 1;
				}

				@Override
				public void run(int interfaceId, int componentId) {
					switch (stage) {
					case 0:
						switch (componentId) {
						case OPTION_1:
						case OPTION_2:
							decanter.decant();
							sendNPCDialogue(npc.getId(), CALM, "There ya go chum..");
							stage = 1;
							break;
						case OPTION_3:
							finish();
							break;
						}
						break;
					case 1:
						finish();
						break;
					}
				}

				@Override
				public void finish() {
					player.getInterfaceManager().closeChatBoxInterface();
				}

			});
			break;
		case 19921: /** Ianto - Redeem Code **/
			player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
					"It seems you already have everything.");
			break;
		case 20109:
		case 20110:
		case 20111:
		case 20311:
			player.getDialogueManager().startDialogue("ToolLeprechaunTeleD");
			break;
		case 20112: /** Wythien **/
			DungeonRewardShop.openRewardShop(player);
			break;
		case 20286: /** Meilyr Clan Store **/
			ShopsHandler.openShop(player, 26);
			break;
		case 19727: /** Elen Anterth's Accomplishment Cape Store **/
			ShopsHandler.openShop(player, 52);
			break;
		default:
			return false;
		}
		return false;
	}

	/**
	 * Handles the actual Prifddinas NPC interactions - Option 4.
	 * 
	 * @param player
	 *            The player interacting.
	 * @param npc
	 *            The NPC interacting with.
	 * @return if is a handable Prifddinas NPC.
	 */
	public static boolean handleNPCOption4(Player player, NPC npc) {
		switch (npc.getId()) {
		case 19921:
			player.getDialogueManager().startDialogue("SimpleNPCMessage", npc.getId(),
					"There's nothing to redeem currently.");
			break;
		case 20112:
			SlayerTask.openSlayerShop(player);
			break;
		case 20284:

			break;
		default:
			return false;
		}
		return false;
	}

	/**
	 * Recharges all jewellery on 'The Tears of Seren' object.
	 * 
	 * @param player
	 *            The player interacting.
	 */
	private static void rechargeJewellery(Player player) {
		for (Item item : player.getInventory().getItems().getItems()) {
			if (item == null)
				continue;
			if (item.getId() >= 11120 && item.getId() <= 11126 && item.getId() % 2 == 0)
				item.setId(11118);
			else if (item.getId() >= 11107 && item.getId() <= 11113 && item.getId() % 2 != 0)
				item.setId(11105);
			else if (item.getId() >= 1704 && item.getId() <= 1710 && item.getId() % 2 == 0)
				item.setId(1712);
			else if (item.getId() == 2572)
				item.setId(20659);
			else if (item.getId() >= 20653 && item.getId() <= 20657 && item.getId() % 2 != 0)
				item.setId(20659);
			else {
				player.getDialogueManager().startDialogue("SimpleMessage", "You have nothing to recharge.");
				return;
			}
		}
		player.getInventory().refresh();
		player.getDialogueManager().startDialogue("SimpleMessage", "You've recharged all of your jewellery!");
	}
}