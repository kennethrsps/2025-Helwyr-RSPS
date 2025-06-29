package com.rs.game.player.content;

import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Utils;

public class CosmeticsHandler {

	public static final int KEEP_SAKE_KEY = 25430;
	public static final int DEFAULT_PRICE_FULL_OUTFIT = 10;
	public static final int DEFAULT_PRICE_SINGLE_PIECE = 1;

	public static void openCosmeticsHandler(final Player player) {
		player.stopAll();
		player.getTemporaryAttributtes().put("Cosmetics", Boolean.TRUE);
		player.getPackets().sendHideIComponent(667, 84, true);
		player.getPackets().sendHideIComponent(667, 22, true);
		player.getPackets().sendHideIComponent(667, 23, true);
		for (int i = 0; i < Utils.getInterfaceDefinitionsComponentsSize(667); i++)
			player.getPackets().sendIComponentText(667, i, "");
		player.getPackets().sendIComponentText(667, 25, "Instructions:");
		player.getPackets().sendIComponentText(667, 28, "Click \"continue\" on a slot");
		player.getPackets().sendIComponentText(667, 29, "to view costumes in that");
		player.getPackets().sendIComponentText(667, 30, "slot.");
		player.getPackets().sendIComponentText(667, 26, "Custom slots:");
		player.getPackets().sendIComponentText(667, 33, "Arrows slot -> Wings.");
		player.getPackets().sendIComponentText(667, 35, "Ring slot -> Full/Saved ");
		player.getPackets().sendIComponentText(667, 36, "Outfits.");
		player.getPackets().sendIComponentText(667, 38, "Aura slot -> Gazes.");
		player.getPackets().sendConfigByFile(8348, 1);
		player.getPackets().sendConfigByFile(4894, 0);
		Item[] cosmetics = player.getEquipment().getItems().getItemsCopy();
		for (int i = 0; i < cosmetics.length; i++) {
			cosmetics[i] = new Item(0);
		}
		player.getPackets().sendItems(94, cosmetics);
		player.getPackets().sendUnlockIComponentOptionSlots(667, 9, 0, 14, true, 0, 1, 2, 3);
		WorldTasksManager.schedule(new WorldTask() {
			@Override
			public void run() {
				player.getPackets().sendConfigByFile(8348, 1);
				player.getPackets().sendRunScriptBlank(2319);
			}
		});
		player.getInterfaceManager().sendInterface(667);
		player.setCloseInterfacesEvent(() -> {
			player.getDialogueManager().finishDialogue();
			player.getTemporaryAttributtes().remove("Cosmetics");
			for (int i = 0; i < 15; i++) {
				player.getEquipment().refresh(i);
			}
		});
	}

	public static boolean keepSakeItem(Player player, Item itemUsed, Item itemUsedWith) {
		if (itemUsed.getId() != KEEP_SAKE_KEY && itemUsedWith.getId() != KEEP_SAKE_KEY)
			return false;
		if (itemUsed.getId() == KEEP_SAKE_KEY && itemUsedWith.getId() == KEEP_SAKE_KEY)
			return false;
		Item keepSakeKey = itemUsed.getId() == KEEP_SAKE_KEY ? itemUsed : itemUsedWith;
		Item keepSakeItem = itemUsed.getId() == KEEP_SAKE_KEY ? itemUsedWith : itemUsed;
		if (keepSakeItem == null || keepSakeKey == null)
			return false;
		if (player.getEquipment().getKeepSakeItems().size() >= 50) {
			player.getPackets().sendGameMessage("You can only keep sake 50 items.");
			return false;
		}
		int equipSlot = keepSakeItem.getDefinitions().getEquipSlot();
		if (equipSlot == Equipment.SLOT_ARROWS || equipSlot == Equipment.SLOT_AURA
				|| equipSlot == Equipment.SLOT_RING) {
			player.getPackets().sendGameMessage(
					"You can only keep sake items that goes into head, cape, neck, body, legs, gloves, main hand, off-hand, or boots slots.");
			return false;
		}
		if (equipSlot == -1) {
			player.getPackets().sendGameMessage("You can't keep sake this item as its not wearable.");
			return false;
		}
		if (!ItemConstants.canWear(keepSakeItem, player, true)) {
			player.getPackets().sendGameMessage("You don't have enough requirments to keep sake this item.");
			return false;
		}
		if (keepSakeItem.getDefinitions().isBindItem() || keepSakeItem.getDefinitions().isLended()
				|| keepSakeItem.getDefinitions().isStackable())
			return false;
		String name = keepSakeItem.getName().toLowerCase();
		if (name.contains("broken")) {
			player.getPackets().sendGameMessage("You can't keep sake broken items.");
			return false;
		}
		for (Item item : player.getEquipment().getKeepSakeItems()) {
			if (item == null)
				continue;
			if (item.getId() == keepSakeItem.getId()) {
				player.getPackets().sendGameMessage("You already have that item in your keepsake box.");
				return false;
			}
		}
		player.stopAll();
		player.getDialogueManager().startDialogue(new Dialogue() {

			@Override
			public void start() {
				sendOptionsDialogue("DO YOU WANT TO KEEP SAKE THIS ITEM?",
						"Yes, keep sake this item.(You won't be able to retrieve key)", "No, I would like to keep it.");
			}

			@Override
			public void run(int interfaceId, int componentId) {
				if (componentId == OPTION_1) {
					player.getEquipment().getKeepSakeItems().add(keepSakeItem);
					player.getPackets().sendGameMessage("You have added " + keepSakeItem.getName()
							+ " to keepsakes. It will appear along with other in cosmetic list.");
					player.getInventory().deleteItem(KEEP_SAKE_KEY, 1);
					player.getInventory().deleteItem(keepSakeItem);
				}
				end();
			}

			@Override
			public void finish() {

			}

		});
		return true;
	}

	public static boolean isRestrictedItem(Player player, Cosmetic... cosmetics) {
		for (Cosmetic costume : cosmetics) {
			if (isRestrictedItem(player, costume.getItemId()))
				return true;
		}
		return false;
	}

	public static boolean isRestrictedItem(Player player, int itemId) {
		/*
		 * if (player.isDev()) return false;
		 */
		for (Cosmetic cosmetic : Cosmetics.HIDE_ALL.getCosmetics()) {
			if (itemId == cosmetic.getItemId())
				return false;
		}
		for (Item item : player.getEquipment().getKeepSakeItems()) {
			if (item == null)
				continue;
			if (item.getId() == itemId)
				return false;
		}
		return getEarnedMessageRequirement(player, itemId) != null || player.isLockedCostume(itemId);
	}

	public static String getEarnedMessageRequirement(Player player, int itemId) {
		switch (itemId) {// Non-Buyable Costumes
		case 33712:// Torso of Omens
			if (player.getMauledWeeksHM()[0])
				return null;
			return "You need to maul hard mode vorago in Ceiling Collapse rotation to unlock this.";
		case 33711:// Helm of Omens
			if (player.getMauledWeeksHM()[1])
				return null;
			return "You need to maul hard mode vorago in Scopulus rotation to unlock this.";
		case 33713:// Legs of Omens
			if (player.getMauledWeeksHM()[2])
				return null;
			return "You need to maul hard mode vorago in Vitalis rotation to unlock this.";
		case 33714:// Boots of Omens
			if (player.getMauledWeeksHM()[3])
				return null;
			return "You need to maul hard mode vorago in Green bomb rotation to unlock this.";
		case 33709:// Maul of Omens
			if (player.getMauledWeeksHM()[4])
				return null;
			return "You need to maul hard mode vorago in TeamSplit rotation to unlock this.";
		case 33715:// Gloves of Omens
			if (player.getMauledWeeksHM()[5])
				return null;
			return "You need to maul hard mode vorago in The End rotation to unlock this.";
		}
		return null;
	}

	public static void UnlockCostume(Player player, Cosmetics cosmetic) {
		boolean unlocked = false;
		for (Cosmetic costume : cosmetic.getCosmetics()) {
			if (player.getUnlockedCostumesIds().contains(costume.getItemId()))
				continue;
			player.getUnlockedCostumesIds().add(costume.getItemId());
			unlocked = true;
		}
		if (unlocked)
			player.getPackets().sendGameMessage("<col=00ff00>You have unlocked " + cosmetic.getName() + "!");
	}

	public static void previewCosmetic(Player player, Cosmetics costume, final int page, final String keyWord) {
		ItemsContainer<Item> cosmeticPreviewItems = new ItemsContainer<>(19, false);
		player.getTemporaryAttributtes().put("Cosmetics", Boolean.TRUE);
		player.getPackets().sendHideIComponent(667, 4, true);
		player.getPackets().sendHideIComponent(667, 9, true);
		player.getPackets().sendHideIComponent(667, 24, true);
		player.getPackets().sendHideIComponent(667, 84, true);
		for (Cosmetic cosmetic : costume.getCosmetics()) {
			cosmeticPreviewItems.set(cosmetic.getSlot(), new Item(cosmetic.getItemId()));
		}
		player.getEquipment().setCosmeticPreviewItems(cosmeticPreviewItems);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		player.getInterfaceManager().sendInterface(667);
		player.setCloseInterfacesEvent(() -> {
			player.getEquipment().setCosmeticPreviewItems(null);
			player.getGlobalPlayerUpdater().generateAppearenceData();
			player.getTemporaryAttributtes().remove("Cosmetics");
			openCosmeticsStore(player, keyWord, page);
		});
	}

	public static void openCosmeticsStore(Player player, int page) {
		CosmeticsHandler.openCosmeticsStore(player, null, page);
	}

	public static void openCosmeticsStore(Player player, String keyWord, int page) {
		player.getDialogueManager().startDialogue("CosmeticStoreD", page, keyWord);
	}

	public enum Cosmetics {

		HIDE_ALL(new Cosmetic("Hide helmet", 27146, 0), new Cosmetic("Hide cape", 27147, 1),
				new Cosmetic("Hide necklace", 30038, 2), new Cosmetic("Hide torso", 30039, 4),
				new Cosmetic("Hide legs", 30040, 7), new Cosmetic("Hide gloves", 30042, 9),
				new Cosmetic("Hide boots", 30041, 10), new Cosmetic("Hide effects", 35865, 14)),

		CABARET_OUTFIT(new Cosmetic("Cabaret hat", 24583, 0), new Cosmetic("Cabaret jacket", 24585, 4),
				new Cosmetic("Cabaret legs", 24587, 7), new Cosmetic("Cabaret gloves", 24591, 9),
				new Cosmetic("Cabaret shoes", 24589, 10)),

		COLOSSEUM_OUTFIT(new Cosmetic("Colosseum head", 24595, 0), new Cosmetic("Colosseum shoes", 24601, 10),
				new Cosmetic("Colosseum jacket", 24597, 4), new Cosmetic("Colosseum legs", 24599, 7)),

		FELINE_OUTFIT(new Cosmetic("Feline ears", 24605, 0), new Cosmetic("Feline tail", 24613, 1),
				new Cosmetic("Feline jacket", 24607, 4), new Cosmetic("Feline legs", 24609, 7),
				new Cosmetic("Feline shoes", 24611, 10)),

		GOTHIC_OUTFIT(new Cosmetic("Gothic cape", 24623, 1), new Cosmetic("Gothic shoes", 24621, 10),
				new Cosmetic("Gothic jacket", 24617, 4), new Cosmetic("Gothic legs", 24619, 7)),

		SWASHBUCKLER_OUTFIT(new Cosmetic("Swashbuckler mask", 24627, 0), new Cosmetic("Swashbuckler cape", 24635, 1),
				new Cosmetic("Swashbuckler jacket", 24629, 4), new Cosmetic("Swashbuckler legs", 24631, 7),
				new Cosmetic("Swashbuckler shoes", 24633, 10)),

		ASSASSIN_OUTFIT(new Cosmetic("Assassin hood", 24639, 0), new Cosmetic("Assassin cape", 24649, 1),
				new Cosmetic("Assassin scimitar", 24651, 3), new Cosmetic("Assassin jacket", 24641, 4),
				new Cosmetic("Assassin scimitar Off-hand", 26029, 5), new Cosmetic("Assassin legs", 24643, 7),
				new Cosmetic("Assassin gloves", 24647, 9), new Cosmetic("Assassin shoes", 24645, 10)),

		BEACHWEAR_OUTFIT(new Cosmetic("Beachwear head", 24827, 0), new Cosmetic("Beachwear sandals", 24832, 10),
				new Cosmetic("Beachwear shirt", 24828, 4), new Cosmetic("Beachwear shorts", 24830, 7)),

		MONARCH_OUTFIT(new Cosmetic("Monarch crown", 25074, 0), new Cosmetic("Monarch doublet", 25076, 4),
				new Cosmetic("Monarch legs", 25080, 7), new Cosmetic("Monarch gloves", 25078, 9),
				new Cosmetic("Monarch shoes", 25082, 10)),

		NOBLE_OUTFIT(new Cosmetic("Noble hat", 25086, 0), new Cosmetic("Noble jacket", 25088, 4),
				new Cosmetic("Noble legs", 25092, 7), new Cosmetic("Noble gloves", 25090, 9),
				new Cosmetic("Noble shoes", 25094, 10)),

		SERVANT_OUTFIT(new Cosmetic("Servant hat", 25098, 0), new Cosmetic("Servant amulet", 25100, 2),
				new Cosmetic("Servant jacket", 25102, 4), new Cosmetic("Servant legs", 25106, 7),
				new Cosmetic("Servant gloves", 25104, 9), new Cosmetic("Servant shoes", 25108, 10)),

		FOX_OUTFIT(new Cosmetic("Fox ears", 25136, 0), new Cosmetic("Fox tail", 25142, 1),
				new Cosmetic("Fox jacket", 25138, 4), new Cosmetic("Fox legs", 25140, 7),
				new Cosmetic("Fox shoes", 25144, 10)),

		WOLF_OUTFIT(new Cosmetic("Wolf ears", 25148, 0), new Cosmetic("Wolf tail", 25154, 1),
				new Cosmetic("Wolf jacket", 25150, 4), new Cosmetic("Wolf legs", 25152, 7),
				new Cosmetic("Wolf shoes", 25156, 10)),

		PANDA_OUTFIT(new Cosmetic("Panda ears", 25160, 0), new Cosmetic("Panda tail", 25166, 1),
				new Cosmetic("Panda jacket", 25162, 4), new Cosmetic("Panda legs", 25164, 7),
				new Cosmetic("Panda shoes", 25168, 10)),

		DWARVEN_WARSUIT_OUTFIT(new Cosmetic("Dwarven Warsuit helm", 25273, 0),
				new Cosmetic("Dwarven Warsuit chest", 25275, 4), new Cosmetic("Dwarven Warsuit legwear", 25277, 7),
				new Cosmetic("Dwarven Warsuit gauntlets", 25279, 9), new Cosmetic("Dwarven Warsuit boots", 25281, 10)),

		KRILS_BATTLEGEAR_OUTFIT(new Cosmetic("K'ril's Battlegear helm", 25374, 0),
				new Cosmetic("K'ril's Battlegear chest armour", 25376, 4),
				new Cosmetic("K'ril's Battlegear leg armour", 25378, 7),
				new Cosmetic("K'ril's Battlegear gauntlets", 25380, 9),
				new Cosmetic("K'ril's Battlegear boots", 25382, 10)),

		KRILS_GODCRUSHER_ARMOUR(new Cosmetic("K'ril's Godcrusher helm", 25386, 0),
				new Cosmetic("K'ril's Godcrusher winged cape", 25396, 1),
				new Cosmetic("K'ril's Godcrusher chest armour", 25388, 4),
				new Cosmetic("K'ril's Godcrusher leg armour", 25390, 7),
				new Cosmetic("K'ril's Godcrusher gauntlets", 25392, 9),
				new Cosmetic("K'ril's Godcrusher boots", 25394, 10)),

		ARIANES_OUTFIT(new Cosmetic("Ariane's tiara", 26043, 0), new Cosmetic("Ariane's robe", 26045, 4),
				new Cosmetic("Ariane's robe", 26047, 7), new Cosmetic("Ariane's bracers", 26049, 9),
				new Cosmetic("Ariane's boots", 26051, 10)),

		OZANS_OUTFIT(new Cosmetic("Ozan's cape", 26071, 1), new Cosmetic("Ozan's tunic", 26063, 4),
				new Cosmetic("Ozan's breeches", 26065, 7), new Cosmetic("Ozan's gloves", 26067, 9),
				new Cosmetic("Ozan's boots", 26069, 10)),

		TOKHAAR_BRUTE_OUTFIT(new Cosmetic("TokHaar Brute helm", 26158, 0),
				new Cosmetic("TokHaar Brute chest armour", 26160, 4),
				new Cosmetic("TokHaar Brute leg armour", 26162, 7), new Cosmetic("TokHaar Brute gauntlets", 26164, 9),
				new Cosmetic("TokHaar Brute boots", 26166, 10)),

		TOKHAAR_VETERAN_OUTFIT(new Cosmetic("TokHaar Veteran helm", 26170, 0),
				new Cosmetic("TokHaar Veteran chest armour", 26172, 4),
				new Cosmetic("TokHaar Veteran leg armour", 26174, 7),
				new Cosmetic("TokHaar Veteran gauntlets", 26176, 9), new Cosmetic("TokHaar Veteran boots", 26178, 10)),

		TOKHAAR_WARLORD_OUTFIT(new Cosmetic("TokHaar Warlord helm", 26182, 0),
				new Cosmetic("TokHaar Warlord chest armour", 26184, 4),
				new Cosmetic("TokHaar Warlord leg armour", 26186, 7),
				new Cosmetic("TokHaar Warlord gauntlets", 26188, 9), new Cosmetic("TokHaar Warlord boots", 26190, 10)),

		EASTERN_CAPTAINS_OUTFIT(new Cosmetic("Eastern Captain's tricorne", 26402, 0),
				new Cosmetic("Eastern Captain's coat", 26404, 4), new Cosmetic("Eastern Captain's trousers", 26406, 7),
				new Cosmetic("Eastern Captain's gloves", 26408, 9), new Cosmetic("Eastern Captain's boots", 26410, 10)),

		EASTERN_CREWS_OUTFIT(new Cosmetic("Eastern Crew's hat", 26414, 0),
				new Cosmetic("Eastern Crew's shirt", 26416, 4), new Cosmetic("Eastern Crew's trousers", 26418, 7),
				new Cosmetic("Eastern Crew's gloves", 26420, 9), new Cosmetic("Eastern Crew's boots", 26422, 10)),

		WESTERN_CAPTAINS_OUTFIT(new Cosmetic("Western Captain's hat", 26390, 0),
				new Cosmetic("Western Captain's coat", 26392, 4), new Cosmetic("Western Captain's trousers", 26394, 7),
				new Cosmetic("Western Captain's gloves", 26396, 9), new Cosmetic("Western Captain's boots", 26398, 10)),

		WESTERN_CREWS_OUTFIT(new Cosmetic("Western Crew's hat", 26426, 0),
				new Cosmetic("Western Crew's shirt", 26428, 4), new Cosmetic("Western Crew's trousers", 26430, 7),
				new Cosmetic("Western Crew's gloves", 26432, 9), new Cosmetic("Western Crew's boots", 26434, 10)),

		PALADIN_OUTFIT(new Cosmetic("Paladin gauntlets", 26472, 9), new Cosmetic("Paladin boots", 26470, 10),
				new Cosmetic("Paladin chestplate", 26466, 4), new Cosmetic("Paladin legplates", 26468, 7)),

		PALADIN_HERO_OUTFIT(new Cosmetic("Paladin Hero helm", 26464, 0), new Cosmetic("Paladin chestplate", 26466, 4),
				new Cosmetic("Paladin legplates", 26468, 7), new Cosmetic("Paladin gauntlets", 26472, 9),
				new Cosmetic("Paladin boots", 26470, 10)),

		KALPHITE_SENTINEL_OUTFIT(new Cosmetic("Kalphite helm", 27075, 0), new Cosmetic("Kalphite cape", 27076, 1),
				new Cosmetic("Kalphite chestplate", 27077, 4), new Cosmetic("Kalphite platelegs", 27079, 7),
				new Cosmetic("Kalphite gauntlets", 27078, 9), new Cosmetic("Kalphite armoured boots", 27080, 10)),

		KALPHITE_EMISSARY_OUTFIT(new Cosmetic("Kalphite antennae", 27083, 0),
				new Cosmetic("Kalphite wing cape", 27084, 1), new Cosmetic("Kalphite robe top", 27085, 4),
				new Cosmetic("Kalphite lower robes", 27087, 7), new Cosmetic("Kalphite gloves", 27086, 9),
				new Cosmetic("Kalphite boots", 27088, 10)),

		SHADOW_CAT_OUTFIT(new Cosmetic("Shadow Cat ears", 27174, 0), new Cosmetic("Shadow Cat tail", 27178, 1),
				new Cosmetic("Shadow Cat jacket", 27175, 4), new Cosmetic("Shadow Cat legs", 27176, 7),
				new Cosmetic("Shadow Cat shoes", 27177, 10)),

		SHADOW_HUNTER_OUTFIT(new Cosmetic("Shadow Hunter hood", 27181, 0), new Cosmetic("Shadow Hunter cape", 27186, 1),
				new Cosmetic("Shadow Hunter jacket", 27182, 4), new Cosmetic("Shadow Hunter legs", 27183, 7),
				new Cosmetic("Shadow Hunter gloves", 27185, 9), new Cosmetic("Shadow Hunter shoes", 27184, 10)),

		SHADOW_SENTINEL_OUTFIT(new Cosmetic("Shadow Sentinel helm", 27189, 0),
				new Cosmetic("Shadow Sentinel chestplate", 27190, 4), new Cosmetic("Shadow Sentinel legwear", 27191, 7),
				new Cosmetic("Shadow Sentinel gauntlets", 27192, 9), new Cosmetic("Shadow Sentinel boots", 27193, 10)),

		SHADOW_DEMON_OUTFIT(new Cosmetic("Shadow Demon helm", 27205, 0), new Cosmetic("Shadow Demon cape", 27210, 1),
				new Cosmetic("Shadow Demon chestplate", 27206, 4), new Cosmetic("Shadow Demon leg armour", 27207, 7),
				new Cosmetic("Shadow Demon gauntlets", 27208, 9), new Cosmetic("Shadow Demon boots", 27209, 10)),

		SHADOW_KNIGHT_OUTFIT(new Cosmetic("Shadow Knight helmet", 27198, 0),
				new Cosmetic("Shadow Knight chestplate", 27199, 4), new Cosmetic("Shadow Knight legs", 27200, 7),
				new Cosmetic("Shadow Knight gloves", 27202, 9), new Cosmetic("Shadow Knight boots", 27201, 10)),

		GREATER_DEMONFLESH_ARMOUR(new Cosmetic("Greater demonflesh mask", 27120, 0),
				new Cosmetic("Greater demonflesh cape", 27130, 1), new Cosmetic("Greater demonflesh torso", 27122, 4),
				new Cosmetic("Greater demonflesh legs", 27124, 7), new Cosmetic("Greater demonflesh gloves", 27126, 9),
				new Cosmetic("Greater demonflesh boots", 27128, 10)),

		LESSER_DEMONFLESH_ARMOUR(new Cosmetic("Lesser demonflesh mask", 27134, 0),
				new Cosmetic("Lesser demonflesh torso", 27136, 4), new Cosmetic("Lesser demonflesh legs", 27138, 7),
				new Cosmetic("Lesser demonflesh gloves", 27140, 9), new Cosmetic("Lesser demonflesh boots", 27142, 10)),

		DRAGON_WOLF_OUTFIT(new Cosmetic("Dragon wolf helmet", 27220, 0), new Cosmetic("Dragon wolf tunic", 27222, 4),
				new Cosmetic("Dragon wolf leggings", 27224, 7), new Cosmetic("Dragon wolf gloves", 27226, 9),
				new Cosmetic("Dragon wolf boots", 27228, 10)),

		GUTHIXIAN_WAR_ROBES(new Cosmetic("Guthixian war hood", 27419, 0), new Cosmetic("Guthixian war robe", 27421, 4),
				new Cosmetic("Guthixian war robe", 27423, 7), new Cosmetic("Guthixian war gloves", 27425, 9),
				new Cosmetic("Guthixian war boots", 27427, 10)),

		SARADOMINIST_WAR_ROBES(new Cosmetic("Saradominist war hood", 27431, 0),
				new Cosmetic("Saradominist war robe", 27433, 4), new Cosmetic("Saradominist war robe", 27435, 7),
				new Cosmetic("Saradominist war gloves", 27437, 9), new Cosmetic("Saradominist war boots", 27439, 10)),

		ZAMORAKIAN_WAR_ROBES(new Cosmetic("Zamorakian war hood", 27443, 0),
				new Cosmetic("Zamorakian war robe", 27445, 4), new Cosmetic("Zamorakian war robe", 27447, 7),
				new Cosmetic("Zamorakian war gloves", 27449, 9), new Cosmetic("Zamorakian war boots", 27451, 10)),

		ZAROSIAN_WAR_ROBES(new Cosmetic("Zarosian war hood", 27455, 0), new Cosmetic("Zarosian war robe", 27457, 4),
				new Cosmetic("Zarosian war robe", 27459, 7), new Cosmetic("Zarosian war gloves", 27461, 9),
				new Cosmetic("Zarosian war boots", 27463, 10)),

		ROBES_OF_SORROW_OUTFIT(new Cosmetic("Cowl of Sorrow", 27557, 0), new Cosmetic("Robes of Sorrow", 27558, 4),
				new Cosmetic("Robes of Sorrow", 27560, 7), new Cosmetic("Gauntlets of Sorrow", 27559, 9),
				new Cosmetic("Boots of Sorrow", 27561, 10)),

		VESTMENTS_OF_SORROW_OUTFIT(new Cosmetic("Hood of Sorrow", 27565, 0),
				new Cosmetic("Vestments of Sorrow", 27566, 4), new Cosmetic("Vestments of Sorrow", 27568, 7),
				new Cosmetic("Gloves of Sorrow", 27567, 9), new Cosmetic("Footwear of Sorrow", 27569, 10)),

		ROBES_OF_REMEMBRANCE_OUTFIT(new Cosmetic("Garland of Remembrance", 27572, 0),
				new Cosmetic("Robes of Remembrance", 27573, 4), new Cosmetic("Robes of Remembrance", 27575, 7),
				new Cosmetic("Bracers of Remembrance", 27574, 9), new Cosmetic("Sandals of Remembrance", 27576, 10)),

		VESTMENTS_OF_REMEMBRANCE_OUTFIT(new Cosmetic("Wreath of Remembrance", 27580, 0),
				new Cosmetic("Vestments of Remembrance", 27581, 4), new Cosmetic("Vestments of Remembrance", 27583, 7),
				new Cosmetic("Handwraps of Remembrance", 27582, 9),
				new Cosmetic("Footwraps of Remembrance", 27584, 10)),

		SKYPOUNCER_OUTFIT(new Cosmetic("Skypouncer headpiece", 27549, 0), new Cosmetic("Skypouncer cape", 27554, 1),
				new Cosmetic("Skypouncer chestpiece", 27550, 4), new Cosmetic("Skypouncer legwear", 27552, 7),
				new Cosmetic("Skypouncer gloves", 27551, 9), new Cosmetic("Skypouncer boots", 27553, 10)),

		EXECUTIONER_OUTFIT(new Cosmetic("Executioner cowl", 28001, 0), new Cosmetic("Executioner robe", 28003, 4),
				new Cosmetic("Executioner robe", 28007, 7), new Cosmetic("Executioner gloves", 28005, 9),
				new Cosmetic("Executioner boots", 28009, 10)),

		FLAMEHEART_ARMOUR(new Cosmetic("Flameheart headgear", 28049, 0), new Cosmetic("Flameheart chest", 28050, 4),
				new Cosmetic("Flameheart legs", 28052, 7), new Cosmetic("Flameheart gloves", 28051, 9),
				new Cosmetic("Flameheart boots", 28053, 10)),

		STONEHEART_ARMOUR(new Cosmetic("Stoneheart helm", 28056, 0), new Cosmetic("Stoneheart chestplate", 28057, 4),
				new Cosmetic("Stoneheart greaves", 28059, 7), new Cosmetic("Stoneheart gauntlets", 28058, 9),
				new Cosmetic("Stoneheart boots", 28060, 10)),

		STORMHEART_ARMOUR(new Cosmetic("Stormheart headgear", 28063, 0), new Cosmetic("Stormheart robe", 28064, 4),
				new Cosmetic("Stormheart robe", 28066, 7), new Cosmetic("Stormheart wraps", 28065, 9),
				new Cosmetic("Stormheart boots", 28067, 10)),

		ICEHEART_ARMOUR(new Cosmetic("Iceheart headgear", 28070, 0), new Cosmetic("Iceheart robe", 28071, 4),
				new Cosmetic("Iceheart robe", 28073, 7), new Cosmetic("Iceheart wraps", 28072, 9),
				new Cosmetic("Iceheart boots", 28074, 10)),

		COLOSSUS_ARMOUR(new Cosmetic("Colossus helm", 28838, 0), new Cosmetic("Colossus cape", 28843, 1),
				new Cosmetic("Colossus cuirass", 28839, 4), new Cosmetic("Colossus greaves", 28840, 7),
				new Cosmetic("Colossus gauntlets", 28841, 9), new Cosmetic("Colossus boots", 28842, 10)),

		VETERAN_COLOSSUS_ARMOUR(new Cosmetic("Veteran colossus helm", 28846, 0),
				new Cosmetic("Veteran colossus cape", 28851, 1), new Cosmetic("Veteran colossus cuirass", 28847, 4),
				new Cosmetic("Veteran colossus greaves", 28848, 7),
				new Cosmetic("Veteran colossus gauntlets", 28849, 9),
				new Cosmetic("Veteran colossus boots", 28850, 10)),

		TITAN_ARMOUR(new Cosmetic("Titan helm", 28854, 0), new Cosmetic("Titan cuirass", 28855, 4),
				new Cosmetic("Titan greaves", 28856, 7), new Cosmetic("Titan gauntlets", 28857, 9),
				new Cosmetic("Titan boots", 28858, 10)),

		VETERAN_TITAN_ARMOUR(new Cosmetic("Veteran titan helm", 28861, 0),
				new Cosmetic("Veteran titan cuirass", 28862, 4), new Cosmetic("Veteran titan greaves", 28863, 7),
				new Cosmetic("Veteran titan gauntlets", 28864, 9), new Cosmetic("Veteran titan boots", 28865, 10)),

		BEHEMOTH_ARMOUR(new Cosmetic("Behemoth helm", 28868, 0), new Cosmetic("Behemoth cape", 28873, 1),
				new Cosmetic("Behemoth cuirass", 28869, 4), new Cosmetic("Behemoth greaves", 28870, 7),
				new Cosmetic("Behemoth gauntlets", 28871, 9), new Cosmetic("Behemoth boots", 28872, 10)),

		VETERAN_BEHEMOTH_ARMOUR(new Cosmetic("Veteran behemoth helm", 28876, 0),
				new Cosmetic("Veteran behemoth cape", 28881, 1), new Cosmetic("Veteran behemoth cuirass", 28877, 4),
				new Cosmetic("Veteran behemoth greaves", 28878, 7),
				new Cosmetic("Veteran behemoth gauntlets", 28879, 9),
				new Cosmetic("Veteran behemoth boots", 28880, 10)),

		BEAST_ARMOUR(new Cosmetic("Beast helm", 28884, 0), new Cosmetic("Beast cuirass", 28885, 4),
				new Cosmetic("Beast greaves", 28886, 7), new Cosmetic("Beast gauntlets", 28887, 9),
				new Cosmetic("Beast boots", 28888, 10)),

		VETERAN_BEAST_ARMOUR(new Cosmetic("Veteran beast helm", 28891, 0),
				new Cosmetic("Veteran beast cuirass", 28892, 4), new Cosmetic("Veteran beast greaves", 28893, 7),
				new Cosmetic("Veteran beast gauntlets", 28894, 9), new Cosmetic("Veteran beast boots", 28895, 10)),

		LINZAS_OUTFIT(new Cosmetic("Linza's gloves", 28954, 9), new Cosmetic("Linza's boots", 28956, 10),
				new Cosmetic("Linza's leather vest", 28950, 4), new Cosmetic("Linza's apron", 28952, 7)),

		OWENS_ARMAMENTS(new Cosmetic("Owen's gauntlets", 28975, 9), new Cosmetic("Owen's boots", 28977, 10),
				new Cosmetic("Owen's cuirass", 28971, 4), new Cosmetic("Owen's cuisses", 28973, 7)),

		DERVISH_OUTFIT(new Cosmetic("Dervish hat", 29009, 0), new Cosmetic("Dervish boots", 29012, 10),
				new Cosmetic("Dervish robe", 29010, 4), new Cosmetic("Dervish legs", 29011, 7)),

		EASTERN_OUTFIT(new Cosmetic("Eastern hair", 29015, 0), new Cosmetic("Eastern sandals", 29018, 10),
				new Cosmetic("Eastern robes", 29016, 4), new Cosmetic("Eastern legs", 29017, 7)),

		TRIBAL_OUTFIT(new Cosmetic("Tribal circlet", 29021, 0), new Cosmetic("Tribal shoes", 29024, 10),
				new Cosmetic("Tribal top", 29022, 4), new Cosmetic("Tribal legs", 29023, 7)),

		SAMBA_OUTFIT(new Cosmetic("Samba headdress", 29027, 0), new Cosmetic("Samba sandals", 29030, 10),
				new Cosmetic("Samba top", 29028, 4), new Cosmetic("Samba loincloth", 29029, 7)),

		THEATRICAL_OUTFIT(new Cosmetic("Theatrical hat", 29033, 0), new Cosmetic("Theatrical shoes", 29036, 10),
				new Cosmetic("Theatrical tunic", 29034, 4), new Cosmetic("Theatrical legs", 29035, 7)),

		PHARAOHS_OUTFIT(new Cosmetic("Pharaoh's hat", 29039, 0), new Cosmetic("Pharaoh's sandals", 29042, 10),
				new Cosmetic("Pharaoh's top", 29040, 4), new Cosmetic("Pharaoh's shendyt", 29041, 7)),

		WUSHANKO_OUTFIT(new Cosmetic("Wushanko hat", 29045, 0), new Cosmetic("Wushanko shoes", 29048, 10),
				new Cosmetic("Wushanko top", 29046, 4), new Cosmetic("Wushanko legs", 29047, 7)),

		SILKEN_OUTFIT(new Cosmetic("Silken turban", 29051, 0), new Cosmetic("Silken boots", 29054, 10),
				new Cosmetic("Silken top", 29052, 4), new Cosmetic("Silken legs", 29053, 7)),

		COLONISTS_OUTFIT(new Cosmetic("Colonist's hat", 29057, 0), new Cosmetic("Colonist's boots", 29060, 10),
				new Cosmetic("Colonist's top", 29058, 4), new Cosmetic("Colonist's legs", 29059, 7)),

		HIGHLAND_OUTFIT(new Cosmetic("Highland war paint", 29063, 0), new Cosmetic("Highland boots", 29066, 10),
				new Cosmetic("Highland top", 29064, 4), new Cosmetic("Highland kilt", 29065, 7)),

		FEATHERED_SERPENT_OUTFIT(new Cosmetic("Feathered serpent headdress", 29069, 0),
				new Cosmetic("Feathered serpent boots", 29072, 10), new Cosmetic("Feathered serpent body", 29070, 4),
				new Cosmetic("Feathered serpent legs", 29071, 7)),

		MUSKETEER_OUTFIT(new Cosmetic("Musketeer hat", 29075, 0), new Cosmetic("Musketeer boots", 29078, 10),
				new Cosmetic("Musketeer top", 29076, 4), new Cosmetic("Musketeer legs", 29077, 7)),

		ELVEN_OUTFIT(new Cosmetic("Elven wig", 29081, 0), new Cosmetic("Elven shoes", 29084, 10),
				new Cosmetic("Elven top", 29082, 4), new Cosmetic("Elven legs", 29083, 7)),

		WEREWOLF_OUTFIT(new Cosmetic("Werewolf mask", 29087, 0), new Cosmetic("Werewolf torso", 29088, 4),
				new Cosmetic("Werewolf legs", 29090, 7), new Cosmetic("Werewolf claws", 29089, 9),
				new Cosmetic("Werewolf paws", 29091, 10)),

		AMBASSADOR_OF_ORDER_OUTFIT(new Cosmetic("Ambassador of Order circlet", 29561, 0),
				new Cosmetic("Ambassador of Order cloak", 29571, 1), new Cosmetic("Ambassador of Order robe", 29563, 4),
				new Cosmetic("Ambassador of Order robe", 29565, 7),
				new Cosmetic("Ambassador of Order gloves", 29567, 9),
				new Cosmetic("Ambassador of Order shoes", 29569, 10)),

		ENVOY_OF_ORDER_OUTFIT(new Cosmetic("Envoy of Order circlet", 29575, 0),
				new Cosmetic("Envoy of Order robe", 29577, 4), new Cosmetic("Envoy of Order robe", 29579, 7),
				new Cosmetic("Envoy of Order gloves", 29581, 9), new Cosmetic("Envoy of Order shoes", 29583, 10)),

		AMBASSADOR_OF_CHAOS(new Cosmetic("Ambassador of Chaos cap", 29587, 0),
				new Cosmetic("Ambassador of Chaos cloak", 29597, 1), new Cosmetic("Ambassador of Chaos robe", 29589, 4),
				new Cosmetic("Ambassador of Chaos robe", 29591, 7),
				new Cosmetic("Ambassador of Chaos gloves", 29593, 9),
				new Cosmetic("Ambassador of Chaos shoes", 29595, 10)),

		ENVOY_OF_CHAOS_OUTFIT(new Cosmetic("Envoy of Chaos cap", 29601, 0),
				new Cosmetic("Envoy of Chaos robe", 29603, 4), new Cosmetic("Envoy of Chaos robe", 29605, 7),
				new Cosmetic("Envoy of Chaos gloves", 29607, 9), new Cosmetic("Envoy of Chaos shoes", 29609, 10)),

		AURORA_ARMOUR(new Cosmetic("Aurora helm", 28428, 0), new Cosmetic("Aurora longsword", 28429, 3),
				new Cosmetic("Aurora cuirass", 28431, 4), new Cosmetic("Off-hand Aurora longsword", 28430, 5),
				new Cosmetic("Aurora greaves", 28432, 7), new Cosmetic("Aurora gauntlets", 28433, 9),
				new Cosmetic("Aurora boots", 28434, 10)),

		TEMPLAR_ARMOUR(new Cosmetic("Templar helm", 28941, 0), new Cosmetic("Templar cuirass", 28942, 4),
				new Cosmetic("Templar greaves", 28943, 7), new Cosmetic("Templar gauntlets", 28944, 9),
				new Cosmetic("Templar boots", 28945, 10)),

		SUPERHERO_OUTFIT(new Cosmetic("Superhero mask", 29421, 0), new Cosmetic("Superhero cape", 29424, 1),
				new Cosmetic("Superhero top", 29419, 4), new Cosmetic("Superhero legs", 29420, 7)),

		SUPERIOR_HERO_OUTFIT(new Cosmetic("Superior hero mask", 29431, 0), new Cosmetic("Superior hero cape", 29436, 1),
				new Cosmetic("Superior hero top", 29432, 4), new Cosmetic("Superior hero legs", 29433, 7)),

		DULCIN_ARMOUR(new Cosmetic("Dulcin helm", 29461, 0), new Cosmetic("Dulcin cuirass", 29462, 4),
				new Cosmetic("Dulcin gown", 29463, 7), new Cosmetic("Dulcin gauntlets", 29464, 9),
				new Cosmetic("Dulcin boots", 29465, 10)),

		RAVENSKULL_OUTFIT(new Cosmetic("Ravenskull cowl", 29766, 0), new Cosmetic("Ravenskull talon", 29776, 3),
				new Cosmetic("Ravenskull raiment", 29768, 4), new Cosmetic("Ravenskull talon Off-hand", 29778, 5),
				new Cosmetic("Ravenskull garb", 29770, 7), new Cosmetic("Ravenskull bracers", 29772, 9),
				new Cosmetic("Ravenskull treads", 29774, 10)),

		DEATHLESS_REGENT_OUTFIT(new Cosmetic("Deathless Regent headguard", 29782, 0),
				new Cosmetic("Deathless Regent shroud", 29792, 1), new Cosmetic("Pain", 29794, 3),
				new Cosmetic("Deathless Regent breastplate", 29784, 4), new Cosmetic("Agony Off-hand", 29796, 5),
				new Cosmetic("Deathless Regent legplates", 29786, 7),
				new Cosmetic("Deathless Regent gauntlets", 29788, 9),
				new Cosmetic("Deathless Regent boots", 29790, 10)),

		ANCIENT_MUMMY_OUTFIT(new Cosmetic("Ancient mummy burial mask", 29958, 0),
				new Cosmetic("Ancient mummy wraps", 29962, 4), new Cosmetic("Ancient mummy greaves", 29960, 7),
				new Cosmetic("Ancient mummy handguards", 29964, 9), new Cosmetic("Ancient mummy sandals", 29966, 10)),

		OGRE_INFILTRATOR_OUTFIT(new Cosmetic("Ogre infiltrator mask", 29976, 0),
				new Cosmetic("Ogre infiltrator sword-club", 29986, 3), new Cosmetic("Ogre infiltrator torso", 29978, 4),
				new Cosmetic("Ogre infiltrator stubs", 29980, 7), new Cosmetic("Ogre infiltrator hands", 29982, 9),
				new Cosmetic("Ogre infiltrator stompers", 29984, 10)),

		DRAKEWING_OUTFIT(new Cosmetic("Drakewing head", 29990, 0), new Cosmetic("Drake wings", 30000, 11),
				new Cosmetic("Drakewing staff", 30002, 3), new Cosmetic("Drakewing torso", 29992, 4),
				new Cosmetic("Drakewing legs", 29994, 7), new Cosmetic("Drakewing claws", 29996, 9),
				new Cosmetic("Drakewing feet", 29998, 10)),

		REPLICA_INFINITY_ROBES(new Cosmetic("Replica infinity hat", 30147, 0),
				new Cosmetic("Replica infinity top", 30149, 4), new Cosmetic("Replica infinity bottoms", 30151, 7),
				new Cosmetic("Replica infinity gloves", 30153, 9), new Cosmetic("Replica infinity boots", 30155, 10)),

		REPLICA_INFINITY_ROBES_AIR(new Cosmetic("Replica infinity hat (air)", 30159, 0),
				new Cosmetic("Replica infinity top (air)", 30161, 4),
				new Cosmetic("Replica infinity bottoms (air)", 30163, 7),
				new Cosmetic("Replica infinity gloves", 30153, 9), new Cosmetic("Replica infinity boots", 30155, 10)),

		REPLICA_INFINITY_ROBES_EARTH(new Cosmetic("Replica infinity hat (earth)", 30167, 0),
				new Cosmetic("Replica infinity top (earth)", 30169, 4),
				new Cosmetic("Replica infinity bottoms (earth)", 30171, 7),
				new Cosmetic("Replica infinity gloves", 30153, 9), new Cosmetic("Replica infinity boots", 30155, 10)),

		REPLICA_INFINITY_ROBES_FIRE(new Cosmetic("Replica infinity hat (fire)", 30175, 0),
				new Cosmetic("Replica infinity top (fire)", 30177, 4),
				new Cosmetic("Replica infinity bottoms (fire)", 30179, 7),
				new Cosmetic("Replica infinity gloves", 30153, 9), new Cosmetic("Replica infinity boots", 30155, 10)),

		REPLICA_INFINITY_ROBES_WATER(new Cosmetic("Replica infinity hat (water)", 30183, 0),
				new Cosmetic("Replica infinity top (water)", 30185, 4),
				new Cosmetic("Replica infinity bottoms (water)", 30187, 7),
				new Cosmetic("Replica infinity gloves", 30153, 9), new Cosmetic("Replica infinity boots", 30155, 10)),

		REPLICA_DRAGON_PLATE_ARMOUR(new Cosmetic("Replica dragon full helm", 30191, 0),
				new Cosmetic("Replica dragon platebody", 30193, 4), new Cosmetic("Replica dragon platelegs", 30194, 7),
				new Cosmetic("Replica dragon gauntlets", 30195, 9), new Cosmetic("Replica dragon boots", 30196, 10)),

		REPLICA_DRAGON_PLATE_ARMOUR_SP(new Cosmetic("Replica dragon full helm (sp)", 30199, 0),
				new Cosmetic("Replica dragon platebody (sp)", 30201, 4),
				new Cosmetic("Replica dragon platelegs (sp)", 30202, 7),
				new Cosmetic("Replica dragon gauntlets", 30195, 9), new Cosmetic("Replica dragon boots", 30196, 10)),

		REPLICA_DRAGON_PLATE_ARMOUR_OR(new Cosmetic("Replica dragon full helm (or)", 30205, 0),
				new Cosmetic("Replica dragon platebody (or)", 30207, 4),
				new Cosmetic("Replica dragon platelegs (or)", 30208, 7),
				new Cosmetic("Replica dragon gauntlets", 30195, 9), new Cosmetic("Replica dragon boots", 30196, 10)),

		FROSTWALKER_OUTFIT(new Cosmetic("Frostwalker hood", 30417, 0), new Cosmetic("Frostwalker cape", 30427, 1),
				new Cosmetic("Frostwalker tunic", 30419, 4), new Cosmetic("Frostwalker leggings", 30421, 7),
				new Cosmetic("Frostwalker gloves", 30423, 9), new Cosmetic("Frostwalker boots", 30425, 10)),

		GLAD_TIDINGS_OUTFIT(new Cosmetic("Glad tidings headpiece", 30433, 0),
				new Cosmetic("Glad tidings cape", 30445, 1), new Cosmetic("Glad tidings shirt", 30437, 4),
				new Cosmetic("Glad tidings bottoms", 30439, 7), new Cosmetic("Glad tidings handwrap", 30441, 9),
				new Cosmetic("Glad tidings boots", 30443, 10)),

		GOLEM_OF_STRENGTH_ARMOUR(new Cosmetic("Golem of Strength helm", 30461, 0),
				new Cosmetic("Golem of Strength cape", 30466, 1),
				new Cosmetic("Golem of Strength chestplate", 30462, 4),
				new Cosmetic("Golem of Strength legplates", 30463, 7),
				new Cosmetic("Golem of Strength gauntlets", 30464, 9),
				new Cosmetic("Golem of Strength boots", 30465, 10)),

		CONSTRUCT_OF_STRENGTH_ARMOUR(new Cosmetic("Construct of Strength helm", 30469, 0),
				new Cosmetic("Construct of Strength chestplate", 30470, 4),
				new Cosmetic("Construct of Strength legplates", 30471, 7),
				new Cosmetic("Construct of Strength gauntlets", 30472, 9),
				new Cosmetic("Construct of Strength boots", 30473, 10)),

		GOLEM_OF_JUSTICE_ARMOUR(new Cosmetic("Golem of Justice helm", 30476, 0),
				new Cosmetic("Golem of Justice cape", 30481, 1), new Cosmetic("Golem of Justice chestplate", 30477, 4),
				new Cosmetic("Golem of Justice legplates", 30478, 7),
				new Cosmetic("Golem of Justice gauntlets", 30480, 9),
				new Cosmetic("Golem of Justice boots", 30479, 10)),

		CONSTRUCT_OF_JUSTICE_ARMOUR(new Cosmetic("Construct of Justice helm", 30484, 0),
				new Cosmetic("Construct of Justice chestplate", 30485, 4),
				new Cosmetic("Construct of Justice legplates", 30487, 7),
				new Cosmetic("Construct of Justice gauntlets", 30486, 9),
				new Cosmetic("Construct of Justice boots", 30488, 10)),

		BLESSED_SENTINEL_OUTFIT(new Cosmetic("Blessed Sentinel hood", 30597, 0),
				new Cosmetic("Blessed Sentinel wings", 30602, 11), new Cosmetic("Blessed Sentinel lance", 30603, 3),
				new Cosmetic("Blessed Sentinel cuirass", 30598, 4), new Cosmetic("Blessed Sentinel robe", 30599, 7),
				new Cosmetic("Blessed Sentinel gloves", 30601, 9), new Cosmetic("Blessed Sentinel boots", 30600, 10)),

		CURSED_REAVER_OUTFIT(new Cosmetic("Cursed Reaver cowl", 30606, 0),
				new Cosmetic("Cursed Reaver wings", 30611, 11), new Cosmetic("Cursed Reaver scythe", 30612, 3),
				new Cosmetic("Cursed Reaver garb", 30607, 4), new Cosmetic("Cursed Reaver garb", 30608, 7),
				new Cosmetic("Cursed Reaver grasps", 30610, 9), new Cosmetic("Cursed Reaver boots", 30609, 10)),

		REPLICA_VIRTUS_OUTFIT(new Cosmetic("Replica Virtus mask", 30617, 0),
				new Cosmetic("Replica Virtus robe top", 30618, 4), new Cosmetic("Replica Virtus robe legs", 30619, 7)),

		REPLICA_TORVA_OUTFIT(new Cosmetic("Replica Torva full helm", 30622, 0),
				new Cosmetic("Replica Torva platebody", 30623, 4), new Cosmetic("Replica Torva platelegs", 30624, 7)),

		REPLICA_PERNIX_OUTFIT(new Cosmetic("Replica Pernix cowl", 30627, 0),
				new Cosmetic("Replica Pernix body", 30628, 4), new Cosmetic("Replica Pernix chaps", 30629, 7)),

		CLOWN_COSTUME(new Cosmetic("Clown hat", 30952, 0), new Cosmetic("Tambourine", 30956, 3),
				new Cosmetic("Clown shirt", 30953, 4), new Cosmetic("Clown leggings", 30954, 7),
				new Cosmetic("Clown shoes", 30955, 10)),

		RINGMASTER_COSTUME(new Cosmetic("Ringmaster hat", 30959, 0), new Cosmetic("Mega-phonus", 30963, 3),
				new Cosmetic("Ringmaster shirt", 30960, 4), new Cosmetic("Ringmaster pants", 30961, 7),
				new Cosmetic("Ringmaster boots", 30962, 10)),

		ACROBAT_COSTUME(new Cosmetic("Acrobat hat", 30967, 0), new Cosmetic("Acrobat shoes", 30966, 10),
				new Cosmetic("Acrobat shirt", 30964, 4), new Cosmetic("Acrobat pants", 30965, 7)),

		AUDIENCE_OUTFIT(new Cosmetic("Audience hat", 30974, 0), new Cosmetic("Audience shoes", 30986, 10),
				new Cosmetic("Audience shirt", 30980, 4), new Cosmetic("Audience pants", 30983, 7)),

		SPECTATORS_OUTFIT(new Cosmetic("Spectator's hat", 30976, 0), new Cosmetic("Spectator's shoes", 30987, 10),
				new Cosmetic("Spectator's shirt", 30981, 4), new Cosmetic("Spectator's pants", 30984, 7)),

		CIRCUS_GOERS_OUTFIT(new Cosmetic("Circus goer's hat", 30978, 0), new Cosmetic("Circus goer's shirt", 30982, 4),
				new Cosmetic("Circus goer's pants", 30985, 7)),

		SHADOW_ARIANE_OUTFIT(new Cosmetic("Shadow Ariane's tiara", 31128, 0),
				new Cosmetic("Shadow Ariane's robe top", 31129, 4),
				new Cosmetic("Shadow Ariane's robe bottom", 31130, 7),
				new Cosmetic("Shadow Ariane's bracers", 31131, 9), new Cosmetic("Shadow Ariane's boots", 31132, 10)),

		SHADOW_OZAN_OUTFIT(new Cosmetic("Shadow Ozan's cape", 31139, 1), new Cosmetic("Shadow Ozan's tunic", 31135, 4),
				new Cosmetic("Shadow Ozan's breeches", 31136, 7), new Cosmetic("Shadow Ozan's gloves", 31137, 9),
				new Cosmetic("Shadow Ozan's boots", 31138, 10)),

		SHADOW_LINZA_OUTFIT(new Cosmetic("Shadow Linza's gloves", 31113, 9),
				new Cosmetic("Shadow Linza's boots", 31114, 10), new Cosmetic("Shadow Linza's leather vest", 31111, 4),
				new Cosmetic("Shadow Linza's apron", 31112, 7)),

		SHADOW_OWEN_OUTFIT(new Cosmetic("Shadow Owen's gauntlets", 31120, 9),
				new Cosmetic("Shadow Owen's boots", 31121, 10), new Cosmetic("Shadow Owen's cuirass", 31118, 4),
				new Cosmetic("Shadow Owen's cuisses", 31119, 7)),

		REPLICA_METAL_PLATE_ARMOUR(new Cosmetic("Replica full helm", 31211, 0),
				new Cosmetic("Replica platebody", 31212, 4), new Cosmetic("Replica platelegs", 31213, 7),
				new Cosmetic("Replica gloves", 31216, 9), new Cosmetic("Replica boots", 31217, 10)),

		REPLICA_METAL_PLATE_ARMOUR_T(new Cosmetic("Replica full helm (t)", 31219, 0),
				new Cosmetic("Replica platebody (t)", 31220, 4), new Cosmetic("Replica platelegs (t)", 31221, 7)),

		REPLICA_METAL_PLATE_ARMOUR_G(new Cosmetic("Replica full helm (g)", 31225, 0),
				new Cosmetic("Replica platebody (g)", 31226, 4), new Cosmetic("Replica platelegs (g)", 31227, 7)),

		REPLICA_ARMADYL_ARMOUR(new Cosmetic("Replica Armadyl helmet", 31232, 0),
				new Cosmetic("Replica Armadyl chestplate", 31233, 4),
				new Cosmetic("Replica Armadyl chainskirt", 31234, 7)),

		REPLICA_BANDOS_ARMOUR(new Cosmetic("Replica Bandos chestplate", 31237, 4),
				new Cosmetic("Replica Bandos boots", 31239, 10), new Cosmetic("Replica Bandos tassets", 31238, 7)),

		HIKER_COSTUME(new Cosmetic("Hiker cap", 31296, 0), new Cosmetic("Hiker backpack", 31300, 1),
				new Cosmetic("Hiker jacket", 31297, 4), new Cosmetic("Hiker leggings", 31298, 7),
				new Cosmetic("Hiker gloves", 31301, 9), new Cosmetic("Hiker boots", 31299, 10)),

		AVIANSIE_SKYGUARD_OUTFIT(new Cosmetic("Skyguard head", 31536, 0), new Cosmetic("Skyguard wings", 31543, 11),
				new Cosmetic("Skyguard longbow", 31541, 3), new Cosmetic("Skyguard mail", 31537, 4),
				new Cosmetic("Skyguard tassets", 31538, 7), new Cosmetic("Skyguard gauntlets", 31540, 9),
				new Cosmetic("Skyguard talons", 31539, 10)),

		VYREWATCH_SKYSHADOW_OUTFIT(new Cosmetic("Skyshadow head", 31546, 0), new Cosmetic("Skyshadow wings", 31551, 11),
				new Cosmetic("Skyshadow staff", 31552, 3), new Cosmetic("Skyshadow vest", 31547, 4),
				new Cosmetic("Skyshadow bottoms", 31548, 7), new Cosmetic("Skyshadow wristwraps", 31549, 9),
				new Cosmetic("Skyshadow boots", 31550, 10)),

		REPLICA_VOID_KNIGHT_ARMOUR(new Cosmetic("Replica Void Knight top", 31698, 4),
				new Cosmetic("Replica Void Knight bottom", 31699, 7)),

		REPLICA_VOID_KNIGHT_EQUIPMENT(new Cosmetic("Replica Void Knight deflector", 31709, 5),
				new Cosmetic("Replica Void Knight gloves", 31711, 9),
				new Cosmetic("Replica Void Knight seal", 31710, 2)),

		VANQUISHERS_GEAR(new Cosmetic("Vanquisher's skull", 31833, 0), new Cosmetic("Vanquisher's cuirass", 31834, 4),
				new Cosmetic("Vanquisher's cuisses", 31835, 7), new Cosmetic("Vanquisher's gauntlets", 31837, 9),
				new Cosmetic("Vanquisher's greaves", 31836, 10)),

		ZAROSIAN_SHADOW_OUTFIT(new Cosmetic("Zarosian shadow hood", 31975, 0),
				new Cosmetic("Zarosian shadow robe", 31976, 4), new Cosmetic("Zarosian shadow robe", 31977, 7),
				new Cosmetic("Zarosian shadow gauntlets", 31978, 9), new Cosmetic("Zarosian shadow boots", 31979, 10)),

		ZAROSIAN_PRAETOR_OUTFIT(new Cosmetic("Zarosian praetor mask", 31982, 0),
				new Cosmetic("Zarosian praetor splint mail", 31983, 4), new Cosmetic("Zarosian praetor robe", 31984, 7),
				new Cosmetic("Zarosian praetor gauntlets", 31985, 9),
				new Cosmetic("Zarosian praetor boots", 31986, 10)),

		ELVEN_WARRIOR_OUTFIT(new Cosmetic("Elven warrior helmet", 32315, 0),
				new Cosmetic("Elven warrior armour", 32316, 4), new Cosmetic("Elven warrior tassets", 32317, 7),
				new Cosmetic("Elven warrior gauntlets", 32319, 9), new Cosmetic("Elven warrior boots", 32318, 10)),

		ELVEN_RANGER_OUTFIT(new Cosmetic("Elven ranger helmet", 32322, 0),
				new Cosmetic("Elven ranger armour", 32323, 4), new Cosmetic("Elven ranger tassets", 32324, 7),
				new Cosmetic("Elven ranger gloves", 32326, 9), new Cosmetic("Elven ranger boots", 32325, 10)),

		ELVEN_MAGE_OUTFIT(new Cosmetic("Elven mage helmet", 32329, 0), new Cosmetic("Elven mage robe", 32330, 4),
				new Cosmetic("Elven mage robe", 32331, 7), new Cosmetic("Elven mage gloves", 32333, 9),
				new Cosmetic("Elven mage boots", 32332, 10)),

		NEX_OUTFIT(new Cosmetic("Nex helmet", 32543, 0), new Cosmetic("Nex tail", 32549, 1),
				new Cosmetic("Nex wings", 32548, 11), new Cosmetic("Nex armour", 32544, 4),
				new Cosmetic("Nex legs", 32545, 7), new Cosmetic("Nex gauntlets", 32547, 9),
				new Cosmetic("Nex boots", 32546, 10)),

		ATTUNED_NEX_OUTFIT(new Cosmetic("Attuned Nex head", 32551, 0), new Cosmetic("Attuned Nex tail", 32557, 1),
				new Cosmetic("Attuned Nex wings", 32556, 11), new Cosmetic("Attuned Nex armour", 32552, 4),
				new Cosmetic("Attuned Nex legs", 32553, 7), new Cosmetic("Attuned Nex gloves", 32555, 9),
				new Cosmetic("Attuned Nex boots", 32554, 10)),

		KING_BLACK_DRAGON_OUTFIT(new Cosmetic("King Black Dragon head", 32560, 0),
				new Cosmetic("King Black Dragon tail", 32566, 1), new Cosmetic("King Black Dragon wings", 32565, 11),
				new Cosmetic("King Black Dragon armour", 32561, 4), new Cosmetic("King Black Dragon legs", 32562, 7),
				new Cosmetic("King Black Dragon gloves", 32564, 9), new Cosmetic("King Black Dragon boots", 32563, 10)),

		ATTUNED_KING_BLACK_DRAGON_OUTFIT(new Cosmetic("Attuned King Black Dragon head", 32568, 0),
				new Cosmetic("Attuned King Black Dragon tail", 32574, 1),
				new Cosmetic("Attuned King Black Dragon wings", 32573, 11),
				new Cosmetic("Attuned King Black Dragon armour", 32569, 4),
				new Cosmetic("Attuned King Black Dragon legs", 32570, 7),
				new Cosmetic("Attuned King Black Dragon gauntlets", 32572, 9),
				new Cosmetic("Attuned King Black Dragon boots", 32571, 10)),

		SNOWMAN_COSTUME(new Cosmetic("Snowman head", 33593, 0), new Cosmetic("Snowman boots", 33633, 10),
				new Cosmetic("Ice sickle", 33597, 3), new Cosmetic("Snowman body", 33594, 4),
				new Cosmetic("Snowstormer", 33596, 14), new Cosmetic("Snowman legs", 33595, 7)),

		SAMURAI_OUTFIT(new Cosmetic("Samurai helmet", 33637, 0), new Cosmetic("Samurai flag", 33642, 1),
				new Cosmetic("Samurai armour", 33638, 4), new Cosmetic("Samurai tassets", 33639, 7),
				new Cosmetic("Samurai gauntlets", 33641, 9), new Cosmetic("Samurai boots", 33640, 10)),

		OUTFIT_OF_OMENS(-1, new Cosmetic("Helm of Omens", 33711, 0), new Cosmetic("Maul of Omens", 33709, 3),
				new Cosmetic("Torso of Omens", 33712, 4), new Cosmetic("Legs of Omens", 33713, 7),
				new Cosmetic("Gloves of Omens", 33715, 9), new Cosmetic("Boots of Omens", 33714, 10)),

		REPLICA_AHRIMS_OUTFIT(new Cosmetic("Replica Ahrim's hood", 33674, 0),
				new Cosmetic("Replica Ahrim's staff", 33677, 3), new Cosmetic("Replica Ahrim's robe top", 33675, 4),
				new Cosmetic("Replica Ahrim's robe skirt", 33676, 7)),

		REPLICA_DHAROKS_OUTFIT(new Cosmetic("Replica Dharok's helm", 33680, 0),
				new Cosmetic("Replica Dharok's greataxe", 33683, 3),
				new Cosmetic("Replica Dharok's platebody", 33681, 4),
				new Cosmetic("Replica Dharok's platelegs", 33682, 7)),

		REPLICA_GUTHANS_OUTFIT(new Cosmetic("Replica Guthan's helm", 33686, 0),
				new Cosmetic("Replica Guthan's warspear", 33689, 3),
				new Cosmetic("Replica Guthan's platebody", 33687, 4),
				new Cosmetic("Replica Guthan's chainskirt", 33688, 7)),

		REPLICA_KARILS_OUTFIT(new Cosmetic("Replica Karil's coif", 33692, 0),
				new Cosmetic("Replica Karil's crossbow", 33695, 3), new Cosmetic("Replica Karil's top", 33693, 4),
				new Cosmetic("Replica Karil's skirt", 33694, 7)),

		REPLICA_TORAGS_OUTFIT(new Cosmetic("Replica Torag's helm", 33698, 0),
				new Cosmetic("Replica Torag's hammer", 33701, 3), new Cosmetic("Replica Torag's platebody", 33699, 4),
				new Cosmetic("Replica Torag's hammer Off-hand", 33702, 5),
				new Cosmetic("Replica Torag's platelegs", 33700, 7)),

		REPLICA_VERACS_OUTFIT(new Cosmetic("Replica Verac's helm", 33705, 0),
				new Cosmetic("Replica Verac's flail", 33708, 3), new Cosmetic("Replica Verac's brassard", 33706, 4),
				new Cosmetic("Replica Verac's plateskirt", 33707, 7)),

		WARM_WINTER_OUTFIT(new Cosmetic("Warm winter hood", 33755, 0), new Cosmetic("Warm winter coat", 33756, 4),
				new Cosmetic("Warm winter coat bottom", 33757, 7), new Cosmetic("Warm winter gloves", 33759, 9),
				new Cosmetic("Warm winter boots", 33758, 10)),

		NOMADS_OUTFIT(new Cosmetic("Nomad's gorget", 34099, 0), new Cosmetic("Nomad's cape", 34104, 1),
				new Cosmetic("Nomad's spear", 34105, 3), new Cosmetic("Nomad's chestplate", 34100, 4),
				new Cosmetic("Nomad's tassets", 34101, 7), new Cosmetic("Nomad's gloves", 34103, 9),
				new Cosmetic("Nomad's boots", 34102, 10)),

		GAMEBLAST_OUTFIT(new Cosmetic("GameBlast bandana", 34114, 0), new Cosmetic("GameBlast cape", 34119, 1),
				new Cosmetic("GameBlast amulet", 34120, 2), new Cosmetic("GameBlast torso", 34115, 4),
				new Cosmetic("GameBlast legs", 34116, 7), new Cosmetic("GameBlast gloves", 34118, 9),
				new Cosmetic("GameBlast boots", 34117, 10)),

		DARK_LORD_OUTFIT(new Cosmetic("Dark Lord head", 34222, 0), new Cosmetic("Dark Lord body", 34223, 4),
				new Cosmetic("Dark Lord legs", 34224, 7), new Cosmetic("Dark Lord hands", 34226, 9),
				new Cosmetic("Dark Lord feet", 34225, 10)),

		NEW_VARROCK_CULTIST_ROBES(new Cosmetic("New Varrock cultist hood", 34319, 0),
				new Cosmetic("New Varrock cultist top", 34320, 4), new Cosmetic("New Varrock cultist robes", 34321, 7)),

		NEW_VARROCK_ZOMBIE_OUTFIT(new Cosmetic("New Varrock zombie head", 34323, 0),
				new Cosmetic("New Varrock zombie torso", 34324, 4), new Cosmetic("New Varrock zombie legs", 34325, 7),
				new Cosmetic("New Varrock zombie hands", 34326, 9), new Cosmetic("New Varrock zombie feet", 34327, 10)),

		NEW_VARROCK_ARRAV_OUTFIT(new Cosmetic("New Varrock Arrav's helm", 34329, 0),
				new Cosmetic("New Varrock Arrav's boots", 34332, 10),
				new Cosmetic("New Varrock Arrav's heart", 34330, 4),
				new Cosmetic("New Varrock Arrav's trousers", 34331, 7)),

		ARRAV_OUTFIT(new Cosmetic("Arrav Helm", 34304, 0), new Cosmetic("Arrav Chest", 34305, 4),
				new Cosmetic("Arrav Legs", 34306, 7), new Cosmetic("Arrav Hands", 34308, 9),
				new Cosmetic("Arrav Boots", 34307, 10)),

		CURSED_ARRAV_OUTFIT(new Cosmetic("Cursed Arrav Helm", 34310, 0), new Cosmetic("Cursed Arrav Chest", 34311, 4),
				new Cosmetic("Cursed Arrav Legs", 34312, 7), new Cosmetic("Cursed Arrav Hands", 34314, 9),
				new Cosmetic("Cursed Arrav Boots", 34313, 10)),

		VITALITY_SUIT(new Cosmetic("Vitality Helmet", 34535, 0), new Cosmetic("Vitality Chest", 34536, 4),
				new Cosmetic("Vitality Legs", 34537, 7), new Cosmetic("Vitality Gloves", 34539, 9),
				new Cosmetic("Vitality Feet", 34538, 10)),

		VITALITY_SUIT_INACTIVE(new Cosmetic("Vitality Helmet (inactive)", 34948, 0),
				new Cosmetic("Vitality Chest (inactive)", 34949, 4), new Cosmetic("Vitality Legs (inactive)", 34950, 7),
				new Cosmetic("Vitality Gloves (inactive)", 34952, 9),
				new Cosmetic("Vitality Feet (inactive)", 34951, 10)),

		AQUARIUM_DIVING_SUIT(new Cosmetic("Oyster hunter helmet", 34678, 0),
				new Cosmetic("Diving suit torso", 34681, 4), new Cosmetic("Diving suit legs", 34682, 7),
				new Cosmetic("Diving suit gloves", 34684, 9), new Cosmetic("Diving suit boots", 34683, 10)),

		LAVA_OUTFIT(new Cosmetic("Lava Gloves", 34813, 9), new Cosmetic("Lava Boots", 34814, 10),
				new Cosmetic("Lava Top", 34811, 4), new Cosmetic("Lava Legs", 34812, 7)),

		COGWHEEL(new Cosmetic("Cogwheel Helmet", 34790, 0), new Cosmetic("Cogwheel Chest", 34791, 4),
				new Cosmetic("Cogwheel Legs", 34792, 7), new Cosmetic("Cogwheel Gloves", 34794, 9),
				new Cosmetic("Cogwheel Boots", 34793, 10)),

		KETHSI_OUTFIT(new Cosmetic("Kethsi helmet", 34999, 0), new Cosmetic("Kethsi chestplate", 35000, 4),
				new Cosmetic("Kethsi robes", 35001, 7), new Cosmetic("Kethsi bracers", 35002, 9),
				new Cosmetic("Kethsi boots", 35003, 10)),

		SWIMMING_OUTFIT(new Cosmetic("Swim top", 35100, 4), new Cosmetic("Swim bottoms", 35101, 7)),

		VS_OUTFIT(new Cosmetic("V's Helmet", 35231, 0), new Cosmetic("V's Cape", 35236, 1),
				new Cosmetic("V's Broadsword", 35237, 3), new Cosmetic("V's Chestpiece", 35232, 4),
				new Cosmetic("V's Legguards", 35233, 7), new Cosmetic("V's Gauntlets", 35235, 9),
				new Cosmetic("V's Sabatons", 35234, 10)),

		BARBARIAN_OUTFIT(new Cosmetic("Barbarian helmet", 35369, 0), new Cosmetic("Barbarian cuirass", 35370, 4),
				new Cosmetic("Barbarian greaves", 35371, 7), new Cosmetic("Barbarian cuffs", 35373, 9),
				new Cosmetic("Barbarian boots", 35372, 10)),

		FAREED_OUTFIT(new Cosmetic("Fareed helm", 35673, 0), new Cosmetic("Fareed top", 35674, 4),
				new Cosmetic("Fareed bottoms", 35675, 7), new Cosmetic("Fareed gauntlets", 35676, 9),
				new Cosmetic("Fareed boots", 35677, 10)),

		KAMIL_OUTFIT(new Cosmetic("Kamil helm", 35679, 0), new Cosmetic("Kamil top", 35680, 4),
				new Cosmetic("Kamil bottoms", 35681, 7), new Cosmetic("Kamil gauntlets", 35682, 9),
				new Cosmetic("Kamil boots", 35683, 10)),

		SPIRIT_HUNTER_OUTFIT(new Cosmetic("Spirit hunter helmet", 35842, 0),
				new Cosmetic("Spirit hunter flesh jacket", 35843, 4), new Cosmetic("Spirit hunter bottoms", 35844, 7),
				new Cosmetic("Spirit hunter cuffs", 35846, 9), new Cosmetic("Spirit hunter greaves", 35845, 10)),

		REVENANT_OUTFIT(new Cosmetic("Revenant helmet", 35853, 0), new Cosmetic("Revenant cuirass", 35854, 4),
				new Cosmetic("Revenant greaves", 35855, 7), new Cosmetic("Revenant cuffs", 35857, 9),
				new Cosmetic("Revenant boots", 35856, 10)),

		COUNT_DRAYNOR_OUTFIT(new Cosmetic("Count Draynor cape", 35949, 1), new Cosmetic("Count Draynor Top", 35947, 4),
				new Cosmetic("Count Draynor bottoms", 35948, 7), new Cosmetic("Count Draynor hands", 35950, 9),
				new Cosmetic("Count Draynor shoes", 35951, 10)),

		RAPTORS_BASIC_OUTFIT(new Cosmetic("Raptor's Basic Helmet", 36026, 0),
				new Cosmetic("Raptor's Basic Chestpiece", 36027, 4), new Cosmetic("Raptor's Basic Shield", 36031, 5),
				new Cosmetic("Raptor's Basic Legguards", 36028, 7), new Cosmetic("Raptor's Basic Gauntlets", 36030, 9),
				new Cosmetic("Raptor's Basic Sabatons", 36029, 10)),

		RAPTORS_ADVANCED_OUTFIT(new Cosmetic("Raptor's Advanced Helmet", 36035, 0),
				new Cosmetic("Raptor's Advanced Chestpiece", 36036, 4),
				new Cosmetic("Raptor's Advanced Shield", 36040, 5),
				new Cosmetic("Raptor's Advanced Legguards", 36037, 7),
				new Cosmetic("Raptor's Advanced Gauntlets", 36039, 9),
				new Cosmetic("Raptor's Advanced Sabatons", 36038, 10)),

		MAHJARRAT_OUTFIT(new Cosmetic("Mahjarrat Head", 36148, 0), new Cosmetic("Mahjarrat Torso", 36140, 4),
				new Cosmetic("Mahjarrat Legs", 36144, 7), new Cosmetic("Mahjarrat Hands", 36142, 9),
				new Cosmetic("Mahjarrat Feet", 36146, 10)),

		MAHJARRAT_SKELETAL_OUTFIT(new Cosmetic("Mahjarrat Skeletal Head", 36150, 0),
				new Cosmetic("Mahjarrat Torso", 36140, 4), new Cosmetic("Mahjarrat Legs", 36144, 7),
				new Cosmetic("Mahjarrat Hands", 36142, 9), new Cosmetic("Mahjarrat Feet", 36146, 10)),

		SHADOW_DRAGOON_OUTFIT(new Cosmetic("Shadow Dragoon Helm", 36344, 0), new Cosmetic("Fury's Remorse", 36349, 3),
				new Cosmetic("Shadow Dragoon Chestplate", 36345, 4), new Cosmetic("Shadow Dragoon Legplates", 36346, 7),
				new Cosmetic("Shadow Dragoon Gauntlets", 36348, 9), new Cosmetic("Shadow Dragoon Boots", 36347, 10)),

		GROTESQUE_ARMOUR_TIER_1(new Cosmetic("Grotesque Helm (tier 1)", 36739, 0),
				new Cosmetic("Grotesque Cape (tier 1)", 36744, 1), new Cosmetic("Grotesque Wings (tier 1)", 36745, 11),
				new Cosmetic("Grotesque Chest (tier 1)", 36740, 4), new Cosmetic("Grotesque Legs (tier 1)", 36741, 7),
				new Cosmetic("Grotesque Gloves (tier 1)", 36742, 9),
				new Cosmetic("Grotesque Boots (tier 1)", 36743, 10)),

		GROTESQUE_ARMOUR_TIER_2(new Cosmetic("Grotesque Helm (tier 2)", 36746, 0),
				new Cosmetic("Grotesque Cape (tier 2)", 36751, 1), new Cosmetic("Grotesque Wings (tier 2)", 36752, 11),
				new Cosmetic("Grotesque Chest (tier 2)", 36747, 4), new Cosmetic("Grotesque Legs (tier 2)", 36748, 7),
				new Cosmetic("Grotesque Gloves (tier 2)", 36749, 9),
				new Cosmetic("Grotesque Boots (tier 2)", 36750, 10)),

		GROTESQUE_ARMOUR_TIER_3(new Cosmetic("Grotesque Helm (tier 3)", 36753, 0),
				new Cosmetic("Grotesque Cape (tier 3)", 36758, 1), new Cosmetic("Grotesque Wings (tier 3)", 36759, 11),
				new Cosmetic("Grotesque Chest (tier 3)", 36754, 4), new Cosmetic("Grotesque Legs (tier 3)", 36755, 7),
				new Cosmetic("Grotesque Gloves (tier 3)", 36756, 9),
				new Cosmetic("Grotesque Boots (tier 3)", 36757, 10)),

		MASQUERADE_OUTFIT(new Cosmetic("Masquerade Head", 36782, 0), new Cosmetic("Masquerade Top", 36786, 4),
				new Cosmetic("Masquerade Bottoms", 36783, 7), new Cosmetic("Masquerade Gloves", 36784, 9),
				new Cosmetic("Masquerade Boots", 36785, 10)),

		ELITE_MAMMOTH_ARMOUR(new Cosmetic("Elite Mammoth Helmet", 36883, 0),
				new Cosmetic("Elite Mammoth Two Hand Tusk Sword", 36888, 3),
				new Cosmetic("Elite Mammoth Torso", 36884, 4), new Cosmetic("Elite Mammoth Legs", 36885, 7),
				new Cosmetic("Elite Mammoth Gauntlets", 36887, 9), new Cosmetic("Elite Mammoth Boots", 36886, 10)),

		ANCIENT_OUTFIT(new Cosmetic("Ancient Outfit", 36902, 0), new Cosmetic("Ancient Robe Top", 36905, 4),
				new Cosmetic("Ancient Robe Bottom", 36906, 7), new Cosmetic("Ancient Cuffs", 36907, 9),
				new Cosmetic("Ancient Shoes", 36908, 10)),

		ROGUE_OUTFIT(new Cosmetic("Rogue Hood", 36947, 0), new Cosmetic("Rogue Top", 36949, 4),
				new Cosmetic("Rogue Bottom", 36950, 7), new Cosmetic("Rogue Wristguards", 36951, 9),
				new Cosmetic("Rogue Sandals", 36952, 10)),

		NAVIGATOR_OUTFIT(new Cosmetic("Navigator Goggles", 37137, 0), new Cosmetic("Navigator Tunic Top", 37140, 4),
				new Cosmetic("Navigator Tunic Bottom", 37141, 7), new Cosmetic("Navigator Cuffs", 37142, 9),
				new Cosmetic("Navigator Knee High Boots", 37143, 10)),

		ROYAL_EASTERN_OUTFIT(new Cosmetic("Royal Eastern Cap", 37215, 0),
				new Cosmetic("Royal Eastern Wrap Top", 37217, 4), new Cosmetic("Royal Eastern Wrap Bottom", 37218, 7),
				new Cosmetic("Royal Eastern Gloves", 37219, 9), new Cosmetic("Royal Eastern Geta", 37220, 10)),

		VAMPYRE_HUNTER_OUTFIT(new Cosmetic("Vampyre Hunter Hat", 37204, 0),
				new Cosmetic("Vampyre Hunter Amulet", 37209, 2), new Cosmetic("Vampyre Hunter Torso", 37205, 4),
				new Cosmetic("Vampyre Hunter Legs", 37206, 7), new Cosmetic("Vampyre Hunter Cuffs", 37208, 9),
				new Cosmetic("Vampyre Hunter Boots", 37207, 10)),

		INVESTIGATOR_UNIFORM(new Cosmetic("Investigator's Fedora", 37325, 0),
				new Cosmetic("Investigator's Coat", 37326, 4), new Cosmetic("Investigator's Trousers", 37327, 7)),

		LION_OUTFIT(new Cosmetic("Lion Mane", 37378, 0), new Cosmetic("Lion Tail", 37383, 1),
				new Cosmetic("Lion Belly", 37379, 4), new Cosmetic("Lion Legs", 37380, 7),
				new Cosmetic("Lion Claws", 37381, 9), new Cosmetic("Lion Paws", 37382, 10)),

		GRIFFIN_OUTFIT(new Cosmetic("Griffin Crown", 37388, 0), new Cosmetic("Griffin Talons", 37391, 9),
				new Cosmetic("Griffin Talons", 37392, 10), new Cosmetic("Griffin Wings", 37393, 11),
				new Cosmetic("Griffin Mantle", 37389, 4), new Cosmetic("Griffin Limbs", 37390, 7)),

		SCORPION_OUTFIT(new Cosmetic("Scorpion Prosoma", 37511, 0), new Cosmetic("Scorpion Tail", 37516, 1),
				new Cosmetic("Scorpion Carapace", 37512, 4), new Cosmetic("Scorpion Tibia", 37513, 7),
				new Cosmetic("Scorpion Palps", 37514, 9), new Cosmetic("Scorpion Tarsus", 37515, 10)),

		CABBAGEMANCER_OUTFIT(new Cosmetic("Cabbagemancer Staff", 37604, 3),
				new Cosmetic("Cabbagemancer Robe Top", 37605, 4), new Cosmetic("Cabbagemancer Robe Bottom", 37606, 7),
				new Cosmetic("Cabbagemancer Gloves", 37608, 9), new Cosmetic("Cabbagemancer Boots", 37607, 10)),

		DEATH_LOTUS_ROGUE_ARMOUR(new Cosmetic("Death Lotus rogue hood", 39005, 0),
				new Cosmetic("Death Lotus rogue chestplate", 39006, 4),
				new Cosmetic("Death Lotus rogue chaps", 39007, 7)),

		GU_RONIN_ARMOUR(new Cosmetic("Gu ronin helm", 37859, 0), new Cosmetic("Gu ronin body", 37860, 4),
				new Cosmetic("Gu ronin platelegs", 37861, 7)),

		SEASINGER_ACOLYTE_ARMOUR(new Cosmetic("Seasinger acolyte hood", 38867, 0),
				new Cosmetic("Seasinger acolyte robe top", 38868, 4),
				new Cosmetic("Seasinger acolyte robe bottoms", 38869, 7)),

		FLOURISHING_FAIRY_OUTFIT(new Cosmetic("Petal Coronet", 37853, 0), new Cosmetic("Petal Gloves", 37857, 9),
				new Cosmetic("Petal Boots", 37856, 10), new Cosmetic("Petal Wings", 37858, 11),
				new Cosmetic("Petal Top", 37854, 4), new Cosmetic("Petal Bottoms", 37855, 7)),

		SATYR_OUTFIT(new Cosmetic("Satyr Horned Headwear", 38068, 0), new Cosmetic("Satyr Armguards", 38072, 9),
				new Cosmetic("Satyr Hooves", 38071, 10), new Cosmetic("Satyr Tendril Wings", 38073, 11),
				new Cosmetic("Satyr Leafy Top", 38069, 4), new Cosmetic("Satyr Hock Legs", 38070, 7)),

		SUMMER_FUN_OUTFIT(new Cosmetic("Summer fun top", 37993, 4), new Cosmetic("Summer fun bottoms", 37994, 7)),

		CRYSTAL_PEACOCK_ARMOUR(new Cosmetic("Crystal Peacock Helmet", 38155, 0),
				new Cosmetic("Crystal Peacock Body", 38156, 4), new Cosmetic("Crystal Peacock Legs", 38157, 7),
				new Cosmetic("Crystal Peacock Gloves", 38158, 9), new Cosmetic("Crystal Peacock Boots", 38159, 10)),

		SUNFURY_ARMOUR_TIER_1(new Cosmetic("Sunfury Helm (Tier 1)", 38139, 0),
				new Cosmetic("Sunfury Cape (Tier 1)", 38149, 1), new Cosmetic("Sunfury Chest (Tier 1)", 38141, 4),
				new Cosmetic("Sunfury Shield (Tier 1)", 38151, 5), new Cosmetic("Sunfury Legs (Tier 1)", 38143, 7),
				new Cosmetic("Sunfury Gauntlets (Tier 1)", 38145, 9),
				new Cosmetic("Sunfury Boots (Tier 1)", 38147, 10)),

		SUNFURY_ARMOUR_TIER_2(new Cosmetic("Sunfury Helm (Tier 2)", 38140, 0),
				new Cosmetic("Sunfury Cape (Tier 2)", 38150, 1), new Cosmetic("Sunfury Chest (Tier 2)", 38142, 4),
				new Cosmetic("Sunfury Shield (Tier 2)", 38152, 5), new Cosmetic("Sunfury Legs (Tier 2)", 38144, 7),
				new Cosmetic("Sunfury Gauntlets (Tier 2)", 38146, 9),
				new Cosmetic("Sunfury Boots (Tier 2)", 38148, 10)),

		GOSSAMER_OUTFIT(new Cosmetic("Gossamer Feathered Headdress", 38193, 0),
				new Cosmetic("Gossamer Silk Gloves", 38197, 9), new Cosmetic("Gossamer Pumps", 38196, 10),
				new Cosmetic("Gossamer Wings", 38198, 11), new Cosmetic("Gossamer Robes Top", 38194, 4),
				new Cosmetic("Gossamer Robes Bottom", 38195, 7)),

		PRIVATEER_OUTFIT(new Cosmetic("Privateer Tricorn", 38857, 0), new Cosmetic("Privateer Tunic", 38858, 4),
				new Cosmetic("Privateer Cargo Pants", 38859, 7), new Cosmetic("Privateer Gloves", 38861, 9),
				new Cosmetic("Privateer Cuffed Boots", 38860, 10)),

		IRONMAN_ARMOUR(new Cosmetic("Ironman helm", 38943, 0), new Cosmetic("Ironman body", 38945, 4),
				new Cosmetic("Ironman legs", 38947, 7), new Cosmetic("Ironman gauntlets", 38948, 9),
				new Cosmetic("Ironman boots", 38949, 10)),

		HC_IRONMAN_ARMOUR(new Cosmetic("HC Ironman helm", 38944, 0), new Cosmetic("HC Ironman body", 38946, 4),
				new Cosmetic("Ironman legs", 38947, 7), new Cosmetic("Ironman gauntlets", 38948, 9),
				new Cosmetic("Ironman boots", 38949, 10)),

		SPOOKY_SPIDER_OUTFIT(new Cosmetic("Spooky Spider Head", 38893, 0),
				new Cosmetic("Spooky Spider Gloves", 38896, 9), new Cosmetic("Spooky Spider Boots", 38897, 10),
				new Cosmetic("Spooky Spider Wings", 38898, 11), new Cosmetic("Spooky Spider Top", 38894, 4),
				new Cosmetic("Spooky Spider Bottoms", 38895, 7)),

		FALLEN_NIHIL_OUTFIT(new Cosmetic("Fallen Nihil Headpiece", 39041, 0),
				new Cosmetic("Fallen Nihil Gauntlets", 39045, 9), new Cosmetic("Fallen Nihil Greaves", 39044, 10),
				new Cosmetic("Fallen Nihil Wings", 39046, 11), new Cosmetic("Fallen Nihil Chestpiece", 39042, 4),
				new Cosmetic("Fallen Nihil Leg-guards", 39043, 7)),

		NAUTILUS_OUTFIT(new Cosmetic("Nautilus Crown", 39169, 0), new Cosmetic("Nautilus Torso", 39170, 4),
				new Cosmetic("Nautilus Gown", 39171, 7), new Cosmetic("Nautilus Gloves", 39172, 9),
				new Cosmetic("Nautilus Boots", 39173, 10)),

		LEGATUS_MAXIMUS_ARMOUR(new Cosmetic("Legatus Maximus Gauntlets", 39257, 9),
				new Cosmetic("Legatus Maximus Boots", 39258, 10), new Cosmetic("Legatus Maximus Platebody", 39255, 4),
				new Cosmetic("Legatus Maximus Platelegs", 39256, 7)),

		MENAPHITE_ANCIENT_OUTFIT(new Cosmetic("Menaphite Ancient Headpiece", 39291, 0),
				new Cosmetic("Menaphite Ancient Cloak", 39296, 1),
				new Cosmetic("Menaphite Ancient Chestplate", 39292, 4),
				new Cosmetic("Menaphite Ancient Legguards", 39293, 7),
				new Cosmetic("Menaphite Ancient Gauntlets", 39295, 9),
				new Cosmetic("Menaphite Ancient Boots", 39294, 10)),

		REVOLUTIONARY_MASK_AND_HAT(15, new Cosmetic("Revolutionary mask and hat", 24567, 0)),

		REVOLUTIONARY_MASK(10, new Cosmetic("Revolutionary mask", 24569, 0)),

		REVOLUTIONARY_HAT(10, new Cosmetic("Revolutionary hat", 24571, 0)),

		SUNGLASS_MONOCLES(10, new Cosmetic("Sunglass monocles", 24822, 0)),

		WOODLAND_CROWN(20, new Cosmetic("Woodland crown", 25170, 0)),

		KALPHITE_GREATHELM(15, new Cosmetic("Kalphite greathelm", 25174, 0)),

		TROPICAL_HEADDRESS(10, new Cosmetic("Tropical headdress", 24806, 0)),

		SEAWEED_HAIR(10, new Cosmetic("Seaweed hair", 24819, 0)),

		BOOK_OF_FACES(10, new Cosmetic("Book of Faces", 23664, 0)),

		SCARECROW_MASK(25, new Cosmetic("Scarecrow mask", 27602, 0)),

		TURKEY_HAT(25, new Cosmetic("Turkey hat", 27604, 0)),

		FLAMING_SKULL_RED(-1, new Cosmetic("Flaming skull (red)", 27606, 0)),

		FLAMING_SKULL_GREEN(-1, new Cosmetic("Flaming skull (green)", 27610, 0)),

		FLAMING_SKULL_BLUE(-1, new Cosmetic("Flaming skull (blue)", 27608, 0)),

		FLAMING_SKULL_PURPLE(-1, new Cosmetic("Flaming skull (purple)", 27612, 0)),

		CROWN_OF_SUPREMACY(15, new Cosmetic("Crown of Supremacy", 28822, 0)),

		CROWN_OF_LEGENDS(15, new Cosmetic("Crown of Legends", 28823, 0)),

		SINISTER_CLOWN_FACE(25, new Cosmetic("Sinister clown face", 29762, 0)),

		CHRISTMAS_PUDDING_HEAD(20, new Cosmetic("Christmas pudding head", 30359, 0)),

		SNOWMAN_HEAD(20, new Cosmetic("Snowman head", 30361, 0)),

		RUNEFEST_2011_HOOD(20, new Cosmetic("RuneFest 2011 hood", 29944, 0)),

		VALKYRIE_HELMET(15, new Cosmetic("Valkyrie helmet", 30613, 0)),

		DR_NABANIKS_OLD_TRILBY(20, new Cosmetic("Dr Nabanik's old trilby", 31028, 0)),

		HELM_OF_ZAROS(25, new Cosmetic("Helm of Zaros", 31039, 0)),

		CREST_OF_SEREN(25, new Cosmetic("Crest of Seren", 31040, 0)),

		REPLICA_VOID_KNIGHT_MELEE_HELM(10, new Cosmetic("Replica Void Knight melee helm", 31706, 0)),

		REPLICA_VOID_KNIGHT_MAGE_HELM(10, new Cosmetic("Replica Void Knight mage helm", 31707, 0)),

		REPLICA_VOID_KNIGHT_RANGER_HELM(10, new Cosmetic("Replica Void Knight ranger helm", 31708, 0)),

		OXFAM_REINDEER_ANTLERS(25, new Cosmetic("Oxfam reindeer antlers", 33587, 0)),

		LAVA_HOOD(20, new Cosmetic("Lava hood", 33654, 0)),

		MORTAR_BOARD(10, new Cosmetic("Mortar Board", 34244, 0)),

		MERMAID_HUNTER_HELMET(10, new Cosmetic("Mermaid hunter helmet", 34679, 0)),

		SALVAGE_HUNTER_HELMET(10, new Cosmetic("Salvage hunter helmet", 34680, 0)),

		COCONUT_HAT(10, new Cosmetic("Coconut hat", 34931, 0)),

		SNORKEL(10, new Cosmetic("Snorkel", 35087, 0)),

		BUCKET_HEAD(10, new Cosmetic("Bucket head", 35088, 0)),

		CLAWDIA_HAT(20, new Cosmetic("Clawdia hat", 35089, 0)),

		GILLYS_HAT(10, new Cosmetic("Gilly's hat", 35549, 0)),

		ZAROS_MORION(10, new Cosmetic("Zaros morion", 35784, 0)),

		MASQUERADE_MASK(10, new Cosmetic("Masquerade Mask", 36781, 0)),

		ANCIENT_HEADWEAR(10, new Cosmetic("Ancient Headwear", 36903, 0)),

		GEM_CROWN(15, new Cosmetic("Gem Crown", 37505, 0)),

		CANNONBALL(15, new Cosmetic("Cannonball!", 37724, 0)),

		LIFEGUARD_CHAIR_HAT(10, new Cosmetic("Lifeguard Chair Hat", 37988, 0)),

		LIGHTNING_ROD_HAT(10, new Cosmetic("Lightning rod hat", 38985, 0)),

		SQUID_TENTACLE_CAPE(30, new Cosmetic("Squid Tentacle cape", 24817, 1)),

		RUNEFEST_2013_CAPE(30, new Cosmetic("RuneFest 2013 cape", 29943, 1)),

		BRASSICAN_CLOAK(20, new Cosmetic("Brassican cloak", 31182, 1)),

		MARIMBAN_CLOAK(20, new Cosmetic("Marimban cloak", 31183, 1)),

		GODLESS_CLOAK(15, new Cosmetic("Godless cloak", 31184, 1)),

		ENHANCED_FIRE_CAPE(-1, new Cosmetic("Enhanced fire cape", 31603, 1)),

		RUNESTREAM_CLOAK(20, new Cosmetic("Runestream cloak", 31618, 1)),

		JERRODS_CAPE(20, new Cosmetic("Jerrod's cape", 34195, 1)),

		VITALITY_CAPE(40, new Cosmetic("Vitality Cape", 34943, 1)),

		VITALITY_CAPE_INACTIVE(40, new Cosmetic("Vitality Cape (inactive)", 34953, 1)),

		SAND_CAPE(15, new Cosmetic("Sand cape", 34930, 1)),

		DRAGON_RING(15, new Cosmetic("Dragon ring", 35097, 1)),

		CLAN_CLOAK(10, new Cosmetic("Clan Cloak", 36350, 1)),

		GAMEBLAST_2016_CAPE(10, new Cosmetic("GameBlast 2016 cape", 36855, 1)),

		DARKSCAPE(25, new Cosmetic("DarksCape", 37199, 1)),

		GEM_CAPE(15, new Cosmetic("Gem Cape", 37504, 1)),

		SKILLING_BACKPACK(10, new Cosmetic("Skilling Backpack", 37699, 1)),

		SKULLS_CAPE(10, new Cosmetic("Skulls cape", 37722, 1)),

		BEACH_TOWEL_CAPE(10, new Cosmetic("Beach Towel Cape", 37986, 1)),

		SHARK_FIN(15, new Cosmetic("Shark Fin", 37987, 1)),

		SHADOW_DRAKE_RING(10, new Cosmetic("Shadow Drake Ring", 37990, 1)),

		DUCK_RING(10, new Cosmetic("Duck Ring", 37991, 1)),

		RAVENSWORN_CAPE(10, new Cosmetic("Ravensworn cape", 38462, 1)),

		BUNNY_TAIL(10, new Cosmetic("Bunny tail", 25172, 1)),

		INARI_TAIL(25, new Cosmetic("Inari tail", 31827, 1)),

		SKELETAL_TAIL(15, new Cosmetic("Skeletal tail", 31829, 1)),

		LEI_NECKLACE(10, new Cosmetic("Lei necklace", 24815, 2)),

		MARK_OF_ZAROS(10, new Cosmetic("Mark of Zaros", 31987, 2)),

		GAMEBLAST_2016_AMULET(10, new Cosmetic("GameBlast 2016 amulet", 36854, 2)),

		GEM_NECKLACE(10, new Cosmetic("Gem Necklace", 37503, 2)),

		RAINBOW_AMULET(20, new Cosmetic("Rainbow Amulet", 39159, 2)),

		SEABORNE_DAGGER(20, new Cosmetic("Seaborne dagger", 24904, 3)),

		JOUSTING_LANCE_RAPIER(40, new Cosmetic("Jousting lance (rapier)", 25112, 3)),

		BRUTAL_RAPIER(30, new Cosmetic("Brutal rapier", 26015, 3)),

		OWENS_SHORTSWORD(20, new Cosmetic("Owen's shortsword", 28983, 3)),

		SHADOW_OWENS_SHORTSWORD(20, new Cosmetic("Shadow Owen's shortsword", 31124, 3)),

		PROTO_PACK_DAGGER(30, new Cosmetic("Proto pack dagger", 32400, 3)),

		SAI(10, new Cosmetic("Sai", 35311, 3)),

		MAZCAB_POKER(15, new Cosmetic("Mazcab Poker", 35874, 3)),

		SHADOW_GLAIVE_DAGGER(30, new Cosmetic("Shadow glaive dagger", 37113, 3)),

		ORNATE_DAGGER(20, new Cosmetic("Ornate Dagger", 37296, 3)),

		ORNATE_SCIMITAR(30, new Cosmetic("Ornate Scimitar", 37298, 3)),

		MANTICORE_DAGGER(10, new Cosmetic("Manticore Dagger", 37655, 3)),

		CORAL_SWORD(20, new Cosmetic("Coral Sword", 38005, 3)),

		CORAL_DAGGER(15, new Cosmetic("Coral Dagger", 38007, 3)),

		CRYSTAL_PEACOCK_SWORD(30, new Cosmetic("Crystal Peacock Sword", 38441, 3)),

		FAE_FAIRY_DAGGER(10, new Cosmetic("Fae Fairy Dagger", 38417, 3)),

		FAE_FAIRY_SWORD(25, new Cosmetic("Fae Fairy Sword", 38419, 3)),

		PACTBREAKER_LONGSWORD(30, new Cosmetic("Pactbreaker longsword", 24573, 3)),

		DWARVEN_LONGSWORD(25, new Cosmetic("Dwarven longsword", 25283, 3)),

		CLEAVER_OF_TSUTSAROTH(20, new Cosmetic("Cleaver of Tsutsaroth", 25398, 3)),

		BRUTAL_LONGSWORD(15, new Cosmetic("Brutal longsword", 26019, 3)),

		PALADIN_BLADE(30, new Cosmetic("Paladin blade", 26476, 3)),

		OWENS_LONGSWORD(30, new Cosmetic("Owen's longsword", 28979, 3)),

		FLAGSTAFF_OF_FESTIVITIES(40, new Cosmetic("Flagstaff of festivities", 29945, 3)),

		SHADOW_OWENS_LONGSWORD(30, new Cosmetic("Shadow Owen's longsword", 31122, 3)),

		FIRESTORM_BLADE(30, new Cosmetic("Firestorm blade", 31363, 3)),

		CURSED_ARRAV_1H_SWORD(25, new Cosmetic("Cursed Arrav 1h Sword", 34315, 3)),

		SILVERLIGHT(20, new Cosmetic("Silverlight", 34511, 3)),

		SILVERLIGHT_DYED(25, new Cosmetic("Silverlight (dyed)", 34513, 3)),

		DARKLIGHT(20, new Cosmetic("Darklight", 34515, 3)),

		STICK_OF_ROCK_SWORD(20, new Cosmetic("Stick of rock sword", 35083, 3)),

		LOST_SWORD_OF_KING_RADDALLIN_1H(20, new Cosmetic("Lost sword of King Raddallin (1h)", 35939, 3)),

		SWORD_2001(20, new Cosmetic("2001 Sword", 36130, 3)),

		SWORD_2008(15, new Cosmetic("2008 Sword", 36132, 3)),

		SWORD_2011(20, new Cosmetic("2011 Sword", 36134, 3)),

		SWORD_2014(15, new Cosmetic("2014 Sword", 36136, 3)),

		VANNAKAS_SWORD(20, new Cosmetic("Vannaka's Sword", 36253, 3)),

		SHARD_OF_HAVOC(20, new Cosmetic("Shard of havoc", 36822, 3)),

		DRAGON_RIDER_BLADE(25, new Cosmetic("Dragon Rider blade", 37115, 3)),

		MANTICORE_SCIMITAR(15, new Cosmetic("Manticore Scimitar", 37657, 3)),

		PRIVATEER_CUTLASS(20, new Cosmetic("Privateer Cutlass", 38862, 3)),

		ICE_SWORD(20, new Cosmetic("Ice Sword", 39318, 3)),

		BRUTAL_MACE(10, new Cosmetic("Brutal mace", 26011, 3)),

		LINZAS_HAMMER(15, new Cosmetic("Linza's hammer", 28958, 3)),

		SHADOW_LINZAS_HAMMER(15, new Cosmetic("Shadow Linza's hammer", 31115, 3)),

		SPADE(20, new Cosmetic("Spade", 34935, 3)),

		BARBARIAN_WARHAMMER(15, new Cosmetic("Barbarian warhammer", 35375, 3)),

		MAZCAB_CUDGEL(15, new Cosmetic("Mazcab Cudgel", 35876, 3)),

		TURKEY_DRUMSTICK(15, new Cosmetic("Turkey drumstick", 36077, 3)),

		ORNATE_MACE(10, new Cosmetic("Ornate Mace", 37294, 3)),

		MANTICORE_MACE(15, new Cosmetic("Manticore Mace", 37653, 3)),

		FAE_FAIRY_MACE(15, new Cosmetic("Fae Fairy Mace", 38415, 3)),

		HAND_FLAIL(15, new Cosmetic("Hand Flail", 38980, 3)),

		SCORCHING_AXE(20, new Cosmetic("Scorching axe", 24900, 3)),

		ORNATE_BATTLEAXE(15, new Cosmetic("Ornate Battleaxe", 37303, 3)),

		FAE_FAIRY_BATTLEAXE(15, new Cosmetic("Fae Fairy Battleaxe", 38424, 3)),

		PARASOL_2H_SWORD(20, new Cosmetic("Parasol 2H sword", 24824, 3)),

		BLAZING_FLAMBERGE(35, new Cosmetic("Blazing flamberge", 24886, 3)),

		OWENS_GREATSWORD(25, new Cosmetic("Owen's greatsword", 28987, 3)),

		SWORD_OF_EDICTS(20, new Cosmetic("Sword of Edicts", 27597, 3)),

		GIANTS_HAND(15, new Cosmetic("Giant's hand", 30949, 3)),

		SHADOW_OWENS_GREATSWORD(25, new Cosmetic("Shadow Owen's greatsword", 31126, 3)),

		REPLICA_ARMADYL_GODSWORD(10, new Cosmetic("Replica Armadyl godsword", 31241, 3)),

		REPLICA_BANDOS_GODSWORD(10, new Cosmetic("Replica Bandos godsword", 31240, 3)),

		REPLICA_SARADOMIN_GODSWORD(10, new Cosmetic("Replica Saradomin godsword", 31242, 3)),

		REPLICA_ZAMORAK_GODSWORD(10, new Cosmetic("Replica Zamorak godsword", 31243, 3)),

		OWARI(15, new Cosmetic("Owari", 33634, 3)),

		NEFARIOUS_EDGE(20, new Cosmetic("Nefarious edge", 33747, 3)),

		ARRAVS_SWORD(15, new Cosmetic("Arrav's sword", 34333, 3)),

		VITALITY_2H_SWORD(20, new Cosmetic("Vitality 2h Sword", 34944, 3)),

		VITALITY_2H_SWORD_INACTIVE(20, new Cosmetic("Vitality 2h Sword (inactive)", 34954, 3)),

		LOST_SWORD_OF_KING_RADDALLIN_2H(20, new Cosmetic("Lost sword of King Raddallin (2h)", 35937, 3)),

		LOST_SWORD_OF_KING_RADDALLIN_2H_1(20, new Cosmetic("Lost sword of King Raddallin 2 (2h)", 35938, 3)),

		ORNATE_2H_SWORD(20, new Cosmetic("Ornate 2h Sword", 37300, 3)),

		BRASSICA_PRIME_GODSWORD(15, new Cosmetic("Brassica Prime godsword", 37602, 3)),

		MANTICORE_2H_SWORD(20, new Cosmetic("Manticore 2h Sword", 37659, 3)),

		FAE_FAIRY_2H_SWORD(20, new Cosmetic("Fae Fairy 2h Sword", 38421, 3)),

		SHARD_OF_CHAOS(15, new Cosmetic("Shard of chaos", 38995, 3)),

		NAUTILUS_2H_SWORD(15, new Cosmetic("Nautilus 2H Sword", 39174, 3)),

		EXECUTIONER_AXE(15, new Cosmetic("Executioner axe", 28011, 3)),

		ICYENIC_GREATHAMMER(25, new Cosmetic("Icyenic greathammer", 28739, 3)),

		INFERNAL_GREATHAMMER(20, new Cosmetic("Infernal greathammer", 28740, 3)),

		GOLDEN_SCYTHE(40, new Cosmetic("Golden scythe", 29946, 3)),

		BLOODIED_KYZAJ(10, new Cosmetic("Bloodied Kyzaj", 31519, 3)),

		HONOURABLE_KYZAJ(10, new Cosmetic("Honourable Kyzaj", 31520, 3)),

		EGG_ON_A_FORK(20, new Cosmetic("Egg on a fork", 34503, 3)),

		BARBARIAN_MAUL(10, new Cosmetic("Barbarian maul", 35374, 3)),

		CAULDRON_MAUL(15, new Cosmetic("Cauldron Maul", 35954, 3)),

		ORNATE_HALBERD(15, new Cosmetic("Ornate Halberd", 37301, 3)),

		ORNATE_MAUL(15, new Cosmetic("Ornate Maul", 37302, 3)),

		YAK_SCYTHE(30, new Cosmetic("Yak Scythe", 37485, 3)),

		MANTICORE_MAUL(15, new Cosmetic("Manticore Maul", 37661, 3)),

		MANTICORE_HALBERD(15, new Cosmetic("Manticore Halberd", 37660, 3)),

		CRYSTAL_PEACOCK_BATTLEAXE(20, new Cosmetic("Crystal Peacock Battleaxe", 38443, 3)),

		FAE_FAIRY_HALBERD(20, new Cosmetic("Fae Fairy Halberd", 38422, 3)),

		FAE_FAIRY_MAUL(15, new Cosmetic("Fae Fairy Maul", 38423, 3)),

		SHIPWRECKER_TRIDENT(20, new Cosmetic("Shipwrecker trident", 24902, 3)),

		JOUSTING_LANCE_SPEAR(30, new Cosmetic("Jousting lance (spear)", 25110, 3)),

		VALKYRIE_SPEAR(10, new Cosmetic("Valkyrie spear", 30614, 3)),

		SARADOMIN_TUSKA_SPEAR(25, new Cosmetic("Saradomin Tuska spear", 34878, 3)),

		ARMADYL_TUSKA_SPEAR(25, new Cosmetic("Armadyl Tuska spear", 34879, 3)),

		ZAMORAK_TUSKA_SPEAR(25, new Cosmetic("Zamorak Tuska spear", 34880, 3)),

		GODLESS_TUSKA_SPEAR(25, new Cosmetic("Godless Tuska spear", 34881, 3)),

		GLAIVE(30, new Cosmetic("Glaive", 35313, 3)),

		DAGGERFIST_CLAWS(10, new Cosmetic("Daggerfist claws", 24898, 3)),

		BRUTAL_CLAW(15, new Cosmetic("Brutal claw", 26007, 3)),

		CRAB_CLAW(10, new Cosmetic("Crab claw", 29466, 3)),

		SUPERHERO_CLAWS(10, new Cosmetic("Superhero claws", 29425, 3)),

		AVIANSIE_CLAW(15, new Cosmetic("Aviansie claw", 30337, 3)),

		ORKISH_CLAW(15, new Cosmetic("Orkish claw", 30339, 3)),

		PROTO_PACK_CLAW(15, new Cosmetic("Proto pack claw", 32396, 3)),

		HELWYRS_CLAWS(20, new Cosmetic("Helwyr's claws", 37117, 3)),

		CLAWDIA_CLAWS(10, new Cosmetic("Clawdia Claws", 37999, 3)),

		FAE_FAIRY_CLAWS(20, new Cosmetic("Fae Fairy Claws", 38426, 3)),

		RAZOR_WHIP(20, new Cosmetic("Razor whip", 24892, 3)),

		FLAMING_LASH(30, new Cosmetic("Flaming lash", 24894, 3)),

		BRUTAL_WHIP(20, new Cosmetic("Brutal whip", 26005, 3)),

		BUNTING_WHIP(20, new Cosmetic("Bunting whip", 35082, 3)),

		ORNATE_WHIP(20, new Cosmetic("Ornate Whip", 37305, 3)),

		CONGA_EEL_WHIP(20, new Cosmetic("Conga Eel Whip", 37998, 3)),

		SHOCK_EYE_STAFF(25, new Cosmetic("Shock Eye staff", 24577, 3)),

		ARIANES_STAFF(25, new Cosmetic("Ariane's staff", 26053, 3)),

		ICYENIC_STAFF(35, new Cosmetic("Icyenic staff", 28737, 3)),

		INFERNAL_STAFF(35, new Cosmetic("Infernal staff", 28738, 3)),

		SHADOW_ARIANES_STAFF(30, new Cosmetic("Shadow Ariane's staff", 31133, 3)),

		THE_BURNING_TRUTH(40, new Cosmetic("The Burning Truth", 31357, 3)),

		NEFARIOUS_SPIRE(20, new Cosmetic("Nefarious spire", 33749, 3)),

		NOMADS_STAFF(20, new Cosmetic("Nomad's staff", 34106, 3)),

		VITALITY_STAFF(40, new Cosmetic("Vitality Staff", 34945, 3)),

		VITALITY_STAFF_INACTIVE(40, new Cosmetic("Vitality Staff (inactive)", 34955, 3)),

		DRAGON_STAFF(30, new Cosmetic("Dragon Staff", 35316, 3)),

		REVENANT_STAFF(20, new Cosmetic("Revenant staff", 35858, 3)),

		BROOM_STAFF(15, new Cosmetic("Broom Staff", 35953, 3)),

		ORNATE_STAFF(20, new Cosmetic("Ornate Staff", 37309, 3)),

		MANTICORE_STAFF(20, new Cosmetic("Manticore Staff", 37665, 3)),

		CRYSTAL_PEACOCK_STAFF(20, new Cosmetic("Crystal Peacock Staff", 38444, 3)),

		FAE_FAIRY_STAFF(20, new Cosmetic("Fae Fairy Staff", 38431, 3)),

		SHARD_OF_SUFFERING(20, new Cosmetic("Shard of suffering", 38997, 3)),

		PRIVATEER_SERPENT_SCEPTRE(30, new Cosmetic("Privateer Serpent Sceptre", 38864, 3)),

		NAUTILUS_TRIDENT(15, new Cosmetic("Nautilus Trident", 39176, 3)),

		ICE_STAFF(20, new Cosmetic("Ice Staff", 39320, 3)),

		SHATTERSTORM_WAND(25, new Cosmetic("Shatterstorm wand", 24890, 3)),

		AVIANSIE_WAND(20, new Cosmetic("Aviansie wand", 30345, 3)),

		ORKISH_WAND(20, new Cosmetic("Orkish wand", 30346, 3)),

		SPITEFUL_SPARK(25, new Cosmetic("Spiteful Spark", 31359, 3)),

		REVENANT_WAND(20, new Cosmetic("Revenant wand", 35859, 3)),

		SHARD_OF_ENERGY(20, new Cosmetic("Shard of energy", 36826, 3)),

		ORNATE_WAND(20, new Cosmetic("Ornate Wand", 37308, 3)),

		MANTICORE_WAND(20, new Cosmetic("Manticore Wand", 37664, 3)),

		ICE_LOLLY_WAND(20, new Cosmetic("Ice Lolly Wand", 37996, 3)),

		CRYSTAL_PEACOCK_WAND(20, new Cosmetic("Crystal Peacock Wand", 38445, 3)),

		FAE_FAIRY_WAND(20, new Cosmetic("Fae Fairy Wand", 38430, 3)),

		BONE_WAND(15, new Cosmetic("Bone Wand", 38982, 3)),

		FIREBRAND_BOW(30, new Cosmetic("Firebrand bow", 24888, 3)),

		OZANS_BOW(20, new Cosmetic("Ozan's bow", 26073, 3)),

		BARBED_BOW(15, new Cosmetic("Barbed bow", 27614, 3)),

		ICYENIC_BOW(25, new Cosmetic("Icyenic bow", 28741, 3)),

		INFERNAL_BOW(30, new Cosmetic("Infernal bow", 28742, 3)),

		SHADOW_OZANS_BOW(25, new Cosmetic("Shadow Ozan's bow", 31140, 3)),

		NEFARIOUS_REACH(15, new Cosmetic("Nefarious reach", 33751, 3)),

		SPIRIT_HUNTER_BOW(20, new Cosmetic("Spirit hunter bow", 35847, 3)),

		ORNATE_SHORTBOW(20, new Cosmetic("Ornate Shortbow", 37313, 3)),

		ORNATE_SHIELDBOW(20, new Cosmetic("Ornate Shieldbow", 37314, 3)),

		MANTICORE_SHORTBOW(20, new Cosmetic("Manticore Shortbow", 37669, 3)),

		MANTICORE_SHIELDBOW(20, new Cosmetic("Manticore Shieldbow", 37670, 3)),

		CRYSTAL_PEACOCK_SHORTBOW(20, new Cosmetic("Crystal Peacock Shortbow", 38447, 3)),

		FAE_FAIRY_SHORTBOW(20, new Cosmetic("Fae Fairy Shortbow", 38435, 3)),

		FAE_FAIRY_SHIELDBOW(20, new Cosmetic("Fae Fairy Shieldbow", 38436, 3)),

		SPINE_BOW(20, new Cosmetic("Spine Bow", 38981, 3)),

		SHARD_OF_DESPITE(20, new Cosmetic("Shard of despite", 38996, 3)),

		ICE_BOW(30, new Cosmetic("Ice bow", 39317, 3)),

		QUICKFIRE_CROSSBOW(20, new Cosmetic("Quick-Fire crossbow", 24575, 3)),

		DWARVEN_CROSSBOW(20, new Cosmetic("Dwarven crossbow", 25285, 3)),

		BRUTAL_CROSSBOW(20, new Cosmetic("Brutal crossbow", 26001, 3)),

		PROTO_PACK_CROSSBOW(20, new Cosmetic("Proto pack crossbow", 32392, 3)),

		SPIRIT_HUNTER_CROSSBOW(20, new Cosmetic("Spirit hunter crossbow", 35848, 3)),

		SHARD_OF_MALICE(20, new Cosmetic("Shard of malice", 36824, 3)),

		ENERGISED_ARM_CANNON(20, new Cosmetic("Energised Arm Cannon", 36942, 3)),

		MANTICORE_1H_CROSSBOW(20, new Cosmetic("Manticore 1h Crossbow", 37671, 3)),

		INK_SHOOTER(30, new Cosmetic("Ink Shooter", 38002, 3)),

		WILDFIRE(30, new Cosmetic("Wildfire", 31369, 3)),

		VITALITY_CROSSBOW(40, new Cosmetic("Vitality Crossbow", 34946, 3)),

		VITALITY_CROSSBOW_INACTIVE(40, new Cosmetic("Vitality Crossbow (inactive)", 34956, 3)),

		PUMPKIN_LAUNCHER(20, new Cosmetic("Pumpkin launcher", 35955, 3)),

		VAMPYRE_HUNTER_STAKE_LAUNCHER(20, new Cosmetic("Vampyre hunter stake launcher", 37210, 3)),

		ORNATE_1H_CROSSBOW(20, new Cosmetic("Ornate 1h Crossbow", 37315, 3)),

		ORNATE_2H_CROSSBOW(20, new Cosmetic("Ornate 2h Crossbow", 37312, 3)),

		MANTICORE_2H_CROSSBOW(20, new Cosmetic("Manticore 2h Crossbow", 37668, 3)),

		WATER_BALLOON_LAUNCHER(20, new Cosmetic("Water Balloon Launcher", 37997, 3)),

		FAE_FAIRY_1H_CROSSBOW(20, new Cosmetic("Fae Fairy 1h Crossbow", 38437, 3)),

		FAE_FAIRY_2H_CROSSBOW(20, new Cosmetic("Fae Fairy 2h Crossbow", 38434, 3)),

		PRIVATEER_REPEATER_CROSSBOW(20, new Cosmetic("Privateer Repeater Crossbow", 38863, 3)),

		NAUTILUS_2H_CROSSBOW(20, new Cosmetic("Nautilus 2H Crossbow", 39175, 3)),

		SUPERHERO_LIGHTNING(30, new Cosmetic("Superhero lightning", 29428, 3)),

		AVIANSIE_THROWING_STAR(20, new Cosmetic("Aviansie throwing star", 30341, 3)),

		ORKISH_THROWING_AXE(20, new Cosmetic("Orkish throwing axe", 30343, 3)),

		SUNFLARE_THROWING_AXE(25, new Cosmetic("Sunflare throwing axe", 31366, 3)),

		NOMADS_JAVELIN(15, new Cosmetic("Nomad's javelin", 34107, 3)),

		THROWING_STARFISH(15, new Cosmetic("Throwing starfish", 35085, 3)),

		ACID_FLASK(15, new Cosmetic("Acid Flask", 35314, 3)),

		ORNATE_THROWING_STAR(15, new Cosmetic("Ornate Throwing Star", 37310, 3)),

		MANTICORE_THROWING_AXE(15, new Cosmetic("Manticore Throwing Axe", 37666, 3)),

		OAR(15, new Cosmetic("Oar!", 37723, 3)),

		CRYSTAL_PEACOCK_THROWING_STAR(15, new Cosmetic("Crystal Peacock Throwing Star", 38448, 3)),

		FAE_FAIRY_THROWING_STAR(15, new Cosmetic("Fae Fairy Throwing Star", 38432, 3)),

		TROPICAL_TOP(15, new Cosmetic("Tropical top", 24807, 4)),

		REPLICA_DRAGON_CHAINBODY(10, new Cosmetic("Replica dragon chainbody", 30635, 4)),

		GAMEBLAST_TUNIC(10, new Cosmetic("GameBlast tunic", 30887, 4)),

		FIREMAKERS_TABARD(10, new Cosmetic("Firemaker's tabard", 30988, 4)),

		REPLICA_ELITE_VOID_KNIGHT_TOP(10, new Cosmetic("Replica Elite Void Knight top", 31702, 4)),

		ABLEGAMERS_TUNIC(10, new Cosmetic("AbleGamers tunic", 32527, 4)),

		YOUNGMINDS_TUNIC(10, new Cosmetic("YoungMinds tunic", 32529, 4)),

		DONATEGAMES_TUNIC(10, new Cosmetic("DonateGames tunic", 32531, 4)),

		GILLYS_TOP(10, new Cosmetic("Gilly's top", 35547, 4)),

		BLUE_GOEBIE_WARPAINT_CHEST(10, new Cosmetic("Blue Goebie Warpaint", 35878, 4)),

		RED_GOEBIE_WARPAINT_CHEST(10, new Cosmetic("Red Goebie Warpaint", 35880, 4)),

		YELLOW_GOEBIE_WARPAINT_CHEST(10, new Cosmetic("Yellow Goebie Warpaint", 35882, 4)),

		TH_ANNIVERSARY_TUNIC(10, new Cosmetic("15th Anniversary tunic", 36267, 4)),

		SEABORNE_DAGGER_OFFHAND(10, new Cosmetic("Seaborne dagger Off-hand", 26037, 5)),

		JOUSTING_LANCE_RAPIER_OFFHAND(30, new Cosmetic("Jousting lance (rapier) Off-hand", 26031, 5)),

		BRUTAL_RAPIER_OFFHAND(25, new Cosmetic("Brutal rapier Off-hand", 26017, 5)),

		OWENS_SHORTSWORD_OFFHAND(20, new Cosmetic("Owen's shortsword Off-hand", 28985, 5)),

		SHADOW_OWENS_OFFHAND_SHORTSWORD(20, new Cosmetic("Shadow Owen's off-hand shortsword", 31125, 5)),

		PROTO_PACK_DAGGER_OFFHAND(20, new Cosmetic("Proto pack dagger Off-hand", 32402, 5)),

		SAI_OFFHAND(15, new Cosmetic("Sai Off-hand", 35312, 5)),

		OFFHAND_MAZCAB_POKER(15, new Cosmetic("Off-hand Mazcab Poker", 35875, 5)),

		SHADOW_GLAIVE_DAGGER_OFFHAND(15, new Cosmetic("Shadow glaive dagger Off-hand", 37114, 5)),

		ORNATE_DAGGER_OFFHAND(15, new Cosmetic("Ornate Dagger Off-hand", 37297, 5)),

		ORNATE_SCIMITAR_OFFHAND(20, new Cosmetic("Ornate Scimitar Off-hand", 37299, 5)),

		MANTICORE_DAGGER_OFFHAND(15, new Cosmetic("Manticore Dagger Off-hand", 37656, 5)),

		CORAL_SWORD_OFFHAND_OFFHAND(20, new Cosmetic("Coral Sword Offhand Off-hand", 38006, 5)),

		CORAL_DAGGER_OFFHAND_OFFHAND(15, new Cosmetic("Coral Dagger Offhand Off-hand", 38008, 5)),

		CRYSTAL_PEACOCK_SWORD_OFFHAND(25, new Cosmetic("Crystal Peacock Sword Off-hand", 38442, 5)),

		FAE_FAIRY_DAGGER_OFFHAND(15, new Cosmetic("Fae Fairy Dagger Off-hand", 38418, 5)),

		FAE_FAIRY_SWORD_OFFHAND(20, new Cosmetic("Fae Fairy Sword Off-hand", 38420, 5)),

		PACTBREAKER_LONGSWORD_OFFHAND(30, new Cosmetic("Pactbreaker longsword Off-hand", 26025, 5)),

		DWARVEN_LONGSWORD_OFFHAND(25, new Cosmetic("Dwarven longsword Off-hand", 25997, 5)),

		CLEAVER_OF_TSUTSAROTH_OFFHAND(25, new Cosmetic("Cleaver of Tsutsaroth Off-hand", 26023, 5)),

		BRUTAL_LONGSWORD_OFFHAND(20, new Cosmetic("Brutal longsword Off-hand", 26021, 5)),

		PALADIN_BLADE_OFFHAND(20, new Cosmetic("Paladin blade Off-hand", 26478, 5)),

		OWENS_LONGSWORD_OFFHAND(20, new Cosmetic("Owen's longsword Off-hand", 28981, 5)),

		SHADOW_OWENS_OFFHAND_LONGSWORD(20, new Cosmetic("Shadow Owen's off-hand longsword", 31123, 5)),

		FIRESTORM_BLADE_OFFHAND(30, new Cosmetic("Firestorm blade Off-hand", 31365, 5)),

		CURSED_ARRAV_1H_SWORD_OFFHAND_OFFHAND(20, new Cosmetic("Cursed Arrav 1h Sword (offhand) Off-hand", 34316, 5)),

		OFFHAND_SILVERLIGHT(15, new Cosmetic("Off-hand Silverlight", 34512, 5)),

		OFFHAND_SILVERLIGHT_DYED(20, new Cosmetic("Off-hand Silverlight (dyed)", 34514, 5)),

		OFFHAND_DARKLIGHT(15, new Cosmetic("Off-hand Darklight", 34516, 5)),

		STICK_OF_ROCK_SWORD_OFFHAND(20, new Cosmetic("Stick of rock sword Off-hand", 35084, 5)),

		LOST_SWORD_OF_KING_RADDALLIN_1H_OFFHAND(15,
				new Cosmetic("Lost sword of King Raddallin (1h) Off-hand", 35940, 5)),

		SWORD_2001_OFFHAND(15, new Cosmetic("2001 Sword off-hand", 36131, 5)),

		SWORD_2008_OFFHAND(15, new Cosmetic("2008 Sword off-hand", 36133, 5)),

		SWORD_2011_OFFHAND(15, new Cosmetic("2011 Sword off-hand", 36135, 5)),

		SWORD_2014_OFFHAND(15, new Cosmetic("2014 Sword off-hand", 36137, 5)),

		OFFHAND_SHARD_OF_HAVOC(20, new Cosmetic("Off-hand shard of havoc", 36823, 5)),

		DRAGON_RIDER_BLADE_OFFHAND(15, new Cosmetic("Dragon Rider blade Off-hand", 37116, 5)),

		MANTICORE_SCIMITAR_OFFHAND(15, new Cosmetic("Manticore Scimitar Off-hand", 37658, 5)),

		BRUTAL_MACE_OFFHAND(15, new Cosmetic("Brutal mace Off-hand", 26013, 5)),

		LINZAS_HAMMER_OFFHAND(20, new Cosmetic("Linza's hammer Off-hand", 28960, 5)),

		SHADOW_LINZAS_OFFHAND_HAMMER(20, new Cosmetic("Shadow Linza's off-hand hammer", 31116, 5)),

		BARBARIAN_WARHAMMER_OFFHAND(15, new Cosmetic("Barbarian warhammer Off-hand", 35376, 5)),

		OFFHAND_MAZCAB_CUDGEL(15, new Cosmetic("Off-hand Mazcab Cudgel", 35877, 5)),

		TURKEY_DRUMSTICK_OFFHAND(10, new Cosmetic("Turkey drumstick Off-hand", 36078, 5)),

		ORNATE_MACE_OFFHAND(10, new Cosmetic("Ornate Mace Off-hand", 37295, 5)),

		MANTICORE_MACE_OFFHAND(15, new Cosmetic("Manticore Mace Off-hand", 37654, 5)),

		FAE_FAIRY_MACE_OFFHAND(10, new Cosmetic("Fae Fairy Mace Off-hand", 38416, 5)),

		SCORCHING_AXE_OFFHAND(30, new Cosmetic("Scorching axe Off-hand", 26035, 5)),

		ORNATE_OFFHAND_BATTLEAXE(15, new Cosmetic("Ornate Off-hand Battleaxe", 37304, 5)),

		FAE_FAIRY_OFFHAND_BATTLEAXE(15, new Cosmetic("Fae Fairy Off-hand Battleaxe", 38425, 5)),

		DAGGERFIST_CLAWS_OFFHAND(10, new Cosmetic("Daggerfist claws Off-hand", 26033, 5)),

		BRUTAL_CLAW_OFFHAND(10, new Cosmetic("Brutal claw Off-hand", 26009, 5)),

		OFFHAND_CRAB_CLAW(10, new Cosmetic("Off-hand crab claw", 29467, 5)),

		SUPERHERO_CLAWS_OFFHAND(15, new Cosmetic("Superhero claws Off-hand", 29426, 5)),

		OFFHAND_AVIANSIE_CLAW(10, new Cosmetic("Off-hand aviansie claw", 30338, 5)),

		OFFHAND_ORKISH_CLAW(15, new Cosmetic("Off-hand orkish claw", 30340, 5)),

		PROTO_PACK_CLAW_OFFHAND(15, new Cosmetic("Proto pack claw Off-hand", 32398, 5)),

		HELWYRS_CLAWS_OFFHAND(20, new Cosmetic("Helwyr's claws Off-hand", 37118, 5)),

		CLAWDIA_CLAWS_OFFHAND_OFFHAND(10, new Cosmetic("Clawdia Claws Offhand Off-hand", 38000, 5)),

		FAE_FAIRY_CLAWS_OFFHAND_OFFHAND(20, new Cosmetic("Fae Fairy Claws Offhand Off-hand", 38427, 5)),

		QUICKFIRE_CROSSBOW_OFFHAND(20, new Cosmetic("Quick-Fire crossbow Off-hand", 26027, 5)),

		DWARVEN_CROSSBOW_OFFHAND(15, new Cosmetic("Dwarven crossbow Off-hand", 25999, 5)),

		BRUTAL_CROSSBOW_OFFHAND(10, new Cosmetic("Brutal crossbow Off-hand", 26003, 5)),

		PROTO_PACK_CROSSBOW_OFFHAND(15, new Cosmetic("Proto pack crossbow Off-hand", 32394, 5)),

		OFFHAND_SPIRIT_HUNTER_CROSSBOW(15, new Cosmetic("Off-hand spirit hunter crossbow", 35849, 5)),

		OFFHAND_SHARD_OF_MALICE(20, new Cosmetic("Off-hand shard of malice", 36825, 5)),

		ENERGISED_ARM_CANNON_OFFHAND(20, new Cosmetic("Energised Arm Cannon Off-hand", 36943, 5)),

		ORNATE_1H_CROSSBOW_OFFHAND(10, new Cosmetic("Ornate 1h Crossbow Off-hand", 37316, 5)),

		MANTICORE_1H_CROSSBOW_OFFHAND(10, new Cosmetic("Manticore 1h Crossbow Off-hand", 37672, 5)),

		INK_SHOOTER_OFFHAND_OFFHAND(20, new Cosmetic("Ink Shooter Offhand Off-hand", 38003, 5)),

		FAE_FAIRY_1H_CROSSBOW_OFFHAND(15, new Cosmetic("Fae Fairy 1h Crossbow Off-hand", 38438, 5)),

		SUPERHERO_LIGHTNING_OFFHAND(30, new Cosmetic("Superhero lightning Off-hand", 29429, 5)),

		OFFHAND_AVIANSIE_THROWING_STAR(10, new Cosmetic("Off-hand aviansie throwing star", 30342, 5)),

		OFFHAND_ORKISH_THROWING_AXE(10, new Cosmetic("Off-hand orkish throwing axe", 30344, 5)),

		SUNFLARE_THROWING_AXE_OFFHAND(20, new Cosmetic("Sunflare throwing axe Off-hand", 31368, 5)),

		THROWING_STARFISH_OFFHAND(10, new Cosmetic("Throwing starfish Off-hand", 35086, 5)),

		ACID_FLASK_OFFHAND(10, new Cosmetic("Acid Flask Off-hand", 35315, 5)),

		ORNATE_OFFHAND_THROWING_STAR(10, new Cosmetic("Ornate Off-hand Throwing Star", 37311, 5)),

		MANTICORE_OFFHAND_THROWING_AXE(10, new Cosmetic("Manticore Off-hand Throwing Axe", 37667, 5)),

		CRYSTAL_PEACOCK_OFFHAND_THROWING_STAR(10, new Cosmetic("Crystal Peacock Off-hand Throwing Star", 38449, 5)),

		FAE_FAIRY_OFFHAND_THROWING_STAR(15, new Cosmetic("Fae Fairy Off-hand Throwing Star", 38433, 5)),

		DEMONFLESH_BOOK_OFFHAND(15, new Cosmetic("Demonflesh book Off-hand", 27144, 5)),

		HATEFUL_HEART_OFFHAND(25, new Cosmetic("Hateful Heart Off-hand", 31361, 5)),

		TEDDY_OFFHAND(20, new Cosmetic("Teddy Off-hand", 33522, 5)),

		NECROFELINOMICON_OFFHAND(10, new Cosmetic("Necrofelinomicon Off-hand", 34334, 5)),

		CALIBRATION_DEVICE_OFFHAND(10, new Cosmetic("Calibration device Off-hand", 34850, 5)),

		REVENANT_ORB_OFFHAND(20, new Cosmetic("Revenant orb Off-hand", 35860, 5)),

		SHARD_OF_FOCUS_OFFHAND(20, new Cosmetic("Shard of focus Off-hand", 36827, 5)),

		DRACCLES_OFFHAND(20, new Cosmetic("Draccles Off-hand", 37232, 5)),

		ORNATE_ORB_OFFHAND(10, new Cosmetic("Ornate Orb Off-hand", 37306, 5)),

		ORNATE_BOOK_OFFHAND(10, new Cosmetic("Ornate Book Off-hand", 37307, 5)),

		MANTICORE_BOOK_OFFHAND(10, new Cosmetic("Manticore Book Off-hand", 37663, 5)),

		MANTICORE_ORB_OFFHAND(10, new Cosmetic("Manticore Orb Off-hand", 37662, 5)),

		BEACHBALL_ORB_OFFHAND(10, new Cosmetic("Beachball Orb Off-hand", 38004, 5)),

		CRYSTAL_PEACOCK_BOOK_OFFHAND(10, new Cosmetic("Crystal Peacock Book Off-hand", 38446, 5)),

		FAE_FAIRY_ORB_OFFHAND(10, new Cosmetic("Fae Fairy Orb Off-hand", 38428, 5)),

		FAE_FAIRY_BOOK_OFFHAND(10, new Cosmetic("Fae Fairy Book Off-hand", 38429, 5)),

		BRAIN_ORB_OFFHAND(10, new Cosmetic("Brain Orb Off-hand", 38983, 5)),

		SOLARIUS_SHIELD(30, new Cosmetic("Solarius shield", 24896, 5)),

		DWARVEN_SHIELD(20, new Cosmetic("Dwarven shield", 25287, 5)),

		PALADIN_SHIELD(20, new Cosmetic("Paladin shield", 26474, 5)),

		OWENS_SHIELD(30, new Cosmetic("Owen's shield", 28989, 5)),

		SUPERHERO_SHIELD(10, new Cosmetic("Superhero shield", 29427, 5)),

		AVIANSIE_SHIELD(20, new Cosmetic("Aviansie shield", 30347, 5)),

		ORKISH_SHIELD(25, new Cosmetic("Orkish shield", 30348, 5)),

		REPLICA_DRAGONFIRE_SHIELD(10, new Cosmetic("Replica dragonfire shield", 30630, 5)),

		SHADOW_OWENS_SHIELD(30, new Cosmetic("Shadow Owen's shield", 31127, 5)),

		REPLICA_KITESHIELD(10, new Cosmetic("Replica kiteshield", 31215, 5)),

		REPLICA_KITESHIELD_T(15, new Cosmetic("Replica kiteshield (t)", 31223, 5)),

		REPLICA_KITESHIELD_G(15, new Cosmetic("Replica kiteshield (g)", 31229, 5)),

		SHIELD_OF_ARRAV(20, new Cosmetic("Shield of Arrav", 34328, 5)),

		CURSED_ARRAV_SHIELD(25, new Cosmetic("Cursed Arrav Shield", 34317, 5)),

		BUCKET(10, new Cosmetic("Bucket", 34934, 5)),

		SURFBOARD_SHIELD(15, new Cosmetic("Surfboard Shield", 37995, 5)),

		SHELL_SHIELD(10, new Cosmetic("Shell Shield", 38001, 5)),

		ICE_SHIELD(25, new Cosmetic("Ice Shield", 39319, 5)),

		GRASS_SKIRT(10, new Cosmetic("Grass skirt", 24809, 7)),

		REPLICA_DRAGON_PLATESKIRT(10, new Cosmetic("Replica dragon plateskirt", 30632, 7)),

		REPLICA_DRAGON_PLATESKIRT_SP(10, new Cosmetic("Replica dragon plateskirt (sp)", 30633, 7)),

		REPLICA_DRAGON_PLATESKIRT_OR(10, new Cosmetic("Replica dragon plateskirt (or)", 30634, 7)),

		FIREMAKERS_TROUSERS(10, new Cosmetic("Firemaker's trousers", 30989, 7)),

		REPLICA_PLATESKIRT(10, new Cosmetic("Replica plateskirt", 31214, 7)),

		REPLICA_PLATESKIRT_T(10, new Cosmetic("Replica plateskirt (t)", 31222, 7)),

		REPLICA_PLATESKIRT_G(10, new Cosmetic("Replica plateskirt (g)", 31228, 7)),

		REPLICA_ELITE_VOID_KNIGHT_BOTTOM(10, new Cosmetic("Replica Elite Void Knight bottom", 31703, 7)),

		GILLYS_TROUSERS(10, new Cosmetic("Gilly's trousers", 35548, 7)),

		BLUE_GOEBIE_WARPAINT(10, new Cosmetic("Blue Goebie Warpaint", 35879, 7)),

		RED_GOEBIE_WARPAINT(10, new Cosmetic("Red Goebie Warpaint", 35881, 7)),

		YELLOW_GOEBIE_WARPAINT(10, new Cosmetic("Yellow Goebie Warpaint", 35883, 7)),

		TROPICAL_BRACELETS(10, new Cosmetic("Tropical bracelets", 24813, 9)),

		JAS_HANDS(15, new Cosmetic("Jas Hands", 34301, 9)),

		GILLYS_GLOVES(10, new Cosmetic("Gilly's gloves", 35545, 9)),

		BARE_FEET(10, new Cosmetic("Bare feet", 24811, 10)),

		JADINKO_SLIPPERS(20, new Cosmetic("Jadinko slippers", 34502, 10)),

		GILLYS_BOOTS(10, new Cosmetic("Gilly's boots", 35546, 10)),

		INFERNAL_GAZE(30, new Cosmetic("Infernal gaze", 29092, 14)),

		SERENE_GAZE(30, new Cosmetic("Serene gaze", 29094, 14)),

		VERNAL_GAZE(30, new Cosmetic("Vernal gaze", 29096, 14)),

		NOCTURNAL_GAZE(30, new Cosmetic("Nocturnal gaze", 29098, 14)),

		DIVINE_GAZE(30, new Cosmetic("Divine gaze", 29100, 14)),

		ABYSSAL_GAZE(30, new Cosmetic("Abyssal gaze", 29102, 14)),

		BLAZING_GAZE(30, new Cosmetic("Blazing gaze", 29104, 14)),

		MYSTICAL_GAZE(30, new Cosmetic("Mystical gaze", 29106, 14)),

		SKELETAL_WINGS(25, new Cosmetic("Skeletal wings", 30044, 11)),

		BUTTERFLY_WINGS(20, new Cosmetic("Butterfly wings", 30046, 11)),

		ZAMORAK_WINGS(25, new Cosmetic("Zamorak wings", 30048, 11)),

		ICYENIC_WINGS(25, new Cosmetic("Icyenic wings", 30050, 11)),

		DRAGONFLY_WINGS(20, new Cosmetic("Dragonfly wings", 30893, 11)),

		ARMADYL_WINGS(25, new Cosmetic("Armadyl wings", 30895, 11)),

		CRYSTALLINE_WINGS(25, new Cosmetic("Crystalline wings", 30897, 11)),

		PARADOX_WINGS(30, new Cosmetic("Paradox wings", 30899, 11)),

		DWARVEN_WINGS(25, new Cosmetic("Dwarven wings", 31823, 11)),

		BLADE_WINGS(25, new Cosmetic("Blade wings", 31825, 11)),

		LAVA_WINGS(30, new Cosmetic("Lava wings", 33655, 11)),

		BLOODBLADE_WINGS(25, new Cosmetic("Bloodblade wings", 33853, 11)),

		LAW_ETHEREAL_WINGS(30, new Cosmetic("Law ethereal wings", 34123, 11)),

		BLOOD_ETHEREAL_WINGS(30, new Cosmetic("Blood ethereal wings", 34124, 11)),

		DEATH_ETHEREAL_WINGS(30, new Cosmetic("Death ethereal wings", 34125, 11)),

		INFINITY_ETHEREAL_WINGS(30, new Cosmetic("Infinity ethereal wings", 34126, 11)),

		SAPPHIRE_GEMSTONE_WINGS(30, new Cosmetic("Sapphire gemstone wings", 34129, 11)),

		EMERALD_GEMSTONE_WINGS(30, new Cosmetic("Emerald gemstone wings", 34130, 11)),

		RUBY_GEMSTONE_WINGS(30, new Cosmetic("Ruby gemstone wings", 34131, 11)),

		MAGIC_GEMSTONE_WINGS(40, new Cosmetic("Magic gemstone wings", 34132, 11)),

		FREEFALL_WINGS(35, new Cosmetic("Freefall wings", 34133, 11)),

		SILVER_BLADED_WINGS(25, new Cosmetic("Silver bladed wings", 34135, 11)),

		DRAKANS_WINGS(25, new Cosmetic("Drakan's wings", 35684, 11)),

		BANNER_OF_FARRADORN(15, new Cosmetic("Banner of Farradorn", 35956, 11)),

		BLESSED_BANNER_OF_FARRADORN(20, new Cosmetic("Blessed Banner of Farradorn", 35957, 11)),

		TH_ANNIVERSARY_CRACKER_WINGS(40, new Cosmetic("15th Anniversary cracker wings", 36268, 11)),

		FURY_WINGS(30, new Cosmetic("Fury wings", 37112, 11)),

		DRYAD_WINGS(35, new Cosmetic("Dryad Wings", 37195, 11)),

		RUNIC_ESSENCE_WINGS(30, new Cosmetic("Runic Essence Wings", 37196, 11)),

		CLAWDIA_WINGS(20, new Cosmetic("Clawdia Wings", 37989, 11)),

		INFLORESCENT_WINGS(30, new Cosmetic("Inflorescent Wings", 38519, 11)),

		DECAYING_WINGS(30, new Cosmetic("Decaying Wings", 38520, 11)),

		;

		private String name;
		private int price;
		private Cosmetic[] cosmetics;

		private Cosmetics(Cosmetic... cosmetics) {// For Default Price
			this(DEFAULT_PRICE_FULL_OUTFIT, cosmetics);
		}

		private Cosmetics(int price, Cosmetic... cosmetics) {
			this.name = Utils.formatPlayerNameForDisplay(toString());
			this.cosmetics = cosmetics;
			this.price = price;
		}

		private Cosmetics(Cosmetic cosmetic) {// For Default Price
			this(DEFAULT_PRICE_SINGLE_PIECE, cosmetic);
		}

		private Cosmetics(int price, Cosmetic cosmetic) {
			this.name = cosmetic.getName();
			this.price = price;
			this.cosmetics = new Cosmetic[] { cosmetic };
		}

		public String getName() {
			return name;
		}

		public int getPrice() {
			return price;
		}

		public Cosmetic[] getCosmetics() {
			return cosmetics;
		}

	}

	public static final class Cosmetic {

		private String name;
		private int itemId;
		private int slot;

		public Cosmetic(String name, int itemId, int slot) {
			this.name = name;
			this.itemId = itemId;
			this.slot = slot;
		}

		public String getName() {
			return name;
		}

		public int getItemId() {
			return itemId;
		}

		public int getSlot() {
			return slot;
		}

	}

}
