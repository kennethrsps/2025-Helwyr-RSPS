package com.rs.game.player.content;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.utils.Utils;

import java.util.Random;

/**
 * Handles the Trivia Bot.
 * @author Zeus
 */
public class TriviaBot {

	/**
	 * String containing all of the Questions & Answers.
	 */
	private static String server[][] = {

			{ "What are the transparent magic robes called?", "Ghostly" },
			{ "What grants you the Fire Cape?", "Fight Caves" },
			{ "What is the max skill cap?", "99" },
			{ "How many portals are there at clan wars?", "3" },
			{ "What weapon is a Tormented Demon's weakness?", "Darklight" },
			{ "How many barrows brothers were there originally?", "6" },
			{ "What is the first ancient spell?", "Smoke Rush" },
			{ "What is the most powerful curse?", "Turmoil" },
			{ "How much percentage does a dragon daggers special require?", "25" },
			{ "What is the best free-to-play armour?", "corrupt dragon" },
			{ "How much xp is required to achieve level 120 in a skill?", "104273167" },
			{ "What do You receive when a fire disappears?", "Ashes" },
			{ "What is the maximum amount of xp you can gain per skill?", "1000000000" },
			{ "What color is the Guthix Flag?", "Green" },
			{ "What prayer Level do You need to use Steel Skin?", "28" },
			{ "What is the King Black Dragons Combat Level?", "276" },
			{ "At what mining level can you mine Runite Ore?", "85" },
			{ "At what mining level can you mine Mithril Ore?", "55" },
			{ "At what mining level can you mine Adamantite Ore?", "70" },
			{ "At what mining level can you mine Gold Ore?", "40" },
			{ "At what fishing level can you fish Lobster?", "40" },
			{ "At what fishing level can you fish Sword Fish?", "50" },
			{ "At what fishing level can you fish Tuna?", "35" },
			{ "What Tool do You need to fish Lobster?", "Lobster Pot" },
			{ "What Tool do You need to fish Tuna?", "Harpoon" },
			{ "What Tool do You need to fish Sword Fish?", "Harpoon" },
			{ "What is the strongest Dungeoneering weapon type?", "Primal" },
			{ "What woodcutting level do You need to cut magic logs?", "75" },
			{ "What cooking level do You need to cook Rocktails?", "93" },
			{ "What Smithing level do You need to smith rune Warhammers?", "94" },
			{ "What bones give most XP per bury?", "Frost Dragon Bones" },
			{ "What Summoning level do You need to use TzRek Jad?", "99" },
			{ "What Summoning level do You need to summon Fruit bat?", "69" },
			{ "What Slayer level do You need to fight Ganodermic beasts?", "95" },
			{ "What Fishing level do You need to fish manta rays?", "81" },
			{ "What Fletching level do You need to fletch Magic Shortbows?", "80" },
			{ "What Agility level do You need for advanced Barbarian outpost?", "90" },
			{ "What emote uses a cannon?", "Chaotic Cookery" },
			{ "What are the first four digits of a max stack?", "2147" },
			{ "What are the digits of a max stack?", "2147483647" },
			{ "What Prayer level do You need for Turmoil?", "95" },
			{ "Which aura gives you red wings?", "Corruption" },
			{ "Which aura gives you white wings?", "Salvation" },
			{ "Which aura gives you green wings?", "Harmony" },
			{ "Which colour charm gives you most xp?", "Blue" },
			{ "What shield does General Graardor Drop?", "Bandos Warshield" },
			{ "What potion blocks dragonfire breath?", "Antifire Potion" },
			{ "What's the hardest skill to get max level in?", "Dungeoneering" },
			{ "What Rank is a gold Crown?", "Administrator" },
			{ "What Rank is a Silver Crown?", "Moderator" },
			{ "How many Dungeoneering tokens are needed to buy a Chaotic?", "200000" },
			{ "What is the maximum total level you can achieve?", "2595" },
			{ "What gaming genre is " + Settings.SERVER_NAME + "?", "MMORPG" },
			{ "What is the maximum combat level in " + Settings.SERVER_NAME + "?", "138" },
			{ "Is tomato a fruit or a vegetable?", "Fruit" },
			{ "How many legs does a spider have?", "8" },
			{ "What is the last modern spellbook spell?", "fire surge" },
			{ "What special item is required to teleport to ape atoll?", "banana" },
			{ "What's hardest skill to get max level in?", "dungeoneering" },
			{ "What is the best free-to-play armour?", "corrupt dragon" },
			{ "What do You receive when a fire disappears?", "ashes" },
			{ "What's the name of the new firecape?", "tokhaar-kal" },
			{ "Who is the founder of " + Settings.SERVER_NAME + "?", "Zeus" },
			{ "What spell can You do at level 55 magic?", "high level alchemy" },
			{ "What potion is used on diseased plants?", "plant cure" },
			{ "What slayer level is required to kill abyssal demons?", "85" },
			{ "What trees can You chop at level 75 woodcutting?", "magic" },
			{ "What ores can You mine at level 30 mining?", "coal" },
			{ "What potions can You make at level 96 herblore?", "overloads" },
			{ "How many total server votes are needed for a Vote Party?", "100" },
			{ "What item is used to make a bowstring?", "flax" },
			{ "In what city is the home of "+Settings.SERVER_NAME+" located?", "Gold Mine" },
			{ "Who can repair broken or damaged gear?", "bob" }};

	/**
	 * String containing the question categories.
	 */
	private static String categories[][][] = { server };

	/**
	 * The Question ID.
	 */
	public static int questionid = -1;
	
	/**
	 * The Trivia round count.
	 */
	public static int round;
	
	/**
	 * If the question has been answered already.
	 */
	public static boolean victory;
	
	/**
	 * Total Trivia answers;
	 */
	public static int answers;
	
	/**
	 * The Trivia category int.
	 */
	public static int category;

	/**
	 * Inits the Trivia Bot's question.
	 */
	public static void Run() {
		int rand = RandomQuestion(category);
		questionid = rand;
		answers = 0;
		victory = false;
		for (Player participant : World.getPlayers()) {
			if (participant == null)
				continue;
			participant.hasAnswered = false;
			participant.sendMessage("<col=56A5EC><shad=000000><img=6>[Trivia]</col></shad> "
					+ categories[category][rand][0], false);
		}
	}

	/**
	 * Sends the Trivia round winner.
	 * @param winner The winner.
	 * @param player The player.
	 */
	public static void sendRoundWinner(String winner, Player player) {
		int reward = 250000;
		int online = World.getPlayers().size();
		int maxPlayers = (online <= 20 ? 3 : (online <= 40 ? 5 : (online <= 80 ? 7 : (online <= 160 ? 10 : 25))));

		for (Player participant : World.getPlayers()) {
			if (participant == null)
				continue;
			if (answers <= maxPlayers) {
				answers++;
				if (answers == maxPlayers)
					victory = true;
				reward /= answers;
				player.addMoney(reward);
				player.setTriviaPoints(player.getTriviaPoints() + 1);
				player.hasAnswered = true;
				player.sendMessage("<col=56A5EC><shad=000000><img=6>[Trivia]</col></shad> "
							+ "You've answered correctly and won "
							+ Utils.getFormattedNumber(reward) + " coins!</col>", false);
				return;
			}
		}
	}

	/**
	 * Verifies the Trivia questions answer.
	 * @param player The player.
	 * @param answer The answer.
	 */
	public static void verifyAnswer(final Player player, String answer) {
		if (victory)
			player.sendMessage("<col=56A5EC><shad=000000><img=6>[Trivia]</col></shad> That round has already been won, wait for the next round.");
		else if (player.hasAnswered)
			player.sendMessage("<col=56A5EC><shad=000000><img=6>[Trivia]</col></shad> You have already answered this question.");
		else if (categories[category][questionid][1].equalsIgnoreCase(answer)) {
			round++;
			sendRoundWinner(player.getDisplayName(), player);
		} else
			player.sendMessage("<col=56A5EC><shad=000000><img=6>[Trivia]</col></shad> That answer wasn't correct, please try again.");
	}

	/**
	 * Rolls a Random Question from the questions & answers String.
	 * @param i the Amount to choose from.
	 * @return The question ID.
	 */
	public static int RandomQuestion(int i) {
		int random = 0;
		Random rand = new Random();
		random = rand.nextInt(categories[i].length);
		return random;
	}
}
