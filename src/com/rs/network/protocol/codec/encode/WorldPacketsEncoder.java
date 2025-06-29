package com.rs.network.protocol.codec.encode;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;

import com.rs.Protocol;
import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.DynamicRegion;
import com.rs.game.Entity;
import com.rs.game.Graphics;
import com.rs.game.NewProjectile;
import com.rs.game.Region;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.item.FloorItem;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.player.content.ChatMessage;
import com.rs.game.player.content.FriendChatsManager;
import com.rs.game.player.content.HintIcon;
import com.rs.game.player.content.PublicChatMessage;
import com.rs.game.player.content.QuickChatMessage;
import com.rs.game.player.content.clans.ClansManager;
import com.rs.game.player.content.grandExchange.Offer;
import com.rs.network.Session;
import com.rs.stream.OutputStream;
import com.rs.utils.Logger;
import com.rs.utils.Utils;
import com.rs.utils.huffman.Huffman;

public class WorldPacketsEncoder extends Encoder {

	private Player player;

	public WorldPacketsEncoder(Session session, Player player) {
		super(session);
		this.player = player;
	}

	public OutputStream createWorldTileStream(WorldTile tile) {
		OutputStream stream = new OutputStream(4);
		stream.writePacket(player, 158);
		stream.writeByte128(tile.getLocalY(player.getLastLoadedMapRegionTile(), player.getMapSize()) >> 3);
		stream.writeByteC(tile.getPlane());
		stream.write128Byte(tile.getLocalX(player.getLastLoadedMapRegionTile(), player.getMapSize()) >> 3);
		return stream;
	}

	public Player getPlayer() {
		return player;
	}

	public void receiveFriendChatMessage(String name, String display, int rights, String chatName, String message) {
		Player other = World.getPlayer(name);
		if (player.getFriendsIgnores().getIgnores().equals(name) && other.getRights() != 2)
			return;
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 139);
		stream.writeByte(name.equals(display) ? 0 : 1);
		stream.writeString(display);
		if (!name.equals(display))
			stream.writeString(name);
		stream.writeLong(Utils.stringToLong(chatName));
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utils.getRandom(255));
		stream.writeByte(rights);
		Huffman.encodeString(stream, message);
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void receiveFriendChatQuickMessage(String name, String display, int rights, String chatName,
			QuickChatMessage message) {
		Player other = World.getPlayer(name);
		if (player.getFriendsIgnores().getIgnores().equals(name) && other.getRights() != 2)
			return;
	}

	public void receivePrivateChatQuickMessage(String name, String display, int rights, QuickChatMessage message) {
		Player other = World.getPlayer(name);
		if (player.getFriendsIgnores().getIgnores().equals(name) && other.getRights() != 2)
			return;

	}

	public void receivePrivateMessage(String name, String display, int rights, String message) {
		Player other = World.getPlayer(name);
		if (player.getFriendsIgnores().getIgnores().equals(name) && other.getRights() != 2)
			return;
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 105);
		stream.writeByte(name.equals(display) ? 0 : 1);
		stream.writeString(display);
		if (!name.equals(display))
			stream.writeString(name);
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utils.getRandom(255));
		stream.writeByte(rights);
		Huffman.encodeString(stream, message);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void resetItems(int key, boolean negativeKey, int size) {
		sendItems(key, negativeKey, new Item[size]);
	}

	public void sendAccessMask(Player player, int interfaceId, int componentId, int fromSlot, int toSlot,
			int settingsHash) {
		OutputStream stream = new OutputStream(13);
		stream.writePacket(player, 40);
		stream.writeIntV2(settingsHash);
		stream.writeInt(interfaceId << 16 | componentId);
		stream.writeShort128(fromSlot);
		stream.writeShortLE(toSlot);
		session.write(stream);
	}

	/**
	 * This will blackout specified area.
	 * 
	 * @param byte
	 *            area = area which will be blackout (0 = unblackout; 1 = blackout
	 *            orb; 2 = blackout map; 5 = blackout orb and map)
	 */
	public void sendMiniMapStatus(int area) {
		OutputStream out = new OutputStream(2);
		out.writePacket(player, 69);
		out.writeByte(area);
		session.write(out);
	}

	// instant
	public void sendCameraLook(int viewLocalX, int viewLocalY, int viewZ) {
		sendCameraLook(viewLocalX, viewLocalY, viewZ, -1, -1);
	}

	public void sendCameraLook(int viewLocalX, int viewLocalY, int viewZ, int speed1, int speed2) {
		OutputStream stream = new OutputStream(6);
		stream.writePacket(player, 116);
		stream.writeByte128(viewLocalY);
		stream.writeByte(speed1);
		stream.writeByteC(viewLocalX);
		stream.writeByte(speed2);
		stream.writeShort128(viewZ >> 2);
		session.write(stream);
	}

	public void sendCameraPos(int moveLocalX, int moveLocalY, int moveZ) {
		sendCameraPos(moveLocalX, moveLocalY, moveZ, -1, -1);
	}

	public void sendCameraPos(int moveLocalX, int moveLocalY, int moveZ, int speed1, int speed2) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(player, 74);
		stream.writeByte128(speed2);
		stream.writeByte128(speed1);
		stream.writeByte(moveLocalY);
		stream.writeShort(moveZ >> 2);
		stream.writeByte(moveLocalX);
		session.write(stream);
	}

	public void sendCameraRotation(int unknown1, int unknown2) {
		OutputStream stream = new OutputStream(5);
		stream.writePacket(player, 123);
		stream.writeShort(unknown1);
		stream.writeShortLE(unknown1);
		session.write(stream);
	}

	public void sendCameraShake(int slotId, int b, int c, int d, int e) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(player, 44);
		stream.writeByte128(b);
		stream.writeByte128(slotId);
		stream.writeByte128(d);
		stream.writeByte128(c);
		stream.writeShortLE(e);
		session.write(stream);
	}

	public void sendClanWarsRequestMessage(Player p) {
		sendMessage(101, "wishes to challenge your clan to a clan war.", p);
	}

	public void sendClientConsoleCommand(String command) {

	}

	public void sendConfig(int id, int value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE) {
			sendConfig2(id, value);
		} else {
			sendConfig1(id, value);
		}
	}

	public void sendConfig1(int id, int value) {
		OutputStream stream = new OutputStream(4);
		stream.writePacket(player, 110);
		stream.writeShortLE128(id);
		stream.writeByte128(value);
		session.write(stream);
	}

	public void sendConfig2(int id, int value) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(player, 56);
		stream.writeShort128(id);
		stream.writeIntLE(value);
		session.write(stream);
	}

	public void sendConfigByFile(int fileId, int value, boolean newDefs) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
			sendConfigByFile2(fileId, value, newDefs);
		else
			sendConfigByFile1(fileId, value, newDefs);
	}

	public void sendConfigByFile(int fileId, int value) {
		sendConfigByFile(fileId, value, false);
	}

	public void sendConfigByFile1(int fileId, int value, boolean newDefs) {
		OutputStream stream = new OutputStream(5);
		stream.writePacket(player, 111);
		stream.writeShort128(fileId);
		stream.writeByteC(value);
		stream.writeByte(newDefs ? 1 : 0);
		session.write(stream);
	}

	public void sendConfigByFile2(int fileId, int value, boolean newDefs) {
		OutputStream stream = new OutputStream(8);
		stream.writePacket(player, 81);
		stream.writeIntV1(value);
		stream.writeShort128(fileId);
		stream.writeByte(newDefs ? 1 : 0);
		session.write(stream);
	}

	public void sendCutscene(int id) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 142);
		stream.writeShort(id);
		stream.writeShort(20); // xteas count
		for (int count = 0; count < 20; count++)
			// xteas
			for (int i = 0; i < 4; i++)
				stream.writeInt(0);
		byte[] appearence = player.getGlobalPlayerUpdater().getAppeareanceData();
		stream.writeByte(appearence.length);
		stream.writeBytes(appearence);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendSpawnedObject(WorldObject object) {
		OutputStream stream = createWorldTileStream(object);
		int localX = object.getLocalX(player.getLastLoadedMapRegionTile(), player.getMapSize());
		int localY = object.getLocalY(player.getLastLoadedMapRegionTile(), player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writePacket(player, 120);
		stream.writeByte((offsetX << 4) | offsetY);
		stream.writeByte((object.getType() << 2) + (object.getRotation() & 0x3));
		stream.writeIntLE(object.getId());
		session.write(stream);
	}

	public void sendDestroyObject(WorldObject object) {
		OutputStream stream = createWorldTileStream(object);
		int localX = object.getLocalX(player.getLastLoadedMapRegionTile(), player.getMapSize());
		int localY = object.getLocalY(player.getLastLoadedMapRegionTile(), player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writePacket(player, 45);
		stream.writeByteC((object.getType() << 2) + (object.getRotation() & 0x3));
		stream.writeByte128((offsetX << 4) | offsetY);
		session.write(stream);

	}

	public void sendDuelChallengeRequestMessage(Player p, boolean friendly) {
		sendMessage(101, "wishes to duel with you(" + (friendly ? "friendly" : "stake") + ").", p);
	}

	public void sendDungDuoRequestMessage(Player p, boolean friendly) {
		sendMessage(101, "wishes to play a duo Dungeoneering.", p);
	}

	/*
	 * dynamic map region
	 */
	public void sendDynamicMapRegion(boolean sendLswp) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 144);
		if (sendLswp)
			player.getLocalPlayerUpdate().init(stream);
		int middleChunkX = player.getChunkX();
		int middleChunkY = player.getChunkY();
		stream.write128Byte(2); // exists on newer protocol, triggers a
		// gamescene supporting npcs
		stream.writeShortLE(middleChunkY);
		stream.writeShortLE128(middleChunkX);
		stream.write128Byte(player.isForceNextMapLoadRefresh() ? 1 : 0);
		stream.writeByteC(player.getMapSize());
		stream.initBitAccess();
		int mapHash = Protocol.MAP_SIZES[player.getMapSize()] >> 4;
		int[] realRegionIds = new int[4 * mapHash * mapHash];
		int realRegionIdsCount = 0;
		for (int plane = 0; plane < 4; plane++) {
			for (int thisRegionX = (middleChunkX - mapHash); thisRegionX <= ((middleChunkX + mapHash)); thisRegionX++) {
				for (int thisRegionY = (middleChunkY - mapHash); thisRegionY <= ((middleChunkY
						+ mapHash)); thisRegionY++) {
					int regionId = (((thisRegionX / 8) << 8) + (thisRegionY / 8));
					Region region = World.getRegions().get(regionId);
					int realRegionX;
					int realRegionY;
					int realPlane;
					int rotation;
					if (region instanceof DynamicRegion) {
						DynamicRegion dynamicRegion = (DynamicRegion) region;
						int[] regionCoords = dynamicRegion.getRegionCoords()[plane][thisRegionX
								- ((thisRegionX / 8) * 8)][thisRegionY - ((thisRegionY / 8) * 8)];
						realRegionX = regionCoords[0];
						realRegionY = regionCoords[1];
						realPlane = regionCoords[2];
						rotation = regionCoords[3];
					} else {
						realRegionX = thisRegionX;
						realRegionY = thisRegionY;
						realPlane = plane;
						rotation = 0;
					}
					if (realRegionX == 0 || realRegionY == 0)
						stream.writeBits(1, 0);
					else {
						stream.writeBits(1, 1);
						stream.writeBits(26,
								(rotation << 1) | (realPlane << 24) | (realRegionX << 14) | (realRegionY << 3));
						int realRegionId = (((realRegionX / 8) << 8) + (realRegionY / 8));
						boolean found = false;
						for (int index = 0; index < realRegionIdsCount; index++)
							if (realRegionIds[index] == realRegionId) {
								found = true;
								break;
							}
						if (!found)
							realRegionIds[realRegionIdsCount++] = realRegionId;
					}

				}
			}
		}
		stream.finishBitAccess();
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendEntityOnIComponent(boolean isPlayer, int entityId, int interfaceId, int componentId) {
		if (isPlayer)
			sendPlayerOnIComponent(interfaceId, componentId);
		else
			sendNPCOnIComponent(interfaceId, componentId, entityId);
	}

	public void sendFaceOnIComponent(int interfaceId, int componentId, int look1, int look2, int look3) {
		/*
		 * OutputStream stream = new OutputStream(); stream.writePacket(player, 192);
		 * stream.writeIntV2(interfaceId << 16 | componentId);
		 * stream.writeShortLE128(interPacketsCount++); stream.writeShortLE128(look1);
		 * stream.writeShortLE128(look2); stream.writeShort128(look2);
		 * session.write(stream);
		 */
	}

	public void sendFriend(String username, String displayName, int world, boolean putOnline, boolean warnMessage) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 2);
		sendFriend(username, displayName, world, putOnline, warnMessage, stream);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendFriend(String username, String displayName, int world, boolean putOnline, boolean warnMessage,
			OutputStream stream) {
		stream.writeByte(warnMessage ? 0 : 1);
		stream.writeString(displayName);
		stream.writeString(displayName.equals(username) ? "" : username);
		stream.writeShort(putOnline ? world : 0);
		stream.writeByte(player.getFriendsIgnores().getRank(Utils.formatPlayerNameForProtocol(username)));
		stream.writeByte(0);
		if (putOnline) {
			stream.writeString(Settings.SERVER_NAME);
			stream.writeByte(0);
		}
	}

	public void sendFriends() {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 2);
		for (String username : player.getFriendsIgnores().getFriends()) {
			String displayName;
			Player p2 = World.getPlayerByDisplayName(username);
			if (p2 != null)
				displayName = p2.getDisplayName();
			else
				displayName = Utils.formatString(username);
			player.getPackets().sendFriend(Utils.formatString(username), displayName, 1,
					p2 != null && player.getFriendsIgnores().isOnline(p2), false, stream);
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendFriendsChatChannel() {
		FriendChatsManager manager = player.getCurrentFriendChat();
		OutputStream stream = new OutputStream(manager == null ? 3 : manager.getDataBlock().length + 3);
		stream.writePacketVarShort(player, 117);
		if (manager != null)
			stream.writeBytes(manager.getDataBlock());
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendGameBarStages() {
		sendConfig(1054, 0);
		sendConfig(1055, 0);
		sendConfig(1056, 0);
		sendConfig(2159, 0);
		sendOtherGameBarStages();
		sendPrivateGameBarStage();
	}

	public void sendOtherGameBarStages() {
		OutputStream stream = new OutputStream(3);
		stream.writePacket(player, 89);
		stream.write128Byte(player.getTradeStatus());
		stream.writeByte(player.getPublicStatus());
		session.write(stream);
	}

	public void sendPrivateGameBarStage() {
		OutputStream stream = new OutputStream(2);
		stream.writePacket(player, 75);
		stream.writeByte(player.getFriendsIgnores().getPrivateStatus());
		session.write(stream);
	}

	public void sendGameMessage(String text) {
		sendGameMessage(text, false);
	}

	public void sendClientConsoleMessage(String text) {
		sendMessage(99, text, null);
	}

	public void sendGameMessage(String text, boolean filter) {
		sendMessage(filter ? 109 : 0, text, null);
	}

	public void sendGlobalConfig(int id, int value) {
		if (value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
			sendGlobalConfig2(id, value);
		else
			sendGlobalConfig1(id, value);
	}

	public void sendGlobalConfig1(int id, int value) {
		OutputStream stream = new OutputStream();
		stream.writePacket(player, 154);
		stream.writeByteC(value);
		stream.writeShort128(id);
		session.write(stream);
	}

	public void sendGlobalConfig2(int id, int value) {
		OutputStream stream = new OutputStream();
		stream.writePacket(player, 63);
		stream.writeShort128(id);
		stream.writeInt(value);
		session.write(stream);
	}

	public void sendGlobalString(int id, String string) {
		OutputStream stream = new OutputStream();
		if (string.length() >= 253) {
			stream.writePacketVarShort(player, 34);
			stream.writeString(string);
			stream.writeShort(id);
			stream.endPacketVarShort();
		} else {
			stream.writePacketVarByte(player, 134);
			stream.writeShort(id);
			stream.writeString(string);
			stream.endPacketVarByte();
		}
		session.write(stream);
	}

	public void sendGraphics(Graphics graphics, Object target) {
		OutputStream stream = new OutputStream(13);
		int hash = 0;
		if (target instanceof Player) {
			Player p = (Player) target;
			hash = p.getIndex() & 0xffff | 1 << 28;
		} else if (target instanceof NPC) {
			NPC n = (NPC) target;
			hash = n.getIndex() & 0xffff | 1 << 29;
		} else {
			WorldTile tile = (WorldTile) target;
			hash = tile.getPlane() << 28 | tile.getX() << 14 | tile.getY() & 0x3fff | 1 << 30;
		}
		stream.writePacket(player, 90);
		stream.writeShort(graphics.getId());
		stream.writeByte128(0); // slot id used for entitys
		stream.writeShort(graphics.getSpeed());
		stream.writeByte128(graphics.getSettings2Hash());
		stream.writeShort(graphics.getHeight());
		stream.writeIntLE(hash);
		session.write(stream);
	}

	public void sendGroundItem(FloorItem item) {
		OutputStream stream = createWorldTileStream(item.getTile());
		int localX = item.getTile().getLocalX(player.getLastLoadedMapRegionTile(), player.getMapSize());
		int localY = item.getTile().getLocalY(player.getLastLoadedMapRegionTile(), player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writePacket(player, 125);
		stream.writeByte128((offsetX << 4) | offsetY);
		stream.writeShortLE128(item.getAmount());
		stream.writeShortLE(item.getId());
		session.write(stream);
	}

	public void sendHideIComponent(int interfaceId, int componentId, boolean hidden) {
		OutputStream stream = new OutputStream(6);
		stream.writePacket(player, 112);
		stream.writeIntV2(interfaceId << 16 | componentId);
		stream.writeByte(hidden ? 1 : 0);
		session.write(stream);
	}

	public void sendHintIcon(HintIcon icon) {
		OutputStream stream = new OutputStream(15);
		stream.writePacket(player, 79);
		stream.writeByte((icon.getTargetType() & 0x1f) | (icon.getIndex() << 5));
		if (icon.getTargetType() == 0)
			stream.skip(13);
		else {
			stream.writeByte(icon.getArrowType());
			if (icon.getTargetType() == 1 || icon.getTargetType() == 10) {
				stream.writeShort(icon.getTargetIndex());
				stream.writeShort(2500); // how often the arrow flashes, 2500
				// ideal, 0 never
				stream.skip(4);
			} else if ((icon.getTargetType() >= 2 && icon.getTargetType() <= 6)) { // directions
				stream.writeByte(icon.getPlane()); // unknown
				stream.writeShort(icon.getCoordX());
				stream.writeShort(icon.getCoordY());
				stream.writeByte(icon.getDistanceFromFloor() * 4 >> 2);
				stream.writeShort(-1); // distance to start showing on minimap,
				// 0 doesnt show, -1 infinite
			}
			stream.writeInt(icon.getModelId());
		}
		session.write(stream);
	}

	public void sendIComponentAnimation(int emoteId, int interfaceId, int componentId) {
		OutputStream stream = new OutputStream(9);
		stream.writePacket(player, 103);
		stream.writeIntV2(emoteId);
		stream.writeInt(interfaceId << 16 | componentId);
		session.write(stream);
	}

	public void sendIComponentModel(int interfaceId, int componentId, int modelId) {
		OutputStream stream = new OutputStream(9);
		stream.writePacket(player, 102);
		stream.writeIntV1(modelId);
		stream.writeIntV1(interfaceId << 16 | componentId);
		session.write(stream);
	}

	public void sendIComponentSettings(int interfaceId, int componentId, int fromSlot, int toSlot, int settingsHash) {
		OutputStream stream = new OutputStream(13);
		stream.writePacket(player, 40);
		stream.writeIntV2(settingsHash);
		stream.writeInt(interfaceId << 16 | componentId);
		stream.writeShort128(fromSlot);
		stream.writeShortLE(toSlot);
		session.write(stream);
	}

	public void sendInterfaceConfig(int interfaceId, int componentId, boolean hide) {
		OutputStream stream = new OutputStream(6);
		stream.writePacket(player, 112);
		stream.writeIntV2(interfaceId << 16 | componentId);
		stream.writeByte(hide ? 1 : 0);
		session.write(stream);
	}

	public void sendIComponentText(int interfaceId, int componentId, String text) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 135);
		stream.writeString(text);
		stream.writeInt(interfaceId << 16 | componentId);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendIgnore(String name, String display, boolean updateName) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 128);
		stream.writeByte(0x2);
		stream.writeString(display.equals(name) ? name : display);
		stream.writeString("");
		stream.writeString(display.equals(name) ? "" : name);
		stream.writeString("");
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendIgnores() {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 55);
		stream.writeByte(player.getFriendsIgnores().getIgnores().size());
		for (String username : player.getFriendsIgnores().getIgnores()) {
			String display;
			Player p2 = World.getPlayerByDisplayName(username);
			if (p2 != null)
				display = p2.getDisplayName();
			else
				display = Utils.formatString(username);
			String name = Utils.formatString(username);
			stream.writeString(display.equals(name) ? name : display);
			stream.writeString("");
			stream.writeString(display.equals(name) ? "" : name);
			stream.writeString("");
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendInputIntegerScript(boolean integerEntryOnly, String message) {
		sendRunScript(108, new Object[] { message });
	}

	public void sendInputLongTextScript(String message) {
		sendRunScript(110, new Object[] { message });
	}

	public void sendInputNameScript(String message) {
		sendRunScript(109, new Object[] { message });
	}

	public void sendRootInterface(int id, int type) {
		int[] xteas = new int[4];
		player.getInterfaceManager().setWindowsPane(id);
		OutputStream stream = new OutputStream(4);
		stream.writePacket(player, 39);
		stream.write128Byte(type);
		stream.writeShort128(id);
		stream.writeIntLE(xteas[1]);
		stream.writeIntV2(xteas[0]);
		stream.writeInt(xteas[3]);
		stream.writeInt(xteas[2]);
		session.write(stream);
	}

	public void sendInterface(boolean clickThrought, int parentUID, int interfaceId) {
		int[] xteas = new int[4];
		OutputStream stream = new OutputStream(24);
		stream.writePacket(player, 14);
		stream.writeShort(interfaceId);
		stream.writeInt(xteas[0]);
		stream.writeIntV2(xteas[1]);
		stream.writeIntV1(parentUID);
		stream.writeByte(clickThrought ? 1 : 0);
		stream.writeIntV1(xteas[3]);
		stream.writeIntV2(xteas[2]);
		session.write(stream);
	}

	public void sendInterFlashScript(int interfaceId, int componentId, int width, int height, int slot) {
		Object[] parameters = new Object[4];
		int index = 0;
		parameters[index++] = slot;
		parameters[index++] = height;
		parameters[index++] = width;
		parameters[index++] = interfaceId << 16 | componentId;
		sendRunScript(143, parameters);
	}

	public void closeInterface(int windowComponentId) {
		closeInterface(player.getInterfaceManager().getTabWindow(windowComponentId), windowComponentId);
		player.getInterfaceManager().removeTab(windowComponentId);
	}

	public void closeInterface(int windowId, int windowComponentId) {
		OutputStream stream = new OutputStream(5);
		stream.writePacket(player, 5);
		stream.writeIntLE(windowId << 16 | windowComponentId);
		session.write(stream);
	}

	public void sendInterface(boolean nocliped, int windowId, int windowComponentId, int interfaceId) {
		// currently fixes the inter engine.. not ready for same component
		// ids(tabs), different inters
		if (!(windowId == 752 && (windowComponentId == 9 || windowComponentId == 12))) { // if
																							// chatbox
			if (player.getInterfaceManager().containsInterface(windowComponentId, interfaceId))
				closeInterface(windowComponentId);
			if (!player.getInterfaceManager().addInterface(windowId, windowComponentId, interfaceId)) {
				Logger.log(this,
						"Error adding interface: " + windowId + " , " + windowComponentId + " , " + interfaceId);
				return;
			}
		}
		int[] xteas = new int[4];
		OutputStream stream = new OutputStream(24);
		stream.writePacket(player, 14);
		stream.writeShort(interfaceId);
		stream.writeInt(xteas[0]);
		stream.writeIntV2(xteas[1]);
		stream.writeIntV1(windowId << 16 | windowComponentId);
		stream.writeByte(nocliped ? 1 : 0);
		stream.writeIntV1(xteas[3]);
		stream.writeIntV2(xteas[2]);
		session.write(stream);
	}

	public void sendInterSetItemsOptionsScript(int interfaceId, int componentId, int key, boolean negativeKey,
			int width, int height, String... options) {
		Object[] parameters = new Object[6 + options.length];
		int index = 0;
		for (int count = options.length - 1; count >= 0; count--)
			parameters[index++] = options[count];
		parameters[index++] = -1; // dunno but always this
		parameters[index++] = 0;// dunno but always this, maybe startslot?
		parameters[index++] = height;
		parameters[index++] = width;
		parameters[index++] = key;
		parameters[index++] = interfaceId << 16 | componentId;
		sendRunScript(negativeKey ? 695 : 150, parameters); // scriptid 150 does
		// that the method
		// name says*/
	}

	public void sendInterSetItemsOptionsScript(int interfaceId, int componentId, int key, int width, int height,
			String... options) {
		sendInterSetItemsOptionsScript(interfaceId, componentId, key, false, width, height, options);
	}

	public void sendInventoryMessage(int border, int slotId, String message) {
		sendGameMessage(message);
		sendRunScript(948, border, slotId, message);
	}

	public void sendItemOnIComponent(int interfaceid, int componentId, int id, int amount) {
		OutputStream stream = new OutputStream(11);
		stream.writePacket(player, 152);
		stream.writeShort128(id);
		stream.writeIntV1(amount);
		stream.writeIntV2(interfaceid << 16 | componentId);
		session.write(stream);
	}

	public void sendItems(int key, boolean negativeKey, Item[] items) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 77);
		stream.writeShort(key); // negativeKey ? -key : key
		stream.writeByte(negativeKey ? 1 : 0);
		stream.writeShort(items.length);
		for (int index = 0; index < items.length; index++) {
			Item item = items[index];
			int id = -1;
			int amount = 0;
			if (item != null) {
				id = item.getId();
				amount = item.getAmount();
			}
			stream.writeShortLE128(id + 1);
			stream.writeByte128(amount >= 255 ? 255 : amount);
			if (amount >= 255)
				stream.writeIntV1(amount);
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendItems(int key, boolean negativeKey, ItemsContainer<Item> items) {
		sendItems(key, negativeKey, items.getItems());
	}

	public void sendItems(int key, Item[] items) {
		sendItems(key, key < 0, items);
	}

	public void sendItems(int key, ItemsContainer<Item> items) {
		sendItems(key, key < 0, items);
	}

	/*
	 * sends local npcs update
	 */
	public void sendLocalNPCsUpdate() {
		session.write(player.getLocalNPCUpdate().createPacketAndProcess());
	}

	/*
	 * sends local players update
	 */
	public void sendLocalPlayersUpdate() {
		
		    // Skip packet updates for bots
		    if (player == null || player.isBot()) {
		        return;
		    }
		    
		    // Skip players without valid sessions
		    if (player.getSession() == null) {
		        return;
		    }
		session.write(player.getLocalPlayerUpdate().createPacketAndProcess());
	}

	
	public void sendLogout() {
		OutputStream stream = new OutputStream(1);
		stream.writePacket(player, 60);
		ChannelFuture future = session.write(stream);
		if (future != null)
			future.addListener(ChannelFutureListener.CLOSE);
		else
			session.getChannel().close();
	}

	/*
	 * normal map region
	 */
	// try this
	public void sendMapRegion(boolean sendLswp) {
		sendGameScene(sendLswp);
	}

	public void sendGameScene(boolean sendLswp) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 42);
		if (sendLswp)
			player.getLocalPlayerUpdate().init(stream);
		stream.writeByteC(player.getMapSize());
		stream.writeByte(player.isForceNextMapLoadRefresh() ? 1 : 0);
		stream.writeShort(player.getChunkX());
		stream.writeShort(player.getChunkY());
		stream.writeByteC(9);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendMessage(int type, String text, Player p) {
		if (!player.togglePouchMessages && (text.toLowerCase().contains("your money pouch")
				&& !text.toLowerCase().startsWith("your money pouch"))) {
			return;
		}
		int maskData = 0;
		if (p != null) {
			maskData |= 0x1;
			if (p.hasDisplayName())
				maskData |= 0x2;
		}
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 136);
		stream.writeSmart(type);
		stream.writeInt(player.getTileHash()); // junk, not used by client
		stream.writeByte(maskData);
		if ((maskData & 0x1) != 0) {
			stream.writeString(p.getDisplayName());
			if (p.hasDisplayName())
				stream.writeString(Utils.formatString(p.getUsername()));
		}
		stream.writeString(text);
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendNPCMessage(int border, NPC npc, String message) {
		sendGameMessage(message);
	}

	public void sendNPCOnIComponent(int interfaceId, int componentId, int npcId) {
		OutputStream stream = new OutputStream(9);
		stream.writePacket(player, 31);
		stream.writeInt(npcId);
		stream.writeInt(interfaceId << 16 | componentId);
		session.write(stream);
	}

	public void sendObjectAnimation(WorldObject object, Animation animation) {
		OutputStream stream = new OutputStream(10);
		stream.writePacket(player, 76);
		stream.writeInt(animation.getIds()[0]);
		stream.writeByteC((object.getType() << 2) + (object.getRotation() & 0x3));
		stream.writeIntLE(object.getTileHash());
		session.write(stream);
	}

	public void sendOpenURL(String url) {
		OutputStream stream = new OutputStream(1);
		stream.writePacketVarShort(player, 17);
		stream.writeByte(0);
		stream.writeString(url);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendPanelBoxMessage(String text) {
		sendMessage(player.getRights() == 2 ? 99 : 0, text, null);
	}

	public void sendPlayerOnIComponent(int interfaceId, int componentId) {
		OutputStream stream = new OutputStream(5);
		stream.writePacket(player, 23);
		stream.writeIntV2(interfaceId << 16 | componentId);
		session.write(stream);
	}

	public void sendPlayerOption(String option, int slot, boolean top) {
		sendPlayerOption(option, slot, top, -1);
	}

	public void sendPlayerOption(String option, int slot, boolean top, int cursor) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 118);
		stream.writeByte128(slot);
		stream.writeString(option);
		stream.writeShortLE128(cursor);
		stream.writeByteC(top ? 1 : 0);
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendPlayerUnderNPCPriority(boolean priority) {
		OutputStream stream = new OutputStream(2);
		stream.writePacket(player, 6);
		stream.writeByteC(priority ? 1 : 0);
		session.write(stream);
	}

	public void sendPrivateMessage(String username, String message) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 130);
		stream.writeString(username);
		Huffman.encodeString(stream, message);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendPrivateQuickMessageMessage(String username, QuickChatMessage message) {

	}

	public void sendProjectile(Entity receiver, WorldTile startTile, WorldTile endTile, int gfxId, int startHeight,
			int endHeight, int speed, int delay, int curve, int startDistanceOffset, int creatorSize) {
		OutputStream stream = createWorldTileStream(startTile);
		stream.writePacket(player, 20);
		int localX = startTile.getLocalX(player.getLastLoadedMapRegionTile(), player.getMapSize());
		int localY = startTile.getLocalY(player.getLastLoadedMapRegionTile(), player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writeByte((offsetX << 3) | offsetY);
		stream.writeByte(endTile.getX() - startTile.getX());
		stream.writeByte(endTile.getY() - startTile.getY());
		stream.writeShort(receiver == null ? 0
				: (receiver instanceof Player ? -(receiver.getIndex() + 1) : receiver.getIndex() + 1));
		stream.writeShort(gfxId);
		stream.writeByte(startHeight);
		stream.writeByte(endHeight);
		stream.writeShort(delay);
		int duration = (Utils.getDistance(startTile.getX(), startTile.getY(), endTile.getX(), endTile.getY()) * 30
				/ ((speed / 10) < 1 ? 1 : (speed / 10))) + delay;
		stream.writeShort(duration);
		stream.writeByte(curve);
		stream.writeShort(creatorSize * 64 + startDistanceOffset * 64);
		session.write(stream);
	}

	public void sendPublicMessage(Player p, PublicChatMessage message) {
		OutputStream stream = new OutputStream(1);
		stream.writePacketVarByte(player, 106);
		stream.writeShort(p.getIndex());
		stream.writeShort(message.getEffects());
		stream.writeByte(p.getMessageIcon());
		if (message instanceof QuickChatMessage) {
			QuickChatMessage qcMessage = (QuickChatMessage) message;
			stream.writeShort(qcMessage.getFileId());
			if (qcMessage.getMessage() != null)
				stream.writeBytes(message.getMessage().getBytes());
		} else {
			Huffman.encodeString(stream, message.getMessage());
		}
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void sendRandomOnIComponent(int interfaceId, int componentId, int id) {
		/*
		 * OutputStream stream = new OutputStream(); stream.writePacket(player, 235);
		 * stream.writeShort(id); stream.writeIntV1(interfaceId << 16 | componentId);
		 * stream.writeShort(interPacketsCount++); session.write(stream);
		 */
	}

	public void sendRemoveGroundItem(FloorItem item) {
		OutputStream stream = createWorldTileStream(item.getTile());
		stream.writePacket(player, 108);
		stream.writeShort128(item.getId());
		stream.write128Byte((item.getTile().getXInChunk() << 4) | item.getTile().getYInChunk());
		session.write(stream);

	}

	public void sendResetCamera() {
		OutputStream stream = new OutputStream(1);
		stream.writePacket(player, 95);
		session.write(stream);
	}

	public void sendRunEnergy() {
		OutputStream stream = new OutputStream(2);
		stream.writePacket(player, 25);
		stream.writeByte(player.getRunEnergy());
		session.write(stream);
	}

	public void sendRunScript(int scriptId, Object... params) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 119);
		String parameterTypes = "";
		if (params != null) {
			for (int count = params.length - 1; count >= 0; count--) {
				if (params[count] instanceof String)
					parameterTypes += "s"; // string
				else
					parameterTypes += "i"; // integer
			}
		}
		stream.writeString(parameterTypes);
		if (params != null) {
			int index = 0;
			for (int count = parameterTypes.length() - 1; count >= 0; count--) {
				if (parameterTypes.charAt(count) == 's')
					stream.writeString((String) params[index++]);
				else
					stream.writeInt((Integer) params[index++]);
			}
		}
		stream.writeInt(scriptId);
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendSkillLevel(int skill) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(player, 146);
		stream.write128Byte(skill);
		stream.writeInt((int) player.getSkills().getXp(skill));
		stream.writeByte128(player.getSkills().getLevel(skill));
		session.write(stream);
	}

	public void sendSound(int id, int delay, int effectType) {
		if (effectType == 1)
			sendIndex14Sound(id, delay);
		else if (effectType == 2)
			sendIndex15Sound(id, delay);
	}

	public void sendVoice(int id) {
		resetSounds();
		sendSound(id, 0, 2);
	}

	public void resetSounds() {
		OutputStream stream = new OutputStream(1);
		stream.writePacket(player, 145);
		session.write(stream);
	}

	public void sendIndex14Sound(int id, int delay) {
		OutputStream stream = new OutputStream(9);
		stream.writePacket(player, 26);
		stream.writeShort(id);
		stream.writeByte(1);// repeated amount
		stream.writeShort(delay);
		stream.writeByte(255);
		stream.writeShort(256);
		session.write(stream);
	}

	public void sendIndex15Sound(int id, int delay) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(player, 70);
		stream.writeShort(id);
		stream.writeByte(1); // amt of times it repeats
		stream.writeShort(delay);
		stream.writeByte(255); // volume
		session.write(stream);
	}

	public void sendMusicEffect(int id) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(player, 9);
		stream.write128Byte(255); // volume
		stream.write24BitIntegerV2(0);
		stream.writeShort(id);
		session.write(stream);
	}

	public void sendMusic(int id) {
		sendMusic(id, 100, 255);
	}

	public void sendMusic(int id, int delay, int volume) {
		OutputStream stream = new OutputStream(5);
		stream.writePacket(player, 129);
		stream.writeByte(delay);
		stream.writeShortLE128(id);
		stream.writeByte128(volume);
		session.write(stream);
	}

	public void sendStopCameraShake() {
		OutputStream stream = new OutputStream(1);
		stream.writePacket(player, 131);
		session.write(stream);
	}

	public void sendSystemUpdate(int delay) {
		OutputStream stream = new OutputStream(3);
		stream.writePacket(player, 141);
		stream.writeShort(delay * 50 / 30);
		session.write(stream);
	}

	// CUTSCENE PACKETS START

	public void sendTileMessage(String message, WorldTile tile, int color) {
		sendTileMessage(message, tile, 5000, 255, color);
	}

	public void sendTileMessage(String message, WorldTile tile, int delay, int height, int color) {

	}

	public void sendTradeRequestMessage(Player p) {
		sendMessage(100, "wishes to trade with you.", p);
	}

	public void sendUnlockIComponentOptionSlots(int interfaceId, int componentId, int fromSlot, int toSlot,
			int... optionsSlots) {
		int settingsHash = 0;
		for (int slot : optionsSlots)
			settingsHash |= 2 << slot;
		sendIComponentSettings(interfaceId, componentId, fromSlot, toSlot, settingsHash);
	}

	public void sendUnlockIComponentOptionSlots(int interfaceId, int componentId, int fromSlot, int toSlot,
			boolean unlockEvent, int... optionsSlots) {
		int settingsHash = unlockEvent ? 1 : 0;
		for (final int slot : optionsSlots)
			settingsHash |= 2 << slot;
		sendIComponentSettings(interfaceId, componentId, fromSlot, toSlot, settingsHash);
	}

	/*
	 * useless, sending friends unlocks it
	 */
	public void sendUnlockIgnoreList() {
		OutputStream stream = new OutputStream(1);
		stream.writePacket(player, 18);
		session.write(stream);
	}

	public void sendUpdateItems(int key, boolean negativeKey, Item[] items, int... slots) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 138);
		stream.writeShort(key);
		stream.writeByte(negativeKey ? 1 : 0);
		for (int slotId : slots) {
			if (slotId >= items.length)
				continue;
			stream.writeSmart(slotId);
			int id = -1;
			int amount = 0;
			Item item = items[slotId];
			if (item != null) {
				id = item.getId();
				amount = item.getAmount();
			}
			stream.writeShort(id + 1);
			if (id != -1) {
				stream.writeByte(amount >= 255 ? 255 : amount);
				if (amount >= 255)
					stream.writeInt(amount);
			}
		}
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendUpdateItems(int key, Item[] items, int... slots) {
		sendUpdateItems(key, key < 0, items, slots);
	}

	public void sendUpdateItems(int key, ItemsContainer<Item> items, int... slots) {
		sendUpdateItems(key, items.getItems(), slots);
	}

	/*
	 * sets the pane interface
	 */
	public void sendWindowsPane(int id, int type) {
		int[] xteas = new int[4];
		if (player.getInterfaceManager().getWindowsPane() == 755 && type != 2)
			return;
		player.getInterfaceManager().setWindowsPane(id);
		OutputStream stream = new OutputStream();
		stream.writePacket(player, 39);
		stream.write128Byte(type);
		stream.writeShort128(id);
		stream.writeIntLE(xteas[1]);
		stream.writeIntV2(xteas[0]);
		stream.writeInt(xteas[3]);
		stream.writeInt(xteas[2]);
		session.write(stream);
	}

	public void sendWorldTile(WorldTile tile) {
		session.write(createWorldTileStream(tile));
	}

	/**
	 * Sends data to G.E interface slots.
	 * 
	 * @param slot
	 * @param progress
	 * @param item
	 * @param price
	 * @param amount
	 * @param currentAmount
	 */
	public void sendGE(int slot, int progress, int item, int price, int amount, int currentAmount) {
		OutputStream stream = new OutputStream();
		stream.writePacket(player, 53);// packet id
		stream.writeByte(slot);
		stream.writeByte(progress);
		stream.writeShort(item);
		stream.writeInt(price);
		stream.writeInt(amount);
		stream.writeInt(currentAmount);
		stream.writeInt(price * currentAmount);
		session.write(stream);
	}

	public void resetGE(int i) {
		OutputStream stream = new OutputStream();
		stream.writePacket(player, 53);// packet id
		stream.writeByte(i);
		stream.writeByte(0);
		stream.writeShort(0);
		stream.writeInt(0);
		stream.writeInt(0);
		stream.writeInt(0);
		stream.writeInt(0);
		session.write(stream);
	}

	public void sendMinimapFlag(int x, int y) {
		OutputStream stream = new OutputStream(3);
		stream.writePacket(player, 13);
		stream.writeByte128(y);
		stream.writeByte128(x);
		session.write(stream);
	}

	public void sendResetMinimapFlag() {
		sendMinimapFlag(255, 255);
	}

	public void sendClanChannel(ClansManager manager, boolean myClan) {
		OutputStream stream = new OutputStream(manager == null ? 4 : manager.getClanChannelDataBlock().length + 4);
		stream.writePacketVarShort(player, 85);
		stream.writeByte(myClan ? 1 : 0);
		if (manager != null)
			stream.writeBytes(manager.getClanChannelDataBlock());
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendClanSettings(ClansManager manager, boolean myClan) {
		OutputStream stream = new OutputStream(manager == null ? 4 : manager.getClanSettingsDataBlock().length + 4);
		stream.writePacketVarShort(player, 133);
		stream.writeByte(myClan ? 1 : 0);
		if (manager != null)
			stream.writeBytes(manager.getClanSettingsDataBlock());
		stream.endPacketVarShort();
		session.write(stream);
	}

	public void sendClanInviteMessage(Player p) {
		sendMessage(117, p.getDisplayName() + " is inviting you to join their clan.", p);
	}

	public void sendIComponentSprite(int interfaceId, int componentId, int spriteId) {// try
		OutputStream stream = new OutputStream(8);
		stream.writePacket(player, 121);
		stream.writeInt(spriteId);
		stream.writeIntV2(interfaceId << 16 | componentId);
		session.write(stream);
	}

	public void receiveClanChatQuickMessage(boolean myClan, String display, int rights, QuickChatMessage message) {
		Player other = World.getPlayerByDisplayName(display);
		if (player.getFriendsIgnores().getIgnores().equals(other.getUsername()) && other.getRights() != 2)
			return;
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 1);
		stream.writeByte(myClan ? 1 : 0);
		stream.writeString(display);
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utils.getRandom(255));
		stream.writeByte(rights);
		stream.writeShort(message.getFileId());
		if (message.getMessage() != null)
			stream.writeBytes(message.getMessage().getBytes());
		stream.endPacketVarByte();
		session.write(stream);
	}

	public void receiveClanChatMessage(boolean myClan, String display, int rights, ChatMessage message) {
		Player other = World.getPlayerByDisplayName(display);
		if (player.getFriendsIgnores().getIgnores().equals(other.getUsername()) && other.getRights() != 2)
			return;
		OutputStream stream = new OutputStream();
		stream.writePacketVarByte(player, 3);
		stream.writeByte(myClan ? 1 : 0);
		stream.writeString(display);
		for (int i = 0; i < 5; i++)
			stream.writeByte(Utils.getRandom(255));
		stream.writeByte(rights);
		Huffman.encodeString(stream, message.getMessage(false));
		stream.endPacketVarByte();
		session.write(stream);
	}

	@Deprecated
	public void sendVar(int id, int value) {
		sendConfig(id, value);
	}

	@Deprecated
	public void sendVarBit(int id, int value) {
		sendConfigByFile(id, value);
	}

	public void sendGrandExchangeOffer(Offer offer) {
		OutputStream stream = new OutputStream(21);
		stream.writePacket(player, 53);
		stream.writeByte(offer.getSlot());
		stream.writeByte(offer.getStage());
		if (offer.forceRemove())
			stream.skip(18);
		else {
			stream.writeShort(offer.getId());
			stream.writeInt(offer.getPrice());
			stream.writeInt(offer.getAmount());
			stream.writeInt(offer.getTotalAmountSoFar());
			stream.writeInt(offer.getTotalPriceSoFar());
		}
		session.write(stream);
	}

	public void sendRunScriptBlank(int scriptId) {
		sendRunScript(scriptId, new Object[] {});
	}

	public void sendFilteredGameMessage(boolean filter, String text, Object... args) {
		sendMessage(filter ? 109 : 0, String.format(text, args), null);
	}

	/**
	 * This will blackout specified area.
	 * 
	 * @param area
	 *            area = area which will be blackout (0 = unblackout; 1 = blackout
	 *            orb; 2 = blackout map; 5 = blackout orb and map)
	 */
	public void sendBlackOut(int area) {
		OutputStream out = new OutputStream(2);
		out.writePacket(player, 69);
		out.writeByte(area);
		session.write(out);
	}

	public void refreshWeight() {
		final OutputStream stream = new OutputStream();
		stream.writePacket(player, 98);
		stream.writeShort((int) player.getWeight());
		session.write(stream);
	}

	public void sendNPCMessage(int border, int color, NPC npc, String message) {
		sendGameMessage(message);
		sendGlobalString(306, message);
		sendGlobalConfig(1699, color);
		sendGlobalConfig(1700, border);
		sendGlobalConfig(1695, 1);
		sendNPCInterface(npc, true, 746, 0, 1177);
	}

	public void sendNPCInterface(NPC npc, boolean nocliped, int windowId, int windowComponentId, int interfaceId) {
		int[] xteas = new int[4];
		OutputStream stream = new OutputStream(26);
		stream.writePacket(player, 57);
		stream.writeIntV2(xteas[0]);
		stream.writeShortLE128(npc.getIndex());
		stream.writeByte128(nocliped ? 1 : 0);
		stream.writeInt(xteas[3]);
		stream.writeShortLE128(interfaceId);
		stream.writeIntLE(xteas[2]);
		stream.writeIntV2(xteas[1]);
		stream.writeIntV1(windowId << 16 | windowComponentId);
		session.write(stream);
	}

	public void sendDungeonneringRequestMessage(Player p) {
		sendMessage(111, "has invited you to a dungeon party.", p);
	}

	public void sendPouchInfusionOptionsScript(boolean dung, int interfaceId, int componentId, int slotLength,
			int width, int height, String... options) {
		Object[] parameters = new Object[5 + options.length];
		int index = 0;
		if (dung) {
			parameters[index++] = 1159;
			parameters[index++] = 1100;
		} else {
			parameters[index++] = slotLength;
			parameters[index++] = 1;
		}
		parameters[index++] = height;
		parameters[index++] = width;
		parameters[index++] = interfaceId << 16 | componentId;
		for (int count = options.length - 1; count >= 0; count--)
			parameters[index++] = options[count];
		sendRunScript(757, parameters);
	}

	public void sendScrollInfusionOptionsScript(boolean dung, int interfaceId, int componentId, int slotLength,
			int width, int height, String... options) {
		Object[] parameters = new Object[5 + options.length];
		int index = 0;
		if (dung) {
			parameters[index++] = 1159;
			parameters[index++] = 1100;
		} else {
			parameters[index++] = slotLength;
			parameters[index++] = 1;
		}
		parameters[index++] = height;
		parameters[index++] = width;
		parameters[index++] = interfaceId << 16 | componentId;
		for (int count = options.length - 1; count >= 0; count--)
			parameters[index++] = options[count];
		sendRunScript(763, parameters);
	}

	public void sendSoundEffect(int id) {
		sendSoundEffect(id, 0, 255);
	}

	public void sendSoundEffect(int id, int delay, int volume) {
		OutputStream stream = new OutputStream(7);
		stream.writePacket(player, 133);
		stream.writeShort(id);
		stream.writeByte(1); // amt of times it repeats
		stream.writeShort(delay);
		stream.writeByte(volume); // volume*/

		/*
		 * stream.writePacket(player, 22); stream.writeShort(id); stream.writeByte(1);
		 * // amt of times it repeats stream.writeShort(delay);
		 * stream.writeByte(volume); stream.writeShort(256);
		 */

		session.write(stream);
	}

	public void sendPlayerMessage(int border, int color, String message, boolean sendGameMessage) {
		sendPlayerMessage(border, color, player, message, sendGameMessage);
	}

	public void sendPlayerMessage(int border, int color, Player p, String message, boolean sendGameMessage) {
		if (sendGameMessage)
			sendGameMessage(message);
		sendGlobalString(306, message);
		sendGlobalConfig(1699, color);
		sendGlobalConfig(1700, border);
		sendGlobalConfig(1695, 1);
		sendPlayerInterface(p, true, 746, 0, 1177);
	}

	public void sendPlayerInterface(Player p, boolean nocliped, int windowId, int windowComponentId, int interfaceId) {
		int[] xteas = new int[4];
		OutputStream stream = new OutputStream(25);
		stream.writePacket(player, 113);
		stream.writeShort128(p.getIndex());
		stream.writeInt(xteas[0]);
		stream.writeShort128(interfaceId);
		stream.writeIntV1(xteas[2]);
		stream.writeIntV2(windowId << 16 | windowComponentId);
		stream.write128Byte(nocliped ? 1 : 0);
		stream.writeIntV2(xteas[1]);
		stream.writeIntV1(xteas[3]);
		session.write(stream);
	}

	public void sendObjectMessage(int border, int color, WorldObject object, String message) {
		sendGameMessage(message);
		sendGlobalString(306, message);
		sendGlobalConfig(1699, color);
		sendGlobalConfig(1700, border);
		sendGlobalConfig(1695, 1);
		sendObjectInterface(object, true, 746, 0, 1177);
	}

	public void sendObjectInterface(WorldObject object, boolean nocliped, int windowId, int windowComponentId,
			int interfaceId) {
		int[] xteas = new int[4];
		OutputStream stream = new OutputStream(33);
		stream.writePacket(player, 143);
		stream.writeIntV2(xteas[1]);
		stream.writeByte(nocliped ? 1 : 0);
		stream.writeIntLE(xteas[2]);
		stream.writeIntV1(object.getId());
		stream.writeByte128((object.getType() << 2) | (object.getRotation() & 0x3));
		stream.writeInt((object.getPlane() << 28) | (object.getX() << 14) | object.getY());
		stream.writeIntV2((windowId << 16) | windowComponentId);
		stream.writeShort(interfaceId);
		stream.writeInt(xteas[3]);
		stream.writeInt(xteas[0]);
		session.write(stream);
	}

	public void sendTestProjectile(NewProjectile projectile) {
		OutputStream stream = createWorldTileStream(projectile.getFrom());
		stream.writePacket(player, 20);
		int localX = projectile.getFrom().getLocalX(player.getLastLoadedMapRegionTile(), player.getMapSize());
		int localY = projectile.getFrom().getLocalY(player.getLastLoadedMapRegionTile(), player.getMapSize());
		int offsetX = localX - ((localX >> 3) << 3);
		int offsetY = localY - ((localY >> 3) << 3);
		stream.writeByte(offsetX << 3 | offsetY);
		stream.writeByte(projectile.getTo().getX() - projectile.getFrom().getX());
		stream.writeByte(projectile.getTo().getY() - projectile.getFrom().getY());
		Entity receiver = projectile.getTo() instanceof Entity ? (Entity) projectile.getTo() : null;
		stream.writeShort(receiver == null ? 0
				: receiver instanceof Player ? -(receiver.getIndex() + 1) : receiver.getIndex() + 1);
		stream.writeShort(projectile.getGraphicsId());
		stream.writeByte(projectile.getStartHeight());
		stream.writeByte(projectile.getEndHeight());
		stream.writeShort(projectile.getDelay());
		int duration = Utils.getDistance(projectile.getFrom().getX(), projectile.getFrom().getY(),
				projectile.getTo().getX(), projectile.getTo().getY()) * 30
				/ (projectile.getSpeed() / 10 < 1 ? 1 : projectile.getSpeed() / 10) + projectile.getDelay();
		stream.writeShort(duration);
		stream.writeByte(projectile.getSlope());
		stream.writeShort((receiver == null ? 0 : receiver.getSize()) * 64 + projectile.getDistanceOffset() * 64);
		session.write(stream);
	}

	public void sendText(int interfaceId, int componentId, Object text) {
		sendIComponentText(interfaceId, componentId, text);
	}

	public void sendIComponentText(int interfaceId, int componentId, Object text) {
		OutputStream stream = new OutputStream();
		stream.writePacketVarShort(player, 135);
		stream.writeString(String.valueOf(text));
		stream.writeInt(interfaceId << 16 | componentId);
		stream.endPacketVarShort();
		session.write(stream);
	}
	
	public void sendMoveIComponent(int interfaceId, int componentId, int x, int y) {
		OutputStream stream = new OutputStream(9);
		stream.writePacket(player, 68);
		stream.writeShort(y);
		stream.writeShort(x);
		stream.writeIntLE(interfaceId << 16 | componentId);
		session.write(stream);
	}

}