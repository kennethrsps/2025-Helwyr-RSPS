package com.rs.game.player.content;

import java.util.HashMap;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.item.Item;
import com.rs.game.player.Player;
import com.rs.game.player.QuestManager.Quests;
import com.rs.game.player.content.pet.Pets;
import com.rs.game.player.Skills;
import com.rs.game.player.TreasureTrails;
import com.rs.game.player.dialogue.impl.CompCape;

public class ItemConstants {

	public static boolean canWear(Item item, Player player) {
		if (player.isDeveloper())
			return true;
		if (item.getName().toLowerCase().contains("ahrim")) {
			if (player.getSkills().getLevel(Skills.MAGIC) < 70) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a magic level of 70.");
				return false;
			}
		}
		if (item.getName().toLowerCase().contains("achto")) {
			if (player.getSkills().getLevel(Skills.DEFENCE) < 90) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a defence level of 90.");
				return false;
			}
		}
		// Veteran cape
		// if (item.getName().toLowerCase().contains("veteran cape")) {
		// if (player.getTotalPlayTime() + (online ? getRecordedPlayTime() : 500000){
		// //getTimePlayed().getTimePlayed(online) != 500000) { //
		// player.sendMessage("You have not played anought to use this item.");
		// player.sendMessage("You need to have 500h of playtime to use this item.");
		// return false;
		//
		// }
		// }
		//
		if (item.getName().toLowerCase().contains("raptor")) {
			if (player.getSkills().getLevel(Skills.DEFENCE) < 88) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a defence level of 88.");
				return false;
			}
		}
		if (item.getName().toLowerCase().contains("hellion")) {
			if (player.getSkills().getLevel(Skills.DEFENCE) < 90) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a defence level of 90.");
				return false;
			}
		}
		if (item.getName().toLowerCase().contains("exquisite crossbow")) {
			if (player.getSkills().getLevel(Skills.RANGE) < 92) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a Range level of 92.");
				return false;
			}
		}
		if (item.getName().toLowerCase().contains("exquisite longsword")) {
			if (player.getSkills().getLevel(Skills.ATTACK) < 92) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a Attack level of 92.");
				return false;
			}
		}
		if (item.getName().toLowerCase().contains("dragoon")) {
			if (player.getSkills().getLevel(Skills.DEFENCE) < 92) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a defence level of 92.");
				return false;
			}
		}
		if (item.getName().toLowerCase().contains("Fallen nihil")) {
			if (player.getSkills().getLevel(Skills.RANGE) < 92) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a Range level of 92.");
				return false;
			}
		}
		if (item.getName().toLowerCase().contains("Menaphite")) {
			if (player.getSkills().getLevel(Skills.MAGIC) < 92) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a Magic level of 92.");
				return false;
			}
		}
		if (item.getId() >= 29185 && item.getId() <= 29188 || item.getId() == 34292 || item.getId() == 34293) {
			if (player.getSkills().getLevel(Skills.DIVINATION) < 99) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a level of 99 Divination to equip this.");
				return false;
			}
		}
		if (item.getId() >= 36351 && item.getId() <= 36355) {
			if (player.getSkills().getLevel(Skills.INVENTION) < 99) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a level of 99 Invention to equip this.");
				return false;
			}
		}
		if (item.getId() == 15241) {
			if (player.getSkills().getLevel(Skills.RANGE) < 75 && player.getSkills().getLevel(Skills.FIREMAKING) < 61) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have a level of 75 Ranged and 61 Firemaking to equip this.");
				return false;
			}
		}
		if (item.getId() >= 18349 && item.getId() <= 18363) {
			if (player.getSkills().getLevel(Skills.DUNGEONEERING) < 80) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have at least level 80 Dungeoneering to equip this.");
				return false;
			}
		}
		if (item.getId() >= 25996 && item.getId() <= 25991) {
			if (player.getSkills().getLevel(Skills.DUNGEONEERING) < 80) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have at least level 80 Dungeoneering to equip this.");
				return false;
			}
		}
		if (item.getName().contains("Fire cape")) {
			if (!player.isCompletedFightCaves()) {
				player.sendMessage("You have to complete the Fight Caves minigame to wear this.");
				return false;
			}
		}
		if (item.getName().contains("TokHaar")) {
			if (!player.isCompletedFightKiln()) {
				player.sendMessage("You have to complete the Fight Kiln minigame to wear this.");
				return false;
			}
		}
		if (item.getId() == 20769 || item.getId() == 20770 || item.getId() == 32152) {
			if (!CompCape.isWorthyCompCape(player)) {
				player.sendMessage("You are not worthy enough to wear this cape.");
				return false;
			}
		}
		if (item.getId() == 20771 || item.getId() == 20772 || item.getId() == 32153) {
			if (!CompCape.isWorthyCompCapeT(player)) {
				player.sendMessage("You are not worthy enough to wear this cape.");
				return false;
			}
		}
		if (item.getId() == 4708 || item.getId() == 4710 || item.getId() == 4712 || item.getId() == 4714) {
			if (player.getSkills().getLevel(Skills.DEFENCE) < 70) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 70 Defence to wear this item.");
				return false;
			}
			if (player.getSkills().getLevel(Skills.MAGIC) < 70) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 70 Magic to wear this item.");
				return false;
			}
		}
		if (item.getId() == 36356) {
			if (player.getSkills().getXp(Skills.INVENTION) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31284) {
			if (player.getSkills().getXp(Skills.DIVINATION) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31277) {
			if (player.getSkills().getXp(Skills.AGILITY) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31268) {
			if (player.getSkills().getXp(Skills.ATTACK) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31275) {
			if (player.getSkills().getXp(Skills.CONSTRUCTION) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31288) {
			if (player.getSkills().getXp(Skills.COOKING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31280) {
			if (player.getSkills().getXp(Skills.CRAFTING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31270) {
			if (player.getSkills().getXp(Skills.DEFENCE) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31291) {
			if (player.getSkills().getXp(Skills.FARMING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31289) {
			if (player.getSkills().getXp(Skills.FIREMAKING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31287) {
			if (player.getSkills().getXp(Skills.FISHING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31281) {
			if (player.getSkills().getXp(Skills.FLETCHING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31278) {
			if (player.getSkills().getXp(Skills.HERBLORE) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31276) {
			if (player.getSkills().getXp(Skills.HITPOINTS) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31283) {
			if (player.getSkills().getXp(Skills.HUNTER) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31273) {
			if (player.getSkills().getXp(Skills.MAGIC) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31285) {
			if (player.getSkills().getXp(Skills.MINING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31272) {
			if (player.getSkills().getXp(Skills.PRAYER) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31271) {
			if (player.getSkills().getXp(Skills.RANGE) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31274) {
			if (player.getSkills().getXp(Skills.RUNECRAFTING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31282) {
			if (player.getSkills().getXp(Skills.SLAYER) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31286) {
			if (player.getSkills().getXp(Skills.SMITHING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31269) {
			if (player.getSkills().getXp(Skills.STRENGTH) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31292) {
			if (player.getSkills().getXp(Skills.SUMMONING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31279) {
			if (player.getSkills().getXp(Skills.THIEVING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 31290) {
			if (player.getSkills().getXp(Skills.WOODCUTTING) < 104273167) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				return false;
			}
		}
		if (item.getId() == 13661) {
			if (player.getSkills().getLevel(Skills.FIREMAKING) < 92) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need a Firemaking of level 92 to equip this item.");
				return false;
			}
		}

		if (item.getId() == 13659) {
			if (player.getSkills().getLevel(Skills.FIREMAKING) < 62) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need a Firemaking of level 62 to equip this item.");
				return false;
			}
		}
		if (item.getId() == 13660) {
			if (player.getSkills().getLevel(Skills.FIREMAKING) < 79) {
				player.sendMessage("You are not high enough level to use this item.");
				player.sendMessage("You need a Firemaking of level 79 to equip this item.");
				return false;
			}
		} else if (item.getId() == 14642 || item.getId() == 14645 || item.getId() == 15433 || item.getId() == 15435
				|| item.getId() == 14641 || item.getId() == 15432 || item.getId() == 15434) {
			if (!player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM)) {
				player.sendMessage("You need to have completed Nomad's Requiem mini-quest to use this cape.");
				return false;
			}
		}
		return true;
	}

	public static int getDegradeItemWhenWear(int id) {
		// Pvp armors
		if (id == 13958 || id == 13961 || id == 13964 || id == 13967 || id == 13970 || id == 13973 || id == 13908
				|| id == 13911 || id == 13914 || id == 13917 || id == 13920 || id == 13923 || id == 13941 || id == 13944
				|| id == 13947 || id == 13950 || id == 13958 || id == 13938 || id == 13926 || id == 13929 || id == 13932
				|| id == 13935)
			return id + 2;
		if (id == 29854 || id == 29857 || id == 29860) // Regular Sirenic
			return id + 2;
		if (id == 33348 || id == 33351 || id == 33354) // Barrows Sirenic
			return id + 1;
		if (id == 33414 || id == 33417 || id == 33420) // Shadow Sirenic
			return id + 1;
		if (id == 33480 || id == 33483 || id == 33486) // Third-Age Sirenic
			return id + 1;
		if (id == 36285 || id == 36288 || id == 36291) // Blood Sirenic
			return id + 1;
		if (id == 31203) // Ascension grips
			return id + 2;
		if (id >= 26346 && id <= 26348) // Death lotus
			return id + 3;
		if (id >= 26325 && id <= 26327) // Tetsu
			return id + 3;
		if (id >= 26337 && id <= 26339) // Seasinger
			return id + 3;
		return -1;
	}

	public static int getItemDefaultCharges(int id) {
		if (id == 13910 || id == 13913 || id == 13916 || id == 13919 || id == 13922 || id == 13925 || id == 13928
				|| id == 13931 || id == 13934 || id == 13937 || id == 13940 || id == 13943 || id == 13946 || id == 13949
				|| id == 13952 || id == 36344 || id == 36345 || id == 36346 || id == 39041 || id == 39042 || id == 39043
				|| id == 39291 || id == 39292 || id == 39293)
			return 30000;
		if (id == 13960 || id == 13963 || id == 13966 || id == 13969 || id == 13972 || id == 13975)
			return 30000;
		if (id == 13889 || id == 13895 || id == 13901 || id == 13913 || id == 13886 || id == 13892 || id == 13898
				|| id == 13904 || id == 13860 || id == 13863 || id == 13866 || id == 13869 || id == 13872 || id == 13875
				|| id == 13878 || id == 18349 || id == 18351 || id == 18353 || id == 18355 || id == 18357 || id == 18359
				|| id == 18361 || id == 18363 || id == 24854 || id == 24857 || id == 24860)
			return 60000;
		if (id == 4880 || id == 4881 || id == 4882 || id == 4883 // dh helm
				|| id == 4886 || id == 4887 || id == 4888 || id == 4889 // dh
																		// axe
				|| id == 4898 || id == 4899 || id == 4900 || id == 4901 // dh
																		// legs
				|| id == 4892 || id == 4893 || id == 4894 || id == 4895 // dh
																		// body
				|| id == 4856 || id == 4857 || id == 4858 || id == 4859 // ahrim
																		// hood
				|| id == 4862 || id == 4863 || id == 4864 || id == 4865 // ahrim
																		// staff
				|| id == 4868 || id == 4869 || id == 4870 || id == 4871 // ahrim
																		// top
				|| id == 4874 || id == 4875 || id == 4876 || id == 4877 // ahrim
																		// skirt
				|| id == 4904 || id == 4905 || id == 4906 || id == 4907 // guthan
																		// helm
				|| id == 4910 || id == 4911 || id == 4912 || id == 4913 // guthan
																		// spear
				|| id == 4916 || id == 4917 || id == 4918 || id == 4920 // guthan
																		// plate
				|| id == 4922 || id == 4923 || id == 4924 || id == 4925 // guthan
																		// skirt
				|| id == 4928 || id == 4929 || id == 4930 || id == 4931 // karil
																		// coif
				|| id == 4934 || id == 4935 || id == 4936 || id == 4937 // karil
																		// x-bow
				|| id == 4940 || id == 4941 || id == 4942 || id == 4943 // karil
																		// top
				|| id == 4946 || id == 4947 || id == 4948 || id == 4949 // karil
																		// skirt
				|| id == 4958 || id == 4959 || id == 4960 || id == 4961 // torag
																		// hammer
				|| id == 4964 || id == 4965 || id == 4966 || id == 4967 // torag
																		// plate
				|| id == 4970 || id == 4971 || id == 4972 || id == 4973 // torag
																		// legs
				|| id == 4952 || id == 4953 || id == 4954 || id == 4955 // torag
																		// helm
				|| id == 4976 || id == 4977 || id == 4978 || id == 4979 // verac
																		// helm
				|| id == 4982 || id == 4983 || id == 4984 || id == 4985 // verac
																		// flail
				|| id == 4988 || id == 4989 || id == 4990 || id == 4991 // verac
																		// brassard
				|| id == 4994 || id == 4995 || id == 4996 || id == 4997 // verac
																		// skirt
				|| id == 21738 || id == 21739 || id == 21740 || id == 21741 // akrisae
																			// hood
				|| id == 21746 || id == 21747 || id == 21748 || id == 21749 // akrisae
																			// mace
				|| id == 21754 || id == 21755 || id == 21756 || id == 21757 // akrisae
																			// top
				|| id == 21762 || id == 21763 || id == 21764 || id == 21765) // akrisae
																				// skirt
			return 36500;
		if (id == 20135 || id == 20139 || id == 20143 || id == 20147 || id == 20151 || id == 20155 || id == 20159
				|| id == 20163 || id == 20167)
			return 1;
		if (id == 20171)// zaryte
			return 60000;
		if (id == 34978 || id == 34981 || id == 34984) // T90 glacor boots
			return 1;
		if (id == 34980 || id == 34983 || id == 34986) // T90 glacor boots
			return 60000;
		if (id == 33879 || id == 33886 || id == 33889) // Port weapons
			return 1;
		if (id == 33880 || id == 33887 || id == 33890) // Port weapons
			return 30000;
		if (id == 20137 || id == 20141 || id == 20145 || id == 20149 || id == 20153 || id == 20157 || id == 20161
				|| id == 20165 || id == 20169 || id == 20173)
			return 90000;
		if (id == 29856 || id == 29859 || id == 29862) // Regular Sirenic
			return 100000;
		if (id == 33349 || id == 33352 || id == 33355) // Barrows Sirenic
			return 100000;
		if (id == 33415 || id == 33418 || id == 33421) // Shadow Sirenic
			return 100000;
		if (id == 33481 || id == 33484 || id == 33487) // Third-Age Sirenic
			return 100000;
		if (id == 36286 || id == 36289 || id == 36292) // Blood Sirenic
			return 100000;
		if (id == 31205) // Ascension grips
			return 100000;
		if (id == 28439 || id == 33319 || id == 33385 || id == 33451 || id == 36322) // Ascension
																						// crossbow
			return 60000;
		if (id == 26581 || id == 33307 || id == 33373 || id == 33439 || id == 36310) // Drygore
																						// rapiers
			return 60000;
		if (id == 26589 || id == 33313 || id == 33379 || id == 33445 || id == 36316) // Drygore
																						// longswords
			return 60000;
		if (id == 26597 || id == 33301 || id == 33367 || id == 33433 || id == 36304) // Drygore
																						// maces
			return 60000;
		if (id == 30013 || id == 30005 || id == 30008) // malevolent armour
			return 60000;
		if (id == 32627 || id == 32629 || id == 32631 || id == 32647 // attuned
																		// items
				|| id == 32649 || id == 32653 || id == 32655 || id == 32659 || id == 31661 || id == 32663)
			return 50000;
		if (id == 31727 || id == 31731 || id == 31735) // Regular Noxious
			return 60000;
		if (id == 33331 || id == 33334 || id == 33337) // Barrows Noxious
			return 60000;
		if (id == 33397 || id == 33400 || id == 33403) // Shadow Noxious
			return 60000;
		if (id == 33463 || id == 33466 || id == 33469) // Third-Age Noxious
			return 60000;
		if (id == 36334 || id == 36340 || id == 36337) // Blood Noxious
			return 60000;
		if (id == 11283 || id == 25558 || id == 25561) // dfs
			return 100;
		if (id == 34980 || id == 34983 || id == 34986) // T90 glacor boots
			return 60000;
		if (id >= 26349 && id <= 26351) // Death lotus
			return 60000;
		if (id >= 26352 && id <= 26354) // Superior Death lotus
			return 72000;
		if (id >= 26328 && id <= 26330) // Tetsu
			return 60000;
		if (id >= 26322 && id <= 26324) // Superior Tetsu
			return 72000;
		if (id >= 26340 && id <= 26342) // Seasinger
			return 60000;
		if (id >= 26334 && id <= 26336) // Superior Seasinger
			return 72000;
		if (id >= 35159 && id <= 35197) // ACHTO Gear from Tusken invasion
			return 60000;
		return -1;
	}

	public static int getItemDegrade(int id) {
		if (id == 20137 || id == 20141 || id == 20145 || id == 20149 || id == 20153 || id == 20157 || id == 20161
				|| id == 20165 || id == 20169 || id == 20173)
			return id + 1;
		if (id == 20135 || id == 20139 || id == 20143 || id == 20147 || id == 20151 || id == 20155 || id == 20159
				|| id == 20163 || id == 20167 || id == 20171)
			return id + 2;
		if (id == 33879 || id == 33886 || id == 33889) // Port weapons
			return id + 1;
		if (id == 33880 || id == 33887 || id == 33890) // Port weapons
			return id + 1;
		if (id == 34978 || id == 34981 || id == 34984) // T90 glacor boots
			return id + 2;
		if (id == 34980 || id == 34983 || id == 34986) // T90 glacor boots
			return id - 13193;
		if (id == 4880 || id == 4881 || id == 4882 || id == 4883 // dh helm
				|| id == 4886 || id == 4887 || id == 4888 || id == 4889 // dh
																		// axe
				|| id == 4898 || id == 4899 || id == 4900 || id == 4901 // dh
																		// legs
				|| id == 4892 || id == 4893 || id == 4894 || id == 4895 // dh
																		// body
				|| id == 4856 || id == 4857 || id == 4858 || id == 4859 // ahrim
																		// hood
				|| id == 4862 || id == 4863 || id == 4864 || id == 4865 // ahrim
																		// staff
				|| id == 4868 || id == 4869 || id == 4870 || id == 4871 // ahrim
																		// top
				|| id == 4874 || id == 4875 || id == 4876 || id == 4877 // ahrim
																		// skirt
				|| id == 4904 || id == 4905 || id == 4906 || id == 4907 // guthan
																		// helm
				|| id == 4910 || id == 4911 || id == 4912 || id == 4913 // guthan
																		// spear
				|| id == 4916 || id == 4917 || id == 4918 || id == 4920 // guthan
																		// plate
				|| id == 4922 || id == 4923 || id == 4924 || id == 4925 // guthan
																		// skirt
				|| id == 4928 || id == 4929 || id == 4930 || id == 4931 // karil
																		// coif
				|| id == 4934 || id == 4935 || id == 4936 || id == 4937 // karil
																		// x-bow
				|| id == 4940 || id == 4941 || id == 4942 || id == 4943 // karil
																		// top
				|| id == 4946 || id == 4947 || id == 4948 || id == 4949 // karil
																		// skirt
				|| id == 4958 || id == 4959 || id == 4960 || id == 4961 // torag
																		// hammer
				|| id == 4964 || id == 4965 || id == 4966 || id == 4967 // torag
																		// plate
				|| id == 4970 || id == 4971 || id == 4972 || id == 4973 // torag
																		// legs
				|| id == 4952 || id == 4953 || id == 4954 || id == 4955 // torag
																		// helm
				|| id == 4976 || id == 4977 || id == 4978 || id == 4979 // verac
																		// helm
				|| id == 4982 || id == 4983 || id == 4984 || id == 4985 // verac
																		// flail
				|| id == 4988 || id == 4989 || id == 4990 || id == 4991 // verac
																		// brassard
				|| id == 4994 || id == 4995 || id == 4996 || id == 4997 // verac
																		// skirt
				|| id == 21738 || id == 21739 || id == 21740 || id == 21741 // akrisae
																			// hood
				|| id == 21746 || id == 21747 || id == 21748 || id == 21749 // akrisae
																			// mace
				|| id == 21754 || id == 21755 || id == 21756 || id == 21757 // akrisae
																			// top
				|| id == 21762 || id == 21763 || id == 21764 || id == 21765 // akrisae
																			// skirt
				|| id == 32627 || id == 32629 || id == 32631 || id == 32647 // attuned
																			// items
				|| id == 32649 || id == 32653 || id == 32655 || id == 32659 || id == 31661 || id == 32663)
			return id + 1;
		if (id == 18349 || id == 18351 || id == 18353 || id == 18355 || id == 18357 || id == 18359 || id == 18361
				|| id == 18363)
			return id + 1;
		if (id == 33349 || id == 33352 || id == 33355) // Sirenic barrows
			return id + 1;
		if (id == 33415 || id == 33418 || id == 33421) // Sirenic shadow
			return id + 1;
		if (id == 33481 || id == 33484 || id == 33487) // Sirenic third-age
			return id + 1;
		if (id == 36286 || id == 36289 || id == 36292) // Sirenic blood
			return id + 1;
		if (id == 28439 || id == 33319 || id == 33385 || id == 33451 || id == 36322) // Ascension
																						// crossbows
			return id + 1;
		if (id == 26581 || id == 33307 || id == 33373 || id == 33439 || id == 36310) // Drygore
																						// rapiers
			return id + 1;
		if (id == 26589 || id == 33313 || id == 33379 || id == 33445 || id == 36316) // Drygore
																						// longswords
			return id + 1;
		if (id == 26597 || id == 33301 || id == 33367 || id == 33433 || id == 36304) // Drygore
																						// maces
			return id + 1;
		if (id == 30008 || id == 30005 || id == 30013) // Malevolent armor
			return -1;
		if (id == 31727 || id == 31731 || id == 31735 || id == 33331 || id == 33334 || id == 33337 || id == 33397
				|| id == 33400 || id == 33403 || id == 33463 || id == 33466 || id == 36340 || id == 36337
				|| id == 36334) // Noxious
			return id + 1;
		if (id == 32627 || id == 32629 || id == 32631 || id == 32647 // attuned
																		// items
				|| id == 32649 || id == 32653 || id == 32655 || id == 32659 || id == 31661 || id == 32663)
			return id + 1;
		switch (id) {
		case 26322:
			return 26331;// superior tetsu helm
		case 26323:
			return 26332;// superioer tetsu body
		case 26324:
			return 26333;// superior tetsy bottom
		}
		return -1;
	}

	public static int getDegradeItemWhenCombating(int id) {
		if (id == 13858 || id == 13861 || id == 13864 || id == 13867 || id == 13870 || id == 13873 || id == 13876
				|| id == 13884 || id == 13887 || id == 13890 || id == 13893 || id == 13896 || id == 13905 || id == 13902
				|| id == 13899 || id == 21736 || id == 21744 || id == 21752 || id == 21760) // akrisae
			return id + 2;
		if (id == 4708)
			return id + 148;
		if (id == 4710)
			return id + 152;
		if (id == 4712)
			return id + 156;
		if (id == 4714)
			return id + 160;
		if (id == 4716)
			return id + 164;
		if (id == 4718)
			return id + 168;
		if (id == 4720)
			return id + 172;
		if (id == 4722)
			return id + 176;
		if (id == 4724)
			return id + 180;
		if (id == 4726)
			return id + 184;
		if (id == 4728)
			return id + 188;
		if (id == 4730)
			return id + 192;
		if (id == 4732)
			return id + 196;
		if (id == 4734)
			return id + 200;
		if (id == 4736)
			return id + 204;
		if (id == 4738)
			return id + 208;
		if (id == 4745)
			return id + 207;
		if (id == 4747)
			return id + 211;
		if (id == 4749)
			return id + 215;
		if (id == 4751)
			return id + 219;
		if (id == 4753)
			return id + 223;
		if (id == 4755)
			return id + 227;
		if (id == 4757)
			return id + 231;
		if (id == 4759)
			return id + 235;
		if (id == 22458 || id == 22462 || id == 22466) // fungal
			return id + 2;
		if (id == 22470 || id == 22474 || id == 22478) // grifolic
			return id + 2;
		if (id == 22482 || id == 22486 || id == 22490) // ganodermic
			return id + 2;
		if (id == 22494)
			return id + 2;

		if (id == 28437) // Ascension crossbow
			return id + 2;
		if (id == 33318) // Ascension crossbow Barrows
			return id + 1;
		if (id == 33384) // Ascension crossbow Shadow
			return id + 1;
		if (id == 33450) // Ascension crossbow Third-Age
			return id + 1;
		if (id == 36321) // Ascension crossbow Blood
			return id + 1;
		if (id == 26579 || id == 33306 || id == 33372 || id == 33438 || id == 36309) // Drygore
																						// rapiers
			return id + (id == 26579 ? 2 : 1);
		if (id == 26587 || id == 33312 || id == 33378 || id == 33444 || id == 36315) // Drygore
																						// longswords
			return id + (id == 26587 ? 2 : 1);
		if (id == 26595 || id == 33300 || id == 33366 || id == 33432 || id == 36303) // Drygore
																						// maces
			return id + (id == 26595 ? 2 : 1);
		if (id == 31725 || id == 31729 || id == 31733) // Regular noxious
			return id + 2;
		if (id == 33330 || id == 33333 || id == 33336) // Barrows noxious
			return id + 1;
		if (id == 33396 || id == 33399 || id == 33402) // Shadow noxious
			return id + 1;
		if (id == 33462 || id == 33465 || id == 33468) // Third-Age noxious
			return id + 1;
		if (id == 36339 || id == 36336 || id == 36333) // Blood noxious
			return id + 1;
		if (id == 32627 || id == 32629 || id == 32631 || id == 32647 // attuned
																		// items
				|| id == 32649 || id == 32653 || id == 32655 || id == 32659 || id == 31661 || id == 32663)
			return id + 1;
		return -1;
	}

	public static int getDegradeItemOnDeath(int id) {
		String name = ItemDefinitions.getItemDefinitions(id).getName().toLowerCase();
		if (name.contains("akrisae's hood"))
			return 21742;
		if (name.contains("akrisae's war mace"))
			return 21750;
		if (name.contains("akrisae's robe top"))
			return 21758;
		if (name.contains("akrisae's robe skirt"))
			return 21766;
		if (name.contains("dharok's helm"))
			return 4884;
		if (name.contains("dharok's greataxe"))
			return 4890;
		if (name.contains("dharok's platebody"))
			return 4896;
		if (name.contains("dharok's platelegs"))
			return 4902;
		if (name.contains("verac's helm"))
			return 4980;
		if (name.contains("verac's flail"))
			return 4986;
		if (name.contains("verac's brassard"))
			return 4992;
		if (name.contains("verac's plateskirt"))
			return 4998;
		if (name.contains("torag's helm"))
			return 4956;
		if (name.contains("torag's hammers"))
			return 4962;
		if (name.contains("torag's platebody"))
			return 4968;
		if (name.contains("torag's platelegs"))
			return 4974;
		if (name.contains("karil's coif"))
			return 4932;
		if (name.contains("karil's crossbow"))
			return 4938;
		if (name.contains("karil's top"))
			return 4944;
		if (name.contains("karil's skirt"))
			return 4950;
		if (name.contains("guthan's helm"))
			return 4908;
		if (name.contains("guthan's warspear"))
			return 4914;
		if (name.contains("guthan's platebody"))
			return 4920;
		if (name.contains("guthan's chainskirt"))
			return 4926;
		if (name.contains("ahrim's hood"))
			return 4860;
		if (name.contains("ahrim's staff"))
			return 4866;
		if (name.contains("ahrim's robe top"))
			return 4872;
		if (name.contains("ahrim's robe skirt"))
			return 4878;
		if (name.contains("torva full helm"))
			return 20138;
		if (name.contains("torva platebody"))
			return 20142;
		if (name.contains("torva platelegs"))
			return 20146;
		if (name.contains("pernix cowl"))
			return 20150;
		if (name.contains("pernix body"))
			return 20154;
		if (name.contains("pernix chaps"))
			return 20158;
		if (name.contains("virtus mask"))
			return 20162;
		if (name.contains("virtus robe top"))
			return 20166;
		if (name.contains("virtus robe legs"))
			return 20170;
		if (name.contains("zaryte bow"))
			return 20174;
		return -1;
	}

	public static int getItemDustOnDeath(int id) {
		String name = ItemDefinitions.getItemDefinitions(id).getName().toLowerCase();
		if (name.contains("statius's"))
			return 592;
		if (name.contains("vesta's"))
			return 592;
		if (name.contains("morrigan's"))
			return 592;
		if (name.contains("zuriel's"))
			return 592;
		return -1;
	}

	public static boolean itemDegradesWhileWearing(int id) {
		String name = ItemDefinitions.getItemDefinitions(id).getName().toLowerCase();
		if (name.contains("c. dragon") || name.contains("corrupt dragon") || name.contains("corrupt")
				|| name.startsWith("corrupt"))
			return true;
		return false;
	}

	public static boolean itemDegradesWhileCombating(int id) {
		String name = ItemDefinitions.getItemDefinitions(id).getName().toLowerCase();
		if (name.contains("torva") || name.contains("pernix") || name.contains("virtux") || name.contains("zaryte")
				|| ((name.contains("vesta") || name.contains("zuriel") || name.contains("morrigan")
						|| name.contains("statius")) && !name.contains("corrupt"))
				|| name.contains("dharok") || name.contains("verac") || name.contains("guthan")
				|| name.contains("ahrim") || name.contains("karil") || name.contains("akrisae")
				|| name.contains("chaotic") || name.contains("eagle-eye") || name.contains("farseer")
				|| name.contains("noxious") || name.contains("ascension") || name.contains("sirenic")
				|| name.contains("fungal") || name.contains("ganodermic") || name.contains("grifolic")
				|| name.contains("Attuned") || name.contains("drygore") || name.contains("emberkeen")
				|| name.contains("malevolent") || name.contains("hailfire") || name.contains("flarefrost")
				|| name.contains("death lotus") || name.contains("seasinger") || name.contains("tetsu")
				|| name.contains("achto"))
			return true;
		return false;
	}

	public static boolean isTradeable(Item item) {
		if (item.getId() == 34027 || item.getId() == 11640 || item.getId() == 27155 || item.getId() == 18337
				|| item.getId() == 13663 || item.getId() == 24154 || item.getId() == 34896 || item.getId() == 8839
				|| item.getId() == 8840 || item.getId() == 11663 || item.getId() == 11664 || item.getId() == 24954
				|| item.getId() == 6199 || item.getId() == 31875 || item.getId() == 31877 || item.getId() == 11665
				|| item.getId() == 8842 || item.getId() == 8839 || item.getId() == 19785 || item.getId() == 19786
				|| item.getId() == 34356 || item.getId() == 34358 || item.getId() == 26123 || item.getId() == 26124
				|| item.getId() == 26126 || item.getId() == 26128 || item.getId() == 26130 || item.getId() == 26132
				|| item.getId() == 26134 || item.getId() == 26136 || item.getId() == 26138 || item.getId() == 26140
				|| item.getId() == 23996 // werewolf event mask
				|| item.getId() == 18363 // farseer kiteshield
				|| item.getId() == 23671 // swag stick
				|| item.getId() == 23672 // swag bag
				|| item.getId() == 41397 // pot of gold
				|| item.getId() == 18361 // eagle kite-shield
				|| item.getId() == 37490 // casino cash
				|| item.getId() == 28388 || item.getId() == 28390 || item.getId() == 28416 || item.getId() == 28418 // Exquiste
				|| item.getId() == 15413 || item.getId() == 37619 || item.getId() == 37620 || item.getId() == 37621 // telos
																													// orb
				|| item.getId() == 41887 || item.getId() == 41888

		)
			return true;
		if (ItemDefinitions.getItemDefinitions(item.getId()).getName().toLowerCase().contains("chaotic")
				|| ItemDefinitions.getItemDefinitions(item.getId()).getName().toLowerCase().contains("(i)"))
			return true;
		if (ItemDefinitions.getItemDefinitions(item.getId()).getName().toLowerCase().contains("(deg)"))
			return true;
		if (item.getDefinitions().isLended())
			return false;
		if (item.getDefinitions().isDestroyItem() && !item.getName().toLowerCase().contains("ethereal")
				&& !item.getName().toLowerCase().contains("shark") && !item.getName().toLowerCase().contains("golem")
				&& !(item.getId() >= 35963 && item.getId() <= 35982))
			return false;
		for (int id : TreasureTrails.CLUE_SCROLLS)
			if (item.getId() == id)
				return false;
		for (int id : TreasureTrails.SCROLL_BOXES)
			if (item.getId() == id)
				return false;
		for (int id : TreasureTrails.PUZZLES)
			if (item.getId() == id)
				return false;
		Pets pets = Pets.forId(item.getId());
		if (pets != null)
			return false;
		String name = ItemDefinitions.getItemDefinitions(item.getId()).getName().toLowerCase();
		if (name.contains("aura") || name.contains("completionist") || name.contains("clue")
				|| name.contains("flarefrost") || name.contains("dice") || name.contains("hatchling")
				|| name.contains("max") || name.contains("effigi") || name.contains("lamp")
				|| name.contains("recover special")/* || name.contains("lucky") */ || name.contains("flaming")
				|| name.contains("clue") || name.contains("tzrek") || name.contains("katana")
				|| name.contains("ring of vigour") || name.contains("bonecrush") || name.contains("flaming skull")
				|| name.contains("veteran") || name.equalsIgnoreCase("cannon base") || name.contains("herbicid")
				|| name.equalsIgnoreCase("cannon stand") || name.contains(" 25")
				|| name.equalsIgnoreCase("cannon barrels") || name.contains(" 0")
				|| name.equalsIgnoreCase("cannon furnace") || name.contains("slayer") || name.contains("deathtouched")
				|| name.contains(" (deg)") || name.contains("void") || name.contains(" cape (t)")
				|| name.equalsIgnoreCase("master cape") || name.contains("(blood") || name.contains("arcane stream")
				|| name.contains(" 100") || name.contains(" 75") || name.contains("(i)")
				|| name.contains("amulet of souls") || name.contains("(barrows") || name.contains("(shadow")
				|| name.contains("(third age") || name.contains("fist of guthix") || name.contains(" 50")
				|| name.contains("fire cape") || name.contains("tokhaar") || name.contains("defender")
				|| name.contains("extreme") || name.contains("ancient weapon") || name.contains("ring of death")
				|| name.contains("emberkeen") || name.contains("hailfire") || name.contains("(worn")
				|| name.contains("superior") || name.contains("kiba") || name.contains("makigai")
				|| name.contains("tetsu wakizashi") || name.contains("sack of eff") || name.contains("teddy")
				|| name.contains("vyrewatch") || name.contains("crabclaw") || name.contains("diving app")
				|| name.contains("pumpkin") || name.contains("urchin") || name.contains("fishbowl h")
				|| name.contains("flaming sk") || name.contains("reaver") || name.contains("corruption sigil")
				|| name.contains("ancient emblem") || name.contains("perfect chitin") || name.contains("achto")
				|| name.contains("golden chaotic") || name.contains("untradeable") || name.contains("ice")
				|| (name.contains("christmas ") && !name.contains("cracker")) || name.contains("penguin")
				|| name.contains("prismatic dye") || name.contains("sparkles") || name.contains("snowball")
				|| name.contains("carrot") || name.contains("yo-yo") || name.contains("reindeer")
				|| name.contains("aurora")

		)
			return false;
		switch (item.getId()) {
		case 30574: // death lotus dart
		case 6570: // firecape
		case 13531: // lent phat
		case 13532: // lent phat
		case 13533: // lent phat
		case 13534: // lent phat
		case 13535: // lent phat
		case 13536: // lent phat
		case 18363: // farseer shield
		case 18361: // eagle eye
		case 18359: // chaotic kite
		case 6529: // tokkul
		case 14641: // SOUL WARS CAPE
		case 14642: // SOULWARS CAPE
		case 24155: // double spin ticket
		case 24154: // spin ticket
		case 299: // mithril seed
		case 10551: // fighter torso
		case 7462: // barrow gloves
		case 23659: // tokhaar-kal
		case 10887: // barrelchest anchor
		case 15584: // Charming imp
		case 15585: // Coin accumulator
		case 32694: // arcane blood neck
		case 32703: // arcane blood neck
			return false;
		/*
		 * Barrows Degraded Items
		 */
		case 4880:
		case 4881:
		case 4882:
		case 4883:
		case 4886:
		case 4887:
		case 4888:
		case 4889:
		case 4898:
		case 4899:
		case 4900:
		case 4901:
		case 4856:
		case 4857:
		case 4858:
		case 4859:
		case 4862:
		case 4863:
		case 4864:
		case 4865:
		case 4868:
		case 4869:
		case 4870:
		case 4871:
		case 4874:
		case 4875:
		case 4876:
		case 4877:
		case 4904:
		case 4905:
		case 4906:
		case 4907:
		case 4910:
		case 4911:
		case 4912:
		case 4913:
		case 4916:
		case 4917:
		case 4918:
		case 4920:
		case 4922:
		case 4923:
		case 4924:
		case 4925:
		case 4928:
		case 4929:
		case 4930:
		case 4931:
		case 4934:
		case 4935:
		case 4936:
		case 4937:
		case 4940:
		case 4941:
		case 4942:
		case 4943:
		case 4946:
		case 4947:
		case 4948:
		case 4949:
		case 4958:
		case 4959:
		case 4960:
		case 4961:
		case 4964:
		case 4965:
		case 4966:
		case 4967:
		case 4970:
		case 4971:
		case 4972:
		case 4973:
		case 4976:
		case 4892:
		case 4977:
		case 4978:
		case 4979:
		case 4982:
		case 4983:
		case 4984:
		case 4985:
		case 4988:
		case 4989:
		case 4990:
		case 4991:
		case 4994:
		case 4995:
		case 4996:
		case 4997:
			return false;
		default:
			return true;
		}
	}

	public static boolean turnCoins(Item item) {
		if (item.getDefinitions().getName().toLowerCase().contains("(deg)"))
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains("strength cape"))
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains("max cape"))
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains("max hood"))
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains("completionist cape"))
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains("completionist hood"))
			return true;
		switch (item.getId()) {
		case 10887:
		case 7462:
		case 7461:
		case 18349:
		case 18351:
		case 18353:
		case 18355:
		case 18357:
		case 18359:
		case 18361:
		case 18363:
		case 18335:
		case 18334:
		case 18333:
		case 31205:
			return true;
		default:
			return false;
		}
	}

	public static int removeAttachedId(Item item) {
		if (item.getDefinitions().getName().toLowerCase().contains("vine whip"))
			return 21369;
		switch (item.getId()) {
		case 12675:
		case 12674:
			return 3751;
		case 12678:
		case 12679:
			return 3755;
		case 12672:
		case 12673:
			return 3749;
		case 12676:
		case 12677:
			return 3753;
		case 15018:
			return 6731;
		case 15019:
			return 6733;
		case 15020:
			return 6735;
		case 15220:
			return 6737;
		case 15017:
			return 6575;

		case 19335:
			return 19333;
		case 19336:
			return 19346;
		case 19337:
			return 19350;
		case 19338:
		case 19339:
			return 19348;
		case 19340:
			return 19352;

		case 19341:
			return 19354;
		case 19342:
			return 19358;
		case 19343:
		case 19344:
			return 19356;
		case 19345:
			return 19360;

		}
		return -1;
	}

	public static int removeAttachedId2(Item item) {
		if (item.getDefinitions().getName().toLowerCase().contains("vine whip"))
			return 4151;
		switch (item.getId()) {
		case 19335:
			return 6585;
		case 19336:
		case 19341:
			return 11335;

		case 19337:
		case 19342:
			return 14479;

		case 19338:
		case 19343:
			return 4087;

		case 19339:
		case 19344:
			return 4585;

		case 19340:
		case 19345:
			return 1187;

		}
		return -1;
	}

	/**
	 * Default items that degrade when dropped.
	 * 
	 * @param item
	 *            The item.
	 * @return true if degrade.
	 */
	public static boolean degradeOnDrop(Item item) {
		if (item.getDefinitions().getName().toLowerCase().contains(" 100")
				|| item.getDefinitions().getName().toLowerCase().contains(" 75")
				|| item.getDefinitions().getName().toLowerCase().contains(" 50")
				|| item.getDefinitions().getName().toLowerCase().contains(" 25"))
			return true;
		return false;
	}

	/**
	 * Default items kept on death.
	 * 
	 * @param item
	 *            The item.
	 * @return true if kept.
	 */
	public static boolean keptOnDeath(Item item) {
		if (item.getDefinitions().isLended())
			return true;
		if (item.getId() == 19888)
			return true;
		if (item.getId() == 18839)
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains("sneak"))
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains("culinaromancer"))
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains(" charm"))
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains("sneak"))
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains("overload"))
			return true;
		if (item.getDefinitions().getName().toLowerCase().contains("slayer "))
			return true;
		switch (item.getId()) {
		case 22899:
		case 22901:
		case 22904:
		case 23876:
		case 22905:
		case 22907:
		case 22909:
		case 23874:
		case 23848:
		case 23850:
		case 23852:
		case 23854:
		case 23856:
		case 23862:
		case 22897:
		case 23866:
		case 23864:
		case 22298:
		case 22300:
		case 23860:
		case 23868:
		case 23858:
		case 2412: // god cape
		case 2413:
		case 2414:
		case 23659:
		case 6570:
		case 23660:
		case 18346:
		case 18335:
		case 10551:
		case 10548:
		case 20072:
		case 8850:
		case 8849:
		case 8848:
		case 8847:
		case 3839:
		case 3840:
		case 3841:
		case 3842:
		case 3843:
		case 3844:
		case 8842:
		case 11663:
		case 11664:
		case 11665:
		case 8839:
		case 8840:
		case 6665:
		case 6666:
			return true;
		default:
			return false;
		}
	}

	public static boolean canWear(Item item, Player player, boolean keepSake) {
		if (player.isDeveloper())
			return true;
		if (item.getName().toLowerCase().contains("ahrim")) {
			if (player.getSkills().getLevel(Skills.MAGIC) < 70) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have a magic level of 70.");
				}
				return false;
			}
		}
		if (item.getId() >= 29185 && item.getId() <= 29188 || item.getId() == 34292 || item.getId() == 34293) {
			if (player.getSkills().getLevel(Skills.DIVINATION) < 99) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have a level of 99 Divination to equip this.");
				}
				return false;
			}
		}
		if (item.getId() >= 36351 && item.getId() <= 36355) {
			if (player.getSkills().getLevel(Skills.INVENTION) < 99) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have a level of 99 Invention to equip this.");
				}
				return false;
			}
		}
		if (item.getId() == 15241) {
			if (player.getSkills().getLevel(Skills.RANGE) < 75 && player.getSkills().getLevel(Skills.FIREMAKING) < 61) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have a level of 75 Ranged and 61 Firemaking to equip this.");
				}
				return false;
			}
		}
		if (item.getId() >= 18349 && item.getId() <= 18363) {
			if (player.getSkills().getLevel(Skills.DUNGEONEERING) < 80) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have at least level 80 Dungeoneering to equip this.");
				}
				return false;
			}
		}
		if (item.getName().contains("Fire cape")) {
			if (!player.isCompletedFightCaves()) {
				if (!keepSake)
					player.sendMessage("You have to complete the Fight Caves minigame to wear this.");
				return false;
			}
		}
		if (item.getName().contains("TokHaar")) {
			if (!player.isCompletedFightKiln()) {
				player.sendMessage("You have to complete the Fight Kiln minigame to wear this.");
				return false;
			}
		}
		if (item.getId() == 20769 || item.getId() == 20770 || item.getId() == 32152) {
			if (!CompCape.isWorthyCompCape(player)) {
				if (!keepSake)
					player.sendMessage("You are not worthy enough to wear this cape.");
				return false;
			}
		}
		if (item.getId() == 20771 || item.getId() == 20772 || item.getId() == 32153) {
			if (!CompCape.isWorthyCompCapeT(player)) {
				if (!keepSake)
					player.sendMessage("You are not worthy enough to wear this cape.");
				return false;
			}
		}
		if (item.getId() == 4708 || item.getId() == 4710 || item.getId() == 4712 || item.getId() == 4714) {
			if (player.getSkills().getLevel(Skills.DEFENCE) < 70) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 70 Defence to wear this item.");
				}
				return false;
			}
			if (player.getSkills().getLevel(Skills.MAGIC) < 70) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 70 Magic to wear this item.");
				}
				return false;
			}
		}
		if (item.getId() == 36356) {
			if (player.getSkills().getXp(Skills.INVENTION) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31284) {
			if (player.getSkills().getXp(Skills.DIVINATION) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31277) {
			if (player.getSkills().getXp(Skills.AGILITY) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31268) {
			if (player.getSkills().getXp(Skills.ATTACK) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31275) {
			if (player.getSkills().getXp(Skills.CONSTRUCTION) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31288) {
			if (player.getSkills().getXp(Skills.COOKING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31280) {
			if (player.getSkills().getXp(Skills.CRAFTING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31270) {
			if (player.getSkills().getXp(Skills.DEFENCE) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31291) {
			if (player.getSkills().getXp(Skills.FARMING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31289) {
			if (player.getSkills().getXp(Skills.FIREMAKING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31287) {
			if (player.getSkills().getXp(Skills.FISHING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31281) {
			if (player.getSkills().getXp(Skills.FLETCHING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31278) {
			if (player.getSkills().getXp(Skills.HERBLORE) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31276) {
			if (player.getSkills().getXp(Skills.HITPOINTS) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31283) {
			if (player.getSkills().getXp(Skills.HUNTER) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31273) {
			if (player.getSkills().getXp(Skills.MAGIC) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31285) {
			if (player.getSkills().getXp(Skills.MINING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31272) {
			if (player.getSkills().getXp(Skills.PRAYER) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31271) {
			if (player.getSkills().getXp(Skills.RANGE) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31274) {
			if (player.getSkills().getXp(Skills.RUNECRAFTING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31282) {
			if (player.getSkills().getXp(Skills.SLAYER) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31286) {
			if (player.getSkills().getXp(Skills.SMITHING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31269) {
			if (player.getSkills().getXp(Skills.STRENGTH) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31292) {
			if (player.getSkills().getXp(Skills.SUMMONING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31279) {
			if (player.getSkills().getXp(Skills.THIEVING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 31290) {
			if (player.getSkills().getXp(Skills.WOODCUTTING) < 104273167) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need to have 104,273,167 experience in the skill to wear this!");
				}
				return false;
			}
		}
		if (item.getId() == 13661) {
			if (player.getSkills().getLevel(Skills.FIREMAKING) < 92) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need a Firemaking of level 92 to equip this item.");
				}
				return false;
			}
		}
		if (item.getId() == 13659) {
			if (player.getSkills().getLevel(Skills.FIREMAKING) < 62) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need a Firemaking of level 62 to equip this item.");
				}
				return false;
			}
		}
		if (item.getId() == 13660) {
			if (player.getSkills().getLevel(Skills.FIREMAKING) < 79) {
				if (!keepSake) {
					player.sendMessage("You are not high enough level to use this item.");
					player.sendMessage("You need a Firemaking of level 79 to equip this item.");
				}
				return false;
			}
		} else if (item.getId() == 14642 || item.getId() == 14645 || item.getId() == 15433 || item.getId() == 15435
				|| item.getId() == 14641 || item.getId() == 15432 || item.getId() == 15434) {
			if (!player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM)) {
				if (!keepSake)
					player.sendMessage("You need to have completed Nomad's Requiem mini-quest to use this cape.");
				return false;
			}
		}

		HashMap<Integer, Integer> requiriments = item.getDefinitions().getWearingSkillRequiriments();
		boolean hasRequiriments = true;
		if (requiriments != null) {
			for (int skillId : requiriments.keySet()) {
				if (skillId > 24 || skillId < 0)
					continue;

				int level = requiriments.get(skillId);
				if (level < 0 || level > 120)
					continue;
				if (item.getDefinitions().getName().equalsIgnoreCase("dark bow") && skillId == Skills.DEFENCE)
					level = 42;
				if (player.getSkills().getLevelForXp(skillId) < level) {
					if (hasRequiriments)
						if (!keepSake)
							player.sendMessage("You are not high enough level to use this item.");
					hasRequiriments = false;
					String name = Skills.SKILL_NAME[skillId].toLowerCase();
					if (!keepSake)
						player.sendMessage("You need to have a" + (name.startsWith("a") ? "n" : "") + " " + name
								+ " level of " + level + ".");
				}
			}
		}
		if (!hasRequiriments)
			return false;
		return true;
	}

	public static final int[] getItemComposition(Item item) {
		switch (item.getId()) {
		case 21371:
		case 21372:
		case 21373:
		case 21374:
		case 21375:
			return new int[] { 4151, 21369 };
		case 12675:
		case 12674:
			return new int[] { 3751 };
		case 12678:
		case 12679:
			return new int[] { 3755 };
		case 12672:
		case 12673:
			return new int[] { 3749 };
		case 12676:
		case 12677:
			return new int[] { 3753 };
		case 15018:
			return new int[] { 6731 };
		case 15019:
			return new int[] { 6733 };
		case 15020:
			return new int[] { 6735 };
		case 15220:
			return new int[] { 6737 };
		case 15017:
			return new int[] { 6575 };

		case 19335:
			return new int[] { 6585, 19333 };
		case 19336:
			return new int[] { 11335, 19346 };
		case 19337:
			return new int[] { 14479, 19350 };
		case 19338:
			return new int[] { 4087, 19348 };
		case 19339:
			return new int[] { 4585, 19348 };
		case 19340:
			return new int[] { 1187, 19352 };

		case 19341:
			return new int[] { 11335, 19354 };
		case 19342:
			return new int[] { 14479, 19358 };
		case 19343:
			return new int[] { 4087, 19356 };
		case 19344:
			return new int[] { 4585, 19356 };
		case 19345:
			return new int[] { 1187, 19360 };
		default:
			return null;
		}
	}

}