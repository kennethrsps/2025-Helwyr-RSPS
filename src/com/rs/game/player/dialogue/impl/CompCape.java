package com.rs.game.player.dialogue.impl;

import com.rs.game.Animation;
import com.rs.game.ForceTalk;
import com.rs.game.Graphics;
import com.rs.game.item.Item;
import com.rs.game.npc.NPC;
import com.rs.game.World;
import com.rs.game.WorldTile;
import com.rs.game.player.Player;
import com.rs.game.player.Skills;
import com.rs.game.player.QuestManager.Quests;
import com.rs.game.player.dialogue.Dialogue;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasksManager;
import com.rs.utils.Colors;
import com.rs.utils.Utils;

/*import mysql.impl.NewsManager;*/

/**
 * Handles the Distinction Cape stand.
 * @author Zeus
 */
public class CompCape extends Dialogue {

	int maxCape = 20767;
	int maxHood = 20768;
	int maxCAmount = 2475000;
	
	int compCape = 20769;
	int compHood = 20770;
	int compCAmount = 5000000;
	
	int compCapeT = 20771;
	int compHoodT = 20772;
	int compTCAmount = 10000000;
	
	int coins = 995;

	/**
	 * This dialogue is sent upon the player clicking on object id: 2562.
	 */

	@Override
	public void start() {
		sendOptionsDialogue("Achievement cape stand", "List requirements",
				"Max cape", "Completionist cape", "Trimmed completionist");
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (stage == -1) {
			if (componentId == OPTION_1) {
				end();
				sendRequirementMessages(player);
			} else if (componentId == OPTION_2) {
				if (!isWorthyMaxCape(player)) {
					notWorthy(player);
					return;
				} else {
					sendOptionsDialogue("Are you sure you wish to buy it?",
							"Yes, I deserve it.", "No, not now.");
					stage = 2;
				}
			} else if (componentId == OPTION_3) {
				if (!isWorthyCompCape(player)) {
					notWorthy(player);
					return;
				} else {
					sendOptionsDialogue("Are you sure you wish to buy it?",
							"Yes, I deserve it.", "No, not now.");
					stage = 3;
				}
			} else if (componentId == OPTION_4) {
				if (!isWorthyCompCapeT(player)) {
					notWorthy(player);
					return;
				} else {
					sendOptionsDialogue("Are you sure you wish to buy it?",
							"Yes, I deserve it.", "No, not now.");
					stage = 4;
				}
			}
		} else if (stage == 2) {
			if (componentId == OPTION_1) {
				if (!player.hasMoney(maxCAmount)) {
					sendNPCDialogue(4405, SAD, "Seems that you don't have enough money, come back later when you do.");
					stage = 99;
					return;
				}
				if (!player.isMax()) {
					World.sendWorldMessage(Colors.red+
						"<img=5><shad=000000>News: "+player.getDisplayName()+" has been awarded the Max Cape on "
							+player.getXPMode()+" mode.", false);

//					new Thread(new NewsManager(player, "<b><img src=\"./bin/images/news/max.png\" "
//							+ "width=14> " + player.getDisplayName()+" has been awarded the Max Cape on "
//							+player.getXPMode()+" mode.")).start();
				}
				player.addItem(new Item(maxCape));
				player.addItem(new Item(maxHood));
				player.takeMoney(maxCAmount);
				player.setMax(true);
				stage = 99;
			} 
			else if (componentId == OPTION_2) {
				end();
			}
		} else if (stage == 3) {
			if (componentId == OPTION_1) {
				if (!player.hasMoney(compCAmount)) {
					sendNPCDialogue(4405, SAD, "Seems that you don't have enough money, come back later when you do.");
					stage = 99;
					return;
				}
				if (!player.isComp()) {
					World.sendWorldMessage(Colors.red+
						"<img=5><shad=000000>News: "+player.getDisplayName()+" has been awarded the Completionist Cape on "
							+player.getXPMode()+" mode.", false);
					
					/*new Thread(new NewsManager(player, "<b><img src=\"./bin/images/news/comp.png\" "
							+ "width=14> " + player.getDisplayName()+" has been awarded the Completionist Cape on "
							+player.getXPMode()+" mode.")).start();*/
				}
				player.addItem(new Item(compCape));
				player.addItem(new Item(compHood));
				player.takeMoney(compCAmount);
				player.setComp(true);
				stage = 99;
			} 
			else if (componentId == OPTION_2) {
				end();
			}
		} else if (stage == 4) {
			if (componentId == OPTION_1) {
				if (!player.hasMoney(compTCAmount)) {
					sendNPCDialogue(4405, SAD, "Seems that you don't have enough money, come back later when you do.");
					stage = 99;
					return;
				}
				if (!player.isCompT()) {
					World.sendWorldMessage(Colors.red+
						"<img=5><shad=000000>News: "+player.getDisplayName()+" has been awarded the Trimmed Completionist Cape on "
							+player.getXPMode()+" mode.", false);

					/*new Thread(new NewsManager(player, "<b><img src=\"./bin/images/news/comp.png\" "
							+ "width=14> " + player.getDisplayName()+" has been awarded the Trimmed Completionist Cape on "
							+player.getXPMode()+" mode.")).start();*/
				}
				player.addItem(new Item(compCapeT));
				player.addItem(new Item(compHoodT));
				player.takeMoney(compTCAmount);
				player.setCompT(true);
				stage = 99;
			} 
			else if (componentId == OPTION_2) {
				end();
			}
		} else if (stage == 99) {
			end();
		}
	}

	@Override
	public void finish() { }

	/**
	 * Checks if the player is worthy to claim the Completionist cape.
	 * @param player The player to check.
	 * @return if is worthy enough.
	 */
	public static boolean isWorthyCompCape(Player player) {
		boolean worthy;
		if (isMaxed(player)
				&& player.getSkills().getLevelForXp(Skills.DUNGEONEERING) == 120 
				&& player.isKilledQueenBlackDragon()
				&& player.isCompletedFightCaves()
				&& player.isKilledCulinaromancer()
				&& player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM)
				&& player.isCompletedFightKiln())
			worthy = true;
		else
			worthy = false;
		return worthy;
	}

	/**
	 * Checks if the player is worthy enough to claim the Trimmed Completionist Cape.
	 * @param player the Player to check.
	 * @return if is worthy enough.
	 */
	public static boolean isWorthyCompCapeT(Player player) {
		boolean worthy;
		if (isWorthyCompCape(player)
				&& player.getOresMined() >= 5000
				&& player.getBarsSmelt() >= 5000
				&& player.getLogsChopped() >= 5000
				&& player.getLogsBurned() >= 5000
				&& player.getBonesOffered() >= 5000
				&& player.getPotionsMade() >= 5000
				&& player.getTimesStolen() >= 5000
				&& player.getItemsMade() >= 5000 
				&& player.getItemsFletched() >= 5000
				&& player.getCreaturesCaught() >= 3000
				&& player.getFishCaught() >= 5000
				&& player.getFoodCooked() >= 5000
				&& player.getProduceGathered() >= 3000
				&& player.getPouchesMade() >= 2500
				&& player.getLapsRan() >= 1000
				&& player.getMemoriesCollected() >= 5000
				&& player.getRunesMade() >= 5000)
			worthy = true;
		else
			worthy = false;
		return worthy;
	}

	/**
	 * Checks if the player is worthy enough to claim the Max Cape.
	 * @param player The player to check.
	 * @return if is worthy enough.
	 */
	public static boolean isWorthyMaxCape(Player player) {
		boolean worthy = false;
		if (isMaxed(player))
			worthy = true;
		return worthy;
	}

	/**
	 * Sends all the requirements for all of the distinction capes.
	 * @param player The player to check.
	 */
	public static void sendRequirementMessages(Player player) {
		for (int i = 1; i < 310; i++)
			inter(player, i, "");
		player.getInterfaceManager().sendInterface(275);
		inter(player, 1, "<col=FF0000><shad=000000>Distinction cape requirements</col>");

		inter(player, 10, "<col=046DB3><shad=000000><u>Max cape requirements</u></col>");
		inter(player, 11, (isWorthyMaxCape(player) ? "<str>" : "")+"Every stat level 99");
		inter(player, 12, (isWorthyMaxCape(player) ? "<col=1BD12A><shad=000000>You are worthy enough to claim this cape!</col>"
						: "<col=ff0000><shad=000000>You are not worthy enough to claim this cape!"));

		inter(player, 14, "<col=046DB3><shad=000000><u>Completionist cape requirements</u></col>");
		inter(player, 15, ((isMaxed(player) && player.getSkills().getLevelForXp(Skills.DUNGEONEERING) == 120) ? "<str>" : "")+"Every stat level 99 & 120 Dungeoneering");
		inter(player, 16, (player.isKilledQueenBlackDragon() ? "<str>" : "")+"Killed the Queen Black Dragon");
		inter(player, 17, (player.isCompletedFightCaves() ? "<str>" : "")+"Completed the Fight Caves minigame");
		inter(player, 18, (player.isCompletedFightKiln() ? "<str>" : "")+"Completed the Fight Kiln minigame");
		inter(player, 19, (player.isKilledCulinaromancer() ? "<str>" : "")+"Completed the Recipe for Disaster minigame");
		inter(player, 20, (player.getQuestManager().completedQuest(Quests.NOMADS_REQUIEM) ? "<str>" : "")+"Completed the Nomad's mini-quest");
		inter(player, 21, (isWorthyCompCape(player) ? "<col=1BD12A><shad=000000>You are worthy enough to claim this cape!</col>"
						: "<col=ff0000><shad=000000>You are not worthy enough to claim this cape!"));

		inter(player, 23, "<col=046DB3><shad=000000><u>Completionist cape (t) requirements</u></col>");
		inter(player, 24, ((isWorthyCompCape(player)) ? "<str>" : "")+"Have completed all of the above");
		inter(player, 25, ((player.getOresMined() >= 5000) ? "<str>" : "")+"Mine a total of 5'000 ores ("+Utils.getFormattedNumber(player.getOresMined())+")");
		inter(player, 26, ((player.getBarsSmelt() >= 5000) ? "<str>" : "")+"Smith a total of 5'000 bars ("+Utils.getFormattedNumber(player.getBarsSmelt())+")");
		inter(player, 27, ((player.getLogsChopped() >= 5000) ? "<str>" : "")+"Chop a total of 5'000 logs ("+Utils.getFormattedNumber(player.getLogsChopped())+")");
		inter(player, 28, ((player.getLogsBurned() >= 5000) ? "<str>" : "")+"Burn a total of 5'000 logs ("+Utils.getFormattedNumber(player.getLogsBurned())+")");
		inter(player, 29, ((player.getLapsRan() >= 1000) ? "<str>" : "")+"Complete a total of 1'000 agility laps ("+Utils.getFormattedNumber(player.getLapsRan())+")");
		inter(player, 30, ((player.getBonesOffered() >= 5000) ? "<str>" : "")+"Offer a total of 5'000 bones ("+Utils.getFormattedNumber(player.getBonesOffered())+")");
		inter(player, 31, ((player.getPotionsMade() >= 5000) ? "<str>" : "")+"Make a total of 5'000 potions ("+Utils.getFormattedNumber(player.getPotionsMade())+")");
		inter(player, 32, ((player.getTimesStolen() >= 5000) ? "<str>" : "")+"Steal a total of 5'000 times ("+Utils.getFormattedNumber(player.getTimesStolen())+")");
		inter(player, 33, ((player.getItemsMade() >= 5000) ? "<str>" : "")+"Craft a total of 5'000 items ("+Utils.getFormattedNumber(player.getItemsMade())+")");
		inter(player, 34, ((player.getItemsFletched() >= 5000) ? "<str>" : "")+"Fletch a total of 5'000 items ("+Utils.getFormattedNumber(player.getItemsFletched())+")");
		inter(player, 35, ((player.getCreaturesCaught() >= 3000) ? "<str>" : "")+"Catch a total of 3'000 creatures ("+Utils.getFormattedNumber(player.getCreaturesCaught())+")");
		inter(player, 36, ((player.getFishCaught() >= 5000) ? "<str>" : "")+"Catch a total of 5'000 fish ("+Utils.getFormattedNumber(player.getFishCaught())+")");
		inter(player, 37, ((player.getFoodCooked() >= 5000) ? "<str>" : "")+"Cook a total of 5'000 food ("+Utils.getFormattedNumber(player.getFoodCooked())+")");
		inter(player, 38, ((player.getProduceGathered() >= 3000) ? "<str>" : "")+"Farm a total of 3'000 produce ("+Utils.getFormattedNumber(player.getProduceGathered())+")");
		inter(player, 39, ((player.getPouchesMade() >= 2500) ? "<str>" : "")+"Infuse a total of 2'500 pouches ("+Utils.getFormattedNumber(player.getPouchesMade())+")");
		inter(player, 40, ((player.getMemoriesCollected() >= 5000) ? "<str>" : "")+"Harvest a total of 5'000 memories ("+Utils.getFormattedNumber(player.getMemoriesCollected())+")");
		inter(player, 41, ((player.getRunesMade() >= 5000) ? "<str>" : "")+"Runecraft a total of 5'000 runes ("+Utils.getFormattedNumber(player.getRunesMade())+")");
		inter(player, 42, ((isWorthyCompCapeT(player)) ? "<col=1BD12A><shad=000000>You are worthy enough to claim this cape!</col>"
						: "<col=ff0000><shad=000000>You are not worthy enough to claim this cape!"));
	}

	/**
	 * Sends the interface ID 275.
	 * @param player The player to send to.
	 * @param number The componentId.
	 * @param message The message to send.
	 */
	public static void inter(Player player, int number, String message) {
		player.getPackets().sendIComponentText(275, number, message);
	}
	
	/**
	 * Checs if the player has all trainable's 99.
	 * @param player The player to check.
	 * @return if has all 99's.
	 */
	public static boolean isMaxed(Player player) {
		if (player.getSkills().getLevelForXp(Skills.ATTACK) >= 99
				&& player.getSkills().getLevelForXp(Skills.STRENGTH) >= 99
				&& player.getSkills().getLevelForXp(Skills.DEFENCE) >= 99
				&& player.getSkills().getLevelForXp(Skills.RANGE) >= 99
				&& player.getSkills().getLevelForXp(Skills.PRAYER) >= 99
				&& player.getSkills().getLevelForXp(Skills.MAGIC) >= 99
				&& player.getSkills().getLevelForXp(Skills.RUNECRAFTING) >= 99
				&& player.getSkills().getLevelForXp(Skills.HITPOINTS) >= 99
				&& player.getSkills().getLevelForXp(Skills.AGILITY) >= 99
				&& player.getSkills().getLevelForXp(Skills.HERBLORE) >= 99
				&& player.getSkills().getLevelForXp(Skills.THIEVING) >= 99
				&& player.getSkills().getLevelForXp(Skills.CRAFTING) >= 99
				&& player.getSkills().getLevelForXp(Skills.FLETCHING) >= 99
				&& player.getSkills().getLevelForXp(Skills.SLAYER) >= 99
				&& player.getSkills().getLevelForXp(Skills.HUNTER) >= 99
				&& player.getSkills().getLevelForXp(Skills.MINING) >= 99
				&& player.getSkills().getLevelForXp(Skills.SMITHING) >= 99
				&& player.getSkills().getLevelForXp(Skills.FISHING) >= 99
				&& player.getSkills().getLevelForXp(Skills.COOKING) >= 99
				&& player.getSkills().getLevelForXp(Skills.FIREMAKING) >= 99
				&& player.getSkills().getLevelForXp(Skills.WOODCUTTING) >= 99
				&& player.getSkills().getLevelForXp(Skills.FARMING) >= 99
				&& player.getSkills().getLevelForXp(Skills.CONSTRUCTION) >= 99
				&& player.getSkills().getLevelForXp(Skills.SUMMONING) >= 99
				&& player.getSkills().getLevelForXp(Skills.DUNGEONEERING) >= 99
				&& player.getSkills().getLevelForXp(Skills.DIVINATION) >= 99)
		return true;
	return false;
	}
	
	/**
	 * Sends the hilarious animation if not worthy to claim a cape.
	 * @param player The player to send to.
	 */
	private void notWorthy(final Player player) {
		player.getInterfaceManager().closeChatBoxInterface();
		player.lock(15);
		player.setNextFaceWorldTile(new WorldTile(3084, 3483, 0));
		WorldTasksManager.schedule(new WorldTask() {
			int phase = 0;

			@Override
			public void run() {
				final int[] randomNPC = { 6935, 3283, 4344, 6966 };
				switch (phase) {
				case 0:
					player.setNextAnimation(new Animation(857));
					break;
				case 1:
					player.setNextAnimation(new Animation(915));
					break;
				case 2:
					player.setNextAnimation(new Animation(857));
					break;
				case 3:
					player.setNextGraphics(new Graphics(86));
					player.getGlobalPlayerUpdater().transformIntoNPC(randomNPC[Utils.random(randomNPC.length - 1)]);
					break;
				case 4:
					player.setNextForceTalk(new ForceTalk(".... what in Helwyr is going on..!?"));
					break;
				case 5:
					player.setNextGraphics(new Graphics(86));
					player.getGlobalPlayerUpdater().transformIntoNPC(-1);
					player.setNextAnimation(new Animation(10070));
					//player.setNextForceMovement(new ForceMovement(new WorldTile(3084, 3485, 0), 0, 2));
					break;
				case 6:
					//player.setNextWorldTile(new WorldTile(3084, 3485, 0)); TODO find out why force movement doesn't work <.>
					NPC guard = World.findNPC(5941);
					sendNPCDialogue(4405, GOOFY_LAUGH, "Looks like Dahmaroc had a sense of humour!");
		    	  //  player.faceEntity(guard);
		    	   // guard.faceEntity(player);
					player.unlock();
					stage = 99;
					break;
				}
				phase++;
			}
		}, 0, 2);
	}
}