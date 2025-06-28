package com.rs.network.protocol.codec.decode;

import java.util.TimerTask;

import com.rs.Settings;
import com.rs.cores.CoresManager;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.TemporaryAtributtes.Key;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activites.duel.DuelArena;
import com.rs.game.activites.duel.DuelRules;
import com.rs.game.activites.soulwars.SoulWarsManager;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.map.bossInstance.BossInstanceHandler;
import com.rs.game.map.bossInstance.BossInstanceHandler.Boss;
import com.rs.game.npc.NPC;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.npc.familiar.Familiar.SpecialAttack;
import com.rs.game.npc.glacor.Glacor;
import com.rs.game.player.BanksManager.ExtraBank;
import com.rs.game.player.Equipment;
import com.rs.game.player.Equipment.Cosmetic;
import com.rs.game.player.Equipment.SavedCosmetic;
import com.rs.game.player.Inventory;
import com.rs.game.player.LendingManager;
import com.rs.game.player.LoginManager;
import com.rs.game.player.PetLootManager.RestrictedItem;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.CombinationPotions;
import com.rs.game.player.actions.PlayerFollow;
import com.rs.game.player.actions.firemaking.Firemaking;
import com.rs.game.player.actions.summoning.Summoning;
import com.rs.game.player.combat.PlayerCombat;
import com.rs.game.player.FarmingManager.FarmingSpot;
import com.rs.game.player.FarmingManager.SpotInfo;
import com.rs.game.player.content.ChatMessage;
import com.rs.game.player.content.Commands;
import com.rs.game.player.content.CosmeticsHandler;
import com.rs.game.player.content.DungeonRewardShop;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.AccountInterfaceManager;
import com.rs.game.player.content.ArtisansWorkShop;
import com.rs.game.player.content.LogicPacket;
import com.rs.game.player.content.Lottery;
import com.rs.game.player.content.Magic;
import com.rs.game.player.content.Profanity;
import com.rs.game.player.content.PublicChatMessage;
import com.rs.game.player.content.QuickChatMessage;
//import com.rs.game.player.content.ReferralHandler;
import com.rs.game.player.content.RouteEvent;
import com.rs.game.player.content.Shop;
import com.rs.game.player.content.SkillCapeCustomizer;
import com.rs.game.player.content.WellOfGoodWill;
import com.rs.game.player.content.ancientthrone.ThroneManager;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.construction.House;
import com.rs.game.player.content.construction.TabletMaking;
import com.rs.game.player.content.dungeoneering.skills.DungeoneeringSmithing;
import com.rs.game.player.content.dungeoneering.skills.DungeoneeringSummoning;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.player.controllers.DungeonController;/*
														import com.rs.game.player.controllers.InstancedPVPControler;*/
import com.rs.game.player.dialogue.impl.PetShopOwner;
import com.rs.game.route.RouteFinder;
import com.rs.game.route.strategy.FixedTileStrategy;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.network.Session;
import com.rs.network.protocol.codec.decode.impl.ButtonHandler;
import com.rs.network.protocol.codec.decode.impl.InventoryOptionsHandler;
import com.rs.network.protocol.codec.decode.impl.NPCHandler;
import com.rs.network.protocol.codec.decode.impl.ObjectHandler;
import com.rs.stream.InputStream;
import com.rs.stream.OutputStream;
import com.rs.utils.Colors;
import com.rs.utils.DisplayNames;
import com.rs.utils.Encrypt;
import com.rs.utils.IPMute;
import com.rs.utils.ItemExamines;
import com.rs.utils.Lend;
import com.rs.utils.Logger;
import com.rs.utils.LoggingSystem;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;
import com.rs.utils.huffman.Huffman;

/*import mysql.impl.NewsManager;*/

@SuppressWarnings("unused")
public final class WorldPacketsDecoder extends Decoder {

	/**
	 * The packet sizes
	 */
	private static final byte[] PACKET_SIZES = new byte[109];

	private final static int WALKING_PACKET = 8;
	private final static int MINI_WALKING_PACKET = 58;
	public final static int ACTION_BUTTON1_PACKET = 14;
	public final static int ACTION_BUTTON2_PACKET = 67;
	public final static int ACTION_BUTTON3_PACKET = 5;
	public final static int ACTION_BUTTON4_PACKET = 55;
	public final static int ACTION_BUTTON5_PACKET = 68;
	public final static int ACTION_BUTTON6_PACKET = 90;
	public final static int ACTION_BUTTON7_PACKET = 6;
	public final static int ACTION_BUTTON8_PACKET = 32;
	public final static int ACTION_BUTTON9_PACKET = 27;
	public final static int WORLD_MAP_CLICK = 38;
	public final static int ACTION_BUTTON10_PACKET = 96;
	public final static int RECEIVE_PACKET_COUNT_PACKET = 33;
	private final static int AFK_PACKET = 16;
	private final static int PLAYER_OPTION_3_PACKET = 77;
	private final static int PLAYER_OPTION_4_PACKET = 17;
	private final static int PLAYER_OPTION_6_PACKET = 49;
	private final static int MOVE_CAMERA_PACKET = 103;
	private final static int INTERFACE_ON_OBJECT = 37;
	private final static int CLICK_PACKET = -1;
	private final static int MOUVE_MOUSE_PACKET = -1;
	private final static int KEY_TYPED_PACKET = -1;
	private final static int CLOSE_INTERFACE_PACKET = 54;
	private final static int COMMANDS_PACKET = 60;
	private final static int INTERFACE_ON_INTERFACE = 3;
	private final static int IN_OUT_SCREEN_PACKET = -1;
	private final static int DONE_LOADING_REGION_PACKET = 30;
	private final static int PING_PACKET = 21;
	private final static int DISPLAY_PACKET = 98;
	private final static int CHAT_TYPE_PACKET = 83;
	private final static int CHAT_PACKET = 53;
	private final static int PUBLIC_QUICK_CHAT_PACKET = 86;
	private final static int ADD_FRIEND_PACKET = 89;
	private final static int ADD_IGNORE_PACKET = 4;
	private final static int REMOVE_IGNORE_PACKET = 73;
	private final static int JOIN_FRIEND_CHAT_PACKET = 36;
	private final static int CHANGE_FRIEND_CHAT_PACKET = 22;
	private final static int KICK_FRIEND_CHAT_PACKET = 74;
	private final static int KICK_CLAN_CHAT_PACKET = 92;
	private final static int REMOVE_FRIEND_PACKET = 24;
	private final static int SEND_FRIEND_MESSAGE_PACKET = 82;
	private final static int SEND_FRIEND_QUICK_CHAT_PACKET = 0;
	private final static int OBJECT_CLICK1_PACKET = 26;
	private final static int OBJECT_CLICK2_PACKET = 59;
	private final static int OBJECT_CLICK3_PACKET = 40;
	private final static int OBJECT_CLICK4_PACKET = 23;
	private final static int OBJECT_CLICK5_PACKET = 80;
	private final static int OBJECT_EXAMINE_PACKET = 25;
	private final static int NPC_CLICK1_PACKET = 31;
	private final static int NPC_CLICK2_PACKET = 101;
	private final static int NPC_CLICK3_PACKET = 34;
	private final static int NPC_CLICK4_PACKET = 65;
	private final static int ATTACK_NPC = 20;
	private final static int PLAYER_OPTION_1_PACKET = 42;
	private final static int PLAYER_OPTION_2_PACKET = 46;
	private static final int PLAYER_OPTION_5_PACKET = 77;// TODO FIND THIS LATER
	private final static int PLAYER_OPTION_9_PACKET = 56;
	private final static int ITEM_TAKE_PACKET = 57;
	private final static int GROUND_ITEM_OPTION_2_PACKET = 62;
	private final static int GROUND_ITEM_OPTION_EXAMINE = 102;
	private final static int DIALOGUE_CONTINUE_PACKET = 72;
	private final static int ENTER_INTEGER_PACKET = 81;
	private final static int ENTER_NAME_PACKET = 29;
	private final static int ENTER_LONG_TEXT_PACKET = 48;
	private final static int SWITCH_INTERFACE_COMPONENTS_PACKET = 76;
	private final static int INTERFACE_ON_PLAYER = 50;
	private final static int INTERFACE_ON_NPC = 66;
	private final static int COLOR_ID_PACKET = 97;
	private static final int NPC_EXAMINE_PACKET = 9;
	private static final int FORUM_THREAD_ID_PACKET = 18;
	private final static int OPEN_URL_PACKET = 91;
	private final static int REPORT_ABUSE_PACKET = 11;
	private final static int GRAND_EXCHANGE_ITEM_SELECT_PACKET = 71;
	private final static int WORLD_LIST_UPDATE = 87;
	private final static int UPDATE_GAMEBAR_PACKET = 79;
	private final static int DROP_ITEM_PACKET = 104;
	public final static int ESCAPE_PRESSED_PACKET = 105;

	static {
		loadPacketSizes();
	}

	public static void decodeLogicPacket(final Player player, LogicPacket packet) {
		int packetId = packet.getId();
		InputStream stream = new InputStream(packet.getData());
		if (packetId == NPC_CLICK4_PACKET) {
			NPCHandler.handleOption4(player, stream);
			return;
		}
		if (packetId == WALKING_PACKET || packetId == MINI_WALKING_PACKET) {
			if (!player.isActive() || !player.clientHasLoadedMapRegion() || player.isDead() || player.isLocked())
				return;
			if (player.getFreezeDelay() >= Utils.currentTimeMillis()) {
				player.sendMessage("A magical force prevents you from moving.", true);
				return;
			}
			int length = stream.getLength();
			int baseX = stream.readUnsignedShort128();
			boolean forceRun = stream.readUnsigned128Byte() == 1;
			int baseY = stream.readUnsignedShort128();
			int steps = (length - 5) / 2;
			if (steps > 25)
				steps = 25;
			player.stopAll();
			if (forceRun)
				player.setRun(forceRun);

			if (steps > 0) {
				int x = 0, y = 0;
				for (int step = 0; step < steps; step++) {
					x = baseX + stream.readUnsignedByte();
					y = baseY + stream.readUnsignedByte();
				}

				steps = RouteFinder.findRoute(RouteFinder.WALK_ROUTEFINDER, player.getX(), player.getY(),
						player.getPlane(), player.getSize(), new FixedTileStrategy(x, y), true);
				int[] bufferX = RouteFinder.getLastPathBufferX();
				int[] bufferY = RouteFinder.getLastPathBufferY();
				int last = -1;
				for (int i = steps - 1; i >= 0; i--) {
					if (!player.addWalkSteps(bufferX[i], bufferY[i], 25, true))
						break;
					last = i;
				}

				if (last != -1) {
					WorldTile tile = new WorldTile(bufferX[last], bufferY[last], player.getPlane());
					player.getPackets().sendMinimapFlag(
							tile.getLocalX(player.getLastLoadedMapRegionTile(), player.getMapSize()),
							tile.getLocalY(player.getLastLoadedMapRegionTile(), player.getMapSize()));
				} else {
					player.getPackets().sendResetMinimapFlag();
				}
			}
		} else if (packetId == OBJECT_CLICK1_PACKET) {
			ObjectHandler.handleOption(player, stream, 1);
		} else if (packetId == OBJECT_CLICK2_PACKET) {
			ObjectHandler.handleOption(player, stream, 2);
		} else if (packetId == OBJECT_CLICK3_PACKET) {
			ObjectHandler.handleOption(player, stream, 3);
		} else if (packetId == OBJECT_CLICK4_PACKET) {
			ObjectHandler.handleOption(player, stream, 4);
		} else if (packetId == OBJECT_CLICK5_PACKET) {
			ObjectHandler.handleOption(player, stream, 5);
		} else if (packetId == INTERFACE_ON_PLAYER) {
			if (!player.isActive() || !player.clientHasLoadedMapRegion() || player.isDead()) {
				return;
			}
			if (player.getLockDelay() > Utils.currentTimeMillis()) {
				return;
			}
			final int itemId = stream.readUnsignedShort();
			int playerIndex = stream.readUnsignedShort();
			int interfaceHash = stream.readIntV2();
			int interfaceSlot = stream.readUnsignedShortLE128();
			final boolean forceRun = stream.read128Byte() == 1;
			int interfaceId = interfaceHash >> 16;
			int componentId = interfaceHash - (interfaceId << 16);
			if (Utils.getInterfaceDefinitionsSize() <= interfaceId) {
				return;
			}
			if (!player.getInterfaceManager().containsInterface(interfaceId)) {
				return;
			}
			if (componentId == 65535) {
				componentId = -1;
			}
			if (componentId != -1 && Utils.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId) {
				return;
			}
			Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2.isDead() || p2.hasFinished()
					|| !player.getMapRegionsIds().contains(p2.getRegionId())) {
				return;
			}
			player.stopAll(false);
			if (interfaceId == 679) {
				if (!player.getControlerManager().processItemOnPlayer(p2, itemId)) {
					return;
				}
				Item item = player.getInventory().getItems().get(interfaceSlot);
				if (item == null) {
					return;
				}
				if (LendingManager.isLendedItem(player, item)) {
					Lend lend = LendingManager.getLend(player);
					if (lend == null) {
						return;
					}
					if (!lend.getLender().equals(p2.getUsername())) {
						player.sendMessage("You can't give your lent item to a stranger...");
						return;
					}
					player.getDialogueManager().startDialogue("LendReturn", lend);
					return;
				}
				InventoryOptionsHandler.handleItemOnPlayer(player, p2, itemId);
			}
			switch (interfaceId) {
			case 1110:
				if (componentId == 87) {
					ClansManager.invite(player, p2);
				}
				break;
			case 662:
			case 747:
				if (player.getFamiliar() == null)
					return;
				player.resetWalkSteps();
				if ((interfaceId == 747 && componentId == 15) || (interfaceId == 662 && componentId == 65)
						|| (interfaceId == 662 && componentId == 74) || interfaceId == 747 && componentId == 18) {
					if ((interfaceId == 662 && componentId == 74 || interfaceId == 747 && componentId == 24
							|| interfaceId == 747 && componentId == 18)) {
						if (player.getFamiliar().getSpecialAttack() != SpecialAttack.ENTITY)
							return;
					}
					if (!player.isCanPvp() || !p2.isCanPvp()) {
						player.getPackets().sendGameMessage("You can only attack players in a player-vs-player area.");
						return;
					}
					if (!player.getFamiliar().canAttack(p2)) {
						player.getPackets().sendGameMessage("You can only use your familiar in a multi-zone area.");
						return;
					} else {
						player.getFamiliar().setSpecial(
								interfaceId == 662 && componentId == 74 || interfaceId == 747 && componentId == 18);
						player.getFamiliar().setTarget(p2);
					}
				}
				break;
			case 193:
				switch (componentId) {
				case 28:
				case 32:
				case 24:
				case 20:
				case 30:
				case 34:
				case 26:
				case 22:
				case 29:
				case 33:
				case 25:
				case 21:
				case 31:
				case 35:
				case 27:
				case 23:
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						player.setNextFaceWorldTile(new WorldTile(p2.getCoordFaceX(p2.getSize()),
								p2.getCoordFaceY(p2.getSize()), p2.getPlane()));
						if (!player.getControlerManager().canAttack(p2))
							return;
						if (!player.isCanPvp() || !p2.isCanPvp()) {
							player.getPackets()
									.sendGameMessage("You can only attack players in a player-vs-player area.");
							return;
						}
						if (!p2.isAtMultiArea() || !player.isAtMultiArea()) {
							if (player.getAttackedBy() != p2
									&& player.getAttackedByDelay() > Utils.currentTimeMillis()) {
								player.getPackets().sendGameMessage(
										"That " + (player.getAttackedBy() instanceof Player ? "player" : "npc")
												+ " is already in combat.",
										true);
								return;
							}
							if (p2.getAttackedBy() != player && p2.getAttackedByDelay() > Utils.currentTimeMillis()) {
								if (p2.getAttackedBy() instanceof NPC) {
									p2.setAttackedBy(player);
								} else {
									player.getPackets().sendGameMessage("That player is already in combat.", true);
									return;
								}
							}
						}
						player.getActionManager().setAction(new PlayerCombat(p2));
					}
					break;
				}
			case 430:
				switch (componentId) {
				case 42:
					player.setNextFaceWorldTile(new WorldTile(p2.getCoordFaceX(p2.getSize()),
							p2.getCoordFaceY(p2.getSize()), p2.getPlane()));
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						if (p2 instanceof Player) {
							if (player.getSkills().getLevel(Skills.MAGIC) < 93) {
								player.getPackets()
										.sendGameMessage("Your Magic level is not high enough for this spell.");
								return;
							} else if (player.getSkills().getLevel(Skills.DEFENCE) < 40) {
								player.getPackets().sendGameMessage("You need a Defence level of 40 for this spell");
								return;
							}
							Long lastVeng = (Long) p2.getTemporaryAttributtes().get("LAST_VENG");
							if (lastVeng != null && lastVeng + 30000 > Utils.currentTimeMillis()) {
								player.getPackets().sendGameMessage("That player already has vengeance.");
								return;
							}
							player.stopAll();
							player.setNextAnimation(new Animation(4411));
							p2.setNextGraphics(new Graphics(725, 0, 100));
							p2.setCastVeng(true);
							p2.getTemporaryAttributtes().put("LAST_VENG", Utils.currentTimeMillis());
							p2.getPackets()
									.sendGameMessage("You recieve vengeance from " + player.getDisplayName() + ".");
						}
					}
					break;
				}
			case 192:
				switch (componentId) {
				case 98:
				case 25: // air strike
				case 28: // water strike
				case 30: // earth strike
				case 32: // fire strike
				case 34: // air bolt
				case 39: // water bolt
				case 42: // earth bolt
				case 45: // fire bolt
				case 49: // air blast
				case 52: // water blast
				case 58: // earth blast
				case 63: // fire blast
				case 70: // air wave
				case 73: // water wave
				case 77: // earth wave
				case 80: // fire wave
				case 86: // teleblock
				case 84: // air surge
				case 87: // water surge
				case 89: // earth surge
				case 91: // fire surge
				case 99: // storm of armadyl
				case 36: // bind
				case 66: // Sara Strike
				case 67: // Guthix Claws
				case 68: // Flame of Zammy
				case 55: // snare
				case 81: // entangle
				case 56: // magic dart
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						player.setNextFaceWorldTile(new WorldTile(p2.getCoordFaceX(p2.getSize()),
								p2.getCoordFaceY(p2.getSize()), p2.getPlane()));
						if (!player.getControlerManager().canAttack(p2))
							return;
						if (!player.isCanPvp() || !p2.isCanPvp()) {
							player.getPackets()
									.sendGameMessage("You can only attack players in a player-vs-player area.");
							return;
						}
						if (!p2.isAtMultiArea() || !player.isAtMultiArea()) {
							if (player.getAttackedBy() != p2
									&& player.getAttackedByDelay() > Utils.currentTimeMillis()) {
								player.getPackets().sendGameMessage(
										"That " + (player.getAttackedBy() instanceof Player ? "player" : "npc")
												+ " is already in combat.",
										true);
								return;
							}
							if (p2.getAttackedBy() != player && p2.getAttackedByDelay() > Utils.currentTimeMillis()) {
								if (p2.getAttackedBy() instanceof NPC) {
									p2.setAttackedBy(player);
								} else {
									player.getPackets().sendGameMessage("That player is already in combat.", true);
									return;
								}
							}
						}
						player.getActionManager().setAction(new PlayerCombat(p2));
					}
					break;
				}
				break;
			}
		} else if (packetId == INTERFACE_ON_NPC) {
			if (!player.isActive() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			if (player.getLockDelay() > Utils.currentTimeMillis())
				return;
			boolean forceRun = stream.readByte() == 1;
			int interfaceHash = stream.readInt();
			int npcIndex = stream.readUnsignedShortLE();
			int interfaceSlot = stream.readUnsignedShortLE128();
			int itemId = stream.readUnsignedShortLE();
			int interfaceId = interfaceHash >> 16;
			int componentId = interfaceHash - (interfaceId << 16);
			if (Utils.getInterfaceDefinitionsSize() <= interfaceId)
				return;
			if (!player.getInterfaceManager().containsInterface(interfaceId))
				return;
			if (componentId == 65535)
				componentId = -1;
			if (componentId != -1 && Utils.getInterfaceDefinitionsComponentsSize(interfaceId) <= componentId)
				return;
			NPC npc = World.getNPCs().get(npcIndex);
			if (npc == null || npc.isDead() || npc.hasFinished()
					|| !player.getMapRegionsIds().contains(npc.getRegionId()))
				return;
			player.stopAll(false);
			if (interfaceId != Inventory.INVENTORY_INTERFACE) {
				if (!npc.getDefinitions().hasAttackOption()) {
					player.getPackets().sendGameMessage("You can't attack this npc.");
					return;
				}
			}
			switch (interfaceId) {
			case Inventory.INVENTORY_INTERFACE:
				Item item = player.getInventory().getItem(interfaceSlot);
				if (item == null || !player.getControlerManager().processItemOnNPC(npc, item))
					return;
				InventoryOptionsHandler.handleItemOnNPC(player, npc, item);
				break;
			case 1165:
				Summoning.attackDreadnipTarget(npc, player);
				break;
			case 662:
			case 747:
				if (player.getFamiliar() == null)
					return;

				player.resetWalkSteps();
				if ((interfaceId == 747 && componentId == 15) || (interfaceId == 662 && componentId == 65)
						|| (interfaceId == 662 && componentId == 74) || interfaceId == 747 && componentId == 18
						|| interfaceId == 747 && componentId == 24) {
					if ((interfaceId == 662 && componentId == 74 || interfaceId == 747 && componentId == 18)) {
						if (player.getFamiliar().getSpecialAttack() != SpecialAttack.ENTITY)
							return;
					}
					if (npc instanceof Familiar) {
						Familiar familiar = (Familiar) npc;
						if (familiar == player.getFamiliar()) {
							player.getPackets().sendGameMessage("You can't attack your own familiar.");
							return;
						}
						if (!player.getFamiliar().canAttack(familiar.getOwner())) {
							player.getPackets()
									.sendGameMessage("You can only attack players in a player-vs-player area.");
							return;
						}
					}
					if (!player.getFamiliar().canAttack(npc)) {
						player.getPackets().sendGameMessage("You can only use your familiar in a multi-zone area.");
						return;
					} else {
						player.getFamiliar().setSpecial(
								interfaceId == 662 && componentId == 74 || interfaceId == 747 && componentId == 18);
						player.getFamiliar().setTarget(npc);
					}
				}
				break;
			case 193:
				switch (componentId) {
				case 28:
				case 32:
				case 24:
				case 20:
				case 30:
				case 34:
				case 26:
				case 22:
				case 29:
				case 33:
				case 25:
				case 21:
				case 31:
				case 35:
				case 27:
				case 23:
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						player.setNextFaceWorldTile(new WorldTile(npc.getCoordFaceX(npc.getSize()),
								npc.getCoordFaceY(npc.getSize()), npc.getPlane()));
						if (!player.getControlerManager().canAttack(npc))
							return;
						if (npc instanceof Familiar) {
							Familiar familiar = (Familiar) npc;
							if (familiar == player.getFamiliar()) {
								player.getPackets().sendGameMessage("You can't attack your own familiar.");
								return;
							}
							if (!familiar.canAttack(player)) {
								player.getPackets().sendGameMessage("You can't attack this npc.");
								return;
							}
						} else if (!npc.isForceMultiAttacked()) {
							if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
								if (player.getAttackedBy() != npc
										&& player.getAttackedByDelay() > Utils.currentTimeMillis()) {
									player.getPackets().sendGameMessage("You are already in combat.", true);
									return;
								}
								if (npc.getAttackedBy() != player
										&& npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
									player.getPackets().sendGameMessage("This npc is already in combat.", true);
									return;
								}
							}
						}
						player.getActionManager().setAction(new PlayerCombat(npc));
					}
					break;
				}
			case 192:
				switch (componentId) {
				case 25: // air strike
				case 28: // water strike
				case 30: // earth strike
				case 32: // fire strike
				case 34: // air bolt
				case 39: // water bolt
				case 42: // earth bolt
				case 45: // fire bolt
				case 49: // air blast
				case 52: // water blast
				case 58: // earth blast
				case 63: // fire blast
				case 70: // air wave
				case 73: // water wave
				case 77: // earth wave
				case 80: // fire wave
				case 84: // air surge
				case 87: // water surge
				case 89: // earth surge
				case 66: // Sara Strike
				case 67: // Guthix Claws
				case 68: // Flame of Zammy
				case 93:
				case 91: // fire surge
				case 99: // storm of Armadyl
				case 36: // bind
				case 55: // snare
				case 81: // entangle
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						player.setNextFaceWorldTile(new WorldTile(npc.getCoordFaceX(npc.getSize()),
								npc.getCoordFaceY(npc.getSize()), npc.getPlane()));
						if (!player.getControlerManager().canAttack(npc))
							return;
						if (npc instanceof Familiar) {
							Familiar familiar = (Familiar) npc;
							if (familiar == player.getFamiliar()) {
								player.getPackets().sendGameMessage("You can't attack your own familiar.");
								return;
							}
							if (!familiar.canAttack(player)) {
								player.getPackets().sendGameMessage("You can't attack this npc.");
								return;
							}
						} else if (!npc.isForceMultiAttacked()) {
							if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
								if (player.getAttackedBy() != npc
										&& player.getAttackedByDelay() > Utils.currentTimeMillis()) {
									player.getPackets().sendGameMessage("You are already in combat.", true);
									return;
								}
								if (npc.getAttackedBy() != player
										&& npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
									player.getPackets().sendGameMessage("This npc is already in combat.", true);
									return;
								}
							}
						}
						player.getActionManager().setAction(new PlayerCombat(npc));
					}
					break;
				}
				break;
			case 950:
				switch (componentId) {
				case 25:
				case 27:
				case 28:
				case 30:
				case 32: // air bolt
				case 36: // water bolt
				case 37: // earth bolt
				case 41: // fire bolt
				case 42: // air blast
				case 43: // water blast
				case 45: // earth blast
				case 47: // fire blast
				case 48: // air wave
				case 49: // water wave
				case 54: // earth wave
				case 58: // fire wave
				case 61:// air surge
				case 62:// water surge
				case 63:// earth surge
				case 67:// fire surge
				case 34:// bind
				case 44:// snare
				case 59:// entangle
					if (Magic.checkCombatSpell(player, componentId, 1, false)) {
						player.setNextFaceWorldTile(new WorldTile(npc.getCoordFaceX(npc.getSize()),
								npc.getCoordFaceY(npc.getSize()), npc.getPlane()));
						if (!player.getControlerManager().canAttack(npc))
							return;
						if (npc instanceof Familiar) {
							Familiar familiar = (Familiar) npc;
							if (familiar == player.getFamiliar()) {
								player.getPackets().sendGameMessage("You can't attack your own familiar.");
								return;
							}
							if (!familiar.canAttack(player)) {
								player.getPackets().sendGameMessage("You can't attack this npc.");
								return;
							}
						} else if (!npc.isForceMultiAttacked()) {
							if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
								if (player.getAttackedBy() != npc
										&& player.getAttackedByDelay() > Utils.currentTimeMillis()) {
									player.getPackets().sendGameMessage("You are already in combat.", true);
									return;
								}
								if (npc.getAttackedBy() != player
										&& npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
									player.getPackets().sendGameMessage("This npc is already in combat.", true);
									return;
								}
							}
						}
						player.getActionManager().setAction(new PlayerCombat(npc));
					}
					break;
				}
				break;
			}
			if (Settings.DEBUG)
				System.out.println("Spell:" + componentId);
		} else if (packetId == ATTACK_NPC) {
			if (!player.isActive() || !player.clientHasLoadedMapRegion() || player.isDead()
					|| player.getLockDelay() > Utils.currentTimeMillis()) {
				return;
			}
			int entityIndex = stream.readUnsignedShort128();
			boolean forceRun = stream.read128Byte() == 1;
			if (forceRun)
				player.setRun(forceRun);
			NPC npc = World.getNPCs().get(entityIndex);
			if (npc == null || npc.isDead() || npc.hasFinished()
					|| !player.getMapRegionsIds().contains(npc.getRegionId())
					|| !npc.getDefinitions().hasAttackOption()) {
				return;
			}
			if (!player.getControlerManager().canAttack(npc)) {
				return;
			}
			if (npc instanceof Familiar) {
				Familiar familiar = (Familiar) npc;
				if (familiar == player.getFamiliar()) {
					player.getPackets().sendGameMessage("You can't attack your own familiar.");
					return;
				}
				if (!familiar.canAttack(player)) {
					player.getPackets().sendGameMessage("You can't attack this npc.");
					return;
				}
			} else if (!npc.isForceMultiAttacked()) {
				if (!npc.isAtMultiArea() || !player.isAtMultiArea()) {
					if (player.getAttackedBy() != npc && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
						player.getPackets().sendGameMessage("You are already in combat.", true);
						return;
					}
					if (npc.getAttackedBy() != player && npc.getAttackedByDelay() > Utils.currentTimeMillis()) {
						player.getPackets().sendGameMessage("This npc is already in combat.", true);
						return;
					}
				}
			}
			player.stopAll(false);
			player.getActionManager().setAction(new PlayerCombat(npc));
		}
		if (packetId == NPC_CLICK1_PACKET) {
			NPCHandler.handleOption1(player, stream);
			return;
		}
		if (packetId == NPC_CLICK2_PACKET) {
			NPCHandler.handleOption2(player, stream);
			return;
		}
		if (packetId == NPC_CLICK3_PACKET) {
			NPCHandler.handleOption3(player, stream);
			return;
		}
		if (packetId == INTERFACE_ON_OBJECT) {// alright should be all good
			boolean forceRun = stream.readByte128() == 1;
			int itemId = stream.readUnsignedShortLE128();
			int y = stream.readShortLE128();
			int objectId = stream.readIntV2();
			int interfaceHash = stream.readInt();
			final int interfaceId = interfaceHash >> 16;
			int componentId = interfaceHash - (interfaceId << 16);
			int slot = stream.readShortLE();
			int x = stream.readShort128();
			if (!player.isActive() || !player.clientHasLoadedMapRegion() || player.isDead()
					|| Utils.getInterfaceDefinitionsSize() <= interfaceId
					|| !player.getInterfaceManager().containsInterface(interfaceId) || player.isDead()
					|| player.isLocked() || player.getEmotesManager().isDoingEmote())
				return;
			final WorldTile tile = new WorldTile(x, y, player.getPlane());
			if (!player.getMapRegionsIds().contains(tile.getRegionId()))
				return;
			WorldObject mapObject = World.getObjectWithId(tile, objectId);
			if (mapObject == null || mapObject.getId() != objectId)
				return;
			final WorldObject object = !player.isAtDynamicRegion() ? mapObject
					: new WorldObject(objectId, mapObject.getType(), mapObject.getRotation(), x, y, player.getPlane());
			final Item item = player.getInventory().getItem(slot);
			if (item == null || item.getId() != itemId)
				return;
			player.stopAll(false); // false
			if (forceRun)
				player.setRun(forceRun);
			switch (interfaceId) {
			case Inventory.INVENTORY_INTERFACE: // inventory
				ObjectHandler.handleItemOnObject(player, object, interfaceId, item);
				break;
			case 430:// lunars
				switch (componentId) {
				case 55:
					if (player.getSkills().getLevel(Skills.MAGIC) < 66) {
						player.getPackets().sendGameMessage("You need a level of 65 in order to cast Cure Plant.");
						return;
					}
					if (!Magic.checkRunes(player, true, Magic.ASTRAL_RUNE, 1, Magic.EARTH_RUNE, 8))
						return;
					final FarmingSpot spot = player.getFarmingManager().getSpot(SpotInfo.getInfo(object.getId()));
					if (spot == null || spot.isDead()) {
						player.getPackets().sendGameMessage("This cannot be cured.");
						return;
					} else if (!spot.isDiseased()) {
						player.getPackets().sendGameMessage("Your patch is not diseased.");
						return;
					}
					player.lock(3);
					WorldTasksManager.schedule(new WorldTask() {

						@Override
						public void run() {
							spot.setDiseased(false);
							spot.refresh();
						}
					}, 2);
					player.getSkills().addXp(Skills.MAGIC, 60);
					player.setNextGraphics(new Graphics(742, 0, 150));
					player.setNextAnimation(new Animation(4409));
					player.getPackets().sendGameMessage("You cast the spell and your patch is in perfect health.");
					break;
				}
				break;
			}
		} else if (packetId == PLAYER_OPTION_1_PACKET) {
			if (!player.isActive() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			boolean forceRun = stream.readUnsignedByte() == 1;
			int playerIndex = stream.readUnsignedShortLE128();
			Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
				return;
			if (player.getLockDelay() > Utils.currentTimeMillis() || !player.getControlerManager().canPlayerOption1(p2))
				return;
			if (player.getControlerManager().getControler() == null) {
				player.faceEntity(p2);
				player.setRouteEvent(new RouteEvent(p2, new Runnable() {
					@Override
					public void run() {
						player.stopAll(false);
						if (player.getInterfaceManager().containsScreenInter() || player.isLocked()) {
							player.getPackets().sendGameMessage("You're too busy.");
							return;
						}
						if (p2.getInterfaceManager().containsScreenInter() || p2.isLocked()) {
							player.getPackets().sendGameMessage(p2.getDisplayName() + " is too busy.");
							return;
						}
						if (p2.getControlerManager().getControler() != null) {
							player.getPackets().sendGameMessage(p2.getDisplayName() + " is too busy.");
							return;
						}
						if (p2.getTemporaryAttributtes().get("DuelChallenged") == player) {
							player.getControlerManager().removeControlerWithoutCheck();
							p2.getControlerManager().removeControlerWithoutCheck();
							p2.getTemporaryAttributtes().remove("DuelChallenged");
							player.setLastDuelRules(new DuelRules(player, p2));
							p2.setLastDuelRules(new DuelRules(p2, player));

							player.getControlerManager().startControler("DuelArena", p2,
									p2.getTemporaryAttributtes().get("DuelFriendly"), true);
							p2.getControlerManager().startControler("DuelArena", player,
									p2.getTemporaryAttributtes().remove("DuelFriendly"), true);
							return;
						}
						player.getTemporaryAttributtes().put("DuelTarget", p2);
						player.getInterfaceManager().sendInterface(640);
						player.getTemporaryAttributtes().put("WillDuelFriendly", true);
						player.getVarBitManager().sendVar(283, 67108864);
					}
				}));
				return;
			}
			if (!player.isCanPvp())
				return;
			if (!player.getControlerManager().canAttack(p2))
				return;
			if (!player.isCanPvp() || !p2.isCanPvp()) {
				player.getPackets().sendGameMessage("You can only attack players in a player-vs-player area.");
				return;
			}
			if (!p2.isAtMultiArea() || !player.isAtMultiArea()) {
				if (player.getAttackedBy() != p2 && player.getAttackedByDelay() > Utils.currentTimeMillis()) {
					player.getPackets().sendGameMessage("You are already in combat.", true);
					return;
				}
				if (p2.getAttackedBy() != player && p2.getAttackedByDelay() > Utils.currentTimeMillis()) {
					if (p2.getAttackedBy() instanceof NPC) {
						p2.setAttackedBy(player); // changes enemy to player,
						// player has priority over
						// npc on single areas
					} else {
						player.getPackets().sendGameMessage("That player is already in combat.", true);
						return;
					}
				}
			}
			player.stopAll(false);
			player.getActionManager().setAction(new PlayerCombat(p2));
		} else if (packetId == PLAYER_OPTION_2_PACKET) {
			if (!player.isActive() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			boolean forceRun = stream.readUnsignedByte() == 1;
			int playerIndex = stream.readUnsignedShortLE128();
			Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId()))
				return;
			if (player.getLockDelay() > Utils.currentTimeMillis())
				return;
			if (player.getControlerManager().getControler() instanceof DuelArena)
				return;
			player.stopAll(false);
			player.getActionManager().setAction(new PlayerFollow(p2));
		} else if (packetId == AFK_PACKET) {
			// Useless
		} else if (packetId == PLAYER_OPTION_4_PACKET) {
			boolean unknown = stream.readByte() == 1;
			int playerIndex = stream.readUnsignedShortLE128();
			final Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId())
					|| player.getLockDelay() >= Utils.currentTimeMillis() || player == p2)
				return;
			player.stopAll(false);

			if (player.getX() == p2.getX() && player.getY() == p2.getY()) {
				if (!player.addWalkSteps(player.getX() - 1, player.getY(), 1))
					if (!player.addWalkSteps(player.getX() + 1, player.getY(), 1))
						if (!player.addWalkSteps(player.getX(), player.getY() + 1, 1))
							player.addWalkSteps(player.getX(), player.getY() - 1, 1);
			}
			if (!p2.withinDistance(player, 14)) {
				player.sendMessage("Unable to find " + p2.getDisplayName() + "; must be within radius.");
				return;
			}
			player.setRouteEvent(new RouteEvent(p2, new Runnable() {
				@Override
				public void run() {
					if (!player.canTrade(p2))
						return;
					if (p2.getTemporaryAttributtes().get("TradeTarget") == player) {
						p2.getTemporaryAttributtes().remove("TradeTarget");
						player.getTrade().openTrade(p2);
						p2.getTrade().openTrade(player);
						p2.faceEntity(player);
						player.faceEntity(p2);
						return;
					}
					player.getTemporaryAttributtes().put("TradeTarget", p2);
					player.sendMessage("Sending " + p2.getDisplayName() + " a trade request...");
					p2.getPackets().sendTradeRequestMessage(player);
					player.faceEntity(p2);
				}
			}));
		} else if (packetId == PLAYER_OPTION_5_PACKET) {
			boolean unknown = stream.readByte() == 1;
			int playerIndex = stream.readUnsignedShortLE128();
			Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId())
					|| player.getLockDelay() >= Utils.currentTimeMillis() || player == p2)
				return;
			if (!p2.withinDistance(player, 14)) {
				player.sendMessage("Unable to find " + p2.getDisplayName() + ".");
				return;
			}
			if (player.getAttackedByDelay() + 5000 > Utils.currentTimeMillis()) {
				player.sendMessage("You can't examine " + p2.getDisplayName()
						+ "'s stats until 5 seconds after the end of combat.");
				return;
			}
			player.getInterfaceManager().sendInterface(1314);
			player.getPackets().sendIComponentText(1314, 93, Colors.cyan + p2.getDisplayName() + "'s stats");
			player.getPackets().sendIComponentText(1314, 92, "Mode: " + Colors.red + p2.getXPMode() + " </col>- Time played: "
					+ Utils.getTimePlayed(p2.getTimePlayed()));
			player.getPackets().sendIComponentText(1314, 63, "" + p2.getSkills().getLevel(0)); // attack
			player.getPackets().sendIComponentText(1314, 64, "" + p2.getSkills().getLevel(2)); // strength
			player.getPackets().sendIComponentText(1314, 65, "" + p2.getSkills().getLevel(1)); // defence
			player.getPackets().sendIComponentText(1314, 67, "" + p2.getSkills().getLevel(4)); // range
			player.getPackets().sendIComponentText(1314, 68, "" + p2.getSkills().getLevel(5)); // prayer
			player.getPackets().sendIComponentText(1314, 66, "" + p2.getSkills().getLevel(6)); // magic
			player.getPackets().sendIComponentText(1314, 80, "" + p2.getSkills().getLevel(20)); // runecrafting
			player.getPackets().sendIComponentText(1314, 85, "" + p2.getSkills().getLevel(22)); // construction
			player.getPackets().sendIComponentText(1314, 78, "" + p2.getSkills().getLevel(24)); // dungeoneering
			player.getPackets().sendIComponentText(1314, 86, "" + p2.getSkills().getLevel(3)); // hitpoints
			player.getPackets().sendIComponentText(1314, 87, "" + p2.getSkills().getLevel(16)); // agility
			player.getPackets().sendIComponentText(1314, 88, "" + p2.getSkills().getLevel(15)); // herblore
			player.getPackets().sendIComponentText(1314, 84, "" + p2.getSkills().getLevel(17)); // thieving
			player.getPackets().sendIComponentText(1314, 72, "" + p2.getSkills().getLevel(12)); // crafting
			player.getPackets().sendIComponentText(1314, 89, "" + p2.getSkills().getLevel(9)); // fletching
			player.getPackets().sendIComponentText(1314, 79, "" + p2.getSkills().getLevel(18)); // slayer
			player.getPackets().sendIComponentText(1314, 81, "" + p2.getSkills().getLevel(21)); // hunter
			player.getPackets().sendIComponentText(1314, 70, "" + p2.getSkills().getLevel(14)); // mining
			player.getPackets().sendIComponentText(1314, 71, "" + p2.getSkills().getLevel(13)); // smithin
			player.getPackets().sendIComponentText(1314, 76, "" + p2.getSkills().getLevel(10)); // fishing
			player.getPackets().sendIComponentText(1314, 77, "" + p2.getSkills().getLevel(7)); // cooking
			player.getPackets().sendIComponentText(1314, 75, "" + p2.getSkills().getLevel(11)); // firemaking
			player.getPackets().sendIComponentText(1314, 73, "" + p2.getSkills().getLevel(8)); // woodcutting
			player.getPackets().sendIComponentText(1314, 74, "" + p2.getSkills().getLevel(19)); // farming
			player.getPackets().sendIComponentText(1314, 69, "" + p2.getSkills().getLevel(23)); // summoning
			player.getPackets().sendIComponentText(1314, 82, "" + p2.getSkills().getLevel(25)); // divination
			player.getPackets().sendIComponentText(1314, 62, "" + p2.getSkills().getCombatLevelWithSummoning());
			player.getPackets().sendIComponentText(1314, 90,
					"" + Utils.getFormattedNumber(p2.getSkills().getTotalLevel(p2)));
			player.getPackets().sendIComponentText(1314, 91, "" + p2.getSkills().getTotalXp(p2));
		} else if (packetId == PLAYER_OPTION_6_PACKET) {
			boolean forceRun = stream.readUnsignedByte() == 1;
			int playerIndex = stream.readUnsignedShortLE128();
			final Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2.isDead() || p2.hasFinished() || !player.getMapRegionsIds().contains(p2.getRegionId())
					|| player.isLocked() || player == p2)
				return;
			if (!p2.withinDistance(player, 14)) {
				player.sendMessage("Unable to find " + p2.getDisplayName() + ".");
				return;
			}
			if (player.getAttackedByDelay() + 5000 > Utils.currentTimeMillis()) {
				player.sendMessage(
						"You can't pelt " + p2.getDisplayName() + "'s stats until 5 seconds after the end of combat.");
				return;
			}
			if (player.getInventory().containsItem(10501, 1))
				player.getInventory().deleteItem(10501, 1);
			else if (player.getEquipment().getWeaponId() == 10501
					&& player.getEquipment().getItem(Equipment.SLOT_WEAPON).getAmount() > 0) {
				Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
				player.getEquipment().getItem(Equipment.SLOT_WEAPON)
						.setAmount(player.getEquipment().getItem(Equipment.SLOT_WEAPON).getAmount() - 1);
				player.getEquipment().refresh(Equipment.SLOT_WEAPON);
				if (player.getEquipment().getItem(Equipment.SLOT_WEAPON).getAmount() == 0) {
					player.sendMessage("You used up your last Snowball.");
					player.getEquipment().deleteItem(10501, weapon.getAmount());
				}
				player.getEquipment().refresh(Equipment.SLOT_WEAPON);
				player.getGlobalPlayerUpdater().generateAppearenceData();
			} else {
				player.getPackets().sendPlayerOption("Null", 6, true);
				return;
			}
			player.faceEntity(p2);
			player.setNextAnimation(new Animation(7530));
			player.lock(2);
			World.sendProjectile(player, player, p2, 1281, 10, 10, 75, 50, 15, 0);
			CoresManager.fastExecutor.schedule(new TimerTask() {
				int snowballtime = 3;

				public void run() {
					try {
						if (snowballtime == 1) {
							p2.setNextGraphics(new Graphics(1282));
						}
						if (this == null || snowballtime <= 0) {
							cancel();
							return;
						}
						if (snowballtime >= 0) {
							snowballtime--;
						}
					} catch (Throwable e) {
						Logger.handle(e);
					}
				}
			}, 0, 600);
			return;
		} else if (packetId == PLAYER_OPTION_9_PACKET) {
			boolean forceRun = stream.readUnsignedByte() == 1;
			int playerIndex = stream.readUnsignedShortLE128();
			if (Settings.DEBUG)
				System.out.println(playerIndex);
			Player p2 = World.getPlayers().get(playerIndex);
			if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished()
					|| !player.getMapRegionsIds().contains(p2.getRegionId()))
				return;
			if (player.isLocked())
				return;
			if (forceRun)
				player.setRun(forceRun);
			player.stopAll();
			ClansManager.viewInvite(player, p2);
		} else if (packetId == ITEM_TAKE_PACKET) {
			if (!player.isActive() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			long currentTime = Utils.currentTimeMillis();
			if (player.getLockDelay() > currentTime)
				return;
			int y = stream.readUnsignedShort();
			int x = stream.readUnsignedShortLE();
			final int id = stream.readUnsignedShort();
			boolean forceRun = stream.read128Byte() == 1;
			final WorldTile tile = new WorldTile(x, y, player.getPlane());
			final int regionId = tile.getRegionId();
			if (!player.getMapRegionsIds().contains(regionId))
				return;
			final FloorItem item = World.getRegion(regionId).getGroundItem(id, tile, player);
			if (item == null)
				return;
			player.stopAll(false);
			if (forceRun)
				player.setRun(forceRun);

			player.setRouteEvent(new RouteEvent(item, new Runnable() {
				@Override
				public void run() {
					final FloorItem item = World.getRegion(regionId).getGroundItem(id, tile, player);
					if (item == null || !player.getNewQuestManager().canTakeItem(item)
							|| !player.getControlerManager().canTakeItem(item))
						return;
					if (item.getOwner() == null) {
						/*
						 * ( if (item.getId() >= 7928 && item.getId() <= 7933)
						 * 
						 * new NewsManager(player,
						 * "<b><img src=\"./bin/images/news/easter.png\" height=15> " +
						 * player.getDisplayName() + " has just found an Easter Egg.")) .start();
						 */
					}
					if (item.getOwner() != null) {
						if (player.getRights() == 2)
							player.sendMessage("This item was dropped by: " + item.getOwner() + ".");
						if ((player.isIronMan() || player.isHCIronMan() || player.isWiki())
								&& !player.getUsername().equalsIgnoreCase(item.getOwner())) {
							player.getPackets()
									.sendGameMessage("You can't pick up the items that aren't yours on iron man mode.");
							return;
						}
					}
					player.setNextFaceWorldTile(tile);
					player.addWalkSteps(tile.getX(), tile.getY(), 1);
					World.removeGroundItem(player, item);
				}
			}));
		}
	}

	public static void loadPacketSizes() {
		PACKET_SIZES[0] = -1;
		PACKET_SIZES[1] = -2;
		PACKET_SIZES[2] = -1;
		PACKET_SIZES[3] = 20;
		PACKET_SIZES[4] = -1;
		PACKET_SIZES[5] = 10;
		PACKET_SIZES[6] = 10;
		PACKET_SIZES[7] = 3;
		PACKET_SIZES[8] = -1;
		PACKET_SIZES[9] = 3;
		PACKET_SIZES[10] = -1;
		PACKET_SIZES[11] = -1;
		PACKET_SIZES[12] = -1;
		PACKET_SIZES[13] = 7;
		PACKET_SIZES[14] = 10;
		PACKET_SIZES[15] = 6;
		PACKET_SIZES[16] = 2;
		PACKET_SIZES[17] = 3;
		PACKET_SIZES[18] = -1;
		PACKET_SIZES[19] = -2;
		PACKET_SIZES[20] = 3;
		PACKET_SIZES[21] = 0;
		PACKET_SIZES[22] = -1;
		PACKET_SIZES[23] = 9;
		PACKET_SIZES[24] = -1;
		PACKET_SIZES[25] = 9;
		PACKET_SIZES[26] = 9;
		PACKET_SIZES[27] = 10;
		PACKET_SIZES[28] = 4;
		PACKET_SIZES[29] = -1;
		PACKET_SIZES[30] = 0;
		PACKET_SIZES[31] = 3;
		PACKET_SIZES[32] = 10;
		PACKET_SIZES[33] = 4;
		PACKET_SIZES[34] = 3;
		PACKET_SIZES[35] = -1;
		PACKET_SIZES[36] = -1;
		PACKET_SIZES[37] = 19;
		PACKET_SIZES[38] = 4;
		PACKET_SIZES[39] = 4;
		PACKET_SIZES[40] = 9;
		PACKET_SIZES[41] = -1;
		PACKET_SIZES[42] = 3;
		PACKET_SIZES[43] = 9;
		PACKET_SIZES[44] = -2;
		PACKET_SIZES[45] = 9;
		PACKET_SIZES[46] = 3;
		PACKET_SIZES[47] = 4;
		PACKET_SIZES[48] = -1;
		PACKET_SIZES[49] = 3;
		PACKET_SIZES[50] = 13;
		PACKET_SIZES[51] = 3;
		PACKET_SIZES[52] = -1;
		PACKET_SIZES[53] = -1;
		PACKET_SIZES[54] = 0;
		PACKET_SIZES[55] = 10;
		PACKET_SIZES[56] = 3;
		PACKET_SIZES[57] = 9;
		PACKET_SIZES[58] = -1;
		PACKET_SIZES[59] = 9;
		PACKET_SIZES[60] = -1;
		PACKET_SIZES[61] = 9;
		PACKET_SIZES[62] = 9;
		PACKET_SIZES[63] = 12;
		PACKET_SIZES[64] = 4;
		PACKET_SIZES[65] = 3;
		PACKET_SIZES[66] = 13;
		PACKET_SIZES[67] = 10;
		PACKET_SIZES[68] = 10;
		PACKET_SIZES[69] = 15;
		PACKET_SIZES[70] = 1;
		PACKET_SIZES[71] = 2;
		PACKET_SIZES[72] = 6;
		PACKET_SIZES[73] = -1;
		PACKET_SIZES[74] = -1;
		PACKET_SIZES[75] = -2;
		PACKET_SIZES[76] = 16;
		PACKET_SIZES[77] = 3;
		PACKET_SIZES[78] = 1;
		PACKET_SIZES[79] = 3;
		PACKET_SIZES[80] = 9;
		PACKET_SIZES[81] = 4;
		PACKET_SIZES[82] = -2;
		PACKET_SIZES[83] = 1;
		PACKET_SIZES[84] = 1;
		PACKET_SIZES[85] = 3;
		PACKET_SIZES[86] = -1;
		PACKET_SIZES[87] = 4;
		PACKET_SIZES[88] = 3;
		PACKET_SIZES[89] = -1;
		PACKET_SIZES[90] = 10;
		PACKET_SIZES[91] = -2;
		PACKET_SIZES[92] = -1;
		PACKET_SIZES[93] = -1;
		PACKET_SIZES[94] = 9;
		PACKET_SIZES[95] = -2;
		PACKET_SIZES[96] = 10;
		PACKET_SIZES[97] = 2;
		PACKET_SIZES[98] = 6;
		PACKET_SIZES[99] = 2;
		PACKET_SIZES[100] = -2;
		PACKET_SIZES[101] = 3;
		PACKET_SIZES[102] = 9;
		PACKET_SIZES[103] = 4;
		PACKET_SIZES[104] = 8;
		PACKET_SIZES[105] = 0;
	}

	private Player player;

	public WorldPacketsDecoder(Session session, Player player) {
		super(session);
		this.player = player;
	}

	private static final short[] IGNORED = { 30, 33, 24, 61, 94, 67, 98, 90 };

	public static boolean isIgnored(int opcode) {
		for (short ignored : IGNORED) {
			if (ignored == opcode)
				return true;
		}
		return false;
	}

	@Override
	public void decode(Session session, InputStream in) {
		while (in.getRemaining() > 0 && player.getSession().getChannel().isConnected() && !player.hasFinished()) {
			int opcode = in.readPacket(player);
			if (opcode >= PACKET_SIZES.length || opcode < 0) {
				break;
			}
			int length = PACKET_SIZES[opcode];
			if (length == -1) {
				length = in.readUnsignedByte();
			} else if (length == -2) {
				length = in.readUnsignedShort();
			} else if (length == -3) {
				length = in.readInt();
			} else if (length == -4) {
				length = in.getRemaining();
			}
			if (length > in.getRemaining()) {
				length = in.getRemaining();
			}
			int startOffset = in.getOffset();
			processPackets(opcode, length, in);
			in.setOffset(startOffset + length);
		}
	}

	private int chatType;

	public void processPackets(final int opcode, final int length, InputStream stream) {
		player.setPacketsDecoderPing(Utils.currentTimeMillis());
		if (opcode == PING_PACKET) {
			OutputStream packet = new OutputStream(0);
			packet.writePacket(player, 153);
			player.getSession().write(packet);
		} else if (opcode == KEY_TYPED_PACKET) {
			// int keyPressed = stream.readByte();
			// switch (keyPressed) {
			// case 13:
			// player.closeInterfaces();
			// break;
			// }
			// if (Settings.DEBUG)
			// Logger.log(this, "Key typed: " + keyPressed);
		} else if (opcode == DISPLAY_PACKET) {
			int displayMode = stream.readUnsignedByte();
			boolean switchScreenMode = stream.readUnsignedByte() == 1;
			player.setDisplayMode(displayMode);
			player.getInterfaceManager().removeAll();
			player.getInterfaceManager().sendInterfaces();
		} else if (opcode == INTERFACE_ON_INTERFACE) {
			InventoryOptionsHandler.handleItemOnItem(player, stream);
		} else if (opcode == RECEIVE_PACKET_COUNT_PACKET) {
			int packetcount = stream.readInt();
		} else if (opcode == DIALOGUE_CONTINUE_PACKET) {
			int interfaceHash = stream.readInt();
			int junk = stream.readShort128();
			int interfaceId = interfaceHash >> 16;
			int buttonId = (interfaceHash & 0xFF);
			if (Utils.getInterfaceDefinitionsSize() <= interfaceId) {
				Logger.log("Dialogue_Continue_Packet error!");
				return;
			}
			if (!player.isRunning())
				return;
			if (Settings.DEBUG)
				Logger.log(this, "Dialogue: " + interfaceId + ", " + buttonId + ", " + junk);
			int componentId = interfaceHash - (interfaceId << 16);
			if (interfaceId == 667) {
				CosmeticsHandler.openCosmeticsHandler(player);
				if (junk == Equipment.SLOT_SHIELD) {
					Item weapon = player.getEquipment().getCosmeticItems().get(Equipment.SLOT_WEAPON);
					if (weapon != null) {
						boolean isTwoHandedWeapon = Equipment.isTwoHandedWeapon(weapon);
						if (isTwoHandedWeapon) {
							player.getPackets().sendGameMessage(
									"You have a two handed cosmetic weapon. You can't edit shield slot unless you remove it.");
							return;
						}
					}
				}
				if (junk == Equipment.SLOT_RING && player.getEquipment().getSavedCosmetics().isEmpty()) {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You don't have any saved costumes. To save your current costume do ::savecurrentcostume or ::savecurrentcosmetic .");
					return;
				}
				player.getDialogueManager().startDialogue(
						(player.isShowSearchOption() && player.getEquipment().getCosmeticItems().get(junk) == null)
								? "CosmeticsSelect"
								: "CosmeticsD",
						junk);
			} else
				player.getDialogueManager().continueDialogue(interfaceId, componentId);
		} else if (opcode == WORLD_MAP_CLICK) {
			int coordinateHash = stream.readInt();
			int x = coordinateHash >> 14;
			int y = coordinateHash & 0x3fff;
			int plane = coordinateHash >> 28;
			Integer hash = (Integer) player.getTemporaryAttributtes().get("worldHash");
			if (hash == null || coordinateHash != hash)
				player.getTemporaryAttributtes().put("worldHash", coordinateHash);
			else {
				player.getTemporaryAttributtes().remove("worldHash");
				player.getHintIconsManager().addHintIcon(x, y, plane, 20, 0, 2, -1, true);
				player.getVarsManager().sendVar(1159, coordinateHash);
			}
		} else if (opcode == CLOSE_INTERFACE_PACKET) {
			if (player.isActive() && !player.hasFinished() && !player.isRunning()) {
				LoginManager.sendLogin(player);
				return;
			}
			player.stopAll();
		} else if (opcode == ESCAPE_PRESSED_PACKET) {
			if (player.isActive() && !player.hasFinished() && !player.isRunning()) {
				LoginManager.sendLogin(player);
				return;
			}
			player.closeInterfaces();
		} else if (opcode == ENTER_INTEGER_PACKET) {
			if (!player.isRunning() || player.isDead())
				return;
			int value = stream.readInt();
			if (player.getTemporaryAttributtes().get("ArtisansWorkShop") != null) {
				Object[] settings = (Object[]) player.getTemporaryAttributtes().remove("ArtisansWorkShop");
				if (value <= 0)
					return;
				int itemId = (int) settings[0];
				boolean deposit = (boolean) settings[1];
				ArtisansWorkShop.withdrawDepositOre(player, itemId, value, deposit);
				return;
			}
			if (player.getTemporaryAttributtes().get("gr_withdraw") == Boolean.TRUE) {
				player.getGrManager().withdraw(value);
				player.getTemporaryAttributtes().put("gr_withdraw", Boolean.FALSE);
			}
			if (player.getTemporaryAttributtes().get("gr_deposit") == Boolean.TRUE) {
				player.getGrManager().deposit(value);
				player.getTemporaryAttributtes().put("gr_deposit", Boolean.FALSE);
			}
			if (player.getTemporaryAttributtes().get("gr_bet") == Boolean.TRUE) {
				player.getGrManager().changeBet(value);
				player.getTemporaryAttributtes().put("gr_bet", Boolean.FALSE);
			}
			if (player.getTemporaryAttributtes().get("set_tax") == Boolean.TRUE) {
				ThroneManager.setTax(player, value);
				player.getTemporaryAttributtes().put("set_tax", Boolean.FALSE);
			}
			if (player.getTemporaryAttributtes().get(Key.DROP_X) != null) {
				Object[] attributtes = (Object[]) player.getTemporaryAttributtes().remove(Key.DROP_X);
				Item item = (Item) attributtes[0];
				int slotId = (int) attributtes[1];
				if (item == null || value <= 0 || slotId >= 28)
					return;
				if (value > player.getInventory().getAmountOf(item.getId()))
					value = player.getInventory().getAmountOf(item.getId());
				item = new Item(item.getId(), value);
				if (!player.getControlerManager().canDropItem(item))
					return;
				InventoryOptionsHandler.handleItemOption7(player, slotId, item.getId(), item);
				return;
			}
			if (player.getTemporaryAttributtes().get(Key.FORGE_X) != null) {
				Integer index = (Integer) player.getTemporaryAttributtes().remove(Key.FORGE_X);
				if (index == null)
					return;
				boolean dungeoneering = false;
				if (index > 100) {
					index -= 100;
					dungeoneering = true;
				}
				player.closeInterfaces();
				player.getActionManager().setAction(new DungeoneeringSmithing(index, value, dungeoneering));
				return;
			}
			if (player.getInterfaceManager().containsInterface(DungeoneeringSummoning.POUCHES_INTERFACE)
					&& player.getTemporaryAttributtes().get(Key.INFUSE_X) != null) {
				boolean dungeoneering = (boolean) player.getTemporaryAttributtes().remove(Key.INFUSE_X);
				int item = (int) player.getTemporaryAttributtes().remove(Key.INFUSE_ITEM);
				DungeoneeringSummoning.handlePouchInfusion(player, item >> 16, item & 0xFF, value, dungeoneering);
				return;
			}
			if (player.getTemporaryAttributtes().get("ChangeSkillIPVP") != null) {
				if (player.getControlerManager().getControler() == null
				/*
				 * || !(player.getControlerManager().getControler() instanceof
				 * InstancedPVPControler)
				 */)
					return;
				int skill = (int) player.getTemporaryAttributtes().remove("ChangeSkillIPVP");
				if (value <= 0 || value > 99) {
					player.getPackets().sendGameMessage("Please choose a valid level.");
					return;
				}
				player.getSkills().set(skill, value);
				player.getSkills().setXp(skill, Skills.getXPForLevel(value));
				player.getGlobalPlayerUpdater().generateAppearenceData();
				player.getPackets().sendGameMessage(
						"You have changed your " + Skills.SKILL_NAME[skill] + " level to " + value + ".");
				return;
			}
			if (player.getTemporaryAttributtes().get("Combination-X") != null) {
				int combinationItemId = (int) player.getTemporaryAttributtes().remove("Combination-X");
				if (value <= 0)
					return;
				CombinationPotions.startCombinationAction(player, value, combinationItemId);
				return;
			}
			if (player.getTemporaryAttributtes().remove("ADD_RESTRICT_ID") != null) {
				if (value >= Utils.getItemDefinitionsSize() || value < 0) {
					player.getPackets().sendGameMessage("Not a vaild item id.");
					return;
				}
				player.getPetLootManager().getRestrictedItems().add(new RestrictedItem(value));
				player.getPackets().sendGameMessage(
						"Restricted item (" + value + ") has been added to your restricted items list.");
				player.getPackets().sendGameMessage("Use ::petlootmanager or ::plm to edit/remove/disable it.");
				return;
			}
			if (player.getTemporaryAttributtes().remove("LOTTERY_TICKET_BUY") != null) {
				if (value <= 0)
					return;
				int price = (value * Lottery.TICKET_PRICE);
				if (player.getInventory().getCoinsAmount() < price) {
					player.getPackets().sendGameMessage("You don't have enough money to buy that many tickets.");
					return;
				}
				if (value + Lottery.getTicketsAmount(player) > Lottery.MAX_TICKETS_PER_PLAYER) {
					player.getPackets().sendGameMessage("You can't have more than 20 tickets, You currently have "
							+ Lottery.getTicketsAmount(player) + " lottery tickets.");
					return;
				}

				if ((value * Lottery.TICKET_PRICE) + (Lottery.LOTTERY_TICKETS.size() * Lottery.TICKET_PRICE) <= 0) {
					player.getPackets()
							.sendGameMessage("You can't buy anymore tickets, JackPot amount can't go over 2147M.");
					return;
				}

				// more checks for security reason and glitch/exploit fix
				if (player.getMoneyPouchValue() < price) {
					player.getPackets().sendGameMessage("You need to have atleast " + value + "m in your moneypouch");
					return;
				}
				player.getInventory().removeItemMoneyPouch(new Item(995, price));
				Lottery.buyTicket(player, value);

				player.getPackets()
						.sendGameMessage("You have bought " + value + " X Lottery Tickets, You currently have "
								+ Lottery.getTicketsAmount(player) + " Lottery Tickets.");
				return;
			}
			if (player.getInterfaceManager().containsInterface(335)) {
				if (player.getTemporaryAttributtes().remove("trade_money") != null) {
					Integer trade_X_money = (Integer) player.getTemporaryAttributtes().remove("trade_X_money");
					if (value <= 0 || trade_X_money == null || player.getMoneyPouch().getTotal() == 0)
						return;
					if (value >= player.getMoneyPouch().getTotal())
						value = player.getMoneyPouch().getTotal();
					player.getTrade().addPouch(trade_X_money, value);
				}
			} else if (player.getTemporaryAttributtes().remove(Key.SELL_SPIRIT_SHARDS) != null)
				PetShopOwner.sellShards(player, value);
			else if (player.getInterfaceManager().containsInterface(400)) {
				Integer create_tab_X_component = (Integer) player.getTemporaryAttributtes()
						.remove("create_tab_X_component");
				if (create_tab_X_component == null)
					return;
				TabletMaking.handleTabletCreation(player, create_tab_X_component, value);
			}
			if (player.getTemporaryAttributtes().get("xpSkillTarget") != null) {
				int xpTarget = value;
				Integer skillId = (Integer) player.getTemporaryAttributtes().remove("xpSkillTarget");
				if (xpTarget < player.getSkills().getXp(player.getSkills().getSkillIdByTargetId(skillId))
						|| player.getSkills().getXp(player.getSkills().getSkillIdByTargetId(skillId)) >= 200000000) {
					return;
				}
				if (xpTarget > 200000000)
					xpTarget = 200000000;
				player.getSkills().setSkillTarget(false, skillId, xpTarget);

			} else if (player.getTemporaryAttributtes().get("levelSkillTarget") != null) {
				int levelTarget = value;
				Integer skillId = (Integer) player.getTemporaryAttributtes().remove("levelSkillTarget");
				int curLevel = player.getSkills().getLevel(player.getSkills().getSkillIdByTargetId(skillId));
				if (curLevel >= (skillId == 24 ? 120 : 99))
					return;
				if (levelTarget > (skillId == 24 ? 120 : 99))
					levelTarget = skillId == 24 ? 120 : 99;
				if (levelTarget < player.getSkills().getLevel(player.getSkills().getSkillIdByTargetId(skillId)))
					return;
				player.getSkills().setSkillTarget(true, skillId, levelTarget);
			} else if (player.getTemporaryAttributtes().remove("donate_xp_well") != null)
				WellOfGoodWill.donate(player, value);
			else if (player.getTemporaryAttributtes().remove("dung_store_xp") != null)
				DungeonRewardShop.buyXP(player, value);
			else if (player.getTemporaryAttributtes().get("create_x_pouch") != null) {
				Integer itemId = (Integer) player.getTemporaryAttributtes().remove("create_x_pouch");
				Summoning.createPouch(player, itemId, value);
			}
			/*
			 * if (player.getTemporaryAttributtes().get("coinsamount") != null &&
			 * !player.getInventory().containsCoins(value * 1000000)) { player.sm(Colors.red
			 * + "You do not have enough gold! 1million gp is equal to 1 Cashbag!"); return;
			 * } else { if (player.getTemporaryAttributtes().remove("coinsamount") != null
			 * && player.getInventory().containsCoins(value * 1000000)) {
			 * player.getInventory().addItem(24954, value);
			 * player.getInventory().deleteCoins(1000000 * value); player.sm(Colors.green +
			 * "You converted " + value + " Million GP into " + Colors.orange + value +
			 * " cash bag!"); } }
			 */
			if (player.getTemporaryAttributtes().get("soul_wars_x") != null) {
				final int item = (int) player.getTemporaryAttributtes().remove("soul_wars_x");
				if (SoulWarsManager.MINUTES_BEFORE_NEXT_GAME.get() < 4)
					return;
				final WorldTile walked = (WorldTile) player.getTemporaryAttributtes().remove("soul_wars_walked");
				if (walked != null && !player.matches(walked))
					return;
				player.getInventory().addItem(item, value);
				return;
			}
			if (player.getTemporaryAttributtes().get("edit_price") != null) {
				if (value < 0) {
					player.getTemporaryAttributtes().remove("edit_price");
					return;
				}
				if (player.getTemporaryAttributtes().get("edit_price") == Boolean.TRUE) {
					player.getTemporaryAttributtes().put("edit_price", Boolean.FALSE);
					player.getGEManager().modifyPricePerItem(value);
					player.getTemporaryAttributtes().remove("edit_price");
				}
				return;
			}
			if (player.getTemporaryAttributtes().get("edit_quantity") != null) {
				if (value < 0) {
					player.getTemporaryAttributtes().remove("edit_quantity");
					return;
				}
				if (player.getTemporaryAttributtes().get("edit_quantity") == Boolean.TRUE) {
					player.getTemporaryAttributtes().put("edit_quantity", Boolean.FALSE);
					player.getGEManager().modifyAmount(value);
					player.getTemporaryAttributtes().remove("edit_quantity");
				}
				return;
			}
			if (player.getTemporaryAttributtes().get("loot_beam") != null) {
				player.getTemporaryAttributtes().remove("loot_beam");
				if (value < 1 || value > 2147000000)
					value = 50000;
				player.setLootBeam = value;
				player.getDialogueManager().startDialogue("SimpleMessage", "You've set your Loot Beam trigger price "
						+ "to: " + Colors.red + Utils.getFormattedNumber(value) + "</col>.");
				AccountInterfaceManager.sendInterface(player);
				return;
			}
			if (player.getTemporaryAttributtes().get("ports_plate") != null) {
				player.getTemporaryAttributtes().remove("ports_plate");
				if (value < 1 || value > 2147000000)
					value = 0;
				int plate = player.getPorts().plate;
				if (value > plate)
					value = plate;
				if (value <= 0)
					return;
				player.getDialogueManager().startDialogue("SimpleNPCMessage", 18891,
						"I've exchanged " + value + " plate for " + (value * 5) + " Chime.");
				player.getPorts().chime += value * 5;
				player.getPorts().plate -= value;
				return;
			}
			if (player.getTemporaryAttributtes().get("ports_chiGlobe") != null) {
				player.getTemporaryAttributtes().remove("ports_chiGlobe");
				if (value < 1 || value > 2147000000)
					value = 0;
				int plate = player.getPorts().chiGlobe;
				if (value > plate)
					value = plate;
				if (value <= 0)
					return;
				player.getDialogueManager().startDialogue("SimpleNPCMessage", 18891,
						"I've exchanged " + value + " chi globe for " + (value * 5) + " Chime.");
				player.getPorts().chime += value * 5;
				player.getPorts().chiGlobe -= value;
				return;
			}
			if (player.getTemporaryAttributtes().get("ports_lacquer") != null) {
				player.getTemporaryAttributtes().remove("ports_lacquer");
				if (value < 1 || value > 2147000000)
					value = 0;
				int plate = player.getPorts().lacquer;
				if (value > plate)
					value = plate;
				if (value <= 0)
					return;
				player.getDialogueManager().startDialogue("SimpleNPCMessage", 18891,
						"I've exchanged " + value + " lacquer for " + (value * 5) + " Chime.");
				player.getPorts().chime += value * 5;
				player.getPorts().lacquer -= value;
				return;
			}
			if (player.getInterfaceManager().containsInterface(631)) {
				if (player.getTemporaryAttributtes().remove("duelmoneypouch_remove") != null) {
					Integer duel_X_money = (Integer) player.getTemporaryAttributtes().remove("duelmoneypouch_X_money");
					if (value <= 0 || duel_X_money == null || player.getMoneyPouch().getTotal() == 0)
						return;
					if (value >= player.getMoneyPouch().getTotal())
						value = player.getMoneyPouch().getTotal();
					((DuelArena) player.getControlerManager().getControler()).addPouch(duel_X_money, value);
				}
			}
			if (player.getInterfaceManager().containsInterface(548)
					|| player.getInterfaceManager().containsInterface(746)) {
				if (player.getTemporaryAttributtes().get("money_pouch_remove") == Boolean.TRUE) {
					player.getMoneyPouch().withdrawPouch(value);
					player.getTemporaryAttributtes().put("money_pouch_remove", Boolean.FALSE);
					return;
				}
			}
			if ((player.getInterfaceManager().containsInterface(762)
					&& player.getInterfaceManager().containsInterface(763))
					|| player.getInterfaceManager().containsInterface(11)) {
				if (value < 0)
					return;
				if (player.getTemporaryAttributtes().get("clanBank") != null
						&& player.getTemporaryAttributtes().get("clanBank") == Boolean.TRUE) {
					Integer bank_item_X_Slot = (Integer) player.getTemporaryAttributtes()
							.remove("clanbank_item_X_Slot");
					if (bank_item_X_Slot == null)
						return;
					player.getBank().setLastX(value);
					player.getBank().refreshLastX();
					if (player.getTemporaryAttributtes().remove("clanbank_isWithdraw") != null)
						player.getClanManager().getClan().getClanBank().withdrawItem(player, bank_item_X_Slot, value);
					else
						player.getClanManager().getClan().getClanBank().depositItem(player, bank_item_X_Slot, value,
								player.getInterfaceManager().containsInterface(11) ? false : true);
				} else {
					Integer bank_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("bank_item_X_Slot");
					if (bank_item_X_Slot == null)
						return;
					player.getBank().setLastX(value);
					player.getBank().refreshLastX();
					if (player.getTemporaryAttributtes().remove("bank_isWithdraw") != null)
						player.getBank().withdrawItem(bank_item_X_Slot, value);
					else
						player.getBank().depositItem(bank_item_X_Slot, value,
								player.getInterfaceManager().containsInterface(11) ? false : true);
				}
			} else if (player.getInterfaceManager().containsInterface(206)
					&& player.getInterfaceManager().containsInterface(207)) {
				if (value < 0)
					return;
				Integer pc_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("pc_item_X_Slot");
				if (pc_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributtes().remove("pc_isRemove") != null)
					player.getPriceCheckManager().removeItem(pc_item_X_Slot, value);
				else
					player.getPriceCheckManager().addItem(pc_item_X_Slot, value);
			} else if (player.getInterfaceManager().containsInterface(671)
					&& player.getInterfaceManager().containsInterface(665)) {
				if (player.getFamiliar() == null || player.getFamiliar().getBob() == null)
					return;
				if (value < 0)
					return;
				Integer bob_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("bob_item_X_Slot");
				if (bob_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributtes().remove("bob_isRemove") != null)
					player.getFamiliar().getBob().removeItem(bob_item_X_Slot, value);
				else
					player.getFamiliar().getBob().addItem(bob_item_X_Slot, value);
			} else if (player.getInterfaceManager().containsInterface(335)
					&& player.getInterfaceManager().containsInterface(336)) {
				if (value < 0)
					return;
				Integer trade_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("trade_item_X_Slot");
				if (trade_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributtes().remove("trade_isRemove") != null)
					player.getTrade().removeItem(trade_item_X_Slot, value);
				else
					player.getTrade().addItem(trade_item_X_Slot, value);
			} else if (player.getInterfaceManager().containsInterface(628)
					|| player.getInterfaceManager().containsInterface(631)) {
				if (value < 0)
					return;
				Integer duel_item_X_Slot = (Integer) player.getTemporaryAttributtes().remove("duel_item_X_Slot");
				if (duel_item_X_Slot == null)
					return;
				if (player.getTemporaryAttributtes().remove("duel_isRemove") != null) {
					((DuelArena) player.getControlerManager().getControler()).removeItem(duel_item_X_Slot, value);
				} else {
					((DuelArena) player.getControlerManager().getControler()).addItem(duel_item_X_Slot, value);
				}
				((DuelArena) player.getControlerManager().getControler()).refresh(duel_item_X_Slot);
			} else if (player.getTemporaryAttributtes().get("Sawing") != null
					&& player.getTemporaryAttributtes().get("Sawitem") != null) {
				int amount = value;
				int itemId = ((Integer) player.getTemporaryAttributtes().remove("Sawitem")).intValue();
				Item item = new Item(itemId);
				if (amount == 0) {
					player.getDialogueManager().startDialogue("SimpleMessage", "...");
					return;
				}
				if (player.getInventory().containsItem(960, amount)) {
					// todo action (lazy coding atm)
					player.getInventory().deleteItem(960, amount);
					player.setNextAnimation(new Animation(9031));
					player.getSkills().addXp(Skills.CONSTRUCTION, 25 * amount);
					return;
				} else {
					player.getDialogueManager().startDialogue("SimpleMessage",
							"You dont have " + amount + " " + item.getName() + "s.");
					return;
				}
			}
		} else if (opcode == ENTER_NAME_PACKET) {
			if (!player.isRunning() || player.isDead())
				return;
			String value = stream.readString();
			if (player.getTemporaryAttributtes().remove("SaveCosmetic") != null) {
				if (value.equals(""))
					return;
				ItemsContainer<Item> cosmeticItems = new ItemsContainer<Item>(19, false);
				for (int i = 0; i < player.getEquipment().getCosmeticItems().getSize(); i++) {
					cosmeticItems.set(i, player.getEquipment().getCosmeticItems().get(i));
				}
				SavedCosmetic cosmetic = new SavedCosmetic(value, cosmeticItems);
				player.getEquipment().getSavedCosmetics().add(cosmetic);
				player.getPackets().sendGameMessage("<col=00ff00>You have succecfully added " + value
						+ " to your saved costumes. You can find it by doing ::cosmetics and clicking on ring slot.");
				return;
			}
			if (player.getTemporaryAttributtes().get("CosmeticsKeyWord") != null) {
				int slotId = (int) player.getTemporaryAttributtes().remove("CosmeticsKeyWord");
				int choosenOption = (int) player.getTemporaryAttributtes().remove("CosmeticsChoosenOption");
				if (value.equals(""))
					return;
				if (choosenOption != -1)
					player.getDialogueManager().startDialogue("CosmeticsD", slotId, value, choosenOption);
				else
					player.getDialogueManager().startDialogue("CosmeticsD", slotId, value);
				return;
			}
			if (player.getTemporaryAttributtes().remove("CosmeticsStoreKeyWord") != null) {
				if (value.equals(""))
					return;
				CosmeticsHandler.openCosmeticsStore(player, value, 0);
				return;
			}
			if (player.getTemporaryAttributtes().remove("referralName") != null) {
				player.unlock();
				if (value.equals("")) {
					return;
				}
				String name = Utils.formatPlayerNameForProtocol(value);
				if (Utils.formatPlayerNameForProtocol(player.getDisplayName()).equalsIgnoreCase(name))
					return;
				if (!SerializableFilesManager.containsPlayer(name)) {
					player.getPackets().sendGameMessage("Account name " + Utils.formatString(name) + " doesn't exist.");
					return;
				}
				// /ReferralHandler.addReferral(Utils.formatPlayerNameForProtocol(player.getDisplayName()),
				// name);
				return;
			}
			if (value.equals(""))
				return;
			if (player.getTemporaryAttributtes().get("gr_cashout") == Boolean.TRUE) {
				player.getGrManager().changeCashout(value);
				player.getTemporaryAttributtes().put("gr_cashout", Boolean.FALSE);
			}
			if (player.getTemporaryAttributtes().get("xp_share") == Boolean.TRUE) {
				player.getXpSharing().submitEntry(value);
				player.getTemporaryAttributtes().put("xp_share", Boolean.FALSE);
			}
			if (player.getTemporaryAttributtes().remove(Key.DUNGEON_INVITE) != null) {
				player.getDungManager().invite(value);
				return;
			}
			if (player.getTemporaryAttributtes().remove("ADD_GEAR_NAME") != null) {
				player.getGearPresets().saveGear(value);
				return;
			}

			if (player.getTemporaryAttributtes().remove("SaveCosmetic") != null) {
				if (value.equals(""))
					return;
				boolean[] hiddenSlots = new boolean[15];
				for (int i = 0; i < player.getEquipment().getHiddenSlots().length; i++) {
					hiddenSlots[i] = player.getEquipment().getHiddenSlots()[i];
				}
				ItemsContainer<Item> cosmeticItems = new ItemsContainer<Item>(15, false);
				for (int i = 0; i < player.getEquipment().getCosmeticItems().getSize(); i++) {
					cosmeticItems.set(i, player.getEquipment().getCosmeticItems().get(i));
				}
				SavedCosmetic cosmetic = new SavedCosmetic(value, cosmeticItems);
				player.getEquipment().getSavedCosmetics().add(cosmetic);
				player.getPackets().sendGameMessage("<col=00ff00>You have succecfully added " + value
						+ " to your saved costumes. You can find it by doing ::cosmetics and clicking on ring slot.");
				return;
			}

			if (player.getTemporaryAttributtes().get("CosmeticsKeyWord") != null) {
				int slotId = (int) player.getTemporaryAttributtes().remove("CosmeticsKeyWord");
				if (value.equals(""))
					return;
				player.getDialogueManager().startDialogue("CosmeticsD", slotId, value);
				return;
			}
			if (player.getTemporaryAttributtes().get("loanHrs") == Boolean.TRUE)
				player.getTrade().updateHours(Integer.valueOf(value));
			if (player.getInterfaceManager().containsInterface(1108))
				player.getFriendsIgnores().setChatPrefix(value);
			else if (player.getTemporaryAttributtes().remove("ADD_RESTRICT_NAME") != null) {
				player.getPetLootManager().getRestrictedItems().add(new RestrictedItem(value));
				player.getPackets().sendGameMessage(
						"Restricted item (" + value + ") has been added to your restricted items list.");
				player.getPackets().sendGameMessage("Use ::petlootmanager or ::plm to edit/remove/disable it.");
			} else if (player.getTemporaryAttributtes().get("RENAME_BANK") != null) {
				int index = (int) player.getTemporaryAttributtes().remove("RENAME_BANK");
				ExtraBank bank = player.getBanksManager().getBanks().get(index);
				if (bank == null)
					return;
				bank.setName(value);
			} else if (player.getTemporaryAttributtes().remove("NAME_BANK") != null) {
				player.getBanksManager().getBanks().add(new ExtraBank(value, new Item[1][0]));
				player.getInventory().deleteItem(13663, 1);
				player.getPackets().sendGameMessage("bank (" + value + ") has been added to your banks manager.");
				player.getPackets().sendGameMessage("Use ::managebanks or ::mb to set it as active bank.");
			} else if (player.getTemporaryAttributtes().remove("setclan") != null)
				ClansManager.createClan(player, value);
			else if (player.getTemporaryAttributtes().remove("joinguestclan") != null)
				ClansManager.connectToClan(player, value, true);
			else if (player.getTemporaryAttributtes().remove("joinguesthouse") != null)
				House.enterHouse(player, value);
			else if (player.getTemporaryAttributtes().remove("banclanplayer") != null)
				ClansManager.banPlayer(player, value);
			else if (player.getTemporaryAttributtes().remove("unbanclanplayer") != null)
				ClansManager.unbanPlayer(player, value);
			else if (player.getTemporaryAttributtes().get("clanmessage_hex") != null) {
				player.getClanManager().getClan().setClanMessageHex(value);
				player.getTemporaryAttributtes().remove("clanmessage_hex");
			} else if (player.getTemporaryAttributtes().remove("setdisplay") != null) {
				if (Utils.invalidAccountName(Utils.formatPlayerNameForProtocol(value))) {
					player.getPackets().sendGameMessage("Invalid name.");
					return;
				}
				if (!DisplayNames.setDisplayName(player, value)) {
					player.getPackets().sendGameMessage("Name already in use!");
					return;
				}
				DisplayNames.setDisplayName(player, value);
				player.displayNameChange = Utils.currentTimeMillis();
				player.getPackets()
						.sendGameMessage("Display name successfully set to: " + player.getDisplayName() + ".");
			} else {
				Boss boss = (Boss) player.getTemporaryAttributtes().remove(Key.JOIN_BOSS_INSTANCE);
				if (boss != null)
					BossInstanceHandler.joinInstance(player, boss, value.toLowerCase(), false);
			}
		} else if (opcode == ENTER_LONG_TEXT_PACKET) {
			if (!player.isRunning() || player.isDead())
				return;
			String value = stream.readString();
			if (value.equals(""))
				return;
			if (player.getTemporaryAttributtes().remove("entering_note") == Boolean.TRUE)
				player.getNotes().add(value);
			else if (player.getTemporaryAttributtes().remove("editing_note") == Boolean.TRUE)
				player.getNotes().edit(value);
			else if (player.getTemporaryAttributtes().remove("change_pass") == Boolean.TRUE) {
				if (value.length() < 5 || value.length() > 15) {
					player.getPackets().sendGameMessage("Password length is limited to 5-15 characters.");
					return;
				}
				player.setPassword(Encrypt.encryptSHA1(value));
				player.getPackets()
						.sendGameMessage("You have changed your password! Your new password is \"" + value + "\".");
			} else if (player.getTemporaryAttributtes().remove("change_troll_name") == Boolean.TRUE) {
				value = Utils.formatString(value);
				if (value.length() < 3 || value.length() > 14) {
					player.getPackets()
							.sendGameMessage("You can't use a name shorter than 3 or longer than 14 characters.");
					return;
				}
				if (value.equalsIgnoreCase("none")) {
					player.getPetManager().setTrollBabyName(null);
				} else {
					player.getPetManager().setTrollBabyName(value);
					if (player.getPet() != null && player.getPet().getId() == Pets.TROLL_BABY.getBabyNpcId()) {
						player.getPet().setName(value);
					}
				}
			} else if (player.getTemporaryAttributtes().remove("yellcolor") == Boolean.TRUE) {
				if (value.length() != 6) {
					player.getPackets().sendGameMessage(
							"The HEX yell color you wanted to pick cannot be longer and shorter then 6.");
				} else if (Utils.containsInvalidCharacter(value) || value.contains("_")) {
					player.getPackets().sendGameMessage(
							"The requested yell color can only contain numeric and regular characters.");
				} else {
					player.setYellColor(value);
					player.getPackets().sendGameMessage("Your yell color has been changed to <col="
							+ player.getYellColor() + ">" + player.getYellColor() + "</col>.");
				}
			} else if (player.getTemporaryAttributtes().remove("setdisplay") == Boolean.TRUE) {
				if (Utils.invalidAccountName(Utils.formatPlayerNameForProtocol(value))) {
					player.getPackets().sendGameMessage("Name contains invalid characters or is too short/long.");
					return;
				}
				if (!DisplayNames.setDisplayName(player, value)) {
					player.getPackets().sendGameMessage("This name is already in use.");
					return;
				}
				player.getPackets().sendGameMessage("Your display name was successfully changed.");
			} else if (player.getInterfaceManager().containsInterface(1103)
					&& player.getClanManager().getInterfaceSelected() == 2) {
				player.getClanManager().getClan().setClanMessage(value);
				player.closeInterfaces();
				player.getClanManager()
						.sendGlobalMessage("[<col=" + player.getClanManager().getClan().getCmHex() + ">"
								+ player.getClanName() + " Message</col>]<col="
								+ player.getClanManager().getClan().getCmHex() + ">" + value);
			} else if (player.getInterfaceManager().containsInterface(1103)
					&& player.getClanManager().getInterfaceSelected() == 1) {
				ClansManager.setClanMottoInterface(player, value);
			}
		} else if (opcode == COLOR_ID_PACKET) {
			if (!player.isActive())
				return;
			int colorId = stream.readUnsignedShort();
			if (player.getTemporaryAttributtes().get("SkillcapeCustomize") != null)
				SkillCapeCustomizer.handleSkillCapeCustomizerColor(player, colorId);
			else if (player.getTemporaryAttributtes().get("MottifCustomize") != null)
				ClansManager.setMottifColor(player, colorId);
			else if (player.getTemporaryAttributtes().remove("COSTUME_COLOR_CUSTOMIZE") != null)
				SkillCapeCustomizer.handleCostumeColor(player, colorId);
		} else if (opcode == SWITCH_INTERFACE_COMPONENTS_PACKET) {
			stream.readShortLE128();
			int fromInterfaceHash = stream.readIntV1();
			int toInterfaceHash = stream.readInt();
			int fromSlot = stream.readUnsignedShort();
			int toSlot = stream.readUnsignedShortLE128();
			stream.readUnsignedShortLE();

			int toInterfaceId = toInterfaceHash >> 16;
			int toComponentId = toInterfaceHash - (toInterfaceId << 16);
			int fromInterfaceId = fromInterfaceHash >> 16;
			int fromComponentId = fromInterfaceHash - (fromInterfaceId << 16);

			if (Settings.DEBUG)
				Logger.log(this,
						"fromSlot: " + fromSlot + ", ToSlot: " + toSlot + ",  toInterfaceHash: " + toInterfaceHash
								+ ", toInterfaceID: " + toInterfaceId + ", toComponent: " + toComponentId
								+ ", frominterface: " + fromInterfaceId + ", from componentId: " + fromComponentId);
			if (fromInterfaceId == 1265 && toInterfaceId == 1266
					&& player.getTemporaryAttributtes().get("shop_buying") != null) {
				if ((boolean) player.getTemporaryAttributtes().get("shop_buying") == true) {
					Shop shop = (Shop) player.getTemporaryAttributtes().get("Shop");
					if (shop == null)
						return;
					shop.buy(player, fromSlot, 1);
				}
			}
			if (Utils.getInterfaceDefinitionsSize() <= fromInterfaceId
					|| Utils.getInterfaceDefinitionsSize() <= toInterfaceId)
				return;
			if (!player.getInterfaceManager().containsInterface(fromInterfaceId)
					|| !player.getInterfaceManager().containsInterface(toInterfaceId))
				return;
			if (fromComponentId != -1
					&& Utils.getInterfaceDefinitionsComponentsSize(fromInterfaceId) <= fromComponentId)
				return;
			if (toComponentId != -1 && Utils.getInterfaceDefinitionsComponentsSize(toInterfaceId) <= toComponentId)
				return;
			if (fromInterfaceId == Inventory.INVENTORY_INTERFACE && fromComponentId == 0
					&& toInterfaceId == Inventory.INVENTORY_INTERFACE && toComponentId == 0) {
				toSlot -= 28;
				if (toSlot < 0 || toSlot >= player.getInventory().getItemsContainerSize()
						|| fromSlot >= player.getInventory().getItemsContainerSize())
					return;
				player.getInventory().switchItem(fromSlot, toSlot);
			} else if (fromInterfaceId == 763 && fromComponentId == 0 && toInterfaceId == 763 && toComponentId == 0) {
				if (toSlot >= player.getInventory().getItemsContainerSize()
						|| fromSlot >= player.getInventory().getItemsContainerSize())
					return;
				player.getInventory().switchItem(fromSlot, toSlot);
			} else if (fromInterfaceId == 762 && toInterfaceId == 762) {
				if (toSlot < 0) {
					toSlot = 65535; // temp fix
				}
				if (player.getBank().isInsertItems()) {
					player.getBank().insertItem(fromSlot, toSlot);
				} else {
					player.getBank().switchItem(fromSlot, toSlot, fromComponentId, toComponentId);
				}
			} else if (fromInterfaceId == 34 && toInterfaceId == 34) {
				player.getNotes().switchNotes(fromSlot, toSlot);
			}
		} else if (opcode == WALKING_PACKET || opcode == MINI_WALKING_PACKET || opcode == ITEM_TAKE_PACKET
				|| opcode == GROUND_ITEM_OPTION_2_PACKET || opcode == PLAYER_OPTION_2_PACKET
				|| opcode == PLAYER_OPTION_4_PACKET || opcode == PLAYER_OPTION_5_PACKET
				|| opcode == PLAYER_OPTION_6_PACKET || opcode == PLAYER_OPTION_1_PACKET
				|| opcode == PLAYER_OPTION_9_PACKET || opcode == ATTACK_NPC || opcode == INTERFACE_ON_PLAYER
				|| opcode == INTERFACE_ON_NPC || opcode == NPC_CLICK1_PACKET || opcode == NPC_CLICK2_PACKET
				|| opcode == NPC_CLICK3_PACKET || opcode == NPC_CLICK4_PACKET || opcode == OBJECT_CLICK1_PACKET
				|| opcode == SWITCH_INTERFACE_COMPONENTS_PACKET || opcode == OBJECT_CLICK2_PACKET
				|| opcode == OBJECT_CLICK3_PACKET || opcode == OBJECT_CLICK4_PACKET || opcode == OBJECT_CLICK5_PACKET
				|| opcode == INTERFACE_ON_OBJECT) {
			player.addLogicPacketToQueue(new LogicPacket(opcode, length, stream));
			player.increaseAFKTimer();
		} else if (opcode == OBJECT_EXAMINE_PACKET) {
			ObjectHandler.handleOption(player, stream, -1);
		} else if (opcode == ADD_FRIEND_PACKET) {
			if (!player.isActive())
				return;
			player.getFriendsIgnores().addFriend(stream.readString());
		} else if (opcode == REMOVE_FRIEND_PACKET) {
			if (!player.isActive())
				return;
			player.getFriendsIgnores().removeFriend(stream.readString());
		} else if (opcode == ADD_IGNORE_PACKET) {
			if (!player.isActive())
				return;
			player.getFriendsIgnores().addIgnore(stream.readString(), stream.readUnsignedByte() == 1);
		} else if (opcode == REMOVE_IGNORE_PACKET) {
			if (!player.isActive())
				return;
			player.getFriendsIgnores().removeIgnore(stream.readString());
		} else if (opcode == SEND_FRIEND_MESSAGE_PACKET) {
			if (player.getMuted() > Utils.currentTimeMillis() || player.isPermMuted()) {
				player.sendMessage("You are muted and cannot talk.");
				return;
			}
			if (IPMute.isMuted(player.getSession().getIP())) {
				player.sendMessage("You are IP-muted and cannot talk.");
				return;
			}
			String username = stream.readString();
			String message = Huffman.decodeString(150, stream);
			Player p2 = World.getPlayerByDisplayName(username);
			if (p2 == null) {
				player.sendMessage(player.getDisplayName() + " is currently offline.");
				return;
			}
			LoggingSystem.logPM(player, p2, Utils.fixChatMessage(message));
			player.getFriendsIgnores().sendMessage(p2, Utils.fixChatMessage(message));
		} else if (opcode == ADD_FRIEND_PACKET) {
			if (!player.isActive())
				return;
			player.getFriendsIgnores().addFriend(stream.readString());
		} else if (opcode == REMOVE_FRIEND_PACKET) {
			if (!player.isActive())
				return;
			player.getFriendsIgnores().removeFriend(stream.readString());
		} else if (opcode == CHAT_TYPE_PACKET) {
			chatType = stream.readUnsignedByte();
		} else if (opcode == CHAT_PACKET) {
			if (!player.isActive() || player.getLastPublicMessage() > Utils.currentTimeMillis())
				return;
			int colorEffect = stream.readUnsignedByte();
			int moveEffect = stream.readUnsignedByte();
			String message = Huffman.decodeString(200, stream);
			if (message == null)
				return;
			if (message.startsWith("::") || message.startsWith(";;")) {
				if (message.startsWith("::")) {
					player.sendMessage("Please use ;; instead of :: to enter commands!");
					return;
				}
				if (!player.getControlerManager().processCommand(message.replace("::", "").replace(";;", ""), false,
						false))
					return;
				Commands.processCommand(player, message.replace("::", "").replace(";;", ""), false, false);
				return;
			}
			if (player.getMuted() > Utils.currentTimeMillis() || player.isPermMuted()) {
				player.sendMessage("You are muted and cannot talk.");
				return;
			}
			if (IPMute.isMuted(player.getSession().getIP())) {
				player.sendMessage("You are IP-muted and cannot talk.");
				return;
			}
			int effects = Utils.fixChatEffects(player.getUsername(), colorEffect, moveEffect);
			message = Profanity.processProfanity(message);
			if (chatType == 1) {
				player.sendFriendsChannelMessage(Utils.fixChatMessage(message));
			} else if (chatType == 2) {
				player.sendClanChannelMessage(new ChatMessage(Utils.fixChatMessage(message)));
			} else if (chatType == 3) {
				player.sendGuestClanChannelMessage(new ChatMessage(Utils.fixChatMessage(message)));
			} else {
				if (player.getControlerManager().getControler() instanceof DungeonController) {
					for (Player party : player.getDungManager().getParty().getTeam()) {
						/*
						 * if (player.getLocalPlayerUpdate().getLocalPlayers()[ party .getIndex()] ==
						 * null || party.getLocalPlayerUpdate().getLocalPlayers ()[player.getIndex()] ==
						 * null) { party.getPackets().sendGameMessage (player.getDisplayName() +
						 * ":<col=7fa9ff> " + message); }
						 */
						party.getPackets().sendPublicMessage(player, new PublicChatMessage(message, effects));
					}
				} else
					player.sendPublicChatMessage(new PublicChatMessage(Utils.fixChatMessage(message), effects));
			}
			player.setLastPublicMessage(Utils.currentTimeMillis() + 300);
			LoggingSystem.logPublicChat(player, message);
			if (Settings.DEBUG)
				Logger.log(this, "Chat type: " + chatType);
		} else if (opcode == JOIN_FRIEND_CHAT_PACKET) {
			FriendChatsManager.joinChat(stream.readString(), player);
		} else if (opcode == KICK_FRIEND_CHAT_PACKET) {
			if (!player.isActive())
				return;
			player.setLastPublicMessage(Utils.currentTimeMillis() + 1000); // avoids
			// message
			// appearing
			player.kickPlayerFromFriendsChannel(stream.readString());
		} else if (opcode == CHANGE_FRIEND_CHAT_PACKET) {
			if (!player.isActive() || !player.getInterfaceManager().containsInterface(1108)) {
				return;
			}
			player.getFriendsIgnores().changeRank(stream.readString(), stream.readUnsignedByte128());
		} else if (opcode == GRAND_EXCHANGE_ITEM_SELECT_PACKET) {
			int itemId = stream.readUnsignedShort();
			player.getGEManager().chooseItem(itemId);
		} else if (opcode == FORUM_THREAD_ID_PACKET) {
			String threadId = stream.readString();
			if (player.getInterfaceManager().containsInterface(1100))
				ClansManager.setThreadIdInterface(player, threadId);
			else if (Settings.DEBUG)
				Logger.log(this, "Called FORUM_THREAD_ID_PACKET: " + threadId);
		} else if (opcode == OPEN_URL_PACKET) {
			String type = stream.readString();
			String path = stream.readString();
			String unknown = stream.readString();
			int flag = stream.readUnsignedByte();
		} else if (opcode == COMMANDS_PACKET) {
			if (!player.isRunning())
				return;
			boolean clientCommand = stream.readUnsignedByte() == 1;
			stream.readUnsignedByte();
			String command = stream.readString();
			if (!Commands.processCommand(player, command, true, clientCommand) && Settings.DEBUG)
				Logger.log(this, "Command: " + command);
		} else if (opcode == NPC_EXAMINE_PACKET) {
			NPCHandler.handleExamine(player, stream);
		} else if (opcode == GROUND_ITEM_OPTION_2_PACKET) {
			if (!player.isActive() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			long currentTime = Utils.currentTimeMillis();
			if (player.getLockDelay() > currentTime)
				return;
			int y = stream.readUnsignedShort();
			int x = stream.readUnsignedShortLE();
			final int id = stream.readInt();
			boolean forceRun = stream.read128Byte() == 1;
			final WorldTile tile = new WorldTile(x, y, player.getPlane());
			final int regionId = tile.getRegionId();
			if (!player.getMapRegionsIds().contains(regionId))
				return;
			final FloorItem item = World.getRegion(regionId).getGroundItem(id, tile, player);
			if (item == null)
				return;
			player.stopAll(false);
			if (forceRun)
				player.setRun(forceRun);
			player.setRouteEvent(new RouteEvent(item, new Runnable() {
				@Override
				public void run() {
					if (item == null)
						return;
					if (item.getOwner() != null) {
						if (item.getOwner() != null) {
							if (!player.getUsername().equalsIgnoreCase(item.getOwner()) && player.isIronMan()) {
								player.sendMessage("Ironmen cannot interact with other player owned items.");
								return;
							}
							if (!player.getUsername().equalsIgnoreCase(item.getOwner()) && player.isHCIronMan()) {
								player.sendMessage("Hardcore ironmen cannot interact with other player owned items.");
								return;
							}
							if (!player.getUsername().equalsIgnoreCase(item.getOwner()) && player.isWiki()) {
								player.sendMessage("Wiki cannot interact with other player owned items.");
								return;
							}
						}
					}
					player.setNextFaceWorldTile(tile);
					player.addWalkSteps(tile.getX(), tile.getY(), 1);
					if (Firemaking.isFiremaking(player, item.getId())) {
						World.removeGroundItem(player, item);
						return;
					}
				}
			}));
			if (Settings.DEBUG)
				System.out.println("Item id: " + item.getId() + ".");
		} else if (opcode == GROUND_ITEM_OPTION_EXAMINE) {
			if (!player.isActive() || !player.clientHasLoadedMapRegion() || player.isDead())
				return;
			long currentTime = Utils.currentTimeMillis();
			if (player.getLockDelay() > currentTime)
				return;
			int y = stream.readUnsignedShort();
			int x = stream.readUnsignedShortLE();
			final int id = stream.readInt();
			boolean forceRun = stream.read128Byte() == 1;
			final WorldTile tile = new WorldTile(x, y, player.getPlane());
			final int regionId = tile.getRegionId();
			if (!player.getMapRegionsIds().contains(regionId))
				return;
			final FloorItem item = World.getRegion(regionId).getGroundItem(id, tile, player);
			if (item == null)
				return;
			player.sendMessage(ItemExamines.getExamine(item));
		} else if (opcode == DONE_LOADING_REGION_PACKET) {
			if (!player.clientHasLoadedMapRegion()) { // load objects and items
				player.setClientHasLoadedMapRegion();
			}
			player.refreshSpawnedObjects();
		} else if (opcode == PUBLIC_QUICK_CHAT_PACKET) {
			if (!player.isActive())
				return;
			if (player.getLastPublicMessage() > Utils.currentTimeMillis())
				return;
			player.setLastPublicMessage(Utils.currentTimeMillis() + 300);
			boolean secondClientScript = stream.readByte() == 1;
			int fileId = stream.readUnsignedShort();
			if (!Utils.isValidQuickChat(fileId))
				return;
			byte[] data = null;
			if (length > 3) {
				data = new byte[length - 3];
				stream.readBytes(data);
			}
			data = Utils.completeQuickMessage(player, fileId, data);
			if (chatType == 0) {
				player.sendPublicChatMessage(new QuickChatMessage(fileId, data));
			} else if (chatType == 1) {
				player.sendFriendsChannelQuickMessage(new QuickChatMessage(fileId, data));
			} else if (chatType == 2) {
				player.sendClanChannelQuickMessage(new QuickChatMessage(fileId, data));
			} else if (chatType == 3) {
				player.sendGuestClanChannelQuickMessage(new QuickChatMessage(fileId, data));
			} else if (Settings.DEBUG)
				Logger.log(this, "Unknown chat type: " + chatType);
		} else if (opcode == ACTION_BUTTON1_PACKET || opcode == ACTION_BUTTON2_PACKET || opcode == ACTION_BUTTON4_PACKET
				|| opcode == ACTION_BUTTON5_PACKET || opcode == ACTION_BUTTON6_PACKET || opcode == ACTION_BUTTON7_PACKET
				|| opcode == ACTION_BUTTON8_PACKET || opcode == ACTION_BUTTON3_PACKET || opcode == ACTION_BUTTON9_PACKET
				|| opcode == ACTION_BUTTON10_PACKET) {
			ButtonHandler.handleButtons(player, stream, opcode);
			player.increaseAFKTimer();
		} else if (opcode == DROP_ITEM_PACKET) {
			@SuppressWarnings("unused")
			int interfaceHash = stream.readIntV2();
			final int slotId2 = stream.readUnsignedShort128();
			final int slotId = stream.readUnsignedShortLE128();
			if (slotId > 27 || player.getInterfaceManager().containsInventoryInter())
				return;
			Item item = player.getInventory().getItem(slotId);
			if (item == null || item.getId() != slotId2)
				return;
			InventoryOptionsHandler.handleItemOption7(player, slotId, slotId2, item);
			player.increaseAFKTimer();
		} else {
			if (Settings.DEBUG)
				Logger.log(this, "Unhandled Packet : " + opcode);
		}
	}
}