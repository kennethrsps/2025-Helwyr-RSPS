package com.rs.game.player.content;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.player.Player;

public class ItemCombining {
	
	private static final int barrows = 33294, third = 33298, shadows = 33296, ice = 41887, blood = 36274;
	private static final int rapier = 26579, ohrapier = 26583, mace = 26595, ohmace = 26599, longsword = 26587, ohlongsword = 26591;
	private static final int ascension = 28441, ohascension = 28437, wand = 28617, singularity = 28621;
	private static final int longbow = 31733, scythe = 31725, staff = 31729;
	private static final int mask = 29854, hauberk = 29857, chaps = 29860;
	private static final int helm = 30005, cuirass = 30008, greaves = 30011;
	private static final int elitemask = 43155, elitehauberk = 43158, elitechaps = 43161;
	private static final int elitetmask = 43166, eliterobetop = 43169, eliterobebottom = 43172;
	private static final int tmask = 28608, robetop = 28611, robebottom = 28614;
	private static final int cloth = 3188;
	private static final int white = 9913, red = 9914, blue = 9915, green = 9916, yellow = 9917, ywhip = 15441, bwhip = 15442, wwhip = 15443, gwhip = 15444;
	private static final int statp = 13884, statl = 13890, stath = 13896, statw = 13902, awep = 39047;   
	private static final int robin = 2581, whip = 4151, ranger = 2577, dark = 11235, sol = 15486, gnome = 9470, vine = 21371;
	private static final String add = "You add a new colour to your item.", remove = "You remove the colour from your item, returning it to its original state.";
	
	public enum Combinings { 
		
		Lava_whip(34151, 4151, 34150, ""),
		Staff_of_darkness(34153, 15486, 34155, ""),
		Strykebow(34156, 11235, 34158, ""),
		
		AURORA(40668, 18351, 28388, "You've successfully made a Exquisite longsword!"),
		AURORA_OFF(40668, 25993, 28390, "You've successfully made a Off-hand Exquisite longsword!"),
		EXQUISTE_CB(40668, 18357, 28416, "You've successfully made a Exquisite Crossbow!"),
		EXQUISTE_OFF_CB(40668, 25995, 28418, "You've successfully made a Off-hand Exquisite Crossbow!"),
		Khopesh(40668, 40312, 40655, "You've successfully made a Khopesh of Tumeken!"),
		khopesh_OFF(40668, 40316, 40659, "You've successfully made a Khopesh of Elidinis!"),
		
		KhopeshICE(ice, 40655, 42091,"" ),
		Khopeshbarrow(barrows, 40655, 42524,"" ),
		Khopeshshadow(shadows, 40655, 42527,"" ),
		Khopesh3age(third, 40655, 42530,"" ),
		Khopeshblood(36274, 40655, 42533,"" ),
		
		KhopeshICEoff(ice, 40659, 42094,"" ),
		Khopeshbarrowoff(barrows, 40659, 42536,"" ),
		Khopeshshadowoff(shadows, 40659, 42539,"" ),
		Khopesh3ageoff(third, 40659, 42542,"" ),
		Khopeshbloodoff(36274, 40659, 42545,"" ),
		
		DEATH_SCYTHE(39501, scythe, 39559,""),
		//DEATH_BOW(39501, longbow, 42946,""),
		//DEATH_STAFF(39501, staff, 42945,""),
		
		Zaros_Godsword(37614, 37626, 37640, "This weapon was build by the gods!"),
		Seren_Godbow(37614, 37622, 37632, "This weapon was build by the gods!"),
		Staff_of_Sliske(37614, 37624, 37636, "This weapon was build by the gods!"),
		
		RAPTOR_KEY(36001, 36012, 36066, "You made a RAPTOR key"),
		
		//Defenders ----- Temporary ------ until Real method fix! 
		KALPHITE_REBOUNCER(28621, 36163, 36173, "You created a Kalphite rebounder"),
		ANCIENT_LANTERN(25664, 36159, 36171, "You created a Ancient Lantern"),
		BLIGHTED_REBOUNDER(25672, 36156, 36168, "You created a Blighed rebounder"), 
		TAINTED_REPRISER(4734, 36156, 36176, "You created a Tainted repriser"),
		ANCIENT_REPRISER(20171, 36159, 36179, "You created a Tainted repriser"),
		KALPHITE_REPRISER(28441, 36163, 36181, "You created a Kalphite repriser"),
		KALPHITE_REPRISERR(28443, 36163, 36181, "You created a Kalphite repriser"),
		CORRUPTED_DEFENDERD(4718, 36156, 36153, "You created a Corrupted defender"),
		CORRUPTED_DEFENDERT(4747, 36156, 36153, "You created a Corrupted defender"),
		CORRUPTED_DEFENDERV(4755, 36156, 36153, "You created a Corrupted defender"),
		CORRUPTED_DEFENDERG(4726, 36156, 36153, "You created a Corrupted defender"),
		ANCIENT_DEFENDER(36164, 36159, 36157, "You created a Ancient defender"),
		KALPHITE_DEFENDERR(26583, 36163, 36160, "You created a Kalphite defender"),
		KALPHITE_DEFENDERL(26591, 36163, 36160, "You created a Kalphite defender"),
		KALPHITE_DEFENDERM(26599, 36163, 36160, "You created a Kalphite defender"),
		
		Superior_Statius_platebody(statp, awep, 39099, "you feel the power of the ancient warriors"),
		Superior_Statius_platelegs(statl, awep, 39107, "you feel the power of the ancient warriors"),
		Superior_Statius_full_helm(stath, awep, 39115, "you feel the power of the ancient warriors"),
		Superior_Statius_warhammer(statw, awep, 39117, "you feel the power of the ancient warriors"),
		Superior_Vesta_plateskirt(13893, awep, 39111, "you feel the power of the ancient warriors"),
		Superior_Vesta_chainbody(13887, awep, 39103, "you feel the power of the ancient warriors"),
		Superior_Vesta_longsword(13899, awep, 39121, "you feel the power of the ancient warriors"),
		Superior_Vesta_spear(13905, awep, 39125, "you feel the power of the ancient warriors"),
		Superior_Zuriel_robe_top(13858, awep, 39085, "you feel the power of the ancient warriors"),
		Superior_Zuriel_robe_bottom(13861, awep, 39089, "you feel the power of the ancient warriors"),
		Superior_Zuriel_hood(13864, awep, 39093, "you feel the power of the ancient warriors"),
		Superior_Zuriel_staff(13867, awep, 39095, "you feel the power of the ancient warriors"),
		Superior_Morrigan_leather_body(13870, awep, 39129, "you feel the power of the ancient warriors"),
		Superior_Morrigan_leather_chaps(13873, awep, 39133, "you feel the power of the ancient warriors"),
		Superior_Morrigan_coif(13876, awep, 39137, "you feel the power of the ancient warriors"),
		
		BARROW_GODSWORD(barrows, 37640, 40701, ""),
		BARROW_GODBOW(barrows, 37632, 40713, ""),
		BARROW_STAFF2(barrows, 37636, 40689, ""),
		
		
		SHADOW_GODSWORD(shadows, 37640, 40704, ""),
		SHADOW_GODBOW(shadows, 37632, 40716, ""),
		SHADOW_STAFF2(shadows, 37636, 40692, ""),
		
		THIRD_GODSWORD(third, 37640, 40707, ""),
		THIRD_GODBOW(third, 37632, 40719, ""),
		THIRD_STAFF2(third, 37636, 40695, ""),
		
		BLOOD_GODSWORD(36274, 37640, 40698, ""),
		BLOOD_GODBOW(36274, 37632, 40710, ""),
		BLOOD_STAFF2(36274, 37636, 40686, ""),
		
		ICE_GODSWORD(ice, 37640, 42067, ""),
		ICE_GODBOW(ice, 37632, 42070, ""),
		ICE_STAFF2(ice, 37636, 42064, ""),
		
				
		BARROW_RAPIER(barrows, rapier, 33306, ""),
		BARROW_OH_RAPIER(barrows, ohrapier, 33309, ""),
		BARROW_MACE(barrows, mace, 33300, ""),
		BARROW_OH_MACE(barrows, ohmace, 33303, ""),
		BARROW_LONGSWORD(barrows, longsword, 33312, ""),
		BARROW_OH_LONGSWORD(barrows, ohlongsword, 33315, ""),
		
		
		SHADOW_RAPIER(shadows, rapier, 33372, ""),
		SHADOW_OH_RAPIER(shadows, ohrapier, 33375, ""),
		SHADOW_MACE(shadows, mace, 33366, ""),
		SHADOW_OH_MACE(shadows, ohmace, 33369, ""),
		SHADOW_LONGSWORD(shadows, longsword, 33378, ""),
		SHADOW_OH_LONGSWORD(shadows, ohlongsword, 33381, ""),
		
		THIRD_RAPIER(third, rapier, 33438, ""),
		THIRD_OH_RAPIER(third, ohrapier, 33441, ""),
		THIRD_MACE(third, mace, 33432, ""),
		THIRD_OH_MACE(third, ohmace, 33435, ""),
		THIRD_LONGSWORD(third, longsword, 33444, ""),
		THIRD_OH_LONGSWORD(third, ohlongsword, 33447, ""),
		
		ICE_RAPIER(ice, rapier, 42034, ""),
		ICE_OH_RAPIER(ice, ohrapier, 42037, ""),
		ICE_MACE(ice, mace, 42028, ""),
		ICE_OH_MACE(ice, ohmace, 42031, ""),
		ICE_LONGSWORD(ice, longsword, 42022, ""),
		ICE_OH_LONGSWORD(ice, ohlongsword, 42025, ""),
		
		BARROW_ASCENSION(barrows, ascension, 33318, ""),
		BARROW_OH_ASCENSION(barrows, ohascension, 33321, ""),
		SHADOW_ASCENSION(shadows, ascension, 33384, ""),
		SHADOW_OH_ASCENSION(shadows, ohascension, 33387, ""),
		THIRD_ASCENSION(third, ascension, 33450, ""),
		THIRD_OH_ASCENSION(third, ohascension, 33453, ""),
		ICE_ASCENSION(ice, ascension, 42016, ""),
		ICE_OH_ASCENSION(ice, ohascension, 42019, ""),
		
		BARROW_WAND(barrows, wand, 33324, ""),
		BARROW_SINGULARITY(barrows, singularity, 33327, ""),
		SHADOW_WAND(shadows, wand, 33390, ""),
		SHADOW_SINGULARITY(shadows, singularity, 33393, ""),
		THIRD_WAND(third, wand, 33456, ""),
		THIRD_SINGULARITY(third, singularity, 33459, ""),
		ICE_WAND(ice, wand, 42058, ""),
		ICE_SINGULARITY(ice, singularity, 42061, ""),
		
		BARROW_LONG(barrows, longbow, 33336, ""),
		SHADOW_LONG(shadows, longbow, 33402, ""),
		THIRD_LONG(third, longbow, 33468, ""),
		ICE_LONG(ice, longbow, 42055, ""),
		
		BARROW_SCYTHE(barrows, scythe, 33330, ""),
		SHADOW_SCYTHE(shadows, scythe, 33396, ""),
		THIRD_SCYTHE(third, scythe, 33462, ""),
		ICE_SCYTHE(ice, scythe, 42049, ""),
		
		BARROWW_STAFF(barrows, staff, 33333, ""),
		SHADOW_STAFF(shadows, staff, 33399, ""),
		THIRD_STAFF(third, staff, 33465, ""),
		ICE_STAFF(ice, staff, 42052, ""),
		
		BARROW_HELM(barrows, helm, 33357, ""),
		BARROW_CUIRASS(barrows, cuirass, 33360, ""),
		BARROW_GREAVES(barrows, greaves, 33363, ""),
		SHADOW_HELM(shadows, helm, 33423, ""),
		SHADOW_CUIRASS(shadows, cuirass, 33426, ""),
		SHADOW_GREAVES(shadows, greaves, 33429, ""),
		THIRD_HELM(third, helm, 33489, ""),
		THIRD_CUIRASS(third, cuirass, 33492, ""),
		THIRD_GREAVES(third, greaves, 33495, ""),
		ICE_HELM(ice, helm, 42040, ""),
		ICE_CUIRASS(ice, cuirass, 42043, ""),
		ICE_GREAVES(ice, greaves, 42046, ""),
		
		BARROW_TMASK(barrows, tmask, 33339, ""),
		BARROW_ROBETOP(barrows, robetop, 33342, ""),
		BARROW_ROBEBOTTOM(barrows, robebottom, 33345, ""),
		SHADOW_TMASK(shadows, tmask, 33405, ""),
		SHADOW_ROBETOP(shadows, robetop, 33408, ""),
		SHADOW_ROBEBOTTOM(shadows, robebottom, 33411, ""),
		THIRD_TMASK(third, tmask, 33471, ""),
		THIRD_ROBETOP(third, robetop, 33474, ""),
		THIRD_ROBEBOTTOM(third, robebottom, 33477, ""),
		ICE_TMASK(ice, tmask, 42082, ""),
		ICE_ROBETOP(ice, robetop, 42085, ""),
		ICE_ROBEBOTTOM(ice, robebottom, 42088, ""),
		
		
		BARROW_MASK(barrows, mask, 33348, ""),
		BARROW_HAUBERK(barrows, hauberk, 33351, ""),
		BARROW_CHAPS(barrows, chaps, 33354, ""),
		SHADOW_MASK(shadows, mask, 33414, ""),
		SHADOW_HAUBERK(shadows, hauberk, 33417, ""),
		SHADOW_CHAPS(shadows, chaps, 33420, ""),
		THIRD_MASK(third, mask, 33480, ""),
		THIRD_HAUBERK(third, hauberk, 33483, ""),
		THIRD_CHAPS(third, chaps, 33486, ""),
		ICE_MASK(ice, mask, 42073, ""),
		ICE_HAUBERK(ice, hauberk, 42076, ""),
		ICE_CHAPS(ice, chaps, 42079, ""),
		
		
		//elite tectonic
		eliteblood_TMASK(blood, elitetmask, 42964, ""),
		eliteblood_ROBETOP(blood, eliterobetop, 42967, ""),
		eliteblood_ROBEBOTTOM(blood, eliterobebottom, 42970, ""),
		
		eliteBARROW_TMASK(barrows, elitetmask, 42973, ""),
		eliteBARROW_ROBETOP(barrows, eliterobetop, 42976, ""),
		eliteBARROW_ROBEBOTTOM(barrows, eliterobebottom, 42979, ""),
		
		eliteSHADOW_TMASK(shadows, elitetmask, 42991, ""),
		eliteSHADOW_ROBETOP(shadows, eliterobetop, 42994, ""),
		eliteSHADOW_ROBEBOTTOM(shadows, eliterobebottom, 42997, ""),
		
		eliteTHIRD_TMASK(third, elitetmask, 43009, ""),
		eliteTHIRD_ROBETOP(third, eliterobetop, 43012, ""),
		eliteTHIRD_ROBEBOTTOM(third, eliterobebottom, 43015, ""),
		
		eliteICE_TMASK(ice, elitetmask, 43036, ""),
		eliteICE_ROBETOP(ice, eliterobetop, 43039, ""),
		eliteICE_ROBEBOTTOM(ice, eliterobebottom, 43042, ""),
		
		//sirenic elite
		eliteblood_MASK(blood, elitemask, 42955, ""),
		eliteblood_HAUBERK(blood, elitehauberk, 42958, ""),
		eliteblood_CHAPS(blood, elitechaps, 42961, ""),
		
		eliteBARROW_MASK(barrows, elitemask, 42982, ""),
		eliteBARROW_HAUBERK(barrows, elitehauberk, 42985, ""),
		eliteBARROW_CHAPS(barrows, elitechaps, 42988, ""),
		
		eliteSHADOW_MASK(shadows, elitemask, 43000, ""),
		eliteSHADOW_HAUBERK(shadows, elitehauberk, 43003, ""),
		eliteSHADOW_CHAPS(shadows, elitechaps, 43006, ""),
		
		eliteTHIRD_MASK(third, elitemask, 43018, ""),
		eliteTHIRD_HAUBERK(third, elitehauberk, 43021, ""),
		eliteTHIRD_CHAPS(third, elitechaps, 43024, ""),
		
		eliteICE_MASK(ice, elitemask, 43027, ""),
		eliteICE_HAUBERK(ice, elitehauberk, 43030, ""),
		eliteICE_CHAPS(ice, elitechaps, 43033, ""),
		
		
		
		YELLOW_ROBIN(yellow, robin, 20950, add),
		RED_ROBIN(red, robin, 20949, add),
		BLUE_ROBIN(blue, robin, 20951, add),
		WHITE_ROBIN(white, robin, 20952, add),
		
		YELLOW_RANGERS(yellow, ranger, 22558, add),
		RED_RANGER(red, ranger, 22552, add),
		BLUE_RANGER(blue, ranger, 22554, add),
		WHITE_RANGER(white, ranger, 22556, add),
		
		YELLOW_WHIP(yellow, whip, 15441, add),
		BLUE_WHIP(blue, whip, 15442, add),
		WHITE_WHIP(white, whip, 15443, add),
		GREEN_WHIP(green, whip, 15444, add),
		
		YELLOW_WHIP_VINE(ywhip, vine, 21372, add),
		BLUE_WHIP_VINE(bwhip, vine, 21373, add),
		WHITE_WHIP_VINE(wwhip, vine, 21374, add),
		GREEN_WHIP_VINE(gwhip, vine, 21375, add),
		
		YELLOW_DARKBOW(yellow, dark, 15701, add),
		BLUE_DARKBOW(blue, dark, 15702, add),
		WHITE_DARKBOW(white, dark, 15703, add),
		GREEN_DARKBOW(green, dark, 15704, add),
		
		RED_SOL(red, sol, 22207, add),
		YELLOW_SOL(yellow, sol, 22209, add),
		BLUE_SOL(blue, sol, 22211, add),
		GREEN_SOL(green, sol, 22213, add),
		
		RED_GNOME(red, gnome, 22215, add),
		YELLOW_GNOME(yellow, gnome, 22216, add),
		BLUE_GNOME(blue, gnome, 22217, add),
		GREEN_GNOME(green, gnome, 22217, add),
		
		//STEADFAST(21787, 34972, 34978, "You combined the 2 items."),
		//RAGEFIRE(21793, 34976, 34984, "You combined the 2 items."),
		//GLAIVEN(21790, 34974, 34981, "You combined the 2 items."),
		
		HOODEN_MAX(20768, 20767, 32151, "You created a hooded cape."),
		HOODEN_COMP(20770, 20769, 32152, "You created a hooded cape."),
		HOODEN_COMPT(20772, 20771, 32153,"You created a hooded cape."),
		
		
		BLOOD_FURRY(6585, 32692, 32703, "You created a Blood furry."),
		BLOOD_KNOCKOUT(31449, 32692, 32700,add),
		BLOOD_ARCANE(18335, 32692, 32694,add),
		BLOOD_FARSIGHT(31445, 32692, 32697,add),
		/**
		 * chaotic remnant
		 */
		BARWLER_KNOCKOUT(6585, 32692, 31449, add),
		FARSIGHT_SNIPER(25034, 32692, 31445, add),
		ARCANE_STREAM(25031,32692,18335,add),
		
		SOULS_FURRY(25028, 31449, 31875, "You created a Soul amulet, this amulet will get you increased soulsplit healing."),
		
		
		;
		
		private int dye, item, product;
		private String message;
		
		/**
		 * Used to obtain the value based off of original item and dye
		 */
		public static Combinings forItem(int item, int dye) {
			for (Combinings product : Combinings.values()) {
				if (product.item == item && product.dye == dye || product.item == dye && product.dye == item) {
					return product;
				}
			}
			return null;
		}
		
		/**
		 * Used to obtain the value based off of the product
		 */
		public static Combinings forProduct(int item) {
			for (Combinings product : Combinings.values()) {
				if (product.product == item) {
					return product;
				}
			}
			return null;
		}
		
		public int getId() {
			return item;
		}
		
		public int getDye() {
			return dye;
		}
		
		public int getProduct() {
			return product;
		}
		
		public String getMessage() {
			return message;
		}
		
		private Combinings(int dye, int item, int product, String message) {
			this.dye = dye;
			this.item = item;
			this.product = product;
			this.message = message;
		}
	}
	
	public static boolean CombineItems(Player player, int used, int usedWith) {
		if (!player.getInventory().containsItem(used, 1) || !player.getInventory().containsItem(usedWith, 1)) {
			return false;
		}
		player.getInventory().deleteItem(used, 1);
		player.getInventory().deleteItem(usedWith, 1);
		player.getInventory().addItem(Combinings.forItem(used, usedWith).getProduct(), 1);
		if (Combinings.forItem(used,  usedWith).getMessage().equals(""))
			player.getPackets().sendGameMessage("You carefully coat your "
					+(ItemDefinitions.getItemDefinitions(Combinings.forItem(used, usedWith).getId()).getName().toLowerCase())+
					" with the "+(ItemDefinitions.getItemDefinitions(Combinings.forItem(used, usedWith).getDye()).getName().toLowerCase())+".", true);
		else
			player.getPackets().sendGameMessage(Combinings.forItem(used, usedWith).getMessage(), true);
		return true;
	}
	
	public static boolean RemoveDyeFromItems(Player player, int used, int usedWith) {
		if (!player.getInventory().containsItem(used, 1) || !player.getInventory().containsItem(usedWith, 1)) {
			return false;
		}
		if (used != cloth && usedWith != cloth)
			return false;
		if (used == cloth)
			player.getInventory().deleteItem(usedWith, 1);
		else
			player.getInventory().deleteItem(used, 1);
		player.getInventory().addItem(Combinings.forProduct(used).getId(), 1);
		if (Combinings.forProduct(used).getMessage().equals(""))
			player.getPackets().sendGameMessage("You carefully remove the dye from your "+(ItemDefinitions.getItemDefinitions(Combinings.forProduct(used).getId()).getName().toLowerCase())+".", true);
		else
			player.getPackets().sendGameMessage(remove, true);
		return true;
	}
	
	
	
}
