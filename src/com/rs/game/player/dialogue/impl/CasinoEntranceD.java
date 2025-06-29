package com.rs.game.player.dialogue.impl;

import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.content.Magic;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class CasinoEntranceD extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Would You like to Enter the Casino?", "Enter", "Buy Mithril Seeds (10m)",
				"Buy Casino Cash", "Convert Casino Cash to GP");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:
			if (componentId == OPTION_1) {
				if (player.getFamiliar() != null || player.getPet() != null) {
					player.getPackets().sendGameMessage(Colors.gold + "[CASINO] " + Colors.red
							+ "You cannot bring a pet/familiar inside the casino");
					end();
					return;
				}
				if (player.getSkills().getTotalLevel() < 150) {
					player.getPackets()
							.sendGameMessage(Colors.gold + "[CASINO] " + Colors.red
									+ "You need over 150 total level to enter the casino, you still need "
									+ (150 - player.getSkills().getTotalLevel()) + " more!");
					end();
					return;
				}
				if (!(player.getInventory().containsCoins(100000000)
						|| player.getBank().containsItem(995, 100000000))) {
					player.getPackets()
							.sendGameMessage(Colors.gold + "[CASINO] " + Colors.red
									+ "You need to have a total of 100 Million gp before you can enter the Casino!");
					end();
					return;
				}
				if (player.getInventory().containsOneItem(299, 1) && player.getInventory().containsItem(37490, 1)) {
					Magic.vineTeleport(player, new WorldTile(5888, 4673, 1));
					end();
				}

				if (!player.getInventory().containsItem(37490, 1)) {
					player.getPackets().sendGameMessage(Colors.gold + "[CASINO] " + Colors.red
							+ "You need to have atleast 1 Casino cash before you enter the casino");
					end();
					return;

				}
				if (player.getInventory().getFreeSlots() < 28 && !player.getInventory().containsItem(299,1) && !player.getInventory().containsItem(37490,0)) {
					player.getPackets().sendGameMessage(Colors.gold + "[CASINO] " + Colors.red
							+ "You can only bring casino cash and mithril seed inside the casino");
					end();
					return;
				}
				for (int i = 0; i < 18; i++) {
					if (player.getEquipment().getItems().get(i) != null) {
						player.sm(Colors.gold + "[CASINO] " + Colors.red + "Please unequip: "
								+ player.getEquipment().getItem(i).getName() + " before you enter the casino.");
						end();
						return;
					}
				}

				Magic.vineTeleport(player, new WorldTile(5888, 4673, 1));
				end();
			}
			if (componentId == OPTION_2) {
				if (!player.getInventory().containsCoins(10000000)) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red + "You do not have enough gp!");
					end();
					return;
				}
				if (player.getInventory().getFreeSlots() < 1) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red + "You do not have free inventory slot!");
					end();
					return;
				}
				player.getInventory().deleteCoins(10000000);
				player.getInventory().addItem(299, 10000);
				player.sm(Colors.gold + "[CASINO] " + Colors.green + "You successfully bought Mithril Seed!");
				end();
				return;
			}
			if (componentId == OPTION_3) {
				sendOptionsDialogue("Buy Casino Cash", "10M GP - 100m Casino Cash", "50m GP - 500M Casino Cash",
						"100m GP - 1B Casino Cash");
				stage = 3;
			}
			if (componentId == OPTION_4) {
				sendOptionsDialogue("Exchange Casino Cash", "100m Casino Cash - 10M GP ", "500M Casino Cash - 50m GP",
						"1B Casino Cash - 100m GP");
				stage = 4;
			}
			break;

		case 3:
			if (componentId == OPTION_1) {
				if (!player.getInventory().containsCoins(10000000)) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red + "You do not have enough gp!");
					end();
					return;
				}
				if (player.getInventory().getFreeSlots() < 1) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red + "You do not have free inventory slot!");
					end();
					return;
				}
				player.getInventory().addItem(37490, 100000000);
				player.getInventory().deleteCoins(10000000);
				player.sm(Colors.gold + "[CASINO] " + Colors.green + "You successfully bought 100m Casino Cash!");
				end();
				return;
			}
			if (componentId == OPTION_2) {
				if (!player.getInventory().containsCoins(50_000_0000)) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red + "You do not have 50M GP in your inventory!");
					end();
					return;
				}
				if (player.getInventory().getFreeSlots() < 1) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red + "You do not have free inventory slot!");
					end();
					return;
				}

				player.getInventory().addItem(37490, 500_000_000);
				player.getInventory().deleteCoins(50_000_000);
				player.sm(Colors.gold + "[CASINO] " + Colors.green + "You successfully bought 500m Casino Cash!");
				end();
				return;
			}
			if (componentId == OPTION_3) {
				if (!player.getInventory().containsCoins(100_000_0000)) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red + "You do not have 100M GP in your inventory!");
					end();
					return;
				}
				if (player.getInventory().getFreeSlots() < 1) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red + "You do not have free inventory slot!");
					end();
					return;
				}

				player.getInventory().addItem(37490, 1_000_000_000);
				player.getInventory().deleteCoins(100_000_000);
				player.sm(Colors.gold + "[CASINO] " + Colors.green + "You successfully bought 1B Casino Cash!");
				end();
				return;
			}
			break;
		case 4:
			if (componentId == OPTION_1) {
				if (!player.getInventory().containsItem(37490, 100000000)) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red
							+ "You need to have 100M Casino Cash in your inventory to exchange!");
					end();
					return;
				}
				if (player.getInventory().getFreeSlots() < 1) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red + "You do not have free inventory slot!");
					end();
					return;
				}
				player.getInventory().addItem(995, 10000000);
				player.getInventory().deleteItem(37490, 100000000);
				player.sm(Colors.gold + "[CASINO] " + Colors.green
						+ "You successfully Exchange 100M Casino Cash to 10M GP!");
				end();
				return;
			}
			if (componentId == OPTION_2) {
				if (!player.getInventory().containsItem(37490, 500000000)) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red
							+ "You need to have 500M Casino Cash in your inventory to exchange!");
					end();
					return;
				}
				if (player.getInventory().getFreeSlots() < 1) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red + "You do not have free inventory slot!");
					end();
					return;
				}
				player.getInventory().addItem(995, 50000000);
				player.getInventory().deleteItem(37490, 500000000);
				player.sm(Colors.gold + "[CASINO] " + Colors.green
						+ "You successfully Exchange 500M Casino Cash to 50M GP!");
				end();
				return;

			}
			if (componentId == OPTION_3) {
				if (!player.getInventory().containsItem(37490, 1000000000)) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red
							+ "You need to have 1B Casino Cash in your inventory to exchange!");
					end();
					return;
				}
				if (player.getInventory().getFreeSlots() < 1) {
					player.sm(Colors.gold + "[CASINO] " + Colors.red + "You do not have free inventory slot!");
					end();
					return;
				}
				player.getInventory().addItem(995, 100000000);
				player.getInventory().deleteItem(37490, 1000000000);
				player.sm(Colors.gold + "[CASINO] " + Colors.green
						+ "You successfully Exchange 1B Casino Cash to 100M GP!");
				end();
				return;
			}

			break;
		}

	}

	public static boolean CasinoArea(WorldTile tile, Player player) {
		int destX = player.getX();
		int destY = player.getY();
		return (destX >= 5872 && destY >= 4658 && destX <= 5905 && destY <= 4690);
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}