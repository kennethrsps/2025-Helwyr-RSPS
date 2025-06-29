package com.rs.game.player.dialogue.impl;

import com.rs.game.Graphics;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.utils.Colors;

public final class LevelUp extends Dialogue {

	/**
	 * Skill level-up Music effect ID's.
	 */
	public static final int[] SKILL_LEVEL_UP_MUSIC_EFFECTS = { 30, 38, 66, 48, 58, 56, 52, 34, 70, 44, 42, 40, 36, 64,
			54, 46, 28, 68, 61, 10, 60, 50, 32, 301, 417, 42 // TODO find real
																// divination
																// level up
																// music effect
	};
	private int skill;
	private static final int[] ICONS = { 1, 0, 2, 6, 3, 7, 4, 16, 18, 19, 15, 17, 11, 14, 13, 9, 8, 10, 20, 21, 12, 23,
			22, 24, 25 };

	public static int getIconValue(int skill) {
		if (skill < ICONS.length)
			return ICONS[skill];
		return 26;
	}

	public static void sendNews(Player player, int skill, int level) {
		boolean reachedAll = true;
		for (int i = 0; i < Skills.SKILL_NAME.length; i++) {
			if (player.getSkills().getLevelForXp(i) < 99) {
				reachedAll = false;
				break;
			}
		}
		if (!reachedAll) {
			World.sendWorldMessage(
					"<img=6>" + Colors.orange + "<shad=000000>News: " + player.getDisplayName() + " has achieved "
							+ level + " " + Skills.SKILL_NAME[skill] + " on " + player.getXPMode() + " mode!",
					false);
			sendSpins(player, skill, 1);
			return;
		}
		if (player.getSkills().getLevelForXp(Skills.DUNGEONEERING) == 120) {
			World.sendWorldMessage("<img=6>" + Colors.red + "<shad=000000>News: " + player.getDisplayName()
					+ " has just achieved all stats 99 and 120 dungeoneering on " + player.getXPMode() + " mode!",
					false);
			sendSpins(player, skill, 1);
			return;
		}
		World.sendWorldMessage("<img=6>" + Colors.red + "<shad=000000>News: " + player.getDisplayName()
				+ " has just achieved all stats 99 on " + player.getXPMode() + " mode!", false);
		
	}

	public static void switchFlash(Player player, int skill, boolean on) {
		int id = 0;
		if (skill == Skills.ATTACK)
			id = 3267;
		else if (skill == Skills.STRENGTH)
			id = 3268;
		else if (skill == Skills.DEFENCE)
			id = 3269;
		else if (skill == Skills.RANGE)
			id = 3270;
		else if (skill == Skills.PRAYER)
			id = 3271;
		else if (skill == Skills.MAGIC)
			id = 3272;
		else if (skill == Skills.HITPOINTS)
			id = 3273;
		else if (skill == Skills.AGILITY)
			id = 3274;
		else if (skill == Skills.HERBLORE)
			id = 3275;
		else if (skill == Skills.THIEVING)
			id = 3276;
		else if (skill == Skills.CRAFTING)
			id = 3277;
		else if (skill == Skills.FLETCHING)
			id = 3278;
		else if (skill == Skills.MINING)
			id = 3279;
		else if (skill == Skills.SMITHING)
			id = 3280;
		else if (skill == Skills.FISHING)
			id = 3281;
		else if (skill == Skills.COOKING)
			id = 3282;
		else if (skill == Skills.FIREMAKING)
			id = 3283;
		else if (skill == Skills.WOODCUTTING)
			id = 3284;
		else if (skill == Skills.RUNECRAFTING)
			id = 3285;
		else if (skill == Skills.SLAYER)
			id = 3286;
		else if (skill == Skills.FARMING)
			id = 3287;
		else if (skill == Skills.CONSTRUCTION)
			id = 3288;
		else if (skill == Skills.HUNTER)
			id = 3289;
		else if (skill == Skills.SUMMONING)
			id = 3290;
		else if (skill == Skills.DUNGEONEERING)
			id = 3291;
		player.getVarBitManager().sendVarBit(id, on ? 1 : 0);
	}

	/**
	 * Sends the Mastery cape World announcement.
	 *
	 * @param player
	 *            The player.
	 * @param skill
	 *            The skill ID.
	 */
	public static void send104m(Player player, int skill) {
		sendSpins(player, skill, 1);
		player.getPackets().sendMusicEffect(321);
		player.setNextGraphics(new Graphics(1765));
		World.sendWorldMessage("<img=6>" + Colors.red + "<shad=000000>News: " + player.getDisplayName()
				+ " has achieved 104,273,167 experience in the " + Skills.SKILL_NAME[skill] + " skill on "
				+ player.getXPMode() + " mode and can now wear the Mastery Cape!", false);
		player.sendMessage(
				"<col=825200><shad=000000>Well done! You've achieved 104,273,167 XP in this skill! You can now purchase a Mastery Cape from the Wise Old Man.");

		return;
	}

	/**
	 * Sends the 250m EXP milestone World announcement.
	 *
	 * @param player
	 *            The player.
	 * @param skill
	 *            The skill ID.
	 */
	public static void send250m(Player player, int skill) {
		sendSpins(player, skill, 1);
		player.getPackets().sendMusicEffect(320);
		player.setNextGraphics(new Graphics(1765));
		World.sendWorldMessage("<img=6>" + Colors.red + "<shad=000000>News: " + player.getDisplayName()
				+ " has achieved 250,000,000 experience in the " + Skills.SKILL_NAME[skill] + " skill on "
				+ player.getXPMode() + " mode!", false);

		return;
	}

	/**
	 * Sends the 500m EXP milestone World announcement.
	 *
	 * @param player
	 *            The player.
	 * @param skill
	 *            The skill ID.
	 */
	public static void send500m(Player player, int skill) {
		sendSpins(player, skill, 1);
		player.getPackets().sendMusicEffect(320);
		player.setNextGraphics(new Graphics(1765));
		World.sendWorldMessage("<img=6>" + Colors.red + "<shad=000000>News: " + player.getDisplayName()
				+ " has achieved 500,000,000 experience in the " + Skills.SKILL_NAME[skill] + " skill on "
				+ player.getXPMode() + " mode!", false);

		return;
	}

	/**
	 * Sends the 1000m EXP milestone World announcement.
	 *
	 * @param player
	 *            The player.
	 * @param skill
	 *            The skill ID.
	 */
	public static void send1000m(Player player, int skill) {
		sendSpins(player, skill, 1);
		player.getPackets().sendMusicEffect(320);
		player.setNextGraphics(new Graphics(1765));
		World.sendWorldMessage("<img=6>" + Colors.red + "<shad=000000>News: " + player.getDisplayName()
				+ " has achieved 1'000,000,000 experience in the " + Skills.SKILL_NAME[skill] + " skill on "
				+ player.getXPMode() + " mode!", false);
		return;
	}

	public static void send2000m(Player player, int skill) {
		sendSpins(player, skill, 1);
		player.getPackets().sendMusicEffect(320);
		player.setNextGraphics(new Graphics(1765));
		World.sendWorldMessage("<img=6>" + Colors.red + "<shad=000000>News: " + player.getDisplayName()
				+ " has achieved 2'000,000,000 experience in the " + Skills.SKILL_NAME[skill] + " skill on "
				+ player.getXPMode() + " mode!", false);
		return;
	}

	public static void sendSpins(Player player, int skill, int multiplier) {
		int spins = 0;
		if (player.isEasy()) {
			spins = 1 * multiplier;
			player.getSquealOfFortune().giveEarnedSpins(spins);
		} else if (player.isIntermediate()) {
			spins = 2 * multiplier;
			player.getSquealOfFortune().giveEarnedSpins(spins);
		} else if (player.isVeteran()) {
			spins = 4 * multiplier;
			player.getSquealOfFortune().giveEarnedSpins(spins);
		} else if (player.isExpert()) {
			spins = 8 * multiplier;
			player.getSquealOfFortune().giveEarnedSpins(spins);
		}
		player.sendMessage(Colors.green + "You have been awarded " + spins + " spins! Congratulations!");
	}

	@Override
	public void start() {
		skill = (Integer) parameters[0];
		int level = player.getSkills().getLevelForXp(skill);
		player.getTemporaryAttributtes().put("leveledUp", skill);
		player.getTemporaryAttributtes().put("leveledUp[" + skill + "]", Boolean.TRUE);
		player.setNextGraphics(new Graphics(199));
		if (level == 99 || level == 120)
			player.setNextGraphics(new Graphics(1765));
		String name = Skills.SKILL_NAME[skill];
		if (level >= 80 && skill != Skills.DIVINATION) {
			player.getInterfaceManager().sendChatBoxInterface(740);
			player.getPackets().sendIComponentText(740, 0, "Congratulations, you have just advanced a"
					+ (name.startsWith("A") ? "n" : "") + " " + name + " level!");
			player.getPackets().sendIComponentText(740, 1, "You have now reached level " + level + ".");
		}
		player.sendMessage("You've just advanced a" + (name.startsWith("A") ? "n" : "") + " " + name + " level! "
				+ "You have reached level " + level + ".");
		if (skill != Skills.DIVINATION) {
			player.getPackets().sendConfigByFile(4757, getIconValue(skill));
			player.getInterfaceManager().sendFadingInterface(1216);
			player.getPackets().sendGlobalConfig(1756, getIconValue(skill));
			switchFlash(player, skill, true);
		}
		int musicEffect = SKILL_LEVEL_UP_MUSIC_EFFECTS[skill];
		if (musicEffect != -1)
			player.getPackets().sendMusicEffect(musicEffect);
		if (level == 99)
			sendNews(player, skill, level);
		player.getInterfaceManager().sendXPPopup();
	}

	@Override
	public void run(int interfaceId, int componentId) {
		end();
	}

	@Override
	public void finish() {
	}
}