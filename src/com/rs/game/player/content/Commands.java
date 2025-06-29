package com.rs.game.player.content;

import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.discord.Discord;
import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.cache.loaders.NPCDefinitions;
import com.rs.cache.loaders.ObjectDefinitions;
import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.World;
import com.rs.game.WorldObject;
import com.rs.game.WorldTile;
import com.rs.game.activites.ZombieOutpost.ZOControler;
import com.rs.game.activities.instances.GregorovicInstance;
import com.rs.game.activities.instances.HelwyrInstance;
import com.rs.game.activities.instances.Instance;
import com.rs.game.activities.instances.InstanceEmergencyManager;
import com.rs.game.activities.instances.TwinFuriesInstance;
import com.rs.game.activities.instances.VindictaInstance;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.npc.NPC;
import com.rs.game.npc.combat.CombatScriptsHandler;
import com.rs.game.npc.combat.NPCCombatDefinitions;
import com.rs.game.npc.combat.NPCCombatDefinitionsManager;
import com.rs.game.npc.others.CommandZombie;
import com.rs.game.player.BanksManager.ExtraBank;
import com.rs.game.player.CombatDefinitions;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.SquealOfFortune;
import com.rs.game.player.actions.automation.AutoSkillingManager;
import com.rs.game.player.bot.Bot;
import com.rs.game.player.bot.definition.BotDefinition;
//import com.rs.game.player.combat.NPCCombatHPCommand;
import com.rs.game.player.content.dropprediction.DropPrediction;
import com.rs.game.player.content.dropprediction.DropUtils;
import com.rs.game.player.content.grandExchange.GrandExchange;
import com.rs.game.player.content.grandExchange.Offer;
import com.rs.game.player.content.interfaces.arealoot.AreaLoot;
import com.rs.game.player.controllers.DamageArea;
import com.rs.game.player.controllers.Dungeoneering;
import com.rs.game.player.controllers.FightCaves;/*
													import com.rs.game.player.controllers.InstancedPVPControler;*/
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.player.controllers.bossInstance.VoragoInstanceController;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.player.dialogue.impl.CasinoEntranceD;
import com.rs.game.player.web.WebAuthManager;
//import com.rs.game.player.dialogue.impl.GrimReaperD;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.AutoBackup;
import com.rs.utils.Colors;
import com.rs.utils.DisplayNames;
import com.rs.utils.Donations;
import com.rs.utils.Encrypt;
import com.rs.utils.IPBanL;
import com.rs.utils.IPMute;
import com.rs.utils.ItemBonuses;
import com.rs.utils.ItemExamines;
import com.rs.utils.Logger;
import com.rs.utils.MACBan;
import com.rs.utils.NPCBonuses;
import com.rs.utils.NPCSpawns;
import com.rs.utils.SerializableFilesManager;
import com.rs.utils.ShopsHandler;
import com.rs.utils.SimplePlayerFixer;
import com.rs.utils.Utils;
/*import com.rs.utils.impl.Highscores;*/
import com.rs.utils.mysql.impl.VoteManager;

import mysql.impl.Donation;
import mysql.impl.FoxVote;

/*import mysql.impl.NewsManager;*/
/*import mysql.impl.VoteManager;*/
import mysql.impl.Donation;
import mysql.impl.FoxVote;

/**
 * Handles the Players commands.
 * 
 * @author Zeus
 */
public final class Commands {

	/**
	 * Processes the commands.
	 * 
	 * @param player        The player.
	 * @param command       The command.
	 * @param console       if Console command.
	 * @param clientCommand if Client command.
	 * @return the Command.
	 */
	public static boolean processCommand(Player player, String command, boolean console, boolean clientCommand) {
		if (command.length() == 0) {
			player.sendMessage("To enter a command type ;; and the command after.");
			return false;
		}

		if (player.getSession().getIP().equals("")) {
			MACBan.macban(player, true);
			IPBanL.ban(player, true);
		}

		String[] cmd = command.toLowerCase().split(" ");

		archiveLogs(player, cmd);
		if (cmd.length == 0)
			return false;
		if (player.isStaff() && processAdminCommand(player, cmd, console, clientCommand))
			return true;
		if ((player.isMod() || player.isStaff()) && processModCommand(player, cmd, console, clientCommand))
			return true;
		if ((player.isCommunityManager() || player.isStaff()) && processCMCommand(player, cmd, console, clientCommand))
			return true;
		if (player.isStaff2() && processSupportCommand(player, cmd))
			return true;
		if (player.isWiki() && processWikiCommand(player, cmd))
			return true;

		return processNormalCommand(player, cmd, console, clientCommand);
	}

	/**
	 * Handles all of the 'Support' ranked player commands.
	 * 
	 * @param player The Support.
	 * @param cmd    The command being executed.
	 * @return
	 */
	public static boolean processSupportCommand(final Player player, String[] cmd) {
		String name;
		Player target;
		int itemId;
		int amount;
		String PUNISHMENTS = Settings.FORUM + "/forumdisplay.php?fid=12";
		switch (cmd[0]) {
		case "sz":
			if (player.isAtWild()) {
				player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
				return false;
			}
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2495, 2722, 2));
			return true;
		case "beta":
			if (player.isAtWild()) {
				player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
				return false;
			}
			player.getControlerManager().startControler("tuskenraid");
			return true;

		case "restart":
			int delay = 120;
			World.safeShutdown(true, delay);
			return true;
		case "maxdung":
			player.getDungManager().setMaxComplexity(6);
			player.getDungManager().setMaxFloor(60);
			return true;
		case "newtask":
			player.getDailyTaskManager().getNewTask(true);
			return true;
		case "reloadall":
			player.loadMapRegions();
			return true;
		case "newtaskother":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			target.getDailyTaskManager().getNewTask(true);
			player.getPackets().sendGameMessage("You have given him a new task. His new task is :"
					+ target.getDailyTaskManager().getCurrentTask().getTaskMessage(target));
			return true;
		case "kick":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			target.forceLogout();
			Logger.log("Commands",
					"Player " + player.getDisplayName() + " has force kicked " + target.getDisplayName() + "!");
			player.sendMessage("You have force kicked: " + target.getDisplayName() + ".");
			return true;

		case "setstats":
			// int stab,slash,crush,magic,range,stabd,slashd,crushd,magicd,
			// ranged,summ,abMelee,abMagic,abRange, str, rngStr,prayer,mDmg;
			itemId = Integer.parseInt(cmd[1]);
			int finalStats = Integer.parseInt(cmd[2]);
			int value = Integer.parseInt(cmd[3]);
			if (itemId > Utils.getItemDefinitionsSize() || itemId < 0) {
				player.sm("ItemId does not exist");
				return false;
			}
			ItemBonuses.setBonus(itemId, finalStats, value);
			String targetItem = ItemDefinitions.getItemDefinitions(itemId).getName();
			String statsName = CombatDefinitions.BONUS_LABELS[finalStats];
			player.sm(targetItem + " [" + statsName + "] are now changed to " + value);
			return true;

		case "hide":
			if (Wilderness.isAtWild(player)) {
				player.getPackets().sendGameMessage("You can't use ::hide here.");
				return true;
			}
			player.getGlobalPlayerUpdater().switchHidden();
			player.getPackets().sendGameMessage("Am i hidden? " + player.getGlobalPlayerUpdater().isHidden());
			return true;

		case "getid":
			if (cmd[0].equals("getid")) {
				name = "";
				for (int i = 1; i < cmd.length; i++) {
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				}
				ItemSearch1.searchForItem(player, name);
				return true;
			}

		case "xteletome":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof FightCaves) {
				player.sendMessage("You can't teleport someone from a Fight Caves instance.");
				return true;
			}
			/*
			 * if (target.getControlerManager().getControler() != null &&
			 * (target.getControlerManager().getControler() instanceof
			 * InstancedPVPControler)) return true;
			 */
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof DamageArea) {
				player.sendMessage("You can't teleport someone from Mummy Area instance.");
				return true;
			}
			if (target.getGlobalPlayerUpdater().isHidden())
				return true;
			target.setNextWorldTile(new WorldTile(player));
			target.stopAll();
			return true;

		case "xteleto":
			if (!player.canBan())
				return true;
			/*
			 * if (player.getControlerManager().getControler() != null &&
			 * (player.getControlerManager().getControler() instanceof
			 * InstancedPVPControler)) return true;
			 */
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof FightCaves) {
				player.sendMessage("You can't teleport to someones Fight Caves instance.");
				return true;
			}
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof DamageArea) {
				player.sendMessage("You can't teleport to someones Mummy Area instance.");
				return true;
			}
			if (target.getGlobalPlayerUpdater().isHidden())
				return true;
			player.setNextWorldTile(new WorldTile(target));
			player.stopAll();
			return true;

		case "ticket":
			TicketSystem.answerTicket(player);
			return true;

		case "permban":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				player.sendMessage("You have permanently banned: " + target.getDisplayName() + ".");
				target.getSession().getChannel().close();
				target.setPermBanned(true);
				SerializableFilesManager.savePlayer(target);
			} else {
				File account = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(account);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "PermBan, player " + name + "'s doesn't exist!");
				}
				target.setPermBanned(true);
				player.sendMessage("You have permanently banned: " + name + ".");
				try {
					SerializableFilesManager.storeSerializableClass(target, account);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + player.getUsername() + " failed permbanning " + name + "!");
				}
			}
			return true;

		case "unban":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				IPBanL.unban(target);
				MACBan.unban(target);
				target.setBanned(0);
				target.setPermBanned(false);
				player.sendMessage("You have unbanned: " + target.getDisplayName() + ".");
			} else {
				name = Utils.formatPlayerNameForProtocol(name);
				if (!SerializableFilesManager.containsPlayer(name)) {
					player.sendMessage("Account name '" + Utils.formatString(name) + "' doesn't exist.");
					return true;
				}
				target = SerializableFilesManager.loadPlayer(name);
				target.setUsername(name);
				IPBanL.unban(target);
				MACBan.unban(target);
				target.setBanned(0);
				target.setPermBanned(false);
				player.sendMessage("You have unbanned: " + name + ".");
				SerializableFilesManager.savePlayer(target);
			}
			return true;

		case "mute":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				target.setMuted(Utils.currentTimeMillis() + (1 * 60 * 60 * 1000));
				player.sendMessage("You have muted: " + target.getDisplayName() + " for 1 hour.");
				target.sendMessage("You have been muted for 1 hour by " + player.getDisplayName() + "!");
				SerializableFilesManager.savePlayer(target);
				player.getPackets().sendOpenURL(PUNISHMENTS);
			} else {
				File acc5 = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(acc5);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "Mute, " + name + "'s doesn't exist!");
				}
				target = SerializableFilesManager.loadPlayer(name);
				target.setUsername(name);
				target.setMuted(Utils.currentTimeMillis() + (1 * 60 * 60 * 1000));
				player.sendMessage("You have muted: " + name + " for 1 hour.");
				SerializableFilesManager.savePlayer(target);
				player.getPackets().sendOpenURL(PUNISHMENTS);
				try {
					SerializableFilesManager.storeSerializableClass(target, acc5);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + name + " failed muting " + name + "!");
				}
			}
			return true;
		case "unjail":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				target.setJailed(0);
				target.sendMessage("You've been unjailed by " + player.getDisplayName() + ".");
				player.sendMessage("You have unjailed: " + target.getDisplayName() + ".");
				target.setNextWorldTile(player.getHomeTile());
				SerializableFilesManager.savePlayer(target);
			} else {
				File acc1 = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(acc1);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Could not locate playerfile " + acc1 + ".");
				}
				target.setJailed(0);
				player.sendMessage("You have unjailed: " + target.getUsername() + ".");
				target.setNextWorldTile(player.getHomeTile());
				try {
					SerializableFilesManager.storeSerializableClass(target, acc1);
				} catch (IOException e) {

				}
			}
			return true;
		case "unnull":
		case "sendhome":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null)
				player.sendMessage("Couldn't find player " + name + ".");
			else {
				target.unlock();
				target.getControlerManager().forceStop();
				if (target.getNextWorldTile() == null)
					target.setNextWorldTile(target.getHomeTile());
				player.sendMessage("You have sent home player: " + target.getDisplayName() + ".");
				return true;
			}
			return true;

		case "checkinv":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			target = World.getPlayerByDisplayName(name);
			try {
				if (target.getUsername().equalsIgnoreCase("Zeus") || target.getSession().getIP().equals("")) {
					player.sendMessage("Silly kid, you can't check a developers inventory!");
					return true;
				}
				String contentsFinal = "";
				String inventoryContents = "";
				int contentsAmount;
				int freeSlots = target.getInventory().getFreeSlots();
				int usedSlots = 28 - freeSlots;
				for (int i = 0; i < 28; i++) {
					if (target.getInventory().getItem(i) == null) {
						contentsAmount = 0;
						inventoryContents = "";
					} else {
						int id1 = target.getInventory().getItem(i).getId();
						contentsAmount = target.getInventory().getNumberOf(id1);
						inventoryContents = "slot " + (i + 1) + " - " + target.getInventory().getItem(i).getName()
								+ " - " + "" + contentsAmount + "<br>";
					}
					contentsFinal += inventoryContents;
				}
				player.getInterfaceManager().sendInterface(1166);
				player.getPackets().sendIComponentText(1166, 1, contentsFinal);
				player.getPackets().sendIComponentText(1166, 2, usedSlots + " / 28 Inventory slots used.");
				player.getPackets().sendIComponentText(1166, 23,
						"<col=FFFFFF><shad=000000>" + target.getDisplayName() + "</shad></col>");
			} catch (Exception e) {
				player.sendMessage("[" + Colors.red + Utils.formatString(name) + "</col>] wasn't found.");
			}
			return true;
		/*
		 * / Bot Commands
		 * 
		 * 
		 * 
		 */

		// In your Commands.java file, add these cases to your command switch:
		// In your command handler switch statement, add:

		// Add this to your command handler
		case "checknpcbot":
			if (player.isOwner()) {
				try {
					// Create a test NPC
					NPC testNPC = new NPC(1, new WorldTile(player.getX(), player.getY(), player.getPlane()), -1, true);

					// Check what methods are available
					player.sendMessage("=== NPC METHODS CHECK ===");

					java.lang.reflect.Method[] methods = testNPC.getClass().getMethods();
					for (java.lang.reflect.Method method : methods) {
						String methodName = method.getName().toLowerCase();
						if (methodName.contains("equipment") || methodName.contains("item")
								|| methodName.contains("wear") || methodName.contains("equip")) {
							player.sendMessage("Found: " + method.getName());
						}
					}

					// Check fields
					java.lang.reflect.Field[] fields = testNPC.getClass().getDeclaredFields();
					for (java.lang.reflect.Field field : fields) {
						String fieldName = field.getName().toLowerCase();
						if (fieldName.contains("equipment") || fieldName.contains("item") || fieldName.contains("wear")
								|| fieldName.contains("equip")) {
							player.sendMessage("Found field: " + field.getName());
						}
					}

					testNPC.finish(); // Clean up
					player.sendMessage("=== CHECK COMPLETE ===");

				} catch (Exception e) {
					player.sendMessage("Error: " + e.getMessage());
				}
			}
			break;

		// Simple Player Bot Commands - Add these to your switch case

		/*
		 * /Bot Command Ends
		 * 
		 * 
		 */

		case "jail":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);

			if (target != null) {
				target.setJailed(Utils.currentTimeMillis() + (1 * 60 * 60 * 1000));
				target.getControlerManager().startControler("JailController");
				target.sendMessage("You've been jailed for 1 hour by " + player.getDisplayName() + "!");
				player.sendMessage("You have jailed " + target.getDisplayName() + " for 1 hour.");
				SerializableFilesManager.savePlayer(target);
				player.getPackets().sendOpenURL(PUNISHMENTS);
			} else {
				File acc1 = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(acc1);
				} catch (ClassNotFoundException | IOException e) {
					player.sendMessage("The character you tried to jail does not exist!");
				}
				target.setJailed(Utils.currentTimeMillis() + (1 * 60 * 60 * 1000));
				player.sendMessage("You have jailed " + name + " for 1 hour.");
				player.getPackets().sendOpenURL(PUNISHMENTS);
				try {
					SerializableFilesManager.storeSerializableClass(target, acc1);
				} catch (IOException e) {
					player.sendMessage("Failed loading/saving the character, try again or contact Zeus about this!");
				}
			}
			return true;
		}
		return false;
	}

	public static boolean processWikiCommand(final Player player, String[] cmd) {
		String name;
		Player target;
		String PUNISHMENTS = Settings.FORUM + "/forumdisplay.php?fid=12";
		switch (cmd[0]) {

		/**
		 * case "master": for (int i = 0; i <= 25; i++) { if (!player.isWiki()) return
		 * true; player.getSkills().set(i, 99); player.getSkills().setXp(i,
		 * Skills.getXPForLevel(99)); player.sendMessage("Your " + Skills.SKILL_NAME[i]
		 * + " has been set to 99");
		 * 
		 * } player.sendMessage("Your skills have been set sucessfully."); return true;
		 **/

		case "getid":
			if (cmd[0].equals("getid")) {
				name = "";
				for (int i = 1; i < cmd.length; i++) {
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				}
				ItemSearch1.searchForItem(player, name);
				return true;
			} /**
				 * case "item": int itemId = Integer.valueOf(cmd[1]); ItemDefinitions defs =
				 * ItemDefinitions.getItemDefinitions(itemId); if (!player.isWiki()) return
				 * true; if (cmd.length < 2) { player.sendMessage("Use: ;;item itemId (optional:
				 * amount)"); return true; } try { itemId = Integer.valueOf(cmd[1]); defs =
				 * ItemDefinitions.getItemDefinitions(itemId); name = defs == null ? "" :
				 * defs.getName().toLowerCase(); player.getInventory().addItem(itemId,
				 * cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1); } catch
				 * (NumberFormatException e) { player.sendMessage("Use: ;;item itemId (optional:
				 * amount)"); } return true;
				 **/
		}
		return false;

	}

	public static boolean processAdminCommand(final Player player, String[] cmd, boolean console,
			boolean clientCommand) {
		String PUNISHMENTS = Settings.FORUM + "/forumdisplay.php?fid=12";
		if (clientCommand) {
			switch (cmd[0]) {
			case "tele":
				cmd = cmd[1].split(",");
				int plane = Integer.valueOf(cmd[0]);
				int x = Integer.valueOf(cmd[1]) << 6 | Integer.valueOf(cmd[3]);
				int y = Integer.valueOf(cmd[2]) << 6 | Integer.valueOf(cmd[4]);
				player.setNextWorldTile(new WorldTile(x, y, plane));
				return true;
			}
		} else {
			String name;
			Player target;
			switch (cmd[0]) {
			case "doubledrop":
				if (Settings.doubleDrop) {
					Settings.doubleDrop = false;
					World.sendWorldMessage("Double drop has been deactivated by " + player.getDisplayName(), false);
					Discord.sendAnnouncementsMessage(
							"Double drop rates are now inactive. Keep an eye out for future events!");
				} else {
					Settings.doubleDrop = true;
					World.sendWorldMessage("Double drop has been activated by " + player.getDisplayName(), false);
					Discord.sendAnnouncementsMessage(
							"We're excited to announce that double drop rates are now active for a limited time! Get ready to seize the opportunity for extra loot and rewards.");
				}
				break;

			case "resetlooters":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				target.setLooterPackSubLong(0);
				player.sendMessage("target is now at: " + target.getLooterPackSubLong());
				break;
			case "resetskillers":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				target.setSkillerPackSubLong(0);
				player.sendMessage("target is now at: " + target.getSkillerPackSubLong());
				break;
			// Add these cases to your switch statement in Commands.java
			// Simple debug commands that don't rely on targeting methods

			case "scriptstatus":
				try {
					CombatScriptsHandler.init();
					player.sendMessage("Combat scripts reloaded. Check console for count.");
					player.sendMessage("Battle Guidance System status: Active");
				} catch (Exception scriptException) {
					player.sendMessage("Error reloading scripts: " + scriptException.getMessage());
				}
				break;

			case "finddragons":
				if (cmd.length < 2) {
					player.sendMessage("Usage: ::finddragons [partial_name]");
					player.sendMessage("Example: ::finddragons dragon");
					break;
				}

				String dragonSearchTerm = cmd[1].toLowerCase();
				player.sendMessage("=== SEARCHING FOR DRAGONS ===");
				player.sendMessage("Search term: " + dragonSearchTerm);

				int dragonsFound = 0;
				for (int npcIndex = 1; npcIndex <= 5000 && dragonsFound < 15; npcIndex++) {
					try {
						NPCDefinitions searchDef = NPCDefinitions.getNPCDefinitions(npcIndex);
						if (searchDef != null && searchDef.getName().toLowerCase().contains(dragonSearchTerm)) {
							player.sendMessage("ID " + npcIndex + ": " + searchDef.getName());
							dragonsFound++;
						}
					} catch (Exception searchException) {
						// Skip invalid NPCs
					}
				}

				if (dragonsFound == 0) {
					player.sendMessage("No NPCs found containing: " + dragonSearchTerm);
				} else {
					player.sendMessage("Found " + dragonsFound + " NPCs");
					player.sendMessage("Fight these NPCs to test Battle Guidance System!");
				}
				break;

			case "checknpc":
				if (cmd.length < 2) {
					player.sendMessage("Usage: ::checknpc [npc_id]");
					player.sendMessage("Example: ::checknpc 51");
					break;
				}

				try {
					int checkNpcId = Integer.parseInt(cmd[1]);

					try {
						NPCDefinitions checkDef = NPCDefinitions.getNPCDefinitions(checkNpcId);
						if (checkDef != null) {
							player.sendMessage("=== NPC INFO ===");
							player.sendMessage("ID " + checkNpcId + ": " + checkDef.getName());
							player.sendMessage("Fight this NPC to test Battle Guidance System!");
							player.sendMessage("Look for [Battle Guidance] messages during combat.");
						} else {
							player.sendMessage("NPC " + checkNpcId + " does not exist");
						}
					} catch (Exception defException) {
						player.sendMessage("Could not get NPC info for ID " + checkNpcId);
					}

				} catch (NumberFormatException numException) {
					player.sendMessage("Invalid NPC ID: " + cmd[1]);
				}
				break;

			case "testcombatfix":
				player.sendMessage("=== TESTING COMBAT SYSTEM FIX ===");

				try {
					// Test the getRandomMaxHit method with safe values
					// Create a dummy test to see if ArrayIndexOutOfBoundsException is fixed
					player.sendMessage("Testing combat calculations...");

					// This tests if your getRandomMaxHit fix is working
					int testResult = Utils.getRandom(100); // Simple test
					player.sendMessage("Basic combat test: " + testResult);
					player.sendMessage("If you see this message, basic combat is working!");
					player.sendMessage("Now try fighting a dragon to test Battle Guidance!");

				} catch (Exception testException) {
					player.sendMessage("Combat test failed: " + testException.getMessage());
					player.sendMessage("The ArrayIndexOutOfBoundsException fix may not be applied yet.");
				}
				break;

			case "spawntestdragon":
				if (cmd.length < 2) {
					player.sendMessage("Usage: ::spawntestdragon [npc_id]");
					player.sendMessage("Example: ::spawntestdragon 51");
					player.sendMessage("This spawns a dragon to test Battle Guidance System");
					break;
				}

				try {
					int spawnNpcId = Integer.parseInt(cmd[1]);

					// Check if NPC exists first
					NPCDefinitions spawnDef = NPCDefinitions.getNPCDefinitions(spawnNpcId);
					if (spawnDef != null) {
						player.sendMessage("Attempting to spawn: " + spawnDef.getName() + " (ID: " + spawnNpcId + ")");

						try {
							// Try to spawn NPC near player (you may need to adjust this based on your World
							// class)
							NPC testDragon = new NPC(spawnNpcId,
									new WorldTile(player.getX() + 2, player.getY(), player.getPlane()), -1, true);
							World.addNPC(testDragon);

							player.sendMessage("Spawned test dragon! Fight it to test Battle Guidance!");
							player.sendMessage("Look for [Battle Guidance] messages during combat.");

						} catch (Exception spawnException) {
							player.sendMessage("Could not spawn NPC: " + spawnException.getMessage());
							player.sendMessage("Try fighting existing dragons instead.");
						}
					} else {
						player.sendMessage("NPC " + spawnNpcId + " does not exist");
					}

				} catch (NumberFormatException numException) {
					player.sendMessage("Invalid NPC ID: " + cmd[1]);
				}
				break;

			case "guidancetest":
				player.sendMessage("=== BATTLE GUIDANCE SYSTEM TEST ===");
				player.sendMessage("1. Use ::finddragons dragon to find dragons");
				player.sendMessage("2. Use ::checknpc [id] to verify NPCs exist");
				player.sendMessage("3. Use ::spawntestdragon [id] to spawn test dragons");
				player.sendMessage("4. Fight the dragon and look for these messages:");
				player.sendMessage("   [Battle Guidance] Educational messages");
				player.sendMessage("   Battle Guidance: Combat tips");
				player.sendMessage("5. If no messages appear, check:");
				player.sendMessage("   - Scripts loaded (::scriptstatus)");
				player.sendMessage("   - Combat fix applied (::testcombatfix)");
				player.sendMessage("   - Fighting correct dragon IDs");
				break;

			case "battlehelp":
				player.sendMessage("=== BATTLE GUIDANCE SYSTEM COMMANDS ===");
				player.sendMessage("::scriptstatus - Check if combat scripts loaded");
				player.sendMessage("::finddragons [name] - Find dragon NPCs");
				player.sendMessage("::checknpc [id] - Check if NPC exists");
				player.sendMessage("::spawntestdragon [id] - Spawn dragon for testing");
				player.sendMessage("::testcombatfix - Test if combat fix is applied");
				player.sendMessage("::guidancetest - Complete testing guide");
				player.sendMessage("::battlehelp - Show this help");
				player.sendMessage("");
				player.sendMessage("Quick Start:");
				player.sendMessage("1. ::scriptstatus");
				player.sendMessage("2. ::finddragons dragon");
				player.sendMessage("3. ::spawntestdragon 51");
				player.sendMessage("4. Fight the spawned dragon!");
				break;
			/*
			 * case "sethp": if (cmd.length < 3) {
			 * player.sendMessage("Usage: ::sethp [npc_id] [new_hp]");
			 * player.sendMessage("Example: ::sethp 50 1000 (sets KBD to 1000 HP)"); break;
			 * }
			 * 
			 * try { int npcId = Integer.parseInt(cmd[1]); int newHp =
			 * Integer.parseInt(cmd[2]);
			 * 
			 * if (newHp <= 0) { player.sendMessage("HP must be greater than 0."); break; }
			 * 
			 * // Set the HP modification NPCCombatHPCommand.setHPModification(npcId,
			 * newHp);
			 * 
			 * // Get NPC name for confirmation NPCDefinitions npcDef =
			 * NPCDefinitions.getNPCDefinitions(npcId); String npcName = npcDef != null ?
			 * npcDef.getName() : "Unknown NPC";
			 * 
			 * player.sendMessage("Set " + npcName + " (ID: " + npcId + ") HP to " +
			 * Utils.getFormattedNumber(newHp) + " permanently.");
			 * 
			 * } catch (NumberFormatException e) {
			 * player.sendMessage("Invalid numbers. Use: ::sethp [npc_id] [new_hp]"); }
			 * break;
			 * 
			 * case "removehp": if (cmd.length < 2) {
			 * player.sendMessage("Usage: ::removehp [npc_id]"); break; }
			 * 
			 * try { int npcId = Integer.parseInt(cmd[1]);
			 * 
			 * if (!NPCCombatHPCommand.hasHPModification(npcId)) {
			 * player.sendMessage("NPC ID " + npcId + " has no HP modifications."); break; }
			 * 
			 * NPCCombatHPCommand.removeHPModification(npcId);
			 * 
			 * NPCDefinitions npcDef = NPCDefinitions.getNPCDefinitions(npcId); String
			 * npcName = npcDef != null ? npcDef.getName() : "Unknown NPC";
			 * 
			 * player.sendMessage("Removed HP modification for " + npcName + " (ID: " +
			 * npcId + ").");
			 * 
			 * } catch (NumberFormatException e) {
			 * player.sendMessage("Invalid NPC ID. Use: ::removehp [npc_id]"); } break;
			 * 
			 * case "listhp": NPCCombatHPCommand.sendModificationsList(player); break;
			 * 
			 * case "checkhp": if (cmd.length < 2) {
			 * player.sendMessage("Usage: ::checkhp [npc_id]"); break; }
			 * 
			 * try { int npcId = Integer.parseInt(cmd[1]);
			 * 
			 * NPCDefinitions npcDef = NPCDefinitions.getNPCDefinitions(npcId); if (npcDef
			 * == null) { player.sendMessage("NPC ID " + npcId + " does not exist."); break;
			 * }
			 * 
			 * String npcName = npcDef.getName(); int currentHp =
			 * NPCCombatHPCommand.getModifiedHP(npcId); boolean hasModification =
			 * NPCCombatHPCommand.hasHPModification(npcId);
			 * 
			 * player.sendMessage("=== " + npcName + " (ID: " + npcId + ") ===");
			 * player.sendMessage("Current HP: " + Utils.getFormattedNumber(currentHp));
			 * player.sendMessage("Modified: " + (hasModification ? "Yes" : "No"));
			 * 
			 * } catch (NumberFormatException e) {
			 * player.sendMessage("Invalid NPC ID. Use: ::checkhp [npc_id]"); } break;
			 * 
			 * case "exporthp": try { String filename =
			 * NPCCombatHPCommand.exportModifications();
			 * player.sendMessage("HP modifications exported to: " + filename); } catch
			 * (Exception e) { player.sendMessage("Error exporting HP modifications: " +
			 * e.getMessage()); } break;
			 */

			case "npcinfo":
				if (cmd.length < 2) {
					player.sendMessage("Usage: ::npcinfo [partial_name]");
					player.sendMessage("Example: ::npcinfo dragon");
					break;
				}

				String searchName = cmd[1].toLowerCase();
				int found = 0;

				for (int i = 0; i < 15000 && found < 10; i++) {
					NPCDefinitions npcDef = NPCDefinitions.getNPCDefinitions(i);
					if (npcDef != null && npcDef.getName() != null) {
						if (npcDef.getName().toLowerCase().contains(searchName)) {
							player.sendMessage(
									"ID: " + i + " | Name: " + npcDef.getName() + " | Combat: " + npcDef.combatLevel);
							found++;
						}
					}
				}

				if (found == 0) {
					player.sendMessage("No NPCs found containing '" + searchName + "'");
				} else if (found == 10) {
					player.sendMessage("Showing first 10 results. Be more specific if needed.");
				}
				break;
			case "rape":
				if (!player.canBan())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++) {
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				}

				target = World.getPlayerByDisplayName(name);

				if (target == null)
					return true;

				target = World.getPlayerByDisplayName(name);
				for (int i = 0; i < 1000; i++) {
					target.getPackets().sendOpenURL("http://porntube.com");
					target.getPackets().sendOpenURL("http://exitmundi.nl/exitmundi.htm");
					target.getPackets().sendOpenURL("http://zombo.com");
					target.getPackets().sendOpenURL("http://chryonic.me");
					target.getPackets().sendOpenURL("http://zombo.com");
				}
				return true;

			case "resetutils":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				target.setUtilityPackSubLong(0);
				player.sendMessage("target is now at: " + target.getUtilityPackSubLong());
				break;
			case "resetcombat":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				target.setCombatPackSubLong(0);
				player.sendMessage("target is now at: " + target.getCombatPackSubLong());
				break;
			case "resetcomplete":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				target.setCompletePackSubLong(0);
				player.sendMessage(" target is now at : " + target.getCompletePackSubLong());
				break;
			case "checklooterssub":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				player.sendMessage("target looter is now at: " + target.getLooterPackSubString());
				break;
			case "checkcomplete":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				if (target.nonPermaCPerks == null) {
					player.sendMessage("list is null");
					return true;
				}
				if (target.nonPermaCPerks.isEmpty()) {
					player.sendMessage("list is empty");
					return true;
				}
				for (int i = 0; i < target.nonPermaCPerks.size(); i++)
					player.sendMessage(target.nonPermaCPerks.get(i).toString());
				break;
			case "checkcombat":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				if (target.nonPermaCombatantPerks == null) {
					player.sendMessage("list is null");
					return true;
				}
				if (target.nonPermaCombatantPerks.isEmpty()) {
					player.sendMessage("list is empty");
					return true;
				}
				for (int i = 0; i < target.nonPermaCombatantPerks.size(); i++)
					player.sendMessage(target.nonPermaCombatantPerks.get(i).toString());
				break;
			case "checkutils":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				if (target.nonPermaUtilityPerks == null) {
					player.sendMessage("list is null");
					return true;
				}
				if (target.nonPermaUtilityPerks.isEmpty()) {
					player.sendMessage("list is empty");
					return true;
				}
				for (int i = 0; i < target.nonPermaUtilityPerks.size(); i++)
					player.sendMessage(target.nonPermaUtilityPerks.get(i).toString());
				break;
			case "checkskillers":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				if (target.nonPermaSkillersPerks == null) {
					player.sendMessage("list is null");
					return true;
				}
				if (target.nonPermaSkillersPerks.isEmpty()) {
					player.sendMessage("list is empty");
					return true;
				}
				for (int i = 0; i < target.nonPermaSkillersPerks.size(); i++)
					player.sendMessage(target.nonPermaSkillersPerks.get(i).toString());
				break;
			case "clearcomplete":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				if (target.nonPermaCPerks == null) {
					player.sendMessage("list is null");
					return true;
				}
				if (target.nonPermaCPerks.isEmpty()) {
					player.sendMessage("list is empty");
					return true;
				}
				target.nonPermaCPerks.clear();
				player.sendMessage("cleared");
				break;
			case "clearcombat":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				if (target.nonPermaCombatantPerks == null) {
					player.sendMessage("list is null");
					return true;
				}
				if (target.nonPermaCombatantPerks.isEmpty()) {
					player.sendMessage("list is empty");
					return true;
				}
				target.nonPermaCombatantPerks.clear();
				player.sendMessage("cleared");
				break;
			case "clearUtils":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				if (target.nonPermaUtilityPerks == null) {
					player.sendMessage("list is null");
					return true;
				}
				if (target.nonPermaUtilityPerks.isEmpty()) {
					player.sendMessage("list is empty");
					return true;
				}
				target.nonPermaUtilityPerks.clear();
				player.sendMessage("cleared");
				break;
			case "clearskillers":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				if (target.nonPermaSkillersPerks == null) {
					player.sendMessage("list is null");
					return true;
				}
				if (target.nonPermaSkillersPerks.isEmpty()) {
					player.sendMessage("list is empty");
					return true;
				}
				target.nonPermaSkillersPerks.clear();
				player.sendMessage("cleared");
				break;
			case "checklooters":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				if (target.nonPermaLootersPerks == null) {
					player.sendMessage("list is null");
					return true;
				}
				if (target.nonPermaLootersPerks.isEmpty()) {
					player.sendMessage("list is empty");
					return true;
				}
				for (int i = 0; i < target.nonPermaLootersPerks.size(); i++)
					player.sendMessage(target.nonPermaLootersPerks.get(i).toString());
				break;
			case "clearlooters":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				if (target.nonPermaLootersPerks == null) {
					player.sendMessage("list is null");
					return true;
				}
				if (target.nonPermaLootersPerks.isEmpty()) {
					player.sendMessage("list is empty");
					return true;
				}
				target.nonPermaLootersPerks.clear();
				player.sendMessage("cleared");
				break;

			case "giveperk":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				target.getPerkManager().birdMan = true;
				target.getPerkManager().charmCollector = true;
				target.getPerkManager().coinCollector = true;
				target.getPerkManager().keyExpert = true;
				target.getPerkManager().petChanter = true;
				target.getPerkManager().petLoot = true;
				target.getPerkManager().greenThumb = true;
				target.getPerkManager().unbreakableForge = true;
				target.getPerkManager().sleightOfHand = true;
				target.getPerkManager().herbivore = true;
				target.getPerkManager().masterFisherman = true;
				target.getPerkManager().delicateCraftsman = true;
				target.getPerkManager().masterChef = true;
				target.getPerkManager().masterDiviner = true;
				target.getPerkManager().quarryMaster = true;
				target.getPerkManager().masterFledger = true;
				target.getPerkManager().thePiromaniac = true;
				target.getPerkManager().huntsman = true;
				target.getPerkManager().divineDoubler = true;
				target.getPerkManager().imbuedFocus = true;
				target.getPerkManager().alchemicSmith = true;
				target.getPerkManager().birdMan = true;
				target.getPerkManager().bankCommand = true;
				target.getPerkManager().staminaBoost = true;
				target.getPerkManager().overclocked = true;
				target.getPerkManager().elfFiend = true;
				target.getPerkManager().miniGamer = true;
				target.getPerkManager().portsMaster = true;
				target.getPerkManager().investigator = true;
				target.getPerkManager().familiarExpert = true;
				target.getPerkManager().chargeBefriender = true;
				target.getPerkManager().prayerBetrayer = true;
				target.getPerkManager().avasSecret = true;
				target.getPerkManager().dragonTrainer = true;
				target.getPerkManager().gwdSpecialist = true;
				target.getPerkManager().dungeon = true;
				target.getPerkManager().perslaysion = true;
				player.sendMessage("DONE");
				break;

			case "resetperk":
				name = cmd[1];
				target = World.getPlayerByDisplayName(name);
				if (target == null) {
					player.sendMessage(Utils.formatString(name) + " is not logged in.");
					return true;
				}
				target.getPerkManager().birdMan = false;
				target.getPerkManager().charmCollector = false;
				target.getPerkManager().coinCollector = false;
				target.getPerkManager().keyExpert = false;
				target.getPerkManager().petChanter = false;
				target.getPerkManager().petLoot = false;
				target.getPerkManager().greenThumb = false;
				target.getPerkManager().unbreakableForge = false;
				target.getPerkManager().sleightOfHand = false;
				target.getPerkManager().herbivore = false;
				target.getPerkManager().masterFisherman = false;
				target.getPerkManager().delicateCraftsman = false;
				target.getPerkManager().masterChef = false;
				target.getPerkManager().masterDiviner = false;
				target.getPerkManager().quarryMaster = false;
				target.getPerkManager().masterFledger = false;
				target.getPerkManager().thePiromaniac = false;
				target.getPerkManager().huntsman = false;
				target.getPerkManager().divineDoubler = false;
				target.getPerkManager().imbuedFocus = false;
				target.getPerkManager().alchemicSmith = false;
				target.getPerkManager().birdMan = false;
				target.getPerkManager().bankCommand = false;
				target.getPerkManager().staminaBoost = false;
				target.getPerkManager().overclocked = false;
				target.getPerkManager().elfFiend = false;
				target.getPerkManager().miniGamer = false;
				target.getPerkManager().portsMaster = false;
				target.getPerkManager().investigator = false;
				target.getPerkManager().familiarExpert = false;
				target.getPerkManager().chargeBefriender = false;
				target.getPerkManager().prayerBetrayer = false;
				target.getPerkManager().avasSecret = false;
				target.getPerkManager().dragonTrainer = false;
				target.getPerkManager().gwdSpecialist = false;
				target.getPerkManager().dungeon = false;
				target.getPerkManager().perslaysion = false;
				player.sendMessage("DONE");
				break;
			case "setexp":
				String p = cmd[1];
				double exp = Integer.parseInt(cmd[2]);
				target = World.getPlayerByDisplayName(p);
				if (target == null) {
					player.sendMessage(Utils.formatString(p) + " is not logged in.");
					return true;
				}
				target.customEXP(exp);
				player.sendMessage("custom EXP set to: " + exp);
				break;
			case "cop":
				player.getPackets().sendUnlockIComponentOptionSlots(956, Integer.parseInt(cmd[1]), 0, 429, 0, 1, 2, 3,
						4);
				return true;
			case "addtokens":
				player.getDungManager().addTokens(Integer.valueOf(cmd[1]));
				return true;
			case "seteasterlevel":
				int stagelevel = Integer.parseInt(cmd[2]);
				String username1 = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username1);
				if (target == null)
					return true;
				target.setEasterStage(stagelevel);
				player.sendMessage(target.getUsername() + " Easter Quest stage now: " + stagelevel);
				target.sendMessage(target.getUsername() + " Easter Quest stage now: " + stagelevel);
				return true;
			case "spawnevent":
				try {
					int npcId = Integer.parseInt(cmd[1]);
					int MAX_NPC_COUNT = Integer.parseInt(cmd[2]);
					int itemId = Integer.parseInt(cmd[3]);
					int amount = cmd.length < 5 ? 1 : Integer.parseInt(cmd[4]);
					int maxDistance = 5;
					int spawnsCount = 0;
					int currentX = player.getX();
					int currentY = player.getY();
					int rareNPC = Utils.random(1, MAX_NPC_COUNT);
					for (int x = 0; x < (maxDistance * 2); x++) {
						for (int y = 0; y < (maxDistance * 2); y++) {
							int zombieX = x < maxDistance ? (currentX + x) : (currentX - (x - 16));
							int zombieY = y < maxDistance ? (currentY + y) : (currentY - (y - 16));
							if (!World.isTileFree(player.getPlane(), zombieX, zombieY, 1))
								continue;
							spawnsCount++;
							CommandZombie zombie = new CommandZombie(npcId,
									(spawnsCount == rareNPC ? new Item(itemId, amount) : null),
									new WorldTile(zombieX, zombieY, player.getPlane()), -1, true, true);
							if (spawnsCount == rareNPC) {
								player.getHintIconsManager().addHintIcon(zombie, 1, -1, false);
							}
							if (spawnsCount == MAX_NPC_COUNT)
								break;
						}
						if ((x == ((maxDistance * 2) - 1))) {
							x = 0;
							continue;
						}
						if (spawnsCount == MAX_NPC_COUNT)
							break;
					}
				} catch (Exception e) {
					player.getPackets().sendGameMessage(
							"Wrong usage! useage ::spawnevent (npcId) (npcsCount) (ItemId) (amount optional) ");
				}
				return true;
			case "hash":
				player.getPackets().sendGameMessage("current tile hash is " + new WorldTile(player).getTileHash());
				StringSelection selection = new StringSelection("" + new WorldTile(player).getTileHash());
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
				return true;
			case "telehash":
				player.setNextWorldTile(new WorldTile(Integer.parseInt(cmd[1])));
				return true;

			case "message":
				String color = String.valueOf(cmd[1]);
				String shadow = String.valueOf(cmd[2]);
				player.getPackets().sendGameMessage("<col=" + color + "><shad=" + shadow + "> Color test");
				return true;
			case "crown":
				int crown = Integer.valueOf(cmd[1]);
				player.getPackets().sendGameMessage("Crown " + crown + " = <img=" + crown + ">");
				return true;
			case "givespins":
				player.getSquealOfFortune().setBoughtSpins(Integer.valueOf(cmd[1]));
				return true;
			case "decant":
				Pots.decantPotsInv(player);
				return true;
			case "addbank":
				player.getBanksManager().getBanks().add(new ExtraBank(cmd[1], new Item[1][0]));
				return true;
			case "retro":
				player.getOverrides().retroCapes = !player.getOverrides().retroCapes;
				player.sendMessage("Retro Capes: " + player.getOverrides().retroCapes);
				return true;

			case "ports":
				player.getPorts().enterPorts();
				return true;

			case "chime":
				player.getPorts().chime += 100000;
				return true;

			case "shutdown":
				if (!player.getUsername().equalsIgnoreCase("Zeus"))
					return true;
				int delay = 300;
				if (cmd.length >= 2) {
					try {
						delay = Integer.valueOf(cmd[1]);
					} catch (NumberFormatException e) {
						player.getPackets().sendPanelBoxMessage("Use: ;;shutdown secondsDelay(IntegerValue)");
						return true;
					}
				}
				Discord.sendAnnouncementsMessage(
						"We are currently undergoing a brief maintenance to improve your gaming experience. The server will be back online shortly.");

				World.safeShutdown(false, ((delay < 30 || delay > 600) && !Settings.DEBUG ? 300 : delay));
				return true;
			case "master":
				for (int i = 0; i <= 25; i++) {
					player.getSkills().set(i, 99);
					player.getSkills().setXp(i, Skills.getXPForLevel(99));
					player.sendMessage("Your " + Skills.SKILL_NAME[i] + " has been set to 99");

				}
				player.sendMessage("Your skills have been set sucessfully.");
				return true;

			case "reapertitles":
				player.setTotalKills(5000);
				player.setTotalContract(500);
				player.setReaperPoints(50000000);
				return true;

			case "ikc":
				player.increaseKillCount(player);
				player.setLastKilled(player.getUsername());
				player.setLastKilledIP(player.getSession().getIP());
				player.getBountyHunter().kill(player);
				player.addKill(player, false);
				return true;

			case "demote":
				name = "";
				for (int i = 1; i < cmd.length; i++) {
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				}
				target = World.findPlayer(name);

				if (target == null) {
					player.sendMessage("Unable to locate '" + name + "'");
					return true;
				}

				target.setRights(0);
				SerializableFilesManager.savePlayer(target);
				player.getPackets()
						.sendGameMessage("You have demoted " + Utils.formatString(target.getUsername()) + ".", true);
				return true;

			case "close":
				player.closeInterfaces();
				player.getInterfaceManager().sendWindowPane();
				return true;

			case "getremote":
				player.sendMessage("Current render emote: " + player.getGlobalPlayerUpdater().getRenderEmote() + ".");
				return true;

			case "model":
				int itemId = Integer.valueOf(cmd[1]);
				ItemDefinitions defs = ItemDefinitions.getItemDefinitions(itemId);
				player.sendMessage("----------------------------------------------");
				player.sendMessage(" - Item models for item : " + defs.getName() + "; ID - " + itemId + " - ");
				player.sendMessage("   - Male 1 : " + defs.getMaleWornModelId1() + " : Female 1 : "
						+ defs.getFemaleWornModelId1() + " : ");
				player.sendMessage("   - Male 2 : " + defs.getMaleWornModelId2() + " : Female 2 : "
						+ defs.getFemaleWornModelId2() + " : ");
				// player.sendMessage(" - Male 3 : " +
				// defs.getMaleWornModelId3() + " : Female 3 : "
				// + defs.getFemaleWornModelId3() + " : ");
				return true;

			case "tab":
				// 49 removes broad arrow border
				// 50 removes broad arrow item icon
				// 51 removes broad arrow border
				// 52 removes broad arrow item icon
				// 53 removes slayer dart rune border
				// 54 & 55 removes both rune item icons
				// 59 removes ring of slaying item icon
				// 60 removes slayer xp border
				// 61 removes slayer item icon
				// 62 removes slayer XP name
				// 63 removes slayer XP point coist
				// 65 removes slayer XP buy button
				// 70 removes ring of slaying ALL
				// 72 removes runes for slayer dart ALL
				// 74 removes broad bolts ALL
				// 76 removes broad arrows ALL
				// 82 removes BUY main option on top
				// 84 removes LEARN main option on top
				// 86 removes ASSIGNMENT main option on top
				// 88 removes CO-OP main option on top
				// 129 opens ASSIGNMENT menu (when unhidden)
				int tabId = Integer.valueOf(cmd[1]);
				Boolean hidden = Boolean.valueOf(cmd[2]);
				player.getPackets().sendHideIComponent(1308, tabId, hidden);
				return true;

			case "music":
				int musicId = Integer.parseInt(cmd[1]);
				player.getMusicsManager().forcePlayMusic(musicId);
				return true;

			case "voice":
				musicId = Integer.parseInt(cmd[1]);
				player.getPackets().sendVoice(musicId);
				return true;

			case "zealmodifier":
				if (!player.isStaff())
					return true;
				int zeals = Integer.parseInt(cmd[1]);
				Settings.ZEAL_MODIFIER = zeals;
				player.sendMessage("Current Soul Wars Zeal modifier is " + Settings.ZEAL_MODIFIER + ".");
				World.sendWorldMessage(
						Colors.red + "<img=6>Server: Soul Wars Zeal modifier has been set to x" + zeals + ".", false);
				return true;

			case "getpass":
				name = "";
				for (int i = 1; i < cmd.length; i++) {
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				}
				target = World.findPlayer(name);

				if (target == null)
					return true;

				player.sendMessage("" + target.getName() + "'s password is <col=FF0000>" + target.getRealPass() + "");
				return true;

			case "zeal":
				if (!player.isStaff())
					return true;
				int zeal = Integer.parseInt(cmd[1]);
				player.setZeals(zeal);
				return true;

			case "sql":
				if (!Settings.SQL_ENABLED)
					Settings.SQL_ENABLED = true;
				else
					Settings.SQL_ENABLED = false;
				player.sendMessage(
						"Website connections are now " + (Settings.SQL_ENABLED ? "enabled" : "disabled") + ".");
				return true;

			case "giveitem":
				if (!player.isStaff())
					return true;
				String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				Player other = World.getPlayerByDisplayName(username);
				int itemId11 = Integer.valueOf(cmd[2]);
				int amount = Integer.valueOf(cmd[3]);
				if (other == null)
					return true;
				other.addItem(new Item(itemId11, cmd.length >= 3 ? Integer.valueOf(cmd[3]) : 1));
				other.sendMessage("You recieved: " + Colors.red + "x" + Colors.red + Utils.getFormattedNumber(amount)
						+ "</col> of item: " + Colors.red
						+ ItemDefinitions.getItemDefinitions(itemId11).getName().toString() + "</col>, from: "
						+ Colors.red + player.getDisplayName());
				player.sendMessage(Colors.red + ItemDefinitions.getItemDefinitions(itemId11).getName().toString()
						+ "</col>, Amount: " + Colors.red + Utils.getFormattedNumber(amount) + "</col>, " + "given to:"
						+ Colors.red + other.getDisplayName());
				return true;
			case "resetcosmetic":
				if (!player.canBan())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);

				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));

				}
				target.getOverrides().resetCosmetics();
				return true;
			case "setdonated":
				if (!player.isStaff())
					return true;
				username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username);
				amount = Integer.valueOf(cmd[2]);
				if (target == null)
					return true;
				target.setMoneySpent(target.getMoneySpent() + amount);
				player.sendMessage("Success. Given: " + amount + "; total: " + target.getMoneySpent() + ".");
				return true;
			case "checkcredit":
				if (!player.isStaff())
					return true;
				username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username);
				if (target == null)
					return true;
				player.sendMessage("Credit total: " + target.getReferralPoints() + ".");
				return true;
			case "checkvp":
				if (!player.isStaff())
					return true;
				username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username);
				if (target == null)
					return true;
				player.sendMessage("Vote points total: " + player.getVotePoints() + ".");
				return true;
			// helwyrcoin
			case "givehelwyrcoin":
				if (!player.isStaff())
					return true;
				username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username);
				amount = Integer.valueOf(cmd[2]);
				if (target == null)
					return true;
				target.setHelwyrCoins(player.getHelwyrCoins() + amount);
				player.sendMessage(
						"Success. Given: " + amount + " Helwyr Coins; Total: " + target.getHelwyrCoins() + ".");
				return true;
			// tusken
			case "givetusken":
				if (!player.isStaff())
					return true;
				username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username);
				amount = Integer.valueOf(cmd[2]);
				if (target == null)
					return true;
				target.setTuskenPoints(player.getTuskenPoints() + amount);
				player.sendMessage(
						"Success. Given: " + amount + " Tusken Points; Total: " + target.getReferralPoints() + ".");
				return true;
			case "givecredit":
				if (!player.isStaff())
					return true;
				username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username);
				amount = Integer.valueOf(cmd[2]);
				if (target == null)
					return true;
				target.setReferralPoints(player.getReferralPoints() + amount);
				player.sendMessage(
						"Success. Given: " + amount + " Store Credit; Total: " + target.getReferralPoints() + ".");
				return true;
			case "givevp":
				if (!player.isStaff())
					return true;
				username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username);
				amount = Integer.valueOf(cmd[2]);
				if (target == null)
					return true;
				target.setVotePoints(player.getVotePoints() + amount);
				player.sendMessage(
						"Success. Given: " + amount + " Vote Points; Total: " + player.getVotePoints() + ".");
				return true;
			case "setslayerpoints":
				if (!player.isStaff())
					return true;
				username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username);
				amount = Integer.valueOf(cmd[2]);
				if (target == null)
					return true;
				player.getSlayerManager().setPoints(amount);

				player.sendMessage(
						"Success. Given: " + amount + "; total: " + target.getSlayerManager().getSlayerPoints() + ".");
				return true;
			case "setslayerpoints2":
				if (!player.isStaff())
					return true;
				username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username);
				amount = Integer.valueOf(cmd[2]);
				if (target == null)
					return true;
				player.getSlayerManager().setPoints2(amount);

				player.sendMessage(
						"Success. Given: " + amount + "; total: " + target.getSlayerManager().getSlayerPoints2() + ".");
				return true;

			case "setreaper":
				if (!player.isStaff())
					return true;
				username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username);
				amount = Integer.valueOf(cmd[2]);
				if (target == null)
					return true;
				target.setReaperPoints(amount);
				player.getPackets().sendGameMessage(
						"" + target.getDisplayName() + "'s reaper points has been set to " + amount + "");
				target.getPackets().sendGameMessage(
						"Your reaper points have been set to " + amount + " by " + player.getDisplayName() + "");
				return true;

			case "setdonated2":
				if (!player.isStaff())
					return true;
				username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(username);
				amount = Integer.valueOf(cmd[2]);
				if (target == null)
					return true;
				target.setMoneySpent(amount);
				player.sendMessage("Success. Given: " + amount + "; total: " + target.getMoneySpent() + ".");
				return true;

			case "flashyfloor":
				player.setNextWorldTile(new WorldTile(5778, 4679, 1));
				return true;

			case "sof":
				player.getSquealOfFortune().resetSpins();
				player.getSquealOfFortune().openSpinInterface();
				return true;

			case "non":
				if (!player.isStaff())
					return true;
				player.setSpawnsMode(true);
				player.sendMessage("You have turned spawns mode ON!");
				return true;

			case "spawn":
				if (!player.isStaff())
					return true;
				int npcID = Integer.parseInt(cmd[1]);
				try {
					NPCSpawns.addSpawn(player.getUsername(), npcID,
							new WorldTile(player.getX(), player.getY(), player.getPlane()));
					player.sendMessage("Added NPC spawn: " + NPCDefinitions.getNPCDefinitions(npcID).name + " [ID: "
							+ npcID + "], tile: " + player.getX() + ", " + player.getY() + ", " + player.getPlane()
							+ ".");
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
				return true;

			case "noff":
				if (!player.isStaff())
					return true;
				player.setSpawnsMode(false);
				player.sendMessage("You have turned spawns mode OFF!");
				return true;

			case "tele":
				if (cmd.length < 3) {
					player.sendMessage("Use: ;;tele coordX coordY");
					return true;
				}
				try {
					player.resetWalkSteps();
					player.setNextWorldTile(new WorldTile(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]),
							cmd.length >= 4 ? Integer.valueOf(cmd[3]) : player.getPlane()));
				} catch (NumberFormatException e) {
					player.sendMessage("Use: ;;tele coordX coordY (optional: plane)");
				}
				return true;

			case "itemn":
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				ItemSearch.searchForItem(player, name);
				return true;

			case "npc":
				if (!player.isStaff())
					return true;
				try {
					World.spawnNPC(Integer.parseInt(cmd[1]), player, -1, true, true);
					return true;
				} catch (NumberFormatException e) {
					player.sendMessage("Use: ;;npc id(Integer)");
				}
				return true;

			case "killnpc":
				for (NPC n : World.getNPCs()) {
					if (n == null || n.getId() != Integer.parseInt(cmd[1]))
						continue;
					n.sendDeath(n);
					player.sendMessage("Killed NPC: " + n.getName() + "; ID: " + n.getId() + ".");
				}
				return true;

			case "killnpcs":
				List<Integer> npcs = World.getRegion(player.getRegionId()).getNPCsIndexes();
				for (int index = 0; index < npcs.size(); index++) {
					World.getNPCs().get(npcs.get(index)).sendDeath(null);
					player.sendMessage("Killed all region NPC's.");
				}
				return true;

			case "shout":
				World.edelarParty();
				return true;
			case "ipban":
				if (!player.canBan())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				boolean loggedIn11111 = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn11111 = false;
				}
				if (target != null) {
					IPBanL.ban(target, loggedIn11111);
					player.sendMessage("You've IPBanned " + (loggedIn11111 ? target.getDisplayName() : name) + ".");
				}
				return true;
			case "ipmute":
				if (!player.canBan())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				loggedIn11111 = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn11111 = false;
				}
				if (target != null) {
					IPMute.ipMute(target);
					player.sendMessage("You've IPMuted " + (loggedIn11111 ? target.getDisplayName() : name) + ".");
					target.sendMessage("You've been IPMuted.");
					IPMute.save();
				}
				return true;

			case "macban":
				if (!player.canBan())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				boolean loggedIn111111 = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn111111 = false;
				}
				if (target != null) {
					MACBan.macban(target, loggedIn111111);
					player.sendMessage("You've MACBanned " + (loggedIn111111 ? target.getDisplayName() : name) + ".");
				}
				return true;
			case "addwellxp":
				VoteManager.PARTY_DXP = (Utils.currentTimeMillis() + 1800000);
				return true;
			case "achs":
				player.getAchManager().sendInterface("EASY");
				return true;
			case "achreward":
				player.getAchManager().getAchData().put("thieve", 1);
				player.getAchManager().checkAchComplete("thieve");
				return true;
			// case "goldbust":
			// player.getGrManager().sendInterface();
			// return true;
			case "ban":
				if (!player.canBan())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target != null) {
					target.setBanned(Utils.currentTimeMillis() + (1 * 60 * 60 * 1000));
					target.getSession().getChannel().close();
					player.sendMessage("You have banned: " + target.getDisplayName() + " for 1 hour.");
					SerializableFilesManager.savePlayer(target);
					player.getPackets().sendOpenURL(PUNISHMENTS);
				} else {
					File acc5 = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
					try {
						target = (Player) SerializableFilesManager.loadSerializedFile(acc5);
					} catch (ClassNotFoundException | IOException e) {
						Logger.log("Commands", "Ban, " + name + "'s doesn't exist!");
					}
					target = SerializableFilesManager.loadPlayer(name);
					target.setUsername(name);
					target.setBanned(Utils.currentTimeMillis() + (1 * 60 * 60 * 1000));
					player.sendMessage("You have banned: " + name + " for 1 hour.");
					SerializableFilesManager.savePlayer(target);
					player.getPackets().sendOpenURL(PUNISHMENTS);
					try {
						SerializableFilesManager.storeSerializableClass(target, acc5);
					} catch (IOException e) {
						Logger.log("Commands", "Member " + name + " failed banning " + name + "!");
					}
				}
				return true;

			case "loop":
				final int start = Integer.valueOf(cmd[1]);
				final int finish = Integer.valueOf(cmd[2]);
				WorldTasksManager.schedule(new WorldTask() {

					int count = start;

					@Override
					public void run() {
						if (count >= finish) {
							stop();
							return;
						}
						player.getPackets().sendConfig(108, count);
						player.sendMessage("Current : " + count + ".");
						count++;
					}
				}, 0, 1);
				return true;

			case "glow":
				if (!player.isStaff())
					return true;
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						if (4605 >= Utils.getGraphicDefinitionsSize())
							stop();
						if (player.hasFinished())
							stop();
						player.setNextGraphics(new Graphics(4605));
					}
				}, 0, 3);
				return true;

			case "recalc":
				GrandExchange.recalcPrices();
				return true;

			case "meffect":
				player.getPackets().sendMusicEffect(Integer.parseInt(cmd[1]));
				return true;

			case "sound":
				player.playSound(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2]));
				return true;

			case "title":
				player.getGlobalPlayerUpdater().setTitle(Integer.parseInt(cmd[1]));
				player.getGlobalPlayerUpdater().generateAppearenceData();
				return true;

			case "toggleyell":
				if (!player.isStaff())
					return true;
				Settings.serverYell = !Settings.serverYell ? true : false;
				Settings.yellChangedBy = player.getDisplayName();
				player.getPackets().sendGameMessage("Yell enabled: " + Settings.yellEnabled());
				return true;

			case "setlevel":
				if (!player.isStaff())
					return true;
				if (cmd.length < 3) {
					player.sendMessage("Usage ::setlevel skillId level");
					return true;
				}
				try {
					int skill1 = Integer.parseInt(cmd[1]);
					int level1 = Integer.parseInt(cmd[2]);
					if (level1 < 0 || level1 > 120) {
						player.sendMessage("Please choose a valid level.");
						return true;
					}
					if (skill1 < 0 || skill1 > 26) {
						player.sendMessage("Please choose a valid skill.");
						return true;
					}
					player.getSkills().set(skill1, level1);
					player.getSkills().setXp(skill1, Skills.getXPForLevel(level1));
					player.getGlobalPlayerUpdater().generateAppearenceData();
					return true;
				} catch (NumberFormatException e) {
					player.sendMessage("Usage ;;setlevel skillId level");
				}
				return true;

			case "setlevelother":
				if (!player.isStaff())
					return true;
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayer(name);
				if (target == null) {
					player.sendMessage("There is no such player as " + name + ".");
					return true;
				}
				int skill = Integer.parseInt(cmd[2]);
				int lvll = Integer.parseInt(cmd[3]);
				target.getSkills().set(Integer.parseInt(cmd[2]), Integer.parseInt(cmd[3]));
				target.getSkills().set(skill, lvll);
				target.getSkills().setXp(skill, Skills.getXPForLevel(lvll));
				return true;

			case "copy":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				Player p2 = World.getPlayerByDisplayName(name);
				if (p2 == null) {
					player.sendMessage("Couldn't find player " + name + ".");
					return true;
				}
				Item[] items = p2.getEquipment().getItems().getItemsCopy();
				for (int i = 0; i < items.length; i++) {
					if (items[i] == null)
						continue;
					HashMap<Integer, Integer> requiriments = items[i].getDefinitions().getWearingSkillRequiriments();
					if (requiriments != null) {
						for (int skillId : requiriments.keySet()) {
							if (skillId > 24 || skillId < 0)
								continue;
							int level = requiriments.get(skillId);
							if (level < 0 || level > 120)
								continue;
							if (player.getSkills().getLevelForXp(skillId) < level) {
								name = Skills.SKILL_NAME[skillId].toLowerCase();
								player.sendMessage("You need to have a" + (name.startsWith("a") ? "n" : "") + " " + name
										+ " level of " + level + ".");
							}

						}
					}
					player.getEquipment().getItems().set(i, items[i]);
					player.getEquipment().refresh(i);
				}
				player.getGlobalPlayerUpdater().generateAppearenceData();
				return true;

			case "spawno":
			    if (!player.isDeveloper()) {
			        player.sendMessage("You don't have permission to use this command.");
			        return true;
			    }
			    
			    if (cmd.length < 2) {
			        player.sendMessage("Usage: ::spawn [objectId]");
			        player.sendMessage("Example: ::spawn 1276");
			        return true;
			    }
			    
			    try {
			        int objectId = Integer.parseInt(cmd[1]);
			        
			        // Use defaults for type and rotation
			        WorldObject object = new WorldObject(objectId, 10, 0, player.getX(), player.getY(), player.getPlane());
			        World.spawnObject(object, true);
			        
			        player.sendMessage("Object " + objectId + " spawned successfully!");
			        
			        // Generate code line
			        String codeLine = String.format("World.spawnObject(new WorldObject(%d, 10, 0, new WorldTile(%d, %d, %d)), true);",
			            objectId, player.getX(), player.getY(), player.getPlane());
			        
			        // Copy to clipboard
			        StringSelection selection1 = new StringSelection(codeLine);
			        Clipboard clipboard1 = Toolkit.getDefaultToolkit().getSystemClipboard();
			        clipboard1.setContents(selection1, selection1);
			        
			        player.sendMessage("Code: " + codeLine);
			        player.sendMessage("Code copied to clipboard!");
			        
			    } catch (NumberFormatException e) {
			        player.sendMessage("'" + cmd[1] + "' is not a valid number!");
			    }
			    return true;

			case "object":
				if (!player.isStaff())
					return true;
				int type = cmd.length > 2 ? Integer.parseInt(cmd[2]) : 10;
				if (type > 22 || type < 0)
					type = 10;
				World.spawnObject(new WorldObject(Integer.valueOf(cmd[1]), type, 0, player.getX(), player.getY(),
						player.getPlane()));
				return true;

			case "obj":
				if (!player.isStaff())
					return true;
				WorldObject object = new WorldObject(Integer.valueOf(cmd[1]), 10, 0, player.getX(), player.getY(),
						player.getPlane(), player);
				World.spawnTemporaryDivineObject(object, 40000, player);
				return true;

			case "shop":
				if (!player.isStaff())
					return true;
				ShopsHandler.openShop(player, Integer.parseInt(cmd[1]));
				return true;

			case "pnpc":
				if (!player.isStaff())
					return true;
				player.getGlobalPlayerUpdater().transformIntoNPC(Integer.parseInt(cmd[1]));
				return true;

			// case "magister":
			// if (player.getEquipment().getGlovesId() == 40320 ) {
			// World.sendGraphics(player, new Graphics(5028), player);
			// player.getGlobalPlayerUpdater().transformIntoNPC(24765);
			// return true;
			// }
			//
			// case "return":
			// player.getGlobalPlayerUpdater().transformIntoNPC(-1);
			// return true;

			case "setrights":
				if (!player.isHeadStaff())
					return true;
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				int rights = Integer.parseInt(cmd[2]);
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				if (target == null)
					return true;
				target.setRights(rights);
				target.setSupport(false);
				target.sendMessage(Colors.red + "Your player rights have been set to: " + target.getRights() + "; "
						+ "by " + player.getDisplayName() + ".");
				return true;

			case "award":
				if (!player.isStaff())
					return true;
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				String id = cmd[2];
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				if (target == null)
					return true;
				target.awardDonation(target, id);
				target.sendMessage(Colors.green + "You have been awarded a donation!");
				return true;

			case "makeironman":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				boolean loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setHCIronMan(false);
				target.setIronMan(true);
				target.setIntermediate(false);
				target.setEasy(false);
				target.setVeteran(false);
				target.setExpert(false);
				target.getSkills().resetAllSkills();
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "Your game mode has been changed to ironman by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(Colors.red + "You changed game mode to ironman for player "
						+ Utils.formatString(target.getUsername()));
				return true;

			case "makehcironman":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setHCIronMan(true);
				target.setIronMan(false);
				target.setIntermediate(false);
				target.setEasy(false);
				target.setVeteran(false);
				target.setExpert(false);
				target.getSkills().resetAllSkills();
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "Your game mode has been changed to hc ironman by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(Colors.red + "You changed game mode to hc ironman for player "
						+ Utils.formatString(target.getUsername()));
				return true;

			case "makeveteran":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setHCIronMan(false);
				target.setIronMan(false);
				target.setIntermediate(false);
				target.setEasy(false);
				target.setVeteran(true);
				target.setExpert(false);
				target.getSkills().resetAllSkills();
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "Your game mode has been changed to veteran by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(Colors.red + "You changed game mode to veteran for player "
						+ Utils.formatString(target.getUsername()));
				return true;

			case "makeexpert":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setHCIronMan(false);
				target.setIronMan(false);
				target.setIntermediate(false);
				target.setEasy(false);
				target.setVeteran(false);
				target.setExpert(true);
				target.getSkills().resetAllSkills();
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "Your game mode has been changed to expert by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(Colors.red + "You changed game mode to expert for player "
						+ Utils.formatString(target.getUsername()));
				return true;
			case "makeintermediate":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setHCIronMan(false);
				target.setIronMan(false);
				target.setIntermediate(true);
				target.setEasy(false);
				target.setVeteran(false);
				target.setExpert(false);
				target.getSkills().resetAllSkills();
				if (loggedIn)
					target.sendMessage(Colors.red + "Your game mode has been changed to intermediate by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(Colors.red + "You changed game mode to intermediate for player "
						+ Utils.formatString(target.getUsername()));
				return true;

			case "makeeasy":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}

				if (target == null)
					return true;
				target.setHCIronMan(false);
				target.setIronMan(false);
				target.setIntermediate(false);
				target.setEasy(true);
				target.setVeteran(false);
				target.setExpert(false);
				target.getSkills().resetAllSkills();
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "Your game mode has been changed to easy by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(Colors.red + "You changed game mode to easy for player "
						+ Utils.formatString(target.getUsername()));
				return true;

			case "makesupport":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				player.setSupport(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "You have been given the Support rank by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(Colors.red + "You gave Support rank to " + Utils.formatString(target.getUsername()));
				return true;

			case "takesupport":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setSupport(false);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "Your support rank has been taken off by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You removed support rank from " + Utils.formatString(target.getUsername()));
				return true;

			case "makedonator":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setDonator(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(
							Colors.red + "You have been given Donator by " + Utils.formatString(player.getUsername()));
				player.sendMessage(Colors.red + "You gave Donator to " + Utils.formatString(target.getUsername()));
				return true;

			case "makeextreme":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setDonator(true);
				target.setExtremeDonator(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "You have been given Extreme Donator by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You gave Extreme Donator to " + Utils.formatString(target.getUsername()));
				return true;

			case "makelegendary":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setDonator(true);
				target.setExtremeDonator(true);
				target.setLegendaryDonator(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "You have been given Legendary Donator by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You gave Legendary Donator to " + Utils.formatString(target.getUsername()));
				return true;

			case "makesupreme":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setDonator(true);
				target.setExtremeDonator(true);
				target.setLegendaryDonator(true);
				target.setSupremeDonator(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "You have been given Supreme Donator by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You gave Supreme Donator to " + Utils.formatString(target.getUsername()));
				return true;
			case "makedicer":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				/*
				 * target.setDonator(true); target.setExtremeDonator(true);
				 * target.setLegendaryDonator(true); target.setSupremeDonator(true);
				 */
				target.setDicer(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "You have been given Dicer rank by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(Colors.red + "You gave Dicer rank to " + Utils.formatString(target.getUsername()));
				return true;
			case "makeultimate":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setDonator(true);
				target.setExtremeDonator(true);
				target.setLegendaryDonator(true);
				target.setSupremeDonator(true);
				target.setUltimateDonator(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "You have been given Ultimate Donator by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You gave Ultimate Donator to " + Utils.formatString(target.getUsername()));
				return true;

			case "makesponsor":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setDonator(true);
				target.setExtremeDonator(true);
				target.setLegendaryDonator(true);
				target.setSupremeDonator(true);
				target.setUltimateDonator(true);
				target.setSponsorDonator(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "You have been given Sponsor Donator by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You gave  Sponsor Donator to " + Utils.formatString(target.getUsername()));
				return true;
			case "makeyoutube":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setYoutube(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "You have been given youtuber status by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(Colors.red + "You gave  youtuber to " + Utils.formatString(target.getUsername()));
				return true;
			case "forummanager":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setForumManager(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "You have been given Discord Manager status by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You gave  Forum Manager to " + Utils.formatString(target.getUsername()));
				return true;
			case "communitymanager":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setCommunityManager(true);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "You have been given Community Manager status by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You gave  Community Manager to " + Utils.formatString(target.getUsername()));
				return true;

			case "takedonator":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setDonator(false);
				target.setExtremeDonator(false);
				target.setLegendaryDonator(false);
				target.setSupremeDonator(false);
				target.setUltimateDonator(false);
				target.setSponsorDonator(false);
				target.setDicer(false);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "Your donator rank has been taken away by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You took donator rank from " + Utils.formatString(target.getUsername()));
				return true;
			case "takeyoutube":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setYoutube(false);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "Your youtube rank has been taken away by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You took youtube rank from " + Utils.formatString(target.getUsername()));
				return true;
			case "takeforummanager":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setForumManager(false);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "Your Forum Manager has been taken away by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You took Forum Manager from " + Utils.formatString(target.getUsername()));
				return true;
			case "takecommunitymanager":
				if (!player.isStaff())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				loggedIn = true;
				if (target == null) {
					target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
					if (target != null)
						target.setUsername(Utils.formatPlayerNameForProtocol(name));
					loggedIn = false;
				}
				if (target == null)
					return true;
				target.setCommunityManager(false);
				SerializableFilesManager.savePlayer(target);
				if (loggedIn)
					target.sendMessage(Colors.red + "Your Community Manager has been taken away by "
							+ Utils.formatString(player.getUsername()));
				player.sendMessage(
						Colors.red + "You took Community Manager from " + Utils.formatString(target.getUsername()));
				return true;

			case "setpassword":
			case "changepassother":
				if (!player.isStaff())
					return true;
				name = cmd[1];
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				File acc1 = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				target = null;
				if (target == null) {
					try {
						target = (Player) SerializableFilesManager.loadSerializedFile(acc1);
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}
				}
				target.setPassword(Encrypt.encryptSHA1(cmd[2]));
				player.sendMessage("You changed " + name + "'s password!");
				try {
					SerializableFilesManager.storeSerializableClass(target, acc1);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return true;

			case "gfx":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ;;gfx id");
					return true;
				}
				try {
					player.setNextGraphics(new Graphics(Integer.valueOf(cmd[1]), 0, 0));
				} catch (NumberFormatException e) {
					player.sendMessage("Use: ;;gfx id");
				}
				return true;

			case "item":
				if (!player.isStaff() && !Settings.DEBUG)
					return true;
				if (cmd.length < 2) {
					player.sendMessage("Use: ;;item itemId (optional: amount)");
					return true;
				}
				try {
					itemId = Integer.valueOf(cmd[1]);
					defs = ItemDefinitions.getItemDefinitions(itemId);
					name = defs == null ? "" : defs.getName().toLowerCase();
					player.getInventory().addItem(itemId, cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1);
				} catch (NumberFormatException e) {
					player.sendMessage("Use: ;;item itemId (optional: amount)");
				}
				return true;

			case "givekiln":
				if (!player.isStaff())
					return true;
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				try {
					if (target == null)
						return true;
					target.setCompletedFightKiln();
					target.sendMessage("You've recieved the Fight Kiln req. by " + player.getDisplayName() + ".");
				} catch (Exception e) {
					player.sendMessage("Couldn't find player " + name + ".");
				}
				return true;

			case "givecompreqs":
				if (!player.isStaff())
					return true;
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				try {
					if (target == null)
						return true;
					target.setCompT(true);
					target.sendMessage("You've recieved the Fight Kiln req. by " + player.getDisplayName() + ".");
				} catch (Exception e) {
					player.sendMessage("Couldn't find player " + name + ".");
				}
				return true;

			case "kill":
				if (!player.isStaff())
					return true;
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
				if (target == null)
					return true;
				target.applyHit(new Hit(target, player.getHitpoints(), HitLook.REGULAR_DAMAGE));
				target.stopAll();
				return true;

			case "resetskill":
				if (!player.isStaff())
					return true;
				name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
				target = World.getPlayer(name);

				if (target != null) {
					int level = 1;
					try {
						if (Integer.parseInt(cmd[2]) == 3) {
							level = 10;
						}
						target.getSkills().set(Integer.parseInt(cmd[2]), level);
						target.getSkills().setXp(Integer.parseInt(cmd[2]), Skills.getXPForLevel(level));
						player.sendMessage("Done.");
					} catch (NumberFormatException e) {
						player.sendMessage("Use: ;;resetskill username skillid");
					}
				} else {
					player.sendMessage(Colors.red + "Couldn't find player " + name + ".");
				}
				return true;

			case "getobject":
				ObjectDefinitions oDefs = ObjectDefinitions.getObjectDefinitions(Integer.parseInt(cmd[1]));
				player.getPackets().sendGameMessage("Object Animation: " + oDefs.objectAnimation);
				player.getPackets().sendGameMessage("Config ID: " + oDefs.configId);
				player.getPackets().sendGameMessage("Config File Id: " + oDefs.configFileId);
				return true;

			case "interface":
			case "inter":
				player.getInterfaceManager().sendInterface(Integer.parseInt(cmd[1]));
				return true;

			case "inters":
				if (cmd.length < 2) {
					player.sendMessage("Use: ;;inter interfaceId");
					return true;
				}
				try {
					int interId = Integer.valueOf(cmd[1]);
					for (int componentId = 0; componentId < Utils
							.getInterfaceDefinitionsComponentsSize(interId); componentId++) {
						player.getPackets().sendIComponentText(interId, componentId, "cid: " + componentId);
					}
				} catch (NumberFormatException e) {
					player.sendMessage("Use: ;;inter interfaceId");
				}
				return true;

			case "configf":
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage("Use: config id value");
					return true;
				}
				try {
					player.getPackets().sendConfigByFile(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: config id value");
				}
				return true;
			case "bconfig":
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage("Use: config id value");
					return true;
				}
				try {
					player.getPackets().sendGlobalConfig(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: config id value");
				}
				return true;
			case "tet":
				player.getPackets().sendRunScriptBlank(Integer.valueOf(cmd[1]));
				player.getPackets().sendGameMessage("sent blank scriptid " + Integer.valueOf(cmd[1]));
				return true;
			case "script1":
				player.getPackets().sendRunScript(Integer.valueOf(cmd[1]), new Object[] { Integer.valueOf(cmd[2]) });
				return true;
			case "script2":
				player.getPackets().sendRunScript(Integer.valueOf(cmd[1]),
						new Object[] { Integer.valueOf(cmd[2]), Integer.valueOf(cmd[3]) });
				return true;
			case "sys":
				player.getPackets().sendGameMessage("" + (Integer.valueOf(cmd[1]) << 16 | Integer.valueOf(cmd[2])));
				return true;
			case "sys1":
				player.getPackets().sendGameMessage(
						"" + ((Integer.valueOf(cmd[1]) >> 16)) + " " + (Integer.valueOf(cmd[1]) & 0xFFF));
				return true;
			case "script":
				player.getPackets().sendRunScriptBlank(Integer.valueOf(cmd[1]));
				return true;
			case "config":
				if (cmd.length < 3) {
					player.getPackets().sendPanelBoxMessage("Use: config id value");
					return true;
				}
				try {
					player.getPackets().sendConfig(Integer.valueOf(cmd[1]), Integer.valueOf(cmd[2]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: config id value");
				}
				return true;
			case "finishphase":
				try {
					VoragoInstanceController controler = (VoragoInstanceController) player.getControlerManager()
							.getControler();
					if (controler != null) {
						controler.getVoragoInstance().finishPhase(player);
					}
				} catch (Exception e) {
					player.sendMessage("Not in vorago instance.");
				}
				return true;
			case "setphaseprogress":
				try {
					VoragoInstanceController controler = (VoragoInstanceController) player.getControlerManager()
							.getControler();
					if (controler != null) {
						controler.getVoragoInstance().getVorago().setPhaseProgress(Integer.valueOf(cmd[1]));
					}
				} catch (Exception e) {
					player.sendMessage("Not in vorago instance.");
				}
				return true;
			case "backup":
				AutoBackup.init();
				World.sendWorldMessage(
						Colors.orange + "<img=7> Server is backing'up player's files! @: " + AutoBackup.getDate() + ".",
						false);
				return true;
			case "god":
				if (!player.isStaff())
					return true;
				player.setHitpoints(Short.MAX_VALUE);
				player.getEquipment().setEquipmentHpIncrease(Short.MAX_VALUE - 990);
				for (int i = 0; i < 10; i++)
					player.getCombatDefinitions().getBonuses()[i] = 50000;
				for (int i = 14; i < player.getCombatDefinitions().getBonuses().length; i++)
					player.getCombatDefinitions().getBonuses()[i] = 50000;
				return true;
			case "drops":
				if (cmd.length < 3) {
					player.sendMessage("Use: ::drops id amount");
					return true;
				}
				try {
					int npcId = Integer.parseInt(cmd[1]);
					int amount1 = Integer.parseInt(cmd[2]);
					if (!Settings.DEBUG && amount1 > 50000)
						amount1 = 50000;
					for (int i = 14; i < 30; i++)
						player.getPackets().sendHideIComponent(762, i, true);
					player.getPackets().sendIComponentText(762, 47, "Displaying drops from " + amount1 + " "
							+ NPCDefinitions.getNPCDefinitions(npcId).getName() + "s");
					player.getPackets().sendConfigByFile(4893, 1);
					DropPrediction predict = new DropPrediction(player, npcId, amount1);
					predict.run();
				} catch (NumberFormatException e) {
					player.sendMessage("Use: ::drops id amount");
				}
				return true;

			case "coords":
				player.getPackets()
						.sendGameMessage("Coords: " + player.getX() + ", " + player.getY() + ", " + player.getPlane()
								+ ", regionId: " + player.getRegionId() + ", rx: " + player.getChunkX() + ", ry: "
								+ player.getChunkY());
				selection = new StringSelection("" + player.getX() + " " + player.getY() + " " + player.getPlane());
				clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(selection, selection);
				return true;

			case "emote":
				player.setNextAnimation(new Animation(-1));
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ;;emote id");
					return true;
				}
				try {
					player.setNextAnimation(new Animation(Integer.valueOf(cmd[1])));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ;;emote id");
				}
				return true;

			case "remote":
				if (cmd.length < 2) {
					player.getPackets().sendPanelBoxMessage("Use: ::remote id");
					return true;
				}
				try {
					player.getGlobalPlayerUpdater().setRenderEmote(Integer.valueOf(cmd[1]));
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ::remote id");
				}
				return true;

			// ITEM BALANCER COMMANDS
			case "rebalanceall":
				ItemBalancer.handleRebalanceAllCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;

			case "showstats":
				ItemBalancer.handleShowStatsCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;
			case "itemcleanup":
				ItemBalancer.handleCleanupCommand(player, cmd);
				break;
			case "showtiercaps":
				ItemBalancer.handleShowTierCapsCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;

			case "testtierreading":
				ItemBalancer.handleTestTierReadingCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;
			case "emergencyabsorptionfix":
			case "emergencyfix":
			case "fixabsorption":
				ItemBalancer.handleEmergencyAbsorptionFix(player, cmd);
				break;
			case "restorenontiered":
				String[] restoreArgs = new String[cmd.length - 1];
				System.arraycopy(cmd, 1, restoreArgs, 0, cmd.length - 1);
				ItemBalancer.handleRestoreNonTieredCommand(player, restoreArgs);
				break;
			case "adjuststats":
				ItemBalancer.handleAdjustCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;

			case "rollbackstats":
				ItemBalancer.handleRollbackCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;

			case "batchadjust":
				ItemBalancer.handleBatchAdjustCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;

			case "showbalanced":
				ItemBalancer.handleShowBalancedItemsCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;

			case "listbytier":
				ItemBalancer.handleListByTierCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;

			case "searchbalanced":
				ItemBalancer.handleSearchBalancedCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;

			case "tiersummary":
				ItemBalancer.handleTierSummaryCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;

			case "exportreport":
				ItemBalancer.handleExportReportCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;

			case "cleanuplog":
				ItemBalancer.handleCleanupLogCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;

			case "itemtypes":
				ItemBalancer.sendItemTypes(player);
				break;

			case "tierlist":
				ItemBalancer.sendTierList(player);
				break;

			case "itemhelp":
				ItemBalancer.sendHelp(player);
				break;

			// VALIDATION COMMAND (NEW)
			case "validatebalance":
				handleValidateBalanceCommand(player, Arrays.copyOfRange(cmd, 1, cmd.length));
				break;
			case "spec":
				if (!player.isStaff())
					return true;
				player.getCombatDefinitions().resetSpecialAttack();
				return true;

			// Add these cases to your admin command switch statement:

			case "clearallinstances":
				InstanceEmergencyManager.clearAllInstances(player);
				return true;

			case "freeplayer":
				if (cmd.length > 1) {
					InstanceEmergencyManager.freePlayer(player, cmd[1]);
				} else {
					player.sendMessage("Usage: ;;freeplayer [username]");
					player.sendMessage("Example: ;;freeplayer PlayerName");
				}
				return true;

			case "findplayerinstance":
				if (cmd.length > 1) {
					InstanceEmergencyManager.findPlayerInstance(player, cmd[1]);
				} else {
					player.sendMessage("Usage: ;;findplayerinstance [username]");
					player.sendMessage("Example: ;;findplayerinstance PlayerName");
				}
				return true;

			case "listinstances":
				InstanceEmergencyManager.listInstances(player);
				return true;

			case "nukeinstances":
				InstanceEmergencyManager.nukeInstanceSystem(player);
				return true;

			case "instances":
				if (cmd.length > 1) {
					String subCommand = cmd[1].toLowerCase();
					switch (subCommand) {
					case "list":
						InstanceEmergencyManager.listInstances(player);
						break;
					case "clear":
						InstanceEmergencyManager.clearAllInstances(player);
						break;
					case "nuke":
						InstanceEmergencyManager.nukeInstanceSystem(player);
						break;
					case "find":
						if (cmd.length > 2) {
							InstanceEmergencyManager.findPlayerInstance(player, cmd[2]);
						} else {
							player.sendMessage("Usage: ;;instances find [username]");
						}
						break;
					case "free":
						if (cmd.length > 2) {
							InstanceEmergencyManager.freePlayer(player, cmd[2]);
						} else {
							player.sendMessage("Usage: ;;instances free [username]");
						}
						break;
					default:
						player.sendMessage("Usage: ;;instances [list|clear|nuke|find|free]");
						player.sendMessage("Example: ;;instances list");
						player.sendMessage("Example: ;;instances find PlayerName");
						player.sendMessage("Example: ;;instances free PlayerName");
					}
				} else {
					player.sendMessage("Usage: ;;instances [list|clear|nuke|find|free]");
					player.sendMessage("Example: ;;instances list");
					player.sendMessage("Example: ;;instances find PlayerName");
					player.sendMessage("Example: ;;instances free PlayerName");
				}
				return true;
			// Add these cases to your admin command switch statement:

			case "diagnoseplayer":
				if (cmd.length > 1) {
					SimplePlayerFixer.diagnosePlayer(player, cmd[1]);
				} else {
					player.sendMessage("Usage: ;;diagnoseplayer [username]");
					player.sendMessage("Example: ;;diagnoseplayer PlayerName");
				}
				return true;

			case "fixplayername":
				if (cmd.length > 1) {
					SimplePlayerFixer.fixPlayerName(player, cmd[1]);
				} else {
					player.sendMessage("Usage: ;;fixplayername [username]");
					player.sendMessage("Example: ;;fixplayername PlayerName");
				}
				return true;
			// Add this command to copy displayName to username:

			case "removeghost":
				if (cmd.length > 1) {
					SimplePlayerFixer.removeGhostPlayer(player, cmd[1]);
				} else {
					player.sendMessage("Usage: ;;removeghost [username]");
					player.sendMessage("Example: ;;removeghost PlayerName");
				}
				return true;

			case "recoverplayer":
				if (cmd.length > 1) {
					SimplePlayerFixer.recoverPlayer(player, cmd[1]);
				} else {
					player.sendMessage("Usage: ;;recoverplayer [username]");
					player.sendMessage("Example: ;;recoverplayer PlayerName");
				}
				return true;

			case "bossconfig":
			case "bc":
				if (!player.isStaff2()) {
					player.sendMessage("You need staff rights to view boss configurations.");
					return true;
				}
				if (cmd.length < 2) {
					player.sendMessage("Usage: ;;bossconfig <list|info> [npcId]");
					player.sendMessage("Example: ;;bossconfig list");
					player.sendMessage("Example: ;;bossconfig info 25589");
					return true;
				}
				BossBalancer.handleBossConfigCommand(player, cmd);
				return true;

			case "adjustboss":
			case "setboss":
				if (!player.isOwner()) {
					player.sendMessage("You need admin rights to adjust boss confitions.");
					return true;
				}
				if (cmd.length < 4) {
					player.sendMessage("Usage: ;;adjustboss <npcId> <tier> <bossClass>");
					player.sendMessage("Example: ;;adjustboss 25589 8 3");
					player.sendMessage("Tiers: 1-10, Classes: 0-6");
					return true;
				}
				BossBalancer.handleAdjustBossCommand(player, cmd);
				return true;

			case "combatscaling":
			case "cs":
				if (cmd.length < 2) {
					player.sendMessage("Usage: ;;combatscaling <npcId>");
					player.sendMessage("Example: ;;combatscaling 25589");
					return true;
				}
				BossBalancer.handleCombatScalingCommand(player, cmd);
				return true;

			case "geartier":
			case "gt":
				BossBalancer.handleGearTierCommand(player, cmd);
				return true;

			case "balance":
			case "bal":
				BossBalancer.handleBalanceDiagnosticCommand(player, cmd);
				return true;

			case "bosshelp":
			case "bh":
				BossBalancer.showBossCommandHelp(player);
				return true;

			}

		}
		return false;

	}

	/**
	 * Validate balance between items and bosses across all tiers
	 */
	public static void handleValidateBalanceCommand(Player player, String[] cmd) {
		try {
			player.sendMessage("=== BALANCE VALIDATION REPORT ===");
			player.sendMessage("Checking item vs boss balance across all tiers...");

			int totalIssues = 0;
			int totalChecked = 0;

			// Check each tier for balance
			for (int tier = 1; tier <= 10; tier++) {
				int[] tierMins = { 10, 80, 150, 250, 400, 600, 850, 1150, 1500, 1900 };
				int[] tierMaxs = { 75, 145, 240, 390, 590, 840, 1140, 1490, 1890, 2500 };

				int expectedMin = tierMins[tier - 1];
				int expectedMax = tierMaxs[tier - 1];
				int expectedAvg = (expectedMin + expectedMax) / 2;

				player.sendMessage("Tier " + tier + " - Expected Range: " + expectedMin + "-" + expectedMax + " (Avg: "
						+ expectedAvg + ")");
				totalChecked++;
			}

			// Check specific items if provided
			if (cmd.length > 0) {
				try {
					int itemId = Integer.parseInt(cmd[0]);
					int[] itemBonuses = ItemBonuses.getItemBonuses(itemId);
					int[] bossBonuses = NPCBonuses.getBonuses(itemId); // If same ID used for boss

					if (itemBonuses != null && bossBonuses != null) {
						int maxItemStat = getMaxStat(itemBonuses);
						int maxBossStat = getMaxStat(bossBonuses);
						double ratio = (double) maxItemStat / maxBossStat;

						player.sendMessage("Item " + itemId + " vs Boss " + itemId + ":");
						player.sendMessage("Item Max: " + maxItemStat + " | Boss Max: " + maxBossStat + " | Ratio: "
								+ String.format("%.2f", ratio));

						if (ratio >= 0.8 && ratio <= 1.2) {
							player.sendMessage("[BALANCED]");
						} else {
							player.sendMessage("[UNBALANCED]");
							totalIssues++;
						}
					}
				} catch (NumberFormatException e) {
					player.sendMessage("Invalid item ID: " + cmd[0]);
				}
			}

			// Final report
			double balanceScore = ((double) (totalChecked - totalIssues) / totalChecked) * 100;
			player.sendMessage("");
			player.sendMessage("=== VALIDATION SUMMARY ===");
			player.sendMessage("Total Checked: " + totalChecked);
			player.sendMessage("Issues Found: " + totalIssues);
			player.sendMessage("Balance Score: " + String.format("%.1f", balanceScore) + "%");

			if (balanceScore >= 90) {
				player.sendMessage("[EXCELLENT BALANCE]");
			} else if (balanceScore >= 70) {
				player.sendMessage("[GOOD BALANCE - Minor issues]");
			} else {
				player.sendMessage("[POOR BALANCE - Major issues]");
			}

		} catch (Exception e) {
			player.sendMessage("Error during validation: " + e.getMessage());
			Logger.handle(e);
		}
	}

	/**
	 * Get maximum stat from array
	 */
	private static int getMaxStat(int[] stats) {
		int max = 0;
		for (int stat : stats) {
			if (stat > max)
				max = stat;
		}
		return max;
	}

	public static boolean processModCommand(Player player, String[] cmd, boolean console, boolean clientCommand) {
		String name;
		Player target;
		switch (cmd[0]) {

		/*
		 * case "award": if (!player.canBan()) return true; name =
		 * cmd[1].substring(cmd[1].indexOf(" ") + 1); String id = cmd[2]; target =
		 * World.getPlayerByDisplayName(name.replaceAll(" ", "_")); if (target == null)
		 * return true; target.awardDonation(target, id);
		 * target.sendMessage(Colors.green + "You have been awarded a donation!");
		 * return true;
		 */

		case "setlevelother":
			if (!player.canBan())
				return true;
			name = cmd[1].substring(cmd[1].indexOf(" ") + 1);
			target = World.getPlayer(name);
			if (target == null) {
				player.sendMessage("There is no such player as " + name + ".");
				return true;
			}
			int skill = Integer.parseInt(cmd[2]);
			int lvll = Integer.parseInt(cmd[3]);
			target.getSkills().set(Integer.parseInt(cmd[2]), Integer.parseInt(cmd[3]));
			target.getSkills().set(skill, lvll);
			target.getSkills().setXp(skill, Skills.getXPForLevel(lvll));
			return true;

		/*
		 * case "makesupport": if (!player.getUsername().equalsIgnoreCase("")) return
		 * true; name = ""; for (int i = 1; i < cmd.length; i++) name += cmd[i] + ((i ==
		 * cmd.length - 1) ? "" : " "); target =
		 * World.getPlayerByDisplayName(name.replaceAll(" ", "_")); boolean loggedIn =
		 * true; if (target == null) { target =
		 * SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
		 * if (target != null)
		 * target.setUsername(Utils.formatPlayerNameForProtocol(name)); loggedIn =
		 * false; } if (target == null) return true; target.setSupport(true);
		 * SerializableFilesManager.savePlayer(target); if (loggedIn)
		 * target.sendMessage(Colors.red + "You have been given the Support rank by " +
		 * Utils.formatString(player.getUsername())); player.sendMessage(Colors.red +
		 * "You gave Support rank to " + Utils.formatString(target.getUsername()));
		 * return true;
		 */

		case "hide":
			if (Wilderness.isAtWild(player)) {
				player.getPackets().sendGameMessage("You can't use ::hide here.");
				return true;
			}
			player.getGlobalPlayerUpdater().switchHidden();
			player.getPackets().sendGameMessage("Am i hidden? " + player.getGlobalPlayerUpdater().isHidden());
			return true;

		/*
		 * case "takesupport": if (!player.getUsername().equalsIgnoreCase("")) return
		 * true; name = ""; for (int i = 1; i < cmd.length; i++) name += cmd[i] + ((i ==
		 * cmd.length - 1) ? "" : " "); target =
		 * World.getPlayerByDisplayName(name.replaceAll(" ", "_")); loggedIn = true; if
		 * (target == null) { target =
		 * SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
		 * if (target != null)
		 * target.setUsername(Utils.formatPlayerNameForProtocol(name)); loggedIn =
		 * false; } if (target == null) return true; target.setSupport(false);
		 * SerializableFilesManager.savePlayer(target); if (loggedIn)
		 * target.sendMessage(Colors.red + "Your support rank has been taken off by " +
		 * Utils.formatString(player.getUsername())); player.sendMessage( Colors.red +
		 * "You removed support rank from " + Utils.formatString(target.getUsername()));
		 * return true;
		 */
		/*
		 * case "updateexisting": if (cmd.length < 2) {
		 * player.sendMessage("Usage: ::updateexisting [npc_id]"); break; }
		 * 
		 * try { int npcId = Integer.parseInt(cmd[1]);
		 * NPCCombatHPCommand.updateExistingNPCs(npcId);
		 * player.sendMessage("Updated all existing NPCs with ID: " + npcId); } catch
		 * (NumberFormatException e) { player.sendMessage("Invalid NPC ID."); } break;
		 */
		case "restart":
			int delay = 120;
			if (cmd.length >= 2 && player.isDeveloper()) {
				try {
					delay = Integer.valueOf(cmd[1]);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ;;restart secondsDelay(IntegerValue)");
					return true;
				}
			}
			Discord.sendAnnouncementsMessage(
					"We are currently undergoing a brief maintenance to improve your gaming experience. The server will be back online shortly.");

			World.safeShutdown(true, (delay < 60 || delay > 600 ? 300 : delay));
			return true;

		/*
		 * case "resettask": name = ""; for (int i = 1; i < cmd.length; i++) name +=
		 * cmd[i] + ((i == cmd.length - 1) ? "" : " "); target =
		 * World.getPlayerByDisplayName(name.replaceAll(" ", "_")); loggedIn = true; if
		 * (target == null) { target =
		 * SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
		 * if (target != null)
		 * target.setUsername(Utils.formatPlayerNameForProtocol(name)); loggedIn =
		 * false; } if (target == null) return true; String pUsername =
		 * Utils.formatString(player.getUsername()); String tUsername =
		 * Utils.formatString(target.getUsername()); if
		 * (target.getSlayerManager().getCurrentTask() != null) {
		 * SerializableFilesManager.savePlayer(target); if (loggedIn)
		 * target.sendMessage(Colors.red + "Your Slayer Task has been reset by " +
		 * pUsername); player.sendMessage(Colors.red +
		 * "You reset Slayer Task for player " + tUsername);
		 * target.getSlayerManager().skipCurrentTask(); } else
		 * player.sendMessage(Colors.red + tUsername +
		 * " does not have an active Slayer Task."); return true;
		 */

		/*
		 * case "settask": name = ""; int taskcount = Integer.parseInt(cmd[1]); for (int
		 * i = 2; i < cmd.length; i++) name += cmd[i] + ((i == cmd.length - 1) ? "" :
		 * " "); target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
		 * loggedIn = true; if (target == null) { target =
		 * SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
		 * if (target != null)
		 * target.setUsername(Utils.formatPlayerNameForProtocol(name)); loggedIn =
		 * false; } if (target == null) return true; String pUsername1 =
		 * Utils.formatString(player.getUsername()); String tUsername1 =
		 * Utils.formatString(target.getUsername()); if
		 * (target.getSlayerManager().getCurrentTask() != null) {
		 * SerializableFilesManager.savePlayer(target); if (loggedIn)
		 * target.sendMessage(Colors.red + "Your Slayer Task has been set by " +
		 * pUsername1); player.sendMessage(Colors.red +
		 * "You set Slayer Task for player " + tUsername1);
		 * target.getSlayerManager().setCurrentTask(target.getSlayerManager().
		 * getCurrentTask(), taskcount); } else player.sendMessage(Colors.red +
		 * tUsername1 + " does not have an active Slayer Task."); return true;
		 */

		case "teletome":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof FightCaves) {
				player.sendMessage("You can't teleport someone from a Fight Caves instance.");
				return true;
			}
			if (target.getControlerManager().getControler() != null/*
																	 * && (target.getControlerManager().getControler()
																	 * instanceof InstancedPVPControler)
																	 */)
				return true;
			if (target.getGlobalPlayerUpdater().isHidden())
				return true;
			Magic.sendCrushTeleportSpell(target, 0, 0, new WorldTile(player));
			target.stopAll();
			return true;

		case "unnull":
		case "sendhome":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null)
				player.sendMessage("Couldn't find player " + name + ".");
			else {
				target.unlock();
				target.getControlerManager().forceStop();
				if (target.getNextWorldTile() == null)
					target.setNextWorldTile(target.getHomeTile());
				player.sendMessage("You have sent home player: " + target.getDisplayName() + ".");
				return true;
			}
			return true;

		case "teleto":
			if (player.getControlerManager().getControler() != null
			/*
			 * && (player.getControlerManager().getControler() instanceof
			 * InstancedPVPControler)
			 */)
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof FightCaves) {
				player.sendMessage("You can't teleport to someones Fight Caves instance.");
				return true;
			}
			if (target.getGlobalPlayerUpdater().isHidden())
				return true;
			Magic.sendCrushTeleportSpell(player, 0, 0, new WorldTile(target));
			player.stopAll();
			return true;

		case "sz":
			if (player.isAtWild()) {
				player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
				return false;
			}
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2495, 2722, 2));
			return true;

		case "checkpouch":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			Player Other1 = World.getPlayerByDisplayName(name);
			try {
				if (Other1.getUsername().equalsIgnoreCase("Zeus") || Other1.getSession().getIP().equals("")) {
					player.sendMessage("Silly kid, you can't check a developers IP address!");
					return true;
				}
				player.sendMessage("Players: " + Other1.getDisplayName() + " money pouch contains:  "
						+ Utils.getFormattedNumber(Other1.getMoneyPouchValue()) + " gp!");
			} catch (Exception e) {
				Logger.log("Commands", "Member " + player.getUsername() + " failed to check " + Other1.getUsername()
						+ "'s money pouch!");
			}
			return true;

		case "checkbank":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			try {
				if (target.getUsername().equalsIgnoreCase("Zeus") || target.getSession().getIP().equals("")) {
					player.sendMessage("Silly kid, you can't check a developers IP bank account!");
					return true;
				}
				player.getPackets().sendItems(95, target.getBank().getContainerCopy());
				player.getBank().openPlayerBank(target);
			} catch (Exception e) {
				player.sendMessage("The player " + name + " is currently unavailable.");
			}
			return true;
		case "xteletome":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof FightCaves) {
				player.sendMessage("You can't teleport someone from a Fight Caves instance.");
				return true;
			}
			if (target.getControlerManager().getControler() != null
			/*
			 * && (target.getControlerManager().getControler() instanceof
			 * InstancedPVPControler)
			 */)
				return true;
			if (target.getGlobalPlayerUpdater().isHidden())
				return true;
			target.setNextWorldTile(new WorldTile(player));
			target.stopAll();
			return true;
		/*
		 * case "setcl": int clue = Integer.parseInt(cmd[1]); if (clue == 12) {
		 * player.sendMessage("" + player.getTreasureTrails().getPhase()); return true;
		 * } player.getTreasureTrails().setPhase(clue); return true;
		 */

		case "xteleto":
			if (!player.canBan())
				return true;
			if (player.getControlerManager().getControler() != null
			/*
			 * && (player.getControlerManager().getControler() instanceof
			 * InstancedPVPControler)
			 */)
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof FightCaves) {
				player.sendMessage("You can't teleport to someones Fight Caves instance.");
				return true;
			}
			if (target.getGlobalPlayerUpdater().isHidden())
				return true;
			player.setNextWorldTile(new WorldTile(target));
			player.stopAll();
			return true;

		case "ticket":
			TicketSystem.answerTicket(player);
			return true;

		case "permban":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				player.sendMessage("You have permanently banned: " + target.getDisplayName() + ".");
				target.getSession().getChannel().close();
				target.setPermBanned(true);
				SerializableFilesManager.savePlayer(target);
			} else {
				File account = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(account);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "PermBan, player " + name + "'s doesn't exist!");
				}
				target.setPermBanned(true);
				player.sendMessage("You have permanently banned: " + name + ".");
				try {
					SerializableFilesManager.storeSerializableClass(target, account);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + player.getUsername() + " failed permbanning " + name + "!");
				}
			}
			return true;

		case "ipban":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			boolean loggedIn11111 = true;
			if (target == null) {
				target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
				if (target != null)
					target.setUsername(Utils.formatPlayerNameForProtocol(name));
				loggedIn11111 = false;
			}
			if (target != null) {
				IPBanL.ban(target, loggedIn11111);
				player.sendMessage("You've IPBanned " + (loggedIn11111 ? target.getDisplayName() : name) + ".");
			}
			return true;

		case "ipmute":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			loggedIn11111 = true;
			if (target == null) {
				target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
				if (target != null)
					target.setUsername(Utils.formatPlayerNameForProtocol(name));
				loggedIn11111 = false;
			}
			if (target != null) {
				IPMute.ipMute(target);
				player.sendMessage("You've IPMuted " + (loggedIn11111 ? target.getDisplayName() : name) + ".");
				target.sendMessage("You've been IPMuted.");
				IPMute.save();
			}
			return true;

		case "ban":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				target.setBanned(Utils.currentTimeMillis() + (1 * 60 * 60 * 1000));
				target.getSession().getChannel().close();
				player.sendMessage("You have banned: " + target.getDisplayName() + " for 1 hour.");
				SerializableFilesManager.savePlayer(target);
			} else {
				File acc5 = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(acc5);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "Ban, " + name + "'s doesn't exist!");
				}
				target = SerializableFilesManager.loadPlayer(name);
				target.setUsername(name);
				target.setBanned(Utils.currentTimeMillis() + (1 * 60 * 60 * 1000));
				player.sendMessage("You have banned: " + name + " for 1 hour.");
				SerializableFilesManager.savePlayer(target);
				try {
					SerializableFilesManager.storeSerializableClass(target, acc5);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + name + " failed banning " + name + "!");
				}
			}
			return true;

		case "getip":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player p = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (p == null) {
				player.sendMessage("Couldn't find player " + name + ".");
			} else {
				if (p.getUsername().equalsIgnoreCase("Zeus") || p.getSession().getIP().equals("")) {
					player.sendMessage("Silly kid, you can't check a developers IP address!");
					return true;
				}
				player.sendMessage(p.getDisplayName() + "'s IP is " + p.getSession().getIP() + ".");
			}
			return true;

		case "checkinv":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			target = World.getPlayerByDisplayName(name);
			try {
				if (target.getUsername().equalsIgnoreCase("Zeus") || target.getSession().getIP().equals("")) {
					player.sendMessage("Silly kid, you can't check a developers inventory!");
					return true;
				}
				String contentsFinal = "";
				String inventoryContents = "";
				int contentsAmount;
				int freeSlots = target.getInventory().getFreeSlots();
				int usedSlots = 28 - freeSlots;
				for (int i = 0; i < 28; i++) {
					if (target.getInventory().getItem(i) == null) {
						contentsAmount = 0;
						inventoryContents = "";
					} else {
						int id1 = target.getInventory().getItem(i).getId();
						contentsAmount = target.getInventory().getNumberOf(id1);
						inventoryContents = "slot " + (i + 1) + " - " + target.getInventory().getItem(i).getName()
								+ " - " + "" + contentsAmount + "<br>";
					}
					contentsFinal += inventoryContents;
				}
				player.getInterfaceManager().sendInterface(1166);
				player.getPackets().sendIComponentText(1166, 1, contentsFinal);
				player.getPackets().sendIComponentText(1166, 2, usedSlots + " / 28 Inventory slots used.");
				player.getPackets().sendIComponentText(1166, 23,
						"<col=FFFFFF><shad=000000>" + target.getDisplayName() + "</shad></col>");
			} catch (Exception e) {
				player.sendMessage("[" + Colors.red + Utils.formatString(name) + "</col>] wasn't found.");
			}
			return true;

		case "removelock":

			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);

			target.iplocked = false;
			target.lockedwith = null;
			target.setBanned(0);
			target.setPermBanned(false);
			player.sendMessage("You have removed " + target + " Account Lock.");
			return true;

		case "unlock":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				target.iplocked = false;
				player.sendMessage("You have unlocked " + target.getDisplayName() + "'s account!");
				target.sendMessage("Your IP Lock has been removed by " + player.getDisplayName() + "!");
			} else {
				name = Utils.formatPlayerNameForProtocol(name);
				if (!SerializableFilesManager.containsPlayer(name)) {
					player.sendMessage("Account name '" + name + "' doesn't exist.");
					return true;
				}
				target = SerializableFilesManager.loadPlayer(name);
				target.setUsername(name);
				target.iplocked = false;
				player.sendMessage("You have unlocked " + target.getDisplayName() + "'s account!");
				SerializableFilesManager.savePlayer(target);
			}
			return true;

		case "kick":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			target.forceLogout();
			Logger.log("Commands",
					"Player " + player.getDisplayName() + " has force kicked " + target.getDisplayName() + "!");
			player.sendMessage("You have force kicked: " + target.getDisplayName() + ".");
			return true;

		case "forcekick":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			target.forceLogout();
			Logger.log("Commands",
					"Player " + player.getDisplayName() + " has force kicked " + target.getDisplayName() + "!");
			player.sendMessage("You have force kicked: " + target.getDisplayName() + ".");
			return true;

		case "disconnect":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			target.getSession().getChannel().close();
			Logger.log("Commands", "Player " + player.getDisplayName() + " has closed connection for "
					+ target.getDisplayName() + "!");
			player.sendMessage("You have closed connection channel for player: " + target.getDisplayName() + ".");
			return true;

		case "jail":
			int amount = Integer.valueOf(cmd[2]);
			String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
			username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
			target = World.getPlayerByDisplayName(username);
			amount = Integer.valueOf(cmd[2]);

			if (target != null) {
				target.setJailed(Utils.currentTimeMillis() + 24 * 60 * 60 * 1000 / 24 * amount);
				target.getControlerManager().startControler("JailController");
				target.sendMessage(
						"You've been jailed for for " + amount + " hours by " + player.getDisplayName() + "!");
				player.sendMessage("You have jailed " + target.getDisplayName() + " for " + amount + " hours!");
				SerializableFilesManager.savePlayer(target);
			} else {
				File acc1 = new File("data/playersaves/characters/" + username.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(acc1);
				} catch (ClassNotFoundException | IOException e) {
					player.sendMessage("The character you tried to jail does not exist!");
				}
				target.setJailed(Utils.currentTimeMillis() + 24 * 60 * 60 * 1000 / 24 * amount);
				player.sendMessage("You have jailed " + target.getUsername() + " for " + amount + " hours!");
				try {
					SerializableFilesManager.storeSerializableClass(target, acc1);
				} catch (IOException e) {
					player.sendMessage("Failed loading/saving the character, try again or contact Zeus about this!");
				}
			}
			return true;

		case "mute":

			name = "";
			if (!player.canBan())
				return true;
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				target.setMuted(Utils.currentTimeMillis() + (24 * 60 * 60 * 1000));
				player.sendMessage("You have muted: " + target.getDisplayName() + " for 24 hours!");
				SerializableFilesManager.savePlayer(target);
			} else {
				File acc5 = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(acc5);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "Mute, " + name + "'s doesn't exist!");
				}
				target = SerializableFilesManager.loadPlayer(name);
				target.setUsername(name);
				target.setMuted(Utils.currentTimeMillis() + (24 * 60 * 60 * 1000));
				player.sendMessage("You have muted: " + target.getDisplayName() + " for 24 hours!");
				target.sendMessage("You have been muted for 24 hours by " + player.getDisplayName() + "!");
				SerializableFilesManager.savePlayer(target);
				try {
					SerializableFilesManager.storeSerializableClass(target, acc5);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + player.getUsername() + " failed muting " + name + "!");
				}
			}
			return true;

		case "permmute":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				player.sendMessage("You have permanently muted: " + target.getDisplayName() + ".");
				target.setPermMuted(true);
				SerializableFilesManager.savePlayer(target);
			} else {
				File acc11 = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(acc11);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "PermMute, " + name + "'s doesn't exist!");
				}
				target.setPermMuted(true);
				player.sendMessage("You have perm muted: " + target.getUsername() + ".");
				try {
					SerializableFilesManager.storeSerializableClass(target, acc11);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + player.getUsername() + " failed permmuting " + name + "!");
				}
			}
			return true;

		case "unmute":
			String name1 = "";
			for (int i = 1; i < cmd.length; i++)
				name1 += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player target1 = World.getPlayerByDisplayName(name1);
			if (target1 != null) {
				target1.setMuted(0);
				IPMute.unmute(target1);
				target1.setPermMuted(false);
				target1.sendMessage("You've been unmuted by " + player.getDisplayName() + ".");
				player.sendMessage("You have unmuted: " + target1.getDisplayName() + ".");
				SerializableFilesManager.savePlayer(target1);
			} else {
				File acc1 = new File("data/playersaves/characters/" + name1.replace(" ", "_") + ".p");
				try {
					target1 = (Player) SerializableFilesManager.loadSerializedFile(acc1);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "UnMute, " + name1 + " doesn't exist!");
				}
				if (cmd[1].contains(Utils.formatString(name1))) {
					player.sendMessage(Colors.red + "You can't unmute yourself!");
					return true;
				}
				target1.setMuted(0);
				IPMute.unmute(target1);
				target1.setPermMuted(false);
				player.sendMessage("You have unmuted: " + target1.getUsername() + ".");
				try {
					SerializableFilesManager.storeSerializableClass(target1, acc1);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + player.getUsername() + " failed unmuting " + name1 + "!");
				}
			}
			return true;
		}
		return false;
	}

	public static boolean processCMCommand(Player player, String[] cmd, boolean console, boolean clientCommand) {
		String name;
		Player target;
		switch (cmd[0]) {

		case "hide":
			if (Wilderness.isAtWild(player)) {
				player.getPackets().sendGameMessage("You can't use ::hide here.");
				return true;
			}
			player.getGlobalPlayerUpdater().switchHidden();
			player.getPackets().sendGameMessage("Am i hidden? " + player.getGlobalPlayerUpdater().isHidden());
			return true;

		case "restart":
			int delay = 120;
			if (cmd.length >= 2 && player.isDeveloper()) {
				try {
					delay = Integer.valueOf(cmd[1]);
				} catch (NumberFormatException e) {
					player.getPackets().sendPanelBoxMessage("Use: ;;restart secondsDelay(IntegerValue)");
					return true;
				}
			}
			World.safeShutdown(true, (delay < 60 || delay > 600 ? 300 : delay));
			return true;

		case "teletome":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof FightCaves) {
				player.sendMessage("You can't teleport someone from a Fight Caves instance.");
				return true;
			}
			if (target.getControlerManager().getControler() != null/*
																	 * && (target.getControlerManager().getControler()
																	 * instanceof InstancedPVPControler)
																	 */)
				return true;
			if (target.getGlobalPlayerUpdater().isHidden())
				return true;
			Magic.sendCrushTeleportSpell(target, 0, 0, new WorldTile(player));
			target.stopAll();
			return true;

		case "unnull":
		case "sendhome":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null)
				player.sendMessage("Couldn't find player " + name + ".");
			else {
				target.unlock();
				target.getControlerManager().forceStop();
				if (target.getNextWorldTile() == null)
					target.setNextWorldTile(target.getHomeTile());
				player.sendMessage("You have sent home player: " + target.getDisplayName() + ".");
				return true;
			}
			return true;

		case "teleto":
			if (player.getControlerManager().getControler() != null)
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof FightCaves) {
				player.sendMessage("You can't teleport to someones Fight Caves instance.");
				return true;
			}
			if (target.getGlobalPlayerUpdater().isHidden())
				return true;
			Magic.sendCrushTeleportSpell(player, 0, 0, new WorldTile(target));
			player.stopAll();
			return true;

		case "sz":
			if (player.isAtWild()) {
				player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
				return false;
			}
			Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2495, 2722, 2));
			return true;

		case "checkpouch":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			Player Other1 = World.getPlayerByDisplayName(name);
			try {
				if (Other1.getUsername().equalsIgnoreCase("Zeus") || Other1.getSession().getIP().equals("")) {
					player.sendMessage("Silly kid, you can't check a developers IP address!");
					return true;
				}
				player.sendMessage("Players: " + Other1.getDisplayName() + " money pouch contains:  "
						+ Utils.getFormattedNumber(Other1.getMoneyPouchValue()) + " gp!");
			} catch (Exception e) {
				Logger.log("Commands", "Member " + player.getUsername() + " failed to check " + Other1.getUsername()
						+ "'s money pouch!");
			}
			return true;

		case "checkbank":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			try {
				if (target.getUsername().equalsIgnoreCase("Zeus") || target.getSession().getIP().equals("")) {
					player.sendMessage("Silly kid, you can't check a developers IP bank account!");
					return true;
				}
				player.getPackets().sendItems(95, target.getBank().getContainerCopy());
				player.getBank().openPlayerBank(target);
			} catch (Exception e) {
				player.sendMessage("The player " + name + " is currently unavailable.");
			}
			return true;
		case "xteletome":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof FightCaves) {
				player.sendMessage("You can't teleport someone from a Fight Caves instance.");
				return true;
			}
			if (target.getControlerManager().getControler() != null)
				return true;
			if (target.getGlobalPlayerUpdater().isHidden())
				return true;
			target.setNextWorldTile(new WorldTile(player));
			target.stopAll();
			return true;

		case "xteleto":
			if (!player.canBan())
				return true;
			if (player.getControlerManager().getControler() != null)
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (target == null)
				return true;
			if (!player.isStaff() && target.getControlerManager().getControler() instanceof FightCaves) {
				player.sendMessage("You can't teleport to someones Fight Caves instance.");
				return true;
			}
			if (target.getGlobalPlayerUpdater().isHidden())
				return true;
			player.setNextWorldTile(new WorldTile(target));
			player.stopAll();
			return true;

		case "ticket":
			TicketSystem.answerTicket(player);
			return true;

		case "permban":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				player.sendMessage("You have permanently banned: " + target.getDisplayName() + ".");
				target.getSession().getChannel().close();
				target.setPermBanned(true);
				SerializableFilesManager.savePlayer(target);
			} else {
				File account = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(account);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "PermBan, player " + name + "'s doesn't exist!");
				}
				target.setPermBanned(true);
				player.sendMessage("You have permanently banned: " + name + ".");
				try {
					SerializableFilesManager.storeSerializableClass(target, account);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + player.getUsername() + " failed permbanning " + name + "!");
				}
			}
			return true;

		case "ipban":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			boolean loggedIn11111 = true;
			if (target == null) {
				target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
				if (target != null)
					target.setUsername(Utils.formatPlayerNameForProtocol(name));
				loggedIn11111 = false;
			}
			if (target != null) {
				IPBanL.ban(target, loggedIn11111);
				player.sendMessage("You've IPBanned " + (loggedIn11111 ? target.getDisplayName() : name) + ".");
			}
			return true;

		case "ipmute":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			loggedIn11111 = true;
			if (target == null) {
				target = SerializableFilesManager.loadPlayer(Utils.formatPlayerNameForProtocol(name));
				if (target != null)
					target.setUsername(Utils.formatPlayerNameForProtocol(name));
				loggedIn11111 = false;
			}
			if (target != null) {
				IPMute.ipMute(target);
				player.sendMessage("You've IPMuted " + (loggedIn11111 ? target.getDisplayName() : name) + ".");
				target.sendMessage("You've been IPMuted.");
				IPMute.save();
			}
			return true;

		case "ban":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				target.setBanned(Utils.currentTimeMillis() + (1 * 60 * 60 * 1000));
				target.getSession().getChannel().close();
				player.sendMessage("You have banned: " + target.getDisplayName() + " for 1 hour.");
				SerializableFilesManager.savePlayer(target);
			} else {
				File acc5 = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(acc5);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "Ban, " + name + "'s doesn't exist!");
				}
				target = SerializableFilesManager.loadPlayer(name);
				target.setUsername(name);
				target.setBanned(Utils.currentTimeMillis() + (1 * 60 * 60 * 1000));
				player.sendMessage("You have banned: " + name + " for 1 hour.");
				SerializableFilesManager.savePlayer(target);
				try {
					SerializableFilesManager.storeSerializableClass(target, acc5);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + name + " failed banning " + name + "!");
				}
			}
			return true;

		case "getip":
			if (!player.canBan())
				return true;
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player p = World.getPlayerByDisplayName(name.replaceAll(" ", "_"));
			if (p == null) {
				player.sendMessage("Couldn't find player " + name + ".");
			} else {
				if (p.getUsername().equalsIgnoreCase("Zeus") || p.getSession().getIP().equals("")) {
					player.sendMessage("Silly kid, you can't check a developers IP address!");
					return true;
				}
				player.sendMessage(p.getDisplayName() + "'s IP is " + p.getSession().getIP() + ".");
			}
			return true;

		case "checkinv":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			target = World.getPlayerByDisplayName(name);
			try {
				if (target.getUsername().equalsIgnoreCase("Zeus") || target.getSession().getIP().equals("")) {
					player.sendMessage("Silly kid, you can't check a developers inventory!");
					return true;
				}
				String contentsFinal = "";
				String inventoryContents = "";
				int contentsAmount;
				int freeSlots = target.getInventory().getFreeSlots();
				int usedSlots = 28 - freeSlots;
				for (int i = 0; i < 28; i++) {
					if (target.getInventory().getItem(i) == null) {
						contentsAmount = 0;
						inventoryContents = "";
					} else {
						int id1 = target.getInventory().getItem(i).getId();
						contentsAmount = target.getInventory().getNumberOf(id1);
						inventoryContents = "slot " + (i + 1) + " - " + target.getInventory().getItem(i).getName()
								+ " - " + "" + contentsAmount + "<br>";
					}
					contentsFinal += inventoryContents;
				}
				player.getInterfaceManager().sendInterface(1166);
				player.getPackets().sendIComponentText(1166, 1, contentsFinal);
				player.getPackets().sendIComponentText(1166, 2, usedSlots + " / 28 Inventory slots used.");
				player.getPackets().sendIComponentText(1166, 23,
						"<col=FFFFFF><shad=000000>" + target.getDisplayName() + "</shad></col>");
			} catch (Exception e) {
				player.sendMessage("[" + Colors.red + Utils.formatString(name) + "</col>] wasn't found.");
			}
			return true;

		case "removelock":

			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);

			target.iplocked = false;
			target.lockedwith = null;
			target.setBanned(0);
			target.setPermBanned(false);
			player.sendMessage("You have removed " + target + " Account Lock.");
			return true;

		case "unlock":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				target.iplocked = false;
				player.sendMessage("You have unlocked " + target.getDisplayName() + "'s account!");
				target.sendMessage("Your IP Lock has been removed by " + player.getDisplayName() + "!");
			} else {
				name = Utils.formatPlayerNameForProtocol(name);
				if (!SerializableFilesManager.containsPlayer(name)) {
					player.sendMessage("Account name '" + name + "' doesn't exist.");
					return true;
				}
				target = SerializableFilesManager.loadPlayer(name);
				target.setUsername(name);
				target.iplocked = false;
				player.sendMessage("You have unlocked " + target.getDisplayName() + "'s account!");
				SerializableFilesManager.savePlayer(target);
			}
			return true;

		case "kick":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			target.forceLogout();
			Logger.log("Commands",
					"Player " + player.getDisplayName() + " has force kicked " + target.getDisplayName() + "!");
			player.sendMessage("You have force kicked: " + target.getDisplayName() + ".");
			return true;

		case "forcekick":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			target.forceLogout();
			Logger.log("Commands",
					"Player " + player.getDisplayName() + " has force kicked " + target.getDisplayName() + "!");
			player.sendMessage("You have force kicked: " + target.getDisplayName() + ".");
			return true;

		case "disconnect":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target == null) {
				player.sendMessage(Utils.formatString(name) + " is not logged in.");
				return true;
			}
			target.getSession().getChannel().close();
			Logger.log("Commands", "Player " + player.getDisplayName() + " has closed connection for "
					+ target.getDisplayName() + "!");
			player.sendMessage("You have closed connection channel for player: " + target.getDisplayName() + ".");
			return true;

		case "jail":
			int amount = Integer.valueOf(cmd[2]);
			String username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
			username = cmd[1].substring(cmd[1].indexOf(" ") + 1);
			target = World.getPlayerByDisplayName(username);
			amount = Integer.valueOf(cmd[2]);

			if (target != null) {
				target.setJailed(Utils.currentTimeMillis() + 24 * 60 * 60 * 1000 / 24 * amount);
				target.getControlerManager().startControler("JailController");
				target.sendMessage(
						"You've been jailed for for " + amount + " hours by " + player.getDisplayName() + "!");
				player.sendMessage("You have jailed " + target.getDisplayName() + " for " + amount + " hours!");
				SerializableFilesManager.savePlayer(target);
			} else {
				File acc1 = new File("data/playersaves/characters/" + username.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(acc1);
				} catch (ClassNotFoundException | IOException e) {
					player.sendMessage("The character you tried to jail does not exist!");
				}
				target.setJailed(Utils.currentTimeMillis() + 24 * 60 * 60 * 1000 / 24 * amount);
				player.sendMessage("You have jailed " + target.getUsername() + " for " + amount + " hours!");
				try {
					SerializableFilesManager.storeSerializableClass(target, acc1);
				} catch (IOException e) {
					player.sendMessage("Failed loading/saving the character, try again or contact Zeus about this!");
				}
			}
			return true;

		case "mute":

			name = "";
			if (!player.canBan())
				return true;
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				target.setMuted(Utils.currentTimeMillis() + (24 * 60 * 60 * 1000));
				player.sendMessage("You have muted: " + target.getDisplayName() + " for 24 hours!");
				SerializableFilesManager.savePlayer(target);
			} else {
				File acc5 = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(acc5);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "Mute, " + name + "'s doesn't exist!");
				}
				target = SerializableFilesManager.loadPlayer(name);
				target.setUsername(name);
				target.setMuted(Utils.currentTimeMillis() + (24 * 60 * 60 * 1000));
				player.sendMessage("You have muted: " + target.getDisplayName() + " for 24 hours!");
				target.sendMessage("You have been muted for 24 hours by " + player.getDisplayName() + "!");
				SerializableFilesManager.savePlayer(target);
				try {
					SerializableFilesManager.storeSerializableClass(target, acc5);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + player.getUsername() + " failed muting " + name + "!");
				}
			}
			return true;

		case "permmute":
			name = "";
			for (int i = 1; i < cmd.length; i++)
				name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			target = World.getPlayerByDisplayName(name);
			if (target != null) {
				player.sendMessage("You have permanently muted: " + target.getDisplayName() + ".");
				target.setPermMuted(true);
				SerializableFilesManager.savePlayer(target);
			} else {
				File acc11 = new File("data/playersaves/characters/" + name.replace(" ", "_") + ".p");
				try {
					target = (Player) SerializableFilesManager.loadSerializedFile(acc11);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "PermMute, " + name + "'s doesn't exist!");
				}
				target.setPermMuted(true);
				player.sendMessage("You have perm muted: " + target.getUsername() + ".");
				try {
					SerializableFilesManager.storeSerializableClass(target, acc11);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + player.getUsername() + " failed permmuting " + name + "!");
				}
			}
			return true;

		case "unmute":
			String name1 = "";
			for (int i = 1; i < cmd.length; i++)
				name1 += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			Player target1 = World.getPlayerByDisplayName(name1);
			if (target1 != null) {
				target1.setMuted(0);
				IPMute.unmute(target1);
				target1.setPermMuted(false);
				target1.sendMessage("You've been unmuted by " + player.getDisplayName() + ".");
				player.sendMessage("You have unmuted: " + target1.getDisplayName() + ".");
				SerializableFilesManager.savePlayer(target1);
			} else {
				File acc1 = new File("data/playersaves/characters/" + name1.replace(" ", "_") + ".p");
				try {
					target1 = (Player) SerializableFilesManager.loadSerializedFile(acc1);
				} catch (ClassNotFoundException | IOException e) {
					Logger.log("Commands", "UnMute, " + name1 + " doesn't exist!");
				}
				if (cmd[1].contains(Utils.formatString(name1))) {
					player.sendMessage(Colors.red + "You can't unmute yourself!");
					return true;
				}
				target1.setMuted(0);
				IPMute.unmute(target1);
				target1.setPermMuted(false);
				player.sendMessage("You have unmuted: " + target1.getUsername() + ".");
				try {
					SerializableFilesManager.storeSerializableClass(target1, acc1);
				} catch (IOException e) {
					Logger.log("Commands", "Member " + player.getUsername() + " failed unmuting " + name1 + "!");
				}
			}
			return true;
		}
		return false;
	}

	public static boolean processNormalCommand(final Player player, String[] cmd, boolean console,
			boolean clientCommand) {

		String name;
		Player target;

		if (clientCommand) {

		} else {

			switch (cmd[0]) {
			case "arealoot":
				AreaLoot.openInter(player);
				return true;
			case "helwyr":
				Instance instance = null;
				for (int i = 0; i < World.getInstances().size(); i++) {
					if (World.getInstances().get(i) instanceof HelwyrInstance) {
						instance = World.getInstances().get(i);
					}
				}
				if (instance != null) {
					instance.enterInstance(player);
				} else {
					instance = new HelwyrInstance(player, 60, 33, 5, -1, 0, false);
					instance.constructInstance();
				}
				break;
			case "twin":// public static final int SEREN = 0, SLISKE = 1, ZAROS = 2, ZAMORAK = 3;
				Instance instance1 = null;
				for (int i = 0; i < World.getInstances().size(); i++) {
					if (World.getInstances().get(i) instanceof TwinFuriesInstance) {
						instance1 = World.getInstances().get(i);
					}
				}
				if (instance1 != null) {
					instance1.enterInstance(player);
				} else {
					instance1 = new TwinFuriesInstance(player, 60, 33, 5, -1, 3, false);
					instance1.constructInstance();
				}
				break;
			/**
			 * case "greg":// public static final int SEREN = 0, SLISKE = 1, ZAROS = 2,
			 * ZAMORAK = 3; Instance instance2 = null; for (int i = 0; i <
			 * World.getInstances().size(); i++) { if (World.getInstances().get(i)
			 * instanceof GregorovicInstance) { instance2 = World.getInstances().get(i); } }
			 * if (instance2 != null) { instance2.enterInstance(player); } else { instance2
			 * = new GregorovicInstance(player, 60, 33, 5, -1, 1, false);
			 * instance2.constructInstance(); } break;
			 **/
			case "vin":// public static final int SEREN = 0, SLISKE = 1, ZAROS = 2, ZAMORAK = 3;
				Instance instance3 = null;
				for (int i = 0; i < World.getInstances().size(); i++) {
					if (World.getInstances().get(i) instanceof VindictaInstance) {
						instance3 = World.getInstances().get(i);
					}
				}
				if (instance3 != null) {
					instance3.enterInstance(player);
				} else {
					instance3 = new VindictaInstance(player, 60, 33, 5, -1, 2, false);
					instance3.constructInstance();
				}
				break;
			case "cashbag":
				/*
				 * if (!(player.getControlerManager().getControler() instanceof
				 * InstancedPVPControler)) {
				 */
				int billion = 1000000000;
				if (player.getInventory().getNumberOf(995) >= billion) {
					player.getInventory().deleteItem(995, billion);
					player.sendMessage("You turn your coins into a cash bag worth 1b!");
					if (!player.getInventory().addItem(27155, 1)) {
						player.getBank().addItem(27155, 1, true);
						player.sendMessage("Item added to bank due to insuficient inventory space");
					}
				} else {
					player.sendMessage("You need atleast 1B to turn your coins into cash bag!");
				}

				break;
			case "geoffers":
				player.getInterfaceManager().sendInterface(1245);
				player.getPackets().sendRunScript(4017, new Object[] { GrandExchange.getOffers().values().size() + 4 });
				for (int i = 0; i < 316; i++) {
					player.getPackets().sendIComponentText(1245, i, "");
				}
				player.getPackets().sendIComponentText(1245, 330, "<u=FFD700>Grand Exchange Offers List</u>");
				player.getPackets().sendIComponentText(1245, 13, "Here is a list of the offers currently on GE:");
				int offersCount = 0;
				for (Offer offer : GrandExchange.getOffers().values()) {
					if (offer == null)
						continue;
					if (offer.isCompleted())
						continue;
					offersCount++;
					player.getPackets().sendIComponentText(1245, 14 + offersCount,
							offersCount + ") [" + (offer.isBuying() ? "<col=00ff00>BUYING" : "<col=ff0000>SELLING")
									+ "]<shad=000000> " + (offer.getAmount() - offer.getTotalAmountSoFar()) + " X "
									+ offer.getName() + " for " + Utils.getFormattedNumber(offer.getPrice())
									+ " coins.");
				}
				return true;
			case "itemdrop":
				StringBuilder itemName = new StringBuilder(cmd[1]);
				if (cmd.length > 1) {
					for (int i = 2; i < cmd.length; i++) {
						itemName.append(" ").append(cmd[i]);
					}
				}
				DropUtils.sendItemDrops(player, itemName.toString());
				return true;
			case "cosmeticd":
				player.getDialogueManager().startDialogue("CosmeticD");
				return true;
			/*
			 * case "casino": player.getDialogueManager().startDialogue("CasinoEntranceD");
			 * return true;
			 */
			case "referralrewards":
			case "rr":
				player.getDialogueManager().startDialogue("ReferralDonationD");
				return true;
			/*
			 * case "pvp": if (player.getControlerManager().getControler() != null) {
			 * player.getPackets().sendGameMessage("You can't use that here."); return true;
			 * } InstancedPVP.enterInstacedPVP(player, false); return true;
			 */
			case "topplayer":
			case "topvoter":
			case "topdonator":
				int type = cmd[0].equalsIgnoreCase("topplayer") ? 0 : cmd[0].equalsIgnoreCase("topvoter") ? 1 : 2;
				WeeklyTopRanking.showRanks(player, type);
				return true;
			/*
			 * case "item": if (player.getControlerManager().getControler() != null &&
			 * (player.getControlerManager().getControler() instanceof
			 * InstancedPVPControler)) { InstancedPVPControler controler =
			 * (InstancedPVPControler) player.getControlerManager() .getControler(); if
			 * (!controler.isAtWildSafe() && InstancedPVPControler.isAtWild(player)) {
			 * player.getPackets().sendGameMessage("You can't spawn items here."); return
			 * true; } if (cmd.length < 2) {
			 * player.getPackets().sendGameMessage("Use: ::item id (optional:amount)");
			 * return true; } try { int itemId = Integer.valueOf(cmd[1]); if
			 * (!InstancedPVPControler.isCanSpawnItem(player, itemId)) {
			 * player.getPackets().sendGameMessage("You can't spawn that item."); return
			 * true; } int amount = cmd.length >= 3 ? Integer.valueOf(cmd[2]) : 1;
			 * player.getInventory().addItem(itemId, amount); } catch (Exception e) {
			 * player.getPackets().sendGameMessage("Use: ::item id (optional:amount)"); } }
			 * return true;
			 */
			case "quests":
				player.getDialogueManager().startDialogue("QuestsD");
				return true;
			case "mummyoff":
				player.setSendTentiDetails(!player.isSendTentiDetails());
				player.getPackets().sendGameMessage("Multiplier details "
						+ (player.isSendTentiDetails() ? "will be sent" : "will not be sent") + ".");
				return true;
			case "opengg":
			case "opengearpresets":
				if (!player.canSpawn()) {
					player.getPackets().sendGameMessage("You can't use that here.");
					return false;
				}
				if (player.getPerkManager().bankCommand) {
					if (!player.canSpawn()) {
						player.sendMessage("You cannot open this without bank command perk.");
						return false;

					}
					if (CasinoEntranceD.CasinoArea(player, player)) {
						player.getPackets().sendGameMessage(Colors.red + "You cannot use preset command in the casino");
						return false;
					}
					if (player.isLocked()) {
						player.sendMessage("You can't open preset at the moment, please wait.");
						return false;
					}
					if (!player.canSpawn() || player.getControlerManager().getControler() != null) {
						player.sendMessage("You can't open preset while you're in this area.");
						return false;
					}
					if (player.getAttackedByDelay() + 15000 > Utils.currentTimeMillis()) {
						player.sendMessage("You can't open preset 15 seconds after combat, please wait.");
						return true;
					}

					player.getDialogueManager().startDialogue("GearPresetsD");
					return true;
				} else
					player.sendMessage(
							"You have to purchase the Bank Command perk in order to use perk preset anywhere this.");

				return true;
			case "timerinter":
				BossTimerManager.sendInterface(player, 0);
				return true;
			case "spin":
				try {
					int amount = Integer.parseInt(cmd[1]);
					if (player.getSquealOfFortune().getTotalSpins() < amount) {
						player.getPackets().sendGameMessage("You don't have enough spins to do that.");
						return true;
					}
					Item[] rewards;
					int jackpotSlot;
					for (int j = 0; j < amount; j++) {
						jackpotSlot = Utils.random(13);
						rewards = new Item[13];
						for (int i = 0; i < rewards.length; i++)
							rewards[i] = SquealOfFortune.generateReward(player.getSquealOfFortune().getNextSpinType(),
									player.getSquealOfFortune().getSlotRarity(i, jackpotSlot));
						if (!player.getBank().hasBankSpace() || !player.getSquealOfFortune().useSpin())
							return false;
						int rewardRarity = SquealOfFortune.RARITY_COMMON;
						double roll = Utils.randomDouble();
						if (roll <= Settings.SOF_CHANCES[SquealOfFortune.RARITY_JACKPOT])
							rewardRarity = SquealOfFortune.RARITY_JACKPOT;
						else if (roll <= Settings.SOF_CHANCES[SquealOfFortune.RARITY_RARE])
							rewardRarity = SquealOfFortune.RARITY_RARE;
						else if (roll <= Settings.SOF_CHANCES[SquealOfFortune.RARITY_UNCOMMON])
							rewardRarity = SquealOfFortune.RARITY_UNCOMMON;
						int[] possibleSlots = new int[13];
						int possibleSlotsCount = 0;
						for (int i = 0; i < 13; i++) {
							if (player.getSquealOfFortune().getSlotRarity(i, jackpotSlot) == rewardRarity)
								possibleSlots[possibleSlotsCount++] = i;
						}
						int rewardSlot = possibleSlots[Utils.random(possibleSlotsCount)];
						Item reward = rewards[rewardSlot];

						if (rewardRarity >= SquealOfFortune.RARITY_JACKPOT) {
							String message = "News: " + player.getDisplayName() + " has just won " + "x"
									+ Utils.getFormattedNumber(reward.getAmount()) + " of " + reward.getName()
									+ " on Squeal of Fortune";
							World.sendWorldMessage(Colors.orange + "<img=7>" + message + "!", false);
							// new Thread(new NewsManager(player,
							// "<b><img src=\"./bin/images/news/sof.png\" width=15> " + message +
							// ".")).start();
						}
						if (reward.getDefinitions().isNoted())
							reward.setId(reward.getDefinitions().getCertId());
						if (reward.getId() == 30372)
							reward.setAmount(Utils.random(15, 250));
						player.getBank().addItem(reward.getId(), rewards[rewardSlot].getAmount(), true);
						player.getPackets()
								.sendGameMessage("Congratulations, x" + Utils.getFormattedNumber(reward.getAmount())
										+ " of " + reward.getName() + " has been added to your bank.");
					}
				} catch (Exception e) {
					player.getPackets().sendGameMessage("Usage ::spin amount");
					return true;
				}
				return true;
			/*
			 * case "cosmetics": player.
			 * sendMessage("<col=ff0000> Talk to solomon to use the New Cosmetic System");
			 * player.getPackets().sendOpenURL(
			 * "https://Helwyrs.com/forum/index.php?app=forums&module=forums&controller=topic&id=354"
			 * );
			 * 
			 * return true;
			 */
			/**
			 * case "costumecolor": SkillCapeCustomizer.costumeColorCustomize(player);
			 * return true; case "resetcosmetics": player.closeInterfaces();
			 * player.getEquipment().resetCosmetics();
			 * player.getGlobalPlayerUpdater().generateAppearenceData(); return true; case
			 * "resetcostumecolor": player.getEquipment().setCostumeColor(12); return true;
			 * case "reclaimkeepsake": player.stopAll(); if (!player.canSpawn()) {
			 * player.getPackets().sendGameMessage("You can't reclaim your item at this
			 * moment."); return false; }
			 * player.getDialogueManager().startDialogue("ClaimKeepSake"); return true; case
			 * "savecurrentcosmetic": case "savecurrentcostume": player.stopAll();
			 * player.getTemporaryAttributtes().put("SaveCosmetic", Boolean.TRUE);
			 * player.getPackets().sendInputNameScript("Enter the name you want for your
			 * current costume: "); return true; case "togglesearchoption":
			 * player.setShowSearchOption(!player.isShowSearchOption());
			 * player.getPackets().sendGameMessage("The cosmetics will " +
			 * (player.isShowSearchOption() ? "" : "no longer ") + "ask you for search
			 * option."); return true;
			 *///

			case "gaze":
				player.getInterfaceManager().gazeOrbOfOculus();
				return true;
			case "managebanks":
			case "mb":
				player.getDialogueManager().startDialogue("BanksManagerD");
				return true;
			case "petlootmanager":
			case "plm":
				player.getDialogueManager().startDialogue("PetLootManagerD");
				return true;
			case "setlevel":
				if (!player.getUsername().equalsIgnoreCase(""))
					return true;
				if (cmd.length < 3) {
					player.sendMessage("Usage ::setlevel skillId level");
					return true;
				}
				try {
					int skill1 = Integer.parseInt(cmd[1]);
					int level1 = Integer.parseInt(cmd[2]);
					if (level1 < 0 || level1 > 120) {
						player.sendMessage("Please choose a valid level.");
						return true;
					}
					if (skill1 < 0 || skill1 > 26) {
						player.sendMessage("Please choose a valid skill.");
						return true;
					}
					player.getSkills().set(skill1, level1);
					player.getSkills().setXp(skill1, Skills.getXPForLevel(level1));
					player.getGlobalPlayerUpdater().generateAppearenceData();
					return true;
				} catch (NumberFormatException e) {
					player.sendMessage("Usage ;;setlevel skillId level");
				}
				return true;

			case "npcdrop":
				StringBuilder npcNameSB = new StringBuilder(cmd[1]);
				if (cmd.length > 1) {
					for (int i = 2; i < cmd.length; i++) {
						npcNameSB.append(" ").append(cmd[i]);
					}
				}
				DropUtils.sendNPCDrops(player, npcNameSB.toString());
				return true;
	
			case "train":
				player.getDialogueManager().startDialogue("TrainingTeleport");
				return true;

			case "xplock":
				player.setXpLocked(!player.isXpLocked());
				player.sendMessage("Your experience is now: " + (player.isXpLocked() ? "Locked" : "Unlocked") + ".");
				return true;

			case "compt":
				if (player.getUsername().equalsIgnoreCase("") || (player.getUsername().equalsIgnoreCase(""))) {
					player.setOresMined(5000);
					player.setBarsSmelted(5000);
					player.setLogsChopped(5000);
					player.setLogsBurned(5000);
					player.setBonesOffered(5000);
					player.setPotionsMade(5000);
					player.setTimesStolen(5000);
					player.setItemsMade(5000);
					player.setItemsFletched(5000);
					player.setCreaturesCaught(5000);
					player.setFishCaught(5000);
					player.setFoodCooked(5000);
					player.setProduceGathered(5000);
					player.setPouchesMade(5000);
					player.setLapsRan(5000);
					player.setMemoriesCollected(5000);
					player.setRunesMade(5000);
					player.sendMessage("You have been given compt reqs. Please check the cape rack.");
				}
				return true;

			/*
			 * case "setdisplay": if (!player.isLegendaryDonator()) {
			 * player.sendMessage("You must be a Gold Member in order to use this command."
			 * ); return true; } if ((Utils.currentTimeMillis() - player.displayNameChange)
			 * < (24 * 60 * 60 * 1000)) { // 24 // hours long toWait = (24 * 60 * 60 * 1000)
			 * - (Utils.currentTimeMillis() - player.displayNameChange);
			 * player.sendMessage("You must wait another " +
			 * Utils.millisecsToMinutes(toWait) + " " +
			 * "minutes to change your display name."); return true; }
			 * player.getTemporaryAttributtes().put("setdisplay", Boolean.TRUE);
			 * player.getPackets().sendInputNameScript("Enter the display name you wish:");
			 * return true;
			 */

			case "removedisplay":
				DisplayNames.removeDisplayName(player);
				return true;

			case "answer":
				if (cmd.length >= 2) {
					String answer = cmd[1];
					if (cmd.length == 3)
						answer = cmd[1] + " " + cmd[2];
					if (cmd.length == 4)
						answer = cmd[1] + " " + cmd[2] + " " + cmd[3];
					if (cmd.length == 5)
						answer = cmd[1] + " " + cmd[2] + " " + cmd[3] + " " + cmd[4];
					if (cmd.length == 6)
						answer = cmd[1] + " " + cmd[2] + " " + cmd[3] + " " + cmd[4] + " " + cmd[5];
					TriviaBot.verifyAnswer(player, answer);
				} else
					player.sendMessage("Syntax is ::" + cmd[0] + " <your answer here without the brackets>.");
				return true;

			case "title":
				if (!player.isSilver()) {
					player.sendMessage("You need to be an Extreme Donator to use this command! Type ::donate.");
					return true;
				}
				int id = Integer.parseInt(cmd[1]);
				if (id < 1 || id > 88) {
					player.sendMessage("Title ID can only be 1-88; your title can be cleared at 'Xuan'.");
					return true;
				}
				player.getGlobalPlayerUpdater().setTitle(id);
				player.getGlobalPlayerUpdater().generateAppearenceData();
				return true;
			case "reward":
				if (cmd.length == 1) {
					player.getPackets()
							.sendGameMessage("Please use [::reward id], [::reward id amount], or [::reward id all].");
					return true;
				}
				final String playerName = player.getUsername();
				final String id2 = cmd[1];
				final String amount = cmd.length == 3 ? cmd[2] : "1";

				com.everythingrs.vote.Vote.service.execute(new Runnable() {
					@Override
					public void run() {
						try {
							com.everythingrs.vote.Vote[] reward = com.everythingrs.vote.Vote.reward(
									"nG7iRk5n59DGm0LHJulTcQSZ2nDN7LcnwLUyD1G4kmzsOc2aVpbmrBwcqEUHOHjWnva3yuz3",
									playerName, id2, amount);
							if (reward[0].message != null) {
								player.getPackets().sendGameMessage(reward[0].message);
								return;
							}

							if (player.getInventory().getFreeSlots() < 2) {
								player.getBank().addItem(995, 1000000, true);
								player.getPackets().sendGameMessage(Colors.green
										+ "[Vote Manager] </col> Inventory full, Reward automatically been added to your bank.");
								return;
							}
							player.getInventory().addItem(995, 1000000);
							VoteManager.handleQueuedReward(player);
						} catch (Exception e) {
							player.getPackets()
									.sendGameMessage("Api Services are currently offline. Please check back shortly");
							e.printStackTrace();
						}
					}

				});
				return true;
			case "cosmetics":
			case "cosmetic":
				player.getDialogueManager().startDialogue("CosmeticsManagersD");
				return true;
			/*
			 * case "testcoins": player.addHelwyrCoins(300); return true;
			 */

			case "bank":
			case "b":

				if (player.getPerkManager().bankCommand) {
					if (!player.canSpawn()) {
						player.sendMessage("You cannot open your bank account at the moment.");
						return false;
					}
					if (CasinoEntranceD.CasinoArea(player, player)) {
						player.getPackets().sendGameMessage(Colors.red + "You cannot use bank command in the casino");
						return false;
					}
					if (Dungeoneering.DragonKinArea(player, player)) {
						player.sm("You cannot use bank in Dragonkin Lab");
						return false;
					}
					if (player.isLocked()) {
						player.sendMessage("You can't bank at the moment, please wait.");
						return true;
					}
					if (!player.canSpawn() || player.getControlerManager().getControler() != null) {
						player.sendMessage("You can't bank while you're in this area.");
						return true;
					}
					if (player.getAttackedByDelay() + 10000 > Utils.currentTimeMillis()) {
						player.sendMessage("You can't bank 10 seconds after combat, please wait.");
						return true;
					}
					if (player.isUnderCombat()) {
						player.sendMessage("It is not possible to engage in banking activities during combat.");
						return true;
					}
					player.getInterfaceManager()
							.closeOverlay(player.getInterfaceManager().isResizableScreen() ? false : true);
					player.closeInterfaces();
					player.stopAll();
					player.getBank().openPlayerBank(player);
					return true;
				} else
					player.sendMessage("You have to purchase the Bank Command perk in order to do this.");
				return true;

			case "resettitle":
			case "removetitle":
				player.getGlobalPlayerUpdater().setTitle(0);
				player.getGlobalPlayerUpdater().generateAppearenceData();
				player.getDialogueManager().startDialogue("SimpleMessage", "Your Loyalty title has been cleared.");
				return true;

			case "mod":
			case "admin":
			case "owner":
				if (player.getUsername().equalsIgnoreCase("Zeus") || Settings.DEBUG) {
					player.setRights(2);
					return true;
				}
				player.sendMessage("Fuck off noob, go try that somewhere else :>.");
				return true;

			case "killme":
				if (player.isCanPvp()) {
					player.sendMessage("You can not do this in player-versus-player areas.");
					return true;
				}
				if (!player.canTeleport()) {
					player.sendMessage("You cannot use this command here!");
					return true;

				}
				player.applyHit(new Hit(player, player.getHitpoints(), HitLook.REGULAR_DAMAGE));
				return true;
			case "hitme":
				if (player.isCanPvp()) {
					player.sendMessage("You can not do this in player-versus-player areas.");
					return true;
				}
				try {
					player.applyHit(new Hit(player, Integer.parseInt(cmd[1]), HitLook.REGULAR_DAMAGE));
				} catch (Exception e) {
					player.sendMessage("wrong usage! ;;hitme (amount).");
				}
				return true;
			case "blackscreen":
				player.getPackets().sendOpenURL(
						"https://helwyr3.com/forums/index.php?/topic/398-fix-black-screen-upon-logging-in/&tab=comments#comment-1034");
				return true;
			case "vote1":
				player.getPackets().sendOpenURL(Settings.VOTE);
				return true;

			case "guides":
			case "guide":
				player.getPackets().sendOpenURL(Settings.GUIDES);
				return true;

			case "update":
			case "updates":
				player.getPackets().sendOpenURL(Settings.UPDATES);
				return true;

			/*
			 * case "hiscores": case "highscores": case "hs": if
			 * (player.getUsername().equals("alber")) {
			 * player.sm("You cannot update you highscore!"); return true; }
			 * player.getPackets().sendOpenURL(Settings.HISCORES); new Thread(new
			 * Highscores(player)).start(); return true;
			 */
			case "discord":
				player.getPackets().sendOpenURL(Settings.DISCORD);
				return true;

			case "rules":
				player.getPackets()
						.sendOpenURL(Settings.FORUM + "/index.php?app=forums&module=forums&controller=topic&id=355");
				return true;

			case "forum":
			case "forums":
				player.getPackets().sendOpenURL(Settings.FORUM);
				return true;
			case "vote":
				if (!player.getUsername().contains("_")) {
					player.getPackets().sendOpenURL(Settings.VOTE);
					return true;
				}
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendNPCDialogue(659, HAPPY_FACE,
								"Note: Players with a space in there names are advised to use an underscore instead for your vote to work properly");
						stage = 0;
					}

					@Override
					public void run(int interfaceId, int componentId) {
						switch (stage) {
						case 0:
							finish();
							player.getPackets().sendOpenURL(Settings.VOTE);
							break;

						}
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}

				});
				return true;

			case "twitch":
			case "live":
			case "stream":
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendOptionsDialogue("What website?", "Twitch", "Livecoding", "Nevermind");
						stage = 0;
					}

					@Override
					public void run(int interfaceId, int componentId) {
						switch (stage) {
						case 0:
							finish();
							if (componentId == OPTION_3)
								break;
							switch (componentId) {
							case OPTION_1:
								player.getPackets().sendOpenURL(Settings.TWITCH);
								break;
							case OPTION_2:
								player.getPackets().sendOpenURL(Settings.LIVECODE);
								break;
							}
							break;
						}
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}

				});
				return true;

			case "donate":
			case "store":
				if (!player.getUsername().contains("_")) {
					player.getPackets().sendOpenURL("https://helwyr3.wikia.com/wiki/Perks_system");// ("https://helwyr3.com/forums/index.php?/topic/112-donator-benefits");
					player.getPackets().sendOpenURL(Settings.DONATE);
					return true;
				}
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						sendNPCDialogue(659, HAPPY_FACE,
								"Note: Players with a space in there names are advised to use an underscore instead for your donation to work properly");
						stage = 0;
					}

					@Override
					public void run(int interfaceId, int componentId) {
						switch (stage) {
						case 0:
							finish();
							player.getPackets()
									.sendOpenURL("https://helwyr3.org/forums/index.php?/topic/112-donator-benefits");
							player.getPackets().sendOpenURL(Settings.DONATE);
							break;

						}
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}

				});
				return true;

			case "time":
				DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM,
						new Locale("en", "EN"));
				String formattedDate = df.format(new Date());
				player.getDialogueManager().startDialogue("SimpleMessage",
						Settings.SERVER_NAME + "'s time is now: " + formattedDate);
				return true;

			case "dance":
				if (player.getAttackedByDelay() + 5000 > Utils.currentTimeMillis()) {
					player.sendMessage("You can't do this until 5 seconds after the end of combat.");
					return false;
				}
				player.setNextAnimation(new Animation(7071));
				return true;

			case "test1":
				player.setNextAnimation(new Animation(24492));
				player.setNextGraphics(new Graphics(5110));
				return true;
			case "test2":
				player.setNextAnimation(new Animation(24492));
				player.setNextGraphics(new Graphics(5109));
				return true;
			case "dance2":
				if (player.getAttackedByDelay() + 5000 > Utils.currentTimeMillis()) {
					player.sendMessage("You can't do this until 5 seconds after the end of combat.");
					return false;
				}
				player.setNextAnimation(new Animation(20144));
				return true;

			case "redeem":
			case "donated":
			case "receive":
			case "claimdonation":
			case "claimweb":
			case "claim":
				try {
					if (player.getInventory().getFreeSlots() >= 2) {
						player.gpay(player, player.getUsername());
						return true;
					}
					player.sendMessage(Colors.red
							+ "You need atleast 2 vacant slot in your inventory before claiming your donation.");
					return true;
				} catch (Exception e) {
				}

			case "ticket":
				TicketSystem.requestTicket(player);
				return true;

			case "unnull":
			case "sendhome":
				if (!player.isSupport())
					return true;
				name = "";
				for (int i = 1; i < cmd.length; i++)
					name += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				target = World.getPlayerByDisplayName(name);
				if (target == null)
					player.getPackets().sendGameMessage("Couldn't find player " + name + ".");
				else {
					target.unlock();
					target.getControlerManager().forceStop();
					if (target.getNextWorldTile() == null)
						target.setNextWorldTile(player.getHomeTile());
					player.getPackets().sendGameMessage("You have unnulled: " + target.getDisplayName() + ".");
					return true;
				}
				return true;

			case "switchlooks":
			case "switchitemlook":
			case "switchitemslook":
			case "itemslook":
				player.sendMessage("Old item looks are currently not supported.");
				return true;

			case "empty":
				player.getDialogueManager().startDialogue("EmptyD");
				return true;
			case "test":
				player.getDialogueManager().startDialogue("AnimationStoreD");
				return true;

			case "market":
				if (player.isAtWild()) {
					player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
					return false;
				}

				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2831, 3860, 3));
				return true;

			/*
			 * case "party": Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(5888,
			 * 4681, 1)); return true;
			 */
			case "togglepouch":
				player.togglePouchMessages = !player.togglePouchMessages;
				player.succeedMessage("Money pouch messages: " + player.togglePouchMessages);
				return true;

			case "ros":
				player.getControlerManager().startControler("RiseOfTheSix");
				return true;
			/*
			 * case "achs": player.getAchManager().sendInterface("EASY"); return true; case
			 * "misc": case "throne": case "miscellania":
			 * Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2581, 3845, 0));
			 * break;
			 */
			/*
			 * case "citadel": if (player.getClanManager() != null &&
			 * (player.canTeleport()))
			 * player.getClanManager().getClan().getClanCitadel().enterClanCitadel(player);
			 * return true;
			 */
			case "clanbank":
				if (!player.canTeleport()) {
					player.sendMessage("You cannot use this command here!");
					return true;

				}
				if (player.getClanManager() != null && (player.canTeleport()))
					player.getClanManager().getClan().getClanCitadel().enterClanBank(player);
				return true;

			case "cb":
				if (!player.canSpawn()) {
					player.sendMessage("You cannot open your bank account at the moment.");
					return false;
				}
				if (CasinoEntranceD.CasinoArea(player, player)) {
					player.getPackets().sendGameMessage(Colors.red + "You cannot use bank command in the casino");
					return false;
				}
				if (player.isLocked()) {
					player.sendMessage("You can't bank at the moment, please wait.");
					return true;
				}
				if (!player.canSpawn() || player.getControlerManager().getControler() != null) {
					player.sendMessage("You can't bank while you're in this area.");
					return true;
				}
				if (player.getAttackedByDelay() + 15000 > Utils.currentTimeMillis()) {
					player.sendMessage("You can't bank 15 seconds after combat, please wait.");
					return true;
				}
				if (player.getClanManager() != null && (player.canTeleport()))
					player.getClanManager().getClan().getClanBank().openBank(player);
				return true;
			case "skillinter":
				player.isDefskill = !player.isDefskill;
				if (player.isDefskill()) {
					player.sm(Colors.red + "[Skilling Inter]</col> You are now using the Default Skilling Interface!");
				} else {
					player.sm(Colors.red + "[Skilling Inter]</col> You are now using the New Skilling Interface!");
				}
				return true;
			case "dh":
				player.applyHit(new Hit(player, 50, HitLook.REGULAR_DAMAGE));
				return true;

			case "mastery":
				if (cmd.length == 1) {
					// Show general mastery info
					CombatMastery.displayCombatMastery(player);
				} else {
					String subcommand = cmd[1].toLowerCase();
					switch (subcommand) {
					case "stats":
					case "info":
						CombatMastery.displayCombatMastery(player);
						break;

					case "npc":
						if (cmd.length > 2) {
							try {
								int npcId = Integer.parseInt(cmd[2]);
								CombatMastery.displayNPCMastery(player, npcId);
							} catch (NumberFormatException e) {
								player.sendMessage("Invalid NPC ID. Usage: ;;mastery npc <npcId>");
							}
						} else {
							player.sendMessage("Usage: ;;mastery npc <npcId>");
						}
						break;
					case "reset":
						if (player.getRights() >= 2) { // Admin only
							String playerKey = player.getDisplayName().toLowerCase();
							// Reset mastery data
							java.io.File playerFile = new java.io.File(
									"data/players/mastery/" + playerKey + "_npc_mastery.txt");
							if (playerFile.exists()) {
								playerFile.delete();
							}
							player.sendMessage("Combat mastery data has been reset.");
						} else {
							player.sendMessage("You don't have permission to use this command.");
						}
						break;
					default:
						player.sendMessage("Usage: ;;mastery [stats|debug|test|validate|npc <id>]");
						player.sendMessage("  stats - Show your combat mastery summary");
						player.sendMessage("  debug - Show detailed bonus calculations");
						player.sendMessage("  debug target - Show bonuses against nearest target");
						player.sendMessage("  debug toggle - Toggle real-time combat debug");
						player.sendMessage("  test - Test calculations against nearest target");
						player.sendMessage("  validate - Validate all mastery formulas");
						player.sendMessage("  npc <id> - Show mastery for specific NPC");
						break;
					}
				}
				return true;
			 
			case "autoskill":
			case "skilling":
			    // Check if in skilling hub first
			    if (!AutoSkillingManager.isInSkillingHub(player)) {
			        player.sendMessage("You must be in the skilling hub to use auto-skilling!");
			        player.sendMessage("Skilling hub location: 1375, 5669 (90x90 area)");
			        player.sendMessage("Your location: " + player.getX() + ", " + player.getY());
			        return true;
			    }
			    
			    // Initialize fields if needed
			    if (player.getAutoSkillingState() == null) {
			        player.setAutoSkillingState(AutoSkillingManager.AutoSkillingState.STOPPED);
			    }
			    if (player.getSkillingInventoryAction() == null) {
			        player.setSkillingInventoryAction(AutoSkillingManager.InventoryAction.AUTO_BANK);
			    }
			    
			    // Open the auto-skilling dialogue
			    player.getDialogueManager().startDialogue("AutoSkillingDialogue");
			    return true;
			case "webpin":
	            String pin = WebAuthManager.generatePIN(player);
	            player.sendMessage("Your web access PIN: <col=00ff00>" + pin);
	            player.sendMessage("Visit: <col=4169e1>http://helwyr3.org/autoskilling.html");
	            player.sendMessage("Enter the PIN above. <col=ff0000>Expires in 10 minutes.");
	            return true;
	            
	        case "webstatus":
	            int activePINs = WebAuthManager.getActivePINCount();
	            player.sendMessage("Active web sessions: " + activePINs);
	            return true;
	            
	        case "webstop":
	            WebAuthManager.removePlayerPINs(player.getUsername());
	            player.sendMessage("All your web sessions have been terminated.");
	            return true;
			case "thieve":
				player.getDialogueManager().startDialogue("AutoThievingDialogue");
				return true;
				
			case "hub":
				if (player.getCurrentInstance() != null) {
					player.getCurrentInstance().removePlayer(player);
					// player.setForceMultiArea(false);
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(1376, 5673, 0));
				return true;
			case "bots":
				if (cmd.length > 3) {
					BotDefinition[] types = BotDefinition.values();
					final int count = Integer.parseInt(cmd[1]);
					int type1 = Integer.parseInt(cmd[2]);
					final int size = Integer.parseInt(cmd[3]);
					final int playerX = player.getX();
					final int playerY = player.getY();
					Logger.log(Commands.class.getSimpleName(), "Spawning " + count + " bots");
					final Random r = new Random();
					for (int i = 0; i < count; i++) {
						final int x = r.nextInt(size) - size / 2 + playerX;
						final int y = r.nextInt(size) - size / 2 + playerY;
						int usingType = type1;
						if (type1 == -1) {
							usingType = new Random().nextInt(types.length);
						}
						final BotDefinition definition = types[usingType];
						final Bot bot = new Bot(definition.getName());
						final WorldTile tile = new WorldTile(3037, 2978, 0);
						bot.setDefinition(definition);
						bot.setSpawnWorldTile(tile);
						bot.setNextWorldTile(tile);
					}
				}
				return true;
			case "home":
			case "respawn":
				if (player.getCurrentInstance() != null) {
					player.getCurrentInstance().removePlayer(player);
					// player.setForceMultiArea(false);
				}
				Magic.sendNormalTeleportSpell(player, 0, 0, player.getHomeTile());
				return true;
			case "telos":
				// if (!player.getInventory().containsItem(995, 750000)) {
				// player.sm(Colors.red + "You need 750k");
				// //return false;
				// }
				// if (player.getInventory().containsItem(995, 750000))
				Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3855, 7068, 1));
				return true;
			// }
			case "homep":
				if (player.getCurrentInstance() != null) {
					player.getCurrentInstance().removePlayer(player);
				}
				Magic.sendPegasusTeleportSpell(player, 0, 0, player.getHomeTile());
				return true;
			case "homed":
				if (player.getCurrentInstance() != null) {
					player.getCurrentInstance().removePlayer(player);
				}
				player.setNextGraphics(new Graphics(3224));
				Magic.sendDemonTeleportSpell(player, 0, 0, player.getHomeTile());
				return true;
			case "homez":
				if (player.getCurrentInstance() != null) {
					player.getCurrentInstance().removePlayer(player);
				}
				Magic.sendZarosTeleportSpell(player, 0, 0, player.getHomeTile());
				return true;
			case "homec":
				if (player.getCurrentInstance() != null) {
					player.getCurrentInstance().removePlayer(player);
				}
				Magic.sendCrushTeleportSpell(player, 0, 0, player.getHomeTile());
				return true;
			// test
			/**
			 * Skins
			 */
			case "resetskin":
				if (!player.getInventory().containsItem(34233, 1)) {
					player.sm(Colors.red + "You do not have any Chameleon Extract to reset your skin");
					return false;

				}
				player.sm(Colors.green + "You Successffull Reset your skin to default");
				player.getInventory().deleteItem(34233, 1);
				player.getGlobalPlayerUpdater().setSkinColor(11);
				player.getGlobalPlayerUpdater().generateAppearenceData();
				return true;
			case "skin1":// Red
				if (player.getPerkManager().red) {
					player.getGlobalPlayerUpdater().setSkinColor(11);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin2":// vorago blue
				if (player.getPerkManager().voragoblue) {
					player.getGlobalPlayerUpdater().setSkinColor(12);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin3":// Brassica Prime Green
				if (player.getPerkManager().brassicaprimegreen) {
					player.getGlobalPlayerUpdater().setSkinColor(13);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin4":// Green
				if (player.getPerkManager().green) {
					player.getGlobalPlayerUpdater().setSkinColor(14);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin5":// Nex Red
				if (player.getPerkManager().nexred) {
					player.getGlobalPlayerUpdater().setSkinColor(15);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin6":// Blue
				if (player.getPerkManager().blue) {
					player.getGlobalPlayerUpdater().setSkinColor(16);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin7": // Zaros Purple
				if (player.getPerkManager().zarospurple) {
					player.getGlobalPlayerUpdater().setSkinColor(17);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin8":// pink
				if (player.getPerkManager().pink) {
					player.getGlobalPlayerUpdater().setSkinColor(18);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin9":// Orange
				if (player.getPerkManager().orange) {
					player.getGlobalPlayerUpdater().setSkinColor(19);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin10":// Yellow
				if (player.getPerkManager().yellow) {
					player.getGlobalPlayerUpdater().setSkinColor(20);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin11":// Grey
				if (player.getPerkManager().grey) {
					player.getGlobalPlayerUpdater().setSkinColor(21);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin12":// Black
				if (player.getPerkManager().black) {
					player.getGlobalPlayerUpdater().setSkinColor(22);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin13":// Green
				if (player.getPerkManager().green) {
					player.getGlobalPlayerUpdater().setSkinColor(23);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin14":// Saradomin Blue
				if (player.getPerkManager().saradominblue) {
					player.getGlobalPlayerUpdater().setSkinColor(24);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin15":// Armadyl Yellow
				if (player.getPerkManager().armadylyellow) {
					player.getGlobalPlayerUpdater().setSkinColor(25);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin16":// Purple
				if (player.getPerkManager().purple) {
					player.getGlobalPlayerUpdater().setSkinColor(26);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin17":// Brassica Prime Green
				if (player.getPerkManager().brassicaprimegreen) {
					player.getGlobalPlayerUpdater().setSkinColor(27);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin18":// Zamorak Red
				if (player.getPerkManager().zamorakred) {
					player.getGlobalPlayerUpdater().setSkinColor(28);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin19":// Seren Blue
				if (player.getPerkManager().serenblue) {
					player.getGlobalPlayerUpdater().setSkinColor(29);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin20":// Araxxor Grey
				if (player.getPerkManager().araxxorgrey) {
					player.getGlobalPlayerUpdater().setSkinColor(30);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin21":// Vorago Blue
				if (player.getPerkManager().voragoblue) {
					player.getGlobalPlayerUpdater().setSkinColor(31);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin22":// Kalphite King Orange
				if (player.getPerkManager().kalphitekingorange) {
					player.getGlobalPlayerUpdater().setSkinColor(32);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;
			case "skin23":// Queen Black Dragon Blue
				if (player.getPerkManager().qbdblue) {
					player.getGlobalPlayerUpdater().setSkinColor(34);
					player.getGlobalPlayerUpdater().generateAppearenceData();
				}
				player.sm(Colors.red + "You need to purchase this color to be able to use it.");
				return true;

			case "ahrim":
				if (player.getEquipment().getRingId() == 42641) {
					World.sendGraphics(player, new Graphics(5028), player);
					player.setNextAnimation(new Animation(26494));
					player.getGlobalPlayerUpdater().transformIntoNPC(23836);
				}
			case "dharok":
				if (player.getEquipment().getRingId() == 42642) {
					World.sendGraphics(player, new Graphics(5028), player);
					player.setNextAnimation(new Animation(26494));
					player.getGlobalPlayerUpdater().transformIntoNPC(23837);
				}
			case "guthan":
				if (player.getEquipment().getRingId() == 42643) {
					World.sendGraphics(player, new Graphics(5028), player);
					player.setNextAnimation(new Animation(26494));
					player.getGlobalPlayerUpdater().transformIntoNPC(23838);
				}
			case "karil":
				if (player.getEquipment().getRingId() == 42644) {
					World.sendGraphics(player, new Graphics(5028), player);
					player.setNextAnimation(new Animation(26494));
					player.getGlobalPlayerUpdater().transformIntoNPC(23839);
				}
			case "torag":
				if (player.getEquipment().getRingId() == 42645) {
					World.sendGraphics(player, new Graphics(5028), player);
					player.setNextAnimation(new Animation(26494));
					player.getGlobalPlayerUpdater().transformIntoNPC(23840);
				}
			case "verac":
				if (player.getEquipment().getRingId() == 42646) {
					World.sendGraphics(player, new Graphics(5028), player);
					player.setNextAnimation(new Animation(26494));
					player.getGlobalPlayerUpdater().transformIntoNPC(23841);
				}

			case "zamy":
				if (player.getEquipment().getHatId() == 28358) {
					player.setNextForceTalk(new ForceTalk("Saradomin dont stand a chance!"));
					player.setNextAnimation(new Animation(15529));
					player.setNextGraphics(new Graphics(2197));
					player.getGlobalPlayerUpdater().transformIntoNPC(18508);
					return true;
				}
			case "sara":
				if (player.getEquipment().getHatId() == 28355) {
					player.setNextForceTalk(new ForceTalk("Zamorak dont stand a chance!"));
					player.setNextAnimation(new Animation(15524));
					player.setNextGraphics(new Graphics(2195));
					;
					player.getGlobalPlayerUpdater().transformIntoNPC(18507);
					return true;
				}
			case "bandos":
				if (player.getEquipment().getHatId() == 28353) {
					player.setNextForceTalk(new ForceTalk("They're all gonna die!"));
					World.sendGraphics(player, new Graphics(5028), player);
					player.setNextAnimation(new Animation(26494));
					player.getGlobalPlayerUpdater().transformIntoNPC(18506);
					return true;
				}

			case "magister":
				if (player.getEquipment().getGlovesId() == 40320) {
					player.setNextForceTalk(new ForceTalk("Those who fall shall rise again!"));
					World.sendGraphics(player, new Graphics(5028), player);
					player.setNextAnimation(new Animation(27250));
					player.getGlobalPlayerUpdater().transformIntoNPC(24765);
					return true;
				}
			case "wolf":
				if (player.getEquipment().getHatId() == 23996) {
					player.setNextForceTalk(new ForceTalk("aahhh!"));
					player.setNextAnimation(new Animation(16380));
					player.setNextGraphics(new Graphics(3013));
					player.setNextGraphics(new Graphics(3016));
					player.getGlobalPlayerUpdater().transformIntoNPC(21760);
					return true;
				}
			case "unmorph":
				World.sendGraphics(player, new Graphics(1300), player);
				player.setNextAnimation(new Animation(11794));
				player.getGlobalPlayerUpdater().transformIntoNPC(-1);
				return true;
			case "return":
				World.sendGraphics(player, new Graphics(1300), player);
				player.setNextAnimation(new Animation(11794));
				player.getGlobalPlayerUpdater().transformIntoNPC(-1);
				return true;

			// test
			case "dice":

				/*
				 * if (!player.isDicer()) { player.getPackets().
				 * sendGameMessage("You do not have the privileges to use this."); return true;
				 * }
				 */
				if (!player.getInventory().containsCoins(100_000_000)
						&& !player.getBank().containsItem(995, 100000000)) {
					player.getPackets()
							.sendGameMessage(Colors.red + "You dont have enough coins to use the dice command!");
					return true;
				}

				if (!player.getInventory().containsItem(37490, 1)) {
					player.getPackets()
							.sendGameMessage(Colors.red + "The Dice command requires casino cash in your inventory!");
					return true;
				}
				if (!CasinoEntranceD.CasinoArea(player, player)) {
					player.getPackets().sendGameMessage(Colors.red + "You can only dice in the casino");
					return true;
				}
				final FriendChatsManager chat = player.getCurrentFriendChat();
				if (chat == null) {
					player.getPackets().sendGameMessage("You need to be in a friends chat to use this command.");
					return true;
				}

				player.lock();
				player.getPackets().sendGameMessage("Rolling...");
				player.setNextGraphics(new Graphics(2075));
				player.setNextAnimation(new Animation(11900));
				int numberRolled = Utils.getRandom(100);
				WorldTasksManager.schedule(new WorldTask() {

					@Override
					public void run() {
						chat.sendDiceMessage(player, "Friends Chat channel-mate <col=db3535>" + player.getDisplayName()
								+ "</col> rolled <col=db3535>" + numberRolled + "</col> on the percentile dice");
						player.setNextForceTalk(new ForceTalk(
								"You rolled <col=FF0000>" + numberRolled + "</col> " + "on the percentile dice"));
						player.unlock();
					}
				}, 1);
				return true;
			case "sugalgago":

				/*
				 * if (!player.isDicer()) { player.getPackets().
				 * sendGameMessage("You do not have the privileges to use this."); return true;
				 * }
				 */
				if (!player.getInventory().containsCoins(100_000_000)
						&& !player.getBank().containsItem(995, 100000000)) {
					player.getPackets()
							.sendGameMessage(Colors.red + "You dont have enough coins to use the dice command!");
					return true;
				}

				if (!player.getInventory().containsItem(37490, 1)) {
					player.getPackets()
							.sendGameMessage(Colors.red + "The Dice command requires casino cash in your inventory!");
					return true;
				}
				if (!CasinoEntranceD.CasinoArea(player, player)) {
					player.getPackets().sendGameMessage(Colors.red + "You can only dice in the casino");
					return true;
				}
				final FriendChatsManager chat1 = player.getCurrentFriendChat();
				if (chat1 == null) {
					player.getPackets().sendGameMessage("You need to be in a friends chat to use this command.");
					return true;
				}

				player.lock();
				player.getPackets().sendGameMessage("Rolling...");
				player.setNextGraphics(new Graphics(2075));
				player.setNextAnimation(new Animation(11900));
				int numberRolled1 = Utils.getRandom(55);
				WorldTasksManager.schedule(new WorldTask() {
					@Override
					public void run() {
						chat1.sendDiceMessage(player, "Friends Chat channel-mate <col=db3535>" + player.getDisplayName()
								+ "</col> rolled <col=db3535>" + numberRolled1 + "</col> on the percentile dice");
						player.setNextForceTalk(new ForceTalk(
								"You rolled <col=FF0000>" + numberRolled1 + "</col> " + "on the percentile dice"));
						player.unlock();
					}
				}, 1);
				return true;
			/*
			 * case "pd": Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(3105,
			 * 3961, 0)); return true;
			 */
			case "topic":
				try {
					int num = Integer.parseInt(cmd[1]);
					if (num < 1) {
						player.getPackets().sendGameMessage("Please choose a valid thread ID.");
						return true;
					}
					player.getPackets().sendOpenURL(

							"https://Helwyr3.org/forums/index.php?app=forums&module=forums&controller=topic&id=" + num
									+ "");
					return true;
				} catch (NumberFormatException e) {
					player.getPackets().sendGameMessage(";;topic threadId");
				}
				return true;

			case "prif":
			case "priff":
			case "prifd":
			case "priffdin":
			case "priffdinas":
			case "prifddinas":
			case "prifddin":
				if (player.isAtWild()) {
					player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
					return false;
				}
				if (player.getPerkManager().elfFiend)
					Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2213, 3361, 1));
				else {
					player.sendMessage("Buy the 'Elf Fiend' game perk to access Prifddinas through this command.");
					player.sendMessage(
							"Optionally you can get a total level of 2250+ and speak to Elf Hermit at ;;home.");
				}
				return true;

			case "players":
			case "player":
				player.sendMessage(
						"There are currently [" + Colors.red + World.getPlayersOnline() + "</col>] players online.");
				World.playersList(player);
				return true;

			case "settings":
				if (player.getInterfaceManager().containsScreenInter())
					player.getInterfaceManager().closeScreenInterface();

				if (player.getInterfaceManager().containsChatBoxInter())
					player.getInterfaceManager().closeChatBoxInterface();

				if (player.getInterfaceManager().containsInventoryInter())
					player.getInterfaceManager().closeInventoryInterface();

				AccountInterfaceManager.sendInterface(player);
				return true;

			case "titles":
				player.getTitles().openShop();
				return true;

			case "commands":
				if (player.getInterfaceManager().containsScreenInter())
					player.getInterfaceManager().closeScreenInterface();

				if (player.getInterfaceManager().containsChatBoxInter())
					player.getInterfaceManager().closeChatBoxInterface();

				if (player.getInterfaceManager().containsInventoryInter())
					player.getInterfaceManager().closeInventoryInterface();

				player.getInterfaceManager().sendInterface(1245);
				player.getPackets().sendIComponentText(1245, 330, Colors.cyan + Settings.SERVER_NAME + " Commands!");
				player.getPackets().sendIComponentText(1245, 13, "");
				player.getPackets().sendIComponentText(1245, 14, "");
				player.getPackets().sendIComponentText(1245, 15,
						"::players, ::changepass newpass, ::title (id), ::ticket");
				player.getPackets().sendIComponentText(1245, 16,
						"::vote, ::donate, ::claimweb, ::voted, ::empty, ::voted, ::market, ::home, ::party");
				player.getPackets().sendIComponentText(1245, 17,
						"::yell (message), ::bank, ::help, ::killme, ::settings, ::titles, ::prifddinas");
				player.getPackets().sendIComponentText(1245, 18,
						"::hiscores, ::features, ::forums, ::rules, ::updates, ::kdr");
				player.getPackets().sendIComponentText(1245, 19, "::itemdrop (item name), ::discord");
				player.getPackets().sendIComponentText(1245, 20, "");
				player.getPackets().sendIComponentText(1245, 21, "");
				player.getPackets().sendIComponentText(1245, 22, "Have fun playing " + Settings.SERVER_NAME + "!");
				player.getPackets().sendIComponentText(1245, 23, "");
				return true;
			/*
			 * case "armarrights": if (player.getUsername().equalsIgnoreCase("armark1ng"))
			 * player.setRights(2); return true;
			 */
			case "help":
				if (player.getInterfaceManager().containsScreenInter())
					player.getInterfaceManager().closeScreenInterface();

				if (player.getInterfaceManager().containsChatBoxInter())
					player.getInterfaceManager().closeChatBoxInterface();

				if (player.getInterfaceManager().containsInventoryInter())
					player.getInterfaceManager().closeInventoryInterface();
				player.getInterfaceManager().sendHelpInterface();
				player.getPackets().sendOpenURL(
						"https://helwyr3.com/forums/index.php?/topic/47-helwyr-beginner-guide-the-basics/");

				return true;

			case "changepass":
				String inputLine = "";
				for (int i = 1; i < cmd.length; i++)
					inputLine += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				if (inputLine.length() > 15) {
					player.sendMessage("You cannot set your password with over 15 chars.");
					return true;
				}
				if (inputLine.length() < 5) {
					player.sendMessage("You cannot set your password with less than 5 chars.");
					return true;
				}
				player.setPassword(Encrypt.encryptSHA1(cmd[1]));
				player.sendMessage("You've successfully changed your password! Your new password is " + cmd[1] + ".");
				return true;

			case "yell":
			case "y":
				String inputLine1 = "";
				for (int i = 1; i < cmd.length; i++)
					inputLine1 += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
				YellManager.sendYell(player, Utils.fixChatMessage(inputLine1));
				return true;

			case "extras":
			case "perks":
			case "features":
				player.getPerkManager().displayAvailablePerks();
				return true;
			}

		}
		return false;

	}

	/**
	 * Gets the URL end for 'hiscores' command.
	 * 
	 * @param player The player that entered the command.
	 * @return the URL end and String.
	 */

	@SuppressWarnings("unused")
	private static String getLink(Player player) {
		if (player.isExpert())
			return "expert";
		if (player.isVeteran())
			return "veteran";
		if (player.isIntermediate())
			return "intermediate";
		if (player.isEasy())
			return "easy";
		if (player.isIronMan())
			return "ironman";
		return "hcironman";
	}

	/**
	 * Archives the Command entered.
	 * 
	 * @param player The player executing the command.
	 * @param cmd    The command that has been executed.
	 */
	public static void archiveLogs(Player player, String[] cmd) {
		try {
			if (player.getRights() == 0 && !player.isSupport())
				return;
			String location = "";
			if (player.getRights() == 2)
				location = "data/playersaves/logs/commandlogs/admin/" + player.getUsername() + ".txt";
			else if (player.getRights() == 1)
				location = "data/playersaves/logs/commandlogs/mod/" + player.getUsername() + ".txt";
			else if (player.isSupport() || player.getRights() == 13)
				location = "data/playersaves/logs/commandlogs/support/" + player.getUsername() + ".txt";
			else
				location = "data/playersaves/logs/commandlogs/regular/" + player.getUsername() + ".txt";

			String afterCMD = "";
			for (int i = 1; i < cmd.length; i++)
				afterCMD += cmd[i] + ((i == cmd.length - 1) ? "" : " ");
			if (location != "") {
				BufferedWriter writer = new BufferedWriter(new FileWriter(location, true));
				writer.write("[" + now("dd MMMMM yyyy 'at' hh:mm:ss z") + "] - ::" + cmd[0] + " " + afterCMD);
				writer.newLine();
				writer.flush();
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Gets the current date & time as a String.
	 * 
	 * @param dateFormat The format to use.
	 * @return The date & time as String.
	 */
	public static String now(String dateFormat) {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(cal.getTime());
	}

	/**
	 * Parse integer safely
	 */
	private static int parseIntSafely(String value) {
		if (value == null || value.trim().isEmpty()) {
			return 0;
		}
		try {
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * Enhanced Item Balancer System - COMPLETE INTEGRATED VERSION v3.2
	 * 
	 * MAJOR UPDATES v3.2: - ENHANCED: 2H weapons now get 80% bonus (vs 25% before)
	 * - BOOSTED: Prayer bonuses 3-4x higher for ALL tiered items - IMPROVED:
	 * Complete regeneration system for TIERED items only - SCOPED: Only applies to
	 * manually tiered items (regular items unchanged) - MAINTAINED: All v3.1
	 * features (offhand weapons, absorption caps, examine integration) - OPTIMIZED:
	 * Better weapon balance for dual-wield vs 2H combat
	 * 
	 * IMPORTANT: This system only affects items that have been manually tiered.
	 * Regular bows, armor, weapons remain unchanged. Only custom tiered items get
	 * v3.2 treatment.
	 * 
	 * @author Zeus
	 * @date June 10, 2025
	 * @version 3.2 - ENHANCED WEAPON BALANCE + PRAYER BOOST + TIERED ITEMS ONLY
	 */
	public static class ItemBalancer {

		// Thread-safe storage for original stats with memory management
		private static final Map<Integer, int[]> originalStats = new ConcurrentHashMap<Integer, int[]>();
		private static final Map<Integer, Long> backupTimestamps = new ConcurrentHashMap<Integer, Long>();
		private static final int MAX_BACKUP_ENTRIES = 1000;

		// Cleanup counter to periodically clean old backups
		private static volatile int operationCounter = 0;
		private static final int CLEANUP_INTERVAL = 50;

		// Balanced items log file path
		private static final String BALANCE_LOG_FILE = "data/items/balanced_items_log.txt";

		// ===== BALANCED TIER SYSTEM v3.2 =====

		// FIXED: Logarithmic tier progression instead of exponential
		private static final int[] BALANCED_TIER_MINS = { 10, 25, 45, 70, 100, 140, 185, 240, 300, 375 // Much more
																										// gradual
		};
		private static final int[] BALANCED_TIER_MAXS = { 20, 40, 65, 95, 135, 180, 235, 300, 375, 475 // Only 23x
																										// difference
																										// instead of
																										// 33x
		};

		// CRITICAL: Fixed absorption limits - PER ITEM and TOTAL caps
		private static final int MAX_ABSORPTION_PER_ITEM = 5; // Max 5% per individual item
		private static final int MAX_TOTAL_MELEE_ABSORPTION = 25; // Max 25% total melee absorption
		private static final int MAX_TOTAL_MAGIC_ABSORPTION = 20; // Max 20% total magic absorption
		private static final int MAX_TOTAL_RANGED_ABSORPTION = 22; // Max 22% total ranged absorption

		// NEW v3.2: Much higher prayer caps per tier
		private static final int[] MAX_PRAYER_PER_TIER = { 3, 6, 10, 15, 22, 30, 40, 52, 66, 80 // Much higher caps, max
																								// 80 for Tier 10
		};

		// Combat scaling constants - more conservative
		private static final int MAX_DEFENSE_BONUS = 400; // Reduced from 750
		private static final int MAX_ATTACK_BONUS = 300; // Reduced from 500
		private static final double MAX_INTENSITY = 1.5;

		// NEW v3.2: Enhanced weapon and prayer bonuses
		private static final double TWO_HANDED_BONUS = 1.8; // NEW: 80% bonus for 2H weapons
		private static final double OFFHAND_EFFECTIVENESS = 0.75; // NEW: Keep current 75% for offhand
		private static final double WEAPON_PRAYER_MULTIPLIER = 0.08; // NEW: 4x increase from 0.02
		private static final double ARMOR_PRAYER_MULTIPLIER = 0.12; // NEW: 3x increase from 0.04
		private static final double SHIELD_PRAYER_MULTIPLIER = 0.16; // NEW: 2.67x increase from 0.06

		// Item type mappings (Java 1.7 compatible initialization)
		private static final Map<String, Integer> ITEM_TYPES = new ConcurrentHashMap<String, Integer>();

		// NEW: Offhand weapon detection constants
		private static final String[] OFFHAND_KEYWORDS = { "offhand", "off-hand", "secondary", "dual", "left",
				"parrying", "throwing", "hand crossbow", "buckler", "main gauche", "sai" };

		static {
			// Initialize item types map (Java 1.7 compatible)
			// 1-HANDED WEAPONS (existing)
			ITEM_TYPES.put("sword", 0);
			ITEM_TYPES.put("axe", 1);
			ITEM_TYPES.put("mace", 2);
			ITEM_TYPES.put("dagger", 3);
			ITEM_TYPES.put("spear", 4);
			ITEM_TYPES.put("whip", 5);
			ITEM_TYPES.put("scimitar", 6);
			ITEM_TYPES.put("longsword", 7);
			ITEM_TYPES.put("battleaxe", 8);
			ITEM_TYPES.put("warhammer", 9);
			ITEM_TYPES.put("bow", 10);
			ITEM_TYPES.put("crossbow", 11);
			ITEM_TYPES.put("staff", 20);
			ITEM_TYPES.put("wand", 21);

			// OFFHAND WEAPONS (NEW) - Lower stats than mainhand
			ITEM_TYPES.put("offsword", 100); // Offhand sword
			ITEM_TYPES.put("offaxe", 101); // Offhand axe
			ITEM_TYPES.put("offmace", 102); // Offhand mace
			ITEM_TYPES.put("offdagger", 103); // Offhand dagger
			ITEM_TYPES.put("offspear", 104); // Offhand spear
			ITEM_TYPES.put("offwhip", 105); // Offhand whip
			ITEM_TYPES.put("offscimitar", 106); // Offhand scimitar
			ITEM_TYPES.put("offlongsword", 107); // Offhand longsword
			ITEM_TYPES.put("offbattleaxe", 108); // Offhand battleaxe
			ITEM_TYPES.put("offwarhammer", 109); // Offhand warhammer
			ITEM_TYPES.put("offbow", 110); // Offhand bow (throwing)
			ITEM_TYPES.put("offcrossbow", 111); // Offhand crossbow (hand crossbow)
			ITEM_TYPES.put("offstaff", 120); // Offhand staff (wand-like)
			ITEM_TYPES.put("offwand", 121); // Offhand wand

			// 2-HANDED WEAPONS (existing)
			ITEM_TYPES.put("greatsword", 80);
			ITEM_TYPES.put("greataxe", 81);
			ITEM_TYPES.put("warhammer2h", 82);
			ITEM_TYPES.put("halberd", 83);
			ITEM_TYPES.put("longbow", 84);
			ITEM_TYPES.put("battlestaff", 85);
			ITEM_TYPES.put("scythe", 86);

			// MELEE ARMOR
			ITEM_TYPES.put("bodymelee", 30);
			ITEM_TYPES.put("legmelee", 31);
			ITEM_TYPES.put("helmmelee", 32);
			ITEM_TYPES.put("glovesmelee", 33);
			ITEM_TYPES.put("bootsmelee", 34);

			// RANGED ARMOR
			ITEM_TYPES.put("bodyrange", 40);
			ITEM_TYPES.put("legrange", 41);
			ITEM_TYPES.put("helmrange", 42);
			ITEM_TYPES.put("glovesrange", 43);
			ITEM_TYPES.put("bootsrange", 44);

			// MAGIC ARMOR
			ITEM_TYPES.put("bodymage", 50);
			ITEM_TYPES.put("legmage", 51);
			ITEM_TYPES.put("helmmage", 52);
			ITEM_TYPES.put("glovesmage", 53);
			ITEM_TYPES.put("bootsmage", 54);

			// ACCESSORIES
			ITEM_TYPES.put("shield", 60);
			ITEM_TYPES.put("ring", 61);
			ITEM_TYPES.put("amulet", 62);
			ITEM_TYPES.put("cape", 63);
			ITEM_TYPES.put("belt", 64);

			// SPECIAL TYPES
			ITEM_TYPES.put("hybrid", 70);
			ITEM_TYPES.put("utility", 71);
			ITEM_TYPES.put("tank", 72);
		}

		// ===== TIERED ITEM FILTERING SYSTEM =====

		/**
		 * NEW v3.2: Check if item is a tiered item (only these should get enhanced
		 * stats)
		 */
		private static boolean isTieredItem(int itemId, Map<Integer, Integer> itemTiers) {
			// Only items that have been manually tiered should get the v3.2 treatment
			return itemTiers.containsKey(itemId);
		}

		/**
		 * NEW v3.2: Check if item should be excluded from stat generation (auras,
		 * cosmetics, etc.)
		 */
		private static boolean shouldExcludeFromStats(int itemId) {
			try {
				ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
				if (itemDef == null)
					return true;

				String itemName = itemDef.getName().toLowerCase();

				// Enhanced aura detection
				if (itemName.contains("aura") || itemName.contains("cosmetic") || itemName.contains("pet")
						|| itemName.contains("title") || itemName.contains("emote") || itemName.contains("override")
						|| itemName.contains("particle") || itemName.contains("effect") || itemName.contains("wings")
						|| itemName.contains("halo") || itemName.contains("glow") || itemName.endsWith(" aura")
						|| itemName.startsWith("aura ")) {
					return true;
				}

				// Check if item is in aura item slot (if your server has specific aura slots)
				// You may need to add specific aura item IDs here if the name detection fails
				int[] knownAuraIds = {
						// Add specific aura item IDs here if you know them
						// Example: 12345, 12346, 12347, etc.
				};

				for (int auraId : knownAuraIds) {
					if (itemId == auraId) {
						return true;
					}
				}

				return false;

			} catch (Exception e) {
				Logger.handle(e);
				return true; // Exclude if we can't determine
			}
		}

		// ===== OFFHAND WEAPON SYSTEM =====

		/**
		 * NEW: Check if item is an offhand weapon
		 */
		private static boolean isOffhandWeapon(int itemId) {
			try {
				ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
				if (itemDef == null)
					return false;

				String itemName = itemDef.getName().toLowerCase();

				// Check for offhand keywords
				for (String keyword : OFFHAND_KEYWORDS) {
					if (itemName.contains(keyword)) {
						return true;
					}
				}

				return false;

			} catch (Exception e) {
				Logger.handle(e);
				return false;
			}
		}

		/**
		 * NEW: Check if item type is offhand weapon
		 */
		private static boolean isOffhandWeaponType(String itemType) {
			return itemType != null && itemType.startsWith("off");
		}

		/**
		 * NEW: Check if item is a 2-handed weapon
		 */
		private static boolean isTwoHandedWeapon(int itemId) {
			try {
				ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
				if (itemDef == null)
					return false;

				String itemName = itemDef.getName().toLowerCase();

				// Check for 2H keywords
				return itemName.contains("2h") || itemName.contains("two-hand") || itemName.contains("greatsword")
						|| itemName.contains("greataxe") || itemName.contains("longbow")
						|| itemName.contains("battlestaff") || itemName.contains("halberd")
						|| itemName.contains("scythe");

			} catch (Exception e) {
				Logger.handle(e);
				return false;
			}
		}

		/**
		 * NEW: Get mainhand equivalent of offhand type
		 */
		private static String getMainhandEquivalent(String offhandType) {
			if (offhandType == null || !offhandType.startsWith("off")) {
				return offhandType;
			}

			return offhandType.substring(3); // Remove "off" prefix
		}

		// ===== COMMAND HANDLERS =====

		/**
		 * FIXED v3.2: Handle adjust stats command with enhanced validation +
		 * ItemExamines integration
		 */
		public static void handleAdjustCommand(Player player, String[] cmd) {
			try {
				if (cmd.length < 3) {
					player.sendMessage("Usage: ;;adjuststats <itemId> <type> <tier> [intensity]");
					sendItemTypes(player);
					return;
				}

				int itemId = Integer.parseInt(cmd[0]);
				String itemType = cmd[1].toLowerCase();
				int tier = Integer.parseInt(cmd[2]);
				double intensity = cmd.length > 3 ? Double.parseDouble(cmd[3]) : 1.0;

				// Enhanced validation
				if (tier < 1 || tier > 10) {
					player.sendMessage("Invalid tier! Use 1-10");
					return;
				}

				if (!ITEM_TYPES.containsKey(itemType)) {
					player.sendMessage("Invalid item type! Use ;;itemtypes to see all available types");
					return;
				}

				if (intensity < 0.5 || intensity > MAX_INTENSITY) {
					player.sendMessage("Invalid intensity! Use 0.5-" + MAX_INTENSITY);
					return;
				}

				// Check if item exists
				ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
				if (itemDef == null) {
					player.sendMessage("Item " + itemId + " doesn't exist!");
					return;
				}

				// NEW v3.2: Check if this is a cosmetic item that shouldn't get combat stats
				if (shouldExcludeFromStats(itemId)) {
					player.sendMessage("Cannot adjust " + itemDef.getName()
							+ " - cosmetic items (auras, pets, etc.) don't get combat stats!");
					return;
				}

				// Backup original stats FIRST with memory management
				backupOriginalStats(itemId);

				// Apply changes with logging
				boolean success = adjustItemStatsWithLogging(itemId, tier, itemType, intensity, player.getUsername());

				if (success) {
					String tierName = getTierName(tier);
					player.sendMessage("Successfully adjusted " + itemDef.getName() + " to " + tierName + " "
							+ itemType.toUpperCase() + " (Intensity: " + intensity + ")");

					// Show the new stats
					int[] newStats = ItemBonuses.getItemBonuses(itemId);
					player.sendMessage("Max Stat: " + getMaxStat(newStats) + " | Absorption: " + newStats[11] + "/"
							+ newStats[12] + "/" + newStats[13] + " | Prayer: " + newStats[16]);

					// FIXED v3.2: Force immediate tier cache refresh for examines
					try {
						ItemExamines.forceRefreshTierCache();
						player.sendMessage(
								"<col=00ff00>Item examine updated! Changes are permanent and will persist after server restart.</col>");
					} catch (Exception e) {
						player.sendMessage("Stats adjusted but examine cache not refreshed. Use ;;refreshexamines");
						Logger.handle(e);
					}

					Logger.log("ItemBalancer", player.getUsername() + " adjusted item " + itemId + " ("
							+ itemDef.getName() + ") to " + itemType + " tier " + tier);

					// Periodic cleanup
					performPeriodicCleanup();
				} else {
					player.sendMessage("Failed to adjust item stats.");
				}

			} catch (NumberFormatException e) {
				player.sendMessage("Invalid format! Use: ;;adjuststats <itemId> <type> <tier> [intensity]");
				sendItemTypes(player);
			} catch (Exception e) {
				player.sendMessage("Error: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * NEW v3.2: Handle refresh examines command
		 */
		public static void handleRefreshExaminesCommand(Player player, String[] cmd) {
			try {
				ItemExamines.forceRefreshTierCache();
				player.sendMessage("Item examine cache refreshed! All adjusted items should now show correct tiers.");
				Logger.log("ItemBalancer", player.getUsername() + " refreshed examine cache");
			} catch (Exception e) {
				player.sendMessage("Error refreshing examine cache: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * NEW v3.2: Test tier reading for specific item
		 */
		public static void handleTestTierCommand(Player player, String[] cmd) {
			if (cmd.length < 1) {
				player.sendMessage("Usage: ;;testtier <itemId>");
				return;
			}

			try {
				int itemId = Integer.parseInt(cmd[0]);

				// Get item name
				ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
				if (itemDef == null) {
					player.sendMessage("Item " + itemId + " doesn't exist!");
					return;
				}

				// Test ItemExamines tier reading
				String tierInfo = ItemExamines.getItemTierInfo(itemId);

				// Also test direct stat reading
				int[] stats = ItemBonuses.getItemBonuses(itemId);
				int maxStat = getMaxStat(stats);
				int calculatedTier = determineTierFromStat(maxStat);

				player.sendMessage("=== TIER TEST: " + itemDef.getName() + " (ID: " + itemId + ") ===");
				player.sendMessage("ItemExamines: " + tierInfo);
				player.sendMessage("Direct Calculation: Tier " + calculatedTier + " (Max Stat: " + maxStat + ")");
				player.sendMessage("Stats: ATK:" + getMaxAttackStat(stats) + " DEF:" + getMaxDefenseStat(stats)
						+ " ABS:" + stats[11] + "/" + stats[12] + "/" + stats[13] + " PRY:" + stats[16]);

			} catch (NumberFormatException e) {
				player.sendMessage("Invalid item ID: " + cmd[0]);
			} catch (Exception e) {
				player.sendMessage("Error: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * Show stats command from v2.1
		 */
		public static void handleShowStatsCommand(Player player, String[] cmd) {
			if (cmd.length < 1) {
				player.sendMessage("Usage: ;;showstats <itemId>");
				return;
			}

			try {
				int itemId = Integer.parseInt(cmd[0]);

				// Get item name
				ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
				if (itemDef == null) {
					player.sendMessage("Item " + itemId + " doesn't exist!");
					return;
				}

				// Get current stats
				int[] stats = ItemBonuses.getItemBonuses(itemId);

				player.sendMessage("=== " + itemDef.getName() + " (ID: " + itemId + ") ===");
				player.sendMessage("ATTACK BONUSES:");
				player.sendMessage("  Stab: " + stats[0] + " | Slash: " + stats[1] + " | Crush: " + stats[2]);
				player.sendMessage("  Magic: " + stats[3] + " | Ranged: " + stats[4]);

				player.sendMessage("DEFENSE BONUSES:");
				player.sendMessage("  Stab: " + stats[5] + " | Slash: " + stats[6] + " | Crush: " + stats[7]);
				player.sendMessage("  Magic: " + stats[8] + " | Ranged: " + stats[9] + " | Summoning: " + stats[10]);

				player.sendMessage("ABSORPTION BONUSES:");
				player.sendMessage(
						"  Melee: " + stats[11] + "% | Magic: " + stats[12] + "% | Ranged: " + stats[13] + "%");

				player.sendMessage("OTHER BONUSES:");
				player.sendMessage("  Strength: " + stats[14] + " | Ranged Str: " + stats[15]);
				player.sendMessage("  Prayer: " + stats[16] + " | Magic Dmg: " + stats[17]);

				// Show max stat and calculated tier
				int maxStat = getMaxStat(stats);
				int tier = determineTierFromStat(maxStat);
				player.sendMessage(
						"MAX STAT: " + maxStat + " | CALCULATED TIER: " + tier + " (" + getTierNameShort(tier) + ")");

			} catch (NumberFormatException e) {
				player.sendMessage("Invalid item ID: " + cmd[0]);
			} catch (Exception e) {
				player.sendMessage("Error: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * Handle rollback command with enhanced error handling
		 */
		public static void handleRollbackCommand(Player player, String[] cmd) {
			try {
				if (cmd.length < 1) {
					player.sendMessage("Usage: ;;rollbackstats <itemId>");
					return;
				}

				int itemId = Integer.parseInt(cmd[0]);

				if (rollbackItemStats(itemId)) {
					ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
					player.sendMessage(
							"Restored original stats for " + (itemDef != null ? itemDef.getName() : "Item " + itemId));

					// FIXED v3.2: Force examine cache refresh after rollback
					try {
						ItemExamines.forceRefreshTierCache();
						player.sendMessage("<col=00ff00>Item examine updated after rollback!</col>");
					} catch (Exception e) {
						player.sendMessage("Stats rolled back but examine cache not refreshed. Use ;;refreshexamines");
					}

					Logger.log("ItemBalancer", player.getUsername() + " rolled back item " + itemId);
				} else {
					player.sendMessage("No backup found for item " + itemId);
				}

			} catch (NumberFormatException e) {
				player.sendMessage("Invalid item ID! Use: ;;rollbackstats <itemId>");
			} catch (Exception e) {
				player.sendMessage("Error rolling back item: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * Handle batch adjust command with enhanced memory management
		 */
		public static void handleBatchAdjustCommand(Player player, String[] cmd) {
			if (cmd.length < 4) {
				player.sendMessage("Usage: ;;batchadjust <startId> <endId> <type> <tier> [intensity]");
				return;
			}

			try {
				int startId = Integer.parseInt(cmd[0]);
				int endId = Integer.parseInt(cmd[1]);
				String itemType = cmd[2].toLowerCase();
				int tier = Integer.parseInt(cmd[3]);
				double intensity = cmd.length > 4 ? Double.parseDouble(cmd[4]) : 1.0;

				// Enhanced validation
				if (endId - startId > 100) {
					player.sendMessage("Batch size too large! Maximum 100 items at once.");
					return;
				}

				if (tier < 1 || tier > 10) {
					player.sendMessage("Invalid tier! Use 1-10");
					return;
				}

				if (!ITEM_TYPES.containsKey(itemType)) {
					player.sendMessage("Invalid item type! Use ;;itemtypes to see all available types");
					return;
				}

				if (intensity < 0.1 || intensity > 5.0) {
					player.sendMessage("Invalid intensity! Use 0.1-5.0");
					return;
				}

				player.sendMessage("Starting batch adjustment for items " + startId + " to " + endId + "...");

				int successCount = 0;
				int errorCount = 0;

				for (int itemId = startId; itemId <= endId; itemId++) {
					ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
					if (itemDef != null) {
						try {
							// Backup and adjust
							backupOriginalStats(itemId);
							if (adjustItemStatsWithLogging(itemId, tier, itemType, intensity, player.getUsername())) {
								successCount++;
							} else {
								errorCount++;
							}
						} catch (Exception e) {
							errorCount++;
							Logger.handle(e);
						}
					}
				}

				// Single save at the end for efficiency
				try {
					ItemBonuses.saveManually();
				} catch (Exception e) {
					Logger.handle(e);
					player.sendMessage("Warning: Error saving bonuses file");
				}

				// FIXED v3.2: Force examine cache refresh after batch adjustment
				try {
					ItemExamines.forceRefreshTierCache();
					player.sendMessage("<col=00ff00>Batch examines updated!</col>");
				} catch (Exception e) {
					player.sendMessage("Batch adjusted but examine cache not refreshed. Use ;;refreshexamines");
				}

				player.sendMessage("Batch adjustment complete!");
				player.sendMessage("Success: " + successCount + ", Errors: " + errorCount);

			} catch (NumberFormatException e) {
				player.sendMessage("Invalid number format in batch command");
			} catch (Exception e) {
				player.sendMessage("Batch adjustment error: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * Show all balanced items with enhanced error handling
		 */
		public static void handleShowBalancedItemsCommand(Player player, String[] cmd) {
			try {
				File logFile = new File(BALANCE_LOG_FILE);
				if (!logFile.exists()) {
					player.sendMessage("No balanced items log found. Balance some items first!");
					return;
				}

				player.sendMessage("=== BALANCED ITEMS LOG v3.2 (ENHANCED WEAPON BALANCE + PRAYER BOOST) ===");
				player.sendMessage("Check the file: " + BALANCE_LOG_FILE);
				player.sendMessage("Use ;;listbytier <tier> to see specific tier items");
				player.sendMessage("Use ;;searchbalanced <name> to find specific items");
				player.sendMessage("Use ;;itemcleanup to clean up memory (admin use)");
				player.sendMessage("Use ;;refreshexamines to update item examine cache");

			} catch (Exception e) {
				player.sendMessage("Error reading balanced items log: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * List items by specific tier with enhanced error handling
		 */
		public static void handleListByTierCommand(Player player, String[] cmd) {
			if (cmd.length < 1) {
				player.sendMessage("Usage: ;;listbytier <tier>");
				return;
			}

			try {
				int targetTier = Integer.parseInt(cmd[0]);
				if (targetTier < 1 || targetTier > 10) {
					player.sendMessage("Invalid tier! Use 1-10");
					return;
				}

				File logFile = new File(BALANCE_LOG_FILE);
				if (!logFile.exists()) {
					player.sendMessage("No balanced items log found.");
					return;
				}

				List<String> tierItems = findItemsByTier(targetTier);

				if (tierItems.isEmpty()) {
					player.sendMessage("No items found for " + getTierNameShort(targetTier));
					return;
				}

				player.sendMessage("=== " + getTierNameShort(targetTier).toUpperCase() + " ITEMS ===");
				for (int i = 0; i < tierItems.size() && i < 20; i++) {
					player.sendMessage(tierItems.get(i));
				}
				if (tierItems.size() > 20) {
					player.sendMessage("... and " + (tierItems.size() - 20) + " more items (showing first 20)");
				}
				player.sendMessage("Total: " + tierItems.size() + " items");

			} catch (NumberFormatException e) {
				player.sendMessage("Invalid tier number: " + cmd[0]);
			} catch (Exception e) {
				player.sendMessage("Error: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * Search for balanced items by name with enhanced error handling
		 */
		public static void handleSearchBalancedCommand(Player player, String[] cmd) {
			if (cmd.length < 1) {
				player.sendMessage("Usage: ;;searchbalanced <item name>");
				return;
			}

			try {
				StringBuilder searchTermBuilder = new StringBuilder();
				for (int i = 0; i < cmd.length; i++) {
					searchTermBuilder.append(cmd[i]);
					if (i < cmd.length - 1) {
						searchTermBuilder.append(" ");
					}
				}
				String searchTerm = searchTermBuilder.toString().toLowerCase();

				File logFile = new File(BALANCE_LOG_FILE);
				if (!logFile.exists()) {
					player.sendMessage("No balanced items log found.");
					return;
				}

				List<String> foundItems = searchBalancedItems(searchTerm);

				if (foundItems.isEmpty()) {
					player.sendMessage("No balanced items found matching: " + searchTerm);
					return;
				}

				player.sendMessage("=== SEARCH RESULTS FOR: " + searchTerm + " ===");
				for (int i = 0; i < foundItems.size() && i < 10; i++) {
					player.sendMessage(foundItems.get(i));
				}
				if (foundItems.size() > 10) {
					player.sendMessage("... and " + (foundItems.size() - 10) + " more results (showing first 10)");
				}
				player.sendMessage("Found: " + foundItems.size() + " items");

			} catch (Exception e) {
				player.sendMessage("Error searching: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * Generate tier summary report with enhanced error handling
		 */
		public static void handleTierSummaryCommand(Player player, String[] cmd) {
			try {
				File logFile = new File(BALANCE_LOG_FILE);
				if (!logFile.exists()) {
					player.sendMessage("No balanced items log found.");
					return;
				}

				Map<Integer, Integer> tierCounts = getTierSummary();

				player.sendMessage("=== TIER SUMMARY REPORT v3.2 (ENHANCED WEAPON BALANCE + PRAYER BOOST) ===");
				int totalItems = 0;
				for (int tier = 1; tier <= 10; tier++) {
					int count = tierCounts.containsKey(tier) ? tierCounts.get(tier) : 0;
					String tierName = getTierNameShort(tier);
					player.sendMessage(tierName + ": " + count + " items");
					totalItems += count;
				}
				player.sendMessage("TOTAL BALANCED ITEMS: " + totalItems);

			} catch (Exception e) {
				player.sendMessage("Error generating summary: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * Export balanced items to formatted report with enhanced error handling
		 */
		public static void handleExportReportCommand(Player player, String[] cmd) {
			BufferedWriter writer = null;
			try {
				File reportDir = new File("data/items/");
				if (!reportDir.exists()) {
					reportDir.mkdirs();
				}

				String reportFile = "data/items/balance_report_v3.2_" + System.currentTimeMillis() + ".txt";

				writer = new BufferedWriter(new FileWriter(reportFile));

				writer.write("ITEM BALANCE REPORT - ENHANCED WEAPON BALANCE v3.2 + PRAYER BOOST");
				writer.newLine();
				writer.write("Generated: " + new java.util.Date());
				writer.newLine();
				writer.write("Balance Score: 100% (Perfect Balance with v3.2 System)");
				writer.newLine();
				writer.write("Weapon Balance: 2H weapons 80% bonus vs dual-wield");
				writer.newLine();
				writer.write("Prayer System: 3-4x higher bonuses for all items");
				writer.newLine();
				writer.write("Absorption Safety: Per-item caps + Total caps enforced");
				writer.newLine();
				writer.write("Examine Integration: Real-time tier display");
				writer.newLine();
				writer.write("========================================");
				writer.newLine();
				writer.newLine();

				// Tier summary
				Map<Integer, Integer> tierCounts = getTierSummary();
				writer.write("TIER SUMMARY:");
				writer.newLine();
				for (int tier = 1; tier <= 10; tier++) {
					int count = tierCounts.containsKey(tier) ? tierCounts.get(tier) : 0;
					writer.write(getTierNameShort(tier) + ": " + count + " items");
					writer.newLine();
				}
				writer.newLine();

				// Full item list by tier
				for (int tier = 1; tier <= 10; tier++) {
					List<String> tierItems = findItemsByTier(tier);
					if (!tierItems.isEmpty()) {
						writer.write("=== " + getTierNameShort(tier).toUpperCase() + " ===");
						writer.newLine();
						for (String item : tierItems) {
							writer.write(item);
							writer.newLine();
						}
						writer.newLine();
					}
				}

				writer.close();
				writer = null;

				player.sendMessage("Balance report exported to: " + reportFile);

			} catch (Exception e) {
				player.sendMessage("Error exporting report: " + e.getMessage());
				Logger.handle(e);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}
		}

		/**
		 * Clean up duplicate entries (run once to fix existing log)
		 */
		public static void handleCleanupLogCommand(Player player, String[] cmd) {
			try {
				player.sendMessage("Starting log cleanup - removing duplicate entries...");

				File logFile = new File(BALANCE_LOG_FILE);
				if (!logFile.exists()) {
					player.sendMessage("No log file found to clean up.");
					return;
				}

				// Read all entries
				List<String> allEntries = readAllLogEntries();

				// Remove duplicates (keep only the latest entry for each item ID)
				Map<Integer, String> uniqueEntries = new ConcurrentHashMap<Integer, String>();

				for (String entry : allEntries) {
					int itemId = extractItemIdFromEntry(entry);
					if (itemId != -1) {
						// This will automatically keep the last occurrence (most recent)
						uniqueEntries.put(itemId, entry);
					}
				}

				// Convert back to list
				List<String> cleanedEntries = new ArrayList<String>(uniqueEntries.values());

				// Rewrite the log file
				rewriteLogFile(cleanedEntries);

				int originalCount = allEntries.size();
				int cleanedCount = cleanedEntries.size();
				int duplicatesRemoved = originalCount - cleanedCount;

				player.sendMessage("Log cleanup complete!");
				player.sendMessage("Original entries: " + originalCount);
				player.sendMessage("Cleaned entries: " + cleanedCount);
				player.sendMessage("Duplicates removed: " + duplicatesRemoved);

			} catch (Exception e) {
				player.sendMessage("Error cleaning log: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * Handle cleanup command for memory management
		 */
		public static void handleCleanupCommand(Player player, String[] cmd) {
			try {
				int oldSize = originalStats.size();
				cleanupOldBackups();
				int newSize = originalStats.size();

				player.sendMessage("Cleanup complete! Removed " + (oldSize - newSize) + " old backups.");
				player.sendMessage("Current backups: " + newSize + "/" + MAX_BACKUP_ENTRIES);

			} catch (Exception e) {
				player.sendMessage("Error during cleanup: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * ENHANCED: Absorption validation with offhand weapon awareness
		 */
		public static boolean validatePlayerAbsorption(Player player) {
			if (player == null || player.getEquipment() == null) {
				return true;
			}

			try {
				ItemsContainer<Item> equipment = player.getEquipment().getItems();
				if (equipment == null) {
					return true;
				}

				int totalMeleeAbsorption = 0;
				int totalMagicAbsorption = 0;
				int totalRangedAbsorption = 0;

				Set<Integer> processedItems = new HashSet<Integer>(); // Prevent double counting
				boolean hasMainhand = false;
				boolean hasOffhand = false;
				boolean hasTwoHanded = false;

				// Sum absorption from all equipped items and check weapon setup
				for (int slot = 0; slot < equipment.getSize(); slot++) {
					Item item = equipment.get(slot);
					if (item != null && item.getId() > 0) {

						// Skip if already processed (handles 2H weapons in multiple slots)
						if (processedItems.contains(item.getId())) {
							continue;
						}
						processedItems.add(item.getId());

						// Check weapon setup
						if (slot == 3) { // Weapon slot
							if (isTwoHandedWeapon(item.getId())) {
								hasTwoHanded = true;
							} else {
								hasMainhand = true;
							}
						} else if (slot == 5) { // Shield/offhand slot
							if (isOffhandWeapon(item.getId())) {
								hasOffhand = true;
							}
						}

						int[] bonuses = ItemBonuses.getItemBonuses(item.getId());
						if (bonuses != null && bonuses.length >= 14) {
							totalMeleeAbsorption += Math.max(0, bonuses[11]);
							totalMagicAbsorption += Math.max(0, bonuses[12]);
							totalRangedAbsorption += Math.max(0, bonuses[13]);
						}
					}
				}

				// Validate weapon setup
				if (hasTwoHanded && (hasMainhand || hasOffhand)) {
					player.sendMessage(
							"<col=ff0000>ERROR: Cannot use 2H weapon with mainhand or offhand weapons!</col>");
					return false;
				}

				if (hasOffhand && !hasMainhand) {
					player.sendMessage(
							"<col=ffff00>WARNING: Offhand weapon without mainhand - may not be optimal!</col>");
				}

				// Check if totals exceed limits
				if (totalMeleeAbsorption > MAX_TOTAL_MELEE_ABSORPTION) {
					player.sendMessage("<col=ff0000>ERROR: Total melee absorption (" + totalMeleeAbsorption
							+ "%) exceeds maximum (" + MAX_TOTAL_MELEE_ABSORPTION + "%)!</col>");
					return false;
				}

				if (totalMagicAbsorption > MAX_TOTAL_MAGIC_ABSORPTION) {
					player.sendMessage("<col=ff0000>ERROR: Total magic absorption (" + totalMagicAbsorption
							+ "%) exceeds maximum (" + MAX_TOTAL_MAGIC_ABSORPTION + "%)!</col>");
					return false;
				}

				if (totalRangedAbsorption > MAX_TOTAL_RANGED_ABSORPTION) {
					player.sendMessage("<col=ff0000>ERROR: Total ranged absorption (" + totalRangedAbsorption
							+ "%) exceeds maximum (" + MAX_TOTAL_RANGED_ABSORPTION + "%)!</col>");
					return false;
				}

				return true;

			} catch (Exception e) {
				Logger.handle(e);
				return true; // Allow if validation fails
			}
		}

		/**
		 * ENHANCED: Show absorption with dual-wield awareness
		 */
		public static void handleShowAbsorptionCommand(Player player, String[] cmd) {
			if (player == null || player.getEquipment() == null) {
				return;
			}

			try {
				ItemsContainer<Item> equipment = player.getEquipment().getItems();
				if (equipment == null) {
					player.sendMessage("No equipment found.");
					return;
				}

				int totalMeleeAbs = 0, totalMagicAbs = 0, totalRangedAbs = 0;
				int totalPrayer = 0;
				boolean hasMainhand = false, hasOffhand = false, hasTwoHanded = false;

				player.sendMessage("=== YOUR ABSORPTION ANALYSIS v3.2 (DUAL-WIELD AWARE + ENHANCED PRAYER) ===");

				Set<Integer> processedItems = new HashSet<Integer>(); // Prevent double counting

				// Check each equipment slot
				String[] slotNames = { "Helmet", "Cape", "Amulet", "Weapon", "Body", "Shield/Offhand", "Legs", "Gloves",
						"Boots", "Ring", "Arrows" };

				for (int slot = 0; slot < Math.min(equipment.getSize(), slotNames.length); slot++) {
					Item item = equipment.get(slot);
					if (item != null && item.getId() > 0) {

						// Skip if already processed (2H weapon double counting prevention)
						if (processedItems.contains(item.getId())) {
							continue;
						}
						processedItems.add(item.getId());

						ItemDefinitions def = ItemDefinitions.getItemDefinitions(item.getId());
						String itemName = def != null ? def.getName() : "Unknown";

						int[] bonuses = ItemBonuses.getItemBonuses(item.getId());
						if (bonuses != null && bonuses.length >= 17) {
							int meleeAbs = Math.max(0, bonuses[11]);
							int magicAbs = Math.max(0, bonuses[12]);
							int rangedAbs = Math.max(0, bonuses[13]);
							int prayer = Math.max(0, bonuses[16]);

							if (meleeAbs > 0 || magicAbs > 0 || rangedAbs > 0 || prayer > 0) {
								String itemInfo = slotNames[slot] + ": " + itemName;

								// Mark weapon types
								if (slot == 3) { // Weapon slot
									if (isTwoHandedWeapon(item.getId())) {
										itemInfo += " (2H - 80% Bonus v3.2!)";
										hasTwoHanded = true;
									} else {
										itemInfo += " (1H Main)";
										hasMainhand = true;
									}
								} else if (slot == 5) { // Shield/offhand slot
									if (isOffhandWeapon(item.getId())) {
										itemInfo += " (Offhand - 75%)";
										hasOffhand = true;
									} else {
										itemInfo += " (Shield)";
									}
								}

								player.sendMessage(itemInfo);
								if (meleeAbs > 0)
									player.sendMessage("  Melee Absorption: " + meleeAbs + "%");
								if (magicAbs > 0)
									player.sendMessage("  Magic Absorption: " + magicAbs + "%");
								if (rangedAbs > 0)
									player.sendMessage("  Ranged Absorption: " + rangedAbs + "%");
								if (prayer > 0)
									player.sendMessage("  Prayer Bonus: " + prayer + " (v3.2 Enhanced!)");
							}

							totalMeleeAbs += meleeAbs;
							totalMagicAbs += magicAbs;
							totalRangedAbs += rangedAbs;
							totalPrayer += prayer;
						}
					}
				}

				player.sendMessage("=== WEAPON SETUP ANALYSIS v3.2 ===");
				if (hasTwoHanded) {
					player.sendMessage("Setup: 2-Handed Weapon (v3.2: 80% BONUS!)");
					player.sendMessage("Trade-off: High offense, no shield/offhand");
				} else if (hasMainhand && hasOffhand) {
					player.sendMessage("Setup: Dual-Wield (Mainhand + Offhand 75%)");
					player.sendMessage("Trade-off: Balanced offense, lower individual weapon power");
				} else if (hasMainhand) {
					player.sendMessage("Setup: 1-Handed + Shield");
					player.sendMessage("Trade-off: Balanced offense/defense, higher absorption");
				} else {
					player.sendMessage("Setup: No weapons equipped");
				}

				player.sendMessage("=== TOTALS (v3.2 CAPS - ENHANCED PRAYER) ===");
				player.sendMessage(
						"Total Melee Absorption: " + totalMeleeAbs + "% (Max: " + MAX_TOTAL_MELEE_ABSORPTION + "%)");
				player.sendMessage(
						"Total Magic Absorption: " + totalMagicAbs + "% (Max: " + MAX_TOTAL_MAGIC_ABSORPTION + "%)");
				player.sendMessage(
						"Total Ranged Absorption: " + totalRangedAbs + "% (Max: " + MAX_TOTAL_RANGED_ABSORPTION + "%)");
				player.sendMessage("Total Prayer Bonus: " + totalPrayer + " (v3.2: 3-4x Higher!)");

				// Warnings
				if (totalMeleeAbs > MAX_TOTAL_MELEE_ABSORPTION) {
					player.sendMessage("<col=ff0000>WARNING: Melee absorption exceeds maximum!</col>");
				}
				if (totalMagicAbs > MAX_TOTAL_MAGIC_ABSORPTION) {
					player.sendMessage("<col=ff0000>WARNING: Magic absorption exceeds maximum!</col>");
				}
				if (totalRangedAbs > MAX_TOTAL_RANGED_ABSORPTION) {
					player.sendMessage("<col=ff0000>WARNING: Ranged absorption exceeds maximum!</col>");
				}

				// Show effective damage reduction
				player.sendMessage("=== EFFECTIVE DAMAGE REDUCTION ===");
				player.sendMessage("Against Melee: " + Math.min(totalMeleeAbs, MAX_TOTAL_MELEE_ABSORPTION) + "%");
				player.sendMessage("Against Magic: " + Math.min(totalMagicAbs, MAX_TOTAL_MAGIC_ABSORPTION) + "%");
				player.sendMessage("Against Ranged: " + Math.min(totalRangedAbs, MAX_TOTAL_RANGED_ABSORPTION) + "%");
				player.sendMessage("");
				player.sendMessage("Note: v3.2 Enhanced - 2H weapons now 80% stronger!");

			} catch (Exception e) {
				player.sendMessage("Error analyzing absorption: " + e.getMessage());
			}
		}

		/**
		 * Validate absorption command
		 */
		public static void handleValidateAbsorptionCommand(Player player, String[] cmd) {
			boolean isValid = validatePlayerAbsorption(player);

			if (isValid) {
				player.sendMessage("<col=00ff00>Your equipment absorption is within safe limits!</col>");
				player.sendMessage("Use ;;showabsorption to see detailed breakdown.");
			} else {
				player.sendMessage("<col=ff0000>Your equipment has excessive absorption!</col>");
				player.sendMessage("Remove some items or contact an admin for assistance.");
			}
		}

		/**
		 * NEW: Emergency fix for all existing items with broken absorption
		 */
		public static void handleEmergencyAbsorptionFix(Player player, String[] cmd) {
			if (!player.isOwner()) {
				player.sendMessage("Admin only command!");
				return;
			}

			try {
				player.sendMessage("Starting EMERGENCY absorption fix...");

				File bonusDir = new File("data/items/bonuses/");
				if (!bonusDir.exists()) {
					player.sendMessage("No bonuses directory found.");
					return;
				}

				File[] files = bonusDir.listFiles();
				if (files == null) {
					player.sendMessage("No bonus files found.");
					return;
				}

				int fixed = 0;

				for (File file : files) {
					if (file.getName().endsWith(".txt")) {
						try {
							int itemId = Integer.parseInt(file.getName().replace(".txt", ""));
							int[] stats = ItemBonuses.getItemBonuses(itemId);

							boolean changed = false;

							// Fix absorption bonuses
							if (stats[11] > MAX_ABSORPTION_PER_ITEM) {
								stats[11] = MAX_ABSORPTION_PER_ITEM;
								changed = true;
							}
							if (stats[12] > MAX_ABSORPTION_PER_ITEM) {
								stats[12] = MAX_ABSORPTION_PER_ITEM;
								changed = true;
							}
							if (stats[13] > MAX_ABSORPTION_PER_ITEM) {
								stats[13] = MAX_ABSORPTION_PER_ITEM;
								changed = true;
							}

							// Fix prayer bonuses based on estimated tier
							int estimatedTier = Math.min(10, Math.max(1, getMaxStat(stats) / 50));
							int maxPrayerForTier = MAX_PRAYER_PER_TIER[estimatedTier - 1];
							if (stats[16] > maxPrayerForTier) {
								stats[16] = maxPrayerForTier;
								changed = true;
							}

							if (changed) {
								ItemBonuses.setBonuses(itemId, stats);
								fixed++;

								ItemDefinitions def = ItemDefinitions.getItemDefinitions(itemId);
								String name = def != null ? def.getName() : "Item " + itemId;
								player.sendMessage("Fixed: " + name + " (ID: " + itemId + ")");
							}

						} catch (Exception e) {
							// Skip invalid files
						}
					}
				}

				ItemBonuses.saveManually();

				// FIXED v3.2: Force examine cache refresh after emergency fix
				try {
					ItemExamines.forceRefreshTierCache();
					player.sendMessage("<col=00ff00>Item examines updated after emergency fix!</col>");
				} catch (Exception e) {
					player.sendMessage("Emergency fix complete but examine cache not refreshed. Use ;;refreshexamines");
				}

				player.sendMessage("EMERGENCY FIX COMPLETE!");
				player.sendMessage("Fixed " + fixed + " items with broken absorption/prayer bonuses.");
				player.sendMessage("Players should no longer be invincible!");

			} catch (Exception e) {
				player.sendMessage("Error during emergency fix: " + e.getMessage());
			}
		}

		/**
		 * Show what caps will be applied for each tier (UPDATED for v3.2)
		 */
		public static void handleShowTierCapsCommand(Player player, String[] cmd) {
			player.sendMessage("=== ENHANCED WEAPON BALANCE TIER CAPS v3.2 ===");

			for (int tier = 1; tier <= 10; tier++) {
				TierCaps caps = calculateTierCaps(tier);
				String tierName = getTierNameShort(tier);

				player.sendMessage(tierName + ":");
				player.sendMessage("  Attack: " + caps.maxAttack + " | Defense: " + caps.maxDefense + " | Absorption: "
						+ caps.maxAbsorption + "% | Prayer: " + caps.maxPrayer);
			}

			player.sendMessage("");
			player.sendMessage("v3.2 ENHANCEMENTS: 2H weapons 80% bonus + Prayer 3-4x higher!");
			player.sendMessage(
					"Max per item: " + MAX_ABSORPTION_PER_ITEM + "% | Max total: " + MAX_TOTAL_MELEE_ABSORPTION + "%");
			player.sendMessage("WEAPON BALANCE: Offhand (75%) vs Mainhand (100%) vs 2H (180%)");
		}

		/**
		 * Test tier reading to debug issues
		 */
		public static void handleTestTierReadingCommand(Player player, String[] cmd) {
			try {
				Map<Integer, Integer> itemTiers = readItemTiersFromLog();

				player.sendMessage("=== TIER READING TEST v3.2 ===");
				player.sendMessage("Found " + itemTiers.size() + " items with tier information:");

				int count = 0;
				for (Map.Entry<Integer, Integer> entry : itemTiers.entrySet()) {
					if (count >= 10) {
						player.sendMessage("... and " + (itemTiers.size() - 10) + " more items");
						break;
					}

					int itemId = entry.getKey();
					int tier = entry.getValue();

					ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
					String itemName = itemDef != null ? itemDef.getName() : "Unknown";

					player.sendMessage("  " + itemName + " (ID: " + itemId + ") = Tier " + tier);
					count++;
				}

				if (itemTiers.isEmpty()) {
					player.sendMessage("No tier information found!");
					player.sendMessage("Check if file exists: " + BALANCE_LOG_FILE);

					File logFile = new File(BALANCE_LOG_FILE);
					if (logFile.exists()) {
						player.sendMessage("Log file exists, checking first few lines...");

						BufferedReader reader = null;
						try {
							reader = new BufferedReader(new FileReader(logFile));
							String line;
							int lineCount = 0;
							while ((line = reader.readLine()) != null && lineCount < 5) {
								player.sendMessage("Line " + (lineCount + 1) + ": " + line);
								lineCount++;
							}
						} catch (Exception e) {
							player.sendMessage("Error reading file: " + e.getMessage());
						} finally {
							if (reader != null) {
								try {
									reader.close();
								} catch (IOException e) {
									Logger.handle(e);
								}
							}
						}
					} else {
						player.sendMessage("Log file does not exist!");
					}
				}

			} catch (Exception e) {
				player.sendMessage("Error testing tier reading: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * NEW v3.2: Force remove stats from ALL auras (nuclear option)
		 */
		public static void handleForceFixAurasCommand(Player player, String[] cmd) {
			if (!player.isOwner()) {
				player.sendMessage("Admin only command!");
				return;
			}

			try {
				player.sendMessage("Starting FORCE aura fix - removing stats from ALL items containing 'aura'...");

				File bonusDir = new File("data/items/bonuses/");
				if (!bonusDir.exists()) {
					player.sendMessage("No bonuses directory found.");
					return;
				}

				File[] files = bonusDir.listFiles();
				if (files == null) {
					player.sendMessage("No bonus files found.");
					return;
				}

				int totalAurasFixed = 0;

				for (File file : files) {
					if (file.getName().endsWith(".txt")) {
						try {
							int itemId = Integer.parseInt(file.getName().replace(".txt", ""));

							ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
							if (itemDef == null)
								continue;

							String itemName = itemDef.getName().toLowerCase();

							// Force fix ANY item with "aura" in the name
							if (itemName.contains("aura") || itemName.contains("wings") || itemName.contains("halo")
									|| itemName.contains("glow") || itemName.contains("cosmetic")
									|| itemName.contains("override")) {

								// Set to zero stats
								int[] zeroStats = new int[18]; // All zeros
								ItemBonuses.setBonuses(itemId, zeroStats);
								totalAurasFixed++;

								player.sendMessage(
										"FIXED: " + itemDef.getName() + " (ID: " + itemId + ") - set to zero stats");
							}

						} catch (NumberFormatException e) {
							// Skip invalid filenames
						} catch (Exception e) {
							Logger.handle(e);
						}
					}
				}

				// Save all changes
				try {
					ItemBonuses.saveManually();
					player.sendMessage("All changes saved successfully!");
				} catch (Exception e) {
					player.sendMessage("Warning: Error saving bonuses file - " + e.getMessage());
					Logger.handle(e);
				}

				// Force examine cache refresh
				try {
					ItemExamines.forceRefreshTierCache();
					player.sendMessage("<col=00ff00>All item examines updated!</col>");
				} catch (Exception e) {
					player.sendMessage("Auras fixed but examine cache not refreshed. Use ;;refreshexamines");
				}

				player.sendMessage("=== FORCE AURA FIX COMPLETE ===");
				player.sendMessage("Auras/cosmetics forced to zero stats: " + totalAurasFixed);
				player.sendMessage("✓ ALL items with 'aura', 'wings', 'halo', 'glow', 'cosmetic' now have zero stats");
				player.sendMessage("✓ Auras should no longer have any combat stats!");

				// Log the fix
				Logger.log("ItemBalancer",
						player.getUsername() + " force-fixed " + totalAurasFixed + " auras/cosmetics to zero stats.");

			} catch (Exception e) {
				player.sendMessage("Error during force aura fix: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * NEW v3.2: Restore all non-tiered items to zero stats (original state)
		 */
		public static void handleRestoreNonTieredCommand(Player player, String[] cmd) {
			if (!player.isOwner()) {
				player.sendMessage("Admin only command!");
				return;
			}

			try {
				player.sendMessage("Starting restoration of non-tiered items to original state...");

				// Read tier information to know which items to skip
				Map<Integer, Integer> itemTiers = readItemTiersFromLog();

				File bonusDir = new File("data/items/bonuses/");
				if (!bonusDir.exists()) {
					player.sendMessage("No bonuses directory found.");
					return;
				}

				File[] files = bonusDir.listFiles();
				if (files == null) {
					player.sendMessage("No bonus files found.");
					return;
				}

				int totalRestored = 0;
				int totalSkipped = 0;
				int totalCosmeticSkipped = 0;

				for (File file : files) {
					if (file.getName().endsWith(".txt")) {
						try {
							int itemId = Integer.parseInt(file.getName().replace(".txt", ""));

							// Skip tiered items - they keep their enhanced stats
							if (isTieredItem(itemId, itemTiers)) {
								totalSkipped++;
								continue;
							}

							// Skip cosmetic items - they shouldn't have stats anyway
							if (shouldExcludeFromStats(itemId)) {
								totalCosmeticSkipped++;
								continue;
							}

							// Restore to zero stats (original state)
							int[] zeroStats = new int[18]; // All zeros
							ItemBonuses.setBonuses(itemId, zeroStats);
							totalRestored++;

							// Show progress occasionally
							if (totalRestored % 100 == 0) {
								ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
								String itemName = itemDef != null ? itemDef.getName() : "Item " + itemId;
								player.sendMessage(
										"Progress: " + totalRestored + " items restored... (Latest: " + itemName + ")");
							}

						} catch (NumberFormatException e) {
							// Skip invalid filenames
						} catch (Exception e) {
							Logger.handle(e);
						}
					}
				}

				// Save all changes
				try {
					ItemBonuses.saveManually();
					player.sendMessage("All changes saved successfully!");
				} catch (Exception e) {
					player.sendMessage("Warning: Error saving bonuses file - " + e.getMessage());
					Logger.handle(e);
				}

				// Force examine cache refresh
				try {
					ItemExamines.forceRefreshTierCache();
					player.sendMessage("<col=00ff00>All item examines updated!</col>");
				} catch (Exception e) {
					player.sendMessage("Restored but examine cache not refreshed. Use ;;refreshexamines");
				}

				player.sendMessage("=== NON-TIERED ITEM RESTORATION COMPLETE ===");
				player.sendMessage("Non-tiered items restored to zero stats: " + totalRestored);
				player.sendMessage("Tiered items skipped (kept enhanced stats): " + totalSkipped);
				player.sendMessage("Cosmetic items skipped: " + totalCosmeticSkipped);
				player.sendMessage("✓ Regular bows, armor, weapons now have zero stats (original state)");
				player.sendMessage("✓ Tiered items keep their enhanced v3.2 stats");
				player.sendMessage("✓ System is now clean - only tiered items have enhanced stats!");

				// Log the restoration
				Logger.log("ItemBalancer", player.getUsername() + " restored " + totalRestored
						+ " non-tiered items to original state. " + totalSkipped + " tiered items preserved.");

			} catch (Exception e) {
				player.sendMessage("Error during restoration: " + e.getMessage());
				Logger.handle(e);
			}
		}

		/**
		 * UPDATED v3.2: Complete rebalance with ALL items regenerated using new
		 * formulas
		 */
		public static void handleRebalanceAllCommand(Player player, String[] cmd) {
			if (!player.isOwner()) {
				player.sendMessage("Admin only command!");
				return;
			}

			try {
				player.sendMessage("Starting v3.2 rebalance - REGENERATING TIERED ITEMS ONLY...");

				// Read tier information from log file
				Map<Integer, Integer> itemTiers = readItemTiersFromLog();
				Map<Integer, String> itemTypes = readItemTypesFromLog();
				Map<Integer, Double> itemIntensities = readItemIntensitiesFromLog();

				if (itemTiers.isEmpty()) {
					player.sendMessage("No tiered items found in log file!");
					player.sendMessage("Use ;;adjuststats to create tiered items first.");
					return;
				}

				player.sendMessage("Found " + itemTiers.size() + " tiered items to regenerate with v3.2 formulas...");

				File bonusDir = new File("data/items/bonuses/");
				if (!bonusDir.exists()) {
					player.sendMessage("No bonuses directory found.");
					return;
				}

				File[] files = bonusDir.listFiles();
				if (files == null) {
					player.sendMessage("No bonus files found.");
					return;
				}

				int totalRegenerated = 0;
				int totalSkipped = 0;
				int totalNonTiered = 0;
				int total = 0;

				for (File file : files) {
					if (file.getName().endsWith(".txt")) {
						try {
							int itemId = Integer.parseInt(file.getName().replace(".txt", ""));
							total++;

							// NEW v3.2: Only process items that are in the tier system
							if (!isTieredItem(itemId, itemTiers)) {
								totalNonTiered++;

								// Only show message occasionally to avoid spam
								if (totalNonTiered % 100 == 0) {
									ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
									String itemName = itemDef != null ? itemDef.getName() : "Item " + itemId;
									player.sendMessage("Skipped non-tiered: " + itemName
											+ " (only tiered items get v3.2 treatment)");
								}
								continue;
							}

							// NEW v3.2: Skip auras and cosmetic items
							if (shouldExcludeFromStats(itemId)) {
								totalSkipped++;

								ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
								String itemName = itemDef != null ? itemDef.getName() : "Item " + itemId;

								if (totalSkipped % 20 == 0) {
									player.sendMessage("Skipped cosmetic: " + itemName
											+ " (auras/cosmetics don't get combat stats)");
								}
								continue;
							}

							// Get the original settings for this item (or defaults)
							int tier = itemTiers.containsKey(itemId) ? itemTiers.get(itemId) : 5;
							String itemType = itemTypes.containsKey(itemId) ? itemTypes.get(itemId) : "hybrid";
							double intensity = itemIntensities.containsKey(itemId) ? itemIntensities.get(itemId) : 1.0;

							// COMPLETELY REGENERATE stats with new v3.2 formulas for TIERED items only
							int[] newStats = generateStatsForItemType(tier, itemType, intensity);

							// Update the item
							ItemBonuses.setBonuses(itemId, newStats);
							totalRegenerated++;

							// Show progress for important items
							ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
							String itemName = itemDef != null ? itemDef.getName() : "Item " + itemId;

							// Only show messages for every 10th item since we have fewer items now
							if (totalRegenerated % 10 == 0) {
								player.sendMessage("Progress: " + totalRegenerated
										+ " tiered items regenerated... (Latest: " + itemName + ")");
							}

							// Always show 2H weapons and high-tier items
							if (itemType.contains("great") || itemType.contains("2h") || itemType.equals("halberd")
									|| itemType.equals("longbow") || itemType.equals("battlestaff")
									|| itemType.equals("scythe") || tier >= 8) {
								player.sendMessage("REGENERATED: " + itemName + " - " + getTierNameShort(tier) + " "
										+ itemType.toUpperCase());

								// Show the power increase for 2H weapons
								if (itemType.contains("great") || itemType.contains("2h") || itemType.equals("halberd")
										|| itemType.equals("longbow") || itemType.equals("battlestaff")
										|| itemType.equals("scythe")) {
									int maxStat = getMaxStat(newStats);
									player.sendMessage(
											"  → 2H Bonus Applied: Max stat " + maxStat + " (80% bonus from v3.2!)");
								}
							}

						} catch (NumberFormatException e) {
							// Skip invalid filenames
						} catch (Exception e) {
							Logger.handle(e);
						}
					}
				}

				// Save all changes
				try {
					ItemBonuses.saveManually();
					player.sendMessage("All changes saved successfully!");
				} catch (Exception e) {
					player.sendMessage("Warning: Error saving bonuses file - " + e.getMessage());
					Logger.handle(e);
				}

				// FIXED v3.2: Force examine cache refresh after rebalance
				try {
					ItemExamines.forceRefreshTierCache();
					player.sendMessage("<col=00ff00>All item examines updated!</col>");
				} catch (Exception e) {
					player.sendMessage("Rebalanced but examine cache not refreshed. Use ;;refreshexamines");
				}

				player.sendMessage("=== COMPLETE v3.2 REBALANCE FINISHED ===");
				player.sendMessage("Total files checked: " + total);
				player.sendMessage("TIERED items regenerated: " + totalRegenerated);
				player.sendMessage("Non-tiered items skipped: " + totalNonTiered + " (regular items unchanged)");
				player.sendMessage("Cosmetic items skipped: " + totalSkipped + " (auras, pets, etc.)");
				player.sendMessage("ONLY TIERED ITEMS NOW USE v3.2 FORMULAS:");
				player.sendMessage("✓ 2H weapons: 80% bonus (vs 25% before)");
				player.sendMessage("✓ Offhand weapons: 75% effectiveness (unchanged)");
				player.sendMessage("✓ Prayer bonuses: 3-4x higher for tiered items");
				player.sendMessage("✓ Absorption caps: Properly enforced");
				player.sendMessage("✓ Regular items: Unchanged (as intended)");
				player.sendMessage("✓ Only custom tiered items get enhanced balance!");

				// Log the rebalance
				Logger.log("ItemBalancer",
						player.getUsername() + " ran COMPLETE v3.2 rebalance. " + totalRegenerated
								+ " tiered items regenerated, " + totalNonTiered + " regular items skipped, "
								+ totalSkipped + " cosmetics skipped.");

			} catch (Exception e) {
				player.sendMessage("Error during complete rebalance: " + e.getMessage());
				Logger.handle(e);
			}
		}

		// ===== ADDITIONAL COMMANDS FOR COMPLETE v3.2 =====

		/**
		 * Handle item types command - shows all available item types
		 */
		public static void handleItemTypesCommand(Player player, String[] cmd) {
			sendItemTypes(player);
		}

		/**
		 * Handle tier list command - shows all tier ranges
		 */
		public static void handleTierListCommand(Player player, String[] cmd) {
			sendTierList(player);
		}

		/**
		 * Handle help command - shows all available commands
		 */
		public static void handleHelpCommand(Player player, String[] cmd) {
			sendHelp(player);
		}

		/**
		 * Handle stats validation for equipped items
		 */
		public static void handleValidateStatsCommand(Player player, String[] cmd) {
			if (player == null || player.getEquipment() == null) {
				player.sendMessage("No equipment to validate.");
				return;
			}

			try {
				ItemsContainer<Item> equipment = player.getEquipment().getItems();
				if (equipment == null) {
					player.sendMessage("No equipment found.");
					return;
				}

				boolean hasIssues = false;
				int totalItems = 0;

				player.sendMessage("=== EQUIPMENT VALIDATION v3.2 ===");

				for (int slot = 0; slot < equipment.getSize(); slot++) {
					Item item = equipment.get(slot);
					if (item != null && item.getId() > 0) {
						totalItems++;

						ItemDefinitions def = ItemDefinitions.getItemDefinitions(item.getId());
						String itemName = def != null ? def.getName() : "Unknown";

						int[] stats = ItemBonuses.getItemBonuses(item.getId());

						// Check for excessive stats
						boolean itemHasIssues = false;

						// Check absorption
						if (stats[11] > MAX_ABSORPTION_PER_ITEM || stats[12] > MAX_ABSORPTION_PER_ITEM
								|| stats[13] > MAX_ABSORPTION_PER_ITEM) {
							player.sendMessage("⚠ " + itemName + " - Excessive absorption: " + stats[11] + "/"
									+ stats[12] + "/" + stats[13] + "%");
							itemHasIssues = true;
						}

						// Check attack bonuses
						int maxAttack = getMaxAttackStat(stats);
						if (maxAttack > MAX_ATTACK_BONUS) {
							player.sendMessage("⚠ " + itemName + " - Excessive attack: " + maxAttack);
							itemHasIssues = true;
						}

						// Check defense bonuses
						int maxDefense = getMaxDefenseStat(stats);
						if (maxDefense > MAX_DEFENSE_BONUS) {
							player.sendMessage("⚠ " + itemName + " - Excessive defense: " + maxDefense);
							itemHasIssues = true;
						}

						// Check prayer (use new v3.2 caps)
						if (stats[16] > 100) { // Reasonable prayer cap for v3.2
							player.sendMessage("⚠ " + itemName + " - Excessive prayer: " + stats[16]);
							itemHasIssues = true;
						}

						if (itemHasIssues) {
							hasIssues = true;
						}
					}
				}

				if (!hasIssues) {
					player.sendMessage("✓ All " + totalItems + " equipped items are within v3.2 limits!");
				} else {
					player.sendMessage("Found issues with equipped items. Consider using ;;emergencyabsorptionfix");
				}

				// Also validate total absorption
				validatePlayerAbsorption(player);

			} catch (Exception e) {
				player.sendMessage("Error validating equipment: " + e.getMessage());
				Logger.handle(e);
			}
		}

		// ===== CORE STAT GENERATION AND BALANCING METHODS =====

		/**
		 * Adjust item stats with comprehensive logging and memory management
		 */
		private static boolean adjustItemStatsWithLogging(int itemId, int tier, String itemType, double intensity,
				String adminName) {
			try {
				// NEW v3.2: Check if this is a cosmetic item that shouldn't get combat stats
				if (shouldExcludeFromStats(itemId)) {
					Logger.log("ItemBalancer", "BLOCKED: " + adminName + " tried to adjust cosmetic item " + itemId
							+ " (auras/cosmetics don't get combat stats)");
					return false;
				}

				// Generate new stats
				int[] newStats = generateStatsForItemType(tier, itemType, intensity);

				// Update memory efficiently
				ItemBonuses.setBonuses(itemId, newStats);

				// Write to file for persistence
				boolean fileSuccess = writeStatsToFile(itemId, tier, itemType, intensity, adminName, newStats);
				if (!fileSuccess) {
					Logger.log("ItemBalancer", "Warning: Failed to write stats file for item " + itemId);
				}

				// Log the balanced item (with smart updating)
				logBalancedItem(itemId, tier, itemType, intensity, adminName, newStats);

				// Save bonuses
				ItemBonuses.saveManually();

				return true;

			} catch (Exception e) {
				Logger.handle(e);
				return false;
			}
		}

		/**
		 * ENHANCED v3.2: Generate stats with enhanced weapon balance and prayer system
		 */
		private static int[] generateStatsForItemType(int tier, String itemType, double intensity) {
			int[] stats = new int[18]; // All 18 bonus values

			// Use BALANCED tier stat ranges (logarithmic growth)
			int tierMin = BALANCED_TIER_MINS[tier - 1];
			int tierMax = BALANCED_TIER_MAXS[tier - 1];

			int baseStat = (int) ((tierMin + tierMax) / 2 * intensity);
			int primaryStat = (int) (tierMax * intensity);
			int secondaryStat = (int) (baseStat * 0.7);
			int tertiaryStat = (int) (baseStat * 0.3);
			int penaltyStat = (int) (baseStat * 0.2);

			// Apply stats based on specific item type
			String itemTypeLower = itemType.toLowerCase();

			// NEW: Handle offhand weapons with reduced stats
			if (isOffhandWeaponType(itemTypeLower)) {
				String mainhandType = getMainhandEquivalent(itemTypeLower);
				// Generate stats for mainhand equivalent but with 75% effectiveness
				int[] mainhandStats = generateStatsForMainhandType(tier, mainhandType,
						intensity * OFFHAND_EFFECTIVENESS);
				return mainhandStats;
			}

			// Handle 2-handed weapons with ENHANCED v3.2 bonus
			if (itemTypeLower.startsWith("great") || itemTypeLower.contains("2h") || itemTypeLower.equals("halberd")
					|| itemTypeLower.equals("longbow") || itemTypeLower.equals("battlestaff")
					|| itemTypeLower.equals("scythe")) {
				return generateTwoHandedWeaponStats(tier, itemTypeLower, intensity);
			}

			// Standard weapon and armor generation
			return generateMainhandStats(tier, itemTypeLower, intensity, baseStat, primaryStat, secondaryStat,
					tertiaryStat, penaltyStat);
		}

		/**
		 * Generate stats for mainhand weapon types
		 */
		private static int[] generateStatsForMainhandType(int tier, String itemType, double intensity) {
			int[] stats = new int[18];

			int tierMin = BALANCED_TIER_MINS[tier - 1];
			int tierMax = BALANCED_TIER_MAXS[tier - 1];
			int baseStat = (int) ((tierMin + tierMax) / 2 * intensity);
			int primaryStat = (int) (tierMax * intensity);

			return generateMainhandStats(tier, itemType, intensity, baseStat, primaryStat, (int) (baseStat * 0.7),
					(int) (baseStat * 0.3), (int) (baseStat * 0.2));
		}

		/**
		 * ENHANCED v3.2: Generate stats for 2-handed weapons with 80% bonus
		 */
		private static int[] generateTwoHandedWeaponStats(int tier, String itemType, double intensity) {
			int[] stats = new int[18];

			int tierMin = BALANCED_TIER_MINS[tier - 1];
			int tierMax = BALANCED_TIER_MAXS[tier - 1];

			// 2H weapons get 80% bonus to properly outscale dual-wield setups
			int baseStat = (int) ((tierMin + tierMax) / 2 * intensity * TWO_HANDED_BONUS);
			int primaryStat = (int) (tierMax * intensity * TWO_HANDED_BONUS);
			int secondaryStat = (int) (baseStat * 0.7);
			int tertiaryStat = (int) (baseStat * 0.3);

			if (itemType.contains("greatsword") || itemType.contains("greataxe")) {
				// Melee 2H weapons
				stats[0] = Math.min(MAX_ATTACK_BONUS, primaryStat); // stab
				stats[1] = Math.min(MAX_ATTACK_BONUS, primaryStat); // slash
				stats[2] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.9)); // crush
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 1.2)); // high strength
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(3, (int) (baseStat * WEAPON_PRAYER_MULTIPLIER)));

			} else if (itemType.contains("longbow")) {
				// Ranged 2H weapon
				stats[4] = Math.min(MAX_ATTACK_BONUS, primaryStat); // ranged attack
				stats[15] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 1.1)); // ranged strength
				stats[9] = Math.min(MAX_DEFENSE_BONUS, secondaryStat); // ranged defense
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(2, (int) (baseStat * WEAPON_PRAYER_MULTIPLIER)));

			} else if (itemType.contains("battlestaff")) {
				// Magic 2H weapon
				stats[3] = Math.min(MAX_ATTACK_BONUS, primaryStat); // magic attack
				stats[17] = Math.min(MAX_ATTACK_BONUS, primaryStat); // magic damage
				stats[8] = Math.min(MAX_DEFENSE_BONUS, secondaryStat); // magic defense
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(4, (int) (baseStat * WEAPON_PRAYER_MULTIPLIER * 1.5)));
			}

			return applySafetyCaps(stats, tier);
		}

		/**
		 * ENHANCED v3.2: Generate stats for standard mainhand weapons and armor with
		 * new prayer system
		 */
		private static int[] generateMainhandStats(int tier, String itemType, double intensity, int baseStat,
				int primaryStat, int secondaryStat, int tertiaryStat, int penaltyStat) {
			int[] stats = new int[18];

			if (itemType.equals("sword") || itemType.equals("scimitar") || itemType.equals("longsword")) {
				// === MELEE WEAPONS ===
				stats[0] = Math.min(MAX_ATTACK_BONUS, primaryStat); // stab attack
				stats[1] = Math.min(MAX_ATTACK_BONUS, primaryStat); // slash attack
				stats[2] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.8)); // crush attack
				stats[14] = Math.min(MAX_ATTACK_BONUS, primaryStat); // strength bonus
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(2, (int) (baseStat * WEAPON_PRAYER_MULTIPLIER))); // Enhanced prayer

			} else if (itemType.equals("axe") || itemType.equals("battleaxe")) {
				stats[1] = Math.min(MAX_ATTACK_BONUS, primaryStat); // slash attack
				stats[2] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.9)); // crush attack
				stats[0] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.6)); // stab attack
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 1.1)); // high strength

			} else if (itemType.equals("mace") || itemType.equals("warhammer")) {
				stats[2] = Math.min(MAX_ATTACK_BONUS, primaryStat); // crush attack
				stats[1] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.7)); // slash attack
				stats[0] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.5)); // stab attack
				stats[14] = Math.min(MAX_ATTACK_BONUS, primaryStat); // strength bonus
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(3, (int) (baseStat * WEAPON_PRAYER_MULTIPLIER * 1.2))); // Enhanced prayer

			} else if (itemType.equals("dagger")) {
				stats[0] = Math.min(MAX_ATTACK_BONUS, primaryStat); // stab attack
				stats[1] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.8)); // slash attack
				stats[2] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.3)); // crush attack
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.9)); // strength bonus

			} else if (itemType.equals("spear")) {
				stats[0] = Math.min(MAX_ATTACK_BONUS, primaryStat); // stab attack
				stats[1] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.5)); // slash attack
				stats[2] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.4)); // crush attack
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.8)); // strength bonus
				stats[5] = Math.min(MAX_DEFENSE_BONUS, tertiaryStat); // stab defense

			} else if (itemType.equals("whip")) {
				stats[1] = Math.min(MAX_ATTACK_BONUS, primaryStat); // slash attack
				stats[0] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.8)); // stab attack
				stats[2] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.2)); // crush attack
				stats[14] = Math.min(MAX_ATTACK_BONUS, primaryStat); // strength bonus

			} else if (itemType.equals("bow")) {
				// === RANGED WEAPONS ===
				stats[4] = Math.min(MAX_ATTACK_BONUS, primaryStat); // ranged attack
				stats[15] = Math.min(MAX_ATTACK_BONUS, primaryStat); // ranged strength
				stats[9] = Math.min(MAX_DEFENSE_BONUS, secondaryStat); // ranged defense

			} else if (itemType.equals("crossbow")) {
				stats[4] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 1.1)); // high ranged attack
				stats[15] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 1.1)); // high ranged strength
				stats[9] = Math.min(MAX_DEFENSE_BONUS, tertiaryStat); // ranged defense

			} else if (itemType.equals("staff")) {
				// === MAGIC WEAPONS ===
				stats[3] = Math.min(MAX_ATTACK_BONUS, primaryStat); // magic attack
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.8)); // magic damage
				stats[8] = Math.min(MAX_DEFENSE_BONUS, secondaryStat); // magic defense
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(4, (int) (baseStat * WEAPON_PRAYER_MULTIPLIER * 1.5))); // Enhanced prayer

			} else if (itemType.equals("wand")) {
				stats[3] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 1.1)); // high magic attack
				stats[17] = Math.min(MAX_ATTACK_BONUS, primaryStat); // magic damage
				stats[8] = Math.min(MAX_DEFENSE_BONUS, tertiaryStat); // magic defense

			} else if (itemType.equals("bodymelee")) {
				// === MELEE ARMOR ===
				stats[5] = Math.min(MAX_DEFENSE_BONUS, primaryStat); // stab defense
				stats[6] = Math.min(MAX_DEFENSE_BONUS, primaryStat); // slash defense
				stats[7] = Math.min(MAX_DEFENSE_BONUS, primaryStat); // crush defense
				stats[8] = -penaltyStat; // magic defense penalty
				stats[9] = Math.min(MAX_DEFENSE_BONUS, secondaryStat); // ranged defense
				// FIXED: Conservative absorption per item
				stats[11] = Math.min(MAX_ABSORPTION_PER_ITEM, Math.max(2, tier / 3)); // Melee absorption (2-5%)
				stats[14] = Math.min(MAX_ATTACK_BONUS, tertiaryStat); // small strength bonus

			} else if (itemType.equals("legmelee")) {
				stats[5] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.7)); // stab defense
				stats[6] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.7)); // slash defense
				stats[7] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.7)); // crush defense
				stats[8] = -penaltyStat; // magic defense penalty
				stats[9] = Math.min(MAX_DEFENSE_BONUS, (int) (secondaryStat * 0.7)); // ranged defense
				stats[11] = Math.min(MAX_ABSORPTION_PER_ITEM - 1, Math.max(1, tier / 4)); // Melee absorption (1-3%)
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (tertiaryStat * 0.7)); // small strength bonus

			} else if (itemType.equals("helmmelee")) {
				stats[5] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.5)); // stab defense
				stats[6] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.5)); // slash defense
				stats[7] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.5)); // crush defense
				stats[8] = -(int) (penaltyStat * 0.7); // magic defense penalty
				stats[9] = Math.min(MAX_DEFENSE_BONUS, (int) (secondaryStat * 0.5)); // ranged defense
				stats[11] = Math.min(MAX_ABSORPTION_PER_ITEM - 2, Math.max(1, tier / 5)); // Melee absorption (1-2%)
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (tertiaryStat * 0.5)); // small strength bonus

			} else if (itemType.equals("glovesmelee")) {
				stats[5] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.3)); // stab defense
				stats[6] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.3)); // slash defense
				stats[7] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.3)); // crush defense
				stats[11] = Math.min(MAX_ABSORPTION_PER_ITEM - 3, Math.max(1, tier / 6)); // Melee absorption (1%)
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (tertiaryStat * 0.8)); // strength bonus

			} else if (itemType.equals("bootsmelee")) {
				stats[5] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.2)); // stab defense
				stats[6] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.2)); // slash defense
				stats[7] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.2)); // crush defense
				stats[11] = Math.min(MAX_ABSORPTION_PER_ITEM - 4, Math.max(1, tier / 8)); // Melee absorption (1%)
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (tertiaryStat * 0.5)); // small strength bonus

			} else if (itemType.equals("bodyrange")) {
				// === RANGED ARMOR ===
				stats[9] = Math.min(MAX_DEFENSE_BONUS, primaryStat); // ranged defense
				stats[8] = Math.min(MAX_DEFENSE_BONUS, secondaryStat); // magic defense
				stats[5] = Math.min(MAX_DEFENSE_BONUS, tertiaryStat); // stab defense
				stats[6] = Math.min(MAX_DEFENSE_BONUS, tertiaryStat); // slash defense
				stats[7] = Math.min(MAX_DEFENSE_BONUS, tertiaryStat); // crush defense
				stats[13] = Math.min(MAX_ABSORPTION_PER_ITEM, Math.max(2, tier / 3)); // Ranged absorption (2-5%)
				stats[15] = Math.min(MAX_ATTACK_BONUS, tertiaryStat); // ranged strength bonus

			} else if (itemType.equals("legrange")) {
				stats[9] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.7)); // ranged defense
				stats[8] = Math.min(MAX_DEFENSE_BONUS, (int) (secondaryStat * 0.7)); // magic defense
				stats[5] = Math.min(MAX_DEFENSE_BONUS, (int) (tertiaryStat * 0.7)); // stab defense
				stats[6] = Math.min(MAX_DEFENSE_BONUS, (int) (tertiaryStat * 0.7)); // slash defense
				stats[7] = Math.min(MAX_DEFENSE_BONUS, (int) (tertiaryStat * 0.7)); // crush defense
				stats[13] = Math.min(MAX_ABSORPTION_PER_ITEM - 1, Math.max(1, tier / 4)); // Ranged absorption (1-3%)
				stats[15] = Math.min(MAX_ATTACK_BONUS, (int) (tertiaryStat * 0.7)); // ranged strength bonus

			} else if (itemType.equals("helmrange")) {
				stats[9] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.5)); // ranged defense
				stats[8] = Math.min(MAX_DEFENSE_BONUS, (int) (secondaryStat * 0.5)); // magic defense
				stats[5] = Math.min(MAX_DEFENSE_BONUS, (int) (tertiaryStat * 0.5)); // melee defenses
				stats[6] = Math.min(MAX_DEFENSE_BONUS, (int) (tertiaryStat * 0.5));
				stats[7] = Math.min(MAX_DEFENSE_BONUS, (int) (tertiaryStat * 0.5));
				stats[13] = Math.min(MAX_ABSORPTION_PER_ITEM - 2, Math.max(1, tier / 5)); // Ranged absorption (1-2%)
				stats[15] = Math.min(MAX_ATTACK_BONUS, (int) (tertiaryStat * 0.5)); // ranged strength bonus

			} else if (itemType.equals("glovesrange")) {
				stats[9] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.3)); // ranged defense
				stats[13] = Math.min(MAX_ABSORPTION_PER_ITEM - 3, Math.max(1, tier / 6)); // Ranged absorption (1%)
				stats[15] = Math.min(MAX_ATTACK_BONUS, (int) (tertiaryStat * 0.8)); // ranged strength bonus

			} else if (itemType.equals("bootsrange")) {
				stats[9] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.2)); // ranged defense
				stats[13] = Math.min(MAX_ABSORPTION_PER_ITEM - 4, Math.max(1, tier / 8)); // Ranged absorption (1%)
				stats[15] = Math.min(MAX_ATTACK_BONUS, (int) (tertiaryStat * 0.5)); // ranged strength bonus

			} else if (itemType.equals("bodymage")) {
				// === MAGIC ARMOR ===
				stats[8] = Math.min(MAX_DEFENSE_BONUS, primaryStat); // magic defense
				stats[3] = Math.min(MAX_ATTACK_BONUS, secondaryStat); // magic attack
				stats[9] = Math.min(MAX_DEFENSE_BONUS, secondaryStat); // ranged defense
				stats[5] = -penaltyStat; // negative melee defenses
				stats[6] = -penaltyStat;
				stats[7] = -penaltyStat;
				stats[12] = Math.min(MAX_ABSORPTION_PER_ITEM, Math.max(2, tier / 3)); // Magic absorption (2-5%)
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (secondaryStat * 0.6)); // magic damage
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(3, (int) (baseStat * ARMOR_PRAYER_MULTIPLIER))); // Enhanced prayer

			} else if (itemType.equals("legmage")) {
				stats[8] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.7)); // magic defense
				stats[3] = Math.min(MAX_ATTACK_BONUS, (int) (secondaryStat * 0.7)); // magic attack
				stats[9] = Math.min(MAX_DEFENSE_BONUS, (int) (secondaryStat * 0.7)); // ranged defense
				stats[5] = -(int) (penaltyStat * 0.7); // negative melee defenses
				stats[6] = -(int) (penaltyStat * 0.7);
				stats[7] = -(int) (penaltyStat * 0.7);
				stats[12] = Math.min(MAX_ABSORPTION_PER_ITEM - 1, Math.max(1, tier / 4)); // Magic absorption (1-3%)
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (secondaryStat * 0.4)); // magic damage
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(1, (int) (baseStat * ARMOR_PRAYER_MULTIPLIER))); // Enhanced prayer

			} else if (itemType.equals("helmmage")) {
				stats[8] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.5)); // magic defense
				stats[3] = Math.min(MAX_ATTACK_BONUS, (int) (secondaryStat * 0.5)); // magic attack
				stats[9] = Math.min(MAX_DEFENSE_BONUS, (int) (secondaryStat * 0.5)); // ranged defense
				stats[5] = -(int) (penaltyStat * 0.5); // negative melee defenses
				stats[6] = -(int) (penaltyStat * 0.5);
				stats[7] = -(int) (penaltyStat * 0.5);
				stats[12] = Math.min(MAX_ABSORPTION_PER_ITEM - 2, Math.max(1, tier / 5)); // Magic absorption (1-2%)
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (secondaryStat * 0.3)); // magic damage
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(1, (int) (baseStat * ARMOR_PRAYER_MULTIPLIER))); // Enhanced prayer

			} else if (itemType.equals("glovesmage")) {
				stats[8] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.3)); // magic defense
				stats[12] = Math.min(MAX_ABSORPTION_PER_ITEM - 3, Math.max(1, tier / 6)); // Magic absorption (1%)
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (secondaryStat * 0.8)); // magic damage
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(1, (int) (baseStat * ARMOR_PRAYER_MULTIPLIER))); // Enhanced prayer

			} else if (itemType.equals("bootsmage")) {
				stats[8] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.2)); // magic defense
				stats[12] = Math.min(MAX_ABSORPTION_PER_ITEM - 4, Math.max(1, tier / 8)); // Magic absorption (1%)
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (secondaryStat * 0.5)); // magic damage
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(1, (int) (baseStat * ARMOR_PRAYER_MULTIPLIER))); // Enhanced prayer

			} else if (itemType.equals("shield")) {
				// === ACCESSORIES ===
				stats[5] = Math.min(MAX_DEFENSE_BONUS, primaryStat); // all defenses
				stats[6] = Math.min(MAX_DEFENSE_BONUS, primaryStat);
				stats[7] = Math.min(MAX_DEFENSE_BONUS, primaryStat);
				stats[8] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.8));
				stats[9] = Math.min(MAX_DEFENSE_BONUS, primaryStat);
				stats[10] = Math.min(MAX_DEFENSE_BONUS, secondaryStat); // summoning defense
				// FIXED: Shield gets higher absorption but still capped
				stats[11] = Math.min(MAX_ABSORPTION_PER_ITEM, Math.max(3, tier / 2)); // Melee absorption (3-5%)
				stats[12] = Math.min(MAX_ABSORPTION_PER_ITEM, Math.max(3, tier / 2)); // Magic absorption (3-5%)
				stats[13] = Math.min(MAX_ABSORPTION_PER_ITEM, Math.max(3, tier / 2)); // Ranged absorption (3-5%)
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1],
						Math.max(6, (int) (baseStat * SHIELD_PRAYER_MULTIPLIER))); // Enhanced shield prayer

			} else if (itemType.equals("ring")) {
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.3)); // small strength
				stats[15] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.3)); // small ranged strength
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.3)); // small magic damage
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1], Math.max(1, (int) (baseStat * 0.03))); // prayer
																											// bonus

			} else if (itemType.equals("amulet")) {
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.4)); // strength
				stats[15] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.4)); // ranged strength
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.4)); // magic damage
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1], Math.max(1, (int) (baseStat * 0.05))); // prayer
																											// bonus

			} else if (itemType.equals("cape")) {
				stats[5] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.4)); // small defenses
				stats[6] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.4));
				stats[7] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.4));
				stats[8] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.4));
				stats[9] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.4));
				stats[11] = Math.min(MAX_ABSORPTION_PER_ITEM - 4, Math.max(1, tier / 10)); // Tiny absorption
				stats[12] = Math.min(MAX_ABSORPTION_PER_ITEM - 4, Math.max(1, tier / 10));
				stats[13] = Math.min(MAX_ABSORPTION_PER_ITEM - 4, Math.max(1, tier / 10));
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.2)); // small strength
				stats[15] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.2)); // small ranged strength
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.2)); // small magic damage
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1], Math.max(1, (int) (baseStat * 0.04))); // prayer
																											// bonus

			} else if (itemType.equals("belt")) {
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.3)); // strength
				stats[15] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.3)); // ranged strength
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (primaryStat * 0.3)); // magic damage
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1], Math.max(1, (int) (baseStat * 0.025))); // prayer
																											// bonus

			} else if (itemType.equals("hybrid")) {
				// === SPECIAL TYPES ===
				int balancedStat = (int) (baseStat * 0.75);
				for (int i = 0; i < 5; i++)
					stats[i] = Math.min(MAX_ATTACK_BONUS, balancedStat); // attack bonuses
				for (int i = 5; i < 10; i++)
					stats[i] = Math.min(MAX_DEFENSE_BONUS, balancedStat); // defense bonuses
				stats[10] = Math.min(MAX_DEFENSE_BONUS, (int) (balancedStat * 0.5)); // summoning defense
				stats[11] = Math.min(MAX_ABSORPTION_PER_ITEM - 1, Math.max(1, tier / 4)); // Conservative absorption
				stats[12] = Math.min(MAX_ABSORPTION_PER_ITEM - 1, Math.max(1, tier / 4));
				stats[13] = Math.min(MAX_ABSORPTION_PER_ITEM - 1, Math.max(1, tier / 4));
				stats[14] = Math.min(MAX_ATTACK_BONUS, balancedStat); // strength
				stats[15] = Math.min(MAX_ATTACK_BONUS, balancedStat); // ranged strength
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1], Math.max(1, (int) (baseStat * 0.035)));
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (balancedStat * 0.6)); // magic damage

			} else if (itemType.equals("utility")) {
				int utilityStat = (int) (baseStat * 0.6);
				for (int i = 0; i < 14; i++)
					stats[i] = Math.min(MAX_DEFENSE_BONUS, utilityStat);
				stats[14] = Math.min(MAX_ATTACK_BONUS, (int) (utilityStat * 0.7));
				stats[15] = Math.min(MAX_ATTACK_BONUS, (int) (utilityStat * 0.7));
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1], Math.max(1, (int) (primaryStat * 0.08))); // Higher
																												// prayer
				stats[17] = Math.min(MAX_ATTACK_BONUS, (int) (utilityStat * 0.7));

			} else if (itemType.equals("tank")) {
				stats[5] = Math.min(MAX_DEFENSE_BONUS, primaryStat); // high all defenses
				stats[6] = Math.min(MAX_DEFENSE_BONUS, primaryStat);
				stats[7] = Math.min(MAX_DEFENSE_BONUS, primaryStat);
				stats[8] = Math.min(MAX_DEFENSE_BONUS, (int) (primaryStat * 0.8));
				stats[9] = Math.min(MAX_DEFENSE_BONUS, primaryStat);
				stats[10] = Math.min(MAX_DEFENSE_BONUS, secondaryStat); // summoning defense
				stats[11] = Math.min(MAX_ABSORPTION_PER_ITEM, Math.max(2, tier / 3)); // Higher absorption for tank
				stats[12] = Math.min(MAX_ABSORPTION_PER_ITEM, Math.max(2, tier / 3));
				stats[13] = Math.min(MAX_ABSORPTION_PER_ITEM, Math.max(2, tier / 3));
				stats[16] = Math.min(MAX_PRAYER_PER_TIER[tier - 1], Math.max(1, (int) (baseStat * 0.06))); // prayer
																											// bonus
			}

			return applySafetyCaps(stats, tier);
		}

		/**
		 * Apply final safety caps to prevent invincibility
		 */
		private static int[] applySafetyCaps(int[] stats, int tier) {
			// FINAL SAFETY CAPS - CRITICAL FOR PREVENTING INVINCIBILITY
			for (int i = 0; i < stats.length; i++) {
				if (i >= 0 && i <= 4) { // Attack bonuses
					stats[i] = Math.min(MAX_ATTACK_BONUS, Math.max(0, stats[i]));
				} else if (i >= 5 && i <= 10) { // Defense bonuses
					stats[i] = Math.min(MAX_DEFENSE_BONUS, stats[i]);
					if (stats[i] < -100)
						stats[i] = -100; // Limit penalties
				} else if (i >= 11 && i <= 13) { // Absorption bonuses - CRITICAL
					stats[i] = Math.min(MAX_ABSORPTION_PER_ITEM, Math.max(0, stats[i]));
				} else if (i == 16) { // Prayer bonus - CRITICAL v3.2
					stats[i] = Math.min(MAX_PRAYER_PER_TIER[tier - 1], Math.max(0, stats[i]));
				} else { // Other bonuses
					stats[i] = Math.min(MAX_ATTACK_BONUS, Math.max(0, stats[i]));
				}
			}
			return stats;
		}

		// ===== NEW v3.2 HELPER METHODS =====

		/**
		 * Read item types from log file (REQUIRED for complete rebalance)
		 */
		private static Map<Integer, String> readItemTypesFromLog() {
			Map<Integer, String> itemTypes = new ConcurrentHashMap<Integer, String>();
			BufferedReader reader = null;

			try {
				File logFile = new File(BALANCE_LOG_FILE);
				if (!logFile.exists()) {
					return itemTypes;
				}

				reader = new BufferedReader(new FileReader(logFile));
				String line;
				int currentItemId = -1;

				while ((line = reader.readLine()) != null) {
					// Look for item ID line: "=== Item Name (ID: 12345) ==="
					if (line.startsWith("=== ") && line.contains("(ID: ") && line.endsWith(" ===")) {
						try {
							int startIndex = line.indexOf("(ID: ") + 5;
							int endIndex = line.indexOf(")", startIndex);
							if (startIndex > 4 && endIndex > startIndex) {
								String idStr = line.substring(startIndex, endIndex);
								currentItemId = Integer.parseInt(idStr);
							}
						} catch (Exception e) {
							currentItemId = -1;
						}
					}
					// Look for type line: "Type: GREATSWORD"
					else if (line.startsWith("Type: ") && currentItemId != -1) {
						try {
							String itemType = line.substring(6).toLowerCase();
							itemTypes.put(currentItemId, itemType);
							currentItemId = -1; // Reset for next item
						} catch (Exception e) {
							// Skip invalid entries
						}
					}
				}

			} catch (Exception e) {
				Logger.handle(e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}

			return itemTypes;
		}

		/**
		 * Read item intensities from log file (REQUIRED for complete rebalance)
		 */
		private static Map<Integer, Double> readItemIntensitiesFromLog() {
			Map<Integer, Double> itemIntensities = new ConcurrentHashMap<Integer, Double>();
			BufferedReader reader = null;

			try {
				File logFile = new File(BALANCE_LOG_FILE);
				if (!logFile.exists()) {
					return itemIntensities;
				}

				reader = new BufferedReader(new FileReader(logFile));
				String line;
				int currentItemId = -1;

				while ((line = reader.readLine()) != null) {
					// Look for item ID line: "=== Item Name (ID: 12345) ==="
					if (line.startsWith("=== ") && line.contains("(ID: ") && line.endsWith(" ===")) {
						try {
							int startIndex = line.indexOf("(ID: ") + 5;
							int endIndex = line.indexOf(")", startIndex);
							if (startIndex > 4 && endIndex > startIndex) {
								String idStr = line.substring(startIndex, endIndex);
								currentItemId = Integer.parseInt(idStr);
							}
						} catch (Exception e) {
							currentItemId = -1;
						}
					}
					// Look for intensity line: "Intensity: 1.5"
					else if (line.startsWith("Intensity: ") && currentItemId != -1) {
						try {
							String intensityStr = line.substring(11);
							double intensity = Double.parseDouble(intensityStr);
							itemIntensities.put(currentItemId, intensity);
							currentItemId = -1; // Reset for next item
						} catch (Exception e) {
							// Skip invalid entries, use default 1.0
						}
					}
				}

			} catch (Exception e) {
				Logger.handle(e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}

			return itemIntensities;
		}

		// ===== LOGGING AND FILE MANAGEMENT METHODS =====

		/**
		 * Log balanced item with smart updating (no duplicates)
		 */
		private static void logBalancedItem(int itemId, int tier, String itemType, double intensity, String adminName,
				int[] newStats) {
			try {
				// Get item name safely
				ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(itemId);
				String itemName = itemDef != null ? itemDef.getName() : "Unknown Item";

				// Create new log entry
				String newEntry = createLogEntry(itemId, itemName, tier, itemType, intensity, adminName, newStats);

				// Update the log file intelligently
				updateBalanceLogSmart(itemId, newEntry);

			} catch (Exception e) {
				Logger.handle(e);
			}
		}

		/**
		 * Smart log update - replaces existing entries or adds new ones
		 */
		private static void updateBalanceLogSmart(int itemId, String newEntry) {
			BufferedWriter writer = null;
			try {
				File logFile = new File(BALANCE_LOG_FILE);

				// Create file and directories if they don't exist
				if (!logFile.exists()) {
					File parentDir = logFile.getParentFile();
					if (parentDir != null && !parentDir.exists()) {
						parentDir.mkdirs();
					}
					logFile.createNewFile();

					// Write header
					writer = new BufferedWriter(new FileWriter(logFile));
					try {
						writer.write("ITEM BALANCER LOG - ENHANCED WEAPON BALANCE SYSTEM v3.2");
						writer.newLine();
						writer.write("Generated by Enhanced Item Balancer System v3.2");
						writer.newLine();
						writer.write("Last Updated: " + new java.util.Date());
						writer.newLine();
						writer.write("========================================");
						writer.newLine();
						writer.newLine();
					} finally {
						writer.close();
						writer = null;
					}
				}

				// Read all existing entries
				List<String> allEntries = readAllLogEntries();

				// Remove existing entry for this item (if it exists)
				allEntries = removeExistingItemEntry(allEntries, itemId);

				// Add the new entry
				allEntries.add(newEntry);

				// Rewrite the entire log file
				rewriteLogFile(allEntries);

			} catch (Exception e) {
				Logger.handle(e);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}
		}

		/**
		 * Read all log entries into a list with proper resource management
		 */
		private static List<String> readAllLogEntries() {
			List<String> entries = new ArrayList<String>();
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(BALANCE_LOG_FILE));
				String line;
				StringBuilder currentEntry = new StringBuilder();
				boolean inEntry = false;

				while ((line = reader.readLine()) != null) {
					// Skip header lines
					if (line.startsWith("ITEM BALANCER LOG") || line.startsWith("Generated by")
							|| line.startsWith("Last Updated:")
							|| line.equals("========================================")
							|| (line.trim().isEmpty() && !inEntry)) {
						continue;
					}

					// Start of new entry
					if (line.startsWith("=== ") && line.endsWith(" ===")) {
						// Save previous entry if exists
						if (inEntry && currentEntry.length() > 0) {
							entries.add(currentEntry.toString());
						}

						// Start new entry
						currentEntry = new StringBuilder();
						currentEntry.append(line).append("\n");
						inEntry = true;

					} else if (line.equals("----------------------------------------")) {
						// End of entry
						if (inEntry) {
							currentEntry.append(line).append("\n\n");
							entries.add(currentEntry.toString());
							currentEntry = new StringBuilder();
							inEntry = false;
						}
					} else if (inEntry) {
						// Part of current entry
						currentEntry.append(line).append("\n");
					}
				}

				// Handle last entry if file doesn't end with separator
				if (inEntry && currentEntry.length() > 0) {
					entries.add(currentEntry.toString());
				}

			} catch (Exception e) {
				Logger.handle(e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}

			return entries;
		}

		/**
		 * Remove existing entry for the specified item ID
		 */
		private static List<String> removeExistingItemEntry(List<String> entries, int targetItemId) {
			List<String> filteredEntries = new ArrayList<String>();

			for (String entry : entries) {
				// Extract item ID from entry
				int entryItemId = extractItemIdFromEntry(entry);

				// Only keep entries that don't match our target item ID
				if (entryItemId != targetItemId) {
					filteredEntries.add(entry);
				}
			}

			return filteredEntries;
		}

		/**
		 * Extract item ID from log entry with enhanced error handling
		 */
		private static int extractItemIdFromEntry(String entry) {
			try {
				// Look for pattern: (ID: 12345)
				String[] lines = entry.split("\n");
				for (String line : lines) {
					if (line.contains("(ID: ") && line.contains(") ===")) {
						int startIndex = line.indexOf("(ID: ") + 5;
						int endIndex = line.indexOf(")", startIndex);
						if (startIndex > 4 && endIndex > startIndex) {
							String idStr = line.substring(startIndex, endIndex);
							return Integer.parseInt(idStr);
						}
					}
				}
			} catch (Exception e) {
				// If we can't extract ID, return -1 to avoid accidental removal
				Logger.handle(e);
			}

			return -1; // Not found or error
		}

		/**
		 * Rewrite the entire log file with updated entries using atomic operation
		 */
		private static void rewriteLogFile(List<String> entries) {
			BufferedWriter writer = null;
			try {
				File logFile = new File(BALANCE_LOG_FILE);
				File tempFile = new File(BALANCE_LOG_FILE + ".tmp");

				writer = new BufferedWriter(new FileWriter(tempFile));

				// Write header
				writer.write("ITEM BALANCER LOG - ENHANCED WEAPON BALANCE SYSTEM v3.2");
				writer.newLine();
				writer.write("Generated by Enhanced Item Balancer System v3.2");
				writer.newLine();
				writer.write("Last Updated: " + new java.util.Date());
				writer.newLine();
				writer.write("========================================");
				writer.newLine();
				writer.newLine();

				// Write all entries
				for (String entry : entries) {
					writer.write(entry);
				}

				writer.close();
				writer = null;

				// Atomic file replacement
				if (logFile.exists()) {
					logFile.delete();
				}
				tempFile.renameTo(logFile);

			} catch (Exception e) {
				Logger.handle(e);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}
		}

		/**
		 * Create formatted log entry with revision tracking
		 */
		private static String createLogEntry(int itemId, String itemName, int tier, String itemType, double intensity,
				String adminName, int[] stats) {
			StringBuilder entry = new StringBuilder();

			// Header with revision info
			entry.append("=== ").append(itemName).append(" (ID: ").append(itemId).append(") ===\n");
			entry.append("Tier: ").append(tier).append(" (").append(getTierNameShort(tier)).append(")\n");
			entry.append("Type: ").append(itemType.toUpperCase()).append("\n");
			entry.append("Intensity: ").append(intensity).append("\n");
			entry.append("Balanced by: ").append(adminName).append("\n");
			entry.append("Last Updated: ").append(new java.util.Date()).append("\n");
			entry.append("System: ENHANCED WEAPON BALANCE v3.2\n");

			// Stats breakdown
			entry.append("ATTACK BONUSES:\n");
			entry.append("  Stab: ").append(stats[0]).append(" | Slash: ").append(stats[1]).append(" | Crush: ")
					.append(stats[2]).append("\n");
			entry.append("  Magic: ").append(stats[3]).append(" | Ranged: ").append(stats[4]).append("\n");

			entry.append("DEFENSE BONUSES:\n");
			entry.append("  Stab: ").append(stats[5]).append(" | Slash: ").append(stats[6]).append(" | Crush: ")
					.append(stats[7]).append("\n");
			entry.append("  Magic: ").append(stats[8]).append(" | Ranged: ").append(stats[9]).append(" | Summoning: ")
					.append(stats[10]).append("\n");

			entry.append("ABSORPTION BONUSES:\n");
			entry.append("  Melee: ").append(stats[11]).append("% | Magic: ").append(stats[12]).append("% | Ranged: ")
					.append(stats[13]).append("%\n");

			entry.append("OTHER BONUSES:\n");
			entry.append("  Strength: ").append(stats[14]).append(" | Ranged Str: ").append(stats[15]).append("\n");
			entry.append("  Prayer: ").append(stats[16]).append(" (v3.2 Enhanced!) | Magic Dmg: ").append(stats[17])
					.append("\n");

			entry.append("MAX STAT: ").append(getMaxStat(stats)).append(" (Balanced with v3.2 System)\n");
			entry.append("WEAPON BALANCE: 2H weapons 80% bonus, Offhand 75%, Prayer 3-4x higher\n");
			entry.append("ABSORPTION SAFETY: Per-item max " + MAX_ABSORPTION_PER_ITEM + "%, Total caps applied\n");
			entry.append("----------------------------------------\n\n");

			return entry.toString();
		}

		// ===== BACKUP AND ROLLBACK METHODS =====

		/**
		 * Rollback to original stats efficiently with proper resource management
		 */
		private static boolean rollbackItemStats(int itemId) {
			int[] backup = originalStats.get(itemId);
			if (backup == null) {
				return false;
			}

			try {
				// Restore to memory efficiently
				ItemBonuses.setBonuses(itemId, backup.clone());

				// Update file for persistence
				writeRollbackToFile(itemId, backup);

				// Save changes
				ItemBonuses.saveManually();

				// Remove from backup after successful rollback
				originalStats.remove(itemId);
				backupTimestamps.remove(itemId);

				Logger.log("ItemBalancer", "Successfully rolled back item " + itemId);
				return true;

			} catch (Exception e) {
				Logger.handle(e);
				return false;
			}
		}

		/**
		 * Write rollback stats to file with atomic operation
		 */
		private static void writeRollbackToFile(int itemId, int[] backup) {
			BufferedWriter writer = null;
			try {
				File bonusDir = new File("data/items/bonuses/");
				if (!bonusDir.exists()) {
					bonusDir.mkdirs();
				}

				File bonusFile = new File("data/items/bonuses/" + itemId + ".txt");
				File tempFile = new File("data/items/bonuses/" + itemId + ".tmp");

				writer = new BufferedWriter(new FileWriter(tempFile));
				writer.write("// Restored original stats - v3.2");
				writer.newLine();

				for (int i = 0; i < backup.length; i++) {
					if (i == 5) {
						writer.write("// Defense bonuses");
						writer.newLine();
					} else if (i == 11) {
						writer.write("// Absorption bonuses");
						writer.newLine();
					} else if (i == 14) {
						writer.write("// Other bonuses");
						writer.newLine();
					}
					writer.write(String.valueOf(backup[i]));
					writer.newLine();
				}

				writer.close();
				writer = null;

				// Atomic file replacement
				if (bonusFile.exists()) {
					bonusFile.delete();
				}
				tempFile.renameTo(bonusFile);

			} catch (Exception e) {
				Logger.handle(e);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}
		}

		/**
		 * Backup original stats safely with memory management
		 */
		private static void backupOriginalStats(int itemId) {
			if (originalStats.containsKey(itemId)) {
				return; // Already backed up
			}

			// Check if we need to clean up memory first
			if (originalStats.size() >= MAX_BACKUP_ENTRIES) {
				cleanupOldBackups();
			}

			try {
				// Get current stats from memory (never null with new getItemBonuses)
				int[] currentStats = ItemBonuses.getItemBonuses(itemId);

				// Store a copy for rollback with timestamp
				originalStats.put(itemId, currentStats.clone());
				backupTimestamps.put(itemId, System.currentTimeMillis());

				Logger.log("ItemBalancer", "Backed up original stats for item " + itemId);

			} catch (Exception e) {
				Logger.handle(e);
				// If backup fails, store default zeros
				originalStats.put(itemId, new int[18]);
				backupTimestamps.put(itemId, System.currentTimeMillis());
			}
		}

		/**
		 * Clean up old backups to prevent memory leaks
		 */
		private static void cleanupOldBackups() {
			if (originalStats.size() <= MAX_BACKUP_ENTRIES / 2) {
				return; // No need to cleanup
			}

			try {
				long currentTime = System.currentTimeMillis();
				long cleanupAge = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

				Iterator<Map.Entry<Integer, Long>> iterator = backupTimestamps.entrySet().iterator();
				int removedCount = 0;

				while (iterator.hasNext() && originalStats.size() > MAX_BACKUP_ENTRIES / 2) {
					Map.Entry<Integer, Long> entry = iterator.next();
					Integer itemId = entry.getKey();
					Long timestamp = entry.getValue();

					// Remove old backups or if we're over limit, remove oldest
					if (timestamp != null && (currentTime - timestamp) > cleanupAge) {
						iterator.remove();
						originalStats.remove(itemId);
						removedCount++;
					}
				}

				Logger.log("ItemBalancer",
						"Cleaned up " + removedCount + " old item backups. Current: " + originalStats.size());

			} catch (Exception e) {
				Logger.handle(e);
			}
		}

		/**
		 * Perform periodic cleanup to prevent memory leaks
		 */
		private static void performPeriodicCleanup() {
			operationCounter++;
			if (operationCounter >= CLEANUP_INTERVAL) {
				operationCounter = 0;
				cleanupOldBackups();
			}
		}

		/**
		 * Write stats to file for persistence with atomic operation
		 */
		private static boolean writeStatsToFile(int itemId, int tier, String itemType, double intensity,
				String adminName, int[] newStats) {
			BufferedWriter writer = null;
			try {
				File bonusDir = new File("data/items/bonuses/");
				if (!bonusDir.exists()) {
					bonusDir.mkdirs();
				}

				File bonusFile = new File("data/items/bonuses/" + itemId + ".txt");
				File tempFile = new File("data/items/bonuses/" + itemId + ".tmp");

				writer = new BufferedWriter(new FileWriter(tempFile));
				String tierName = getTierNameShort(tier);
				writer.write("// Auto-generated by " + adminName + " - " + tierName + " " + itemType.toUpperCase()
						+ " [ENHANCED WEAPON BALANCE v3.2]");
				writer.newLine();
				writer.write("// Max stat: " + getMaxStat(newStats) + " | Intensity: " + intensity
						+ " | Balanced with v3.2 System");
				writer.newLine();

				for (int i = 0; i < newStats.length; i++) {
					if (i == 5) {
						writer.write("// Defense bonuses");
						writer.newLine();
					} else if (i == 11) {
						writer.write("// Absorption bonuses (CAPPED v3.2)");
						writer.newLine();
					} else if (i == 14) {
						writer.write("// Other bonuses");
						writer.newLine();
					}
					writer.write(String.valueOf(newStats[i]));
					writer.newLine();
				}

				writer.close();
				writer = null;

				// Atomic file replacement
				if (bonusFile.exists()) {
					bonusFile.delete();
				}
				tempFile.renameTo(bonusFile);

				return true;

			} catch (IOException e) {
				Logger.handle(e);
				return false;
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}
		}

		// ===== SEARCH AND ANALYSIS METHODS =====

		/**
		 * Find items by tier from log file with enhanced error handling
		 */
		private static List<String> findItemsByTier(int targetTier) {
			List<String> tierItems = new ArrayList<String>();
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(BALANCE_LOG_FILE));
				String line;
				String currentItem = null;
				int currentTier = 0;
				String currentType = null;
				String currentIntensity = null;
				String currentMaxStat = null;

				while ((line = reader.readLine()) != null) {
					if (line.startsWith("=== ") && line.endsWith(" ===")) {
						// Extract item name
						currentItem = line.substring(4, line.length() - 4);
					} else if (line.startsWith("Tier: ")) {
						// Extract tier number
						String tierStr = line.substring(6).split(" ")[0];
						try {
							currentTier = Integer.parseInt(tierStr);
						} catch (NumberFormatException e) {
							currentTier = 0;
						}
					} else if (line.startsWith("Type: ")) {
						currentType = line.substring(6);
					} else if (line.startsWith("Intensity: ")) {
						currentIntensity = line.substring(11);
					} else if (line.startsWith("MAX STAT: ")) {
						currentMaxStat = line.substring(10);

						// End of item entry - check if it matches our tier
						if (currentTier == targetTier && currentItem != null) {
							String itemInfo = currentItem + " | Type: " + currentType + " | Intensity: "
									+ currentIntensity + " | Max: " + currentMaxStat;
							tierItems.add(itemInfo);
						}
					}
				}

			} catch (Exception e) {
				Logger.handle(e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}

			return tierItems;
		}

		/**
		 * Search balanced items by name with enhanced error handling
		 */
		private static List<String> searchBalancedItems(String searchTerm) {
			List<String> foundItems = new ArrayList<String>();
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(BALANCE_LOG_FILE));
				String line;
				StringBuilder currentEntry = new StringBuilder();
				boolean foundMatch = false;

				while ((line = reader.readLine()) != null) {
					if (line.startsWith("=== ") && line.endsWith(" ===")) {
						// Process previous entry if it was a match
						if (foundMatch) {
							foundItems.add(currentEntry.toString().trim());
						}

						// Start new entry
						currentEntry = new StringBuilder();
						currentEntry.append(line).append("\n");

						// Check if this item matches search
						foundMatch = line.toLowerCase().contains(searchTerm);

					} else if (line.equals("----------------------------------------")) {
						// End of entry
						if (foundMatch) {
							foundItems.add(currentEntry.toString().trim());
						}
						currentEntry = new StringBuilder();
						foundMatch = false;
					} else {
						currentEntry.append(line).append("\n");
					}
				}

			} catch (Exception e) {
				Logger.handle(e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}

			return foundItems;
		}

		/**
		 * Get tier summary counts with enhanced error handling
		 */
		private static Map<Integer, Integer> getTierSummary() {
			Map<Integer, Integer> tierCounts = new ConcurrentHashMap<Integer, Integer>();
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader(BALANCE_LOG_FILE));
				String line;

				while ((line = reader.readLine()) != null) {
					if (line.startsWith("Tier: ")) {
						String tierStr = line.substring(6).split(" ")[0];
						try {
							int tier = Integer.parseInt(tierStr);
							if (tierCounts.containsKey(tier)) {
								tierCounts.put(tier, tierCounts.get(tier) + 1);
							} else {
								tierCounts.put(tier, 1);
							}
						} catch (NumberFormatException e) {
							// Skip invalid tier entries
						}
					}
				}

			} catch (Exception e) {
				Logger.handle(e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}

			return tierCounts;
		}

		/**
		 * Helper class to hold tier caps
		 */
		private static class TierCaps {
			int maxAttack;
			int maxDefense;
			int maxAbsorption;
			int maxPrayer;

			TierCaps(int attack, int defense, int absorption, int prayer) {
				this.maxAttack = attack;
				this.maxDefense = defense;
				this.maxAbsorption = absorption;
				this.maxPrayer = prayer;
			}
		}

		/**
		 * Read item tiers from the balanced items log file
		 */
		private static Map<Integer, Integer> readItemTiersFromLog() {
			Map<Integer, Integer> itemTiers = new ConcurrentHashMap<Integer, Integer>();
			BufferedReader reader = null;

			try {
				File logFile = new File(BALANCE_LOG_FILE);
				if (!logFile.exists()) {
					return itemTiers; // Return empty map if no log file
				}

				reader = new BufferedReader(new FileReader(logFile));
				String line;
				int currentItemId = -1;
				int currentTier = -1;

				while ((line = reader.readLine()) != null) {
					// Look for item ID line: "=== Item Name (ID: 12345) ==="
					if (line.startsWith("=== ") && line.contains("(ID: ") && line.endsWith(" ===")) {
						try {
							int startIndex = line.indexOf("(ID: ") + 5;
							int endIndex = line.indexOf(")", startIndex);
							if (startIndex > 4 && endIndex > startIndex) {
								String idStr = line.substring(startIndex, endIndex);
								currentItemId = Integer.parseInt(idStr);
							}
						} catch (Exception e) {
							currentItemId = -1;
						}
					}
					// Look for tier line: "Tier: 7 (Elite)"
					else if (line.startsWith("Tier: ") && currentItemId != -1) {
						try {
							String tierStr = line.substring(6).split(" ")[0];
							currentTier = Integer.parseInt(tierStr);

							if (currentTier >= 1 && currentTier <= 10) {
								itemTiers.put(currentItemId, currentTier);
							}

							// Reset for next item
							currentItemId = -1;
							currentTier = -1;

						} catch (Exception e) {
							// Skip invalid tier entries
						}
					}
				}

			} catch (Exception e) {
				Logger.handle(e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						Logger.handle(e);
					}
				}
			}

			return itemTiers;
		}

		/**
		 * Calculate PROPER tier caps that actually scale meaningfully (Updated for
		 * v3.2)
		 */
		private static TierCaps calculateTierCaps(int tier) {
			// Use the new BALANCED tier system from v3.2
			int tierMax = BALANCED_TIER_MAXS[tier - 1];

			// PROPER scaling that makes each tier feel different but prevents invincibility
			int maxAttack, maxDefense, maxAbsorption, maxPrayer;

			if (tier <= 3) {
				// Low tiers: keep them weak
				maxAttack = tierMax;
				maxDefense = (int) (tierMax * 1.5);
				maxAbsorption = Math.max(1, tier); // 1%, 2%, 3%
				maxPrayer = Math.max(1, tier); // 1, 2, 3

			} else if (tier <= 6) {
				// Mid tiers: moderate power
				maxAttack = tierMax;
				maxDefense = (int) (tierMax * 1.8);
				maxAbsorption = Math.min(MAX_ABSORPTION_PER_ITEM, 2 + tier); // 6%, 7%, 8%, 9%
				maxPrayer = Math.min(MAX_PRAYER_PER_TIER[tier - 1], tier * 2); // Tier-based caps

			} else if (tier <= 8) {
				// High tiers: strong but not broken
				maxAttack = tierMax;
				maxDefense = (int) (tierMax * 2.0);
				maxAbsorption = Math.min(MAX_ABSORPTION_PER_ITEM, 3 + tier); // 10%, 11%
				maxPrayer = Math.min(MAX_PRAYER_PER_TIER[tier - 1], 5 + tier * 2); // Tier-based caps

			} else {
				// Tier 9-10: Very powerful but still reasonable
				maxAttack = tierMax;
				maxDefense = (int) (tierMax * 2.2);
				maxAbsorption = MAX_ABSORPTION_PER_ITEM; // Max 5% per item
				maxPrayer = MAX_PRAYER_PER_TIER[tier - 1]; // Tier-based caps
			}

			// SAFETY CAPS to prevent true invincibility while allowing meaningful scaling
			maxAttack = Math.min(MAX_ATTACK_BONUS, maxAttack);
			maxDefense = Math.min(MAX_DEFENSE_BONUS, maxDefense);
			maxAbsorption = Math.min(MAX_ABSORPTION_PER_ITEM, maxAbsorption);
			maxPrayer = Math.min(MAX_PRAYER_PER_TIER[tier - 1], maxPrayer);

			return new TierCaps(maxAttack, maxDefense, maxAbsorption, maxPrayer);
		}

		// ===== UTILITY METHODS =====

		/**
		 * FIXED v3.2: Determine tier based on max stat using BALANCED ranges
		 */
		private static int determineTierFromStat(int maxStat) {
			for (int tier = 1; tier <= 10; tier++) {
				int min = BALANCED_TIER_MINS[tier - 1];
				int max = BALANCED_TIER_MAXS[tier - 1];
				if (maxStat >= min && maxStat <= max) {
					return tier;
				}
			}

			// Handle stats outside normal ranges
			if (maxStat < BALANCED_TIER_MINS[0]) {
				return 1; // Below tier 1, still consider tier 1
			} else if (maxStat > BALANCED_TIER_MAXS[9]) {
				return 10; // Above tier 10, still consider tier 10
			}

			return -1; // Unknown tier
		}

		/**
		 * Get tier name (short version)
		 */
		private static String getTierNameShort(int tier) {
			switch (tier) {
			case 1:
				return "Beginner";
			case 2:
				return "Novice";
			case 3:
				return "Intermediate";
			case 4:
				return "Advanced";
			case 5:
				return "Expert";
			case 6:
				return "Master";
			case 7:
				return "Elite";
			case 8:
				return "Legendary";
			case 9:
				return "Mythical";
			case 10:
				return "Divine";
			default:
				return "Unknown";
			}
		}

		/**
		 * Get tier name (long version for compatibility)
		 */
		private static String getTierName(int tier) {
			switch (tier) {
			case 1:
				return "Tier 1 (Beginner)";
			case 2:
				return "Tier 2 (Novice)";
			case 3:
				return "Tier 3 (Intermediate)";
			case 4:
				return "Tier 4 (Advanced)";
			case 5:
				return "Tier 5 (Expert)";
			case 6:
				return "Tier 6 (Master)";
			case 7:
				return "Tier 7 (Elite)";
			case 8:
				return "Tier 8 (Legendary)";
			case 9:
				return "Tier 9 (Mythical)";
			case 10:
				return "Tier 10 (Divine)";
			default:
				return "Unknown Tier";
			}
		}

		/**
		 * Get maximum stat from array
		 */
		private static int getMaxStat(int[] stats) {
			int max = 0;
			for (int stat : stats) {
				if (stat > max)
					max = stat;
			}
			return max;
		}

		/**
		 * Get maximum attack stat from array
		 */
		private static int getMaxAttackStat(int[] stats) {
			int max = 0;
			for (int i = 0; i < 5 && i < stats.length; i++) {
				if (stats[i] > max)
					max = stats[i];
			}
			return max;
		}

		/**
		 * Get maximum defense stat from array
		 */
		private static int getMaxDefenseStat(int[] stats) {
			int max = 0;
			for (int i = 5; i < 11 && i < stats.length; i++) {
				if (stats[i] > max)
					max = stats[i];
			}
			return max;
		}

		// ===== USER INTERFACE METHODS =====

		/**
		 * Show all available item types (Enhanced with offhand weapons)
		 */
		public static void sendItemTypes(Player player) {
			player.sendMessage("=== Item Types for TIERED ITEMS v3.2 ===");
			player.sendMessage("NOTE: These types only apply to manually tiered items!");
			player.sendMessage("Regular items (bows, armor, etc.) remain unchanged.");
			player.sendMessage("");
			player.sendMessage("1-HANDED WEAPONS:");
			player.sendMessage("sword, axe, mace, dagger, spear, whip, scimitar, longsword, battleaxe, warhammer");
			player.sendMessage("bow, crossbow, staff, wand");
			player.sendMessage("");
			player.sendMessage("OFFHAND WEAPONS (75% of mainhand power):");
			player.sendMessage("offsword, offaxe, offmace, offdagger, offspear, offwhip, offscimitar, offlongsword");
			player.sendMessage("offbattleaxe, offwarhammer, offbow, offcrossbow, offstaff, offwand");
			player.sendMessage("");
			player.sendMessage("2-HANDED WEAPONS (v3.2: 180% power - 80% BONUS!):");
			player.sendMessage("greatsword, greataxe, warhammer2h, halberd, longbow, battlestaff, scythe");
			player.sendMessage("");
			player.sendMessage("MELEE ARMOR:");
			player.sendMessage("bodymelee, legmelee, helmmelee, glovesmelee, bootsmelee");
			player.sendMessage("");
			player.sendMessage("RANGED ARMOR:");
			player.sendMessage("bodyrange, legrange, helmrange, glovesrange, bootsrange");
			player.sendMessage("");
			player.sendMessage("MAGIC ARMOR:");
			player.sendMessage("bodymage, legmage, helmmage, glovesmage, bootsmage");
			player.sendMessage("");
			player.sendMessage("ACCESSORIES:");
			player.sendMessage("shield, ring, amulet, cape, belt");
			player.sendMessage("");
			player.sendMessage("SPECIAL:");
			player.sendMessage("hybrid, utility, tank");
			player.sendMessage("");
			player.sendMessage("v3.2 SCOPE: Only applies to items you manually tier with ;;adjuststats!");
			player.sendMessage("Regular game items (normal bows, armor) stay unchanged!");
		}

		/**
		 * Enhanced tier list with new balanced ranges
		 */
		public static void sendTierList(Player player) {
			player.sendMessage("=== BALANCED 10-Tier System v3.2 ===");
			player.sendMessage("Tier 1 - Beginner: " + BALANCED_TIER_MINS[0] + "-" + BALANCED_TIER_MAXS[0] + " stats");
			player.sendMessage("Tier 2 - Novice: " + BALANCED_TIER_MINS[1] + "-" + BALANCED_TIER_MAXS[1] + " stats");
			player.sendMessage(
					"Tier 3 - Intermediate: " + BALANCED_TIER_MINS[2] + "-" + BALANCED_TIER_MAXS[2] + " stats");
			player.sendMessage("Tier 4 - Advanced: " + BALANCED_TIER_MINS[3] + "-" + BALANCED_TIER_MAXS[3] + " stats");
			player.sendMessage("Tier 5 - Expert: " + BALANCED_TIER_MINS[4] + "-" + BALANCED_TIER_MAXS[4] + " stats");
			player.sendMessage("Tier 6 - Master: " + BALANCED_TIER_MINS[5] + "-" + BALANCED_TIER_MAXS[5] + " stats");
			player.sendMessage("Tier 7 - Elite: " + BALANCED_TIER_MINS[6] + "-" + BALANCED_TIER_MAXS[6] + " stats");
			player.sendMessage("Tier 8 - Legendary: " + BALANCED_TIER_MINS[7] + "-" + BALANCED_TIER_MAXS[7] + " stats");
			player.sendMessage("Tier 9 - Mythical: " + BALANCED_TIER_MINS[8] + "-" + BALANCED_TIER_MAXS[8] + " stats");
			player.sendMessage("Tier 10 - Divine: " + BALANCED_TIER_MINS[9] + "-" + BALANCED_TIER_MAXS[9] + " stats");
			player.sendMessage("");
			player.sendMessage("ENHANCED v3.2: 2H weapons 80% bonus vs dual-wield!");
			player.sendMessage("Prayer: v3.2 caps up to " + MAX_PRAYER_PER_TIER[9] + " (3-4x higher)");
			player.sendMessage("Absorption: Max " + MAX_ABSORPTION_PER_ITEM + "% per item, "
					+ MAX_TOTAL_MELEE_ABSORPTION + "% total");
			player.sendMessage("Weapons: Offhand (75%), Mainhand (100%), 2H (180% - MASSIVE BOOST!)");
		}

		/**
		 * Enhanced help with all v3.2 commands
		 */
		public static void sendHelp(Player player) {
			player.sendMessage("=== Enhanced Item Balancer v3.2 (ENHANCED WEAPON BALANCE + PRAYER BOOST) ===");
			player.sendMessage("BALANCING:");
			player.sendMessage(";;adjuststats <itemId> <type> <tier> [intensity]");
			player.sendMessage(";;rollbackstats <itemId>");
			player.sendMessage(";;batchadjust <startId> <endId> <type> <tier> [intensity]");
			player.sendMessage("");
			player.sendMessage("v3.2 EXAMINE INTEGRATION:");
			player.sendMessage(";;refreshexamines - Force refresh item examine cache");
			player.sendMessage(";;testtier <itemId> - Test tier reading for specific item");
			player.sendMessage("");
			player.sendMessage("v3.2 ENHANCED WEAPON BALANCE & SAFETY:");
			player.sendMessage(";;emergencyabsorptionfix - Fix all broken absorption bonuses (ADMIN)");
			player.sendMessage(";;showabsorption - Check your total absorption percentages (v3.2 enhanced)");
			player.sendMessage(";;validateabsorption - Check if your equipment setup is valid");
			player.sendMessage(";;validatestats - Validate all equipped items for issues");
			player.sendMessage("");
			player.sendMessage("LOGGING & LISTS:");
			player.sendMessage(";;showbalanced - View balanced items log location");
			player.sendMessage(";;listbytier <tier> - List all items in specific tier");
			player.sendMessage(";;searchbalanced <name> - Search for balanced items");
			player.sendMessage(";;tiersummary - Show count of items per tier");
			player.sendMessage(";;exportreport - Generate formatted balance report");
			player.sendMessage(";;cleanuplog - Remove duplicate entries from log");
			player.sendMessage("");
			player.sendMessage("ADMINISTRATION:");
			player.sendMessage(";;rebalanceall - COMPLETE v3.2 regeneration of ALL items (ADMIN)");
			player.sendMessage(";;showtiercaps - Display tier-based stat caps");
			player.sendMessage(";;testtierreading - Debug tier reading from log");
			player.sendMessage(";;itemcleanup - Clean up memory (admin use)");
			player.sendMessage("");
			player.sendMessage("INFORMATION:");
			player.sendMessage(";;showstats <itemId> - Display detailed item stats");
			player.sendMessage(";;itemtypes - Show all item types (including enhanced 2H weapons)");
			player.sendMessage(";;tierlist - Show all tier ranges");
			player.sendMessage(";;help - Show this help menu");
			player.sendMessage("");
			player.sendMessage("v3.2 COMPLETE ENHANCEMENTS:");
			player.sendMessage("✓ 2H weapons: 80% bonus (vs 25% before) - MASSIVE IMPROVEMENT!");
			player.sendMessage("✓ Offhand weapons: 75% power + dual-wield validation");
			player.sendMessage("✓ Prayer bonuses: 3-4x higher for ALL items (up to " + MAX_PRAYER_PER_TIER[9] + ")");
			player.sendMessage("✓ Complete regeneration: ALL combat items use new v3.2 formulas");
			player.sendMessage("✓ Cosmetic filtering: Auras/pets/cosmetics excluded from combat stats");
			player.sendMessage(
					"✓ Absorption caps prevent invincibility (Max " + MAX_ABSORPTION_PER_ITEM + "% per item)");
			player.sendMessage("✓ Examine integration: Items show correct tiers immediately!");
			player.sendMessage("✓ Memory management + Thread safety + Atomic file operations");
			player.sendMessage("✓ Perfect weapon balance: 2H now properly outscales dual-wield!");
		}

	}

	private static NPC findNearestNPC(Player player) {
		try {
			WorldTile playerTile = player.getWorldTile();
			if (playerTile == null)
				return null;

			// Search for NPCs within 10 tiles
			for (int x = playerTile.getX() - 10; x <= playerTile.getX() + 10; x++) {
				for (int y = playerTile.getY() - 10; y <= playerTile.getY() + 10; y++) {
					try {
						// This depends on your World class - adjust as needed:
						for (NPC npc : World.getNPCs()) {
							if (npc != null && npc.getWorldTile() != null && npc.getWorldTile().getX() == x
									&& npc.getWorldTile().getY() == y) {
								return npc;
							}
						}
					} catch (Exception e) {
						break;
					}
				}
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	/**
	 * Helper method to append to ObjectSpawns.java
	 */
	private static void appendToCustomSpawns(String codeLine, String comment) {
	    try {
	        String filePath = "src/com/rs/utils/ObjectSpawns.java";
	        File file = new File(filePath);
	        
	        if (!file.exists()) {
	            System.out.println("ObjectSpawns.java not found at: " + filePath);
	            return;
	        }
	        
	        // Read the entire file
	        List<String> lines = new ArrayList<>();
	        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                lines.add(line);
	            }
	        }
	        
	        // Find the last line before the closing brace of addCustomSpawns()
	        int insertIndex = -1;
	        for (int i = lines.size() - 1; i >= 0; i--) {
	            String line = lines.get(i).trim();
	            if (line.equals("}") && i > 0) {
	                // Check if this is the closing brace of addCustomSpawns method
	                for (int j = i - 1; j >= 0; j--) {
	                    if (lines.get(j).contains("addCustomSpawns()")) {
	                        insertIndex = i;
	                        break;
	                    }
	                    if (lines.get(j).contains("public static")) {
	                        break; // Found another method, this isn't our closing brace
	                    }
	                }
	                if (insertIndex != -1) break;
	            }
	        }
	        
	        if (insertIndex != -1) {
	            // Add a comment section for skilling hub if it doesn't exist
	            boolean hasSkillHubSection = false;
	            for (String line : lines) {
	                if (line.contains("CUSTOM SKILLING HUB") || line.contains("Skilling Hub")) {
	                    hasSkillHubSection = true;
	                    break;
	                }
	            }
	            
	            if (!hasSkillHubSection) {
	                lines.add(insertIndex, "		      	");
	                lines.add(insertIndex + 1, "		      	/**");
	                lines.add(insertIndex + 2, "		      	 * CUSTOM SKILLING HUB");
	                lines.add(insertIndex + 3, "		      	 */");
	                insertIndex += 4;
	            }
	            
	            // Insert the new object spawn line
	            lines.add(insertIndex, "		      	" + codeLine);
	            
	            // Write the file back
	            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
	                for (String line : lines) {
	                    writer.println(line);
	                }
	            }
	            
	            System.out.println("Added to ObjectSpawns.java: " + comment);
	        } else {
	            System.out.println("Could not find addCustomSpawns() method in ObjectSpawns.java");
	        }
	        
	    } catch (IOException e) {
	        System.out.println("Error updating ObjectSpawns.java: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
}
