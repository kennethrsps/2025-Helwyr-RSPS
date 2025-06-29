package com.rs.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.util.ArrayList;
import java.util.List;
import com.rs.utils.Utils.EntityDirection;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.npc.NPC;

public final class NPCSpawns {

	private static final Object lock = new Object();
	private static BufferedWriter writer;
	private static BufferedReader in;
	private static BufferedWriter writer2;
	private static BufferedReader in2;

	public static boolean addSpawn(String username, int id, WorldTile tile) throws Throwable {
		synchronized (lock) {
			File file = new File("data/npcs/spawns.txt");
			writer = new BufferedWriter(new FileWriter(file, true));
			writer.write("// " + NPCDefinitions.getNPCDefinitions(id).name + ", "
					+ NPCDefinitions.getNPCDefinitions(id).combatLevel + ", added by: " + username);
			writer.newLine();
			writer.flush();
			writer.write(id + " - " + tile.getX() + " " + tile.getY() + " " + tile.getPlane());
			writer.newLine();
			writer.flush();
			World.spawnNPC(id, tile, -1, true);
			writer.close();
			return true;
		}

	}

	public static boolean removeSpawn(NPC npc) throws Throwable {
		synchronized (lock) {
			List<String> page = new ArrayList<String>();
			File file = new File("data/npcs/spawns.txt");
			in = new BufferedReader(new FileReader(file));
			String line;
			boolean removed = false;
			int id = npc.getId();
			WorldTile tile = npc.getRespawnTile();
			while ((line = in.readLine()) != null) {
				if (line.equals(id + " - " + tile.getX() + " " + tile.getY() + " " + tile.getPlane())) {
					page.remove(page.get(page.size() - 1)); // description
					removed = true;
					continue;
				}
				page.add(line);
			}
			if (!removed)
				return false;
			file.delete();
			writer2 = new BufferedWriter(new FileWriter(file));
			for (String l : page) {
				writer2.write(l);
				writer2.newLine();
				writer2.flush();
			}
			npc.finish();
			writer2.close();
			return true;
		}
	}

	public static final void init() {
		if (!new File("data/npcs/packedSpawns").exists())
			packNPCSpawns();
	}

	private static final void packNPCSpawns() {
		Logger.log("NPCSpawns", "Packing npc spawns...");
		if (!new File("data/npcs/packedSpawns").mkdir())
			throw new RuntimeException("Couldn't create packedSpawns directory.");
		try {
			in2 = new BufferedReader(new FileReader("data/npcs/unpackedSpawnsList.txt"));
			while (true) {
				String line = in2.readLine();
				if (line == null)
					break;
				if (line.startsWith("//"))
					continue;
				if (line.startsWith("RSBOT"))
					continue;
				String[] splitedLine = line.split(" - ", 2);
				if (splitedLine.length != 2)
					throw new RuntimeException("Invalid NPC Spawn line: " + line);
				int npcId = Integer.parseInt(splitedLine[0]);
				String[] splitedLine2 = splitedLine[1].split(" ", 5);
				if (splitedLine2.length != 3 && splitedLine2.length != 5)
					throw new RuntimeException("Invalid NPC Spawn line: " + line);
				WorldTile tile = new WorldTile(Integer.parseInt(splitedLine2[0]), Integer.parseInt(splitedLine2[1]),
						Integer.parseInt(splitedLine2[2]));
				int mapAreaNameHash = -1;
				boolean canBeAttackFromOutOfArea = true;
				if (splitedLine2.length == 5) {
					mapAreaNameHash = Utils.getNameHash(splitedLine2[3]);
					canBeAttackFromOutOfArea = Boolean.parseBoolean(splitedLine2[4]);
				}
				addNPCSpawn(npcId, tile.getRegionId(), tile, mapAreaNameHash, canBeAttackFromOutOfArea);
			}
			in2.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	public static void addCustomSpawns() {
		World.spawnNPC(19553, new WorldTile(3810, 4712, 0), -1, true);// darklord
		World.spawnNPC(3847, new WorldTile(1623, 4570, 0), -1, true);// sea troll

		/* NEW NPC SPAWNED BY RYAN THE GREAT */
		World.spawnNPC(522, new WorldTile(986, 4115, 0), -1, true, EntityDirection.NORTH); // THIEVING SHOP
		World.spawnNPC(553, new WorldTile(995, 4113, 0), -1, true, EntityDirection.NORTH); // BANK
		World.spawnNPC(1419, new WorldTile(988, 4113, 0), -1, true, EntityDirection.NORTH);// GRAND EXCHANGE
		World.spawnNPC(537, new WorldTile(997, 4116, 0), -1, true, EntityDirection.NORTHWEST); // SCAVVO
		World.spawnNPC(519, new WorldTile(998, 4118, 0), -1, true, EntityDirection.NORTHWEST); // BOB
		World.spawnNPC(546, new WorldTile(1000, 4118, 0), -1, true, EntityDirection.NORTHWEST); // ZAFF
		World.spawnNPC(549, new WorldTile(1002, 4119, 0), -1, true, EntityDirection.NORTHWEST); // HORVIK
		World.spawnNPC(550, new WorldTile(1002, 4121, 0), -1, true, EntityDirection.WEST); // LOWE
		World.spawnNPC(6539, new WorldTile(1003, 4127, 0), -1, true, EntityDirection.WEST); // NASTROTH
		/* OTHERSIDE OF GOLD CITY */
		World.spawnNPC(14381, new WorldTile(980, 4119, 0), -1, true, EntityDirection.EAST);
		World.spawnNPC(557, new WorldTile(979, 4121, 0), -1, true, EntityDirection.EAST);
		// World.spawnNPC(552, new WorldTile(979, 4123, 0), -1, true,
		// EntityDirection.EAST);
		World.spawnNPC(13727, new WorldTile(979, 4126, 0), -1, true, EntityDirection.EAST);
		World.spawnNPC(7402, new WorldTile(980, 4117, 0), -1, true, EntityDirection.EAST);
		World.spawnNPC(4247, new WorldTile(979, 4134, 0), -1, true, EntityDirection.EAST);
		/* SOUTH OF CITY OF GOLD */
		World.spawnNPC(2998, new WorldTile(1003, 4132, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(659, new WorldTile(1002, 4134, 0), -1, true, EntityDirection.SOUTHWEST);
		World.spawnNPC(19519, new WorldTile(999, 4136, 0), -1, true, EntityDirection.SOUTHWEST);
		World.spawnNPC(14874, new WorldTile(999, 4139, 0), -1, true, EntityDirection.SOUTHWEST);
		World.spawnNPC(3404, new WorldTile(998, 4141, 0), -1, true, EntityDirection.SOUTHWEST);
		/* LAST PART CITY OF GOLD NPC */
		World.spawnNPC(15085, new WorldTile(1001, 4135, 0), -1, true, EntityDirection.SOUTHWEST);
		World.spawnNPC(2538, new WorldTile(997, 4142, 0), -1, true, EntityDirection.SOUTHWEST);
		World.spawnNPC(2253, new WorldTile(993, 4143, 0), -1, true, EntityDirection.SOUTH);
		World.spawnNPC(278, new WorldTile(989, 4142, 0), -1, true, EntityDirection.SOUTH);
		World.spawnNPC(554, new WorldTile(982, 4142, 0), -1, true, EntityDirection.SOUTH);
		World.spawnNPC(18808, new WorldTile(1002, 4124, 0), -1, true, EntityDirection.SOUTH);
		// Spawned by kingkenobi
		// masuta
		World.spawnNPC(25589, new WorldTile(1758, 5226, 0), -1, true);
		// barrel
		World.spawnNPC(5666, new WorldTile(3873, 4706, 0), -1, true);
		// Airut
		World.spawnNPC(18621, new WorldTile(1656, 5312, 0), -1, true);
		World.spawnNPC(18621, new WorldTile(1653, 5315, 0), -1, true);
		World.spawnNPC(18621, new WorldTile(1650, 5318, 0), -1, true);
		World.spawnNPC(18621, new WorldTile(1645, 5315, 0), -1, true);
		World.spawnNPC(18621, new WorldTile(1641, 5317, 0), -1, true);
		World.spawnNPC(18621, new WorldTile(1635, 5316, 0), -1, true);
		// Gem Dragon
		World.spawnNPC(24172, new WorldTile(3278, 9837, 0), -1, true);
		World.spawnNPC(24172, new WorldTile(3286, 9832, 0), -1, true);
		World.spawnNPC(24172, new WorldTile(3290, 9842, 0), -1, true);
		World.spawnNPC(24171, new WorldTile(3306, 9832, 0), -1, true);
		World.spawnNPC(24171, new WorldTile(3313, 9844, 0), -1, true);
		World.spawnNPC(24171, new WorldTile(3308, 9843, 0), -1, true);
		World.spawnNPC(24170, new WorldTile(3278, 9809, 0), -1, true);
		World.spawnNPC(24170, new WorldTile(3279, 9804, 0), -1, true);
		World.spawnNPC(24170, new WorldTile(3282, 9799, 0), -1, true);
		// Home Slayer master
		// World.spawnNPC(20112, new WorldTile(4327, 859, 0), -1, true);
		// WildyWyrm
		World.spawnNPC(3334, new WorldTile(3153, 3934, 0), -1, true);
		// Wyvern
		World.spawnNPC(21812, new WorldTile(5151, 7530, 0), -1, true);
		// Telos
		World.spawnNPC(22891, new WorldTile(3859, 7060, 0), -1, true);
		World.spawnNPC(22891, new WorldTile(3859, 7060, 1), -1, true);
		// Solak
		World.spawnNPC(25513, new WorldTile(4128, 3223, 0), -1, true);
		// Menaphos home
		World.spawnNPC(1419, new WorldTile(3230, 2730, 0), -1, true);
		World.spawnNPC(553, new WorldTile(3230, 2728, 0), -1, true);
		// Shop npcs
		World.spawnNPC(2998, new WorldTile(3241, 2728, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(659, new WorldTile(3241, 2727, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(19519, new WorldTile(3241, 2726, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(14874, new WorldTile(3241, 2725, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(3404, new WorldTile(3241, 2724, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(15085, new WorldTile(3241, 2723, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(2538, new WorldTile(3241, 2722, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(2253, new WorldTile(3241, 2721, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(278, new WorldTile(3241, 2720, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(554, new WorldTile(3241, 2719, 0), -1, true, EntityDirection.WEST);
		// other shop
		World.spawnNPC(7402, new WorldTile(3241, 2730, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(13727, new WorldTile(3241, 2731, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(522, new WorldTile(3241, 2732, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(557, new WorldTile(3241, 2733, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(14381, new WorldTile(3241, 2734, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(550, new WorldTile(3241, 2735, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(549, new WorldTile(3241, 2736, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(546, new WorldTile(3241, 2737, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(519, new WorldTile(3241, 2738, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(537, new WorldTile(3241, 2739, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(6539, new WorldTile(3241, 2729, 0), -1, true, EntityDirection.WEST);
		World.spawnNPC(522, new WorldTile(3224, 2744, 0), -1, true, EntityDirection.WEST);
		// Ripper Demon
		World.spawnNPC(21994, new WorldTile(5163, 7586, 0), -1, true);
		World.spawnNPC(21994, new WorldTile(5152, 7586, 0), -1, true);
		World.spawnNPC(21994, new WorldTile(5155, 7580, 0), -1, true);
		World.spawnNPC(21994, new WorldTile(5162, 7584, 0), -1, true);
		World.spawnNPC(21994, new WorldTile(5141, 7588, 0), -1, true);
		World.spawnNPC(21994, new WorldTile(5142, 7589, 0), -1, true);
		World.spawnNPC(21994, new WorldTile(5160, 7584, 0), -1, true);
		World.spawnNPC(21994, new WorldTile(5159, 7582, 0), -1, true);
		// Acheron Mammoth
		World.spawnNPC(22007, new WorldTile(3426, 4390, 0), -1, true);
		World.spawnNPC(22007, new WorldTile(3434, 4399, 0), -1, true);
		World.spawnNPC(22007, new WorldTile(3436, 4383, 0), -1, true);
		// Magister
		World.spawnNPC(24765, new WorldTile(2207, 6876, 0), -1, true);
		// Grenwall
		World.spawnNPC(7010, new WorldTile(2361, 3620, 0), -1, true);
		World.spawnNPC(7010, new WorldTile(2360, 3624, 0), -1, true);
		World.spawnNPC(7010, new WorldTile(2362, 3622, 0), -1, true);
		World.spawnNPC(7010, new WorldTile(2363, 3621, 0), -1, true);
		World.spawnNPC(7010, new WorldTile(2358, 3620, 0), -1, true);
		World.spawnNPC(7010, new WorldTile(2361, 3621, 0), -1, true);
		World.spawnNPC(7010, new WorldTile(2361, 3624, 0), -1, true);
		// NEWHOME
		// Welcomer
		World.spawnNPC(6139, new WorldTile(2514, 2727, 2), -1, true);
		World.spawnNPC(6139, new WorldTile(3227, 2726, 0), -1, true);
		// Shop npcs
		World.spawnNPC(2998, new WorldTile(2515, 2684, 2), -1, true, EntityDirection.WEST);
		World.spawnNPC(659, new WorldTile(2515, 2683, 2), -1, true, EntityDirection.WEST);
		World.spawnNPC(19519, new WorldTile(2515, 2682, 2), -1, true, EntityDirection.WEST);
		World.spawnNPC(14874, new WorldTile(2515, 2681, 2), -1, true, EntityDirection.WEST);
		World.spawnNPC(3404, new WorldTile(2515, 2680, 2), -1, true, EntityDirection.WEST);
		World.spawnNPC(15085, new WorldTile(2515, 2679, 2), -1, true, EntityDirection.WEST);
		//
		World.spawnNPC(2538, new WorldTile(2509, 2677, 2), -1, true, EntityDirection.NORTH);
		World.spawnNPC(2253, new WorldTile(2510, 2677, 2), -1, true, EntityDirection.NORTH);
		World.spawnNPC(278, new WorldTile(2511, 2677, 2), -1, true, EntityDirection.NORTH);
		World.spawnNPC(554, new WorldTile(2512, 2677, 2), -1, true, EntityDirection.NORTH);
		World.spawnNPC(7402, new WorldTile(2513, 2677, 2), -1, true, EntityDirection.NORTH);
		World.spawnNPC(13727, new WorldTile(2514, 2677, 2), -1, true, EntityDirection.NORTH);
		//
		// World.spawnNPC(522, new WorldTile(2500, 2722, 2), -1, true);
		World.spawnNPC(557, new WorldTile(2512, 2687, 2), -1, true, EntityDirection.SOUTHWEST);
		World.spawnNPC(14381, new WorldTile(2513, 2686, 2), -1, true, EntityDirection.SOUTHWEST);
		World.spawnNPC(522, new WorldTile(2514, 2685, 2), -1, true, EntityDirection.SOUTHWEST);
		World.spawnNPC(6539, new WorldTile(2508, 2703, 2), -1, true, EntityDirection.WEST);
		World.spawnNPC(546, new WorldTile(2511, 2695, 2), -1, true, EntityDirection.SOUTHEAST);
		World.spawnNPC(519, new WorldTile(2509, 2693, 2), -1, true, EntityDirection.SOUTHEAST);
		World.spawnNPC(537, new WorldTile(2510, 2694, 2), -1, true, EntityDirection.SOUTHEAST);
		// workshop
		World.spawnNPC(549, new WorldTile(2515, 2695, 2), -1, true, EntityDirection.WEST);
		World.spawnNPC(550, new WorldTile(2508, 2692, 2), -1, true, EntityDirection.EAST);
		// ge
		World.spawnNPC(1419, new WorldTile(2502, 2687, 2), -1, true);
		// shop stall
		World.spawnNPC(522, new WorldTile(2521, 2708, 2), -1, true, EntityDirection.WEST);
		// house agent
		World.spawnNPC(4247, new WorldTile(2516, 2667, 2), -1, true, EntityDirection.WEST);
		World.spawnNPC(4247, new WorldTile(3223, 2714, 0), -1, true, EntityDirection.NORTH);
		// EVENT
		World.spawnNPC(6390, new WorldTile(2505, 2707, 2), -1, true, EntityDirection.NORTHWEST);
		World.spawnNPC(6390, new WorldTile(4325, 850, 0), -1, true, EntityDirection.WEST);
		// Nex Angel of death
		// World.spawnNPC(24004, new WorldTile(2848, 1824, 1), -1, true,
		// EntityDirection.NORTH);
		World.spawnNPC(18808, new WorldTile(2508, 2684, 2), -1, true, EntityDirection.WEST);
		World.spawnNPC(18808, new WorldTile(3221, 2727, 0), -1, true, EntityDirection.SOUTH);

		// hween event
		World.spawnNPC(1863, new WorldTile(3218, 3402, 0), -1, true);
		World.spawnNPC(7116, new WorldTile(3025, 3237, 0), -1, true);

		/**
		 * Platinium zone
		 */
		World.spawnNPC(24574, new WorldTile(2060, 11784, 0), -1, true);
		World.spawnNPC(24574, new WorldTile(2060, 11782, 0), -1, true);
		World.spawnNPC(24574, new WorldTile(2042, 11788, 0), -1, true);
		/**
		 * Dragonkin lab
		 */
		// green dragon
		World.spawnNPC(941, new WorldTile(5024, 9231, 0), -1, true);
		World.spawnNPC(941, new WorldTile(5024, 9228, 0), -1, true);
		World.spawnNPC(941, new WorldTile(5032, 9230, 0), -1, true);
		World.spawnNPC(941, new WorldTile(5032, 9222, 0), -1, true);
		World.spawnNPC(941, new WorldTile(5017, 9222, 0), -1, true);
		World.spawnNPC(941, new WorldTile(5013, 9222, 0), -1, true);
		World.spawnNPC(941, new WorldTile(5008, 9231, 0), -1, true);
		World.spawnNPC(941, new WorldTile(5018, 9210, 0), -1, true);
		World.spawnNPC(941, new WorldTile(5027, 9225, 0), -1, true);
		// red dragon
		World.spawnNPC(53, new WorldTile(5031, 9215, 0), -1, true);
		World.spawnNPC(53, new WorldTile(5026, 9215, 0), -1, true);
		World.spawnNPC(53, new WorldTile(5031, 9206, 0), -1, true);
		World.spawnNPC(53, new WorldTile(5026, 9206, 0), -1, true);
		World.spawnNPC(53, new WorldTile(5023, 9203, 0), -1, true);
		World.spawnNPC(53, new WorldTile(5036, 9205, 0), -1, true);
		World.spawnNPC(53, new WorldTile(5046, 9205, 0), -1, true);
		// king black
		World.spawnNPC(50, new WorldTile(5039, 9191, 0), -1, true);
		// celestial
		World.spawnNPC(19109, new WorldTile(5046, 9177, 0), -1, true);
		World.spawnNPC(19109, new WorldTile(5041, 9177, 0), -1, true);
		World.spawnNPC(19109, new WorldTile(5037, 9175, 0), -1, true);
		World.spawnNPC(19109, new WorldTile(5038, 9170, 0), -1, true);
		World.spawnNPC(19109, new WorldTile(5038, 9167, 0), -1, true);
		World.spawnNPC(19109, new WorldTile(5032, 9168, 0), -1, true);
		World.spawnNPC(19109, new WorldTile(5032, 9164, 0), -1, true);
		// Black Demon
		World.spawnNPC(84, new WorldTile(5011, 9160, 0), -1, true);
		World.spawnNPC(84, new WorldTile(5011, 9158, 0), -1, true);
		World.spawnNPC(84, new WorldTile(5012, 9153, 0), -1, true);
		World.spawnNPC(84, new WorldTile(5013, 9149, 0), -1, true);
		World.spawnNPC(84, new WorldTile(5019, 9158, 0), -1, true);
		World.spawnNPC(84, new WorldTile(5007, 9149, 0), -1, true);
		World.spawnNPC(84, new WorldTile(5004, 9150, 0), -1, true);
		World.spawnNPC(84, new WorldTile(5001, 9152, 0), -1, true);
		World.spawnNPC(84, new WorldTile(5001, 9156, 0), -1, true);
		World.spawnNPC(84, new WorldTile(5001, 9165, 0), -1, true);
		// brutal green
		World.spawnNPC(5362, new WorldTile(4990, 9156, 0), -1, true);
		// Elegorn the Celestial
		World.spawnNPC(25695, new WorldTile(4973, 9138, 0), -1, true);
		// Verak Lith
		World.spawnNPC(25656, new WorldTile(5125, 9023, 0), -1, true);
		// Sunfreet
		World.spawnNPC(15222, new WorldTile(5012, 9124, 0), -1, true);
		// Dragonsonte Dragon
		World.spawnNPC(24170, new WorldTile(5007, 9109, 0), -1, true);
		World.spawnNPC(24170, new WorldTile(5011, 9098, 0), -1, true);
		World.spawnNPC(24170, new WorldTile(5020, 9107, 0), -1, true);
		World.spawnNPC(24170, new WorldTile(5021, 9112, 0), -1, true);
		World.spawnNPC(24170, new WorldTile(5025, 9115, 0), -1, true);
		World.spawnNPC(24170, new WorldTile(5027, 9121, 0), -1, true);
		World.spawnNPC(24170, new WorldTile(5040, 9123, 0), -1, true);
		// Onyx Dragon
		World.spawnNPC(24171, new WorldTile(5088, 9116, 0), -1, true);
		World.spawnNPC(24171, new WorldTile(5083, 9111, 0), -1, true);
		World.spawnNPC(24171, new WorldTile(5091, 9109, 0), -1, true);
		World.spawnNPC(24171, new WorldTile(5096, 9102, 0), -1, true);
		World.spawnNPC(24171, new WorldTile(5090, 9096, 0), -1, true);
		World.spawnNPC(24171, new WorldTile(5091, 9097, 0), -1, true);

		// Hydrix Dragon
		World.spawnNPC(24172, new WorldTile(5063, 9070, 0), -1, true);
		World.spawnNPC(24172, new WorldTile(5079, 9067, 0), -1, true);
		// Rune Dragon
		World.spawnNPC(21136, new WorldTile(5033, 9044, 0), -1, true);
		World.spawnNPC(21136, new WorldTile(5033, 9038, 0), -1, true);
		World.spawnNPC(21136, new WorldTile(5041, 9036, 0), -1, true);
		World.spawnNPC(21136, new WorldTile(5036, 9041, 0), -1, true);
		World.spawnNPC(21136, new WorldTile(5018, 9042, 0), -1, true);

	}

	public static final void loadNPCSpawns(int regionId) {
		File file = new File("data/npcs/packedSpawns/" + regionId + ".ns");
		if (!file.exists())
			return;
		try {
			RandomAccessFile in = new RandomAccessFile(file, "r");
			FileChannel channel = in.getChannel();
			ByteBuffer buffer = channel.map(MapMode.READ_ONLY, 0, channel.size());
			while (buffer.hasRemaining()) {
				int npcId = buffer.getShort() & 0xffff;
				int plane = buffer.get() & 0xff;
				int x = buffer.getShort() & 0xffff;
				int y = buffer.getShort() & 0xffff;
				boolean hashExtraInformation = buffer.get() == 1;
				int mapAreaNameHash = -1;
				boolean canBeAttackFromOutOfArea = true;
				if (hashExtraInformation) {
					mapAreaNameHash = buffer.getInt();
					canBeAttackFromOutOfArea = buffer.get() == 1;
				}
				World.spawnNPC(npcId, new WorldTile(x, y, plane), mapAreaNameHash, canBeAttackFromOutOfArea);
			}
			channel.close();
			in.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private static final void addNPCSpawn(int npcId, int regionId, WorldTile tile, int mapAreaNameHash,
			boolean canBeAttackFromOutOfArea) {
		try {
			DataOutputStream out = new DataOutputStream(
					new FileOutputStream("data/npcs/packedSpawns/" + regionId + ".ns", true));
			out.writeShort(npcId);
			out.writeByte(tile.getPlane());
			out.writeShort(tile.getX());
			out.writeShort(tile.getY());
			out.writeBoolean(mapAreaNameHash != -1);
			if (mapAreaNameHash != -1) {
				out.writeInt(mapAreaNameHash);
				out.writeBoolean(canBeAttackFromOutOfArea);
			}
			out.flush();
			out.close();
		} catch (Throwable e) {
			Logger.handle(e);
		}
	}

	private NPCSpawns() {
	}
}
