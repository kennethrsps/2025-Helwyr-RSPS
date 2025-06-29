package com.rs.game.player.content;

import java.util.List;

import com.rs.Settings;
import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.activites.clanwars.FfaZone;
import com.rs.game.activites.clanwars.RequestController;
import com.rs.game.activites.duel.DuelArena;
import com.rs.game.item.Item;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.actions.Cooking.Cookables;
import com.rs.game.player.actions.DreamSpellAction;
import com.rs.game.player.actions.WaterFilling.Fill;
import com.rs.game.player.content.Burying.Bone;
import com.rs.game.player.content.dungeoneering.DungeonConstants;
import com.rs.game.player.content.interfaces.potionTimer.PotionTimersInter;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Logger;
import com.rs.utils.Utils;

/*
 * content package used for static stuff
 */
public class Magic {

	public static final int MAGIC_TELEPORT = 0, ITEM_TELEPORT = 1, OBJECT_TELEPORT = 2;

	public static final int AIR_RUNE = 556, WATER_RUNE = 555;

	public static final int EARTH_RUNE = 557;

	private static final int FIRE_RUNE = 554;

	private static final int MIND_RUNE = 558;

	public static final int NATURE_RUNE = 561;

	private static final int CHAOS_RUNE = 562;

	private static final int DEATH_RUNE = 560;

	private static final int BLOOD_RUNE = 565;

	public static final int DUNG_AIR_RUNE = 17780, DUNG_WATER_RUNE = 17781, DUNG_EARTH_RUNE = 17782,
			DUNG_FIRE_RUNE = 17783, DUNG_MIND_RUNE = 17784, DUNG_CHAOS_RUNE = 17785, DUNG_DEATH_RUNE = 17786,
			DUNG_BLOOD_RUNE = 17787, DUNG_BODY_RUNE = 17788, DUNG_COSMIC_RUNE = 17789, DUNG_ASTRAL_RUNE = 17790,
			DUNG_NATURE_RUNE = 17791, DUNG_LAW_RUNE = 17792, DUNG_SOUL_RUNE = 17793;
	private static final int SOUL_RUNE = 566;

	public static final int ASTRAL_RUNE = 9075;

	private static final int LAW_RUNE = 563;

	@SuppressWarnings("unused")
	private static final int STEAM_RUNE = 4694;

	@SuppressWarnings("unused")
	private static final int MIST_RUNE = 4695;

	@SuppressWarnings("unused")
	private static final int DUST_RUNE = 4696;

	@SuppressWarnings("unused")
	private static final int SMOKE_RUNE = 4697;

	@SuppressWarnings("unused")
	private static final int MUD_RUNE = 4698;

	@SuppressWarnings("unused")
	private static final int LAVA_RUNE = 4699;

	private static final int ARMADYL_RUNE = 21773;

	private static final int BODY_RUNE = 559;

	private static final int COSMIC_RUNE = 564;

	private final static WorldTile[] TABS = { new WorldTile(3217, 3426, 0), new WorldTile(3222, 3218, 0),
			new WorldTile(2965, 3379, 0), new WorldTile(2758, 3478, 0), new WorldTile(2660, 3306, 0) };

	private final static WorldTile[] SCROLLS = { new WorldTile(3361, 2970, 0), new WorldTile(3171, 2982, 0),
			new WorldTile(2515, 3861, 0), new WorldTile(3535, 5189, 0), new WorldTile(2796, 3085, 0),
			new WorldTile(3308, 3491, 0) };

	public static final boolean checkCombatSpell(Player player, int spellId, int set, boolean delete) {
		if (spellId == 65535)
			return true;
		if (spellId == 2790)
			return true;
		if (spellId == 2472)
			return true;
		if (spellId == 2473)
			return true;
		if (spellId == 2474)
			return true;
		if (spellId == 2572)
			return true;
		if (spellId == 2672)
			return true;
		if (spellId == 2673)
			return true;
		if (spellId == 2675)
			return true;
		if (spellId == 2680)
			return true;
		if (spellId == 2690)
			return true;
		switch (player.getCombatDefinitions().getSpellBook()) {
		case 193:
			switch (spellId) {
			case 28:
				if (!checkSpellRequirements(player, 50, delete, CHAOS_RUNE, 2, DEATH_RUNE, 2, FIRE_RUNE, 1, AIR_RUNE,
						1))
					return false;
				break;
			case 32:
				if (!checkSpellRequirements(player, 52, delete, CHAOS_RUNE, 2, DEATH_RUNE, 2, AIR_RUNE, 1, SOUL_RUNE,
						1))
					return false;
				break;
			case 24:
				if (!checkSpellRequirements(player, 56, delete, CHAOS_RUNE, 2, DEATH_RUNE, 2, BLOOD_RUNE, 1))
					return false;
				break;
			case 20:
				if (!checkSpellRequirements(player, 58, delete, CHAOS_RUNE, 2, DEATH_RUNE, 2, WATER_RUNE, 2))
					return false;
				break;
			case 30:
				if (!checkSpellRequirements(player, 62, delete, CHAOS_RUNE, 4, DEATH_RUNE, 2, FIRE_RUNE, 2, AIR_RUNE,
						2))
					return false;
				break;
			case 34:
				if (!checkSpellRequirements(player, 64, delete, CHAOS_RUNE, 4, DEATH_RUNE, 2, AIR_RUNE, 1, SOUL_RUNE,
						2))
					return false;
				break;
			case 26:
				if (!checkSpellRequirements(player, 68, delete, CHAOS_RUNE, 4, DEATH_RUNE, 2, BLOOD_RUNE, 2))
					return false;
				break;
			case 22:
				if (!checkSpellRequirements(player, 70, delete, CHAOS_RUNE, 4, DEATH_RUNE, 2, WATER_RUNE, 4))
					return false;
				break;
			case 29:
				if (!checkSpellRequirements(player, 74, delete, DEATH_RUNE, 2, BLOOD_RUNE, 2, FIRE_RUNE, 2, AIR_RUNE,
						2))
					return false;
				break;
			case 33:
				if (!checkSpellRequirements(player, 76, delete, DEATH_RUNE, 2, BLOOD_RUNE, 2, AIR_RUNE, 2, SOUL_RUNE,
						2))
					return false;
				break;
			case 25:
				if (!checkSpellRequirements(player, 80, delete, DEATH_RUNE, 2, BLOOD_RUNE, 4))
					return false;
				break;
			case 21:
				if (!checkSpellRequirements(player, 82, delete, DEATH_RUNE, 2, BLOOD_RUNE, 2, WATER_RUNE, 3))
					return false;
				break;
			case 31:
				if (!checkSpellRequirements(player, 86, delete, DEATH_RUNE, 4, BLOOD_RUNE, 2, FIRE_RUNE, 4, AIR_RUNE,
						4))
					return false;
				break;
			case 35:
				if (!checkSpellRequirements(player, 88, delete, DEATH_RUNE, 4, BLOOD_RUNE, 2, AIR_RUNE, 4, SOUL_RUNE,
						3))
					return false;
				break;
			case 27:
				if (!checkSpellRequirements(player, 92, delete, DEATH_RUNE, 4, BLOOD_RUNE, 4, SOUL_RUNE, 1))
					return false;
				break;
			case 23:
				if (!checkSpellRequirements(player, 94, delete, DEATH_RUNE, 4, BLOOD_RUNE, 2, WATER_RUNE, 6))
					return false;
				break;
			case 36: // Miasmic rush.
				if (!checkSpellRequirements(player, 61, delete, CHAOS_RUNE, 2, EARTH_RUNE, 1, SOUL_RUNE, 1)) {
					return false;
				}
				int weaponId = player.getEquipment().getWeaponId();
				if (weaponId != 13867 && weaponId != 13869 && weaponId != 13941 && weaponId != 13943
						&& !player.isSilver()) {
					player.getPackets().sendGameMessage("You need a Zuriel's staff to cast this spell.");
					player.getPackets()
							.sendGameMessage("Extreme donators can cast Miasmic spells without Zuriel's staff.");
					return false;
				}
				break;
			case 38: // Miasmic burst.
				if (!checkSpellRequirements(player, 73, delete, CHAOS_RUNE, 4, EARTH_RUNE, 2, SOUL_RUNE, 2)) {
					return false;
				}
				weaponId = player.getEquipment().getWeaponId();
				if (weaponId != 13867 && weaponId != 13869 && weaponId != 13941 && weaponId != 13943
						&& !player.isSilver()) {
					player.getPackets().sendGameMessage("You need a Zuriel's staff to cast this spell.");
					player.getPackets()
							.sendGameMessage("Extreme donators can cast Miasmic spells without Zuriel's staff.");
					return false;
				}
				break;
			case 37: // Miasmic blitz.
				if (!checkSpellRequirements(player, 85, delete, BLOOD_RUNE, 2, EARTH_RUNE, 3, SOUL_RUNE, 3)) {
					return false;
				}
				weaponId = player.getEquipment().getWeaponId();
				if (weaponId != 13867 && weaponId != 13869 && weaponId != 13941 && weaponId != 13943
						&& !player.isSilver()) {
					player.getPackets().sendGameMessage("You need a Zuriel's staff to cast this spell.");
					player.getPackets()
							.sendGameMessage("Extreme donators can cast Miasmic spells without Zuriel's staff.");
					return false;
				}
				break;
			case 39: // Miasmic barrage.
				if (!checkSpellRequirements(player, 97, delete, BLOOD_RUNE, 4, EARTH_RUNE, 4, SOUL_RUNE, 4)) {
					return false;
				}
				weaponId = player.getEquipment().getWeaponId();
				if (weaponId != 13867 && weaponId != 13869 && weaponId != 13941 && weaponId != 13943
						&& !player.isSilver()) {
					player.getPackets().sendGameMessage("You need a Zuriel's staff to cast this spell.");
					player.getPackets()
							.sendGameMessage("Extreme donators can cast Miasmic spells without Zuriel's staff.");
					return false;
				}
				break;
			default:
				return false;
			}
			break;
		case 192:
			switch (spellId) {
			case 25:
				if (!checkSpellRequirements(player, 1, delete, AIR_RUNE, 1, MIND_RUNE, 1))
					return false;
				break;
			case 28:
				if (!checkSpellRequirements(player, 5, delete, WATER_RUNE, 1, AIR_RUNE, 1, MIND_RUNE, 1))
					return false;
				break;
			case 30:
				if (!checkSpellRequirements(player, 9, delete, EARTH_RUNE, 2, AIR_RUNE, 1, MIND_RUNE, 1))
					return false;
				break;
			case 32:
				if (!checkSpellRequirements(player, 13, delete, FIRE_RUNE, 3, AIR_RUNE, 2, MIND_RUNE, 1))
					return false;
				break;
			case 34: // air bolt
				if (!checkSpellRequirements(player, 17, delete, AIR_RUNE, 2, CHAOS_RUNE, 1))
					return false;
				break;
			case 36:// bind
				if (!checkSpellRequirements(player, 20, delete, EARTH_RUNE, 3, WATER_RUNE, 3, NATURE_RUNE, 2))
					return false;
				break;
			case 55: // snare
				if (!checkSpellRequirements(player, 50, delete, EARTH_RUNE, 4, WATER_RUNE, 4, NATURE_RUNE, 3))
					return false;
				break;
			case 81:// entangle
				if (!checkSpellRequirements(player, 79, delete, EARTH_RUNE, 5, WATER_RUNE, 5, NATURE_RUNE, 4))
					return false;
				break;
			case 82: // stun
				if (!checkSpellRequirements(player, 80, delete, WATER_RUNE, 12, EARTH_RUNE, 12, SOUL_RUNE, 1))
					return false;
				break;
			case 39: // water bolt
				if (!checkSpellRequirements(player, 23, delete, WATER_RUNE, 2, AIR_RUNE, 2, CHAOS_RUNE, 1))
					return false;
				break;
			case 42: // earth bolt
				if (!checkSpellRequirements(player, 29, delete, EARTH_RUNE, 3, AIR_RUNE, 2, CHAOS_RUNE, 1))
					return false;
				break;
			case 45: // fire bolt
				if (!checkSpellRequirements(player, 35, delete, FIRE_RUNE, 4, AIR_RUNE, 3, CHAOS_RUNE, 1))
					return false;
				break;
			case 49: // air blast
				if (!checkSpellRequirements(player, 41, delete, AIR_RUNE, 3, DEATH_RUNE, 1))
					return false;
				break;
			case 52: // water blast
				if (!checkSpellRequirements(player, 47, delete, WATER_RUNE, 3, AIR_RUNE, 3, DEATH_RUNE, 1))
					return false;
				break;
			case 58: // earth blast
				if (!checkSpellRequirements(player, 53, delete, EARTH_RUNE, 4, AIR_RUNE, 3, DEATH_RUNE, 1))
					return false;
				break;
			case 63: // fire blast
				if (!checkSpellRequirements(player, 59, delete, FIRE_RUNE, 5, AIR_RUNE, 4, DEATH_RUNE, 1))
					return false;
				break;
			case 70: // air wave
				if (!checkSpellRequirements(player, 62, delete, AIR_RUNE, 5, BLOOD_RUNE, 1))
					return false;
				break;
			case 73: // water wave
				if (!checkSpellRequirements(player, 65, delete, WATER_RUNE, 7, AIR_RUNE, 5, BLOOD_RUNE, 1))
					return false;
				break;
			case 77: // earth wave
				if (!checkSpellRequirements(player, 70, delete, EARTH_RUNE, 7, AIR_RUNE, 5, BLOOD_RUNE, 1))
					return false;
				break;
			case 80: // fire wave
				if (!checkSpellRequirements(player, 75, delete, FIRE_RUNE, 7, AIR_RUNE, 5, BLOOD_RUNE, 1))
					return false;
				break;
			case 56: // slayer dart
				if (player.getEquipment().getWeaponId() != 4170) {
					player.sendMessage("You need to be equipping a Slayer's staff to cast this spell.");
					return false;
				}
				if (player.getSkills().getLevel(Skills.SLAYER) < 55) {
					player.sendMessage("You need at least 55 Slayer to cast this spell.");
					return false;
				}
				if (!checkSpellRequirements(player, 1, delete, DEATH_RUNE, 1, MIND_RUNE, 4))
					return false;
				break;
			case 84:
				if (!checkSpellRequirements(player, 81, delete, AIR_RUNE, 7, DEATH_RUNE, 1, BLOOD_RUNE, 1))
					return false;
				break;
			case 87:
				if (!checkSpellRequirements(player, 85, delete, WATER_RUNE, 10, AIR_RUNE, 7, DEATH_RUNE, 1, BLOOD_RUNE,
						1))
					return false;
				break;
			case 89:
				if (!checkSpellRequirements(player, 85, delete, EARTH_RUNE, 10, AIR_RUNE, 7, DEATH_RUNE, 1, BLOOD_RUNE,
						1))
					return false;
				break;
			case 66: // Sara Strike
				if (player.getEquipment().getWeaponId() != 2415) {
					player.getPackets()
							.sendGameMessage("You need to be equipping a Saradomin staff to cast this spell.", true);
					return false;
				}
				if (!checkSpellRequirements(player, 60, delete, AIR_RUNE, 4, FIRE_RUNE, 1, BLOOD_RUNE, 2))
					return false;
				break;
			case 67: // Guthix Claws
				if (player.getEquipment().getWeaponId() != 2416) {
					player.getPackets().sendGameMessage(
							"You need to be equipping a Guthix Staff or Void Mace to cast this spell.", true);
					return false;
				}
				if (!checkSpellRequirements(player, 60, delete, AIR_RUNE, 4, FIRE_RUNE, 1, BLOOD_RUNE, 2))
					return false;
				break;
			case 68: // Flame of Zammy
				if (player.getEquipment().getWeaponId() != 2417) {
					player.getPackets().sendGameMessage("You need to be equipping a Zamorak Staff to cast this spell.",
							true);
					return false;
				}
				if (!checkSpellRequirements(player, 60, delete, AIR_RUNE, 4, FIRE_RUNE, 4, BLOOD_RUNE, 2))
					return false;
				break;
			case 91:
				if (!checkSpellRequirements(player, 95, delete, FIRE_RUNE, 10, AIR_RUNE, 7, DEATH_RUNE, 1, BLOOD_RUNE,
						1))
					return false;
				break;
			case 86: // teleblock
				if (!checkSpellRequirements(player, 85, delete, CHAOS_RUNE, 1, LAW_RUNE, 1, DEATH_RUNE, 1))
					return false;
				break;
			case 99: // Storm of Armadyl
				if (!checkSpellRequirements(player, 77, delete, ARMADYL_RUNE, 1))
					return false;
				break;
			default:
				return false;
			}
			break;
		case 950:
			switch (spellId) {
			case 25:
				if (!checkSpellRequirements(player, 1, delete, true, Magic.DUNG_AIR_RUNE, 1, Magic.DUNG_MIND_RUNE, 1))
					return false;
				break;
			case 27:
				if (!checkSpellRequirements(player, 5, delete, true, Magic.DUNG_WATER_RUNE, 1, Magic.DUNG_AIR_RUNE, 1,
						Magic.DUNG_MIND_RUNE, 1))
					return false;
				break;
			case 28:
				if (!checkSpellRequirements(player, 9, delete, true, Magic.DUNG_EARTH_RUNE, 2, Magic.DUNG_AIR_RUNE, 1,
						Magic.DUNG_MIND_RUNE, 1))
					return false;
				break;
			case 30:
				if (!checkSpellRequirements(player, 13, delete, true, Magic.DUNG_FIRE_RUNE, 3, Magic.DUNG_AIR_RUNE, 2,
						Magic.DUNG_MIND_RUNE, 1))
					return false;
				break;
			case 32:
				if (!checkSpellRequirements(player, 17, delete, true, Magic.DUNG_AIR_RUNE, 2, Magic.DUNG_CHAOS_RUNE, 1))
					return false;
				break;
			case 36:
				if (!checkSpellRequirements(player, 23, delete, true, Magic.DUNG_WATER_RUNE, 2, Magic.DUNG_AIR_RUNE, 2,
						Magic.DUNG_CHAOS_RUNE, 1))
					return false;
				break;
			case 37:
				if (!checkSpellRequirements(player, 29, delete, true, Magic.DUNG_EARTH_RUNE, 3, Magic.DUNG_AIR_RUNE, 2,
						Magic.DUNG_CHAOS_RUNE, 1))
					return false;
				break;
			case 41:
				if (!checkSpellRequirements(player, 35, delete, true, Magic.DUNG_FIRE_RUNE, 4, Magic.DUNG_AIR_RUNE, 3,
						Magic.DUNG_CHAOS_RUNE, 1))
					return false;
				break;
			case 42:
				if (!checkSpellRequirements(player, 41, delete, true, Magic.DUNG_AIR_RUNE, 3, Magic.DEATH_RUNE, 1))
					return false;
				break;
			case 43:
				if (!checkSpellRequirements(player, 47, delete, true, Magic.DUNG_WATER_RUNE, 3, Magic.DUNG_AIR_RUNE, 3,
						Magic.DEATH_RUNE, 1))
					return false;
				break;
			case 45:
				if (!checkSpellRequirements(player, 53, delete, true, Magic.DUNG_EARTH_RUNE, 4, Magic.DUNG_AIR_RUNE, 3,
						Magic.DUNG_DEATH_RUNE, 1))
					return false;
				break;
			case 47:
				if (!checkSpellRequirements(player, 59, delete, true, Magic.DUNG_FIRE_RUNE, 5, Magic.DUNG_AIR_RUNE, 4,
						Magic.DUNG_DEATH_RUNE, 1))
					return false;
				break;
			case 48:
				if (!checkSpellRequirements(player, 62, delete, true, Magic.DUNG_AIR_RUNE, 5, Magic.DUNG_BLOOD_RUNE, 1))
					return false;
				break;
			case 49:
				if (!checkSpellRequirements(player, 65, delete, true, Magic.DUNG_WATER_RUNE, 7, Magic.DUNG_AIR_RUNE, 5,
						Magic.DUNG_BLOOD_RUNE, 1))
					return false;
				break;
			case 54:
				if (!checkSpellRequirements(player, 70, delete, true, Magic.DUNG_EARTH_RUNE, 7, Magic.DUNG_AIR_RUNE, 5,
						Magic.DUNG_BLOOD_RUNE, 1))
					return false;
				break;
			case 58:
				if (!checkSpellRequirements(player, 75, delete, true, Magic.DUNG_FIRE_RUNE, 7, Magic.DUNG_AIR_RUNE, 5,
						Magic.DUNG_BLOOD_RUNE, 1))
					return false;
				break;
			case 61:
				if (!checkSpellRequirements(player, 81, delete, true, Magic.DUNG_AIR_RUNE, 7, Magic.DUNG_DEATH_RUNE, 1,
						Magic.DUNG_BLOOD_RUNE, 1))
					return false;
				break;
			case 62:
				if (!checkSpellRequirements(player, 85, delete, true, Magic.DUNG_WATER_RUNE, 10, Magic.DUNG_AIR_RUNE, 7,
						Magic.DUNG_DEATH_RUNE, 1, Magic.DUNG_BLOOD_RUNE, 1))
					return false;
				break;
			case 63:
				if (!checkSpellRequirements(player, 90, delete, true, Magic.DUNG_EARTH_RUNE, 10, Magic.DUNG_AIR_RUNE, 7,
						Magic.DUNG_DEATH_RUNE, 1, Magic.DUNG_BLOOD_RUNE, 1))
					return false;
				break;
			case 67:
				if (!checkSpellRequirements(player, 95, delete, true, Magic.DUNG_FIRE_RUNE, 10, Magic.DUNG_AIR_RUNE, 7,
						Magic.DUNG_DEATH_RUNE, 1, Magic.DUNG_BLOOD_RUNE, 1))
					return false;
				break;
			case 34:// bind
				if (!checkSpellRequirements(player, 20, delete, true, Magic.DUNG_EARTH_RUNE, 3, Magic.DUNG_WATER_RUNE,
						3, Magic.DUNG_NATURE_RUNE, 2))
					return false;
				break;
			case 44:// snare
				if (!checkSpellRequirements(player, 50, delete, true, Magic.DUNG_EARTH_RUNE, 4, Magic.DUNG_WATER_RUNE,
						4, Magic.DUNG_NATURE_RUNE, 3))
					return false;
				break;
			case 59:// entangle
				if (!checkSpellRequirements(player, 79, delete, true, Magic.DUNG_EARTH_RUNE, 5, Magic.DUNG_WATER_RUNE,
						5, Magic.DUNG_NATURE_RUNE, 4))
					return false;
				break;
			case 26:// confuse
				if (!checkSpellRequirements(player, 3, delete, true, Magic.DUNG_WATER_RUNE, 3, Magic.DUNG_EARTH_RUNE, 2,
						Magic.DUNG_BODY_RUNE, 1))
					return false;
				break;
			case 29:// weaken
				if (!checkSpellRequirements(player, 11, delete, true, Magic.DUNG_WATER_RUNE, 3, Magic.DUNG_EARTH_RUNE,
						2, Magic.DUNG_BODY_RUNE, 1))
					return false;
				break;
			case 33:// curse
				if (!checkSpellRequirements(player, 19, delete, true, Magic.DUNG_WATER_RUNE, 2, Magic.DUNG_EARTH_RUNE,
						3, Magic.DUNG_BODY_RUNE, 1))
					return false;
				break;
			case 50:// vurnability
				if (!checkSpellRequirements(player, 66, delete, true, Magic.DUNG_WATER_RUNE, 5, Magic.DUNG_EARTH_RUNE,
						5, Magic.DUNG_SOUL_RUNE, 1))
					return false;
				break;
			case 56:// enfeeble
				if (!checkSpellRequirements(player, 73, delete, true, Magic.DUNG_WATER_RUNE, 8, Magic.DUNG_EARTH_RUNE,
						8, Magic.DUNG_SOUL_RUNE, 1))
					return false;
				break;
			case 60:// stun
				if (!checkSpellRequirements(player, 80, delete, true, Magic.DUNG_WATER_RUNE, 12, Magic.DUNG_EARTH_RUNE,
						12, Magic.DUNG_SOUL_RUNE, 1))
					return false;
				break;
			default:
				return false;
			}
			break;
		default:
			Logger.log("Magic id: " + spellId + " spellbook:" + player.getCombatDefinitions().getSpellBook());
			return false;
		}
		if (set >= 0) {
			if (set == 0) {
				player.getCombatDefinitions().setAutoCastSpell(spellId);
				player.getPackets().sendGameMessage("Autocast spell selected.");
			} else {
				player.getTemporaryAttributtes().put("tempCastSpell", spellId);
			}
		}
		return true;
	}

	public static final boolean checkRunes(Player player, boolean delete, int... runes) {
		int weaponId = player.getEquipment().getWeaponId();
		int shieldId = player.getEquipment().getShieldId();
		int runesCount = 0;
		while (runesCount < runes.length) {
			int runeId = runes[runesCount++];
			int ammount = runes[runesCount++];
			if (hasInfiniteRunes(runeId, weaponId, shieldId))
				continue;
			if (hasStaffOfLight(weaponId) && Utils.getRandom(8) == 0 && runeId != 21773)
				continue;
			if (runeId == -1)
				continue;
			if (!player.getInventory().containsItem(runeId, ammount)) {
				player.sendMessage("You do not have enough "
						+ ItemDefinitions.getItemDefinitions(runeId).getName().replace("rune", "Rune")
						+ "s to cast this spell.");
				return false;
			}
		}
		if (delete) {
			if (player.getPerkManager().mastersorcerer && !containsRune(LAW_RUNE, runes)
					&& !containsRune(NATURE_RUNE, runes) && Utils.random(1, 5) == 2) {
				player.getPackets().sendGameMessage("Your perk saves some runes from being drained.", true);
				return true;
			}
			runesCount = 0;
			while (runesCount < runes.length) {
				int runeId = runes[runesCount++];
				int ammount = runes[runesCount++];
				if (hasInfiniteRunes(runeId, weaponId, shieldId))
					continue;
				player.getInventory().deleteItem(runeId, ammount);
			}
		}
		return true;
	}

	public static final boolean checkSpellRequirements(Player player, int level, boolean delete, boolean dung,
			int... runes) {
		if (player.getSkills().getLevel(Skills.MAGIC) < level) {
			player.sendMessage("Your Magic level is not high enough for this spell.");
			return false;
		}
		return checkRunes(player, delete, dung, runes);
	}

	public static final boolean checkSpellRequirements(Player player, int level, boolean delete, int... runes) {
		if (player.getSkills().getLevel(Skills.MAGIC) < level) {
			player.sendMessage("Your Magic level is not high enough for this spell.");
			return false;
		}
		return checkRunes(player, delete, runes);
	}

	public static final boolean hasInfiniteRunes(int runeId, int weaponId, int shieldId) {
		if (runeId == AIR_RUNE) {
			switch (weaponId) {
			case 1381:
			case 21777:
			case 1397:
			case 1405:
				return true;
			default:
				return false;
			}
		} else if (runeId == WATER_RUNE) {
			if (shieldId == 18346)
				return true;
			switch (weaponId) {
			case 1383:
			case 1395:
			case 1403:
			case 6562:
			case 6563:
			case 11736:
			case 11738:
				return true;
			default:
				return false;
			}
		} else if (runeId == EARTH_RUNE) {
			switch (weaponId) {
			case 1385:
			case 1399:
			case 1407:
			case 3053:
			case 3054:
			case 6562:
			case 6563:
				return true;
			default:
				return false;
			}
		} else if (runeId == FIRE_RUNE) {
			switch (weaponId) {
			case 1387:
			case 1401:
			case 3053:
			case 1393:
			case 3054:
			case 11736:
			case 11738:
			case 36019:
				return true;
			default:
				return false;
			}
		} else if (runeId == 16091 || runeId == 17780) {
			switch (weaponId) {
			case 16169:
			case 16170:
			case 17009:
			case 17011:
				return true;
			}
		} else if (runeId == 16093 || runeId == 17782) {
			switch (weaponId) {
			case 16165:
			case 16166:
			case 17001:
			case 17003:
				return true;
			}
		} else if (runeId == 16092 || runeId == 17781) {
			switch (weaponId) {
			case 16163:
			case 16164:
			case 16997:
			case 16999:
				return true;
			}
		} else if (runeId == 16094 || runeId == 17783) {
			switch (weaponId) {
			case 16167:
			case 16168:
			case 17005:
			case 17007:
				return true;
			}
		}
		switch (runeId) {
		case 16091:
		case 17780:
		case 16093:
		case 17782:
		case 16092:
		case 17781:
		case 16094:
		case 17783:
			switch (weaponId) {
			case 15835:
			case 16153:
			case 16154:
			case 16155:
			case 16156:
			case 16157:
			case 16158:
			case 16159:
			case 16160:
			case 16161:
			case 16162:
			case 16171:
			case 16172:
			case 16173:
			case 16967:
			case 16969:
			case 16977:
			case 16979:
			case 16981:
			case 16983:
			case 16985:
			case 16987:
			case 16989:
			case 16991:
			case 16993:
			case 16995:
			case 17013:
			case 17015:
			case 17017:
			case 17293:
			case 27687:
			case 27689:
			case 27691:
			case 27693:
			case 27695:
			case 27697:
			case 27699:
			case 27701:
			case 27703:
			case 27705:
			case 27707:
			case 27709:
			case 27711:
			case 27768:
			case 27769:
			case 27770:
			case 27771:
			case 27772:
			case 27773:
			case 27774:
			case 27775:
			case 27776:
			case 27777:
			case 27778:
			case 27779:
			case 27780:
				return true;
			default:
				return false;
			}
		default:
			return false;
		}
	}

	public static boolean hasStaffOfLight(int weaponId) {
		if (weaponId == 15486 || weaponId == 22207 || weaponId == 22209 || weaponId == 22211 || weaponId == 22213)
			return true;
		return false;
	}

	public static final void processAncientSpell(Player player, int spellId, int packetId) {
		player.stopAll(false);
		switch (spellId) {
		case 28:
		case 32:
		case 24:
		case 20:
		case 30:
		case 34:
		case 26:
		case 22:
		case 29:
		case 33:
		case 25:
		case 21:
		case 31:
		case 35:
		case 27:
		case 23:
		case 36:
		case 37:
		case 38:
		case 39:
			setCombatSpell(player, spellId);
			break;
		case 40:
			sendAncientTeleportSpell(player, 54, 64, new WorldTile(3099, 9882, 0), LAW_RUNE, 2, FIRE_RUNE, 1, AIR_RUNE,
					1);
			break;
		case 41: // Senntisten teleport
			sendAncientTeleportSpell(player, 60, 70, new WorldTile(3319, 3336, 0), LAW_RUNE, 2, SOUL_RUNE, 1);
			break;
		case 42:
			sendAncientTeleportSpell(player, 66, 76, new WorldTile(3492, 3471, 0), LAW_RUNE, 2, BLOOD_RUNE, 1);

			break;
		case 43:
			sendAncientTeleportSpell(player, 72, 82, new WorldTile(3006, 3471, 0), LAW_RUNE, 2, WATER_RUNE, 4);
			break;
		case 44:
			sendAncientTeleportSpell(player, 78, 88, new WorldTile(2990, 3696, 0), LAW_RUNE, 2, FIRE_RUNE, 3, AIR_RUNE,
					2);
			break;
		case 45:
			sendAncientTeleportSpell(player, 84, 94, new WorldTile(3217, 3677, 0), LAW_RUNE, 2, SOUL_RUNE, 2);
			break;
		case 46:
			sendAncientTeleportSpell(player, 90, 100, new WorldTile(3288, 3886, 0), LAW_RUNE, 2, BLOOD_RUNE, 2);
			break;
		case 47:
			sendAncientTeleportSpell(player, 96, 106, new WorldTile(2977, 3873, 0), LAW_RUNE, 2, WATER_RUNE, 8);
			break;
		case 48:
			player.getInterfaceManager().sendInterface(1092);
			// useHomeTele(player);
			break;
		}
	}

	public static final void processLunarSpell(Player player, int spellId, int packetId) {
		player.stopAll(false);
		switch (spellId) {
		case 37:
			if (player.getSkills().getLevel(Skills.MAGIC) < 94) {
				player.getPackets().sendGameMessage("Your Magic level is not high enough for this spell.");
				return;
			} else if (player.getSkills().getLevel(Skills.DEFENCE) < 40) {
				player.getPackets().sendGameMessage("You need a Defence level of 40 for this spell");
				return;
			}
			Long lastVeng = (Long) player.getTemporaryAttributtes().getOrDefault("LAST_VENG",0L);
			if (lastVeng != -1 && lastVeng + 30000 > Utils.currentTimeMillis()) {
				player.getPackets().sendGameMessage("Players may only cast vengeance once every 30 seconds.");
				return;
			}
			if (!checkRunes(player, true, ASTRAL_RUNE, 4, DEATH_RUNE, 2, EARTH_RUNE, 10))
				return;
			player.setNextGraphics(new Graphics(726, 0, 100));
			player.setNextAnimation(new Animation(4410));
			player.setCastVeng(true);
			player.getTemporaryAttributtes().put("LAST_VENG", Utils.currentTimeMillis());
			
			player.getPotiontimers().slotTimerArray[PotionTimersInter.VENG] =  Utils.currentTimeMillis() + 30000;
			player.getPackets().sendGameMessage("You cast a vengeance.");
			break;
		case 74:
			Long lastVengGroup = (Long) player.getTemporaryAttributtes().get("LAST_VENGGROUP");
			if (lastVengGroup != null && lastVengGroup + 30000 > Utils.currentTimeMillis()) {
				player.getPackets().sendGameMessage("Players may only cast vengeance group once every 30 seconds.");
				return;
			}
			if (!player.isAtMultiArea()) {
				player.getPackets().sendGameMessage("You can only cast vengeance group in a multi area.");
				return;
			}
			if (player.getSkills().getLevel(Skills.MAGIC) < 95) {
				player.getPackets().sendGameMessage("You need a level of 95 magic to cast vengeance group.");
				return;
			}
			if (!player.getInventory().containsItem(560, 3) && !player.getInventory().containsItem(557, 11)
					&& !player.getInventory().containsItem(9075, 4)) {
				player.getPackets().sendGameMessage("You don't have enough runes to cast vengeance group.");
				return;
			}
			int count = 0;
			for (Player other : World.getPlayers()) {
				if (other.withinDistance(player, 4) && other.isAtMultiArea()) {
					other.getPackets().sendGameMessage("Someone cast the Group Vengeance spell and you were affected!");
					other.setCastVeng(true);
					other.setNextGraphics(new Graphics(725, 0, 100));
					other.getTemporaryAttributtes().put("LAST_VENGGROU P", Utils.currentTimeMillis());
					other.getTemporaryAttributtes().put("LAST_VENG", Utils.currentTimeMillis());
					count++;
				}
			}
			player.getPackets().sendGameMessage("The spell affected " + count + " nearby people.");
			player.setNextGraphics(new Graphics(725, 0, 100));
			player.setNextAnimation(new Animation(4410));
			player.setCastVeng(true);
			player.getTemporaryAttributtes().put("LAST_VENGGROUP", Utils.currentTimeMillis());
			player.getTemporaryAttributtes().put("LAST_VENG", Utils.currentTimeMillis());
			player.getInventory().deleteItem(560, 3);
			player.getInventory().deleteItem(557, 11);
			player.getInventory().deleteItem(9075, 4);
			break;
		case 39:
			player.getInterfaceManager().sendInterface(1092);
			// useHomeTele(player);
			break;
		case 42:
			if (player.getSkills().getLevel(Skills.MAGIC) < 93) {
				player.getPackets().sendGameMessage("Your Magic level is not high enough for this spell.");
				return;
			} else if (player.getSkills().getLevel(Skills.DEFENCE) < 40) {
				player.getPackets().sendGameMessage("You need a Defence level of 40 for this spell");
				return;
			}
			if (!checkRunes(player, true, EARTH_RUNE, 11, DEATH_RUNE, 3, ASTRAL_RUNE, 4))
				return;
			break;
		case 32: // dream spell
			if (player.isUnderCombat(6)) {
				player.getPackets().sendGameMessage("You can't cast dream until 10 seconds after the end of combat.");
				return;
			} else if (player.getHitpoints() == player.getMaxHitpoints()) {
				player.getPackets().sendGameMessage(
						"You have no need to cast this spell since your life points are already full.");
				return;
			} else if (!checkRunes(player, true, ASTRAL_RUNE, 2, COSMIC_RUNE, 1, BODY_RUNE, 5))
				return;
			player.getActionManager().setAction(new DreamSpellAction());
			break;
		// Barbarian TP
		case 22:
			sendLunarTeleportSpell(player, 75, 76, new WorldTile(2544, 3569, 0), ASTRAL_RUNE, 2, LAW_RUNE, 2, FIRE_RUNE,
					3);
			break;
		// Fishing Guild TP
		case 40:
			sendLunarTeleportSpell(player, 85, 89, new WorldTile(2614, 3381, 0), ASTRAL_RUNE, 3, LAW_RUNE, 3,
					WATER_RUNE, 8);
			break;
		// Khazard TP
		case 41:
			sendLunarTeleportSpell(player, 78, 80, new WorldTile(2634, 3168, 0), ASTRAL_RUNE, 2, LAW_RUNE, 2,
					WATER_RUNE, 4);
			// Moonclan TP(Lunar Isle)
		case 43:
			sendLunarTeleportSpell(player, 69, 66, new WorldTile(2111, 3915, 0), ASTRAL_RUNE, 2, LAW_RUNE, 1,
					EARTH_RUNE, 2);
			break;
		// Catherby TP
		case 44:
			sendLunarTeleportSpell(player, 87, 92, new WorldTile(2764, 3454, 0), ASTRAL_RUNE, 3, LAW_RUNE, 3,
					WATER_RUNE, 10);
			break;
		// Waterbirth TP
		case 47:
			sendLunarTeleportSpell(player, 72, 71, new WorldTile(2550, 3755, 0), ASTRAL_RUNE, 2, LAW_RUNE, 1,
					WATER_RUNE, 1);
			break;
		case 51:
			// Ice Plateau TP
			sendLunarTeleportSpell(player, 89, 96, new WorldTile(2951, 3936, 0), ASTRAL_RUNE, 3, LAW_RUNE, 3,
					WATER_RUNE, 8);
			break;
		// Ourania TP
		case 54:
			sendLunarTeleportSpell(player, 71, 74, new WorldTile(3273, 4857, 0), ASTRAL_RUNE, 2, LAW_RUNE, 1,
					EARTH_RUNE, 6);
			break;
		// South Falador TP
		case 67:
			sendLunarTeleportSpell(player, 72, 75, new WorldTile(3005, 3327, 0), ASTRAL_RUNE, 2, LAW_RUNE, 1, AIR_RUNE,
					2);
			break;
		// North Ardougne TP
		case 69:
			sendLunarTeleportSpell(player, 76, 77, new WorldTile(2613, 3347, 0), ASTRAL_RUNE, 2, LAW_RUNE, 1,
					WATER_RUNE, 5);
			break;
		// Trollheim TP
		case 75:
			sendLunarTeleportSpell(player, 92, 101, new WorldTile(2890, 3672, 0), ASTRAL_RUNE, 3, LAW_RUNE, 3,
					WATER_RUNE, 10);
			break;
		case 38: // bake pie
			if (player.isLocked())
				return;
			for (Cookables food : Cookables.values()) {
				if (food.toString().toLowerCase().contains("_pie")) {
					if (player.getSkills().getLevel(Skills.COOKING) < food.getLvl())
						continue;
					Item item = food.getRawItem();
					if (player.getInventory().containsItem(item.getId(), 1)) {
						for (int i = 0; i < player.getInventory().getAmountOf(item.getId());) {
							if (!checkRunes(player, true, ASTRAL_RUNE, 1, FIRE_RUNE, 5, WATER_RUNE, 4))
								return;
							player.lock(2);
							player.getInventory().replaceItem(food.getProduct().getId(), item.getAmount(),
									player.getInventory().getItems().getThisItemSlot(item.getId()));
							player.getInterfaceManager().openGameTab(7);
							player.getSkills().addXp(Skills.MAGIC, 60);
							player.getSkills().addXp(Skills.COOKING, food.getXp());
							player.setNextAnimation(new Animation(4413));
							player.setNextGraphics(new Graphics(746));
							return;
						}
					}
				}
			}
			player.getPackets().sendGameMessage("You do not have any pie in your inventory.");
			break;
		case 29: // humidify
			if (player.getLunarDelay() > Utils.currentTimeMillis())
				return;
			if (!Magic.checkSpellRequirements(player, 68, false, ASTRAL_RUNE, 1, FIRE_RUNE, 1, WATER_RUNE, 3))
				return;
			final int fillables[][] = { { 229, 227 }, // vials
					{ 1923, 1921 }, // bowls
					{ 1925, 1929 }, // buckets
					{ 1935, 1937 }, // jugs
					{ 3734, 3735 }, // vase
					{ 5350, 5354 }, // plant pots
					{ 434, 1761 }, // clay
					// beginning of watering cans
					{ 5331, 5340 }, // empty -> full
					{ 5339, 5340 }, // 7 -> 8
					{ 5338, 5340 }, // 6 -> 8
					{ 5337, 5340 }, // 5 -> 8
					{ 5336, 5340 }, // 4 -> 8
					{ 5335, 5340 }, // 3 -> 8
					{ 5334, 5340 }, // 2 -> 8
					{ 5333, 5340 }, // 1 -> 8
					// end of watering cans
					{ 6667, 6668 }, // fish bowl
					// beginning of waterskins
					{ 1831, 1823 }, // empty -> full
					{ 1829, 1823 }, // 1 -> 4
					{ 1827, 1823 }, // 2 -> 4
					{ 1825, 1823 },// 3 -> 4
					// end of waterskins
			};
			int tryCount = 0;
			for (int i = 0; i < fillables.length; i++) {
				if (!player.getInventory().containsItem(fillables[i][0], 1)) {
					tryCount++;
					continue;
				}
			}
			if (tryCount >= fillables.length) {
				player.getPackets().sendGameMessage("You have no items capable of being filled in your inventory.");
				return;
			}
			player.setLunarDelay(4500);
			WorldTasksManager.schedule(new WorldTask() {
				int loop;

				@Override
				public void run() {
					if (loop == 0) {
						player.setNextAnimation(new Animation(6294));
						player.setNextGraphics(new Graphics(1061));
					} else if (loop == 3) {
						for (int i = 0; i < fillables.length; i++) {
							if (!player.getInventory().containsItem(fillables[i][0], 1)) {
								continue;
							}
							while (player.getInventory().containsItem(fillables[i][0], 1)) {
								int amount = player.getInventory().getNumberOf(fillables[i][0]);
								player.getInventory().deleteItem(fillables[i][0],
										player.getInventory().getNumberOf(fillables[i][0]));
								player.getInventory().addItem(fillables[i][1], amount);
								if (!player.getInventory().containsItem(fillables[i][0], 1))
									break;
							}
						}
						Magic.checkRunes(player, true, ASTRAL_RUNE, 1, FIRE_RUNE, 1, WATER_RUNE, 3);
						player.getSkills().addXp(Skills.MAGIC, 65);
						player.getPackets().sendGameMessage("Moisture fills your inventory.");
						stop();
					}
					loop++;
				}
			}, 0, 1);
			break;

		// group teleport
		case 56: // moonclan
		case 57: // waterbirth
		case 58: // barbarian
		case 59: // khazard
		case 60: // fishing guild
		case 61: // catherby
		case 62: // ice plateau
		case 76: // trollheim
			for (int regionId : player.getMapRegionsIds()) {
				List<Integer> playersIndexes = World.getRegion(regionId).getPlayerIndexes();
				if (playersIndexes == null)
					continue;
				for (Integer playerIndex : playersIndexes) {
					Player p2 = World.getPlayers().get(playerIndex);
					if (p2 == null || p2.isDead() || p2.hasFinished() || !p2.isRunning()
							|| !p2.isAcceptingAid() && player.getIndex() != p2.getIndex()
							|| !p2.withinDistance(player, 5))
						continue;
					// ManiFoldTeleport.openInterface(p2, name, index, true);
				}
			}
			break;
		case 46: // cure me
			if (!checkRunes(player, true, ASTRAL_RUNE, 2, COSMIC_RUNE, 2))
				return;
			if (player.isLocked())
				return;
			if (!player.getPoison().isPoisoned()) {
				player.sendMessage("You cannot use this spell right now.");
				return;
			}
			player.lock(2);
			player.getSkills().addXp(Skills.MAGIC, 69);
			player.setNextAnimation(new Animation(4411));
			player.setNextGraphics(new Graphics(736, 0, 150));
			player.getPoison().reset();
			break;
		case 25:
			if (!Magic.checkSpellLevel(player, 74))
				return;
			else if (!checkRunes(player, true, ASTRAL_RUNE, 2, COSMIC_RUNE, 2))
				return;
			if (player.isLocked())
				return;
			player.lock(2);
			int affectedPeopleCount = 0;
			for (int regionId : player.getMapRegionsIds()) {
				List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
				if (playerIndexes == null)
					continue;
				for (int playerIndex : playerIndexes) {
					Player p2 = World.getPlayers().get(playerIndex);
					if (p2 == null || p2 == player || p2.isDead() || p2.hasFinished() || !p2.withinDistance(player, 4))
						continue;
					if (!p2.isAcceptingAid())
						continue;
					player.setNextGraphics(new Graphics(736, 0, 150));
					if (p2.getPoison().isPoisoned()) {
						p2.getPackets().sendGameMessage("You have been cured of all illnesses!");
						p2.getPoison().reset();
						affectedPeopleCount++;
					}
				}
			}
			player.setNextAnimation(new Animation(4411));
			if (affectedPeopleCount > 0) {
				player.getPackets().sendGameMessage("The spell affected " + affectedPeopleCount + " nearby people.");
			}
			if (player.getPoison().isPoisoned() || affectedPeopleCount > 0) {
				player.getSkills().addXp(Skills.MAGIC, 69);
				return;
			}
			player.sendMessage("No experience gained because no nearby people were ill.");
			break;
		}
	}

	public static boolean checkSpellLevel(Player player, int level) {
		if (player.getSkills().getLevel(Skills.MAGIC) < level) {
			player.getPackets().sendGameMessage("Your Magic level is not high enough for this spell.");
			return false;
		}
		return true;
	}

	public static final void processNormalSpell(Player player, int spellId, int packetId) {
		player.stopAll(false);
		switch (spellId) {
		case 25: // air strike
		case 28: // water strike
		case 30: // earth strike
		case 32: // fire strike
		case 34: // air bolt
		case 39: // water bolt
		case 42: // earth bolt
		case 45: // fire bolt
		case 49: // air blast
		case 52: // water blast
		case 58: // earth blast
		case 63: // fire blast
		case 70: // air wave
		case 73: // water wave
		case 77: // earth wave
		case 80: // fire wave
		case 99:
		case 84:
		case 87:
		case 89:
		case 91:
		case 36:
		case 55:
		case 81:
		case 66:
		case 67:
		case 68:
			setCombatSpell(player, spellId);
			break;
		case 27: // crossbow bolt enchant
			if (player.getSkills().getLevel(Skills.MAGIC) < 4) {
				player.getPackets().sendGameMessage("Your Magic level is not high enough for this spell.");
				return;
			}
			player.stopAll();
			player.getInterfaceManager().sendInterface(432);
			break;
		case 24:
			// useHomeTele(player);
			player.getInterfaceManager().sendInterface(1092);
			break;
		case 37: // mobi
			sendNormalTeleportSpell(player, 10, 19, new WorldTile(2413, 2848, 0), LAW_RUNE, 1, WATER_RUNE, 1, AIR_RUNE,
					1);
			break;
		case 40: // varrock
			sendNormalTeleportSpell(player, 25, 19, new WorldTile(3212, 3424, 0), FIRE_RUNE, 1, AIR_RUNE, 3, LAW_RUNE,
					1);
			break;
		case 43: // lumby
			sendNormalTeleportSpell(player, 31, 41, new WorldTile(3222, 3218, 0), EARTH_RUNE, 1, AIR_RUNE, 3, LAW_RUNE,
					1);
			break;
		case 46: // fally
			sendNormalTeleportSpell(player, 37, 48, new WorldTile(2964, 3379, 0), WATER_RUNE, 1, AIR_RUNE, 3, LAW_RUNE,
					1);
			break;
		case 51: // camelot
			sendNormalTeleportSpell(player, 45, 55.5, new WorldTile(2757, 3478, 0), AIR_RUNE, 5, LAW_RUNE, 1);
			break;
		case 57: // ardy
			sendNormalTeleportSpell(player, 51, 61, new WorldTile(2664, 3305, 0), WATER_RUNE, 2, LAW_RUNE, 2);
			break;
		case 62: // watch
			sendNormalTeleportSpell(player, 58, 68, new WorldTile(2547, 3113, 2), EARTH_RUNE, 2, LAW_RUNE, 2);
			break;
		case 69: // troll
			sendNormalTeleportSpell(player, 61, 68, new WorldTile(2888, 3674, 0), FIRE_RUNE, 2, LAW_RUNE, 2);
			break;
		case 72: // ape
			sendNormalTeleportSpell(player, 64, 76, new WorldTile(2788, 9105, 0), FIRE_RUNE, 2, WATER_RUNE, 2, LAW_RUNE,
					2, 1963, 1);
			break;
		}
	}

	public static void pushLeverTeleport(final Player player, final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		player.setNextAnimation(new Animation(2140));
		player.lock(2);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.unlock();
				Magic.sendObjectTeleportSpell(player, false, tile);
			}
		}, 1);
	}

	public static final void sendAncientTeleportSpell(Player player, int level, double xp, WorldTile tile,
			int... runes) {
		sendTeleportSpell(player, 1979, -1, 1681, -1, level, xp, tile, 4, true, MAGIC_TELEPORT, runes);

	}

	public static final void sendDelayedObjectTeleportSpell(Player player, int delay, boolean randomize,
			WorldTile tile) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, delay, randomize, OBJECT_TELEPORT);
	}

	public static final boolean sendItemTeleportSpell(Player player, boolean randomize, int upEmoteId, int upGraphicId,
			int delay, WorldTile tile) {
		return sendTeleportSpell(player, upEmoteId, -2, upGraphicId, -1, 0, 0, tile, delay, randomize, ITEM_TELEPORT);
	}

	public static final void sendNormalTeleportSpell(Player player, int level, double xp, WorldTile tile,
			int... runes) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, level, xp, tile, 3, true, MAGIC_TELEPORT, runes);
		if (player.isAtMultiArea()
				|| player.getCurrentInstance() != null && player.getControlerManager().getControler() != null) {
			player.setForceMultiArea(false);
			if (player.getCurrentInstance() != null) {
				player.getCurrentInstance().removePlayer(player);

			}

		}
	}

	public static final void sendObjectTeleportSpell(Player player, boolean randomize, WorldTile tile) {
		sendTeleportSpell(player, 8939, 8941, 1576, 1577, 0, 0, tile, 3, randomize, OBJECT_TELEPORT);
	}

	public static final void sendCrushTeleportSpell(Player player, int level, double xp, WorldTile tile, int... runes) {
		sendTeleportSpell(player, 17542, 8941, 3402, 1577, level, xp, tile, 10, true, MAGIC_TELEPORT, runes);
	}

	public static final void sendPegasusTeleportSpell(Player player, int level, double xp, WorldTile tile,
			int... runes) {
		sendTeleportSpell(player, 17106, 8941, 3223, 1577, level, xp, tile, 19, true, MAGIC_TELEPORT, runes);
	}

	public static final void sendDemonTeleportSpell(Player player, int level, double xp, WorldTile tile, int... runes) {
		sendTeleportSpell(player, 17108, 8941, 3225, 1577, level, xp, tile, 19, true, MAGIC_TELEPORT, runes);
	}

	public static final void sendZarosTeleportSpell(Player player, int level, double xp, WorldTile tile, int... runes) {
		sendTeleportSpell(player, 24487, 8941, 5107, 5108, level, xp, tile, 18, true, MAGIC_TELEPORT, runes);
	}

	public static final boolean sendTeleportSpell(final Player player, int upEmoteId, final int downEmoteId,
			int upGraphicId, final int downGraphicId, int level, final double xp, final WorldTile tile, int delay,
			final boolean randomize, final int teleType, boolean dungeoneering, int... runes) {
		long currentTime = Utils.currentTimeMillis();

		if (player.getLockDelay() > currentTime)
			return false;
		if (player.getX() >= 2956 && player.getX() <= 3067 && player.getY() >= 5512 && player.getY() <= 5630
				|| (player.getX() >= 2756 && player.getX() <= 2875 && player.getY() >= 5512 && player.getY() <= 5627)) {
			player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
			return false;
		}
		if (player.getSkills().getLevel(Skills.MAGIC) < level) {
			player.getPackets().sendGameMessage("Your Magic level is not high enough for this spell.");
			return false;
		}
		if (!checkRunes(player, false, runes))
			return false;
		if (!dungeoneering) {
			if (teleType == MAGIC_TELEPORT) {
				if (!player.getControlerManager().processMagicTeleport(tile))
					return false;
			} else if (teleType == ITEM_TELEPORT) {
				if (!player.getControlerManager().processItemTeleport(tile))
					return false;
			} else if (teleType == OBJECT_TELEPORT) {
				if (!player.getControlerManager().processObjectTeleport(tile))
					return false;
			}
		}
		checkRunes(player, true, runes);
		player.stopAll();
		if (upEmoteId != -1)
			player.setNextAnimation(new Animation(upEmoteId));
		if (upGraphicId != -1)
			player.setNextGraphics(new Graphics(upGraphicId));
		if (teleType == MAGIC_TELEPORT)
			player.getPackets().sendSound(5527, 0, 2);
		player.lock(3 + delay);
		WorldTasksManager.schedule(new WorldTask() {

			boolean removeDamage;

			@Override
			public void run() {
				if (!removeDamage) {
					WorldTile teleTile = tile;
					if (randomize) {
						// attemps to randomize tile by 4x4 area
						for (int trycount = 0; trycount < 10; trycount++) {
							teleTile = new WorldTile(tile, 2);
							if (World.canMoveNPC(tile.getPlane(), teleTile.getX(), teleTile.getY(), player.getSize()))
								break;
							teleTile = tile;
						}
					}
					player.setNextWorldTile(teleTile);
					player.getControlerManager().magicTeleported(teleType);
					if (player.getControlerManager().getControler() == null)
						teleControlersCheck(player, teleTile);
					if (xp != 0)
						player.getSkills().addXp(Skills.MAGIC, xp);
					if (downEmoteId != -1)
						player.setNextAnimation(new Animation(downEmoteId == -2 ? -1 : downEmoteId));
					if (downGraphicId != -1)
						player.setNextGraphics(new Graphics(downGraphicId));
					if (teleType == MAGIC_TELEPORT) {
						player.getPackets().sendSound(5524, 0, 2);
						player.setNextFaceWorldTile(
								new WorldTile(teleTile.getX(), teleTile.getY() - 1, teleTile.getPlane()));
						player.setDirection(6);
					}
					removeDamage = true;
				} else {
					player.resetReceivedDamage();
					stop();
				}
			}
		}, delay, 0);
		return true;
	}

	public static final boolean sendTeleportSpell(final Player player, int upEmoteId, final int downEmoteId,
			int upGraphicId, final int downGraphicId, int level, final double xp, final WorldTile tile, int delay,
			final boolean randomize, final int teleType, int... runes) {
		long currentTime = Utils.currentTimeMillis();
		if (player.getLockDelay() > currentTime)
			return false;
		if (player.isCanPvp() && player.getAttackedBy() != null
				&& player.getTemporaryAttributtes().remove("obelisktele") == null
				|| player.getX() >= 2956 && player.getX() <= 3067 && player.getY() >= 5512 && player.getY() <= 5630
				|| (player.getX() >= 2756 && player.getX() <= 2875 && player.getY() >= 5512 && player.getY() <= 5627)) {
			player.getPackets().sendGameMessage("A magical force is blocking you from teleporting.");
			return false;
		}
		if (player.getSkills().getLevel(Skills.MAGIC) < level) {
			player.getPackets().sendGameMessage("Your Magic level is not high enough for this spell.");
			return false;
		}
		if (!checkRunes(player, false, runes))
			return false;
		if (teleType == MAGIC_TELEPORT) {
			if (!player.getControlerManager().processMagicTeleport(tile))
				return false;
		} else if (teleType == ITEM_TELEPORT) {
			if (!player.getControlerManager().processItemTeleport(tile))
				return false;
		} else if (teleType == OBJECT_TELEPORT) {
			if (!player.getControlerManager().processObjectTeleport(tile))
				return false;
		}
		if (player.getCurrentInstance() != null)
			player.getCurrentInstance().removePlayer(player);
		checkRunes(player, true, runes);
		player.stopAll();
		if (upEmoteId != -1)
			player.setNextAnimation(new Animation(upEmoteId));
		if (upGraphicId != -1)
			player.setNextGraphics(new Graphics(upGraphicId));
		if (teleType == MAGIC_TELEPORT)
			player.getPackets().sendSound(5527, 0, 2);
		player.lock(3 + delay);
		WorldTasksManager.schedule(new WorldTask() {

			boolean removeDamage;

			@Override
			public void run() {
				if (!removeDamage) {
					WorldTile teleTile = tile;
					if (randomize) {
						// attemps to randomize tile by 4x4 area
						for (int trycount = 0; trycount < 10; trycount++) {
							if (tile == null)
								break;
							teleTile = new WorldTile(tile, 2);
							if (tile == null || teleTile == null)
								break;
							if (World.canMoveNPC(tile.getPlane(), teleTile.getX(), teleTile.getY(), player.getSize()))
								break;
							teleTile = tile;
						}
					}
					player.setNextWorldTile(teleTile);
					player.getControlerManager().magicTeleported(teleType);
					if (player.getControlerManager().getControler() == null)
						teleControlersCheck(player, teleTile);
					if (xp != 0)
						player.getSkills().addXp(Skills.MAGIC, xp);
					if (downEmoteId != -1)
						player.setNextAnimation(new Animation(downEmoteId == -2 ? -1 : downEmoteId));
					if (downGraphicId != -1)
						player.setNextGraphics(new Graphics(downGraphicId));
					if (teleType == MAGIC_TELEPORT) {
						player.getPackets().sendSound(5524, 0, 2);
						player.setNextFaceWorldTile(
								new WorldTile(teleTile.getX(), teleTile.getY() - 1, teleTile.getPlane()));
						player.setDirection(6);
					}
					removeDamage = true;
				} else {
					player.resetReceivedDamage();
					stop();
				}
			}
		}, delay, 0);
		return true;
	}

	public static final void setCombatSpell(Player player, int spellId) {
		if (player.getCombatDefinitions().getAutoCastSpell() == spellId)
			player.getCombatDefinitions().resetSpells(true);
		else
			checkCombatSpell(player, spellId, 0, false);
	}

	public static void teleControlersCheck(Player player, WorldTile teleTile) {
		if (player.getRegionId() == 11601)
			player.getControlerManager().startControler("GodWars");
		else if (player.getRegionId() == 13626 || player.getRegionId() == 13625)
			player.getControlerManager().startControler("DungeoneeringLobby");
		else if (Wilderness.isAtWild(teleTile) || Wilderness.isAtDitch(teleTile))
			player.getControlerManager().startControler("Wilderness");
		else if (RequestController.inWarRequest(player))
			player.getControlerManager().startControler("clan_wars_request");
		else if (FfaZone.inArea(player))
			player.getControlerManager().startControler("clan_wars_ffa");
	}

	private static void useHomeTele(Player player) {
		player.stopAll();
		vineTeleport(player, player.getHomeTile());
	}

	public static boolean useTabTeleport(final Player player, final int itemId) {
		if (itemId == 8013) {
			if (useTeleTab(player, player.getHomeTile()))
				player.getInventory().deleteItem(itemId, 1);
			return true;
		}
		if (itemId < 8007 || itemId > 8007 + TABS.length - 1)
			return false;
		if (useTeleTab(player, TABS[itemId - 8007]))
			player.getInventory().deleteItem(itemId, 1);
		return true;
	}

	public static boolean useTeleTab(final Player player, final WorldTile tile) {
		if (!player.getControlerManager().processItemTeleport(tile))
			return false;
		if (player.isLocked())
			return false;
		player.lock();
		player.setNextAnimation(new Animation(9597));
		player.setNextGraphics(new Graphics(1680));
		WorldTasksManager.schedule(new WorldTask() {
			int stage;

			@Override
			public void run() {
				if (stage == 0) {
					player.setNextAnimation(new Animation(4731));
					stage = 1;
				} else if (stage == 1) {
					WorldTile teleTile = tile;
					// attemps to randomize tile by 4x4 area
					for (int trycount = 0; trycount < 10; trycount++) {
						teleTile = new WorldTile(tile, 2);
						if (World.canMoveNPC(tile.getPlane(), teleTile.getX(), teleTile.getY(), player.getSize()))
							break;
						teleTile = tile;
					}
					player.setNextWorldTile(teleTile);
					player.getControlerManager().magicTeleported(ITEM_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						teleControlersCheck(player, teleTile);
					player.setNextFaceWorldTile(
							new WorldTile(teleTile.getX(), teleTile.getY() - 1, teleTile.getPlane()));
					player.setDirection(6);
					player.setNextAnimation(new Animation(-1));
					stage = 2;
				} else if (stage == 2) {
					player.resetReceivedDamage();
					player.unlock();
					stop();
				}
			}
		}, 2, 1);
		return true;
	}

	public static void vineTeleport(final Player player, final WorldTile tile) {
		if (!player.getControlerManager().processMagicTeleport(tile))
			return;
		if (player.isLocked() || player.getTeleBlockDelay() > Utils.currentTimeMillis())
			return;
		player.lock();
		player.stopAll();
		player.setNextGraphics(new Graphics(1229));
		player.setNextAnimation(new Animation(7082));
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				WorldTile teleTile = tile;
				for (int trycount = 0; trycount < 10; trycount++) {
					teleTile = new WorldTile(tile, 2);
					if (World.canMoveNPC(tile.getPlane(), teleTile.getX(), teleTile.getY(), player.getSize()))
						break;
					teleTile = tile;
				}
				player.setNextAnimation(new Animation(7084));
				player.setNextGraphics(new Graphics(1228));
				player.setNextWorldTile(teleTile);
				player.getControlerManager().magicTeleported(MAGIC_TELEPORT);
				player.checkMovement(teleTile.getX(), teleTile.getY(), teleTile.getPlane());
				if (player.getControlerManager().getControler() == null)
					teleControlersCheck(player, teleTile);
				player.unlock();
				stop();
			}
		}, 4);
	}

	public static final void sendEctophialTeleport(Player player, int level, double xp, WorldTile tile, int... runes) {
		sendTeleportSpell(player, 8939, 8941, 1678, 1679, level, xp, tile, 3, true, MAGIC_TELEPORT, runes);
	}

	public static void daemonheimTeleport(final Player player, final WorldTile tile) {
		if (!player.getControlerManager().processItemTeleport(tile))
			return;
		if (player.isLocked())
			return;
		player.lock();
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			@Override
			public void run() {
				if (loop == 0) {
					player.setNextAnimation(new Animation(13652));
					player.setNextGraphics(new Graphics(2602));
				}
				if (loop == 5)
					player.setNextWorldTile(tile);
				if (loop == 6) {
					player.setNextAnimation(new Animation(13654));
					player.setNextGraphics(new Graphics(2603));
					player.getControlerManager().magicTeleported(ITEM_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						teleControlersCheck(player, tile);
					player.setNextFaceWorldTile(new WorldTile(tile.getX(), tile.getY() - 1, tile.getPlane()));
					player.setDirection(6);
					player.resetReceivedDamage();
					player.unlock();
					stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	/**
	 * Handles the scroll teleporting.
	 * 
	 * @param player
	 *            The player teleporting.
	 * @param itemId
	 *            The scroll ID.
	 * @return if can Teleport.
	 */
	public static boolean useScrollTeleport(final Player player, final int itemId) {
		if (itemId < 19475 || itemId > 19475 + SCROLLS.length - 1)
			return false;
		if (useTeleScroll(player, SCROLLS[itemId - 19475]))
			player.getInventory().deleteItem(itemId, 1);
		return true;
	}

	/**
	 * Uses the teleport scroll.
	 * 
	 * @param player
	 *            The player using the scroll.
	 * @param tile
	 *            The worldtile to teleport to.
	 * @return if Successful.
	 */
	public static boolean useTeleScroll(final Player player, final WorldTile tile) {
		if (!player.getControlerManager().processItemTeleport(tile))
			return false;
		if (player.isLocked())
			return false;
		player.lock();
		player.setNextAnimation(new Animation(14293));
		player.setNextGraphics(new Graphics(94));
		WorldTasksManager.schedule(new WorldTask() {
			int stage;

			@Override
			public void run() {
				stage++;
				if (stage == 3) {
					WorldTile teleTile = tile;
					for (int trycount = 0; trycount < 10; trycount++) {
						teleTile = new WorldTile(tile, 2);
						if (World.canMoveNPC(tile.getPlane(), teleTile.getX(), teleTile.getY(), player.getSize()))
							break;
						teleTile = tile;
					}
					player.setNextWorldTile(teleTile);
					player.getControlerManager().magicTeleported(ITEM_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						teleControlersCheck(player, teleTile);
					player.setNextFaceWorldTile(
							new WorldTile(teleTile.getX(), teleTile.getY() - 1, teleTile.getPlane()));
					player.setDirection(6);
					player.setNextAnimation(new Animation(-1));
					player.resetReceivedDamage();
					player.unlock();
					stop();
				}
			}
		}, 1, 1);
		return true;
	}

	public static void compCapeTeleport(final Player player, final int x, final int y, final int h) {
		final WorldTile teleTile = new WorldTile(x, y, h);
		if (!player.getControlerManager().processItemTeleport(teleTile))
			return;
		if (player.isLocked())
			return;
		player.lock();
		player.setNextAnimation(new Animation(3254));
		player.setNextGraphics(new Graphics(2670));
		WorldTasksManager.schedule(new WorldTask() {
			int loop;

			public void run() {
				if (loop == 1) {
					player.setNextWorldTile(teleTile);
					player.setNextAnimation(new Animation(3255));
					player.getControlerManager().magicTeleported(ITEM_TELEPORT);
					if (player.getControlerManager().getControler() == null)
						teleControlersCheck(player, teleTile);
					player.setNextFaceWorldTile(
							new WorldTile(teleTile.getX(), teleTile.getY() - 1, teleTile.getPlane()));
					player.setDirection(6);
					player.resetReceivedDamage();
					player.unlock();
					player.setNextGraphics(new Graphics(2671));
					this.stop();
				}
				loop++;
			}
		}, 0, 1);
	}

	public static void fairyRingTeleport(final Player player, final WorldTile tile) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		if (player.isLocked())
			return;
		player.setNextAnimation(new Animation(3254));
		player.setNextGraphics(new Graphics(2670));
		player.lock();
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextWorldTile(tile);
				player.setNextAnimation(new Animation(3255));
				player.setNextGraphics(new Graphics(2670));
				player.getControlerManager().magicTeleported(OBJECT_TELEPORT);
				if (player.getControlerManager().getControler() == null)
					teleControlersCheck(player, tile);
				player.setNextFaceWorldTile(new WorldTile(tile.getX(), tile.getY() - 1, tile.getPlane()));
				player.setDirection(6);
				player.resetReceivedDamage();
				player.unlock();
			}
		}, 2);
	}

	public static void resourcesTeleport(final Player player, final WorldTile tile) {
		resourcesTeleport(player, tile, 1);
	}

	public static void resourcesTeleport(final Player player, final WorldTile tile, int level) {
		if (!player.getControlerManager().processObjectTeleport(tile))
			return;
		if (player.isLocked())
			return;
		if (player.getSkills().getLevelForXp(Skills.DUNGEONEERING) < level) {
			player.sendMessage("You need a Dungeoneering level of at least " + level + " to enter this dungeon.");
			return;
		}
		player.setNextAnimation(new Animation(13288));
		player.setNextGraphics(new Graphics(2516));
		player.lock();
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.setNextWorldTile(tile);
				player.setNextAnimation(new Animation(13285));
				player.setNextGraphics(new Graphics(2517));
				player.getControlerManager().magicTeleported(OBJECT_TELEPORT);
				if (player.getControlerManager().getControler() == null)
					teleControlersCheck(player, tile);
				player.setNextFaceWorldTile(new WorldTile(tile.getX(), tile.getY() - 1, tile.getPlane()));
				player.setDirection(6);
				player.resetReceivedDamage();
				player.unlock();
			}
		}, 1);
	}

	public static final void sendLunarTeleportSpell(Player player, int level, double xp, WorldTile tile, int... runes) {
		sendTeleportSpell(player, 9606, -2, 1685, -1, level, xp, tile, 5, true, MAGIC_TELEPORT, runes);
	}

	public static final void processDungSpell(Player player, int spellId, int slot, int packetId) {

		switch (spellId) {
		case 25:
		case 27:
		case 28:
		case 30:
		case 32: // air bolt
		case 36: // water bolt
		case 37: // earth bolt
		case 41: // fire bolt
		case 42: // air blast
		case 43: // water blast
		case 45: // earth blast
		case 47: // fire blast
		case 48: // air wave
		case 49: // water wave
		case 54: // earth wave
		case 58: // fire wave
		case 61:// air surge
		case 62:// water surge
		case 63:// earth surge
		case 67:// fire surge
		case 34:// bind
		case 44:// snare
		case 59:// entangle
		case 60:// stun
			setCombatSpell(player, spellId);
			break;
		case 65:
			if (player.getSkills().getLevel(Skills.MAGIC) < 94) {
				player.getPackets().sendGameMessage("Your Magic level is not high enough for this spell.");
				return;
			} else if (player.getSkills().getLevel(Skills.DEFENCE) < 40) {
				player.getPackets().sendGameMessage("You need a Defence level of 40 for this spell");
				return;
			}
			Long lastVeng = (Long) player.getTemporaryAttributtes().get("LAST_VENG");
			if (lastVeng != null && lastVeng + 30000 > Utils.currentTimeMillis()) {
				player.getPackets().sendGameMessage("Players may only cast vengeance once every 30 seconds.");
				return;
			}
			if (!checkRunes(player, true, true, 17790, 4, 17786, 2, 17782, 10))
				return;
			player.getSkills().addXp(Skills.MAGIC, 112);
			player.setNextGraphics(new Graphics(726, 0, 100));
			player.setNextAnimation(new Animation(4410));
			player.setCastVeng(true);
			player.getTemporaryAttributtes().put("LAST_VENG", Utils.currentTimeMillis());
			player.getPackets().sendGameMessage("You cast a vengeance.");
			break;
		case 66: // vegeance group
			if (player.getSkills().getLevel(Skills.MAGIC) < 95) {
				player.getPackets().sendGameMessage("Your Magic level is not high enough for this spell.");
				return;
			}
			lastVeng = (Long) player.getTemporaryAttributtes().get("LAST_VENG");
			if (lastVeng != null && lastVeng + 30000 > Utils.currentTimeMillis()) {
				player.getPackets().sendGameMessage("Players may only cast vengeance once every 30 seconds.");
				return;
			}
			if (!checkRunes(player, true, true, 17790, 4, 17786, 3, 17782, 11))
				return;
			int affectedPeopleCount = 0;
			for (int regionId : player.getMapRegionsIds()) {
				List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
				if (playerIndexes == null)
					continue;
				for (int playerIndex : playerIndexes) {
					Player p2 = World.getPlayers().get(playerIndex);
					if (p2 == null || p2 == player || p2.isDead() || !p2.isActive() || p2.hasFinished()
							|| !p2.withinDistance(player, 4) || !player.getControlerManager().canHit(p2))
						continue;
					if (!p2.isAcceptingAid()) {
						player.getPackets().sendGameMessage(p2.getDisplayName() + " is not accepting aid");
						continue;
					} else if (p2.getControlerManager().getControler() != null
							&& p2.getControlerManager().getControler() instanceof DuelArena) {
						continue;
					}
					p2.setNextGraphics(new Graphics(725, 0, 100));
					p2.setCastVeng(true);
					p2.getPackets().sendGameMessage("You have the power of vengeance!");
					affectedPeopleCount++;
				}
			}
			player.getSkills().addXp(Skills.MAGIC, 120);
			player.setNextAnimation(new Animation(4411));
			player.getTemporaryAttributtes().put("LAST_VENG", Utils.currentTimeMillis());
			player.getPackets().sendGameMessage("The spell affected " + affectedPeopleCount + " nearby people.");
			break;
		case 53:
			if (player.getSkills().getLevel(Skills.MAGIC) < 68) {
				player.getPackets().sendGameMessage("Your Magic level is not high enough for this spell.");
				return;
			}
			if (!checkRunes(player, true, true, 17790, 1, 17783, 1, 17781, 3))
				return;
			player.lock(2);
			Item[] itemsBefore = player.getInventory().getItems().getItemsCopy();
			for (Item item : player.getInventory().getItems().getItems()) {
				if (item == null)
					continue;
				for (Fill fill : Fill.values()) {
					if (fill.getEmpty() == item.getId())
						item.setId(fill.getFull());
				}
			}
			player.getInventory().refreshItems(itemsBefore);
			player.getSkills().addXp(Skills.MAGIC, 65);
			player.getInterfaceManager().openGameTab(4);
			player.setNextAnimation(new Animation(4413));
			player.setNextGraphics(new Graphics(1061, 0, 150));
			break;
		case 35: // low alch
		case 46: // high alch
			final Item target = player.getInventory().getItem(slot);
			if (target == null && slot != -1)
				return;
			boolean highAlch = spellId == 46;
			if (!Magic.checkSpellLevel(player, (highAlch ? 55 : 21)))
				return;
			if (target.getId() == DungeonConstants.RUSTY_COINS) {
				player.getPackets()
						.sendGameMessage("You can't cast " + (highAlch ? "high" : "low") + " alchemy on gold.");
				return;
			}
			if (target.getDefinitions().isDestroyItem() || ItemConstants.getItemDefaultCharges(target.getId()) != -1
					|| !ItemConstants.isTradeable(target)) {
				player.getPackets().sendGameMessage("You can't convert this item..");
				return;
			}
			if (target.getAmount() != 1 && !player.getInventory().hasFreeSlots()) {
				player.getPackets().sendGameMessage("Not enough space in your inventory.");
				return;
			}
			if (!checkRunes(player, true, true, 17783, highAlch ? 5 : 3, 17791, 1))
				return;
			player.lock(4);
			player.getInterfaceManager().openGameTab(7);
			player.getInventory().deleteItem(target.getId(), 1);
			player.getSkills().addXp(Skills.MAGIC, highAlch ? 25 : 15);
			player.getInventory().addItemMoneyPouch(new Item(DungeonConstants.RUSTY_COINS,
					(int) (target.getDefinitions().getValue() * (highAlch ? 0.6D : 0.3D))));
			Item weapon = player.getEquipment().getItem(Equipment.SLOT_WEAPON);
			if (weapon != null && weapon.getName().toLowerCase().contains("staff")) {
				player.setNextAnimation(new Animation(highAlch ? 9633 : 9625));
				player.setNextGraphics(new Graphics(highAlch ? 1693 : 1692));
			} else {
				player.setNextAnimation(new Animation(713));
				player.setNextGraphics(new Graphics(highAlch ? 113 : 112));
			}
			break;
		case 31:// bones to bananas
			if (!Magic.checkSpellLevel(player, 15))
				return;
			else if (!checkRunes(player, true, true, 17791, 1, 17781, 2, 17782, 2))
				return;
			int bones = 0;
			for (int i = 0; i < 28; i++) {
				Item item = player.getInventory().getItem(i);
				if (item == null || Bone.forId(item.getId()) == null)
					continue;
				item.setId(18199);
				bones++;
			}
			if (bones != 0) {
				player.getSkills().addXp(Skills.MAGIC, 25);
				player.getInventory().refresh();
			}
			break;
		case 55:
			if (player.getSkills().getLevel(Skills.MAGIC) < 71) {
				player.getPackets().sendGameMessage("Your Magic level is not high enough for this spell.");
				return;
			}
			if (!checkRunes(player, true, true, 17790, 2, 17789, 2))
				return;
			player.setNextAnimation(new Animation(4411));
			player.setNextGraphics(new Graphics(736, 0, 150));
			// EffectsManager.reset();
			break;
		case 57:
			if (!Magic.checkSpellLevel(player, 74))
				return;
			else if (!checkRunes(player, true, true, 17790, 2, 17789, 2))
				return;
			affectedPeopleCount = 0;
			for (int regionId : player.getMapRegionsIds()) {
				List<Integer> playerIndexes = World.getRegion(regionId).getPlayerIndexes();
				if (playerIndexes == null)
					continue;
				for (int playerIndex : playerIndexes) {
					Player p2 = World.getPlayers().get(playerIndex);
					if (p2 == null || p2 == player || p2.isDead() || !p2.isActive() || p2.hasFinished()
							|| !p2.withinDistance(player, 4))
						continue;
					if (!p2.isAcceptingAid())
						continue;
					player.setNextGraphics(new Graphics(736, 0, 150));
					p2.getPackets().sendGameMessage("You have been cured of all illnesses!");
					affectedPeopleCount++;
				}
			}
			player.setNextAnimation(new Animation(4411));
			player.getPackets().sendGameMessage("The spell affected " + affectedPeopleCount + " nearby people.");
			break;
		default:
			if (Settings.DEBUG)
				Logger.log(Magic.class, "Component " + spellId);
			break;
		}
	}

	public static final boolean checkRunes(Player player, boolean delete, boolean dungeoneering, int... values) {
		int weaponId = player.getEquipment().getWeaponId();
		int shieldId = player.getEquipment().getShieldId();
		int runesCount = 0;
		while (runesCount < values.length) {
			int runeId = values[runesCount++];
			int amount = values[runesCount++];
			if (hasInfiniteRunes(runeId, weaponId, shieldId))
				continue;
			else if (hasSpecialRunes(player, runeId, amount))
				continue;
			else if (dungeoneering) {
				if (player.getInventory().containsItem(runeId - 1689, amount))
					continue;
			}
			if (!player.getInventory().containsItem(runeId, amount)) {
				player.getPackets()
						.sendGameMessage("You do not have enough "
								+ ItemDefinitions.getItemDefinitions(runeId).getName().replace("rune", "Rune")
								+ "s to cast this spell.");
				return false;
			}

		}
		if (delete) {
			if (hasStaffOfLight(weaponId) && !containsRune(LAW_RUNE, values) && !containsRune(NATURE_RUNE, values)
					&& Utils.random(8) == 0) {
				player.getPackets()
						.sendGameMessage("The power of your staff of light saves some runes from being drained.", true);
				return true;
			}
			runesCount = 0;
			while (runesCount < values.length) {
				int runeId = values[runesCount++];
				int amount = values[runesCount++];
				if (hasInfiniteRunes(runeId, weaponId, shieldId))
					continue;
				else if (hasSpecialRunes(player, runeId, amount))
					runeId = getRuneForId(runeId);
				else if (dungeoneering) {
					int bindedRune = runeId - 1689;
					if (player.getInventory().containsItem(bindedRune, amount)) {
						player.getInventory().deleteItem(bindedRune, amount);
						continue; // won't delete the extra rune anyways.
					}
				}
				player.getInventory().deleteItem(runeId, amount);
			}
		}
		return true;
	}

	public static boolean hasSpecialRunes(Player player, int runeId, int amountRequired) {
		if (player.getInventory().containsItem(ELEMENTAL_RUNE, amountRequired)) {
			if (runeId == AIR_RUNE || runeId == WATER_RUNE || runeId == EARTH_RUNE || runeId == FIRE_RUNE)
				return true;
		}
		if (player.getInventory().containsItem(CATALYTIC_RUNE, amountRequired)) {
			if (runeId == ARMADYL_RUNE || runeId == MIND_RUNE || runeId == CHAOS_RUNE || runeId == DEATH_RUNE
					|| runeId == BLOOD_RUNE || runeId == BODY_RUNE || runeId == NATURE_RUNE || runeId == ASTRAL_RUNE
					|| runeId == SOUL_RUNE || runeId == LAW_RUNE)
				return true;
		}
		return false;
	}

	private static final int ELEMENTAL_RUNE = 12850;
	private static final int CATALYTIC_RUNE = 12851;

	public static int getRuneForId(int runeId) {
		if (runeId == AIR_RUNE || runeId == WATER_RUNE || runeId == EARTH_RUNE || runeId == FIRE_RUNE)
			return ELEMENTAL_RUNE;
		else if (runeId == ARMADYL_RUNE || runeId == DEATH_RUNE || runeId == MIND_RUNE || runeId == CHAOS_RUNE
				|| runeId == BLOOD_RUNE || runeId == BODY_RUNE || runeId == NATURE_RUNE || runeId == ASTRAL_RUNE
				|| runeId == SOUL_RUNE || runeId == LAW_RUNE)
			return CATALYTIC_RUNE;
		return -1;
	}

	private static boolean containsRune(int rune, int[] integer) {
		for (int id : integer) {
			if (rune == id)
				return true;
		}
		return false;
	}
}