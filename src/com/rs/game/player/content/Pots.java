package com.rs.game.player.content;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.Hit;
import com.rs.game.Hit.HitLook;
import com.rs.game.activites.clanwars.FfaZone;
import com.rs.game.item.Item;
import com.rs.game.npc.familiar.Familiar;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.controllers.CrucibleController;
import com.rs.game.player.controllers.Wilderness;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

public final class Pots {

	public static final int VIAL = 229, DUNGEONEERING_VIAL = 17490;

	public static enum Pot {

		ATTACK_POTION(new int[] { 2428, 121, 123, 125 }, Effects.ATTACK_POTION),

		STRENGTH_POTION(new int[] { 113, 115, 117, 119 }, Effects.STRENGTH_POTION),

		DEFENCE_POTION(new int[] { 2432, 133, 135, 137 }, Effects.DEFENCE_POTION),

		RANGE_POTION(new int[] { 2444, 169, 171, 173 }, Effects.RANGE_POTION),

		MAGIC_POTION(new int[] { 3040, 3042, 3044, 3046 }, Effects.MAGIC_POTION),

		MAGIC_FLASK(new int[] { 23423, 23425, 23427, 23429, 23431, 23433 }, Effects.MAGIC_POTION),

		ANTI_POISION(new int[] { 2446, 175, 177, 179 }, Effects.ANTIPOISON), SUPER_ANTI_POISION(
				new int[] { 2448, 181, 183, 185 },
				Effects.SUPER_ANTIPOISON), DUNGEONEERING_ANTI_POISION(new int[] { 17566 },
						Effects.ANTIPOISON), PRAYER_POTION(new int[] { 2434, 139, 141, 143 }, Effects.PRAYER_POTION),

		SUPER_ATT_POTION(new int[] { 2436, 145, 147, 149 }, Effects.SUPER_ATT_POTION),

		SUPER_STR_POTION(new int[] { 2440, 157, 159, 161 }, Effects.SUPER_STR_POTION),

		SUPER_DEF_POTION(new int[] { 2442, 163, 165, 167 }, Effects.SUPER_DEF_POTION),

		ENERGY_POTION(new int[] { 3008, 3010, 3012, 3014 }, Effects.ENERGY_POTION),

		SUPER_ENERGY(new int[] { 3016, 3018, 3020, 3022 }, Effects.SUPER_ENERGY),

		EXTREME_ATT_POTION(new int[] { 15308, 15309, 15310, 15311 }, Effects.EXTREME_ATT_POTION),

		EXTREME_STR_POTION(new int[] { 15312, 15313, 15314, 15315 }, Effects.EXTREME_STR_POTION),

		EXTREME_DEF_POTION(new int[] { 15316, 15317, 15318, 15319 }, Effects.EXTREME_DEF_POTION),

		EXTREME_MAGE_POTION(new int[] { 15320, 15321, 15322, 15323 }, Effects.EXTREME_MAG_POTION),

		EXTREME_RANGE_POTION(new int[] { 15324, 15325, 15326, 15327 }, Effects.EXTREME_RAN_POTION),

		SUPER_RESTORE_POTION(new int[] { 3024, 3026, 3028, 3030 }, Effects.SUPER_RESTORE),

		SARADOMIN_BREW(new int[] { 6685, 6687, 6689, 6691 }, Effects.SARADOMIN_BREW),

		RECOVER_SPECIAL(new int[] { 15300, 15301, 15302, 15303 }, Effects.RECOVER_SPECIAL),

		SUPER_PRAYER(new int[] { 15328, 15329, 15330, 15331 }, Effects.SUPER_PRAYER),

		OVERLOAD(new int[] { 15332, 15333, 15334, 15335 }, Effects.OVERLOAD),

		ANTI_FIRE(new int[] { 2452, 2454, 2456, 2458 }, Effects.ANTI_FIRE),

		SUPER_ANTIFIRE(new int[] { 15304, 15305, 15306, 15307 }, Effects.SUPER_ANTI_FIRE),

		SUMMONING_POTION(new int[] { 12140, 12142, 12144, 12146 }, Effects.SUMMONING_POT),

		SUMMONING_FLASK(new int[] { 23621, 23623, 23625, 23627, 23629, 23631 }, Effects.SUMMONING_POT),

		SANFEW_SERUM(new int[] { 10925, 10927, 10929, 10931 }, Effects.SANFEW_SERUM),

		PRAYER_RENEWAL(new int[] { 21630, 21632, 21634, 21636 }, Effects.PRAYER_RENEWAL),

		PRAYER_RENEWAL_FLASK(new int[] { 23609, 23611, 23613, 23615, 23617, 23619 }, Effects.PRAYER_RENEWAL),

		ATTACK_FLASK(new int[] { 23195, 23197, 23199, 23201, 23203, 23205 }, Effects.ATTACK_POTION),

		STRENGTH_FLASK(new int[] { 23207, 23209, 23211, 23213, 23215, 23217 }, Effects.STRENGTH_POTION),

		RESTORE_FLASK(new int[] { 23219, 23221, 23223, 23225, 23227, 23229 }, Effects.RESTORE_POTION),

		DEFENCE_FLASK(new int[] { 23231, 23233, 23235, 23237, 23239, 23241 }, Effects.DEFENCE_POTION),

		PRAYER_FLASK(new int[] { 23243, 23245, 23247, 23249, 23251, 23253 }, Effects.PRAYER_POTION),

		SUPER_ATTACK_FLASK(new int[] { 23255, 23257, 23259, 23261, 23263, 23265 }, Effects.SUPER_ATT_POTION),

		FISHING_FLASK(new int[] { 23267, 23269, 23271, 23273, 23275, 23277 }, Effects.FISHING_POTION),

		SUPER_STRENGTH_FLASK(new int[] { 23279, 23281, 23283, 23285, 23287, 23289 }, Effects.SUPER_STR_POTION),

		SUPER_DEFENCE_FLASK(new int[] { 23291, 23293, 23295, 23297, 23299, 23301 }, Effects.SUPER_DEF_POTION),

		RANGING_FLASK(new int[] { 23303, 23305, 23307, 23309, 23311, 23313 }, Effects.RANGE_POTION),

		ANTIPOISON_FLASK(new int[] { 23315, 23317, 23319, 23321, 23323, 23325 }, Effects.ANTIPOISON),

		SUPER_ANTIPOISON_FLASK(new int[] { 23327, 23329, 23331, 23333, 23335, 23337 }, Effects.SUPER_ANTIPOISON),

		SUPER_ANTIFIRE_FLASK(new int[] { 23489, 23490, 23491, 23492, 23493, 23494 }, Effects.SUPER_ANTI_FIRE),

		SARADOMIN_BREW_FLASK(new int[] { 23351, 23353, 23355, 23357, 23359, 23361 }, Effects.SARADOMIN_BREW),

		ANTIFIRE_FLASK(new int[] { 23363, 23365, 23367, 23369, 23371, 23373 }, Effects.ANTI_FIRE),

		ENERGY_FLASK(new int[] { 23375, 23377, 23379, 23381, 23383, 23385 }, Effects.ENERGY_POTION),

		SUPER_ENERGY_FLASK(new int[] { 23387, 23389, 23391, 23393, 23395, 23397 }, Effects.SUPER_ENERGY),

		SUPER_RESTORE_FLASK(new int[] { 23399, 23401, 23403, 23405, 23407, 23409 }, Effects.SUPER_RESTORE),

		RECOVER_SPECIAL_FLASK(new int[] { 23483, 23484, 23485, 23486, 23487, 23488 }, Effects.RECOVER_SPECIAL),

		EXTREME_ATTACK_FLASK(new int[] { 23495, 23496, 23497, 23498, 23499, 23500 }, Effects.EXTREME_ATT_POTION),

		EXTREME_STRENGTH_FLASK(new int[] { 23501, 23502, 23503, 23504, 23505, 23506 }, Effects.EXTREME_STR_POTION),

		EXTREME_DEFENCE_FLASK(new int[] { 23507, 23508, 23509, 23510, 23511, 23512 }, Effects.EXTREME_DEF_POTION),

		EXTREME_MAGIC_FLASK(new int[] { 23513, 23514, 23515, 23516, 23517, 23518 }, Effects.EXTREME_MAG_POTION),

		EXTREME_RANGING_FLASK(new int[] { 23519, 23520, 23521, 23522, 23523, 23524 }, Effects.EXTREME_RAN_POTION),

		SUPER_PRAYER_FLASK(new int[] { 23525, 23526, 23527, 23528, 23529, 23530 }, Effects.SUPER_PRAYER),

		OVERLOAD_FLASK(new int[] { 23531, 23532, 23533, 23534, 23535, 23536 }, Effects.OVERLOAD),

		BEER(new int[] { 1917, 1919 }, Effects.BEER),

		JUG(new int[] { 1993, 1935 }, Effects.WINE),

		SANFEW_SERUM_FLASK(new int[] { 23567, 23569, 23571, 23573, 23575, 23577 }, Effects.SANFEW_SERUM),
		/**
		 * Combination Potions
		 */

		GRAND_STRENGTH_POTION(new int[] { 32958, 32956, 32954, 32952, 32950, 32948 }, Effects.GRAND_STRENGTH),

		GRAND_RANGING_POTION(new int[] { 32970, 32968, 32966, 32964, 32962, 32960 }, Effects.GRAND_RANGING),

		GRAND_MAGIC_POTION(new int[] { 32982, 32980, 32978, 32976, 32974, 32972 }, Effects.GRAND_MAGIC),

		GRAND_ATTACK_POTION(new int[] { 32994, 32992, 32990, 32988, 32986, 32984 }, Effects.GRAND_ATTACK),

		GRAND_DEFENCE_POTION(new int[] { 33006, 33004, 33002, 33000, 32998, 32996 }, Effects.GRAND_DEFENCE),

		SUPER_MELEE_POTION(new int[] { 33018, 33016, 33014, 33012, 33010, 33008 }, Effects.SUPER_MELEE),

		SUPER_WARMASTERS_POTION(new int[] { 33030, 33028, 33026, 33024, 33022, 33020 }, Effects.SUPER_WARMASTER),

		REPLENISHMENT_POTION(new int[] { 33042, 33040, 33038, 33036, 33034, 33032 }, Effects.REPLENISHMENT),

		WYRMFIRE_POTION(new int[] { 33054, 33052, 33050, 33048, 33046, 33044 }, Effects.WYRMFIRE),

		EXTREME_BRAWLERS_POTION(new int[] { 33066, 33064, 33062, 33060, 33058, 33056 }, Effects.EXTREME_BRAWLERS),

		EXTREME_BATTLEMAGES_POTION(new int[] { 33078, 33076, 33074, 33072, 33070, 33068 }, Effects.EXTREME_BATTLEMAGES),

		EXTREME_SHARPSHOOTERS_POTION(new int[] { 33090, 33088, 33086, 33084, 33082, 33080 },
				Effects.EXTREME_SHARPSHOOTERS),

		EXTREME_WARMASTERS_POTION(new int[] { 33102, 33100, 33098, 33096, 33094, 33092 }, Effects.EXTREME_WARMASTERS),

		SUPREME_STRENGTH_POTION(new int[] { 33114, 33112, 33110, 33108, 33106, 33104 }, Effects.SUPREME_STRENGTH),

		SUPREME_ATTACK_POTION(new int[] { 33126, 33124, 33122, 33120, 33118, 33116 }, Effects.SUPREME_ATTACK),

		SUPREME_DEFENCE_POTION(new int[] { 33138, 33136, 33134, 33132, 33130, 33128 }, Effects.SUPREME_DEFENCE),

		SUPREME_MAGIC_POTION(new int[] { 33150, 33148, 33146, 33144, 33142, 33140 }, Effects.SUPREME_MAGIC),

		SUPREME_RANGING_POTION(new int[] { 33162, 33160, 33158, 33156, 33154, 33152 }, Effects.SUPREME_RANGING),

		BRIGHTFIRE_POTION(new int[] { 33174, 33172, 33170, 33168, 33166, 33164 }, Effects.BRIGHTFIRE),

		SUPER_PRAYER_RENEWAL_POTION(new int[] { 33186, 33184, 33182, 33180, 33178, 33176 }, Effects.SUPER_PRAYER),

		HOLY_OVERLOAD_POTION(new int[] { 33246, 33244, 33242, 33240, 33238, 33236 }, Effects.HOLY_OVERLOAD),

		SEARING_OVERLOAD_POTION(new int[] { 33258, 33256, 33254, 33252, 33250, 33248 }, Effects.SEARING_OVERLOAD),

		SUPREME_OVERLOAD_POTION(new int[] { 33210, 33208, 33206, 33204, 33202, 33200 }, Effects.SUPREME_OVERLOAD),

		RANGING_POTION(new int[] { 2444, 169, 171, 173 }, Effects.RANGE_POTION),

		ANTIPOISON_POTION(new int[] { 2446, 175, 177, 179 }, Effects.ANTIPOISON),

		SUPER_ANTIPOISON(new int[] { 2448, 181, 183, 185 }, Effects.SUPER_ANTIPOISON),

		ANTIPOISION_PLUS(new int[] { 5943, 5945, 5947, 5949 }, Effects.ANTIPOISON_PLUS),

		ANTIPOISON_DOUBLE_PLUS(new int[] { 5952, 5954, 5956, 5958 }, Effects.ANTIPOISON_DOUBLE_PLUS),

		ANTIPOISON_PLUS_FLASK(new int[] { 23579, 23581, 23583, 23585, 23587, 23589 }, Effects.ANTIPOISON_PLUS),

		ANTIPOISON_DOUBLE_PLUS_Flask(new int[] { 23591, 23593, 23595, 23597, 23599, 23601 },
				Effects.ANTIPOISON_DOUBLE_PLUS),

		ZAMORAK_BREW(new int[] { 2450, 189, 191, 193 }, Effects.ZAMORAK_BREW),

		ZAMORAK_BREW_FLASK(new int[] { 23339, 23341, 23343, 23345, 23347, 23349 }, Effects.ZAMORAK_BREW),

		SUPER_ATTACK_POTION(new int[] { 2436, 145, 147, 149 }, Effects.SUPER_ATT_POTION),

		SUPER_STRENGTH_POTION(new int[] { 2440, 157, 159, 161 }, Effects.SUPER_STR_POTION),

		SUPER_DEFENCE_POTION(new int[] { 2442, 163, 165, 167 }, Effects.SUPER_DEF_POTION),

		SUPER_RANGING_POTION(new int[] { 2444, 169, 171, 173 }, Effects.SUPER_RANGE_POTION),

		SUPER_MAGIC_POTION(new int[] { 3040, 3042, 3044, 3046 }, Effects.SUPER_DEF_POTION),

		SUPER_ENERGY_POTION(new int[] { 3016, 3018, 3020, 3022 }, Effects.SUPER_ENERGY),

		EXTREME_ATTACK_POTION(new int[] { 15308, 15309, 15310, 15311 }, Effects.EXTREME_ATT_POTION),

		EXTREME_STRENGTH_POTION(new int[] { 15312, 15313, 15314, 15315 }, Effects.EXTREME_STR_POTION),

		EXTREME_DEFENCE_POTION(new int[] { 15316, 15317, 15318, 15319 }, Effects.EXTREME_DEF_POTION),

		EXTREME_MAGIC_POTION(new int[] { 15320, 15321, 15322, 15323 }, Effects.EXTREME_MAG_POTION),

		EXTREME_RANGING_POTION(new int[] { 15324, 15325, 15326, 15327 }, Effects.EXTREME_RAN_POTION),

		RESTORE_POTION(new int[] { 2430, 127, 129, 131 }, Effects.RESTORE_POTION),

		SARADOMIN_BREW_POTION(new int[] { 6685, 6687, 6689, 6691 }, Effects.SARADOMIN_BREW),

		SUPER_SARADOMIN_BREW_POTION(new int[] { 28191, 28193, 28195, 28197 }, Effects.SUPER_SARADOMIN_BREW),

		RECOVER_SPECIAL_POTION(new int[] { 15300, 15301, 15302, 15303 }, Effects.RECOVER_SPECIAL),

		SUPER_PRAYER_POTION(new int[] { 15328, 15329, 15330, 15331 }, Effects.SUPER_PRAYER),

		OVERLOAD_POTION(new int[] { 15332, 15333, 15334, 15335 }, Effects.OVERLOAD),

		ANTIFIRE_POTION(new int[] { 2452, 2454, 2456, 2458 }, Effects.ANTI_FIRE),

		SUPER_ANTIFIRE_POTION(new int[] { 15304, 15305, 15306, 15307 }, Effects.SUPER_ANTI_FIRE),

		SANFEW_SERUM_POTION(new int[] { 10925, 10927, 10929, 10931 }, Effects.SANFEW_SERUM),

		PRAYER_RENEWAL_POTION(new int[] { 21630, 21632, 21634, 21636 }, Effects.PRAYER_RENEWAL),

		FISHING_POTION(new int[] { 2438, 151, 153, 155 }, Effects.FISHING_POTION),

		AGILITY_POTION(new int[] { 3032, 3034, 3036, 3038 }, Effects.AGILITY_POTION),

		AGILITY_FLASK(new int[] { 23411, 23413, 23415, 23417, 23419, 23421 }, Effects.AGILITY_POTION),

		COMBAT_POTION(new int[] { 9740, 9742, 9744, 9746 }, Effects.COMBAT_POTION),

		COMBAT_FLASK(new int[] { 23447, 23449, 23451, 23453, 23455, 23457 }, Effects.COMBAT_POTION),

		GUTHIX_REST_POTION(new int[] { 4417, 4419, 4421, 4423 }, Effects.GUTHIX_REST),

		GUTHIX_REST_FLASK(new int[] { 29448, 29450, 29452, 29454, 29456, 29458 }, Effects.GUTHIX_REST),

		SUPER_GUTHIX_REST(new int[] { 28207, 28209, 28211, 28213 }, Effects.SUPER_GUTHIX_REST),

		SUPER_GUTHIX_REST_FLASK(new int[] { 28239, 28241, 28243, 28245, 28247, 28249 }, Effects.SUPER_GUTHIX_REST),

		MAGIC_ESSENCE_POTION(new int[] { 4417, 4419, 4421, 4423 }, Effects.MAGIC_ESSENCE),

		MAGIC_ESSENCE_FLASK(new int[] { 23447, 23449, 23451, 23453, 23455, 23457 }, Effects.MAGIC_ESSENCE),

		CRAFTING_POTION(new int[] { 14838, 14840, 14842, 14844 }, Effects.CRAFTING_POTION),

		CRAFTING_FLASK(new int[] { 23459, 23461, 23463, 23465, 23467, 23469 }, Effects.CRAFTING_POTION),

		HUNTER_POTION(new int[] { 9998, 10000, 10002, 10004 }, Effects.HUNTER_POTION),

		HUNTER_FLASK(new int[] { 23435, 23437, 23439, 23441, 23443, 23435 }, Effects.HUNTER_POTION),

		FLETCHING_POTION(new int[] { 14846, 14848, 14850, 14852 }, Effects.FLETCHING_POTION),

		FLETCHING_FLASK(new int[] { 23471, 23473, 23475, 23477, 23479, 23481 }, Effects.FLETCHING_POTION),

		SUPER_RANGING_FLASK(new int[] { 23303, 23305, 23307, 23309, 23311, 23313 }, Effects.SUPER_RANGE_POTION),

		SUPER_MAGIC_FLASK(new int[] { 23423, 23425, 23427, 23429, 23431, 23433 }, Effects.SUPER_MAGIC_POTION),

		TANKARD(new int[] { 3803 }, Effects.TANKARD),

		GREENMANS_ALE(new int[] { 1909 }, Effects.GREENMANS_ALE),

		GREENMANS_ALE_KEG(new int[] { 5793, 5791, 5789, 5787 }, Effects.GREENMANS_ALE),

		AXEMANS_ALE(new int[] { 5751 }, Effects.AXEMANS),

		AXEMANS_ALE_KEG(new int[] { 5825, 5823, 5821, 5819 }, Effects.AXEMANS),

		SLAYER_RESPITE(new int[] { 5759 }, Effects.SLAYER_RESPITE),

		SLAYER_RESPITE_KEG(new int[] { 5841, 5839, 5837, 5835 }, Effects.SLAYER_RESPITE),

		RANGERS_AID(new int[] { 15119 }, Effects.RANGERS_AID),

		MOONLIGHT_MEAD(new int[] { 2955 }, Effects.MOONLIGHT_MEAD),

		MOONLIGHT_MEAD_KEG(new int[] { 5817, 5815, 5813, 5811 }, Effects.MOONLIGHT_MEAD),

		DRAGON_BITTER(new int[] { 1911 }, Effects.DRAGON_BITTER),

		DRAGON_BITTER_KEG(new int[] { 5809, 5807, 5805, 5803 }, Effects.DRAGON_BITTER),

		ASGARNIAN_ALE(new int[] { 1905 }, Effects.ASGARNIAN_ALE),

		ASGARNIAN_ALE_KEG(new int[] { 5785, 5783, 5781, 5779 }, Effects.ASGARNIAN_ALE),

		CHEF_DELIGHT(new int[] { 5755 }, Effects.CHEF_DELIGHT),

		CHEF_DELIGHT_KEG(new int[] { 5833, 5831, 5829, 5827 }, Effects.CHEF_DELIGHT),

		CIDER(new int[] { 5763 }, Effects.CIDER),

		CIDER_KEG(new int[] { 5849, 5847, 5845, 5843 }, Effects.CIDER),

		WIZARD_MIND_BOMB(new int[] { 1907 }, Effects.WIZARD_MIND_BOMB),

		DWARVEN_STOUT(new int[] { 1913 }, Effects.DWARVEN_STOUT),

		DWARVEN_STOUT_KEG(new int[] { 5777, 5775, 5773, 5771 }, Effects.DWARVEN_STOUT),

		GROG(new int[] { 1915 }, Effects.GROG),

		KEG_OF_BEER(new int[] { 3801 }, Effects.KEG_OF_BEER),

		BANDIT_BREW(new int[] { 4627 }, Effects.BANDIT_BREW),

		WEAK_MELEE_POTION(new int[] { 17560 }, Effects.WEAK_MELEE_POTION),

		WEAK_MAGIC_POTION(new int[] { 17556 }, Effects.WEAK_MAGIC_POTION),

		WEAK_RANGE_POTION(new int[] { 17558 }, Effects.WEAK_RANGE_POTION),

		WEAK_DEFENCE_POTION(new int[] { 17562 }, Effects.WEAK_DEFENCE_POTION),

		WEAK_STAT_RESTORE(new int[] { 17564 }, Effects.WEAK_STAT_RESTORE_POTION),

		WEAK_CURE_POTION(new int[] { 17568 }, Effects.WEAK_STAT_CURE_POTION),

		WEAK_REJUVINATION_POTION(new int[] { 17570 }, Effects.WEAK_REJUVINATION_POTION),

		WEAK_GATHERS_POTION(new int[] { 17574 }, Effects.WEAK_GATHERER_POTION),

		WEAK_ARTISTAN_POTION(new int[] { 17576 }, Effects.WEAK_ARTISTIANS_POTION),

		WEAK_NATURALIST_POTION(new int[] { 17578 }, Effects.WEAK_NATURALISTS_POTION),

		WEAK_SURVIVALIST_POTION(new int[] { 17580 }, Effects.WEAK_SURVIVALISTS_POTION),

		REGULAR_MELEE_POTION(new int[] { 17586 }, Effects.REGULAR_MELEE_POTION),

		REGULAR_MAGIC_POTION(new int[] { 17582 }, Effects.REGULAR_MAGIC_POTION),

		REGULAR_RANGE_POTION(new int[] { 17584 }, Effects.REGULAR_RANGE_POTION),

		REGULAR_DEFENCE_POTION(new int[] { 17588 }, Effects.REGULAR_DEFENCE_POTION),

		REGULAR_STAT_RESTORE(new int[] { 17590 }, Effects.REGULAR_STAT_RESTORE_POTION),

		REGULAR_CURE_POTION(new int[] { 17592 }, Effects.REGULAR_STAT_CURE_POTION),

		REGULAR_REJUVINATION_POTION(new int[] { 17594 }, Effects.REGULAR_REJUVINATION_POTION),

		REGULAR_GATHERS_POTION(new int[] { 17598 }, Effects.REGULAR_GATHERER_POTION),

		REGULAR_ARTISTAN_POTION(new int[] { 17600 }, Effects.REGULAR_ARTISTIANS_POTION),

		REGULAR_NATURALIST_POTION(new int[] { 17602 }, Effects.REGULAR_NATURALISTS_POTION),

		REGULAR_SURVIVALIST_POTION(new int[] { 17604 }, Effects.REGULAR_SURVIVALISTS_POTION),

		STRONG_MELEE_POTION(new int[] { 17610 }, Effects.STRONG_MELEE_POTION),

		STRONG_MAGIC_POTION(new int[] { 17606 }, Effects.STRONG_MAGIC_POTION),

		STRONG_RANGE_POTION(new int[] { 17608 }, Effects.STRONG_RANGE_POTION),

		STRONG_DEFENCE_POTION(new int[] { 17612 }, Effects.STRONG_DEFENCE_POTION),

		STRONG_STAT_RESTORE(new int[] { 17614 }, Effects.STRONG_STAT_RESTORE_POTION),

		STRONG_CURE_POTION(new int[] { 17616 }, Effects.STRONG_STAT_CURE_POTION),

		STRONG_REJUVINATION_POTION(new int[] { 17618 }, Effects.STRONG_REJUVINATION_POTION),

		STRONG_GATHERS_POTION(new int[] { 17622 }, Effects.STRONG_GATHERER_POTION),

		STRONG_ARTISTAN_POTION(new int[] { 17624 }, Effects.STRONG_ARTISTIANS_POTION),

		STRONG_NATURALIST_POTION(new int[] { 17626 }, Effects.STRONG_NATURALISTS_POTION),

		STRONG_SURVIVALIST_POTION(new int[] { 17628 }, Effects.STRONG_SURVIVALISTS_POTION),

		;

		;

		public int[] id;
		private Effects effect;

		private Pot(int[] id, Effects effect) {
			this.id = id;
			this.effect = effect;
		}

		public boolean isFlask() {
			return getMaxDoses() == 6;
		}

		public boolean isPotion() {
			return getMaxDoses() == 4;
		}

		public int getMaxDoses() {
			return id.length;
		}

		public int getIdForDoses(int doses) {
			return id[getMaxDoses() - doses];
		}

		public Pot toFlask() {
			if (isFlask())
				return this;
			for (int i = 0; i < Pot.values().length; i++) {
				Pot pot = Pot.values()[i];
				if (pot == null || !pot.isFlask())
					continue;
				String checkName = Pot.values()[i].name().toLowerCase().replace("_flask", "");
				String thisName = Pot.values()[ordinal()].name().toLowerCase().replace("_potion", "");
				if (checkName.equals(thisName))
					return pot;
			}
			return null;
		}

		public Pot toPotion() {
			for (int i = 0; i < Pot.values().length; i++) {
				Pot pot = Pot.values()[i];
				if (pot == null || !pot.isPotion())
					continue;
				String checkName = Pot.values()[i].name().toLowerCase().replace("_potion", "");
				String thisName = Pot.values()[ordinal()].name().toLowerCase().replace("_flask", "");
				if (checkName.equals(thisName))
					return pot;
			}
			return null;
		}

		public int[] getNotedId() {
			int[] notedIds = new int[id.length];
			for (int i = 0; i < id.length; i++) {
				int notedId = ItemDefinitions.getItemDefinitions(id[i]).getCertId();
				notedIds[i] = notedId == -1 ? id[i] : notedId;
			}
			return notedIds;
		}

		public int[] getId() {
			return id;
		}
	}
	

	private enum Effects {
		ATTACK_POTION(Skills.ATTACK) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.1));
			}
		},
		FISHING_POTION(Skills.FISHING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (level + 3);
			}
		},
		ZAMORAK_BREW(Skills.ATTACK) {
			int toAdd;

			@Override
			public void extra(Player player) {
				toAdd = (player.getSkills().getLevelForXp(Skills.ATTACK));
				player.getSkills().set(Skills.ATTACK, toAdd);
			}

		},
		SANFEW_SERUM(Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGE, Skills.AGILITY,
				Skills.COOKING, Skills.CRAFTING, Skills.FARMING, Skills.FIREMAKING, Skills.FISHING, Skills.FLETCHING,
				Skills.HERBLORE, Skills.MINING, Skills.RUNECRAFTING, Skills.SLAYER, Skills.SMITHING, Skills.THIEVING,
				Skills.WOODCUTTING, Skills.SUMMONING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int boost = (int) (realLevel * 0.33);
				if (actualLevel > realLevel)
					return actualLevel;
				if (actualLevel + boost > realLevel)
					return realLevel;
				return actualLevel + boost;
			}

			@Override
			public void extra(Player player) {
				player.getPrayer()
						.restorePrayer((int) ((int) (player.getSkills().getLevelForXp(Skills.PRAYER) * 0.33 * 10)
								* player.getAuraManager().getPrayerPotsRestoreMultiplier()));
				player.addPoisonImmune(180000);
				// TODO DISEASE HEALING
			}

		},
		SUMMONING_POT(Skills.SUMMONING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int restore = (int) (Math.floor(player.getSkills().getLevelForXp(Skills.SUMMONING) * 0.25) + 7);
				if (actualLevel + restore > realLevel)
					return realLevel;
				return actualLevel + restore;
			}

			@Override
			public void extra(Player player) {
				Familiar familiar = player.getFamiliar();
				if (familiar != null)
					familiar.restoreSpecialAttack(15);
			}
		},
		ANTIPOISON() {
			@Override
			public void extra(Player player) {
				player.addPoisonImmune((player.getPerkManager().herbivore ? 2 : 1) * 86000);
				player.sendMessage("You are now immune to all kinds of poison.");
				if (player.getPerkManager().herbivore)
					player.sendMessage("Antipoison extended from 86 secs. to 172 secs. thanks to Herbivore perk.",
							true);
			}
		},
		SUPER_ANTIPOISON() {
			@Override
			public void extra(Player player) {
				player.addPoisonImmune((player.getPerkManager().herbivore ? 2 : 1) * 346000);
				player.sendMessage("You are now immune to all kinds of poison.");

				if (player.getPerkManager().herbivore)
					player.sendMessage(
							"Super Antipoison extended from 346 secs. to 692 secs. thanks to Herbivore perk.", true);
			}
		},
		ENERGY_POTION() {
			@Override
			public void extra(Player player) {
				int restoredEnergy = player.getRunEnergy() + 20;
				player.setRunEnergy(restoredEnergy > 100 ? 100 : restoredEnergy);
			}
		},
		SUPER_ENERGY() {
			@Override
			public void extra(Player player) {
				int restoredEnergy = player.getRunEnergy() + 40;
				player.setRunEnergy(restoredEnergy > 100 ? 100 : restoredEnergy);
			}
		},
		ANTI_FIRE() {
			@Override
			public void extra(final Player player) {
				player.addFireImmune((player.getPerkManager().herbivore ? 2 : 1) * 360000);
				final long current = player.getFireImmune();
				player.sendMessage("You are now immune to all kinds of dragonfire.");

				if (player.getPerkManager().herbivore)
					player.sendMessage("Anti-fire extended from 5 mins. to 10 mins. thanks to Herbivore perk.", true);

				WorldTasksManager.schedule(new WorldTask() {
					boolean stop = false;

					@Override
					public void run() {
						if (current != player.getFireImmune()) {
							stop();
							return;
						}
						if (!stop) {
							player.sendMessage(Colors.red + "Your antifire potion is about to run out...");
							stop = true;
						} else {
							stop();
							player.sendMessage(Colors.red + "Your antifire potion has ran out.</col>");
						}
					}
				}, (player.getPerkManager().herbivore ? 2 : 1) * 500, 100);
			}
		},
		SUPER_ANTI_FIRE() {
			@Override
			public void extra(final Player player) {
				player.addSuperAntiFire((player.getPerkManager().herbivore ? 2 : 1) * 360000);

				if (player.getPerkManager().herbivore)
					player.sendMessage("Super Anti-fire extended from 5 mins. to 10 mins. thanks to Herbivore perk.",
							true);

				final long current = player.getSuperAntiFire();
				WorldTasksManager.schedule(new WorldTask() {
					boolean stop = false;

					@Override
					public void run() {
						if (current != player.getSuperAntiFire()) {
							stop();
							return;
						}
						if (!stop) {
							player.sendMessage(Colors.red + "Your super antifire potion is about to run out...");
							stop = true;
						} else {
							stop();
							player.sendMessage(Colors.red + "Your super antifire potion has run out.");
						}
					}
				}, (player.getPerkManager().herbivore ? 2 : 1) * 500, 100);
			}
		},
		STRENGTH_POTION(Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.1));
			}
		},
		DEFENCE_POTION(Skills.DEFENCE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.1));
			}
		},
		RANGE_POTION(Skills.RANGE) {

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.1));
			}
		},
		MAGIC_POTION(Skills.MAGIC) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return level + 5;
			}
		},
		PRAYER_POTION() {
			@Override
			public void extra(Player player) {
				player.getPrayer().restorePrayer(
						(int) ((int) (Math.floor(player.getSkills().getLevelForXp(Skills.PRAYER) * 2.5) + 70)
								* player.getAuraManager().getPrayerPotsRestoreMultiplier()));
			}
		},
		SUPER_STR_POTION(Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.15));
			}
		},
		SUPER_DEF_POTION(Skills.DEFENCE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.15));
			}
		},
		SUPER_ATT_POTION(Skills.ATTACK) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.15));
			}
		},
		EXTREME_STR_POTION(Skills.STRENGTH) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness
						|| player.getControlerManager().getControler() instanceof CrucibleController
						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.22));
			}
		},
		EXTREME_DEF_POTION(Skills.DEFENCE) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness
						|| player.getControlerManager().getControler() instanceof CrucibleController
						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.22));
			}
		},
		EXTREME_ATT_POTION(Skills.ATTACK) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness
						|| player.getControlerManager().getControler() instanceof CrucibleController
						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 5 + (realLevel * 0.22));
			}
		},
		EXTREME_RAN_POTION(Skills.RANGE) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness
						|| player.getControlerManager().getControler() instanceof CrucibleController
						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 4 + (Math.floor(realLevel / 5.2)));
			}
		},
		EXTREME_MAG_POTION(Skills.MAGIC) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness
						|| player.getControlerManager().getControler() instanceof CrucibleController
						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return level + 7;
			}
		},
		RECOVER_SPECIAL() {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness
						|| player.getControlerManager().getControler() instanceof CrucibleController
						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				Long time = (Long) player.getTemporaryAttributtes().get("Recover_Special_Pot");
				if (time != null && Utils.currentTimeMillis() - time < 30000) {
					player.sendMessage("You may only use this pot every 30 seconds.");
					return false;
				}
				return true;
			}

			@Override
			public void extra(Player player) {
				player.getTemporaryAttributtes().put("Recover_Special_Pot", Utils.currentTimeMillis());
				player.getCombatDefinitions().restoreSpecialAttack(25);
			}
		},
		SARADOMIN_BREW("You drink some of the foul liquid.", Skills.ATTACK, Skills.DEFENCE, Skills.STRENGTH,
				Skills.MAGIC, Skills.RANGE) {

			@Override
			public boolean canDrink(Player player) {
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.DEFENCE) {
					int boost = (int) (realLevel * 0.25);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel * 0.90);
				}
			}

			@Override
			public void extra(Player player) {
				int hitpointsModification = (int) (player.getMaxHitpoints() * 0.15);
				player.heal(hitpointsModification + 20, hitpointsModification);
			}
		},

		
		
		
		OVERLOAD() {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness
						|| player.getControlerManager().getControler() instanceof CrucibleController
						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				if (player.getOverloadDelay() > 0) {
					player.sendMessage("You may only use this potion every five minutes.");
					return false;
				}
				if (player.getHitpoints() <= 500 || player.getOverloadDelay() > 480) {
					player.getPackets()
							.sendGameMessage("You need more than 500 life points to survive the power of overload.");
					return false;
				}
				return true;
			}

			@Override
			public void extra(final Player player) {
				player.setOverloadDelay((player.getPerkManager().herbivore ? 2 : 1) * 501);

				if (player.getPerkManager().herbivore)
					player.sendMessage("Overload extended from 5 mins. to 10 mins. thanks to Herbivore perk.", true);

				WorldTasksManager.schedule(new WorldTask() {
					int count = 4;

					@Override
					public void run() {
						if (count == 0)
							stop();
						player.setNextAnimation(new Animation(3170));
						player.setNextGraphics(new Graphics(560));
						player.applyHit(new Hit(player, 100, HitLook.REGULAR_DAMAGE, 0));
						count--;
					}
				}, 0, 2);
			}
		},
		SUPER_PRAYER() {
			@Override
			public void extra(Player player) {
				player.getPrayer()
						.setPrayerpoints((int) ((int) (70 + (player.getSkills().getLevelForXp(Skills.PRAYER) * 3.43))
								* player.getAuraManager().getPrayerPotsRestoreMultiplier()));
			}
		},
		PRAYER_RENEWAL() {
			@Override
			public void extra(Player player) {
				player.setPrayerRenewalDelay((player.getPerkManager().herbivore ? 2 : 1) * 501);

				if (player.getPerkManager().herbivore)
					player.sendMessage("Prayer Renewal extended from 5 mins. to 10 mins. thanks to Herbivore perk.",
							true);
			}
		},
		SUPER_RESTORE(Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGE, Skills.AGILITY,
				Skills.COOKING, Skills.CRAFTING, Skills.FARMING, Skills.FIREMAKING, Skills.FISHING, Skills.FLETCHING,
				Skills.HERBLORE, Skills.MINING, Skills.RUNECRAFTING, Skills.SLAYER, Skills.SMITHING, Skills.THIEVING,
				Skills.WOODCUTTING, Skills.SUMMONING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int boost = (int) (realLevel * 0.33);
				if (actualLevel > realLevel)
					return actualLevel;
				if (actualLevel + boost > realLevel)
					return realLevel;
				return actualLevel + boost;
			}

			@Override
			public void extra(Player player) {
				player.getPrayer()
						.restorePrayer((int) ((int) (player.getSkills().getLevelForXp(Skills.PRAYER) * 0.33 * 10)
								* player.getAuraManager().getPrayerPotsRestoreMultiplier()));
			}

		},
		RESTORE_POTION(Skills.ATTACK, Skills.STRENGTH, Skills.MAGIC, Skills.RANGE, Skills.PRAYER) {

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int boost = (int) (realLevel * 0.33);
				if (actualLevel > realLevel)
					return actualLevel;
				if (actualLevel + boost > realLevel)
					return realLevel;
				return actualLevel + boost;
			}
		},
		BEER(Skills.ATTACK, Skills.STRENGTH) {

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.STRENGTH) {
					int boost = (int) (realLevel * 0.07);
					if (actualLevel > realLevel)
						return actualLevel;
					if (actualLevel + boost > realLevel)
						return realLevel;
					return actualLevel + boost;
				} else {
					return (int) (actualLevel * 0.90);
				}
			}

			@Override
			public void extra(Player player) {
				player.sendMessage("You drink the beer. You feel slightly reinvigorated...", true);
				player.sendMessage("...and slightly dizzy too.", true);
			}
		},

		STRENGTH_BOOST(Skills.STRENGTH) {

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				return (int) (actualLevel * 1.1);
			}

			@Override
			public void extra(Player player) {
				player.sendMessage("You drink the chocolatey milk..", true);
				player.sendMessage(".. and feel a lot more positive and stronger.", true);
			}
		},

		WINE(Skills.ATTACK) {
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				return (int) (actualLevel * 0.90);
			}

			@Override
			public void extra(Player player) {
				player.sendMessage("You drink the wine. You feel slightly reinvigorated...", true);
				player.sendMessage("...and slightly dizzy too.", true);
				player.heal(70);
			}
		},
		GRAND_STRENGTH(Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.14));
			}
		},
		GRAND_RANGING(Skills.RANGE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.14));
			}
		},
		GRAND_MAGIC(Skills.MAGIC) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.14));
			}
		},
		GRAND_ATTACK(Skills.ATTACK) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.14));
			}
		},
		GRAND_DEFENCE(Skills.DEFENCE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.14));
			}
		},
		SUPER_MELEE(Skills.ATTACK, Skills.DEFENCE, Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.12));
			}
		},
		SUPER_WARMASTER(Skills.ATTACK, Skills.DEFENCE, Skills.STRENGTH, Skills.MAGIC, Skills.RANGE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.12));
			}
		},
		REPLENISHMENT(Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGE, Skills.AGILITY,
				Skills.COOKING, Skills.CRAFTING, Skills.FARMING, Skills.FIREMAKING, Skills.FISHING, Skills.FLETCHING,
				Skills.HERBLORE, Skills.MINING, Skills.RUNECRAFTING, Skills.SLAYER, Skills.SMITHING, Skills.THIEVING,
				Skills.WOODCUTTING, Skills.SUMMONING) {
			@Override
			public void extra(Player player) {
				player.heal(200);
				player.getPrayer()
						.restorePrayer((int) ((int) (player.getSkills().getLevelForXp(Skills.PRAYER) * 0.25 * 10)
								* player.getAuraManager().getPrayerPotsRestoreMultiplier()));
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int boost = (int) (realLevel * 0.25) + 8;
				if (actualLevel > realLevel)
					return actualLevel;
				if (actualLevel + boost > realLevel)
					return realLevel;
				return actualLevel + boost;
			}

		},
		WYRMFIRE() {
			@Override
			public void extra(final Player player) {
				player.addSuperAntiFire(360000);
				final long current = player.getSuperAntiFire();
				player.sendMessage("You are now completely immune to dragonfire.");
				WorldTasksManager.schedule(new WorldTask() {
					boolean stop = false;

					@Override
					public void run() {
						if (current != player.getSuperAntiFire()) {
							stop();
							return;
						}
						if (!stop) {
							player.sendMessage("<col=480000>Your wyrmfire potion is about to run out...</col>");
							stop = true;
						} else {
							stop();
							player.sendMessage("<col=480000>Your wymrfire potion has ran out...</col>");
						}
					}
				}, 500, 100);
			}
		},
		EXTREME_BRAWLERS(Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness
						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.15));
			}
		},
		EXTREME_BATTLEMAGES(Skills.MAGIC, Skills.DEFENCE) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness
						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.15));
			}
		},
		EXTREME_SHARPSHOOTERS(Skills.RANGE, Skills.DEFENCE) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness
						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.15));
			}
		},
		EXTREME_WARMASTERS(Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGE, Skills.MAGIC) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness
						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.15));
			}
		},
		SUPREME_ATTACK(Skills.ATTACK) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 4 + (realLevel * 0.16));
			}
		},
		SUPREME_STRENGTH(Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 4 + (realLevel * 0.16));
			}
		},
		SUPREME_DEFENCE(Skills.DEFENCE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 4 + (realLevel * 0.16));
			}
		},
		SUPREME_RANGING(Skills.RANGE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 4 + (realLevel * 0.16));
			}
		},
		SUPREME_MAGIC(Skills.MAGIC) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 4 + (realLevel * 0.16));
			}
		},
		BRIGHTFIRE() {
			@Override
			public void extra(final Player player) {
				player.addSuperAntiFire(360000);
				player.setPrayerRenewalDelay(486);
				final long current = player.getSuperAntiFire();
				player.sendMessage("You are now completely immune to dragonfire and will receive prayer points.");
				WorldTasksManager.schedule(new WorldTask() {
					boolean stop = false;

					@Override
					public void run() {
						if (current != player.getSuperAntiFire()) {
							stop();
							return;
						}
						if (!stop) {
							player.sendMessage("<col=480000>Your brightfire potion is about to run out...</col>");
							stop = true;
						} else {
							stop();
							player.sendMessage("<col=480000>Your brightfire potion has ran out...</col>");
						}
					}
				}, 500, 100);
			}
		},
		SEARING_OVERLOAD() {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness

						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				if (player.getHitpoints() <= 500 || player.getOverloadDelay() > 480) {
					player.sendMessage("You need more than 500 life points to survive the power of overload.");
					return false;
				}
				return true;
			}

			@Override
			public void extra(final Player player) {
				player.setOverloadDelay(501);
				startOverLoadAnimation(player);
				player.addFireImmune(360000);
				final long current = player.getFireImmune();
				player.sendMessage("You are now immune to dragonfire.");
				WorldTasksManager.schedule(new WorldTask() {
					boolean stop = false;

					@Override
					public void run() {
						if (current != player.getFireImmune()) {
							stop();
							return;
						}
						if (!stop) {
							player.sendMessage("<col=480000>Your searing overload potion is about to run out...</col>");
							stop = true;
						} else {
							stop();
							player.sendMessage("<col=480000>Your searing overload potion has ran out...</col>");
						}
					}
				}, 500, 100);
			}
		},

		SUPREME_OVERLOAD(Skills.ATTACK, Skills.DEFENCE, Skills.STRENGTH, Skills.RANGE, Skills.MAGIC) {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness

						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				if (player.getHitpoints() <= 500 || player.getOverloadDelay() > 480) {
					player.sendMessage("You need more than 500 life points to survive the power of overload.");
					return false;
				}
				return true;
			}

			@Override
			public void extra(final Player player) {
				player.setOverloadDelay(501);
				startOverLoadAnimation(player);
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				final int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 6 + (realLevel * 0.18));
			}
		},

		HOLY_OVERLOAD() {

			@Override
			public boolean canDrink(Player player) {
				if (player.getControlerManager().getControler() instanceof Wilderness

						|| FfaZone.isOverloadChanged(player)) {
					player.sendMessage("You cannot drink this potion here.");
					return false;
				}
				if (player.getHitpoints() <= 500 || player.getOverloadDelay() > 480) {
					player.sendMessage("You need more than 500 life points to survive the power of overload.");
					return false;
				}
				return true;
			}

			@Override
			public void extra(final Player player) {
				player.setOverloadDelay(501);
				player.setPrayerRenewalDelay(500);
				startOverLoadAnimation(player);
			}
		},
		WEAK_MELEE_POTION(Skills.ATTACK, Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.07));
			}
		},

		WEAK_MAGIC_POTION(Skills.MAGIC) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.07));
			}
		},

		WEAK_RANGE_POTION(Skills.RANGE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.07));
			}
		},

		WEAK_DEFENCE_POTION(Skills.DEFENCE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.07));
			}
		},

		WEAK_STAT_RESTORE_POTION(Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGE,
				Skills.AGILITY, Skills.COOKING, Skills.CRAFTING, Skills.FARMING, Skills.FIREMAKING, Skills.FISHING,
				Skills.FLETCHING, Skills.HERBLORE, Skills.MINING, Skills.RUNECRAFTING, Skills.SLAYER, Skills.SMITHING,
				Skills.THIEVING, Skills.WOODCUTTING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int boost = (int) (realLevel * 0.12) + 5;
				if (actualLevel > realLevel)
					return actualLevel;
				if (actualLevel + boost > realLevel)
					return realLevel;
				return actualLevel + boost;
			}
		},

		WEAK_STAT_CURE_POTION() {
			@Override
			public void extra(Player player) {
				player.addFireImmune((player.getPerkManager().herbivore ? 2 : 1) * 500);
				player.addPoisonImmune((player.getPerkManager().herbivore ? 2 : 1) * 500);
				if (player.getPerkManager().herbivore)
					player.getPackets().sendGameMessage(
							"Your poison immunity and anti fire immunity time increased by double for having herbivore perk.");
			}
		},

		WEAK_REJUVINATION_POTION(Skills.SUMMONING, Skills.PRAYER) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int restore = (int) (Math.floor(player.getSkills().getLevelForXp(skillId) * 0.08) + 4);
				if (actualLevel + restore > realLevel)
					return realLevel;
				return actualLevel + restore;
			}

			@Override
			public void extra(Player player) {
				player.getPrayer()
						.restorePrayer((int) (40 + (player.getSkills().getLevelForXp(Skills.PRAYER) * 10) * .08));
			}
		},

		WEAK_GATHERER_POTION(Skills.WOODCUTTING, Skills.MINING, Skills.FISHING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.02));
			}
		},

		WEAK_ARTISTIANS_POTION(Skills.SMITHING, Skills.CRAFTING, Skills.FLETCHING, Skills.CONSTRUCTION,
				Skills.FIREMAKING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.02));
			}
		},

		WEAK_NATURALISTS_POTION(Skills.COOKING, Skills.FARMING, Skills.HERBLORE, Skills.RUNECRAFTING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.02));
			}
		},

		WEAK_SURVIVALISTS_POTION(Skills.AGILITY, Skills.HUNTER, Skills.THIEVING, Skills.SLAYER) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.1));
			}
		},

		REGULAR_MELEE_POTION(Skills.ATTACK, Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.11));
			}
		},

		REGULAR_MAGIC_POTION(Skills.MAGIC) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.11));
			}
		},

		REGULAR_RANGE_POTION(Skills.RANGE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.11));
			}
		},

		REGULAR_DEFENCE_POTION(Skills.DEFENCE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.11));
			}
		},

		REGULAR_STAT_RESTORE_POTION(Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGE,
				Skills.AGILITY, Skills.COOKING, Skills.CRAFTING, Skills.FARMING, Skills.FIREMAKING, Skills.FISHING,
				Skills.FLETCHING, Skills.HERBLORE, Skills.MINING, Skills.RUNECRAFTING, Skills.SLAYER, Skills.SMITHING,
				Skills.THIEVING, Skills.WOODCUTTING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int boost = (int) (realLevel * 0.17) + 7;
				if (actualLevel > realLevel)
					return actualLevel;
				if (actualLevel + boost > realLevel)
					return realLevel;
				return actualLevel + boost;
			}
		},

		REGULAR_STAT_CURE_POTION() {
			@Override
			public void extra(Player player) {
				player.addSuperAntiFire((player.getPerkManager().herbivore ? 2 : 1) * 1000);
				player.addPoisonImmune((player.getPerkManager().herbivore ? 2 : 1) * 1000);
				if (player.getPerkManager().herbivore)
					player.getPackets().sendGameMessage(
							"Your poison immunity and super anti fire immunity time increased by double for having herbivore perk.");
			}
		},

		REGULAR_REJUVINATION_POTION(Skills.SUMMONING, Skills.PRAYER) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int restore = (int) (Math.floor(player.getSkills().getLevelForXp(skillId) * 0.15) + 7);
				if (actualLevel + restore > realLevel)
					return realLevel;
				return actualLevel + restore;
			}

			@Override
			public void extra(Player player) {
				player.getPrayer()
						.restorePrayer((int) (70 + (player.getSkills().getLevelForXp(Skills.PRAYER) * 10) * .15));
			}
		},

		REGULAR_GATHERER_POTION(Skills.WOODCUTTING, Skills.MINING, Skills.FISHING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 4 + (realLevel * 0.04));
			}
		},

		REGULAR_ARTISTIANS_POTION(Skills.SMITHING, Skills.CRAFTING, Skills.FLETCHING, Skills.CONSTRUCTION,
				Skills.FIREMAKING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 4 + (realLevel * 0.04));
			}
		},

		REGULAR_NATURALISTS_POTION(Skills.COOKING, Skills.FARMING, Skills.HERBLORE, Skills.RUNECRAFTING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 4 + (realLevel * 0.04));
			}
		},

		REGULAR_SURVIVALISTS_POTION(Skills.AGILITY, Skills.HUNTER, Skills.THIEVING, Skills.SLAYER) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 4 + (realLevel * 0.04));
			}
		},

		STRONG_MELEE_POTION(Skills.ATTACK, Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.15));
			}
		},

		STRONG_MAGIC_POTION(Skills.MAGIC) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.15));
			}
		},

		STRONG_RANGE_POTION(Skills.RANGE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.15));
			}
		},

		STRONG_DEFENCE_POTION(Skills.DEFENCE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 3 + (realLevel * 0.15));
			}
		},

		STRONG_STAT_RESTORE_POTION(Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.MAGIC, Skills.RANGE,
				Skills.AGILITY, Skills.COOKING, Skills.CRAFTING, Skills.FARMING, Skills.FIREMAKING, Skills.FISHING,
				Skills.FLETCHING, Skills.HERBLORE, Skills.MINING, Skills.RUNECRAFTING, Skills.SLAYER, Skills.SMITHING,
				Skills.THIEVING, Skills.WOODCUTTING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int boost = (int) (realLevel * 0.24) + 10;
				if (actualLevel > realLevel)
					return actualLevel;
				if (actualLevel + boost > realLevel)
					return realLevel;
				return actualLevel + boost;
			}
		},

		STRONG_STAT_CURE_POTION() {
			@Override
			public void extra(Player player) {
				player.addSuperAntiFire((player.getPerkManager().herbivore ? 2 : 1) * 2000);
				player.addPoisonImmune((player.getPerkManager().herbivore ? 2 : 1) * 2000);
				if (player.getPerkManager().herbivore)
					player.getPackets().sendGameMessage(
							"Your poison immunity and super anti fire immunity time increased by double for having herbivore perk.");
			}
		},

		STRONG_REJUVINATION_POTION(Skills.SUMMONING, Skills.PRAYER) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int restore = (int) (Math.floor(player.getSkills().getLevelForXp(skillId) * 0.22) + 10);
				if (actualLevel + restore > realLevel)
					return realLevel;
				return actualLevel + restore;
			}

			@Override
			public void extra(Player player) {
				player.getPrayer()
						.restorePrayer((int) (100 + (player.getSkills().getLevelForXp(Skills.PRAYER) * 10) * 22));
			}
		},

		STRONG_GATHERER_POTION(Skills.WOODCUTTING, Skills.MINING, Skills.FISHING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 6 + (realLevel * 0.06));
			}
		},

		STRONG_ARTISTIANS_POTION(Skills.SMITHING, Skills.CRAFTING, Skills.FLETCHING, Skills.CONSTRUCTION,
				Skills.FIREMAKING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 6 + (realLevel * 0.06));
			}
		},

		STRONG_NATURALISTS_POTION(Skills.COOKING, Skills.FARMING, Skills.HERBLORE, Skills.RUNECRAFTING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 6 + (realLevel * 0.06));
			}
		},

		STRONG_SURVIVALISTS_POTION(Skills.AGILITY, Skills.HUNTER, Skills.THIEVING, Skills.SLAYER) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 6 + (realLevel * 0.06));
			}
		},

		AGILITY_POTION(Skills.AGILITY) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (level + 3);
			}
		},
		COMBAT_POTION(Skills.ATTACK, Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 1 + (realLevel * 0.08));
			}
		},
		CRAFTING_POTION(Skills.CRAFTING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (level + 3);
			}
		},
		HUNTER_POTION(Skills.HUNTER) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (level + 3);
			}
		},
		FLETCHING_POTION(Skills.FLETCHING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (level + 3);
			}
		},

		SUPER_ZAMORAK_BREW(Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE) {

			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.DEFENCE) {
					int newLevel = (int) ((actualLevel * 0.90));
					if (newLevel < 1)
						newLevel = 0;
					return newLevel;
				} else {
					int boost = (int) ((realLevel * 0.12) + 2);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				}
			}

			@Override
			public boolean canDrink(Player player) {
				int reflectedDmg = (int) (player.getHitpoints() * 0.15);
				if (player.getHitpoints() - reflectedDmg < 0) {
					player.getPackets().sendGameMessage(
							"You need more hitpoints in order to survive the effects of the zamorak brew.");
					return false;
				}
				player.applyHit(new Hit(player, reflectedDmg, HitLook.REFLECTED_DAMAGE));
				return true;
			}
		},
		ANTIPOISON_PLUS() {
			@Override
			public void extra(Player player) {
				player.addPoisonImmune((player.getPerkManager().herbivore ? 2 : 1) * 900);
				if (player.getPerkManager().herbivore)
					player.getPackets().sendGameMessage(
							"Your poison immunity time increased by double for having herbivore perk.");
			}
		},
		ANTIPOISON_DOUBLE_PLUS() {
			@Override
			public void extra(Player player) {
				player.addPoisonImmune((player.getPerkManager().herbivore ? 2 : 1) * 1200);
				if (player.getPerkManager().herbivore)
					player.getPackets().sendGameMessage(
							"Your poison immunity time increased by double for having herbivore perk.");
			}
		},
		SUPER_GUTHIX_REST() {
			@Override
			public boolean canDrink(Player player) {
				Long time = (Long) player.getTemporaryAttributtes().get("Recover_Special_Pot");
				if (time != null && Utils.currentTimeMillis() - time < 120000) {
					player.getPackets().sendGameMessage("You may only use this pot once every two minutes.");
					return false;
				}
				return true;
			}

			@Override
			public void extra(Player player) {
				player.getTemporaryAttributtes().put("Recover_Special_Pot", Utils.currentTimeMillis());
				player.getCombatDefinitions().restoreSpecialAttack(25);
				player.setRunEnergy((int) (player.getRunEnergy() * 0.15) + player.getRunEnergy());
				player.heal(500, 500, 0, true);
			}

		},
		GUTHIX_REST() {
			@Override
			public void extra(Player player) {
				player.setRunEnergy((int) (player.getRunEnergy() * 0.05) + player.getRunEnergy());
				player.heal(0, 50, 0, true);
			}

		},
		MAGIC_ESSENCE(Skills.MAGIC) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return level + Utils.random(3, 8);
			}
		},
		SUPER_MAGIC_POTION(Skills.MAGIC) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.12));
			}
		},
		SUPER_RANGE_POTION(Skills.RANGE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				int level = actualLevel > realLevel ? realLevel : actualLevel;
				return (int) (level + 2 + (realLevel * 0.12));
			}
		},
		SUPER_SARADOMIN_BREW("You drink some of the foul liquid.", Skills.ATTACK, Skills.DEFENCE, Skills.STRENGTH,
				Skills.MAGIC, Skills.RANGE) {

			@Override
			public boolean canDrink(Player player) {
				return true;
			}

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.DEFENCE) {
					int boost = (int) (realLevel * 0.08) + 1;
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel * 0.96) - 1;
				}
			}

			@Override
			public void extra(Player player) {
				int hitpointsModification = (int) (player.getMaxHitpoints() * 0.10);
				player.heal(1300, hitpointsModification, 0, true);
			}
		},

		TANKARD("You quaff the beer. You feel slightly reinvigorated... but very dizzy too.", Skills.ATTACK,
				Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.STRENGTH) {
					int boost = (int) (4);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel > 9 && actualLevel != 0 ? actualLevel - 9 : actualLevel >> 9);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(10, 0, 0, true);
			}
		},
		KEG_OF_BEER("You chug the keg. You feel reinvigorated... ...but extremely drunk, too.", Skills.ATTACK,
				Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.STRENGTH) {
					int boost = (int) (10);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel > 40 && actualLevel != 0 ? actualLevel - 40 : actualLevel >> 40);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(20, 0, 0, true);

			}
		},
		MOONLIGHT_MEAD("You drink a glass of Moonlight Mead.") {
			@Override
			public void extra(Player player) {
				player.heal(40, 0, 0, true);
				player.getPackets().sendGameMessage("It tastes like something died in your mouth.");
			}
		},
		GROG("You drink a glass of grog.", Skills.ATTACK, Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.STRENGTH) {
					int boost = (int) (3);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel > 6 && actualLevel != 0 ? actualLevel - 6 : actualLevel >> 6);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(30, 0, 0, true);
			}
		},
		CIDER("You drink a glass of cider.", Skills.ATTACK, Skills.STRENGTH, Skills.FARMING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.FARMING) {
					int boost = (int) (1);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel > 2 && actualLevel != 0 ? actualLevel - 2 : actualLevel >> 2);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(20, 0, 0, true);
			}
		},
		GREENMANS_ALE("You drink a glass of Greenman's Ale.", Skills.ATTACK, Skills.STRENGTH, Skills.HERBLORE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.HERBLORE) {
					int boost = (int) (1);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel > 2 && actualLevel != 0 ? actualLevel - 2 : actualLevel >> 2);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(10, 0, 0, true);
			}
		},
		DWARVEN_STOUT("You drink a glass of Dwarven Stout.", Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE,
				Skills.MINING, Skills.SMITHING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.MINING && skillId == Skills.SMITHING) {
					int boost = (int) (1);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel > 2 && actualLevel != 0 ? actualLevel - 2 : actualLevel >> 2);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(10, 0, 0, true);
			}
		},
		DRAGON_BITTER("You drink a glass of Dragon Bitter.", Skills.ATTACK, Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.STRENGTH) {
					int boost = (int) (2);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel > 1 && actualLevel != 0 ? actualLevel - 1 : actualLevel >> 1);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(10, 0, 0, true);
			}
		},
		RANGERS_AID("You drink a glass of Ranger's Aid.", Skills.RANGE, Skills.DEFENCE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.RANGE) {
					int boost = (int) (2);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel > 5 && actualLevel != 0 ? actualLevel - 5 : actualLevel >> 5);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(10, 0, 0, true);
			}
		},
		SLAYER_RESPITE("You drink a glass of Slayer's Respite.", Skills.ATTACK, Skills.STRENGTH, Skills.SLAYER) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.SLAYER) {
					int boost = (int) (1);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel > 2 && actualLevel != 0 ? actualLevel - 2 : actualLevel >> 2);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(10, 0, 0, true);
			}
		},
		WIZARD_MIND_BOMB("You drink a glass of Wizard's mind bomb.", Skills.ATTACK, Skills.STRENGTH, Skills.MAGIC,
				Skills.DEFENCE) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.MAGIC) {

					int boost = (int) (player.getSkills().getLevelForXp(Skills.MAGIC) >= 50 ? 3 : 2);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else if (skillId == Skills.ATTACK) {
					return (int) (actualLevel > 4 && actualLevel != 0 ? actualLevel - 4 : actualLevel >> 4);
				} else {
					return (int) (actualLevel > 3 && actualLevel != 0 ? actualLevel - 3 : actualLevel >> 3);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(10, 0, 0, true);
			}
		},
		CHEF_DELIGHT("You drink a glass of Chef's Delight", Skills.ATTACK, Skills.STRENGTH, Skills.COOKING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.COOKING) {
					int boost = (int) (realLevel * 0.05);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel * 0.97);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(10, 0, 0, true);
			}
		},
		BANDIT_BREW("You drink a glass of Bandit's brew.", Skills.ATTACK, Skills.STRENGTH, Skills.THIEVING,
				Skills.DEFENCE) {

			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.ATTACK && skillId == Skills.THIEVING) {
					int boost = (int) (1);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else if (skillId == Skills.STRENGTH) {
					return (int) (actualLevel > 1 && actualLevel != 0 ? actualLevel - 1 : actualLevel >> 1);
				} else {
					return (int) (actualLevel > 6 && actualLevel != 0 ? actualLevel - 6 : actualLevel >> 6);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(20, 0, 0, true);
			}
		},
		AXEMANS("You drink a glass of Axeman's Folly", Skills.ATTACK, Skills.STRENGTH, Skills.WOODCUTTING) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.WOODCUTTING) {
					int boost = (int) (1);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel > 3 && actualLevel != 0 ? actualLevel - 3 : actualLevel >> 3);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(10, 0, 0, true);
			}
		},
		ASGARNIAN_ALE("You drink a glass of asgarnian ale.", Skills.ATTACK, Skills.STRENGTH) {
			@Override
			public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
				if (skillId == Skills.STRENGTH) {
					int boost = (int) (2);
					int level = actualLevel > realLevel ? realLevel : actualLevel;
					return level + boost;
				} else {
					return (int) (actualLevel > 4 && actualLevel != 0 ? actualLevel - 4 : actualLevel >> 4);
				}
			}

			@Override
			public void extra(Player player) {
				player.heal(10, 0, 0, true);
			}
		},

		;

		private int[] affectedSkills;
		private String drinkMessage;

		private Effects(int... affectedSkills) {
			this(null, affectedSkills);
		}

		private Effects(String drinkMessage, int... affectedSkills) {
			this.drinkMessage = drinkMessage;
			this.affectedSkills = affectedSkills;
		}

		public int getAffectedSkill(Player player, int skillId, int actualLevel, int realLevel) {
			return actualLevel;
		}

		public boolean canDrink(Player player) {
			// usualy unused
			return true;
		}

		public void extra(Player player) {
			// usualy unused
		}
	}

	private static void startOverLoadAnimation(final Player player) {
		WorldTasksManager.schedule(new WorldTask() {
			int count = 4;

			@Override
			public void run() {
				if (count == 0)
					stop();
				player.setNextAnimation(new Animation(3170));
				player.setNextGraphics(new Graphics(560));
				player.applyHit(new Hit(player, 100, HitLook.REGULAR_DAMAGE, 0));
				count--;
			}
		}, 0, 2);
	}

	public static Pot getPot(int id) {
		for (Pot pot : Pot.values())
			for (int potionId : pot.id) {
				if (id == potionId)
					return pot;
			}
		return null;
	}

	public static int getDoses(Pot pot, Item item) {
		for (int i = pot.id.length - 1; i >= 0; i--) {
			if (pot.id[i] == item.getId())
				return pot.id.length - i;
		}
		return 0;
	}

	public static int mixPot(Player player, Item fromItem, Item toItem, int fromSlot, int toSlot, boolean single) {
		if (single) {
			if (fromItem.getId() == VIAL || toItem.getId() == VIAL) {
				Pot pot = getPot(fromItem.getId() == VIAL ? toItem.getId() : fromItem.getId());
				if (pot == null || pot.isFlask())
					return -1;
				int doses = getDoses(pot, fromItem.getId() == VIAL ? toItem : fromItem);
				if (doses == 1) {
					player.getInventory().switchItem(fromSlot, toSlot);
					if (single)
						player.sendMessage("You pour from one container into the other.", true);
					return 1;
				}
				int vialDoses = doses / 2;
				doses -= vialDoses;
				player.getInventory().getItems().set(fromItem.getId() == VIAL ? toSlot : fromSlot,
						new Item(pot.getIdForDoses(doses), 1));
				player.getInventory().getItems().set(fromItem.getId() == VIAL ? fromSlot : toSlot,
						new Item(pot.getIdForDoses(vialDoses), 1));
				player.getInventory().refresh(fromSlot);
				player.getInventory().refresh(toSlot);
				if (single)
					player.sendMessage("You split the potion between the two vials.", true);
				return 2;
			}
		}
		Pot pot = getPot(fromItem.getId());
		if (pot == null)
			return -1;
		int doses2 = getDoses(pot, toItem);
		if (doses2 == 0 || doses2 == pot.getMaxDoses()) // not same pot type or
			// full already
			return -1;
		int doses1 = getDoses(pot, fromItem);
		doses2 += doses1;
		doses1 = doses2 > pot.getMaxDoses() ? doses2 - pot.getMaxDoses() : 0;
		doses2 -= doses1;
		if (doses1 == 0 && pot.isFlask())
			player.getInventory().deleteItem(fromSlot, fromItem);
		else {
			player.getInventory().getItems().set(fromSlot, new Item(doses1 > 0 ? pot.getIdForDoses(doses1) : VIAL, 1));
			player.getInventory().refresh(fromSlot);
		}
		player.getInventory().getItems().set(toSlot, new Item(pot.getIdForDoses(doses2), 1));
		player.getInventory().refresh(toSlot);
		if (single)
			player.sendMessage("You pour from one container into the other"
					+ (pot.isFlask() && doses1 == 0 ? " and the glass shatters to pieces." : "."));
		return 3;
	}

	public static boolean emptyPot(Player player, Item item, int slot) {
		Pot pot = getPot(item.getId());
		if (pot == null || pot.isFlask())
			return false;
		item.setId(VIAL);
		player.getInventory().refresh(slot);
		player.sendMessage("You empty the vial.", true);
		return true;
	}

	public static boolean pot(Player player, Item item, int slot) {
		Pot pot = getPot(item.getId());
		if (pot == null)
			return false;
		if (player.getPotDelay() > Utils.currentTimeMillis())
			return true;
		if (!player.getControlerManager().canPot(pot))
			return true;
		if (!pot.effect.canDrink(player))
			return true;
		player.addPotDelay(1075);
		pot.effect.extra(player);
		int dosesLeft = getDoses(pot, item) - 1;
		if (dosesLeft == 0 && pot.isFlask())
			player.getInventory().deleteItem(slot, item);
		else {
			player.getInventory().getItems().set(slot,
					new Item(dosesLeft > 0 ? pot.getIdForDoses(dosesLeft)
							: item.getDefinitions().isDungeoneeringItem() ? DUNGEONEERING_VIAL
									: pot.isPotion() ? VIAL : getReplacedId(pot),
							1));
			player.getInventory().refresh(slot);
		}
		for (int skillId : pot.effect.affectedSkills)
			player.getSkills().set(skillId, pot.effect.getAffectedSkill(player, skillId,
					player.getSkills().getLevel(skillId), player.getSkills().getLevelForXp(skillId)));
		player.setNextAnimationForce(new Animation(player.isUnderCombat() ? 18003 : 18000));
		player.getPackets().sendSound(4580, 0, 1);
		if (pot.isFlask() || pot.isPotion()) {
			player.sendMessage(pot.effect.drinkMessage != null ? pot.effect.drinkMessage
					: "You drink some of your "
							+ item.getDefinitions().getName().toLowerCase().replace(" (1)", "").replace(" (2)", "")
									.replace(" (3)", "").replace(" (4)", "").replace(" (5)", "").replace(" (6)", "")
							+ ".",
					true);
			player.sendMessage(dosesLeft == 0
					? "You have finished your " + (pot.isFlask() ? "flask and the glass shatters to pieces."
							: pot.isPotion() ? "potion." : item.getName().toLowerCase() + ".")
					: "You have " + dosesLeft + " dose of potion left.", true);
		}
		return true;
	}

	@SuppressWarnings("incomplete-switch")
	private static int getReplacedId(Pot drink) {
		switch (drink) {
		case JUG:
			return 1935;
		case BEER:
		case TANKARD:
		case GREENMANS_ALE:
		case AXEMANS_ALE:
		case SLAYER_RESPITE:
		case RANGERS_AID:
		case MOONLIGHT_MEAD:
		case DRAGON_BITTER:
		case ASGARNIAN_ALE:
		case CHEF_DELIGHT:
		case CHEF_DELIGHT_KEG:
		case CIDER:
		case CIDER_KEG:
		case WIZARD_MIND_BOMB:
		case DWARVEN_STOUT:
		case GROG:
		case BANDIT_BREW:
			return 1919;
		case GREENMANS_ALE_KEG:
		case AXEMANS_ALE_KEG:
		case SLAYER_RESPITE_KEG:
		case MOONLIGHT_MEAD_KEG:
		case DRAGON_BITTER_KEG:
		case ASGARNIAN_ALE_KEG:
		case DWARVEN_STOUT_KEG:
		case KEG_OF_BEER:
			return 10885;
		}
		return 0;
	}

	public static void resetOverLoadEffect(Player player) {
		if (!player.isDead()) {
			int actualLevel = player.getSkills().getLevel(Skills.ATTACK);
			int realLevel = player.getSkills().getLevelForXp(Skills.ATTACK);
			if (actualLevel > realLevel)
				player.getSkills().set(Skills.ATTACK, realLevel);
			actualLevel = player.getSkills().getLevel(Skills.STRENGTH);
			realLevel = player.getSkills().getLevelForXp(Skills.STRENGTH);
			if (actualLevel > realLevel)
				player.getSkills().set(Skills.STRENGTH, realLevel);
			actualLevel = player.getSkills().getLevel(Skills.DEFENCE);
			realLevel = player.getSkills().getLevelForXp(Skills.DEFENCE);
			if (actualLevel > realLevel)
				player.getSkills().set(Skills.DEFENCE, realLevel);
			actualLevel = player.getSkills().getLevel(Skills.MAGIC);
			realLevel = player.getSkills().getLevelForXp(Skills.MAGIC);
			if (actualLevel > realLevel)
				player.getSkills().set(Skills.MAGIC, realLevel);
			actualLevel = player.getSkills().getLevel(Skills.RANGE);
			realLevel = player.getSkills().getLevelForXp(Skills.RANGE);
			if (actualLevel > realLevel)
				player.getSkills().set(Skills.RANGE, realLevel);
			player.heal(500);
		}
		player.setOverloadDelay(0);
		player.getPackets()
				.sendGameMessage("<col=480000>The effects of overload have worn off and you feel normal again.");
	}

	public static void applyOverLoadEffect(Player player) {
		if (player.getControlerManager().getControler() instanceof Wilderness
				|| player.getControlerManager().getControler() instanceof CrucibleController
				|| FfaZone.isOverloadChanged(player)) {
			int actualLevel = player.getSkills().getLevel(Skills.ATTACK);
			int realLevel = player.getSkills().getLevelForXp(Skills.ATTACK);
			int level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.ATTACK, (int) (level + 5 + (realLevel * 0.15)));

			actualLevel = player.getSkills().getLevel(Skills.STRENGTH);
			realLevel = player.getSkills().getLevelForXp(Skills.STRENGTH);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.STRENGTH, (int) (level + 5 + (realLevel * 0.15)));

			actualLevel = player.getSkills().getLevel(Skills.DEFENCE);
			realLevel = player.getSkills().getLevelForXp(Skills.DEFENCE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.DEFENCE, (int) (level + 5 + (realLevel * 0.15)));

			actualLevel = player.getSkills().getLevel(Skills.MAGIC);
			realLevel = player.getSkills().getLevelForXp(Skills.MAGIC);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.MAGIC, level + 5);

			actualLevel = player.getSkills().getLevel(Skills.RANGE);
			realLevel = player.getSkills().getLevelForXp(Skills.RANGE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.RANGE, (int) (level + 5 + (realLevel * 0.1)));
		} else {
			int actualLevel = player.getSkills().getLevel(Skills.ATTACK);
			int realLevel = player.getSkills().getLevelForXp(Skills.ATTACK);
			int level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.ATTACK, (int) (level + 5 + (realLevel * 0.22)));

			actualLevel = player.getSkills().getLevel(Skills.STRENGTH);
			realLevel = player.getSkills().getLevelForXp(Skills.STRENGTH);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.STRENGTH, (int) (level + 5 + (realLevel * 0.22)));

			actualLevel = player.getSkills().getLevel(Skills.DEFENCE);
			realLevel = player.getSkills().getLevelForXp(Skills.DEFENCE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.DEFENCE, (int) (level + 5 + (realLevel * 0.22)));

			actualLevel = player.getSkills().getLevel(Skills.MAGIC);
			realLevel = player.getSkills().getLevelForXp(Skills.MAGIC);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.MAGIC, level + 7);

			actualLevel = player.getSkills().getLevel(Skills.RANGE);
			realLevel = player.getSkills().getLevelForXp(Skills.RANGE);
			level = actualLevel > realLevel ? realLevel : actualLevel;
			player.getSkills().set(Skills.RANGE, (int) (level + 4 + (Math.floor(realLevel / 5.2))));
		}
	}

	public static void decantPotsInv(Player player) {
		int count = 0;
		outLoop: for (int fromSlot = 0; fromSlot < 28; fromSlot++) {
			Item fromItem = player.getInventory().getItem(fromSlot);
			if (fromItem == null)
				continue outLoop;
			innerLoop: for (int toSlot = 0; toSlot < 28; toSlot++) {
				Item toItem = player.getInventory().getItem(toSlot);
				if (toItem == null || fromSlot == toSlot)
					continue innerLoop;
				if (mixPot(player, fromItem, toItem, fromSlot, toSlot, false) != -1) {
					count++;
					break innerLoop;
				}
			}
		}

		if (count != 0) {
			for (Item item : player.getInventory().getItems().getItems()) {
				if (item == null || item.getId() != VIAL)
					continue;
				player.getInventory().deleteItem(item);
				player.getInventory().getItems().shift();
				player.getInventory().addItem(item);
			}
			player.getInventory().refresh();
		}
	}
}
