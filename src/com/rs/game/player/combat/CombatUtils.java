package com.rs.game.player.combat;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;

/**
 * @author Jetset I'm not sure if this is the right name to pick or place to put
 *         it
 *         <p>
 *         http://runescape.wikia.com/wiki/Attack_range
 */
public class CombatUtils {

	public static boolean isRangingDistance(Player player) {
		return isRanging(player) || isMaging(player);
	}

	public static boolean isRanging(Player player) {
		return PlayerCombat.isRanging(player) == 2;
	}

	public static boolean isMaging(Player player) {
		return player.getCombatDefinitions().getSpellId() > 0;
	}

	public static int getAttackDistance(Player player) {
		int weapon = player.getEquipment().getWeaponId();
		ItemDefinitions itemDef = ItemDefinitions.getItemDefinitions(weapon);
		int attackStyle = player.getCombatDefinitions().getAttackStyle();
		int modifier = 0;
		int distance = 1;
		boolean isRangingDistance = isRangingDistance(player);

		if (attackStyle == 2 && isRangingDistance) { // Long Range attack style
			modifier += 2;
		}

		if (weapon == -1 || itemDef == null) {
			return isRangingDistance ? 7 + modifier : 1 + modifier;
		}

		String weaponName = itemDef.getName().toLowerCase();
		if (weaponName.contains("snowball")) {
			return 10;
		}
		if (PlayerCombat.hasPolyporeStaff(player)) {
			return 7;
		}

		if (isRanging(player)) {
			if (weaponName.contains("dart") || weaponName.contains("thrownaxe")) {
				distance = 4;
			} else if (weaponName.contains("knives") || weaponName.contains("javelin")
					|| weaponName.contains("blisterwood")) {
				distance = 5;
			} else if (weaponName.contains("death lotus") || weaponName.contains("shadow glaive")) {
				distance = 6;
			} else if (weaponName.contains("crossbow") && !weaponName.contains("wyvern")
					|| weaponName.contains("shortbow") || weaponName.contains("morrigan's javelin")
					|| weaponName.contains("seercull") || weaponName.contains("crystal bow")
					|| weaponName.contains("zaryte") || weaponName.contains("boogie")
					|| weaponName.contains("starfire bow") || weaponName.contains("Winds of Waiko")) {
				distance = 7;
			} else if (weaponName.contains("shieldbows")) {
				distance = 8;
			} else if (weaponName.contains("slighted shieldbow") || weaponName.contains("composite bow")
					|| weaponName.contains("sagaie") || weaponName.contains("royal crossbow")
					|| weaponName.contains("wyvern crossbow") || weaponName.contains("noxious longbow")) {
				distance = 9;
			} else {
				distance = 7;
			}
		} else if (isMaging(player)) { // magic spells
			distance = 7;
		} else { // melee checking
			if (weaponName.contains("halberd")) {
				modifier += 1;
			}
			distance = 1;
		}

		return distance + modifier;
	}

	public static final boolean fullGuthanEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Guthan")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Guthan")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Guthan")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Guthan");
	}

	public static final boolean berserkerNecklace(Player player) {
		int amuletId = player.getEquipment().getAmuletId();
		int weaponId = player.getEquipment().getWeaponId();
		if (amuletId == 11128) {
			if (weaponId == 6527 || weaponId == 6539 || weaponId == 6523 || weaponId == 6526)
				return true;
		}
		return false;
	}

	public static final boolean fullHantoEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		int bootsId = player.getEquipment().getBootsId();
		int glovesId = player.getEquipment().getGlovesId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1 || bootsId == -1 || glovesId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Hanto")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Hanto")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Hanto")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Hanto")
				&& ItemDefinitions.getItemDefinitions(bootsId).getName().contains("Hanto")
				&& ItemDefinitions.getItemDefinitions(glovesId).getName().contains("Hanto");
	}
	
	public static final boolean fullVanguardEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		int bootsId = player.getEquipment().getBootsId();
		int glovesId = player.getEquipment().getGlovesId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1 || bootsId == -1 || glovesId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Vanguard")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Vanguard")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Vanguard")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Vanguard")
				&& ItemDefinitions.getItemDefinitions(bootsId).getName().contains("Vanguard")
				&& ItemDefinitions.getItemDefinitions(glovesId).getName().contains("Vanguard");
	}

	public static final boolean usingGoliathGloves(Player player) {
		String name = player.getEquipment().getItem(Equipment.SLOT_SHIELD) != null
				? player.getEquipment().getItem(Equipment.SLOT_SHIELD).getDefinitions().getName().toLowerCase()
				: "";
		if (player.getEquipment().getItem((Equipment.SLOT_HANDS)) != null) {
			if (player.getEquipment().getItem(Equipment.SLOT_HANDS).getDefinitions().getName().toLowerCase()
					.contains("goliath") && player.getEquipment().getWeaponId() == -1) {
				if (name.contains("defender") && name.contains("dragonfire shield"))
					return true;
				return true;
			}
		}
		return false;
	}

	public static final boolean fullDharokEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Dharok's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Dharok's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Dharok's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Dharok's");
	}

	public static final boolean fullVeracsEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Verac's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Verac's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Verac's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Verac's");
	}

	public static final boolean fullAkrisaesEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Akrisae's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Akrisae's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Akrisae's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Akrisae's");
	}

	public static final boolean fullLinzasEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		int shieldId = player.getEquipment().getShieldId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1 || shieldId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Linza's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Linza's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Linza's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Linza's")
				&& ItemDefinitions.getItemDefinitions(shieldId).getName().contains("Linza's");
	}

	public static final boolean fullAhrimsEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Ahrim's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Ahrim's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Ahrim's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Ahrim's");
	}

	public static final boolean fullToragsEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Torag's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Torag's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Torag's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Torag's");
	}

	public static final boolean fullKarilsEquipped(Player player) {
		int helmId = player.getEquipment().getHatId();
		int chestId = player.getEquipment().getChestId();
		int legsId = player.getEquipment().getLegsId();
		int weaponId = player.getEquipment().getWeaponId();
		if (helmId == -1 || chestId == -1 || legsId == -1 || weaponId == -1)
			return false;
		return ItemDefinitions.getItemDefinitions(helmId).getName().contains("Karil's")
				&& ItemDefinitions.getItemDefinitions(chestId).getName().contains("Karil's")
				&& ItemDefinitions.getItemDefinitions(legsId).getName().contains("Karil's")
				&& ItemDefinitions.getItemDefinitions(weaponId).getName().contains("Karil's");
	}

	public static final boolean fullVoidEquipped(Player player, int... helmid) {
		boolean hasDeflector = player.getEquipment().getShieldId() == 19712;
		if (player.getEquipment().getGlovesId() != 8842) {
			if (hasDeflector)
				hasDeflector = false;
			else
				return false;
		}
		int legsId = player.getEquipment().getLegsId();
		boolean hasLegs = legsId != -1 && (legsId == 8840 || legsId == 19786 || legsId == 19788 || legsId == 19790);
		if (!hasLegs) {
			if (hasDeflector)
				hasDeflector = false;
			else
				return false;
		}
		int torsoId = player.getEquipment().getChestId();
		boolean hasTorso = torsoId != -1
				&& (torsoId == 8839 || torsoId == 10611 || torsoId == 19785 || torsoId == 19787 || torsoId == 19789);
		if (!hasTorso) {
			if (hasDeflector)
				hasDeflector = false;
			else
				return false;
		}
		if (hasDeflector)
			return true;
		int helmId = player.getEquipment().getHatId();
		if (helmId == -1)
			return false;
		boolean hasHelm = false;
		for (int id : helmid) {
			if (helmId == id) {
				hasHelm = true;
				break;
			}
		}
		if (!hasHelm)
			return false;
		return true;
	}
}
