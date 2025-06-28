package com.rs.game.player;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.activites.duel.DuelControler;
import com.rs.game.item.Item;
import com.rs.game.item.ItemsContainer;
import com.rs.game.player.Equipment.SavedCosmetic;
import com.rs.game.player.actions.firemaking.Bonfire;
import com.rs.game.player.content.ItemConstants;
import com.rs.game.player.controllers.DungeonController;
import com.rs.utils.ItemExamines;
import com.rs.utils.ItemWeights;

public final class Equipment implements Serializable {

	public static final class SavedCosmetic implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3243926269985535095L;
		private ItemsContainer<Item> cosmeticItems;
		private String cosmeticName;

		public SavedCosmetic(String cosmeticName, ItemsContainer<Item> cosmeticItems) {
			this.cosmeticName = cosmeticName;
			this.cosmeticItems = cosmeticItems;
		}

		public ItemsContainer<Item> getCosmeticItems() {
			return cosmeticItems;
		}

		public String getCosmeticName() {
			return cosmeticName;
		}

	}

	private static final long serialVersionUID = -4147163237095647617L;

	public static final byte SLOT_HAT = 0, SLOT_CAPE = 1, SLOT_AMULET = 2, SLOT_WEAPON = 3, SLOT_CHEST = 4,
			SLOT_SHIELD = 5, SLOT_LEGS = 7, SLOT_HANDS = 9, SLOT_FEET = 10, SLOT_RING = 12, SLOT_ARROWS = 13,
			SLOT_AURA = 14, SLOT_POCKET = 17; 

	public static boolean hideArms(Item item) {
		return item.getDefinitions().getEquipType() == 6;
	}
	
	/**public static boolean scrimshaw(Item item) {
		String name = item.getName().toLowerCase();
		return item.getDefinitions().getEquipType() == 17 || name.contains("scrimshaw");
	}**/

	public static boolean hideHair(Item item) {
		String name = item.getName().toLowerCase();
		return item.getDefinitions().getEquipType() == 8 || name.contains("full helm")
				|| (name.startsWith("void") && name.contains("ranger")) || name.contains("dragon mask")
				|| name.contains("ween mask") || name.contains("fox mask");
	}

	public static boolean showBear(Item item) {
		String name = item.getName().toLowerCase();
		return !hideHair(item) || name.contains("horns") || name.contains("hat") || name.contains("afro")
				|| name.contains("bronze full") || name.contains("cowl") || name.contains("tattoo")
				|| name.contains("headdress") || name.contains("hood") || name.contains("bearhead")
				|| name.equals("santa hat") || name.contains("partyhat") || name.contains("sleeping")
				|| name.contains("coif") || name.contains("wig") || name.contains("bandana") || name.contains("mitre")
				|| (name.contains("mask") && !name.contains("ween") && !name.contains("sirenic"))
				|| name.contains("med helm") || name.contains("chicken head")
				|| (name.contains("helm") && !name.contains("full") || name.contains("headwear")
						|| item.getId() == 32386);
	}

	private ItemsContainer<Item> items;
	private boolean[] hiddenSlots;
	private boolean hideAll;
	private transient double equipmentWeight;

	public void checkItems() {
		if (cosmeticItems == null)
			cosmeticItems = new ItemsContainer<>(19, false);
		if (keepSakeItems == null)
			keepSakeItems = new ArrayList<>(50);
		if (savedCosmetics == null)
			savedCosmetics = new ArrayList<>();
		int size = items.getSize();
		int newSize = 15;
		if (size != newSize) {
			Item[] copy = items.getItemsCopy();
			items = new ItemsContainer<Item>(newSize, false);
			items.addAll(copy);
		}
		for (int i = 0; i < size; i++) {
			Item item = items.get(i);
			if (item == null)
				continue;
			if (!ItemConstants.canWear(item, player)) {
				items.set(i, null);
				player.getInventory().addItemDrop(item.getId(), item.getAmount());
			}
		}
	}

	private transient Player player;

	private transient int equipmentHpIncrease;
	// cosmetics
		private ItemsContainer<Item> cosmeticItems;
		private transient ItemsContainer<Item> cosmeticPreviewItems;
		private List<Item> keepSakeItems;
		private List<SavedCosmetic> savedCosmetics;
		private int costumeColor;


	public static final int[] DISABLED_SLOTS = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0 };

	// try
	public static int getItemSlot(int itemId) {
		if (itemId == 14632)
			return Equipment.SLOT_WEAPON;
		if (itemId == 1917 || itemId == 1963 || itemId == 8794 || itemId == 288)
			return -1;
		return ItemDefinitions.getItemDefinitions(itemId).getEquipSlot();
	}

	public static boolean isTwoHandedWeapon(Item item) {
		return item.getDefinitions().getEquipType() == 5;
	}

	/**
	 * Sets the attack render animation.
	 * 
	 * @param player
	 *            The player to set the r'emote to.
	 */
	public int getWeaponStance() {
		boolean combatStance = player.getCombatDefinitions().isCombatStance();
		Item weapon = items.get(3);
		if (weapon == null) {
			Item offhand = items.get(SLOT_SHIELD);
			if (offhand == null)
				return combatStance ? 2688 : 2699;
			int emote = offhand.getDefinitions().getCombatOpcode(combatStance ? 2955 : 2954);
			return emote == 0 ? combatStance ? 2688 : 2699 : emote;
		}
		int emote = weapon.getDefinitions().getCombatOpcode(combatStance ? 2955 : 2954);
		if (weapon.getId() == 4084) // sled exception
			return 1119;
		return emote == 0 ? combatStance ? 2688 : 2699 : emote;
	}

	public Equipment() {
		items = new ItemsContainer<Item>(15, false);
		cosmeticItems = new ItemsContainer<>(15, false);
		keepSakeItems = new ArrayList<>(50);
		savedCosmetics = new ArrayList<>();
		hiddenSlots = new boolean[15];
	}

	public void deleteItem(int itemId, int amount) {
		Item[] itemsBefore = items.getItemsCopy();
		items.remove(new Item(itemId, amount));
		refreshItems(itemsBefore);
	}

	public int getAmmoId() {
		Item item = items.get(SLOT_ARROWS);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getAmuletId() {
		Item item = items.get(SLOT_AMULET);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getAuraId() {
		Item item = items.get(SLOT_AURA);
		if (item == null)
			return -1;
		return item.getId();
	}
	
	/**public int getScrimshawId() {
		Item item = items.get(SLOT_POCKET);
		if (item == null)
			return -1;
		return item.getId();
	}**/

	public int getBootsId() {
		Item item = items.get(SLOT_FEET);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getCapeId() {
		Item item = items.get(SLOT_CAPE);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getChestId() {
		Item item = items.get(SLOT_CHEST);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getEquipmentHpIncrease() {
		return equipmentHpIncrease;
	}

	public int getGlovesId() {
		Item item = items.get(SLOT_HANDS);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getHatId() {
		Item item = items.get(SLOT_HAT);
		if (item == null)
			return -1;
		return item.getId();
	}

	public Item getItem(int slot) {
		return items.get(slot);
	}

	public ItemsContainer<Item> getItems() {
		return items;
	}

	public int getLegsId() {
		Item item = items.get(SLOT_LEGS);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getRingId() {
		Item item = items.get(SLOT_RING);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getShieldId() {
		Item item = items.get(SLOT_SHIELD);
		if (item == null)
			return -1;
		return item.getId();
	}

	public int getWeaponId() {
		Item item = items.get(SLOT_WEAPON);
		if (item == null)
			return -1;
		return item.getId();
	}

	public boolean hasShield() {
		return items.get(5) != null;
	}

	public boolean hasTwoHandedWeapon() {
		Item weapon = items.get(SLOT_WEAPON);
		return weapon != null && isTwoHandedWeapon(weapon);
	}

	public void init() {
		if (cosmeticItems == null)
			cosmeticItems = new ItemsContainer<>(15, false);
		if (hiddenSlots == null)
			hiddenSlots = new boolean[15];
		if (keepSakeItems == null)
			keepSakeItems = new ArrayList<>(50);
		if (savedCosmetics == null)
			savedCosmetics = new ArrayList<>();
		player.getPackets().sendItems(94, items);
		refresh(null);
	}

	public void refresh(int... slots) {
		if (slots != null) {
			if (player.getTemporaryAttributtes().get("Cosmetics") != null) {
				Item[] cosmetics = items.getItemsCopy();
				for (int i = 0; i < cosmetics.length; i++) {
					Item item = cosmetics[i];
					if (item == null)
						cosmetics[i] = new Item(0);
				}
				player.getPackets().sendUpdateItems(94, cosmetics, slots);
			} else
				player.getPackets().sendUpdateItems(94, items, slots);
			player.getCombatDefinitions().checkAttackStyle();
		}
		player.getCombatDefinitions().refreshBonuses();
		refreshConfigs(slots == null);
	}

	public void refreshConfigs(boolean init) {
		double hpIncrease = 0;
		for (int index = 0; index < items.getSize(); index++) {
			Item item = items.get(index);
			if (item == null)
				continue;
			refreshItems(item);
			int id = item.getId();
			if (index == Equipment.SLOT_HAT) {
				if (id == 20135 || id == 20137 // torva
						|| id == 20147 || id == 20149 // pernix
						|| id == 20159 || id == 20161 // virtus
						|| id == 43166 || id == 43155 // Elite
						|| id == 42964 || id == 42973 || id == 42991 || id == 43009 || id == 43036 //dye
						|| id == 42955 || id == 42982 || id == 43000 || id == 43018 || id == 43027 //dye
						
						)
					hpIncrease += 66;

				if (id == 35159 || id == 35174 || id == 35189)// ATCHO HELM
					hpIncrease += 150;
				if (id == 16711 || id == 20824)// Primal helm
					hpIncrease += 200;

			} else if (index == Equipment.SLOT_SHIELD) {
				if (id == 30014 || id == 30018 || id == 30022// ROTS Shield
					|| id == 17361 || id == 17362	)
					hpIncrease += 735;

				if (id == 41450)
					hpIncrease += 500;
			} else if (index == Equipment.SLOT_CHEST) {
				if (id == 20139 || id == 20141 // torva
						|| id == 20151 || id == 20153 // pernix
						|| id == 20163 || id == 20165 // virtus
						|| id == 43169 || id == 43158 // Elite
						|| id == 42967 || id == 42976 || id == 42994 || id == 43012 || id == 43039 //dye
						|| id == 42958 || id == 42985 || id == 43003 || id == 43021 || id == 43030 //dye
				)
					hpIncrease += 200;
				if (id == 35165 || id == 35180 || id == 35195)// ATCHO CHEST
					hpIncrease += 400;
				if (id == 17259 || id == 16733 || id == 20822)// PRIMAL CHEST
					hpIncrease += 550;
			} else if (index == Equipment.SLOT_LEGS) {
				if (id == 20143 || id == 20145 // torva
						|| id == 20155 || id == 20157 // pernix
						|| id == 20167 || id == 20169 // virtus
						|| id == 43172 || id == 43161 // Elite
						|| id == 42970 || id == 42979 || id == 42997 || id == 43015 || id == 43042 //dye
						|| id == 42961 || id == 42988 || id == 43006 || id == 43024 || id == 43033 //dye						
				)
					hpIncrease += 134;
				if (id == 35167 || id == 35182 || id == 35197) // ATCHO LEGS
					hpIncrease += 250;
				if (id == 20823 || id == 16667 || id == 16689) // ATCHO LEGS
					hpIncrease += 325;
			}
		}
		if (player.getLastBonfire() > 0) {
			int maxhp = player.getSkills().getLevel(Skills.HITPOINTS) * 10;
			hpIncrease += (maxhp
					* (Bonfire.getBonfireBoostMultiplier(player) * (player.getPerkManager().thePiromaniac ? 1.5 : 1)))
					- maxhp;
		}
		if (player.getHpBoostMultiplier() != 0) {
			int maxhp = player.getSkills().getLevel(Skills.HITPOINTS) * 10;
			hpIncrease += maxhp * player.getHpBoostMultiplier();
		}
		if (hpIncrease != equipmentHpIncrease) {
			equipmentHpIncrease = (int) hpIncrease;
			if (!init)
				player.refreshHitPoints();
		}
		double w = 0;
		for (Item item : items.getItems()) {
			if (item == null)
				continue;
			w += ItemWeights.getWeight(item, true);
		}
		equipmentWeight = w;
		player.getPackets().refreshWeight();
	}

	public double getEquipmentWeight() {
		return equipmentWeight;
	}

	public void refreshItems(Item[] itemsBefore) {
		int[] changedSlots = new int[itemsBefore.length];
		int count = 0;
		for (int index = 0; index < itemsBefore.length; index++) {
			if (itemsBefore[index] != items.getItems()[index])
				changedSlots[count++] = index;
		}
		int[] finalChangedSlots = new int[count];
		System.arraycopy(changedSlots, 0, finalChangedSlots, 0, count);
		refresh(finalChangedSlots);
	}

	public void removeAmmo(int ammoId, int ammount) {
		if (ammount == -1) {
			items.remove(SLOT_WEAPON, new Item(ammoId, 1));
			refresh(SLOT_WEAPON);
		} else {
			items.remove(SLOT_ARROWS, new Item(ammoId, ammount));
			refresh(SLOT_ARROWS);
		}
		player.getGlobalPlayerUpdater().generateAppearenceData();
	}

	public void setGear(byte gear) {
		int slot = (int) gear;
		items.remove(slot, new Item(items.get(slot)));
		refresh(gear);
	}

	public void reset() {
		items.reset();
		init();
	}

	public void sendExamine(int slotId) {
		Item item = items.get(slotId);
		if (item == null)
			return;
		player.getPackets().sendGameMessage(ItemExamines.getExamine(item));
	}

	public void setEquipmentHpIncrease(int hp) {
		this.equipmentHpIncrease = hp;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public boolean wearingArmour() {
		return getItem(SLOT_HAT) != null || getItem(SLOT_CAPE) != null || getItem(SLOT_AMULET) != null
				|| getItem(SLOT_WEAPON) != null || getItem(SLOT_CHEST) != null || getItem(SLOT_SHIELD) != null
				|| getItem(SLOT_LEGS) != null || getItem(SLOT_HANDS) != null || getItem(SLOT_FEET) != null
				|| getItem(SLOT_ARROWS) != null || getItem(SLOT_AURA) != null || getItem(SLOT_RING) != null;
	}

	public ItemsContainer<Item> getItemsContainer() {
		return items;
	}

	public void set(int i, Item item) {
		items.set(i, item);
		refresh();
	}

	public boolean containsOneItem(int... itemIds) {
		for (int itemId : itemIds) {
			if (items.containsOne(new Item(itemId, 1)))
				return true;
		}
		return false;
	}

	/**
	 * Explicitly used for Warpriest upgrading.
	 * 
	 * @param itemId
	 *            the Item ID to upgrade.
	 */
	private boolean refreshItems(Item item) {
		int defenceLvl = player.getSkills().getLevelForXp(Skills.DEFENCE);
		int items50[] = { 30306, 30309, 30312, 30315, 30318, 30321, 28773, 28776, 28779, 28782, 28785, 28788, 28755,
				28758, 28761, 28764, 28767, 28770, 30288, 30291, 30294, 30297, 30300, 30303 };
		int items75[] = { 30307, 30310, 30313, 30316, 30319, 30322, 28774, 28777, 28780, 28783, 28786, 28789, 28756,
				28759, 28762, 28765, 28768, 28771, 30289, 30292, 30295, 30298, 30301, 30304 };
		if (defenceLvl >= 50 && defenceLvl < 75) {
			for (int itemId : items50) {
				if (item.getId() == itemId) {
					player.sendMessage("Your " + item.getName() + " upgraded to a higher tier.");
					item.setId(item.getId() + 1);
					return true;
				}
			}
		}
		if (defenceLvl >= 75) {
			for (int itemId : items75) {
				if (item.getId() == itemId) {
					player.sendMessage("Your " + item.getName() + " upgraded to a higher tier.");
					item.setId(item.getId() + 1);
					return true;
				}
			}
			for (int itemId : items50) {
				if (item.getId() == itemId) {
					player.sendMessage("Your " + item.getName() + " upgraded to a higher tier.");
					item.setId(item.getId() + 2);
					return true;
				}
			}
		}
		return false;
	}

	public int getWeaponEndCombatEmote() {
		Item weapon = items.get(3);
		if (weapon == null) {
			Item offhand = items.get(SLOT_SHIELD);
			if (offhand == null)
				return -1;
			int emote = offhand.getDefinitions().getCombatOpcode(2918);
			return emote == 0 ? 18025 : emote;
		}
		int emote = weapon.getDefinitions().getCombatOpcode(2918);
		return emote == 0 ? 18025 : emote;
	}

	public ItemsContainer<Item> getCosmeticItems() {
		return cosmeticItems;
	}

	public List<Item> getKeepSakeItems() {
		return keepSakeItems;
	}

	public boolean containsKeepSakeItem(int itemId) {
		for (Item item : keepSakeItems) {
			if (item == null)
				continue;
			if (item.getId() == itemId)
				return true;
		}
		return false;
	}


	public List<SavedCosmetic> getSavedCosmetics() {
		if (savedCosmetics == null)
			savedCosmetics = new ArrayList<>();
		return savedCosmetics;
	}
	

	public boolean[] getHiddenSlots() {
		return hiddenSlots;
	}

	public boolean isCanDisplayCosmetic() {
		if (player.getControlerManager().getControler() != null
				&& player.getControlerManager().getControler() instanceof DuelControler)
			return false;
		if (player.getControlerManager().getControler() != null
				&& player.getControlerManager().getControler() instanceof DungeonController)
			return false;
		return !player.isCanPvp();
	}

	public int getCostumeColor() {
		return costumeColor;
	}

	public void setCostumeColor(int costumeColor) {
		this.costumeColor = costumeColor;
		player.getGlobalPlayerUpdater().generateAppearenceData();
	}

	public boolean isHideAll() {
		return hideAll;
	}

	public void setHideAll(boolean hideAll) {
		this.hideAll = hideAll;
	}

	public void resetCosmetics() {
		cosmeticItems.reset();
	}


	public static final class Cosmetic implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3243926269985535095L;
		private ItemsContainer<Item> cosmeticItems;
		private boolean[] hiddenSlots;
		private String cosmeticName;

		public Cosmetic(String cosmeticName, ItemsContainer<Item> cosmeticItems, boolean[] hiddenSlots) {
			this.cosmeticName = cosmeticName;
			this.cosmeticItems = cosmeticItems;
			this.hiddenSlots = hiddenSlots;
		}

		public ItemsContainer<Item> getCosmeticItems() {
			return cosmeticItems;
		}

		public boolean[] getHiddenSlots() {
			return hiddenSlots;
		}

		public String getCosmeticName() {
			return cosmeticName;
		}

	}

	public int getWeaponRenderEmote() {
		Item weapon = items.get(3);
		if (weapon == null)
			return 1426;
		if (weapon.getName().toLowerCase().contains("mace") || weapon.getName().toLowerCase().contains("halberd")) {
			return 1426;
		}
		
		return weapon.getDefinitions().getRenderAnimId();
	}
	public ItemsContainer<Item> getCosmeticPreviewItems() {
		return cosmeticPreviewItems;
	}

	public void setCosmeticPreviewItems(ItemsContainer<Item> cosmeticPreviewItems) {
		this.cosmeticPreviewItems = cosmeticPreviewItems;
	}

	
}
