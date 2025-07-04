package com.rs.game.player.content.summoning.defs;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.item.Item;

/**
 * Represents all of the summoning scrolls.
 * 
 * @author Byte Me
 */

public class SummoningScrolls {

public enum SummoningScroll {
	
	HOWL_SCROLL(12425, 1, 0.1, new Item(12047)),
	DREADFOWL_STRIKE_SCROLL(12445, 4, 0.1, new Item(12043)),
	FETCH_CASKET_SCROLL(19621, 4, 0.0, new Item(-1)),
	EGG_SPAWN_SCROLL(12428, 4, 10.2, new Item(12059)),
	SLIME_SPRAY_SCROLL(12459, 13, 0.2,new Item(12019)),
	STONY_SHELL_SCROLL(12533, 16, 0.2, new Item(12009)),
	PESTER_SCROLL(12838, 17, 0.5, new Item(12778)),
	ELECTRIC_LASH_SCROLL(12460, 18, 0.4, new Item(12049)),
	VENOM_SHOT_SCROLL(12432, 19, 0.9, new Item(12055)),
	FIREBALL_ASSAULT_SCROLL(12839, 22, 1.1, new Item(12808)),
	CHEESE_FEAST_SCROLL(12430, 23, 2.3, new Item(12067)),
	SANDSTORM_SCROLL(12446, 25, 2.5, new Item(12064)),
	GENERATE_COMPOST_SCROLL(12440, 28, 0.6, new Item(12091)),
	EXPLODE_SCROLL(12834, 29, 2.9, new Item(12800)),
	VAMPIRE_TOUCH_SCROLL(12447, 31, 1.5, new Item(12053)),
	INSANE_FEROCITY_SCROLL(12433, 32, 1.6, new Item(12065)),
	MULTICHOP_SCROLL(12429, 33, 0.7, new Item(12021)),
	CALL_TO_ARMS_SCROLL(12443, 34, 0.7, new Item(12818)), // Need
	BRONZE_BULL_RUSH_SCROLL(12461, 36, 3.6, new Item(12073)),
	UNBURDEN_SCROLL(12431, 40, 0.6, new Item(12087)),
	HERBCALL_SCROLL(12422, 41, 0.8, new Item(12071)),
	EVIL_FLAMES_SCROLL(12448, 42, 2.1, new Item(12051)),
	PETRIFYING_GAZE_SCROLL(12458, 43,0.9, new Item(12095)),
	// TODO need to add 12097, 12099, 12101, 12103, 12105, 12107
	IRON_BULL_RUSH_SCROLL(12462, 46, 4.6, new Item(12075)),
	IMMENSE_HEAT_SCROLL(12829, 46, 2.3, new Item(12816)),
	THIEVING_FINGERS_SCROLL(12426, 47, 0.9, new Item(12041)),
	BLOOD_DRAIN_SCROLL(12444, 49, 2.4, new Item(12061)),
	TIRELESS_RUN_SCROLL(12441, 52, 0.8, new Item(12007)),
	ABYSSAL_DRAIN_SCROLL(12454, 54, 1.1, new Item(12035)),
	DISSOLVE_SCROLL(12453, 55, 5.5, new Item(12027)),
	FISH_RAIN_SCROLL(12424, 56, 1.1, new Item(12531)),
	STEEL_BULL_RUSH_SCROLL(12463, 56, 5.6,new Item(12077)),
	AMBUSH_SCROLL(12836, 57, 5.7, new Item(12812)),
	RENDING_SCROLL(12840, 57, 5.7, new Item(12784)),
	GOAD_SCROLL(12835, 57, 5.7, new Item(12710)),
	DOOMSPHERE_SCROLL(12455, 58, 5.8, new Item(12023)),
	DUST_CLOUD_SCROLL(12468,61, 3.1, new Item(12085)),
	ABYSSAL_STEALTH_SCROLL(12427, 62, 1.9, new Item(12037)),
	OPHIDIAN_INCUBATION_SCROLL(12436, 63, 3.2, new Item(12015)),
	POISONOUS_BLAST_SCROLL(12467, 64, 3.2, new Item(12045)),
	MITHRIL_BULL_RUSH_SCROLL(12464, 66,6.6, new Item(12079)),
	TOAD_BARK_SCROLL(12452, 66, 1.0, new Item(12123)),
	TESTUDO_SCROLL(12439, 67, 0.7, new Item(12031)),
	SWALLOW_WHOLE_SCROLL(12438, 68, 1.4, new Item(12029)),
	FRUITFALL_SCROLL(12423, 69, 1.4, new Item(12033)),
	FAMINE_SCROLL(12830, 70, 1.4, new Item(12820)),
	ARCTIC_BLAST_SCROLL(12451, 71, 1.1, new Item(12057)),
	RISE_FROM_THE_ASHES_SCROLL(14622, 72, 8.0, new Item(14623)),
	VOLCANIC_STRENGTH_SCROLL(12826, 73, 7.3, new Item(12792)),
	CRUSHING_CLAW_SCROLL(12449,74, 3.7, new Item(12069)), 
	MANTIS_STRIKE_SCROLL(12450, 75, 3.7, new Item(12011)),
	INFERNO_SCROLL(12841, 76, 1.5, new Item(12782)),
	ADAMANT_BULL_RUSH_SCROLL(12465, 76, 7.6, new Item(12081)),
	DEADLY_CLAW_SCROLL(12831, 77, 11.4, new Item(12794)),
	ACORN_MISSILE_SCROLL(12457, 78, 1.6, new Item(12013)),
	TITANS_CONSTITUTION_SCROLL(12824, 79, 7.9, new Item(12802)),
	// TODO add 12806 and 12804
	REGROWTH_SCROLL(12442, 80, 1.6, new Item(12025)),
	SPIKE_SHOT_SCROLL(12456, 83, 4.1, new Item(12017)),
	EBON_THUNDER_SCROLL(12837, 83, 8.3, new Item(12788)),
	SWAMP_PLAGUE_SCROLL(12832, 85, 4.1, new Item(12776)),
	RUNE_BULL_RUSH_SCROLL(12466, 86,8.6, new Item(12083)),
	HEALING_AURA_SCROLL(12434, 88, 1.8, new Item(12039)), 
	BOIL_SCROLL(12833, 89, 8.9, new Item(12786)),
	MAGIC_FOCUS_SCROLL(12437, 92, 4.6, new Item(12089)),
	ESSENCE_SHIPMENT_SCROLL(12827, 93, 1.9, new Item(12796)),
	IRON_WITHIN_SCROLL(12828, 95, 4.7, new Item(12822)),
	WINTER_STORAGE_SCROLL(12435, 96, 4.8, new Item(12093)),
	STEEL_OF_LEGENDS_SCROLL(12825, 99, 4.9, new Item(12790)),
	SIPHON_SELF(31332, 81, 1.6, new Item(31328)),
	RING_OF_FIRE(34146, 82, 3.9, new Item(34137)),
	ANNIHILATE(31380, 87, 1.6, new Item(31410)),
	ENLIGHTENMENT(32832, 88, 8.7, new Item(32829)),
	MAMMOTH_FEAST(36056, 99, 5, new Item(36060)),
	
	/**
	 * Dungeoneering scrolls..
	 * itemId, level, xp, pouch
	 */
	SUNDERING_STRIKE_T1(18027, 1, 0.1, new Item(17935)),
	SUNDERING_STRIKE_T2(18028, 11, 0.1, new Item(17936)),
	SUNDERING_STRIKE_T3(18029, 21, 0.1, new Item(17937)),
	SUNDERING_STRIKE_T4(18030, 31, 0.1, new Item(17938)),
	SUNDERING_STRIKE_T5(18031, 41, 0.1, new Item(17939)),
	SUNDERING_STRIKE_T6(18032, 51, 0.1, new Item(17940)),
	SUNDERING_STRIKE_T7(18033, 61, 0.1, new Item(17941)),
	SUNDERING_STRIKE_T8(18034, 71, 0.1, new Item(17942)),
	SUNDERING_STRIKE_T9(18035, 81, 0.1, new Item(17943)),
	SUNDERING_STRIKE_T10(18036, 91, 0.1, new Item(17944)),
	POISONOUS_SHOT_T1(18037, 2, 0.1, new Item(17985)),
	POISONOUS_SHOT_T2(18038, 12, 0.1, new Item(17986)),
	POISONOUS_SHOT_T3(18039, 22, 0.1, new Item(17987)),
	POISONOUS_SHOT_T4(18040, 32, 0.1, new Item(17988)),
	POISONOUS_SHOT_T5(18041, 42, 0.1, new Item(17989)),
	POISONOUS_SHOT_T6(18042, 52, 0.1, new Item(17990)),
	POISONOUS_SHOT_T7(18043, 62, 0.1, new Item(17991)),
	POISONOUS_SHOT_T8(18044, 72, 0.1, new Item(17992)),
	POISONOUS_SHOT_T9(18045, 82, 0.1, new Item(17993)),
	POISONOUS_SHOT_T10(18046, 92, 0.1, new Item(17994)),
	SNARING_WAVE_T1(18047, 3, 0.1, new Item(17945)),
	SNARING_WAVE_T2(18048, 13, 0.1, new Item(17946)),
	SNARING_WAVE_T3(18049, 23, 0.1, new Item(17947)),
	SNARING_WAVE_T4(18050, 33, 0.1, new Item(17948)),
	SNARING_WAVE_T5(18051, 43, 0.1, new Item(17949)),
	SNARING_WAVE_T6(18052, 53, 0.1, new Item(17950)),
	SNARING_WAVE_T7(18053, 63, 0.1, new Item(17951)),
	SNARING_WAVE_T8(18054, 73, 0.1, new Item(17952)),
	SNARING_WAVE_T9(18055, 83, 0.1, new Item(17953)),
	SNARING_WAVE_T10(18056, 93, 0.1, new Item(17954)),
	APTITUDE_T1(18057, 5, 0.1, new Item(17955)),
	APTITUDE_T2(18058, 15, 0.1, new Item(17956)),
	APTITUDE_T3(18059, 25, 0.1, new Item(17957)),
	APTITUDE_T4(18060, 35, 0.1, new Item(17958)),
	APTITUDE_T5(18061, 45, 0.1, new Item(17959)),
	APTITUDE_T6(18062, 55, 0.1, new Item(17960)),
	APTITUDE_T7(18063, 65, 0.1, new Item(17961)),
	APTITUDE_T8(18064, 75, 0.1, new Item(17962)),
	APTITUDE_T9(18065, 85, 0.1, new Item(17963)),
	APTITUDE_T10(18066, 95, 0.1, new Item(17964)),
	SECOND_WIND_T1(18067, 7, 0.1, new Item(17975)),
	SECOND_WIND_T2(18068, 17, 0.1, new Item(17976)),
	SECOND_WIND_T3(18069, 27, 0.1, new Item(17977)),
	SECOND_WIND_T4(18070, 37, 0.1, new Item(17978)),
	SECOND_WIND_T5(18071, 47, 0.1, new Item(17979)),
	SECOND_WIND_T6(18072, 57, 0.1, new Item(17980)),
	SECOND_WIND_T7(18073, 67, 0.1, new Item(17981)),
	SECOND_WIND_T8(18074, 77, 0.1, new Item(17982)),
	SECOND_WIND_T9(18075, 87, 0.1, new Item(17983)),
	SECOND_WIND_T10(18076, 97, 0.1, new Item(17984)),
	GLIMMER_OF_LIGHT_T1(18077, 9, 0.1, new Item(17965)),
	GLIMMER_OF_LIGHT_T2(18078, 19, 0.1, new Item(17966)),
	GLIMMER_OF_LIGHT_T3(18079, 29, 0.1, new Item(17967)),
	GLIMMER_OF_LIGHT_T4(18080, 39, 0.1, new Item(17968)),
	GLIMMER_OF_LIGHT_T5(18081, 49, 0.1, new Item(17969)),
	GLIMMER_OF_LIGHT_T6(18082, 59, 0.1, new Item(17970)),
	GLIMMER_OF_LIGHT_T7(18083, 69, 0.1, new Item(17971)),
	GLIMMER_OF_LIGHT_T8(18084, 79, 0.1, new Item(17972)),
	GLIMMER_OF_LIGHT_T9(18085, 89, 0.1, new Item(17973)),
	GLIMMER_OF_LIGHT_T10(18086, 99, 0.1, new Item(17974));

	public static SummoningScroll get(int itemId) {
		return SCROLLS.get(itemId);
	}

	private static final Map<Integer, SummoningScroll> SCROLLS = new HashMap<Integer, SummoningScroll>();

	static {
		for (SummoningScroll scroll : SummoningScroll.values()) {
			SCROLLS.put(scroll.itemId, scroll);
		}
	}

	private final int itemId, levelRequired;

	private final double experience;

	private final Item pouch;

	private SummoningScroll(int itemId, int levelRequired, double experience, Item pouch) {
		this.itemId = itemId;
		this.levelRequired = levelRequired;
		this.experience = experience;
		this.pouch = pouch;
	}

	public int getItemId() {
		return itemId;
	}

	public int getLevelRequired() {
		return levelRequired;
	}

	public double getExperience() {
		return experience;
	}

	public Item getPouch() {
		return pouch;
	}

}
}
