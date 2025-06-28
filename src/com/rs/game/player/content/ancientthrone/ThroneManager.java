package com.rs.game.player.content.ancientthrone;

import java.io.File;

import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.utils.SerializableFilesManager;

public class ThroneManager {

	private static Throne throne;
	private static String claimingThrone = "";
	private static int claimThroneTick = -1;
	private static int saveThrone = -1;

	private static int SECOND_CLAIM_THRONE = 60;

	public static int ANCIENT_SWORD = 40935;//you need to set these to YOUR custom items
	public static int ANCIENT_CROWN = 20857;//its not ready

	public static void runTick() {
		if (claimingThrone != null && !claimingThrone.equals("")) {
			Player player = World.getPlayer(claimingThrone);
			if (player == null) {
				claimingThrone = "";
				claimThroneTick = -1;
				return;
			}
			if (!nearThrone(player)) {
				player.errorMessage("You've stopped claiming the Ancient Throne.");
				claimingThrone = "";
				claimThroneTick = -1;
				return;
			}
			claimThroneTick++;
			player.succeedMessage("Claiming throne... " + claimThroneTick + "/" + SECOND_CLAIM_THRONE);
			if (claimThroneTick >= SECOND_CLAIM_THRONE) {
				claimThrone(player);
			}
		}
		saveThrone++;
		if (saveThrone >= 60) {
			save();
			saveThrone = -1;
		}
	}

	public static void kingDied(Player player) {
		getThrone().setKing("");
		getThrone().setTaxRate(0);
		player.yell("<shad=000000><col=FFFF00>Your " + (player.getGlobalPlayerUpdater().isMale() ? "King" : "Queen") + ", "
				+ player.getDisplayName() + " has died. The Ancient Throne is vacated once again.");
	}

	public static void startClaimingThrone(Player player) {
		if (player.getSkills().getCombatLevel() < 126) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You need at least 126 combat level to claim the throne!");
			return;
		}
		if (claimingThrone != null && !claimingThrone.equals("")) {
			player.getDialogueManager().startDialogue("SimpleMessage", "Somebody else is claiming the Ancient Throne!");
			return;
		}
		if (claimingThrone.equals(player.getUsername())) {
			player.getDialogueManager().startDialogue("SimpleMessage", "You're already claiming the Ancient Throne!");
			return;
		}
		if (getThrone().getKing().equals(player.getUsername())) {
			player.getDialogueManager().startDialogue("SimpleMessage",
					"You're already the " + (player.getGlobalPlayerUpdater().isMale() ? "King" : "Queen") + "!");
			return;
		}
		if (nearThrone(player)) {
			claimingThrone = player.getUsername();
			claimThroneTick = -1;
			player.yell("<shad=000000><col=FFFF00>" + player.getDisplayName() + " is claiming the Ancient Throne!");
		}
	}

	public static void setTax(Player player, int tax) {
		if (getThrone().getKing().equals(player.getUsername())) {
			if (tax < 0) {
				tax = 0;
			}
			if (tax > 20) {
				tax = 20;
			}
			player.yell("<shad=000000><col=FFFF00>" + player.getDisplayName() + " adjusted the taxes to " + tax + "%");
			getThrone().setTaxRate(tax);
		}
	}

	public static void claimThrone(Player player) {
		claimingThrone = "";
		claimThroneTick = -1;
		getThrone().setKing(player.getUsername());
		player.yell("<shad=000000><col=FFFF00>" + player.getDisplayName()
				+ " has claimed the Ancient Throne! Long live the "
				+ (player.getGlobalPlayerUpdater().isMale() ? "King" : "Queen") + "!");
	}
	
	public static void spawnGlobalObjects() {
		/**
		 * ANCIENT THRONE
		 */
		World.spawnNPC(2481, new WorldTile(3089, 3575, 0), -1, true, true);// servant
		World.spawnObject(new WorldObject(11, 10, 1, 3079, 3568, 0), true);
		World.spawnObject(new WorldObject(11, 10, 1, 3081, 3566, 0), true);
		World.spawnObject(new WorldObject(64282, 10, 0, 3086, 3575, 0), true);
		World.spawnObject(new WorldObject(6448, 10, 2, 3088, 9973, 0), true);
		World.spawnObject(new WorldObject(678, 10, 2, 3086, 9966, 0), true);
		World.spawnObject(new WorldObject(678, 10, 2, 3087, 9966, 0), true);
		World.spawnObject(new WorldObject(678, 10, 2, 3088, 9966, 0), true);
		World.spawnObject(new WorldObject(678, 10, 2, 3090, 9966, 0), true);
	}

	public static void init() {
		File file = new File("data/throne.at");
		if (!file.exists()) {
			setThrone(new Throne());
			save();
		} else {
			load();
		}
		spawnGlobalObjects();
	}

	public static void load() {
		setThrone(SerializableFilesManager.loadThrone());
	}

	public static void save() {
		SerializableFilesManager.saveThrone(getThrone());
	}

	public static Throne getThrone() {
		return throne;
	}

	public static boolean nearThrone(Player player) {
		if (player.getX() >= 3085 && player.getY() >= 3574 && player.getX() <= 3089 && player.getY() <= 3577) {
			return true;
		}
		return false;
	}

	public static void setThrone(Throne throne) {
		ThroneManager.throne = throne;
	}

}
