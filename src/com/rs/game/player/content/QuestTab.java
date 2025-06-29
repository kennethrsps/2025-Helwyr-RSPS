package com.rs.game.player.content;

import com.rs.Settings;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.dialogue.impl.CasinoEntranceD;
import com.rs.utils.Colors;
import com.rs.utils.Utils;
import com.rs.game.player.dialogue.Dialogue;

/**
 * Handles the custom Quest Tab and its button interactions.
 * 
 * This system provides: - Banking functionality with proper validation -
 * Teleportation systems (Home, Training, Boss, Minigame, PvP, Skilling) -
 * Prayer book switching - Account management interfaces - External links to
 * community resources - Prifddinas multi-stage teleport system
 * 
 * @author Zeus
 * @refactored for performance and maintainability
 */
public class QuestTab {

	// Constants for component IDs
	private static final class ComponentIds {
		static final int BANK_BUTTON = 1;
		static final int HOME_TELEPORT = 2;
		static final int PRAYER_TOGGLE = 3;
		static final int MAGIC_BUTTON = 4;
		static final int TRAINING_TELEPORTS = 5;
		static final int BOSS_TELEPORTS = 6;
		static final int MINIGAME_TELEPORTS = 7;
		static final int PVP_TELEPORTS = 8;
		static final int SKILLING_TELEPORTS = 9;
		static final int DONATOR_ZONE = 10;
		static final int EXTERNAL_LINKS = 11;
		static final int ACCOUNT_MANAGER = 12;
		static final int PRIFDDINAS_TELEPORT = 22;
	}

	// Constants for validation
	private static final int COMBAT_DELAY_MS = 10000;
	private static final int MIN_PRAYER_LEVEL_CURSES = 50;
	private static final int HAPPY_FACE_EMOTE = 9827;

	// Interface selection constants
	private static final class InterfaceSelection {
		static final int BOSS_TELEPORTS = 1;
		static final int MINIGAME_TELEPORTS = 2;
		static final int TRAINING_TELEPORTS = 3;
		static final int PVP_TELEPORTS = 4;
	}

	// Prifddinas teleport locations
	private static final class PrifddinasLocations {
		static final WorldTile MAIN = new WorldTile(2213, 3361, 1);
		static final WorldTile HEFIN = new WorldTile(2186, 3409, 1);
		static final WorldTile AMLODD = new WorldTile(2155, 3381, 1);
		static final WorldTile ITHELL = new WorldTile(2153, 3339, 1);
		static final WorldTile IORWERTH = new WorldTile(2185, 3312, 1);
		static final WorldTile TRAHAEARN = new WorldTile(2232, 3311, 1);
		static final WorldTile CADARN = new WorldTile(2260, 3338, 1);
		static final WorldTile CRWYS = new WorldTile(2260, 3383, 1);
		static final WorldTile MEILYR = new WorldTile(2246, 3351, 1);
	}

	/**
	 * Sends the quest tab to the player.
	 * 
	 * @param player The player to send the quest tab to.
	 */
	public static void sendTab(Player player) {
		// Implementation to send the quest tab UI can be added here.
		// This method is placeholder for future UI implementation
	}

	/**
	 * Handles button clicks on the Quest Tab interface.
	 * 
	 * @param player      The player who clicked a button.
	 * @param componentId The component/button ID clicked.
	 */
	public static void handleTab(Player player, int componentId) {
		if (player == null) {
			return;
		}

		switch (componentId) {
		case ComponentIds.BANK_BUTTON:
			handleBankButton(player);
			break;
		case ComponentIds.HOME_TELEPORT:
			handleHomeTeleport(player);
			break;
		case ComponentIds.PRAYER_TOGGLE:
			handlePrayerToggle(player);
			break;
		case ComponentIds.MAGIC_BUTTON:
			handleMagicButton(player);
			break;
		case ComponentIds.TRAINING_TELEPORTS:
			openTrainingTeleports(player);
			break;
		case ComponentIds.BOSS_TELEPORTS:
			openBossTeleports(player);
			break;
		case ComponentIds.MINIGAME_TELEPORTS:
			openMinigameTeleports(player);
			break;
		case ComponentIds.PVP_TELEPORTS:
			openPvPTeleports(player);
			break;
		case ComponentIds.SKILLING_TELEPORTS:
			openSkillingDialogue(player);
			break;
		case ComponentIds.DONATOR_ZONE:
			openDonatorZoneDialogue(player);
			break;
		case ComponentIds.EXTERNAL_LINKS:
			openExternalLinksDialogue(player);
			break;
		case ComponentIds.ACCOUNT_MANAGER:
			openAccountManager(player);
			break;
		case ComponentIds.PRIFDDINAS_TELEPORT:
			openPrifddinasDialogue(player);
			break;
		default:
			player.sendMessage("Feature not implemented yet.");
			break;
		}
	}

	/**
	 * Handles the magic button interaction
	 */
	private static void handleMagicButton(Player player) {
		player.closeInterfaces();
		player.getDialogueManager().startDialogue("MagicButton");
	}

	/**
	 * Handles opening the player's bank via the Bank Button.
	 */
	private static void handleBankButton(Player player) {
		player.closeInterfaces();

		// Validate bank access permissions and conditions
		if (!validateBankAccess(player)) {
			return;
		}

		// Open bank interface
		player.getInterfaceManager().closeOverlay(!player.getInterfaceManager().isResizableScreen());
		player.closeInterfaces();
		player.stopAll();
		player.getBank().openPlayerBank(player);
	}

	/**
	 * Validates if the player can access banking functionality
	 */
	private static boolean validateBankAccess(Player player) {
		if (!player.getPerkManager().bankCommand) {
			player.sendMessage("You have to purchase the Bank Command perk in order to do this.");
			return false;
		}

		if (!player.canSpawn()) {
			player.sendMessage("You cannot open your bank account at the moment.");
			return false;
		}

		if (CasinoEntranceD.CasinoArea(player, player)) {
			player.getPackets().sendGameMessage(Colors.red + "You cannot use bank command in the casino");
			return false;
		}

		if (player.isLocked()) {
			player.sendMessage("You can't bank at the moment, please wait.");
			return false;
		}

		if (player.getControlerManager().getControler() != null) {
			player.sendMessage("You can't bank while you're in this area.");
			return false;
		}

		if (player.getAttackedByDelay() + COMBAT_DELAY_MS > Utils.currentTimeMillis()) {
			player.sendMessage("You can't bank 10 seconds after combat, please wait.");
			return false;
		}

		if (player.isUnderCombat()) {
			player.sendMessage("It is not possible to engage in banking activities during combat.");
			return false;
		}

		return true;
	}

	/**
	 * Handles the Home Teleport action.
	 */
	private static void handleHomeTeleport(Player player) {
		player.closeInterfaces();

		// Remove player from current instance if present
		if (player.getCurrentInstance() != null) {
			player.getCurrentInstance().removePlayer(player);
		}

		Magic.sendNormalTeleportSpell(player, 0, 0, player.getHomeTile());
	}

	/**
	 * Toggles between normal prayers and ancient curses.
	 */
	private static void handlePrayerToggle(Player player) {
		player.closeInterfaces();

		if (!player.getPrayer().isAncientCurses()) {
			if (player.getSkills().getLevel(Skills.PRAYER) < MIN_PRAYER_LEVEL_CURSES) {
				player.sendMessage("Your Prayer level is not high enough to do this.");
				return;
			}
			player.sendMessage(
					"The altar fills your head with dark thoughts, purging the prayers from your memory and leaving only Curses in their place.");
			player.getPrayer().setPrayerBook(true);
		} else {
			player.sendMessage(
					"The altar eases its grip on your mind. The Curses slip from your memory and you recall the prayers you used to know.");
			player.getPrayer().setPrayerBook(false);
		}
	}

	/**
	 * Opens the Training Teleports interface.
	 */
	private static void openTrainingTeleports(Player player) {
		prepareInterfaceOpen(player);
		InterfaceManager.setPlayerInterfaceSelected(InterfaceSelection.TRAINING_TELEPORTS);
		TrainingTeleports.sendInterface(player);
	}

	/**
	 * Opens the Boss Teleports interface.
	 */
	private static void openBossTeleports(Player player) {
		prepareInterfaceOpen(player);
		InterfaceManager.setPlayerInterfaceSelected(InterfaceSelection.BOSS_TELEPORTS);
		BossTeleports.sendInterface(player);
	}

	/**
	 * Opens the Minigame Teleports interface.
	 */
	private static void openMinigameTeleports(Player player) {
		prepareInterfaceOpen(player);
		InterfaceManager.setPlayerInterfaceSelected(InterfaceSelection.MINIGAME_TELEPORTS);
		MinigameTeleports.sendInterface(player);
	}

	/**
	 * Opens the PvP Teleports interface.
	 */
	private static void openPvPTeleports(Player player) {
		prepareInterfaceOpen(player);
		InterfaceManager.setPlayerInterfaceSelected(InterfaceSelection.PVP_TELEPORTS);
		PvPTeleports.sendInterface(player);
	}

	/**
	 * Opens the Skilling Teleports dialogue.
	 */
	private static void openSkillingDialogue(Player player) {
		prepareInterfaceOpen(player);
		player.getDialogueManager().startDialogue("SkillTeleport");
	}

	/**
	 * Opens the Donator Zone dialogue.
	 */
	private static void openDonatorZoneDialogue(Player player) {
		prepareInterfaceOpen(player);
		player.getDialogueManager().startDialogue("MembersTeleport");
	}

	/**
	 * Opens the Account Manager interface.
	 */
	private static void openAccountManager(Player player) {
		prepareInterfaceOpen(player);
		AccountInterfaceManager.sendInterface(player);
	}

	/**
	 * Prepares the interface for opening by closing existing interfaces
	 */
	private static void prepareInterfaceOpen(Player player) {
		player.closeInterfaces();
		player.getInterfaceManager().closeOverlay(!player.getInterfaceManager().isResizableScreen());
	}

	/**
	 * Opens the External Links dialogue with community resources.
	 */
	private static void openExternalLinksDialogue(Player player) {
		prepareInterfaceOpen(player);
		player.getDialogueManager().startDialogue(new ExternalLinksDialogue());
	}

	/**
	 * Opens the Prifddinas teleport dialogue system.
	 */
	private static void openPrifddinasDialogue(Player player) {
		prepareInterfaceOpen(player);
		player.getDialogueManager().startDialogue(new PrifddinasDialogue());
	}

	/**
	 * Dialogue for external links (forums, voting, donation, etc.)
	 */
	private static class ExternalLinksDialogue extends Dialogue {
		@Override
		public void start() {
			sendOptionsDialogue("Helwyr Links", "Forums", "Vote", "Donate", "Discord", "Highscores");
		}

		@Override
		public void run(int interfaceId, int componentId) {
			if (stage == -1) {
				handleLinkSelection(componentId);
				stage = 99;
			} else {
				finish();
			}
		}

		private void handleLinkSelection(int componentId) {
			switch (componentId) {
			case OPTION_1:
				openURLAndNotify(Settings.FORUM, "Opening forums on your default browser");
				break;
			case OPTION_2:
				openURLAndNotify(Settings.VOTE, "Opening Voting page on your default browser");
				break;
			case OPTION_3:
				openURLAndNotify(Settings.DONATE, "Opening Donation page on your default browser");
				break;
			case OPTION_4:
				openURLAndNotify(Settings.DISCORD, "Opening Discord on your default browser");
				break;
			case OPTION_5:
				openURLAndNotify(Settings.HISCORES, "Opening highscores on your default browser");
				break;
			}
		}

		private void openURLAndNotify(String url, String message) {
			player.getPackets().sendOpenURL(url);
			sendNPCDialogue(18808, HAPPY_FACE_EMOTE, message);
		}

		@Override
		public void finish() {
			player.getInterfaceManager().closeChatBoxInterface();
		}
	}

	/**
	 * Multi-stage dialogue for Prifddinas teleport system
	 */
	private static class PrifddinasDialogue extends Dialogue {
		private static final int STAGE_MAIN = -1;
		private static final int STAGE_MORE_OPTIONS = 98;
		private static final int STAGE_BACK_OPTIONS = 97;
		private static final int STAGE_FINISH = 99;

		@Override
		public void start() {
			sendOptionsDialogue("Prifddinas", "Prifddinas Main", "Hefin Herald", "Amlodd Herald", "Ithell Herald",
					Colors.red + "More");
		}

		@Override
		public void run(int interfaceId, int componentId) {
			switch (stage) {
			case STAGE_MAIN:
				handleMainOptions(componentId);
				break;
			case STAGE_MORE_OPTIONS:
				handleMoreOptions(componentId);
				break;
			case STAGE_BACK_OPTIONS:
				handleBackOptions(componentId);
				break;
			case STAGE_FINISH:
			default:
				finish();
				break;
			}
		}

		private void handleMainOptions(int componentId) {
			switch (componentId) {
			case OPTION_1:
				teleportTo(PrifddinasLocations.MAIN); // Free teleport
				break;
			case OPTION_2:
			case OPTION_3:
			case OPTION_4:
				if (!validateDonatorAccess())
					return;

				WorldTile destination = getDonatorTeleportDestination(componentId);
				if (destination != null) {
					teleportTo(destination);
				}
				break;
			case OPTION_5:
				if (!validateDonatorAccess())
					return;

				sendOptionsDialogue("Prifddinas", "Iorwerth Herald", "Trahaearn Herald", "Cadarn Herald",
						"Crwys Herald", Colors.red + "More");
				stage = STAGE_MORE_OPTIONS;
				break;
			}
		}

		private void handleMoreOptions(int componentId) {
			if (!validateDonatorAccess())
				return;

			switch (componentId) {
			case OPTION_1:
				teleportTo(PrifddinasLocations.IORWERTH);
				break;
			case OPTION_2:
				teleportTo(PrifddinasLocations.TRAHAEARN);
				break;
			case OPTION_3:
				teleportTo(PrifddinasLocations.CADARN);
				break;
			case OPTION_4:
				teleportTo(PrifddinasLocations.CRWYS);
				break;
			case OPTION_5:
				sendOptionsDialogue("Prifddinas", "Meilyr Herald", Colors.red + "Back");
				stage = STAGE_BACK_OPTIONS;
				break;
			}
		}

		private void handleBackOptions(int componentId) {
			if (!validateDonatorAccess())
				return;

			switch (componentId) {
			case OPTION_1:
				teleportTo(PrifddinasLocations.MEILYR);
				break;
			case OPTION_2:
				// Go back to main menu
				sendOptionsDialogue("Prifddinas", "Prifddinas Main", "Hefin Herald", "Amlodd Herald", "Ithell Herald",
						Colors.red + "More");
				stage = STAGE_MAIN;
				break;
			}
		}

		private WorldTile getDonatorTeleportDestination(int componentId) {
			switch (componentId) {
			case OPTION_2:
				return PrifddinasLocations.HEFIN;
			case OPTION_3:
				return PrifddinasLocations.AMLODD;
			case OPTION_4:
				return PrifddinasLocations.ITHELL;
			default:
				return null;
			}
		}

		private boolean validateDonatorAccess() {
			if (!player.isDonator()) {
				player.closeInterfaces();
				player.sendMessage("This teleport requires donator status.");
				player.sendMessage("Visit our donation page to unlock premium teleports!");
				return false;
			}
			return true;
		}

		private void teleportTo(WorldTile tile) {
			player.closeInterfaces();
			Magic.sendNormalTeleportSpell(player, 0, 0, tile);
			finish();
		}

		@Override
		public void finish() {
			player.getInterfaceManager().closeChatBoxInterface();
		}
	}
}