package com.rs.game.player.dialogue.impl;

import com.rs.game.item.Item;
import com.rs.game.player.Equipment;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public class PvpSpawn extends Dialogue {

	@Override
	public void start() {
		sendOptionsDialogue("Choose an Option", "Food", "Potions", "Veng", "Barrage", Colors.red + "More Options");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		switch (stage) {
		case -1:

			if (componentId == OPTION_1) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}

				player.getInventory().addItem(15273, 500);

				end();
			}
			if (componentId == OPTION_2) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getInventory().addItem(2437, 500);
				player.getInventory().addItem(2441, 500);
				player.getInventory().addItem(2443, 500);
				player.getInventory().addItem(6686, 500);
				player.getInventory().addItem(3025, 500);
				player.getInventory().addItem(26636, 800);
				end();

			}
			if (componentId == OPTION_3) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(9075, 1000);
				player.getInventory().addItem(557, 1000);

				end();
			}
			if (componentId == OPTION_4) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getInventory().addItem(555, 1000);
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(565, 1000);
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "Void Set", "Zerker", "Tank", "2006 set",
						Colors.red + "More Options");
				stage = 2;

			}
			break;
		case 2:
			if (componentId == OPTION_1) { // VOID SET
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(11665));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(6585));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(4151));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(8850));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(9751));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(8839));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(8840));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(6737));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(8842));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(10925, 1);
				player.getInventory().addItem(6685, 1);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(10925, 1);
				player.getInventory().addItem(2440, 1);
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(9075, 1000);
				player.getInventory().addItem(557, 1000);

				end();
			}
			if (componentId == OPTION_2) { // ZERKER
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(3751));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(6585));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(4151));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(8850));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(9751));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(1127));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(1079));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(2550));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(4131));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(7462));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(5698, 1);
				player.getInventory().addItem(2440, 1);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(10925, 1);
				player.getInventory().addItem(557, 1000);
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(9075, 1000);

				end();
			}
			if (componentId == OPTION_3) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(11335));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(6585));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(9185));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(10499));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(10386));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(1079));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(6733));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(7462));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD, Equipment.SLOT_FEET,
						Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA, Equipment.SLOT_CAPE,
						Equipment.SLOT_ARROWS);
				player.getInventory().addItem(9244, 500000);
				player.getInventory().addItem(10925, 2);
				player.getInventory().addItem(6685, 2);
				player.getInventory().addItem(10925, 2);
				player.getInventory().addItem(6685, 6);
				player.getInventory().addItem(2440, 1);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(2444, 1);
				player.getInventory().addItem(557, 1000);
				player.getInventory().addItem(1127, 1);
				player.getInventory().addItem(5680, 1);
				player.getInventory().addItem(9075, 1000);
				player.getInventory().addItem(560, 1000);
				end();
			}
			if (componentId == OPTION_4) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(11335));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(65851));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(1201));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(1333));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(1019));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(1127));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(1079));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(1061));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(1059));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD, Equipment.SLOT_FEET,
						Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA, Equipment.SLOT_CAPE,
						Equipment.SLOT_ARROWS);
				player.getInventory().addItem(1319, 1);
				player.getInventory().addItem(113, 1);
				player.getInventory().addItem(544, 1);
				player.getInventory().addItem(542, 1);
				player.getInventory().addItem(1373, 1);
				player.getInventory().addItem(373, 1);
				player.getInventory().addItem(1169, 1);
				player.getInventory().addItem(1171, 1);
				player.getInventory().addItem(373, 20);
				end();

			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "Dharok Set", "Verac Set", "Torag Set",
						Colors.red + "More options");
				stage = 3;

			}
			break;
		case 3:
			if (componentId == OPTION_1) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(4716));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(6585));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(4720));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(4718));
				// player.getEquipment().set(Equipment.SLOT_SHIELD, new
				// Item(1019));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(9751));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(4722));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(6737));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(11840));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(7462));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(2440, 1);//
				player.getInventory().addItem(2442, 1);//
				player.getInventory().addItem(8850, 1);//
				player.getInventory().addItem(6685, 1);//
				player.getInventory().addItem(10925, 2);//
				player.getInventory().addItem(4151, 1);
				player.getInventory().addItem(5680, 1);
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(9075, 1000);
				player.getInventory().addItem(557, 1000);
				end();
			}
			if (componentId == OPTION_2) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(4753));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(6585));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(4757));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(4755));
				// player.getEquipment().set(Equipment.SLOT_SHIELD, new
				// Item(1019));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(9748));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(4759));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(6737));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(11840));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(7462));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(2440, 1);//
				player.getInventory().addItem(2442, 1);//
				player.getInventory().addItem(8850, 1);//
				player.getInventory().addItem(6685, 1);//
				player.getInventory().addItem(10925, 2);//
				player.getInventory().addItem(4151, 1);
				player.getInventory().addItem(5680, 1);
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(9075, 1000);
				player.getInventory().addItem(557, 1000);
				end();
			}
			if (componentId == OPTION_3) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(4748));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(6585));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(4749));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(4747));
				// player.getEquipment().set(Equipment.SLOT_SHIELD, new
				// Item(1019));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(9753));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(4751));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(6737));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(11840));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(7462));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(2440, 1);//
				player.getInventory().addItem(2442, 1);//
				player.getInventory().addItem(8850, 1);//
				player.getInventory().addItem(6685, 1);//
				player.getInventory().addItem(10925, 2);//
				player.getInventory().addItem(4151, 1);
				player.getInventory().addItem(5680, 1);
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(9075, 1000);
				player.getInventory().addItem(557, 1000);
				end();
			}
			if (componentId == OPTION_4) {
				sendOptionsDialogue("Choose an Option", "Ghutans", "Karils", "Ahrims", Colors.red + "More Options");

				stage = 4;

			}
			break;
		case 4:
			if (componentId == OPTION_1) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(4724));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(6585));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(4728));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(4726));
				// player.getEquipment().set(Equipment.SLOT_SHIELD, new
				// Item(1019));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(9769));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(4730));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(6737));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(11840));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(7462));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(2440, 1);//
				player.getInventory().addItem(2442, 1);//
				player.getInventory().addItem(8850, 1);//
				player.getInventory().addItem(6685, 1);//
				player.getInventory().addItem(10925, 2);//
				player.getInventory().addItem(4151, 1);
				player.getInventory().addItem(5680, 1);
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(9075, 1000);
				player.getInventory().addItem(557, 1000);
				end();
			}
			if (componentId == OPTION_2) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(4732));
				player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(4740, 5000));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(6585));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(4736));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(4734));
				// player.getEquipment().set(Equipment.SLOT_SHIELD, new
				// Item(1019));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(9757));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(4738));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(6733));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(11840));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(7462));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(2440, 1);//
				player.getInventory().addItem(2444, 1);//
				player.getInventory().addItem(5680, 1);//
				player.getInventory().addItem(6685, 1);//
				player.getInventory().addItem(10925, 2);//
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(9244, 1000);
				player.getInventory().addItem(11212, 1000);
				player.getInventory().addItem(9075, 1000);
				player.getInventory().addItem(4225, 1);
				player.getInventory().addItem(9185, 1);
				player.getInventory().addItem(4212, 1);
				player.getInventory().addItem(557, 1000);
				end();
			}
			if (componentId == OPTION_3) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(4708));
				// player.getEquipment().set(Equipment.SLOT_ARROWS, new
				// Item(4740, 5000));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(6585));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(4712));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(4710));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(3842));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(2412));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(4714));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(6733));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(11840));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(7462));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(2503, 1);
				player.getInventory().addItem(4151, 1);
				player.getInventory().addItem(10828, 1);//
				player.getInventory().addItem(1127, 1);//
				player.getInventory().addItem(8850, 1);//
				player.getInventory().addItem(1079, 1);
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(6685, 1);
				player.getInventory().addItem(10925, 1);
				player.getInventory().addItem(2440, 1);
				player.getInventory().addItem(565, 1000);
				player.getInventory().addItem(6685, 1);
				player.getInventory().addItem(10925, 1);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(555, 1000);
				end();
			}
			if (componentId == OPTION_4) {
				sendOptionsDialogue("Choose an Option", "TANK BRID SET", "PURE BRID SET", "PURE SET", "Ghostly Set",
						Colors.red + "Back");

				stage = 5;

			}
			break;
		case 5:
			if (componentId == OPTION_1) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(10828));
				player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(9244, 5000));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(1712));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(4091));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(4675));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(4224));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(2412));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(4093));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(2550));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(4097));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(7462));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(385, 1);
				player.getInventory().addItem(11212, 1000);
				player.getInventory().addItem(9185, 1);//
				player.getInventory().addItem(4751, 1);//
				player.getInventory().addItem(2503, 1);//
				player.getInventory().addItem(10499, 1);//
				player.getInventory().addItem(2444, 1);
				player.getInventory().addItem(10925, 1);
				player.getInventory().addItem(555, 1000);
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(565, 1000);
				player.getInventory().addItem(385, 12);
				player.getInventory().addItem(3144, 4);
				end();
			}
			if (componentId == OPTION_2) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(6109));
				player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(9244, 5000));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(1712));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(6107));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(4675));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(3842));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(3414));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(6108));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(2550));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(2579));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(7458));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(9185, 1);
				player.getInventory().addItem(10499, 1);
				player.getInventory().addItem(12383, 1);
				player.getInventory().addItem(4587, 1);
				player.getInventory().addItem(5698, 1);
				player.getInventory().addItem(2440, 1);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(2444, 1);
				player.getInventory().addItem(2434, 1);
				player.getInventory().addItem(385, 12);
				player.getInventory().addItem(3144, 4);
				player.getInventory().addItem(555, 1000);
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(565, 1000);
				end();
			}
			if (componentId == OPTION_3) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(4502));
				player.getEquipment().set(Equipment.SLOT_ARROWS, new Item(892, 5000));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(1712));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(6107));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(861));
				// player.getEquipment().set(Equipment.SLOT_SHIELD, new
				// Item(1019));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(10499));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(12383));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(2550));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(3105));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(7458));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(4587, 1);
				player.getInventory().addItem(4153, 1);
				player.getInventory().addItem(5698, 1);
				player.getInventory().addItem(2440, 1);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(2444, 1);
				player.getInventory().addItem(2434, 1);
				player.getInventory().addItem(385, 17);
				player.getInventory().addItem(3144, 4);
				end();
			}
			if (componentId == OPTION_4) {
				if (player.getInventory().getFreeSlots() == 0) {
					player.sendMessage("You must bank all of your items before doing this.");
					end();
					return;
				}
				player.getEquipment().set(Equipment.SLOT_HAT, new Item(6109));
				// player.getEquipment().set(Equipment.SLOT_ARROWS, new
				// Item(4740, 5000));
				player.getEquipment().set(Equipment.SLOT_AMULET, new Item(6585));
				player.getEquipment().set(Equipment.SLOT_CHEST, new Item(6107));
				player.getEquipment().set(Equipment.SLOT_WEAPON, new Item(4675));
				player.getEquipment().set(Equipment.SLOT_SHIELD, new Item(3842));
				player.getEquipment().set(Equipment.SLOT_CAPE, new Item(6111));
				player.getEquipment().set(Equipment.SLOT_LEGS, new Item(6108));
				player.getEquipment().set(Equipment.SLOT_RING, new Item(6737));
				player.getEquipment().set(Equipment.SLOT_FEET, new Item(6106));
				player.getEquipment().set(Equipment.SLOT_HANDS, new Item(6110));
				player.getEquipment().refresh(Equipment.SLOT_AMULET, Equipment.SLOT_HAT, Equipment.SLOT_FEET,
						Equipment.SLOT_HANDS, Equipment.SLOT_WEAPON, Equipment.SLOT_LEGS, Equipment.SLOT_SHIELD,
						Equipment.SLOT_FEET, Equipment.SLOT_CHEST, Equipment.SLOT_RING, Equipment.SLOT_AURA,
						Equipment.SLOT_CAPE, Equipment.SLOT_ARROWS);
				player.getInventory().addItem(2503, 1);
				player.getInventory().addItem(4151, 1);
				player.getInventory().addItem(10828, 1);//
				player.getInventory().addItem(1127, 1);//
				player.getInventory().addItem(8850, 1);//
				player.getInventory().addItem(1079, 1);
				player.getInventory().addItem(11726, 2);//
				player.getInventory().addItem(560, 1000);
				player.getInventory().addItem(6685, 1);
				player.getInventory().addItem(10925, 1);
				player.getInventory().addItem(2440, 1);
				player.getInventory().addItem(565, 1000);
				player.getInventory().addItem(6685, 1);
				player.getInventory().addItem(10925, 1);
				player.getInventory().addItem(2436, 1);
				player.getInventory().addItem(555, 1000);
				end();
			}
			if (componentId == OPTION_5) {
				sendOptionsDialogue("Choose an Option", "Food", "Potions", "Veng", "Barrage",
						Colors.red + "More Options");

				stage = -1;

			}
			break;

		default:
			end();
		}

	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub

	}

}
