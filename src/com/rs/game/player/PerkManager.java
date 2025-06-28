package com.rs.game.player;

import java.io.Serializable;

import com.rs.Settings;
import com.rs.utils.Colors;

/**
 * Handles all Game Perks that have been donated for.
 * 
 * @author Zeus.
 */
public class PerkManager implements Serializable {

	/**
	 * Generated serial UID.
	 */
	private static final long serialVersionUID = -6454356751078830705L;

	/**
	 * The player instance.
	 */
	private transient Player player;

	/**
	 * The player instance saving to.
	 * 
	 * @param player
	 *            The player.
	 */
	protected void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * A list of available perks.
	 */
	public boolean bankCommand, staminaBoost, greenThumb, birdMan, unbreakableForge, sleightOfHand, familiarExpert,
			chargeBefriender, charmCollector, herbivore, masterFisherman, delicateCraftsman, coinCollector,
			prayerBetrayer, avasSecret, keyExpert, dragonTrainer, gwdSpecialist, dungeon, petChanter, perslaysion,
			overclocked, elfFiend, masterChef, masterDiviner, quarryMaster, miniGamer, masterFledger, thePiromaniac,
			huntsman, portsMaster, investigator, divineDoubler, imbuedFocus, alchemicSmith, petLoot, dungeoneeringPower,
			dungeonOrison, mastersorcerer, petmaster, basher, butcher, lifeSteal, regenerator, breath,HazelmereLuck,
			red, voragoblue, brassicaprimegreen, green, nexred, blue, zarospurple, pink, orange, yellow, grey
			, black, saradominblue, armadylyellow, purple, zamorakred, serenblue, araxxorgrey, kalphitekingorange,qbdblue,
			superprayer
			
			;

	/**
	 * Lets shorten the line here. Have to love our eyes man :(.
	 * 
	 * @param line
	 *            The interID to print.
	 * @param message
	 *            The String to print.
	 */
	private void sendText(int line, String message) {
		player.getPackets().sendIComponentText(275, line, "<shad=000000>" + message);
	}

	/**
	 * Displays players activated game perks.
	 */
	public void displayAvailablePerks() {
		for (int i = 0; i < 309; i++)
			player.getPackets().sendIComponentText(275, i, "");
		sendText(1, Colors.red + Settings.SERVER_NAME + " Game Perks");
		sendText(10, Colors.cyan + "Game Perks can be purchased from our ;;store.");
		sendText(11, Colors.red + "Red - not active</col>  -  " + Colors.green + "Green - active</col>.");
		sendText(12, "</shad>----------------------");
		sendText(13, (bankCommand ? Colors.green : Colors.red) + "Bank Command");
		sendText(14, (staminaBoost ? Colors.green : Colors.red) + "Stamina Boost");
		sendText(15, (greenThumb ? Colors.green : Colors.red) + "Green Thumb");
		sendText(16, (birdMan ? Colors.green : Colors.red) + "Bird Man");
		sendText(17, (unbreakableForge ? Colors.green : Colors.red) + "Unbreakable Forge");
		sendText(18, (sleightOfHand ? Colors.green : Colors.red) + "Sleight of Hand");
		sendText(19, (familiarExpert ? Colors.green : Colors.red) + "Familiar Expert");
		sendText(20, (chargeBefriender ? Colors.green : Colors.red) + "Charge Befriender");
		sendText(21, (charmCollector ? Colors.green : Colors.red) + "Charm Collector");
		sendText(22, (herbivore ? Colors.green : Colors.red) + "Herbivore");
		sendText(23, (masterFisherman ? Colors.green : Colors.red) + "Master Fisherman");
		sendText(24, (delicateCraftsman ? Colors.green : Colors.red) + "Delicate Craftsman");
		sendText(25, (coinCollector ? Colors.green : Colors.red) + "Coin Collector");
		sendText(26, (prayerBetrayer ? Colors.green : Colors.red) + "Prayer Betrayer");
		sendText(27, (avasSecret ? Colors.green : Colors.red) + "Avas Secret");
		sendText(28, (keyExpert ? Colors.green : Colors.red) + "Key Expert");
		sendText(29, (dragonTrainer ? Colors.green : Colors.red) + "Dragon Trainer");
		sendText(30, (gwdSpecialist ? Colors.green : Colors.red) + "GWD Specialist");
		sendText(31, (dungeon ? Colors.green : Colors.red) + "Dungeons Master");
		sendText(32, (petChanter ? Colors.green : Colors.red) + "Pet'chanter");
		sendText(33, (perslaysion ? Colors.green : Colors.red) + "Per'slay'sion");
		sendText(34, (overclocked ? Colors.green : Colors.red) + "Overclocked");
		sendText(35, (elfFiend ? Colors.green : Colors.red) + "Elf Fiend");
		sendText(36, (masterChef ? Colors.green : Colors.red) + "Master Chefs Man");
		sendText(37, (masterDiviner ? Colors.green : Colors.red) + "Master Diviner");
		sendText(38, (quarryMaster ? Colors.green : Colors.red) + "Quarrymaster");
		sendText(39, (miniGamer ? Colors.green : Colors.red) + "The Mini-Gamer");
		sendText(40, (masterFledger ? Colors.green : Colors.red) + "Master Fledger");
		sendText(41, (thePiromaniac ? Colors.green : Colors.red) + "The Piromaniac");
		sendText(42, (huntsman ? Colors.green : Colors.red) + "Huntsman");
		sendText(43, (portsMaster ? Colors.green : Colors.red) + "Ports Master");
		sendText(44, (investigator ? Colors.green : Colors.red) + "Investigator");
		sendText(45, (divineDoubler ? Colors.green : Colors.red) + "Divine Doubler");
		sendText(46, (imbuedFocus ? Colors.green : Colors.red) + "Imbued Focus");
		sendText(47, (alchemicSmith ? Colors.green : Colors.red) + "Alchemic Smithing");
		sendText(48, (petLoot ? Colors.green : Colors.red) + "Pet Loot");
		sendText(49, (petmaster ? Colors.green : Colors.red) + "D'Companion");
		sendText(50, (basher ? Colors.green : Colors.red) + "Stag·ger");
		sendText(51, (butcher ? Colors.green : Colors.red) + "Annihilator");
		sendText(52, (lifeSteal ? Colors.green : Colors.red) + "Dominator");
		sendText(53, (regenerator ? Colors.green : Colors.red) + "Heart of tarrasque");
		sendText(54, (breath ? Colors.green : Colors.red) + "Corruption Blast");
		sendText(55, (HazelmereLuck ? Colors.green : Colors.red) + "Hazelmere Luck");
		sendText(60, Colors.cyan + "SKINS can be activated with ;;skin(id)");
		sendText(61, (red ? Colors.green : Colors.red) + "Red - 1");
		sendText(62, (voragoblue ? Colors.green : Colors.red) + "VoragoBlue - 2");
		sendText(63, (brassicaprimegreen ? Colors.green : Colors.red) + "Brassica Prime Green - 3");
		sendText(64, (green ? Colors.green : Colors.red) + "Green - 4");
		sendText(65, (nexred ? Colors.green : Colors.red) + "Nex Red - 5");
		sendText(66, (blue ? Colors.green : Colors.red) + "Blue - 6");
		sendText(67, (zarospurple ? Colors.green : Colors.red) + "Zaros Purple - 7");
		sendText(68, (pink ? Colors.green : Colors.red) + "Pink - 8");
		sendText(69, (orange ? Colors.green : Colors.red) + "Orange - 9");
		sendText(70, (yellow ? Colors.green : Colors.red) + "Yellow - 10");
		sendText(71, (grey ? Colors.green : Colors.red) + "Grey - 11");
		sendText(72, (black ? Colors.green : Colors.red) + "Black - 12");
		sendText(73, (saradominblue ? Colors.green : Colors.red) + "Saradomin Blue - 14");
		sendText(74, (armadylyellow ? Colors.green : Colors.red) + "Armadyl Yellow - 15");
		sendText(75, (purple ? Colors.green : Colors.red) + "Purple - 16");
		sendText(76, (zamorakred ? Colors.green : Colors.red) + "Zamorak Red - 18");
		sendText(77, (serenblue ? Colors.green : Colors.red) + "Seren Blue - 19");
		sendText(78, (araxxorgrey ? Colors.green : Colors.red) + "Araxxor Grey - 20");
		sendText(79, (kalphitekingorange ? Colors.green : Colors.red) + "Kalphite King Orange - 22");
		sendText(80, (qbdblue ? Colors.green : Colors.red) + "Queen Black Dragon Blue - 23");
		sendText(80, (superprayer ? Colors.green : Colors.red) + "Praesul");
		player.getInterfaceManager().sendInterface(275);
	}
}