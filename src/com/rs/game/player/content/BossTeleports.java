package com.rs.game.player.content;

import com.rs.Settings;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.activities.instances.GregorovicInstance;
import com.rs.game.activities.instances.HelwyrInstance;
import com.rs.game.activities.instances.Instance;
import com.rs.game.activities.instances.TwinFuriesInstance;
import com.rs.game.activities.instances.VindictaInstance;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;
import com.rs.game.player.Player;

/**
 * Handles everything related to the Boss Teleports interface.
 * 
 * @author Zeus.
 */
public class BossTeleports {

	/**
	 * Sends the actual interface with all available options.
	 * 
	 * @param player The player to send the interface to.
	 */

	public static void sendInterface(Player player) {
		player.getInterfaceManager().closeChatBoxInterface();
		player.getInterfaceManager().sendInterface(813);
		player.getPackets().sendItemOnIComponent(813, 33, 30005, 1);
		player.getPackets().sendIComponentText(813, 80, Colors.green + "HELWYR"); // Title
		player.getPackets().sendIComponentText(813, 15, "Where would you like to go, " + player.getDisplayName() + "?"); // Desc
		player.getPackets().sendIComponentText(813, 29, Colors.green + "Tier I"); // Boss Teleports
		player.getPackets().sendIComponentText(813, 52, Colors.yellow + "Masuta</col> (Best Starter)");
		player.getPackets().sendIComponentText(813, 53, Colors.green + "GodWars");
		player.getPackets().sendIComponentText(813, 54, Colors.green + "Sunfreet");
		player.getPackets().sendIComponentText(813, 55, Colors.green + "Bork");
		player.getPackets().sendIComponentText(813, 56, Colors.green + "Sotapana");
		player.getPackets().sendIComponentText(813, 60, Colors.green + "Wyvern");
		player.getPackets().sendIComponentText(813, 57, Colors.green + "Kalphite Queen");
		player.getPackets().sendIComponentText(813, 58, Colors.green + "Tormented Demon");
		player.getPackets().sendIComponentText(813, 59, Colors.green + "");
		player.getPackets().sendIComponentText(813, 61, Colors.green + "");

		player.getPackets().sendIComponentText(813, 30, Colors.gold + "Tier II");
		player.getPackets().sendIComponentText(813, 43, Colors.gold + "Dark Lord");
		player.getPackets().sendIComponentText(813, 44, Colors.gold + "Kingblack Dragon");
		player.getPackets().sendIComponentText(813, 45, Colors.gold + "QueenBlack Dragon");
		player.getPackets().sendIComponentText(813, 46, Colors.gold + "Dagganoth King");
		player.getPackets().sendIComponentText(813, 47, Colors.gold + "Mercenary Mage");
		player.getPackets().sendIComponentText(813, 48, Colors.gold + "Helwyr");
		player.getPackets().sendIComponentText(813, 49, Colors.gold + "Vindicta");
		player.getPackets().sendIComponentText(813, 50, Colors.gold + "Twin Furies");
		player.getPackets().sendIComponentText(813, 51, Colors.red + "Gregorovic");

		player.getPackets().sendIComponentText(813, 31, Colors.yellow + "Tier III");
		player.getPackets().sendIComponentText(813, 37, Colors.yellow + "Kalphite King");
		player.getPackets().sendIComponentText(813, 38, Colors.yellow + "Araxxor");
		player.getPackets().sendIComponentText(813, 39, Colors.red + "Solak (Beta)");
		player.getPackets().sendIComponentText(813, 40, Colors.yellow + "Vorago");
		player.getPackets().sendIComponentText(813, 41, Colors.yellow + "Corporeal Beast");
		player.getPackets().sendIComponentText(813, 42, Colors.yellow + "Telos, The Warden");

		player.getPackets().sendIComponentText(813, 32, Colors.red + "Wilderness");
		player.getPackets().sendIComponentText(813, 64, Colors.red + "Blink");
		player.getPackets().sendIComponentText(813, 65, Colors.red + "Party Demon");
		player.getPackets().sendIComponentText(813, 66, Colors.red + "Chaos Elemental");
		player.getPackets().sendIComponentText(813, 67, Colors.red + "WildyWyrm");
		player.getPackets().sendIComponentText(813, 68, Colors.red + "");
		player.getPackets().sendIComponentText(813, 69, Colors.red + "");
		player.getPackets().sendIComponentText(813, 70, Colors.red + "");
		player.getPackets().sendIComponentText(813, 71, Colors.red + "");

		player.getPackets().sendIComponentText(813, 36, Colors.red + "Information");
		player.getPackets().sendIComponentText(813, 99,
				Colors.red + "Combat Lvl: " + player.getSkills().getCombatLevelWithSummoning() + "");
		player.getPackets().sendIComponentText(813, 101,
				Colors.red + "PvM Pts:" + Utils.formatNumber(player.getPVMPoints()) + " ");
		player.getPackets().sendIComponentText(813, 103, Colors.red + "DToken: " + player.dungTokens + "");
		player.getPackets().sendIComponentText(813, 105, Colors.red + "");
		player.getPackets().sendIComponentText(813, 107, Colors.red + "");

	}

	/**
	 * Handles the actual interfaces buttons.
	 * 
	 * @param player      The players interface to handle.
	 * @param componentId The players interface pressed component ID.
	 */
	public static void handleInterface(Player player, int componentId) {
		InterfaceManager.setPlayerInterfaceSelected(1);
		player.getInterfaceManager().closeChatBoxInterface();
		// tier i
		if (componentId == 52)
			Magic.vineTeleport(player, new WorldTile(1750, 5243, 0)); // giant mole
		if (componentId == 53) {
			if (player.getPerkManager().gwdSpecialist) {
				player.getDialogueManager().startDialogue(new Dialogue() {

					@Override
					public void start() {
						player.sm("send gwd option");
						sendOptionsDialogue("Godwars Bosses", "Godwars: Bandos", "Godwars: Armadyl",
								"Godwars: Saradomin", "Godwars: Zamorak", "Godwars: Nex");
						stage = 0;
					}

					@Override
					public void run(int interfaceId, int componentId) {
						if (stage == 0) {
							if (componentId == OPTION_1) { // bandos
								player.getInventory().deleteItem(24954, 1);
								teleportPlayer(player, 2861, 5357, 0, "GodWars");
								// Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2861, 5357, 0));
								end();
							}
							if (componentId == OPTION_2) { // armadyl
								player.getInventory().deleteItem(24954, 1);
								teleportPlayer(player, 2832, 5292, 0, "GodWars");
								// Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2832, 5292, 0));
								end();
							}
							if (componentId == OPTION_3) { // saradmin
								player.getInventory().deleteItem(24954, 1);
								teleportPlayer(player, 2919, 5261, 0, "GodWars");
								// Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2919, 5261, 0));
								end();
							}
							if (componentId == OPTION_4) { // zamorak
								player.getInventory().deleteItem(24954, 1);
								teleportPlayer(player, 2925, 5337, 0, "GodWars");
								// Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2925, 5337, 0));
								end();
							}
							if (componentId == OPTION_5) { // nex
								player.getInventory().deleteItem(24954, 1);
								teleportPlayer(player, 2904, 5203, 0, "GodWars");
								// Magic.sendNormalTeleportSpell(player, 0, 0, new WorldTile(2904, 5203, 0));
								end();
							}

						}
					}

					@Override
					public void finish() {
						player.getInterfaceManager().closeChatBoxInterface();
					}

				});
				return;
			}
			teleportPlayer(player, 2882, 5311, 0, "GodWars");
			player.sm(Colors.green + "[GWD Msg]: " + Colors.eshad
					+ "You can purchase GWD instant Teleport @ Pvm Shop for only 300 Pvm Points");
		}
		if (componentId == 54)
			Magic.vineTeleport(player, new WorldTile(3535, 5189, 0));// sunfreet
		if (componentId == 55)
			Magic.vineTeleport(player, new WorldTile(3143, 5545, 0)); // bork
		if (componentId == 56)
			Magic.vineTeleport(player, new WorldTile(1734, 5240, 0)); // sotapana
		if (componentId == 60)
			Magic.vineTeleport(player, new WorldTile(5158, 7536, 0));// Wyvern
		// tier ii
		if (componentId == 57)
			Magic.vineTeleport(player, new WorldTile(3479, 9488, 0));// kalphite queen
		if (componentId == 44)
			Magic.vineTeleport(player, new WorldTile(2273, 4681, 0));// king black dragon
		if (componentId == 45)
			Magic.vineTeleport(player, new WorldTile(1195, 6499, 0));// queen black dragon
		if (componentId == 46)
			Magic.vineTeleport(player, new WorldTile(2900, 4449, 0));// dagganoth
		if (componentId == 47) {
			if (player.getSkills().getCombatLevelWithSummoning() < 126) {
				player.sendMessage(Colors.red + "You need atleast a combat level of 126 to enter the arena.");
				player.getDialogueManager().finishDialogue();
				return;

			}
			Magic.vineTeleport(player, new WorldTile(3210, 5477, 0)); // Mercenary Mage
		}
		if (componentId == 48) { // helwyr
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
		}
		if (componentId == 49) { // vindicta
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
		}
		if (componentId == 50) { // twin
			/*
			 * player.sm(Colors.red +
			 * "Twin Furies is currently disabled for combat rework!");
			 * player.getDialogueManager().finishDialogue();
			 */
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
		}
		if (componentId == 51) { // greg

			player.sm(Colors.red + "Gregorvic is currently disabled");
			player.getDialogueManager().finishDialogue();

			/**
			 * Instance instance1 = null; for (int i = 0; i < World.getInstances().size();
			 * i++) { if (World.getInstances().get(i) instanceof GregorovicInstance) {
			 * instance1 = World.getInstances().get(i); } } if (instance1 != null) {
			 * instance1.enterInstance(player); } else { instance1 = new
			 * GregorovicInstance(player, 60, 33, 5, -1, 3, false);
			 * instance1.constructInstance(); }
			 **/

		}
		// tier iii
		if (componentId == 37)
			Magic.vineTeleport(player, new WorldTile(2974, 1654, 0));// kalphite king
		if (componentId == 38)
			Magic.vineTeleport(player, new WorldTile(4512, 6289, 1));// araxyte
		if (componentId == 39)
			Magic.vineTeleport(player, new WorldTile(4128, 3242, 0));// Solak
		if (componentId == 58)
			Magic.vineTeleport(player, new WorldTile(2571, 5735, 0));// tormented Demon
		if (componentId == 40)
			teleportPlayer(player, 2972, 3430, 0, null);// vorago
		if (componentId == 41)
			Magic.vineTeleport(player, new WorldTile(2966, 4383, 2)); // corp
		if (componentId == 42)
			Magic.vineTeleport(player, new WorldTile(3851, 7051, 0)); // Telos 3200, 6961, 1
		if (componentId == 43)
			Magic.vineTeleport(player, new WorldTile(3809, 4727, 0)); // Dark Lord

		// tier wilderness
		if (componentId == 64) {
			player.getDialogueManager().startDialogue(new Dialogue() {
				@Override
				public void start() {
					sendOptionsDialogue(Colors.red + "You lose items in this area!", "Proceed.", "Cancel.");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					if (componentId == OPTION_1) {
						Magic.vineTeleport(player, new WorldTile(3064, 3951, 0));// blink
						player.getControlerManager().startControler("Wilderness");
					}
					end();
				}

				@Override
				public void finish() {
				}
			});
			return;
		}

		if (componentId == 65) {
			player.getDialogueManager().startDialogue(new Dialogue() {

				@Override
				public void start() {
					sendOptionsDialogue(Colors.red + "You lose items in this area!", "Proceed.", "Cancel.");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					if (componentId == OPTION_1) {
						Magic.vineTeleport(player, new WorldTile(3105, 3961, 0));// Party Demon
						player.getControlerManager().startControler("Wilderness");
					}
					end();
				}

				@Override
				public void finish() {
				}
			});
			return;

		}
		if (componentId == 66) {
			player.getDialogueManager().startDialogue(new Dialogue() {
				@Override
				public void start() {
					sendOptionsDialogue(Colors.red + "You lose items in this area!", "Proceed.", "Cancel.");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					if (componentId == OPTION_1) {
						Magic.vineTeleport(player, new WorldTile(3143, 3823, 0));// chaos elemental
						player.getControlerManager().startControler("Wilderness");
					}
					end();
				}

				@Override
				public void finish() {
				}
			});
			return;
		}
		if (componentId == 67) {
			player.getDialogueManager().startDialogue(new Dialogue() {
				@Override
				public void start() {
					sendOptionsDialogue(Colors.red + "You lose items in this area!", "Proceed.", "Cancel.");
				}

				@Override
				public void run(int interfaceId, int componentId) {
					if (componentId == OPTION_1) {
						Magic.vineTeleport(player, new WorldTile(3158, 3953, 0));// chaos elemental
						player.getControlerManager().startControler("Wilderness");
					}
					end();
				}

				@Override
				public void finish() {
				}
			});
			return;
		}

	}

	public static void teleportPlayer(Player player, final int placeX, final int placeY, final int placePlane,
			String controller) {
		Magic.vineTeleport(player, new WorldTile(placeX, placeY, placePlane));
		final WorldTile teleTile = new WorldTile(placeX, placeY, placePlane);
		if (player.isAtWild()) {
			player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
			return;
		}
		if (!player.getControlerManager().processMagicTeleport(teleTile))
			return;
		player.lock(4);
		player.stopAll();
		player.setNextGraphics(new Graphics(1229));
		player.setNextAnimation(new Animation(7082));

		WorldTasksManager.schedule(new WorldTask() {

			@Override
			public void run() {
				player.setNextAnimation(new Animation(7084));
				player.setNextGraphics(new Graphics(1228));
				player.setNextWorldTile(teleTile);
				player.getControlerManager().magicTeleported(Magic.MAGIC_TELEPORT);
				player.checkMovement(placeX, placeY, placePlane);
				if (player.getControlerManager().getControler() == null)
					Magic.teleControlersCheck(player, teleTile);
				if (controller != null)
					player.getControlerManager().startControler(controller);
				player.unlock();
				stop();
			}
		}, 4);
	}
}