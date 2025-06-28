package com.rs.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import com.rs.Settings;
import com.rs.cache.Cache;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.cores.WorldThread;
import com.rs.game.Entity;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.npc.vorago.Vorago;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;

public final class Utils {

	private static final Object ALGORITHM_LOCK = new Object();
	public static final Random RANDOM = new Random();

	private static long timeCorrection;
	private static long lastTimeUpdate;

	public static Player getOnlineorOfflinePlayer(String name) {
		Player player = World.getPlayer(name);
		if (player == null) {
			player = SerializableFilesManager.loadPlayer(name);
			if (player != null)
				player.setUsername(name);
		}
		return player;
	}

	/**
	 * Converts a Long into a String.
	 * 
	 * @param amount
	 *            The amount to convert.
	 * @return returns the Long as a formatted String.
	 */
	public static int getPercentage(int lowest, int highest) {
		return getPercentage(lowest, highest, false);
	}

	public static int getPercentage(int lowest, int highest, boolean max100) {
		if (lowest == 0 && highest == 0) {
			return 0;
		}
		int n = lowest;
		int v = highest;
		float percent = n * 100f / v;
		if (max100 && percent >= 100)
			percent = 100;
		return (int) percent;
	}

	public static String moneyToString(long amount) {
		if (amount == -1)
			return "none";
		return (amount <= 999 ? ("" + amount)
				: amount <= 999999 ? ((int) (amount / 1000) + "K (" + amount + ")")
						: amount <= 999999999 ? ((int) (amount / 1000000) + "M (" + amount + ")")
								: amount <= 999999999999L ? ((int) (amount / 1000000000) + "B (" + amount + ")")
										: ((int) (amount / 1000000000000L) + "T (" + amount + ")"));
	}

	public static String moneyToString2(long amount) {
		if (amount == -1)
			return "none";
		return (amount <= 999 ? ("" + amount)
				: amount <= 999999 ? ((int) (amount / 1000) + "K ")
						: amount <= 999999999 ? ((int) (amount / 1000000) + "M ")
								: amount <= 999999999999L ? ((int) (amount / 1000000000) + "B ")
										: ((int) (amount / 1000000000000L) + "T "));
	}

	public static String getFormattedTime(long millis, boolean useDays) {
		long second = (millis / 1000) % 60;
		long minute = (millis / (1000 * 60)) % 60;
		long hour = (millis / (1000 * 60 * 60)) % 24;
		String time = "";
		if (useDays) {
			long days = (millis / (1000 * 60 * 60 * 24));
			time = String.format("%02d:%02d:%02d:%02d", days, hour, minute, second);
		} else {
			time = String.format("%02d:%02d:%02d", hour, minute, second);
		}
		return time;
	}

	public static final byte[] DIRECTION_DELTA_X = new byte[] { -1, 0, 1, -1, 1, -1, 0, 1 };

	public static final byte[] DIRECTION_DELTA_Y = new byte[] { 1, 1, 1, 0, 0, -1, -1, -1 };

	public static final char[] VALID_CHARS = { '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
			'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2',
			'3', '4', '5', '6', '7', '8', '9' };

	public static char[] aCharArray6385 = { '\u20ac', '\0', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021',
			'\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\0', '\u017d', '\0', '\0', '\u2018', '\u2019', '\u201c',
			'\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\0', '\u017e',
			'\u0178' };

	public static final int[] ROTATION_DIR_X = { -1, 0, 1, 0 };

	public static final int[] ROTATION_DIR_Y = { 0, 1, 0, -1 };

	public static final int calculateGJString2Length(String String) {
		int length = String.length();
		int gjStringLength = 0;
		for (int index = 0; length > index; index++) {
			char c = String.charAt(index);
			if (c > '\u007f') {
				if (c <= '\u07ff')
					gjStringLength += 2;
				else
					gjStringLength += 3;
			} else
				gjStringLength++;
		}
		return gjStringLength;
	}

	public static byte[] completeQuickMessage(Player player, int fileId, byte[] data) {
		if (Settings.DEBUG)
			Logger.log("Utils", "qc: " + fileId + ", " + (data == null ? 0 : data.length));
		if (fileId == 1)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.AGILITY) };
		else if (fileId == 8)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.ATTACK) };
		else if (fileId == 13)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.CONSTRUCTION) };
		else if (fileId == 16)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.COOKING) };
		else if (fileId == 23)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.CRAFTING) };
		else if (fileId == 30)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.DEFENCE) };
		else if (fileId == 34)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.FARMING) };
		else if (fileId == 41)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.FIREMAKING) };
		else if (fileId == 47)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.FISHING) };
		else if (fileId == 55)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.FLETCHING) };
		else if (fileId == 62)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.HERBLORE) };
		else if (fileId == 70)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.HITPOINTS) };
		else if (fileId == 74)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.HUNTER) };
		else if (fileId == 135)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.MAGIC) };
		else if (fileId == 127)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.MINING) };
		else if (fileId == 120)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.PRAYER) };
		else if (fileId == 116)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.RANGE) };
		else if (fileId == 111)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.RUNECRAFTING) };
		else if (fileId == 103)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.SLAYER) };
		else if (fileId == 96)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.SMITHING) };
		else if (fileId == 92)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.STRENGTH) };
		else if (fileId == 85)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.SUMMONING) };
		else if (fileId == 79)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.THIEVING) };
		else if (fileId == 142)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.WOODCUTTING) };
		else if (fileId == 526) {
			int value = player.getLoyaltyPoints();
			data = new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
		} else if (fileId == 547) {
			// Quest points
			int value = 0;
			data = new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
		} else if (fileId == 821) {
			// Penguins found
			int value = 0;
			data = new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
		} else if (fileId == 990)
			data = new byte[] { (byte) player.getSkills().getLevelForXp(Skills.DUNGEONEERING) };
		else if (fileId == 965) {
			int value = player.getHitpoints();
			data = new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
		} else if (fileId == 1108) {
			int value = player.getDominionTower().getKilledBossesCount();
			data = new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
		} else if (fileId == 1109) {
			long value = player.getDominionTower().getTotalScore();
			data = new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
		} else if (fileId == 1110) {
			int value = (int) player.getDominionTower().getTotalScore();
			data = new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
		} else if (fileId == 1111) {
			int value = player.getDominionTower().getMaxFloorEndurance();
			data = new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
		} else if (fileId == 1134) {
			int value = player.getCrucibleHighScore();
			data = new byte[] { (byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value };
		}
		return data;
	}

	public static boolean containsInvalidCharacter(char c) {
		for (char vc : VALID_CHARS) {
			if (vc == c)
				return false;
		}
		return true;
	}

	public static boolean containsInvalidCharacter(String name) {
		for (char c : name.toCharArray()) {
			if (containsInvalidCharacter(c))
				return true;
		}
		return false;
	}

	@SuppressWarnings("resource")
	public static void copyFile(File sourceFile, File destFile) throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	public static byte[] cryptRSA(byte[] data, BigInteger exponent, BigInteger modulus) {
		return new BigInteger(data).modPow(exponent, modulus).toByteArray();
	}

	public static synchronized long currentTimeMillis() {
		long l = System.currentTimeMillis();
		if (l < lastTimeUpdate)
			timeCorrection += lastTimeUpdate - l;
		lastTimeUpdate = l;
		return l + timeCorrection;
	}

	public static byte[] decipher(int[] key, byte[] data) {
		return decipher(key, data, 0, data.length);
	}

	public static byte[] decipher(int[] key, byte[] data, int offset, int length) {
		int numBlocks = (length - offset) / 8;
		ByteBuffer buffer = ByteBuffer.wrap(data);
		buffer.position(offset);
		for (int i = 0; i < numBlocks; i++) {
			int y = buffer.getInt();
			int z = buffer.getInt();
			int sum = -957401312;
			int delta = -1640531527;
			int numRounds = 32;
			while (numRounds > 0) {
				z -= ((y >>> 5 ^ y << 4) + y ^ sum + key[sum >>> 11 & 0x56c00003]);
				sum -= delta;
				y -= ((z >>> 5 ^ z << 4) - -z ^ sum + key[sum & 0x3]);
				numRounds--;
			}
			buffer.position(buffer.position() - 8);
			buffer.putInt(y);
			buffer.putInt(z);
		}
		return buffer.array();
	}

	public static String decodeBase37(long value) {
		char[] chars = new char[12];
		int pos = 0;
		while (value != 0) {
			int remainder = (int) (value % 37);
			value /= 37;

			char c;
			if (remainder >= 1 && remainder <= 26) {
				c = (char) ('a' + remainder - 1);
			} else if (remainder >= 27 && remainder <= 36) {
				c = (char) ('0' + remainder - 27);
			} else {
				c = '_';
			}

			chars[chars.length - pos++ - 1] = c;
		}
		return new String(chars, chars.length - pos, pos);
	}

	public static final byte[] encryptUsingMD5(byte[] buffer) {
		// prevents concurrency problems with the algorithm
		synchronized (ALGORITHM_LOCK) {
			try {
				MessageDigest algorithm = MessageDigest.getInstance("MD5");
				algorithm.update(buffer);
				byte[] digest = algorithm.digest();
				algorithm.reset();
				return digest;
			} catch (Throwable e) {
				Logger.handle(e);
			}
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private static List<Class> findClasses(File directory, String packageName) {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class")) {
				try {
					classes.add(Class
							.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
				} catch (Throwable e) {
				}
			}
		}
		return classes;
	}

	public static String fixChatMessage(String message) {
		StringBuilder newText = new StringBuilder();
		boolean wasSpace = true;
		boolean exception = false;
		for (int i = 0; i < message.length(); i++) {
			if (!exception) {
				if (wasSpace) {
					newText.append(("" + message.charAt(i)).toUpperCase());
					if (!String.valueOf(message.charAt(i)).equals(" "))
						wasSpace = false;
				} else {
					newText.append(("" + message.charAt(i)).toLowerCase());
				}
			} else {
				newText.append(("" + message.charAt(i)));
			}
			if (String.valueOf(message.charAt(i)).contains(":"))
				exception = true;
			else if (String.valueOf(message.charAt(i)).contains(".") || String.valueOf(message.charAt(i)).contains("!")
					|| String.valueOf(message.charAt(i)).contains("?"))
				wasSpace = true;
		}
		return newText.toString();
	}

	public static String formatString(String name) {
		if (name == null)
			return "";
		name = name.replaceAll("_", " ");
		name = name.toLowerCase();
		StringBuilder newName = new StringBuilder();
		boolean wasSpace = true;
		for (int i = 0; i < name.length(); i++) {
			if (wasSpace) {
				newName.append(("" + name.charAt(i)).toUpperCase());
				wasSpace = false;
			} else {
				newName.append(name.charAt(i));
			}
			if (name.charAt(i) == ' ') {
				wasSpace = true;
			}
		}
		return newName.toString();
	}

	public static String formatPlayerNameForProtocol(String name) {
		if (name == null)
			return "";
		name = name.replaceAll(" ", "_");
		name = name.toLowerCase();
		return name;
	}

	public static final int getAnimationDefinitionsSize() {
		int lastArchiveId = Cache.STORE.getIndexes()[20].getLastArchiveId();
		return lastArchiveId * 128 + Cache.STORE.getIndexes()[20].getValidFilesCount(lastArchiveId);
	}

	public static String getAorAn(String name) {
		if (name.startsWith("A") || name.startsWith("a"))
			return "an ";
		return "a ";
	}

	@SuppressWarnings({ "rawtypes" })
	public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		assert classLoader != null;
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile().replaceAll("%20", " ")));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes.toArray(new Class[classes.size()]);
	}

	public static final int getConfigDefinitionsSize() {
		int lastArchiveId = Cache.STORE.getIndexes()[22].getLastArchiveId();
		return lastArchiveId * 256 + Cache.STORE.getIndexes()[22].getValidFilesCount(lastArchiveId);
	}

	public static final int[][] getCoordOffsetsNear(int size) {
		int[] xs = new int[4 + (4 * size)];
		int[] xy = new int[xs.length];
		xs[0] = -size;
		xy[0] = 1;
		xs[1] = 1;
		xy[1] = 1;
		xs[2] = -size;
		xy[2] = -size;
		xs[3] = 1;
		xy[2] = -size;
		for (int fakeSize = size; fakeSize > 0; fakeSize--) {
			xs[(4 + ((size - fakeSize) * 4))] = -fakeSize + 1;
			xy[(4 + ((size - fakeSize) * 4))] = 1;
			xs[(4 + ((size - fakeSize) * 4)) + 1] = -size;
			xy[(4 + ((size - fakeSize) * 4)) + 1] = -fakeSize + 1;
			xs[(4 + ((size - fakeSize) * 4)) + 2] = 1;
			xy[(4 + ((size - fakeSize) * 4)) + 2] = -fakeSize + 1;
			xs[(4 + ((size - fakeSize) * 4)) + 3] = -fakeSize + 1;
			xy[(4 + ((size - fakeSize) * 4)) + 3] = -size;
		}
		return new int[][] { xs, xy };
	}

	// 22314

	public static final int getDistance(int coordX1, int coordY1, int coordX2, int coordY2) {
		int deltaX = coordX2 - coordX1;
		int deltaY = coordY2 - coordY1;
		return ((int) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2)));
	}

	public static final int getDistance(WorldTile t1, WorldTile t2) {
		return getDistance(t1.getX(), t1.getY(), t2.getX(), t2.getY());
	}

	public static final int getFaceDirection(int xOffset, int yOffset) {
		return ((int) (Math.atan2(-xOffset, -yOffset) * 2607.5945876176133)) & 0x3fff;
	}

	/**
	 * Used strictly for Kalphite King's combat script.
	 * 
	 * @param from
	 *            from WorldTile.
	 * @param to
	 *            to WorldTile.
	 * @return direction.
	 */
	public static byte[] getOrthogonalDirection(WorldTile from, WorldTile to) {
		int[] dirVector = { to.getX() - from.getX() - 2, to.getY() - from.getY() - 2 };
		if (Math.abs(dirVector[0]) > Math.abs(dirVector[1])) {
			if (dirVector[0] > 0)
				return new byte[] { 1, 0 };
			else if (dirVector[0] < 0)
				return new byte[] { -1, 0 };
		} else if (Math.abs(dirVector[0]) < Math.abs(dirVector[1])) {
			if (dirVector[1] > 0)
				return new byte[] { 0, 1 };
			else if (dirVector[1] < 0)
				return new byte[] { 0, -1 };
		}
		return new byte[] { 1, 0 };
	}

	public static final byte[] getFormatedMessage(String message) {
		int i_0_ = message.length();
		byte[] is = new byte[i_0_];
		for (int i_1_ = 0; (i_1_ ^ 0xffffffff) > (i_0_ ^ 0xffffffff); i_1_++) {
			int i_2_ = message.charAt(i_1_);
			if (((i_2_ ^ 0xffffffff) >= -1 || i_2_ >= 128) && (i_2_ < 160 || i_2_ > 255)) {
				if ((i_2_ ^ 0xffffffff) != -8365) {
					if ((i_2_ ^ 0xffffffff) == -8219)
						is[i_1_] = (byte) -126;
					else if ((i_2_ ^ 0xffffffff) == -403)
						is[i_1_] = (byte) -125;
					else if (i_2_ == 8222)
						is[i_1_] = (byte) -124;
					else if (i_2_ != 8230) {
						if ((i_2_ ^ 0xffffffff) != -8225) {
							if ((i_2_ ^ 0xffffffff) != -8226) {
								if ((i_2_ ^ 0xffffffff) == -711)
									is[i_1_] = (byte) -120;
								else if (i_2_ == 8240)
									is[i_1_] = (byte) -119;
								else if ((i_2_ ^ 0xffffffff) == -353)
									is[i_1_] = (byte) -118;
								else if ((i_2_ ^ 0xffffffff) != -8250) {
									if (i_2_ == 338)
										is[i_1_] = (byte) -116;
									else if (i_2_ == 381)
										is[i_1_] = (byte) -114;
									else if ((i_2_ ^ 0xffffffff) == -8217)
										is[i_1_] = (byte) -111;
									else if (i_2_ == 8217)
										is[i_1_] = (byte) -110;
									else if (i_2_ != 8220) {
										if (i_2_ == 8221)
											is[i_1_] = (byte) -108;
										else if ((i_2_ ^ 0xffffffff) == -8227)
											is[i_1_] = (byte) -107;
										else if ((i_2_ ^ 0xffffffff) != -8212) {
											if (i_2_ == 8212)
												is[i_1_] = (byte) -105;
											else if ((i_2_ ^ 0xffffffff) != -733) {
												if (i_2_ != 8482) {
													if (i_2_ == 353)
														is[i_1_] = (byte) -102;
													else if (i_2_ != 8250) {
														if ((i_2_ ^ 0xffffffff) == -340)
															is[i_1_] = (byte) -100;
														else if (i_2_ != 382) {
															if (i_2_ == 376)
																is[i_1_] = (byte) -97;
															else
																is[i_1_] = (byte) 63;
														} else
															is[i_1_] = (byte) -98;
													} else
														is[i_1_] = (byte) -101;
												} else
													is[i_1_] = (byte) -103;
											} else
												is[i_1_] = (byte) -104;
										} else
											is[i_1_] = (byte) -106;
									} else
										is[i_1_] = (byte) -109;
								} else
									is[i_1_] = (byte) -117;
							} else
								is[i_1_] = (byte) -121;
						} else
							is[i_1_] = (byte) -122;
					} else
						is[i_1_] = (byte) -123;
				} else
					is[i_1_] = (byte) -128;
			} else
				is[i_1_] = (byte) i_2_;
		}
		return is;
	}

	public static String getFormattedNumber(int value) {
		return new DecimalFormat("#,###,##0").format(value).toString();
	}

	public static String getFormattedNumber1(int amount) {
		return new DecimalFormat("#,###,###").format(amount);
	}

	public static final int getGraphicDefinitionsSize() {
		int lastArchiveId = Cache.STORE.getIndexes()[21].getLastArchiveId();
		return lastArchiveId * 256 + Cache.STORE.getIndexes()[21].getValidFilesCount(lastArchiveId);
	}

	public static int getHashMapSize(int size) {
		size--;
		size |= size >>> -1810941663;
		size |= size >>> 2010624802;
		size |= size >>> 10996420;
		size |= size >>> 491045480;
		size |= size >>> 1388313616;
		return 1 + size;
	}

	public static final int getInterfaceDefinitionsComponentsSize(int interfaceId) {
		return Cache.STORE.getIndexes()[3].getLastFileId(interfaceId) + 1;
	}

	public static final int getInterfaceDefinitionsSize() {
		return Cache.STORE.getIndexes()[3].getLastArchiveId() + 1;
	}

	// test
	public enum EntityDirection {
		NORTH(8192), SOUTH(0), EAST(12288), WEST(4096), NORTHEAST(10240), SOUTHEAST(14366), NORTHWEST(6144), SOUTHWEST(
				2048);

		private int value;

		public int getValue() {
			return value;
		}

		private EntityDirection(int value) {
			this.value = value;
		}
	}

	// end
	public static final int getItemDefinitionsSize() {
		int lastArchiveId = Cache.STORE.getIndexes()[19].getLastArchiveId();
		return (lastArchiveId * 256 + Cache.STORE.getIndexes()[19].getValidFilesCount(lastArchiveId));
	}

	public static final boolean isValidQuickChat(int id) {
		return Cache.STORE.getIndexes()[24].fileExists(1, id);
	}

	public static final int getMoveDirection(int xOffset, int yOffset) {
		if (xOffset < 0) {
			if (yOffset < 0)
				return 5;
			else if (yOffset > 0)
				return 0;
			else
				return 3;
		} else if (xOffset > 0) {
			if (yOffset < 0)
				return 7;
			else if (yOffset > 0)
				return 2;
			else
				return 4;
		} else {
			if (yOffset < 0)
				return 6;
			else if (yOffset > 0)
				return 1;
			else
				return -1;
		}
	}

	public static final int getNameHash(String name) {
		name = name.toLowerCase();
		int hash = 0;
		for (int index = 0; index < name.length(); index++)
			hash = method1258(name.charAt(index)) + ((hash << 5) - hash);
		return hash;
	}

	public static final int getNPCDefinitionsSize() {
		int lastArchiveId = Cache.STORE.getIndexes()[18].getLastArchiveId();
		return lastArchiveId * 128 + Cache.STORE.getIndexes()[18].getValidFilesCount(lastArchiveId);
	}

	public static int getNpcMoveDirection(int dd) {
		if (dd < 0)
			return -1;
		return getNpcMoveDirection(DIRECTION_DELTA_X[dd], DIRECTION_DELTA_Y[dd]);
	}

	public static int getNpcMoveDirection(int dx, int dy) {
		if (dx == 0 && dy > 0)
			return 0;
		if (dx > 0 && dy > 0)
			return 1;
		if (dx > 0 && dy == 0)
			return 2;
		if (dx > 0 && dy < 0)
			return 3;
		if (dx == 0 && dy < 0)
			return 4;
		if (dx < 0 && dy < 0)
			return 5;
		if (dx < 0 && dy == 0)
			return 6;
		if (dx < 0 && dy > 0)
			return 7;
		return -1;
	}

	public static final int getObjectDefinitionsSize() {
		int lastArchiveId = Cache.STORE.getIndexes()[16].getLastArchiveId();
		return lastArchiveId * 256 + Cache.STORE.getIndexes()[16].getValidFilesCount(lastArchiveId);
	}

	public static double getPercent(double percent, double number) {
		return (percent / 100) * number;
	}

	public static int getPlayerRunningDirection(int dx, int dy) {
		if (dx == -2 && dy == -2)
			return 0;
		if (dx == -1 && dy == -2)
			return 1;
		if (dx == 0 && dy == -2)
			return 2;
		if (dx == 1 && dy == -2)
			return 3;
		if (dx == 2 && dy == -2)
			return 4;
		if (dx == -2 && dy == -1)
			return 5;
		if (dx == 2 && dy == -1)
			return 6;
		if (dx == -2 && dy == 0)
			return 7;
		if (dx == 2 && dy == 0)
			return 8;
		if (dx == -2 && dy == 1)
			return 9;
		if (dx == 2 && dy == 1)
			return 10;
		if (dx == -2 && dy == 2)
			return 11;
		if (dx == -1 && dy == 2)
			return 12;
		if (dx == 0 && dy == 2)
			return 13;
		if (dx == 1 && dy == 2)
			return 14;
		if (dx == 2 && dy == 2)
			return 15;
		return -1;
	}

	/**
	 * Walk dirs 0 - South-West 1 - South 2 - South-East 3 - West 4 - East 5 -
	 * North-West 6 - North 7 - North-East
	 */
	public static int getPlayerWalkingDirection(int dx, int dy) {
		if (dx == -1 && dy == -1) {
			return 0;
		}
		if (dx == 0 && dy == -1) {
			return 1;
		}
		if (dx == 1 && dy == -1) {
			return 2;
		}
		if (dx == -1 && dy == 0) {
			return 3;
		}
		if (dx == 1 && dy == 0) {
			return 4;
		}
		if (dx == -1 && dy == 1) {
			return 5;
		}
		if (dx == 0 && dy == 1) {
			return 6;
		}
		if (dx == 1 && dy == 1) {
			return 7;
		}
		return -1;
	}

	public static final int getRandom(int maxValue) {
		return (int) (Math.random() * (maxValue + 1));
	}

	public static final double getRandomDouble(double maxValue) {
		return (Math.random() * (maxValue + 1));

	}

	public static final String getUnformatedMessage(int messageDataLength, int messageDataOffset, byte[] messageData) {
		char[] cs = new char[messageDataLength];
		int i = 0;
		for (int i_6_ = 0; i_6_ < messageDataLength; i_6_++) {
			int i_7_ = 0xff & messageData[i_6_ + messageDataOffset];
			if ((i_7_ ^ 0xffffffff) != -1) {
				if ((i_7_ ^ 0xffffffff) <= -129 && (i_7_ ^ 0xffffffff) > -161) {
					int i_8_ = aCharArray6385[i_7_ - 128];
					if (i_8_ == 0)
						i_8_ = 63;
					i_7_ = i_8_;
				}
				cs[i++] = (char) i_7_;
			}
		}
		return new String(cs, 0, i);
	}

	public static boolean inCircle(WorldTile location, WorldTile center, int radius) {
		return getDistance(center, location) < radius;
	}

	public static boolean invalidAccountName(String name) {
		return name.length() < 2 || name.length() > 12 || name.startsWith("_") || name.startsWith("mod ")
				|| name.startsWith("owner ") || !name.matches("^[a-zA-Z0-9_ ]{3,12}$") || name.contains("dragonkk")
				|| name.endsWith("_") || name.contains("__");
	}

	public static boolean invalidAuthId(String auth) {
		return auth.length() != 10 || auth.contains("_") || containsInvalidCharacter(auth);
	}

	public static boolean itemExists(int id) {
		if (id >= getItemDefinitionsSize()) // setted because of custom items
			return false;
		return Cache.STORE.getIndexes()[19].fileExists(id >>> 8, 0xff & id);
	}

	public static String getCompleted(String[] cmd, int index) {
		StringBuilder sb = new StringBuilder();
		for (int i = index; i < cmd.length; i++) {
			if (i == cmd.length - 1 || cmd[i + 1].startsWith("+")) {
				return sb.append(cmd[i]).toString();
			}
			sb.append(cmd[i]).append(" ");
		}
		return "null";
	}

	public static final String longToString(long l) {
		if (l <= 0L || l >= 0x5b5b57f8a98a5dd1L)
			return null;
		if (l % 37L == 0L)
			return null;
		int i = 0;
		char ac[] = new char[12];
		while (l != 0L) {
			long l1 = l;
			l /= 37L;
			ac[11 - i++] = VALID_CHARS[(int) (l1 - l * 37L)];
		}
		return new String(ac, 12 - i, i);
	}

	public static final byte method1258(char c) {
		byte charByte;
		if (c > 0 && c < '\200' || c >= '\240' && c <= '\377') {
			charByte = (byte) c;
		} else if (c != '\u20AC') {
			if (c != '\u201A') {
				if (c != '\u0192') {
					if (c == '\u201E') {
						charByte = -124;
					} else if (c != '\u2026') {
						if (c != '\u2020') {
							if (c == '\u2021') {
								charByte = -121;
							} else if (c == '\u02C6') {
								charByte = -120;
							} else if (c == '\u2030') {
								charByte = -119;
							} else if (c == '\u0160') {
								charByte = -118;
							} else if (c == '\u2039') {
								charByte = -117;
							} else if (c == '\u0152') {
								charByte = -116;
							} else if (c != '\u017D') {
								if (c == '\u2018') {
									charByte = -111;
								} else if (c != '\u2019') {
									if (c != '\u201C') {
										if (c == '\u201D') {
											charByte = -108;
										} else if (c != '\u2022') {
											if (c == '\u2013') {
												charByte = -106;
											} else if (c == '\u2014') {
												charByte = -105;
											} else if (c == '\u02DC') {
												charByte = -104;
											} else if (c == '\u2122') {
												charByte = -103;
											} else if (c != '\u0161') {
												if (c == '\u203A') {
													charByte = -101;
												} else if (c != '\u0153') {
													if (c == '\u017E') {
														charByte = -98;
													} else if (c != '\u0178') {
														charByte = 63;
													} else {
														charByte = -97;
													}
												} else {
													charByte = -100;
												}
											} else {
												charByte = -102;
											}
										} else {
											charByte = -107;
										}
									} else {
										charByte = -109;
									}
								} else {
									charByte = -110;
								}
							} else {
								charByte = -114;
							}
						} else {
							charByte = -122;
						}
					} else {
						charByte = -123;
					}
				} else {
					charByte = -125;
				}
			} else {
				charByte = -126;
			}
		} else {
			charByte = -128;
		}
		return charByte;
	}

	public static char method2782(byte value) {
		int byteChar = 0xff & value;
		if (byteChar == 0)
			throw new IllegalArgumentException(
					"Non cp1252 character 0x" + Integer.toString(byteChar, 16) + " provided");
		if ((byteChar ^ 0xffffffff) <= -129 && byteChar < 160) {
			int i_4_ = aCharArray6385[-128 + byteChar];
			if ((i_4_ ^ 0xffffffff) == -1)
				i_4_ = 63;
			byteChar = i_4_;
		}
		return (char) byteChar;
	}

	public static final int next(int max, int min) {
		return min + (int) (Math.random() * ((max - min) + 1));
	}

	public static final int packGJString2(int position, byte[] buffer, String String) {
		int length = String.length();
		int offset = position;
		for (int index = 0; length > index; index++) {
			int character = String.charAt(index);
			if (character > 127) {
				if (character > 2047) {
					buffer[offset++] = (byte) ((character | 919275) >> 12);
					buffer[offset++] = (byte) (128 | ((character >> 6) & 63));
					buffer[offset++] = (byte) (128 | (character & 63));
				} else {
					buffer[offset++] = (byte) ((character | 12309) >> 6);
					buffer[offset++] = (byte) (128 | (character & 63));
				}
			} else
				buffer[offset++] = (byte) character;
		}
		return offset - position;
	}

	public static final double random(double min, double max) {
		final double n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random((int) n));
	}

	public static final int random(int maxValue) {
		if (maxValue <= 0)
			return 0;
		return RANDOM.nextInt(maxValue);
	}

	public static final long random(long maxValue) {
		if (maxValue <= 0)
			return 0;
		long randomValue = (long) (RANDOM.nextDouble() * (0 - maxValue));
		return randomValue;
	}

	public static final int random(int min, int max) {
		final int n = Math.abs(max - min);
		return Math.min(min, max) + (n == 0 ? 0 : random(n));
	}

	public static final long stringToLong(String s) {
		long l = 0L;
		for (int i = 0; i < s.length() && i < 12; i++) {
			char c = s.charAt(i);
			l *= 37L;
			if (c >= 'A' && c <= 'Z')
				l += (1 + c) - 65;
			else if (c >= 'a' && c <= 'z')
				l += (1 + c) - 97;
			else if (c >= '0' && c <= '9')
				l += (27 + c) - 48;
		}
		while (l % 37L == 0L && l != 0L) {
			l /= 37L;
		}
		return l;
	}

	public static boolean colides(int x1, int y1, int size1, int x2, int y2, int size2) {
		int distanceX = x1 - x2;
		int distanceY = y1 - y2;
		return distanceX < size2 && distanceX > -size1 && distanceY < size2 && distanceY > -size1;
	}

	public static boolean isOnRange(int x1, int y1, int size1, int x2, int y2, int size2, int maxDistance) {
		int distanceX = x1 - x2;
		int distanceY = y1 - y2;
		if (distanceX > size2 + maxDistance || distanceX < -size1 - maxDistance || distanceY > size2 + maxDistance
				|| distanceY < -size1 - maxDistance)
			return false;
		return true;
	}

	/*
	 * dont use this one
	 */
	public static boolean isOnRange(int x1, int y1, int x2, int y2, int sizeX, int sizeY) {
		int distanceX = x1 - x2;
		int distanceY = y1 - y2;
		if (distanceX > sizeX || distanceX < -1 || distanceY > sizeY || distanceY < -1)
			return false;
		return true;
	}

	public static int fixChatEffects(String username, int colorEffect, int moveEffect) {
		if (colorEffect > 11 || moveEffect > 5) {
			Logger.log("Fake Chat Effects recieved by client (" + username + ").");
			return 0;
		}
		return (colorEffect << 8) | (moveEffect & 0xff);
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file " + file.getName());
		}

		is.close();
		return bytes;
	}

	public static final double randomDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}

	public static boolean badName(String name) {
		String n = name.toLowerCase();
		return n.contains("****") || n.contains("mod ") || n.contains("admin") || n.contains("isis")
				|| n.contains("m0d") || n.contains("moderator") || n.contains("m0derator")
				|| n.contains("fuck") || n.contains("penis") || n.contains("sex")
				;
	}

	public static int get32BitValue(boolean[] array, boolean trueCondition) {
		int value = 0;
		for (int index = 1; index < array.length + 1; index++) {
			if (array[index - 1] == trueCondition) {
				value += 1 << index;
			}
		}
		return value;
	}

	public static String getFormattedNumber(double amount, char seperator) {
		String str = new DecimalFormat("#,###,###").format(amount);
		char[] rebuff = new char[str.length()];
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= '0' && c <= '9')
				rebuff[i] = c;
			else
				rebuff[i] = seperator;
		}
		return new String(rebuff);
	}

	public static final int getAngle(int xOffset, int yOffset) {
		return ((int) (Math.atan2(-xOffset, -yOffset) * 2607.5945876176133)) & 0x3fff;
	}

	public static int getMapArchiveId(int regionX, int regionY) {
		return regionX | regionY << 7;
	}

	/**
	 * String used to handle displaying and formatting of total time played.
	 * 
	 * @param totalPlayTime
	 *            The total time played.
	 * @return Formatted total time played in String.
	 */
	public static String getTimePlayed(long totalPlayTime) {
		final long tPt = Utils.currentTimeMillis() - totalPlayTime;
		final int sec = (int) (tPt / 1000), h = sec / 3600, m = sec / 60 % 60, s = sec % 60;
		return (h < 1 ? "" : (h < 10 ? "0" + h : h) + "h:") + ((m < 1) && h < 1 ? "" : (m < 10 ? "0" + m : m) + "m:")
				+ ((s < 1) && m < 1 ? "" : (s < 10 ? "0" + s + "s" : s + "s"));
	}

	public static int getMinutesPlayed(Player player) {
		long totalPlayTime = Utils.currentTimeMillis() - (player.getTotalPlayTime() + player.getRecordedPlayTime());
		int sec = (int) (totalPlayTime / 1000), h = sec / 3600, m = sec / 60 % 60;
		for (int i = 0; i < h; i++)
			m += 60;
		return m;
	}

	public static String getHoursPlayed(long totalPlayTime) {
		final long tPt = Utils.currentTimeMillis() - totalPlayTime;
		final int sec = (int) (tPt / 1000), h = sec / 3600;
		return (h < 10 ? "0" + h : h) + "h";
	}

	/**
	 * Converts milliseconds to minutes.
	 * 
	 * @param millisecs
	 *            The ms to convert.
	 * @return minutes.
	 */
	public static int millisecsToMinutes(long millisecs) {
		int sec = (int) (millisecs / 1000), h = sec / 3600, m = sec / 60 % 60;
		for (int i = 0; i < h; i++)
			m += 60;
		return m;
	}

	public static WorldTile getFreeTile(WorldTile center, int distance) {
		WorldTile tile = center;
		for (int i = 0; i < 10; i++) {
			tile = new WorldTile(center, distance);
			if (World.isTileFree(tile.getPlane(), tile.getX(), tile.getY(), 1))
				return tile;
		}
		return center;
	}

	/*
	 * world cycles, each is 600ms :). its 100% safe to use :p example of usage well
	 * doesnt save with restarts it should work fine for disabled. its bad dont use
	 * for things that save ofc good for stuff that doesnt save such as temporary
	 * args and delays
	 */
	public static long currentWorldCycle() {
		return WorldThread.WORLD_CYCLE;
	}

	public static boolean isOnRange(Entity entity, Entity target, int rangeRatio) {
		return entity.getPlane() == target.getPlane() && isOnRange(entity.getX(), entity.getY(), entity.getSize(),
				target.getX(), target.getY(), target.getSize(), rangeRatio);
	}

	/**
	 * @return the number with commas
	 */
	public static String formatNumber(int num) {
		return NumberFormat.getInstance().format(num);
	}

	public static boolean colides(Entity entity, Entity target) {
		return entity.getPlane() == target.getPlane() && colides(entity.getX(), entity.getY(), entity.getSize(),
				target.getX(), target.getY(), target.getSize());
	}

	public static boolean colides(WorldTile entity, WorldTile target, int s1, int s2) {
		return entity.getPlane() == target.getPlane()
				&& colides(entity.getX(), entity.getY(), s1, target.getX(), target.getY(), s2);
	}

	public static int getProjectileTime(WorldTile startTile, WorldTile endTile, int startHeight, int endHeight,
			int speed, int delay, int curve, int startDistanceOffset, int creatorSize) {
		int distance = Utils.getDistance(startTile, endTile) + 1;
		if (speed == 0) // cant be 0, happens cuz method wrong and so /10 needed
						// so may round to 0
			speed = 1;
		return (delay * 10) + (distance * ((30 / speed) * 10)); /** Math.cos(Math.toRadians(curve)) */
	}

	public static int getProjectileTimeNew(WorldTile from, int fromSizeX, int fromSizeY, WorldTile to, int toSizeX,
			int toSizeY, double speed) {
		int fromX = from.getX() * 2 + fromSizeX;
		int fromY = from.getY() * 2 + fromSizeY;

		int toX = to.getX() * 2 + toSizeX;
		int toY = to.getY() * 2 + toSizeY;

		fromX /= 2;
		fromY /= 2;
		toX /= 2;
		toY /= 2;

		int deltaX = fromX - toX;
		int deltaY = fromY - toY;
		int sqrt = (int) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
		return (int) (sqrt * (10 / speed));
	}

	public static int getProjectileTimeSoulsplit(WorldTile from, int fromSizeX, int fromSizeY, WorldTile to,
			int toSizeX, int toSizeY) {
		int fromX = from.getX() * 2 + fromSizeX;
		int fromY = from.getY() * 2 + fromSizeY;

		int toX = to.getX() * 2 + toSizeX;
		int toY = to.getY() * 2 + toSizeY;

		fromX /= 2;
		fromY /= 2;
		toX /= 2;
		toY /= 2;

		int deltaX = fromX - toX;
		int deltaY = fromY - toY;
		int sqrt = (int) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));
		sqrt *= 15;
		sqrt -= sqrt % 30;
		return Math.max(30, sqrt);
	}

	public static int projectileTimeToCycles(int time) {
		return (time + 29) / 30;
	}

	public static String currentTime(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	public static String getDaysRemaining(long time) {
		long timeLeft = (time - System.currentTimeMillis());
		long days = TimeUnit.MILLISECONDS.toDays(timeLeft);
		long hours = TimeUnit.MILLISECONDS.toHours(timeLeft);
		long min = TimeUnit.MILLISECONDS.toMinutes(timeLeft)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeLeft));
		long sec = TimeUnit.MILLISECONDS.toSeconds(timeLeft)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft));
		return days > 0 ? days == 1 ? days + "Day" : days + "Days"
				: hours > 0 ? hours == 1 ? hours + "hr." : hours + "hrs" : min > 0 ? min + "min." : +sec + "sec.";
	}

	public static String getTimeLeft(long time) {
		long timeLeft = (time - System.currentTimeMillis());
		long min = TimeUnit.MILLISECONDS.toMinutes(timeLeft)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(timeLeft));
		long sec = TimeUnit.MILLISECONDS.toSeconds(timeLeft)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft));
		return min + "min. " + sec + "sec.";
	}

	public static String getTimeRemaining(long time) {
		long timeLeft = (time - System.currentTimeMillis());
		long sec = TimeUnit.MILLISECONDS.toSeconds(timeLeft)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeLeft));
		return "" + sec + "sec.";
	}

	public static String getTimeToString(long time) {
		final long tPt = time;
		final int sec = (int) (tPt / 1000), h = sec / 3600, m = sec / 60 % 60, s = sec % 60;
		return (h < 1 ? "" : (h < 10 ? "0" + h : h) + "h:") + ((m < 1) && h < 1 ? "" : (m < 10 ? "0" + m : m) + "m:")
				+ ((s < 1) && m < 1 ? "" : (s < 10 ? "0" + s + "s" : s + "s"));
	}

	public static String formatTime(long time) {
		long seconds = time / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;
		seconds = seconds % 60;
		minutes = minutes % 60;
		hours = hours % 24;
		StringBuilder string = new StringBuilder();
		string.append(hours > 9 ? hours : ("0" + hours));
		string.append(":" + (minutes > 9 ? minutes : ("0" + minutes)));
		string.append(":" + (seconds > 9 ? seconds : ("0" + seconds)));
		return string.toString();
	}

	public static byte[] getDirection(int angle) {
		int v = angle >> 11;
		return ANGLE_DIRECTION_DELTA[v];
	}

	private static final byte[][] ANGLE_DIRECTION_DELTA = { { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 },
			{ 1, 1 }, { 1, 0 }, { 1, -1 } };

	public static double getProjectileSpeed(WorldTile startTile, WorldTile endTile, int startHeight, int endHeight,
			long startTime, long arriveTime) {
		int fromSizeX, fromSizeY;
		if (startTile instanceof Entity)
			fromSizeX = fromSizeY = ((Entity) startTile).getSize();
		else if (startTile instanceof WorldObject) {
			ObjectDefinitions defs = ((WorldObject) startTile).getDefinitions();
			fromSizeX = defs.getSizeX();
			fromSizeY = defs.getSizeY();
		} else
			fromSizeX = fromSizeY = 1;
		int toSizeX, toSizeY;
		if (endTile instanceof Entity)
			toSizeX = toSizeY = ((Entity) endTile).getSize();
		else if (endTile instanceof WorldObject) {
			ObjectDefinitions defs = ((WorldObject) endTile).getDefinitions();
			toSizeX = defs.getSizeX();
			toSizeY = defs.getSizeY();
		} else
			toSizeX = toSizeY = 1;
		int fromX = startTile.getX() * 2 + fromSizeX;
		int fromY = startTile.getY() * 2 + fromSizeY;

		int toX = endTile.getX() * 2 + toSizeX;
		int toY = endTile.getY() * 2 + toSizeY;

		fromX /= 2;
		fromY /= 2;
		toX /= 2;
		toY /= 2;

		int deltaX = fromX - toX;
		int deltaY = fromY - toY;
		double distance = Math.sqrt((deltaX * deltaX) + (deltaY * deltaY));

		double speed = ((distance * 1000) / (arriveTime - startTime));

		return speed;
	}

	public static boolean isOnRange(WorldTile entity, WorldTile target, int rangeRatio, int s1, int s2) {
		return entity.getPlane() == target.getPlane()
				&& isOnRange(entity.getX(), entity.getY(), s1, target.getX(), target.getY(), s2, rangeRatio);
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public static String formatPlayerNameForDisplay(String name) {
		if (name == null)
			return "";
		name = name.replaceAll("_", " ");
		name = name.toLowerCase();
		StringBuilder newName = new StringBuilder();
		boolean wasSpace = true;
		for (int i = 0; i < name.length(); i++) {
			if (wasSpace) {
				newName.append(("" + name.charAt(i)).toUpperCase());
				wasSpace = false;
			} else {
				newName.append(name.charAt(i));
			}
			if (name.charAt(i) == ' ') {
				wasSpace = true;
			}
		}
		return newName.toString();
	}

	public static String formatNumber(long num) {
		return NumberFormat.getNumberInstance(Locale.UK).format(num);
	}

	public static final byte[][] DIRS = new byte[][] { { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 },
			{ 1, 0 }, { 1, -1 } };

	/**
	 * Constructs an arraylist of tiles between two points. x1/y1 -> Starting
	 * location. x2/y2 -> End location. Draws as straight of a line as possible
	 * between the given locations
	 */
	public static final List<WorldTile> calculateLine(int x1, int y1, int x2, int y2, int plane) {
		final List<WorldTile> tiles = new ArrayList<WorldTile>();
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		int sx = (x1 < x2) ? 1 : -1;
		int sy = (y1 < y2) ? 1 : -1;
		int err = dx - dy;
		while (true) {
			tiles.add(new WorldTile(x1, y1, plane));
			if (x1 == x2 && y1 == y2)
				break;
			int e2 = 2 * err;
			if (e2 > -dy) {
				err = err - dy;
				x1 = x1 + sx;
			}
			if (e2 < dx) {
				err = err + dx;
				y1 = y1 + sy;
			}
		}
		return tiles;
	}

	public static final int getRandoms(int maxValue) {
		return (int) (Math.random() * (maxValue + 1));
	}

	public static int getAreaDefinitionsSize() {
		return Cache.STORE.getIndexes()[2].getTable().getArchives()[36].getValidFileIds().length;
	}

	public static String formatTimeLeft(long ticks) {
        ticks = ticks - currentTimeMillis();
        long seconds = ticks / 1000;//180000 divided by 1000 = 180
        long min = seconds / 60;//180 divded by 60 = 3
        seconds = seconds % 60;
        String sec = seconds > 9 ? seconds+"" : min > 0 && seconds < 10 ? "0"+seconds : seconds+"";
        return min > 0 ? min+":"+sec : sec;
    }

	

}