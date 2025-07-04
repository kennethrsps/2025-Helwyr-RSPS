package com.rs.game.player;

import java.io.Serializable;

import com.rs.cache.loaders.ItemDefinitions;
import com.rs.game.Animation;
import com.rs.game.item.Item;
import com.rs.game.player.combat.PlayerCombat;
import com.rs.game.player.content.interfaces.potionTimer.PotionTimersInter;
import com.rs.utils.Colors;
import com.rs.utils.ItemBonuses;
import com.rs.utils.Utils;

public final class CombatDefinitions implements Serializable {

	private static final long serialVersionUID = 2102201264836121104L;
	public static final int STAB_ATTACK = 0, SLASH_ATTACK = 1, CRUSH_ATTACK = 2, RANGE_ATTACK = 4, MAGIC_ATTACK = 3;
	public static final int STAB_DEF = 5, SLASH_DEF = 6, CRUSH_DEF = 7, RANGE_DEF = 9, MAGIC_DEF = 8,
			SUMMONING_DEF = 10;
	public static final int STRENGTH_BONUS = 14, RANGED_STR_BONUS = 15, MAGIC_DAMAGE = 17, PRAYER_BONUS = 16;
	public static final int ABSORVE_MELEE_BONUS = 11, ABSORVE_RANGE_BONUS = 13, ABSORVE_MAGE_BONUS = 12;

	public static final int SHARED = -1;
	public static final int MELEE_TYPE = 0, RANGE_TYPE = 1, MAGIC_TYPE = 2, ALL_TYPE = 3;
	public static final String[] BONUS_LABELS = { "Stab", "Slash", "Crush", "Magic", "Range", "Stab", "Slash", "Crush",
			"Magic", "Range", "Summoning", "Absorb Melee", "Absorb Magic", "Absorb Ranged", "Strength", "Ranged Str",
			"Prayer", "Magic Damage" };

	// saving stuff

	private transient Player player;
	private transient boolean usingSpecialAttack;
	private transient int[] bonuses;
	private byte attackStyle;
	private byte specialAttackPercentage;
	private boolean autoRelatie;
	private byte sortSpellBook;
	private boolean showCombatSpells;
	private boolean showSkillSpells;
	private boolean showMiscallaneousSpells;
	private boolean showTeleportSpells;
	private boolean defensiveCasting;
	private boolean sheathe;
	private transient long mainHandDelay, offHandDelay;
	private transient boolean forceNoSheathe;
	private transient boolean instantAttack;
	private transient boolean dungeonneringSpellBook;

	private byte spellBook;

	private byte autoCastSpell;

	public CombatDefinitions() {
		specialAttackPercentage = 100;
		autoRelatie = true;
		showCombatSpells = true;
		showSkillSpells = true;
		showMiscallaneousSpells = true;
		showTeleportSpells = true;
		sheathe = true;
	}

	public void checkAttackStyle() {
		if (autoCastSpell == 0)
			setAttackStyle(attackStyle);
	}

	public void decreaseSpecialAttack(int ammount) {
		usingSpecialAttack = false;
		refreshUsingSpecialAttack();
		if (ammount > 0) {
			specialAttackPercentage -= ammount;
			refreshSpecialAttackPercentage();
		}
	}

	public int getAttackStyle() {
		return attackStyle;
	}

	public int getAutoCastSpell() {
		return autoCastSpell;
	}

	public int[] getBonuses() {
		return bonuses;
	}

	public int getSpecialAttackPercentage() {
		return specialAttackPercentage;
	}

	public int getSpellAutoCastConfigValue() {
		if (dungeonneringSpellBook) {
			switch (autoCastSpell) {
			case 25:
				return 103;
			case 27:
				return 105;
			case 28:
				return 107;
			case 30:
				return 109;
			case 32:
				return 111;
			case 36:
				return 113;
			case 37:
				return 115;
			case 41:
				return 117;
			case 42:
				return 119;
			case 43:
				return 121;
			case 45:
				return 123;
			case 47:
				return 125;
			case 48:
				return 127;
			case 49:
				return 129;
			case 54:
				return 131;
			case 58:
				return 133;
			case 61:
				return 135;
			case 62:
				return 137;
			case 63:
				return 139;
			case 67:
				return 141;
			default:
				return 0;
			}
		}
		if (spellBook == 0) {
			switch (autoCastSpell) {
			case 25:
				return 3;
			case 28:
				return 5;
			case 30:
				return 7;
			case 32:
				return 9;
			case 34:
				return 11; // air bolt
			case 39:
				return 13;// water bolt
			case 42:
				return 15;// earth bolt
			case 45:
				return 17; // fire bolt
			case 49:
				return 19;// air blast
			case 52:
				return 21;// water blast
			case 56:
				return 37; // magic dart
			case 58:
				return 23;// earth blast
			case 63:
				return 25;// fire blast
			case 66: // Saradomin Strike
				return 41;
			case 67:// Claws of Guthix
				return 39;
			case 68:// Flames of Zammorak
				return 43;
			case 70:
				return 27;// air wave
			case 73:
				return 29;// water wave
			case 77:
				return 31;// earth wave
			case 80:
				return 33;// fire wave
			case 84:
				return 47;
			case 87:
				return 49;
			case 89:
				return 51;
			case 91:
				return 53;
			case 98:
				return 143; // wind rush
			case 99:
				return 145;
			default:
				return 0;
			}
		} else if (spellBook == 1) {
			switch (autoCastSpell) {
			case 28:
				return 63;
			case 32:
				return 65;
			case 24:
				return 67;
			case 20:
				return 69;
			case 30:
				return 71;
			case 34:
				return 73;
			case 26:
				return 75;
			case 22:
				return 77;
			case 29:
				return 79;
			case 33:
				return 81;
			case 25:
				return 83;
			case 21:
				return 85;
			case 31:
				return 87;
			case 35:
				return 89;
			case 27:
				return 91;
			case 23:
				return 93;
			case 36:
				return 95;
			case 37:
				return 99;
			case 38:
				return 97;
			case 39:
				return 101;
			default:
				return 0;
			}
		} else {
			return 0;
		}
	}

	public int getSpellBook() {
		if (dungeonneringSpellBook)
			return 950; // dung book
		else {
			if (spellBook == 0)
				return 192; // normal
			else if (spellBook == 1)
				return 193; // ancients
			else
				return 430; // lunar
		}
	}

	public int getSpellId() {
		Integer tempCastSpell = (Integer) player.getTemporaryAttributtes().get("tempCastSpell");
		if (tempCastSpell != null)
			return tempCastSpell + 256;
		return autoCastSpell;
	}

	public boolean hasRingOfVigour() {
		return player.getEquipment().getRingId() == 19669;
	}

	public void init() {
		refreshUsingSpecialAttack();
		refreshSpecialAttackPercentage();
		refreshAutoRelatie();
		refreshAttackStyle();
		refreshSpellBook();
		refreshAutoCastSpell();
		refreshSpellBookScrollBar_DefCast();
	}

	public boolean isAutoRelatie() {
		return autoRelatie;
	}

	public boolean isDefensiveCasting() {
		return defensiveCasting;
	}

	public boolean isDungeonneringSpellBook() {
		return dungeonneringSpellBook;
	}

	public boolean isInstantAttack() {
		return instantAttack;
	}

	public boolean isUsingSpecialAttack() {
		return usingSpecialAttack;
	}

	public void refreshAttackStyle() {
		player.getPackets().sendConfig(43, autoCastSpell > 0 ? 4 : attackStyle);
	}

	public void refreshAutoCastSpell() {
		refreshAttackStyle();
		player.getPackets().sendConfig(108, getSpellAutoCastConfigValue());
	}

	public void refreshAutoRelatie() {
		player.getPackets().sendConfig(172, autoRelatie ? 0 : 1);
	}

	public void refreshBonuses() {
		bonuses = new int[18];
		for (Item item : player.getEquipment().getItems().getItems()) {
			if (item == null)
				continue;
			int[] bonuses = ItemBonuses.getItemBonuses(item.getId());
			if (bonuses == null)
				continue;
			for (int id = 0; id < bonuses.length; id++) {
				//if (id == RANGED_STR_BONUS && this.bonuses[RANGED_STR_BONUS] != 0)
				//	continue;
				this.bonuses[id] += bonuses[id];
			}
		}
	}

	public void refreshSpecialAttackPercentage() {
		player.getPackets().sendConfig(300, specialAttackPercentage * 10);
		double newPercentagee = specialAttackPercentage > 90 && specialAttackPercentage < 100 ? 90 : specialAttackPercentage;
		if(specialAttackPercentage >= 90 && specialAttackPercentage < 100)
			newPercentagee = 90;
		else if(specialAttackPercentage >= 80 && specialAttackPercentage < 90) 
			newPercentagee = 80;
		else if(specialAttackPercentage >= 70 && specialAttackPercentage < 80) 
			newPercentagee = 70;
		double specTime =  (double) (100 - newPercentagee) /  (double) 100;//25
		//player.sm("specTime: "+specTime);
		double currentPlusMili = 300000 * specTime;
		player.getPotiontimers().slotTimerArray[PotionTimersInter.SPEC] = (long) (currentPlusMili + Utils.currentTimeMillis());
	}

	public void refreshSpellBook() {
		if (isDungeonneringSpellBook()) {
			player.getPackets().sendConfig(1376,
					sortSpellBook << 18 | (showCombatSpells ? 0 : 1 << 21) | (showSkillSpells ? 0 : 1 << 22)
							| (showMiscallaneousSpells ? 0 : 1 << 23) | (showTeleportSpells ? 0 : 1 << 24));
		} else if (spellBook == 0) {
			player.getPackets().sendConfig(1376,
					sortSpellBook | (showCombatSpells ? 0 : 1 << 9) | (showSkillSpells ? 0 : 1 << 10)
							| (showMiscallaneousSpells ? 0 : 1 << 11) | (showTeleportSpells ? 0 : 1 << 12));
		} else if (spellBook == 1) {
			player.getPackets().sendConfig(1376,
					sortSpellBook << 3 | (showCombatSpells ? 0 : 1 << 16) | (showTeleportSpells ? 0 : 1 << 17));
		} else if (spellBook == 2) {
			player.getPackets().sendConfig(1376, sortSpellBook << 6 | (showCombatSpells ? 0 : 1 << 13)
					| (showMiscallaneousSpells ? 0 : 1 << 14) | (showTeleportSpells ? 0 : 1 << 15));
		}
	}

	public void refreshSpellBookScrollBar_DefCast() {
		player.getPackets().sendConfig(439, (dungeonneringSpellBook ? 3 : spellBook) + (defensiveCasting ? 0 : 1 << 8));
	}

	public void refreshUsingSpecialAttack() {
		player.getPackets().sendConfig(301, usingSpecialAttack ? 1 : 0);
	}

	public void removeDungeonneringBook() {
		if (dungeonneringSpellBook) {
			dungeonneringSpellBook = false;
			player.getInterfaceManager().sendMagicBook();
		}
	}

	public void resetSpecialAttack() {
		decreaseSpecialAttack(0);
		specialAttackPercentage = 100;
		refreshSpecialAttackPercentage();
	}

	public void resetSpells(boolean removeAutoSpell) {
		player.getTemporaryAttributtes().remove("tempCastSpell");
		if (removeAutoSpell) {
			setAutoCastSpell(0);
			refreshAutoCastSpell();
		}
	}

	public void restoreSpecialAttack() {
		if (player.getFamiliar() != null)
			player.getFamiliar().restoreSpecialAttack(15);
		if (specialAttackPercentage == 100)
			return;
		restoreSpecialAttack(10);
		if (specialAttackPercentage == 100 || specialAttackPercentage == 50)
			player.getPackets().sendGameMessage(
					"<col=00FF00><shad=000000>Your special attack energy is now " + specialAttackPercentage + "%.",
					true);
	}

	public void restoreSpecialAttack(int percentage) {
		if (specialAttackPercentage >= 100 || player.getInterfaceManager().containsScreenInter())
			return;
		specialAttackPercentage += specialAttackPercentage > (100 - percentage) ? 100 - specialAttackPercentage
				: percentage;
		refreshSpecialAttackPercentage();
	}

	public void sendUnlockAttackStylesButtons() {
		for (int componentId = 7; componentId <= 10; componentId++)
			player.getPackets().sendUnlockIComponentOptionSlots(884, componentId, -1, 0, 0);
	}

	public void setAttackStyle(int style) {
		int maxSize = 3;
		int weaponId = player.getEquipment().getWeaponId();
		String name = weaponId == -1 ? "" : ItemDefinitions.getItemDefinitions(weaponId).getName().toLowerCase();
		if (weaponId == -1 || PlayerCombat.isRanging(player) != 0 || name.contains("whip") || name.contains("halberd"))
			maxSize = 2;
		if (style > maxSize)
			style = maxSize;
		if (style != attackStyle) {
			attackStyle = (byte) style;
			if (autoCastSpell > 1)
				resetSpells(true);
			else
				refreshAttackStyle();
		} else if (autoCastSpell > 1)
			resetSpells(true);
	}

	public void setAutoCastSpell(int id) {
		autoCastSpell = (byte) id;
		refreshAutoCastSpell();
	}

	public void setAutoRelatie(boolean autoRelatie) {
		this.autoRelatie = autoRelatie;
	}

	public void setInstantAttack(boolean instantAttack) {
		this.instantAttack = instantAttack;
	}

	public void setPlayer(Player player) {
		this.player = player;
		bonuses = new int[18];
	}

	public void setSortSpellBook(int sortId) {
		this.sortSpellBook = (byte) sortId;
		refreshSpellBook();
	}

	public void setSpecialAttack(int special) {
		decreaseSpecialAttack(0);
		specialAttackPercentage = (byte) special;
		refreshSpecialAttackPercentage();
	}

	public void setSpecialAttackPercentage(int amount) {
		specialAttackPercentage += amount;
		refreshSpecialAttackPercentage();
	}

	public void setSpellBook(int id) {
		if (id == 3)
			dungeonneringSpellBook = true;
		else
			spellBook = (byte) id;
		refreshSpellBookScrollBar_DefCast();
		player.getInterfaceManager().sendMagicBook();
		player.getCombatDefinitions().resetSpells(true);
	}

	public void switchAutoRelatie() {
		autoRelatie = !autoRelatie;
		refreshAutoRelatie();
	}

	public void switchDefensiveCasting() {
		defensiveCasting = !defensiveCasting;
		refreshSpellBookScrollBar_DefCast();
	}

	public void switchShowCombatSpells() {
		showCombatSpells = !showCombatSpells;
		refreshSpellBook();
	}

	public void switchShowMiscallaneousSpells() {
		showMiscallaneousSpells = !showMiscallaneousSpells;
		refreshSpellBook();
	}

	public void switchShowSkillSpells() {
		showSkillSpells = !showSkillSpells;
		refreshSpellBook();
	}

	public void switchShowTeleportSkillSpells() {
		showTeleportSpells = !showTeleportSpells;
		refreshSpellBook();
	}

	public void switchUsingSpecialAttack() {
		usingSpecialAttack = !usingSpecialAttack;
		refreshUsingSpecialAttack();
	}

	private boolean isForceNoSheathe() {
		return player.isCanPvp();
	}

	public boolean isCombatStance() {
		return combatStance;
	}

	public void setCombatStance(boolean combatStance) {
		this.combatStance = combatStance;
	}

	private transient boolean combatStance;

	public void processCombatStance() {
		boolean forceSheathe = isForceNoSheathe();
		boolean underCombat = player.isUnderCombat();
		if (forceNoSheathe != forceSheathe) {
			forceNoSheathe = forceSheathe;
			if (underCombat == combatStance)
				player.getGlobalPlayerUpdater().generateAppearenceData();
		}
		if (underCombat != combatStance) {
			// wait until def emote performs, cuz render anims cant be delayed
			if (underCombat && player.getNextAnimation() == null)
				return;
			combatStance = underCombat;
			if (!combatStance) {
				if (isSheathe())
					player.setNextAnimationNoPriority(new Animation(18027));
				else
					player.setNextAnimationNoPriority(new Animation(player.getEquipment().getWeaponEndCombatEmote()));
			}
			player.getGlobalPlayerUpdater().generateAppearenceData();
		}
	}

	public boolean isSheathe() {
		return !player.isUnderCombat() && !forceNoSheathe && sheathe;
	}

	public void switchSheathe() {
		if (player.isLocked())
			return;
		if (player.isUnderCombat()) {
			player.sendMessage("You can't do that while in combat.");
			return;
		}
		sheathe = !sheathe;
		player.setNextAnimation(new Animation(sheathe ? 18027 : 18028));
		player.lock(1);
		player.getGlobalPlayerUpdater().generateAppearenceData();
		player.sendMessage("Sheathing is now turned: <shad=000000>"
				+ (isSheathe() ? Colors.green + "on" : Colors.red + "off") + "</col></shad>.");
		player.getPackets().sendIComponentText(751, 16,
				"<shad=000000>" + (isSheathe() ? Colors.green : Colors.red) + "Sheathe</col></shad>");
	}

	public long getMainHandDelay() {
		return mainHandDelay;
	}

	public void setMainHandDelay(long mainHandDelay) {
		this.mainHandDelay = mainHandDelay;
	}

	public long getOffHandDelay() {
		return offHandDelay;
	}

	public void setOffHandDelay(long offHandDelay) {
		this.offHandDelay = offHandDelay;
	}

	public static final int getMeleeBonusStyle(int weaponId, int attackStyle) {
		if (weaponId != -1) {
			if (weaponId == -2) {
				return CRUSH_ATTACK;
			}
			String weaponName = ItemDefinitions.getItemDefinitions(weaponId).getName().toLowerCase();
			if (weaponName.contains("whip"))
				return SLASH_ATTACK;
			if (weaponName.contains("staff of light")) {
				switch (attackStyle) {
				case 0:
					return STAB_ATTACK;
				case 1:
					return SLASH_ATTACK;
				default:
					return CRUSH_ATTACK;
				}
			}
			if (weaponName.contains("staff") || weaponName.contains("granite mace") || weaponName.contains("warhammer")
					|| weaponName.contains("tzhaar-ket-em") || weaponName.contains("tzhaar-ket-om")
					|| weaponName.contains("maul"))
				return CRUSH_ATTACK;
			if (weaponName.contains("godsword") || weaponName.contains("greataxe") || weaponName.contains("2h sword")
					|| weaponName.equals("saradomin sword")) {
				switch (attackStyle) {
				case 2:
					return CRUSH_ATTACK;
				default:
					return SLASH_ATTACK;
				}
			}
			if (weaponName.contains("scimitar") || weaponName.contains("hatchet") || weaponName.contains("claws")
					|| weaponName.contains(" sword") || weaponName.contains("longsword")
					|| weaponName.contains("scythe")) {
				switch (attackStyle) {
				case 2:
					return STAB_ATTACK;
				default:
					return SLASH_ATTACK;
				}
			}
			if (weaponName.contains("mace") || weaponName.contains("anchor")) {
				switch (attackStyle) {
				case 2:
					return STAB_ATTACK;
				default:
					return CRUSH_ATTACK;
				}
			}
			if (weaponName.contains("halberd")) {
				switch (attackStyle) {
				case 1:
					return SLASH_ATTACK;
				default:
					return STAB_ATTACK;
				}
			}
			if (weaponName.contains("spear")) {
				switch (attackStyle) {
				case 1:
					return SLASH_ATTACK;
				case 2:
					return CRUSH_ATTACK;
				default:
					return STAB_ATTACK;
				}
			}
			if (weaponName.contains("pickaxe")) {
				switch (attackStyle) {
				case 2:
					return CRUSH_ATTACK;
				default:
					return STAB_ATTACK;
				}
			}

			if (weaponName.contains("dagger") || weaponName.contains("rapier")) {
				switch (attackStyle) {
				case 2:
					return SLASH_ATTACK;
				default:
					return STAB_ATTACK;
				}
			}

		}
		switch (weaponId) {
		default:
			return CRUSH_ATTACK;
		}
	}

	public static final int getMeleeDefenceBonus(int bonusId) {
		if (bonusId == STAB_ATTACK)
			return STAB_DEF;
		if (bonusId == SLASH_DEF)
			return SLASH_DEF;
		return CRUSH_DEF;
	}

	public static final int getXpStyle(int weaponId, int attackStyle) {
		if (weaponId != -1 && weaponId != -2) {
			String weaponName = ItemDefinitions.getItemDefinitions(weaponId).getName().toLowerCase();
			if (weaponName.contains("whip")) {
				switch (attackStyle) {
				case 0:
					return Skills.ATTACK;
				case 1:
					return SHARED;
				case 2:
				default:
					return Skills.DEFENCE;
				}
			}
			if (weaponName.contains("halberd")) {
				switch (attackStyle) {
				case 0:
					return SHARED;
				case 1:
					return Skills.STRENGTH;
				case 2:
				default:
					return Skills.DEFENCE;
				}
			}
			if (weaponName.contains("longsword")) {
				switch (attackStyle) {
				case 0:
					return Skills.ATTACK;
				case 1:
					return Skills.STRENGTH;
				case 2:
					return SHARED;
				default:
					return Skills.DEFENCE;
				}
			}
			if (weaponName.contains("staff")) {
				switch (attackStyle) {
				case 0:
					return Skills.ATTACK;
				case 1:
					return Skills.STRENGTH;
				case 2:
				default:
					return Skills.DEFENCE;
				}
			}
			if (weaponName.contains("godsword") || weaponName.contains("sword") || weaponName.contains("2h")) {
				switch (attackStyle) {
				case 0:
					return Skills.ATTACK;
				case 1:
					return Skills.STRENGTH;
				case 2:
					return Skills.STRENGTH;
				case 3:
				default:
					return Skills.DEFENCE;
				}
			}
		}
		switch (weaponId) {
		case -1:
		case -2:
			switch (attackStyle) {
			case 0:
				return Skills.ATTACK;
			case 1:
				return Skills.STRENGTH;
			case 2:
			default:
				return Skills.DEFENCE;
			}
		default:
			switch (attackStyle) {
			case 0:
				return Skills.ATTACK;
			case 1:
				return Skills.STRENGTH;
			case 2:
				return SHARED;
			case 3:
			default:
				return Skills.DEFENCE;
			}
		}
	}

	public void desecreaseSpecialAttack(int ammount) {
		usingSpecialAttack = false;
		refreshUsingSpecialAttack();
		if (ammount > 0) {
			specialAttackPercentage -= ammount;
			refreshSpecialAttackPercentage();
		}
	}

	public int getType(int slot) {
		Item item = player.getEquipment().getItem(slot);
		if (item != null) {
			ItemDefinitions defs = item.getDefinitions();
			if (defs.isMeleeTypeWeapon() || defs.isMeleeTypeGear())
				return MELEE_TYPE;
			if (defs.isRangeTypeWeapon() || defs.isRangeTypeGear())
				return RANGE_TYPE;
			if (defs.isMagicTypeWeapon() || defs.isMagicTypeGear())
				return MAGIC_TYPE;
		}
		return ALL_TYPE;
	}
}