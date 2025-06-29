package com.rs.game.player.content;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public class UpgradeSystem {

	public static int UPGRADESTONE = 39922;
	public static int PERFECT_UPGRADESTONE = 39921;

	public enum Upgradables {
		// base item - output item
		Veracbody(true, 4757, 36397), Veraglegs(true, 4759, 36399), toragbody(true, 4749, 36401), toraglegs(true, 4751,
				36403), dharokbody(true, 4720, 36405), dharoklegs(true, 4722, 36407), karilstop(true, 4736,
						36409), karilslegs(true, 4738, 36411), ahrimstop(true, 4712, 36413), ahrimslegs(true, 4714,
								36415), akrisaetop(true, 21752, 36417), akrisaelegs(true, 21760,
										36419), guthanbody(true, 4725, 36393), guthanlegs(true, 4730, 36395),

		;

		private int baseItem, output;
		private boolean armour;

		Upgradables(boolean armour, int baseItem, int output) {
			this.armour = armour;
			this.baseItem = baseItem;
			this.output = output;
		}

		public int baseItem() {
			return baseItem;
		}

		public int outPut() {
			return output;
		}

		public static Upgradables getBaseItem(int itemId) {
			for (Upgradables up : Upgradables.values())
				if (up.baseItem() == itemId)
					return up;
			return null;
		}

		public boolean isArmour() {
			return armour;
		}

	}

	static boolean success = false;
	static String message = "";

	public static void Upgrade(Player player, Item usedWith, Item itemUsed, Upgradables Upsys, int stone) {
		player.lock(3);
		player.getInterfaceManager().closeInventory();// removing inv
		player.setNextAnimation(new Animation(898));
		int baseItem = usedWith.getId();
		int outputItem = Upsys.outPut();
		String baseItemName = ItemDefinitions.getItemDefinitions(baseItem).getName();
		String outputName = ItemDefinitions.getItemDefinitions(Upsys.outPut()).getName();
		String upgradestone = ItemDefinitions.getItemDefinitions(stone).getName();
		int rng = Utils.random(15);
		if (stone == PERFECT_UPGRADESTONE && rng == 14 || rng == 15) {
			rng = Utils.random(13);
		}

		switch (rng) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
			player.getInventory().deleteItem(stone, 1);
			message = Colors.red + "Upgrade failed! " + upgradestone + " has been consumed!";
			break;
		case 12:
		case 13:
			player.getInventory().deleteItem(stone, 1);
			player.getInventory().getItems().set(player.getInventory().getItems().getThisItemSlot(usedWith),
					new Item(outputItem, 1));
			player.getInventory().refresh();
			World.sendWorldMessage(Colors.red + "[Upgrade] </col> " + player.getUsername()
					+ " has successfully upgraded " + baseItemName + " into " + outputName, true);
			success = true;
			break;
		case 14:
		case 15:
			player.getInventory().deleteItem(stone, 1);
			player.getInventory().deleteItem(baseItem, 1);
			message = Colors.red + "Upgrade failed and all vanished!";
			break;

		}
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				if (success) {
					World.sendWorldMessage(message, false);
					player.setNextGraphics(new Graphics(2000));
				} else {
					player.sm(message);
					player.setNextGraphics(new Graphics(1989));
				}
				player.getInterfaceManager().sendInventory();// adding inv
				player.getInventory().unlockInventoryOptions();// unlock inv options
				player.getPackets().sendGlobalConfig(168, 4);// opens inv
				stop();
			}
		}, 3);
	}

}
