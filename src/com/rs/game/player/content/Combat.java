package com.rs.game.player.content;

import com.rs.game.Entity;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.player.Equipment;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

/**
 * Handles everything related to combat configurations.
 * @author Zeus
 */
public final class Combat {

	public static final int MAGIC_TYPE = 2;

	/**
	 * Checks if the target has anti-dragon protection.
	 * @param target The target.
	 * @return if has protection.
	 */
    public static boolean hasAntiDragProtection(Entity target) {
		if (target instanceof NPC)
		    return false;
		Player p2 = (Player) target;
		int shieldId = p2.getEquipment().getShieldId();
		return shieldId == 1540 || shieldId == 11283 || shieldId == 11284  || shieldId == 41450 || p2.getPerkManager().dragonTrainer;
    }
    
    
    /**
	 * Gets the dragonfire protect message.
	 * @param player The player.
	 * @return The message to send, or {@code null} if the player was unprotected.
	 */
	public static final String getProtectMessage(Player player) {
		boolean hasFireImmune = player.getFireImmune() > Utils.currentTimeMillis() && player.getFireImmune() != 0;
		boolean hasFirePrayerProtection = player.getPrayer().usingPrayer(0, 17) || player.getPrayer().usingPrayer(1, 17);
		if (player.getPerkManager().dragonTrainer)
			return "Your dragon trainer perk fully absorbs the heat of the dragon's breath!";
		if (player.getSuperAntiFire() > Utils.currentTimeMillis())
			return "Your potion fully absorbs the heat of the dragon's breath!";
		if (hasFireImmune && hasFirePrayerProtection)
			return "Your prayer and potion fully absorbs the heat of the dragon's breath!";
		if (hasDFS(player) && hasFirePrayerProtection)
			return "Your prayer and shield fully absorbs the heat of the dragon's breath!";
		if (hasFireImmune && hasDFS(player))
			return "Your potion and shield fully absorbs the heat of the dragon's breath!";
		if (hasDFS(player))
			return "Your shield absorbs some of the dragon's breath!";
		if (hasFireImmune)
			return "Your potion absorbs some of the dragon's breath!";
		if (hasFirePrayerProtection)
			return "Your prayer absorbs some of the dragon's breath!";
		return null;
	}

	/**
	 * Gets the Slayer level required to attack certain slayer NPC's.
	 * @param id The NPC Id.
	 * @return the slayer level required.
	 */
    public static int getSlayerLevelForNPC(int id) {
		switch (id) {
		case 14696:
		case 14698:
			return 95;
		case 9463:
			return 93;
		case 2783:
			return 90;
		case 14688:
			return 88;
		case 1615:
			return 85;
		case 14700:
			return 82;
		case 17144:
		case 17145:
		case 17146:
		case 17147:
		case 17148:
		case 17149:
		case 17150:
			return 81;
		case 1610:
			return 75;
		default:
			return 0;
		}
	}

    /**
     * Gets the defence combat animation to show.
     * @param target The target animating.
     * @return the animation id as Integer.
     */
    public static int getDefenceEmote(Entity target) {
    	if (target instanceof NPC) {
			NPC n = (NPC) target;
			return n.getCombatDefinitions().getDefenceEmote();
		} else { 
			Player p = (Player) target;
			if (p.getEquipment().getShieldId() != -1)
				return 18346;
			Item weapon = p.getEquipment().getItem(Equipment.SLOT_WEAPON);
			if (weapon == null)
				return 18346;
			int emote = weapon.getDefinitions().getCombatOpcode(2917);
			return emote == 0 ? 18346 : emote;
		}
    }

    /**
     * Checks if the Player has a dragonfire-shield.
     * @param player The player to check.
     * @return if has protection.
     */
    public static boolean hasDFS(Player player) {
		int shieldId = player.getEquipment().getShieldId();
		return shieldId == 1540 || shieldId == 11283 || shieldId == 11284 || shieldId == 25558
				 || shieldId == 25559 || shieldId == 25561 || shieldId == 25562 || shieldId == 16933 
				 || shieldId == 41450 || player.getPerkManager().dragonTrainer;
	}
    
    /**
     * Checks if the Player has an Avas device.
     * @param player The player to check.
     * @return if has Ava's device.
     */
	public static boolean hasAvas(Player player) {
		int capeId = player.getEquipment().getCapeId();
		return capeId == 10498 || capeId == 10499 || capeId == 20068 || 
				capeId == 20769 || capeId == 20771 || capeId == 31610 ||
				capeId == 32152 || capeId == 32153;
	}
}