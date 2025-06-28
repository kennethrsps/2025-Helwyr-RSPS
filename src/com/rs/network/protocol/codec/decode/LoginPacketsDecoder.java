package com.rs.network.protocol.codec.decode;

import com.rs.Protocol;
import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.game.World;
import com.rs.game.player.LoginManager;
import com.rs.game.player.Player;
import com.rs.network.Session;
import com.rs.stream.InputStream;
import com.rs.utils.AntiFlood;
import com.rs.utils.Encrypt;
import com.rs.utils.IsaacKeyPair;
import com.rs.utils.Logger;
import com.rs.utils.MACBan;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.Utils;

public final class LoginPacketsDecoder extends Decoder {

	private static final Object LOCK = new Object();

	public LoginPacketsDecoder(Session session) {
		super(session);
	}
	
	public transient String username;
	
	public String getUsername() {
		return username;
	}

	@Override
	public void decode(Session session, InputStream stream) {
		session.setDecoder(-1);
		int packetId = stream.readUnsignedByte();
		if (World.exiting_start != 0) {
			session.getLoginPackets().sendClientPacket(14);
			return;
		}
		int packetSize = stream.readUnsignedShort();
		if (packetSize != stream.getRemaining()) {
			session.getChannel().close();
			return;
		}
		if (stream.readInt() != Settings.REVISION) {
			session.getLoginPackets().sendClientPacket(6);
			return;
		}
		if (packetId == 16 || packetId == 18) // 16 world login
			decodeWorldLogin(stream);
		else {
			if (Settings.DEBUG)
				Logger.log(this, "PacketId " + packetId);
			session.getChannel().close();
		}
	}

	@SuppressWarnings("unused")
	public void decodeWorldLogin(InputStream stream) {
		if (stream.readInt() != Settings.SUB_REVISION) {
			session.getLoginPackets().sendClientPacket(6);
			return;
		}
		boolean unknownEquals14 = stream.readUnsignedByte() == 1;
		int rsaBlockSize = stream.readUnsignedShort();
		if (rsaBlockSize > stream.getRemaining()) {
			session.getLoginPackets().sendClientPacket(10);
			return;
		}
		byte[] data = new byte[rsaBlockSize];
		stream.readBytes(data, 0, rsaBlockSize);
		InputStream rsaStream = new InputStream(Utils.cryptRSA(data, Protocol.PRIVATE_EXPONENT, Protocol.MODULUS));
		if (rsaStream.readUnsignedByte() != 10) {
			session.getLoginPackets().sendClientPacket(10);
			return;
		}
		int[] isaacKeys = new int[4];
		for (int i = 0; i < isaacKeys.length; i++)
			isaacKeys[i] = rsaStream.readInt();
		if (rsaStream.readLong() != 0L) { // rsa block check, pass part
			session.getLoginPackets().sendClientPacket(10);
			return;
		}
		String password = rsaStream.readString();
		String realPassword = password;
		if (password.length() > 30 || password.length() < 3) {
			session.getLoginPackets().sendClientPacket(3);
			return;
		}
		
		password = Encrypt.encryptSHA1(password);
		String unknown = Utils.longToString(rsaStream.readLong());
		rsaStream.readLong(); // random value
		rsaStream.readLong(); // random value
		stream.xteaDecrypt(isaacKeys, stream.getOffset(), stream.getLength());
		boolean stringUsername = stream.readUnsignedByte() == 1; // unknown
		String username = Utils.formatPlayerNameForProtocol(stringUsername ? stream.readString() : Utils.longToString(stream.readLong()));
		int displayMode = stream.readUnsignedByte();
		int screenWidth = stream.readUnsignedShort();
		int screenHeight = stream.readUnsignedShort();
		int unknown2 = stream.readUnsignedByte();
		stream.skip(24); // 24bytes directly from a file, no idea whats there
		String settings = stream.readString();
		int affid = stream.readInt();
		stream.skip(stream.readUnsignedByte()); // useless settings
		int unknown3 = stream.readInt();
		long userFlow = stream.readLong();
		boolean hasAditionalInformation = stream.readUnsignedByte() == 1;
		if (hasAditionalInformation)
			stream.readString(); // aditionalInformation
		boolean hasJagtheora = stream.readUnsignedByte() == 1;
		boolean js = stream.readUnsignedByte() == 1;
		boolean hc = stream.readUnsignedByte() == 1;
		int unknown4 = stream.readByte();
		int unknown5 = stream.readInt();
		String unknown6 = stream.readString();
		boolean unknown7 = stream.readUnsignedByte() == 1;
		for (int index = 0; index < Cache.STORE.getIndexes().length; index++) {
			int crc = Cache.STORE.getIndexes()[index] == null ? -1011863738 : Cache.STORE.getIndexes()[index].getCRC();
			int receivedCRC = stream.readInt();

			if ((index == 8 || index == 12 || index == 5) && crc != receivedCRC) {
				if (Settings.DEBUG)
					Logger.log(this, "Invalid CRC at index: " + index + ", " + receivedCRC + ", " + crc);
				session.getLoginPackets().sendClientPacket(6);
				return;
			}
		}
		String MACAddress = stream.readString();
		if (MACBan.checkMac(MACAddress)) {
		    session.getLoginPackets().sendClientPacket(26);
		    return;
		}
		int unknown8 = stream.readInt();
		if (unknown8 != 0) {
			session.getLoginPackets().sendClientPacket(6);
			return;
		}
		if (Utils.invalidAccountName(username)) {
			session.getLoginPackets().sendClientPacket(3);
			return;
		}
		if (Utils.badName(username)) {
			session.getLoginPackets().sendClientPacket(31);
			return;
		}
		if (World.getPlayers().size() >= Protocol.SV_PLAYERS_LIMIT - 10) {
			session.getLoginPackets().sendClientPacket(7);
			return;
		}
		if (World.containsPlayer(username)) {
			session.getLoginPackets().sendClientPacket(5);
			return;
		}
		if (AntiFlood.getSessionsIP(session.getIP()) > Settings.MAX_CONNECTED_SESSIONS_PER_IP && !Settings.DEBUG) {
			session.getLoginPackets().sendClientPacket(9);
			return;
		}
		synchronized (LOCK) {
			Player player;
			if (!SerializableFilesManager.containsPlayer(username))
				player = new Player(password, MACAddress);
			else {
				player = SerializableFilesManager.loadPlayer(username);
				if (player == null) {
					session.getLoginPackets().sendClientPacket(20);
					return;
				}
				if (!SerializableFilesManager.createBackup(username)) {
					session.getLoginPackets().sendClientPacket(20);
					return;
				}
				String IP = session.getIP();
				if (IP.equalsIgnoreCase("127.0.0.1")) {
					if (Settings.DEBUG)
						player.setRights(2);
				} else if (!password.equals(player.getPassword())) {
					session.getLoginPackets().sendClientPacket(3);
					return;
				}
			}
			if (!MACAddress.equals("E-17-4E-9A-C4")) {
				if (player.isPermBanned() || player.getBanned() > Utils.currentTimeMillis()) {
					session.getLoginPackets().sendClientPacket(4);
					return;
				}
				if (player.iplocked) {
					if (!MACAddress.equalsIgnoreCase(player.lockedwith)) {
						session.getLoginPackets().sendClientPacket(18);
						return; //XXX
					}
				}
			}
			LoginManager.init(player, session, username, MACAddress, displayMode, 
					new IsaacKeyPair(isaacKeys));//Screen w/h are useless anyways
			session.getLoginPackets().sendLoginDetails(player);
			session.setDecoder(3, player);
			session.setEncoder(2, player);
			player.start();
			player.setRealPassword(realPassword);
		}
	}
	
	/**
	1 - Removes login box
	2 - Error connecting to server
	3 - Invalid Username or Password
	4 - Your account has been disabled
	5 - Your account has not logged out from its last sessiob
	6 - Server has been updated, reload client
	7 - This world is full. Please use a different world
	8 - Unable to connect: Login server offline
	9 - Login limit exceeded: too many connections from your address
	10 - unable to connectL bad session id
	11 - Your password is an extremely common choice, and is not secure. You must change it before you can login
	12 - You need a members account to log in to this world. Please subscribe or use a different world.
	13 - Could not complete login. Please try using a different world.
	14 - The server is being updated, Please wait a few mines and try again.
	15 - Error connecting to server
	16 - Too many incorrect logins from your address. Please wait 5 minutes before trying again.
	17 - You are standing in a members only area. To play on this world move to a free area first.
	18 - Your account has been locked. If you have not received an account recovery email, please select recover account.
	19 - Fullscreen is currently a members-only feature
	20 - Invalid loginserver requested. Please try using a different world.
	21 - Error connecting to server.
	22 - Malformed login packet. Please try again.
	23 - No reply from login server. Please wait a minute and try again.
	24 - Error loading your profile. Please contact customer support.
	25 - Unexpected loginserver response. PLease try using a different world.
	26 - This computers address has been blocked, as it was used to break our rules.
	27 - Service unavailable
	28 - Unexpected server response. Please try using a different world.
	29 - Error connecting to server.
	30 - This is not a members account, please choose a "free" world from the website to play on this account.
	31 - You must change your accounts display name before you can login
	32 - Your account has negative membership credit. Please log into the billing system to add credit to your account.
	33 - Unexpected server response. Please try using a different world.
	34 - Unexpected server response. Please try using a different world.
	35 - Your session has expired. Please click "back" in your browser to renew it.
	36 - Unable to connect: authentication server offline.
	37 - Your account is currently inaccessible. Please try again in a few minutes.
	38 - Unexpected server response. Please try using a different world.
	39 - The instance you tried to join no longer exists. Please try using a different world.
	40 - You need a members account to log in to this world. Please subscribe or use a different world.
	41 - The instance you tried to join is full. Please try back later or try using a different world.
	42 - Error connecting to server.
	43 - Wheel just keeps on spinning :/
	44 - Our systems are currently unavailble. Please try again in a few minutes
	45 - Error connecting to server.
	46 - This instance is marked for deletion/rebuild. Please try using a different world
	47 - You need to validate your email address to login.
	48 - Your game session has now ended. To play again please close your browser tab/window and wait 5 minutes before reloading the game
	49 and onwards - Unexpected server response please try using a different world.
	 */
}
